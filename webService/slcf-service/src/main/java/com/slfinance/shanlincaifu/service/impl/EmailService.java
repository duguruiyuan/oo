/** 
 * @(#)EmailService.java 1.0.0 2015年4月23日 上午10:38:03  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.utils.RestClient;
import com.slfinance.shanlincaifu.utils.RestClientProperties;
import com.slfinance.vo.ResultVo;

/**   
 * 邮件服务业务
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年4月23日 上午10:38:03 $ 
 */
@Service
@Transactional(readOnly=true)
public class EmailService {
	@Autowired
	RestClientProperties restProp;
	
	@Autowired
	private RestClient client;
	
	//发送邮件
	public ResultVo sendEmail( Map<String,Object> smsInfo ) throws SLException {
		return client.postForObject( restProp.getFoundtionClient().getServicePrefix() + "/mail/sendMail", smsInfo, ResultVo.class);
	}
	//批量发送邮件
	public ResultVo sendBatchEmail( List<Map<String,Object>> smsInfoList ) throws SLException {
		for( Map<String,Object> smsInfo : smsInfoList ){
			sendEmail(smsInfo);
		}
		return new ResultVo(true);
	}
	
}
