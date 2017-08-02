/** 
 * @(#)ProductServiceTest.java 1.0.0 2015年5月4日 下午5:08:35  
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
 * 产品服务测试
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年5月4日 下午5:08:35 $ 
 */
public class ProductServiceTest  extends AbstractSpringContextTestSupport {

	@Autowired
	private ProductService productService;
	
	/**
	 * 测试关闭投标
	 *
	 * @author  wangjf
	 * @date    2015年5月4日 下午6:10:15
	 * @throws SLException
	 */
	@Test
	public void testCloseJob() throws SLException {
		Map<String, Object> param = new HashMap<String, Object>();
		productService.closeJob(param);
	}
	
	/**
	 * 测试体验宝赎回发送短信
	 *
	 * @author  caoyi
	 * @date    2015年7月2日 下午19:24:15
	 * @throws SLException
	 */
	@Test
	public void testExperienceWithdrawSendSms() throws SLException {
		productService.experienceWithdrawSendSms(new Date());
	}
	
	/**
	 * 定期宝关标
	 *
	 * @author  wangjf
	 * @date    2015年8月25日 下午12:13:54
	 * @throws SLException
	 */
	@Test
	public void testCloseTermJob() throws SLException {
		Map<String, Object> param = new HashMap<String, Object>();
		productService.closeTermJob(param);
	}
	
	/**
	 * 测试回收未赎回
	 *
	 * @author  wangjf
	 * @date    2015年12月15日 下午6:18:16
	 * @throws SLException
	 */
	@Test
	public void testRecoverUnAtone() throws SLException {
		Map<String, Object> param = new HashMap<String, Object>();
		productService.recoverUnAtone(param);
	}
}
