package com.zhangyun.filecloud.client;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/10/27 23:11
 * @since: 1.0
 */
public class CommonTest {
    @Test
    public void testNull() {
        System.out.println(" " + null);
    }
    
    @Test
    public void getOSType() {
        System.out.println(System.getProperty("os.name").toLowerCase());
    }

    @Test
    public void getCurDir() {
        System.out.println(System.getProperty("user.dir"));
    }

    @Test
    public void getResource() {
        System.out.println(getClass());
    }

    @Test
    public void createFile() throws IOException {

        Path filePath = Paths.get("/Users/zhangyun/test/", "/test/file");
        System.out.println(Files.createFile(filePath));
    }

}
