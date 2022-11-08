package com.zhangyun.filecloud.server.handler;

import com.zhangyun.filecloud.server.service.session.SessionService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/11/8 23:57
 * @since: 1.0
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class QuitHandler extends ChannelInboundHandlerAdapter {
    @Autowired
    private SessionService sessionService;

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        sessionService.unbind(ctx.channel());
        log.info("连接 {} 断开", ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        sessionService.unbind(ctx.channel());
        log.info("连接 {} 异常, {}", ctx.channel(), cause.getMessage());
    }
}
