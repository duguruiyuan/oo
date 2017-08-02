package com.slfinance.shanlincaifu.component;

import static com.slfinance.shanlincaifu.limits.RequestLimitsManager.DAILY_IDX;
import static com.slfinance.shanlincaifu.limits.RequestLimitsManager.MONTHLY_IDX;
import static com.slfinance.shanlincaifu.limits.RequestLimitsManager.RATE_IDX;
import static com.slfinance.shanlincaifu.limits.RequestLimitsManager.RATE_LIMITER_IDX;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import com.google.common.cache.Cache;
import com.google.common.collect.Table;
import com.google.common.util.concurrent.RateLimiter;
import com.slfinance.annotation.AutoDispatch;
import com.slfinance.exception.SLException;
import com.slfinance.exception.SLSystemException;
import com.slfinance.shanlincaifu.exception.SLBException;
import com.slfinance.shanlincaifu.limits.RequestLimitsManager;
import com.slfinance.util.MetaDataManager;
import com.slfinance.vo.ResultVo;

/**
 * 日志切面 默认在Controller的所有方法生效
 * 
 * @author larry
 *
 */
@Aspect
@Component
public class WebLayerAspect {

	@Autowired
	MetaDataManager metaDataManager;
	
	@Autowired
	private StringRedisTemplate redisTemplate;
	
	@SuppressWarnings("unchecked")
	@Around("com.slfinance.shanlincaifu.SystemArchitecture.inWebLayer()")
	public Object log(ProceedingJoinPoint pjp) throws Throwable {
		//RateLimiter
		Object result = null;
		Throwable error = null;
		Class<?> type = pjp.getTarget().getClass();
		MethodSignature ms = (MethodSignature) pjp.getSignature();
		Method method = ms.getMethod();
		Object[] args = pjp.getArgs();
		Logger logger = Logger.getLogger(this.getClass());
		StopWatch sw = new StopWatch(getClass().getSimpleName());
		
		sw.start(ms.getName());
		String rowKey = "", userKey="", ipKey="", mobileKey="";
		try {
			if(ms.getName().contains("dipatch")) {
				AutoDispatch annotation = pjp.getTarget().getClass().getAnnotation(AutoDispatch.class);
				Class<?>[] services = annotation.serviceInterface();
				for (Class<?> cls : services) {
					String aKey = new StringBuilder().append(cls.getSimpleName()).append(".").append(args[2].toString()).toString();
					String _k=new StringBuilder().append(cls.getName()).append(args[2].toString()).toString();
					if (metaDataManager.getMethodCache().containsKey(_k)) {
						method = metaDataManager.getMethodCache().get(_k);
						rowKey = _k;
					}
					
					if (RequestLimitsManager.userLimitsCache.containsRow(aKey)){
						userKey = aKey;
					}

					if (RequestLimitsManager.ipLimitsCache.containsRow(aKey)){
						ipKey = aKey;
					}
					
					if (RequestLimitsManager.mobileLimitsCache.containsRow(aKey)){
						mobileKey = aKey;
					}
				}
			}
			
			if(RequestLimitsManager.aTable.containsRow(rowKey)) {
				RateLimiter limiter = (RateLimiter) RequestLimitsManager.aTable.get(rowKey, RATE_LIMITER_IDX);
				if(!limiter.tryAcquire()) {
					throw new SLSystemException("系统忙");
				}
			}
			
			if (RequestLimitsManager.userLimitsCache.containsRow(userKey)) {
				Object args0 = args[3];
				if (Map.class.isAssignableFrom(args0.getClass())) {
					Map<String, Object> map = ((Map<String, Object>) args0);

					if (map.get("custId") != null) {
						//总量控制
						String custId = map.get("custId") + "";
						totalRequestLimit("USER", userKey, custId);
						
						//速率控制
						rateRequestLimit("USER", userKey, custId);
					}
				}
			}
			
			if (RequestLimitsManager.ipLimitsCache.containsRow(ipKey)) {
				
				Object args0 = args[3];
				if (Map.class.isAssignableFrom(args0.getClass())) {
					Map<String, Object> map = ((Map<String, Object>) args0);

					if (map.get("ipAddr") != null) {
						//总量控制
						String ipAddr = map.get("ipAddr") + "";
						totalRequestLimit("IP", ipKey, ipAddr);

						//速率控制
						rateRequestLimit("IP", ipKey, ipAddr);
					}
				}
			}
			if(RequestLimitsManager.mobileLimitsCache.containsRow(mobileKey)){
				Object args0 = args[3];
				if (Map.class.isAssignableFrom(args0.getClass())) {
					Map<String, Object> map = ((Map<String, Object>) args0);
					if (map.get("mobile") != null) {
						//总量控制
						String mobile = map.get("mobile") + "";
						totalRequestLimit("MOBILE", mobileKey, mobile);
						
						//速率控制
						rateRequestLimit("MOBILE", mobileKey, mobile);
					}
				}

			}
			 
			result = pjp.proceed();
			return result;
		} catch (Throwable e) {
			if (method.getReturnType() == ResultVo.class) {
				
				Throwable ex = e;
				do  {
					if(SLSystemException.class.isAssignableFrom(ex.getClass())) {
						result = new ResultVo(false, "系统异常，请稍后重试!");
						return result;
					}
					else if(SLException.class.isAssignableFrom(ex.getClass())){
						String err = ex.getCause() == null ? ex.getMessage() : ex.getCause().getMessage();
						result = new ResultVo(false, err.substring(err.indexOf(' ') + 1));
						return result;
					}
					else if( SLBException.class.isAssignableFrom(ex.getClass())) {
						String err = ex.getCause() == null ? ex.getMessage() : ex.getCause().getMessage();
						result = new ResultVo(false, err.substring(err.indexOf(' ') + 1));
						return result;
					}
					else if( OptimisticLockingFailureException.class.isAssignableFrom(ex.getClass())) {
						result = new ResultVo(false, "操作失败，请稍后重试！");
						return result;
					}
					ex = ex.getCause();
				} while(ex != null);
			}
			error = e;
			logger.error("----------error with " + type.getName() + "." + method.getName() + " with argv " + Arrays.toString(args) + "\n" + e.getMessage(), e);
			throw e;
		}
		finally {
			sw.stop();
			logger.info(type.getSimpleName() + "." + ms.getName() + " " + Arrays.toString(pjp.getArgs()) + " " + sw.getTotalTimeMillis() + " " + result + " " + error);
		}
	}
	
