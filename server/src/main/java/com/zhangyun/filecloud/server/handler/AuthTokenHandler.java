package com.zhangyun.filecloud.server.handler;

import com.zhangyun.filecloud.common.annotation.TraceLog;
import com.zhangyun.filecloud.common.message.AuthFailResponseMessage;
import com.zhangyun.filecloud.common.message.LoginMessage;
import com.zhangyun.filecloud.common.message.Message;
import com.zhangyun.filecloud.common.message.PingMessage;
import com.zhangyun.filecloud.server.service.RedisService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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
public class AuthTokenHandler extends SimpleChannelInboundHandler<Message> {
    private static final List<Class<? extends Message>> IGNORE_CLASS_LIST = new ArrayList<>();
    static {
        IGNORE_CLASS_LIST.add(LoginMessage.class);
        IGNORE_CLASS_LIST.add(PingMessage.class);
    }

    @Autowired
    private RedisService redisService;

    @Override
    @TraceLog
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        boolean authToken = authToken(msg);
        if (authToken) {
            // 认证成功
            ctx.fireChannelRead(msg);
        } else {
            ctx.writeAndFlush(new AuthFailResponseMessage(301, "token 认证失败"));
        }
    }

    @TraceLog
    private boolean authToken(Message msg) {
        if (msg.getUsername() ==null || msg.getToken() == null) {
            return false;
        }
        // 认证token
        return redisService.authTokenAndUpdateExpireTime(msg.getToken(), msg.getUsername(), msg.getDeviceId());
    }
}
