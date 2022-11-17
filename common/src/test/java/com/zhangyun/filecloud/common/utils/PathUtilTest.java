package com.zhangyun.filecloud.common.utils;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class PathUtilTest {

    @Test
    void testGetAbsolutePath() {
        Path test = PathUtil.getAbsolutePath("test", "/user/zy");
        System.out.println(test);
    }

    @Test
    void testGetRelativePath() {
        Path relativePath = PathUtil.getRelativePath("/user/zy/test", "/user/zy/");
        System.out.println(relativePath);
    }
}