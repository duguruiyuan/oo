package com.slfinance.shanlincaifu.service.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AccountFlowInfoEntity;
import com.slfinance.shanlincaifu.entity.AccountInfoEntity;
import com.slfinance.shanlincaifu.entity.AuditInfoEntity;
import com.slfinance.shanlincaifu.entity.CustInfoEntity;
import com.slfinance.shanlincaifu.entity.LogInfoEntity;
import com.slfinance.shanlincaifu.entity.TransAccountInfoEntity;
import com.slfinance.shanlincaifu.repository.AccountFlowInfoRepository;
import com.slfinance.shanlincaifu.repository.AccountInfoRepository;
import com.slfinance.shanlincaifu.repository.AuditInfoRepository;
import com.slfinance.shanlincaifu.repository.CustInfoRepository;
import com.slfinance.shanlincaifu.repository.LogInfoEntityRepository;
import com.slfinance.shanlincaifu.repository.TransAccountInfoRepository;
import com.slfinance.shanlincaifu.repository.custom.TransAccountInfoRepositoryCustom;
import com.slfinance.shanlincaifu.service.AccountFlowService;
import com.slfinance.shanlincaifu.service.FlowNumberService;
import com.slfinance.shanlincaifu.service.TransAccountService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.SubjectConstant;
import com.slfinance.vo.ResultVo;

@Service("transAccountService")
public class TransAccountServiceImpl implements TransAccountService {
	
	@Autowired
	private TransAccountInfoRepository transAccountInfoRepository;
	
	@Autowired
	private TransAccountInfoRepositoryCustom transAccountInfoRepositoryCustom;
	
	@Autowired
	private CustInfoRepository custInfoRepository;
	
	@Autowired
	private AccountInfoRepository accountInfoRepository;
	
	@Autowired
	AuditInfoRepository auditInfoRepository;
	
	@Autowired
	private LogInfoEntityRepository logInfoEntityRepository;
	
	@Autowired
	FlowNumberService numberService;
	
	@Autowired
	private AccountFlowInfoRepository accountFlowInfoRepository;
	
	@Autowired
	private AccountFlowService accountFlowService;

	@Override
	public ResultVo queryCompanyTransAccountList(Map<String, Object> param) {
		Map<String, Object> resultMap = Maps.newHashMap();
		if(StringUtils.isEmpty(param.get("projectType"))){
			List<String> projectTypeList = Arrays.asList(Constant.CUST_KIND_02, Constant.CUST_KIND_03);
			param.put("projectTypeList", projectTypeList);
		}
		Page<Map<String, Object>> page = transAccountInfoRepositoryCustom.findCompanyTransAccountList(param);
		resultMap.put("iTotalDisplayRecords", page.getTotalElements());
		resultMap.put("data", page.getContent());
		return new ResultVo(true, "调账列表查询成功", resultMap);
	}

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo saveCompanyTransAccount(Map<String, Object> params) throws SLException {
		String companyId = (String) params.get("companyId");
		CustInfoEntity custInfoEntity = custInfoRepository.findOne(companyId);
		if(custInfoEntity == null){
			return new ResultVo(false, "没有找到该公司信息");
		}
		AccountInfoEntity accountInfoEntity = accountInfoRepository.findByCustId(custInfoEntity.getId());
		if(accountInfoEntity == null){
			return new ResultVo(false, "该公司没有账户");
		}
		TransAccountInfoEntity transAccountInfoEntity = new TransAccountInfoEntity();
		//转账类型
		String transType = (String) params.get("transType");
		BigDecimal tradeAmount = new BigDecimal(params.get("transAmount").toString());
		if(Constant.ACCOUNT_TRANS_DIRECTION_IN.equals(transType)){
			transAccountInfoEntity.setIntoAccount(accountInfoEntity.getAccountNo());
		}else if(Constant.ACCOUNT_TRANS_DIRECTION_OUT.equals(transType)){
			if(accountInfoEntity.getAccountAvailableAmount().compareTo(tradeAmount) == -1){
				return new ResultVo(false, "账户可用余额不足");
			}
			transAccountInfoEntity.setExpendAccount(accountInfoEntity.getAccountNo());
		}
		transAccountInfoEntity.setTransType(transType);
		transAccountInfoEntity.setTradeAmount(tradeAmount);
		transAccountInfoEntity.setAuditStatus(Constant.AUDIT_PROJECT_STATUS_REVIEWING);
		transAccountInfoEntity.setBasicModelProperty((String)params.get("userId"), true);
		transAccountInfoEntity.setMemo((String) params.get("transMemo"));
		transAccountInfoRepository.save(transAccountInfoEntity);
		return new ResultVo(true, "调账申请已提交，请等待审核");
	}

