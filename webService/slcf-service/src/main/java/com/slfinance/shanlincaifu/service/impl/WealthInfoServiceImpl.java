package com.slfinance.shanlincaifu.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AccountFlowInfoEntity;
import com.slfinance.shanlincaifu.entity.AccountInfoEntity;
import com.slfinance.shanlincaifu.entity.AtoneInfoEntity;
import com.slfinance.shanlincaifu.entity.AuditInfoEntity;
import com.slfinance.shanlincaifu.entity.CustInfoEntity;
import com.slfinance.shanlincaifu.entity.CustRecommendInfoEntity;
import com.slfinance.shanlincaifu.entity.InvestDetailInfoEntity;
import com.slfinance.shanlincaifu.entity.InvestInfoEntity;
import com.slfinance.shanlincaifu.entity.LoanTransferEntity;
import com.slfinance.shanlincaifu.entity.LogInfoEntity;
import com.slfinance.shanlincaifu.entity.ProjectInvestInfoEntity;
import com.slfinance.shanlincaifu.entity.SubAccountInfoEntity;
import com.slfinance.shanlincaifu.entity.WealthHoldHistoryInfoEntity;
import com.slfinance.shanlincaifu.entity.WealthHoldInfoEntity;
import com.slfinance.shanlincaifu.entity.WealthInfoEntity;
import com.slfinance.shanlincaifu.entity.WealthPaymentPlanInfoEntity;
import com.slfinance.shanlincaifu.entity.WealthTypeInfoEntity;
import com.slfinance.shanlincaifu.repository.AccountInfoRepository;
import com.slfinance.shanlincaifu.repository.AtoneInfoRepository;
import com.slfinance.shanlincaifu.repository.AuditInfoRepository;
import com.slfinance.shanlincaifu.repository.CustInfoRepository;
import com.slfinance.shanlincaifu.repository.CustRecommendInfoRepository;
import com.slfinance.shanlincaifu.repository.DeviceInfoRepository;
import com.slfinance.shanlincaifu.repository.InvestDetailInfoRepository;
import com.slfinance.shanlincaifu.repository.InvestInfoRepository;
import com.slfinance.shanlincaifu.repository.LoanTransferRepository;
import com.slfinance.shanlincaifu.repository.LogInfoEntityRepository;
import com.slfinance.shanlincaifu.repository.ProjectInvestInfoRepository;
import com.slfinance.shanlincaifu.repository.RepaymentPlanInfoRepository;
import com.slfinance.shanlincaifu.repository.SubAccountInfoRepository;
import com.slfinance.shanlincaifu.repository.WealthHoldHistoryInfoRepository;
import com.slfinance.shanlincaifu.repository.WealthHoldInfoRepository;
import com.slfinance.shanlincaifu.repository.WealthInfoRepository;
import com.slfinance.shanlincaifu.repository.WealthPaymentPlanInfoRepository;
import com.slfinance.shanlincaifu.repository.WealthTypeInfoRepository;
import com.slfinance.shanlincaifu.repository.custom.LoanInfoRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.WealthHoldInfoRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.WealthInfoRepositoryCustom;
import com.slfinance.shanlincaifu.service.AccountFlowService;
import com.slfinance.shanlincaifu.service.DeviceService;
import com.slfinance.shanlincaifu.service.FlowNumberService;
import com.slfinance.shanlincaifu.service.ParamService;
import com.slfinance.shanlincaifu.service.ProjectService;
import com.slfinance.shanlincaifu.service.SMSService;
import com.slfinance.shanlincaifu.service.WealthInfoService;
import com.slfinance.shanlincaifu.service.WealthJobService;
import com.slfinance.shanlincaifu.utils.ArithUtil;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.SharedUtil;
import com.slfinance.shanlincaifu.utils.SubjectConstant;
import com.slfinance.util.DateUtilSL;
import com.slfinance.vo.ResultVo;

@Service("wealthInfoService")
public class WealthInfoServiceImpl implements WealthInfoService {
	
	@Autowired
	private WealthInfoRepositoryCustom wealthInfoRepositoryCustom;
	
	@Autowired
	private WealthInfoRepository wealthInfoRepository;
	
	@Autowired
	private WealthTypeInfoRepository wealthTypeInfoRepository;
	
	@Autowired
	private AuditInfoRepository auditInfoRepository;
	
	@Autowired
	private LogInfoEntityRepository logInfoEntityRepository; 
	
	@Autowired
	private ProjectInvestInfoRepository projectInvestInfoRepository;
	
	@Autowired
	private RepaymentPlanInfoRepository repaymentPlanInfoRepository; 
	
	@Autowired
	private InvestInfoRepository investInfoRepository;
	
	@Autowired
	private SubAccountInfoRepository subAccountInfoRepository;
	
	@Autowired
	private CustInfoRepository custInfoRepository;
	
	@Autowired
	private AccountInfoRepository accountInfoRepository;
	
	@Autowired
	private FlowNumberService numberService;
	
	@Autowired
	private InvestDetailInfoRepository investDetailInfoRepository;
	
	@Autowired
	private AccountFlowService accountFlowService;
	
	@Autowired
	private ParamService paramService;

	@Autowired
	private DeviceInfoRepository deviceInfoRepository;
	
	@Autowired
	private WealthJobService wealthJobService;
	
	@Autowired
	private WealthHoldInfoRepository wealthHoldInfoRepository;
	
	@Autowired
	private WealthHoldHistoryInfoRepository wealthHoldHistoryInfoRepository;
	
	@Autowired
	private LoanTransferRepository loanTransferRepository;
	
	@Autowired
	private AtoneInfoRepository atoneInfoRepository;
	
	@Autowired
	private SMSService smsService;
	
	@Autowired
	private ProjectService projectService;
	
	@Autowired
	WealthHoldInfoRepositoryCustom wealthHoldInfoRepositoryCustom;
	
	@Autowired
	LoanInfoRepositoryCustom loanInfoRepositoryCustom;
	
	@Autowired
	CustRecommendInfoRepository custRecommendInfoRepository;
	
	@Autowired
	private DeviceService deviceService;
	
	@Override
	public ResultVo queryWealthList(Map<String, Object> parmas)throws SLException {
		
		return wealthInfoRepositoryCustom.queryWealthList(parmas);
	}

	@Override
	public ResultVo queryWealthDetailById(Map<String, Object> params)throws SLException {
		
		return wealthInfoRepositoryCustom.queryWealthDetailById(params);
	}

	/**
	 * 新建/编辑项目
	 * 
	 * @author zhiwen_feng
	 * @date 2016-02-23
	 * @param params
	 * @return ResultVo
	 * @throws SLException
	 */
	@Override
	@Transactional(readOnly = false, rollbackFor=SLException.class)
	public ResultVo saveWealth(Map<String, Object> params) throws SLException {
		//1)check 验证
		BigDecimal investMinAmount = new BigDecimal((String) params.get("investMinAmount"));
		BigDecimal increaseAmount = new BigDecimal((String) params.get("increaseAmount"));
		Date releaseDate = DateUtils.parseDate((String) params.get("releaseDate"), "yyyy-MM-dd");
		BigDecimal awardRate = StringUtils.isEmpty((String) params.get("awardRate")) ? BigDecimal.ZERO : new BigDecimal((String) params.get("awardRate"));
		awardRate = ArithUtil.div(awardRate, new BigDecimal("100")); 
		
		if(DateUtils.datePhaseDiffer(DateUtils.truncateDate(new Date()), releaseDate) < 0){
			return new ResultVo(false, "发布日期不能小于当前日期！");
		}
		Date effectDate = DateUtils.parseDate((String) params.get("effectDate"), "yyyy-MM-dd");
		if(DateUtils.datePhaseDiffer(releaseDate, effectDate) < 0) {
			return new ResultVo(false, "生效日期不能小于发布日期！");
		}
//		if(!isDivInt(investMinAmount, increaseAmount)) {
//			return new ResultVo(false, "递增金额必须是起投金额的整数倍！");
//		}
		if(investMinAmount.remainder(new BigDecimal("10000")) != BigDecimal.ZERO) {
			return new ResultVo(false, "起投金额必须为10000的整数倍！");
		}
		if(awardRate.compareTo(new BigDecimal("0.5")) > 0) {
			return new ResultVo(false, "奖励利率不能大于50%");
		}
		
		//优选计划表插入或更新
		WealthTypeInfoEntity wealthTypeInfoEntiry = wealthTypeInfoRepository.findOne(params.get("wealthTypeId").toString());
		WealthInfoEntity wealthInfoEntity = null;
		if(!StringUtils.isEmpty(params.get("wealthId"))) { //编辑
			wealthInfoEntity = wealthInfoRepository.findOne(params.get("wealthId").toString());
			wealthInfoEntity.setBasicModelProperty((String) params.get("userId"), false);
		}else {//新建
			wealthInfoEntity = new WealthInfoEntity();
			wealthInfoEntity.setId(SharedUtil.getUniqueString());
			wealthInfoEntity.setBasicModelProperty((String) params.get("userId"), true);
		}
		wealthInfoEntity.setWealthTypeId((String) params.get("wealthTypeId"));
		wealthInfoEntity.setPlanTotalAmount(new BigDecimal((String) params.get("planTotalAmount")));
		wealthInfoEntity.setInvestMinAmount(investMinAmount);
		wealthInfoEntity.setIncreaseAmount(increaseAmount);
		wealthInfoEntity.setYearRate(wealthTypeInfoEntiry.getYearRate());
		wealthInfoEntity.setAwardRate(awardRate);
		wealthInfoEntity.setReleaseDate(releaseDate);
		wealthInfoEntity.setEffectDate(effectDate);
		wealthInfoEntity.setEndDate(DateUtils.getAfterDay(DateUtils.getAfterMonth(effectDate, wealthTypeInfoEntiry.getTypeTerm()), -1));
		wealthInfoEntity.setWealthStatus((String) params.get("wealthStatus"));
		wealthInfoEntity.setFlowStatus((String) params.get("wealthStatus"));
		wealthInfoEntity.setWealthDescr((String) params.get("wealthDescr"));
		wealthInfoEntity.setEnableStatus(Constant.ENABLE_STATUS_QY);
		wealthInfoEntity.setPenaltyRate(paramService.findPenaltyRate());
//		wealthInfoEntity.setWealthDescr("优选投资计划是善林财富为您提供的本息自动循环出借及到期自动转入退出的投资工具。具有更好的安全性、便捷性，投资项目真实透明。<br>优选投资计划系列产品不同于普通借款的投资方式，投资人无需关注借款人具体信息，由优选投资计划帮助用户从借款项目库中选取最优项目进行循环出借投资。当所投借款产生本金及利息还款时，优选投资计划将根据还款金额自动匹配新的借款项目进行投资，以保证计划内所有资金处于借出状态，确保达到计划预期年化收益率。");
		wealthInfoEntity.setWealthDescr("优选投资计划是善林财富为客户提供的可智能投标、到期自动退出的投资工具。不但具有更好的安全性、便捷性，更能提高资金流动率和利用率，使投资项目真实透明。优选投资计划系列产品不同于普通借款的投资方式，投资人既可以了解借款人具体信息，更可以通过优选投资计划从融资项目库中选取最优项目进行投资。当所投项目产生本金及利息还款时，又可通过优选投资计划将金额匹配新的融资项目进行投资，以保证投资者富余资金的利用率和流动性，以期让投资者获得更高的收益。");

		if(StringUtils.isEmpty(wealthInfoEntity.getLendingNo())) {
			wealthInfoEntity.setLendingNo(getLendingNoByReleaseDate(releaseDate, wealthTypeInfoEntiry, null));
		}
		wealthInfoEntity = wealthInfoRepository.save(wealthInfoEntity);
		
		//添加审核表信息
		AuditInfoEntity auditInfoEntity = auditInfoRepository.findAuditInfoEntityByRelatePrimaryAudits(wealthInfoEntity.getId());
		String operBeforeContent = wealthInfoEntity.getWealthStatus(); //之前状态
		if(null == auditInfoEntity) {
			auditInfoEntity = new AuditInfoEntity();
			auditInfoEntity.setId(SharedUtil.getUniqueString());
			auditInfoEntity.setApplyType(DateUtilSL.dateToStr(new Date()));
			auditInfoEntity.setBasicModelProperty((String) params.get("userId"), true);
		}
		auditInfoEntity.setCustId(null);
		auditInfoEntity.setRelateType(Constant.TABLE_BAO_T_WEALTH_INFO);
		auditInfoEntity.setRelatePrimary(wealthInfoEntity.getId());
		auditInfoEntity.setTradeAmount(wealthInfoEntity.getPlanTotalAmount());
		auditInfoEntity.setApplyTime(new Date());
		auditInfoEntity.setApplyType(Constant.OPERATION_TYPE_57);
		auditInfoEntity.setAuditStatus(Constant.WEALTH_STATUS_01);
		auditInfoEntity.setBasicModelProperty((String) params.get("userId"), false);
		auditInfoRepository.save(auditInfoEntity);
		
		//添加审核日志
		LogInfoEntity logInfoEntity = new LogInfoEntity(Constant.TABLE_BAO_T_WEALTH_INFO, wealthInfoEntity.getId(), Constant.OPERATION_TYPE_57, operBeforeContent, wealthInfoEntity.getWealthStatus(), "提交审核", (String) params.get("userId"));
		logInfoEntityRepository.save(logInfoEntity);
		return new ResultVo(true, "操作成功");
	}
	
