package com.zhangyun.filecloud.client.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.*;

/**
 * description: 设置配置文件路径
 *
 * @author: zhangyun
 * @date: 2022/11/3 19:55
 * @since: 1.0
 */
@Configuration
@Slf4j
public class ClientConfig {
    public static Path SETTING_PATH;
    static {
        SETTING_PATH = Paths.get(System.getProperty("user.dir") + "/etc/");
    }
    public static final Integer NETTY_TIMEOUT_SECONDS = 5;

    /**
     * 线程池：执行定时任务
     * @return
     */
    @Bean("scheduledExecutorService")
    public ScheduledExecutorService getExecutorService() {
        return Executors.newScheduledThreadPool(2);
    }

    @Bean("threadPoolExecutor")
    public ThreadPoolExecutor getThreadPoolExecutor() {
        return new ThreadPoolExecutor(3, 8, 60, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(10),
                (r, executor) -> log.warn("任务过多，已拒绝新任务 {}", r));
    }
}
