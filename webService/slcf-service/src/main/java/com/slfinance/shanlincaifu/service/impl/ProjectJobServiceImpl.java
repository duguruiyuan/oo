/** 
 * @(#)ProjectJobServiceImpl.java 1.0.0 2016年1月6日 下午12:33:54  
 *  
 * Copyright © 2016 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AccountFlowInfoEntity;
import com.slfinance.shanlincaifu.entity.AccountInfoEntity;
import com.slfinance.shanlincaifu.entity.ProjectInfoEntity;
import com.slfinance.shanlincaifu.repository.AccountFlowInfoRepository;
import com.slfinance.shanlincaifu.repository.AccountInfoRepository;
import com.slfinance.shanlincaifu.repository.ProjectInfoRepository;
import com.slfinance.shanlincaifu.repository.custom.DailySettlementRepository;
import com.slfinance.shanlincaifu.repository.custom.ProjectInfoRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.ProjectRepaymentRepositoryCustom;
import com.slfinance.shanlincaifu.service.FlowNumberService;
import com.slfinance.shanlincaifu.service.ProjectJobService;
import com.slfinance.shanlincaifu.service.ProjectRepaymentService;
import com.slfinance.shanlincaifu.service.ProjectService;
import com.slfinance.shanlincaifu.utils.ArithUtil;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.SharedUtil;
import com.slfinance.shanlincaifu.utils.SubjectConstant;
import com.slfinance.vo.ResultVo;

/**   
 * 项目定时任务接口实现类
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2016年1月6日 下午12:33:54 $ 
 */
@Slf4j
@Service("projectJobService")
public class ProjectJobServiceImpl implements ProjectJobService {
	
	@Autowired
	private ProjectService projectService;

	@Autowired
	private ProjectInfoRepositoryCustom projectInfoRepositoryCustom;
	
	@Autowired
	private ProjectInfoRepository projectInfoRepository;
	
	@Autowired
	private ProjectRepaymentRepositoryCustom projectRepaymentRepositoryCustom;
	
	@Autowired
	private ProjectRepaymentService projectRepaymentService;
	
	@Autowired
	private AccountInfoRepository accountInfoRepository;
	
	@Autowired
	private FlowNumberService numberService;
	
	@Autowired
	private DailySettlementRepository dailySettlementRepository;
	
	@Autowired
	private AccountFlowInfoRepository accountFlowInfoRepository;

	@Override
	public ResultVo autoPublishProject(Map<String, Object> params)
			throws SLException {
		List<ProjectInfoEntity> waitingPublishProjectList = projectInfoRepository.findWaitingPublishProject(Constant.PROJECT_STATUS_05, new Date());
		log.info("查询到{}条项目等待发布", waitingPublishProjectList.size());
		Map<Integer, Boolean> judgeMap = Maps.newHashMap();
		int success = 0, failure = 0;
		for(ProjectInfoEntity p : waitingPublishProjectList) {
			if(!judgeMap.containsKey(p.getTypeTerm())) {
				Map<String, Object> param = Maps.newHashMap();
				param.put("typeTerm", p.getTypeTerm());
				judgeMap.put(p.getTypeTerm(), ResultVo.isSuccess(projectService.judgeProject(param)));
			}
			
			// 若不存在相同期限的项目正在发布中，则可以进行发布
			if(judgeMap.get(p.getTypeTerm())) {
				Map<String, Object> param = Maps.newHashMap();
				param.put("projectId", p.getId());
				param.put("userId", Constant.SYSTEM_USER_BACK);
				try {
					ResultVo result = projectService.publishProject(param);
					if(ResultVo.isSuccess(result)) {
						success ++;
						judgeMap.put(p.getTypeTerm(), false);
						log.info("发布项目{}:{}成功", p.getProjectNo(), p.getProjectName());
					}
					else {
						failure ++;
						log.info("发布项目{}:{}失败，失败原因{}", p.getProjectNo(), p.getProjectName(), (String)result.getValue("message"));
					}
				}
				catch(Exception e) {
					failure ++;
					log.error("发布项目{}:{}失败，失败原因{}", p.getProjectNo(), p.getProjectName(), e.getMessage());
				}
			}
			else {
				failure ++;
				log.error("发布项目{}:{}失败，失败原因存在发布中的项目", p.getProjectNo(), p.getProjectName());
			}
		}
		log.info("发布项目成功{}条，失败{}条", success, failure);
		return new ResultVo(true);
	}

