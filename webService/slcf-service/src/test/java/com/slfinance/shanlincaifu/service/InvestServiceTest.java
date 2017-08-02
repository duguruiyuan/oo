/** 
 * @(#)InvestServiceTest.java 1.0.0 2015年4月29日 下午7:18:41  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */

package com.slfinance.shanlincaifu.service;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.vo.ResultVo;

/**
 * 测试投资
 * 
 * @author wangjf
 * @version $Revision:1.0.0, $Date: 2015年4月29日 下午7:18:41 $
 */
public class InvestServiceTest extends AbstractSpringContextTestSupport {

	@Autowired
	private InvestService investService;
	@Autowired
	private RedemptionService redemptionService;
	
	@Test
	public void countByConditionTest(){
		Map<String,Object> params=new HashMap<>();
		params.put("start", 0);
		params.put("length", 10);
		redemptionService.countByCondition(params);
	}
	
	@Test
	public void findAtoneListByConditionTest(){
		Map<String,Object> params=new HashMap<>();
		params.put("start", 0);
		params.put("length", 10);
		redemptionService.findAtoneListByCondition(params);
	}

	@Test
	public void findBAODetailTest(){
		Map<String, Object> params=new HashMap<>();
		params.put("custId", "bbfb5287-574d-42f7-b0b3-51ca1263c5fa");
		params.put("productName", "活期宝");
		Map<String,Object> rtnMap=investService.findBAODetail(params);
		System.out.println(rtnMap==null);
	}
	
	@Test
	public void findOwnerListTest(){
		Map<String, Object> params=new HashMap<>();
		params.put("productName","活期宝");
		params.put("start", 0);
		params.put("length", 10);
		Map<String,Object> rtnMap=new HashMap<>();investService.findOwnerList(params);
		System.out.println(rtnMap==null);
		
	}
	
	@Test
	public void findByConditionTest() throws SLException {
		Map<String, Object> params=new HashMap<>();
		params.put("start", 0);
		params.put("length", 10);
		Map<String, Object> map=investService.findByCondition(params);
		System.out.println(map==null);
		
	}
	
	/**
	 * 测试加入活期宝
	 *
	 * @author  wangjf
	 * @date    2015年5月5日 下午12:00:28
	 * @throws SLException
	 */
	@Test
	public void testJoinBao() throws SLException {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("custId", "20150427000000000000000000000000001");
		param.put("tradeAmount", "100");
		ResultVo result = investService.joinBao(param);
		assertEquals(ResultVo.isSuccess(result), true);
	}
	
	/**
	 * 测试快速赎回活期宝
	 *
	 * @author  wangjf
	 * @date    2015年5月5日 下午12:00:36
	 * @throws SLException
	 */
	@Test
	public void testWithdrawBao() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("custId", "20150427000000000000000000000000001");
		param.put("tradeAmount", "300");
		param.put("tradePassword", "5a656b10d2dec100254fc5892f3e972a");
		ResultVo result = investService.withdrawBao(param);
		assertEquals(ResultVo.isSuccess(result), true);
	}
	
	/**
	 * 测试补全赎回详情
	 *
	 * @author  wangjf
	 * @throws SLException 
	 * @date    2015年5月5日 下午12:01:11
	 */
	@Test
	public void testFullAtoneDetail() throws SLException {
		Map<String, Object> param = new HashMap<String, Object>();
		investService.fullAtoneDetail(param);
	}
	
	/**
	 * 测试普通赎回活期宝
	 *
	 * @author  wangjf
	 * @date    2015年5月9日 上午10:14:08
	 * @throws SLException
	 */
	@Test
	public void testWithdrawBaoNormal() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("custId", "20150427000000000000000000000000001");
		param.put("tradeAmount", "100");
		param.put("tradePassword", "dd9d21e22391090ddce7c6ed58c6412d");
		ResultVo result = investService.withdrawBaoNormal(param);
		assertEquals(ResultVo.isSuccess(result), true);
	}
	
	/**
	 * 测试普通赎回审核通过
	 *
	 * @author  wangjf
	 * @date    2015年5月9日 上午10:22:34
	 * @throws SLException
	 */
	@Test
	public void testAuditWithdrawBaoNormal_Pass() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("atoneId", "c987e836-4262-4c34-a032-cf5c0721b55e");
		param.put("auditCustId", "wangjf");
		param.put("auditStatus", Constant.AUDIT_STATUS_PASS);
		investService.auditWithdrawBaoNormal(param);
	}
	
	/**
	 * 测试普通赎回审核拒绝
	 *
	 * @author  wangjf
	 * @date    2015年5月9日 上午10:22:34
	 * @throws SLException
	 */
	@Test
	public void testAuditWithdrawBaoNormal_Refuse() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("atoneId", "91aab343-ed47-47c0-afe9-237297683a5f");
		param.put("auditCustId", "wangjf");
		param.put("auditStatus", Constant.AUDIT_STATUS_REfUSE);
		investService.auditWithdrawBaoNormal(param);
	}
	
	/**
	 * 加入体验宝
	 *
	 * @author  wangjf
	 * @date    2015年7月16日 上午11:45:44
	 * @throws SLException
	 */
	@Test
	public void testJoinExperienceBao() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("custId", "6221f6a5-c8d5-4196-8a3f-4dc50ffa37cd");
		param.put("tradeAmount", "10000");
		ResultVo result = investService.joinExperienceBao(param);
		assertEquals(ResultVo.isSuccess(result), true);
	}
	
	@Test
	public void test()
	{
		List<Map<String, Object>> list1 = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> list2 = new ArrayList<Map<String, Object>>();
		
		for(int i = 0; i < 20000; i ++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("aa", i);
			map.put("bb", i);
			list1.add(map);
		}
		
		for(int i = 0; i < 20000; i ++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("aa", i);
			map.put("bb", i);
			list2.add(map);
		}
		
		//BigDecimal b = new BigDecimal("0");
		Date now = new Date();
		for(Map<String, Object> m1 : list1) {
			for(Map<String, Object> m2 : list2) {
				if(m1.get("aa").toString().equals(m2.get("aa").toString())) {
					//b = new BigDecimal(m1.get("aa").toString());
					//System.out.println("==============" + m1.get("aa").toString() + "==============");
				}
			}
		}
		Date now2 = new Date();
		System.out.println("耗时" + String.valueOf(DateUtils.secondPhaseDiffer(now, now2)));
		//System.out.println(now);
		
		long maxDayWithdrawCount = 1;
		long dayWithdrawCount = 1;
		System.out.print(String.format("当日可赎回次数不能超过%d，实际赎回次数为%d", maxDayWithdrawCount, dayWithdrawCount));
	}
	
	private void test3(List<String> list) {
		
		list.add("1111");
		list.add("2222");
	}
	
	@Test
	public void test2() {
		List<String> list = new ArrayList<String>();
		test3(list);
		System.out.println(list);
	}
	
	@Test
	public void testQueryProductRate() throws SLException {
		Map<String, Object> params = new HashMap<String, Object>();
		//params.put("typeName", Constant.PRODUCT_TYPE_03);
		ResultVo reuslt = investService.queryProductRate(params);	
		assertEquals(ResultVo.isSuccess(reuslt), true);
		System.out.println(reuslt);
	} 
	/**
	 * 投资排行查询测试
	 */
	@Test
	public  void testQueryInvestRankInfo(){
		Map<String, Object> params = new HashMap<String, Object>();
		ResultVo reuslt =investService.queryInvestRankInfo(params);
		assertEquals(ResultVo.isSuccess(reuslt), true);
		System.out.println(reuslt.toString());
	}
}
