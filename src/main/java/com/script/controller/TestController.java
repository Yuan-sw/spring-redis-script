package com.script.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.toolkit.trace.RunnableWrapper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/test")
public class TestController
{

    @Resource(name = "completeThreadExecutor")
    private ThreadPoolTaskExecutor executor;

    @GetMapping("/list")
    public void findOrderList()
    {
        log.info("======接口请求======");
        executor.execute(RunnableWrapper.of(() ->
        {
            log.info("======子线程=======");
        }));
    }
}
