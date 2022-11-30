package com.zhangyun.filecloud.client.service.nettyservice;

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

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/11/3 10:08
 * @since: 1.0
 */
@Slf4j
@Service
public class LoginService extends AbstractNettyService<LoginRespMsg>{
    @Autowired
    private NettyClient nettyClient;
    @Autowired
    private FileMonitorService fileMonitorService;

    @TraceLog
    public LoginRespMsg login(String username, String password, String deviceId) {
        // 构建登录消息
        LoginMsg loginMsg = new LoginMsg();
        loginMsg.setUsername(username);
        loginMsg.setPassword(password);
        loginMsg.setDeviceId(deviceId);
        // 建立连接 发送消息
        Channel channel = nettyClient.getChannel();
        channel.writeAndFlush(loginMsg);
        // 等待响应
        return super.waitForData();
    }

    /**
     * 登出
     * @param username
     */
    @TraceLog
    public void logout(String username) {
        // 1.构建登出消息
        LogoutMessage logoutMessage = new LogoutMessage();
        logoutMessage.setUsername(username);
        // 2.发送消息
        Channel channel = nettyClient.getChannel();
        channel.writeAndFlush(logoutMessage);
        try {
            channel.close().sync();
        } catch (InterruptedException e) {
            log.error("channel close error {}", e.getMessage());
        }
    }

}
