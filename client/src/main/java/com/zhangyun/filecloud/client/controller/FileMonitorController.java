package com.zhangyun.filecloud.client.controller;

import com.zhangyun.filecloud.client.monitor.ClientFileMonitor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/10/28 13:31
 * @since: 1.0
 */
@RestController
@RequestMapping("/fileMonitor")
@Slf4j
public class FileMonitorController {
    @Autowired
    private ClientFileMonitor clientFileMonitor;

    @GetMapping("stopMonitor")
    public String stopMonitor() throws Exception {
        clientFileMonitor.getMonitor().stop();
        log.info("File Monitor停止");
        return "stop";
    }

    @GetMapping("startMonitor")
    public String startMonitor() throws Exception {
        clientFileMonitor.getMonitor().start();
        log.info("File Monitor启动");
        return "stop";
    }
}
