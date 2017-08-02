/** 
 * @(#)SystemMessageServiceTes.java 1.0.0 2015年4月29日 下午4:22:34  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.AbstractSpringContextTestSupport;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.entity.SystemMessageInfoEntity;
import com.slfinance.shanlincaifu.repository.AccountFlowInfoRepository;
import com.slfinance.shanlincaifu.repository.SystemMessageInfoRepository;
import com.slfinance.shanlincaifu.repository.TradeFlowInfoRepository;

/**   
 * 系统消息业务测试
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年4月29日 下午4:22:34 $ 
 */
@Slf4j
public class SystemMessageServiceTest extends AbstractSpringContextTestSupport {

	@Autowired
	private SystemMessageService systemMessageService;
	
	@Autowired
	private SystemMessageInfoRepository messageInfoRepository;
	
	@Autowired
	private TradeFlowInfoRepository tradeFlowInfoRepository;
	
	@Autowired
	private AccountFlowInfoRepository accountFlowInfoRepository;
	
	Map<String, Object> paramsMap = Maps.newHashMap();
	Map<String, Object> resultMap = Maps.newHashMap();
	
	/**
	 * 站内消息--保存
	 */
	@Test
	public void testSaveSiteMessage() throws SLException{
		SystemMessageInfoEntity sysMessage = new SystemMessageInfoEntity();
		sysMessage.setSendTitle("1231231222222222222");
		sysMessage.setSendContent("1231231222222222222");
		systemMessageService.saveSiteMessage(sysMessage, Constant.AFFICHE_TYPE_NOTICE);
	}
	
	/**
	 * 客户反馈--查询（分页）
	 */
	@Test
	public void testFindFeedback() throws SLException{
		paramsMap.clear();
		paramsMap.put("pageNum", 0);
		paramsMap.put("pageSize", 10);
		paramsMap.put("custName", "zhangzhisheng001");
		paramsMap.put("startDate", "2011-09-09");
		paramsMap.put("endDate", "2016-09-09");
		paramsMap.put("recordStatus", "未处理");
		resultMap.clear();
		resultMap = systemMessageService.findFeedback(paramsMap);
	}
	
	/**
	 * 客户反馈--查看详情
	 */
	@Test
	public void findFeedbackDetail() throws SLException{
		resultMap.clear();
		resultMap = systemMessageService.findFeedbackDetail("003");
		SystemMessageInfoEntity message = (SystemMessageInfoEntity)resultMap.get("message");
		log.info(message.getReceiveCust().getCustName());
	}
	
	/**
	 * 客户反馈--更新处理结果
	 */
	@Test
	public void testUpdateFeedback() throws SLException{
		paramsMap.clear();
		paramsMap.put("id", "154bd988-ff16-49f9-9253-6c4e2c588f98");
		paramsMap.put("custId", "zhangzs");
		paramsMap.put("memo", "更新消息");
		paramsMap.put("result", "通过了");
		systemMessageService.updateFeedback(paramsMap);
	}
	
}
