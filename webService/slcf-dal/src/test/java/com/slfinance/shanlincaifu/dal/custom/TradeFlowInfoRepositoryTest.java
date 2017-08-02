/** 
 * @(#)TradeFlowInfoRepositoryTest.java 1.0.0 2015年4月25日 下午6:28:06  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.dal.custom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import com.slfinance.shanlincaifu.dal.base.AbstractSpringContextTestSupport;
import com.slfinance.shanlincaifu.repository.custom.TradeFlowInfoRepositoryCustom;

/**   
 * 交易流水测试类
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年4月25日 下午6:28:06 $ 
 */
public class TradeFlowInfoRepositoryTest extends AbstractSpringContextTestSupport{

	@Autowired
	private TradeFlowInfoRepositoryCustom tradeFlowInfoRepositoryCustom;
	
	@Test
	public void testFindAllRechargeSum() throws ParseException
	{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("start", 1);
		param.put("length", 10);
		param.put("nickName", "GaoMing");
		param.put("opearteDateBegin", "2015-04-01");
		param.put("opearteDateEnd", "2015-05-01");
		Map<String, Object> map = tradeFlowInfoRepositoryCustom.findAllRechargeSum(param);
		
		assertNotNull(map);
		assertEquals(map.size() != 0, true);
	}
	
	@Test
	public void testFindAllRechargeList() throws ParseException
	{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("start", 0);
		param.put("length", 10);
		//param.put("nickName", "GaoMing");
		//param.put("opearteDateBegin", "2015-04-01");
		//param.put("opearteDateEnd", "2015-05-01");
		Page<Map<String, Object>> page = tradeFlowInfoRepositoryCustom.findAllRechargeList(param);
		
		assertNotNull(page);
		assertEquals(page.getTotalElements() != 0, true);
		assertEquals(page.getContent().size() != 0, true);
	}
	
	@Test
	public void testFindRechargeDetailInfo()
	{
		Map<String, Object> map = tradeFlowInfoRepositoryCustom.findRechargeDetailInfo("20150427000000000000000000000000001");
		
		assertNotNull(map);
		assertEquals(map.size() != 0, true);
	}
}
