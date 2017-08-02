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
 * <b>类名：</b>LoanGrantJLJob.java<br>
 * <p><b>描述：</b>巨涟自动放款定时任务 </p>
 * <p><b>项目：</b>善林财富</p>
 * <p><b>版权：</b>Copyright © 2015 善林金融. All rights reserved. </p>
 * @author <font color='blue'>张祥</font> 
 * @version 2.0.1
 * @date  2017年7月4日 下午2:02:16
 */
@Component
public class LoanGrantJLJob extends AbstractJob{

	@Autowired
	private LoanJobService loanJobService;
	
	@Override
	public void execute() {

		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("companyName", Constant.DEBT_SOURCE_JLJR);
			loanJobService.autoGrant4Company(params);
		} catch (SLException e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	protected String getJobName() {
		return Constant.JOB_NAME_LOAN_GRANT_JL;
	}

}
