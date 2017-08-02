/** 
 * @(#)InvestInfoRepositoryTest.java 1.0.0 2015年5月25日 下午7:37:53  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.dal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import com.slfinance.modules.test.spring.Profiles;
import com.slfinance.shanlincaifu.repository.InvestInfoRepository;
import com.slfinance.shanlincaifu.repository.custom.DailySettlementRepository;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;

/**   
 * 测试投资
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年5月25日 下午7:37:53 $ 
 */
@ActiveProfiles(Profiles.DEVELOPMENT)
@ContextConfiguration(locations = { "/application-jpa.xml" })
public class InvestInfoRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests{
	
	@Autowired
	private InvestInfoRepository investInfoRepository;
	
	@Autowired
	private DailySettlementRepository dailySettlementRepository;
	
	/**
	 * 测试统计投资次数
	 *
	 * @author  wangjf
	 * @date    2015年5月25日 下午7:41:42
	 */
	@Test
	public void testCountInvestInfoByCustId() {
		BigDecimal counts = investInfoRepository.countInvestInfoByCustId("20150427000000000000000000000000001", "1");
		assertNotNull(counts);
		assertEquals(counts.compareTo(new BigDecimal("0")) != 0, true);
	}
	
	/**
	 * 测试查询满足体验宝赎回的投资
	 *
	 * @author  wangjf
	 * @date    2015年7月16日 上午11:35:24
	 */
	@Test
	public void testFindTYBExpireSubAccount() {
		Map<String,Object> params=new HashMap<>();
		params.put("execDate", DateUtils.formatDate(new Date(), "yyyyMMdd"));
		params.put("typeName", Constant.PRODUCT_TYPE_03);
		List<Map<String, Object>> list = dailySettlementRepository.findTYBExpireSubAccount(params);
		assertNotNull(list);
		assertEquals(list.size() != 0, true);
	}
}