	@Override
	public ResultVo autoReleaseProject(Map<String, Object> params)
			throws SLException {
		List<ProjectInfoEntity> waitingUnReleaseProjectList = projectInfoRepository.findWaitingReleaseProject(Constant.PROJECT_STATUS_07, new Date());
		log.info("查询到{}条项目等待生效", waitingUnReleaseProjectList.size());
		int success = 0, failure = 0;
		for(ProjectInfoEntity p : waitingUnReleaseProjectList) {
			Map<String, Object> param = Maps.newHashMap();
			param.put("projectId", p.getId());
			param.put("userId", Constant.SYSTEM_USER_BACK);
			try {
				ResultVo result = projectService.releaseProject(param);
				if(ResultVo.isSuccess(result)) {
					success ++;
					log.info("生效项目{}:{}成功", p.getProjectNo(), p.getProjectName());
				}
				else {
					failure ++;
					log.info("生效项目{}:{}失败，失败原因{}", p.getProjectNo(), p.getProjectName(), (String)result.getValue("message"));
				}
			}
			catch(Exception e) {
				failure ++;
				log.error("生效项目{}:{}失败，失败原因{}", p.getProjectNo(), p.getProjectName(), e.getMessage());
			}
		}
		log.info("生效项目成功{}条，失败{}条", success, failure);
		return new ResultVo(true);
	}

