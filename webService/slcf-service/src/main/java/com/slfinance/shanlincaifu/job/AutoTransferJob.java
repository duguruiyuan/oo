package com.slfinance.shanlincaifu.job;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.AutoInvestJobService;
import com.slfinance.shanlincaifu.utils.Constant;

/**
 * @Desc 自动转让JOB类
 * @author fengyl
 * @date 2017/03/13
 */
@Component
public class AutoTransferJob extends AbstractJob {
	@Autowired
	AutoInvestJobService autoInvestJobService;

	@Override
	protected void execute() {
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			autoInvestJobService.autoTransfer(params);
		} catch (SLException e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	protected String getJobName() {
		return Constant.JOB_NAME_AUTO_TRANSFER;
	}

}
