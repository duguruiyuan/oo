/** 
 * @(#)DataJpaServiceDemoTest.java 1.0.0 2015年4月14日 下午8:39:22  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.dal.custom;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import com.slfinance.modules.test.spring.Profiles;
import com.slfinance.shanlincaifu.repository.SmsInfoRepository;
import com.slfinance.shanlincaifu.utils.Constant;

@ActiveProfiles(Profiles.DEVELOPMENT)
@ContextConfiguration(locations = { "/application-jpa.xml" })
public class SmsInfoRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests{


	@Autowired
	private SmsInfoRepository smsInfoRepository;
	@Test
	public void findByAddressAndTypeAndDateTest(){
		List<Object[]> objList=smsInfoRepository.findByAddressAndTypeAndDate("13111111111", 
				Constant.SMS_TYPE_REGISTER,"20150507","20150507", "20150507");
		for(Object[] obj:objList){
			System.out.println(obj[0]+":"+obj[1]);
		}
		System.out.println(objList.size());
	}
}
