/** 
 * @(#)WithDrawCashStatistics.java 1.0.0 2015年4月28日 上午11:03:40  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.vo;

import java.io.Serializable;
import java.math.BigDecimal;

/**   
 * 提现统计信息实体类
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年4月28日 上午11:03:40 $ 
 */
@SuppressWarnings("serial")
public class WithDrawCashStatisticsVO implements Serializable{
	
	/**
	 * 提现金额汇总
	 */
	private BigDecimal tradeAmountCount;
	
	/**
	 * 际到账金额汇总
	 */
	private BigDecimal tradeCostCount;    
	
	/**
	 * 提现手续费汇总
	 */
	private BigDecimal realTradeAmountCount;

	public BigDecimal getTradeAmountCount() {
		return tradeAmountCount;
	}

	public void setTradeAmountCount(BigDecimal tradeAmountCount) {
		this.tradeAmountCount = tradeAmountCount;
	}

	public BigDecimal getTradeCostCount() {
		return tradeCostCount;
	}

	public void setTradeCostCount(BigDecimal tradeCostCount) {
		this.tradeCostCount = tradeCostCount;
	}

	public BigDecimal getRealTradeAmountCount() {
		return realTradeAmountCount;
	}

	public void setRealTradeAmountCount(BigDecimal realTradeAmountCount) {
		this.realTradeAmountCount = realTradeAmountCount;
	}
	
	
}
