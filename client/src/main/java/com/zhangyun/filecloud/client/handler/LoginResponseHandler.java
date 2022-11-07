package com.zhangyun.filecloud.client.handler;

import com.zhangyun.filecloud.client.service.nettyservice.LoginService;
import com.zhangyun.filecloud.common.message.LoginResponseMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/11/2 23:33
 * @since: 1.0
 */
@Component
@ChannelHandler.Sharable
@Slf4j
public class LoginResponseHandler extends SimpleChannelInboundHandler<LoginResponseMessage> {
    @Autowired
    private LoginService loginService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginResponseMessage msg) throws Exception {
        loginService.setResponseMessage(msg);
        loginService.getLoginSemaphore().release();
    }
}
