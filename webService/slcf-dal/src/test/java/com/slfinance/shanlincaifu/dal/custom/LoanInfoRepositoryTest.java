/** 
 * @(#)LoanInfoRepositoryTest.java 1.0.0 2015年4月27日 下午8:32:18  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.dal.custom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.dal.base.AbstractSpringContextTestSupport;
import com.slfinance.shanlincaifu.repository.custom.LoanInfoRepositoryCustom;

/**   
 * 债权访问数据测试类
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年4月27日 下午8:32:18 $ 
 */
public class LoanInfoRepositoryTest extends AbstractSpringContextTestSupport {

	@Autowired
	LoanInfoRepositoryCustom loanInfoRepositoryCustom;
	
	/**
	 * 测试债权列表
	 *
	 * @author  wangjf
	 * @date    2015年4月27日 下午8:34:30
	 * @throws SLException
	 */
	@Test
	public void testFindLoanList() throws SLException
	{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("start", 0);
		param.put("length", 10);
		param.put("productType", "活期宝");
		Page<Map<String, Object>> page = loanInfoRepositoryCustom.findLoanList(param);
		
		assertNotNull(page);
		assertEquals(page.getTotalElements() != 0, true);
		assertEquals(page.getContent().size() != 0, true);
	}
	
	/**
	 * 测试债权明细-还款计划
	 *
	 * @author  wangjf
	 * @date    2015年4月27日 下午8:34:40
	 * @throws SLException
	 */
	@Test
	public void testFindRepaymentPlanList() throws SLException
	{
		List<Map<String, Object>> page = loanInfoRepositoryCustom.findRepaymentPlanList("20150427000000000000000000000000001", new BigDecimal("1"));
		
		assertNotNull(page);
		assertEquals(page.size() != 0, true);
	}
	
	/**
	 * 测试债权明细-债权明细
	 *
	 * @author  wangjf
	 * @date    2015年4月27日 下午8:34:56
	 * @throws SLException
	 */
	@Test
	public void testFindLoanDeatilById() throws SLException
	{
		Map<String, Object> result = loanInfoRepositoryCustom.findLoanDeatilById("20150427000000000000000000000000001");
		assertNotNull(result);
		assertEquals(result.size() != 0, true);
	}
	
	/**
	 * 测试业务管理-债权数据汇总
	 *
	 * @author  wangjf
	 * @date    2015年5月26日 上午10:24:08
	 * @throws SLException
	 */
	@Test
	public void testFindLoanListCount() throws SLException
	{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("start", 0);
		param.put("length", 10);
		param.put("productType", "活期宝");
		Map<String, Object> result = loanInfoRepositoryCustom.findLoanListCount(param);
		assertNotNull(result);
	}
}
