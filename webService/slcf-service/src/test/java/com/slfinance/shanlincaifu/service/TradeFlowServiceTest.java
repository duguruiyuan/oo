package com.slfinance.shanlincaifu.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.google.common.collect.Maps;
import com.slfinance.shanlincaifu.entity.TradeFlowInfoEntity;

@ContextConfiguration(locations = { "classpath:/application-test.xml" })
@ActiveProfiles("dev")
public class TradeFlowServiceTest extends AbstractJUnit4SpringContextTests{

	@Autowired
	TradeFlowService tradeFlowService;
	
	@Test
	public void findTradeFlowInfoPagableTest() {
		Map<String, Object> query = Maps.newHashMap();
		query.put("custId", "20150427000000000000000000000000001");
		query.put("tradeType", "充值");
		query.put("beginDate", DateTime.parse("2015-4-1").toDate());
		Page<TradeFlowInfoEntity> map= tradeFlowService.findTradeFlowInfoPagable(query);
		
		assertNotNull(map);
		assertEquals(map.getTotalElements(), 2);
	}
}
