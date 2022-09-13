package com.atguigu.gmall.web.config;

import com.atguigu.gmall.common.constant.SysRedisConst;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 调用feign时，feign会创建新的请求，从而导致老请求头中的部分信息丢失。所以需要创建一个拦截器，将老连接的一些信息获取到，
 * 设置给feign创建的新请求中
 *
 * @author quxiaolei
 * @date 2022/9/7 - 20:18
 */
@Configuration
public class WebAllConfiguration {

    /**
     * 把用户的id带到feign即将发起的新请求中
     *
     * @return
     */
    @Bean
    public RequestInterceptor userHeaderInterceptor() {

        return (template) -> {
            // 随时调用，获取老请求
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            HttpServletRequest request = attributes.getRequest();

            String userId = request.getHeader(SysRedisConst.USERID_HEADER);

            // 用户id头添加到feign的新请求中
            template.header(SysRedisConst.USERID_HEADER, userId);

            //临时id也透传
            String tempId = request.getHeader(SysRedisConst.USERTEMPID_HEADER);
            template.header(SysRedisConst.USERTEMPID_HEADER,tempId);
        };
    }
}
