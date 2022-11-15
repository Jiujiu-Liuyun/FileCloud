package com.zhangyun.filecloud.server.handler;

import com.zhangyun.filecloud.common.message.FileChangeMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/11/15 10:45
 * @since: 1.0
 */
@Slf4j
@ChannelHandler.Sharable
@Component
public class FileChangeHandler extends SimpleChannelInboundHandler<FileChangeMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FileChangeMessage msg) throws Exception {

    }
}
