package com.slfinance.shanlincaifu.service.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.LoanDetailInfoEntity;
import com.slfinance.shanlincaifu.entity.LoanInfoEntity;
import com.slfinance.shanlincaifu.entity.RepaymentPlanInfoEntity;
import com.slfinance.shanlincaifu.service.LoanRepaymentPlanService;
import com.slfinance.shanlincaifu.utils.ArithUtil;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.vo.ResultVo;

@Service("loanRepaymentPlanService")
public class LoanRepaymentPlanServiceImpl implements LoanRepaymentPlanService {
	
	@Override
	public ResultVo createRepaymentPlan(LoanInfoEntity loanInfo, LoanDetailInfoEntity loanDetailInfo, BigDecimal loanAmount, Date setFirstRepaymentDay) throws SLException{
		Map<String, Object> data = Maps.newHashMap();

		switch(loanInfo.getRepaymentMethod()) {
		case Constant.REPAYMENT_METHOD_01: // 等额本息
			data = repaymentMethod01(loanInfo, loanDetailInfo, loanAmount, setFirstRepaymentDay);
			break;
		case Constant.REPAYMENT_METHOD_03: // 到期还本付息
//			data = repaymentMethod03(loanInfo, loanDetailInfo, loanAmount, setFirstRepaymentDay);
			// update by liyy 2017/03/25 到期还本付息(支持天标)
			data = repaymentMethod13(loanInfo, loanDetailInfo, loanAmount, setFirstRepaymentDay);
			break;
		case Constant.REPAYMENT_METHOD_04: // 先息后本(按季)
			data = repaymentMethod04(loanInfo, loanDetailInfo, loanAmount, 3, setFirstRepaymentDay);
			break;
		case Constant.REPAYMENT_METHOD_05: // 先息后本(按月)
		case Constant.REPAYMENT_METHOD_06: // 每期还息到期付本
			data = repaymentMethod04(loanInfo, loanDetailInfo, loanAmount, 1, setFirstRepaymentDay);
			break;
			
		}
		
		return new ResultVo(true, "创建还款计划成功", data);
	}
	