	@Override
	public ResultVo queryCompanyTransAccountById(Map<String, Object> params) {
		return transAccountInfoRepositoryCustom.findCompanyTransAccountById(params);
	}

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo auditCompanyTransAccount(Map<String, Object> params) throws SLException {
		String transId = (String) params.get("transId");
		String auditStatus = (String) params.get("auditStatus");
		String auditUser = (String) params.get("auditUser");
		String auditMemo = (String) params.get("auditMemo");
		
		//转账信息
		TransAccountInfoEntity transAccountInfoEntity = transAccountInfoRepository.findOne(transId);
		if(transAccountInfoEntity == null){
			return new ResultVo(false, "没有此转账记录");
		}
		
		String operBeforeStatus= transAccountInfoEntity.getAuditStatus();
		if(!Constant.AUDIT_PROJECT_STATUS_REVIEWING.equals(operBeforeStatus)){
			return new ResultVo(false, "记录的审核状态不是待审核，不能进行审核操作");
		}
		
		if(Constant.AUDIT_PROJECT_STATUS_PASS.equals(auditStatus) && 
				Constant.ACCOUNT_TRANS_DIRECTION_OUT.equals(transAccountInfoEntity.getTransType())){
			AccountInfoEntity accountInfoEntity = accountInfoRepository.findByAccountNo(transAccountInfoEntity.getExpendAccount());
			if(accountInfoEntity == null){
				return new ResultVo(false, "账户不存在");
			}
			if(accountInfoEntity.getAccountAvailableAmount().compareTo(transAccountInfoEntity.getTradeAmount()) == -1){
				return new ResultVo(false, "账户可用余额不足");
			}
		}

		transAccountInfoEntity.setAuditStatus(auditStatus);
		transAccountInfoEntity.setBasicModelProperty(auditUser, false);
		
		//审核
		AuditInfoEntity auditInfoEntity = new AuditInfoEntity();
		auditInfoEntity.setRelateType(Constant.TABLE_BAO_T_TRANS_ACCOUNT_INFO);
		auditInfoEntity.setRelatePrimary(transId);
		auditInfoEntity.setApplyType(transAccountInfoEntity.getTransType());
		auditInfoEntity.setTradeAmount(transAccountInfoEntity.getTradeAmount());
		auditInfoEntity.setApplyTime(new Date());
		auditInfoEntity.setAuditTime(new Date());
		auditInfoEntity.setAuditUser(auditUser);
		auditInfoEntity.setAuditStatus(auditStatus);
		auditInfoEntity.setBasicModelProperty(auditUser, true);
		auditInfoEntity.setMemo(auditMemo);
		auditInfoRepository.save(auditInfoEntity);
		
		//日志
		LogInfoEntity logInfoEntity = new LogInfoEntity();
		logInfoEntity.setRelateType(Constant.TABLE_BAO_T_AUDIT_INFO);
		logInfoEntity.setRelatePrimary(auditInfoEntity.getId());
		logInfoEntity.setLogType(Constant.LOG_TYPE_AUDIT);
		logInfoEntity.setOperBeforeContent(operBeforeStatus);
		logInfoEntity.setOperAfterContent(auditStatus);
		logInfoEntity.setOperPerson(auditUser);
		logInfoEntity.setBasicModelProperty(auditUser, true);
		logInfoEntity.setMemo(auditMemo);
		logInfoEntityRepository.save(logInfoEntity);
		
		String accountNo = "";
		String tradeType = "";
		BigDecimal tradeAmount = transAccountInfoEntity.getTradeAmount() == null ? BigDecimal.ZERO : transAccountInfoEntity.getTradeAmount();
		if(Constant.ACCOUNT_TRANS_DIRECTION_IN.equals(transAccountInfoEntity.getTransType())){
			accountNo = transAccountInfoEntity.getIntoAccount();
			tradeType = Constant.OPERATION_TYPE_05;
		} else {
			accountNo = transAccountInfoEntity.getExpendAccount();
			tradeType = Constant.OPERATION_TYPE_06;
			tradeAmount = new BigDecimal("-" + tradeAmount.toString());
		}
		//账户
		AccountInfoEntity accountInfoEntity = accountInfoRepository.findByAccountNo(accountNo);
		if(accountInfoEntity == null){
			return new ResultVo(false, "没有查询到账户信息");
		}
		//审核--客户id
		auditInfoEntity.setCustId(accountInfoEntity.getCustId());
		
		//审核拒绝，不记录账户和账户流水信息
		if(Constant.AUDIT_PROJECT_STATUS_REfUSE.equals(auditStatus)){
			return new ResultVo(true);
		}
		
		BigDecimal accountTotalAmount = accountInfoEntity.getAccountTotalAmount().add(tradeAmount);
		BigDecimal accountAvailableAmount = accountInfoEntity.getAccountAvailableAmount().add(tradeAmount);
		accountInfoEntity.setAccountTotalAmount(accountTotalAmount);
		accountInfoEntity.setAccountAvailableAmount(accountAvailableAmount);
		accountInfoEntity.setBasicModelProperty(auditUser, false);
		
		//账户流水
		AccountFlowInfoEntity accountFlowInfoEntity = new AccountFlowInfoEntity();
		if(Constant.OPERATION_TYPE_05.equals(tradeType)){
			accountFlowInfoEntity = accountFlowService.saveAccountFlow(accountInfoEntity, null, null, null, "1", 
					SubjectConstant.TRADE_FLOW_TYPE_RECHARGE, numberService.generateTradeNumber(), SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING, 
					transAccountInfoEntity.getTradeAmount(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_RECHARGE, 
					null, null, SubjectConstant.SUBJECT_TYPE_AMOUNT);
		}else if(Constant.OPERATION_TYPE_06.equals(tradeType)){
			accountFlowInfoEntity = accountFlowService.saveAccountFlow(accountInfoEntity, null, null, null, "1", 
					SubjectConstant.TRADE_FLOW_TYPE_WITHDRAW, numberService.generateTradeNumber(), SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, 
					transAccountInfoEntity.getTradeAmount(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_WITHDRAW, 
					null, null, SubjectConstant.SUBJECT_TYPE_AMOUNT);
		}
		
		accountFlowInfoEntity.setRequestNo(numberService.generateTradeBatchNumber());
		
		return new ResultVo(true);
	}

}
