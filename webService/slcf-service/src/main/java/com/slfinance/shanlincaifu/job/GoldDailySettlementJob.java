/** 
 * @(#)GoldDailySettlementJob.java 1.0.0 2015年8月26日 上午10:47:19  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.job;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.GoldService;
import com.slfinance.shanlincaifu.service.impl.RefereeAuditService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;

/**   
 * 金牌推荐人每日结息
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年8月26日 上午10:47:19 $ 
 */
@Component
public class GoldDailySettlementJob extends AbstractJob {

	@Autowired
	private GoldService goldService;
	
	@Autowired
	private RefereeAuditService refereeAuditService;
	
	@Override
	public void execute() {

		try {
			Map<String, Object> params = new HashMap<String, Object>();
			
//			// 活期宝每日结息
//			goldService.goldDailySettlement(params);
			
			// 对已离职的业务员进行标记
			refereeAuditService.leaveJob(params);
			
			// 企业借款和优选计划每月1号生成业务报表
			if(DateUtils.formatDate(new Date(), "dd").equals("01")) { 
				//goldService.goldMonthlySettlement(params);
			}
		} catch (SLException e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	protected String getJobName() {
		return Constant.JOB_NAME_GOLD_DALIY_SETTLEMENT;
	}
}
