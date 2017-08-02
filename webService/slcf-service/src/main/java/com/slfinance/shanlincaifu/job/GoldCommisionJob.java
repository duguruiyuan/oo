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
import com.slfinance.shanlincaifu.service.GoldService;
import com.slfinance.shanlincaifu.service.LoanJobService;
import com.slfinance.shanlincaifu.utils.Constant;

/**   
 * 业绩统计
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年8月26日 上午10:52:23 $ 
 */
@Component
public class GoldCommisionJob extends AbstractJob{
 
	@Autowired
	private LoanJobService loanJobService;
	
	@Autowired
	private GoldService goldService;
	
	@Override
	public void execute() {

		try {
			// 计算佣金
			Map<String, Object> params = new HashMap<String, Object>();
			// update by wangjf 2017-6-1
			// 从2017-4-1开始线上佣金改为线下结算的方式，此处不再计算
			//loanJobService.caclCommission(params);
			
			// 业绩统计
			goldService.goldMonthlySettlement(params);
		} catch (SLException e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	protected String getJobName() {
		return Constant.JOB_NAME_GOLD_COMMISION;
	}
	
}
