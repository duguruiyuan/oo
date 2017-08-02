package com.slfinance.shanlincaifu.vo.withHoldingRequet;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by SLCF-ZX on 2017/7/14.
 */
@Setter
@Getter
public class WithHoldingReqVo {

    private String tradeAmount;//交易金额

    private String noAgree;//协议号

    private String platformUserNo;//用户标识

    private String repaymentNo;//还款计划编号

    private String repaymentTerm;//还款期数

    private String isLimit;//是否逾期

    private String repaymentDate;//还款时间

    private String notifyUrl;//通知地址

    private String requestNo;//请求号



}
