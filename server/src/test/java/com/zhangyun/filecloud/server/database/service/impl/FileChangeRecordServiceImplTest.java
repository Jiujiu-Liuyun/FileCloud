package com.zhangyun.filecloud.server.database.service.impl;

import com.zhangyun.filecloud.server.database.mapper.FileChangeRecordMapper;
import com.zhangyun.filecloud.server.database.service.FileChangeRecordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.Serializable;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FileChangeRecordServiceImplTest {
    @Autowired
    private FileChangeRecordMapper fileChangeRecordMapper;

    @Test
    public void testDeleteBatchIds() {
        ArrayList<Serializable> ids = new ArrayList<>();
        ids.add(0);
        fileChangeRecordMapper.deleteBatchIds(ids);
    }
}