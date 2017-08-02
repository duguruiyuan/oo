/** 
 * @(#)SubAccountInfoRepositoryTest.java 1.0.0 2015年4月30日 下午3:36:07  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.dal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import com.slfinance.modules.test.spring.Profiles;
import com.slfinance.shanlincaifu.entity.SubAccountInfoEntity;
import com.slfinance.shanlincaifu.repository.SubAccountInfoRepository;
import com.slfinance.shanlincaifu.utils.Constant;

/**   
 * 测试分账户访问类
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年4月30日 下午3:36:07 $ 
 */
@ActiveProfiles(Profiles.DEVELOPMENT)
@ContextConfiguration(locations = { "/application-jpa.xml" })
public class SubAccountInfoRepositoryTest  extends AbstractTransactionalJUnit4SpringContextTests{

	@Autowired
	private SubAccountInfoRepository subAccountInfoRepository;
	
	@Test
	public void testQueryUserAllValue()
	{
		BigDecimal totalValue = subAccountInfoRepository.queryUserAllValue("20150427000000000000000000000000001", Constant.PRODUCT_TYPE_01, Constant.VALID_STATUS_VALID);
		assertNotNull(totalValue);
		assertEquals(totalValue.compareTo(new BigDecimal("0")) != 0, true);
	}
	
	@Test
	public void testQueryTermAtone() {
		List<SubAccountInfoEntity> list = subAccountInfoRepository.queryTermAtone(Constant.PRODUCT_TYPE_04, Constant.TRADE_STATUS_01, new Date());
		assertNotNull(list);
	}
}
