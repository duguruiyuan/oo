package com.slfinance.shanlincaifu.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AccountInfoEntity;
import com.slfinance.shanlincaifu.entity.CustInfoEntity;
import com.slfinance.shanlincaifu.entity.LoanInfoEntity;
import com.slfinance.shanlincaifu.entity.RepaymentPlanInfoEntity;
import com.slfinance.shanlincaifu.entity.UserCommissionInfoEntity;
import com.slfinance.shanlincaifu.repository.AccountInfoRepository;
import com.slfinance.shanlincaifu.repository.CustInfoRepository;
import com.slfinance.shanlincaifu.repository.LoanInfoRepository;
import com.slfinance.shanlincaifu.repository.RepaymentPlanInfoRepository;
import com.slfinance.shanlincaifu.repository.UserCommissionInfoRepository;
import com.slfinance.shanlincaifu.repository.custom.LoanInfoRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.LoanManagerRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.LoanProjectRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.UserCommissionInfoRepositoryCustom;
import com.slfinance.shanlincaifu.service.AutoInvestJobService;
import com.slfinance.shanlincaifu.service.CustAccountService;
import com.slfinance.shanlincaifu.service.FlowNumberService;
import com.slfinance.shanlincaifu.service.GoldService;
import com.slfinance.shanlincaifu.service.LoanInfoService;
import com.slfinance.shanlincaifu.service.LoanJobService;
import com.slfinance.shanlincaifu.service.LoanManagerService;
import com.slfinance.shanlincaifu.service.LoanProjectService;
import com.slfinance.shanlincaifu.service.LoanRepaymentService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.SubjectConstant;
import com.slfinance.vo.ResultVo;

/**
 * @author lyy
 * @date 2016/11/29 17:39:56
 * */
@Slf4j
@Service
public class LoanJobServiceImpl implements LoanJobService {
	
	@Autowired
	LoanInfoService loanInfoService;
	
	@Autowired
	private LoanProjectRepositoryCustom loanProjectRepositoryCustom;
	
	@Autowired
	private LoanProjectService loanProjectService;
	
	@Autowired
	private RepaymentPlanInfoRepository repaymentPlanInfoRepository;
	
	@Autowired
	private LoanRepaymentService loanRepaymentService;
	
	@Autowired
	private UserCommissionInfoRepositoryCustom userCommissionInfoRepositoryCustom;
	
	@Autowired
	private GoldService goldService;
	
	@Autowired
	private UserCommissionInfoRepository userCommissionInfoRepository;
	
	@Autowired
	private LoanManagerService loanManagerService;
	
	@Autowired
	private LoanInfoRepository loanInfoRepository;

	@Autowired
	private CustInfoRepository custInfoRepository;
	
	@Autowired
	private AccountInfoRepository accountInfoRepository;
	
	@Autowired
	private CustAccountService custAccountService;
	
	@Autowired
	private FlowNumberService numberService;
	
	@Autowired
	private LoanManagerRepositoryCustom loanManagerRepositoryCustom;
	
	@Autowired
	private AutoInvestJobService autoInvestJobService;
	
	@Autowired
	LoanInfoRepositoryCustom loanInfoRepositoryCustom;
	
	@Autowired
	@Qualifier("loanAuditThreadPoolTaskExecutor")
	private Executor executor;
	
