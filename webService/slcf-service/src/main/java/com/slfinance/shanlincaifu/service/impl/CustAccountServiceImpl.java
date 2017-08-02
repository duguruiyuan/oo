/** 
 * @(#)CustAccountServiceImpl.java 1.0.0 2015年5月11日 下午3:46:04  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.impl;

import com.google.common.collect.Lists;
import com.slfinance.exception.SLException;
import com.slfinance.exception.SLSystemException;
import com.slfinance.shanlincaifu.entity.AccountFlowInfoEntity;
import com.slfinance.shanlincaifu.entity.AccountInfoEntity;
import com.slfinance.shanlincaifu.entity.SubAccountInfoEntity;
import com.slfinance.shanlincaifu.entity.TradeFlowInfoEntity;
import com.slfinance.shanlincaifu.repository.AccountFlowInfoRepository;
import com.slfinance.shanlincaifu.repository.AccountInfoRepository;
import com.slfinance.shanlincaifu.repository.CustInfoRepository;
import com.slfinance.shanlincaifu.repository.SubAccountInfoRepository;
import com.slfinance.shanlincaifu.service.AccountFlowService;
import com.slfinance.shanlincaifu.service.CustAccountService;
import com.slfinance.shanlincaifu.service.FlowNumberService;
import com.slfinance.shanlincaifu.service.ParamService;
import com.slfinance.shanlincaifu.utils.ArithUtil;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.SubjectConstant;
import com.slfinance.vo.ResultVo;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**   
 * 更新账户信息和新增流水服务接口实现
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年5月11日 下午3:46:04 $ 
 */
@Slf4j
@Service
public class CustAccountServiceImpl implements CustAccountService {

	@Autowired
	private AccountInfoRepository accountInfoRepository;
	
	@Autowired
	private SubAccountInfoRepository subAccountInfoRepository;
	
//	@Autowired
//	private AccountFlowDetailRepository accountFlowDetailRepository;
	
	@Autowired
	private AccountFlowInfoRepository accountFlowInfoRepository;

	@Autowired
	private CustInfoRepository custInfoRepository;
	
	@Autowired
	private ParamService paramService;
	
	@Autowired
	private FlowNumberService numberService;
	
	@Autowired
	private AccountFlowService accountFlowService;
	
	/**
	 *更新客户账户信息,并且记录账户流水详情信息
	 * @throws SLException 
	 */
	@Transactional(readOnly=false,rollbackFor=SLException.class)
	@Override
	public ResultVo updateCustAccount(AccountFlowInfoEntity accountFlowInfo,AccountInfoEntity accountInfo) throws SLException {
		validateAccountFlowInfo(accountFlowInfo);
		if (!Constant.ACCOUNT_TYPE_MAIN.equals(accountFlowInfo.getAccountType()))
			throw new SLSystemException("账户资金流水记账，主账不能为空");

//		List<AccountFlowDetailEntity> accountFlowDetailList = new ArrayList<AccountFlowDetailEntity>();
		List<AccountInfoEntity> custAccountInfoList = new ArrayList<AccountInfoEntity>();
		switch (accountFlowInfo.getTradeType()) {
		case SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_WITHDRAW: // 提现
			// 账户划扣
			withDrawAccount(accountInfo, accountFlowInfo);
			//提现操作时，账户流水的提现流水详情，交易金额=提现金额-提现手续费
//			accountFlowDetailList.add(accountFlowDetail);
			//提现申请和提现手续费修改为分别记账户流水信息
//			AccountFlowDetailEntity withdrawDatailExpense = new AccountFlowDetailEntity(accountFlowDetail.getAccountFlowId(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_WITHDRAW_EXPENSE, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, withdrawExpense, Constant.ACCOUNT_ID_ERAN, "提现手续费");
//			withdrawDatailExpense.setBasicModelProperty(accountFlowDetail.getCreateUser(), true);
//			withDrawAccount(accountInfo, accountFlowInfo);
//			accountFlowDetailList.add(withdrawDatailExpense);
			accountFlowInfo.setTargetAccount(Constant.ACCOUNT_ID_ERAN);
			custAccountInfoList.add(accountInfo);
			break;
		case SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_UNFREEZE_WITHDRAW: //解冻 
			unFreezeAccount(accountInfo, accountFlowInfo);
//			accountFlowDetailList.add(accountFlowDetail);
			custAccountInfoList.add(accountInfo);
			break;
		}
		updateAccountBatch(custAccountInfoList);
		return new ResultVo(true);
	}
	
	/**
	 * 批量更新流水记录、主账户和分账户 入库
	 * 
	 * @param accountFlowDetailList
	 *            待插入流水记录
	 * @param custAccountInfoList
	 *            待更新主账户
	 * @throws SLException
	 */
	public void updateAccountBatch(List<AccountInfoEntity> accountInfoList) throws SLException {
//		if (accountFlowDetailList.size() < 1)
//			throw new SLSystemException("账户资金流水记账，无需更新流水记录");
		if ((accountInfoList == null || accountInfoList.size() < 1) )
			throw new SLSystemException("账户资金流水记账，无需更新的主账户");
		
//		// 批量插入流水号
//		accountFlowDetailRepository.save((Iterable<AccountFlowDetailEntity>)accountFlowDetailList);

		// 更新主账户
		if (accountInfoList != null) {
			for (AccountInfoEntity c : accountInfoList) {
				// 判断账户总额是否小于0？
				if (c.getAccountTotalAmount().compareTo(new BigDecimal("0")) < 0) {
					log.warn(String.format("客户%s账户总额不足", c.getAccountNo()));
					throw new SLException("账户总额不足");
				}
				// 判断可用金额是否小于0？
				if (c.getAccountAvailableAmount().compareTo(new BigDecimal("0")) < 0) {
					log.warn(String.format("客户%s账户总额不足", c.getAccountNo()));
					throw new SLException("账户可用金额不足");
				}
				// 判断冻结金额是否小于0？
				if (c.getAccountFreezeAmount().compareTo(new BigDecimal("0")) < 0) {
					log.warn(String.format("客户%s账户总额不足", c.getAccountNo()));
					throw new SLException("账户冻结金额不足");
				}
				//更新账户信息
				AccountInfoEntity accountInfo = accountInfoRepository.findOne(c.getId());
				AccountInfoEntity accountInfoUpd = new AccountInfoEntity();
				accountInfoUpd.setAccountAvailableAmount(c.getAccountAvailableAmount());
				accountInfoUpd.setAccountFreezeAmount(c.getAccountFreezeAmount());
				accountInfoUpd.setAccountTotalAmount(c.getAccountTotalAmount());
				accountInfoUpd.setBasicModelProperty(c.getLastUpdateUser(), false);
				if(!accountInfo.updAccountInfo(accountInfoUpd))
					throw new SLException(String.format("更新%s账户信息失败", c.getAccountNo()));
			}
		}
	}

	
	
