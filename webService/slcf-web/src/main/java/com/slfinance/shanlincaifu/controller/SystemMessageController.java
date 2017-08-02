/** 
 * @(#)SystemMessageController.java 1.0.0 2015年5月5日 上午9:19:49  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.SystemMessageService;
import com.slfinance.vo.ResultVo;

/**   
 * 系统消息业务控制器
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年5月5日 上午9:19:49 $ 
 */
@Controller
@RequestMapping(value="/systemMessage", produces="application/json;charset=UTF-8")
public class SystemMessageController {
	
	@Autowired
	private SystemMessageService systemMessageService;
	
	/**
	 * 客户反馈--查询（分页）
	 */
	@RequestMapping(value="/findFeedback", method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> findFeedback(@RequestBody Map<String,Object> paramsMap) throws SLException{
		try {
			return systemMessageService.findFeedback(paramsMap);
		} catch (Exception e) {
			throw new SLException(e.getMessage());
		}
	}
	
	/**
	 * 客户反馈--查看详情
	 */
	@RequestMapping(value="/findFeedbackDetail", method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> findFeedbackDetail(@RequestBody Map<String,Object> paramsMap) throws SLException{
		try {
			return systemMessageService.findFeedbackDetail((String)paramsMap.get("id"));
		} catch (Exception e) {
			throw new SLException(e.getMessage());
		}
	}
	
	/**
	 * 客户反馈--更新处理结果
	 */
	@RequestMapping(value="/updateFeedback", method = RequestMethod.POST)
	public @ResponseBody ResultVo updateFeedback(@RequestBody Map<String,Object> paramsMap) throws SLException{
		try {
			return systemMessageService.updateFeedback(paramsMap);
		} catch (Exception e) {
			throw new SLException(e.getMessage());
		}
	}
	
}