	@Override
	public ResultVo autoUnReleaseProject(Map<String, Object> params)
			throws SLException {
		
		List<ProjectInfoEntity> waitingUnReleaseProjectList = projectInfoRepository.findWaitingUnReleaseProject(Constant.PROJECT_STATUS_06, new Date(), Constant.PROJECT_STATUS_05);
		log.info("查询到{}条项目等待流标", waitingUnReleaseProjectList.size());
		int success = 0, failure = 0;
		for(ProjectInfoEntity p : waitingUnReleaseProjectList) {
			Map<String, Object> param = Maps.newHashMap();
			param.put("projectId", p.getId());
			param.put("userId", Constant.SYSTEM_USER_BACK);
			try{
				ResultVo result = projectService.unReleaseProject(param);
				if(ResultVo.isSuccess(result)) {
					success ++;
					log.info("流标项目{}:{}成功", p.getProjectNo(), p.getProjectName());
				}
				else {
					failure ++;
					log.info("流标项目{}:{}失败，失败原因{}", p.getProjectNo(), p.getProjectName(), (String)result.getValue("message"));
				}
			}
			catch(Exception e) {
				failure ++;
				log.error("流标项目{}:{}失败，失败原因{}", p.getProjectNo(), p.getProjectName(), e.getMessage());
			}
		}
		log.info("流标项目成功{}条，失败{}条", success, failure);
		return new ResultVo(true);
	}

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo autoCompensateProject(Map<String, Object> params)
			throws SLException {
		
		// 判断是否有重复结息
		int runTimes = accountFlowInfoRepository.countByCustIdAndTradeTypeAndCreateDate(Constant.CUST_ID_PROJECT_ERAN, SubjectConstant.TRADE_FLOW_TYPE_REPAYMENT_COMPENSATE_PROJECT, DateUtils.truncateDate(new Date()));
		if(runTimes > 0) {
			return new ResultVo(false, "重复贴息");
		}
		
		// 取当天所有项目状态为发布中或者满标复核的投资
		params.put("projectStatusList", Arrays.asList(Constant.PROJECT_STATUS_06, Constant.PROJECT_STATUS_07));
		params.put("investDate", DateUtils.formatDate(DateUtils.getAfterDay(new Date(), -1), "yyyyMMdd"));
		List<Map<String, Object>> list = projectInfoRepositoryCustom.queryWaitingSubsidyList(params);
		
		List<AccountInfoEntity> accountList = new ArrayList<AccountInfoEntity>();
		List<AccountFlowInfoEntity> accountFlowList = new ArrayList<AccountFlowInfoEntity>();
//		List<AccountFlowDetailEntity> accountFlowDetailList = new ArrayList<AccountFlowDetailEntity>();
//		List<FlowBusiRelationEntity> flowBusiList = new ArrayList<FlowBusiRelationEntity>();
		Map<String, Integer> requestNOMap = new HashMap<String, Integer>();
		int countRequestNo = 0;
		
		// 收益账户
		AccountInfoEntity earnMainAccount = accountInfoRepository.findOne(Constant.ACCOUNT_ID_PROJECT_ERAN); 		
		for(Map<String, Object> m : list) {
			//accountId
			// 2) 计算贴息 = 用户投资金额*投资人月利率/当月实际天数
			BigDecimal tradeAmount = new BigDecimal(m.get("tradeAmount").toString());
			
			// 若用户有多条数据时需判断账户是否已经使用过（账户公用一个）
			AccountInfoEntity accountInfoEntity = null;
			for(AccountInfoEntity acc : accountList) {
				if(acc.getId().equals(m.get("accountId").toString())) { // 账户已经存在
					accountInfoEntity = acc;
					break;
				}
			}
			
			if(accountInfoEntity == null) { // 不存在账户时需初始化
				accountInfoEntity = new AccountInfoEntity();
				accountInfoEntity.setId(m.get("accountId").toString());
				accountInfoEntity.setCustId(m.get("custId").toString());
				accountInfoEntity.setAccountTotalAmount(new BigDecimal(m.get("accountTotalAmount").toString()));
				accountInfoEntity.setAccountAvailableAmount(new BigDecimal(m.get("accountAvailableAmount").toString()));
				accountInfoEntity.setAccountFreezeAmount(new BigDecimal(m.get("accountFreezeAmount").toString()));
				accountInfoEntity.setVersion(Integer.valueOf(m.get("accountVersion").toString()));
				accountList.add(accountInfoEntity);
			}
			
			earnMainAccount.setAccountTotalAmount(ArithUtil.sub(earnMainAccount.getAccountTotalAmount(), tradeAmount));
			earnMainAccount.setAccountAvailableAmount(ArithUtil.sub(earnMainAccount.getAccountAvailableAmount(), tradeAmount));
			earnMainAccount.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
			
			accountInfoEntity.setAccountTotalAmount(ArithUtil.add(accountInfoEntity.getAccountTotalAmount(), tradeAmount));
			accountInfoEntity.setAccountAvailableAmount(ArithUtil.add(accountInfoEntity.getAccountAvailableAmount(), tradeAmount));
			accountInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
			
			//同一个客户的收益，记录流水时，REQUEST_NO要是同一个
			if(!requestNOMap.containsKey(m.get("custId").toString())){
				requestNOMap.put(m.get("custId").toString(), countRequestNo ++);
			}
			
			{
				/* 记录公司分账户流水 */
				AccountFlowInfoEntity accountFlowInfo = new AccountFlowInfoEntity();
				accountFlowInfo.setId(SharedUtil.getUniqueString());
				accountFlowInfo.setAccountTotalAmount(earnMainAccount.getAccountTotalAmount());
				accountFlowInfo.setAccountAvailable(earnMainAccount.getAccountAvailableAmount());
				accountFlowInfo.setAccountFreezeAmount(earnMainAccount.getAccountFreezeAmount());
				accountFlowInfo.setAccountType(Constant.ACCOUNT_TYPE_MAIN);
				accountFlowInfo.setAccountId(earnMainAccount.getId());
				accountFlowInfo.setCustId(earnMainAccount.getCustId());
				accountFlowInfo.setTradeType(SubjectConstant.TRADE_FLOW_TYPE_REPAYMENT_COMPENSATE_PROJECT);
				accountFlowInfo.setRequestNo(m.get("custId").toString());// 请求编号
				accountFlowInfo.setTradeNo(""); // 交易编号
				accountFlowInfo.setBankrollFlowDirection(SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT);
				accountFlowInfo.setTradeAmount(tradeAmount);
				accountFlowInfo.setTradeDate(new Date());
				accountFlowInfo.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
				accountFlowInfo.setFlowType(SubjectConstant.SUBJECT_TYPE_AMOUNT);
				accountFlowInfo.setMemo(String.format("项目%s[%s]贴息", m.get("projectName").toString(), m.get("projectNo").toString()));
				accountFlowInfo.setTargetAccount(accountInfoEntity.getId());
				accountFlowInfo.setRelateType(Constant.TABLE_BAO_T_INVEST_INFO);
				accountFlowInfo.setRelatePrimary(m.get("investId").toString());
				accountFlowList.add(accountFlowInfo);
				
//				AccountFlowDetailEntity accountFlowDetailEntity = new AccountFlowDetailEntity();
//				accountFlowDetailEntity.setId(SharedUtil.getUniqueString());
//				accountFlowDetailEntity.setAccountFlowId(accountFlowInfo.getId());
//				accountFlowDetailEntity.setSubjectType(SubjectConstant.TRADE_FLOW_TYPE_REPAYMENT_COMPENSATE_PROJECT);
//				accountFlowDetailEntity.setSubjectDirection(SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT);
//				accountFlowDetailEntity.setTradeAmount(accountFlowInfo.getTradeAmount());
//				accountFlowDetailEntity.setTradeDesc("企业借款");
//				accountFlowDetailEntity.setTargetAccount(accountInfoEntity.getId());
//				accountFlowDetailEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
//				accountFlowDetailList.add(accountFlowDetailEntity);
//				
//				FlowBusiRelationEntity flowBusiRelationEntity = new FlowBusiRelationEntity();
//				flowBusiRelationEntity.setId(SharedUtil.getUniqueString());
//				flowBusiRelationEntity.setAccountFlowId(accountFlowInfo.getId());
//				flowBusiRelationEntity.setRelateType(Constant.TABLE_BAO_T_INVEST_INFO);
//				flowBusiRelationEntity.setRelatePrimary(m.get("investId").toString());
//				flowBusiRelationEntity.setCreateDate(new Date());
//				flowBusiList.add(flowBusiRelationEntity);
			}
			
			{
				/* 记录客户分账户流水 */
				AccountFlowInfoEntity accountFlowInfo = new AccountFlowInfoEntity();
				accountFlowInfo.setId(SharedUtil.getUniqueString());
				accountFlowInfo.setAccountTotalAmount(accountInfoEntity.getAccountTotalAmount());
				accountFlowInfo.setAccountAvailable(accountInfoEntity.getAccountAvailableAmount());
				accountFlowInfo.setAccountFreezeAmount(accountInfoEntity.getAccountFreezeAmount());
				accountFlowInfo.setAccountType(Constant.ACCOUNT_TYPE_MAIN);
				accountFlowInfo.setAccountId(accountInfoEntity.getId());
				accountFlowInfo.setCustId(accountInfoEntity.getCustId());
				accountFlowInfo.setTradeType(SubjectConstant.TRADE_FLOW_TYPE_REPAYMENT_COMPENSATE_PROJECT);
				accountFlowInfo.setRequestNo(m.get("custId").toString());// 请求编号
				accountFlowInfo.setTradeNo(""); // 交易编号
				accountFlowInfo.setBankrollFlowDirection(SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING);
				accountFlowInfo.setTradeAmount(tradeAmount);
				accountFlowInfo.setTradeDate(new Date());
				accountFlowInfo.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
				accountFlowInfo.setFlowType(SubjectConstant.SUBJECT_TYPE_AMOUNT);
				accountFlowInfo.setMemo(String.format("项目%s[%s]贴息", m.get("projectName").toString(), m.get("projectNo").toString()));
				accountFlowInfo.setRelateType(Constant.TABLE_BAO_T_INVEST_INFO);
				accountFlowInfo.setRelatePrimary(m.get("investId").toString());
				accountFlowInfo.setTargetAccount(earnMainAccount.getId());
				accountFlowList.add(accountFlowInfo);
				
//				AccountFlowDetailEntity accountFlowDetailEntity = new AccountFlowDetailEntity();
//				accountFlowDetailEntity.setId(SharedUtil.getUniqueString());
//				accountFlowDetailEntity.setAccountFlowId(accountFlowInfo.getId());
//				accountFlowDetailEntity.setSubjectType(SubjectConstant.TRADE_FLOW_TYPE_REPAYMENT_COMPENSATE_PROJECT);
//				accountFlowDetailEntity.setSubjectDirection(SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING);
//				accountFlowDetailEntity.setTradeAmount(accountFlowInfo.getTradeAmount());
//				accountFlowDetailEntity.setTradeDesc("企业借款");
//				accountFlowDetailEntity.setTargetAccount(earnMainAccount.getId());
//				accountFlowDetailEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
//				accountFlowDetailList.add(accountFlowDetailEntity);		
//				
//				FlowBusiRelationEntity flowBusiRelationEntity = new FlowBusiRelationEntity();
//				flowBusiRelationEntity.setId(SharedUtil.getUniqueString());
//				flowBusiRelationEntity.setAccountFlowId(accountFlowInfo.getId());
//				flowBusiRelationEntity.setRelateType(Constant.TABLE_BAO_T_INVEST_INFO);
//				flowBusiRelationEntity.setRelatePrimary(m.get("investId").toString());
//				flowBusiRelationEntity.setCreateDate(new Date());
//				flowBusiList.add(flowBusiRelationEntity);
			}

		}
		
		List<String> requestNoList = Lists.newArrayList();
		List<String> tradeNoList = Lists.newArrayList();
		int page = 5000;
		for(int i = 0; i < countRequestNo/page; i ++) {
			requestNoList.addAll(numberService.generateTradeBatchNumber(page));
		}
		
		if( countRequestNo%page != 0 ) {
			requestNoList.addAll(numberService.generateTradeBatchNumber(countRequestNo%page));
		}
		
		int tradeNoSize = accountFlowList.size();
		for(int i = 0; i < tradeNoSize/page; i ++) {
			tradeNoList.addAll(numberService.generateTradeNumber(page));
		}
		
		if( tradeNoSize%page != 0 ) {
			tradeNoList.addAll(numberService.generateTradeNumber(tradeNoSize%page));
		}
				
		// 从allRequestNoMap取RequestNo
		for(int i = 0; i < accountFlowList.size(); i ++) {
			accountFlowList.get(i).setTradeNo(tradeNoList.get(i));
			accountFlowList.get(i).setRequestNo(requestNoList.get(requestNOMap.get(accountFlowList.get(i).getRequestNo())));
		}
		
		/*批量插入表*/
		dailySettlementRepository.batchInsertAccountFlow(accountFlowList);		
//		dailySettlementRepository.batchInsertAccountFlowDetail(accountFlowDetailList); 
//		dailySettlementRepository.batchInsertFlowBusiRelation(flowBusiList);
		dailySettlementRepository.batchUpdateAccount(accountList);	
		
		return new ResultVo(true, "贴息成功");
	}

