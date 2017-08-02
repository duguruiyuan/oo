/** 
 * @(#)CloseJob.java 1.0.0 2015年5月4日 下午4:41:27  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.job;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.LoanProjectService;
import com.slfinance.shanlincaifu.utils.Constant;

/**   
 * 授权申请（授权失败的）定时任务
 *  
 * @author  liaobingbing
 * @version $Revision:1.0.0, 
 */
@Component
public class AccreditRequestJob extends AbstractJob {

	@Autowired
	private LoanProjectService loanProjectService;
	
	@Override
	public void execute() {
		Map<String, Object> param = new HashMap<String, Object>();
		try {
			loanProjectService.AccreditRequestJob(param);
		} catch (SLException e) {
			logger.error(e.getMessage());
		}
		
		
	}

	@Override
	protected String getJobName() {
		return Constant.JOB_NAME_ACCREDITREQUESTJOB;
	}

}
