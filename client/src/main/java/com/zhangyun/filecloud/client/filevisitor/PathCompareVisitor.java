package com.zhangyun.filecloud.client.filevisitor;

import cn.hutool.crypto.digest.DigestUtil;
import com.zhangyun.filecloud.client.service.PathCompareService;
import com.zhangyun.filecloud.common.annotation.TraceLog;
import com.zhangyun.filecloud.common.message.CompareMessage;
import com.zhangyun.filecloud.common.message.CompareResponseMessage;
import io.netty.channel.Channel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/10/27 23:56
 * @since: 1.0
 */
@Slf4j
@Data
@Component
public class PathCompareVisitor extends SimpleFileVisitor<Path> {
    private Deque<CompareResponseMessage> clientInfoList;

    @Override
    @TraceLog
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        CompareResponseMessage clientInfo = new CompareResponseMessage();
        clientInfo.setFilePath(dir.toString());
        clientInfo.setType(CompareResponseMessage.DIRECTORY);
        clientInfo.setFileList(new File(dir.toUri()).list());
        clientInfoList.add(clientInfo);
        return super.preVisitDirectory(dir, attrs);
    }

    @Override
    @TraceLog
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (file.endsWith(".DS_Store")) {
            return super.visitFile(file, attrs);
        }
        // 客户端信息
        File sourceFile = new File(file.toUri());
        CompareResponseMessage clientInfo = new CompareResponseMessage();
        clientInfo.setFilePath(file.toString());
        clientInfo.setType(CompareResponseMessage.FILE);
        clientInfo.setMd5(DigestUtil.md5Hex(sourceFile));
        clientInfo.setLastModified(sourceFile.lastModified());
        clientInfoList.add(clientInfo);
        return super.visitFile(file, attrs);
    }

}
