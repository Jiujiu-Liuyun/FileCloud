package com.zhangyun.filecloud.server.handler;

import com.zhangyun.filecloud.common.annotation.TraceLog;
import com.zhangyun.filecloud.common.message.AuthFailResponseMessage;
import com.zhangyun.filecloud.common.message.LoginMessage;
import com.zhangyun.filecloud.common.message.Message;
import com.zhangyun.filecloud.server.service.RedisService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * description: 权限认证，非法消息过滤掉
 *
 * @author: zhangyun
 * @date: 2022/11/4 10:26
 * @since: 1.0
 */
@Slf4j
@ChannelHandler.Sharable
@Component
public class MessageFilterHandler extends SimpleChannelInboundHandler<Message> {
    @Autowired
    private RedisService redisService;

    @Override
    @TraceLog
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        AuthFailResponseMessage authFailResponseMessage = new AuthFailResponseMessage();
        boolean authToken = authToken(msg, authFailResponseMessage);
        if (authToken) {
            // 认证成功
            ctx.fireChannelRead(msg);
        } else {
            ctx.writeAndFlush(authFailResponseMessage);
        }
    }

    @TraceLog
    private boolean authToken(Message msg, AuthFailResponseMessage authFailResponseMessage) {
        // 如果是登录消息，直接放行
        if (msg instanceof LoginMessage) {
            return true;
        }
        // token is null
        if (msg.getToken() == null || msg.getUsername() ==null) {
            authFailResponseMessage.setCode(301);
            authFailResponseMessage.setMsg("消息内容不完整");
            return false;
        }
        // 认证token
        if (redisService.authTokenAndUpdateExpireTime(msg.getToken(), msg.getUsername())) {
            return true;
        } else {
            authFailResponseMessage.setCode(301);
            authFailResponseMessage.setMsg("token认证不通过");
            return false;
        }
    }
}
