/** 
 * @(#)TermAtoneBuy.java 1.0.0 2015年8月18日 下午4:21:15  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.job;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.TermService;
import com.slfinance.shanlincaifu.utils.Constant;

/**   
 * 公司回购
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年8月18日 下午4:21:15 $ 
 */
@Component
public class TermAtoneBuyJob extends AbstractJob{

	@Autowired
	private TermService termService;
	
	@Override
	public void execute() {

		try {
			Map<String, Object> param = new HashMap<String, Object>();
			termService.termAtoneBuy(param);
		} catch (SLException e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	protected String getJobName() {
		return Constant.JOB_NAME_TERM_ATONE_BUY;
	}
}
