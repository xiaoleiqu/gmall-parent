package com.atguigu.gmall.model.to;

import lombok.Data;

import java.util.List;

/**
 * @author quxiaolei
 * @date 2022/8/26 - 21:22
 */
@Data
public class CategoryTreeTo {
    private Long categoryId;
    private String categoryName;
    private List<CategoryTreeTo> categoryChild; // 子分类（嵌套类型，自己嵌套自己）
}
