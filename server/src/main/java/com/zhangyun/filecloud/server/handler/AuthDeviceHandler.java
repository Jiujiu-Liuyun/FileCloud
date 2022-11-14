package com.zhangyun.filecloud.server.handler;

import com.zhangyun.filecloud.common.message.AuthFailResponseMessage;
import com.zhangyun.filecloud.common.message.Message;
import com.zhangyun.filecloud.server.service.IDeviceService;
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
 * @date: 2022/11/14 20:38
 * @since: 1.0
 */
@Slf4j
@ChannelHandler.Sharable
@Component
public class AuthDeviceHandler extends SimpleChannelInboundHandler<Message> {
    @Autowired
    private IDeviceService deviceService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        boolean authDevice = deviceService.authDevice(msg.getDeviceId(), msg.getUsername());
        if (authDevice) {
            // 认证成功
            ctx.fireChannelRead(msg);
        } else {
            ctx.writeAndFlush(new AuthFailResponseMessage(302, "设备号认证失败"));
        }
    }
}
