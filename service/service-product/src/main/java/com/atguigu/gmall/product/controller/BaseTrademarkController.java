package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author quxiaolei
 * @date 2022/8/23 - 23:10
 */
@Api(tags = "品牌接口")
@RestController
@RequestMapping("/admin/product")
public class BaseTrademarkController {

    @Autowired
    BaseTrademarkService baseTrademarkService;

    @ApiOperation("分页查询所有品牌")
    @GetMapping("baseTrademark/{pageNumber}/{pageSize}")
    public Result baseTrademark(@PathVariable("pageNumber") Long pageNumber,
                                @PathVariable("pageSize") Long pageSize) {

        Page<BaseTrademark> page = new Page<>(pageNumber, pageSize);

        // 分页查询
        Page<BaseTrademark> pageResult = baseTrademarkService.page(page);

        return Result.ok(pageResult);
    }

    @ApiOperation("添加品牌")
    @PostMapping("baseTrademark/save")
    public Result saveBaseTrademark(@RequestBody BaseTrademark baseTrademark) {
        baseTrademarkService.save(baseTrademark);
        return Result.ok();
    }

    @ApiOperation("根据id获取品牌")
    @GetMapping("baseTrademark/get/{id}")
    public Result getBaseTrademark(@PathVariable("id") Long id) {

        BaseTrademark trademark = baseTrademarkService.getById(id);
        return Result.ok(trademark);
    }

    @ApiOperation("修改品牌")
    @PutMapping("baseTrademark/update")
    public Result updateBaseTrademark(@RequestBody BaseTrademark baseTrademark) {
        baseTrademarkService.updateById(baseTrademark);
        return Result.ok();
    }

    @ApiOperation("删除品牌")
    @DeleteMapping("baseTrademark/remove/{id}")
    public Result removeBaseTrademark(@PathVariable("id") Long id) {

        baseTrademarkService.removeById(id);

        return Result.ok();

    }

}
