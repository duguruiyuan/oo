/** 
 * @(#)FinancialStatisticsJob.java 1.0.0 2015年8月12日 上午11:26:43  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.slfinance.shanlincaifu.service.impl.FinancialStatisticsService;
import com.slfinance.shanlincaifu.utils.Constant;

/**   
 * 还款统计定时任务
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年8月12日 上午11:26:43 $ 
 */
@Component
public class FinancialStatisticsJob extends AbstractJob {

	@Autowired
	private FinancialStatisticsService financialStatisticsService;
	
	@Override
	public void execute() {
		try {
			financialStatisticsService.sendRepaymentEmail();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}		
	}

	@Override
	protected String getJobName() {
		return Constant.JOB_REPAYMENT_EMAIL;
	}

}
