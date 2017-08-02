package com.slfinance.shanlincaifu.service;

import java.util.List;
import java.util.Map;

/**
 * 活动奖励
 * Created by mali on 2017/7/13.
 */
public interface ActivityAwardService {

    /**
     * 根据投资信息中的用户活动id查询红包、加息券等的信息
     *
     * @param custActivityId 用户活动id
     * @return
     */
    List<Map<String, Object>> findByCustActivityId(String custActivityId);
}
