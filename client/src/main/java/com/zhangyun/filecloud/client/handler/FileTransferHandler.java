package com.zhangyun.filecloud.client.handler;

import com.zhangyun.filecloud.client.controller.app.AppController;
import com.zhangyun.filecloud.common.enums.StatusEnum;
import com.zhangyun.filecloud.common.enums.TransferModeEnum;
import com.zhangyun.filecloud.common.message.FileTransferMessage;
import com.zhangyun.filecloud.common.message.FileTransferResponseMessage;
import com.zhangyun.filecloud.common.utils.FileUtil;
import com.zhangyun.filecloud.common.utils.PathUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/11/16 17:40
 * @since: 1.0
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class FileTransferHandler extends SimpleChannelInboundHandler<FileTransferMessage> {
    @Autowired
    private AppController appController;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FileTransferMessage msg) throws Exception {
        FileTransferResponseMessage responseMessage = new FileTransferResponseMessage();
        String rootPath = appController.getUserInfo().getRootPath();
        Path absolutePath = PathUtil.getAbsolutePath(msg.getFileTransferBO().getRelativePath(), rootPath);
        if (msg.getFileTransferBO().getTransferModeEnum() == TransferModeEnum.DOWNLOAD) {
            // 写入文件
            FileUtil.writeFile(absolutePath.toString(), msg.getFileTransferBO().getStartPos(), msg.getMessageBody());
            //
            responseMessage.setCode(200);
            responseMessage.setDesc("ok");
            responseMessage.setNextPos(msg.getFileTransferBO().getStartPos() + msg.getMessageBody().length);
            responseMessage.setStatusEnum(msg.getStatusEnum());
        } else {
            // 读文件
            byte[] bytes = FileUtil.readFile(absolutePath.toString(), msg.getFileTransferBO().getStartPos(), 1024);
            //
            responseMessage.setCode(200);
            responseMessage.setDesc("ok");
            responseMessage.setNextPos(msg.getFileTransferBO().getStartPos() + bytes.length);
            responseMessage.setMessageBody(bytes);
            if (bytes.length > 0) {
                responseMessage.setStatusEnum(StatusEnum.GOING);
            } else {
                responseMessage.setStatusEnum(StatusEnum.FINISHED);
            }
        }
        ctx.writeAndFlush(responseMessage);
    }

}
