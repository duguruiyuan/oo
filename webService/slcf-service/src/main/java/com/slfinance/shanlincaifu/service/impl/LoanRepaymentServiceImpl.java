package com.slfinance.shanlincaifu.service.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AccountFlowInfoEntity;
import com.slfinance.shanlincaifu.entity.AccountInfoEntity;
import com.slfinance.shanlincaifu.entity.CustInfoEntity;
import com.slfinance.shanlincaifu.entity.InvestInfoEntity;
import com.slfinance.shanlincaifu.entity.LoanDetailInfoEntity;
import com.slfinance.shanlincaifu.entity.LoanInfoEntity;
import com.slfinance.shanlincaifu.entity.LoanTransferEntity;
import com.slfinance.shanlincaifu.entity.LogInfoEntity;
import com.slfinance.shanlincaifu.entity.PaymentRecordDetailEntity;
import com.slfinance.shanlincaifu.entity.PaymentRecordInfoEntity;
import com.slfinance.shanlincaifu.entity.PurchaseAwardInfoEntity;
import com.slfinance.shanlincaifu.entity.RepayRecordDetailInfoEntity;
import com.slfinance.shanlincaifu.entity.RepaymentPlanInfoEntity;
import com.slfinance.shanlincaifu.entity.RepaymentRecordInfoEntity;
import com.slfinance.shanlincaifu.entity.SubAccountInfoEntity;
import com.slfinance.shanlincaifu.entity.WealthHoldInfoEntity;
import com.slfinance.shanlincaifu.repository.AccountInfoRepository;
import com.slfinance.shanlincaifu.repository.CustInfoRepository;
import com.slfinance.shanlincaifu.repository.InvestInfoRepository;
import com.slfinance.shanlincaifu.repository.LoanDetailInfoRepository;
import com.slfinance.shanlincaifu.repository.LoanInfoRepository;
import com.slfinance.shanlincaifu.repository.LoanTransferRepository;
import com.slfinance.shanlincaifu.repository.LogInfoEntityRepository;
import com.slfinance.shanlincaifu.repository.PaymentRecordDetailRepository;
import com.slfinance.shanlincaifu.repository.PaymentRecordInfoRepository;
import com.slfinance.shanlincaifu.repository.PurchaseAwardInfoRepository;
import com.slfinance.shanlincaifu.repository.RepayRecordDetailInfoRepository;
import com.slfinance.shanlincaifu.repository.RepaymentPlanInfoRepository;
import com.slfinance.shanlincaifu.repository.RepaymentRecordInfoRepository;
import com.slfinance.shanlincaifu.repository.SubAccountInfoRepository;
import com.slfinance.shanlincaifu.repository.WealthHoldInfoRepository;
import com.slfinance.shanlincaifu.repository.custom.RepaymentPlanInfoRepositoryCustom;
import com.slfinance.shanlincaifu.service.CustAccountService;
import com.slfinance.shanlincaifu.service.FlowNumberService;
import com.slfinance.shanlincaifu.service.LoanRepaymentService;
import com.slfinance.shanlincaifu.service.ParamService;
import com.slfinance.shanlincaifu.service.ProjectPaymentService;
import com.slfinance.shanlincaifu.service.SMSService;
import com.slfinance.shanlincaifu.utils.ArithUtil;
import com.slfinance.shanlincaifu.utils.CommonUtils;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.HttpRequestUtil;
import com.slfinance.shanlincaifu.utils.SubjectConstant;
import com.slfinance.vo.ResultVo;

@Slf4j
@Service("loanRepaymentService")
public class LoanRepaymentServiceImpl implements LoanRepaymentService {
	
	@Autowired
	private LoanInfoRepository loanInfoRepository;
	
	@Autowired
	private RepaymentPlanInfoRepository repaymentPlanInfoRepository;
	
	@Autowired
	private RepaymentPlanInfoRepositoryCustom repaymentPlanInfoRepositoryCustom;
	
	@Autowired
	private CustAccountService custAccountService;
	
	@Autowired
	private FlowNumberService numberService;
	
	@Autowired
	private AccountInfoRepository accountInfoRepository;
	
	@Autowired
	private SubAccountInfoRepository subAccountInfoRepository;
	
	@Autowired
	private SMSService smsService;
	
	@Autowired
	private LogInfoEntityRepository logInfoEntityRepository;
	
