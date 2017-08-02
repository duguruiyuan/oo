package com.slfinance.shanlincaifu.job;

import com.slfinance.shanlincaifu.service.CustActivityInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author: huifei
 * @date: 2017-07-04 16:37
 */
@Component
public class ExpLoanJob extends AbstractJob{

    @Autowired
    CustActivityInfoService custActivityInfoService;

    @Override
    protected void execute() {
        custActivityInfoService.expLoanTimeJob();
    }

    @Override
    protected String getJobName() {
        return "体验金定时任务";
    }
}
