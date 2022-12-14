package com.atguigu.gmall.product.mapper;


import com.atguigu.gmall.model.product.SkuInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
* @author 屈晓磊
* @description 针对表【sku_info(库存单元表)】的数据库操作Mapper
* @createDate 2022-08-23 10:13:40
* @Entity com.atguigu.gmall.product.domain.SkuInfo
*/
public interface SkuInfoMapper extends BaseMapper<SkuInfo> {

    // 上架
    void onSale(@Param("skuId") Long skuId);

    // 下架
    void cancelSale(@Param("skuId") Long skuId);

    // 查询实时价格
    BigDecimal getRealPrice(@Param("skuId") Long skuId);

    /**
     * 查询出所有的skuId
     * @return
     */
    List<Long> getAllSkuId();
}




