package com.slfinance.shanlincaifu.service;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.slfinance.exception.SLException;

public class SMSServiceTest extends AbstractSpringContextTestSupport{

	@Autowired
	SMSService sMSService;

	@Test
	public void checkMobileAndMessageTypeTest() throws SLException{
		Map<String, Object> params=new HashMap<>();
		params.put("mobile", "13111111111");
		params.put("messageType", "注册");
		params.put("verityCode", "123123");
		this.sMSService.checkMobileAndMessageType(params);
	}
	
	@Test
	public void findByMobileAndMessageTypeTest(){
		Map<String, Object> params=new HashMap<>();
		params.put("mobile", "13111111111");
		params.put("messageType", "注册");
		params.put("verityCode", "123123");
		sMSService.findByMobileAndMessageType(params);
		
	}

	@Test
	public void sendSMSTest(){
		Map<String, Object> params=new HashMap<>();
		params.put("mobile", "13111111111");
		params.put("messageType", "注册");
		sMSService.sendSMS(params);
	}
	
	@Test
	public void findByAddressAndTypeAndDateTest(){
		Map<String, Object> params=new HashMap<>();
		params.put("mobile", "13111111111");
		params.put("smsType", "注册");
		sMSService.findByAddressAndTypeAndDate(params);
	}
}
