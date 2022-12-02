package com.zhangyun.filecloud.client.service.nettyservice;

import com.zhangyun.filecloud.client.service.NettyClient;
import com.zhangyun.filecloud.common.message.RegDeviceMsg;
import com.zhangyun.filecloud.common.message.RegDeviceRespMsg;
import com.zhangyun.filecloud.common.message.RegUserMsg;
import com.zhangyun.filecloud.common.message.RegUserRespMsg;
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
public class RegUserService extends AbstractNettyService<RegUserRespMsg>{
    @Autowired
    private NettyClient nettyClient;

    public RegUserRespMsg registerUser(String username, String password) {
        Channel channel = nettyClient.getChannel();
        RegUserMsg regUserMsg = new RegUserMsg();
        regUserMsg.setUsername(username);
        regUserMsg.setPassword(password);
        channel.writeAndFlush(regUserMsg);
        // 等待响应
        return super.waitForData();
    }
}
