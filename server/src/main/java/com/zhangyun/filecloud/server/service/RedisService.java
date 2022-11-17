package com.zhangyun.filecloud.server.service;

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

    public static final String LOCK_FILE_TRANSFER_PREFIX = "lock-file-transfer:";

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

    public boolean lockFileTransferList(String username) {
        String key = LOCK_FILE_TRANSFER_PREFIX + username;
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, "1", 10, TimeUnit.SECONDS));
    }

    /**
     * 尝试获取锁
     * @param username
     * @return
     */
    public boolean tryLockFileTransferList(String username) {
        boolean lock;
        boolean flag = true;
        long begin = System.currentTimeMillis();

        do {
            lock = this.lockFileTransferList(username);
            if (!lock) {
                try {
                    //休眠0.1秒后重试，直到重试超时getTimeOut
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                long endTime = System.currentTimeMillis();
                if (endTime - begin > (3 * 1000)) {
                    log.warn("获取锁超时，锁标识：" + username);
                    flag = false;//退出循环
                }
            } else {
                flag = false;//退出循环
            }
        } while (flag);
        return lock;
    }
}
