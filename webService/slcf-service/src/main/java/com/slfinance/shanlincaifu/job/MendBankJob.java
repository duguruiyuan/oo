/** 
 * @(#)MendBankJob.java 1.0.0 2015年8月6日 下午2:38:00  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.BankCardService;
import com.slfinance.shanlincaifu.utils.Constant;

/**   
 * 补银行卡信息
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年8月6日 下午2:38:00 $ 
 */
@Component
public class MendBankJob extends AbstractJob {

	@Autowired
	private BankCardService bankCardService;
	
	@Override
	public void execute() {

		try {
			bankCardService.mendBankCard();
		} catch (SLException e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	protected String getJobName() {
		return Constant.JOB_NAME_MENDBANK;
	}

}
