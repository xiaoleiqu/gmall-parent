package com.atguigu.gmall.product.config.minio;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author quxiaolei
 * @date 2022/8/25 - 22:13
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.minio")
public class MinioProperties {

    String endpoint;
    String accessKey;
    String secretKey;
    String bucketName;

    // 以后加配置，配置文件中直接加，别忘了属性类加个属性
    // 以前的代码一个不改，以后的代码都能使用
    // 设计模式：对新增开发，对修改关闭(开闭原则)

}
