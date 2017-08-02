/** 
 * @(#)ComputeOpenValue.java 1.0.0 2015年5月6日 下午3:40:37  
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
 * 可开放价值计算
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年5月6日 下午3:40:37 $ 
 */
@Component
public class ComputeOpenValueJob extends AbstractJob{

	@Autowired
	private ProductBusinessService productBusinessService;
	
	@Override
	public void execute() {
		try {
			productBusinessService.computeOpenValue();
		} catch (SLException e) {
			logger.error(e.getMessage());
		}
		
	}

	@Override
	protected String getJobName() {
		// TODO Auto-generated method stub
		return Constant.JOB_NAME_OPENVALUEJOB;
	}

}
