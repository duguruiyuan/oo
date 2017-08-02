/** 
 * @(#)NumberService.java 1.0.0 2015年1月8日 下午7:58:36  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */

package com.slfinance.shanlincaifu.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slfinance.shanlincaifu.repository.custom.AccountInfoRepositoryCustom;
import com.slfinance.shanlincaifu.utils.DateUtils;

/**
 * 系统各种编号生成
 * 
 * @author zhanghao
 * @version $Revision:1.0.0, $Date: 2015年1月8日 下午7:58:36 $
 */
@Service
public class FlowNumberService {

	@Autowired
	SequenceService sequenceService;
	
	@Autowired
	AccountInfoRepositoryCustom accountInfoRepositoryCustom;

	/**
	 * 生成交易编号
	 * 
	 * @param flowNumber
	 * @return
	 */
	public String generateTradeNumber() {
		return "SLCF-TRADE-" + String.format("%012d", sequenceService.getTradeNumberSequence());
	}

	/**
	 * 生成报盘批次号SL-BATCH-12位流水号000000000001
	 * 
	 * @return
	 */
	public String generateTradeBatchNumber() {
		return "SLCF-BATCH-" + String.format("%012d", sequenceService.getTradeBatchNumberSequence());
	}
	
	public List<String> generateTradeNumber(int size) {
		return sequenceService.getTradeNumberSequence("SLCF-TRADE-", size);
	}
	
	public List<String> generateTradeBatchNumber(int size) {
		return sequenceService.getTradeBatchNumberSequence("SLCF-BATCH-", size);
	}

	/**
	 * 生成提现编号
	 * 
	 * @return
	 */
	public String generateWithdrawNumber() {
		return "SLCF-WITHDRAW-" + String.format("%012d", sequenceService.getWithdrawNumberSequence());
	}

	/**
	 * 生成投资编号, 生成规则:SLCF+yyyyMMdd+当天五位序列号,起始值:00001
	 * 
	 * @return
	 */
	public String generateLoanContractNumber() {
		return "SLCF" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + String.format("%05d", sequenceService.getContractNumberSequence());
	}

	/**
	 * 生成客户编号, 生成规则:12位,不够位前面加零,起始值：100000000000
	 * 
	 * @return
	 */
	public String generateCustomerNumber() {
		return String.valueOf(sequenceService.getCustomerNumberSequence());
	}
	
	/**
	 * 分配编号
	 *
	 * @author  wangjf
	 * @date    2015年5月1日 下午3:04:06
	 * @return
	 */
	public String generateAllotNumber() {
		return "SLCF" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + String.format("%05d", sequenceService.getAllotNumberSequence());
	}
	
	/**
	 * 对外流水交易编号
	 * 
	 * @param flowNumber
	 * @return
	 */
	public String generateOpenServiceTradeNumber() {
		return "SLCF-TRADE-" + String.format("%012d", sequenceService.getOpenServiceNumberSequence());
	}
	
	/**
	 * 生成金牌推荐人客户申请编号
	 */
	public String generateRecCustApplyNo() {
		return "CUST-APPLY-" + String.format("%012d", sequenceService.getRecCustApplyNoSequence());
	}

	/**
	 * 生成注册用户昵称
	 *
	 * @author  wangjf
	 * @date    2015年11月2日 上午10:37:38
	 * @return
	 */
	public String generateUserNickName() {
		return "SLCF_" + String.format("%012d", sequenceService.getUserNickNameSequnce());
	}
	
	/**
	 * 生成债权转让序号
	 *
	 * @author  wangjf
	 * @date    2015年11月2日 上午10:37:38
	 * @return
	 */
	public String generateLoanTransferNo() {
		return "SLCF_" + DateUtils.getCurrentDate("yyyyMMdd") + String.format("%012d", sequenceService.getLoanTransferSequnce());
	}
	
	/**
	 * 债权转让申请编号
	 *
	 * @author  wangjf
	 * @date    2016年12月27日 下午3:39:08
	 * @return
	 */
	public String generateLoanTransferApplyNo() {
		return String.format("ZR%09d", sequenceService.getLoanTransferApplySequnce());
	}
	
	/**
	 * 债权转让转出编号
	 *
	 * @author  wangjf
	 * @date    2016年12月28日 下午1:37:31
	 * @param prefix
	 * @return
	 */
	public String generateLoanTransferOutNo() {
		return String.format("ZR%011d", sequenceService.getLoanTransferSequnce());
	}
	
	/**
	 * 佣金交易编号
	 *
	 * @author  wangjf
	 * @date    2017年1月13日 上午9:33:13
	 * @return
	 */
	public String generateCommissionTradeNo() {
		return String.format("YJ%012d", sequenceService.generateCommissionSequnce());
	}
	
	/**
	 * 活动序号
	 *
	 * @author  guoyk
	 * @date    2017年4月27日 上午11:34:38
	 * @return
	 */
	public String generateActivitySequnce() {
		return String.format("HD%013d", sequenceService.generateActivitySequnce());
	}
	
	/**
	 * 生成报盘批次号SL-BATCH-12位流水号000000000001
	 * 
	 * @return
	 */
	public String generateReserveBatchNumber() {
		return "ZTYY-BATCH-" + String.format("%012d", sequenceService.getReserveBatchNumberSequence());
	}
	/**
	 * 
	 * <生成体验标的借款编号>
	 * <功能详细描述>
	 *
	 * @return [参数说明]
	 * @return String [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	public String generateExperienceTheNumber(){
		return "TY_"+String.format("%07d", sequenceService.getExperienceTheNumberSequence());
		
	}

	/**
	 * 生成一键出借序列
	 *
	 * @author  wangjf
	 * @date    2017年7月21日 上午11:48:13
	 * @return
	 */
	public String generateLoanReserveTradeNo(){
		return String.format("%04d", sequenceService.getLoanReserveTradeNoSequence());
		
	}

}
