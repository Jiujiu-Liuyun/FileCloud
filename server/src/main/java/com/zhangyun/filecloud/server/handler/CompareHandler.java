package com.zhangyun.filecloud.server.handler;

import cn.hutool.crypto.digest.DigestUtil;
import com.zhangyun.filecloud.common.annotation.TraceLog;
import com.zhangyun.filecloud.common.message.CompareMessage;
import com.zhangyun.filecloud.common.message.CompareResponseMessage;
import com.zhangyun.filecloud.common.utils.FileUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/10/27 22:49
 * @since: 1.0
 */
@Component
@ChannelHandler.Sharable
public class CompareHandler extends SimpleChannelInboundHandler<CompareMessage> {
    @Value("${file.server.path}")
    private String serverPath;

    @Value("${file.client.path}")
    private String clientPath;

    @Override
    @TraceLog
    protected void channelRead0(ChannelHandlerContext ctx, CompareMessage msg) throws Exception {
        File file = FileUtil.sourceMapToTarget(new File(msg.getFilePath()), clientPath, serverPath);
        CompareResponseMessage responseMessage = new CompareResponseMessage();
        responseMessage.setFilePath(msg.getFilePath());
        // 文件不存在
        if (!file.exists()) {
            responseMessage.setIsExist(false);
            ctx.writeAndFlush(responseMessage);
            return;
        }
        responseMessage.setIsExist(true);
        if (file.isFile()) {
            responseMessage.setType(CompareResponseMessage.FILE);
            responseMessage.setMd5(DigestUtil.md5Hex(file));
            responseMessage.setLastModified(file.lastModified());
        } else {
            responseMessage.setType(CompareResponseMessage.DIRECTORY);
            responseMessage.setFileList(file.list());
        }
        ctx.writeAndFlush(responseMessage);
    }
}