	/**
	 * 等额本息还款计划
	 * @author  wagnjf
	 * @date    2016年12月19日 下午5:19:31
	 * @param projectInfoEntity
	 */
	private Map<String, Object> repaymentMethod01(LoanInfoEntity loanInfo, LoanDetailInfoEntity loanDetailInfo, BigDecimal loanAmount, Date setFirstRepaymentDay) {
		Map<String, Object> data = Maps.newHashMap();

		// 投资期限
		Integer typeTerm = Integer.parseInt(loanInfo.getLoanTerm().toString());
		// 投资月利率
		BigDecimal monthlyRate = ArithUtil.div(loanDetailInfo.getYearIrr(), new BigDecimal(Constant.MONTH_OF_YEAR));
		// 原始月利率 = 年化利率/12 + 月服务费率
		BigDecimal actualmonthlyRate = ArithUtil.add(ArithUtil.div(loanDetailInfo.getYearIrr(), new BigDecimal(Constant.MONTH_OF_YEAR)), loanInfo.getMonthlyManageRate());
		// 数字1
		BigDecimal one = new BigDecimal("1");
		// (1+投资月利率)^期限
		BigDecimal commonInterest = new BigDecimal(String.valueOf(Math.pow((1 + monthlyRate.doubleValue()), typeTerm)));
		// (1+原始月利率)^期限
		BigDecimal actualCommonInterest = new BigDecimal(String.valueOf(Math.pow((1 + actualmonthlyRate.doubleValue()), typeTerm)));
		// 投资人应得 = (投资金额*投资月利率*(1+投资月利率)^期限)/((1+投资月利率)^期限-1)
		BigDecimal userIncome = ArithUtil.div(ArithUtil.mul(ArithUtil.mul(loanAmount, monthlyRate), commonInterest), ArithUtil.sub(commonInterest, one));
		// 总本息
		BigDecimal totalUserIncome = ArithUtil.mul(userIncome, new BigDecimal(typeTerm.toString()));
		// 还款总额 = (投资金额*原始月利率*(1+原始月利率)^期限)/((1+原始月利率)^期限-1)
		BigDecimal repaymentTotalAmount = ArithUtil.div(ArithUtil.mul(ArithUtil.mul(loanAmount, actualmonthlyRate), actualCommonInterest), ArithUtil.sub(actualCommonInterest, one));
		repaymentTotalAmount = ArithUtil.formatScale(repaymentTotalAmount, 2);
		userIncome = ArithUtil.formatScale(userIncome, 2);// 每期应还本息
		// 管理费=还款总额-投资人应得
		BigDecimal accountManageExpense = ArithUtil.sub(repaymentTotalAmount, userIncome);
		// 上一期剩余本金(开始时等于投资金额)
		BigDecimal prevRemainderPrincipal = loanAmount;
		// 首个还款日(取生效日期)
		Date firstRepaymentDay = setFirstRepaymentDay!=null?setFirstRepaymentDay:new Date();
		
		BigDecimal tmpRepaymentInterest = BigDecimal.ZERO, 
				   tmpRepaymentPrincipal = BigDecimal.ZERO,
				   tmpRemainderPrincipal = BigDecimal.ZERO;	

		accountManageExpense = ArithUtil.formatScale(accountManageExpense, 2);
		totalUserIncome = ArithUtil.formatScale(totalUserIncome, 2);
	
		List<RepaymentPlanInfoEntity> planList = Lists.newArrayList();
		for(int i = 1; i <= typeTerm; i ++) {
			// 利息 = 上一期剩余本金*投资月利率
			BigDecimal repaymentInterest = ArithUtil.mul(prevRemainderPrincipal, monthlyRate);
			repaymentInterest = ArithUtil.formatScale(repaymentInterest, 2);
			// 本金 = 投资人应得-利息
			BigDecimal repaymentPrincipal = ArithUtil.sub(userIncome, repaymentInterest);
			// 剩余本金=上一期剩余本金-本期应还本金
			BigDecimal remainderPrincipal = ArithUtil.sub(prevRemainderPrincipal, repaymentPrincipal);
			// 违约金=上一期剩余本金*违约金率
			BigDecimal penaltyAmount = ArithUtil.mul(prevRemainderPrincipal, loanInfo.getOverdueRepaymentRate());
			penaltyAmount = ArithUtil.formatScale(penaltyAmount, 2);
			// 提前结清金额=上一期的剩余本金 + 违约金
			BigDecimal advanceCleanupTotalAmount = ArithUtil.add(prevRemainderPrincipal, penaltyAmount);
			
			repaymentPrincipal = ArithUtil.formatScale(repaymentPrincipal, 2);			
			remainderPrincipal = ArithUtil.formatScale(remainderPrincipal, 2);
			advanceCleanupTotalAmount = ArithUtil.formatScale(advanceCleanupTotalAmount, 2);
			
			if(i == typeTerm) { // 最后一期取剩余值
				repaymentInterest = ArithUtil.sub(ArithUtil.sub(totalUserIncome, loanAmount), tmpRepaymentInterest);
				repaymentPrincipal = ArithUtil.sub(loanAmount, tmpRepaymentPrincipal);
				remainderPrincipal = ArithUtil.sub(prevRemainderPrincipal, repaymentPrincipal);
				repaymentTotalAmount = ArithUtil.add(ArithUtil.add(repaymentPrincipal, repaymentInterest), accountManageExpense);
			}
			else {
				tmpRepaymentInterest = ArithUtil.add(tmpRepaymentInterest, repaymentInterest);
				tmpRepaymentPrincipal = ArithUtil.add(tmpRepaymentPrincipal, repaymentPrincipal);
				tmpRemainderPrincipal = ArithUtil.add(tmpRemainderPrincipal, remainderPrincipal);
			}
			
			// 生成还款计划
			RepaymentPlanInfoEntity repaymentPlanInfoEntity = new RepaymentPlanInfoEntity();
			repaymentPlanInfoEntity.setCurrentTerm(i);
			repaymentPlanInfoEntity.setExpectRepaymentDate(DateUtils.formatDate(DateUtils.getAfterMonth(firstRepaymentDay, i), "yyyyMMdd"));
			repaymentPlanInfoEntity.setRepaymentTotalAmount(repaymentTotalAmount);
			repaymentPlanInfoEntity.setTermAlreadyRepayAmount(BigDecimal.ZERO);
			repaymentPlanInfoEntity.setRepaymentPrincipal(repaymentPrincipal);
			repaymentPlanInfoEntity.setRepaymentInterest(repaymentInterest);
			repaymentPlanInfoEntity.setRemainderPrincipal(remainderPrincipal);
			repaymentPlanInfoEntity.setAdvanceCleanupTotalAmount(advanceCleanupTotalAmount);
			repaymentPlanInfoEntity.setRepaymentStatus(Constant.REPAYMENT_STATUS_WAIT);
			repaymentPlanInfoEntity.setFactRepaymentDate(null);
			repaymentPlanInfoEntity.setPenaltyStartDate(null);
			repaymentPlanInfoEntity.setAccountManageExpense(accountManageExpense);
//			repaymentPlanInfoEntity.setProjectId(loanInfo.getId());
			repaymentPlanInfoEntity.setIsAmountFrozen(Constant.IS_AMOUNT_FROZEN_NO);
			repaymentPlanInfoEntity.setIsRiskamountRepay(Constant.IS_RISKAMOUNT_REPAY_NO);
			repaymentPlanInfoEntity.setPenaltyAmount(penaltyAmount);
			repaymentPlanInfoEntity.setLoanEntity(loanInfo);
			repaymentPlanInfoEntity.setBasicModelProperty(loanInfo.getLastUpdateUser(), true);
			planList.add(repaymentPlanInfoEntity);
			
			// 修改上一期剩余本金为本次剩余本金
			prevRemainderPrincipal = remainderPrincipal;
		}

		data.put("planList", planList);
		data.put("firstRepaymentDay", planList.get(0).getExpectRepaymentDate());
		data.put("lastRepaymentDay",  planList.get(planList.size()-1).getExpectRepaymentDate());
		data.put("nextRepaymentDay", planList.get(0).getExpectRepaymentDate());
		data.put("remainderTerms", typeTerm);
		data.put("remainderPrincipal", loanAmount);
		data.put("accountManageExpense", ArithUtil.mul(accountManageExpense, new BigDecimal(typeTerm)));
		
		return data;
	}
	
