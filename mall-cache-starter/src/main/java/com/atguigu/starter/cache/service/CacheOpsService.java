package com.atguigu.starter.cache.service;

import java.lang.reflect.Type;

/**
 * @author quxiaolei
 * @date 2022/9/1 - 20:19
 */
public interface CacheOpsService {

    /**
     * 查询缓存:从缓存中获取一个JSON并转为普通对象
     *
     * @param cacheKey
     * @param clz
     * @param <T>
     * @return
     */
    <T> T getCacheData(String cacheKey, Class<T> clz);

    /**
     * 并转成指定带泛型的返回值类型；
     *
     * @param cacheKey
     * @param type
     * @return
     */
    Object getCacheData(String cacheKey, Type type);

    /**
     * 布隆过滤器判断是否有这个商品
     *
     * @param skuId
     * @return
     */
    boolean bloomContains(Long skuId);

    /**
     * 判定指定布隆过滤器（bloomName） 是否 包含 指定值（bVal）
     * @param bloomName
     * @param bVal
     * @return
     */
    boolean bloomContains(String bloomName, Object bVal);

    /**
     * 给指定商品加锁
     *
     * @param skuId
     * @return
     */
    boolean tryLock(Long skuId);

    /**
     * 加指定的分布式锁
     * @param lockName
     * @return
     */
    boolean tryLock(String lockName);

    /**
     * 把指定对象使用指定的key保存到redis
     *
     * @param cacheKey
     * @param fromRpc
     */
    void saveData(String cacheKey, Object fromRpc);

    /**
     * 解锁
     *
     * @param skuId
     */
    void unlock(Long skuId);

    /**
     * 解指定的锁
     * @param lockName
     */
    void unlock(String lockName);

}
