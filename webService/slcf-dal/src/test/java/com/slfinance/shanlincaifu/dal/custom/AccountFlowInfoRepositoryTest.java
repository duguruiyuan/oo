/** 
 * @(#)AccountFlowInfoRepositoryTest.java 1.0.0 2015年4月25日 下午6:26:33  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.dal.custom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import com.slfinance.shanlincaifu.dal.base.AbstractSpringContextTestSupport;
import com.slfinance.shanlincaifu.repository.custom.AccountFlowInfoRepositoryCustom;
import com.slfinance.shanlincaifu.utils.SubjectConstant;

/**   
 * 账户流水测试类
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年4月25日 下午6:26:33 $ 
 */
public class AccountFlowInfoRepositoryTest extends AbstractSpringContextTestSupport{

	@Autowired
	private AccountFlowInfoRepositoryCustom accountFlowInfoRepositoryCustom;
	
	/**
	 * 测试用户资金流水列表
	 *
	 * @author  wangjf
	 * @throws ParseException 
	 * @date    2015年4月25日 下午6:29:47
	 */
	@Test
	public void testFindAllAccountFlowList() throws ParseException
	{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("start", 0);
		param.put("length", 10);
		//param.put("nickName", "GaoMing");
		//param.put("opearteDateBegin", "2015-04-01");
		//param.put("opearteDateEnd", "2015-06-01");
		Page<Map<String, Object>> page = accountFlowInfoRepositoryCustom.findAllAccountFlowList(param);
		
		assertNotNull(page);
		assertEquals(page.getTotalElements() != 0, true);
		assertEquals(page.getContent().size() != 0, true);
	}
	
	/**
	 * 测试根据客户ID统计用户受益
	 *
	 * @author  wangjf
	 * @date    2015年4月28日 下午5:23:55
	 */
	@Test
	public void testFindIncomeByCustId() 
	{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("custId", "20150427000000000000000000000000001");
		param.put("tradeType", Arrays.asList(SubjectConstant.TRADE_FLOW_TYPE_01));
		BigDecimal tradeAmount = accountFlowInfoRepositoryCustom.findIncomeByCustId(param);
		
		assertNotNull(tradeAmount);
		assertEquals(tradeAmount.compareTo(new BigDecimal("0")) != 0, true);
	}
	
	/**
	 * 测试我的账户-我的投资-活期宝-交易明细
	 *
	 * @author  wangjf
	 * @date    2015年4月28日 下午5:42:11
	 * @throws ParseException
	 */
	@Test
	public void testFindAllBaoAccountDetailByCustId() throws ParseException
	{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("start", 0);
		param.put("length", 7);
		param.put("custId", "20150427000000000000000000000000001");
		param.put("tradeType", "活期宝收益");
		param.put("opearteDateBegin", "2015-04-01");
		param.put("opearteDateEnd", "2015-06-01");
		Page<Map<String, Object>> page = accountFlowInfoRepositoryCustom.findAllBaoAccountDetailByCustId(param);
		
		assertNotNull(page);
		assertEquals(page.getTotalElements() != 0, true);
		assertEquals(page.getContent().size() != 0, true);
	}
	
	/**
	 * 交易查询
	 *
	 * @author  HuangXiaodong
	 * @date     2015年5月19日 上午11:33:31
	 * @param param
	  		<tt>tradeType： String:交易类型</tt><br>
	  		<tt>tradeDateBegin： Date:交易开始时间</tt><br>
	  		<tt>tradeDateEnd： Date:交易结束时间</tt><br>
	  		<tt>custId:用户id</tt><br>
	 * @return
	 * List<Map<String, Object>>
	 		<tt>tradeAmount:交易金额</tt><br>
	  		<tt>tradeDate： BigDecimal:交易时间</tt><br>
	 */
	@Test
	public void findSumTradeAmount() 
	{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("custId", "20150427000000000000000000000000001");
		param.put("tradeType", Arrays.asList(SubjectConstant.TRADE_FLOW_TYPE_01,SubjectConstant.TRADE_FLOW_TYPE_03));
		param.put("tradeDateBegin", "2015-05-06");
		param.put("tradeDateEnd", "2015-05-09");
		List<Map<String, Object>>  mapList = accountFlowInfoRepositoryCustom.findSumTradeAmount(param);
		assertNotNull(mapList);
		assertEquals(mapList.size() != 0, true);
	}
	
}
