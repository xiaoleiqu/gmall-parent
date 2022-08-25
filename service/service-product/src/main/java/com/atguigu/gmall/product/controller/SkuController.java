package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author quxiaolei
 * @date 2022/8/26 - 0:21
 */
@Api(tags = "SKU-Api")
@RequestMapping("/admin/product/")
@RestController
public class SkuController {

    @Autowired
    SkuInfoService skuInfoService;

    @ApiOperation("分页查询sku列表")
    @GetMapping("/list/{pageNum}/{pageSize}")
    public Result getSkuPage(
            @PathVariable("pageNum") Long pageNum,
            @PathVariable("pageSize") Long pageSize) {

        Page<SkuInfo> page = new Page<>(pageNum, pageSize);

        Page<SkuInfo> skuInfoPage = skuInfoService.page(page);

        return Result.ok(skuInfoPage);
    }

    @ApiOperation("SKU数据大保存")
    @PostMapping("saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuInfo skuInfo) {

        skuInfoService.saveSkuInfo(skuInfo);

        return Result.ok();
    }

    @ApiOperation("商品上架")
    @GetMapping("/onSale/{skuId}")
    public Result onSale(@PathVariable("skuId") Long skuId){
        skuInfoService.onSale(skuId);
        return Result.ok();
    }


    @ApiOperation("商品下架")
    @GetMapping("/cancelSale/{skuId}")
    public Result cancelSale(@PathVariable("skuId") Long skuId){
        skuInfoService.cancelSale(skuId);
        return Result.ok();
    }


}
