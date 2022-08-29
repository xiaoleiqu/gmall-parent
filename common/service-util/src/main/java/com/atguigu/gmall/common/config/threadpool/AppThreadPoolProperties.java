package com.atguigu.gmall.common.config.threadpool;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author quxiaolei
 * @date 2022/8/29 - 23:35
 */
@ConfigurationProperties(prefix = "app.thread-pool")
@Component
@Data
public class AppThreadPoolProperties {

    private Integer core = 2;
    private Integer max = 4;
    private Integer queueSize = 200;
    private Long KeepAliveTime = 300L;
}
