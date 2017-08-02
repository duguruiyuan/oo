/** 
 * @(#)SequenceService.java 1.0.0 2014年11月29日 下午2:26:44  
 *  
 * Copyright © 2014 善林金融.  All rights reserved.  
 */

package com.slfinance.shanlincaifu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slfinance.shanlincaifu.repository.custom.AccountInfoRepositoryCustom;

/**
 * 序列Service
 * 
 * @author zhanghao
 * @version $Revision:1.0.0, $Date: 2014年11月29日 下午2:26:44 $
 */
@Service
public class SequenceService {

	@Autowired
	AccountInfoRepositoryCustom accountInfoRepositoryCustom;

	/**
	 * 获取交易编号序列值
	 * 
	 * @return
	 */
	public long getTradeNumberSequence() {
		return accountInfoRepositoryCustom.findSequenceValueByName("TRADE_NUMBER_SEQ");
	}

	/**
	 * 获取交易批次号序列值
	 * 
	 * @return
	 */
	public long getTradeBatchNumberSequence() {
		return accountInfoRepositoryCustom.findSequenceValueByName("TRADE_BATCH_NUMBER_SEQ");
	}
	
	/**
	 * 批量获取交易编号序列值
	 *
	 * @author  wangjf
	 * @date    2015年9月9日 下午4:50:12
	 * @param prefix
	 * @param size
	 * @return
	 */
	public List<String> getTradeNumberSequence(String prefix, int size) {
		return accountInfoRepositoryCustom.findMoreSequenceValueByName(prefix, "TRADE_NUMBER_SEQ", size);
	}
	
	/**
	 * 批量获取交易批次号序列值
	 *
	 * @author  wangjf
	 * @date    2015年9月9日 下午4:50:26
	 * @param prefix
	 * @param size
	 * @return
	 */
	public List<String> getTradeBatchNumberSequence(String prefix, int size) {
		return accountInfoRepositoryCustom.findMoreSequenceValueByName(prefix, "TRADE_BATCH_NUMBER_SEQ", size);
	}

	/**
	 * 获取提现编号序列值
	 * 
	 * @return
	 */
	public long getWithdrawNumberSequence() {
		return accountInfoRepositoryCustom.findSequenceValueByName("WITHDRAW_NUMBER_SEQ");
	}

	/**
	 * 获取合同编号序列值
	 * 
	 * @return
	 */
	public long getContractNumberSequence() {
		return accountInfoRepositoryCustom.findSequenceValueByName("CONTRACT_NUMBER_SEQ");
	}

	/**
	 * 获取客户编号序列值
	 * 
	 * @return
	 */
	public long getCustomerNumberSequence() {
		return accountInfoRepositoryCustom.findSequenceValueByName("CUSTOMER_NUMBER_SEQ");
	}

	/**
	 * 获取分配编号
	 *
	 * @author  wangjf
	 * @date    2015年5月1日 下午3:05:27
	 * @return
	 */
	public long getAllotNumberSequence() {
		return accountInfoRepositoryCustom.findSequenceValueByName("ALLOT_NUMBER_SEQ");
	}
	
	/**
	 * 对外交易流水编号
	 *
	 * @author  wangjf
	 * @date    2015年5月1日 下午3:05:27
	 * @return
	 */
	public long getOpenServiceNumberSequence() {
		return accountInfoRepositoryCustom.findSequenceValueByName("OPEN_SERVICE_NUMBER_SEQ");
	}

	/**
	 * 生成金牌推荐人客户申请编号
	 */
	public Object getRecCustApplyNoSequence() {
		return accountInfoRepositoryCustom.findSequenceValueByName("REC_CUST_APPLY_NUMBER_SEQ");

	}
	
	/**
	 * 生成用户昵称
	 *
	 * @author  wangjf
	 * @date    2015年11月2日 上午10:39:34
	 * @return
	 */
	public long getUserNickNameSequnce() {
		return accountInfoRepositoryCustom.findSequenceValueByName("USER_NICK_NAME_SEQ");
	}
	
	/**
	 * 生成债权转让序号
	 *
	 * @author  wangjf
	 * @date    2015年11月2日 上午10:39:34
	 * @return
	 */
	public long getLoanTransferSequnce() {
		return accountInfoRepositoryCustom.findSequenceValueByName("LOAN_TRANSFER_NO_SEQ");
	}
	
	/**
	 * 生成债权转让申请序号
	 *
	 * @author  wangjf
	 * @date    2016年12月27日 下午4:01:39
	 * @return
	 */
	public long getLoanTransferApplySequnce() {
		return accountInfoRepositoryCustom.findSequenceValueByName("LOAN_TRANSFER_APPLY_NO_SEQ");
	}
	
	/**
	 * 佣金交易序号
	 *
	 * @author  wangjf
	 * @date    2017年1月13日 上午9:34:38
	 * @return
	 */
	public long generateCommissionSequnce() {
		return accountInfoRepositoryCustom.findSequenceValueByName("COMMISSION_TRADE_NO_SEQ");
	}
	
	/**
	 * 活动序号
	 *
	 * @author  guoyk
	 * @date    2017年4月27日 上午11:34:38
	 * @return
	 */
	public long generateActivitySequnce() {
		return accountInfoRepositoryCustom.findSequenceValueByName("ACTIVITY_TRADE_NO_SEQ");
	}
	
	/**
	 * 获取交易批次号序列值
	 * 
	 * @return
	 */
	public long getReserveBatchNumberSequence() {
		return accountInfoRepositoryCustom.findSequenceValueByName("RESERVE_BATCH_NUMBER_SEQ");
	}
	/**
	 * 
	 * <生成体验标的借款编号>
	 * <功能详细描述>
	 *
	 * @return [参数说明]
	 * @return long [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	public long getExperienceTheNumberSequence(){
		return accountInfoRepositoryCustom.findSequenceValueByName("TY_NUMBER_SEQ");
		
	}
	
	/**
	 * 生成一键出借序列
	 *
	 * @author  wangjf
	 * @date    2017年7月21日 上午11:49:06
	 * @return
	 */
	public long getLoanReserveTradeNoSequence(){
		return accountInfoRepositoryCustom.findSequenceValueByName("LOAN_RESERVE_TRADE_NO_SEQ");
		
	}
	
}
