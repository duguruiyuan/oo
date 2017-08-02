package com.slfinance.shanlincaifu.service;

import java.math.BigDecimal;
import java.util.Date;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.LoanDetailInfoEntity;
import com.slfinance.shanlincaifu.entity.LoanInfoEntity;
import com.slfinance.vo.ResultVo;

public interface LoanRepaymentPlanService {

	ResultVo createRepaymentPlan(LoanInfoEntity loanInfo,
			LoanDetailInfoEntity loanDetailInfo, BigDecimal loanAmount, Date setFirstRepaymentDay)
			throws SLException;

}
