package com.zhangyun.filecloud.client.service.nettyservice;

import com.zhangyun.filecloud.client.service.NettyClient;
import com.zhangyun.filecloud.common.message.RegisterDeviceMessage;
import com.zhangyun.filecloud.common.message.RegisterDeviceRespMsg;
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
public class RegisterDeviceService extends AbstractNettyService<RegisterDeviceRespMsg>{
    @Autowired
    private NettyClient nettyClient;

    public RegisterDeviceRespMsg registerDevice(String username, String deviceName, String rootPath) throws InterruptedException {
        Channel channel = nettyClient.getChannel();
        RegisterDeviceMessage registerDeviceMessage = new RegisterDeviceMessage();
        registerDeviceMessage.setUsername(username);
        registerDeviceMessage.setDeviceName(deviceName);
        registerDeviceMessage.setRootPath(rootPath);
        channel.writeAndFlush(registerDeviceMessage);
        // 等待响应
        return super.waitForData();
    }
}
