package com.slfinance.shanlincaifu.job;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.slfinance.shanlincaifu.service.BizExtractData;
import com.slfinance.shanlincaifu.utils.Constant;

@Component
public class DailyDataSummaryJob extends AbstractJob {

	@Autowired
	BizExtractData bizExtractData;
	
	@Override
	protected void execute() {
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			bizExtractData.dailyDataSummary(params);
			
//			bizExtractData.dailyDataPropellingSummary(params);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	protected String getJobName() {
		return Constant.JOB_NAME_DAILY_DATA_SUMMARY;
	}
}
