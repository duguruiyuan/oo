package com.slfinance.shanlincaifu.job;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.LoanJobService;
import com.slfinance.shanlincaifu.utils.Constant;

/**
 * 拿米自动审核借款项目JOB类
 * create by xuchunchun 2017/7/27
 */
@Component
public class LoanAuditNMJRJob extends AbstractJob {

	@Autowired
	LoanJobService LoanJobService;
	
	@Override
	protected void execute() {
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("start", "0");
			params.put("length", "20000");
			params.put("loanStatus", Constant.LOAN_STATUS_01);
			params.put("companyName", Constant.DEBT_SOURCE_NMJR);
			params.put("isBackStage", "借款信息");
			LoanJobService.autoAuditLoanNM(params);
		} catch (SLException e) {
			logger.error("执行定时任务失败!",e);
		}
		
	}

	@Override
	protected String getJobName() {
		return Constant.JOB_NAME_AUTO_AUDIT_PASS_NM;
	}

}
