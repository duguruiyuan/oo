/** 
 * @(#)AppThirdServiceTest.java 1.0.0 2015年8月20日 下午6:26:06  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service;

import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.mobile.APPAccountService;

/**   
 * 手机端善林财富测试
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年8月20日 下午6:26:06 $ 
 */
public class AppAccountServiceTest extends AbstractSpringContextTestSupport {

	
	@Autowired
	private APPAccountService appAccountService;
	
	final Map<String, Object> paramsMap = Maps.newHashMap();
	
	
	/**
	 * 活期宝、体验宝、定期宝购买投资页面
	 */	
	@Test
	public void  testTradeListALLInfo() throws SLException{
		paramsMap.clear();
		paramsMap.put("custId", "1221");
		paramsMap.put("tradeTypeList", "普通赎回定期宝,提前赎回定期宝");
		paramsMap.put("pageNum", "0");
		paramsMap.put("pageSize", "10");
		appAccountService.tradeListALLInfo(paramsMap);
	}
	
	@Test 
	public void testMessageListALLNew() throws SLException {
		Map<String, Object> paramsMap = Maps.newHashMap();
		paramsMap.put("start", "0");
		paramsMap.put("length", "10");
		paramsMap.put("custId", "05395af8-a2ab-4e50-8289-7c8ade68d952");
		appAccountService.messageListALLNew(paramsMap);
	}
	
	@Test
	public void testUser() throws SLException {
		Map<String, Object> paramsMap = Maps.newHashMap();
		paramsMap.put("custId", "05395af8-a2ab-4e50-8289-7c8ade68d952");
		paramsMap.put("newUnreadMsgMthod", "true");
		appAccountService.user(paramsMap);
	}
}
