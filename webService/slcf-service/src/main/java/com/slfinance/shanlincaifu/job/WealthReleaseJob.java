package com.slfinance.shanlincaifu.job;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.WealthJobService;
import com.slfinance.shanlincaifu.utils.Constant;

/**
 * 自动生效优选计划
 * 
 * @author zhiwen_feng
 *
 */
@Component
public class WealthReleaseJob extends AbstractJob {

	@Autowired
	private WealthJobService wealthJobService;
	
	@Override
	public void execute() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			wealthJobService.autoReleaseWealthJob(param);
		} catch (SLException e) {
			logger.error(e.getMessage());
		}		
	}

	@Override
	protected String getJobName() {
		return Constant.JOB_NAME_WEALTH_RELEASE;
	}

}
