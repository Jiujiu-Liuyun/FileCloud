package com.zhangyun.filecloud.common.utils;

import com.zhangyun.filecloud.common.entity.FileTransferBO;
import com.zhangyun.filecloud.common.enums.StatusEnum;
import com.zhangyun.filecloud.common.enums.UploadStatusEnum;
import com.zhangyun.filecloud.common.exception.InvalidArgumentsException;
import com.zhangyun.filecloud.common.message.UploadMessage;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

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

//    public static void readFile(UploadMessage message) {
//        if (message.getRelativePath() == null || message.getStartPos() == null) {
//            throw new InvalidArgumentsException("message has no file path");
//        }
//
//        // 读文件
//        try {
//            RandomAccessFile randomAccessFile = new RandomAccessFile(message.getRelativePath(), "r");
//            long length = randomAccessFile.length();
//            if (length - message.getStartPos() <= 0) {
//                log.info("file read finished");
//                message.setStatusEnum(StatusEnum.FINISHED);
//                randomAccessFile.close();
//                return;
//            }
//            int readLength = (int) Math.min(MAX_READ_LENGTH, length - message.getStartPos());
//            byte[] bytes = new byte[readLength];
//            randomAccessFile.seek(message.getStartPos());
//            randomAccessFile.read(bytes);
//            message.setMessageBody(bytes);
//            // 判断是否已经读完
//            if (message.getStartPos() + readLength >= length) {
//                message.setStatusEnum(StatusEnum.FINISHED);
//            } else {
//                message.setStatusEnum(StatusEnum.LOADINF);
//            }
//            randomAccessFile.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

//    public static void writeFile(UploadMessage message, String destPath) {
//        if (message.getRelativePath() == null || message.getStartPos() == null) {
//            throw new InvalidArgumentsException("message has no file path");
//        }
//
//        // 写文件
//        try {
//            RandomAccessFile randomAccessFile = new RandomAccessFile(destPath, "rw");
//            if (message.getMessageBody() != null && message.getMessageBody().length > 0) {
//                randomAccessFile.seek(message.getStartPos());
//                randomAccessFile.write(message.getMessageBody());
//            }
//            randomAccessFile.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public static File sourceMapToTarget(File sourceFile, String sourcePath, String targetPath) {
        String targetFile = sourceFile.getPath().replace(sourcePath, targetPath);
        log.info("{} =====> {}", sourceFile, targetFile);
        return new File(targetFile);
    }

    /**
     * 读文件
     *
     * @param absolutePath
     * @param startPos
     * @param maxReadLength
     * @return
     */
    public static byte[] readFile(String absolutePath, long startPos, long maxReadLength) throws IOException {
        // 读文件
        RandomAccessFile randomAccessFile = new RandomAccessFile(absolutePath, "r");
        long length = randomAccessFile.length();
        // 剩余长度
        long left = length - startPos;
        // 数据读完
        if (left <= 0) {
            randomAccessFile.close();
            return new byte[0];
        }
        int readLength = (int) Math.min(maxReadLength, left);
        byte[] bytes = new byte[readLength];
        randomAccessFile.seek(startPos);
        randomAccessFile.read(bytes);
        randomAccessFile.close();
        return bytes;
    }

    public static void writeFile(String absolutePath, Long startPos, byte[] bytes) throws IOException {
        if (absolutePath == null || startPos == null || bytes == null) {
            throw new InvalidArgumentsException("写文件，非法参数");
        }
        // 写文件
        RandomAccessFile randomAccessFile = new RandomAccessFile(absolutePath, "rw");
        randomAccessFile.seek(startPos);
        randomAccessFile.write(bytes);
        randomAccessFile.close();
    }
}