	private void totalRequestLimit(String type, String key, String who) throws SLSystemException {
		Table<String, Integer, Object> cache = null;
		switch(type) {
		case "USER":
			cache = RequestLimitsManager.userLimitsCache;
			break;
		case "IP":
			cache = RequestLimitsManager.ipLimitsCache;
			break;
		case "MOBILE":
			cache = RequestLimitsManager.mobileLimitsCache;
			break;
		}
		//每日总量控制
		int daily = (int) cache.get(key, DAILY_IDX);
		if(daily > 0) {
			
			String mKey = new StringBuilder().append(who).append(".daily.").append(key).toString();
			String value = redisTemplate.opsForValue().get(mKey);
			if (value == null) {
				redisTemplate.opsForValue().set(mKey, "1", 1, TimeUnit.DAYS);
			} else {
				long v = Long.parseLong(value);
				if (v > daily)
					throw new SLSystemException("超过最大请求限制");
				else
					redisTemplate.opsForValue().set(mKey, String.valueOf(v + 1L), 1, TimeUnit.DAYS);
			}
		}
		//每月总量控制
		int monthly = (int) cache.get(key, MONTHLY_IDX);
		if(monthly > 0) {
			String mKey = new StringBuilder().append(who).append(".monthly.").append(key).toString();
			String value = redisTemplate.opsForValue().get(mKey);
			if (value == null) {
				redisTemplate.opsForValue().set(mKey, "1", 30, TimeUnit.DAYS);
			} else {
				long v = Long.parseLong(value);;
				if (v > monthly)
					throw new SLSystemException("超过最大请求限制");
				else
					redisTemplate.opsForValue().set(mKey, String.valueOf(v + 1L), 30, TimeUnit.DAYS);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void rateRequestLimit(String type, final String key, final String who) throws SLSystemException {
		Table<String, Integer, Object> cache = null;
		switch(type) {
		case "USER":
			cache = RequestLimitsManager.userLimitsCache;
			break;
		case "IP":
			cache = RequestLimitsManager.ipLimitsCache;
			break;
		case "MOBILE":
			cache = RequestLimitsManager.mobileLimitsCache;
			break;
		}
		
		final Table<String, Integer, Object>  fCache = cache;
		Cache<String, RateLimiter> rateCache =  (Cache<String, RateLimiter>) cache.get(key, RATE_LIMITER_IDX);
		RateLimiter limiter = null;
		try {
			limiter = rateCache.get(key,  new Callable<RateLimiter>(){

				@Override
				public RateLimiter call() throws Exception {
					int rate = (int) fCache.get(key, RATE_IDX);
					RateLimiter limiter =  RateLimiter.create(rate);
					return limiter;
				}
				
			});
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		if(limiter != null && !limiter.tryAcquire()) {
			throw new SLSystemException("服务器系统忙");
		}
	}
}
