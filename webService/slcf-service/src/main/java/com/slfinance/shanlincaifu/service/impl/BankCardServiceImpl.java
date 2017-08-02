/** 
 * @(#)BankCardServiceImpl.java 1.0.0 2015年4月21日 上午11:24:11  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AttachmentInfoEntity;
import com.slfinance.shanlincaifu.entity.AuditInfoEntity;
import com.slfinance.shanlincaifu.entity.BankCardInfoEntity;
import com.slfinance.shanlincaifu.entity.CustInfoEntity;
import com.slfinance.shanlincaifu.entity.LogInfoEntity;
import com.slfinance.shanlincaifu.entity.SystemMessageInfoEntity;
import com.slfinance.shanlincaifu.entity.TradeFlowInfoEntity;
import com.slfinance.shanlincaifu.entity.UnbindInfoEntity;
import com.slfinance.shanlincaifu.repository.AuditInfoRespository;
import com.slfinance.shanlincaifu.repository.BankCardInfoRepository;
import com.slfinance.shanlincaifu.repository.CustApplyInfoRepository;
import com.slfinance.shanlincaifu.repository.CustInfoRepository;
import com.slfinance.shanlincaifu.repository.CustRecommendInfoRepository;
import com.slfinance.shanlincaifu.repository.LogInfoEntityRepository;
import com.slfinance.shanlincaifu.repository.SystemMessageInfoRepository;
import com.slfinance.shanlincaifu.repository.TradeFlowInfoRepository;
import com.slfinance.shanlincaifu.repository.UnbindInfoRepository;
import com.slfinance.shanlincaifu.repository.custom.AttachmentRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.BankCardInfoRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.ParamRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.UnbindInfoRepositoryCustom;
import com.slfinance.shanlincaifu.service.BankCardService;
import com.slfinance.shanlincaifu.service.CustomerService;
import com.slfinance.shanlincaifu.service.FlowNumberService;
import com.slfinance.shanlincaifu.service.OpenThirdService;
import com.slfinance.shanlincaifu.service.SMSService;
import com.slfinance.shanlincaifu.service.ThirdPartyPayService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.SubjectConstant;
import com.slfinance.vo.ResultVo;

/**   
 * 银行服务接口实现类
 *  
 * @author  HuangXiaodong
 * @version $Revision:1.0.0, $Date: 2015年4月21日 上午11:24:11 $ 
 */
@Slf4j
@Service("bankCardService")
public class BankCardServiceImpl implements BankCardService{
	
	@Autowired
	private  BankCardInfoRepository bankCardInfoRepository;
	@Autowired
	private  CustInfoRepository custInfoRepository;
	@Autowired
	private  ThirdPartyPayService thirdPartyPayService;
	@Autowired
	private  BankCardInfoRepositoryCustom bankCardInfoRepositoryCustom;
	@Autowired
	private  AttachmentRepositoryCustom attachmentRepositoryCustom;
	@Autowired
	private  AuditInfoRespository auditInfoRespository;
	@Autowired
	private  UnbindInfoRepository unbindInfoRepository;
	@Autowired
	private  LogInfoEntityRepository logInfoEntityRepository;
	@Autowired
	private  UnbindInfoRepositoryCustom unbindInfoRepositoryCustom;
	
	//private  SystemMessageInfoRepository systemMessageInfoRepository;
	@Autowired
	private  SMSService smsService;
	@Autowired
	private  TradeFlowInfoRepository tradeFlowInfoRepository;
	@Autowired
	private  FlowNumberService numberService;
	@Autowired
	private  ParamRepositoryCustom paramRepositoryCustom;
	
	@Autowired
	private OpenThirdService openThirdService;
	
	@Autowired
	private CustomerService customerService;
	
/*	@Autowired
	public BankCardServiceImpl(BankCardInfoRepository bank, CustInfoRepository cust, 
			ThirdPartyPayService thirdPartyPayService, BankCardInfoRepositoryCustom bankCardInfoRepositoryCustom,
			AttachmentRepositoryCustom attachmentRepositoryCustom, AuditInfoRespository auditInfoRespository,
			UnbindInfoRepository unbindInfoRepository, LogInfoEntityRepository logInfoEntityRepository,
			UnbindInfoRepositoryCustom unbindInfoRepositoryCustom, //SystemMessageInfoRepository systemMessageInfoRepository,
			SMSService smsService, TradeFlowInfoRepository tradeFlowInfoRepository,
			FlowNumberService numberService, ParamRepositoryCustom paramRepositoryCustom) {
		bankCardInfoRepository = bank;
		custInfoRepository = cust;
		this.thirdPartyPayService = thirdPartyPayService;
		this.bankCardInfoRepositoryCustom = bankCardInfoRepositoryCustom;
		this.attachmentRepositoryCustom = attachmentRepositoryCustom;
		this.auditInfoRespository = auditInfoRespository;
		this.unbindInfoRepository = unbindInfoRepository;
		this.logInfoEntityRepository = logInfoEntityRepository;
		this.unbindInfoRepositoryCustom = unbindInfoRepositoryCustom;
		//this.systemMessageInfoRepository = systemMessageInfoRepository;
		this.smsService = smsService;
		this.tradeFlowInfoRepository = tradeFlowInfoRepository;
		this.numberService = numberService;
		this.paramRepositoryCustom = paramRepositoryCustom;
	}*/
	
