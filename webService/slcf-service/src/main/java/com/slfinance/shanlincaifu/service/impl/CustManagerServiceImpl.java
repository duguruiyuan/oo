package com.slfinance.shanlincaifu.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.Executor;

import com.slfinance.shanlincaifu.utils.*;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AccountInfoEntity;
import com.slfinance.shanlincaifu.entity.AgentEntity;
import com.slfinance.shanlincaifu.entity.AttachmentInfoEntity;
import com.slfinance.shanlincaifu.entity.AuditInfoEntity;
import com.slfinance.shanlincaifu.entity.BankCardInfoEntity;
import com.slfinance.shanlincaifu.entity.CustApplyInfoEntity;
import com.slfinance.shanlincaifu.entity.CustInfoEntity;
import com.slfinance.shanlincaifu.entity.CustRecommendInfoEntity;
import com.slfinance.shanlincaifu.entity.DeviceInfoEntity;
import com.slfinance.shanlincaifu.entity.InvestInfoEntity;
import com.slfinance.shanlincaifu.entity.LogInfoEntity;
import com.slfinance.shanlincaifu.entity.PosInfoEntity;
import com.slfinance.shanlincaifu.entity.TradeFlowInfoEntity;
import com.slfinance.shanlincaifu.entity.TradeResultInfoEntity;
import com.slfinance.shanlincaifu.entity.UnbindInfoEntity;
import com.slfinance.shanlincaifu.entity.UserEntity;
import com.slfinance.shanlincaifu.entity.WealthHoldInfoEntity;
import com.slfinance.shanlincaifu.repository.AccountInfoRepository;
import com.slfinance.shanlincaifu.repository.AttachmentRepository;
import com.slfinance.shanlincaifu.repository.AuditInfoRepository;
import com.slfinance.shanlincaifu.repository.AuditInfoRespository;
import com.slfinance.shanlincaifu.repository.BankCardInfoRepository;
import com.slfinance.shanlincaifu.repository.CustApplyInfoRepository;
import com.slfinance.shanlincaifu.repository.CustInfoRepository;
import com.slfinance.shanlincaifu.repository.CustRecommendInfoRepository;
import com.slfinance.shanlincaifu.repository.DeviceInfoRepository;
import com.slfinance.shanlincaifu.repository.InvestInfoRepository;
import com.slfinance.shanlincaifu.repository.LogInfoEntityRepository;
import com.slfinance.shanlincaifu.repository.PosInfoRepository;
import com.slfinance.shanlincaifu.repository.TradeFlowInfoRepository;
import com.slfinance.shanlincaifu.repository.TradeResultInfoRepository;
import com.slfinance.shanlincaifu.repository.UnbindInfoRepository;
import com.slfinance.shanlincaifu.repository.UserRepository;
import com.slfinance.shanlincaifu.repository.WealthHoldInfoRepository;
import com.slfinance.shanlincaifu.repository.custom.CommissionInfoRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.CustApplyInfoRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.CustRecommendInfoRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.TradeFlowInfoRepositoryCustom;
import com.slfinance.shanlincaifu.service.CustAccountService;
import com.slfinance.shanlincaifu.service.CustManagerService;
import com.slfinance.shanlincaifu.service.FlowNumberService;
import com.slfinance.shanlincaifu.service.LoanManagerService;
import com.slfinance.shanlincaifu.service.ParamService;
import com.slfinance.shanlincaifu.service.SMSService;
import com.slfinance.shanlincaifu.service.SystemMessageService;
import com.slfinance.shanlincaifu.service.WealthInfoService;
import com.slfinance.shanlincaifu.utils.office.ExcelConstants;
import com.slfinance.shanlincaifu.utils.office.ExcelUtil;
import com.slfinance.spring.mail.MailType;
import com.slfinance.vo.ResultVo;

@Slf4j
@Service("custManagerService")
public class CustManagerServiceImpl implements CustManagerService {

	@Autowired
	private CustInfoRepository custInfoRepository;
	
	@Autowired
	private AccountInfoRepository accountInfoRepository;
	
	@Autowired
	private BankCardInfoRepository bankCardInfoRepository;
	
	@Autowired
	private UnbindInfoRepository unbindInfoRepository;
	
	@Autowired
	private FlowNumberService numberService;
	
	@Autowired
	private TradeFlowInfoRepository tradeFlowInfoRepository;
	
	@Autowired
	private AttachmentRepository attachmentRepository;
	
	@Autowired
	private AuditInfoRepository auditInfoRepository;
	
	@Autowired
	private LogInfoEntityRepository logInfoEntityRepository;
	
	@Autowired
	private CustApplyInfoRepository custApplyInfoRepository;
	
	@Autowired
	private CustApplyInfoRepositoryCustom custApplyInfoRepositoryCustom;
	
	@Autowired
	private CustRecommendInfoRepository custRecommendInfoRepository;
	
	@Autowired
	private CustRecommendInfoRepositoryCustom custRecommendInfoRepositoryCustom;
	
	@Autowired
	private TradeFlowInfoRepositoryCustom tradeFlowInfoRepositoryCustom;
	
	@Autowired
	private PosInfoRepository posInfoRepository;
	
	@Autowired
	private ParamService paramService;
	
	@Autowired
	private AuditInfoRespository auditInfoRespository;
	
	@Autowired
	private CustAccountService custAccountService;
	
	@Autowired
	DeviceInfoRepository deviceInfoRepository;
	
	@Autowired
	SMSService smsService; 
	
	@Autowired
	SystemMessageService systemMessageService;
	
	@Autowired
	@Qualifier("withdrawThreadPoolTaskExecutor")
	private Executor withdrawThreadPoolTaskExecutor;
	
	@Autowired
	private CommissionInfoRepositoryCustom commissionInfoRepositoryCustom;
	
	@Autowired
	private InvestInfoRepository investInfoRepository;
	
	@Autowired
	private LoanManagerService loanManagerService;
	
	@Autowired
	private WealthInfoService wealthInfoService;
	
	@Autowired
	private WealthHoldInfoRepository wealthHoldInfoRepository;
	
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo saveWealthRechargeByManager(Map<String, Object> param)
			throws SLException {
		// update by liyy @2016-07-04 Start
		// 充值金额不能小于1元
		BigDecimal amount = new BigDecimal(param.get("tradeAmount").toString());
		if(ArithUtil.compare(amount, new BigDecimal(1)) < 0){
			return new ResultVo(false, "充值金额不能小于1元");
		}
		// update by liyy @2016-07-04 End
		//验证客户是否有效
		String custId = (String) param.get("custId");
		CustInfoEntity custInfoEntity = custInfoRepository.findOne(custId);
		if(custInfoEntity == null){
			return new ResultVo(false, "客户不存在");
		}
		if(!Constant.ENABLE_STATUS_01.equals(custInfoEntity.getEnableStatus())){
			return new ResultVo(false, "账户为非正常状态，可能被冻结，请联系客服！");
		}
		
		//客户账户信息
		AccountInfoEntity accountInfoEntity = accountInfoRepository.findByCustId(custId);
		if(accountInfoEntity == null){
			return new ResultVo(false, "客户账户不存在");
		}
		
		//客户银行卡信息
		String bankId = (String) param.get("bankId");
		BankCardInfoEntity bankCardInfoEntity = bankCardInfoRepository.findOne(bankId);
		if(bankCardInfoEntity == null){
			return new ResultVo(false, "银行卡不存在");
		} 
		else if (Constant.VALID_STATUS_INVALID.equals(bankCardInfoEntity.getRecordStatus())) {
			return new ResultVo(false, "银行卡已作废");
		}
		else if (!bankCardInfoEntity.getCustInfoEntity().getId().equals(custId)) {
			return new ResultVo(false, "银行卡和客户不匹配");
		}
		
		//判断银行卡是否在解绑申请中
		List<UnbindInfoEntity> unbindList = unbindInfoRepository
				.findByRelatePrimaryAndUnbindStatus(bankId,
						Constant.TRADE_STATUS_02);
		if (unbindList != null && unbindList.size() > 0) {
			return new ResultVo(false, "您已经为该卡提交了解绑申请，暂时无法充值");
		}
		
		//是否有过程流水id
		String tradeFlowId = (String) param.get("tradeFlowId");
		//是否是新增--true 新增，false 表示修改
		boolean isNewSave = false;
		if(StringUtils.isEmpty(tradeFlowId)){//新增
			isNewSave = true;
		}
	
		//记录交易过程流水
		TradeFlowInfoEntity tradeFlowInfoEntity = new TradeFlowInfoEntity();
		if(!isNewSave){
			tradeFlowInfoEntity = tradeFlowInfoRepository.findOne(tradeFlowId);
			if(tradeFlowInfoEntity == null){
				return new ResultVo(false, "过程流水信息不存在");
			}
		} else {
			String tradeNo = numberService.generateTradeNumber();
			tradeFlowInfoEntity.setRelateType(Constant.TABLE_BAO_T_ACCOUNT_INFO);
			tradeFlowInfoEntity.setTradeType(SubjectConstant.TRADE_FLOW_TYPE_OFFLINE_RECHARGE);
			tradeFlowInfoEntity.setTradeNo(tradeNo);
		}
		
		String userId = (String) param.get("userId");
		String thirdPay = (String) param.get("thirdPay");
		BigDecimal tradeAmount = new BigDecimal(param.get("tradeAmount").toString());
		Date tradeDate = DateUtils.parseDate(param.get("tradeDate").toString(), "yyyy-MM-dd");//充值日期
		String appSource = (String) param.get("appSource"); // 来源
		
		tradeFlowInfoEntity.setTradeStatus(Constant.OFFLINE_RECHARGE_STATUS_UNPROCESS);
		tradeFlowInfoEntity.setRelatePrimary(accountInfoEntity.getId());
		tradeFlowInfoEntity.setThirdPay(thirdPay);
		tradeFlowInfoEntity.setCustId(custId);
		tradeFlowInfoEntity.setCustAccountId(accountInfoEntity.getId());
		tradeFlowInfoEntity.setBankName(bankCardInfoEntity.getBankName());
		tradeFlowInfoEntity.setBankCardNo(bankCardInfoEntity.getCardNo());
		tradeFlowInfoEntity.setOpenProvince(bankCardInfoEntity.getOpenProvince());
		tradeFlowInfoEntity.setOpenCity(bankCardInfoEntity.getOpenCity());
		tradeFlowInfoEntity.setBranchBankName(bankCardInfoEntity.getSubBranchName());
		tradeFlowInfoEntity.setTradeAmount(tradeAmount);
		tradeFlowInfoEntity.setTradeDate(tradeDate);
		tradeFlowInfoEntity.setTradeDesc(String.format("%s线下充值%s元申请", custInfoEntity.getLoginName(), tradeAmount));
		tradeFlowInfoEntity.setTradeSource(StringUtils.isEmpty(appSource) ? Constant.INVEST_SOURCE_PC
				: appSource);
		tradeFlowInfoEntity.setBasicModelProperty(userId, isNewSave);
		tradeFlowInfoEntity.setMemo(Constant.OPERATION_TYPE_42);
		tradeFlowInfoEntity.setTradeExpenses(BigDecimal.ZERO);
		tradeFlowInfoEntity = tradeFlowInfoRepository.save(tradeFlowInfoEntity);
		
		//记录pos信息 ，如修改，将原有的置为无效，然后重新保存
		if(!isNewSave){
			posInfoRepository.updateByTradeFlowId(tradeFlowInfoEntity.getId());
		}
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> posList = (List<Map<String, Object>>) param.get("posList");
		List<PosInfoEntity> posInfoEntities = Lists.newArrayList();
		for(Map<String, Object> pos : posList){
			String posNo = (String)  pos.get("posNo");
			String referenceNo = (String) pos.get("referenceNo");
			int count = posInfoRepository.countByPosNoAndReferenceNoAndRecordStatus(posNo, referenceNo, Constant.VALID_STATUS_VALID);
			if(count > 0){
				throw new SLException("终端号+参考号重复");
			}
			PosInfoEntity posInfoEntity = new PosInfoEntity();
			posInfoEntity.setTradeFlowId(tradeFlowInfoEntity.getId());
			posInfoEntity.setPosNo(posNo);
			posInfoEntity.setReferenceNo(referenceNo);
			posInfoEntity.setTradeStatus(Constant.OFFLINE_RECHARGE_POS_STATUS_UNPROCCESS);
			posInfoEntity.setBasicModelProperty(userId, true);
			posInfoEntities.add(posInfoEntity);
		}
		posInfoRepository.save(posInfoEntities);
		
		//记录附件信息
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> attachmentList = (List<Map<String, Object>>) param.get("attachmentList");
		if(!isNewSave && attachmentList != null && attachmentList.size() >= 0){
			attachmentRepository.updateByTypeAndPrimary(Constant.TABLE_BAO_T_TRADE_FLOW_INFO, tradeFlowInfoEntity.getId());
		}
		if(attachmentList != null && attachmentList.size() > 0){
			List<AttachmentInfoEntity> attachmentEntities = Lists.newArrayList();
			for(Map<String, Object> attachment : attachmentList){
				AttachmentInfoEntity attachmentEntity = new AttachmentInfoEntity();
				attachmentEntity.setRelateType(Constant.TABLE_BAO_T_TRADE_FLOW_INFO);
				attachmentEntity.setRelatePrimary(tradeFlowInfoEntity.getId());
				attachmentEntity.setAttachmentType((String) attachment.get("attachmentType"));
				attachmentEntity.setAttachmentName((String) attachment.get("attachmentName"));
				attachmentEntity.setStoragePath((String) attachment.get("storagePath"));
				attachmentEntity.setDocType((String) attachment.get("docType"));
				attachmentEntity.setBasicModelProperty(userId, true);
				attachmentEntities.add(attachmentEntity);
			}
			attachmentRepository.save(attachmentEntities);
		}
		
		//记录审核信息
		AuditInfoEntity auditInfoEntity = new AuditInfoEntity();
		String operBeforeContent = "";
		if(!isNewSave){
			auditInfoEntity = auditInfoRespository.findByRelatePrimary(tradeFlowInfoEntity.getId());
			if(auditInfoEntity == null){
				return new ResultVo(false, "审核信息不存在");
			}
			operBeforeContent = auditInfoEntity.getAuditStatus();
			auditInfoEntity.setAuditTime(new Timestamp(new Date().getTime()));
			auditInfoEntity.setAuditUser(userId);
		} else {
			auditInfoEntity.setRelateType(Constant.TABLE_BAO_T_TRADE_FLOW_INFO);
			auditInfoEntity.setApplyTime(new Timestamp(new Date().getTime()));
			auditInfoEntity.setApplyType(SubjectConstant.TRADE_FLOW_TYPE_OFFLINE_RECHARGE);
		}
		auditInfoEntity.setAuditStatus(Constant.OFFLINE_RECHARGE_AUDIT_STATUS_01);
		auditInfoEntity.setTradeStatus(Constant.OFFLINE_RECHARGE_STATUS_UNPROCESS);
		auditInfoEntity.setRelatePrimary(tradeFlowInfoEntity.getId());
		auditInfoEntity.setCustId(custInfoEntity.getId());
		auditInfoEntity.setTradeAmount(tradeAmount);
		auditInfoEntity.setBasicModelProperty(userId, isNewSave);
		auditInfoRespository.save(auditInfoEntity);
		
		//记录操作日志
		LogInfoEntity logInfoEntity = new LogInfoEntity();
		if(!isNewSave){
			logInfoEntity.setOperBeforeContent(operBeforeContent);
			logInfoEntity.setOperAfterContent(Constant.OFFLINE_RECHARGE_AUDIT_STATUS_01);
		}
		logInfoEntity.setRelateType(Constant.TABLE_BAO_T_TRADE_FLOW_INFO);
		logInfoEntity.setRelatePrimary(tradeFlowInfoEntity.getId());
		logInfoEntity.setLogType(Constant.LOG_TYPE_OFFLINE_RECHARGE_APPLY);
		logInfoEntity.setOperDesc(String.format("%s线下充值%s元申请", custInfoEntity.getLoginName(), tradeAmount));
		logInfoEntity.setOperPerson(custInfoEntity.getId());
		logInfoEntity.setBasicModelProperty(userId, true);
		logInfoEntityRepository.save(logInfoEntity);
		
		// 记录设备信息
		if(param.containsKey("channelNo")) {
			DeviceInfoEntity deviceInfoEntity = new DeviceInfoEntity();
			deviceInfoEntity.setRelateTableIdentification(Constant.TABLE_BAO_T_TRADE_FLOW_INFO);
			deviceInfoEntity.setRelatePrimary(tradeFlowInfoEntity.getId());
			deviceInfoEntity.setCustId(custId);
			deviceInfoEntity.setTradeType(SubjectConstant.TRADE_FLOW_TYPE_OFFLINE_RECHARGE);
			deviceInfoEntity.setMeId((String)param.get("meId"));
			deviceInfoEntity.setMeVersion((String)param.get("meVersion"));
			deviceInfoEntity.setAppSource(Strings.nullToEmpty((String)param.get("appSource")).toLowerCase());
			deviceInfoEntity.setChannelNo(paramService.findByChannelNo((String)param.get("channelNo")));
			deviceInfoEntity.setBasicModelProperty(userId, true);
			deviceInfoRepository.save(deviceInfoEntity);
		}
		
		return new ResultVo(true, "线下充值申请成功");
	}

	@Override
	public ResultVo queryWealthRechargeList(Map<String, Object> param) {
		Map<String, Object> resultMap = Maps.newHashMap();
		param.put("tradeType", SubjectConstant.TRADE_FLOW_TYPE_OFFLINE_RECHARGE);
		Page<Map<String, Object>> page = tradeFlowInfoRepositoryCustom.findWealthTradeFlowList(param);
		resultMap.put("iTotalDisplayRecords", page.getTotalElements());
		resultMap.put("data", page.getContent());
		return new ResultVo(true, "线下充值列表查询成功", resultMap);
	}

