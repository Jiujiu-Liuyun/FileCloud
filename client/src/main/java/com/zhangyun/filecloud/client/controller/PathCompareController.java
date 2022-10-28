package com.zhangyun.filecloud.client.controller;

import com.zhangyun.filecloud.client.service.PathCompareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/10/27 20:34
 * @since: 1.0
 */
@RestController
@RequestMapping("/pathCompare")
public class PathCompareController {
    @Autowired
    private PathCompareService pathCompareService;

    @GetMapping("/compare")
    public List<String> compare() throws IOException, InterruptedException {
        return pathCompareService.pathCompare();
    }
}
