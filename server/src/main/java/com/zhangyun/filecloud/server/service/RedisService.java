package com.zhangyun.filecloud.server.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

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

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * token认证并对token过期时间重新设置
     * @param token
     * @param username
     * @return
     */
    public boolean authTokenAndUpdateExpireTime(String token, String username) {
        String key = TOKEN_PREFIX + token;
        String value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return false;
        }
        if (value.equals(username)) {
            // 认证通过，更新过期时间
            redisTemplate.expire(key, TOKEN_EXPIRE_DAY, TimeUnit.DAYS);
            return true;
        }
        return false;
    }

    public void setToken(String token, String username) {
        String key = TOKEN_PREFIX + token;
        redisTemplate.opsForValue().set(key, username, TOKEN_EXPIRE_DAY, TimeUnit.DAYS);
    }

}
