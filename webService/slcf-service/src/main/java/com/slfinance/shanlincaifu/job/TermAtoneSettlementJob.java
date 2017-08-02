/** 
 * @(#)TermAtoneSettlement.java 1.0.0 2015年8月18日 下午4:21:37  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.job;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.SMSService;
import com.slfinance.shanlincaifu.service.TermService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.vo.ResultVo;

/**   
 * 定期宝赎回到帐
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年8月18日 下午4:21:37 $ 
 */
@Component
public class TermAtoneSettlementJob extends AbstractJob{

	@Autowired
	private TermService termService;
	
	@Autowired
	private SMSService smsService;
	
	@SuppressWarnings({ "unchecked" })
	@Override
	public void execute() {

		try {
			Map<String, Object> param = new HashMap<String, Object>();
			ResultVo result = termService.termAtoneSettlement(param);
			if(ResultVo.isSuccess(result)) {
				List<Map<String, Object>> smsList = (List<Map<String, Object>>)result.getValue("data");
				for(Map<String, Object> sms : smsList) {
					smsService.asnySendSMSAndSystemMessage(sms);
				}
			}
		} catch (SLException e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	protected String getJobName() {
		return Constant.JOB_NAME_TERM_ATONE_SETTLEMENT;
	}
}
