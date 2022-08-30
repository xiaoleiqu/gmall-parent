package com.atguigu.gmall.item.cache;

import com.atguigu.gmall.model.to.SkuDetailTo;

/**
 * @author quxiaolei
 * @date 2022/8/30 - 21:04
 */
public interface CacheOpsService {
    /**
     * 查询缓存
     * @param cacheKey
     * @param clz
     * @param <T>
     * @return
     */
    <T> T getCacheData(String cacheKey, Class<T> clz);

    /**
     * 布隆过滤器判断是否有这个商品
     * @param skuId
     * @return
     */
    boolean bloomContains(Long skuId);

    /**
     * 给指定商品加锁
     * @param skuId
     * @return
     */
    boolean tryLock(Long skuId);

    /**
     * 把指定对象使用指定的key保存到redis
     * @param cacheKey
     * @param fromRpc
     */
    void saveData(String cacheKey, Object fromRpc);

    /**
     * 解锁
     * @param skuId
     */
    void unlock(Long skuId);
}