	@Override
	public ResultVo queryWealthRechargeDetailById(Map<String, Object> param) {
		String tradeFlowId = (String) param.get("tradeFlowId");
		param.put("tradeType", Constant.OPERATION_TYPE_42);
		Map<String, Object> resultMap = tradeFlowInfoRepositoryCustom.queryWealthTradeFlowDetailById(param);
		//终端列表
		List<Map<String, Object>> posList = posInfoRepository.findListByTradeFlowIdAndRecordStatus(tradeFlowId, Constant.VALID_STATUS_VALID);
		//附件列表
		List<Map<String, Object>> attachmentList = attachmentRepository
				.findMapByRelatePrimaryAndRecordStatus(tradeFlowId, Constant.VALID_STATUS_VALID);
		//审核列表
		List<Map<String, Object>> auditList = logInfoEntityRepository.findListByRelatePrimaryAndLogType(tradeFlowId, Constant.LOG_TYPE_OFFLINE_RECHARGE_AUDIT);
		
		resultMap.put("posList", posList);
		resultMap.put("attachmentList", attachmentList);
		resultMap.put("auditList", auditList);
		
		return new ResultVo(true, "线下充值详情查询成功", resultMap);
	}

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo auditWealthRechargeFirst(Map<String, Object> param)
			throws SLException {
		String tradeFlowId = (String) param.get("tradeFlowId");
		String auditStatus = (String)  param.get("auditStatus");
		String auditMemo = (String)  param.get("auditMemo");
		String userId = (String)  param.get("userId");
		
		//验证交易过程流水是否有效
		TradeFlowInfoEntity tradeFlowInfoEntity = tradeFlowInfoRepository.findOne(tradeFlowId);
		if(tradeFlowInfoEntity == null){
			return new ResultVo(false, "交易流水不存在");
		}
		
		//审核信息
		AuditInfoEntity auditInfoEntity = auditInfoRespository.findByRelatePrimary(tradeFlowId);
		String operBeforeContent = auditInfoEntity.getAuditStatus();
		if(!Constant.OFFLINE_RECHARGE_AUDIT_STATUS_01.equals(operBeforeContent)){
			return new ResultVo(false, "不是待审核状态，不能审核");
		}
		String tradeStatus = Constant.OFFLINE_RECHARGE_STATUS_FAIL;
		if(Constant.OFFLINE_RECHARGE_AUDIT_STATUS_04.equals(auditStatus)){
			tradeStatus = Constant.OFFLINE_RECHARGE_STATUS_PROCESSING;
		}
		
		//初审拒绝，将pos处理状态置为结算失败
		// update by liyy @2016-04-12 
//		if(Constant.OFFLINE_RECHARGE_AUDIT_STATUS_06.equals(auditStatus) && !"富友".equals(tradeFlowInfoEntity.getThirdPay())){
		if(Constant.OFFLINE_RECHARGE_AUDIT_STATUS_06.equals(auditStatus)){
			List<PosInfoEntity> posInfoEntities = posInfoRepository.findByTradeFlowIdAndRecordStatus(tradeFlowId, Constant.VALID_STATUS_VALID);
//			if(posInfoEntities == null || posInfoEntities.size() <= 0){
//				log.info("未找到pos信息:流水ID" + tradeFlowId);
//				throw new SLException("未找到pos信息");
//			}
			if(posInfoEntities != null && posInfoEntities.size() > 0){
				for(PosInfoEntity posInfoEntity : posInfoEntities){
					posInfoEntity.setTradeStatus(Constant.OFFLINE_RECHARGE_POS_STATUS_FAIL);
					posInfoEntity.setBasicModelProperty(userId, false);
				}
			}
		}
		
		//修改交易流水
		tradeFlowInfoEntity.setTradeStatus(tradeStatus);
		tradeFlowInfoEntity.setBasicModelProperty(userId, false);
		
		//修改审核信息
		auditInfoEntity.setAuditStatus(auditStatus);
		auditInfoEntity.setAuditTime(new Timestamp(new Date().getTime()));
		auditInfoEntity.setAuditUser(userId);
		auditInfoEntity.setTradeStatus(tradeStatus);
		auditInfoEntity.setMemo(auditMemo);
		auditInfoEntity.setBasicModelProperty(userId, false);
		
		//日志
		LogInfoEntity logInfoEntity = new LogInfoEntity();
		logInfoEntity.setRelateType(Constant.TABLE_BAO_T_TRADE_FLOW_INFO);
		logInfoEntity.setRelatePrimary(tradeFlowInfoEntity.getId());
		logInfoEntity.setLogType(Constant.LOG_TYPE_OFFLINE_RECHARGE_AUDIT);
		logInfoEntity.setOperPerson(userId);
		logInfoEntity.setOperBeforeContent(operBeforeContent);
		logInfoEntity.setOperAfterContent(auditStatus);
		logInfoEntity.setOperDesc(String.format("线下充值初审结果%s", tradeStatus));
		logInfoEntity.setMemo(auditMemo);
		logInfoEntity.setBasicModelProperty(userId, true);
		logInfoEntityRepository.save(logInfoEntity);
		
		//站内信(初审拒绝、初审回退，发送站内信)
		if (auditStatus.equals(Constant.OFFLINE_RECHARGE_AUDIT_STATUS_06) || auditStatus.equals(Constant.OFFLINE_RECHARGE_AUDIT_STATUS_02)) {
			sendSysRechargeFail(auditInfoEntity.getCustId(), DateUtils.formatDate(auditInfoEntity.getCreateDate(), "yyyy-MM-dd"), auditMemo, auditStatus);
		}
		
		return new ResultVo(true, "线下充值初审成功");
	}
	
	/**
	 * 线下充值复审
	 * @author liyy
	 * @date   2016年4月5日
	 * @param param
	 *      <tt>tradeFlowId:String:交易过程ID</tt><br>
     *      <tt>auditStatus:String:审核状态</tt><br>
     *      <tt>auditMemo  :String: 审核备注</tt><br>
     *      <tt>userId     :String:创建人</tt><br>
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo auditWealthRechargeSecond(Map<String, Object> param) throws SLException {
		String tradeFlowId = (String) param.get("tradeFlowId");
		String auditStatus = (String)  param.get("auditStatus");
		String auditMemo = (String)  param.get("auditMemo");
		String userId = (String)  param.get("userId");
		
		//交易流水是否存在
		TradeFlowInfoEntity tradeFlowInfoEntity = tradeFlowInfoRepository.findOne(tradeFlowId);
		if(tradeFlowInfoEntity == null){
			return new ResultVo(false, "交易流水不存在");
		}
		
		//判断过程流水的审核状态是不是初审通过
		AuditInfoEntity auditInfoEntity = auditInfoRespository.findByRelatePrimary(tradeFlowId);
		String operBeforeContent = auditInfoEntity.getAuditStatus();
		if(!Constant.OFFLINE_RECHARGE_AUDIT_STATUS_04.equals(auditInfoEntity.getAuditStatus())){
			return new ResultVo(false, "不是初审通过状态，不能审核");
		}
		
		//修改过程流水信息
		String tradeStatus = Constant.OFFLINE_RECHARGE_STATUS_FAIL;
		if(Constant.OFFLINE_RECHARGE_AUDIT_STATUS_09.equals(auditStatus)){
			tradeStatus = Constant.OFFLINE_RECHARGE_STATUS_SUCCESS;
		}
		
		//复审拒绝，将pos处理状态置为结算失败
		// update by liyy @2016-04-12 
//		if(Constant.OFFLINE_RECHARGE_AUDIT_STATUS_10.equals(auditStatus) && !"富友".equals(tradeFlowInfoEntity.getThirdPay())){
		if(Constant.OFFLINE_RECHARGE_AUDIT_STATUS_10.equals(auditStatus)){
			List<PosInfoEntity> posInfoEntities = posInfoRepository.findByTradeFlowIdAndRecordStatus(tradeFlowId, Constant.VALID_STATUS_VALID);
//			if(posInfoEntities == null || posInfoEntities.size() <= 0){
//				log.info("未找到pos信息:流水ID" + tradeFlowId);
//				throw new SLException("未找到pos信息");
//			}
			if(posInfoEntities != null && posInfoEntities.size() > 0){
				for(PosInfoEntity posInfoEntity : posInfoEntities){
					posInfoEntity.setTradeStatus(Constant.OFFLINE_RECHARGE_POS_STATUS_FAIL);
					posInfoEntity.setBasicModelProperty(userId, false);
				}
			}
			
		}
		
		tradeFlowInfoEntity.setTradeStatus(tradeStatus);
		tradeFlowInfoEntity.setBasicModelProperty(userId, false);
		
		//修改审核
		auditInfoEntity.setAuditStatus(auditStatus);
		auditInfoEntity.setAuditTime(new Timestamp(new Date().getTime()));
		auditInfoEntity.setAuditUser(userId);
		auditInfoEntity.setTradeStatus(tradeStatus);
		auditInfoEntity.setMemo(auditMemo);
		auditInfoEntity.setBasicModelProperty(userId, false);
		
		//记录日志
		LogInfoEntity logInfoEntity = new LogInfoEntity();
		logInfoEntity.setRelateType(Constant.TABLE_BAO_T_TRADE_FLOW_INFO);
		logInfoEntity.setRelatePrimary(tradeFlowInfoEntity.getId());
		logInfoEntity.setLogType(Constant.LOG_TYPE_OFFLINE_RECHARGE_AUDIT);
		logInfoEntity.setOperPerson(userId);
		logInfoEntity.setOperBeforeContent(operBeforeContent);
		logInfoEntity.setOperAfterContent(auditStatus);
		logInfoEntity.setOperDesc(String.format("线下充值复审结果%s", auditStatus));
		logInfoEntity.setMemo(auditMemo);
		logInfoEntity.setBasicModelProperty(userId, true);
		logInfoEntityRepository.save(logInfoEntity);
		
		if(Constant.OFFLINE_RECHARGE_AUDIT_STATUS_10.equals(auditStatus)
				||Constant.OFFLINE_RECHARGE_AUDIT_STATUS_08.equals(auditStatus)) {
			//站内信
			sendSysRechargeFail(auditInfoEntity.getCustId(), DateUtils.formatDate(auditInfoEntity.getCreateDate(), "yyyy-MM-dd"), auditMemo, auditStatus);
		}
		
		return new ResultVo(true, "线下充值复审成功");
	}

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo auditWealthRechargeLast(Map<String, Object> param)
			throws SLException {
		String tradeFlowId = (String) param.get("tradeFlowId");
		String auditStatus = (String)  param.get("auditStatus");
		String auditMemo = (String)  param.get("auditMemo");
		String userId = (String)  param.get("userId");
		
		//交易流水是否存在
		TradeFlowInfoEntity tradeFlowInfoEntity = tradeFlowInfoRepository.findOne(tradeFlowId);
		if(tradeFlowInfoEntity == null){
			return new ResultVo(false, "交易流水不存在");
		}
		
		// @2016/4/5 by liyy Start
		// 判断过程流水的审核状态是不是初审通过   --> 判断过程流水的审核状态是不是复审通过 
		AuditInfoEntity auditInfoEntity = auditInfoRespository.findByRelatePrimary(tradeFlowId);
		String operBeforeContent = auditInfoEntity.getAuditStatus();
//		if(!Constant.OFFLINE_RECHARGE_AUDIT_STATUS_04.equals(auditInfoEntity.getAuditStatus())){
//			return new ResultVo(false, "不是初审通过状态，不能审核");
//		}
		if (!Constant.OFFLINE_RECHARGE_AUDIT_STATUS_09.equals(auditInfoEntity.getAuditStatus())) {
			return new ResultVo(false, "不是复审通过状态，不能审核");
		}
		// @2016/4/5 by liyy End
		
		//修改过程流水信息
		String tradeStatus = Constant.OFFLINE_RECHARGE_STATUS_FAIL;
		if(Constant.OFFLINE_RECHARGE_AUDIT_STATUS_05.equals(auditStatus)){
			tradeStatus = Constant.OFFLINE_RECHARGE_STATUS_SUCCESS;
		}
		tradeFlowInfoEntity.setTradeStatus(tradeStatus);
		tradeFlowInfoEntity.setBasicModelProperty(userId, false);
		
		//修改审核
		auditInfoEntity.setAuditStatus(auditStatus);
		auditInfoEntity.setAuditTime(new Timestamp(new Date().getTime()));
		auditInfoEntity.setAuditUser(userId);
		auditInfoEntity.setTradeStatus(tradeStatus);
		auditInfoEntity.setMemo(auditMemo);
		auditInfoEntity.setBasicModelProperty(userId, false);
		
		if(Constant.OFFLINE_RECHARGE_AUDIT_STATUS_05.equals(auditStatus)){
			
			String requestNo = numberService.generateTradeBatchNumber();
			AccountInfoEntity custAccount = accountInfoRepository.findByCustId(tradeFlowInfoEntity.getCustId());
			if(custAccount == null){
				log.info("未找到客户账户信息:客户ID" + tradeFlowInfoEntity.getCustId());
				throw new SLException("未找到客户账户信息");
			}
			
			custAccountService.updateAccount(custAccount, null, null, null, "5", SubjectConstant.TRADE_FLOW_TYPE_OFFLINE_RECHARGE,
					requestNo, tradeFlowInfoEntity.getTradeAmount(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_OFFLINE_RECHARGE, Constant.TABLE_BAO_T_ACCOUNT_INFO, custAccount.getId(), "线下充值", userId);
			
			CustInfoEntity custInfo = custInfoRepository.findOne(tradeFlowInfoEntity.getCustId());
			if(custInfo == null){
				log.info("未找到客户信息:客户ID" + tradeFlowInfoEntity.getCustId());
				throw new SLException("未找到客户信息");
			}
			if(custInfo.getCustType() == null){
				custInfo.setCustType(Constant.CUST_TYPE_OFFLINE);
			}
			
			//修改pos状态
			List<PosInfoEntity> posInfoEntities = posInfoRepository.findByTradeFlowIdAndRecordStatus(tradeFlowId, Constant.VALID_STATUS_VALID);
			// update by liyy @2016-04-12 
//			if(!"富友".equals(tradeFlowInfoEntity.getThirdPay())){
//				if(posInfoEntities == null || posInfoEntities.size() <= 0){
//					log.info("未找到pos信息:流水ID" + tradeFlowId);
//					throw new SLException("未找到pos信息");
//				}
			if(posInfoEntities != null && posInfoEntities.size() > 0){
				for(PosInfoEntity posInfoEntity : posInfoEntities){
					posInfoEntity.setTradeStatus(Constant.OFFLINE_RECHARGE_POS_STATUS_SUCCESS);
					posInfoEntity.setBasicModelProperty(userId, false);
				}	
			}
			
			//异步发送短信
			sendSms(custInfo.getId(), custInfo.getMobile(), Constant.SMS_TYPE_OFFLINE_RECHARGE, custAccount.getAccountNo(), ArithUtil.formatScale(tradeFlowInfoEntity.getTradeAmount(), 2).toPlainString(), new Date());
		}
		
		//终审拒绝，将pos处理状态置为结算失败
		// update by liyy @2016-04-12 
//		if(Constant.OFFLINE_RECHARGE_AUDIT_STATUS_07.equals(auditStatus) && !"富友".equals(tradeFlowInfoEntity.getThirdPay())){
		if(Constant.OFFLINE_RECHARGE_AUDIT_STATUS_07.equals(auditStatus)){
			List<PosInfoEntity> posInfoEntities = posInfoRepository.findByTradeFlowIdAndRecordStatus(tradeFlowId, Constant.VALID_STATUS_VALID);
//			if(posInfoEntities == null || posInfoEntities.size() <= 0){
//				log.info("未找到pos信息:流水ID" + tradeFlowId);
//				throw new SLException("未找到pos信息");
//			}
			if(posInfoEntities != null && posInfoEntities.size() > 0){
				for(PosInfoEntity posInfoEntity : posInfoEntities){
					posInfoEntity.setTradeStatus(Constant.OFFLINE_RECHARGE_POS_STATUS_FAIL);
					posInfoEntity.setBasicModelProperty(userId, false);
				}
			}
		}
		
		if(Constant.OFFLINE_RECHARGE_AUDIT_STATUS_07.equals(auditStatus)
				||Constant.OFFLINE_RECHARGE_AUDIT_STATUS_03.equals(auditStatus)) {
			//站内信
			sendSysRechargeFail(auditInfoEntity.getCustId(), DateUtils.formatDate(auditInfoEntity.getCreateDate(), "yyyy-MM-dd"), auditMemo, auditStatus);
		}
		
		//记录日志
		LogInfoEntity logInfoEntity = new LogInfoEntity();
		logInfoEntity.setRelateType(Constant.TABLE_BAO_T_TRADE_FLOW_INFO);
		logInfoEntity.setRelatePrimary(tradeFlowInfoEntity.getId());
		logInfoEntity.setLogType(Constant.LOG_TYPE_OFFLINE_RECHARGE_AUDIT);
		logInfoEntity.setOperPerson(userId);
		logInfoEntity.setOperBeforeContent(operBeforeContent);
		logInfoEntity.setOperAfterContent(auditStatus);
		logInfoEntity.setOperDesc(String.format("线下充值终审结果%s", auditStatus));
		logInfoEntity.setMemo(auditMemo);
		logInfoEntity.setBasicModelProperty(userId, true);
		logInfoEntityRepository.save(logInfoEntity);
		return new ResultVo(true, "线下充值终审成功");
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
	private void sendSysRechargeFail(String custId, String date, String errMessage, String auditStatus) {
		Map<String,Object> sysParams = Maps.newHashMap();
		sysParams.put("messageType", Constant.SYS_TYPE_OFFLINE_RECHARGE_FAIL);
		sysParams.put("custId", custId);
		sysParams.put("systemMessage", new Object[] { // 站内信内容
				date,
				auditStatus,
				errMessage,
				Constant.PLATFORM_SERVICE_TEL});
		//做异步发送邮件处理,短信服务已经做了异步处理
		try {
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
	 * 
	 */
	private void sendSms(String custId,String mobile,String messageType, String accountNo, String tradeMount,Date sendDate) {
		Map<String,Object> smsParams = Maps.newHashMap();
		smsParams.put("custId", custId);
		smsParams.put("mobile", mobile);
		smsParams.put("messageType", messageType);
		smsParams.put("values", new Object[] { // 短信息内容
				mobile.substring(0, mobile.length()-(mobile.substring(3)).length())+"****"+mobile.substring(7),
				DateUtils.formatDate(sendDate, "MM"),
				DateUtils.formatDate(sendDate, "dd"),
				tradeMount
		});
//		//做异步发送邮件处理,短信服务已经做了异步处理
		try {
			smsService.asnySendSMS(smsParams);
		} catch (Exception e) {
			log.info(e.getCause().toString());
		}
	}

	@Override
	public ResultVo queryWealthWithDrawList(Map<String, Object> param) {
		Map<String, Object> resultMap = Maps.newHashMap();
		param.put("tradeType", SubjectConstant.TRADE_FLOW_TYPE_OFFLINE_WITHDRAW);
		Page<Map<String, Object>> page = tradeFlowInfoRepositoryCustom.findWealthTradeFlowList(param);
		resultMap.put("iTotalDisplayRecords", page.getTotalElements());
		resultMap.put("data", page.getContent());
		return new ResultVo(true, "线下提现列表查询成功", resultMap);
	}

	@Override
	public ResultVo exportWealthWithDraw(Map<String, Object> param)
			throws SLException {
		Map<String, Object> resultMap = Maps.newHashMap();
		//付款人
		CustInfoEntity payer = custInfoRepository.findOne(Constant.CUST_ID_WEALTH_CENTER);
		if(payer == null){
			return new ResultVo(false, "未查询到付款人信息");
		}
		//付款账号
		BankCardInfoEntity payerBankCard = bankCardInfoRepository.findOne(Constant.BANK_ID_WEALTH_CENTER);
		if(payerBankCard == null){
			return new ResultVo(false, "未查询到付款银行卡信息");
		}
		
		if(!payer.getId().equals(payerBankCard.getCustInfoEntity().getId())){
			return new ResultVo(false, "付款银行卡与付款人不匹配");
		}
		param.put("tradeType", SubjectConstant.TRADE_FLOW_TYPE_OFFLINE_WITHDRAW);
		param.put("tradeStatus", Constant.OFFLINE_WITHDRAW_STATUS_UNPROCESS);
		param.put("payerBankNo", payerBankCard.getCardNo());
		param.put("payerCustName", payer.getCustName());
		Page<Map<String, Object>> page = tradeFlowInfoRepositoryCustom.queryExportWealthWithDraw(param);
		resultMap.put("iTotalDisplayRecords", page.getTotalElements());
		resultMap.put("data", page.getContent());
		return new ResultVo(true, "导出数据成功", resultMap);
	}
	
	
	@Override
	public ResultVo importWealthWithDraw(final Map<String, Object> param)
			throws SLException {
		withdrawThreadPoolTaskExecutor.execute(new Runnable() {
			
			@Override
			public void run() {
				try {
					internalCustManagerService.importWealthWithDraw(param);
				} catch (SLException e) {
					log.error("执行线下提现批量导入处理任务出错");
					e.printStackTrace();
				}
				
			}
		});
		
		return new ResultVo(true, "线下提现提现批量导入处理完成");
	}

