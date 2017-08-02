/** 
 * @(#)APPMessageController.java 1.0.0 2015年6月2日 下午2:05:30  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.controller.mobile;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.mobile.APPMessageService;
import com.slfinance.vo.ResultVo;

/**   
 * APP手机端消息管理业务控制器
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年6月2日 下午2:05:30 $ 
 */
@Controller
@RequestMapping(value="/message", produces="application/json;charset=UTF-8")
public class APPMessageController {
	
	@Autowired
	private APPMessageService appMessageService;

	/**
	 * 客户反馈信息保存
	 */
	@RequestMapping(value="/saveFeedback", method = RequestMethod.POST)
	public @ResponseBody ResultVo saveFeedback(@RequestBody Map<String,Object> paramsMap) throws SLException{
		return appMessageService.saveFeedback(paramsMap);
	}
	
	/**
	 * 查询最新网站公告
	 */
	@RequestMapping(value="/findLatestMessage", method = RequestMethod.POST)
	public @ResponseBody ResultVo findLatestMessage() throws SLException{
		return appMessageService.findLatestMessage();
	}
	
	/**
	 * 查询网站公告
	 */
	@RequestMapping(value="/findMessagePage", method = RequestMethod.POST)
	public @ResponseBody ResultVo findMessagePage(@RequestBody Map<String,Object> paramsMap) throws SLException{
		return appMessageService.findMessagePage(paramsMap);
	}
	
}
