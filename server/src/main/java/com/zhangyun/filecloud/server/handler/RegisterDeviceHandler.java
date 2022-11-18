package com.zhangyun.filecloud.server.handler;

import com.zhangyun.filecloud.common.annotation.TraceLog;
import com.zhangyun.filecloud.common.enums.RespEnum;
import com.zhangyun.filecloud.common.message.RegisterDeviceMessage;
import com.zhangyun.filecloud.common.message.RegisterDeviceRespMsg;
import com.zhangyun.filecloud.server.database.service.DeviceService;
import com.zhangyun.filecloud.server.database.service.UserService;
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
    @Autowired
    private UserService userService;

    @Override
    @TraceLog
    protected void channelRead0(ChannelHandlerContext ctx, RegisterDeviceMessage msg) throws Exception {
        // 1. 验证username password
        RespEnum respEnum = userService.authUsernameAndPassword(msg.getUsername(), msg.getPassword());
        RegisterDeviceRespMsg respMsg = new RegisterDeviceRespMsg();
        respMsg.setRespEnum(respEnum);
        if (respEnum != RespEnum.OK) {
            ctx.writeAndFlush(respMsg);
            return;
        }
        // todo: 限制设备个数
        // 生成设备UUID
        String deviceId = UUID.randomUUID().toString();
        boolean isInsert = deviceService.createDevice(deviceId, msg.getUsername());
        if (isInsert) {
            respMsg.setDeviceId(deviceId);
            // 生成token
            String token = redisService.genToken(msg.getUsername(), deviceId);
            respMsg.setToken(token);
            ctx.writeAndFlush(respMsg);
        } else {
            log.warn("设备创建失败");
        }
    }
}
