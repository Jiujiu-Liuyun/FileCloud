package com.zhangyun.filecloud.server.handler;

import com.zhangyun.filecloud.common.entity.FileChangeBO;
import com.zhangyun.filecloud.common.entity.FileTransferBO;
import com.zhangyun.filecloud.common.enums.StatusEnum;
import com.zhangyun.filecloud.common.enums.TransferModeEnum;
import com.zhangyun.filecloud.common.message.FileChangeMessage;
import com.zhangyun.filecloud.server.config.Config;
import com.zhangyun.filecloud.server.database.entity.FileChangeRecord;
import com.zhangyun.filecloud.server.database.service.DeviceService;
import com.zhangyun.filecloud.server.service.FileTransferService;
import com.zhangyun.filecloud.server.database.service.FileChangeRecordService;
import com.zhangyun.filecloud.server.service.session.SessionService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
     * 记录更改
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FileChangeMessage msg) throws Exception {
        FileChangeBO fileChangeBO = msg.getFileChangeBO();
        // 1. c ==》 s
        FileTransferBO fileTransferBO1 = new FileTransferBO();
        fileTransferBO1.setRelativePath(fileChangeBO.getRelativePath());
        fileTransferBO1.setFileTypeEnum(fileChangeBO.getFileTypeEnum());
        fileTransferBO1.setOperationTypeEnum(fileChangeBO.getOperationTypeEnum());
        fileTransferBO1.setDeviceId(msg.getDeviceId());
        fileTransferBO1.setTransferModeEnum(TransferModeEnum.UPLOAD);
        fileTransferBO1.setStatusEnum(StatusEnum.GOING);
        fileTransferBO1.setStartPos(0L);
        fileTransferBO1.setMaxReadLength(Config.MAX_READ_LENGTH);
        fileTransferService.addLastByUsername(msg.getUsername(), fileTransferBO1);

        // 2. s ==> others
        // 获取username对应的其他在线设备
        List<String> deviceIds = deviceService.selectDeviceIdsByUsername(msg.getUsername())
                .stream()
                .filter(deviceId -> !deviceId.equals(msg.getDeviceId()))
                .collect(Collectors.toList());
        List<String> deviceIdsOnline = deviceIds
                .stream()
                .filter(deviceId -> sessionService.isOnline(deviceId))
                .collect(Collectors.toList());
        for (String deviceId : deviceIdsOnline) {
            FileTransferBO fileTransferBO2 = new FileTransferBO();
            fileTransferBO2.setRelativePath(fileChangeBO.getRelativePath());
            fileTransferBO2.setFileTypeEnum(fileChangeBO.getFileTypeEnum());
            fileTransferBO2.setOperationTypeEnum(fileChangeBO.getOperationTypeEnum());
            fileTransferBO2.setDeviceId(deviceId);
            fileTransferBO2.setTransferModeEnum(TransferModeEnum.DOWNLOAD);
            fileTransferBO2.setStatusEnum(StatusEnum.GOING);
            fileTransferBO2.setStartPos(0L);
            fileTransferBO2.setMaxReadLength(Config.MAX_READ_LENGTH);
            fileTransferService.addLastByUsername(msg.getUsername(), fileTransferBO2);
        }

        // 3. s ==> offline device 记录到数据库
        // 离线设备
        List<String> deviceIdsOffline = deviceIds
                .stream()
                .filter(deviceId -> !deviceIdsOnline.contains(deviceId))
                .collect(Collectors.toList());
        for (String deviceId : deviceIdsOffline) {
            FileChangeRecord fileChangeRecord = new FileChangeRecord();

            fileChangeRecord.setRelativePath(fileChangeBO.getRelativePath());
            fileChangeRecord.setFileType(fileChangeBO.getFileTypeEnum().getCode());
            fileChangeRecord.setOperationType(fileChangeBO.getOperationTypeEnum().getCode());
            fileChangeRecord.setDeviceId(deviceId);
            fileChangeRecord.setTransferMode(TransferModeEnum.DOWNLOAD.getCode());
            fileChangeRecord.setStatus(StatusEnum.GOING.getCode());
            fileChangeRecord.setStartPos(0L);
            fileChangeRecord.setMaxReadLength(Config.MAX_READ_LENGTH);
            // 记录数据库
            fileChangeRecordService.insertOne(fileChangeRecord);
        }
    }
}
