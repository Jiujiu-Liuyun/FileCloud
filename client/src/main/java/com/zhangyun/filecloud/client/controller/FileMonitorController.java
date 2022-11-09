package com.zhangyun.filecloud.client.controller;

import com.zhangyun.filecloud.client.service.monitor.FileMonitorService;
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
    private FileMonitorService fileMonitorService;

    @GetMapping("stopMonitor")
    public String stopMonitor() throws Exception {
        fileMonitorService.closeMonitor();
        log.info("File Monitor停止");
        return "stop";
    }

    @GetMapping("startMonitor")
    public String startMonitor() throws Exception {
        fileMonitorService.startMonitor();
        log.info("File Monitor启动");
        return "stop";
    }
}
