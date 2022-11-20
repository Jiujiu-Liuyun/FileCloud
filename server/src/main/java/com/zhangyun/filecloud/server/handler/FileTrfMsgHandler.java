package com.zhangyun.filecloud.server.handler;

import com.zhangyun.filecloud.common.entity.FileTrfBO;
import com.zhangyun.filecloud.common.enums.*;
import com.zhangyun.filecloud.common.message.FileTrfMsg;
import com.zhangyun.filecloud.common.message.FileTrfRespMsg;
import com.zhangyun.filecloud.common.utils.FileUtil;
import com.zhangyun.filecloud.common.utils.PathUtil;
import com.zhangyun.filecloud.server.config.ServerConfig;
import com.zhangyun.filecloud.server.database.entity.FileChangeRecord;
import com.zhangyun.filecloud.server.database.service.DeviceService;
import com.zhangyun.filecloud.server.database.service.FileChangeRecordService;
import com.zhangyun.filecloud.server.service.RedisService;
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

    @Override
    @Transactional
    protected void channelRead0(ChannelHandlerContext ctx, FileTrfMsg msg) throws Exception {
        // 1. 校验消息
        boolean auth = authFileTrfBO(msg);
        if (!auth) {
            ctx.writeAndFlush(new FileTrfRespMsg(RespEnum.MSG_FORMAT_ERROR));
            return;
        }
        // 2. 加锁
        boolean lock = redisService.lockForDevice(msg.getDeviceId());
        if (!lock) {
            ctx.writeAndFlush(new FileTrfRespMsg(RespEnum.LOCK_DEVICE_FAIL));
            return;
        }
        // 3. 查询msg对应的记录
        FileChangeRecord fileChangeRecord = fileChangeRecordService.selectFCRByRelativePathAndDeviceId(msg.getFileTrfBO().getRelativePath(), msg.getDeviceId(),
                msg.getFileTrfBO().getFileTypeEnum().getCode());
        if (fileChangeRecord == null || !fileChangeRecord.getId().equals(msg.getFileTrfBO().getId())) {
            ctx.writeAndFlush(new FileTrfRespMsg(RespEnum.FCR_NOT_EXIST));
            // 释放锁
            redisService.unlockForDevice(msg.getDeviceId());
            return;
        }
        // 3. 处理FTM
        FileTrfRespMsg fileTrfRespMsg = handleFileTrfMsg(msg);
        // 4. 更新数据库
        if (fileTrfRespMsg.getFileTrfBO().getStatusEnum() == StatusEnum.FINISHED) {
            // 删除FCR
            fileChangeRecordService.deleteById(fileChangeRecord.getId());
        } else if (fileTrfRespMsg.getFileTrfBO().getStatusEnum() == StatusEnum.GOING) {
            // 更新startPos
            fileChangeRecordService.updateStartPosById(fileTrfRespMsg.getNextPos(), fileChangeRecord.getId());
        }
        // 5. 查询新的FTBO
        FileChangeRecord newFCR = fileChangeRecordService.selectNextFCR(msg.getDeviceId());
        FileTrfBO fileTrfBO = fileChangeRecordService.convertFileChangeRecordToFTBO(newFCR);
        fileTrfRespMsg.setFileTrfBO(fileTrfBO);
        // 5. 响应客户端
        ctx.writeAndFlush(fileTrfRespMsg);
        // 6. 释放锁
        redisService.unlockForDevice(msg.getDeviceId());
    }

    /**
     * 处理FTM消息，文件读写
     * @param msg
     * @return
     * @throws IOException
     */
    private FileTrfRespMsg handleFileTrfMsg(FileTrfMsg msg) throws IOException {
        FileTrfBO fileTrfBO = msg.getFileTrfBO();
        FileTrfRespMsg fileTrfRespMsg = new FileTrfRespMsg();
        fileTrfRespMsg.setFileTrfBO(fileTrfBO);
        // 1. 文件绝对路径
        Path absolutePath = PathUtil.getAbsolutePath(fileTrfBO.getRelativePath(), ServerConfig.ROOT_PATH);
        if (fileTrfBO.getTransferModeEnum() == TransferModeEnum.UPLOAD) {
            // 2.1 写
            if (fileTrfBO.getFileTypeEnum() == FileTypeEnum.DIRECTORY && fileTrfBO.getOperationTypeEnum() == OperationTypeEnum.CREATE) {
                FileUtil.createDir(absolutePath);
            } else if (fileTrfBO.getFileTypeEnum() == FileTypeEnum.DIRECTORY && fileTrfBO.getOperationTypeEnum() == OperationTypeEnum.CHANGE) {
                FileUtil.changeDir(absolutePath);
            } else if (fileTrfBO.getFileTypeEnum() == FileTypeEnum.DIRECTORY && fileTrfBO.getOperationTypeEnum() == OperationTypeEnum.DELETE) {
                FileUtil.deleteDir(absolutePath);
            } else if (fileTrfBO.getFileTypeEnum() == FileTypeEnum.FILE &&
                    (fileTrfBO.getOperationTypeEnum() == OperationTypeEnum.CREATE || fileTrfBO.getOperationTypeEnum() == OperationTypeEnum.CHANGE)) {
                FileUtil.writeFile(absolutePath.toString(), fileTrfBO.getStartPos(), msg.getMessageBody());
                // nextPos (status由客户端更新)
                fileTrfRespMsg.setNextPos(fileTrfBO.getStartPos() + msg.getMessageBody().length);
            } else if (fileTrfBO.getFileTypeEnum() == FileTypeEnum.FILE && fileTrfBO.getOperationTypeEnum() == OperationTypeEnum.DELETE) {
                if (new File(absolutePath.toString()).isFile()) {
                    Files.delete(absolutePath);
                }
            }
        } else if (fileTrfBO.getTransferModeEnum() == TransferModeEnum.DOWNLOAD) {
            if (fileTrfBO.getFileTypeEnum() == FileTypeEnum.FILE &&
                    (fileTrfBO.getOperationTypeEnum() == OperationTypeEnum.CREATE || fileTrfBO.getOperationTypeEnum() == OperationTypeEnum.CHANGE)) {
                // 2.2 读出文件，传给客户端
                byte[] bytes = FileUtil.readFile(absolutePath.toString(), fileTrfBO.getStartPos(), fileTrfBO.getMaxReadLength());
                fileTrfRespMsg.setMessageBody(bytes);
                if (bytes.length < fileTrfBO.getMaxReadLength()) {
                    fileTrfBO.setStatusEnum(StatusEnum.FINISHED);
                }
                fileTrfRespMsg.setNextPos(fileTrfBO.getStartPos() + bytes.length);
            }
        }
        fileTrfRespMsg.setRespEnum(RespEnum.OK);
        return fileTrfRespMsg;
    }

    /**
     * FTM消息参数校验
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
        if (fileTrfBO.getTransferModeEnum() == TransferModeEnum.UPLOAD && fileTrfMsg.getMessageBody() == null) {
            return false;
        }
        return true;
    }
}
