package com.zhangyun.filecloud.server.handler;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.zhangyun.filecloud.common.message.LoginMessage;
import com.zhangyun.filecloud.common.message.LoginReseponseMessage;
import com.zhangyun.filecloud.server.entity.User;
import com.zhangyun.filecloud.server.service.impl.UserServiceImpl;
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
 * @date: 2022/11/2 23:17
 * @since: 1.0
 */
@Slf4j
@ChannelHandler.Sharable
@Component
public class LoginHandler extends SimpleChannelInboundHandler<LoginMessage> {
    @Autowired
    private UserServiceImpl userService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginMessage msg) throws Exception {
        if (msg == null || msg.getUsername() == null || msg.getPassword() == null) {
            log.info("接收消息错误: {}", msg);
            return;
        }
        User user = userService.selectUserByName(msg.getUsername());
        LoginReseponseMessage reseponseMessage;
        if (user == null) {
            reseponseMessage = new LoginReseponseMessage(301, "该账户不存在！");
        } else {
            String loginPassword = DigestUtil.md5Hex(msg.getPassword());
            // 判断密码是否相同
            if (ObjectUtil.notEqual(loginPassword, user.getPassword())) {
                reseponseMessage = new LoginReseponseMessage(302, "密码错误");
            } else {
                reseponseMessage = new LoginReseponseMessage(200, "登录成功！");
            }
        }
        ctx.writeAndFlush(reseponseMessage);
    }
}
