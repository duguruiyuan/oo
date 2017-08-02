/** 
 * @(#)AtoneJob.java 1.0.0 2015年5月4日 下午4:45:51  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.OfflineWealthService;
import com.slfinance.shanlincaifu.utils.Constant;

/**   
 * 赎回详情定时任务
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年5月4日 下午4:45:51 $ 
 */
@Component
public class OffLineTransferToSLFinanceJob extends AbstractJob {

	@Autowired
	private OfflineWealthService offlineWealthService;
	
	@Override
	public void execute() {

		try {
			// 增量同步线下理财的客户信息和投资信息到财富中
			offlineWealthService.synchronizeCustInfoAndInvestInfoFromWealthToSlcf();
			
			offlineWealthService.transferOffLineCustManagerjob();
		} catch (SLException e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	protected String getJobName() {
		return Constant.JOB_NAME_TRANSFER_ONLINE;
	}

}
