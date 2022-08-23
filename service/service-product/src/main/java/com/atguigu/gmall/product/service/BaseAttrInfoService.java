package com.atguigu.gmall.product.service;


import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author 屈晓磊
 * @description 针对表【base_attr_info(属性表)】的数据库操作Service
 * @createDate 2022-08-23 10:13:40
 */
public interface BaseAttrInfoService extends IService<BaseAttrInfo> {

    /**
     * 查询某个分类下的所有平台属性
     *
     * @param c1Id
     * @param c2Id
     * @param c3Id
     * @return
     */
    List<BaseAttrInfo> getAttrInfoAndValueByCategoryId(Long c1Id, Long c2Id, Long c3Id);

    /**
     * 新增、修改平台属性
     *
     * @param baseAttrInfo
     */
    void saveAttrInfo(BaseAttrInfo baseAttrInfo);
}
