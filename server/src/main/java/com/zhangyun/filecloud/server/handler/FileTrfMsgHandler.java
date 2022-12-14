package com.zhangyun.filecloud.server.handler;

import com.zhangyun.filecloud.common.annotation.TraceLog;
import com.zhangyun.filecloud.common.entity.FileTrfBO;
import com.zhangyun.filecloud.common.enums.*;
import com.zhangyun.filecloud.common.message.FileTrfMsg;
import com.zhangyun.filecloud.common.message.FileTrfRespMsg;
import com.zhangyun.filecloud.common.message.NotifyChangeMsg;
import com.zhangyun.filecloud.common.utils.FileUtil;
import com.zhangyun.filecloud.common.utils.PathUtil;
import com.zhangyun.filecloud.server.config.ServerConfig;
import com.zhangyun.filecloud.server.database.entity.FileChangeRecord;
import com.zhangyun.filecloud.server.database.service.DeviceService;
import com.zhangyun.filecloud.server.database.service.FileChangeRecordService;
import com.zhangyun.filecloud.server.service.RedisService;
import com.zhangyun.filecloud.server.service.session.SessionService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/11/19 11:15
 * @since: 1.0
 */
@Slf4j
@ChannelHandler.Sharable
@Component
public class FileTrfMsgHandler extends SimpleChannelInboundHandler<FileTrfMsg> {
    @Autowired
    private FileChangeRecordService fileChangeRecordService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private SessionService sessionService;

    @Override
    @Transactional
    protected void channelRead0(ChannelHandlerContext ctx, FileTrfMsg msg) throws Exception {
        log.info(">>>>>>>>>>>>>>>> {}", msg);
        // 1. ????????????
        boolean auth = authFileTrfBO(msg);
        if (!auth) {
            ctx.writeAndFlush(new FileTrfRespMsg(RespEnum.MSG_FORMAT_ERROR));
            return;
        }
        // 2. ??????
        boolean lock = redisService.lockForDevice(msg.getDeviceId());
        if (!lock) {
            ctx.writeAndFlush(new FileTrfRespMsg(RespEnum.LOCK_DEVICE_FAIL));
            return;
        }
        // 3. ??????msg???????????????
        FileChangeRecord fileChangeRecord = fileChangeRecordService.selectFCRByRelativePathAndDeviceId(msg.getFileTrfBO().getRelativePath(), msg.getDeviceId(),
                msg.getFileTrfBO().getFileTypeEnum().getCode());
        if (fileChangeRecord == null || !fileChangeRecord.getId().equals(msg.getFileTrfBO().getId())) {
            ctx.writeAndFlush(new FileTrfRespMsg(RespEnum.FCR_NOT_EXIST));
            // ?????????
            redisService.unlockForDevice(msg.getDeviceId());
            return;
        }
        // 3. ??????FTM
        FileTrfRespMsg fileTrfRespMsg = handleFileTrfMsg(msg);
        // 4. ???????????????
        if (fileTrfRespMsg.getFileTrfBO().getStatusEnum() == StatusEnum.FINISHED) {
            // ??????FCR
            fileChangeRecordService.deleteById(fileChangeRecord.getId());
            // ??????
            if (fileTrfRespMsg.getFileTrfBO().getTransferModeEnum() == TransferModeEnum.UPLOAD) {
                // 2. s ==> others
                List<FileChangeRecord> fileChangeRecords = new ArrayList<>();
                // ??????username?????????????????????
                List<String> deviceIds = deviceService.selectDeviceIdsByUsername(msg.getUsername());
                List<String> others = deviceIds.stream()
                        .filter(deviceId -> !deviceId.equals(msg.getDeviceId()))
                        .collect(Collectors.toList());
                for (String deviceId : others) {
                    FileChangeRecord fcrForOthers = new FileChangeRecord();
                    fcrForOthers.setRelativePath(fileTrfRespMsg.getFileTrfBO().getRelativePath());
                    fcrForOthers.setFileType(fileTrfRespMsg.getFileTrfBO().getFileTypeEnum().getCode());
                    fcrForOthers.setOperationType(fileTrfRespMsg.getFileTrfBO().getOperationTypeEnum().getCode());
                    fcrForOthers.setDeviceId(deviceId);
                    fcrForOthers.setTransferMode(TransferModeEnum.DOWNLOAD.getCode());
                    fcrForOthers.setStatus(StatusEnum.GOING.getCode());
                    fcrForOthers.setStartPos(0L);
                    fcrForOthers.setMaxReadLength(ServerConfig.MAX_READ_LENGTH);
                    // ???????????????
                    fileChangeRecords.add(fcrForOthers);
                }
                // ?????????????????????
                fileChangeRecordService.insertBatch(fileChangeRecords);
                // ?????????????????????
                notifyOnlineDevices(deviceIds);
            }
        } else if (fileTrfRespMsg.getFileTrfBO().getStatusEnum() == StatusEnum.GOING) {
            // ??????startPos
            fileChangeRecordService.updateStartPosById(fileTrfRespMsg.getNextPos(), fileChangeRecord.getId());
        }
        // 5. ????????????FTBO
        FileChangeRecord newFCR = fileChangeRecordService.selectNextFCR(msg.getDeviceId());
        FileTrfBO nextFileTrfBO = fileChangeRecordService.convertFileChangeRecordToFTBO(newFCR);
        fileTrfRespMsg.setNextFileTrfBO(nextFileTrfBO);
        // 5. ???????????????
        ctx.writeAndFlush(fileTrfRespMsg);
        // 6. ?????????
        redisService.unlockForDevice(msg.getDeviceId());
    }

