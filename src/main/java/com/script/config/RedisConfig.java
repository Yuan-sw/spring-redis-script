package com.script.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig
{

    @Value("${spring.redis.host}")
    private String host;

}