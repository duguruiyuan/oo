package com.slfinance.shanlincaifu.job;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.SendRepaymentService;
import com.slfinance.shanlincaifu.utils.Constant;

@Component
public class SendRepaymentJob  extends AbstractJob {

	@Autowired
	SendRepaymentService sendRepaymentService;
	
	@Override
	protected void execute() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			sendRepaymentService.sendRepayment(param);
		} catch (SLException e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	protected String getJobName() {
		return Constant.JOB_NAME_SEND_MANUAL_REPAYMENT;
	}

}
