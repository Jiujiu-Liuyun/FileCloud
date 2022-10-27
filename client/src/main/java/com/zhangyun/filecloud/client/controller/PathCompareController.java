package com.zhangyun.filecloud.client.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/compare")
    public String compare() {
        String res = "compare result:";
        return res;
    }
}
