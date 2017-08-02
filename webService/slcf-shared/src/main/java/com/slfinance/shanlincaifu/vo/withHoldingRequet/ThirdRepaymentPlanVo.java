package com.slfinance.shanlincaifu.vo.withHoldingRequet;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 *
 * 第三放还款计划实体信息
 *
 */
@Getter
@Setter
public class ThirdRepaymentPlanVo implements Serializable{

    private static final long serialVersionUID = 1836280892680307052L;

    private String date;

    private String amount;

}
