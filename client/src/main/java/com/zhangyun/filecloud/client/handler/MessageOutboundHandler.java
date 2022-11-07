package com.zhangyun.filecloud.client.handler;

import com.zhangyun.filecloud.client.controller.app.AppController;
import com.zhangyun.filecloud.common.annotation.TraceLog;
import com.zhangyun.filecloud.common.message.Message;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * description:
 * 1. 给所有消息添加添加token
 *
 * @author: zhangyun
 * @date: 2022/11/7 13:58
 * @since: 1.0
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class MessageOutboundHandler extends ChannelOutboundHandlerAdapter {
    @Autowired
    private AppController appController;

    @Override
    @TraceLog
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof Message) {
            Message message = (Message) msg;
            message.setToken(appController.getUserInfo().getToken());
            log.info("{}", message);
        }
        super.write(ctx, msg, promise);
    }
}