	@Override
	public ResultVo queryWealthWithDrawDetailById(Map<String, Object> param) {
		String tradeFlowId = (String) param.get("tradeFlowId");
		param.put("tradeType", Constant.OPERATION_TYPE_43);
		Map<String, Object> resultMap = tradeFlowInfoRepositoryCustom.queryWealthTradeFlowDetailById(param);
		//审核列表
		List<Map<String, Object>> auditList = logInfoEntityRepository.findListByRelatePrimaryAndLogType(tradeFlowId, Constant.LOG_TYPE_OFFLINE_WITHDRAW_AUDIT);
		resultMap.put("auditList", auditList);
		return new ResultVo(true, "查询线下提现详情成功", resultMap);
	}

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo auditWealthWithDraw(Map<String, Object> param) throws SLException {
		String tradeFlowId = (String) param.get("tradeFlowId");
		String userId = (String) param.get("userId");
		
		//查询过程流水
		TradeFlowInfoEntity tradeFlowInfoEntity = tradeFlowInfoRepository.findOne(tradeFlowId);
		if(tradeFlowInfoEntity == null){
			return new ResultVo(false, "交易过程不存在");
		}
		
		//查询审核信息
		AuditInfoEntity auditInfoEntity = auditInfoRespository.findOne(tradeFlowInfoEntity.getRelatePrimary());
		if(auditInfoEntity == null){
			return new ResultVo(false, "审核信息不存在");
		}
		String operBeforeContent = auditInfoEntity.getAuditStatus();
		// @2016/4/6 add by liyy 条件追加 !Constant.TRADE_STATUS_02.equals(tradeFlowInfoEntity.getTradeStatus())
		if(!Constant.TRADE_STATUS_01.equals(tradeFlowInfoEntity.getTradeStatus()) && !Constant.TRADE_STATUS_02.equals(tradeFlowInfoEntity.getTradeStatus())){
			return new ResultVo(false, "不是未处理或处理中的状态，不能处理");
		}
		
		//审核结果
		String auditStatus = (String) param.get("auditStatus");
		String tradeStatus = Constant.TRADE_STATUS_04;
		if(Constant.AUDIT_STATUS_PASS.equals(auditStatus)){
			tradeStatus = Constant.TRADE_STATUS_03;
		}
		
		//更新流水过程
		tradeFlowInfoEntity.setTradeStatus(tradeStatus);
		tradeFlowInfoEntity.setBasicModelProperty(userId, false);
		
		//更新审核信息
		String auditMemo = (String) param.get("auditMemo");
		auditInfoEntity.setAuditTime(new Timestamp(new Date().getTime()));
		auditInfoEntity.setAuditUser(userId);
		auditInfoEntity.setAuditStatus(auditStatus);
		auditInfoEntity.setTradeStatus(tradeStatus);
		auditInfoEntity.setMemo(auditMemo);
		auditInfoEntity.setBasicModelProperty(userId, false);
		
		String requestNo = numberService.generateTradeBatchNumber();
		AccountInfoEntity custAccount = accountInfoRepository.findByCustId(tradeFlowInfoEntity.getCustId());
		if(custAccount == null){
			log.info("未找到客户账户信息:客户ID" + tradeFlowInfoEntity.getCustId());
			throw new SLException("未找到客户账户信息");
		}
		//更新账户
		if(Constant.AUDIT_STATUS_PASS.equals(auditStatus)){
			//用户账户解冻
			custAccountService.updateAccount(custAccount, null, null, null, "8", SubjectConstant.TRADE_FLOW_TYPE_OFFLINE_UNFREEZE, requestNo,
					tradeFlowInfoEntity.getTradeAmount(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_OFFLINE_UNFREEZE_WITHDRAW, Constant.TABLE_BAO_T_ACCOUNT_INFO, custAccount.getId(), "线下提现解冻", userId);
			//用户账户提现
			custAccountService.updateAccount(custAccount, null, null, null, "6", SubjectConstant.TRADE_FLOW_TYPE_OFFLINE_WITHDRAW,
					requestNo, tradeFlowInfoEntity.getTradeAmount(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_OFFLINE_WITHDRAW, Constant.TABLE_BAO_T_ACCOUNT_INFO, custAccount.getId(), "线下提现", userId);
		} else {
			//用户账户账户解冻
			custAccountService.updateAccount(custAccount, null, null, null, "8", SubjectConstant.TRADE_FLOW_TYPE_OFFLINE_UNFREEZE, requestNo,
					tradeFlowInfoEntity.getTradeAmount(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_OFFLINE_UNFREEZE_WITHDRAW, Constant.TABLE_BAO_T_ACCOUNT_INFO, custAccount.getId(), "线下提现解冻", userId);
		}
		
		//记录日志信息
		LogInfoEntity logInfoEntity = new LogInfoEntity();
		logInfoEntity.setRelateType(Constant.TABLE_BAO_T_TRADE_FLOW_INFO);
		logInfoEntity.setRelatePrimary(tradeFlowId);
		logInfoEntity.setLogType(Constant.LOG_TYPE_OFFLINE_WITHDRAW_AUDIT);
		logInfoEntity.setOperDesc(String.format("线下提现处理结果：%s", tradeStatus));
		logInfoEntity.setOperPerson(userId);
		logInfoEntity.setOperBeforeContent(operBeforeContent);
		logInfoEntity.setOperAfterContent(auditStatus);
		logInfoEntity.setMemo(auditMemo);
		logInfoEntity.setBasicModelProperty(userId, true);
		logInfoEntityRepository.save(logInfoEntity);
		
		return new ResultVo(true, "线下提现处理完成");
	}
	/**
	 * 线下业务-客户转移-客户转移列表（部门额相关）
	 *
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param param
     *      <tt>start           :String:起始值</tt><br>
     *      <tt>length          :String:长度</tt><br>
     *      <tt>userId          :String:客户经理ID</tt><br>
     *      <tt>loginName      :String:用户名（可选）</tt><br>
     *      <tt>custName        :String:用户名（可选）</tt><br>
     *      <tt>credentialsCode :String:证件号码(可选)</tt><br>
     *      <tt>mobile          :String:手机号（可选）</tt><br>
     *      <tt>auditStatus     :String:审核状态（可选）</tt><br>
     *      <tt>custId         :String:客户ID（可选）</tt><br>
     *      <tt>beginOperateDate:String:开始创建时间（可选）</tt><br>
     *      <tt>endOperateDate  :String:结束创建时间（可选）</tt><br>
	 * @return ResultVo
	 * 		<tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
     *      <tt>data:Objcet
     *      	<tt>iTotalDisplayRecords:String:总数</tt><br>
     *      	<tt>data:List
     *      		<tt>custApplyId    :String:客户申请ID</tt><br>
     *      		<tt>loginName      :String:用户名</tt><br>
     *      		<tt>custName       :String:客户姓名</tt><br>
     *      		<tt>credentialsCode:String:证件号码</tt><br>
     *      		<tt>mobile         :String:手机号</tt><br>
     *      		<tt>createUser     :String:操作人</tt><br>
     *      		<tt>createDate     :String:操作时间</tt><br>
     *      		<tt>registerDate   :String:注册时间</tt><br>
     *      		<tt>newCustManager :String:新业务员</tt><br>
     *      		<tt>auditStatus    :String:审核状态</tt><br>
     *          </tt><br>
     *      </tt><br>
     *  @throws SLException
	 */
	public ResultVo queryAllCustTransferList(Map<String, Object> param) throws SLException{
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			// udpate @2016/4/8 by liyy Start
			// old // Page<Map<String, Object>> page = custApplyInfoRepositoryCustom.queryAllCustTransferList(param);
			List<AgentEntity> list = custApplyInfoRepositoryCustom.getAgentEntityByUserId(param.get("userId").toString());
			if(list == null || list.size() == 0) {
				log.warn("客户转移列表查询失败:未查询的您的agent信息");
				return new ResultVo(false, "客户转移列表查询失败:未查询的您的agent信息");
			}
			AgentEntity agentEntity = list.get(0);
			String dataPermission = agentEntity.getDataPermission();
			
			Page<Map<String, Object>> page = custApplyInfoRepositoryCustom.queryAllCustTransferListNew(param, dataPermission);
			
			// udpate @2016/4/8 by liyy End
			
			data.put("iTotalDisplayRecords", page.getTotalElements());
			data.put("data", page.getContent());
			return new ResultVo(true, "客户转移列表查询成功",data);
		} catch (Exception e) {
			log.error("客户转移列表查询失败" + e.getMessage());
			return new ResultVo(false, "客户转移列表查询失败");
		}
	}
	
	/**
	 * 前台-客户转移-客户转移列表
	 *
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param param
     *      <tt>start          :String:起始值</tt><br>
     *      <tt>length         :String:长度</tt><br>
     *      <tt>loginName      :String:用户名（可选）</tt><br>
     *      <tt>custName       :String:客户姓名（可选）</tt><br>
     *      <tt>credentialsCode:String:证件号码(可选)</tt><br>
     *      <tt>mobile         :String:手机号（可选）</tt><br>
     *      <tt>auditStatus    :String:审核状态（可选）</tt><br>
     *      <tt>custId         :String:客户ID（可选）</tt><br>
     *      <tt>userId          :String:客户经理ID（可选）</tt><br>
     *      <tt>beginOperateDate:String:开始创建时间（可选）</tt><br>
     *      <tt>endOperateDate  :String:结束创建时间（可选）</tt><br>
	 * @return ResultVo
	 * 		<tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
     *      <tt>data:Objcet
     *      	<tt>iTotalDisplayRecords:String:总数</tt><br>
     *      	<tt>data:List
     *      		<tt>custApplyId    :String:客户申请ID</tt><br>
     *      		<tt>loginName      :String:用户名</tt><br>
     *      		<tt>custName       :String:客户姓名</tt><br>
     *      		<tt>credentialsCode:String:证件号码</tt><br>
     *      		<tt>mobile         :String:手机号</tt><br>
     *      		<tt>createUser     :String:操作人</tt><br>
     *      		<tt>createDate     :String:操作时间</tt><br>
     *      		<tt>registerDate   :String:注册时间</tt><br>
     *      		<tt>newCustManager :String:新业务员</tt><br>
     *      		<tt>auditStatus    :String:审核状态</tt><br>
     *          </tt><br>
     *      </tt><br>
     *  @throws SLException
	 */
	public ResultVo queryCustTransferList(Map<String, Object> param) throws SLException {
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			Page<Map<String, Object>> page = custApplyInfoRepositoryCustom.queryCustTransferList(param);
			
			data.put("iTotalDisplayRecords", page.getTotalElements());
			data.put("data", page.getContent());
			return new ResultVo(true, "客户转移列表查询成功",data);
		} catch (Exception e) {
			log.error("客户转移列表查询失败" + e.getMessage());
			return new ResultVo(false, "客户转移列表查询失败");
		}
	}

	/**
	 * 线下业务-客户转移-客户转移查看详情
	 *
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param param
     *      <tt>custApplyId：客户申请ID</tt><br>
	 * @return ResultVo
	 * 		<tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
     *      <tt>data
     *      	<tt>custApplyId                  :String:客户申请ID</tt><br>
     *      	<tt>custId                       :String:用户ID</tt><br>
     *      	<tt>loginName                    :String:用户名</tt><br>
     *      	<tt>custName                     :String:客户姓名</tt><br>
     *      	<tt>credentialsCode              :String:证件号码</tt><br>
     *      	<tt>mobile                       :String:手机号</tt><br>
     *      	<tt>registerDate                 :String:注册时间</tt><br>
     *      	<tt>oldCustManagerId             :String:原客户经理主键</tt><br>
     *      	<tt>oldCustManagerName           :String:原客户经理名称</tt><br>
     *      	<tt>oldCustManagerMobile         :String:原客户经理手机号</tt><br>
     *      	<tt>oldCustManagerCredentialsCode:String:原客户经理身份证号</tt><br>
     *      	<tt>newCustManagerId             :String:新客户经理主键</tt><br>
     *      	<tt>newCustManagerName           :String:新客户经理名称</tt><br>
     *      	<tt>newCustManagerMobile         :String:新客户经理手机号</tt><br>
     *      	<tt>newCustManagerCredentialsCode:String:新客户经理身份证号</tt><br>
     *      	<tt>attachmentList:List<Map<String, Object>>:附件列表
     *     			<tt>attachmentType:String:         附件类型</tt><br>
     *      		<tt>attachmentName:String:         附件名称</tt><br>
     *      		<tt>storagePath   :String:         存储路径</tt><br>
     *      	</tt><br>
     *     		<tt>auditList:List<Map<String, Object>>:审核日志列表
     *      		<tt>auditId    :String:审核ID</tt><br>
     *      		<tt>auditDate  :String:审核时间</tt><br>
     *      		<tt>auditUser  :String:审核人员</tt><br>
     *      		<tt>auditStatus:String:审核结果</tt><br>
     *      		<tt>auditMemo  :String:审核备注</tt><br>
     *      	</tt><br>
     *      </tt><br>
     *  @throws SLException
	 */
	public ResultVo queryCustTransferDetailById(Map<String, Object> param) throws SLException {
		try {
			Map<String, Object> data = null;
			// 基本信息
			List<Map<String, Object>> list = custApplyInfoRepositoryCustom.queryCustTransferDetailById(param);
			if(list == null || list.size() == 0){
				return new ResultVo(false, "客户转移查看详情查询失败, 数据为空");
			}
			data = list.get(0);
			// 附件列表
			List<Map<String, Object>> attachmentList 
				= custApplyInfoRepositoryCustom.queryAttachmentInfoListById(Constant.TABLE_BAO_T_CUST_APPLY_INFO, data.get("custApplyId").toString());
			// 审核日志列表
			List<Map<String, Object>> auditList 
				= custApplyInfoRepositoryCustom.queryAuditInfoListById(Constant.OPERATION_TYPE_56, Constant.TABLE_BAO_T_CUST_APPLY_INFO, data.get("custApplyId").toString());
			
			data.put("attachmentList", attachmentList);
			data.put("auditList", auditList);
			return new ResultVo(true, "客户转移查看详情查询成功", data);
		} catch (Exception e) {
			log.error("客户转移查看详情查询失败" + e.getMessage());
			return new ResultVo(false, "客户转移查看详情查询失败");
		}
	}
	
	/**
	 * 线下业务-客户转移-客户转移保存 新建/编辑
	 *
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param param
     *      <tt>custApplyId                  :String:客户申请ID（编辑时非空）</tt><br>
     *      <tt>custId                       :String:用户ID</tt><br>
     *      <tt>oldCustManagerId             :String:原客户经理主键</tt><br>
     *      <tt>oldCustManagerName           :String:原客户经理名称</tt><br>
     *      <tt>oldCustManagerMobile         :String:原客户经理手机号</tt><br>
     *      <tt>oldCustManagerCredentialsCode:String:原客户经理身份证号</tt><br>
     *      <tt>newCustManagerId             :String:新客户经理主键</tt><br>
     *      <tt>newCustManagerName           :String:新客户经理名称</tt><br>
     *      <tt>newCustManagerMobile         :String:新客户经理手机号</tt><br>
     *      <tt>newCustManagerCredentialsCode:String:新客户经理身份证号</tt><br>
     *      <tt>userId                       :String:创建人</tt><br>
     *      <tt>attachmentList:List:附件列表
     *      	<tt>attachmentType:String:附件类型</tt><br>
     *      	<tt>attachmentName:String:附件名称</tt><br>
     *      	<tt>storagePath   :String:存储路径</tt><br>
     *      </tt><br>
	 * @return ResultVo
	 * 		<tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
     *  @throws SLException
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo saveCustTransfer(Map<String, Object> param) throws SLException {
		List<Map<String, Object>> attachmentList = (List<Map<String, Object>>) param.get("attachmentList");
		if(attachmentList == null || attachmentList.size() == 0){
			log.error("附件不能为空！");
			throw new SLException("附件不能为空！");
		}
		String custApplyId = (String)param.get("custApplyId");
		String userId = (String) param.get("userId");
		
		if(!StringUtils.isEmpty(param.get("oldCustManagerId")) && param.get("oldCustManagerId").toString().equals(param.get("newCustManagerId"))){
			log.error("不允许将客户转移给自己，请先重新输入!");
			throw new SLException("不允许将客户转移给自己，请先重新输入!");
		}
		
		// 验证客户是否属于原客户经理
		if(!StringUtils.isEmpty(param.get("oldCustManagerId")) && !checkMatch((String)param.get("oldCustManagerId"), (String)param.get("custId"))) {
			log.error("被转移客户不属于该客户经理，请先检查数据");
			throw new SLException("被转移客户不属于该客户经理，请先检查数据");
		}
		// 客户申请表
		CustApplyInfoEntity custApplyInfoEntity = null;
		
		if(StringUtils.isEmpty(param.get("custApplyId"))){
			/************ 新增 ************/
			// 校验转移是否存在
			int count = custApplyInfoRepositoryCustom.countCheckMgrAndCust((String) param.get("oldCustManagerId"), param.get("custId").toString());
			if(count > 0){
				log.error("该客户处于待审核或者审核回退状态，请确认！");
				throw new SLException("该客户处于待审核或者审核回退状态，请确认！");
			}
			
			// 客户申请表
			custApplyInfoEntity = new CustApplyInfoEntity();
			custApplyInfoEntity.setBasicModelProperty(userId, true);
			custApplyInfoEntity.setCustId((String)param.get("oldCustManagerId"));
			custApplyInfoEntity.setApplyType(Constant.OPERATION_TYPE_54);
			custApplyInfoEntity.setApplyDesc("申请");
			custApplyInfoEntity.setApplyStatus(Constant.CUST_MANAGER_APPLY_STATUS_REVIEWD);
			custApplyInfoEntity.setApplyNo(numberService.generateUserNickName());// 取数据库序列
			custApplyInfoEntity.setApplyDate(new Date());
			custApplyInfoEntity.setSalesmanType(Constant.CUST_APPLY_SALESMAN_TYPE_INTERNAL);
			custApplyInfoEntity.setTransferCustId((String) param.get("custId"));
			custApplyInfoEntity.setNewCustManagerId((String) param.get("newCustManagerId"));
			custApplyInfoEntity = custApplyInfoRepository.save(custApplyInfoEntity);
			// 审核表
			AuditInfoEntity auditInfoEntity = new AuditInfoEntity();
			auditInfoEntity.setBasicModelProperty(userId, true);
			auditInfoEntity.setCustId((String) param.get("custId"));
			auditInfoEntity.setRelateType(Constant.TABLE_BAO_T_CUST_APPLY_INFO);
			auditInfoEntity.setRelatePrimary(custApplyInfoEntity.getId());
			auditInfoEntity.setApplyType(Constant.OPERATION_TYPE_55);
			auditInfoEntity.setApplyTime(new Date());
			auditInfoEntity.setAuditStatus(Constant.CUST_MANAGER_AUDIT_STATUS_UNREVIEW);
			auditInfoEntity.setTradeStatus(Constant.TRADE_STATUS_01);
			auditInfoRepository.save(auditInfoEntity);
		} else {
			/************ 编辑 ************/
			// 客户申请表
			custApplyInfoEntity = custApplyInfoRepository.findOne(custApplyId);
			custApplyInfoEntity.setBasicModelProperty(userId, false);
			custApplyInfoEntity.setCustId((String)param.get("oldCustManagerId"));
			custApplyInfoEntity.setApplyStatus(Constant.CUST_MANAGER_APPLY_STATUS_REVIEWD);
			custApplyInfoEntity.setTransferCustId((String) param.get("custId"));
			custApplyInfoEntity.setNewCustManagerId((String) param.get("newCustManagerId"));
			// 审核表
			AuditInfoEntity auditInfoEntity = auditInfoRepository.findAuditInfoEntityByRelatePrimary(custApplyId);
			auditInfoEntity.setBasicModelProperty(userId, false);
			auditInfoEntity.setAuditStatus(Constant.CUST_MANAGER_AUDIT_STATUS_UNREVIEW);
			auditInfoEntity.setTradeStatus(Constant.TRADE_STATUS_02);
			
			if(attachmentList != null && attachmentList.size() > 0){
				// 旧附件更新为无效
				attachmentRepository.updateByTypeAndPrimary(Constant.TABLE_BAO_T_CUST_APPLY_INFO, custApplyId);
			}
		}
		
