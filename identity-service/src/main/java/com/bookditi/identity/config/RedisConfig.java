package com.bookditi.identity.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
@EnableCaching
public class RedisConfig {
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName("redis-10405.c262.us-east-1-3.ec2.redns.redis-cloud.com");
        config.setPort(10405);
        config.setUsername("default");
        config.setPassword(RedisPassword.of("FQHBx4ExYvxQeTsoamok0Qjom9YEdaDd"));
        return new LettuceConnectionFactory(config);
    }
}