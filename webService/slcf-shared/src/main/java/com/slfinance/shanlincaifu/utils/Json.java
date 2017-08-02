/** 
 * @(#)ObjectMapper.java 1.0.0 2014-11-21 上午11:57:59  
 *  
 * Copyright © 2014 善林金融.  All rights reserved.  
 */

package com.slfinance.shanlincaifu.utils;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * Json序列化 单例模式实现
 * 
 * @author liubin
 * @version $Revision:1.0.0, $Date: 2014-11-21 上午11:57:59 $
 */
public enum Json implements Function {
	ObjectMapper {
		private ObjectMapper mapper = new ObjectMapper();

		@Override
		public <T> String writeValue(T t) {
			try {
				return mapper.writeValueAsString(t);
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public <T> T readValue(String str, Class<T> clazz) {
			try {
				return mapper.readValue(str, clazz);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

	};

}

interface Function {
	/**
	 * JavaBean模型转换为Json字符串
	 * 
	 * @param t
	 * @return
	 */
	<T> String writeValue(T t);

	/**
	 * Json字符串转换为JavaBean
	 * 
	 * @param str
	 * @param clazz
	 * @return
	 */
	<T> T readValue(String str, Class<T> clazz);
}