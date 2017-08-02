package com.slfinance.shanlincaifu.job;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.AutoQueryWithHoldResultService;
import com.slfinance.shanlincaifu.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * 自动查询代扣结果定时任务
 *
 */
@Component
public class AutoQueryWithHoldResultJob extends  AbstractJob{

    @Autowired
    public AutoQueryWithHoldResultService autoQueryWithHoldResultService;

    @Override
    protected void execute() {
        try {
            autoQueryWithHoldResultService.queryTppWithholdingResult();
        } catch (SLException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    protected String getJobName() {
        return Constant.JOB_NAME_WITHHOLD_QUERY;
    }
}
