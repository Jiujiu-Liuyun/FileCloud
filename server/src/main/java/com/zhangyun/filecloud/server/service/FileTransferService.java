package com.zhangyun.filecloud.server.service;

import com.zhangyun.filecloud.common.entity.FileTrfBO;
import com.zhangyun.filecloud.common.enums.FileTypeEnum;
import com.zhangyun.filecloud.common.enums.OperationTypeEnum;
import com.zhangyun.filecloud.common.enums.StatusEnum;
import com.zhangyun.filecloud.common.enums.TransferModeEnum;
import com.zhangyun.filecloud.common.message.FileTrfMsg;
import com.zhangyun.filecloud.common.message.FileTrfRespMsg;
import com.zhangyun.filecloud.common.utils.FileUtil;
import com.zhangyun.filecloud.common.utils.PathUtil;
import com.zhangyun.filecloud.server.config.Config;
import com.zhangyun.filecloud.server.handler.data.FileTrfRespData;
import com.zhangyun.filecloud.server.service.session.SessionService;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.*;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/11/16 14:45
 * @since: 1.0
 */
@Slf4j
@Service
public class FileTransferService {
    @Autowired
    private RedisService redisService;
    @Resource(name = "scheduledExecutorService")
    private ScheduledExecutorService scheduledExecutorService;
    @Resource(name = "threadPoolExecutor")
    private ExecutorService executorService;
    @Autowired
    private SessionService sessionService;

    /**
     * 存储每个用户的传输对象
     */
    private ConcurrentHashMap<String, ConcurrentLinkedDeque<FileTrfBO>> FILE_TRANSFER_BO_MAP = new ConcurrentHashMap<>();
    /**
     * 表示每个用户的FileTransfer数据是否处理完毕，可以发送新的FileTransfer数据
     */
    private ConcurrentHashMap<String, FileTrfRespData> IS_READY_MAP = new ConcurrentHashMap<>();

    public void setReady(String username) {
        FileTrfRespData fileTrfRespData = IS_READY_MAP.get(username);
    }

    /**
     * 添加 文件传输对象 到列表末尾
     *
     * @param username
     * @param fileTrfBO
     */
    public void addLastByUsername(String username, FileTrfBO fileTrfBO) {
        if (!IS_READY_MAP.containsKey(username)) {
            FileTrfRespData fileTrfRespData = new FileTrfRespData();
            IS_READY_MAP.put(username, fileTrfRespData);
        }
        ConcurrentLinkedDeque<FileTrfBO> fileTrfBOS = FILE_TRANSFER_BO_MAP.getOrDefault(username, new ConcurrentLinkedDeque<>());
        fileTrfBOS.addLast(fileTrfBO);
        FILE_TRANSFER_BO_MAP.put(username, fileTrfBOS);
    }

    /**
     * 修改 文件传输对象 startPos
     *
     * @param username
     * @param startPos
     */
    public void setStartPosOnFirstByUsername(String username, Long startPos) {
        ConcurrentLinkedDeque<FileTrfBO> fileTrfBOS = FILE_TRANSFER_BO_MAP.getOrDefault(username, new ConcurrentLinkedDeque<>());
        // 移除第一个
        FileTrfBO fileTrfBO = fileTrfBOS.peekFirst();
        if (fileTrfBO != null) {
            fileTrfBO.setStartPos(startPos);
        }
    }

    /**
     * 获取列表第一个 文件传输对象
     *
     * @param username
     * @return
     */
    public FileTrfBO getFirstByUsername(String username) {
        ConcurrentLinkedDeque<FileTrfBO> fileTrfBOS = FILE_TRANSFER_BO_MAP.getOrDefault(username, new ConcurrentLinkedDeque<>());
        if (!fileTrfBOS.isEmpty()) {
            return fileTrfBOS.getFirst();
        }
        return null;
    }

    /**
     * 移除列表第一个 文件传输对象
     *
     * @param username
     * @return
     */
    public void removeFirstByUsername(String username) {
        ConcurrentLinkedDeque<FileTrfBO> fileTrfBOS = FILE_TRANSFER_BO_MAP.getOrDefault(username, new ConcurrentLinkedDeque<>());
        if (!fileTrfBOS.isEmpty()) {
            fileTrfBOS.removeFirst();
        }
    }

    public FileTrfMsg constructFileTrfMsg(FileTrfBO fileTrfBO) throws IOException {
        FileTrfMsg fileTrfMsg = new FileTrfMsg();
        fileTrfMsg.setFileTrfBO(fileTrfBO);
        fileTrfMsg.setDeviceId(fileTrfBO.getDeviceId());
        // 读文件：StatusEnum MessageBody
        if (fileTrfBO.getTransferModeEnum() == TransferModeEnum.DOWNLOAD) {
            // 如果为 CREATE_FILE CHANGE_FILE，则需要读取文件内容
            if (fileTrfBO.getFileTypeEnum() == FileTypeEnum.FILE && fileTrfBO.getOperationTypeEnum() != OperationTypeEnum.DELETE) {
                Path absolutePath = PathUtil.getAbsolutePath(fileTrfBO.getRelativePath(), Config.ROOT_PATH);
                byte[] bytes = FileUtil.readFile(absolutePath.toString(), fileTrfBO.getStartPos(), fileTrfBO.getMaxReadLength());
                if (bytes.length == 0 || bytes.length < fileTrfBO.getMaxReadLength()) {
                    // 文件读取完毕
                    fileTrfBO.setStatusEnum(StatusEnum.FINISHED);
                } else {
                    fileTrfBO.setStatusEnum(StatusEnum.GOING);
                }
                fileTrfMsg.setMessageBody(bytes);
            } else {
                fileTrfBO.setStatusEnum(StatusEnum.FINISHED);
            }
        }
        return fileTrfMsg;
    }

