package com.atguigu.gmall.product.service;


import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.to.SkuDetailTo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;

/**
* @author 屈晓磊
* @description 针对表【sku_info(库存单元表)】的数据库操作Service
* @createDate 2022-08-23 10:13:40
*/
public interface SkuInfoService extends IService<SkuInfo> {

    /**
     * sku数据大保存
     * @param skuInfo
     */
    void saveSkuInfo(SkuInfo skuInfo);

    /**
     * 上加
     * @param skuId
     */
    void onSale(Long skuId);

    /**
     * 下架
     * @param skuId
     */
    void cancelSale(Long skuId);

    /**
     * 商品详情大查询
     * @param skuId
     * @return
     */
    SkuDetailTo getSkuDetail(Long skuId);


    /**
     * 获取sku_info信息
     * @param skuId
     * @return
     */
    SkuInfo getDetailSkuInfo(Long skuId);

    /**
     * 查询sku的实时价格
     * @param skuId
     * @return
     */
    List<SkuImage> getDetailSkuImages(Long skuId);

    /**
     * 查询sku的实时价格
     * @param skuId
     * @return
     */
    BigDecimal get1010Price(Long skuId);

    /**
     * 查询出所有的skuId
     * @return
     */
    List<Long> findAllSkuId();

    /**
     * 得到某个sku在es中需要存储的所有数据
     * @param skuId
     * @return
     */
    Goods getGoodsBySkuId(Long skuId);
}