		if(attachmentList != null && attachmentList.size() > 0){
			List<AttachmentInfoEntity> attachmentNewList = new ArrayList<AttachmentInfoEntity>();
			for (int i = 0; i < attachmentList.size(); i++) {
				Map<String, Object> temp = attachmentList.get(i);
				AttachmentInfoEntity attachmentInfoEntityNew = new AttachmentInfoEntity();
				attachmentInfoEntityNew.setBasicModelProperty(userId, true);
				attachmentInfoEntityNew.setRelateType(Constant.TABLE_BAO_T_CUST_APPLY_INFO);
				attachmentInfoEntityNew.setRelatePrimary(custApplyInfoEntity.getId());
				attachmentInfoEntityNew.setAttachmentType((String) temp.get("attachmentType"));
				attachmentInfoEntityNew.setAttachmentName((String) temp.get("attachmentName"));
				attachmentInfoEntityNew.setStoragePath((String) temp.get("storagePath"));
				attachmentInfoEntityNew.setDocType((String) temp.get("docType"));
				attachmentNewList.add(attachmentInfoEntityNew);
			}
			attachmentRepository.save(attachmentNewList);
		}
		
		// 判断申请是否由原客户经理发起
		if(!userId.equals(param.get("oldCustManagerId"))) {
			return new ResultVo(true, "客户转移变更申请成功"); 
		}
		
		/* 是原客户经理发起 */
		Map<String, Object> paramSub = new HashMap<String, Object>();
		paramSub.put("custApplyId", custApplyInfoEntity.getId());
		paramSub.put("auditStatus", Constant.CUST_MANAGER_AUDIT_STATUS_PASS); //:String:审核状态
		paramSub.put("auditMemo", "SYS:原客户经理发起客户转移"); //:String: 审核备注
		paramSub.put("userId", userId); //:String:创建人
		auditCustTransferSub(paramSub, true); // 借用审核的方法
		
