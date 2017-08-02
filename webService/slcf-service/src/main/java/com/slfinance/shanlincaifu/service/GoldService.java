/** 
 * @(#)GoldService.java 1.0.0 2015年8月24日 上午10:46:00  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service;

import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.vo.ResultVo;

/**   
 * 金牌推荐人接口
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年8月24日 上午10:46:00 $ 
 */
public interface GoldService {

	/**
	 * 金牌推荐人每日结息(活期宝)
	 *
	 * @author  wangjf
	 * @date    2015年8月24日 上午10:47:43
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo goldDailySettlement(Map<String, Object> params)throws SLException;
	
	/**
	 * 到期处理
	 *
	 * @author  wangjf
	 * @date    2015年8月24日 上午10:48:59
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo goldWithdraw(Map<String, Object> params)throws SLException;
	
	/**
	 * 金牌推荐人月结
	 *
	 * @author  wangjf
	 * @date    2015年10月12日 上午9:42:25
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo goldMonthlySettlement(Map<String, Object> params) throws SLException;
	
	/**
	 * 生成单笔投资的业务员业绩情况
	 *
	 * @author  wangjf
	 * @date    2016年12月5日 下午6:52:15
	 * @param params
	 * 		<tt>investId:单笔投资</tt>
	 *      <tt>custId:业务员ID</tt>
	 *      <tt>loanUnit:期限的单位（月、天）</tt>
	 * @return
	 * @throws SLException
	 */
	public ResultVo createSingleCommission(Map<String, Object> params) throws SLException;
	
	/**
	 * 业绩到期处理
	 *
	 * @author  wangjf
	 * @date    2016年12月8日 下午2:13:49
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo commissionWithdraw(Map<String, Object> params)throws SLException;
}
