/** 
 * @(#)OpenServiceNotifyJob.java 1.0.0 2015年7月2日 下午3:21:29  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.OpenNotifyService;
import com.slfinance.shanlincaifu.utils.Constant;

/**   
 * 定时对外通知
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年7月2日 下午3:21:29 $ 
 */
@Component
public class OpenServiceNotifyJob extends AbstractJob{
	
	@Autowired
	private OpenNotifyService openNotifyService;
	
	@Override
	public void execute() {
		try {
			openNotifyService.asynNotify();
		} catch (SLException e) {
			logger.error(e.getMessage());
		}
		
	}

	@Override
	protected String getJobName() {
		return Constant.JOB_NAME_OPENSERVICE_NOTIFY;
	}

}
