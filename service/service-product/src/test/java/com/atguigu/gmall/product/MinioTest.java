package com.atguigu.gmall.product;

import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import io.minio.errors.MinioException;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;

/**
 * @author quxiaolei
 * @date 2022/8/25 - 20:33
 */
public class MinioTest {

    @Test
    public void uploadFile() {
        try {
            // 1、使用MinIO服务的URL，端口，Access key和Secret key创建一个MinioClient对象
            MinioClient minioClient =
                    new MinioClient(
                            "http://192.168.6.101:9000",
                            "admin",
                            "admin123456");

            // 2、检查存储桶是否已经存在
            boolean isExist = minioClient.bucketExists("gmall");
            if (isExist) {
                System.out.println("Bucket already exists.");
            } else {
                // 3、如果桶不存在需要先创建一个桶
                minioClient.makeBucket("gmall");
            }

            // 4、使用putObject上传一个文件到存储桶中。

            /**
             * String bucketName,  桶名字
             * String objectName,  对象名，也就是文件名
             * InputStream stream, 文件流
             * PutObjectOptions options 上传的参数设置
             */
            // 1.准备一个文件流
            FileInputStream inputStream = new FileInputStream("F:\\20220310\\atguigu\\09_lfy_sph\\尚品汇\\资料\\03 商品图片\\品牌\\oppo.png");

            // 2.文件上传的参数设置
            PutObjectOptions options = new PutObjectOptions(inputStream.available(), -1L);

            // 3.告诉Minio上传的这个文件的内容类型
            options.setContentType("image/png");

            minioClient.putObject("gmall", "oppo.png", inputStream, options);

            System.out.println("上传成功");
        } catch (Exception e) {
            System.err.println("Error occurred: " + e);
        }
    }
}
