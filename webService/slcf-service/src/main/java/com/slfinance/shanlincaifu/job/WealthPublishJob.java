/** 
 * @(#)RepaymentJob.java 1.0.0 2015年5月4日 下午4:26:43  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.job;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.WealthJobService;
import com.slfinance.shanlincaifu.utils.Constant;

/**   
 * 自动发布优选计划
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年5月4日 下午4:26:43 $ 
 */
@Component
public class WealthPublishJob extends AbstractJob {

	@Autowired
	private WealthJobService wealthJobService;
	
	@Override
	public void execute() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			wealthJobService.autoPublishWealth(param);
		} catch (SLException e) {
			logger.error(e.getMessage());
		}		
	}

	@Override
	protected String getJobName() {
		return Constant.JOB_NAME_WEALTH_PUBLISH;
	}

}
