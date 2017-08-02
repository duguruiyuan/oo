package com.slfinance.shanlincaifu.service.impl;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.WithHoldingFlowEntity;
import com.slfinance.shanlincaifu.repository.WithHoldingFlowRepository;
import com.slfinance.shanlincaifu.service.WithHoldingFlowService;
import com.slfinance.shanlincaifu.validate.RuleUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by SLCF-ZX on 2017/7/14.
 */
@Service
public class WithHoldingFlowServiceImpl implements WithHoldingFlowService {

    @Autowired
    private WithHoldingFlowRepository withHoldingFlowRepository;

    @Override
    @Transactional(readOnly = false, rollbackFor = SLException.class)
    public void doUpdateWithHoldingFlow(String batchCode, String requsetNo, String tradeStatus, String responsInfo,String isNeedQuery, String memo) {
        WithHoldingFlowEntity withHoldingFlowEntity = withHoldingFlowRepository.findByBatchCodeAndRequestNo(batchCode,requsetNo);
        withHoldingFlowEntity.setTradeStatus(tradeStatus);
        withHoldingFlowEntity.setResponseInfo(responsInfo);
        withHoldingFlowEntity.setIsNeedQuery(isNeedQuery);
        withHoldingFlowEntity.setMemo(memo);
        if (RuleUtils.required(memo)){
            withHoldingFlowEntity.setMemo(memo);
        }

    }
}
