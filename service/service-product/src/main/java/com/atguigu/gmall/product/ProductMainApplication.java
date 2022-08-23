package com.atguigu.gmall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author quxiaolei
 * @date 2022/8/22 - 22:47
 */
@EnableSwagger2
@SpringCloudApplication
@MapperScan(basePackages = "com.atguigu.gmall.product.mapper") // 自动扫描这个包下的所有mapper接口
public class ProductMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductMainApplication.class, args);
    }
}
