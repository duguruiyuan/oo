/** 
 * @(#)RepaymentServiceImpl.java 1.0.0 2015年5月1日 下午4:19:43  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AccountInfoEntity;
import com.slfinance.shanlincaifu.entity.RepaymentPlanInfoEntity;
import com.slfinance.shanlincaifu.entity.RepaymentRecordInfoEntity;
import com.slfinance.shanlincaifu.entity.SubAccountInfoEntity;
import com.slfinance.shanlincaifu.repository.AccountInfoRepository;
import com.slfinance.shanlincaifu.repository.RepaymentPlanInfoRepository;
import com.slfinance.shanlincaifu.repository.RepaymentRecordInfoRepository;
import com.slfinance.shanlincaifu.repository.SubAccountInfoRepository;
import com.slfinance.shanlincaifu.service.AccountFlowService;
import com.slfinance.shanlincaifu.service.FlowNumberService;
import com.slfinance.shanlincaifu.service.RepaymentService;
import com.slfinance.shanlincaifu.utils.ArithUtil;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.SubjectConstant;
import com.slfinance.vo.ResultVo;

/**   
 * 还款接口实现类
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年5月1日 下午4:19:43 $ 
 */
@Service("repaymentService")
public class RepaymentServiceImpl implements RepaymentService {

	@Autowired
	private RepaymentPlanInfoRepository repaymentPlanInfoRepository;
	
	@Autowired
	private RepaymentRecordInfoRepository repaymentRecordInfoRepository;
	
	@Autowired
	private AccountInfoRepository accountInfoRepository;
	
	@Autowired
	private SubAccountInfoRepository subAccountInfoRepository;
	
	@Autowired
	private FlowNumberService numberService;
	
