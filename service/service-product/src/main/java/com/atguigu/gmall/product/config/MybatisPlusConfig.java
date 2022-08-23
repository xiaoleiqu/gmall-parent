package com.atguigu.gmall.product.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author quxiaolei
 * @date 2022/8/23 - 23:20
 */
@Configuration // 告诉springboot这是一个配置类
public class MybatisPlusConfig {

    // 把MybatisPlus的插件主体(总插件)放到容器
    @Bean
    public MybatisPlusInterceptor interceptor() {
        // 插件主体
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 加入内部的小插件
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        paginationInnerInterceptor.setOverflow(true); // 页码溢出时，默认访问最后一页

        // 分页插件
        interceptor.addInnerInterceptor(paginationInnerInterceptor);

        return interceptor;
    }
}
