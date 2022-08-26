package com.atguigu.gmall.web.feign;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.to.CategoryTreeTo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author quxiaolei
 * @date 2022/8/26 - 21:14
 */
@RequestMapping("/api/inner/rpc/product")
@FeignClient("service-product")
public interface CategoryFeignClient {

    /**
     * 1.给service-product发送一个Get请求，路径是/api/inner/rpc/product/category/tree
     * 2.拿到远程响应JSON结果后，转成Result类型的对象，并且返回的数据是 List<CategoryTreeTo>
     *
     * @return
     */
    @GetMapping("/category/tree")
    Result<List<CategoryTreeTo>> getAllCategoryWithTree();

}
