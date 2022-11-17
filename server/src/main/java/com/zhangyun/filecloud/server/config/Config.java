package com.zhangyun.filecloud.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/10/21 22:17
 * @since: 1.0
 */
@Configuration
public class Config {
    @Value("${file.server.rootPath}")
    private String rootPath;
    @Value("${file.server.maxReadLength}")
    private String maxReadLength;

    public static String ROOT_PATH;

    public static Long MAX_READ_LENGTH;

    @PostConstruct
    private void initConfig() {
        ROOT_PATH = rootPath;
        MAX_READ_LENGTH = Long.valueOf(maxReadLength);
    }

    /**
     * 线程池
     * @return
     */
    @Bean
    public ScheduledExecutorService getExecutorService() {
        return Executors.newScheduledThreadPool(2);
    }
}
