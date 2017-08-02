package com.slfinance.shanlincaifu.controller;

import com.slfinance.shanlincaifu.entity.TradeLogInfoEntity;
import com.slfinance.shanlincaifu.entity.WithHoldingFlowEntity;
import com.slfinance.shanlincaifu.repository.TradeLogInfoRepository;
import com.slfinance.shanlincaifu.repository.WithHoldingFlowRepository;
import com.slfinance.shanlincaifu.service.WithHoldingService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.WithHoldingConstant;
import com.slfinance.thirdpp.util.ShareConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 张祥 on 2017/7/14.
 */
@Slf4j
@RestController
@RequestMapping(value = "withHolding")
public class WithHoldingController extends WelcomeController{

    @Autowired
    private WithHoldingService withHoldingService;

    @Autowired
    private WithHoldingFlowRepository withHoldingFlowRepository;

    @Autowired
    private TradeLogInfoRepository tradeLogInfoRepository;

    @RequestMapping(value = "/recivedNotify")
    public String reciveNotify(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String,String> requestParamMap){

        try {
            log.info("代扣异步通知回掉 处理 开始================="+requestParamMap.toString());
            Map<String,Object> paramMap = new HashMap<String,Object>();
            paramMap.put("code",requestParamMap.get("code"));
            paramMap.put("status",requestParamMap.get("status"));
            paramMap.put("batchCode",requestParamMap.get("batchCode"));
            paramMap.put("requestNo",requestParamMap.get("requestNo"));
            paramMap.put("repaymentNo",requestParamMap.get("repaymentNo"));
            paramMap.put("moneyOrder",requestParamMap.get("moneyOrder"));
            paramMap.put("repaymentDate",requestParamMap.get("repaymentDate"));
            paramMap.put("repaymentTerm",requestParamMap.get("repaymentTerm"));
            paramMap.put("isLimit",requestParamMap.get("isLimit"));
            paramMap.put("tradeAmount",requestParamMap.get("moneyOrder"));
            WithHoldingFlowEntity withHoldingFlowEntity = withHoldingFlowRepository.findByBatchCodeAndRequestNo(requestParamMap.get("batchCode"),requestParamMap.get("requestNo"));

            if (withHoldingFlowEntity.getTradeStatus().equals(Constant.TRADE_STATUS_02)){
                if (requestParamMap.get("code").equals(WithHoldingConstant.RESPONSE_SUCCESS_CODE)){
                    withHoldingService.withHoldingSuccess(paramMap);
                }else{
                    if (requestParamMap.get("isLimit").equals(WithHoldingConstant.REPAYMENT_IS_TIME_LIMIT_NO)){
                        withHoldingService.withHoldingFailed(paramMap);
                    }else{
                    	withHoldingService.timeLimitWithHoldingFailed(paramMap);
                    }
                }
            }else{
                log.info("还款计划{},期数{}代扣结果批次号{}已被处理，无需再次处理",requestParamMap.get("repaymentNo"),requestParamMap.get("repaymentTerm"),requestParamMap.get("batchCode"));
            }
            //记录一个tradeLog
            TradeLogInfoEntity tradeLogInfoEntity = new TradeLogInfoEntity();
            tradeLogInfoEntity.setTradeCode(requestParamMap.get("batchCode"));
            tradeLogInfoEntity.setInterfaceType("交易系统代扣结果异步通知");
            tradeLogInfoEntity.setRequestMessage(requestParamMap.toString());
            tradeLogInfoEntity.setResponseMessage(ShareConstant.TRADE_STATUS_SUCCESS);
            tradeLogInfoEntity.setRelatePrimary(withHoldingFlowEntity.getId());
            tradeLogInfoEntity.setRelateTableIdentification("BAO_T_TRADE_LOG_INFO");
            tradeLogInfoEntity.setMemo("交易系统代扣结果异步通知");
            tradeLogInfoRepository.save(tradeLogInfoEntity);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ShareConstant.TRADE_STATUS_SUCCESS;
    }

}
