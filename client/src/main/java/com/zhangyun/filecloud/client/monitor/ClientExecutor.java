package com.zhangyun.filecloud.client.monitor;

import org.springframework.stereotype.Component;

import java.util.concurrent.*;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/8/2 00:23
 * @since: 1.0
 */
@Component
public class ClientExecutor extends ThreadPoolExecutor {

    public ClientExecutor() {
        super(4, 8, 60,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(),
                Executors.defaultThreadFactory(),
                new AbortPolicy());
    }

    public ClientExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }
}
