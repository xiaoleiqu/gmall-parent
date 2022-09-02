package com.atguigu.gmall.product.bloom.impl;

import com.atguigu.gmall.product.bloom.BloomDataQueryService;
import com.atguigu.gmall.product.bloom.BloomOpsService;
import com.atguigu.gmall.product.service.SkuInfoService;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author quxiaolei
 * @date 2022/9/1 - 18:38
 */
@Service
public class BloomOpsServiceImpl implements BloomOpsService {

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    SkuInfoService skuInfoService;


    @Override
    public void rebuildBloom(String bloomName, BloomDataQueryService bloomDataQueryService) {

        // 获取旧的布隆过滤器
        RBloomFilter<Object> oldBloomFilter = redissonClient.getBloomFilter(bloomName);

        // 1.先准备一个新的布隆过滤器，所有的东西都初始化好
        String newBloomName = bloomName + "_new";
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter(newBloomName);

        // 2.获取到所有的商品ID(动态获取，传入对应的service，固定queryData()方法，具体数据查什么由实现类决定)
        List list = bloomDataQueryService.queryData();

        // 3.初始化新的布隆
        bloomFilter.tryInit(5000000, 0.00001);

        // 4.添加商品ID到新布隆过滤器中
        for (Object skuId : list) {
            bloomFilter.add(skuId);
        }

        // 5.交换新老布隆过滤器，引入第三方变量(修改将老布过滤器的名字(老布隆下线)，修改布隆过滤器为新布隆)
        oldBloomFilter.rename("abc_bloom"); // 老布隆过滤器下线
        bloomFilter.rename(bloomName); // 新布隆上线

        // 6.删除老布隆和中间交换层
        oldBloomFilter.deleteAsync();
        redissonClient.getBloomFilter("abc_bloom").deleteAsync();


    }
}
