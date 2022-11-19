package com.zhangyun.filecloud.client.service.nettyservice;

import com.zhangyun.filecloud.client.config.ClientConfig;
import com.zhangyun.filecloud.client.service.NettyClient;
import com.zhangyun.filecloud.client.service.monitor.FileMonitorService;
import com.zhangyun.filecloud.common.annotation.TraceLog;
import com.zhangyun.filecloud.common.message.LoginMsg;
import com.zhangyun.filecloud.common.message.LoginRespMsg;
import com.zhangyun.filecloud.common.message.LogoutMessage;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

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
    @Autowired
    private FileMonitorService fileMonitorService;

    /**
     * 标识是否接收到来自服务器的登录响应消息
     */
    private Semaphore loginSemaphore = new Semaphore(0);
    public Semaphore getLoginSemaphore() {
        return loginSemaphore;
    }

    private LoginRespMsg responseMessage;
    public void setResponseMessage(LoginRespMsg responseMessage) {
        this.responseMessage = responseMessage;
    }

    @TraceLog
    public LoginRespMsg login(String username, String password, String deviceId) {
        // 构建登录消息
        responseMessage = null;
        LoginMsg loginMsg = new LoginMsg();
        loginMsg.setUsername(username);
        loginMsg.setPassword(password);
        loginMsg.setDeviceId(deviceId);
        // 建立连接 发送消息
        Channel channel = nettyClient.getChannel();
        channel.writeAndFlush(loginMsg);
        // 等待响应
        try {
            boolean acquire = loginSemaphore.tryAcquire(ClientConfig.NETTY_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (!acquire) {
                responseMessage = null;
            }
        } catch (InterruptedException e) {
            responseMessage = null;
            log.info("netty 等待超时");
        }
        // 传回服务器响应消息
        return responseMessage;
    }

    @TraceLog
    public void logout(String username) {
        // 1.构建登出消息
        LogoutMessage logoutMessage = new LogoutMessage();
        logoutMessage.setUsername(username);
        // 2.发送消息
        Channel channel = nettyClient.getChannel();
        channel.writeAndFlush(logoutMessage);
        // 3.关闭文件监听器
        fileMonitorService.closeMonitor();
    }

}
