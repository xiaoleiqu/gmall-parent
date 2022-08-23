package com.atguigu.gmall.product.controller;

/**
 * @author quxiaolei
 * @date 2022/8/22 - 22:56
 */

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.model.product.BaseCategory2;
import com.atguigu.gmall.model.product.BaseCategory3;
import com.atguigu.gmall.product.service.BaseCategory1Service;
import com.atguigu.gmall.product.service.BaseCategory2Service;
import com.atguigu.gmall.product.service.BaseCategory3Service;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 分类的请求处理器
 * 前后分离：前端发送请求，后台处理好后响应JSON数据
 * 所有请求全部返回Result对象的JSON，所有要携带的数据放到Result的data属性内即可
 */
//@Controller // 用来接收请求
//@ResponseBody // 所有的响应数据都直接写给浏览器(如果是对象写出JSON，如果是文本就写成普通字符串)
@RestController // 以上两个二合一版本
@RequestMapping("/admin/product/")
@Api(tags = "三级分类接口")
public class CategoryController {

    @Autowired
    BaseCategory1Service baseCategory1Service;

    @Autowired
    BaseCategory2Service baseCategory2Service;

    @Autowired
    BaseCategory3Service baseCategory3Service;

    /**
     * 获取所有一级分类
     */
    @ApiOperation("获取所有一级分类")
    @GetMapping("getCategory1")
    public Result getCategory1() {
        List<BaseCategory1> list = baseCategory1Service.list();
        return Result.ok(list);
    }

    /**
     * 获取某个一级分类下的所有二级分类
     *
     * @param c1Id 一级分类的id
     * @return
     */
    @ApiOperation("获取所有二级分类")
    @GetMapping("getCategory2/{c1Id}")
    public Result getCategory2(@PathVariable("c1Id") Long c1Id) {
        // 查询父分类是c1Id的所有二级分类
        List<BaseCategory2> category2s = baseCategory2Service.getCategory1Child(c1Id);
        return Result.ok(category2s);
    }

    @ApiOperation("获取所有三级分类")
    @GetMapping("getCategory3/{c2Id}")
    public Result getCategory3(@PathVariable("c2Id") Long c2Id) {
        // 查询父类是c2Id的所有三级分类
        List<BaseCategory3> category3s = baseCategory3Service.getCategory2Child(c2Id);
        return Result.ok(category3s);
    }

}
