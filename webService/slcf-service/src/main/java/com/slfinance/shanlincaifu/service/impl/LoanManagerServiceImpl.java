package com.slfinance.shanlincaifu.service.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AccountInfoEntity;
import com.slfinance.shanlincaifu.entity.AssetInfoEntity;
import com.slfinance.shanlincaifu.entity.AttachmentInfoEntity;
import com.slfinance.shanlincaifu.entity.AuditInfoEntity;
import com.slfinance.shanlincaifu.entity.AutoInvestInfoEntity;
import com.slfinance.shanlincaifu.entity.AutoPublishInfoEntity;
import com.slfinance.shanlincaifu.entity.AutoTransferInfoEntity;
import com.slfinance.shanlincaifu.entity.BankCardInfoEntity;
import com.slfinance.shanlincaifu.entity.CustActivityInfoEntity;
import com.slfinance.shanlincaifu.entity.CustInfoEntity;
import com.slfinance.shanlincaifu.entity.CustRecommendInfoEntity;
import com.slfinance.shanlincaifu.entity.InterfaceDetailInfoEntity;
import com.slfinance.shanlincaifu.entity.InvestDetailInfoEntity;
import com.slfinance.shanlincaifu.entity.InvestInfoEntity;
import com.slfinance.shanlincaifu.entity.JobRunListenerEntity;
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
import com.slfinance.shanlincaifu.entity.ParamEntity;
import com.slfinance.shanlincaifu.entity.ProjectInvestInfoEntity;
import com.slfinance.shanlincaifu.entity.PurchaseAwardInfoEntity;
import com.slfinance.shanlincaifu.entity.RepaymentPlanInfoEntity;
import com.slfinance.shanlincaifu.entity.SubAccountInfoEntity;
import com.slfinance.shanlincaifu.entity.UserEntity;
import com.slfinance.shanlincaifu.entity.WealthHoldInfoEntity;
import com.slfinance.shanlincaifu.job.AutoInvestJob;
import com.slfinance.shanlincaifu.repository.AccountInfoRepository;
import com.slfinance.shanlincaifu.repository.AssetInfoRepository;
import com.slfinance.shanlincaifu.repository.AttachmentRepository;
import com.slfinance.shanlincaifu.repository.AuditInfoRepository;
import com.slfinance.shanlincaifu.repository.AuditInfoRespository;
import com.slfinance.shanlincaifu.repository.AutoInvestInfoRepository;
import com.slfinance.shanlincaifu.repository.AutoPublishInfoRepository;
import com.slfinance.shanlincaifu.repository.AutoTransferInfoRepository;
import com.slfinance.shanlincaifu.repository.CreditRightValueRepository;
import com.slfinance.shanlincaifu.repository.CustInfoRepository;
import com.slfinance.shanlincaifu.repository.CustRecommendInfoRepository;
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
import com.slfinance.shanlincaifu.repository.ProjectInvestInfoRepository;
import com.slfinance.shanlincaifu.repository.PurchaseAwardInfoRepository;
import com.slfinance.shanlincaifu.repository.RepaymentPlanInfoRepository;
import com.slfinance.shanlincaifu.repository.SubAccountInfoRepository;
import com.slfinance.shanlincaifu.repository.UserRepository;
import com.slfinance.shanlincaifu.repository.WealthHoldHistoryInfoRepository;
import com.slfinance.shanlincaifu.repository.WealthHoldInfoRepository;
import com.slfinance.shanlincaifu.repository.custom.AttachmentRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.AuditInfoRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.AutoInvestRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.InvestInfoRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.LoanInfoRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.LoanManagerRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.LoanServicePlanInfoRepositoryCustom;
import com.slfinance.shanlincaifu.service.AccountFlowService;
import com.slfinance.shanlincaifu.service.AutoPublishJobService;
import com.slfinance.shanlincaifu.service.BankCardService;
import com.slfinance.shanlincaifu.service.CustAccountService;
import com.slfinance.shanlincaifu.service.CustActivityInfoService;
import com.slfinance.shanlincaifu.service.CustActivityService;
import com.slfinance.shanlincaifu.service.CustomerService;
import com.slfinance.shanlincaifu.service.DeviceService;
import com.slfinance.shanlincaifu.service.ExpandInfoService;
import com.slfinance.shanlincaifu.service.FlowNumberService;
import com.slfinance.shanlincaifu.service.LoanManagerService;
import com.slfinance.shanlincaifu.service.LoanProjectService;
import com.slfinance.shanlincaifu.service.LoanRepaymentPlanService;
import com.slfinance.shanlincaifu.service.LogInfoService;
import com.slfinance.shanlincaifu.service.ParamService;
import com.slfinance.shanlincaifu.service.SMSService;
import com.slfinance.shanlincaifu.utils.ArithUtil;
import com.slfinance.shanlincaifu.utils.CommonUtils;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.NumberToChinese;
import com.slfinance.shanlincaifu.utils.OpenSerivceCode;
import com.slfinance.shanlincaifu.utils.SharedUtil;
import com.slfinance.shanlincaifu.utils.SubjectConstant;
import com.slfinance.spring.mail.MailType;
import com.slfinance.vo.ResultVo;

@Slf4j
@Service("loanManagerService")
public class LoanManagerServiceImpl implements LoanManagerService {
	
	@Autowired
	private LoanInfoRepositoryCustom loanInfoRepositoryCustom;
	
	@Autowired
	private InvestInfoRepositoryCustom investInfoRepositoryCustom;
	
	@Autowired
	private LoanManagerRepositoryCustom loanManagerRepositoryCustom;
	
	@Autowired
	private AuditInfoRepository auditInfoRepository;
	
	@Autowired
	private LogInfoEntityRepository logInfoEntityRepository;
	
	@Autowired
	private AccountInfoRepository accountInfoRepository;
	
	@Autowired
	private LoanInfoRepository loanInfoRepository;
	
	@Autowired
	private InvestInfoRepository investInfoRepository;
	
	@Autowired
	private ProjectInvestInfoRepository projectInvestInfoRepository;
	
	@Autowired
	private FlowNumberService numberService;
	
	@Autowired
	CustRecommendInfoRepository custRecommendInfoRepository;
	
	@Autowired
	private CustInfoRepository custInfoRepository;
	
	@Autowired
	private InvestDetailInfoRepository investDetailInfoRepository;
	
	@Autowired
	private SubAccountInfoRepository subAccountInfoRepository;
	
	@Autowired
	private AutoInvestRepositoryCustom autoInvestRepositoryCustom;
	
	@Autowired
	private AutoPublishInfoRepository autoPublishInfoRepository;
	
	@Autowired
	private AccountFlowService accountFlowService;
	
	@Autowired
	private ParamService paramService;
	
	@Autowired
	private DeviceService deviceService;
	
	@Autowired
	private InterfaceDetailInfoRepository interfaceDetailInfoRepository;
	
	@Autowired
	private ExpandInfoService expandInfoService;
	
	@Autowired
	private LoanCustInfoRepository loanCustInfoRepository;
	
	@Autowired
	private FlowNumberService flowNumberService;
	
	@Autowired
	private AttachmentRepositoryCustom attachmentRepositoryCustom;
	
	@Autowired
	private AuditInfoRepositoryCustom auditInfoRepositoryCustom;
	
	@Autowired
	private RepaymentPlanInfoRepository repaymentPlanInfoRepository;
	
	@Autowired
	private LoanProjectService loanProjectService;
	
	@Autowired
	private CustActivityInfoService custActivityInfoService;
	
	@Autowired
	private BankCardService bankCardService;
	
	@Autowired
	EmailService emailService;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	private CustAccountService custAccountService;

	@Autowired
	@Qualifier("grantThreadPoolTaskExecutor")
	Executor executor;
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private AutoPublishJobService autoPublishJobService;
	
	@Autowired
	private JobRunListenerRepository jobRunListenerRepository;
	
	@Autowired
	private LoanRebateInfoRepository loanRebateInfoRepository;
	
	@Autowired
	private LoanDetailInfoRepository loanDetailInfoRepository;
	
	@Autowired
	private AttachmentRepository attachmentRepository;
	
	@Autowired
	private AuditInfoRespository auditInfoRespository;
	
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
	private AssetInfoRepository assetInfoRespository;
	
	@Autowired
	private LoanServicePlanInfoRepository loanServicePlanInfoRespository;
	
	
	@Autowired
	private SMSService smsService;
	
	@Autowired
	private AssetInfoRepository assetInfoRepository;
	
	@Autowired
	private LoanServicePlanInfoRepositoryCustom loanServicePlanInfoRepositoryCustom;
	
	@Autowired
	private LoanServicePlanInfoRepository loanServicePlanInfoRepository;
	
	@Autowired
	private AutoInvestInfoRepository autoInvestInfoRepository;
	
	@Autowired
	private AutoInvestJob autoInvestJob;
	
	@Autowired
	private AutoTransferInfoRepository autoTransferInfoRepository;
	
	@Autowired
	InnerClass innerClass;
	
	@Autowired
	LogInfoService logInfoService;
	
	@Autowired
	PurchaseAwardInfoRepository purchaseAwardInfoRepository;
	
	private LoanManagerService self;//AOP增强后的代理对象  
	@Override
	public void setSelf(Object proxyBean) {
		self = (LoanManagerService) proxyBean;
		System.out.println("LoanManagerService = " + AopUtils.isAopProxy(this.self)); 
	}
	
	/**
	 * 业务管理列表查询
	 * 
	 * 
	 * @author zhiwen_feng
	 * @date 2016-11-29
	 * 
	 * @param params
     *      <tt>start           :String:起始值</tt><br>
     *      <tt>length          :String:长度</tt><br>
     *      <tt>loanCode        :String:借款编号(可以为空)</tt><br>
     *      <tt>custName        :String:客户姓名(可以为空)</tt><br>
     *      <tt>loanType        :String:借款类型(可以为空)</tt><br>
     *      <tt>loanStatus      :String:借款状态(可以为空)</tt><br>
     *      <tt>loanTerm        :String:还款期限(可以为空)</tt><br>
     *      <tt>repaymentMethod :String:还款方式(可以为空)</tt><br>
     *      <tt>publishDateStart:String:发布日期-区间头(可以为空)</tt><br>
     *      <tt>publishDateEnd  :String:发布日期-区间末(可以为空)</tt><br>
	 * @return
	 * @throws SLException
	 */
	@Override
	public ResultVo queryBusinessManageList(Map<String, Object> params) throws SLException {
		return new ResultVo(true, "查询成功", loanInfoRepositoryCustom.queryBusinessManageList(params));
	}

	/**
	 * 我的投资总览查询
	 * 
	 * @author zhiwen_feng
	 * @date 2016-11-29 
	 * @param params
     *      <tt>custId:String:客户ID</tt><br>
	 * @return Map<String, Object>
     *      <tt>totalDisperseAmount  :String:散标投资金额</tt><br>
     *      <tt>totalBuyCreditAmount :String:购买转让债权金额</tt><br>
     *      <tt>totalSaleCreditAmount:String:转出债权金额</tt><br>
	 * @throws SLException
	 */
	@Override
	public ResultVo queryMyTotalInvest(Map<String, Object> params) throws SLException {
		return new ResultVo(true, "查询成功", investInfoRepositoryCustom.queryMyTotalInvest(params));
	}

	/**
	 * 债权收益查询
	 * 
	 * @author zhiwen_feng
	 * @date 2016-11-29
	 * @param params
	 * 		<tt>custId:String:客户ID</tt><br>
	 * @return Map<String, Object>
     *      <tt>transferTotalValue   :String:转让总价值</tt><br>
     *      <tt>transferTotalAmount  :String:转让总金额</tt><br>
     *      <tt>transferTotalInterest:String:转让总收益</tt><br>
     *      <tt>transferTotalExpense :String:转让总费用</tt><br>
	 * @throws SLException
	 */
	@Override
	public ResultVo queryMyCreditIncome(Map<String, Object> params) throws SLException {
		String onlineTime = paramService.findTransferRefromOnlineTimeByParameterName(Constant.TRANSFERRE_FROM_ONLINE_TIME_20170531);
		params.put("onlineTime", onlineTime);
		return new ResultVo(true, "查询成功", investInfoRepositoryCustom.queryMyCreditIncome(params));
	}

	@Override
	public ResultVo queryMyDisperseList(Map<String, Object> params) throws SLException {
		String onlineTime = paramService.findTransferRefromOnlineTimeByParameterName(Constant.TRANSFERRE_FROM_ONLINE_TIME_20170531);
		params.put("onlineTime", onlineTime);
		return new ResultVo(true, "查询成功！", loanInfoRepositoryCustom.queryMyDisperseList(params));
	}

	@Override
	public ResultVo queryMyDisperseDetail(Map<String, Object> params) throws SLException {
		String onlineTime = paramService.findTransferRefromOnlineTimeByParameterName(Constant.TRANSFERRE_FROM_ONLINE_TIME_20170531);
		params.put("onlineTime", onlineTime);
		return new ResultVo(true, "查询成功！", loanInfoRepositoryCustom.queryMyDisperseDetail(params));
	}

	@Override
	public ResultVo queryMyDispersePaybackPlan(Map<String, Object> params) throws SLException {
		return new ResultVo(true, "查询成功！", loanInfoRepositoryCustom.queryMyDispersePaybackPlan(params));
	}

