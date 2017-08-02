package com.slfinance.shanlincaifu.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AccountFlowInfoEntity;
import com.slfinance.shanlincaifu.entity.AccountInfoEntity;
import com.slfinance.shanlincaifu.entity.CustInfoEntity;
import com.slfinance.shanlincaifu.entity.LogInfoEntity;
import com.slfinance.shanlincaifu.entity.ReserveInvestInfoEntity;
import com.slfinance.shanlincaifu.repository.AccountFlowInfoRepository;
import com.slfinance.shanlincaifu.repository.AccountInfoRepository;
import com.slfinance.shanlincaifu.repository.CustInfoRepository;
import com.slfinance.shanlincaifu.repository.InvestInfoRepository;
import com.slfinance.shanlincaifu.repository.LoanInfoRepository;
import com.slfinance.shanlincaifu.repository.LoanReserveRepository;
import com.slfinance.shanlincaifu.repository.LogInfoEntityRepository;
import com.slfinance.shanlincaifu.repository.ParamRepository;
import com.slfinance.shanlincaifu.repository.ReserveInvestInfoRepository;
import com.slfinance.shanlincaifu.repository.custom.LoanReserveRepositoryCustom;
import com.slfinance.shanlincaifu.service.CustAccountService;
import com.slfinance.shanlincaifu.service.DeviceService;
import com.slfinance.shanlincaifu.service.FlowNumberService;
import com.slfinance.shanlincaifu.service.LoanReserveService;
import com.slfinance.shanlincaifu.utils.ArithUtil;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.SubjectConstant;
import com.slfinance.vo.ResultVo;

@Service("loanReserveService")
public class LoanReserveServiceImpl implements LoanReserveService {
	@Autowired
	private ReserveInvestInfoRepository reserveInvestInfoRepository;
	@Autowired
	private LoanReserveRepositoryCustom loanReserveRepositoryCustom;
	@Autowired
	AccountInfoRepository accountInfoRepository;
	@Autowired
	CustInfoRepository custInfoRepository;
	@Autowired
	LoanInfoRepository loanInfoRepository;
	@Autowired
	InvestInfoRepository investInfoRepository;
	@Autowired
	DeviceService deviceService;
	@Autowired
	LogInfoEntityRepository logInfoEntityRepository;
	@Autowired
    ParamRepository paramRepository;
	@Autowired
	FlowNumberService numberService;
	@Autowired
	CustAccountService custAccountService;
	@Autowired
	LoanReserveRepository loanReserveRepository;

	public ResultVo queryJoinReserve(Map<String, Object> params)
			throws SLException {
		Map<String, Object> result = Maps.newHashMap();
		BigDecimal totalReserveAmount = reserveInvestInfoRepository.getTotalReserveAmount();
		BigDecimal totalReservePeople = reserveInvestInfoRepository.getTotalReservePeople();
		BigDecimal minYearRate = new BigDecimal(paramRepository.findByTypeNameAndParameterName(Constant.SLCF_COM_PARAM,Constant.SLCF_COM_PARAM_01).getValue());
		BigDecimal maxYearRate = new BigDecimal(paramRepository.findByTypeNameAndParameterName(Constant.SLCF_COM_PARAM, Constant.SLCF_COM_PARAM_02).getValue());
		BigDecimal investMinAmount = new BigDecimal(paramRepository.findByTypeNameAndParameterName(Constant.SLCF_COM_PARAM, Constant.SLCF_COM_PARAM_03).getValue());
		BigDecimal increaseAmount = new BigDecimal(paramRepository.findByTypeNameAndParameterName(Constant.SLCF_COM_PARAM, Constant.SLCF_COM_PARAM_04).getValue());

		result.put("totalReserveAmount",totalReserveAmount != null ? totalReserveAmount: BigDecimal.ZERO);
		result.put("totalReservePeople",totalReservePeople != null ? totalReservePeople: BigDecimal.ZERO);
		result.put("minLoanTerm", minYearRate);
		result.put("maxLoanTerm", maxYearRate);
		result.put("investMinAmount", investMinAmount);
		result.put("increaseAmount", increaseAmount);
		result.put("seatTerm", "不可转让");
		result.put("repaymentMethod", "到期还本付息");

		return new ResultVo(true, "优先投查询成功", result);
	}

//	public ResultVo queryReserveLoanList(Map<String, Object> params)
//			throws SLException {
//		Page<Map<String, Object>> loanReserveRepositoryList = loanReserveRepositoryCustom.queryMyReserveList(params);
//		Map<String, Object> result = Maps.newHashMap();
//		result.put("data", loanReserveRepositoryList.getContent());
//		result.put("iTotalDisplayRecords",loanReserveRepositoryList.getTotalElements());
//		return new ResultVo(true, "优先投借款信息列表查询成功", result);
//	}

