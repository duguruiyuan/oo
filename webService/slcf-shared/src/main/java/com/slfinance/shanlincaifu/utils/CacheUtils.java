package com.slfinance.shanlincaifu.utils;

import java.util.List;

import org.apache.commons.collections.ListUtils;
import org.springframework.cache.ehcache.EhCacheCacheManager;

import com.slfinance.spring.ServiceLocator;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;



/**
 * Cache工具类
 * @author Ric.w
 * @version 2015-10-24
 */
public class CacheUtils {
	
	private static CacheManager cacheManager = ((EhCacheCacheManager)ServiceLocator.getService("ehcache")).getCacheManager();
	
	/**
	 * 获取缓存
	 * @param cacheName
	 * @param key
	 * @return
	 */
	public static Object get(String cacheName, String key) {
		Element element = getCache(cacheName).get(key);
		return element==null?null:element.getObjectValue();
	}

	/**
	 * 获取缓存
	 * @param cacheName
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getKeys(String cacheName) {
		Cache cache = getCache(cacheName);
		List<String> keys = cache.getKeys();
		return keys == null? ListUtils.EMPTY_LIST:keys;
	}

	/**
	 * 写入缓存
	 * @param cacheName
	 * @param key
	 * @param value
	 */
	public static void put(String cacheName, String key, Object value) {
		Element element = new Element(key, value);
		getCache(cacheName).put(element);
	}

	/**
	 * 从缓存中移除
	 * @param cacheName
	 * @param key
	 */
	public static void remove(String cacheName, String key) {
		getCache(cacheName).remove(key);
	}
	
	/**
	 * 删除缓存中所有缓存
	 * @param cacheName
	 * @param key
	 */
	public static void removeAll(String cacheName) {
		getCache(cacheName).removeAll();
	}
	
	/**
	 * 获得一个Cache，没有则创建一个。
	 * @param cacheName
	 * @return
	 */
	private static Cache getCache(String cacheName){
		Cache cache = cacheManager.getCache(cacheName);
		if (cache == null){
			cacheManager.addCache(cacheName);
			cache = cacheManager.getCache(cacheName);
			cache.getCacheConfiguration().setEternal(true);
		}
		return cache;
	}

	public static CacheManager getCacheManager() {
		return cacheManager;
	}
	
}
