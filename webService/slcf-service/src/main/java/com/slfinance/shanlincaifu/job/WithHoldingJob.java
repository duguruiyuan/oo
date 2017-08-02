package com.slfinance.shanlincaifu.job;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.WithHoldingService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 *
 * 代扣定时任务job
 * Created by 张祥 on 2017/7/14.
 */
@Component
public class WithHoldingJob extends AbstractJob{

    @Autowired
    private WithHoldingService withHoldingService;

    @Override
    protected void execute() {
        try {
            Date date = new Date();
            String expectRepaymentDate = DateUtils.formatDate(date,"yyyyMMdd");
            withHoldingService.autoWithHoldingRepayment(expectRepaymentDate);
        } catch (SLException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    protected String getJobName() {
        return Constant.JOB_NAME_WITHHOLD;
    }
}
