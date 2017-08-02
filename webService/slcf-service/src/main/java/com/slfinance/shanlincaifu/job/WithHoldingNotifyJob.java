package com.slfinance.shanlincaifu.job;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.WithHoldingNotifyService;
import com.slfinance.shanlincaifu.utils.Constant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * 代扣通知定时任务
 * Created by lixx on 2017/7/17
 */
@Component
public class WithHoldingNotifyJob extends AbstractJob{

    @Autowired
    private WithHoldingNotifyService withHoldingNotifyService;

    @Override
	public void execute() {
		try {
			withHoldingNotifyService.asynNotify();
		} catch (SLException e) {
			logger.error(e.getMessage());
		}
		
	}

    @Override
    protected String getJobName() {
        return Constant.JOB_NAME_WITHHOLD_NOTIFY;
    }
}
