package com.zhangyun.filecloud.client.service.monitor;

import com.zhangyun.filecloud.client.service.NettyClient;
import com.zhangyun.filecloud.client.service.executor.ThreadPoolService;
import com.zhangyun.filecloud.common.entity.FileChangeBO;
import com.zhangyun.filecloud.common.message.FileChangeMessage;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Semaphore;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/11/14 22:43
 * @since: 1.0
 */
@Slf4j
@Service
public class FileChangeService implements ApplicationRunner {
    @Autowired
    private ThreadPoolService threadPoolService;
    @Autowired
    private NettyClient nettyClient;

    private ConcurrentLinkedDeque<FileChangeBO> FILE_CHANGE_LIST = new ConcurrentLinkedDeque<>();
    private Semaphore FILE_CHANGE_LIST_SEMAPHORE = new Semaphore(0);
    private Semaphore FILE_CHANGE_HANDLER_SEMAPHORE = new Semaphore(0);

    public void addFileChangeBO(FileChangeBO fileChangeBO) {
        FILE_CHANGE_LIST.addLast(fileChangeBO);
        FILE_CHANGE_LIST_SEMAPHORE.release();
    }

    /**
     * 发送FileChangeMessage到服务器
     * @param fileChangeBO
     */
    private void sendFileChangeMessage(FileChangeBO fileChangeBO) {
        // 建立连接
        Channel channel = nettyClient.getChannel();
        // 构造message
        FileChangeMessage fileChangeMessage = new FileChangeMessage();
        fileChangeMessage.setFileChangeBO(fileChangeBO);
        // send message
        channel.writeAndFlush(fileChangeMessage);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        threadPoolService.executor.submit(() -> {
            while (true) {
                try {
                    // 获取令牌
                    FILE_CHANGE_LIST_SEMAPHORE.acquire();
                    // 传送数据
                    sendFileChangeMessage(FILE_CHANGE_LIST.removeFirst());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
