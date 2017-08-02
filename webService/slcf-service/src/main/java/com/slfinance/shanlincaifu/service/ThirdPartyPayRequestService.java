/** 
 * @(#)ThirdPartyPayRequestService.java 1.0.0 2015年4月28日 下午2:51:13  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.ThridPartyPayRequestEntity;

/**   
 * 第三方请求报文日志接口
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年4月28日 下午2:51:13 $ 
 */
public interface ThirdPartyPayRequestService {
	
	/**
	 * 保存请求报文
	 *
	 * @author  wangjf
	 * @date    2015年4月28日 下午2:54:45
	 * @param thirdPartyPayRequest
	 * @throws SLException
	 */
	public void saveThirdPartyPayRequest(final ThridPartyPayRequestEntity thirdPartyPayRequest) throws SLException;
}
