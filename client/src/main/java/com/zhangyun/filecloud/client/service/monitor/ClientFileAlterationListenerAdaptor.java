package com.zhangyun.filecloud.client.service.monitor;

import com.zhangyun.filecloud.common.annotation.FileFilter;
import com.zhangyun.filecloud.common.annotation.TraceLog;
import com.zhangyun.filecloud.client.service.FileUploadService;
import com.zhangyun.filecloud.common.enums.FileOperationEnum;
import com.zhangyun.filecloud.common.enums.FileStatusEnum;
import com.zhangyun.filecloud.common.message.UploadMessage;
import com.zhangyun.filecloud.common.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.springframework.stereotype.Service;

import java.io.File;

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
    @Override
    @FileFilter
    @TraceLog
    public void onDirectoryCreate(File directory) {
        addUploadMessageList(FileOperationEnum.DIRECTORY_CREATE, directory, false);
    }

    @Override
    @TraceLog
    @FileFilter
    public void onDirectoryChange(File directory) {
        addUploadMessageList(FileOperationEnum.DIRECTORY_CHANGE, directory, false);
    }

    @Override
    @TraceLog
    @FileFilter
    public void onDirectoryDelete(File directory) {
        addUploadMessageList(FileOperationEnum.DIRECTORY_DELETE, directory, false);
    }

    @Override
    @TraceLog
    @FileFilter
    public void onFileCreate(File file) {
        addUploadMessageList(FileOperationEnum.FILE_CREATE, file, true);
    }

    @Override
    @TraceLog
    @FileFilter
    public void onFileChange(File file) {
        addUploadMessageList(FileOperationEnum.FILE_CHANGE, file, true);
    }

    @Override
    @TraceLog
    @FileFilter
    public void onFileDelete(File file) {
        addUploadMessageList(FileOperationEnum.FILE_DELETE, file, false);
    }

    private UploadMessage constructUploadMessage(FileOperationEnum operationEnum, File file, boolean read) {
        UploadMessage uploadMessage = new UploadMessage();
        uploadMessage.setOperationEnum(operationEnum);
        uploadMessage.setStartPos(0L);
        uploadMessage.setFilePath(file.getAbsolutePath());
        uploadMessage.setLastModified(file.lastModified());

        // 是否需要读文件
        if (read) {
            FileUtil.readFile(uploadMessage);
        } else {
            uploadMessage.setStatusEnum(FileStatusEnum.FINISHED);
        }
        return uploadMessage;
    }

    private void addUploadMessageList(FileOperationEnum operationEnum, File file, boolean read) {
        // 构造文件上传消息
        UploadMessage fileTransferMessage = constructUploadMessage(operationEnum, file, read);
        // 添加到处理列表
        FileUploadService.FILE_UPLOAD_MESSAGE_LIST.addLast(fileTransferMessage);
    }
}
