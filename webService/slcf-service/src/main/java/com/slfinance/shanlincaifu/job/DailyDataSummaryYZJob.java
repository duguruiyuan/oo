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
import com.slfinance.shanlincaifu.service.BizExtractData;
import com.slfinance.shanlincaifu.service.LoanJobService;
import com.slfinance.shanlincaifu.utils.Constant;


/**
 * 意真放款定时发送数据
 * @author sunht
 * @version $Revision:1.0.0, $Date: 2017年6月16日 上午14:52:23 $ 
 *
 */
@Component
public class DailyDataSummaryYZJob extends AbstractJob{
 

	@Autowired
	BizExtractData bizExtractData;
	
	@Override
	protected void execute() {
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			bizExtractData.dailyDataYZloanAccountSummary(params);
			

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	protected String getJobName() {
		return Constant.JOB_NAME_DAILY_DATA_GRANT_AMOUNT_YZ;
	}


	
}