	public ResultVo setDefault(Map<String, Object> param ) {
		return new ResultVo(true);
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo createBank(Map<String, Object> param ) throws SLException {
		BankCardInfoEntity bankCard = new BankCardInfoEntity();
		bankCard.setCardNo("1234qwer");
		Map<String, Object> cust = (Map<String, Object>) param.get("custInfoEntity");
		log.debug("进入创建银行卡服务");
		CustInfoEntity custInfo = custInfoRepository.findOne(cust.get("id").toString());
		bankCard.setCustInfoEntity(custInfo);
		bankCardInfoRepository.save(bankCard);
		return new ResultVo(true);
	}
	
	@Override
	public Map<String, Object> queryBankCard(Map<String, Object> param)
			throws SLException {
		Map<String, Object> result = new HashMap<String, Object>();
		List<BankCardInfoEntity> list = bankCardInfoRepository.findByCustIdOrderByCreateDateDesc((String)param.get("custId"));
		
		// add by liyy @2016-03-23
		boolean onLineCardIsExist = false;
		
		BankCardInfoEntity cardInfoEntity = null;
		if(list != null && list.size() > 0){
			for(BankCardInfoEntity bankCardInfoEntity : list){
				if(Constant.VALID_STATUS_VALID.equals(bankCardInfoEntity.getRecordStatus())){
					if(cardInfoEntity == null) { // 取第一个有效的值
						cardInfoEntity = bankCardInfoEntity;
					}
					if(!StringUtils.isEmpty((String)param.get("cardNo"))) { // 卡号非空，需取对应卡号
						if(!bankCardInfoEntity.getCardNo().equals((String)param.get("cardNo"))) {
							continue;
						}
					}
					result.put("id", bankCardInfoEntity.getId());
					result.put("custId", bankCardInfoEntity.getCustInfoEntity().getId());
					result.put("bankName", bankCardInfoEntity.getBankName());
					result.put("cardNo", bankCardInfoEntity.getCardNo());
					result.put("protocolNo", bankCardInfoEntity.getProtocolNo());
					result.put("openProvince", bankCardInfoEntity.getOpenProvince());
					result.put("openCity", bankCardInfoEntity.getOpenCity());
					result.put("subBranchName", bankCardInfoEntity.getSubBranchName());
					result.put("bankCode", bankCardInfoRepositoryCustom.findByBankName(bankCardInfoEntity.getBankName()));
					onLineCardIsExist = true;
					break;
				}
			}
		}
		
		if(!onLineCardIsExist && cardInfoEntity != null) { // 未找到银行卡
			result.put("id", cardInfoEntity.getId());
			result.put("custId", cardInfoEntity.getCustInfoEntity().getId());
			result.put("bankName", cardInfoEntity.getBankName());
			result.put("cardNo", cardInfoEntity.getCardNo());
			result.put("protocolNo", cardInfoEntity.getProtocolNo());
			result.put("openProvince", cardInfoEntity.getOpenProvince());
			result.put("openCity", cardInfoEntity.getOpenCity());
			result.put("subBranchName", cardInfoEntity.getSubBranchName());
			result.put("bankCode", bankCardInfoRepositoryCustom.findByBankName(cardInfoEntity.getBankName()));
		}
		
		if(!StringUtils.isEmpty(param.get("bankFlag"))){
			onLineCardIsExist = true;
		}
		// add by liyy @2016-03-23 Start
		// 如果上面没有银行卡
		// 线下银行卡
		if(!onLineCardIsExist){
			List<BankCardInfoEntity> listForOffLine = bankCardInfoRepository.findOffCardByCustIdOrderByCreateDateDesc((String)param.get("custId"));
			if(listForOffLine != null && listForOffLine.size() > 0){
				BankCardInfoEntity bankCardInfoEntity = listForOffLine.get(0);
				result.put("id", bankCardInfoEntity.getId());
				result.put("custId", bankCardInfoEntity.getCustInfoEntity().getId());
				result.put("bankName", bankCardInfoEntity.getBankName());
				result.put("cardNo", bankCardInfoEntity.getCardNo());
				result.put("protocolNo", bankCardInfoEntity.getProtocolNo());
				result.put("openProvince", bankCardInfoEntity.getOpenProvince());
				result.put("openCity", bankCardInfoEntity.getOpenCity());
				result.put("subBranchName", bankCardInfoEntity.getSubBranchName());
				result.put("bankCode", bankCardInfoRepositoryCustom.findByBankName(bankCardInfoEntity.getBankName()));
			}
		}
		// add by liyy @2016-03-23 End
		return result;
	}
	
	@Override
	public Map<String, Object> querySupportBank(Map<String, Object> param) throws SLException {
		
		return thirdPartyPayService.querySupportBank();
	}
	
	@Override
	public Map<String, Object> queryThirdBankByCardNo(
			Map<String, Object> paramsMap) throws SLException {
		
		return thirdPartyPayService.queryThirdBankByCardNo(paramsMap);
	}
	
	@Override
	public Map<String, Object> queryBankCardDetail(Map<String, Object> param)
			throws SLException {
		return bankCardInfoRepositoryCustom.findByCustId((String)param.get("custId"));
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo unBindBankCard(Map<String, Object> param)
			throws SLException {
		
		String custId = (String)param.get("custId");
		String bankId = (String)param.get("bankId");
		String tradePassword = (String)param.get("tradePassword");
		String unbindType = (String)param.get("unbindType");
		String unbindReason = (String)param.get("unbindReason");
		if(!param.containsKey("attachmentList")) {
			return new ResultVo(false, "缺少附件");
		}
		List<Map<String, String>> attachmentList = (List<Map<String, String>>)param.get("attachmentList");
		
		// 验证客户有效性
		CustInfoEntity custInfoEntity = custInfoRepository.findOne(custId);
		if(custInfoEntity == null) {
			return new ResultVo(false, "客户不存在");
		}
		
		if(!tradePassword.equals(custInfoEntity.getTradePassword())) {
			return new ResultVo(false, "交易密码不正确");
		}
		
		// 验证银行卡有效性
		BankCardInfoEntity bankCardInfoEntity = bankCardInfoRepository.findOne(bankId);
		if (bankCardInfoEntity == null) {
			return new ResultVo(false, "客户银行卡不存在");
		} else if (!bankCardInfoEntity.getCustInfoEntity().getId().equals(custId)) {
			return new ResultVo(false, "银行卡和客户不匹配");
		}
		
		if(!Constant.VALID_STATUS_VALID.equals(bankCardInfoEntity.getRecordStatus())){
			return new ResultVo(false, "该银行卡未绑定成功或者已经解绑");
		}
		
		// 验证是否重复提交
		List<UnbindInfoEntity> unbindList = unbindInfoRepository.findByRelatePrimaryAndUnbindStatus(bankId, Constant.TRADE_STATUS_02);
		if(unbindList != null && unbindList.size() > 0) {
			return new ResultVo(false, "您已经为该卡提交了解绑申请，请勿重复提交");
		}
		
		// 判断是否存在提现申请未处理
		List<TradeFlowInfoEntity> tradeFlowList = tradeFlowInfoRepository.findWithDraw(custId, SubjectConstant.TRADE_FLOW_TYPE_WITHDRAW, Constant.AUDIT_STATUS_REVIEWD);
		if(tradeFlowList != null && tradeFlowList.size() > 0) {		
			for(TradeFlowInfoEntity t : tradeFlowList) {
				if(t.getBankCardNo().equals(bankCardInfoEntity.getCardNo())) { // 提现申请中包含正在解绑的银行卡则不允许解绑
					return new ResultVo(false, "您存在提现申请未处理，暂时不能解绑银行卡");
				}
			}			
		}
		
		// 验证附件是否符合要求
		List<String> attachmentTypeList = new ArrayList<String>();
		for(Map<String, String> m : attachmentList) { 
			attachmentTypeList.add((String)m.get("attachmentType"));
		}
		if(Constant.UNBIND_CARD_TYPE_LOST.equals(unbindType)) { // 已丢失
			// 附件需同时包含银行挂失证明、手持身份证正面、手持身份证反面
			if (!attachmentTypeList.contains(Constant.UNBIND_CARD_ATTACHMENT_TYPE_BANK_LOST)
					|| !attachmentTypeList.contains(Constant.UNBIND_CARD_ATTACHMENT_TYPE_PAPER_BEFORE)
					|| !attachmentTypeList.contains(Constant.UNBIND_CARD_ATTACHMENT_TYPE_PAPER_BEHIND)) {
				return new ResultVo(false, String.format("%s解绑必须同时包含以下附件：%s,%s,%s", 
						Constant.UNBIND_CARD_TYPE_LOST,
						Constant.UNBIND_CARD_ATTACHMENT_TYPE_BANK_LOST,
						Constant.UNBIND_CARD_ATTACHMENT_TYPE_PAPER_BEFORE,
						Constant.UNBIND_CARD_ATTACHMENT_TYPE_PAPER_BEHIND));
			}
		}
		else if(Constant.UNBIND_CARD_TYPE_UNLOST.equals(unbindType)) { // 未丢失
			// 附件需同时包含手持银行卡照片、手持身份证正面、手持身份证反面
			if (!attachmentTypeList.contains(Constant.UNBIND_CARD_ATTACHMENT_TYPE_BANK_CARD)
					|| !attachmentTypeList.contains(Constant.UNBIND_CARD_ATTACHMENT_TYPE_PAPER_BEFORE)
					|| !attachmentTypeList.contains(Constant.UNBIND_CARD_ATTACHMENT_TYPE_PAPER_BEHIND)) {
				return new ResultVo(false, String.format("%s解绑必须同时包含以下附件：%s,%s,%s", 
						Constant.UNBIND_CARD_TYPE_UNLOST,
						Constant.UNBIND_CARD_ATTACHMENT_TYPE_BANK_CARD,
						Constant.UNBIND_CARD_ATTACHMENT_TYPE_PAPER_BEFORE,
						Constant.UNBIND_CARD_ATTACHMENT_TYPE_PAPER_BEHIND));
			}
		}
		else {
			return new ResultVo(false, String.format("解绑类型错误，必须为%s,%s之一", Constant.UNBIND_CARD_TYPE_LOST, Constant.UNBIND_CARD_TYPE_UNLOST));
		}
		
		// 1)插入解绑申请表
		UnbindInfoEntity unbindInfoEntity = new UnbindInfoEntity();
		unbindInfoEntity.setCustId(custId);
		unbindInfoEntity.setRelateType(Constant.TABLE_BAO_T_BANK_CARD_INFO);
		unbindInfoEntity.setRelatePrimary(bankId);
		unbindInfoEntity.setUnbindCode(numberService.generateTradeNumber());
		unbindInfoEntity.setUnbindType(unbindType);
		unbindInfoEntity.setUnbindStatus(Constant.TRADE_STATUS_02);
		unbindInfoEntity.setUnbindDesc(unbindReason);
		unbindInfoEntity.setMemo(bankCardInfoEntity.getProtocolNo());
		unbindInfoEntity.setBasicModelProperty(custId, true);
		unbindInfoEntity = unbindInfoRepositoryCustom.insertUnBindInfo(unbindInfoEntity);
		
		// 2)插入附件表
		List<AttachmentInfoEntity> list = new ArrayList<AttachmentInfoEntity>();
		for(Map<String, String> m : attachmentList) {
			AttachmentInfoEntity attachmentInfoEntity = new AttachmentInfoEntity();
			attachmentInfoEntity.setRelateType(Constant.TABLE_BAO_T_UNBIND_INFO);
			attachmentInfoEntity.setRelatePrimary(unbindInfoEntity.getId());
			attachmentInfoEntity.setAttachmentType((String)m.get("attachmentType"));
			attachmentInfoEntity.setAttachmentName((String)m.get("attachmentName"));
			attachmentInfoEntity.setStoragePath((String)m.get("storagePath"));
			attachmentInfoEntity.setDocType((String)m.get("docType"));
			attachmentInfoEntity.setBasicModelProperty(custId, true);
			list.add(attachmentInfoEntity);
		}
		attachmentRepositoryCustom.batchInsert(list);
		
		// 3)插入审核表
		AuditInfoEntity auditInfoEntity = new AuditInfoEntity();
		auditInfoEntity.setCustId(custId);
		auditInfoEntity.setRelateType(Constant.TABLE_BAO_T_UNBIND_INFO);
		auditInfoEntity.setRelatePrimary(unbindInfoEntity.getId());
		auditInfoEntity.setApplyType(SubjectConstant.TRADE_FLOW_TYPE_UNBIND_CARD);
		auditInfoEntity.setApplyTime(new Timestamp(System.currentTimeMillis()));
		auditInfoEntity.setTradeStatus(Constant.TRADE_STATUS_01);
		auditInfoEntity.setAuditStatus(Constant.AUDIT_STATUS_REVIEWD);
		auditInfoEntity.setBasicModelProperty(custId, true);
		auditInfoEntity = auditInfoRespository.save(auditInfoEntity);
		
		// 4)记录日志
		LogInfoEntity logInfoEntity = new LogInfoEntity();
		logInfoEntity.setRelateType(Constant.TABLE_BAO_T_UNBIND_INFO);
		logInfoEntity.setRelatePrimary(unbindInfoEntity.getId());
		logInfoEntity.setLogType(Constant.OPERATION_TYPE_18);
		logInfoEntity.setOperDesc("");
		logInfoEntity.setOperPerson(custId);
		logInfoEntity.setOperIpaddress((String)param.get("ipAddress"));
		logInfoEntity.setMemo(String.format("银行卡%s提交解绑申请", replaceSpecialWord(bankCardInfoEntity.getCardNo())));
		logInfoEntity.setBasicModelProperty(custId, true);
		logInfoEntityRepository.save(logInfoEntity);
		
		return new ResultVo(true);
	}

	/**
	 * 替换敏感字符
	 *
	 * @author  wangjf
	 * @date    2015年6月3日 下午1:40:10
	 * @param words
	 * @return
	 */
	private String replaceSpecialWord(String words){
		if(StringUtils.isEmpty(words)){
			return "";
		}
		
		if(words.length() <= 8) return words;
		
		return words.substring(0, 4) + "***" + words.substring(words.length() - 4, words.length());
	}

	@Override
	public Map<String, Object> queryUnBindCard(Map<String, Object> param)
			throws SLException {
		Map<String, Object> result = new HashMap<String, Object>();
		Page<Map<String, Object>> page = unbindInfoRepositoryCustom.findUnBindList(param);
		result.put("iTotalDisplayRecords", page.getTotalElements());
		result.put("data", page.getContent());
		return result;
	}
	
	@Override
	public Map<String, Object> queryUnBindCardById(Map<String, Object> param)
			throws SLException {
		return unbindInfoRepositoryCustom.findUnBindById(param);
	}
	
	@Override
	public Map<String, Object> queryAllUnBindCard(Map<String, Object> param)
			throws SLException {	
		Map<String, Object> result = new HashMap<String, Object>();
		Page<Map<String, Object>> page = unbindInfoRepositoryCustom.findAllUnBindList(param);
		result.put("iTotalDisplayRecords", page.getTotalElements());
		result.put("data", page.getContent());
		return result;
	}
	
	@Override
	public Map<String, Object> queryAuditUnBindCardById(
			Map<String, Object> param) throws SLException {
		Map<String, Object> result = new HashMap<String, Object>();
		String unbindId = (String)param.get("unbindId");
		UnbindInfoEntity unbindInfoEntity = unbindInfoRepository.findOne(unbindId);
		param.put("bankId", unbindInfoEntity.getRelatePrimary());
		param.put("custId", unbindInfoEntity.getCustId());
		Map<String, Object> userInfo = unbindInfoRepositoryCustom.findUserBankInfo(param); // 取客户信息
		result.put("userInfo", userInfo);
		List<Map<String, Object>> list = unbindInfoRepositoryCustom.findUnBindByCustId(param); // 取历史解绑记录
//		for(Map<String, Object> m : list){
//			if(unbindId.equals((String)m.get("unbindId"))) {
//				list.remove(m);
//				break;
//			}
//		}
		result.put("unBindList", list);
		Map<String, Object> unbindInfo = unbindInfoRepositoryCustom.findUnBindById(param); // 取解绑信息
		result.put("unbindInfo", unbindInfo);
		
		AuditInfoEntity auditInfoEntity = auditInfoRespository.findByRelatePrimary(unbindId); // 取审核信息
		Map<String, Object> auditInfo = new HashMap<String, Object>();
		auditInfo.put("auditId", auditInfoEntity.getId());
		auditInfo.put("auditUser", auditInfoEntity.getAuditUser());
		auditInfo.put("auditStatus", auditInfoEntity.getAuditStatus());
		auditInfo.put("auditMemo", auditInfoEntity.getMemo());
		result.put("auditInfo", auditInfo);
		
		return result;
	}
	
//	@Override
//	public ResultVo auditUnBindCard(Map<String, Object> param)
//			throws SLException {
//		String auditId = (String)param.get("auditId");
//		String auditUserId = (String)param.get("auditUserId");
//		String auditStatus = (String)param.get("auditStatus");
//		String auditMemo = (String)param.get("auditMemo");
//		
//		// 审核信息
//		AuditInfoEntity auditInfoEntity = auditInfoRespository.findOne(auditId);
//		
//		if(!Constant.AUDIT_STATUS_REVIEWD.equals(auditInfoEntity.getAuditStatus())) {
//			return new ResultVo(false, "该笔申请已经审核，请勿重复审核！");
//		}
//		
//		// 审核通过
//		UnbindInfoEntity unbindInfoEntity = unbindInfoRepository.findOne(auditInfoEntity.getRelatePrimary());
//		CustInfoEntity custEntity = custInfoRepository.findOne(unbindInfoEntity.getCustId());
//		if(Constant.AUDIT_STATUS_PASS.equals(auditStatus)) {
//			
//			CustInfoEntity custInfoEntity = custInfoRepository.findOne(unbindInfoEntity.getCustId());
//			BankCardInfoEntity bankCardInfoEntity = bankCardInfoRepository.findOne(unbindInfoEntity.getRelatePrimary());
//			
//			// 调用第三方解绑接口
//			Map<String, Object> paramsMap = new HashMap<String, Object>();
//			paramsMap.put("custCode", custInfoEntity.getCustCode());
//			paramsMap.put("noAgree", bankCardInfoEntity.getProtocolNo());
//			paramsMap.put("tradeCode", unbindInfoEntity.getUnbindCode());
//			ResultVo resultVo = thirdPartyPayService.unbindUserBank(paramsMap);
//			if(!ResultVo.isSuccess(resultVo)) { // 第三方调用失败返回
//				return resultVo;
//			}
//			
//			// 解绑申请置为成功
//			unbindInfoEntity.setUnbindStatus(Constant.TRADE_STATUS_03);
//			unbindInfoEntity.setBasicModelProperty(auditUserId, false);
//			auditInfoEntity.setTradeStatus(Constant.TRADE_STATUS_03);
//			
//			// 设置银行卡状态为无效
//			bankCardInfoEntity.setRecordStatus(Constant.VALID_STATUS_INVALID);
//			bankCardInfoEntity.setBasicModelProperty(auditUserId, false);
//			
//			// 发送短信
//			Map<String, Object> smsParams = new HashMap<String, Object>();
//			smsParams.put("mobile", custEntity.getMobile());
//			smsParams.put("custId", unbindInfoEntity.getCustId());
//			smsParams.put("messageType", Constant.SMS_TYPE_UNBIND_PASS);
//			smsParams.put("values", bankCardInfoEntity.getCardNo().substring(bankCardInfoEntity.getCardNo().length() - 4, bankCardInfoEntity.getCardNo().length()));
//			smsService.asnySendSMS(smsParams);
//		}
//		else {
//			unbindInfoEntity.setUnbindStatus(Constant.TRADE_STATUS_04);
//			unbindInfoEntity.setBasicModelProperty(auditUserId, false);
//			auditInfoEntity.setTradeStatus(Constant.TRADE_STATUS_04);
//			
//			// 发送通知
//			CustInfoEntity systemEntity = custInfoRepository.findOne(Constant.CUST_ADMIN_ID);
//			SystemMessageInfoEntity systemMessageInfoEntity = new SystemMessageInfoEntity();
//			systemMessageInfoEntity.setSendCust(systemEntity);
//			systemMessageInfoEntity.setReceiveCust(custEntity);
//			systemMessageInfoEntity.setSendTitle("银行卡解绑通知");
//			systemMessageInfoEntity.setSendContent(String.format("尊敬的用户，您的银行卡解绑请求被拒绝，原因是“%s”", auditMemo));
//			systemMessageInfoEntity.setSendDate(new Date());
//			systemMessageInfoEntity.setIsRead(Constant.SITE_MESSAGE_NOREAD);
//			systemMessageInfoEntity.setBasicModelProperty(auditUserId, true);
//			systemMessageInfoRepository.save(systemMessageInfoEntity);
//			
//			// 发送短信
//			Map<String, Object> smsParams = new HashMap<String, Object>();
//			smsParams.put("mobile", custEntity.getMobile());
//			smsParams.put("custId", unbindInfoEntity.getCustId());
//			smsParams.put("messageType", Constant.SMS_TYPE_UNBIND_REFUSE);
//			smsParams.put("values", auditMemo);
//			smsService.asnySendSMS(smsParams);
//		}
//		
//		auditInfoEntity.setAuditTime(new Date());
//		auditInfoEntity.setAuditUser(auditUserId);
//		auditInfoEntity.setAuditStatus(auditStatus);
//		auditInfoEntity.setMemo(auditMemo);
//		
//		return new ResultVo(true, "审核成功");
//	}
	
	private final static ThreadLocal<AuditInfoEntity> localAuditInfo = new ThreadLocal<>();
	private final static ThreadLocal<UnbindInfoEntity> localUnbindInfo = new ThreadLocal<>();
	private final static ThreadLocal<CustInfoEntity> localCustInfo = new ThreadLocal<>();
	private final static ThreadLocal<BankCardInfoEntity> localBankCardInfo = new ThreadLocal<>();
	
	@Override
	public ResultVo preauditUnBindCard(Map<String, Object> param)
			throws SLException {
		
		String auditId = (String)param.get("auditId");
		
		// 审核信息
		AuditInfoEntity auditInfoEntity = auditInfoRespository.findOne(auditId);
		
		if(!Constant.AUDIT_STATUS_REVIEWD.equals(auditInfoEntity.getAuditStatus())) {
			return new ResultVo(false, "该笔申请已经审核，请勿重复审核！");
		}
		
		// 审核通过
		UnbindInfoEntity unbindInfoEntity = unbindInfoRepository.findOne(auditInfoEntity.getRelatePrimary());
		CustInfoEntity custEntity = custInfoRepository.findOne(unbindInfoEntity.getCustId());
		BankCardInfoEntity bankCardInfoEntity = bankCardInfoRepository.findOne(unbindInfoEntity.getRelatePrimary());
		
		localAuditInfo.set(auditInfoEntity);
		localUnbindInfo.set(unbindInfoEntity);
		localCustInfo.set(custEntity);
		localBankCardInfo.set(bankCardInfoEntity);
		
		return new ResultVo(true);
	}

	@Override
	public ResultVo postauditUnBindCard(ResultVo result)
			throws SLException {
		
		AuditInfoEntity auditInfoEntity = localAuditInfo.get();		
		UnbindInfoEntity unbindInfoEntity = localUnbindInfo.get();
		CustInfoEntity custInfoEntity = localCustInfo.get();
		BankCardInfoEntity bankCardInfoEntity = localBankCardInfo.get();
		
		localAuditInfo.remove();
		localUnbindInfo.remove();
		localCustInfo.remove();
		localBankCardInfo.remove();
		
		if(!ResultVo.isSuccess(result)){
			return result;
		}
		
		if(Constant.TRADE_STATUS_03.equals(unbindInfoEntity.getUnbindStatus())) {
			// 发送短信
			Map<String, Object> smsParams = new HashMap<String, Object>();
			smsParams.put("mobile", custInfoEntity.getMobile());
			smsParams.put("custId", unbindInfoEntity.getCustId());
			smsParams.put("messageType", Constant.SMS_TYPE_UNBIND_PASS);
			smsParams.put("values", bankCardInfoEntity.getCardNo().substring(bankCardInfoEntity.getCardNo().length() - 4, bankCardInfoEntity.getCardNo().length()));
			smsService.asnySendSMS(smsParams);
			
			// 判断用户是否是借款用户，是的话则需通知商务
			Map<String, Object> requestParam = Maps.newConcurrentMap();
			requestParam.put("custId", custInfoEntity.getId());
			requestParam.put("bankId", unbindInfoEntity.getRelatePrimary());
			openThirdService.unBindBankNotify(requestParam);
		}
		else {
			// 发送短信
			Map<String, Object> smsParams = new HashMap<String, Object>();
			smsParams.put("mobile", custInfoEntity.getMobile());
			smsParams.put("custId", unbindInfoEntity.getCustId());
			smsParams.put("messageType", Constant.SMS_TYPE_UNBIND_REFUSE);
			smsParams.put("values", auditInfoEntity.getMemo());
			smsService.asnySendSMS(smsParams);
		}
		
		return new ResultVo(true);
	}
	
	@Override
	public ResultVo auditUnBindCard(Map<String, Object> param)
			throws SLException {
		
		String auditStatus = (String)param.get("auditStatus");
		UnbindInfoEntity unbindInfoEntity = localUnbindInfo.get();
		CustInfoEntity custInfoEntity = localCustInfo.get();
		BankCardInfoEntity bankCardInfoEntity = localBankCardInfo.get();
		if(Constant.AUDIT_STATUS_PASS.equals(auditStatus)) {
			// 调用第三方解绑接口
			Map<String, Object> paramsMap = new HashMap<String, Object>();
			paramsMap.put("custCode", custInfoEntity.getCustCode());
			paramsMap.put("noAgree", bankCardInfoEntity.getProtocolNo());
			paramsMap.put("tradeCode", unbindInfoEntity.getUnbindCode());
			 
			try {
				ResultVo resultVo = thirdPartyPayService.unbindUserBank(paramsMap);
				if(!ResultVo.isSuccess(resultVo)) { // 第三方调用失败返回
					return resultVo;
				}
			} catch (Exception e) {
				log.error("节约失败！" + e.getMessage());
				return new ResultVo(false, "解约失败！");
			}
		}
		
		return internalAuditUnBindCard.doAuditUnBindCard(param);
	}
	
	@Autowired
	private InternalAuditUnBindCard internalAuditUnBindCard;
	
	@Service
	public static class InternalAuditUnBindCard {
		
		@Autowired
		private CustInfoRepository custInfoRepository;
		
		@Autowired
		private SystemMessageInfoRepository systemMessageInfoRepository;
		
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public ResultVo doAuditUnBindCard(Map<String, Object> param)
				throws SLException {
			String auditUserId = (String)param.get("auditUserId");
			String auditStatus = (String)param.get("auditStatus");
			String auditMemo = (String)param.get("auditMemo");
			
			UnbindInfoEntity unbindInfoEntity = localUnbindInfo.get();
			CustInfoEntity custInfoEntity = localCustInfo.get();
			BankCardInfoEntity bankCardInfoEntity = localBankCardInfo.get();
			AuditInfoEntity auditInfoEntity = localAuditInfo.get();
			
			if(Constant.AUDIT_STATUS_PASS.equals(auditStatus)) {
				// 解绑申请置为成功
				unbindInfoEntity.setUnbindStatus(Constant.TRADE_STATUS_03);
				unbindInfoEntity.setBasicModelProperty(auditUserId, false);
				auditInfoEntity.setTradeStatus(Constant.TRADE_STATUS_03);
				
				// 设置银行卡状态为无效
				bankCardInfoEntity.setRecordStatus(Constant.VALID_STATUS_INVALID);
				bankCardInfoEntity.setBasicModelProperty(auditUserId, false);
			}
			else {
				unbindInfoEntity.setUnbindStatus(Constant.TRADE_STATUS_04);
				unbindInfoEntity.setBasicModelProperty(auditUserId, false);
				auditInfoEntity.setTradeStatus(Constant.TRADE_STATUS_04);
				
				// 发送通知
				CustInfoEntity systemEntity = custInfoRepository.findOne(Constant.CUST_ADMIN_ID);
				SystemMessageInfoEntity systemMessageInfoEntity = new SystemMessageInfoEntity();
				systemMessageInfoEntity.setSendCust(systemEntity);
				systemMessageInfoEntity.setReceiveCust(custInfoEntity);
				systemMessageInfoEntity.setSendTitle("银行卡解绑通知");
				systemMessageInfoEntity.setSendContent(String.format("尊敬的用户，您的银行卡解绑请求被拒绝，原因是“%s”", auditMemo));
				systemMessageInfoEntity.setSendDate(new Date());
				systemMessageInfoEntity.setIsRead(Constant.SITE_MESSAGE_NOREAD);
				systemMessageInfoEntity.setBasicModelProperty(auditUserId, true);
				systemMessageInfoRepository.save(systemMessageInfoEntity);
			}
			
			auditInfoEntity.setAuditTime(new Date());
			auditInfoEntity.setAuditUser(auditUserId);
			auditInfoEntity.setAuditStatus(auditStatus);
			auditInfoEntity.setMemo(auditMemo);
			
			return new ResultVo(true);
		}
	}

	@Override
	public Map<String, Object> queryBankManager(Map<String, Object> param)
			throws SLException {
		Map<String, Object> result = new HashMap<String, Object>();
		Page<Map<String, Object>> page = bankCardInfoRepositoryCustom.findBankList(param);
		result.put("iTotalDisplayRecords", page.getTotalElements());
		result.put("data", page.getContent());
		return result;
	}

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo mendBankCard() throws SLException {
		
		// 查询所有银行卡有效且协议号为空的记录
		List<BankCardInfoEntity> bankList = bankCardInfoRepository.findByRecordStatus(Constant.VALID_STATUS_VALID);
		
		for(BankCardInfoEntity bank : bankList){
			Map<String, Object> paramsMap = new HashMap<String, Object>();
			paramsMap.put("bankCardNo", bank.getCardNo());
			paramsMap.put("custCode", bank.getCustInfoEntity().getCustCode());
			paramsMap.put("noAgree", bank.getProtocolNo());
			paramsMap.put("bankId", bank.getId());
		
			mendOneBank(paramsMap);
			
			// 纠正银行卡名称
			try
			{
				Map<String, Object> result = thirdPartyPayService.queryThirdBankByCardNo(paramsMap);
				if(!StringUtils.isEmpty((String)result.get("bankName")) && !bank.getBankName().equals((String)result.get("bankName"))) {
					int rows = bankCardInfoRepository.updateBankNameById(bank.getId(), (String)result.get("bankName"));
					if(rows == 0) {
						log.warn(String.format("更新银行卡名称失败！银行卡ID：", bank.getId()));
					}
				}
			}
			catch(Exception e) 
			{
				log.error("银行补单失败！" + e.getMessage());
			}
		}
		
		return new ResultVo(true);
	}

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo mendOneBank(Map<String, Object> paramsMap) {	
		String noAgree = "";
		// 补协议号
		try
		{
			Map<String, Object> result = thirdPartyPayService.queryUserBank(paramsMap);
			noAgree = (String)result.get("no_agree");
			if(!StringUtils.isEmpty(noAgree)) {
				int rows = bankCardInfoRepository.updateProtocolNoById((String)paramsMap.get("bankId"), noAgree, Constant.SYSTEM_USER_BACK, new Date());
				if(rows == 0) {
					log.warn(String.format("更新银行卡协议号失败！银行卡ID：", (String)paramsMap.get("bankId")));
				}
			}
		}
		catch(Exception e) 
		{
			log.error("银行补单失败！" + e.getMessage());
		}	
		return new ResultVo(true, "补全协议号成功", noAgree);
	}
	
	@Override
	public ResultVo queryBankList(Map<String, Object> param) {
		Map<String, Object> map= Maps.newHashMap();
		List<Map<String, Object>> result = bankCardInfoRepositoryCustom.findBankCardList(param);
		map.put("bankCardList", result);
		return new ResultVo(true, "银行卡列表查询成功", map);
	}

	@Override
	public ResultVo queryWealthBankDetailByBankId(Map<String, Object> param) {
		Map<String, Object> map = Maps.newHashMap();
		String custId = (String) param.get("custId");
		String tradePassword = (String) param.get("tradePassword");
		
		CustInfoEntity custInfoEntity = custInfoRepository.findOne(custId);
		if(custInfoEntity == null){
			return new ResultVo(false, "客户不存在");
		}
		
		if(!tradePassword.equals(custInfoEntity.getTradePassword())){
			return new ResultVo(false, "交易密码有误");
		}
		
		List<Map<String, Object>> list = bankCardInfoRepositoryCustom.findBankCardList(param);
		if(list != null && list.size() > 0){
			map.put("bankCardInfo", list.get(0));
		}
		
		return new ResultVo(true, "查看附属银行卡详情成功", map);
	}

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo updateBank(Map<String, Object> param) throws SLException {
		String custId = (String) param.get("custId");
		String bankId = (String) param.get("bankId");
		String tradePassword = (String) param.get("tradePassword");
		String branchBankName = (String) param.get("branchBankName");
		String openProvince = (String) param.get("openProvince");
		String openCity = (String) param.get("openCity");
		
		//验证客户有效性
		CustInfoEntity custInfoEntity = custInfoRepository.findOne(custId);
		if(custInfoEntity == null){
			return new ResultVo(false, "客户不存在");
		}
		
		//验证交易密码是否正确
		if(!tradePassword.equals(custInfoEntity.getTradePassword())){
			return new ResultVo(false, "交易密码有误");
		}
		
		//验证银行卡有效性
		BankCardInfoEntity bankCardInfoEntity = bankCardInfoRepository.findOne(bankId);
		if(bankCardInfoEntity == null){
			return new ResultVo(false, "银行卡不存在");
		} else if (!custId.equals(bankCardInfoEntity.getCustInfoEntity().getId())){
			return new ResultVo(false, "银行卡与客户不匹配");
		}
		
		if(!Constant.VALID_STATUS_VALID.equals(bankCardInfoEntity.getRecordStatus())){
			return new ResultVo(false, "该银行卡未绑定成功或者已经解绑");
		}
		
		//修改银行卡信息
		bankCardInfoEntity.setSubBranchName(branchBankName);
		bankCardInfoEntity.setOpenProvince(openProvince);
		bankCardInfoEntity.setOpenCity(openCity);
		bankCardInfoEntity.setBasicModelProperty(custId, false);
		
		//记录日志
		LogInfoEntity logInfoEntity = new LogInfoEntity();
		logInfoEntity.setRelateType(Constant.TABLE_BAO_T_BANK_CARD_INFO);
		logInfoEntity.setRelatePrimary(bankCardInfoEntity.getId());
		logInfoEntity.setLogType(Constant.OPERATION_TYPE_41);
		logInfoEntity.setOperDesc("修改银行卡信息");
		logInfoEntity.setOperPerson(custId);
		logInfoEntity.setBasicModelProperty(custId, true);
		logInfoEntityRepository.save(logInfoEntity);
		
		return new ResultVo(true, "银行卡信息修改成功");
	}
		
	/**
	 * 线下业务-附属银行卡-客户银行卡审核列表
	 *
	 * @author  liyy
	 * @date    2016年2月24日 
	 * @param param
     *      <tt>start          :String:起始值</tt><br>
     *      <tt>length         :String:长度</tt><br>
     *      <tt>custName       :String:用户名（可选）</tt><br>
     *      <tt>credentialsCode:String:证件号码(可选)</tt><br>
     *      <tt>mobile         :String:手机号（可选）</tt><br>
     *      <tt>auditStatus    :String:审核状态（可选）</tt><br>
     *      <tt>bankCardNo     :String:银行卡号（可选）</tt><br>
	 * @return ResultVo
	 * 		<tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
     *      <tt>data
     *      	<tt>iTotalDisplayRecords:String:总数</tt><br>
     *      	<tt>data
     *      		<tt>tradeFlowId    :String:交易过程流水ID</tt><br>
     *      		<tt>custName       :String:用户名</tt><br>
     *      		<tt>credentialsCode:String:证件号码</tt><br>
     *      		<tt>mobile         :String:手机号</tt><br>
     *      		<tt>bankName       :String:银行名称</tt><br>
     *      		<tt>bankCardNo     :String:银行卡号</tt><br>
     *      		<tt>branchBankName :String:支行名称</tt><br>
     *      		<tt>openProvince   :String:开户行所在省</tt><br>
     *      		<tt>openCity       :String:开户行所在市</tt><br>
     *      		<tt>auditStatus    :String:审核状态</tt><br>
     *          </tt><br>
     *      </tt><br>
	 */
	public ResultVo queryWealthBankList(Map<String, Object> param) {
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			Page<Map<String, Object>> page = bankCardInfoRepositoryCustom.queryWealthBankList(param);
			
			data.put("iTotalDisplayRecords", page.getTotalElements());
			data.put("data", page.getContent());
			return new ResultVo(true, "客户银行卡审核列表查询成功",data);
		} catch (Exception e) {
			log.error("客户银行卡审核列表查询失败" + e.getMessage());
			return new ResultVo(false, "客户银行卡审核列表查询失败");
		}
	}
	
	/**
	 * 线下业务-附属银行卡-客户银行卡查看详情
	 *
	 * @author  liyy
	 * @date    2016年2月24日 
	 * @param param
     *      <tt>tradeFlowId    :String:交易过程流水ID</tt><br>
	 * @return ResultVo
	 * 		<tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
     *      <tt>data
     *      	<tt>tradeFlowId    :String:交易过程流水ID</tt><br>
     *      	<tt>custName       :String:用户名</tt><br>
     *      	<tt>credentialsCode:String:证件号码</tt><br>
     *      	<tt>mobile         :String:手机号</tt><br>
     *      	<tt>bankName       :String:银行名称</tt><br>
     *      	<tt>bankCardNo     :String:银行卡号</tt><br>
     *      	<tt>branchBankName :String:支行名称</tt><br>
     *      	<tt>openProvince   :String:开户行所在省</tt><br>
     *      	<tt>openCity       :String:开户行所在市</tt><br>
     *      	<tt>attachmentList:List<Map<String, Object>>:附件列表
     *     			<tt>attachmentType:String:         附件类型</tt><br>
     *      		<tt>attachmentName:String:         附件名称</tt><br>
     *      		<tt>storagePath   :String:         存储路径</tt><br>
     *      	</tt><br>
     *     		<tt>auditList:List<Map<String, Object>>:审核日志列表
     *      		<tt>审核ID:String:         auditId</tt><br>
     *      		<tt>审核时间:String:         auditDate</tt><br>
     *      		<tt>审核人员:String:         auditUser</tt><br>
     *      		<tt>审核结果:String:         auditStatus</tt><br>
     *      		<tt>审核备注:String:         auditMemo</tt><br>
     *      	</tt><br>
     *      </tt><br>
	 */
	public ResultVo queryWealthBankDetailById(Map<String, Object> param) {
		try {
			Map<String, Object> data;
			// 卡基本信息
			List<Map<String, Object>> list = bankCardInfoRepositoryCustom.queryBankDetailById(param);
			/* data
			 * tradeFlowId :String:交易过程流水ID
			 * custName :String:客户名称
			 * credentialsCode :String:证件号码
			 * mobile :String:手机
			 * bankName :String:银行名称
			 * bankCardNo :String:银行卡号
			 * branchBankName :String:支行名称
			 * openProvince :String:开户省
			 * openCity :String:开户市
			 */
			if(list == null || list.size() == 0){
				return new ResultVo(false, "客户银行卡查看详情查询失败, 数据为空");
			}
			data = list.get(0);
			// 附件列表
			List<Map<String, Object>> attachmentList 
				= bankCardInfoRepositoryCustom.queryAttachmentInfoListById(data);
			// 审核日志列表
			List<Map<String, Object>> auditList 
				= bankCardInfoRepositoryCustom.queryAuditInfoListById(data);
			
			data.put("attachmentList", attachmentList);
			data.put("auditList", auditList);
			return new ResultVo(true, "客户银行卡查看详情查询成功",data);
		} catch (Exception e) {
			log.error("客户银行卡审核列表查询失败" + e.getMessage());
			return new ResultVo(false, "客户银行卡查看详情查询失败");
		}
	}
	
	/**
	 * 线下业务-附属银行卡-客户银行卡审核
	 *
	 * @author  liyy
	 * @date    2016年2月25日 
	 * @param param
     *      <tt>tradeFlowId:String:交易过程流水ID</tt><br>
     *      <tt>auditStatus:String:审核状态</tt><br>
     *      <tt>auditMemo  :String: 审核备注</tt><br>
     *      <tt>userId     :String:创建人</tt><br>
	 * @return ResultVo
	 * 		<tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
     *  @throws SLException
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo auditWealthBank(Map<String, Object> param) throws SLException {
		String tradeFlowId = (String) param.get("tradeFlowId");
		String auditStatus = (String) param.get("auditStatus");
		String auditMemo = (String) param.get("auditMemo");
		String userId = (String) param.get("userId");

		// 审核表
		AuditInfoEntity auditInfoEntity = auditInfoRespository.findByRelatePrimary(tradeFlowId);
		String beforeAuditStatus = auditInfoEntity.getAuditStatus();// 修改前审核状态
		// add @2016/3/22  添加数据检查防止前台重复提交
		if(Constant.BANK_AUDIT_STATUS_PASS.equals(beforeAuditStatus)){
			log.error("该记录审核通过，请刷新数据！");
			throw new SLException("该记录审核通过，请刷新数据！");
		}else if(Constant.BANK_AUDIT_STATUS_REFUSE.equals(beforeAuditStatus)){
			log.error("该记录审核拒绝，请刷新数据！");
			throw new SLException("该记录审核拒绝，请刷新数据！");
		}
		auditInfoEntity.setBasicModelProperty(userId, false);
		auditInfoEntity.setAuditTime(new Date());
		auditInfoEntity.setAuditUser(userId);
		auditInfoEntity.setAuditStatus(auditStatus);
		auditInfoEntity.setMemo(auditMemo);

		// 交易流水表
		TradeFlowInfoEntity tradeFlowInfoEntity = tradeFlowInfoRepository.findOne(tradeFlowId);
		tradeFlowInfoEntity.setBasicModelProperty(userId, false);
		CustInfoEntity custInfoEntity = custInfoRepository.findOne(tradeFlowInfoEntity.getCustId());
		
		if(Constant.BANK_AUDIT_STATUS_PASS.equals(auditStatus)){
			// 通过
			tradeFlowInfoEntity.setTradeStatus(Constant.TRADE_STATUS_03);
			auditInfoEntity.setTradeStatus(Constant.TRADE_STATUS_03);
			
			// 新增附属银行卡信息
			BankCardInfoEntity bankCardInfoEntity = new BankCardInfoEntity();
			bankCardInfoEntity.setBasicModelProperty(userId, true);
			bankCardInfoEntity.setCustInfoEntity(custInfoEntity);
			bankCardInfoEntity.setBankName(tradeFlowInfoEntity.getBankName());
			bankCardInfoEntity.setCardNo(tradeFlowInfoEntity.getBankCardNo());
			bankCardInfoEntity.setOpenProvince(tradeFlowInfoEntity.getOpenProvince());
			bankCardInfoEntity.setOpenCity(tradeFlowInfoEntity.getOpenCity());
			bankCardInfoEntity.setSubBranchName(tradeFlowInfoEntity.getBranchBankName());
			bankCardInfoEntity.setIsDefault(Constant.BANK_IS_DEFAULT_NO);
			bankCardInfoEntity.setBankFlag(Constant.CUST_TYPE_OFFLINE);
			bankCardInfoRepository.save(bankCardInfoEntity);
			
			//发送短息
			sendSms(custInfoEntity.getId(), custInfoEntity.getMobile(), Constant.SMS_TYPE_BIND_BANKCARD, bankCardInfoEntity.getCardNo(), new Date());
			
		} else if (Constant.BANK_AUDIT_STATUS_FALLBACK.equals(auditStatus)||Constant.BANK_AUDIT_STATUS_REFUSE.equals(auditStatus)) {
			// 审核回退、拒绝
			tradeFlowInfoEntity.setTradeStatus(Constant.TRADE_STATUS_04);
			auditInfoEntity.setTradeStatus(Constant.TRADE_STATUS_04);
			//发送站内信
			sendSysBindBankCardFail(custInfoEntity.getId(), DateUtils.formatDate(auditInfoEntity.getCreateDate(), "yyyy-MM-dd"), auditMemo, auditStatus);
		} else {
			log.error("更新审核状态出错，请选择正确的状态");
			throw new SLException("更新审核状态出错，请选择正确的状态");
		}
		
		// 日志信息
		LogInfoEntity logInfoEntity = new LogInfoEntity();
		logInfoEntity.setBasicModelProperty(userId, true);
		logInfoEntity.setRelateType(Constant.TABLE_BAO_T_TRADE_FLOW_INFO);
		logInfoEntity.setRelatePrimary(tradeFlowId);
		logInfoEntity.setLogType(Constant.OPERATION_TYPE_45);
		logInfoEntity.setOperDesc(auditMemo);
		logInfoEntity.setOperPerson(userId);
		logInfoEntity.setOperBeforeContent(beforeAuditStatus);
		logInfoEntity.setOperAfterContent(auditStatus);
		logInfoEntity.setMemo(auditMemo);
		logInfoEntityRepository.save(logInfoEntity);
		
		return new ResultVo(true, "客户银行卡审核操作成功");
	}
	
	/**
	 * 线下充值审核失败发送站内信
	 * 
	 * @param custId 客户id
	 * @param date 提交申请年月日
	 * @param errMessage 错误提示
	 * @param auditStatus 审核状态 
	 * @return
	 */
	private void sendSysBindBankCardFail(String custId, String date, String errMessage, String auditStatus) {
		try {
			Map<String, Object> sysParams = Maps.newHashMap();
			sysParams.put("messageType", Constant.SYS_TYPE_BIND_BANKCARD_FAIL);
			sysParams.put("custId", custId);
			sysParams.put("systemMessage", new Object[] { date, auditStatus, errMessage, Constant.PLATFORM_SERVICE_TEL }); // 站内信内容
			// 做异步发送邮件处理,短信服务已经做了异步处理
			smsService.asnySendSystemMessage(sysParams);
		} catch (Exception e) {
			log.info(e.getCause().toString());
		}
	}
	
	/**
	 * 线下充值成功发送短信内容map
	 * 
	 * @date 2016-07-05
	 * @author zhiwen_feng
	 * @param mobile
	 * @param messageType
	 * @param tradeMount
	 * @param accountNo
	 * @param custId
	 */
	private void sendSms(String custId, String mobile, String messageType, String bankNo, Date sendDate) {
		// //做异步发送邮件处理,短信服务已经做了异步处理
		try {
			Map<String, Object> smsParams = Maps.newHashMap();
			smsParams.put("custId", custId);
			smsParams.put("mobile", mobile);
			smsParams.put("messageType", messageType);
			smsParams.put("values", new Object[] { // 短信息内容
							mobile.substring(0, mobile.length()-(mobile.substring(3)).length())+"****"+mobile.substring(7),
							DateUtils.formatDate(sendDate, "MM"), 
							DateUtils.formatDate(sendDate, "dd"),
							bankNo.substring(bankNo.length() - 4, bankNo.length()) 
						});

			smsService.asnySendSMS(smsParams);
		} catch (Exception e) {
			log.info(e.getCause().toString());
		}
	}

	@Override
	public ResultVo queryWithDrawBankList(Map<String, Object> param) {
		Map<String, Object> map= Maps.newHashMap();
		List<Map<String, Object>> bankCardList = bankCardInfoRepositoryCustom.queryWithDrawBankList(param);
		map.put("bankCardList", bankCardList);
		return new ResultVo(true, "提现银行卡列表查询成功", map);
	}

	@Override
	public ResultVo queryBankById(Map<String, Object> param) {
		Map<String, Object> map= Maps.newHashMap();
		List<Map<String, Object>> bankCardList = bankCardInfoRepositoryCustom.queryWithDrawBankList(param);
		if(bankCardList != null && bankCardList.size() > 0){
			map.put("bankCardInfo", bankCardList.get(0));
		}
		return new ResultVo(true, "查询提现银行卡详细成功", map);
	}

	@Override
	public ResultVo queryRechargeBankList(Map<String, Object> param) {
		
		List<Map<String, Object>> bankCardList = paramRepositoryCustom.findBankByType(param);
		
		return new ResultVo(true, "查询第三方支持的银行卡", bankCardList);
	}
	
	@Autowired
	private CustApplyInfoRepository custApplyInfoRepository;
	
	@Autowired
	private CustRecommendInfoRepository custRecommendInfoRepository;
	
	/**
	 * 判断是否允许展示线下银行卡
	 * @author  liyy
	 * @date    2016年5月31日
	 * @param param
	        <tt>custId:客户ID(可选)</tt><br>
	 * @return
	 		<tt>resultVo： isSuccess:是否成功</tt><br>
	 */
	public ResultVo queryCanShowCustBank(Map<String, Object> param) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("isShow", "否");
		
		// 判断客户有没有实名认证
		int num = custApplyInfoRepository.checkRealNameAuth(param.get("custId").toString());
		// 判断客户有没有归属关系
		int count = custRecommendInfoRepository.findCountCustRecommendByQuiltCustId(param.get("custId").toString());
		if(num > 0  && count > 0) {
			data.put("isShow", "是");
		}
		
		return new ResultVo(true, "判断是否允许展示线下银行卡,处理成功", data);
	}
	
