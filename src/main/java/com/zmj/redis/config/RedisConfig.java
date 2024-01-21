package com.zmj.redis.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.DefaultLettucePool;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePool;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * description:
 * 使用spring-data-redis
 * 连接池为lettuce
 * 包括lettuce连接池的配置以及Redistemplate和StringRedistemplate
 *
 * @author wkGui
 */
@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String hostname;
    @Value("${spring.data.redis.port}")
    private int port;
    @Value("${spring.data.redis.database}")
    private int dbIndex;
    @Value("${spring.data.redis.password}")
    private String password;
    @Value("${spring.data.redis.lettuce.pool.max-active}")
    private int maxTotal;
    @Value("${spring.data.redis.lettuce.pool.max-idle}")
    private int maxIdle;
    @Value("${spring.data.redis.lettuce.pool.min-idle}")
    private int minIdle;
    @Value("${spring.data.redis.lettuce.pool.max-wait}")
    private int maxWait;
    // @Value("${spring.data.redis.blockWhenExhausted}")
    private boolean isBlockWhenExhausted = true;

    // @Value("${spring.data.redis.testOnBorrow}")
    private boolean isTestOnBorrow = true;
    // @Value("${spring.data.redis.testOnReturn}")
    private boolean isTestOnReturn = true;
    // @Value("${spring.data.redis.testWhileIdle}")
    private boolean isTestWhileIdle = true;
    //  @Value("${spring.data.redis.timeBetweenEvictionRunsMillis}")
    private int timeBetweenEvictionRunsMillis = 30000;
    // @Value("${spring.data.redis.minEvictableIdleTimeMillis}")
    private int minEvictableIdleTimeMillis = 180000;


    @Bean
    public StringRedisSerializer getStringRedisSerializer() {
        return new StringRedisSerializer();
    }

    /**
     * 配置连接池参数
     *
     * @return GenericObjectPool
     */
    @Bean
    public GenericObjectPoolConfig getRedisConfig() {
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxIdle(maxIdle);
        genericObjectPoolConfig.setMaxTotal(maxTotal);
        genericObjectPoolConfig.setMinIdle(minIdle);
        //连接耗尽时是否阻塞, false报异常,ture阻塞直到超时, 默认true
        genericObjectPoolConfig.setBlockWhenExhausted(isBlockWhenExhausted);
        genericObjectPoolConfig.setMaxWaitMillis(maxWait);
        //在borrow一个实例时，是否提前进行alidate操作；如果为true，则得到的实例均是可用的
        genericObjectPoolConfig.setTestOnBorrow(isTestOnBorrow);
        //调用returnObject方法时，是否进行有效检查
        genericObjectPoolConfig.setTestOnReturn(isTestOnReturn);
        //在空闲时检查有效性, 默认false
        genericObjectPoolConfig.setTestWhileIdle(isTestWhileIdle);
        //表示idle object evitor两次扫描之间要sleep的毫秒数；
        genericObjectPoolConfig.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        //表示一个对象至少停留在idle状态的最短时间，
        //然后才能被idle object evitor扫描并驱逐；这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义；
        genericObjectPoolConfig.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);

        return genericObjectPoolConfig;
    }

    /**
     * 生成连接池
     *
     * @param poolConfig 连接池配置
     * @return DefaultLettucePool
     */
    @Bean
    public DefaultLettucePool getDefaultLettucePool(GenericObjectPoolConfig poolConfig) {
        DefaultLettucePool defaultLettucePool = new DefaultLettucePool(hostname, port, poolConfig);
        defaultLettucePool.setPassword(password);
        defaultLettucePool.afterPropertiesSet();
        return defaultLettucePool;
    }

    /**
     * lettuce 连接工厂配置
     *
     * @return LettuceConnectionFactory implement RedisConnectionFactory
     */
    @Bean
    public LettuceConnectionFactory getLettuceConnectionFactory(LettucePool pool) {
        LettuceConnectionFactory factory = new LettuceConnectionFactory(pool);
        //校验连接是否有效
        factory.setValidateConnection(true);
        //选择数据库
        factory.setDatabase(dbIndex);
        factory.setTimeout(maxWait);
        factory.afterPropertiesSet();
        return factory;
    }

    @Bean(name = "redisTemplate")
    public RedisTemplate<String, Object> getRedisTemplate(LettuceConnectionFactory factory, StringRedisSerializer stringRedisSerializer) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        //对key采用String的序列化方式--统一
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        //事务支持
        redisTemplate.setEnableTransactionSupport(true);
        redisTemplate.setConnectionFactory(factory);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer());
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer());
        return redisTemplate;
    }


    private Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer(){
        Jackson2JsonRedisSerializer<Object> jsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        jsonRedisSerializer.setObjectMapper(objectMapper);
        return jsonRedisSerializer;
    }

    @Bean(name = "stringRedisTemplate")
    public StringRedisTemplate getStringRedisTemplate(RedisConnectionFactory factory, StringRedisSerializer stringRedisSerializer) {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        //对key采用String的序列化方式--统一
        stringRedisTemplate.setKeySerializer(stringRedisSerializer);
        //事务支持
        stringRedisTemplate.setEnableTransactionSupport(true);
        stringRedisTemplate.setConnectionFactory(factory);
        return stringRedisTemplate;
    }


    /**
     * 缓存管理器 使用redisTemplate操作
     */
    @Bean
    public RedisCacheManager redisCacheManager(LettuceConnectionFactory redisConnectionFactory) {
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory);
        RedisSerializationContext.SerializationPair<Object> pair = RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer());
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(pair).entryTtl(Duration.ofSeconds(10));
        return new RedisCacheManager(redisCacheWriter, defaultCacheConfig);
    }
}

