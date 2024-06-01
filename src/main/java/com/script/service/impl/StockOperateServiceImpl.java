package com.script.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.script.constant.ScriptConstant;
import com.script.domain.dto.CreateOrderDTO;
import com.script.domain.dto.OrderSkuDTO;
import com.script.domain.res.ScriptResult;
import com.script.service.StockOperateService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class StockOperateServiceImpl implements StockOperateService {

    @Resource(name = "redisTemplate")
    private RedisTemplate redisTemplate;

    @Override
    public void frozenStock(CreateOrderDTO dto) {
        List<OrderSkuDTO> skus = dto.getSkus();
        Object[] nums = new Object[skus.size()];

        List<String> keys = new ArrayList<>();
        for (int i = 0; i < skus.size(); i++) {
            OrderSkuDTO item = skus.get(i);
            keys.add("cop:shop:sku:stock:" + item.getSkuId());
            nums[i] = item.getNum();
        }

        RedisScript<String> redisScript = RedisScript.of(ScriptConstant.FROZEN_STOCK_SCRIPT, String.class);
        Object executeResult = redisTemplate.execute(redisScript, keys, nums);
        ScriptResult result = JSON.parseObject(executeResult.toString(), ScriptResult.class);
        if (result.ok()) {
            // 扣减成功的，打印一下日志
            boolean anyMatch = result.getData().stream().anyMatch(item -> item.getAfterChangeStock() < 0);
            if (anyMatch) {
                log.info("库存异常了：{}", JSON.toJSONString(result.getData()));
            }
        }
    }

}
