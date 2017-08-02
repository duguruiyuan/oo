package com.slfinance.shanlincaifu.service;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestOperations;


import com.alibaba.fastjson.JSONObject;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.utils.JsonUtil;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/application-test.xml" })
@ActiveProfiles("dev")
public class RepaymentPlanChangeTest {
	
	
	@Autowired
	private TppRepaymentChangeService tppRepaymentChangeService;

	@Test
	public void testrepaymentPlanChangeRequest() throws SLException {
		JSONObject obj = new JSONObject();
	    obj.put("advanceCleanupTotalAmount", "850");
	    obj.put("currentTerm", "1");
	    obj.put("expectRepaymentDate", "20170627");
	    obj.put("repaymentPrincipal", "850");
	    obj.put("repaymentTotalAmount", "850");
//		String repaymentPlan = JsonUtil.jsonToString(obj);//json格式字符串
		Random rm  = new Random();
		double pross = (1 + rm.nextDouble()) * Math.pow(10, 13); //生成13位随机数
	    String fixLenthString = String.valueOf(pross);  
		String batchCode = "SLCF"+"-"+"BATCH"+"-"+fixLenthString.substring(2, 13 + 2);
		JSONObject smsParamobj = new JSONObject() ;
		smsParamobj.put("contact_type", "融资租赁");
		smsParamobj.put("contact_way", "0571-12345678");
//		String smsParam = JsonUtil.jsonToString(smsParamobj);
		JSONObject reqobj = new JSONObject();
		reqobj.put("platformUserNo", "001");
		reqobj.put("rempaymentPlan",obj);
		reqobj.put("smsparam", smsParamobj);
		reqobj.put("repayment_state", "1");
		reqobj.put("repaymentNo", "111111");
//		String reqData = JsonUtil.jsonToString(reqobj);
		Map<String, Object> TppNeedParams =new HashMap<String, Object>();
		TppNeedParams.put("reqData", reqobj);
		TppNeedParams.put("buzName","SLCF");
		TppNeedParams.put("requestTime", "");
		TppNeedParams.put("batchCode", batchCode);
		TppNeedParams.put("serviceName", "REPAYMENT_PLAN_CHANGE");
		TppNeedParams.put("platform", "lianpay");
		TppNeedParams.put("userDevice", "PC");
		tppRepaymentChangeService.repaymentChange_Tpp(TppNeedParams);
		
		
	}
}
