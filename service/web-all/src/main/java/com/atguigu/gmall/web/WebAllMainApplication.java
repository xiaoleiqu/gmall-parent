package com.atguigu.gmall.web;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author quxiaolei
 * @date 2022/8/26 - 19:28
 * <p>
 * 不要启动用数据源的自动配置：DataSourceAutoConfiguration 就会生效
 * @SpringBootApplication(exclude = DataSourceAutoConfiguration.class)这个排除
 */

//@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
//@EnableDiscoveryClient
//@EnableCircuitBreaker

@SpringCloudApplication
@EnableFeignClients(basePackages = {
        "com.atguigu.gmall.feign"
}) //只会扫描主程序所在的子包,所以需要指定扫描的包
public class WebAllMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebAllMainApplication.class, args);
    }
}
