/** 
 * @(#)AppMarketingActivitiesServiceImpl.java 1.0.0 2015年6月3日 上午11:02:30  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.mobile.impl;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.CustActivityInfoRepositoryCustom;
import com.slfinance.shanlincaifu.service.CustActivityInfoService;
import com.slfinance.shanlincaifu.service.mobile.AppMarketingActivitiesService;
import com.slfinance.shanlincaifu.utils.ArithUtil;
import com.slfinance.shanlincaifu.utils.PageFuns;
import com.slfinance.vo.ResultVo;

/**   
 * 手机端活动中心业务接口实现
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年6月3日 上午11:02:30 $ 
 */
@Service
public class AppMarketingActivitiesServiceImpl implements AppMarketingActivitiesService {

	@Autowired
	private CustActivityInfoService custActivityInfoService;
	
	@Autowired
	private RepositoryUtil repositoryUtil;
	
	@Autowired
	CustActivityInfoRepositoryCustom custActivityInfoRepositoryCustom;
	/**
	 * 活动中心首页展示
	 */
	@Override
	public ResultVo getMarketingActivitiesInfo(Map<String, Object> paramsMap)throws SLException {
		
		Map<String,Object> model = Maps.newHashMap();
		/**获取推荐金额信息 =现金奖励+推荐金额奖励**/
		Map<String,Object> recMap = custActivityInfoService.findRewardById(paramsMap);
		model.put("recommendAmount", ArithUtil.add(((BigDecimal)recMap.get("rewardCount")),custActivityInfoRepositoryCustom.getExpAmountByCustId((String)paramsMap.get("custId"))));
		/**获取体验金额信息**/
		Map<String,Object> expMap =  custActivityInfoService.findExperienceGoldById(paramsMap);
		model.put("experienceAmount", expMap.get("ExperienceGoldCount"));
		
		return new ResultVo(true,"活动中心首页展示",model);
	}

	/**
	 * 我的推荐列表分页
	 */
	@Override
	public ResultVo getRecommendList(Map<String, Object> paramsMap)throws SLException {
		Map<String,Object> model = Maps.newHashMap();
		/**分页开始数当前页数-1转换成索引数**/
		PageFuns.stringParamsConvertInt(PageFuns.pageNumToPageIndex(paramsMap));
		model.put("resList", custActivityInfoService.findCustRecommendList(paramsMap));
		/**已结算金额 、未结算金额**/ 
		Map<String,Object> recMap = custActivityInfoService.findRewardById(paramsMap);
		if (recMap != null) {
			model.put("rewardSettle", recMap.get("rewardSettle"));
			model.put("rewardNotSettle", recMap.get("rewardNotSettle"));
		}
		return new ResultVo(true,"我的推荐列表分页",model);
	}

	/**
	 * 我的体验金信息
	 */
	@Override
	public ResultVo getExperAmountInfo(Map<String, Object> paramsMap)throws SLException {
		return new ResultVo(true,"我的体验金信息",custActivityInfoService.findExperienceGoldById(paramsMap));
	}

	/**
	 * 我的体验金信息-分页列表
	 */
	@Override
	public ResultVo getCustExperienceList(Map<String, Object> paramsMap)throws SLException {
		//分页开始数当前页数-1转换成索引数
		PageFuns.stringParamsConvertInt(PageFuns.pageNumToPageIndex(paramsMap));
		return new ResultVo(true,"我的体验金信息",custActivityInfoService.findCustExperienceList(paramsMap));
	}
	
	/**
	 * 我的体验金信息、分页列表
	 */
	public ResultVo  getExpAmountAndListInfo(Map<String, Object> paramsMap)throws SLException{
		Map<String,Object> model = Maps.newHashMap();
		/**体验金信息**/
		model.put("amountMap", custActivityInfoService.findExperienceGoldById(paramsMap));
		//分页开始数当前页数-1转换成索引数
		PageFuns.stringParamsConvertInt(PageFuns.pageNumToPageIndex(paramsMap));
		/**分页列表**/
		model.put("listMap", custActivityInfoService.findCustExperienceList(paramsMap));
		return new ResultVo(true,"我的体验金信息",model);
	}
	
	/**
	 * 我的推荐列表分页
	 */
	public ResultVo findRecommendList(Map<String, Object> paramsMap)throws SLException {
		Map<String,Object> model = Maps.newHashMap();
		/**分页开始数当前页数-1转换成索引数**/
		PageFuns.stringParamsConvertInt(PageFuns.pageNumToPageIndex(paramsMap));
		Map<String,Object> resList = custActivityInfoService.findCustRecommendList(paramsMap);
		model.put("resList",resList);
		
//		List<Map<String,Object>> resMap = (List<Map<String,Object>>)resList.get("data");
//		if (resMap != null) {
//			/**体验金奖励**/
//			model.put("expAmount", custActivityInfoRepositoryCustom.getExpAmountByCustId((String)paramsMap.get("custId")));
//			/**现金奖励**/
//			Map<String,Object> rewardMap =  custActivityInfoService.findRewardById(paramsMap);
//			model.put("amount", rewardMap != null ? rewardMap.get("rewardCount"):BigDecimal.ZERO); 
//		}
		return new ResultVo(true,"我的推荐列表分页",model);
	}
	
}
