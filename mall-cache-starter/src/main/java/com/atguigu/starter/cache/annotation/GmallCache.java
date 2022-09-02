package com.atguigu.starter.cache.annotation;

import java.lang.annotation.*;

/**
 * @author quxiaolei
 * @date 2022/9/1 - 20:13
 * <p>
 * 自定义注解，缓存注解
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface GmallCache {

    String cacheKey() default ""; // cachekey

    String bloomName() default ""; // 如果指定了布隆过滤器的名字，就使用

    String bloomValue() default ""; // 指定布隆过滤器如果需要判定的话，用什么表达式计算出的值进行判断

    String lockName() default ""; // 传入精确锁就用精确的，否则用全局默认的

}
