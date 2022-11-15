package com.zhangyun.filecloud.client.service.executor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/11/14 23:08
 * @since: 1.0
 */
@Slf4j
@Service
public class ThreadPoolService {
    public ExecutorService executor = Executors.newCachedThreadPool();

}
