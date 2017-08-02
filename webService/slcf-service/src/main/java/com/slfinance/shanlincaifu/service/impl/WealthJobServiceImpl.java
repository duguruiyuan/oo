/** 
 * @(#)WealthJobServiceImpl.java 1.0.0 2016年2月23日 上午11:19:51  
 *  
 * Copyright © 2016 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AccountFlowInfoEntity;
import com.slfinance.shanlincaifu.entity.AccountInfoEntity;
import com.slfinance.shanlincaifu.entity.AtoneInfoEntity;
import com.slfinance.shanlincaifu.entity.AutoMatchRuleEntity;
import com.slfinance.shanlincaifu.entity.CustInfoEntity;
import com.slfinance.shanlincaifu.entity.InvestInfoEntity;
import com.slfinance.shanlincaifu.entity.LoanTransferEntity;
import com.slfinance.shanlincaifu.entity.LogInfoEntity;
import com.slfinance.shanlincaifu.entity.PaymentRecordDetailEntity;
import com.slfinance.shanlincaifu.entity.PaymentRecordInfoEntity;
import com.slfinance.shanlincaifu.entity.RepaymentPlanInfoEntity;
import com.slfinance.shanlincaifu.entity.RepaymentRecordInfoEntity;
import com.slfinance.shanlincaifu.entity.SubAccountInfoEntity;
import com.slfinance.shanlincaifu.entity.WealthHoldHistoryInfoEntity;
import com.slfinance.shanlincaifu.entity.WealthHoldInfoEntity;
import com.slfinance.shanlincaifu.entity.WealthInfoEntity;
import com.slfinance.shanlincaifu.entity.WealthPaymentPlanInfoEntity;
import com.slfinance.shanlincaifu.entity.WealthTypeInfoEntity;
import com.slfinance.shanlincaifu.repository.AccountInfoRepository;
import com.slfinance.shanlincaifu.repository.AtoneInfoRepository;
import com.slfinance.shanlincaifu.repository.AutoMatchRuleRepository;
import com.slfinance.shanlincaifu.repository.CustInfoRepository;
import com.slfinance.shanlincaifu.repository.InvestInfoRepository;
import com.slfinance.shanlincaifu.repository.LoanTransferRepository;
import com.slfinance.shanlincaifu.repository.LogInfoEntityRepository;
import com.slfinance.shanlincaifu.repository.PaymentRecordDetailRepository;
import com.slfinance.shanlincaifu.repository.PaymentRecordInfoRepository;
import com.slfinance.shanlincaifu.repository.RepaymentPlanInfoRepository;
import com.slfinance.shanlincaifu.repository.RepaymentRecordInfoRepository;
import com.slfinance.shanlincaifu.repository.SubAccountInfoRepository;
import com.slfinance.shanlincaifu.repository.WealthHoldHistoryInfoRepository;
import com.slfinance.shanlincaifu.repository.WealthHoldInfoRepository;
import com.slfinance.shanlincaifu.repository.WealthInfoRepository;
import com.slfinance.shanlincaifu.repository.WealthPaymentPlanInfoRepository;
import com.slfinance.shanlincaifu.repository.WealthTypeInfoRepository;
import com.slfinance.shanlincaifu.repository.custom.InvestInfoRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.WealthHoldInfoRepositoryCustom;
import com.slfinance.shanlincaifu.service.AccountFlowService;
import com.slfinance.shanlincaifu.service.CustAccountService;
import com.slfinance.shanlincaifu.service.FlowNumberService;
import com.slfinance.shanlincaifu.service.SMSService;
import com.slfinance.shanlincaifu.service.WealthInfoService;
import com.slfinance.shanlincaifu.service.WealthJobService;
import com.slfinance.shanlincaifu.utils.ArithUtil;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.SubjectConstant;
import com.slfinance.util.DateUtilSL;
import com.slfinance.vo.ResultVo;

import lombok.extern.slf4j.Slf4j;

/**   
 * 优选计划定时任务实现类
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2016年2月23日 上午11:19:51 $ 
 */
@Slf4j
@Service("wealthJobService")
public class WealthJobServiceImpl implements WealthJobService {

	@Autowired
	private RepaymentPlanInfoRepository repaymentPlanInfoRepository;
	
	@Autowired
	private WealthInfoRepository wealthInfoRepository;
	
	@Autowired
	private AtoneInfoRepository atoneInfoRepository;
	
	@Autowired
	WealthInfoService wealthInfoService;
	
	@Autowired
	private InvestInfoRepositoryCustom investInfoRepositoryCustom; 
	
	@Autowired
	private AutoMatchRuleRepository autoMatchRuleRepository;
	
	@Autowired
	private LogInfoEntityRepository logInfoEntityRepository;
	
	@Autowired
	private WealthTypeInfoRepository wealthTypeInfoRepository;
	
	@Autowired
	private SMSService smsService;
	