	/**
	 * 
	 * 用户计划投资列表
	 * 
	 * @date 2016-02-23
	 * @author zhiwen_feng
	 * @param params
	 * @throws SLException
	 */
	@Override
	public ResultVo queryWealthJoinList(Map<String, Object> params)throws SLException {
		
		return wealthInfoRepositoryCustom.queryWealthJoinList(params);
	}

	/**
	 * 审核项目
	 * 
	 * @author zhiwen_feng
	 * @date 2016-02-23
	 * @param params
     *      <tt>wealthId   :String:计划主键</tt><br>
     *      <tt>auditStatus:String:审核状态</tt><br>
     *      <tt>auditMemo  :String: 审核备注</tt><br>
     *      <tt>userId     :String:创建人</tt><br>
	 * @return ResultVo
	 * @throws SLException
	 */
	@Transactional(readOnly = false, rollbackFor=SLException.class)
	@Override
	public ResultVo auditWealth(Map<String, Object> params) throws SLException {
		String wealthId = (String) params.get("wealthId"); 
		String userId = (String) params.get("userId");
		String auditMemo = (String) params.get("auditMemo");
		String auditStatus = (String) params.get("auditStatus"); //提交的审核状态
		WealthInfoEntity wealthInfoEntity = wealthInfoRepository.findOne(wealthId); //优选计划
		
		AuditInfoEntity autditInfoEntity = auditInfoRepository.findByRelatePrimaryAndAuditStatus(wealthInfoEntity.getId(), Constant.WEALTH_STATUS_01); //审核信息
		if(null == autditInfoEntity || null == wealthInfoEntity || !Constant.WEALTH_STATUS_01.equals(wealthInfoEntity.getWealthStatus())) {
			return new ResultVo(false, "该项目不是待审核状态！");
		}
		
		String operBeforeContent = wealthInfoEntity.getWealthStatus(); //之前状态
		wealthInfoEntity.setWealthStatus(auditStatus);
		if (Constant.WEALTH_AUDIT_STATUS_PASS.equals(auditStatus)) { //审核通过
			wealthInfoEntity.setWealthStatus(Constant.WEALTH_STATUS_04);
			if(DateUtils.datePhaseDiffer(DateUtils.truncateDate(new Date()), DateUtils.truncateDate(wealthInfoEntity.getReleaseDate())) < 0) {
				return new ResultVo(false, "发布日期日期不能小于当前日期！");
			}
		}
		wealthInfoEntity.setFlowStatus(wealthInfoEntity.getWealthStatus());
		wealthInfoEntity.setBasicModelProperty(userId, false);
		
		autditInfoEntity.setAuditStatus(auditStatus);
		autditInfoEntity.setBasicModelProperty(userId, false);
		autditInfoEntity.setMemo((String)params.get("auditMemo"));
		
		//添加审核日志
		LogInfoEntity logInfoEntity = new LogInfoEntity(Constant.TABLE_BAO_T_WEALTH_INFO, wealthInfoEntity.getId(), Constant.OPERATION_TYPE_57, operBeforeContent, auditStatus, auditMemo, userId);
		logInfoEntityRepository.save(logInfoEntity);
		return new ResultVo(true, "操作成功");
	}

	/**
	 * 发布项目
	 * 
	 * @date 2016-02-24
	 * @author zhiwen_feng
	 * @param params
     *      <tt>wealthId:String:计划主键</tt><br>
     *      <tt>userId  :String:创建人</tt><br>
	 * @return ResultVo
	 * @throws SLException
	 */
	@Transactional(readOnly = false, rollbackFor=SLException.class)
	@Override
	public ResultVo publishWealth(Map<String, Object> params)throws SLException {
		String userId = (String) params.get("userId");
		Date releaseDate = new Date(); //发布日期
		WealthInfoEntity wealthInfoEntity = wealthInfoRepository.findOne((String) params.get("wealthId")); //优选计划
		WealthTypeInfoEntity wealthTypeInfoEntity = wealthTypeInfoRepository.findOne(wealthInfoEntity.getWealthTypeId());
		
		if(!Constant.WEALTH_STATUS_04.equals(wealthInfoEntity.getWealthStatus())) {
			return new ResultVo(false, "该项目不是待发布状态！");
		}
		if(DateUtils.datePhaseDiffer(DateUtils.truncateDate(releaseDate), DateUtils.truncateDate(wealthInfoEntity.getEffectDate())) < 0) {
			return new ResultVo(false, "生效日期日期不能小于当前日期！");
		}
		
		wealthInfoEntity.setReleaseDate(releaseDate);
		wealthInfoEntity.setLendingNo(getLendingNoByReleaseDate(releaseDate, wealthTypeInfoEntity, wealthInfoEntity.getId())); 
		wealthInfoEntity.setWealthStatus(Constant.WEALTH_STATUS_05);
		wealthInfoEntity.setFlowStatus(Constant.WEALTH_STATUS_05);
		wealthInfoEntity.setEndDate(DateUtils.getAfterDay(DateUtils.getAfterMonth(wealthInfoEntity.getEffectDate(), wealthTypeInfoEntity.getTypeTerm()), -1)); //到期日期
		
		if(Constant.WEALTH_INCOME_TYPE_02.equals(wealthTypeInfoEntity.getIncomeType())) { // 月返息
			String firstRepayDay = getReturnBackInterestDay(releaseDate); //首个反息日
			wealthInfoEntity.setFirstRepayDay(firstRepayDay);
			wealthInfoEntity.setNextRepayDay(firstRepayDay);
		}else { //到期结算本息
			wealthInfoEntity.setFirstRepayDay(DateUtilSL.print(wealthInfoEntity.getEndDate(), "yyyyMMdd"));
			wealthInfoEntity.setNextRepayDay(DateUtilSL.print(wealthInfoEntity.getEndDate(), "yyyyMMdd"));
		}
		wealthInfoEntity.setPrevRepayDay(DateUtilSL.print(wealthInfoEntity.getEffectDate(), "yyyyMMdd"));
		wealthInfoEntity.setLastRepayDay(DateUtilSL.print(wealthInfoEntity.getEndDate(), "yyyyMMdd"));
		wealthInfoEntity.setBasicModelProperty(userId, false);
		
		//添加项目投资情况信息
		ProjectInvestInfoEntity projectInvestInfoEntity = new ProjectInvestInfoEntity();
		projectInvestInfoEntity.setId(SharedUtil.getUniqueString());
		projectInvestInfoEntity.setBasicModelProperty(userId, true);
		projectInvestInfoEntity.setAlreadyInvestAmount(BigDecimal.ZERO);
		projectInvestInfoEntity.setAlreadyInvestPeoples(0);
		projectInvestInfoEntity.setAlreadyInvestScale(BigDecimal.ZERO);
		projectInvestInfoEntity.setWealthId(wealthInfoEntity.getId());
		projectInvestInfoRepository.save(projectInvestInfoEntity);
		
		//审核信息表
//		AuditInfoEntity auditInfoEntity = auditInfoRepository.findAuditInfoEntityByRelatePrimary(wealthInfoEntity.getId());
//		auditInfoEntity.setBasicModelProperty(userId, false);
//		auditInfoEntity.setAuditStatus(wealthInfoEntity.getWealthStatus());
//		auditInfoEntity.setAuditTime(new Date());
		
		//添加审核日志
		LogInfoEntity logInfoEntity = new LogInfoEntity(Constant.TABLE_BAO_T_WEALTH_INFO, wealthInfoEntity.getId(), Constant.OPERATION_TYPE_58, Constant.WEALTH_STATUS_04, wealthInfoEntity.getWealthStatus(), "审核", userId);
		logInfoEntityRepository.save(logInfoEntity);
		return new ResultVo(true, "发布优选计划成功！");
	}
	
	

	@Override
	public ResultVo queryAllWealthList(Map<String, Object> params)throws SLException {
		//项目列表
		Map<String, Object> result = wealthInfoRepositoryCustom.queryAllWealthList(params);
		result.putAll(wealthInfoRepositoryCustom.queryAllWealthSummary(params));
		return new ResultVo(true, "查询成功", result);
	}

	/**
	 * 待匹配列表
	 */
	@Override
	public ResultVo queryWaitingMatchList(Map<String, Object> params)throws SLException {
		return wealthInfoRepositoryCustom.queryWaitingMatchList(params);
	}

