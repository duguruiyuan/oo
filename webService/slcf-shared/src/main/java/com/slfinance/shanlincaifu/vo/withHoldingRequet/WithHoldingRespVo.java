package com.slfinance.shanlincaifu.vo.withHoldingRequet;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by SLCF-ZX on 2017/7/14.
 */
@Getter
@Setter
public class WithHoldingRespVo {

    private String batchCode;

    private String repaymentNo;

    private String repaymentTerm;

    private String  tradeAmount;

    private String isLimit;//是否逾期

    private String code;

    private String status;

    private String errorCode;

    private String errorMessage;

}
