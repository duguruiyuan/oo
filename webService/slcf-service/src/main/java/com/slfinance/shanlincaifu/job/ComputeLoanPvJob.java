/** 
 * @(#)ComputeLoanPvJob.java 1.0.0 2015年5月6日 下午3:42:24  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.job;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.AllotService;
import com.slfinance.shanlincaifu.service.LoanInfoService;
import com.slfinance.shanlincaifu.utils.Constant;

/**   
 * 债权价值计算
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年5月6日 下午3:42:24 $ 
 */
@Component
public class ComputeLoanPvJob extends AbstractJob{
	
	@Autowired
	private LoanInfoService loanInfoService;
	
	@Autowired
	private AllotService allotService;
	
	@Override
	public synchronized void execute() {
		try {
			loanInfoService.execLoanPv();
			
			// 将债权分配给居间人
			Map<String, Object> params = Maps.newHashMap();
			allotService.autoAllotWealth(params);	
		} catch (SLException e) {
			logger.error(e.getMessage());
		}
		
	}
	
	@Override
	protected String getJobName() {
		return Constant.JOB_NAME_LOANVAULECALCULATEJOB;
	}
	
}
