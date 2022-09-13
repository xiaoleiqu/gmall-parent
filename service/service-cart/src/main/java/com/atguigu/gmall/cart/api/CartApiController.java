package com.atguigu.gmall.cart.api;


import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.auth.AuthUtils;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author quxiaolei
 * @date 2022/9/7 - 19:58
 */
@RequestMapping("/api/inner/rpc/cart")
@RestController
public class CartApiController {

    @Autowired
    CartService cartService;


    /**
     * 添加商品到购物车
     *
     * @param skuId
     * @param skuNum
     * @param
     * @return
     * @RequestHeader(value = SysRedisConst.USERID_HEADER, required = false) String userId,
     * @RequestHeader(value = SysRedisConst.USERTEMPID_HEADER, required = false) String userTempId)
     */
    @GetMapping("addToCart")
    public Result<SkuInfo> addToCart(@RequestParam("skuId") Long skuId,
                                     @RequestParam("skuNum") Integer skuNum) {

//        UserAuthInfo authInfo = AuthUtils.getCurrentAuthInfo();
//
//        System.out.println("service-cart 获取到的用户id:" + authInfo.getUserId());
//        System.out.println("service-cart 获取到的临时id:" + authInfo.getUserTempId());

        SkuInfo skuInfo = cartService.addToCart(skuId, skuNum);

        return Result.ok(skuInfo);
    }

    /**
     * 删除购物车中选中的商品
     *
     * @return
     */
    @GetMapping("/deleteChecked")
    public Result deleteChecked() {
        String cartKey = cartService.determinCartKey();
        cartService.deleteChecked(cartKey);
        return Result.ok();
    }

}
