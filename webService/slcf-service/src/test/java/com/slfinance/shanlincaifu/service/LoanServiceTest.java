/** 
 * @(#)LoanServiceTest.java 1.0.0 2015年5月5日 上午11:44:47  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.utils.Constant;

/**   
 * 测试债权
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年5月5日 上午11:44:47 $ 
 */
public class LoanServiceTest extends AbstractSpringContextTestSupport{

	@Autowired
	private LoanInfoService loanInfoService;
	
	/**
	 * 测试查询债权明细
	 *
	 * @author  wangjf
	 * @date    2015年5月5日 上午11:47:03
	 * @throws SLException
	 */
	@Test
	public void testFindLoanDetailInfo() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("loanId", "20150427000000000000000000000000001");
		loanInfoService.findLoanDetailInfo(param);	
	}
	
	/**
	 * 测试查询用户每天价值情况
	 *
	 * @author  wangjf
	 * @date    2015年5月23日 下午5:09:38
	 * @throws SLException
	 */
	@Test
	public void testFindDailyValueList() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("start", 0);
		param.put("length", 10);
		param.put("custId", "6e0c6d2b-c678-429a-bb97-6d7a9047d271");
		param.put("productName", Constant.PRODUCT_TYPE_04);
		Map<String, Object> resultMap = loanInfoService.findDailyValueList(param);
		System.out.println(resultMap);
		assertNotNull(resultMap);
		assertEquals(resultMap.size() != 0, true);
	}
	
	/**
	 * 测试查询用户每天所拥有的债权情况
	 *
	 * @author  wangjf
	 * @date    2015年5月23日 下午5:10:27
	 * @throws SLException
	 */
	@Test
	public void testFindDailyLoanList() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("start", 0);
		param.put("length", 10);
		param.put("dailyValueId", "1E32F69BACD91DB6E053D89610C02B74");
		param.put("custId", "6e0c6d2b-c678-429a-bb97-6d7a9047d271");
		param.put("subAccountId", "553f8fe0-1e11-4364-b205-3c4beb53138e");
		Map<String, Object> resultMap = loanInfoService.findDailyLoanList(param);
		System.out.println(resultMap);
		assertNotNull(resultMap);
		assertEquals(resultMap.size() != 0, true);
	}
	
	/**
	 * 测试保存每日债权和用户价值
	 *
	 * @author  wangjf
	 * @date    2015年5月23日 下午5:11:12
	 * @throws SLException
	 */
	@Test
	public void testSaveDailyLoanAndValue() throws SLException{
		loanInfoService.saveDailyLoanAndValue(DateUtils.addDays(new Date(), -1), Constant.PRODUCT_TYPE_04);
	}
}
