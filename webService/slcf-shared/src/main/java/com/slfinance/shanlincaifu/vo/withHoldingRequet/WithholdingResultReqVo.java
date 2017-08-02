package com.slfinance.shanlincaifu.vo.withHoldingRequet;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p><b>标题：</b>webService</p>
 * <p><b>描述：</b>TODO </p>
 * <p><b>公司：</b>善林财富 </p>
 * <p><b>版权声明：</b>Copyright (c) 2017</p>
 *
 * @author 张祥
 * @date 2017/7/17 16:36
 */
@Getter
@Setter
public class WithholdingResultReqVo implements Serializable{

    private String bizBatchCode;

    private String requestNo;

}
