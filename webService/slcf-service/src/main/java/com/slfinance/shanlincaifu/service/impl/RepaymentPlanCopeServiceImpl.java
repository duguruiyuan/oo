package com.slfinance.shanlincaifu.service.impl;

import java.util.List;

import com.slfinance.shanlincaifu.utils.WithHoldingConstant;
import com.slfinance.shanlincaifu.validate.RuleUtils;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.RepaymentPlanCopeEntity;
import com.slfinance.shanlincaifu.repository.RepaymentPlanCopeRepository;
import com.slfinance.shanlincaifu.service.RepaymentPlanCopeService;

@Slf4j
@Service("repaymentPlanCopeService")
public class RepaymentPlanCopeServiceImpl implements RepaymentPlanCopeService {
	@Autowired
	private RepaymentPlanCopeRepository repaymentPlanCopeRepository;

	/**
	 * 根据loanNo查询还款计划
	 */
	public List<RepaymentPlanCopeEntity> queryByLoanCode(String loanCode) {
		List<RepaymentPlanCopeEntity> list = repaymentPlanCopeRepository
				.findByLoanCode(loanCode);
		if (list != null && list.size() != 0) {
			log.info("根据loancode查询成功：");
		}
		return list;
	}

	/*
	 * 跟新还款计划
	 * 
	 * @see
	 * com.slfinance.shanlincaifu.service.RepaymentPlanCopeService#updatePlan
	 * (java.lang.String, java.util.List)
	 */
	@Override
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public void updatePlan(String loanCode, List<RepaymentPlanCopeEntity> planList) {
		List<RepaymentPlanCopeEntity> oldList = queryByLoanCode(loanCode);
		List<RepaymentPlanCopeEntity> saveList = Lists.newArrayList();
		if (planList.size() != 0) {
			for (RepaymentPlanCopeEntity oldPlan : oldList) {
				for (RepaymentPlanCopeEntity newPlan : planList) {
					if (oldPlan.getCurrentTerm() == newPlan.getCurrentTerm()) {
						oldPlan.setCurrentTerm(newPlan.getCurrentTerm());
						oldPlan.setExpectRepaymentDate(newPlan
								.getExpectRepaymentDate());
						oldPlan.setRepaymentTotalAmount(newPlan
								.getRepaymentTotalAmount());
						oldPlan.setRepaymentPrincipal(newPlan
								.getRepaymentPrincipal());
						oldPlan.setRepaymentInterest(newPlan
								.getRepaymentInterest());
						oldPlan.setAdvanceCleanupTotalAmount(newPlan
								.getAdvanceCleanupTotalAmount());
					}
				}
			}
		}

	}

	/***
	 * 更新还款状态
	 * @param loanNo
	 * @param currentTerm
	 * @param repayMentstatus
	 */
	@Override
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public void doUpdatePlanCopeStatus(String loanNo,String currentTerm,String repayMentstatus,String isLimit,String rieskamoutAccountId,String changeEnable) {
		RepaymentPlanCopeEntity repaymentPlanCopeEntity = repaymentPlanCopeRepository.findByLoanCodeAndCurrentTerm(loanNo,Integer.parseInt(currentTerm));
		repaymentPlanCopeEntity.setRepaymentStatus(repayMentstatus);
		if (RuleUtils.required(isLimit)){
			repaymentPlanCopeEntity.setIsLimit(isLimit);
		}
		if (RuleUtils.required(rieskamoutAccountId)){
			repaymentPlanCopeEntity.setIsLimit(isLimit);
		}
		if (RuleUtils.required(changeEnable)){
			repaymentPlanCopeEntity.setChangeEnable(changeEnable);
		}
	}

	@Override
	public RepaymentPlanCopeEntity doFindPlanByLoanNoAndTerm(String loanNo, String currentTerm) {
		return repaymentPlanCopeRepository.findByLoanCodeAndCurrentTerm(loanNo,Integer.parseInt(currentTerm));
	}


}
