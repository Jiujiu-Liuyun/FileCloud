package com.zhangyun.filecloud.client.handler;

import com.zhangyun.filecloud.client.controller.AppController;
import com.zhangyun.filecloud.common.entity.FileTrfBO;
import com.zhangyun.filecloud.common.enums.FileTypeEnum;
import com.zhangyun.filecloud.common.enums.OperationTypeEnum;
import com.zhangyun.filecloud.common.enums.RespEnum;
import com.zhangyun.filecloud.common.enums.TransferModeEnum;
import com.zhangyun.filecloud.common.message.FileTrfMsg;
import com.zhangyun.filecloud.common.message.FileTrfRespMsg;
import com.zhangyun.filecloud.common.utils.FileUtil;
import com.zhangyun.filecloud.common.utils.PathUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/11/20 20:26
 * @since: 1.0
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class FileTrfRespMsgHandler extends SimpleChannelInboundHandler<FileTrfRespMsg> {
    private ConcurrentHashMap<String, ConcurrentHashMap<OperationTypeEnum, AtomicInteger>> changeCount = new ConcurrentHashMap<>();

    public void addCount(String absolutePath, OperationTypeEnum operationTypeEnum) {
        ConcurrentHashMap<OperationTypeEnum, AtomicInteger> operationTypeCount = changeCount.getOrDefault(absolutePath, new ConcurrentHashMap<>());
        AtomicInteger count = operationTypeCount.getOrDefault(operationTypeEnum, new AtomicInteger());
        count.incrementAndGet();
        operationTypeCount.put(operationTypeEnum, count);
        changeCount.put(absolutePath, operationTypeCount);
    }

    public boolean releaseCount(String absolutePath, OperationTypeEnum operationTypeEnum) {
        ConcurrentHashMap<OperationTypeEnum, AtomicInteger> operationTypeCount = changeCount.getOrDefault(absolutePath, null);
        if (operationTypeCount == null) {
            return false;
        }
        AtomicInteger count = operationTypeCount.getOrDefault(operationTypeEnum, null);
        if (count == null) {
            return false;
        }
        if (count.get() <= 0) {
            return false;
        }
        int i = count.decrementAndGet();
        if (i == 0 && operationTypeEnum == OperationTypeEnum.DELETE) {
            changeCount.remove(absolutePath);
        }
        return true;
    }

    @Autowired
    private AppController appController;
    @Autowired
    private RespFTBOHandler respFTBOHandler;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FileTrfRespMsg msg) throws Exception {
        log.info(">>>>>>>>>>>>>>>> {}", msg);
        if (msg == null || msg.getRespEnum() != RespEnum.OK) {
            log.info("FileTrfRespMsg消息错误, {}", msg);
            return;
        }
        handleFTRM(msg);
        if (msg.getNextFileTrfBO() == null) {
            // 尝试关闭消息链
            boolean isStop = respFTBOHandler.tryStopReqChain();
            if (!isStop) {
                log.info("关闭消息链失败");
            }
        } else {
            // 处理下一个FTBO
            FileTrfMsg fileTrfMsg = respFTBOHandler.handleFTBO(msg.getNextFileTrfBO());
            ctx.writeAndFlush(fileTrfMsg);
            log.info("<<<<<<<<<<<<<<<< {}", fileTrfMsg);
        }
    }

    /**
     * 处理FTBO 下载消息
     *
     * @param msg
     * @throws IOException
     */
    private void handleFTRM(FileTrfRespMsg msg) throws IOException {
        FileTrfBO fileTrfBO = msg.getFileTrfBO();
        Path absolutePath = PathUtil.getAbsolutePath(fileTrfBO.getRelativePath(), appController.getUserInfo().getRootPath());
        if (fileTrfBO.getTransferModeEnum() == TransferModeEnum.DOWNLOAD) {
            // 2.1 写
            if (fileTrfBO.getFileTypeEnum() == FileTypeEnum.DIRECTORY && fileTrfBO.getOperationTypeEnum() == OperationTypeEnum.CREATE) {
                boolean create = FileUtil.createDir(absolutePath);
                if (create) {
                    addCount(String.valueOf(absolutePath), fileTrfBO.getOperationTypeEnum());
                }
            } else if (fileTrfBO.getFileTypeEnum() == FileTypeEnum.DIRECTORY && fileTrfBO.getOperationTypeEnum() == OperationTypeEnum.CHANGE) {
                FileUtil.changeDir(absolutePath);
            } else if (fileTrfBO.getFileTypeEnum() == FileTypeEnum.DIRECTORY && fileTrfBO.getOperationTypeEnum() == OperationTypeEnum.DELETE) {
                boolean delete = FileUtil.deleteDir(absolutePath);
                if (delete) {
                    addCount(String.valueOf(absolutePath), fileTrfBO.getOperationTypeEnum());
                }
            } else if (fileTrfBO.getFileTypeEnum() == FileTypeEnum.FILE &&
                    (fileTrfBO.getOperationTypeEnum() == OperationTypeEnum.CREATE || fileTrfBO.getOperationTypeEnum() == OperationTypeEnum.CHANGE)) {
                boolean write = FileUtil.writeFile(absolutePath.toString(), fileTrfBO.getStartPos(), msg.getMessageBody());
                if (write) {
                    addCount(String.valueOf(absolutePath), fileTrfBO.getOperationTypeEnum());
                }
            } else if (fileTrfBO.getFileTypeEnum() == FileTypeEnum.FILE && fileTrfBO.getOperationTypeEnum() == OperationTypeEnum.DELETE) {
                if (new File(absolutePath.toString()).isFile()) {
                    Files.delete(absolutePath);
                    addCount(String.valueOf(absolutePath), fileTrfBO.getOperationTypeEnum());
                }
            }
        }
    }
}
