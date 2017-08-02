package com.slfinance.shanlincaifu.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.slfinance.shanlincaifu.service.RecommendedAwardsService;
import com.slfinance.shanlincaifu.utils.Constant;

/**
 * 推荐奖励信息
 * 
 * @author mzhu
 * @version $Revision:1.0.0, $Date: 2015年5月6日 下午3:37:27 $
 */

@Component
public class RecommendedAwardsJob extends AbstractJob {
	@Autowired
	private RecommendedAwardsService recommendedAwardsService;

	@Override
	protected void execute() {
		recommendedAwardsService.grentRecommendedAwardsData(null);

	}

	@Override
	protected String getJobName() {
		return Constant.JOB_NAME_RECOMMENDEDAWARDS;
	}

}
