/** 
 * @(#)AtoneJob.java 1.0.0 2015年5月4日 下午4:45:51  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.job;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.InvestService;
import com.slfinance.shanlincaifu.utils.Constant;

/**   
 * 赎回详情定时任务
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年5月4日 下午4:45:51 $ 
 */
@Component
public class AtoneJob extends AbstractJob {

	@Autowired
	private InvestService investService;
	
	@Override
	public void execute() {

		try {
			Map<String, Object> param = new HashMap<String, Object>();
			investService.fullAtoneDetail(param);
		} catch (SLException e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	protected String getJobName() {
		return Constant.JOB_NAME_ATONEDETAIL;
	}

}
