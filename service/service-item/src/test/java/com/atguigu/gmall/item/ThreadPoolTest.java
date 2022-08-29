package com.atguigu.gmall.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author quxiaolei
 * @date 2022/8/30 - 0:04
 */
@SpringBootTest
public class ThreadPoolTest {

    @Autowired
    ThreadPoolExecutor executor;

    @Test
    public void testPool() throws InterruptedException {

        for (int i = 0; i < 100; i++) {
            executor.submit(() -> {
                System.out.println(Thread.currentThread().getName() + ":" + UUID.randomUUID().toString());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        Thread.sleep(10000000000L);

    }
}
