/** 
 * @(#)SystemMessageServiceImpl.java 1.0.0 2015年4月29日 下午2:42:42  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */

package com.slfinance.shanlincaifu.service.impl;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.CustInfoEntity;
import com.slfinance.shanlincaifu.entity.LogInfoEntity;
import com.slfinance.shanlincaifu.entity.SystemMessageInfoEntity;
import com.slfinance.shanlincaifu.repository.CustInfoRepository;
import com.slfinance.shanlincaifu.repository.LogInfoEntityRepository;
import com.slfinance.shanlincaifu.repository.SystemMessageInfoRepository;
import com.slfinance.shanlincaifu.repository.UserRepository;
import com.slfinance.shanlincaifu.repository.searchFilter.DynamicSpecifications;
import com.slfinance.shanlincaifu.repository.searchFilter.SearchFilter;
import com.slfinance.shanlincaifu.repository.searchFilter.SearchFilter.Operator;
import com.slfinance.shanlincaifu.service.SystemMessageService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.PageFuns;
import com.slfinance.vo.ResultVo;

/**
 * 系统消息业务接口实现
 * 
 * @author zhangzs
 * @version $Revision:1.0.0, $Date: 2015年4月29日 下午2:42:42 $
 */
@Slf4j
@Service
public class SystemMessageServiceImpl implements SystemMessageService {

	@Autowired
	private CustInfoRepository custInfoRepository;
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private SystemMessageInfoRepository messageInfoRepository;
	
	@Autowired
	private LogInfoEntityRepository logInfoRepository;

	@PersistenceContext
	private EntityManager manager;
	
	/**
	 * 站内消息--保存
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo saveSiteMessage(SystemMessageInfoEntity sysMessage, String afficheType)
			throws SLException {
//		List<CustInfoEntity> custList = custInfoRepository.findByIdNotIn(Arrays.asList(Constant.CUST_ADMIN_ID,Constant.CUST_KF_ID,Constant.SYSTEM_USER_BACK,Constant.CUST_ID_CENTER,Constant.CUST_ID_ERAN));
//		if (null == custList || (null != custList && custList.size() < 0))
//			return new ResultVo(false, "没有客户信息");
//		ArrayList<SystemMessageInfoEntity> messageList = new ArrayList<SystemMessageInfoEntity>();
//		for (CustInfoEntity cust : custList) {
//			CustInfoEntity sendCust = new CustInfoEntity();
//			sendCust.setId(Constant.CUST_ADMIN_ID);
//			CustInfoEntity receiveCust = new CustInfoEntity();
//			receiveCust.setId(cust.getId());
//			SystemMessageInfoEntity message = new SystemMessageInfoEntity(sendCust,receiveCust, sysMessage.getSendTitle(),sysMessage.getSendContent(), DateTime.now().toDate(),Constant.SITE_MESSAGE_NOREAD, null,Constant.TRADE_STATUS_01);
//			message.setBasicModelProperty(cust.getId(), true);
//			messageList.add(message);
//		}
//		Iterable<SystemMessageInfoEntity> ite = messageList;
//		messageInfoRepository.save(ite);
		CustInfoEntity sendCust = new CustInfoEntity();
		sendCust.setId(Constant.CUST_ADMIN_ID);
		CustInfoEntity receiveCust = new CustInfoEntity();
		
		// update by lyy @2016/11/14 to add salesMan's Type --Start
// before update		receiveCust.setId("ALL");
		// after update
		if(Constant.AFFICHE_TYPE_SALESMAN.equals(afficheType)){
			receiveCust.setId("salesMan");
		}else {
			receiveCust.setId("ALL");
		}
		// update by lyy @2016/11/14 to add salesMan's Type --End
		
		SystemMessageInfoEntity message = new SystemMessageInfoEntity(sendCust,receiveCust, sysMessage.getSendTitle(),sysMessage.getSendContent(), DateTime.now().toDate(),Constant.SITE_MESSAGE_NOREAD, null,Constant.TRADE_STATUS_01);
		message.setBasicModelProperty(sendCust.getId(), true);
		messageInfoRepository.save(message);
		return new ResultVo(true);
	}

	/**
	 * 客户反馈--查询（分页）
	 * @throws ParseException 
	 */
	@Override
	public Map<String, Object> findFeedback(Map<String, Object> paramsMap)throws SLException {
		PageFuns.pageIndexToPageNum(paramsMap);
		PageRequest pageRequest = new PageRequest((int)paramsMap.get("pageNum"), (int)paramsMap.get("pageSize"),new Sort(Direction.DESC, "createDate"));
		List<SearchFilter> filters = Lists.newArrayList();
		//增加过滤条件,只查询客户反馈的数据信息
		if (StringUtils.isEmpty((String)paramsMap.get("custName")))
			filters.add(new SearchFilter("sendCust.id", Operator.NE,Constant.CUST_ADMIN_ID));
		if (StringUtils.isNotEmpty((String)paramsMap.get("custName")))
			filters.add(new SearchFilter("sendCust.custName", Operator.LIKE, (String)paramsMap.get("custName")));
		if (StringUtils.isNotEmpty((String)paramsMap.get("loginName")))
			filters.add(new SearchFilter("sendCust.loginName", Operator.LIKE, (String)paramsMap.get("loginName")));
		if (StringUtils.isNotEmpty((String)paramsMap.get("recordStatus")))
			filters.add(new SearchFilter("recordStatus", Operator.EQ, (String)paramsMap.get("recordStatus")));
		if (StringUtils.isNotEmpty((String)paramsMap.get("startDate")))
			filters.add(new SearchFilter("createDate", Operator.GTE, DateUtils.parseDate((String)paramsMap.get("startDate") + " 00:00:00", "yyyy-MM-dd HH:mm:ss")));
		if (StringUtils.isNotEmpty((String)paramsMap.get("endDate")))
			filters.add(new SearchFilter("createDate", Operator.LTE, DateUtils.parseDate((String)paramsMap.get("endDate") + " 23:59:59", "yyyy-MM-dd HH:mm:ss")));
		Specification<SystemMessageInfoEntity> spec = DynamicSpecifications.bySearchFilter(filters, SystemMessageInfoEntity.class);
		Page<SystemMessageInfoEntity> page = messageInfoRepository.findAll(spec, pageRequest);
		return PageFuns.pageVoToMap(page);
	}
	
