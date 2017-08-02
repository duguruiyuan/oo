/**
 * 
 */
package com.slfinance.shanlincaifu.cfg;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.cache.interceptor.SimpleCacheResolver;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.Maps;

@Configuration
@EnableCaching
public class CachingConfig implements CachingConfigurer {

	@Autowired
	RedisOperations<Object, Object> redisTemplate;

	@Autowired
	RedisConnectionFactory redisConnectionFactory;

	@Bean(name = "ehcache")
	@Override
	public CacheManager cacheManager() {
		EhCacheCacheManager cacheManager = new EhCacheCacheManager();
		cacheManager.setCacheManager(ehCacheManagerFactoryBean().getObject());
		return cacheManager;
	}

	@Bean(name = "redis")
	public CacheManager redisCacheManger() {
		RedisCacheManager cacheManager = new RedisCacheManager(
				(RedisTemplate<Object, Object>) redisTemplate);
		cacheManager.setUsePrefix(true);
		Map<String, Long> expires = Maps.newHashMap();
		expires.put("slcf_afficheCount", 86000L);
		cacheManager.setExpires(expires);
		return cacheManager;
	}

	@Bean
	@Override
	public KeyGenerator keyGenerator() {
		return new SimpleKeyGenerator();
	}

	@Bean
	public EhCacheManagerFactoryBean ehCacheManagerFactoryBean() {
		EhCacheManagerFactoryBean ehCacheManagerFactoryBean = new EhCacheManagerFactoryBean();
		ehCacheManagerFactoryBean.setConfigLocation(new ClassPathResource(
				"ehcache.xml"));
		ehCacheManagerFactoryBean.setCacheManagerName("mp");
		ehCacheManagerFactoryBean.setShared(true);
		return ehCacheManagerFactoryBean;
	}

	@Override
	public CacheResolver cacheResolver() {
		return new SimpleCacheResolver(cacheManager());
	}

	@Override
	public CacheErrorHandler errorHandler() {
		return new SimpleCacheErrorHandler();
	}

	@Bean
	public RedisTemplate<String, Object> jsonSerializerRedisTemplate(
			GenericJackson2JsonRedisSerializer objectSerializer) {
		RedisTemplate<String, Object> redis = new RedisTemplate<String, Object>();
		redis.setConnectionFactory(redisConnectionFactory);
		// explicitly enable transaction support
		// redis.setEnableTransactionSupport(true);
		RedisSerializer<String> stringSerializer = new StringRedisSerializer();

		redis.setKeySerializer(stringSerializer);
		redis.setValueSerializer(objectSerializer);
		redis.setHashKeySerializer(stringSerializer);
		redis.setHashValueSerializer(objectSerializer);
		return redis;
	}

	@Bean
	public GenericJackson2JsonRedisSerializer redisSerializer() {
		ObjectMapper om = new ObjectMapper();
		om.setVisibility(PropertyAccessor.ALL, Visibility.ANY);
		om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		om.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		return new GenericJackson2JsonRedisSerializer(om);
	}
}
