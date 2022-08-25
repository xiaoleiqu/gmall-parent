package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.service.SpuImageService;
import com.atguigu.gmall.product.service.SpuInfoService;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author quxiaolei
 * @date 2022/8/25 - 22:48
 */
@Api(tags = "SPU-Api")
@RequestMapping("/admin/product/")
@RestController
public class SpuController {

    @Autowired
    SpuInfoService spuInfoService;

    @Autowired
    SpuImageService spuImageService;


    @ApiOperation("分页查询spu列表")
    @GetMapping("{pageNum}/{pageSize}")
    public Result getSpuPage(
            @PathVariable("pageNum") Long pageNum,
            @PathVariable("pageSize") Long pageSize,
            @RequestParam("category3Id") Long category3Id) {

        Page<SpuInfo> page = new Page<>(pageNum, pageSize);

        // 分页查询
        LambdaQueryWrapper<SpuInfo> spuInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        spuInfoLambdaQueryWrapper.eq(SpuInfo::getCategory3Id, category3Id);

        Page<SpuInfo> spuInfoPage = spuInfoService.page(page, spuInfoLambdaQueryWrapper);

        return Result.ok(spuInfoPage);
    }


    // http://192.168.6.1/admin/product/saveSpuInfo
    @ApiOperation("spu信息大保存")
    @PostMapping("saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfo spuInfo) {

        spuInfoService.spuInfoService(spuInfo);

        return Result.ok();
    }

    @ApiOperation("根据spuId获取图片列表")
    @GetMapping("spuImageList/{spuId}")
    public Result getSpuImageListBySpuId(@PathVariable("spuId") Long spuId ){

        List<SpuImage> spuImages = spuImageService.list(new LambdaQueryWrapper<SpuImage>().eq(SpuImage::getSpuId, spuId));

        return Result.ok(spuImages);
    }


}