	@Autowired
	private AccountFlowService accountFlowService;	
	
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo repaymentJob(Map<String, Object> param) throws SLException {
		// 1 取还款计划数据
		List<RepaymentPlanInfoEntity> repaymentPlanList = repaymentPlanInfoRepository.findByExpectRepaymentDate((String)param.get("expectRepaymentDate"), Constant.REPAYMENT_STATUS_WAIT);
		
		// 2 新增还款记录与更新还款计划
		for(RepaymentPlanInfoEntity entity : repaymentPlanList){
			RepaymentRecordInfoEntity repaymentRecordInfoEntity = new RepaymentRecordInfoEntity();
			repaymentRecordInfoEntity.setLoanId(entity.getLoanEntity().getId());
			repaymentRecordInfoEntity.setRepayAmount(entity.getRepaymentTotalAmount());
			repaymentRecordInfoEntity.setAlreadyRepayAmt(new BigDecimal("0"));
			repaymentRecordInfoEntity.setHandleStatus(Constant.TRADE_STATUS_01);
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
		}
	
		// 3 处理还款记录
		// 取已分配未处理的还款记录，包含之前未处理与此次新增部分
		SubAccountInfoEntity earnSubAccount = subAccountInfoRepository.findBySubAccountNo(Constant.SUB_ACCOUNT_NO_ERAN); // 公司收益分账户
		SubAccountInfoEntity centerSubAccount = subAccountInfoRepository.findBySubAccountNo(Constant.SUB_ACCOUNT_NO_CENTER); // 公司居间人分账户
		AccountInfoEntity earnMainAccount = accountInfoRepository.findOne(earnSubAccount.getAccountId()); // 公司收益主账户
		AccountInfoEntity centerMainAccount = accountInfoRepository.findOne(centerSubAccount.getAccountId()); // 公司居间人主账户
		AccountInfoEntity riskMainAccount = accountInfoRepository.findByAccountNo(Constant.ACCOUNT_NO_RISK); // 公司风险金主账户
		SubAccountInfoEntity earnSubAccount12 = subAccountInfoRepository.findBySubAccountNo(Constant.SUB_ACCOUNT_NO_ERAN_12); // 公司收益分账户
		SubAccountInfoEntity centerSubAccount11 = subAccountInfoRepository.findBySubAccountNo(Constant.SUB_ACCOUNT_NO_CENTER_11); // 公司居间人分账户

		// 活期宝还款
		repaymentAllotLoan(earnMainAccount, earnSubAccount, centerMainAccount, centerSubAccount, riskMainAccount, Constant.PRODUCT_TYPE_01);
		// 定期宝还款
		repaymentAllotLoan(earnMainAccount, earnSubAccount12, centerMainAccount, centerSubAccount11, riskMainAccount, Constant.PRODUCT_TYPE_04);

		// 取未分配未处理的还款记录
		List<RepaymentRecordInfoEntity> repaymentRecordListOut = repaymentRecordInfoRepository.queryUnHandleNotAllot(Constant.TRADE_STATUS_01);
		for(RepaymentRecordInfoEntity entity : repaymentRecordListOut){
			
			// 未分配的还款直接将金额打到居间人主账户
			BigDecimal tradeAmount = ArithUtil.sub(entity.getRepayAmount(), entity.getAlreadyRepayAmt());
			String reqeustNo = numberService.generateTradeBatchNumber();
			
			// 风险金账户金额减少
			riskMainAccount.setAccountTotalAmount(ArithUtil.sub(riskMainAccount.getAccountTotalAmount(), tradeAmount));
			riskMainAccount.setAccountAvailableAmount(ArithUtil.sub(riskMainAccount.getAccountAvailableAmount(), tradeAmount));
			riskMainAccount.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
			
			accountFlowService.saveAccountFlow(riskMainAccount, null, null, null, "1", 
					SubjectConstant.TRADE_FLOW_TYPE_REPAY, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, 
					tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_REPAY, 
					Constant.TABLE_BAO_T_REPAYMENT_RECORD_INFO, entity.getId(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
			
			// 主账户现金增加
			centerMainAccount.setAccountTotalAmount(ArithUtil.add(centerMainAccount.getAccountTotalAmount(), tradeAmount));
			centerMainAccount.setAccountAvailableAmount(ArithUtil.add(centerMainAccount.getAccountAvailableAmount(), tradeAmount));
			centerMainAccount.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
			
			accountFlowService.saveAccountFlow(centerMainAccount, null, null, null, "1", 
					SubjectConstant.TRADE_FLOW_TYPE_REPAY, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING, 
					tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_REPAY, 
					Constant.TABLE_BAO_T_REPAYMENT_RECORD_INFO, entity.getId(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
			
			entity.setAlreadyRepayAmt(tradeAmount);
			entity.setHandleStatus(Constant.TRADE_STATUS_03);
		}
		
		return new ResultVo(true);
	}
	
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public void repaymentAllotLoan(AccountInfoEntity earnMainAccount, SubAccountInfoEntity earnSubAccount, 
			AccountInfoEntity centerMainAccount, SubAccountInfoEntity centerSubAccount, AccountInfoEntity riskMainAccount, String typeName) {
		List<RepaymentRecordInfoEntity> repaymentRecordListIn = repaymentRecordInfoRepository.queryUnHandleAllot(Constant.TRADE_STATUS_01, typeName);
		for(RepaymentRecordInfoEntity entity : repaymentRecordListIn){
			
			// 待处理金额
			BigDecimal tradeAmount = ArithUtil.sub(entity.getRepayAmount(), entity.getAlreadyRepayAmt());
			String reqeustNo = numberService.generateTradeBatchNumber();
			
			// 若待处理金额等于0则不再处理
			if(tradeAmount.compareTo(new BigDecimal("0")) == 0){
				// 设置已处理金额 = 已处理金额 + (待处理金额-剩余金额)
				entity.setAlreadyRepayAmt(ArithUtil.add(entity.getAlreadyRepayAmt(), ArithUtil.sub(ArithUtil.sub(entity.getRepayAmount(), entity.getAlreadyRepayAmt()), tradeAmount)));
				// 金额为0表示该笔还款已全部处理，将状态设置为已处理
				entity.setHandleStatus(Constant.TRADE_STATUS_03);
				continue;
			}
			
			// 3.1 处理公司居间人分账户
			if(centerSubAccount.getAccountAvailableValue().compareTo(tradeAmount) >= 0){
				
				// 处理居间人账户
				saveAccount(centerMainAccount, centerSubAccount, riskMainAccount, tradeAmount, reqeustNo, entity);
				
				// 未处理金额减少
				tradeAmount = ArithUtil.sub(tradeAmount, tradeAmount);
			}
			else if(centerSubAccount.getAccountAvailableValue().compareTo(new BigDecimal("0")) > 0){// 只有居间人账户可用价值大于0时才处理
				
				// 此处需处理所有居间人分账户金额
				BigDecimal transferAmount = centerSubAccount.getAccountAvailableValue(); 
				
				// 处理居间人账户
				saveAccount(centerMainAccount, centerSubAccount, riskMainAccount, transferAmount, reqeustNo, entity);
				
				// 未处理金额减少
				tradeAmount = ArithUtil.sub(tradeAmount, transferAmount);
			}
			
			// 若待处理金额等于0则不再处理
			if(tradeAmount.compareTo(new BigDecimal("0")) == 0){
				// 设置已处理金额 = 已处理金额 + (待处理金额-剩余金额)
				entity.setAlreadyRepayAmt(ArithUtil.add(entity.getAlreadyRepayAmt(), ArithUtil.sub(ArithUtil.sub(entity.getRepayAmount(), entity.getAlreadyRepayAmt()), tradeAmount)));
				// 金额为0表示该笔还款已全部处理，将状态设置为已处理
				entity.setHandleStatus(Constant.TRADE_STATUS_03);
				continue;
			}
			
			// 3.2 处理公司收益分账户
			if(earnSubAccount.getAccountAvailableValue().compareTo(tradeAmount) >= 0){
				
				// 处理收益账户
				saveAccount(earnMainAccount, earnSubAccount, riskMainAccount, tradeAmount, reqeustNo, entity);
				
				// 未处理金额减少
				tradeAmount = ArithUtil.sub(tradeAmount, tradeAmount);
			}
			else if(earnSubAccount.getAccountAvailableValue().compareTo(new BigDecimal("0")) > 0){// 只有收益人账户可用价值大于0时才处理
				
				// 此处需处理所有收益分账户金额
				BigDecimal transferAmount = earnSubAccount.getAccountAvailableValue(); 
				
				// 处理收益账户
				saveAccount(earnMainAccount, earnSubAccount, riskMainAccount, transferAmount, reqeustNo, entity);
				
				// 未处理金额减少
				tradeAmount = ArithUtil.sub(tradeAmount, transferAmount);
			}
			
			// 设置已处理金额 = 已处理金额 + (待处理金额-剩余金额)
			entity.setAlreadyRepayAmt(ArithUtil.add(entity.getAlreadyRepayAmt(), ArithUtil.sub(ArithUtil.sub(entity.getRepayAmount(), entity.getAlreadyRepayAmt()), tradeAmount)));
			// 金额为0表示该笔还款已全部处理，将状态设置为已处理
			if(tradeAmount.compareTo(new BigDecimal("0")) == 0){
				entity.setHandleStatus(Constant.TRADE_STATUS_03);
			}
		}
	}
	
	/**
	 * 保存账户数据
	 *
	 * @author  wangjf
	 * @date    2015年5月1日 下午6:11:55
	 * @param centerMainAccount
	 * @param centerSubAccount
	 * @param tradeAmount
	 * @param reqeustNo
	 * @param entity
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public void saveAccount(AccountInfoEntity centerMainAccount, SubAccountInfoEntity centerSubAccount, AccountInfoEntity riskMainAccount,
			BigDecimal tradeAmount, String reqeustNo, RepaymentRecordInfoEntity entity)
	{
		// 3.1.1 风险金账户金额减少
		riskMainAccount.setAccountTotalAmount(ArithUtil.sub(riskMainAccount.getAccountTotalAmount(), tradeAmount));
		riskMainAccount.setAccountAvailableAmount(ArithUtil.sub(riskMainAccount.getAccountAvailableAmount(), tradeAmount));
		riskMainAccount.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
		
		accountFlowService.saveAccountFlow(riskMainAccount, null, null, null, "1", 
				SubjectConstant.TRADE_FLOW_TYPE_REPAY, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, 
				tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_REPAY, 
				Constant.TABLE_BAO_T_REPAYMENT_RECORD_INFO, entity.getId(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
		
		// 3.1.2 居间人分账户价值减少
		centerSubAccount.setAccountTotalValue(ArithUtil.sub(centerSubAccount.getAccountTotalValue(), tradeAmount));
		centerSubAccount.setAccountAvailableValue(ArithUtil.sub(centerSubAccount.getAccountAvailableValue(), tradeAmount));
		centerSubAccount.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
		
		accountFlowService.saveAccountFlow(centerMainAccount, centerSubAccount, centerMainAccount, centerSubAccount, "4", 
				SubjectConstant.TRADE_FLOW_TYPE_REPAY, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, 
				tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_REPAY, 
				Constant.TABLE_BAO_T_REPAYMENT_RECORD_INFO, entity.getId(), SubjectConstant.SUBJECT_TYPE_VALUE);
		
		// 3.1.3 居间人分账户现金增加
		centerSubAccount.setAccountAmount(ArithUtil.add(centerSubAccount.getAccountAmount(), tradeAmount));
		
		accountFlowService.saveAccountFlow(centerMainAccount, centerSubAccount, centerMainAccount, centerSubAccount, "4", 
				SubjectConstant.TRADE_FLOW_TYPE_REPAY, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING, 
				tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_REPAY, 
				Constant.TABLE_BAO_T_REPAYMENT_RECORD_INFO, entity.getId(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
		
		// 3.1.4 居间人分账户现金减少
		centerSubAccount.setAccountAmount(ArithUtil.sub(centerSubAccount.getAccountAmount(), tradeAmount));
		
		accountFlowService.saveAccountFlow(centerMainAccount, centerSubAccount, centerMainAccount, centerSubAccount, "3", 
				SubjectConstant.TRADE_FLOW_TYPE_REPAY, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, 
				tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_REPAY, 
				Constant.TABLE_BAO_T_REPAYMENT_RECORD_INFO, entity.getId(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
		
		// 3.1.5 居间人主账户现金增加
		centerMainAccount.setAccountTotalAmount(ArithUtil.add(centerMainAccount.getAccountTotalAmount(), tradeAmount));
		centerMainAccount.setAccountAvailableAmount(ArithUtil.add(centerMainAccount.getAccountAvailableAmount(), tradeAmount));
		centerMainAccount.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
		
		accountFlowService.saveAccountFlow(centerMainAccount, centerSubAccount, centerMainAccount, centerSubAccount, "2", 
				SubjectConstant.TRADE_FLOW_TYPE_REPAY, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING, 
				tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_REPAY, 
				Constant.TABLE_BAO_T_REPAYMENT_RECORD_INFO, entity.getId(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
	}
}
