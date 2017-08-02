package com.slfinance.shanlincaifu.job;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.AutoInvestJobService;
import com.slfinance.shanlincaifu.utils.Constant;

/**
 * @Desc 自动投标JOB类
 * @author guoyk
 * @date 2017/06/16 10:15:30
 */
@Component
public class AutoInvestUpJob extends AbstractJob {

	@Autowired
	AutoInvestJobService autoInvestJobService;
	
	@Override
	protected void execute() {
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			autoInvestJobService.autoInvestUp(params);
		} catch (SLException e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	protected String getJobName() {
		return Constant.JOB_NAME_AUTO_INVEST;
	}

}