	/*
	 * 巨涟自动审核，发布标的信息
	 * (non-Javadoc)
	 * @see com.slfinance.shanlincaifu.service.LoanJobService#autoAuditLoanJL(java.util.Map)
	 */
	@Override
	public ResultVo autoAuditLoanJL(Map<String, Object> params) throws SLException {
		final Map<String, Object> jlParams = new HashMap<>();
		jlParams.put("start", "0");
		jlParams.put("length", "20000");
		jlParams.put("loanStatus", Constant.LOAN_STATUS_01);
		jlParams.put("companyName", Constant.DEBT_SOURCE_JLJR);
		Page<Map<String, Object>> pageVo = loanManagerRepositoryCustom.queryLoanInfoList(jlParams);
		List<Map<String, Object>> content = pageVo.getContent();
		for (Map<String, Object> map : content) {
			// 逐条审核
			Map<String, Object> param = Maps.newHashMap();
			param.put("loanId", map.get("loanId"));
			param.put("userId", Constant.SYSTEM_USER_BACK);
			param.put("auditStatus", Constant.WEALTH_AUDIT_STATUS_PASS);
			param.put("auditMemo", "系统自动审核通过");
			try {
				// 审核通过
				ResultVo restultVo = loanManagerService.auditLoan(param);
				if(ResultVo.isSuccess(restultVo)) { // 审核成功
					// 发布项目
					loanManagerService.publishLoanInfo(param);
				}
			} catch (SLException e) {
				log.warn("巨涟借款项目[id:{}]自动审核异常，原因:{}", map.get("loanId"), e.getMessage());
			} catch (Exception e) {
				log.error("巨涟借款项目[id:{}]自动审核未知异常，原因:{}", map.get("loanId"), e.getMessage());
			}
		}
		// 发布之后去跑预约投标
		executor.execute(new Runnable() {
			@Override
			public void run() {
//				List<String> loanIds = loanInfoRepository.findListReserveLoan();
				List<String> loanIds = loanInfoRepositoryCustom.findListReserveLoan();
				for(String loanId : loanIds){
					try {
						autoInvestJobService.autoReserveInvestForCustId(loanId);
					} catch (Exception e) {
						log.warn("预约投标异常!"+e.getMessage());
					}
				}
			}
		});
		return new ResultVo(true, "巨涟自动批量审核成功");
	}
	
	/*
	 * 根据公司名称自动审核，发布标的信息
	 * (non-Javadoc)
	 * @see com.slfinance.shanlincaifu.service.LoanJobService#autoAuditLoanJL(java.util.Map)
	 */
	@Override
	public ResultVo autoAuditLoan4Company(Map<String, Object> params) throws SLException {
		Page<Map<String, Object>> pageVo = loanManagerRepositoryCustom.queryLoanInfoList(params);
		List<Map<String, Object>> content = pageVo.getContent();
		for (Map<String, Object> map : content) {
			// 逐条审核
			Map<String, Object> param = Maps.newHashMap();
			param.put("loanId", map.get("loanId"));
			param.put("userId", Constant.SYSTEM_USER_BACK);
			param.put("auditStatus", Constant.WEALTH_AUDIT_STATUS_PASS);
			param.put("auditMemo", "系统自动审核通过");
			try {
				// 审核通过
				ResultVo restultVo = loanManagerService.auditLoan(param);
				if(ResultVo.isSuccess(restultVo)) { // 审核成功
					// 发布项目
					loanManagerService.publishLoanInfo(param);
				}
			} catch (SLException e) {
				log.warn("{}借款项目[id:{}]自动审核异常，原因:{}", params.get("companyName"), map.get("loanId"), e.getMessage());
			} catch (Exception e) {
				log.error("{}借款项目[id:{}]自动审核未知异常，原因:{}", params.get("companyName"), map.get("loanId"), e.getMessage());
			}
		}
		// 发布之后去跑预约投标
		executor.execute(new Runnable() {
			@Override
			public void run() {
//				List<String> loanIds = loanInfoRepository.findListReserveLoan();
				List<String> loanIds = loanInfoRepositoryCustom.findListReserveLoan();
				for(String loanId : loanIds){
					try {
						autoInvestJobService.autoReserveInvestForCustId(loanId);
					} catch (Exception e) {
						log.warn("预约投标异常!"+e.getMessage());
					}
				}
			}
		});
		return new ResultVo(true, params.get("companyName") + "自动批量审核成功");
	}
	
	
	@Override
	public ResultVo autoAuditLoanYZ(Map<String, Object> params) throws SLException {
		// 获取意真待审核借款项目列表
		final Map<String, Object> yzParam = new HashMap<String, Object>();
		yzParam.put("start", "0");
		yzParam.put("length", "20000");
		yzParam.put("loanStatus", Constant.LOAN_STATUS_01);
		yzParam.put("companyName", Constant.DEBT_SOURCE_YZJR);
		yzParam.put("isBackStage", "借款信息");
		Page<Map<String, Object>> pageVo = loanManagerRepositoryCustom.queryLoanInfoList(yzParam);
		List<Map<String, Object>> content = pageVo.getContent();
		for (Map<String, Object> map : content) {
			// 逐条审核
			Map<String, Object> param = Maps.newHashMap();
			param.put("loanId", map.get("loanId"));
			param.put("userId", Constant.SYSTEM_USER_BACK);
			param.put("auditStatus", Constant.WEALTH_AUDIT_STATUS_PASS);
			param.put("auditMemo", "系统自动审核通过");
			try {
				// 审核通过
				ResultVo restultVo = loanManagerService.auditLoan(param);
				if(ResultVo.isSuccess(restultVo)) { // 审核成功
					// 发布项目
					loanManagerService.publishLoanInfo(param);
				}
			} catch (SLException e) {
				log.warn("意真借款项目[id:{}]自动审核异常，原因:{}", map.get("loanId"), e.getMessage());
			} catch (Exception e) {
				log.error("意真借款项目[id:{}]自动审核未知异常，原因:{}", map.get("loanId"), e.getMessage());
			}
		}
		// 发布之后去跑预约投标
		executor.execute(new Runnable() {
			@Override
			public void run() {
//				List<String> loanIds = loanInfoRepository.findListReserveLoan();
				List<String> loanIds = loanInfoRepositoryCustom.findListReserveLoan();
				for(String loanId : loanIds){
					try {
						autoInvestJobService.autoReserveInvestForCustId(loanId);
					} catch (Exception e) {
						log.warn("预约投标异常!"+e.getMessage());
					}
				}
				
				// 单标智能投顾
				for(String loanId : loanIds){
					try {
						// 所有可够买的人购买之后，没满标并符合一定条件去做智能投顾
//						LoanInfoEntity loanInfoEntity = loanInfoRepository.findInfoToAutoInvest(loanId);
						String loanInfoEntity = loanInfoRepositoryCustom.findInfoToAutoInvest(loanId);
						if(!StringUtils.isEmpty(loanInfoEntity)){
								Map<String, Object> autoParam = Maps.newHashMap();
								autoParam.put("id", loanId);
								autoParam.put("type", "优选标");
								autoParam.put("isShow", false);
								
//								self.autoInvestUp(autoParam);
								autoInvestJobService.autoInvestUp(autoParam);
						}
					} catch (Exception e) {
						log.warn("预约未满，智能投顾出错！ loanId=" + loanId);
					}
				}
			}
		});
		return new ResultVo(true, "意真自动批量审核成功");
	}
	
