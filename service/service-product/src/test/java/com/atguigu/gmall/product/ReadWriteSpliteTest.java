package com.atguigu.gmall.product;

import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.mapper.BaseTrademarkMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author quxiaolei
 * @date 2022/9/2 - 19:44
 */
@SpringBootTest
public class ReadWriteSpliteTest {


    @Autowired
    BaseTrademarkMapper baseTrademarkMapper;

    @Test
    public void testw() {
        BaseTrademark baseTrademark = baseTrademarkMapper.selectById(4L);
        System.out.println(baseTrademark);

        baseTrademark.setTmName("小米");

    }

    @Test
    public void testrw(){
        /**
         * 所有的负载均衡来到从库
         */
        BaseTrademark baseTrademark = baseTrademarkMapper.selectById(4L);
        System.out.println(baseTrademark);


        BaseTrademark baseTrademark1 = baseTrademarkMapper.selectById(4L);
        System.out.println(baseTrademark1);


        BaseTrademark baseTrademark2 = baseTrademarkMapper.selectById(4L);
        System.out.println(baseTrademark2);

        BaseTrademark baseTrademark3 = baseTrademarkMapper.selectById(4L);
        System.out.println(baseTrademark3);
    }

}
