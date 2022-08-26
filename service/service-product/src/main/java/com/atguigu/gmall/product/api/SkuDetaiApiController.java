package com.atguigu.gmall.product.api;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.to.SkuDetailTo;
import com.atguigu.gmall.product.service.SkuInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author quxiaolei
 * @date 2022/8/26 - 22:53
 */
@RestController
@RequestMapping("/api/inner/rpc/product")
public class SkuDetaiApiController {

    @Autowired
    SkuInfoService skuInfoService;


    @GetMapping("/skudetail/{skuId}")
    public Result<SkuDetailTo> getSkuDetail(@PathVariable("skuId") Long skuId) {

        // 商品详情大查询
        SkuDetailTo skuDetailTo = skuInfoService.getSkuDetail(skuId);

        return Result.ok(skuDetailTo);
    }
}
