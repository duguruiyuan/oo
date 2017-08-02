package com.slfinance.shanlincaifu.service.impl;

import com.slfinance.shanlincaifu.repository.custom.ActivityAwardRepositoryCustom;
import com.slfinance.shanlincaifu.service.ActivityAwardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 活动奖励
 * Created by mali on 2017/7/13.
 */
@Service
public class ActivityAwardServiceImpl implements ActivityAwardService {

    @Autowired
    private ActivityAwardRepositoryCustom activityAwardRepositoryCustom;

    @Override
    public List<Map<String, Object>> findByCustActivityId(String custActivityId) {
        return activityAwardRepositoryCustom.findByCustActivityId(custActivityId);
    }
}
