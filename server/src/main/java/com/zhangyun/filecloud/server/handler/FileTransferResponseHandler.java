package com.zhangyun.filecloud.server.handler;

import com.zhangyun.filecloud.common.entity.FileTransferBO;
import com.zhangyun.filecloud.common.enums.StatusEnum;
import com.zhangyun.filecloud.common.enums.TransferModeEnum;
import com.zhangyun.filecloud.common.message.FileTransferResponseMessage;
import com.zhangyun.filecloud.common.utils.FileUtil;
import com.zhangyun.filecloud.common.utils.PathUtil;
import com.zhangyun.filecloud.server.config.Config;
import com.zhangyun.filecloud.server.service.FileTransferService;
import com.zhangyun.filecloud.server.service.rabbitmq.RabbitMqService;
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
 * @date: 2022/11/16 16:36
 * @since: 1.0
 */
@Slf4j
@ChannelHandler.Sharable
@Component
public class FileTransferResponseHandler extends SimpleChannelInboundHandler<FileTransferResponseMessage> {
    @Autowired
    private RabbitMqService rabbitMqService;
    @Autowired
    private FileTransferService fileTransferService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FileTransferResponseMessage msg) throws Exception {
        if (msg.getCode() != 200) {
            // 不处理此消息
            log.warn("FileTransferResponseMessage消息错误! {}", msg);
            return;
        }
        // 获取列表第一个 文件传输对象
        FileTransferBO fileTransferBO = fileTransferService.getFirstByUsername(msg.getUsername());
        // 是上传消息，写入文件
        if (fileTransferBO.getTransferModeEnum() == TransferModeEnum.UPLOAD) {
            // 1. 文件绝对路径
            Path absolutePath = PathUtil.getAbsolutePath(fileTransferBO.getRelativePath(), Config.ROOT_PATH);
            // 2. 写入文件
            FileUtil.writeFile(absolutePath.toString(), fileTransferBO.getStartPos(), msg.getMessageBody());
        }
        if (msg.getStatusEnum() == StatusEnum.FINISHED) {
            // 上传完毕，移除第一个
            fileTransferService.removeFirstByUsername(msg.getUsername());
        } else {
            // 尚未上传成功，修改pos
            long nextPos = msg.getNextPos();
            fileTransferService.setStartPosOnFirstByUsername(msg.getUsername(), nextPos);
        }
        fileTransferService.setReady(msg.getUsername());
    }
}
