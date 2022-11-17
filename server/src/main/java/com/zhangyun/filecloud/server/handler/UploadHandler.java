package com.zhangyun.filecloud.server.handler;

import com.alibaba.fastjson.JSONObject;
import com.zhangyun.filecloud.common.annotation.TraceLog;
import com.zhangyun.filecloud.common.enums.FileOperationEnum;
import com.zhangyun.filecloud.common.exception.InvalidArgumentsException;
import com.zhangyun.filecloud.common.message.UploadMessage;
import com.zhangyun.filecloud.common.message.UploadResponseMessage;
import com.zhangyun.filecloud.common.utils.FileUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/10/17 19:55
 * @since: 1.0
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class UploadHandler extends SimpleChannelInboundHandler<UploadMessage> {

    @Override
    @TraceLog
    protected void channelRead0(ChannelHandlerContext ctx, UploadMessage msg) throws Exception {

    }
}
