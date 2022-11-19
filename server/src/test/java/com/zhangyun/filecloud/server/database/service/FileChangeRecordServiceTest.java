package com.zhangyun.filecloud.server.database.service;

import com.zhangyun.filecloud.server.database.entity.FileChangeRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FileChangeRecordServiceTest {
    @Autowired
    private FileChangeRecordService fileChangeRecordService;

    @Test
    void testSelectByDeviceId() {
        List<FileChangeRecord> device = fileChangeRecordService.selectByDeviceId("device", 1);
        device.forEach(System.out::println);
    }

    @Test
    void testTransaction() {
//        System.out.println(fileChangeRecordService.deleteById(1));
    }
}