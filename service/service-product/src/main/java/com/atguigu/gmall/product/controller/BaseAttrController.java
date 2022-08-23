package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.atguigu.gmall.product.service.BaseAttrValueService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author quxiaolei
 * @date 2022/8/23 - 18:03
 */
@RequestMapping("/admin/product/")
@RestController
@Api(tags = "平台属性API")
public class BaseAttrController {

    @Autowired
    BaseAttrInfoService baseAttrInfoService;

    @Autowired
    BaseAttrValueService baseAttrValueService;

    @ApiOperation("查询某个分类下的所有平台属性")
    @GetMapping("attrInfoList/{c1Id}/{c2Id}/{c3Id}")
    public Result attrInfoList(@PathVariable("c1Id") Long c1Id,
                               @PathVariable("c2Id") Long c2Id,
                               @PathVariable("c3Id") Long c3Id) {

        List<BaseAttrInfo> infos = baseAttrInfoService.getAttrInfoAndValueByCategoryId(c1Id, c2Id, c3Id);

        return Result.ok(infos);
    }

    /**
     * 保存、修改属性信息二合一的方法：
     * * 前端把所有页面录入的数据以json的方式post传给我们
     * * 请求体：
     * *  {"id":null,"attrName":"出厂日期","category1Id":0,"category2Id":0,"category3Id":0,"attrValueList":[{"valueName":"2019","edit":false},{"valueName":"2020","edit":false},{"valueName":"2021","edit":false},{"valueName":"2022","edit":false}],"categoryId":2,"categoryLevel":1}
     * *
     * *  取出前端发送的请求的请求体中的数据 @RequestBody，
     * *  并把这个数据(json)转成指定的BaseAttrInfo对象，
     * *  BaseAttrInfo封装前端提交来的所有数据
     */
    @ApiOperation("新增、修改平台属性")
    @PostMapping("saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo) {

        baseAttrInfoService.saveAttrInfo(baseAttrInfo);

        return Result.ok();
    }

    @ApiOperation("根据平台属性ID，查询属性值")
    @GetMapping("getAttrValueList/{attrId}")
    public Result getAttrValueList(@PathVariable("attrId") Long attrId) {
        List<BaseAttrValue> baseAttrValueList = baseAttrValueService.getAttrValueByAttrId(attrId);
        return Result.ok(baseAttrValueList);
    }
}

