/** 
 * @(#)RecommedBusiServiceTest.java 1.0.0 2015年10月12日 下午6:05:42  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service;

import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.vo.ResultVo;

/**   
 * 金牌推荐人前端WEB业务相关接口测试
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年10月12日 下午6:05:42 $ 
 */
public class RecommedBusiServiceTest extends AbstractSpringContextTestSupport {

	@Autowired
	private RecommedBusiService recommedBusiService;
	
	Map<String,Object> paramsMap = Maps.newHashMap();

	/**
	 * 统计信息(推广奖励、年化投资、推荐好友)和推广奖励信息(活期宝、定期宝)测试
	 */
	@Test
	public void testGetRecommedInfo() throws SLException{
		paramsMap.clear();
		paramsMap.put("custId", "674ab60e-4d34-48aa-8078-b039c3b48e74");
//		paramsMap.put("productType", Constant.PRODUCT_TYPE_01);
		paramsMap.put("productType", Constant.PRODUCT_TYPE_04);
		paramsMap.put("pageNum", 0);
		paramsMap.put("pageSize",10);
		ResultVo result = recommedBusiService.getRecommedInfo(paramsMap);
		log.debug(JSON.toJSONString(result));
	}
	
	/**
	 * 金牌推荐人当天或当月在投详情
	 */
	@Test
	public void testGetInvestListDetail() throws SLException{
		paramsMap.clear();
		paramsMap.put("custId", "674ab60e-4d34-48aa-8078-b039c3b48e74");
//		paramsMap.put("productType", Constant.PRODUCT_TYPE_01);
		paramsMap.put("productType", Constant.PRODUCT_TYPE_04);
		paramsMap.put("id","1E31CA14249A1A90E053D89610C0D182");
		paramsMap.put("pageNum", 0);
		paramsMap.put("pageSize",10);
		recommedBusiService.getInvestListDetail(paramsMap);
	}
	
}
