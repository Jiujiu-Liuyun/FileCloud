package com.zhangyun.filecloud.client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * description: 设置配置文件路径
 *
 * @author: zhangyun
 * @date: 2022/11/3 19:55
 * @since: 1.0
 */
@Configuration
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

}
