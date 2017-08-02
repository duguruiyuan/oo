/** 
 * @(#)WealthJobService.java 1.0.0 2016年2月23日 上午10:46:20  
 *  
 * Copyright © 2016 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service;

import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.vo.ResultVo;

/**   
 * 优选计划定时任务
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2016年2月23日 上午10:46:20 $ 
 */
public interface WealthJobService {


	/**
	 * 流标
	 *
	 * @author  fengzw
	 * @date    2016年2月25日 上午10:37:52
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo autoUnReleaseWealth(Map<String, Object> params) throws SLException;
	
	/**
	 * 自动匹配债权(首次撮合、还款撮合)
	 *
	 * @author  fengzw
	 * @date    2016年2月25日 上午10:38:14
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo autoMatchLoan(Map<String, Object> params) throws SLException;
	
	/**
	 * 生成业务报表
	 *
	 * @author  zhangt
	 * @date    2016年2月25日 上午10:38:34
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo autoMonthlyWealth(Map<String, Object> params) throws SLException;
	
	/**
	 * 还款
	 *
	 * @author  wangjf
	 * @date    2016年2月23日 上午11:16:11
	 * @param params
	 * 	expectRepaymentDate
	 * @return
	 * @throws SLException
	 */
	public ResultVo autoRepaymentWealth(Map<String, Object> params) throws SLException;
	
	/**
	 * 月返息
	 *
	 * @author  wangjf
	 * @date    2016年2月23日 上午11:17:06
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo autoRecoveryWealth(Map<String, Object> params) throws SLException;
	
	/**
	 * 到期和提前赎回处理
	 *
	 * @author  wangjf
	 * @date    2016年2月23日 上午11:17:10
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo autoAtoneWealth(Map<String, Object> params) throws SLException;
	
	/**
	 * 自动发布优选计划
	 * 
	 * @author zhiwen_feng
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo autoPublishWealth(Map<String, Object> params)throws SLException;
	
	/**
	 *  自动生效优选计划
	 * 
	 * @author zhiwen_feng
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo autoReleaseWealthJob(Map<String, Object> params)throws SLException;
	
	/**
	 * 新开事物发布优选计划
	 * 
	 * @author zhiwen_feng
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo publishWealth(Map<String, Object> params)throws SLException;
} 
 