package com.script.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.script.domain.dto.CreateOrderDTO;
import com.script.domain.vo.R;
import com.script.service.StockOperateService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    private StockOperateService stockOperateService;

    @GetMapping("/log")
    public void log() {
        log.info("======接口请求======");
        stockOperateService.logTransfer();
    }

    /**
     * 正常下单锁定库存
     * 
     * @param dto
     * @return
     */
    @PostMapping("/frozenStock")
    public R frozenStock(@RequestBody CreateOrderDTO dto) {
        stockOperateService.frozenStock(dto);
        return R.ok();
    }

    /**
     * 模拟下单锁定库存成功，后续逻辑异常，执行冻结库存释放逻辑
     * 
     * @param dto
     * @return
     */
    @PostMapping("/releaseFrozenStock")
    public R releaseFrozenStock(@RequestBody CreateOrderDTO dto) {
        stockOperateService.releaseFrozenStock(dto);
        return R.ok();
    }

    /**
     * 支付成功后清理冻结库存
     * 
     * @param dto
     * @return
     */
    @PostMapping("/cleanFrozenStock")
    public R cleanFrozenStock(@RequestBody CreateOrderDTO dto) {
        stockOperateService.cleanFrozenStock(dto);
        return R.ok();
    }

    /**
     * 售后退款后加回可售库存/后台操作增加可售库存
     * 
     * @param dto
     * @return
     */
    @PostMapping("/addSaleStock")
    public R addSaleStock(@RequestBody CreateOrderDTO dto) {
        stockOperateService.addSaleStock(dto);
        return R.ok();
    }
}
