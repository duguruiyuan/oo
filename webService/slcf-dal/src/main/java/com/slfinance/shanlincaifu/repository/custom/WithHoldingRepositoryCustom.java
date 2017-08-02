package com.slfinance.shanlincaifu.repository.custom;

import java.util.List;
import java.util.Map;

/**
 * 描述：<b>代扣项目定义数据访问层</b>
 *
 * @author:张祥
 * @date 2017/7/13
 */

public interface WithHoldingRepositoryCustom {


    /**
     * 查询需要进行代扣的（已授权）还款的还款计划
     * @param repayMentDate
     * @return
     */
    public List<Map<String,Object>> doFindWatingRepayPlanCope(String repayMentDate);


    /**
     * 查询需要进行代扣的（已授权）还款的还款计划
     * @return
     */
    public List<Map<String,Object>> doFindWatingRepayPlanCopeByLoanInfo(String loanNo,String currentTerm);

}
