/*
 * Copyright 2012-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.slfinance.shanlincaifu.controller;

import java.lang.reflect.Method;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;

import com.google.common.base.CaseFormat;
import com.google.common.base.Charsets;
import com.google.common.io.BaseEncoding;
import com.slfinance.annotation.AutoDispatch;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.exception.IllegalPerameterException;
import com.slfinance.spring.ServiceLocator;
import com.slfinance.util.MetaDataManager;
import com.slfinance.vo.ResultVo;

@Component
@Slf4j
public class WelcomeController {

	@Autowired
	MetaDataManager metaDataManager;

	protected Object autoDispatch(HttpServletRequest request, HttpServletResponse response, String methodName, Map<String, ?> model) throws SLException {
		if(request.getMethod().equalsIgnoreCase("GET")) {
			model = request.getParameterMap();
		}
		AutoDispatch annotation = getClass().getAnnotation(AutoDispatch.class);
		Class<?>[] services = annotation.serviceInterface();
		String preKey = null, postKey = null;
		String key = null;
		Class<?> service = null;
		for (Class<?> cls : services) {
			if (metaDataManager.getMethodCache().containsKey(key = cls.getName()
					.concat(methodName))) {
				preKey = key.replace(methodName, "pre" + methodName);
				postKey = key.replace(methodName, "post" + methodName);
				service = cls;
				break;
			}
		}
		if(key != null) {
			Object o = ServiceLocator.getService(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, service.getSimpleName()));
			Object ret = null;
			
			//1. preCondition process
			ResultVo preCheck = null;
			Method pre = metaDataManager.getMethodCache().get(preKey);
			if (pre != null) {
				try {
					int pCount = pre.getParameterTypes().length;
					if(pCount == 1)
						preCheck = (ResultVo) pre.invoke(o, model);
					else
						preCheck = (ResultVo) pre.invoke(o);
				}catch (Exception e) {
					if(e.getCause() != null && e.getCause().getClass() == IllegalPerameterException.class) {
						response.setCharacterEncoding("UTF-8");
						response.addHeader("x-slb-err", BaseEncoding.base64().encode(e.getCause().getMessage().getBytes(Charsets.UTF_8)));
					}
					log.debug("preCondition check error:" + e.getCause().getMessage());
					
				}
				
				if (!ResultVo.isSuccess(preCheck))
					throw new SLException((String)preCheck.getValue("message"));
			}
			
			//2. business process
			Method m = metaDataManager.getMethodCache().get(key);
			try {
				int pCount = m.getParameterTypes().length;
				if(pCount == 1)
					ret = m.invoke(o, model);
				else
					ret = m.invoke(o);
			} catch (Exception e) {
				if(e.getCause() != null && e.getCause().getClass() == IllegalPerameterException.class) {
					response.setCharacterEncoding("UTF-8");
					response.addHeader("x-slb-err", BaseEncoding.base64().encode(e.getCause().getMessage().getBytes(Charsets.UTF_8)));
				}
				log.error("[shanlinbao] bussiness process error." + e.getCause().getMessage());
				e.printStackTrace();
				
				String errMessage = e.getCause().getMessage();
			
				if( OptimisticLockingFailureException.class.isAssignableFrom(e.getCause().getClass())) {
					errMessage = "操作失败，请稍后重试！";
				}
				else if( org.springframework.dao.InvalidDataAccessResourceUsageException.class.isAssignableFrom(e.getCause().getClass())) {
					errMessage = "操作失败！";
				}
				
				throw new SLException(errMessage);
			} finally {
				//3. postCondition process
				Method post = metaDataManager.getMethodCache().get(postKey);
				if(post != null) {
					try {
						post.invoke(o, ret);
					} catch (Exception e) {
						log.info("[shanlinbao] business postCondition error!");
					} 
				}
			}
						
			return ret;
		}
		else
			throw new UnsupportedOperationException();
	}
}