	public ResultVo queryUserReserve(Map<String, Object> params)
			throws SLException {
		Map<String, Object> result = Maps.newHashMap();
		String custId = (String) params.get("custId");
		AccountInfoEntity investorAccount = accountInfoRepository.findByCustId(custId); // 客户账户表
		BigDecimal accountAvailableAmount = investorAccount.getAccountAvailableAmount();
		BigDecimal remainderReserveAmount = reserveInvestInfoRepository.getRemainderReserveAmount(custId);
		BigDecimal investMinAmount = new BigDecimal(paramRepository.findByTypeNameAndParameterName(Constant.SLCF_COM_PARAM, Constant.SLCF_COM_PARAM_03).getValue());
		BigDecimal increaseAmount = new BigDecimal(paramRepository.findByTypeNameAndParameterName(Constant.SLCF_COM_PARAM, Constant.SLCF_COM_PARAM_04).getValue());

		result.put("accountAvailableAmount",accountAvailableAmount != null ? accountAvailableAmount: BigDecimal.ZERO);
		result.put("remainderReserveAmount",remainderReserveAmount != null ? remainderReserveAmount: BigDecimal.ZERO);
		result.put("investMinAmount",investMinAmount);
		result.put("increaseAmount",increaseAmount);
		return new ResultVo(true, "可用余额与待投金额查询成功", result);
	}

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo joinReserve(Map<String, Object> params) throws SLException {
		String custId = (String) params.get("custId");
		String tradePass = (String) params.get("tradePass");
		BigDecimal tradeAmount = new BigDecimal(params.get("tradeAmount").toString()); // 投资金额
		String appSource = (String) params.get("appSource");
		appSource = Strings.nullToEmpty(StringUtils.isEmpty(appSource) ? Constant.INVEST_SOURCE_PC: appSource).toLowerCase();

		// check.0 判断用户是否正常
		CustInfoEntity custInfoEntity = custInfoRepository.findOne(custId);
		if (null == custInfoEntity) {
			return new ResultVo(false, "用户不存在！");
		}
		// check.1 判断用户交易密码
//		tradePass = DigestUtils.md5DigestAsHex(tradePass.getBytes(Charsets.UTF_8));
		if (!tradePass.equals(custInfoEntity.getTradePassword())) {
			return new ResultVo(false, "交易密码不正确！");
		}
		// check.2 验证投资人账户是否足额
		AccountInfoEntity investorAccount = accountInfoRepository.findByCustId(custId); // 客户账户表
		if (tradeAmount.compareTo(investorAccount.getAccountAvailableAmount()) > 0) {
			return new ResultVo(false, "账户可用余额不足， 请充值！");
		}
		BigDecimal investMinAmount = new BigDecimal(paramRepository.findByTypeNameAndParameterName(Constant.SLCF_COM_PARAM, Constant.SLCF_COM_PARAM_03).getValue());
		BigDecimal increaseAmount = new BigDecimal(paramRepository.findByTypeNameAndParameterName(Constant.SLCF_COM_PARAM, Constant.SLCF_COM_PARAM_04).getValue());
		// check.3 验证投资金额是否大于起投金额
		if (tradeAmount.compareTo(investMinAmount) < 0) {
			return new ResultVo(false, "投资金额不能小于起投金额！");
		}
		// check.4 验证投资金额是否是递增倍数
		if (tradeAmount.compareTo(investMinAmount) > 0 && !ArithUtil.isDivInt(increaseAmount,ArithUtil.sub(tradeAmount, investMinAmount))) {
			return new ResultVo(false, "投资金额必须是起投金额加递增金额整数倍！");
		}
		
		ReserveInvestInfoEntity	reserveInvestInfoEntity = new ReserveInvestInfoEntity();
			reserveInvestInfoEntity.setReserveAmount(tradeAmount);// 预约金额
			reserveInvestInfoEntity.setAlreadyInvestAmount(BigDecimal.ZERO);// 已投预约金额
			reserveInvestInfoEntity.setRemainderAmount(tradeAmount);// 剩余预约金额
			reserveInvestInfoEntity.setCustId(custId);
			reserveInvestInfoEntity.setReserveStartDate(new Date());// 预约开始时间
			reserveInvestInfoEntity.setReserveEndDate(DateUtils.getDateAfterByMinute(new Date(), Integer.parseInt(paramRepository.findByTypeNameAndParameterName(Constant.SLCF_COM_PARAM,Constant.SLCF_COM_PARAM_06).getValue())*60));// 预约结束时间
			reserveInvestInfoEntity.setReserveDate(new Date());// 排队时间
			reserveInvestInfoEntity.setReserveStatus("排队中"); // 状态
			reserveInvestInfoEntity.setCanInvestTerm("7天~60天");// 可投期限，XX天-XX天,XX天-XX天
			reserveInvestInfoEntity.setCanInvestProduct(paramRepository.findByTypeNameAndParameterName(Constant.SLCF_COM_PARAM,Constant.SLCF_COM_PARAM_05).getValue());// 可投产品：XX贷，XX贷，多个用逗号隔开
			reserveInvestInfoEntity.setBasicModelProperty(custId, true);
			reserveInvestInfoRepository.save(reserveInvestInfoEntity);
		String reqeustNo = numberService.generateTradeBatchNumber();
		// 预约冻结
		custAccountService.updateAccount(investorAccount, null, null, 
				null, "7", SubjectConstant.TRADE_FLOW_TYPE_RESERVE_INVEST_01, 
				reqeustNo, tradeAmount, SubjectConstant.TRADE_FLOW_TYPE_RESERVE_INVEST_01, 
				Constant.TABLE_BAO_T_RESERVE_INVEST_INFO, reserveInvestInfoEntity.getId(),  
				"优先投预约金额冻结", custId);

		// 3 记录设备信息(优选)
		Map<String, Object> deviceParams = Maps.newConcurrentMap();
		deviceParams.putAll(params);
		deviceParams.put("relateType", Constant.TABLE_BAO_T_RESERVE_INVEST_INFO);
		deviceParams.put("relatePrimary", reserveInvestInfoEntity.getId());
		deviceParams.put("tradeType", Constant.OPERATION_TYPE_87);
		deviceParams.put("userId", custId);
		deviceService.saveUserDevice(deviceParams);

		// 4 记录日志
		LogInfoEntity logInfoEntity = new LogInfoEntity();
		logInfoEntity.setRelateType(Constant.TABLE_BAO_T_RESERVE_INVEST_INFO);
		logInfoEntity.setRelatePrimary(reserveInvestInfoEntity.getId());
		logInfoEntity.setLogType(Constant.OPERATION_TYPE_87);
		logInfoEntity.setOperBeforeContent("");
		logInfoEntity.setOperAfterContent(String.format("预约金额=%s，已投预约金额=%s，剩余预约金额=%s，预约开始时间=%s，预约结束时间=%s，排队时间=%s，状态=%s，可投期限=%s，可投产品=%s",
								reserveInvestInfoEntity.getReserveAmount(),
								reserveInvestInfoEntity.getAlreadyInvestAmount(),
								reserveInvestInfoEntity.getRemainderAmount(),
								reserveInvestInfoEntity.getReserveStartDate(),
								reserveInvestInfoEntity.getReserveEndDate(),
								reserveInvestInfoEntity.getReserveDate(),
								reserveInvestInfoEntity.getRecordStatus(),
								reserveInvestInfoEntity.getCanInvestTerm(),
								reserveInvestInfoEntity.getCanInvestProduct()));
		logInfoEntity.setOperDesc("");
		logInfoEntity.setOperPerson(custId);
		logInfoEntity.setOperIpaddress((String) params.get("ipAddress"));
		logInfoEntity.setMemo(String.format("%s直投预约，预约金额%s",custInfoEntity.getLoginName(), tradeAmount.toString()));
		logInfoEntity.setBasicModelProperty(custId, true);
		logInfoEntityRepository.save(logInfoEntity);

		return new ResultVo(true, "预约操作成功", true);
	}

