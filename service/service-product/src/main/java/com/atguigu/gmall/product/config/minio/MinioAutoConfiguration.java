package com.atguigu.gmall.product.config.minio;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * Minio的组件类
 *
 * @author quxiaolei
 * @date 2022/8/25 - 22:19
 */
@Configuration //是容器中的组件
@EnableConfigurationProperties(value = MinioProperties.class)
public class MinioAutoConfiguration {

    @Autowired
    MinioProperties minioProperties;

    @Bean
    public MinioClient minioClient() throws Exception {

        // 1.创建MinionClient对象
        MinioClient minioClient = new MinioClient(
                minioProperties.getEndpoint(),
                minioProperties.getAccessKey(),
                minioProperties.getSecretKey()
        );

        // 2.判断桶是否存在，如果不存在则创建
        String bucketName = minioProperties.getBucketName();
        if (!minioClient.bucketExists(bucketName)) {
            minioClient.makeBucket(bucketName);
        }

        return minioClient;
    }

}
