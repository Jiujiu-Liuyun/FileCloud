package com.zhangyun.filecloud.server.handler;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.zhangyun.filecloud.common.annotation.TraceLog;
import com.zhangyun.filecloud.common.enums.RespEnum;
import com.zhangyun.filecloud.common.message.LoginMsg;
import com.zhangyun.filecloud.common.message.LoginRespMsg;
import com.zhangyun.filecloud.server.database.entity.Device;
import com.zhangyun.filecloud.server.database.entity.FileChangeRecord;
import com.zhangyun.filecloud.server.database.entity.User;
import com.zhangyun.filecloud.server.database.service.DeviceService;
import com.zhangyun.filecloud.server.database.service.FileChangeRecordService;
import com.zhangyun.filecloud.server.service.RedisService;
import com.zhangyun.filecloud.server.database.service.impl.UserServiceImpl;
import com.zhangyun.filecloud.server.service.session.SessionService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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
public class LoginHandler extends SimpleChannelInboundHandler<LoginMsg> {
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private SessionService sessionService;
    @Autowired
    private FileChangeRecordService fileChangeRecordService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginMsg msg) throws Exception {
        log.info("========>>>>>>>> {}", msg);
        LoginRespMsg loginRespMsg = authLoginMsg(msg);
        if (loginRespMsg.getRespBO() == RespEnum.OK && loginRespMsg.getIsRegister()) {
            // 将连接加入会话管理器
            sessionService.bind(ctx.channel(), msg.getDeviceId());
            // 当设备有效时生成token，否则在注册设备时生成token
            String token = redisService.genToken(msg.getUsername(), msg.getDeviceId());
            loginRespMsg.setToken(token);
            ctx.writeAndFlush(loginRespMsg);
        } else {
            ctx.writeAndFlush(loginRespMsg);
            // 关闭连接
            ctx.channel().close();
        }
    }

    private LoginRespMsg authLoginMsg(LoginMsg msg) {
        RespEnum respEnum = userService.authUsernameAndPassword(msg.getUsername(), msg.getPassword());
        LoginRespMsg loginRespMsg = new LoginRespMsg(respEnum);
        // auth device
        if (msg.getDeviceId() == null) {
            loginRespMsg.setIsRegister(false);
        } else {
            Device device = deviceService.selectDeviceByDeviceId(msg.getDeviceId());
            loginRespMsg.setIsRegister(device != null);
        }
        return loginRespMsg;
    }

}
