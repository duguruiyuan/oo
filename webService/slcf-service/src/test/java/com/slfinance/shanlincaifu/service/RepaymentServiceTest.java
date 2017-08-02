/** 
 * @(#)RepaymentServiceTest.java 1.0.0 2015年5月1日 下午6:40:30  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.vo.ResultVo;

/**   
 * 还款测试
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年5月1日 下午6:40:30 $ 
 */
public class RepaymentServiceTest extends AbstractSpringContextTestSupport{

	@Autowired
	private RepaymentService repaymentService;
	
	@Test
	public void testRepaymentJob() throws SLException {
		Map<String ,Object> param = new HashMap<String, Object>();
		param.put("expectRepaymentDate", DateUtils.formatDate(new Date(), "yyyyMMdd"));
		ResultVo result = repaymentService.repaymentJob(param);
		assertEquals(ResultVo.isSuccess(result), true);
	}
}
