package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.common.util.DateUtil;
import com.atguigu.gmall.product.config.minio.MinioProperties;
import com.atguigu.gmall.product.service.FileUploadService;
import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Date;
import java.util.UUID;


/**
 *
 *
 * @author quxiaolei
 * @date 2022/8/25 - 21:22
 * <p>
 * 1、文件名重名覆盖问题
 * 2、以日期作为文件夹进行文件归档
 * 3、将参数抽取到配置文件内
 * 4、抽取Minio组件
 *
 */
@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Autowired
    MinioProperties minioProperties;

    @Autowired
    MinioClient minioClient;

    @Override
    public String fileUpload(MultipartFile file) throws Exception {

        // 1.创建MinionClient对象
        // 2.判断桶是否存在，如果不存在则创建
        // 前两步已经提取出MinIO组件(这个包下：com.atguigu.gmall.product.config.minio.MinioAutoConfiguration)


        // 3. 准备一个文件流

        String filename = UUID.randomUUID().toString().replace("-", "") + "-" + file.getOriginalFilename(); // 唯一文件名
        String contentType = file.getContentType(); // 文件类型(png?jpg?mp4?)
        InputStream inputStream = file.getInputStream(); // 文件流


        // 4.文件上传的参数设置
        PutObjectOptions options = new PutObjectOptions(file.getSize(), -1L);
        options.setContentType(contentType); // 设置文件类型，默认都是二进制流，如果是二进制流的话，默认是下载

        String date = DateUtil.formatDate(new Date()); // 获取当前日期

        minioClient.putObject(minioProperties.getBucketName(), date + "/" + filename, inputStream, options);

        String url = minioProperties.getEndpoint() + "/" + minioProperties.getBucketName() + "/" + date + "/" + filename;

        return url;
    }
}
