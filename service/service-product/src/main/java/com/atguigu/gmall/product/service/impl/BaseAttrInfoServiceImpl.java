package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.mapper.BaseAttrValueMapper;
import com.atguigu.gmall.product.mapper.BaseSaleAttrMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.atguigu.gmall.product.mapper.BaseAttrInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 屈晓磊
 * @description 针对表【base_attr_info(属性表)】的数据库操作Service实现
 * @createDate 2022-08-23 10:13:40
 */
@Service
public class BaseAttrInfoServiceImpl extends ServiceImpl<BaseAttrInfoMapper, BaseAttrInfo>
        implements BaseAttrInfoService {

    @Autowired
    BaseAttrInfoMapper baseAttrInfoMapper;

    @Autowired
    BaseAttrValueMapper baseAttrValueMapper;

    @Override
    public List<BaseAttrInfo> getAttrInfoAndValueByCategoryId(Long c1Id, Long c2Id, Long c3Id) {

        // 指定Sql连表查询平台的属性与属性值
        List<BaseAttrInfo> infos = baseAttrInfoMapper.getAttrInfoAndValueByCategoryId(c1Id, c2Id, c3Id);

        return infos;
    }

    @Override
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {

        if (baseAttrInfo.getId() == null) {
            // 1.如果id值为null，说明是新增
            addBaseAttrInfo(baseAttrInfo);
        } else {
            // 2.如果id值为null，说明是修改

            updateBaseAttrInfo(baseAttrInfo);
        }

    }

    private void updateBaseAttrInfo(BaseAttrInfo baseAttrInfo) {
        // 2.1 属性名的修改
        baseAttrInfoMapper.updateById(baseAttrInfo);

            /*
              2.2 该属性值，思路：前端传入的JSON中，整理传入的所有属性值id
              2.2.1 属性值的id存在的全部修改(数据库中已经存在，再次提交进行修改)，
              2.2.2 没有属性值id的进行新增(数据库中没有，说明前端新增的属性值，此时新增)，
              2.2.3 传入时没有属性值id但数据库内存在的进行删除（数据库中已经存在，提交的时候没有，说明前端已经删除了，此时进行删除）
             */
        // 整理属性值：
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();

        ArrayList<Long> vids = new ArrayList<>();
        for (BaseAttrValue attrValue : attrValueList) {
            Long vid = attrValue.getId();
            if (vid != null) {
                vids.add(vid);
            }
        }

        // 删除操作(不在传入的id值中的数据删除操作)
        if (vids.size() > 0) {

            // 部分删除
            baseAttrValueMapper.delete(new LambdaQueryWrapper<BaseAttrValue>()
                    .eq(BaseAttrValue::getAttrId, baseAttrInfo.getId()) // 查询出该平台id的属性值
                    .notIn(BaseAttrValue::getId, vids)); // 根据属性值id，删除前台未传入的属性值
        } else {
            // 如果前台传入的属性值一个id都没带，需要把这个平台属性下的所有属性值全部删除
            baseAttrValueMapper.delete(new LambdaQueryWrapper<BaseAttrValue>()
                    .eq(BaseAttrValue::getAttrId, baseAttrInfo.getId()));
        }

        // 新增、修改操作
        for (BaseAttrValue attrValue : attrValueList) {
            // 修改属性值
            if (attrValue.getId() != null) {
                // 说明数据库有该值，修改即可
                baseAttrValueMapper.updateById(attrValue);
            }

            // 新增操作
            if (attrValue.getId() == null) {
                attrValue.setAttrId(baseAttrInfo.getId());
                baseAttrValueMapper.insert(attrValue);
            }
        }
    }

    private void addBaseAttrInfo(BaseAttrInfo baseAttrInfo) {
        // 1、保存平台属性
        baseAttrInfoMapper.insert(baseAttrInfo);
        Long id = baseAttrInfo.getId(); // 获取自增id

        // 2、保存平台属性值
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        for (BaseAttrValue baseAttrValue : attrValueList) {
            baseAttrValue.setAttrId(id);
            baseAttrValueMapper.insert(baseAttrValue);
        }
    }
}




