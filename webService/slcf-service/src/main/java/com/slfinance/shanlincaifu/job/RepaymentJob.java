/** 
 * @(#)RepaymentJob.java 1.0.0 2015年5月4日 下午4:26:43  
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
import com.slfinance.shanlincaifu.service.RepaymentService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;

/**   
 * 还款定时任务
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年5月4日 下午4:26:43 $ 
 */
@Component
public class RepaymentJob extends AbstractJob {

	@Autowired
	private RepaymentService repaymentService;
	
	@Override
	public void execute() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("expectRepaymentDate", DateUtils.formatDate(new Date(), "yyyyMMdd"));
			repaymentService.repaymentJob(param);
		} catch (SLException e) {
			logger.error(e.getMessage());
		}		
	}

	@Override
	protected String getJobName() {
		return Constant.JOB_NAME_REPAYMENTJOB;
	}

}
