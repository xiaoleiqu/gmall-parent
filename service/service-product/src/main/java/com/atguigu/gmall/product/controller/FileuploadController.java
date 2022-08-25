package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.service.FileUploadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author quxiaolei
 * @date 2022/8/23 - 23:49
 */
@Api(tags = "文件上传接口")
@RequestMapping("/admin/product/")
@RestController
public class FileuploadController {

    @Autowired
    FileUploadService fileUploadService;


    /**
     * 文件上传：
     * 1、前端把文件流放到 Post请求将数据放在请求体内(包含了文件流)
     * 2、如何获取：
     *
     * @param file
     * @return
     * @RequestParam("file") MultipartFile file
     * @RequestPart("file") MultipartFile file  专门处理文件的
     * <p>
     * 3、各种注解，获取不同位置的请求数据
     * @RequestParam：无论是什么请求，接收请求参数；用一个pojo把所有请求都接了
     * @RequestPart：接请求参数中的文件项目
     * @RequestBody: 接请求体内的所有数据(json转为pojo)
     * @PathVariable: 接路径参数上的动态变量
     * @RequestHeader：获取浏览器发送的请求的请求头中的某些值
     * @CookieValue：获取浏览器发送的请求的Cookie值
     */
    @ApiOperation("文件上传")
    @PostMapping("/fileUpload")
    public Result fileUpload(@RequestPart("file") MultipartFile file) throws Exception {

        // 文件上传功能
        String url = fileUploadService.fileUpload(file);
        return Result.ok(url);
    }
}
