/** 
 * @(#)BankCardInfoRepositoryTest.java 1.0.0 2015年4月28日 下午5:16:47  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.dal.custom;

import static org.junit.Assert.assertEquals;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.slfinance.shanlincaifu.dal.base.AbstractSpringContextTestSupport;
import com.slfinance.shanlincaifu.repository.custom.BankCardInfoRepositoryCustom;

/**   
 * 银行卡测试类
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年4月28日 下午5:16:47 $ 
 */
public class BankCardInfoRepositoryTest  extends AbstractSpringContextTestSupport{

	@Autowired
	BankCardInfoRepositoryCustom bankCardInfoRepositoryCustom;
	
	/**
	 * 测试根据银行编号获取银行名称
	 *
	 * @author  wangjf
	 * @date    2015年4月28日 下午5:18:15
	 */
	@Test
	public void testFindByBankCode()
	{
		String bankName = bankCardInfoRepositoryCustom.findByBankCode("00080001");
		assertEquals(!StringUtils.isEmpty(bankName), true);
	}
	
	/**
	 * 测试根据银行编号获取银行名称
	 *
	 * @author  wangjf
	 * @date    2015年4月28日 下午5:18:15
	 */
	@Test
	public void testFindByBankName()
	{
		String bankName = bankCardInfoRepositoryCustom.findByBankName("招商银行");
		assertEquals(!StringUtils.isEmpty(bankName), true);
	}
}
