package com.atguigu.gmall.item.controller;

import com.atguigu.gmall.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author quxiaolei
 * @date 2022/8/30 - 0:12
 * <p>
 * 如果自己自定义检测线程池时，可以这样做
 */
@RestController
public class ThreadPoolController {
    @Autowired
    ThreadPoolExecutor executor;

    @GetMapping("/close/pool")
    public Result clousPool() {
        executor.shutdown(); // 关闭线程池
        return Result.ok();
    }

    @GetMapping("/monitor/pool")
    public Result monitorThreadPllo() {

        int corePoolSize = executor.getCorePoolSize(); //线程核心数是多少
        long taskCount = executor.getTaskCount(); //当前任务数是多少

        return Result.ok(corePoolSize + "=====" + taskCount);
    }
}
