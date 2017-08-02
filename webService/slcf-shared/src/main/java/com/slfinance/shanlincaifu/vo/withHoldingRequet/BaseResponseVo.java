package com.slfinance.shanlincaifu.vo.withHoldingRequet;

import com.slfinance.shanlincaifu.utils.Json;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 张祥 on 2017/7/13.
 */
@Getter
@Setter
@ToString
public class BaseResponseVo<T> implements Serializable {

    private static final long serialVersionUID = 7776642335074715115L;

    private String buzName;

    private String platform;

    private String serviceName;

    private T respData;


//    public static void main(String[] args) {
//        BaseResponseVo<WithHoldingRespVo> vo = new BaseResponseVo<WithHoldingRespVo>();
//        vo.setBuzName("buz");
//        vo.setPlatForm("plat");
//        vo.setServiceName("serviceName");
//        WithHoldingRespVo respVo = new WithHoldingRespVo();
//        respVo.setBatchCodel("SLCF-");
//        respVo.setCode("12321312");
//        respVo.setStatus("success");
//        respVo.setRepaymentNo("nosadasdsa");
//        vo.setRespData(respVo);
//        String res =  Json.ObjectMapper.writeValue(vo);
//        System.out.println(res);
//    }


}
