package com.zhangyun.filecloud.client.handler;

import com.zhangyun.filecloud.client.service.FileUploadService;
import com.zhangyun.filecloud.common.enums.FileStatusEnum;
import com.zhangyun.filecloud.common.message.UploadMessage;
import com.zhangyun.filecloud.common.message.UploadResponseMessage;
import com.zhangyun.filecloud.common.utils.FileUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/10/17 20:18
 * @since: 1.0
 */
@Component
@ChannelHandler.Sharable
public class UploadResponseMessageHandler extends SimpleChannelInboundHandler<UploadResponseMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, UploadResponseMessage msg) throws Exception {
        // 传输完成
        if (msg.getStatusEnum() == FileStatusEnum.FINISHED) {
            // 计数加一
            FileUploadService.SEMAPHORE.release();
            return;
        }
        UploadMessage fileTransferMessage = new UploadMessage();
        fileTransferMessage.setOperationEnum(msg.getOperationEnum());
        fileTransferMessage.setStartPos(msg.getNextPos());
        fileTransferMessage.setFilePath(msg.getFilePath());
        // read file
        FileUtil.readFile(fileTransferMessage);
        fileTransferMessage.setLastModified(new File(msg.getFilePath()).lastModified());
        // 传输数据
        ctx.writeAndFlush(fileTransferMessage).sync();
    }
}
