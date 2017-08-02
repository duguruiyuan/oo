package com.slfinance.shanlincaifu.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AccountFlowInfoEntity;
import com.slfinance.shanlincaifu.entity.AccountInfoEntity;
import com.slfinance.shanlincaifu.entity.AccreditRequestEntity;
import com.slfinance.shanlincaifu.entity.AttachmentInfoEntity;
import com.slfinance.shanlincaifu.entity.AuditInfoEntity;
import com.slfinance.shanlincaifu.entity.BankCardInfoEntity;
import com.slfinance.shanlincaifu.entity.CustActivityInfoEntity;
import com.slfinance.shanlincaifu.entity.CustInfoEntity;
import com.slfinance.shanlincaifu.entity.CustRecommendInfoEntity;
import com.slfinance.shanlincaifu.entity.InterfaceDetailInfoEntity;
import com.slfinance.shanlincaifu.entity.InvestDetailInfoEntity;
import com.slfinance.shanlincaifu.entity.InvestInfoEntity;
import com.slfinance.shanlincaifu.entity.JobRunListenerEntity;
import com.slfinance.shanlincaifu.entity.LoanAgreeEntity;
import com.slfinance.shanlincaifu.entity.LoanCustInfoEntity;
import com.slfinance.shanlincaifu.entity.LoanDetailInfoEntity;
import com.slfinance.shanlincaifu.entity.LoanInfoEntity;
import com.slfinance.shanlincaifu.entity.LoanRebateInfoEntity;
import com.slfinance.shanlincaifu.entity.LoanReserveEntity;
import com.slfinance.shanlincaifu.entity.LoanReserveRelationEntity;
import com.slfinance.shanlincaifu.entity.LoanServicePlanInfoEntity;
import com.slfinance.shanlincaifu.entity.LoanTransferApplyEntity;
import com.slfinance.shanlincaifu.entity.LoanTransferEntity;
import com.slfinance.shanlincaifu.entity.LogInfoEntity;
import com.slfinance.shanlincaifu.entity.PurchaseAwardInfoEntity;
import com.slfinance.shanlincaifu.entity.RepaymentPlanCopeEntity;
import com.slfinance.shanlincaifu.entity.RepaymentPlanInfoEntity;
import com.slfinance.shanlincaifu.entity.SubAccountInfoEntity;
import com.slfinance.shanlincaifu.entity.ThridPartyPayRequestEntity;
import com.slfinance.shanlincaifu.entity.TradeLogInfoEntity;
import com.slfinance.shanlincaifu.entity.WealthHoldHistoryInfoEntity;
import com.slfinance.shanlincaifu.entity.WealthHoldInfoEntity;
import com.slfinance.shanlincaifu.entity.WithholdAccountEntity;
import com.slfinance.shanlincaifu.repository.AccountInfoRepository;
import com.slfinance.shanlincaifu.repository.AttachmentRepository;
import com.slfinance.shanlincaifu.repository.AuditInfoRespository;
import com.slfinance.shanlincaifu.repository.BankCardInfoRepository;
import com.slfinance.shanlincaifu.repository.CreditRightValueRepository;
import com.slfinance.shanlincaifu.repository.CustActivityInfoRepository;
import com.slfinance.shanlincaifu.repository.CustInfoRepository;
import com.slfinance.shanlincaifu.repository.CustRecommendInfoRepository;
import com.slfinance.shanlincaifu.repository.DeviceInfoRepository;
import com.slfinance.shanlincaifu.repository.InterfaceDetailInfoRepository;
import com.slfinance.shanlincaifu.repository.InvestDetailInfoRepository;
import com.slfinance.shanlincaifu.repository.InvestInfoRepository;
import com.slfinance.shanlincaifu.repository.JobRunListenerRepository;
import com.slfinance.shanlincaifu.repository.LoanCustInfoRepository;
import com.slfinance.shanlincaifu.repository.LoanDetailInfoRepository;
import com.slfinance.shanlincaifu.repository.LoanInfoRepository;
import com.slfinance.shanlincaifu.repository.LoanRebateInfoRepository;
import com.slfinance.shanlincaifu.repository.LoanReserveRelationRepository;
import com.slfinance.shanlincaifu.repository.LoanReserveRepository;
import com.slfinance.shanlincaifu.repository.LoanServicePlanInfoRepository;
import com.slfinance.shanlincaifu.repository.LoanTransferApplyRepository;
import com.slfinance.shanlincaifu.repository.LoanTransferRepository;
import com.slfinance.shanlincaifu.repository.LogInfoEntityRepository;
import com.slfinance.shanlincaifu.repository.RepaymentPlanCopeRepository;
import com.slfinance.shanlincaifu.repository.RepaymentPlanInfoRepository;
import com.slfinance.shanlincaifu.repository.SubAccountInfoRepository;
import com.slfinance.shanlincaifu.repository.ThridPartyPayRequestRepository;
import com.slfinance.shanlincaifu.repository.TradeLogInfoRepository;
import com.slfinance.shanlincaifu.repository.WealthHoldHistoryInfoRepository;
import com.slfinance.shanlincaifu.repository.WealthHoldInfoRepository;
import com.slfinance.shanlincaifu.repository.WithholdAccountRepository;
import com.slfinance.shanlincaifu.repository.custom.InvestInfoRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.LoanInfoRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.ProjectInfoRepositoryCustom;
import com.slfinance.shanlincaifu.service.AccountFlowService;
import com.slfinance.shanlincaifu.service.AccountService;
import com.slfinance.shanlincaifu.service.AccreditRequestService;
import com.slfinance.shanlincaifu.service.ActivityAwardService;
import com.slfinance.shanlincaifu.service.BankCardService;
import com.slfinance.shanlincaifu.service.CustAccountService;
import com.slfinance.shanlincaifu.service.CustActivityService;
import com.slfinance.shanlincaifu.service.DeviceService;
import com.slfinance.shanlincaifu.service.ExpandInfoService;
import com.slfinance.shanlincaifu.service.ExportFileService;
import com.slfinance.shanlincaifu.service.FlowNumberService;
import com.slfinance.shanlincaifu.service.LoanAgreeService;
import com.slfinance.shanlincaifu.service.LoanInfoService;
import com.slfinance.shanlincaifu.service.LoanManagerService;
import com.slfinance.shanlincaifu.service.LoanProjectService;
import com.slfinance.shanlincaifu.service.LoanRepaymentPlanService;
import com.slfinance.shanlincaifu.service.OpenThirdService;
import com.slfinance.shanlincaifu.service.ParamService;
import com.slfinance.shanlincaifu.service.PurchaseAwardInfoService;
import com.slfinance.shanlincaifu.service.RepaymentPlanCopeService;
import com.slfinance.shanlincaifu.service.SMSService;
import com.slfinance.shanlincaifu.service.ThirdPartyPayRequestService;
import com.slfinance.shanlincaifu.utils.ArithUtil;
import com.slfinance.shanlincaifu.utils.CommonUtils;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.HttpRequestUtil;
import com.slfinance.shanlincaifu.utils.ImgSrcReplaceUtil;
import com.slfinance.shanlincaifu.utils.Json;
import com.slfinance.shanlincaifu.utils.JsonUtil;
import com.slfinance.shanlincaifu.utils.OpenSerivceCode;
import com.slfinance.shanlincaifu.utils.SharedUtil;
import com.slfinance.shanlincaifu.utils.SubjectConstant;
import com.slfinance.shanlincaifu.utils.WithHoldingConstant;
import com.slfinance.thirdpp.util.ShareUtil;
import com.slfinance.vo.ResultVo;

@Slf4j
@Service("loanProjectService")
@SuppressWarnings({ "all" })
public class LoanProjectServiceImpl implements LoanProjectService {
	
	@Autowired
	private DeviceService deviceService;
	
	@Autowired
	private SMSService smsService;
	
	@Autowired
	private LoanInfoRepository loanInfoRepository;
	
	@Autowired
	private LoanRebateInfoRepository loanRebateInfoRepository;
	
	@Autowired
	private LoanCustInfoRepository loanCustInfoRepository;
	
	@Autowired
	private AuditInfoRespository auditInfoRespository;
	
	@Autowired
	private AttachmentRepository attachmentRepository;
	
	@Autowired
	private RepaymentPlanInfoRepository repaymentPlanInfoRepository;
	
	@Autowired
	private InvestInfoRepositoryCustom investInfoRepositoryCustom;
	
	@Autowired
	private LoanDetailInfoRepository loanDetailInfoRepository;
	
	@Autowired
	private OpenThirdService openThirdService;
	
	@Autowired
	private LoanInfoRepositoryCustom loanInfoRepositoryCustom;
	
	@Autowired
	private LogInfoEntityRepository logInfoEntityRepository;
	
	@Autowired
	private ParamService paramService;

	@Autowired
	private ExportFileService exportFileService;
	
	@Autowired
	private AccountService accountService;
	
	@Autowired
	private LoanManagerService loanManagerService;
	
	@Autowired
	private InterfaceDetailInfoRepository interfaceDetailInfoRepository;
	
	@Autowired
	private FlowNumberService numberService;
	
	@Autowired
	private ExpandInfoService expandInfoService;
	
	@Autowired
	private JobRunListenerRepository jobRunListenerRepository;
	
	@Autowired
	private LoanRepaymentPlanService loanRepaymentPlanService;
	
	@Autowired
	private WealthHoldInfoRepository wealthHoldInfoRepository;
	
	@Autowired
	private CreditRightValueRepository creditRightValueRepository;
	
	@Autowired
	private LoanTransferApplyRepository loanTransferApplyRepository;
	
	@Autowired
	private LoanTransferRepository loanTransferRepository;
	
	@Autowired
	private WealthHoldHistoryInfoRepository wealthHoldHistoryInfoRepository;
	
	@Autowired
	private CustInfoRepository custInfoRepository;
	
	@Autowired
	private AccountInfoRepository accountInfoRepository;
	
	@Autowired
	private InvestInfoRepository investInfoRepository;
	
	@Autowired
	private InvestDetailInfoRepository investDetailInfoRepository;
	
	@Autowired
	private CustRecommendInfoRepository custRecommendInfoRepository;
	
	@Autowired
	private SubAccountInfoRepository subAccountInfoRepository;
	
	@Autowired
	private CustAccountService custAccountService;
	
	@Autowired
	private DeviceInfoRepository deviceInfoRepository;
	
	@Autowired
	private BankCardService bankCardService;
	
	@Autowired
	private BankCardInfoRepository bankCardInfoRepository;

	@Autowired
	private CustActivityService custActivityService;
	@Autowired
	private RepaymentPlanCopeRepository repaymentPlanCopeRepository;

