package com.slfinance.shanlincaifu.limits;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.slfinance.annotation.IPRequestLimits;
import com.slfinance.annotation.MobileRequestLimits;
import com.slfinance.annotation.RequestLimits;
import com.slfinance.annotation.UserRequestLimits;
import com.slfinance.util.LoadPackageClasses;

@Slf4j
public class RequestLimitsManager {
	
	public static final int DAILY_IDX = 0;
	public static final int MONTHLY_IDX = 1;
	public static final int RATE_IDX = 2;
	public static final int RATE_LIMITER_IDX = 3;
	
	public static Table<String, Integer, Object> aTable = HashBasedTable.create();  
	
	public static final Table<String, Integer, Object> userLimitsCache = HashBasedTable.create();
	
	public static final Table<String, Integer, Object> ipLimitsCache  = HashBasedTable.create();
	
	public static final Table<String, Integer, Object>  mobileLimitsCache  = HashBasedTable.create();
	
	
	public RequestLimitsManager() {
		this(new String[]{"com.slfinance.service"});
	}
	
	public RequestLimitsManager(String[] pkgs) {
		try {
			scan(pkgs);
		} catch (Exception e) {
			log.info("[Shanlinbao] scan requestLimits error!");
		}
	}
	
	public RequestLimitsManager scan(String[] pkgs) throws Exception {
		Preconditions.checkNotNull(pkgs);
		
		getMethodSet(getClassSet(pkgs));
		return this;
	}
	
	private static Set<Class<?>> getClassSet(final String... pkgs) throws IOException, ClassNotFoundException {
		Set<Class<?>> classSet = new HashSet<Class<?>>();
		Preconditions.checkNotNull(pkgs);
		ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
		for (String pkg : pkgs) {
			String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
							+ ClassUtils.convertClassNameToResourcePath(pkg)
							+ LoadPackageClasses.RESOURCE_PATTERN;
			Resource[] resources = resourcePatternResolver.getResources(pattern);
			MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
			for (Resource resource : resources) {
				if (resource.isReadable()) {
					MetadataReader reader = readerFactory.getMetadataReader(resource);
					String className = reader.getClassMetadata().getClassName();
					classSet.add(Class.forName(className));
				}
			}
		}
		return classSet;
	}
	
	private static void getMethodSet(final Set<Class<?>> clz) throws IOException, ClassNotFoundException {
		Preconditions.checkNotNull(clz);
		for(Class<?> cls: clz) {
			Method[] methods = cls.getDeclaredMethods();
			for(Method method: methods) {
				RequestLimits limit = method.getAnnotation(RequestLimits.class);
				String key = cls.getSimpleName().concat(".").concat(method.getName());
				if(limit != null) {
					aTable.put(key, DAILY_IDX, limit.daily());
					aTable.put(key, MONTHLY_IDX, limit.monthly());
					aTable.put(key, RATE_IDX, limit.rate());
					aTable.put(key, RATE_LIMITER_IDX, CacheBuilder
					          .newBuilder()
					          .maximumSize(10000)
					          .expireAfterWrite(1, TimeUnit.DAYS)
					          .build());
				}
				
				UserRequestLimits userLimits = method.getAnnotation(UserRequestLimits.class);
				if(userLimits != null) {
					userLimitsCache.put(key, DAILY_IDX, userLimits.daily());
					userLimitsCache.put(key, MONTHLY_IDX, userLimits.monthly());
					userLimitsCache.put(key, RATE_IDX, userLimits.rate());
					userLimitsCache.put(key, RATE_LIMITER_IDX,  CacheBuilder
					          .newBuilder()
					          .maximumSize(10000)
					          .expireAfterWrite(1, TimeUnit.DAYS)
					          .build());
				}
				
				IPRequestLimits ipLimits = method.getAnnotation(IPRequestLimits.class);
				if(ipLimits != null) {
					ipLimitsCache.put(key, DAILY_IDX, ipLimits.daily());
					ipLimitsCache.put(key, MONTHLY_IDX, ipLimits.daily());
					ipLimitsCache.put(key, RATE_IDX, ipLimits.rate());
					ipLimitsCache.put(key, RATE_LIMITER_IDX, CacheBuilder
					          .newBuilder()
					          .maximumSize(10000)
					          .expireAfterWrite(1, TimeUnit.DAYS)
					          .build());
				}
				
				MobileRequestLimits mobileLimits=method.getAnnotation(MobileRequestLimits.class);
				if(mobileLimits!=null){
					mobileLimitsCache.put(key, DAILY_IDX, userLimits.daily());
					mobileLimitsCache.put(key, MONTHLY_IDX, userLimits.monthly());
					mobileLimitsCache.put(key, RATE_IDX, userLimits.rate());
					mobileLimitsCache.put(key, RATE_LIMITER_IDX, CacheBuilder
					          .newBuilder()
					          .maximumSize(10000)
					          .expireAfterWrite(1, TimeUnit.DAYS)
					          .build());
				}
			}
		}
	}
}
