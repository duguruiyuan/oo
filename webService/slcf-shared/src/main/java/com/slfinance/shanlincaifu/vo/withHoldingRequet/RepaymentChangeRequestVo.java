package com.slfinance.shanlincaifu.vo.withHoldingRequet;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 *
 * 还款计划变更请求实体类
 * 张祥
 *
 */
@Getter
@Setter
public class RepaymentChangeRequestVo implements Serializable{

    private String platformUserNo;

    private String requestNo;

    private String repaymentPlan;

    private String repaymentNo;

    private String repaymentState;

    private String smsParam;




}
