/** 
 * @(#)AllotRepositoryCustomTest.java 1.0.0 2015年4月23日 下午4:47:31  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.dal.custom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import com.slfinance.shanlincaifu.dal.base.AbstractSpringContextTestSupport;
import com.slfinance.shanlincaifu.entity.AllotDetailInfoEntity;
import com.slfinance.shanlincaifu.repository.custom.AllotRepositoryCustom;

/**   
 * 测试自定义分配信息接口
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年4月23日 下午4:47:31 $ 
 */
public class AllotRepositoryCustomTest extends AbstractSpringContextTestSupport {

	@Autowired
	AllotRepositoryCustom allotRepositoryCustom;
	
	/**
	 * 测试根据条件查询所有分配信息
	 *
	 * @author  wangjf
	 * @date    2015年4月23日 下午4:51:47
	 */
	@Test
	public void testFindAllot()
	{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("start", 0);
		param.put("length", 10);
		Page<Map<String, Object>> page = allotRepositoryCustom.findAllot(param);
		
		assertNotNull(page);
		assertEquals(page.getTotalElements() != 0, true);
		assertEquals(page.getContent().size() != 0, true);
	}
	
	/**
	 * 测试查询可以分配的债权
	 *
	 * @author  wangjf
	 * @date    2015年4月24日 下午12:06:32
	 */
	@Test
	public void testFindCanAllotLoan()
	{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("start", 0);
		param.put("length", 10);
		Page<Map<String, Object>> page = allotRepositoryCustom.findCanAllotLoan(param);
		
		assertNotNull(page);
		assertEquals(page.getTotalElements() != 0, true);
		assertEquals(page.getContent().size() != 0, true);
	}
	
	/**
	 * 测试查询可以分配的债权汇总
	 *
	 * @author  wangjf
	 * @date    2015年4月24日 下午2:49:21
	 */
	@Test
	public void testFindCanAllotLoanCount()
	{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("start", 0);
		param.put("length", 10);
		Map<String, Object> result = allotRepositoryCustom.findCanAllotLoanCount(param);
		
		assertNotNull(result);
		assertEquals(result.size() != 0, true);
	}
	
	/**
	 * 测试根据分配信息主键查询债权信息
	 *
	 * @author  wangjf
	 * @date    2015年4月24日 下午3:44:56
	 */
	@Test
	public void testFindAllotLoanById()
	{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("start", 0);
		param.put("length", 10);
		param.put("id", "20150423000000000000000000000001");
		Page<Map<String, Object>> page = allotRepositoryCustom.findAllotLoanById(param);
		assertNotNull(page);
		assertEquals(page.getTotalElements() != 0, true);
		assertEquals(page.getContent().size() != 0, true);
	}
	
	/**
	 * 测试批量插入分配详情
	 *
	 * @author  wangjf
	 * @date    2015年4月28日 下午6:11:26
	 */
	@Test
	public void testBatchInsertAllotDetail()
	{
		List<AllotDetailInfoEntity> list = new ArrayList<AllotDetailInfoEntity>();
		AllotDetailInfoEntity detail = new AllotDetailInfoEntity();
		detail.setAllotId("11111111111111");
		detail.setLoanId("11111111111111");
		detail.setTradeCode("11111111111111");// 交易编号
		detail.setTradeAmount(new BigDecimal("0")); //交易金额
		detail.setBasicModelProperty("wjf", true);
		list.add(detail);
		
		AllotDetailInfoEntity detail2 = new AllotDetailInfoEntity();
		detail2.setAllotId("22222222222222");
		detail2.setLoanId("22222222222222");
		detail2.setTradeCode("22222222222222");// 交易编号
		detail2.setTradeAmount(new BigDecimal("100")); //交易金额
		detail2.setBasicModelProperty("wjf", true);
		list.add(detail2);
		
		allotRepositoryCustom.batchInsertAllotDetail(list);
	}
}
