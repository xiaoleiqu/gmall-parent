package com.atguigu.gmall.feign.cart;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author quxiaolei
 * @date 2022/9/7 - 20:03
 */
@RequestMapping("/api/inner/rpc/cart")
@FeignClient("service-cart")
public interface CartFeignClient {

    /**
     * 把商品添加到购物车
     *
     * @param skuId
     * @param skuNum
     * @return
     */
    @GetMapping("addToCart")
    public Result<SkuInfo> addToCart(@RequestParam("skuId") Long skuId,
                                     @RequestParam("skuNum") Integer skuNum);

    /**
     * 删除购物车中选中的商品
     * @return
     */
    @GetMapping("/deleteChecked")
    Result deleteChecked();

}
