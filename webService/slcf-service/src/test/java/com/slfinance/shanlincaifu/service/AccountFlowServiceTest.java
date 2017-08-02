package com.slfinance.shanlincaifu.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AccountFlowInfoEntity;
import com.slfinance.vo.ResultVo;

public class AccountFlowServiceTest extends AbstractSpringContextTestSupport{

	@Autowired
	AccountFlowService accountFlowService;
	
	@Test
	public void findAccountFlowInfoPagableTest() {
		Map<String, Object> query = Maps.newHashMap();
		query.put("custId", "20150427000000000000000000000000001");
		//query.put("tradeType", "提现");
		//query.put("startDate", DateTime.parse("2015-4-1").toDate());
		Page<AccountFlowInfoEntity> map= accountFlowService.findAccountFlowInfoPagable(query);
		
		assertNotNull(map);
		assertEquals(map.getTotalElements(), 1);
	}
	
	/**
	 * 测试用户资金流水列表
	 *
	 * @author  wangjf
	 * @date    2015年5月22日 下午1:59:34
	 */
	@Test
	public void testFindAllAccountFlowList(){
		Map<String, Object> params = Maps.newHashMap();
		params.put("start", 0);
		params.put("length", 10);
		Map<String, Object> resultMap = accountFlowService.findAllAccountFlowList(params);
		System.out.println(resultMap);
		assertNotNull(resultMap);
		assertEquals(resultMap.size() != 0, true);
	}
	
	/**
	 * 测试充值统计
	 *
	 * @author  wangjf
	 * @date    2015年5月22日 下午2:00:50
	 */
	@Test
	public void testFindAllRechargeSum() {
		Map<String, Object> params = Maps.newHashMap();
		Map<String, Object> resultMap = accountFlowService.findAllRechargeSum(params);
		System.out.println(resultMap);
		assertNotNull(resultMap);
		assertEquals(resultMap.size() != 0, true);
	}
	
	/**
	 * 测试充值管理--列表
	 *
	 * @author  wangjf
	 * @date    2015年5月22日 下午2:01:20
	 */
	@Test
	public void testFindAllRechargeList() {
		Map<String, Object> params = Maps.newHashMap();
		params.put("start", 0);
		params.put("length", 10);
		Map<String, Object> resultMap = accountFlowService.findAllRechargeList(params);
		System.out.println(resultMap);
		assertNotNull(resultMap);
		assertEquals(resultMap.size() != 0, true);
	}
	
	/**
	 * 测试充值管理--明细
	 *
	 * @author  wangjf
	 * @date    2015年5月22日 下午2:01:59
	 */
	@Test
	public void testFindRechargeDetailInfo() {
		Map<String, Object> param = Maps.newHashMap();
		param.put("flowId", "b0249014-03dd-4330-937f-5872a8dffc5b");
		Map<String, Object> resultMap = accountFlowService.findRechargeDetailInfo(param);
		System.out.println(resultMap);
		assertNotNull(resultMap);
		assertEquals(resultMap.size() != 0, true);
	}
	
	/**
	 * 测试获取公司资金流水
	 *
	 * @author  wangjf
	 * @date    2015年7月14日 下午4:00:04
	 */
	@Test
	public void testFindCompanyAccount() {
		Map<String, Object> params = Maps.newHashMap();
		params.put("start", 0);
		params.put("length", 10);
		params.put("companyType", "02");
		Map<String, Object> resultMap = accountFlowService.findCompanyAccount(params);
		System.out.println(resultMap);
		assertNotNull(resultMap);
		assertEquals(resultMap.size() != 0, true);
	}
	
	/**
	 * 测试获取公司资金流水汇总
	 *
	 * @author  wangjf
	 * @date    2015年7月14日 下午4:02:50
	 */
	@Test
	public void testFindCompanyAccountSum() {
		Map<String, Object> params = Maps.newHashMap();
		params.put("start", 0);
		params.put("length", 10);
		params.put("companyType", "03");
		Map<String, Object> resultMap = accountFlowService.findCompanyAccountSum(params);
		System.out.println(resultMap);
		assertNotNull(resultMap);
		assertEquals(resultMap.size() != 0, true);
	}
	
	/**
	 * 测试充值回调成功
	 *
	 * @author  wangjf
	 * @date    2015年7月25日 下午5:44:04
	 * @throws SLException
	 */
	@Test
	public void testCallbackRechargeSuccess() throws SLException {
		Map<String, Object> params = Maps.newHashMap();
		params.put("tradeCode", "SLBAO-TRADE-1000000002752");
		accountFlowService.callbackRechargeSuccess(params);
	}
	
	/**
	 * 测试我的资金流水
	 *
	 * @author  Tools
	 * @throws SLException 
	 * @date    2016-03-04 10:15:33
	 */
	@Test
	public void testQueryMyWealthFlow() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("start", 0);
		param.put("length", 10);
		param.put("wealthId", "3211b69a-4c63-4000-bb1d-96fba69d773d");
		param.put("custId", "6dd3463c-a094-4a64-829b-4e7dd861d164");
		ResultVo result = accountFlowService.queryMyWealthFlow(param);
		assertNotNull(result);
		assertEquals(ResultVo.isSuccess(result), true);
	}
}
