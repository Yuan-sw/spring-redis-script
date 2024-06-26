package com.script.config;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.FileCopyUtils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.script.constant.ScriptConstant;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class RedisConfig implements CommandLineRunner {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.password}")
    private String password;

    @Value("${spring.redis.database}")
    private int db;

    @Value("${spring.redis.timeout}")
    private int timeout;

    @Bean(name = "redisTemplate")
    @SuppressWarnings(value = { "unchecked", "rawtypes" })
    public RedisTemplate<Object, Object> redisTemplate() {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(getFactory(db));

        FastJson2JsonRedisSerializer serializer = new FastJson2JsonRedisSerializer(Object.class);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY);
        serializer.setObjectMapper(mapper);

        // 使用StringRedisSerializer来序列化和反序列化redis的key值
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);

        // Hash的key也采用StringRedisSerializer的序列化方式
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }

    private RedisConnectionFactory getFactory(int database) {
        // 构建工厂对象
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(host);
        config.setPort(port);
        config.setPassword(RedisPassword.of(password));
        LettucePoolingClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
                .commandTimeout(Duration.ofSeconds(timeout))
                .build();
        LettuceConnectionFactory factory = new LettuceConnectionFactory(config, clientConfig);
        // 设置使用的redis数据库
        factory.setDatabase(database);
        // 重新初始化工厂
        factory.afterPropertiesSet();
        return factory;
    }

    @Override
    public void run(String... args) throws Exception {
        ClassPathResource resource = null;
        Reader reader = null;
        try {
            log.info("项目启动加载lua脚本文件");
            resource = new ClassPathResource("script\\frozenStockScript.txt");
            reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
            ScriptConstant.FROZEN_STOCK_SCRIPT = FileCopyUtils.copyToString(reader);
            reader.close();

            resource = new ClassPathResource("script\\cleanFrozenStockScript.txt");
            reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
            ScriptConstant.CLEAN_FROZEN_STOCK_SCRIPT = FileCopyUtils.copyToString(reader);
            reader.close();

            resource = new ClassPathResource("script\\addSaleStockScript.txt");
            reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
            ScriptConstant.ADD_SALE_STOCK_SCRIPT = FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            log.error("项目启动时加载lua脚本文件失败:{}", e);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }
}