	@Override
	public ResultVo autoRepaymentWealth(Map<String, Object> params)
			throws SLException {
		
		// 取出所有预期还款日期等于当天的数据
		List<RepaymentPlanInfoEntity> repaymentPlanList = repaymentPlanInfoRepository.findByExpectRepaymentDate((String)params.get("expectRepaymentDate"), Constant.REPAYMENT_STATUS_WAIT);
		for(RepaymentPlanInfoEntity entity : repaymentPlanList){
			try {
				ResultVo result = internalWealthJobService.repaymentWealth(entity);
				if(!ResultVo.isSuccess(result)) {
					log.error("还款ID{}还款失败！失败原因{}", entity.getId(), result.getValue("message").toString());
				}
			}
			catch(SLException e) {
				log.error("还款ID{}还款失败！失败原因" + e.getMessage(), entity.getId());
			}
		}
		
		return new ResultVo(true, "还款成功");
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResultVo autoRecoveryWealth(Map<String, Object> params)
			throws SLException {

		// 取待返息的优选计划
		List<WealthInfoEntity> wealthList = wealthInfoRepository.findByNextRepayDayAndIncomeType((String)params.get("currentDate"), Constant.WEALTH_INCOME_TYPE_02);
		for(WealthInfoEntity wealth : wealthList) {
			try {
				if(wealth.getNextRepayDay().equals(DateUtils.formatDate(wealth.getEndDate(), "yyyyMMdd"))) { // 若下个返息日等于到期日，则不作返息，由到期处理来实现
					continue;
				}
				ResultVo result = internalWealthJobService.recoveryWealth(wealth);
				if(!ResultVo.isSuccess(result)) {
					log.error("计划ID{}返息失败！失败原因{}", wealth.getId(), result.getValue("message").toString());
				}else { //发送短信
					List<Map<String, Object>> smsList = (List<Map<String, Object>>) result.getValue("data");
					for(Map<String, Object> sms : smsList) {
						smsService.asnySendSMSAndSystemMessage(sms);
					}
				}
			}
			catch(SLException e) {
				log.error("计划ID{}返息失败！失败原因" + e.getMessage(), wealth.getId());
			}
		}
		
		return new ResultVo(true, "返息成功");
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResultVo autoAtoneWealth(Map<String, Object> params)
			throws SLException {
		
		// 1)提前赎回
		List<AtoneInfoEntity> atoneList = atoneInfoRepository.findByCleanupDateAndAtoneMethodAndAuditStatus((Date)params.get("advanceDate"), Constant.ATONE_METHOD_ADVANCE, Constant.AUDIT_STATUS_REVIEWD);
		for(AtoneInfoEntity entity : atoneList) {
			try {
				ResultVo result = internalWealthJobService.advanceAtoneWealth(entity);
				if(!ResultVo.isSuccess(result)) {
					log.error("赎回ID{}赎回到帐失败！失败原因{}", entity.getId(), result.getValue("message").toString());
				} else { //发送短息 1:理财计划类型、2：lendingNo、3：回款  
					Map<String, Object> sms = (Map<String, Object>) result.getValue("data");
					smsService.asnySendSMSAndSystemMessage(sms);
				}
			}
			catch(SLException e) {
				log.error("赎回ID{}赎回到帐失败！失败原因" + e.getMessage(), entity.getId());
			}
		}
		
		// 2)到期赎回
		List<WealthInfoEntity> wealthList = wealthInfoRepository.findByEndDateAndWealthStatus((Date)params.get("dueDate"), Constant.WEALTH_STATUS_07);
		for(WealthInfoEntity entity : wealthList) {
			try {
				ResultVo result = internalWealthJobService.dueAtoneWealth(entity);
				if(!ResultVo.isSuccess(result)) {
					log.error("优选计划ID{}到期赎回失败！失败原因{}", entity.getId(), result.getValue("message").toString());
				}else { //发送短息
					List<Map<String, Object>> smsList = (List<Map<String, Object>>) result.getValue("data");
					for (Map<String, Object> sms : smsList) {
						smsService.asnySendSMSAndSystemMessage(sms);
					}
				}
			}
			catch(SLException e) {
				log.error("优选计划ID{}到期赎回失败！失败原因" + e.getMessage(), entity.getId());
			}
		}
		return new ResultVo(true, "赎回成功");
	}
	
	@Autowired
	private InternalWealthJobService internalWealthJobService;

	@Service
	public static class InternalWealthJobService {
		
		@PersistenceContext
		private EntityManager manager;
		
		@Autowired
		private RepaymentRecordInfoRepository repaymentRecordInfoRepository;
		
		@Autowired
		private AccountInfoRepository accountInfoRepository;
		
		@Autowired
		private SubAccountInfoRepository subAccountInfoRepository;
		
		@Autowired
		private WealthHoldInfoRepository wealthHoldInfoRepository;
		
		@Autowired
		private AccountFlowService accountFlowService;	
		
		@Autowired
		private FlowNumberService numberService;
		
		@Autowired
		private PaymentRecordInfoRepository paymentRecordInfoRepository;
		
		@Autowired
		private PaymentRecordDetailRepository paymentRecordDetailRepository;
		
		@Autowired
		private WealthInfoRepository wealthInfoRepository;
		
		@Autowired
		private WealthTypeInfoRepository wealthTypeInfoRepository;
		
		@Autowired
		private CustAccountService custAccountService;
		
		@Autowired
		private WealthHoldInfoRepositoryCustom wealthHoldInfoRepositoryCustom;
		
		@Autowired
		private WealthHoldHistoryInfoRepository wealthHoldHistoryInfoRepository;
		
		@Autowired
		private LoanTransferRepository loanTransferRepository;
		
		@Autowired
		private InvestInfoRepository investInfoRepository;
		
		@Autowired
		private LogInfoEntityRepository logInfoEntityRepository;
		
		@Autowired
		private CustInfoRepository custInfoRepository;
		
		@Autowired
		private AtoneInfoRepository atoneInfoRepository;
		
		@Autowired
		private WealthPaymentPlanInfoRepository wealthPaymentPlanInfoRepository;
		
		
		/**
		 * 优选计划还款
		 *
		 * @author  wangjf
		 * @date    2016年2月23日 上午11:23:24
		 * @param params
		 * @return
		 */
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public ResultVo repaymentWealth(RepaymentPlanInfoEntity entity) throws SLException{

			// 将游离对象变为受控对象
			entity = manager.merge(entity);

			// 2 新增还款记录与更新还款计划
			RepaymentRecordInfoEntity repaymentRecordInfoEntity = new RepaymentRecordInfoEntity();
			repaymentRecordInfoEntity.setLoanId(entity.getLoanEntity().getId());
			repaymentRecordInfoEntity.setRepayAmount(entity.getRepaymentTotalAmount());
			repaymentRecordInfoEntity.setAlreadyRepayAmt(entity.getRepaymentTotalAmount());
			repaymentRecordInfoEntity.setHandleStatus(Constant.TRADE_STATUS_03);
			repaymentRecordInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
			repaymentRecordInfoRepository.save(repaymentRecordInfoEntity);
			
			entity.setTermAlreadyRepayAmount(entity.getRepaymentTotalAmount());
			entity.setRepaymentStatus(Constant.REPAYMENT_STATUS_CLEAN);
			entity.setFactRepaymentDate(new Timestamp(System.currentTimeMillis()));
			entity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
			
			// 设置还款期数
			int cycle = (entity.getLoanEntity().getRepaymentCycle() == null ? 1 : Integer.valueOf(entity.getLoanEntity().getRepaymentCycle().toString()));
			entity.getLoanEntity().getLoanDetailInfoEntity().setCurrTremEndDate(new Timestamp(DateUtils.getAfterMonth(DateUtils.parseDate(entity.getExpectRepaymentDate(), "yyyyMMdd"), cycle).getTime()));
			entity.getLoanEntity().getLoanDetailInfoEntity().setLastExpiry(new Timestamp(DateUtils.parseDate(entity.getExpectRepaymentDate(), "yyyyMMdd").getTime()));
			entity.getLoanEntity().getLoanDetailInfoEntity().setNextExpiry(new Timestamp(DateUtils.getAfterMonth(DateUtils.parseDate(entity.getExpectRepaymentDate(), "yyyyMMdd"), cycle).getTime()));
			entity.getLoanEntity().getLoanDetailInfoEntity().setCurrTerm(new BigDecimal(entity.getCurrentTerm() + 1));
			entity.getLoanEntity().getLoanDetailInfoEntity().setAlreadyPaymentTerm(new BigDecimal(entity.getCurrentTerm() + 1));
			
			// 判断当期还款计划是否是最后一期，若是最后一期则将债权状态置为结清
			if(entity.getLoanEntity().getInvestEndDate() != null 
					&& entity.getExpectRepaymentDate() != null
					&& DateUtils.formatDate(entity.getLoanEntity().getInvestEndDate(), "yyyyMMdd").equals(entity.getExpectRepaymentDate())){ // 预计还款日期等于最后还款日期
				entity.getLoanEntity().getLoanDetailInfoEntity().setCreditRightStatus(Constant.CREDIT_RIGHT_STATUS_CLEAN);
				entity.getLoanEntity().getLoanDetailInfoEntity().setCurrTremEndDate(new Timestamp(DateUtils.parseDate(entity.getExpectRepaymentDate(), "yyyyMMdd").getTime()));
				entity.getLoanEntity().getLoanDetailInfoEntity().setNextExpiry(new Timestamp(DateUtils.parseDate(entity.getExpectRepaymentDate(), "yyyyMMdd").getTime()));
				entity.getLoanEntity().getLoanDetailInfoEntity().setCurrTerm(new BigDecimal(entity.getCurrentTerm()));
				entity.getLoanEntity().getLoanDetailInfoEntity().setAlreadyPaymentTerm(new BigDecimal(entity.getCurrentTerm()));
			}
			
			// 还款给居间人和投资人
			AccountInfoEntity riskMainAccount = accountInfoRepository.findOne(Constant.ACCOUNT_ID_REPAYMENT);// 优选计划专用账户
			List<WealthHoldInfoEntity> centerHoldList = wealthHoldInfoRepository.findByLoanId(entity.getLoanEntity().getId());
			for(WealthHoldInfoEntity w : centerHoldList) {
				
				// 若持有比例为0则不做处理
				if(w.getHoldScale().compareTo(BigDecimal.ZERO) == 0) {
					continue;
				}
		
				BigDecimal tradeAmount = ArithUtil.mul(entity.getRepaymentTotalAmount(), w.getHoldScale());
				tradeAmount = ArithUtil.formatScale2(tradeAmount);
				String requestNo = numberService.generateTradeBatchNumber();
	
				// 3.1.2 用户主账户现金增加
				AccountFlowInfoEntity accountFlowInfoEntity = null;
				if(w.getCustId().equals(Constant.CUST_ID_WEALTH_CENTER)) { // 居间人直接把钱返至主账户
					
					AccountInfoEntity userAccount = accountInfoRepository.findByCustId(w.getCustId());
					
					// 3.1.1 风险金账户金额减少
					riskMainAccount.setAccountTotalAmount(ArithUtil.sub(riskMainAccount.getAccountTotalAmount(), tradeAmount));
					riskMainAccount.setAccountAvailableAmount(ArithUtil.sub(riskMainAccount.getAccountAvailableAmount(), tradeAmount));
					riskMainAccount.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
					
					accountFlowService.saveAccountFlow(riskMainAccount, null, userAccount, null, "1", 
							SubjectConstant.TRADE_FLOW_TYPE_USER_REPAYMENT_ALL_WEALTH, requestNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, 
							tradeAmount, SubjectConstant.TRADE_FLOW_TYPE_USER_REPAYMENT_ALL_WEALTH, 
							Constant.TABLE_BAO_T_REPAYMENT_PLAN_INFO, entity.getId(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
					
					userAccount.setAccountTotalAmount(ArithUtil.add(userAccount.getAccountTotalAmount(), tradeAmount));
					userAccount.setAccountAvailableAmount(ArithUtil.add(userAccount.getAccountAvailableAmount(), tradeAmount));
					userAccount.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
					
					accountFlowInfoEntity = accountFlowService.saveAccountFlow(userAccount, null, riskMainAccount, null, "1", 
							SubjectConstant.TRADE_FLOW_TYPE_USER_REPAYMENT_ALL_WEALTH, requestNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING, 
							tradeAmount, SubjectConstant.TRADE_FLOW_TYPE_USER_REPAYMENT_ALL_WEALTH, 
							Constant.TABLE_BAO_T_REPAYMENT_PLAN_INFO, entity.getId(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
				}
				else { // 投资人把钱更新至分账户
					SubAccountInfoEntity userAccount = subAccountInfoRepository.findOne(w.getSubAccountId());
					
					// 3.1.1 风险金账户金额减少
					riskMainAccount.setAccountTotalAmount(ArithUtil.sub(riskMainAccount.getAccountTotalAmount(), tradeAmount));
					riskMainAccount.setAccountAvailableAmount(ArithUtil.sub(riskMainAccount.getAccountAvailableAmount(), tradeAmount));
					riskMainAccount.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
					
					accountFlowService.saveAccountFlow(riskMainAccount, null, null, userAccount, "2", 
							SubjectConstant.TRADE_FLOW_TYPE_USER_REPAYMENT_ALL_WEALTH, requestNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, 
							tradeAmount, SubjectConstant.TRADE_FLOW_TYPE_USER_REPAYMENT_ALL_WEALTH, 
							Constant.TABLE_BAO_T_REPAYMENT_PLAN_INFO, entity.getId(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
					
					userAccount.setAccountAmount(ArithUtil.add(userAccount.getAccountAmount(), tradeAmount));
					userAccount.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
					
					accountFlowInfoEntity = accountFlowService.saveAccountFlow(null, userAccount, riskMainAccount, null, "3", 
							SubjectConstant.TRADE_FLOW_TYPE_USER_REPAYMENT_ALL_WEALTH, requestNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING, 
							tradeAmount, SubjectConstant.TRADE_FLOW_TYPE_USER_REPAYMENT_ALL_WEALTH, 
							Constant.TABLE_BAO_T_REPAYMENT_PLAN_INFO, entity.getId(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
				}
				
				BigDecimal repaymentPrincipal = ArithUtil.mul(entity.getRepaymentPrincipal(), w.getHoldScale());
				BigDecimal repaymentInterest = ArithUtil.mul(entity.getRepaymentInterest(), w.getHoldScale());
				repaymentPrincipal = ArithUtil.formatScale2(repaymentPrincipal);
				repaymentInterest = ArithUtil.formatScale2(repaymentInterest);
				accountFlowInfoEntity.setMemo(String.format("收到%s返回的本金%s元，利息%s元。债权编号:%s", 
						entity.getLoanEntity().getLoanCustInfoEntity().getCustName(), repaymentPrincipal.toPlainString(),
						repaymentInterest.toPlainString(), entity.getLoanEntity().getLoanCode()));
						
				// 3.2.1 生成付款记录
				PaymentRecordInfoEntity paymentRecordInfoEntity = new PaymentRecordInfoEntity();
				paymentRecordInfoEntity.setRelateType(Constant.TABLE_BAO_T_ACCOUNT_FLOW_INFO);
				paymentRecordInfoEntity.setRelatePrimary(accountFlowInfoEntity.getId());
				paymentRecordInfoEntity.setLoanId(w.getLoanId());
				paymentRecordInfoEntity.setCustId(w.getCustId());
				paymentRecordInfoEntity.setTradeType(SubjectConstant.TRADE_FLOW_TYPE_USER_REPAYMENT_ALL_WEALTH);
				paymentRecordInfoEntity.setRepayAmount(tradeAmount);
				paymentRecordInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
				paymentRecordInfoEntity = paymentRecordInfoRepository.save(paymentRecordInfoEntity);
				
				// 3.2.2 生成付款明细
				List<PaymentRecordDetailEntity> payRecordDetailList = Lists.newArrayList();
				// 本金
				payRecordDetailList.add(createPaymentRecordDetail(paymentRecordInfoEntity.getId(), entity.getId(), 
						SubjectConstant.TRADE_FLOW_TYPE_USER_REPAYMENT_PRINCIPAL_WEALTH, repaymentPrincipal));
				
				// 利息
				payRecordDetailList.add(createPaymentRecordDetail(paymentRecordInfoEntity.getId(), entity.getId(), 
						SubjectConstant.TRADE_FLOW_TYPE_USER_REPAYMENT_INTEREST_WEALTH, repaymentInterest));
				
				paymentRecordDetailRepository.save(payRecordDetailList);
				
				// 若项目已结清，则将持有状态也改为已结清
				if(entity.getLoanEntity().getLoanDetailInfoEntity().getCreditRightStatus().equals(Constant.CREDIT_RIGHT_STATUS_CLEAN)) {
					w.setHoldStatus(Constant.HOLD_STATUS_05);
					w.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
				}
			}

			return new ResultVo(true);
		}
		
		/**
		 * 返息
		 *
		 * @author  wangjf
		 * @date    2016年2月23日 下午3:32:55
		 * @param entity
		 * @return
		 */
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public ResultVo recoveryWealth(WealthInfoEntity entity) throws SLException{
			
			// 将游离对象变为受控对象
			entity = manager.merge(entity);
			
			// 取产品类型
			WealthTypeInfoEntity wealthTypeInfoEntity = wealthTypeInfoRepository.findOne(entity.getWealthTypeId());
			int typeTerm = wealthTypeInfoEntity.getTypeTerm();
			// 返息金额=投资金额*年化利率*计息天数*出借期限/(12*封闭天数)
			// 计息天数：首期：下个返息日-初始日期+1，其他:当月返息日-上一个返息日
			// 初始日期：第一次为生效日期，后面为上一个返息日
			Date beginDate = entity.getFirstRepayDay().equals(entity.getNextRepayDay()) ? 
					DateUtils.getAfterDay(DateUtils.truncateDate(entity.getEffectDate()), -1) : DateUtils.parseDate(entity.getPrevRepayDay(), "yyyyMMdd");
			Date endDate = DateUtils.parseDate(entity.getNextRepayDay(), "yyyyMMdd");		
			int days = DateUtils.datePhaseDiffer(beginDate, endDate);
			// 封闭天数=出借期限/12不为整数=到期日-初始日期+1，出借期限/12为整数=365*出借期限/12；
			int seatDays = typeTerm % 12 == 0 ? 
					365*typeTerm/12 : DateUtils.datePhaseDiffer(DateUtils.truncateDate(entity.getEffectDate()), DateUtils.parseDate(entity.getLastRepayDay(), "yyyyMMdd")) + 1;
			
			// 居间人账户
			AccountInfoEntity centerAccount = accountInfoRepository.findOne(Constant.ACCOUNT_ID_WEALTH_CENTER);
			List<InvestInfoEntity> investList = wealthInfoRepository.findByWealthId(entity.getId());
			List<WealthHoldHistoryInfoEntity> wealthHoldHistoryList = Lists.newArrayList();
			List<LoanTransferEntity> loanTransferList = Lists.newArrayList();
			List<Map<String, Object>> smsList = Lists.newArrayList();
			for(InvestInfoEntity iEntity : investList) {
				
				if(!Constant.INVEST_STATUS_EARN.equals(iEntity.getInvestStatus())) { // 非收益中不允许返息
					continue;
				}
				
				String requestNo = numberService.generateTradeBatchNumber();
				double dInterest = iEntity.getInvestAmount().doubleValue()*entity.getYearRate().doubleValue()*days*typeTerm/(12*seatDays);
				BigDecimal interest = ArithUtil.formatScale(new BigDecimal(String.valueOf(dInterest)), 2);
				SubAccountInfoEntity userSubAccount = subAccountInfoRepository.findByRelatePrimary(iEntity.getId());
				AccountInfoEntity userAccount = accountInfoRepository.findByCustId(iEntity.getCustId());
				
				// 更新返息计划表
				WealthPaymentPlanInfoEntity wealthPaymentPlanInfoEntity = wealthPaymentPlanInfoRepository.findByInvestIdAndPaymentStatusAndExceptPaymentDate(iEntity.getId(), Constant.WEALTH_PAYMENT_PLAN_STATUS_NOT, entity.getNextRepayDay());
				wealthPaymentPlanInfoEntity.setPaymentStatus(Constant.WEALTH_PAYMENT_PLAN_STATUS_ALREADY);
				wealthPaymentPlanInfoEntity.setFactPaymentAmount(interest);
				wealthPaymentPlanInfoEntity.setFactPaymentDate(DateUtils.getCurrentDate("yyyyMMdd"));
				wealthPaymentPlanInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
				
				// 准备短信内容
				CustInfoEntity custInfoEntity = custInfoRepository.findOne(iEntity.getCustId());
				Map<String, Object> smsParams = new HashMap<String, Object>();
				smsParams.put("mobile", custInfoEntity.getMobile());
				smsParams.put("custId", custInfoEntity.getId());	
				smsParams.put("messageType", Constant.SMS_TYPE_WEALTH_INTEREST);				
				smsParams.put("values", new Object[] { // 短信息内容
						wealthTypeInfoEntity.getLendingType(), //理财计划类型名称 lendingType
						entity.getLendingNo(), //理财计划lendingNo
						wealthPaymentPlanInfoEntity.getCurrentTerm().toString(), //第几期 例如 1
						interest.toPlainString(), //回款
						Constant.SHANLINCAIFU_URL
				});				
				smsParams.put("systemMessage", new Object[] { // 站内信内容
						wealthTypeInfoEntity.getLendingType(), //理财计划类型名称 lendingType
						entity.getLendingNo(), //理财计划lendingNo
						wealthPaymentPlanInfoEntity.getCurrentTerm().toString(), //第几期 例如 1
						interest.toPlainString(), //回款
				});
				smsList.add(smsParams);
				
				if(userSubAccount.getAccountAmount().compareTo(interest) >= 0) { // 分账现金大于等于返息金额直接返息
										
					// 用户分账户出返息金额至主账户
					custAccountService.updateAccount(null, userSubAccount, userAccount, 
							null, "14", SubjectConstant.TRADE_FLOW_TYPE_USER_REPAY_INTEREST_WEALTH, 
							requestNo, interest, SubjectConstant.TRADE_FLOW_TYPE_USER_REPAY_INTEREST_WEALTH, 
							Constant.TABLE_BAO_T_WEALTH_INFO, entity.getId(),  
							"月返息", Constant.SYSTEM_USER_BACK);
					
					continue;
				}
				else if(userSubAccount.getAccountAmount().compareTo(BigDecimal.ZERO) > 0) { // 存在分账数据
					BigDecimal tradeAmount = userSubAccount.getAccountAmount();
					// 剩余未返息金额
					interest = ArithUtil.sub(interest, tradeAmount);					
				}
				
				// 投资人进行债权转让来返息
				Map<String, Object> pvParams = Maps.newHashMap();
				pvParams.put("investId", iEntity.getId());
				pvParams.put("valueDate", DateUtils.getCurrentDate("yyyyMMdd"));
				pvParams.put("holdStatusList", Arrays.asList(Constant.HOLD_STATUS_01));
				// 查询投资人持有债权情况
				List<Map<String, Object>> userPvList = wealthHoldInfoRepositoryCustom.findUserHoldPv(pvParams);
				List<String> loanList = Lists.transform(userPvList, new Function<Map<String, Object>, String>() {
					@Override
					public String apply(Map<String, Object> input) {
						return (String)input.get("loanId");// 将List<Map<String, Object>>转为List<String>，取Key为loanId的数据
					}
				});
				pvParams.put("loanList", loanList);
				pvParams.put("isCenter", "是");
				// 查询居间人持有债权情况
				List<Map<String, Object>> centerPvList = wealthHoldInfoRepositoryCustom.findCenterHoldPv(pvParams);
				List<WealthHoldInfoEntity> wealthHoldList = Lists.newArrayList();
				BigDecimal totalTransferAmount = BigDecimal.ZERO;
				for(Map<String, Object> u : userPvList) {
					BigDecimal totalPv = new BigDecimal(u.get("pv").toString());
					BigDecimal holdPv = new BigDecimal(u.get("holdPv").toString());
					
					Map<String, Object> centerMap = null;
					for(Map<String, Object> c : centerPvList) {
						if(c.get("loanId").toString().equals(u.get("loanId").toString())) {
							centerMap = c;
							break;
						}
					}
					
					if(centerMap == null) { //理论上不会存在居间人不持有该笔债权的情况
						throw new SLException(String.format("居间人未持有该笔债权%s", u.get("loanId").toString()));
					}
					
					// 保存历史持有情况
					String tradeNo = numberService.generateLoanTransferNo();
					wealthHoldHistoryList.add(createWealthHoldHistoryInfo(u, requestNo, tradeNo));
					wealthHoldHistoryList.add(createWealthHoldHistoryInfo(centerMap, requestNo, tradeNo));
					WealthHoldInfoEntity uWealthHold = createWealthHoldInfo(u);
					WealthHoldInfoEntity cWealthHold = createWealthHoldInfo(centerMap);
					
					BigDecimal centerholdPv = new BigDecimal(centerMap.get("holdPv").toString());// 居间人持有价值
					BigDecimal tradeAmount = BigDecimal.ZERO;
					if(holdPv.compareTo(interest) >= 0) {
						tradeAmount = interest;
					}
					else {
						tradeAmount = holdPv;						
					}
					
					holdPv = ArithUtil.sub(holdPv, tradeAmount);
					centerholdPv = ArithUtil.add(centerholdPv, tradeAmount);
					
					// 修改持有信息
					uWealthHold.setHoldScale(ArithUtil.div(holdPv, totalPv));					
					cWealthHold.setHoldScale(ArithUtil.div(centerholdPv, totalPv));
					if(holdPv.compareTo(BigDecimal.ZERO) == 0) { // 若用户持有债权为0则改为已转让
						uWealthHold.setHoldStatus(Constant.HOLD_STATUS_04);
					}
					cWealthHold.setHoldStatus(Constant.HOLD_STATUS_01);// 居间人回购债权需将状态改为持有中
					uWealthHold.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
					cWealthHold.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
					wealthHoldList.add(uWealthHold);
					wealthHoldList.add(cWealthHold);
					
					// 生成债权转让信息
					LoanTransferEntity loanTransferEntity = new LoanTransferEntity();
					loanTransferEntity.setSenderHoldId((String)u.get("id"));
					loanTransferEntity.setSenderLoanId((String)u.get("loanId"));
					loanTransferEntity.setSenderCustId((String)u.get("custId"));
					loanTransferEntity.setReceiveHoldId((String)centerMap.get("id"));
					loanTransferEntity.setReceiveLoanId((String)centerMap.get("loanId"));
					loanTransferEntity.setReceiveCustId((String)centerMap.get("custId"));
					loanTransferEntity.setTradeAmount(tradeAmount);
					loanTransferEntity.setTradeScale(ArithUtil.div(tradeAmount, totalPv));
					loanTransferEntity.setTradeNo(tradeNo);
					loanTransferEntity.setRequestNo(requestNo);
					loanTransferEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
					loanTransferList.add(loanTransferEntity);
					
					// 债权转让返息
					custAccountService.updateAccount(centerAccount, null, null, 
							userSubAccount, "13", SubjectConstant.TRADE_FLOW_TYPE_USER_REPAY_LOAN_TRANSFER_WEALTH, 
							requestNo, tradeAmount, SubjectConstant.TRADE_FLOW_TYPE_USER_REPAY_LOAN_TRANSFER_WEALTH, 
							Constant.TABLE_BAO_T_WEALTH_INFO, entity.getId(),  
							String.format("月返息出售转让债权%s", u.get("loanCode").toString()), Constant.SYSTEM_USER_BACK);
					
					// 统计债权转让的金额
					totalTransferAmount = ArithUtil.add(totalTransferAmount, tradeAmount);
					// 剩余未返利息
					interest = ArithUtil.sub(interest, tradeAmount);					
					if(interest.compareTo(BigDecimal.ZERO) == 0) {
						break;
					}
				}
				
				wealthHoldInfoRepositoryCustom.batchUpdate(wealthHoldList);

				// 仍有利息没有返息则需从居间人账户进行贴息
				if(interest.compareTo(BigDecimal.ZERO) > 0) {
					AccountInfoEntity earnAccount = accountInfoRepository.findOne(Constant.ACCOUNT_ID_ERAN);
					custAccountService.updateAccount(earnAccount, null, null, 
							userSubAccount, "13", SubjectConstant.TRADE_FLOW_TYPE_USER_REPAY_INTEREST_OTHER_WEALTH, 
							requestNo, interest, SubjectConstant.TRADE_FLOW_TYPE_USER_REPAY_INTEREST_OTHER_WEALTH, 
							Constant.TABLE_BAO_T_WEALTH_INFO, entity.getId(),  
							"月返息", Constant.SYSTEM_USER_BACK);
				}
				
				// 用户分账户出返息金额至主账户				
				custAccountService.updateAccount(null, userSubAccount, userAccount, 
						null, "14", SubjectConstant.TRADE_FLOW_TYPE_USER_REPAY_INTEREST_WEALTH, 
						requestNo, userSubAccount.getAccountAmount(), SubjectConstant.TRADE_FLOW_TYPE_USER_REPAY_INTEREST_WEALTH, 
						Constant.TABLE_BAO_T_WEALTH_INFO, entity.getId(),  
						"月返息", Constant.SYSTEM_USER_BACK);
			}
			
			// 当前返息日+1个月
			Date nextRepayDate = DateUtils.getAfterMonth(DateUtils.parseDate(entity.getNextRepayDay(), "yyyyMMdd"), 1);
			if(nextRepayDate.compareTo(entity.getEndDate()) > 0) {
				nextRepayDate = entity.getEndDate(); //若返息时间大于到期时间，则设置为到期时间
			}
			
			entity.setPrevRepayDay(entity.getNextRepayDay());
			entity.setNextRepayDay(DateUtils.formatDate(nextRepayDate, "yyyyMMdd"));
			entity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
			
			wealthHoldHistoryInfoRepository.save(wealthHoldHistoryList);
			loanTransferRepository.save(loanTransferList);
						
			return new ResultVo(true, "操作成功！", smsList);

		}
	
		/**
		 * 提前赎回优选计划
		 *
		 * @author  wangjf
		 * @date    2016年2月24日 上午10:45:51
		 * @param entity
		 * @return
		 * @throws SLException 
		 */
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public ResultVo advanceAtoneWealth(AtoneInfoEntity entity) throws SLException {
			
			// 将游离对象变为受控对象
			entity = manager.merge(entity);
			
			// 取投资对象
			InvestInfoEntity investInfoEntity = investInfoRepository.findOne(entity.getInvestId());
			
			// 赎回优选计划
			return atoneWealth(entity, investInfoEntity, entity.getAtoneExpenses());
		}
		
		/**
		 * 到期赎回优选计划
		 *
		 * @author  wangjf
		 * @date    2016年2月24日 上午10:47:27
		 * @param entity
		 * @return
		 */
		@SuppressWarnings("unchecked")
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public ResultVo dueAtoneWealth(WealthInfoEntity entity) throws SLException{
			
			// 将游离对象变为受控对象
			entity = manager.merge(entity);
			
			// 取计划类型
			WealthTypeInfoEntity wealthTypeInfoEntity = wealthTypeInfoRepository.findOne(entity.getWealthTypeId());
			int typeTerm = wealthTypeInfoEntity.getTypeTerm();
			BigDecimal yearRate = entity.getYearRate();
			BigDecimal awardRate = entity.getAwardRate();
			String incomeType = wealthTypeInfoEntity.getIncomeType();
			
			List<Map<String, Object>> smsList = Lists.newArrayList();
			List<InvestInfoEntity> investList = wealthInfoRepository.findByWealthId(entity.getId());
			
			for(InvestInfoEntity iEntity : investList) {
				
				if(!Constant.INVEST_STATUS_EARN.equals(iEntity.getInvestStatus())) {
					continue;
				}
				
                // 计算投资人应赎回金额
				// 1)到期结算本息：
				// 投资人收益 =投资金额*年化利率*计息天数*出借期限/（12*封闭天数）
				// 
				// 2)按月返息：
				// 投资金额*年化利率*出借期限*(到期日-上一返息日)/（封闭天数*12）
				//
				// 用户赎回总额=投资金额+投资收益+奖励收益
				// 投资人奖励收益=投资金额*奖励年化利率*计息天数*出借期限/（12*封闭天数）
				
				// 计息天数  = 封闭天数=出借期限/12不为整数=到期日-初始日期+1，出借期限/12为整数=365*出借期限/12；
				int seatDays = typeTerm % 12 == 0 ? 
						365*typeTerm/12 : DateUtils.datePhaseDiffer(DateUtils.truncateDate(entity.getEffectDate()), DateUtils.parseDate(entity.getLastRepayDay(), "yyyyMMdd")) + 1;
				
				BigDecimal investAmount = iEntity.getInvestAmount();
				double dInterest = 0.0;
				if(incomeType.equals(Constant.WEALTH_INCOME_TYPE_01)) {
					dInterest = investAmount.doubleValue()*yearRate.doubleValue()*seatDays*typeTerm/(12*seatDays);
				}
				else {
					// 上一返息日
					Date beginDate = entity.getFirstRepayDay().equals(entity.getNextRepayDay()) ? 
							DateUtils.truncateDate(entity.getEffectDate()) : DateUtils.parseDate(entity.getPrevRepayDay(), "yyyyMMdd");
					// 到期日
					Date endDate = DateUtils.truncateDate(entity.getEndDate());
					// 到期日-上一返息日
					int days = DateUtils.datePhaseDiffer(beginDate, endDate);
					dInterest = investAmount.doubleValue()*yearRate.doubleValue()*typeTerm*days/(12*seatDays);
				}
				double dAward = investAmount.doubleValue()*awardRate.doubleValue()*seatDays*typeTerm/(12*seatDays);
				BigDecimal bInterest = ArithUtil.formatScale(new BigDecimal(String.valueOf(dInterest)), 2);
				BigDecimal bAward = ArithUtil.formatScale(new BigDecimal(String.valueOf(dAward)), 2);
				// 用户赎回总额=投资金额+投资收益+奖励收益
				BigDecimal atoneTotalAmount = ArithUtil.add(ArithUtil.add(investAmount, bInterest), bAward);
				
				// 生成赎回信息
				AtoneInfoEntity atoneInfoEntity = new AtoneInfoEntity();
				atoneInfoEntity.setCustId(iEntity.getCustId());
				atoneInfoEntity.setInvestId(iEntity.getId());
				atoneInfoEntity.setOperType("");
				atoneInfoEntity.setTradeCode(numberService.generateTradeNumber());
				atoneInfoEntity.setCleanupDate(new Date());
				atoneInfoEntity.setAtoneMethod(Constant.ATONE_METHOD_EXPIRE);
				atoneInfoEntity.setAtoneExpenses(new BigDecimal("0"));
				atoneInfoEntity.setAtoneStatus(Constant.TRADE_STATUS_01);
				atoneInfoEntity.setAuditStatus(Constant.AUDIT_STATUS_PASS);
				atoneInfoEntity.setAtoneTotalAmount(atoneTotalAmount);
				atoneInfoEntity.setAlreadyAtoneAmount(atoneTotalAmount);
				atoneInfoEntity.setTradeSource(Constant.INVEST_SOURCE_PC);
				atoneInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
				atoneInfoEntity = atoneInfoRepository.save(atoneInfoEntity);
				
				// 投资状态改为到期赎回中
				iEntity.setInvestStatus(Constant.INVEST_STATUS_WAIT);
				iEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
				
				ResultVo result = atoneWealth(atoneInfoEntity, iEntity, bAward);
				if(ResultVo.isSuccess(result)) {
					// 赎回成功
					smsList.add((Map<String, Object>)result.getValue("data"));
				}
				else {
					throw new SLException(String.format("投资ID%s赎回失败", iEntity.getId()));
				}
				
			}
			
			// 修改优选计划
			entity.setWealthStatus(Constant.WEALTH_STATUS_09);
			entity.setFlowStatus(Constant.WEALTH_STATUS_09);
			entity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
			
			return new ResultVo(true, "赎回成功", smsList);
		}
		
		/**
		 * 赎回优选计划
		 *
		 * @author  wangjf
		 * @date    2016年2月24日 下午4:42:45
		 * @param entity
		 * @param iEntity
		 * @param otherAmount 若是提前赎回则该值为提前赎回手续费，若是到期赎回则该值为奖励收益
		 * @return
		 * @throws SLException
		 */
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public ResultVo atoneWealth(AtoneInfoEntity entity, InvestInfoEntity iEntity, BigDecimal otherAmount) throws SLException {
			
			// 赎回金额
			BigDecimal atoneTotalAmount = entity.getAtoneTotalAmount();
			String tradeType = "", investStatus = "", messageType = "";
			if(entity.getAtoneMethod().equals(Constant.ATONE_METHOD_ADVANCE)) { // 提前赎回
				tradeType = SubjectConstant.TRADE_FLOW_TYPE_USER_ADVANCE_ATONE_WEALTH;
				investStatus = Constant.INVEST_STATUS_ADVANCE_FINISH;
				messageType = Constant.SMS_TYPE_WEALTH_ADVANCE_ATONE;
				atoneTotalAmount = ArithUtil.add(atoneTotalAmount, entity.getAtoneExpenses()); // atoneTotalAmount表示到账金额，此处需先加上提前退出费用
			}
			else { // 到期赎回
				tradeType = SubjectConstant.TRADE_FLOW_TYPE_USER_DUE_ATONE_WEALTH;
				investStatus = Constant.INVEST_STATUS_FINISH;
				messageType = Constant.SMS_TYPE_WEALTH_DUE_ATONE;
				atoneTotalAmount = ArithUtil.sub(atoneTotalAmount, otherAmount); // atoneTotalAmount表示到账金额，此处需先减去奖励金额
			}
			
			// 取公司账户、用户主账户、分账户
			String requestNo = numberService.generateTradeBatchNumber();
			AccountInfoEntity centerAccount = accountInfoRepository.findOne(Constant.ACCOUNT_ID_WEALTH_CENTER);// 居间人账户
			SubAccountInfoEntity userSubAccount = subAccountInfoRepository.findByRelatePrimary(iEntity.getId());
			AccountInfoEntity userAccount = accountInfoRepository.findByCustId(entity.getCustId());
			
			// 1) 计算用户当前持有价值
			Map<String, Object> pvParams = Maps.newHashMap();
			pvParams.put("investId", iEntity.getId());
			pvParams.put("valueDate", DateUtils.getCurrentDate("yyyyMMdd"));
			pvParams.put("holdStatusList", Arrays.asList(Constant.HOLD_STATUS_01, Constant.HOLD_STATUS_02));
			BigDecimal userHoldTotalPv = wealthHoldInfoRepositoryCustom.sumUserHoldPv(pvParams);
			if(userHoldTotalPv.compareTo(BigDecimal.ZERO) > 0) { // 用户持有债权价值大于0需回购债权
				List<Map<String, Object>> userPvList = wealthHoldInfoRepositoryCustom.findUserHoldPv(pvParams);
				for(Map<String, Object> u : userPvList){
					BigDecimal tradeAmount = ArithUtil.formatScale(new BigDecimal(u.get("holdPv").toString()), 2);
					// 居间人回购债权出现金给投资人
					custAccountService.updateAccount(centerAccount, null, null, 
							userSubAccount, "13", SubjectConstant.TRADE_FLOW_TYPE_USER_ATONE_LOAN_TRANSFER_WEALTH, 
							requestNo, tradeAmount, SubjectConstant.TRADE_FLOW_TYPE_USER_ATONE_LOAN_TRANSFER_WEALTH, 
							Constant.TABLE_BAO_T_INVEST_INFO, iEntity.getId(),  
							String.format("优选计划退出转让债权%s", u.get("loanCode").toString()), Constant.SYSTEM_USER_BACK);
				}
				
				// 2) 完成债权回购
				pvParams.put("lastUpdateUser", Constant.SYSTEM_USER_BACK);
				pvParams.put("lastUpdateDate", new Date());
				pvParams.put("requestNo", requestNo);
				wealthHoldInfoRepositoryCustom.batchLoanTransfer(pvParams);
			}

			// 3) 给用户返还现金（理论值大于实际值贴息，否则算服务费）
			// 用户子账户金额
			BigDecimal factHoldAmount = userSubAccount.getAccountAmount();
			AccountInfoEntity earnAccount = accountInfoRepository.findOne(Constant.ACCOUNT_ID_ERAN);
			if(atoneTotalAmount.compareTo(factHoldAmount) > 0) { // 实际持有值小于理论值需补息
				BigDecimal distanceAmount = ArithUtil.sub(atoneTotalAmount, factHoldAmount);//补息金额
				custAccountService.updateAccount(earnAccount, null, null, 
						userSubAccount, "13", SubjectConstant.TRADE_FLOW_TYPE_USER_ATONE_INTEREST_OTHER_WEALTH, 
						requestNo, distanceAmount, SubjectConstant.TRADE_FLOW_TYPE_USER_ATONE_INTEREST_OTHER_WEALTH, 
						Constant.TABLE_BAO_T_INVEST_INFO, iEntity.getId(),  
						"赎回贴息", Constant.SYSTEM_USER_BACK);
			}
			else if(atoneTotalAmount.compareTo(factHoldAmount) < 0) { // 实际持有值大于理论值出服务费
				BigDecimal distanceAmount = ArithUtil.sub(factHoldAmount, atoneTotalAmount);//服务费金额
				custAccountService.updateAccount(null, userSubAccount, earnAccount, 
						null, "14", SubjectConstant.TRADE_FLOW_TYPE_USER_ATONE_EXPENSE_OTHER_WEALTH, 
						requestNo, distanceAmount, SubjectConstant.TRADE_FLOW_TYPE_USER_ATONE_EXPENSE_OTHER_WEALTH, 
						Constant.TABLE_BAO_T_INVEST_INFO, iEntity.getId(),  
						"赎回服务费", Constant.SYSTEM_USER_BACK);
			}
			
			if(entity.getAtoneMethod().equals(Constant.ATONE_METHOD_ADVANCE)) { // 提前赎回
				
				// 用户出提前退出费用
				custAccountService.updateAccount(null, userSubAccount, earnAccount, 
						null, "14", SubjectConstant.TRADE_FLOW_TYPE_USER_ADVANCED_ATONE_EXPENSE_WEALTH, 
						requestNo, entity.getAtoneExpenses(), SubjectConstant.TRADE_FLOW_TYPE_USER_ADVANCED_ATONE_EXPENSE_WEALTH, 
						Constant.TABLE_BAO_T_INVEST_INFO, iEntity.getId(),  
						"提前退出费用", Constant.SYSTEM_USER_BACK);
				
				// 用户分账户出返息金额至主账户
				custAccountService.updateAccount(null, userSubAccount, userAccount, 
						null, "14", tradeType, 
						requestNo, userSubAccount.getAccountAmount(), tradeType, 
						Constant.TABLE_BAO_T_INVEST_INFO, iEntity.getId(),  
						"赎回优选计划", Constant.SYSTEM_USER_BACK);
			}
			else { //到期处理
				
				// 公司主账户出奖励金额至用户主账户
				if(otherAmount.compareTo(BigDecimal.ZERO) > 0) {
					custAccountService.updateAccount(earnAccount, null, null, 
							userSubAccount, "13", SubjectConstant.TRADE_FLOW_TYPE_USER_DUE_ATONE_AWARD_WEALTH, 
							requestNo, otherAmount, SubjectConstant.TRADE_FLOW_TYPE_USER_DUE_ATONE_AWARD_WEALTH, 
							Constant.TABLE_BAO_T_INVEST_INFO, iEntity.getId(),  
							"赎回优选计划", Constant.SYSTEM_USER_BACK);
				}
				
				// 用户分账户出本息金额至用户主账户
				custAccountService.updateAccount(null, userSubAccount, userAccount, 
						null, "14", tradeType, 
						requestNo, userSubAccount.getAccountAmount(), tradeType, 
						Constant.TABLE_BAO_T_INVEST_INFO, iEntity.getId(),  
						"赎回优选计划", Constant.SYSTEM_USER_BACK);
			}
			
			// 4) 更新赎回表
			entity.setAtoneTotalValue(userHoldTotalPv);
			entity.setAuditStatus(Constant.AUDIT_STATUS_PASS);
			entity.setAtoneStatus(Constant.TRADE_STATUS_03);
			entity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
			
			// 5) 更新投资表
			iEntity.setInvestStatus(investStatus);
			iEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
			
			// 6) 更新返息计划表
			List<WealthPaymentPlanInfoEntity> wealthPaymentPlanList = wealthPaymentPlanInfoRepository.findByInvestIdAndPaymentStatusOrderByExceptPaymentDateAsc(iEntity.getId(), Constant.WEALTH_PAYMENT_PLAN_STATUS_NOT);
			for(int i = 0; i < wealthPaymentPlanList.size(); i ++) {
				WealthPaymentPlanInfoEntity plan = wealthPaymentPlanList.get(i);
				plan.setPaymentStatus(Constant.WEALTH_PAYMENT_PLAN_STATUS_ALREADY);
				if(i == 0) {
					plan.setFactPaymentAmount(entity.getAtoneTotalAmount());
				}
				else {
					plan.setFactPaymentAmount(BigDecimal.ZERO);
				}
				plan.setFactPaymentDate(DateUtils.getCurrentDate("yyyyMMdd"));
				plan.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
			}
			
			
			// 7) 给投资人发送短信
			WealthInfoEntity wealthInfoEntity = wealthInfoRepository.findOne(iEntity.getWealthId());
			WealthTypeInfoEntity wealthTypeInfoEntity = wealthTypeInfoRepository.findOne(wealthInfoEntity.getWealthTypeId());
			CustInfoEntity custInfoEntity = custInfoRepository.findOne(entity.getCustId());
			Map<String, Object> smsParams = new HashMap<String, Object>();
			smsParams.put("mobile", custInfoEntity.getMobile());
			smsParams.put("custId", custInfoEntity.getId());	
			smsParams.put("messageType", messageType);
			smsParams.put("values", new Object[] { // 短信息内容
					wealthTypeInfoEntity.getLendingType(),
					wealthInfoEntity.getLendingNo(),
					ArithUtil.formatScale(entity.getAtoneTotalAmount(), 2).toPlainString(),
					Constant.SHANLINCAIFU_URL
			});
			if(entity.getAtoneMethod().equals(Constant.ATONE_METHOD_ADVANCE)) { // 提前赎回
				smsParams.put("systemMessage", new Object[] { // 站内信内容
						wealthTypeInfoEntity.getLendingType(),
						wealthInfoEntity.getLendingNo(),
						ArithUtil.formatScale(entity.getAtoneTotalAmount(), 2).toPlainString()});
			}else {
				smsParams.put("systemMessage", new Object[] { // 站内信内容
						DateUtils.formatDate(iEntity.getCreateDate(), "yyyy-MM-dd"),
						wealthTypeInfoEntity.getLendingType(),
						wealthInfoEntity.getLendingNo(),
						ArithUtil.formatScale(entity.getAtoneTotalAmount(), 2).toPlainString(),
						ArithUtil.formatScale(iEntity.getInvestAmount(), 2).toPlainString(), 
						ArithUtil.formatScale(ArithUtil.sub(entity.getAtoneTotalAmount(), iEntity.getInvestAmount()), 2).toPlainString() //到期总利息
				});
			}
			
			// 8) 记录日志信息
			LogInfoEntity logInfoEntity = new LogInfoEntity();
			logInfoEntity.setRelateType(Constant.TABLE_BAO_T_INVEST_INFO);
			logInfoEntity.setRelatePrimary(iEntity.getId());
			logInfoEntity.setLogType(Constant.OPERATION_TYPE_50);
			logInfoEntity.setOperDesc("");
			logInfoEntity.setOperPerson(Constant.SYSTEM_USER_BACK);
			logInfoEntity.setMemo(String.format("用户%s赎回优选计划%s元", custInfoEntity.getLoginName(), atoneTotalAmount.toPlainString()));
			logInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
			logInfoEntityRepository.save(logInfoEntity);
			
			return new ResultVo(true, "赎回成功", smsParams);
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
		
		/**
		 * 创建历史PV持有信息
		 *
		 * @author  wangjf
		 * @date    2016年2月23日 下午8:18:57
		 * @param wealthHoldMap
		 * @return
		 */
		public WealthHoldHistoryInfoEntity createWealthHoldHistoryInfo(Map<String, Object> wealthHoldMap, String requestNo, String tradeNo) {
			WealthHoldHistoryInfoEntity wealthHoldHistoryInfoEntity = new WealthHoldHistoryInfoEntity();
			wealthHoldHistoryInfoEntity.setHoldId((String)wealthHoldMap.get("id"));
			wealthHoldHistoryInfoEntity.setInvestId((String)wealthHoldMap.get("investId"));
			wealthHoldHistoryInfoEntity.setSubAccountId((String)wealthHoldMap.get("subAccountId"));
			wealthHoldHistoryInfoEntity.setCustId((String)wealthHoldMap.get("custId"));
			wealthHoldHistoryInfoEntity.setLoanId((String)wealthHoldMap.get("loanId"));
			wealthHoldHistoryInfoEntity.setHoldScale(wealthHoldMap.get("holdScale") == null ? BigDecimal.ZERO : new BigDecimal(wealthHoldMap.get("holdScale").toString()));
			wealthHoldHistoryInfoEntity.setHoldAmount(wealthHoldMap.get("holdAmount") == null ? BigDecimal.ZERO : new BigDecimal(wealthHoldMap.get("holdAmount").toString()));
			wealthHoldHistoryInfoEntity.setExceptAmount(wealthHoldMap.get("exceptAmount") == null ? BigDecimal.ZERO : new BigDecimal(wealthHoldMap.get("exceptAmount").toString()));
			wealthHoldHistoryInfoEntity.setReceivedAmount(wealthHoldMap.get("receivedAmount") == null ? BigDecimal.ZERO : new BigDecimal(wealthHoldMap.get("receivedAmount").toString()));
			wealthHoldHistoryInfoEntity.setHoldStatus((String)wealthHoldMap.get("holdStatus"));
			wealthHoldHistoryInfoEntity.setHoldSource((String)wealthHoldMap.get("holdSource"));
			wealthHoldHistoryInfoEntity.setIsCenter((String)wealthHoldMap.get("isCenter"));
			wealthHoldHistoryInfoEntity.setOldCreateDate((Date)wealthHoldMap.get("createDate"));
			wealthHoldHistoryInfoEntity.setOldCreateUser((String)wealthHoldMap.get("createUser"));
			wealthHoldHistoryInfoEntity.setOldLastUpdateDate((Date)wealthHoldMap.get("lastUpdateDate"));
			wealthHoldHistoryInfoEntity.setOldLastUpdateUser((String)wealthHoldMap.get("lastUpdateUser"));
			wealthHoldHistoryInfoEntity.setOldMemo((String)wealthHoldMap.get("memo"));
			wealthHoldHistoryInfoEntity.setRequestNo(requestNo);
			wealthHoldHistoryInfoEntity.setTradeNo(tradeNo);
			wealthHoldHistoryInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
			
			return wealthHoldHistoryInfoEntity;
		}
		
		/**
		 * 创建PV持有信息
		 *
		 * @author  wangjf
		 * @date    2016年2月23日 下午8:36:43
		 * @param wealthHoldMap
		 * @return
		 */
		public WealthHoldInfoEntity createWealthHoldInfo(Map<String, Object> wealthHoldMap) {
			WealthHoldInfoEntity wealthHoldInfoEntity = new WealthHoldInfoEntity();
			wealthHoldInfoEntity.setId((String)wealthHoldMap.get("id"));
			wealthHoldInfoEntity.setHoldScale(wealthHoldMap.get("holdScale") == null ? BigDecimal.ZERO : new BigDecimal(wealthHoldMap.get("holdScale").toString()));
			wealthHoldInfoEntity.setHoldStatus((String)wealthHoldMap.get("holdStatus"));
			wealthHoldInfoEntity.setVersion(Integer.valueOf(wealthHoldMap.get("version").toString()));
			
			return wealthHoldInfoEntity;
		}
		
	}

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo autoUnReleaseWealth(Map<String, Object> params)throws SLException {

		// 1）当前时间大于生效日且优选计划状态为待审核
		// 2）当前时间大于生效日且优选计划状态为未发布
		// 3）当前时间大于生效日且优选计划状态为发布中(且没有投资)
		List<WealthInfoEntity> wealthList = wealthInfoRepository.findCanUnReleaseWealth(DateUtils.getAfterDay(new Date(), 1), 
				Constant.WEALTH_STATUS_01, Constant.WEALTH_STATUS_04, Constant.WEALTH_STATUS_05);
		
		for(WealthInfoEntity wealth : wealthList) {

			WealthTypeInfoEntity wealthTypeInfoEntity = wealthTypeInfoRepository.findOne(wealth.getWealthTypeId());
			if(wealthTypeInfoEntity == null) {
				log.warn("计划{}不存在，流标失败！", wealth.getId());
				continue;
			}
			
			// 修改状态
			wealth.setWealthStatus(Constant.WEALTH_STATUS_10);
			wealth.setFlowStatus(Constant.WEALTH_STATUS_10);
			wealth.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
			
			// 日志
			LogInfoEntity logInfoEntity = new LogInfoEntity();
			logInfoEntity.setRelateType(Constant.TABLE_BAO_T_WEALTH_INFO);
			logInfoEntity.setRelatePrimary(wealth.getId());
			logInfoEntity.setLogType(Constant.OPERATION_TYPE_52);
			logInfoEntity.setOperDesc("");
			logInfoEntity.setOperPerson(Constant.SYSTEM_USER_BACK);
			logInfoEntity.setMemo(String.format("计划%s[%s]流标成功", wealthTypeInfoEntity.getLendingType(), wealth.getLendingNo()));
			logInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
			logInfoEntityRepository.save(logInfoEntity);
			
		}
		
		return new ResultVo(true, "流标成功");
	}

	/**
	 * 自动撮合--自有操作的时候才会有事物
	 * 1:查询所有的投资状态为收益中的分账中有钱的数据（需要撮合）
	 * 2：查询规则表信息
	 * 3:取出符合条件的债权、筛选债权
	 * 4:匹配操作
	 */
	@Override
	public ResultVo autoMatchLoan(Map<String, Object> params)throws SLException {
		String userId = Constant.SYSTEM_USER_BACK;
		List<Map<String, Object>> investList = investInfoRepositoryCustom.findNeedMatchInvestInfo(params); //需要匹配债权的列表
		if(null == investList || investList.size() == 0) {
			return new ResultVo(false, "没有待匹配的投资！");
		}
		List<AutoMatchRuleEntity> investAutoMatchRuleList = new ArrayList<AutoMatchRuleEntity>();
		List<AutoMatchRuleEntity> autoMatchRuleList = (List<AutoMatchRuleEntity>) autoMatchRuleRepository.findAll(); //所有规则表信息
		// 1) 通过投资金额找到匹配的自动撮合规则
		for(Map<String, Object> map : investList) {
			boolean isExists = false;
			BigDecimal accountAmount = (BigDecimal) map.get("accountAmount");
			for(AutoMatchRuleEntity autoMatchRuleEntity : autoMatchRuleList) {
				if(accountAmount.compareTo(autoMatchRuleEntity.getInvestMiniAmt()) > 0 && accountAmount.compareTo(autoMatchRuleEntity.getInvestMaxAmt()) <= 0) {
					investAutoMatchRuleList.add(autoMatchRuleEntity);
					isExists = true;
					break;
				}
			}
			
			if(!isExists) { //如果未找到规则， 则没什么规则
				AutoMatchRuleEntity autoMatchRuleEntity = new AutoMatchRuleEntity();
				autoMatchRuleEntity.setDebtMiniAmt(new BigDecimal("0"));
				autoMatchRuleEntity.setDebtMaxAmt(new BigDecimal("200000000"));
				investAutoMatchRuleList.add(autoMatchRuleEntity);
			}
		}
		
		// 2) 遍历撮合
		for(int i = 0; i < investList.size(); i++) {
			try {
				Map<String, Object> investMap = investList.get(i);
				
				//2-1 找出符合条件的债权
				Map<String, Object> result = findOneAutoMatch(investMap, investAutoMatchRuleList.get(i), userId);
				
				//2-2 匹配操作
				wealthInfoService.autoMatchLoan(result);
				
			} catch(SLException e) {
				e.printStackTrace();
			}
			
		}
		return new ResultVo(true, "匹配成功！");
	}
	
	/**
	 * 找出满足条件的债权
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> findOneAutoMatch(Map<String, Object> investMap, AutoMatchRuleEntity autoMatchRuleEntity, String userId) throws SLException{
		//查找出满足条件的债权
		Map<String, Object> queryMap = Maps.newHashMap();
		queryMap.put("isCenter", Constant.IS_CENTER_01); //居间人客户id
		queryMap.put("investId", investMap.get("investId")); //投资id
		List<Map<String, Object>> canMatchList = investInfoRepositoryCustom.findAllCanAutoMatchInfo(queryMap); //可用债权
		Map<String, Object> classificaitionMatch = classificationMatchLoan(autoMatchRuleEntity, canMatchList); //分类债权
		
		BigDecimal needMatchAmount = new BigDecimal(investMap.get("accountAmount").toString()); //需要匹配金额
		List<Map<String, Object>> alreadyMatchList = Lists.newArrayList(); //已经匹配债权
		needMatchAmount = obtainMatchInfo((List<Map<String, Object>>) classificaitionMatch.get("inMatchRule"), alreadyMatchList, needMatchAmount, autoMatchRuleEntity.getDebtMaxAmt()); //满足条件的匹配
		needMatchAmount = obtainMatchInfo((List<Map<String, Object>>) classificaitionMatch.get("gtMatchRule"), alreadyMatchList, needMatchAmount, autoMatchRuleEntity.getDebtMaxAmt()); //大于区间的匹配
		needMatchAmount = obtainMatchInfo((List<Map<String, Object>>) classificaitionMatch.get("otherMatchRule"), alreadyMatchList, needMatchAmount, autoMatchRuleEntity.getDebtMaxAmt()); //剩余所有债权的匹配
		investMap.put("match_list", alreadyMatchList);
		if(alreadyMatchList.size() == 0) {
			throw new SLException("没有符合条件的债权！");
		}
		return investMap;
	}
	
	/**
	 * 获取债权
	 * 
	 * @date 2016-02-29
	 * @author zhiwen_feng
	 * @param canMatchList
	 * @param alreadyMatch
	 * @param needMatchAmount
	 * @return
	 */
	public BigDecimal obtainMatchInfo(List<Map<String, Object>> canMatchList, List<Map<String, Object>> alreadyMatch, BigDecimal needMatchAmount, BigDecimal debtMaxAmt) {
		for (Map<String, Object> match : canMatchList) {
			//带匹配金额==0是直接返回
			if(needMatchAmount.compareTo(BigDecimal.ZERO) == 0) {
				return needMatchAmount;
			}
			BigDecimal holdAmount = new BigDecimal(match.get("holdAmount").toString()); //可用金额
			
			if (needMatchAmount.compareTo(debtMaxAmt) >= 0) { //带匹配金额 >= 最大可匹配债权
				
				if (debtMaxAmt.compareTo(holdAmount) >= 0) { // 最大可匹配债权 >= 可用金额 （=可用金额）
					match.put("match_Amount", holdAmount);
					needMatchAmount = ArithUtil.sub(needMatchAmount, holdAmount); 
					alreadyMatch.add(match);
				}else { //可用金额 》 最大可匹配债权 （=最大可匹配债权）
					match.put("match_Amount", debtMaxAmt);
					needMatchAmount = ArithUtil.sub(needMatchAmount, debtMaxAmt); 
					alreadyMatch.add(match);
				}
			}else { //持有金额 < investMaxAmt
				if (needMatchAmount.compareTo(holdAmount) > 0) { // 带匹配金额 >= 可用金额 （=可用金额）
					match.put("match_Amount", holdAmount);
					needMatchAmount = ArithUtil.sub(needMatchAmount, holdAmount); 
					alreadyMatch.add(match);
				}else { //可用金额 > 带匹配金额 （=带匹配金额 ）
					match.put("match_Amount", needMatchAmount);
					needMatchAmount = BigDecimal.ZERO; 
					alreadyMatch.add(match);
				}
			}
			
		}
		return needMatchAmount;
	}
	
	/**
	 * 债权分类--
	 * 		分成三个List 1：在匹配范围内的债权 2：大于债权范围最大值得 3：剩余的债权
	 * @author zhiwen_feng
	 * @param autoMatchRuleEntity
	 * @param canMatchList
	 * @return
	 */
	public Map<String, Object> classificationMatchLoan(AutoMatchRuleEntity autoMatchRuleEntity, List<Map<String, Object>> canMatchList){
		Map<String, Object> result = Maps.newHashMap();
		List<Map<String, Object>> inMatchRule = Lists.newArrayList();
		List<Map<String, Object>> gtMatchRule = Lists.newArrayList();
		List<Map<String, Object>> otherMatchRule = Lists.newArrayList();
		for (Map<String, Object> match : canMatchList) {
			BigDecimal holdAmount = new BigDecimal(match.get("holdAmount").toString());
			if(holdAmount.compareTo(autoMatchRuleEntity.getDebtMiniAmt()) >= 0 
					&& holdAmount.compareTo(autoMatchRuleEntity.getDebtMaxAmt()) < 0) { //大于等于最小可匹配债权金额&&小于最大可匹配债权金额
				inMatchRule.add(match);
			}else if (holdAmount.compareTo(autoMatchRuleEntity.getDebtMaxAmt()) >= 0) { //持有价值大于等于最大可匹配债权金额
				gtMatchRule.add(match);
			}else { //其他
				otherMatchRule.add(match);
			}
		}
		result.put("inMatchRule", inMatchRule);
		result.put("gtMatchRule", gtMatchRule);
		result.put("otherMatchRule", otherMatchRule);
		
		return result;
		
	}
	

	@Override
	public ResultVo autoMonthlyWealth(Map<String, Object> params)
			throws SLException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 自动发布优选计划
	 */
	@Override
	public ResultVo autoPublishWealth(Map<String, Object> params)throws SLException {
		String currentDate = DateUtilSL.dateToStr(new Date());
		List<WealthInfoEntity> list = wealthInfoRepository.findByReleaseDateWealthStatus(Constant.WEALTH_STATUS_04, currentDate); //当前日期待发布列表
		Map<String, Object> map = Maps.newHashMap();
		map.put("userId", "ROOT");
		ResultVo result = null;
		for(WealthInfoEntity o : list) {
			List<WealthInfoEntity> WealthInfoEntityList = wealthInfoRepository.findByWealthTypeIdAndWealthStatusOrderByEffectDate(o.getWealthTypeId(), Constant.WEALTH_STATUS_05);
			//当已有该类性的计划不发布
			if(WealthInfoEntityList != null && WealthInfoEntityList.size() > 0) 
				continue;
			map.put("wealthId", o.getId());
			result = wealthInfoService.publishWealth(map);
		}
		return result;
	}

	/**
	 * 自动生效优选计划
	 */
	@Override
	public ResultVo autoReleaseWealthJob(Map<String, Object> params) throws SLException {
		List<WealthInfoEntity> effectWealth = wealthInfoRepository.findAutoReleaseWealth(); //需要生效的优选计划
		ResultVo result = new ResultVo(true, "生效成功！");
		for(WealthInfoEntity o : effectWealth) {			
			// 非已满额在23点之前不予生效
			if(!Constant.WEALTH_STATUS_06.equals(o.getWealthStatus()) 
					&& DateUtils.getCurrentDate("HH:mm:ss").compareTo("23:00:00") < 0) { 
				continue;
			}
			Map<String, Object> map = Maps.newHashMap();
			map.put("userId", Constant.SYSTEM_USER_BACK);
			map.put("wealthId", o.getId());
			result = wealthInfoService.autoReleaseWealth(map);
		}
		return result;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, rollbackFor = SLException.class)
	@Override
	public ResultVo publishWealth(Map<String, Object> params)throws SLException {
		return wealthInfoService.publishWealth(params);
	}
	
}
