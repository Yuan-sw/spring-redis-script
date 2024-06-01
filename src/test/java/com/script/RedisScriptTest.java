package com.script;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import com.script.constant.ScriptConstant;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest(classes = {ScriptApplication.class})
public class RedisScriptTest
{
    @Resource(name = "redisTemplate")
    private RedisTemplate redisTemplate;

    /**
     * 下单扣减库存
     */
    @Test
    public void frozenStockScriptTest()
    {
        List<String> keys = new ArrayList<String>()
        {
            {
                add("cop:shop:sku:stock:1");
                add("cop:shop:sku:stock:2");
            }
        };
        Object[] args = {1, 1};
        RedisScript<String> redisScript = RedisScript.of(ScriptConstant.FROZEN_STOCK_SCRIPT, String.class);
        Object executeResult = redisTemplate.execute(redisScript, keys, args);
        log.info("执行结果：{}", executeResult);
    }
}
