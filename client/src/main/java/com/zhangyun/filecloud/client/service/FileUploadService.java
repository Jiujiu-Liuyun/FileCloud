package com.zhangyun.filecloud.client.service;

import com.zhangyun.filecloud.client.monitor.ClientExecutor;
import com.zhangyun.filecloud.common.message.UploadMessage;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Semaphore;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/10/24 15:10
 * @since: 1.0
 */
@Component
@Slf4j
public class FileUploadService implements ApplicationRunner {
    public static ConcurrentLinkedDeque<UploadMessage> FILE_UPLOAD_MESSAGE_LIST
            = new ConcurrentLinkedDeque<>();
    public static Semaphore SEMAPHORE = new Semaphore(0);

    @Autowired
    private ClientExecutor clientExecutor;

    @Autowired
    private NettyClient nettyClient;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        clientExecutor.execute(() -> {
            while (true) {
                try {
                    if (!FILE_UPLOAD_MESSAGE_LIST.isEmpty()) {
                        fileUpload();
                    }
                    // 休眠3s
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void fileUpload() throws InterruptedException {
        // 建立连接
        Channel channel = nettyClient.getChannel();
        while (!FILE_UPLOAD_MESSAGE_LIST.isEmpty()) {
            // 传送文件
            channel.writeAndFlush(FILE_UPLOAD_MESSAGE_LIST.removeFirst());
            // 等待该文件传送完毕
            SEMAPHORE.acquire();
        }
        channel.close().sync();
        log.info("消息已全部处理，连接关闭！");
    }
}
