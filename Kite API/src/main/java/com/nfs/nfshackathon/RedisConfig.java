package com.nfs.nfshackathon;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

@Slf4j
@Configuration
public class RedisConfig {

    @Value("${spring.redis.addr:}")
    private String addr;

    @Value("${spring.redis.port:}")
    private int port;

    @Value("${spring.redis.maxTotal:20}")
    private Integer maxTotal;

    @Value("${spring.redis.minIdle:5}")
    private Integer minIdle;

    @Value("${spring.redis.maxIdle:5}")
    private Integer maxIdle;

    @Bean
    StringRedisSerializer stringRedisSerializer() {
        return new StringRedisSerializer();
    }

    @Bean
    GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }

    @Bean
    JedisConnectionFactory redisStandaloneConnectionFactory() {
        val redisStandaloneConfiguration = new RedisStandaloneConfiguration(addr, port);

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(maxTotal);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setMaxIdle(maxIdle);

        return new JedisConnectionFactory(
                redisStandaloneConfiguration,
                JedisClientConfiguration.builder().usePooling().poolConfig(poolConfig).build());
    }

    @Bean
    RedisTemplate<String, Object> redisTemplate(JedisConnectionFactory redisConnectionFactory) {
        final RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(stringRedisSerializer());
        template.setDefaultSerializer(jackson2JsonRedisSerializer());
        template.setHashKeySerializer(new GenericJackson2JsonRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    @Bean
    @Qualifier("IntegerRedisTemplate")
    RedisTemplate<String, Integer> integerRedisTemplate(
            JedisConnectionFactory redisConnectionFactory) {
        final RedisTemplate<String, Integer> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(stringRedisSerializer());
        template.setDefaultSerializer(jackson2JsonRedisSerializer());
        template.setHashKeySerializer(new GenericJackson2JsonRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}