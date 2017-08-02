/** 
 * @(#)RealNameAuthenticationServiceTest.java 1.0.0 2015年4月25日 下午12:12:35  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;

/**   
 * 实名认证业务相关测试
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年4月25日 下午12:12:35 $ 
 */
public class RealNameAuthenticationServiceTest extends AbstractSpringContextTestSupport {

	@Autowired
	private RealNameAuthenticationService realNameAuthenticationService;
	
	Map<String,Object> paramsMap = Maps.newHashMap();

	/**
	 * 实名认证测试
	 */
	@Test
	public void verifyIdentification() throws SLException{
		paramsMap.clear();
		paramsMap.put("custId", "e05913aa-52cc-4ecd-b6a2-0954121dd137");
		paramsMap.put("custName", "黄晓冬");
		paramsMap.put("credentialsCode", "430503199008254046");
		paramsMap.put("channelNo", "2015070100000001");
		paramsMap.put("utid", "123");
		realNameAuthenticationService.verifyIdentification(paramsMap);
	}
	
	
	@Autowired
	private UserEmailService userEmailService;

	/**
	 * 检查该邮箱是否已经存在
	 */
	@Test
	public void checkEmailIsExist() throws SLException {
		paramsMap.clear();
		paramsMap.put("email", "zhangzhisheng@shanlinjinrong.com");
		Assert.assertNotNull(userEmailService.checkEmailIsExist(paramsMap));
	}
	
	
	/**
	 * 实名认证次数测试
	 */
	@Test
	public void getRealNameAuthCount() throws SLException {
		paramsMap.clear();
		paramsMap.put("custId", "zhangzs");
		System.out.println(realNameAuthenticationService.getRealNameAuthCount(paramsMap));
	}
	
}
