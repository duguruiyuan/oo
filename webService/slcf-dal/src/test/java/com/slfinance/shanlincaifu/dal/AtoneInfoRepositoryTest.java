/** 
 * @(#)AtoneInfoRepositoryTest.java 1.0.0 2015年4月30日 下午1:38:39  
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
import com.slfinance.shanlincaifu.entity.AtoneInfoEntity;
import com.slfinance.shanlincaifu.repository.AtoneInfoRepository;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;

/**   
 * 测试赎回数据访问类
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年4月30日 下午1:38:39 $ 
 */
@ActiveProfiles(Profiles.DEVELOPMENT)
@ContextConfiguration(locations = { "/application-jpa.xml" })
public class AtoneInfoRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests{

	@Autowired
	private AtoneInfoRepository atoneInfoRepository;
	
	@Test
	public void testQueryByCreateDate()
	{
		List<Object> list = atoneInfoRepository.queryByCreateDate("20150427000000000000000000000000001", "1", DateUtils.parseDate("1970-1-1", "yyyy-MM-dd"), DateUtils.getEndDate(new Date()), Constant.ATONE_METHOD_NORMAL);
		Object[] obj = (Object[])list.get(0);
		BigDecimal totalAmount = (BigDecimal)obj[0];
		long counts = (long)obj[1];
		assertNotNull(totalAmount);
		assertEquals(totalAmount.compareTo(new BigDecimal("0")) != 0, true);
		assertEquals(counts != 0, true);
	}
	
	@Test
	public void testQueryByCleanupDate()
	{
		List<AtoneInfoEntity> list = atoneInfoRepository.queryByCleanupDate(Constant.AUDIT_STATUS_PASS, DateUtils.getStartDate(new Date()), DateUtils.getEndDate(new Date()));
		assertNotNull(list);
	}
	
	/**
	 * 计算已经审核处理成功的已赎回金额
	 */
	@Test
	public void testFindSumAlreadyAtoneAmount()
	{
		atoneInfoRepository.findSumAlreadyAtoneAmount("12", "12", Constant.TRADE_STATUS_03, Constant.AUDIT_STATUS_PASS);
	}
}
