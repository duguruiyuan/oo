/** 
 * @(#)ProductService.java 1.0.0 2015年5月1日 上午10:40:12  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service;

import java.util.List;
import java.util.Map;

import com.slfinance.shanlincaifu.entity.RepaymentPlanCopeEntity;
import com.slfinance.vo.ResultVo;



public interface RepaymentPlanCopeService {

	List<RepaymentPlanCopeEntity> queryByLoanCode(String loanNo);


	void updatePlan(String loanCode, List<RepaymentPlanCopeEntity> PlanList);

	/**
	 * 修改还款状态
	 * @param loanNo
	 * @param currentTerm
	 * @param repayMentstatus
	 * param isLimit
	 */
	void doUpdatePlanCopeStatus(String loanNo,String currentTerm,String repayMentstatus,String isLimit,String rieskamoutAccountId,String changeEnable);

	RepaymentPlanCopeEntity doFindPlanByLoanNoAndTerm(String loanNo,String currentTerm);


}
