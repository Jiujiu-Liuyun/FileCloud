package com.zhangyun.filecloud.server.handler;

import com.alibaba.fastjson.JSONObject;
import com.zhangyun.filecloud.common.annotation.TraceLog;
import com.zhangyun.filecloud.common.enums.FileOperationEnum;
import com.zhangyun.filecloud.common.exception.InvalidArgumentsException;
import com.zhangyun.filecloud.common.message.UploadMessage;
import com.zhangyun.filecloud.common.message.UploadResponseMessage;
import com.zhangyun.filecloud.common.utils.FileUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/10/17 19:55
 * @since: 1.0
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class UploadMessageHandler extends SimpleChannelInboundHandler<UploadMessage> {
    @Value("${file.server.path}")
    private String serverPath;

    @Value("${file.client.path}")
    private String clientPath;

    @Override
    @TraceLog
    protected void channelRead0(ChannelHandlerContext ctx, UploadMessage msg) throws Exception {
        // 获取Server端下的路径
        Path relative = Paths.get(clientPath).relativize(Paths.get(msg.getFilePath()));
        Path serverFile = Paths.get(serverPath, String.valueOf(relative));
        // 判断操作类型
        switch (msg.getOperationEnum()) {
            case DIRECTORY_CREATE:
                Files.createDirectory(serverFile);
                log.info("文件夹 {} 创建！", serverFile);
                break;
            case DIRECTORY_DELETE:
                try {
                    boolean exists = Files.deleteIfExists(serverFile);
                    if (!exists) {
                        log.warn("文件夹 {} 不存在！", serverFile);
                    } else {
                        log.info("文件夹 {} 被删除！", serverFile);
                    }
                } catch (DirectoryNotEmptyException e) {
                    File file = new File(String.valueOf(serverFile));
                    log.warn("删除异常: {} ===> {}", serverFile, JSONObject.toJSONString(file.list()));
                }
                break;
            case DIRECTORY_CHANGE:
                log.info("文件夹 {} 改变", serverFile);
                break;
            case FILE_CHANGE:
                log.info("文件 {} 改变", serverFile);
                FileUtil.writeFile(msg, serverFile.toString());
                break;
            case FILE_CREATE:
                log.info("文件 {} 创建", serverFile);
                FileUtil.writeFile(msg, serverFile.toString());
                break;
            case FILE_DELETE:
                if (new File(String.valueOf(serverFile)).exists()) {
                    log.info("文件 {} 删除", serverFile);
                    Files.deleteIfExists(serverFile);
                } else {
                    log.warn("文件 {} 不存在，删除失败", serverFile);
                }
                break;
            default:
        }
        if (msg.getLastModified() == null) {
            throw new InvalidArgumentsException("last modified is null!");
        }
        if (msg.getOperationEnum() != FileOperationEnum.FILE_DELETE
                && msg.getOperationEnum() != FileOperationEnum.DIRECTORY_DELETE) {
            if (!new File(String.valueOf(serverFile)).setLastModified(msg.getLastModified())) {
                log.warn("last modified 修改失败");
            }
        }
        // response message
        UploadResponseMessage responseMessage = new UploadResponseMessage();
        responseMessage.setFilePath(msg.getFilePath());
        if (msg.getStartPos() != null && msg.getMessageBody() != null) {
            responseMessage.setNextPos(msg.getStartPos() + msg.getMessageBody().length);
        } else {
            responseMessage.setNextPos(-1L);
        }
        responseMessage.setOperationEnum(msg.getOperationEnum());
        responseMessage.setStatusEnum(msg.getStatusEnum());
        ctx.writeAndFlush(responseMessage);
    }
}
