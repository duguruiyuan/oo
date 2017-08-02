package com.slfinance.shanlincaifu.vo.withHoldingRequet;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created by 张祥 on 2017/7/14.
 */
@Getter
@Setter
public class ThidPayBaseRequestVo<T> implements Serializable{

    private static final long serialVersionUID = 785148532612549966L;

    private String platform;

    private String serviceName;

    private T respData;

}
