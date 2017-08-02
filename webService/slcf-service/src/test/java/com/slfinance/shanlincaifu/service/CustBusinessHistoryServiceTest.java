package com.slfinance.shanlincaifu.service;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.slfinance.exception.SLException;

public class CustBusinessHistoryServiceTest extends AbstractSpringContextTestSupport{

	@Autowired
	private CustBusinessHistoryService custBusinessHistoryService;
	
	@Test
	public void findCustBusinessHistoryInfoTest() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("start", 0);
		param.put("length", 10);
/*		String arr = "PC端,PC";
		param.put("appSource", arr);*/
		//param.put("startDate", "2014-12-01");
		//param.put("endDate", "2014-12-31");
		param.put("recordDate", "2015-12");
		param.put("userId", "1");
		param.put("custId", "1");
		//{userId=1, custId=1, recordDate=2015-12, start=10, length=10}
		Map<String, Object> result = custBusinessHistoryService.findCustBusinessHistoryInfo(param);
		param.put("start", 10);
		param.put("length", 10);
		result = custBusinessHistoryService.findCustBusinessHistoryInfo(param);
		
		System.out.println(result);
	}
	
	@Test
	public void exportTest() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("start", 0);
		param.put("length", 10);
		custBusinessHistoryService.export(param);
	}
}
