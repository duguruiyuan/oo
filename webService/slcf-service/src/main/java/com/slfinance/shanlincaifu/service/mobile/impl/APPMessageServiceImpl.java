/** 
 * @(#)APPMessageServiceImpl.java 1.0.0 2015年6月2日 下午1:44:58  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.mobile.impl;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.CustInfoEntity;
import com.slfinance.shanlincaifu.entity.SystemMessageInfoEntity;
import com.slfinance.shanlincaifu.repository.SystemMessageInfoRepository;
import com.slfinance.shanlincaifu.service.AfficheInfoService;
import com.slfinance.shanlincaifu.service.SystemMessageService;
import com.slfinance.shanlincaifu.service.mobile.APPMessageService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.PageFuns;
import com.slfinance.vo.ResultVo;

/**   
 * APP手机端消息管理业务接口
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年6月2日 下午1:44:58 $ 
 */
@Service
public class APPMessageServiceImpl implements APPMessageService {

	@Autowired
	private AfficheInfoService afficheInfoService;
	
	@Autowired
	private SystemMessageService systemMessageService;
	
	@Autowired
	private SystemMessageInfoRepository messageInfoRepository;
	
	/**
	 * 客户反馈信息保存
	 * @throws SLException 
	 */
	@Override
	public ResultVo saveFeedback(Map<String, Object> paramsMap) throws SLException {
		SystemMessageInfoEntity sysMessage = new SystemMessageInfoEntity(new CustInfoEntity((String)paramsMap.get("custId"), new Date()), new CustInfoEntity(Constant.CUST_KF_ID, new Date()), Constant.SUGGESTION_TYPE_FEEDBACK, (String)paramsMap.get("sendContent"), new Date(), Constant.SITE_MESSAGE_NOREAD, null, Constant.TRADE_STATUS_01);
		sysMessage.setBasicModelProperty((String)paramsMap.get("custId"), true);
		return new ResultVo(true,"保存成功",messageInfoRepository.save(sysMessage));
	}

	/**
	 * 查询最新网站公告
	 * @throws SLException 
	 */
	@Override
	public ResultVo findLatestMessage() throws SLException {
		return new ResultVo(true,"最新网站公告",afficheInfoService.findNewestWebsiteAffiche());
	}

	/**
	 * 查询网站公告
	 */
	@Override
	public ResultVo findMessagePage(Map<String,Object> paramsMap) throws SLException{
		paramsMap.put("afficheType", Constant.AFFICHE_TYPE_ALL); 	
		paramsMap.put("afficheStatus", Constant.AFFICHE_STATUS_PUBLISHED);
		PageFuns.pageNumToPageIndex(paramsMap);
		paramsMap.put("start", Integer.parseInt((String)paramsMap.get("start")));
		paramsMap.put("length", Integer.parseInt((String)paramsMap.get("length")));
		Map<String, Object> result = afficheInfoService.findAllAffiche(paramsMap);
		result.putAll(afficheInfoService.countAffiche(paramsMap));
		return new ResultVo(true,"查询网站公告分页列表", result);
	}

}
