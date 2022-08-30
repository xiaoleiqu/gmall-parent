package com.atguigu.gmall.item.cache.impl;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.item.cache.CacheOpsService;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author quxiaolei
 * @date 2022/8/30 - 21:05
 */
@Service
public class CacheOpsServiceImpl implements CacheOpsService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedissonClient redissonClient;

    /**
     * 从缓存中获取一个数据，并转成指定类型的对象
     *
     * @param cacheKey
     * @param clz
     * @param <T>
     * @return
     */
    @Override
    public <T> T getCacheData(String cacheKey, Class<T> clz) {
        String jsonStr = redisTemplate.opsForValue().get(cacheKey);

        // 引入null值缓存机制
        if (SysRedisConst.NULL_VAL.equals(jsonStr)) {
            return null;
        }
        T t = Jsons.toObj(jsonStr, clz);
        return t;
    }

    /**
     * 布隆过滤器判断是否有这个商品
     *
     * @param skuId
     * @return
     */
    @Override
    public boolean bloomContains(Long skuId) {
        RBloomFilter<Object> filter = redissonClient.getBloomFilter(SysRedisConst.BLOOM_SKUID);
        return filter.contains(skuId);
    }

    /**
     * 给指定商品加锁
     *
     * @param skuId
     * @return
     */
    @Override
    public boolean tryLock(Long skuId) {
        // 1.准备锁用的key
        String lockKey = SysRedisConst.LOCK_SKU_DETAIL + skuId;
        RLock lock = redissonClient.getLock(lockKey);
        // 2.尝试加锁
        boolean tryLock = lock.tryLock();
        return tryLock;
    }

    /**
     * 把指定对象使用指定的key保存到redis
     *
     * @param cacheKey
     * @param fromRpc
     */
    @Override
    public void saveData(String cacheKey, Object fromRpc) {
        if (fromRpc == null) {
            // null值缓存短一点时间
            redisTemplate.opsForValue().set(cacheKey,
                    SysRedisConst.NULL_VAL,
                    SysRedisConst.NULL_VAL_TTL,
                    TimeUnit.SECONDS);
        } else {
            String str = Jsons.toStr(fromRpc);
            redisTemplate.opsForValue().set(cacheKey,
                    str,
                    SysRedisConst.SKUDETAIL_TTL,
                    TimeUnit.SECONDS);
        }

    }

    /**
     * 解锁
     *
     * @param skuId
     */
    @Override
    public void unlock(Long skuId) {
        String lockKey = SysRedisConst.LOCK_SKU_DETAIL + skuId;
        RLock lock = redissonClient.getLock(lockKey);

        // 解锁
        lock.unlock();
    }
}
