package com.zhangyun.filecloud.server.handler;

import com.zhangyun.filecloud.common.annotation.TraceLog;
import com.zhangyun.filecloud.common.message.RegisterDeviceMessage;
import com.zhangyun.filecloud.common.message.RegisterDeviceResponseMessage;
import com.zhangyun.filecloud.server.database.service.DeviceService;
import com.zhangyun.filecloud.server.service.RedisService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/11/6 11:41
 * @since: 1.0
 */
@Slf4j
@ChannelHandler.Sharable
@Component
public class RegisterDeviceHandler extends SimpleChannelInboundHandler<RegisterDeviceMessage> {
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private RedisService redisService;

    @Override
    @TraceLog
    protected void channelRead0(ChannelHandlerContext ctx, RegisterDeviceMessage msg) throws Exception {
        RegisterDeviceResponseMessage responseMessage = new RegisterDeviceResponseMessage();
        // 生成设备UUID
        String deviceId = UUID.randomUUID().toString();
        boolean isInsert = deviceService.createDevice(deviceId, msg.getUsername(), msg.getDeviceName(), msg.getRootPath());
        responseMessage.setDeviceId(deviceId);
        if (isInsert) {
            // 生成token
            String token = redisService.genToken(msg.getUsername(), deviceId);
            responseMessage.setToken(token);
            ctx.writeAndFlush(responseMessage);
        } else {
            log.info("设备创建失败创建失败");
        }
    }
}
