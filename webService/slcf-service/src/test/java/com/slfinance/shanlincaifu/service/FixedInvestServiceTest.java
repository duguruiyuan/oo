/** 
 * @(#)FixedInvestServiceTest.java 1.0.0 2015年8月15日 下午3:22:01  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service;

import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.impl.FixedInvestmentService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.Json;

/**   
 * 定期宝业务列表、统计、交易、投资记录接口测试
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年8月15日 下午3:22:01 $ 
 */
public class FixedInvestServiceTest extends AbstractSpringContextTestSupport {

	@Autowired
	private FixedInvestService fixedInvestService;
	
	private final Map<String, Object> paramsMap = Maps.newHashMap();
	
	@Autowired
	private FixedInvestmentService fixedInvestmentService;

	/**
	 * 定期宝分页列表查询测试
	 */
	@Test
	public void testGetFixedInvestListPage() throws SLException{
		paramsMap.clear();
		paramsMap.put("pageNum", 0);
		paramsMap.put("pageSize", 10);
		log.info(Json.ObjectMapper.writeValue(fixedInvestService.getFixedInvestListPage(paramsMap)));
	}
	
	/**
	 * 定期宝分页列表查询测试--按照欢迎程序
	 */
	@Test
	public void testGetInvestListByFavoPage() throws SLException{
		paramsMap.clear();
		paramsMap.put("pageNum", 0);
		paramsMap.put("pageSize", 10);
		log.info(Json.ObjectMapper.writeValue(fixedInvestService.getInvestListByFavoPage(paramsMap)));
	}
	
	/**
	 * 定期宝购买页面详情测试
	 */
	@Test
	public void testGetFixedInvestDatail() throws SLException{
		paramsMap.clear();
		paramsMap.put("id","8");
		paramsMap.put("custId","49f9be50-a141-4cc0-85b3-2b62d25785c0");
		log.info(Json.ObjectMapper.writeValue(fixedInvestService.getFixedInvestDatail(paramsMap)));
	}
	
	/**
	 * 定期宝产品加入记录
	 */
	@Test
	public void testGetInvestListByProIdPage() throws SLException{
		paramsMap.clear();
		paramsMap.put("id","8");
		paramsMap.put("pageNum",0);
		paramsMap.put("pageSize",10);
		paramsMap.put("termName","jibenjianjie");
		log.info(Json.ObjectMapper.writeValue(fixedInvestService.getInvestListByProIdPage(paramsMap)));
	}
	
	/**
	 * 帐户总览统计信息
	 */
	@Test
	public void testGetFixedInvestStatisicInfo() throws SLException{
		paramsMap.clear();
		paramsMap.put("custId","f379b15a-b804-460f-8184-ae547e0ce38c");
		log.info(Json.ObjectMapper.writeValue(fixedInvestService.getFixedInvestStatisicInfo(paramsMap)));
	}

	/**
	 * 定期宝投资详情
	 */
	@Test
	public void testGetFixedInvestDetail() throws SLException{
		paramsMap.clear();
		paramsMap.put("id","4");
		paramsMap.put("investId","c7a43aa4-2b3d-40f6-853d-edc41d372dc3");
		log.info(Json.ObjectMapper.writeValue(fixedInvestService.getFixedInvestDetail(paramsMap)));
	}
	
	/**
	 * 定期宝加入记录
	 */
	@Test
	public void testGetFixedTradeInfoPage() throws SLException{
		paramsMap.clear();
		paramsMap.put("custId","4");
		paramsMap.put("pageNum",0);
		paramsMap.put("pageSize",10);
		paramsMap.put("startDate","2014-09-09");
		paramsMap.put("endDate","2016-09-09");
		paramsMap.put("tradeType","加入定期宝");
		paramsMap.put("investId","1212");
		log.info(Json.ObjectMapper.writeValue(fixedInvestService.getFixedTradeInfoPage(paramsMap)));
	}
	
	/**
	 * 校验用户金牌推荐人资格
	 */
	@Test
	public void testGetRecommendFalg() throws SLException{
		paramsMap.clear();
		paramsMap.put("custId","674ab60e-4d34-48aa-8078-b039c3b48e74");
		log.info(Json.ObjectMapper.writeValue(fixedInvestService.getRecommendFalg(paramsMap)));
	}
	
	/**
	 * 申请金牌推荐人
	 */
	@Test
	public void testPutRecommendInfo() throws SLException{
		paramsMap.clear();
		paramsMap.put("custId","674ab60e-4d34-48aa-8078-b039c3b48e74");
		paramsMap.put("operIpaddress","123-456-7899");
		log.info(Json.ObjectMapper.writeValue(fixedInvestService.putRecommendInfo(paramsMap)));
	}
	
	@Test
	public void testqueryFixedInvestmentCount()  {
		paramsMap.clear();
		paramsMap.put("userId","26612795-ba11-4a89-a0f4-688321b59004");
		paramsMap.put("productType",Constant.PRODUCT_TYPE_04);
		fixedInvestmentService.queryFixedInvestmentCount(paramsMap);
	}
	
}