	/**
	 * 认证银行卡列表
	 * @author  liyy
	 * @date    2016年5月31日
	 * @param param
	        <tt>custId:客户ID</tt><br>
	 * @return
	 *		<tt>resultVo： isSuccess:是否成功</tt><br>
	 *			<tt>message:String</tt><br>
     *      	<tt>success:String</tt><br>
     *      	<tt>data:object
     *      		<tt>data:list
     *      			<tt>bankId      :String:银行主键</tt><br>
     *      			<tt>bankName    :String:银行名称</tt><br>
     *      			<tt>bankCardNo  :String:银行卡号</tt><br>
     *      			<tt>bankCode    :String:银行编号</tt><br>
     *      			<tt>recordStatus:String:绑定状态</tt><br>
     *      		</tt><br>
     *      	</tt><br>
	 */
	public ResultVo queryAuthBankList(Map<String, Object> param) {
		Map<String, Object> data = new HashMap<String, Object>();
		
//		List<Map<String, Object>> list = bankCardInfoRepositoryCustom.findBankCardList(param);
		param.put("bankFlag", "线上");
		List<Map<String, Object>> list = bankCardInfoRepositoryCustom.findBankCardList(param);
		data.put("data", list);
		
		return new ResultVo(true, "判断是否允许展示线下银行卡,处理成功", data);
	}

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo bindBankCard(Map<String, Object> param) throws SLException {
		String tradeType = (String)param.get("tradeType");	
		String credentialsCode = (String)param.get("credentialsCode");
		String custName = (String)param.get("custName");
		String custCode = (String)param.get("custCode");
		String cardNo = (String)param.get("bankCardNo");
		if(Constant.NOTIFY_TYPE_TRADE_TYPE_BIND_CARD.equals(tradeType)) { // 绑定银行卡
	
			CustInfoEntity custInfoEntity = null;
			if(!param.containsKey("custInfoEntity")) { // 不存在客户ID则需通过身份证来验证是否需要信息客户
				// 验证借款用户是否存在，不存在生成一个
				Map<String, Object> custMap = Maps.newHashMap();
				custMap.put("credentialsCode", credentialsCode);
				custMap.put("custName", custName);
				custMap.put("custCode", custCode);
				custMap.put("userId", Constant.SYSTEM_USER_BACK);
				custMap.put("cust", param);
				ResultVo resultVo = customerService.createLoanCust(custMap);
				custInfoEntity = (CustInfoEntity)resultVo.getValue("data");
			}
			else {
				custInfoEntity = (CustInfoEntity)param.get("custInfoEntity");
			}
							
			List<BankCardInfoEntity> list = bankCardInfoRepository.findByCardNo(cardNo);
			if(list.size() == 0) {
				BankCardInfoEntity bankCard = new BankCardInfoEntity();
				bankCard.setBankName((String)param.get("bankName"));
				bankCard.setCardNo(cardNo);
				if(param.containsKey("responseCardId")) {
					bankCard.setProtocolNo((String)param.get("responseCardId"));
				}
				if(param.containsKey("openProvince")) {
					bankCard.setOpenProvince((String)param.get("openProvince"));
				}
				if(param.containsKey("openCity")) {
					bankCard.setOpenCity((String)param.get("openCity"));
				}
				if(param.containsKey("subBranchName")) {
					bankCard.setSubBranchName((String)param.get("subBranchName"));
				}
				bankCard.setCustInfoEntity(custInfoEntity);
				bankCard.setIsDefault("是");
				bankCard.setBankFlag(Constant.BANK_FLAG_ONLINE);
				bankCard.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
				bankCardInfoRepository.save(bankCard);
			} else{
				throw new SLException("该银行卡号已存在！");
			}
		}
		else if(Constant.NOTIFY_TYPE_TRADE_TYPE_UNBIND_CARD.equals(tradeType)) {
			CustInfoEntity custInfoEntity = custInfoRepository.findByCredentialsCodeAndCustName(credentialsCode, custName);
			if(custInfoEntity != null) {	
				List<BankCardInfoEntity> list = bankCardInfoRepository.findByCardNo(cardNo);
				for(BankCardInfoEntity b : list) {
					b.setRecordStatus(Constant.VALID_STATUS_INVALID);
					b.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
					b.setMemo("商务同步解绑");
				}
			}
		}
		return new ResultVo(true, "操作成功");
	}

	@Override
	public Map<String, Object> queryBankCardInfoByLoanId(String loanId) {
		Map<String, Object> result = bankCardInfoRepositoryCustom.queryBankCardInfoByLoanId(loanId);
		return result;
	}
}