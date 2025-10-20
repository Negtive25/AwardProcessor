package org.com.code.certificateProcessor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    /**
     * 创建通用的 RedisTemplate 配置方法
     */
    private <K, V> RedisTemplate<K, V> createRedisTemplate(
            RedisConnectionFactory redisConnectionFactory,
            RedisSerializer<K> keySerializer,
            RedisSerializer<V> valueSerializer) {
        RedisTemplate<K, V> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 设置 key 和 hashKey 的序列化方式
        redisTemplate.setKeySerializer(keySerializer);
        redisTemplate.setHashKeySerializer(keySerializer);

        // 设置 value 和 hashValue 的序列化方式
        redisTemplate.setValueSerializer(valueSerializer);
        redisTemplate.setHashValueSerializer(valueSerializer);

        // 初始化 RedisTemplate
        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }

    @Bean("objRedisTemplate")
    public RedisTemplate<String, Object> objectRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        return createRedisTemplate(
                redisConnectionFactory,
                new StringRedisSerializer(),
                new GenericJackson2JsonRedisSerializer()
        );
    }

    /**
     * 专门用于存储和读取二进制数据（如序列化后的向量）的RedisTemplate
     */
    @Bean("redisTemplateByteArray")
    public RedisTemplate<String, byte[]> redisTemplateByteArray(RedisConnectionFactory redisConnectionFactory) {

        return createRedisTemplate(
                redisConnectionFactory,
                new StringRedisSerializer(),
                RedisSerializer.byteArray()
        );
    }
}
