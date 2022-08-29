package com.atguigu.gmall.common.config.threadpool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author quxiaolei
 * @date 2022/8/29 - 22:55
 * <p>
 * 配置线程池：
 */
//1、AppThreadPoolProperties 里面的所有属性和指定配置绑定
//2、AppThreadPoolProperties 组件自动放到容器中
//开启自动化属性绑定配置
@EnableConfigurationProperties(AppThreadPoolProperties.class)
@SpringBootConfiguration
public class AppThreadPoolAutoConfiguration {

    @Autowired
    AppThreadPoolProperties appThreadPoolProperties;

    @Value("${spring.application.name}")
    String applicationName;

    @Bean
    public ThreadPoolExecutor coreExecutro() {

        // -Xmx100m 100mb：  内存合理规划使用
        //压力测试：  1亿/1万/1个   500mb
//        new ArrayBlockingQueue(10)： 底层队列是一个数组
//        new LinkedBlockingDeque(10)： 底层是一个链表
        //数组与链表？ -- 检索、插入
        //数组是连续空间，链表不连续（利用碎片化空间）

        /**
         * 线程池7大参数：
         * int corePoolSize,  核心线程池：cpu核心数：4
         * int maximumPoolSize, 最大线程数：
         * long keepAliveTime,  线程存活时间(非核心的线程的存活时间)
         * TimeUnit unit,  时间单位
         * BlockingQueue<Runnable> workQueue,  阻塞队列：需要合理规划大小(项目最终能占的最大内存决定/或者队列的大小根据接口吞吐量标准调整)
         * ThreadFactory threadFactory,  线程工厂(创建线程)，自定义创建线程的方法
         * RejectedExecutionHandler handler 拒绝策略
         *
         * 拒绝策略：能启用拒绝策略，说明核心线程数满了，最大线程数也满了，队列中也满了
         * DiscardOldestPolicy：抛弃队列中等待最久的任务，然后把当前任务加人队列中，尝试再次提交当前任务。
         * AbortPolicy（默认）: 直接抛出RejectedExecutionException异常阻止系统正常运行
         * CallerRunsPolicy: “调用者运行”一种调节机制，该策略既不会抛弃任务，也不会抛出异常，而是将某些任务回退到调用者，从而降低新任务的流量(谁给我提交的，由谁以同步的方式执行)。
         * DiscardPolicy: 该策略默默地丢弃无法处理的任务，不做任何处理也不抛出异常。如果业务允许任务丢失，这是最好的一种策略
         */
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                appThreadPoolProperties.getCore(),
                appThreadPoolProperties.getMax(),
                appThreadPoolProperties.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(appThreadPoolProperties.getQueueSize()), // 阻塞队列(队列的大小由项目最终能占的最大内存决定/或者队列的大小根据接口吞吐量标准调整)
                new ThreadFactory() { // 负责给线程池创建线程
                    int i = 0; // 记录线程id

                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = new Thread(r); // 线程接收任务
                        thread.setName(applicationName+"[core-thread-"+ i++ +"]");
                        return thread;
                    }
                },
                // 拒绝策略，生产环境使用CallerRuns，就算线程池满了，不能提交的任务，由当前线程自己以同步的方式执行
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        return executor;

    }

}
