/** 
 * @(#)AppThirdServiceTest.java 1.0.0 2015年8月20日 下午6:26:06  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.mobile.APPInvestService;
import com.slfinance.shanlincaifu.service.mobile.AppThirdService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.Json;
import com.slfinance.vo.ResultVo;

/**   
 * 手机端善林财富三期测试
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年8月20日 下午6:26:06 $ 
 */
@Slf4j
public class AppThirdServiceTest extends AbstractSpringContextTestSupport {

	@Autowired
	private AppThirdService appThirdService;
	
	@Autowired
	private APPInvestService aPPInvestService;
	
	final Map<String, Object> paramsMap = Maps.newHashMap();
	
	
	/**
	 * 活期宝、体验宝、定期宝购买投资页面
	 */	
	@Test
	public void  testInvestbBuyDetail() throws SLException{
		paramsMap.clear();
		//paramsMap.put("custId", "b2dbac43-ef86-4b59-9d59-a60c2f236c70");
		//paramsMap.put("productId", "1");
		paramsMap.put("pageNum", "0");
		paramsMap.put("pageSize", "111");
		//paramsMap.put("typeName", Constant.PRODUCT_TYPE_01);
//		appThirdService.investbBuyDetail(paramsMap);
//		appThirdService.joinListInfo(paramsMap);
		ResultVo resultVo = appThirdService.investListAll(paramsMap);
		System.out.println(resultVo);
	}
	
	/**
	 * 交易记录
	 */
	@Test
	public void  testTransactionList() throws SLException{
		
	}
	
	/**
	 * 我的投资-活期宝、定期宝、体验宝页面
	 */
	@Test
	public void  testInvestStatisticsInfo() throws SLException{
		paramsMap.clear();
		paramsMap.put("custId", "bbfb5287-574d-42f7-b0b3-51ca1263c5fa");
		paramsMap.put("productType", Constant.PRODUCT_TYPE_03);
		log.info(Json.ObjectMapper.writeValue(appThirdService.investStatisticsInfo(paramsMap)));
	}
	
	/**
	 * 定期宝投资详情
	 */
	@Test
	public void  testFixedInvestDetail() throws SLException{
		
	}
	
	/**
	 * 提前退出定期宝详情
	 */
	@Test
	public void  testPreAtoneDetail() throws SLException{
		paramsMap.clear();
	}
	
	@Test
	public void testInvestIndex() throws SLException {
		paramsMap.clear();
		paramsMap.put("pageNum", "0");
		paramsMap.put("pageSize", "1");
		ResultVo result = aPPInvestService.investIndex(paramsMap);
		System.out.println(result);
	}
	
	@Test
	public void testBuyDetailToCurrent() throws SLException {
		paramsMap.clear();
		paramsMap.put("custId", "bbfb5287-574d-42f7-b0b3-51ca1263c5fa");
		ResultVo result = aPPInvestService.buyDetailToCurrent(paramsMap);
		System.out.println(result);
	}
}
