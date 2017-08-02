/** 
 * @(#)AppMarketingActivitiesController.java 1.0.0 2015年6月3日 下午12:00:11  
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
import com.slfinance.shanlincaifu.service.mobile.AppMarketingActivitiesService;
import com.slfinance.vo.ResultVo;

/**   
 * 手机端活动中心业务接口控制器
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年6月3日 下午12:00:11 $ 
 */
@Controller
@RequestMapping(value="/activity", produces="application/json;charset=UTF-8")
public class AppMarketingActivitiesController {
	
	@Autowired
	private AppMarketingActivitiesService appMarketingActivitiesService;
	
	/**
	 * 活动中心首页展示
	 */
	@RequestMapping(value="/marActInfo", method = RequestMethod.POST)
	public @ResponseBody ResultVo getMarketingActivitiesInfo(@RequestBody Map<String, Object> paramsMap)throws SLException{
		return appMarketingActivitiesService.getMarketingActivitiesInfo(paramsMap);
	}
	
	/**
	 * 我的推荐列表分页
	 */
	@RequestMapping(value="/recList", method = RequestMethod.POST)
	public  @ResponseBody ResultVo getRecommendList(@RequestBody Map<String, Object> paramsMap)throws SLException{
		return appMarketingActivitiesService.getRecommendList(paramsMap);
	}
	
	/**
	 * 我的推荐列表分页
	 */
	@RequestMapping(value="/recommendList", method = RequestMethod.POST)
	public  @ResponseBody ResultVo findRecommendList(@RequestBody Map<String, Object> paramsMap)throws SLException{
		return appMarketingActivitiesService.findRecommendList(paramsMap);
	}
	
	/**
	 * 我的体验金信息
	 */
	@RequestMapping(value="/expAmount", method = RequestMethod.POST)
	public @ResponseBody ResultVo getExperAmountInfo(@RequestBody Map<String, Object> paramsMap)throws SLException{
		return appMarketingActivitiesService.getExperAmountInfo(paramsMap);
	}
	
	/**
	 * 我的体验金信息-分页列表
	 */
	@RequestMapping(value="/expList", method = RequestMethod.POST)
	public @ResponseBody ResultVo getCustExperienceList(@RequestBody Map<String, Object> paramsMap)throws SLException{
		return appMarketingActivitiesService.getCustExperienceList(paramsMap);
	}
	
	/**
	 * 我的体验金信息、分页列表
	 */
	@RequestMapping(value="/expAmountAndList", method = RequestMethod.POST)
	public @ResponseBody ResultVo getExpAmountAndListInfo(@RequestBody Map<String, Object> paramsMap)throws SLException{
		return appMarketingActivitiesService.getExpAmountAndListInfo(paramsMap);
	}
	

}
