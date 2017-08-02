/** 
 * @(#)OpenServiceTest.java 1.0.0 2015年7月2日 下午4:23:23  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;



import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.google.common.xml.XmlEscapers;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.PasswordUtil;

/**   
 * 对外服务测试类
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年7月2日 下午4:23:23 $ 
 */
@ContextConfiguration(locations = { "classpath:/application-test.xml", "classpath:/applicationContext-restclient.xml" })
@ActiveProfiles("dev")
public class OpenServiceTest extends AbstractJUnit4SpringContextTests { 
	
	@Autowired
	private OpenService openService;
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private OpenNotifyService openNotifyService;
	
	/**
	 * 注册
	 *
	 * @author  wangjf
	 * @throws SLException 
	 * @date    2015年7月2日 下午4:25:29
	 */
	@Test
	public void testRegister() throws SLException {
		Map<String, Object> params = new HashMap<String, Object>();
		String mobile = "18521509556";
		String loginName = "wangjingfeng2666";
		String loginPassword = "a1!@#$%^&*()aA";
		String ipAddress = "";
		String utid = "123456789";
		String channelNo = "2015070100000001";
		String requestTime = DateUtils.formatDate(new Date(), "yyyyMMddHHmmss");
		String validateCode = "123456";
		String appSource = "web";
		String hashString = "slb" + mobile + loginName + loginPassword + ipAddress + utid + channelNo + requestTime + validateCode + appSource;
		String sign = Hashing.md5().hashString(hashString, Charsets.UTF_8).toString();
		
		params.put("mobile", mobile);
		params.put("loginName", loginName);
		params.put("loginPassword", loginPassword);
		params.put("utid", utid);
		params.put("ipAddress", "");
		params.put("channelNo", channelNo);
		params.put("requestTime", requestTime);
		params.put("validateCode", validateCode);
		params.put("appSource", appSource);
		params.put("sign", sign);
		
		System.out.println(XmlEscapers.xmlContentEscaper().escape("a1!@#$%^&*()aA"));
		System.out.println(PasswordUtil.getPwdLevel(PasswordUtil.getPwdSecurityLevel((String)params.get("loginPassword"))));
		
		openService.openRegister(params);
	}
	
	@Test
	public void testSendSms() throws SLException {
		Map<String, Object> params = new HashMap<String, Object>();
		String mobile = "18521509555";
		String utid = "123456789";
		String channelNo = "2015070100000001";
		String requestTime = DateUtils.formatDate(new Date(), "yyyyMMddHHmmss");
		String hashString = "slb" + mobile + utid + channelNo + requestTime;
		String sign = Hashing.md5().hashString(hashString, Charsets.UTF_8).toString();
		
		params.put("mobile", mobile);
		params.put("utid", utid);
		params.put("channelNo", channelNo);
		params.put("requestTime", requestTime);
		params.put("sign", sign);
		
		params.put("messageType", Constant.SMS_TYPE_REGISTER);
		params.put("custId", Constant.SYSTEM_USER_BACK);
		
		openService.openSendSms(params);
	}
	
	@Test
	public void testRegister2() throws SLException {
		Map<String, Object> params = new HashMap<String, Object>();
		String mobile = "18521509556";
		String loginName = "wangjingfeng2005";
		String loginPassword = "a1!@#$%^&*()aA";
		params.put("mobile", mobile);
		params.put("loginName", loginName);
		params.put("loginPassword", loginPassword);
		params.put("messageType", Constant.SMS_TYPE_REGISTER);
		params.put("verityCode", "367532");
		
		customerService.register(params);
	}
	
	@Test
	public void testAsynNotify() throws SLException {
		openNotifyService.asynNotify();
	}
	
	@Test
	public void testSaveDownloadMessage()throws SLException  {
		Map<String, Object> params = new HashMap<String, Object>();
		String mobile = "";
		String meid= "231";
		String meversion="ios";
		String appSource = "ios";
		String requestNo = "3123213213";
		String requestTime = DateUtils.formatDate(new Date(), "yyyyMMddHHmmss");
		requestTime = "31231";
		String channelNo = "2015101200000001";
		String partner_no = "111";
		String hashString = "slb" + mobile + meid
		+ meversion + appSource + requestNo 
		+ requestTime + channelNo + partner_no;
		String sign = Hashing.md5().hashString(hashString, Charsets.UTF_8).toString();
		params.put("mobile", mobile);
		params.put("meid", meid);
		params.put("meversion", meversion);
		params.put("appSource", appSource);
		params.put("requestNo", requestNo);
		params.put("requestTime", requestTime);
		params.put("channelNo", channelNo);
		params.put("partner_no", partner_no);
		params.put("sign", sign);
		openService.saveDownloadMessage(params);
	}
	
	@Test
	public void testConfirmStatus() throws SLException {
		Map<String, Object> params = new HashMap<String, Object>();
		String request_no = "SLBAO-TRADE-000000009724";
		String result_code = "00000";
		params.put("request_no", request_no);
		params.put("result_code", result_code);
		openService.confirmStatus(params);
	}
}
