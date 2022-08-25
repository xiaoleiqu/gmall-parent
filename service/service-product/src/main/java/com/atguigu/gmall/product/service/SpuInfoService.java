package com.atguigu.gmall.product.service;


import com.atguigu.gmall.model.product.SpuInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 屈晓磊
* @description 针对表【spu_info(商品表)】的数据库操作Service
* @createDate 2022-08-23 10:13:40
*/
public interface SpuInfoService extends IService<SpuInfo> {

    /**
     * 保存SPU信息
     * @param spuInfo
     */
    void spuInfoService(SpuInfo spuInfo);
}
