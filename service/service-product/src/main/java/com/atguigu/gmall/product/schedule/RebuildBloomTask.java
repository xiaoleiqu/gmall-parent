package com.atguigu.gmall.product.schedule;

import com.atguigu.gmall.common.constant.SysRedisConst;
import com.atguigu.gmall.product.bloom.BloomDataQueryService;
import com.atguigu.gmall.product.bloom.BloomOpsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @author quxiaolei
 * @date 2022/9/1 - 19:25
 */

/**
 * 定时重建布隆任务
 */
@Service
public class RebuildBloomTask {

    @Autowired
    BloomOpsService bloomOpsService;

    @Autowired
    BloomDataQueryService bloomDataQueryService;

    /**
     * 每周3晚上，凌晨点执行一次定时任务
     */
    @Scheduled(cron = "0 0 3 ? * 3")
    public void rebuild() {
        bloomOpsService.rebuildBloom(SysRedisConst.BLOOM_SKUID, bloomDataQueryService);
    }

}
