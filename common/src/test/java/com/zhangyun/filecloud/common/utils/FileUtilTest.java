package com.zhangyun.filecloud.common.utils;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class FileUtilTest {
    @Test
    public void test() throws IOException {
        File file = new File("/user/zy");
        System.out.println(file.isDirectory());
    }
}