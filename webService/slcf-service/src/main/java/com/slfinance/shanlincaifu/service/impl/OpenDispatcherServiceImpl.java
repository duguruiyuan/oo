/** 
 * @(#)OpenDispatcherServiceImpl.java 1.0.0 2015年12月19日 上午11:08:32  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.impl;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.slfinance.shanlincaifu.service.OpenDispatcherService;
import com.slfinance.shanlincaifu.service.OpenService;
import com.slfinance.shanlincaifu.utils.Constant;

/**   
 * 对外服务分发实现类
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年12月19日 上午11:08:32 $ 
 */
@Slf4j
@Service("openDispatcherService")
public class OpenDispatcherServiceImpl implements OpenDispatcherService {

	@Autowired
	private OpenService openService;
	
	@Override
	//@RabbitListener(queues = Constant.AMQP_QUEUE_OPENSERVICE)
	public void handleMessage(Map<String, Object> params) {
		try
		{
			String openServiceType = (String)params.get("openServiceType");
			switch(Strings.nullToEmpty(openServiceType)) {
			case Constant.OPERATION_TYPE_24:
				openService.saveDownloadMessage(params);
				break;
			default:
				log.warn("无法识别业务类型{}", openServiceType);
				break;
			}
		}
		catch(Exception e) {
			log.error("处理第三方业务失败！" + e.getMessage());
		}
		
	}

}
