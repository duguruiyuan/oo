package com.slfinance.shanlincaifu.service;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.slfinance.exception.SLException;
import com.slfinance.vo.ResultVo;

public class TransAccountServiceTest extends AbstractSpringContextTestSupport{
	
	@Autowired
	TransAccountService transAccountService;
	
	@Test
	public void queryCompanyTransAccountListTest(){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("start", 0);
		params.put("length", 10);
		transAccountService.queryCompanyTransAccountList(params);
	}

	@Test
	public void queryCompanyTransAccountByIdTest(){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("transId", "efbcf17f-8ce7-4dfe-9faf-db7465c1fca6");
		ResultVo resultVo = transAccountService.queryCompanyTransAccountById(params);
		System.out.println(resultVo);
	}
	
	@Test
	public void auditCompanyTransAccountTest() throws SLException{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("transId", "a2a9f45d-507b-4881-868d-e99ff18c955c");
		params.put("auditMemo", "哈哈哈");
		params.put("auditStatus", "通过");
		transAccountService.auditCompanyTransAccount(params);
	}
	
	@Test
	public void saveCompanyTransAccountTest() throws SLException{
		Map<String, Object> params = new HashMap<String, Object>();
		
		params.put("companyName", "213");
		params.put("projectType", "111");
		transAccountService.saveCompanyTransAccount(params);
	}
}