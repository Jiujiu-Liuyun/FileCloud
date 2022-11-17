package com.zhangyun.filecloud.server.service;

import com.zhangyun.filecloud.common.entity.FileTransferBO;
import com.zhangyun.filecloud.common.enums.StatusEnum;
import com.zhangyun.filecloud.common.enums.TransferModeEnum;
import com.zhangyun.filecloud.common.message.FileTransferMessage;
import com.zhangyun.filecloud.common.utils.FileUtil;
import com.zhangyun.filecloud.common.utils.PathUtil;
import com.zhangyun.filecloud.server.config.Config;
import com.zhangyun.filecloud.server.service.session.SessionService;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
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
    @Autowired
    private ScheduledExecutorService scheduledExecutorService;
    @Autowired
    private SessionService sessionService;

    /**
     * 存储每个用户的传输对象
     */
    private ConcurrentHashMap<String, ConcurrentLinkedDeque<FileTransferBO>> FILE_TRANSFER_BO_MAP = new ConcurrentHashMap<>();
    /**
     * 表示每个用户的FileTransfer数据是否处理完毕，可以发送新的FileTransfer数据
     */
    private ConcurrentHashMap<String, Boolean> IS_READY = new ConcurrentHashMap<>();

    public void setReady(String username) {
        IS_READY.put(username, true);
    }

    /**
     * 添加 文件传输对象 到列表末尾
     *
     * @param username
     * @param fileTransferBO
     */
    public void addLastByUsername(String username, FileTransferBO fileTransferBO) {
        if (!IS_READY.containsKey(username)) {
            IS_READY.put(username, true);
        }
        ConcurrentLinkedDeque<FileTransferBO> fileTransferBOS = FILE_TRANSFER_BO_MAP.getOrDefault(username, new ConcurrentLinkedDeque<>());
        fileTransferBOS.addLast(fileTransferBO);
        FILE_TRANSFER_BO_MAP.put(username, fileTransferBOS);
    }

    /**
     * 修改 文件传输对象 startPos
     *
     * @param username
     * @param startPos
     */
    public void setStartPosOnFirstByUsername(String username, Long startPos) {
        ConcurrentLinkedDeque<FileTransferBO> fileTransferBOS = FILE_TRANSFER_BO_MAP.getOrDefault(username, new ConcurrentLinkedDeque<>());
        // 移除第一个
        FileTransferBO fileTransferBO = fileTransferBOS.peekFirst();
        if (fileTransferBO != null) {
            fileTransferBO.setStartPos(startPos);
        }
    }

    /**
     * 获取列表第一个 文件传输对象
     *
     * @param username
     * @return
     */
    public FileTransferBO getFirstByUsername(String username) {
        ConcurrentLinkedDeque<FileTransferBO> fileTransferBOS = FILE_TRANSFER_BO_MAP.getOrDefault(username, new ConcurrentLinkedDeque<>());
        if (!fileTransferBOS.isEmpty()) {
            return fileTransferBOS.getFirst();
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
        ConcurrentLinkedDeque<FileTransferBO> fileTransferBOS = FILE_TRANSFER_BO_MAP.getOrDefault(username, new ConcurrentLinkedDeque<>());
        if (!fileTransferBOS.isEmpty()) {
            fileTransferBOS.removeFirst();
        }
    }

    public FileTransferMessage constructFileTransferMsg(FileTransferBO fileTransferBO) throws IOException {
        FileTransferMessage fileTransferMessage = new FileTransferMessage();
        fileTransferMessage.setFileTransferBO(fileTransferBO);
        fileTransferMessage.setDeviceId(fileTransferBO.getDeviceId());
        if (fileTransferBO.getTransferModeEnum() == TransferModeEnum.DOWNLOAD) {
            // 绝对路径
            Path absolutePath = PathUtil.getAbsolutePath(fileTransferBO.getRelativePath(), Config.ROOT_PATH);
            // 读文件
            byte[] bytes = FileUtil.readFile(absolutePath.toString(), fileTransferBO.getStartPos(), Config.MAX_READ_LENGTH);
            if (bytes.length == 0 || bytes.length < Config.MAX_READ_LENGTH) {
                // 文件读取完毕
                fileTransferMessage.setStatusEnum(StatusEnum.FINISHED);
            } else {
                fileTransferMessage.setStatusEnum(StatusEnum.GOING);
            }
            fileTransferMessage.setMessageBody(bytes);
        }
        return fileTransferMessage;
    }

    /**
     * 每隔50ms检查一遍是否有新的消息需要发送
     */
    @PostConstruct
    public void sendFileTransferBO() {
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            for (String username : FILE_TRANSFER_BO_MAP.keySet()) {
                // 当前用户netty数据已经处理好了
                if (IS_READY.get(username)) {
                    ConcurrentLinkedDeque<FileTransferBO> fileTransferBOS = FILE_TRANSFER_BO_MAP.get(username);
                    if (fileTransferBOS.isEmpty()) {
                        continue;
                    }
                    // 下一个数据
                    FileTransferBO fileTransferBO = fileTransferBOS.peekFirst();
                    FileTransferMessage fileTransferMessage = null;
                    try {
                        fileTransferMessage = constructFileTransferMsg(fileTransferBO);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // 通道
                    Channel channel = sessionService.getChannel(fileTransferBO.getDeviceId());
                    channel.writeAndFlush(fileTransferMessage);
                    IS_READY.put(username, false);
                }
            }
        }, 0, 50, TimeUnit.MILLISECONDS);
    }
}