	/**
	 * 到期还本付息
	 * @author  wangjf
	 * @date    2016年12月19日 下午5:19:31
	 * @param projectInfoEntity
	 * @deprecated 
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private Map<String, Object> repaymentMethod03(LoanInfoEntity loanInfo, LoanDetailInfoEntity loanDetailInfo, BigDecimal loanAmount, Date setFirstRepaymentDay) {
		Map<String, Object> data = Maps.newHashMap();

		// 投资期限
		Integer typeTerm = Integer.parseInt(loanInfo.getLoanTerm().toString());
		// 投资月利率
		BigDecimal monthlyRate = ArithUtil.div(loanDetailInfo.getYearIrr(), new BigDecimal(Constant.MONTH_OF_YEAR));
		// 原始月利率 = 年化利率/12 + 月服务费率
		BigDecimal actualmonthlyRate = ArithUtil.add(ArithUtil.div(loanDetailInfo.getYearIrr(), new BigDecimal(Constant.MONTH_OF_YEAR)), loanInfo.getMonthlyManageRate());
		// 利息 = 投资金额*期限* 投资年利率/12(投资月利率)
		BigDecimal repaymentInterest = ArithUtil.mul(ArithUtil.mul(loanAmount, new BigDecimal(typeTerm.toString())), monthlyRate);
		repaymentInterest = ArithUtil.formatScale(repaymentInterest, 2);
		// 投资人应得 = 投资金额+利息
		BigDecimal userIncome = ArithUtil.add(loanAmount, repaymentInterest);
		userIncome = ArithUtil.formatScale(userIncome, 2);
		// 还款总额 = 投资金额 + 投资金额*原始月利率*期限
		BigDecimal repaymentTotalAmount = ArithUtil.add(loanAmount, ArithUtil.mul(loanAmount, ArithUtil.mul(actualmonthlyRate, new BigDecimal(typeTerm.toString()))));
		repaymentTotalAmount = ArithUtil.formatScale(repaymentTotalAmount, 2);
		// 管理费=还款总额-投资人应得
		BigDecimal accountManageExpense = ArithUtil.sub(repaymentTotalAmount, userIncome);
		accountManageExpense = ArithUtil.formatScale(accountManageExpense, 2);
		// 首个还款日(取生效日期)
		Date firstRepaymentDay = setFirstRepaymentDay!=null?setFirstRepaymentDay:new Date();
		// 本金 = 投资金额
		BigDecimal repaymentPrincipal = loanAmount;
		// 剩余本金=0
		BigDecimal remainderPrincipal = BigDecimal.ZERO;
		// 违约金=0
		BigDecimal penaltyAmount = BigDecimal.ZERO;
		// 提前结清金额=投资金额 + 投资金额*原始月利率*期限
		BigDecimal advanceCleanupTotalAmount = repaymentTotalAmount;
		
		// 生成还款计划
		RepaymentPlanInfoEntity repaymentPlanInfoEntity = new RepaymentPlanInfoEntity();
		repaymentPlanInfoEntity.setCurrentTerm(1);
		repaymentPlanInfoEntity.setExpectRepaymentDate(DateUtils.formatDate(DateUtils.getAfterMonth(firstRepaymentDay, typeTerm), "yyyyMMdd"));
		repaymentPlanInfoEntity.setRepaymentTotalAmount(repaymentTotalAmount);
		repaymentPlanInfoEntity.setTermAlreadyRepayAmount(BigDecimal.ZERO);
		repaymentPlanInfoEntity.setRepaymentPrincipal(repaymentPrincipal);
		repaymentPlanInfoEntity.setRepaymentInterest(repaymentInterest);
		repaymentPlanInfoEntity.setRemainderPrincipal(remainderPrincipal);
		repaymentPlanInfoEntity.setAdvanceCleanupTotalAmount(advanceCleanupTotalAmount);
		repaymentPlanInfoEntity.setRepaymentStatus(Constant.REPAYMENT_STATUS_WAIT);
		repaymentPlanInfoEntity.setFactRepaymentDate(null);
		repaymentPlanInfoEntity.setPenaltyStartDate(null);
		repaymentPlanInfoEntity.setAccountManageExpense(accountManageExpense);
//		repaymentPlanInfoEntity.setProjectId(loanInfo.getId());
		repaymentPlanInfoEntity.setIsAmountFrozen(Constant.IS_AMOUNT_FROZEN_NO);
		repaymentPlanInfoEntity.setIsRiskamountRepay(Constant.IS_RISKAMOUNT_REPAY_NO);
		repaymentPlanInfoEntity.setPenaltyAmount(penaltyAmount);
		repaymentPlanInfoEntity.setLoanEntity(loanInfo);
		repaymentPlanInfoEntity.setBasicModelProperty(loanInfo.getLastUpdateUser(), true);
		
		data.put("planList", Arrays.asList(repaymentPlanInfoEntity));
		data.put("firstRepaymentDay", repaymentPlanInfoEntity.getExpectRepaymentDate());
		data.put("lastRepaymentDay",  repaymentPlanInfoEntity.getExpectRepaymentDate());
		data.put("nextRepaymentDay", repaymentPlanInfoEntity.getExpectRepaymentDate());
		data.put("remainderTerms", 1);
		data.put("remainderPrincipal", loanAmount);
		data.put("accountManageExpense", accountManageExpense);
		
		return data;
	}

	/**
	 * 先息后本
	 *
	 * @author  wangjf
	 * @date    2016年12月19日 下午5:19:31
	 * @param projectInfoEntity
	 * @param cycle 跨度，如1表示1月一还，如3表示3月一还
	 */
	private Map<String, Object> repaymentMethod04(LoanInfoEntity loanInfo, LoanDetailInfoEntity loanDetailInfo, BigDecimal loanAmount, int cycle, Date setFirstRepaymentDay) throws SLException{
		if(loanInfo.getLoanTerm() % cycle != 0) {
			throw new SLException("生成还款计划失败！项目期数与还款方式存在冲突");
		}
		
		Map<String, Object> data = Maps.newHashMap();
		BigDecimal accountManageExpenseTotal = BigDecimal.ZERO;
		
		// 投资期限
		Integer typeTerm = Integer.parseInt(loanInfo.getLoanTerm().toString());
		// 投资月利率
		BigDecimal monthlyRate = ArithUtil.div(loanDetailInfo.getYearIrr(), new BigDecimal(Constant.MONTH_OF_YEAR));
		// 原始月利率 = 年化利率/12 + 月服务费率
		BigDecimal actualmonthlyRate = ArithUtil.add(ArithUtil.div(loanDetailInfo.getYearIrr(), new BigDecimal(Constant.MONTH_OF_YEAR)), loanInfo.getMonthlyManageRate());
		// 真实还款期数
		Integer actualTerm = typeTerm/cycle;
		// 上一期剩余本金(开始时等于投资金额)
		BigDecimal prevRemainderPrincipal = loanAmount;
		// 首个还款日(取生效日期)
		Date firstRepaymentDay = setFirstRepaymentDay!=null?setFirstRepaymentDay:new Date();
		// 利息 = 投资金额*投资月利率
		BigDecimal repaymentInterest = ArithUtil.mul(ArithUtil.mul(loanAmount, monthlyRate), new BigDecimal(String.valueOf(cycle)));
		// 借贷利息
		BigDecimal actualRepaymentInterest = ArithUtil.mul(ArithUtil.mul(loanAmount, actualmonthlyRate), new BigDecimal(String.valueOf(cycle)));
		// 总利息
		BigDecimal totalUserIncome = ArithUtil.mul(repaymentInterest, new BigDecimal(actualTerm.toString()));
		// 总借贷利息
		BigDecimal totalRepaymentInterest = ArithUtil.mul(actualRepaymentInterest, new BigDecimal(actualTerm.toString()));
		
		BigDecimal tmpRepaymentInterest = BigDecimal.ZERO, 
				   tmpActualRepaymentInterest = BigDecimal.ZERO;	
		
		repaymentInterest = ArithUtil.formatScale(repaymentInterest, 2);
		actualRepaymentInterest = ArithUtil.formatScale(actualRepaymentInterest, 2);
		totalUserIncome = ArithUtil.formatScale(totalUserIncome, 2);
		
		List<RepaymentPlanInfoEntity> planList = Lists.newArrayList();
		for(int i = 1; i <= actualTerm; i ++) {
			
			if(i == actualTerm) { // 最后一期取剩余值
				repaymentInterest = ArithUtil.sub(totalUserIncome, tmpRepaymentInterest);
				actualRepaymentInterest = ArithUtil.sub(totalRepaymentInterest, tmpActualRepaymentInterest);
			}
			else {
				tmpRepaymentInterest = ArithUtil.add(tmpRepaymentInterest, repaymentInterest);
				tmpActualRepaymentInterest = ArithUtil.add(tmpActualRepaymentInterest, actualRepaymentInterest);
			}
			
			// 投资人应得 = 利息（最后一期本金+利息）
			BigDecimal userIncome = (i == actualTerm ? ArithUtil.add(loanAmount, repaymentInterest) : repaymentInterest);
			// 还款总额 = 投资金额*原始月利率（最后一期 投资金额+投资金额*原始月利率）
			BigDecimal repaymentTotalAmount = (i == actualTerm ? ArithUtil.add(loanAmount, actualRepaymentInterest) : actualRepaymentInterest);			
			// 本金 = 0（最后一期本金投资金额）
			BigDecimal repaymentPrincipal = (i == actualTerm ? loanAmount : BigDecimal.ZERO);
			// 管理费=还款总额-投资人应得
			BigDecimal accountManageExpense = ArithUtil.sub(repaymentTotalAmount, userIncome);
			// 剩余本金=投资金额（最后一期 0）
			BigDecimal remainderPrincipal = (i == actualTerm ? BigDecimal.ZERO : loanAmount);
			// 违约金=上一期剩余本金*违约金率（第一期时上一期剩余本金=投资金额）
			BigDecimal penaltyAmount = ArithUtil.mul(prevRemainderPrincipal, loanInfo.getOverdueRepaymentRate());
			penaltyAmount = ArithUtil.formatScale(penaltyAmount, 2);
			// 提前结清金额=上一期的剩余本金（首期投资金额）+ 违约金
			BigDecimal advanceCleanupTotalAmount = ArithUtil.add(prevRemainderPrincipal, penaltyAmount);
			
			repaymentTotalAmount = ArithUtil.formatScale(repaymentTotalAmount, 2);
			accountManageExpense = ArithUtil.formatScale(accountManageExpense, 2);
			advanceCleanupTotalAmount = ArithUtil.formatScale(advanceCleanupTotalAmount, 2);
			accountManageExpenseTotal = ArithUtil.add(accountManageExpenseTotal, accountManageExpense);

			// 生成还款计划
			RepaymentPlanInfoEntity repaymentPlanInfoEntity = new RepaymentPlanInfoEntity();
			repaymentPlanInfoEntity.setCurrentTerm(i);
			repaymentPlanInfoEntity.setExpectRepaymentDate(DateUtils.formatDate(DateUtils.getAfterMonth(firstRepaymentDay, i*cycle), "yyyyMMdd"));
			repaymentPlanInfoEntity.setRepaymentTotalAmount(repaymentTotalAmount);
			repaymentPlanInfoEntity.setTermAlreadyRepayAmount(BigDecimal.ZERO);
			repaymentPlanInfoEntity.setRepaymentPrincipal(repaymentPrincipal);
			repaymentPlanInfoEntity.setRepaymentInterest(repaymentInterest);
			repaymentPlanInfoEntity.setRemainderPrincipal(remainderPrincipal);
			repaymentPlanInfoEntity.setAdvanceCleanupTotalAmount(advanceCleanupTotalAmount);
			repaymentPlanInfoEntity.setRepaymentStatus(Constant.REPAYMENT_STATUS_WAIT);
			repaymentPlanInfoEntity.setFactRepaymentDate(null);
			repaymentPlanInfoEntity.setPenaltyStartDate(null);
			repaymentPlanInfoEntity.setAccountManageExpense(accountManageExpense);
//			repaymentPlanInfoEntity.setProjectId(loanInfo.getId());
			repaymentPlanInfoEntity.setIsAmountFrozen(Constant.IS_AMOUNT_FROZEN_NO);
			repaymentPlanInfoEntity.setIsRiskamountRepay(Constant.IS_RISKAMOUNT_REPAY_NO);
			repaymentPlanInfoEntity.setPenaltyAmount(penaltyAmount);
			repaymentPlanInfoEntity.setLoanEntity(loanInfo);
			repaymentPlanInfoEntity.setBasicModelProperty(loanInfo.getLastUpdateUser(), true);
			planList.add(repaymentPlanInfoEntity);
			
			// 修改上一期剩余本金为本次剩余本金
			prevRemainderPrincipal = remainderPrincipal;
		}
		
		data.put("planList", planList);
		data.put("firstRepaymentDay", planList.get(0).getExpectRepaymentDate());
		data.put("lastRepaymentDay",  planList.get(planList.size()-1).getExpectRepaymentDate());
		data.put("nextRepaymentDay", planList.get(0).getExpectRepaymentDate());
		data.put("remainderTerms", actualTerm);
		data.put("remainderPrincipal", loanAmount);
		data.put("accountManageExpense", accountManageExpenseTotal);
		
		return data;
	}
	
