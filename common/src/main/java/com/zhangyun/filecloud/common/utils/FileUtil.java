package com.zhangyun.filecloud.common.utils;

import cn.hutool.core.util.ObjectUtil;
import com.zhangyun.filecloud.common.annotation.TraceLog;
import com.zhangyun.filecloud.common.enums.FileStatusEnum;
import com.zhangyun.filecloud.common.exception.InvalidArgumentsException;
import com.zhangyun.filecloud.common.message.UploadMessage;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Path;
import java.util.Properties;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/10/15 00:45
 * @since: 1.0
 */
@Slf4j
public class FileUtil {
    public static final int MAX_READ_LENGTH = 1000000;

    public static void readFile(UploadMessage message) {
        if (message.getFilePath() == null || message.getStartPos() == null) {
            throw new InvalidArgumentsException("message has no file path");
        }

        // 读文件
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(message.getFilePath(), "r");
            long length = randomAccessFile.length();
            if (length - message.getStartPos() <= 0) {
                log.info("file read finished");
                message.setStatusEnum(FileStatusEnum.FINISHED);
                randomAccessFile.close();
                return;
            }
            int readLength = (int) Math.min(MAX_READ_LENGTH, length - message.getStartPos());
            byte[] bytes = new byte[readLength];
            randomAccessFile.seek(message.getStartPos());
            randomAccessFile.read(bytes);
            message.setMessageBody(bytes);
            // 判断是否已经读完
            if (message.getStartPos() + readLength >= length) {
                message.setStatusEnum(FileStatusEnum.FINISHED);
            } else {
                message.setStatusEnum(FileStatusEnum.UPLOADING);
            }
            randomAccessFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeFile(UploadMessage message, String destPath) {
        if (message.getFilePath() == null || message.getStartPos() == null) {
            throw new InvalidArgumentsException("message has no file path");
        }

        // 写文件
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(destPath, "rw");
            if (message.getMessageBody() != null && message.getMessageBody().length > 0) {
                randomAccessFile.seek(message.getStartPos());
                randomAccessFile.write(message.getMessageBody());
            }
            randomAccessFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File sourceMapToTarget(File sourceFile, String sourcePath, String targetPath) {
        String targetFile = sourceFile.getPath().replace(sourcePath, targetPath);
        log.info("{} =====> {}", sourceFile, targetFile);
        return new File(targetFile);
    }


}
