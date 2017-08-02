package com.slfinance.shanlincaifu.job;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.LoanJobService;
import com.slfinance.shanlincaifu.utils.Constant;

/**
 * @Desc 自动审核借款项目JOB类
 * @author zhangze
 * @date 2017/06/13
 */
@Component
public class LoanAuditJob extends AbstractJob {
	@Autowired
	LoanJobService LoanJobService;
	
	@Override
	protected void execute() {
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			LoanJobService.autoAuditLoanYZ(params);
		} catch (SLException e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	protected String getJobName() {
		return Constant.JOB_NAME_AUTO_AUDIT_PASS;
	}

}
