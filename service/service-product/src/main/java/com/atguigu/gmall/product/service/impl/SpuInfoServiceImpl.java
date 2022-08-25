package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.product.SpuSaleAttrValue;
import com.atguigu.gmall.product.service.SpuImageService;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import com.atguigu.gmall.product.service.SpuSaleAttrValueService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.atguigu.gmall.product.service.SpuInfoService;
import com.atguigu.gmall.product.mapper.SpuInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 屈晓磊
 * @description 针对表【spu_info(商品表)】的数据库操作Service实现
 * @createDate 2022-08-23 10:13:40
 */
@Service
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoMapper, SpuInfo>
        implements SpuInfoService {

    @Autowired
    SpuInfoMapper spuInfoMapper;

    @Autowired
    SpuImageService spuImageService;

    @Autowired
    SpuSaleAttrValueService spuSaleAttrValueService;

    @Autowired
    SpuSaleAttrService spuSaleAttrService;

    /**
     * 保存spu信息
     *
     * @param spuInfo
     */
    @Override
    public void spuInfoService(SpuInfo spuInfo) {

        // 1.将spu的基本信息保存到spu_info表中
        spuInfoMapper.insert(spuInfo);
        Long spuInfoId = spuInfo.getId(); // 获取spu保存后的id值

        // 2.将spu的图片保存到spu_image表中
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        for (SpuImage spuImage : spuImageList) {
            // 回填spu_id
            spuImage.setSpuId(spuInfoId);
        }
        // 批量保存图片信息
        spuImageService.saveBatch(spuImageList);

        // 3.保存销售属性到spu_sale_atter表中
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
            // 回填spu_id
            spuSaleAttr.setSpuId(spuInfoId);

            // 4.拿到这个销售属性所对应的所有属性值
            List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
            for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList) {
                // 回填spu_id
                spuSaleAttrValue.setSpuId(spuInfoId);
                // 回填销售属性名
                spuSaleAttrValue.setSaleAttrName(spuSaleAttr.getSaleAttrName());
            }
            spuSaleAttrValueService.saveBatch(spuSaleAttrValueList);
        }
        spuSaleAttrService.saveBatch(spuSaleAttrList);
    }
}