	/***
	 * 更新客户账户信息,并且记录账户流水详情信息
	 */
	@Transactional(readOnly=false,rollbackFor={SLException.class,SLSystemException.class})
	@Override
	public ResultVo updateCustAccount(String memo,TradeFlowInfoEntity tradeFlowInfo, AccountFlowInfoEntity accountFlowInfo,AccountInfoEntity accountInfo,String tradeType) throws SLException {
		validateAccountFlowInfo(accountFlowInfo);
		if (!Constant.ACCOUNT_TYPE_MAIN.equals(accountFlowInfo.getAccountType()))
			throw new SLSystemException("账户资金流水记账，主账不能为空");

		/**提现手续费**/
		BigDecimal withDrawExpense = paramService.findWithDrawExpenseAmount();
		
		switch (tradeType) {
		case SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_UNFREEZE_WITHDRAW: //解冻 
			unFreezeAccount(accountInfo, accountFlowInfo);
			/**记录解冻流水和解冻流水详情**/
			AccountFlowInfoEntity unFreezeFlow = new AccountFlowInfoEntity(accountInfo.getId(), accountInfo.getCustId(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_UNFREEZE_WITHDRAW, numberService.generateTradeNumber(), accountFlowInfo.getTradeNo(),accountFlowInfo.getRequestNo(),"", accountFlowInfo.getTradeAmount(), new Date(), memo, Constant.ACCOUNT_TYPE_MAIN,accountInfo.getAccountTotalAmount(), accountInfo.getAccountFreezeAmount(), accountInfo.getAccountAvailableAmount(), SubjectConstant.SUBJECT_TYPE_AMOUNT, BigDecimal.ZERO);
			unFreezeFlow.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
			
			/**提现解冻 记录账户流水详情更新账户表**/ 
//			AccountFlowDetailEntity accountFlowDetail = new AccountFlowDetailEntity(unFreezeFlow.getId(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_UNFREEZE_WITHDRAW, "", accountFlowInfo.getTradeAmount(), "", "提现解冻");
//			accountFlowDetail.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
			
			updateAccountBatch(unFreezeFlow , accountInfo);
			break;
		case SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_WITHDRAW_EXPENSE://提现手续费
			/**账户总额和可用金额减去提现手续费**/
			withDrawExpensAccount(accountInfo, accountFlowInfo, withDrawExpense);
			/**新增提现手续费流水**/
			AccountFlowInfoEntity withDrawExpenseFlow = new AccountFlowInfoEntity(accountInfo.getId(), accountInfo.getCustId(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_WITHDRAW_EXPENSE, numberService.generateTradeNumber(), "",accountFlowInfo.getRequestNo(),SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, withDrawExpense, new Date(), "", Constant.ACCOUNT_TYPE_MAIN,accountInfo.getAccountTotalAmount(), accountInfo.getAccountFreezeAmount(), accountInfo.getAccountAvailableAmount(), SubjectConstant.SUBJECT_TYPE_AMOUNT, BigDecimal.ZERO);
			withDrawExpenseFlow.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
			
			/**新增提现手续费流水详情**/
//			AccountFlowDetailEntity withdrawDatailExpense = new AccountFlowDetailEntity(withDrawExpenseFlow.getId(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_WITHDRAW_EXPENSE, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, withDrawExpense, "", "提现手续费");
//			withdrawDatailExpense.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
			
			updateAccountBatch(withDrawExpenseFlow , accountInfo);
			break;
		case SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_WITHDRAW: // 提现
			withDrawAccount(accountInfo, accountFlowInfo,withDrawExpense);
			/**记录提现帐户流水**/
			AccountFlowInfoEntity withDrawFlow = new AccountFlowInfoEntity(accountInfo.getId(), accountInfo.getCustId(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_WITHDRAW, numberService.generateTradeNumber(), "",accountFlowInfo.getRequestNo(),SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, ArithUtil.sub(accountFlowInfo.getTradeAmount(), withDrawExpense), new Date(), "", Constant.ACCOUNT_TYPE_MAIN,accountInfo.getAccountTotalAmount(), accountInfo.getAccountFreezeAmount(), accountInfo.getAccountAvailableAmount(), SubjectConstant.SUBJECT_TYPE_AMOUNT, BigDecimal.ZERO);
			withDrawFlow.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
			
			/**记录提现帐户流水详情**/
//			AccountFlowDetailEntity accountDetailWithDraw = new AccountFlowDetailEntity(withDrawFlow.getId(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_WITHDRAW, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, ArithUtil.sub(accountFlowInfo.getTradeAmount(), withDrawExpense), "", "提现");
//			accountDetailWithDraw.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
			
			updateAccountBatch(withDrawFlow , accountInfo);
			break;
		}
		return new ResultVo(true);
		
	}
	
	/**
	 * 批量更新流水记录、主账户和分账户 入库
	 * 
	 * @param accountFlowDetailList
	 *            待插入流水记录
	 * @param custAccountInfoList
	 *            待更新主账户
	 * @throws SLException
	 */
	public void updateAccountBatch(AccountFlowInfoEntity accountFlow , AccountInfoEntity accountInfo) throws SLException {
		
		/**插入账户流水信息和插入账户流水详情信息**/
		if (accountFlow != null ){
			accountFlowInfoRepository.save(accountFlow);
//			if ( accountFlowDetail != null)
//				accountFlowDetail.setAccountFlowId(accountFlow.getId());
//				accountFlowDetailRepository.save(accountFlowDetail);
		}

		// 更新用户账户的可用余额 、总金额、冻结金额
		if (accountInfo != null) {
			// 判断账户总额是否小于0？
			if (accountInfo.getAccountTotalAmount().compareTo(new BigDecimal("0")) < 0) {
				log.warn(String.format("客户%s账户总额不足", accountInfo.getAccountNo()));
				throw new SLException("账户总额不足");
			}
			// 判断可用金额是否小于0？
			if (accountInfo.getAccountAvailableAmount().compareTo(new BigDecimal("0")) < 0) {
				log.warn(String.format("客户%s账户总额不足", accountInfo.getAccountNo()));
				throw new SLException("账户可用金额不足");
			}
			// 判断冻结金额是否小于0？
			if (accountInfo.getAccountFreezeAmount().compareTo(new BigDecimal("0")) < 0) {
				log.warn(String.format("客户%s账户总额不足", accountInfo.getAccountNo()));
				throw new SLException("账户冻结金额不足");
			}
			//更新账户信息
			AccountInfoEntity accountInfoUpd = new AccountInfoEntity();
			accountInfoUpd.setAccountAvailableAmount(accountInfo.getAccountAvailableAmount());
			accountInfoUpd.setAccountFreezeAmount(accountInfo.getAccountFreezeAmount());
			accountInfoUpd.setAccountTotalAmount(accountInfo.getAccountTotalAmount());
			accountInfoUpd.setBasicModelProperty(accountInfo.getLastUpdateUser(), false);
			if(!accountInfo.updAccountInfo(accountInfoUpd))
				throw new SLException(String.format("更新%s账户信息失败", accountInfo.getAccountNo()));
		}
	}	
	
//-------------私有方法区-----------------------------------------------------------------------------------------------------------------------------------
	/**
	 * 更新账户时校验账户流水
	 * 
	 * @param accountFlowInfo
	 * @throws SLException
	 */
	private void validateAccountFlowInfo(AccountFlowInfoEntity accountFlowInfo) throws SLException {
		if (accountFlowInfo == null)
			throw new SLSystemException("账户资金流水记账，对象不能为空");
		if (StringUtils.isEmpty(accountFlowInfo.getAccountId()))
			throw new SLSystemException("账户资金流水记账，关联表或者关联表主键不能为空");
		if (StringUtils.isEmpty(accountFlowInfo.getTradeType()))
			throw new SLSystemException("账户资金流水记账，交易类型不能为空");
		if (StringUtils.isEmpty(accountFlowInfo.getTradeNo()))
			throw new SLSystemException("账户资金流水记账，交易编号不能为空");
		if (accountFlowInfo.getTradeAmount() == null)
			throw new SLSystemException("账户资金流水记账，交易金额不能为空");
		if (accountFlowInfo.getTradeType().equals(SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_UNFREEZE_WITHDRAW) && StringUtils.isEmpty(accountFlowInfo.getOldTradeNo()))
			throw new SLSystemException("账户资金流水记账，解冻时原交易流水号不能为空");
	}	
	
	/**
	 * 解冻
	 * 
	 * @param accountInfo 账户信息
	 * @param accountFlowInfo 原交易流水
	 *         
	 */
	private void unFreezeAccount(AccountInfoEntity accountInfo, AccountFlowInfoEntity accountFlowInfo) {
		// 可用金额 = 原可用金额 + 交易金额
		accountInfo.setAccountAvailableAmount(ArithUtil.add(accountInfo.getAccountAvailableAmount(), accountFlowInfo.getTradeAmount()));
		// 冻结金额 = 原冻结金额 - 交易金额
		accountInfo.setAccountFreezeAmount(ArithUtil.sub(accountInfo.getAccountFreezeAmount(), accountFlowInfo.getTradeAmount()));
		accountFlowInfo.setAccountTotalAmount(accountInfo.getAccountTotalAmount());
		accountFlowInfo.setAccountFreezeAmount(accountInfo.getAccountFreezeAmount());
		accountFlowInfo.setAccountAvailable(accountInfo.getAccountAvailableAmount());
		accountFlowInfo.setAccountId(accountInfo.getId());
		accountFlowInfo.setCustId(accountInfo.getCustId());
	}	
	
	/**
	 * 提现手续费
	 * 
	 * @param accountInfo 账户信息
	 * @param accountFlowInfo 原交易流水
	 * @param withDrawExpense   提现手续费
	 *         
	 */
	private void withDrawExpensAccount(AccountInfoEntity accountInfo, AccountFlowInfoEntity accountFlowInfo,BigDecimal withDrawExpense) {
		// 可用金额 = 原可用金额  - 提现手续费
		accountInfo.setAccountAvailableAmount(ArithUtil.sub(accountInfo.getAccountAvailableAmount(), withDrawExpense));
		// 总金额 = 原总金额 - 提现手续费
		accountInfo.setAccountTotalAmount(ArithUtil.sub(accountInfo.getAccountTotalAmount(), withDrawExpense));
		accountFlowInfo.setAccountTotalAmount(accountInfo.getAccountTotalAmount());
		accountFlowInfo.setAccountFreezeAmount(accountInfo.getAccountFreezeAmount());
		accountFlowInfo.setAccountAvailable(accountInfo.getAccountAvailableAmount());
		accountFlowInfo.setAccountId(accountInfo.getId());
		accountFlowInfo.setCustId(accountInfo.getCustId());
	}

	/**
	 * 提现
	 * 
	 * @param accountInfo 账户信息
	 * @param accountFlowInfo 原交易流水
	 * @param withDrawExpense   提现手续费
	 *            
	 */
	private void withDrawAccount(AccountInfoEntity accountInfo, AccountFlowInfoEntity accountFlowInfo,BigDecimal withDrawExpense) {
		// 可用金额 = 原可用金额 - 交易金额 + 提现手续费  
		accountInfo.setAccountAvailableAmount(ArithUtil.add(ArithUtil.sub(accountInfo.getAccountAvailableAmount(), accountFlowInfo.getTradeAmount()), withDrawExpense));
		// 总的金额 = 原总的金额 - 交易金额 + 提现手续费
		accountInfo.setAccountTotalAmount(ArithUtil.add(ArithUtil.sub(accountInfo.getAccountTotalAmount(), accountFlowInfo.getTradeAmount()), withDrawExpense));
		accountFlowInfo.setAccountTotalAmount(accountInfo.getAccountTotalAmount());
		accountFlowInfo.setAccountFreezeAmount(accountInfo.getAccountFreezeAmount());
		accountFlowInfo.setAccountAvailable(accountInfo.getAccountAvailableAmount());
		accountFlowInfo.setAccountId(accountInfo.getId());
		accountFlowInfo.setCustId(accountInfo.getCustId());
	}	
	
	/**
	 * 划扣/提现
	 * 
	 * @param custAccountInfo
	 *            账户对象
	 * @param tradeAmout
	 *            交易金额
	 */
	private void withDrawAccount(AccountInfoEntity accountInfo, AccountFlowInfoEntity accountFlowInfo) {
		// 可用金额 = 原可用金额 - 交易金额  
		// 总的金额 = 原总的金额 - 交易金额
		accountInfo.setAccountAvailableAmount(ArithUtil.sub(accountInfo.getAccountAvailableAmount(), accountFlowInfo.getTradeAmount()));
		accountInfo.setAccountTotalAmount(ArithUtil.sub(accountInfo.getAccountTotalAmount(), accountFlowInfo.getTradeAmount()));
		// 设置账户更新后的资金流水
		accountFlowInfo.setAccountTotalAmount(accountInfo.getAccountTotalAmount());
		accountFlowInfo.setAccountFreezeAmount(accountInfo.getAccountFreezeAmount());
		accountFlowInfo.setAccountAvailable(accountInfo.getAccountAvailableAmount());
		accountFlowInfo.setAccountId(accountInfo.getId());
		accountFlowInfo.setCustId(accountInfo.getCustId());
	}

	/**
	 * 更新账户与记录账户流水
	 *
	 * @author  wangjf
	 * @date    2016年1月7日 上午9:50:28
	 * @param sourceMainAccount
	 * @param sourceSubAccount
	 * @param destMainAccount
	 * @param destSubAccount
	 * @param accountType
	 * 		"1":主账户——>主账户 
	 		"2":主账户——>分账户
	 		"3":分账户——>主账户
	 		"4":分账户——>分账户
	 		"5":主账户收入
	 		"6":主账户支出
	 		"7":主账户冻结
	 		"8":主账户解冻
	 		"9":分账户收入
	 		"10":分账户支出
	 		"11":分账户冻结
	 		"12":分账户解冻
	 		"13":主账户——>分账户(现金字段)
	 		"14":分账户(现金字段)——>主账户
	 * @param tradeType
	 * @param reqeustNo
	 * @param bankrollFlowDirection
	 * @param tradeAmount
	 * @param subjectType
	 * @param flowBusiRelateType
	 * @param flowRelatePrimary
	 * @param flowType
	 * @param memo
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public List<AccountFlowInfoEntity> updateAccount(
			AccountInfoEntity sourceMainAccount,
			SubAccountInfoEntity sourceSubAccount,
			AccountInfoEntity destMainAccount,
			SubAccountInfoEntity destSubAccount, String accountType,
			String tradeType, String reqeustNo, 
			BigDecimal tradeAmount, String subjectType,
			String flowBusiRelateType, String flowRelatePrimary, 
			String memo, String userId) throws SLException{
		List<AccountFlowInfoEntity> accountList = Lists.newArrayList();
		switch(accountType) {
		case "1": 
		{
			if(sourceMainAccount.getAccountTotalAmount().compareTo(tradeAmount) < 0
					|| sourceMainAccount.getAccountAvailableAmount().compareTo(tradeAmount) < 0) {
				throw new SLException("账户余额不足");
			}
			// 源主账户 支出
			sourceMainAccount.setAccountTotalAmount(ArithUtil.sub(sourceMainAccount.getAccountTotalAmount(), tradeAmount));
			sourceMainAccount.setAccountAvailableAmount(ArithUtil.sub(sourceMainAccount.getAccountAvailableAmount(), tradeAmount));
			sourceMainAccount.setBasicModelProperty(userId, false);
			AccountFlowInfoEntity accountFlowInfoEntity1 = accountFlowService.saveAccountFlow(sourceMainAccount, null, destMainAccount, null, "1", 
					tradeType, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, 
					tradeAmount, subjectType, 
					flowBusiRelateType, flowRelatePrimary, SubjectConstant.SUBJECT_TYPE_AMOUNT);
			accountFlowInfoEntity1.setMemo(memo);
			
			// 目标主账户 收入
			destMainAccount.setAccountTotalAmount(ArithUtil.add(destMainAccount.getAccountTotalAmount(), tradeAmount));
			destMainAccount.setAccountAvailableAmount(ArithUtil.add(destMainAccount.getAccountAvailableAmount(), tradeAmount));
			destMainAccount.setBasicModelProperty(userId, false);
			AccountFlowInfoEntity accountFlowInfoEntity2 = accountFlowService.saveAccountFlow(destMainAccount, null, sourceMainAccount, null, "1", 
					tradeType, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING, 
					tradeAmount, subjectType, 
					flowBusiRelateType, flowRelatePrimary, SubjectConstant.SUBJECT_TYPE_AMOUNT);
			accountFlowInfoEntity2.setMemo(memo);
			
			accountList.add(accountFlowInfoEntity1);
			accountList.add(accountFlowInfoEntity2);
		}
			break;
		case "2":
		{
			if(sourceMainAccount.getAccountTotalAmount().compareTo(tradeAmount) < 0
					|| sourceMainAccount.getAccountAvailableAmount().compareTo(tradeAmount) < 0) {
				throw new SLException("账户余额不足");
			}
			// 源主账户 支出
			sourceMainAccount.setAccountTotalAmount(ArithUtil.sub(sourceMainAccount.getAccountTotalAmount(), tradeAmount));
			sourceMainAccount.setAccountAvailableAmount(ArithUtil.sub(sourceMainAccount.getAccountAvailableAmount(), tradeAmount));
			sourceMainAccount.setBasicModelProperty(userId, false);
			AccountFlowInfoEntity accountFlowInfoEntity1 = accountFlowService.saveAccountFlow(sourceMainAccount, null, null, destSubAccount, "2", 
					tradeType, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, 
					tradeAmount, subjectType, 
					flowBusiRelateType, flowRelatePrimary, SubjectConstant.SUBJECT_TYPE_AMOUNT);
			accountFlowInfoEntity1.setMemo(memo);
			
			// 目标分账户 收入
			destSubAccount.setAccountTotalValue(ArithUtil.add(destSubAccount.getAccountTotalValue(), tradeAmount));
			destSubAccount.setAccountAvailableValue(ArithUtil.add(destSubAccount.getAccountAvailableValue(), tradeAmount));
			destSubAccount.setBasicModelProperty(userId, false);
			AccountFlowInfoEntity accountFlowInfoEntity2 = accountFlowService.saveAccountFlow(null, destSubAccount, sourceMainAccount, null, "3", 
					tradeType, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING, 
					tradeAmount, subjectType, 
					flowBusiRelateType, flowRelatePrimary, SubjectConstant.SUBJECT_TYPE_AMOUNT);
			accountFlowInfoEntity2.setMemo(memo);
			
			accountList.add(accountFlowInfoEntity1);
			accountList.add(accountFlowInfoEntity2);
		}
			break;
		case "3": 
		{
			if(sourceSubAccount.getAccountTotalValue().compareTo(tradeAmount) < 0
					|| sourceSubAccount.getAccountAvailableValue().compareTo(tradeAmount) < 0) {
				throw new SLException("账户余额不足");
			}
			// 目标分账户 支出
			sourceSubAccount.setAccountTotalValue(ArithUtil.sub(sourceSubAccount.getAccountTotalValue(), tradeAmount));
			sourceSubAccount.setAccountAvailableValue(ArithUtil.sub(sourceSubAccount.getAccountAvailableValue(), tradeAmount));
			sourceSubAccount.setBasicModelProperty(userId, false);
			AccountFlowInfoEntity accountFlowInfoEntity2 = accountFlowService.saveAccountFlow(null, sourceSubAccount, destMainAccount, null, "3", 
					tradeType, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, 
					tradeAmount, subjectType, 
					flowBusiRelateType, flowRelatePrimary, SubjectConstant.SUBJECT_TYPE_AMOUNT);
			accountFlowInfoEntity2.setMemo(memo);
						
			// 源主账户 收入
			destMainAccount.setAccountTotalAmount(ArithUtil.add(destMainAccount.getAccountTotalAmount(), tradeAmount));
			destMainAccount.setAccountAvailableAmount(ArithUtil.add(destMainAccount.getAccountAvailableAmount(), tradeAmount));
			destMainAccount.setBasicModelProperty(userId, false);
			AccountFlowInfoEntity accountFlowInfoEntity1 = accountFlowService.saveAccountFlow(destMainAccount, null, null, sourceSubAccount, "2", 
					tradeType, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING, 
					tradeAmount, subjectType, 
					flowBusiRelateType, flowRelatePrimary, SubjectConstant.SUBJECT_TYPE_AMOUNT);
			accountFlowInfoEntity1.setMemo(memo);
			
			accountList.add(accountFlowInfoEntity1);
			accountList.add(accountFlowInfoEntity2);
		}
			break;
		case "4": 
		{
			if(sourceSubAccount.getAccountTotalValue().compareTo(tradeAmount) < 0
					|| sourceSubAccount.getAccountAvailableValue().compareTo(tradeAmount) < 0) {
				throw new SLException("账户余额不足");
			}
			// 目标分账户 支出
			sourceSubAccount.setAccountTotalValue(ArithUtil.sub(sourceSubAccount.getAccountTotalValue(), tradeAmount));
			sourceSubAccount.setAccountAvailableValue(ArithUtil.sub(sourceSubAccount.getAccountAvailableValue(), tradeAmount));
			sourceSubAccount.setBasicModelProperty(userId, false);
			AccountFlowInfoEntity accountFlowInfoEntity1 = accountFlowService.saveAccountFlow(null, sourceSubAccount, null, destSubAccount, "4", 
					tradeType, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, 
					tradeAmount, subjectType, 
					flowBusiRelateType, flowRelatePrimary, SubjectConstant.SUBJECT_TYPE_AMOUNT);
			accountFlowInfoEntity1.setMemo(memo);
						
			// 目标分账户 收入
			destSubAccount.setAccountTotalValue(ArithUtil.add(destSubAccount.getAccountTotalValue(), tradeAmount));
			destSubAccount.setAccountAvailableValue(ArithUtil.add(destSubAccount.getAccountAvailableValue(), tradeAmount));
			destSubAccount.setBasicModelProperty(userId, false);
			AccountFlowInfoEntity accountFlowInfoEntity2 = accountFlowService.saveAccountFlow(null, destSubAccount, null, sourceSubAccount, "4", 
					tradeType, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING, 
					tradeAmount, subjectType, 
					flowBusiRelateType, flowRelatePrimary, SubjectConstant.SUBJECT_TYPE_AMOUNT);
			accountFlowInfoEntity2.setMemo(memo);
			
			accountList.add(accountFlowInfoEntity1);
			accountList.add(accountFlowInfoEntity2);
		}
			break;
		case "5": 
		{
			// 源主账户 收入
			sourceMainAccount.setAccountTotalAmount(ArithUtil.add(sourceMainAccount.getAccountTotalAmount(), tradeAmount));
			sourceMainAccount.setAccountAvailableAmount(ArithUtil.add(sourceMainAccount.getAccountAvailableAmount(), tradeAmount));
			sourceMainAccount.setBasicModelProperty(userId, false);
			AccountFlowInfoEntity accountFlowInfoEntity1 = accountFlowService.saveAccountFlow(sourceMainAccount, null, null, null, "1", 
					tradeType, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING, 
					tradeAmount, subjectType, 
					flowBusiRelateType, flowRelatePrimary, SubjectConstant.SUBJECT_TYPE_AMOUNT);
			accountFlowInfoEntity1.setMemo(memo);
			
			accountList.add(accountFlowInfoEntity1);
		}
			break;
		case "6": 
		{
			if(sourceMainAccount.getAccountTotalAmount().compareTo(tradeAmount) < 0
					|| sourceMainAccount.getAccountAvailableAmount().compareTo(tradeAmount) < 0) {
				throw new SLException("账户余额不足");
			}
			// 源主账户 支出
			sourceMainAccount.setAccountTotalAmount(ArithUtil.sub(sourceMainAccount.getAccountTotalAmount(), tradeAmount));
			sourceMainAccount.setAccountAvailableAmount(ArithUtil.sub(sourceMainAccount.getAccountAvailableAmount(), tradeAmount));
			sourceMainAccount.setBasicModelProperty(userId, false);
			AccountFlowInfoEntity accountFlowInfoEntity1 = accountFlowService.saveAccountFlow(sourceMainAccount, null, null, null, "1", 
					tradeType, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, 
					tradeAmount, subjectType, 
					flowBusiRelateType, flowRelatePrimary, SubjectConstant.SUBJECT_TYPE_AMOUNT);
			accountFlowInfoEntity1.setMemo(memo);
			
			accountList.add(accountFlowInfoEntity1);
		}	
			break;
		case "7": 
		{
			if(sourceMainAccount.getAccountAvailableAmount().compareTo(tradeAmount) < 0) {
				throw new SLException("账户余额不足");
			}
			// 源主账户 冻结
			sourceMainAccount.setAccountFreezeAmount(ArithUtil.add(sourceMainAccount.getAccountFreezeAmount(), tradeAmount));
			sourceMainAccount.setAccountAvailableAmount(ArithUtil.sub(sourceMainAccount.getAccountAvailableAmount(), tradeAmount));
			sourceMainAccount.setBasicModelProperty(userId, false);
			AccountFlowInfoEntity accountFlowInfoEntity1 = accountFlowService.saveAccountFlow(sourceMainAccount, null, null, null, "1", 
					tradeType, reqeustNo, null, 
					tradeAmount, subjectType, 
					flowBusiRelateType, flowRelatePrimary, SubjectConstant.SUBJECT_TYPE_AMOUNT);
			accountFlowInfoEntity1.setMemo(memo);
			
			accountList.add(accountFlowInfoEntity1);
		}
			break;
		case "8": 
		{
			if(sourceMainAccount.getAccountFreezeAmount().compareTo(tradeAmount) < 0) {
				throw new SLException("账户冻结余额不足");
			}
			// 源主账户 解冻
			sourceMainAccount.setAccountFreezeAmount(ArithUtil.sub(sourceMainAccount.getAccountFreezeAmount(), tradeAmount));
			sourceMainAccount.setAccountAvailableAmount(ArithUtil.add(sourceMainAccount.getAccountAvailableAmount(), tradeAmount));
			sourceMainAccount.setBasicModelProperty(userId, false);
			AccountFlowInfoEntity accountFlowInfoEntity1 = accountFlowService.saveAccountFlow(sourceMainAccount, null, null, null, "1", 
					tradeType, reqeustNo, null, 
					tradeAmount, subjectType, 
					flowBusiRelateType, flowRelatePrimary, SubjectConstant.SUBJECT_TYPE_AMOUNT);
			accountFlowInfoEntity1.setMemo(memo);
			
			accountList.add(accountFlowInfoEntity1);
		}
			break;
		case "9":
		{
			if(sourceSubAccount.getAccountTotalValue().compareTo(tradeAmount) < 0
					|| sourceSubAccount.getAccountAvailableValue().compareTo(tradeAmount) < 0) {
				throw new SLException("账户余额不足");
			}
			// 目标分账户 支出
			sourceSubAccount.setAccountTotalValue(ArithUtil.sub(sourceSubAccount.getAccountTotalValue(), tradeAmount));
			sourceSubAccount.setAccountAvailableValue(ArithUtil.sub(sourceSubAccount.getAccountAvailableValue(), tradeAmount));
			sourceSubAccount.setBasicModelProperty(userId, false);
			AccountFlowInfoEntity accountFlowInfoEntity1 = accountFlowService.saveAccountFlow(null, sourceSubAccount, null, null, "4", 
					tradeType, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, 
					tradeAmount, subjectType, 
					flowBusiRelateType, flowRelatePrimary, SubjectConstant.SUBJECT_TYPE_AMOUNT);
			accountFlowInfoEntity1.setMemo(memo);
			
			accountList.add(accountFlowInfoEntity1);
		}
			break;
		case "10": 
		{
			// 目标分账户 收入
			sourceSubAccount.setAccountTotalValue(ArithUtil.add(sourceSubAccount.getAccountTotalValue(), tradeAmount));
			sourceSubAccount.setAccountAvailableValue(ArithUtil.add(sourceSubAccount.getAccountAvailableValue(), tradeAmount));
			sourceSubAccount.setBasicModelProperty(userId, false);
			AccountFlowInfoEntity accountFlowInfoEntity1 = accountFlowService.saveAccountFlow(null, sourceSubAccount, null, null, "4", 
					tradeType, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING, 
					tradeAmount, subjectType, 
					flowBusiRelateType, flowRelatePrimary, SubjectConstant.SUBJECT_TYPE_AMOUNT);
			accountFlowInfoEntity1.setMemo(memo);
			
			accountList.add(accountFlowInfoEntity1);
		}
			break;
		case "11": 
		{
			if(sourceSubAccount.getAccountAvailableValue().compareTo(tradeAmount) < 0) {
				throw new SLException("账户余额不足");
			}
			// 目标分账户 冻结
			sourceSubAccount.setAccountFreezeValue(ArithUtil.add(sourceSubAccount.getAccountFreezeValue(), tradeAmount));
			sourceSubAccount.setAccountAvailableValue(ArithUtil.sub(sourceSubAccount.getAccountAvailableValue(), tradeAmount));
			sourceSubAccount.setBasicModelProperty(userId, false);
			AccountFlowInfoEntity accountFlowInfoEntity1 = accountFlowService.saveAccountFlow(null, sourceSubAccount, null, null, "4", 
					tradeType, reqeustNo, null, 
					tradeAmount, subjectType, 
					flowBusiRelateType, flowRelatePrimary, SubjectConstant.SUBJECT_TYPE_AMOUNT);
			accountFlowInfoEntity1.setMemo(memo);
			
			accountList.add(accountFlowInfoEntity1);
		}
			break;
		case "12": 
		{
			if(sourceSubAccount.getAccountFreezeValue().compareTo(tradeAmount) < 0) {
				throw new SLException("账户冻结余额不足");
			}
			// 目标分账户 解冻
			sourceSubAccount.setAccountFreezeValue(ArithUtil.sub(sourceSubAccount.getAccountFreezeValue(), tradeAmount));
			sourceSubAccount.setAccountAvailableValue(ArithUtil.add(sourceSubAccount.getAccountAvailableValue(), tradeAmount));
			sourceSubAccount.setBasicModelProperty(userId, false);
			AccountFlowInfoEntity accountFlowInfoEntity1 = accountFlowService.saveAccountFlow(null, sourceSubAccount, null, null, "4", 
					tradeType, reqeustNo, null, 
					tradeAmount, subjectType, 
					flowBusiRelateType, flowRelatePrimary, SubjectConstant.SUBJECT_TYPE_AMOUNT);
			accountFlowInfoEntity1.setMemo(memo);
			
			accountList.add(accountFlowInfoEntity1);
		}
			break;
		case "13":
		{
			if(sourceMainAccount.getAccountTotalAmount().compareTo(tradeAmount) < 0
					|| sourceMainAccount.getAccountAvailableAmount().compareTo(tradeAmount) < 0) {
				throw new SLException("账户余额不足");
			}
			// 源主账户 支出
			sourceMainAccount.setAccountTotalAmount(ArithUtil.sub(sourceMainAccount.getAccountTotalAmount(), tradeAmount));
			sourceMainAccount.setAccountAvailableAmount(ArithUtil.sub(sourceMainAccount.getAccountAvailableAmount(), tradeAmount));
			sourceMainAccount.setBasicModelProperty(userId, false);
			AccountFlowInfoEntity accountFlowInfoEntity1 = accountFlowService.saveAccountFlow(sourceMainAccount, null, null, destSubAccount, "2", 
					tradeType, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, 
					tradeAmount, subjectType, 
					flowBusiRelateType, flowRelatePrimary, SubjectConstant.SUBJECT_TYPE_AMOUNT);
			accountFlowInfoEntity1.setMemo(memo);
			
			// 目标分账户 收入
			destSubAccount.setAccountAmount(ArithUtil.add(destSubAccount.getAccountAmount(), tradeAmount));
			destSubAccount.setBasicModelProperty(userId, false);
			AccountFlowInfoEntity accountFlowInfoEntity2 = accountFlowService.saveAccountFlow(null, destSubAccount, sourceMainAccount, null, "3", 
					tradeType, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING, 
					tradeAmount, subjectType, 
					flowBusiRelateType, flowRelatePrimary, SubjectConstant.SUBJECT_TYPE_AMOUNT);
			accountFlowInfoEntity2.setMemo(memo);
			
			accountList.add(accountFlowInfoEntity1);
			accountList.add(accountFlowInfoEntity2);
		}
			break;
		case "14": 
		{
			if(sourceSubAccount.getAccountAmount().compareTo(tradeAmount) < 0) {
				throw new SLException("账户余额不足");
			}
			// 目标分账户 支出
			sourceSubAccount.setAccountAmount(ArithUtil.sub(sourceSubAccount.getAccountAmount(), tradeAmount));
			sourceSubAccount.setBasicModelProperty(userId, false);
			AccountFlowInfoEntity accountFlowInfoEntity2 = accountFlowService.saveAccountFlow(null, sourceSubAccount, destMainAccount, null, "3", 
					tradeType, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, 
					tradeAmount, subjectType, 
					flowBusiRelateType, flowRelatePrimary, SubjectConstant.SUBJECT_TYPE_AMOUNT);
			accountFlowInfoEntity2.setMemo(memo);
						
			// 源主账户 收入
			destMainAccount.setAccountTotalAmount(ArithUtil.add(destMainAccount.getAccountTotalAmount(), tradeAmount));
			destMainAccount.setAccountAvailableAmount(ArithUtil.add(destMainAccount.getAccountAvailableAmount(), tradeAmount));
			destMainAccount.setBasicModelProperty(userId, false);
			AccountFlowInfoEntity accountFlowInfoEntity1 = accountFlowService.saveAccountFlow(destMainAccount, null, null, sourceSubAccount, "2", 
					tradeType, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING, 
					tradeAmount, subjectType, 
					flowBusiRelateType, flowRelatePrimary, SubjectConstant.SUBJECT_TYPE_AMOUNT);
			accountFlowInfoEntity1.setMemo(memo);
			
			accountList.add(accountFlowInfoEntity1);
			accountList.add(accountFlowInfoEntity2);
		}
			break;
		}
		
		return accountList;
	}

	/**
	 * 更新账户与记录账户流水--2017-6-28 红包使用
	 *
	 * @author  wangjf
	 * @date    2016年1月7日 上午9:50:28
	 * @param sourceMainAccount
	 * @param sourceSubAccount
	 * @param destMainAccount
	 * @param destSubAccount
	 * @param accountType
	 * 		"1":主账户——>主账户
	"2":主账户——>分账户
	"3":分账户——>主账户
	"4":分账户——>分账户
	"5":主账户收入
	"6":主账户支出
	"7":主账户冻结
	"8":主账户解冻
	"9":分账户收入
	"10":分账户支出
	"11":分账户冻结
	"12":分账户解冻
	"13":主账户——>分账户(现金字段)
	"14":分账户(现金字段)——>主账户
	 * @param tradeType
	 * @param reqeustNo
	 * @param tradeAmount
	 * @param subjectType
	 * @param flowBusiRelateType
	 * @param flowRelatePrimary
	 * @param memo
	 * @return
	 */
	@Transactional(rollbackFor = SLException.class)
	@Override
	public List<AccountFlowInfoEntity> updateAccountExt(
			AccountInfoEntity sourceMainAccount,
			SubAccountInfoEntity sourceSubAccount,
			AccountInfoEntity destMainAccount,
			SubAccountInfoEntity destSubAccount, String accountType,
			String tradeType, String reqeustNo,
			BigDecimal tradeAmount, String subjectType,
			String flowBusiRelateType, String flowRelatePrimary,
			String memo, String userId,
			BigDecimal usableAmount) throws SLException{
		List<AccountFlowInfoEntity> accountList = Lists.newArrayList();
		switch(accountType) {
			case "1":
			{
				if(sourceMainAccount.getAccountTotalAmount().compareTo(tradeAmount) < 0
						|| sourceMainAccount.getAccountAvailableAmount().compareTo(tradeAmount) < 0) {
					throw new SLException("账户余额不足");
				}
				// 源主账户 支出
				sourceMainAccount.setAccountTotalAmount(ArithUtil.sub(sourceMainAccount.getAccountTotalAmount(), tradeAmount));
				sourceMainAccount.setAccountAvailableAmount(ArithUtil.sub(sourceMainAccount.getAccountAvailableAmount(), tradeAmount));
				sourceMainAccount.setBasicModelProperty(userId, false);
				AccountFlowInfoEntity accountFlowInfoEntity1 = accountFlowService.saveAccountFlow(sourceMainAccount, null, destMainAccount, null, "1",
						tradeType, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT,
						tradeAmount, subjectType,
						flowBusiRelateType, flowRelatePrimary, SubjectConstant.SUBJECT_TYPE_AMOUNT);
				accountFlowInfoEntity1.setMemo(memo);

				// 目标主账户 收入
				destMainAccount.setAccountTotalAmount(ArithUtil.add(destMainAccount.getAccountTotalAmount(), tradeAmount));
				destMainAccount.setAccountAvailableAmount(ArithUtil.add(destMainAccount.getAccountAvailableAmount(), tradeAmount));
				destMainAccount.setBasicModelProperty(userId, false);
				AccountFlowInfoEntity accountFlowInfoEntity2 = accountFlowService.saveAccountFlow(destMainAccount, null, sourceMainAccount, null, "1",
						tradeType, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING,
						tradeAmount, subjectType,
						flowBusiRelateType, flowRelatePrimary, SubjectConstant.SUBJECT_TYPE_AMOUNT);
				accountFlowInfoEntity2.setMemo(memo);

				accountList.add(accountFlowInfoEntity1);
				accountList.add(accountFlowInfoEntity2);
			}
			break;
			case "2":
			{
				if(sourceMainAccount.getAccountTotalAmount().compareTo(tradeAmount) < 0
						|| sourceMainAccount.getAccountAvailableAmount().compareTo(tradeAmount) < 0) {
					throw new SLException("账户余额不足");
				}
				// 使用红包
				if (usableAmount != null) {
					// 往投资人总账户加一笔红包收入金额流水
					//TODO hxp 投资人账户不加红包金额
//					sourceMainAccount.setAccountTotalAmount(ArithUtil.add(sourceMainAccount.getAccountTotalAmount(), usableAmount));
//					sourceMainAccount.setAccountAvailableAmount(ArithUtil.add(sourceMainAccount.getAccountAvailableAmount(), usableAmount));
//					sourceMainAccount.setBasicModelProperty(userId, false);
//					AccountFlowInfoEntity accountFlowInfoEntity0 = accountFlowService.saveAccountFlowExt(null, null, sourceMainAccount, null, "1",
//							tradeType, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING,
//							usableAmount, subjectType,
//							flowBusiRelateType, flowRelatePrimary, SubjectConstant.SUBJECT_TYPE_AMOUNT_RED_ENVELOPE);
//					accountFlowInfoEntity0.setMemo(memo);
//					accountList.add(accountFlowInfoEntity0);
					// 走定时器公司红包账户走一笔红包支出流水
					//用户实际支出金额：交易金额-红包
					tradeAmount = tradeAmount.subtract(usableAmount);
				}

				// 源主账户 支出
				sourceMainAccount.setAccountTotalAmount(ArithUtil.sub(sourceMainAccount.getAccountTotalAmount(), tradeAmount));
				sourceMainAccount.setAccountAvailableAmount(ArithUtil.sub(sourceMainAccount.getAccountAvailableAmount(), tradeAmount));
				sourceMainAccount.setBasicModelProperty(userId, false);
				AccountFlowInfoEntity accountFlowInfoEntity1 = accountFlowService.saveAccountFlow(sourceMainAccount, null, null, destSubAccount, "2",
						tradeType, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT,
						tradeAmount, subjectType,
						flowBusiRelateType, flowRelatePrimary, SubjectConstant.SUBJECT_TYPE_AMOUNT);
				accountFlowInfoEntity1.setMemo(memo);
				// 这边红包金额流水走定时器处理
				if (usableAmount != null) {
					//目标分账户收入包含红包
					tradeAmount = tradeAmount.add(usableAmount);
				}
				// 目标分账户 收入
				destSubAccount.setAccountTotalValue(ArithUtil.add(destSubAccount.getAccountTotalValue(), tradeAmount));
				destSubAccount.setAccountAvailableValue(ArithUtil.add(destSubAccount.getAccountAvailableValue(), tradeAmount));
				destSubAccount.setBasicModelProperty(userId, false);
				AccountFlowInfoEntity accountFlowInfoEntity2 = accountFlowService.saveAccountFlow(null, destSubAccount, sourceMainAccount, null, "3",
						tradeType, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING,
						tradeAmount, subjectType,
						flowBusiRelateType, flowRelatePrimary, SubjectConstant.SUBJECT_TYPE_AMOUNT);
				accountFlowInfoEntity2.setMemo(memo);

				accountList.add(accountFlowInfoEntity1);
				accountList.add(accountFlowInfoEntity2);
			}
			break;
			case "3":
			{
				if(sourceSubAccount.getAccountTotalValue().compareTo(tradeAmount) < 0
						|| sourceSubAccount.getAccountAvailableValue().compareTo(tradeAmount) < 0) {
					throw new SLException("账户余额不足");
				}
				// 目标分账户 支出
				sourceSubAccount.setAccountTotalValue(ArithUtil.sub(sourceSubAccount.getAccountTotalValue(), tradeAmount));
				sourceSubAccount.setAccountAvailableValue(ArithUtil.sub(sourceSubAccount.getAccountAvailableValue(), tradeAmount));
				sourceSubAccount.setBasicModelProperty(userId, false);
				AccountFlowInfoEntity accountFlowInfoEntity2 = accountFlowService.saveAccountFlow(null, sourceSubAccount, destMainAccount, null, "3",
						tradeType, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT,
						tradeAmount, subjectType,
						flowBusiRelateType, flowRelatePrimary, SubjectConstant.SUBJECT_TYPE_AMOUNT);
				accountFlowInfoEntity2.setMemo(memo);

				// 源主账户 收入
				destMainAccount.setAccountTotalAmount(ArithUtil.add(destMainAccount.getAccountTotalAmount(), tradeAmount));
				destMainAccount.setAccountAvailableAmount(ArithUtil.add(destMainAccount.getAccountAvailableAmount(), tradeAmount));
				destMainAccount.setBasicModelProperty(userId, false);
				AccountFlowInfoEntity accountFlowInfoEntity1 = accountFlowService.saveAccountFlow(destMainAccount, null, null, sourceSubAccount, "2",
						tradeType, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING,
						tradeAmount, subjectType,
						flowBusiRelateType, flowRelatePrimary, SubjectConstant.SUBJECT_TYPE_AMOUNT);
				accountFlowInfoEntity1.setMemo(memo);

				accountList.add(accountFlowInfoEntity1);
				accountList.add(accountFlowInfoEntity2);
			}
			break;
			case "4":
			{
				if(sourceSubAccount.getAccountTotalValue().compareTo(tradeAmount) < 0
						|| sourceSubAccount.getAccountAvailableValue().compareTo(tradeAmount) < 0) {
					throw new SLException("账户余额不足");
				}
				// 目标分账户 支出
				sourceSubAccount.setAccountTotalValue(ArithUtil.sub(sourceSubAccount.getAccountTotalValue(), tradeAmount));
				sourceSubAccount.setAccountAvailableValue(ArithUtil.sub(sourceSubAccount.getAccountAvailableValue(), tradeAmount));
				sourceSubAccount.setBasicModelProperty(userId, false);
				AccountFlowInfoEntity accountFlowInfoEntity1 = accountFlowService.saveAccountFlow(null, sourceSubAccount, null, destSubAccount, "4",
						tradeType, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT,
						tradeAmount, subjectType,
						flowBusiRelateType, flowRelatePrimary, SubjectConstant.SUBJECT_TYPE_AMOUNT);
				accountFlowInfoEntity1.setMemo(memo);

				// 目标分账户 收入
				destSubAccount.setAccountTotalValue(ArithUtil.add(destSubAccount.getAccountTotalValue(), tradeAmount));
				destSubAccount.setAccountAvailableValue(ArithUtil.add(destSubAccount.getAccountAvailableValue(), tradeAmount));
				destSubAccount.setBasicModelProperty(userId, false);
				AccountFlowInfoEntity accountFlowInfoEntity2 = accountFlowService.saveAccountFlow(null, destSubAccount, null, sourceSubAccount, "4",
						tradeType, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING,
						tradeAmount, subjectType,
						flowBusiRelateType, flowRelatePrimary, SubjectConstant.SUBJECT_TYPE_AMOUNT);
				accountFlowInfoEntity2.setMemo(memo);

				accountList.add(accountFlowInfoEntity1);
				accountList.add(accountFlowInfoEntity2);
			}
			break;
			case "5":
			{
				// 源主账户 收入
				sourceMainAccount.setAccountTotalAmount(ArithUtil.add(sourceMainAccount.getAccountTotalAmount(), tradeAmount));
				sourceMainAccount.setAccountAvailableAmount(ArithUtil.add(sourceMainAccount.getAccountAvailableAmount(), tradeAmount));
				sourceMainAccount.setBasicModelProperty(userId, false);
				AccountFlowInfoEntity accountFlowInfoEntity1 = accountFlowService.saveAccountFlow(sourceMainAccount, null, null, null, "1",
						tradeType, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING,
						tradeAmount, subjectType,
						flowBusiRelateType, flowRelatePrimary, SubjectConstant.SUBJECT_TYPE_AMOUNT);
				accountFlowInfoEntity1.setMemo(memo);

				accountList.add(accountFlowInfoEntity1);
			}
			break;
			case "6":
			{
				if(sourceMainAccount.getAccountTotalAmount().compareTo(tradeAmount) < 0
						|| sourceMainAccount.getAccountAvailableAmount().compareTo(tradeAmount) < 0) {
					throw new SLException("账户余额不足");
				}
				// 源主账户 支出
				sourceMainAccount.setAccountTotalAmount(ArithUtil.sub(sourceMainAccount.getAccountTotalAmount(), tradeAmount));
				sourceMainAccount.setAccountAvailableAmount(ArithUtil.sub(sourceMainAccount.getAccountAvailableAmount(), tradeAmount));
				sourceMainAccount.setBasicModelProperty(userId, false);
				AccountFlowInfoEntity accountFlowInfoEntity1 = accountFlowService.saveAccountFlow(sourceMainAccount, null, null, null, "1",
						tradeType, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT,
						tradeAmount, subjectType,
						flowBusiRelateType, flowRelatePrimary, SubjectConstant.SUBJECT_TYPE_AMOUNT);
				accountFlowInfoEntity1.setMemo(memo);

				accountList.add(accountFlowInfoEntity1);
			}
			break;
			case "7":
			{
				if(sourceMainAccount.getAccountAvailableAmount().compareTo(tradeAmount) < 0) {
					throw new SLException("账户余额不足");
				}
				// 源主账户 冻结
				sourceMainAccount.setAccountFreezeAmount(ArithUtil.add(sourceMainAccount.getAccountFreezeAmount(), tradeAmount));
				sourceMainAccount.setAccountAvailableAmount(ArithUtil.sub(sourceMainAccount.getAccountAvailableAmount(), tradeAmount));
				sourceMainAccount.setBasicModelProperty(userId, false);
				AccountFlowInfoEntity accountFlowInfoEntity1 = accountFlowService.saveAccountFlow(sourceMainAccount, null, null, null, "1",
						tradeType, reqeustNo, null,
						tradeAmount, subjectType,
						flowBusiRelateType, flowRelatePrimary, SubjectConstant.SUBJECT_TYPE_AMOUNT);
				accountFlowInfoEntity1.setMemo(memo);

				accountList.add(accountFlowInfoEntity1);
			}
			break;
			case "8":
			{
				if(sourceMainAccount.getAccountFreezeAmount().compareTo(tradeAmount) < 0) {
					throw new SLException("账户冻结余额不足");
				}
				// 源主账户 解冻
				sourceMainAccount.setAccountFreezeAmount(ArithUtil.sub(sourceMainAccount.getAccountFreezeAmount(), tradeAmount));
				sourceMainAccount.setAccountAvailableAmount(ArithUtil.add(sourceMainAccount.getAccountAvailableAmount(), tradeAmount));
				sourceMainAccount.setBasicModelProperty(userId, false);
				AccountFlowInfoEntity accountFlowInfoEntity1 = accountFlowService.saveAccountFlow(sourceMainAccount, null, null, null, "1",
						tradeType, reqeustNo, null,
						tradeAmount, subjectType,
						flowBusiRelateType, flowRelatePrimary, SubjectConstant.SUBJECT_TYPE_AMOUNT);
				accountFlowInfoEntity1.setMemo(memo);

				accountList.add(accountFlowInfoEntity1);
			}
			break;
			case "9":
			{
				if(sourceSubAccount.getAccountTotalValue().compareTo(tradeAmount) < 0
						|| sourceSubAccount.getAccountAvailableValue().compareTo(tradeAmount) < 0) {
					throw new SLException("账户余额不足");
				}
				// 目标分账户 支出
				sourceSubAccount.setAccountTotalValue(ArithUtil.sub(sourceSubAccount.getAccountTotalValue(), tradeAmount));
				sourceSubAccount.setAccountAvailableValue(ArithUtil.sub(sourceSubAccount.getAccountAvailableValue(), tradeAmount));
				sourceSubAccount.setBasicModelProperty(userId, false);
				AccountFlowInfoEntity accountFlowInfoEntity1 = accountFlowService.saveAccountFlow(null, sourceSubAccount, null, null, "4",
						tradeType, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT,
						tradeAmount, subjectType,
						flowBusiRelateType, flowRelatePrimary, SubjectConstant.SUBJECT_TYPE_AMOUNT);
				accountFlowInfoEntity1.setMemo(memo);

				accountList.add(accountFlowInfoEntity1);
			}
			break;
			case "10":
			{
				// 目标分账户 收入
				sourceSubAccount.setAccountTotalValue(ArithUtil.add(sourceSubAccount.getAccountTotalValue(), tradeAmount));
				sourceSubAccount.setAccountAvailableValue(ArithUtil.add(sourceSubAccount.getAccountAvailableValue(), tradeAmount));
				sourceSubAccount.setBasicModelProperty(userId, false);
				AccountFlowInfoEntity accountFlowInfoEntity1 = accountFlowService.saveAccountFlow(null, sourceSubAccount, null, null, "4",
						tradeType, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING,
						tradeAmount, subjectType,
						flowBusiRelateType, flowRelatePrimary, SubjectConstant.SUBJECT_TYPE_AMOUNT);
				accountFlowInfoEntity1.setMemo(memo);

				accountList.add(accountFlowInfoEntity1);
			}
			break;
			case "11":
			{
				if(sourceSubAccount.getAccountAvailableValue().compareTo(tradeAmount) < 0) {
					throw new SLException("账户余额不足");
				}
				// 目标分账户 冻结
				sourceSubAccount.setAccountFreezeValue(ArithUtil.add(sourceSubAccount.getAccountFreezeValue(), tradeAmount));
				sourceSubAccount.setAccountAvailableValue(ArithUtil.sub(sourceSubAccount.getAccountAvailableValue(), tradeAmount));
				sourceSubAccount.setBasicModelProperty(userId, false);
				AccountFlowInfoEntity accountFlowInfoEntity1 = accountFlowService.saveAccountFlow(null, sourceSubAccount, null, null, "4",
						tradeType, reqeustNo, null,
						tradeAmount, subjectType,
						flowBusiRelateType, flowRelatePrimary, SubjectConstant.SUBJECT_TYPE_AMOUNT);
				accountFlowInfoEntity1.setMemo(memo);

				accountList.add(accountFlowInfoEntity1);
			}
			break;
			case "12":
			{
				if(sourceSubAccount.getAccountFreezeValue().compareTo(tradeAmount) < 0) {
					throw new SLException("账户冻结余额不足");
				}
				// 目标分账户 解冻
				sourceSubAccount.setAccountFreezeValue(ArithUtil.sub(sourceSubAccount.getAccountFreezeValue(), tradeAmount));
				sourceSubAccount.setAccountAvailableValue(ArithUtil.add(sourceSubAccount.getAccountAvailableValue(), tradeAmount));
				sourceSubAccount.setBasicModelProperty(userId, false);
				AccountFlowInfoEntity accountFlowInfoEntity1 = accountFlowService.saveAccountFlow(null, sourceSubAccount, null, null, "4",
						tradeType, reqeustNo, null,
						tradeAmount, subjectType,
						flowBusiRelateType, flowRelatePrimary, SubjectConstant.SUBJECT_TYPE_AMOUNT);
				accountFlowInfoEntity1.setMemo(memo);

				accountList.add(accountFlowInfoEntity1);
			}
			break;
			case "13":
			{
				if(sourceMainAccount.getAccountTotalAmount().compareTo(tradeAmount) < 0
						|| sourceMainAccount.getAccountAvailableAmount().compareTo(tradeAmount) < 0) {
					throw new SLException("账户余额不足");
				}
				// 源主账户 支出
				sourceMainAccount.setAccountTotalAmount(ArithUtil.sub(sourceMainAccount.getAccountTotalAmount(), tradeAmount));
				sourceMainAccount.setAccountAvailableAmount(ArithUtil.sub(sourceMainAccount.getAccountAvailableAmount(), tradeAmount));
				sourceMainAccount.setBasicModelProperty(userId, false);
				AccountFlowInfoEntity accountFlowInfoEntity1 = accountFlowService.saveAccountFlow(sourceMainAccount, null, null, destSubAccount, "2",
						tradeType, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT,
						tradeAmount, subjectType,
						flowBusiRelateType, flowRelatePrimary, SubjectConstant.SUBJECT_TYPE_AMOUNT);
				accountFlowInfoEntity1.setMemo(memo);

				// 目标分账户 收入
				destSubAccount.setAccountAmount(ArithUtil.add(destSubAccount.getAccountAmount(), tradeAmount));
				destSubAccount.setBasicModelProperty(userId, false);
				AccountFlowInfoEntity accountFlowInfoEntity2 = accountFlowService.saveAccountFlow(null, destSubAccount, sourceMainAccount, null, "3",
						tradeType, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING,
						tradeAmount, subjectType,
						flowBusiRelateType, flowRelatePrimary, SubjectConstant.SUBJECT_TYPE_AMOUNT);
				accountFlowInfoEntity2.setMemo(memo);

				accountList.add(accountFlowInfoEntity1);
				accountList.add(accountFlowInfoEntity2);
			}
			break;
			case "14":
			{
				if(sourceSubAccount.getAccountAmount().compareTo(tradeAmount) < 0) {
					throw new SLException("账户余额不足");
				}
				// 目标分账户 支出
				sourceSubAccount.setAccountAmount(ArithUtil.sub(sourceSubAccount.getAccountAmount(), tradeAmount));
				sourceSubAccount.setBasicModelProperty(userId, false);
				AccountFlowInfoEntity accountFlowInfoEntity2 = accountFlowService.saveAccountFlow(null, sourceSubAccount, destMainAccount, null, "3",
						tradeType, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT,
						tradeAmount, subjectType,
						flowBusiRelateType, flowRelatePrimary, SubjectConstant.SUBJECT_TYPE_AMOUNT);
				accountFlowInfoEntity2.setMemo(memo);

				// 源主账户 收入
				destMainAccount.setAccountTotalAmount(ArithUtil.add(destMainAccount.getAccountTotalAmount(), tradeAmount));
				destMainAccount.setAccountAvailableAmount(ArithUtil.add(destMainAccount.getAccountAvailableAmount(), tradeAmount));
				destMainAccount.setBasicModelProperty(userId, false);
				AccountFlowInfoEntity accountFlowInfoEntity1 = accountFlowService.saveAccountFlow(destMainAccount, null, null, sourceSubAccount, "2",
						tradeType, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING,
						tradeAmount, subjectType,
						flowBusiRelateType, flowRelatePrimary, SubjectConstant.SUBJECT_TYPE_AMOUNT);
				accountFlowInfoEntity1.setMemo(memo);

				accountList.add(accountFlowInfoEntity1);
				accountList.add(accountFlowInfoEntity2);
			}
			break;
		}

		return accountList;
	}
}