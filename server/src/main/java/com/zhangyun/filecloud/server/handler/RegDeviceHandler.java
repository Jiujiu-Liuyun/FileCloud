package com.zhangyun.filecloud.server.handler;

import com.zhangyun.filecloud.common.enums.RespEnum;
import com.zhangyun.filecloud.common.message.RegDeviceMsg;
import com.zhangyun.filecloud.common.message.RegDeviceRespMsg;
import com.zhangyun.filecloud.server.database.service.DeviceService;
import com.zhangyun.filecloud.server.database.service.UserService;
import com.zhangyun.filecloud.server.service.RedisService;
import com.zhangyun.filecloud.server.service.session.SessionService;
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
public class RegDeviceHandler extends SimpleChannelInboundHandler<RegDeviceMsg> {
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private UserService userService;
    @Autowired
    private SessionService sessionService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RegDeviceMsg msg) throws Exception {
        log.info("========>>>>>>>> {}", msg);
        // 1. 验证username password
        RespEnum respEnum = userService.authUsernameAndPassword(msg.getUsername(), msg.getPassword());
        RegDeviceRespMsg respMsg = new RegDeviceRespMsg();
        respMsg.setRespEnum(respEnum);
        if (respEnum != RespEnum.OK) {
            ctx.writeAndFlush(respMsg);
            log.info("<<<<<<<<======== {}", respMsg);
            return;
        }
        // todo: 限制设备个数
        // 生成设备UUID
        String deviceId = UUID.randomUUID().toString();
        boolean isInsert = deviceService.createDevice(deviceId, msg.getUsername(), msg.getDeviceName(), msg.getRootPath());
        if (isInsert) {
            respMsg.setDeviceId(deviceId);
            // 将连接加入会话管理器
            sessionService.bind(ctx.channel(), msg.getDeviceId());
            // 生成token
            String token = redisService.genToken(msg.getUsername(), deviceId);
            respMsg.setToken(token);
        } else {
            respMsg.setRespEnum(RespEnum.REGISTER_DEVICE_FAIL);
            log.warn("设备创建失败");
        }
        ctx.writeAndFlush(respMsg);
        log.info("<<<<<<<<======== {}", respMsg);
    }
}
