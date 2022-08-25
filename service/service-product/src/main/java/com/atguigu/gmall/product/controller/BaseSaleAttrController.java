package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseSaleAttr;
import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.service.BaseSaleAttrService;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author quxiaolei
 * @date 2022/8/25 - 23:32
 */
@Api(tags = "销售属性相关接口")
@RequestMapping("/admin/product/")
@RestController
public class BaseSaleAttrController {

    @Autowired
    BaseSaleAttrService baseSaleAttrService;

    @Autowired
    SpuSaleAttrService spuSaleAttrService;


    // baseSaleAttrList
    @ApiOperation("获取所有销售属性")
    @GetMapping("baseSaleAttrList")
    public Result getBaseSaleAttrList() {

        List<BaseSaleAttr> list = baseSaleAttrService.list();

        return Result.ok(list);
    }

    @ApiOperation("根据spuId查询出指定spu当时定义的所有销售属性的名和值")
    @GetMapping("spuSaleAttrList/{spuId}")
    public Result getSpuSaleAttrListBySpuId(@PathVariable("spuId") Long spuId){
        List<SpuSaleAttr> saleAttrList = spuSaleAttrService.getSaleAttrAndValueBySpuId(spuId);
        return Result.ok(saleAttrList);
    }



}
