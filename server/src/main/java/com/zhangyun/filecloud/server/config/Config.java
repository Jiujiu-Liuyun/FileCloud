package com.zhangyun.filecloud.server.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.concurrent.*;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/10/21 22:17
 * @since: 1.0
 */
@Configuration
@Slf4j
public class Config {
    @Value("${file.server.rootPath}")
    private String rootPath;
    @Value("${file.server.maxReadLength}")
    private String maxReadLength;

    public static String ROOT_PATH;

    public static Long MAX_READ_LENGTH;

    public static Integer TIMEOUT = 5 * 1000;

    @PostConstruct
    private void initConfig() {
        ROOT_PATH = rootPath;
        MAX_READ_LENGTH = Long.valueOf(maxReadLength);
    }

    /**
     * 线程池：执行定时任务
     * @return
     */
    @Bean("scheduledExecutorService")
    public ScheduledExecutorService getExecutorService() {
        return Executors.newScheduledThreadPool(2);
    }

    @Bean("threadPoolExecutor")
    public ThreadPoolExecutor getCachedThreadPool() {
        return new ThreadPoolExecutor(3, 8, 60, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(10),
                (r, executor) -> log.warn("任务过多，已拒绝新任务 {}", r));
    }
}
