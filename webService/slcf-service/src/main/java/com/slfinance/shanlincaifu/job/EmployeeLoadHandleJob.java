/** 
 * @(#)GoldDailySettlementJob.java 1.0.0 2015年8月26日 上午10:47:19  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.EmployeeLoadService;
import com.slfinance.shanlincaifu.utils.Constant;

/**   
 * 金牌推荐人每日结息
 *  
 * @author zhiwen_feng
 * @version $Revision:1.0.0, $Date: 2016年4月27日 上午10:47:19 $ 
 */
@Component
public class EmployeeLoadHandleJob extends AbstractJob {

	@Autowired
	private EmployeeLoadService employeeLoadService;
	
	@Override
	public void execute() {

		try {
			employeeLoadService.handleOriginalData();
		} catch (SLException e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	protected String getJobName() {
		return Constant.JOB_NAME_EMPLOYEELOAD_HANDLE;
	}
}
