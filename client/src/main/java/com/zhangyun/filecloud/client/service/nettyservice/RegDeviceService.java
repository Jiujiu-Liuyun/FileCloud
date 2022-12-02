package com.zhangyun.filecloud.client.service.nettyservice;

import com.zhangyun.filecloud.client.service.NettyClient;
import com.zhangyun.filecloud.common.message.RegDeviceMsg;
import com.zhangyun.filecloud.common.message.RegDeviceRespMsg;
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
public class RegDeviceService extends AbstractNettyService<RegDeviceRespMsg>{
    @Autowired
    private NettyClient nettyClient;

    public RegDeviceRespMsg registerDevice(String username, String password, String deviceName, String rootPath) throws InterruptedException {
        Channel channel = nettyClient.getChannel();
        RegDeviceMsg regDeviceMsg = new RegDeviceMsg();
        regDeviceMsg.setUsername(username);
        regDeviceMsg.setPassword(password);
        regDeviceMsg.setDeviceName(deviceName);
        regDeviceMsg.setRootPath(rootPath);
        channel.writeAndFlush(regDeviceMsg);
        // 等待响应
        return super.waitForData();
    }
}
