/** 
 * @(#)UserEmailRepositoryTest.java 1.0.0 2015年4月24日 下午7:51:16  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.userEmail;

import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.AbstractSpringContextTestSupport;
import com.slfinance.shanlincaifu.service.UserEmailService;
import com.slfinance.shanlincaifu.service.impl.EmailService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.spring.mail.MailType;
import com.slfinance.vo.ResultVo;

/**   
 * 用户邮箱业务相关测试类
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年4月24日 下午7:51:16 $ 
 */

public class UserEmailServiceTest extends AbstractSpringContextTestSupport{

	@Autowired
	private UserEmailService userEmailService;
	
	Map<String, Object> paramsMap = Maps.newHashMap();
	
	@Autowired
	EmailService emailService;

	/**
	 * 检查该邮箱是否已经存在
	 */
	@Test
	public void checkEmailIsExist() throws SLException {
		paramsMap.clear();
		paramsMap.put("email", "zhangzhisheng@shanlinjinrong.com");
		Assert.assertNotNull(userEmailService.checkEmailIsExist(paramsMap));
	}
	
	/** 
	 * 登陆用户发送绑定邮箱业务
	 */
	@Test
	public void testSendMailForBindEmail() throws SLException {
		paramsMap.clear();
		paramsMap.put("custId", "zhangzs");
		paramsMap.put("targetAddress", "zhangzhisheng@shanlinjinrong.com");
		userEmailService.sendMailForBindEmail(paramsMap);
	}
	
	/**
	 * 邮箱回调地址校验邮箱验证码和验证地址
	 *            <tt>verityCode:String:邮箱验证码</tt>
	 *            <tt>targetAddress:String:邮箱地址</tt>
	 */
	@Test
	public void testCheckVerityCodeAndTargetAddress() throws SLException {
		paramsMap.clear();
		paramsMap.put("verityCode", "20150512153556772dff5e11d3422bbfcc4fef8c6b1655");
		paramsMap.put("targetAddress", "317919091@qq.com");
		userEmailService.checkVerityCodeAndTargetAddress(paramsMap);
	}
	

	/**
	 * 验证用户成功，更新验证码信息和用户信息
	 *            <tt>custId:String:客户ID</tt>
	 *            <tt>messageType:String:消息类型</tt>
	 *            <tt>verityCode:String:邮箱验证码</tt>
	 *            <tt>targetAddress:String:邮箱地址</tt>
	 */
	@Test
	public void testUpdateCustEmail() throws SLException {
		paramsMap.put("custId", "zhangzs");
		paramsMap.put("verityCode", "20150425175607105738d58d1946b58d737d4d359bed53");
		paramsMap.put("targetAddress", "zhangzhisheng@shanlinjinrong.com");
		paramsMap.put("messageType", Constant.SMS_TYPE_BINDING_EMAIL);
		userEmailService.updateCustEmail(paramsMap);
		
	}
	
	/**
	 *  修改绑定邮箱--发送修改邮箱邮件
	 * 				<tt>custId:String:用户Id</tt>
	 * 				<tt>targetAddressOld:String:旧邮箱地址</tt>
	 * 				<tt>targetAddress:String:新邮箱地址</tt>
	 */
	@Test
	public void sendMailForUpdateEmail() throws SLException {
		paramsMap.clear();
		paramsMap.put("custId", "zhangzs");
		paramsMap.put("targetAddress", "zhangzhisheng@shanlinjinrong.com");
		paramsMap.put("targetAddressOld", "zhangzhisheng@shanlinjinrong.com");
		userEmailService.sendMailForUpdateEmail(paramsMap);
	}
	
	@Test
	public void test() throws SLException {
		//调用邮件服务发送邮件
		Map<String,Object> smsInfo = Maps.newHashMap();
		smsInfo.put("to", "zhangzhisheng@shanlinjinrong.com");
		smsInfo.put("type", MailType.TEXT);
		smsInfo.put("title", "活期宝-邮箱绑定");
		smsInfo.put("ftlPath", "bindEmailTemplate.ftl");
		
		Map<String,Object> dataSet = Maps.newHashMap(); 
		dataSet.put("url", "http://localhost:8050");
		dataSet.put("custId", "zhangzs");
		dataSet.put("verityCode", "zhangzzsCode");
		dataSet.put("targetAddress", "zhangzhisheng@shanlinjinrong.com");
		dataSet.put("invalidHours", "48");
		dataSet.put("invalidDate", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
		dataSet.put("customerTle", "");
		dataSet.put("customerEmail", "");
		smsInfo.put("dataSet", dataSet);
		
		ResultVo result = emailService.sendEmail(smsInfo);
		System.out.println(JSONObject.toJSON(result));
	}

}
