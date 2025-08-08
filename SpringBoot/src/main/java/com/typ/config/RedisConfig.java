package com.typ.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.typ.handler.RedisErrorHandler;
import com.typ.listener.RedisMessageListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.time.Duration;

import static com.typ.constants.GatewayConfConstants.DEFAULT_KEY_CHAIN_FORMAT;
import static com.typ.constants.RedisConstants.APP_REGISTER_CHANNEL;
import static com.typ.constants.RedisConstants.UPDATE_GATEWAY_CONFIG_IN_MEMORY;


/**
 * redis配置类
 */
@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {


    static final int DEFAULT_KEY_CACHE_TTL = 30 * 60;    // 30min
    static final int KEY_CACHE_TTL = 10 * 60;    // 10min

    static final int CORE_POOL_SIZE = 10;

    static final int MAX_CORE_POOL_SIZE = 60;

    private static RedisTemplate<String, Object> redisTemplate;
    private static RedisMessageListenerContainer container;

    public static RedisTemplate getRedisTemplate() {
        if (redisTemplate == null) {
            throw new RuntimeException("redisTemplate初始化失败");
        }
        return redisTemplate;
    }

    private static String redisKeyPrefix;
    @Value("${spring.redis.prefix}")
    public void setRedisKeyPrefix(String param) {
        this.redisKeyPrefix = param;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        GenericToStringSerializer valueStringRedisSerializer = new GenericToStringSerializer(Object.class);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringRedisSerializer);
        template.setValueSerializer(valueStringRedisSerializer);

        // 设置hash key 和value序列化模式
        template.setHashKeySerializer(stringRedisSerializer);
        template.setHashValueSerializer(stringRedisSerializer);
        template.afterPropertiesSet();

        redisTemplate = template;
        return template;
    }

    public static RedisMessageListenerContainer getContainer() {
        if (container == null) {
            container = new RedisMessageListenerContainer();
            container.setConnectionFactory(redisTemplate.getConnectionFactory());
            ThreadPoolTaskExecutor subscriptionExecutor = new ThreadPoolTaskExecutor();
            subscriptionExecutor.setCorePoolSize(CORE_POOL_SIZE);
            subscriptionExecutor.setMaxPoolSize(MAX_CORE_POOL_SIZE);
            subscriptionExecutor.setThreadNamePrefix("redis-subscription-thread-");
            subscriptionExecutor.initialize();
            container.setSubscriptionExecutor(subscriptionExecutor);
        }
        return container;
    }


    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory,
                                                                       RedisMessageListener listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter, new PatternTopic(
                String.format(DEFAULT_KEY_CHAIN_FORMAT, redisKeyPrefix, UPDATE_GATEWAY_CONFIG_IN_MEMORY)));
        container.addMessageListener(listenerAdapter, new PatternTopic(
                String.format(DEFAULT_KEY_CHAIN_FORMAT, redisKeyPrefix, APP_REGISTER_CHANNEL)));
        container.setErrorHandler(new RedisErrorHandler());

        return container;
    }

    /**
     * 对hash类型的数据操作: 针对map类型的数据操作
     *
     * @param redisTemplate
     * @return
     */
    @Bean
    public HashOperations<String, String, Object> hashOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForHash();
    }

    /**
     * 对redis字符串类型数据操作： 简单K-V操作
     *
     * @param redisTemplate
     * @return
     */
    @Bean
    public ValueOperations<String, Object> valueOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForValue();
    }

    /**
     * 对链表类型的数据操作: 针对list类型的数据操作
     *
     * @param redisTemplate
     * @return
     */
    @Bean
    public ListOperations<String, Object> listOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForList();
    }

    /**
     * 对无序集合类型的数据操作： set类型数据操作
     *
     * @param redisTemplate
     * @return
     */
    @Bean
    public SetOperations<String, Object> setOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForSet();
    }

    /**
     * 对有序集合类型的数据操作 ：zset类型数据操作
     *
     * @param redisTemplate
     * @return
     */
    @Bean
    public ZSetOperations<String, Object> zSetOperations(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForZSet();
    }

    /**************** 注解缓存失效时间配置 *******************/
    /**
     * springboot2.x 设置redis缓存失效时间(注解)：
     *
     * @param redisConnectionFactory
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        return new RedisCacheManager(
                RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory),
                this.getRedisCacheConfigurationWithTtl(DEFAULT_KEY_CACHE_TTL) // 默认策略，未配置的 key 会使用这个
        );
    }

    private RedisCacheConfiguration getRedisCacheConfigurationWithTtl(Integer seconds) {
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);

        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
        redisCacheConfiguration = redisCacheConfiguration.serializeValuesWith(
                RedisSerializationContext
                        .SerializationPair
                        .fromSerializer(jackson2JsonRedisSerializer)
        ).entryTtl(Duration.ofSeconds(seconds));

        return redisCacheConfiguration;
    }
}
