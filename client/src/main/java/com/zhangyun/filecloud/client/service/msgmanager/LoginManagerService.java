package com.zhangyun.filecloud.client.service.msgmanager;

import com.zhangyun.filecloud.client.service.NettyClient;
import com.zhangyun.filecloud.common.annotation.TraceLog;
import com.zhangyun.filecloud.common.message.LoginMessage;
import com.zhangyun.filecloud.common.message.LoginResponseMessage;
import com.zhangyun.filecloud.common.message.LogoutMessage;
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
public class LoginManagerService {
    @Autowired
    private NettyClient nettyClient;

    /**
     * 标识是否接收到来自服务器的登录响应消息
     */
    private Semaphore loginSemaphore = new Semaphore(0);
    public Semaphore getLoginSemaphore() {
        return loginSemaphore;
    }

    private LoginResponseMessage responseMessage;
    public void setResponseMessage(LoginResponseMessage responseMessage) {
        this.responseMessage = responseMessage;
    }

    @TraceLog
    public LoginResponseMessage login(String username, String password, String deviceId, String rootPath, String deviceName) throws InterruptedException {
        responseMessage = null;
        LoginMessage loginMessage = new LoginMessage();
        loginMessage.setUsername(username);
        loginMessage.setPassword(password);
        loginMessage.setDeviceId(deviceId);
        loginMessage.setRootPath(rootPath);
        loginMessage.setDeviceName(deviceName);
        // 发送消息
        Channel channel = nettyClient.getChannel();
        channel.writeAndFlush(loginMessage);
        // 等待响应
        loginSemaphore.acquire();
        // 传回服务器响应消息
        return responseMessage;
    }

    @TraceLog
    public void logout(String username) {
        // 1.发送登出消息
        LogoutMessage logoutMessage = new LogoutMessage();
        logoutMessage.setUsername(username);
        // 发送消息
        Channel channel = nettyClient.getChannel();
        channel.writeAndFlush(logoutMessage);
    }

}
