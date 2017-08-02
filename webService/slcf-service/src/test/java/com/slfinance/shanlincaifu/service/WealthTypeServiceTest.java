package com.slfinance.shanlincaifu.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.slfinance.exception.SLException;
import com.slfinance.vo.ResultVo;

/**
 * WealthTypeServiceImpl Test. @author Tools
 */
public class WealthTypeServiceTest extends AbstractSpringContextTestSupport  {


	@Autowired
	private WealthTypeInfoService wealthTypeService;

	@Test
	public void findWealthType(){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("enableStatus", "启用");
		ResultVo resultVo = wealthTypeService.findWealthType(params);
		assertNotNull(resultVo);
	}
	
	@Test
	public void findWealthTypeById(){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("wealthTypeId", "1");
		ResultVo resultVo = wealthTypeService.findWealthTypeById(params);
		assertNotNull(resultVo);
	}
	
	@Test
	public void queryWealthTypeList(){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("start", "0");
		params.put("length", "20");
		params.put("lendingType", "1");
		params.put("typeTerm", "1");
		params.put("incomeType", "1");
		params.put("enableStatus", "无效");
		ResultVo resultVo = wealthTypeService.queryWealthTypeList(params);
		assertNotNull(resultVo);
	}
	
	@Test
	public void queryWealthTypeDetailById(){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("wealthTypeId", "1");
		ResultVo resultVo = wealthTypeService.queryWealthTypeDetailById(params);
		assertNotNull(resultVo);
	}
	
	@Test
	public void saveWealthType() throws SLException{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("lendingType", "鑫月盈321");
		params.put("typeTerm", "3");
		params.put("yearRate", "0.3");
		params.put("incomeType", "3");
		params.put("enableStatus", "有效");
		params.put("userId", "1122");
		params.put("sort", "444444");
		ResultVo resultVo1 = wealthTypeService.saveWealthType(params);
//		params.put("wealthTypeId", "0a8fc8bc-f9c7-4969-b1a1-ed19b597da50");
//		params.put("yearRate", "0.2233");
//		params.put("userId", "123465");
//		ResultVo resultVo2 = wealthTypeService.saveWealthType(params);
		
		assertNotNull(resultVo1);
		assertEquals(ResultVo.isSuccess(resultVo1), true);
//		assertNotNull(resultVo2);
//		assertEquals(ResultVo.isSuccess(resultVo2), true);
	}
	
	@Test
	public void enableWealthType() throws SLException{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("wealthTypeId", "1");
		params.put("enableStatus", "有效");
		params.put("userId", "123456");
		ResultVo resultVo = wealthTypeService.enableWealthType(params);
		assertNotNull(resultVo);
	}
	
	@Test
	public void queryMatchRuleList() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("start", "0");
		params.put("length", "20");
		ResultVo resultVo = wealthTypeService.queryMatchRuleList(params);
		assertNotNull(resultVo);
	}

	@Test
	public void saveMatchRule() throws SLException {
		Map<String, Object> params = new HashMap<String, Object>();
//		params.put("matchRuleId", "");
		params.put("InvestMiniAmt", "1000");
		params.put("InvestMaxAmt", "5000");
		params.put("DebtMiniAmt", "5000");
		params.put("DebtMaxAmt", "10000");
		params.put("userId", "123");
		ResultVo resultVo1 = wealthTypeService.saveMatchRule(params);
		
		params.put("matchRuleId", "1");
		params.put("userId", "987");
		ResultVo resultVo2 = wealthTypeService.saveMatchRule(params);
		assertNotNull(resultVo1);
		assertNotNull(resultVo2);
	}
	
	@Test
	public void deleteMatchRule() throws SLException {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("matchRuleId", "1");
		params.put("userId", "987");
		
		ResultVo resultVo = wealthTypeService.deleteMatchRule(params);
		assertNotNull(resultVo);
	}
}