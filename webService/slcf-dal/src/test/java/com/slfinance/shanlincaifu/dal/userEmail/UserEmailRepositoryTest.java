/** 
 * @(#)UserEmailRepositoryTest.java 1.0.0 2015年4月24日 下午7:51:16  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.dal.userEmail;

import java.util.Date;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.slfinance.shanlincaifu.dal.base.AbstractSpringContextTestSupport;
import com.slfinance.shanlincaifu.entity.SmsInfoEntity;
import com.slfinance.shanlincaifu.repository.CustInfoRepository;
import com.slfinance.shanlincaifu.repository.SmsInfoRepository;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.util.CommUtil;

/**   O
 * 用户邮箱业务相关测试类
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年4月24日 下午7:51:16 $ 
 */
@Slf4j
public class UserEmailRepositoryTest extends AbstractSpringContextTestSupport{

	@Autowired
	CustInfoRepository custInfoRepository;
	
	@Autowired
	SmsInfoRepository smsInfoRepository;
	
	Map<String,Object> paramsMap = Maps.newHashMap();
	
	/**
	 * 校验用户数量
	 */
	@Test
	public void testCountByEmail() {
		Assert.assertNotNull(custInfoRepository.countByEmail("112212@qq.com"));
	}
	
	/**
	 * 测试根据地址和邮箱地址和类型查询第一个有效的消息
	 */
	@Test
	public void testCheckVerityCodeAndTargetAddress() {
		SmsInfoEntity smsInfoEntity = smsInfoRepository.checkVerityCodeAndTargetAddress("112212@qq.com", Constant.SMS_TYPE_BINDING_EMAIL,"zhangzs123456", new Date());
		log.info(JSONObject.toJSONString(smsInfoEntity));
	}
	
	/**
	 * 测试新增消息
	 */
	@Test
	public void testSaveSmsInfo() {
		SmsInfoEntity smsInfoEntity = new SmsInfoEntity("zhangzhisheng@shanlinjinrong", Constant.TARGET_TYPE_MAIL, "绑定邮箱", new Date(), Constant.SMS_SEND_STATUS_SENT,CommUtil.getUniqueString(), DateUtils.addHours(new Date(), Integer.parseInt("48")), Constant.VALID_STATUS_VALID, Constant.SMS_TYPE_BINDING_EMAIL, Constant.VALID_STATUS_VALID);
		smsInfoEntity.setBasicModelProperty("zhangzs", true);
		Assert.assertNotNull(smsInfoRepository.save(smsInfoEntity).getId());
	}
	
}
