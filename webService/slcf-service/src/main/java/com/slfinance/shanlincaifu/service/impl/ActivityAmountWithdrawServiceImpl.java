package com.slfinance.shanlincaifu.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AccountFlowInfoEntity;
import com.slfinance.shanlincaifu.entity.AccountInfoEntity;
import com.slfinance.shanlincaifu.entity.AuditInfoEntity;
import com.slfinance.shanlincaifu.entity.CustInfoEntity;
import com.slfinance.shanlincaifu.entity.SystemMessageInfoEntity;
import com.slfinance.shanlincaifu.entity.TradeFlowInfoEntity;
import com.slfinance.shanlincaifu.repository.AccountFlowInfoRepository;
import com.slfinance.shanlincaifu.repository.AccountInfoRepository;
import com.slfinance.shanlincaifu.repository.AuditInfoRepository;
import com.slfinance.shanlincaifu.repository.CustInfoRepository;
import com.slfinance.shanlincaifu.repository.SystemMessageInfoRepository;
import com.slfinance.shanlincaifu.repository.TradeFlowInfoRepository;
import com.slfinance.shanlincaifu.repository.custom.WithdrawCashRepositoryCustom;
import com.slfinance.shanlincaifu.service.AccountFlowService;
import com.slfinance.shanlincaifu.service.ActivityAmountWithdrawService;
import com.slfinance.shanlincaifu.service.CustAccountService;
import com.slfinance.shanlincaifu.service.FlowNumberService;
import com.slfinance.shanlincaifu.service.SMSService;
import com.slfinance.shanlincaifu.utils.ArithUtil;
import com.slfinance.shanlincaifu.utils.CommonUtils;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.SubjectConstant;
import com.slfinance.vo.ResultVo;

