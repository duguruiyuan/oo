/** 
 * @(#)CustAccountService.java 1.0.0 2015年5月11日 下午3:43:05  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AccountFlowInfoEntity;
import com.slfinance.shanlincaifu.entity.AccountInfoEntity;
import com.slfinance.shanlincaifu.entity.SubAccountInfoEntity;
import com.slfinance.shanlincaifu.entity.TradeFlowInfoEntity;
import com.slfinance.vo.ResultVo;
import java.math.BigDecimal;
import java.util.List;

/**   
 * 更新账户信息和新增流水服务接口
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年5月11日 下午3:43:05 $ 
 */
public interface CustAccountService {
	
	/***
	 * 更新客户账户信息,并且记录账户流水详情信息
	 * 
	 * @author zhangzs
	 * @date 2015年5月11日 下午3:43:05  
	 * @param accountFlowInfo
	 * @param accountFlowDetail
	 * @param accountInfo
	 * @return
	 * @throws SLException 
	 */
	ResultVo updateCustAccount(AccountFlowInfoEntity accountFlowInfo, AccountInfoEntity accountInfo) throws SLException;

	/***
	 * 更新客户账户信息,并且记录账户流水详情信息
	 * 
	 * @author zhangzs
	 * @date 2015年5月11日 下午3:43:05  
	 * @param memo       	      备注信息
	 * @param tradeFlowInfo   原账户交易流水信息
	 * @param accountFlowInfo 账户冻结流水信息
	 * @param accountInfo 	      账户信息	
	 * @param tradeType       交易类型
	 * @return
	 * @throws SLException 
	 */
	ResultVo updateCustAccount(String memo,TradeFlowInfoEntity tradeFlowInfo,AccountFlowInfoEntity accountFlowInfo,AccountInfoEntity accountInfo,String tradeType) throws SLException;

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
	 * @param memo
	 * @return
	 */
	public List<AccountFlowInfoEntity> updateAccount(AccountInfoEntity sourceMainAccount, SubAccountInfoEntity sourceSubAccount, 
			AccountInfoEntity destMainAccount, SubAccountInfoEntity destSubAccount,
			String accountType, String tradeType, String reqeustNo, BigDecimal tradeAmount, String subjectType,
			String flowBusiRelateType, String flowRelatePrimary, String memo, String userId) throws SLException;

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
	List<AccountFlowInfoEntity> updateAccountExt(AccountInfoEntity sourceMainAccount, SubAccountInfoEntity sourceSubAccount,
			AccountInfoEntity destMainAccount, SubAccountInfoEntity destSubAccount,
			String accountType, String tradeType, String reqeustNo, BigDecimal tradeAmount, String subjectType,
			String flowBusiRelateType, String flowRelatePrimary, String memo, String userId,
			BigDecimal usableAmount) throws SLException;
}