	@Override
	public ResultVo autoRepaymentProject(Map<String, Object> params)
			throws SLException {
		
		params.put("repaymentStatus", Constant.REPAYMENT_STATUS_WAIT);
		params.put("expectRepaymentDate", DateUtils.getCurrentDate("yyyyMMdd"));
		List<Map<String, Object>> list = projectRepaymentRepositoryCustom.queryWaitingRepaymentList(params);
		log.info("查询到{}条还款计划等待还款", list.size());
		int success = 0, failure = 0;
		
		for(Map<String, Object> m : list) {
			String projectStatus = (String)m.get("projectStatus");
			ResultVo result = new ResultVo(false);
			
			try{
				switch(projectStatus) {
				case Constant.PROJECT_STATUS_08: // 还款中
				{
					Map<String, Object> param = Maps.newHashMap();
					param.put("replaymentPlanId", m.get("replaymentPlanId"));
					param.put("userId", Constant.SYSTEM_USER_BACK);
					result = projectRepaymentService.normalRepayment(param);
					break;
				}
				case Constant.PROJECT_STATUS_10: // 已逾期
				{
					Map<String, Object> param = Maps.newHashMap();
					param.put("projectId", m.get("projectId"));
					param.put("userId", Constant.SYSTEM_USER_BACK);
					result = projectRepaymentService.overdueRepayment(param);
					break;
				}
				default:
					log.warn("项目{}状态为{}，不符合还款条件", m.get("projectId").toString(), m.get("projectStatus").toString());
					break;
				}
				
				if(ResultVo.isSuccess(result)) {
					success ++;
					log.info("项目{}:{}还款成功", m.get("projectNo").toString(), m.get("projectName").toString());
				}
				else {
					failure ++;
					log.info("项目{}:{}还款失败，失败原因{}", m.get("projectNo").toString(), m.get("projectName").toString(), (String)result.getValue("message"));
				}
			}
			catch(Exception e) {
				failure ++;
				log.error("项目{}:{}还款失败，失败原因{}", m.get("projectNo").toString(), m.get("projectName").toString(), e.getMessage());
			}	
		}
		
		log.info("还款成功{}条，失败{}条", success, failure);
		return new ResultVo(true, "自动还款成功");
	}

