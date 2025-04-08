package com.Heypon.manager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedisLimiterManagerTest {

    @Resource
    private RedisLimiterManager redisLimiterManager;

    @Test
    void doRedisLimit() throws InterruptedException {
        String key = "1";
        for (int i = 0; i < 2; i++) {
            redisLimiterManager.doRedisLimit(key);
            System.out.println("成功");
        }
        Thread.sleep(1000);
        for (int i = 0; i < 10; i++) {
            redisLimiterManager.doRedisLimit(key);
            System.out.println("成功");
        }
    }
}