	/**
	 * 客户反馈--查看详情
	 */
	@Override
	public Map<String,Object> findFeedbackDetail(String id)throws SLException {
		Map<String,Object> result = Maps.newHashMap();
		result.put("message", messageInfoRepository.findOne(id));
		List<LogInfoEntity> list = logInfoRepository.findLogInfoEntityByRelatePrimaryOrderByCreateDateDesc(id);
		for( LogInfoEntity log : list ){
			String username = userRepository.findUserNameById(log.getCreateUser());
			if(username != null )
				log.setOperPerson(username);
		}
		result.put("logList",list );
		return result;
	}

	/**
	 * 客户反馈--更新处理结果
	 */
	@Override
	@Transactional(readOnly = false,rollbackFor=SLException.class)
	public ResultVo updateFeedback(Map<String, Object> paramsMap)throws SLException {
		//查询客户反馈
		SystemMessageInfoEntity message = messageInfoRepository.findOne((String)paramsMap.get("id"));
		if(null == message)
			throw new SLException("客户反馈信息不存在");
		
		String  userName = userRepository.findUserNameById((String)paramsMap.get("custId"));
		StringBuilder operDesc = new StringBuilder("用户：").append(null != userName ? userName:(String)paramsMap.get("custId")).append("，做").append(Constant.OPERATION_TYPE_17).append("操作");
		LogInfoEntity logInfo = new LogInfoEntity(Constant.TABLE_BAO_T_SYSTEM_MESSAGE_INFO, (String)paramsMap.get("id"), Constant.OPERATION_TYPE_17, message.getRecordStatus(), (String)paramsMap.get("result"), operDesc.toString(),message.getSendCust().getId() );
		
		SystemMessageInfoEntity messageUpd = new SystemMessageInfoEntity();
		messageUpd.setRecordStatus((String)paramsMap.get("result"));
		messageUpd.setBasicModelProperty((String)paramsMap.get("custId"), false);
		if(!message.updateMessage(messageUpd))
			throw new SLException("客户反馈处理错误");
		
		logInfo.setBasicModelProperty((String)paramsMap.get("custId"), true);
		logInfo.setMemo((String)paramsMap.get("memo"));
		logInfo.setOperIpaddress((String)paramsMap.get("ipAddress"));
		logInfoRepository.save(logInfo);
		
		return new ResultVo(true);
	}
	
	@Transactional(readOnly = false,rollbackFor=SLException.class)
	public void sendSystemMessage(Map<String, Object> paramsMap){
		
		String custId = (String)paramsMap.get("custId");
		CustInfoEntity custInfoEntity = custInfoRepository.findOne(custId);
		if(custInfoEntity == null) {
			log.warn("发送站内信失败，用户{}不存在", custId);
			return;
		}
		
		// 发系统站内信
		CustInfoEntity systemEntity = custInfoRepository.findOne(Constant.CUST_ADMIN_ID);
		SystemMessageInfoEntity systemMessageInfoEntity = new SystemMessageInfoEntity();
		systemMessageInfoEntity.setSendCust(systemEntity);
		systemMessageInfoEntity.setReceiveCust(custInfoEntity);
		systemMessageInfoEntity.setSendTitle((String)paramsMap.get("title"));
		systemMessageInfoEntity.setSendContent((String)paramsMap.get("content"));
		systemMessageInfoEntity.setSendDate(new Date());
		systemMessageInfoEntity.setIsRead(Constant.SITE_MESSAGE_NOREAD);
		systemMessageInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
		messageInfoRepository.save(systemMessageInfoEntity);
	}
}