	@Override
	public ResultVo autoAuditLoanNM(Map<String, Object> params) throws SLException {
		// 获取拿米待审核借款项目列表
		final Map<String, Object> yzParam = new HashMap<String, Object>();
		yzParam.put("start", "0");
		yzParam.put("length", "20000");
		yzParam.put("loanStatus", Constant.LOAN_STATUS_01);
		yzParam.put("companyName", Constant.DEBT_SOURCE_NMJR);
		Page<Map<String, Object>> pageVo = loanManagerRepositoryCustom.queryLoanInfoList(yzParam);
		List<Map<String, Object>> content = pageVo.getContent();
		for (Map<String, Object> map : content) {
			// 逐条审核
			Map<String, Object> param = Maps.newHashMap();
			param.put("loanId", map.get("loanId"));
			param.put("userId", Constant.SYSTEM_USER_BACK);
			param.put("auditStatus", Constant.WEALTH_AUDIT_STATUS_PASS);
			param.put("auditMemo", "系统自动审核通过");
			try {
				// 审核通过
				ResultVo restultVo = loanManagerService.auditLoan(param);
				if(ResultVo.isSuccess(restultVo)) { // 审核成功
					// 发布项目
					loanManagerService.publishLoanInfo(param);
				}
			} catch (SLException e) {
				log.warn("拿米借款项目[id:{}]自动审核异常，原因:{}", map.get("loanId"), e.getMessage());
			} catch (Exception e) {
				log.error("拿米借款项目[id:{}]自动审核未知异常，原因:{}", map.get("loanId"), e.getMessage());
			}
		}
	    // 发布之后去跑预约投标
		executor.execute(new Runnable() {
			@Override
			public void run() {
//				List<String> loanIds = loanInfoRepository.findListReserveLoan();
				List<String> loanIds = loanInfoRepositoryCustom.findListReserveLoan();
				for(String loanId : loanIds){
					try {
						autoInvestJobService.autoReserveInvestForCustId(loanId);
					} catch (Exception e) {
						log.warn("预约投标异常!", e);
					}
				}
			}
		});
		return new ResultVo(true, "拿米自动批量审核成功");
	}
	
