/** 
 * @(#)AccountService.java 1.0.0 2015年4月21日 上午11:24:11  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.impl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.CustInfoEntity;
import com.slfinance.shanlincaifu.entity.SystemMessageInfoEntity;
import com.slfinance.shanlincaifu.entity.SystemMessageReadInfoEntity;
import com.slfinance.shanlincaifu.repository.CustInfoRepository;
import com.slfinance.shanlincaifu.repository.SystemMessageInfoRepository;
import com.slfinance.shanlincaifu.repository.SystemMessageReadInfoRepository;
import com.slfinance.shanlincaifu.service.MessageService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.vo.ResultVo;

/**   
 * 
 *  
 * @author  HuangXiaodong
 * @version $Revision:1.0.0, $Date: 2015年4月21日 上午11:24:11 $ 
 */
@Slf4j
@Service("messageService")
@Transactional(readOnly = true)
public class MessageServiceImpl implements MessageService{

	private final SystemMessageInfoRepository systemMessageInfoRepository;
	
	private final CustInfoRepository custInfoRepository;
	
	private final SystemMessageReadInfoRepository systemMessageReadInfoRepository;
	
	@Autowired
	public MessageServiceImpl(SystemMessageInfoRepository message, CustInfoRepository cust, SystemMessageReadInfoRepository messageReadInfo) {
		systemMessageInfoRepository = message;
		custInfoRepository = cust;
		systemMessageReadInfoRepository = messageReadInfo;
	}
	
	@Override
	@Transactional
	public ResultVo unReadMessage(Map<String, Object> param) throws SLException {
		log.debug("Entering unReadMessage -----");
		int count = 0;
		Object custIds = param.get("custId");
		if(custIds != null) {
			String[] ids = (String[]) custIds;
			count = systemMessageInfoRepository.unreadMessageCount(ids[0]);
		}
		
		return new ResultVo(true, "操作成功", count);
	}

	@Override
	@Transactional
	public ResultVo feedback(Map<String, Object> param) throws SLException {
		log.debug("Entering feedback -----");
		String custId = param.get("custId") + "";
		SystemMessageInfoEntity msg = new SystemMessageInfoEntity();
		CustInfoEntity kf = custInfoRepository.findOne(Constant.CUST_KF_ID);
		msg.setReceiveCust(kf);
		
		CustInfoEntity cust = custInfoRepository.findOne(custId);
		msg.setSendCust(cust);
		
		msg.setSendContent(param.get("suggestionContent") + "");
		msg.setSendTitle(param.get("suggestionType") + "");
		msg.setRecordStatus(param.get("recordStatus") + "");
		msg.setSendDate(new Timestamp(System.currentTimeMillis()));
		msg.setCreateDate(new Date());
		systemMessageInfoRepository.save(msg);
		return new ResultVo(true, "操作成功");
	}

	@Override
	@Transactional
	public ResultVo updateStatus(Map<String, Object> param) throws SLException {
		String custId = param.get("custId") + "";
		String messageId = param.get("messageId") + "";
		SystemMessageReadInfoEntity smri = systemMessageReadInfoRepository.findOneByMessageIdAndCustId(messageId,custId);
		if(smri!=null){
			return new ResultVo(false,"已经为'已读'状态", 2);
		}
		
		SystemMessageReadInfoEntity smri2 = new SystemMessageReadInfoEntity();
		CustInfoEntity receiveCust = new CustInfoEntity();
		receiveCust.setId(custId);
		SystemMessageInfoEntity message = new SystemMessageInfoEntity();
		message.setId(messageId);
		smri2.setReceiveCust(receiveCust);
		smri2.setMessage(message);
		smri2.setIsRead(Constant.SITE_MESSAGE_ISREAD);
		smri2.setCreateDate(new Timestamp(System.currentTimeMillis()));
		smri2.setCreateUser(custId);
		systemMessageReadInfoRepository.save(smri2);
		return new ResultVo(true, "操作成功", 1);
	}
}
