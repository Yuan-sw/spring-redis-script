package com.script;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@SpringBootTest(classes = {ScriptApplication.class})
public class RedisScriptTest
{
    @Resource(name = "redisTemplate")
    private RedisTemplate redisTemplate;

    @Test
    public void lockScriptTest()
    {
        List<String> keys = new ArrayList<String>()
        {
            {
                add("cop:shop:sku:stock:1");
                add("cop:shop:sku:stock:2");
            }
        };
        Object[] args = {1, 1};
        String script = "local keys = KEYS\n" +
                "local args = ARGV\n" +
                "-- 创建一个表保存所有的操作\n" +
                "local operations = {}\n" +
                "local incrValue = 0;\n" +
                "-- 返回的库存信息\n" +
                "local data = {}\n" +
                "-- 返回结果\n" +
                "local result = {}\n" +
                "\n" +
                "-- 遍历所有的key和对应的值减去可售库存\n" +
                "for i, key in ipairs(keys) do\n" +
                "    if incrValue < 0 then\n" +
                "        -- 任意一个扣减后的值小于0，剩余的就不再做扣减逻辑，只get库存值\n" +
                "        local currentValue = tonumber(redis.call('GET', key))\n" +
                "        table.insert(data, {key, currentValue})\n" +
                "    else\n" +
                "        local increment = tonumber(args[i])\n" +
                "        -- 执行扣减\n" +
                "        local currentValue = redis.call('INCRBY', key, -increment)\n" +
                "        --incrValue大于等于0的时候，每次都赋值，直到赋值到有负数\n" +
                "        incrValue = currentValue\n" +
                "        -- 将本次操作记录下来\n" +
                "        table.insert(operations, {key, increment})\n" +
                "        -- 记录一下当前key的扣减前的可售库存值\n" +
                "        table.insert(data, {key, increment + tonumber(currentValue)})\n" +
                "    end\n" +
                "end\n" +
                "result['data'] = data\n" +
                "\n" +
                "if incrValue < 0 then\n" +
                "    -- 如果有任何一个sku扣减后库存值小于0,则回滚所有已经执行的操作\n" +
                "    for _, op in ipairs(operations) do\n" +
                "        redis.call('INCRBY', op[1], op[2])\n" +
                "    end\n" +
                "    result['success'] = false\n" +
                "else\n" +
                "    result['success'] = true\n" +
                "end\n" +
                "\n" +
                "-- 所有操作都成功,返回true\n" +
                "return cjson.encode(result)";
        RedisScript<String> redisScript = RedisScript.of(script, String.class);
        Object executeResult = redisTemplate.execute(redisScript, keys, args);
        log.info("执行结果：{}", executeResult);
    }

    @Test
    public void lockScriptTest1()
    {
        List<String> keys = new ArrayList<String>()
        {
            {
                add("cop:shop:sku:stock:1");
                add("cop:shop:sku:stock:2");
            }
        };
        String script = "local keys = KEYS\n" +
                "local sum = 0\n" +
                "local results = {}\n" +
                "\n" +
                "for i, key in ipairs(keys) do\n" +
                "    local value = tonumber(redis.call('GET', key))\n" +
                "    table.insert(results, value)\n" +
                "    if value then\n" +
                "        sum = sum + value\n" +
                "    end\n" +
                "end\n" +
                "return {true, results}";
        RedisScript<Long> redisScript = RedisScript.of(script, Long.class);
        Object executeResult = redisTemplate.execute(redisScript, keys);

        log.info("执行结果：{}", executeResult);
    }

    @Test
    public void lockScriptTest2()
    {
        String script = "local result = {}\n" +
                "result['success'] = true\n" +
                "result['data'] = {1, 2, 3}\n" +
                "result['total'] = 6\n" +
                "return cjson.encode(result)";
        RedisScript<String> redisScript = RedisScript.of(script, String.class);
        Object executeResult = redisTemplate.execute(redisScript, Collections.singletonList(""));

        log.info("执行结果：{}", executeResult);
        JSONObject jsonObject = JSON.parseObject(executeResult.toString());
        log.info("执行结果json：{}", jsonObject.toJSONString());
    }
}
