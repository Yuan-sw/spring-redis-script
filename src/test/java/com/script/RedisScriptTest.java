package com.script;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

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
        String script = "local keys = KEYS\n" + //
                        "local args = ARGV\n" + //
                        "-- 创建一个表保存所有的操作\n" + //
                        "local saleStockOperations = {}\n" + //
                        "local frozenStockOperations = {}\n" + //
                        "local incrValue = 0;\n" + //
                        "-- 返回的库存信息\n" + //
                        "local data = {}\n" + //
                        "-- 返回结果\n" + //
                        "local result = {}\n" + //
                        "\n" + //
                        "-- 遍历所有的key和对应的值减去可售库存\n" + //
                        "for i, key in ipairs(keys) do\n" + //
                        "    local saleStockKey = key .. ':saleStock'\n" + //
                        "    local frozenStockKey = key .. ':frozenStock'\n" + //
                        "    local currentResValue = {}\n" + //
                        "    currentResValue['key'] = key\n" + //
                        "    if incrValue < 0 then\n" + //
                        "        -- 任意一个扣减后的值小于0，剩余的就不再做扣减逻辑，只get库存值\n" + //
                        "        local currentSaleValue = 0;\n" + //
                        "        local value = redis.call('GET', saleStockKey)\n" + //
                        "        if value then \n" + //
                        "            currentSaleValue = tonumber(value)\n" + //
                        "        end\n" + //
                        "        currentResValue['beforeChangeStock'] = currentSaleValue\n" + //
                        "        currentResValue['changeStock'] = 0\n" + //
                        "        currentResValue['afterChangeStock'] = currentSaleValue\n" + //
                        "        table.insert(data, currentResValue)\n" + //
                        "    else\n" + //
                        "        local increment = tonumber(args[i])\n" + //
                        "        -- 执行扣减可售库存\n" + //
                        "        local currentSaleValue = redis.call('DECRBY', saleStockKey, increment)\n" + //
                        "        -- 执行增加冻结库存\n" + //
                        "        local currentFrezenValue = redis.call('INCRBY', frozenStockKey, increment)\n" + //
                        "        --incrValue大于等于0的时候，每次都赋值，直到赋值到有负数\n" + //
                        "        incrValue = currentSaleValue\n" + //
                        "        -- 将本次操作记录下来\n" + //
                        "        table.insert(saleStockOperations, {saleStockKey, increment})\n" + //
                        "        table.insert(frozenStockOperations, {frozenStockKey, increment})\n" + //
                        "        -- 记录一下当前key的扣减前的可售库存值\n" + //
                        "        currentResValue['beforeChangeStock'] = increment + tonumber(currentSaleValue)\n" + //
                        "        -- 记录一下当前操作扣改变的库存值和改编后的库存之，改编后如果是负值，就认为没有发生改变，下面的逻辑会回滚\n" + //
                        "        if currentSaleValue >= 0 then\n" + //
                        "            currentResValue['changeStock'] = -increment\n" + //
                        "            currentResValue['afterChangeStock'] = currentSaleValue\n" + //
                        "        else\n" + //
                        "            currentResValue['changeStock'] = 0\n" + //
                        "            currentResValue['afterChangeStock'] = increment + tonumber(currentSaleValue)\n" + //
                        "        end\n" + //
                        "        table.insert(data, currentResValue)\n" + //
                        "    end\n" + //
                        "end\n" + //
                        "result['data'] = data\n" + //
                        "\n" + //
                        "if incrValue < 0 then\n" + //
                        "    -- 如果有任何一个sku扣减后库存值小于0,则回滚所有已经扣减过的可售库存\n" + //
                        "    for _, op in ipairs(saleStockOperations) do\n" + //
                        "        redis.call('INCRBY', op[1], op[2])\n" + //
                        "    end\n" + //
                        "    -- 回滚所有加上来的冻结库存\n" + //
                        "    for _, op in ipairs(frozenStockOperations) do\n" + //
                        "       redis.call('DECRBY', op[1], op[2])\n" + //
                        "    end\n" + //
                        "    result['success'] = false\n" + //
                        "else\n" + //
                        "    result['success'] = true\n" + //
                        "end\n" + //
                        "\n" + //
                        "-- 所有操作都成功,返回true\n" + //
                        "return cjson.encode(result)";
        RedisScript<String> redisScript = RedisScript.of(script, String.class);
        Object executeResult = redisTemplate.execute(redisScript, keys, args);
        log.info("执行结果：{}", executeResult);
    }
}
