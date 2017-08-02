/** 
 * @(#)AccessService.java 1.0.0 2015年11月3日 上午10:09:01  
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
 * 用户访问服务
 *  
 * 本接口服务主要用户记录APP情况，便于后期统计设备激活情况
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年11月3日 上午10:09:01 $ 
 */
public interface AccessService {

	/**
	 * 保存访问信息
	 *
	 * @author  wangjf
	 * @date    2015年11月3日 上午11:20:59
	 * @param params
	 * 		<tt>meId： String:设备ID</tt><br>
	 * 		<tt>meVersion： String:设备版本号</tt><br>
	 * 		<tt>appSource： String:来源（android，ios等）</tt><br>
	 * 		<tt>channelNo： String:渠道编号</tt><br>
	 * @return
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "meId", required = true, requiredMessage = "设备ID不能为空!"),
			@Rule(name = "meVersion", required = true, requiredMessage = "设备版本号不能为空!"),
			@Rule(name = "appSource", required = true, requiredMessage = "设备来源不能为空!"),	
			@Rule(name = "channelNo", required = true, requiredMessage = "渠道来源不能为空!")
	})
	public ResultVo saveAccess(Map<String, Object> params) throws SLException;
	
	/**
	 * 检查应用版本号
	 *
	 * @author  wangjf
	 * @date    2015年11月18日 下午6:59:14
	 * @param params
	 * 		<tt>appSource： String:来源（android，ios等）</tt><br>
	 * 		<tt>appVersion： String:app版本号</tt><br>
	 * @return
	 * @throws SLException
	 */
	public ResultVo checkAppVersion(Map<String, Object> params) throws SLException;
	
	/**
	 * 判断AppStore中版本号与所传保本号是否一致
	 *
	 * @author  wangjf
	 * @date    2015年12月18日 上午9:48:42
	 * @param params
	 * 		<tt>appVersion： String:app版本号</tt><br>
	 * @return
	 * @throws SLException
	 */
	public ResultVo judgeVersion(Map<String, Object> params) throws SLException;
}
