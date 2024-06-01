package com.script.controller;

import javax.annotation.Resource;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.script.constant.ScriptConstant;
import com.script.domain.dto.CreateOrderDTO;
import com.script.domain.vo.R;
import com.script.service.StockOperateService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    @Resource(name = "completeThreadExecutor")
    private ThreadPoolTaskExecutor executor;
    @Resource(name = "redisTemplate")
    private RedisTemplate redisTemplate;
    @Resource
    private StockOperateService StockOperateService;

    @GetMapping("/list")
    public void findOrderList() {
        log.info("======接口请求======");
        Object object = redisTemplate.opsForValue().get("cop:shop:sku:stock:1");
    }

    @PostMapping("/frozenStock")
    public R frozenStock(@RequestBody CreateOrderDTO dto) {
        StockOperateService.frozenStock(dto);
        return R.ok();
    }
}
