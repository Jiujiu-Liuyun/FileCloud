package com.zhangyun.filecloud.client.service.nettyservice;

import com.zhangyun.filecloud.client.config.ClientConfig;
import com.zhangyun.filecloud.common.message.LoginRespMsg;
import com.zhangyun.filecloud.common.message.Message;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/11/20 10:53
 * @since: 1.0
 */
@Slf4j
public abstract class AbstractNettyService<T extends Message> {
    /**
     * 标识是否接收到来自服务器的登录响应消息
     */
    private Semaphore semaphore = new Semaphore(0);
    /**
     * 存储netty传来的数据
     */
    private T data;

    public void serviceRefresh() {
        semaphore = new Semaphore(0);
        data = null;
    }

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
