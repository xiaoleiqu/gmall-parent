package com.atguigu.starter.cache.service.impl;

import com.atguigu.starter.cache.constant.SysRedisConst;
import com.atguigu.starter.cache.service.CacheOpsService;
import com.atguigu.starter.cache.utils.Jsons;
import com.fasterxml.jackson.core.type.TypeReference;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author quxiaolei
 * @date 2022/9/1 - 20:19
 * 封装了缓存操作
 */
@Service
public class CacheOpsServiceImpl implements CacheOpsService {


    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedissonClient redissonClient;

    // 专门执行延迟任务的线程池
    ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(4);

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
     * 从缓存中获取一个数据，并转成指定带泛型的返回值类型；
     *
     * @param cacheKey
     * @param type
     * @return
     */
    @Override
    public Object getCacheData(String cacheKey, Type type) {
        String jsonStr = redisTemplate.opsForValue().get(cacheKey);

        // 引入null值缓存机制
        if (SysRedisConst.NULL_VAL.equals(jsonStr)) {
            return null;
        }

        // 逆转JSON为Type类型的复杂对象
        Object obj = Jsons.toObj(jsonStr, new TypeReference<Object>() {
            public Type getType() {
                return type; // 这个是方法的带泛型的返回值类型；
            }
        });
        return obj;
    }

    /**
     * 延迟双删
     *
     * @param cacheKey
     */
    @Override
    public void delay2Delete(String cacheKey) {
        redisTemplate.delete(cacheKey);

        // 1.提交一个延迟任务。(断电失效，结合后台管理系统，专门准备清空缓存的按钮功能)
        // 2.分布式池框架。Redisson.
        scheduledExecutor.schedule(() -> {
            redisTemplate.delete(cacheKey);
        }, 5, TimeUnit.SECONDS);
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
     * * 判定指定布隆过滤器（bloomName） 是否 包含 指定值（bVal）
     *
     * @param bloomName
     * @param bVal
     * @return
     */
    @Override
    public boolean bloomContains(String bloomName, Object bVal) {
        RBloomFilter<Object> filter = redissonClient.getBloomFilter(bloomName);
        return filter.contains(bVal);
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
     * 加指定的分布式锁
     *
     * @param lockName
     * @return
     */
    @Override
    public boolean tryLock(String lockName) {
        RLock lock = redissonClient.getLock(lockName);
        return lock.tryLock();
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

    @Override
    public void saveData(String cacheKey, Object fromRpc, Long dataTtl) {
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
                    dataTtl,
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

    /**
     * 解指定的锁
     *
     * @param lockName
     */
    @Override
    public void unlock(String lockName) {
        RLock lock = redissonClient.getLock(lockName);
        lock.unlock(); //redisson已经防止了删别人锁
    }

}
