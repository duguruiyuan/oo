/** 
 * @(#)ProjectRepaymentRepositoryCustom.java 1.0.0 2016年1月6日 下午7:15:40  
 *  
 * Copyright © 2016 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository.custom;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

/**   
 * 自定义项目还款数据访问接口
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2016年1月6日 下午7:15:40 $ 
 */
public interface ProjectRepaymentRepositoryCustom {

	/**
	 * 近期应还数据列表
	 *
	 * @author  wangjf
	 * @date    2016年1月6日 下午7:32:57
	 * @param params
	 * @return
	 */
	Page<Map<String, Object>> queryLatestRepaymentList(Map<String, Object> params);
	
	/**
	 * 应还总额、已还总额、公司可用余额查询
	 *
	 * @author  wangjf
	 * @date    2016年1月6日 下午7:54:25
	 * @param params
	 * @return
	 */
	Map<String, Object> queryLatesttRepaymentTotal(Map<String, Object> params);
	
	/**
	 * 逾期中数据列表
	 *
	 * @author  wangjf
	 * @date    2016年1月7日 上午11:43:27
	 * @param params
	 * @return
	 */
	Page<Map<String, Object>> queryOverdueRepaymentList(Map<String, Object> params);
	
	/**
	 * 应还总额、已还总额、公司可用余额、逾期总额查询
	 *
	 * @author  wangjf
	 * @date    2016年1月7日 下午2:45:02
	 * @param params
	 * @return
	 */
	Map<String, Object> queryOverdueRepaymentTotal(Map<String, Object> params);
	
	/**
	 * 还款数据列表（含提前结清应付利息和本金）
	 *
	 * @author  wangjf
	 * @date    2016年1月8日 下午12:01:17
	 * @param params
	 * @return
	 */
	Page<Map<String, Object>> queryAllRepaymentList(Map<String, Object> params);
	
	/**
	 * 还款数据列表（含提前结清应付利息和本金）
	 *
	 * @author  wangjf
	 * @date    2016年1月8日 下午12:01:17
	 * @param params
	 * @return
	 */
	Page<Map<String, Object>> queryAllRepaymentListNew(Map<String, Object> params);
	
	/**
	 * 已投总额、剩余本金总额、本期应还总额、提前结清手续费总额
	 *
	 * @author  wangjf
	 * @date    2016年1月8日 下午12:02:15
	 * @param params
	 * @return
	 */
	Map<String, Object> queryAllRepaymentTotal(Map<String, Object> params);
	
	/**
	 * 查询待还款数据列表
	 *
	 * @author  wangjf
	 * @date    2016年1月11日 下午4:11:33
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> queryWaitingRepaymentList(Map<String, Object> params);
	
	/**
	 * 查询待待垫付还款数据列表
	 *
	 * @author  wangjf
	 * @date    2016年1月11日 下午4:11:33
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> queryWaitingRiskRepaymentList(Map<String, Object> params);
	
	/**
	 * 查询付款列表
	 *
	 * @author  wangjf
	 * @date    2016年1月14日 下午8:08:03
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> queryRepaymentList(Map<String, Object> params);
}