		return new ResultVo(true, "客户转移变更申请成功");
	}
	
	/**
	 * 线下业务/前台-客户转移-客户转移保存 新建/编辑
	 *
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param param
     *      <tt>custApplyId                  :String:客户申请ID（编辑时非空）</tt><br>
     *      <tt>custId                       :String:用户ID</tt><br>
     *      <tt>oldCustManagerId             :String:原客户经理主键</tt><br>
     *      <tt>oldCustManagerName           :String:原客户经理名称</tt><br>
     *      <tt>oldCustManagerMobile         :String:原客户经理手机号</tt><br>
     *      <tt>oldCustManagerCredentialsCode:String:原客户经理身份证号</tt><br>
     *      <tt>newCustManagerId             :String:新客户经理主键</tt><br>
     *      <tt>newCustManagerName           :String:新客户经理名称</tt><br>
     *      <tt>newCustManagerMobile         :String:新客户经理手机号</tt><br>
     *      <tt>newCustManagerCredentialsCode:String:新客户经理身份证号</tt><br>
     *      <tt>userId                       :String:创建人</tt><br>
     *      <tt>attachmentList:List:附件列表
     *      	<tt>attachmentType:String:附件类型</tt><br>
     *      	<tt>attachmentName:String:附件名称</tt><br>
     *      	<tt>storagePath   :String:存储路径</tt><br>
     *      </tt><br>
	 * @return ResultVo
	 * 		<tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
     *  @throws SLException
	 */
	@SuppressWarnings("unchecked")
	private ResultVo saveCustTransferSub(Map<String, Object> param) throws SLException {
		String custApplyId = (String)param.get("custApplyId");
		String userId = (String) param.get("userId");
		
		if(!StringUtils.isEmpty(param.get("oldCustManagerId")) && param.get("oldCustManagerId").toString().equals(param.get("newCustManagerId"))){
			log.error("不允许将客户转移给自己，请先重新输入!");
			throw new SLException("不允许将客户转移给自己，请先重新输入!");
		}
		
		// 验证客户是否属于原客户经理
		if(!checkMatch((String)param.get("oldCustManagerId"), (String)param.get("custId"))) {
			log.error("被转移客户不属于该客户经理，请先检查数据");
			throw new SLException("被转移客户不属于该客户经理，请先检查数据");
		}
		// 客户申请表
		CustApplyInfoEntity custApplyInfoEntity = null;
		// 附件表
		List<Map<String, Object>> attachmentList = (List<Map<String, Object>>) param.get("attachmentList");
		
		if(StringUtils.isEmpty(param.get("custApplyId"))){
			/************ 新增 ************/
			// 校验转移是否存在
			int count = custApplyInfoRepositoryCustom.countCheckMgrAndCust(param.get("oldCustManagerId").toString(), param.get("custId").toString());
			if(count > 0){
				log.error("该客户处于待审核或者审核回退状态，请确认！");
				throw new SLException("该客户处于待审核或者审核回退状态，请确认！");
			}
			
			// 客户申请表
			custApplyInfoEntity = new CustApplyInfoEntity();
			custApplyInfoEntity.setBasicModelProperty(userId, true);
			custApplyInfoEntity.setCustId((String)param.get("oldCustManagerId"));
			custApplyInfoEntity.setApplyType(Constant.OPERATION_TYPE_54);
			custApplyInfoEntity.setApplyDesc("申请");
			custApplyInfoEntity.setApplyStatus(Constant.CUST_MANAGER_APPLY_STATUS_REVIEWD);
			custApplyInfoEntity.setApplyNo(numberService.generateUserNickName());// 取数据库序列
			custApplyInfoEntity.setApplyDate(new Date());
			custApplyInfoEntity.setSalesmanType(Constant.CUST_APPLY_SALESMAN_TYPE_INTERNAL);
			custApplyInfoEntity.setTransferCustId((String) param.get("custId"));
			custApplyInfoEntity.setNewCustManagerId((String) param.get("newCustManagerId"));
			custApplyInfoEntity = custApplyInfoRepository.save(custApplyInfoEntity);
			// 审核表
			AuditInfoEntity auditInfoEntity = new AuditInfoEntity();
			auditInfoEntity.setBasicModelProperty(userId, true);
			auditInfoEntity.setCustId((String) param.get("custId"));
			auditInfoEntity.setRelateType(Constant.TABLE_BAO_T_CUST_APPLY_INFO);
			auditInfoEntity.setRelatePrimary(custApplyInfoEntity.getId());
			auditInfoEntity.setApplyType(Constant.OPERATION_TYPE_55);
			auditInfoEntity.setApplyTime(new Date());
			auditInfoEntity.setAuditStatus(Constant.CUST_MANAGER_AUDIT_STATUS_UNREVIEW);
			auditInfoEntity.setTradeStatus(Constant.TRADE_STATUS_01);
			auditInfoRepository.save(auditInfoEntity);
		} else {
			/************ 编辑 ************/
			// 客户申请表
			custApplyInfoEntity = custApplyInfoRepository.findOne(custApplyId);
			custApplyInfoEntity.setBasicModelProperty(userId, false);
			custApplyInfoEntity.setCustId((String)param.get("oldCustManagerId"));
			custApplyInfoEntity.setApplyStatus(Constant.CUST_MANAGER_APPLY_STATUS_REVIEWD);
			custApplyInfoEntity.setTransferCustId((String) param.get("custId"));
			custApplyInfoEntity.setNewCustManagerId((String) param.get("newCustManagerId"));
			// 审核表
			AuditInfoEntity auditInfoEntity = auditInfoRepository.findAuditInfoEntityByRelatePrimary(custApplyId);
			auditInfoEntity.setBasicModelProperty(userId, false);
			auditInfoEntity.setAuditStatus(Constant.CUST_MANAGER_AUDIT_STATUS_UNREVIEW);
			auditInfoEntity.setTradeStatus(Constant.TRADE_STATUS_02);
			
			if(attachmentList != null && attachmentList.size() > 0){
				// 旧附件更新为无效
				attachmentRepository.updateByTypeAndPrimary(Constant.TABLE_BAO_T_CUST_APPLY_INFO, custApplyId);
			}
		}
		
		if(attachmentList != null && attachmentList.size() > 0){
			List<AttachmentInfoEntity> attachmentNewList = new ArrayList<AttachmentInfoEntity>();
			for (int i = 0; i < attachmentList.size(); i++) {
				Map<String, Object> temp = attachmentList.get(i);
				AttachmentInfoEntity attachmentInfoEntityNew = new AttachmentInfoEntity();
				attachmentInfoEntityNew.setBasicModelProperty(userId, true);
				attachmentInfoEntityNew.setRelateType(Constant.TABLE_BAO_T_CUST_APPLY_INFO);
				attachmentInfoEntityNew.setRelatePrimary(custApplyInfoEntity.getId());
				attachmentInfoEntityNew.setAttachmentType((String) temp.get("attachmentType"));
				attachmentInfoEntityNew.setAttachmentName((String) temp.get("attachmentName"));
				attachmentInfoEntityNew.setStoragePath((String) temp.get("storagePath"));
				attachmentInfoEntityNew.setDocType((String) temp.get("docType"));
				attachmentNewList.add(attachmentInfoEntityNew);
			}
			attachmentRepository.save(attachmentNewList);
		}
		
		// 判断申请是否由原客户经理发起
		if(!userId.equals(param.get("oldCustManagerId"))) {
			return new ResultVo(true, "客户转移变更申请成功"); 
		}
		
		/* 是原客户经理发起 */
		Map<String, Object> paramSub = new HashMap<String, Object>();
		paramSub.put("custApplyId", custApplyInfoEntity.getId());
		paramSub.put("auditStatus", Constant.CUST_MANAGER_AUDIT_STATUS_PASS); //:String:审核状态
		paramSub.put("auditMemo", "SYS:原客户经理发起客户转移"); //:String: 审核备注
		paramSub.put("userId", userId); //:String:创建人
		auditCustTransferSub(paramSub, true); // 借用审核的方法
		
		return new ResultVo(true, "客户转移变更申请成功");
	}
	
	/**
	 * 验证客户是否属于原客户经理
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param mgrId  :String:原客户经理Id
	 * @param custId :String:客户ID
	 * @return boolean 
	 */
	private boolean checkMatch(String mgrId, String custId) throws SLException{
		if(StringUtils.isEmpty(mgrId) || StringUtils.isEmpty(custId)){
			return false;
		}
		int count = custRecommendInfoRepository.findCountCustRecommendByCustIdAndQuiltCustId(mgrId, custId);
		if(count <= 0){
			return false;
		}
		return true;
	}
	
	/**
	 * 我是业务员-客户转移-客户转移
	 *
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param param
     *      <tt>custId                       :String:用户ID</tt><br>
     *      <tt>oldCustManagerId             :String:原客户经理主键</tt><br>
     *      <tt>newCustManagerName           :String:新客户经理名称</tt><br>
     *      <tt>newCustManagerMobile         :String:新客户经理手机号</tt><br>
     *      <tt>newCustManagerCredentialsCode:String:新客户经理身份证号</tt><br>
     *      <tt>userId                       :String:创建人</tt><br>
	 * @return ResultVo
	 * 		<tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
     *  @throws SLException
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo saveCustTransferByManager(Map<String, Object> param) throws SLException {
//		String custId = (String) param.get("custId");
//		String oldCustManagerId = (String) param.get("oldCustManagerId");
//		String userId = (String) param.get("custId");
		String newCustManagerName = (String) param.get("newCustManagerName");
		String newCustManagerMobile = (String) param.get("newCustManagerMobile");
		String newCustManagerCredentialsCode = (String) param.get("newCustManagerCredentialsCode");
		
		CustInfoEntity custInfoEntity = custInfoRepository.findOneByNameAndMobileAndCode(newCustManagerName, newCustManagerMobile, newCustManagerCredentialsCode);
		if(custInfoEntity == null){
			log.error("业务员信息不匹配");
			throw new SLException("业务员信息不匹配");
		}
		if(StringUtils.isEmpty(custInfoEntity.getId())){
			log.error("业务员ID查询出错");
			throw new SLException("业务员ID查询出错");
		} else if(custInfoEntity.getId().equals(param.get("oldCustManagerId"))) {
			log.error("不能将客户转移给自己");
			throw new SLException("不能将客户转移给自己");
		}
//		param.put("oldCustManagerName ", ""); // 暂时无用
//		param.put("oldCustManagerMobile ", "");// 暂时无用
//		param.put("oldCustManagerCredentialsCode", "");// 暂时无用
		param.put("newCustManagerId", custInfoEntity.getId());
		saveCustTransferSub(param);
		
		return new ResultVo(true, "客户转移成功");
	}
	
	/**
	 * 同步原线下客户经理(实名认证/定时同步)
	 *
	 * @author  liyy
	 * @date    2016年6月29日 
	 * @param param
     *      <tt>custId                       :String:用户ID</tt><br>
     *      <tt>newCustManagerId             :String:新客户经理ID</tt><br>
     *      <tt>userId                       :String:创建人</tt><br>
	 * @return ResultVo
	 * 		<tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
     *  @throws SLException
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo saveCustTransferForSyncOldOffLineWealth(Map<String, Object> param) 
			throws SLException {
		String userId = (String) param.get("userId");
		String custId = (String) param.get("custId");
		String newCustManagerId = (String) param.get("newCustManagerId");
		String memo = (param.get("memo")!=null)?param.get("memo").toString():"";
		
		String oldCustManagerId = "";
		CustRecommendInfoEntity oldRecommendInfo = custRecommendInfoRepository.findInfoCustRecommendByQuiltCustId(custId);
		if(oldRecommendInfo != null){
			oldCustManagerId = oldRecommendInfo.getCustId();
			if(oldRecommendInfo.getCustId().equals(newCustManagerId)) {
				log.error("不需要转给自己");
				return new ResultVo(false, "不需要转给自己");
			}
		}
		
		// 客户申请表
		CustApplyInfoEntity custApplyInfoEntity = null;
		
		/************ 新增 ************/
		// 校验转移是否存在
		int count = custApplyInfoRepositoryCustom.countCheckMgrAndCust(oldCustManagerId, custId);
		if(count > 0){
			log.error("该客户处于待审核或者审核回退状态，请确认！");
			return new ResultVo(false, "该客户处于待审核或者审核回退状态，请确认！");
		}
		
		// 客户申请表
		custApplyInfoEntity = new CustApplyInfoEntity();
		custApplyInfoEntity.setBasicModelProperty(userId, true);
		custApplyInfoEntity.setCustId(oldCustManagerId);
		custApplyInfoEntity.setApplyType(Constant.OPERATION_TYPE_54);
		custApplyInfoEntity.setApplyDesc("申请");
		custApplyInfoEntity.setApplyStatus(Constant.CUST_MANAGER_APPLY_STATUS_REVIEWD);
		custApplyInfoEntity.setApplyNo(numberService.generateUserNickName());// 取数据库序列
		custApplyInfoEntity.setApplyDate(new Date());
		custApplyInfoEntity.setSalesmanType(Constant.CUST_APPLY_SALESMAN_TYPE_INTERNAL);
		custApplyInfoEntity.setTransferCustId((String) param.get("custId"));
		custApplyInfoEntity.setNewCustManagerId((String) param.get("newCustManagerId"));
		custApplyInfoEntity.setMemo("同步原线下客户经理," + memo);
		custApplyInfoEntity = custApplyInfoRepository.save(custApplyInfoEntity);
		// 审核表
		AuditInfoEntity auditInfoEntity = new AuditInfoEntity();
		auditInfoEntity.setBasicModelProperty(userId, true);
		auditInfoEntity.setCustId(custId);
		auditInfoEntity.setRelateType(Constant.TABLE_BAO_T_CUST_APPLY_INFO);
		auditInfoEntity.setRelatePrimary(custApplyInfoEntity.getId());
		auditInfoEntity.setApplyType(Constant.OPERATION_TYPE_55);
		auditInfoEntity.setApplyTime(new Date());
		auditInfoEntity.setAuditStatus(Constant.CUST_MANAGER_AUDIT_STATUS_UNREVIEW);
		auditInfoEntity.setTradeStatus(Constant.TRADE_STATUS_01);
		auditInfoRepository.save(auditInfoEntity);
		
		/* 自动审核 */
		Map<String, Object> paramSub = new HashMap<String, Object>();
		paramSub.put("custApplyId", custApplyInfoEntity.getId());
		paramSub.put("auditStatus", Constant.CUST_MANAGER_AUDIT_STATUS_PASS); //:String:审核状态
		paramSub.put("auditMemo", "SYS:原线下理财客户经理调整," + memo); //:String: 审核备注
		paramSub.put("userId", userId); //:String:创建人
		auditCustTransferSub(paramSub, true); // 借用审核的方法
		
		return new ResultVo(true, "客户转移变更申请成功");
	}
	
	/**
	 * 线下业务-客户转移-客户转移审核
	 *
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param param
     *      <tt>custApplyId:String:客户申请ID</tt><br>
     *      <tt>auditStatus:String:审核状态</tt><br>
     *      <tt>auditMemo  :String: 审核备注</tt><br>
     *      <tt>userId     :String:创建人</tt><br>
	 * @return ResultVo
	 * 		<tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
     *  @throws SLException
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo auditCustTransfer(Map<String, Object> param) throws SLException {
		String custApplyId = (String) param.get("custApplyId");
		AuditInfoEntity auditInfoEntity = auditInfoRepository.findAuditInfoEntityByRelatePrimary(custApplyId);
		String beforeAuditStatus = auditInfoEntity.getAuditStatus();// 修改前审核状态
		// add @2016/3/22  添加数据检查防止前台重复提交
		if(Constant.BANK_AUDIT_STATUS_PASS.equals(beforeAuditStatus)){
			log.error("该记录审核通过，请先刷新数据！");
			throw new SLException("该记录审核通过，请刷新数据！");
		}else if(Constant.BANK_AUDIT_STATUS_REFUSE.equals(beforeAuditStatus)){
			log.error("该记录审核拒绝，请刷新数据！");
			throw new SLException("该记录审核拒绝，请刷新数据！");
		}
		return auditCustTransferSub(param, false);
	}
	
	/**
	 * 线下业务-客户转移-客户转移审核-子程序
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param param
     *      <tt>custApplyId:String:客户申请ID</tt><br>
     *      <tt>auditStatus:String:审核状态</tt><br>
     *      <tt>auditMemo  :String: 审核备注</tt><br>
     *      <tt>userId     :String:创建人</tt><br>
     * @param isSelf boolean:原客户经理发起
	 * @return ResultVo
	 * 		<tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
     *  @throws SLException
	 */
	private ResultVo auditCustTransferSub(Map<String, Object> param, boolean isSelf) throws SLException {
		String custApplyId = (String) param.get("custApplyId");
		String auditStatus = (String) param.get("auditStatus");
		String auditMemo = (String) param.get("auditMemo");
		String userId = (String) param.get("userId");
		
		// 变更申请表
		CustApplyInfoEntity custApplyInfoEntity = custApplyInfoRepository.findOne(custApplyId);
		custApplyInfoEntity.setApplyStatus(auditStatus);
		// 审核表
		AuditInfoEntity auditInfoEntity = auditInfoRepository.findAuditInfoEntityByRelatePrimary(custApplyId);
		String beforeAuditStatus = auditInfoEntity.getAuditStatus();// 修改前审核状态
		auditInfoEntity.setAuditTime(new Date());
		auditInfoEntity.setAuditUser(userId);
		auditInfoEntity.setAuditStatus(auditStatus);
		auditInfoEntity.setMemo(auditMemo);
		
		if(Constant.CUST_MANAGER_AUDIT_STATUS_PASS.equals(auditStatus)){
			// 通过
			auditInfoEntity.setTradeStatus(Constant.TRADE_STATUS_03); // 处理成功 
			// 旧对应关系更新为无效
			CustRecommendInfoEntity custRecommendInfoEntity 
					= custRecommendInfoRepository.findOneByApplyIdOfTableOfCustApply(custApplyId);
			if(custRecommendInfoEntity != null){		
				custRecommendInfoEntity.setBasicModelProperty(userId, false);
				custRecommendInfoEntity.setExpireDate(new Timestamp(new Date().getTime()));
				custRecommendInfoEntity.setRecordStatus(Constant.VALID_STATUS_INVALID);
			}
			
			// 取出所有有效的客户与业务员关系
			List<String> quiltCustIdList = Arrays.asList(custApplyInfoEntity.getTransferCustId());
			Map<String, Object> quiltCustIdMap = Maps.newConcurrentMap();
			quiltCustIdMap.put("recordStatus", Constant.VALID_STATUS_VALID);
			quiltCustIdMap.put("quiltCustIdList", quiltCustIdList);
			List<Map<String, Object>> existsQuiltCustIdList = Lists.newArrayList();
			if(quiltCustIdList.size() > 0) {
				existsQuiltCustIdList = custRecommendInfoRepositoryCustom.queryTransferByQuiltCustId(quiltCustIdMap);
			}
			if(!StringUtils.isEmpty(custApplyInfoEntity.getCustId())) { //原业务员不为空
				
				// 判断客户是否已经变更到其他客户经理名下
				for(Map<String, Object> m : existsQuiltCustIdList) {
					if(!m.get("custId").toString().equals(custApplyInfoEntity.getCustId())) {
						log.error("该客户已经变更到其他客户经理名下，客户转移失败，请将当前申请拒绝并重新申请");
						throw new SLException("该客户已经变更到其他客户经理名下，客户转移失败，请将当前申请拒绝并重新申请");
					}
				}	
				
				// 判断原业务员是否是业务员(若已解除业务员则不能变更)
				CustInfoEntity oldManager = custInfoRepository.findOne(custApplyInfoEntity.getCustId());
				if(oldManager != null && !Constant.IS_RECOMMEND_YES.equals(oldManager.getIsEmployee())) {
					/*log.error("原业务员已经被解除业务员，客户转移失败，请将当前申请拒绝并重新申请");
					throw new SLException("原业务员已经被解除业务员，客户转移失败，请将当前申请拒绝并重新申请");*/
					// 原业务员被解除业务员后，将客户关系备注清除（即若原业务员恢复业务员身份，不再拥有当前客户）
					if(custRecommendInfoEntity != null){ 
						custRecommendInfoEntity.setMemo(null);
					}
				}
			}
			else { // 没有原业务员但已经转移到其他人名下
				if(existsQuiltCustIdList != null && existsQuiltCustIdList.size() > 0) {
					log.error("该客户已经变更到其他客户经理名下，客户转移失败，请将当前申请拒绝并重新申请");
					throw new SLException("该客户已经变更到其他客户经理名下，客户转移失败，请将当前申请拒绝并重新申请");
				}
			}

			// 判断新业务员是否是业务员(若已解除业务员则不能变更)
			CustInfoEntity newManager = custInfoRepository.findOne(custApplyInfoEntity.getNewCustManagerId());
			if(newManager != null && !Constant.IS_RECOMMEND_YES.equals(newManager.getIsEmployee())) {
				log.error("新业务员已经被解除业务员，客户转移失败，请将当前申请拒绝并重新申请");
				throw new SLException("新业务员已经被解除业务员，客户转移失败，请将当前申请拒绝并重新申请");
			}
			
			// 新的对应关系建立
			CustRecommendInfoEntity custRecommendNew = new CustRecommendInfoEntity();
			custRecommendNew.setBasicModelProperty(userId, true);
			custRecommendNew.setApplyId(custApplyId);
			custRecommendNew.setSource(Constant.OPERATION_TYPE_54);
			custRecommendNew.setCustId(custApplyInfoEntity.getNewCustManagerId());
			custRecommendNew.setQuiltCustId(custApplyInfoEntity.getTransferCustId());
			custRecommendNew.setStartDate(new Timestamp(new Date().getTime()));
			custRecommendNew.setRecordStatus(Constant.VALID_STATUS_VALID);
			custRecommendInfoRepository.save(custRecommendNew);
			
		} else if(Constant.CUST_MANAGER_AUDIT_STATUS_FALLBACK.equals(auditStatus)||Constant.CUST_MANAGER_AUDIT_STATUS_REFUSE.equals(auditStatus)){
			// 审核回退/拒绝
			auditInfoEntity.setTradeStatus(Constant.TRADE_STATUS_04); // 处理失败
		} else {
			log.error("更新审核状态出错，请选择正确的状态");
			throw new SLException("更新审核状态出错，请选择正确的状态");
		}
		
		// 日志信息
		LogInfoEntity logInfoEntity = new LogInfoEntity();
		logInfoEntity.setBasicModelProperty(userId, true);
		logInfoEntity.setRelateType(Constant.TABLE_BAO_T_CUST_APPLY_INFO);
		logInfoEntity.setRelatePrimary(custApplyId);
		if(isSelf){// 原客户经理发起
			logInfoEntity.setLogType(Constant.OPERATION_TYPE_55);// 客户经理变更申请
		} else {
			logInfoEntity.setLogType(Constant.OPERATION_TYPE_56);// 客户经理变更审核
		}
		logInfoEntity.setOperDesc(auditMemo);
		logInfoEntity.setMemo(auditMemo);
		logInfoEntity.setOperPerson(userId);
		logInfoEntity.setOperBeforeContent(beforeAuditStatus);// 审核前状态
		logInfoEntity.setOperAfterContent(auditStatus);
		logInfoEntityRepository.save(logInfoEntity);
		
		return new ResultVo(true, "客户转移变更审核成功");
	}
	
	/**
	 * 我是业务员-客户管理-客户列表查询
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param param:Map
     *      <tt>start            :String:起始值</tt><br>
     *      <tt>length           :String:长度</tt><br>
     *      <tt>custId           :String:客户ID（客户经理）</tt><br>
     *      <tt>custName         :String:用户名（可以为空）</tt><br>
     *      <tt>credentialsCode  :String:证件号码（可以为空）</tt><br>
     *      <tt>mobile           :String:手机号（可以为空）</tt><br>
     *      <tt>beginRegisterDate:String:开始注册时间（可以为空）</tt><br>
     *      <tt>endRegisterDate  :String:结束注册时间（可以为空）</tt><br>
	 * @return ResultVo
	 * 		<tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
     *      <tt>data:Map
     *      	<tt>iTotalDisplayRecords:String:总数</tt><br>
     *      	<tt>data:List
     *      		<tt>custId         :String:客户ID</tt><br>
     *      		<tt>custName       :String:用户名</tt><br>
     *      		<tt>credentialsCode:String:证件号码</tt><br>
     *      		<tt>mobile         :String:手机号</tt><br>
     *      		<tt>registerDate   :String:注册时间</tt><br>
     *          </tt><br>
     *      </tt><br>
     *  @throws SLException
	 */
	public ResultVo queryCustByManager(Map<String, Object> param) throws SLException {
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			Page<Map<String, Object>> page = custApplyInfoRepositoryCustom.queryCustByManager(param);
			
			data.put("iTotalDisplayRecords", page.getTotalElements());
			data.put("data", page.getContent());
			return new ResultVo(true, "客户列表查询成功",data);
		} catch (Exception e) {
			log.error("客户列表查询失败" + e.getMessage());
			return new ResultVo(false, "客户列表查询失败");
		}
	}
	
	/**
	 * 我是业务员-客户管理-投资信息查询
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param param:Map
     *      <tt>start :String:起始值</tt><br>
     *      <tt>length:String:长度</tt><br>
     *      <tt>custId:String:客户ID</tt><br>
	 * @return ResultVo
	 * 		<tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
     *      <tt>data:Map
     *      	<tt>iTotalDisplayRecords:String:总数</tt><br>
     *      	<tt>data:List
     *      		<tt>investDate  :String:投资时间</tt><br>
     *      		<tt>investAmount:String:投资金额</tt><br>
     *      		<tt>lendingType :String:计划名称</tt><br>
     *      		<tt>lendingNo   :String:项目期数</tt><br>
     *      		<tt>typeTerm    :String:项目期限(月)</tt><br>
     *      		<tt>yearRate    :String:年化收益率</tt><br>
     *      		<tt>awardRate   :String:奖励利率</tt><br>
     *      		<tt>effectDate  :String:生效日期</tt><br>
     *      		<tt>endDate     :String:到期日期</tt><br>
     *      		<tt>investStatus:String:投资状态（当前状态）</tt><br>
     *          </tt><br>
     *      </tt><br>
     *  @throws SLException
	 */
	public ResultVo queryCustWealthByManager(Map<String, Object> param) throws SLException {
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			Page<Map<String, Object>> page = custApplyInfoRepositoryCustom.queryCustWealthByManager(param);
			
			data.put("iTotalDisplayRecords", page.getTotalElements());
			data.put("data", page.getContent());
			return new ResultVo(true, "投资信息查询成功",data);
		} catch (Exception e) {
			log.error("投资信息查询失败" + e.getMessage());
			return new ResultVo(false, "投资信息查询失败");
		}
	}
	
	/**
	 * 我是业务员-附属银行卡-客户银行卡列表查询
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param param:Map
     *      <tt>start :String:起始值</tt><br>
     *      <tt>length:String:长度</tt><br>
     *      <tt>custId:String:客户ID</tt><br>
	 * @return ResultVo
	 * 		<tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
     *      <tt>data:Map
     *      	<tt>iTotalDisplayRecords:String:总数</tt><br>
     *      	<tt>data:List
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
     *  @throws SLException
	 */
	public ResultVo queryCustBankByManager(Map<String, Object> param) throws SLException {
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			Page<Map<String, Object>> page = custApplyInfoRepositoryCustom.queryCustBankByManager(param);
			
			data.put("iTotalDisplayRecords", page.getTotalElements());
			data.put("data", page.getContent());
			return new ResultVo(true, "客户银行卡列表查询成功",data);
		} catch (Exception e) {
			log.error("客户银行卡列表查询失败" + e.getMessage());
			return new ResultVo(false, "客户银行卡列表查询失败");
		}
	}
	
	
	/**
	 * 我是业务员-附属银行卡-查看客户银行卡
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param param:Map
     *      <tt>tradeFlowId:String:交易过程流水ID</tt><br>
	 * @return ResultVo
	 * 		<tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
     *      <tt>data
     *      	<tt>tradeFlowId    :String:交易过程流水ID</tt><br>
     *      	<tt>custId         :String:客户ID</tt><br>
     *      	<tt>custName       :String:用户名</tt><br>
     *      	<tt>credentialsCode:String:证件号码</tt><br>
     *      	<tt>mobile         :String:手机号</tt><br>
     *      	<tt>bankCode       :String:银行名称code</tt><br>
     *      	<tt>bankName       :String:银行名称</tt><br>
     *      	<tt>bankCardNo     :String:银行卡号</tt><br>
     *      	<tt>branchBankName :String:支行名称</tt><br>
     *      	<tt>openProvince   :String:开户行所在省code</tt><br>
     *      	<tt>openProvinceName   :String:开户行所在省</tt><br>
     *      	<tt>openCity       :String:开户行所在市code</tt><br>
     *      	<tt>openCityName       :String:开户行所在市</tt><br>
     *      	<tt>tradeStatus       :String:当前状态</tt><br>
     *      	<tt>attachmentList:List<Map<String, Object>>:附件列表
     *     			<tt>attachmentId  :String:         附件ID</tt><br>
     *     			<tt>attachmentType:String:         附件类型</tt><br>
     *      		<tt>attachmentName:String:         附件名称</tt><br>
     *      		<tt>storagePath   :String:         存储路径</tt><br>
     *      	</tt><br>
     *     		<tt>auditList:List<Map<String, Object>>:审核日志列表
     *      		<tt>auditId    :String:审核ID</tt><br>
     *      		<tt>auditDate  :String:审核时间</tt><br>
     *      		<tt>auditUser  :String:审核人员</tt><br>
     *      		<tt>auditStatus:String:审核结果</tt><br>
     *      		<tt>auditMemo  :String:审核备注</tt><br>
     *      	</tt><br>
     *      </tt><br>
     *  @throws SLException
	 */
	public ResultVo queryCustBankDetailByManager(Map<String, Object> param) throws SLException {
		try {
			Map<String, Object> data = null;
			// 基本信息
			List<Map<String, Object>> list = custApplyInfoRepositoryCustom.queryCustBankDetailByManager(param);
			if(list == null || list.size() == 0){
				return new ResultVo(false, "客户银行卡查询失败, 数据为空");
			}
			data = list.get(0);
			// 附件列表
			List<Map<String, Object>> attachmentList 
				= custApplyInfoRepositoryCustom.queryAttachmentInfoListById(Constant.TABLE_BAO_T_TRADE_FLOW_INFO, data.get("tradeFlowId").toString());
			// 审核日志列表
			List<Map<String, Object>> auditList 
				= custApplyInfoRepositoryCustom.queryAuditInfoListById(Constant.OPERATION_TYPE_45, Constant.TABLE_BAO_T_TRADE_FLOW_INFO, data.get("tradeFlowId").toString());
			
			data.put("attachmentList", attachmentList);
			data.put("auditList", auditList);
			
			return new ResultVo(true, "客户银行卡查询成功", data);
		} catch (Exception e) {
			log.error("客户银行卡查询失败" + e.getMessage());
			return new ResultVo(false, "客户银行卡查询失败");
		}
	}
	
	/**
	 * 我是业务员-附属银行卡-新增客户银行卡
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param param:Map
     *      <tt>tradeFlowId   :String:交易过程流水ID(编辑时非空)</tt><br>
     *      <tt>custId        :String:客户ID</tt><br>
     *      <tt>bankName      :String:银行名称(code)</tt><br>
     *      <tt>bankCardNo    :String:银行卡号</tt><br>
     *      <tt>branchBankName:String:支行名称</tt><br>
     *      <tt>openProvince  :String:开户行所在省</tt><br>
     *      <tt>openCity      :String:开户行所在市</tt><br>
     *      <tt>userId        :String:创建人</tt><br>
     *      <tt>attachmentList:List< Map< String, Object>>:附件列表<br>
     *      	<tt>attachmentId（非空时编辑，否则新增）:String: 附件ID</tt><br>
     *      	<tt>attachmentType          :String: 附件类型</tt><br>
     *      	<tt>attachmentName          :String: 附件名称</tt><br>
     *      	<tt>storagePath             :String:存储路径</tt><br>
     *      </tt><br>
	 * @return ResultVo
	 * 		<tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
     * @throws SLException
     */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo saveCustBankByManager(Map<String, Object> param) throws SLException {
		String tradeFlowId = (String)param.get("tradeFlowId");
		String userId = (String) param.get("userId");
		String bankName = null;
		String appSource = (String) param.get("appSource"); // 来源
		appSource = Strings.nullToEmpty(StringUtils.isEmpty(appSource) ? Constant.INVEST_SOURCE_PC : appSource).toLowerCase();
		try {
			// 传入是(code)转化成name
			bankName = custApplyInfoRepositoryCustom.getNameByCode("8", param.get("bankName").toString());
		} catch (Exception e) {
			log.error("银行名称不正确，请重新输入");
			throw new SLException("银行名称不正确，请重新输入");
		}
		
//		// 校验银行卡和银行是否匹配
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("bankCardNo", param.get("bankCardNo").toString().replaceAll(" ", ""));
//		Map<String, Object> rs = bankCardService.queryThirdBankByCardNo(map);
//		if(!bankName.equals(rs.get("bankName"))){
//			log.error("银行名称应是["+rs.get("bankName")+"]，请重新选择");
//			throw new SLException("银行名称应是["+rs.get("bankName")+"]，请重新选择");
//		}

		// 交易流水过程表
		TradeFlowInfoEntity tradeFlowInfoEntity = null;
		// 附件表
		List<Map<String, Object>> attachmentList = (List<Map<String, Object>>) param.get("attachmentList");
		
		if(StringUtils.isEmpty(param.get("tradeFlowId"))){
			/************ 新增 ************/
			// 判断客户有没有实名认证
			int num = custApplyInfoRepository.checkRealNameAuth(param.get("custId").toString());
			if(num == 0){
				log.error("该客户还未实名认证，请先实名认证!");
				throw new SLException("该客户还未实名认证，请先实名认证!");
			}			
			// 判断同一客户同一卡号不能重复存在
			int count = custApplyInfoRepositoryCustom.checkCustAndCardNo(param.get("custId").toString(), param.get("bankCardNo").toString().replaceAll(" ", ""));
			if(count > 0){
				log.error("该卡已绑定或在审核中");
				throw new SLException("该卡已绑定或在审核中");
			}
			// 取得账户ID
			String custAccountId = accountInfoRepository.findCustAccountIdByCustId(param.get("custId").toString());
			
			// 交易流水过程表
			tradeFlowInfoEntity = new TradeFlowInfoEntity();
			tradeFlowInfoEntity.setBasicModelProperty(userId, true);
			tradeFlowInfoEntity.setRelateType(Constant.TABLE_BAO_T_CUST_INFO);
			tradeFlowInfoEntity.setRelatePrimary((String) param.get("custId"));
			tradeFlowInfoEntity.setCustId((String) param.get("custId"));
			tradeFlowInfoEntity.setCustAccountId(custAccountId);
			tradeFlowInfoEntity.setTradeType(Constant.OPERATION_TYPE_44);
			tradeFlowInfoEntity.setTradeStatus(Constant.TRADE_STATUS_01);
			tradeFlowInfoEntity.setTradeNo(numberService.generateTradeNumber());
			tradeFlowInfoEntity.setTradeDate(new Date());
			tradeFlowInfoEntity.setTradeDesc(Constant.OPERATION_TYPE_44);
			tradeFlowInfoEntity.setBankName(bankName);
			tradeFlowInfoEntity.setBankCardNo(param.get("bankCardNo").toString().replaceAll(" ", ""));
			tradeFlowInfoEntity.setBranchBankName((String) param.get("branchBankName"));
			tradeFlowInfoEntity.setTradeSource(appSource);
			tradeFlowInfoEntity.setOpenProvince((String) param.get("openProvince"));
			tradeFlowInfoEntity.setOpenCity((String) param.get("openCity"));
			tradeFlowInfoEntity = tradeFlowInfoRepository.save(tradeFlowInfoEntity);
			// 审核表
			AuditInfoEntity auditInfoEntity = new AuditInfoEntity();
			auditInfoEntity.setBasicModelProperty(userId, true);
			auditInfoEntity.setCustId((String) param.get("custId"));
			auditInfoEntity.setRelateType(Constant.TABLE_BAO_T_TRADE_FLOW_INFO);
			auditInfoEntity.setRelatePrimary(tradeFlowInfoEntity.getId());
			auditInfoEntity.setApplyType(Constant.OPERATION_TYPE_44);
			auditInfoEntity.setApplyTime(new Date());
			auditInfoEntity.setAuditStatus(Constant.CUST_MANAGER_AUDIT_STATUS_UNREVIEW);
			auditInfoEntity.setTradeStatus(Constant.TRADE_STATUS_01);
			auditInfoRepository.save(auditInfoEntity);
		} else {
			/************ 编辑 ************/
			// 交易流水过程表
			tradeFlowInfoEntity = tradeFlowInfoRepository.findOne(tradeFlowId);
			tradeFlowInfoEntity.setBasicModelProperty(userId, false);
			tradeFlowInfoEntity.setTradeStatus(Constant.TRADE_STATUS_02);
			tradeFlowInfoEntity.setBankName(bankName);
			tradeFlowInfoEntity.setBankCardNo(param.get("bankCardNo").toString().replaceAll(" ", ""));
			tradeFlowInfoEntity.setBranchBankName((String) param.get("branchBankName"));
			tradeFlowInfoEntity.setTradeSource(appSource);
			tradeFlowInfoEntity.setOpenProvince((String) param.get("openProvince"));
			tradeFlowInfoEntity.setOpenCity((String) param.get("openCity"));
			// 审核表
			AuditInfoEntity auditInfoEntity = auditInfoRepository.findAuditInfoEntityByRelatePrimary(tradeFlowInfoEntity.getId());
			auditInfoEntity.setBasicModelProperty(userId, false);
			auditInfoEntity.setAuditStatus(Constant.CUST_MANAGER_AUDIT_STATUS_UNREVIEW);
			auditInfoEntity.setTradeStatus(Constant.TRADE_STATUS_02);
			
			if(attachmentList != null && attachmentList.size() > 0){
				// 旧附件更新为无效
				attachmentRepository.updateByTypeAndPrimary(Constant.TABLE_BAO_T_TRADE_FLOW_INFO, tradeFlowInfoEntity.getId());
			}
		}

		if(attachmentList != null && attachmentList.size() > 0){
			List<AttachmentInfoEntity> attachmentNewList = new ArrayList<AttachmentInfoEntity>();
			for(String a : Constant.ATTACHMENT_TYPE_BANK_LIST) {
				boolean isFound = false;
				for(Map<String, Object> b : attachmentList) {
					if(a.equals(b.get("attachmentType").toString())) {
						isFound = true;
						break;
					}	
				}
				if(!isFound) {
					log.error("附件必须包含" + Constant.ATTACHMENT_TYPE_BANK_LIST.toString());
					throw new SLException("附件必须包含" + Constant.ATTACHMENT_TYPE_BANK_LIST.toString());
				}
			}
			for (int i = 0; i < attachmentList.size(); i++) {
				Map<String, Object> temp = attachmentList.get(i);
				AttachmentInfoEntity attachmentInfoEntityNew = new AttachmentInfoEntity();
				attachmentInfoEntityNew.setBasicModelProperty(userId, true);
				attachmentInfoEntityNew.setRelateType(Constant.TABLE_BAO_T_TRADE_FLOW_INFO);
				attachmentInfoEntityNew.setRelatePrimary(tradeFlowInfoEntity.getId());
				attachmentInfoEntityNew.setAttachmentType((String) temp.get("attachmentType"));
				attachmentInfoEntityNew.setAttachmentName((String) temp.get("attachmentName"));
				attachmentInfoEntityNew.setStoragePath((String) temp.get("storagePath"));
				attachmentInfoEntityNew.setDocType((String) temp.get("docType"));
				attachmentNewList.add(attachmentInfoEntityNew);
			}
			attachmentRepository.save(attachmentNewList);
		}
		
		// 日志信息
		LogInfoEntity logInfoEntity = new LogInfoEntity();
		logInfoEntity.setBasicModelProperty(userId, true);
		logInfoEntity.setRelateType(Constant.TABLE_BAO_T_TRADE_FLOW_INFO);
		logInfoEntity.setRelatePrimary(tradeFlowInfoEntity.getId());
		logInfoEntity.setLogType(Constant.OPERATION_TYPE_44);// 附属银行卡申请
		logInfoEntity.setOperDesc(tradeFlowInfoEntity.getCustId() +":"+ Constant.OPERATION_TYPE_44);
		logInfoEntity.setMemo(Constant.OPERATION_TYPE_44);
		logInfoEntity.setOperPerson(userId);
		logInfoEntity.setOperAfterContent(Constant.CUST_MANAGER_AUDIT_STATUS_UNREVIEW);
		logInfoEntityRepository.save(logInfoEntity);
		
		return new ResultVo(true, "新增客户银行卡操作成功");
	}

	/**
	 * 我是业务员-附属银行卡-作废客户银行卡
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param param:Map
     *      <tt>tradeFlowId:String:交易过程流水ID</tt><br>
     *      <tt>userId     :String:创建人</tt><br>
	 * @return ResultVo
	 * 		<tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
     * @throws SLException
     */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo abandonCustBankByManager(Map<String, Object> param) throws SLException {
		String tradeFlowId = (String) param.get("tradeFlowId");
		String userId = (String) param.get("userId");
		// 交易流水表
		TradeFlowInfoEntity tradeFlowInfoEntity = tradeFlowInfoRepository.findOne(tradeFlowId);
		// 银行卡置为无效
		int count = custInfoRepository.abandonAtCustBank(tradeFlowInfoEntity.getCustId(), tradeFlowInfoEntity.getBankCardNo(), userId);
		if(count != 1){
			log.error("更新的数据不满足要求");
			throw new SLException("更新的数据不满足要求");
		}
		// 审核状态置为作废
		AuditInfoEntity auditInfoEntity = auditInfoRepository.findAuditInfoEntityByRelatePrimary(tradeFlowInfoEntity.getId());
		auditInfoEntity.setBasicModelProperty(userId, false);
		auditInfoEntity.setAuditStatus(Constant.BANK_AUDIT_STATUS_INVALID);
		
		// 日志信息
		LogInfoEntity logInfoEntity = new LogInfoEntity();
		logInfoEntity.setBasicModelProperty(userId, true);
		logInfoEntity.setRelateType(Constant.TABLE_BAO_T_TRADE_FLOW_INFO);
		logInfoEntity.setRelatePrimary(tradeFlowInfoEntity.getId());
		logInfoEntity.setLogType(Constant.OPERATION_TYPE_45);
		logInfoEntity.setOperDesc("CustId:"+ tradeFlowInfoEntity.getCustId()+", CardNo:"+tradeFlowInfoEntity.getBankCardNo());
		logInfoEntity.setMemo("作废");
		logInfoEntity.setOperPerson(Constant.BANK_AUDIT_STATUS_PASS);
		logInfoEntity.setOperAfterContent(Constant.BANK_AUDIT_STATUS_INVALID);
		logInfoEntityRepository.save(logInfoEntity);
		return new ResultVo(true, "作废客户银行卡操作成功");
	}
	
	/**
	 * 我是业务员-公用-查询客户经理名下所有客户
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param param:Map
     *      <tt>custManagerId:String:客户经理主键(可选)</tt><br>
     *      <tt>custName     :String:用户姓名(可选)</tt><br>
	 * @return ResultVo
	 * 		<tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
     *      <tt>data:Map
     *      	<tt>data:List
	 *      		<tt>custId         :String:客户ID</tt><br>
	 *      		<tt>custName:String:用户名</tt><br>
	 *      		<tt>credentialsCode:String:证件号码</tt><br>
	 *      		<tt>mobile         :String:手机</tt><br>
	 *      		<tt>registerDate   :String:注册时间</tt><br>
	 *      		<tt>custManagerId  :String:客户经理Id</tt><br>  
     *      	</tt><br>
     *      </tt><br>
     * @throws SLException
     */
	public ResultVo queryCustNameByManager(Map<String, Object> param) throws SLException {
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			List<Map<String, Object>> list = custRecommendInfoRepositoryCustom.queryCustNameByManager(param);
			
			data.put("data", list);
			return new ResultVo(true, "客户经理名下所有客户查询成功", data);
		} catch (Exception e) {
			log.error("客户经理名下所有客户查询失败" + e.getMessage());
			return new ResultVo(false, "客户经理名下所有客户查询失败");
		}
	}
	
	/**
	 * 我是业务员-公用-查询业务员信息
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param param:Map
     *      <tt>custName       :String:用户姓名(可选)</tt><br>
     *      <tt>credentialsCode:String:证件号码（可以为空）</tt><br>
     *      <tt>mobile:String:手机号（可以为空）</tt><br>
	 * @return ResultVo
	 * 		<tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
     *      <tt>data:Map
     *      	<tt>data:List
     *      		<tt>custId         :String:客户ID</tt><br>
     *      		<tt>custName       :String:用户名</tt><br>
     *      		<tt>credentialsCode:String:证件号码</tt><br>
     *      		<tt>mobile         :String:手机号</tt><br>
     *      		<tt>registerDate   :String:注册时间</tt><br>
     *      	</tt><br>
     *      </tt><br>
     * @throws SLException
     */
	public ResultVo queryCustManager(Map<String, Object> param) throws SLException {
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			List<Map<String, Object>> list = custRecommendInfoRepositoryCustom.queryCustManager(param);
			
			data.put("data", list);
			return new ResultVo(true, "业务员信息查询成功", data);
		} catch (Exception e) {
			log.error("业务员信息查询失败" + e.getMessage());
			return new ResultVo(false, "业务员信息查询失败");
		}
	}
	
	/**
	 * 我是业务员-公用-查询客户下面附属银行
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param param:Map
     *      <tt>custId:String:客户ID</tt><br>
	 * @return ResultVo
	 * 		<tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
     *      <tt>data:Map
     *      	<tt>data:List
     *      		<tt>bankId  :String:银行主键</tt><br>
     *      		<tt>bankName:String:bankName</tt><br>
     *      	</tt><br>
     *      </tt><br>
     * @throws SLException
     */
	public ResultVo queryCustBankByCustId(Map<String, Object> param) throws SLException {
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			List<Map<String, Object>> list = custRecommendInfoRepositoryCustom.queryCustBankByCustId(param);
			
			data.put("data", list);
			return new ResultVo(true, "客户下面附属银行查询成功", data);
		} catch (Exception e) {
			log.error("客户下面附属银行查询失败" + e.getMessage());
			return new ResultVo(false, "客户下面附属银行查询失败");
		}
	}
	
	@Autowired
	private InternalCustManagerService internalCustManagerService;
	
	@Service
	public static class InternalCustManagerService {
		
		@Autowired
		private TradeResultInfoRepository tradeResultInfoRepository;
		
		@Autowired
		private BankCardInfoRepository bankCardInfoRepository;
		
		@Autowired
		private CustInfoRepository custInfoRepository;
		
		@Autowired
		private TradeFlowInfoRepository tradeFlowInfoRepository;
		
		@Autowired
		private AuditInfoRespository auditInfoRespository;
		
		@Autowired
		private FlowNumberService numberService;
		
		@Autowired
		private AccountInfoRepository accountInfoRepository;
		
		@Autowired
		private CustAccountService custAccountService;
		
		@Autowired
		private LogInfoEntityRepository logInfoEntityRepository;
		
		@Autowired
		private UserRepository userRepository;
		
		@Autowired
		private EmailService emailService;
		
		private String slcfUploadPath = "/upload/slcf/user";
		
		
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public ResultVo excuteWealthWithDraw(Map<String, Object> param) throws SLException{
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> wealthWithDrawList = (List<Map<String, Object>>) param.get("wealthWithDrawList");
			String fileName = (String) param.get("fileName");
			String userId = (String) param.get("userId");
			//保存excel信息
			List<TradeResultInfoEntity> list = Lists.newArrayList();
			for(Map<String, Object> wealthWithDraw : wealthWithDrawList){
				TradeResultInfoEntity tradeResultInfoEntity = new TradeResultInfoEntity();
				tradeResultInfoEntity.setFileName(fileName);
				
				tradeResultInfoEntity.setTransferType(replaceBlank(wealthWithDraw.get("transferType")));
				tradeResultInfoEntity.setCurrencyType(replaceBlank(wealthWithDraw.get("currency")));
				String tradeAmount = replaceBlank(wealthWithDraw.get("tradeAmount"));
				tradeResultInfoEntity.setTradeAmount(new BigDecimal(!"".equals(tradeAmount) ? tradeAmount : "0"));
				if(!StringUtils.isEmpty(wealthWithDraw.get("tradeDate"))){
					tradeResultInfoEntity.setFileTradeDate(DateUtils.parseDate(
							(wealthWithDraw.get("tradeDate").toString().replaceFirst("^[\\x00-\\x200\\xA0]+", "")
									.replaceFirst("[\\x00-\\x20\\xA0]+$", "")), "yyyy-MM-dd HH:mm:ss"));
				}
				tradeResultInfoEntity.setFileTradeStatus(replaceBlank(wealthWithDraw.get("tradeStatus")));
				tradeResultInfoEntity.setPayeeBankCardNo(replaceBlank(wealthWithDraw.get("payeeBankNo")));
				tradeResultInfoEntity.setPayeyBankCardNo(replaceBlank(wealthWithDraw.get("payeyBankNo")));
				tradeResultInfoEntity.setPayeyCustName(replaceBlank(wealthWithDraw.get("payeyCustName")));
				tradeResultInfoEntity.setBasicModelProperty(userId, true);
				list.add(tradeResultInfoEntity);
			}
			List<TradeResultInfoEntity> tradeResultList = (List<TradeResultInfoEntity>) tradeResultInfoRepository.save(list);
			
			//循环处理
			for(TradeResultInfoEntity tradeResultInfoEntity : tradeResultList) {
				BigDecimal tradeAmount = tradeResultInfoEntity.getTradeAmount();
				String tradeStatus = tradeResultInfoEntity.getFileTradeStatus();
				String payeeBankNo = tradeResultInfoEntity.getPayeeBankCardNo(); //付款人账号
				String payeyCustName = tradeResultInfoEntity.getPayeyCustName(); //收款人姓名
				String payeyBankNo = tradeResultInfoEntity.getPayeyBankCardNo(); //收款人账号
				Date fileTradeDate = tradeResultInfoEntity.getFileTradeDate(); //文件处理日期
				
				//校验文件中处理时间不能大于当前时间
				if(!DateUtils.compare_date(new Date(), fileTradeDate)){
					tradeResultInfoEntity.setTradeDate(new Date());
					tradeResultInfoEntity.setTradeStatus(Constant.OFFLINE_WITHDRAW_MANAGER_FAIL);
					tradeResultInfoEntity.setMemo("文件处理日期大于当前日期");
					tradeResultInfoEntity.setBasicModelProperty(userId, false);
					continue;
				}
				
				//验证收款人银行卡是否存在
				List<BankCardInfoEntity> payeyList = bankCardInfoRepository.findByCardNo(payeyBankNo);
				if(payeyList == null || payeyList.size() <= 0){
					tradeResultInfoEntity.setTradeDate(new Date());
					tradeResultInfoEntity.setTradeStatus(Constant.OFFLINE_WITHDRAW_MANAGER_FAIL);
					tradeResultInfoEntity.setMemo("收款人银行卡不存在");
					tradeResultInfoEntity.setBasicModelProperty(userId, false);
					continue;
				}
				//收款人银行卡
				BankCardInfoEntity bankCardInfoEntity = payeyList.get(0);
				
				//验证付款银行卡是否存在
				List<BankCardInfoEntity> payeeList = bankCardInfoRepository.findByCardNo(payeeBankNo);
				if(payeeList == null || payeeList.size() <= 0){
					tradeResultInfoEntity.setTradeDate(new Date());
					tradeResultInfoEntity.setTradeStatus(Constant.OFFLINE_WITHDRAW_MANAGER_FAIL);
					tradeResultInfoEntity.setMemo("付款人银行卡不存在");
					tradeResultInfoEntity.setBasicModelProperty(userId, false);
					continue;
				}
				//付款人银行卡
				BankCardInfoEntity payeeBankCardInfo = payeeList.get(0);
				//查询付款人客户信息
				CustInfoEntity payeecustInfo = custInfoRepository.findOne(payeeBankCardInfo.getCustInfoEntity().getId());
				if(payeecustInfo == null){
					tradeResultInfoEntity.setTradeDate(new Date());
					tradeResultInfoEntity.setTradeStatus(Constant.OFFLINE_WITHDRAW_MANAGER_FAIL);
					tradeResultInfoEntity.setMemo("付款人不存在");
					tradeResultInfoEntity.setBasicModelProperty(userId, false);
					continue;
				}
					
				//判断客户是否存在
				CustInfoEntity custInfoEntity = custInfoRepository.findOne(bankCardInfoEntity.getCustInfoEntity().getId());
				if(custInfoEntity == null){
					tradeResultInfoEntity.setTradeDate(new Date());
					tradeResultInfoEntity.setTradeStatus(Constant.OFFLINE_WITHDRAW_MANAGER_FAIL);
					tradeResultInfoEntity.setMemo("收款人不存在");
					tradeResultInfoEntity.setBasicModelProperty(userId, false);
					continue;
				}
				//如果表格中姓名与系统中姓名不同，则不做处理
				if(!payeyCustName.equals(custInfoEntity.getCustName())) {
					tradeResultInfoEntity.setTradeDate(new Date());
					tradeResultInfoEntity.setTradeStatus(Constant.OFFLINE_WITHDRAW_MANAGER_FAIL);
					tradeResultInfoEntity.setMemo("表格与系统中姓名不同");
					tradeResultInfoEntity.setBasicModelProperty(userId, false);
					continue;
				}
				
				//验证交易流水是否存在
				List<TradeFlowInfoEntity> tradeFlowInfoEntities = tradeFlowInfoRepository.findByCustIdAndTradeTypeAndTradeStatus(custInfoEntity.getId(), 
						Constant.OPERATION_TYPE_43, Constant.TRADE_STATUS_02);
				if(tradeFlowInfoEntities.size() == 0){
					tradeResultInfoEntity.setTradeDate(new Date());
					tradeResultInfoEntity.setTradeStatus(Constant.OFFLINE_WITHDRAW_MANAGER_FAIL);
					tradeResultInfoEntity.setMemo("交易流水不存在");
					tradeResultInfoEntity.setBasicModelProperty(userId, false);
					continue;
				} else if (tradeFlowInfoEntities.size() > 1){
					tradeResultInfoEntity.setTradeDate(new Date());
					tradeResultInfoEntity.setTradeStatus(Constant.OFFLINE_WITHDRAW_MANAGER_FAIL);
					tradeResultInfoEntity.setMemo(String.format("客户%s有多条线下提现记录", custInfoEntity.getLoginName()));
					tradeResultInfoEntity.setBasicModelProperty(userId, false);
					continue;
				}
				
				TradeFlowInfoEntity tradeFlowInfoEntity = tradeFlowInfoEntities.get(0);
				
				//如果表格中金额与系统中提现金额不同，则不做处理
				if(ArithUtil.compare(tradeAmount, tradeFlowInfoEntity.getTradeAmount()) != 0){
					tradeResultInfoEntity.setTradeDate(new Date());
					tradeResultInfoEntity.setTradeStatus(Constant.OFFLINE_WITHDRAW_MANAGER_FAIL);
					tradeResultInfoEntity.setMemo("表格中金额与系统中提现金额不同");
					tradeResultInfoEntity.setBasicModelProperty(userId, false);
					continue;
				}
				
				//审核信息
				AuditInfoEntity auditInfoEntity = auditInfoRespository.findOne(tradeFlowInfoEntity.getRelatePrimary());
				if(auditInfoEntity == null){
					tradeResultInfoEntity.setTradeDate(new Date());
					tradeResultInfoEntity.setTradeStatus(Constant.OFFLINE_WITHDRAW_MANAGER_FAIL);
					tradeResultInfoEntity.setMemo("审核信息不存在");
					tradeResultInfoEntity.setBasicModelProperty(userId, false);
					continue;
				}

				String requestNo = numberService.generateTradeBatchNumber();
				//收款人账户
				AccountInfoEntity payeeAccount = accountInfoRepository.findByCustId(custInfoEntity.getId());
				if(payeeAccount == null){
					tradeResultInfoEntity.setTradeDate(new Date());
					tradeResultInfoEntity.setTradeStatus(Constant.OFFLINE_WITHDRAW_MANAGER_FAIL);
					tradeResultInfoEntity.setMemo("收款人账户不存在");
					tradeResultInfoEntity.setBasicModelProperty(userId, false);
					continue;
				}
				String auditStatus = Constant.AUDIT_STATUS_REfUSE; //审核状态
				String sysTradeStatus = Constant.OFFLINE_WITHDRAW_STATUS_FAIL; //系统处理结果
				if(tradeStatus.equals(Constant.OFFLINE_WITHDRAW_SUCCESS)){
					//收款人账户解冻
					custAccountService.updateAccount(payeeAccount, null, null, null, "8", SubjectConstant.TRADE_FLOW_TYPE_OFFLINE_UNFREEZE, requestNo,
							tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_OFFLINE_UNFREEZE_WITHDRAW, null, null, "线下提现解冻", userId);
					
					custAccountService.updateAccount(payeeAccount, null, null, null, "6", SubjectConstant.TRADE_FLOW_TYPE_OFFLINE_WITHDRAW,
							requestNo, tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_OFFLINE_WITHDRAW, null, null, "线下提现", userId);
					
					auditStatus = Constant.AUDIT_STATUS_PASS;
					sysTradeStatus = Constant.OFFLINE_WITHDRAW_STATUS_SUCCESS;
				} else {
					//收款人账户解冻
					custAccountService.updateAccount(payeeAccount, null, null, null, "8", SubjectConstant.TRADE_FLOW_TYPE_OFFLINE_UNFREEZE, requestNo,
							tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_OFFLINE_UNFREEZE_WITHDRAW, null, null, "线下提现解冻", userId);
				}
				
				//修改交易流水
				tradeFlowInfoEntity.setTradeStatus(sysTradeStatus);
				tradeFlowInfoEntity.setBasicModelProperty(userId, false);
				
				//修改审核信息
				String operBeforeContent = auditInfoEntity.getAuditStatus();
				auditInfoEntity.setAuditStatus(auditStatus);
				auditInfoEntity.setTradeStatus(tradeFlowInfoEntity.getTradeStatus());
				auditInfoEntity.setAuditTime(new Date());
				auditInfoEntity.setBasicModelProperty(userId, false);
				
				//修改处理结果
				//付款人信息补充
				tradeResultInfoEntity.setPayeeCustId(payeecustInfo.getId());
				tradeResultInfoEntity.setPayeeCustName(payeecustInfo.getCustName());
				tradeResultInfoEntity.setPayeeBankId(payeeBankCardInfo.getId());			
				//收款人信息补充
				tradeResultInfoEntity.setPayeyCustId(custInfoEntity.getId());
				tradeResultInfoEntity.setPayeyBankId(bankCardInfoEntity.getId());
				tradeResultInfoEntity.setTradeDate(new Date());
				tradeResultInfoEntity.setTradeStatus(Constant.OFFLINE_WITHDRAW_MANAGER_SUCCESS);
				tradeResultInfoEntity.setBasicModelProperty(userId, false);
				
				//记录日志
				LogInfoEntity logInfoEntity = new LogInfoEntity();
				logInfoEntity.setRelateType(Constant.TABLE_BAO_T_TRADE_FLOW_INFO);
				logInfoEntity.setRelatePrimary(tradeFlowInfoEntity.getId());
				logInfoEntity.setLogType(Constant.LOG_TYPE_OFFLINE_WITHDRAW_AUDIT);
				logInfoEntity.setOperPerson(userId);
				logInfoEntity.setOperBeforeContent(operBeforeContent);
				logInfoEntity.setOperAfterContent(auditStatus);
				logInfoEntity.setOperDesc(String.format("%s线下提现%s结果%s", custInfoEntity.getLoginName(), tradeAmount, tradeStatus));
				logInfoEntity.setBasicModelProperty(userId, true);
				logInfoEntityRepository.save(logInfoEntity);
			}
			
			return new ResultVo(true, "处理完成");
		}
		
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public ResultVo importWealthWithDraw(Map<String, Object> param)
				throws SLException {
			String fileName = (String) param.get("fileName");
			String userId = (String) param.get("userId");
			UserEntity user = userRepository.findOne(userId);
			if(user == null){
				return new ResultVo(false, "操作人不存在");
			}
			String email = user.getEmail();
			// 处理线下提现
			ResultVo excuteResult = excuteWealthWithDraw(param);
			// 提现成功发送邮件
			if (ResultVo.isSuccess(excuteResult)) {
				List<Map<String, Object>> tradeResultList = tradeResultInfoRepository
						.findMapByFileName(fileName);
				String filePath = "";
				String attrPath = ""; // 附件内容
				try {
					filePath = ExcelUtil.createExcel(tradeResultList,
							slcfUploadPath, fileName,
							ExcelConstants.WITHDRAW_TITLE_COLUMINDEX2KEY_MAP,
							ExcelConstants.WITHDRAW_COLUMINDEX2KEY_MAP);
				} catch (Exception e) {
					log.info("生成处理文档出错");
				}

				try {
					File file = new File(slcfUploadPath + filePath);
					FileInputStream inputFile = new FileInputStream(file);
					byte[] buffer = new byte[(int) file.length()];
					inputFile.read(buffer);
					inputFile.close();
					attrPath = org.apache.commons.codec.binary.Base64
							.encodeBase64String(buffer);
				} catch (FileNotFoundException e) {
					log.info("处理文件没有找到");
					e.printStackTrace();
				} catch (IOException e) {
					log.info("读文件出错");
					e.printStackTrace();
				}
				
				Map<String, Object> smsInfo = Maps.newHashMap();
				smsInfo.put("to", email);// 收件人邮箱地址
				smsInfo.put("type", MailType.ATTACHMENT);
				smsInfo.put("title", "线下提现批量导入处理结果列表");
				smsInfo.put("attrPathName", "线下提现批量导入处理结果列表.xlsx");
				smsInfo.put("attrPath", attrPath);
				smsInfo.put("content", "线下提现批量导入处理结果列表，请参看附件<线下提现批量导入处理结果列表.xlsx>。");
				smsInfo.put("fromNickName",Constant.FORM_PLAT_NAME);
				smsInfo.put("fromAddress",Constant.PLAT_EMAIL_ADDRESS);
		
				log.info("准备发送邮件给", smsInfo.get("to"));
				ResultVo sendEmailResult = emailService.sendEmail(smsInfo);
				log.info("发送邮件处理结果：",sendEmailResult.getValue("message"));
				if (!ResultVo.isSuccess(sendEmailResult)) {
					return new ResultVo(false, "发送邮件失败");
				}
			}

			return new ResultVo(true, "线下提现导入成功");
		}
		
		private String replaceBlank(Object obj) {	
			return obj == null ? "" : obj.toString().replaceAll("\\s+", "")
					.replaceFirst("^[\\x00-\\x200\\xA0]+", "")
					.replaceFirst("[\\x00-\\x20\\xA0]+$", "");
		}

	}
	
