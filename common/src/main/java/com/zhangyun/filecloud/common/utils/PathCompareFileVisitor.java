package com.zhangyun.filecloud.common.utils;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.digest.DigestUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/10/26 16:43
 * @since: 1.0
 */
@Slf4j
@Data
public class PathCompareFileVisitor extends SimpleFileVisitor<Path> {
    private String sourcePath;
    private String targetPath;

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        File sourceDirectory = new File(dir.toUri());
        File targetDirectory = FileUtil.sourceMapToTarget(sourceDirectory, sourcePath, targetPath);
        if (!targetDirectory.exists() || targetDirectory.isFile()) {
            log.warn("{} <=====> {}", sourceDirectory, targetDirectory);
            return super.preVisitDirectory(dir, attrs);
        }
        // 比较文件目录 - 将source目录下与target目录对应的文件删除
        // 剩下的target就是source中不存在的文件
        String[] sourceFileList = sourceDirectory.list();
        List<String> targetFileList = Arrays.stream(targetDirectory.list())
                .filter(targetFile -> {
                    for (String sourceFile : sourceFileList) {
                        if (ObjectUtil.equal(targetFile, sourceFile)) {
                            return false;
                        }
                    }
                    return true;
                }).collect(Collectors.toList());
        // 打印target中存在但source中不存在的文件或目录
        if (ObjectUtil.isNotEmpty(targetFileList)) {
            log.warn("target extra: {}", targetFileList);
        }
        return super.preVisitDirectory(dir, attrs);
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (file.endsWith(".DS_Store")) {
            return super.visitFile(file, attrs);
        }
        File sourceFile = new File(file.toUri());
        File targetFile = FileUtil.sourceMapToTarget(sourceFile, sourcePath, targetPath);
        if (!targetFile.exists() || targetFile.isDirectory()
                || ObjectUtil.notEqual(DigestUtil.md5Hex(sourceFile), DigestUtil.md5Hex(targetFile))) {
            log.warn("{} <=====> {}", sourceFile, targetFile);
        }
        return super.visitFile(file, attrs);
    }

    public PathCompareFileVisitor(String sourcePath, String targetPath) {
        this.sourcePath = sourcePath;
        this.targetPath = targetPath;
    }
}
