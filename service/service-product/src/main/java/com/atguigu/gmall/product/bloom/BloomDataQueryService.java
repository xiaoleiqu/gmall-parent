package com.atguigu.gmall.product.bloom;

import java.util.List;

/**
 * @author quxiaolei
 * @date 2022/9/1 - 18:44
 */
public interface BloomDataQueryService {

    /**
     * 父接口规则好算法，查询所有商品详情的key值，保存到布隆过滤器中
     *
     * @return
     */
    List queryData();

}