	@Override
	public ResultVo autoPublishLoan(Map<String, Object> params)
			throws SLException {
		int success = 0, failure = 0;
		Map<String, Object> requestParams = Maps.newConcurrentMap();
		requestParams.put("loanStatus", Constant.LOAN_STATUS_04);
		requestParams.put("publishDate", DateUtils.truncateMinute(new Date()));
		List<Map<String, Object>> publishList = loanProjectRepositoryCustom.findWaitingPublishLoan(requestParams);
		log.info("查询到{}条项目等待发布", publishList.size());
		for(Map<String, Object> m : publishList) {
			Map<String, Object> param = Maps.newHashMap();
			param.put("loanId", m.get("id"));
			param.put("userId", Constant.SYSTEM_USER_BACK);
			try{
				ResultVo result = loanManagerService.publishLoanInfo(param);
				if(ResultVo.isSuccess(result)) {
					success ++;
					log.info("发布项目{}:{}成功", m.get("loanCode"), m.get("loanDesc"));
				}
				else {
					failure ++;
					log.info("发布项目{}:{}失败，失败原因{}", m.get("loanCode"), m.get("loanDesc"), (String)result.getValue("message"));
				}
			}
			catch(Exception e) {
				failure ++;
				log.error("发布项目{}:{}失败，失败原因{}", m.get("loanCode"), m.get("loanDesc"), e.getMessage());
			}
		}
		log.info("发布项目成功{}条，失败{}条", success, failure);
		return new ResultVo(true);
	}