	@Override
	public ResultVo autoRiskRepaymentProject(Map<String, Object> params)
			throws SLException {
		params.put("isRiskamountRepay", Constant.IS_RISKAMOUNT_REPAY_NO);
		params.put("repaymentStatus", Constant.REPAYMENT_STATUS_WAIT);
		params.put("expectRepaymentDate", DateUtils.getCurrentDate("yyyyMMdd"));
		List<Map<String, Object>> list = projectRepaymentRepositoryCustom.queryWaitingRiskRepaymentList(params);
		log.info("查询到{}条还款计划等待垫付", list.size());
		int success = 0, failure = 0;
		
		for(Map<String, Object> m : list) {
			Map<String, Object> param = Maps.newHashMap();
			param.put("replaymentPlanId", m.get("replaymentPlanId"));
			param.put("userId", Constant.SYSTEM_USER_BACK);
			ResultVo result = projectRepaymentService.riskRepayment(param);
			if(ResultVo.isSuccess(result)) {
				success ++;
				log.info("项目{}:{}垫付成功", m.get("projectNo").toString(), m.get("projectName").toString());
			}
			else {
				failure ++;
				log.info("项目{}:{}垫付失败，失败原因{}", m.get("projectNo").toString(), m.get("projectName").toString(), (String)result.getValue("message"));
			}
		}
		
		log.info("垫付成功{}条，失败{}条", success, failure);
		return new ResultVo(true, "风险金垫付成功");
	}

