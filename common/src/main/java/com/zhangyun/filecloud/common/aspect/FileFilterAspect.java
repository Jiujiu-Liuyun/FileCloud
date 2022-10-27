package com.zhangyun.filecloud.common.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/7/24 19:20
 * @since: 1.0
 */
@Aspect
@Component
@Slf4j
@Order(10)
public class FileFilterAspect {
    // 忽略列表
    public static List<String> FILE_IGNORE_LIST = new ArrayList<>();
    static {
        FILE_IGNORE_LIST.add(".DS_Store");
    }

    @Pointcut("@annotation(com.zhangyun.filecloud.common.annotation.FileFilter)")
    public void pointcut() {
    }

    @Around("pointcut() && args(source)")
    public void fileFilter(ProceedingJoinPoint jp, File source) throws Throwable {
        // 文件名在忽略列表中，直接返回
        if (FILE_IGNORE_LIST.contains(source.getName())) {
            return;
        }

        // 执行方法
        jp.proceed();
    }

}
