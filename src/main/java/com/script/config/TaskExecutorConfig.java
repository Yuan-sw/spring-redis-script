package com.script.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @program： COP(官方商城)
 * @description: 线程池配置
 * @author: 弥彦
 * @date: 2023/07/27
 * @company: 深圳减字科技有限公司
 */
@Slf4j
@Configuration
public class TaskExecutorConfig
{

    @Value("${spring.application.name:app}")
    private String appName;

    @Value("${thread.pool.core.pool.size:10}")
    private Integer corePoolSize;

    @Value("${thread.pool.max.pool.size:20}")
    private Integer maxPoolSize;

    @Value("${thread.pool.keep.alive.second:10}")
    private Integer keepAliveSecond;

    @Value("${thread.pool.queue.capacity:200}")
    private Integer queueCapacity;

    @Bean("completeThreadExecutor")
    public ThreadPoolTaskExecutor threadExecutor()
    {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix(appName);
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setKeepAliveSeconds(keepAliveSecond);
        executor.setQueueCapacity(queueCapacity);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        log.info("TaskExecutorConfig#threadExecutor 线程池初始化成功！");
        return executor;
    }
}
