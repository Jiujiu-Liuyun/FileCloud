package com.zhangyun.filecloud.client.service;

import com.zhangyun.filecloud.common.annotation.TraceLog;
import com.zhangyun.filecloud.common.message.LoginMessage;
import com.zhangyun.filecloud.common.message.LoginReseponseMessage;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.Semaphore;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/11/3 10:08
 * @since: 1.0
 */
@Slf4j
@Service
public class LoginService {
    @Autowired
    private NettyClient nettyClient;

    /**
     * 标识是否接收到来自服务器的登录响应消息
     */
    private Semaphore loginSemaphore = new Semaphore(0);
    public Semaphore getLoginSemaphore() {
        return loginSemaphore;
    }

    private LoginReseponseMessage responseMessage;
    public void setResponseMessage(LoginReseponseMessage responseMessage) {
        this.responseMessage = responseMessage;
    }

    @TraceLog
    public LoginReseponseMessage sendLoginMessage(String username, String password) throws InterruptedException {
        responseMessage = null;
        LoginMessage loginMessage = new LoginMessage();
        loginMessage.setUsername(username);
        loginMessage.setPassword(password);
        // 获取channel
        Channel channel = nettyClient.getChannel();
        channel.writeAndFlush(loginMessage);
        // 等待响应
        loginSemaphore.acquire();
        // 传回服务器响应消息
        return responseMessage;
    }

}
