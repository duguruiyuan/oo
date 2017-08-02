/** 
 * @(#)ProjectPaymentService.java 1.0.0 2016年1月14日 下午5:50:54  
 *  
 * Copyright © 2016 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.slfinance.shanlincaifu.entity.PaymentRecordDetailEntity;
import com.slfinance.vo.ResultVo;

/**   
 * 项目付款服务接口
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2016年1月14日 下午5:50:54 $ 
 */
public interface ProjectPaymentService {

	/**
	 * 保存用户付款记录
	 *
	 * @author  wangjf
	 * @date    2016年1月14日 下午7:05:06
	 * @param projectId
	 * @param custId
	 * @param accountFlowId
	 * @param principalAmount
	 * @param interestAmount
	 * @param penaltyAmount
	 */
	public void saveUserPaymentRecord(String projectId, String custId, String accountFlowId,
			BigDecimal principalAmount, BigDecimal interestAmount, BigDecimal penaltyAmount, 
			BigDecimal awardAmount, String repaymentType, String tradeType);
	
	/**
	 * 保存风险金付款记录
	 *
	 * @author  wangjf
	 * @date    2016年1月14日 下午7:05:09
	 * @param projectId
	 * @param custId
	 * @param accountFlowId
	 * @param principalAmount
	 * @param interestAmount
	 * @param penaltyAmount
	 * @param expenseAmount
	 */
	public void saveRiskPaymentRecord(String projectId, String custId, String accountFlowId,
			BigDecimal principalAmount, BigDecimal interestAmount, BigDecimal penaltyAmount,
			BigDecimal expenseAmount);
	
	/**
	 * 保存公司付款记录
	 *
	 * @author  wangjf
	 * @date    2016年1月14日 下午7:05:12
	 * @param projectId
	 * @param custId
	 * @param accountFlowId
	 * @param penaltyAmount
	 * @param expenseAmount
	 */
	public void saveCompanyPaymentRecord(String projectId, String custId, String accountFlowId,
			BigDecimal penaltyAmount, BigDecimal expenseAmount,
			String repaymentType);
	
	/**
	 * 查询付款明细
	 *
	 * @author  wangjf
	 * @date    2016年1月14日 下午7:59:21
	 * @param params
	 * @return
	 */
	public ResultVo queryPaymentList(Map<String, Object> params);
	
	/**
	 * 保存用户付款记录
	 *
	 * @author  wangjf
	 * @date    2016年1月14日 下午7:05:06
	 * @param loanId
	 * @param custId
	 * @param accountFlowId
	 * @param principalAmount
	 * @param interestAmount
	 * @param penaltyAmount
	 */
	public List<PaymentRecordDetailEntity> saveLoanUserPaymentRecord(String loanId, String custId, String accountFlowId,
			BigDecimal principalAmount, BigDecimal interestAmount, BigDecimal penaltyAmount, 
			BigDecimal awardAmount, String repaymentType, String tradeType);
	
	/**
	 * 保存公司付款记录
	 *
	 * @author  wangjf
	 * @date    2016年12月17日 下午2:22:07
	 * @param loanId
	 * @param custId
	 * @param accountFlowId
	 * @param penaltyAmount
	 * @param expenseAmount
	 * @param repaymentType
	 */
	public void saveLoanCompanyPaymentRecord(String loanId, String custId, String accountFlowId,
			BigDecimal penaltyAmount, BigDecimal expenseAmount,
			String repaymentType);
}
