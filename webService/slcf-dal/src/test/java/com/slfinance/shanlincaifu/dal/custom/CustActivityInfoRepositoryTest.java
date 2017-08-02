/** 
 * @(#)CustActivityRepositoryTest.java 1.0.0 2015年4月14日 下午8:39:22  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */

package com.slfinance.shanlincaifu.dal.custom;

import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import com.slfinance.modules.test.spring.Profiles;
import com.slfinance.shanlincaifu.dal.base.AbstractSpringContextTestSupport;
import com.slfinance.shanlincaifu.repository.custom.CustActivityInfoRepositoryCustom;

@ActiveProfiles(Profiles.DEVELOPMENT)
@ContextConfiguration(locations = { "/application-jpa.xml" })
public class CustActivityInfoRepositoryTest extends AbstractSpringContextTestSupport {

	@Autowired
	private CustActivityInfoRepositoryCustom custActivityRepositoryInfoCustom;

	// 测试获取我的推荐记录
	@Test
	public void testFindCustRecommendList() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("start", 0);
		map.put("length", 10);
		map.put("custId", "ef397a8e-a2ba-4f32-8546-8bbd0ef57fc6");//7bdb04d1-953c-4092-ab49-b5431463b8d8
		//map.put("spreadLevel", 1);
		//map.put("registDateBegin", "2015-01-01");
		//map.put("registDateEnd", "2015-05-19");
		//map.put("realName", "是");
		//map.put("invest","是");
		//map.put("tradeStatus","否");	
		Page<Map<String, Object>> result = custActivityRepositoryInfoCustom.findCustRecommendList(map);
		assertNotNull(result);
	}

	// 测试获取我的体验金记录
	@Test
	public void testFindCustExperienceList() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("start", 0);
		map.put("length", 10);
		map.put("custId", "26b0048b-10a4-44a4-abaa-b380250fae4a");
		map.put("source", "注册");
		map.put("receiveDateBegin", "2015-01-01");
		map.put("receiveDateEnd", "2015-05-20");
		map.put("tradeStatus", "正常");
		Page<Map<String, Object>> result = custActivityRepositoryInfoCustom.findCustExperienceList(map);
		assertNotNull(result);
	}
	

	// 测试推荐奖励信息
	@Test
	public void testFindRewardById() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("custId", "26b0048b-10a4-44a4-abaa-b380250fae4a");
		Map<String, Object> result = custActivityRepositoryInfoCustom.findRewardById(map);
		assertNotNull(result);
	}
	
	/**
	 * 测试计算客户推荐奖励
	 *
	 * @author  wangjf
	 * @date    2015年6月4日 下午4:02:32
	 */
	@Test
	public void testCaclCustActivityDetail() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("custId", "27fafe2f-d6b0-4317-bf25-22bdd2d8fad4");
		map.put("custSpreadLevel", "4");
		map.put("queryPermission", "00000027000000002000000024000000015000000016");
		custActivityRepositoryInfoCustom.caclCustActivityDetail(map);
	}
}
