package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseCategory3;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.service.BaseCategory3Service;
import com.atguigu.gmall.product.mapper.BaseCategory3Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 屈晓磊
 * @description 针对表【base_category3(三级分类表)】的数据库操作Service实现
 * @createDate 2022-08-22 23:54:42
 */
@Service
public class BaseCategory3ServiceImpl extends ServiceImpl<BaseCategory3Mapper, BaseCategory3>
        implements BaseCategory3Service {

    @Autowired
    BaseCategory3Mapper baseCategory3Mapper;

    /**
     * 根据二级分类id，查询三级分类
     *
     * @param c2Id
     * @return
     */
    @Override
    public List<BaseCategory3> getCategory2Child(Long c2Id) {
        List<BaseCategory3> category3s = baseCategory3Mapper.selectList(new QueryWrapper<BaseCategory3>().eq("category2_id", c2Id));
        return category3s;
    }
}




