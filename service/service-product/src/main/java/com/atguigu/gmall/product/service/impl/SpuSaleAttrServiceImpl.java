package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.to.ValueSkuJsonTo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.atguigu.gmall.product.service.SpuSaleAttrService;
import com.atguigu.gmall.product.mapper.SpuSaleAttrMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 屈晓磊
 * @description 针对表【spu_sale_attr(spu销售属性)】的数据库操作Service实现
 * @createDate 2022-08-23 10:13:40
 */
@Service
public class SpuSaleAttrServiceImpl extends ServiceImpl<SpuSaleAttrMapper, SpuSaleAttr>
        implements SpuSaleAttrService {

    @Autowired
    SpuSaleAttrMapper spuSaleAttrMapper;

    @Override
    public List<SpuSaleAttr> getSaleAttrAndValueBySpuId(Long spuId) {

        List<SpuSaleAttr> list = spuSaleAttrMapper.getSaleAttrAndValueBySpuId(spuId);

        return list;
    }

    /**
     * 商品（sku）所属的SPU当时定义的所有销售属性名值组合（固定好顺序）。
     *
     * @param spuId
     * @param skuId
     * @return
     */
    @Override
    public List<SpuSaleAttr> getSaleAttrAndValueMarkSku(Long spuId, Long skuId) {

        return spuSaleAttrMapper.getSaleAttrAndValueMarkSku(spuId, skuId);
    }

    /**
     * //商品（sku）的所有兄弟产品的销售属性名和值组合关系全部查询出来，并封装成前端需要的JSON
     *
     * @param spuId
     * @return
     */
    @Override
    public String getAllSkuSaleAttrValueJson(Long spuId) {
        List<ValueSkuJsonTo> valueSkuJsonTos = spuSaleAttrMapper.getAllSkuSaleAttrValueJson(spuId);

        Map<String, Long> map = new HashMap<>();

        for (ValueSkuJsonTo valueSkuJsonTo : valueSkuJsonTos) {

            String valueJson = valueSkuJsonTo.getValueJson();
            Long skuId = valueSkuJsonTo.getSkuId();

            map.put(valueJson, skuId);

        }

        // 将map转换为字符串
        String json = Jsons.toStr(map);

        return json;
    }
}




