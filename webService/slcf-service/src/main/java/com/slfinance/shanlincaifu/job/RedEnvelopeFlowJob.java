package com.slfinance.shanlincaifu.job;

import com.slfinance.shanlincaifu.service.CustActivityInfoService;
import com.slfinance.shanlincaifu.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 平红包使用流水记录
 * Created by ganyy on 2017/6/29.
 */
@Component
public class RedEnvelopeFlowJob extends AbstractJob {

  @Autowired
  CustActivityInfoService custActivityInfoService;

  @Override
  protected void execute() {
    try {
      // 记录公司红包账户支出流水
      // 投资账户收入流水在投资时已记录 --
      custActivityInfoService.redEnvelopeFlowJob();
    } catch (Exception e) {
      logger.error(e.getMessage());
    }
  }

  @Override
  protected String getJobName() {
    return Constant.JOB_NAME_RED_ENVELOPE_FLOW;
  }
}
