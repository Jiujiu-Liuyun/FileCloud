package com.zhangyun.filecloud.client.service.msgmanager;

import com.zhangyun.filecloud.client.service.NettyClient;
import com.zhangyun.filecloud.common.message.RegisterDeviceMessage;
import com.zhangyun.filecloud.common.message.RegisterDeviceResponseMessage;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.Semaphore;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/11/7 16:35
 * @since: 1.0
 */
@Service
@Slf4j
public class RegisterDeviceService {
    @Autowired
    private NettyClient nettyClient;

    private Semaphore initDeviceSemaphore = new Semaphore(0);
    private RegisterDeviceResponseMessage registerDeviceResponseMessage;

    public Semaphore getInitDeviceSemaphore() {
        return initDeviceSemaphore;
    }

    public void setInitDeviceSemaphore(Semaphore initDeviceSemaphore) {
        this.initDeviceSemaphore = initDeviceSemaphore;
    }

    public RegisterDeviceResponseMessage getRegisterDeviceResponseMessage() {
        return registerDeviceResponseMessage;
    }

    public void setRegisterDeviceResponseMessage(RegisterDeviceResponseMessage registerDeviceResponseMessage) {
        this.registerDeviceResponseMessage = registerDeviceResponseMessage;
    }

    public RegisterDeviceResponseMessage registerDevice(String username, String deviceName, String token, String rootPath) throws InterruptedException {
        Channel channel = nettyClient.getChannel();
        RegisterDeviceMessage registerDeviceMessage = new RegisterDeviceMessage();
        registerDeviceMessage.setUsername(username);
        registerDeviceMessage.setDeviceName(deviceName);
        registerDeviceMessage.setRootPath(rootPath);
        channel.writeAndFlush(registerDeviceMessage);
        // 等待Server响应消息
        initDeviceSemaphore.acquire();
        return registerDeviceResponseMessage;
    }
}
