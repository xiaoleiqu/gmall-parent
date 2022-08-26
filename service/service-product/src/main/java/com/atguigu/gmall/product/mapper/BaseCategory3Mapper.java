package com.atguigu.gmall.product.mapper;


import com.atguigu.gmall.model.product.BaseCategory3;
import com.atguigu.gmall.model.to.CategoryViewTo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author 屈晓磊
* @description 针对表【base_category3(三级分类表)】的数据库操作Mapper
* @createDate 2022-08-22 23:54:42
* @Entity com.atguigu.gmall.product.domain.BaseCategory3
*/
public interface BaseCategory3Mapper extends BaseMapper<BaseCategory3> {

    /**
     * 商品(sku)所属的完整分类信息（根据3级分类倒着查）
     * @param category3Id
     * @return
     */
    CategoryViewTo getCategoryView(Long category3Id);
}




