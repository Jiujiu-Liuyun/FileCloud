package com.zhangyun.filecloud.server.database.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhangyun.filecloud.server.database.entity.Device;
import com.zhangyun.filecloud.server.database.mapper.DeviceMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DeviceServiceImplTest {

    @Autowired
    private DeviceMapper deviceMapper;

    @Test
    public void testPage() {
        Page<Device> page = new Page<>(1, 2);
        QueryWrapper<Device> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", 1);
        Page<Device> devicePage = deviceMapper.selectPage(page, wrapper);
        devicePage.getRecords().forEach(System.out::println);
    }
}