package com.zhangyun.filecloud.server.handler;

import com.zhangyun.filecloud.common.message.FileTrfRespMsg;
import com.zhangyun.filecloud.server.service.FileTransferService;
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
 * @date: 2022/11/16 16:36
 * @since: 1.0
 */
@Slf4j
@ChannelHandler.Sharable
@Component
public class FileTransferResponseHandler extends SimpleChannelInboundHandler<FileTrfRespMsg> {
    @Autowired
    private FileTransferService fileTransferService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FileTrfRespMsg msg) throws Exception {

    }
}
