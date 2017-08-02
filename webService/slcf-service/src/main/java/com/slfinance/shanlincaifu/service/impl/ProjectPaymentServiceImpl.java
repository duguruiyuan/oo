/** 
 * @(#)ProjectPaymentServiceImpl.java 1.0.0 2016年1月14日 下午5:51:54  
 *  
 * Copyright © 2016 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.PaymentRecordDetailEntity;
import com.slfinance.shanlincaifu.entity.PaymentRecordInfoEntity;
import com.slfinance.shanlincaifu.repository.PaymentRecordDetailRepository;
import com.slfinance.shanlincaifu.repository.PaymentRecordInfoRepository;
import com.slfinance.shanlincaifu.repository.custom.ProjectPaymentRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.ProjectRepaymentRepositoryCustom;
import com.slfinance.shanlincaifu.service.ProjectPaymentService;
import com.slfinance.shanlincaifu.utils.ArithUtil;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.SubjectConstant;
import com.slfinance.vo.ResultVo;

/**   
 * 项目付款服务实现类
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2016年1月14日 下午5:51:54 $ 
 */
@Service("projectPaymentService")
public class ProjectPaymentServiceImpl implements ProjectPaymentService {

	@Autowired
	private PaymentRecordInfoRepository paymentRecordInfoRepository;
	
	@Autowired
	private PaymentRecordDetailRepository paymentRecordDetailRepository;
	
	@Autowired
	private ProjectPaymentRepositoryCustom projectPaymentRepositoryCustom;
	
	@Autowired
	private ProjectRepaymentRepositoryCustom projectRepaymentRepositoryCustom;
		
