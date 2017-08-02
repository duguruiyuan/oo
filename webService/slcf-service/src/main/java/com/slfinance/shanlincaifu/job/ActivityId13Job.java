package com.slfinance.shanlincaifu.job;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.slfinance.shanlincaifu.service.CustActivityInfoService;
import com.slfinance.shanlincaifu.utils.Constant;

@Component
public class ActivityId13Job extends AbstractJob {
	
	@Autowired
	CustActivityInfoService custActivityInfoService;
	
	@Override
	protected void execute() {
		Map<String, Object> activityParams = new HashMap<String, Object>();
		activityParams.put("activityId", Constant.ACTIVITY_ID_REGIST_13);
		custActivityInfoService.custActivityRecommend(activityParams);
	}

	@Override
	protected String getJobName() {
		return "6月市场部活动";
	} 

}
