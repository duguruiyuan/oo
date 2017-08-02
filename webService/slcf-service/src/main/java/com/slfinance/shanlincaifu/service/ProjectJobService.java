/** 
 * @(#)ProjectJobService.java 1.0.0 2016年1月6日 下午12:32:23  
 *  
 * Copyright © 2016 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service;

import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.vo.ResultVo;

/**   
 * 项目定时任务接口服务
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2016年1月6日 下午12:32:23 $ 
 */
public interface ProjectJobService {

	/**
	 * Job项目定时发布（待发布->发布中）
	 *
	 * @author  wangjf
	 * @date    2016年1月5日 下午12:05:18
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo autoPublishProject(Map<String, Object> params) throws SLException;
	
	/**
	 * Job项目定时生效（满标复核->还款中）
	 *
	 * @author  wangjf
	 * @date    2016年1月5日 下午12:05:21
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo autoReleaseProject(Map<String, Object> params) throws SLException;
	
	/**
	 * Job项目自动流标
	 *
	 * @author  wangjf
	 * @date    2016年1月5日 下午12:05:23
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo autoUnReleaseProject(Map<String, Object> params) throws SLException;
	
	/**
	 * Job项目自动贴息
	 *
	 * @author  wangjf
	 * @date    2016年1月5日 下午12:05:26
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo autoCompensateProject(Map<String, Object> params) throws SLException;
	
	/**
	 * Job项目定时还款
	 *
	 * @author  wangjf
	 * @date    2016年1月5日 下午12:05:28
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo autoRepaymentProject(Map<String, Object> params) throws SLException;
	
	/**
	 * Job项目定时垫付
	 *
	 * @author  wangjf
	 * @date    2016年1月5日 下午12:05:28
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo autoRiskRepaymentProject(Map<String, Object> params) throws SLException;
	
	/**
	 * Job 定时审核拒绝
	 * 
	 * @author zhangt
	 * @date   2016年1月28日 下午16:23:28
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo autoRefuseProject(Map<String, Object> params) throws SLException;
	
}
