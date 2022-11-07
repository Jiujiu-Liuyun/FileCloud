package com.zhangyun.filecloud.client;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Properties;

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

    @Test
    public void isFolder() {
        File file = new File("/Users/zhangyun/test");
        System.out.println(file.isDirectory());
    }

    @Test
    public void testProperty() {
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream("/Users/zhangyun/test/file");
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(inputStream !=null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        String value = properties.getProperty("key");
        System.out.println(value);
    }
}
