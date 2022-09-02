package com.atguigu.gmall.product.bloom;

/**
 * @author quxiaolei
 * @date 2022/9/1 - 18:38
 */

public interface BloomOpsService {

    /**
     * 重建指定的布隆过滤器
     *
     * @param bloomName
     * @param bloomDataQueryService
     */
    void rebuildBloom(String bloomName, BloomDataQueryService bloomDataQueryService);
}