	@Autowired
	private PurchaseAwardInfoService purchaseAwardInfoService;
	@Override
	public ResultVo cancel(Map<String, Object> params) throws SLException {
		// 项目撤销
		return internalLoanProjectService.cancelProject(params);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ResultVo unRelease(Map<String, Object> params) throws SLException {
		ResultVo result = internalLoanProjectService.unReleaseProjectExt(params);
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
	public ResultVo releaseLoan(final Map<String, Object> params) throws SLException {
		ResultVo result = internalLoanProjectService.releaseLoan(params);
		if(ResultVo.isSuccess(result)) {
			// 发送短信
			List<Map<String, Object>> smsList = (List<Map<String, Object>>)result.getValue("data");
			for(Map<String, Object> sms : smsList) {
				smsService.asnySendSMSAndSystemMessage(sms);
			}
			
			// 异步生成协议
			exportFileService.asynDownloadContract(params);
			
		}
		
		return result;
	}

	@Autowired
	private InternalLoanProjectService internalLoanProjectService;
	
	@Service
	public static class InternalLoanProjectService {
		
		@PersistenceContext
		private EntityManager manager;
		
		@Autowired
		private LoanInfoRepository loanInfoRepository;
		
		@Autowired
		private InvestInfoRepository investInfoRepository;
		
		@Autowired
		private FlowNumberService numberService;
		
		@Autowired
		private SubAccountInfoRepository subAccountInfoRepository;
		
		@Autowired
		private AccountInfoRepository accountInfoRepository;
		
		@Autowired
		private AccountFlowService accountFlowService;
		
		@Autowired
		private LogInfoEntityRepository logInfoEntityRepository;
		
		@Autowired
		private ProjectInfoRepositoryCustom projectInfoRepositoryCustom;
		
		@Autowired
		private InterfaceDetailInfoRepository interfaceDetailInfoRepository;
		
		@Autowired
		private ExpandInfoService expandInfoService;
		
		@Autowired
		private RepaymentPlanInfoRepository repaymentPlanInfoRepository;
		
		@Autowired
		private CustAccountService custAccountService;
		
		@Autowired
		private WealthHoldInfoRepository wealthHoldInfoRepository;
		
		@Autowired
		private LoanManagerService loanManagerService;
		
		@Autowired
		private LoanServicePlanInfoRepository loanServicePlanInfoRepository;

		@Autowired
		private CustActivityInfoRepository custActivityInfoRepository;

		@Autowired
		private RepaymentPlanCopeRepository repaymentPlanCopeRepository;

        @Autowired
        private ActivityAwardService activityAwardService;

        @Autowired
        private PurchaseAwardInfoService purchaseAwardInfoService;

		@Autowired
		private LoanReserveRepository loanReserveRepository;	
		
		@Autowired
		private LoanReserveRelationRepository loanReserveRelationRepository;
		
		 /* 撤销
		 *
		 * @author  zhangze
		 * @date    2017年6月9日 下午4:17:16
		 * @param params
		 * @return
		 * @throws SLException
		 */
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public ResultVo cancelProject(Map<String, Object> params)
				throws SLException {
			String loanId = (String)params.get("loanId");
			String userId = (String)params.get("userId");

			// 1) 状态必须为待发布或者待审核
			LoanInfoEntity loanInfoEntity = loanInfoRepository.findOne(loanId);
			if(loanInfoEntity == null) {
				throw new SLException("项目不存在");
			}
			if(!Constant.LOAN_STATUS_01.equals(loanInfoEntity.getLoanStatus())
					&& !Constant.LOAN_STATUS_04.equals(loanInfoEntity.getLoanStatus())) {
				throw new SLException("项目非待发布或者待审核状态");
			}
			if(!Constant.GRANT_STATUS_01.equals(loanInfoEntity.getGrantStatus())) {
				throw new SLException("项目非待放款状态");
			}
			
			// 2) 修改项目状态为流标
			String oldProjectStatus = loanInfoEntity.getLoanStatus();
			loanInfoEntity.setLoanStatus(Constant.LOAN_STATUS_12);
			loanInfoEntity.setBasicModelProperty(userId, false);
			
			// 3) 记录日志
			LogInfoEntity logInfoEntity = new LogInfoEntity();
			logInfoEntity.setRelateType(Constant.TABLE_BAO_T_LOAN_INFO);
			logInfoEntity.setRelatePrimary(loanId);
			logInfoEntity.setLogType(Constant.OPERATION_TYPE_69);
			logInfoEntity.setOperBeforeContent(oldProjectStatus);
			logInfoEntity.setOperAfterContent(loanInfoEntity.getLoanStatus());
			logInfoEntity.setOperDesc("");
			logInfoEntity.setOperPerson(userId);
			logInfoEntity.setOperIpaddress((String)params.get("ipAddress"));
			logInfoEntity.setMemo(String.format("流标项目%s:%s", loanInfoEntity.getLoanCode(), loanInfoEntity.getLoanDesc()));
			logInfoEntity.setBasicModelProperty(userId, true);
			logInfoEntityRepository.save(logInfoEntity);
			
			return new ResultVo(true, "项目撤销成功");
		}
		
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
			
			String loanId = (String)params.get("loanId");
			String userId = (String)params.get("userId");
			List<Map<String, Object>> smsList = Lists.newArrayList();

			// 1) 状态必须为募集中
			LoanInfoEntity loanInfoEntity = loanInfoRepository.findOne(loanId);
			if(loanInfoEntity == null) {
				throw new SLException("项目不存在");
			}
			if(!Constant.LOAN_STATUS_05.equals(loanInfoEntity.getLoanStatus())
					&& !Constant.LOAN_STATUS_06.equals(loanInfoEntity.getLoanStatus())
					&& !Constant.LOAN_MANAGER_GROUP_03.equals(loanInfoEntity.getLoanStatus())) {
				throw new SLException("项目非募集或者满标复核状态");
			}
			if(!Constant.GRANT_STATUS_01.equals(loanInfoEntity.getGrantStatus())
				&& !Constant.GRANT_STATUS_03.equals(loanInfoEntity.getGrantStatus()) ) {
				throw new SLException("项目非待放款或者放款失败状态");
			}
			
			// 2) 修改项目状态为流标
			String oldProjectStatus = loanInfoEntity.getLoanStatus();
			loanInfoEntity.setLoanStatus(Constant.LOAN_STATUS_12);
			loanInfoEntity.setBasicModelProperty(userId, false);
			
			// 3) 查询所有投资，状态置为无效
			List<InvestInfoEntity> investList = investInfoRepository.findByLoanId(loanId);
			for(InvestInfoEntity i : investList) {
				i.setInvestStatus(Constant.INVEST_STATUS_UNRELEASE);
				i.setBasicModelProperty(userId, false);
			}
			
			// 4) 循环退还投资金额至用户账户
			String reqeustNo = numberService.generateTradeBatchNumber();
			AccountInfoEntity loanerAccount = accountInfoRepository.findByCustId(loanInfoEntity.getCustId());
			SubAccountInfoEntity projectSubAccount = subAccountInfoRepository.findByRelatePrimary(loanInfoEntity.getId()); // 项目分账户
			// 分账金额的钱等于0时，需从主账户中将资金划拨到分账户
			if(loanerAccount.getAccountAvailableAmount().compareTo(BigDecimal.ZERO) >= 0
					&& projectSubAccount.getAccountAvailableValue().compareTo(BigDecimal.ZERO) == 0) {
				
				// 检查是否存在服务费流水，若存在需将服务费划拨回主账户
				subMainAccountBack(loanId, userId, reqeustNo);
				
				// 检查是否存在放款流水，若存在需将从主账户中将资金划拨到分账户
				moveToSubAccount(loanInfoEntity, userId, reqeustNo);
			}

			List<AccountInfoEntity> accountList = accountInfoRepository.findAccountByLoanId(loanId);
			for(InvestInfoEntity i : investList) {
				for(AccountInfoEntity a : accountList) {
					if(a.getCustId().equals(i.getCustId())) {
						
						// 更新项目分账
						projectSubAccount.setAccountTotalValue(ArithUtil.sub(projectSubAccount.getAccountTotalValue(), i.getInvestAmount()));
						projectSubAccount.setAccountAvailableValue(ArithUtil.sub(projectSubAccount.getAccountAvailableValue(), i.getInvestAmount()));
						projectSubAccount.setBasicModelProperty(userId, false);
						AccountFlowInfoEntity accountFlowInfoEntity = accountFlowService.saveAccountFlow(null, projectSubAccount, a, null, "3", 
								SubjectConstant.TRADE_FLOW_TYPE_UNRELEASE_LOAN, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, 
								i.getInvestAmount(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_UNRELEASE_LOAN, 
								Constant.TABLE_BAO_T_INVEST_INFO, i.getId(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
						accountFlowInfoEntity.setMemo(String.format("%s流标", loanInfoEntity.getLoanDesc()));
						
						// 更新用户主账
						a.setAccountTotalAmount(ArithUtil.add(a.getAccountTotalAmount(), i.getInvestAmount()));
						a.setAccountAvailableAmount(ArithUtil.add(a.getAccountAvailableAmount(), i.getInvestAmount()));
						a.setBasicModelProperty(userId, false);
						AccountFlowInfoEntity accountFlowInfoEntity2 = accountFlowService.saveAccountFlow(a, null, null, projectSubAccount, "2", 
								SubjectConstant.TRADE_FLOW_TYPE_UNRELEASE_LOAN, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING, 
								i.getInvestAmount(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_UNRELEASE_LOAN, 
								Constant.TABLE_BAO_T_INVEST_INFO, i.getId(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
						accountFlowInfoEntity2.setMemo(String.format("%s流标", loanInfoEntity.getLoanDesc()));
					
						break;
					}
				}
			}
			
			// 5) 判断项目分账户是否退还全部余额
			if(projectSubAccount != null && ArithUtil.compare(BigDecimal.ZERO, projectSubAccount.getAccountTotalValue()) != 0) {
				throw new SLException("流标失败！项目资金未能全部退回到用户账户");
			}
			
			//流标的，把预约表(Bao_t_Loan_Reserve)的已投资金额减去预约关系表(Bao_t_Loan_Reserve_Relation)对应的预约出借金额
			List<LoanReserveEntity> loanReserveList = loanReserveRepository.findByLoanId(loanId);

			if (loanReserveList.size() > 0) {
				List<LoanReserveRelationEntity> loanReserveRelationEntityList = loanReserveRelationRepository.findByLoanId(loanId);

				for (LoanReserveEntity loanReserveEntity : loanReserveList) {
					for (LoanReserveRelationEntity loanReserveRelationEntity : loanReserveRelationEntityList) {
						if (loanReserveEntity.getId().equals(loanReserveRelationEntity.getLoanReserveId()) && loanReserveEntity.getAlreadyInvestAmount().compareTo(BigDecimal.ZERO)>0) {
							loanReserveEntity.setAlreadyInvestAmount(ArithUtil.sub(loanReserveEntity.getAlreadyInvestAmount(),loanReserveRelationEntity.getTradeAmount()));
						}
					}
				}
			}
			
			// 6) 记录日志
			LogInfoEntity logInfoEntity = new LogInfoEntity();
			logInfoEntity.setRelateType(Constant.TABLE_BAO_T_LOAN_INFO);
			logInfoEntity.setRelatePrimary(loanId);
			logInfoEntity.setLogType(Constant.OPERATION_TYPE_69);
			logInfoEntity.setOperBeforeContent(oldProjectStatus);
			logInfoEntity.setOperAfterContent(loanInfoEntity.getLoanStatus());
			logInfoEntity.setOperDesc("");
			logInfoEntity.setOperPerson(userId);
			logInfoEntity.setOperIpaddress((String)params.get("ipAddress"));
			logInfoEntity.setMemo(String.format("流标项目%s:%s", loanInfoEntity.getLoanCode(), loanInfoEntity.getLoanDesc()));
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
				smsParams.put("messageType", Constant.SMS_TYPE_LOAN_UNRELEASE);
				smsParams.put("values", new Object[] { // 短信息内容
						String.format("%s-%s", loanInfoEntity.getLoanType(), loanInfoEntity.getLoanDesc()),
						ArithUtil.formatScale(new BigDecimal(m.get("tradeAmount").toString()), 2).toPlainString()});
				smsParams.put("systemMessage", new Object[] { // 站内信内容
						DateUtils.formatDate((Date)m.get("tradeDate"), "yyyy-MM-dd"),
						String.format("%s-%s", loanInfoEntity.getLoanType(), loanInfoEntity.getLoanDesc()),
						ArithUtil.formatScale(new BigDecimal(m.get("tradeAmount").toString()), 2).toPlainString()});
				smsList.add(smsParams);
			}
			
			
			String repaymentMethod = loanInfoEntity.getRepaymentMethod();
			Integer loanTerm = Integer.parseInt(loanInfoEntity.getLoanTerm().toString());
			String loanType =  loanInfoEntity.getLoanType();
			String loanUnit =  loanInfoEntity.getLoanUnit();
			// 判断借款类型、还款方式、还款期限在项目折年系数表中是否为商务（Flag标识），若为商务则表示需要通知。
			if(loanManagerService.needToNotifyBiz(repaymentMethod, loanTerm, loanType, loanUnit)
					|| Constant.PUSH_FLAG_YES.equals(loanInfoEntity.getPushFlag())){
				// 8) 通知商务流标
				InterfaceDetailInfoEntity interfaceDetailInfoEntity = 
						interfaceDetailInfoRepository.findByThirdPartyTypeInterfaceType(loanInfoEntity.getCompanyName(), Constant.NOTIFY_TYPE_LOAN_STATUS);
				if (interfaceDetailInfoEntity != null) {
					Map<String, Object> map = Maps.newHashMap();
					map.put("relateType", Constant.TABLE_BAO_T_LOAN_INFO);
					map.put("relatePrimary", loanId);
					map.put("tradeCode", numberService.generateTradeNumber());
					map.put("innerTradeCode", numberService.generateOpenServiceTradeNumber());
					map.put("interfaceDetailInfoEntity", interfaceDetailInfoEntity);
					map.put("userId", Constant.SYSTEM_USER_BACK);
					map.put("memo", Constant.OPERATION_TYPE_69);
					expandInfoService.saveExpandInfo(map);
				}
			}
			
			return new ResultVo(true, "项目流标成功", smsList);
		}

		/**
		 * 流标--使用红包流水返还
		 *
		 * @author  wangjf
		 * @date    2016年1月16日 下午2:20:16
		 * @param params
		 * @return
		 * @throws SLException
		 */
		@SuppressWarnings("deprecation")
		@Transactional(rollbackFor = SLException.class)
		public ResultVo unReleaseProjectExt(Map<String, Object> params)
				throws SLException {

			String loanId = (String)params.get("loanId");
			String userId = (String)params.get("userId");
			List<Map<String, Object>> smsList = Lists.newArrayList();

			// 1) 状态必须为募集中
			LoanInfoEntity loanInfoEntity = loanInfoRepository.findOne(loanId);
			if(loanInfoEntity == null) {
				throw new SLException("项目不存在");
			}
			if(!Constant.LOAN_STATUS_05.equals(loanInfoEntity.getLoanStatus())
					&& !Constant.LOAN_STATUS_06.equals(loanInfoEntity.getLoanStatus())
					&& !Constant.LOAN_MANAGER_GROUP_03.equals(loanInfoEntity.getLoanStatus())) {
				throw new SLException("项目非募集或者满标复核状态");
			}
			if(!Constant.GRANT_STATUS_01.equals(loanInfoEntity.getGrantStatus())
					&& !Constant.GRANT_STATUS_03.equals(loanInfoEntity.getGrantStatus()) ) {
				throw new SLException("项目非待放款或者放款失败状态");
			}

			// 2) 修改项目状态为流标
			String oldProjectStatus = loanInfoEntity.getLoanStatus();
			loanInfoEntity.setLoanStatus(Constant.LOAN_STATUS_12);
			loanInfoEntity.setBasicModelProperty(userId, false);

			// 3) 查询所有投资，状态置为无效
			List<InvestInfoEntity> investList = investInfoRepository.findByLoanId(loanId);
			for(InvestInfoEntity i : investList) {
				i.setInvestStatus(Constant.INVEST_STATUS_UNRELEASE);
				i.setBasicModelProperty(userId, false);
			}

			// 4) 循环退还投资金额至用户账户
			String reqeustNo = numberService.generateTradeBatchNumber();
			AccountInfoEntity loanerAccount = accountInfoRepository.findByCustId(loanInfoEntity.getCustId());
			SubAccountInfoEntity projectSubAccount = subAccountInfoRepository.findByRelatePrimary(loanInfoEntity.getId()); // 项目分账户
			// 分账金额的钱等于0时，需从主账户中将资金划拨到分账户
			if(loanerAccount.getAccountAvailableAmount().compareTo(BigDecimal.ZERO) >= 0 
					&& projectSubAccount.getAccountAvailableValue().compareTo(BigDecimal.ZERO) == 0) {
				
				// 检查是否存在服务费流水，若存在需将服务费划拨回主账户
				subMainAccountBack(loanId, userId, reqeustNo);
				// 检查是否存在放款流水，若存在需将从主账户中将资金划拨到分账户
				moveToSubAccount(loanInfoEntity, userId, reqeustNo);
			}

			// 该标的有使用现金劵奖励的记录
			List<CustActivityInfoEntity> custActivityInfoEntities = custActivityInfoRepository
					.findByLoanIdAndRewardShapeAndTradeStatus(loanId, Constant.REAWARD_SPREAD_04, Constant.USER_ACTIVITY_TRADE_STATUS_03);
			Map<String, CustActivityInfoEntity> custActivityInfoEntityMap = new HashMap<>(custActivityInfoEntities.size());
			// 下面的循环map构建依据 一个标的只会使用一个现金奖励 2017-6-30
				for (CustActivityInfoEntity custActivityInfoEntity : custActivityInfoEntities) {
					custActivityInfoEntityMap.put(custActivityInfoEntity.getId(), custActivityInfoEntity);
				}

			List<AccountInfoEntity> accountList = accountInfoRepository.findAccountByLoanId(loanId);
			AccountInfoEntity redEnvelopeAccount = accountInfoRepository.findByAccountNo(Constant.ACCOUNT_NO_RED_ENVELOPE);
			
			for(InvestInfoEntity i : investList) {
				for(AccountInfoEntity a : accountList) {
					if(a.getCustId().equals(i.getCustId())) {
						// 投资人账户如果有使用现金红包的 需扣除红包金额
						CustActivityInfoEntity custActivityInfoEntity = custActivityInfoEntityMap.get(i.getCustActivityId());
						// 更新项目分账
						projectSubAccount.setAccountTotalValue(ArithUtil.sub(projectSubAccount.getAccountTotalValue(), i.getInvestAmount()));
						projectSubAccount.setAccountAvailableValue(ArithUtil.sub(projectSubAccount.getAccountAvailableValue(), i.getInvestAmount()));
						projectSubAccount.setBasicModelProperty(userId, false);
						AccountFlowInfoEntity accountFlowInfoEntity = accountFlowService.saveAccountFlow(null, projectSubAccount, a, null, "3",
								SubjectConstant.TRADE_FLOW_TYPE_UNRELEASE_LOAN, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT,
								i.getInvestAmount(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_UNRELEASE_LOAN,
								Constant.TABLE_BAO_T_INVEST_INFO, i.getId(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
						accountFlowInfoEntity.setMemo(String.format("%s流标", loanInfoEntity.getLoanDesc()));
						if (custActivityInfoEntity != null && custActivityInfoEntity.getUsableAmount() != null) {
							i.setInvestAmount(i.getInvestAmount().subtract(custActivityInfoEntity.getUsableAmount()));//用户实际支付金额=投资金额-红包
						}
						// 更新用户主账
						a.setAccountTotalAmount(ArithUtil.add(a.getAccountTotalAmount(), i.getInvestAmount()));
						a.setAccountAvailableAmount(ArithUtil.add(a.getAccountAvailableAmount(), i.getInvestAmount()));
						a.setBasicModelProperty(userId, false);
						AccountFlowInfoEntity accountFlowInfoEntity2 = accountFlowService.saveAccountFlow(a, null, null, projectSubAccount, "2",
								SubjectConstant.TRADE_FLOW_TYPE_UNRELEASE_LOAN, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING,
								i.getInvestAmount(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_UNRELEASE_LOAN,
								Constant.TABLE_BAO_T_INVEST_INFO, i.getId(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
						accountFlowInfoEntity2.setMemo(String.format("%s流标", loanInfoEntity.getLoanDesc()));

						
						if (custActivityInfoEntity != null) {
							// 如果是加息券
							// 1)重置加息券使用状态
							 if(Constant.REAWARD_SPREAD_05.equals(custActivityInfoEntity.getRewardShape())){
				                // 重置加息券状态 -- 已领取
				                custActivityInfoEntity.setTradeStatus(Constant.USER_ACTIVITY_TRADE_STATUS_01);
				                custActivityInfoEntity.setMemo("");

				              }else if (Constant.REAWARD_SPREAD_04.equals(custActivityInfoEntity.getRewardShape())) {
								// 如果是红包
								// 账户扣除红包金额
								// 重置红包使用状态
								// ..
								//TODO hxp因为用户主账户购买的时候，没有往其账户增加红包金额，固在流标的时候 也不需要对其账户进行红包金额的减少
	//							a.setAccountTotalAmount(ArithUtil.sub(a.getAccountTotalAmount(), custActivityInfoEntity.getUsableAmount()));
	//							a.setAccountAvailableAmount(ArithUtil.sub(a.getAccountAvailableAmount(), custActivityInfoEntity.getUsableAmount()));
	//							a.setBasicModelProperty(userId, false);
								redEnvelopeAccount.setAccountTotalAmount(ArithUtil.add(redEnvelopeAccount.getAccountTotalAmount(), custActivityInfoEntity.getUsableAmount()));
								redEnvelopeAccount.setAccountAvailableAmount(ArithUtil.add(redEnvelopeAccount.getAccountAvailableAmount(), custActivityInfoEntity.getUsableAmount()));
								redEnvelopeAccount.setBasicModelProperty(userId, false);
								AccountFlowInfoEntity accountFlowInfoEntity0 = accountFlowService.saveAccountFlowExt(redEnvelopeAccount, null, null, null, "1",
										SubjectConstant.TRADE_FLOW_TYPE_UNRELEASE_LOAN, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING,
										custActivityInfoEntity.getUsableAmount(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_UNRELEASE_LOAN,
										Constant.TABLE_BAO_T_INVEST_INFO, i.getId(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
								accountFlowInfoEntity2.setMemo(String.format("%s流标--红包返还%s元",
										loanInfoEntity.getLoanDesc(), custActivityInfoEntity.getUsableAmount()));
								accountFlowInfoEntity0.setMemo(String.format("%s流标--红包返还%s元",
										loanInfoEntity.getLoanDesc(), custActivityInfoEntity.getUsableAmount()));
								// 重置红包状态 -- 已领取
								custActivityInfoEntity.setTradeStatus(Constant.USER_ACTIVITY_TRADE_STATUS_01);
								//custActivityInfoEntity.setMemo(custActivityInfoEntity.getMemo().replaceAll("已付款", "已流标"));
								custActivityInfoEntity.setMemo("");
							}
						}

						break;
					}
				}
			}

			// 5) 判断项目分账户是否退还全部余额
			if(projectSubAccount != null && ArithUtil.compare(BigDecimal.ZERO, projectSubAccount.getAccountTotalValue()) != 0) {
				throw new SLException("流标失败！项目资金未能全部退回到用户账户");
			}
			
			//流标的，把预约表(Bao_t_Loan_Reserve)的已投资金额减去预约关系表(Bao_t_Loan_Reserve_Relation)对应的预约出借金额
			List<LoanReserveEntity> loanReserveList = loanReserveRepository.findByLoanId(loanId);

			if (loanReserveList.size() > 0) {
				List<LoanReserveRelationEntity> loanReserveRelationEntityList = loanReserveRelationRepository.findByLoanId(loanId);

				for (LoanReserveEntity loanReserveEntity : loanReserveList) {
					for (LoanReserveRelationEntity loanReserveRelationEntity : loanReserveRelationEntityList) {
						if (loanReserveEntity.getId().equals(loanReserveRelationEntity.getLoanReserveId()) && loanReserveEntity.getAlreadyInvestAmount().compareTo(BigDecimal.ZERO)>0) {
							loanReserveEntity.setAlreadyInvestAmount(ArithUtil.sub(loanReserveEntity.getAlreadyInvestAmount(),loanReserveRelationEntity.getTradeAmount()));
						}
					}
				}
			}

			// 6) 记录日志
			LogInfoEntity logInfoEntity = new LogInfoEntity();
			logInfoEntity.setRelateType(Constant.TABLE_BAO_T_LOAN_INFO);
			logInfoEntity.setRelatePrimary(loanId);
			logInfoEntity.setLogType(Constant.OPERATION_TYPE_69);
			logInfoEntity.setOperBeforeContent(oldProjectStatus);
			logInfoEntity.setOperAfterContent(loanInfoEntity.getLoanStatus());
			logInfoEntity.setOperDesc("");
			logInfoEntity.setOperPerson(userId);
			logInfoEntity.setOperIpaddress((String)params.get("ipAddress"));
			logInfoEntity.setMemo(String.format("流标项目%s:%s", loanInfoEntity.getLoanCode(), loanInfoEntity.getLoanDesc()));
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
				smsParams.put("messageType", Constant.SMS_TYPE_LOAN_UNRELEASE);
				smsParams.put("values", new Object[] { // 短信息内容
						String.format("%s-%s", loanInfoEntity.getLoanType(), loanInfoEntity.getLoanDesc()),
						ArithUtil.formatScale(new BigDecimal(m.get("tradeAmount").toString()), 2).toPlainString()});
				smsParams.put("systemMessage", new Object[] { // 站内信内容
						DateUtils.formatDate((Date)m.get("tradeDate"), "yyyy-MM-dd"),
						String.format("%s-%s", loanInfoEntity.getLoanType(), loanInfoEntity.getLoanDesc()),
						ArithUtil.formatScale(new BigDecimal(m.get("tradeAmount").toString()), 2).toPlainString()});
				smsList.add(smsParams);
			}


			String repaymentMethod = loanInfoEntity.getRepaymentMethod();
			Integer loanTerm = Integer.parseInt(loanInfoEntity.getLoanTerm().toString());
			String loanType =  loanInfoEntity.getLoanType();
			String loanUnit =  loanInfoEntity.getLoanUnit();
			// 判断借款类型、还款方式、还款期限在项目折年系数表中是否为商务（Flag标识），若为商务则表示需要通知。
			if(loanManagerService.needToNotifyBiz(repaymentMethod, loanTerm, loanType, loanUnit)
					|| Constant.PUSH_FLAG_YES.equals(loanInfoEntity.getPushFlag())){
				// 8) 通知商务流标
				InterfaceDetailInfoEntity interfaceDetailInfoEntity =
						interfaceDetailInfoRepository.findByThirdPartyTypeInterfaceType(loanInfoEntity.getCompanyName(), Constant.NOTIFY_TYPE_LOAN_STATUS);
				if (interfaceDetailInfoEntity != null) {
					Map<String, Object> map = Maps.newHashMap();
					map.put("relateType", Constant.TABLE_BAO_T_LOAN_INFO);
					map.put("relatePrimary", loanId);
					map.put("tradeCode", numberService.generateTradeNumber());
					map.put("innerTradeCode", numberService.generateOpenServiceTradeNumber());
					map.put("interfaceDetailInfoEntity", interfaceDetailInfoEntity);
					map.put("userId", Constant.SYSTEM_USER_BACK);
					map.put("memo", Constant.OPERATION_TYPE_69);
					expandInfoService.saveExpandInfo(map);
				}
			}
			// 9) 通知微信公众号流标
				for(Map<String, Object> m : page.getContent()) {
					Map<String, Object> smsParams = new HashMap<String, Object>();
					if(m.get("openId")!=null){
						smsParams.put("openId", m.get("openId"));	
						smsParams.put("template_id", Constant.SMS_TYPE_VX_3);	
						StringBuilder sb=new StringBuilder()
							.append(DateUtils.formatDate((Date)m.get("tradeDate"), "yyyy-MM-dd"))
							.append("|")
							.append(String.format("%s-%s", loanInfoEntity.getLoanType(), loanInfoEntity.getLoanDesc()))
							.append("|")
							.append(ArithUtil.formatScale(new BigDecimal(m.get("tradeAmount").toString()), 2).toPlainString());

						smsParams.put("data", java.net.URLEncoder.encode(sb.toString()));
						String sign=HttpRequestUtil.sign((String)smsParams.get("openId"),(String)smsParams.get("template_id"),sb.toString());
						smsParams.put("sign", sign);
						HttpRequestUtil.pushSmsToVX(smsParams);
					}
				}
			return new ResultVo(true, "项目流标成功", smsList);
		}
		
		/**
		 * 生效项目
		 *
		 * @author  wangjf
		 * @date    2016年12月1日 下午5:22:09
		 * @return
		 */
		@SuppressWarnings("unchecked")
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public ResultVo releaseLoan(Map<String, Object> params)
				throws SLException {
			
			final String loanId = (String)params.get("loanId");
			String userId = (String)params.get("userId");
			Boolean needUpdateAccount = (Boolean)params.get("needUpdateAccount");
			String grantStatus = (String)params.get("grantStatus");
			Date now = new Date();
			List<Map<String, Object>> repaymentList = (List<Map<String, Object>>)params.get("repaymentList");
			List<Map<String, Object>> smsList = Lists.newArrayList();

			// 1) 验证项目是否为满标复核状态
			LoanInfoEntity loanInfoEntity = loanInfoRepository.findOne(loanId);
			if(loanInfoEntity == null) {
				throw new SLException("项目不存在");
			}
			String oldProjectStatus = loanInfoEntity.getLoanStatus();
			if(!Constant.LOAN_STATUS_06.equals(oldProjectStatus)) {
				throw new SLException("项目非满标复核状态");
			}
			
			// 更新放款状态
			grantLoan(params);
			
			// 放款失败即返回
			if(!Constant.GRANT_STATUS_02.equals(grantStatus)) {
				return new ResultVo(false, "放款失败");
			}
			
			Collections.sort(repaymentList, new Comparator<Map<String, Object>>(){
				@Override
				public int compare(Map<String, Object> o1,
						Map<String, Object> o2) {
					
					return Integer.parseInt(o1.get("currentTerm").toString()) - Integer.parseInt(o2.get("currentTerm").toString());
				}
			});
			
			Date startDate = now; // 起息日期为当天
			Date endDate   = DateUtils.parseDate(repaymentList.get(repaymentList.size() - 1).get("expectRepaymentDate").toString(), "yyyyMMdd");
			
			// 2) 修改项目状态、生效时间、到期时间等
			loanInfoEntity.setLoanStatus(Constant.LOAN_STATUS_08);
			loanInfoEntity.setGrantDate(new Timestamp(now.getTime()));
			loanInfoEntity.setInvestStartDate(new Timestamp(startDate.getTime()));
			loanInfoEntity.setInvestEndDate(new Timestamp(endDate.getTime()));
			loanInfoEntity.setBasicModelProperty(userId, false);
			
			// 3) 修改还款计划到期时间
			List<RepaymentPlanInfoEntity> repaymentPlanList = repaymentPlanInfoRepository.findByLoanId(loanId);
			BigDecimal totalInterest = BigDecimal.ZERO; // 统计所有利息
			for(RepaymentPlanInfoEntity r : repaymentPlanList) {
				String expectRepaymentDate = "";
				for(Map<String, Object> m : repaymentList) {
					if(Integer.parseInt(m.get("currentTerm").toString()) == r.getCurrentTerm()) {
						expectRepaymentDate = (String)m.get("expectRepaymentDate");
						break;
					}
				}
				if(StringUtils.isEmpty(expectRepaymentDate)) {
					throw new SLException("还款数据不全，缺少" + r.getCurrentTerm() + "期还款日期");
				}
				r.setExpectRepaymentDate(expectRepaymentDate);
				r.setBasicModelProperty(userId, false);
				totalInterest = ArithUtil.add(totalInterest, r.getRepaymentInterest());
			}
			//同时修改还款计划副本
//			List<RepaymentPlanCopeEntity> repaymentCopePlanList = repaymentPlanCopeRepository.queryByLoanId(loanId);
//			//如果不是雪城就会为空不做副本就不做修改
//			if(repaymentCopePlanList!=null && repaymentCopePlanList.size()!=0) {
//				for(RepaymentPlanCopeEntity r : repaymentCopePlanList) {
//					String expectRepaymentDate = "";
//					for(Map<String, Object> m : repaymentList) {
//						if(Integer.parseInt(m.get("currentTerm").toString()) == r.getCurrentTerm()) {
//							expectRepaymentDate = (String)m.get("expectRepaymentDate");
//							if(!StringUtils.isEmpty(expectRepaymentDate)) {
//								r.setExpectRepaymentDate(expectRepaymentDate);
//								r.setBasicModelProperty(userId, false);
//							}else {
//								throw new SLException("还款数据不全，缺少" + r.getCurrentTerm() + "期还款日期");
//							}
//							break;
//						}
//					}
//				}
//			}
			// 4) 设置服务费计划表
			List<LoanServicePlanInfoEntity> servicePlanList = loanServicePlanInfoRepository.findByLoanId(loanId);
			if(servicePlanList != null && servicePlanList.size() > 0) {
				if(servicePlanList.size() == 0) { // 只有一条服务费计划时取到期日期
					servicePlanList.get(0).setExceptDate(DateUtils.formatDate(endDate, "yyyyMMdd"));
				}
				else { // 其他情况服务费计划时间跟还款计划表保持一致
					for(LoanServicePlanInfoEntity s : servicePlanList) {
						for(RepaymentPlanInfoEntity r : repaymentPlanList) {
							if(s.getCurrentTerm() == r.getCurrentTerm()) {
								s.setExceptDate(r.getExpectRepaymentDate());
								s.setBasicModelProperty(userId, false);
								break;
							}
						}
					}
				}
			}

			// 设置下个还款日
			RepaymentPlanInfoEntity firstRepaymentPlan = repaymentPlanList.get(0);
			loanInfoEntity.getLoanDetailInfoEntity().setNextExpiry(new Timestamp(DateUtils.parseDate(firstRepaymentPlan.getExpectRepaymentDate(), "yyyyMMdd").getTime()));
			loanInfoEntity.getLoanDetailInfoEntity().setCurrTerm(new BigDecimal(firstRepaymentPlan.getCurrentTerm().toString()));
			loanInfoEntity.getLoanDetailInfoEntity().setCurrTremEndDate(new Timestamp(DateUtils.parseDate(firstRepaymentPlan.getExpectRepaymentDate(), "yyyyMMdd").getTime()));

			if(needUpdateAccount) { // 需要扣借款人的款
				
				String requestNo = numberService.generateTradeBatchNumber();
				BigDecimal tradeAmount = loanInfoEntity.getLoanAmount();
				AccountInfoEntity loanerAccount = accountInfoRepository.findByCustId(loanInfoEntity.getCustId());
				
				// 5) 更新借款人账户（扣款）
				custAccountService.updateAccount(loanerAccount, null, null, 
						null, "6", SubjectConstant.TRADE_FLOW_TYPE_GRANT_LOAN, 
						requestNo, tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_GRANT_LOAN, 
						Constant.TABLE_BAO_T_LOAN_INFO, loanInfoEntity.getId(),  
						String.format("优选项目[%s]生效", loanInfoEntity.getLoanCode()), userId);
			}
			
			// 6) 修改投资人投资到期期限和生成投资人持有债权信息
			List<InvestInfoEntity> investList = investInfoRepository.findByLoanId(loanId);
			List<SubAccountInfoEntity> subAccountList = subAccountInfoRepository.findSubAccountWithInvestByLoanId(loanId);
			List<WealthHoldInfoEntity> holdList = Lists.newArrayList();

            List<InvestInfoEntity> investListWithAward = Lists.newArrayList();

			for(InvestInfoEntity i : investList) {
				i.setEffectDate(DateUtils.formatDate(startDate, "yyyyMMdd"));
				i.setExpireDate(DateUtils.formatDate(endDate, "yyyyMMdd"));
				i.setInvestStatus(Constant.INVEST_STATUS_EARN);
				i.setBasicModelProperty(userId, false);
							
				WealthHoldInfoEntity wealthHoldInfoEntity = new WealthHoldInfoEntity();
				wealthHoldInfoEntity.setInvestId(i.getId());
				wealthHoldInfoEntity.setCustId(i.getCustId());
				wealthHoldInfoEntity.setLoanId(loanId);
				wealthHoldInfoEntity.setSubAccountId(findUserSubAccountByInvest(subAccountList, i.getId()).getId());
				wealthHoldInfoEntity.setHoldScale(ArithUtil.div(i.getInvestAmount(), loanInfoEntity.getHoldAmount()));
				wealthHoldInfoEntity.setHoldAmount(i.getInvestAmount());
				wealthHoldInfoEntity.setExceptAmount(ArithUtil.mul(totalInterest, wealthHoldInfoEntity.getHoldScale()));
				wealthHoldInfoEntity.setReceivedAmount(BigDecimal.ZERO);
				wealthHoldInfoEntity.setHoldStatus(Constant.HOLD_STATUS_01);
				wealthHoldInfoEntity.setIsCenter(Constant.IS_CENTER_02);
				wealthHoldInfoEntity.setTradeScale(BigDecimal.ZERO);
				wealthHoldInfoEntity.setBasicModelProperty(userId, true);
				holdList.add(wealthHoldInfoEntity);

                // 使用了加息券的投资
                if (Constant.RATE_COUPON.equals(i.getRedPacketType())
                        && !CommonUtils.isEmpty(i.getInvestRedPacket()) && !CommonUtils.isEmpty(i.getCustActivityId())) {
                    investListWithAward.add(i);
                }
			}
			wealthHoldInfoRepository.save(holdList);

            try {
                // 加息券计划
                generatePurchaseAwardPlan(investListWithAward, repaymentPlanList, loanInfoEntity);
            } catch (Exception e) {
                log.error("生成加息券计划出错！标的id：[{}], exception is : {}", loanId, e.getStackTrace());
            }

			// 7) 记录日志
			LogInfoEntity logInfoEntity = new LogInfoEntity();
			logInfoEntity.setRelateType(Constant.TABLE_BAO_T_LOAN_INFO);
			logInfoEntity.setRelatePrimary(loanId);
			logInfoEntity.setLogType(Constant.OPERATION_TYPE_70);
			logInfoEntity.setOperBeforeContent(oldProjectStatus);
			logInfoEntity.setOperAfterContent(loanInfoEntity.getLoanStatus());
			logInfoEntity.setOperDesc("");
			logInfoEntity.setOperPerson(userId);
			logInfoEntity.setOperIpaddress((String)params.get("ipAddress"));
			logInfoEntity.setMemo(String.format("生效项目%s:%s", loanInfoEntity.getLoanCode(), loanInfoEntity.getLoanDesc()));
			logInfoEntity.setBasicModelProperty(userId, true);
			logInfoEntityRepository.save(logInfoEntity);
			
			// 8) 发送投资人短信
			params.put("start", 0);
			params.put("length", Integer.MAX_VALUE);
			Page<Map<String, Object>> page = projectInfoRepositoryCustom.queryProjectJoinList(params);
			for(Map<String, Object> m : page.getContent()) {
				Map<String, Object> smsParams = new HashMap<String, Object>();
				smsParams.put("mobile", m.get("mobile"));
				smsParams.put("custId", m.get("custId"));	
				smsParams.put("messageType", Constant.SMS_TYPE_LOAN_RELEASE);
				smsParams.put("values", new Object[] { // 短信息内容
						String.format("%s-%s", loanInfoEntity.getLoanType(), loanInfoEntity.getLoanDesc()),
						ArithUtil.formatScale(new BigDecimal(m.get("tradeAmount").toString()), 2).toPlainString()});
				smsParams.put("systemMessage", new Object[] { // 站内信内容
						DateUtils.formatDate((Date)m.get("tradeDate"), "yyyy-MM-dd"),
						String.format("%s-%s", loanInfoEntity.getLoanType(), loanInfoEntity.getLoanDesc()),
						DateUtils.formatDate(startDate, "yyyy-MM-dd"),
						DateUtils.formatDate(endDate, "yyyy-MM-dd"),
						ArithUtil.formatPercent(loanInfoEntity.getLoanDetailInfoEntity().getYearIrr()),
						ArithUtil.formatScale(new BigDecimal(m.get("tradeAmount").toString()), 2).toPlainString()});
				smsList.add(smsParams);
			}
			
			// 9) 通知微信公众号项目生效
			log.info("优选项目生效发送站内信");
			for(Map<String, Object> m : page.getContent()) {
				Map<String, Object> vxParams = new HashMap<String, Object>();
				if(m.get("openId")!=null){
					vxParams.put("openId", m.get("openId"));	
					vxParams.put("template_id", Constant.SMS_TYPE_VX_4);	
					StringBuilder sb=new StringBuilder()
						.append(DateUtils.formatDate((Date)m.get("tradeDate"), "yyyy-MM-dd"))
						.append("|")
						.append(String.format("%s-%s", loanInfoEntity.getLoanType(), loanInfoEntity.getLoanDesc()))
						.append("|")
						.append(DateUtils.formatDate(startDate, "yyyy-MM-dd"))
						.append("|")
						.append(DateUtils.formatDate(endDate, "yyyy-MM-dd"))
						.append("|")
						.append(ArithUtil.formatPercent(loanInfoEntity.getLoanDetailInfoEntity().getYearIrr()))
						.append("|")
						.append(ArithUtil.formatScale(new BigDecimal(m.get("tradeAmount").toString()), 2).toPlainString());
					vxParams.put("data", java.net.URLEncoder.encode(sb.toString()));
					String sign=HttpRequestUtil.sign((String)vxParams.get("openId"),(String)vxParams.get("template_id"),sb.toString());
					vxParams.put("sign", sign);
					HttpRequestUtil.pushSmsToVX(vxParams);
				}
					
			}
			
			return new ResultVo(true, "生效成功", smsList);
		}

        /**
         * 生成加息计划
         *
         * @param investInfoList    使用过加息券的投资
         * @param repaymentPlanList 还款计划
         * @param loanInfo          标的信息
         */
        public void generatePurchaseAwardPlan(List<InvestInfoEntity> investInfoList,
                                              List<RepaymentPlanInfoEntity> repaymentPlanList, LoanInfoEntity loanInfo) {
            if (!CommonUtils.isEmpty(investInfoList) && !CommonUtils.isEmpty(repaymentPlanList)) {
                for (InvestInfoEntity investInfo : investInfoList) {
                    List<PurchaseAwardInfoEntity> list = Lists.newArrayList();
                    // 投资金额
                    BigDecimal investAmount = investInfo.getInvestAmount();
                    // 查询加息券详情
                    List<Map<String, Object>> activityAwardList = activityAwardService.findByCustActivityId(investInfo.getCustActivityId());
                    if (CommonUtils.isEmpty(activityAwardList)) {
                        log.error("为找到对应的加息券， 投资id : {}", investInfo.getId());
                        continue;
                    }
                    Map<String, Object> activityAward = activityAwardList.get(0);
                    Integer awardSeatTerm = CommonUtils.emptyToInt(activityAward.get("SEAT_TERM"));// 加息券期限
                    BigDecimal grantAmount = CommonUtils.emptyToDecimal(activityAward.get("GRANT_AMOUNT"));// 加息比例
                    grantAmount = ArithUtil.div(grantAmount, new BigDecimal(100));
                    Long repaymentCycle = loanInfo.getRepaymentCycle();// 还款频率
                    // 目前只有“1个月1还”的标的
                    String repaymentMethod = loanInfo.getRepaymentMethod();

                    if (Constant.REPAYMENT_METHOD_01.equals(repaymentMethod)) {/** 等额本息 */
                        list = rateCouponPlan4ACPI(loanInfo, investInfo, awardSeatTerm, grantAmount, repaymentPlanList);
                    } else {
                        for (int i = 0; i < repaymentPlanList.size(); i++) {
                            if (i < awardSeatTerm) {// 加息券有几期，就生成几条加息计划
                                RepaymentPlanInfoEntity repaymentPlan = repaymentPlanList.get(i);
                                // 本期利息
                                BigDecimal repaymentInterest = repaymentPlan.getRepaymentInterest();
                                Integer currentTerm = repaymentPlan.getCurrentTerm();

                                PurchaseAwardInfoEntity purchaseAwardInfo = new PurchaseAwardInfoEntity();
                                purchaseAwardInfo.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
                                purchaseAwardInfo.setLoanId(loanInfo.getId());
                                purchaseAwardInfo.setAwardStatus(Constant.PURCHASE_AWARD_STATUS_NO);
                                purchaseAwardInfo.setCustId(investInfo.getCustId());
                                purchaseAwardInfo.setInvestId(investInfo.getId());
                                purchaseAwardInfo.setInvestAmonut(investAmount);
                                purchaseAwardInfo.setCurrentTerm(currentTerm);
                                purchaseAwardInfo.setPaymentPlanId(repaymentPlanList.get(i).getId());

                                BigDecimal awardAmount = BigDecimal.ZERO;
                                if (Constant.REPAYMENT_METHOD_03.equals(repaymentMethod)) {
                                    // 投资本金*加息期限（一个月按30天算）*利率/360
                                    // investAmount*awardSeatTerm*30*grantAmountActual/360
                                    awardAmount = ArithUtil.div(ArithUtil.mul(ArithUtil.mul(
                                            ArithUtil.mul(investAmount, new BigDecimal(awardSeatTerm)), new BigDecimal(30)), grantAmount)
                                            , new BigDecimal(360));
                                    purchaseAwardInfo.setAwardAmount(awardAmount);
                                } else if (Constant.REPAYMENT_METHOD_06.equals(repaymentMethod)) {
                                    // 投资本金*30*利率/360
                                    // investAmount*30*grantAmountActual/360
                                    awardAmount = ArithUtil.div(ArithUtil.mul(ArithUtil.mul(investAmount, new BigDecimal(30)), grantAmount)
                                            , new BigDecimal(360));
                                    purchaseAwardInfo.setAwardAmount(awardAmount);
                                }
                                list.add(purchaseAwardInfo);
                            } else {
                                break;
                            }
                        }
                    }
                    purchaseAwardInfoService.saveAwardList(list);
                }
            }
        }

        /**
         * 生成等额本息标的投资的加息计划（加息券不计算服务费）
         *
         * @param activityAward     加息券
         * @param loanInfo          标的信息
         * @param investInfo        投资信息
         * @param repaymentPlanList 还款计划
         * @return
         */
        private List<PurchaseAwardInfoEntity> rateCouponPlan4ACPI(LoanInfoEntity loanInfo, InvestInfoEntity investInfo, Integer awardTerm,
                                                                  BigDecimal grantAmount, List<RepaymentPlanInfoEntity> repaymentPlanList) {

            BigDecimal investAmount = investInfo.getInvestAmount();

            // 投资期限
            Integer typeTerm = Integer.parseInt(loanInfo.getLoanTerm().toString());
            // 投资月利率
            BigDecimal monthlyRate = ArithUtil.div(grantAmount, new BigDecimal(Constant.MONTH_OF_YEAR));
            // (1+投资月利率)^期限
            BigDecimal commonInterest = new BigDecimal(String.valueOf(Math.pow((1 + monthlyRate.doubleValue()), typeTerm)));
            // 投资人应得 = (投资金额*投资月利率*(1+投资月利率)^期限)/((1+投资月利率)^期限-1)
            BigDecimal userIncome = ArithUtil.div(ArithUtil.mul(ArithUtil.mul(investAmount, monthlyRate), commonInterest), ArithUtil.sub(commonInterest, BigDecimal.ONE));
            // 总本息 = 投资人应得*期限
            BigDecimal totalUserIncome = ArithUtil.mul(userIncome, new BigDecimal(awardTerm));

            userIncome = ArithUtil.formatScale(userIncome, 2);
            totalUserIncome = ArithUtil.formatScale(totalUserIncome, 2);

            // 上一期剩余本金(开始时等于投资金额)
            BigDecimal prevRemainderPrincipal = investAmount;
            List<PurchaseAwardInfoEntity> planList = Lists.newArrayList();

            for(int i = 1; i <= typeTerm; i ++) {
                if (i > awardTerm) {
                    // 加息计划的数据总条数为加息券的期数
                    break;
                }
                // 利息 = 上一期剩余本金*投资月利率
                BigDecimal repaymentInterest = ArithUtil.mul(prevRemainderPrincipal, monthlyRate);
                repaymentInterest = ArithUtil.formatScale(repaymentInterest, 2);
                // 本金 = 投资人应得-利息
                BigDecimal repaymentPrincipal = ArithUtil.sub(userIncome, repaymentInterest);
                // 剩余本金=上一期剩余本金-本期应还本金
                BigDecimal remainderPrincipal = ArithUtil.sub(prevRemainderPrincipal, repaymentPrincipal);

                repaymentPrincipal = ArithUtil.formatScale(repaymentPrincipal, 2);
                remainderPrincipal = ArithUtil.formatScale(remainderPrincipal, 2);

                // 生成加息计划
                PurchaseAwardInfoEntity purchaseAwardInfo = new PurchaseAwardInfoEntity();
                purchaseAwardInfo.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
                purchaseAwardInfo.setLoanId(loanInfo.getId());
                purchaseAwardInfo.setAwardStatus(Constant.PURCHASE_AWARD_STATUS_NO);
                purchaseAwardInfo.setCustId(investInfo.getCustId());
                purchaseAwardInfo.setInvestId(investInfo.getId());
                purchaseAwardInfo.setInvestAmonut(investAmount);
                purchaseAwardInfo.setCurrentTerm(i);
                purchaseAwardInfo.setAwardAmount(repaymentInterest);
                purchaseAwardInfo.setPaymentPlanId(repaymentPlanList.get(i - 1).getId());

                planList.add(purchaseAwardInfo);

                // 修改上一期剩余本金为本次剩余本金
                prevRemainderPrincipal = remainderPrincipal;
            }
            return planList;
        }

		/**
		 * 项目放款
		 *
		 * @author  wangjf
		 * @date    2016年12月3日 上午11:09:32
		 * @param params
		 * @return
		 * @throws SLException
		 */
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public ResultVo grantLoan(Map<String, Object> params) throws SLException {
			String loanId = (String)params.get("loanId");
			String userId = (String)params.get("userId");
			String grantStatus = (String)params.get("grantStatus");

			LoanInfoEntity loanInfoEntity = loanInfoRepository.findOne(loanId);
			if(loanInfoEntity == null) {
				throw new SLException("项目不存在");
			}
			
			String oldGrantStatus = loanInfoEntity.getGrantStatus();

			// 更新放款状态
			loanInfoEntity.setGrantDate(new Timestamp(new Date().getTime()));
			loanInfoEntity.setGrantStatus(grantStatus);
			//loanInfoEntity.setGrantUser(userId);
			loanInfoEntity.setBasicModelProperty(userId, false);
			
			// 记录日志
			LogInfoEntity logInfoEntity = new LogInfoEntity();
			logInfoEntity.setRelateType(Constant.TABLE_BAO_T_LOAN_INFO);
			logInfoEntity.setRelatePrimary(loanId);
			logInfoEntity.setLogType(Constant.OPERATION_TYPE_77);
			logInfoEntity.setOperBeforeContent(oldGrantStatus);
			logInfoEntity.setOperAfterContent(loanInfoEntity.getGrantStatus());
			logInfoEntity.setOperDesc("");
			logInfoEntity.setOperPerson(userId);
			logInfoEntity.setOperIpaddress((String)params.get("ipAddress"));
			logInfoEntity.setMemo(String.format("项目%s:%s放款", loanInfoEntity.getLoanCode(), loanInfoEntity.getLoanDesc()));
			logInfoEntity.setBasicModelProperty(userId, true);
			logInfoEntityRepository.save(logInfoEntity);

			return new ResultVo(true, "放款成功");
		}
		
		public SubAccountInfoEntity findUserSubAccountByInvest(final List<SubAccountInfoEntity> accountList, final String investId) throws SLException{
			
			SubAccountInfoEntity userAccount = null;
			for(SubAccountInfoEntity account : accountList) {
				if(account.getRelatePrimary().equals(investId)) {
					userAccount = account;
					break;
				}
			}
			
			if(userAccount == null) {
				throw new SLException("未找到投资人分账户");
			}
			
			return userAccount;
		}
		
		/**
		 * 借款人分账——>主张
		 *
		 * @author  wangjf
		 * @date    2017年2月28日 上午10:15:42
		 * @param loanInfoEntity
		 * @param userId
		 * @throws SLException
		 */
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public void moveToMainAccount(LoanInfoEntity loanInfoEntity, String userId) throws SLException {
			
			SubAccountInfoEntity loanerSubAccount = subAccountInfoRepository.findByRelatePrimary(loanInfoEntity.getId());
			AccountInfoEntity loanerAccount = accountInfoRepository.findByCustId(loanInfoEntity.getCustId());
			
			// 借款人分账——>主账户
			custAccountService.updateAccount(null, loanerSubAccount, loanerAccount, 
					null, "3", SubjectConstant.TRADE_FLOW_TYPE_GRANT_LOAN, 
					numberService.generateTradeBatchNumber(), loanInfoEntity.getLoanAmount(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_GRANT_LOAN, 
					Constant.TABLE_BAO_T_LOAN_INFO, loanInfoEntity.getId(),  
					String.format("优选项目[%s-%s]放款", loanInfoEntity.getLoanType(), loanInfoEntity.getLoanDesc()), userId);
		}
		
		/**
		 * 借款人主张——>分账
		 *
		 * @author  wangjf
		 * @date    2017年2月28日 上午10:15:42
		 * @param loanInfoEntity
		 * @param userId
		 * @throws SLException
		 */
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public void moveToSubAccount(LoanInfoEntity loanInfoEntity, String userId, String requestNo) throws SLException {
			
			SubAccountInfoEntity loanerSubAccount = subAccountInfoRepository.findByRelatePrimary(loanInfoEntity.getId());
			AccountInfoEntity loanerAccount = accountInfoRepository.findByCustId(loanInfoEntity.getCustId());
			
			// 借款人主账户——>分账户
			int counts = accountFlowService.queryByTradeTypeAndRelatePrimary(SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_GRANT_LOAN, loanInfoEntity.getId());
			if(counts != 0) {
				custAccountService.updateAccount(loanerAccount, null, null, 
						loanerSubAccount, "2", SubjectConstant.TRADE_FLOW_TYPE_GRANT_LOAN_REVERT, 
						requestNo, loanInfoEntity.getLoanAmount(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_GRANT_LOAN_REVERT, 
						Constant.TABLE_BAO_T_LOAN_INFO, loanInfoEntity.getId(),  
						String.format("优选项目[%s-%s]放款冲正", loanInfoEntity.getLoanType(), loanInfoEntity.getLoanDesc()), userId);
			}
		}
		
		/**
		 * 借款人扣除服务费
		 *
		 * @author  wangjf
		 * @date    2017年2月28日 上午10:16:52
		 * @param loanId
		 * @param userId
		 * @throws SLException 
		 */
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public void subMainAccount(String loanId,  String userId) throws SLException {
			LoanInfoEntity loanInfoEntity = loanInfoRepository.findOne(loanId);
			AccountInfoEntity loanerAccount = accountInfoRepository.findByCustId(loanInfoEntity.getCustId());
			AccountInfoEntity companyAccount= accountInfoRepository.findByCustId(Constant.CUST_ID_ERAN);
			
			// 检查是否存在服务费流水，若存在表示已扣除服务费
			int counts = accountFlowService.queryByTradeTypeAndRelatePrimary(SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_MANAGE_LOAN, loanId);
			if(counts == 0) {
				custAccountService.updateAccount(loanerAccount, null, companyAccount, 
						null, "1", SubjectConstant.TRADE_FLOW_TYPE_MANAGE_LOAN, 
						numberService.generateTradeBatchNumber(), loanInfoEntity.getPlatServiceAmount(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_MANAGE_LOAN, 
						Constant.TABLE_BAO_T_LOAN_INFO, loanId,  
						"内扣服务费", userId);
			}
		}
		
		/**
		 * 借款人扣除服务费冲正
		 *
		 * @author  wangjf
		 * @date    2017年7月11日 下午8:17:51
		 * @param loanId
		 * @param userId
		 * @param requestNo
		 * @throws SLException
		 */
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public void subMainAccountBack(String loanId,  String userId, String requestNo) throws SLException {
			LoanInfoEntity loanInfoEntity = loanInfoRepository.findOne(loanId);
			AccountInfoEntity loanerAccount = accountInfoRepository.findByCustId(loanInfoEntity.getCustId());
			AccountInfoEntity companyAccount= accountInfoRepository.findByCustId(Constant.CUST_ID_ERAN);
			
			// 检查是否存在服务费流水，若存在表示已扣除服务费
			int counts = accountFlowService.queryByTradeTypeAndRelatePrimary(SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_MANAGE_LOAN, loanId);
			if(counts != 0) {
				custAccountService.updateAccount(companyAccount, null, loanerAccount, 
						null, "1", SubjectConstant.TRADE_FLOW_TYPE_MANAGE_LOAN_REVERT, 
						requestNo, loanInfoEntity.getPlatServiceAmount(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_MANAGE_LOAN_REVERT, 
						Constant.TABLE_BAO_T_LOAN_INFO, loanId,  
						"内扣服务费", userId);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo saveLoanProject(Map<String, Object> params) throws SLException {
		
		String loanNo = (String)params.get("loanNo");
		String repaymentMethod = (String)params.get("repaymentMethod");
		Integer loanTerm = Integer.parseInt(params.get("loanTerm").toString());
		String loanType = (String)params.get("loanType");
		String termUnit = (String)params.get("termUnit");
		//奖励利率--huifei
		BigDecimal awardRate = params.get("awardRate") == null ? BigDecimal.ZERO : new BigDecimal(params.get("awardRate").toString());

		// 验证借款编号是否存在
		LoanInfoEntity loanInfoEntity = loanInfoRepository.findByLoanCode(loanNo);
		if(loanInfoEntity != null) {
			// 雪橙金服 不是流标和拒绝状态的标的不允许需要重新推送
			if (Constant.DEBT_SOURCE_XCJF.equals(params.get("companyName").toString().trim())) {
				if(!Constant.LOAN_STATUS_12.equals(loanInfoEntity.getLoanStatus())
						&& !Constant.LOAN_STATUS_14.equals(loanInfoEntity.getLoanStatus())) {
					return new ResultVo(false, "借款编号重复", OpenSerivceCode.ERR_LOAN_NO_REPEATE);
				}
				// 重新推送 需要将以前的借款编号修改掉
				String nowTime = DateUtils.getCurrentDate("yyyyMMddHHmmss");
				// 修改原借款编号为原借款编号-当前时间
				String newLoanNo = loanInfoEntity.getLoanCode() + "-" + nowTime;
				loanInfoEntity.setLoanCode(newLoanNo);
			} else {
				return new ResultVo(false, "借款编号重复", OpenSerivceCode.ERR_LOAN_NO_REPEATE);
			}
		}


		/*begin 验证橙信贷利率是否在折扣范围内--huifei*/
		// 验证还款方式、期限是否在我们折扣范围内
		LoanRebateInfoEntity loanRebateInfoEntity = null;
		if (Constant.DEBT_SOURCE_XCJF.equals(params.get("companyName").toString().trim()) && awardRate.compareTo(BigDecimal.ZERO) > 0) {
			loanRebateInfoEntity = loanRebateInfoRepository.findByRepaymentMethodAndLoanTermAndProductTypeAndLoanUnitAndAwardRate(repaymentMethod, loanTerm, loanType,
					termUnit, awardRate);
		} else {
			loanRebateInfoEntity = loanRebateInfoRepository.findByRepaymentMethodAndLoanTermAndProductTypeAndLoanUnit(repaymentMethod, loanTerm, loanType, termUnit);
		}

		if(loanRebateInfoEntity == null) {
			return new ResultVo(false, "还款方式和借款期限不在折价系数范围内", OpenSerivceCode.ERR_OTHER);
		}
		/*end 验证橙信贷利率是否在折扣范围内--huifei*/

		// 保存借款客户信息
		Map<String, Object> custMap = (Map<String, Object>)params.get("custMap");
		Map<String, Object> bankMap = (Map<String, Object>)params.get("bankMap");
		String custName = (String)custMap.get("custName");
		String credentialsCode = (String)custMap.get("credentialsCode");
		LoanCustInfoEntity loanCustInfoEntity = new LoanCustInfoEntity();
		loanCustInfoEntity.setMobile((String)custMap.get("mobile"));
		loanCustInfoEntity.setCustName(custName);
		loanCustInfoEntity.setCredentialsCode(credentialsCode);
		loanCustInfoEntity.setCredentialsType(Constant.LOAN_ID_CARD);
		loanCustInfoEntity.setCustGender(Integer.parseInt(credentialsCode.substring(16, 17)) % 2 != 0 ? Constant.SEX_MAN : Constant.SEX_WOMAN);
		loanCustInfoEntity.setCustAge(DateUtils.yearPhaseDiffer(DateUtils.parseDate(credentialsCode.substring(6, 14), "yyyyMMdd"), new Date()));
		loanCustInfoEntity.setCustCode((String)custMap.get("custCode"));
		loanCustInfoEntity.setCustEducation((String)custMap.get("education"));
		loanCustInfoEntity.setMarriage((String)custMap.get("marriage"));
		loanCustInfoEntity.setHomeAddress((String)custMap.get("homeAddress"));
		loanCustInfoEntity.setWorkCorporation((String)custMap.get("workCorporation"));
		loanCustInfoEntity.setWorkAddress((String)custMap.get("workAddress"));
		loanCustInfoEntity.setWorkTelephone((String)custMap.get("workTelephone"));
		if(custMap.containsKey("workYear")) {
			loanCustInfoEntity.setWorkYear(custMap.get("workYear").toString());
		}
		loanCustInfoEntity.setSalaryType((String)custMap.get("salaryType"));
		  if(bankMap != null) {
		      if(bankMap.containsKey("bankName")) {
		        loanCustInfoEntity.setBankName((String)bankMap.get("bankName"));
		      }
		      if(bankMap.containsKey("cardNo")) {
		        loanCustInfoEntity.setCardNo((String)bankMap.get("cardNo"));
		      }
		      // 意真金融 | 拿米金融 | 雪橙金服 | 巨涟金融 | 财富现金贷 的标的需要验证绑卡
		      if (Constant.DEBT_SOURCE_NMJR.equals(params.get("companyName").toString().trim())
					|| Constant.DEBT_SOURCE_XCJF.equals(params.get("companyName").toString().trim())
					|| Constant.DEBT_SOURCE_YZJR.equals(params.get("companyName").toString().trim())
					  || Constant.DEBT_SOURCE_JLJR.equals(params.get("companyName").toString().trim())
					  || Constant.DEBT_SOURCE_CFXJD.equals(params.get("companyName").toString().trim())) {
		    	  // 如果不存在银行卡，则绑卡
			      List<BankCardInfoEntity> list = bankCardInfoRepository.findByCardNo((String) bankMap.get("cardNo"));
			      if(list.size() == 0) {
			          //需要绑卡
			          HashMap<String, Object> bindParams = Maps.newHashMap();
			          bindParams.put("tradeType", "绑卡");
			          bindParams.put("custName",custMap.get("custName"));
			          bindParams.put("credentialsCode",custMap.get("credentialsCode"));
			          bindParams.put("custCode",custMap.get("custCode"));
			          bindParams.put("bankName",bankMap.get("bankName"));
			          bindParams.put("bankCardNo",bankMap.get("cardNo"));
			          if(bankMap.containsKey("responseCardId")) {
			        	  bindParams.put("responseCardId",bankMap.get("responseCardId"));
			          }
			          if(bankMap.containsKey("openProvince")) {
			        	  bindParams.put("openProvince",bankMap.get("openProvince"));
			          }
			          if(bankMap.containsKey("openCity")) {
			        	  bindParams.put("openCity",bankMap.get("openCity"));
			          }
			          if(bankMap.containsKey("subBranchName")) {
			        	  bindParams.put("subBranchName",bankMap.get("subBranchName"));
			          }
			          ResultVo bindBankCard = bankCardService.bindBankCard(bindParams);
			          if(!ResultVo.isSuccess(bindBankCard)) { // 绑卡失败
			            return bindBankCard;
			          }  
			       }
		      } 
		      
		    }
		loanCustInfoEntity = loanCustInfoRepository.save(loanCustInfoEntity);
	//添加银行卡协议
//		PotocalEntity potocalEntity = new PotocalEntity();
//		potocalEntity.setBankCardCode((String)bankMap.get("cardNo"));
//		potocalEntity.setBankName((String)bankMap.get("bankName"));
//		potocalEntity.setNoAgree((String)bankMap.get("noAgree"));
//		potocalEntity.setCreateDate(new Date());
//		potocalEntity.setUpdateDate(null);
//		potocalEntity.setCustId(loanCustInfoEntity.getId());
//		protocalRepository.save(potocalEntity);	
		// 保存借款信息
		loanInfoEntity = new LoanInfoEntity();
		loanInfoEntity.setRelateType(Constant.TABLE_BAO_T_LOAN_CUST_INFO);
		loanInfoEntity.setLoanCustInfoEntity(loanCustInfoEntity);
		loanInfoEntity.setDebtSourceCode((String)params.get("debtSourceCode"));
		loanInfoEntity.setLoanCode(loanNo);
		loanInfoEntity.setCreditAcctStatus(Constant.CREDIT_RIGHT_STATUS_NORMAL);
		loanInfoEntity.setLoanTerm(Long.parseLong(loanTerm.toString()));
		loanInfoEntity.setLoanUnit((String)params.get("termUnit"));
		loanInfoEntity.setImportDate(new Timestamp(new Date().getTime()));
		loanInfoEntity.setLoanAmount(new BigDecimal(params.get("loanAmount").toString()));
		loanInfoEntity.setHoldAmount(new BigDecimal(params.get("loanAmount").toString()));
		loanInfoEntity.setHoldScale(new BigDecimal("1"));
		loanInfoEntity.setRepaymentMethod((String)params.get("repaymentMethod"));
		loanInfoEntity.setRepaymentCycle(Long.parseLong(params.get("repaymentCycle").toString()));
		loanInfoEntity.setAssetTypeCode("散标投资");
		loanInfoEntity.setLoanDesc((String)params.get("loanDesc"));
		loanInfoEntity.setLoanTitle(String.format("【%s】%s", params.get("loanType").toString(), params.get("loanDesc").toString()));
		loanInfoEntity.setLoanStatus(Constant.LOAN_STATUS_01);
		loanInfoEntity.setInvestMinAmount(new BigDecimal("100"));
		loanInfoEntity.setInvestMaxAmount(new BigDecimal("1000000"));
		loanInfoEntity.setIncreaseAmount(new BigDecimal("100"));
		// 意真金融 | 拿米金融 | 巨涟金融 | 财富现金贷 的标的不允许转让
		if (Constant.DEBT_SOURCE_NMJR.equals(params.get("companyName").toString().trim())
			|| Constant.DEBT_SOURCE_YZJR.equals(params.get("companyName").toString().trim())
			|| Constant.DEBT_SOURCE_JLJR.equals(params.get("companyName").toString().trim())
			|| Constant.DEBT_SOURCE_CFXJD.equals(params.get("companyName").toString().trim())) {
			loanInfoEntity.setSeatTerm(-1); // 不允许转让
		} else {
			loanInfoEntity.setSeatTerm(30); // 30天之内不允许转让
		}
		// 雪橙金服的标的loanType都是橙信贷
	    if (Constant.DEBT_SOURCE_XCJF.equals(params.get("companyName").toString().trim())) {
	    	loanInfoEntity.setLoanType(Constant.LOAN_TYPE_XCJF);
	    	loanInfoEntity.setLoanInfo((String)params.get("loanType"));
			loanInfoEntity.setAwardRate(awardRate);//借款表存入奖励利率--huifei
	    } else {
	    	loanInfoEntity.setLoanType((String)params.get("loanType"));
	    }
		loanInfoEntity.setRasieDays(3);
		loanInfoEntity.setRebateRatio(loanRebateInfoEntity.getRebateRatio());
		loanInfoEntity.setCompanyName((String)params.get("companyName"));
		loanInfoEntity.setApplyTime(DateUtils.parseDate((String)params.get("applyTime"), "yyyyMMddHHmmss"));
		loanInfoEntity.setCarType((String)params.get("carType"));
		loanInfoEntity.setPropertyRight((String)params.get("propertyRight"));
		loanInfoEntity.setHouseType((String)params.get("houseType"));
		loanInfoEntity.setGrantStatus(Constant.GRANT_STATUS_01);
		// 雪橙 | 巨涟使用同一个借款协议
		if (Constant.DEBT_SOURCE_XCJF.equals(params.get("companyName").toString().trim())
				|| Constant.DEBT_SOURCE_JLJR.equals(params.get("companyName").toString().trim())) {
			loanInfoEntity.setProtocalType("g_20170702");
		}
		else {
			loanInfoEntity.setProtocalType("g_20161220");
		}
		loanInfoEntity.setReceiveStatus(Constant.RECEIVE_STATUS_02);
		loanInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
		if (Constant.DEBT_SOURCE_YZJR.equals(params.get("companyName").toString().trim())
				 || Constant.DEBT_SOURCE_JLJR.equals(params.get("companyName").toString().trim())
				 || Constant.DEBT_SOURCE_CFXJD.equals(params.get("companyName").toString().trim())) {
			loanInfoEntity.setGrantType(Constant.GRANT_TYPE_02); 
		}else{
			loanInfoEntity.setGrantType(Constant.GRANT_TYPE_01);
		}
		if (Constant.DEBT_SOURCE_XCJF.equals(params.get("companyName").toString().trim())
				|| Constant.DEBT_SOURCE_JLJR.equals(params.get("companyName").toString().trim())) {
			loanInfoEntity.setManageExpenseDealType(Constant.MANAGE_EXPENSE_DEAL_TYPE_02);
		}else{
			loanInfoEntity.setManageExpenseDealType(Constant.MANAGE_EXPENSE_DEAL_TYPE_01);
		}		
		loanInfoEntity.setNewerFlag("普通标");
		// 意真金融 | 拿米金融 | 雪橙金服 | 巨涟金融 | 财富现金贷  的标的默认为脱密已完成
		if (Constant.DEBT_SOURCE_NMJR.equals(params.get("companyName").toString().trim())
				|| Constant.DEBT_SOURCE_XCJF.equals(params.get("companyName").toString().trim())
				|| Constant.DEBT_SOURCE_YZJR.equals(params.get("companyName").toString().trim())
				|| Constant.DEBT_SOURCE_JLJR.equals(params.get("companyName").toString().trim())
				|| Constant.DEBT_SOURCE_CFXJD.equals(params.get("companyName").toString().trim())) {
			loanInfoEntity.setAttachmentFlag(Constant.ATTACHMENT_FLAG_03);
		} else {
			loanInfoEntity.setAttachmentFlag(Constant.ATTACHMENT_FLAG_01);//商务推送默认
		}
		
		if(StringUtils.isEmpty(params.get("isAllowAutoInvest"))){
			loanInfoEntity.setIsAllowAutoInvest("是");
		}else{
			loanInfoEntity.setIsAllowAutoInvest(params.get("isAllowAutoInvest").toString());
		}
		if(params.get("memo") != null) {
			loanInfoEntity.setMemo(params.get("memo").toString());
		}
		if(params.get("monthlyManageAmount") != null) {
			loanInfoEntity.setMonthlyManageAmount(new BigDecimal(params.get("monthlyManageAmount").toString()));
		}
		if(params.get("accountManageAmount") != null) {
			loanInfoEntity.setPlatServiceAmount(new BigDecimal(params.get("accountManageAmount").toString()));
		}
		if(params.get("monthlyManageRate") != null) {
			loanInfoEntity.setMonthlyManageRate(new BigDecimal(params.get("monthlyManageRate").toString()));
		}
		// 意真金融 | 拿米金融 | 雪橙金服 | 巨涟金融 | 财富现金贷  的标的平台管理费率 需要特殊处理
		if (Constant.DEBT_SOURCE_NMJR.equals(params.get("companyName").toString().trim())
				|| Constant.DEBT_SOURCE_XCJF.equals(params.get("companyName").toString().trim())
				|| Constant.DEBT_SOURCE_YZJR.equals(params.get("companyName").toString().trim())
				|| Constant.DEBT_SOURCE_JLJR.equals(params.get("companyName").toString().trim())
				|| Constant.DEBT_SOURCE_CFXJD.equals(params.get("companyName").toString().trim())) {
			// 平台管理费率等于打包收益费率 - 年化收益率
			loanInfoEntity.setManageRate(ArithUtil.sub(loanRebateInfoEntity.getTotalYearRate(), loanRebateInfoEntity.getYearRate()));
		}else{
			if(params.get("accountManageRate") != null) {
				loanInfoEntity.setManageRate(new BigDecimal(params.get("accountManageRate").toString()));
			}
		}
		if(params.get("advancedRepaymentRate") != null) {
			loanInfoEntity.setAdvancedRepaymentRate(new BigDecimal(params.get("advancedRepaymentRate").toString()));
		}
		if(params.get("overdueRepaymentRate") != null) {
			loanInfoEntity.setOverdueRepaymentRate(new BigDecimal(params.get("overdueRepaymentRate").toString()));
		}
		loanInfoEntity.setPushFlag(Constant.PUSH_FLAG_YES);
		loanInfoEntity.setChannelFlag(Constant.PUSH_FLAG_NO);
		loanInfoEntity.setSpecialUsersFlag(Constant.PUSH_FLAG_NO);
		//loanInfoEntity.setAwardRate(BigDecimal.ZERO);屏蔽原有代码--huifei
		loanInfoEntity = loanInfoRepository.save(loanInfoEntity);
		
		LoanDetailInfoEntity loanDetailInfoEntity = new LoanDetailInfoEntity();
		loanDetailInfoEntity.setLoanInfoEntity(loanInfoEntity);
		loanDetailInfoEntity.setAlreadyPaymentTerm(BigDecimal.ZERO);
		loanDetailInfoEntity.setCurrTerm(new BigDecimal("1"));
		loanDetailInfoEntity.setCreditRemainderPrincipal(new BigDecimal(params.get("loanAmount").toString()));
		loanDetailInfoEntity.setWealthRemainderPrincipal(BigDecimal.ZERO);
		loanDetailInfoEntity.setExecPvStatus(Constant.EXEC_UN_STATUS);
		loanDetailInfoEntity.setCreditRightStatus(Constant.CREDIT_RIGHT_STATUS_NORMAL);
		// 意真金融 | 拿米金融 | 雪橙金服 | 巨涟金融 | 财富现金贷  的标的年化收益率 需要特殊处理
		if (Constant.DEBT_SOURCE_NMJR.equals(params.get("companyName").toString().trim())
				|| Constant.DEBT_SOURCE_XCJF.equals(params.get("companyName").toString().trim())
				|| Constant.DEBT_SOURCE_YZJR.equals(params.get("companyName").toString().trim())
				|| Constant.DEBT_SOURCE_JLJR.equals(params.get("companyName").toString().trim())
				|| Constant.DEBT_SOURCE_CFXJD.equals(params.get("companyName").toString().trim())) {
			loanDetailInfoEntity.setYearIrr(loanRebateInfoEntity.getYearRate());
		}else{
			loanDetailInfoEntity.setYearIrr(new BigDecimal(params.get("yearIrr").toString()));
		}
		loanDetailInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
		loanDetailInfoEntity = loanDetailInfoRepository.save(loanDetailInfoEntity);
		
		
		//添加银行卡协议
//		if (Constant.DEBT_SOURCE_XCJF.equals(params.get("companyName").toString().trim())) {//雪橙金服，保存借款协议
//			//借款协议liao1018   保存参数：custId,bankName,bankNo,noAgree
//		      
//		      LoanAgreeEntity loanAgreeEntity = new LoanAgreeEntity();
//		      loanAgreeEntity.setId(SharedUtil.getUniqueString());
//		      loanAgreeEntity.setCustId(loanInfoEntity.getLoanCustInfoEntity().getId());
//		      loanAgreeEntity.setBankName((String)bankMap.get("bankName"));
//		      loanAgreeEntity.setBankNo((String)bankMap.get("cardNo"));
//		      loanAgreeEntity.setNoAgree((String)bankMap.get("noAgree"));
//		      loanAgreeEntity = loanAgreeService.save(loanAgreeEntity);
//		      log.info("保存借款协议custId:{},bankName:{},bankNo:{},noAgree:{}",loanAgreeEntity.getCustId(),loanAgreeEntity.getBankName(),loanAgreeEntity.getBankNo(),loanAgreeEntity.getNoAgree());
//		}
		
		// 保存审核信息
		List<Map<String, Object>> auditMapList = (List<Map<String, Object>>)params.get("auditMap");
		Map<String, AuditInfoEntity> auditMap = Maps.newHashMap();
		List<AttachmentInfoEntity> attachmentList = Lists.newArrayList();
		Map<String, Integer> attachmentTypeMap = Maps.newHashMap();
		for(Map<String, Object> m : auditMapList) {
			
			AuditInfoEntity auditInfoEntity = null;
			if(auditMap.containsKey((String)m.get("auditType"))) {
				auditInfoEntity = (AuditInfoEntity)auditMap.get((String)m.get("auditType"));
			}
			else {
				auditInfoEntity = new AuditInfoEntity();
				auditInfoEntity.setRelateType(Constant.TABLE_BAO_T_LOAN_INFO);
				auditInfoEntity.setRelatePrimary(loanInfoEntity.getId());
				auditInfoEntity.setApplyType((String)m.get("auditType"));
				auditInfoEntity.setApplyTime(new Timestamp(DateUtils.parseDate((String)m.get("auditDate"), "yyyyMMddHHmmss").getTime()));
				auditInfoEntity.setAuditTime(new Timestamp(DateUtils.parseDate((String)m.get("auditDate"), "yyyyMMddHHmmss").getTime()));
				if(!StringUtils.isEmpty((String)m.get("auditUser"))) {
					auditInfoEntity.setAuditUser((String)m.get("auditUser"));
				}
				auditInfoEntity.setTradeStatus(Constant.TRADE_STATUS_03);
				auditInfoEntity.setAuditStatus(Constant.AUDIT_STATUS_PASS);
				auditInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
				auditInfoEntity = auditInfoRespository.save(auditInfoEntity);
				
				auditMap.put((String)m.get("auditType"), auditInfoEntity);
			}
			

			String attachmentType = (String)m.get("fileName");
			if(!StringUtils.isEmpty(attachmentType)) {
				AttachmentInfoEntity attachmentInfoEntity = new AttachmentInfoEntity();
				attachmentInfoEntity.setRelateType(Constant.TABLE_BAO_T_AUDIT_INFO);
				attachmentInfoEntity.setRelatePrimary(auditInfoEntity.getId());
				if(attachmentTypeMap.containsKey(attachmentType)) { // 若附件类型重复则自动编号
					int typeNo = attachmentTypeMap.get(attachmentType);
					typeNo ++;
					attachmentInfoEntity.setAttachmentType(String.format("%s%02d", attachmentType, typeNo));
					attachmentTypeMap.put(attachmentType, typeNo);
				}
				else {
					attachmentInfoEntity.setAttachmentType(String.format("%s%02d", attachmentType, 1));
					attachmentTypeMap.put(attachmentType, 1);
				}
				String filePath = (String)m.get("filePath");
				if(Constant.DEBT_SOURCE_XCJF.equals(params.get("companyName").toString().trim())&&!StringUtils.isEmpty(filePath)){
					attachmentInfoEntity.setStoragePath(filePath.trim());
					attachmentInfoEntity.setAttachmentName(attachmentType);
				}else if(!StringUtils.isEmpty(filePath)) {
					File tempFile =new File( filePath.trim());  
			        String fileName = tempFile.getName(); 
			        String fileParent = tempFile.getParent();
					attachmentInfoEntity.setAttachmentName(fileName);
					attachmentInfoEntity.setStoragePath(fileParent + File.separator);
				}
				
				attachmentInfoEntity.setShowType(Constant.SHOW_TYPE_INTERNAL);// 附件仅供内部使用
				attachmentInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
				attachmentList.add(attachmentInfoEntity);
			}
		}
		
		if(attachmentList.size() > 0) {
			attachmentRepository.save(attachmentList);
		}
		List<RepaymentPlanInfoEntity> planList = Lists.newArrayList();
		// 还款计划处理
		// 意真金融 | 拿米金融 | 雪橙金服 | 巨涟金融 | 财富现金贷  的标的年化收益率 需要特殊处理
		if (Constant.DEBT_SOURCE_NMJR.equals(params.get("companyName").toString().trim())
				|| Constant.DEBT_SOURCE_XCJF.equals(params.get("companyName").toString().trim())
				|| Constant.DEBT_SOURCE_YZJR.equals(params.get("companyName").toString().trim())
				|| Constant.DEBT_SOURCE_JLJR.equals(params.get("companyName").toString().trim())
				|| Constant.DEBT_SOURCE_CFXJD.equals(params.get("companyName").toString().trim())) {

			/*begin 等额本息奖励金--huifei*/
			Map<String, Object> awardAmountData = null;
			if (Constant.DEBT_SOURCE_XCJF.equals(params.get("companyName").toString().trim()) && Constant.REPAYMENT_METHOD_01.equals(repaymentMethod)
					&& awardRate.compareTo(BigDecimal.ZERO) > 0) {

				LoanInfoEntity loanInfo = new LoanInfoEntity();
				loanInfo.setId(loanInfoEntity.getId());
				loanInfo.setRepaymentMethod(Constant.REPAYMENT_METHOD_01);
				loanInfo.setLoanAmount(new BigDecimal(params.get("loanAmount").toString()));
				loanInfo.setLoanTerm(Long.parseLong(loanTerm.toString()));//奖励金期限
				loanInfo.setMonthlyManageRate(BigDecimal.ZERO);
				loanInfo.setOverdueRepaymentRate(BigDecimal.ZERO);
				loanInfo.setLoanUnit((String)params.get("termUnit"));
				loanInfo.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);

				LoanDetailInfoEntity loanDetailInfo = new LoanDetailInfoEntity();
				loanDetailInfo.setYearIrr(awardRate);
				loanDetailInfo.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);

				ResultVo repaymentListVo = loanRepaymentPlanService.createRepaymentPlan(loanInfo, loanDetailInfo, loanInfo.getLoanAmount(), new Date());
				awardAmountData = (Map<String, Object>)repaymentListVo.getValue("data");

			}
			/*end 等额本息奖励金--huifei*/

			ResultVo repaymentListVo = loanRepaymentPlanService.createRepaymentPlan(loanInfoEntity, loanDetailInfoEntity, loanInfoEntity.getLoanAmount(), new Date());
			Map<String, Object> data = (Map<String, Object>)repaymentListVo.getValue("data");
			if(data.containsKey("planList")) {
				// 保存还款计划
				List<RepaymentPlanInfoEntity> repaymentList = (List<RepaymentPlanInfoEntity>)data.get("planList");
				for (RepaymentPlanInfoEntity repaymentPlanInfoEntity : repaymentList) {

					/*begin 还款计划插入奖励金额--huifei*/
					Integer crrentTerm = repaymentPlanInfoEntity.getCurrentTerm();
					//1、等额本息
					if (awardAmountData != null) {
						if (awardAmountData.containsKey("planList")) {
							List<RepaymentPlanInfoEntity> awardAmountList = (List<RepaymentPlanInfoEntity>) awardAmountData.get("planList");
							for (RepaymentPlanInfoEntity repaymentPlanInfo : awardAmountList) {

								String loanId = repaymentPlanInfo.getLoanEntity().getId();

								Integer awardTerm = repaymentPlanInfo.getCurrentTerm();

								if (loanId.equals(repaymentPlanInfoEntity.getLoanEntity().getId()) && (crrentTerm == awardTerm)) {

									//当期奖励利率产生的利息，就是当期的奖励金额
									BigDecimal awardAmount = repaymentPlanInfo.getRepaymentInterest();
									repaymentPlanInfoEntity.setAwardAmount(awardAmount);

								}
							}
						}
					}
					//2、每期还息到期付本
					if (Constant.DEBT_SOURCE_XCJF.equals(params.get("companyName").toString().trim()) && Constant.REPAYMENT_METHOD_06.equals(repaymentMethod)
							&& awardRate.compareTo(BigDecimal.ZERO) > 0) {
						if (Constant.LOAN_UNIT_MONTH.equals(termUnit)) {
							//本金
							BigDecimal loanAmount = loanInfoEntity.getLoanAmount();
							//奖励金额 = 投资本金*利率*30/360
							BigDecimal awardAmount = ArithUtil.div(ArithUtil.mul(loanAmount, awardRate), new BigDecimal(12),2);
							repaymentPlanInfoEntity.setAwardAmount(awardAmount);
						} else {
							return new ResultVo(false, "先息后本期数单位为月");
						}
					}
					//3、到期还本付息
					if (Constant.DEBT_SOURCE_XCJF.equals(params.get("companyName").toString().trim()) && Constant.REPAYMENT_METHOD_03.equals(repaymentMethod)
							&& awardRate.compareTo(BigDecimal.ZERO) > 0) {
						//本金
						BigDecimal loanAmount = loanInfoEntity.getLoanAmount();
						if (Constant.LOAN_UNIT_MONTH.equals(termUnit)) {//月标
							//奖励金额 = 投资本金*利率*期数/12
							BigDecimal awardAmount = ArithUtil.div(ArithUtil.mul(ArithUtil.mul(loanAmount, awardRate), new BigDecimal(loanTerm)), new BigDecimal(12),2);
							repaymentPlanInfoEntity.setAwardAmount(awardAmount);
						} else if (Constant.LOAN_UNIT_DAY.equals(termUnit)) {//天标
							//奖励金额 = 投资本金*利率*期数/360
							BigDecimal awardAmount = ArithUtil.div(ArithUtil.mul(ArithUtil.mul(loanAmount, awardRate), new BigDecimal(loanTerm)), new BigDecimal(360),2);
							repaymentPlanInfoEntity.setAwardAmount(awardAmount);
						} else {
							return new ResultVo(false, "到期还本付息期数单位有误");
						}

					}

					/*end 还款计划插入奖励金额--huifei*/

					// 投资期限
					Integer typeTerm = Integer.parseInt(loanInfoEntity.getLoanTerm().toString());
					//投资期限单位
					// 月标的管理费 = 投资金额*平台管理费率*期限/12
					// 天标的管理费= 投资金额*平台管理费率*天标期限/360
					BigDecimal accountManageExpense = ArithUtil.div(ArithUtil.mul(repaymentPlanInfoEntity.getRepaymentPrincipal(), ArithUtil.mul(loanInfoEntity.getManageRate(), new BigDecimal(typeTerm.toString()))), Constant.LOAN_UNIT_DAY.equals(termUnit) ? new BigDecimal(Constant.DAYS_OF_YEAR) : new BigDecimal(Constant.MONTH_OF_YEAR)); 
					accountManageExpense = ArithUtil.formatScale(accountManageExpense, 2);
					repaymentPlanInfoEntity.setAccountManageExpense(accountManageExpense);
					// 每期还款总额 = 每期还款本金 + 利息  + 管理费
					BigDecimal repaymentTotalAmount = ArithUtil.add(repaymentPlanInfoEntity.getRepaymentPrincipal(), ArithUtil.add(repaymentPlanInfoEntity.getRepaymentInterest(), repaymentPlanInfoEntity.getAccountManageExpense()));
					repaymentTotalAmount = ArithUtil.formatScale(repaymentTotalAmount, 2);
					repaymentPlanInfoEntity.setRepaymentTotalAmount(repaymentTotalAmount);
					repaymentPlanInfoEntity.setAdvanceCleanupTotalAmount(repaymentTotalAmount);
					planList.add(repaymentPlanInfoEntity);
				}
			}
		}else{
			// 保存还款计划
			List<Map<String, Object>> repaymentList = (List<Map<String, Object>>)params.get("repaymentList");
			for(Map<String, Object> m : repaymentList) {
				RepaymentPlanInfoEntity repaymentPlanInfoEntity = new RepaymentPlanInfoEntity();
				repaymentPlanInfoEntity.setCurrentTerm(Integer.parseInt(m.get("currentTerm").toString()));
				repaymentPlanInfoEntity.setExpectRepaymentDate((String)m.get("expectRepaymentDate"));
				repaymentPlanInfoEntity.setTermAlreadyRepayAmount(BigDecimal.ZERO);
				repaymentPlanInfoEntity.setRepaymentPrincipal(new BigDecimal(m.get("repaymentPrincipal").toString()));
				repaymentPlanInfoEntity.setRepaymentInterest(new BigDecimal(m.get("repaymentInterest").toString()));
				repaymentPlanInfoEntity.setRepaymentTotalAmount(ArithUtil.add(repaymentPlanInfoEntity.getRepaymentPrincipal(), repaymentPlanInfoEntity.getRepaymentInterest()));
				repaymentPlanInfoEntity.setRemainderPrincipal(BigDecimal.ZERO);
				repaymentPlanInfoEntity.setAdvanceCleanupTotalAmount(new BigDecimal(m.get("advanceCleanupTotalAmount").toString()));
				repaymentPlanInfoEntity.setRepaymentStatus(Constant.REPAYMENT_STATUS_WAIT);
				repaymentPlanInfoEntity.setFactRepaymentDate(null);
				repaymentPlanInfoEntity.setPenaltyStartDate(null);
				repaymentPlanInfoEntity.setAccountManageExpense(BigDecimal.ZERO);
				repaymentPlanInfoEntity.setLoanEntity(loanInfoEntity);
				repaymentPlanInfoEntity.setIsAmountFrozen(Constant.IS_AMOUNT_FROZEN_NO);
				repaymentPlanInfoEntity.setIsRiskamountRepay(Constant.IS_RISKAMOUNT_REPAY_NO);
				repaymentPlanInfoEntity.setPenaltyAmount(BigDecimal.ZERO);
				repaymentPlanInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
				planList.add(repaymentPlanInfoEntity);
			}
		}
		
		// 计算剩余本金和违约金
		// 1) 先排序
		Collections.sort(planList, new Comparator<RepaymentPlanInfoEntity>(){
			@Override
			public int compare(RepaymentPlanInfoEntity o1,
					RepaymentPlanInfoEntity o2) {
				
				return o1.getCurrentTerm() - o2.getCurrentTerm();
			}
		});
		// 2) 计算剩余本金和违约金
		BigDecimal remainderPrincipal = loanInfoEntity.getLoanAmount();
		for(int i = 0; i < planList.size(); i ++) {
			remainderPrincipal = ArithUtil.sub(remainderPrincipal, planList.get(i).getRepaymentPrincipal());
			planList.get(i).setRemainderPrincipal(remainderPrincipal);
			planList.get(i).setPenaltyAmount(ArithUtil.sub(planList.get(i).getAdvanceCleanupTotalAmount(), remainderPrincipal));
		}
		repaymentPlanInfoRepository.save(planList);

		//如果是雪橙就保存还款计划副本生成还款计划对应的副本>>>@anthor易长安,有问题来找我啊
//		if(Constant.DEBT_SOURCE_XCJF.equals((String)params.get("companyName"))) {
//			List<RepaymentPlanCopeEntity> planListCope = Lists.newArrayList();
//			for (RepaymentPlanInfoEntity repin : planList) {
//				RepaymentPlanCopeEntity repaymentPlanCopeEntity = new RepaymentPlanCopeEntity();
//				String loanCode = repin.getLoanEntity().getLoanCode();
//				repaymentPlanCopeEntity.setAccountManageExpense(repin.getAccountManageExpense());
//				repaymentPlanCopeEntity.setRepaymentTotalAmount(repin.getRepaymentTotalAmount());
//				repaymentPlanCopeEntity.setAdvanceCleanupTotalAmount(repin.getAdvanceCleanupTotalAmount());
//				repaymentPlanCopeEntity.setCurrentTerm(repin.getCurrentTerm());
//				repaymentPlanCopeEntity.setExpectRepaymentDate(repin.getExpectRepaymentDate());
//				repaymentPlanCopeEntity.setTermAlreadyRepayAmount(repin.getTermAlreadyRepayAmount());
//				repaymentPlanCopeEntity.setRepaymentPrincipal(repin.getRepaymentPrincipal());
//				repaymentPlanCopeEntity.setRepaymentInterest(repin.getRepaymentInterest());
//				repaymentPlanCopeEntity.setRepaymentTotalAmount(repin.getRepaymentTotalAmount());
//				repaymentPlanCopeEntity.setRemainderPrincipal(BigDecimal.ZERO);
//				repaymentPlanCopeEntity.setRepaymentStatus(Constant.REPAYMENT_STATUS_WAIT);
//				repaymentPlanCopeEntity.setFactRepaymentDate(null);
//				repaymentPlanCopeEntity.setPenaltyStartDate(null);
//				repaymentPlanCopeEntity.setAccountManageExpense(BigDecimal.ZERO);
//				repaymentPlanCopeEntity.setLoanEntity(repin.getLoanEntity());
//				repaymentPlanCopeEntity.setIsAmountFrozen(Constant.IS_AMOUNT_FROZEN_NO);
//				repaymentPlanCopeEntity.setIsRiskamountRepay(Constant.IS_RISKAMOUNT_REPAY_NO);
//				repaymentPlanCopeEntity.setPenaltyAmount(BigDecimal.ZERO);
//				repaymentPlanCopeEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
//				repaymentPlanCopeEntity.setRemainderPrincipal(repin.getRemainderPrincipal());
//				repaymentPlanCopeEntity.setPenaltyAmount(repin.getPenaltyAmount());
//				repaymentPlanCopeEntity.setLoanCode(loanCode);
//				repaymentPlanCopeEntity.setIsLimit(WithHoldingConstant.REPAYMENT_IS_TIME_LIMIT_NO);
//				planListCope.add(repaymentPlanCopeEntity);
//			}
//			repaymentPlanCopeRepository.save(planListCope);
//		}
		return new ResultVo(true, "保存项目成");
	}


	@Override
	public ResultVo queryProject(Map<String, Object> params) throws SLException {
		
		Map<String, Object> resultMap = Maps.newHashMap();
		
		// 验证借款编号是否存在
		LoanInfoEntity loanInfoEntity = null;
		if(!StringUtils.isEmpty((String)params.get("loanNo"))) {
			loanInfoEntity = loanInfoRepository.findByLoanCode((String)params.get("loanNo"));
		}
		else if(!StringUtils.isEmpty((String)params.get("loanId"))){
			loanInfoEntity = loanInfoRepository.findOne((String)params.get("loanId"));
		}
		if(loanInfoEntity == null) {
			return new ResultVo(false, "未查询到相关借款信息", OpenSerivceCode.ERR_NOT_EXISTS_PROJECT);
		}
		resultMap.put("loanNo", loanInfoEntity.getLoanCode());
		resultMap.put("companyName", loanInfoEntity.getCompanyName());
		resultMap.put("loanStatus", loanInfoEntity.getLoanStatus());
		
		
		if(Constant.LOAN_STATUS_14.equals(loanInfoEntity.getLoanStatus())) { // 拒绝
			AuditInfoEntity auditInfoEntity = auditInfoRespository.findFirstByRelatePrimaryOrderByCreateDateDesc(loanInfoEntity.getId(), Constant.OPERATION_TYPE_60);
			Map<String, Object> reason = Maps.newHashMap();
			reason.put("reason", auditInfoEntity.getMemo());
			resultMap.put("result", reason);
		}
		else if(Constant.LOAN_STATUS_06.equals(loanInfoEntity.getLoanStatus())){ // 满标复核取投资信息
			List<Map<String, Object>> investList = investInfoRepositoryCustom.queryInvestListByLoanId(loanInfoEntity.getId());
			Map<String, Object> reason = Maps.newHashMap();
			reason.put("investorList", investList);
			resultMap.put("result", reason);
		}
		// 通过openservice查询的
		if (params.containsKey("openFlag")) {
			// 意真查询项目状态
			if (Constant.DEBT_SOURCE_YZJR.equals(loanInfoEntity.getCompanyName())) {
				if (Constant.LOAN_STATUS_08.equals(loanInfoEntity.getLoanStatus())) {
					// 如果项目状态是"正常",则返回给意真"满标复核"并且返回放款时间
					resultMap.put("loanStatus", Constant.LOAN_STATUS_06);
					resultMap.put("grantMoneyDate", loanInfoEntity.getGrantDate());
				} else if (Constant.LOAN_STATUS_06.equals(loanInfoEntity.getLoanStatus())
						&& Constant.GRANT_STATUS_02.equals(loanInfoEntity.getGrantStatus())) {
					// 如果项目状态是"满标复核",但是放款状态是"放款成功",则返回给意真"满标复核"并且返回放款时间
					resultMap.put("loanStatus", Constant.LOAN_STATUS_06);
					resultMap.put("grantMoneyDate", loanInfoEntity.getGrantDate());
				}  else if (Constant.LOAN_STATUS_06.equals(loanInfoEntity.getLoanStatus())
						&& !Constant.GRANT_STATUS_02.equals(loanInfoEntity.getGrantStatus())) {
					// 如果项目状态是"满标复核",但是放款状态不是"放款成功",则返回给意真"募集中"
					resultMap.put("loanStatus", Constant.LOAN_STATUS_05);
				}
				// 满标复核返回投资人列表
				if (Constant.LOAN_STATUS_06.equals(resultMap.get("loanStatus"))) {
					if(!resultMap.containsKey("result")) {
						List<Map<String, Object>> investList = investInfoRepositoryCustom.queryInvestListByLoanId(loanInfoEntity.getId());
						Map<String, Object> reason = Maps.newHashMap();
						reason.put("investorList", investList);
						resultMap.put("result", reason);
					}
				}
			}
		}
		
		
		return new ResultVo(true, "查询项目成功", resultMap);
	}

	@Override
	public ResultVo operateProject(Map<String, Object> params) throws SLException {
		String operateType = (String)params.get("operateType");
		// 验证借款编号是否存在
		LoanInfoEntity loanInfoEntity = loanInfoRepository.findByLoanCode((String)params.get("loanNo"));
		if(loanInfoEntity == null) {
			return new ResultVo(false, "未查询到相关借款信息", OpenSerivceCode.ERR_NOT_EXISTS_PROJECT);
		}
		if(Constant.NOTIFY_TYPE_TRADE_TYPE_BIDDERS.equals(operateType)) { // 流标
			Map<String, Object> param = Maps.newHashMap();
			param.put("loanId", loanInfoEntity.getId());
			param.put("userId", Constant.SYSTEM_USER_BACK);
			// 操作流标
			return unRelease(param);
		} else if(Constant.NOTIFY_TYPE_TRADE_TYPE_LENDING.equals(operateType)) { // 放款
			Map<String, Object> param = Maps.newHashMap();
			param.put("loanId", loanInfoEntity.getId());
			param.put("userId", Constant.SYSTEM_USER_BACK);
			param.put("operateType", operateType);
			
			
			//授权申请
	
			
			// 操作放款
			return notifyGrant(param);
		} else if(Constant.NOTIFY_TYPE_TRADE_TYPE_CANCEL.equals(operateType)) { // 放款
			Map<String, Object> param = Maps.newHashMap();
			param.put("loanId", loanInfoEntity.getId());
			param.put("userId", Constant.SYSTEM_USER_BACK);
			// 操作撤销
			return cancel(param);
		}
		return new ResultVo(false);
	}
	
	
	
	@Autowired
	private LoanInfoService loanInfoService;
	@Autowired
	private AccreditRequestService accreditRequestService;
	@Autowired
	private RepaymentPlanCopeService repaymentPlanCopeService;
	@Autowired
	private TradeLogInfoRepository tradeLogInfoRepository;
	@Autowired
	private LoanAgreeService loanAgreeService;
	@Autowired
	private WithholdAccountRepository withholdAccountRepository;
	@Autowired
	private ThridPartyPayRequestRepository thridPartyPayRequestRepository;
	@Autowired
	@Qualifier("thirdPartyPayRestClientService")
	private RestOperations slRestClient;
	@Autowired
	private ThirdPartyPayRequestService thirdPartyPayRequestService;
	
	/**
	 * 授权申请------廖兵兵
	 * @param repaymentDayList
	 * @throws SLException
	 */
	@SuppressWarnings("all")
	private void accreditRequest(String repaymentDayList,String loanNo,AccreditRequestEntity entity)
			throws SLException {
		//授权申请
		
		//1.为还款计划请求授权申请（请求tpp）
		//2.保存对应还款计划的授权信息以及结果
		//=====从财富系统获取请求tpp的参数====还款计划编号:loanNo、还款计划:repaymentList、签约协议号:no_agree
		LoanInfoEntity loanInfoEntity = loanInfoService.findByLoanCode(loanNo);
		String companyName = loanInfoEntity.getCompanyName();//公司名称
		String loanId = loanInfoEntity.getId();//借款id
		String custId = loanInfoEntity.getLoanCustInfoEntity().getId();//借款人id
		LoanAgreeEntity loanAgreeEntity = loanAgreeService.findByCustId(custId);//根据custId查询noAgree
		LoanCustInfoEntity custInfoEntity = loanCustInfoRepository.findById(custId);
		String custCode = custInfoEntity.getCustCode();
		String noAgree = loanAgreeEntity.getNoAgree();
		
		Map reqMap = Maps.newConcurrentMap(); //请求参数封装
		reqMap.put("batchCode", numberService.generateTradeBatchNumber());//批次号
		reqMap.put("buzName", "SLCF");//业务系统
		ConcurrentMap<Object, Object> reqData = Maps.newConcurrentMap();
		reqData.put("noAgree", noAgree);//签约协议号
		reqData.put("platformUserNo", custInfoEntity.getCredentialsCode());//商户用户编号 
		reqData.put("repaymentNo", loanNo);//还款计划编号
		reqData.put("repaymentPlan", repaymentDayList);//还款计划
		reqData.put("requestNo", "SLCF-"+ShareUtil.getCurrentTime());//交易流水号
		ConcurrentMap<Object, Object> smsParam = Maps.newConcurrentMap();
		WithholdAccountEntity withholdAccountEntity = withholdAccountRepository.findByCompanyName(companyName);//根据公司名查询短信参数
		smsParam.put("contract_type", withholdAccountEntity.getContractType());
		smsParam.put("contact_way", withholdAccountEntity.getContactWay());
		reqData.put("smsParam", JSONObject.toJSONString(smsParam));//短信参数
		reqMap.put("reqData", reqData);
		reqMap.put("requestTime", ShareUtil.getCurrentTime());//时间戳
		reqMap.put("serviceName", "AUTH_APPLY");//服务名称
		reqMap.put("userDevice", "PC");//用户终端设备类型（网关页面）
		reqMap.put("platform", "lianpay");//用户终端设备类型（网关页面）
		
		
		// 记录第三方请求报文
		ThridPartyPayRequestEntity partyPayRequestEntity = new ThridPartyPayRequestEntity();
		partyPayRequestEntity.setId(SharedUtil.getUniqueString());
		partyPayRequestEntity.setRequestUrl(Constant.XCJF_SQSQ_TPP_URL);
		partyPayRequestEntity.setRequestMethod("POST");
		partyPayRequestEntity.setRequestHeaders("{Accept=[application/json], Content-Type=[application/json;charset=UTF-8]}");
		partyPayRequestEntity.setRequestBatchNumber(numberService.generateTradeBatchNumber());
		partyPayRequestEntity.setRequestTime(DateUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
		partyPayRequestEntity.setRequestBody(Json.ObjectMapper.writeValue(reqMap));
		partyPayRequestEntity.setCreateDate(new Date());
		partyPayRequestEntity.setMemo("授权申请");
		
		//记录交易请求报文
		String innerTradeCode = numberService.generateOpenServiceTradeNumber();
		TradeLogInfoEntity tradeLogInfoEntity = new TradeLogInfoEntity();
		tradeLogInfoEntity.setRelatePrimary((String)reqMap.get("loanId"));
		tradeLogInfoEntity.setRequestTime(DateUtils.getCurrentDate("yyyyMMddHHmmss"));
		tradeLogInfoEntity.setTradeCode(reqMap.get("batchCode").toString());//请求批次号
		tradeLogInfoEntity.setInnerTradeCode(innerTradeCode);
		tradeLogInfoEntity.setRequestMessage(Json.ObjectMapper.writeValue(reqMap));
		tradeLogInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
		tradeLogInfoEntity.setMemo("授权申请");
		tradeLogInfoEntity = tradeLogInfoRepository.save(tradeLogInfoEntity);
		HttpHeaders headers = new HttpHeaders();
		MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
		headers.setContentType(type);
		headers.add("Accept", MediaType.APPLICATION_JSON.toString());
      
		HttpEntity<Map<String, Object>> formEntity = new HttpEntity<Map<String, Object>>(reqMap, headers);
		String url = Constant.XCJF_SQSQ_TPP_URL;//雪橙金服授权申请调用tpp接口url
		
		Map respMap = null;
		try {
			 respMap = slRestClient.postForObject(url, formEntity, Map.class);
			 log.info("授权响应："+respMap);
		} catch (RestClientException e) {
			log.error("调tpp授权申请失败！", e == null ? "调用失败" : e.getMessage());
			tradeLogInfoEntity.setResponseMessage(e == null ? "调用失败" : e.getMessage());
			partyPayRequestEntity.setResponseStatusCode("500");
			partyPayRequestEntity.setResponseStatusText("未知异常");
			partyPayRequestEntity.setLastUpdateDate(new Date());
			partyPayRequestEntity.setResponseTime(DateUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
		}
		
		if(respMap!= null){
			Map<String, Object> respData = (Map<String, Object>) respMap.get("respData");
			String code = respData.get("code") == null ? null : respData.get("code").toString();
			if(code != null && !code.equals("")){
				if(Constant.ACCREDIT_REQUEST_RESP_CODE.equals(code)){//授权成功 0000
					entity.setStatus("已授权");//授权成功
					log.info("还款计划编号："+loanNo+",授权申请成功");
					partyPayRequestEntity.setResponseStatusCode("200");
					partyPayRequestEntity.setResponseStatusText("OK");
					partyPayRequestEntity.setLastUpdateDate(new Date());
					partyPayRequestEntity.setResponseTime(DateUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
					partyPayRequestEntity.setResponseBody(Json.ObjectMapper.writeValue(respMap));
				}
			}else{//失败
				partyPayRequestEntity.setResponseStatusCode(respData.get("errorCode").toString());
				partyPayRequestEntity.setResponseStatusText(respData.get("errorMessage").toString());
				partyPayRequestEntity.setLastUpdateDate(new Date());
				partyPayRequestEntity.setResponseTime(DateUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss"));
				partyPayRequestEntity.setResponseBody(Json.ObjectMapper.writeValue(respMap));
				log.error("还款计划编号：{},授权申请失败errorCode:{},errorMessage:{}",loanNo,respData.get("errorCode") == null ? "999" :loanNo,respData.get("errorCode").toString(),loanNo,respData.get("errorMessage") == null ? "未知异常" : respData.get("errorMessage").toString());
			}
			tradeLogInfoEntity.setResponseMessage(Json.ObjectMapper.writeValue(respMap));//记录响应参数
		}
		thirdPartyPayRequestService.saveThirdPartyPayRequest(partyPayRequestEntity);
		
	}
	
	
	/**
	 * 定时任务（授权申请：未授权的）
	 */
	@Override
	public void AccreditRequestJob(Map<String, Object> params)
			throws SLException {
		//查询未授权的记录
		String status = "未授权";
		List<AccreditRequestEntity> list = accreditRequestService.findByStatus(status);
		
		if(list == null || list.isEmpty()){
			log.info("==授权申请定时任务：=====未授权的记录数为0======");
			return;
		}
		log.info("==授权申请定时任务：有"+list.size()+"条记录需要授权申请");
		for (AccreditRequestEntity accreditRequestEntity : list) {
			String loanNo = accreditRequestEntity.getLoanNo();
			String rempaymentPlan = accreditRequestEntity.getRempaymentPlan();
			accreditRequest(rempaymentPlan, loanNo, accreditRequestEntity);
			log.info("还款计划编号："+loanNo+",授权申请成功");
		}
	}

	@Override
	public ResultVo notifyGrant(Map<String, Object> params) throws SLException {
		String loanId = (String)params.get("loanId");
		String userId = (String)params.get("userId");
		Date grantDate = new Date(); // 放款时间取当前时间
		
		// 1) 状态必须为满标复核
		LoanInfoEntity loanInfoEntity = loanInfoRepository.findOne(loanId);
		if(loanInfoEntity == null) {
			throw new SLException("项目不存在");
		}
		if(!Constant.LOAN_STATUS_06.equals(loanInfoEntity.getLoanStatus())) {
			throw new SLException("项目非满标复核状态");
		}
		
		String oldGrantStatus = loanInfoEntity.getGrantStatus();
		
		// 非待放款和放款失败，不允许放款
		if(!Constant.GRANT_STATUS_01.equals(oldGrantStatus)
				&& ! Constant.GRANT_STATUS_03.equals(oldGrantStatus)) {
			throw new SLException("项目已经放款成功或者正在放款中，不允许再次放款");
		}
		
		// 判断项目余额是否足额
		SubAccountInfoEntity loanerSubAccount = subAccountInfoRepository.findByRelatePrimary(loanInfoEntity.getId());
		AccountInfoEntity loanerAccount = accountInfoRepository.findByCustId(loanInfoEntity.getCustId());
		if(loanerSubAccount == null || loanerAccount == null) {
			throw new SLException("借款人账户不存在");
		}
		
		// 判断分账户余额是否足额，如不足额则判断主账户是否足额。
		// 分账户足额，将分账户资金打到主账户，进行放款。
		// 分账户不足，主账户足额的情况为第一次放款时已将分账户的金额转账主账户，第二次放款时出现分账户不足，而主账户足额。
		// 分账户不足，主账户不足的情况为第一次放款时已将分账户的金额转账主账户，主账户的资金用于还款了，或者被冻结中（放款异常）。
		if(loanerSubAccount.getAccountAvailableValue().compareTo(loanInfoEntity.getLoanAmount()) < 0) {
			if(loanerAccount.getAccountAvailableAmount().compareTo(ArithUtil.sub(loanInfoEntity.getLoanAmount(), loanInfoEntity.getPlatServiceAmount())) < 0) {
				throw new SLException("借款人账户余额不足");
			}
		}
		else {
			// 借款人分账——>主账户
			internalLoanProjectService.moveToMainAccount(loanInfoEntity, userId);
		}

		// 状态改为放款中(立即执行SQL防重复提交)
		Map<String, Object> requestParams = Maps.newConcurrentMap();
		requestParams.put("grantStatus", Constant.GRANT_STATUS_04);
		requestParams.put("lastUpdateUser", userId);
		requestParams.put("lastUpdateDate", grantDate);
		requestParams.put("loanId", loanId);
		int rows = loanInfoRepositoryCustom.updateLoanGrantStatus(requestParams);
		if(rows == 0) {
			throw new SLException("项目已经在放款中，请勿重复提交");
		}

		// 2) 通知商务
		Map<String, Object> notifyParams = Maps.newConcurrentMap();
		notifyParams.put("loanId", loanId);
		notifyParams.put("userId", userId);
		notifyParams.put("custId", loanInfoEntity.getCustId());
		notifyParams.put("tradeAmount", loanInfoEntity.getLoanAmount());
		notifyParams.put("platServiceAmount", loanInfoEntity.getPlatServiceAmount() == null ? BigDecimal.ZERO : loanInfoEntity.getPlatServiceAmount());
		notifyParams.put("manageExpenseDealType", loanInfoEntity.getManageExpenseDealType());
		notifyParams.put("grantType", loanInfoEntity.getGrantType());
		if (params.containsKey("operateType")) {
			notifyParams.put("operateType", (String)params.get("operateType"));
		}
		notifyParams.put("loanCust", loanInfoEntity.getLoanCustInfoEntity());
		
		LoanRebateInfoEntity loanRebateInfoEntity = loanRebateInfoRepository.findByRepaymentMethodAndLoanTermAndProductTypeAndLoanUnit(loanInfoEntity.getRepaymentMethod(), Integer.parseInt(loanInfoEntity.getLoanTerm().toString()), loanInfoEntity.getLoanType(), loanInfoEntity.getLoanUnit());
		if(Constant.LOAN_FLAG_01.equals(loanRebateInfoEntity.getFlag())) {
			return grantApplyBussiness(notifyParams);
		}
		else if(Constant.LOAN_FLAG_02.equals(loanRebateInfoEntity.getFlag())) {
			return grantApplyCompany(notifyParams);
		}
		
		return new ResultVo(true, "放款成功");
	}
	
	@SuppressWarnings("unchecked")
	public ResultVo grantConfirm(Map<String, Object> params) throws SLException {
		
		String loanId = (String)params.get("loanId");
		String userId = (String) params.get("userId");
		String grantStatus = (String)params.get("grantStatus");
		
		// 查询借款信息
		LoanInfoEntity loanInfoEntity = loanInfoRepository.findOne(loanId);
		if(loanInfoEntity == null) {
			return new ResultVo(false, "未查询到相关借款信息", OpenSerivceCode.ERR_NOT_EXISTS_PROJECT);
		}
		Map<String, Object> releaseParam = Maps.newHashMap();

		// 意真金融|雪橙金服 并且需要通知的项目，发送放款通知
		if ((Constant.DEBT_SOURCE_YZJR.equals(loanInfoEntity.getCompanyName())
				|| Constant.DEBT_SOURCE_XCJF.equals(loanInfoEntity.getCompanyName())
				|| Constant.DEBT_SOURCE_JLJR.equals(loanInfoEntity.getCompanyName())
				|| Constant.DEBT_SOURCE_CFXJD.equals(loanInfoEntity.getCompanyName()))
				&& Constant.PUSH_FLAG_YES.equals(loanInfoEntity.getPushFlag())) {
			// 获取借款项目信息封装放款通知参数
			ResultVo resultVo = queryProject(params);
			if(!ResultVo.isSuccess(resultVo)) {
				throw new SLException((String)resultVo.getValue("message"));
			}
			Map<String, Object> data = (Map<String, Object>)resultVo.getValue("data");
			Map<String, Object> requestMap = Maps.newHashMap();
			requestMap.put("loanId", loanId);
			requestMap.put("loanNo", data.get("loanNo"));
			requestMap.put("requestTime", DateUtils.getCurrentDate("yyyyMMddHHmmss"));
			requestMap.put("companyName", data.get("companyName"));
			if (Constant.DEBT_SOURCE_XCJF.equals(data.get("companyName"))) {
				requestMap.put("loanStatus", "放款成功");
			} else {
				requestMap.put("loanStatus", data.get("loanStatus"));
			}
			requestMap.put("grantMoneyDate", new Date());
			requestMap.put("accountManageAmount", BigDecimal.ZERO);
			if(data.containsKey("result")) {
				Map<String, Object> result = (Map<String, Object>)data.get("result");
				requestMap.put("result", result);
			}
			if(Constant.GRANT_STATUS_02.equals(grantStatus)) { // 放款成功才通知
				if (Constant.DEBT_SOURCE_YZJR.equals(data.get("companyName"))) {
					try {
						// 意真只需要发送放款通知不关心通知是否成功
						resultVo = openThirdService.nofity(requestMap);
						if(!ResultVo.isSuccess(resultVo)) {
							log.error("意真项目{}放款通知失败！失败原因:{}", loanId, (String)resultVo.getValue("message"));
						}
					} catch(Exception e) {
						log.error("意真项目{}放款通知失败！失败原因:{}", loanId, e.getMessage());
					}
				} else {
					// 发送放款通知,如果通知失败则放款失败
					resultVo = openThirdService.nofity(requestMap);
					if(!ResultVo.isSuccess(resultVo)) {
						throw new SLException((String)resultVo.getValue("message"));
					}
					// 获取通知资产端后的放款状态
					data = (Map<String, Object>)resultVo.getValue("data");
					releaseParam.put("grantStatus", (String)data.get("grantStatus"));
					if(data.containsKey("repaymentList")){
						releaseParam.put("repaymentList", data.get("repaymentList"));
					}
				}
			}
		}
		
		// 生效项目
		releaseParam.put("loanId", loanId);
		releaseParam.put("userId", userId);
		if(!releaseParam.containsKey("repaymentList")){
			releaseParam.put("repaymentList", params.get("repaymentList"));
		}
		releaseParam.put("needUpdateAccount", params.get("needUpdateAccount"));
		if (!releaseParam.containsKey("grantStatus")) {
			releaseParam.put("grantStatus", grantStatus);
		}
		releaseLoan(releaseParam);
		
		
		
		//授权申请
		//授权申请，上送还款计划
//		ResultVo resultVo1 = queryProject(params);
//		if(!ResultVo.isSuccess(resultVo1)) {
//			throw new SLException((String)resultVo1.getValue("message"));
//		}
//		Map<String, Object> data1 = (Map<String, Object>)resultVo1.getValue("data");
//		String loanNo = data1.get("loanNo").toString();
//		String companyName = data1.get("companyName").toString();
//		if(Constant.DEBT_SOURCE_XCJF.equals(companyName)){//只有雪橙金服才授权申请
//		    
//			LoanInfoEntity loanInfoEntity2 = loanInfoService.findByLoanCode(loanNo);
//			
//			
//			if(loanInfoEntity2==null){
//				throw new SLException("没有此记录");
//			}
//			//转换还款计划参数格式（tpp）
//			List<RepaymentPlanCopeEntity> RepaymentPlanCopeEntitys = repaymentPlanCopeService.queryByLoanCode(loanNo);
//			List<Map<String, Object>> repaymentplan = Lists.newArrayList();
//			for (RepaymentPlanCopeEntity repaymentPlanCopeEntity : RepaymentPlanCopeEntitys) {
//				Map<String, Object> repayment = Maps.newConcurrentMap();
//				 SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
//				try {
//					repayment.put("date", DateUtils.formatDate(simpleDateFormat.parse(repaymentPlanCopeEntity.getExpectRepaymentDate().toString()), "yyyy-MM-dd"));
//				} catch (ParseException e) {
//					e.printStackTrace();
//				}
//				repayment.put("amount", repaymentPlanCopeEntity.getRepaymentTotalAmount().toString());
//				repaymentplan.add(repayment);
//			}
//			ConcurrentMap<Object, Object> map = Maps.newConcurrentMap();
//			map.put("repaymentPlan", repaymentplan);
//			String loanId2 = loanInfoEntity2.getId();//借款id
//			//保存授权申请实体
//			AccreditRequestEntity entity = new AccreditRequestEntity();//授权请求实体
//			entity.setId(SharedUtil.getUniqueString());
//			entity.setStatus("未授权");//还未授权
//			entity.setLoanId(loanId2);//借款id
//			entity.setLoanNo((String)params.get("loanNo"));//还款计划编号
//			entity.setRempaymentPlan(JsonUtil.jsonToString(map));
//			entity = accreditRequestService.save(entity);
//			accreditRequest(JsonUtil.jsonToString(map),loanNo,entity);//授权申请
//		}
//		

		return new ResultVo(true);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ResultVo grantApplyCompany(Map<String, Object> params) throws SLException {
		
		String loanId = (String)params.get("loanId");
		String custId = (String)params.get("custId");
		String userId = (String) params.get("userId");
		String auditMemo = (String) params.get("auditMemo");
		BigDecimal tradeAmount = (BigDecimal)params.get("tradeAmount");
		String manageExpenseDealType = (String) params.get("manageExpenseDealType");
		String grantType = (String) params.get("grantType");
		BigDecimal platServiceAmount = (BigDecimal)params.get("platServiceAmount");
		
		if(Constant.MANAGE_EXPENSE_DEAL_TYPE_02.equals(manageExpenseDealType)) { // 需内扣服务费
			tradeAmount = ArithUtil.sub(tradeAmount, platServiceAmount);
			internalLoanProjectService.subMainAccount(loanId, userId);
		}
						
		// 4) 代付
		Map<String, Object> grantRequest = Maps.newHashMap();
		grantRequest.put("custId", custId);
		grantRequest.put("userId", userId);
		grantRequest.put("tradeAmount", tradeAmount);
		grantRequest.put("relateType", Constant.TABLE_BAO_T_LOAN_INFO);
		grantRequest.put("relatePrimary", loanId);
		grantRequest.put("auditMemo", StringUtils.isEmpty(auditMemo) ? "" : auditMemo);		
		// 为防止同一个客户在多个资产端借款，而每个资产端银行卡卡号不一样，此处需将该客户的资金放至对应银行卡
		LoanCustInfoEntity loanCustInfoEntity = (LoanCustInfoEntity)params.get("loanCust");
		if(loanCustInfoEntity != null) {
			grantRequest.put("cardNo", loanCustInfoEntity.getCardNo());
		}
		ResultVo grantResponse = accountService.grantAccount(grantRequest);
		if(!ResultVo.isSuccess(grantResponse)) {
			log.error("项目{}代付失败！失败原因:{}", loanId, grantResponse.getValue("message"));
			String data = (String)grantResponse.getValue("data");
			if(!StringUtils.isEmpty(data) && "代付失败".equals(data)) { // 确定代付失败，修改项目打款状态				
				Map<String, Object> requestParams = Maps.newHashMap();
				requestParams.put("grantStatus", Constant.GRANT_STATUS_03);
				requestParams.put("loanId", loanId);
				requestParams.put("userId", userId);
				internalLoanProjectService.grantLoan(requestParams);
			}
			return grantResponse;
		}
		
		// 5) 放款即生效
		if(Constant.GRANT_TYPE_01.equals(grantType)) {

			Map<String, Object> requestParams = Maps.newHashMap();
			requestParams.put("grantDate", new Date());
			requestParams.put("grantStatus", Constant.GRANT_STATUS_02);
			requestParams.put("loanId", loanId);
			requestParams.put("userId", userId);	
			requestParams.put("needUpdateAccount", false);
			if (params.containsKey("operateType")) {
				requestParams.put("operateType", (String)params.get("operateType"));
			}
			// 若放款成功则给还款计划生成还款日期
			ResultVo result = generateRepaymentPlanDay(requestParams);
			Map<String, Object> data = (Map<String, Object>)result.getValue("data");
			requestParams.put("repaymentList", data.get("repaymentList"));
			
			// 放款确认
			grantConfirm(requestParams);
		}
		return new ResultVo(true, "放款提交成功");
	}
	
	@SuppressWarnings("unchecked")
	public ResultVo grantApplyBussiness(Map<String, Object> params) throws SLException {
		
		String loanId = (String)params.get("loanId");
		String userId = (String) params.get("userId");
		
		try {
			ResultVo resultVo = queryProject(params);
			if(!ResultVo.isSuccess(resultVo)) {
				throw new SLException((String)resultVo.getValue("message"));
			}
			Map<String, Object> data = (Map<String, Object>)resultVo.getValue("data");
			Map<String, Object> requestMap = Maps.newHashMap();
			requestMap.put("loanId", loanId);
			requestMap.put("loanNo", data.get("loanNo"));
			requestMap.put("requestTime", DateUtils.getCurrentDate("yyyyMMddHHmmss"));
			requestMap.put("companyName", data.get("companyName"));
			requestMap.put("loanStatus", data.get("loanStatus"));
			requestMap.put("grantMoneyDate", new Date());
			requestMap.put("accountManageAmount", BigDecimal.ZERO);
			if(data.containsKey("result")) {
				Map<String, Object> result = (Map<String, Object>)data.get("result");				
				requestMap.put("result", result);
			}
			resultVo = openThirdService.nofity(requestMap);
			if(!ResultVo.isSuccess(resultVo)) {
				throw new SLException((String)resultVo.getValue("message"));
			}
			
			data = (Map<String, Object>)resultVo.getValue("data");
			String grantStatus = (String)data.get("grantStatus");
			Map<String, Object> requestParams = Maps.newHashMap();
			requestParams.put("grantStatus", grantStatus);
			requestParams.put("loanId", loanId);
			requestParams.put("userId", userId);
			requestParams.put("repaymentList", data.get("repaymentList"));
			requestParams.put("needUpdateAccount", true);

			grantConfirm(requestParams);
			
		} catch (Exception e) {
			log.error("放款失败！" + e.getMessage());
			return new ResultVo(false, e.getMessage());
		}
		
		return new ResultVo(true);
	}

	@Override
	public ResultVo generateRepaymentPlanDay(Map<String, Object> params)
			throws SLException {
		String loanId = (String)params.get("loanId");
		Date grantDate = (Date)params.get("grantDate"); 
		
		LoanInfoEntity loanInfoEntity = loanInfoRepository.findOne(loanId);
		if(loanInfoEntity == null) {
			throw new SLException("项目不存在");
		}
		List<RepaymentPlanInfoEntity> repaymentPlanList = repaymentPlanInfoRepository.findByLoanId(loanId);
		List<Map<String, Object>> repaymentList = Lists.newArrayList();
		switch(loanInfoEntity.getRepaymentMethod()) {
		case Constant.REPAYMENT_METHOD_01: // 等额本息
			for(RepaymentPlanInfoEntity r : repaymentPlanList) {
				Map<String, Object> repayment = Maps.newConcurrentMap();
				repayment.put("currentTerm", r.getCurrentTerm());
				repayment.put("expectRepaymentDate", DateUtils.formatDate(DateUtils.getAfterMonth(grantDate, r.getCurrentTerm()), "yyyyMMdd"));
				repaymentList.add(repayment);
			}
			break;
		case Constant.REPAYMENT_METHOD_03: // 到期还本付息
			for(RepaymentPlanInfoEntity r : repaymentPlanList) {
				Map<String, Object> repayment = Maps.newConcurrentMap();
				repayment.put("currentTerm", r.getCurrentTerm());
//				DateUtils.formatDate(Constant.LOAN_UNIT_DAY.equals(termUnit) ? DateUtils.getAfterDay(firstRepaymentDay, typeTerm) : DateUtils.getAfterMonth(firstRepaymentDay, typeTerm), "yyyyMMdd");
//				DateUtils.formatDate(DateUtils.getAfterMonth(grantDate, Integer.parseInt(loanInfoEntity.getLoanTerm().toString())), "yyyyMMdd")
				repayment.put("expectRepaymentDate", DateUtils.formatDate(Constant.LOAN_UNIT_DAY.equals(loanInfoEntity.getLoanUnit()) ? DateUtils.getAfterDay(grantDate, Integer.parseInt(loanInfoEntity.getLoanTerm().toString())) : DateUtils.getAfterMonth(grantDate, Integer.parseInt(loanInfoEntity.getLoanTerm().toString())), "yyyyMMdd"));
				repaymentList.add(repayment);
			}
			break;
		}
		
		Map<String, Object> result = Maps.newConcurrentMap();
		result.put("repaymentList", repaymentList);
		return new ResultVo(true, "生成还款日成功", result);
	}
	
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo buyCredit(Map<String, Object> params) throws SLException {
		
		String transferApplyId = (String)params.get("transferApplyId");
		String custId = (String)params.get("custId");
		BigDecimal tradeAmount = new BigDecimal(params.get("tradeAmount").toString());// update SLCF-2823 现在传进来的是折价的本金
		Date now = new Date();
		String appSource = (String)params.get("appSource");
		appSource = Strings.nullToEmpty(StringUtils.isEmpty(appSource) ? Constant.INVEST_SOURCE_PC : appSource).toLowerCase();
		
		// 1) 还款时不允许投资
		JobRunListenerEntity jobRunListenerEntity = jobRunListenerRepository.findByJobName(Constant.JOB_NAME_LOAN_REPAYMENT);
		if(jobRunListenerEntity != null && Constant.EXEC_STATUS_RUNNING.equals(jobRunListenerEntity.getExecuteStatus())) {
			return new ResultVo(false, "系统正在执行还款任务，请稍后再试！");
		}
		
		// 2) 验证用户状态
		CustInfoEntity custInfoEntity = custInfoRepository.findOne(custId);
		if(custInfoEntity == null){
			throw new SLException("用户不存在！");
		}
		if(!Constant.ENABLE_STATUS_01.equals(custInfoEntity.getEnableStatus())){
			throw new SLException("账户为非正常状态，可能被冻结，请联系客服！");
		}
				
		// 3) 验证投资人账户是否足额
		AccountInfoEntity investorAccount = accountInfoRepository.findByCustId(custId); //客户账户表
		if(tradeAmount.compareTo(investorAccount.getAccountAvailableAmount()) > 0) {
			return new ResultVo(false, "账户可用余额不足， 请充值！");
		}
		
		// 4) 验证债权申请状态、结束时间
		LoanTransferApplyEntity loanTransferApplyEntity = loanTransferApplyRepository.findOne(transferApplyId);
		if(loanTransferApplyEntity == null) {
			return new ResultVo(false, "未找到债权申请");
		}
		if(Constant.LOAN_TRANSFER_CANCEL_STATUS_02.equals(loanTransferApplyEntity.getCancelStatus())) {
			return new ResultVo(false, "该笔债权转让申请已撤销");
		}
		if(Constant.LOAN_TRANSFER_APPLY_STATUS_02.equals(loanTransferApplyEntity.getApplyStatus())) {
			return new ResultVo(false, "该笔债权转让申请已转让成功");
		}
		if(now.compareTo(loanTransferApplyEntity.getTransferEndDate()) > 0) {
			return new ResultVo(false, "该笔债权转让申请已超出预定日期");
		}
		
		// 5) 用户不允许投资自己的债权转让
		if(custId.equals(loanTransferApplyEntity.getSenderCustId())) {
			return new ResultVo(false, "用户不允许投资自己的债权转让");
		}
		
		// 6) 判断借款状态
		WealthHoldInfoEntity wealthHoldInfoEntity = wealthHoldInfoRepository.findOne(loanTransferApplyEntity.getSenderHoldId());
		if(wealthHoldInfoEntity == null) {
			return new ResultVo(false, "未找到持有债权信息");
		}
		if(Constant.HOLD_STATUS_04.equals(wealthHoldInfoEntity.getHoldStatus())) {
			return new ResultVo(false, "该笔债权已转让");
		}
		LoanInfoEntity loanInfoEntity = loanInfoRepository.findOne(wealthHoldInfoEntity.getLoanId());
		if(loanInfoEntity == null || !Constant.LOAN_STATUS_08.equals(loanInfoEntity.getLoanStatus())){
			return new ResultVo(false, "项目非正常状态");
		}
		
		// update by http://192.16.150.101:8080/browse/SLCF-2823 at 2017-05-18
		// 7) 验证交易金额是否小于等于剩余可投金额	
		// 债权PV
		BigDecimal loanValue = creditRightValueRepository.findByLoanIdAndValueDate(wealthHoldInfoEntity.getLoanId(), DateUtils.formatDate(now, "yyyyMMdd"));
//		// 剩余可投金额=剩余可投持有比例×PV×折价系数
//		BigDecimal remainderAmount = ArithUtil.mul(ArithUtil.mul(loanValue, loanTransferApplyEntity.getRemainderTradeScale(), 2), loanTransferApplyEntity.getReducedScale(), 2);

		// 剩余本金
		BigDecimal remainPrincipal = loanInfoEntity.getLoanDetailInfoEntity().getCreditRemainderPrincipal();
		// 剩余本金（折价）
		BigDecimal remainPrincipalReduced = ArithUtil.mul(ArithUtil.mul(remainPrincipal, loanTransferApplyEntity.getDeviationScale()), loanTransferApplyEntity.getReducedScale());
		// 剩余可投金额 = 剩余本金 * 剩余可投持有比例
		BigDecimal remainderInvest = ArithUtil.mul(remainPrincipalReduced, loanTransferApplyEntity.getRemainderTradeScale()).setScale(8, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_DOWN);
		
		if(tradeAmount.compareTo(remainderInvest) > 0){
			return new ResultVo(false, String.format("项目剩余可投金额%s元！", remainderInvest));
		}
//		remainderAmount = ArithUtil.sub(remainderAmount, tradeAmount);
		remainderInvest = ArithUtil.sub(remainderInvest, tradeAmount);
		BigDecimal tradeScacle = BigDecimal.ZERO; // 交易比例
		BigDecimal tradePrincipal;// 原始金额(用折价金额反推())
		if(BigDecimal.ONE.compareTo(loanTransferApplyEntity.getReducedScale()) == 0){
			tradePrincipal = tradeAmount;
		} else {
			tradePrincipal = ArithUtil.div(tradeAmount, loanTransferApplyEntity.getReducedScale());
		}
		if(remainderInvest.compareTo(new BigDecimal("0.01")) <= 0) { // 剩余可投金额小于0.01表示该用户为最后一个用户，把剩余比例都给最后一个用户
			tradeScacle = loanTransferApplyEntity.getRemainderTradeScale();
		}
		else {
			// 交易比例 = 交易金额/(剩余本金*折价比例)
			tradeScacle = ArithUtil.div(tradeAmount, remainPrincipalReduced);
			// 债权的起投金额
//			BigDecimal investMinAmount =  loanInfoEntity.getInvestMinAmount();
			BigDecimal investMinAmount =  loanTransferApplyEntity.getInvestMinAmount();
			// by guoyk 2017/5/17 SLCF-2821 如果是智能投顾,不判断起投金额
			if ("auto".equals(appSource)) {
				investMinAmount = BigDecimal.ZERO;
			}
			
			// 8) 验证投资金额是否大于起投金额
			if(tradePrincipal.compareTo(investMinAmount) < 0){
				return new ResultVo(false, "投资金额不能小于起投金额！");
			}
			
			// 9) 验证投资金额是否是递增倍数
			// by guoyk 2017/6/1 SLCF-2821 如果是智能投顾,不判断递增金额
			if (!"auto".equals(appSource)) {
				if(tradePrincipal.compareTo(investMinAmount) > 0 
						&& !ArithUtil.isDivInt(loanTransferApplyEntity.getIncreaseAmount(), ArithUtil.sub(tradePrincipal, investMinAmount))){
					return new ResultVo(false, "投资金额必须是递增金额的整数倍！");
				}
			}
			
			// 10) 投资后剩余金额小于投标金额，需要补满
			if(remainderInvest.compareTo(investMinAmount) < 0){
				return new ResultVo(false, "投资后剩余可投金额小于起投金额，您必须购买所有剩余可投金额!");
			}
			
			// 11) 计算投资比例
//			// 转让金额 = 持有比例*PV*误差系数*折价比例
//			BigDecimal transferAmount = ArithUtil.mul(ArithUtil.mul(ArithUtil.mul(loanValue, loanTransferApplyEntity.getTradeScale()), loanTransferApplyEntity.getDeviationScale()), loanTransferApplyEntity.getReducedScale());
//			// 交易比例=交易金额/转让金额*持有比例
//			tradeScacle = ArithUtil.div(ArithUtil.mul(tradeAmount, loanTransferApplyEntity.getTradeScale()), transferAmount);
		}

		// todo 甘叶勇 加息券
		// 最近一期未结算的加息券奖励记录
		PurchaseAwardInfoEntity purchaseAwardInfoEntity = purchaseAwardInfoService.findOne();
		if (purchaseAwardInfoEntity != null) {
			// 如果出让的债权有使用加息券的，需要失效, 按期数排序
			// 最近一期未结清的记录有效，其它全部失效
			purchaseAwardInfoService.invalidByCurrentTerm(purchaseAwardInfoEntity.getCurrentTerm());
			// 更新最近一期加息金额调整，根据持有时间/还款周期
			List<RepaymentPlanInfoEntity> repaymentPlanInfoEntities = repaymentPlanInfoRepository.findByLoanId(loanInfoEntity.getId());
			Date preDate = null;
			Date nextDate = null;
			// 还款计划在查询的时候已经排序
			for (RepaymentPlanInfoEntity repaymentPlanInfo : repaymentPlanInfoEntities) {
				if (Constant.REPAYMENT_STATUS_CLEAN.equals(repaymentPlanInfo.getRepaymentStatus())) {
					preDate = DateUtils.parseDate(repaymentPlanInfo.getExpectRepaymentDate(), "yyyyMMdd");
					continue;
				}
				if (Constant.REPAYMENT_STATUS_WAIT.equals(repaymentPlanInfo.getRepaymentStatus())) {
					nextDate = DateUtils.parseDate(repaymentPlanInfo.getExpectRepaymentDate(), "yyyyMMdd");
					break;
				}
			}
			// 持有时间
			int d1 = 0;
			// 间隔周期
			int d2 = 0;
			if (1 == purchaseAwardInfoEntity.getCurrentTerm()) {
				// 第一期 起息开始日期 与 当前日期 间隔 / 起息开始日期 与 第一个还款日 间隔
				d1 = DateUtils.datePhaseDiffer(now, loanInfoEntity.getInvestStartDate());
				d2 = DateUtils.datePhaseDiffer(nextDate, loanInfoEntity.getInvestStartDate());
			} else {
				// 第二期 + 上一个还款日-当前日期间隔 / 上个还款日 - 期望还款日 间隔
				d1 = DateUtils.datePhaseDiffer(now, preDate);
				d2 = DateUtils.datePhaseDiffer(nextDate, preDate);
			}
			// 金额 * 比例
			purchaseAwardInfoEntity.setAwardAmount(ArithUtil.div(
					ArithUtil.mul(purchaseAwardInfoEntity.getAwardAmount(),
							new BigDecimal(d1)),
					new BigDecimal(d2)));
		}

		// 生效日期
		String effectDate = DateUtils.formatDate(now, "yyyyMMdd");
		// 当期还款日期
		String currentRepaymentDate = DateUtils.formatDate(loanInfoEntity.getLoanDetailInfoEntity().getNextExpiry(), "yyyyMMdd");
		// 如果延期还款，有效时间取计划中的还款日
		if(effectDate.compareTo(currentRepaymentDate) >= 0) {
			effectDate = currentRepaymentDate;
		}
		// 12) 创建一笔新的投资
		// 12-1) 投资表
		InvestInfoEntity investInfoEntity = new InvestInfoEntity();
		investInfoEntity.setCustId(custId);
		investInfoEntity.setInvestAmount(tradeAmount);
		investInfoEntity.setInvestStatus(Constant.INVEST_STATUS_EARN); //投资中
		investInfoEntity.setInvestMode(Constant.INVEST_METHOD_TRANSFER); //转让
		investInfoEntity.setInvestDate(DateUtils.formatDate(now, "yyyyMMdd"));
		investInfoEntity.setEffectDate(effectDate);
		investInfoEntity.setExpireDate(DateUtils.formatDate(loanInfoEntity.getInvestEndDate(), "yyyyMMdd"));
		investInfoEntity.setLoanId(wealthHoldInfoEntity.getLoanId());
		investInfoEntity.setTransferApplyId(transferApplyId);
		investInfoEntity.setBasicModelProperty(custId, true);
		
		// 添加客户经理
		CustRecommendInfoEntity custRecommendInfoEntity = custRecommendInfoRepository.findByQuiltCustIdAndRecordStatus(custInfoEntity.getId(), Constant.VALID_STATUS_VALID);
		if(null != custRecommendInfoEntity) 
		investInfoEntity.setCustManagerId(custRecommendInfoEntity.getCustId());
		investInfoEntity = investInfoRepository.save(investInfoEntity);
		
		// 12-2) 投资详情
		InvestDetailInfoEntity investDetailInfoEntity = new InvestDetailInfoEntity();
		investDetailInfoEntity.setInvestId(investInfoEntity.getId());
		investDetailInfoEntity.setTradeNo(numberService.generateLoanContractNumber());
		investDetailInfoEntity.setInvestAmount(tradeAmount);
		investDetailInfoEntity.setInvestSource(appSource);
		investDetailInfoEntity.setBasicModelProperty(custId, true);
		investDetailInfoEntity = investDetailInfoRepository.save(investDetailInfoEntity);
		
		// 12-3) 添加分账
		SubAccountInfoEntity investorSubAccount = new SubAccountInfoEntity();
		investorSubAccount.setBasicModelProperty(custId, true);
		investorSubAccount.setCustId(custId);
		investorSubAccount.setAccountId(investorAccount.getId());
		investorSubAccount.setRelateType(Constant.TABLE_BAO_T_INVEST_INFO);
		investorSubAccount.setRelatePrimary(investInfoEntity.getId());
		investorSubAccount.setSubAccountNo(numberService.generateCustomerNumber());
		investorSubAccount.setAccountTotalValue(BigDecimal.ZERO);
		investorSubAccount.setAccountFreezeValue(BigDecimal.ZERO);
		investorSubAccount.setAccountAvailableValue(BigDecimal.ZERO);
		investorSubAccount.setAccountAmount(BigDecimal.ZERO);
		investorSubAccount.setDeviationAmount(BigDecimal.ZERO);
		investorSubAccount = subAccountInfoRepository.save(investorSubAccount);
		
		// 13) 记录流水
		String reqeustNo = numberService.generateTradeBatchNumber();
		// 13-1) 投资人主账户——>投资人分账户
		custAccountService.updateAccount(investorAccount, null, null, 
				investorSubAccount, "2", SubjectConstant.TRADE_FLOW_TYPE_JOIN_LOAN_TRANSFER, 
				reqeustNo, tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_JOIN_LOAN_TRANSFER, 
				Constant.TABLE_BAO_T_INVEST_INFO, investInfoEntity.getId(),  
				String.format("购买债权转让[%s]", loanTransferApplyEntity.getTransferNo()), custId);
		
		// 13-2) 受让人分账户——>转让人分账户
		// 转让人分账户
		SubAccountInfoEntity loanerSubAccount = subAccountInfoRepository.findByRelatePrimary(wealthHoldInfoEntity.getInvestId());
		custAccountService.updateAccount(null, investorSubAccount, null, 
				loanerSubAccount, "4", SubjectConstant.TRADE_FLOW_TYPE_JOIN_LOAN_TRANSFER, 
				reqeustNo, tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_JOIN_LOAN_TRANSFER, 
				Constant.TABLE_BAO_T_INVEST_INFO, investInfoEntity.getId(),  
				String.format("购买债权转让[%s]", loanTransferApplyEntity.getTransferNo()), custId);
		
		// 13-3) 转让人分账户——>转让人主账户
		// 转让人主账户
		AccountInfoEntity loanerAccount = accountInfoRepository.findByCustId(wealthHoldInfoEntity.getCustId());
		custAccountService.updateAccount(null, loanerSubAccount, loanerAccount, 
				null, "3", SubjectConstant.TRADE_FLOW_TYPE_JOIN_LOAN_TRANSFER, 
				reqeustNo, tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_JOIN_LOAN_TRANSFER, 
				Constant.TABLE_BAO_T_INVEST_INFO, investInfoEntity.getId(),  
				String.format("购买债权转让[%s]", loanTransferApplyEntity.getTransferNo()), custId);
		
//		 13-4) 转让人主账户——>公司收益主账户
//		BigDecimal manageAmount = ArithUtil.mul(tradeAmount, paramService.findTransferRate());
		// 转让本金 = 剩余本金*交易比例
//		BigDecimal tradePrincipal = ArithUtil.mul(remainPrincipal, tradeScacle);
		// 转让费用 = 转让金额 * 0.4%
		BigDecimal transferRate = loanTransferApplyEntity.getTransferRate();
		BigDecimal manageRate = BigDecimal.ZERO;
		if(transferRate != null){
			manageRate = transferRate;
		} else {
			manageRate = paramService.findTransferRate();
		}
		BigDecimal manageAmount = ArithUtil.mul(tradePrincipal, manageRate);
		
		AccountInfoEntity companyAccount = accountInfoRepository.findByCustId(Constant.CUST_ID_ERAN);
		custAccountService.updateAccount(loanerAccount, null, companyAccount, 
				null, "1", SubjectConstant.TRADE_FLOW_TYPE_EXPENSE_LOAN_TRANSFER, 
				reqeustNo, manageAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_EXPENSE_LOAN_TRANSFER, 
				Constant.TABLE_BAO_T_INVEST_INFO, investInfoEntity.getId(),  
				String.format("购买债权转让[%s]", loanTransferApplyEntity.getTransferNo()), custId);
		
		// 14) 更新转让申请
		// 转让价值 = PV*交易比例
		BigDecimal tradeValue = ArithUtil.mul(loanValue, tradeScacle);
		loanTransferApplyEntity.setRemainderTradeScale(ArithUtil.sub(loanTransferApplyEntity.getRemainderTradeScale(), tradeScacle));
//		loanTransferApplyEntity.setTradeAmount(ArithUtil.add(loanTransferApplyEntity.getTradeAmount(), tradeAmount));
		loanTransferApplyEntity.setTradeAmount(ArithUtil.add(loanTransferApplyEntity.getTradeAmount(), ArithUtil.mul(tradeValue, loanTransferApplyEntity.getReducedScale())));
		loanTransferApplyEntity.setTradeValue(ArithUtil.add(loanTransferApplyEntity.getTradeValue(), tradeValue));
		loanTransferApplyEntity.setTradePrincipal(ArithUtil.add(loanTransferApplyEntity.getTradePrincipal(), tradePrincipal));
		loanTransferApplyEntity.setManageAmount(ArithUtil.add(loanTransferApplyEntity.getManageAmount(), manageAmount));
		loanTransferApplyEntity.setTradeTimes(loanTransferApplyEntity.getTradeTimes() + 1);
		loanTransferApplyEntity.setApplyStatus(loanTransferApplyEntity.getRemainderTradeScale().compareTo(BigDecimal.ZERO) == 0 ? Constant.LOAN_TRANSFER_APPLY_STATUS_02 : Constant.LOAN_TRANSFER_APPLY_STATUS_04);
		loanTransferApplyEntity.setBasicModelProperty(custId, false);
		
		// 15) 新建受让人持有债权信息
		BigDecimal totalInterest = repaymentPlanInfoRepository.sumRepaymentInterestByLoanIdAndRepaymentStatus(loanInfoEntity.getId(), Constant.REPAYMENT_STATUS_WAIT);
		WealthHoldInfoEntity investHoldInfoEntity = new WealthHoldInfoEntity();
		investHoldInfoEntity.setInvestId(investInfoEntity.getId());
		investHoldInfoEntity.setSubAccountId(investorSubAccount.getId());
		investHoldInfoEntity.setCustId(custId);
		investHoldInfoEntity.setLoanId(loanInfoEntity.getId());
		investHoldInfoEntity.setHoldScale(tradeScacle);
		investHoldInfoEntity.setHoldAmount(tradeAmount);
		investHoldInfoEntity.setTradeScale(BigDecimal.ZERO);
		investHoldInfoEntity.setExceptAmount(ArithUtil.mul(totalInterest, tradeScacle));
		investHoldInfoEntity.setReceivedAmount(BigDecimal.ZERO);
		investHoldInfoEntity.setHoldStatus(Constant.HOLD_STATUS_01);
		investHoldInfoEntity.setIsCenter(Constant.IS_CENTER_02);
		investHoldInfoEntity.setTradeScale(BigDecimal.ZERO);
		investHoldInfoEntity.setBasicModelProperty(custId, true);
		investHoldInfoEntity = wealthHoldInfoRepository.save(investHoldInfoEntity);
		
		// 16) 备份转让人持有债权信息
		String tradeNo = numberService.generateLoanTransferOutNo();
		WealthHoldHistoryInfoEntity wealthHoldHistoryInfoEntity = new WealthHoldHistoryInfoEntity();
		wealthHoldHistoryInfoEntity.setHoldId(wealthHoldInfoEntity.getId());
		wealthHoldHistoryInfoEntity.setInvestId(wealthHoldInfoEntity.getInvestId());
		wealthHoldHistoryInfoEntity.setSubAccountId(wealthHoldInfoEntity.getSubAccountId());
		wealthHoldHistoryInfoEntity.setCustId(wealthHoldInfoEntity.getCustId());
		wealthHoldHistoryInfoEntity.setLoanId(wealthHoldInfoEntity.getLoanId());
		wealthHoldHistoryInfoEntity.setHoldScale(wealthHoldInfoEntity.getHoldScale());
		wealthHoldHistoryInfoEntity.setHoldAmount(wealthHoldInfoEntity.getHoldAmount());
		wealthHoldHistoryInfoEntity.setExceptAmount(wealthHoldInfoEntity.getExceptAmount());
		wealthHoldHistoryInfoEntity.setReceivedAmount(wealthHoldInfoEntity.getReceivedAmount());
		wealthHoldHistoryInfoEntity.setHoldStatus(wealthHoldInfoEntity.getHoldStatus());
		wealthHoldHistoryInfoEntity.setHoldSource(wealthHoldInfoEntity.getHoldSource());
		wealthHoldHistoryInfoEntity.setIsCenter(wealthHoldInfoEntity.getIsCenter());
		wealthHoldHistoryInfoEntity.setOldCreateDate(wealthHoldInfoEntity.getCreateDate());
		wealthHoldHistoryInfoEntity.setOldCreateUser(wealthHoldInfoEntity.getCreateUser());
		wealthHoldHistoryInfoEntity.setOldLastUpdateDate(wealthHoldInfoEntity.getLastUpdateDate());
		wealthHoldHistoryInfoEntity.setOldLastUpdateUser(wealthHoldInfoEntity.getLastUpdateUser());
		wealthHoldHistoryInfoEntity.setOldMemo(wealthHoldInfoEntity.getMemo());
		wealthHoldHistoryInfoEntity.setRequestNo(reqeustNo);
		wealthHoldHistoryInfoEntity.setTradeNo(tradeNo);
		wealthHoldHistoryInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
		wealthHoldHistoryInfoRepository.save(wealthHoldHistoryInfoEntity);
		
		// 17) 更新转让人持有债权信息
		wealthHoldInfoEntity.setHoldScale(ArithUtil.sub(wealthHoldInfoEntity.getHoldScale(), tradeScacle));
		wealthHoldInfoEntity.setTradeScale(ArithUtil.add(wealthHoldInfoEntity.getTradeScale(), tradeScacle));
		if(wealthHoldInfoEntity.getHoldScale().compareTo(BigDecimal.ZERO) == 0) {
			wealthHoldInfoEntity.setHoldStatus(Constant.HOLD_STATUS_04);
			InvestInfoEntity senderInvestInfoEntity = investInfoRepository.findOne(wealthHoldInfoEntity.getInvestId());
			if(senderInvestInfoEntity == null) { // 投资信息为空
				throw new SLException("原始投资信息为空");
			}
			senderInvestInfoEntity.setInvestStatus(Constant.INVEST_STATUS_TRANSFER);
			senderInvestInfoEntity.setBasicModelProperty(custId, false);
		}
		wealthHoldInfoEntity.setBasicModelProperty(custId, false);
		
		// 18) 记录债权转让信息
		LoanTransferEntity loanTransferEntity = new LoanTransferEntity();
		loanTransferEntity.setSenderHoldId(wealthHoldInfoEntity.getId());
		loanTransferEntity.setSenderLoanId(wealthHoldInfoEntity.getLoanId());
		loanTransferEntity.setSenderCustId(wealthHoldInfoEntity.getCustId());
		loanTransferEntity.setReceiveHoldId(investHoldInfoEntity.getId());
		loanTransferEntity.setReceiveLoanId(investHoldInfoEntity.getLoanId());
		loanTransferEntity.setReceiveCustId(investHoldInfoEntity.getCustId());
		loanTransferEntity.setTransferApplyId(transferApplyId);
//		loanTransferEntity.setTradeAmount(tradeAmount);
		loanTransferEntity.setTradeAmount(ArithUtil.mul(tradeValue, loanTransferApplyEntity.getReducedScale()));
		loanTransferEntity.setTradeValue(tradeValue);
		loanTransferEntity.setTradeScale(tradeScacle);
		loanTransferEntity.setTradePrincipal(tradePrincipal);
		loanTransferEntity.setTransferExpenses(manageAmount);
		loanTransferEntity.setTradeNo(tradeNo);
		loanTransferEntity.setRequestNo(reqeustNo);
		loanTransferEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
		loanTransferRepository.save(loanTransferEntity);
		
		// 19) 记录操作日志
		LogInfoEntity logInfoEntity = new LogInfoEntity();
		logInfoEntity.setRelateType(Constant.TABLE_BAO_T_INVEST_INFO);
		logInfoEntity.setRelatePrimary(investInfoEntity.getId());
		logInfoEntity.setLogType(Constant.OPERATION_TYPE_81);
		logInfoEntity.setOperDesc("");
		logInfoEntity.setOperPerson(custId);
		logInfoEntity.setOperIpaddress((String)params.get("ipAddress"));
		logInfoEntity.setMemo(String.format("%s购买债权转让，投资金额%s", custInfoEntity.getLoginName(), tradeAmount.toString()));
		logInfoEntity.setBasicModelProperty(custId, true);
		logInfoEntityRepository.save(logInfoEntity);
		
		// 20) 记录APP信息(债权转让)
		if(params.containsKey("channelNo")) {
			Map<String, Object> deviceParams = Maps.newConcurrentMap();
			deviceParams.putAll(params);
			deviceParams.put("relateType", Constant.TABLE_BAO_T_INVEST_INFO);
			deviceParams.put("relatePrimary", investInfoEntity.getId());
			deviceParams.put("tradeType", Constant.OPERATION_TYPE_81);
			deviceParams.put("userId", custId);
			deviceService.saveUserDevice(deviceParams);
		}	
		
		// 21) 准备短信内容
		List<Map<String, Object>> smsList = Lists.newArrayList();
		// 转让人短信、站内信
		CustInfoEntity senderCust = custInfoRepository.findOne(wealthHoldInfoEntity.getCustId());
		Map<String, Object> smsParams = new HashMap<String, Object>();
		smsParams.put("mobile", senderCust.getMobile());
		smsParams.put("custId", senderCust.getId());	
		smsParams.put("messageType", Constant.SMS_TYPE_LOAN_TRASNFER);
		smsParams.put("values", new Object[] { // 短信息内容
				loanInfoEntity.getLoanTitle(),
				ArithUtil.formatScale(tradeAmount, 2).toPlainString()});
		smsParams.put("systemMessage", new Object[] { // 站内信内容
				DateUtils.formatDate(new Date(), "yyyy-MM-dd"),
				loanInfoEntity.getLoanTitle(),
				ArithUtil.formatScale(tradeAmount, 2).toPlainString()});
		smsList.add(smsParams);

		// 给原始投资人站内信
		CustInfoEntity ownerCust = custInfoRepository.findOne(loanInfoEntity.getCustId());
		Map<String, Object> ownerSmsParams = new HashMap<String, Object>();
		ownerSmsParams.put("mobile", ownerCust.getMobile()!=null?ownerCust.getMobile():"18888888888");
		ownerSmsParams.put("custId", ownerCust.getId());	
		ownerSmsParams.put("messageType", Constant.SMS_TYPE_LOAN_TRASNFER_OWNER);
		ownerSmsParams.put("systemMessage", new Object[] { // 站内信内容
				DateUtils.formatDate(new Date(), "yyyy-MM-dd"),
				loanInfoEntity.getLoanTitle(),
				ArithUtil.formatScale(tradeAmount, 2).toPlainString()});
		smsList.add(ownerSmsParams);

		// 异步生成协议
		Map<String, Object> protocalParam = Maps.newConcurrentMap();
		protocalParam.put("investId", investInfoEntity.getId());
		protocalParam.put("custId", investInfoEntity.getCustId());
		exportFileService.asynDownloadContract(protocalParam);
		
		//准备向微信推送债权转让通知消息
		Map<String, Object> VXparams = new HashMap<String, Object>();
		if(senderCust.getOpenid()!=null){
			VXparams.put("openId", senderCust.getOpenid());
			VXparams.put("template_id", Constant.SMS_TYPE_VX_6);
			StringBuilder sb=new StringBuilder()
				.append(DateUtils.formatDate(new Date(), "yyyy-MM-dd"))
				.append("|")
				.append(loanInfoEntity.getLoanTitle())
				.append("|")
				.append(ArithUtil.formatScale(tradeAmount, 2).toPlainString());

			VXparams.put("data", java.net.URLEncoder.encode(sb.toString()));
			String sign=HttpRequestUtil.sign((String)VXparams.get("openId"),(String)VXparams.get("template_id"),sb.toString());
			VXparams.put("sign", sign);
			HttpRequestUtil.pushSmsToVX(VXparams);
		}
		
		return new ResultVo(true, "购买操作成功", smsList);
	}

	@SuppressWarnings("deprecation")
	@Transactional(rollbackFor = SLException.class)
	@Override
	public ResultVo buyCreditExt(Map<String, Object> params) throws SLException {

		String transferApplyId = (String)params.get("transferApplyId");
		String custId = (String)params.get("custId");
		BigDecimal tradeAmount = new BigDecimal(params.get("tradeAmount").toString());// update SLCF-2823 现在传进来的是折价的本金
		Date now = new Date();
		String appSource = (String)params.get("appSource");
		appSource = Strings.nullToEmpty(StringUtils.isEmpty(appSource) ? Constant.INVEST_SOURCE_PC : appSource).toLowerCase();
		String custActivityId = CommonUtils.emptyToString(params.get("custActivityId"));

		// 1) 还款时不允许投资
		JobRunListenerEntity jobRunListenerEntity = jobRunListenerRepository.findByJobName(Constant.JOB_NAME_LOAN_REPAYMENT);
		if(jobRunListenerEntity != null && Constant.EXEC_STATUS_RUNNING.equals(jobRunListenerEntity.getExecuteStatus())) {
			return new ResultVo(false, "系统正在执行还款任务，请稍后再试！");
		}

		// 2) 验证用户状态
		CustInfoEntity custInfoEntity = custInfoRepository.findOne(custId);
		if(custInfoEntity == null){
			throw new SLException("用户不存在！");
		}
		if(!Constant.ENABLE_STATUS_01.equals(custInfoEntity.getEnableStatus())){
			throw new SLException("账户为非正常状态，可能被冻结，请联系客服！");
		}

		// 3) 验证投资人账户是否足额
		AccountInfoEntity investorAccount = accountInfoRepository.findByCustId(custId); //客户账户表
		if(tradeAmount.compareTo(investorAccount.getAccountAvailableAmount()) > 0) {
			return new ResultVo(false, "账户可用余额不足， 请充值！");
		}

		// 4) 验证债权申请状态、结束时间
		LoanTransferApplyEntity loanTransferApplyEntity = loanTransferApplyRepository.findOne(transferApplyId);
		if(loanTransferApplyEntity == null) {
			return new ResultVo(false, "未找到债权申请");
		}
		if(Constant.LOAN_TRANSFER_CANCEL_STATUS_02.equals(loanTransferApplyEntity.getCancelStatus())) {
			return new ResultVo(false, "该笔债权转让申请已撤销");
		}
		if(Constant.LOAN_TRANSFER_APPLY_STATUS_02.equals(loanTransferApplyEntity.getApplyStatus())) {
			return new ResultVo(false, "该笔债权转让申请已转让成功");
		}
		if(now.compareTo(loanTransferApplyEntity.getTransferEndDate()) > 0) {
			return new ResultVo(false, "该笔债权转让申请已超出预定日期");
		}

		// 5) 用户不允许投资自己的债权转让
		if(custId.equals(loanTransferApplyEntity.getSenderCustId())) {
			return new ResultVo(false, "用户不允许投资自己的债权转让");
		}

		// 6) 判断借款状态
		WealthHoldInfoEntity wealthHoldInfoEntity = wealthHoldInfoRepository.findOne(loanTransferApplyEntity.getSenderHoldId());
		if(wealthHoldInfoEntity == null) {
			return new ResultVo(false, "未找到持有债权信息");
		}
		if(Constant.HOLD_STATUS_04.equals(wealthHoldInfoEntity.getHoldStatus())) {
			return new ResultVo(false, "该笔债权已转让");
		}
		LoanInfoEntity loanInfoEntity = loanInfoRepository.findOne(wealthHoldInfoEntity.getLoanId());
		if(loanInfoEntity == null || !Constant.LOAN_STATUS_08.equals(loanInfoEntity.getLoanStatus())){
			return new ResultVo(false, "项目非正常状态");
		}

		// update by http://192.16.150.101:8080/browse/SLCF-2823 at 2017-05-18
		// 7) 验证交易金额是否小于等于剩余可投金额
		// 债权PV
		BigDecimal loanValue = creditRightValueRepository.findByLoanIdAndValueDate(wealthHoldInfoEntity.getLoanId(), DateUtils.formatDate(now, "yyyyMMdd"));
//		// 剩余可投金额=剩余可投持有比例×PV×折价系数
//		BigDecimal remainderAmount = ArithUtil.mul(ArithUtil.mul(loanValue, loanTransferApplyEntity.getRemainderTradeScale(), 2), loanTransferApplyEntity.getReducedScale(), 2);

		// 剩余本金
		BigDecimal remainPrincipal = loanInfoEntity.getLoanDetailInfoEntity().getCreditRemainderPrincipal();
		// 剩余本金（折价）
		BigDecimal remainPrincipalReduced = ArithUtil.mul(ArithUtil.mul(remainPrincipal, loanTransferApplyEntity.getDeviationScale()), loanTransferApplyEntity.getReducedScale());
		// 剩余可投金额 = 剩余本金 * 剩余可投持有比例
		BigDecimal remainderInvest = ArithUtil.mul(remainPrincipalReduced, loanTransferApplyEntity.getRemainderTradeScale()).setScale(8, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_DOWN);

		if(tradeAmount.compareTo(remainderInvest) > 0){
			return new ResultVo(false, String.format("项目剩余可投金额%s元！", remainderInvest));
		}
//		remainderAmount = ArithUtil.sub(remainderAmount, tradeAmount);
		remainderInvest = ArithUtil.sub(remainderInvest, tradeAmount);
		BigDecimal tradeScacle = BigDecimal.ZERO; // 交易比例
		BigDecimal tradePrincipal;// 原始金额(用折价金额反推())
		if(BigDecimal.ONE.compareTo(loanTransferApplyEntity.getReducedScale()) == 0){
			tradePrincipal = tradeAmount;
		} else {
			tradePrincipal = ArithUtil.div(tradeAmount, loanTransferApplyEntity.getReducedScale());
		}
		if(remainderInvest.compareTo(new BigDecimal("0.01")) <= 0) { // 剩余可投金额小于0.01表示该用户为最后一个用户，把剩余比例都给最后一个用户
			tradeScacle = loanTransferApplyEntity.getRemainderTradeScale();
		}
		else {
			// 交易比例 = 交易金额/(剩余本金*折价比例)
			tradeScacle = ArithUtil.div(tradeAmount, remainPrincipalReduced);
			// 起投金额
//			BigDecimal investMinAmount =  loanInfoEntity.getInvestMinAmount();
			BigDecimal investMinAmount =  loanTransferApplyEntity.getInvestMinAmount();
			// by guoyk 2017/5/17 SLCF-2821 如果是智能投顾,不判断起投金额
			if ("auto".equals(appSource)) {
				investMinAmount = BigDecimal.ZERO;
			}

			// 8) 验证投资金额是否大于起投金额
			if(tradePrincipal.compareTo(investMinAmount) < 0){
				return new ResultVo(false, "投资金额不能小于起投金额！");
			}

			// 9) 验证投资金额是否是递增倍数
			// by guoyk 2017/6/1 SLCF-2821 如果是智能投顾,不判断递增金额
			if (!"auto".equals(appSource)) {
				if(tradePrincipal.compareTo(investMinAmount) > 0
						&& !ArithUtil.isDivInt(loanTransferApplyEntity.getIncreaseAmount(), ArithUtil.sub(tradePrincipal, investMinAmount))){
					return new ResultVo(false, "投资金额必须是递增金额的整数倍！");
				}
			}

			// 10) 投资后剩余金额小于投标金额，需要补满
			if(remainderInvest.compareTo(investMinAmount) < 0){
				return new ResultVo(false, "投资后剩余可投金额小于起投金额，您必须购买所有剩余可投金额!");
			}

			// 11) 计算投资比例
//			// 转让金额 = 持有比例*PV*误差系数*折价比例
//			BigDecimal transferAmount = ArithUtil.mul(ArithUtil.mul(ArithUtil.mul(loanValue, loanTransferApplyEntity.getTradeScale()), loanTransferApplyEntity.getDeviationScale()), loanTransferApplyEntity.getReducedScale());
//			// 交易比例=交易金额/转让金额*持有比例
//			tradeScacle = ArithUtil.div(ArithUtil.mul(tradeAmount, loanTransferApplyEntity.getTradeScale()), transferAmount);
		}
		Map<String, Object> customerActivityInfo = new HashMap<String, Object>();
		 // todo 甘叶勇 加息券
		// 最近一期未结算的加息券奖励记录
		PurchaseAwardInfoEntity purchaseAwardInfoEntity = purchaseAwardInfoService.findOne();
		if (purchaseAwardInfoEntity != null) {
			// 如果出让的债权有使用加息券的，需要失效, 按期数排序
			// 最近一期未结清的记录有效，其它全部失效
			purchaseAwardInfoService.invalidByCurrentTerm(purchaseAwardInfoEntity.getCurrentTerm());
			// 更新最近一期加息金额调整，根据持有时间/还款周期
			List<RepaymentPlanInfoEntity> repaymentPlanInfoEntities = repaymentPlanInfoRepository.findByLoanId(loanInfoEntity.getId());
			Date preDate = null;
			Date nextDate = null;
			// 还款计划在查询的时候已经排序
			for (RepaymentPlanInfoEntity repaymentPlanInfo : repaymentPlanInfoEntities) {
				if (Constant.REPAYMENT_STATUS_CLEAN.equals(repaymentPlanInfo.getRepaymentStatus())) {
					preDate = DateUtils.parseDate(repaymentPlanInfo.getExpectRepaymentDate(), "yyyyMMdd");
					continue;
				}
				if (Constant.REPAYMENT_STATUS_WAIT.equals(repaymentPlanInfo.getRepaymentStatus())) {
					nextDate = DateUtils.parseDate(repaymentPlanInfo.getExpectRepaymentDate(), "yyyyMMdd");
					break;
				}
			}
			int d1 = 0;
			int d2 = 0;
			if (1 == purchaseAwardInfoEntity.getCurrentTerm()) {
				// 第一期 起息开始日期 与 当前日期 间隔 / 起息开始日期 与 第一个还款日 间隔
				d1 = DateUtils.datePhaseDiffer(now, loanInfoEntity.getInvestStartDate());
				d2 = DateUtils.datePhaseDiffer(nextDate, loanInfoEntity.getInvestStartDate());

			} else {
				// 第二期 + 上一个还款日-当前日期间隔 / 上个还款日 - 期望还款日 间隔
				d1 = DateUtils.datePhaseDiffer(now, preDate);
				d2 = DateUtils.datePhaseDiffer(nextDate, preDate);
			}
			// 金额 * 比例
			purchaseAwardInfoEntity.setAwardAmount(purchaseAwardInfoEntity.getAwardAmount().multiply(new BigDecimal(d1)).divide(new BigDecimal(d2)));
		}

		//判断是否使用奖励
		if(custActivityId != null && !custActivityId.isEmpty())
		{
			// 判断红包使用条件是否满足
			customerActivityInfo = custActivityService.findRewardByIdAndCustId(custActivityId, custId);
			if (customerActivityInfo == null) {
				return new ResultVo(false, "无有效的红包记录！");
			}
			int startAmount = CommonUtils.emptyToInt(customerActivityInfo.get("STARTAMOUNT"));
			if (startAmount != 0 && tradeAmount.compareTo(new BigDecimal(startAmount)) < 0 ) {
				return new ResultVo(false, String.format("红包限制起投金额%s元！", startAmount));
			}
			String useScope = CommonUtils.emptyToString(customerActivityInfo.get("USESCOPE"));
			//还款方式
			String repayMent = CommonUtils.emptyToString(customerActivityInfo.get("REPAYMENTMETHODS"));
			//标的是否可转让
			String isTransfer = CommonUtils.emptyToString(customerActivityInfo.get("ISTRANSFER"));
			String seatTeam = loanInfoEntity.getSeatTerm() == -1 ? "不可转让" : "可转让";
						// 是新手标，并且 红包使用范围不包含新手标
//						if (Constant.LOAN_INFO_NEWER_FLAG.equals(loanInfoEntity.getNewerFlag()) && useScope.indexOf(Constant.ACTIVITY_AWARD_USE_SCOPE_0) == -1) {
//							return new ResultVo(false, "该红包不能在新手标使用!");
//						}
			// 转让范围是否包含转让专区
			if (useScope.indexOf(Constant.ACTIVITY_AWARD_USE_SCOPE_2) == -1) {
				return new ResultVo(false, "该红包不能在转让专区使用!");
			}
			//标的还款方式
			if(!repayMent.isEmpty() && repayMent.indexOf(loanInfoEntity.getRepaymentMethod()) == -1)
			{
				return new ResultVo(false, repayMent + "红包不能用于" + loanInfoEntity.getRepaymentMethod() + "标!");
			}
			//标的是否可转让
			if(!isTransfer.isEmpty() && !isTransfer.equals(seatTeam))
			{
				return new ResultVo(false, isTransfer+"红包不能用于" + seatTeam + "的标!");
			}
			// 如果有标的期限限制 并且期限单位不等
			String increaseUnit = CommonUtils.emptyToString(customerActivityInfo.get("INCREASEUNIT"));
			if (!increaseUnit.isEmpty() && !increaseUnit.equals(loanInfoEntity.getLoanUnit())) 
			{
				// 期限单位不一致
				return new ResultVo(false, "项目期限不符合红包使用条件!");
			}
			Integer seatTerm = CommonUtils.emptyToInt(customerActivityInfo.get("SEATTERM"));

			// 如果有期数限制并且标的期数小于限制
			if (seatTerm != 0 && loanInfoEntity.getLoanTerm() != null && loanInfoEntity.getLoanTerm() < seatTerm) 
			{
				// 期数不符合
				return new ResultVo(false, "项目期限不符合红包使用条件!");
			}
			// 校验完成			
		}
		// 生效日期
		String effectDate = DateUtils.formatDate(now, "yyyyMMdd");
		// 当期还款日期
		String currentRepaymentDate = DateUtils.formatDate(loanInfoEntity.getLoanDetailInfoEntity().getNextExpiry(), "yyyyMMdd");
		// 如果延期还款，有效时间取计划中的还款日
		if(effectDate.compareTo(currentRepaymentDate) >= 0) {
			effectDate = currentRepaymentDate;
		}
		// 12) 创建一笔新的投资
		// 12-1) 投资表
		InvestInfoEntity investInfoEntity = new InvestInfoEntity();
		investInfoEntity.setCustId(custId);
		investInfoEntity.setInvestAmount(tradeAmount);
		investInfoEntity.setInvestStatus(Constant.INVEST_STATUS_EARN); //投资中
		investInfoEntity.setInvestMode(Constant.INVEST_METHOD_TRANSFER); //转让
		investInfoEntity.setInvestDate(DateUtils.formatDate(now, "yyyyMMdd"));
		investInfoEntity.setEffectDate(effectDate);
		investInfoEntity.setExpireDate(DateUtils.formatDate(loanInfoEntity.getInvestEndDate(), "yyyyMMdd"));
		investInfoEntity.setLoanId(wealthHoldInfoEntity.getLoanId());
		investInfoEntity.setTransferApplyId(transferApplyId);
		investInfoEntity.setBasicModelProperty(custId, true);

		// 添加客户经理
		CustRecommendInfoEntity custRecommendInfoEntity = custRecommendInfoRepository.findByQuiltCustIdAndRecordStatus(custInfoEntity.getId(), Constant.VALID_STATUS_VALID);
		if(null != custRecommendInfoEntity)
			investInfoEntity.setCustManagerId(custRecommendInfoEntity.getCustId());
		investInfoEntity = investInfoRepository.save(investInfoEntity);

		// 12-2) 投资详情
		InvestDetailInfoEntity investDetailInfoEntity = new InvestDetailInfoEntity();
		investDetailInfoEntity.setInvestId(investInfoEntity.getId());
		investDetailInfoEntity.setTradeNo(numberService.generateLoanContractNumber());
		investDetailInfoEntity.setInvestAmount(tradeAmount);
		investDetailInfoEntity.setInvestSource(appSource);
		investDetailInfoEntity.setBasicModelProperty(custId, true);
		investDetailInfoEntity = investDetailInfoRepository.save(investDetailInfoEntity);

		// 12-3) 添加分账
		SubAccountInfoEntity investorSubAccount = new SubAccountInfoEntity();
		investorSubAccount.setBasicModelProperty(custId, true);
		investorSubAccount.setCustId(custId);
		investorSubAccount.setAccountId(investorAccount.getId());
		investorSubAccount.setRelateType(Constant.TABLE_BAO_T_INVEST_INFO);
		investorSubAccount.setRelatePrimary(investInfoEntity.getId());
		investorSubAccount.setSubAccountNo(numberService.generateCustomerNumber());
		investorSubAccount.setAccountTotalValue(BigDecimal.ZERO);
		investorSubAccount.setAccountFreezeValue(BigDecimal.ZERO);
		investorSubAccount.setAccountAvailableValue(BigDecimal.ZERO);
		investorSubAccount.setAccountAmount(BigDecimal.ZERO);
		investorSubAccount.setDeviationAmount(BigDecimal.ZERO);
		investorSubAccount = subAccountInfoRepository.save(investorSubAccount);

		// 13) 记录流水
		String reqeustNo = numberService.generateTradeBatchNumber();
		// 	判断是否有使用红包
		if (custActivityId != null && !custActivityId.isEmpty()) {
			// 红包金额
			BigDecimal usableAmount = CommonUtils.emptyToDecimal(customerActivityInfo.get("GRANTAMOUNT"));
			// 更改账户金额流水 -- 用户实际主账户金额 + 红包金额 - 投资金额
			// 2.3.2 投资人主账户金额-加入金额、添加流水 更新客户主账户（投资人主账户——>投资人分账户）
			// 记录红包账户流水
			custAccountService.updateAccountExt(investorAccount, null, null,
					investorSubAccount, "2", SubjectConstant.TRADE_FLOW_TYPE_JOIN_LOAN_TRANSFER,
					reqeustNo, tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_JOIN_LOAN_TRANSFER,
					Constant.TABLE_BAO_T_INVEST_INFO, investInfoEntity.getId(),
					String.format("购买债权转让[%s],使用红包%s", loanTransferApplyEntity.getTransferNo(), usableAmount), custId, usableAmount);
			// 更改红包使用状态
			// 红包状态--全部使用
			// 最后更新时间、更新人
			// 标的编号
			// 投资金额
			// memo 投资编号 -- 请求编号
			CustActivityInfoEntity custActivityInfoEntity = custActivityService.findById(custActivityId);
			custActivityInfoEntity.setTradeStatus(Constant.USER_ACTIVITY_TRADE_STATUS_03);
			custActivityInfoEntity.setLastUpdateDate(new Date());
			custActivityInfoEntity.setLastUpdateUser(Constant.SYSTEM_USER_BACK);
			custActivityInfoEntity.setLoanId(wealthHoldInfoEntity.getLoanId());
			custActivityInfoEntity.setInvestAmount(CommonUtils.emptyToString(investInfoEntity.getInvestAmount()));
			StringBuilder memo = new StringBuilder(CommonUtils.emptyToString(custActivityInfoEntity.getMemo()));
			memo.append("待扣款-投资编号-")
					.append(investInfoEntity.getId())
					.append("-请求编号-")
					.append(reqeustNo)
					.append("-END")
					.append("-转让申请编号-")
					.append(transferApplyId);
			custActivityInfoEntity.setMemo(memo.toString());
			// 定时器定时更新 memo中待扣款的数据
		} else {
			// 13-1) 投资人主账户——>投资人分账户
			custAccountService.updateAccount(investorAccount, null, null,
					investorSubAccount, "2", SubjectConstant.TRADE_FLOW_TYPE_JOIN_LOAN_TRANSFER,
					reqeustNo, tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_JOIN_LOAN_TRANSFER,
					Constant.TABLE_BAO_T_INVEST_INFO, investInfoEntity.getId(),
					String.format("购买债权转让[%s]", loanTransferApplyEntity.getTransferNo()), custId);
		}

		// 13-2) 受让人分账户——>转让人分账户
		// 转让人分账户
		SubAccountInfoEntity loanerSubAccount = subAccountInfoRepository.findByRelatePrimary(wealthHoldInfoEntity.getInvestId());
		custAccountService.updateAccount(null, investorSubAccount, null,
				loanerSubAccount, "4", SubjectConstant.TRADE_FLOW_TYPE_JOIN_LOAN_TRANSFER,
				reqeustNo, tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_JOIN_LOAN_TRANSFER,
				Constant.TABLE_BAO_T_INVEST_INFO, investInfoEntity.getId(),
				String.format("购买债权转让[%s]", loanTransferApplyEntity.getTransferNo()), custId);

		// 13-3) 转让人分账户——>转让人主账户
		// 转让人主账户
		AccountInfoEntity loanerAccount = accountInfoRepository.findByCustId(wealthHoldInfoEntity.getCustId());
		custAccountService.updateAccount(null, loanerSubAccount, loanerAccount,
				null, "3", SubjectConstant.TRADE_FLOW_TYPE_JOIN_LOAN_TRANSFER,
				reqeustNo, tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_JOIN_LOAN_TRANSFER,
				Constant.TABLE_BAO_T_INVEST_INFO, investInfoEntity.getId(),
				String.format("购买债权转让[%s]", loanTransferApplyEntity.getTransferNo()), custId);

//		 13-4) 转让人主账户——>公司收益主账户
//		BigDecimal manageAmount = ArithUtil.mul(tradeAmount, paramService.findTransferRate());
		// 转让本金 = 剩余本金*交易比例
//		BigDecimal tradePrincipal = ArithUtil.mul(remainPrincipal, tradeScacle);
		// 转让费用 = 转让金额 * 0.4%
		BigDecimal transferRate = loanTransferApplyEntity.getTransferRate();
		BigDecimal manageRate = BigDecimal.ZERO;
		if(transferRate != null){
			manageRate = transferRate;
		} else {
			manageRate = paramService.findTransferRate();
		}
		BigDecimal manageAmount = ArithUtil.mul(tradePrincipal, manageRate);

		AccountInfoEntity companyAccount = accountInfoRepository.findByCustId(Constant.CUST_ID_ERAN);
		custAccountService.updateAccount(loanerAccount, null, companyAccount,
				null, "1", SubjectConstant.TRADE_FLOW_TYPE_EXPENSE_LOAN_TRANSFER,
				reqeustNo, manageAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_EXPENSE_LOAN_TRANSFER,
				Constant.TABLE_BAO_T_INVEST_INFO, investInfoEntity.getId(),
				String.format("购买债权转让[%s]", loanTransferApplyEntity.getTransferNo()), custId);

		// 14) 更新转让申请
		// 转让价值 = PV*交易比例
		BigDecimal tradeValue = ArithUtil.mul(loanValue, tradeScacle);
		loanTransferApplyEntity.setRemainderTradeScale(ArithUtil.sub(loanTransferApplyEntity.getRemainderTradeScale(), tradeScacle));
//		loanTransferApplyEntity.setTradeAmount(ArithUtil.add(loanTransferApplyEntity.getTradeAmount(), tradeAmount));
		loanTransferApplyEntity.setTradeAmount(ArithUtil.add(loanTransferApplyEntity.getTradeAmount(), ArithUtil.mul(tradeValue, loanTransferApplyEntity.getReducedScale())));
		loanTransferApplyEntity.setTradeValue(ArithUtil.add(loanTransferApplyEntity.getTradeValue(), tradeValue));
		loanTransferApplyEntity.setTradePrincipal(ArithUtil.add(loanTransferApplyEntity.getTradePrincipal(), tradePrincipal));
		loanTransferApplyEntity.setManageAmount(ArithUtil.add(loanTransferApplyEntity.getManageAmount(), manageAmount));
		loanTransferApplyEntity.setTradeTimes(loanTransferApplyEntity.getTradeTimes() + 1);
		loanTransferApplyEntity.setApplyStatus(loanTransferApplyEntity.getRemainderTradeScale().compareTo(BigDecimal.ZERO) == 0 ? Constant.LOAN_TRANSFER_APPLY_STATUS_02 : Constant.LOAN_TRANSFER_APPLY_STATUS_04);
		loanTransferApplyEntity.setBasicModelProperty(custId, false);

		// 15) 新建受让人持有债权信息
		BigDecimal totalInterest = repaymentPlanInfoRepository.sumRepaymentInterestByLoanIdAndRepaymentStatus(loanInfoEntity.getId(), Constant.REPAYMENT_STATUS_WAIT);
		WealthHoldInfoEntity investHoldInfoEntity = new WealthHoldInfoEntity();
		investHoldInfoEntity.setInvestId(investInfoEntity.getId());
		investHoldInfoEntity.setSubAccountId(investorSubAccount.getId());
		investHoldInfoEntity.setCustId(custId);
		investHoldInfoEntity.setLoanId(loanInfoEntity.getId());
		investHoldInfoEntity.setHoldScale(tradeScacle);
		investHoldInfoEntity.setHoldAmount(tradeAmount);
		investHoldInfoEntity.setTradeScale(BigDecimal.ZERO);
		investHoldInfoEntity.setExceptAmount(ArithUtil.mul(totalInterest, tradeScacle));
		investHoldInfoEntity.setReceivedAmount(BigDecimal.ZERO);
		investHoldInfoEntity.setHoldStatus(Constant.HOLD_STATUS_01);
		investHoldInfoEntity.setIsCenter(Constant.IS_CENTER_02);
		investHoldInfoEntity.setTradeScale(BigDecimal.ZERO);
		investHoldInfoEntity.setBasicModelProperty(custId, true);
		investHoldInfoEntity = wealthHoldInfoRepository.save(investHoldInfoEntity);

		// 16) 备份转让人持有债权信息
		String tradeNo = numberService.generateLoanTransferOutNo();
		WealthHoldHistoryInfoEntity wealthHoldHistoryInfoEntity = new WealthHoldHistoryInfoEntity();
		wealthHoldHistoryInfoEntity.setHoldId(wealthHoldInfoEntity.getId());
		wealthHoldHistoryInfoEntity.setInvestId(wealthHoldInfoEntity.getInvestId());
		wealthHoldHistoryInfoEntity.setSubAccountId(wealthHoldInfoEntity.getSubAccountId());
		wealthHoldHistoryInfoEntity.setCustId(wealthHoldInfoEntity.getCustId());
		wealthHoldHistoryInfoEntity.setLoanId(wealthHoldInfoEntity.getLoanId());
		wealthHoldHistoryInfoEntity.setHoldScale(wealthHoldInfoEntity.getHoldScale());
		wealthHoldHistoryInfoEntity.setHoldAmount(wealthHoldInfoEntity.getHoldAmount());
		wealthHoldHistoryInfoEntity.setExceptAmount(wealthHoldInfoEntity.getExceptAmount());
		wealthHoldHistoryInfoEntity.setReceivedAmount(wealthHoldInfoEntity.getReceivedAmount());
		wealthHoldHistoryInfoEntity.setHoldStatus(wealthHoldInfoEntity.getHoldStatus());
		wealthHoldHistoryInfoEntity.setHoldSource(wealthHoldInfoEntity.getHoldSource());
		wealthHoldHistoryInfoEntity.setIsCenter(wealthHoldInfoEntity.getIsCenter());
		wealthHoldHistoryInfoEntity.setOldCreateDate(wealthHoldInfoEntity.getCreateDate());
		wealthHoldHistoryInfoEntity.setOldCreateUser(wealthHoldInfoEntity.getCreateUser());
		wealthHoldHistoryInfoEntity.setOldLastUpdateDate(wealthHoldInfoEntity.getLastUpdateDate());
		wealthHoldHistoryInfoEntity.setOldLastUpdateUser(wealthHoldInfoEntity.getLastUpdateUser());
		wealthHoldHistoryInfoEntity.setOldMemo(wealthHoldInfoEntity.getMemo());
		wealthHoldHistoryInfoEntity.setRequestNo(reqeustNo);
		wealthHoldHistoryInfoEntity.setTradeNo(tradeNo);
		wealthHoldHistoryInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
		wealthHoldHistoryInfoRepository.save(wealthHoldHistoryInfoEntity);

		// 17) 更新转让人持有债权信息
		wealthHoldInfoEntity.setHoldScale(ArithUtil.sub(wealthHoldInfoEntity.getHoldScale(), tradeScacle));
		wealthHoldInfoEntity.setTradeScale(ArithUtil.add(wealthHoldInfoEntity.getTradeScale(), tradeScacle));
		if(wealthHoldInfoEntity.getHoldScale().compareTo(BigDecimal.ZERO) == 0) {
			wealthHoldInfoEntity.setHoldStatus(Constant.HOLD_STATUS_04);
			InvestInfoEntity senderInvestInfoEntity = investInfoRepository.findOne(wealthHoldInfoEntity.getInvestId());
			if(senderInvestInfoEntity == null) { // 投资信息为空
				throw new SLException("原始投资信息为空");
			}
			senderInvestInfoEntity.setInvestStatus(Constant.INVEST_STATUS_TRANSFER);
			senderInvestInfoEntity.setBasicModelProperty(custId, false);
		}
		wealthHoldInfoEntity.setBasicModelProperty(custId, false);

		// 18) 记录债权转让信息
		LoanTransferEntity loanTransferEntity = new LoanTransferEntity();
		loanTransferEntity.setSenderHoldId(wealthHoldInfoEntity.getId());
		loanTransferEntity.setSenderLoanId(wealthHoldInfoEntity.getLoanId());
		loanTransferEntity.setSenderCustId(wealthHoldInfoEntity.getCustId());
		loanTransferEntity.setReceiveHoldId(investHoldInfoEntity.getId());
		loanTransferEntity.setReceiveLoanId(investHoldInfoEntity.getLoanId());
		loanTransferEntity.setReceiveCustId(investHoldInfoEntity.getCustId());
		loanTransferEntity.setTransferApplyId(transferApplyId);
//		loanTransferEntity.setTradeAmount(tradeAmount);
		loanTransferEntity.setTradeAmount(ArithUtil.mul(tradeValue, loanTransferApplyEntity.getReducedScale()));
		loanTransferEntity.setTradeValue(tradeValue);
		loanTransferEntity.setTradeScale(tradeScacle);
		loanTransferEntity.setTradePrincipal(tradePrincipal);
		loanTransferEntity.setTransferExpenses(manageAmount);
		loanTransferEntity.setTradeNo(tradeNo);
		loanTransferEntity.setRequestNo(reqeustNo);
		loanTransferEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
		loanTransferRepository.save(loanTransferEntity);

		// 19) 记录操作日志
		LogInfoEntity logInfoEntity = new LogInfoEntity();
		logInfoEntity.setRelateType(Constant.TABLE_BAO_T_INVEST_INFO);
		logInfoEntity.setRelatePrimary(investInfoEntity.getId());
		logInfoEntity.setLogType(Constant.OPERATION_TYPE_81);
		logInfoEntity.setOperDesc("");
		logInfoEntity.setOperPerson(custId);
		logInfoEntity.setOperIpaddress((String)params.get("ipAddress"));
		logInfoEntity.setMemo(String.format("%s购买债权转让，投资金额%s", custInfoEntity.getLoginName(), tradeAmount.toString()));
		logInfoEntity.setBasicModelProperty(custId, true);
		logInfoEntityRepository.save(logInfoEntity);

		// 20) 记录APP信息(债权转让)
		if(params.containsKey("channelNo")) {
			Map<String, Object> deviceParams = Maps.newConcurrentMap();
			deviceParams.putAll(params);
			deviceParams.put("relateType", Constant.TABLE_BAO_T_INVEST_INFO);
			deviceParams.put("relatePrimary", investInfoEntity.getId());
			deviceParams.put("tradeType", Constant.OPERATION_TYPE_81);
			deviceParams.put("userId", custId);
			deviceService.saveUserDevice(deviceParams);
		}

		// 21) 准备短信内容
		List<Map<String, Object>> smsList = Lists.newArrayList();
		// 转让人短信、站内信
		CustInfoEntity senderCust = custInfoRepository.findOne(wealthHoldInfoEntity.getCustId());
		Map<String, Object> smsParams = new HashMap<String, Object>();
		smsParams.put("mobile", senderCust.getMobile());
		smsParams.put("custId", senderCust.getId());
		smsParams.put("messageType", Constant.SMS_TYPE_LOAN_TRASNFER);
		smsParams.put("values", new Object[] { // 短信息内容
				loanInfoEntity.getLoanTitle(),
				ArithUtil.formatScale(tradeAmount, 2).toPlainString()});
		smsParams.put("systemMessage", new Object[] { // 站内信内容
				DateUtils.formatDate(new Date(), "yyyy-MM-dd"),
				loanInfoEntity.getLoanTitle(),
				ArithUtil.formatScale(tradeAmount, 2).toPlainString()});
		smsList.add(smsParams);

		// 给原始投资人站内信
		CustInfoEntity ownerCust = custInfoRepository.findOne(loanInfoEntity.getCustId());
		Map<String, Object> ownerSmsParams = new HashMap<String, Object>();
		ownerSmsParams.put("mobile", ownerCust.getMobile()!=null?ownerCust.getMobile():"18888888888");
		ownerSmsParams.put("custId", ownerCust.getId());
		ownerSmsParams.put("messageType", Constant.SMS_TYPE_LOAN_TRASNFER_OWNER);
		ownerSmsParams.put("systemMessage", new Object[] { // 站内信内容
				DateUtils.formatDate(new Date(), "yyyy-MM-dd"),
				loanInfoEntity.getLoanTitle(),
				ArithUtil.formatScale(tradeAmount, 2).toPlainString()});
		smsList.add(ownerSmsParams);
		
		// 22) 准备向微信推送债权转让通知消息
		Map<String, Object> VXparams = new HashMap<String, Object>();
		if(senderCust.getOpenid()!=null){
			VXparams.put("openId", senderCust.getOpenid());
			VXparams.put("template_id", Constant.SMS_TYPE_VX_6);
			StringBuilder sb=new StringBuilder()
				.append(DateUtils.formatDate(new Date(), "yyyy-MM-dd"))
				.append("|")
				.append(loanInfoEntity.getLoanTitle())
				.append("|")
				.append(ArithUtil.formatScale(tradeAmount, 2).toPlainString());

			VXparams.put("data", java.net.URLEncoder.encode(sb.toString()));
			String sign=HttpRequestUtil.sign((String)VXparams.get("openId"),(String)VXparams.get("template_id"),sb.toString());
			VXparams.put("sign", sign);
			HttpRequestUtil.pushSmsToVX(VXparams);
		}
		
		// 异步生成协议
		Map<String, Object> protocalParam = Maps.newConcurrentMap();
		protocalParam.put("investId", investInfoEntity.getId());
		protocalParam.put("custId", investInfoEntity.getCustId());
		exportFileService.asynDownloadContract(protocalParam);

		return new ResultVo(true, "购买操作成功", smsList);
	}
}