	@Override
	public ResultVo autoUnReleaseLoan(Map<String, Object> params)
			throws SLException {
		int success = 0, failure = 0;
		Map<String, Object> requestParams = Maps.newConcurrentMap();
		requestParams.put("loanStatus", Constant.LOAN_STATUS_05);
		requestParams.put("rasieEndDate", DateUtils.getDateAfterByHour(new Date(), 0));
		List<Map<String, Object>> unReleaseList = loanProjectRepositoryCustom.findWaitingUnReleaseLoan(requestParams);
		log.info("查询到{}条项目等待流标", unReleaseList.size());
		for(Map<String, Object> m : unReleaseList) {
			Map<String, Object> param = Maps.newHashMap();
			param.put("loanId", m.get("id"));
			param.put("userId", Constant.SYSTEM_USER_BACK);
			try{
				ResultVo result = loanProjectService.unRelease(param);
				if(ResultVo.isSuccess(result)) {
					success ++;
					log.info("流标项目{}:{}成功", m.get("loanCode"), m.get("loanDesc"));
				}
				else {
					failure ++;
					log.info("流标项目{}:{}失败，失败原因{}", m.get("loanCode"), m.get("loanDesc"), (String)result.getValue("message"));
				}
			}
			catch(Exception e) {
				failure ++;
				log.error("流标项目{}:{}失败，失败原因{}", m.get("loanCode"), m.get("loanDesc"), e.getMessage());
			}
		}
		log.info("流标项目成功{}条，失败{}条", success, failure);
		return new ResultVo(true);
	}


	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, rollbackFor = SLException.class)
	public ResultVo publishLoan(Map<String, Object> params)
			throws SLException {
//		return loanInfoService.publishLoanInfo(params);
		return null;
	}

	@Override
	public ResultVo autoRepaymentLoan(Map<String, Object> params)
			throws SLException {
		 
		String repaymentDate = (String)params.get("repaymentDate");
		if(StringUtils.isEmpty(repaymentDate)) {
			repaymentDate = DateUtils.getCurrentDate("yyyyMMdd");
		}
		// 正常还款(预计还款日期小于等于当前时间且未还款，借款状态正常，发布时间不为空（散标）)
		List<RepaymentPlanInfoEntity> repaymentList = repaymentPlanInfoRepository.findWaitingRepaymentPlan(repaymentDate, Constant.REPAYMENT_STATUS_WAIT, Constant.LOAN_STATUS_08);
		for(RepaymentPlanInfoEntity r : repaymentList) {
			// 从资产端公司账户划款到借款人账户
			internalLoanJobService.transfer(r);
		}
		// 自动还款
		Map<String, Object> requestParam = Maps.newConcurrentMap();
		requestParam.put("waitingRepaymentPlanList", repaymentList);
		requestParam.put("loanId", "");
		loanRepaymentService.normalRepayment(requestParam);

		return new ResultVo(true, "自动还款成功");
	}
	
	@Autowired
	private InternalLoanJobService internalLoanJobService;
		
	@Service
	public static class InternalLoanJobService {
		@Autowired
		private LoanInfoRepository loanInfoRepository;
		@Autowired
		private CustInfoRepository custInfoRepository;
		@Autowired
		private AccountInfoRepository accountInfoRepository;
		@Autowired
		private CustAccountService custAccountService;
		@Autowired
		private FlowNumberService numberService;

		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public void transfer(RepaymentPlanInfoEntity r) throws SLException {
			if(!Constant.IS_AMOUNT_FROZEN_YES.equals(r.getIsAmountFrozen())) { // 未做还款冻结
				// 判断项目是否为意真|拿米 需要先从公司账号划款到借款人账户
				LoanInfoEntity loanInfoEntity = loanInfoRepository.findOne(r.getLoanEntity().getId());
				// 意真金融 | 拿米金融 的标的需要先从公司账号划款到借款人账户
				if (Constant.DEBT_SOURCE_NMJR.equals(loanInfoEntity.getCompanyName()) 
						|| Constant.DEBT_SOURCE_YZJR.equals(loanInfoEntity.getCompanyName())
						|| Constant.DEBT_SOURCE_JLJR.equals(loanInfoEntity.getCompanyName())) {
					String loginName = Constant.COMPANY_LOGIN_NAME_NMJR;
					if (Constant.DEBT_SOURCE_YZJR.equals(loanInfoEntity.getCompanyName())) {
						loginName = Constant.COMPANY_LOGIN_NAME_YZJR;
					}
					if (Constant.DEBT_SOURCE_JLJR.equals(loanInfoEntity.getCompanyName())) {
						loginName = Constant.COMPANY_LOGIN_NAME_JLJR;
					}
					// 1. 获取公司账户信息和借款人账户信息
					CustInfoEntity companyCustInfo = custInfoRepository.findByLoginName(loginName);
					// 公司账户
					AccountInfoEntity companyAccount = accountInfoRepository.findByCustId(companyCustInfo.getId()); 
					// 借款人账户
					AccountInfoEntity loanerAccount = accountInfoRepository.findByCustId(loanInfoEntity.getCustId());
					// 借款人账户金额小于还款金额且公司账户金额大于等于还款金额时，从公司账户划扣到借款人账户
					if(loanerAccount.getAccountAvailableAmount().compareTo(r.getRepaymentTotalAmount()) < 0) {
						if(companyAccount.getAccountAvailableAmount().compareTo(r.getRepaymentTotalAmount()) >= 0) {
							String requestNo = numberService.generateTradeBatchNumber();
							// 2. 从公司账号划款到借款人账户
					        custAccountService.updateAccount(companyAccount, null, loanerAccount, 
									null, "1", SubjectConstant.TRADE_FLOW_TYPE_REPAYMENT_RECHARGE_LOAN, 
									requestNo, r.getRepaymentTotalAmount(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_REPAYMENT_RECHARGE_LOAN, 
									Constant.TABLE_BAO_T_LOAN_INFO, loanInfoEntity.getId(),  
									String.format("优选项目[%s]还款充值", loanInfoEntity.getLoanCode()), Constant.SYSTEM_USER_BACK);
						}
						else {
							log.warn("还款计划[id:{}]公司账户可用余额不足，无法还款", r.getId());
						} 
					} 
				}
			}
		}
	}

	@Override
	public ResultVo caclCommission(Map<String, Object> params)
			throws SLException {
		
		String commonMonth = "";
		Date now = new Date();
		if(params.containsKey("currentDate")) {
			now = DateUtils.parseDate((String)params.get("currentDate"), "yyyyMMdd");
			commonMonth = DateUtils.formatDate(now, "yyyyMM");
		}
		else {
			now = new Date();
			commonMonth = DateUtils.formatDate(now, "yyyyMM");
		}
		
		Map<String, Object> commissionParams = Maps.newConcurrentMap();
		commissionParams.put("commonMonth", commonMonth);
		
		// 计算新投资人业务员业绩
		List<Map<String, Object>> commissionList = userCommissionInfoRepositoryCustom.queryWaitingCommissionList(commissionParams);
		for(Map<String, Object> m : commissionList) {
			goldService.createSingleCommission(m);
		}
		
		// 计算原业务员业绩（针对新投资人没有业务员，而原业务员需要更新业绩的，主要针对债权转让的情况）
		List<Map<String, Object>> outcommissionList = userCommissionInfoRepositoryCustom.queryWaitingTransferOutCommissionList(commissionParams);
		for(Map<String, Object> m : outcommissionList) {
			goldService.createSingleCommission(m);
		}
		
		return new ResultVo(true, "计算业绩成功");
	}

	@Override
	public ResultVo commissionWithdraw(Map<String, Object> params)
			throws SLException {
		
		if(StringUtils.isEmpty((String)params.get("currentDate"))) {
			params.put("currentDate", DateUtils.formatDate(new Date(), "yyyyMMdd"));
		}
		
		List<UserCommissionInfoEntity> commissionList = userCommissionInfoRepository.findByWaitingSettlement(Constant.PAYMENT_STATUS_02, (String)params.get("currentDate"));
		for(UserCommissionInfoEntity u : commissionList) {
			Map<String, Object> requestMap = Maps.newConcurrentMap();
			requestMap.put("userCommissionId", u.getId());
			requestMap.put("userId", Constant.SYSTEM_USER_BACK);
			goldService.commissionWithdraw(requestMap);
		}
		return new ResultVo(true, "计算业绩成功");
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResultVo autoGrantConfirm(Map<String, Object> params)
			throws SLException {
		List<Map<String, Object>> waitGrantConfirmList = loanProjectRepositoryCustom.findWaitingGrantConfirmList(params);
		log.info("查询到{}条项目等待放款确认", waitGrantConfirmList.size());
		int success = 0, failure = 0;
		for(Map<String, Object> param : waitGrantConfirmList) {
			try {
				ResultVo result = null;
				Map<String, Object> requestParams = Maps.newHashMap();
				requestParams.put("grantDate", new Date());
				requestParams.put("grantStatus", param.get("grantStatus"));
				requestParams.put("loanId", param.get("projectId"));
				requestParams.put("userId", Constant.SYSTEM_USER_BACK);	
				requestParams.put("needUpdateAccount", false);
				
				// 若放款成功则给还款计划生成还款日期
				if(Constant.GRANT_STATUS_02.equals((String)param.get("grantStatus"))) {
					result = loanProjectService.generateRepaymentPlanDay(requestParams);
					Map<String, Object> data = (Map<String, Object>)result.getValue("data");
					requestParams.put("repaymentList", data.get("repaymentList"));
				}
				
				// 放款确认
				result = loanProjectService.grantConfirm(requestParams);
				if(ResultVo.isSuccess(result)) {
					success ++;
					log.info("放款确认项目{}:{}成功", param.get("projectNo"), param.get("projectName"));
				}
				else {
					failure ++;
					log.info("放款确认项目{}:{}失败，失败原因{}", param.get("projectNo"), param.get("projectName"), (String)result.getValue("message"));
				}
			}
			catch(Exception e) {
				failure ++;
				log.error("放款确认项目{}:{}失败，失败原因{}", param.get("projectNo"), param.get("projectName"), e.getMessage());
			}
		}
		log.info("放款确认项目成功{}条，失败{}条", success, failure);
		return new ResultVo(true);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ResultVo autoGrantConfirmYZ(Map<String, Object> params)
			throws SLException {
		List<Map<String, Object>> waitGrantConfirmList = loanProjectRepositoryCustom.findWaitingGrantConfirmListYZ(params);
		log.info("查询到{}条意真项目等待放款确认", waitGrantConfirmList.size());
		int success = 0, failure = 0;
		for(Map<String, Object> param : waitGrantConfirmList) {
			try {
				ResultVo result = null;
				Map<String, Object> requestParams = Maps.newHashMap();
				requestParams.put("grantDate", new Date());
				requestParams.put("grantStatus", param.get("grantStatus"));
				requestParams.put("loanId", param.get("projectId"));
				requestParams.put("userId", Constant.SYSTEM_USER_BACK);	
				requestParams.put("needUpdateAccount", false);
				
				// 若放款成功则给还款计划生成还款日期
				if(Constant.GRANT_STATUS_02.equals((String)param.get("grantStatus"))) {
					result = loanProjectService.generateRepaymentPlanDay(requestParams);
					Map<String, Object> data = (Map<String, Object>)result.getValue("data");
					requestParams.put("repaymentList", data.get("repaymentList"));
				}
				
				// 放款确认
				result = loanProjectService.grantConfirm(requestParams);
				if(ResultVo.isSuccess(result)) {
					success ++;
					log.info("放款确认意真项目{}:{}成功", param.get("projectNo"), param.get("projectName"));
				}
				else {
					failure ++;
					log.info("放款确认意真项目{}:{}失败，失败原因{}", param.get("projectNo"), param.get("projectName"), (String)result.getValue("message"));
				}
			}
			catch(Exception e) {
				failure ++;
				log.error("放款确认意真项目{}:{}失败，失败原因{}", param.get("projectNo"), param.get("projectName"), e.getMessage());
			}
		}
		log.info("放款确认意真项目成功{}条，失败{}条", success, failure);
		return new ResultVo(true);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ResultVo autoGrantConfirmJL(Map<String, Object> params)
			throws SLException {
		List<Map<String, Object>> waitGrantConfirmList = loanProjectRepositoryCustom.findWaitingGrantConfirmListJL(params);
		log.info("查询到{}条巨涟项目等待放款确认", waitGrantConfirmList.size());
		int success = 0, failure = 0;
		for(Map<String, Object> param : waitGrantConfirmList) {
			try {
				ResultVo result = null;
				Map<String, Object> requestParams = Maps.newHashMap();
				requestParams.put("grantDate", new Date());
				requestParams.put("grantStatus", param.get("grantStatus"));
				requestParams.put("loanId", param.get("projectId"));
				requestParams.put("userId", Constant.SYSTEM_USER_BACK);	
				requestParams.put("needUpdateAccount", false);
				
				// 若放款成功则给还款计划生成还款日期
				if(Constant.GRANT_STATUS_02.equals((String)param.get("grantStatus"))) {
					result = loanProjectService.generateRepaymentPlanDay(requestParams);
					Map<String, Object> data = (Map<String, Object>)result.getValue("data");
					requestParams.put("repaymentList", data.get("repaymentList"));
				}
				
				// 放款确认
				result = loanProjectService.grantConfirm(requestParams);
				if(ResultVo.isSuccess(result)) {
					success ++;
					log.info("放款确认巨涟项目{}:{}成功", param.get("projectNo"), param.get("projectName"));
				}
				else {
					failure ++;
					log.info("放款确认巨涟项目{}:{}失败，失败原因{}", param.get("projectNo"), param.get("projectName"), (String)result.getValue("message"));
				}
			}
			catch(Exception e) {
				failure ++;
				log.error("放款确认巨涟项目{}:{}失败，失败原因{}", param.get("projectNo"), param.get("projectName"), e.getMessage());
			}
		}
		log.info("放款确认巨涟项目成功{}条，失败{}条", success, failure);
		return new ResultVo(true);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ResultVo autoGrantConfirm4Company(Map<String, Object> params)
			throws SLException {
		List<Map<String, Object>> waitGrantConfirmList = loanProjectRepositoryCustom.findWaitingGrantConfirm4CompanyList(params);
		log.info("查询到{}条{}项目等待放款确认", waitGrantConfirmList.size(), params.get("companyName"));
		int success = 0, failure = 0;
		for(Map<String, Object> param : waitGrantConfirmList) {
			try {
				ResultVo result = null;
				Map<String, Object> requestParams = Maps.newHashMap();
				requestParams.put("grantDate", new Date());
				requestParams.put("grantStatus", param.get("grantStatus"));
				requestParams.put("loanId", param.get("projectId"));
				requestParams.put("userId", Constant.SYSTEM_USER_BACK);	
				requestParams.put("needUpdateAccount", false);
				
				// 若放款成功则给还款计划生成还款日期
				if(Constant.GRANT_STATUS_02.equals((String)param.get("grantStatus"))) {
					result = loanProjectService.generateRepaymentPlanDay(requestParams);
					Map<String, Object> data = (Map<String, Object>)result.getValue("data");
					requestParams.put("repaymentList", data.get("repaymentList"));
				}
				
				// 放款确认
				result = loanProjectService.grantConfirm(requestParams);
				if(ResultVo.isSuccess(result)) {
					success ++;
					log.info("放款确认{}项目{}:{}成功", params.get("companyName"), param.get("projectNo"), param.get("projectName"));
				}
				else {
					failure ++;
					log.info("放款确认{}项目{}:{}失败，失败原因{}", params.get("companyName"), param.get("projectNo"), param.get("projectName"), (String)result.getValue("message"));
				}
			}
			catch(Exception e) {
				failure ++;
				log.error("放款确认{}项目{}:{}失败，失败原因{}", params.get("companyName"), param.get("projectNo"), param.get("projectName"), e.getMessage());
			}
		}
		log.info("放款确认{}项目成功{}条，失败{}条", params.get("companyName"), success, failure);
		return new ResultVo(true);
	}
	

	@Override
	public ResultVo autoGrant(Map<String, Object> params) throws SLException {
		List<Map<String, Object>> waitGrantConfirmList = loanProjectRepositoryCustom.findWaitingGrantList(params);
		log.info("查询到{}条项目等待放款", waitGrantConfirmList.size());
		
		if(waitGrantConfirmList.size() > 0) {
			
			List<String> loanIds = Lists.transform(waitGrantConfirmList, new Function<Map<String, Object>, String>() {
				@Override
				public String apply(Map<String, Object> input) {
					return (String)input.get("loanId");
				}
			});

			Map<String, Object> requestParams = Maps.newHashMap();
			requestParams.put("loanIds", loanIds);
			requestParams.put("userId", "1");		
			
			loanManagerService.batchLending(requestParams);
		}
		
		return new ResultVo(true);
	}
	
	@Override
	public ResultVo autoGrant4Company(Map<String, Object> params) throws SLException {
		List<Map<String, Object>> waitGrantConfirmList = loanProjectRepositoryCustom.findWaitingGrant4CompanyList(params);
		log.info("查询到{}条{}项目等待放款", waitGrantConfirmList.size(), params.get("companyName"));
		
		if(waitGrantConfirmList.size() > 0) {
			
			List<String> loanIds = Lists.transform(waitGrantConfirmList, new Function<Map<String, Object>, String>() {
				@Override
				public String apply(Map<String, Object> input) {
					return (String)input.get("loanId");
				}
			});

			Map<String, Object> requestParams = Maps.newHashMap();
			requestParams.put("loanIds", loanIds);
			requestParams.put("userId", "1");		
			
			loanManagerService.batchLending(requestParams);
		}
		
		return new ResultVo(true);
	}
	
	@Override
	public ResultVo autoGrantYZ(Map<String, Object> params) throws SLException {
		List<Map<String, Object>> waitGrantConfirmList = loanProjectRepositoryCustom.findWaitingGrantYZList(params);
		log.info("查询到{}条意真项目等待放款", waitGrantConfirmList.size());
		
		if(waitGrantConfirmList.size() > 0) {
			
			List<String> loanIds = Lists.transform(waitGrantConfirmList, new Function<Map<String, Object>, String>() {
				@Override
				public String apply(Map<String, Object> input) {
					return (String)input.get("loanId");
				}
			});

			Map<String, Object> requestParams = Maps.newHashMap();
			requestParams.put("loanIds", loanIds);
			requestParams.put("userId", "1");		
			
			loanManagerService.batchLending(requestParams);
		}
		
		return new ResultVo(true);
	}
	
	/*
	 * 巨涟自动放款
	 * (non-Javadoc)
	 * @see com.slfinance.shanlincaifu.service.LoanJobService#autoGrantJL(java.util.Map)
	 */
	@Override
	public ResultVo autoGrantJL(Map<String, Object> params) throws SLException {
		List<Map<String, Object>> waitGrantConfirmList = loanProjectRepositoryCustom.findWaitingGrantJLList(params);
		log.info("查询到{}条巨涟标的等待放款", waitGrantConfirmList.size());
		
		if(waitGrantConfirmList.size() > 0) {
			
			List<String> loanIds = Lists.transform(waitGrantConfirmList, new Function<Map<String, Object>, String>() {
				@Override
				public String apply(Map<String, Object> input) {
					return (String)input.get("loanId");
				}
			});

			Map<String, Object> requestParams = Maps.newHashMap();
			requestParams.put("loanIds", loanIds);
			requestParams.put("userId", "1");		
			
			loanManagerService.batchLending(requestParams);
		}
		
		return new ResultVo(true);
	}

	@Override
	public ResultVo autoCancelLoan(Map<String, Object> params)
			throws SLException {
		
		if(!params.containsKey("currentDate")) {
			params.put("currentDate", new Date());
		}
		
		// 1) 转让价值小于1000的需撤销		
		// 2) 已逾期的且转让中的需撤销		
		// 3) 超出转让戒指日期的需撤销
		List<Map<String, Object>> waitingCancelList = loanProjectRepositoryCustom.findWaitingCancelList(params);
		log.info("查询到{}条项目等待撤销转让", waitingCancelList.size());
		
		if(waitingCancelList.size() > 0) {
			for(Map<String, Object> param : waitingCancelList) { 
				Map<String, Object> requestParams = Maps.newHashMap();
				requestParams.put("transferApplyId", param.get("id"));
				requestParams.put("custId", Constant.SYSTEM_USER_BACK);	
				requestParams.put("tradePass", "123456");	
				loanManagerService.canceTransferDebt(requestParams);
			}
			
		}
		return new ResultVo(true);
	}

	
}
