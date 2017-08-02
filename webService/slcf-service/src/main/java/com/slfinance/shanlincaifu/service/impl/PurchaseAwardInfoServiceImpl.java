package com.slfinance.shanlincaifu.service.impl;

import com.slfinance.shanlincaifu.entity.PurchaseAwardInfoEntity;
import com.slfinance.shanlincaifu.repository.PurchaseAwardInfoRepository;
import com.slfinance.shanlincaifu.service.PurchaseAwardInfoService;
import com.slfinance.shanlincaifu.utils.Constant;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by ganyy on 2017/7/13.
 */
@Service
public class PurchaseAwardInfoServiceImpl implements PurchaseAwardInfoService {

    @Autowired
    private PurchaseAwardInfoRepository purchaseAwardInfoRepository;

    @Override
    public PurchaseAwardInfoEntity findOne() {
        List<PurchaseAwardInfoEntity> purchaseAwardInfoEntityList = purchaseAwardInfoRepository.listByAwardStatus(Constant.PURCHASE_AWARD_STATUS_NO);
        if (purchaseAwardInfoEntityList == null || purchaseAwardInfoEntityList.isEmpty()) {
            return null;
        }
        return purchaseAwardInfoEntityList.get(0);
    }

    @Override
    public void invalidByCurrentTerm(Integer currentTerm) {
        purchaseAwardInfoRepository.updateByCurrentTerm(Constant.USER_ACTIVITY_TRADE_STATUS_07, currentTerm);
    }

    @Override
    public void saveAwardList(List<PurchaseAwardInfoEntity> list) {
        purchaseAwardInfoRepository.save(list);
    }
}
