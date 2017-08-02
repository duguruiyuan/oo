package com.slfinance.shanlincaifu.vo.withHoldingRequet;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created by SLCF-ZX on 2017/7/14.
 */
@Getter
@Setter
public class BaseRequestVo<T> implements Serializable{

    private static final long serialVersionUID = -7114691893505336779L;

    private String batchCode;

    private String buzName;

    private String platform;

    private String serviceName;

    private String requestTime;

    private String userDevice;

    private T reqData;
}
