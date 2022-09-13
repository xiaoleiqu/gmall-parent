package com.atguigu.gmall.cart.service.impl;

import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.auth.AuthUtils;
import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.Jsons;
import com.atguigu.gmall.feign.product.SkuProductFeignClient;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.vo.user.UserAuthInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author quxiaolei
 * @date 2022/9/8 - 19:49
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    SkuProductFeignClient skuProductFeignClient;

    @Autowired
    ThreadPoolExecutor executor;

    /**
     * 添加指定商品到购物车
     *
     * @param skuId
     * @param skuNum
     * @return
     */
    @Override
    public SkuInfo addToCart(Long skuId, Integer skuNum) {

        // cart:user: == hash(skuId,skuInfo)
        // 1.决定购物车使用那个键
        String cartKey = determinCartKey();

        // 2.给购物车添加指定商品
        SkuInfo skuInfo = addItemToCart(skuId, skuNum, cartKey);

        // 3.购物车超时设置，自动延期
        UserAuthInfo authInfo = AuthUtils.getCurrentAuthInfo(); // 获取用户id或临时id信息
        if (authInfo.getUserId() == null) {
            // 用户未登录状态，一直操作的是临时购物车
            String tempKey = SysRedisConst.CART_KEY + authInfo.getUserTempId();
            // 临时购物车都有过期时间，自动延期
            redisTemplate.expire(tempKey, 90, TimeUnit.DAYS);
        }
        return skuInfo;
    }


    /**
     * 1.决定购物车使用那个键  根据用户登录信息决定用哪个购物车键
     *
     * @return
     */
    public String determinCartKey() {
        UserAuthInfo info = AuthUtils.getCurrentAuthInfo();

        String cartKey = SysRedisConst.CART_KEY;

        if (info.getUserId() != null) {
            // 用户登录了
            cartKey = cartKey + "" + info.getUserId();
        } else {
            // 用户没有登录，使用的是临时id
            cartKey = cartKey + "" + info.getUserTempId();
        }
        return cartKey;
    }

    /**
     * 2.给购物车添加指定商品
     *
     * @param skuId
     * @param skuNum
     * @param cartKey
     * @return
     */
    public SkuInfo addItemToCart(Long skuId, Integer skuNum, String cartKey) {
        // key(cartKey) - hash(skuId - skuInfo)

        // 1.先获取到购物车
        BoundHashOperations<String, String, String> cart = redisTemplate.boundHashOps(cartKey);

        // 2.先看当前购物车里是否有该商品，如果没有则新增，如果有则修改数量
        Boolean hasKey = cart.hasKey(skuId.toString());

        // 获取当前购物车中的品列数量 需要做出限制，购物车中的品类数量不能超过200
        Long itemSize = cart.size();
        if (!hasKey) {
            // 新增商品

            if (itemSize + 1 > SysRedisConst.CART_ITEMS_LIMIT) {
                // 抛出异常  需要做出限制，购物车中的品类数量不能超过200
                throw new GmallException(ResultCodeEnum.CART_OVERFLOW);
            }

            // 2.1 远程获取商品信息
            SkuInfo data = skuProductFeignClient.getSkuInfo(skuId).getData();

            // 2.2 转为购物车中要保存的数据模型 把SkuInfo转为CartInfo
            CartInfo item = converSkuInfo2CartInfo(data);

            // 2.3 设置商品数量
            item.setSkuNum(skuNum);

            // 2.4 保存到redis中
            cart.put(skuId.toString(), Jsons.toStr(item));

            return data; // 该方法需要返回一个商品详情数据；
        } else {

            // 购物车中之前添加过，修改skuId对应的商品的数量
            // 3.1 获取实时价格
            BigDecimal price = skuProductFeignClient.getSku1010Price(skuId).getData();

            // 3.2 获取原来的商品信息
            CartInfo cartInfo = getItemFromCart(cartKey, skuId);

            // 3.3 更新商品
            cartInfo.setSkuPrice(price);
            cartInfo.setSkuNum(cartInfo.getSkuNum() + skuNum);
            cartInfo.setUpdateTime(new Date());

            // 3.4 同步到redis中,由于该方法需要返回一个商品详情数据，所以需要进行数据转换
            cart.put(skuId.toString(), Jsons.toStr(cartInfo));
            SkuInfo skuInfo = converCartInfo2SkuInfo(cartInfo);
            return skuInfo;
        }
    }

    /**
     * 3.4 同步到redis中，由于该方法需要返回一个商品详情数据，所以需要进行数据转换
     *
     * @param cartInfo
     * @return
     */
    private SkuInfo converCartInfo2SkuInfo(CartInfo cartInfo) {

        SkuInfo skuInfo = new SkuInfo();

        skuInfo.setSkuName(cartInfo.getSkuName());
        skuInfo.setSkuDefaultImg(cartInfo.getImgUrl());
        skuInfo.setId(cartInfo.getSkuId());

        return skuInfo;
    }

    /**
     * 3.2 获取原来的商品信息
     *
     * @param cartKey
     * @param skuId
     * @return
     */
    public CartInfo getItemFromCart(String cartKey, Long skuId) {
        BoundHashOperations<String, String, String> ops = redisTemplate.boundHashOps(cartKey);

        // 获取购物者中指定商品的JSON数据
        String jsonData = ops.get(skuId.toString());
        return Jsons.toObj(jsonData, CartInfo.class);
    }

    /**
     * 获取购物车列表
     *
     * @param cartKey
     * @return
     */
    @Override
    public List<CartInfo> getCartList(String cartKey) {
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(cartKey);

        //流式编程
        List<CartInfo> infos = hashOps.values().stream()
                .map(str -> Jsons.toObj(str, CartInfo.class))
                .sorted((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime()))
                .collect(Collectors.toList());

        //顺便把购物车中所有商品的价格再次查询一遍进行更新。 异步不保证立即执行。
        //不用等价格更新。 异步情况下拿不到老请求
        //1、老请求
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        //【异步会导致feign丢失请求】
        executor.submit(() -> {
            //2、绑定请求到到这个线程
            RequestContextHolder.setRequestAttributes(requestAttributes);
            updateCartAllItemsPrice(cartKey);
            //3、移除数据
            RequestContextHolder.resetRequestAttributes();
        });
        return infos;
    }

    @Override
    public void updateItemNum(Long skuId, Integer num, String cartKey) {
        //1、拿到购物车
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(cartKey);

        //2、拿到商品
        CartInfo item = getItemFromCart(cartKey, skuId);
        item.setSkuNum(item.getSkuNum() + num);
        item.setUpdateTime(new Date());

        //3、保存到购物车
        hashOps.put(skuId.toString(), Jsons.toStr(item));
    }

    @Override
    public void updateChecked(Long skuId, Integer status, String cartKey) {
        //1、拿到购物车
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(cartKey);

        //2、拿到要修改的商品
        CartInfo item = getItemFromCart(cartKey, skuId);
        item.setIsChecked(status);
        item.setUpdateTime(new Date());
        //3、保存
        hashOps.put(skuId.toString(), Jsons.toStr(item));
    }

    @Override
    public void deleteCartItem(Long skuId, String cartKey) {
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(cartKey);

        hashOps.delete(skuId.toString());
    }

    /**
     * 删除商品
     *
     * @param cartKey
     */
    @Override
    public void deleteChecked(String cartKey) {

        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(cartKey);

        //1、拿到选中的商品，并删除。收集所有选中商品的id
        List<String> ids = getCheckedItems(cartKey).stream()
                .map(cartInfo -> cartInfo.getSkuId().toString())
                .collect(Collectors.toList());

        if (ids != null && ids.size() > 0) {
            hashOps.delete(ids.toArray());
        }
    }

    /**
     * 收集所有选中商品的id
     *
     * @param cartKey
     * @return
     */
    @Override
    public List<CartInfo> getCheckedItems(String cartKey) {
        List<CartInfo> cartList = getCartList(cartKey);
        List<CartInfo> checkedItems = cartList.stream()
                .filter(cartInfo -> cartInfo.getIsChecked() == 1)
                .collect(Collectors.toList());
        return checkedItems;
    }

    @Override
    public void mergeUserAndTempCart() {
        UserAuthInfo authInfo = AuthUtils.getCurrentAuthInfo();
        //1、判断是否需要合并
        if (authInfo.getUserId() != null && !StringUtils.isEmpty(authInfo.getUserTempId())) {
            //2、可能需要合并
            //3、临时购物车有东西。合并后删除临时购物车
            String tempCartKey = SysRedisConst.CART_KEY + authInfo.getUserTempId();
            //3.1、获取临时购物车中所有商品
            List<CartInfo> tempCartList = getCartList(tempCartKey);
            if (tempCartList != null && tempCartList.size() > 0) {
                //临时购物车有数据，需要合并
                String userCartKey = SysRedisConst.CART_KEY + authInfo.getUserId();
                for (CartInfo info : tempCartList) {
                    Long skuId = info.getSkuId();
                    Integer skuNum = info.getSkuNum();
                    addItemToCart(skuId, skuNum, userCartKey);
                    //39   200
                    //3.2、合并成一个商品就删除一个
                    redisTemplate.opsForHash().delete(tempCartKey, skuId.toString());
                }
            }
        }
    }

    @Override
    public void updateCartAllItemsPrice(String cartKey) {
        BoundHashOperations<String, String, String> cartOps =
                redisTemplate.boundHashOps(cartKey);

        System.out.println("更新价格启动：" + Thread.currentThread());

        cartOps.values().stream()
                .map(str -> Jsons.toObj(str, CartInfo.class))
                .forEach(cartInfo -> {
                    //1、查出最新价格  15ms
                    Result<BigDecimal> price = skuProductFeignClient.getSku1010Price(cartInfo.getSkuId());
                    //2、设置新价格
                    cartInfo.setSkuPrice(price.getData());
                    cartInfo.setUpdateTime(new Date());
                    //3、更新购物车价格  5ms
                    if (cartOps.hasKey(cartInfo.getSkuId().toString())) {
                        cartOps.put(cartInfo.getSkuId().toString(), Jsons.toStr(cartInfo));
                    }

                });
        System.out.println("更新价格结束：" + Thread.currentThread());
    }

    /**
     * 2.2 转为购物车中要保存的数据模型 把SkuInfo转为CartInfo
     *
     * @param data
     * @return
     */
    private CartInfo converSkuInfo2CartInfo(SkuInfo data) {

        CartInfo cartInfo = new CartInfo();

        cartInfo.setSkuId(data.getId());
        cartInfo.setImgUrl(data.getSkuDefaultImg());
        cartInfo.setSkuName(data.getSkuName());
        cartInfo.setIsChecked(1);
        cartInfo.setCreateTime(new Date());
        cartInfo.setUpdateTime(new Date());
        cartInfo.setSkuPrice(data.getPrice());
        cartInfo.setCartPrice(data.getPrice());

        return cartInfo;
    }
}