package com.zhangyun.filecloud.client.service.nettyservice;

import com.zhangyun.filecloud.client.service.NettyClient;
import com.zhangyun.filecloud.common.message.RegisterDeviceMsg;
import com.zhangyun.filecloud.common.message.RegisterDeviceRespMsg;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        RegisterDeviceMsg registerDeviceMsg = new RegisterDeviceMsg();
        registerDeviceMsg.setUsername(username);
        registerDeviceMsg.setDeviceName(deviceName);
        registerDeviceMsg.setRootPath(rootPath);
        channel.writeAndFlush(registerDeviceMsg);
        // 等待响应
        return super.waitForData();
    }
}
