/** 
 * @(#)FinancialStatisticsCustom.java 1.0.0 2015年8月11日 上午11:26:06  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 
package com.slfinance.shanlincaifu.repository.custom;

import java.util.List;
import java.util.Map;

import com.slfinance.exception.SLException;

/**   
 * 财务统计接口
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年8月11日 上午11:26:06 $ 
 */
public interface FinancialStatisticsCustom {

	/**
	 * 当天未还款数据汇总
	 * @return
	 * @throws SLException
	 */
	List<Map<String,Object>> getRepaymentList() throws SLException;
	
}
