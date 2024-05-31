package com.script;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class ScriptApplication implements CommandLineRunner
{
    @Value("${spring.redis.host}")
    private String host;

    public static void main(String[] args)
    {
        SpringApplication.run(ScriptApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception
    {
        log.info("host:{}", host);
    }
}