	public ResultVo queryMyReserveIncome(Map<String, Object> params)
			throws SLException {
		return new ResultVo(true, "预约投资汇总查询成功",loanReserveRepositoryCustom.queryMyReserveIncome(params));
	}

	public ResultVo queryMyReserveList(Map<String, Object> params)
			throws SLException {
		Page<Map<String, Object>> pageVo = loanReserveRepositoryCustom.queryMyReserveList(params);
		BigDecimal minYearRate = new BigDecimal(paramRepository.findByTypeNameAndParameterName(Constant.SLCF_COM_PARAM,Constant.SLCF_COM_PARAM_01).getValue());
		BigDecimal maxYearRate = new BigDecimal(paramRepository.findByTypeNameAndParameterName(Constant.SLCF_COM_PARAM, Constant.SLCF_COM_PARAM_02).getValue());
		Map<String, Object> result = Maps.newHashMap();
		result.put("iTotalDisplayRecords", pageVo.getTotalElements());
		result.put("data", pageVo.getContent());
		result.put("minLoanTerm", minYearRate);
		result.put("maxLoanTerm", maxYearRate);

		return new ResultVo(true, "预约投资列表查询成功",result);
	}
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo cancelReserve(Map<String, Object> params)
			throws SLException {
		String custId = (String) params.get("custId");
		String tradePass = (String) params.get("tradePass");
		String appSource = (String) params.get("appSource");
		appSource = Strings.nullToEmpty(StringUtils.isEmpty(appSource) ? Constant.INVEST_SOURCE_PC: appSource).toLowerCase();
		// check.0 判断用户是否正常
		CustInfoEntity custInfoEntity = custInfoRepository.findOne(custId);
		if (null == custInfoEntity) {
			return new ResultVo(false, "用户不存在！");
		}
		// check.1 判断用户交易密码
		if (!tradePass.equals(custInfoEntity.getTradePassword())) {
			return new ResultVo(false, "交易密码不正确！");
		}
		List<ReserveInvestInfoEntity> reserveInvestInfoList = reserveInvestInfoRepository.findByCustIdAndReserveStatus(custId, "排队中");
		String requestNo = numberService.generateReserveBatchNumber();
		for (ReserveInvestInfoEntity reserveInvestInfo : reserveInvestInfoList) {
			Map<String, Object> cancelParam = Maps.newHashMap();
			cancelParam.put("reserveId",reserveInvestInfo.getId());
			cancelParam.put("appSource",appSource);
			cancelParam.put("requestNo",requestNo);
			innerClass.cancelReserveByReserveId(cancelParam);
		}
		
		return new ResultVo(true, "撤销预约投标成功", true);
	}
//	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo cancelReserveByJob(Map<String, Object> params) {
		List<ReserveInvestInfoEntity> reserveInvestInfoList = (List<ReserveInvestInfoEntity>) loanReserveRepositoryCustom.getReserveCancelByReserveStatusAndReserveEndDate(params);

