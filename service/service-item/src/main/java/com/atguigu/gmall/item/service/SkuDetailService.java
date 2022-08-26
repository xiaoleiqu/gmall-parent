package com.atguigu.gmall.item.service;

import com.atguigu.gmall.model.to.SkuDetailTo;

/**
 * @author quxiaolei
 * @date 2022/8/26 - 22:40
 */
public interface SkuDetailService {
    /**
     * 商品详情大查询
     *
     * @param skuId
     * @return
     */
    SkuDetailTo getSkuDetail(Long skuId);
}
