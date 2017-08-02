/** 
 * @(#)AppVersionCheckService.java 1.0.0 2015年11月27日 下午19:13:05  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service;

import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;

/**   
 * App版本检查服务接口
 *  
 * @author  gaoll
 * @version $Revision:1.0.0, $Date: 2015年11月27日 下午19:13:05 $ 
 */
public interface AppVersionCheckService {

	/**
	 * 查询App版本信息
	 *
	 * @author 	gaoll
	 * @date 	2015年11月27日 下午19:13:05
	 * @param params
	 * @return
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "version", required = true, requiredMessage = "版本号不能为空"),
			@Rule(name = "appSource", required = true, requiredMessage = "应用来源不能为空", inRange = { "android", "ios"}, inRangeMessage = "APP类型不在区间内")
	})
	public ResultVo checkAPPVersion(Map<String, Object> params) throws SLException;
}
