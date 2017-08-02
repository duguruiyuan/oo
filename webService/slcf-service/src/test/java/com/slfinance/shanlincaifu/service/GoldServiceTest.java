/** 
 * @(#)GoldServiceTest.java 1.0.0 2015年8月25日 上午11:35:28  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.slfinance.exception.SLException;

/**   
 * 金牌推荐人测试
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年8月25日 上午11:35:28 $ 
 */
public class GoldServiceTest  extends AbstractSpringContextTestSupport{

	@Autowired
	private GoldService goldService;
	
	/**
	 * 测试金牌推荐人每日结算
	 *
	 * @author  wangjf
	 * @throws SLException 
	 * @date    2015年8月25日 上午11:37:36
	 */
	@Test
	public void testGoldDailySettlement() throws SLException {
		goldService.goldDailySettlement(null);
	}
	
	/**
	 * 测试金牌推荐人到期结算
	 *
	 * @author  wangjf
	 * @date    2015年8月25日 上午11:49:10
	 * @throws SLException
	 */
	@Test
	public void testGoldWithdraw() throws SLException {
		Map<String, Object> params = new HashMap<String, Object>();
		//params.put("date", DateUtils.addMonths(new Date(), 1));
		params.put("date", new Date());
		goldService.goldWithdraw(params);
	}
	
	/**
	 * 测试金牌推荐人定期宝月结
	 *
	 * @author  wangjf
	 * @date    2015年10月12日 下午4:46:03
	 * @throws SLException
	 */
	@Test
	public void testGoldMonthlySettlement() throws SLException {
		goldService.goldMonthlySettlement(null);
	}
	
}
