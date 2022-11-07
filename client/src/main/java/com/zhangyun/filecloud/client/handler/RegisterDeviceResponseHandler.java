package com.zhangyun.filecloud.client.handler;

import com.zhangyun.filecloud.client.controller.registerdevice.ConfirmController;
import com.zhangyun.filecloud.client.service.nettyservice.RegisterDeviceService;
import com.zhangyun.filecloud.common.annotation.TraceLog;
import com.zhangyun.filecloud.common.message.RegisterDeviceResponseMessage;
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
 * @date: 2022/11/6 13:38
 * @since: 1.0
 */
@Slf4j
@ChannelHandler.Sharable
@Component
public class RegisterDeviceResponseHandler extends SimpleChannelInboundHandler<RegisterDeviceResponseMessage> {
    @Autowired
    private RegisterDeviceService registerDeviceService;

    @Override
    @TraceLog
    protected void channelRead0(ChannelHandlerContext ctx, RegisterDeviceResponseMessage msg) throws Exception {
        registerDeviceService.setRegisterDeviceResponseMessage(msg);
        registerDeviceService.getInitDeviceSemaphore().release();
    }
}