//	@Override
//	public ResultVo queryMyWeathSummary(Map<String, Object> params)throws SLException {
//		Map<String, Object> result = Maps.newHashMap();
//		List<Map<String, Object>> list = wealthInfoRepositoryCustom.queryMyWeathSummary(params); //所有的总额
//		BigDecimal totalInvestAmount = BigDecimal.ZERO; //累计年化投资额
//		BigDecimal totalIncomeAmount = BigDecimal.ZERO; //累计进账金额
//		for (Map<String, Object> map : list) {
//			totalInvestAmount = ArithUtil.add(totalInvestAmount, new BigDecimal(map.get("sumInvestAmount").toString()));
//			totalIncomeAmount = ArithUtil.add(totalIncomeAmount, new BigDecimal(map.get("sumIncomeAmount").toString()));
//		}
//		BigDecimal totalMonthlyInvestAmount = BigDecimal.ZERO; //本月年化投资额
//		BigDecimal totalMonthlyIncomeAmount = BigDecimal.ZERO; //本月进账金额
//		params.put("yearMonth", DateUtils.formatDate(new Date(), "yyyyMM"));
//		list = wealthInfoRepositoryCustom.queryMyWeathSummary(params); //月总额
//		for (Map<String, Object> map : list) {
//			totalMonthlyInvestAmount = ArithUtil.add(totalMonthlyInvestAmount, new BigDecimal(map.get("sumInvestAmount").toString()));
//			totalMonthlyIncomeAmount = ArithUtil.add(totalMonthlyIncomeAmount, new BigDecimal(map.get("sumIncomeAmount").toString()));
//		}
//		result.put("totalInvestAmount", totalInvestAmount);
//		result.put("totalIncomeAmount", totalIncomeAmount);
//		result.put("totalMonthlyInvestAmount", totalMonthlyInvestAmount);
//		result.put("totalMonthlyIncomeAmount", totalMonthlyIncomeAmount);
//		return new ResultVo(true, "查询成功", result);
//	}

	@Override
	public ResultVo queryMyWeathSummary(Map<String, Object> params) throws SLException {
		params.put("currentMonth", DateUtils.getCurrentDate("yyyyMM"));
		params.put("preMonth", DateUtils.formatDate(DateUtils.getAfterMonth(new Date(), -1), "yyyyMM"));
		Map<String, Object> result = commissionInfoRepositoryCustom.sumProjectAndWealthCommission(params);
		return new ResultVo(true, "查询汇总数据成功", result);
	}


	@Override
	public ResultVo queryMyWeathSummaryList(Map<String, Object> params)
			throws SLException {
		params.put("currentMonth", DateUtils.getCurrentDate("yyyyMM"));
		Map<String, Object> resultMap = Maps.newHashMap(); 
		Page<Map<String, Object>> page = commissionInfoRepositoryCustom.findProjectAndWealthCommission(params);
		resultMap.put("iTotalDisplayRecords", page.getTotalElements());
		resultMap.put("data", page.getContent());
		return new ResultVo(true, "查询业绩成功", resultMap);
	}

	@Override
	public ResultVo queryCustName(Map<String, Object> params)throws SLException {
		Map<String, Object> data = new HashMap<String, Object>();
		List<Map<String, Object>> list = custRecommendInfoRepositoryCustom.queryCustName(params);
		data.put("data", list);
		return new ResultVo(true, "查询成功！", data);
	}
	
	/**
	 * 新增客户银行卡
	 * @author  liyy
	 * @date    2016年5月31日 
	 * @param params Map <br>
     *      <tt>tradeFlowId             :String:交易过程流水ID(编辑时非空)</tt><br>
     *      <tt>custId                  :String:客户ID</tt><br>
     *      <tt>bankName                :String:银行名称</tt><br>
     *      <tt>bankCardNo              :String:银行卡号</tt><br>
     *      <tt>branchBankName          :String:支行名称</tt><br>
     *      <tt>openProvince            :String:开户行所在省</tt><br>
     *      <tt>openCity                :String:开户行所在市</tt><br>
     *      <tt>userId                  :String:创建人</tt><br>
     **     <tt>custName                :String:用户名</tt><br>
     **     <tt>credentialsCode         :String:证件号码</tt><br>
     *      <tt>attachmentList          :String:附件列表:List<Map<String, Object>></tt><br>
     *      	<tt>attachmentId            :String:附件ID（非空时编辑，否则新增）</tt><br>
     *      	<tt>attachmentType          :String:         附件类型</tt><br>
     *      	<tt>attachmentName          :String:         附件名称</tt><br>
     *      	<tt>storagePath             :String:         存储路径</tt><br>
	 * @return ResultVo
	 * 		<tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
     *      <tt>data
     *      </tt><br>
     *  @throws SLException
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo saveCustBank(Map<String, Object> param) throws SLException {
		String custName = (String)param.get("custName");
		String credentialsCode = (String)param.get("credentialsCode");
		
		if(StringUtils.isEmpty(param.get("tradeFlowId"))) {
			/************ 新增 ************/
			// 判断客户有没有实名认证
			int num = custApplyInfoRepository.checkRealNameAuth(param.get("custId").toString());
			if(num == 0){
				log.error("该客户还未实名认证，请先实名认证!");
				throw new SLException("该客户还未实名认证，请先实名认证!");
			}
		}
		int countName = custInfoRepository.findCountBycustIdAndCustName(param.get("custId").toString(), custName);
		if(countName == 0) {
			log.error("您提交的银行卡开户名与实名认证信息不符，请绑定本人银行卡！");
			throw new SLException("您提交的银行卡开户名与实名认证信息不符，请绑定本人银行卡！");
		}	
		int countCode = custInfoRepository.findCountBycustIdAndCredentialsCode(param.get("custId").toString(), credentialsCode);
		if(countCode == 0) {
			log.error("你提交的开户证件号与实名认证信息不符，请绑定本人银行卡！");
			throw new SLException("你提交的开户证件号与实名认证信息不符，请绑定本人银行卡！");
		}
		
		
		String tradeFlowId = (String)param.get("tradeFlowId");
		String userId = (String) param.get("userId");
		String bankName = null;
		String appSource = (String) param.get("appSource"); // 来源
		appSource = Strings.nullToEmpty(StringUtils.isEmpty(appSource) ? Constant.INVEST_SOURCE_PC : appSource).toLowerCase();
		try {
			// 传入是(code)转化成name
			bankName = custApplyInfoRepositoryCustom.getNameByCode("8", param.get("bankName").toString());
		} catch (Exception e) {
			log.error("银行名称不正确，请重新输入");
			throw new SLException("银行名称不正确，请重新输入");
		}
		
