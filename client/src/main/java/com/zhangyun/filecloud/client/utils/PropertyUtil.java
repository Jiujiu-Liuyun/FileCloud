package com.zhangyun.filecloud.client.utils;

import com.zhangyun.filecloud.client.config.ClientConfig;
import com.zhangyun.filecloud.common.annotation.TraceLog;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/11/7 12:01
 * @since: 1.0
 */
@Slf4j
public class PropertyUtil {
    public static Path getPropertyPath(String username) {
        Path dirPath = ClientConfig.SETTING_PATH;
        return Paths.get(dirPath.toString(), username);
    }

    public static void setProperty(String username, String key, String value) throws IOException {
        if (key == null || username == null || value == null) {
            return;
        }
        Path propertyPath = getPropertyPath(username);
        Properties properties = getProperties(propertyPath.toString());
        properties.put(key, value);
        log.info("add property, key: {}, value: {}", key, value);
        BufferedOutputStream bos = new BufferedOutputStream(
                new FileOutputStream(propertyPath.toString()));
        properties.store(bos, "");
    }

    @TraceLog
    public static String getProperty(String username, String key) {
        if (key == null || username == null) {
            return null;
        }
        Path propertyPath = getPropertyPath(username);
        Properties properties = getProperties(propertyPath.toString());
        return properties.getProperty(key);
    }

    private static Properties getProperties(String filePath) {
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath);
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(inputStream !=null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return properties;
    }
}
