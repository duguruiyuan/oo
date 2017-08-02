/** 
 * @(#)InvestService.java 1.0.0 2015年4月24日 下午2:11:13  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service;

import java.util.Map;

/**   
 * 
 *  
 * @author  HuYaHui
 * @version $Revision:1.0.0, $Date: 2015年4月24日 下午2:11:13 $ 
 */
public interface RedemptionService {

	//	赎回管理-列表（多表）
	public Map<String, Object> findAtoneListByCondition(Map<String,Object> params);
	//	赎回管理-额度查询（统计）
	public Map<String, Object> countByCondition(Map<String, Object> params);
	//	赎回管理--详情
	public Map<String, Object> findDetailByCondition(Map<String, Object> params);
	
}
