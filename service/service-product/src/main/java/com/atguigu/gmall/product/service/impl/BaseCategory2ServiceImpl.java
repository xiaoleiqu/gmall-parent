package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseCategory2;
import com.atguigu.gmall.model.to.CategoryTreeTo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.service.BaseCategory2Service;
import com.atguigu.gmall.product.mapper.BaseCategory2Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 屈晓磊
 * @description 针对表【base_category2(二级分类表)】的数据库操作Service实现
 * @createDate 2022-08-22 23:54:42
 */
@Service
public class BaseCategory2ServiceImpl extends ServiceImpl<BaseCategory2Mapper, BaseCategory2>
        implements BaseCategory2Service {

    @Autowired
    BaseCategory2Mapper baseCategory2Mapper;

    /**
     * 查询一级分类的所有二级分类
     *
     * @param c1Id
     * @return
     */
    @Override
    public List<BaseCategory2> getCategory1Child(Long c1Id) {

        List<BaseCategory2> list = baseCategory2Mapper.selectList(new LambdaQueryWrapper<BaseCategory2>().eq(BaseCategory2::getCategory1Id, c1Id));

        return list;
    }

    /**
     * 首页三级分类树形结构查询
     * @return
     */
    @Override
    public List<CategoryTreeTo> getAllCategoryWithTree() {

        return  baseCategory2Mapper.getAllCategoryWithTree();
    }
}




