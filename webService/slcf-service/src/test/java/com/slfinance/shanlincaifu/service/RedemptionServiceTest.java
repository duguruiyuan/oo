/** 
 * @(#)InvestServiceTest.java 1.0.0 2015年4月29日 下午7:18:41  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */

package com.slfinance.shanlincaifu.service;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

/**
 * 测试投资
 * 
 * @author HuYaHui
 * @version $Revision:1.0.0, $Date: 2015年4月29日 下午7:18:41 $
 */
@ContextConfiguration(locations = { "classpath:/application-test.xml" })
@ActiveProfiles("dev")
public class RedemptionServiceTest extends AbstractJUnit4SpringContextTests {
	@Autowired
	private RedemptionService redemptionService;


	@Test
	public void findDetailByConditionTest(){
		Map<String,Object> params=new HashMap<>();
		params.put("tradeCode", "");
		redemptionService.findDetailByCondition(params);
	}
	
	@Test
	public void countByConditionTest(){
		Map<String,Object> params=new HashMap<>();
		params.put("start", 0);
		params.put("length", 10);
		redemptionService.countByCondition(params);
	}
	
	@Test
	public void findAtoneListByConditionTest(){
		Map<String,Object> params=new HashMap<>();
		params.put("start", 0);
		params.put("length", 10);
		redemptionService.findAtoneListByCondition(params);
	}
}