	/**
	 * 等额本息还款计划(支持天标)
	 * 等额本息 算期数，一期算30天，也要求项目的期限是 必须是30天的倍数(支持指尖贷)
	 * @author  wangjf
	 * @date    2016年1月6日 下午5:19:31
	 * @param projectInfoEntity
	 */
	@SuppressWarnings("unused")
	@Deprecated /* 天标没有等额本息 */
	private Map<String, Object> repaymentMethod11(LoanInfoEntity loanInfo, LoanDetailInfoEntity loanDetailInfo, BigDecimal loanAmount, Date setFirstRepaymentDay) {
		
		Map<String, Object> result = Maps.newHashMap();

		// 投资期限
		Integer typeTerm = Integer.parseInt(loanInfo.getLoanTerm().toString());
		//modified by zhangt 2016/11/18 指尖贷修改  // modified by liyy 2017/03/24  善意贷 == 指尖贷
		if ( // Constant.LOAN_PRODUCT_NAME_03.equals(loanInfo.getLoanType()) && 
				Constant.LOAN_UNIT_DAY.equals(loanInfo.getLoanUnit())) {
			typeTerm = typeTerm / 30;
		}
		// 投资月利率
		BigDecimal monthlyRate = ArithUtil.div(loanDetailInfo.getYearIrr(), new BigDecimal(Constant.MONTH_OF_YEAR));
		// 原始月利率 = 年化利率/12 + 月服务费率
		BigDecimal actualmonthlyRate = ArithUtil.add(ArithUtil.div(loanDetailInfo.getYearIrr(), new BigDecimal(Constant.MONTH_OF_YEAR)), loanInfo.getMonthlyManageRate());
		// 数字1
		BigDecimal one = new BigDecimal("1");
		// (1+投资月利率)^期限
		BigDecimal commonInterest = new BigDecimal(String.valueOf(Math.pow((1 + monthlyRate.doubleValue()), typeTerm)));
		// (1+原始月利率)^期限
		BigDecimal actualCommonInterest = new BigDecimal(String.valueOf(Math.pow((1 + actualmonthlyRate.doubleValue()), typeTerm)));
		// 投资人应得 = (投资金额*投资月利率*(1+投资月利率)^期限)/((1+投资月利率)^期限-1)
		BigDecimal userIncome = ArithUtil.div(ArithUtil.mul(ArithUtil.mul(loanAmount, monthlyRate), commonInterest), ArithUtil.sub(commonInterest, one));
		// 总本息
		BigDecimal totalUserIncome = ArithUtil.mul(userIncome, new BigDecimal(typeTerm.toString()));
		// 还款总额 = (投资金额*原始月利率*(1+原始月利率)^期限)/((1+原始月利率)^期限-1)
		BigDecimal repaymentTotalAmount = ArithUtil.div(ArithUtil.mul(ArithUtil.mul(loanAmount, actualmonthlyRate), actualCommonInterest), ArithUtil.sub(actualCommonInterest, one));
		repaymentTotalAmount = ArithUtil.formatScale(repaymentTotalAmount, 2);
		userIncome = ArithUtil.formatScale(userIncome, 2);// 每期应还本息
		// 管理费=还款总额-投资人应得
		BigDecimal accountManageExpense = ArithUtil.sub(repaymentTotalAmount, userIncome);
		// 上一期剩余本金(开始时等于投资金额)
		BigDecimal prevRemainderPrincipal = loanAmount;
		// 首个还款日(取生效日期)
		Date firstRepaymentDay = setFirstRepaymentDay!=null?setFirstRepaymentDay:new Date();
		
		BigDecimal tmpRepaymentInterest = BigDecimal.ZERO, 
				   tmpRepaymentPrincipal = BigDecimal.ZERO,
				   tmpRemainderPrincipal = BigDecimal.ZERO;	

		accountManageExpense = ArithUtil.formatScale(accountManageExpense, 2);
		totalUserIncome = ArithUtil.formatScale(totalUserIncome, 2);
	
		List<RepaymentPlanInfoEntity> planList = Lists.newArrayList();
		for(int i = 1; i <= typeTerm; i ++) {
			// 利息 = 上一期剩余本金*投资月利率
			BigDecimal repaymentInterest = ArithUtil.mul(prevRemainderPrincipal, monthlyRate);
			repaymentInterest = ArithUtil.formatScale(repaymentInterest, 2);
			// 本金 = 投资人应得-利息
			BigDecimal repaymentPrincipal = ArithUtil.sub(userIncome, repaymentInterest);
			// 剩余本金=上一期剩余本金-本期应还本金
			BigDecimal remainderPrincipal = ArithUtil.sub(prevRemainderPrincipal, repaymentPrincipal);
			// 违约金=上一期剩余本金*违约金率
			BigDecimal penaltyAmount = ArithUtil.mul(prevRemainderPrincipal, loanInfo.getOverdueRepaymentRate());
			penaltyAmount = ArithUtil.formatScale(penaltyAmount, 2);
			// 提前结清金额=上一期的剩余本金 + 违约金
			BigDecimal advanceCleanupTotalAmount = ArithUtil.add(prevRemainderPrincipal, penaltyAmount);
			
			repaymentPrincipal = ArithUtil.formatScale(repaymentPrincipal, 2);			
			remainderPrincipal = ArithUtil.formatScale(remainderPrincipal, 2);
			advanceCleanupTotalAmount = ArithUtil.formatScale(advanceCleanupTotalAmount, 2);
			
			if(i == typeTerm) { // 最后一期取剩余值
				repaymentInterest = ArithUtil.sub(ArithUtil.sub(totalUserIncome, loanAmount), tmpRepaymentInterest);
				repaymentPrincipal = ArithUtil.sub(loanAmount, tmpRepaymentPrincipal);
				remainderPrincipal = ArithUtil.sub(prevRemainderPrincipal, repaymentPrincipal);
				repaymentTotalAmount = ArithUtil.add(ArithUtil.add(repaymentPrincipal, repaymentInterest), accountManageExpense);
			}
			else {
				tmpRepaymentInterest = ArithUtil.add(tmpRepaymentInterest, repaymentInterest);
				tmpRepaymentPrincipal = ArithUtil.add(tmpRepaymentPrincipal, repaymentPrincipal);
				tmpRemainderPrincipal = ArithUtil.add(tmpRemainderPrincipal, remainderPrincipal);
			}
			
			// 生成还款计划
			RepaymentPlanInfoEntity repaymentPlanInfoEntity = new RepaymentPlanInfoEntity();
			repaymentPlanInfoEntity.setCurrentTerm(i);
			repaymentPlanInfoEntity.setExpectRepaymentDate(DateUtils.formatDate(
				Constant.LOAN_UNIT_DAY.equals(loanInfo.getLoanUnit()) 
					? DateUtils.getAfterDay(firstRepaymentDay, i * 30)
					: DateUtils.getAfterMonth(firstRepaymentDay, i)
				, "yyyyMMdd")
			);
			repaymentPlanInfoEntity.setRepaymentTotalAmount(repaymentTotalAmount);
			repaymentPlanInfoEntity.setTermAlreadyRepayAmount(BigDecimal.ZERO);
			repaymentPlanInfoEntity.setRepaymentPrincipal(repaymentPrincipal);
			repaymentPlanInfoEntity.setRepaymentInterest(repaymentInterest);
			repaymentPlanInfoEntity.setRemainderPrincipal(remainderPrincipal);
			repaymentPlanInfoEntity.setAdvanceCleanupTotalAmount(advanceCleanupTotalAmount);
			repaymentPlanInfoEntity.setRepaymentStatus(Constant.REPAYMENT_STATUS_WAIT);
			repaymentPlanInfoEntity.setFactRepaymentDate(null);
			repaymentPlanInfoEntity.setPenaltyStartDate(null);
			repaymentPlanInfoEntity.setAccountManageExpense(accountManageExpense);
//			repaymentPlanInfoEntity.setProjectId(loanInfo.getId());
			repaymentPlanInfoEntity.setIsAmountFrozen(Constant.IS_AMOUNT_FROZEN_NO);
			repaymentPlanInfoEntity.setIsRiskamountRepay(Constant.IS_RISKAMOUNT_REPAY_NO);
			repaymentPlanInfoEntity.setPenaltyAmount(penaltyAmount);
			repaymentPlanInfoEntity.setLoanEntity(loanInfo);
			repaymentPlanInfoEntity.setBasicModelProperty(loanInfo.getLastUpdateUser(), true);
			planList.add(repaymentPlanInfoEntity);
			
			// 修改上一期剩余本金为本次剩余本金
			prevRemainderPrincipal = remainderPrincipal;
		}

		result.put("planList", planList);
		result.put("firstRepaymentDay", planList.get(0).getExpectRepaymentDate());
		result.put("lastRepaymentDay",  planList.get(planList.size()-1).getExpectRepaymentDate());
		result.put("nextRepaymentDay", planList.get(0).getExpectRepaymentDate());
		result.put("remainderTerms", typeTerm);
		result.put("remainderPrincipal", loanAmount);
		result.put("accountManageExpense", ArithUtil.mul(accountManageExpense, new BigDecimal(typeTerm)));
		
		return result;
	}
	