	@Override
	public ResultVo autoRefuseProject(Map<String, Object> params)
			throws SLException {
		List<ProjectInfoEntity> waitAuditRefuseProject = projectInfoRepository.findWaitAuditRefuseProject(Constant.PROJECT_STATUS_02, new Date());
		log.info("查询到{}条项目待审核", waitAuditRefuseProject.size());
		int success = 0, failure = 0;
		
		for(ProjectInfoEntity p : waitAuditRefuseProject){
			Map<String, Object> param = Maps.newHashMap();
			param.put("projectId", p.getId());
			param.put("auditStatus", Constant.AUDIT_STATUS_REfUSE);
			param.put("auditMemo", "过期未操作，系统自动拒绝");
			param.put("userId", Constant.SYSTEM_USER_BACK);
			try{
				ResultVo result = projectService.auditProject(param);
				if(ResultVo.isSuccess(result)){
					success ++;
					log.info("项目{}:{}审核拒绝成功", p.getProjectNo(), p.getProjectName());
				} else {
					failure ++;
					log.info("项目{}:{}审核拒绝失败，失败原因{}", p.getProjectNo(), p.getProjectName(), (String)result.getValue("message"));
				}
			} catch (Exception e) {
				failure ++;
				log.error("项目{}:{}审核拒绝失败，失败原因{}", p.getProjectNo(), p.getProjectName(), e.getMessage());
			}
		}
		log.info("项目审核拒绝成功{}条，失败{}条", success, failure);
		return new ResultVo(true, "项目审核拒绝成功");
	}
}