	@Override
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public void saveUserPaymentRecord(String projectId, String custId, String accountFlowId,
			BigDecimal principalAmount, BigDecimal interestAmount, BigDecimal penaltyAmount,
			BigDecimal awardAmount, String repaymentType, String tradeType) {
		
		PaymentRecordInfoEntity paymentRecordInfoEntity = new PaymentRecordInfoEntity();
		paymentRecordInfoEntity.setRelateType(Constant.TABLE_BAO_T_ACCOUNT_FLOW_INFO);
		paymentRecordInfoEntity.setRelatePrimary(accountFlowId);
		paymentRecordInfoEntity.setProjectId(projectId);
		paymentRecordInfoEntity.setCustId(custId);
		paymentRecordInfoEntity.setTradeType(tradeType);
		paymentRecordInfoEntity.setRepayAmount(ArithUtil.add(ArithUtil.add(ArithUtil.add(principalAmount, interestAmount), penaltyAmount), awardAmount));
		paymentRecordInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
		paymentRecordInfoEntity = paymentRecordInfoRepository.save(paymentRecordInfoEntity);
		
		List<PaymentRecordDetailEntity> payRecordDetailList = Lists.newArrayList();
		
		switch(repaymentType) {
		case Constant.REPAYMENT_TYPE_01: // 正常还款
			// 本金
			payRecordDetailList.add(createPaymentRecordDetail(paymentRecordInfoEntity.getId(), "", 
					SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_PRINCIPAL_PROJECT, principalAmount));
			
			// 利息
			payRecordDetailList.add(createPaymentRecordDetail(paymentRecordInfoEntity.getId(), "", 
					SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_INTEREST_PROJECT, interestAmount));
			break;
		case Constant.REPAYMENT_TYPE_02: // 逾期还款
			// 本金
			payRecordDetailList.add(createPaymentRecordDetail(paymentRecordInfoEntity.getId(), "", 
					SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_PRINCIPAL_PROJECT, principalAmount));
			
			// 利息
			payRecordDetailList.add(createPaymentRecordDetail(paymentRecordInfoEntity.getId(), "", 
					SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_INTEREST_PROJECT, interestAmount));
			
			// 罚息
			if(penaltyAmount.compareTo(BigDecimal.ZERO) > 0) {
				payRecordDetailList.add(createPaymentRecordDetail(paymentRecordInfoEntity.getId(), "", 
						SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_PENALTY_PROJECT, penaltyAmount));
			}
			break;
		case Constant.REPAYMENT_TYPE_03: // 提前还款
			// 本金
			payRecordDetailList.add(createPaymentRecordDetail(paymentRecordInfoEntity.getId(), "", 
					SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_PRINCIPAL_PROJECT, principalAmount));
			
			// 利息
			if(interestAmount.compareTo(BigDecimal.ZERO) > 0) {
				payRecordDetailList.add(createPaymentRecordDetail(paymentRecordInfoEntity.getId(), "", 
						SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_INTEREST_PROJECT, interestAmount));
			}
	
			if(penaltyAmount.compareTo(BigDecimal.ZERO) > 0) {
				// 违约金
				payRecordDetailList.add(createPaymentRecordDetail(paymentRecordInfoEntity.getId(), "", 
						SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_BREACH_PROJECT, penaltyAmount));
			}

			break;
		case Constant.REPAYMENT_TYPE_04: // 风险金垫付
			// 本金
			payRecordDetailList.add(createPaymentRecordDetail(paymentRecordInfoEntity.getId(), "", 
					SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_RISK_OVERDUE_PRINCIPAL_PROJECT, principalAmount));
			
			// 利息
			payRecordDetailList.add(createPaymentRecordDetail(paymentRecordInfoEntity.getId(), "", 
					SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_RISK_OVERDUE_INTEREST_PROJECT, interestAmount));
			
			// 罚息
			if(penaltyAmount.compareTo(BigDecimal.ZERO) > 0) {
				payRecordDetailList.add(createPaymentRecordDetail(paymentRecordInfoEntity.getId(), "", 
						SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_RISK_OVERDUE_PENALTY_PROJECT, penaltyAmount));
			}
			break;
		}
		
		// 奖励收益
		if(awardAmount.compareTo(BigDecimal.ZERO) > 0) {
			payRecordDetailList.add(createPaymentRecordDetail(paymentRecordInfoEntity.getId(), "", 
					SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_AWARD_PROJECT, awardAmount));
		}
		
		paymentRecordDetailRepository.save(payRecordDetailList);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public void saveRiskPaymentRecord(String projectId, String custId, String accountFlowId,
			BigDecimal principalAmount, BigDecimal interestAmount, BigDecimal penaltyAmount,
			BigDecimal expenseAmount) {
		
		PaymentRecordInfoEntity paymentRecordInfoEntity = new PaymentRecordInfoEntity();
		paymentRecordInfoEntity.setRelateType(Constant.TABLE_BAO_T_ACCOUNT_FLOW_INFO);
		paymentRecordInfoEntity.setRelatePrimary(accountFlowId);
		paymentRecordInfoEntity.setProjectId(projectId);
		paymentRecordInfoEntity.setCustId(custId);
		paymentRecordInfoEntity.setTradeType(SubjectConstant.TRADE_FLOW_TYPE_RISK_REPAY_PROJECT);
		paymentRecordInfoEntity.setRepayAmount(ArithUtil.add(ArithUtil.add(ArithUtil.add(principalAmount, interestAmount), penaltyAmount), expenseAmount));
		paymentRecordInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
		paymentRecordInfoEntity = paymentRecordInfoRepository.save(paymentRecordInfoEntity);
		
		List<PaymentRecordDetailEntity> payRecordDetailList = Lists.newArrayList();
		
		// 本金
		payRecordDetailList.add(createPaymentRecordDetail(paymentRecordInfoEntity.getId(), "", 
				SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_RISK_REPAYMENT_PRINCIPAL_PROJECT, principalAmount));
		
		// 利息
		payRecordDetailList.add(createPaymentRecordDetail(paymentRecordInfoEntity.getId(), "", 
				SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_RISK_REPAYMENT_INTEREST_PROJECT, interestAmount));
		
		// 账户管理费
		payRecordDetailList.add(createPaymentRecordDetail(paymentRecordInfoEntity.getId(), "", 
				SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_RISK_REPAYMENT_EXPENSE_PROJECT, expenseAmount));
	
		// 罚息
		if(penaltyAmount.compareTo(BigDecimal.ZERO) > 0) {
			
			payRecordDetailList.add(createPaymentRecordDetail(paymentRecordInfoEntity.getId(), "", 
					SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_RISK_REPAYMENT_PENALTY_PROJECT, penaltyAmount));
		}
		
		paymentRecordDetailRepository.save(payRecordDetailList);		
	}
	
	@Override
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public void saveCompanyPaymentRecord(String projectId, String custId, String accountFlowId,
			BigDecimal penaltyAmount, BigDecimal expenseAmount,
			String repaymentType) {
		PaymentRecordInfoEntity paymentRecordInfoEntity = new PaymentRecordInfoEntity();
		paymentRecordInfoEntity.setRelateType(Constant.TABLE_BAO_T_ACCOUNT_FLOW_INFO);
		paymentRecordInfoEntity.setRelatePrimary(accountFlowId);
		paymentRecordInfoEntity.setProjectId(projectId);
		paymentRecordInfoEntity.setCustId(custId);
		paymentRecordInfoEntity.setTradeType(SubjectConstant.TRADE_FLOW_TYPE_COMPANY_REPAYMENT_ALL_PROJECT);
		paymentRecordInfoEntity.setRepayAmount(ArithUtil.add(penaltyAmount, expenseAmount));
		paymentRecordInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
		paymentRecordInfoEntity = paymentRecordInfoRepository.save(paymentRecordInfoEntity);
		
		List<PaymentRecordDetailEntity> payRecordDetailList = Lists.newArrayList();
		
		switch(repaymentType) {
		case Constant.REPAYMENT_TYPE_01: // 正常还款
			// 账户管理费
			payRecordDetailList.add(createPaymentRecordDetail(paymentRecordInfoEntity.getId(), "", 
					SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMPANY_REPAYMENT_EXPENSE_PROJECT, expenseAmount));
			break;
		case Constant.REPAYMENT_TYPE_02: // 逾期还款
			// 账户管理费
			if(expenseAmount.compareTo(BigDecimal.ZERO) > 0) {
				payRecordDetailList.add(createPaymentRecordDetail(paymentRecordInfoEntity.getId(), "", 
						SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMPANY_REPAYMENT_EXPENSE_PROJECT, expenseAmount));
			}

			// 罚息
			if(penaltyAmount.compareTo(BigDecimal.ZERO) > 0) {
				
				payRecordDetailList.add(createPaymentRecordDetail(paymentRecordInfoEntity.getId(), "", 
						SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMPANY_REPAYMENT_PENALTY_PROJECT, penaltyAmount));
			}
			break;
		case Constant.REPAYMENT_TYPE_03: // 提前还款
			// 账户管理费
			payRecordDetailList.add(createPaymentRecordDetail(paymentRecordInfoEntity.getId(), "", 
					SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMPANY_REPAYMENT_EXPENSE_PROJECT, expenseAmount));
			break;
		case Constant.REPAYMENT_TYPE_04: // 风险金垫付
			// 账户管理费
			payRecordDetailList.add(createPaymentRecordDetail(paymentRecordInfoEntity.getId(), "", 
					SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_RISK_OVERDUE_EXPENSE_PROJECT, expenseAmount));
			break;
		}
		
		paymentRecordDetailRepository.save(payRecordDetailList);
	}
	
	/**
	 * 创建付款明细
	 *
	 * @author  wangjf
	 * @date    2016年1月14日 下午7:03:45
	 * @param payRecordId
	 * @param repayPlanId
	 * @param subjectType
	 * @param tradeAmount
	 * @return
	 */
	public PaymentRecordDetailEntity createPaymentRecordDetail(String payRecordId, String repayPlanId, String subjectType, 
			BigDecimal tradeAmount) {
		
		PaymentRecordDetailEntity paymentRecordDetailEntity = new PaymentRecordDetailEntity();
		paymentRecordDetailEntity.setPayRecordId(payRecordId);
		paymentRecordDetailEntity.setRepayPlanId(repayPlanId);
		paymentRecordDetailEntity.setSubjectDirection(SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING);
		paymentRecordDetailEntity.setSubjectType(subjectType);
		paymentRecordDetailEntity.setTradeAmount(tradeAmount);
		paymentRecordDetailEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
		
		return paymentRecordDetailEntity;
	}

	@Override
	public ResultVo queryPaymentList(Map<String, Object> params) {
		
		Map<String, Object> result = Maps.newHashMap();
		String tradeType = (String)params.get("tradeType");
		if(SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_RISK_REPAYMENT_PROJECT.equals(tradeType)) {
			params.put("custId", "");
			result.put("paymentList", projectRepaymentRepositoryCustom.queryRepaymentList(params));
		}
		else {
			result.put("paymentList", projectPaymentRepositoryCustom.queryPaymentList(params));
		}
		
		return new ResultVo(true, "查询付款列表成功", result);
	}

	@Override
	public List<PaymentRecordDetailEntity> saveLoanUserPaymentRecord(String loanId, String custId,
			String accountFlowId, BigDecimal principalAmount,
			BigDecimal interestAmount, BigDecimal penaltyAmount,
			BigDecimal awardAmount, String repaymentType, String tradeType) {
		PaymentRecordInfoEntity paymentRecordInfoEntity = new PaymentRecordInfoEntity();
		paymentRecordInfoEntity.setRelateType(Constant.TABLE_BAO_T_ACCOUNT_FLOW_INFO);
		paymentRecordInfoEntity.setRelatePrimary(accountFlowId);
		paymentRecordInfoEntity.setLoanId(loanId);
		paymentRecordInfoEntity.setCustId(custId);
		paymentRecordInfoEntity.setTradeType(tradeType);
		paymentRecordInfoEntity.setRepayAmount(ArithUtil.add(ArithUtil.add(ArithUtil.add(principalAmount, interestAmount), penaltyAmount), awardAmount));
		paymentRecordInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
		paymentRecordInfoEntity = paymentRecordInfoRepository.save(paymentRecordInfoEntity);
		
		List<PaymentRecordDetailEntity> payRecordDetailList = Lists.newArrayList();
		
		switch(repaymentType) {
		case Constant.REPAYMENT_TYPE_01: // 正常还款
			// 本金
			payRecordDetailList.add(createPaymentRecordDetail(paymentRecordInfoEntity.getId(), "", 
					SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_PRINCIPAL_PROJECT, principalAmount));
			
			// 利息
			payRecordDetailList.add(createPaymentRecordDetail(paymentRecordInfoEntity.getId(), "", 
					SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_INTEREST_PROJECT, interestAmount));
			break;
		case Constant.REPAYMENT_TYPE_02: // 逾期还款
			// 本金
			payRecordDetailList.add(createPaymentRecordDetail(paymentRecordInfoEntity.getId(), "", 
					SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_PRINCIPAL_PROJECT, principalAmount));
			
			// 利息
			payRecordDetailList.add(createPaymentRecordDetail(paymentRecordInfoEntity.getId(), "", 
					SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_INTEREST_PROJECT, interestAmount));
			
			// 罚息
			if(penaltyAmount.compareTo(BigDecimal.ZERO) > 0) {
				payRecordDetailList.add(createPaymentRecordDetail(paymentRecordInfoEntity.getId(), "", 
						SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_PENALTY_PROJECT, penaltyAmount));
			}
			break;
		case Constant.REPAYMENT_TYPE_03: // 提前还款
			// 本金
			payRecordDetailList.add(createPaymentRecordDetail(paymentRecordInfoEntity.getId(), "", 
					SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_PRINCIPAL_PROJECT, principalAmount));
			
			// 利息
			if(interestAmount.compareTo(BigDecimal.ZERO) > 0) {
				payRecordDetailList.add(createPaymentRecordDetail(paymentRecordInfoEntity.getId(), "", 
						SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_INTEREST_PROJECT, interestAmount));
			}
	
			if(penaltyAmount.compareTo(BigDecimal.ZERO) > 0) {
				// 违约金
				payRecordDetailList.add(createPaymentRecordDetail(paymentRecordInfoEntity.getId(), "", 
						SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_BREACH_PROJECT, penaltyAmount));
			}

			break;
		case Constant.REPAYMENT_TYPE_04: // 风险金垫付
			// 本金
			payRecordDetailList.add(createPaymentRecordDetail(paymentRecordInfoEntity.getId(), "", 
					SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_RISK_OVERDUE_PRINCIPAL_PROJECT, principalAmount));
			
			// 利息
			payRecordDetailList.add(createPaymentRecordDetail(paymentRecordInfoEntity.getId(), "", 
					SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_RISK_OVERDUE_INTEREST_PROJECT, interestAmount));
			
			// 罚息
			if(penaltyAmount.compareTo(BigDecimal.ZERO) > 0) {
				payRecordDetailList.add(createPaymentRecordDetail(paymentRecordInfoEntity.getId(), "", 
						SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_RISK_OVERDUE_PENALTY_PROJECT, penaltyAmount));
			}
			break;
		}
		
		// 奖励收益
		if(awardAmount.compareTo(BigDecimal.ZERO) > 0) {
			payRecordDetailList.add(createPaymentRecordDetail(paymentRecordInfoEntity.getId(), "", 
					SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_DISPERSE_AWARD_PROJECT, awardAmount));
		}
		
		paymentRecordDetailRepository.save(payRecordDetailList);
		return payRecordDetailList;
	}

	@Override
	public void saveLoanCompanyPaymentRecord(String loanId, String custId,
			String accountFlowId, BigDecimal penaltyAmount,
			BigDecimal expenseAmount, String repaymentType) {
		PaymentRecordInfoEntity paymentRecordInfoEntity = new PaymentRecordInfoEntity();
		paymentRecordInfoEntity.setRelateType(Constant.TABLE_BAO_T_ACCOUNT_FLOW_INFO);
		paymentRecordInfoEntity.setRelatePrimary(accountFlowId);
		paymentRecordInfoEntity.setLoanId(loanId);
		paymentRecordInfoEntity.setCustId(custId);
		paymentRecordInfoEntity.setTradeType(SubjectConstant.TRADE_FLOW_TYPE_COMPANY_REPAYMENT_ALL_LOAN);
		paymentRecordInfoEntity.setRepayAmount(ArithUtil.add(penaltyAmount, expenseAmount));
		paymentRecordInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
		paymentRecordInfoEntity = paymentRecordInfoRepository.save(paymentRecordInfoEntity);
		
		List<PaymentRecordDetailEntity> payRecordDetailList = Lists.newArrayList();
		
		switch(repaymentType) {
		case Constant.REPAYMENT_TYPE_01: // 正常还款
			// 账户管理费
			payRecordDetailList.add(createPaymentRecordDetail(paymentRecordInfoEntity.getId(), "", 
					SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMPANY_REPAYMENT_EXPENSE_LOAN, expenseAmount));
			break;
		case Constant.REPAYMENT_TYPE_02: // 逾期还款
			// 账户管理费
			if(expenseAmount.compareTo(BigDecimal.ZERO) > 0) {
				payRecordDetailList.add(createPaymentRecordDetail(paymentRecordInfoEntity.getId(), "", 
						SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMPANY_REPAYMENT_EXPENSE_LOAN, expenseAmount));
			}

			// 罚息
			if(penaltyAmount.compareTo(BigDecimal.ZERO) > 0) {
				
				payRecordDetailList.add(createPaymentRecordDetail(paymentRecordInfoEntity.getId(), "", 
						SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMPANY_REPAYMENT_PENALTY_LOAN, penaltyAmount));
			}
			break;
		case Constant.REPAYMENT_TYPE_03: // 提前还款
			// 账户管理费
			payRecordDetailList.add(createPaymentRecordDetail(paymentRecordInfoEntity.getId(), "", 
					SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMPANY_REPAYMENT_EXPENSE_LOAN, expenseAmount));
			break;
		case Constant.REPAYMENT_TYPE_04: // 风险金垫付
			// 账户管理费
			payRecordDetailList.add(createPaymentRecordDetail(paymentRecordInfoEntity.getId(), "", 
					SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_RISK_OVERDUE_EXPENSE_LOAN, expenseAmount));
			break;

		case Constant.REPAYMENT_TYPE_05: //加息券奖励金发放
			payRecordDetailList.add(createPaymentRecordDetail(paymentRecordInfoEntity.getId(), "",
					SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_AWARD_LOAN, expenseAmount));
			break;
		}

		paymentRecordDetailRepository.save(payRecordDetailList);
		
	}
}
