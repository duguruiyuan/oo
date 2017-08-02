/** 
 * @(#)ProjectServiceImpl.java 1.0.0 2016年1月5日 下午12:10:22  
 *  
 * Copyright © 2016 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AccountFlowInfoEntity;
import com.slfinance.shanlincaifu.entity.AccountInfoEntity;
import com.slfinance.shanlincaifu.entity.AttachmentInfoEntity;
import com.slfinance.shanlincaifu.entity.AuditInfoEntity;
import com.slfinance.shanlincaifu.entity.CustInfoEntity;
import com.slfinance.shanlincaifu.entity.DeviceInfoEntity;
import com.slfinance.shanlincaifu.entity.InvestDetailInfoEntity;
import com.slfinance.shanlincaifu.entity.InvestInfoEntity;
import com.slfinance.shanlincaifu.entity.LogInfoEntity;
import com.slfinance.shanlincaifu.entity.ProjectInfoEntity;
import com.slfinance.shanlincaifu.entity.ProjectInvestInfoEntity;
import com.slfinance.shanlincaifu.entity.RepaymentPlanInfoEntity;
import com.slfinance.shanlincaifu.entity.SubAccountInfoEntity;
import com.slfinance.shanlincaifu.repository.AccountInfoRepository;
import com.slfinance.shanlincaifu.repository.AttachmentRepository;
import com.slfinance.shanlincaifu.repository.AuditInfoRepository;
import com.slfinance.shanlincaifu.repository.CustInfoRepository;
import com.slfinance.shanlincaifu.repository.DeviceInfoRepository;
import com.slfinance.shanlincaifu.repository.InvestDetailInfoRepository;
import com.slfinance.shanlincaifu.repository.InvestInfoRepository;
import com.slfinance.shanlincaifu.repository.LogInfoEntityRepository;
import com.slfinance.shanlincaifu.repository.ProjectInfoRepository;
import com.slfinance.shanlincaifu.repository.ProjectInvestInfoRepository;
import com.slfinance.shanlincaifu.repository.RepaymentPlanInfoRepository;
import com.slfinance.shanlincaifu.repository.SubAccountInfoRepository;
import com.slfinance.shanlincaifu.repository.custom.AttachmentRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.AuditInfoRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.InvestInfoRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.ProjectInfoRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.RepaymentPlanInfoRepositoryCustom;
import com.slfinance.shanlincaifu.service.AccountFlowService;
import com.slfinance.shanlincaifu.service.FlowNumberService;
import com.slfinance.shanlincaifu.service.ParamService;
import com.slfinance.shanlincaifu.service.ProjectRepaymentService;
import com.slfinance.shanlincaifu.service.ProjectService;
import com.slfinance.shanlincaifu.service.SMSService;
import com.slfinance.shanlincaifu.utils.ArithUtil;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.SubjectConstant;
import com.slfinance.vo.ResultVo;

import lombok.extern.slf4j.Slf4j;

/**   
 * 项目管理实现类
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2016年1月5日 下午12:10:22 $ 
 */
@Slf4j
@Service("projectService")
public class ProjectServiceImpl implements ProjectService {
	
	@Autowired
	private ProjectInfoRepository projectInfoRepository;
	
	@Autowired
	private ProjectInvestInfoRepository projectInvestInfoRepository;
	
	@Autowired
	private AttachmentRepository attachmentRepository;
	
	@Autowired
	private AttachmentRepositoryCustom attachmentRepositoryCustom;
	
	@Autowired
	private AuditInfoRepository auditInfoRepository;
	
	@Autowired
	private ProjectInfoRepositoryCustom projectInfoRepositoryCustom;
	
	@Autowired
	private InvestInfoRepositoryCustom investInfoRepositoryCustom;
	
	@Autowired
	private InvestInfoRepository investInfoRepository;
	
	@Autowired
	private InvestDetailInfoRepository investDetailInfoRepository;
	
	@Autowired
	private FlowNumberService numberService;
	
	@Autowired
	private CustInfoRepository custInfoRepository;
	
	@Autowired
	private LogInfoEntityRepository logInfoEntityRepository;
	
	@Autowired
	private AccountInfoRepository accountInfoRepository;
	
	@Autowired
	private AuditInfoRepositoryCustom auditInfoRepositoryCustom;
	
	@Autowired
	private ParamService paramService;
	
	@Autowired
	private DeviceInfoRepository deviceInfoRepository;
	
	@Autowired
	private AccountFlowService accountFlowService;
	
	@Autowired
	private SubAccountInfoRepository subAccountInfoRepository;
	
	@Autowired
	private ProjectRepaymentService projectRepaymentService;
	
	@Autowired
	private SMSService smsService;
	
	@Autowired
	private RepaymentPlanInfoRepositoryCustom repaymentPlanInfoRepositoryCustom;

	@Override
	public ResultVo queryAllProjectList(Map<String, Object> params) {
		Map<String, Object> resultMap = Maps.newHashMap();
		//区别大后台
		params.put("order", "front");
		List<String> projectStatusList = new ArrayList<String>();
		projectStatusList.add(Constant.PROJECT_STATUS_06);
		projectStatusList.add(Constant.PROJECT_STATUS_07);
		projectStatusList.add(Constant.PROJECT_STATUS_08);
		params.put("projectStatusList", projectStatusList);
		Page<Map<String, Object>> page = projectInfoRepositoryCustom.queryAllProjectPage(params);
		resultMap.put("iTotalDisplayRecords", page.getTotalElements());
		resultMap.put("data", page.getContent());
		log.info("查询项目列表成功");
		return new ResultVo(true, "查询项目列表成功", resultMap);
	}

	@Override
	public ResultVo queryProjectDetail(Map<String, Object> params) {
		Map<String, Object> resultMap = projectInfoRepositoryCustom.queryProjectDetail(params);
		params.put("showType", Constant.SHOW_TYPE_EXTERNAL);
		List<Map<String, Object>> attachment = attachmentRepositoryCustom.findListByRelatePrimary(params);
		resultMap.put("attachment", attachment);
		return new ResultVo(true, "项目明细查询成功", resultMap);
	}

