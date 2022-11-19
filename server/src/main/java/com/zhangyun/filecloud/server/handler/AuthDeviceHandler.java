package com.zhangyun.filecloud.server.handler;

import com.zhangyun.filecloud.common.annotation.TraceLog;
import com.zhangyun.filecloud.common.enums.RespEnum;
import com.zhangyun.filecloud.common.message.Message;
import com.zhangyun.filecloud.common.message.RespMsg;
import com.zhangyun.filecloud.server.database.service.DeviceService;
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
    private DeviceService deviceService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) {
        RespEnum respEnum = authDevice(msg);

        if (respEnum == RespEnum.OK) {
            // 认证成功
            ctx.fireChannelRead(msg);
        } else {
            ctx.writeAndFlush(new RespMsg(respEnum));
        }
    }

    @TraceLog
    private RespEnum authDevice(Message msg) {
        if (msg.getUsername() ==null || msg.getDeviceId() == null) {
            return RespEnum.MSG_FORMAT_ERROR;
        }
        // 认证token
        boolean authDevice = deviceService.authDevice(msg.getDeviceId(), msg.getUsername());
        return authDevice ? RespEnum.OK : RespEnum.AUTH_DEVICE_FAIL;
    }
}
