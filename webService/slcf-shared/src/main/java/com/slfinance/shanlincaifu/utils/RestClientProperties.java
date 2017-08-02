/** 
 * @(#)RestClient.java 1.0.0 2014年6月19日 下午2:25:50  
 *  
 * Copyright © 2014 善林金融.  All rights reserved.  
 */

package com.slfinance.shanlincaifu.utils;

import lombok.Data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


/**
 * RectClient Properties
 * 
 * @author 孟山
 * @version $Revision:1.0.0, $Date: 2014年6月19日 下午2:25:50 $
 */
@Configuration
@Data
public class RestClientProperties  {
	
	
	@Autowired
	private FoundtionClient foundtionClient;
	
	@Autowired
	private TppClient tppClient;
	
	@Data
	@Configuration
	public static class FoundtionClient {
		@Value("${foundation.appSource:web}")
		String appSource;
		@Value("${foundation.readTimeout:60000}")
		int readTimeout;
		@Value("${foundation.connectTimeout:60000}")
		int connectTimeout;
		@Value("${foundation.md5Seeds:er4uydfjdkf6}")
		String  md5Seeds;
		@Value("${foundation.user:test}")
		String user;
		@Value("${foundation.pwd:test}")
		String pwd;
		@Value("${foundation.servicePrefix:http://localhost:8090/}")
		String servicePrefix;
	}
	
	@Data
	@Configuration
	public static class TppClient {
		@Value("${tpp.readTimeout:60000}")
		int readTimeout;
		@Value("${tpp.connectTimeout:60000}")
		int connectTimeout;
		@Value("${tpp.servicePrefix:http://localhost:8090/}")
		String servicePrefix;
	}

}