	@Override
	public ResultVo queryProjectJoinList(Map<String, Object> params) {
		Map<String, Object> resultMap = Maps.newHashMap(); 
		Page<Map<String, Object>> page = investInfoRepositoryCustom.queryProjectJoinPage(params);
		resultMap.put("iTotalDisplayRecords", page.getTotalElements());
		resultMap.put("data", page.getContent());
		return new ResultVo(true, "加入记录列表查询成功", resultMap);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResultVo caclProject(Map<String, Object> params) throws SLException {
		
		Map<String, Object> result = Maps.newHashMap();
		
		String repaymentMethod = (String)params.get("repaymentMethod");
		BigDecimal tradeAmount = new BigDecimal(params.get("tradeAmount").toString());
		BigDecimal yearRate = new BigDecimal(params.get("yearRate").toString());
		Integer typeTerm = new Integer(params.get("typeTerm").toString());
		BigDecimal actualYearRate = new BigDecimal(StringUtils.isEmpty(params.get("actualYearRate")) ? params.get("yearRate").toString() : params.get("actualYearRate").toString());
		
		ProjectInfoEntity projectInfoEntity = new ProjectInfoEntity();
		projectInfoEntity.setRepaymnetMethod(repaymentMethod);
		projectInfoEntity.setYearRate(yearRate);
		projectInfoEntity.setActualYearRate(actualYearRate);
		projectInfoEntity.setTypeTerm(typeTerm);
		projectInfoEntity.setPenaltyRate(new BigDecimal("0.01"));
		projectInfoEntity.setEffectDate(new Date());
		
		ResultVo resultVo = projectRepaymentService.createRepaymentPlan(projectInfoEntity, tradeAmount);
		Map<String, Object> data = (Map<String, Object>)resultVo.getValue("data");
		List<RepaymentPlanInfoEntity> planList = (List<RepaymentPlanInfoEntity>)data.get("planList");
		
		List<Map<String, Object>> list = Lists.newArrayList();
		BigDecimal totalAmount = BigDecimal.ZERO; // 到期本息
		BigDecimal termInterest = BigDecimal.ZERO; // 每期利息
		BigDecimal totalInterest = BigDecimal.ZERO; // 到期利息
		
		for(RepaymentPlanInfoEntity r : planList) {
			
			BigDecimal principalInterest = ArithUtil.add(r.getRepaymentPrincipal(), r.getRepaymentInterest());
			
			Map<String, Object> m = Maps.newHashMap();
			m.put("currentTerm", r.getCurrentTerm()); // 当前期数
			m.put("repaymentTotalAmount", r.getRepaymentTotalAmount()); // 总额
			m.put("principalInterest", principalInterest); // 本息
			m.put("principal", r.getRepaymentPrincipal()); // 本金
			m.put("interest", r.getRepaymentInterest()); // 利息
			m.put("remainderPrincipal", r.getRemainderPrincipal()); // 剩余本金
			m.put("accountManageExpense", r.getAccountManageExpense()); // 总额
			list.add(m);
			
			totalAmount = ArithUtil.add(totalAmount, principalInterest);
			termInterest = r.getRepaymentInterest();
			totalInterest = ArithUtil.add(totalInterest, r.getRepaymentInterest());
		}
		
		result.put("planList", list);
		result.put("totalAmount", totalAmount);
		result.put("termInterest", termInterest);
		result.put("totalInterest", totalInterest);
		
		return new ResultVo(true, "计算", result);
	}

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo joinProject(Map<String, Object> params) throws SLException {
		String custId = (String)params.get("custId");
		BigDecimal tradeAmount = new BigDecimal((String)params.get("tradeAmount"));
		String projectId = (String)params.get("projectId");
		String appSource = (String)params.get("appSource"); // 来源
		appSource = Strings.nullToEmpty(StringUtils.isEmpty(appSource) ? Constant.INVEST_SOURCE_PC : appSource).toLowerCase();
		
		// 1、检查是否是ios设备，若是ios设备且缺少应用版本号则不允许购买
//		ResultVo resultVo = accessService.checkAppVersion(params);
//		if(!ResultVo.isSuccess(resultVo)) {
//			return resultVo;
//		}

		// 2、 验证用户状态
		CustInfoEntity custInfoEntity = custInfoRepository.findOne(custId);
		if(custInfoEntity == null){
			throw new SLException("用户不存在！");
		}
		if(!Constant.ENABLE_STATUS_01.equals(custInfoEntity.getEnableStatus())){
			throw new SLException("账户为非正常状态，可能被冻结，请联系客服！");
		}
				
		// 3、 客户账户校验： 验证金额是否足够
		AccountInfoEntity accountInfoEntity = accountInfoRepository.findByCustId(custId);
		if(accountInfoEntity == null) {
			throw new SLException("账户不存在");
		}
		if(accountInfoEntity.getAccountAvailableAmount().compareTo(tradeAmount) < 0){
			throw new SLException(String.format("账户可用余额%s小于加入金额%s", accountInfoEntity.getAccountAvailableAmount().toString(), tradeAmount.toString()));
		}

		// 4、 加入金额验证 ：起投金额   上限
		ProjectInfoEntity projectInfoEntity = projectInfoRepository.findOne(projectId);
		if(projectInfoEntity == null) {
			throw new SLException("项目不存在");
		}
		if(projectInfoEntity.getInvestMinAmount().compareTo(tradeAmount) > 0 || projectInfoEntity.getInvestMaxAmount().compareTo(tradeAmount) < 0){
			throw new SLException(String.format("加入金额不能大于上限%s，且不能小于下限%s", projectInfoEntity.getInvestMaxAmount().toString(), projectInfoEntity.getInvestMinAmount().toString()));
		}
		
		// 5、 验证用户投资金额是否为整数倍递增
		BigDecimal investIncrase = ArithUtil.sub(tradeAmount, projectInfoEntity.getInvestMinAmount());
		if(investIncrase.intValue() % projectInfoEntity.getIncreaseAmount().intValue() != 0){
			throw new SLException(String.format("递增金额必须为%s的整数倍", projectInfoEntity.getIncreaseAmount().toString()));
		}
		
		// 6、验证定期宝项目状态为:发布中
		if(!projectInfoEntity.getProjectStatus().equals(Constant.PROJECT_STATUS_06)){
			throw new SLException("项目状态非发布中，不能加入！");
		}
		
		// 7、可投金额验证：判断客户此次投资是否超过剩余可投金额
		ProjectInvestInfoEntity projectInvestInfoEntity = projectInvestInfoRepository.findByProjectId(projectId);
		BigDecimal alreadyInvestAmount = ArithUtil.add(projectInvestInfoEntity.getAlreadyInvestAmount(), tradeAmount);//已投资金额
		if(alreadyInvestAmount.compareTo(projectInfoEntity.getProjectTotalAmount()) > 0) {
			throw new SLException(String.format("投资金额超过剩余可投金额%s", ArithUtil.sub(projectInfoEntity.getProjectTotalAmount(), projectInvestInfoEntity.getAlreadyInvestAmount()).toString()));
		}
		
		// 8、修改已投金额、已投比例、已投人数等
		int countInvestTimes = projectInfoRepository.countInvestInfoByCustId(custId, projectId);
		projectInvestInfoEntity.setAlreadyInvestAmount(alreadyInvestAmount);
		projectInvestInfoEntity.setBasicModelProperty(custId, false);
		if(countInvestTimes == 0) {//投资次数为0表示新用户需将参与人数加1
			projectInvestInfoEntity.setAlreadyInvestPeoples(projectInvestInfoEntity.getAlreadyInvestPeoples() + 1);
		}
		// 已投比例计算如下：已投金额/项目总金额
		// 若剩余可投金额小于起投金额则投资比例改为100%
		projectInvestInfoEntity.setAlreadyInvestScale(ArithUtil.div(alreadyInvestAmount, projectInfoEntity.getProjectTotalAmount()));
		BigDecimal currUsableValue = ArithUtil.sub(projectInfoEntity.getProjectTotalAmount(), alreadyInvestAmount);//剩余可投金额
		if(currUsableValue.compareTo(new BigDecimal("0")) == 0 // 可投金额等于0
				|| currUsableValue.compareTo(projectInfoEntity.getInvestMinAmount()) < 0) { // 可投金额小于起投上限
			projectInvestInfoEntity.setAlreadyInvestScale(new BigDecimal("1"));
		}
		
		// 若剩余可投金额为0，则项目状态改为满标复核
		if(currUsableValue.compareTo(new BigDecimal("0")) == 0) { 
			projectInfoEntity.setProjectStatus(Constant.PROJECT_STATUS_07);
			projectInfoEntity.setFlowStatus(Constant.PROJECT_STATUS_07);
		}
		
		// 9、新建投资
		String investDate = DateUtils.formatDate(new Date(), "yyyyMMdd");
		String expireDate = DateUtils.formatDate(DateUtils.getAfterMonthNext(new Date(), projectInfoEntity.getTypeTerm().intValue()), "yyyyMMdd");
		InvestInfoEntity newInvestInfoEntity = null;
		String tradeCode = numberService.generateLoanContractNumber();
		if(countInvestTimes == 0){// 投资次数为0为新投资，直接新增投资记录
			newInvestInfoEntity = saveInvest(null, custId, projectInfoEntity.getId(), tradeAmount, investDate, expireDate, tradeCode, true, appSource);
		}
		else { // 投资次数不为0，需判断之前有没有投资，有则新加详情，否则新建一笔投资
			InvestInfoEntity investInfoEntity = projectInfoRepository.findInvestInfoByCustIdAndInvestDate(custId, projectInfoEntity.getId(), Constant.VALID_STATUS_VALID);
			if(investInfoEntity == null){
				newInvestInfoEntity = saveInvest(null, custId, projectInfoEntity.getId(), tradeAmount, investDate, expireDate, tradeCode, true, appSource);
			} 
			else {
				if(ArithUtil.add(investInfoEntity.getInvestAmount(), tradeAmount).compareTo(projectInfoEntity.getInvestMaxAmount()) > 0) {
					throw new SLException("累积加入金额超过个人投资上限");
				}
				newInvestInfoEntity = saveInvest(investInfoEntity, custId, projectInfoEntity.getId(), tradeAmount, investDate, expireDate, tradeCode, false, appSource);
			}
		}
		
		// 10、更新账户及记录流水（用户主账户——>项目分账户）
		String reqeustNo = numberService.generateTradeBatchNumber();
		SubAccountInfoEntity projectSubAccount = subAccountInfoRepository.findByRelatePrimary(projectInfoEntity.getId()); // 项目分账户
		// 更新用户账户
		accountInfoEntity.setAccountTotalAmount(ArithUtil.sub(accountInfoEntity.getAccountTotalAmount(), tradeAmount));
		accountInfoEntity.setAccountAvailableAmount(ArithUtil.sub(accountInfoEntity.getAccountAvailableAmount(), tradeAmount));
		accountInfoEntity.setBasicModelProperty(custId, false);	
		AccountFlowInfoEntity accountFlowInfoEntity = accountFlowService.saveAccountFlow(accountInfoEntity, null, null, projectSubAccount, "2", 
				SubjectConstant.TRADE_FLOW_TYPE_JOIN_PROJECT, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, 
				tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_JOIN_PROJECT, 
				Constant.TABLE_BAO_T_INVEST_INFO, newInvestInfoEntity.getId(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
		accountFlowInfoEntity.setMemo(String.format("购买%s", projectInfoEntity.getProjectName()));
		
		// 更新公司账户
		projectSubAccount.setAccountTotalValue(ArithUtil.add(projectSubAccount.getAccountTotalValue(), tradeAmount));
		projectSubAccount.setAccountAvailableValue(ArithUtil.add(projectSubAccount.getAccountAvailableValue(), tradeAmount));
		projectSubAccount.setBasicModelProperty(custId, false);	
		AccountFlowInfoEntity projectAccountEntity = accountFlowService.saveAccountFlow(null, projectSubAccount, accountInfoEntity, null, "3", 
				SubjectConstant.TRADE_FLOW_TYPE_JOIN_PROJECT, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING, 
				tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_JOIN_PROJECT, 
				Constant.TABLE_BAO_T_INVEST_INFO, newInvestInfoEntity.getId(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
		projectAccountEntity.setMemo(String.format("购买%s", projectInfoEntity.getProjectName()));
		
		// 11、记录日志
		LogInfoEntity logInfoEntity = new LogInfoEntity();
		logInfoEntity.setRelateType(Constant.TABLE_BAO_T_INVEST_INFO);
		logInfoEntity.setRelatePrimary(newInvestInfoEntity.getId());
		logInfoEntity.setLogType(Constant.OPERATION_TYPE_27);
		logInfoEntity.setOperDesc("");
		logInfoEntity.setOperPerson(custId);
		logInfoEntity.setOperIpaddress((String)params.get("ipAddress"));
		logInfoEntity.setMemo(String.format("%s购买%s，投资金额%s", custInfoEntity.getLoginName(), projectInfoEntity.getProjectName(), tradeAmount.toString()));
		logInfoEntity.setBasicModelProperty(custId, true);
		logInfoEntityRepository.save(logInfoEntity);
		
		// 12、记录设备信息
		DeviceInfoEntity deviceInfoEntity = null;
		if(params.containsKey("channelNo")) {
			deviceInfoEntity = new DeviceInfoEntity();
			deviceInfoEntity.setRelateTableIdentification(Constant.TABLE_BAO_T_INVEST_INFO);
			deviceInfoEntity.setRelatePrimary(newInvestInfoEntity.getId());
			deviceInfoEntity.setCustId(custId);
			deviceInfoEntity.setTradeType(Constant.OPERATION_TYPE_27);
			deviceInfoEntity.setMeId((String)params.get("meId"));
			deviceInfoEntity.setMeVersion((String)params.get("meVersion"));
			deviceInfoEntity.setAppSource(appSource);
			deviceInfoEntity.setChannelNo(paramService.findByChannelNo((String)params.get("channelNo")));
			deviceInfoEntity.setBasicModelProperty(custId, true);
			deviceInfoRepository.save(deviceInfoEntity);
		}
		
		return new ResultVo(true, "加入成功");
	}
	
	/**
	 * 创建或更新一笔投资
	 *
	 * @author  wangjf
	 * @date    2015年4月29日 下午7:16:34
	 * @param oldInvestInfoEntity
	 * @param custId
	 * @param tradeAmount
	 * @param investDate
	 * @param insert
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public InvestInfoEntity saveInvest(InvestInfoEntity oldInvestInfoEntity, String custId, String projectId, 
			BigDecimal tradeAmount, String investDate, String expireDate, 
			String tradeCode, boolean insert, String investSource)
	{
		InvestInfoEntity tmp = null;
		if(insert){
			InvestInfoEntity investInfoEntity = new InvestInfoEntity();
			investInfoEntity.setCustId(custId);
			investInfoEntity.setProjectId(projectId);
			investInfoEntity.setInvestAmount(tradeAmount);
			investInfoEntity.setInvestStatus(Constant.VALID_STATUS_VALID);
			investInfoEntity.setInvestMode(Constant.INVEST_METHOD_JOIN);
			investInfoEntity.setInvestDate(investDate);
			investInfoEntity.setExpireDate(expireDate);
			investInfoEntity.setBasicModelProperty(custId, true);
			investInfoEntity = investInfoRepository.save(investInfoEntity);
			tmp = investInfoEntity;
		}
		else{
			oldInvestInfoEntity.setInvestAmount(ArithUtil.add(oldInvestInfoEntity.getInvestAmount(), tradeAmount));
			oldInvestInfoEntity.setBasicModelProperty(custId, false);
			tmp = oldInvestInfoEntity;
		}
		
		InvestDetailInfoEntity investDetailInfoEntity = new InvestDetailInfoEntity();
		investDetailInfoEntity.setInvestId(tmp.getId());
		investDetailInfoEntity.setTradeNo(tradeCode);
		investDetailInfoEntity.setInvestAmount(tradeAmount);
		investDetailInfoEntity.setInvestSource(investSource);
		investDetailInfoEntity.setBasicModelProperty(custId, true);
		investDetailInfoRepository.save(investDetailInfoEntity);
		
		return tmp;
	}

	@Override
	public ResultVo queryProjectIncome(Map<String, Object> params) {
		return projectInfoRepositoryCustom.findProjectIncomeByCustId(params);
	}

	@Override
	public ResultVo queryMyProjectList(Map<String, Object> params) {
		Map<String, Object> resultMap = Maps.newHashMap();
		Page<Map<String, Object>> page = projectInfoRepositoryCustom
				.queryMyProjectPage(params);
		resultMap.put("iTotalDisplayRecords", page.getTotalElements());
		resultMap.put("data", page.getContent());
		return new ResultVo(true, "查询项目列表成功", resultMap);
	}

	@Override
	public ResultVo queryProjectRepaymentList(Map<String, Object> params) {
		String projectId = (String) params.get("projectId");
		String custId = (String) params.get("custId"); 
		//投资比例，
		double rate = 0.0;
		if(!StringUtils.isEmpty(custId)){
			//个人投资
			BigDecimal totalInvestAmount = investInfoRepository.queryTotalInvestAmountByCustIdAndProjectId(custId, projectId);
			//项目已投资金额
			BigDecimal alreadyInvestAmount = BigDecimal.ZERO;
			ProjectInvestInfoEntity projectInvestInfoEntity = projectInvestInfoRepository.findByProjectId(projectId);
			if(projectInvestInfoEntity != null){
				alreadyInvestAmount = projectInvestInfoEntity.getAlreadyInvestAmount();
				rate = totalInvestAmount.doubleValue() / alreadyInvestAmount.doubleValue();
			}
			
		} else {
			rate = 1.0;
		}
		params.put("rate", rate);
		
		return repaymentPlanInfoRepositoryCustom.findRepaymentPlanInfoList(params);
	}

	@Override
	public ResultVo queryProjectInvestList(Map<String, Object> params) {
		return investInfoRepositoryCustom.queryProjectInvestInfo(params);
	}

	@Override
	public ResultVo queryProjectContract(Map<String, Object> params) {
		return projectInfoRepositoryCustom.queryProjectContract(params);
	}

	@Override
	public ResultVo queryProjectList(Map<String, Object> params) {
		Map<String, Object> resultMap = Maps.newHashMap();
		Page<Map<String, Object>> page = projectInfoRepositoryCustom.queryAllProjectPage(params);
		resultMap.put("iTotalDisplayRecords", page.getTotalElements());
		resultMap.put("data", page.getContent());
		return new ResultVo(true, "项目列表查询成功", resultMap);
	}

	@Override
	public ResultVo queryProjectDetailById(Map<String, Object> params) {
		Map<String, Object> resultMap = projectInfoRepositoryCustom.queryProjectDetail(params);
		List<Map<String, Object>> attachment = attachmentRepositoryCustom.findListByRelatePrimary(params);
		List<Map<String, Object>> auditInfoEntities = auditInfoRepositoryCustom.findByRelatePrimary(params);
		resultMap.put("attachment", attachment);
		resultMap.put("auditInfoEntities", auditInfoEntities);
		return new ResultVo(true, "查询项目明细成功", resultMap);
	}

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo applyProject(Map<String, Object> params) throws SLException {
		ProjectInfoEntity projectInfoEntity = new ProjectInfoEntity();
		List<AttachmentInfoEntity> attachmentInfoEntities = new ArrayList<AttachmentInfoEntity>();
		params.put("projectStatus", Constant.PROJECT_STATUS_01);
		ResultVo resultVo = preparedSet(projectInfoEntity, attachmentInfoEntities, params);
		if(!ResultVo.isSuccess(resultVo)){
			return resultVo;
		}
		String projectId = (String) params.get("projectId");
		if(StringUtils.isEmpty(projectId)){
			projectInfoRepository.save(projectInfoEntity);
			for(int i = 0; i< attachmentInfoEntities.size(); i++){
				attachmentInfoEntities.get(i).setRelatePrimary(projectInfoEntity.getId());
			}
		}
		attachmentRepository.save(attachmentInfoEntities);
		return new ResultVo(true);
	}

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo saveProject(Map<String, Object> params) throws SLException {
		ProjectInfoEntity projectInfoEntity = new ProjectInfoEntity();
		List<AttachmentInfoEntity> attachmentInfoEntities = new ArrayList<AttachmentInfoEntity>();
		params.put("projectStatus", Constant.PROJECT_STATUS_02);
		ResultVo resultVo = preparedSet(projectInfoEntity, attachmentInfoEntities, params);
		if(!ResultVo.isSuccess(resultVo)){
			return resultVo;
		}
		String projectId = (String) params.get("projectId");
		if(StringUtils.isEmpty(projectId)){
			projectInfoRepository.save(projectInfoEntity);
			for(int i = 0; i< attachmentInfoEntities.size(); i++){
				attachmentInfoEntities.get(i).setRelatePrimary(projectInfoEntity.getId());
			}
		}
		attachmentRepository.save(attachmentInfoEntities);
		return new ResultVo(true);
	}

	@Transactional
	@SuppressWarnings("unchecked")
	public ResultVo preparedSet(ProjectInfoEntity projectInfoEntity, List<AttachmentInfoEntity> attachmentInfoEntities, Map<String, Object> params){
		
		String projectId = (String) params.get("projectId");
		String oldProjectNo = "";
		String oldProjectName = "";
		// isEditOperate true 表示 新建项目，false 编辑
		boolean isEditOperate = true;
		if(!StringUtils.isEmpty(projectId)){
			isEditOperate = false;
			projectInfoEntity = projectInfoRepository.findOne(projectId);
			if(projectInfoEntity == null){
				return new ResultVo(false, "没有此项目");
			}
			oldProjectNo = projectInfoEntity.getProjectNo();
			oldProjectName = projectInfoEntity.getProjectName();
		}
		String projectNo = (String) params.get("projectNo");
		ProjectInfoEntity existsProjectInfoEntity = projectInfoRepository.findByProjectNo(projectNo);
		//项目编号不能重复
		if(isEditOperate){//新建
			if(existsProjectInfoEntity != null){
				return new ResultVo(false, "项目编号不能有重复");
			}
		}else {
			if((!oldProjectNo.equals(projectNo)) && existsProjectInfoEntity != null){
				return new ResultVo(false, "项目编号不能有重复");
			}
		}
		
		//项目名称不能重复
		String projectName = (String) params.get("projectName");
		ProjectInfoEntity existsProjectInfoEntity2 = projectInfoRepository.findByProjectName(projectName);
		if(isEditOperate){//新建
			if(existsProjectInfoEntity2 != null){
				return new ResultVo(false, "项目名称不能有重复");
			}
		}else{//编辑，编辑名称与原项目名称不相同时，判断是否与其他已存在项目名相同
			if((!oldProjectName.equals(projectName)) && existsProjectInfoEntity2 != null){
				return new ResultVo(false, "项目名称不能有重复");
			}
		}
		
		String custId = (String) params.get("companyId");
		CustInfoEntity custInfoEntity = custInfoRepository.findOne(custId);
		//判断公司是否存在
		if(custInfoEntity == null){
			return new ResultVo(false, "不存在此公司");
		}
		
		BigDecimal actualYearRate = new BigDecimal((String) params.get("actualYearRate")).
				divide(new BigDecimal("100"));
		BigDecimal yearRate = new BigDecimal((String) params.get("yearRate")).
				divide(new BigDecimal("100"));
		//用户年化利率不能大于原始年化利率
		if(actualYearRate.compareTo(yearRate) == -1){
			return new ResultVo(false, "用户年化利率不能大于原始年化利率");
		}
		
		String repaymnetMethod = (String) params.get("repaymnetMethod");
		Integer typeTerm = Integer.parseInt((String) params.get("typeTerm"));
		if(Constant.REPAYMENT_METHOD_04.equals(repaymnetMethod) && (typeTerm < 3 || typeTerm % 3 != 0)){
			return new ResultVo(false, "还款方式为按季付息时，项目期限不能小于3个月且必须是3的倍数");
		}
		
		Date releaseDate = DateUtils.parseDate((String) params.get("releaseDate"), "yyyy-MM-dd");
		Date currentDate = DateUtils.truncateDate(new Date());
		if(!DateUtils.compare_date(releaseDate, currentDate)){
			return new ResultVo(false, "发布日期大于等于当前日期");
		}
		
		Date effectDate = DateUtils.parseDate((String) params.get("effectDate"), "yyyy-MM-dd");
		if(!DateUtils.compare_date(effectDate, releaseDate)){
			return new ResultVo(false, "生效日期大于等于发布日期");
		}
		
		Date projectEndDate = DateUtils.parseDate((String) params.get("projectEndDate"), "yyyy-MM-dd");
		if(DateUtils.compare_date(effectDate, projectEndDate)){
			return new ResultVo(false, "到期日期必须大于生效日期");
		}
		
		String projectType = (String) params.get("projectType");
		BigDecimal projectTotalAmount = new BigDecimal((String) params.get("projectTotalAmount"));
		BigDecimal investMinAmount = new BigDecimal((String) params.get("investMinAmount"));
		if(projectTotalAmount.remainder(investMinAmount) != BigDecimal.ZERO){
			return new ResultVo(false, "起投金额必须整除项目总金额");
		}
		
		BigDecimal increaseAmount = new BigDecimal((String) params.get("increaseAmount"));
		if(investMinAmount.remainder(increaseAmount)  != BigDecimal.ZERO){
			return new ResultVo(false, "投资递增金额必须整除起投金额");
		}
		
		BigDecimal investMaxAmount = new BigDecimal(params.get("investMaxAmount").toString());
		if(investMaxAmount.remainder(increaseAmount) != BigDecimal.ZERO){
			return new ResultVo(false, "单人投资上限是投资递增金额的整数倍");
		}
		BigDecimal awardRate = new BigDecimal(params.get("awardRate").toString()).
				divide(new BigDecimal("100"));
		String ensureMethod = (String) params.get("ensureMethod");
		String isAtone = (String) params.get("isAtone");
		Integer seatTerm = MapUtils.getInteger(params, "seatTerm", null);
		if(Constant.IS_ATONE_YES.equals(isAtone) && (seatTerm == null || seatTerm < 0)){
			return new ResultVo(false, "当选择可以赎回时，封闭期限不能为空或为负数");
		}
		String projectDescr = (String) params.get("projectDescr");
		String companyDescr = (String) params.get("companyDescr");
		String userId = (String) params.get("userId");
		String projectStatus = (String) params.get("projectStatus");
		
		projectInfoEntity.setProjectStatus(projectStatus);
		projectInfoEntity.setFlowStatus(projectStatus);
		projectInfoEntity.setProjectType(projectType);
		projectInfoEntity.setProjectNo(projectNo);
		projectInfoEntity.setCompanyName(custInfoEntity.getLoginName());
		projectInfoEntity.setCustId(custId);
		projectInfoEntity.setProjectName(projectName);
		projectInfoEntity.setActualYearRate(actualYearRate);
		projectInfoEntity.setYearRate(yearRate);
		projectInfoEntity.setAwardRate(awardRate);
		projectInfoEntity.setProjectTotalAmount(projectTotalAmount);
		projectInfoEntity.setTypeTerm(typeTerm);
		projectInfoEntity.setSeatTerm(seatTerm);
		projectInfoEntity.setInvestMinAmount(investMinAmount);
		projectInfoEntity.setIncreaseAmount(increaseAmount);
		projectInfoEntity.setInvestMaxAmount(investMaxAmount);
		projectInfoEntity.setReleaseDate(releaseDate);
		projectInfoEntity.setEffectDate(effectDate);
		projectInfoEntity.setProjectEndDate(projectEndDate);
		projectInfoEntity.setRepaymnetMethod(repaymnetMethod);
		projectInfoEntity.setEnsureMethod(ensureMethod);
		projectInfoEntity.setIsAtone(isAtone);
		projectInfoEntity.setProjectDescr(projectDescr);
		projectInfoEntity.setCompanyDescr(companyDescr);
		projectInfoEntity.setPenaltyRate(paramService.findPenaltyRate());
		projectInfoEntity.setRiskRate(paramService.findRiskRate());
		projectInfoEntity.setBasicModelProperty(userId, true);
		
		//编辑项目，将原先的附件的recordstatus 置为 无效
		List<Map<String, Object>> attachments = (List<Map<String, Object>>) params.get("attachment");
		if(!isEditOperate){
			List<AttachmentInfoEntity> oldAttachmentInfoEntities = attachmentRepository.findByRelatePrimaryAndRecordStatus(projectInfoEntity.getId(), Constant.VALID_STATUS_VALID);
			for(AttachmentInfoEntity attachmentInfoEntity : oldAttachmentInfoEntities){
				attachmentInfoEntity.setRecordStatus(Constant.VALID_STATUS_INVALID);
			}
		}
		
		if(attachments != null) {
			for(Map<String, Object> attachment : attachments){
				AttachmentInfoEntity attachmentInfoEntity = new AttachmentInfoEntity();
				String attachmentName = (String) attachment.get("attachmentName");
				String storagePath = (String) attachment.get("storagePath");
				attachmentInfoEntity.setAttachmentType(Constant.ATTACHMENT_TYPE_01);
				attachmentInfoEntity.setAttachmentName(attachmentName);
				attachmentInfoEntity.setStoragePath(storagePath);
				attachmentInfoEntity.setRelateType(Constant.TABLE_BAO_T_PROJECT_INFO);
				attachmentInfoEntity.setRelatePrimary(projectInfoEntity.getId());
				attachmentInfoEntity.setDocType(Constant.UNBIND_CARD_ATTACHMENT_DOC_TYPE_PIG);
				attachmentInfoEntity.setBasicModelProperty(userId, true);
				attachmentInfoEntity.setShowType((String) attachment.get("showType"));
				attachmentInfoEntities.add(attachmentInfoEntity);
			}
		}
		return new ResultVo(true);
	}
	
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo deleteProject(Map<String, Object> params)
			throws SLException {
		String projectId = (String) params.get("projectId");
		ProjectInfoEntity projectInfoEntity = projectInfoRepository.findOne(projectId);
		if(!Constant.PROJECT_STATUS_01.equals(projectInfoEntity.getProjectStatus())){
			return new ResultVo(false, "项目不是暂存状态不能删除");
		}
		projectInfoRepository.delete(projectId);
		attachmentRepository.deleteByRelatePrimary(projectId);
		return new ResultVo(true);
	}

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo auditProject(Map<String, Object> params) throws SLException {
		
		String projectId = (String)params.get("projectId");
		ProjectInfoEntity projectInfoEntity = projectInfoRepository.findOne(projectId);
		if(!Constant.PROJECT_STATUS_02.equals(projectInfoEntity.getProjectStatus())){
			return new ResultVo(false, "项目状态不是待审核，不能做审核操作");
		}
		
		String auditStatus = (String) params.get("auditStatus");
		String auditMemo = (String) params.get("auditMemo");
		//审核前状态
		String operBeforeContent = projectInfoEntity.getProjectStatus();
		
		String prjectStatus = "";
		switch(auditStatus){
		case Constant.AUDIT_STATUS_PASS:
			Date releaseDate = DateUtils.truncateDate(projectInfoEntity.getReleaseDate());
			if(!DateUtils.compare_date(releaseDate, DateUtils.truncateDate(new Date()))){
				return new ResultVo(false, "当前日期大于发布日期，不能审核通过");
			}
			prjectStatus = Constant.PROJECT_STATUS_05;
			break;
		case Constant.AUDIT_STATUS_REfUSE:
			prjectStatus = Constant.PROJECT_STATUS_04;
			break;
		case Constant.AUDIT_STATUS_FALLBACK:
			prjectStatus = Constant.PROJECT_STATUS_03;
			break;
		}
		
		String userId = (String) params.get("userId");
		projectInfoEntity.setProjectStatus(prjectStatus);
		projectInfoEntity.setFlowStatus(prjectStatus);
		projectInfoEntity.setBasicModelProperty(userId, false);
		
		AuditInfoEntity auditInfoEntity = new AuditInfoEntity();
		auditInfoEntity.setRelateType(Constant.TABLE_BAO_T_PROJECT_INFO);
		auditInfoEntity.setRelatePrimary(projectId);
		auditInfoEntity.setApplyType(Constant.APPLY_TYPE_08);//申请类型
		auditInfoEntity.setAuditUser(userId);
		auditInfoEntity.setAuditStatus(auditStatus);
		auditInfoEntity.setAuditTime(new Date());
		auditInfoEntity.setApplyTime(new Date());
		auditInfoEntity.setBasicModelProperty(userId, true);
		auditInfoEntity.setMemo(auditMemo);
		auditInfoRepository.save(auditInfoEntity);
		
		LogInfoEntity logInfoEntity = new LogInfoEntity();
		logInfoEntity.setRelateType(Constant.TABLE_BAO_T_AUDIT_INFO);
		logInfoEntity.setRelatePrimary(auditInfoEntity.getId());
		logInfoEntity.setLogType(Constant.LOG_TYPE_AUDIT);
		logInfoEntity.setOperBeforeContent(operBeforeContent);//原项目状态
		logInfoEntity.setOperAfterContent(auditStatus);
		logInfoEntity.setMemo(auditMemo);
		logInfoEntity.setOperPerson(userId);
		logInfoEntity.setBasicModelProperty(userId, true);
		logInfoEntityRepository.save(logInfoEntity);
		
		return new ResultVo(true);
	}

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo publishProject(Map<String, Object> params)
			throws SLException {
		
		String projectId = (String)params.get("projectId");
		String userId = (String)params.get("userId");
		// 1) 取项目
		ProjectInfoEntity projectInfoEntity = projectInfoRepository.findOne(projectId);
		if(projectInfoEntity == null) {
			throw new SLException("项目不存在");
		}
		if(!Constant.PROJECT_STATUS_05.equals(projectInfoEntity.getProjectStatus())) {
			throw new SLException("项目非待发布状态");
		}
		if(DateUtils.truncateDate(new Date()).compareTo(projectInfoEntity.getEffectDate()) > 0) { // 当前时间大于生效时间，不允许发布
			throw new SLException("当前时间大于生效时间，不允许发布");
		}
		
		// 2) 项目状态改为发布中
		projectInfoEntity.setProjectStatus(Constant.PROJECT_STATUS_06);
		projectInfoEntity.setFlowStatus(Constant.PROJECT_STATUS_06);
		projectInfoEntity.setReleaseDate(new Date());
		projectInfoEntity.setBasicModelProperty(userId, false);
		
		// 3) 初始化项目投资情况
		ProjectInvestInfoEntity projectInvestInfoEntity = new ProjectInvestInfoEntity();
		projectInvestInfoEntity.setProjectId(projectId);
		projectInvestInfoEntity.setAlreadyInvestPeoples(0);
		projectInvestInfoEntity.setAlreadyInvestAmount(BigDecimal.ZERO);
		projectInvestInfoEntity.setAlreadyInvestScale(BigDecimal.ZERO);
		projectInvestInfoEntity.setBasicModelProperty(userId, true);
		projectInvestInfoRepository.save(projectInvestInfoEntity);
		
		// 4) 生成项目分账户表
		AccountInfoEntity accountInfoEntity = accountInfoRepository.findByCustId(projectInfoEntity.getCustId());
		if(accountInfoEntity == null) {
			throw new SLException("缺少公司账户");
		}
		SubAccountInfoEntity subAccountInfoEntity = new SubAccountInfoEntity();
		subAccountInfoEntity.setCustId(projectInfoEntity.getCustId());
		subAccountInfoEntity.setAccountId(accountInfoEntity.getId());
		subAccountInfoEntity.setRelateType(Constant.TABLE_BAO_T_PROJECT_INFO);
		subAccountInfoEntity.setRelatePrimary(projectId);
		subAccountInfoEntity.setSubAccountNo(numberService.generateCustomerNumber());
		subAccountInfoEntity.setAccountAmount(new BigDecimal("0"));
		subAccountInfoEntity.setAccountTotalValue(new BigDecimal("0"));
		subAccountInfoEntity.setAccountAvailableValue(new BigDecimal("0"));
		subAccountInfoEntity.setAccountFreezeValue(new BigDecimal("0"));
		subAccountInfoEntity.setBasicModelProperty(userId, true);
		subAccountInfoRepository.save(subAccountInfoEntity);
		
		// 5) 记录日志
		LogInfoEntity logInfoEntity = new LogInfoEntity();
		logInfoEntity.setRelateType(Constant.TABLE_BAO_T_PROJECT_INFO);
		logInfoEntity.setRelatePrimary(projectId);
		logInfoEntity.setLogType(Constant.OPERATION_TYPE_28);
		logInfoEntity.setOperBeforeContent(Constant.PROJECT_STATUS_05);
		logInfoEntity.setOperAfterContent(Constant.PROJECT_STATUS_06);
		logInfoEntity.setOperDesc("");
		logInfoEntity.setOperPerson(userId);
		logInfoEntity.setOperIpaddress((String)params.get("ipAddress"));
		logInfoEntity.setMemo(String.format("发布项目%s:%s", projectInfoEntity.getProjectNo(), projectInfoEntity.getProjectName()));
		logInfoEntity.setBasicModelProperty(userId, true);
		logInfoEntityRepository.save(logInfoEntity);
		
		return new ResultVo(true, "项目发布成功");
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResultVo unReleaseProject(Map<String, Object> params)
			throws SLException {
		ResultVo result = internalProjectService.unReleaseProject(params);
		if(ResultVo.isSuccess(result)) {
			// 发送短信
			List<Map<String, Object>> smsList = (List<Map<String, Object>>)result.getValue("data");
			for(Map<String, Object> sms : smsList) {
				smsService.asnySendSMSAndSystemMessage(sms);
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResultVo releaseProject(Map<String, Object> params)
			throws SLException {
		ResultVo result = internalProjectService.releaseProject(params);
		if(ResultVo.isSuccess(result)) {
			// 发送短信
			List<Map<String, Object>> smsList = (List<Map<String, Object>>)result.getValue("data");
			for(Map<String, Object> sms : smsList) {
				smsService.asnySendSMSAndSystemMessage(sms);
			}
		}
		return result;
	}
	
	@Autowired
	private InternalProjectService internalProjectService;
	
	@Service
	public static class InternalProjectService {
		
		@Autowired
		private ProjectInfoRepository projectInfoRepository;
		
		@Autowired
		private ProjectInvestInfoRepository projectInvestInfoRepository;
				
		@Autowired
		private AuditInfoRepository auditInfoRepository;
		
		@Autowired
		private ProjectInfoRepositoryCustom projectInfoRepositoryCustom;
			
		@Autowired
		private FlowNumberService numberService;
		
		@Autowired
		private LogInfoEntityRepository logInfoEntityRepository;
		
		@Autowired
		private RepaymentPlanInfoRepository repaymentPlanInfoRepository;
		
		@Autowired
		private AccountInfoRepository accountInfoRepository;
		
		@Autowired
		private AccountFlowService accountFlowService;
		
		@Autowired
		private SubAccountInfoRepository subAccountInfoRepository;
		
		@Autowired
		private ProjectRepaymentService projectRepaymentService;

		/**
		 * 流标
		 *
		 * @author  wangjf
		 * @date    2016年1月16日 下午2:20:16
		 * @param params
		 * @return
		 * @throws SLException
		 */
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public ResultVo unReleaseProject(Map<String, Object> params)
				throws SLException {
			
			String projectId = (String)params.get("projectId");
			String userId = (String)params.get("userId");
			List<Map<String, Object>> smsList = Lists.newArrayList();

			// 1) 状态必须为发布中、满标复核
			ProjectInfoEntity projectInfoEntity = projectInfoRepository.findOne(projectId);
			if(projectInfoEntity == null) {
				throw new SLException("项目不存在");
			}
			if(!Constant.PROJECT_STATUS_06.equals(projectInfoEntity.getProjectStatus())
					&& !Constant.PROJECT_STATUS_07.equals(projectInfoEntity.getProjectStatus())
					&& !(Constant.PROJECT_STATUS_05.equals(projectInfoEntity.getProjectStatus()) && new Date().compareTo(projectInfoEntity.getEffectDate()) > 0)) {
				throw new SLException("项目非发布中或者满标复核状态");
			}
			
			// 2) 修改项目状态为流标
			String oldProjectStatus = projectInfoEntity.getProjectStatus();
			projectInfoEntity.setProjectStatus(Constant.PROJECT_STATUS_12);
			projectInfoEntity.setFlowStatus(Constant.PROJECT_STATUS_12);
			projectInfoEntity.setBasicModelProperty(userId, false);
			
			// 若项目状态为满标复核，则为强制流标，需插入一条审核记录
			if(Constant.PROJECT_STATUS_07.equals(oldProjectStatus)) {
				AuditInfoEntity auditInfoEntity = new AuditInfoEntity();
				auditInfoEntity.setCustId(projectInfoEntity.getCustId());
				auditInfoEntity.setRelateType(Constant.TABLE_BAO_T_PROJECT_INFO);
				auditInfoEntity.setRelatePrimary(projectInfoEntity.getId());
				auditInfoEntity.setApplyType(Constant.APPLY_TYPE_07);
				auditInfoEntity.setApplyTime(new Timestamp(System.currentTimeMillis()));
				auditInfoEntity.setTradeStatus(Constant.TRADE_STATUS_04);
				auditInfoEntity.setAuditStatus(Constant.AUDIT_STATUS_REfUSE);
				auditInfoEntity.setAuditTime(new Date());
				auditInfoEntity.setAuditUser(Constant.SYSTEM_USER_BACK);
				auditInfoEntity.setBasicModelProperty(userId, true);
				auditInfoEntity = auditInfoRepository.save(auditInfoEntity);
			}

			// 3) 查询所有投资，状态置为无效
			List<InvestInfoEntity> investList = projectInfoRepository.findInvestByProjectId(projectId);
			for(InvestInfoEntity i : investList) {
				i.setInvestStatus(Constant.VALID_STATUS_INVALID);
				i.setBasicModelProperty(userId, false);
			}
			
			// 4) 循环退还投资金额至用户账户
			String reqeustNo = numberService.generateTradeBatchNumber();
			SubAccountInfoEntity projectSubAccount = subAccountInfoRepository.findByRelatePrimary(projectInfoEntity.getId()); // 项目分账户
			List<AccountInfoEntity> accountList = projectInfoRepository.findAccountByProjectId(projectId);
			for(AccountInfoEntity a : accountList) {
				for(InvestInfoEntity i : investList) {
					if(a.getCustId().equals(i.getCustId())) {
						
						// 更新项目分账
						projectSubAccount.setAccountTotalValue(ArithUtil.sub(projectSubAccount.getAccountTotalValue(), i.getInvestAmount()));
						projectSubAccount.setAccountAvailableValue(ArithUtil.sub(projectSubAccount.getAccountAvailableValue(), i.getInvestAmount()));
						projectSubAccount.setBasicModelProperty(userId, false);
						AccountFlowInfoEntity accountFlowInfoEntity = accountFlowService.saveAccountFlow(null, projectSubAccount, a, null, "3", 
								SubjectConstant.TRADE_FLOW_TYPE_UNRELEASE_PROJECT, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, 
								i.getInvestAmount(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_UNRELEASE_PROJECT, 
								Constant.TABLE_BAO_T_INVEST_INFO, i.getId(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
						accountFlowInfoEntity.setMemo(String.format("%s流标", projectInfoEntity.getProjectName()));
						
						// 更新用户主账
						a.setAccountTotalAmount(ArithUtil.add(a.getAccountTotalAmount(), i.getInvestAmount()));
						a.setAccountAvailableAmount(ArithUtil.add(a.getAccountAvailableAmount(), i.getInvestAmount()));
						a.setBasicModelProperty(userId, false);
						AccountFlowInfoEntity accountFlowInfoEntity2 = accountFlowService.saveAccountFlow(a, null, null, projectSubAccount, "2", 
								SubjectConstant.TRADE_FLOW_TYPE_UNRELEASE_PROJECT, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING, 
								i.getInvestAmount(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_UNRELEASE_PROJECT, 
								Constant.TABLE_BAO_T_INVEST_INFO, i.getId(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
						accountFlowInfoEntity2.setMemo(String.format("%s流标", projectInfoEntity.getProjectName()));
					
						break;
					}
				}
			}
			
			// 5) 判断项目分账户是否退还全部余额
			if(projectSubAccount != null && ArithUtil.compare(BigDecimal.ZERO, projectSubAccount.getAccountTotalValue()) != 0) {
				throw new SLException("流标失败！项目资金未能全部退回到用户账户");
			}
			
			// 6) 记录日志
			LogInfoEntity logInfoEntity = new LogInfoEntity();
			logInfoEntity.setRelateType(Constant.TABLE_BAO_T_PROJECT_INFO);
			logInfoEntity.setRelatePrimary(projectId);
			logInfoEntity.setLogType(Constant.OPERATION_TYPE_29);
			logInfoEntity.setOperBeforeContent(oldProjectStatus);
			logInfoEntity.setOperAfterContent(projectInfoEntity.getProjectStatus());
			logInfoEntity.setOperDesc("");
			logInfoEntity.setOperPerson(userId);
			logInfoEntity.setOperIpaddress((String)params.get("ipAddress"));
			logInfoEntity.setMemo(String.format("流标项目%s:%s", projectInfoEntity.getProjectNo(), projectInfoEntity.getProjectName()));
			logInfoEntity.setBasicModelProperty(userId, true);
			logInfoEntityRepository.save(logInfoEntity);
			
			// 7) 准备发送短信内容
			params.put("start", 0);
			params.put("length", Integer.MAX_VALUE);
			Page<Map<String, Object>> page = projectInfoRepositoryCustom.queryProjectJoinList(params);
			for(Map<String, Object> m : page.getContent()) {
				Map<String, Object> smsParams = new HashMap<String, Object>();
				smsParams.put("mobile", m.get("mobile"));
				smsParams.put("custId", m.get("custId"));	
				smsParams.put("messageType", Constant.SMS_TYPE_PROJECT_UNRELEASE);
				smsParams.put("values", new Object[] { // 短信息内容
						DateUtils.formatDate((Date)m.get("tradeDate"), "yyyy-MM-dd"),
						projectInfoEntity.getProjectNo(),
						ArithUtil.formatScale(new BigDecimal(m.get("tradeAmount").toString()), 2).toPlainString()});
				smsParams.put("systemMessage", new Object[] { // 站内信内容
						DateUtils.formatDate((Date)m.get("tradeDate"), "yyyy-MM-dd"),
						projectInfoEntity.getProjectNo(),
						ArithUtil.formatScale(new BigDecimal(m.get("tradeAmount").toString()), 2).toPlainString()});
				smsList.add(smsParams);
			}

			return new ResultVo(true, "项目流标成功", smsList);
		}

		/**
		 * 生效
		 *
		 * @author  wangjf
		 * @date    2016年1月16日 下午2:20:26
		 * @param params
		 * @return
		 * @throws SLException
		 */
		@SuppressWarnings("unchecked")
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public ResultVo releaseProject(Map<String, Object> params)
				throws SLException {
			
			String projectId = (String)params.get("projectId");
			String userId = (String)params.get("userId");
			List<Map<String, Object>> smsList = Lists.newArrayList();

			// 1) 状态必须为发布中且当前时间大于生效时间，或者状态为满标待复核且当前时间等于生效时间
			ProjectInfoEntity projectInfoEntity = projectInfoRepository.findOne(projectId);
			if(projectInfoEntity == null) {
				throw new SLException("项目不存在");
			}
			if(!(Constant.PROJECT_STATUS_06.equals(projectInfoEntity.getProjectStatus()) 
					&& DateUtils.getStartDate(new Date()).compareTo(DateUtils.truncateDate(projectInfoEntity.getEffectDate())) >= 0)
					&& 
				!(Constant.PROJECT_STATUS_07.equals(projectInfoEntity.getProjectStatus())
					&& DateUtils.getStartDate(new Date()).compareTo(DateUtils.truncateDate(projectInfoEntity.getEffectDate())) == 0)) {
				throw new SLException("项目非发布中状态(且当前时间大于生效时间)或满标复核状态(且当前时间等于生效时间)");
			}
			
			ProjectInvestInfoEntity projectInvestInfoEntity = projectInvestInfoRepository.findByProjectId(projectInfoEntity.getId());
			// 投资金额（考虑到可能未满标强制生效的情况，投资金额需取已投金额）
			BigDecimal projectAmount = projectInvestInfoEntity.getAlreadyInvestAmount();
			if(projectAmount.compareTo(BigDecimal.ZERO) <= 0) { // 放款金额小于0不允许生效
				throw new SLException("项目尚未筹集到资金，无法生效");
			}
			
			// 2) 修改项目状态为还款中
			String oldProjectStatus = projectInfoEntity.getProjectStatus();
			projectInfoEntity.setProjectStatus(Constant.PROJECT_STATUS_08);
			projectInfoEntity.setFlowStatus(Constant.PROJECT_STATUS_08);
			projectInfoEntity.setBasicModelProperty(userId, false);

			// 若项目状态为满标复核，则为自动生效，需插入一条审核记录
			if(Constant.PROJECT_STATUS_07.equals(oldProjectStatus)) {
				AuditInfoEntity auditInfoEntity = new AuditInfoEntity();
				auditInfoEntity.setCustId(projectInfoEntity.getCustId());
				auditInfoEntity.setRelateType(Constant.TABLE_BAO_T_PROJECT_INFO);
				auditInfoEntity.setRelatePrimary(projectInfoEntity.getId());
				auditInfoEntity.setApplyType(Constant.APPLY_TYPE_07);
				auditInfoEntity.setApplyTime(new Timestamp(System.currentTimeMillis()));
				auditInfoEntity.setTradeStatus(Constant.TRADE_STATUS_03);
				auditInfoEntity.setAuditStatus(Constant.AUDIT_STATUS_PASS);
				auditInfoEntity.setAuditTime(new Date());
				auditInfoEntity.setAuditUser(Constant.SYSTEM_USER_BACK);
				auditInfoEntity.setBasicModelProperty(userId, true);
				auditInfoEntity = auditInfoRepository.save(auditInfoEntity);
			}
			
			// 3) 项目分账至主账户
			String reqeustNo = numberService.generateTradeBatchNumber();
			SubAccountInfoEntity projectSubAccount = subAccountInfoRepository.findByRelatePrimary(projectInfoEntity.getId()); // 项目分账户
			AccountInfoEntity accountInfoEntity = accountInfoRepository.findByCustId(projectInfoEntity.getCustId());
			BigDecimal tradeAmount = projectSubAccount.getAccountTotalValue();
			// 更新项目分账
			projectSubAccount.setAccountTotalValue(ArithUtil.sub(projectSubAccount.getAccountTotalValue(), tradeAmount));
			projectSubAccount.setAccountAvailableValue(ArithUtil.sub(projectSubAccount.getAccountAvailableValue(), tradeAmount));
			projectSubAccount.setBasicModelProperty(userId, false);
			AccountFlowInfoEntity accountFlowInfoEntity = accountFlowService.saveAccountFlow(null, projectSubAccount, accountInfoEntity, null, "3", 
					SubjectConstant.TRADE_FLOW_TYPE_RELEASE_PROJECT, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, 
					tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_RELEASE_PROJECT, 
					Constant.TABLE_BAO_T_PROJECT_INFO, projectInfoEntity.getId(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
			accountFlowInfoEntity.setMemo(String.format("%s生效", projectInfoEntity.getProjectName()));
			
			// 更新项目主账
			accountInfoEntity.setAccountTotalAmount(ArithUtil.add(accountInfoEntity.getAccountTotalAmount(), tradeAmount));
			accountInfoEntity.setAccountAvailableAmount(ArithUtil.add(accountInfoEntity.getAccountAvailableAmount(), tradeAmount));
			accountInfoEntity.setBasicModelProperty(userId, false);
			AccountFlowInfoEntity accountFlowInfoEntity2 = accountFlowService.saveAccountFlow(accountInfoEntity, null, null, projectSubAccount, "2", 
					SubjectConstant.TRADE_FLOW_TYPE_RELEASE_PROJECT, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING, 
					tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_RELEASE_PROJECT, 
					Constant.TABLE_BAO_T_INVEST_INFO, projectInfoEntity.getId(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
			accountFlowInfoEntity2.setMemo(String.format("%s生效", projectInfoEntity.getProjectName()));
			
			// 4) 生成还款计划
			ResultVo planResult = projectRepaymentService.createRepaymentPlan(projectInfoEntity, projectAmount);
			if(ResultVo.isSuccess(planResult)) {
				
				Map<String, Object> data = (Map<String, Object>)planResult.getValue("data");
				
				// 创建还款计划
				List<RepaymentPlanInfoEntity> planList = (List<RepaymentPlanInfoEntity>)data.get("planList");
				repaymentPlanInfoRepository.save(planList);
				
				// 更新项目值
				projectInfoEntity.setFirstTermRepayDay((String)data.get("firstRepaymentDay"));
				projectInfoEntity.setLastTermRepayDay((String)data.get("lastRepaymentDay"));
				projectInfoEntity.setRepaymentDay((String)data.get("nextRepaymentDay"));
				projectInfoEntity.setRemainderTerms((Integer)data.get("remainderTerms"));
				projectInfoEntity.setRemainderPrincipal((BigDecimal)data.get("remainderPrincipal"));
			}
			else {
				throw new SLException("创建还款计划失败");
			}
			
			// 5) 记录日志
			LogInfoEntity logInfoEntity = new LogInfoEntity();
			logInfoEntity.setRelateType(Constant.TABLE_BAO_T_PROJECT_INFO);
			logInfoEntity.setRelatePrimary(projectId);
			logInfoEntity.setLogType(Constant.OPERATION_TYPE_30);
			logInfoEntity.setOperBeforeContent(oldProjectStatus);
			logInfoEntity.setOperAfterContent(projectInfoEntity.getProjectStatus());
			logInfoEntity.setOperDesc("");
			logInfoEntity.setOperPerson(userId);
			logInfoEntity.setOperIpaddress((String)params.get("ipAddress"));
			logInfoEntity.setMemo(String.format("生效项目%s:%s", projectInfoEntity.getProjectNo(), projectInfoEntity.getProjectName()));
			logInfoEntity.setBasicModelProperty(userId, true);
			logInfoEntityRepository.save(logInfoEntity);
			
			// 7) 准备发送短信内容
			params.put("start", 0);
			params.put("length", Integer.MAX_VALUE);
			Page<Map<String, Object>> page = projectInfoRepositoryCustom.queryProjectJoinList(params);
			for(Map<String, Object> m : page.getContent()) {
				Map<String, Object> smsParams = new HashMap<String, Object>();
				smsParams.put("mobile", m.get("mobile"));
				smsParams.put("custId", m.get("custId"));	
				smsParams.put("messageType", Constant.SMS_TYPE_PROJECT_RELEASE);
				smsParams.put("values", new Object[] { // 短信息内容
						DateUtils.formatDate((Date)m.get("tradeDate"), "yyyy-MM-dd"),
						projectInfoEntity.getProjectNo(),
						DateUtils.formatDate(projectInfoEntity.getEffectDate(), "yyyy-MM-dd"),
						DateUtils.formatDate(projectInfoEntity.getProjectEndDate(), "yyyy-MM-dd"),
						ArithUtil.formatPercent(projectInfoEntity.getYearRate()),
						ArithUtil.formatScale(new BigDecimal(m.get("tradeAmount").toString()), 2).toPlainString()});
				smsParams.put("systemMessage", new Object[] { // 站内信内容
						DateUtils.formatDate((Date)m.get("tradeDate"), "yyyy-MM-dd"),
						projectInfoEntity.getProjectNo(),
						DateUtils.formatDate(projectInfoEntity.getEffectDate(), "yyyy-MM-dd"),
						DateUtils.formatDate(projectInfoEntity.getProjectEndDate(), "yyyy-MM-dd"),
						ArithUtil.formatPercent(projectInfoEntity.getYearRate()),
						ArithUtil.formatScale(new BigDecimal(m.get("tradeAmount").toString()), 2).toPlainString()});
				smsList.add(smsParams);
			}

			return new ResultVo(true, "项目生效成功", smsList);
		}
	}

	@Override
	public ResultVo judgeProject(Map<String, Object> params) throws SLException {
		Integer typeTerm = (Integer)params.get("typeTerm");
		int counts = projectInfoRepository.countByTypeTermAndProjectStatus(typeTerm, Constant.PROJECT_STATUS_06);
		if(counts != 0) {
			return new ResultVo(false, "存在相同期限的项目正在发布中");
		}
		
		return new ResultVo(true);
	}


	@Override
	public ResultVo isExistsProject(Map<String, Object> params) {
		ResultVo resultVo = new ResultVo(false, "项目编号不能有重复");
		String projectNo = (String) params.get("projectNo");
		ProjectInfoEntity projectInfoEntity = projectInfoRepository.findByProjectNo(projectNo);
		if(projectInfoEntity == null){
			resultVo = new ResultVo(true, "项目编号没有重复");
		}
		return resultVo;
	}

	@Override
	public ResultVo queryMyProjectDetail(Map<String, Object> params) {
		Map<String, Object> resultMap = Maps.newHashMap();
		List<Map<String, Object>> result = projectInfoRepositoryCustom.queryMyProjectDetail(params);
		if(result != null && result.size() > 0){
			resultMap = result.get(0);
		}
		return new ResultVo(true, "查看个人投资企业借款成功", resultMap);
	}

	@Override
	public ResultVo queryProjectTotalIncome(Map<String, Object> params) {
		Map<String, Object> resultMap = Maps.newHashMap();
		//投资中数据
		List<String> createdStatus = Arrays.asList(Constant.PROJECT_STATUS_06, Constant.PROJECT_STATUS_07);
		params.put("projectStatus", createdStatus);
		Map<String, Object> created = projectInfoRepositoryCustom.queryProjectTotalIncome(params);
		//收益中数据
		List<String> doingStatus = Arrays.asList(Constant.PROJECT_STATUS_08, Constant.PROJECT_STATUS_10);
		params.put("projectStatus", doingStatus);
		Map<String, Object> doing = projectInfoRepositoryCustom.queryProjectTotalIncome(params);
		//已结束数据
		List<String> finishedStatus = Arrays.asList(Constant.PROJECT_STATUS_09, Constant.PROJECT_STATUS_11, Constant.PROJECT_STATUS_12);
		params.put("projectStatus", finishedStatus);
		Map<String, Object> finished = projectInfoRepositoryCustom.queryProjectTotalIncome(params);
		
		resultMap.put("created", created);
		resultMap.put("doing", doing);
		resultMap.put("finished", finished);
		
		return new ResultVo(true, "企业借款收益展示查询成功", resultMap);
	}
}
