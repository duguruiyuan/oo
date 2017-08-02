/** 
 * @(#)RecoverAtoneJob.java 1.0.0 2015年12月21日 下午6:20:11  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.job;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.ProductService;
import com.slfinance.shanlincaifu.utils.Constant;

/**   
 * 活期宝价值回收
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年12月21日 下午6:20:11 $ 
 */
@Component
public class RecoverAtoneJob extends AbstractJob {

	@Autowired
	private ProductService productService;
	
	@Override
	protected void execute() { 
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			productService.recoverUnAtone(params);
		} catch (SLException e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	protected String getJobName() {
		
		return Constant.JOB_NAME_RECOVER_ATONE;
	}

}
