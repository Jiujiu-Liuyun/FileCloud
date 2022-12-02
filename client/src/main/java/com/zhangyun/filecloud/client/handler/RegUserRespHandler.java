package com.zhangyun.filecloud.client.handler;

import com.zhangyun.filecloud.client.service.nettyservice.RegDeviceService;
import com.zhangyun.filecloud.client.service.nettyservice.RegUserService;
import com.zhangyun.filecloud.common.message.RegUserRespMsg;
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
 * @date: 2022/12/2 15:06
 * @since: 1.0
 */
@Slf4j
@ChannelHandler.Sharable
@Component
public class RegUserRespHandler extends SimpleChannelInboundHandler<RegUserRespMsg> {
    @Autowired
    private RegUserService regUserService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RegUserRespMsg msg) throws Exception {
        log.info(">>>>>>>>>>>>>>>> {}", msg);
        regUserService.setData(msg);
        regUserService.dataIsReady();
    }
}
