/** 
 * @(#)AccountInfoRepositoryTest.java 1.0.0 2015年4月25日 下午6:27:27  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */

package com.slfinance.shanlincaifu.dal.custom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import com.slfinance.shanlincaifu.dal.base.AbstractSpringContextTestSupport;
import com.slfinance.shanlincaifu.repository.custom.AccountInfoRepositoryCustom;

/**
 * 账户测试类
 * 
 * @author wangjf
 * @version $Revision:1.0.0, $Date: 2015年4月25日 下午6:27:27 $
 */
public class AccountInfoRepositoryTest extends AbstractSpringContextTestSupport {

	@Autowired
	private AccountInfoRepositoryCustom accountInfoRepositoryCustom;

	/**
	 * 测试用户资金统计
	 *
	 * @author wangjf
	 * @throws ParseException
	 * @date 2015年4月25日 下午7:04:01
	 */
	@Test
	public void testFindAllCustAccountSum() throws ParseException {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("start", 0);
		param.put("length", 10);
		param.put("nickName", "GaoMing");
		//param.put("opearteDateBegin", "2015-04-01");
		//param.put("opearteDateEnd", "2015-06-01");
		Map<String, Object> map = accountInfoRepositoryCustom.findAllCustAccountSum(param);

		assertNotNull(map);
		assertEquals(map.size() != 0, true);
	}

	/**
	 * 测试用户资金列表
	 *
	 * @author wangjf
	 * @throws ParseException
	 * @date 2015年4月25日 下午7:04:14
	 */
	@Test
	public void testFindAllCustAccountList() throws ParseException {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("start", 0);
		param.put("length", 10);
		param.put("nickName", "GaoMing");
		param.put("opearteDateBegin", "2015-04-01");
		param.put("opearteDateEnd", "2015-06-01");
		Page<Map<String, Object>> page = accountInfoRepositoryCustom.findAllCustAccountList(param);
		assertNotNull(page);
		assertEquals(page.getTotalElements() != 0, true);
		assertEquals(page.getContent().size() != 0, true);
	}

	/**
	 * 测试根据序列取序列编号
	 *
	 * @author wangjf
	 * @date 2015年4月28日 下午5:53:44
	 */
	@Test
	public void testFindSequenceValueByName() {
		long tradeNumberSeq = accountInfoRepositoryCustom.findSequenceValueByName("TRADE_NUMBER_SEQ");
		assertEquals(tradeNumberSeq != 0, true);
	}

	/**
	 * 测试统计用户持有价值
	 *
	 * @author wangjf
	 * @date 2015年4月28日 下午5:57:53
	 */
	@Test
	public void testFindAllValueByCustId() {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("custId", "20150427000000000000000000000000001");
		param.put("typeName", "活期宝");
		Map<String, Object> accountAmountMap = accountInfoRepositoryCustom.findAllValueByCustId(param);
		BigDecimal accountTotalValue = accountAmountMap.containsKey("accountTotalValue") ? (BigDecimal)accountAmountMap.get("accountTotalValue") : new BigDecimal("0"); 
		BigDecimal accountAvailableValue = accountAmountMap.containsKey("accountAvailableValue") ? (BigDecimal)accountAmountMap.get("accountAvailableValue") : new BigDecimal("0");

		assertNotNull(accountTotalValue);
		assertNotNull(accountAvailableValue);
		assertEquals(accountTotalValue.compareTo(new BigDecimal("0")) != 0, true);
	}

	@Test
	public void testCheckFormat() {
		BigDecimal bg = new BigDecimal("0000001").add(new BigDecimal(1));
		String str1 = String.format("%08d", bg.intValue());
		System.out.println(str1);
	}
}
