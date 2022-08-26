package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.atguigu.gmall.model.to.SkuDetailTo;
import com.atguigu.gmall.product.mapper.BaseCategory3Mapper;
import com.atguigu.gmall.product.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.atguigu.gmall.product.mapper.SkuInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author 屈晓磊
 * @description 针对表【sku_info(库存单元表)】的数据库操作Service实现
 * @createDate 2022-08-23 10:13:40
 */
@Service
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoMapper, SkuInfo>
        implements SkuInfoService {

    @Autowired
    SkuInfoMapper skuInfoMapper;

    @Autowired
    SkuInfoService skuInfoService;

    @Autowired
    SkuImageService skuImageService;

    @Autowired
    SkuAttrValueService skuAttrValueService;

    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    BaseCategory3Mapper baseCategory3Mapper;

    @Autowired
    SpuSaleAttrService spuSaleAttrService;

    @Transactional
    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {
        // 1.保存sku基础信息到sku_info表
        skuInfoService.save(skuInfo);
        Long skuInfoId = skuInfo.getId();


        // 2.获取SkuImageList
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        for (SkuImage skuImage : skuImageList) {
            skuImage.setSkuId(skuInfoId);
        }
        skuImageService.saveBatch(skuImageList);

        // 3.sku的平台属性名和值的关系保存到 sku_attr_value
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        for (SkuAttrValue skuAttrValue : skuAttrValueList) {
            skuAttrValue.setSkuId(skuInfoId);
        }
        skuAttrValueService.saveBatch(skuAttrValueList);

        // 4.sku的销售属性名和值的关系保存到 sku_sale_attr_value
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
            skuSaleAttrValue.setSkuId(skuInfoId);
            skuSaleAttrValue.setSpuId(skuInfo.getSpuId());
        }
        skuSaleAttrValueService.saveBatch(skuSaleAttrValueList);
    }

    // 上架
    @Override
    public void onSale(Long skuId) {
        skuInfoMapper.onSale(skuId);
        //TODO 2、给es中保存这个商品，商品就能被检索到了
    }

    // 下架
    @Override
    public void cancelSale(Long skuId) {
        skuInfoMapper.cancelSale(skuId);
        //TODO 2、从es中删除这个商品
    }

    // 商品详情大查询
    @Override
    public SkuDetailTo getSkuDetail(Long skuId) {
        //T1、商品详情大查询
        SkuDetailTo detailTo = new SkuDetailTo();

        // 0.查询skuInfo
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);

        // 1、商品(sku)的基本信息【价格、名字、重量等...】  sku_info表中
        // 将查询到的数据放到SkuDetailTo中
        detailTo.setSkuInfo(skuInfo);

        // 2、商品(sku)的图片    sku_image表中
        List<SkuImage> imageList = skuImageService.getSkuImage(skuId);
        skuInfo.setSkuImageList(imageList);

        // 3、商品(sku)所属的完整分类信息（根据3级分类倒着查）：base_category1、base_category2、base_category3
        CategoryViewTo categoryViewTo = baseCategory3Mapper.getCategoryView(skuInfo.getCategory3Id());
        detailTo.setCategoryView(categoryViewTo);

        // 4、查询实时价格
        BigDecimal price = get1010Price(skuId);
        detailTo.setPrice(price);

        //5、商品（sku）所属的SPU当时定义的所有销售属性名值组合（固定好顺序）。
        //          spu_sale_attr、spu_sale_attr_value
        // 并标识出当前sku到底spu的那种组合，页面要有高亮框 sku_sale_attr_value
        //查询当前sku对应的spu定义的所有销售属性名和值（固定好顺序）并且标记好当前sku属于哪一种组合
        List<SpuSaleAttr> saleAttrList = spuSaleAttrService.getSaleAttrAndValueMarkSku(skuInfo.getSpuId(),skuId);
        detailTo.setSpuSaleAttrList(saleAttrList);

        return detailTo;
    }

    /**
     * 查询实时价格
     *
     * @param skuId
     * @return
     */
    private BigDecimal get1010Price(Long skuId) {
       BigDecimal price = skuInfoMapper.getRealPrice(skuId);
       return price;
    }
}