		for (ReserveInvestInfoEntity reserveInvestList : reserveInvestInfoList) {
			List<ReserveInvestInfoEntity> reserveList=reserveInvestInfoRepository.findByReserveStatusAndCustId(Constant.RESERVE_INVEST_01, reserveInvestList.getCustId());
			String requestNo = numberService.generateReserveBatchNumber();
			for(ReserveInvestInfoEntity reserveInvestInfo : reserveList){
			Map<String, Object> cancelParam = Maps.newHashMap();
			cancelParam.put("reserveId",reserveInvestInfo.getId());
			cancelParam.put("appSource","");
			cancelParam.put("isReserveCancelJob",true);
			cancelParam.put("requestNo",requestNo);
			try {
				innerClass.cancelReserveByReserveId(cancelParam);
			} catch (SLException e) {
				innerClass.saveLog(Constant.TABLE_BAO_T_RESERVE_INVEST_INFO, reserveInvestInfo.getId(), Constant.OPERATION_TYPE_88, Constant.SYSTEM_USER_BACK, "定时预约撤销异常");
			}
			}
		}
		return new ResultVo(true, "定时撤销预约投标成功");
	}
	@Autowired
	InnerClass innerClass;
	@Service
	static class InnerClass{
		
		@Autowired
		ReserveInvestInfoRepository reserveInvestInfoRepository;
		
		@Autowired
		CustInfoRepository custInfoRepository;
		
		@Autowired
		AccountInfoRepository accountInfoRepository;
		
		@Autowired
		FlowNumberService numberService;
		
		@Autowired
		CustAccountService custAccountService;
		
		@Autowired
		LogInfoEntityRepository logInfoEntityRepository;
		
		@Autowired
		DeviceService deviceService;
		@Autowired
		AccountFlowInfoRepository accountFlowInfoRepository;
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public ResultVo cancelReserveByReserveId(Map<String, Object> params)
				throws SLException {
			String reserveId = (String) params.get("reserveId");
			String requestNo = (String) params.get("requestNo");
			String appSource = (String) params.get("appSource");
			appSource = Strings.nullToEmpty(StringUtils.isEmpty(appSource) ? Constant.INVEST_SOURCE_PC: appSource).toLowerCase();

			ReserveInvestInfoEntity reserveInvestInfo = reserveInvestInfoRepository.findOne(reserveId);
			if(reserveInvestInfo==null || !"排队中".equals(reserveInvestInfo.getReserveStatus())){
				return new ResultVo(false, "数据已处理请刷新"); 
			}
			// check.0 判断用户是否正常
			CustInfoEntity custInfoEntity = custInfoRepository.findOne(reserveInvestInfo.getCustId());
			if (null == custInfoEntity) {
				return new ResultVo(false, "用户不存在！");
			}
			String custId = custInfoEntity.getId();
			// check.2 验证投资人账户是否存在
			AccountInfoEntity investorAccount = accountInfoRepository.findByCustId(custId); // 客户账户表
			if (investorAccount == null) {
				return new ResultVo(false, "该客户账户不存在！");
			}
			if(investorAccount.getAccountFreezeAmount().compareTo(reserveInvestInfo.getRemainderAmount()) < 0) {
				return new ResultVo(false, "冻结金额不足");
			}
			BigDecimal cancelAmount = reserveInvestInfo.getRemainderAmount();
			// 2.预约状态改为待投金额已撤销
				String beforeReserveStatus = reserveInvestInfo.getReserveStatus();
			//	isReserveCancelJob为 true 是定时任务预约投标撤销 预约状态修改为 待出借金额超时退回 
			if (params.get("isReserveCancelJob") != null && (Boolean) params.get("isReserveCancelJob")) {
				reserveInvestInfo.setReserveStatus("待出借金额超时退回");
			} else {
				reserveInvestInfo.setReserveStatus("待出借金额已撤销");
			}
			
			reserveInvestInfo.setBatchNo(requestNo);
			reserveInvestInfo.setBasicModelProperty(custId, false);
			reserveInvestInfoRepository.save(reserveInvestInfo);
			
			// 1. 账户表预约撤销
			String reqeustNo = numberService.generateTradeBatchNumber();
			List<AccountFlowInfoEntity> updateAccount=null;
			if (cancelAmount.compareTo(BigDecimal.ZERO) > 0) {
				if (params.get("isReserveCancelJob") != null && (Boolean) params.get("isReserveCancelJob")) {
					updateAccount = custAccountService.updateAccount(
							investorAccount, null, null, null, "8",
							SubjectConstant.TRADE_FLOW_TYPE_RESERVE_INVEST_03,
							reqeustNo, cancelAmount,
							SubjectConstant.TRADE_FLOW_TYPE_RESERVE_INVEST_03,
							Constant.TABLE_BAO_T_RESERVE_INVEST_INFO,
							reserveInvestInfo.getId(), "预约金额部分解冻，超时退回", custId);
				} else {
					updateAccount = custAccountService.updateAccount(
							investorAccount, null, null, null, "8",
							SubjectConstant.TRADE_FLOW_TYPE_RESERVE_INVEST_02,
							reqeustNo, cancelAmount,
							SubjectConstant.TRADE_FLOW_TYPE_RESERVE_INVEST_02,
							Constant.TABLE_BAO_T_RESERVE_INVEST_INFO,
							reserveInvestInfo.getId(), "预约出借撤销", custId);
				}
				AccountFlowInfoEntity account=updateAccount.get(0);
				AccountFlowInfoEntity accountFlowInfo=accountFlowInfoRepository.findByRelatePrimaryAndTradeType(account.getRelatePrimary(), SubjectConstant.TRADE_FLOW_TYPE_RESERVE_INVEST_01);
				account.setOldTradeNo(accountFlowInfo.getTradeNo());
				accountFlowInfoRepository.save(account);
			}
			// 3.记录设备信息
			Map<String, Object> deviceParams = Maps.newHashMap();
			deviceParams.putAll(params);
			deviceParams.put("relateType",Constant.TABLE_BAO_T_RESERVE_INVEST_INFO);
			deviceParams.put("relatePrimary", reserveInvestInfo.getId());
			deviceParams.put("tradeType", Constant.OPERATION_TYPE_87);
			deviceParams.put("userId", custId);
			deviceService.saveUserDevice(deviceParams);

			LogInfoEntity logInfoEntity = new LogInfoEntity();
			logInfoEntity.setRelateType(Constant.TABLE_BAO_T_RESERVE_INVEST_INFO);
			logInfoEntity.setRelatePrimary(reserveInvestInfo.getId());
			logInfoEntity.setLogType(Constant.OPERATION_TYPE_88);
			logInfoEntity.setOperBeforeContent(String.format("状态=%s",beforeReserveStatus));
			logInfoEntity.setOperAfterContent(String.format("状态=%s",reserveInvestInfo.getReserveStatus()));
			logInfoEntity.setOperDesc("");
			logInfoEntity.setOperPerson(custId);
			logInfoEntity.setOperIpaddress((String) params.get("ipAddress"));
			logInfoEntity.setMemo("");
			logInfoEntity.setBasicModelProperty(custId, true);
			logInfoEntityRepository.save(logInfoEntity);
			return new ResultVo(true, "撤销预约成功", true);
		}
		
		public void saveLog(String relateType, String relatePrimary, String logType, String opId, String memo){
			LogInfoEntity logInfoEntity = new LogInfoEntity();
			logInfoEntity.setRelateType(relateType);
			logInfoEntity.setRelatePrimary(relatePrimary);
			logInfoEntity.setLogType(logType);
//			logInfoEntity.setOperBeforeContent(String.format("状态=%s",beforeReserveStatus));
//			logInfoEntity.setOperAfterContent(String.format("状态=%s",reserveInvestInfo.getReserveStatus()));
			logInfoEntity.setOperDesc(memo);
			logInfoEntity.setOperPerson(opId);
			logInfoEntity.setOperIpaddress("");
			logInfoEntity.setMemo(memo);
			logInfoEntity.setBasicModelProperty(opId, true);
			logInfoEntityRepository.save(logInfoEntity);
		}
	}
	@Override
	public ResultVo queryIsJoinReserve(Map<String, Object> params)
			throws SLException {
		Map<String, Object> result = Maps.newHashMap();
		BigDecimal count=reserveInvestInfoRepository.getCountByCustIdAndReserveStatus("排队中", params.get("custId").toString());
		if (count.compareTo(BigDecimal.ZERO) > 0) {
			result.put("flag", true);
		} else {
			result.put("flag", false);
		}
		return new ResultVo(true, "查询是否有排队中的优先投预约数据成功",result);
	}
	@Override
	public ResultVo queryRemainAmount(Map<String, Object> params) {
		// 通过openservice查询的
		Map<String, Object> resultMap = Maps.newHashMap();
		
		if (params.containsKey("openFlag")) {
			// 通用查询预约金额
			String companyName = (String) params.get("companyName");
			BigDecimal amount1 = reserveInvestInfoRepository.getTotalAmount();
			BigDecimal amount2 = loanReserveRepository.getTotalAmount();
			resultMap.put("amount",(amount1==null?BigDecimal.ZERO:amount1).add(amount2==null?BigDecimal.ZERO:amount2));
		}
		
		return new ResultVo(true, "预约金额查询成功", resultMap);
	}
}
