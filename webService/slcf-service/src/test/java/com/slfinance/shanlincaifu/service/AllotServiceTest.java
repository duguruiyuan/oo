/** 
 * @(#)AllotServiceTest.java 1.0.0 2015年5月5日 下午4:31:51  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.slfinance.exception.SLException;

/**   
 * 测试债权分配
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年5月5日 下午4:31:51 $ 
 */
public class AllotServiceTest extends AbstractSpringContextTestSupport{

	@Autowired
	private AllotService allotService;
	
	/**
	 * 取消分配
	 *
	 * @author  wangjf
	 * @throws SLException 
	 * @date    2015年5月5日 下午4:51:20
	 */
	@Test
	public void testCancelLoanAllot() throws SLException{
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("id", "781bdbdd-2edd-4b6b-8887-acc53c8ebcfe");
		param.put("updateUser", "20150427000000000000000000000000001");
		allotService.cancelLoanAllot(param);
	}
	
	/**
	 * 债权分配
	 *
	 * @author  wangjf
	 * @date    2015年5月5日 下午4:51:13
	 * @throws SLException
	 */
	@Test
	public void testAllotLoan() throws SLException {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("productId", "定期宝");
		param.put("createUser", "20150427000000000000000000000000001");
		param.put("allotAmount", "1000");
		param.put("useDate", "2015-8-20");
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("loanId", "2d017765-c0e6-46dd-8e1c-e32cfa91546b");
		map.put("currentValue", "500");
		Map<String, Object> map1 = new HashMap<String, Object>();
		map1.put("loanId", "5e5d0ce5-da50-419e-a10b-4bd33917a12a");
		map1.put("currentValue", "500");
		list.add(map);
		list.add(map1);
		param.put("loanList", list);
		allotService.allotLoan(param);
	}
	
	
}
