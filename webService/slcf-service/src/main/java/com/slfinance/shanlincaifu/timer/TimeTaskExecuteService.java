package com.slfinance.shanlincaifu.timer;

import com.slfinance.exception.SLException;

/**   
 * 
 * 定时任务统一接口
 * @author  zhoudl
 * @version $Revision:1.0.0, $Date: 2015年4月23日 上午10:04:46 $ 
 */
public interface TimeTaskExecuteService {
	
	/**
	 * @desc 预算债权PV值
	 * @author zhoudl
	 * **/
	public void execLoanPv() throws SLException;
	

	/**
	 * 定时发标
	 * 
	 * @author caoyi
	 * @date 2015年05月01日 下午午13:41:36
	*/
	public void autoOpenBid() throws SLException;

	/**
	 * 可开放价值计算
	 * 
	 * @author caoyi
	 * @date 2015年05月01日 下午午15:43:41
	*/
	public void computeOpenValue() throws SLException;
	
	/**
	 * 每日结息
	 * 
	 * @author linhj
	 * @date 2015年05月01日 下午午15:43:41
	*/
	public void dailySettlement() throws SLException;
}
