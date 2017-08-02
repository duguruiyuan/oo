/** 
 * @(#)OpenDispatcherService.java 1.0.0 2015年12月19日 上午11:07:48  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service;

import java.util.Map;

/**   
 * 对外服务分发服务类
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年12月19日 上午11:07:48 $ 
 */
public interface OpenDispatcherService {

	/**
	 * 消息处理
	 *
	 * @author  wangjf
	 * @date    2015年12月19日 下午1:31:17
	 * @param params
	 */
	public void handleMessage(Map<String, Object> params);
	
}