	/**
	 *  发布
	 *  
	 *  @author zhiwen_feng
	 *  @date 2016-11-30
	 * @param params
     *      <tt>loanId                  :String:借款信息表主键Id</tt><br>
     *      <tt>auditList               :String:List<Map<String,Object>> 审核信息</tt><br>
     *      <tt>审核ID                    :String:      auditId</tt><br>
     *      <tt>List<Map<String,Object>>:String:      attachmentList</tt><br>
     *      <tt>attachmentId            :String:         附件ID</tt><br>
     *      <tt>attachmentType          :String:         附件类型</tt><br>
     *      <tt>attachmentName          :String:         附件名称</tt><br>
     *      <tt>storagePath             :String:         存储路径</tt><br>
	 * @return ResultVo
	 * @throws SLException
	 */
	/*
	 * 1)验证散表为待发布
	 * 2）修改状态为募集中、发布时间当前时间、募集日=发布日期+募集天数
	 * 2-1）添加附件
	 * 3）通知善林商务
	 * 4）记录日志
	 * 
	 */
	@Override
	public ResultVo publishLoanInfo(Map<String, Object> params) throws SLException {
		ResultVo resultVo = innerClass.publishLoanInfo(params);
		if(ResultVo.isSuccess(resultVo)){
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>)resultVo.getValue("data");
			if((boolean)map.get("flag")){
				dispatchAutoInvest();
			}
		}
		return resultVo;
	}

	@Override
	public ResultVo forcebidders(Map<String, Object> params) throws SLException {
		
		return loanProjectService.unRelease(params);
	}

	/**
	 * 借款管理-投资记录列表查询
	 * @date 2016年11月25日下午16:25:59
	 * @author lyy
	 */
	public ResultVo queryInvestList(Map<String, Object> param) {
		BigDecimal investTotalAmount = loanManagerRepositoryCustom.queryInvestTotalAmount(param);
		
		Page<Map<String, Object>> pageVo = loanManagerRepositoryCustom.queryInvestListForPageMap(param);
		
		Map<String, Object> result = Maps.newHashMap();
		result.put("investCount", pageVo.getTotalElements());
		result.put("investTotalAmount", investTotalAmount!=null?investTotalAmount:BigDecimal.ZERO);
		result.put("iTotalDisplayRecords", pageVo.getTotalElements());
		result.put("data", pageVo.getContent());
		return new ResultVo(true, "投资记录列表查询成功", result);
	}

	/**
	 * 转让记录列表查询
	 * @date 2016年11月25日下午16:25:59
	 * @author lyy
	 */
	public ResultVo queryLoanTransferList(Map<String, Object> param) {
		String onlineTime = paramService.findTransferRefromOnlineTimeByParameterName(Constant.TRANSFERRE_FROM_ONLINE_TIME_20170531);
		param.put("onlineTime", onlineTime);
		
		Map<String, Object> totalAmount = loanManagerRepositoryCustom.queryLoanTransferTotalAmount(param);
		
		Page<Map<String, Object>> pageVo = loanManagerRepositoryCustom.queryLoanTransferListForPageMap(param);
		
		Map<String, Object> result = Maps.newHashMap();
		result.put("transferCount", totalAmount.get("transferCount"));
		result.put("transferTotalAmount", totalAmount.get("transferTotalAmount"));
		result.put("transferTotalValue", totalAmount.get("transferTotalValue"));
		result.put("iTotalDisplayRecords", pageVo.getTotalElements());
		result.put("data", pageVo.getContent());
		return new ResultVo(true, "转让记录列表查询成功", result);
	}
	
	/**
	 * 借款信息列表查询
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 */
	public ResultVo queryLoanInfoList(Map<String, Object> param) {
		
		Page<Map<String, Object>> pageVo = loanManagerRepositoryCustom.queryLoanInfoList(param);
		
		Map<String, Object> statisticsMap = loanManagerRepositoryCustom.queryLoanInfoStatistics(param);
		
		Map<String, Object> result = Maps.newHashMap();
		result.put("iTotalDisplayRecords", pageVo.getTotalElements());
		result.put("data", pageVo.getContent());
		result.put("loanInfoCount", statisticsMap.get("loanInfoCount"));
		result.put("loanInfoAmountCount", statisticsMap.get("loanInfoAmountCount")!=null?statisticsMap.get("loanInfoAmountCount"):BigDecimal.ZERO);
		return new ResultVo(true, "借款信息列表查询成功", result);
	}
	
	/**
	 * 借款信息详情
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 */
	public ResultVo queryLoanBasicInfoByLoanId(Map<String, Object> params) {
		
		List<Map<String, Object>> list = loanManagerRepositoryCustom.queryLoanBasicInfoByLoanId(params);
		
		Map<String, Object> result = null;
		if(list != null && list.size() > 0){
			result = list.get(0);
			if (new BigDecimal(result.get("awardRate").toString()).compareTo(BigDecimal.ZERO)>0) {
				result.put("yearIrr", result.get("yearIrr")+"+"+result.get("awardRate"));
			}
		}
		return new ResultVo(true, "借款信息详情查询成功", result);
	}
	
	/**
	 * 借款人信息
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 */
	public ResultVo queryLoanerInfoByLoanId(Map<String, Object> params) {
		List<Map<String, Object>> list = loanManagerRepositoryCustom.queryLoanerInfoByLoanId(params);
		
		Map<String, Object> result = Maps.newHashMap();
		if(list != null && list.size() > 0){
			result = list.get(0);
		}
		return new ResultVo(true, "借款人信息查询成功", result);
	}

	/**
	 * 散标投资精选列表ByApp
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 */
	public ResultVo queryPriorityDisperseList(Map<String, Object> params) {
		return queryDisperseList(params);
	}
	
	/**
	 * 债权转让精选列表ByApp
	 * @author  lyy
	 * @throws SLException 
	 * @date    2016-11-28 16:48:18
	 */
	public ResultVo queryPriorityCreditList(Map<String, Object> params) throws SLException {
		return queryCreditList(params);
	}

	/**
	 * 散标投资列表查询ByApp
	 * @date 2016年11月25日下午16:25:59
	 * @author lyy
	 */
	public ResultVo queryDisperseList(Map<String, Object> param) {
		String juneActivityTime = custActivityInfoService.queryJuneActivity();
		String[] juneActivityTimeArr = juneActivityTime.split(",");
		param.put("startTime", juneActivityTimeArr[0]);
		param.put("endTime", juneActivityTimeArr[1]);
		// list
		Page<Map<String, Object>> pageVo = loanManagerRepositoryCustom.queryDisperseList(param);
		
		Map<String, Object> result = Maps.newHashMap();
		result.put("iTotalDisplayRecords", pageVo.getTotalElements());
		result.put("data", pageVo.getContent());
		return new ResultVo(true, "散标投资列表查询成功", result);
	}

    @Override
    public ResultVo queryDisperseListInSpecialChannel(Map<String, Object> param) {
        String juneActivityTime = custActivityInfoService.queryJuneActivity();
        String[] juneActivityTimeArr = juneActivityTime.split(",");
        param.put("startTime", juneActivityTimeArr[0]);
        param.put("endTime", juneActivityTimeArr[1]);
        // list
        Page<Map<String, Object>> pageVo = loanManagerRepositoryCustom.queryDisperseListInSpecialChannel(param);

        Map<String, Object> result = Maps.newHashMap();
        result.put("iTotalDisplayRecords", pageVo.getTotalElements());
        result.put("data", pageVo.getContent());
        return new ResultVo(true, "散标投资列表查询成功", result);
    }

    /**
	 * 散标投资列表查询forPC
	 */
	@SuppressWarnings("unchecked")
	public ResultVo queryDisperseListForPc(Map<String, Object> param) throws SLException{
		BigDecimal start = new BigDecimal(param.get("start").toString());
		List<Map<String, Object>> totalList = new ArrayList<Map<String,Object>>();
		
		if(BigDecimal.ZERO.compareTo(start) == 0){
			Map<String, Object> creditParamMap = Maps.newHashMap();
			creditParamMap.put("start", 0);
			creditParamMap.put("length", 3);
			creditParamMap.put("orderBy", "investScale");
			creditParamMap.put("isRise", "desc");
			ResultVo creditVo = self.queryCreditList(creditParamMap);
			Map<String, Object> creditMap = (Map<String, Object>) creditVo.getValue("data");
			List<Map<String, Object>> creditList = (List<Map<String, Object>>) creditMap.get("data");
			totalList.addAll(creditList);
		}
		ResultVo loanVo = self.queryDisperseList(param);
		Map<String, Object> loanMap = (Map<String, Object>) loanVo.getValue("data");
		List<Map<String, Object>> loanList = (List<Map<String, Object>>) loanMap.get("data"); 
		
		totalList.addAll(loanList);
		Map<String, Object> result = Maps.newHashMap();
		result.put("iTotalDisplayRecords", loanMap.get("iTotalDisplayRecords"));
		result.put("data", totalList);
		return new ResultVo(true, "散标投资列表查询成功", result);
	}

    /**
     * 散标投资列表查询forPC（用于善意贷特殊渠道购买）
     * @param param
     * @return
     */
    @Override
    public ResultVo queryDisperseListForPcInSpecialChannel(Map<String, Object> param) throws SLException {
        BigDecimal start = new BigDecimal(param.get("start").toString());

        List<Map<String, Object>> totalList = new ArrayList<Map<String,Object>>();

		if (CommonUtils.isEmpty(param.get("specialUsersFlag"))) {
            if (BigDecimal.ZERO.compareTo(start) == 0) {
                Map<String, Object> creditParamMap = Maps.newHashMap();
                creditParamMap.put("start", 0);
                creditParamMap.put("length", 3);
                creditParamMap.put("orderBy", "investScale");
                creditParamMap.put("isRise", "desc");
                ResultVo creditVo = self.queryCreditListInSpecialChannel(creditParamMap);
                Map<String, Object> creditMap = (Map<String, Object>) creditVo.getValue("data");
                List<Map<String, Object>> creditList = (List<Map<String, Object>>) creditMap.get("data");
                totalList.addAll(creditList);
            }
        }        

        ResultVo loanVo = self.queryDisperseListInSpecialChannel(param);
        @SuppressWarnings("unchecked")
		Map<String, Object> loanMap = (Map<String, Object>) loanVo.getValue("data");
        @SuppressWarnings("unchecked")
		List<Map<String, Object>> loanList = (List<Map<String, Object>>) loanMap.get("data");

        totalList.addAll(loanList);
        Map<String, Object> result = Maps.newHashMap();
        result.put("iTotalDisplayRecords", loanMap.get("iTotalDisplayRecords"));
        result.put("data", totalList);
        return new ResultVo(true, "散标投资列表查询成功", result);
    }

    public List<Map<String, Object>> queryDisperseListForJob(Map<String, Object> param) {
		return loanManagerRepositoryCustom.queryDisperseListForJob(param);
	}

	/**
	 * 散标投资详情ByApp
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 */
	public ResultVo queryDisperseDetail(Map<String, Object> params) {
		List<Map<String, Object>> lists = loanManagerRepositoryCustom.queryDisperseDetail(params);
		
		Date date = new Date();
		
		Map<String, Object> result = Maps.newHashMap();;
		if(lists != null && lists.size() > 0){
			result = lists.get(0);
			result.put("nowDate", date);
		}
		
		// 新增总利息用于计算投资收益:投资收益=投资总额/借款总额×总利息
		BigDecimal totalInterest = repaymentPlanInfoRepository.sumRepaymentInterestByLoanId((String)params.get("disperseId"));
		result.put("totalInterest", totalInterest);
		
		return new ResultVo(true, "散标投资详情查询成功", result);
	}

	/**
	 * 历史借款
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 */
	public ResultVo queryLoanHisInfo(Map<String, Object> params) {
		// 申请借款、借款总额:待发布、募集中、满标复核、正常、逾期、提前结清、已到期、流标
		params.put("status", "'待发布','募集中','满标复核','正常','逾期','提前结清','已到期','流标'");
		int applyLoanCount = loanManagerRepositoryCustom.queryLoanHisInfoCount(params);
		// 成功借款：正常、逾期、提前结清、已到期
		params.put("status", "'正常','逾期','已到期','提前结清'");
		int loanedCount = loanManagerRepositoryCustom.queryLoanHisInfoCount(params);
		// 还清笔数：提前结清、已到期
		params.put("status", "'已到期','提前结清'");
		int payoffCount = loanManagerRepositoryCustom.queryLoanHisInfoCount(params);
		// 逾期笔数：逾期
		params.put("status", "'逾期'");
		int overdueCount = loanManagerRepositoryCustom.queryLoanHisInfoCount(params);
		
		// 借款总额
		params.put("status", "'待发布','募集中','满标复核','正常','逾期','提前结清','已到期','流标'");
		BigDecimal loanTotalAmount = loanManagerRepositoryCustom.queryLoanHisInfoAmount(params);
		// 待还本息：正常、逾期
		params.put("status", "'正常','逾期'");
		BigDecimal payAmount = loanManagerRepositoryCustom.queryLoanHisInfoPay(params);
		
		Map<String, Object> result = Maps.newHashMap();
		result.put("applyLoanCount", applyLoanCount);
		result.put("loanedCount", loanedCount);
		result.put("payoffCount", payoffCount);
		result.put("overdueCount", overdueCount);
		result.put("loanTotalAmount", loanTotalAmount);
		result.put("payAmount", payAmount);
		return new ResultVo(true, "历史借款查询成功", result);
	}

	/**
	 * 历史借款ByApp
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 */
	public ResultVo queryDisperseHistoryLoan(Map<String, Object> params) {
		params.put("loanId", params.get("disperseId"));
		
		// 申请借款、借款总额:待发布、募集中、满标复核、正常、逾期、提前结清、已到期、流标
		params.put("status", "'待发布','募集中','满标复核','正常','逾期','提前结清','已到期','流标'");
		int applyLoanCount = loanManagerRepositoryCustom.queryLoanHisInfoCount(params);
		// 成功借款：正常、逾期、提前结清、已到期
		params.put("status", "'正常','逾期','已到期','提前结清'");
		int loanedCount = loanManagerRepositoryCustom.queryLoanHisInfoCount(params);
		// 还清笔数：提前结清、已到期
		params.put("status", "'已到期','提前结清'");
		int payoffCount = loanManagerRepositoryCustom.queryLoanHisInfoCount(params);
		// 逾期笔数：逾期
		params.put("status", "'逾期'");
		int overdueCount = loanManagerRepositoryCustom.queryLoanHisInfoCount(params);
		// 借款总额
		params.put("status", "'待发布','募集中','满标复核','正常','逾期','提前结清','已到期','流标'");
		BigDecimal loanTotalAmount = loanManagerRepositoryCustom.queryLoanHisInfoAmount(params);
		// 待还本息：正常、逾期
		params.put("status", "'正常','逾期'");
		BigDecimal payAmount = loanManagerRepositoryCustom.queryLoanHisInfoPay(params);
		
		Map<String, Object> result = Maps.newHashMap();
		result.put("applyLoanCount", applyLoanCount);
		result.put("loanedCount", loanedCount);
		result.put("payoffCount", payoffCount);
		result.put("overdueCount", overdueCount);
		result.put("loanTotalAmount", loanTotalAmount);
		result.put("payAmount", payAmount);
		return new ResultVo(true, "历史借款查询成功", result);
	}
	
	/**
	 * 审核日志
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 */
	public ResultVo queryLoanAuditInfoByLoanId(Map<String, Object> params) {
		List<Map<String, Object>> list = loanManagerRepositoryCustom.queryLoanAuditInfoByLoanId(params);
		
		Map<String, Object> result = Maps.newHashMap();
		result.put("data", list);
		return new ResultVo(true, "审核日志查询成功", result);
	}

	/**
	 * 还款计划
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 */
	public ResultVo queryRepaymentPlanByLoanId(Map<String, Object> params) {
		List<Map<String, Object>> list = loanManagerRepositoryCustom.queryRepaymentPlanByLoanId(params);
		
		Map<String, Object> result = Maps.newHashMap();
		result.put("data", list);
		return new ResultVo(true, "还款计划查询成功", result);
	}

	/**
	 * 投资记录
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 */
	public ResultVo queryInvestInfoByLoanId(Map<String, Object> params) {
		Page<Map<String, Object>> pageVo = loanManagerRepositoryCustom.queryInvestInfoByLoanId(params);
		
		Map<String, Object> result = Maps.newHashMap();
		result.put("iTotalDisplayRecords", pageVo.getTotalElements());
		result.put("data", pageVo.getContent());
		return new ResultVo(true, "投资记录查询成功", result);
	}

	/**
	 * 转让记录
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 * @param params
     *      <tt>loanId:String:借款信息表主键Id</tt><br>
	 * @return
     *      <tt>data             :String:List<Map<String,Object>></tt><br>
     *      <tt>receiveCustName  :String:受让人</tt><br>
     *      <tt>tradeValue:String:受让债权价值（元）</tt><br>
     *      <tt>tradeAmount      :String:受让价格（元）</tt><br>
     *      <tt>senderCustName   :String:转让人</tt><br>
     *      <tt>transferDate     :String:转让日期</tt><br>
	 * @throws SLException
	 */
	public ResultVo queryLoanTransferByLoanId(Map<String, Object> params) {
		String onlineTime = paramService.findTransferRefromOnlineTimeByParameterName(Constant.TRANSFERRE_FROM_ONLINE_TIME_20170531);
		params.put("onlineTime", onlineTime);
		
		Page<Map<String, Object>> pageVo = loanManagerRepositoryCustom.queryLoanTransferByLoanId(params);
		
		Map<String, Object> result = Maps.newHashMap();
		result.put("iTotalDisplayRecords", pageVo.getTotalElements());
		result.put("data", pageVo.getContent());
		return new ResultVo(true, "转让记录查询成功", result);
	}
	
	/**
	 * 审核
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 * @param params
     *      <tt>loanId     :String:借款信息表主键Id</tt><br>
     *      <tt>userId     :String:操作人ID</tt><br>
     *      <tt>auditStatus:String:审核状态</tt><br>
     *      <tt>auditMemo  :String:审核备注</tt><br>
	 * @return
     *      ResultVo
	 * @throws SLException
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo auditLoan(Map<String, Object> params) throws SLException {
		String loanId = (String) params.get("loanId");
		String userId = (String) params.get("userId");
		String auditStatus = (String) params.get("auditStatus");
		String auditMemo = (String) params.get("auditMemo");
		// 判断审核状态
		if(!Constant.WEALTH_AUDIT_STATUS_PASS.equals(auditStatus) 
				&& !Constant.WEALTH_AUDIT_STATUS_REfUSE.equals(auditStatus) 
				&& !Constant.WEALTH_AUDIT_STATUS_FALLBACK.equals(auditStatus)){
			
			throw new SLException("更新审核状态出错，请选择正确的状态");
		}
		
		// 审核信息 ，没有建一个
		String beforeAuditStatus = "";
		// 借款信息
		LoanInfoEntity loanInfoEntity = loanInfoRepository.findOne(loanId);
		if(loanInfoEntity == null) {
			throw new SLException("项目不存在");
		}
		if(!Constant.LOAN_STATUS_01.equals(loanInfoEntity.getLoanStatus())) { // 项目非待审核状态，不允许审核
			throw new SLException("项目非待审核状态，不允许审核");
		}
		
		Date nowDate = new Date();		
		AuditInfoEntity auditInfoEntity = new AuditInfoEntity();
		auditInfoEntity.setRelateType(Constant.TABLE_BAO_T_LOAN_INFO);
		auditInfoEntity.setRelatePrimary(loanInfoEntity.getId());
		auditInfoEntity.setApplyType(Constant.OPERATION_TYPE_60);//申请类型
		auditInfoEntity.setAuditUser(userId);
		auditInfoEntity.setAuditStatus(auditStatus);
		auditInfoEntity.setAuditTime(nowDate);
		auditInfoEntity.setApplyTime(nowDate);
		auditInfoEntity.setBasicModelProperty(userId, true);
		auditInfoEntity.setMemo(auditMemo);
		auditInfoRepository.save(auditInfoEntity);

		// 处理状态设置、借款状态设置
		if(Constant.WEALTH_AUDIT_STATUS_FALLBACK.equals(auditStatus)){
			// 审核回退
			auditInfoEntity.setTradeStatus(Constant.TRADE_STATUS_04);
			
			loanInfoEntity.setLoanStatus(Constant.LOAN_STATUS_02);
		} else if(Constant.WEALTH_AUDIT_STATUS_REfUSE.equals(auditStatus)){
			// 拒绝
			auditInfoEntity.setTradeStatus(Constant.TRADE_STATUS_04);
			
			loanInfoEntity.setLoanStatus(Constant.LOAN_STATUS_14);
			
			String repaymentMethod = loanInfoEntity.getRepaymentMethod();
			Integer loanTerm = Integer.parseInt(loanInfoEntity.getLoanTerm().toString());
			String loanType =  loanInfoEntity.getLoanType();
			String loanUnit =  loanInfoEntity.getLoanUnit();
			// 判断借款类型、还款方式、还款期限在项目折年系数表中是否为商务（Flag标识），若为商务则表示需要通知。
			if(needToNotifyBiz(repaymentMethod, loanTerm, loanType, loanUnit)
					|| Constant.PUSH_FLAG_YES.equals(loanInfoEntity.getPushFlag())){
				// 待发送通知给善林商务
				InterfaceDetailInfoEntity interfaceDetailInfoEntity = 
						interfaceDetailInfoRepository.findByThirdPartyTypeInterfaceType(loanInfoEntity.getCompanyName(), Constant.NOTIFY_TYPE_LOAN_STATUS);
				if (interfaceDetailInfoEntity != null) {
					Map<String, Object> map = Maps.newHashMap();
					map.put("relateType", Constant.TABLE_BAO_T_LOAN_INFO);
					map.put("relatePrimary", loanInfoEntity.getId());
					map.put("tradeCode", numberService.generateTradeNumber());
					map.put("innerTradeCode", numberService.generateOpenServiceTradeNumber());
					map.put("interfaceDetailInfoEntity", interfaceDetailInfoEntity);
					map.put("userId", Constant.SYSTEM_USER_BACK);
					map.put("memo", Constant.OPERATION_TYPE_60);
					expandInfoService.saveExpandInfo(map);
				}
			}
		} else if (Constant.WEALTH_AUDIT_STATUS_PASS.equals(auditStatus)){
			// 通过
			auditInfoEntity.setTradeStatus(Constant.TRADE_STATUS_03);
			
			// 项目状态改为待发布
			loanInfoEntity.setLoanStatus(Constant.LOAN_STATUS_04);

			String loanCustId = loanInfoEntity.getLoanCustInfoEntity().getId();
			LoanCustInfoEntity loanCustInfoEntity = loanCustInfoRepository.findOne(loanCustId);
			
			// 生成借款客户信息
			Map<String, Object> custMap = Maps.newConcurrentMap();
			custMap.put("credentialsCode", loanCustInfoEntity.getCredentialsCode());
			custMap.put("custName", loanCustInfoEntity.getCustName());
			custMap.put("custCode", loanCustInfoEntity.getCustCode());
			custMap.put("userId", userId);
			ResultVo resultVo = customerService.createLoanCust(custMap);
			CustInfoEntity custInfo = (CustInfoEntity)resultVo.getValue("data");
			
			// 借款对应理财客户信息的id
			loanInfoEntity.setCustId(custInfo.getId());
			
			// 判断是否存在分账
			SubAccountInfoEntity loanerSubAccount = subAccountInfoRepository.findByRelatePrimary(loanInfoEntity.getId());
			if(loanerSubAccount == null) {				
				AccountInfoEntity loanerAccount = accountInfoRepository.findByCustId(custInfo.getId());
				
				// 分账
				loanerSubAccount = new SubAccountInfoEntity();
				loanerSubAccount.setBasicModelProperty(userId, true);
				loanerSubAccount.setCustId(custInfo.getId());
				loanerSubAccount.setAccountId(loanerAccount.getId());
				loanerSubAccount.setRelateType(Constant.TABLE_BAO_T_LOAN_INFO);
				loanerSubAccount.setRelatePrimary(loanInfoEntity.getId());
				loanerSubAccount.setSubAccountNo(numberService.generateCustomerNumber());
				loanerSubAccount.setAccountAmount(new BigDecimal("0"));
				loanerSubAccount.setAccountTotalValue(new BigDecimal("0"));
				loanerSubAccount.setAccountAvailableValue(new BigDecimal("0"));
				loanerSubAccount.setAccountFreezeValue(new BigDecimal("0"));
				loanerSubAccount = subAccountInfoRepository.save(loanerSubAccount);
			}
			
		}
		
		loanInfoEntity.setBasicModelProperty(userId, false);
		loanInfoEntity.setReceiveUser(userId);
		loanInfoEntity = loanInfoRepository.save(loanInfoEntity);
		
		// 日志信息
		LogInfoEntity logInfoEntity = new LogInfoEntity();
		logInfoEntity.setBasicModelProperty(userId, true);
		logInfoEntity.setRelateType(Constant.TABLE_BAO_T_LOAN_INFO);
		logInfoEntity.setRelatePrimary(loanId);
		logInfoEntity.setLogType(Constant.OPERATION_TYPE_60);
		logInfoEntity.setOperDesc(auditMemo);
		logInfoEntity.setMemo(auditMemo);
		logInfoEntity.setOperPerson(userId);
		logInfoEntity.setOperBeforeContent(beforeAuditStatus);
		logInfoEntity.setOperAfterContent(auditStatus);
		logInfoEntityRepository.save(logInfoEntity);
		
		return new ResultVo(true, "审核成功");
	}

	/**
	 * 借款人详情ByApp
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 */
	public ResultVo queryDisperseLoanUser(Map<String, Object> params) {
		List<Map<String, Object>> list = loanManagerRepositoryCustom.queryDisperseLoanUser(params);
		
		Map<String, Object> result = Maps.newHashMap();
		if(list != null && list.size() > 0){
			result = list.get(0);
		}
		return new ResultVo(true, "借款人详情查询成功", result);
	}

	/**
	 * 投资记录ByApp
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 */
	public ResultVo queryDisperseInvestRecord(Map<String, Object> params) {
		Page<Map<String, Object>> pageVo = loanManagerRepositoryCustom.queryDisperseInvestRecord(params);
		
		Map<String, Object> result = Maps.newHashMap();
		result.put("iTotalDisplayRecords", pageVo.getTotalElements());
		result.put("data", pageVo.getContent());
		return new ResultVo(true, "投资记录查询成功", result);
	}

	/**
	 * 转让记录ByApp
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 */
	public ResultVo queryDisperseTransferRecord(Map<String, Object> params) {
		params.put("loanId", params.get("disperseId"));
		Page<Map<String, Object>> pageVo = loanManagerRepositoryCustom.queryLoanTransferByLoanId(params);
		
		Map<String, Object> result = Maps.newHashMap();
		result.put("iTotalDisplayRecords", pageVo.getTotalElements());
		result.put("data", pageVo.getContent());
		return new ResultVo(true, "转让记录查询成功", result);
	}

	/**
	 * 还款计划ByApp
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 */
	public ResultVo queryDispersePaymentPlan(Map<String, Object> params) {
		List<Map<String, Object>> list = loanManagerRepositoryCustom.queryDispersePaymentPlan(params);
		
		Map<String, Object> result = Maps.newHashMap();
		result.put("data", list);
		return new ResultVo(true, "还款计划查询成功", result);
	}
	
	/**
	 * 认证信息
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 */
	public ResultVo queryAuthInfoByLoanId(Map<String, Object> params) {
		List<AuditInfoEntity> list = auditInfoRepository.findByRelatePrimaryForLoan(params.get("loanId").toString());
		
		// 查询附件信息
		Map<String, Object> requestParams = Maps.newConcurrentMap();
		List<String> auditIds = Lists.transform(list, new Function<AuditInfoEntity, String>() {
			@Override
			public String apply(AuditInfoEntity input) {
				return input.getId();
		}});
		requestParams.put("relatePrimaryList", auditIds);
		requestParams.put("showType", Constant.SHOW_TYPE_INTERNAL);
		List<Map<String, Object>> attachList = attachmentRepositoryCustom.findListByRelatePrimary(requestParams);
		
		List<Map<String, Object>> auditList = Lists.newArrayList();
		for(final AuditInfoEntity a : list) {
			Map<String, Object> auditMap = Maps.newConcurrentMap();
			auditMap.put("auditId", a.getId());
			auditMap.put("applyType", a.getApplyType());
			auditMap.put("aduitUser", a.getAuditUser());
			auditMap.put("auditDate", a.getAuditUser());
			
			Collection<Map<String, Object>> auditAttachmentList = Collections2.filter(attachList, new Predicate<Map<String, Object>>() {
				@Override
				public boolean apply(Map<String, Object> input) {
					return input.get("relatePrimary").toString().equals(a.getId());
			}});
			
			auditMap.put("attachmentList", auditAttachmentList);
			auditList.add(auditMap);
		}
		
		Map<String, Object> result = Maps.newHashMap();
		result.put("data", auditList);
		return new ResultVo(true, "查询认证信息成功", result);
	}
	
	/**
	 * 认证信息ByApp
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 */
	public ResultVo queryDisperseCheckInfo(Map<String, Object> params) {
		List<AuditInfoEntity> list = auditInfoRepository.findByRelatePrimaryForLoan(params.get("disperseId").toString());
		List<Map<String, Object>> auditList = Lists.newArrayList();
		for(AuditInfoEntity a : list) {
			Map<String, Object> map = Maps.newConcurrentMap();
			map.put("checkItem", a.getApplyType());
			map.put("checkStatus", a.getAuditStatus());
			map.put("checkDate", a.getAuditTime());
			auditList.add(map);
		}
		Map<String, Object> result = Maps.newHashMap();
		result.put("data", auditList);
		return new ResultVo(true, "还款计划查询成功", result);
	}
	
	@Override
	public ResultVo queryDisperseAttachmentList(Map<String, Object> params) {

		Map<String, Object> result = Maps.newHashMap();
		List<AuditInfoEntity> list = auditInfoRepository.findByRelatePrimaryForLoan(params.get("disperseId").toString());
		if(list == null || list.size() == 0) {
			result.put("data", Lists.newArrayList());
			return new ResultVo(true, "还款计划查询成功", result);
		}
		
		// 查询附件信息
		Map<String, Object> requestParams = Maps.newConcurrentMap();
		List<String> auditIds = Lists.transform(list, new Function<AuditInfoEntity, String>() {
			@Override
			public String apply(AuditInfoEntity input) {
				return input.getId();
		}});
		requestParams.put("relatePrimaryList", auditIds);
		requestParams.put("showType", Constant.SHOW_TYPE_EXTERNAL);
		List<Map<String, Object>> attachList = attachmentRepositoryCustom.findListByRelatePrimary(requestParams);
		
		result.put("data", attachList);
		return new ResultVo(true, "还款计划查询成功", result);
	}

	/**
	 * 购买散标投资ByApp
	 * @author  lyy
	 * @param params
     *      <tt>disperseId :String:散标主键</tt><br>
     *      <tt>custId     :String:客户ID</tt><br>
     *      <tt>tradeAmount:String:投资金额</tt><br>
     *      <tt>channelNo  :String:渠道号（可以为空）</tt><br>
     *      <tt>meId       :String:设备ID（可以为空）</tt><br>
     *      <tt>meVersion  :String:设备版本号（可以为空）</tt><br>
     *      <tt>appSource  :String:设备来源（可以为空）</tt><br>
     *      <tt>ipAddress  :String:ip地址（可以为空）</tt><br>
	 *        <tt>custActivityId	:String 客户活动ID （可以为空）</tt>
     * 投资状态：投资中、收益中、已到期、提前结清、流标
     * 借款状态：待审核、审核回退、通过、待发布、募集中、满标复核、复核通过、正常、逾期、提前结清、已到期、流标、复核拒绝、拒绝
	 */
	public ResultVo buyDispersionExt(Map<String, Object> params) throws SLException{
		ResultVo resultVo = innerClass.buyDispersionExt(params);
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> flagMap =(Map<String, Object>)resultVo.getValue("data");
			if(ResultVo.isSuccess(resultVo) && (boolean)flagMap.get("fullScale") && !(boolean)flagMap.get("isNewerFlag")){
				//当满标复核的时候调用自动发布
				dispatchAutoPublish();
			}
		} catch (Exception e) {
			log.error("调用自动发布异常");
		}
		
		return resultVo;
	}
	/**
	 * 购买散标投资ByApp
	 * @author  lyy
	 * @param params
	 *      <tt>disperseId :String:散标主键</tt><br>
	 *      <tt>custId     :String:客户ID</tt><br>
	 *      <tt>tradeAmount:String:投资金额</tt><br>
	 *      <tt>channelNo  :String:渠道号（可以为空）</tt><br>
	 *      <tt>meId       :String:设备ID（可以为空）</tt><br>
	 *      <tt>meVersion  :String:设备版本号（可以为空）</tt><br>
	 *      <tt>appSource  :String:设备来源（可以为空）</tt><br>
	 *      <tt>ipAddress  :String:ip地址（可以为空）</tt><br>
	 * 投资状态：投资中、收益中、已到期、提前结清、流标
	 * 借款状态：待审核、审核回退、通过、待发布、募集中、满标复核、复核通过、正常、逾期、提前结清、已到期、流标、复核拒绝、拒绝
	 * @date    2016-11-28 16:48:18
	 */
	public ResultVo buyDispersion(Map<String, Object> params) throws SLException{
		ResultVo resultVo = innerClass.buyDispersion(params);
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> flagMap =(Map<String, Object>)resultVo.getValue("data");
			if(ResultVo.isSuccess(resultVo) && (boolean)flagMap.get("fullScale") && !(boolean)flagMap.get("isNewerFlag")){
				//当满标复核的时候调用自动发布
				dispatchAutoPublish();
			}
		} catch (Exception e) {
			log.warn("调用自动发布异常");
		}

		return resultVo;
	}

	@Override
	public ResultVo queryMyCreditList(Map<String, Object> params) throws SLException {
		
		params.put("needHoldDay", paramService.findTransferNeedHoldDay());
		params.put("fromEndDay", paramService.findTransferFromEndDay());
		
		String onlineTime = paramService.findTransferRefromOnlineTimeByParameterName(Constant.TRANSFERRE_FROM_ONLINE_TIME_20170531);
		params.put("onlineTime", onlineTime);
		return new ResultVo(true, "查询成功", loanInfoRepositoryCustom.queryMyCreditList(params));
	}

	@Override
	public ResultVo queryMyCreditDetail(Map<String, Object> params) throws SLException {
		params.put("transferRate", paramService.findTransferRate());
		params.put("transferDay", paramService.findTransferDay());
		params.put("fromEndDay", paramService.findTransferFromEndDay());
		params.put("transferProtocalType", paramService.findTransferProtocalType());
		return new ResultVo(true, "转让详细信息查询成功", loanInfoRepositoryCustom.queryMyCreditDetail(params));
	}
	
	@Override
	public ResultVo queryMyCreditDetailForBatch(Map<String, Object> params) throws SLException {
		String holdIdsIn = (String) params.get("holdIds");
		String[] holdIdArray =  holdIdsIn.split(",");
		List<String> holdIdList = new ArrayList<String>(Arrays.asList(holdIdArray));
		
		if(holdIdList == null || holdIdList.size() ==0){
			return new ResultVo(false, "批量转让详细信息查询Id数目为零");
		}
		// 一个时前台还是原来的画面
		if(holdIdList.size() == 1){
			params.remove("holdIds");// 后面sql公用防止出错
			params.put("holdId", holdIdList.get(0));
			return self.queryMyCreditDetail(params);
		}
		params.put("holdIdList", holdIdList);//查询条件
		BigDecimal transferRate = paramService.findTransferRate();
		int transferDay = paramService.findTransferDay();
		int fromEndDay= paramService.findTransferFromEndDay();
		String transferProtocalType = paramService.findTransferProtocalType();
		// 批量的画面
		params.put("transferRate", transferRate);
		params.put("transferDay", transferDay);
		params.put("fromEndDay", fromEndDay);
		params.put("transferProtocalType", transferProtocalType);
		
		List<Map<String, Object>> list = loanInfoRepositoryCustom.queryMyCreditDetailForBatch(params);
		if(holdIdList.size() != list.size()){
			return new ResultVo(false, "批量转让详细信息查询出错，数据数目不匹配");
		}
		
		List<String> holdIds = Lists.newArrayList();
		BigDecimal remainPrincipal = BigDecimal.ZERO; // 当前债权 = 剩余本金
		// remainTerm // 剩余期限
		BigDecimal exceptRepayInterest = BigDecimal.ZERO; // 待收总利息
		BigDecimal currentInterest = BigDecimal.ZERO; // 当期收益
		BigDecimal transAmount =  BigDecimal.ZERO; // 转让金额=剩余本金*转让比例
		BigDecimal reducedAmount =  BigDecimal.ZERO; // 折价金额=转让金额*折价比例
		BigDecimal transferExpense =  BigDecimal.ZERO; // 转让费用
		BigDecimal exceptArrivePrincipal = BigDecimal.ZERO; // 预计到账本金=折价金额 - 转让费用
		BigDecimal exceptArriveInterest = BigDecimal.ZERO; // 预计到账利息=当期收益 / 当期天数 * 持有天数（不含当天）
		BigDecimal exceptArriveAmount = BigDecimal.ZERO; // 预计到账金额 =  预计到账本金  +  预计到账利息 
		
		for (Map<String, Object> map : list) {
			holdIds.add((String) map.get("holdId"));
			remainPrincipal = remainPrincipal.add((BigDecimal) map.get("remainPrincipal"));
			exceptRepayInterest = exceptRepayInterest.add((BigDecimal) map.get("exceptRepayInterest"));
			currentInterest = currentInterest.add((BigDecimal) map.get("currentInterest"));
			transAmount = transAmount.add((BigDecimal) map.get("transAmount"));
			reducedAmount = reducedAmount.add((BigDecimal) map.get("reducedAmount"));
			transferExpense = transferExpense.add((BigDecimal) map.get("transferExpense"));
			exceptArrivePrincipal = exceptArrivePrincipal.add((BigDecimal) map.get("exceptArrivePrincipal"));
			exceptArriveInterest = exceptArriveInterest.add((BigDecimal) map.get("exceptArriveInterest"));
			exceptArriveAmount = exceptArriveAmount.add((BigDecimal) map.get("exceptArriveAmount"));
		}
		
		Map<String, Object> data = Maps.newHashMap();
		data.put("size", list.size());
		data.put("tradeScale", 1);// 转让比例
		data.put("reducedScale", 1);// 折价比例
		data.put("transferRate", transferRate);
//		data.put("transferDay", transferDay);
//		data.put("fromEndDay", fromEndDay);
		data.put("protocolType", transferProtocalType);
		
		data.put("holdIds", holdIdsIn);
		data.put("remainPrincipal", remainPrincipal);
		data.put("exceptRepayInterest", exceptRepayInterest);
		data.put("currentInterest", currentInterest);
		data.put("transAmount", transAmount);
		data.put("reducedAmount", reducedAmount);
//		data.put("transferExpense", transferExpense);// 可能和总金额有差异，用加完后的总金额计算显示
//		data.put("exceptArrivePrincipal", exceptArrivePrincipal);
//		data.put("exceptArriveAmount", exceptArriveAmount);
		data.put("exceptArriveInterest", exceptArriveInterest);
		data.put("transferExpense", ArithUtil.mul(transAmount, paramService.findTransferRate()).setScale(2, BigDecimal.ROUND_DOWN)); // 用汇总的金额展示
		data.put("exceptArrivePrincipal", ArithUtil.sub(transAmount, (BigDecimal)data.get("transferExpense")));// 用汇总的金额展示
		data.put("exceptArriveAmount", ArithUtil.add((BigDecimal)data.get("exceptArrivePrincipal"), (BigDecimal)data.get("exceptArriveInterest")));// 用汇总的金额展示
		
		return new ResultVo(true, "批量转让详细信息查询成功", data);
	}

	@Override
	public ResultVo queryMyCreditPaybackPlan(Map<String, Object> params) throws SLException {
		return new ResultVo(true, loanInfoRepositoryCustom.queryMyCreditPaybackPlan(params));
	}

	/**
	 * 附件信息查询
	 * 
	 * @author zhiwen_feng
	 * @date 2016-12-01
	 * @param params
     *      <tt>loanId:String:借款信息表主键Id</tt><br>
	 * @return
     *      <tt>data          :list:List<Map<String,Object>> 审核信息</tt><br>
     *      <tt>审核ID         :String:      auditId</tt><br>
     *      <tt>审核名称                    :String:      auditName</tt><br>
     *      <tt>审核人                        :String:      auditUser</tt><br>
     *      <tt>审核时间                    :String:      auditDate</tt><br>
     *      <tt>List<Map<String,Object>>:String:      attachmentList</tt><br>
     *      <tt>attachmentId            :String:         附件ID</tt><br>
     *      <tt>attachmentType          :String:         附件类型</tt><br>
     *      <tt>attachmentName          :String:         附件名称</tt><br>
     *      <tt>storagePath             :String:         存储路径</tt><br>
	 * @throws SLException
	 */
	@Override
	public ResultVo queryAttachmentByLoanId(Map<String, Object> params) throws SLException {
		params.put("projectId", params.get("loanId"));
		List<Map<String, Object>> auditList = auditInfoRepositoryCustom.findByRelatePrimary(params);
		List<Map<String, Object>> attachList = attachmentRepositoryCustom.findAuditAttachmentInfoByLoanId(params);
		
		for(Map<String, Object> audit : auditList) {
			List<Map<String, Object>> attList = Lists.newArrayList();
			for (Map<String, Object> att : attachList) {
				if(((String) att.get("relateId")).equals((String)audit.get("auditId"))) 
					attList.add(att);
			}
			audit.put("attachmentList", attList);
		}
				
		return new ResultVo(true, "查询成功", auditList);
	}
	
	@Override
	public ResultVo queryAttachmentByLoanIdInEdit(Map<String, Object> params) throws SLException {
		params.put("projectId", params.get("loanId"));
		List<Map<String, Object>> auditList = auditInfoRepositoryCustom.findByRelatePrimary(params);
		List<Map<String, Object>> attachList = attachmentRepositoryCustom.queryAttachmentByLoanIdInEdit(params);
		
		for(Map<String, Object> audit : auditList) {
			List<Map<String, Object>> attList = Lists.newArrayList();
			for (Map<String, Object> att : attachList) {
				if(((String) att.get("relateId")).equals((String)audit.get("auditId"))) 
					attList.add(att);
			}
			audit.put("attachmentList", attList);
		}
				
		return new ResultVo(true, "查询成功", auditList);
	}

	/**
	 * 近期应还数据查询
	 * 
	 * @author zhiwen_feng
	 * @date 2016-12-01
	 * @param params
     *      <tt>start                  :String:起始值</tt><br>
     *      <tt>length                 :String:长度</tt><br>
     *      <tt>loanCode               :String:借款编号(可以为空)</tt><br>
     *      <tt>custName               :String:客户姓名(可以为空)</tt><br>
     *      <tt>repaymentMethod        :String:还款方式(可以为空)</tt><br>
     *      <tt>loanType               :String:借款类型(可以为空)</tt><br>
     *      <tt>expectRpaymentDateStart:String:应还日期-区间头(可以为空)</tt><br>
     *      <tt>expectRpaymentDateEnd  :String:应还日期-区间末(可以为空)</tt><br>
	 * @return
     *      <tt>totalCount          :String:应还款笔数</tt><br>
     *      <tt>totalAmount         :String:应还金额汇总</tt><br>
     *      <tt>data                :String:List<Map<String,Object>></tt><br>
     *      <tt>repaymentPlanId     :String:还款计划表主键Id</tt><br>
     *      <tt>loanCode            :String:借款编号</tt><br>
     *      <tt>repaymentTotalAmount:String:应还金额（元）</tt><br>
     *      <tt>expectRpaymentDate  :String:应还日期</tt><br>
     *      <tt>currentTerm         :String:当前期数   (格式“currentTerm/loanTerm”)</tt><br>
     *      <tt>yearIrr             :String:借款利率</tt><br>
     *      <tt>repaymentMethod     :String:还款方式</tt><br>
     *      <tt>loanAmount          :String:借款金额（元）</tt><br>
     *      <tt>custName            :String:客户姓名</tt><br>
     *      <tt>loanType            :String:借款类型</tt><br>
	 * @throws SLException
	 */
	@Override
	public ResultVo queryRecentlyRepaymentList(Map<String, Object> params) throws SLException {
//		if(StringUtils.isEmpty(params.get("expectRpaymentDateEnd"))) 
//			params.put("expectRpaymentDateEnd", DateUtils.formatDate(DateUtils.getAfterDay(new Date(), 30), "yyyyMMdd"));
		
		Map<String, Object> result = loanInfoRepositoryCustom.queryRecentlyRepaymentList(params);
		result.putAll(loanInfoRepositoryCustom.queryRecentlyRepaymentCount(params));
		return new ResultVo(true, "查询成功", result);
	}

	/**
	 * 逾期中数据查询
	 * 
	 * @author zhiwen_feng
	 * @date 2016-12-01
	 * @param params
     *      <tt>start                  :String:起始值</tt><br>
     *      <tt>length                 :String:长度</tt><br>
     *      <tt>loanCode               :String:借款编号(可以为空)</tt><br>
     *      <tt>custName               :String:客户姓名(可以为空)</tt><br>
     *      <tt>repaymentMethod        :String:还款方式(可以为空)</tt><br>
     *      <tt>loanType               :String:借款类型(可以为空)</tt><br>
     *      <tt>expectRpaymentDateStart:String:应还日期-区间头(可以为空)</tt><br>
     *      <tt>expectRpaymentDateEnd  :String:应还日期-区间末(可以为空)</tt><br>
	 * @return
     *      <tt>totalCount          :String:逾期期数</tt><br>
     *      <tt>totalOverdueExpense :String:逾期管理费</tt><br>
     *      <tt>totalAmount         :String:逾期金额</tt><br>
     *      <tt>data                :String:List<Map<String,Object>></tt><br>
     *      <tt>repaymentPlanId     :String:还款计划表主键Id</tt><br>
     *      <tt>loanCode            :String:借款编号</tt><br>
     *      <tt>repaymentTotalAmount:String:应还金额（元）</tt><br>
     *      <tt>expectRpaymentDate  :String:应还日期</tt><br>
     *      <tt>currentTerm         :String:应还期数   (格式“currentTerm/loanTerm”)</tt><br>
     *      <tt>overdueDays         :String:逾期天数</tt><br>
     *      <tt>overdueExpense      :String:逾期管理费</tt><br>
     *      <tt>loanAmount          :String:借款金额（元）</tt><br>
     *      <tt>custName            :String:客户姓名</tt><br>
     *      <tt>repaymentMethod     :String:还款方式</tt><br>
     *      <tt>loanType            :String:借款类型</tt><br>
	 * @throws SLException
	 */
	@Override
	public ResultVo queryOverdueDataList(Map<String, Object> params) throws SLException {
		Map<String, Object> result = loanInfoRepositoryCustom.queryOverdueDataList(params);
		result.putAll(loanInfoRepositoryCustom.queryRecentlyRepaymentCount(params));
		result.putAll(loanInfoRepositoryCustom.queryOverdueDataCount(params));
		return new ResultVo(true, "查询成功", result);
	}

	/**
	 * 还款数据查询
	 * 
	 * @author zhiwen_feng
	 * @date 2016-12-01
	 * @param params
     *      <tt>start               :String:起始值</tt><br>
     *      <tt>length              :String:长度</tt><br>
     *      <tt>loanCode            :String:借款编号(可以为空)</tt><br>
     *      <tt>custName            :String:客户姓名(可以为空)</tt><br>
     *      <tt>loanTerm            :String:借款期限(可以为空)</tt><br>
     *      <tt>repaymentMethod     :String:还款方式(可以为空)</tt><br>
     *      <tt>loanType            :String:借款类型(可以为空)</tt><br>
     *      <tt>loanStatus          :String:借款状态(可以为空)</tt><br>
     *      <tt>investStartDateStart:String:起息日期(可以为空)</tt><br>
     *      <tt>investStartDateEnd  :String:起息日期(可以为空)</tt><br>
     *      <tt>investEndDateStart  :String:到期日期</tt><br>
     *      <tt>investEndDateEnd    :String:到期日期</tt><br>
	 * @return
     *      <tt>totalCount             :String:借款笔数</tt><br>
     *      <tt>totalAmount            :String:借款金额</tt><br>
     *      <tt>totalRepaymentAmount   :String:待还本息</tt><br>
     *      <tt>totalAlreadyRepayAmount:String:已还本息</tt><br>
     *      <tt>data                   :String:List<Map<String,Object>></tt><br>
     *      <tt>repaymentPlanId        :String:还款计划表主键Id</tt><br>
     *      <tt>loanCode               :String:借款编号</tt><br>
     *      <tt>custName               :String:客户姓名</tt><br>
     *      <tt>loanType               :String:借款类型</tt><br>
     *      <tt>loanAmount             :String:借款金额（元）</tt><br>
     *      <tt>loanTerm               :String:借款期限</tt><br>
     *      <tt>yearIrr                :String:借款利率</tt><br>
     *      <tt>repaymentMethod        :String:还款方式</tt><br>
     *      <tt>loanStatus             :String:借款状态</tt><br>
     *      <tt>waitingPayment         :String:待还本息</tt><br>
     *      <tt>hasPayment             :String:已还本息</tt><br>
     *      <tt>startDate              :String:起息日期</tt><br>
     *      <tt>endDate                :String:到期日期</tt><br>
	 * @throws SLException
	 */
	@Override
	public ResultVo queryAlreadyRepayList(Map<String, Object> params) throws SLException {
		Map<String, Object> result = loanInfoRepositoryCustom.queryAlreadyRepayList(params);
		result.putAll(loanInfoRepositoryCustom.queryAlreadyRepayCount(params));
		return new ResultVo(true, "查询成功", result);
	}

	/**
	 * 财务放款列表查询
	 * 
	 * @author zhiwen_feng
	 * @date 2016-12-01
	 * @param params
     *      <tt>start             :String:起始值</tt><br>
     *      <tt>length            :String:长度</tt><br>
     *      <tt>loanCode          :String:借款编号(可以为空)</tt><br>
     *      <tt>custName          :String:客户姓名(可以为空)</tt><br>
     *      <tt>loanType          :String:借款类型(可以为空)</tt><br>
     *      <tt>lendStatus        :String:放款状态(可以为空)</tt><br>
     *      <tt>lendDateStart     :String:放款日期-区间头(可以为空)</tt><br>
     *      <tt>lendDateEnd       :String:放款日期-区间末(可以为空)</tt><br>
     *      <tt>fullScaleDateStart:String:满标日期-区间头(可以为空)</tt><br>
     *      <tt>fullScaleDateEnd  :String:满标日期-区间末(可以为空)</tt><br>
     *      <tt>reviewedDateStart :String:复核通过日期-区间头(可以为空)</tt><br>
     *      <tt>reviewedDateEnd   :String:复核通过日期-区间末(可以为空)</tt><br>
	 * @return
     *      <tt>loanCode       :String:借款编号</tt><br>
     *      <tt>custName       :String:客户姓名</tt><br>
     *      <tt>credentialsCode:String:证件号码</tt><br>
     *      <tt>loanType       :String:借款类型</tt><br>
     *      <tt>loanAmount     :String:借款金额（元）</tt><br>
     *      <tt>fullScaleDate  :String:满标日期</tt><br>
     *      <tt>reviewedDate   :String:复核通过日期</tt><br>
     *      <tt>lendDate       :String:放款日期</tt><br>
     *      <tt>lendStatus     :String:放款状态</tt><br>
     *      <tt>auditorName    :String:审核人员</tt><br>
	 * @throws SLException
	 */
	@Override
	public ResultVo queryLendMoneyList(Map<String, Object> params) throws SLException {
		return new ResultVo(true,"success", loanInfoRepositoryCustom.queryLendMoneyList(params));
	}

	/**
	 * 批量放款
	 * 
	 * @author zhiwen_feng
	 * @date 2016-12-01
	 * @param params
     *      <tt>LoadIds:String:List<String></tt><br>
     *      <tt>loanId :String:借款信息表主键Id</tt><br>	 * 
	 * @return
	 * @throws SLException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ResultVo batchLending(Map<String, Object> params) throws SLException {
		final List<String> loanIds = (List<String>) params.get("loanIds");
		if(null == loanIds || loanIds.size() == 0) 
			return new ResultVo(false, "放款数据不能为空！");
		final Map<String, Object> param = params;
		final List<Map<String, Object>> list = loanInfoRepositoryCustom.queryLoanInfobyLoanIds(params);
		
		executor.execute(new Runnable() {
			public void run() {
				Map<String, Object> resultMap = Maps.newHashMap();
				
				for(String loanId : loanIds) {
					try {
						resultMap.put(loanId, "success");
						//调用单条放款方法
						Map<String, Object> map = Maps.newHashMap();
						map.put("loanId", loanId);
						map.put("userId", param.get("userId"));
						ResultVo result = loanProjectService.notifyGrant(map);
						if(!ResultVo.isSuccess(result)) {
							resultMap.put(loanId, result.getValue("message"));
						}
					}catch (Exception e) {
						resultMap.put(loanId, e.getMessage());
						e.printStackTrace();
					}
				}
				
				String userId = (String) param.get("userId");
				String email = "";
				//发送通知邮件
				try {
					UserEntity user = userRepository.findOne((String)param.get("userId"));
					email = user.getEmail();
					Map<String, Object> smsInfo = Maps.newHashMap();
					smsInfo.put("to", email);// 收件人邮箱地址
					smsInfo.put("type", MailType.TEXT);
					smsInfo.put("title", "优选项目放款反馈");
					smsInfo.put("content", getContent(list, resultMap));
					smsInfo.put("fromNickName",Constant.FORM_PLAT_NAME);
					smsInfo.put("fromAddress",Constant.PLAT_EMAIL_ADDRESS);
					emailService.sendEmail(smsInfo);
				} catch (SLException e) {
//					e.printStackTrace();
					log.error(String.format("优选项目放款反馈邮件发送异常！[loanIds=%s，userId=%s，userEmail=%s]", loanIds, userId, email));
					LogInfoEntity log = new LogInfoEntity();
					log.setBasicModelProperty(userId, true);
					log.setRelateType("COM_T_USER");
					log.setRelatePrimary(userId);
					log.setLogType("优选项目放款反馈");
					log.setOperDesc(String.format("优选项目放款反馈邮件发送异常！[loanIds=%s，userId=%s，userEmail=%s]", loanIds, userId, email));
					log.setOperPerson(userId);
					log.setMemo("");
					logInfoService.saveLogInfo(log);
				}
			}
		});
		
		return new ResultVo(true, "系统正在批量放款， 稍后登陆邮箱查看结果");
	}
	
	private String getContent(List<Map<String, Object>> list, Map<String, Object> ids) {
		StringBuffer sb = new StringBuffer();
		sb.append("你好,放款结果如下：");
		sb.append("\n");
		sb.append("借款编号|客户姓名|证件号码|借款类型|借款金额（元）|放款结果|原因");
		for(Map<String, Object> info : list) {
			sb.append("\n");
			String message = "success".equals((String)ids.get((String) info.get("id"))) ? "成功|无" : "失败|" + (String)ids.get((String) info.get("id"));
			sb.append(info.get("loanCode") + "|"+ info.get("custName")+"|"+ info.get("credentialsCode") +"|" + info.get("loanType") + "|" + info.get("loanAmount") + "|" + message);
		}
		
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResultVo queryPriority(Map<String, Object> params)
			throws SLException {
		
		Map<String, Object> result = Maps.newHashMap();
 		
		Map<String, Object> requestParams = Maps.newHashMap();
		requestParams.put("start", 0);
		requestParams.put("length", 1);
		ResultVo resultVo = queryDisperseList(requestParams);
		Map<String, Object> data = (Map<String, Object>)resultVo.getValue("data");
		if(data == null || data.size() == 0) {
			return new ResultVo(false, "查询失败"); 
		}
		List<Map<String, Object>> dataList = (List<Map<String, Object>>)data.get("data");
		if(dataList == null || dataList.size() == 0) {
			return new ResultVo(false, "查询失败"); 
		}
		Map<String, Object> dataMap = dataList.get(0);
		result.put("productId", dataMap.get("disperseId"));
		result.put("productName", dataMap.get("loanTitle"));
		result.put("productTotalAmount", dataMap.get("loanAmount"));
		result.put("investMinAmount", dataMap.get("investMinAmount"));
		result.put("investMaxAmount", dataMap.get("investMaxAmount"));
		result.put("increaseAmount", dataMap.get("increaseAmount"));
		result.put("currUsableValue", dataMap.get("remainAmount"));
		result.put("investScale", dataMap.get("investScale"));
		result.put("typeTerm", dataMap.get("typeTerm"));
		result.put("typeUnit", dataMap.get("loanUnit"));
		result.put("yearRate", dataMap.get("yearRate"));
		result.put("awardRate", 0.0);
		result.put("incomeType", dataMap.get("repaymentMethod"));
		result.put("releaseDate", dataMap.get("publishDate"));
		result.put("endDate", new Date());
		result.put("effectDate", new Date());
		result.put("productStatus", dataMap.get("disperseStatus"));
		result.put("alreadyInvestPeoples", dataMap.get("alreadyInvestPeoples"));
		result.put("isAddYearRate", dataMap.get("isAddYearRate"));
		return new ResultVo(true, "查询成功", result);
	}
	@SuppressWarnings("unchecked")
	@Override
	public ResultVo queryPriorityNewerFlag(Map<String, Object> params)
			throws SLException {
		Map<String, Object> dataMap;
		Map<String, Object> result = Maps.newHashMap(); 
		Map<String, Object> requestParams = Maps.newHashMap();
		requestParams.put("start", 0);
		requestParams.put("length", 1);
		requestParams.put("isNewerFlag",true);
		ResultVo resultVo = queryNewerFlagList(requestParams);
		Map<String, Object> newerData = (Map<String, Object>)resultVo.getValue("data");
		List<Map<String, Object>> newDataList = (List<Map<String, Object>>) newerData.get("data");
		String custId=(String)params.get("custId");
		if ((StringUtils.isEmpty(custId) || (investInfoRepository.investCountInfoByCustId(custId).compareTo(BigDecimal.ZERO) <= 0))
				&& (newDataList != null && newDataList.size() > 0)) {
				dataMap = newDataList.get(0);
			}else{
				resultVo = queryDisperseList(requestParams);
				Map<String, Object> data = (Map<String, Object>) resultVo.getValue("data");
				if (data == null || data.size() == 0) {
					return new ResultVo(false, "查询失败");
				}
				List<Map<String, Object>> dataList = (List<Map<String, Object>>) data.get("data");
				if (dataList == null || dataList.size() == 0) {
					return new ResultVo(false, "查询失败");
				}
				dataMap = dataList.get(0);
			}

		result.put("productId", dataMap.get("disperseId"));
		result.put("productName", dataMap.get("loanTitle"));
		result.put("productTotalAmount", dataMap.get("loanAmount"));
		result.put("investMinAmount", dataMap.get("investMinAmount"));
		result.put("investMaxAmount", dataMap.get("investMaxAmount"));
		result.put("increaseAmount", dataMap.get("increaseAmount"));
		result.put("currUsableValue", dataMap.get("remainAmount"));
		result.put("investScale", dataMap.get("investScale"));
		result.put("typeTerm", dataMap.get("typeTerm"));
		result.put("typeUnit", dataMap.get("loanUnit"));
		result.put("yearRate", dataMap.get("yearRate"));
		result.put("awardRate", dataMap.get("awardRate"));
		result.put("incomeType", dataMap.get("repaymentMethod"));
		result.put("releaseDate", dataMap.get("publishDate"));
		result.put("endDate", new Date());
		result.put("effectDate", new Date());
		result.put("productStatus", dataMap.get("disperseStatus"));
		result.put("alreadyInvestPeoples", dataMap.get("alreadyInvestPeoples"));
		result.put("newerFlag", dataMap.get("newerFlag"));
		result.put("isAddYearRate", dataMap.get("isAddYearRate"));
		return new ResultVo(true, "查询成功", result);
	}
	/**
	 * 散标投资汇总
	 * 
	 * @param params
     *      <tt>custId:String:客户ID</tt><br>
	 * @return
     *      <tt>earnTotalAmount  	 :String:已赚金额</tt><br>
     *      <tt>exceptTotalAmount	 :String:待收收益</tt><br>
     *      <tt>investTotalAmount	 :String:在投金额</tt><br>
     *      <tt>tradeTotalAmount 	 :String:投资总金额</tt><br>
     *      <tt>exceptTotalPrincipal :String:待收本金</tt><br>
     *      <tt>notStatyInvestAmount :String:投资中本金</tt><br>
	 * @throws SLException
	 */
	@Override
	public ResultVo queryMyDisperseIncome(Map<String, Object> params) throws SLException {
		String onlineTime = paramService.findTransferRefromOnlineTimeByParameterName(Constant.TRANSFERRE_FROM_ONLINE_TIME_20170531);
		params.put("onlineTime", onlineTime);
		
		return new ResultVo(true, "查询成功", loanInfoRepositoryCustom.queryMyDisperseIncome(params));
	}

	/**
	 * 优选项目查询BySalesMan
	 * @author admin liyy
	 */
	public ResultVo queryAwardLoanList(Map<String, Object> params)
			throws SLException {
		Page<Map<String, Object>> pageVo = null;
		if(Constant.PRODUCT_TYPE_08.equals(params.get("productType"))){
			pageVo = loanManagerRepositoryCustom.queryDisperseList(params);
		} else if(Constant.PRODUCT_TYPE_09.equals(params.get("productType"))){
			
			pageVo = loanManagerRepositoryCustom.queryCreditList(params);
		} else {
			pageVo = new PageImpl<Map<String, Object>>(new ArrayList<Map<String, Object>>());
		}
		
		
		Map<String, Object> result = Maps.newHashMap();
		result.put("iTotalDisplayRecords", pageVo.getTotalElements());
		result.put("data", pageVo.getContent());
		return new ResultVo(true, "散标投资列表查询成功", result);
	}

	@Override
	public ResultVo queryEmployeeLoanList(Map<String, Object> params)
			throws SLException {
		String ids = (String)params.get("ids");
		String mobile = (String)params.get("mobile");
		
		CustInfoEntity custInfoEntity = custInfoRepository.findByMobile(mobile);
		
		List<String> idList = Splitter.onPattern("&").omitEmptyStrings().splitToList(ids);
		Map<String, Object> requestParam = Maps.newHashMap();
		requestParam.put("ids", idList);
		requestParam.put("productType", params.get("productType"));
		ResultVo resultVo = loanInfoRepositoryCustom.queryAllShowLoanList(requestParam);
		
		@SuppressWarnings("unchecked")
		Map<String, Object> data = (Map<String, Object>)resultVo.getValue("data");
		data.put("cust", custInfoEntity);
		return new ResultVo(true, "", data);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResultVo queryLoanContract(Map<String, Object> params)
			throws SLException {
		String loanId = (String)params.get("loanId");
		LoanInfoEntity loanInfoEntity = loanInfoRepository.findOne(loanId);
		if(loanInfoEntity == null) {
			return new ResultVo(false, "项目不存在");
		}
		if(Constant.LOAN_STATUS_05.equals(loanInfoEntity.getLoanStatus())) {
			return new ResultVo(false, "项目为募集中状态不允许");
		}
	
		Map<String, Object> result = Maps.newHashMap();
		result.put("loanCode", loanInfoEntity.getLoanCode());
		result.put("loanTitle", loanInfoEntity.getLoanTitle());
		result.put("protocolType", loanInfoEntity.getProtocalType());
		result.put("loanCustName", loanInfoEntity.getLoanCustInfoEntity().getCustName());
		result.put("loanCredentialsCode", loanInfoEntity.getLoanCustInfoEntity().getCredentialsCode());
		result.put("loanAmount", loanInfoEntity.getLoanAmount());
		result.put("incomeType", loanInfoEntity.getRepaymentMethod());
		result.put("yearRate", loanInfoEntity.getLoanDetailInfoEntity().getYearIrr());
		result.put("typeTerm", loanInfoEntity.getLoanTerm());
		result.put("effectDate", loanInfoEntity.getInvestStartDate());
		result.put("endDate", loanInfoEntity.getInvestEndDate());
		result.put("monthlyManageAmount", loanInfoEntity.getMonthlyManageAmount());
		result.put("accountManageAmount", loanInfoEntity.getPlatServiceAmount());
		result.put("loanDesc", loanInfoEntity.getLoanDesc());
		result.put("advancedRepaymentRate", loanInfoEntity.getAdvancedRepaymentRate());
		result.put("overdueRepaymentRate", loanInfoEntity.getOverdueRepaymentRate());
		result.put("grantDateYear", DateUtils.formatDate((Date)result.get("effectDate"), "yyyy"));
		result.put("grantDateMonth", DateUtils.formatDate((Date)result.get("effectDate"), "MM"));
		result.put("grantDateDay", DateUtils.formatDate((Date)result.get("effectDate"), "dd"));
		
		BigDecimal totalRepaymentInterest = repaymentPlanInfoRepository.sumRepaymentInterestByLoanIdNotAwardRate(loanId);
		result.put("totalRepaymentInterest", totalRepaymentInterest);
		
		List<Map<String, Object>> investList = investInfoRepositoryCustom.queryInvestListByLoanId(loanInfoEntity.getId());
		result.put("investorList", investList);
		if(params.get("isDownload")!=null&&(Boolean)params.get("isDownload")){
		// 验签数据
		Map<String , Object> sign = Maps.newHashMap();
		List<Map<String , Object>> signData = Lists.newArrayList();
		Map<String , Object> ourCom = Maps.newHashMap();// 平台公章
		ourCom.put("signType", "平台");
		ourCom.put("keyword", "条款的效力"); // 签署位置
		ourCom.put("offsetX", "0.06");// 签署位置偏移量
		ourCom.put("offsetY", "-0.05");// 签署位置偏移量
		signData.add(ourCom);
		if(!Constant.LOAN_PRODUCT_NAME_04.equals(loanInfoEntity.getLoanType())){// 借款类型为精英贷、善薪贷、善楼贷、善美贷
			Map<String , Object> loanPerson = Maps.newHashMap();// 借款人
			loanPerson.put("signType", "个人");
			loanPerson.put("name", loanInfoEntity.getLoanCustInfoEntity().getCustName()); // 个人印章
			loanPerson.put("idCard", loanInfoEntity.getLoanCustInfoEntity().getCredentialsCode()); // 个人印章
			loanPerson.put("keyword", loanInfoEntity.getLoanCustInfoEntity().getCustName()); // 签署位置
			loanPerson.put("offsetX", "-0.01");// 签署位置偏移量
			loanPerson.put("offsetY", "-0.02");// 签署位置偏移量
			signData.add(loanPerson);
		}
		for (Map<String, Object> invest : investList) { // 投资人
			Map<String, Object> investPerson = Maps.newHashMap();
			investPerson.put("signType", "个人");
			investPerson.put("name", invest.get("custName")); // 个人印章
			investPerson.put("idCard", invest.get("idCard")); // 个人印章
			investPerson.put("keyword", invest.get("loginName").toString()+invest.get("custName").toString());// 签署位置
			investPerson.put("offsetX", "-0.005");// 签署位置偏移量
			investPerson.put("offsetY", "-0.02");// 签署位置偏移量
			signData.add(investPerson);
		}
		sign.put("platType", "契约锁");
		sign.put("signData", signData);
		result.put("sign", sign);
		}
		
		// 对格式做特殊处理
		result.put("loanAmount", ArithUtil.formatNumber(result.get("loanAmount")));
		result.put("monthlyManageAmount", ArithUtil.formatNumber(result.get("monthlyManageAmount")));
		result.put("accountManageAmount", ArithUtil.formatNumber(result.get("accountManageAmount")));
		result.put("yearRate", ArithUtil.formatPercent2((BigDecimal)result.get("yearRate"), 1, 5));
		result.put("advancedRepaymentRate", ArithUtil.formatPercent2((BigDecimal)result.get("advancedRepaymentRate"), 1, 5));
		result.put("overdueRepaymentRate", ArithUtil.formatPercent2((BigDecimal)result.get("overdueRepaymentRate"), 1, 5));
		result.put("grantDateYear", DateUtils.formatDate((Date)result.get("effectDate"), "yyyy"));
		result.put("grantDateMonth", DateUtils.formatDate((Date)result.get("effectDate"), "MM"));
		result.put("grantDateDay", DateUtils.formatDate((Date)result.get("effectDate"), "dd"));
		result.put("effectDate", DateUtils.formatDate((Date)result.get("effectDate"), "yyyy年MM月dd日"));
		result.put("endDate", DateUtils.formatDate((Date)result.get("endDate"), "yyyy年MM月dd日"));
		result.put("totalRepaymentInterest", ArithUtil.formatNumber(result.get("totalRepaymentInterest")));
		
		for(Map<String, Object> invest : (List<Map<String, Object>>)result.get("investorList")) {
			invest.put("investAmount", ArithUtil.formatNumber(invest.get("investAmount")));
			invest.put("investDate", DateUtils.formatDate(DateUtils.parseDate((String)invest.get("investDate"), "yyyyMMdd"), "yyyy年MM月dd日"));
//			invest.put("custName", SharedUtil.replaceSpecialWord((String)invest.get("custName")));// 出借人签名不要脱敏
			invest.put("idCard", SharedUtil.replaceSpecialWord((String)invest.get("idCard")));
		}

		return new ResultVo(true, "查询协议成功", result);
	}
	
	@Override
	public ResultVo findByLoanCode(String loanCode) {
		LoanInfoEntity loanInfoEntity = loanInfoRepository.findByLoanCode(loanCode);
		if(loanInfoEntity == null) {
			return new ResultVo(false, "项目不存在");
		}
		return new ResultVo(true, "查询债权成功", loanInfoEntity);
	}
	
	public Map<String, Object> getGrantTimerStatus(Map<String, Object> params) throws SLException{
		JobRunListenerEntity jobRunListenerEntity = jobRunListenerRepository.findByJobName(Constant.JOB_NAME_LOAN_GRANT);
		if(jobRunListenerEntity == null) {
			throw new SLException(String.format("暂未设置%s定时任务，请先设置", Constant.JOB_NAME_LOAN_GRANT));
		}
		String status = "";
		if(Constant.VALID_STATUS_VALID.equals(jobRunListenerEntity.getRecordStatus())) {
			status = Constant.ENABLE_STATUS_QY;
		}
		else if(Constant.VALID_STATUS_INVALID.equals(jobRunListenerEntity.getRecordStatus())) {
			status = Constant.ENABLE_STATUS_TY;
		}
		
		Map<String, Object> result = Maps.newHashMap();
		result.put("status", status);
		return result;
	}

	@Transactional(readOnly = false, rollbackFor=SLException.class)
	@Override
	public ResultVo startGrantTimer(Map<String, Object> params)
			throws SLException {
		String userId = (String)params.get("userId");
		String status = (String)params.get("status");
		JobRunListenerEntity jobRunListenerEntity = jobRunListenerRepository.findByJobName(Constant.JOB_NAME_LOAN_GRANT);
		if(jobRunListenerEntity == null) {
			return new ResultVo(false, String.format("暂未设置%s定时任务，请先设置", Constant.JOB_NAME_LOAN_GRANT));
		}
		if(Constant.ENABLE_STATUS_QY.equals(status)) {
			jobRunListenerEntity.setRecordStatus(Constant.VALID_STATUS_VALID);
		}
		else if(Constant.ENABLE_STATUS_TY.equals(status)) {
			jobRunListenerEntity.setRecordStatus(Constant.VALID_STATUS_INVALID);
		}
		jobRunListenerEntity.setBasicModelProperty(userId, false);
		return new ResultVo(true, "操作成功");
	}
	
	/**
	 * 判断借款类型、还款方式、还款期限在项目折年系数表中是否为商务（Flag标识），若为商务则表示需要通知。
	 */
	public boolean needToNotifyBiz(String repaymentMethod, Integer loanTerm, String loanType, String loanUnit){
		LoanRebateInfoEntity loanRebateInfoEntity = loanRebateInfoRepository.findByRepaymentMethodAndLoanTermAndProductTypeAndLoanUnit(repaymentMethod, loanTerm, loanType, loanUnit);
		if(loanRebateInfoEntity != null && Constant.LOAN_FLAG_01.equals(loanRebateInfoEntity.getFlag())) {
			return true;
		}
		return false;
	}
	
	/**
	 * 企业借款-借款类型
	 * @author  liyy
	 * @date    2016年12月19日 上午11:28:10
	 * @param params
	 * 		<tt>status    :type:类型（传企业）</tt>><br>
	 * @return
	 * @throws SLException
	 */
	public Map<String, Object> queryLoanTypeList(Map<String, Object> params)
			throws SLException {
		Map<String, Object> resultMap = Maps.newHashMap();
		List<Map<String, Object>> list = loanRebateInfoRepository.queryLoanTypeList((String) params.get("type"));
		resultMap.put("paramList", list);
		return resultMap;
	}

	/**
	 * 保存企业借款
	 * @author  liyy
	 * @date    2016年12月19日 上午11:28:10
	 * @param params Map < String, Object > 
	 * @return ResultVo
	 * @throws SLException
	 */
	@Transactional(readOnly = false, rollbackFor=SLException.class)
	public ResultVo saveLoan(Map<String, Object> params) throws SLException {
		final String loanId = (String) params.get("loanId");
		final String userId = (String) params.get("userId");
		final String loanCode = (String) params.get("loanCode");
		final String custId = (String) params.get("custId");
		String repaymentMethod = (String)params.get("repaymentMethod");
		final Integer loanTerm = Integer.parseInt(params.get("loanTerm").toString());
		final String loanType = (String)params.get("loanType");
		final Integer seatTerm = Integer.parseInt(params.get("seatTerm").toString());
		final String loanUnit = (String) params.get("loanUnit");
		final String newerFlag = (String)params.get("newerFlag");
		final String loanAmount = (String)params.get("loanAmount");
		final String investMinAmount = (String)params.get("investMinAmount");
		final String investMaxAmount = (String)params.get("investMaxAmount");
		final String increaseAmount=(String)params.get("increaseAmount");
		String manageExpenseDealType = params.get("manageExpenseDealType").toString();//服务费扣款方式：线下、期初、期末、期初期末
		if(!ArithUtil.isDivInt(new BigDecimal(increaseAmount),new BigDecimal(investMinAmount))){
			return new ResultVo(false, "起投金额必须是递增金额的整数倍！");
		}
		// update by fengyl 2017/04/11 新手标优化判断
		if(Constant.LOAN_INFO_NEWER_FLAG.equals(newerFlag)){
			if(!StringUtils.isEmpty(investMaxAmount)&&new BigDecimal(investMaxAmount).compareTo(BigDecimal.ZERO)>0){
				if(new BigDecimal(investMinAmount).compareTo(new BigDecimal(investMaxAmount))>0){
					return new ResultVo(false, "投资限额必须大于等于起投金额！");
				}
				if(new BigDecimal(investMaxAmount).compareTo(new BigDecimal(loanAmount))>0){
					return new ResultVo(false, "投资限额必须小于等于借款金额！");
				}
				if(new BigDecimal(investMaxAmount).compareTo(BigDecimal.ZERO)>0 && !ArithUtil.isDivInt(new BigDecimal(increaseAmount), ArithUtil.sub(new BigDecimal(investMaxAmount), new BigDecimal(investMinAmount)))){
					return new ResultVo(false, "投资限额必须是起投金额加递增金额整数倍！");
				}
			}else{
				return new ResultVo(false, "投标限额不能为空！");
			}
		}

		// update by liyy 2017/03/25 新增天标判断条件
		if(Constant.LOAN_UNIT_DAY.equals(loanUnit)){
			if(!Constant.LOAN_PRODUCT_NAME_01.equals(loanType) && !Constant.LOAN_PRODUCT_NAME_03.equals(loanType)){
				return new ResultVo(false, "目前仅善转贷、善意贷可以选择天标！");
			}
			if(!Constant.REPAYMENT_METHOD_03.equals(repaymentMethod)){
				return new ResultVo(false, "目前天标仅可以选择：到期还本付息！");
			}
			if( seatTerm != -1){
				return new ResultVo(false, "目前天标仅可以选择：不可转让！");
			}
			try {
				boolean retBoolean = true;
				Map<String, Object> paramP = Maps.newHashMap();
				paramP.put("type", "projectTermDay");
				Map<String, Object> paramResult = paramService.findByParamType(paramP);
				@SuppressWarnings("unchecked")
				List<ParamEntity> paramList = (List<ParamEntity>) paramResult.get("paramList");
				for(ParamEntity entity : paramList){
					if(loanTerm == Integer.parseInt(entity.getValue())) {
						retBoolean = false;
						break ;
					}
				}
				if(retBoolean){
					return new ResultVo(false, "目前天标仅可以选择:7天、10天、14天、15天、28天、30天、45天、60天、90天!");
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.error("天标参数取得异常："+e.getMessage());
				return new ResultVo(false, "天标参数取得异常！");
			}
		}
		
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> auditMapList = (List<Map<String, Object>>)params.get("auditList");
		
		if(!Constant.LOAN_PRODUCT_NAME_01.equals(loanType) && !Constant.LOAN_PRODUCT_NAME_03.equals(loanType)){
			if(auditMapList == null || auditMapList.size() == 0) {
				return new ResultVo(false, "附件不能为空！");
			}
		}
		
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> assetList = (List<Map<String, Object>>)params.get("assetList");
		if (Constant.LOAN_PRODUCT_NAME_01.equals(loanType) || Constant.LOAN_PRODUCT_NAME_03.equals(loanType)) {
			if (assetList == null || assetList.size() == 0) {
				return new ResultVo(false, "债权文件附件不能为空！");
			}
			
			String currentDateStr;
			if(Constant.LOAN_UNIT_DAY.equals(loanUnit)){
				currentDateStr = DateUtils.formatDate(DateUtils.getAfterDay(new Date(), loanTerm),("yyyy-MM-dd"));
			} else {
				currentDateStr = DateUtils.formatDate(DateUtils.getAfterMonth(new Date(), loanTerm),("yyyy-MM-dd"));
			}
			
			BigDecimal sumLoanAmount = BigDecimal.ZERO;
			for (Map<String, Object> m : assetList) {
				try {
					sumLoanAmount = ArithUtil.add(sumLoanAmount,
							new BigDecimal(m.get("loanAmount").toString()));
					try {
						// com.slfinance.shanlincaifu.utils.DateUtils.parseDate  2017-02-8s 能正常解析
						Date investEndDate = org.apache.commons.lang3.time.DateUtils.parseDate((String) m.get("investEndDate"), "yyyy-MM-dd");
						String investEndDateStr = DateUtils.formatDate(investEndDate, "yyyy-MM-dd");
						if (currentDateStr.compareTo(investEndDateStr) > 0) {
							return new ResultVo(false, "校验上传的债权期限应大于等于借款期限！");
						}
					} catch (ParseException e) {
						//  e.printStackTrace(); 
						return new ResultVo(false, "借款到期日格式不正确！");
					}
				} catch (NumberFormatException e) { 
					return new ResultVo(false, "借款金额格式不正确！");
				}
			}
			if (sumLoanAmount.compareTo(new BigDecimal(params.get("loanAmount")
					.toString())) != 0) {
				return new ResultVo(false, "校验上传的债权金额总和与借款金额不一致！");
			}

		}
		// 验证还款方式、期限是否在我们折扣范围内
		LoanRebateInfoEntity loanRebateInfoEntity = loanRebateInfoRepository.findByRepaymentMethodAndLoanTermAndProductTypeAndLoanUnit(repaymentMethod, loanTerm, loanType, loanUnit);
		if(loanRebateInfoEntity == null) {
			return new ResultVo(false, "还款方式和借款期限不在折价系数范围内", OpenSerivceCode.ERR_OTHER);
		}
		// 商户信息有没有
		CustInfoEntity custInfo = custInfoRepository.findOne(custId);
		if(custInfo == null){
			return new ResultVo(false, "客户信息不存在!", "saveLoan:客户信息不存在!");
		}
		BankCardInfoEntity bankCardInfo = null;
		List<BankCardInfoEntity> bankCardInfoList = custInfo.getBankCardInfoEntitys();
		if(bankCardInfoList !=null && bankCardInfoList.size() > 0){
			bankCardInfo = bankCardInfoList.get(0);
		}
		
		Date date = new Date();
		LoanCustInfoEntity loanCustInfo = null;
		LoanInfoEntity loanInfo = null;
		LoanDetailInfoEntity loanDetailInfo = null;
		if(StringUtils.isEmpty(loanId)){// 新增
			// 验证借款编号是否存在
			LoanInfoEntity loanInfoError = loanInfoRepository.findByLoanCode(loanCode);
			if(loanInfoError != null) {
				return new ResultVo(false, "借款编号重复", OpenSerivceCode.ERR_LOAN_NO_REPEATE);
			}
			// 借款客户信息
			loanCustInfo = new LoanCustInfoEntity();
			loanCustInfo.setCustName(custInfo.getCustName());
			loanCustInfo.setCredentialsType(custInfo.getCredentialsType());
			loanCustInfo.setCredentialsCode(custInfo.getCredentialsCode());
			loanCustInfo.setBankName(bankCardInfo!=null?bankCardInfo.getBankName():"");
			loanCustInfo.setCardNo(custInfo.getCredentialsCode());
			loanCustInfo.setMobile(custInfo.getMobile());
			loanCustInfo.setCustCode(custInfo.getCustCode());
			loanCustInfo.setWorkCorporation(custInfo.getCustName());
			loanCustInfo.setWorkAddress(custInfo.getCommunAddress());
			
			// 借款信息
			loanInfo = new LoanInfoEntity();
			loanInfo.setBasicModelProperty(userId, true);
			// 借款详细信息
			loanDetailInfo = new LoanDetailInfoEntity();
			loanDetailInfo.setBasicModelProperty(userId, true);
		}else {// 编辑
			// 借款信息
			loanInfo = loanInfoRepository.findOne(loanId);
			if(loanInfo == null){throw new SLException("借款信息出错！");}
			loanInfo.setBasicModelProperty(userId, false);
			loanDetailInfo = loanInfo.getLoanDetailInfoEntity();
			loanDetailInfo.setBasicModelProperty(userId, false);
			loanCustInfo = loanInfo.getLoanCustInfoEntity();
		}
		//再判断是否有附件
		if(!Constant.LOAN_PRODUCT_NAME_01.equals(loanType) && !Constant.LOAN_PRODUCT_NAME_03.equals(loanType)){
			loanInfo.setAttachmentFlag(Constant.ATTACHMENT_FLAG_01);
		}else {
			loanInfo.setAttachmentFlag(Constant.ATTACHMENT_FLAG_03);
		}
		// 借款信息
		loanInfo.setRelateType(Constant.TABLE_BAO_T_LOAN_CUST_INFO);
		loanInfo.setLoanCustInfoEntity(loanCustInfo);
		loanInfo.setDebtSourceCode(Constant.DEBT_SOURCE_CODE_SLCF);
		loanInfo.setLoanCode(params.get("loanCode").toString());
		loanInfo.setCreditAcctStatus(Constant.CREDIT_ACCT_STATUS_01);
		loanInfo.setLoanTerm(Long.parseLong(params.get("loanTerm").toString()));
		loanInfo.setGrantDate(null);
		loanInfo.setImportDate(DateUtils.getDateToTimeStamp(date));
		loanInfo.setInvestStartDate(null);// 到设置
		loanInfo.setInvestEndDate(null);// 到设置
		loanInfo.setRepaymentDay(null);
		loanInfo.setLoanAmount(new BigDecimal(loanAmount));
		loanInfo.setLoanDesc(params.get("loanDesc").toString().replaceAll("\\s*", ""));
		loanInfo.setLoanTitle(params.get("loanDesc").toString().replaceAll("\\s*", ""));
		loanInfo.setHoldAmount(new BigDecimal(loanAmount));
		loanInfo.setHoldScale(BigDecimal.ONE);
		loanInfo.setRepaymentMethod(params.get("repaymentMethod").toString());
		loanInfo.setRepaymentCycle(null);
		loanInfo.setAssetTypeCode(Constant.ASSET_TYPE_CODE_01);
		loanInfo.setLoanStatus(Constant.LOAN_STATUS_01);
		loanInfo.setPublishDate(null);
		loanInfo.setRasieEndDate(DateUtils.nextPayDate(date, params.get("rasieDays").toString()));
		loanInfo.setInvestMinAmount(new BigDecimal(investMinAmount));
		if(!StringUtils.isEmpty(investMaxAmount)){
		loanInfo.setInvestMaxAmount(new BigDecimal(investMaxAmount));
		}else{
			loanInfo.setInvestMaxAmount(new BigDecimal(Constant.INVEST_MAX_AMOUNT));
		}
		loanInfo.setIncreaseAmount(new BigDecimal(increaseAmount));
		loanInfo.setCustId(params.get("custId").toString());
		loanInfo.setLoanType(params.get("loanType").toString());

		loanInfo.setRasieDays(Integer.parseInt(params.get("rasieDays").toString()));
		loanInfo.setLoanUnit(loanUnit);
		loanInfo.setRebateRatio(loanRebateInfoEntity.getRebateRatio());
		loanInfo.setCompanyName(null);
		loanInfo.setApplyTime(date);
		loanInfo.setGrantStatus(Constant.GRANT_STATUS_01);
		loanInfo.setManageRate(ArithUtil.div(new BigDecimal(params.get("manageRate").toString()), new BigDecimal(100)));
		loanInfo.setSeatTerm(seatTerm);
		loanInfo.setNewerFlag(newerFlag);//新手标
		loanInfo.setManageExpenseDealType(manageExpenseDealType);
		loanInfo.setGrantType(params.get("grantType").toString());//放款方式：放款即生效,到帐即生效
		
		if(Constant.MANAGE_EXPENSE_DEAL_TYPE_01.equals(manageExpenseDealType)){
			//服务费计算公式：借款金额*年服务费率*借款期限/12
			//天标服务费服务费计算公式：借款金额*年服务费率*天标期限/360
			loanInfo.setMonthlyManageRate(BigDecimal.ZERO);
			loanInfo.setPlatServiceAmount(BigDecimal.ZERO);
		}else if(Constant.MANAGE_EXPENSE_DEAL_TYPE_02.equals(manageExpenseDealType)){
			loanInfo.setMonthlyManageRate(BigDecimal.ZERO);
			loanInfo.setPlatServiceAmount(ArithUtil.mul(new BigDecimal(params.get("loanAmount").toString())
			, ArithUtil.mul(ArithUtil.div(new BigDecimal(params.get("manageRate").toString())
										, new BigDecimal(Constant.STRING_100))
							, ArithUtil.div(new BigDecimal(loanTerm)
											, Constant.LOAN_UNIT_DAY.equals(loanUnit) 
												? new BigDecimal(Constant.DAYS_OF_YEAR) 
												: new BigDecimal(Constant.MONTH_OF_YEAR)))));
		}else if(Constant.MANAGE_EXPENSE_DEAL_TYPE_03.equals(manageExpenseDealType)){
			loanInfo.setMonthlyManageRate(ArithUtil.div(ArithUtil.div(new BigDecimal(params.get("manageRate").toString()), new BigDecimal(Constant.STRING_100)), new BigDecimal(Constant.MONTH_OF_YEAR)));
			loanInfo.setPlatServiceAmount(BigDecimal.ZERO);
		}	
		
		if(Constant.LOAN_PRODUCT_NAME_01.equals(loanType) || Constant.LOAN_PRODUCT_NAME_03.equals(loanType)){
			loanInfo.setServiceName((String)params.get("serviceName"));
			loanInfo.setServiceCode((String)params.get("serviceCode"));
//			//服务费计算公式：借款金额*年服务费率*借款期限/12
//			//天标服务费服务费计算公式：借款金额*年服务费率*天标期限/360
//			loanInfo.setMonthlyManageRate(BigDecimal.ZERO);
//			loanInfo.setPlatServiceAmount(ArithUtil.mul(new BigDecimal(params.get("loanAmount").toString())
//														, ArithUtil.mul(ArithUtil.div(new BigDecimal(params.get("manageRate").toString())
//																					, new BigDecimal(Constant.STRING_100))
//																		, ArithUtil.div(new BigDecimal(loanTerm)
//																						, Constant.LOAN_UNIT_DAY.equals(loanUnit) 
//																							? new BigDecimal(Constant.DAYS_OF_YEAR) 
//																							: new BigDecimal(Constant.MONTH_OF_YEAR)))));
//		}else if(Constant.LOAN_PRODUCT_NAME_02.equals(loanType)){
//			loanInfo.setMonthlyManageRate(BigDecimal.ZERO);
//			//服务费计算公式：借款金额*年服务费率*借款期限/12
//			loanInfo.setPlatServiceAmount(ArithUtil.mul(new BigDecimal(params.get("loanAmount").toString())
//														, ArithUtil.mul(ArithUtil.div(new BigDecimal(params.get("manageRate").toString())
//																					, new BigDecimal(100))
//																		, ArithUtil.div(new BigDecimal(loanTerm), new BigDecimal("12")))));
//		}else{
//			loanInfo.setMonthlyManageRate(ArithUtil.div(ArithUtil.div(new BigDecimal(params.get("manageRate").toString()), new BigDecimal("100")), new BigDecimal("12")));
		}
		loanInfo.setAdvancedRepaymentRate(new BigDecimal("0.05"));
		loanInfo.setOverdueRepaymentRate(new BigDecimal("0.001"));
		loanInfo.setAwardRate(BigDecimal.ZERO);
		loanInfo.setLoanInfo(params.get("loanInfo").toString());
		loanInfo.setProtocalType(params.get("protocolType").toString());
		loanInfo.setReceiveStatus(Constant.RECEIVE_STATUS_02);
		if(StringUtils.isEmpty(params.get("isAllowAutoInvest"))){
			loanInfo.setIsAllowAutoInvest("是");
		}else{
			loanInfo.setIsAllowAutoInvest(params.get("isAllowAutoInvest").toString());
		}

		/** 2017-7-13 update by mali 保存是否用于渠道 */
        if (StringUtils.isEmpty(params.get("channelFlag"))) {
            /*if (Constant.LOAN_PRODUCT_NAME_03.equals(CommonUtils.emptyToString(params.get("loanType")))) {
                loanInfo.setChannelFlag("是");
            } else {
                loanInfo.setChannelFlag("否");
            }*/
            loanInfo.setChannelFlag("否");
        } else {
            loanInfo.setChannelFlag(CommonUtils.emptyToString(params.get("channelFlag")));
        }
//        loanInfo.setChannelFlag(CommonUtils.emptyToString(params.get("channelFlag")));
        if (StringUtils.isEmpty(params.get("specialUsersFlag"))) {
            /*if (Constant.LOAN_PRODUCT_NAME_03.equals(CommonUtils.emptyToString(params.get("loanType")))) {
                loanInfo.setChannelFlag("是");
            } else {
                loanInfo.setChannelFlag("否");
            }*/
            loanInfo.setSpecialUsersFlag("否");
        } else {
            loanInfo.setSpecialUsersFlag(CommonUtils.emptyToString(params.get("specialUsersFlag")));
        }

		loanInfo = loanInfoRepository.save(loanInfo);
		// 详细信息
		loanDetailInfo.setLoanInfoEntity(loanInfo);
		loanDetailInfo.setAlreadyPaymentTerm(BigDecimal.ZERO);
		loanDetailInfo.setCurrTerm(new BigDecimal("1"));
		loanDetailInfo.setCreditRemainderPrincipal(new BigDecimal(params.get("loanAmount").toString()));
		loanDetailInfo.setWealthRemainderPrincipal(BigDecimal.ZERO);
		loanDetailInfo.setExecPvStatus(Constant.EXEC_UN_STATUS);
		loanDetailInfo.setCreditRightStatus(Constant.CREDIT_RIGHT_STATUS_NORMAL);
		loanDetailInfo.setYearIrr(ArithUtil.div(new BigDecimal(params.get("yearRate").toString()), new BigDecimal(Constant.STRING_100)));
		loanDetailInfo = loanDetailInfoRepository.save(loanDetailInfo);
		// 编辑时先删除再新增
		// 非善转贷 保存审核信息
		// 善转贷 保存资产信息
		if (!StringUtils.isEmpty(loanId)) {
			if (!Constant.LOAN_PRODUCT_NAME_01.equals(loanType) && !Constant.LOAN_PRODUCT_NAME_03.equals(loanType)) {
				List<AuditInfoEntity> oldAuditList = auditInfoRespository.findLoanByRelatePrimary(loanId);
				for (AuditInfoEntity audit : oldAuditList) {
					String auditId = audit.getId();
					attachmentRepository.deleteByRelatePrimary(auditId);
					auditInfoRespository.delete(audit);
				}
			} else {
				List<AssetInfoEntity> assetInfoList = assetInfoRepository.findLoanByLoanId(loanId);
				for (AssetInfoEntity asset : assetInfoList) {
					String assetId = asset.getId();
					assetInfoRepository.delete(assetId);
				}
			}
		}

		Map<String, AuditInfoEntity> auditMap = Maps.newHashMap();
		List<AttachmentInfoEntity> attachmentList = Lists.newArrayList();
		if(!Constant.LOAN_PRODUCT_NAME_01.equals(loanType) && !Constant.LOAN_PRODUCT_NAME_03.equals(loanType)){
		for(Map<String, Object> m : auditMapList) {
			
			AuditInfoEntity auditInfoEntity = null;
			if(auditMap.containsKey((String)m.get("auditType"))) {
				auditInfoEntity = (AuditInfoEntity)auditMap.get((String)m.get("auditType"));
			}
			else {
				auditInfoEntity = new AuditInfoEntity();
				auditInfoEntity.setRelateType(Constant.TABLE_BAO_T_LOAN_INFO);
				auditInfoEntity.setRelatePrimary(loanInfo.getId());
				auditInfoEntity.setApplyType((String)m.get("auditType"));
				auditInfoEntity.setApplyTime(DateUtils.getDateToTimeStamp(date));
				auditInfoEntity.setAuditTime(DateUtils.getDateToTimeStamp(date));
				if(!StringUtils.isEmpty((String)m.get("auditUser"))) {
					auditInfoEntity.setAuditUser((String)m.get("auditUser"));
				}
				auditInfoEntity.setTradeStatus(Constant.TRADE_STATUS_03);
				auditInfoEntity.setAuditStatus(Constant.AUDIT_STATUS_PASS);
				auditInfoEntity.setBasicModelProperty(userId, true);
				auditInfoEntity = auditInfoRespository.save(auditInfoEntity);
				
				auditMap.put((String)m.get("auditType"), auditInfoEntity);
			}
			
			AttachmentInfoEntity attachmentInfoEntity = new AttachmentInfoEntity();
			attachmentInfoEntity.setRelateType(Constant.TABLE_BAO_T_AUDIT_INFO);
			attachmentInfoEntity.setRelatePrimary(auditInfoEntity.getId());
			attachmentInfoEntity.setAttachmentType((String)m.get("auditType"));
			attachmentInfoEntity.setAttachmentName((String)m.get("fileName"));
			attachmentInfoEntity.setStoragePath((String)m.get("filePath"));
			attachmentInfoEntity.setShowType(Constant.SHOW_TYPE_INTERNAL);// 附件仅供内部使用
			attachmentInfoEntity.setBasicModelProperty(userId, true);
			attachmentList.add(attachmentInfoEntity);
		}
		attachmentRepository.save(attachmentList);
		}
		// 存储资产信息
		if (Constant.LOAN_PRODUCT_NAME_01.equals(loanType) || Constant.LOAN_PRODUCT_NAME_03.equals(loanType)) {
			for (Map<String, Object> m : assetList) {
				AssetInfoEntity assetInfoEntity = new AssetInfoEntity();
				assetInfoEntity.setLoanId(loanInfo.getId());
				assetInfoEntity.setCredentialsType(Constant.CREDENTIALS_ID_CARD);
				assetInfoEntity.setLoanTerm(loanTerm);
				assetInfoEntity.setLoanDesc((String) params.get("loanDesc"));
				assetInfoEntity.setCustName((String) m.get("custName"));
				assetInfoEntity.setCredentialsCode((String) m.get("credentialsCode"));
				assetInfoEntity.setLoanAmount(new BigDecimal(m.get("loanAmount").toString()));
				assetInfoEntity.setInvestEndDate(DateUtils.parseDate((String) m.get("investEndDate"), "yyyy-MM-dd"));
				assetInfoEntity.setRepaymentMethod((String) m.get("repaymentMethod"));
				assetInfoEntity.setLoanUnit(loanUnit);
				assetInfoEntity.setLoanDesc((String) m.get("loanUse"));
				assetInfoRespository.save(assetInfoEntity);
			}
			
//			// 记录服务费计划表
//			// 编辑时先删除再新增
//			if (!StringUtils.isEmpty(loanId)) {
//				loanServicePlanInfoRespository.deleteByLoanId(loanId);
//			}
//			LoanServicePlanInfoEntity loanServicePlanInfo = new LoanServicePlanInfoEntity();
//			loanServicePlanInfo.setLoanId(loanInfo.getId());
//			loanServicePlanInfo.setCurrentTerm(1);
//			loanServicePlanInfo.setExceptDate(DateUtils.getCurrentDate("yyyyMMdd"));
//			loanServicePlanInfo.setExceptAmount(loanInfo.getPlatServiceAmount());
//			loanServicePlanInfo.setFactDate(null);
//			loanServicePlanInfo.setFactAmount(null);
//			loanServicePlanInfo.setPaymentStatus(Constant.LOAN_PAYMENT_STATUS_01);
//			loanServicePlanInfo.setBasicModelProperty(loanInfo.getLastUpdateUser(), true);
//			loanServicePlanInfoRespository.save(loanServicePlanInfo);
		}
		//如果是线下，则记录
		if(Constant.MANAGE_EXPENSE_DEAL_TYPE_01.equals(manageExpenseDealType)){
			// 记录服务费计划表,只有线下的时候记录,编辑时先删除再新增
			if (!StringUtils.isEmpty(loanId)) {
				loanServicePlanInfoRespository.deleteByLoanId(loanId);
			}
			LoanServicePlanInfoEntity loanServicePlanInfo = new LoanServicePlanInfoEntity();
			loanServicePlanInfo.setLoanId(loanInfo.getId());
			loanServicePlanInfo.setCurrentTerm(1);
			loanServicePlanInfo.setExceptDate(DateUtils.getCurrentDate("yyyyMMdd"));
			loanServicePlanInfo.setExceptAmount(ArithUtil.mul(new BigDecimal(params.get("loanAmount").toString())
																, ArithUtil.mul(ArithUtil.div(new BigDecimal(params.get("manageRate").toString()), new BigDecimal(Constant.STRING_100))
																			, ArithUtil.div(new BigDecimal(loanTerm)
																							, Constant.LOAN_UNIT_DAY.equals(loanUnit) 
																								? new BigDecimal(Constant.DAYS_OF_YEAR) 
																								: new BigDecimal(Constant.MONTH_OF_YEAR)))));
			loanServicePlanInfo.setFactDate(null);
			loanServicePlanInfo.setFactAmount(null);
			loanServicePlanInfo.setPaymentStatus(Constant.LOAN_PAYMENT_STATUS_01);
			loanServicePlanInfo.setBasicModelProperty(loanInfo.getLastUpdateUser(), true);
			loanServicePlanInfoRespository.save(loanServicePlanInfo);
		}
		
		// 保存还款计划
		// 编辑时先删除再新增
		if(!StringUtils.isEmpty(loanId)){
			repaymentPlanInfoRepository.deleteByLoanId(loanId);
		}
		ResultVo loanPlanVo = loanRepaymentPlanService.createRepaymentPlan(loanInfo, loanDetailInfo, loanInfo.getLoanAmount(), date);
		if(ResultVo.isSuccess(loanPlanVo)) {
			@SuppressWarnings("unchecked")
			Map<String, Object> data = (Map<String, Object>)loanPlanVo.getValue("data");
			loanInfo.setMonthlyManageAmount(new BigDecimal(data.get("accountManageExpense").toString()));
			
			// 创建还款计划
			@SuppressWarnings("unchecked")
			List<RepaymentPlanInfoEntity> planList = (List<RepaymentPlanInfoEntity>)data.get("planList");
			repaymentPlanInfoRepository.save(planList);
			
			// 更新项目值
//			projectInfoEntity.setFirstTermRepayDay((String)data.get("firstRepaymentDay"));
//			projectInfoEntity.setLastTermRepayDay((String)data.get("lastRepaymentDay"));
//			projectInfoEntity.setRepaymentDay((String)data.get("nextRepaymentDay"));
//			projectInfoEntity.setRemainderTerms((Integer)data.get("remainderTerms"));
//			projectInfoEntity.setRemainderPrincipal((BigDecimal)data.get("remainderPrincipal"));
			
		} else {
			throw new SLException("创建还款计划失败");
		}
		return new ResultVo(true, "保存项目成功");
	}

	/**
	 * @author  liyy
	 * @date    2016年12月19日 上午11:28:10
	 */
	public ResultVo getLoanAttachment(Map<String, Object> params)
			throws SLException {
		params.put("showType", Constant.SHOW_TYPE_INTERNAL);
		List<Map<String, Object>> attachList = attachmentRepositoryCustom.findAuditAttachmentInfoByLoanId(params);
		List<Map<String, Object>> attList = Lists.newArrayList();
		for(Map<String, Object> map : attachList){
			Map<String, Object> tmp = Maps.newHashMap();
			tmp.put("auditType", map.get("applyType"));
			tmp.put("attachmentType", map.get("attachmentType"));
			tmp.put("fileName", map.get("attachmentName"));
			tmp.put("filePath", map.get("storagePath"));
			attList.add(tmp);
		}
		
		Map<String, Object> data = Maps.newHashMap();
		data.put("data", attList);
		return new ResultVo(true, "附件查询成功", data);
	}

	/**
	 * 获取已募集金额 
	 */
	public ResultVo getAlreadyAmount(Map<String, Object> params) {
		BigDecimal projectJoinAmount = loanInfoRepository.getAlreadyAmount();
		
		Map<String, Object> data = Maps.newHashMap();
		data.put("projectJoinAmount", projectJoinAmount);
		return new ResultVo(true, "获取已募集金额 ", data);
	}
	
	@Override
	public Map<String, Object> queryBankData(Map<String, Object> params) {
		Map<String, Object> resultMap = Maps.newHashMap();
		List<Map<String, Object>> list = loanRebateInfoRepository.queryBankData();
		resultMap.put("paramList", list);
		return resultMap;
	}

	/**
	 * 债权投资列表查询
	 * @author  wangjf
	 * @date    2016-12-26 16:56:52
	 * @throws SLException
	 */
	public ResultVo queryCreditList(Map<String, Object> params)
			throws SLException {
		Page<Map<String, Object>> pageVo = loanManagerRepositoryCustom.queryCreditList(params);
		
		Map<String, Object> data = Maps.newHashMap();
		data.put("iTotalDisplayRecords", pageVo.getTotalElements());
		data.put("data", pageVo.getContent());
		return new ResultVo(true, "债权投资列表查询成功", data);
	}

    @Override
    public ResultVo queryCreditListInSpecialChannel(Map<String, Object> params) throws SLException {
        Page<Map<String, Object>> pageVo = loanManagerRepositoryCustom.queryCreditListInSpecialChannel(params);

        Map<String, Object> data = Maps.newHashMap();
        data.put("iTotalDisplayRecords", pageVo.getTotalElements());
        data.put("data", pageVo.getContent());
        return new ResultVo(true, "债权投资列表查询成功", data);
    }

    /**
	 * 债权投资列表查询ForJob
	 * @author  liyy
	 * @date    2017-3-8 10:24:20
	 * @throws SLException
	 */
	public List<Map<String, Object>> queryCreditListForJob(Map<String, Object> params)
			throws SLException {
		return loanManagerRepositoryCustom.queryCreditListForJob(params);
	}

	/**
	 * 债权转让详情
	 * @author  wangjf
	 * @date    2016-12-26 16:56:52
	 * @throws SLException
	 */
	public ResultVo queryCreditDetail(Map<String, Object> params)
			throws SLException {
		LoanTransferApplyEntity loanTransferApplyEntity = loanTransferApplyRepository.findOne((String)params.get("transferApplyId"));
		if(loanTransferApplyEntity == null) {
			return new ResultVo(false, "债权转让详情查询失败", Maps.newHashMap());
		}
		if(Constant.LOAN_TRANSFER_CANCEL_STATUS_02.equals(loanTransferApplyEntity.getCancelStatus())) {
			params.put("currentDate", loanTransferApplyEntity.getCreateDate());
		}
		else {
			if(Constant.LOAN_TRANSFER_APPLY_STATUS_01.equals(loanTransferApplyEntity.getApplyStatus())
					|| Constant.LOAN_TRANSFER_APPLY_STATUS_04.equals(loanTransferApplyEntity.getApplyStatus())) {
				params.put("currentDate", new Date());
			}
			else {
				params.put("currentDate", loanTransferApplyEntity.getCreateDate());
			}
		}

		List<Map<String, Object>> list = loanManagerRepositoryCustom.queryCreditDetail(params);
		
		Map<String, Object> result = null;
		if(list != null && list.size() > 0){
			result = list.get(0);
			result.put("serverDate", new Date());
		}
		return new ResultVo(true, "债权转让详情查询成功", result);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResultVo buyCredit(Map<String, Object> params) throws SLException {
		
		ResultVo result = loanProjectService.buyCredit(params);
		if(ResultVo.isSuccess(result)) {
			try{
				// 发送短信
				List<Map<String, Object>> smsList = (List<Map<String, Object>>)result.getValue("data");
				for(Map<String, Object> sms : smsList) {
					smsService.asnySendSMSAndSystemMessage(sms);
				}
			}catch(Exception e){
				log.warn("buyCredit：购买债权成功，发送短信异常！");
			}
		}
		
		return result;
	}

	@Override
	public ResultVo buyCreditExt(Map<String, Object> params) throws SLException {

		ResultVo result = loanProjectService.buyCreditExt(params);
		if(ResultVo.isSuccess(result)) {
			try{
				// 发送短信
				@SuppressWarnings("unchecked")
				List<Map<String, Object>> smsList = (List<Map<String, Object>>)result.getValue("data");
				for(Map<String, Object> sms : smsList) {
					smsService.asnySendSMSAndSystemMessage(sms);
				}
			}catch(Exception e){
				log.warn("buyCredit：购买债权成功，发送短信异常！");
			}
		}

		return result;
	}

	@Override
	public ResultVo queryMyCreditTransferingList(Map<String, Object> params)
			throws SLException {
		Map<String, Object> resultMap = Maps.newHashMap(); 
		Page<Map<String, Object>> page = loanInfoRepositoryCustom.queryMyCreditTransferingList(params);
		resultMap.put("iTotalDisplayRecords", page.getTotalElements());
		resultMap.put("data", page.getContent());
		return new ResultVo(true, "查询转让中债权列表成功", resultMap);
	}

	@Override
	public ResultVo queryMyCreditBeTransferedList(Map<String, Object> params)
			throws SLException {
		String onlineTime = paramService.findTransferRefromOnlineTimeByParameterName(Constant.TRANSFERRE_FROM_ONLINE_TIME_20170531);
		params.put("onlineTime", onlineTime);
		
		Page<Map<String, Object>> page = loanInfoRepositoryCustom.queryMyCreditBeTransferedList(params);
		
		Map<String, Object> resultMap = Maps.newHashMap(); 
		resultMap.put("iTotalDisplayRecords", page.getTotalElements());
		resultMap.put("data", page.getContent());
		return new ResultVo(true, "查询转出债权列表成功", resultMap);
	}

	@Override
	public ResultVo queryMyCreditTransferingDetail(Map<String, Object> params)
			throws SLException {
		params.put("transferRate", paramService.findTransferRate());
		params.put("transferDay", paramService.findTransferDay());
		return new ResultVo(true, "转让详细信息查询成功", loanInfoRepositoryCustom.queryMyCreditTransferingDetail(params));
	}

	@Override
	public ResultVo queryMyCreditBeTransferedDetail(Map<String, Object> params)
			throws SLException {
		String onlineTime = paramService.findTransferRefromOnlineTimeByParameterName(Constant.TRANSFERRE_FROM_ONLINE_TIME_20170531);
		params.put("onlineTime", onlineTime);
		return new ResultVo(true, "转让详细信息查询成功", loanInfoRepositoryCustom.queryMyCreditBeTransferedDetail(params));
	}

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo canceTransferDebt(Map<String, Object> params)
			throws SLException {
		
		String transferApplyId = (String)params.get("transferApplyId");
		String tradePass = (String)params.get("tradePass");
		String custId = (String)params.get("custId");
		String appSource = (String)params.get("appSource");
		appSource = Strings.nullToEmpty(StringUtils.isEmpty(appSource) ? Constant.INVEST_SOURCE_PC : appSource).toLowerCase();
				
		// 1) 判断交易密码是否正确
		if(!Constant.SYSTEM_USER_BACK.equals(custId)) { // 非系统用户需验证用户名和交易密码
			ResultVo resultVo = customerService.checkUserTradePassword(custId, tradePass);
			if(!ResultVo.isSuccess(resultVo)) {
				return resultVo;
			}
		}
		
		// 2) 判断转让申请是否存在
		LoanTransferApplyEntity loanTransferApplyEntity = loanTransferApplyRepository.findOne(transferApplyId);
		if(loanTransferApplyEntity == null) {
			return new ResultVo(false, "撤销失败！转让申请不存在");
		}
		
		// 3) 判断是否已经撤销
		if(Constant.LOAN_TRANSFER_CANCEL_STATUS_02.equals(loanTransferApplyEntity.getCancelStatus())) {
			return new ResultVo(false, "撤销失败！该笔转让申请已经撤销成功，请勿重复撤销");
		}
		
		// 4) 判断是否已经转让成功
		if(Constant.LOAN_TRANSFER_APPLY_STATUS_02.equals(loanTransferApplyEntity.getCancelStatus())) {
			return new ResultVo(false, "撤销失败！该笔转让申请已经转让成功，无法撤销");
		}
		
		loanTransferApplyEntity.setCancelStatus(Constant.LOAN_TRANSFER_CANCEL_STATUS_02);
		loanTransferApplyEntity.setBasicModelProperty(custId, false);
		
		// 记录日志
		LogInfoEntity logInfoEntity = new LogInfoEntity();
		logInfoEntity.setRelateType(Constant.TABLE_BAO_T_LOAN_TRANSFER_APPLY);
		logInfoEntity.setRelatePrimary(transferApplyId);
		logInfoEntity.setLogType(Constant.OPERATION_TYPE_80);
		logInfoEntity.setOperBeforeContent("");
		logInfoEntity.setOperAfterContent("");
		logInfoEntity.setOperDesc("");
		logInfoEntity.setOperPerson(custId);
		logInfoEntity.setOperIpaddress((String)params.get("ipAddress"));
		logInfoEntity.setMemo(String.format("撤销债权转让申请%s", loanTransferApplyEntity.getTransferNo()));
		logInfoEntity.setBasicModelProperty(custId, true);
		logInfoEntityRepository.save(logInfoEntity);
		
		// 11) 记录设备信息
		Map<String, Object> deviceParams = Maps.newConcurrentMap();
		deviceParams.putAll(params);
		deviceParams.put("relateType", Constant.TABLE_BAO_T_LOAN_TRANSFER_APPLY);
		deviceParams.put("relatePrimary", transferApplyId);
		deviceParams.put("tradeType", Constant.OPERATION_TYPE_80);
		deviceParams.put("userId", custId);
		deviceService.saveUserDevice(deviceParams);
		
		return new ResultVo(true, "撤销成功");
	}
	
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo transferDebtForBatch(Map<String, Object> params) throws SLException {
		String holdIds = (String) params.get("holdIds");
		String[] holdIdArray = holdIds.split(",");
		
		String custId = (String)params.get("custId");
//		BigDecimal tradeScale = new BigDecimal(params.get("tradeScale").toString());
//		BigDecimal reducedScale = new BigDecimal(params.get("discountScale").toString());
		String tradePass = (String)params.get("tradePass");
		String appSource = (String)params.get("appSource");
		appSource = Strings.nullToEmpty(StringUtils.isEmpty(appSource) ? Constant.INVEST_SOURCE_PC : appSource).toLowerCase();
		String userId = (String)params.get("userId");
		
		int holdIdsLength = Integer.valueOf(params.get("holdIdsLength").toString());
		
		if(holdIdArray==null || holdIdArray.length==0 ){
			return new ResultVo(false, "转让数目为空");
		}
		if(holdIdsLength != holdIdArray.length){
			return new ResultVo(false, "转让数目不一致");
		}
		// 1) 判断交易密码是否正确
		if(!Constant.SYSTEM_USER_BACK.equals(userId)) { // 非系统用户需验证用户名和交易密码
			ResultVo resultVo = customerService.checkUserTradePassword(custId, tradePass);
			if(!ResultVo.isSuccess(resultVo)) {
				return resultVo;
			}
		}
		
//		StringBuilder failMessage = new StringBuilder();
		// 转让处理
		for (String holdId : holdIdArray) {
			Map<String, Object> transParam = Maps.newHashMap();
			transParam.putAll(params);
			
			transParam.put("holdId", holdId);
			transParam.put("tradeScale", "1");
//			transParam.put("custId", custId);
//			transParam.put("discountScale", reducedScale);
//			transParam.put("tradePass", tradePass);
//			transParam.put("appSource", appSource);
//			transParam.put("userId", userId);
			
//			try{
				ResultVo transferVo = self.transferDebt(transParam);
				if(!ResultVo.isSuccess(transferVo)) {
//					failMessage.append(transferVo.getValue("message").toString()).append("，");
					throw new SLException(transferVo.getValue("message").toString());
				}
//			}catch(Exception e){
//				log.warn("批量转让警告：" + e.getMessage());
////				failMessage.append("后台处理异常，");
//			}
		}
		
//		return new ResultVo(true, StringUtils.isEmpty(failMessage.toString())?"批量转让成功":"部分失败：".concat(failMessage.toString()));
		return new ResultVo(true, "批量转让成功");
	}

	/* 
	 * 转让债权
	 * @see com.slfinance.shanlincaifu.service.LoanManagerService#transferDebt(java.util.Map)
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo transferDebt(Map<String, Object> params) throws SLException {
		
		String holdId = (String)params.get("holdId");
		String custId = (String)params.get("custId");
		BigDecimal transferScale = new BigDecimal(params.get("tradeScale").toString());
		BigDecimal reducedScale = new BigDecimal(params.get("discountScale").toString());
		String tradePass = (String)params.get("tradePass");
		Date now = new Date();
		String appSource = (String)params.get("appSource");
		appSource = Strings.nullToEmpty(StringUtils.isEmpty(appSource) ? Constant.INVEST_SOURCE_PC : appSource).toLowerCase();
		String userId = (String)params.get("userId");
		
		// 1) 判断交易密码是否正确
		if(!Constant.SYSTEM_USER_BACK.equals(userId)) { // 非系统用户需验证用户名和交易密码
			ResultVo resultVo = customerService.checkUserTradePassword(custId, tradePass);
			if(!ResultVo.isSuccess(resultVo)) {
				return resultVo;
			}
		}
		
		if(transferScale.compareTo(BigDecimal.ZERO) <= 0 && transferScale.compareTo(BigDecimal.ONE) > 0) {
			return new ResultVo(false, "转让比例不在范围之内");
		}
		
		// 2) 判断系统是否正在还款
		JobRunListenerEntity jobRunListenerEntity = jobRunListenerRepository.findByJobName(Constant.JOB_NAME_LOAN_REPAYMENT);
		if(jobRunListenerEntity != null && Constant.EXEC_STATUS_RUNNING.equals(jobRunListenerEntity.getExecuteStatus())) {
			return new ResultVo(false, "转让失败！系统正在执行还款任务，请稍后再试！");
		}

		// 3) 判断借款状态是否正确
		WealthHoldInfoEntity wealthHoldInfoEntity = wealthHoldInfoRepository.findOne(holdId);
		if(wealthHoldInfoEntity == null) {
			return new ResultVo(false, "转让失败！用户未持有该笔债权");
		}
		if(wealthHoldInfoEntity.getHoldScale().compareTo(BigDecimal.ZERO) == 0 
				|| !Constant.HOLD_STATUS_01.equals(wealthHoldInfoEntity.getHoldStatus())) {
			return new ResultVo(false, "转让失败！用户不再持有该笔债权");
		}
		
		LoanInfoEntity loanInfoEntity = loanInfoRepository.findOne(wealthHoldInfoEntity.getLoanId());
		if(loanInfoEntity == null) {
			return new ResultVo(false, "转让失败！借款不存在");
		}
		if(!Constant.LOAN_STATUS_08.equals(loanInfoEntity.getLoanStatus())) {
			return new ResultVo(false, "转让失败！借款非正常状态");
		}
		
		// 4) 判断距离到期日是否大于30天
		Integer fromEndDay = paramService.findTransferFromEndDay();
		if(DateUtils.datePhaseDiffer(DateUtils.truncateDate(now), loanInfoEntity.getInvestEndDate()) <= fromEndDay) {
			return new ResultVo(false, String.format("转让失败！剩余期限小于%d天的债权不允许转让", fromEndDay.intValue()));
		}
		
		// 5) 判断持有债权天数是否大于30天
		InvestInfoEntity investInfoEntity = investInfoRepository.findOne(wealthHoldInfoEntity.getInvestId());
		if(investInfoEntity == null) {
			return new ResultVo(false, "转让失败！投资记录不存在");
		}
		Integer needHoldDay = paramService.findTransferNeedHoldDay();
		if(DateUtils.datePhaseDiffer(DateUtils.parseDate(investInfoEntity.getEffectDate(), "yyyyMMdd"), DateUtils.truncateDate(now)) < needHoldDay) {
			return new ResultVo(false, String.format("转让失败！持有债权天数小于%d天不允许转让", needHoldDay.intValue()));
		}
		
		// 6) update by http://192.16.150.101:8080/browse/SLCF-2823 at 2017-05-18
		// 剩余本金大于等于1000才可转让
//		// 债权价值
//		BigDecimal loanValue = creditRightValueRepository.findByLoanIdAndValueDate(wealthHoldInfoEntity.getLoanId(), DateUtils.formatDate(now, "yyyyMMdd"));
//		// 用户持有的价值		
//		BigDecimal holdValue = ArithUtil.mul(loanValue, wealthHoldInfoEntity.getHoldScale());
//		// 转让持有价值 = 持有价值×转让比例
//		BigDecimal transferHoldValue = ArithUtil.mul(holdValue, transferScale);
//		// 转让金额  = 转让持有价值  * 折价比例    不能小于1000 
//		if(ArithUtil.mul(transferHoldPrincipal, reducedScale).compareTo(new BigDecimal("1000")) < 0) {
//			return new ResultVo(false, String.format("转让失败！转让金额不能低于1000，实际为%s", ArithUtil.mul(transferHoldValue, reducedScale).setScale(2, BigDecimal.ROUND_DOWN).toPlainString()));
//		}

		// 剩余本金
		BigDecimal remainPrincipal = loanInfoEntity.getLoanDetailInfoEntity().getCreditRemainderPrincipal();
		// 持有本金
		BigDecimal holdPrincipal =  ArithUtil.mul(remainPrincipal, wealthHoldInfoEntity.getHoldScale());
		// 转让本金 = 持有本金×转让比例
		BigDecimal transferHoldPrincipal = ArithUtil.mul(holdPrincipal, transferScale);
		
		// update lyy 2017-7-1 by http://192.16.150.101:8080/browse/SLCF-3181
		// 转让本金  不能小于100 // 本金大于100元或全部转让
		if(transferHoldPrincipal.compareTo(new BigDecimal("100")) < 0 && transferScale.compareTo(BigDecimal.ONE) < 0) {
			return new ResultVo(false, String.format("转让失败！转让金额不能低于100，实际为%s, ", transferHoldPrincipal.setScale(2, BigDecimal.ROUND_DOWN).toPlainString()));
		}

		// 7) 判断是否存在未撤销的转让申请（转让中的数据）
		List<LoanTransferApplyEntity> transferApplyList = loanTransferApplyRepository.findBySenderHoldIdAndCancelStatus(holdId, Constant.LOAN_TRANSFER_CANCEL_STATUS_01);
		if(transferApplyList != null && transferApplyList.size() > 0) {
			return new ResultVo(false, "转让失败！该笔债权已经在转让中");
		}
		
		// 7.1) 判断是否存在审核中的数据
		List<LoanTransferApplyEntity> transferApplyList1 = loanTransferApplyRepository.findBySenderHoldIdAndAuditStatus(holdId, Constant.TRANSFER_APPLY_STATUS_UNREVIEW);
		if(transferApplyList1 != null && transferApplyList1.size() > 0) {
			return new ResultVo(false, "转让失败！该笔债权已经在审核中");
		}
		
		// 8) 判断剩余可申请转让的比例
		// 已申请转让的比例
		BigDecimal hasAppliedScale = loanTransferApplyRepository.sumTradeScaleBySenderHoldId(holdId);
		if(hasAppliedScale == null) {
			hasAppliedScale = BigDecimal.ZERO;
		}
		// 计算转让持有比例
		BigDecimal tradeScale = ArithUtil.mul(wealthHoldInfoEntity.getHoldScale(), transferScale);
		// 若已申请的转让持有比例 + 本次申请转让持有比例 > 总持有比例
		if(ArithUtil.add(hasAppliedScale, tradeScale).compareTo(ArithUtil.add(wealthHoldInfoEntity.getHoldScale(), wealthHoldInfoEntity.getTradeScale())) > 0) {
			return new ResultVo(false, "转让失败！总转让比例超出持有比例");
		}

		
		LoanTransferApplyEntity loanTransferApplyEntity = new LoanTransferApplyEntity();
		loanTransferApplyEntity.setSenderHoldId(holdId);
		loanTransferApplyEntity.setSenderCustId(custId);
		loanTransferApplyEntity.setTransferNo(flowNumberService.generateLoanTransferApplyNo()); //ZR+年(2位)+月(2位)+日(2位)+5位序号 如ZR16122400001
		loanTransferApplyEntity.setTransferScale(transferScale);
		loanTransferApplyEntity.setTradeScale(tradeScale);
		loanTransferApplyEntity.setRemainderTradeScale(tradeScale);
		loanTransferApplyEntity.setDeviationScale(BigDecimal.ONE);
		loanTransferApplyEntity.setReducedScale(reducedScale);
		loanTransferApplyEntity.setTradeAmount(BigDecimal.ZERO);
		loanTransferApplyEntity.setTradeValue(BigDecimal.ZERO);
		loanTransferApplyEntity.setTradePrincipal(BigDecimal.ZERO);
		loanTransferApplyEntity.setManageAmount(BigDecimal.ZERO);
		loanTransferApplyEntity.setTradeTimes(0);
		loanTransferApplyEntity.setApplyStatus(Constant.LOAN_TRANSFER_APPLY_STATUS_01);
		loanTransferApplyEntity.setCancelStatus(Constant.LOAN_TRANSFER_CANCEL_STATUS_01);
//		loanTransferApplyEntity.setTransferStartDate(now);
//		loanTransferApplyEntity.setTransferEndDate(transferEndDate);
		loanTransferApplyEntity.setProtocolType(paramService.findTransferProtocalType());
		loanTransferApplyEntity.setBasicModelProperty(custId, true);
		loanTransferApplyEntity.setAuditStatus(Constant.TRANSFER_APPLY_STATUS_UNREVIEW);
		loanTransferApplyEntity.setMemo(appSource);
		// update 2017-07-01
		loanTransferApplyEntity.setInvestMinAmount(new BigDecimal(Constant.STRING_100));
		loanTransferApplyEntity.setIncreaseAmount(BigDecimal.ONE);
		loanTransferApplyEntity = loanTransferApplyRepository.save(loanTransferApplyEntity);
		
		
		// todo 母国平
		// 判断投资是否使用加息券
		// 如果该投资有使用加息券，将对应的债权转让申请编号存储到加息券奖励记录中
		String id = loanTransferApplyEntity.getId();
		String InvestId = wealthHoldInfoEntity.getInvestId();
		List<PurchaseAwardInfoEntity> PurchaseAwardInfo= purchaseAwardInfoRepository
				.findByCustIdAndInvestId(custId, InvestId);
		for(PurchaseAwardInfoEntity Purchase:PurchaseAwardInfo){
			Purchase.setTransferApplyId(id);
		}
//		purchaseAwardInfoEntity.setTransferApplyId(id);
		
//		
		// 10) 记录日志
		LogInfoEntity logInfoEntity = new LogInfoEntity();
		logInfoEntity.setRelateType(Constant.TABLE_BAO_T_LOAN_TRANSFER_APPLY);
		logInfoEntity.setRelatePrimary(loanTransferApplyEntity.getId());
		logInfoEntity.setLogType(Constant.OPERATION_TYPE_79);
		logInfoEntity.setOperBeforeContent("");
		logInfoEntity.setOperAfterContent("");
		logInfoEntity.setOperDesc("");
		logInfoEntity.setOperPerson(custId);
		logInfoEntity.setOperIpaddress((String)params.get("ipAddress"));
		logInfoEntity.setMemo(String.format("申请转让剩余本金%s, 转让比例%s，折价比例%s", transferHoldPrincipal.setScale(2,BigDecimal.ROUND_DOWN).toPlainString(), transferScale.toPlainString(), reducedScale.toPlainString()));
		logInfoEntity.setBasicModelProperty(custId, true);
		logInfoEntityRepository.save(logInfoEntity);
		
		// 11) 记录设备信息
		Map<String, Object> deviceParams = Maps.newConcurrentMap();
		deviceParams.putAll(params);
		deviceParams.put("relateType", Constant.TABLE_BAO_T_LOAN_TRANSFER_APPLY);
		deviceParams.put("relatePrimary", loanTransferApplyEntity.getId());
		deviceParams.put("tradeType", Constant.OPERATION_TYPE_79);
		deviceParams.put("userId", custId);
		deviceService.saveUserDevice(deviceParams);
		
		// 12)审核信息表
		Date date =  new Date();
		AuditInfoEntity auditInfoEntity = new AuditInfoEntity();
		auditInfoEntity.setBasicModelProperty(custId, true);
		auditInfoEntity.setCustId(custId);
		auditInfoEntity.setRelateType(Constant.TABLE_BAO_T_LOAN_TRANSFER_APPLY);
		auditInfoEntity.setRelatePrimary(loanTransferApplyEntity.getId());
		auditInfoEntity.setApplyType(Constant.OPERATION_TYPE_79);
		auditInfoEntity.setApplyTime(date);
//		auditInfoEntity.setTradeAmount(new BigDecimal(param.get("tradeAmount").toString()));
		auditInfoEntity.setTradeStatus(Constant.TRADE_STATUS_01);
		auditInfoEntity.setAuditStatus(Constant.TRANSFER_APPLY_STATUS_UNREVIEW);
		auditInfoEntity = auditInfoRepository.save(auditInfoEntity);
		
		return new ResultVo(true, "申请转让债权成功");
	}

	@Override
	public ResultVo queryEmployeeTransferList(Map<String, Object> params)
			throws SLException {
		String ids = (String)params.get("ids");
		String mobile = (String)params.get("mobile");
		
		CustInfoEntity custInfoEntity = custInfoRepository.findByMobile(mobile);
		
		List<String> idList = Splitter.onPattern("&").omitEmptyStrings().splitToList(ids);
		Map<String, Object> requestParam = Maps.newHashMap();
		requestParam.put("ids", idList);
		ResultVo resultVo = loanInfoRepositoryCustom.queryAllShowTransferList(requestParam);
		
		@SuppressWarnings("unchecked")
		Map<String, Object> data = (Map<String, Object>)resultVo.getValue("data");
		data.put("cust", custInfoEntity);
		return new ResultVo(true, "", data);
	}

	@Override
	public ResultVo queryTransferContract(Map<String, Object> params)
			throws SLException {
		
		Map<String, Object> requestParam = Maps.newConcurrentMap();
		if(StringUtils.isEmpty((String)params.get("transferId"))) {
			if(StringUtils.isEmpty((String)params.get("investId"))){ // 投资ID为空
				return new ResultVo(false, "转让记录ID和投资ID不能同时为空");
			}
			else {
				WealthHoldInfoEntity wealthHoldInfoEntity = wealthHoldInfoRepository.findByInvestId((String)params.get("investId"));
				if(wealthHoldInfoEntity == null) {
					return new ResultVo(false, "缺少持有情况");
				}
				LoanTransferEntity loanTransferEntity = loanTransferRepository.findByReceiveHoldId(wealthHoldInfoEntity.getId());
				if(loanTransferEntity == null) {
					return new ResultVo(false, "缺少转让记录");
				}
				requestParam.put("transferId", loanTransferEntity.getId());
			}
		}
		else {
			requestParam.put("transferId", params.get("transferId"));
		}
		// 债权转让调整上线时间
		String onlineTime = paramService.findTransferRefromOnlineTimeByParameterName(Constant.TRANSFERRE_FROM_ONLINE_TIME_20170531);
		requestParam.put("onlineTime", onlineTime);
		
		Map<String, Object> result = loanInfoRepositoryCustom.queryTransferContract(requestParam);
		if(params.get("isDownload")!=null&&(Boolean)params.get("isDownload")){
		//电签
	    Map<String , Object> sign = Maps.newHashMap();
	    List<Map<String , Object>> signData = Lists.newArrayList();
	    
	    Map<String , Object> outPerson = Maps.newHashMap();//出让人
	    outPerson.put("signType", "个人");
	    outPerson.put("name", result.get("senderCustName"));
	    outPerson.put("idCard", result.get("senderCredentialsCode"));
	    outPerson.put("keyword","甲方（债权出让人）：");//签署位置
	    outPerson.put("offsetX", "0.1");
	    outPerson.put("offsetY", "-0.05");
	    Map<String , Object> inPerson = Maps.newHashMap();//受让人
	    inPerson.put("signType", "个人");
	    inPerson.put("name", result.get("receiverCustName"));
	    inPerson.put("idCard", result.get("receiverCredentialsCode"));
	    inPerson.put("keyword", "乙方（债权受让人）：");//签署位置
	    inPerson.put("offsetX", "0.1");
	    inPerson.put("offsetY", "-0.05");
	    
	    Map<String, Object> ourCom = Maps.newHashMap();//平台公章
	    ourCom.put("signType", "平台");
	    ourCom.put("keyword", "第十二条");//签署位置
	    ourCom.put("offsetX", "0.6");//签署位置偏移量
	    ourCom.put("offsetY", "-0.1");//签署位置偏移量
	    
	    signData.add(ourCom);
	    signData.add(outPerson);
	    signData.add(inPerson);
	    sign.put("platType", "契约锁");
	    sign.put("signData", signData);
	    result.put("sign", sign);
		}

		// 对格式做特殊处理
		result.put("senderCredentialsCode", SharedUtil.replaceSpecialWord((String)(result.get("senderCredentialsCode"))));
		result.put("receiverCredentialsCode", SharedUtil.replaceSpecialWord((String)(result.get("receiverCredentialsCode"))));
		result.put("yearRate",  ArithUtil.formatPercent2((BigDecimal)result.get("yearRate"), 1, 5));
		result.put("remainPrincipal", ArithUtil.formatNumber(result.get("remainPrincipal")) + "元");
		result.put("remainInterest", ArithUtil.formatNumber(result.get("remainInterest")) + "元");
		result.put("transferAmout", ArithUtil.formatNumber(result.get("transferAmout")) + "元");
		result.put("payAmount", ArithUtil.formatNumber(result.get("payAmount")) + "元");
		result.put("transferExpense", ArithUtil.formatNumber(result.get("transferExpense")) + "元");
		result.put("transferDate", DateUtils.formatDate((Date)result.get("transferDate"), "yyyy年MM月dd日"));
		result.put("remainTerm", result.get("remainTerm").toString() + "期");
		result.put("remainTermMonth", result.get("remainTermMonth").toString() + "个月");
		
		return new ResultVo(true, "查询协议成功", result);
	}

	@Override
	public ResultVo queryCreditTransferRecord(Map<String, Object> params)
			throws SLException {
		Page<Map<String, Object>> pageVo = loanManagerRepositoryCustom.queryLoanTransferByLoanId(params);
		
		Map<String, Object> result = Maps.newHashMap();
		result.put("iTotalDisplayRecords", pageVo.getTotalElements());
		result.put("data", pageVo.getContent());
		return new ResultVo(true, "转让记录查询成功", result);
	}

	@Override
	public ResultVo queryCreditPaymentPlan(Map<String, Object> params)
			throws SLException {
		List<Map<String, Object>> list = loanManagerRepositoryCustom.queryCreditPaymentPlan(params);
		
		Map<String, Object> result = Maps.newHashMap();
		result.put("data", list);
		return new ResultVo(true, "还款计划查询成功", result);
	}

	/**
	 * 保存附件-完成编辑
	 * @author  liyy
	 * @date    2017年2月7日 下午15:35:51
	 * @param
	 *          <tt>loanId         :String:借款信息表主键Id</tt><br>
     *        <tt>auditId        :String:审核ID</tt><br>
     *        <tt>attachmentId   :String:         附件ID(原附件ID)</tt><br>
     *        <tt>attachmentType :String:         附件类型</tt><br>
     *        <tt>attachmentName :String:         附件名称</tt><br>
     *        <tt>storagePath    :String:         存储路径</tt><br>
	 * @return ResultVo
	 * @throws SLException
	 */
	@Override
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo saveLoanAttachment(Map<String, Object> params)
			throws SLException {
		String auditId = (String) params.get("auditId");
		String attachmentId = (String) params.get("attachmentId");
		String userId = (String) params.get("userId");
		
		List<AttachmentInfoEntity> list = attachmentRepository.findByRelatePrimaryAndMemoAndShowType(auditId, attachmentId, Constant.SHOW_TYPE_EXTERNAL);
		if(list != null && list.size() > 0) {
			throw new SLException("附件已存在");
		}
		
		AttachmentInfoEntity attInfo = new AttachmentInfoEntity();
		attInfo.setBasicModelProperty(userId, true);
		attInfo.setRelateType(Constant.TABLE_BAO_T_AUDIT_INFO);
		attInfo.setRelatePrimary(auditId);
		attInfo.setAttachmentType((String) params.get("attachmentType"));
		attInfo.setAttachmentName((String) params.get("attachmentName"));
		attInfo.setStoragePath((String) params.get("storagePath"));
		attInfo.setMemo(attachmentId); // 内部关联
		attInfo.setShowType(Constant.SHOW_TYPE_EXTERNAL);
		
		//脱敏状态保存
		LoanInfoEntity  loanInfoEntity  = loanInfoRepository.findOne(params.get("loanId").toString());
		if(loanInfoEntity==null){
			return new ResultVo(false, "客户信息不存在!");
		}
		loanInfoEntity.setAttachmentFlag(params.get("attachmentFlag").toString());
		loanInfoRepository.save(loanInfoEntity);
		attachmentRepository.save(attInfo);
		return new ResultVo(true, "操作成功");
	}

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo timerPublishLoanInfo(Map<String, Object> params)
			throws SLException {
		String loanId = (String) params.get("loanId");
		String publishDate = (String) params.get("publishDate");
		String custId = (String) params.get("userId");

		try {
			LoanInfoEntity loanInfoEntity = loanInfoRepository.findOne(loanId);
			if (loanInfoEntity == null) {
				return new ResultVo(false, "定时发布操作失败！借款不存在");
			}
			
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			loanInfoEntity.setPublishDate(format.parse(publishDate));
			loanInfoEntity.setBasicModelProperty(custId, false);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new SLException("发布时间格式错误！");
		}
		return new ResultVo(true, "定时发布操作成功");
	}

	@SuppressWarnings({ "unchecked", "unused" })
	public ResultVo queryAssetContract(Map<String, Object> params) throws SLException {
		String loanId = (String)params.get("loanId");
		String custId = (String)params.get("custId");
		LoanInfoEntity loanInfoEntity = loanInfoRepository.findOne(loanId);
		if(loanInfoEntity == null) {
			return new ResultVo(false, "项目不存在");
		}
		if(Constant.LOAN_STATUS_05.equals(loanInfoEntity.getLoanStatus())) {
			return new ResultVo(false, "项目为募集中状态不允许");
		}
		// income  :债权收益取还款计划所有利息
		BigDecimal income= repaymentPlanInfoRepository.sumRepaymentInterestByLoanIdNotAwardRate(loanId);
		
		// 投资人列表
		List<Map<String, Object>> investList = investInfoRepositoryCustom.queryInvestListByLoanId(loanInfoEntity.getId());
		
		// 资产信息列表
		List<Map<String, Object>> assetList = assetInfoRepository.findMapAssetInfoByloanId(loanInfoEntity.getId());
	
		Map<String, Object> result = Maps.newHashMap();
		result.put("loanId", loanInfoEntity.getId());
		result.put("loanCode", loanInfoEntity.getLoanCode());
		result.put("senderCustName", loanInfoEntity.getServiceName());
		result.put("senderCredentialsCode", loanInfoEntity.getServiceCode());
		result.put("loanAmount", loanInfoEntity.getLoanAmount());
		result.put("incomeType", loanInfoEntity.getRepaymentMethod());
		result.put("atoneType", "到期一次性回购");
		result.put("yearRate", loanInfoEntity.getLoanDetailInfoEntity().getYearIrr());
		result.put("awardRate", loanInfoEntity.getAwardRate());
		result.put("typeTerm", Constant.LOAN_UNIT_DAY.equals(loanInfoEntity.getLoanUnit())?loanInfoEntity.getLoanTerm()+"天":loanInfoEntity.getLoanTerm()+"个月");
		result.put("effectDate", loanInfoEntity.getInvestStartDate());
		result.put("endDate", loanInfoEntity.getInvestEndDate());
		result.put("monthlyManageAmount", loanInfoEntity.getMonthlyManageAmount());
		result.put("accountManageAmount", loanInfoEntity.getPlatServiceAmount());
		result.put("loanDesc", loanInfoEntity.getLoanDesc());
		result.put("protocolType", loanInfoEntity.getProtocalType());
		// 投资人列表
		result.put("investorList", investList);
		// 资产信息列表
		result.put("assetList", assetList);
		if (params.get("isDownload") != null
				&& (Boolean) params.get("isDownload")) {
			// 电签
			Map<String, Object> sign = Maps.newHashMap();
			List<Map<String, Object>> signData = Lists.newArrayList();

			Map<String, Object> loanPerson = Maps.newHashMap();// 债权转让方
			loanPerson.put("signType", "个人");
			loanPerson.put("name", loanInfoEntity.getServiceName());
			loanPerson.put("idCard", loanInfoEntity.getServiceCode());
			loanPerson.put("keyword", loanInfoEntity.getServiceName());// 签署位置
			loanPerson.put("offsetX", "-0.01");// 偏移量
			loanPerson.put("offsetY", "-0.02");// 偏移量
			signData.add(loanPerson);

			Map<String, Object> loanPerson_ = Maps.newHashMap();// 债权转让方
			loanPerson_.put("signType", "个人");
			loanPerson_.put("name", loanInfoEntity.getServiceName());
			loanPerson_.put("idCard", loanInfoEntity.getServiceCode());
			loanPerson_.put("keyword", "甲方：");// 签署位置
			loanPerson_.put("offsetX", "0.04");// 偏移量
			loanPerson_.put("offsetY", "-0.01");// 偏移量
			signData.add(loanPerson_);

			for (Map<String, Object> invest : investList) {
				Map<String, Object> investPerson = Maps.newHashMap();// 债权受让方
				investPerson.put("signType", "个人");
				investPerson.put("name", invest.get("custName"));
				investPerson.put("idCard", invest.get("idCard"));
				investPerson.put("keyword", invest.get("loginName").toString()
						+ invest.get("custName").toString());// 签署位置
				investPerson.put("offsetX", "-0.005");// 偏移量
				investPerson.put("offsetY", "-0.02");// 偏移量
				signData.add(investPerson);
			}
			Map<String, Object> ourCom = Maps.newHashMap();// 平台公章
			ourCom.put("signType", "平台");
			ourCom.put("keyword", "丙方：");
			ourCom.put("offsetX", "0.05");
			ourCom.put("offsetY", "-0.05");
			signData.add(ourCom);
			sign.put("platType", "契约锁");
			sign.put("signData", signData);
			result.put("sign", sign);
		}
		
		// 对格式做特殊处理
		result.put("loanAmount", ArithUtil.formatNumber(result.get("loanAmount")));
		result.put("yearRate", ArithUtil.formatPercent2((BigDecimal)result.get("yearRate"), 1, 5));
		result.put("awardRate", ArithUtil.formatPercent2((BigDecimal)result.get("awardRate"), 1, 5));
		result.put("monthlyManageAmount", ArithUtil.formatNumber(result.get("monthlyManageAmount")));
		result.put("accountManageAmount", ArithUtil.formatNumber(result.get("accountManageAmount")));
		result.put("effectDate", DateUtils.formatDate((Date)result.get("effectDate"), "yyyy年MM月dd日"));
		result.put("endDate", DateUtils.formatDate((Date)result.get("endDate"), "yyyy年MM月dd日"));
		// waitingIncome:债权收益（回购数额=债权本金数额+债权收益）取还款计划所有利息
		result.put("waitingIncome",  ArithUtil.formatNumber(ArithUtil.add(loanInfoEntity.getLoanAmount(), income))+"元");
		
		for(Map<String, Object> invest : (List<Map<String, Object>>)result.get("investorList")) {
			invest.put("investAmount", ArithUtil.formatNumber(invest.get("investAmount")));
			invest.put("investDate", DateUtils.formatDate(DateUtils.parseDate((String)invest.get("investDate"), "yyyyMMdd"), "yyyy年MM月dd日"));
//			invest.put("custName", SharedUtil.replaceSpecialWord((String)invest.get("custName")));
			invest.put("idCard", SharedUtil.replaceSpecialWord((String)invest.get("idCard")));
		}
		
		for(Map<String, Object> asset : (List<Map<String, Object>>)result.get("assetList")) {
			asset.put("loanAmount", ArithUtil.formatNumber(asset.get("loanAmount")));
			asset.put("investEndDate", DateUtils.formatDate((Date)asset.get("investEndDate"), "yyyy年MM月dd日"));
			asset.put("custName", SharedUtil.replaceSpecialWord((String)asset.get("custName")));
		}
		return new ResultVo(true, "债权转让及回购协议", result);
	}

	@Override
	public ResultVo queryAssetListByLoanId(Map<String, Object> params) {
		Page<Map<String, Object>> pageVo = loanManagerRepositoryCustom.queryAssetListByLoanId(params);
		Map<String, Object> result = Maps.newHashMap();
		result.put("iTotalDisplayRecords", pageVo.getTotalElements());
		result.put("data", pageVo.getContent());
		return new ResultVo(true, "资产信息列表查询成功", result);
	}

	@Override
	public ResultVo queryLoanExpenseList(Map<String, Object> params)
			throws SLException {
		
		Page<Map<String, Object>> pageVo = loanServicePlanInfoRepositoryCustom.queryLoanExpenseList(params);
		
		Map<String, Object> result = Maps.newHashMap();
		result.put("iTotalDisplayRecords", pageVo.getTotalElements());
		result.put("data", pageVo.getContent());
		return new ResultVo(true, "服务费回款列表查询成功", result);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor=SLException.class)
	public ResultVo payExpense(Map<String, Object> params) throws SLException {
		String loanServicePlanId = (String) params.get("loanServicePlanId");
		String userId = (String) params.get("userId");
		LoanServicePlanInfoEntity entity = loanServicePlanInfoRepository.findOne(loanServicePlanId);
		if(entity == null){
			throw new SLException("借款服务费计划获取出错");
		}
		entity.setPaymentStatus(Constant.LOAN_PAYMENT_STATUS_02);
		entity.setBasicModelProperty(userId, false);
		
		return new ResultVo(true, "确认回款操作成功");
	}

	@Override
	public ResultVo isExistsLoan(Map<String, Object> params) throws SLException {
		String type = (String) params.get("type");
		List<BigDecimal> counts = loanInfoRepository.isExistsLoan();
		Map<String, Object> data = Maps.newHashMap();
		
		if(StringUtils.isEmpty(type) || "loan".equals(type)){
			data.put("canBuyLoan", counts.get(0).compareTo(BigDecimal.ZERO)>0?true:false);
		}
		if(StringUtils.isEmpty(type) || "transfer".equals(type)){
			data.put("canBuyTransfer", counts.get(1).compareTo(BigDecimal.ZERO)>0?true:false);
		}
		return new ResultVo(true, "操作成功", data);
	}
	
	/**
	 * 善融贷-协议接口
	 */
	public ResultVo queryFinancingContract(Map<String, Object> params) throws SLException {
//		String investId = (String) params.get("investId");
		Map<String, Object> data = loanInfoRepositoryCustom.queryFinancingContract(params);
		if(params.get("isDownload")!=null&&(Boolean)params.get("isDownload")){
		// 善融贷电签数据
		Map<String, Object> sign = Maps.newHashMap();
		List<Map<String, Object>> signData = Lists.newArrayList();
		Map<String, Object> info = Maps.newHashMap();
		info.put("signType", "个人");
		info.put("name", data.get("receiverCustName"));
		info.put("idCard", data.get("receiverCredentialsCode"));
		info.put("offsetX", "-0.01");
		info.put("offsetY", "-0.02");
		info.put("keyword", data.get("receiverCustName"));

		Map<String, Object> ourComapany = Maps.newHashMap();
		ourComapany.put("signType", "平台");
		ourComapany.put("keyword", "条款的效力");
		ourComapany.put("offsetX", "0.06");
		ourComapany.put("offsetY", "-0.06");
		
		signData.add(info);
		signData.add(ourComapany);
		sign.put("platType", "契约锁");
		sign.put("signData", signData);
		data.put("sign", sign);
		}
		
		// 对格式做特殊处理
//		data.put("receiverCustName", SharedUtil.replaceSpecialWord((String)(data.get("receiverCustName"))));
//		data.put("receiverCredentialsCode", SharedUtil.replaceSpecialWord((String)(data.get("receiverCredentialsCode"))));
//		data.put("senderCustName", SharedUtil.replaceSpecialWord((String)(data.get("senderCustName"))));
//		data.put("senderCredentialsCode", SharedUtil.replaceSpecialWord((String)(data.get("senderCredentialsCode"))));
//		data.put("sourceLoanUser", SharedUtil.replaceSpecialWord((String)(data.get("sourceLoanUser"))));
		data.put("investAmount", ArithUtil.formatNumber(data.get("investAmount")));
		data.put("yearRate", ArithUtil.formatPercent2((BigDecimal)data.get("yearRate"), 1, 5));
		data.put("effectDate", DateUtils.formatDate((Date)data.get("effectDate"), "yyyy年MM月dd日"));
		data.put("endDate", DateUtils.formatDate((Date)data.get("endDate"), "yyyy年MM月dd日"));
		return new ResultVo(true, "应收账款转让协议", data);
	}
	
	/**
	 * 善意贷-协议接口
	 */
	@SuppressWarnings("unchecked")
	public ResultVo queryFingertipContract(Map<String, Object> params) throws SLException {
//		String investId = (String) params.get("investId");
		String loanId = (String) params.get("loanId");
		Map<String, Object> data = loanInfoRepositoryCustom.queryFingertipContract(params);
		// income  :债权收益取还款计划所有利息
		BigDecimal income= repaymentPlanInfoRepository.sumRepaymentInterestByLoanIdNotAwardRate(loanId);

		// 投资人列表
		List<Map<String, Object>> investList = investInfoRepositoryCustom.queryInvestListByLoanId(loanId);
		// 资产信息列表
		List<Map<String, Object>> assetList = assetInfoRepository.findMapAssetInfoByloanId(loanId);
		
		// 投资人列表
		data.put("investorList", investList);
		// 资产信息列表
		data.put("assetList", assetList);
		if (params.get("isDownload") != null
				&& (Boolean) params.get("isDownload")) {
			// 善意贷 电签数据
			Map<String, Object> sign = Maps.newHashMap();
			List<Map<String, Object>> signData = Lists.newArrayList();
			for (Map<String, Object> invest : investList) {
				Map<String, Object> investPerson = Maps.newHashMap();
				investPerson.put("signType", "个人");
				investPerson.put("name", invest.get("custName"));
				investPerson.put("idCard", invest.get("idCard"));
				investPerson.put("offsetX", "-0.005");
				investPerson.put("offsetY", "-0.02");
				investPerson.put("keyword", invest.get("loginName").toString()
						+ invest.get("custName").toString());
				signData.add(investPerson);
			}
			Map<String, Object> ourComapany = Maps.newHashMap();
			ourComapany.put("signType", "平台");
			ourComapany.put("keyword", "条款的效力");
			ourComapany.put("offsetX", "0.06");
			ourComapany.put("offsetY", "-0.05");
			signData.add(ourComapany);
			sign.put("platType", "契约锁");
			sign.put("signData", signData);
			data.put("sign", sign);
		}
		
		// 对格式做特殊处理
		data.put("grantDateYear", DateUtils.formatDate((Date)data.get("effectDate"), "yyyy"));
		data.put("grantDateMonth", DateUtils.formatDate((Date)data.get("effectDate"), "MM"));
		data.put("grantDateDay", DateUtils.formatDate((Date)data.get("effectDate"), "dd"));

		data.put("waitingIncome", ArithUtil.formatNumber(income)+"元");
		data.put("loanAmount", ArithUtil.formatNumber(data.get("loanAmount"))+"元");
		data.put("typeTerm", Constant.LOAN_UNIT_DAY.equals(data.get("loanUnit"))?data.get("typeTerm")+"天":data.get("typeTerm")+"个月");
		data.put("yearRate", ArithUtil.formatPercent2((BigDecimal)data.get("yearRate"), 1, 5));
		data.put("effectDate", DateUtils.formatDate((Date)data.get("effectDate"), "yyyy年MM月dd日"));
		data.put("endDate", DateUtils.formatDate((Date)data.get("endDate"), "yyyy年MM月dd日"));

		for(Map<String, Object> invest : (List<Map<String, Object>>)data.get("investorList")) {
			invest.put("investAmount", ArithUtil.formatNumber(invest.get("investAmount")));
			invest.put("investDate", DateUtils.formatDate(DateUtils.parseDate((String)invest.get("investDate"), "yyyyMMdd"), "yyyy年MM月dd日"));
//			invest.put("custName", (String)invest.get("custName"));
			invest.put("idCard", SharedUtil.replaceSpecialWord((String)invest.get("idCard")));
		}
		
		for(Map<String, Object> asset : (List<Map<String, Object>>)data.get("assetList")) {
			asset.put("loanAmount", ArithUtil.formatNumber(asset.get("loanAmount")));
			asset.put("investEndDate", DateUtils.formatDate((Date)asset.get("investEndDate"), "yyyy年MM月dd日"));
//			asset.put("custName", SharedUtil.replaceSpecialWord((String)asset.get("custName")));
		}
		
		return new ResultVo(true, "借款与服务协议", data);
	}

	/**
	 * 查询协议通用接口
     * <tt>investId：投资ID </tt><br>
     * <tt>custId：客户ID</tt><br>
	 */
	public ResultVo queryContract(Map<String, Object> params)
			throws SLException {
		String investId = (String) params.get("investId");
		String custId = (String) params.get("custId");
		
		InvestInfoEntity investInfo = investInfoRepository.findOne(investId);
		if(investInfo == null){
			return new ResultVo(false, "投资信息取得出错");
		}
		LoanInfoEntity loanInfoEntity = loanInfoRepository.findByLoanInfoByInvestId(investId);
		if(loanInfoEntity == null){
			return new ResultVo(false, "借款信息取得出错");
		}
		
		// 产品类型
		String loanType = loanInfoEntity.getLoanType();
		Map<String, Object> subParam = new HashMap<String, Object>();
		ResultVo postVo;

		if(Constant.INVEST_METHOD_TRANSFER.equals(investInfo.getInvestMode())){
			if(StringUtils.isEmpty(investInfo.getTransferApplyId())){
				return new ResultVo(false, "债权信息取得出错");
			}
			// 借款的债权转让
			subParam.put("investId", investId);
			subParam.put("custId", custId);
			postVo = self.queryTransferContract(subParam);
		} else if(Constant.LOAN_PRODUCT_NAME_01.equals(loanType)){
			// 善转贷 loanId,custId
			subParam.put("loanId", loanInfoEntity.getId());
			subParam.put("custId", custId);
			postVo = self.queryAssetContract(subParam);
		} else if(Constant.LOAN_PRODUCT_NAME_02.equals(loanType)){
			// 善融贷-协议接口
			subParam.put("investId", investId);
			subParam.put("custId", custId);
			postVo = self.queryFinancingContract(subParam);
		} else if(Constant.LOAN_PRODUCT_NAME_03.equals(loanType)){
			// 善意贷-协议接口
			subParam.put("investId", investId);
			subParam.put("loanId", loanInfoEntity.getId());
			subParam.put("custId", custId);
			postVo = self.queryFingertipContract(subParam);
		} else if(Constant.DEBT_SOURCE_CODE_XCJF.equals(loanInfoEntity.getDebtSourceCode())){
			// 雪澄金服-协议接口
			subParam.put("investId", investId);
			subParam.put("loanId", loanInfoEntity.getId());
			subParam.put("custId", custId);
			postVo = self.querySnowOrangeContract(subParam);
		} else {
			// 其他借款 类型 loanId
			subParam.put("loanId", loanInfoEntity.getId());
			subParam.put("custId", custId);
			postVo = self.queryLoanContract(subParam);
		}
		
		if(ResultVo.isSuccess(postVo)){
			@SuppressWarnings("unchecked")
			Map<String, Object> data = (Map<String, Object>) postVo.getValue("data");
			data.put("investId", investId);
		}
		return postVo;
	}

	/**
	 * 查询 自动投标接口
	 */
	@Override
	public ResultVo queryAutoInvestList(Map<String, Object> params)
			throws SLException {
		Page<Map<String, Object>> autoInvestList = loanManagerRepositoryCustom.queryAutoInvestList(params);
		
		List<Map<String, Object>> autoInvestTotalAmountList = loanManagerRepositoryCustom.queryAutoInvestTotalAmount(params);
		BigDecimal totalAmount = (BigDecimal)autoInvestTotalAmountList.get(0).get("totalAmount");
		BigDecimal totalKeepAvailableAmount = (BigDecimal)autoInvestTotalAmountList.get(0).get("totalKeepAvailableAmount");
		BigDecimal totalRealAvailableAmount = (BigDecimal)autoInvestTotalAmountList.get(0).get("totalRealAvailableAmount");
		Map<String, Object> result = Maps.newHashMap();
		result.put("totalAmount", totalAmount!=null?totalAmount:BigDecimal.ZERO);
		result.put("totalKeepAvailableAmount", totalKeepAvailableAmount!=null?totalKeepAvailableAmount:BigDecimal.ZERO);
		result.put("totalRealAvailableAmount", totalRealAvailableAmount!=null?totalRealAvailableAmount:BigDecimal.ZERO);
//		for (Map<String, Object> map : autoInvestList.getContent()) {
//			String rm =(String)map.get("repaymentMethod");
//			String cp =(String)map.get("canInvestProduct");
//			if(cp!=null){
//				map.put("canInvestProduct", Arrays.asList(cp.split(",")));
//			}
//			if(rm!=null){
//				map.put("repaymentMethod", Arrays.asList(rm.split(",")));
//			}
//		}
		result.put("data", autoInvestList.getContent());
		result.put("iTotalDisplayRecords", autoInvestList.getTotalElements());
		return new ResultVo(true, "自动投标列表查询成功", result);
	}

	@Override
	public ResultVo queryAutoInvestInfo(Map<String, Object> params)
			throws SLException {
		String custId = params.get("custId").toString();
		AutoInvestInfoEntity autoInvestInfo = autoInvestInfoRepository.findByCustId(custId);
		if(autoInvestInfo == null){
			autoInvestInfo =new AutoInvestInfoEntity();
			autoInvestInfo.setOpenStatus("未设置");
		}
		Map<String, Object> result = Maps.newHashMap();
		result.put("limitedYearRate", ArithUtil.mul(autoInvestInfo.getLimitedYearRate(),new BigDecimal("100")));
		result.put("limitedYearRateMax", autoInvestInfo.getLimitedYearRateMax()==null?new BigDecimal("15"):ArithUtil.mul(autoInvestInfo.getLimitedYearRateMax(),new BigDecimal("100")));
		result.put("limitedTermMin", autoInvestInfo.getLimitedTermMin()==null?0:autoInvestInfo.getLimitedTermMin());
		result.put("limitedTerm", autoInvestInfo.getLimitedTerm()==null?24:autoInvestInfo.getLimitedTerm());
		result.put("keepAvailableAmount", autoInvestInfo.getKeepAvailableAmount()==null?new BigDecimal("0"):autoInvestInfo.getKeepAvailableAmount());
		result.put("openStatus", autoInvestInfo.getOpenStatus());
		result.put("pointStatus", autoInvestInfo.getPointStatus()==null?"Y":autoInvestInfo.getPointStatus());
		result.put("repaymentMethod", StringUtils.isEmpty(autoInvestInfo.getRepaymentMethod())?"等额本息,到期还本付息,每期还息到期付本":autoInvestInfo.getRepaymentMethod());
		result.put("canInvestProduct",  StringUtils.isEmpty(autoInvestInfo.getCanInvestProduct())?"优选项目（可转让标的）,优选项目（不可转让标的）,转让专区（可转让标的）,转让专区（不可转让标的）":autoInvestInfo.getCanInvestProduct());
		result.put("loanUnit", StringUtils.isEmpty(autoInvestInfo.getLoanUnit())?"月":autoInvestInfo.getLoanUnit());
		result.put("rechargePointStatus", autoInvestInfo.getRechargePointStatus()==null?"Y":autoInvestInfo.getRechargePointStatus());
		result.put("smallQuantityInvest", autoInvestInfo.getSmallQuantityInvest()==null?"否":autoInvestInfo.getSmallQuantityInvest());
		return new ResultVo(true, "自动投标信息查询成功", result);
	}

	/**
	 * 保存自动投标设置
	 * 
	 * @author fengyl
	 * @date 2017年3月8日
	 * @param params
	 *            Map < String, Object >
	 * @return ResultVo
	 * @throws SLException
	 */
	public ResultVo saveAutoInvest(Map<String, Object> params)throws SLException {
		String custId = params.get("custId").toString();
		// 防重复提交 先锁定
		ResultVo resultVo = new ResultVo(true);
		if (0 >= autoInvestRepositoryCustom.updateAutoInvestInfoToAvoidDuplicate(custId, true)) {
			return new ResultVo(false, "您已经提交智能投顾信息，请勿重复提交");
		}
		try {
			resultVo = innerClass.saveAutoInvest(params);
		} catch (Exception e) {
			resultVo = new ResultVo(false,"保存智能投顾失败！");
			log.error("保存智能投顾异常：" + e.getMessage());
		}finally{
			// 无论 保存成功还是失败 ，都要  再解锁
			autoInvestRepositoryCustom.updateAutoInvestInfoToAvoidDuplicate(custId, false);
		}
		return resultVo;
	}
	
	@Override
	/**
	 * 保存发布设置
	 * @author  guoyk
	 * @date    2017-06-23
	 * @param params
	 * @return ResultVo
	 * @throws SLException
	 */
	public ResultVo savePublishInstall(Map<String, Object> params)throws SLException {
		ResultVo resultVo = innerClass.savePublishInstall(params);
		if (ResultVo.isSuccess(resultVo)) {
			if (!StringUtils.isEmpty(params.get("isPublishNow")) && "Y".equals(params.get("isPublishNow").toString())) {
				self.publishLoanInfo(params);//立即发布
			}else {
				self.timerPublishLoanInfo(params);//定时发布
			}
		}
		return  new ResultVo(true, "保存成功");
	}
	
	@Service
	public static class InnerClass{
		@Autowired
		AutoInvestInfoRepository autoInvestInfoRepository;
		@Autowired
		LogInfoEntityRepository logInfoEntityRepository;
		@Autowired
		DeviceService deviceService;
		@Autowired
		ProjectInvestInfoRepository projectInvestInfoRepository;
		@Autowired
		LoanInfoRepository loanInfoRepository;
		@Autowired
		InterfaceDetailInfoRepository interfaceDetailInfoRepository;
		@Autowired
		AttachmentRepositoryCustom attachmentRepositoryCustom;
		@Autowired
		FlowNumberService numberService;
		@Autowired
		ExpandInfoService expandInfoService;
		@Autowired
		LoanRebateInfoRepository loanRebateInfoRepository;
		@Autowired
		CustInfoRepository custInfoRepository;
		@Autowired
		AccountInfoRepository accountInfoRepository;
		@Autowired
		InvestInfoRepository investInfoRepository;
		@Autowired
		InvestDetailInfoRepository investDetailInfoRepository;
		@Autowired
		CustRecommendInfoRepository custRecommendInfoRepository;
		@Autowired
		SubAccountInfoRepository subAccountInfoRepository;
		@Autowired
		CustAccountService custAccountService;
		@Autowired
	    LoanTransferApplyRepository loanTransferApplyRepository;
		@Autowired
		AuditInfoRepository auditInfoRepository;
		@Autowired
		WealthHoldInfoRepository wealthHoldInfoRepository;
		@Autowired
		ParamService paramService;
		@Autowired
		CreditRightValueRepository creditRightValueRepository;
		@Autowired
		AutoInvestRepositoryCustom autoInvestRepositoryCustom;

		@Autowired
		CustActivityService custActivityService;
		
		@Autowired
		LoanReserveRepository loanReserveRepository;
		
		@Autowired
		LoanReserveRelationRepository loanReserveRelationRepository;

		@Transactional(rollbackFor = SLException.class)
		public ResultVo buyDispersionExt(Map<String, Object> params) throws SLException {

			String custId = (String) params.get("custId");
			String loanId = (String) params.get("disperseId");
			BigDecimal tradeAmount = new BigDecimal(params.get("tradeAmount").toString()); //投资金额
			String appSource = (String)params.get("appSource");
			appSource = Strings.nullToEmpty(StringUtils.isEmpty(appSource) ? Constant.INVEST_SOURCE_PC : appSource).toLowerCase();
			String custActivityId = CommonUtils.emptyToString(params.get("custActivityId"));

			// check.0 判断用户是否正常
			CustInfoEntity custInfoEntity = custInfoRepository.findOne(custId);
			if(null == custInfoEntity) {
				return new ResultVo(false, "用户不存在！");
			}

			// check.1 验证投资人账户是否足额
			AccountInfoEntity investorAccount = accountInfoRepository.findByCustId(custId); //客户账户表
			if(tradeAmount.compareTo(investorAccount.getAccountAvailableAmount()) > 0) {
				return new ResultVo(false, "账户可用余额不足， 请充值！");
			}
			// check.2 验证项目是否存在且状态为发布中
			LoanInfoEntity loanInfoEntity = loanInfoRepository.findOne(loanId);
			if(loanInfoEntity == null || !Constant.LOAN_STATUS_05.equals(loanInfoEntity.getLoanStatus())){
				return new ResultVo(false, "该项目不在募集中， 请刷新重试！");
			}
			//新手标校验
			if (Constant.LOAN_INFO_NEWER_FLAG.equals(loanInfoEntity.getNewerFlag())) {
				BigDecimal investCount = investInfoRepository.investCountInfoByCustId(custId);
				if (investCount.compareTo(BigDecimal.ZERO) >0) {
					return new ResultVo(false, "您已出借过新手标！");
				}
			}
			// check.3 验证交易金额是否小于等于项目可投金额
			ProjectInvestInfoEntity projectInvestInfoEntity = projectInvestInfoRepository.findByLoanId(loanId);
			if(projectInvestInfoEntity == null){
				return new ResultVo(false, "项目出借情况数据异常！");
			}
			BigDecimal remainderAmount = ArithUtil.sub(loanInfoEntity.getLoanAmount(), projectInvestInfoEntity.getAlreadyInvestAmount());//剩余可投金额
			if(tradeAmount.compareTo(remainderAmount) > 0){
				return new ResultVo(false, String.format("项目剩余可出借金额%s元！", remainderAmount));
			}
			// 交易金额等于剩余可投金额表示最后一笔投资，不受起投金额、投资递增金额等条件制约
			remainderAmount = ArithUtil.sub(remainderAmount, tradeAmount);

			//  by guoyk 2017/5/17 SLCF-2821 如果是智能投顾,不判断起投金额
			BigDecimal investMinAmount = loanInfoEntity.getInvestMinAmount();
			if ("auto".equals(appSource)) {
				investMinAmount = BigDecimal.ZERO;
			}
			if(remainderAmount.compareTo(BigDecimal.ZERO) != 0) {
				// check.3 验证投资金额是否大于起投金额
				if(tradeAmount.compareTo(investMinAmount) < 0){
					return new ResultVo(false, "出借金额不能小于起投金额！");
				}
				// check.4 验证投资金额是否是递增倍数
				//  by guoyk 2017/6/1 SLCF-2821 如果是智能投顾,不判断递增金额
				if (!"auto".equals(appSource)) {
					if(tradeAmount.compareTo(investMinAmount) > 0
							&& !ArithUtil.isDivInt(loanInfoEntity.getIncreaseAmount(), ArithUtil.sub(tradeAmount, investMinAmount))){
						return new ResultVo(false, "出借金额必须是最小出借金额加递增金额整数倍！");
					}
				}

				// check.6 投资后剩余金额小于投标金额，需要补满
				if(remainderAmount.compareTo(investMinAmount) < 0){
					return new ResultVo(false, "出借后剩余可出借金额小于最小出借金额，您必须购买所有剩余可出借金额!");
				}
				//add by fengyl 新手标checek 投资后剩余可投金额大于起投金额,判断投资金额是否小于投资限额
				if (Constant.LOAN_INFO_NEWER_FLAG.equals(loanInfoEntity.getNewerFlag())&& tradeAmount.compareTo(loanInfoEntity.getInvestMaxAmount()) > 0
						&&remainderAmount.compareTo(investMinAmount) >= 0) {
					return new ResultVo(false, String.format("每人限购%s元！", loanInfoEntity.getInvestMaxAmount()));
				}
			}else{
				if (Constant.LOAN_INFO_NEWER_FLAG.equals(loanInfoEntity.getNewerFlag())) {
					if (tradeAmount.compareTo(ArithUtil.add(loanInfoEntity.getInvestMaxAmount(),investMinAmount)) >= 0) {
						return new ResultVo(false, String.format("每人限购%s元！",loanInfoEntity.getInvestMaxAmount()));
					}
				}
			}
			Map<String, Object> customerActivityInfo = new HashMap<String, Object>();
			//判断是否使用奖励
			String awardType = "";
			if(custActivityId != null && !custActivityId.isEmpty())
			{
				// 判断红包使用条件是否满足
				customerActivityInfo = custActivityService.findRewardByIdAndCustId(custActivityId, custId);
				if (customerActivityInfo == null) {
					return new ResultVo(false, "无有效的奖励记录！");
				}

				// 奖励提示
				String tip;
				// 奖励类型
				awardType = CommonUtils.emptyToString(customerActivityInfo.get("AWARDTYPE"));
				// 如果是加息券
				if (Constant.REAWARD_SPREAD_05.equals(awardType)) {
					tip = Constant.REAWARD_SPREAD_05;
				} else {
					// 满减红包 ，sql查询的时候已加条件限制
					tip = Constant.REAWARD_SPREAD_04;
				}
				// 如果是红包
				int startAmount = CommonUtils.emptyToInt(customerActivityInfo.get("STARTAMOUNT"));
				if (startAmount != 0 && tradeAmount.compareTo(new BigDecimal(startAmount)) < 0) {
					return new ResultVo(false, String.format(tip + "限制最小出借金额%s元！", startAmount));
				}
				String useScope = CommonUtils.emptyToString(customerActivityInfo.get("USESCOPE"));
				//还款方式
				String repayMent = CommonUtils
						.emptyToString(customerActivityInfo.get("REPAYMENTMETHODS"));
				//标的是否可转让
				String isTransfer = CommonUtils.emptyToString(customerActivityInfo.get("ISTRANSFER"));
				String seatTeam = loanInfoEntity.getSeatTerm() == -1 ? "不可转让" : "可转让";
				//标的还款方式
				if (!repayMent.isEmpty()
						&& repayMent.indexOf(loanInfoEntity.getRepaymentMethod()) == -1) {
					return new ResultVo(false,
							repayMent + tip + "不能用于" + loanInfoEntity.getRepaymentMethod() + "标!");
				}
				//标的是否可转让
				if (!isTransfer.isEmpty() && !isTransfer.equals(seatTeam)) {
					return new ResultVo(false, isTransfer + tip + "不能用于" + seatTeam + "的标!");
				}

				// 是新手标，并且 红包使用范围不包含新手标
				if (Constant.LOAN_INFO_NEWER_FLAG.equals(loanInfoEntity.getNewerFlag())
						&& useScope.indexOf(Constant.ACTIVITY_AWARD_USE_SCOPE_0) == -1) {
					return new ResultVo(false, "该"+tip+"不能在新手标使用!");
				}
				if (useScope.indexOf(Constant.ACTIVITY_AWARD_USE_SCOPE_1) == -1) {
					return new ResultVo(false, "该"+tip+"不能在优选项目使用!");
				}

				if (Constant.REAWARD_SPREAD_04.equals(awardType)) {
					// 如果有标的期限限制 并且期限单位不等
					String increaseUnit = CommonUtils.emptyToString(customerActivityInfo.get("INCREASEUNIT"));
					if (!increaseUnit.isEmpty() && !increaseUnit.equals(loanInfoEntity.getLoanUnit())) {
						// 期限单位不一致
						return new ResultVo(false, "项目期限不符合"+tip+"使用条件!");
					}
					Integer loanAllottedTime = CommonUtils
							.emptyToInt(customerActivityInfo.get("LOANALLOTTEDTIME"));

					// 如果有期数限制并且标的期数小于限制
					if (loanAllottedTime != 0 && loanInfoEntity.getLoanTerm() != null
							&& loanInfoEntity.getLoanTerm() < loanAllottedTime) {
						// 期数不符合
						return new ResultVo(false, "项目期限不符合"+tip+"使用条件!");
					}
				}
				// 校验完成
			}
			// todo list 1 修改可投金额、可投比例、投资人数、已投金额
			int investCounts = investInfoRepository.countByLoanIdAndCustIdAndInvestStatus(loanId,custId,Constant.INVEST_STATUS_INVESTING); // 阅读 todo list 2
			projectInvestInfoEntity.setAlreadyInvestAmount(ArithUtil.add(tradeAmount, projectInvestInfoEntity.getAlreadyInvestAmount()));//已投金额 = 已投金额 + 投资金额
			projectInvestInfoEntity.setAlreadyInvestScale(ArithUtil.div(projectInvestInfoEntity.getAlreadyInvestAmount(), loanInfoEntity.getLoanAmount()));//投资比例
			if(investCounts == 0){
				projectInvestInfoEntity.setAlreadyInvestPeoples(projectInvestInfoEntity.getAlreadyInvestPeoples() + 1);// 投资人+1
			}
			projectInvestInfoEntity.setBasicModelProperty(custId, false);

			// todo list 2 新建一笔投资
			/*
			1）投资表新增一条记录
			2）投资详情表新增一条记录
			3）投资人新增分账户，
			4）将投资用户分账户的资金打到借款用户分账户
			若是同一个标的重复投资，则合并为一笔投资
			*/
			// 2.1 投资表
			InvestInfoEntity investInfoEntity = new InvestInfoEntity();
			investInfoEntity.setCustId(custId);
			investInfoEntity.setInvestAmount(tradeAmount);
			investInfoEntity.setInvestStatus(Constant.INVEST_STATUS_INVESTING); //投资中
			investInfoEntity.setInvestMode(Constant.INVEST_METHOD_JOIN); //加入
			investInfoEntity.setInvestDate(DateUtils.formatDate(new Date(), "yyyyMMdd"));
			//investInfoEntity.setExpireDate(DateUtilSL.print(loanInfoEntity.getInvestEndDate(), "yyyyMMdd"));
			investInfoEntity.setLoanId(loanId);
			investInfoEntity.setBasicModelProperty(custId, true);
			//如果使用红包存入红包金额
			if(custActivityId != null && !custActivityId.isEmpty())
			{
				if (Constant.REAWARD_SPREAD_05.equals(awardType)) {
					// 加息券
					investInfoEntity.setRedPacketType(Constant.RATE_COUPON);
				} else {
					// 满减红包
					investInfoEntity.setRedPacketType(Constant.RED_PACKET);
				}
				investInfoEntity.setInvestRedPacket(CommonUtils.emptyToDecimal(customerActivityInfo.get("GRANTAMOUNT")));

				investInfoEntity.setCustActivityId(custActivityId);
			}

			// 添加客户经理
			CustRecommendInfoEntity custRecommendInfoEntity = custRecommendInfoRepository.findByQuiltCustIdAndRecordStatus(custInfoEntity.getId(), Constant.VALID_STATUS_VALID);
			if(null != custRecommendInfoEntity)
				investInfoEntity.setCustManagerId(custRecommendInfoEntity.getCustId());
			investInfoEntity = investInfoRepository.save(investInfoEntity);

			// 2.2 投资详情
			InvestDetailInfoEntity investDetailInfoEntity = new InvestDetailInfoEntity();
			investDetailInfoEntity.setInvestId(investInfoEntity.getId());
			investDetailInfoEntity.setTradeNo(numberService.generateLoanContractNumber());
			investDetailInfoEntity.setInvestAmount(tradeAmount);
			investDetailInfoEntity.setInvestSource(appSource);
			investDetailInfoEntity.setBasicModelProperty(custId, true);
			investDetailInfoEntity = investDetailInfoRepository.save(investDetailInfoEntity);

			// 2.3  添加分账、分账户流水
			// 2.3.1 添加投资人分账
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
			investorSubAccount.setAccountAmount(tradeAmount);
			investorSubAccount.setDeviationAmount(BigDecimal.ZERO);
			investorSubAccount = subAccountInfoRepository.save(investorSubAccount);

			String reqeustNo = numberService.generateTradeBatchNumber();

			// 	判断是否有使用红包
			if (custActivityId != null && !custActivityId.isEmpty()) {
				// 如果是加息劵
				if(Constant.REAWARD_SPREAD_05.equals(awardType)) {
					// 更改加息券使用状态
					// 加息券状态--全部使用
					// 最后更新时间、更新人
					// 标的编号
					// 投资金额
					// memo 投资编号 -- 请求编号
					CustActivityInfoEntity custActivityInfoEntity = custActivityService
							.findById(custActivityId);
					custActivityInfoEntity.setTradeStatus(Constant.USER_ACTIVITY_TRADE_STATUS_03);
					custActivityInfoEntity.setLastUpdateDate(new Date());
					custActivityInfoEntity.setLastUpdateUser(Constant.SYSTEM_USER_BACK);
					custActivityInfoEntity.setLoanId(loanId);
					custActivityInfoEntity
							.setInvestAmount(CommonUtils.emptyToString(investInfoEntity.getInvestAmount()));
					StringBuilder memo = new StringBuilder(
							CommonUtils.emptyToString(custActivityInfoEntity.getMemo()));
					memo.append("投资编号-")
							.append(investInfoEntity.getId())
							.append("-请求编号-")
							.append(reqeustNo)
							.append("-END");
					custActivityInfoEntity.setMemo(memo.toString());

					// 2.3.2 投资人主账户金额-加入金额、添加流水 更新客户主账户（投资人主账户——>投资人分账户）
					custAccountService.updateAccount(investorAccount, null, null,
							investorSubAccount, "2", SubjectConstant.TRADE_FLOW_TYPE_JOIN_LOAN,
							reqeustNo, tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_JOIN_LOAN,
							Constant.TABLE_BAO_T_INVEST_INFO, investInfoEntity.getId(),
							String.format("购买优选项目[%s-%s]", loanInfoEntity.getLoanType(), loanInfoEntity.getLoanDesc()), custId);
				} else {
					// 如果是红包
					// 红包金额
					BigDecimal usableAmount = CommonUtils
							.emptyToDecimal(customerActivityInfo.get("GRANTAMOUNT"));
					// 更改账户金额流水 -- 用户实际主账户金额 + 红包金额 - 投资金额
					// 2.3.2 投资人主账户金额-加入金额、添加流水 更新客户主账户（投资人主账户——>投资人分账户）
					// 记录红包账户流水
					custAccountService.updateAccountExt(investorAccount, null, null,
							investorSubAccount, "2", SubjectConstant.TRADE_FLOW_TYPE_JOIN_LOAN,
							reqeustNo, tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_JOIN_LOAN,
							Constant.TABLE_BAO_T_INVEST_INFO, investInfoEntity.getId(),
							String.format("购买优选项目[%s-%s],使用红包%s元", loanInfoEntity.getLoanType(),
									loanInfoEntity.getLoanDesc(), usableAmount), custId, usableAmount);
					// 更改红包使用状态
					// 红包状态--全部使用
					// 最后更新时间、更新人
					// 标的编号
					// 投资金额
					// memo 投资编号 -- 请求编号
					CustActivityInfoEntity custActivityInfoEntity = custActivityService
							.findById(custActivityId);
					custActivityInfoEntity.setTradeStatus(Constant.USER_ACTIVITY_TRADE_STATUS_03);
					custActivityInfoEntity.setLastUpdateDate(new Date());
					custActivityInfoEntity.setLastUpdateUser(Constant.SYSTEM_USER_BACK);
					custActivityInfoEntity.setLoanId(loanId);
					custActivityInfoEntity
							.setInvestAmount(CommonUtils.emptyToString(investInfoEntity.getInvestAmount()));
					StringBuilder memo = new StringBuilder(
							CommonUtils.emptyToString(custActivityInfoEntity.getMemo()));
					memo.append("待扣款-投资编号-")
							.append(investInfoEntity.getId())
							.append("-请求编号-")
							.append(reqeustNo)
							.append("-END");
					custActivityInfoEntity.setMemo(memo.toString());
					// 定时器定时更新 memo中待扣款的数据
				}
			} else {
				// 2.3.2 投资人主账户金额-加入金额、添加流水 更新客户主账户（投资人主账户——>投资人分账户）
				custAccountService.updateAccount(investorAccount, null, null,
						investorSubAccount, "2", SubjectConstant.TRADE_FLOW_TYPE_JOIN_LOAN,
						reqeustNo, tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_JOIN_LOAN,
						Constant.TABLE_BAO_T_INVEST_INFO, investInfoEntity.getId(),
						String.format("购买优选项目[%s-%s]", loanInfoEntity.getLoanType(), loanInfoEntity.getLoanDesc()), custId);
			}

			// 2.3.4 投资人分账户出
			// 借款分账户
			SubAccountInfoEntity loanerSubAccount = subAccountInfoRepository.findByRelatePrimary(loanInfoEntity.getId());
			custAccountService.updateAccount(null, investorSubAccount, null,
					loanerSubAccount, "4", SubjectConstant.TRADE_FLOW_TYPE_JOIN_LOAN,
					reqeustNo, tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_JOIN_LOAN,
					Constant.TABLE_BAO_T_INVEST_INFO, investInfoEntity.getId(),
					String.format("购买优选项目[%s-%s]", loanInfoEntity.getLoanType(), loanInfoEntity.getLoanDesc()), custId);

			// 3 记录设备信息(优选)
			Map<String, Object> deviceParams = Maps.newConcurrentMap();
			deviceParams.putAll(params);
			deviceParams.put("relateType", Constant.TABLE_BAO_T_INVEST_INFO);
			deviceParams.put("relatePrimary", investInfoEntity.getId());
			deviceParams.put("tradeType", Constant.OPERATION_TYPE_67);
			deviceParams.put("userId", custId);
			deviceService.saveUserDevice(deviceParams);

			// 4 记录日志
			LogInfoEntity logInfoEntity = new LogInfoEntity();
			logInfoEntity.setRelateType(Constant.TABLE_BAO_T_INVEST_INFO);
			logInfoEntity.setRelatePrimary(investInfoEntity.getId());
			logInfoEntity.setLogType(Constant.OPERATION_TYPE_51);
			logInfoEntity.setOperDesc("");
			logInfoEntity.setOperPerson(custId);
			logInfoEntity.setOperIpaddress((String)params.get("ipAddress"));
			logInfoEntity.setMemo(String.format("%s购买优选项目，投资金额%s", custInfoEntity.getLoginName(), tradeAmount.toString()));
			logInfoEntity.setBasicModelProperty(custId, true);
			logInfoEntityRepository.save(logInfoEntity);

			boolean fullScale = false;
			Map<String, Object> flag = Maps.newHashMap();
			flag.put("fullScale", false);
			flag.put("isNewerFlag", false);
			// 剩余可投金额为0，改项目为满标复核
			if(remainderAmount.compareTo(BigDecimal.ZERO) == 0) {
				boolean isNewerFlag = false;
				flag = Maps.newHashMap();
				if (Constant.LOAN_INFO_NEWER_FLAG.equals(loanInfoEntity.getNewerFlag())) {
					isNewerFlag = true;
				}
				loanInfoEntity.setLoanStatus(Constant.LOAN_STATUS_06);
				loanInfoEntity.setBasicModelProperty(custId, false);
				fullScale = true;
				flag.put("fullScale", fullScale);
				flag.put("isNewerFlag", isNewerFlag);

				if(Constant.DEBT_SOURCE_XCJF.equals(loanInfoEntity.getCompanyName())) {
					String repaymentMethod = loanInfoEntity.getRepaymentMethod();
					Integer loanTerm = Integer.parseInt(loanInfoEntity.getLoanTerm().toString());
					String loanType =  loanInfoEntity.getLoanType();
					String loanUnit =  loanInfoEntity.getLoanUnit();
					// 判断借款类型、还款方式、还款期限在项目折年系数表中是否为商务（Flag标识），若为商务则表示需要通知。
					if(needToNotifyBiz(repaymentMethod, loanTerm, loanType, loanUnit)
							|| Constant.PUSH_FLAG_YES.equals(loanInfoEntity.getPushFlag())){
						// 通知雪橙满标
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
							map.put("memo", Constant.OPERATION_TYPE_84);
							expandInfoService.saveExpandInfo(map);
						}
					}
				}
			}

			return new ResultVo(true, "购买操作成功", flag);
		}

		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public ResultVo buyDispersion(Map<String, Object> params) throws SLException{

			String custId = (String) params.get("custId"); 
			String loanId = (String) params.get("disperseId"); 
			BigDecimal tradeAmount = new BigDecimal(params.get("tradeAmount").toString()); //投资金额
			String appSource = (String)params.get("appSource");
			appSource = Strings.nullToEmpty(StringUtils.isEmpty(appSource) ? Constant.INVEST_SOURCE_PC : appSource).toLowerCase();
			
			// check.0 判断用户是否正常
			CustInfoEntity custInfoEntity = custInfoRepository.findOne(custId);
			if(null == custInfoEntity) {
				return new ResultVo(false, "用户不存在！");
			}
			
			// check.1 验证投资人账户是否足额
			AccountInfoEntity investorAccount = accountInfoRepository.findByCustId(custId); //客户账户表
			if(!StringUtils.isEmpty(params.get("loanManagerGroup")) && Constant.LOAN_MANAGER_GROUP_01.equals(params.get("loanManagerGroup"))) {
				if(tradeAmount.compareTo(investorAccount.getAccountFreezeAmount()) > 0) {
					return new ResultVo(false, "账户冻结金额不足， 请确认数据的正确性！");
				}
			}else{
				if(tradeAmount.compareTo(investorAccount.getAccountAvailableAmount()) > 0) {
					return new ResultVo(false, "账户可用余额不足， 请充值！");
				}
			}
			// check.2 验证项目是否存在且状态为发布中
			LoanInfoEntity loanInfoEntity = loanInfoRepository.findOne(loanId);
			if(loanInfoEntity == null){
				return new ResultVo(false, "该项目出错！");
			}
			// 如果是一键预约
			if(!StringUtils.isEmpty(params.get("loanManagerGroup")) && Constant.LOAN_MANAGER_GROUP_01.equals(params.get("loanManagerGroup"))) {
				if(!Constant.LOAN_MANAGER_GROUP_03.equals(loanInfoEntity.getLoanStatus())){
					return new ResultVo(false, "该项目不在募集中， 请刷新重试！");
				}
			} else {
				if(!Constant.LOAN_STATUS_05.equals(loanInfoEntity.getLoanStatus())){
					return new ResultVo(false, "该项目不在募集中， 请刷新重试！");
				}
			}
			//新手标校验
			if (Constant.LOAN_INFO_NEWER_FLAG.equals(loanInfoEntity.getNewerFlag())) {
				BigDecimal investCount = investInfoRepository.investCountInfoByCustId(custId);
				if (investCount.compareTo(BigDecimal.ZERO) >0) {
					return new ResultVo(false, "您已投资过新手标！");
				}
			}
			// check.3 验证交易金额是否小于等于项目可投金额
			ProjectInvestInfoEntity projectInvestInfoEntity = projectInvestInfoRepository.findByLoanId(loanId);
			if(projectInvestInfoEntity == null){
				return new ResultVo(false, "项目投资情况数据异常！");
			}
			BigDecimal remainderAmount = ArithUtil.sub(loanInfoEntity.getLoanAmount(), projectInvestInfoEntity.getAlreadyInvestAmount());//剩余可投金额
			if(tradeAmount.compareTo(remainderAmount) > 0){
				return new ResultVo(false, String.format("项目剩余出借金额%s元！", remainderAmount));
			}
			// 交易金额等于剩余可投金额表示最后一笔投资，不受起投金额、投资递增金额等条件制约
			remainderAmount = ArithUtil.sub(remainderAmount, tradeAmount);
			
			//  by guoyk 2017/5/17 SLCF-2821 如果是智能投顾,不判断起投金额
			BigDecimal investMinAmount = loanInfoEntity.getInvestMinAmount();
			if ("auto".equals(appSource)) {
				investMinAmount = BigDecimal.ZERO;
			}
			
			if(!Constant.LOAN_MANAGER_GROUP_01.equals(params.get("loanManagerGroup"))) {
				if(remainderAmount.compareTo(BigDecimal.ZERO) != 0) { 
					// check.3 验证投资金额是否大于起投金额
					if(tradeAmount.compareTo(investMinAmount) < 0){
						return new ResultVo(false, "出借金额不能小于起投金额！");
					}
					// check.4 验证投资金额是否是递增倍数
					//  by guoyk 2017/6/1 SLCF-2821 如果是智能投顾,不判断递增金额
					if (!"auto".equals(appSource)) {
						if(tradeAmount.compareTo(investMinAmount) > 0 
								&& !ArithUtil.isDivInt(loanInfoEntity.getIncreaseAmount(), ArithUtil.sub(tradeAmount, investMinAmount))){
							return new ResultVo(false, "投资金额必须是起投金额加递增金额整数倍！");
						}
					}
					
					// check.6 投资后剩余金额小于投标金额，需要补满
					if(remainderAmount.compareTo(investMinAmount) < 0){
						return new ResultVo(false, "出借后剩余可出借金额小于起投金额，您必须出借所有剩余可出借金额!");
					}
					//add by fengyl 新手标checek 投资后剩余可投金额大于起投金额,判断投资金额是否小于投资限额  
					if (Constant.LOAN_INFO_NEWER_FLAG.equals(loanInfoEntity.getNewerFlag())&& tradeAmount.compareTo(loanInfoEntity.getInvestMaxAmount()) > 0
							&&remainderAmount.compareTo(investMinAmount) >= 0) {
						return new ResultVo(false, String.format("每人限购%s元！", loanInfoEntity.getInvestMaxAmount()));
					}
				}else{
					if (Constant.LOAN_INFO_NEWER_FLAG.equals(loanInfoEntity.getNewerFlag())) {
						if (tradeAmount.compareTo(ArithUtil.add(loanInfoEntity.getInvestMaxAmount(),investMinAmount)) >= 0) {
							return new ResultVo(false, String.format("每人限购%s元！",loanInfoEntity.getInvestMaxAmount()));
						}
					}
				}
			}

			// todo list 1 修改可投金额、可投比例、投资人数、已投金额
			int investCounts = investInfoRepository.countByLoanIdAndCustIdAndInvestStatus(loanId,custId,Constant.INVEST_STATUS_INVESTING); // 阅读 todo list 2
			projectInvestInfoEntity.setAlreadyInvestAmount(ArithUtil.add(tradeAmount, projectInvestInfoEntity.getAlreadyInvestAmount()));//已投金额 = 已投金额 + 投资金额
			projectInvestInfoEntity.setAlreadyInvestScale(ArithUtil.div(projectInvestInfoEntity.getAlreadyInvestAmount(), loanInfoEntity.getLoanAmount()));//投资比例
			if(investCounts == 0){
				projectInvestInfoEntity.setAlreadyInvestPeoples(projectInvestInfoEntity.getAlreadyInvestPeoples() + 1);// 投资人+1
			}
			projectInvestInfoEntity.setBasicModelProperty(custId, false);
			
			// todo list 2 新建一笔投资
			/*
			1）投资表新增一条记录
			2）投资详情表新增一条记录
			3）投资人新增分账户，
			4）将投资用户分账户的资金打到借款用户分账户
			若是同一个标的重复投资，则合并为一笔投资
			*/
			// 2.1 投资表
			InvestInfoEntity investInfoEntity = new InvestInfoEntity();
			investInfoEntity.setCustId(custId);
			investInfoEntity.setInvestAmount(tradeAmount);
			investInfoEntity.setInvestStatus(Constant.INVEST_STATUS_INVESTING); //投资中
			investInfoEntity.setInvestMode(Constant.INVEST_METHOD_JOIN); //加入
			investInfoEntity.setInvestDate(DateUtils.formatDate(new Date(), "yyyyMMdd"));
			//investInfoEntity.setExpireDate(DateUtilSL.print(loanInfoEntity.getInvestEndDate(), "yyyyMMdd"));
			investInfoEntity.setLoanId(loanId);
			investInfoEntity.setBasicModelProperty(custId, true);
			if(!StringUtils.isEmpty(params.get("loanManagerGroup")) && Constant.LOAN_MANAGER_GROUP_01.equals(params.get("loanManagerGroup"))) {
				investInfoEntity.setGroupBatchNo((String) params.get("groupBatchNo"));
			}
			
			// 添加客户经理
			CustRecommendInfoEntity custRecommendInfoEntity = custRecommendInfoRepository.findByQuiltCustIdAndRecordStatus(custInfoEntity.getId(), Constant.VALID_STATUS_VALID);
			if(null != custRecommendInfoEntity) 
			investInfoEntity.setCustManagerId(custRecommendInfoEntity.getCustId());
			investInfoEntity = investInfoRepository.save(investInfoEntity);
			
			// 2.2 投资详情
			InvestDetailInfoEntity investDetailInfoEntity = new InvestDetailInfoEntity();
			investDetailInfoEntity.setInvestId(investInfoEntity.getId());
			investDetailInfoEntity.setTradeNo(numberService.generateLoanContractNumber());
			investDetailInfoEntity.setInvestAmount(tradeAmount);
			investDetailInfoEntity.setInvestSource(appSource);
			investDetailInfoEntity.setBasicModelProperty(custId, true);
			investDetailInfoEntity = investDetailInfoRepository.save(investDetailInfoEntity);
			
			// 2.3  添加分账、分账户流水
			// 2.3.1 添加投资人分账
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
			investorSubAccount.setAccountAmount(tradeAmount);
			investorSubAccount.setDeviationAmount(BigDecimal.ZERO);
			investorSubAccount = subAccountInfoRepository.save(investorSubAccount);
			
			String reqeustNo = StringUtils.isEmpty((String) params.get("groupBatchNo")) ? numberService.generateTradeBatchNumber() : (String) params.get("groupBatchNo");
			// 如果是一键预约先解冻
			if(!StringUtils.isEmpty(params.get("loanManagerGroup")) && Constant.LOAN_MANAGER_GROUP_01.equals(params.get("loanManagerGroup"))) {
				/*if(!StringUtils.isEmpty(params.get("loanReserveId"))){
					LoanReserveEntity loanReserve = loanReserveRepository.findOne(params.get("loanReserveId").toString());
					if(loanReserve != null){
						loanReserve.setAlreadyInvestAmount(ArithUtil.add(loanReserve.getAlreadyInvestAmount(), tradeAmount));
						loanReserve.setRemainderAmount(ArithUtil.sub(loanReserve.getRemainderAmount(), tradeAmount));
						loanReserve.setBasicModelProperty(custId, false);
						
						LoanReserveRelationEntity relation = new LoanReserveRelationEntity();
						relation.setBasicModelProperty(custId, true);
						relation.setLoanReserveId(loanReserve.getId());
						relation.setCustId(custId);
						relation.setLoanId(loanId);
						relation.setInvestId(investInfoEntity.getId());
						relation.setTradeAmount(tradeAmount);
						relation.setMemo("一键出借对应关系记录");
						loanReserveRelationRepository.save(relation);
					}
				}*/
				
				custAccountService.updateAccount(investorAccount, null, null, 
						null, "8", SubjectConstant.TRADE_FLOW_TYPE_BUY_GROUP_UNFREEZE, 
						reqeustNo, tradeAmount, SubjectConstant.TRADE_FLOW_TYPE_BUY_GROUP_UNFREEZE, 
						Constant.TABLE_BAO_T_INVEST_INFO, investInfoEntity.getId(),  
						SubjectConstant.TRADE_FLOW_TYPE_BUY_GROUP_UNFREEZE_MEMO, custId);
			}

			// 2.3.2 投资人主账户金额-加入金额、添加流水 更新客户主账户（投资人主账户——>投资人分账户）
			custAccountService.updateAccount(investorAccount, null, null, 
					investorSubAccount, "2", SubjectConstant.TRADE_FLOW_TYPE_JOIN_LOAN, 
					reqeustNo, tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_JOIN_LOAN, 
					Constant.TABLE_BAO_T_INVEST_INFO, investInfoEntity.getId(),  
					String.format("购买优选项目[%s-%s]", loanInfoEntity.getLoanType(), loanInfoEntity.getLoanDesc()), custId);
			
			// 2.3.4 投资人分账户出
			// 借款分账户
			SubAccountInfoEntity loanerSubAccount = subAccountInfoRepository.findByRelatePrimary(loanInfoEntity.getId());
			custAccountService.updateAccount(null, investorSubAccount, null,
					loanerSubAccount, "4", SubjectConstant.TRADE_FLOW_TYPE_JOIN_LOAN, 
					reqeustNo, tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_JOIN_LOAN, 
					Constant.TABLE_BAO_T_INVEST_INFO, investInfoEntity.getId(),  
					String.format("购买优选项目[%s-%s]", loanInfoEntity.getLoanType(), loanInfoEntity.getLoanDesc()), custId);
			
			// 3 记录设备信息(优选)
			Map<String, Object> deviceParams = Maps.newHashMap();
			deviceParams.putAll(params);
			deviceParams.put("relateType", Constant.TABLE_BAO_T_INVEST_INFO);
			deviceParams.put("relatePrimary", investInfoEntity.getId());
			deviceParams.put("tradeType", Constant.OPERATION_TYPE_67);
			deviceParams.put("userId", custId);
			deviceService.saveUserDevice(deviceParams);
			
			// 4 记录日志
			LogInfoEntity logInfoEntity = new LogInfoEntity();
			logInfoEntity.setRelateType(Constant.TABLE_BAO_T_INVEST_INFO);
			logInfoEntity.setRelatePrimary(investInfoEntity.getId());
			logInfoEntity.setLogType(Constant.OPERATION_TYPE_51);
			logInfoEntity.setOperDesc("");
			logInfoEntity.setOperPerson(custId);
			logInfoEntity.setOperIpaddress((String)params.get("ipAddress"));
			logInfoEntity.setMemo(String.format("%s购买优选项目，投资金额%s", custInfoEntity.getLoginName(), tradeAmount.toString()));
			logInfoEntity.setBasicModelProperty(custId, true);
			logInfoEntityRepository.save(logInfoEntity);
			
			boolean fullScale = false;
			Map<String, Object> flag = Maps.newHashMap();
			flag.put("fullScale", false);
			flag.put("isNewerFlag", false);
			// 剩余可投金额为0，改项目为满标复核	
			if(remainderAmount.compareTo(BigDecimal.ZERO) == 0) {
				boolean isNewerFlag = false;
			    flag = Maps.newHashMap();
				if (Constant.LOAN_INFO_NEWER_FLAG.equals(loanInfoEntity.getNewerFlag())) {
					isNewerFlag = true;
				}
				loanInfoEntity.setLoanStatus(Constant.LOAN_STATUS_06);
				loanInfoEntity.setBasicModelProperty(custId, false);
				fullScale = true;
				flag.put("fullScale", fullScale);
				flag.put("isNewerFlag", isNewerFlag);
				
				if(Constant.DEBT_SOURCE_XCJF.equals(loanInfoEntity.getCompanyName())) {
					String repaymentMethod = loanInfoEntity.getRepaymentMethod();
					Integer loanTerm = Integer.parseInt(loanInfoEntity.getLoanTerm().toString());
					String loanType =  loanInfoEntity.getLoanType();
					String loanUnit =  loanInfoEntity.getLoanUnit();
					// 判断借款类型、还款方式、还款期限在项目折年系数表中是否为商务（Flag标识），若为商务则表示需要通知。
					if(needToNotifyBiz(repaymentMethod, loanTerm, loanType, loanUnit)
							|| Constant.PUSH_FLAG_YES.equals(loanInfoEntity.getPushFlag())){
						// 通知雪橙满标
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
							map.put("memo", Constant.OPERATION_TYPE_84);
							expandInfoService.saveExpandInfo(map);
						}
					}
				}
			}
			
			flag.put("investId", investInfoEntity.getId());
			return new ResultVo(true, "购买操作成功", flag);
		}
		
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public ResultVo saveAutoInvest(Map<String, Object> params)
				throws SLException {
			String custId = params.get("custId").toString();
			if(params.get("keepAvailableAmount")!=null&&Integer.valueOf(params.get("keepAvailableAmount").toString())<0){
				return new ResultVo(false,"保留余额不能为负数");
			}
			if(params.get("limitedTerm")!=null&&Integer.valueOf(params.get("limitedTerm").toString())<0){
				return new ResultVo(false,"最大期限不能为负数");
			}
			if(params.get("limitedTermMin")!=null&&Integer.valueOf(params.get("limitedTermMin").toString())<0){
				return new ResultVo(false,"最小期限不能为负数");
			}
			if(params.get("limitedYearRateMax")!=null&&Integer.valueOf(params.get("limitedYearRateMax").toString())<0){
				return new ResultVo(false,"最大年利率不能为负数");
			}
			if(params.get("limitedYearRate")!=null&&Integer.parseInt(params.get("limitedYearRate").toString())<0){
				return new ResultVo(false,"最小年利率不能为负数");
			}
			if(params.get("limitedYearRateMax")!=null&&Integer.parseInt(params.get("limitedYearRateMax").toString())>15){
				return new ResultVo(false, "年化利率最高不能超过15%");
			}
			if("月".equals(CommonUtils.emptyToString(params.get("loanUnit")))&& Integer.parseInt(params.get("limitedTerm").toString())>99){
				return new ResultVo(false, "投资期限最高不能超过99个月");
			}
			if("天".equals(CommonUtils.emptyToString(params.get("loanUnit")))&& Integer.parseInt(params.get("limitedTerm").toString())>2970){
				return new ResultVo(false, "按天计算后，投资期限最高输入值不能超过2970天（99个月）");
			}
			
			
			AutoInvestInfoEntity autoInvestInfo = autoInvestInfoRepository.findByCustId(custId);
			if (autoInvestInfo == null) {
				autoInvestInfo = new AutoInvestInfoEntity();
				autoInvestInfo.setBasicModelProperty(custId, true);
				autoInvestInfo.setCustId(custId);
				autoInvestInfo.setCustPriority("1");
			} else {
				autoInvestInfo.setBasicModelProperty(custId, false);
			}
			
			String beforeRate = autoInvestInfo.getLimitedYearRate()==null?"":autoInvestInfo.getLimitedYearRate().setScale(2,BigDecimal.ROUND_HALF_UP).toPlainString();
			String beforeTerm = autoInvestInfo.getLimitedTerm()==null?"":autoInvestInfo.getLimitedTerm().toString();
			String beforeMaxYearRate = autoInvestInfo.getLimitedYearRateMax()==null?"":autoInvestInfo.getLimitedYearRateMax().setScale(2,BigDecimal.ROUND_HALF_UP).toPlainString();
			String beforeTermMin = autoInvestInfo.getLimitedTermMin()==null?"":autoInvestInfo.getLimitedTermMin().toString();
			String beforeRepaymentMethod = autoInvestInfo.getRepaymentMethod()==null?"":autoInvestInfo.getRepaymentMethod();
			String beforeCanTransferProduct= autoInvestInfo.getCanInvestProduct()==null?"":autoInvestInfo.getCanInvestProduct();
			String beforeKeepAvailableAmount= autoInvestInfo.getKeepAvailableAmount()==null?"":autoInvestInfo.getKeepAvailableAmount().toString();
			String beforeLoanUnit = autoInvestInfo.getLoanUnit()==null?"":autoInvestInfo.getLoanUnit().toString();
			String beforeSmallQuantityInvest = autoInvestInfo.getSmallQuantityInvest()==null?"":autoInvestInfo.getSmallQuantityInvest().toString();
			String beforeRechargePointStatus = autoInvestInfo.getRechargePointStatus()==null?"":autoInvestInfo.getRechargePointStatus().toString();
			
			BigDecimal KeepAvailableAmount= params.get("keepAvailableAmount")==null?new BigDecimal("0"):new BigDecimal(params.get("keepAvailableAmount").toString());
			BigDecimal limitedYearRateMax= params.get("limitedYearRateMax")==null?new BigDecimal("15"):new BigDecimal(params.get("limitedYearRateMax").toString());
			Integer limitedTermMin= params.get("limitedTermMin")==null?Integer.valueOf("0"):Integer.parseInt(params.get("limitedTermMin").toString());
			Integer limitedTerm= params.get("limitedTerm")==null?Integer.valueOf("99"):Integer.parseInt(params.get("limitedTerm").toString());
			
			autoInvestInfo.setLimitedYearRate(ArithUtil.div(new BigDecimal(params.get("limitedYearRate").toString()),new BigDecimal("100")));
			autoInvestInfo.setLimitedYearRateMax(ArithUtil.div(limitedYearRateMax,new BigDecimal("100")));
			autoInvestInfo.setLimitedTerm(limitedTerm);
			autoInvestInfo.setLimitedTermMin(limitedTermMin);
			autoInvestInfo.setRepaymentMethod((String)params.get("repaymentMethod"));
			autoInvestInfo.setCanInvestProduct((String)params.get("canInvestProduct"));
			autoInvestInfo.setKeepAvailableAmount(KeepAvailableAmount);
			autoInvestInfo.setLoanUnit(CommonUtils.emptyToString(params.get("loanUnit")));
			autoInvestInfo.setRechargePointStatus(CommonUtils.emptyToString(params.get("rechargePointStatus")));
			autoInvestInfo.setSmallQuantityInvest(CommonUtils.emptyToString(params.get("smallQuantityInvest")));
			autoInvestInfo = autoInvestInfoRepository.save(autoInvestInfo);
			
			LogInfoEntity logInfoEntity = new LogInfoEntity();
			logInfoEntity.setBasicModelProperty(custId, true);
			logInfoEntity.setRelateType(Constant.TABLE_BAO_T_AUTO_INVEST_INFO);
			logInfoEntity.setRelatePrimary(autoInvestInfo.getId());
			logInfoEntity.setLogType(Constant.JOB_NAME_AUTO_INVEST);
			logInfoEntity.setOperBeforeContent(String.format(
					"最小利率=%s，最大利率=%s，最小期限=%s，最大期限=%s，还款方式=%s，项目类型=%s，保留余额=%s，期限单位=%s，小额复投=%s，充值页面提醒状态=%s",
					beforeRate,beforeMaxYearRate,beforeTermMin,beforeTerm,beforeRepaymentMethod,beforeCanTransferProduct,beforeKeepAvailableAmount,beforeLoanUnit,beforeSmallQuantityInvest,beforeRechargePointStatus));
			logInfoEntity.setOperAfterContent(String.format(
					"最小利率=%s，最大利率=%s，最小期限=%s，最大期限=%s，还款方式=%s，项目类型=%s，保留余额=%s，期限单位=%s，小额复投=%s，充值页面提醒状态=%s", 
					autoInvestInfo.getLimitedYearRate().setScale(2,BigDecimal.ROUND_HALF_UP).toPlainString(),
					autoInvestInfo.getLimitedYearRateMax().setScale(2,BigDecimal.ROUND_HALF_UP).toPlainString(),
					autoInvestInfo.getLimitedTermMin().toString(),
					autoInvestInfo.getLimitedTerm().toString(),
					autoInvestInfo.getRepaymentMethod(),
					autoInvestInfo.getCanInvestProduct(),
					autoInvestInfo.getKeepAvailableAmount().toString(),
					autoInvestInfo.getLoanUnit().toString(),
					autoInvestInfo.getSmallQuantityInvest(),
					autoInvestInfo.getRechargePointStatus()
					));
			logInfoEntity.setOperDesc("");
			logInfoEntity.setOperPerson(custId);
			logInfoEntityRepository.save(logInfoEntity);
			
			// 记录设备信息
			Map<String, Object> deviceParams = Maps.newConcurrentMap();
			deviceParams.putAll(params);
			deviceParams.put("relateType", Constant.TABLE_BAO_T_AUTO_INVEST_INFO);
			deviceParams.put("relatePrimary", autoInvestInfo.getId());
			deviceParams.put("tradeType", Constant.JOB_NAME_AUTO_INVEST);
			deviceParams.put("userId", custId);
			deviceService.saveUserDevice(deviceParams);
			
			if(!Constant.AUTO_INVEST_INFO_OPEN_STATUS_01.equals(autoInvestInfo.getOpenStatus())){
				autoInvestInfo.setOpenStatus(Constant.AUTO_INVEST_INFO_OPEN_STATUS_01);
				autoInvestInfo.setOpenDate(new Date());
			}
			Map<String, Object> dataMap = Maps.newHashMap();
			dataMap.put("openStatus", autoInvestInfo.getOpenStatus());
			return new ResultVo(true, "保存自动投标设置成功", dataMap);
		}
		
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public ResultVo enableAutoInvest(Map<String, Object> params)
				throws SLException {
			String openStatus=params.get("openStatus").toString();
			String custId = params.get("custId").toString();
			if (!Constant.AUTO_INVEST_INFO_OPEN_STATUS_01.equals(openStatus)
					&& !Constant.AUTO_INVEST_INFO_OPEN_STATUS_02.equals(openStatus)) {
				return new ResultVo(false, "自动投标状态只能是启用或者禁用");
			}
			AutoInvestInfoEntity autoInvestInfo = autoInvestInfoRepository.findByCustId(custId);
			String beforeContent = autoInvestInfo.getOpenStatus();
			
			autoInvestInfo.setBasicModelProperty(custId, false);
			autoInvestInfo.setOpenStatus(openStatus);
			autoInvestInfo.setOpenDate(new Date());
			autoInvestInfo = autoInvestInfoRepository.save(autoInvestInfo);
			
			LogInfoEntity logInfoEntity = new LogInfoEntity();
			logInfoEntity.setBasicModelProperty(custId, true);
			logInfoEntity.setRelateType(Constant.TABLE_BAO_T_AUTO_INVEST_INFO);
			logInfoEntity.setRelatePrimary(autoInvestInfo.getId());
			logInfoEntity.setLogType(Constant.JOB_NAME_AUTO_INVEST);
			logInfoEntity.setOperBeforeContent(autoInvestInfo==null?"":beforeContent);
			logInfoEntity.setOperAfterContent(openStatus);
			logInfoEntity.setOperDesc("");
			logInfoEntity.setOperPerson(custId);
			logInfoEntityRepository.save(logInfoEntity);

			return new ResultVo(true, "设置成功");
		}
		
		@Transactional(readOnly = false, rollbackFor=SLException.class)
		@SuppressWarnings("unchecked")
		public ResultVo publishLoanInfo(Map<String, Object> params) throws SLException {
			String loanId = (String) params.get("loanId");
			String userId = (String)params.get("userId");
			LoanInfoEntity loanInfo = loanInfoRepository.findOne(loanId);
			if(!Constant.LOAN_STATUS_04.equals(loanInfo.getLoanStatus())) {
				return new ResultVo(false, "该项目不是待发布状态");
			}
			loanInfo.setLoanStatus(Constant.LOAN_STATUS_05);
			loanInfo.setPublishDate(new Date());
			loanInfo.setRasieEndDate(DateUtils.getDateAfterByHour(new Date(), loanInfo.getRasieDays()*24));
			//设置 标识符 用来判断是否需要发布时调用智能投顾
			boolean flag = true;
			//新发布的标在允许智能投顾的前提下，修改状态为N.并且在智能投顾结束后，修改状态为是
			if (Constant.IS_ALLOW_AUTO_INVEST_01.equals(loanInfo.getIsAllowAutoInvest())) {
				loanInfo.setIsRunAutoInvest(Constant.IS_RUN_AUTO_INVEST_02);
			}else {
				loanInfo.setIsRunAutoInvest(Constant.IS_RUN_AUTO_INVEST_01);
				flag = false;
			}
			// 初始化已投比例等数据
			ProjectInvestInfoEntity projectInvestInfoEntity = new ProjectInvestInfoEntity();
			projectInvestInfoEntity.setLoanId(loanId);
			projectInvestInfoEntity.setAlreadyInvestPeoples(0);
			projectInvestInfoEntity.setAlreadyInvestAmount(BigDecimal.ZERO);
			projectInvestInfoEntity.setAlreadyInvestScale(BigDecimal.ZERO);
			projectInvestInfoEntity.setBasicModelProperty(userId, true);
			projectInvestInfoRepository.save(projectInvestInfoEntity);
			
			//附件信息
			List<Map<String,Object>> auditList = (List<Map<String, Object>>)params.get("auditList");
			List<AttachmentInfoEntity> list = Lists.newArrayList();
			
			//审核日志
			if(auditList !=null && auditList.size() > 0) {
				for (Map<String, Object> arr : auditList) {
					String auditId = (String) arr.get("auditId"); //审核id
					List<Map<String, Object>> attachmentList = (List<Map<String, Object>>) arr.get("attachmentList");
					
					//审核附件
					for (Map<String, Object> att : attachmentList) {
						AttachmentInfoEntity a = new AttachmentInfoEntity();
						
						String attName = (String)att.get("attachmentName");
						a.setBasicModelProperty(userId, true);
						a.setRelatePrimary(auditId);
						a.setRelateType(Constant.TABLE_BAO_T_AUDIT_INFO);
						
						a.setAttachmentType((String)att.get("attachmentType"));
						a.setAttachmentName(attName);
						a.setStoragePath((String) att.get("storagePath"));
						a.setDocType(attName.split("\\.")[1]);
						a.setShowType(Constant.SHOW_TYPE_EXTERNAL);
						
						list.add(a);
					}
				}
			}
			//保存附件
			if(list.size() > 0) {
				attachmentRepositoryCustom.batchInsert(list);
			}
			
			String repaymentMethod = loanInfo.getRepaymentMethod();
			Integer loanTerm = Integer.parseInt(loanInfo.getLoanTerm().toString());
			String loanType = loanInfo.getLoanType();
			String loanUnit = loanInfo.getLoanUnit();
			// 判断借款类型、还款方式、还款期限在项目折年系数表中是否为商务（Flag标识），若为商务则表示需要通知。
			if(needToNotifyBiz(repaymentMethod, loanTerm, loanType, loanUnit)
					|| Constant.PUSH_FLAG_YES.equals(loanInfo.getPushFlag())){
				//通知善林商务发布
				InterfaceDetailInfoEntity interfaceDetailInfoEntity = interfaceDetailInfoRepository.findByThirdPartyTypeInterfaceType(loanInfo.getCompanyName(), Constant.NOTIFY_TYPE_LOAN_STATUS);
				if (interfaceDetailInfoEntity != null) {
					Map<String, Object> map = Maps.newHashMap();
					map.put("relateType", Constant.TABLE_BAO_T_LOAN_INFO);
					map.put("relatePrimary", loanId);
					map.put("tradeCode", numberService.generateTradeNumber());
					map.put("innerTradeCode", numberService.generateOpenServiceTradeNumber());
					map.put("interfaceDetailInfoEntity", interfaceDetailInfoEntity);
					map.put("userId", userId);
					map.put("memo", Constant.OPERATION_TYPE_68);
					expandInfoService.saveExpandInfo(map);
				}
			}
			
			//添加日志
			LogInfoEntity logInfoEntity = new LogInfoEntity();
			logInfoEntity.setBasicModelProperty(userId, true);
			logInfoEntity.setRelateType(Constant.TABLE_BAO_T_LOAN_INFO);
			logInfoEntity.setRelatePrimary(loanId);
			logInfoEntity.setLogType(Constant.OPERATION_TYPE_68);
			logInfoEntity.setOperBeforeContent(Constant.PROJECT_STATUS_05);
			logInfoEntity.setOperAfterContent(Constant.PROJECT_STATUS_06);
			logInfoEntity.setOperDesc("发布");
			logInfoEntity.setOperPerson(userId);
			logInfoEntityRepository.save(logInfoEntity);
			
			Map<String, Object> dataMap = Maps.newHashMap();
			dataMap.put("flag", flag);
			return new ResultVo(true, "操作成功",dataMap);
		}
		
		/**
		 * 判断借款类型、还款方式、还款期限在项目折年系数表中是否为商务（Flag标识），若为商务则表示需要通知。
		 */
		private boolean needToNotifyBiz(String repaymentMethod, Integer loanTerm, String loanType, String loanUnit){
			LoanRebateInfoEntity loanRebateInfoEntity = loanRebateInfoRepository.findByRepaymentMethodAndLoanTermAndProductTypeAndLoanUnit(repaymentMethod, loanTerm, loanType, loanUnit);
			if(loanRebateInfoEntity != null && Constant.LOAN_FLAG_01.equals(loanRebateInfoEntity.getFlag())) {
				return true;
			}
			return false;
		}
		
		/**
		 * 审核转让
		 * @author  guoyk
		 * @date   	2017年3月13日 
		 * @throws 	SLException
		 */
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public ResultVo auditTransfer(Map<String, Object> params) throws SLException {
			String transferApplyId = params.get("transferApplyId").toString();
			String auditStatus = params.get("auditStatus").toString();
			String userId = params.get("userId").toString();

			// 当前时间
			Date now = new Date();
			
			//申请记录表
			LoanTransferApplyEntity loanTransferApply = loanTransferApplyRepository.findOne(transferApplyId);
			if(loanTransferApply == null){
				return new ResultVo(false, "查询债券转让申请失败！");
			}
			
			if(Constant.TRANSFER_APPLY_STATUS_PASS.equals(auditStatus)){
				if(params.get("transferRate") == null) {
					return new ResultVo(false, "转让费率不能为空！");
				}
				BigDecimal transferRate = new BigDecimal(params.get("transferRate").toString());
				if(transferRate.compareTo(new BigDecimal("0.01")) < 0 || transferRate.compareTo(new BigDecimal("100")) > 0){
					return new ResultVo(false, "转让费率填写错误，范围：0.01 - 100！");
				}
				// 是否转让
				Integer transferSeatTerm = Integer.parseInt(params.get("transferSeatTerm").toString());
				WealthHoldInfoEntity wealthHoldInfoEntity = wealthHoldInfoRepository.findOne(loanTransferApply.getSenderHoldId());
				if(wealthHoldInfoEntity == null) {
					return new ResultVo(false, "审核失败！用户未持有该笔债权");
				}
				if(wealthHoldInfoEntity.getHoldScale().compareTo(BigDecimal.ZERO) == 0 
						|| !Constant.HOLD_STATUS_01.equals(wealthHoldInfoEntity.getHoldStatus())) {
					return new ResultVo(false, "审核失败！用户不再持有该笔债权");
				}
				LoanInfoEntity loanInfoEntity = loanInfoRepository.findOne(wealthHoldInfoEntity.getLoanId());
				if(loanInfoEntity == null){
					return new ResultVo(false, "审核失败！查询借款信息失败！");
				}
			
				Integer fromEndDay = paramService.findTransferFromEndDay();
				// 4) 判断距离到期日是否大于30天
				if(DateUtils.datePhaseDiffer(DateUtils.truncateDate(now), loanInfoEntity.getInvestEndDate()) <= fromEndDay) {
					return new ResultVo(false, String.format("审核失败！剩余期限小于%d天的债权不允许转让， 请选择拒绝", fromEndDay.intValue()));
				}
				
				// 5) 判断持有债权天数是否大于30天
				InvestInfoEntity investInfoEntity = investInfoRepository.findOne(wealthHoldInfoEntity.getInvestId());
				if(investInfoEntity == null) {
					return new ResultVo(false, "审核失败！投资记录不存在");
				}
				Integer needHoldDay = paramService.findTransferNeedHoldDay();
				if(DateUtils.datePhaseDiffer(DateUtils.parseDate(investInfoEntity.getEffectDate(), "yyyyMMdd"), DateUtils.truncateDate(now)) < needHoldDay) {
					return new ResultVo(false, String.format("审核失败！持有债权天数小于%d天不允许转让， 请选择拒绝", needHoldDay.intValue()));
				}
				
//				BigDecimal reducedScale = loanTransferApply.getReducedScale();
				// update by http://192.16.150.101:8080/browse/SLCF-2823 at 2017-05-18 债权转让改造
//				// 6) 判断持有价值是否大于1000
//				// 债权价值
//				BigDecimal loanValue = creditRightValueRepository.findByLoanIdAndValueDate(wealthHoldInfoEntity.getLoanId(), DateUtils.formatDate(now, "yyyyMMdd"));
//				// 用户持有的价值		
//				BigDecimal holdValue = ArithUtil.mul(loanValue, wealthHoldInfoEntity.getHoldScale());
//				// 转让持有价值 = 持有价值×转让比例
//				BigDecimal transferHoldValue = ArithUtil.mul(holdValue, transferScale);
//				// 转让金额  = 转让持有价值  * 折价比例    不能小于1000 
//				if(ArithUtil.mul(transferHoldValue, reducedScale).compareTo(new BigDecimal("1000")) < 0) {
//					return new ResultVo(false, String.format("转让失败！转让金额不能低于1000，实际为%s， 请选择拒绝", ArithUtil.mul(transferHoldValue, reducedScale).setScale(2, BigDecimal.ROUND_DOWN).toPlainString()));
//				}
				
				// SLCF-3181 每期还款复投和复投后不可转让需求 转让金额没有限制 update 2017-07-11
//				BigDecimal transferScale = loanTransferApply.getTransferScale();
//				//  判断持有本金是否大于1000
//				BigDecimal loanPrincipal = loanInfoEntity.getLoanDetailInfoEntity().getCreditRemainderPrincipal();
//				// 用户持有的本金	
//				BigDecimal holdPrincipal = ArithUtil.mul(loanPrincipal, wealthHoldInfoEntity.getHoldScale());
//				// 转让持有本金 = 持有价值×转让比例
//				BigDecimal transferHoldPrincipal = ArithUtil.mul(holdPrincipal, transferScale);
//				// 转让金额  = 转让持有价值  不能小于1000 
//				if(transferHoldPrincipal.compareTo(new BigDecimal("1000")) < 0) {
//					return new ResultVo(false, String.format("转让失败！转让金额不能低于1000，实际为%s， 请选择拒绝", transferHoldPrincipal.setScale(2, BigDecimal.ROUND_DOWN).toPlainString()));
//				}

				// 计算到期日期
				Date transferEndDate = null;
				Date investEndDate = loanInfoEntity.getInvestEndDate();
				Integer transferDay = paramService.findTransferDay();
				if(DateUtils.datePhaseDiffer(DateUtils.getAfterDay(now, transferDay), investEndDate) <= fromEndDay) { // 若转让结束时间距离到期日期小于30天，则取到期日期-30天
					transferEndDate = DateUtils.getAfterDay(investEndDate, -fromEndDay);   // 该方法自动取整天
				} else {
					transferEndDate = DateUtils.getDateAfterByHour(now, transferDay * 24); // 该方法自动取整时
				}
//				params.put("transferDay", paramService.findTransferDay());
//				params.put("fromEndDay", paramService.findTransferFromEndDay());
//				params.put("transferProtocalType", paramService.findTransferProtocalType());
//				.append("      , case when loan.invest_end_date - (date + params.get("transferDay")) <= params.get("fromEndDay")  ") // 不能超过截止日期
//				.append("             then to_date(to_char((loan.invest_end_date - params.get("fromEndDay")), 'yyyy-MM-dd hh24')||':00:00', 'yyyy-MM-dd hh24:mi:ss') ")
//				.append("             else to_date(to_char((date + params.get("transferDay")), 'yyyy-MM-dd hh24')||':00:00', 'yyyy-MM-dd hh24:mi:ss') end \"tradeEndDate\" ")  
				
				loanTransferApply.setTransferStartDate(now);
				loanTransferApply.setTransferEndDate(transferEndDate);
				loanTransferApply.setTransferSeatTerm(transferSeatTerm);
				loanTransferApply.setTransferRate(ArithUtil.div(transferRate, new BigDecimal("100")));
				//审核转让通过的标的，修改状态为N，智能投顾结束后，再修改为Y
				loanTransferApply.setIsRunAutoInvest(Constant.IS_RUN_AUTO_INVEST_02);
			}
			loanTransferApply.setBasicModelProperty(userId, false);
			loanTransferApply.setAuditStatus(auditStatus);
			loanTransferApplyRepository.save(loanTransferApply);
			
			// 添加审核信息
			AuditInfoEntity auditInfoEntity = auditInfoRepository.findAuditInfoEntityByRelatePrimary(transferApplyId);
			if (auditInfoEntity == null) {
				throw new SLException("查询审核记录出错");
			}
			auditInfoEntity.setBasicModelProperty(userId, false);
			auditInfoEntity.setAuditStatus(auditStatus);
			auditInfoEntity.setAuditTime(now);
			auditInfoEntity.setAuditUser(userId);
			auditInfoEntity.setTradeStatus(Constant.TRADE_STATUS_03);
			
			// 记录日志
			LogInfoEntity logInfoEntity = new LogInfoEntity();
			logInfoEntity.setRelateType(Constant.TABLE_BAO_T_LOAN_TRANSFER_APPLY);
			logInfoEntity.setRelatePrimary(transferApplyId);
			logInfoEntity.setLogType(Constant.TRANSFER_APPLY_STATUS_UNREVIEW);
			logInfoEntity.setOperBeforeContent("");
			logInfoEntity.setOperAfterContent(auditStatus);
			logInfoEntity.setOperDesc("");
			logInfoEntity.setOperPerson(userId);
			if (!StringUtils.isEmpty(params.get("auditMemo"))) {
				String auditMemo = (String)params.get("auditMemo");
				logInfoEntity.setMemo(auditMemo);
			}
			logInfoEntity.setBasicModelProperty(userId, true);
			logInfoEntityRepository.save(logInfoEntity);
			
			//转让审核通过后，调用智能投顾
			Map<String, Object> data = Maps.newHashMap();
			boolean flag = false;
			if (Constant.TRANSFER_APPLY_STATUS_PASS.equals(auditStatus)) {
				 flag = true;
				 data.put("flag", flag);
			}else {
				 data.put("flag", flag);
			}
			return new ResultVo(true, "保存成功",data);
		}
		
		
		/**
		 * 批量审核转让
		 * @author  lixx
		 * @date   	2017年6月28日 
		 * @throws 	SLException
		 */
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public ResultVo batchAuditTransfer(Map<String, Object> params) throws SLException {
			@SuppressWarnings("unchecked")
			List<String> transferApplyIds = (ArrayList<String>)params.get("transferApplyIds");
			String auditStatus = params.get("auditStatus").toString();
			String userId = params.get("userId").toString();
			BigDecimal transferRate = new BigDecimal(params.get("transferRate").toString());

			// 当前时间
			Date now = new Date();
			
			for(String transferApplyId : transferApplyIds){
				//申请记录表
				LoanTransferApplyEntity loanTransferApply = loanTransferApplyRepository.findOne(transferApplyId);
				if(loanTransferApply == null){
					throw new SLException("查询债券转让申请失败！");
					//return new ResultVo(false, "查询债券转让申请失败！");
				}
				
				if(Constant.TRANSFER_APPLY_STATUS_PASS.equals(auditStatus)){
					if(transferRate.compareTo(new BigDecimal("0.01")) < 0 || transferRate.compareTo(new BigDecimal("100")) > 0){
						throw new SLException("转让费率填写错误，范围：0.01 - 100！");
						//return new ResultVo(false, "转让费率填写错误，范围：0.01 - 100！");
					}
					// 是否转让
					Integer transferSeatTerm = Integer.parseInt(params.get("transferSeatTerm").toString());
					WealthHoldInfoEntity wealthHoldInfoEntity = wealthHoldInfoRepository.findOne(loanTransferApply.getSenderHoldId());
					if(wealthHoldInfoEntity == null) {
						throw new SLException("审核失败！用户未持有该笔债权");
						//return new ResultVo(false, "审核失败！用户未持有该笔债权");
					}
					if(wealthHoldInfoEntity.getHoldScale().compareTo(BigDecimal.ZERO) == 0 
							|| !Constant.HOLD_STATUS_01.equals(wealthHoldInfoEntity.getHoldStatus())) {
						throw new SLException("审核失败！用户不再持有该笔债权");
						//return new ResultVo(false, "审核失败！用户不再持有该笔债权");
					}
					LoanInfoEntity loanInfoEntity = loanInfoRepository.findOne(wealthHoldInfoEntity.getLoanId());
					if(loanInfoEntity == null){
						throw new SLException("审核失败！查询借款信息失败！");
						//return new ResultVo(false, "审核失败！查询借款信息失败！");
					}
				
					Integer fromEndDay = paramService.findTransferFromEndDay();
					// 4) 判断距离到期日是否大于30天
					if(DateUtils.datePhaseDiffer(DateUtils.truncateDate(now), loanInfoEntity.getInvestEndDate()) <= fromEndDay) {
						throw new SLException("审核失败！剩余期限小于%d天的债权不允许转让， 请选择拒绝");
						//return new ResultVo(false, String.format("审核失败！剩余期限小于%d天的债权不允许转让， 请选择拒绝", fromEndDay.intValue()));
					}
					
					// 5) 判断持有债权天数是否大于30天
					InvestInfoEntity investInfoEntity = investInfoRepository.findOne(wealthHoldInfoEntity.getInvestId());
					if(investInfoEntity == null) {
						throw new SLException("审核失败！投资记录不存在");
						//return new ResultVo(false, "审核失败！投资记录不存在");
					}
					Integer needHoldDay = paramService.findTransferNeedHoldDay();
					if(DateUtils.datePhaseDiffer(DateUtils.parseDate(investInfoEntity.getEffectDate(), "yyyyMMdd"), DateUtils.truncateDate(now)) < needHoldDay) {
						throw new SLException("审核失败！持有债权天数小于%d天不允许转让， 请选择拒绝");
						//return new ResultVo(false, String.format("审核失败！持有债权天数小于%d天不允许转让， 请选择拒绝", needHoldDay.intValue()));
					}
					
					// SLCF-3181 每期还款复投和复投后不可转让需求 转让金额没有限制 update 2017-07-11
//					BigDecimal transferScale = loanTransferApply.getTransferScale();
//					//  判断持有本金是否大于1000
//					BigDecimal loanPrincipal = loanInfoEntity.getLoanDetailInfoEntity().getCreditRemainderPrincipal();
//					// 用户持有的本金	
//					BigDecimal holdPrincipal = ArithUtil.mul(loanPrincipal, wealthHoldInfoEntity.getHoldScale());
//					// 转让持有本金 = 持有价值×转让比例
//					BigDecimal transferHoldPrincipal = ArithUtil.mul(holdPrincipal, transferScale);
//					// 转让金额  = 转让持有价值  不能小于1000 
//					if(transferHoldPrincipal.compareTo(new BigDecimal("1000")) < 0) {
//						throw new SLException("转让失败！转让金额不能低于1000，实际为"+transferHoldPrincipal.setScale(2, BigDecimal.ROUND_DOWN).toPlainString()+"， 请选择拒绝");
//						//return new ResultVo(false, String.format("转让失败！转让金额不能低于1000，实际为%s， 请选择拒绝", transferHoldPrincipal.setScale(2, BigDecimal.ROUND_DOWN).toPlainString()));
//					}

					// 计算到期日期
					Date transferEndDate = null;
					Date investEndDate = loanInfoEntity.getInvestEndDate();
					Integer transferDay = paramService.findTransferDay();
					if(DateUtils.datePhaseDiffer(DateUtils.getAfterDay(now, transferDay), investEndDate) <= fromEndDay) { // 若转让结束时间距离到期日期小于30天，则取到期日期-30天
						transferEndDate = DateUtils.getAfterDay(investEndDate, -fromEndDay);   // 该方法自动取整天
					} else {
						transferEndDate = DateUtils.getDateAfterByHour(now, transferDay * 24); // 该方法自动取整时
					}
					
					loanTransferApply.setTransferStartDate(now);
					loanTransferApply.setTransferEndDate(transferEndDate);
					loanTransferApply.setTransferSeatTerm(transferSeatTerm);
					loanTransferApply.setTransferRate(ArithUtil.div(transferRate, new BigDecimal("100")));
					//审核转让通过的标的，修改状态为N，智能投顾结束后，再修改为Y
					loanTransferApply.setIsRunAutoInvest(Constant.IS_RUN_AUTO_INVEST_02);
				}
				loanTransferApply.setBasicModelProperty(userId, false);
				loanTransferApply.setAuditStatus(auditStatus);
				loanTransferApplyRepository.save(loanTransferApply);
				
				// 添加审核信息
				AuditInfoEntity auditInfoEntity = auditInfoRepository.findAuditInfoEntityByRelatePrimary(transferApplyId);
				if (auditInfoEntity == null) {
					throw new SLException("查询审核记录出错");
				}
				auditInfoEntity.setBasicModelProperty(userId, false);
				auditInfoEntity.setAuditStatus(auditStatus);
				auditInfoEntity.setAuditTime(now);
				auditInfoEntity.setAuditUser(userId);
				auditInfoEntity.setTradeStatus(Constant.TRADE_STATUS_03);
				
				// 记录日志
				LogInfoEntity logInfoEntity = new LogInfoEntity();
				logInfoEntity.setRelateType(Constant.TABLE_BAO_T_LOAN_TRANSFER_APPLY);
				logInfoEntity.setRelatePrimary(transferApplyId);
				logInfoEntity.setLogType(Constant.TRANSFER_APPLY_STATUS_UNREVIEW);
				logInfoEntity.setOperBeforeContent("");
				logInfoEntity.setOperAfterContent(auditStatus);
				logInfoEntity.setOperDesc("");
				logInfoEntity.setOperPerson(userId);
				if (!StringUtils.isEmpty(params.get("auditMemo"))) {
					String auditMemo = (String)params.get("auditMemo");
					logInfoEntity.setMemo(auditMemo);
				}
				logInfoEntity.setBasicModelProperty(userId, true);
				logInfoEntityRepository.save(logInfoEntity);
			}
			
			//转让审核通过后，调用智能投顾
			Map<String, Object> data = Maps.newHashMap();
			boolean flag = false;
			if (Constant.TRANSFER_APPLY_STATUS_PASS.equals(auditStatus)) {
				 flag = true;
				 data.put("flag", flag);
			}else {
				 data.put("flag", flag);
			}
			return new ResultVo(true, "保存成功",data);
		}
	
		/**
		 * 保存发布设置
		 * @author  guoyk
		 * @date    2017-06-23
		 * @param params
		 *      <tt>loanId:		<List>String</tt><br>
		 * @return ResultVo
		 * @throws SLException
		 */
		@Transactional(readOnly = false, rollbackFor=SLException.class)
		public ResultVo savePublishInstall(Map<String, Object> params)throws SLException {
			String userId = (String)params.get("userId");
			String loanId = (String)params.get("loanId");
			LoanInfoEntity loanInfoEntity = loanInfoRepository.findOne(loanId);
			if (loanInfoEntity == null) {
				return new ResultVo(false,"该借款信息不存在!");
			}
			//借款标题
			loanInfoEntity.setLoanTitle(CommonUtils.emptyToString(params.get("loanTitle")));
			//如果传来的新手标标识不为空
			if (!StringUtils.isEmpty(params.get("isNewerFlag")) && "Y".equals(params.get("isNewerFlag").toString())) {
				loanInfoEntity.setNewerFlag(Constant.LOAN_INFO_NEWER_FLAG);
				//是否贴息
				if (!StringUtils.isEmpty(params.get("awardRate"))) {
					loanInfoEntity.setAwardRate(ArithUtil.div(new BigDecimal(params.get("awardRate").toString()), new BigDecimal("100")));
				}
			}
			//是否支持自动投标
			if (!StringUtils.isEmpty(params.get("isAllowAutoInvest"))) {
				if ("Y".equals(params.get("isAllowAutoInvest").toString())) {
					loanInfoEntity.setIsAllowAutoInvest("是");
				}
				if ("N".equals(params.get("isAllowAutoInvest").toString())) {
					loanInfoEntity.setIsAllowAutoInvest("否");
				}
			}
			// 2017-7-21 update by mali 是否特殊用户
            if (!CommonUtils.isEmpty(params.get("specialUsersFlag"))) {
			    loanInfoEntity.setSpecialUsersFlag(params.get("specialUsersFlag").toString());
            }

			LogInfoEntity logInfoEntity = new LogInfoEntity();
			logInfoEntity.setBasicModelProperty(userId, true);
			logInfoEntity.setRelateType(Constant.TABLE_BAO_T_LOAN_INFO);
			logInfoEntity.setRelatePrimary(loanId);
			logInfoEntity.setLogType(Constant.TABLE_BAO_T_LOAN_INFO);
			logInfoEntity.setOperBeforeContent(String.format("借款标题=%s，是否是新手标=%s，发布类型=%s，是否支持自动投标=%s，是否特殊用户=%s","","","","",""));
			logInfoEntity.setOperAfterContent(String.format(
						"借款标题=%s，是否是新手标=%s，发布类型=%s，是否支持自动投标=%s，是否特殊用户=%s",
						CommonUtils.emptyToString(params.get("loanTitle")),
						CommonUtils.emptyToString(params.get("isNewerFlag")),
						CommonUtils.emptyToString(params.get("isPublishNow")),
						CommonUtils.emptyToString(params.get("isAllowAutoInvest")),
                        CommonUtils.emptyToString(params.get("specialUsersFlag"))
					));
			logInfoEntity.setOperDesc("");
			logInfoEntity.setOperPerson(userId);
			logInfoEntityRepository.save(logInfoEntity);
			
//			//是否立即发布
//			HashMap<String, Object> map = Maps.newHashMap();
//			map.put("result", false);
//			if (!StringUtils.isEmpty(params.get("isPublishNow")) && "Y".equals(params.get("isPublishNow").toString())) {
//				//如果有奖励利息，必须该方法保存成功过后，再调用发布接口
//				map.put("result", true);
//			}
			return  new ResultVo(true, "保存成功");
		}
	}

	/**
	 * 启用/禁用自动投标
	 * 
	 * @author fengyl
	 * @date 2017年3月8日
	 * @param params
	 *            Map < String, Object >
	 * @return ResultVo
	 * @throws SLException
	 */
	public ResultVo enableAutoInvest(Map<String, Object> params)
			throws SLException {
		ResultVo resultVo =	innerClass.enableAutoInvest(params);
//		String openStatus=params.get("openStatus").toString();
//		if(Constant.AUTO_INVEST_INFO_OPEN_STATUS_01.equals(openStatus)){
//			dispatchAutoInvest();
//		}
		return resultVo;
	}
	
	public ResultVo setInvestPoint(Map<String, Object> params)throws SLException{
		//pointType，可以不传，不传默认为登录时设置 by guoyk 2017/5/18
		String pointType = params.get("pointType")==null?Constant.ZNTG_SET_POINT_TYPE_01:params.get("pointType").toString();
		String custId = params.get("custId").toString();
		AutoInvestInfoEntity autoInvestInfo = autoInvestInfoRepository.findByCustId(custId);
		
		if (Constant.ZNTG_SET_POINT_TYPE_01.equals(pointType)) {
			String pointStatus = params.get("pointStatus").toString();
			String beforeContent = autoInvestInfo.getPointStatus();
			autoInvestInfo.setPointStatus(pointStatus);
			String meMo = Constant.ZNTG_SET_POINT_TYPE_01;
			//记录登录页面 设置提醒日志信息
			LogInfoEntity logInfoEntity = new LogInfoEntity();
			logInfoEntity.setBasicModelProperty(custId, true);
			logInfoEntity.setRelateType(Constant.TABLE_BAO_T_AUTO_INVEST_INFO);
			logInfoEntity.setRelatePrimary(autoInvestInfo.getId());
			logInfoEntity.setLogType(Constant.JOB_NAME_AUTO_INVEST);
			logInfoEntity.setOperBeforeContent(autoInvestInfo==null?"":beforeContent);
			logInfoEntity.setOperAfterContent(pointStatus);
			logInfoEntity.setOperDesc("");
			logInfoEntity.setMemo(meMo);
			logInfoEntity.setOperPerson(custId);
			logInfoEntityRepository.save(logInfoEntity);
		}
		if (Constant.ZNTG_SET_POINT_TYPE_02.equals(pointType)) {
			String pointStatus = params.get("pointStatus").toString();
			String beforeContent = autoInvestInfo.getRechargePointStatus();
			autoInvestInfo.setRechargePointStatus(pointStatus);
			String meMo = Constant.ZNTG_SET_POINT_TYPE_02;
			//记录充值页面 设置提醒日志信息
			LogInfoEntity logInfoEntity = new LogInfoEntity();
			logInfoEntity.setBasicModelProperty(custId, true);
			logInfoEntity.setRelateType(Constant.TABLE_BAO_T_AUTO_INVEST_INFO);
			logInfoEntity.setRelatePrimary(autoInvestInfo.getId());
			logInfoEntity.setLogType(Constant.JOB_NAME_AUTO_INVEST);
			logInfoEntity.setOperBeforeContent(autoInvestInfo==null?"":beforeContent);
			logInfoEntity.setOperAfterContent(pointStatus);
			logInfoEntity.setOperDesc("");
			logInfoEntity.setMemo(meMo);
			logInfoEntity.setOperPerson(custId);
			logInfoEntityRepository.save(logInfoEntity);
		}
		return new ResultVo(true, "设置成功");
	}

	/**
	 * 自动投标调用
	 */
	private void dispatchAutoInvest() {
		executor.execute(new Runnable() {
			public void run() {
				try {
					autoInvestJob.invoke();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * 自动发布调用
	 */
	private void dispatchAutoPublish() {
		executor.execute(new Runnable() {
			public void run() {
				try {
					Map<String, Object> params = new HashMap<String, Object>();
					autoPublishJobService.autoPublish(params);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * 查询 自动转让
	 */
	@Override
	public ResultVo queryAutoTransferList(Map<String, Object> params)
			throws SLException {
		params.put("needHoldDay", paramService.findTransferNeedHoldDay());
		params.put("fromEndDay", paramService.findTransferFromEndDay());

		Page<Map<String, Object>> autoTransferList = loanManagerRepositoryCustom.queryAutoTransferList(params);
		BigDecimal autoTransferTotalAmount = loanManagerRepositoryCustom.queryAutoTransferTotalAmount(params);

		Map<String, Object> result = Maps.newHashMap();
		result.put("totalAmount",autoTransferTotalAmount != null ? autoTransferTotalAmount: BigDecimal.ZERO);
		result.put("data", autoTransferList.getContent());
		result.put("iTotalDisplayRecords", autoTransferList.getTotalElements());
		return new ResultVo(true, "自动转让列表查询成功", result);
	}

	@Override
	public ResultVo queryAutoTransferInfo(Map<String, Object> params)
			throws SLException {
		String custId = params.get("custId").toString();
		AutoTransferInfoEntity autoTransferInfo = autoTransferInfoRepository.findByCustId(custId);
		Map<String, Object> result = Maps.newHashMap();
		if (autoTransferInfo == null) {
			autoTransferInfo = new AutoTransferInfoEntity();
			autoTransferInfo.setOpenStatus("未设置");
		}
		result.put("isAuto", autoTransferInfo.getOpenStatus());  
		result.put("termMax", autoTransferInfo.getLimitedTerm()==null?1:autoTransferInfo.getLimitedTerm());
		result.put("minTerm", autoTransferInfo.getMinTerm()==null?0:autoTransferInfo.getMinTerm());
		result.put("maxTerm", autoTransferInfo.getMaxTerm()==null?24:autoTransferInfo.getMaxTerm());
		result.put("minYearRate", ArithUtil.mul(autoTransferInfo.getMinYearRate(),new BigDecimal("100")));
		result.put("maxYearRate", autoTransferInfo.getMaxYearRate()==null?new BigDecimal("15"):ArithUtil.mul(autoTransferInfo.getMaxYearRate(),new BigDecimal("100")));
		result.put("repaymentMethod", StringUtils.isEmpty(autoTransferInfo.getRepaymentMethod())?"等额本息,到期还本付息,每期还息到期付本":autoTransferInfo.getRepaymentMethod());
		result.put("canTransferProduct",  StringUtils.isEmpty(autoTransferInfo.getCanTransferProduct())?"普通标的,转让标的":autoTransferInfo.getCanTransferProduct());
		return new ResultVo(true, "自动转让信息查询成功", result);
	}

	/**
	 * 保存自动转让设置
	 * 
	 * @author fengyl
	 * @date 2017年3月13日
	 * @param params
	 *            Map < String, Object >
	 * @return ResultVo
	 * @throws SLException
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo saveAutoTransfer(Map<String, Object> params)
			throws SLException {
		String custId = params.get("custId").toString();
		Integer termMax = Integer.parseInt(params.get("termMax").toString());
		
		if (termMax < 1) {
			return new ResultVo(false, "自动转让最低期限为一个月");
		}
		if ((!StringUtils.isEmpty(params.get("minYearRate")) && Integer.parseInt((String) params.get("minYearRate")) < 0)
				|| (!StringUtils.isEmpty(params.get("maxYearRate")) && Integer.parseInt((String) params.get("maxYearRate")) < 0)) {
			return new ResultVo(false, "请重新设置转让标的利率");
		}
		if((!StringUtils.isEmpty(params.get("minTerm")) && Integer.parseInt(params.get("minTerm").toString())<0)
				||(!StringUtils.isEmpty(params.get("maxTerm"))&&Integer.parseInt(params.get("maxTerm").toString())<0)){
			return new ResultVo(false, "请重新设置转让标的期限");
		}
		if(!StringUtils.isEmpty(params.get("maxYearRate")) && Integer.parseInt((String)params.get("maxYearRate"))>15){
			return new ResultVo(false, "转让标的利率最高不能超过15%");
		}
		if(!StringUtils.isEmpty(params.get("maxTerm")) && Integer.parseInt(params.get("maxTerm").toString())>99){
			return new ResultVo(false, "转让标的期限最高不能超过99月");
		}

		AutoTransferInfoEntity autoTransferInfo = autoTransferInfoRepository.findByCustId(custId);
		if (autoTransferInfo == null) {
			autoTransferInfo = new AutoTransferInfoEntity();
			autoTransferInfo.setBasicModelProperty(custId, true);
			autoTransferInfo.setCustId(custId);
			autoTransferInfo.setCustPriority("1");
		} else {
			autoTransferInfo.setBasicModelProperty(custId, false);
		}
		String beforeTerm = autoTransferInfo.getLimitedTerm()==null?"":autoTransferInfo.getLimitedTerm().toString();
		String beforeMinYearRate = autoTransferInfo.getMinYearRate()==null?"":autoTransferInfo.getMinYearRate().toString();
		String beforeMaxYearRate = autoTransferInfo.getMaxYearRate()==null?"":autoTransferInfo.getMaxYearRate().toString();
		String beforeMinTerm = autoTransferInfo.getMinTerm()==null?"":autoTransferInfo.getMinTerm().toString();
		String beforeMaxTerm = autoTransferInfo.getMaxTerm()==null?"":autoTransferInfo.getMaxTerm().toString();
		String beforeRepaymentMethod = autoTransferInfo.getRepaymentMethod()==null?"":autoTransferInfo.getRepaymentMethod().toString();
		String beforeCanTransferProduct= autoTransferInfo.getCanTransferProduct()==null?"":autoTransferInfo.getCanTransferProduct().toString();

		if (!Constant.AUTO_INVEST_INFO_OPEN_STATUS_01.equals(autoTransferInfo.getOpenStatus())) {
			autoTransferInfo.setOpenStatus(Constant.AUTO_INVEST_INFO_OPEN_STATUS_01);
			autoTransferInfo.setOpenDate(new Date());
		}
		Integer minTerm = params.get("minTerm")==null?Integer.valueOf("0"):Integer.parseInt(params.get("minTerm").toString());
		Integer maxTerm = params.get("maxTerm")==null?Integer.valueOf("99"):Integer.parseInt(params.get("maxTerm").toString());
		BigDecimal minYearRate = params.get("minYearRate")==null?new BigDecimal("0"):ArithUtil.div(new BigDecimal(params.get("minYearRate").toString()),new BigDecimal("100"));
		BigDecimal maxYearRate = params.get("maxYearRate")==null?new BigDecimal("0.15"):ArithUtil.div(new BigDecimal(params.get("maxYearRate").toString()),new BigDecimal("100"));

		autoTransferInfo.setLimitedTerm(termMax);
		autoTransferInfo.setMinTerm(minTerm);
		autoTransferInfo.setMaxTerm(maxTerm);
		autoTransferInfo.setMinYearRate(minYearRate);
		autoTransferInfo.setMaxYearRate(maxYearRate);
		autoTransferInfo.setRepaymentMethod((String)params.get("repaymentMethod"));
		autoTransferInfo.setCanTransferProduct((String)params.get("canTransferProduct"));
		autoTransferInfo = autoTransferInfoRepository.save(autoTransferInfo);
		
		LogInfoEntity logInfoEntity = new LogInfoEntity();
		logInfoEntity.setBasicModelProperty(custId, true);
		logInfoEntity.setRelateType(Constant.TABLE_BAO_T_AUTO_TRANSFER_INFO);
		logInfoEntity.setRelatePrimary(autoTransferInfo.getId());
		logInfoEntity.setLogType(Constant.JOB_NAME_AUTO_TRANSFER);
		logInfoEntity.setOperBeforeContent(String.format(
				"转让期限=%s，最小利率=%s，最大利率=%s，最小期限=%s，最大期限=%s，还款方式=%s，项目类型=%s",
				beforeTerm,beforeMinYearRate,beforeMaxYearRate,beforeMinTerm,beforeMaxTerm,beforeRepaymentMethod,beforeCanTransferProduct));
		logInfoEntity.setOperAfterContent(String.format(
				"转让期限=%s，最小利率=%s，最大利率=%s，最小期限=%s，最大期限=%s，还款方式=%s，项目类型=%s",
				autoTransferInfo.getLimitedTerm().toString(), autoTransferInfo
						.getMinYearRate().toString(), autoTransferInfo
						.getMaxYearRate().toString(), autoTransferInfo
						.getMinTerm().toString(), autoTransferInfo.getMaxTerm()
						.toString(), autoTransferInfo.getRepaymentMethod(),
				autoTransferInfo.getCanTransferProduct()));
		logInfoEntity.setOperDesc("");
		logInfoEntity.setOperPerson(custId);
		logInfoEntityRepository.save(logInfoEntity);
		
		// 记录设备信息(自动转让)
		Map<String, Object> deviceParams = Maps.newConcurrentMap();
		deviceParams.putAll(params);
		deviceParams.put("relateType", Constant.TABLE_BAO_T_AUTO_TRANSFER_INFO);
		deviceParams.put("relatePrimary", autoTransferInfo.getId());
		deviceParams.put("tradeType", Constant.JOB_NAME_AUTO_TRANSFER);
		deviceParams.put("userId", custId);
		deviceService.saveUserDevice(deviceParams);
		return new ResultVo(true, "保存自动转让设置成功");
	}

	/**
	 * 启用/禁用自动转让
	 * 
	 * @author fengyl
	 * @date 2017年3月13日
	 * @param params
	 *            Map < String, Object >
	 * @return ResultVo
	 * @throws SLException
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo enableAutoTransfer(Map<String, Object> params)
			throws SLException {
		String openStatus = params.get("openStatus").toString();
		String custId = params.get("custId").toString();
		if (!Constant.AUTO_INVEST_INFO_OPEN_STATUS_01.equals(openStatus)&& !Constant.AUTO_INVEST_INFO_OPEN_STATUS_02.equals(openStatus)) {
			return new ResultVo(false, "自动转让状态只能是启用或者禁用");
		}
		AutoTransferInfoEntity autoTransferInfo = autoTransferInfoRepository.findByCustId(custId);
		String beforeContent = autoTransferInfo.getOpenStatus();
		autoTransferInfo.setOpenStatus(openStatus);
		autoTransferInfo.setBasicModelProperty(custId, false);
		autoTransferInfo.setOpenDate(new Date());
		autoTransferInfo = autoTransferInfoRepository.save(autoTransferInfo);

		LogInfoEntity logInfoEntity = new LogInfoEntity();
		logInfoEntity.setBasicModelProperty(custId, true);
		logInfoEntity.setRelateType(Constant.TABLE_BAO_T_AUTO_TRANSFER_INFO);
		logInfoEntity.setRelatePrimary(autoTransferInfo.getId());
		logInfoEntity.setLogType(Constant.JOB_NAME_AUTO_TRANSFER);
		logInfoEntity.setOperBeforeContent(beforeContent);
		logInfoEntity.setOperAfterContent(openStatus);
		logInfoEntity.setOperDesc("");
		logInfoEntity.setOperPerson(custId);
		logInfoEntityRepository.save(logInfoEntity);

		return new ResultVo(true, "设置成功");
	}

	@Override
    public List<Map<String, Object>> queryMyCreditListForJob(Map<String, Object> params)
			throws SLException {
		params.put("needHoldDay", paramService.findTransferNeedHoldDay());
		params.put("fromEndDay", paramService.findTransferFromEndDay());
		return loanInfoRepositoryCustom.queryMyCreditListForJob(params);

	}

	/**
	 * 查询转让审核列表
	 * @author  guoyk
	 * @date   	2017年3月13日 
	 * @throws 	SLException
	 */
	public ResultVo queryAuditTransferList(Map<String, Object> params)throws SLException {
		String onlineTime = paramService.findTransferRefromOnlineTimeByParameterName(Constant.TRANSFERRE_FROM_ONLINE_TIME_20170531);
		params.put("onlineTime", onlineTime);
		
		Page<Map<String, Object>> pageVo = loanManagerRepositoryCustom.queryAuditTransferList(params);
		Map<String, Object> result = Maps.newHashMap();
		result.put("iTotalDisplayRecords", pageVo.getTotalElements());
		result.put("data", pageVo.getContent());
		return new ResultVo(true, "转让审核列表查询成功", result);
	}
	
//	/**
//	 * 批量审核转让
//	 */
//	@Override
//	public ResultVo auditTransferForBatch(Map<String, Object> params) throws SLException {
//		@SuppressWarnings("unchecked")
//		List<String> transferApplyIds = (List<String>) params.get("transferApplyIds");
//		params.remove("transferApplyIds");
//		
//		if(transferApplyIds == null || transferApplyIds.size() ==0) {
//			return new ResultVo(false, "尺寸为零");
//		}
//
//		int count = 0; // 成功计数
//		StringBuilder failMessage = new StringBuilder();
//		for (String transferApplyId : transferApplyIds) {
//			params.put("transferApplyId", transferApplyId);
//			try{
//				ResultVo resultVo = self.auditTransfer(params);
//				if (ResultVo.isSuccess(resultVo)) {
//					count++;
//				} else {
//					failMessage.append(resultVo.getValue("message").toString()).append("，");
//				}
//			}catch(Exception e){
//				failMessage.append("后台处理异常，");
//			}	
//		}
//		if(count > 0 && Constant.TRANSFER_APPLY_STATUS_PASS.equals(params.get("auditStatus"))){
//			// 当转让审核通过的时候调用智能投顾
//			dispatchAutoInvest();
//		}
//		return new ResultVo(true, StringUtils.isEmpty(failMessage.toString())?"批量审核成功":failMessage.append("请刷新后再重试！"));
//	}
	
	/**
	 * 审核转让
	 * @author  guoyk
	 * @date   	2017年3月13日 
	 * @throws 	SLException
	 */
	public ResultVo auditTransfer(Map<String, Object> params) throws SLException {
		ResultVo resultVo = innerClass.auditTransfer(params);
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> flagMap =(Map<String, Object>)resultVo.getValue("data");
			if(ResultVo.isSuccess(resultVo) && (boolean)flagMap.get("flag")){
				//当转让审核通过的时候调用智能投顾
				dispatchAutoInvest();
			}
		} catch (Exception e) {
			log.warn("调用智能投顾异常");
		}
		return resultVo;
	}
	
	
	/**
	 * 批量审核转让
	 * @author  lixx
	 * @date   	2017年6月28日 
	 * @throws 	SLException
	 */
	public ResultVo batchAuditTransfer(Map<String, Object> params) throws SLException {
		ResultVo resultVo = innerClass.batchAuditTransfer(params);
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> flagMap =(Map<String, Object>)resultVo.getValue("data");
			if(ResultVo.isSuccess(resultVo) && (boolean)flagMap.get("flag")){
				//当转让审核通过的时候调用智能投顾
				dispatchAutoInvest();
			}
		} catch (Exception e) {
			log.warn("调用智能投顾异常");
		}
		return resultVo;
	}
	

	@Override
	public ResultVo queryNewerFlagList(Map<String, Object> params) {
		Page<Map<String, Object>> pageVo = loanManagerRepositoryCustom.queryNewerFlagList(params);
		
		Map<String, Object> result = Maps.newHashMap();
		result.put("iTotalDisplayRecords", pageVo.getTotalElements());
		result.put("data", pageVo.getContent());
		return new ResultVo(true, "新手标列表查询成功", result);
	}
//
//	@Override
//	public ResultVo dockingWdzj(Map<String, Object> params) {
//		return new ResultVo(true, "查询成功", loanManagerRepositoryCustom.queryLoaninfoAndInvestlistMap(params));
//	}

	/**
	 * 保存自动发标规则
	 * @author  guoyk
	 * @date   	2017年4月11日 
	 * @throws 	SLException
	 */
	@Override
	@Transactional(readOnly = false, rollbackFor=SLException.class)
	public ResultVo saveAutoPublish(Map<String, Object> params) throws SLException{
		//从传来的参数中获取
		String userId = params.get("userId").toString();
		String id = params.get("id")==null?"": params.get("id").toString();
		String debtSource = params.get("debtSource").toString();
		if ("".equals(debtSource)) {
			debtSource = Constant.DEBT_SOURCE_STRING;
		}
		Integer minTerm = params.get("minTerm")==""?0: Integer.valueOf(params.get("minTerm").toString());
		Integer maxTerm = params.get("maxTerm")==""?0: Integer.valueOf(params.get("maxTerm").toString());
		BigDecimal minYearRate = params.get("minYearRate")==""?BigDecimal.ZERO: ArithUtil.div(new BigDecimal(params.get("minYearRate").toString()),new BigDecimal("100"));
		BigDecimal maxYearRate = params.get("maxYearRate")==""?BigDecimal.ZERO:ArithUtil.div( new BigDecimal(params.get("maxYearRate").toString()),new BigDecimal("100"));
		Integer minRasieDays = params.get("minRasieDays")==""?0: Integer.valueOf(params.get("minRasieDays").toString());
		Integer maxRasieDays = params.get("maxRasieDays")==""?0: Integer.valueOf(params.get("maxRasieDays").toString());
		String repaymentMethod = params.get("repaymentMethod").toString();
		if ("".equals(repaymentMethod)) {
			repaymentMethod = Constant.REPAYMENT_METHOD;
		}
		BigDecimal minLoanAmount = params.get("minLoanAmount")==""?BigDecimal.ZERO: new BigDecimal(params.get("minLoanAmount").toString());
		BigDecimal maxLoanAmount = params.get("maxLoanAmount")==""?BigDecimal.ZERO: new BigDecimal(params.get("maxLoanAmount").toString());
		Integer maxLoanNumber = params.get("maxLoanNumber")==""?0: Integer.valueOf(params.get("maxLoanNumber").toString());
		BigDecimal minTotalLoanAmount = params.get("minTotalLoanAmount")==""?BigDecimal.ZERO: new BigDecimal(params.get("minTotalLoanAmount").toString());
		BigDecimal maxTotalLoanAmount = params.get("maxTotalLoanAmount")==""?BigDecimal.ZERO: new BigDecimal(params.get("maxTotalLoanAmount").toString());
		
		
		AutoPublishInfoEntity autoPublishInfo = autoPublishInfoRepository.findOne(id);
		List<AutoPublishInfoEntity> autoPublishInfoList = autoPublishInfoRepository.findAllAutoPublishInfo();
		if(autoPublishInfo==null || autoPublishInfoList.size()==0){
			//说明是新增规则
			autoPublishInfo = new AutoPublishInfoEntity();
			autoPublishInfo = new AutoPublishInfoEntity();
			autoPublishInfo.setBasicModelProperty(userId,  true);
		}else{//修改
			autoPublishInfo.setBasicModelProperty(userId,  false);
		}
		 
		//先获取之前的数据，为记录日志
		String beforeDebtSource = autoPublishInfo.getDebtSource()==null?"": autoPublishInfo.getDebtSource();
		String beforeMinTerm = autoPublishInfo.getMinTerm()==null?"": autoPublishInfo.getMinTerm().toString();
		String beforeMaxTerm = autoPublishInfo.getMaxTerm()==null?"": autoPublishInfo.getMaxTerm().toString();
		String beforeMinYearRate = autoPublishInfo.getMinYearRate()==null?"": autoPublishInfo.getMinYearRate().setScale(2,BigDecimal.ROUND_HALF_UP).toPlainString();
		String beforeMaxYearRate = autoPublishInfo.getMaxYearRate()==null?"":autoPublishInfo.getMaxYearRate().setScale(2,BigDecimal.ROUND_HALF_UP).toPlainString();
		String beforeMinRasieDays = autoPublishInfo.getMinRasieDays()==null?"": autoPublishInfo.getMinRasieDays().toString();
		String beforeMaxRasieDays = autoPublishInfo.getMaxRasieDays()==null?"": autoPublishInfo.getMaxRasieDays().toString();
		String beforeRepaymentMethod = autoPublishInfo.getRepaymentMethod()==null?"": autoPublishInfo.getRepaymentMethod();
		String beforeMinLoanAmount = autoPublishInfo.getMinLoanAmount()==null?"": autoPublishInfo.getMinLoanAmount().toString();
		String beforeMaxLoanAmount = autoPublishInfo.getMaxLoanAmount()==null?"": autoPublishInfo.getMaxLoanAmount().toString();
		String beforeMaxLoanNumber = autoPublishInfo.getMaxLoanNumber()==null?"": autoPublishInfo.getMaxLoanNumber().toString();
		String beforeMinTotalLoanAmount = autoPublishInfo.getMinTotalLoanAmount()==null?"": autoPublishInfo.getMinTotalLoanAmount().toString();
		String beforeMaxTotalLoanAmount = autoPublishInfo.getMaxTotalLoanAmount()==null?"": autoPublishInfo.getMaxTotalLoanAmount().toString();
		
		autoPublishInfo.setDebtSource(debtSource);
		autoPublishInfo.setMinTerm(minTerm);
		autoPublishInfo.setMaxTerm(maxTerm);
		autoPublishInfo.setMinYearRate(minYearRate);
		autoPublishInfo.setMaxYearRate(maxYearRate);
		autoPublishInfo.setMinRasieDays(minRasieDays);
		autoPublishInfo.setMaxRasieDays(maxRasieDays);
		autoPublishInfo.setRepaymentMethod(repaymentMethod);
		autoPublishInfo.setMinLoanAmount(minLoanAmount);
		autoPublishInfo.setMaxLoanAmount(maxLoanAmount);
		autoPublishInfo.setMaxLoanNumber(maxLoanNumber);
		autoPublishInfo.setMinTotalLoanAmount(minTotalLoanAmount);
		autoPublishInfo.setMaxTotalLoanAmount(maxTotalLoanAmount);
		
		AutoPublishInfoEntity autopie = autoPublishInfoRepository.save(autoPublishInfo);
		Map<String, Object> data = null;
		//保存成功返回一个id
		if(StringUtils.isEmpty(params.get("id"))){
			data = Maps.newHashMap();
			data.put("id", autopie.getId());
		}
		//记录日志
		LogInfoEntity logInfoEntity = new LogInfoEntity();
		logInfoEntity.setBasicModelProperty(userId, true);
		logInfoEntity.setRelateType(Constant.TABLE_BAO_T_AUTO_PUBLISH_INFO);
		logInfoEntity.setRelatePrimary(autoPublishInfo.getId());
		logInfoEntity.setLogType(Constant.JOB_NAME_AUTO_PUBLISH);
		logInfoEntity.setOperBeforeContent(String.format(
				"资产来源 =%s，最低期限=%s，最高期限=%s，最低利率=%s，最高利率=%s，最低募集天数=%s，最高募集天数=%s，还款方式=%s，最低借款金额=%s，最高借款金额=%s，标的个数=%s，最低线上金额=%s，最高线上金额=%s",
				beforeDebtSource,beforeMinTerm,beforeMaxTerm,beforeMinYearRate,beforeMaxYearRate,beforeMinRasieDays,beforeMaxRasieDays,beforeRepaymentMethod,
				beforeMinLoanAmount,beforeMaxLoanAmount,beforeMaxLoanNumber,beforeMinTotalLoanAmount,beforeMaxTotalLoanAmount));
		logInfoEntity.setOperAfterContent(String.format(
				"资产来源 =%s，最低期限=%s，最高期限=%s，最低利率=%s，最高利率=%s，最低募集天数=%s，最高募集天数=%s，还款方式=%s，最低借款金额=%s，最高借款金额=%s，标的个数=%s，最低线上金额=%s，最高线上金额=%s", 
				autoPublishInfo.getDebtSource()==null?"": autoPublishInfo.getDebtSource(),
				autoPublishInfo.getMinTerm()==null?"": autoPublishInfo.getMinTerm().toString(),
				autoPublishInfo.getMaxTerm()==null?"": autoPublishInfo.getMaxTerm().toString(),
				autoPublishInfo.getMinYearRate()==null?"": autoPublishInfo.getMinYearRate().setScale(2,BigDecimal.ROUND_HALF_UP).toPlainString(),
				autoPublishInfo.getMaxYearRate()==null?"":autoPublishInfo.getMaxYearRate().setScale(2,BigDecimal.ROUND_HALF_UP).toPlainString(),
				autoPublishInfo.getMinRasieDays()==null?"": autoPublishInfo.getMinRasieDays().toString(),
				autoPublishInfo.getMaxRasieDays()==null?"": autoPublishInfo.getMaxRasieDays().toString(),
				autoPublishInfo.getRepaymentMethod()==null?"": autoPublishInfo.getRepaymentMethod(),
				autoPublishInfo.getMinLoanAmount()==null?"": autoPublishInfo.getMinLoanAmount().toString(),
				autoPublishInfo.getMaxLoanAmount()==null?"": autoPublishInfo.getMaxLoanAmount().toString(),
				autoPublishInfo.getMaxLoanNumber()==null?"": autoPublishInfo.getMaxLoanNumber().toString(),
				autoPublishInfo.getMinTotalLoanAmount()==null?"": autoPublishInfo.getMinTotalLoanAmount().toString(),
				autoPublishInfo.getMaxTotalLoanAmount()==null?"": autoPublishInfo.getMaxTotalLoanAmount().toString()));
		logInfoEntity.setOperDesc("");
		logInfoEntity.setOperPerson(userId);
		logInfoEntityRepository.save(logInfoEntity);
		
		return new ResultVo(true,"保存成功",data);
		
	}
	
	/**
	 * 自动发标查询（发标池）
	 * @author  guoyk
	 * @date   	2017年4月11日
	 */
	@Override
	public ResultVo queryAutoPublish(Map<String, Object> params) {
		String id = params.get("id").toString();
		AutoPublishInfoEntity autoPublishInfo = autoPublishInfoRepository.findOne(id);
		if(autoPublishInfo==null){
			return new ResultVo(false,"发标规则不存在");
		}
		BigDecimal minYearRate = ArithUtil.formatScale(ArithUtil.mul(autoPublishInfo.getMinYearRate(), new BigDecimal("100")), 0);
		BigDecimal maxYearRate = ArithUtil.formatScale(ArithUtil.mul(autoPublishInfo.getMaxYearRate(), new BigDecimal("100")), 0);
		Map<String, Object> data =  Maps.newHashMap();
		data.put("id", id);
		data.put("debtSource", autoPublishInfo.getDebtSource());
		if (autoPublishInfo.getMinTerm()==0&&autoPublishInfo.getMaxTerm()!=0) {
			data.put("minTerm", autoPublishInfo.getMinTerm());
		}else {
			data.put("minTerm", autoPublishInfo.getMinTerm()==0?"":autoPublishInfo.getMinTerm());
		}
		data.put("maxTerm", autoPublishInfo.getMaxTerm()==0?"":autoPublishInfo.getMaxTerm());
		if (minYearRate==BigDecimal.ZERO&&maxYearRate!=BigDecimal.ZERO) {
			data.put("minYearRate", minYearRate);
		}else {
			data.put("minYearRate", minYearRate==BigDecimal.ZERO?"":minYearRate);
		}
		data.put("maxYearRate", maxYearRate==BigDecimal.ZERO?"":maxYearRate);
		if (autoPublishInfo.getMinRasieDays()==0&&autoPublishInfo.getMaxRasieDays()!=0) {
			data.put("minRasieDays", autoPublishInfo.getMinRasieDays());
		}else {
			data.put("minRasieDays", autoPublishInfo.getMinRasieDays()==0?"":autoPublishInfo.getMinRasieDays());
		}
		data.put("maxRasieDays", autoPublishInfo.getMaxRasieDays()==0?"":autoPublishInfo.getMaxRasieDays());
		data.put("repaymentMethod", autoPublishInfo.getRepaymentMethod());
		if (autoPublishInfo.getMinLoanAmount()==BigDecimal.ZERO&&autoPublishInfo.getMaxLoanAmount()!=BigDecimal.ZERO) {
			data.put("minLoanAmount", autoPublishInfo.getMinLoanAmount());
		}else {
			data.put("minLoanAmount", autoPublishInfo.getMinLoanAmount()==BigDecimal.ZERO?"":autoPublishInfo.getMinLoanAmount());
		}
		data.put("maxLoanAmount", autoPublishInfo.getMaxLoanAmount()==BigDecimal.ZERO?"":autoPublishInfo.getMaxLoanAmount());
		data.put("maxLoanNumber", autoPublishInfo.getMaxLoanNumber()==0?"":autoPublishInfo.getMaxLoanNumber());
		if (autoPublishInfo.getMinTotalLoanAmount()==BigDecimal.ZERO&&autoPublishInfo.getMaxTotalLoanAmount()!=BigDecimal.ZERO) {
			data.put("minTotalLoanAmount", autoPublishInfo.getMinTotalLoanAmount());
		}else {
			data.put("minTotalLoanAmount", autoPublishInfo.getMinTotalLoanAmount()==BigDecimal.ZERO?"":autoPublishInfo.getMinTotalLoanAmount());
		}
		data.put("maxTotalLoanAmount", autoPublishInfo.getMaxTotalLoanAmount()==BigDecimal.ZERO?"":autoPublishInfo.getMaxTotalLoanAmount());
		Map<String, Object> result = Maps.newHashMap();
		result.put("data", data);
		return new ResultVo(true,"查询发标规则成功",result);
	}

	/**
	 *  附件编辑标识接口
	 *  @author  guoyk
	 *  @date    2017年4月10日
	 *  @throws  SLException
	 */
	@Override
	@Transactional(readOnly = false, rollbackFor=SLException.class)
	public ResultVo editAttachmentFlag(Map<String, Object> params)throws SLException {
		String loanId = params.get("loanId").toString();
		String attachmentFlag = params.get("attachmentFlag").toString();
		//脱敏状态保存
		LoanInfoEntity  loanInfoEntity  = loanInfoRepository.findOne(loanId);
		if(loanInfoEntity==null){
			throw new SLException("借款信息不存在，脱敏编辑失败");
		}
		loanInfoEntity.setAttachmentFlag(attachmentFlag);
		loanInfoEntity.setBasicModelProperty((String)params.get("userId"), false);
		loanInfoRepository.save(loanInfoEntity);
		return new ResultVo(true,"修改附件编辑标识成功");
	}


	/**
	 * 取消自动发标
	 * @author  guoyk
	 * @date    2017-4-12 
	 * @param params
     *      <tt>id:String:自动发布主键</tt><br>
     * @return ResultVo
	 * @throws SLException
	 */
	@Transactional(readOnly = false, rollbackFor=SLException.class)
	public ResultVo cancleAutoPublish(Map<String, Object> params) throws SLException{
		if(StringUtils.isEmpty(params.get("id"))){
			throw new SLException("取消自动发标出错");
		}
		String id = params.get("id").toString();
		autoPublishInfoRepository.delete(id);
		return new ResultVo(true,"取消自动发标成功");
	}
	
	/**
	 * 列表查询自动发标（发标池）
	 * @author  guoyk
	 * @date   	2017年4月12日
	 */
	public ResultVo queryAutoPublishList() {
		List<AutoPublishInfoEntity> autoPublishInfoList = autoPublishInfoRepository.findAllAutoPublishInfo();
		AutoPublishInfoEntity autoPublishInfo =null;
		String debtSourceCh = null;
		Map<String, Object> result = null;
		//第一次是没有数据的
		if(autoPublishInfoList.size()==0){
			Map<String, Object> data = Maps.newHashMap();
			result = Maps.newHashMap();
			data.put("data", autoPublishInfoList.size());
			data.put("iTotalDisplayRecords", 0);
			result.put("data", data);
			return new ResultVo(false,"暂时没有发标规则",result);
		}else{
			//取第一个展示
			autoPublishInfo =  autoPublishInfoList.get(0);
			StringBuffer sb = new StringBuffer();
			String debtSource = autoPublishInfo.getDebtSource();
			String[] dearr = debtSource.split(",");
//			//总资产来源
//			String[] debtSourceArr= Constant.DEBT_SOURCE;
//			//如果是all，应当另作处理
////			if("all".equals(debtSource)){
////				dearr=debtSourceArr;
////			}
			for (int i = 0; i < dearr.length; i++) {
				if(dearr[i].equals(Constant.DEBT_SOURCE_SLSW_CODE)){
					sb.append(Constant.DEBT_SOURCE_SLSW).append(",");
				}
				if(dearr[i].equals(Constant.DEBT_SOURCE_XC_CODE)){
					sb.append(Constant.DEBT_SOURCE_XC).append(",");
				}
				if(dearr[i].equals(Constant.DEBT_SOURCE_ZJD_CODE)){
					sb.append(Constant.DEBT_SOURCE_ZJD).append(",");
				}
				if(dearr[i].equals(Constant.DEBT_SOURCE_NM_CODE)){
					sb.append(Constant.DEBT_SOURCE_NM).append(",");
				}
				if(dearr[i].equals(Constant.DEBT_SOURCE_JL_CODE)){
					sb.append(Constant.DEBT_SOURCE_JL).append(",");
				}
			}
			//还款方式
//			String repaymentMethod = autoPublishInfo.getRepaymentMethod();
//			if("all".equals(repaymentMethod)){
//				autoPublishInfo.setRepaymentMethod(Constant.REPAYMENT_METHOD);
//			}
			BigDecimal minYearRate = ArithUtil.formatScale(ArithUtil.mul(autoPublishInfo.getMinYearRate(), new BigDecimal("100")), 0);
			BigDecimal maxYearRate = ArithUtil.formatScale(ArithUtil.mul(autoPublishInfo.getMaxYearRate(), new BigDecimal("100")), 0);
			debtSourceCh = sb.substring(0,sb.length()-1);
			Map<String, Object> data =  Maps.newHashMap();
			data.put("id", autoPublishInfo.getId());
			data.put("debtSource", autoPublishInfo.getDebtSource());
			data.put("debtSourceCh", debtSourceCh);
			if (autoPublishInfo.getMinTerm()==0&&autoPublishInfo.getMaxTerm()!=0) {
				data.put("minTerm", autoPublishInfo.getMinTerm());
			}else {
				data.put("minTerm", autoPublishInfo.getMinTerm()==0?"":autoPublishInfo.getMinTerm());
			}
			data.put("maxTerm", autoPublishInfo.getMaxTerm()==0?"":autoPublishInfo.getMaxTerm());
			if (minYearRate==BigDecimal.ZERO&&maxYearRate!=BigDecimal.ZERO) {
				data.put("minYearRate", minYearRate);
			}else {
				data.put("minYearRate", ArithUtil.mul(autoPublishInfo.getMinYearRate(), new BigDecimal("100"))==BigDecimal.ZERO?"":ArithUtil.mul(autoPublishInfo.getMinYearRate(), new BigDecimal("100")));
			}
			data.put("maxYearRate", ArithUtil.mul(autoPublishInfo.getMaxYearRate(), new BigDecimal("100"))==BigDecimal.ZERO?"":ArithUtil.mul(autoPublishInfo.getMaxYearRate(), new BigDecimal("100")));
			if (autoPublishInfo.getMinRasieDays()==0&&autoPublishInfo.getMaxRasieDays()!=0) {
				data.put("minRasieDays", autoPublishInfo.getMinRasieDays());
			}else {
				data.put("minRasieDays", autoPublishInfo.getMinRasieDays()==0?"":autoPublishInfo.getMinRasieDays());
			}
			data.put("maxRasieDays", autoPublishInfo.getMaxRasieDays()==0?"":autoPublishInfo.getMaxRasieDays());
			data.put("repaymentMethod", autoPublishInfo.getRepaymentMethod());
			if (autoPublishInfo.getMinLoanAmount()==BigDecimal.ZERO&&autoPublishInfo.getMaxLoanAmount()!=BigDecimal.ZERO) {
				data.put("minLoanAmount", autoPublishInfo.getMinLoanAmount());
			}else {
				data.put("minLoanAmount", autoPublishInfo.getMinLoanAmount()==BigDecimal.ZERO?"":autoPublishInfo.getMinLoanAmount());
			}
			data.put("maxLoanAmount", autoPublishInfo.getMaxLoanAmount()==BigDecimal.ZERO?"":autoPublishInfo.getMaxLoanAmount());
			data.put("maxLoanNumber", autoPublishInfo.getMaxLoanNumber()==0?"":autoPublishInfo.getMaxLoanNumber());
			if (autoPublishInfo.getMinTotalLoanAmount()==BigDecimal.ZERO&&autoPublishInfo.getMaxTotalLoanAmount()!=BigDecimal.ZERO) {
				data.put("minTotalLoanAmount", autoPublishInfo.getMinTotalLoanAmount());
			}else {
				data.put("minTotalLoanAmount", autoPublishInfo.getMinTotalLoanAmount()==BigDecimal.ZERO?"":autoPublishInfo.getMinTotalLoanAmount());
			}
			data.put("maxTotalLoanAmount", autoPublishInfo.getMaxTotalLoanAmount()==BigDecimal.ZERO?"":autoPublishInfo.getMaxTotalLoanAmount());
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			list.add(data);
			result = Maps.newHashMap();
			result.put("iTotalDisplayRecords", 1);
			result.put("data", list);
		}
		return new ResultVo(true,"查询发标规则成功",result);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResultVo batchModifyTransferSeatTerm(Map<String, Object> params)
			throws SLException {
		final List<String> loanIds = (List<String>) params.get("loanIds");
		final String seatTerm = (String) params.get("seatTerm");
		final String userId = (String) params.get("custId");
		if (null == loanIds || loanIds.size() == 0) {
			return new ResultVo(false, "请至少选择一条数据记录！");
		}
		
		boolean flag=loanInfoRepositoryCustom.batchModifyTransferSeatTerm(loanIds,seatTerm,userId);
        if(!flag){
        	return new ResultVo(false,"设置失败，请刷新后重试。");
        }        
		 return new ResultVo(true,"批量修改转让设置成功");
	}

	/**
	 * 充值是否提示查询
	 * 
	 * @author fengyl
	 * @date 2017年5月19日
	 * @param params
	 *            <tt>custId:String:客户ID</tt><br>
	 *            <tt>rechargeAmount:String:充值金额</tt><br>
	 * @return Map<String, Object> 
	 *            <tt>flag :String:是否提示</tt><br>
	 *            <tt>amount  :String:(flag为true时)返回提示金额</tt><br>
	 * 
	 * @throws SLException
	 */
	@Override
	public ResultVo queryIsShowAutoInvestRechargeTipDialog(
			Map<String, Object> params) throws SLException {
		Map<String, Object> result = Maps.newHashMap();
		String custId = params.get("custId").toString();
		String rechargeAmount = (String) params.get("rechargeAmount");//充值金额
		
		String openStatus=null;
		String rechargePointStatus=null;
		BigDecimal keepAvailableAmount =BigDecimal.ZERO;// 客户保留余额
		
		AutoInvestInfoEntity custEntity = autoInvestInfoRepository.findByCustId(custId);
		//custEntity 为 null的时候，说明该用户从来没开启过智能投顾（禁用，N） by guoyk 2017/06/01
		if (custEntity == null) {
			openStatus = "禁用";
			rechargePointStatus = "N";
		} else {
			openStatus = custEntity.getOpenStatus();
			rechargePointStatus = custEntity.getRechargePointStatus() == null ? "Y": custEntity.getRechargePointStatus();
			keepAvailableAmount=custEntity.getKeepAvailableAmount();
		}

		AccountInfoEntity accountInfoEntity = accountInfoRepository.findByCustId(custId);
		if (accountInfoEntity == null) {
			return new ResultVo(false, "客户的账户查询出错");
		}
		// 客户账户可用金额
		BigDecimal availableAmount = accountInfoEntity.getAccountAvailableAmount();
		BigDecimal amount = ArithUtil.sub(ArithUtil.add(availableAmount,new BigDecimal(rechargeAmount)),keepAvailableAmount);
		
		//当openStatus为启用 并且 rechargePointStatus 为Y时才提醒
		if (!Constant.AUTO_INVEST_INFO_OPEN_STATUS_02.equals(openStatus) && !"N".equals(rechargePointStatus)){
			//(充值金额+账户可用余额  )- 保留余额 <=0 的话 不提示 by guoyk 2017/06/01
			if (amount.compareTo(BigDecimal.ZERO)<=0) {
				result.put("flag", "false");
			}else {
				result.put("amount", amount);
				result.put("flag", "true");
			}
		}else{
			result.put("flag", "false");
		}
		return new ResultVo(true,"充值提示返回成功",result);
	}
	
	/**
	 * 领取列表查询
	 * @author  zhangyb
	 * @date 2017-06-12
	 */
	public ResultVo queryReceiveList(Map<String, Object> param) {
		
		Page<Map<String, Object>> pageVo = loanManagerRepositoryCustom.queryReceiveList(param);
		
		param.put("receiveStatus", Constant.RECEIVE_STATUS_01);
		param.put("userId", param.get("userId"));
		Map<String, Object> statisticsMap = loanManagerRepositoryCustom.queryLoanInfoReceives(param);
		
		Map<String, Object> result = Maps.newHashMap();
		result.put("iTotalDisplayRecords", pageVo.getTotalElements());
		result.put("data", pageVo.getContent());
		result.put("loanInfoCount", statisticsMap.get("loanInfoCount"));
		result.put("loanInfoAmountCount", statisticsMap.get("loanInfoAmountCount")!=null?statisticsMap.get("loanInfoAmountCount"):BigDecimal.ZERO);
		return new ResultVo(true, "领取信息列表查询成功", result);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public ResultVo batchModifyReceiveStaus(Map<String, Object> params)
			throws SLException {
		final List<String> loanCodes = (List<String>) params.get("loanCodes");
		final boolean receiveFlag = (boolean) params.get("receiveFlag");
		final String userId = (String) params.get("userId");
		if (null == loanCodes || loanCodes.size() == 0) {
			return new ResultVo(false, "请至少选择一条数据记录！");
		}
		
		boolean flag=loanInfoRepositoryCustom.batchModifyReceiveStaus(loanCodes,receiveFlag,userId);
        //领取失败
		if(!flag){
        	if(receiveFlag){
        		return new ResultVo(false,"领取失败，请刷新后重试。");
        	}else{
        		return new ResultVo(false,"取消领取失败，请刷新后重试。");
        	}
        }
        //领取成功
        if(receiveFlag){
        	return new ResultVo(true,"领取成功");
        }else{
        	return new ResultVo(true,"取消领取成功");
        }
	}
	
	/**
	 * 转让申请置顶和取消置顶操作
	 * @author  lixx
	 * @date    2017-06-29
	 * @param params
     *      <tt>transferApplyId: String:转让申请ID</tt><br>
     *      <tt>stickyStatus:    String:设置状态(置顶或取消置顶)</tt><br>
     * @return ResultVo
	 * @throws SLException
	 */
	@Transactional
	@Override
	public ResultVo transferStickyOperation(Map<String, Object> params)
			throws SLException {
		
		String transferApplyId = params.get("transferApplyId").toString();
		String stickyStatus = params.get("stickyStatus").toString();
		
		LoanTransferApplyEntity loanTransferApply = loanTransferApplyRepository.findOne(transferApplyId);
		if(loanTransferApply == null){
			return new ResultVo(false, "查询债券转让申请失败！");
		}
		if(stickyStatus.equals("1")){//1代表置顶
			loanTransferApply.setStickyStatus("1");//1代表置顶
			String stickyLevel = loanTransferApplyRepository.findMaxStickyLevel("1");
			if(stickyLevel != null){
				loanTransferApply.setStickyLevel(String.valueOf(Integer.parseInt(stickyLevel)+1));
			}else{
				loanTransferApply.setStickyLevel("1");
			}
			loanTransferApplyRepository.save(loanTransferApply);
			return new ResultVo(true,"置顶成功");
		}else if(stickyStatus.equals("0")){//0代表取消置顶
			loanTransferApplyRepository.cancelStickyUpdateStickyLevel(loanTransferApply.getStickyLevel(),"1");
			loanTransferApply.setStickyStatus("");
			loanTransferApply.setStickyLevel("");
			loanTransferApplyRepository.save(loanTransferApply);
			return new ResultVo(true,"取消置顶成功");
		}
		
		return new ResultVo(false,"置顶状态错误");
	}
	
	/**
	 * 转让申请上移操作
	 * @author  lixx
	 * @date    2017-06-29
	 * @param params
     *      <tt>transferApplyId: String:转让申请ID</tt><br>
     * @return ResultVo
	 * @throws SLException
	 */
	@Transactional
	@Override
	public ResultVo transferMoveOperation(Map<String, Object> params)
			throws SLException {
		String transferApplyId = params.get("transferApplyId").toString();
		LoanTransferApplyEntity loanTransferApply = loanTransferApplyRepository.findOne(transferApplyId);
		if(loanTransferApply == null){
			return new ResultVo(false, "查询债券转让申请失败！");
		}
		String stickyLevel = loanTransferApply.getStickyLevel();
		if(stickyLevel != null && !stickyLevel.equals("1")){
			loanTransferApplyRepository.moveUpUpdateStickyLevel(stickyLevel, "1");
			loanTransferApply.setStickyLevel(String.valueOf(Integer.parseInt(stickyLevel)-1));
			loanTransferApplyRepository.save(loanTransferApply);
		}else{
			new ResultVo(false,"该条数据不能上移");
		}
		return new ResultVo(true,"上移成功");
	}

	@SuppressWarnings({ "unused", "unchecked" })
	@Override
	public ResultVo querySnowOrangeContract(Map<String, Object> params)throws SLException {
		String loanId = (String) params.get("loanId");
		String custId = (String) params.get("custId");
		LoanInfoEntity loanInfoEntity = loanInfoRepository.findOne(loanId);
		CustInfoEntity custInfoEntity = custInfoRepository.findOne(custId);
		if(loanInfoEntity == null) {
			return new ResultVo(false, "项目不存在");
		}
		Map<String, Object> map = bankCardService.queryBankCardInfoByLoanId(loanId);
		
		Map<String, Object> result = Maps.newHashMap();
		result.put("loanId", loanInfoEntity.getId());
		result.put("loanCode", loanInfoEntity.getLoanCode());
		result.put("loanCustName", loanInfoEntity.getLoanCustInfoEntity().getCustName());
		result.put("loanCredentialsCode", loanInfoEntity.getLoanCustInfoEntity().getCredentialsCode());
		result.put("currentAddress", CommonUtils.emptyToString(map.get("homeAddress")));
		result.put("loanDesc", loanInfoEntity.getLoanDesc());
		result.put("typeTerm", Constant.LOAN_UNIT_DAY.equals(loanInfoEntity.getLoanUnit())?loanInfoEntity.getLoanTerm()+"天":loanInfoEntity.getLoanTerm()+"个月");
		result.put("incomeType", loanInfoEntity.getRepaymentMethod());
		result.put("bankCustName", map.get("custName"));
		if (StringUtils.isEmpty(map.get("subBankName"))) {//如果支行信息为空就传主行信息
			result.put("subBankName", map.get("bankName"));
		}else {
			result.put("subBankName", map.get("subBankName"));
		}
		result.put("bankNo", map.get("cardNo"));
		result.put("protocolType", loanInfoEntity.getProtocalType());

		
		
		Map<String, Object> data = loanInfoRepositoryCustom.queryFingertipContract(params);
		// income  :债权收益取还款计划所有利息
		BigDecimal sum = repaymentPlanInfoRepository.sumRepaymentInterestAndRepaymentPrincipalByLoanId(loanId);

		// 投资人列表
		List<Map<String, Object>> investList = investInfoRepositoryCustom.queryInvestListByLoanId(loanId);
		// 资产信息列表
		List<Map<String, Object>> assetList = assetInfoRepository.findMapAssetInfoByloanId(loanId);
		
		// 投资人列表
		result.put("investorList", investList);
		// 资产信息列表
		result.put("assetList", assetList);
		if (params.get("isDownload") != null&& (Boolean) params.get("isDownload")) {
			// 电签数据
			Map<String, Object> sign = Maps.newHashMap();
			List<Map<String, Object>> signData = Lists.newArrayList();
			Map<String, Object> comapany = Maps.newHashMap();
			comapany.put("signType", "平台");//平台公章
			comapany.put("keyword", "或电子信息");
			comapany.put("offsetX", "0.05");
			comapany.put("offsetY", "-0.15");
			signData.add(comapany);
			for (Map<String, Object> invest : investList) {//投资人
				Map<String, Object> investPerson = Maps.newHashMap();
				investPerson.put("signType", "个人");
				investPerson.put("name", invest.get("custName"));
				investPerson.put("idCard", invest.get("idCard"));
				investPerson.put("offsetX", "-0.005");
				investPerson.put("offsetY", "-0.02");
				investPerson.put("keyword", invest.get("custName").toString());
				signData.add(investPerson);
			}
			
			Map<String , Object> loanPerson = Maps.newHashMap();// 借款人
			loanPerson.put("signType", "个人");
			loanPerson.put("name", loanInfoEntity.getLoanCustInfoEntity().getCustName()); // 个人印章
			loanPerson.put("idCard", loanInfoEntity.getLoanCustInfoEntity().getCredentialsCode()); // 个人印章
			loanPerson.put("keyword", loanInfoEntity.getLoanCustInfoEntity().getCustName()); // 签署位置
			loanPerson.put("offsetX", "-0.01");// 签署位置偏移量
			loanPerson.put("offsetY", "-0.02");// 签署位置偏移量
			signData.add(loanPerson);
			
//			Map<String, Object> ourComapany = Maps.newHashMap();
//			ourComapany.put("signType", "附件一");
//			ourComapany.put("keyword", "附件一");
//			ourComapany.put("offsetX", "0.06");
//			ourComapany.put("offsetY", "-0.05");
//			signData.add(ourComapany);
			sign.put("platType", "契约锁");
			sign.put("signData", signData);
			result.put("sign", sign);
		}
		
		// 对格式做特殊处理
		result.put("grantDateYear", DateUtils.formatDate((Date)loanInfoEntity.getInvestStartDate(), "yyyy"));
		result.put("grantDateMonth", DateUtils.formatDate((Date)loanInfoEntity.getInvestStartDate(), "MM"));
		result.put("grantDateDay", DateUtils.formatDate((Date)loanInfoEntity.getInvestStartDate(), "dd"));

		result.put("sumAmount", ArithUtil.formatNumber(sum)+"元");
		result.put("sumAmountChinese", NumberToChinese.NumToRMBStr(Double.valueOf(CommonUtils.emptyToDecimal(sum).toString())));
		result.put("loanAmountChinese",  NumberToChinese.NumToRMBStr(Double.valueOf(CommonUtils.emptyToDecimal(loanInfoEntity.getLoanAmount()).toString())));
		result.put("loanAmount", ArithUtil.formatNumber(loanInfoEntity.getLoanAmount())+"元");
		result.put("yearRate", ArithUtil.formatPercent2((BigDecimal)loanInfoEntity.getLoanDetailInfoEntity().getYearIrr(), 1, 5));

		for(Map<String, Object> invest : (List<Map<String, Object>>)result.get("investorList")) {
			invest.put("investAmount", ArithUtil.formatNumber(invest.get("investAmount")));
			invest.put("investDate", DateUtils.formatDate(DateUtils.parseDate((String)invest.get("investDate"), "yyyyMMdd"), "yyyy年MM月dd日"));
//			invest.put("custName", (String)invest.get("custName"));
			invest.put("idCard", SharedUtil.replaceSpecialWord((String)invest.get("idCard")));
		}
		
		for(Map<String, Object> asset : (List<Map<String, Object>>)result.get("assetList")) {
			asset.put("loanAmount", ArithUtil.formatNumber(asset.get("loanAmount")));
			asset.put("investEndDate", DateUtils.formatDate((Date)asset.get("investEndDate"), "yyyy年MM月dd日"));
//			asset.put("custName", SharedUtil.replaceSpecialWord((String)asset.get("custName")));
		}
		
		return new ResultVo(true, "借款协议", result);
	}
	@Override
	public ResultVo getXCFileBrowseUrl(Map<String, Object> params)
			throws SLException {
		params.put("showType", Constant.SHOW_TYPE_INTERNAL);
		List<Map<String, Object>> attachList = attachmentRepositoryCustom.findAuditAttachmentInfoByLoanId(params);
		if(attachList.size()!=1){
			return new ResultVo(false, "借款项目附件不存在");
		}
		Map<String, Object> data = Maps.newHashMap();
		data.put("XCFileBrowseUrl",attachList.get(0).get("storagePath"));
		return new ResultVo(true, "借款项目附件浏览地址查询成功", data);
	}
	

}