	/**
	 * 到期还本付息(支持天标)
	 * @author  wangjf
	 * @date    2016年1月6日 下午5:20:13
	 * @param projectInfoEntity
	 * @update 2017年3月25日 下午5:20:13
	 */
	private Map<String, Object> repaymentMethod13(LoanInfoEntity loanInfo, LoanDetailInfoEntity loanDetailInfo, BigDecimal loanAmount, Date setFirstRepaymentDay) {
		
		Map<String, Object> result = Maps.newHashMap();

		// 投资期限
		Integer typeTerm = Integer.parseInt(loanInfo.getLoanTerm().toString());
		//投资期限单位
		String termUnit = loanInfo.getLoanUnit();
//		// 投资月利率
//		BigDecimal monthlyRate = ArithUtil.div(loanDetailInfo.getYearIrr(), new BigDecimal("12"));
//		// 原始月利率 = 年化利率/12 + 月服务费率
//		BigDecimal actualmonthlyRate = ArithUtil.add(ArithUtil.div(loanDetailInfo.getYearIrr(), new BigDecimal("12")), loanInfo.getMonthlyManageRate());
		// 原始年利率 = 投资年利率  + [年服务费率(期末)]
		BigDecimal actualYearRate = loanDetailInfo.getYearIrr();
		if(Constant.MANAGE_EXPENSE_DEAL_TYPE_03.equals(loanInfo.getManageExpenseDealType())){
			actualYearRate = ArithUtil.add(actualYearRate, loanInfo.getManageRate());
		}
		
		// 利息 = 投资金额*投资年利率*期限/12
		// 天标的利息：投资金额*投资年利率*天标期限/360
		BigDecimal repaymentInterest = ArithUtil.div(ArithUtil.mul(loanAmount, ArithUtil.mul(loanDetailInfo.getYearIrr(), new BigDecimal(typeTerm.toString())))
													, Constant.LOAN_UNIT_DAY.equals(termUnit) ? new BigDecimal(Constant.DAYS_OF_YEAR) : new BigDecimal(Constant.MONTH_OF_YEAR)); 
		repaymentInterest = ArithUtil.formatScale(repaymentInterest, 2);
		
		// 投资人应得 = 投资金额+利息
		BigDecimal userIncome = ArithUtil.add(loanAmount, repaymentInterest);
		userIncome = ArithUtil.formatScale(userIncome, 2);
		//     还款总额 = 投资金额 + 投资金额*原始年利率*期限/12
		// 天标的还款总额 = 投资金额 + 投资金额*原始年利率*天标期限/360
		BigDecimal repaymentTotalAmount = ArithUtil.add(loanAmount, ArithUtil.div(ArithUtil.mul(loanAmount, ArithUtil.mul(actualYearRate, new BigDecimal(typeTerm.toString())))
																				, Constant.LOAN_UNIT_DAY.equals(termUnit) ? new BigDecimal(Constant.DAYS_OF_YEAR) : new BigDecimal(Constant.MONTH_OF_YEAR)));
		repaymentTotalAmount = ArithUtil.formatScale(repaymentTotalAmount, 2);
		
		// 管理费=还款总额-投资人应得
		BigDecimal accountManageExpense = ArithUtil.sub(repaymentTotalAmount, userIncome);
		accountManageExpense = ArithUtil.formatScale(accountManageExpense, 2);
		// 首个还款日(取生效日期)
		Date firstRepaymentDay = setFirstRepaymentDay!=null?setFirstRepaymentDay:new Date();
		// 本金 = 投资金额
		BigDecimal repaymentPrincipal = loanAmount;
		// 剩余本金=0
		BigDecimal remainderPrincipal = BigDecimal.ZERO;
		// 违约金=0
		BigDecimal penaltyAmount = BigDecimal.ZERO;
		// 提前结清金额=投资金额 + 投资金额*原始月利率*期限
		BigDecimal advanceCleanupTotalAmount = repaymentTotalAmount;
		
		// 生成还款计划
		RepaymentPlanInfoEntity repaymentPlanInfoEntity = new RepaymentPlanInfoEntity();
		repaymentPlanInfoEntity.setCurrentTerm(1);
		repaymentPlanInfoEntity.setExpectRepaymentDate(DateUtils.formatDate(Constant.LOAN_UNIT_DAY.equals(termUnit) ? DateUtils.getAfterDay(firstRepaymentDay, typeTerm) : DateUtils.getAfterMonth(firstRepaymentDay, typeTerm), "yyyyMMdd"));
		repaymentPlanInfoEntity.setRepaymentTotalAmount(repaymentTotalAmount);
		repaymentPlanInfoEntity.setTermAlreadyRepayAmount(BigDecimal.ZERO);
		repaymentPlanInfoEntity.setRepaymentPrincipal(repaymentPrincipal);
		repaymentPlanInfoEntity.setRepaymentInterest(repaymentInterest);
		repaymentPlanInfoEntity.setRemainderPrincipal(remainderPrincipal);
		repaymentPlanInfoEntity.setAdvanceCleanupTotalAmount(advanceCleanupTotalAmount);
		repaymentPlanInfoEntity.setRepaymentStatus(Constant.REPAYMENT_STATUS_WAIT);
		repaymentPlanInfoEntity.setFactRepaymentDate(null);
		repaymentPlanInfoEntity.setPenaltyStartDate(null);
		repaymentPlanInfoEntity.setAccountManageExpense(accountManageExpense);
//		repaymentPlanInfoEntity.setProjectId(loanInfo.getId());
		repaymentPlanInfoEntity.setIsAmountFrozen(Constant.IS_AMOUNT_FROZEN_NO);
		repaymentPlanInfoEntity.setIsRiskamountRepay(Constant.IS_RISKAMOUNT_REPAY_NO);
		repaymentPlanInfoEntity.setPenaltyAmount(penaltyAmount);
		repaymentPlanInfoEntity.setLoanEntity(loanInfo);
		repaymentPlanInfoEntity.setBasicModelProperty(loanInfo.getLastUpdateUser(), true);
		
		result.put("planList", Arrays.asList(repaymentPlanInfoEntity));
		result.put("firstRepaymentDay", repaymentPlanInfoEntity.getExpectRepaymentDate());
		result.put("lastRepaymentDay",  repaymentPlanInfoEntity.getExpectRepaymentDate());
		result.put("nextRepaymentDay", repaymentPlanInfoEntity.getExpectRepaymentDate());
		result.put("remainderTerms", 1);
		result.put("remainderPrincipal", loanAmount);
		result.put("accountManageExpense", accountManageExpense);
		
		return result;
	}
}
