package com.zhangyun.filecloud.client.service.nettyservice;

import com.zhangyun.filecloud.client.config.ClientConfig;
import com.zhangyun.filecloud.common.message.Msg;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/11/20 10:53
 * @since: 1.0
 */
@Slf4j
public abstract class AbstractNettyService<T extends Msg> {
    /**
     * 标识是否接收到来自服务器的登录响应消息
     */
    private Semaphore semaphore = new Semaphore(0);
    /**
     * 存储netty传来的数据
     */
    private T data;

    public void dataIsReady() {
        semaphore.release();
    }

    public void setData(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    /**
     * 是否在TIMEOUT时间内返回
     */
    public T waitForData() {
        // init
        semaphore = new Semaphore(0);
        data = null;
        // 等待响应
        try {
            boolean acquire = semaphore.tryAcquire(ClientConfig.NETTY_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (!acquire) {
                data = null;
            }
        } catch (InterruptedException e) {
            log.info("netty 等待超时");
            data = null;
        }
        return data;
    }
}
