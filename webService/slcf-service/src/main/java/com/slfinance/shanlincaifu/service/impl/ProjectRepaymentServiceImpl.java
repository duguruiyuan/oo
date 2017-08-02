/** 
 * @(#)ProjectRepaymentServiceImpl.java 1.0.0 2016年1月6日 下午4:15:23  
 *  
 * Copyright © 2016 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AccountFlowInfoEntity;
import com.slfinance.shanlincaifu.entity.AccountInfoEntity;
import com.slfinance.shanlincaifu.entity.InvestInfoEntity;
import com.slfinance.shanlincaifu.entity.LogInfoEntity;
import com.slfinance.shanlincaifu.entity.ProjectInfoEntity;
import com.slfinance.shanlincaifu.entity.ProjectInvestInfoEntity;
import com.slfinance.shanlincaifu.entity.RepayRecordDetailInfoEntity;
import com.slfinance.shanlincaifu.entity.RepaymentPlanInfoEntity;
import com.slfinance.shanlincaifu.entity.RepaymentRecordInfoEntity;
import com.slfinance.shanlincaifu.entity.SubAccountInfoEntity;
import com.slfinance.shanlincaifu.repository.AccountInfoRepository;
import com.slfinance.shanlincaifu.repository.LogInfoEntityRepository;
import com.slfinance.shanlincaifu.repository.ProjectInfoRepository;
import com.slfinance.shanlincaifu.repository.ProjectInvestInfoRepository;
import com.slfinance.shanlincaifu.repository.RepayRecordDetailInfoRepository;
import com.slfinance.shanlincaifu.repository.RepaymentPlanInfoRepository;
import com.slfinance.shanlincaifu.repository.RepaymentRecordInfoRepository;
import com.slfinance.shanlincaifu.repository.SubAccountInfoRepository;
import com.slfinance.shanlincaifu.repository.custom.ProjectInfoRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.ProjectRepaymentRepositoryCustom;
import com.slfinance.shanlincaifu.service.AccountFlowService;
import com.slfinance.shanlincaifu.service.CustAccountService;
import com.slfinance.shanlincaifu.service.FlowNumberService;
import com.slfinance.shanlincaifu.service.ParamService;
import com.slfinance.shanlincaifu.service.ProjectPaymentService;
import com.slfinance.shanlincaifu.service.ProjectRepaymentService;
import com.slfinance.shanlincaifu.service.SMSService;
import com.slfinance.shanlincaifu.utils.ArithUtil;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.SubjectConstant;
import com.slfinance.vo.ResultVo;

/**   
 * 项目还款服务实现类
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2016年1月6日 下午4:15:23 $ 
 */
@Service("projectRepaymentService")
public class ProjectRepaymentServiceImpl implements ProjectRepaymentService {
	
	@Autowired
	private ProjectRepaymentRepositoryCustom projectRepaymentRepositoryCustom;
	
	@Autowired
	private ParamService paramService;
	
	@Autowired
	private SMSService smsService;

	@Override
	public ResultVo createRepaymentPlan(ProjectInfoEntity projectInfoEntity, BigDecimal projectAmount) throws SLException{
		
		Map<String, Object> result = Maps.newHashMap();

		switch(projectInfoEntity.getRepaymnetMethod()) {
		case Constant.REPAYMENT_METHOD_01: // 等额本息
			result = repaymentMethod01(projectInfoEntity, projectAmount);
			break;
		case Constant.REPAYMENT_METHOD_03: // 到期还本付息
			result = repaymentMethod03(projectInfoEntity, projectAmount);
			break;
		case Constant.REPAYMENT_METHOD_04: // 先息后本(按季)
			result = repaymentMethod04(projectInfoEntity, projectAmount, 3);
			break;
		case Constant.REPAYMENT_METHOD_05: // 先息后本(按月)
			result = repaymentMethod04(projectInfoEntity, projectAmount, 1);
			break;
		}
		
		return new ResultVo(true, "创建还款计划成功", result);
	}
	
	/**
	 * 等额本息还款计划
	 *
	 * @author  wangjf
	 * @date    2016年1月6日 下午5:19:31
	 * @param projectInfoEntity
	 */
	public Map<String, Object> repaymentMethod01(ProjectInfoEntity projectInfoEntity, BigDecimal projectAmount) {
		
		Map<String, Object> result = Maps.newHashMap();

		// 投资期限
		Integer typeTerm = projectInfoEntity.getTypeTerm();
		// 投资月利率
		BigDecimal monthlyRate = ArithUtil.div(projectInfoEntity.getYearRate(), new BigDecimal("12"));
		// 原始月利率
		BigDecimal actualmonthlyRate = ArithUtil.div(projectInfoEntity.getActualYearRate(), new BigDecimal("12"));
		// 数字1
		BigDecimal one = new BigDecimal("1");
		// (1+投资月利率)^期限
		BigDecimal commonInterest = new BigDecimal(String.valueOf(Math.pow((1 + monthlyRate.doubleValue()), typeTerm)));
		// (1+原始月利率)^期限
		BigDecimal actualCommonInterest = new BigDecimal(String.valueOf(Math.pow((1 + actualmonthlyRate.doubleValue()), typeTerm)));
		// 投资人应得 = (投资金额*投资月利率*(1+投资月利率)^期限)/((1+投资月利率)^期限-1)
		BigDecimal userIncome = ArithUtil.div(ArithUtil.mul(ArithUtil.mul(projectAmount, monthlyRate), commonInterest), ArithUtil.sub(commonInterest, one));
		// 总本息
		BigDecimal totalUserIncome = ArithUtil.mul(userIncome, new BigDecimal(typeTerm.toString()));
		// 还款总额 = (投资金额*原始月利率*(1+原始月利率)^期限)/((1+原始月利率)^期限-1)
		BigDecimal repaymentTotalAmount = ArithUtil.div(ArithUtil.mul(ArithUtil.mul(projectAmount, actualmonthlyRate), actualCommonInterest), ArithUtil.sub(actualCommonInterest, one));
		repaymentTotalAmount = ArithUtil.formatScale(repaymentTotalAmount, 2);
		userIncome = ArithUtil.formatScale(userIncome, 2);// 每期应还本息
		// 管理费=还款总额-投资人应得
		BigDecimal accountManageExpense = ArithUtil.sub(repaymentTotalAmount, userIncome);
		// 上一期剩余本金(开始时等于投资金额)
		BigDecimal prevRemainderPrincipal = projectAmount;
		// 首个还款日(取生效日期)
		Date firstRepaymentDay = projectInfoEntity.getEffectDate();
		
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
			BigDecimal penaltyAmount = ArithUtil.mul(prevRemainderPrincipal, projectInfoEntity.getPenaltyRate());
			penaltyAmount = ArithUtil.formatScale(penaltyAmount, 2);
			// 提前结清金额=上一期的剩余本金 + 违约金
			BigDecimal advanceCleanupTotalAmount = ArithUtil.add(prevRemainderPrincipal, penaltyAmount);
			
			repaymentPrincipal = ArithUtil.formatScale(repaymentPrincipal, 2);			
			remainderPrincipal = ArithUtil.formatScale(remainderPrincipal, 2);
			advanceCleanupTotalAmount = ArithUtil.formatScale(advanceCleanupTotalAmount, 2);
			
			if(i == typeTerm) { // 最后一期取剩余值
				repaymentInterest = ArithUtil.sub(ArithUtil.sub(totalUserIncome, projectAmount), tmpRepaymentInterest);
				repaymentPrincipal = ArithUtil.sub(projectAmount, tmpRepaymentPrincipal);
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
			repaymentPlanInfoEntity.setProjectId(projectInfoEntity.getId());
			repaymentPlanInfoEntity.setIsAmountFrozen(Constant.IS_AMOUNT_FROZEN_NO);
			repaymentPlanInfoEntity.setIsRiskamountRepay(Constant.IS_RISKAMOUNT_REPAY_NO);
			repaymentPlanInfoEntity.setPenaltyAmount(penaltyAmount);
			repaymentPlanInfoEntity.setLoanEntity(null);
			repaymentPlanInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
			planList.add(repaymentPlanInfoEntity);
			
			// 修改上一期剩余本金为本次剩余本金
			prevRemainderPrincipal = remainderPrincipal;
		}

		result.put("planList", planList);
		result.put("firstRepaymentDay", planList.get(0).getExpectRepaymentDate());
		result.put("lastRepaymentDay",  planList.get(planList.size()-1).getExpectRepaymentDate());
		result.put("nextRepaymentDay", planList.get(0).getExpectRepaymentDate());
		result.put("remainderTerms", typeTerm);
		result.put("remainderPrincipal", projectAmount);
		
		return result;
	}
	
	/**
	 * 到期还本付息
	 *
	 * @author  wangjf
	 * @date    2016年1月6日 下午5:20:13
	 * @param projectInfoEntity
	 */
	public Map<String, Object> repaymentMethod03(ProjectInfoEntity projectInfoEntity, BigDecimal projectAmount) {
		
		Map<String, Object> result = Maps.newHashMap();

		// 投资期限
		Integer typeTerm = projectInfoEntity.getTypeTerm();
		// 利息 = 投资金额*期限*投资年利率/12
		BigDecimal repaymentInterest = ArithUtil.div(ArithUtil.mul(projectAmount, ArithUtil.mul(projectInfoEntity.getYearRate(), new BigDecimal(typeTerm.toString()))), new BigDecimal("12"));
		repaymentInterest = ArithUtil.formatScale(repaymentInterest, 2);
		// 投资人应得 = 投资金额+利息
		BigDecimal userIncome = ArithUtil.add(projectAmount, repaymentInterest);
		userIncome = ArithUtil.formatScale(userIncome, 2);
		// 还款总额 = 投资金额 + 投资金额*原始月利率*期限
		BigDecimal repaymentTotalAmount = ArithUtil.add(projectAmount, ArithUtil.div(ArithUtil.mul(projectAmount, ArithUtil.mul(projectInfoEntity.getActualYearRate(), new BigDecimal(typeTerm.toString()))), new BigDecimal("12")));
		repaymentTotalAmount = ArithUtil.formatScale(repaymentTotalAmount, 2);
		// 管理费=还款总额-投资人应得
		BigDecimal accountManageExpense = ArithUtil.sub(repaymentTotalAmount, userIncome);
		accountManageExpense = ArithUtil.formatScale(accountManageExpense, 2);
		// 首个还款日(取生效日期)
		Date firstRepaymentDay = projectInfoEntity.getEffectDate();
		// 本金 = 投资金额
		BigDecimal repaymentPrincipal = projectAmount;
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
		repaymentPlanInfoEntity.setProjectId(projectInfoEntity.getId());
		repaymentPlanInfoEntity.setIsAmountFrozen(Constant.IS_AMOUNT_FROZEN_NO);
		repaymentPlanInfoEntity.setIsRiskamountRepay(Constant.IS_RISKAMOUNT_REPAY_NO);
		repaymentPlanInfoEntity.setPenaltyAmount(penaltyAmount);
		repaymentPlanInfoEntity.setLoanEntity(null);
		repaymentPlanInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
		
		result.put("planList", Arrays.asList(repaymentPlanInfoEntity));
		result.put("firstRepaymentDay", repaymentPlanInfoEntity.getExpectRepaymentDate());
		result.put("lastRepaymentDay",  repaymentPlanInfoEntity.getExpectRepaymentDate());
		result.put("nextRepaymentDay", repaymentPlanInfoEntity.getExpectRepaymentDate());
		result.put("remainderTerms", 1);
		result.put("remainderPrincipal", projectAmount);
		
		return result;
	}

