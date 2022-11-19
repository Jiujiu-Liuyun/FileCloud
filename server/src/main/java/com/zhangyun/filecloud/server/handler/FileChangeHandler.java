package com.zhangyun.filecloud.server.handler;

import com.zhangyun.filecloud.common.entity.FileChangeBO;
import com.zhangyun.filecloud.common.enums.StatusEnum;
import com.zhangyun.filecloud.common.enums.TransferModeEnum;
import com.zhangyun.filecloud.common.message.FileChangeMessage;
import com.zhangyun.filecloud.common.message.NotifyChangeMsg;
import com.zhangyun.filecloud.server.config.ServerConfig;
import com.zhangyun.filecloud.server.database.entity.FileChangeRecord;
import com.zhangyun.filecloud.server.database.service.DeviceService;
import com.zhangyun.filecloud.server.service.FileTransferService;
import com.zhangyun.filecloud.server.database.service.FileChangeRecordService;
import com.zhangyun.filecloud.server.service.session.SessionService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
public class FileChangeHandler extends SimpleChannelInboundHandler<FileChangeMessage> {
    @Autowired
    private FileTransferService fileTransferService;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private SessionService sessionService;
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
    protected void channelRead0(ChannelHandlerContext ctx, FileChangeMessage msg) throws Exception {
        List<FileChangeRecord> fileChangeRecords = new ArrayList<>();
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
        fileChangeRecords.add(fileChangeRecord);
        // 2. s ==> others
        // 获取username对应的其他设备
        List<String> deviceIds = deviceService.selectDeviceIdsByUsername(msg.getUsername());
        List<String> others = deviceIds.stream()
                .filter(deviceId -> !deviceId.equals(msg.getDeviceId()))
                .collect(Collectors.toList());
        for (String deviceId : others) {
            FileChangeRecord fileChangeRecordForOthers = new FileChangeRecord();
            fileChangeRecordForOthers.setRelativePath(fileChangeBO.getRelativePath());
            fileChangeRecordForOthers.setFileType(fileChangeBO.getFileTypeEnum().getCode());
            fileChangeRecordForOthers.setOperationType(fileChangeBO.getOperationTypeEnum().getCode());
            fileChangeRecordForOthers.setDeviceId(deviceId);
            fileChangeRecordForOthers.setTransferMode(TransferModeEnum.DOWNLOAD.getCode());
            fileChangeRecordForOthers.setStatus(StatusEnum.GOING.getCode());
            fileChangeRecordForOthers.setStartPos(0L);
            fileChangeRecordForOthers.setMaxReadLength(ServerConfig.MAX_READ_LENGTH);
            // 记录数据库
            fileChangeRecords.add(fileChangeRecordForOthers);
        }
        // 批量插入数据库
        fileChangeRecordService.insertBatch(fileChangeRecords);
        // 通知在线客户端
        notifyOnlineDevices(deviceIds);
    }

    private void notifyOnlineDevices(List<String> deviceIds) {
        NotifyChangeMsg notifyChangeMsg = new NotifyChangeMsg();
        for (String deviceId : deviceIds) {
            Channel channel = sessionService.getChannel(deviceId);
            // 客户端在线，发送通知消息
            if (channel != null) {
                channel.writeAndFlush(notifyChangeMsg);
            }
        }
    }
}
