package com.atguigu.gmall.web.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.product.CategoryFeignClient;
import com.atguigu.gmall.model.to.CategoryTreeTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @author quxiaolei
 * @date 2022/8/26 - 20:53
 */

@Controller
public class IndexController {

    @Autowired
    CategoryFeignClient categoryFeignClient;

    @GetMapping({"/", "/index","/index.html"})
    public String indexPage(Model model) {

        // 查询出所有的菜单，封装成一个树形结构的数据模型
        // 远程调用
        Result<List<CategoryTreeTo>> result = categoryFeignClient.getAllCategoryWithTree();

        if (result.isOk()) {
            List<CategoryTreeTo> list = result.getData();
            model.addAttribute("list", list);
        }

        return "index/index";
    }
}
