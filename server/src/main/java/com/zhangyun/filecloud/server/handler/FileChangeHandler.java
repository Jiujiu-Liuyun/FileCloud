package com.zhangyun.filecloud.server.handler;

import com.zhangyun.filecloud.common.annotation.TraceLog;
import com.zhangyun.filecloud.common.entity.FileChangeBO;
import com.zhangyun.filecloud.common.enums.StatusEnum;
import com.zhangyun.filecloud.common.enums.TransferModeEnum;
import com.zhangyun.filecloud.common.message.FileChangeMsg;
import com.zhangyun.filecloud.server.config.ServerConfig;
import com.zhangyun.filecloud.server.database.entity.FileChangeRecord;
import com.zhangyun.filecloud.server.database.service.DeviceService;
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
 * @date: 2022/11/15 10:45
 * @since: 1.0
 */
@Slf4j
@ChannelHandler.Sharable
@Component
public class FileChangeHandler extends SimpleChannelInboundHandler<FileChangeMsg> {
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private FileChangeRecordService fileChangeRecordService;

    /**
     * 1. 记录更改，写入数据库（查询是否存在 deviceId + relativePath，存在则删除）
     * 2. 通知在线客户端
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FileChangeMsg msg) throws Exception {
        log.info("========>>>>>>>> {}", msg);
        FileChangeBO fileChangeBO = msg.getFileChangeBO();
        // 1. c ==》 s
        FileChangeRecord fileChangeRecord = new FileChangeRecord();
        fileChangeRecord.setRelativePath(fileChangeBO.getRelativePath());
        fileChangeRecord.setFileType(fileChangeBO.getFileTypeEnum().getCode());
        fileChangeRecord.setOperationType(fileChangeBO.getOperationTypeEnum().getCode());
        fileChangeRecord.setDeviceId(msg.getDeviceId());
        fileChangeRecord.setTransferMode(TransferModeEnum.UPLOAD.getCode());
        fileChangeRecord.setStatus(StatusEnum.GOING.getCode());
        fileChangeRecord.setStartPos(0L);
        fileChangeRecord.setMaxReadLength(ServerConfig.MAX_READ_LENGTH);
        // 批量插入数据库
        fileChangeRecordService.insertOne(fileChangeRecord);
    }

}
