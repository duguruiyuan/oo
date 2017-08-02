package com.slfinance.shanlincaifu.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.SummarizationBusinessHistoryService;
import com.slfinance.shanlincaifu.utils.Constant;

/**
 * 业务数据汇总定时任务
 * @author zhangt
 *
 */
@Component
public class SummarizationBusinessHistoryJob extends AbstractJob {

	@Autowired
	private SummarizationBusinessHistoryService summarizationBusinessHistoryService;
	
	@Override
	protected void execute() {
		
		try {
			summarizationBusinessHistoryService.sumBusinessHistory();
		} catch (SLException e) {
			logger.error(e.getMessage());
		}

	}

	@Override
	protected String getJobName() {
		return Constant.JOB_NAME_SUMMARIZATION;
	}

}
