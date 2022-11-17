package com.zhangyun.filecloud.client.service.monitor;

import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * description: 定义文件监视器，可用来实现热更配置文件/监听文件场景
 *
 * @author: zhangyun
 * @date: 2022/7/22 00:55
 * @since: 1.0
 */
@Service
@Slf4j
public class FileMonitorService {
    private FileAlterationMonitor monitor;

    @Autowired
    private FileAlterationListenerAdaptor listener;    // 事件处理类对象

    /***
     * 创建文件监听器
     */
    public FileAlterationMonitor createMonitor(String rootPath, int interval) {
        if (ObjectUtil.isEmpty(rootPath)) {
            throw new IllegalArgumentException("Listen path must not be blank");
        }
        if (!new File(rootPath).isDirectory()) {
            throw new IllegalArgumentException(rootPath + " 不是用户根目录");
        }
        if (ObjectUtil.isEmpty(listener)) {
            throw new IllegalArgumentException("Listener must not be null");
        }

        // 设定观察者，监听文件
        FileAlterationObserver observer = new FileAlterationObserver(rootPath);

        // 给观察者添加监听事件
        observer.addListener(listener);

        // 开启一个监视器，监听频率是interval
        // FileAlterationMonitor本身实现了 Runnable，是单独的一个线程，按照设定的时间间隔运行
        FileAlterationMonitor monitor = new FileAlterationMonitor(interval);
        monitor.addObserver(observer);

        return monitor;
    }

    public void closeMonitor() {
        try {
            monitor.stop();
        } catch (Exception e) {
            log.warn("monitor关闭失败 {}", e.getMessage());
        }
        log.info("停止文件监听器 {}", monitor);
    }

    public void startMonitor(String rootPath, int interval) {
        // 创建monitor
        monitor = createMonitor(rootPath, interval);
        try {
            this.monitor.start();
        } catch (Exception e) {
            log.warn("monitor启动失败 {}", e.getMessage());
        }
        log.info("启动文件监听器 {}", this.monitor);
    }
}