	/**
	 * 先息后本
	 *
	 * @author  wangjf
	 * @date    2016年1月6日 下午5:20:43
	 * @param projectInfoEntity
	 * @param cycle 跨度，如1表示1月一还，如3表示3月一还
	 */
	public Map<String, Object> repaymentMethod04(ProjectInfoEntity projectInfoEntity, BigDecimal projectAmount, int cycle) throws SLException{

		if(projectInfoEntity.getTypeTerm() % cycle != 0) {
			throw new SLException("生成还款计划失败！项目期数与还款方式存在冲突");
		}
		
		Map<String, Object> result = Maps.newHashMap();
		
		// 投资期限
		Integer typeTerm = projectInfoEntity.getTypeTerm();
		// 真实还款期数
		Integer actualTerm = typeTerm/cycle;
		// 上一期剩余本金(开始时等于投资金额)
		BigDecimal prevRemainderPrincipal = projectAmount;
		// 首个还款日(取生效日期)
		Date firstRepaymentDay = projectInfoEntity.getEffectDate();
		// 利息 = 投资金额*投资月利率
		BigDecimal repaymentInterest = ArithUtil.mul(ArithUtil.mul(projectAmount, ArithUtil.div(projectInfoEntity.getYearRate(), new BigDecimal("12"))), new BigDecimal(String.valueOf(cycle)));
		// 借贷利息
		BigDecimal actualRepaymentInterest = ArithUtil.mul(ArithUtil.mul(projectAmount, ArithUtil.div(projectInfoEntity.getActualYearRate(), new BigDecimal("12"))), new BigDecimal(String.valueOf(cycle)));
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
			BigDecimal userIncome = (i == actualTerm ? ArithUtil.add(projectAmount, repaymentInterest) : repaymentInterest);
			// 还款总额 = 投资金额*原始月利率（最后一期 投资金额+投资金额*原始月利率）
			BigDecimal repaymentTotalAmount = (i == actualTerm ? ArithUtil.add(projectAmount, actualRepaymentInterest) : actualRepaymentInterest);			
			// 本金 = 0（最后一期本金投资金额）
			BigDecimal repaymentPrincipal = (i == actualTerm ? projectAmount : BigDecimal.ZERO);
			// 管理费=还款总额-投资人应得
			BigDecimal accountManageExpense = ArithUtil.sub(repaymentTotalAmount, userIncome);
			// 剩余本金=投资金额（最后一期 0）
			BigDecimal remainderPrincipal = (i == actualTerm ? BigDecimal.ZERO : projectAmount);
			// 违约金=上一期剩余本金*违约金率（第一期时上一期剩余本金=投资金额）
			BigDecimal penaltyAmount = ArithUtil.mul(prevRemainderPrincipal, projectInfoEntity.getPenaltyRate());
			penaltyAmount = ArithUtil.formatScale(penaltyAmount, 2);
			// 提前结清金额=上一期的剩余本金（首期投资金额）+ 违约金
			BigDecimal advanceCleanupTotalAmount = ArithUtil.add(prevRemainderPrincipal, penaltyAmount);
			
			repaymentTotalAmount = ArithUtil.formatScale(repaymentTotalAmount, 2);
			accountManageExpense = ArithUtil.formatScale(accountManageExpense, 2);
			advanceCleanupTotalAmount = ArithUtil.formatScale(advanceCleanupTotalAmount, 2);

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
			repaymentPlanInfoEntity.setProjectId(projectInfoEntity.getId());
			repaymentPlanInfoEntity.setIsAmountFrozen(Constant.IS_AMOUNT_FROZEN_NO);
			repaymentPlanInfoEntity.setIsRiskamountRepay(Constant.IS_RISKAMOUNT_REPAY_NO);
			repaymentPlanInfoEntity.setPenaltyAmount(penaltyAmount);
			repaymentPlanInfoEntity.setLoanEntity(null);
			repaymentPlanInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
			planList.add(repaymentPlanInfoEntity);
			
			// 修改上一期剩余本金为本次剩余本金
			prevRemainderPrincipal = remainderPrincipal;
		}
		
		result.put("planList", planList);
		result.put("firstRepaymentDay", planList.get(0).getExpectRepaymentDate());
		result.put("lastRepaymentDay",  planList.get(planList.size()-1).getExpectRepaymentDate());
		result.put("nextRepaymentDay", planList.get(0).getExpectRepaymentDate());
		result.put("remainderTerms", actualTerm);
		result.put("remainderPrincipal", projectAmount);
		
