/** 
 * @(#)DataJpaServiceDemoTest.java 1.0.0 2015年4月14日 下午8:39:22  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */

package com.slfinance.shanlincaifu.dal.custom;

import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import com.slfinance.modules.test.spring.Profiles;
import com.slfinance.shanlincaifu.dal.base.AbstractSpringContextTestSupport;
import com.slfinance.shanlincaifu.repository.custom.ProductBusinessRepositoryCustom;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.SubjectConstant;

@ActiveProfiles(Profiles.DEVELOPMENT)
@ContextConfiguration(locations = { "/application-jpa.xml" })
public class ProductBusinessRepositoryTest extends AbstractSpringContextTestSupport {

	@Autowired
	private ProductBusinessRepositoryCustom productBusinessRepositoryCustom;

	@Test
	public void testFindBaoCurrentDetailInfo() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("productType", Constant.PRODUCT_TYPE_01);
		map.put("eranAccount", Constant.SUB_ACCOUNT_NO_ERAN);
		map.put("centerAccount", Constant.SUB_ACCOUNT_NO_CENTER);
		map.put("dateFormat", DateUtils.getCurrentDate("yyyyMMdd"));
		map.put("date", DateUtils.truncateDate(new Date()));
//		map.put("dateFormat", DateUtils.getCurrentDate("yyyyMMdd"));
//		map.put("date", DateUtils.getDateyyyyMMdd());
		Map<String, Object> result = productBusinessRepositoryCustom.findBaoCurrentDetailInfo(map);
		assertNotNull(result);
	}

	@Test
	public void testFindfindBaoCurrentVauleSum() {
		Map<String, Object> result = productBusinessRepositoryCustom.findBaoCurrentVauleSum();
		assertNotNull(result);
	}

	@Test
	public void testFfindBaoCurrentVauleSet() {
		Map<String, Object> result = productBusinessRepositoryCustom.findBaoCurrentVauleSet();
		assertNotNull(result);
	}

	@Test
	public void testUpdateBaoPreVaule() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("expectPreValue", 600);
		map.put("lastUpdateDate", new Date());
		map.put("productType", Constant.PRODUCT_TYPE_01);
		int i = productBusinessRepositoryCustom.updateBaoPreVaule(map);
		assertNotNull(i);
	}

	@Test
	public void testUpdateBaoOpenScale() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("allotScale", 0.8);
		map.put("lastUpdateDate", new Date());
		map.put("productType", Constant.PRODUCT_TYPE_01);
		int i = productBusinessRepositoryCustom.updateBaoOpenScale(map);
		assertNotNull(i);
	}

	@Test
	public void testLoanRepaymentForecast() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("productType", Constant.PRODUCT_TYPE_01);
		map.put("startDate", "20150428");
		map.put("endDate", "20150530");
		map.put("date", DateUtils.truncateDate(new Date()));
		//map.put("date", DateUtils.getDateyyyyMMdd());
		Map<String, Object> result = productBusinessRepositoryCustom.loanRepaymentForecast(map);
		assertNotNull(result);
	}

	@Test
	public void testFindLoanRepaymentList() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("productType", Constant.PRODUCT_TYPE_01);
		map.put("startDate", "20150428");
		map.put("endDate", "20150530");
		map.put("start", "1");
		map.put("length", "10");
		Page<Map<String, Object>> result = productBusinessRepositoryCustom.findLoanRepaymentList(map);
		assertNotNull(result);
	}

	@Test
	public void testLoanValueForecast() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("productType", Constant.PRODUCT_TYPE_01);
		map.put("eranAccount", Constant.SUB_ACCOUNT_NO_ERAN);
		map.put("centerAccount", Constant.SUB_ACCOUNT_NO_CENTER);
//		map.put("dateFormat", DateUtils.getCurrentDate("yyyyMMdd"));
//		map.put("date", DateUtils.getDateyyyyMMdd());
		map.put("queryDate", "201050428");
		Map<String, Object> result = productBusinessRepositoryCustom.loanValueForecast(map);
		assertNotNull(result);
	}

	// 测试更新产品状态
	@Test
	public void testUpdateProductStatus() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("productStatus", Constant.PRODUCT_STATUS_BID_ING);
		map.put("lastUpdateDate", new Date());
		map.put("productType", Constant.PRODUCT_TYPE_01);
		int i = productBusinessRepositoryCustom.updateProductStatus(map);
		assertNotNull(i);
	}

	// 测试更新产品状态
	@Test
	public void testUpdateProductDetail() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("tradeType", SubjectConstant.TRADE_FLOW_TYPE_01);
		map.put("lastUpdateDate", new Date());
		map.put("productType", Constant.PRODUCT_TYPE_01);
		int i = productBusinessRepositoryCustom.updateProductDetail(map);
		assertNotNull(i);
	}

	// 测试更新产品状态
	@Test
	public void testUpdateBaoAllotStatus() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("allotStatus", Constant.ALLOT_STATUS_02);
		map.put("oldAllotStatus", Constant.ALLOT_STATUS_01);
		map.put("lastUpdateDate", new Date());
		map.put("productType", Constant.PRODUCT_TYPE_01);
		int i = productBusinessRepositoryCustom.updateBaoAllotStatus(map);
		assertNotNull(i);
	}

	// 测试获取累计成交统计
	@Test
	public void testFindTotalTradetInfo() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("baoTradeType", SubjectConstant.TRADE_FLOW_TYPE_01);
		Map<String, Object> result = productBusinessRepositoryCustom.findTotalTradetInfo(map);
		assertNotNull(result);
	}
}