	/**
	 * 债权预算
	 * 1待还款金额 当前日期到截止日期的还款计划还款金额总额
	 * 2：居间人持有价值（当前）
	 * 3：带匹配金额 = 计划发布中的 计划总额 + 计划状态满额 的分账的金额总和 且计划的结束日期 》=截止日期
	 * 4：预计退出金额 = 优选计划到期日《=截止日期 的退出价值 + 提前赎回的交易日《= 截止日期 的价值
	 */
	@Override
	public ResultVo queryMatchLoanList(Map<String, Object> params)throws SLException {
		Map<String, Object> result = Maps.newHashMap();
		String beginDate = DateUtils.formatDate(new Date(), "yyyyMMdd");
		String endDate = beginDate;
		if(!StringUtils.isEmpty(params.get("currentDate"))) { //截止日期为空的截止日期就是当天
			endDate = ((String) params.get("currentDate")).replace("-", "");
		}
		
		result.put("exceptRepaymentAmount", repaymentPlanInfoRepository.sumRepaymentTotalAmountByExpectRepaymentDate(beginDate, endDate)); //待还款
		result.put("exceptUsableValue", investInfoRepository.findAllHoldCreditValueByCustId(Constant.IS_CENTER_01, endDate)); //当前居间人持有价值
		
		result.put("exceptMatchAmount", subAccountInfoRepository.sumAllWealthAccountAmount()); //带匹配金额
		
		result.put("exceptAtoneAmount", ArithUtil.add(subAccountInfoRepository.sumAllBackValue(endDate), 
				subAccountInfoRepository.sumAllEarlyExitValue(endDate))); //预计退出金额
		
		return new ResultVo(true, "查询债权预算成功！", result);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResultVo autoReleaseWealth(Map<String, Object> params)throws SLException {
		ResultVo result = innerWealthService.effectWealth(params);
		//操作成功， 发送短信
		if(ResultVo.isSuccess(result)) { 
			List<Map<String, Object>> smsList = (List<Map<String, Object>>) result.getValue("data");
			for(Map<String, Object> sms : smsList) {
				smsService.asnySendSMSAndSystemMessage(sms);
			}
		}
		return result;
	}
	
	@Autowired
	InnerWealthService innerWealthService;
	@Service
	public static class InnerWealthService {
		
		@Autowired
		WealthInfoRepository wealthInfoRepository;
		
		@Autowired
		WealthTypeInfoRepository wealthTypeInfoRepository;
		
		@Autowired
		ProjectInvestInfoRepository projectInvestInfoRepository;
		
		@Autowired
		AuditInfoRepository auditInfoRepository;
		
		@Autowired
		LogInfoEntityRepository logInfoEntityRepository;
		
		@Autowired
		InvestInfoRepository investInfoRepository;
		
		@Autowired
		WealthPaymentPlanInfoRepository wealthPaymentPlanInfoRepository;
		
		@Autowired
		WealthInfoRepositoryCustom wealthInfoRepositoryCustom;
		
		@Autowired
		CustInfoRepository custInfoRepository;
		@Autowired
		AccountInfoRepository accountInfoRepository;
		@Autowired
		AtoneInfoRepository atoneInfoRepository;
		@Autowired
		ParamService paramService;
		@Autowired
		WealthHoldInfoRepository wealthHoldInfoRepository;
		@Autowired
		DeviceInfoRepository deviceInfoRepository;
		@Autowired
		FlowNumberService numberService;
		@Autowired
		private DeviceService deviceService;
		/**
		 * 自动生效优选计划
		 * 
		 * 1:判断生效日期是否是当天、状态是否是发布中、满额
		 * 2：是否有投资过
		 * 3：优选计划状态改成收益中、计算NEXT_REPAY_DAY,FIRST_REPAY_DAY,LAST_REPAY_DAY
		 * 4：投资状态投资中的改成收益中
		 * 5:生成投资的优选计划反息信息
		 */
		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public ResultVo effectWealth(Map<String, Object> params) throws SLException {
			String userId = (String) params.get("userId");
			String wealthId = (String) params.get("wealthId");
			WealthInfoEntity wealthInfoEntity = wealthInfoRepository.findOne(wealthId); //优选计划
			WealthTypeInfoEntity wealthTypeInfoEntity = wealthTypeInfoRepository.findOne(wealthInfoEntity.getWealthTypeId()); //优选计划类型
			String beforeStatus = wealthInfoEntity.getWealthStatus();
			//短息内容List
			List<Map<String, Object>> smsList = Lists.newArrayList();
			
			//1)check判断生效日期是否是当天
			int day = Integer.valueOf(DateUtilSL.getTwoDay(DateUtilSL.dateToStr(new Date()), DateUtilSL.dateToStr(wealthInfoEntity.getEffectDate())));
			if(day != 0) {
				return new ResultVo(false, "生效日期不是当天， 不允许生效操作！");
			}
			//2)check判断是否是发布中、计划满额的状态
			List<String> wealthStatusList = new ArrayList<String>();
			wealthStatusList.add(Constant.WEALTH_STATUS_05);
			wealthStatusList.add(Constant.WEALTH_STATUS_06);
			if(!wealthStatusList.contains(wealthInfoEntity.getWealthStatus())) {
				return new ResultVo(false, "计划状态不允许生效！"); 
			}
			//1)没有投资 做流标处理
			ProjectInvestInfoEntity projectInvestInfoEntity = projectInvestInfoRepository.findByWealthId(wealthId);
			if(projectInvestInfoEntity.getAlreadyInvestAmount().compareTo(BigDecimal.ZERO) == 0) {
				//流标处理
			}else { //正常生效
				Date releaseDate = wealthInfoEntity.getReleaseDate();
				
				wealthInfoEntity.setWealthStatus(Constant.WEALTH_STATUS_07); //收益中
				wealthInfoEntity.setFlowStatus(Constant.WEALTH_STATUS_07);
				
				//反息日计算
				if(Constant.WEALTH_INCOME_TYPE_02.equals(wealthTypeInfoEntity.getIncomeType())) { // 月返息
					String firstRepayDay = getReturnBackInterestDay(releaseDate); //首个反息日
					wealthInfoEntity.setFirstRepayDay(firstRepayDay); //首个反息日
					wealthInfoEntity.setNextRepayDay(firstRepayDay);
				}else { //到期结算本息
					wealthInfoEntity.setFirstRepayDay(DateUtilSL.print(wealthInfoEntity.getEndDate(), "yyyyMMdd"));
					wealthInfoEntity.setNextRepayDay(DateUtilSL.print(wealthInfoEntity.getEndDate(), "yyyyMMdd"));
				}
				wealthInfoEntity.setLastRepayDay(DateUtilSL.print(wealthInfoEntity.getEndDate(), "yyyyMMdd"));
				wealthInfoEntity.setBasicModelProperty(userId, false);
				
				//审核信息表
				AuditInfoEntity auditInfoEntity = auditInfoRepository.findAuditInfoEntityByRelatePrimary(wealthInfoEntity.getId());
				auditInfoEntity.setBasicModelProperty(userId, false);
				auditInfoEntity.setAuditStatus(wealthInfoEntity.getWealthStatus());
				auditInfoEntity.setAuditTime(new Date());
				
				//添加审核日志
				LogInfoEntity logInfoEntity = new LogInfoEntity(Constant.TABLE_BAO_T_WEALTH_INFO, wealthInfoEntity.getId(), Constant.OPERATION_TYPE_59, beforeStatus, wealthInfoEntity.getWealthStatus(), "生效", userId);
				logInfoEntityRepository.save(logInfoEntity);
				
				//投资信息更改
				List<InvestInfoEntity> investInfoEntityList = investInfoRepository.findByWealthId(wealthId);
				for(InvestInfoEntity invest : investInfoEntityList) {
					invest.setInvestStatus(Constant.INVEST_STATUS_EARN);//收益中
					invest.setBasicModelProperty(userId, false);
				}
				
				//生成优选计划反息表
				List<WealthPaymentPlanInfoEntity> WealthPaymentPlanInfoEntityList = getWealthPaymentPlanInfoByInvest(investInfoEntityList, wealthInfoEntity, wealthTypeInfoEntity, userId);
				wealthPaymentPlanInfoRepository.save(WealthPaymentPlanInfoEntityList);
				
				//发短信内容
				List<Map<String, Object>> list = wealthInfoRepositoryCustom.querySendSmsEffectWealthInfo(params);
				for(Map<String, Object> m : list) {
					Map<String, Object> smsParams = new HashMap<String, Object>();
					smsParams.put("mobile", m.get("mobile"));
					smsParams.put("custId", m.get("custId"));	
					smsParams.put("messageType", Constant.SMS_TYPE_WEALTH_RELEASE);
					
					smsParams.put("values", new Object[] { // 短信息内容
							m.get("lendingType"),
							ArithUtil.formatScale(new BigDecimal(m.get("yearRate").toString()), 2).toPlainString(),
							ArithUtil.formatScale(new BigDecimal(m.get("investAmount").toString()), 2).toPlainString(),
							Constant.SHANLINCAIFU_URL
					});
					
					smsParams.put("systemMessage", new Object[] { // 站内信内容
							m.get("investDate"),
							m.get("lendingType"),
							m.get("effectDate"),
							m.get("endDate"),
							ArithUtil.formatScale(new BigDecimal(m.get("yearRate").toString()), 2).toPlainString(),
							ArithUtil.formatScale(new BigDecimal(m.get("investAmount").toString()), 2).toPlainString()
					});
					smsList.add(smsParams);
				}
				
			}
			
			return new ResultVo(true, "自动生效优选计划成功！", smsList);
		}
		
		@Transactional(readOnly = false, rollbackFor=SLException.class)
		public ResultVo advance(Map<String, Object> params)throws SLException {
			String custId = (String) params.get("custId");
			String wealthId = (String) params.get("wealthId");
			String tradePassword = (String) params.get("tradePassword");
			String appSource = (String)params.get("appSource"); // 来源
			appSource = Strings.nullToEmpty(StringUtils.isEmpty(appSource) ? Constant.INVEST_SOURCE_PC : appSource).toLowerCase();
			Date atoneDate = DateUtils.getAfterDay(new Date(), 1); // 到帐日期 = 申请日期 + 1

			// 1) 验证用户状态
			CustInfoEntity custInfoEntity = custInfoRepository.findOne(custId);
			if(custInfoEntity == null){
				throw new SLException("用户不存在！");
			}
			if(!Constant.ENABLE_STATUS_01.equals(custInfoEntity.getEnableStatus())){
				throw new SLException("账户为非正常状态，可能被冻结，请联系客服！");
			}
			
			// 2) 验证交易密码
			if(!tradePassword.equals(custInfoEntity.getTradePassword())){
				throw new SLException("交易密码不正确");
			}
			AccountInfoEntity accountInfoEntity = accountInfoRepository.findByCustId(custId);
			if(accountInfoEntity == null) {
				throw new SLException("账户不存在");
			}
			
			// 3) 验证项目状态
			WealthInfoEntity wealthInfoEntity = wealthInfoRepository.findOne(wealthId);
			if(wealthInfoEntity == null) {
				return new ResultVo(false, "优选计划不存在");
			}
			if(!Constant.WEALTH_STATUS_07.equals(wealthInfoEntity.getWealthStatus())) {
				return new ResultVo(false, "优选计划非收益中状态不允许做提前赎回");
			}
			WealthTypeInfoEntity wealthTypeInfoEntity = wealthTypeInfoRepository.findOne(wealthInfoEntity.getWealthTypeId());
			if(wealthTypeInfoEntity == null) {
				return new ResultVo(false, "优选计划类型不存在");
			}

			// 4) 验证投资状态
			InvestInfoEntity investInfoEntity = investInfoRepository.findByWealthIdAndCustId(wealthId, custId);
			if(investInfoEntity == null) {
				throw new SLException("未找到投资记录");
			}
			AtoneInfoEntity existsAtone = atoneInfoRepository.findByInvestId(investInfoEntity.getId());
			if(existsAtone != null) {
				throw new SLException("该笔投资已经赎回，请勿重复赎回");
			}
			
			// 计算赎回金额、退出手续费
			// 到期结算本息：
			// 投资人收益 =投资金额*年化利率*(预计赎回日期-生效日期)/365
			// 按月返息：
			// 投资人收益 =投资金额*年化利率*出借期限*(约定交割日-上一返息日-1)/（封闭天数*12）
			// 提前退出费用=（投资金额+投资人收益 ）*3%
			// 用户赎回金额=投资金额+投资收益-提前退出费用
			
			// 5) 计算收益
			BigDecimal factIncome = BigDecimal.ZERO;
			BigDecimal manageExpenses = BigDecimal.ZERO;
			BigDecimal atoneAmount = BigDecimal.ZERO;
			if(Constant.WEALTH_INCOME_TYPE_01.equals(wealthTypeInfoEntity.getIncomeType())) {
				double dFactIncome = investInfoEntity.getInvestAmount().doubleValue()*wealthInfoEntity.getYearRate().doubleValue()*DateUtils.datePhaseDiffer(wealthInfoEntity.getEffectDate(), atoneDate)/365;
				factIncome = new BigDecimal(String.valueOf(dFactIncome));
			}
			else {
				// 封闭天数=出借期限/12不为整数=到期日-初始日期+1，出借期限/12为整数=365*出借期限/12
				int typeTerm = wealthTypeInfoEntity.getTypeTerm();
				int seatDays = typeTerm % 12 == 0 ? 
						365*typeTerm/12 : DateUtils.datePhaseDiffer(DateUtils.truncateDate(wealthInfoEntity.getEffectDate()), DateUtils.truncateDate(wealthInfoEntity.getEndDate())) + 1;
				double dFactIncome = investInfoEntity.getInvestAmount().doubleValue()*wealthInfoEntity.getYearRate().doubleValue()*wealthTypeInfoEntity.getTypeTerm()*(DateUtils.datePhaseDiffer(DateUtils.parseDate(wealthInfoEntity.getPrevRepayDay(), "yyyyMMdd"), atoneDate)-1)/(seatDays*12);
				factIncome = new BigDecimal(String.valueOf(dFactIncome));
			}
			factIncome = ArithUtil.formatScale(factIncome, 2);
			atoneAmount = ArithUtil.add(investInfoEntity.getInvestAmount(), factIncome);
			manageExpenses = ArithUtil.mul(atoneAmount, paramService.findAdvanceAtoneWealthRate(), 2);
			atoneAmount = ArithUtil.sub(atoneAmount, manageExpenses);
			
			// 6) 生成赎回订单表(赎回债权详情表由定时任务处理)
			AtoneInfoEntity atoneInfoEntity = new AtoneInfoEntity();
			atoneInfoEntity.setCustId(custId);
			atoneInfoEntity.setInvestId(investInfoEntity.getId());
			atoneInfoEntity.setOperType("");
			atoneInfoEntity.setTradeCode(numberService.generateTradeNumber());
			atoneInfoEntity.setCleanupDate(atoneDate);
			atoneInfoEntity.setAtoneMethod(Constant.ATONE_METHOD_ADVANCE);
			atoneInfoEntity.setAtoneExpenses(manageExpenses);
			atoneInfoEntity.setAtoneStatus(Constant.TRADE_STATUS_01);
			atoneInfoEntity.setAuditStatus(Constant.AUDIT_STATUS_REVIEWD);
			atoneInfoEntity.setAtoneTotalAmount(atoneAmount);
			atoneInfoEntity.setAlreadyAtoneAmount(atoneAmount);
			atoneInfoEntity.setTradeSource(appSource);
			atoneInfoEntity.setBasicModelProperty(custId, true);
			atoneInfoEntity = atoneInfoRepository.save(atoneInfoEntity);
			
			// 7) 投资状态改为提前赎回中
			investInfoEntity.setInvestStatus(Constant.INVEST_STATUS_ADVANCE);
			investInfoEntity.setBasicModelProperty(custId, false);
			
			// 8) 持有债权状态改为待转让
			List<WealthHoldInfoEntity> holdList = wealthHoldInfoRepository.findByInvestIdAndHoldStatus(investInfoEntity.getId(), Constant.HOLD_STATUS_01);
			for(WealthHoldInfoEntity entity : holdList) {
				entity.setHoldStatus(Constant.HOLD_STATUS_02);
				entity.setBasicModelProperty(custId, false);
			}
			
			// 9) 记录日志
			LogInfoEntity logInfoEntity = new LogInfoEntity();
			logInfoEntity.setRelateType(Constant.TABLE_BAO_T_ATONE_INFO);
			logInfoEntity.setRelatePrimary(atoneInfoEntity.getId());
			logInfoEntity.setLogType(Constant.OPERATION_TYPE_53);
			logInfoEntity.setOperDesc("");
			logInfoEntity.setOperPerson(custId);
			logInfoEntity.setOperIpaddress((String)params.get("ipAddress"));
			logInfoEntity.setMemo(String.format("%s赎回%s[%s]，赎回金额(已扣手续费)%s，手续费%s", custInfoEntity.getLoginName(), 
					wealthTypeInfoEntity.getLendingType(), wealthInfoEntity.getLendingNo(), atoneAmount.toPlainString(), manageExpenses.toPlainString()));
			logInfoEntity.setBasicModelProperty(custId, true);
			logInfoEntityRepository.save(logInfoEntity);
			
			// 10) 记录设备信息 
			if(params.containsKey("channelNo")) {
//				DeviceInfoEntity deviceInfoEntity = new DeviceInfoEntity();
//				deviceInfoEntity.setRelateTableIdentification(Constant.TABLE_BAO_T_ATONE_INFO);
//				deviceInfoEntity.setRelatePrimary(atoneInfoEntity.getId());
//				deviceInfoEntity.setCustId(custId);
//				deviceInfoEntity.setTradeType(Constant.OPERATION_TYPE_53);
//				deviceInfoEntity.setMeId((String)params.get("meId"));
//				deviceInfoEntity.setMeVersion((String)params.get("meVersion"));
//				deviceInfoEntity.setAppSource(appSource);
//				deviceInfoEntity.setChannelNo(paramService.findByChannelNo((String)params.get("channelNo")));
//				deviceInfoEntity.setBasicModelProperty(custId, true);
//				deviceInfoRepository.save(deviceInfoEntity);
				Map<String, Object> deviceParams = Maps.newConcurrentMap();
				deviceParams.putAll(params);
				deviceParams.put("relateType", Constant.TABLE_BAO_T_ATONE_INFO);
				deviceParams.put("relatePrimary", atoneInfoEntity.getId());
				deviceParams.put("tradeType", Constant.OPERATION_TYPE_53);
				deviceParams.put("userId", custId);
				deviceService.saveUserDevice(deviceParams);
			}

			// 11) 准备短信和站内信
			Map<String, Object> smsParams = new HashMap<String, Object>();
			smsParams.put("mobile", custInfoEntity.getMobile());
			smsParams.put("custId", custInfoEntity.getId());
			smsParams.put("messageType", Constant.SMS_TYPE_WEALTH_ADVANCED_ATONE_APPLY);
			smsParams.put("values", new Object[] {
					ArithUtil.formatScale(atoneAmount, 2).toPlainString(),
					ArithUtil.formatScale(investInfoEntity.getInvestAmount(), 2).toPlainString(),
					ArithUtil.formatScale(manageExpenses, 2).toPlainString(),
					Constant.SHANLINCAIFU_URL
			});
			smsParams.put("systemMessage", new Object[] {
					ArithUtil.formatScale(atoneAmount, 2).toPlainString(),
					ArithUtil.formatScale(investInfoEntity.getInvestAmount(), 2).toPlainString(),
					ArithUtil.formatScale(manageExpenses, 2).toPlainString()});
			
			return new ResultVo(true, "赎回成功", smsParams);
		} 
		
		/**
		 * 生成理财反息计划
		 * 
		 * @author zhiwen_feng
		 * @param invest
		 * @param wealth
		 * @param type
		 * @return
		 * @throws SLException
		 */
		// 返息金额=投资金额*年化利率*计息天数*出借期限/(12*封闭天数)
		// 计息天数：约定交割日-初始日期+1 
		// 约定交割日：返息日
		// 初始日期：第一次为生效日期，后面为上一个返息日
		public List<WealthPaymentPlanInfoEntity> getWealthPaymentPlanInfoByInvest(List<InvestInfoEntity> investList, 
				WealthInfoEntity wealth, WealthTypeInfoEntity type, String userId) throws SLException{
			List<WealthPaymentPlanInfoEntity> list = Lists.newArrayList();
			
			BigDecimal yearRate = ArithUtil.div(wealth.getYearRate(), new BigDecimal("1"), 4) ; //年利率
			BigDecimal awardRate = wealth.getAwardRate() == null ? BigDecimal.ZERO : wealth.getAwardRate(); //奖励收益利率
			BigDecimal term = BigDecimal.valueOf(type.getTypeTerm());
			int totalDay = type.getTypeTerm() % 12 == 0 ? 365*type.getTypeTerm()/12 : 
				DateUtils.datePhaseDiffer(DateUtils.truncateDate(wealth.getEffectDate()), DateUtils.truncateDate(wealth.getEndDate())) + 1;//封闭天数
			
			for (InvestInfoEntity invest : investList) {
				//月返息版
				if(Constant.WEALTH_INCOME_TYPE_02.equals(type.getIncomeType())) {
					boolean bool = true; //是否最后一期
					int currentTerm = 1;
					Date endDate = wealth.getEndDate(); //结束日期
					Date beginInterestDate = wealth.getEffectDate(); //起息日
					Date exceptPaymentDate = DateUtils.parseDate(wealth.getFirstRepayDay(), "yyyyMMdd"); //反息日
					
					while (DateUtils.datePhaseDiffer(exceptPaymentDate, endDate) >= 0 && bool) { //反息日小于结束日期
						WealthPaymentPlanInfoEntity plan = new WealthPaymentPlanInfoEntity();
						//利息天数
						int interestDay = (currentTerm == 1 ? DateUtils.datePhaseDiffer(beginInterestDate, exceptPaymentDate) + 1 : DateUtils.datePhaseDiffer(beginInterestDate, exceptPaymentDate));
						
						//返息金额=投资金额*年化利率*计息天数*出借期限/(12*封闭天数)
						BigDecimal interest = ArithUtil.formatScale2(ArithUtil.div(ArithUtil.mul(ArithUtil.mul(ArithUtil.mul(invest.getInvestAmount(), yearRate), new BigDecimal(interestDay)), term), ArithUtil.mul(new BigDecimal(12), new BigDecimal(totalDay))));
						//奖励收益
						BigDecimal awardAmount = BigDecimal.ZERO;
						//预收本金
						BigDecimal exceptPaymentPrincipal = BigDecimal.ZERO;
						
						if(DateUtils.datePhaseDiffer(exceptPaymentDate, endDate) == 0) { //已经是最后日期
							bool =false;
							exceptPaymentPrincipal = invest.getInvestAmount(); //最后一期的预计返款总额包括本金
							//奖励反息金额 = 投资金额*奖励收益利率*封闭天数*出借期限/(12*封闭天数)
							awardAmount = ArithUtil.formatScale2(ArithUtil.div(ArithUtil.mul(ArithUtil.mul(ArithUtil.mul(invest.getInvestAmount(), awardRate), new BigDecimal(totalDay)), term), ArithUtil.mul(new BigDecimal(12), new BigDecimal(totalDay))));

						}
						//预收总金额
						BigDecimal exceptPaymentAmount = ArithUtil.add(ArithUtil.add(interest, awardAmount), exceptPaymentPrincipal);
						
						plan.setBasicModelProperty(userId, true);
						plan.setCustId(invest.getCustId());
						plan.setInvestId(invest.getId());
						plan.setWealthId(wealth.getId());
						plan.setCurrentTerm(currentTerm);
						plan.setExceptPaymentDate(DateUtils.formatDate(exceptPaymentDate, "yyyyMMdd"));
						plan.setExceptPaymentAmount(exceptPaymentAmount);
						plan.setExceptPaymentPrincipal(exceptPaymentPrincipal);
						plan.setExceptPaymentInterest(interest);
						plan.setExceptPaymentAward(awardAmount);
						plan.setFactPaymentAmount(BigDecimal.ZERO);
						plan.setPaymentStatus(Constant.WEALTH_PAYMENT_PLAN_STATUS_NOT);
						list.add(plan);
						
						currentTerm = currentTerm + 1; //期数 + 1
						
						Calendar ca = Calendar.getInstance();
						ca.setTime(exceptPaymentDate);
						beginInterestDate = ca.getTime(); //起息反息日
						exceptPaymentDate = DateUtils.getAfterMonth(exceptPaymentDate, 1); //反息 = 反息日加一个月
						
						if(DateUtils.datePhaseDiffer(exceptPaymentDate, endDate) < 0) { //反息日大于结束日期 反息日等于结束日期
							exceptPaymentDate = endDate;
						}
					}
				//到期一次性返本付息
				}else if (Constant.WEALTH_INCOME_TYPE_01.equals(type.getIncomeType())){
					WealthPaymentPlanInfoEntity plan = new WealthPaymentPlanInfoEntity();
					//返息金额=投资金额*年化利率*计息天数*出借期限/(12*封闭天数)
					BigDecimal interest = ArithUtil.formatScale2(ArithUtil.div(ArithUtil.mul(ArithUtil.mul(ArithUtil.mul(invest.getInvestAmount(), yearRate), new BigDecimal(totalDay)), term), ArithUtil.mul(new BigDecimal(12), new BigDecimal(totalDay))));
					//奖励反息金额 = 投资金额*奖励收益利率*封闭天数*出借期限/(12*封闭天数)
					 BigDecimal awardAmount = ArithUtil.formatScale2(ArithUtil.div(ArithUtil.mul(ArithUtil.mul(ArithUtil.mul(invest.getInvestAmount(), awardRate), new BigDecimal(totalDay)), term), ArithUtil.mul(new BigDecimal(12), new BigDecimal(totalDay))));
					//预收本金
					BigDecimal exceptPaymentPrincipal = invest.getInvestAmount();
					//预收总金额
					BigDecimal exceptPaymentAmount = ArithUtil.add(ArithUtil.add(interest, awardAmount), exceptPaymentPrincipal);
					
					plan.setBasicModelProperty(userId, true);
					plan.setCustId(invest.getCustId());
					plan.setInvestId(invest.getId());
					plan.setWealthId(wealth.getId());
					plan.setCurrentTerm(1);
					plan.setExceptPaymentDate(DateUtils.formatDate(wealth.getEndDate(), "yyyyMMdd"));
					plan.setExceptPaymentAmount(exceptPaymentAmount);
					plan.setExceptPaymentPrincipal(exceptPaymentPrincipal);
					plan.setExceptPaymentInterest(interest);
					plan.setExceptPaymentAward(awardAmount);
					plan.setFactPaymentAmount(BigDecimal.ZERO);
					plan.setPaymentStatus(Constant.WEALTH_PAYMENT_PLAN_STATUS_NOT);
					list.add(plan);
				}
			}
			
			return list;
		}

	}
	
	
	/**
	 * 精选优选计划列表 --只取出4条
	 * 1： 相同类型的只取一个
	 * 2：非反息的方前面、反息放后面
	 * 3：然后按照期数排序
	 */
	@Override
	public ResultVo queryPriorityWealthList(Map<String, Object> params)throws SLException {
		List<Map<String, Object>> result = Lists.newArrayList(); //只要前四调不相同类型的
		List<Map<String, Object>> list = wealthInfoRepositoryCustom.queryPriorityWealthList(params);
		
		List<String> listStr = Lists.newArrayList();
		for(Map<String, Object> map : list) {
			if(result.size() == 4) 
				break;
			if(!listStr.contains((String) map.get("wealthTypeId"))) {//不存在该类型的优选计划
				listStr.add((String) map.get("wealthTypeId"));
//				if(!Constant.WEALTH_INCOME_TYPE_01.equals((String) map.get("incomeType"))) { 
//					setRank(map, 1000);
//				}
				result.add(map);
			}
				
		}
		//按照期数升序排序
		if(null != result && result.size() > 0) {
			Collections.sort(result, new orderingRule());
		}
		return new ResultVo(true, "查询优选计划列表成功！", result);
	}
	
	/**
	 * 排序
	 * @author zhiwen_feng
	 * @param map
	 * @param level
	 */
	public static void setRank(Map<String, Object> map, int level) {
		int sort = Integer.valueOf(map.get("sort").toString());
		sort = sort + level;
		map.put("sort", sort);
	}
	
	/**
	 * 查看优选计划
	 */
	@Override
	public ResultVo queryWealthDetail(Map<String, Object> params)throws SLException {
		String exitFee = "退出金额*" + ArithUtil.formatScale(ArithUtil.mul(paramService.findAdvanceAtoneWealthRate(), new BigDecimal(100)), 0)  + "%";
		params.put("exitFee", exitFee);
		return wealthInfoRepositoryCustom.queryWealthDetail(params);
	}
	
	/**
	 * 马上投资
	 * 
	 * @date 2016-02-27
	 * @author zhiwen_feng
	 * @param params
     *      <tt>wealthId   :String:计划主键</tt><br>
     *      <tt>custId     :String:客户ID</tt><br>
     *      <tt>tradeAmount:String:投资金额</tt><br>
     *      <tt>channelNo  :String:渠道号（可以为空）</tt><br>
     *      <tt>meId       :String:设备ID（可以为空）</tt><br>
     *      <tt>meVersion  :String:设备版本号（可以为空）</tt><br>
     *      <tt>appSource  :String:设备来源（可以为空）</tt><br>
     *      <tt>ipAddress  :String:ip地址（可以为空）</tt><br>
	 * @return ResultVo
	 * @throws SLException
	 * Check 1.0验证用户是否存在
	 * 		 1.1 验证用户状态是否正常
	 * 		 1.2验证投资金额是否小于等于账户可用余额
	 * 		 1.3验证投资金额是否大于起投金额
	 * 		 1.4验证投资金额是否整数倍递增（投资金额 - 起投金额 > 0 且（投资金额 - 起投金额）整除递增金额）
	 * 		 1.5验证项目是否待发布-金额是否够
	 * 1：修改投资情况信息-人数-金额-比例（已有过投资的人数不变）
	 * 2：判断理财项目是否满额
	 * 3：新建投资、投资明细
	 * 4：添加分账、分账户流水
	 * 5：主账户金额-加入金额、添加流水
	 * 6：记录设备信息
	 * 7:日志
	 * 8：判断是否满额， 满额的要找一条最近的待发布的优选计划发布
	 * 
	 */
	@Transactional(readOnly = false, rollbackFor=SLException.class)
	@Override
	public ResultVo joinWealth(Map<String, Object> params) throws SLException {
		String custId = (String) params.get("custId"); 
		String wealthId = (String) params.get("wealthId"); 
		BigDecimal tradeAmount = new BigDecimal(params.get("tradeAmount").toString()); //投资金额
		String appSource = (String)params.get("appSource"); // 来源
		appSource = Strings.nullToEmpty(StringUtils.isEmpty(appSource) ? Constant.INVEST_SOURCE_PC : appSource).toLowerCase();
		
		WealthInfoEntity wealthInfoEntity = wealthInfoRepository.findOne(wealthId); 
		WealthTypeInfoEntity WealthTypeInfoEntity = wealthTypeInfoRepository.findOne(wealthInfoEntity.getWealthTypeId());
		ProjectInvestInfoEntity projectInvestInfoEntity = projectInvestInfoRepository.findByWealthId(wealthId);
		CustInfoEntity custInfoEntity = custInfoRepository.findOne(custId);
		AccountInfoEntity accountInfoEntity = accountInfoRepository.findByCustId(custId); //客户账户表
		// 验证信息
		//1.0) Check 判断用户是否正常
		if(null == custInfoEntity) {
			return new ResultVo(false, "用户不存在！");
		}
		//1.1) Check 判断用户的状态是否是正常
		if(!Constant.ENABLE_STATUS_01.equals(custInfoEntity.getEnableStatus())) {
			return new ResultVo(false, "账户为非正常状态，可能被冻结，请联系客服！");
		}
		//1.2) Check 验证投资金额是否小于等于账户可用余额
		if(tradeAmount.compareTo(accountInfoEntity.getAccountAvailableAmount()) > 0) {
			return new ResultVo(false, "账户可用余额不足， 请充值！");
		}
		//1.3) Check 验证投资金额是否大于起投金额
		if(wealthInfoEntity.getInvestMinAmount().compareTo(tradeAmount) > 0) {
			return new ResultVo(false, "投资金额不能小于起投金额！");
		}
		//1.4) Check 验证投资金额是否整数倍递增（投资金额 - 起投金额 > 0 或者 （投资金额 - 起投金额）整除递增金额）
		if(tradeAmount.compareTo(wealthInfoEntity.getInvestMinAmount()) > 0 
				&& !isDivInt(wealthInfoEntity.getIncreaseAmount(), ArithUtil.sub(tradeAmount, wealthInfoEntity.getInvestMinAmount()))) {
			return new ResultVo(false, "投资金额必须是递增金额的整数倍！");
		}
		//1.5) Check 1.5验证项目是否待发布-金额是否够
		if(!wealthInfoEntity.getWealthStatus().equals(Constant.WEALTH_STATUS_05)) {
			return new ResultVo(false, "该项目不允许投资！");
		}
		if(tradeAmount.compareTo(ArithUtil.sub(wealthInfoEntity.getPlanTotalAmount(), projectInvestInfoEntity.getAlreadyInvestAmount())) > 0) {
			return new ResultVo(false, String.format("该计划剩余可投金额%s不足", String.valueOf(ArithUtil.sub(wealthInfoEntity.getPlanTotalAmount(), projectInvestInfoEntity.getAlreadyInvestAmount()))));
		}
		
		InvestInfoEntity investInfoEntity = investInfoRepository.findByWealthIdAndCustIdAndInvestStatus(wealthId, custId, Constant.INVEST_STATUS_INVESTING);
		//1: 修改投资情况信息-人数-金额-比例（已有过投资的人数不变）
		projectInvestInfoEntity.setAlreadyInvestAmount(ArithUtil.add(tradeAmount, projectInvestInfoEntity.getAlreadyInvestAmount())); //已投金额 = 已投金额 + 投资金额
		projectInvestInfoEntity.setAlreadyInvestScale(ArithUtil.div(projectInvestInfoEntity.getAlreadyInvestAmount(), wealthInfoEntity.getPlanTotalAmount())); //投资比例
		if(null == investInfoEntity) { //之前没有投资过该优选计划
			projectInvestInfoEntity.setAlreadyInvestPeoples(projectInvestInfoEntity.getAlreadyInvestPeoples() + 1); //投资人数  = +1
		}
		projectInvestInfoEntity.setBasicModelProperty(custId, false);
		
		//2: 判断理财项目是否满额 (计划总金额 - 一投金额 < 起投金额 就算满额)
		if((ArithUtil.sub(wealthInfoEntity.getPlanTotalAmount(), projectInvestInfoEntity.getAlreadyInvestAmount()).compareTo(wealthInfoEntity.getInvestMinAmount())) < 0) {
			wealthInfoEntity.setWealthStatus(Constant.WEALTH_STATUS_06);
			wealthInfoEntity.setFlowStatus(Constant.WEALTH_STATUS_06);
		}
		wealthInfoEntity.setBasicModelProperty(custId, false);
		
		//3：新建投资 (如果之前已有投资就修改)、投资明细
		//3-1 投资
		String tradeCode = numberService.generateLoanContractNumber(); //交易编号
		if(null != investInfoEntity) {
			investInfoEntity.setInvestAmount(ArithUtil.add(tradeAmount, investInfoEntity.getInvestAmount())); // 投资金额= +投资金额
			investInfoEntity.setBasicModelProperty(custId, false);
		}else { //之前没有投资
			investInfoEntity = new InvestInfoEntity();
			investInfoEntity.setId(SharedUtil.getUniqueString());
			investInfoEntity.setCustId(custId);
			investInfoEntity.setExpireDate(DateUtilSL.print(wealthInfoEntity.getEndDate(), "yyyyMMdd"));
			investInfoEntity.setInvestAmount(tradeAmount);
			investInfoEntity.setInvestDate(DateUtilSL.print(new Date(), "yyyyMMdd"));
			investInfoEntity.setInvestMode(Constant.INVEST_METHOD_JOIN); //加入
			investInfoEntity.setInvestStatus(Constant.INVEST_STATUS_INVESTING); //投资中
			investInfoEntity.setWealthId(wealthId);
			investInfoEntity.setBasicModelProperty(custId, true);
		}
		//添加客户经理
		CustRecommendInfoEntity custRecommendInfoEntity = custRecommendInfoRepository.findByQuiltCustIdAndRecordStatus(custInfoEntity.getId(), Constant.VALID_STATUS_VALID);
		if(null != custRecommendInfoEntity) 
			investInfoEntity.setCustManagerId(custRecommendInfoEntity.getCustId());
		investInfoEntity = investInfoRepository.save(investInfoEntity);
		
		//3-2 投资详情
		InvestDetailInfoEntity investDetailInfoEntity = new InvestDetailInfoEntity();
		investDetailInfoEntity.setId(SharedUtil.getUniqueString());
		investDetailInfoEntity.setInvestId(investInfoEntity.getId());
		investDetailInfoEntity.setTradeNo(tradeCode);
		investDetailInfoEntity.setInvestAmount(tradeAmount);
		investDetailInfoEntity.setInvestSource(appSource);
		investDetailInfoEntity.setBasicModelProperty(custId, true);
		investDetailInfoEntity = investDetailInfoRepository.save(investDetailInfoEntity);
		
		//4：添加客户分账、分账户流水
		//4-1 添加客户分账户 添加账户及记录流水（用户主账户——>客户分账户）
		String reqeustNo = numberService.generateTradeBatchNumber();
		SubAccountInfoEntity subAccountInfoEntity = subAccountInfoRepository.findByRelatePrimary(investInfoEntity.getId()); // 客户分账户
		if(null == subAccountInfoEntity) { //之前没有分账
			subAccountInfoEntity = new SubAccountInfoEntity();
			subAccountInfoEntity.setBasicModelProperty(custId, true);
			subAccountInfoEntity.setCustId(custId);
			subAccountInfoEntity.setAccountId(accountInfoEntity.getId());
			subAccountInfoEntity.setRelateType(Constant.TABLE_BAO_T_INVEST_INFO);
			subAccountInfoEntity.setRelatePrimary(investInfoEntity.getId());
			subAccountInfoEntity.setSubAccountNo(numberService.generateCustomerNumber());
			subAccountInfoEntity.setAccountTotalValue(BigDecimal.ZERO);
			subAccountInfoEntity.setAccountFreezeValue(BigDecimal.ZERO);
			subAccountInfoEntity.setAccountAvailableValue(BigDecimal.ZERO);
			subAccountInfoEntity.setAccountAmount(tradeAmount);
			subAccountInfoEntity.setDeviationAmount(BigDecimal.ZERO);
		} else {
			subAccountInfoEntity.setBasicModelProperty(custId, false);
			subAccountInfoEntity.setAccountAmount(ArithUtil.add(tradeAmount, subAccountInfoEntity.getAccountAmount()));
		}
		subAccountInfoEntity = subAccountInfoRepository.save(subAccountInfoEntity);
		
		//4-2 添加分账户流水
		AccountFlowInfoEntity accountFlowInfoEntity = accountFlowService.saveAccountFlow(null, subAccountInfoEntity, accountInfoEntity, null, "3", 
				SubjectConstant.TRADE_FLOW_TYPE_JOIN_WEALTH, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING, 
				tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_WEALTH_JOIN, 
				subAccountInfoEntity.getRelateType(), subAccountInfoEntity.getRelatePrimary(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
		accountFlowInfoEntity.setMemo(WealthTypeInfoEntity.getLendingType() + wealthInfoEntity.getLendingNo());
		
		//5: 主账户金额-加入金额、添加流水
		//5-1更新客户主账户
		accountInfoEntity.setAccountTotalAmount(ArithUtil.sub(accountInfoEntity.getAccountTotalAmount(), tradeAmount));
		accountInfoEntity.setAccountAvailableAmount(ArithUtil.sub(accountInfoEntity.getAccountAvailableAmount(), tradeAmount));
		accountInfoEntity.setBasicModelProperty(custId, false);	
		AccountFlowInfoEntity accountFlowInfoEntity1 = accountFlowService.saveAccountFlow(accountInfoEntity, null, null, subAccountInfoEntity, "2", 
				SubjectConstant.TRADE_FLOW_TYPE_JOIN_WEALTH, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, 
				tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_WEALTH_JOIN, 
				subAccountInfoEntity.getRelateType(), subAccountInfoEntity.getRelatePrimary(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
		accountFlowInfoEntity1.setMemo(WealthTypeInfoEntity.getLendingType() + wealthInfoEntity.getLendingNo());
		
		//6：记录设备信息
		if(params.containsKey("channelNo")) {
//			DeviceInfoEntity deviceInfoEntity = new DeviceInfoEntity();
//			deviceInfoEntity.setRelateTableIdentification(Constant.TABLE_BAO_T_INVEST_INFO);
//			deviceInfoEntity.setRelatePrimary(investInfoEntity.getId());
//			deviceInfoEntity.setCustId(custId);
//			deviceInfoEntity.setTradeType(Constant.OPERATION_TYPE_51);
//			deviceInfoEntity.setMeId((String)params.get("meId"));
//			deviceInfoEntity.setMeVersion((String)params.get("meVersion"));
//			deviceInfoEntity.setAppSource(appSource);
//			deviceInfoEntity.setChannelNo(paramService.findByChannelNo((String)params.get("channelNo")));
//			deviceInfoEntity.setBasicModelProperty(custId, true);
//			deviceInfoRepository.save(deviceInfoEntity);
			Map<String, Object> deviceParams = Maps.newConcurrentMap();
			deviceParams.putAll(params);
			deviceParams.put("relateType", Constant.TABLE_BAO_T_INVEST_INFO);
			deviceParams.put("relatePrimary", investInfoEntity.getId());
			deviceParams.put("tradeType", Constant.OPERATION_TYPE_51);
			deviceParams.put("userId", custId);
			deviceService.saveUserDevice(deviceParams);
		}
		// 7: 记录日志
		LogInfoEntity logInfoEntity = new LogInfoEntity();
		logInfoEntity.setRelateType(Constant.TABLE_BAO_T_INVEST_INFO);
		logInfoEntity.setRelatePrimary(investInfoEntity.getId());
		logInfoEntity.setLogType(Constant.OPERATION_TYPE_51);
		logInfoEntity.setOperDesc("");
		logInfoEntity.setOperPerson(custId);
		logInfoEntity.setOperIpaddress((String)params.get("ipAddress"));
		logInfoEntity.setMemo(String.format("%s购买优选计划，投资金额%s", custInfoEntity.getLoginName(), tradeAmount.toString()));
		logInfoEntity.setBasicModelProperty(custId, true);
		logInfoEntityRepository.save(logInfoEntity);
		
		//8：判断是否满额， 满额的要找一条最近的待发布的类型一样的优选计划发布 新开一个事物
		try {
			if(Constant.WEALTH_STATUS_06.equals(wealthInfoEntity.getWealthStatus())) { //满额
				List<WealthInfoEntity> wealthInfoEntityList = wealthInfoRepository.findByWealthTypeIdAndWealthStatusOrderByEffectDate(wealthInfoEntity.getWealthTypeId(), Constant.WEALTH_STATUS_04);
				if(null != wealthInfoEntityList && wealthInfoEntityList.size() > 0) {
					Map<String, Object> map = Maps.newHashMap();
					WealthInfoEntity wealth = wealthInfoEntityList.get(0);
					map.put("wealthId", wealth.getId());
					map.put("userId", "Root");
					wealthJobService.publishWealth(map);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return new ResultVo(true, "加入优选计划成功！");
	}
	
	/**
	 * 收益计算器
	 * //投资金额*年华利率*封闭天数*出借月份/(12*封闭天数)
	 */
	@Override
	public ResultVo caclWealth(Map<String, Object> params) throws SLException {
		Map<String, Object> result = Maps.newHashMap();
		String wealthId = (String) params.get("wealthId"); 
		BigDecimal tradeAmount = new BigDecimal(params.get("tradeAmount").toString()); //交易金额
		WealthInfoEntity wealth = wealthInfoRepository.findOne(wealthId);
		WealthTypeInfoEntity type = wealthTypeInfoRepository.findOne(wealth.getWealthTypeId());
		
		BigDecimal yearRate = wealth.getYearRate(); //年华利率
		BigDecimal awardRate = wealth.getAwardRate() == null ? BigDecimal.ZERO : wealth.getAwardRate();//奖励利率
		
		int closedDay = 0; //封闭天数
		
		boolean isDivInt = isDivInt(new BigDecimal("12"), new BigDecimal(type.getTypeTerm())); //是否整数
		if(isDivInt) { //整除
			closedDay = 365 * type.getTypeTerm() / 12;
		} else {
			closedDay = DateUtils.datePhaseDiffer(wealth.getEffectDate(), wealth.getEndDate()) + 1;
		}
		
		int interestDay = DateUtils.datePhaseDiffer(wealth.getEffectDate(), wealth.getEndDate()) + 1; //计息天数
		//到期结算本息的计息天数 = 封闭天数
		if(type.getIncomeType().equals(Constant.WEALTH_INCOME_TYPE_01)) {
			interestDay = closedDay;
		}
			
		//利息
		BigDecimal incomeAmount = ArithUtil.formatScale2(ArithUtil.div(ArithUtil.mul(ArithUtil.mul(ArithUtil.mul(tradeAmount, yearRate), BigDecimal.valueOf(interestDay)), BigDecimal.valueOf(type.getTypeTerm())), ArithUtil.mul(new BigDecimal("12"), BigDecimal.valueOf(closedDay))));
		//奖励收益
		BigDecimal awardAmount = ArithUtil.formatScale2(ArithUtil.div(ArithUtil.mul(ArithUtil.mul(ArithUtil.mul(tradeAmount, awardRate), BigDecimal.valueOf(interestDay)), BigDecimal.valueOf(type.getTypeTerm())), ArithUtil.mul(new BigDecimal("12"), BigDecimal.valueOf(closedDay))));
		
		result.put("totalAmount", ArithUtil.add(ArithUtil.add(tradeAmount, incomeAmount), awardAmount)); //总金额
		result.put("incomeAmount", incomeAmount);
		result.put("awardAmount", awardAmount);
		return new ResultVo(true, "收益计算成功！", result);
	}
	
	/**
	 * 收益计算器(通过计划类型计算)
	 */
	@Override
	public ResultVo caclWealth2(Map<String, Object> params) throws SLException {
		Map<String, Object> result = Maps.newHashMap();
		
		String wealthTypeId = (String) params.get("wealthTypeId");
		BigDecimal tradeAmount = new BigDecimal(params.get("tradeAmount").toString()); //交易金额
		WealthTypeInfoEntity type = wealthTypeInfoRepository.findOne(wealthTypeId);
		//new 一个理财计划 
		WealthInfoEntity wealth = new WealthInfoEntity();
		wealth.setAwardRate(BigDecimal.ZERO);
		wealth.setEffectDate(DateUtils.parseDate(DateUtils.getCurrentDate("yyyy-MM-dd"), "yyyy-MM-dd"));
		wealth.setEndDate(DateUtils.getAfterDay(DateUtils.getAfterMonth(wealth.getEffectDate(), type.getTypeTerm()), -1));
		wealth.setFirstRepayDay(getReturnBackInterestDay(wealth.getEffectDate()));
		
		int totalDay = type.getTypeTerm() % 12 == 0 ? 365*type.getTypeTerm()/12 : 
			DateUtils.datePhaseDiffer(DateUtils.truncateDate(wealth.getEffectDate()), DateUtils.truncateDate(wealth.getEndDate())) + 1;//封闭天数
		BigDecimal yearRate = type.getYearRate(); //年利率
		BigDecimal awardRate = BigDecimal.ZERO; //奖励收益利率
		BigDecimal term = BigDecimal.valueOf(type.getTypeTerm());
		
		List<Map<String, Object>> list = Lists.newArrayList();
		//月返息版
		if(Constant.WEALTH_INCOME_TYPE_02.equals(type.getIncomeType())) {
			boolean bool = true; //是否最后一期
			int currentTerm = 1;
			Date endDate = wealth.getEndDate(); //结束日期
			Date beginInterestDate = wealth.getEffectDate(); //起息日
			Date exceptPaymentDate = DateUtils.parseDate(wealth.getFirstRepayDay(), "yyyyMMdd"); //反息日
			
			while (DateUtils.datePhaseDiffer(exceptPaymentDate, endDate) >= 0 && bool) { //反息日小于结束日期
				Map<String, Object> plan = Maps.newHashMap();
				//利息天数
				int interestDay = (currentTerm == 1 ? DateUtils.datePhaseDiffer(beginInterestDate, exceptPaymentDate) + 1 : DateUtils.datePhaseDiffer(beginInterestDate, exceptPaymentDate));
				
				//返息金额=投资金额*年化利率*计息天数*出借期限/(12*封闭天数)
				BigDecimal interest = ArithUtil.formatScale2(ArithUtil.div(ArithUtil.mul(ArithUtil.mul(ArithUtil.mul(tradeAmount, yearRate), new BigDecimal(interestDay)), term), ArithUtil.mul(new BigDecimal(12), new BigDecimal(totalDay))));
				//奖励收益
				BigDecimal awardAmount = BigDecimal.ZERO;
				//预收本金
				BigDecimal exceptPaymentPrincipal = BigDecimal.ZERO;
				
				if(DateUtils.datePhaseDiffer(exceptPaymentDate, endDate) == 0) { //已经是最后日期
					bool =false;
					exceptPaymentPrincipal = tradeAmount; //最后一期的预计返款总额包括本金
					//奖励反息金额 = 投资金额*奖励收益利率*封闭天数*出借期限/(12*封闭天数)
					awardAmount = ArithUtil.formatScale2(ArithUtil.div(ArithUtil.mul(ArithUtil.mul(ArithUtil.mul(tradeAmount, awardRate), new BigDecimal(totalDay)), term), ArithUtil.mul(new BigDecimal(12), new BigDecimal(totalDay))));

				}
				//预收总金额
				BigDecimal exceptPaymentAmount = ArithUtil.add(ArithUtil.add(interest, awardAmount), exceptPaymentPrincipal);
				
				plan.put("interestDay", interestDay);
				plan.put("currentTerm", currentTerm);
				plan.put("exceptPaymentDate", DateUtils.formatDate(exceptPaymentDate, "yyyyMMdd"));
				plan.put("exceptPaymentAmount", exceptPaymentAmount);
				plan.put("exceptPaymentPrincipal", exceptPaymentPrincipal);
				plan.put("exceptPaymentInterest", interest);
				plan.put("exceptPaymentAward", awardAmount);
				list.add(plan);
				
				currentTerm = currentTerm + 1; //期数 + 1
				
				Calendar ca = Calendar.getInstance();
				ca.setTime(exceptPaymentDate);
				beginInterestDate = ca.getTime(); //起息反息日
				exceptPaymentDate = DateUtils.getAfterMonth(exceptPaymentDate, 1); //反息 = 反息日加一个月
				
				if(DateUtils.datePhaseDiffer(exceptPaymentDate, endDate) < 0) { //反息日大于结束日期 反息日等于结束日期
					exceptPaymentDate = endDate;
				}
			}
		//到期一次性返本付息
		}else if (Constant.WEALTH_INCOME_TYPE_01.equals(type.getIncomeType())){
			Map<String, Object> plan = Maps.newHashMap();
			//返息金额=投资金额*年化利率*计息天数*出借期限/(12*封闭天数)
			BigDecimal interest = ArithUtil.formatScale2(ArithUtil.div(ArithUtil.mul(ArithUtil.mul(ArithUtil.mul(tradeAmount, yearRate), new BigDecimal(totalDay)), term), ArithUtil.mul(new BigDecimal(12), new BigDecimal(totalDay))));
			//奖励反息金额 = 投资金额*奖励收益利率*封闭天数*出借期限/(12*封闭天数)
			 BigDecimal awardAmount = ArithUtil.formatScale2(ArithUtil.div(ArithUtil.mul(ArithUtil.mul(ArithUtil.mul(tradeAmount, awardRate), new BigDecimal(totalDay)), term), ArithUtil.mul(new BigDecimal(12), new BigDecimal(totalDay))));
			//预收本金
			BigDecimal exceptPaymentPrincipal = tradeAmount;
			//预收总金额
			BigDecimal exceptPaymentAmount = ArithUtil.add(ArithUtil.add(interest, awardAmount), exceptPaymentPrincipal);
			
			plan.put("interestDay", totalDay);
			plan.put("currentTerm", 1);
			plan.put("exceptPaymentDate", DateUtils.formatDate(wealth.getEndDate(), "yyyyMMdd"));
			plan.put("exceptPaymentAmount", exceptPaymentAmount);
			plan.put("exceptPaymentPrincipal", exceptPaymentPrincipal);
			plan.put("exceptPaymentInterest", interest);
			plan.put("exceptPaymentAward", awardAmount);
			list.add(plan);
		}
		result.put("data", list);
		return new ResultVo(true, "查询成功", result);
	}
	
	@Override
	public ResultVo queryWealthInvestList(Map<String, Object> params)throws SLException {
		return wealthInfoRepositoryCustom.queryWealthInvestList(params);
	}
	
	/**
	 * 撮合操作
	 * 
	 * @author zhiwen_feng
	 * @date 2016-02-29
	 * @param params
	 * @return
	 * @throws SLException
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false, rollbackFor=SLException.class)
	@Override
	public ResultVo autoMatchLoan(Map<String, Object> params)throws SLException {
		String userId = Constant.SYSTEM_USER_BACK;
		List<Map<String, Object>> matchList = (List<Map<String, Object>>) params.get("match_list"); //债权列表
		
		SubAccountInfoEntity subAccountInfoEntity = subAccountInfoRepository.findOne(params.get("subAccountId").toString());
		AccountInfoEntity accountInfoEntity = accountInfoRepository.findByCustId(Constant.CUST_ID_WEALTH_CENTER);
		
		//循环处理
		for (Map<String, Object> match : matchList) {
			//持有表
			WealthHoldInfoEntity wealthHoldInfoEntity = wealthHoldInfoRepository.findOne(match.get("holdId").toString());
			//新增了债权转让的序号
			String tradeNo = numberService.generateLoanTransferNo();
			//1:历史情况表
			WealthHoldHistoryInfoEntity wealthHoldHistoryInfoEntity = new WealthHoldHistoryInfoEntity();
			wealthHoldHistoryInfoEntity.setBasicModelProperty(userId, true);
			wealthHoldHistoryInfoEntity.setHoldId(wealthHoldInfoEntity.getId());
			wealthHoldHistoryInfoEntity.setCustId(wealthHoldInfoEntity.getCustId());
			wealthHoldHistoryInfoEntity.setLoanId(wealthHoldInfoEntity.getLoanId());
			wealthHoldHistoryInfoEntity.setHoldScale(wealthHoldInfoEntity.getHoldScale());
			wealthHoldHistoryInfoEntity.setHoldAmount(wealthHoldInfoEntity.getHoldAmount());
			wealthHoldHistoryInfoEntity.setExceptAmount(wealthHoldInfoEntity.getExceptAmount());
			wealthHoldHistoryInfoEntity.setReceivedAmount(wealthHoldInfoEntity.getReceivedAmount());
			wealthHoldHistoryInfoEntity.setHoldStatus(wealthHoldInfoEntity.getHoldStatus());
			wealthHoldHistoryInfoEntity.setHoldSource(wealthHoldInfoEntity.getHoldSource());
			wealthHoldHistoryInfoEntity.setIsCenter(wealthHoldInfoEntity.getIsCenter());
			wealthHoldHistoryInfoEntity.setRecordStatus(wealthHoldInfoEntity.getRecordStatus());
			wealthHoldHistoryInfoEntity.setTradeNo(tradeNo);
			wealthHoldHistoryInfoRepository.save(wealthHoldHistoryInfoEntity);
			
			//2:生成hold表信息
			BigDecimal matchAmount = (BigDecimal) match.get("match_Amount"); //匹配金额
			BigDecimal holdScale = ArithUtil.div((BigDecimal) match.get("match_Amount"), new BigDecimal(match.get("loanValue").toString())); //匹配比例
			
			WealthHoldInfoEntity newWealthHoldInfoEntity = new WealthHoldInfoEntity();
			newWealthHoldInfoEntity.setBasicModelProperty(userId, true);
			newWealthHoldInfoEntity.setInvestId((String) params.get("investId"));
			newWealthHoldInfoEntity.setSubAccountId((String) params.get("subAccountId"));
			newWealthHoldInfoEntity.setCustId((String) params.get("custId"));
			newWealthHoldInfoEntity.setLoanId((String) match.get("loanId")); 
			newWealthHoldInfoEntity.setHoldScale(holdScale);
			newWealthHoldInfoEntity.setHoldAmount(matchAmount);
			newWealthHoldInfoEntity.setExceptAmount(BigDecimal.ZERO);
			newWealthHoldInfoEntity.setReceivedAmount(BigDecimal.ZERO);
			newWealthHoldInfoEntity.setHoldStatus(Constant.BAO_FIXEDINVESTMENT_TYPE_CY);
			newWealthHoldInfoEntity.setIsCenter(Constant.IS_CENTER_02);
			newWealthHoldInfoEntity = wealthHoldInfoRepository.save(newWealthHoldInfoEntity);
			
			//3:生成转让表
			LoanTransferEntity loanTransferEntity = new LoanTransferEntity();
			loanTransferEntity.setBasicModelProperty(userId, true);
			loanTransferEntity.setSenderHoldId(wealthHoldInfoEntity.getId());
			loanTransferEntity.setReceiveHoldId(newWealthHoldInfoEntity.getId());
			loanTransferEntity.setSenderCustId(match.get("custId").toString());
			loanTransferEntity.setReceiveCustId(newWealthHoldInfoEntity.getCustId());
			loanTransferEntity.setSenderLoanId(wealthHoldInfoEntity.getLoanId());
			loanTransferEntity.setReceiveLoanId(newWealthHoldInfoEntity.getLoanId());
			loanTransferEntity.setTradeAmount(matchAmount);
			loanTransferEntity.setTradeScale(holdScale);
			loanTransferEntity.setTransferExpenses(BigDecimal.ZERO);
			loanTransferEntity.setMemo("债权匹配");
			loanTransferEntity.setTradeNo(tradeNo);
			loanTransferEntity = loanTransferRepository.save(loanTransferEntity);
			
			String reqeustNo = numberService.generateTradeBatchNumber();
			//4:分账户更新
			subAccountInfoEntity.setAccountAmount(ArithUtil.sub(subAccountInfoEntity.getAccountAmount(), matchAmount));
			subAccountInfoEntity.setBasicModelProperty(userId, false);
			
			//5:主账户更新
			accountInfoEntity.setAccountAvailableAmount(ArithUtil.add(accountInfoEntity.getAccountAvailableAmount(), matchAmount));
			accountInfoEntity.setAccountTotalAmount(ArithUtil.add(accountInfoEntity.getAccountTotalAmount(), matchAmount));
			accountInfoEntity.setBasicModelProperty(userId, false);
			
			//6：主账户流水添加
			AccountFlowInfoEntity accountFlowInfoEntity = accountFlowService.saveAccountFlow(accountInfoEntity, null, null, subAccountInfoEntity, "2", 
					SubjectConstant.TRADE_FLOW_TYPE_AUTO_MATCH, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING, 
					matchAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_WEALTH_AUTO_MATCH, 
					subAccountInfoEntity.getRelateType(), subAccountInfoEntity.getRelatePrimary(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
			accountFlowInfoEntity.setMemo(String.format("本次撮合债权%s元", ArithUtil.formatScale2(matchAmount).toPlainString()));
			
			//7：分账户流水添加
			AccountFlowInfoEntity accountFlowInfoEntity1 = accountFlowService.saveAccountFlow(null, subAccountInfoEntity, accountInfoEntity, null, "3", 
					SubjectConstant.TRADE_FLOW_TYPE_AUTO_MATCH, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, 
					matchAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_WEALTH_AUTO_MATCH, 
					subAccountInfoEntity.getRelateType(), subAccountInfoEntity.getRelatePrimary(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
			accountFlowInfoEntity1.setMemo(String.format("本次撮合债权%s元", ArithUtil.formatScale2(matchAmount).toPlainString()));
			
			//8: 居间人的hold表信息更新
			BigDecimal centerAmount = (BigDecimal) match.get("holdAmount"); //居间人金额
			BigDecimal shengYuAmount = ArithUtil.sub(centerAmount, matchAmount); //居间人剩余金额
			BigDecimal shengYuScale = ArithUtil.div(shengYuAmount, new BigDecimal(match.get("loanValue").toString())); //剩余比例
			WealthHoldInfoEntity centerWealthHoldInfo = wealthHoldInfoRepository.findOne(match.get("holdId").toString());
			centerWealthHoldInfo.setHoldScale(shengYuScale);
			centerWealthHoldInfo.setHoldStatus(shengYuAmount.compareTo(BigDecimal.ZERO) > 0 ? Constant.HOLD_STATUS_01 : Constant.HOLD_STATUS_04);
			centerWealthHoldInfo.setBasicModelProperty(userId, false);
		}
		
		return new ResultVo(true, "匹配成功！");
	}


//--------------------------------------------------------私有方法------------------------------------------------------------------------------	
	/**
	 * 升序排序
	 */
	public static class orderingRule implements Comparator<Map<String, Object>> {

		@Override
		public int compare(Map<String, Object> o1, Map<String, Object> o2) {
			int sort1 = Integer.valueOf(o1.get("sort").toString());
			int sort2 = Integer.valueOf(o2.get("sort").toString()) ;	
			return sort1 > sort2 ? 1 : (sort1 == sort2 ? 0 : -1);
		}
		
	}
	
	/**
	 * 获取first反息日
	 * @modify 需求变更
	 *  1：返息日：1号到15号投资，返息日为下个月1号
	 *  2：16号到月底投资，返息日为下个月下个月16号；
	 * @author zhiwen_feng
	 * @param effectDate 生效日期
	 * @return
	 */
	public static String getReturnBackInterestDay(Date effectDate) {
		String returnBackDay = "";
		Calendar ca = Calendar.getInstance();
		ca.setTime(effectDate);
		int day = ca.get(Calendar.DATE);//天
		if(day <= 15) {//1号到15号投资，返息日为下个月1号
			ca.add(Calendar.MONTH, 1);
			ca.set(Calendar.DATE, 1);
			returnBackDay = DateUtils.formatDate(ca.getTime(), "yyyyMMdd");
		} else if (day > 15) { //16号到月底投资，返息日为下个月下个月16号；
			ca.add(Calendar.MONTH, 1);
			ca.set(Calendar.DATE, 16);
			returnBackDay = DateUtils.formatDate(ca.getTime(), "yyyyMMdd");
		}
		return returnBackDay;
	}
	
	/**
	 * 期数获得
	 * 
	 * lendingNo = yyMMdd+产品编号-+当天发布的总条数
	 * @author zhiwen_feng
	 * @param releaseDate
	 * @param wealthTypeId
	 * @return
	 */
	public String getLendingNoByReleaseDate(Date releaseDate, WealthTypeInfoEntity wealthTypeInfoEntity, String wealthId) {
		String strCount = null;
		int max = 0;
		List<WealthInfoEntity> list = wealthInfoRepository.findCountWealthByReleaseDayWealthTypeId(wealthTypeInfoEntity.getId(), DateUtilSL.dateToStr(releaseDate));
		//去除自己的那条
		if(!Strings.isNullOrEmpty(wealthId) && null != list && list.size() > 0) {
			List<WealthInfoEntity> listWealth = Lists.newArrayList();
			for (WealthInfoEntity wealth : list) {
				if (!wealth.getId().equals(wealthId))
					listWealth.add(wealth);
			}
			
			if(null != listWealth && listWealth.size() > 0) {
				for(WealthInfoEntity wealth : listWealth) {
					String lendingNo = wealth.getLendingNo().split("-")[1];
					int no = Integer.valueOf(lendingNo.replaceFirst("^0*", ""));
					if(no > max) 
						max = no;
				}
			}
		}
		
		max = max + 1;
		//需要两位， 没有前面加0
		if(max < 10) {
			strCount = "0" + String.valueOf(max);
		}else {
			strCount = String.valueOf(max);
		}
		String lendingNo = DateUtils.formatDate(releaseDate, "yyMMdd") + wealthTypeInfoEntity.getProductNo() + "-" + strCount;
		
		return lendingNo;
	}
	
	/**
	 * 判断是否整除
	 * @param investMinAmount 被除数
	 * @param increaseAmount 除数
	 * @return
	 */
	private static boolean isDivInt(BigDecimal investMinAmount, BigDecimal increaseAmount){
		
		BigDecimal multiple = ArithUtil.div(increaseAmount, investMinAmount);
		BigDecimal mil = ArithUtil.formatScale(multiple, 0); 
		if(multiple.compareTo(mil) > 0) {
			return false;
		}
		return true;
	}

	@Override
	public ResultVo queryAdvancedWealthAtoneDetail(Map<String, Object> params) {
		String custId = (String) params.get("custId");
		String wealthId = (String) params.get("wealthId");
		Date atoneDate = DateUtils.getAfterDay(new Date(), 1); // 到帐日期 = 申请日期 + 1
		
		// 1) 验证用户状态
		CustInfoEntity custInfoEntity = custInfoRepository.findOne(custId);
		if(custInfoEntity == null){
			return new ResultVo(false, "用户不存在！");
		}
		if(!Constant.ENABLE_STATUS_01.equals(custInfoEntity.getEnableStatus())){
			return new ResultVo(false, "账户为非正常状态，可能被冻结，请联系客服！");
		}
		
		// 3) 验证项目状态
		WealthInfoEntity wealthInfoEntity = wealthInfoRepository.findOne(wealthId);
		if(wealthInfoEntity == null) {
			return new ResultVo(false, "优选计划不存在");
		}
		if(!Constant.WEALTH_STATUS_07.equals(wealthInfoEntity.getWealthStatus())) {
			return new ResultVo(false, "优选计划非收益中状态不允许做提前赎回");
		}
		WealthTypeInfoEntity wealthTypeInfoEntity = wealthTypeInfoRepository.findOne(wealthInfoEntity.getWealthTypeId());
		if(wealthTypeInfoEntity == null) {
			return new ResultVo(false, "优选计划类型不存在");
		}

		// 4) 验证投资状态
		InvestInfoEntity investInfoEntity = investInfoRepository.findByWealthIdAndCustId(wealthId, custId);
		if(investInfoEntity == null) {
			return new ResultVo(false, "未找到投资记录");
		}
		AtoneInfoEntity existsAtone = atoneInfoRepository.findByInvestId(investInfoEntity.getId());
		if(existsAtone != null) {
			return new ResultVo(false, "该笔投资已经赎回，请勿重复赎回");
		}
		
		// 计算赎回金额、退出手续费
		// 到期结算本息：
		// 投资人收益 =投资金额*年化利率*(预计赎回日期-生效日期)/365
		// 按月返息：
		// 投资人收益 =投资金额*年化利率*出借期限*(约定交割日-上一返息日-1)/（封闭天数*12）
		// 提前退出费用=（投资金额+投资人收益 ）*3%
		// 用户赎回金额=投资金额+投资收益-提前退出费用
		
		// 5) 计算收益
		BigDecimal factIncome = BigDecimal.ZERO;
		BigDecimal manageExpenses = BigDecimal.ZERO;
		BigDecimal atoneAmount = BigDecimal.ZERO;
		if(Constant.WEALTH_INCOME_TYPE_01.equals(wealthTypeInfoEntity.getIncomeType())) {
			double dFactIncome = investInfoEntity.getInvestAmount().doubleValue()*wealthInfoEntity.getYearRate().doubleValue()*DateUtils.datePhaseDiffer(wealthInfoEntity.getEffectDate(), atoneDate)/365;
			factIncome = new BigDecimal(String.valueOf(dFactIncome));
		}
		else {
			// 封闭天数=出借期限/12不为整数=到期日-初始日期+1，出借期限/12为整数=365*出借期限/12
			int typeTerm = wealthTypeInfoEntity.getTypeTerm();
			int seatDays = typeTerm % 12 == 0 ? 
					365*typeTerm/12 : DateUtils.datePhaseDiffer(DateUtils.truncateDate(wealthInfoEntity.getEffectDate()), DateUtils.truncateDate(wealthInfoEntity.getEndDate())) + 1;
			double dFactIncome = investInfoEntity.getInvestAmount().doubleValue()*wealthInfoEntity.getYearRate().doubleValue()*wealthTypeInfoEntity.getTypeTerm()*(DateUtils.datePhaseDiffer(DateUtils.parseDate(wealthInfoEntity.getPrevRepayDay(), "yyyyMMdd"), atoneDate)-1)/(seatDays*12);
			factIncome = new BigDecimal(String.valueOf(dFactIncome));
		}
		factIncome = ArithUtil.formatScale(factIncome, 2);
		atoneAmount = ArithUtil.add(investInfoEntity.getInvestAmount(), factIncome);
		manageExpenses = ArithUtil.mul(atoneAmount, paramService.findAdvanceAtoneWealthRate(), 2);
		atoneAmount = ArithUtil.sub(atoneAmount, manageExpenses);
				
		Map<String, Object> result = Maps.newHashMap();
		result.put("wealthId", wealthId);
		result.put("custId", custId);
		result.put("lendingType", wealthTypeInfoEntity.getLendingType());
		result.put("investAmount", investInfoEntity.getInvestAmount());
		result.put("atoneExpenses", manageExpenses);
		result.put("atoneTotalAmount", atoneAmount);
		result.put("atoneDate", atoneDate);
		result.put("lendingNo", wealthInfoEntity.getLendingNo());
		
		return new ResultVo(true, "查询提前赎回成功", result);
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false, rollbackFor=SLException.class)
	@Override
	public ResultVo advancedAtoneWealth(Map<String, Object> params)
			throws SLException {
		ResultVo result = innerWealthService.advance(params);
		if (ResultVo.isSuccess(result)) {
			Map<String, Object> sms = (Map<String, Object>) result.getValue("data");
			smsService.asnySendSMSAndSystemMessage(sms);
		}
		return result;
	}
	
	/**
	 * 优选计划收益展示
	 * 
	 *1:发布中、已满额 == 投资中
	 *2：收益中、到期处理中 == 收益中
	 *3：已到期 = 已结束
	 */
	@Override
	public ResultVo queryWealthTotalIncome(Map<String, Object> params)throws SLException {
		Map<String, Object> data = Maps.newHashMap();
		
		List<Map<String, Object>> list = wealthInfoRepositoryCustom.queryProjectIncome(params);
		Map<String, Object> created = Maps.newHashMap(); //投资中
		Map<String, Object> doing = Maps.newHashMap(); //收益中
		Map<String, Object> finished = Maps.newHashMap(); // 已结束
		created.put("totalCounts", BigDecimal.ZERO);
		created.put("totalTradeAmount", BigDecimal.ZERO);
		created.put("totalIncomeAmount", BigDecimal.ZERO);
		doing.putAll(created);
		finished.putAll(created);
		
		if (null != list && list.size() > 0) {
			for (Map<String, Object> o : list) {
				if("1".equals((String) o.get("investStatus"))) { //投资中
					created = o;
				}
				if("2".equals((String) o.get("investStatus"))) { //收益中
					doing = o;
				}
				if("3".equals((String) o.get("investStatus"))) { //已结束
					finished = o;
				}
			}
		}
		
		data.put("created", created);
		data.put("doing", doing);
		data.put("finished", finished);
		data.put("totalCount", ArithUtil.add(ArithUtil.add( (BigDecimal) created.get("totalCounts"), (BigDecimal) doing.get("totalCounts")), (BigDecimal) finished.get("totalCounts")));
		data.put("sumTotalTradeAmount", ArithUtil.add((BigDecimal) created.get("totalTradeAmount"), ArithUtil.add((BigDecimal) doing.get("totalTradeAmount"), (BigDecimal) finished.get("totalTradeAmount"))));
		data.put("sumTotalIncomeAmount", ArithUtil.add((BigDecimal) created.get("totalIncomeAmount"), ArithUtil.add((BigDecimal) doing.get("totalIncomeAmount"), (BigDecimal) finished.get("totalIncomeAmount"))));
		return new ResultVo(true, "查询成功！", data);
	}

	/**
	 * 优选计划列表(投资人)
	 */
	@Override
	public ResultVo queryMyWealthList(Map<String, Object> params)throws SLException {
		return wealthInfoRepositoryCustom.queryMyWealthList(params);
	}

	/**
	 * 优选计划查看
	 */
	@Override
	public ResultVo queryMyWealthDetail(Map<String, Object> params)throws SLException {
		return wealthInfoRepositoryCustom.queryMyWealthDetail(params);
	}

	@Override
	public ResultVo queryMyWealthLoan(Map<String, Object> params)throws SLException {
		
		return wealthInfoRepositoryCustom.queryMyWealthLoan(params);
	}

	/**
	 * 优选计划汇总
	 */
	@Override
	public ResultVo queryMyWealthIncome(Map<String, Object> params)throws SLException {
		return wealthInfoRepositoryCustom.queryMyWealthIncome(params);
	}

	@Override
	public ResultVo queryWealthContract(Map<String, Object> params)throws SLException {
		return new ResultVo(true, "查询成功！", wealthInfoRepositoryCustom.queryWealthContract(params));
	}

	@Override
	public ResultVo queryWealthLoanContract(Map<String, Object> params)throws SLException {
		return new ResultVo(true, "查询成功！", wealthInfoRepositoryCustom.queryWealthLoanContract(params));
	}

	@Override
	public ResultVo queryShowWealthList(Map<String, Object> params)throws SLException {
		return wealthInfoRepositoryCustom.queryShowWealthList(params);
	}

	/**
	 * 只查询一条， 先查精选理财计划， 如果有发布中的直接选取第一条返回
	 * 没有的话需要先看下企业借款有没有发布中的， 优化话直接返回， 没有选取优选理财计划的第一条返回
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ResultVo queryPriority(Map<String, Object> params) throws SLException {
		Map<String, Object> map = Maps.newHashMap();
		
		ResultVo wealthResult = queryPriorityWealthList(params);
		List<Map<String, Object>> wealthList = (List<Map<String, Object>>) wealthResult.getValue("data"); // 理财计划精选列表
		for(Map<String, Object> o : wealthList) {
			if(Constant.WEALTH_STATUS_05.equals((String) o.get("wealthStatus"))) { //发布中的优选
				map.put("plan", o);
				map.put("type", "plan");
				break;
			}
		}
		if(map.isEmpty()) { //理财计划没有发布中的， 选取项目的发布中的
			params.put("start", 0);
			params.put("length", 10);
			ResultVo projectResult = projectService.queryAllProjectList(params); //企业借款列表
			List<Map<String, Object>> projectList = (List<Map<String, Object>>) ((Map<String, Object>) projectResult.getValue("data")).get("data");
			for (Map<String, Object> o : projectList) {
				if(Constant.PROJECT_STATUS_06.equals((String) o.get("projectStatus"))) { //发布中
					map.put("project", o);
					map.put("type", "project");
					break;
				}
			}
			
			// 没有发布中的， 如果计划有去计划第一条、计划没有项目有取项目第一条， 否者返回false
			if(map.isEmpty()) { 
				if(null != wealthList && wealthList.size() > 0 ) {
					map.put("plan", wealthList.get(0));
					map.put("type", "plan");
				} else if (null != projectList && projectList.size() > 0){
					map.put("project", projectList.get(0));
					map.put("type", "project");
				}else {
					return new ResultVo(false, "没有满足条件的数据！");
				}
			}
		}
		
		return new ResultVo(true, "查询成功", map);
	}

	/**
	 * 优选债权列表
	 * 1发布中、已满额 展示可匹配的债权
	 * 2收益中、到期处理中、已到期 展示投资人所有已匹配的债权
	 * 3：其他状态不展示债权
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public ResultVo queryLoanInfoByWealthId(Map<String, Object> params) throws SLException {
		WealthInfoEntity wealthInfoEntity = wealthInfoRepository.findOne(params.get("wealthId").toString());
		Map<String, Object> map = Maps.newHashMap();
		map.put("iTotalDisplayRecords", 0);
		map.put("data", new ArrayList());
		Page<Map<String, Object>> page = null;
		
		switch (wealthInfoEntity.getWealthStatus()) {
			//1> 发布中、已满额 展示可匹配的债权
			case Constant.WEALTH_STATUS_05:
			case Constant.WEALTH_STATUS_06:
				page = wealthHoldInfoRepositoryCustom.queryCanUserLoan(params);
				break;
			//2> 收益中、到期处理中、已到期 展示投资人所有已匹配的债权
			case Constant.WEALTH_STATUS_07:
			case Constant.WEALTH_STATUS_08:	
			case Constant.WEALTH_STATUS_09:	
				page = wealthHoldInfoRepositoryCustom.queryPageAlreadyAutoLoanByWealthId(params);
				break;
		}
		
		if(null != page) {
			map.put("iTotalDisplayRecords", page.getTotalElements() > 100 ? 100 : page.getTotalElements());
			map.put("data", page.getContent());
		}
		return new ResultVo(true, "查询成功！", map);
	}

	@Override
	public ResultVo queryLoanInfoDetailByLoanId(Map<String, Object> params) throws SLException {
		return new ResultVo(true, "查询成功", loanInfoRepositoryCustom.queryLoanInfoDetailByLoanId((String) params.get("loanId")));
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResultVo queryEmployeeWealthList(Map<String, Object> params)
			throws SLException {
		
		String mobile = (String)params.get("mobile");
		String ids = (String)params.get("ids");
		
		CustInfoEntity custInfoEntity = custInfoRepository.findByMobile(mobile);
		if(custInfoEntity == null) {
			return new ResultVo(false, "业务员不存在");
		}
		
		if(!Constant.IS_RECOMMEND_YES.equals(custInfoEntity.getIsEmployee())) {
			return new ResultVo(false, "业务员不存在");
		}
		
		Map<String, Object> requestParam = Maps.newConcurrentMap();
		List<String> idList = Splitter.onPattern("&").omitEmptyStrings().splitToList(ids);
		requestParam.put("ids", idList);
		ResultVo resultVo = wealthInfoRepositoryCustom.queryAllShowWealthList(requestParam);
		Map<String, Object> data = (Map<String, Object>)resultVo.getValue("data");
		data.put("cust", custInfoEntity);
		
		return new ResultVo(true, "查询成功", data);
	}
}
