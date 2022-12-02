package com.zhangyun.filecloud.client.handler;

import com.zhangyun.filecloud.client.service.nettyservice.RegDeviceService;
import com.zhangyun.filecloud.common.message.RegDeviceRespMsg;
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
 * @date: 2022/11/6 13:38
 * @since: 1.0
 */
@Slf4j
@ChannelHandler.Sharable
@Component
public class RegDeviceRespHandler extends SimpleChannelInboundHandler<RegDeviceRespMsg> {
    @Autowired
    private RegDeviceService regDeviceService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RegDeviceRespMsg msg) throws Exception {
        log.info(">>>>>>>>>>>>>>>> {}", msg);
        regDeviceService.setData(msg);
        regDeviceService.dataIsReady();
    }
}
