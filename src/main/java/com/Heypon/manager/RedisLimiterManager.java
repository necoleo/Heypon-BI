package com.Heypon.manager;

import com.Heypon.common.ErrorCode;
import com.Heypon.exception.BusinessException;
import com.Heypon.exception.ThrowUtils;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 专门提供 RedisLimiter 限流基础服务（通用）
 */
@Service
public class RedisLimiterManager {

    @Resource
    private RedissonClient redissonClient;

    /**
     *  限流操作
     * @param key   区分不同的限流器，比如不同的用户 id 应该分别统计
     */
    public void doRedisLimit(String key) {

        // 设置限流器的key
        RRateLimiter reteLimiter = redissonClient.getRateLimiter(key);

        // 设置限流规则，每秒允许 2 个请求
        reteLimiter.trySetRate(RateType.OVERALL, 2 ,1 , RateIntervalUnit.SECONDS);

        // 每当一个操作来了后，请求一个令牌
        boolean canOp = reteLimiter.tryAcquire(1);

        if (!canOp){
            throw new BusinessException(ErrorCode.TOO_MANY_REQUEST);
        }

    }

}
