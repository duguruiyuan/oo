/** 
 * @(#)LoanAllotHistoryRepositoryCustom.java 1.0.0 2015年5月23日 下午12:24:56  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository.custom;

import java.util.Date;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.slfinance.exception.SLException;

/**   
 * 债权历史情况数据访问类
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年5月23日 下午12:24:56 $ 
 */
public interface LoanAllotHistoryRepositoryCustom {

	/**
	 * 查询用户每天所拥有的债权情况
	 *
	 * @author  wangjf
	 * @date    2015年5月23日 下午12:05:01
	 * @param param
	 		<tt>start：int:分页起始页</tt><br>
	 		<tt>length：int:每页长度</tt><br>
	 		<tt>custId：String:客户主键</tt><br>
	 		<tt>dailyValueId： String:用户每天价值情况主键</tt><br>
	 * @return
	 * 		<tt>loanCode： String:债权编号</tt><br>
	 		<tt>debtSource： String:来源机构</tt><br>
			<tt>loanAmount： BigDecimal:借款金额（元）</tt><br>
			<tt>holdValue： BigDecimal:持有价值</tt><br>
			<tt>loanTerm： String:借款期限（月）</tt><br>
			<tt>yearRate： BigDecimal:年化利率</tt><br>
			<tt>assetTypeCode： String:资产类型</tt><br>	
			<tt>loanDesc： String:借款用途</tt><br>	
			<tt>repaymentMethod： String:还款方式</tt><br>	
	 */
	public Page<Map<String, Object>> findDailyLoanList(Map<String, Object> param);
	
	/**
	 * 保存每日债权
	 *
	 * @author  wangjf
	 * @date    2015年5月23日 下午3:22:52
	 * @param now
	 */
	public void saveDailyLoanList(Date now, String productName) throws SLException;
}
