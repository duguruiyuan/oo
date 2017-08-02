/** 
 * @(#)OpenNotifyService.java 1.0.0 2015年7月2日 下午3:22:30  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service;

import com.slfinance.exception.SLException;

/**   
 * 对外服务通知接口
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年7月2日 下午3:22:30 $ 
 */
public interface OpenNotifyService {

	/**
	 * 异步通知（注：该接口目前仅给点赞网使用）
	 *
	 * @author  wangjf
	 * @date    2015年7月2日 下午3:23:51
	 */
	public void	asynNotify() throws SLException;
}
