package com.script.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.skywalking.apm.toolkit.trace.RunnableWrapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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
    @Resource(name = "completeThreadExecutor")
    private ThreadPoolTaskExecutor executor;

    @Override
    public void logTransfer() {
        log.info("======主线程日志======");
        executor.execute(RunnableWrapper.of(() -> {
            log.info("======子线程日志======");
        }));
    }

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
            boolean anyMatch = result.getData().stream().anyMatch(item -> item.getAfterChangeSaleStock() < 0);
            if (anyMatch) {
                log.info("库存异常了：{}", JSON.toJSONString(result.getData()));
            }
        }
    }

    @Override
    public void releaseFrozenStock(CreateOrderDTO dto) {
        List<OrderSkuDTO> skus = dto.getSkus();
        Object[] nums = new Object[skus.size()];

        List<String> keys = new ArrayList<>();
        for (int i = 0; i < skus.size(); i++) {
            OrderSkuDTO item = skus.get(i);
            keys.add("cop:shop:sku:stock:" + item.getSkuId());
            nums[i] = item.getNum();
        }
        try {
            RedisScript<String> redisScript = RedisScript.of(ScriptConstant.FROZEN_STOCK_SCRIPT, String.class);
            Object executeResult = redisTemplate.execute(redisScript, keys, nums);
            ScriptResult result = JSON.parseObject(executeResult.toString(), ScriptResult.class);
            if (result.ok()) {
                // 扣减后可售库存有负值的打印一下日志
                boolean anyMatch = result.getData().stream().anyMatch(item -> item.getAfterChangeSaleStock() < 0);
                if (anyMatch) {
                    log.info("下单锁定库存异常了：{}", JSON.toJSONString(result.getData()));
                }
            }
        } catch (Exception e) {
            for (int i = 0; i < nums.length; i++) {
                nums[i] = -Integer.valueOf(nums[i].toString());
            }
            RedisScript<String> redisScript = RedisScript.of(ScriptConstant.FROZEN_STOCK_SCRIPT, String.class);
            Object executeResult = redisTemplate.execute(redisScript, keys, nums);
            ScriptResult result = JSON.parseObject(executeResult.toString(), ScriptResult.class);
            if (result.ok()) {
                // 释放后冻结库存有负值的，打印一下日志
                boolean anyMatch = result.getData().stream().anyMatch(item -> item.getAfterChangeFrozenStock() < 0);
                if (anyMatch) {
                    log.info("释放冻结库存的时候有异常数据：{}", JSON.toJSONString(result.getData()));
                }
            }
        }
    }

    @Override
    public void cleanFrozenStock(CreateOrderDTO dto) {
        List<OrderSkuDTO> skus = dto.getSkus();
        Object[] nums = new Object[skus.size()];

        List<String> keys = new ArrayList<>();
        for (int i = 0; i < skus.size(); i++) {
            OrderSkuDTO item = skus.get(i);
            keys.add("cop:shop:sku:stock:" + item.getSkuId());
            nums[i] = item.getNum();
        }

        RedisScript<String> redisScript = RedisScript.of(ScriptConstant.CLEAN_FROZEN_STOCK_SCRIPT, String.class);
        Object executeResult = redisTemplate.execute(redisScript, keys, nums);
        ScriptResult result = JSON.parseObject(executeResult.toString(), ScriptResult.class);
        if (result.ok()) {
            // 清理后冻结库存有负值的，打印一下日志
            boolean anyMatch = result.getData().stream().anyMatch(item -> item.getAfterChangeFrozenStock() < 0);
            if (anyMatch) {
                log.info("清理冻结库存的时候有异常数据：{}", JSON.toJSONString(result.getData()));
            }
        }
    }

    @Override
    public void addSaleStock(CreateOrderDTO dto) {
        List<OrderSkuDTO> skus = dto.getSkus();
        Object[] nums = new Object[skus.size()];

        List<String> keys = new ArrayList<>();
        for (int i = 0; i < skus.size(); i++) {
            OrderSkuDTO item = skus.get(i);
            keys.add("cop:shop:sku:stock:" + item.getSkuId());
            nums[i] = item.getNum();
        }

        RedisScript<String> redisScript = RedisScript.of(ScriptConstant.ADD_SALE_STOCK_SCRIPT, String.class);
        Object executeResult = redisTemplate.execute(redisScript, keys, nums);
        ScriptResult result = JSON.parseObject(executeResult.toString(), ScriptResult.class);
        if (result.ok()) {
            // 加库存，成功就返回成功，没成功就提示异常，异常的场景很少
        }
    }
}