//		// 校验银行卡和银行是否匹配
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("bankCardNo", param.get("bankCardNo").toString().replaceAll(" ", ""));
//		Map<String, Object> rs = bankCardService.queryThirdBankByCardNo(map);
//		if(!bankName.equals(rs.get("bankName"))){
//			log.error("银行名称应是["+rs.get("bankName")+"]，请重新选择");
//			throw new SLException("银行名称应是["+rs.get("bankName")+"]，请重新选择");
//		}

		// 交易流水过程表
		TradeFlowInfoEntity tradeFlowInfoEntity = null;
		// 附件表
		List<Map<String, Object>> attachmentList = (List<Map<String, Object>>) param.get("attachmentList");
		
		if(StringUtils.isEmpty(param.get("tradeFlowId"))){
			/************ 新增 ************/
//			// 判断客户有没有实名认证
//			int num = custApplyInfoRepository.checkRealNameAuth(param.get("custId").toString());
//			if(num == 0){
//				log.error("该客户还未实名认证，请先实名认证!");
//				throw new SLException("该客户还未实名认证，请先实名认证!");
//			}			
			// 判断同一客户同一卡号不能重复存在
			int count = custApplyInfoRepositoryCustom.checkCustAndCardNo(param.get("custId").toString(), param.get("bankCardNo").toString().replaceAll(" ", ""));
			if(count > 0){
				log.error("该卡已绑定或在审核中");
				throw new SLException("该卡已绑定或在审核中");
			}
			// 取得账户ID
			String custAccountId = accountInfoRepository.findCustAccountIdByCustId(param.get("custId").toString());
			
			// 交易流水过程表
			tradeFlowInfoEntity = new TradeFlowInfoEntity();
			tradeFlowInfoEntity.setBasicModelProperty(userId, true);
			tradeFlowInfoEntity.setRelateType(Constant.TABLE_BAO_T_CUST_INFO);
			tradeFlowInfoEntity.setRelatePrimary((String) param.get("custId"));
			tradeFlowInfoEntity.setCustId((String) param.get("custId"));
			tradeFlowInfoEntity.setCustAccountId(custAccountId);
			tradeFlowInfoEntity.setTradeType(Constant.OPERATION_TYPE_44);
			tradeFlowInfoEntity.setTradeStatus(Constant.TRADE_STATUS_01);
			tradeFlowInfoEntity.setTradeNo(numberService.generateTradeNumber());
			tradeFlowInfoEntity.setTradeDate(new Date());
			tradeFlowInfoEntity.setTradeDesc(Constant.OPERATION_TYPE_44);
			tradeFlowInfoEntity.setBankName(bankName);
			tradeFlowInfoEntity.setBankCardNo(param.get("bankCardNo").toString().replaceAll(" ", ""));
			tradeFlowInfoEntity.setBranchBankName((String) param.get("branchBankName"));
			tradeFlowInfoEntity.setTradeSource(appSource);
			tradeFlowInfoEntity.setOpenProvince((String) param.get("openProvince"));
			tradeFlowInfoEntity.setOpenCity((String) param.get("openCity"));
			tradeFlowInfoEntity = tradeFlowInfoRepository.save(tradeFlowInfoEntity);
			// 审核表
			AuditInfoEntity auditInfoEntity = new AuditInfoEntity();
			auditInfoEntity.setBasicModelProperty(userId, true);
			auditInfoEntity.setCustId((String) param.get("custId"));
			auditInfoEntity.setRelateType(Constant.TABLE_BAO_T_TRADE_FLOW_INFO);
			auditInfoEntity.setRelatePrimary(tradeFlowInfoEntity.getId());
			auditInfoEntity.setApplyType(Constant.OPERATION_TYPE_44);
			auditInfoEntity.setApplyTime(new Date());
			auditInfoEntity.setAuditStatus(Constant.CUST_MANAGER_AUDIT_STATUS_UNREVIEW);
			auditInfoEntity.setTradeStatus(Constant.TRADE_STATUS_01);
			auditInfoRepository.save(auditInfoEntity);
		} else {
			/************ 编辑 ************/
			// 交易流水过程表
			tradeFlowInfoEntity = tradeFlowInfoRepository.findOne(tradeFlowId);
			tradeFlowInfoEntity.setBasicModelProperty(userId, false);
			tradeFlowInfoEntity.setTradeStatus(Constant.TRADE_STATUS_02);
			tradeFlowInfoEntity.setBankName(bankName);
			tradeFlowInfoEntity.setBankCardNo(param.get("bankCardNo").toString().replaceAll(" ", ""));
			tradeFlowInfoEntity.setBranchBankName((String) param.get("branchBankName"));
			tradeFlowInfoEntity.setTradeSource(appSource);
			tradeFlowInfoEntity.setOpenProvince((String) param.get("openProvince"));
			tradeFlowInfoEntity.setOpenCity((String) param.get("openCity"));
			// 审核表
			AuditInfoEntity auditInfoEntity = auditInfoRepository.findAuditInfoEntityByRelatePrimary(tradeFlowInfoEntity.getId());
			auditInfoEntity.setBasicModelProperty(userId, false);
			auditInfoEntity.setAuditStatus(Constant.CUST_MANAGER_AUDIT_STATUS_UNREVIEW);
			auditInfoEntity.setTradeStatus(Constant.TRADE_STATUS_02);
			
			if(attachmentList != null && attachmentList.size() > 0){
				// 旧附件更新为无效
				attachmentRepository.updateByTypeAndPrimary(Constant.TABLE_BAO_T_TRADE_FLOW_INFO, tradeFlowInfoEntity.getId());
			}
		}

		if(attachmentList != null && attachmentList.size() > 0){
			List<AttachmentInfoEntity> attachmentNewList = new ArrayList<AttachmentInfoEntity>();
			for (int i = 0; i < attachmentList.size(); i++) {
				Map<String, Object> temp = attachmentList.get(i);
				AttachmentInfoEntity attachmentInfoEntityNew = new AttachmentInfoEntity();
				attachmentInfoEntityNew.setBasicModelProperty(userId, true);
				attachmentInfoEntityNew.setRelateType(Constant.TABLE_BAO_T_TRADE_FLOW_INFO);
				attachmentInfoEntityNew.setRelatePrimary(tradeFlowInfoEntity.getId());
				attachmentInfoEntityNew.setAttachmentType((String) temp.get("attachmentType"));
				attachmentInfoEntityNew.setAttachmentName((String) temp.get("attachmentName"));
				attachmentInfoEntityNew.setStoragePath((String) temp.get("storagePath"));
				attachmentInfoEntityNew.setDocType((String) temp.get("docType"));
				attachmentNewList.add(attachmentInfoEntityNew);
			}
			attachmentRepository.save(attachmentNewList);
		}
		
		// 日志信息
		LogInfoEntity logInfoEntity = new LogInfoEntity();
		logInfoEntity.setBasicModelProperty(userId, true);
		logInfoEntity.setRelateType(Constant.TABLE_BAO_T_TRADE_FLOW_INFO);
		logInfoEntity.setRelatePrimary(tradeFlowInfoEntity.getId());
		logInfoEntity.setLogType(Constant.OPERATION_TYPE_44);// 附属银行卡申请
		logInfoEntity.setOperDesc(tradeFlowInfoEntity.getCustId() +":"+ Constant.OPERATION_TYPE_44);
		logInfoEntity.setMemo(Constant.OPERATION_TYPE_44);
		logInfoEntity.setOperPerson(userId);
		logInfoEntity.setOperAfterContent(Constant.CUST_MANAGER_AUDIT_STATUS_UNREVIEW);
		logInfoEntityRepository.save(logInfoEntity);
		
		return new ResultVo(true, "新增客户银行卡操作成功");
	}
	
	
	/**
	 * 查询业务员信息
	 * @author  liyy
	 * @date    2016年5月31日 
	 * @param params Map <br>
     *      <tt>custId                  :String:客户ID</tt><br>
	 * @return ResultVo
	 * 		<tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
     *      <tt>data:object
     *      	<tt>data:Map
     *      		<tt>custId         :String:业务员ID</tt><br>
     *      		<tt>custName       :String:业务员用户名</tt><br>
     *      		<tt>credentialsCode:String:业务员证件号码</tt><br>
     *      		<tt>mobile         :String:业务员手机号</tt><br>
     *      		<tt>registerDate   :String:业务员注册时间</tt><br>
     *      	</tt><br>
     *      </tt><br>
     *  @throws SLException
	 */
	public ResultVo queryCustManagerByCustId(Map<String, Object> param) throws SLException {
		String custId = (String) param.get("custId");
		
		CustInfoEntity custInfo = custInfoRepository.queryCustManagerByCustId(custId);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("custId", custInfo.getId());
		map.put("custName", custInfo.getCustName());
		map.put("credentialsCode", custInfo.getCredentialsCode());
		map.put("mobile", custInfo.getMobile());
		map.put("registerDate", custInfo.getCreateDate());
		
		return new ResultVo(true, "查询业务员信息操作成功", map);
	}
	
	/**
	 * 线下充值列表（客户自己的数据）
	 * 
	 * @author liyy
	 * @date   2016年6月1日
	 * @param param
     *      <tt>start           :String:起始值</tt><br>
     *      <tt>length          :String:长度</tt><br>
     *      <tt>custName        :String:用户名（可选）</tt><br>
     *      <tt>credentialsCode :String:证件号码(可选)</tt><br>
     *      <tt>mobile          :String:手机号（可选）</tt><br>
     *      <tt>auditStatus     :String:审核状态（可选）</tt><br>
     *      <tt>beginOperateDate:String:开始创建时间（可选）</tt><br>
     *      <tt>endOperateDate  :String:结束创建时间（可选）</tt><br>
     *      <tt>custId          :String:客户ID</tt><br>
	 * @return
	 *      <tt>tradeFlowId    :String:交易过程ID</tt><br>
     *      <tt>custName       :String:用户名</tt><br>
     *      <tt>credentialsCode:String:证件号码</tt><br>
     *      <tt>mobile         :String:手机号</tt><br>
     *      <tt>tradeType      :String:交易类型</tt><br>
     *      <tt>tradeAmount    :String:交易金额</tt><br>
     *      <tt>createUser     :String:创建人</tt><br>
     *      <tt>createDate     :String:创建时间</tt><br>
     *      <tt>auditStatus    :String:审核状态</tt><br>
	 */
	public ResultVo queryWealthRechargeListByCustId(Map<String, Object> param){
		Map<String, Object> resultMap = Maps.newHashMap();
		param.put("tradeType", SubjectConstant.TRADE_FLOW_TYPE_OFFLINE_RECHARGE);
		Page<Map<String, Object>> page = tradeFlowInfoRepositoryCustom.queryWealthRechargeListByCustId(param);
		resultMap.put("iTotalDisplayRecords", page.getTotalElements());
		resultMap.put("data", page.getContent());
		return new ResultVo(true, "线下充值列表查询成功", resultMap);
	}

	/**
	 * 转移线下理财客户
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo transferOffLineCustManager(Map<String, Object> map) throws SLException{
		String custId = (String) map.get("custId");
		Date investStartDate = (Date) map.get("investStartDate");
		String manageCardId = (String) map.get("manageCardId");
		//线上客户
		CustInfoEntity custInfo = custInfoRepository.findOne(custId);
		//线上客户经理关系
		CustRecommendInfoEntity custRecommendInfoEntity =  custRecommendInfoRepository.findByQuiltCustIdAndRecordStatus(custId, Constant.VALID_STATUS_VALID);
		//线下客户经理
		CustInfoEntity custManageOffLineInfo = custInfoRepository.findByCredentialsCodeAndIsEmployee(manageCardId, Constant.IS_RECOMMEND_YES);
		//线下客户经理不是线上客户经理
		if(null == custManageOffLineInfo) {
			return new ResultVo(true);
		}
		//判断线下客户经理是不是客户自己，是则不处理
		if(manageCardId.equals(custInfo.getCredentialsCode())) {
			custInfo.setWealthFlag(Constant.WEALTH_FLAG_01);
			return new ResultVo(true);
		}
		
		//线上存在客户经理
		if (null != custRecommendInfoEntity) {
			//线上客户经理
			CustInfoEntity custManageOnLineInfo = custInfoRepository.findOne(custRecommendInfoEntity.getCustId());
			//3当线上客户经理=线下的客户经理--无需处理
			if(manageCardId.equals(custManageOnLineInfo.getCredentialsCode())) {
				custInfo.setWealthFlag(Constant.WEALTH_FLAG_01);
			} else { //线下理财客户经理 ！= 线上客户经理
				//线上投资早于线下投资信息
				List<InvestInfoEntity> investList = investInfoRepository.findInvestInfoByInvestDate(investStartDate, custInfo.getId());
				//线上客户投资晚于线下--上线的客户经理改成线下的客户经理
				if(null == investList || investList.size() == 0) {
					modifyCustManage(custInfo.getId(), custManageOffLineInfo.getId());
				}
				custInfo.setWealthFlag(Constant.WEALTH_FLAG_01);
			}
		
		// 线上不存在客户经理--把客户经理改成线下的客户经理	
		}else { 
			modifyCustManage(custInfo.getId(), custManageOffLineInfo.getId());
			custInfo.setWealthFlag(Constant.WEALTH_FLAG_01);
		}
		return new ResultVo(true);
	}

	//修改客户经理
	private ResultVo modifyCustManage(String custId, String newCustmanageId) throws SLException {
		Map<String, Object> params = Maps.newHashMap();
		params.put("custId", custId);
		params.put("newCustManagerId", newCustmanageId);
		params.put("userId", Constant.CUST_ADMIN_ID);
		params.put("memo", "定时转移客户经理");
		ResultVo result = saveCustTransferForSyncOldOffLineWealth(params);
		if(!ResultVo.isSuccess(result)) {
			throw new SLException((String) result.getValue("message"));
		}
		return result;
	}

	/**
	 * 业绩详情BySalesMan
	 * @author liyy
	 * @param params
     *      <tt>custId     :String:客户ID（可选）</tt><br>
     *      <tt>investId   :String:投资ID</tt><br>
     *      <tt>productType:String:产品类型（优选计划、优选项目、债权转让）</tt><br>
	 * @return ResultVo
     *      <tt>investDate     :String:投资时间</tt><br>
     *      <tt>investAmount   :String:投资金额</tt><br>
     *      <tt>lendingType    :String:计划名称</tt><br>
     *      <tt>lendingNo      :String:项目期数</tt><br>
     *      <tt>typeTerm       :String:项目期限(月)</tt><br>
     *      <tt>yearRate       :String:年化收益率</tt><br>
     *      <tt>awardRate      :String:奖励利率</tt><br>
     *      <tt>effectDate     :String:生效日期（起息日期）</tt><br>
     *      <tt>endDate        :String:到期日期（到期日期）</tt><br>
     *      <tt>investStatus   :String:投资状态（当前状态）</tt><br>
     *      <tt>custName       :String:用户名</tt><br>
     *      <tt>credentialsCode:String:证件号码</tt><br>
     *      <tt>mobile         :String:手机号</tt><br>
     *      <tt>registerDate   :String:注册时间</tt><br>
     *      <tt>productId      :String:产品ID</tt><br>
	 */
	@SuppressWarnings("unchecked")
	public ResultVo queryCustWealthDetailByManager(Map<String, Object> params)
			throws SLException {
		String productType = params.get("productType").toString();
		String investId = params.get("investId").toString();
		InvestInfoEntity investInfoEntity = investInfoRepository.findOne(investId);
		if(null == investInfoEntity){
			throw new SLException("投资记录不存在！");
		}
		CustInfoEntity custInfoEntity = custInfoRepository.findOne(investInfoEntity.getCustId());
		String credentialsCode = custInfoEntity.getCredentialsCode().toString();
		String mobile = custInfoEntity.getMobile().toString();
		
		Map<String, Object> data = Maps.newHashMap();
		if(Constant.PRODUCT_TYPE_06.equals(productType)){
			if(StringUtils.isEmpty(investInfoEntity.getWealthId())){
				throw new SLException("优选计划不存在！");
			}
			Map<String, Object> newParam = Maps.newHashMap();
			newParam.put("wealthId", investInfoEntity.getWealthId());
			ResultVo wealthVo = wealthInfoService.queryWealthDetail(newParam);
			Map<String, Object> result = (Map<String, Object>) wealthVo.getValue("data");
			
			data.put("investDate", investInfoEntity.getInvestDate());
			data.put("investAmount", investInfoEntity.getInvestAmount());
			data.put("lendingType", new StringBuffer().append(result.get("lendingType")).append(result.get("lendingNo")).append("期").toString());
			data.put("lendingNo", result.get("lendingNo"));
			data.put("typeTerm", result.get("typeTerm")+"个月");
			data.put("yearRate", result.get("yearRate"));
			data.put("awardRate", result.get("awardRate"));
			data.put("effectDate", result.get("effectDate"));
			data.put("endDate", result.get("endDate"));
			data.put("investStatus", investInfoEntity.getInvestStatus());
			data.put("custName", custInfoEntity.getCustName());
			data.put("credentialsCode", new StringBuffer().append(credentialsCode.substring(0, 3)).append("****").append(credentialsCode.substring(credentialsCode.length()-4).toString()));
			data.put("mobile", new StringBuffer().append(mobile.substring(0, 3)).append("****").append(mobile.substring(mobile.length()-4).toString()));
			data.put("registerDate", custInfoEntity.getCreateDate());
			data.put("productId", result.get("wealthId"));
			
		} else if(Constant.PRODUCT_TYPE_08.equals(productType)){
			if(StringUtils.isEmpty(investInfoEntity.getLoanId())){
				throw new SLException("优选项目不存在！");
			}
			Map<String, Object> newParam = Maps.newHashMap();
			newParam.put("disperseId", investInfoEntity.getLoanId());
			ResultVo loanVo = loanManagerService.queryDisperseDetail(newParam);
			Map<String, Object> result = (Map<String, Object>) loanVo.getValue("data");
			if(result != null && !StringUtils.isEmpty(result.get("disperseId"))){
				data.put("investDate", investInfoEntity.getInvestDate());
				data.put("investAmount", investInfoEntity.getInvestAmount());
				data.put("lendingType", result.get("loanTitle"));
				data.put("lendingNo", result.get("loanNo"));
				data.put("typeTerm", "天".equals(result.get("loanUnit"))?(result.get("typeTerm")+"天"):(result.get("typeTerm")+"个月"));
				data.put("yearRate", result.get("yearRate"));
				data.put("awardRate", BigDecimal.ZERO);
				data.put("effectDate", result.get("interestStartDate"));
				data.put("endDate", result.get("expireDate"));
				data.put("investStatus", investInfoEntity.getInvestStatus());
				data.put("custName", custInfoEntity.getCustName());
				data.put("credentialsCode", new StringBuffer().append(credentialsCode.substring(0, 3)).append("****").append(credentialsCode.substring(credentialsCode.length()-4).toString()));
				data.put("mobile", new StringBuffer().append(mobile.substring(0, 3)).append("****").append(mobile.substring(mobile.length()-4).toString()));
				data.put("registerDate", custInfoEntity.getCreateDate());
				data.put("productId", result.get("disperseId"));
				
				WealthHoldInfoEntity wealthHoldInfoEntity = wealthHoldInfoRepository.findByInvestId(investId);
				if(wealthHoldInfoEntity != null) {
					// 已转让占比 = 转让比例/(持有比例+转让比例)
					data.put("transferScale", ArithUtil.div(wealthHoldInfoEntity.getTradeScale(), ArithUtil.add(wealthHoldInfoEntity.getHoldScale(), wealthHoldInfoEntity.getTradeScale())));
				}
			}
		} else if(Constant.PRODUCT_TYPE_10.equals(productType)){
			if(StringUtils.isEmpty(investInfoEntity.getTransferApplyId())){
				throw new SLException("债权转让不存在！");
			}
			Map<String, Object> newParam = Maps.newHashMap();
			newParam.put("disperseId", investInfoEntity.getLoanId());
			ResultVo loanVo = loanManagerService.queryDisperseDetail(newParam);
			Map<String, Object> result = (Map<String, Object>) loanVo.getValue("data");
			if(result != null && !StringUtils.isEmpty(result.get("disperseId"))){
				data.put("investDate", investInfoEntity.getInvestDate());
				data.put("investAmount", investInfoEntity.getInvestAmount());
				data.put("lendingType", result.get("loanTitle"));
				data.put("lendingNo", result.get("loanNo"));
				data.put("typeTerm", "天".equals(result.get("loanUnit"))?(result.get("typeTerm")+"天"):(result.get("typeTerm")+"个月"));
				data.put("yearRate", result.get("yearRate"));
				data.put("awardRate", BigDecimal.ZERO);
				data.put("effectDate", DateUtils.parseDate(investInfoEntity.getEffectDate(), "yyyyMMdd"));
				data.put("endDate", result.get("expireDate"));
				data.put("investStatus", investInfoEntity.getInvestStatus());
				data.put("custName", custInfoEntity.getCustName());
				data.put("credentialsCode", new StringBuffer().append(credentialsCode.substring(0, 3)).append("****").append(credentialsCode.substring(credentialsCode.length()-4).toString()));
				data.put("mobile", new StringBuffer().append(mobile.substring(0, 3)).append("****").append(mobile.substring(mobile.length()-4).toString()));
				data.put("registerDate", custInfoEntity.getCreateDate());
				data.put("productId", result.get("disperseId"));
				data.put("transferApplyId", investInfoEntity.getTransferApplyId());
				
				WealthHoldInfoEntity wealthHoldInfoEntity = wealthHoldInfoRepository.findByInvestId(investId);
				if(wealthHoldInfoEntity != null) {
					// 已转让占比 = 转让比例/(持有比例+转让比例)
					data.put("transferScale", ArithUtil.div(wealthHoldInfoEntity.getTradeScale(), ArithUtil.add(wealthHoldInfoEntity.getHoldScale(), wealthHoldInfoEntity.getTradeScale())));
				}
			}
		}
 		return new ResultVo(true, "业绩详情查询成功", data);
	}

    @Override
    public ResultVo queryNextTeamCustWealthList(Map<String, Object> params) {
        try {
            Map<String, Object> resultMap = Maps.newHashMap();
            Page<Map<String, Object>> page = commissionInfoRepositoryCustom.queryNextTeamCustWealthList(params);
            if (null == page) {
                resultMap.put("iTotalDisplayRecords", 0);
                resultMap.put("data", null);
            } else {
                resultMap.put("iTotalDisplayRecords", page.getTotalElements());
                resultMap.put("data", page.getContent());

                List<Map<String, Object>> contents = page.getContent();
                Map<String, Map<String, Object>> temp = Maps.newHashMap();
                for (Map<String, Object> content : contents) {
                    String jobNo = CommonUtils.emptyToString(content.get("jobNo"));
                    // 根据下属姓名查询工号
                    /*String managerName = commissionInfoRepositoryCustom.getJobNoByManagerName(jobNo);
                    content.put("subName", managerName);
                    content.put("subRanking", 99);*/
                    Map<String, Object> map = temp.get(jobNo);
                    if (null == map) {
                        temp.put(jobNo, content);
                    } else {
                        String subRanking = CommonUtils.emptyToString(content.get("subRanking"));
                        String subRankingTemp = CommonUtils.emptyToString(map.get("subRanking"));
                        // 相同工号的投资金额累加
                        BigDecimal investAmount = CommonUtils.emptyToDecimal(map.get("investAmount"));
                        BigDecimal investAmountCurr = CommonUtils.emptyToDecimal(content.get("investAmount"));
                        map.put("investAmount", ArithUtil.add(investAmount, investAmountCurr));

                        // 查询当前级别
//                        int subRankingCurr = commissionInfoRepositoryCustom.getCurrentRanking(jobNo);
//                        map.put("subRanking", subRankingCurr);
                    }
                }

                List<Map<String, Object>> sumList = Lists.newArrayList();
                for (String jobNo : temp.keySet()) {
                    Map<String, Object> map = temp.get(jobNo);
                    sumList.add(map);
                }

                Collections.sort(sumList, new Comparator<Map<String,Object>>() {
                    public int compare(Map<String, Object> o1,Map<String, Object> o2) {
                        //o1，o2是list中的Map，可以在其内取得值，按其排序，此例为降序，s1和s2是排序字段值
                        BigDecimal s1 = CommonUtils.emptyToDecimal(o1.get("investAmount"));
                        BigDecimal s2 = CommonUtils.emptyToDecimal(o2.get("investAmount"));
                        if(s1.compareTo(s2) > 0) {
                            return -1;
                        } else {
                            return 1;
                        }
                    }
                });
                // 返回的结果集
                resultMap.put("data", sumList);
            }
            return new ResultVo(true, "查询业绩列表成功", resultMap);
        } catch (Exception e) {
            return new ResultVo(false, "查询业绩列表失败", e.getMessage());
        }
    }

    @Override
    public ResultVo queryNextTeamCustWealthDetail(Map<String, Object> params) {
        try {
            Map<String, Object> resultMap = Maps.newHashMap();
            Page<Map<String, Object>> page = commissionInfoRepositoryCustom.queryNextTeamCustWealthDetail(params);
            if (null == page) {
                resultMap.put("iTotalDisplayRecords", 0);
                resultMap.put("data", null);
            } else {
                resultMap.put("iTotalDisplayRecords", page.getTotalElements());
                resultMap.put("data", page.getContent());
            }
            return new ResultVo(true, "查询业绩详情成功", resultMap);
        } catch (Exception e) {
            return new ResultVo(false, "查询业绩详情失败", e.getMessage());
        }
    }
}
