package com.zhangyun.filecloud.common.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileUtilTest {

    @Test
    void testGetProperty() {
        System.out.println(FileUtil.getProperty("/Users/zhangyun/Documents/javaProject/FileCloud/etc/zhangyun", "username"));
    }
}