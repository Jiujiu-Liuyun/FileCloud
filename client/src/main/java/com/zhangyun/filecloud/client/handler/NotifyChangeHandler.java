package com.zhangyun.filecloud.client.handler;

import com.zhangyun.filecloud.client.service.nettyservice.RegUserService;
import com.zhangyun.filecloud.common.message.NotifyChangeMsg;
import com.zhangyun.filecloud.common.message.RegUserRespMsg;
import com.zhangyun.filecloud.common.message.ReqFTBOMsg;
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
public class NotifyChangeHandler extends SimpleChannelInboundHandler<NotifyChangeMsg> {
    @Autowired
    private RegUserService regUserService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NotifyChangeMsg msg) throws Exception {
        log.info(">>>>>>>>>>>>>>>> {}", msg);
        ctx.writeAndFlush(new ReqFTBOMsg());
    }
}