    /**
     * ??????FTM?????????????????????
     * @param msg
     * @return
     * @throws IOException
     */
    private FileTrfRespMsg handleFileTrfMsg(FileTrfMsg msg) throws IOException {
        FileTrfBO fileTrfBO = msg.getFileTrfBO();
        FileTrfRespMsg fileTrfRespMsg = new FileTrfRespMsg();
        fileTrfRespMsg.setFileTrfBO(fileTrfBO);
        // 1. ??????????????????
        Path absolutePath = PathUtil.getAbsolutePath(fileTrfBO.getRelativePath(), ServerConfig.ROOT_PATH);
        if (fileTrfBO.getTransferModeEnum() == TransferModeEnum.UPLOAD) {
            // 2.1 ???
            if (fileTrfBO.getFileTypeEnum() == FileTypeEnum.DIRECTORY && fileTrfBO.getOperationTypeEnum() == OperationTypeEnum.CREATE) {
                FileUtil.createDir(absolutePath);
            } else if (fileTrfBO.getFileTypeEnum() == FileTypeEnum.DIRECTORY && fileTrfBO.getOperationTypeEnum() == OperationTypeEnum.CHANGE) {
                FileUtil.changeDir(absolutePath);
            } else if (fileTrfBO.getFileTypeEnum() == FileTypeEnum.DIRECTORY && fileTrfBO.getOperationTypeEnum() == OperationTypeEnum.DELETE) {
                FileUtil.deleteDir(absolutePath);
            } else if (fileTrfBO.getFileTypeEnum() == FileTypeEnum.FILE &&
                    (fileTrfBO.getOperationTypeEnum() == OperationTypeEnum.CREATE || fileTrfBO.getOperationTypeEnum() == OperationTypeEnum.CHANGE)) {
                FileUtil.writeFile(absolutePath.toString(), fileTrfBO.getStartPos(), msg.getMessageBody());
                // nextPos (status??????????????????)
                fileTrfRespMsg.setNextPos(fileTrfBO.getStartPos() + msg.getMessageBody().length);
            } else if (fileTrfBO.getFileTypeEnum() == FileTypeEnum.FILE && fileTrfBO.getOperationTypeEnum() == OperationTypeEnum.DELETE) {
                if (new File(absolutePath.toString()).isFile()) {
                    Files.delete(absolutePath);
                }
            }
        } else if (fileTrfBO.getTransferModeEnum() == TransferModeEnum.DOWNLOAD) {
            if (fileTrfBO.getFileTypeEnum() == FileTypeEnum.FILE &&
                    (fileTrfBO.getOperationTypeEnum() == OperationTypeEnum.CREATE ||
                            fileTrfBO.getOperationTypeEnum() == OperationTypeEnum.CHANGE)) {
                // 2.2 ??????????????????????????????
                byte[] bytes = FileUtil.readFile(absolutePath.toString(), fileTrfBO.getStartPos(), fileTrfBO.getMaxReadLength());
                fileTrfRespMsg.setMessageBody(bytes);
                if (bytes.length < fileTrfBO.getMaxReadLength()) {
                    fileTrfBO.setStatusEnum(StatusEnum.FINISHED);
                } else {
                    fileTrfBO.setStatusEnum(StatusEnum.GOING);
                }
                fileTrfRespMsg.setNextPos(fileTrfBO.getStartPos() + bytes.length);
            } else {
                fileTrfBO.setStatusEnum(StatusEnum.FINISHED);
            }
        }
        fileTrfRespMsg.setRespEnum(RespEnum.OK);
        return fileTrfRespMsg;
    }

    /**
     * FTM??????????????????
     * @param fileTrfMsg
     * @return
     */
    private boolean authFileTrfBO(FileTrfMsg fileTrfMsg) {
        FileTrfBO fileTrfBO = fileTrfMsg.getFileTrfBO();
        if (fileTrfBO == null) {
            return false;
        }
        if (fileTrfBO.getTransferModeEnum() == null || fileTrfBO.getFileTypeEnum() == null || fileTrfBO.getMaxReadLength() == null
                || fileTrfBO.getOperationTypeEnum() == null || fileTrfBO.getRelativePath() == null || fileTrfBO.getStartPos() == null
                || fileTrfBO.getStatusEnum() == null || fileTrfBO.getDeviceId() == null) {
            return false;
        }
        return true;
    }

    /**
     * ???????????????????????????
     * @param deviceIds
     */
    private void notifyOnlineDevices(List<String> deviceIds) {
        NotifyChangeMsg notifyChangeMsg = new NotifyChangeMsg();
        for (String deviceId : deviceIds) {
            Channel channel = sessionService.getChannel(deviceId);
            // ????????????????????????????????????
            if (channel != null) {
                channel.writeAndFlush(notifyChangeMsg);
            }
        }
    }
}
