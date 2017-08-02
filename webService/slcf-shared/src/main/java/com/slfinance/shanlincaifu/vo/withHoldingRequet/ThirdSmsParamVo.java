package com.slfinance.shanlincaifu.vo.withHoldingRequet;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 *
 * 第三方短信参数信息
 *
 */
@Getter
@Setter
public class ThirdSmsParamVo implements Serializable{

    private static final long serialVersionUID = -5349547815877954581L;

    private String contract_type;

    private String contact_way;

}
