package com.zhangyun.filecloud.server.handler;

import com.zhangyun.filecloud.common.annotation.TraceLog;
import com.zhangyun.filecloud.common.enums.RespEnum;
import com.zhangyun.filecloud.common.message.*;
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
public class AuthTokenHandler extends SimpleChannelInboundHandler<Msg> {

    @Autowired
    private RedisService redisService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Msg msg) throws Exception {
        RespEnum respEnum = authToken(msg);
        if (respEnum == RespEnum.OK) {
            // 认证成功，向后传递消息
            ctx.fireChannelRead(msg);
        } else {
            // 通知客户端
            ctx.writeAndFlush(new RespMsg(respEnum));
        }
    }

    @TraceLog
    private RespEnum authToken(Msg msg) {
        if (msg.getUsername() ==null || msg.getToken() == null) {
            return RespEnum.MSG_FORMAT_ERROR;
        }
        // 认证token
        boolean authToken = redisService.authTokenAndUpdateExpireTime(msg.getToken(), msg.getUsername(), msg.getDeviceId());
        return authToken ? RespEnum.OK : RespEnum.AUTH_TOKEN_FAIL;
    }
}
