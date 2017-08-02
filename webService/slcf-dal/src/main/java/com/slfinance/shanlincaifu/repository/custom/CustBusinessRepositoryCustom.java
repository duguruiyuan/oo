/** 
 * @(#)CustBusinessRepositoryCustom.java 1.0.0 2015年12月11日 上午9:44:47  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository.custom;

import java.util.Map;

import org.springframework.data.domain.Page;

/**   
 * 自定义客户业务数据访问接口
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年12月11日 上午9:44:47 $ 
 */
public interface CustBusinessRepositoryCustom {
	
	/**
	 * 查询注册报告
	 *
	 * @author  wangjf
	 * @date    2015年12月11日 上午9:45:32
	 * @param params
	 * @return
	 */
	public Page<Map<String, Object>> findRegisterReport(Map<String, Object> params);
	
	/**
	 * 统计注册报告
	 *
	 * @author  wangjf
	 * @date    2015年12月11日 上午10:27:44
	 * @param params
	 * @return
	 */
	public int countRegisterReport(Map<String, Object> params);
}