@Service
@Slf4j
public class ActivityAmountWithdrawServiceImpl implements
		ActivityAmountWithdrawService {

	@Autowired
	private CustInfoRepository custInfoRepository;
	@Autowired
	private AccountInfoRepository accountInfoRepository;
	@Autowired
	private AuditInfoRepository auditInfoRepository;
	@Autowired
	private FlowNumberService numberService;
	@Autowired
	private WithdrawCashRepositoryCustom withdrawCashRepositoryCustom;
	@Autowired
	private TradeFlowInfoRepository tradeFlowInfoRepository;
	@Autowired
	private AccountFlowInfoRepository accountFlowInfoRepository;
	@Autowired
	private SystemMessageInfoRepository systemMessageInfoRepository;
	@Autowired
	private SMSService smsService;
	@Autowired
	private AccountFlowService accountFlowService;
	@Autowired
	private CustAccountService custAccountService;

	@Override
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo saveWithdrawApply(Map<String, Object> params)
			throws SLException {
		String custId = CommonUtils.emptyToString(params.get("custId"));
		BigDecimal amount = CommonUtils.emptyToDecimal(params.get("amount"));
		String requestNo = numberService.generateTradeNumber();
		String tradeFlowType = SubjectConstant.TRADE_FLOW_TYPE_ACTIVITY_AMOUNT_FREEZE;
		String accountFlowType = SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_ACTIVITY_AMOUNT_FREEZE;
		Date tradeDate = new Date();
		// 验证用户状态
		CustInfoEntity custInfoEntity = custInfoRepository.findOne(custId);
		if (!custInfoEntity.getEnableStatus().equals(Constant.ENABLE_STATUS_01)) {
			return new ResultVo(false, "账户为非正常状态，可能被冻结，请联系客服!");
		}
		AccountInfoEntity custAccount = accountInfoRepository
				.findByCustId(custId);
		if (null == custAccount) {
			log.info("未找到客户账户信息:客户ID" + custId);
			throw new SLException("未找到客户账户信息");
		}
		if(custAccount.getAccountActivityAmount().compareTo(new BigDecimal("0"))==0){
			return new ResultVo(false, "没有可转入的余额");
		}
		if (amount.compareTo(custAccount.getAccountActivityAmount()) < 0) {
			return new ResultVo(false, "活动金额必须全部提现");
		}
		if(amount.compareTo(custAccount.getAccountActivityAmount()) > 0){
			return new ResultVo(false,"提现金额大于可用金额");
		}
		custAccount.setAccountTotalAmount(ArithUtil.add(custAccount.getAccountTotalAmount(), amount));
		custAccount.setAccountActivityAmount(ArithUtil.sub(custAccount.getAccountActivityAmount(), amount));
		custAccount.setAccountFreezeAmount(ArithUtil.add(custAccount.getAccountFreezeAmount(), amount));
		custAccount.setBasicModelProperty(custId, false);
		accountInfoRepository.save(custAccount);
		
		//记录账户流水信息
		
		AccountFlowInfoEntity accountFlowInfoEntity = new AccountFlowInfoEntity();
		accountFlowInfoEntity.setAccountActivityAmount(custAccount.getAccountActivityAmount());
		accountFlowInfoEntity.setAccountAvailable(custAccount.getAccountAvailableAmount());
		accountFlowInfoEntity.setAccountFreezeAmount(custAccount.getAccountFreezeAmount());
		accountFlowInfoEntity.setAccountInfo(custAccount);
		accountFlowInfoEntity.setAccountId(custAccount.getId());
		accountFlowInfoEntity.setAccountTotalAmount(custAccount.getAccountTotalAmount());
		accountFlowInfoEntity.setTradeType(accountFlowType);
		accountFlowInfoEntity.setTradeNo(requestNo);
		accountFlowInfoEntity.setTradeAmount(amount);
		accountFlowInfoEntity.setTradeDate(tradeDate);
		accountFlowInfoEntity.setRelateType(Constant.TABLE_BAO_T_CUST_INFO);
		accountFlowInfoEntity.setRelatePrimary(custId);
		accountFlowInfoEntity.setFlowType(SubjectConstant.SUBJECT_TYPE_AMOUNT);
		accountFlowInfoEntity.setCustId(custId);
		accountFlowInfoEntity.setBasicModelProperty(custId, true);
		accountFlowInfoEntity.setMemo("活动金额提现冻结");
		accountFlowInfoRepository.save(accountFlowInfoEntity);
		// 5.插入提现审核信息
		AuditInfoEntity auditInfoEntity = new AuditInfoEntity();
		auditInfoEntity.setCustId(custId);
		auditInfoEntity.setRelateType(Constant.TABLE_BAO_T_ACCOUNT_INFO);
		auditInfoEntity.setRelatePrimary(custAccount.getId());
		auditInfoEntity.setApplyType(Constant.OPERATION_TYPE_91);
		auditInfoEntity.setApplyTime(tradeDate);
		auditInfoEntity.setTradeAmount(amount);
		auditInfoEntity.setTradeStatus(Constant.TRADE_STATUS_01);
		auditInfoEntity.setAuditStatus(Constant.AUDIT_STATUS_REVIEWD);
		auditInfoEntity.setBasicModelProperty(custId, true);
		auditInfoEntity.setMemo("");
		auditInfoEntity = auditInfoRepository.save(auditInfoEntity);

		// 6.插入提现流水过程记录
		TradeFlowInfoEntity flow = new TradeFlowInfoEntity();
		flow.setCustId(custId);
		flow.setCustAccountId(custAccount.getId());
		flow.setRelateType(Constant.TABLE_BAO_T_AUDIT_INFO);
		flow.setRelatePrimary(auditInfoEntity.getId());
		flow.setTradeAmount(amount);
		flow.setTradeExpenses(new BigDecimal("0"));
		flow.setTradeType(tradeFlowType);
		flow.setTradeNo(requestNo);
		flow.setTradeStatus(Constant.TRADE_STATUS_01);
		flow.setTradeDate(tradeDate);
		flow.setTradeDesc("活动金额提现冻结");
		flow.setBasicModelProperty(custId, true);
		flow.setMemo("");
		flow = tradeFlowInfoRepository.save(flow);

		return new ResultVo(true, "提现申请成功");
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo saveWithdrawAudit(Map<String, Object> params)
			throws SLException {
		
		String auditId = CommonUtils.emptyToString(params.get("id"));
		String auditStatus = CommonUtils.emptyToString(params.get("auditStatus"));
		String custId = CommonUtils.emptyToString(params.get("custId"));
		String message="";
		String memo = CommonUtils.emptyToString(params.get("memo"));

		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("id", auditId);
		conditionMap.put("auditStatus", Constant.AUDIT_STATUS_REVIEWD);
		int rows = withdrawCashRepositoryCustom	
				.updateWithDrawCashRecord(conditionMap);
		if (rows == 0) {
			throw new SLException("重复操作,请返回列表查看!!!");
		}
		AuditInfoEntity auditInfoEntity = auditInfoRepository
				.findByIdAndAuditStatus(auditId, Constant.AUDIT_STATUS_REVIEWD);
		if (null == auditInfoEntity)
			return new ResultVo(false, "非法的提现审核操作,没有找到该提现的申请信息!");
		// 检查审核状态
		if (Constant.AUDIT_STATUS_REfUSE.equals(auditInfoEntity.getAuditStatus())|| Constant.AUDIT_STATUS_PASS.equals(auditInfoEntity.getAuditStatus()))
			return new ResultVo(false, "提现已经处理完成,请勿 重复提交!");
		TradeFlowInfoEntity flowInfo = tradeFlowInfoRepository.findFirstByRelatePrimaryOrderByCreateDateDesc(auditId);
		if (null == flowInfo)
			return new ResultVo(false, "非法的提现审核操作,没有找到该比提现的流水过程信息!");
		AccountInfoEntity accountInfo = accountInfoRepository.findFirstByCustIdOrderByCreateDateDesc(auditInfoEntity.getCustId());
		//检查交易状态
		if(Constant.TRADE_STATUS_03.equals(flowInfo.getTradeStatus()) || Constant.TRADE_STATUS_04.equals(auditInfoEntity.getTradeStatus()))
			return new ResultVo(false, "提现已经处理完成,请勿重复提交!");
		if (null == accountInfo)
			
			return new ResultVo(false, "非法的提现审核操作,没有账户信息!");
		
		AccountFlowInfoEntity accountflow = accountFlowInfoRepository.findFirstByTradeNoOrderByCreateDateDesc(flowInfo.getTradeNo());
		if( null == accountflow )
			throw new SLException("非法的提现审核操作,没有冻结流水信息!");
		//用户账户信息
		CustInfoEntity cust = custInfoRepository.findOne(auditInfoEntity.getCustId());
		if(null == cust)
			throw new SLException("用户信息不存在");
		
		BigDecimal amount = auditInfoEntity.getTradeAmount();//交易金额

		
		switch (auditStatus) {
		case Constant.AUDIT_STATUS_PASS:
			AccountInfoEntity companyAccount = accountInfoRepository.findByCustId(Constant.CUST_ID_RED_ENVELOP);
			//更新账户、账户流水   公司主账户→客户主账户
			custAccountService
					.updateAccount(
							companyAccount,
							null,
							accountInfo,
							null,
							"1",
							SubjectConstant.TRADE_FLOW_TYPE_ACTIVITY_AMOUNT_WITHDRAW,
							flowInfo.getTradeNo(),
							amount,
							SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_ACTIVITY_AMOUNT_WITHDRAW,
							Constant.TABLE_BAO_T_CUST_INFO, auditInfoEntity.getCustId(), memo, custId);
			accountInfo.setBasicModelProperty(custId, false);
			accountInfo.setAccountFreezeAmount(ArithUtil.sub(accountInfo.getAccountFreezeAmount(), amount));
			accountInfo.setAccountTotalAmount(ArithUtil.sub(accountInfo.getAccountTotalAmount(), amount));
			//更新交易状态
			flowInfo.setTradeStatus(Constant.TRADE_STATUS_03);
			flowInfo.setMemo(memo);
			flowInfo.setBasicModelProperty(custId, false);
			//更新审核状态
			auditInfoEntity.setAuditStatus(auditStatus);
			auditInfoEntity.setMemo(memo);
			message = "通过操作成功";
			break;
		case Constant.AUDIT_STATUS_REfUSE:
			//更新账户金额
			accountInfo.setAccountTotalAmount(ArithUtil.sub(accountInfo.getAccountTotalAmount(), amount));
			accountInfo.setAccountActivityAmount(ArithUtil.add(accountInfo.getAccountActivityAmount(), amount));
			accountInfo.setAccountFreezeAmount(ArithUtil.sub(accountInfo.getAccountFreezeAmount(), amount));
			accountInfo.setBasicModelProperty(custId, false);
			//记录交易流水
			AccountFlowInfoEntity accountFlowInfoEntity = new AccountFlowInfoEntity();
			accountFlowInfoEntity.setAccountActivityAmount(accountInfo.getAccountActivityAmount());
			accountFlowInfoEntity.setAccountAvailable(accountInfo.getAccountAvailableAmount());
			accountFlowInfoEntity.setAccountFreezeAmount(accountInfo.getAccountFreezeAmount());
			accountFlowInfoEntity.setAccountInfo(accountInfo);
			accountFlowInfoEntity.setAccountId(accountInfo.getId());
			accountFlowInfoEntity.setTradeDate(new Date());
			accountFlowInfoEntity.setAccountTotalAmount(accountInfo.getAccountTotalAmount());
			accountFlowInfoEntity.setTradeType(SubjectConstant.TRADE_FLOW_TYPE_ACTIVITY_AMOUNT_UNFREEZE);
			accountFlowInfoEntity.setTradeNo(flowInfo.getTradeNo());
			accountFlowInfoEntity.setTradeAmount(amount);
			accountFlowInfoEntity.setRelateType(Constant.TABLE_BAO_T_CUST_INFO);
			accountFlowInfoEntity.setRelatePrimary(custId);
			accountFlowInfoEntity.setFlowType(SubjectConstant.SUBJECT_TYPE_AMOUNT);
			accountFlowInfoEntity.setCustId(custId);
			accountFlowInfoEntity.setBasicModelProperty(custId, true);
			accountFlowInfoEntity.setMemo("活动金额提现解冻");
			accountFlowInfoRepository.save(accountFlowInfoEntity);
			
			//更新交易状态
			flowInfo.setTradeStatus(Constant.TRADE_STATUS_04);
			flowInfo.setMemo(memo);
			flowInfo.setBasicModelProperty(custId, false);
			//更新审核状态
			auditInfoEntity.setAuditStatus(auditStatus);
			auditInfoEntity.setMemo(memo);
			message = "拒绝操作成功";
			break;
		default:
			break;
		}
		// 发站内信
		CustInfoEntity systemEntity = custInfoRepository.findOne(Constant.CUST_ADMIN_ID);
		SystemMessageInfoEntity systemMessageInfoEntity = new SystemMessageInfoEntity();
		systemMessageInfoEntity.setSendCust(systemEntity);
		systemMessageInfoEntity.setReceiveCust(cust);
		if (Constant.AUDIT_STATUS_PASS.equals(auditStatus)) {
			systemMessageInfoEntity.setSendTitle("恭喜您，提现成功！");
			systemMessageInfoEntity.setSendContent(String.format("活动资金%s元，已提现到您的余额，马上去投资吧",amount));
		}else {
			systemMessageInfoEntity.setSendTitle("很遗憾 ，提现失败！");
			systemMessageInfoEntity.setSendContent("活动金额提现失败，请重新提现。");
		}
		systemMessageInfoEntity.setSendDate(new Date());
		systemMessageInfoEntity.setIsRead(Constant.SITE_MESSAGE_NOREAD);
		systemMessageInfoEntity.setBasicModelProperty(custId, true);
		systemMessageInfoRepository.save(systemMessageInfoEntity);
		
		//发送短信
		Map<String, Object> smsParams = new HashMap<String, Object>();
		smsParams.put("mobile", cust.getMobile());
		smsParams.put("custId", cust.getId());
		if (Constant.AUDIT_STATUS_PASS.equals(auditStatus)) {
			smsParams.put("messageType", Constant.SMS_TYPE_ACTIVITY_AMOUNT_WITHDRAW_SUCCESS);
		}else{
			smsParams.put("messageType", Constant.SMS_TYPE_ACTIVITY_AMOUNT_WITHDRAW_FAIL);
		}
		smsParams.put("values",new Object[]{
				auditInfoEntity.getApplyTime(),
				amount
		});
		smsService.asnySendSMS(smsParams);
		
		return new ResultVo(true,message);
	}
	
	@Override
	public Map<String, Object> findAllWithdrawAuditList(
			Map<String, Object> params) {
		Map<String, Object> result = new HashMap<String, Object>();
		Page<Map<String, Object>>page = withdrawCashRepositoryCustom.findAllWithdrawAuditList(params);
		result.put("iTotalDisplayRecords", page.getTotalElements());
		result.put("data", page.getContent());
		return result;

	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo saveBatchWithdrawAudit(Map<String, Object> paramsMap)
			throws SLException {
		List<String> auditList = (List<String>) paramsMap.get("ids");
		String auditStatus = CommonUtils.emptyToString(paramsMap.get("auditStatus"));
		String memo = CommonUtils.emptyToString(paramsMap.get("memo"));
		String custId = CommonUtils.emptyToString(paramsMap.get("custId"));

		if (auditList == null || auditList.size() == 0) {
			return new ResultVo(false, "审核ID不能为空");
		}

		int success = 0, failed = 0;
		for (String id : auditList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", id);
			map.put("auditStatus", auditStatus);
			map.put("memo", memo);
			map.put("custId", custId);

			ResultVo resultVo = new ResultVo(false);
			try {
				resultVo = this.saveWithdrawAudit(map);
			} catch (SLException ex) {
				log.error(String.format("提现申请审核失败！审核ID：%s，异常信息：%s", id,
						ex.getMessage()));
			}
			if (ResultVo.isSuccess(resultVo)) {
				success++;
			} else {
				failed++;
			}
		}

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("success", success);
		result.put("failed", failed);

		return new ResultVo(true, "审核成功", result);
	}

}
