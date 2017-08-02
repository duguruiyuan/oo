package com.slfinance.shanlincaifu.service;

import com.slfinance.shanlincaifu.entity.WithHoldingFlowEntity;

/**
 * Created by 张祥 on 2017/7/14.
 */
public interface WithHoldingFlowService {

    void doUpdateWithHoldingFlow(String batchCode,String requsetNo,String tradeStatus,String responsInfo,String isNeedQuery,String memo);


}
