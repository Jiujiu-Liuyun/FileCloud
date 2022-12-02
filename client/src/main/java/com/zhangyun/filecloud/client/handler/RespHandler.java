package com.zhangyun.filecloud.client.handler;

import com.zhangyun.filecloud.common.message.RespMsg;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/12/2 16:14
 * @since: 1.0
 */
@Slf4j
@ChannelHandler.Sharable
@Component
public class RespHandler extends SimpleChannelInboundHandler<RespMsg> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RespMsg msg) throws Exception {
        log.info("resp msg: {}", msg);
    }
}
