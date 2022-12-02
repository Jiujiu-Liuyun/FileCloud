package com.zhangyun.filecloud.client.handler;

import com.zhangyun.filecloud.client.controller.AppController;
import com.zhangyun.filecloud.common.annotation.TraceLog;
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
     * @param msg
     * @throws IOException
     */
    private void handleFTRM(FileTrfRespMsg msg) throws IOException {
        FileTrfBO fileTrfBO = msg.getFileTrfBO();
        Path absolutePath = PathUtil.getAbsolutePath(fileTrfBO.getRelativePath(), appController.getUserInfo().getRootPath());
        if (fileTrfBO.getTransferModeEnum() == TransferModeEnum.DOWNLOAD) {
            // 2.1 写
            if (fileTrfBO.getFileTypeEnum() == FileTypeEnum.DIRECTORY && fileTrfBO.getOperationTypeEnum() == OperationTypeEnum.CREATE) {
                FileUtil.createDir(absolutePath);
            } else if (fileTrfBO.getFileTypeEnum() == FileTypeEnum.DIRECTORY && fileTrfBO.getOperationTypeEnum() == OperationTypeEnum.CHANGE) {
                FileUtil.changeDir(absolutePath);
            } else if (fileTrfBO.getFileTypeEnum() == FileTypeEnum.DIRECTORY && fileTrfBO.getOperationTypeEnum() == OperationTypeEnum.DELETE) {
                FileUtil.deleteDir(absolutePath);
            } else if (fileTrfBO.getFileTypeEnum() == FileTypeEnum.FILE &&
                    (fileTrfBO.getOperationTypeEnum() == OperationTypeEnum.CREATE || fileTrfBO.getOperationTypeEnum() == OperationTypeEnum.CHANGE)) {
                FileUtil.writeFile(absolutePath.toString(), fileTrfBO.getStartPos(), msg.getMessageBody());
            } else if (fileTrfBO.getFileTypeEnum() == FileTypeEnum.FILE && fileTrfBO.getOperationTypeEnum() == OperationTypeEnum.DELETE) {
                if (new File(absolutePath.toString()).isFile()) {
                    Files.delete(absolutePath);
                }
            }
        }
    }
}
