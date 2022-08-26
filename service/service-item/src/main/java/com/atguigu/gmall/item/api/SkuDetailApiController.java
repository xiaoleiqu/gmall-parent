package com.atguigu.gmall.item.api;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.item.service.SkuDetailService;
import com.atguigu.gmall.model.to.SkuDetailTo;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author quxiaolei
 * @date 2022/8/26 - 22:34
 */
@Api(tags = "商品详情")
@RequestMapping("/api/inner/rpc.item")
@RestController
public class SkuDetailApiController {

    @Autowired
    SkuDetailService skuDetailService;

    @GetMapping("/skudetail/{skuId}")
    public Result<SkuDetailTo> getSkuDetail(@PathVariable("skuId") Long skuId) {

        // TODO 商品详细大查询
        SkuDetailTo skuDetailTo = skuDetailService.getSkuDetail(skuId);

        return Result.ok(skuDetailTo);
    }
}
