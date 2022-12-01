package com.zhangyun.filecloud.server.handler;

import com.zhangyun.filecloud.common.entity.FileTrfBO;
import com.zhangyun.filecloud.common.enums.RespEnum;
import com.zhangyun.filecloud.common.message.ReqFTBOMsg;
import com.zhangyun.filecloud.common.message.RespFTBOMsg;
import com.zhangyun.filecloud.server.database.entity.FileChangeRecord;
import com.zhangyun.filecloud.server.database.service.FileChangeRecordService;
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
 * @date: 2022/11/19 15:38
 * @since: 1.0
 */
@Slf4j
@ChannelHandler.Sharable
@Component
public class ReqFTBOHandler extends SimpleChannelInboundHandler<ReqFTBOMsg> {
    @Autowired
    private FileChangeRecordService fileChangeRecordService;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ReqFTBOMsg msg) throws Exception {
        RespFTBOMsg respFTBOMsg = new RespFTBOMsg();
        FileChangeRecord fileChangeRecord = fileChangeRecordService.selectNextFCR(msg.getDeviceId());
        if (fileChangeRecord == null) {
            respFTBOMsg.setRespEnum(RespEnum.NO_MORE_FTBO);
        } else {
            FileTrfBO fileTrfBO = fileChangeRecordService.convertFileChangeRecordToFTBO(fileChangeRecord);
            respFTBOMsg.setFileTrfBO(fileTrfBO);
            respFTBOMsg.setRespEnum(RespEnum.OK);
        }
        ctx.writeAndFlush(respFTBOMsg);
    }
}
