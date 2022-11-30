package com.zhangyun.filecloud.server.handler;

import com.zhangyun.filecloud.common.message.LogoutMessage;
import com.zhangyun.filecloud.server.service.RedisService;
import com.zhangyun.filecloud.server.service.session.SessionService;
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
 * @date: 2022/11/8 17:04
 * @since: 1.0
 */
@Slf4j
@ChannelHandler.Sharable
@Component
public class LogoutHandler extends SimpleChannelInboundHandler<LogoutMessage> {
    @Autowired
    private SessionService sessionService;
    @Autowired
    private RedisService redisService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LogoutMessage msg) throws Exception {
        // 1.删除token
        redisService.delToken(msg.getUsername(), msg.getDeviceId());
        // 2.解除会话连接
        sessionService.unbind(ctx.channel());
        // 3.关闭连接
        ctx.close().sync();
        // 4.日志
        log.info("{} 退出登录", msg.getUsername());
    }
}
