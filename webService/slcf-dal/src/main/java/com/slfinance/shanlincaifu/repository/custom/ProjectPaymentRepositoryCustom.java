/** 
 * @(#)ProjectPaymentRepositoryCustom.java 1.0.0 2016年1月14日 下午8:05:07  
 *  
 * Copyright © 2016 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository.custom;

import java.util.List;
import java.util.Map;

/**   
 * 自定义付款数据访问接口
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2016年1月14日 下午8:05:07 $ 
 */
public interface ProjectPaymentRepositoryCustom {

	/**
	 * 查询付款列表
	 *
	 * @author  wangjf
	 * @date    2016年1月14日 下午8:08:03
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> queryPaymentList(Map<String, Object> params);
	
}
