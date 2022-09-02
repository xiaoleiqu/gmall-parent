package com.atguigu.gmall.common.retry;

import feign.RetryableException;
import feign.Retryer;

/**
 * 幂等性：做一次和做很多次效果一样
 *      - select、update、delete 天然幂等
 *      - insert：不幂等，每次操作都会导致数据库保存新数据进去
 *      - 生产环境需要关闭feign重试功能 特殊业务放大读取超时，连接超时不用管
 * <p>
 * <p>
 * <p>
 * 配置Feign重试次数（Feign默认是先发一次请求，如果超时，会再发一次请求。）
 *
 * @author quxiaolei
 * @date 2022/9/2 - 20:36
 */
public class MyRetryer implements Retryer {

    private int cur = 0;
    private int max = 2;

    public MyRetryer() {
        cur = 0;
        max = 2;
    }

    /**
     * 继续重试还是中断重试
     *
     * @param e
     */
    @Override
    public void continueOrPropagate(RetryableException e) {
        throw e; // 表示一次都不重试，只要抛出异常就中断重试，若啥也不做处理会一直重试

        // 重试两次
//        if(cur++ > max){
//            throw e;
//        }
    }

    @Override
    public Retryer clone() {
        return this;
    }
}