    /**
     * 每隔50ms检查一遍，处理FTBO数据队列
     * 步骤
     * 1. 遍历map，找出netty已准备好，且队列中还有数据的
     * 2. 构造FTM，发送给客户端 (不移除首个元素，只有在成功获取netty消息，且成功消费后才移除)
     * 3.
     */
    @PostConstruct
    public void handleFileTrfBO() {
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            for (String username : FILE_TRANSFER_BO_MAP.keySet()) {
                // 当前用户FTR消息已经准备好
                FileTrfRespData fileTrfRespData = IS_READY_MAP.get(username);
                if (fileTrfRespData.getIsReady().get()) {
                    // 处理FTR消息
                    FileTrfRespMsg fileTrfRespMsg = fileTrfRespData.getFileTrfRespMsg();
                    handleFileTrfRespMsg(fileTrfRespMsg);
                    // 发送FT消息
                    sendFileTrfMsg(username);
                    // 消息处于未准备好状态
                    IS_READY_MAP.get(username).getIsReady().set(false);
                    IS_READY_MAP.get(username).setBeginTime(System.currentTimeMillis());
                }
            }
        }, 0, 50, TimeUnit.MILLISECONDS);
    }

    /**
     * 向channel发送FT消息
     * @param username
     */
    private void sendFileTrfMsg(String username) {
        ConcurrentLinkedDeque<FileTrfBO> fileTrfBOS = FILE_TRANSFER_BO_MAP.get(username);
        if (fileTrfBOS.isEmpty()) {
            return;
        }
        // 下一个数据
        FileTrfBO fileTrfBO = fileTrfBOS.peekFirst();
        FileTrfMsg fileTrfMsg = null;
        try {
            fileTrfMsg = constructFileTrfMsg(fileTrfBO);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 通道
        Channel channel = sessionService.getChannel(fileTrfBO.getDeviceId());
        channel.writeAndFlush(fileTrfMsg);
    }

    /**
     * netty获取数据超时控制
     */
    @PostConstruct
    public void nettyDataTimeout() {
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            for (String username : IS_READY_MAP.keySet()) {
                FileTrfRespData fileTrfRespData = IS_READY_MAP.get(username);
                long costTime = System.currentTimeMillis() - fileTrfRespData.getBeginTime();
                if (costTime > Config.TIMEOUT) {
                    // 获取数据超时
                    if (fileTrfRespData.getIsReady().compareAndSet(false, true)) {
                        fileTrfRespData.setFileTrfRespMsg(null);
                    }
                }
            }
        }, 0, 500, TimeUnit.MILLISECONDS);
    }

    /**
     * 处理FTR消息
     * @param msg
     */
    private void handleFileTrfRespMsg(FileTrfRespMsg msg) {
        // 交给线程池处理 FileTrfMsg
        executorService.submit(() -> {
            if (msg == null) {
                log.info("FileTrfRespMsg is null!");
                return;
            }
//            if (msg.getCode() != 200) {
//                // 不处理此消息
//                log.warn("FileTrfRespMsg error! {}", msg);
//                return;
//            }
            // 获取列表第一个 文件传输对象
            FileTrfBO fileTrfBO = msg.getFileTrfBO();
            // 是上传消息，写入文件
            if (fileTrfBO.getTransferModeEnum() == TransferModeEnum.UPLOAD) {
                try {
                    FileTrfBO transferBO = msg.getFileTrfBO();
                    if (transferBO.getTransferModeEnum() != TransferModeEnum.UPLOAD) {
                        return;
                    }
                    // 1. 文件绝对路径
                    Path absolutePath = PathUtil.getAbsolutePath(transferBO.getRelativePath(), Config.ROOT_PATH);
                    // 2. 写入文件
                    FileUtil.writeFile(absolutePath.toString(), transferBO.getStartPos(), msg.getMessageBody());
                } catch (IOException e) {
                    log.error("write file error! {}", e.getMessage());
                    return;
                }
            }
            if (fileTrfBO.getStatusEnum() == StatusEnum.FINISHED) {
                // 上传完毕，移除第一个
                removeFirstByUsername(msg.getUsername());
            } else {
                // 尚未上传成功，修改pos
                long nextPos = msg.getNextPos();
                setStartPosOnFirstByUsername(msg.getUsername(), nextPos);
            }
        });
    }
}
