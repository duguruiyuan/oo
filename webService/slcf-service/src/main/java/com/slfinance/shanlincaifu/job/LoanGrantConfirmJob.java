/** 
 * @(#)GoldWithdrawJob.java 1.0.0 2015年8月26日 上午10:52:23  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.job;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.LoanJobService;
import com.slfinance.shanlincaifu.utils.Constant;

/**   
 * 散标自动放款确认
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年8月26日 上午10:52:23 $ 
 */
@Component
public class LoanGrantConfirmJob extends AbstractJob{
 
	@Autowired
	private LoanJobService loanJobService;
	
	@Override
	public void execute() {

		try {
			Map<String, Object> params = new HashMap<String, Object>();
			loanJobService.autoGrantConfirm(params);
		} catch (SLException e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	protected String getJobName() {
		return Constant.JOB_NAME_LOAN_GRANT_CONFIRM;
	}
	
}
