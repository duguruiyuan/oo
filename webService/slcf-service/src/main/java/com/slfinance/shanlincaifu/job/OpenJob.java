/** 
 * @(#)OpenJob.java 1.0.0 2015年5月6日 下午3:37:27  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.ProductBusinessService;
import com.slfinance.shanlincaifu.utils.Constant;

/**   
 * 定时发标
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年5月6日 下午3:37:27 $ 
 */
@Component
public class OpenJob extends AbstractJob{

	@Autowired
	private ProductBusinessService productBusinessService;
	
	@Override
	public void execute() {
		try {
			productBusinessService.releaseBid();
		} catch (SLException e) {
			logger.error(e.getMessage());
		}
		
	}

	@Override
	protected String getJobName() {
		return Constant.JOB_NAME_RELEASEJOB;
	}

}
