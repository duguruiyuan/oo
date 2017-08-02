/** 
 * @(#)WealthHoldInfoRepositoryCustom.java 1.0.0 2016年2月23日 下午7:27:44  
 *  
 * Copyright © 2016 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository.custom;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.WealthHoldInfoEntity;

/**   
 * 自定义持有价值数据访问接口
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2016年2月23日 下午7:27:44 $ 
 */
public interface WealthHoldInfoRepositoryCustom {

	/**
	 * 查询用户持有的PV
	 *
	 * @author  wangjf
	 * @date    2016年2月23日 下午7:29:40
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> findUserHoldPv(Map<String, Object> params);
	
	/**
	 * 查询居间人持有的PV
	 *
	 * @author  wangjf
	 * @date    2016年2月23日 下午7:48:58
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> findCenterHoldPv(Map<String, Object> params);
	
	/**
	 * 批量更新
	 *
	 * @author  wangjf
	 * @date    2016年2月23日 下午9:08:21
	 * @param wealthHoldList
	 */
	public void batchUpdate(List<WealthHoldInfoEntity> wealthHoldList) throws SLException;
	
	/**
	 * 查询用户持有的PV(汇总)
	 *
	 * @author  wangjf
	 * @date    2016年2月23日 下午7:29:40
	 * @param params
	 * @return
	 */
	public BigDecimal sumUserHoldPv(Map<String, Object> params);
	
	/**
	 * 批量债权转让
	 *
	 * @author  wangjf
	 * @date    2016年2月24日 上午11:22:24
	 * @param params
	 */
	public void batchLoanTransfer(Map<String, Object> params) throws SLException;
	
	/**
	 * 查询所有可用的债权分页查询
	 * 
	 * @author zhiwen_feng
	 * @date 2016-05-10
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public Page<Map<String, Object>> queryCanUserLoan(Map<String, Object> params)throws SLException;
	
	/**
	 *根据理财计划id查询已经匹配的债权
	 *
	 *@author zhiwen_feng
	 *@date 2016-05-10
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public Page<Map<String, Object>> queryPageAlreadyAutoLoanByWealthId(Map<String, Object> params)throws SLException;
}