		return result;
	}
	
	@Override
	public ResultVo queryLatestRepaymentList(Map<String, Object> params) {
		Map<String, Object> resultMap = Maps.newHashMap();
			
		Page<Map<String, Object>> page = projectRepaymentRepositoryCustom.queryLatestRepaymentList(params);
		resultMap.put("iTotalDisplayRecords", page.getTotalElements());
		resultMap.put("data", page.getContent());
		
		return new ResultVo(true, "查询近期应还数据成功", resultMap);
	}

	@Override
	public ResultVo queryLatesttRepaymentTotal(Map<String, Object> params) {
		
		Map<String, Object> resultMap = projectRepaymentRepositoryCustom.queryLatesttRepaymentTotal(params);
		
		return new ResultVo(true, "查询成功", resultMap);
	}
	
	@Override
	public ResultVo queryOverdueRepaymentList(Map<String, Object> params) {
		Map<String, Object> resultMap = Maps.newHashMap();
		
		params.put("overdueRate", paramService.findOverdueRate());
		Page<Map<String, Object>> page = projectRepaymentRepositoryCustom.queryOverdueRepaymentList(params);
		resultMap.put("iTotalDisplayRecords", page.getTotalElements());
		resultMap.put("data", page.getContent());
		
		return new ResultVo(true, "查询逾期数据成功", resultMap);
	}
	
	@Override
	public ResultVo queryOverdueRepaymentTotal(Map<String, Object> params) {
		
		params.put("overdueRate", paramService.findOverdueRate());
		Map<String, Object> resultMap = projectRepaymentRepositoryCustom.queryOverdueRepaymentTotal(params);
		
		return new ResultVo(true, "查询逾期汇总成功", resultMap);
	}
	
	@Override
	public ResultVo queryAllRepaymentList(Map<String, Object> params) {
		
		Map<String, Object> resultMap = Maps.newHashMap();
		
		params.put("projectStatusList", Arrays.asList(Constant.PROJECT_STATUS_08, Constant.PROJECT_STATUS_09, Constant.PROJECT_STATUS_10, Constant.PROJECT_STATUS_11));
		Page<Map<String, Object>> page = projectRepaymentRepositoryCustom.queryAllRepaymentListNew(params);
		resultMap.put("iTotalDisplayRecords", page.getTotalElements());
		resultMap.put("data", page.getContent());
		
		return new ResultVo(true, "查询还款数据成功", resultMap);
	}

	@Override
	public ResultVo queryAllRepaymentTotal(Map<String, Object> params) {
		
		params.put("projectStatusList", Arrays.asList(Constant.PROJECT_STATUS_08, Constant.PROJECT_STATUS_09, Constant.PROJECT_STATUS_10, Constant.PROJECT_STATUS_11));
		Map<String, Object> resultMap = projectRepaymentRepositoryCustom.queryAllRepaymentTotal(params);
		
		return new ResultVo(true, "查询逾期汇总成功", resultMap);
	}
	
	@Override
	public ResultVo multiRepayment(Map<String, Object> params)
			throws SLException {
		return internalProjectRepaymentService.multiRepayment(params);
	}

	@Override
	public ResultVo singleRepayment(Map<String, Object> params)
			throws SLException {
		return internalProjectRepaymentService.singleRepayment(params);
	}

	@Deprecated
	@Override
	public ResultVo caclOverdueRepayment(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResultVo overdueRepayment(Map<String, Object> params)
			throws SLException {
		ResultVo result = internalProjectRepaymentService.overdueRepayment(params);
		if(ResultVo.isSuccess(result)) {
			// 发送短信
			List<Map<String, Object>> smsList = (List<Map<String, Object>>)result.getValue("data");
			for(Map<String, Object> sms : smsList) {
				smsService.asnySendSMSAndSystemMessage(sms);
			}
		}
		return result;
	}

	@Deprecated
	@Override
	public ResultVo caclEarlyRepayment(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResultVo earlyRepayment(Map<String, Object> params)
			throws SLException {
		ResultVo result = internalProjectRepaymentService.earlyRepayment(params);
		if(ResultVo.isSuccess(result)) {
			// 发送短信
			List<Map<String, Object>> smsList = (List<Map<String, Object>>)result.getValue("data");
			for(Map<String, Object> sms : smsList) {
				smsService.asnySendSMSAndSystemMessage(sms);
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResultVo normalRepayment(Map<String, Object> params)
			throws SLException {
		ResultVo result = internalProjectRepaymentService.normalRepayment(params);
		if(ResultVo.isSuccess(result)) {
			// 发送短信
			List<Map<String, Object>> smsList = (List<Map<String, Object>>)result.getValue("data");
			for(Map<String, Object> sms : smsList) {
				smsService.asnySendSMSAndSystemMessage(sms);
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResultVo riskRepayment(Map<String, Object> params)
			throws SLException {
		ResultVo result = internalProjectRepaymentService.riskRepayment(params);
		if(ResultVo.isSuccess(result)) {
			// 发送短信
			List<Map<String, Object>> smsList = (List<Map<String, Object>>)result.getValue("data");
			for(Map<String, Object> sms : smsList) {
				smsService.asnySendSMSAndSystemMessage(sms);
			}
		}
		return result;
	}
	
	@Autowired
	private InternalProjectRepaymentService internalProjectRepaymentService;
	
	@Service
	public static class InternalProjectRepaymentService {
		
		@Autowired
		private ProjectInfoRepository projectInfoRepository;
		
		@Autowired
		private ProjectInvestInfoRepository projectInvestInfoRepository;
		
		@Autowired
		private RepaymentPlanInfoRepository repaymentPlanInfoRepository;
		
		@Autowired
		private ProjectRepaymentRepositoryCustom projectRepaymentRepositoryCustom;
		
		@Autowired
		private AccountInfoRepository accountInfoRepository;
		
		@Autowired
		private SubAccountInfoRepository subAccountInfoRepository;
		
		@Autowired
		private CustAccountService custAccountService;
		
		@Autowired
		private FlowNumberService numberService;
		
		@Autowired
		private LogInfoEntityRepository logInfoEntityRepository;
		
		@Autowired
		private ParamService paramService;
		
		@Autowired
		private AccountFlowService accountFlowService;
		
		@Autowired
		private RepaymentRecordInfoRepository repaymentRecordInfoRepository;
		
		@Autowired
		private RepayRecordDetailInfoRepository repayRecordDetailInfoRepository;
		
		@Autowired
		private ProjectPaymentService projectPaymentService;
		
		@Autowired
		private ProjectInfoRepositoryCustom projectInfoRepositoryCustom;

		/**
		 * 批量还款
		 *
		 * @author  wangjf
		 * @date    2016年1月16日 下午2:43:12
		 * @param params
		 * @return
		 * @throws SLException
		 */
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		@SuppressWarnings("unchecked")
		public ResultVo multiRepayment(Map<String, Object> params)
				throws SLException {
			String userId = (String)params.get("userId");
			List<Map<String, Object>> replaymentPlanList = (List<Map<String, Object>>)params.get("replaymentPlanList");
			if(replaymentPlanList == null || replaymentPlanList.size() == 0) {
				return new ResultVo(false, "还款参数列表为空");
			}
			
			// List<Map<String, Object>>转为List<String>
			List<String> planList = Lists.transform(replaymentPlanList, new Function<Map<String, Object>, String>() {
				@Override
				public String apply(Map<String, Object> input) {
					return (String)input.get("replaymentPlanId");
				}
			});
			
			if(planList == null || planList.size() == 0) {
				return new ResultVo(false, "还款参数列表为空或者参数格式不正确");
			}
			
			params.put("planList", planList);
			Map<String, Object> resultMap = projectRepaymentRepositoryCustom.queryLatesttRepaymentTotal(params);
			BigDecimal repaymentTotalAmount = new BigDecimal(resultMap.get("repaymentTotalAmount").toString());
			BigDecimal companyAccountAvailableAmount = new BigDecimal(resultMap.get("companyAccountAvailableAmount").toString());
			
			if(repaymentTotalAmount.compareTo(companyAccountAvailableAmount) > 0) {
				return new ResultVo(false, "还款账户余额不足，不允许还款");
			}
			
			// 循环处理还款计划列表
			for(String replaymentPlanId : planList) {
				Map<String, Object> param = Maps.newHashMap();
				param.put("replaymentPlanId", replaymentPlanId);
				param.put("userId", userId);
				ResultVo result = singleRepayment(param);
				if(!ResultVo.isSuccess(result)) {
					return result; 
				}
			}
			
			return new ResultVo(true, "批量还款成功");
		}

		/**
		 * 单笔还款
		 *
		 * @author  wangjf
		 * @date    2016年1月16日 下午2:43:23
		 * @param params
		 * @return
		 * @throws SLException
		 */
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public ResultVo singleRepayment(Map<String, Object> params)
				throws SLException {
			
			// 时间大于晚上10点不允许还款
			if(DateUtils.getCurrentDate("HH:mm:ss").compareTo("22:00:00") > 0){ 
				return new ResultVo(false , "22:00之后不允许还款");
			}
			
			String replaymentPlanId = (String)params.get("replaymentPlanId");
			String userId = (String)params.get("userId");
			
			// 1) 判断还款计划是否存在
			RepaymentPlanInfoEntity repaymentPlanInfoEntity = repaymentPlanInfoRepository.findOne(replaymentPlanId);
			if(repaymentPlanInfoEntity == null) {
				return new ResultVo(false, "还款计划不存在");
			}
			if(Constant.IS_AMOUNT_FROZEN_YES.equals(repaymentPlanInfoEntity.getIsAmountFrozen())) {
				return new ResultVo(false, "该笔还款已做还款冻结，切勿重复操作");
			}
			
			// 2) 公司账户余额>=本次应还？
			ProjectInfoEntity projectInfoEntity = projectInfoRepository.findOne(repaymentPlanInfoEntity.getProjectId());
			if(projectInfoEntity == null 
					|| !Constant.PROJECT_STATUS_08.equals(projectInfoEntity.getProjectStatus())) {
				return new ResultVo(false, "项目不存在或者非还款中状态，不允许做还款");
			}
			
			AccountInfoEntity accountInfoEntity = accountInfoRepository.findByCustId(projectInfoEntity.getCustId());
			
			if(accountInfoEntity.getAccountAvailableAmount().compareTo(repaymentPlanInfoEntity.getRepaymentTotalAmount()) < 0) {
				return new ResultVo(false, "还款账户余额不足，不允许还款");
			}
			
			// 3) 冻结公司账户(主账户->分账户，分账户冻结)
			SubAccountInfoEntity subAccountInfoEntity = subAccountInfoRepository.findByRelatePrimary(projectInfoEntity.getId());
			
			// 主账户->分账户
			String reqeustNo = numberService.generateTradeBatchNumber();
			List<AccountFlowInfoEntity> accountList = custAccountService.updateAccount(accountInfoEntity, null, null, 
					subAccountInfoEntity, "2", SubjectConstant.TRADE_FLOW_TYPE_REPAYMENT_PROJECT, 
					reqeustNo, repaymentPlanInfoEntity.getRepaymentTotalAmount(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_REPAYMENT_PROJECT, 
					Constant.TABLE_BAO_T_REPAYMENT_PLAN_INFO, replaymentPlanId,  
					"财务还款", userId); 
			
			// 分账户冻结
			List<AccountFlowInfoEntity> freeAccountList = custAccountService.updateAccount(null, subAccountInfoEntity, null, 
					null, "11", SubjectConstant.TRADE_FLOW_TYPE_REPAYMENT_FREEZE_PROJECT, 
					reqeustNo, repaymentPlanInfoEntity.getRepaymentTotalAmount(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_REPAYMENT_FREEZE_PROJECT, 
					Constant.TABLE_BAO_T_REPAYMENT_PLAN_INFO, replaymentPlanId,  
					"还款冻结", userId); 
			
			// 4) 更新还款计划财务已还情况
			repaymentPlanInfoEntity.setIsAmountFrozen(Constant.IS_AMOUNT_FROZEN_YES);
			repaymentPlanInfoEntity.setMemo(accountList.get(0).getId() + "|" + freeAccountList.get(0).getTradeNo());// 总账id | 冻结流水号
			repaymentPlanInfoEntity.setBasicModelProperty(userId, false);
			
			// 5) 更新项目(注此处不应更新项目，等真正还款时再更新)
			//updateProjectWithRepayment(projectInfoEntity, repaymentPlanInfoEntity);
			
			// 6) 记录日志
			LogInfoEntity logInfoEntity = new LogInfoEntity();
			logInfoEntity.setRelateType(Constant.TABLE_BAO_T_REPAYMENT_PLAN_INFO);
			logInfoEntity.setRelatePrimary(replaymentPlanId);
			logInfoEntity.setLogType(Constant.OPERATION_TYPE_31);
			logInfoEntity.setOperDesc("");
			logInfoEntity.setOperPerson(userId);
			logInfoEntity.setOperIpaddress((String)params.get("ipAddress"));
			logInfoEntity.setMemo(String.format("项目%s:%s还款冻结%s元", projectInfoEntity.getProjectNo(), projectInfoEntity.getProjectName(), repaymentPlanInfoEntity.getRepaymentTotalAmount().toPlainString()));
			logInfoEntity.setBasicModelProperty(userId, true);
			logInfoEntityRepository.save(logInfoEntity);
			
			return new ResultVo(true, "还款成功");
		}
		
		/**
		 * 还款时更新项目剩余期数、剩余本金、下个还款日、是否到期
		 *
		 * @author  wangjf
		 * @date    2016年1月8日 下午3:01:09
		 * @param projectInfoEntity
		 * @param repaymentPlanInfoEntity
		 */
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public void updateProjectWithRepayment(ProjectInfoEntity projectInfoEntity, RepaymentPlanInfoEntity repaymentPlanInfoEntity) {
			List<RepaymentPlanInfoEntity> planList = projectInfoRepository.findRepaymentByProjectId(projectInfoEntity.getId());
			String repaymentDay = "";
			for(int i = 0; i < planList.size(); i ++) {
				if(repaymentPlanInfoEntity.getId().equals(planList.get(i).getId())) {
					if(i == planList.size() - 1) { // 若是最后一期，则下个还款日不存在
						repaymentDay = "";
					}
					else { // 否则取当期的下个还款日
						repaymentDay = planList.get(i + 1).getExpectRepaymentDate();
					}
					break;
				}
			}
			projectInfoEntity.setRemainderTerms(planList.size() - repaymentPlanInfoEntity.getCurrentTerm() > 0 ? planList.size() - repaymentPlanInfoEntity.getCurrentTerm() : 0);
			projectInfoEntity.setRemainderPrincipal(repaymentPlanInfoEntity.getRemainderPrincipal());
			if(!StringUtils.isEmpty(repaymentDay)) {
				projectInfoEntity.setRepaymentDay(repaymentDay);
			}
			// 若是最后一期，则项目状态改为已到期
			if(repaymentPlanInfoEntity.getId().equals(planList.get(planList.size() - 1).getId())) {
				projectInfoEntity.setProjectStatus(Constant.PROJECT_STATUS_09);
				projectInfoEntity.setFlowStatus(Constant.PROJECT_STATUS_09);
				
				// 投资记录均置为无效
				List<InvestInfoEntity> investList = projectInfoRepository.findInvestByProjectId(projectInfoEntity.getId());
				for(InvestInfoEntity i : investList) {
					i.setInvestStatus(Constant.VALID_STATUS_INVALID);
					i.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
				}
			}
		}

		/**
		 * 逾期还款
		 *
		 * @author  wangjf
		 * @date    2016年1月16日 下午2:43:44
		 * @param params
		 * @return
		 * @throws SLException
		 */
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public ResultVo overdueRepayment(Map<String, Object> params)
				throws SLException {
			String projectId = (String)params.get("projectId");
			String userId = (String)params.get("userId");
			final Date now = new Date();
			List<Map<String, Object>> smsList = Lists.newArrayList();
			
			// 1) 判断项目是否存在及是否逾期
			ProjectInfoEntity projectInfoEntity = projectInfoRepository.findOne(projectId);
			if(projectInfoEntity == null 
					|| !Constant.PROJECT_STATUS_10.equals(projectInfoEntity.getProjectStatus())) {
				return new ResultVo(false, "项目不存在或者非逾期状态，不允许做逾期还款");
			}
			
			// 2) 取所有逾期还款计划数据
			List<RepaymentPlanInfoEntity> allRepaymentPlanList = projectInfoRepository.findRepaymentByProjectIdAndRepaymentStatus(projectId, Constant.REPAYMENT_STATUS_WAIT);
			Collection<RepaymentPlanInfoEntity> tmpPlanList = Collections2.filter(allRepaymentPlanList, new Predicate<RepaymentPlanInfoEntity>() {

				@Override
				public boolean apply(RepaymentPlanInfoEntity input) {
					// 筛选出状态未还款且预期还款日小于等于当天的数据
					if(DateUtils.parseDate(input.getExpectRepaymentDate(), "yyyyMMdd").compareTo(DateUtils.truncateDate(now)) <= 0) {
						return true;
					}
					return false;
				}

			});
			
			// 取符合条件的还款计划
			List<RepaymentPlanInfoEntity> planList = Lists.newArrayList(tmpPlanList.iterator());
			if(planList.size() == 0) {
				return new ResultVo(false, "项目不存在逾期待还的还款计划");
			}

			// 3) 计算本期应还
			// 逾期天数 = 当前日期 - 应还日期
			RepaymentPlanInfoEntity firstPlan = planList.get(0);
			int overdueDays = DateUtils.datePhaseDiffer(DateUtils.parseDate(firstPlan.getExpectRepaymentDate(), "yyyyMMdd"), now);
			
			// 逾期费用 = 剩余本金 * 逾期管理费率 * 逾期天数
			BigDecimal overdueRate = paramService.findOverdueRate();
			BigDecimal overdueExpense = ArithUtil.mul(ArithUtil.mul(ArithUtil.add(firstPlan.getRemainderPrincipal(), firstPlan.getRepaymentPrincipal()), overdueRate), new BigDecimal(String.valueOf(overdueDays)));
			overdueExpense = ArithUtil.formatScale2(overdueExpense);
			
			// 初始化本期应还、风险金垫付本金总额、风险金垫付利息总额、风险金垫付管理费总额、风险金垫付罚息总额
			//     应还用户本金总额、应还用户利息总额、应还公司管理费总额、应还用户罚息、应还公司罚息
			BigDecimal currRepayAmount = BigDecimal.ZERO; // 本期应还
			BigDecimal totalPrincipalRisk = BigDecimal.ZERO;// 风险金垫付本金总额
			BigDecimal totalInterestRisk = BigDecimal.ZERO;// 风险金垫付利息总额
			BigDecimal totalExpenseRisk = BigDecimal.ZERO;// 风险金垫付管理费总额
			BigDecimal totalPenaltyAmountRisk = BigDecimal.ZERO; // 风险金垫付罚息总额
			BigDecimal totalPrincipalUser = BigDecimal.ZERO;// 应还用户本金总额
			BigDecimal totalInterestUser = BigDecimal.ZERO;// 应还用户利息总额
			BigDecimal totalExpenseUser = BigDecimal.ZERO;// 应还公司管理费总额
			BigDecimal totalPenaltyAmountUser = BigDecimal.ZERO; // 应还用户罚息
			BigDecimal totalPenaltyAmountCompany = BigDecimal.ZERO; // 应还公司罚息
			
			for(int i = 0; i < planList.size(); i ++) {
				if(Constant.IS_RISKAMOUNT_REPAY_YES.equals(planList.get(i).getIsRiskamountRepay())) { // 已垫付
					totalPrincipalRisk = ArithUtil.add(planList.get(i).getRepaymentPrincipal(), totalPrincipalRisk);
					totalInterestRisk = ArithUtil.add(planList.get(i).getRepaymentInterest(), totalInterestRisk);
					totalExpenseRisk = ArithUtil.add(planList.get(i).getAccountManageExpense(), totalExpenseRisk);
					totalPenaltyAmountRisk = ArithUtil.add(totalPenaltyAmountRisk, 
							ArithUtil.sub(planList.get(i).getRiskRepayAmount(), planList.get(i).getRepaymentTotalAmount()));
				}
				else {
					totalPrincipalUser = ArithUtil.add(planList.get(i).getRepaymentPrincipal(), totalPrincipalUser);
					totalInterestUser = ArithUtil.add(planList.get(i).getRepaymentInterest(), totalInterestUser);
					totalExpenseUser = ArithUtil.add(planList.get(i).getAccountManageExpense(), totalExpenseUser);
					if(totalPenaltyAmountUser.compareTo(BigDecimal.ZERO) == 0) {
						int tmpOverdueDays = DateUtils.datePhaseDiffer(DateUtils.parseDate(planList.get(i).getExpectRepaymentDate(), "yyyyMMdd"), now);
						totalPenaltyAmountUser = ArithUtil.add(totalPenaltyAmountUser, 
								ArithUtil.mul(ArithUtil.mul(ArithUtil.add(planList.get(i).getRemainderPrincipal(), planList.get(i).getRepaymentPrincipal()), overdueRate), new BigDecimal(String.valueOf(tmpOverdueDays))));
						totalPenaltyAmountUser = ArithUtil.formatScale2(totalPenaltyAmountUser);
					}
				}
			}
					
			// 本次应还(所有逾期本金+所有逾期利息+所有逾期账户管理费+逾期费用)
			currRepayAmount = ArithUtil.add(currRepayAmount, totalPrincipalRisk);
			currRepayAmount = ArithUtil.add(currRepayAmount, totalInterestRisk);
			currRepayAmount = ArithUtil.add(currRepayAmount, totalExpenseRisk);
			currRepayAmount = ArithUtil.add(currRepayAmount, totalPrincipalUser);
			currRepayAmount = ArithUtil.add(currRepayAmount, totalInterestUser);
			currRepayAmount = ArithUtil.add(currRepayAmount, totalExpenseUser);
			currRepayAmount = ArithUtil.add(currRepayAmount, overdueExpense);
					
			// 应还公司罚息 = 总罚息 - 风险金垫付罚息 - 用户罚息
			totalPenaltyAmountCompany = ArithUtil.sub(ArithUtil.sub(overdueExpense, totalPenaltyAmountRisk), totalPenaltyAmountUser);
			
			// 4) 判断公司账户余额<本期应还
			AccountInfoEntity accountInfoEntity = accountInfoRepository.findByCustId(projectInfoEntity.getCustId());		
			if(accountInfoEntity.getAccountAvailableAmount().compareTo(currRepayAmount) < 0) {
				return new ResultVo(false, "还款账户余额不足，不允许还款");
			}
			
			// 5) 给风险金、用户、公司还款
			
			// 项目主账户->项目分账户
			String reqeustNo = numberService.generateTradeBatchNumber();
			SubAccountInfoEntity subAccountInfoEntity = subAccountInfoRepository.findByRelatePrimary(projectInfoEntity.getId());
			List<AccountFlowInfoEntity> accountList = custAccountService.updateAccount(accountInfoEntity, null, null, 
					subAccountInfoEntity, "2", SubjectConstant.TRADE_FLOW_TYPE_OVEDUE_REPAYMENT_PROJECT, 
					reqeustNo, currRepayAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_OVERDUE_REPAYMENT_PROJECT, 
					Constant.TABLE_BAO_T_PROJECT_INFO, projectInfoEntity.getId(),  
					"逾期还款", userId); 
			
			//保存借款人还款记录
			saveRepaymentRecord(currRepayAmount, projectInfoEntity.getId(), SubjectConstant.TRADE_FLOW_TYPE_OVEDUE_REPAYMENT_PROJECT,
					accountList.get(0).getId(), planList, Constant.REPAYMENT_TYPE_02, overdueExpense);
			
			//    5-1) 还款风险金
			if(totalPrincipalRisk.compareTo(BigDecimal.ZERO) > 0) { // 还利息、本金、管理费
				AccountInfoEntity accountRisk = accountInfoRepository.findOne(Constant.ACCOUNT_ID_PROJECT_RISK);
				
				// 账户更新及记录流水(还本金 + 利息 + 账户管理费 + 罚息)
				List<AccountFlowInfoEntity> riskAccountFlowList = custAccountService.updateAccount(null, subAccountInfoEntity, accountRisk, 
						null, "3", SubjectConstant.TRADE_FLOW_TYPE_RISK_REPAY_PROJECT, 
						reqeustNo, ArithUtil.add(ArithUtil.add(ArithUtil.add(totalPrincipalRisk, totalInterestRisk), totalExpenseRisk), totalPenaltyAmountRisk),
						SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_RISK_REPAY_PROJECT, 
						Constant.TABLE_BAO_T_PROJECT_INFO, projectInfoEntity.getId(),  
						"逾期还款", userId);
				
				// 保存用户付款明细
				projectPaymentService.saveRiskPaymentRecord(projectId, accountRisk.getCustId(), riskAccountFlowList.get(0).getId(), 
						totalPrincipalRisk, totalInterestRisk, totalPenaltyAmountRisk, totalExpenseRisk);
			}

			//    5-2) 还款投资用户
			if(totalPrincipalUser.compareTo(BigDecimal.ZERO) > 0) {
				repaymentForUser(projectInfoEntity, totalPrincipalUser, totalInterestUser, totalPenaltyAmountUser, 
						subAccountInfoEntity, reqeustNo, userId, "逾期还款", Constant.REPAYMENT_TYPE_02,
						projectInfoEntity.getProjectNo(), planList, smsList);
			}

			//    5-3) 还公司
			if(totalExpenseUser.compareTo(BigDecimal.ZERO) > 0 
					|| totalPenaltyAmountCompany.compareTo(BigDecimal.ZERO) > 0) { // 还管理费
				
				// 公司收益账户
				AccountInfoEntity earnMainAccount = accountInfoRepository.findOne(Constant.ACCOUNT_ID_PROJECT_ERAN);
				// 账户更新及记录流水(账户管理费 + 罚息)
				List<AccountFlowInfoEntity> earnMainAccountFlowList = custAccountService.updateAccount(null, subAccountInfoEntity, earnMainAccount, 
						null, "3", SubjectConstant.TRADE_FLOW_TYPE_COMPANY_REPAYMENT_ALL_PROJECT, 
						reqeustNo, ArithUtil.add(totalExpenseUser, totalPenaltyAmountCompany), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMPANY_REPAYMENT_ALL_PROJECT, 
						Constant.TABLE_BAO_T_PROJECT_INFO, projectInfoEntity.getId(),  
						"逾期还款", userId);
				// 保存用户付款明细
				projectPaymentService.saveCompanyPaymentRecord(projectId, earnMainAccount.getCustId(), earnMainAccountFlowList.get(0).getId(), 
						totalPenaltyAmountCompany, totalExpenseUser, Constant.REPAYMENT_TYPE_02);
			}
			
			// 6) 循环处理逾期还款计划
			for(RepaymentPlanInfoEntity p : planList) {
				p.setTermAlreadyRepayAmount(p.getRepaymentTotalAmount());
				p.setRepaymentStatus(Constant.REPAYMENT_STATUS_CLEAN);
				p.setFactRepaymentDate(new Timestamp(System.currentTimeMillis()));
				p.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);			
			}
		
			// 7) 更新项目状态为正常
			projectInfoEntity.setProjectStatus(Constant.PROJECT_STATUS_08);
			projectInfoEntity.setFlowStatus(Constant.PROJECT_STATUS_08);
			projectInfoEntity.setBasicModelProperty(userId, false);
			
			// 9) 更新项目
			updateProjectWithRepayment(projectInfoEntity, (RepaymentPlanInfoEntity)planList.toArray()[planList.size() - 1]);
			
			// 10) 记录日志
			LogInfoEntity logInfoEntity = new LogInfoEntity();
			logInfoEntity.setRelateType(Constant.TABLE_BAO_T_PROJECT_INFO);
			logInfoEntity.setRelatePrimary(projectInfoEntity.getId());
			logInfoEntity.setLogType(Constant.OPERATION_TYPE_32);
			logInfoEntity.setOperBeforeContent(Constant.PROJECT_STATUS_10);
			logInfoEntity.setOperAfterContent(Constant.PROJECT_STATUS_08);
			logInfoEntity.setOperDesc("");
			logInfoEntity.setOperPerson(userId);
			logInfoEntity.setOperIpaddress((String)params.get("ipAddress"));
			logInfoEntity.setMemo(String.format("项目%s:%s逾期还款%s元", projectInfoEntity.getProjectNo(), projectInfoEntity.getProjectName(), currRepayAmount.toPlainString()));
			logInfoEntity.setBasicModelProperty(userId, true);
			logInfoEntityRepository.save(logInfoEntity);

			return new ResultVo(true, "逾期还款成功", smsList);
		}
		
		/**
		 * 通过投资查询用户账户
		 *
		 * @author  wangjf
		 * @date    2016年1月8日 上午11:58:44
		 * @param accountList
		 * @param invest
		 * @return
		 * @throws SLException
		 */
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public AccountInfoEntity findUserAccountByInvest(final List<AccountInfoEntity> accountList, final String custId) throws SLException{
					
			AccountInfoEntity userAccount = null;
			for(AccountInfoEntity account : accountList) {
				if(account.getCustId().equals(custId)) {
					userAccount = account;
					break;
				}
			}
			
			if(userAccount == null) {
				throw new SLException("未找到投资人账户");
			}
			
			return userAccount;
		}
		
		/**
		 * 查询奖励利率
		 *
		 * @author  wangjf
		 * @date    2016年1月19日 下午3:16:51
		 * @param projectInfoEntity
		 * @param planList
		 * @return
		 */
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public BigDecimal findAwardRate(ProjectInfoEntity projectInfoEntity, List<RepaymentPlanInfoEntity> planList) {
			BigDecimal awardRate = BigDecimal.ZERO;
			// 最后一期给用户奖励			
			if(BigDecimal.ZERO.equals(planList.get(planList.size() - 1).getRemainderPrincipal())) {
				// 奖励 = 奖励利率/12×期数
				awardRate = ArithUtil.mul(ArithUtil.div(projectInfoEntity.getAwardRate(), new BigDecimal("12")), new BigDecimal(projectInfoEntity.getTypeTerm().toString()));
			}
			
			return awardRate;
		}
		

		/**
		 * 提前结清
		 *
		 * @author  wangjf
		 * @date    2016年1月16日 下午2:44:34
		 * @param params
		 * @return
		 * @throws SLException
		 */
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public ResultVo earlyRepayment(Map<String, Object> params)
				throws SLException {
			
			String projectId = (String)params.get("projectId");
			String userId = (String)params.get("userId");
			List<Map<String, Object>> smsList = Lists.newArrayList();
			
			// 1) 判断项目是否逾期，逾期不能提前结清
			ProjectInfoEntity projectInfoEntity = projectInfoRepository.findOne(projectId);
			if(projectInfoEntity == null) {
				return new ResultVo(false, "项目不存在");
			}
			if(!Constant.PROJECT_STATUS_08.equals(projectInfoEntity.getProjectStatus())) {
				return new ResultVo(false, String.format("项目状态为%s(仅有状态为还款中的允许提前结清)，不允许提前结清", projectInfoEntity.getProjectStatus()));
			}
			
			// 判断还款计划是否存在
			List<RepaymentPlanInfoEntity> planList = projectInfoRepository.findRepaymentByProjectIdAndRepaymentStatus(projectId, Constant.REPAYMENT_STATUS_WAIT);
			if(planList == null || planList.size() == 0) {
				return new ResultVo(false, "未找符合条件的还款计划");
			}
			
			// 2) 计算提前结清金额
			params.put("projectId", projectId);
			params.put("start", 0);
			params.put("length", Integer.MAX_VALUE);
			Page<Map<String, Object>> page = projectRepaymentRepositoryCustom.queryAllRepaymentListNew(params);
			if(page.getTotalElements() == 0) {
				return new ResultVo(false, "未找到符合条件的提前结清数据");
			}
			
			Map<String, Object> earlyExpenseMap = page.getContent().get(0);
			if(Constant.IS_AMOUNT_FROZEN_YES.equals((String)earlyExpenseMap.get("isAmountFrozen"))) {
				return new ResultVo(false, "存在一笔还款冻结未处理，暂时无法提前结清");
			}
			// 剩余本金
			BigDecimal remainderPrincipal = new BigDecimal(earlyExpenseMap.get("remainderPrincipal").toString());
			// 违约金
			BigDecimal earlyPenaltyAmount = new BigDecimal(earlyExpenseMap.get("earlyPenaltyAmount").toString());
//			// 应付利息
//			BigDecimal earlyInterest = new BigDecimal(earlyExpenseMap.get("earlyInterest").toString());
//			// 应付账户管理费
//			BigDecimal earlyManageExpense = new BigDecimal(earlyExpenseMap.get("earlyManageExpense").toString());
//			// 提前结清手续费
//			BigDecimal earlyPenaltyAmount = new BigDecimal(earlyExpenseMap.get("earlyRepaymentExpense").toString());
//			// 应还总额 = 剩余本金+应付利息+应付管理费+提前结清手续费
//			BigDecimal repaymentTotalAmount = BigDecimal.ZERO;
//			repaymentTotalAmount = ArithUtil.add(repaymentTotalAmount, remainderPrincipal);
//			repaymentTotalAmount = ArithUtil.add(repaymentTotalAmount, earlyInterest);
//			repaymentTotalAmount = ArithUtil.add(repaymentTotalAmount, earlyManageExpense);
//			repaymentTotalAmount = ArithUtil.add(repaymentTotalAmount, earlyPenaltyAmount);
			// 应还总额 
			BigDecimal repaymentTotalAmount = new BigDecimal(earlyExpenseMap.get("earlyRepaymentExpense").toString());
			
			// 3) 判断公司余额是否大于等于提前结清金额
			AccountInfoEntity accountInfoEntity = accountInfoRepository.findByCustId(projectInfoEntity.getCustId());		
			if(accountInfoEntity.getAccountAvailableAmount().compareTo(repaymentTotalAmount) < 0) {
				return new ResultVo(false, "还款账户余额不足，不允许还款");
			}
			
			// 4) 提前结清
			// 项目主账户->项目分账户
			String reqeustNo = numberService.generateTradeBatchNumber();
			SubAccountInfoEntity subAccountInfoEntity = subAccountInfoRepository.findByRelatePrimary(projectInfoEntity.getId());
			List<AccountFlowInfoEntity> accountList = custAccountService.updateAccount(accountInfoEntity, null, null, 
					subAccountInfoEntity, "2", SubjectConstant.TRADE_FLOW_TYPE_EARLY_REPAYMENT_PROJECT, 
					reqeustNo, repaymentTotalAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_EARLY_REPAYMENT_PROJECT, 
					Constant.TABLE_BAO_T_PROJECT_INFO, projectInfoEntity.getId(),  
					"提前还款", userId); 
			
//			// 还款给公司收益账户
//			AccountInfoEntity earnMainAccount = accountInfoRepository.findOne(Constant.ACCOUNT_ID_PROJECT_ERAN);
//			// 还账户管理费(公司)
//			custAccountService.updateAccount(null, subAccountInfoEntity, earnMainAccount, 
//					null, "3", SubjectConstant.TRADE_FLOW_TYPE_COMPANY_REPAYMENT_EXPENSE_PROJECT, 
//					reqeustNo, earlyManageExpense, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMPANY_REPAYMENT_EXPENSE_PROJECT, 
//					Constant.TABLE_BAO_T_PROJECT_INFO, projectInfoEntity.getId(),  
//					"提前还款", userId);
			
			// 5) 还款计划更新
			for(RepaymentPlanInfoEntity p : planList) {
				p.setTermAlreadyRepayAmount(p.getRepaymentTotalAmount());
				p.setRepaymentStatus(Constant.REPAYMENT_STATUS_CLEAN);
				p.setFactRepaymentDate(new Timestamp(System.currentTimeMillis()));
				p.setBasicModelProperty(userId, false);
			}
			
			//保存借款人还款记录
			saveRepaymentRecord(repaymentTotalAmount, projectInfoEntity.getId(), SubjectConstant.TRADE_FLOW_TYPE_EARLY_REPAYMENT_PROJECT,
					accountList.get(0).getId(), planList, Constant.REPAYMENT_TYPE_03, earlyPenaltyAmount);
			
			// 到期还本付息(到期还本付息的时候提前结清金额=本金+利息，其余情况为剩余本金+违约金)
			if(Constant.REPAYMENT_METHOD_03.equals(projectInfoEntity.getRepaymnetMethod())) {
				// 利息
				BigDecimal repaymentInterest = new BigDecimal(earlyExpenseMap.get("repaymentInterest").toString());
				BigDecimal accountManageExpense = new BigDecimal(earlyExpenseMap.get("accountManageExpense").toString());

				// 还款给投资人
				repaymentForUser(projectInfoEntity, remainderPrincipal, repaymentInterest, BigDecimal.ZERO, 
						subAccountInfoEntity, reqeustNo, userId, "提前还款", Constant.REPAYMENT_TYPE_03,
						projectInfoEntity.getProjectNo(), planList, smsList);
				
				// 还款给公司收益账户
				AccountInfoEntity earnMainAccount = accountInfoRepository.findOne(Constant.ACCOUNT_ID_PROJECT_ERAN);
				// 还账户管理费(公司)
				List<AccountFlowInfoEntity> earnMainAccountFlowList = custAccountService.updateAccount(null, subAccountInfoEntity, earnMainAccount, 
						null, "3", SubjectConstant.TRADE_FLOW_TYPE_COMPANY_REPAYMENT_ALL_PROJECT, 
						reqeustNo, accountManageExpense, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMPANY_REPAYMENT_ALL_PROJECT, 
						Constant.TABLE_BAO_T_PROJECT_INFO, projectInfoEntity.getId(),  
						"提前还款", userId);
				
				// 保存用户付款明细
				projectPaymentService.saveCompanyPaymentRecord(projectId, earnMainAccount.getCustId(), earnMainAccountFlowList.get(0).getId(), 
						BigDecimal.ZERO, accountManageExpense, Constant.REPAYMENT_TYPE_03);
			}
			else {
				// 还款给投资人
				repaymentForUser(projectInfoEntity, remainderPrincipal, BigDecimal.ZERO, earlyPenaltyAmount, 
						subAccountInfoEntity, reqeustNo, userId, "提前还款", Constant.REPAYMENT_TYPE_03,
						projectInfoEntity.getProjectNo(), planList, smsList);
			}

			// 6) 项目更新
			updateProjectWithRepayment(projectInfoEntity, planList.get(planList.size() - 1));
			projectInfoEntity.setProjectStatus(Constant.PROJECT_STATUS_11);
			projectInfoEntity.setFlowStatus(Constant.PROJECT_STATUS_11);
			
			// 7) 记录日志
			LogInfoEntity logInfoEntity = new LogInfoEntity();
			logInfoEntity.setRelateType(Constant.TABLE_BAO_T_PROJECT_INFO);
			logInfoEntity.setRelatePrimary(projectInfoEntity.getId());
			logInfoEntity.setLogType(Constant.OPERATION_TYPE_34);
			logInfoEntity.setOperBeforeContent(Constant.PROJECT_STATUS_08);
			logInfoEntity.setOperAfterContent(Constant.PROJECT_STATUS_11);
			logInfoEntity.setOperDesc("");
			logInfoEntity.setOperPerson(userId);
			logInfoEntity.setOperIpaddress((String)params.get("ipAddress"));
			logInfoEntity.setMemo(String.format("项目%s:%s提前还款%s元", projectInfoEntity.getProjectNo(), projectInfoEntity.getProjectName(), repaymentTotalAmount.toPlainString()));
			logInfoEntity.setBasicModelProperty(userId, true);
			logInfoEntityRepository.save(logInfoEntity);
			
			return new ResultVo(true, "提前还款成功", smsList);
		}
		
		/**
		 * 给投资人还款
		 *
		 * @author  wangjf
		 * @date    2016年1月11日 下午2:34:08
		 * @param projectId
		 * @param totalPrincipal
		 * @param totalInterest
		 * @param totalPenalty
		 * @param subAccountInfoEntity
		 * @param reqeustNo
		 * @param userId
		 * @param memo
		 * @param projectStatus
		 * @throws SLException
		 */
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public void repaymentForUser(ProjectInfoEntity projectInfoEntity, BigDecimal totalPrincipal, BigDecimal totalInterest,
				BigDecimal totalPenalty, SubAccountInfoEntity subAccountInfoEntity,
				String reqeustNo, String userId, String memo, String repaymentType,
				String projectNo, List<RepaymentPlanInfoEntity> planList,
				List<Map<String, Object>> smsList) throws SLException{
			
			String projectId = projectInfoEntity.getId();			
			BigDecimal awardRate = projectInfoEntity.getAwardRate();
			int typeTerm = projectInfoEntity.getTypeTerm();
			// 判断是否是最后一期(剩余本金为0表示最后一期)
			boolean isLastTerm = BigDecimal.ZERO.equals(planList.get(planList.size() - 1).getRemainderPrincipal());
			
			Map<String, Object> params = Maps.newHashMap();
			params.put("start", 0);
			params.put("length", Integer.MAX_VALUE);
			params.put("projectId", projectId);
			Page<Map<String, Object>> page = projectInfoRepositoryCustom.queryProjectJoinList(params);
			List<AccountInfoEntity> accountList =  projectInfoRepository.findAccountByProjectId(projectId);
			ProjectInvestInfoEntity projectInvest = projectInvestInfoRepository.findByProjectId(projectId);			
			BigDecimal tmpTotalPrincipalAmount = BigDecimal.ZERO;
			BigDecimal tmpTotalInterestAmount = BigDecimal.ZERO;
			BigDecimal tmpTotalPenaltyAmount = BigDecimal.ZERO;
			
			List<Map<String, Object>> investList = page.getContent();
			for(int i = 0; i < investList.size(); i ++) {
				
				Map<String, Object> invest = investList.get(i);
				// 用户投资比例 = 投资金额/项目总价 
				//BigDecimal investScale = ArithUtil.div(new BigDecimal(invest.get("tradeAmount").toString()), projectInvest.getAlreadyInvestAmount());
				double tradeAmount = Double.valueOf(invest.get("tradeAmount").toString());
				// 奖励金额 = 投资金额*奖励利率/12×期数
				//BigDecimal awardAmount = ArithUtil.mul(new BigDecimal(invest.get("tradeAmount").toString()), awardRate);
				double dAwardAmount = isLastTerm ? tradeAmount*awardRate.doubleValue()/12*typeTerm : 0;
				BigDecimal awardAmount = new BigDecimal(String.valueOf(dAwardAmount));
				// 通过投资找到用户账户
				AccountInfoEntity userAccount = findUserAccountByInvest(accountList, invest.get("custId").toString());
				// 用户应得本金、利息、违约金
				double dPrincipalAmount = totalPrincipal.doubleValue()*tradeAmount/projectInvest.getAlreadyInvestAmount().doubleValue();
				double dInterestAmount = totalInterest.doubleValue()*tradeAmount/projectInvest.getAlreadyInvestAmount().doubleValue();
				double dPenaltyAmount = totalPenalty.doubleValue()*tradeAmount/projectInvest.getAlreadyInvestAmount().doubleValue();
				BigDecimal principalAmount = new BigDecimal(String.valueOf(dPrincipalAmount));
				BigDecimal interestAmount = new BigDecimal(String.valueOf(dInterestAmount));
				BigDecimal penaltyAmount = new BigDecimal(String.valueOf(dPenaltyAmount));
				//BigDecimal principalAmount = ArithUtil.mul(totalPrincipal, investScale);
				//BigDecimal interestAmount = ArithUtil.mul(totalInterest, investScale);
				//BigDecimal penaltyAmount = ArithUtil.mul(totalPenalty, investScale);
				
				principalAmount = ArithUtil.formatScale(principalAmount, 8);
				interestAmount = ArithUtil.formatScale(interestAmount, 8);
				penaltyAmount = ArithUtil.formatScale(penaltyAmount, 8);
				awardAmount = ArithUtil.formatScale(awardAmount, 8);
				
				if (i != investList.size() - 1) { // 非最后一条记录
					tmpTotalPrincipalAmount = ArithUtil.add(tmpTotalPrincipalAmount, principalAmount);
					tmpTotalInterestAmount = ArithUtil.add(tmpTotalInterestAmount, interestAmount);
					tmpTotalPenaltyAmount = ArithUtil.add(tmpTotalPenaltyAmount, penaltyAmount);
				}
				else { // 最后一个投资人所得 = 总额-前面所有人总额
					principalAmount = ArithUtil.sub(totalPrincipal, tmpTotalPrincipalAmount);
					interestAmount = ArithUtil.sub(totalInterest, tmpTotalInterestAmount);
					penaltyAmount = ArithUtil.sub(totalPenalty, tmpTotalPenaltyAmount);
				}
							
				// 账户更新及记录流水
				BigDecimal totalAmount = ArithUtil.add(ArithUtil.add(principalAmount, interestAmount), penaltyAmount);
				List<AccountFlowInfoEntity> userAccountFlowList = custAccountService.updateAccount(null, subAccountInfoEntity, userAccount, 
						null, "3", SubjectConstant.TRADE_FLOW_TYPE_USER_REPAYMENT_ALL_PROJECT, 
						reqeustNo, totalAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_ALL_PROJECT, 
						Constant.TABLE_BAO_T_PROJECT_INFO, projectId,  
						memo, userId);
				
				// 保存用户付款明细
				projectPaymentService.saveUserPaymentRecord(projectId, userAccount.getCustId(), userAccountFlowList.get(0).getId(), 
						principalAmount, interestAmount, penaltyAmount, BigDecimal.ZERO, repaymentType, SubjectConstant.TRADE_FLOW_TYPE_USER_REPAYMENT_ALL_PROJECT);
				
				if(awardAmount.compareTo(BigDecimal.ZERO) > 0) { // 奖励金额大于0，给用户奖励
					
					// 公司收益账户
					AccountInfoEntity earnMainAccount = accountInfoRepository.findOne(Constant.ACCOUNT_ID_PROJECT_ERAN);
					// 公司出奖励收益
					List<AccountFlowInfoEntity> earnMainAccountFlowList = custAccountService.updateAccount(earnMainAccount, null, userAccount, 
							null, "1", SubjectConstant.TRADE_FLOW_TYPE_USER_REPAYMENT_AWARD_PROJECT, 
							reqeustNo, awardAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_AWARD_PROJECT, 
							Constant.TABLE_BAO_T_PROJECT_INFO, projectId,  
							memo, userId);		
					
					// 保存付款信息
					saveRepaymentRecord(awardAmount, projectId, SubjectConstant.TRADE_FLOW_TYPE_USER_REPAYMENT_AWARD_PROJECT,
							earnMainAccountFlowList.get(0).getId(), planList, "", BigDecimal.ZERO);
					
					// 保存用户付款明细
					projectPaymentService.saveUserPaymentRecord(projectId, userAccount.getCustId(), earnMainAccountFlowList.get(1).getId(), 
							BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, awardAmount, "", SubjectConstant.TRADE_FLOW_TYPE_USER_REPAYMENT_AWARD_PROJECT);
					
					// 总金额加上奖励金额
					totalAmount = ArithUtil.add(totalAmount, awardAmount);
				}
				
				// 准备短信内容
				switch(repaymentType) {
				case Constant.REPAYMENT_TYPE_01: // 正常还款
				case Constant.REPAYMENT_TYPE_02: // 逾期还款
				case Constant.REPAYMENT_TYPE_04: // 风险金垫付
					if(isLastTerm) { 
						// 发送已到期短信
						Map<String, Object> smsParams = new HashMap<String, Object>();
						smsParams.put("mobile", invest.get("mobile"));
						smsParams.put("custId", invest.get("custId"));	
						smsParams.put("messageType", Constant.SMS_TYPE_PROJECT_FINAL_REPAYMENT);
						smsParams.put("values", new Object[] { // 短信息内容
								DateUtils.formatDate((Date)invest.get("tradeDate"), "yyyy-MM-dd"),
								projectNo,
								ArithUtil.formatScale(totalAmount, 2).toPlainString()});
						smsParams.put("systemMessage", new Object[] { // 站内信内容
								DateUtils.formatDate((Date)invest.get("tradeDate"), "yyyy-MM-dd"),
								projectNo,
								ArithUtil.formatScale(totalAmount, 2).toPlainString()});
						smsList.add(smsParams);
					}
					else {
						// 发送回款短信
						String term = ""; // 取得期数，多期时使用逗号隔开
						for(int j = 0; j < planList.size(); j ++) {
							if(!Constant.IS_RISKAMOUNT_REPAY_YES.equals(planList.get(j).getIsRiskamountRepay())) {
								if(term.isEmpty()) {
									term += planList.get(j).getCurrentTerm().toString();
								}
								else {
									term += "," + planList.get(j).getCurrentTerm().toString();
								}
							}
						}
						Map<String, Object> smsParams = new HashMap<String, Object>();
						smsParams.put("mobile", invest.get("mobile"));
						smsParams.put("custId", invest.get("custId"));	
						smsParams.put("messageType", Constant.SMS_TYPE_PROJECT_NORMAL_REPAYMENT);
						smsParams.put("values", new Object[] { // 短信息内容
								projectNo,
								term,								
								ArithUtil.formatScale(totalAmount, 2).toPlainString()});
						smsParams.put("systemMessage", new Object[] { // 站内信内容
								projectNo,
								term,
								ArithUtil.formatScale(totalAmount, 2).toPlainString()});
						smsList.add(smsParams);
					}
					break;
				case Constant.REPAYMENT_TYPE_03: // 提前还款
				{
					Map<String, Object> smsParams = new HashMap<String, Object>();
					smsParams.put("mobile", invest.get("mobile"));
					smsParams.put("custId", invest.get("custId"));	
					smsParams.put("messageType", Constant.SMS_TYPE_PROJECT_EARLY_REPAYMENT);
					smsParams.put("values", new Object[] { // 短信息内容
							DateUtils.formatDate((Date)invest.get("tradeDate"), "yyyy-MM-dd"),
							projectNo,
							ArithUtil.formatScale(totalAmount, 2).toPlainString()});
					smsParams.put("systemMessage", new Object[] { // 站内信内容
							DateUtils.formatDate((Date)invest.get("tradeDate"), "yyyy-MM-dd"),
							projectNo,
							ArithUtil.formatScale(totalAmount, 2).toPlainString()});
					smsList.add(smsParams);
					break;
				}
				}
				
			}
		}

		/**
		 * 正常还款
		 *
		 * @author  wangjf
		 * @date    2016年1月16日 下午2:44:50
		 * @param params
		 * @return
		 * @throws SLException
		 */
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public ResultVo normalRepayment(Map<String, Object> params)
				throws SLException {
			
			String replaymentPlanId = (String)params.get("replaymentPlanId");
			String userId = (String)params.get("userId");
			List<Map<String, Object>> smsList = Lists.newArrayList();
			
			// 1) 判断还款计划是否存在
			RepaymentPlanInfoEntity repaymentPlanInfoEntity = repaymentPlanInfoRepository.findOne(replaymentPlanId);
			if(repaymentPlanInfoEntity == null) {
				return new ResultVo(false, "还款计划不存在");
			}
			if(Constant.REPAYMENT_STATUS_CLEAN.equals(repaymentPlanInfoEntity.getRepaymentStatus())) {
				return new ResultVo(false, "该笔还款计划已经还款，切勿重复操作");
			}
			
			// 2) 判断项目是否是正常状态
			ProjectInfoEntity projectInfoEntity = projectInfoRepository.findOne(repaymentPlanInfoEntity.getProjectId());
			if(projectInfoEntity == null 
					|| !Constant.PROJECT_STATUS_08.equals(projectInfoEntity.getProjectStatus())) {
				return new ResultVo(false, "项目不存在或者非还款中状态，不允许做还款");
			}
			
			// 3) 判断是否已经还款冻结
			String reqeustNo = numberService.generateTradeBatchNumber();
			SubAccountInfoEntity subAccountInfoEntity = subAccountInfoRepository.findByRelatePrimary(projectInfoEntity.getId());
			List<AccountFlowInfoEntity> accountList = Lists.newArrayList();
			if(Constant.IS_AMOUNT_FROZEN_YES.equals(repaymentPlanInfoEntity.getIsAmountFrozen())) { // 已经还款冻结
				
				// 对分账户进行解冻
				List<AccountFlowInfoEntity> unFreezeAccountList = custAccountService.updateAccount(null, subAccountInfoEntity, null, 
						null, "12", SubjectConstant.TRADE_FLOW_TYPE_REPAYMENT_UNFREEZE_PROJECT, 
						reqeustNo, repaymentPlanInfoEntity.getRepaymentTotalAmount(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_REPAYMENT_UNFREEZE_PROJECT, 
						Constant.TABLE_BAO_T_REPAYMENT_PLAN_INFO, replaymentPlanId,  
						"还款解冻", userId); 
				
				// 还款计划备注中存储的[原主账流水|冻结流水号], 此处取出后续处理
				String[] values = Strings.nullToEmpty(repaymentPlanInfoEntity.getMemo()).split("\\|");
				if(values == null || values.length != 2) {
					throw new SLException("还款未冻结成功，数据异常");
				}
				unFreezeAccountList.get(0).setOldTradeNo(values[1]);
				// 创建主账户流水对象，保存借款人还款记录使用
				AccountFlowInfoEntity accountFlowInfoEntity = new AccountFlowInfoEntity();
				accountFlowInfoEntity.setId(values[0]);
				accountList.add(accountFlowInfoEntity);
			}
			else { // 未还款冻结
				AccountInfoEntity accountInfoEntity = accountInfoRepository.findByCustId(projectInfoEntity.getCustId());		
				if(accountInfoEntity.getAccountAvailableAmount().compareTo(repaymentPlanInfoEntity.getRepaymentTotalAmount()) < 0) {
					return new ResultVo(false, "还款账户余额不足，不允许还款");
				}
				
				// 公司主账户->分账户
				accountList = custAccountService.updateAccount(accountInfoEntity, null, null, 
						subAccountInfoEntity, "2", SubjectConstant.TRADE_FLOW_TYPE_REPAYMENT_PROJECT, 
						reqeustNo, repaymentPlanInfoEntity.getRepaymentTotalAmount(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_REPAYMENT_PROJECT, 
						Constant.TABLE_BAO_T_REPAYMENT_PLAN_INFO, replaymentPlanId,  
						"正常还款", userId); 
			}
			
			// 保存借款人还款记录
			saveRepaymentRecord(repaymentPlanInfoEntity.getRepaymentTotalAmount(), projectInfoEntity.getId(), SubjectConstant.TRADE_FLOW_TYPE_REPAYMENT_PROJECT,
					accountList.get(0).getId(), Arrays.asList(repaymentPlanInfoEntity), Constant.REPAYMENT_TYPE_02, BigDecimal.ZERO);
				
			// 4) 给投资人和公司还款
			// 还款给投资人
			repaymentForUser(projectInfoEntity, repaymentPlanInfoEntity.getRepaymentPrincipal(), repaymentPlanInfoEntity.getRepaymentInterest(), BigDecimal.ZERO, 
					subAccountInfoEntity, reqeustNo, userId, "正常还款", Constant.REPAYMENT_TYPE_01,
					projectInfoEntity.getProjectNo(), Arrays.asList(repaymentPlanInfoEntity), smsList);

			// 还款给公司收益账户
			AccountInfoEntity earnMainAccount = accountInfoRepository.findOne(Constant.ACCOUNT_ID_PROJECT_ERAN);
			// 还账户管理费(公司)
			List<AccountFlowInfoEntity> earnMainAccountFlowList = custAccountService.updateAccount(null, subAccountInfoEntity, earnMainAccount, 
					null, "3", SubjectConstant.TRADE_FLOW_TYPE_COMPANY_REPAYMENT_ALL_PROJECT, 
					reqeustNo, repaymentPlanInfoEntity.getAccountManageExpense(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMPANY_REPAYMENT_ALL_PROJECT, 
					Constant.TABLE_BAO_T_PROJECT_INFO, projectInfoEntity.getId(),  
					"正常还款", userId);
			// 保存用户付款明细
			projectPaymentService.saveCompanyPaymentRecord(projectInfoEntity.getId(), earnMainAccount.getCustId(), earnMainAccountFlowList.get(0).getId(), 
					BigDecimal.ZERO, repaymentPlanInfoEntity.getAccountManageExpense(), Constant.REPAYMENT_TYPE_01);
			
			// 5) 还款计划更新
			repaymentPlanInfoEntity.setTermAlreadyRepayAmount(repaymentPlanInfoEntity.getRepaymentTotalAmount());
			repaymentPlanInfoEntity.setRepaymentStatus(Constant.REPAYMENT_STATUS_CLEAN);
			repaymentPlanInfoEntity.setFactRepaymentDate(new Timestamp(System.currentTimeMillis()));
			repaymentPlanInfoEntity.setBasicModelProperty(userId, false);
			
			// 6) 项目更新
			updateProjectWithRepayment(projectInfoEntity, repaymentPlanInfoEntity);
			
			// 7) 记录日志
			LogInfoEntity logInfoEntity = new LogInfoEntity();
			logInfoEntity.setRelateType(Constant.TABLE_BAO_T_REPAYMENT_PLAN_INFO);
			logInfoEntity.setRelatePrimary(repaymentPlanInfoEntity.getId());
			logInfoEntity.setLogType(Constant.OPERATION_TYPE_33);
			logInfoEntity.setOperDesc("");
			logInfoEntity.setOperPerson(userId);
			logInfoEntity.setOperIpaddress((String)params.get("ipAddress"));
			logInfoEntity.setMemo(String.format("项目%s:%s提前还款%s元", projectInfoEntity.getProjectNo(), projectInfoEntity.getProjectName(), repaymentPlanInfoEntity.getRepaymentTotalAmount().toPlainString()));
			logInfoEntity.setBasicModelProperty(userId, true);
			logInfoEntityRepository.save(logInfoEntity);
			
			return new ResultVo(true, "正常还款成功", smsList);
		}

		/**
		 * 风险金垫付
		 *
		 * @author  wangjf
		 * @date    2016年1月16日 下午2:45:00
		 * @param params
		 * @return
		 * @throws SLException
		 */
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public ResultVo riskRepayment(Map<String, Object> params)
				throws SLException {
			
			String replaymentPlanId = (String)params.get("replaymentPlanId");
			String userId = (String)params.get("userId");
			
			// 1) 判断还款计划是否存在
			RepaymentPlanInfoEntity repaymentPlanInfoEntity = repaymentPlanInfoRepository.findOne(replaymentPlanId);
			if(repaymentPlanInfoEntity == null) {
				return new ResultVo(false, "还款计划不存在");
			}
			if(Constant.REPAYMENT_STATUS_CLEAN.equals(repaymentPlanInfoEntity.getRepaymentStatus())
					|| Constant.IS_RISKAMOUNT_REPAY_YES.equals(repaymentPlanInfoEntity.getIsRiskamountRepay())
					|| Constant.IS_AMOUNT_FROZEN_YES.equals(repaymentPlanInfoEntity.getIsAmountFrozen())) {
				return new ResultVo(false, "该笔还款计划已经还款或已经垫付，切勿重复操作");
			}
			
			// 2) 判断项目是否是正常状态
			ProjectInfoEntity projectInfoEntity = projectInfoRepository.findOne(repaymentPlanInfoEntity.getProjectId());
			if(projectInfoEntity == null) {
				return new ResultVo(false, "项目不存，不允许做还款");
			}
			
			ResultVo resultVo = new ResultVo(false);
			if(Constant.PROJECT_STATUS_08.equals(projectInfoEntity.getProjectStatus())) { // 当期垫付
				
				// 3) 项目改为逾期状态
				projectInfoEntity.setProjectStatus(Constant.PROJECT_STATUS_10);
				projectInfoEntity.setFlowStatus(Constant.PROJECT_STATUS_10);
				
				if(DateUtils.parseDate(repaymentPlanInfoEntity.getExpectRepaymentDate(), "yyyyMMdd").compareTo(DateUtils.truncateDate(new Date())) == 0){
					resultVo = riskRepaymentForCurrent(projectInfoEntity, repaymentPlanInfoEntity, userId);
				}
				else {
					resultVo = riskRepaymentForOverDue(projectInfoEntity, repaymentPlanInfoEntity, userId);
				}
			}
			else if(Constant.PROJECT_STATUS_10.equals(projectInfoEntity.getProjectStatus())) { // 逾期垫付
				resultVo = riskRepaymentForOverDue(projectInfoEntity, repaymentPlanInfoEntity, userId);
			}
			else {
				resultVo = new ResultVo(false, "项目非还款中或者已逾期，不允许垫付");
			}
			
			return resultVo;
			
		}
		
		/**
		 * 风险金当期垫付
		 *
		 * @author  wangjf
		 * @date    2016年1月11日 下午3:14:47
		 * @param projectInfoEntity
		 * @param repaymentPlanInfoEntity
		 * @param userId
		 * @throws SLException
		 */
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public ResultVo riskRepaymentForCurrent(ProjectInfoEntity projectInfoEntity, RepaymentPlanInfoEntity repaymentPlanInfoEntity,
				String userId) throws SLException {

			List<Map<String, Object>> smsList = Lists.newArrayList();
			
			// 4) 判断风险金是否足额
			AccountInfoEntity accountRisk = accountInfoRepository.findOne(Constant.ACCOUNT_ID_PROJECT_RISK);
			if(accountRisk.getAccountAvailableAmount().compareTo(repaymentPlanInfoEntity.getRepaymentTotalAmount()) < 0) {
				return new ResultVo(false, "风险金余额不足，无法垫付");
			}
			
			String reqeustNo = numberService.generateTradeBatchNumber();
			SubAccountInfoEntity subAccountInfoEntity = subAccountInfoRepository.findByRelatePrimary(projectInfoEntity.getId());
			// 风险金账户->分账户
			List<AccountFlowInfoEntity> riskAccountFlowList = custAccountService.updateAccount(accountRisk, null, null, 
					subAccountInfoEntity, "2", SubjectConstant.TRADE_FLOW_TYPE_RISK_REPAYMENT_PROJECT, 
					reqeustNo, repaymentPlanInfoEntity.getRepaymentTotalAmount(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_RISK_REPAYMENT_PROJECT, 
					Constant.TABLE_BAO_T_REPAYMENT_PLAN_INFO, repaymentPlanInfoEntity.getId(),  
					"风险金垫付", userId); 
			
			// 保存风险金还款明细
			saveRepaymentRecord(repaymentPlanInfoEntity.getRepaymentTotalAmount(), projectInfoEntity.getId(), SubjectConstant.TRADE_FLOW_TYPE_RISK_REPAYMENT_PROJECT,
					riskAccountFlowList.get(0).getId(), Arrays.asList(repaymentPlanInfoEntity), Constant.REPAYMENT_TYPE_04, BigDecimal.ZERO);
			
			// 5) 给投资人和公司还款
			// 还款给投资人
			repaymentForUser(projectInfoEntity, repaymentPlanInfoEntity.getRepaymentPrincipal(), repaymentPlanInfoEntity.getRepaymentInterest(), BigDecimal.ZERO, 
					subAccountInfoEntity, reqeustNo, userId, "风险金垫付", Constant.REPAYMENT_TYPE_04,
					projectInfoEntity.getProjectNo(), Arrays.asList(repaymentPlanInfoEntity), smsList);
			
			// 还款给公司收益账户
			AccountInfoEntity earnMainAccount = accountInfoRepository.findOne(Constant.ACCOUNT_ID_PROJECT_ERAN);
			// 还账户管理费(公司)
			List<AccountFlowInfoEntity> earnMainAccountFlowList = custAccountService.updateAccount(null, subAccountInfoEntity, earnMainAccount, 
					null, "3", SubjectConstant.TRADE_FLOW_TYPE_COMPANY_REPAYMENT_ALL_PROJECT, 
					reqeustNo, repaymentPlanInfoEntity.getAccountManageExpense(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMPANY_REPAYMENT_ALL_PROJECT, 
					Constant.TABLE_BAO_T_PROJECT_INFO, projectInfoEntity.getId(),  
					"风险金垫付", userId);
			// 保存用户付款明细
			projectPaymentService.saveCompanyPaymentRecord(projectInfoEntity.getId(), earnMainAccount.getCustId(), earnMainAccountFlowList.get(0).getId(), 
					BigDecimal.ZERO, repaymentPlanInfoEntity.getAccountManageExpense(), Constant.REPAYMENT_TYPE_04);

			// 6) 更新还款计划
			repaymentPlanInfoEntity.setIsRiskamountRepay(Constant.IS_RISKAMOUNT_REPAY_YES);
			repaymentPlanInfoEntity.setRiskRepayAmount(repaymentPlanInfoEntity.getRepaymentTotalAmount());
			repaymentPlanInfoEntity.setBasicModelProperty(userId, false);
			
			// 7) 记录日志
			LogInfoEntity logInfoEntity = new LogInfoEntity();
			logInfoEntity.setRelateType(Constant.TABLE_BAO_T_REPAYMENT_PLAN_INFO);
			logInfoEntity.setRelatePrimary(repaymentPlanInfoEntity.getId());
			logInfoEntity.setLogType(Constant.OPERATION_TYPE_35);
			logInfoEntity.setOperBeforeContent(Constant.PROJECT_STATUS_08);
			logInfoEntity.setOperAfterContent(Constant.PROJECT_STATUS_10);
			logInfoEntity.setOperDesc("");
			logInfoEntity.setOperPerson(userId);
			logInfoEntity.setOperIpaddress("");
			logInfoEntity.setMemo(String.format("项目%s:%s风险金垫付%s元", projectInfoEntity.getProjectNo(), projectInfoEntity.getProjectName(), repaymentPlanInfoEntity.getRepaymentTotalAmount().toPlainString()));
			logInfoEntity.setBasicModelProperty(userId, true);
			logInfoEntityRepository.save(logInfoEntity);
			
			return new ResultVo(true, "风险金垫付成功", smsList);
		}
		
		/**
		 * 风险金逾期垫付
		 *
		 * @author  wangjf
		 * @date    2016年1月11日 下午3:15:16
		 * @param projectInfoEntity
		 * @param repaymentPlanInfoEntity
		 * @param userId
		 * @throws SLException
		 */
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public ResultVo riskRepaymentForOverDue(ProjectInfoEntity projectInfoEntity, RepaymentPlanInfoEntity repaymentPlanInfoEntity,
				String userId) throws SLException {
			
			List<Map<String, Object>> smsList = Lists.newArrayList();
			final Date now = new Date();
			
			// 2) 取所有逾期还款计划数据
			List<RepaymentPlanInfoEntity> allRepaymentPlanList = projectInfoRepository.findRepaymentByProjectId(projectInfoEntity.getId());
			Collection<RepaymentPlanInfoEntity> tmpPlanList = Collections2.filter(allRepaymentPlanList, new Predicate<RepaymentPlanInfoEntity>() {

				@Override
				public boolean apply(RepaymentPlanInfoEntity input) {
					// 筛选出状态未还款且预期还款日小于当天的数据
					if(Constant.REPAYMENT_STATUS_WAIT.equals(input.getRepaymentStatus()) // 未还款
							&& Constant.IS_RISKAMOUNT_REPAY_NO.equals(input.getIsRiskamountRepay()) // 未垫付
							&& DateUtils.parseDate(input.getExpectRepaymentDate(), "yyyyMMdd").compareTo(now) <= 0) {
						return true;
					}
					return false;
				}

			});
			
			// 没有符合条件的还款计划
			List<RepaymentPlanInfoEntity> planList = Lists.newArrayList(tmpPlanList.iterator());
			if(planList.size() == 0) {
				return new ResultVo(false, "项目不存在逾期待垫付的还款计划");
			}

			// 3) 计算本期应还
			// 逾期天数 = 当前日期 - 应还日期
			RepaymentPlanInfoEntity firstPlan = planList.get(0);
			int overdueDays = DateUtils.datePhaseDiffer(DateUtils.parseDate(firstPlan.getExpectRepaymentDate(), "yyyyMMdd"), now);
			
			// 逾期费用 = 剩余本金 * 逾期管理费率 * 逾期天数
			BigDecimal overdueRate = paramService.findOverdueRate();
			BigDecimal overdueExpense = ArithUtil.mul(ArithUtil.mul(ArithUtil.add(firstPlan.getRemainderPrincipal(), firstPlan.getRepaymentPrincipal()), overdueRate), new BigDecimal(String.valueOf(overdueDays)));
			overdueExpense = ArithUtil.formatScale2(overdueExpense);
			
			// 初始化本期应还、风险金垫付本金总额、风险金垫付利息总额、风险金垫付管理费总额、风险金垫付罚息总额
			BigDecimal currRepayAmount = BigDecimal.ZERO; // 本期应还
			BigDecimal totalPrincipalRisk = BigDecimal.ZERO;// 风险金垫付本金总额
			BigDecimal totalInterestRisk = BigDecimal.ZERO;// 风险金垫付利息总额
			BigDecimal totalExpenseRisk = BigDecimal.ZERO;// 风险金垫付管理费总额
			
			for (int i = 0; i < planList.size(); i ++) {
				totalPrincipalRisk = ArithUtil.add(planList.get(i).getRepaymentPrincipal(), totalPrincipalRisk);
				totalInterestRisk = ArithUtil.add(planList.get(i).getRepaymentInterest(), totalInterestRisk);
				totalExpenseRisk = ArithUtil.add(planList.get(i).getAccountManageExpense(), totalExpenseRisk);
			}
			
			// 本次应还(所有逾期本金+所有逾期利息+所有逾期账户管理费+逾期费用)
			currRepayAmount = ArithUtil.add(currRepayAmount, totalPrincipalRisk);
			currRepayAmount = ArithUtil.add(currRepayAmount, totalInterestRisk);
			currRepayAmount = ArithUtil.add(currRepayAmount, totalExpenseRisk);
			currRepayAmount = ArithUtil.add(currRepayAmount, overdueExpense);

			// 4) 判断风险金是否足额
			AccountInfoEntity accountRisk = accountInfoRepository.findOne(Constant.ACCOUNT_ID_PROJECT_RISK);
			if(accountRisk.getAccountAvailableAmount().compareTo(currRepayAmount) < 0) {
				return new ResultVo(false, "风险金余额不足，无法垫付");
			}
			
			String reqeustNo = numberService.generateTradeBatchNumber();
			SubAccountInfoEntity subAccountInfoEntity = subAccountInfoRepository.findByRelatePrimary(projectInfoEntity.getId());
			// 风险金账户->分账户
			List<AccountFlowInfoEntity> riskAccountFlowList = custAccountService.updateAccount(accountRisk, null, null, 
					subAccountInfoEntity, "2", SubjectConstant.TRADE_FLOW_TYPE_RISK_REPAYMENT_PROJECT, 
					reqeustNo, currRepayAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_RISK_REPAYMENT_PROJECT, 
					Constant.TABLE_BAO_T_REPAYMENT_PLAN_INFO, repaymentPlanInfoEntity.getId(),  
					"风险金垫付", userId); 
			
			// 保存风险金还款明细
			saveRepaymentRecord(currRepayAmount, projectInfoEntity.getId(), SubjectConstant.TRADE_FLOW_TYPE_RISK_REPAYMENT_PROJECT,
					riskAccountFlowList.get(0).getId(), planList, Constant.REPAYMENT_TYPE_04, overdueExpense);
			
			// 5) 给投资人和公司还款
			// 还款给投资人
			repaymentForUser(projectInfoEntity, totalPrincipalRisk, totalInterestRisk, overdueExpense, 
					subAccountInfoEntity, reqeustNo, userId, "风险金垫付", Constant.REPAYMENT_TYPE_04,
					projectInfoEntity.getProjectNo(), Arrays.asList(repaymentPlanInfoEntity), smsList);
			
			// 还款给公司收益账户
			AccountInfoEntity earnMainAccount = accountInfoRepository.findOne(Constant.ACCOUNT_ID_PROJECT_ERAN);
			// 还账户管理费(公司)
			List<AccountFlowInfoEntity> earnMainAccountFlowList = custAccountService.updateAccount(null, subAccountInfoEntity, earnMainAccount, 
					null, "3", SubjectConstant.TRADE_FLOW_TYPE_COMPANY_REPAYMENT_ALL_PROJECT, 
					reqeustNo, totalExpenseRisk, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMPANY_REPAYMENT_ALL_PROJECT, 
					Constant.TABLE_BAO_T_PROJECT_INFO, projectInfoEntity.getId(),  
					"风险金垫付", userId);
			// 保存用户付款明细
			projectPaymentService.saveCompanyPaymentRecord(projectInfoEntity.getId(), earnMainAccount.getCustId(), earnMainAccountFlowList.get(0).getId(), 
					BigDecimal.ZERO, totalExpenseRisk, Constant.REPAYMENT_TYPE_04);
			
			// 6) 更新还款计划
			for(RepaymentPlanInfoEntity r : planList) {
				BigDecimal riskRepayAmount = r.getRepaymentTotalAmount();
				if(firstPlan.getId().equals(r.getId())) {
					riskRepayAmount = ArithUtil.add(riskRepayAmount, overdueExpense);
				}
				r.setIsRiskamountRepay(Constant.IS_RISKAMOUNT_REPAY_YES);
				r.setRiskRepayAmount(riskRepayAmount);
				r.setBasicModelProperty(userId, false);
			}

			// 7) 记录日志
			LogInfoEntity logInfoEntity = new LogInfoEntity();
			logInfoEntity.setRelateType(Constant.TABLE_BAO_T_PROJECT_INFO);
			logInfoEntity.setRelatePrimary(projectInfoEntity.getId());
			logInfoEntity.setLogType(Constant.OPERATION_TYPE_35);
			logInfoEntity.setOperBeforeContent(Constant.PROJECT_STATUS_08);
			logInfoEntity.setOperAfterContent(Constant.PROJECT_STATUS_10);
			logInfoEntity.setOperDesc("");
			logInfoEntity.setOperPerson(userId);
			logInfoEntity.setOperIpaddress("");
			logInfoEntity.setMemo(String.format("项目%s:%s风险金垫付%s元", projectInfoEntity.getProjectNo(), projectInfoEntity.getProjectName(), currRepayAmount.toPlainString()));
			logInfoEntity.setBasicModelProperty(userId, true);
			logInfoEntityRepository.save(logInfoEntity);
			
			return new ResultVo(true, "风险金垫付成功", smsList);
		}
		
		/**
		 * 保存还款记录
		 *
		 * @author  wangjf
		 * @date    2016年1月14日 下午2:00:25
		 * @param totalRepaymentAmount
		 * @param projectId
		 * @param tradeType
		 * @param accountFlowId
		 * @param planList
		 * @param repaymentType
		 * @param penaltyAmount
		 */
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public void saveRepaymentRecord(BigDecimal totalRepaymentAmount, String projectId, String tradeType,
				String accountFlowId, List<RepaymentPlanInfoEntity> planList, String repaymentType, 
				BigDecimal penaltyAmount) {
			RepaymentRecordInfoEntity repaymentRecordInfoEntity = new RepaymentRecordInfoEntity();
			repaymentRecordInfoEntity.setRepayAmount(totalRepaymentAmount);
			repaymentRecordInfoEntity.setHandleStatus(Constant.TRADE_STATUS_03);
			repaymentRecordInfoEntity.setProjectId(projectId);
			repaymentRecordInfoEntity.setTradeType(tradeType);
			repaymentRecordInfoEntity.setRelateType(Constant.TABLE_BAO_T_ACCOUNT_FLOW_INFO);
			repaymentRecordInfoEntity.setRelatePrimary(accountFlowId);
			repaymentRecordInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
			repaymentRecordInfoEntity = repaymentRecordInfoRepository.save(repaymentRecordInfoEntity);
			
			List<RepayRecordDetailInfoEntity> repayRecordDetailList = Lists.newArrayList();
			
			switch(repaymentType) {
			case Constant.REPAYMENT_TYPE_02://逾期还款
			{
				for(int i = 0; i < planList.size(); i ++) {

					if(i == 0) { // 首期含有罚息
						// 罚息
						if(penaltyAmount.compareTo(BigDecimal.ZERO) > 0) {
							repayRecordDetailList.add(createRepaymentDetail(repaymentRecordInfoEntity.getId(), planList.get(i).getId(), 
									SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_PENALTY_PROJECT, penaltyAmount));
						}
					}
					
					if(Constant.IS_RISKAMOUNT_REPAY_YES.equals(planList.get(i).getIsRiskamountRepay())) { // 风险金垫付
						// 本金
						repayRecordDetailList.add(createRepaymentDetail(repaymentRecordInfoEntity.getId(), planList.get(i).getId(), 
								SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_RISK_REPAYMENT_PRINCIPAL_PROJECT, planList.get(i).getRepaymentPrincipal()));
						
						// 利息
						repayRecordDetailList.add(createRepaymentDetail(repaymentRecordInfoEntity.getId(), planList.get(i).getId(), 
								SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_RISK_REPAYMENT_INTEREST_PROJECT, planList.get(i).getRepaymentInterest()));
						
						// 账户管理费
						repayRecordDetailList.add(createRepaymentDetail(repaymentRecordInfoEntity.getId(), planList.get(i).getId(), 
								SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_RISK_REPAYMENT_EXPENSE_PROJECT, planList.get(i).getAccountManageExpense()));
					}
					else {
						// 本金
						repayRecordDetailList.add(createRepaymentDetail(repaymentRecordInfoEntity.getId(), planList.get(i).getId(), 
								SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_PRINCIPAL_PROJECT, planList.get(i).getRepaymentPrincipal()));
						
						// 利息
						repayRecordDetailList.add(createRepaymentDetail(repaymentRecordInfoEntity.getId(), planList.get(i).getId(), 
								SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_INTEREST_PROJECT, planList.get(i).getRepaymentInterest()));
						
						// 账户管理费
						repayRecordDetailList.add(createRepaymentDetail(repaymentRecordInfoEntity.getId(), planList.get(i).getId(), 
								SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMPANY_REPAYMENT_EXPENSE_PROJECT, planList.get(i).getAccountManageExpense()));
					}
				}
				break;
			}
			case Constant.REPAYMENT_TYPE_03://提前还款
			{
				// 剩余本金
				repayRecordDetailList.add(createRepaymentDetail(repaymentRecordInfoEntity.getId(), planList.get(0).getId(), 
						SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_PRINCIPAL_PROJECT, ArithUtil.add(planList.get(0).getRepaymentPrincipal(), planList.get(0).getRemainderPrincipal())));
				
				if(planList.get(0).getPenaltyAmount().compareTo(BigDecimal.ZERO) == 0) { // 违约金为0（到期还本付息），给利息
					// 利息
					repayRecordDetailList.add(createRepaymentDetail(repaymentRecordInfoEntity.getId(), planList.get(0).getId(), 
							SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_INTEREST_PROJECT, planList.get(0).getRepaymentInterest()));
					
					// 账户管理费
					repayRecordDetailList.add(createRepaymentDetail(repaymentRecordInfoEntity.getId(), planList.get(0).getId(), 
							SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMPANY_REPAYMENT_EXPENSE_PROJECT, planList.get(0).getAccountManageExpense()));
				}
				else {
					// 违约金
					repayRecordDetailList.add(createRepaymentDetail(repaymentRecordInfoEntity.getId(), planList.get(0).getId(), 
							SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_BREACH_PROJECT, planList.get(0).getPenaltyAmount()));
				}
				break;
			}
			case Constant.REPAYMENT_TYPE_01://正常还款
			{
				for(int i = 0; i < planList.size(); i ++) {
					// 本金
					repayRecordDetailList.add(createRepaymentDetail(repaymentRecordInfoEntity.getId(), planList.get(i).getId(), 
							SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_PRINCIPAL_PROJECT, planList.get(i).getRepaymentPrincipal()));
					
					// 利息
					repayRecordDetailList.add(createRepaymentDetail(repaymentRecordInfoEntity.getId(), planList.get(i).getId(), 
							SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_INTEREST_PROJECT, planList.get(i).getRepaymentInterest()));
					
					// 账户管理费
					repayRecordDetailList.add(createRepaymentDetail(repaymentRecordInfoEntity.getId(), planList.get(i).getId(), 
							SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMPANY_REPAYMENT_EXPENSE_PROJECT, planList.get(i).getAccountManageExpense()));
				}
				break;
			}
			case Constant.REPAYMENT_TYPE_04://风险金垫付
			{
				for(int i = 0; i < planList.size(); i ++) {
					
					if(i == 0) { // 首期含有罚息
						// 罚息
						if(penaltyAmount.compareTo(BigDecimal.ZERO) > 0) {
							repayRecordDetailList.add(createRepaymentDetail(repaymentRecordInfoEntity.getId(), planList.get(i).getId(), 
									SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_RISK_OVERDUE_PENALTY_PROJECT, penaltyAmount));
						}
					}
					
					// 本金
					repayRecordDetailList.add(createRepaymentDetail(repaymentRecordInfoEntity.getId(), planList.get(i).getId(), 
							SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_RISK_OVERDUE_PRINCIPAL_PROJECT, planList.get(i).getRepaymentPrincipal()));
					
					// 利息
					repayRecordDetailList.add(createRepaymentDetail(repaymentRecordInfoEntity.getId(), planList.get(i).getId(), 
							SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_RISK_OVERDUE_INTEREST_PROJECT, planList.get(i).getRepaymentInterest()));
					
					// 账户管理费
					repayRecordDetailList.add(createRepaymentDetail(repaymentRecordInfoEntity.getId(), planList.get(i).getId(), 
							SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_RISK_OVERDUE_EXPENSE_PROJECT, planList.get(i).getAccountManageExpense()));
				}
				break;
			}
			default : 
				// 奖励收益
				repayRecordDetailList.add(createRepaymentDetail(repaymentRecordInfoEntity.getId(), planList.get(0).getId(), 
						SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_AWARD_PROJECT, totalRepaymentAmount));
				break;
			}

			repayRecordDetailInfoRepository.save(repayRecordDetailList);
		}
		
		/**
		 * 创建还款明细
		 *
		 * @author  wangjf
		 * @date    2016年1月14日 下午5:45:18
		 * @param repayRecordId
		 * @param repayPlanId
		 * @param subjectType
		 * @param tradeAmount
		 * @return
		 */
		public RepayRecordDetailInfoEntity createRepaymentDetail(String repayRecordId, String repayPlanId, String subjectType, BigDecimal tradeAmount) {
			RepayRecordDetailInfoEntity repayRecordDetailInfoEntity = new RepayRecordDetailInfoEntity();
			repayRecordDetailInfoEntity.setRepayRecordId(repayRecordId);
			repayRecordDetailInfoEntity.setRepayPlanId(repayPlanId);
			repayRecordDetailInfoEntity.setSubjectDirection(SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT);
			repayRecordDetailInfoEntity.setSubjectType(subjectType);
			repayRecordDetailInfoEntity.setTradeAmount(tradeAmount);
			repayRecordDetailInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
			return repayRecordDetailInfoEntity;
		}
	}
}
