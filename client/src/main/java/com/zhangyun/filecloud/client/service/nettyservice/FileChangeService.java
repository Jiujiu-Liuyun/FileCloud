package com.zhangyun.filecloud.client.service.nettyservice;

import com.zhangyun.filecloud.client.service.NettyClient;
import com.zhangyun.filecloud.common.entity.FileChangeBO;
import com.zhangyun.filecloud.common.message.FileChangeMsg;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/11/14 22:43
 * @since: 1.0
 */
@Slf4j
@Service
public class FileChangeService {
    @Resource(name = "threadPoolExecutor")
    private ThreadPoolExecutor threadPoolExecutor;
    @Autowired
    private NettyClient nettyClient;

    /**
     * 发送FileChangeMessage到服务器
     *
     * @param fileChangeBO
     */
    public void sendFileChangeMessage(FileChangeBO fileChangeBO) {
        // 建立连接
        Channel channel = nettyClient.getChannel();
        // 构造message
        FileChangeMsg fileChangeMessage = new FileChangeMsg();
        fileChangeMessage.setFileChangeBO(fileChangeBO);
        // send message
        channel.writeAndFlush(fileChangeMessage);
    }
}
