/** 
 * @(#)ProductBusinessServiceTest.java 1.0.0 2015年4月28日 下午2:33:46  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */

package com.slfinance.shanlincaifu.service;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.vo.ResultVo;

/**
 * 
 * 
 * @author caoyi
 * @version $Revision:1.0.0, $Date: 2015年4月28日 下午2:33:46 $
 */
@ContextConfiguration(locations = { "classpath:/application-test.xml",
		"classpath:/applicationContext-restclient.xml" })
@ActiveProfiles("dev")
public class ProductBusinessServiceTest extends AbstractJUnit4SpringContextTests {

	@Autowired
	ProductBusinessService productBusinessService;

	// 测试获取活期宝今日明细
	@Test
	public void testFindBaoCurrentDetailInfo() throws SLException {
		Map<String, Object> map = productBusinessService.findBaoCurrentDetailInfo();
		assertTrue(!map.isEmpty());
	}

	// 测试获取活期宝当前价值分配
	@Test
	public void testFindBaoCurrentVauleSum() throws SLException {
		Map<String, Object> map = productBusinessService.findBaoCurrentVauleSum();
		assertTrue(!map.isEmpty());

	}

	// 测试获取活期宝分配规则
	@Test
	public void testFindBaoCurrentVauleSet() throws SLException {
		Map<String, Object> map = productBusinessService.findBaoCurrentVauleSet();
		assertTrue(!map.isEmpty());

	}

