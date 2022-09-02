package com.atguigu.starter.cache.aspect;

import com.atguigu.starter.cache.annotation.GmallCache;
import com.atguigu.starter.cache.constant.SysRedisConst;
import com.atguigu.starter.cache.service.CacheOpsService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author quxiaolei
 * @date 2022/9/1 - 20:21
 */
@Aspect // 声明这是一个切面
@Component
public class CacheAspect {

    @Autowired
    CacheOpsService cacheOpsService;

    // 创建一个表达式解析器，这个是线程安全的
    ExpressionParser parser = new SpelExpressionParser();
    ParserContext context = new TemplateParserContext();

    /**
     * 目标方法： public SkuDetailTo getSkuDetailWithCache(Long skuId)
     * 连接点：所有目标方法的信息都在连接点
     * <p>
     * try{
     * //前置通知
     * 目标方法.invoke(args)
     * //返回通知
     * }catch(Exception e){
     * //异常通知
     * }finally{
     * //后置通知
     * }
     *
     * @Around 环绕通知注解
     * ProceedingJoinPoint 连接点,所有的参数等信息都可以通过它获取
     */
    @Around("@annotation(com.atguigu.starter.cache.annotation.GmallCache)") // 在标注了 @GmallCache 注解的方法环绕通知
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {

        Object result = null;

        // 1.先获取缓存中的key，不同方法有不同的key，前台传入的是表达式，所以需要计算
        String cacheKey = determinCacheKey(joinPoint);

        // 2.先查询缓存：先获取返回值类型；
        Type returnType = getMethodGenericReturnType(joinPoint); // 获取返回值类型
        Object cacheData = cacheOpsService.getCacheData(cacheKey, returnType); // 从缓存中获取到的数据


        // 3.判断缓存中是否存在
        if (cacheData == null) {
            // 4.准备回源
            // 5.先问布隆。有些场景并不一定需要布隆。比如：三级分类(只有一个大数据)
            String bloomName = determinBloomName(joinPoint); // 从注解中获取布隆过滤器的名字，如果是空值，代表不起用布隆过滤器

            if (!StringUtils.isEmpty(bloomName)) {
                // 说明注解中传入了布隆过滤器的名字，指定开启了布隆

                Object bVal = determinBloomValue(joinPoint); // 通过表达式计算出要在布隆过滤器中查询那个值。

                boolean contains = cacheOpsService.bloomContains(bloomName, bVal); // 查询该值是否在布隆过滤器中存在

                if (!contains) {
                    return null; // 布隆过滤器说没有，那一定时是没有
                }
            }

            // 6.布隆说有。准备回源，会有击穿风险，所以加锁处理
            boolean lock = false;
            String lockName = "";
            try {

                // 不同的场景用自己的锁
                lockName = determinLockName(joinPoint);
                lock = cacheOpsService.tryLock(lockName);

                if (lock) {
                    // 7.获取锁成功，开始回源,调用目标方法
                    result = joinPoint.proceed(joinPoint.getArgs());
                    long ttl = determinTtl(joinPoint); // 获取传入的过期时间
                    // 8.调用成功，重新保存到缓存中
                    cacheOpsService.saveData(cacheKey, result, ttl);
                    return result;
                } else {
                    Thread.sleep(1000L);
                    return cacheOpsService.getCacheData(cacheKey, returnType);
                }

            } finally {
                if (lock) {
                    cacheOpsService.unlock(lockName);
                }
            }
        }

        return cacheData;
    }

    /**
     * 获取传入的过期时间
     *
     * @param joinPoint
     * @return
     */
    private long determinTtl(ProceedingJoinPoint joinPoint) {

        //获取到GmallCache注解
        GmallCache cacheAnnotation = cacheAnnotation(joinPoint);
        long ttl = cacheAnnotation.ttl();
        return ttl;
    }

    /**
     * 根据表达式计算出要用的锁的名字
     *
     * @param joinPoint
     * @return
     */
    private String determinLockName(ProceedingJoinPoint joinPoint) {
        //1、拿到目标方法上的@GmallCache注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        Method method = signature.getMethod();

        //2、拿到注解
        GmallCache cacheAnnotation = method.getDeclaredAnnotation(GmallCache.class);

        //3、拿到锁表达式
        String lockName = cacheAnnotation.lockName(); //lock-方法名
        if (StringUtils.isEmpty(lockName)) {
            //没指定锁用方法级别的锁
            return SysRedisConst.LOCK_PREFIX + method.getName();
        }

        //4、计算锁值
        String lockNameVal = evaluationExpression(lockName, joinPoint, String.class);
        return lockNameVal;
    }

    /**
     * 根据布隆过滤器值表达式计算出布隆需要判定的值
     *
     * @param joinPoint
     * @return
     */
    private Object determinBloomValue(ProceedingJoinPoint joinPoint) {
        //获取到GmallCache注解
        GmallCache cacheAnnotation = cacheAnnotation(joinPoint);

        // 获取到表达式
        String bloomValue = cacheAnnotation.bloomValue();

        Object expression = evaluationExpression(bloomValue, joinPoint, Object.class);

        return expression;
    }


    /**
     * 获取布隆过滤器的名字
     *
     * @param joinPoint
     * @return
     */
    private String determinBloomName(ProceedingJoinPoint joinPoint) {
        //1、拿到目标方法上的@GmallCache注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        Method method = signature.getMethod();

        //2、拿到注解
        GmallCache cacheAnnotation = method.getDeclaredAnnotation(GmallCache.class);

        String bloomName = cacheAnnotation.bloomName();

        return bloomName;
    }

    /**
     * 获取目标方法的精确返回值类型
     *
     * @param joinPoint
     * @return
     */
    private Type getMethodGenericReturnType(ProceedingJoinPoint joinPoint) {

        // 1.获取目标方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        Type type = method.getGenericReturnType();

        return type;
    }

    /**
     * 根据当前整个连接点的执行信息，确定缓存使用什么key
     *
     * @param joinPoint
     * @return
     */
    private String determinCacheKey(ProceedingJoinPoint joinPoint) {

        // 获取到GmallCache注解
        GmallCache cacheAnnotation = cacheAnnotation(joinPoint);

        // 获取到注解上的cacheKey，此时是动态表达式，需要通过表达式计算出缓存键
        String expression = cacheAnnotation.cacheKey();

        // 根据表达式计算缓存键
        String cacheKey = evaluationExpression(expression, joinPoint, String.class);

        return cacheKey;

    }

    private <T> T evaluationExpression(String expression,
                                       ProceedingJoinPoint joinPoint,
                                       Class<T> clz) {
        // 1.获取到表达式
        Expression exp = parser.parseExpression(expression, context);

        // 2.sku:info:#{#params[0]}
        StandardEvaluationContext evaluationContext = new StandardEvaluationContext();

        // 3.取出所有参数，绑定到上下文
        Object[] args = joinPoint.getArgs();
        evaluationContext.setVariable("params", args);

        // 4.得到表达式的值
        T value = exp.getValue(evaluationContext, clz);
        return value;
    }


    /**
     * 获取目标方法上的@GmallCache注解，使用比较频繁，抽取出来
     *
     * @param joinPoint
     * @return
     */
    private GmallCache cacheAnnotation(ProceedingJoinPoint joinPoint) {
        // 1.获取目标方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 2.拿到注解
        GmallCache cacheAnnotation = method.getDeclaredAnnotation(GmallCache.class);

        return cacheAnnotation;
    }
}
