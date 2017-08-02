package com.slfinance.shanlincaifu.job;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.LoanJobService;
import com.slfinance.shanlincaifu.utils.Constant;

/***
 * 
 * <b>类名：</b>LoanAuditJLJob.java<br>
 * <p><b>描述：</b>巨涟标的自动审核与发布 </p>
 * <p><b>项目：</b>善林财富</p>
 * <p><b>版权：</b>Copyright © 2017 善林金融. All rights reserved. </p>
 * @author <font color='blue'>张祥</font> 
 * @version 2.0.1
 * @date  2017年7月4日 下午1:41:49
 */
@Component
public class LoanAuditJLJRJob extends AbstractJob{

	@Autowired
	LoanJobService LoanJobService;
	
	@Override
	protected void execute() {
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("start", "0");
			params.put("length", "20000");
			params.put("loanStatus", Constant.LOAN_STATUS_01);
			params.put("companyName", Constant.DEBT_SOURCE_JLJR);
			params.put("isBackStage", "借款信息");
			LoanJobService.autoAuditLoan4Company(params);
		} catch (SLException e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	protected String getJobName() {
		return Constant.JOB_NAME_AUTO_AUDIT_PASS_JL;
	}
}
