package com.zhangyun.filecloud.common.utils;

import com.zhangyun.filecloud.common.exception.InvalidArgumentsException;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/10/15 00:45
 * @since: 1.0
 */
@Slf4j
public class FileUtil {
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
        if (absolutePath == null || startPos < 0 || maxReadLength < 0) {
            return new byte[0];
        }
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

    public static boolean writeFile(String absolutePath, Long startPos, byte[] bytes) throws IOException {
        if (absolutePath == null || startPos == null || bytes == null) {
            log.warn("写文件，非法参数");
            return false;
        }
        // 写文件
        RandomAccessFile randomAccessFile = new RandomAccessFile(absolutePath, "rw");
        randomAccessFile.seek(startPos);
        randomAccessFile.write(bytes);
        randomAccessFile.close();
        return true;
    }

    public static boolean deleteDir(Path absolutePath) throws IOException {
        if (!Files.exists(absolutePath) || new File(absolutePath.toString()).isFile()) {
            return false;
        }
        try {
            Files.walkFileTree(absolutePath, new FileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
            return true;
        } catch (Exception e) {
            log.error("delete dir error!", e);
            return false;
        }
    }

    public static void changeDir(Path absolutePath) throws IOException {
        if (!Files.exists(absolutePath)) {
            log.info("dir not exists {}", absolutePath);
            Files.createDirectories(absolutePath);
        }
    }

    public static boolean createDir(Path absolutePath) throws IOException {
        try {
            Files.createDirectories(absolutePath);
            return true;
        } catch (FileAlreadyExistsException e) {
            log.info("file {} already exists", absolutePath);
            // 删除文件
            Files.deleteIfExists(absolutePath);
            // 创建目录
            Files.createDirectories(absolutePath);
            return false;
        }
    }
}
