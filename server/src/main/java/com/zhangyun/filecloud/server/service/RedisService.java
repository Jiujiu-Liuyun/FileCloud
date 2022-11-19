package com.zhangyun.filecloud.server.service;

import com.zhangyun.filecloud.common.annotation.TraceLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * description:
 *
 * @author: zhangyun
 * @date: 2022/11/4 19:21
 * @since: 1.0
 */
@Slf4j
@Service
public class RedisService {
    public static final String TOKEN_PREFIX = "token:";
    public static final Long TOKEN_EXPIRE_DAY = 1L;

    public static final String LOCK_FCR_PREFIX = "lock-FCR:";
    public static final Integer LOCK_FCR_TIMEOUT_SECONDS = 30;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * token认证并对token过期时间重新设置
     *
     * @param token
     * @param username
     * @return
     */
    public boolean authTokenAndUpdateExpireTime(String token, String username, String deviceId) {
        String key = genTokenKey(username, deviceId);
        String value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return false;
        }
        if (value.equals(token)) {
            // 认证通过，更新过期时间
            redisTemplate.expire(key, TOKEN_EXPIRE_DAY, TimeUnit.DAYS);
            return true;
        }
        return false;
    }

    public void setToken(String token, String username, String deviceId) {
        String key = genTokenKey(username, deviceId);
        redisTemplate.opsForValue().set(key, token, TOKEN_EXPIRE_DAY, TimeUnit.DAYS);
    }

    public void delToken(String username, String deviceId) {
        String key = genTokenKey(username, deviceId);
        redisTemplate.delete(key);
    }

    private String genTokenKey(String username, String deviceId) {
        return TOKEN_PREFIX + username + ":" + deviceId;
    }

    public String genToken(String username, String deviceId) {
        // 生成token
        String token = UUID.randomUUID().toString();
        // 将token写入Redis
        setToken(token, username, deviceId);
        return token;
    }

    /**
     * 给设备加锁，每个设备同时只能处理一个FCR
     * @param deviceId
     * @return
     */
    @TraceLog
    public boolean lockForDevice(String deviceId) {
        String key = LOCK_FCR_PREFIX + deviceId;
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, "lock", LOCK_FCR_TIMEOUT_SECONDS, TimeUnit.SECONDS));
    }

    /**
     * 解锁
     * @param deviceId
     * @return
     */
    @TraceLog
    public boolean unlockForDevice(String deviceId) {
        String key = LOCK_FCR_PREFIX + deviceId;
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }
}
