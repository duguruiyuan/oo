/** 
 * @(#)DataJpaServiceDemoTest.java 1.0.0 2015年4月14日 下午8:39:22  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.dal.custom;

import java.util.Date;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import com.slfinance.modules.test.spring.Profiles;
import com.slfinance.shanlincaifu.repository.ActivityInfoRepository;
import com.slfinance.shanlincaifu.utils.Constant;

@ActiveProfiles(Profiles.DEVELOPMENT)
@ContextConfiguration(locations = { "/application-jpa.xml" })
public class ActivityInfoRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests{

	@Autowired
	private ActivityInfoRepository activityInfoRepository;
	
	@Test
	public void findByActIdTest(){
		activityInfoRepository.findByActId("1", Constant.VALID_STATUS_VALID, new Date());
	}
	
	
	
	
}
