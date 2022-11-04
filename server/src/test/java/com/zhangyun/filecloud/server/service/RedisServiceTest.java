package com.zhangyun.filecloud.server.service;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;


@RunWith(SpringRunner.class)
@SpringBootTest
class RedisServiceTest {
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Test
    public void testSetKey(){
        redisTemplate.opsForValue().set("myKey","myValue");
        System.out.println(redisTemplate.opsForValue().get("key"));     // null
    }

    @Test
    public void setExpire() {
        System.out.println(redisTemplate.expire("myKey", 1L, TimeUnit.DAYS));
    }
}