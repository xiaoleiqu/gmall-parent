package com.atguigu.gmall.product.service;


import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 屈晓磊
* @description 针对表【spu_sale_attr(spu销售属性)】的数据库操作Service
* @createDate 2022-08-23 10:13:40
*/
public interface SpuSaleAttrService extends IService<SpuSaleAttr> {

    /**
     * 查询出指定spu当时定义的所有销售属性的名和值
     * @param spuId
     * @return
     */
    List<SpuSaleAttr> getSaleAttrAndValueBySpuId(Long spuId);

    /**
     * 商品（sku）所属的SPU当时定义的所有销售属性名值组合（固定好顺序）。
     * @param spuId
     * @param skuId
     * @return
     */
    List<SpuSaleAttr> getSaleAttrAndValueMarkSku(Long spuId, Long skuId);

    /**
     * 商品（sku）的所有兄弟产品的销售属性名和值组合关系全部查询出来，并封装
     * @param spuId
     * @return
     */
    String getAllSkuSaleAttrValueJson(Long spuId);
}
