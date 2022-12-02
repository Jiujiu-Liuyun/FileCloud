package com.zhangyun.filecloud.client.service.monitor;

import com.zhangyun.filecloud.client.controller.AppController;
import com.zhangyun.filecloud.client.handler.FileTrfRespMsgHandler;
import com.zhangyun.filecloud.client.service.nettyservice.FileChangeService;
import com.zhangyun.filecloud.common.annotation.FileFilter;
import com.zhangyun.filecloud.common.annotation.TraceLog;
import com.zhangyun.filecloud.common.entity.FileChangeBO;
import com.zhangyun.filecloud.common.enums.FileTypeEnum;
import com.zhangyun.filecloud.common.enums.OperationTypeEnum;
import com.zhangyun.filecloud.common.utils.PathUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;

/**
 * description: 文件变动事件处理器类
 *
 * @author: zhangyun
 * @date: 2022/7/22 00:51
 * @since: 1.0
 */
@Service
@Slf4j
public class ClientFileAlterationListenerAdaptor extends FileAlterationListenerAdaptor {
    @Autowired
    private FileChangeService fileChangeService;
    @Autowired
    private AppController appController;
    @Autowired
    private FileTrfRespMsgHandler fileTrfRespMsgHandler;

    @Override
    @TraceLog
    @FileFilter
    public void onFileCreate(File file) {
        boolean release = fileTrfRespMsgHandler.releaseCount(file.getAbsolutePath(), OperationTypeEnum.CREATE);
        if (release) {
            return;
        }

        // 获取文件的相对路径
        Path relativePath = PathUtil.getRelativePath(file.getAbsolutePath(), appController.getUserInfo().getRootPath());
        // 构造FileChangeBO
        FileChangeBO fileChangeBO = new FileChangeBO();
        fileChangeBO.setRelativePath(relativePath.toString());
        fileChangeBO.setOperationTypeEnum(OperationTypeEnum.CREATE);
        fileChangeBO.setFileTypeEnum(FileTypeEnum.FILE);
        fileChangeService.sendFileChangeMessage(fileChangeBO);
    }

    @Override
    @TraceLog
    @FileFilter
    public void onFileChange(File file) {
        boolean release = fileTrfRespMsgHandler.releaseCount(file.getAbsolutePath(), OperationTypeEnum.CHANGE);
        if (release) {
            return;
        }

        // 获取文件的相对路径
        Path relativePath = PathUtil.getRelativePath(file.getAbsolutePath(), appController.getUserInfo().getRootPath());
        // 构造FileChangeBO
        FileChangeBO fileChangeBO = new FileChangeBO();
        fileChangeBO.setRelativePath(relativePath.toString());
        fileChangeBO.setOperationTypeEnum(OperationTypeEnum.CHANGE);
        fileChangeBO.setFileTypeEnum(FileTypeEnum.FILE);
        fileChangeService.sendFileChangeMessage(fileChangeBO);
    }

    @Override
    @TraceLog
    @FileFilter
    public void onFileDelete(File file) {
        boolean release = fileTrfRespMsgHandler.releaseCount(file.getAbsolutePath(), OperationTypeEnum.DELETE);
        if (release) {
            return;
        }

        // 获取文件的相对路径
        Path relativePath = PathUtil.getRelativePath(file.getAbsolutePath(), appController.getUserInfo().getRootPath());
        // 构造FileChangeBO
        FileChangeBO fileChangeBO = new FileChangeBO();
        fileChangeBO.setRelativePath(relativePath.toString());
        fileChangeBO.setOperationTypeEnum(OperationTypeEnum.DELETE);
        fileChangeBO.setFileTypeEnum(FileTypeEnum.FILE);
        fileChangeService.sendFileChangeMessage(fileChangeBO);
    }

    @Override
    @TraceLog
    @FileFilter
    public void onDirectoryCreate(File directory) {
        boolean release = fileTrfRespMsgHandler.releaseCount(directory.getAbsolutePath(), OperationTypeEnum.CREATE);
        if (release) {
            return;
        }

        // 获取文件的相对路径
        Path relativePath = PathUtil.getRelativePath(directory.getAbsolutePath(), appController.getUserInfo().getRootPath());
        // 构造FileChangeBO
        FileChangeBO fileChangeBO = new FileChangeBO();
        fileChangeBO.setRelativePath(relativePath.toString());
        fileChangeBO.setOperationTypeEnum(OperationTypeEnum.CREATE);
        fileChangeBO.setFileTypeEnum(FileTypeEnum.DIRECTORY);
        fileChangeService.sendFileChangeMessage(fileChangeBO);
    }

    @Override
    @TraceLog
    @FileFilter
    public void onDirectoryDelete(File directory) {
        boolean release = fileTrfRespMsgHandler.releaseCount(directory.getAbsolutePath(), OperationTypeEnum.DELETE);
        if (release) {
            return;
        }

        // 获取文件的相对路径
        Path relativePath = PathUtil.getRelativePath(directory.getAbsolutePath(), appController.getUserInfo().getRootPath());
        // 构造FileChangeBO
        FileChangeBO fileChangeBO = new FileChangeBO();
        fileChangeBO.setRelativePath(relativePath.toString());
        fileChangeBO.setOperationTypeEnum(OperationTypeEnum.DELETE);
        fileChangeBO.setFileTypeEnum(FileTypeEnum.DIRECTORY);
        fileChangeService.sendFileChangeMessage(fileChangeBO);
    }
}
