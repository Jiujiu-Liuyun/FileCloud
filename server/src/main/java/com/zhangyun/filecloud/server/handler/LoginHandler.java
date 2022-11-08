package com.zhangyun.filecloud.server.handler;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.zhangyun.filecloud.common.annotation.TraceLog;
import com.zhangyun.filecloud.common.message.LoginMessage;
import com.zhangyun.filecloud.common.message.LoginResponseMessage;
import com.zhangyun.filecloud.server.entity.Device;
import com.zhangyun.filecloud.server.entity.User;
import com.zhangyun.filecloud.server.service.IDeviceService;
import com.zhangyun.filecloud.server.service.RedisService;
import com.zhangyun.filecloud.server.service.impl.DeviceServiceImpl;
import com.zhangyun.filecloud.server.service.impl.UserServiceImpl;
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
 * @date: 2022/11/2 23:17
 * @since: 1.0
 */
@Slf4j
@ChannelHandler.Sharable
@Component
public class LoginHandler extends SimpleChannelInboundHandler<LoginMessage> {
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private IDeviceService deviceService;
    @Autowired
    private SessionService sessionService;

    @Override
    @TraceLog
    protected void channelRead0(ChannelHandlerContext ctx, LoginMessage msg) throws Exception {
        if (msg == null || msg.getUsername() == null || msg.getPassword() == null) {
            log.info("接收消息错误: {}", msg);
            return;
        }
        User user = userService.selectUserByName(msg.getUsername());
        LoginResponseMessage responseMessage;
        if (user == null) {
            responseMessage = new LoginResponseMessage(301, "该账户不存在！");
        } else {
            String loginPassword = DigestUtil.md5Hex(msg.getPassword());
            // 判断密码是否相同
            if (ObjectUtil.notEqual(loginPassword, user.getPassword())) {
                responseMessage = new LoginResponseMessage(302, "密码错误");
            } else {
                responseMessage = new LoginResponseMessage(200, "登录成功！");
                // 生成token
                String token = UUID.randomUUID().toString();
                responseMessage.setToken(token);
                // 将token写入Redis
                redisService.setToken(token, msg.getUsername());
                // 将连接加入会话管理器
                sessionService.bind(ctx.channel(), msg.getUsername());
            }
        }

        // 设备是否注册
        if (msg.getDeviceId() == null || msg.getDeviceName() == null || msg.getRootPath() == null) {
            responseMessage.setIsRegister(false);
        } else {
            Device device = deviceService.selectDeviceByDeviceId(msg.getDeviceId());
            responseMessage.setIsRegister(device != null && msg.getDeviceName().equals(device.getDeviceName()) && msg.getRootPath().equals(device.getRootPath()));
        }

        ctx.writeAndFlush(responseMessage);
    }
}
