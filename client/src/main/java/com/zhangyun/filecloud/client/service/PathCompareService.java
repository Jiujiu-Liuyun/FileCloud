package com.zhangyun.filecloud.client.service;

import com.zhangyun.filecloud.client.filevisitor.PathCompareVisitor;
import com.zhangyun.filecloud.common.exception.StatusException;
import com.zhangyun.filecloud.common.message.CompareMessage;
import com.zhangyun.filecloud.common.message.CompareResponseMessage;
import io.netty.channel.Channel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/10/27 20:38
 * @since: 1.0
 */
@Slf4j
@Component
@Data
public class PathCompareService {
    @Value("${file.client.path}")
    private String clientPath;
    @Autowired
    private PathCompareVisitor visitor;
    @Autowired
    private NettyClient nettyClient;

    private AtomicBoolean isComparing = new AtomicBoolean(false); // 是否处于上一次比较中
    private List<String> compareResultList;
    private Semaphore semaphore;

    public List<String> pathCompare() throws IOException, InterruptedException {
        if (!isComparing.compareAndSet(false, true)) {
            throw new StatusException("请等待上一次比较完成！");
        }
        // visit directory
        visitor.setClientInfoList(new LinkedList<>());
        Files.walkFileTree(Paths.get(clientPath), visitor);
        // 建立连接
        Channel channel = nettyClient.getChannel();
        semaphore = new Semaphore(0);
        compareResultList = new ArrayList<>();
        while (!visitor.getClientInfoList().isEmpty()) {
            // 传送文件
            CompareMessage compareMessage = new CompareMessage();
            if (visitor.getClientInfoList().peekFirst() != null) {
                compareMessage.setFilePath(visitor.getClientInfoList().peekFirst().getFilePath());
            } else {
                throw new StatusException("clientInfoList is empty");
            }
            channel.writeAndFlush(compareMessage);
            // 等待该文件传送完毕
            semaphore.acquire();
        }
        channel.close().sync();
        log.info("compare message消息已全部处理，连接关闭！");

        isComparing.set(false);
        return compareResultList;
    }


}
