package com.slfinance.shanlincaifu.service;

import com.slfinance.shanlincaifu.entity.PurchaseAwardInfoEntity;

import java.util.List;

/**
 * 加息券奖励计划service
 * Created by ganyy on 2017/7/13.
 */
public interface PurchaseAwardInfoService {

    /**
     * 查找最近一期未结算的奖励计划.
     */
    PurchaseAwardInfoEntity findOne();

    /**
     * 根据当前期数失效奖励计划
     * 在债权转让的时候，当期的计划奖励金额按比例计算
     * ，其它的奖励计划全部失效
     *
     * @param currentTerm
     */
    void invalidByCurrentTerm(Integer currentTerm);

    /**
     * 批量保存加息券计划
     *
     * @param list
     */
    void saveAwardList(List<PurchaseAwardInfoEntity> list);
}