	@Override
	public ResultVo creditFreezeRepayment(Map<String, Object> params)
			throws SLException {
		
		return internalLoanRepaymentService.creditFreezeRepayment(params);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResultVo normalRepayment(Map<String, Object> params)
			throws SLException {

		List<RepaymentPlanInfoEntity> waitingRepaymentPlanList = (List<RepaymentPlanInfoEntity>)params.get("waitingRepaymentPlanList");
		String loanId = (String)params.get("loanId");
		
		// 逐笔还款
		for(RepaymentPlanInfoEntity r: waitingRepaymentPlanList) {
			
			Map<String, Object> requestParam = Maps.newConcurrentMap();
			requestParam.put("loanId", loanId);
			requestParam.put("replaymentPlanId", r.getId());
			requestParam.put("userId", Constant.SYSTEM_USER_BACK);
			ResultVo result = internalLoanRepaymentService.normalRepayment(requestParam);
			if(ResultVo.isSuccess(result)) {
				// 发送短信
				List<Map<String, Object>> smsList = (List<Map<String, Object>>)result.getValue("data");
				for(Map<String, Object> sms : smsList) {
					smsService.asnySendSMSAndSystemMessage(sms);
				}
			}
			else {
				log.error("借款{}，预计还款日期{}，还款失败，失败原因:{}", r.getLoanEntity().getLoanCode(), r.getExpectRepaymentDate(), result.getValue("message"));
			}
		}
		
		return new ResultVo(true, "还款成功");
	}
	
	

	@Autowired
	private InternalLoanRepaymentService internalLoanRepaymentService;
	
	@Service
	public static class InternalLoanRepaymentService {
		
		@Autowired
		private PurchaseAwardInfoRepository purchaseAwardInfoRepository;
		@Autowired
		private LoanInfoRepository loanInfoRepository;
		
		@Autowired
		private LoanDetailInfoRepository loanDetailInfoRepository;
		
		@Autowired
		private RepaymentPlanInfoRepository repaymentPlanInfoRepository;
		
		@Autowired
		private RepaymentPlanInfoRepositoryCustom repaymentPlanInfoRepositoryCustom;
		
		@Autowired
		private CustAccountService custAccountService;
		
		@Autowired
		private FlowNumberService numberService;
		
		@Autowired
		private AccountInfoRepository accountInfoRepository;
		
		@Autowired
		private SubAccountInfoRepository subAccountInfoRepository;
		
		@Autowired
		private RepaymentRecordInfoRepository repaymentRecordInfoRepository;
		
		@Autowired
		private RepayRecordDetailInfoRepository repayRecordDetailInfoRepository;
		
		@Autowired
		private LogInfoEntityRepository logInfoEntityRepository;
		
		@Autowired
		private WealthHoldInfoRepository wealthHoldInfoRepository;
		
		@Autowired
		private ProjectPaymentService projectPaymentService;
		
		@Autowired
		private CustInfoRepository custInfoRepository;
		
		@Autowired
		private InvestInfoRepository investInfoRepository;
		
		@PersistenceContext
		private EntityManager manager;
		
		@Autowired
		ParamService paramService;
		
		@Autowired
		LoanTransferRepository loanTransferRepository;
		
		@Autowired
		PaymentRecordDetailRepository paymentRecordDetailRepository;
		
		@Autowired
		PaymentRecordInfoRepository paymentRecordInfoRepository;
		
		/**
		 * 商务还款冻结
		 *
		 * @author  wangjf
		 * @date    2016年12月22日 下午2:04:48
		 * @param params
		 * @return
		 * @throws SLException
		 */
		@SuppressWarnings("unchecked")
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public ResultVo creditFreezeRepayment(Map<String, Object> params)
				throws SLException {
			String loanNo = (String)params.get("loanNo");		
			List<Map<String, Object>> repaymentList = (List<Map<String, Object>>)params.get("repaymentList");
			Date now = new Date();
			Map<String, Object> result = Maps.newConcurrentMap();
			
			LoanInfoEntity loanInfoEntity = loanInfoRepository.findByLoanCode(loanNo);
			if(loanInfoEntity == null) {
				return new ResultVo(false, "借款不存在");
			}
			if(!Constant.LOAN_STATUS_08.equals(loanInfoEntity.getLoanStatus())) {
				return new ResultVo(false, "项目非正常状态，不能做正常还款");
			}
			
			// 还款计划按照期数排序
			Collections.sort(repaymentList, new Comparator<Map<String, Object>>(){
				@Override
				public int compare(Map<String, Object> o1,
						Map<String, Object> o2) {
					
					return Integer.parseInt(o1.get("currentTerm").toString()) - Integer.parseInt(o2.get("currentTerm").toString());
				}
			});
			
			// 校验数据合法性
			List<RepaymentPlanInfoEntity> repaymentPlanList = repaymentPlanInfoRepository.findByLoanId(loanInfoEntity.getId());
			List<RepaymentPlanInfoEntity> waitingRepaymentPlanList = Lists.newArrayList();
			List<RepaymentPlanInfoEntity> receiveRepaymentPlanList = Lists.newArrayList();
			BigDecimal totalRepaymentAmount = BigDecimal.ZERO; // 统计总共应还
			for(Map<String, Object> m : repaymentList) {
				boolean isFound = false;
				for(RepaymentPlanInfoEntity r : repaymentPlanList) {
					if(Integer.parseInt(m.get("currentTerm").toString()) == r.getCurrentTerm()
							&& r.getExpectRepaymentDate().equals((String)m.get("expectRepaymentDate"))) {
						
						if(Constant.IS_AMOUNT_FROZEN_YES.equals(r.getIsAmountFrozen())) {
							log.warn(String.format("借款[%s]期数[%s]已经还款，无需重复还款", loanInfoEntity.getLoanCode(), r.getCurrentTerm().toString()));
							continue;
						}
						
						isFound = true;
						// 预期还款时间小于等于当期时间的均为马上需要还款的，剩余的等定时任务处理
						if(now.compareTo(DateUtils.parseDate(r.getExpectRepaymentDate(), "yyyyMMdd")) >= 0) {
							waitingRepaymentPlanList.add(r);
						}
						totalRepaymentAmount = ArithUtil.add(totalRepaymentAmount, r.getRepaymentTotalAmount());
						receiveRepaymentPlanList.add(r);
						break;
					}
				}
				if(!isFound) {
					return new ResultVo(false, String.format("还款数据异常，未找到期数%s，还款时间为%s的待还款数据。", m.get("currentTerm").toString(), (String)m.get("expectRepaymentDate")));
				}
			}
			
			if(totalRepaymentAmount.compareTo(BigDecimal.ZERO) == 0) {
				return new ResultVo(false, "请勿重复还款");
			}
			
			String requestNo = numberService.generateTradeBatchNumber();
			// 意真金融 | 拿米金融 的标的需要先从公司账号划款到借款人账户
			if (Constant.DEBT_SOURCE_NMJR.equals(loanInfoEntity.getCompanyName()) 
					|| Constant.DEBT_SOURCE_YZJR.equals(loanInfoEntity.getCompanyName())
					|| Constant.DEBT_SOURCE_JLJR.equals(loanInfoEntity.getCompanyName())) {
				String loginName = Constant.COMPANY_LOGIN_NAME_NMJR;
				if (Constant.DEBT_SOURCE_YZJR.equals(loanInfoEntity.getCompanyName())) {
					loginName = Constant.COMPANY_LOGIN_NAME_YZJR;
				}
				if (Constant.DEBT_SOURCE_JLJR.equals(loanInfoEntity.getCompanyName())){
					loginName = Constant.COMPANY_LOGIN_NAME_JLJR;
				}
				// 1. 获取公司账户信息和借款人账户信息
				CustInfoEntity companyCustInfo = custInfoRepository.findByLoginName(loginName);
				// 公司账户
				AccountInfoEntity companyAccount = accountInfoRepository.findByCustId(companyCustInfo.getId()); 
				// 借款人账户
				AccountInfoEntity loanerAccount = accountInfoRepository.findByCustId(loanInfoEntity.getCustId());
				if(loanerAccount.getAccountAvailableAmount().compareTo(totalRepaymentAmount) < 0) {
					if(companyAccount.getAccountAvailableAmount().compareTo(totalRepaymentAmount) >= 0) {
						// 2. 从公司账号划款到借款人账户
				        custAccountService.updateAccount(companyAccount, null, loanerAccount, 
								null, "1", SubjectConstant.TRADE_FLOW_TYPE_REPAYMENT_RECHARGE_LOAN, 
								requestNo, totalRepaymentAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_REPAYMENT_RECHARGE_LOAN, 
								Constant.TABLE_BAO_T_LOAN_INFO, loanInfoEntity.getId(),  
								String.format("优选项目[%s]还款充值", loanInfoEntity.getLoanCode()), Constant.SYSTEM_USER_BACK);
					}
					else {
						return new ResultVo(false, "还款失败，公司账户余额不足，请先充值。");
					}
				}
			} else {
				AccountInfoEntity accountInfoEntity = accountInfoRepository.findByCustId(loanInfoEntity.getCustId());
				custAccountService.updateAccount(accountInfoEntity, null, null, 
						null, "5", SubjectConstant.TRADE_FLOW_TYPE_RECHARGE_LOAN, 
						requestNo, totalRepaymentAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_RECHARGE_LOAN, 
						Constant.TABLE_BAO_T_LOAN_INFO, loanInfoEntity.getId(),  
						String.format("优选项目[%s]充值", loanInfoEntity.getLoanCode()), Constant.SYSTEM_USER_BACK);
			}
			
			Map<String, Object> freezeParam = Maps.newHashMap();
			freezeParam.put("requestNo", requestNo);
			freezeParam.put("totalRepaymentAmount", totalRepaymentAmount);
			freezeParam.put("loan", loanInfoEntity);
			freezeParam.put("repaymentPlanList", receiveRepaymentPlanList);
			freezeParam.put("userId", Constant.SYSTEM_USER_BACK);
			freezeRepayment(freezeParam); // 还款冻结
			
			result.put("waitingRepaymentPlanList", waitingRepaymentPlanList);
			result.put("loanId", loanInfoEntity.getId());
			return new ResultVo(true, "还款冻结成功", result);
		}
		
		/**
		 * 还款冻结
		 *
		 * @author  wangjf
		 * @date    2016年12月22日 下午2:04:36
		 * @param params
		 * @return
		 */
		@SuppressWarnings("unchecked")
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public ResultVo freezeRepayment(Map<String, Object> params) throws SLException{
			
			String requestNo = (String)params.get("requestNo");
			BigDecimal totalRepaymentAmount = (BigDecimal)params.get("totalRepaymentAmount");
			LoanInfoEntity loanInfoEntity = (LoanInfoEntity)params.get("loan");
			List<RepaymentPlanInfoEntity> repaymentPlanList = (List<RepaymentPlanInfoEntity>)params.get("repaymentPlanList");
			String userId = (String)params.get("userId");
			
			// 借款人主账户入账到分账户
			AccountInfoEntity accountInfoEntity = accountInfoRepository.findByCustId(loanInfoEntity.getCustId());
			if(accountInfoEntity.getAccountAvailableAmount().compareTo(totalRepaymentAmount) < 0) {
				return new ResultVo(false, "还款账户余额不足，不允许还款");
			}
			
			SubAccountInfoEntity subAccountInfoEntity = subAccountInfoRepository.findByRelatePrimary(loanInfoEntity.getId());
			List<AccountFlowInfoEntity> accountList = custAccountService.updateAccount(accountInfoEntity, null, null, 
					subAccountInfoEntity, "2", SubjectConstant.TRADE_FLOW_TYPE_REPAYMENT_LOAN, 
					requestNo, totalRepaymentAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_REPAYMENT_LOAN, 
					Constant.TABLE_BAO_T_LOAN_INFO, loanInfoEntity.getId(),  
					String.format("优选项目[%s]还款", loanInfoEntity.getLoanCode()), userId);
			
			
			// 借款人分账户冻结		
			List<AccountFlowInfoEntity> freeAccountList = custAccountService.updateAccount(null, subAccountInfoEntity, null, 
					null, "11", SubjectConstant.TRADE_FLOW_TYPE_REPAYMENT_FREEZE_LOAN, 
					requestNo, totalRepaymentAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_REPAYMENT_FREEZE_LOAN, 
					Constant.TABLE_BAO_T_LOAN_INFO, loanInfoEntity.getId(),  
					String.format("优选项目[%s]冻结", loanInfoEntity.getLoanCode()), userId);
			
			// 将还款计划标记为还款冻结
			for(RepaymentPlanInfoEntity r : repaymentPlanList) {
				
				r.setIsAmountFrozen(Constant.IS_AMOUNT_FROZEN_YES);
				r.setMemo(accountList.get(0).getId() + "|" + freeAccountList.get(0).getTradeNo());// 总账id | 冻结流水号
				r.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
			}
			repaymentPlanInfoRepository.save(repaymentPlanList);
		
			// 6) 记录日志
			LogInfoEntity logInfoEntity = new LogInfoEntity();
			logInfoEntity.setRelateType(Constant.TABLE_BAO_T_LOAN_INFO);
			logInfoEntity.setRelatePrimary(loanInfoEntity.getId());
			logInfoEntity.setLogType(Constant.OPERATION_TYPE_31);
			logInfoEntity.setOperDesc("");
			logInfoEntity.setOperPerson(userId);
			logInfoEntity.setOperIpaddress((String)params.get("ipAddress"));
			logInfoEntity.setMemo(String.format("项目%s:%s还款冻结%s元", loanInfoEntity.getLoanType(), loanInfoEntity.getLoanDesc(), totalRepaymentAmount));
			logInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
			logInfoEntityRepository.save(logInfoEntity);
			
			Map<String, Object> rtnMap = Maps.newHashMap();
			rtnMap.put("repaymentTotalAmount", totalRepaymentAmount);
			rtnMap.put("availableAmount", accountInfoEntity.getAccountAvailableAmount());
			return new ResultVo(true, "还款冻结成功", rtnMap);
		}
		
		/**
		 * 正常还款
		 *
		 * @author  wangjf
		 * @date    2016年12月2日 下午1:54:34
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
			LoanInfoEntity loanInfoEntity = loanInfoRepository.findOne(repaymentPlanInfoEntity.getLoanEntity().getId());
			if(loanInfoEntity == null) {
				return new ResultVo(false, "借款不存在");
			}
			if(!Constant.LOAN_STATUS_08.equals(loanInfoEntity.getLoanStatus())) {
				return new ResultVo(false, "项目非正常状态，不能做正常还款");
			}
			
			// 3) 判断是否已经还款冻结
			String reqeustNo = numberService.generateTradeBatchNumber();
			SubAccountInfoEntity subAccountInfoEntity = subAccountInfoRepository.findByRelatePrimary(loanInfoEntity.getId());
			List<AccountFlowInfoEntity> accountList = Lists.newArrayList();
			if(Constant.IS_AMOUNT_FROZEN_YES.equals(repaymentPlanInfoEntity.getIsAmountFrozen())) { // 已经还款冻结
				
				// 对分账户进行解冻
				List<AccountFlowInfoEntity> unFreezeAccountList = custAccountService.updateAccount(null, subAccountInfoEntity, null, 
						null, "12", SubjectConstant.TRADE_FLOW_TYPE_REPAYMENT_UNFREEZE_LOAN, 
						reqeustNo, repaymentPlanInfoEntity.getRepaymentTotalAmount(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_REPAYMENT_UNFREEZE_LOAN, 
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
				AccountInfoEntity accountInfoEntity = accountInfoRepository.findByCustId(loanInfoEntity.getCustId());		
				if(accountInfoEntity.getAccountAvailableAmount().compareTo(repaymentPlanInfoEntity.getRepaymentTotalAmount()) < 0) {
					return new ResultVo(false, "还款账户余额不足，不允许还款");
				}
				
				// 公司主账户->分账户
				accountList = custAccountService.updateAccount(accountInfoEntity, null, null, 
						subAccountInfoEntity, "2", SubjectConstant.TRADE_FLOW_TYPE_REPAYMENT_LOAN, 
						reqeustNo, repaymentPlanInfoEntity.getRepaymentTotalAmount(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_REPAYMENT_LOAN, 
						Constant.TABLE_BAO_T_REPAYMENT_PLAN_INFO, replaymentPlanId,  
						"正常还款", userId);
			}
			
			// 保存借款人还款记录
			saveRepaymentRecord(repaymentPlanInfoEntity.getRepaymentTotalAmount(), loanInfoEntity.getId(), SubjectConstant.TRADE_FLOW_TYPE_REPAYMENT_LOAN,
					accountList.get(0).getId(), Arrays.asList(repaymentPlanInfoEntity), Constant.REPAYMENT_TYPE_01, BigDecimal.ZERO);
				
			// 4) 给投资人和公司还款
			// 还款给投资人
			repaymentForUser(loanInfoEntity, repaymentPlanInfoEntity.getRepaymentPrincipal(), repaymentPlanInfoEntity.getRepaymentInterest(), BigDecimal.ZERO,
                    repaymentPlanInfoEntity, subAccountInfoEntity, reqeustNo, userId, "正常还款", Constant.REPAYMENT_TYPE_01,//增加repaymentPlanInfoEntity --huifei
					loanInfoEntity.getLoanCode(), Arrays.asList(repaymentPlanInfoEntity), smsList,repaymentPlanInfoEntity.getCurrentTerm());
			
			// 5) 给公司收益账户还账户管理费
			if(repaymentPlanInfoEntity.getAccountManageExpense().compareTo(BigDecimal.ZERO) > 0) {
				// 还款给公司收益账户
				AccountInfoEntity earnMainAccount = accountInfoRepository.findOne(Constant.ACCOUNT_ID_ERAN);
				// 还账户管理费(公司)
				List<AccountFlowInfoEntity> earnMainAccountFlowList = custAccountService.updateAccount(null, subAccountInfoEntity, earnMainAccount, 
						null, "3", SubjectConstant.TRADE_FLOW_TYPE_COMPANY_REPAYMENT_ALL_LOAN, 
						reqeustNo, repaymentPlanInfoEntity.getAccountManageExpense(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMPANY_REPAYMENT_ALL_LOAN, 
						Constant.TABLE_BAO_T_REPAYMENT_PLAN_INFO, replaymentPlanId,  
						"正常还款", userId);
				// 保存用户付款明细
				projectPaymentService.saveLoanCompanyPaymentRecord(loanInfoEntity.getId(), earnMainAccount.getCustId(), earnMainAccountFlowList.get(0).getId(), 
						BigDecimal.ZERO, repaymentPlanInfoEntity.getAccountManageExpense(), Constant.REPAYMENT_TYPE_01);
			}

			// 6) 还款计划更新
			repaymentPlanInfoEntity.setTermAlreadyRepayAmount(repaymentPlanInfoEntity.getRepaymentTotalAmount());
			repaymentPlanInfoEntity.setRepaymentStatus(Constant.REPAYMENT_STATUS_CLEAN);
			repaymentPlanInfoEntity.setFactRepaymentDate(new Timestamp(System.currentTimeMillis()));
			repaymentPlanInfoEntity.setBasicModelProperty(userId, false);
			
			// 7) 项目更新
			updateProjectWithRepayment(loanInfoEntity, repaymentPlanInfoEntity);
			
			// 8) 记录日志
			LogInfoEntity logInfoEntity = new LogInfoEntity();
			logInfoEntity.setRelateType(Constant.TABLE_BAO_T_REPAYMENT_PLAN_INFO);
			logInfoEntity.setRelatePrimary(repaymentPlanInfoEntity.getId());
			logInfoEntity.setLogType(Constant.OPERATION_TYPE_33);
			logInfoEntity.setOperDesc("");
			logInfoEntity.setOperPerson(userId);
			logInfoEntity.setOperIpaddress((String)params.get("ipAddress"));
			logInfoEntity.setMemo(String.format("项目%s:%s正常还款%s元", loanInfoEntity.getLoanCode(), loanInfoEntity.getLoanDesc(), repaymentPlanInfoEntity.getRepaymentTotalAmount().toPlainString()));
			logInfoEntity.setBasicModelProperty(userId, true);
			logInfoEntityRepository.save(logInfoEntity);
			
			return new ResultVo(true, "正常还款成功", smsList);
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
		public void saveRepaymentRecord(BigDecimal totalRepaymentAmount, String loanId, String tradeType,
				String accountFlowId, List<RepaymentPlanInfoEntity> planList, String repaymentType, 
				BigDecimal penaltyAmount) {
			RepaymentRecordInfoEntity repaymentRecordInfoEntity = new RepaymentRecordInfoEntity();
			repaymentRecordInfoEntity.setRepayAmount(totalRepaymentAmount);
			repaymentRecordInfoEntity.setHandleStatus(Constant.TRADE_STATUS_03);
			repaymentRecordInfoEntity.setLoanId(loanId);
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
									SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_PENALTY_LOAN, penaltyAmount));
						}
					}
					
					if(Constant.IS_RISKAMOUNT_REPAY_YES.equals(planList.get(i).getIsRiskamountRepay())) { // 风险金垫付
						// 本金
						repayRecordDetailList.add(createRepaymentDetail(repaymentRecordInfoEntity.getId(), planList.get(i).getId(), 
								SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_RISK_REPAYMENT_PRINCIPAL_LOAN, planList.get(i).getRepaymentPrincipal()));
						
						// 利息
						repayRecordDetailList.add(createRepaymentDetail(repaymentRecordInfoEntity.getId(), planList.get(i).getId(), 
								SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_RISK_REPAYMENT_INTEREST_LOAN, planList.get(i).getRepaymentInterest()));
						
						/*// 账户管理费
						repayRecordDetailList.add(createRepaymentDetail(repaymentRecordInfoEntity.getId(), planList.get(i).getId(), 
								SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_RISK_REPAYMENT_EXPENSE_LOAN, planList.get(i).getAccountManageExpense()));*/
					}
					else {
						// 本金
						repayRecordDetailList.add(createRepaymentDetail(repaymentRecordInfoEntity.getId(), planList.get(i).getId(), 
								SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_PRINCIPAL_LOAN, planList.get(i).getRepaymentPrincipal()));
						
						// 利息
						repayRecordDetailList.add(createRepaymentDetail(repaymentRecordInfoEntity.getId(), planList.get(i).getId(), 
								SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_INTEREST_LOAN, planList.get(i).getRepaymentInterest()));
						
						/*// 账户管理费
						repayRecordDetailList.add(createRepaymentDetail(repaymentRecordInfoEntity.getId(), planList.get(i).getId(), 
								SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMPANY_REPAYMENT_EXPENSE_LOAN, planList.get(i).getAccountManageExpense()));*/
					}
				}
				break;
			}
			case Constant.REPAYMENT_TYPE_03://提前还款
			{
				// 剩余本金
				repayRecordDetailList.add(createRepaymentDetail(repaymentRecordInfoEntity.getId(), planList.get(0).getId(), 
						SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_PRINCIPAL_LOAN, ArithUtil.add(planList.get(0).getRepaymentPrincipal(), planList.get(0).getRemainderPrincipal())));
				
				if(planList.get(0).getPenaltyAmount().compareTo(BigDecimal.ZERO) == 0) { // 违约金为0（到期还本付息），给利息
					// 利息
					repayRecordDetailList.add(createRepaymentDetail(repaymentRecordInfoEntity.getId(), planList.get(0).getId(), 
							SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_INTEREST_LOAN, planList.get(0).getRepaymentInterest()));
					
					/*// 账户管理费
					repayRecordDetailList.add(createRepaymentDetail(repaymentRecordInfoEntity.getId(), planList.get(0).getId(), 
							SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMPANY_REPAYMENT_EXPENSE_LOAN, planList.get(0).getAccountManageExpense()));*/
				}
				else {
					// 违约金
					repayRecordDetailList.add(createRepaymentDetail(repaymentRecordInfoEntity.getId(), planList.get(0).getId(), 
							SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_BREACH_LOAN, planList.get(0).getPenaltyAmount()));
				}
				break;
			}
			case Constant.REPAYMENT_TYPE_01://正常还款
			{
				for(int i = 0; i < planList.size(); i ++) {
					// 本金
					repayRecordDetailList.add(createRepaymentDetail(repaymentRecordInfoEntity.getId(), planList.get(i).getId(), 
							SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_PRINCIPAL_LOAN, planList.get(i).getRepaymentPrincipal()));
					
					// 利息
					repayRecordDetailList.add(createRepaymentDetail(repaymentRecordInfoEntity.getId(), planList.get(i).getId(), 
							SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_INTEREST_LOAN, planList.get(i).getRepaymentInterest()));
					
					/*// 账户管理费
					repayRecordDetailList.add(createRepaymentDetail(repaymentRecordInfoEntity.getId(), planList.get(i).getId(), 
							SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMPANY_REPAYMENT_EXPENSE_LOAN, planList.get(i).getAccountManageExpense()));*/
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
									SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_RISK_OVERDUE_PENALTY_LOAN, penaltyAmount));
						}
					}
					
					// 本金
					repayRecordDetailList.add(createRepaymentDetail(repaymentRecordInfoEntity.getId(), planList.get(i).getId(), 
							SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_RISK_OVERDUE_PRINCIPAL_LOAN, planList.get(i).getRepaymentPrincipal()));
					
					// 利息
					repayRecordDetailList.add(createRepaymentDetail(repaymentRecordInfoEntity.getId(), planList.get(i).getId(), 
							SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_RISK_OVERDUE_INTEREST_LOAN, planList.get(i).getRepaymentInterest()));
					
					/*// 账户管理费
					repayRecordDetailList.add(createRepaymentDetail(repaymentRecordInfoEntity.getId(), planList.get(i).getId(), 
							SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_RISK_OVERDUE_EXPENSE_LOAN, planList.get(i).getAccountManageExpense()));*/
				}
				break;
			}
			default :
				// 奖励收益
				repayRecordDetailList.add(createRepaymentDetail(repaymentRecordInfoEntity.getId(), planList.get(0).getId(),
						SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_AWARD_LOAN, totalRepaymentAmount));
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
		@SuppressWarnings("deprecation")
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public void repaymentForUser(LoanInfoEntity loanInfoEntity, BigDecimal totalPrincipal, BigDecimal totalInterest,
                                     BigDecimal totalPenalty, RepaymentPlanInfoEntity repaymentPlanInfoEntity, SubAccountInfoEntity subAccountInfoEntity,
				String reqeustNo, String userId, String memo, String repaymentType,
				String projectNo, List<RepaymentPlanInfoEntity> planList,
				List<Map<String, Object>> smsList,Integer CurrentTerm) throws SLException{

			// update by http://192.16.150.101:8080/browse/SLCF-2823 at 2017-05-18
			// yyyy-MM-dd HH24:mi:ss
			String onlineTime = paramService.findTransferRefromOnlineTimeByParameterName(Constant.TRANSFERRE_FROM_ONLINE_TIME_20170531);
			Date onlineTimeDate = DateUtils.parseDate(onlineTime, "yyyy-MM-dd HH:mm:ss");
			if(onlineTimeDate == null){
				throw new SLException("债权转让时间取得出错");
			}
			String loanId = loanInfoEntity.getId();
			List<WealthHoldInfoEntity> holdList = wealthHoldInfoRepository.findByLoanId(loanId);
			List<AccountInfoEntity> accountList =  accountInfoRepository.findAccountByLoanId(loanId);
			List<CustInfoEntity> custList = custInfoRepository.findAllCustByLoanId(loanId);
			List<SubAccountInfoEntity> subAccountList = subAccountInfoRepository.findSubAccountWithInvestByLoanId(loanId);
			
			LoanDetailInfoEntity loanDetail= loanInfoEntity.getLoanDetailInfoEntity();
			// 上个还款日，本期还款日
			final Date lastExpiry = loanDetail.getLastExpiry() == null? loanInfoEntity.getInvestStartDate() :loanDetail.getLastExpiry();
			final Date nextExpiry = loanDetail.getNextExpiry();
			
//			List<WealthHoldInfoEntity> holdList = wealthHoldInfoRepository.findByLoanIdAndHoldStatus(loanId, Constant.HOLD_STATUS_01);
//			List<AccountInfoEntity> accountList =  accountInfoRepository.findAllAccountByLoanId(loanId, Constant.HOLD_STATUS_01);
//			List<CustInfoEntity> custList = custInfoRepository.findAllCustByLoanId(loanId, Constant.HOLD_STATUS_01);
//			List<SubAccountInfoEntity> subAccountList = subAccountInfoRepository.findAllSubAccountByLoanId(loanId, Constant.HOLD_STATUS_01);
			
			List<InvestInfoEntity> investList = investInfoRepository.findByLoanId(loanId);
			
			BigDecimal tmpTotalPrincipalAmount = BigDecimal.ZERO;
			BigDecimal tmpTotalInterestAmount = BigDecimal.ZERO;
			BigDecimal tmpTotalPenaltyAmount = BigDecimal.ZERO;

			
			for(int i = 0; i < holdList.size(); i ++) {
				
				WealthHoldInfoEntity invest = holdList.get(i);
				// 通过投资找到用户账户
				AccountInfoEntity userAccount = findUserAccountByInvest(accountList, invest.getCustId());
				CustInfoEntity user = findUserByInvest(custList, invest.getCustId());
				SubAccountInfoEntity userSubAccount = findUserSubAccountByInvest(subAccountList, invest.getInvestId());
				InvestInfoEntity investInfoEntity = findInvestById(investList, invest.getInvestId());
				//公司红包主账户
                AccountInfoEntity companyAccount = accountInfoRepository.findByCustId(Constant.CUST_ID_RED_ENVELOP);
                PurchaseAwardInfoEntity purchaseAwardInfoEntity = purchaseAwardInfoRepository
						.findByCustIdAndInvestIdAndCurrentTermAndAwardStatus(
								invest.getCustId(), invest.getInvestId(),
								CurrentTerm,Constant.PURCHASE_AWARD_STATUS_NO);
                /** yangc  取对应加息券奖励记录发放给投资人**/
                if(null != purchaseAwardInfoEntity){

					//更新账户、记录账户流水
					custAccountService
							.updateAccount(
									companyAccount,
									null,
									userAccount,
									null,
									"1",
									SubjectConstant.TRADE_FLOW_TYPE_PURCHASE_AWARD_RELEASE,
									reqeustNo,
									purchaseAwardInfoEntity.getAwardAmount(),
									SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_PURCHASE_AWARD_RELEASE,
									Constant.TABLE_BAO_T_PURCHASE_AWARD,
									purchaseAwardInfoEntity.getId(), "加息券奖励金",
									userId);
					//更新奖励表状态
					purchaseAwardInfoEntity.setAwardStatus(Constant.PURCHASE_AWARD_STATUS_YES);
				}
				
				//----------------------------改造开始----------------------------
				/*流程方向，具体数据可能有出入(比如转让前后的 “持有比例” 是不一致的，所以aa的描述不确切)
				1.invest_mode加入(是否转让通过持有表转让比例是否为0判断)
				1)未转让，持有比例*当期本息 
				2)已转让
				  aa.转让时间<上线时间，持有比例*当期本息 (补充：上线时时间前的转让，已经结算过了)
				  ab.转让时间>=上线时间
					选出上个还款日<转让时间<=下个还款日(从转让表里查询)
					i.非还款日[转让时间！=下个还款日](转让时间-上个还款日)/(下个还款日-上个还款日)*转让比例*当期本息
					ii.还款日(未还款的情况下转让时间<还款计划表实际还款时间) (转让时间-上个还款日-1)/(下个还款日-上个还款日)*转让比例*当期本息

				2、invest_mode转让
				1)未转让
				非购买当期  持有比例*当期本息 
				购买当期
				  ba.非还款日当天购买(下个还款日-转让时间)/(下个还款日-上个还款日）*持有比例*当期本息
				  bb.还款日当天购买(未还款的情况下)1/(下个还款日-上个还款日)*持有比例*当期本息
				2)转让
					同[invest_mode加入]的[2)已转让]
				总结：待收收益等于 = 剩余持比的收益(如果当月是转让买进=剩余持比当月的收益+剩余收益) + 转让人当月转出持比的收益
				*/
				// 用户应得本金、利息、违约金
				double dPrincipalAmount = 0;
				double dInterestAmount = 0;
				double dPenaltyAmount = 0;

				// 用户应得标的加息奖励金 --huifei
				double dAwardAmount = 0.0;
				BigDecimal totalAwardAmount = CommonUtils.emptyToDecimal(repaymentPlanInfoEntity.getAwardAmount());

				// 1
				if(Constant.INVEST_METHOD_JOIN.equals(investInfoEntity.getInvestMode())){
					List<LoanTransferEntity> transferList = loanTransferRepository.findBySenderHoldIdAndCreateDate(invest.getId(), lastExpiry, nextExpiry);
					// 1.1) // 持有比例(如果有转让，就是剩余持比)当月收益    (剩余持比当月的收益)
					dPrincipalAmount = totalPrincipal.doubleValue()*invest.getHoldScale().doubleValue();
					dInterestAmount = totalInterest.doubleValue()*invest.getHoldScale().doubleValue();
					dPenaltyAmount = totalPenalty.doubleValue()*invest.getHoldScale().doubleValue();

					//标的加息奖励金 --huifei
					if (totalAwardAmount.compareTo(BigDecimal.ZERO) > 0 && Constant.DEBT_SOURCE_XCJF.equals(loanInfoEntity.getCompanyName())) {
						dAwardAmount = totalAwardAmount.doubleValue()*invest.getHoldScale().doubleValue();
					}

					if(transferList == null || transferList.size() ==0){
						// 没有转出持有比例不变，上面就已经算过了
					} else {
						// 1.2)
						for(int ti = 0; ti < transferList.size(); ti++){
							LoanTransferEntity loanTransfer = transferList.get(ti);
							Date transferDate = loanTransfer.getCreateDate();
							// aa
							// 有的转让在上线，上线前的转让，受让人购买时已经结算
							if(transferDate.compareTo(onlineTimeDate) < 0) {
//								dPrincipalAmount += totalPrincipal.doubleValue()*loanTransfer.getTradeScale().doubleValue();
//								dInterestAmount += totalInterest.doubleValue()*loanTransfer.getTradeScale().doubleValue();
//								dPenaltyAmount += totalPenalty.doubleValue()*loanTransfer.getTradeScale().doubleValue();
							} else {
								// 受让人起息真正日期
								WealthHoldInfoEntity transferHold = wealthHoldInfoRepository.findOne(loanTransfer.getReceiveHoldId());
								InvestInfoEntity transferInvest = investInfoRepository.findOne(transferHold.getInvestId());
								// 购买时如果是实际还款延期一两天，投资表的生效日期是还款计划表的计划还款日的日期
								String transferEffectDate = transferInvest.getEffectDate();
								transferEffectDate = transferEffectDate.substring(0, 4).concat("-").concat(transferEffectDate.substring(4, 6)).concat("-").concat(transferEffectDate.substring(6));
								
								// ab
								int holdDays = DateUtils.datePhaseDiffer(DateUtils.truncateDate(lastExpiry), DateUtils.truncateDate(DateUtils.parseStandardDate(transferEffectDate)));
								int daysOfmonth = DateUtils.datePhaseDiffer(DateUtils.truncateDate(lastExpiry), DateUtils.truncateDate(nextExpiry));
								// 现在是还款，还款后购买的数据不会出来(~\(≧▽≦)/~)
								// 如果是还款日未还款之前购买则让一天利息（还款可能出现延期一两天）
								if(transferEffectDate.compareTo(DateUtils.formatDate(nextExpiry, "yyyy-MM-dd")) >= 0) {
									holdDays = holdDays - 1;
								}
								// 转让人当月转出持比的收益, 只有利息，本金转让时已结算
//								dPrincipalAmount += totalPrincipal.doubleValue()*loanTransfer.getTradeScale().doubleValue()*holdDays/daysOfmonth;
								dInterestAmount += totalInterest.doubleValue()*loanTransfer.getTradeScale().doubleValue()*holdDays/daysOfmonth;
								dPenaltyAmount += totalPenalty.doubleValue()*loanTransfer.getTradeScale().doubleValue()*holdDays/daysOfmonth;

								//标的加息奖励金 --huifei 暂时屏蔽债转部分
								/*if (totalAwardAmount.compareTo(BigDecimal.ZERO) > 0 && Constant.DEBT_SOURCE_XCJF.equals(loanInfoEntity.getCompanyName())) {
									dAwardAmount += totalAwardAmount.doubleValue()*loanTransfer.getTradeScale().doubleValue()*holdDays/daysOfmonth;
								}*/
							}
						}
					}
				} else {// 转让标,购买是不是在当期，当期有没有转出
					// 2 剩余持比的收益(如果当月是转让买进=剩余持比当月的收益+剩余收益) + 转让人当月转出持比的收益
					// 购买时如果是实际还款延期一两天，投资表的生效日期是还款计划表的计划还款日的日期
					String effectDate = investInfoEntity.getEffectDate();
					effectDate = effectDate.substring(0, 4).concat("-").concat(effectDate.substring(4, 6)).concat("-").concat(effectDate.substring(6));
					// 非购买当期  持有比例*当期本息 
					if(DateUtils.parseStandardDate(effectDate).compareTo(DateUtils.truncateDate(onlineTimeDate)) < 0 || DateUtils.parseStandardDate(effectDate).compareTo(DateUtils.truncateDate(lastExpiry)) < 0){
						dPrincipalAmount = totalPrincipal.doubleValue()*invest.getHoldScale().doubleValue();
						dInterestAmount = totalInterest.doubleValue()*invest.getHoldScale().doubleValue();
						dPenaltyAmount = totalPenalty.doubleValue()*invest.getHoldScale().doubleValue();

						//标的加息奖励金 --huifei
						if (totalAwardAmount.compareTo(BigDecimal.ZERO) > 0 && Constant.DEBT_SOURCE_XCJF.equals(loanInfoEntity.getCompanyName())) {
							dAwardAmount = totalAwardAmount.doubleValue()*invest.getHoldScale().doubleValue();
						}

					} else {
						int holdDays = DateUtils.datePhaseDiffer(DateUtils.truncateDate(DateUtils.parseStandardDate(effectDate)), DateUtils.truncateDate(nextExpiry));
						int daysOfmonth = DateUtils.datePhaseDiffer(DateUtils.truncateDate(lastExpiry), DateUtils.truncateDate(nextExpiry));
						// 现在是还款，还款后购买的数据不会出来(~\(≧▽≦)/~)
						if(effectDate.compareTo(DateUtils.formatDate(nextExpiry, "yyyy-MM-dd")) == 0){
							holdDays = 1;
						}
						dPrincipalAmount = totalPrincipal.doubleValue()*invest.getHoldScale().doubleValue();// 本金不用乘以holdDays/daysOfmonth
						dInterestAmount = totalInterest.doubleValue()*invest.getHoldScale().doubleValue()*holdDays/daysOfmonth;
						dPenaltyAmount = totalPenalty.doubleValue()*invest.getHoldScale().doubleValue()*holdDays/daysOfmonth;

						//标的加息奖励金 --huifei
						if (totalAwardAmount.compareTo(BigDecimal.ZERO) > 0 && Constant.DEBT_SOURCE_XCJF.equals(loanInfoEntity.getCompanyName())) {
							dAwardAmount += totalAwardAmount.doubleValue()*invest.getTradeScale().doubleValue()*holdDays/daysOfmonth;
						}

					}
					
					// 查询是否有转出
					List<LoanTransferEntity> transferList = loanTransferRepository.findBySenderHoldIdAndCreateDate(invest.getId(), lastExpiry, nextExpiry);
					// 2.2)
					if(transferList == null || transferList.size() ==0){
						// 没有转出持有比例不变，上面就已经算过了
					} else {
						// 这比债权转出比例的受益
						for(int ti = 0; ti < transferList.size(); ti++){
							LoanTransferEntity loanTransfer = transferList.get(ti);
							Date transferDate = loanTransfer.getCreateDate();
							// aa
							// 有的转让在上线，上线前的转让，受让人购买时已经结算
							if(transferDate.compareTo(onlineTimeDate) < 0){
//								dPrincipalAmount += totalPrincipal.doubleValue()*loanTransfer.getTradeScale().doubleValue();
//								dInterestAmount += totalInterest.doubleValue()*loanTransfer.getTradeScale().doubleValue();
//								dPenaltyAmount += totalPenalty.doubleValue()*loanTransfer.getTradeScale().doubleValue();
							} else {
								// 受让人起息真正日期
								WealthHoldInfoEntity transferHold = wealthHoldInfoRepository.findOne(loanTransfer.getReceiveHoldId());
								InvestInfoEntity transferInvest = investInfoRepository.findOne(transferHold.getInvestId());
								// 购买时如果是实际还款延期一两天，投资表的生效日期是还款计划表的计划还款日的日期
								String transferEffectDate = transferInvest.getEffectDate();
								transferEffectDate = transferEffectDate.substring(0, 4).concat("-").concat(transferEffectDate.substring(4, 6)).concat("-").concat(transferEffectDate.substring(6));
								// ab
								int holdDays = DateUtils.datePhaseDiffer(DateUtils.truncateDate(lastExpiry), DateUtils.truncateDate(DateUtils.parseStandardDate(transferEffectDate)));
								int daysOfmonth = DateUtils.datePhaseDiffer(DateUtils.truncateDate(lastExpiry), DateUtils.truncateDate(nextExpiry));
								// 现在是还款，还款后购买的数据不会出来(~\(≧▽≦)/~)
								// 如果是还款日未还款之前购买则让一天利息（还款可能出现延期一两天）
								if(transferEffectDate.compareTo(DateUtils.formatDate(nextExpiry, "yyyy-MM-dd")) >= 0) {
									holdDays = holdDays - 1;
								}
								
//								dPrincipalAmount += totalPrincipal.doubleValue()*loanTransfer.getTradeScale().doubleValue()*holdDays/daysOfmonth;
								dInterestAmount += totalInterest.doubleValue()*loanTransfer.getTradeScale().doubleValue()*holdDays/daysOfmonth;
								dPenaltyAmount += totalPenalty.doubleValue()*loanTransfer.getTradeScale().doubleValue()*holdDays/daysOfmonth;

								//标的加息奖励金 --huifei 暂时屏蔽债转部分
								/*if (totalAwardAmount.compareTo(BigDecimal.ZERO) > 0 && Constant.DEBT_SOURCE_XCJF.equals(loanInfoEntity.getCompanyName())) {
									dAwardAmount += totalAwardAmount.doubleValue()*loanTransfer.getTradeScale().doubleValue()*holdDays/daysOfmonth;
								}*/

							}
						}
					}
				}
				
				BigDecimal principalAmount = ArithUtil.formatScale(new BigDecimal(String.valueOf(dPrincipalAmount)), 8);
				BigDecimal interestAmount = ArithUtil.formatScale(new BigDecimal(String.valueOf(dInterestAmount)), 8);
				BigDecimal penaltyAmount = ArithUtil.formatScale(new BigDecimal(String.valueOf(dPenaltyAmount)), 8);

				//标的加息奖励金 --huifei
				BigDecimal investAwardAmount = BigDecimal.ZERO;
				if (dAwardAmount > 0) {
					investAwardAmount = ArithUtil.formatScale(new BigDecimal(String.valueOf(dAwardAmount)), 8);
				}

				if (i != holdList.size() - 1) { // 非最后一条记录
					tmpTotalPrincipalAmount = ArithUtil.add(tmpTotalPrincipalAmount, principalAmount);
					tmpTotalInterestAmount = ArithUtil.add(tmpTotalInterestAmount, interestAmount);
					tmpTotalPenaltyAmount = ArithUtil.add(tmpTotalPenaltyAmount, penaltyAmount);
				}
				else { // 最后一个投资人所得 = 总额-前面所有人总额
					principalAmount = ArithUtil.sub(totalPrincipal, tmpTotalPrincipalAmount);
					interestAmount = ArithUtil.sub(totalInterest, tmpTotalInterestAmount);
					penaltyAmount = ArithUtil.sub(totalPenalty, tmpTotalPenaltyAmount);
				}
//				// 用户应得本金、利息、违约金
//				double dPrincipalAmount = totalPrincipal.doubleValue()*invest.getHoldScale().doubleValue();
//				double dInterestAmount = totalInterest.doubleValue()*invest.getHoldScale().doubleValue();
//				double dPenaltyAmount = totalPenalty.doubleValue()*invest.getHoldScale().doubleValue();
//				BigDecimal principalAmount = new BigDecimal(String.valueOf(dPrincipalAmount));
//				BigDecimal interestAmount = new BigDecimal(String.valueOf(dInterestAmount));
//				BigDecimal penaltyAmount = new BigDecimal(String.valueOf(dPenaltyAmount));
//				
//				principalAmount = ArithUtil.formatScale(principalAmount, 8);
//				interestAmount = ArithUtil.formatScale(interestAmount, 8);
//				penaltyAmount = ArithUtil.formatScale(penaltyAmount, 8);
//				
//				if (i != holdList.size() - 1) { // 非最后一条记录
//					tmpTotalPrincipalAmount = ArithUtil.add(tmpTotalPrincipalAmount, principalAmount);
//					tmpTotalInterestAmount = ArithUtil.add(tmpTotalInterestAmount, interestAmount);
//					tmpTotalPenaltyAmount = ArithUtil.add(tmpTotalPenaltyAmount, penaltyAmount);
//				}
//				else { // 最后一个投资人所得 = 总额-前面所有人总额
//					principalAmount = ArithUtil.sub(totalPrincipal, tmpTotalPrincipalAmount);
//					interestAmount = ArithUtil.sub(totalInterest, tmpTotalInterestAmount);
//					penaltyAmount = ArithUtil.sub(totalPenalty, tmpTotalPenaltyAmount);
//				}
				//----------------------------改造结束----------------------------
				
				// 账户更新及记录流水
				BigDecimal totalAmount = ArithUtil.add(ArithUtil.add(principalAmount, interestAmount), penaltyAmount);
				// 金额为零就不往下走（1.在上个还款日之前全转的。。。）
				if(totalAmount.compareTo(new BigDecimal("0.01")) < 0){
					continue;
				}
				
				List<AccountFlowInfoEntity> userAccountFlowList = custAccountService.updateAccount(null, subAccountInfoEntity, null, 
						userSubAccount, "4", SubjectConstant.TRADE_FLOW_TYPE_USER_REPAYMENT_ALL_LOAN, 
						reqeustNo, totalAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_ALL_LOAN, 
						Constant.TABLE_BAO_T_INVEST_INFO, invest.getInvestId(),  
						memo, userId);


				/*begin 橙信贷给投资人主账户打入加息奖励金--huifei*/
				if (investAwardAmount.compareTo(BigDecimal.ZERO) > 0 && Constant.DEBT_SOURCE_XCJF.equals(loanInfoEntity.getCompanyName())) {

					//从公司红包主账户给投资人奖励金额（产生2笔流水，1公司红包主账户支出流水，2投资人主账户收入流水）
					List<AccountFlowInfoEntity> userAccountFlow = custAccountService.updateAccount(companyAccount, null,
							userAccount, null, "1",
							SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_AWARD_LOAN, reqeustNo, investAwardAmount,
							SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_AWARD_LOAN, Constant.TABLE_BAO_T_REPAYMENT_PLAN_INFO, repaymentPlanInfoEntity.getId(),
							"橙信贷奖励金" ,userId);
				}
                /*end 橙信贷给投资人主账户打入加息奖励金--huifei*/
				
				// 保存用户付款明细
				//修改参数awardAmount(原值BigDecimal.ZERO)为investAwardAmount --huifei
				List<PaymentRecordDetailEntity> paymentRecords = projectPaymentService.saveLoanUserPaymentRecord(loanId, userAccount.getCustId(), userAccountFlowList.get(0).getId(),
						principalAmount, interestAmount, penaltyAmount, investAwardAmount, repaymentType, SubjectConstant.TRADE_FLOW_TYPE_USER_REPAYMENT_ALL_LOAN);


				invest.setReceivedAmount(ArithUtil.add(invest.getReceivedAmount(), totalAmount));
				
				if(!StringUtils.isEmpty(investInfoEntity.getLoanId())) { // 用于借款的投资，将分账户打至主账户
					custAccountService.updateAccount(null, userSubAccount, userAccount, 
							null, "3", SubjectConstant.TRADE_FLOW_TYPE_USER_REPAYMENT_ALL_LOAN, 
							reqeustNo, totalAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_ALL_LOAN, 
							Constant.TABLE_BAO_T_INVEST_INFO, invest.getInvestId(),  
							memo, userId);	
					
					//TODO 新手标 加息
					if(Constant.LOAN_INFO_NEWER_FLAG.equals(loanInfoEntity.getNewerFlag()) && BigDecimal.ZERO.compareTo(CommonUtils.emptyToDecimal(loanInfoEntity.getAwardRate())) < 0){
						awardAmountToUser(reqeustNo,investInfoEntity.getId(),userId,userAccount,loanId);
						BigDecimal awardAmount = repaymentPlanInfoRepositoryCustom.findAwardRateInfoForNewerFlag(investInfoEntity.getId());
					    awardAmount = ArithUtil.formatScale(new BigDecimal(String.valueOf(awardAmount)), 8);
						totalAmount = ArithUtil.add(totalAmount, awardAmount);
					}
				}
				else {
					log.warn("该笔投资{}赞不返回给投资人", investInfoEntity.getId());
					continue;
				}

				//短信中的总金额增加 --huifei
				if (investAwardAmount.compareTo(BigDecimal.ZERO) > 0 && Constant.DEBT_SOURCE_XCJF.equals(loanInfoEntity.getCompanyName())) {
					totalAmount = ArithUtil.add(totalAmount, investAwardAmount);
				}

				// 准备短信内容
				switch(repaymentType) {
				case Constant.REPAYMENT_TYPE_01: // 正常还款
				case Constant.REPAYMENT_TYPE_02: // 逾期还款
				case Constant.REPAYMENT_TYPE_04: // 风险金垫付
				{
					// 发送回款短信
					String term = ""; // 取得期数，多期时使用逗号隔开
					for(int j = 0; j < planList.size(); j ++) {
						if(term.isEmpty()) {
							term += planList.get(j).getCurrentTerm().toString();
						}
						else {
							term += "," + planList.get(j).getCurrentTerm().toString();
						}
					}
					Map<String, Object> smsParams = new HashMap<String, Object>();
					smsParams.put("mobile", user.getMobile());
					smsParams.put("custId", user.getId());		
					smsParams.put("messageType", Constant.SMS_TYPE_LOAN_NORMAL_REPAYMENT);
					smsParams.put("values", new Object[] { // 短信息内容
							String.format("%s-%s", loanInfoEntity.getLoanType(), loanInfoEntity.getLoanDesc()),
							term,								
							ArithUtil.formatScale(totalAmount, 2).toPlainString()});
					smsParams.put("systemMessage", new Object[] { // 站内信内容
							String.format("%s-%s", loanInfoEntity.getLoanType(), loanInfoEntity.getLoanDesc()),
							term,
							ArithUtil.formatScale(totalAmount, 2).toPlainString()});
					smsList.add(smsParams);
					
					if(user.getOpenid()!=null&&user.getOpenid()!=""){
						//向微信公众号推送回款信息
						Map<String, Object> params = new HashMap<String, Object>();
						params.put("openId", user.getOpenid());
						params.put("template_id", Constant.SMS_TYPE_VX_5);
						StringBuilder sb=new StringBuilder()
						
						.append(String.format("%s-%s", loanInfoEntity.getLoanType(), loanInfoEntity.getLoanDesc()))
						.append("|")
						.append(term)
						.append("|")
						.append(ArithUtil.formatScale(totalAmount, 2).toPlainString());
						
						//加密加签
						params.put("data", java.net.URLEncoder.encode(sb.toString()));
						String sign=HttpRequestUtil.sign((String)params.get("openId"),(String)params.get("template_id"), sb.toString());
						params.put("sign", sign);
						
						HttpRequestUtil.pushSmsToVX(params);
					}
					
					break;
				}	
				case Constant.REPAYMENT_TYPE_03: // 提前还款
				{
					/*Map<String, Object> smsParams = new HashMap<String, Object>();
					smsParams.put("mobile", user.getMobile());
					smsParams.put("custId", user.getId());	
					smsParams.put("messageType", Constant.SMS_TYPE_PROJECT_EARLY_REPAYMENT);
					smsParams.put("values", new Object[] { // 短信息内容
							DateUtils.formatDate((Date)invest.get("tradeDate"), "yyyy-MM-dd"),
							projectNo,
							ArithUtil.formatScale(totalAmount, 2).toPlainString()});
					smsParams.put("systemMessage", new Object[] { // 站内信内容
							DateUtils.formatDate((Date)invest.get("tradeDate"), "yyyy-MM-dd"),
							projectNo,
							ArithUtil.formatScale(totalAmount, 2).toPlainString()});
					smsList.add(smsParams);*/
					break;
				}
				}
				
			}
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
		public void updateProjectWithRepayment(LoanInfoEntity loanInfoEntity, RepaymentPlanInfoEntity repaymentPlanInfoEntity) {
			List<RepaymentPlanInfoEntity> planList = repaymentPlanInfoRepository.findByLoanId(loanInfoEntity.getId());
			RepaymentPlanInfoEntity nextRepaymentPlan = null;
			for(int i = 0; i < planList.size(); i ++) {
				if(repaymentPlanInfoEntity.getId().equals(planList.get(i).getId())) {
					if(i == planList.size() - 1) { // 若是最后一期，则下个还款日不存在
						nextRepaymentPlan = null;
					}
					else { // 否则取当期的下个还款日
						nextRepaymentPlan = planList.get(i + 1);
					}
					break;
				}
			}
				
			LoanDetailInfoEntity loanDetailInfoEntity = loanInfoEntity.getLoanDetailInfoEntity();
			loanDetailInfoEntity.setLastExpiry(new Timestamp(DateUtils.parseDate(repaymentPlanInfoEntity.getExpectRepaymentDate(), "yyyyMMdd").getTime()));
			if(nextRepaymentPlan != null) {
				loanDetailInfoEntity.setCurrTerm(new BigDecimal(nextRepaymentPlan.getCurrentTerm().toString()));
				loanDetailInfoEntity.setCurrTremEndDate(new Timestamp(DateUtils.parseDate(nextRepaymentPlan.getExpectRepaymentDate(), "yyyyMMdd").getTime()));
				loanDetailInfoEntity.setNextExpiry(new Timestamp(DateUtils.parseDate(nextRepaymentPlan.getExpectRepaymentDate(), "yyyyMMdd").getTime()));
			}
			else {
				loanDetailInfoEntity.setCreditRightStatus(Constant.CREDIT_RIGHT_STATUS_CLEAN);
			}
			loanDetailInfoEntity.setAlreadyPaymentTerm(new BigDecimal(repaymentPlanInfoEntity.getCurrentTerm().toString()));
			loanDetailInfoEntity.setCreditRemainderPrincipal(repaymentPlanInfoEntity.getRemainderPrincipal());
			
			// 若是最后一期，则项目状态改为已到期
			if(repaymentPlanInfoEntity.getId().equals(planList.get(planList.size() - 1).getId())) {
				loanInfoEntity.setLoanStatus(Constant.LOAN_STATUS_11);
				loanInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
				
				// 更新债权持有情况
				List<WealthHoldInfoEntity> holdList = wealthHoldInfoRepository.findByLoanId(loanInfoEntity.getId());
				for(WealthHoldInfoEntity w : holdList) {
					w.setHoldStatus(Constant.HOLD_STATUS_05);
					w.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
				}
				
				// 投资记录均置为已到期
				List<InvestInfoEntity> investList = investInfoRepository.findByLoanId(loanInfoEntity.getId());
				for(InvestInfoEntity i : investList) {
					if(Constant.INVEST_STATUS_EARN.equals(i.getInvestStatus())) { // 收益中改为已到期
						i.setInvestStatus(Constant.INVEST_STATUS_END);
						i.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
					}
				}
			}
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
		 * 通过投资查找用户
		 *
		 * @author  wangjf
		 * @date    2016年12月2日 下午4:06:44
		 * @param accountList
		 * @param custId
		 * @return
		 * @throws SLException
		 */
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public CustInfoEntity findUserByInvest(final List<CustInfoEntity> accountList, final String custId) throws SLException{
					
			CustInfoEntity userAccount = null;
			for(CustInfoEntity account : accountList) {
				if(account.getId().equals(custId)) {
					userAccount = account;
					break;
				}
			}
			
			if(userAccount == null) {
				throw new SLException("未找到投资人");
			}
			
			return userAccount;
		}
		
		/**
		 * 通过投资查找分账户
		 *
		 * @author  wangjf
		 * @date    2016年12月6日 下午8:43:41
		 * @param accountList
		 * @param investId
		 * @return
		 * @throws SLException
		 */
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public SubAccountInfoEntity findUserSubAccountByInvest(final List<SubAccountInfoEntity> accountList, final String investId) throws SLException{
			
			SubAccountInfoEntity userAccount = null;
			for(SubAccountInfoEntity account : accountList) {
				if(account.getRelatePrimary().equals(investId)) {
					userAccount = account;
					break;
				}
			}
			
			if(userAccount == null) {
				throw new SLException("未找到投资人分账户");
			}
			
			return userAccount;
		}
		
		/**
		 * 取投资ID
		 *
		 * @author  wangjf
		 * @date    2016年12月6日 下午8:49:30
		 * @param investList
		 * @param investId
		 * @return
		 * @throws SLException
		 */
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public InvestInfoEntity findInvestById(final List<InvestInfoEntity> investList, final String investId)  throws SLException{
			InvestInfoEntity userAccount = null;
			for(InvestInfoEntity account : investList) {
				if(account.getId().equals(investId)) {
					userAccount = account;
					break;
				}
			}
			
			if(userAccount == null) {
				throw new SLException("未找到投资人分账户");
			}
			
			return userAccount;
		}
		
		/**
		 * 计算奖励利率
		 *
		 * @author  guoyk
		 * @date    2017年7月4日 下午1:56:46
		 * @param params
		 * @return
		 * @throws SLException
		 */
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public ResultVo awardAmountToUser(String reqeustNo,String investId,String userId,AccountInfoEntity userAccount,String loanId)throws SLException {
			//加息金额
			BigDecimal awardAmount = repaymentPlanInfoRepositoryCustom.findAwardRateInfoForNewerFlag(investId);
			if(BigDecimal.ZERO.compareTo(awardAmount) >= 0){
				return new ResultVo(false, "加息奖励金额不能小于等于零");
			}
			//公司主账户
			AccountInfoEntity earnMainAccount = accountInfoRepository.findOne(Constant.ACCOUNT_ID_LOAN_AWARD);
			List<AccountFlowInfoEntity> userAccountFlowList = custAccountService.updateAccount(earnMainAccount, null, userAccount, 
					null, "1", SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_DISPERSE_AWARD_PROJECT, 
					reqeustNo, awardAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_DISPERSE_AWARD_PROJECT, 
					Constant.TABLE_BAO_T_INVEST_INFO, investId,  
					"加息利息", userId);
			
			//付款记录
			PaymentRecordInfoEntity paymentRecordInfoEntity = new PaymentRecordInfoEntity();
	        paymentRecordInfoEntity.setRelateType(Constant.TABLE_BAO_T_ACCOUNT_FLOW_INFO);
	        paymentRecordInfoEntity.setRelatePrimary(userAccountFlowList.get(1).getId());
	        paymentRecordInfoEntity.setLoanId(loanId);
	        paymentRecordInfoEntity.setCustId(userAccount.getCustId());
	        paymentRecordInfoEntity.setTradeType(SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_DISPERSE_AWARD_PROJECT);
	        paymentRecordInfoEntity.setRepayAmount(awardAmount);
	        paymentRecordInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
	        paymentRecordInfoEntity = paymentRecordInfoRepository.save(paymentRecordInfoEntity);
	        //付款记录详情信息
			PaymentRecordDetailEntity paymentRecordDetailEntity = new PaymentRecordDetailEntity();
			paymentRecordDetailEntity.setPayRecordId(paymentRecordInfoEntity.getId());
			paymentRecordDetailEntity.setRepayPlanId("");
			paymentRecordDetailEntity.setSubjectDirection(SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING);
			paymentRecordDetailEntity.setSubjectType(SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_DISPERSE_AWARD_PROJECT);
			paymentRecordDetailEntity.setTradeAmount(awardAmount);
			paymentRecordDetailEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
			paymentRecordDetailRepository.save(paymentRecordDetailEntity);
			//借款人还款记录
			RepaymentRecordInfoEntity repaymentRecordInfoEntity = new RepaymentRecordInfoEntity();
			repaymentRecordInfoEntity.setRepayAmount(awardAmount);
			repaymentRecordInfoEntity.setHandleStatus(Constant.TRADE_STATUS_03);
			repaymentRecordInfoEntity.setLoanId(loanId);
			repaymentRecordInfoEntity.setTradeType(SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_DISPERSE_AWARD_PROJECT);
			repaymentRecordInfoEntity.setRelateType(Constant.TABLE_BAO_T_ACCOUNT_FLOW_INFO);
			repaymentRecordInfoEntity.setRelatePrimary(userAccountFlowList.get(0).getId());
			repaymentRecordInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
			repaymentRecordInfoEntity = repaymentRecordInfoRepository.save(repaymentRecordInfoEntity);
			//还款记录详情信息
			RepayRecordDetailInfoEntity repayRecordDetailInfoEntity = new RepayRecordDetailInfoEntity();
			repayRecordDetailInfoEntity.setRepayRecordId(repaymentRecordInfoEntity.getId());
			repayRecordDetailInfoEntity.setRepayPlanId("");
			repayRecordDetailInfoEntity.setSubjectDirection(SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT);
			repayRecordDetailInfoEntity.setSubjectType(SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_USER_REPAYMENT_DISPERSE_AWARD_PROJECT);
			repayRecordDetailInfoEntity.setTradeAmount(awardAmount);
			repayRecordDetailInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
			repayRecordDetailInfoRepository.save(repayRecordDetailInfoEntity);
			
			return new ResultVo(true, "加息利息操作成功");
		}
	}

	@Override
	public ResultVo userRepayment(Map<String, Object> params)
			throws SLException {
		String replaymentPlanId = (String)params.get("repaymentPlanId");
		String userId = (String)params.get("userId");
		RepaymentPlanInfoEntity repaymentPlanInfoEntity = repaymentPlanInfoRepository.findOne(replaymentPlanId);
		if(repaymentPlanInfoEntity == null) {
			return new ResultVo(false, "还款计划不存在");
		}
		if(Constant.IS_AMOUNT_FROZEN_YES.equals(repaymentPlanInfoEntity.getIsAmountFrozen())) {
			return new ResultVo(false, "该笔还款已做还款冻结，切勿重复操作");
		}
		
		// 2) 判断项目是否是正常状态
		LoanInfoEntity loanInfoEntity = loanInfoRepository.findOne(repaymentPlanInfoEntity.getLoanEntity().getId());
		if(loanInfoEntity == null) {
			return new ResultVo(false, "借款不存在");
		}
		
		String requestNo = numberService.generateTradeBatchNumber();
		Map<String, Object> freezeParam = Maps.newHashMap();
		freezeParam.put("requestNo", requestNo);
		freezeParam.put("totalRepaymentAmount", repaymentPlanInfoEntity.getRepaymentTotalAmount());
		freezeParam.put("loan", loanInfoEntity);
		freezeParam.put("repaymentPlanList", Arrays.asList(repaymentPlanInfoEntity));
		freezeParam.put("userId", userId);
		ResultVo resultVo = internalLoanRepaymentService.freezeRepayment(freezeParam); // 还款冻结
		
		return resultVo;
	}

	
}