	// 测试重定义活期宝分配规则
	@Test
	public void testUpdateBaoSetVaule() throws SLException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("allotScale", "0.95");
		map.put("expectPreValue", 400);
		ResultVo ret = productBusinessService.updateBaoSetVaule(map);
		assertTrue(ResultVo.isSuccess(ret));

	}

	// 手工修改活期宝当前价值分配
	@Test
	public void testUpdateBaoCurrentVaule() throws SLException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("canOpenValue", "231446.03526882");
		map.put("openValue", "226229.11");
		map.put("alreadyPreValue", "600");
		map.put("unopenValue", "4616.92");
		ResultVo ret = productBusinessService.updateBaoCurrentVaule(map);
		assertTrue(ResultVo.isSuccess(ret));
	}

	// 获取债权还款预算
	@Test
	public void testLoanRepaymentForecast() throws SLException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("productType", Constant.PRODUCT_TYPE_01);
		map.put("startDate", "2015-04-28");
		map.put("endDate", "2015-05-30");
		Map<String, Object> map2 = productBusinessService.loanRepaymentForecast(map);
		assertTrue(!map2.isEmpty());
	}

	// 查询产品预计还款明细
	@Test
	public void testFindLoanRepaymentList() throws SLException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("productType", Constant.PRODUCT_TYPE_01);
		map.put("startDate", "2015-04-28");
		map.put("endDate", "2015-05-30");
		map.put("start", "1");
		map.put("length", "10");
		Map<String, Object> map2 = productBusinessService.findLoanRepaymentList(map);
		assertTrue(!map2.isEmpty());
	}

	// 获取债权价值预算
	@Test
	public void testLoanValueForecast() throws SLException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("productType", Constant.PRODUCT_TYPE_01);
		map.put("queryDate", "2015-04-28");
		Map<String, Object> map2 = productBusinessService.loanValueForecast(map);
		assertTrue(!map2.isEmpty());
	}

	// 发标(手动或定时)
	@Test
	public void testReleaseBid() throws SLException {
		productBusinessService.releaseBid();
	}

	// 可开放价值计算(手动或定时)
	@Test
	public void testComputeOpenValue() throws SLException {
		productBusinessService.computeOpenValue();
	}

	// 获取累计成交统计
	@Test
	public void testFindTotalTradetInfo() throws SLException {
		Map<String, Object> map = productBusinessService.findTotalTradetInfo();
		assertTrue(!map.isEmpty());
	}
	
	/**
	 * 测试理财计算器
	 *
	 * @author  wangjf
	 * @date    2015年7月21日 下午1:30:24
	 * @throws SLException
	 */
	@Test
	public void testIncomeCalculator() throws SLException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("investMethod", "推广佣金");
		map.put("investAmount", "10000");
		map.put("days", "200");
		Map<String, Object> result = productBusinessService.incomeCalculator(map);
		System.out.println(result);
		assertTrue(!result.isEmpty());
	}
	
    // 测试获取定期宝今日明细
	@Test
	public void testFindTermCurrentDetailInfo() throws SLException {
		Map<String, Object> map = productBusinessService.findTermCurrentDetailInfo();
		assertTrue(!map.isEmpty());
	}

	// 测试获取定期宝当前价值分配
	@Test
	public void testFindTermCurrentVauleSum() throws SLException {
		List<Map<String, Object>> list = productBusinessService.findTermCurrentVauleSum();
		assertTrue(!list.isEmpty());

	}
	
	// 测试获取定期宝分配规则
	@Test
	public void testFindTermCurrentVauleSet() throws SLException {
		List<Map<String, Object>> list = productBusinessService.findTermCurrentVauleSet();
		assertTrue(!list.isEmpty());

	}
	
	// 测试重定义定期宝分配规则
	@Test
	public void testUpdateTermSetVaule() throws SLException {
		List<Map<String, Object>> list= new ArrayList<Map<String,Object>>();
		Map<String, Object> map1 = new HashMap<String, Object>();
		map1.put("name", "鑫月盈");
		map1.put("type", "product");
		map1.put("value", "0.1");
		map1.put("code", "001");
		list.add(map1);
		Map<String, Object> map2 = new HashMap<String, Object>();
		map2.put("name", "鑫季丰");
		map2.put("type", "product");
		map2.put("value", "0.2");
		map2.put("code", "002");
		list.add(map2);
		Map<String, Object> map3 = new HashMap<String, Object>();
		map3.put("name", "双季盈");
		map3.put("type", "product");
		map3.put("value", "0.25");
		map3.put("code", "003");
		list.add(map3);
		Map<String, Object> map4 = new HashMap<String, Object>();
		map4.put("name", "鑫年丰");
		map4.put("type", "product");
		map4.put("value", "0.3");
		map4.put("code", "004");
		list.add(map4);
		Map<String, Object> map5 = new HashMap<String, Object>();
		map5.put("name", "三季喜");
		map5.put("type", "product");
		map5.put("value", "0.15");
		map5.put("code", "005");
		list.add(map5);
		Map<String, Object> map6 = new HashMap<String, Object>();
		map6.put("name", "预计预留价值");
		map6.put("type", "expectPreValue");
		map6.put("value", "500000");
		map6.put("code", "expectPreValue");
		list.add(map6);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("data", list);
		ResultVo ret = productBusinessService.updateTermSetVaule(map);
		assertTrue(ResultVo.isSuccess(ret));
	}
	
	// 手工修改定期宝宝当前价值分配
	@Test
	public void testUpdateTermCurrentVaule() throws SLException {
		List<Map<String, Object>> list= new ArrayList<Map<String,Object>>();
		Map<String, Object> map1 = new HashMap<String, Object>();
		map1.put("name", "鑫月盈");
		map1.put("type", "product");
		map1.put("value", "500");
		map1.put("code", "001");
		list.add(map1);
		Map<String, Object> map2 = new HashMap<String, Object>();
		map2.put("name", "鑫季丰");
		map2.put("type", "product");
		map2.put("value", "400");
		map2.put("code", "002");
		list.add(map2);
		Map<String, Object> map3 = new HashMap<String, Object>();
		map3.put("name", "双季盈");
		map3.put("type", "product");
		map3.put("value", "700");
		map3.put("code", "003");
		list.add(map3);
		Map<String, Object> map4 = new HashMap<String, Object>();
		map4.put("name", "鑫年丰");
		map4.put("type", "product");
		map4.put("value", "800");
		map4.put("code", "004");
		list.add(map4);
		Map<String, Object> map5 = new HashMap<String, Object>();
		map5.put("name", "三季喜");
		map5.put("type", "product");
		map5.put("value", "700");
		map5.put("code", "005");
		list.add(map5);
		Map<String, Object> map6 = new HashMap<String, Object>();
		map6.put("name", "实际预留价值");
		map6.put("type", "alreadyPreValue");
		map6.put("value", "100");
		map6.put("code", "alreadyPreValue");
		list.add(map6);
		Map<String, Object> map7 = new HashMap<String, Object>();
		map7.put("name", "未开放价值");
		map7.put("type", "unopenValue");
		map7.put("value", "400");
		map7.put("code", "unopenValue");
		list.add(map7);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("data", list);
		ResultVo ret = productBusinessService.updateTermCurrentVaule(map);
		assertTrue(ResultVo.isSuccess(ret));
	}
}
