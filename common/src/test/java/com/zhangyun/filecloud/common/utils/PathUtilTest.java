package com.zhangyun.filecloud.common.utils;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

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

    @Test
    public void testChangePathName() {
        Path path = Paths.get("/user/zy/test");
        Path fileName = path.getFileName();
        System.out.println(fileName);
        Path resolve = path.getParent().resolve("file");
        System.out.println(resolve);
    }
}