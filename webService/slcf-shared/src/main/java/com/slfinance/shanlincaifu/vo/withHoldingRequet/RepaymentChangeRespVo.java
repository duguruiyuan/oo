package com.slfinance.shanlincaifu.vo.withHoldingRequet;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created by SLCF-ZX on 2017/7/15.
 */
@Getter
@Setter
public class RepaymentChangeRespVo implements Serializable{

    private static final long serialVersionUID = -5044647099812120021L;

    private String code;

    private String status;

    private String errorCode;

    private String errorMeaage;
}
