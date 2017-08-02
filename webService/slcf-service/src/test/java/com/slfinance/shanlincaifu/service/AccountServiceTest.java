/** 
 * @(#)AccountServiceTest.java 1.0.0 2015年5月5日 上午11:09:21  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */

package com.slfinance.shanlincaifu.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.vo.ResultVo;

/**
 * 测试账户
 * 
 * @author wangjf
 * @version $Revision:1.0.0, $Date: 2015年5月5日 上午11:09:21 $
 */
public class AccountServiceTest extends AbstractSpringContextTestSupport {

	@Autowired
	private AccountService accountService;

	@Autowired
	private ThirdPartyPayService thirdPartyPayService;

	/**
	 * 测试用户资金统计
	 *
	 * @author wangjf
	 * @date 2015年5月22日 下午1:49:16
	 * @throws SLException
	 */
	@Test
	public void testFindAllCustAccountSum() throws SLException {
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> resultMap = accountService
				.findAllCustAccountSum(params);
		System.out.println(resultMap);
		assertNotNull(resultMap);
		assertEquals(resultMap.size() != 0, true);
	}

	/**
	 * 测试用户资金列表
	 *
	 * @author wangjf
	 * @date 2015年5月22日 下午1:53:05
	 * @throws SLException
	 */
	@Test
	public void testFindAllCustAccountList() throws SLException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("start", 0);
		params.put("length", 10);
		Map<String, Object> resultMap = accountService
				.findAllCustAccountList(params);
		System.out.println(resultMap);
		assertNotNull(resultMap);
		assertEquals(resultMap.size() != 0, true);
	}

	/**
	 * 测试提现审核
	 *
	 * @author wangjf
	 * @throws SLException
	 * @date 2015年5月5日 上午11:10:21
	 */
	@Test
	public void testWithdrawalCashAudit() throws SLException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("custId", "20150427000000000000000000000000001");
		params.put("bankId", "d4809f02-1a77-43e7-a974-f3c4288a4703");
		params.put("mobile", "18249546888");
		params.put("amount", "10");
		params.put("mobileVerifyCode", "123321");
		params.put("withdrawPsd", "dd9d21e22391090ddce7c6ed58c6412d");
		accountService.withdrawalCashAudit(params);
	}

	/**
	 * 测试充值申请
	 *
	 * @author wangjf
	 * @date 2015年5月22日 下午1:56:16
	 * @throws SLException
	 */
	@Test
	public void testRechargeApply() throws SLException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("custId", "20150427000000000000000000000000001");
		params.put("bankNo", "00080001");
		params.put("bankCardNo", "6225885130545775");
		params.put("tradeAmount", "10");
		params.put("payType", "AUTH_PAY");
		params.put("ipAddress", "192.16.2.108");
		params.put("channelNo", "2015070100000001");
		params.put("utid", "123");
		accountService.rechargeApply(params);
	}

	/**
	 * 查询用户银行
	 *
	 * @author wangjf
	 * @date 2015年6月3日 下午2:38:24
	 * @throws SLException
	 */
	@Test
	public void testQueryUserBank() throws SLException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("bankCardNo", "6225885130545775");
		params.put("custCode", "201505150000001544");
		thirdPartyPayService.queryUserBank(params);
	}

	/**
	 * 测试提现审核
	 *
	 * @author wangjf
	 * @date 2015年8月3日 上午11:27:41
	 * @throws SLException
	 */
	@Test
	public void testSaveBatchWithdrawCashAudit() throws SLException {
		List<String> auditList = new ArrayList<String>();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("auditStatus", Constant.AUDIT_STATUS_PASS);
		params.put("memo", "审核通过");
		params.put("custId", "1");
		params.put("ipAddress", "192.16.2.108");

		auditList.add("4ce5290f-aed7-49f0-bd87-2af6e3844bd3");
		auditList.add("62e93c1c-ed35-46a8-85a7-f7c6396e28db");
		params.put("auditList", auditList);
		ResultVo resultVo = accountService.saveBatchWithdrawCashAudit(params);
		System.out.println(resultVo);
	}

	@Test
	public void testFindRewardInfo() throws SLException {
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("productId", "1");
		param.put("custId", "9e0462bc-474d-4443-8478-696039d0b8c9");

		accountService.findRewardInfo(param);
	}
	
	/**
	 * 充值补单
	 *
	 * @author  wangjf
	 * @date    2015年10月13日 下午4:48:38
	 * @throws SLException
	 */
	@Test
	public void testMendRecharge() throws SLException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("tradeFlowId", "fb4fd8f2-3605-48d8-a4e3-32d5bd10940c");
		ResultVo resultVo = accountService.mendRecharge(params);
		System.out.println(resultVo);
		assertEquals(ResultVo.isSuccess(resultVo), true);
	}
	
	/**
	 * 提现补单
	 *
	 * @author  wangjf
	 * @throws SLException 
	 * @date    2015年10月26日 下午4:34:56
	 */
	@Test
	public void testMendWithdrawCash() throws SLException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("tradeFlowId", "cf08abe1-1694-45f0-ab58-ba537664c822");
		ResultVo resultVo = accountService.mendWithdrawCash(params);
		System.out.println(resultVo);
		assertEquals(ResultVo.isSuccess(resultVo), true);
	}
	
	/**
	 * 测试查询商户余额
	 * 
	 * @throws SLException
	 */
	@Test
	public void testQueryAccountAmount() throws SLException {
		Map<String, Object> params = new HashMap<String, Object>();
		ResultVo resultVo = thirdPartyPayService.queryAccountAmount(params);
		System.out.println(resultVo);
		assertEquals(ResultVo.isSuccess(resultVo), true);
	}
	
	@Test
	public void testGrantAccount() throws SLException {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("custId", "09331f16-9cec-4acc-86ae-175677247120");
		param.put("userId", "1");
		param.put("tradeAmount", "1000");
//		param.put("relateType",);
//		param.put("relatePrimary",);
		param.put("auditMemo", "test");
		
		
		ResultVo resultVo = accountService.grantAccount(param);
		System.out.println(resultVo);
		assertEquals(ResultVo.isSuccess(resultVo), true);
	}
}
