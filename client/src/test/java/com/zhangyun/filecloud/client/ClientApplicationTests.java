package com.zhangyun.filecloud.client;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@RunWith(SpringRunner.class)
@SpringBootTest
class ClientApplicationTests {

    @Value("${file.client.path}")
    private String rootPath;

    @Test
    void testDir() {
        Path path = Paths.get(rootPath);
        File file = new File(rootPath);
        System.out.println(file.isDirectory());
    }

}
