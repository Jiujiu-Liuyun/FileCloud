package com.zhangyun.filecloud.client.config;

import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * description: 设置配置文件路径
 *
 * @author: zhangyun
 * @date: 2022/11/3 19:55
 * @since: 1.0
 */
@Configuration
public class Config {
    public static Path SETTING_PATH;
    static {
        SETTING_PATH = Paths.get(System.getProperty("user.dir") + "/etc/");
    }

}
