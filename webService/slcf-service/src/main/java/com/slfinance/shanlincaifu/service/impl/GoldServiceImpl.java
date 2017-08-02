/** 
 * @(#)GoldServiceImpl.java 1.0.0 2015年8月24日 上午10:49:27  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AccountInfoEntity;
import com.slfinance.shanlincaifu.entity.CommissionInfoEntity;
import com.slfinance.shanlincaifu.entity.CustInfoEntity;
import com.slfinance.shanlincaifu.entity.CustRecommendInfoEntity;
import com.slfinance.shanlincaifu.entity.InvestInfoEntity;
import com.slfinance.shanlincaifu.entity.LoanInfoEntity;
import com.slfinance.shanlincaifu.entity.LoanTransferApplyEntity;
import com.slfinance.shanlincaifu.entity.ProductTypeInfoEntity;
import com.slfinance.shanlincaifu.entity.TransAccountInfoEntity;
import com.slfinance.shanlincaifu.entity.UserCommissionInfoEntity;
import com.slfinance.shanlincaifu.entity.WealthHoldInfoEntity;
import com.slfinance.shanlincaifu.repository.AccountInfoRepository;
import com.slfinance.shanlincaifu.repository.CommissionDetailInfoRepository;
import com.slfinance.shanlincaifu.repository.CommissionInfoRepository;
import com.slfinance.shanlincaifu.repository.CustInfoRepository;
import com.slfinance.shanlincaifu.repository.CustRecommendInfoRepository;
import com.slfinance.shanlincaifu.repository.InvestInfoRepository;
import com.slfinance.shanlincaifu.repository.LoanInfoRepository;
import com.slfinance.shanlincaifu.repository.LoanTransferApplyRepository;
import com.slfinance.shanlincaifu.repository.ProductTypeInfoRepository;
import com.slfinance.shanlincaifu.repository.TransAccountInfoRepository;
import com.slfinance.shanlincaifu.repository.UserCommissionInfoRepository;
import com.slfinance.shanlincaifu.repository.WealthHoldInfoRepository;
import com.slfinance.shanlincaifu.repository.custom.CommissionInfoRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.DailySettlementRepository;
import com.slfinance.shanlincaifu.service.AccountFlowService;
import com.slfinance.shanlincaifu.service.CustAccountService;
import com.slfinance.shanlincaifu.service.FlowNumberService;
import com.slfinance.shanlincaifu.service.GoldService;
import com.slfinance.shanlincaifu.service.ParamService;
import com.slfinance.shanlincaifu.utils.ArithUtil;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.SubjectConstant;
import com.slfinance.vo.ResultVo;

/**   
 * 金牌推荐人接口实现类
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年8月24日 上午10:49:27 $ 
 */
@Service("goldService")
public class GoldServiceImpl implements GoldService {

	@Autowired
	private CustRecommendInfoRepository custRecommendInfoRepository;
	
	@Autowired
	private CommissionInfoRepository commissionInfoRepository;
	
	@Autowired
	private CommissionDetailInfoRepository commissionDetailInfoRepository;
	
	@Autowired
	private CommissionInfoRepositoryCustom commissionInfoRepositoryCustom;
	
	@Autowired
	private TransAccountInfoRepository transAccountInfoRepository;
	
	@Autowired
	private AccountInfoRepository accountInfoRepository;
	
	@Autowired
	private FlowNumberService numberService;
	
	@Autowired
	private AccountFlowService accountFlowService;
	
	@Autowired
	private DailySettlementRepository dailySettlementRepository;
	
	@Autowired
	private ProductTypeInfoRepository productTypeInfoRepository;
	
	@Autowired
	private UserCommissionInfoRepository userCommissionInfoRepository;
	
	@Autowired
	private InvestInfoRepository investInfoRepository;
	
	@Autowired
	private LoanInfoRepository loanInfoRepository;
	
	@Autowired
	private ParamService paramService;
	
	@Autowired
	private CustAccountService custAccountService;
	
	@Autowired
	private LoanTransferApplyRepository loanTransferApplyRepository;
	
	@Autowired
	private WealthHoldInfoRepository wealthHoldInfoRepository;
	
	@Autowired
	private CustInfoRepository custInfoRepository;

    @Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo goldDailySettlement(Map<String, Object> params)
			throws SLException {
		
		Date now = new Date();
		
		ProductTypeInfoEntity productTypeInfoEntity = productTypeInfoRepository.findByTypeName(Constant.PRODUCT_TYPE_01);
		
		int counts = commissionInfoRepository.countByCommDate(productTypeInfoEntity.getId(), DateUtils.getStartDate(now), DateUtils.getEndDate(now));
		if(counts > 0) {
			return new ResultVo(false, "重复运行");
		}
		
		CommissionInfoEntity commissionInfoEntity = new CommissionInfoEntity();
		commissionInfoEntity.setCommDate(new Timestamp(now.getTime()));
		commissionInfoEntity.setTradeStatus(Constant.USER_ACTIVITY_TRADE_STATUS_06);
		commissionInfoEntity.setProductTypeId(productTypeInfoEntity.getId());
		commissionInfoEntity.setCommMonth(DateUtils.formatDate(now, "yyyyMM"));
		commissionInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
		commissionInfoRepositoryCustom.batchInsert(commissionInfoEntity);
		return new ResultVo(true);
	}

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo goldWithdraw(Map<String, Object> params) throws SLException {
		
		if(!params.containsKey("date")) {
			params.put("date", new Date());
		}
		
		ProductTypeInfoEntity productTypeInfoEntity = productTypeInfoRepository.findByTypeName(Constant.PRODUCT_TYPE_01);
		params.put("productTypeId", productTypeInfoEntity.getId());
		
		List<AccountInfoEntity> accountInfoList = new ArrayList<AccountInfoEntity>();
		List<TransAccountInfoEntity> transAccountInfoList = new ArrayList<TransAccountInfoEntity>();
		
		AccountInfoEntity earnMainAccount = accountInfoRepository.findOne(Constant.ACCOUNT_ID_ERAN); // 公司收益主账户
		
		List<Map<String, Object>> list = commissionInfoRepositoryCustom.findTransfer(params);
		for(Map<String, Object> map : list) {
			
			AccountInfoEntity account = new AccountInfoEntity();
			account.setId(map.get("accountId").toString());
			account.setCustId(map.get("custId").toString());
			account.setAccountAvailableAmount(new BigDecimal(map.get("accountAvailableAmount").toString()));
			account.setAccountFreezeAmount(new BigDecimal(map.get("accountFreezeAmount").toString()));
			account.setAccountTotalAmount(new BigDecimal(map.get("accountTotalAmount").toString()));
			account.setVersion(new Integer(map.get("accountVersion").toString()));
			account.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
			accountInfoList.add(account);
			
			String requestNo = numberService.generateTradeBatchNumber();
			
			if(new BigDecimal(map.get("commissionAmount").toString()).compareTo(BigDecimal.ZERO) != 0) {
				// 推广佣金
				TransAccountInfoEntity transAccountInfoEntity = new TransAccountInfoEntity();
				transAccountInfoEntity.setIntoAccount(map.get("accountId").toString());
				transAccountInfoEntity.setExpendAccount(earnMainAccount.getId());
				transAccountInfoEntity.setTradeAmount(new BigDecimal(map.get("commissionAmount").toString()));
				transAccountInfoEntity.setTransType(Constant.TANS_ACCOUNT_TYPE_04);
				transAccountInfoEntity.setAuditStatus(Constant.AUDIT_STATUS_PASS);
				transAccountInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
				transAccountInfoList.add(transAccountInfoEntity);
				
				// 推广佣金流水
				account.setAccountTotalAmount(ArithUtil.add(account.getAccountTotalAmount(), transAccountInfoEntity.getTradeAmount()));
				account.setAccountAvailableAmount(ArithUtil.add(account.getAccountAvailableAmount(), transAccountInfoEntity.getTradeAmount()));
				accountFlowService.saveAccountFlow(account, null, null, null, "1", 
						SubjectConstant.TRADE_FLOW_TYPE_GOLD_INCOME, requestNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING, 
						transAccountInfoEntity.getTradeAmount(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_GOLD_INCOME, 
						null, null, SubjectConstant.SUBJECT_TYPE_AMOUNT);
				
				earnMainAccount.setAccountTotalAmount(ArithUtil.sub(earnMainAccount.getAccountTotalAmount(), transAccountInfoEntity.getTradeAmount()));
				earnMainAccount.setAccountAvailableAmount(ArithUtil.sub(earnMainAccount.getAccountAvailableAmount(), transAccountInfoEntity.getTradeAmount()));
				accountFlowService.saveAccountFlow(earnMainAccount, null, null, null, "1", 
						SubjectConstant.TRADE_FLOW_TYPE_GOLD_INCOME, requestNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, 
						transAccountInfoEntity.getTradeAmount(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_GOLD_INCOME, 
						null, null, SubjectConstant.SUBJECT_TYPE_AMOUNT);
			}
			
			if(new BigDecimal(map.get("rewardAmount").toString()).compareTo(BigDecimal.ZERO) != 0) {
				// 推广奖励
				TransAccountInfoEntity transAccountInfoEntity2 = new TransAccountInfoEntity();
				transAccountInfoEntity2.setIntoAccount(map.get("accountId").toString());
				transAccountInfoEntity2.setExpendAccount(earnMainAccount.getId());
				transAccountInfoEntity2.setTradeAmount(new BigDecimal(map.get("rewardAmount").toString()));
				transAccountInfoEntity2.setTransType(Constant.TANS_ACCOUNT_TYPE_05);
				transAccountInfoEntity2.setAuditStatus(Constant.AUDIT_STATUS_PASS);
				transAccountInfoEntity2.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
				transAccountInfoList.add(transAccountInfoEntity2);
				
				// 推广奖励流水
				account.setAccountTotalAmount(ArithUtil.add(account.getAccountTotalAmount(), transAccountInfoEntity2.getTradeAmount()));
				account.setAccountAvailableAmount(ArithUtil.add(account.getAccountAvailableAmount(), transAccountInfoEntity2.getTradeAmount()));
				accountFlowService.saveAccountFlow(account, null, null, null, "1", 
						SubjectConstant.TRADE_FLOW_TYPE_GOLD_AWARD, requestNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING, 
						transAccountInfoEntity2.getTradeAmount(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_GOLD_AWARD, 
						null, null, SubjectConstant.SUBJECT_TYPE_AMOUNT);
				
				earnMainAccount.setAccountTotalAmount(ArithUtil.sub(earnMainAccount.getAccountTotalAmount(), transAccountInfoEntity2.getTradeAmount()));
				earnMainAccount.setAccountAvailableAmount(ArithUtil.sub(earnMainAccount.getAccountAvailableAmount(), transAccountInfoEntity2.getTradeAmount()));
				accountFlowService.saveAccountFlow(earnMainAccount, null, null, null, "1", 
						SubjectConstant.TRADE_FLOW_TYPE_GOLD_AWARD, requestNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, 
						transAccountInfoEntity2.getTradeAmount(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_GOLD_AWARD, 
						null, null, SubjectConstant.SUBJECT_TYPE_AMOUNT);
			}			
		}
		
		dailySettlementRepository.batchUpdateAccount(accountInfoList);
		dailySettlementRepository.batchInsertList(transAccountInfoList);
		
		commissionInfoRepository.updateTradeStatusByCommDate(DateUtils.getMonthStartDate(new Date()));
		
		return new ResultVo(true);
	}

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo goldMonthlySettlement(Map<String, Object> params)
			throws SLException {
		
		String commonMonth = "";
		Date now = new Date();
		if(params.containsKey("currentDate")) {
			now = DateUtils.parseDate((String)params.get("currentDate"), "yyyyMMdd");
			commonMonth = DateUtils.formatDate(now, "yyyyMM");
		}
		else {
			now = new Date();
			commonMonth = DateUtils.formatDate(now, "yyyyMM");
		}
		/*// 提成月取上个月，因为是每月1号结算，故减掉1个月
		String commonMonth = DateUtils.formatDate(DateUtils.getAfterMonth(now, -1), "yyyyMM");
		
		ProductTypeInfoEntity productTypeInfoEntity = productTypeInfoRepository.findByTypeName(Constant.PRODUCT_TYPE_05);
		
		int counts = commissionInfoRepository.countByCommonMonth(productTypeInfoEntity.getId(), commonMonth);
		if(counts > 0) {
			return new ResultVo(false, "重复运行");
		}*/

		CommissionInfoEntity commissionInfoEntity = new CommissionInfoEntity();
		commissionInfoEntity.setCommDate(new Timestamp(now.getTime()));
		commissionInfoEntity.setTradeStatus(Constant.USER_ACTIVITY_TRADE_STATUS_06);
		//commissionInfoEntity.setProductTypeId(productTypeInfoEntity.getId());
		commissionInfoEntity.setCommMonth(commonMonth);
		commissionInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
		//commissionInfoRepositoryCustom.batchProjectAndWealth(commissionInfoEntity);
		//commissionInfoRepositoryCustom.batchTermInsert(commissionInfoEntity);
		commissionInfoRepositoryCustom.batchProjectAndWealthAndLoan(commissionInfoEntity);
		
		return new ResultVo(true);
	}

    @Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo createSingleCommission(Map<String, Object> params)
			throws SLException {
		
		String investId = (String)params.get("investId");
		String custId   = (String)params.get("custId");
		String userId   = (String)params.get("userId");
		String loanUnit = (String)params.get("loanUnit");// 2017-3-25 09:45:13 借款期限单位（月、天）

		if(StringUtils.isEmpty(userId)) {
			userId = Constant.SYSTEM_USER_BACK;
		}
		
		// 1) 查询投资金额
		InvestInfoEntity investInfoEntity = investInfoRepository.findOne(investId);
		if(investInfoEntity == null) {
			return new ResultVo(false, "投资记录不存在");
		}
		
		// 2) 查询产品信息
		LoanInfoEntity loanInfoEntity = loanInfoRepository.findOne(investInfoEntity.getLoanId());
		if(loanInfoEntity == null) {
			return new ResultVo(false, "借款信息不存在");
		}
		if(loanInfoEntity.getInvestStartDate() == null) {
			return new ResultVo(false, "缺少生效时间");
		}
		
		if(Constant.INVEST_METHOD_JOIN.equals(investInfoEntity.getInvestMode())) { // 加入
			createJoinCommission(loanInfoEntity, investInfoEntity, custId, userId, loanUnit);
		}
		else if(Constant.INVEST_METHOD_TRANSFER.equals(investInfoEntity.getInvestMode())) { // 债权转让
			CustRecommendInfoEntity custRecommendInfoEntity = custRecommendInfoRepository.findByQuiltCustIdAndRecordStatus(investInfoEntity.getCustId(), Constant.VALID_STATUS_VALID);
			if(custRecommendInfoEntity == null) { // 新投资人没有推荐人
				// 判断新投资人是不是金牌推荐人
				CustInfoEntity custInfoEntity = custInfoRepository.findOne(investInfoEntity.getCustId());
				if(!Constant.IS_RECOMMEND_YES.equals(custInfoEntity.getIsRecommend())) { // 非金牌推荐人
					createTransferOutCommission(investInfoEntity, userId);
					investInfoEntity.setMemo("已处理转出业绩");
				}
				else { // 新投资人有推荐人则更新转入和转出
					createTransferCommission(loanInfoEntity, investInfoEntity, custId, userId);
				}
			}
			else { // 新投资人有推荐人则更新转入和转出
				createTransferCommission(loanInfoEntity, investInfoEntity, custId, userId);
			}
		}

		return new ResultVo(true, "保存佣金成功");
	}

	/**
	 * 加入计算佣金
	 *
	 * @author  wangjf
	 * @date    2017年1月12日 上午11:24:05
	 * @param loanInfoEntity
	 * @param investInfoEntity
	 * @param userId
	 * @param loanUnit
     * @return
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo createJoinCommission(LoanInfoEntity loanInfoEntity, InvestInfoEntity investInfoEntity, String custManagerId, String userId, String loanUnit) throws SLException {
		
		Integer startTerm = 1;
        Integer endTerm = Integer.parseInt(loanInfoEntity.getLoanTerm().toString());
        Date releaseDate = DateUtils.parseDate(investInfoEntity.getEffectDate(), "yyyyMMdd"); // 生效时间
		BigDecimal commissionRate = paramService.findCommissionRate(); // 提成比例
        BigDecimal commissionAwardRate = paramService.findCommissionAwardRate(); // 奖励利率
        BigDecimal interestDays = new BigDecimal("30");// 计息天数
        if (Constant.LOAN_UNIT_DAY.equals(loanUnit)) {
            // 天标没有补贴
            commissionAwardRate = BigDecimal.ZERO;
            // 天标期限=起息结束日期-起息开始日期
            interestDays = new BigDecimal(DateUtils.datePhaseDiffer(loanInfoEntity.getInvestStartDate(), loanInfoEntity.getInvestEndDate()));
            // 天标期限是1
            endTerm = 1;
        }
		List<UserCommissionInfoEntity> commissionList = Lists.newArrayList();
		for(int i = startTerm; i <= endTerm; i ++) {
            Date exceptPaymentDate;
            if (Constant.LOAN_UNIT_MONTH.equals(loanUnit)) {
                exceptPaymentDate = DateUtils.getAfterMonth(releaseDate, i);
                if (exceptPaymentDate.compareTo(loanInfoEntity.getInvestEndDate()) > 0) {
                    exceptPaymentDate = loanInfoEntity.getInvestEndDate();
                }
            } else {// 天标只有一期，所以返佣时间=天标到期时间
                exceptPaymentDate = loanInfoEntity.getInvestEndDate();
            }

			UserCommissionInfoEntity userCommissionInfoEntity = createCommission(investInfoEntity.getInvestAmount(), commissionRate, commissionAwardRate,
					interestDays, i, custManagerId, investInfoEntity.getId(), DateUtils.formatDate(exceptPaymentDate, "yyyyMMdd"), userId,
					BigDecimal.ONE);

			commissionList.add(userCommissionInfoEntity);
		}
		userCommissionInfoRepository.save(commissionList);
		
		return new ResultVo(true, "保存佣金成功");
	}

	/**
	 * 债权转让计算佣金
	 *
	 * @author  wangjf
	 * @date    2017年1月12日 上午11:26:39
	 * @param loanInfoEntity
	 * @param investInfoEntity
	 * @param userId
	 * @return
	 * @throws SLException 
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo createTransferCommission(LoanInfoEntity loanInfoEntity, InvestInfoEntity investInfoEntity, String custManagerId, String userId) throws SLException {

		// Step-1) 计算转出佣金
		createTransferOutCommission(investInfoEntity, userId);
		
		// Step-2) 计算转入佣金
		BigDecimal commissionRate = paramService.findCommissionRate(); // 提成比例
		BigDecimal commissionAwardRate = paramService.findCommissionAwardRate(); // 奖励利率
		List<UserCommissionInfoEntity> commissionList = Lists.newArrayList();
		Date releaseDate = DateUtils.parseDate(investInfoEntity.getEffectDate(), "yyyyMMdd"); // 生效时间
		Date endDate = DateUtils.parseDate(investInfoEntity.getExpireDate(), "yyyyMMdd"); // 到期时间
		Integer endTerm = DateUtils.monthPhaseDiffer(releaseDate, endDate);
		Integer startTerm = 1;
		Integer interestDays = 30;// 计息天数
		Date prevExceptPaymentDate = null;
		for(int i = startTerm; i <= endTerm; i ++) {
			
			Date exceptPaymentDate = DateUtils.getAfterMonth(releaseDate, i);
			if(exceptPaymentDate.compareTo(loanInfoEntity.getInvestEndDate()) >= 0) { // 最后一期
				if(prevExceptPaymentDate == null) { // 若上个返佣日期为空，则取生效日期
					prevExceptPaymentDate = releaseDate;
				}
				// 最后一期计息天数 = 到期日期 - 上一个返佣日期
				interestDays = DateUtils.datePhaseDiffer(prevExceptPaymentDate, loanInfoEntity.getInvestEndDate());			
				exceptPaymentDate = loanInfoEntity.getInvestEndDate();
			}
			prevExceptPaymentDate = exceptPaymentDate;
			
			UserCommissionInfoEntity userCommissionInfoEntity = createCommission(investInfoEntity.getInvestAmount(), commissionRate, commissionAwardRate, 
					new BigDecimal(interestDays), i, custManagerId, 
					investInfoEntity.getId(), DateUtils.formatDate(exceptPaymentDate, "yyyyMMdd"), userId,
					BigDecimal.ONE);
			
			commissionList.add(userCommissionInfoEntity);
		}
		userCommissionInfoRepository.save(commissionList);

		return new ResultVo(true, "保存佣金转入成功"); 
	}
	
	/**
	 * 创建债权转让转出佣金业绩
	 *
	 * @author  wangjf
	 * @date    2017年3月10日 上午11:03:18
	 * @param investInfoEntity
	 * @param userId
	 * @return
	 * @throws SLException
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo createTransferOutCommission(InvestInfoEntity investInfoEntity, String userId) throws SLException {
		
		LoanTransferApplyEntity loanTransferApplyEntity = loanTransferApplyRepository.findOne(investInfoEntity.getTransferApplyId());
		if(loanTransferApplyEntity == null) {
			return new ResultVo(false, "缺少转让申请");
		}
		
		WealthHoldInfoEntity wealthHoldInfoEntity = wealthHoldInfoRepository.findOne(loanTransferApplyEntity.getSenderHoldId());
		if(wealthHoldInfoEntity == null) {
			return new ResultVo(false, "缺少持有情况");
		}
		
		InvestInfoEntity sourceInvestInfoEntity = investInfoRepository.findOne(wealthHoldInfoEntity.getInvestId());
		if(sourceInvestInfoEntity == null) {
			return new ResultVo(false, "缺少转让来源");
		}
		
		List<UserCommissionInfoEntity> userCommissionList = userCommissionInfoRepository.findByInvestIdOrderByCurrentTermAsc(sourceInvestInfoEntity.getId());
		int pos = -1; // 表示转让开始的期数
		for(int i = 0; i < userCommissionList.size(); i ++) {
			UserCommissionInfoEntity u = userCommissionList.get(i);
			if(Constant.PAYMENT_STATUS_02.equals(u.getPaymentStatus())) {
				u.setPaymentStatus(Constant.PAYMENT_STATUS_03);
				u.setTotalPaymentAmount(BigDecimal.ZERO);
				u.setBasicModelProperty(userId, false);
				u.setMemo("系统作废");
				if(pos == -1) {
					pos = i;
				}
			}
		}
		
		if(pos != -1) {			
			// Step-1-2) 生成新的业绩
			BigDecimal commissionRate = paramService.findCommissionRate(); // 提成比例
			BigDecimal commissionAwardRate = paramService.findCommissionAwardRate(); // 奖励利率
			BigDecimal remainderScale = ArithUtil.div(wealthHoldInfoEntity.getHoldScale(), ArithUtil.add(wealthHoldInfoEntity.getHoldScale(), wealthHoldInfoEntity.getTradeScale()));
			List<UserCommissionInfoEntity> commissionList = Lists.newArrayList();
			if(Constant.HOLD_STATUS_04.equals(wealthHoldInfoEntity.getHoldStatus())) { // 全部转让
				
				Date prevExceptPaymentDate = null;
				// 判断上一返佣日期是否存在
				UserCommissionInfoEntity prevUserCommissionInfoEntity = null;
				if(pos == 0) { // 不存在上一个返佣日，上个返佣日取起息日期
					prevExceptPaymentDate = DateUtils.parseDate(sourceInvestInfoEntity.getEffectDate(), "yyyyMMdd");
					// 上一个返佣对象
					prevUserCommissionInfoEntity = userCommissionInfoRepository.findLatestMinByInvestIdAndExceptPaymentDate(sourceInvestInfoEntity.getId(), userCommissionList.get(pos).getExceptPaymentDate());
				}
				else {
					prevExceptPaymentDate = DateUtils.parseDate(userCommissionList.get(pos - 1).getExceptPaymentDate(), "yyyyMMdd");
					String beforeExceptPaymentDate = userCommissionList.get(pos - 1).getExceptPaymentDate();
					// 上一个返佣对象
					prevUserCommissionInfoEntity = userCommissionInfoRepository.findLatestMaxByInvestIdAndExceptPaymentDate(sourceInvestInfoEntity.getId(), beforeExceptPaymentDate, beforeExceptPaymentDate);
				}
				
				// 反推转让比例 = 佣金/(投资金额*提成比例*30/360)
				remainderScale = ArithUtil.div(prevUserCommissionInfoEntity.getPaymentPrincipal(), ArithUtil.div(ArithUtil.mul(ArithUtil.mul(sourceInvestInfoEntity.getInvestAmount(), commissionRate), new BigDecimal("30")), new BigDecimal("360"))); 
				
				Integer interestDays = DateUtils.datePhaseDiffer(prevExceptPaymentDate, DateUtils.parseDate(investInfoEntity.getEffectDate(), "yyyyMMdd"));
				BigDecimal principalAmount = cacl(sourceInvestInfoEntity.getInvestAmount(), commissionRate, new BigDecimal(interestDays), remainderScale);
				BigDecimal interestAmount  = BigDecimal.ZERO;
				if(pos == 0) {
					interestAmount = BigDecimal.ZERO;
				}
				else {
					interestAmount = cacl(principalAmount, commissionAwardRate, new BigDecimal((pos - 1)*30), BigDecimal.ONE);
					interestAmount = ArithUtil.add(interestAmount, cacl(principalAmount, commissionAwardRate, new BigDecimal(interestDays), BigDecimal.ONE));
				}
				
				UserCommissionInfoEntity userCommissionInfoEntity = new UserCommissionInfoEntity();
				userCommissionInfoEntity.setCustId(userCommissionList.get(0).getCustId());
				userCommissionInfoEntity.setInvestId(userCommissionList.get(0).getInvestId());
				userCommissionInfoEntity.setCurrentTerm(userCommissionList.get(pos).getCurrentTerm());
				userCommissionInfoEntity.setExceptPaymentDate(investInfoEntity.getEffectDate());
				userCommissionInfoEntity.setTotalPaymentAmount(ArithUtil.add(principalAmount, interestAmount));
				userCommissionInfoEntity.setPaymentPrincipal(principalAmount);
				userCommissionInfoEntity.setPaymentInterest(interestAmount);
				userCommissionInfoEntity.setFactPaymentAmount(BigDecimal.ZERO);
				userCommissionInfoEntity.setMakeUpAmount(BigDecimal.ZERO);
				userCommissionInfoEntity.setPaymentPrincipalRate(commissionRate);
				userCommissionInfoEntity.setPaymentInterestRate(commissionAwardRate);
				userCommissionInfoEntity.setMakeUpRate(BigDecimal.ZERO);
				userCommissionInfoEntity.setPaymentStatus(Constant.PAYMENT_STATUS_02);
				userCommissionInfoEntity.setTradeNo(numberService.generateCommissionTradeNo());
				userCommissionInfoEntity.setBasicModelProperty(userId, true);
				
				userCommissionInfoEntity = userCommissionInfoRepository.save(userCommissionInfoEntity);
				
				// 立即结算
				Map<String, Object> requestParam = Maps.newConcurrentMap();
				requestParam.put("userCommissionId", userCommissionInfoEntity.getId());
				requestParam.put("userId", userId);
				commissionWithdraw(requestParam);
			}
			else { // 部分转让
				for(int i = pos; i < userCommissionList.size(); i ++) {
					UserCommissionInfoEntity u = userCommissionList.get(i);
					BigDecimal interestAmount = u.getPaymentInterest();
					
					UserCommissionInfoEntity userCommissionInfoEntity = createCommission(sourceInvestInfoEntity.getInvestAmount(), commissionRate, commissionAwardRate, 
							new BigDecimal("30"), u.getCurrentTerm(), u.getCustId(), 
							u.getInvestId(), u.getExceptPaymentDate(), userId,
							remainderScale);

					if(i > 0 && i == pos) { // 若是首个，转让当期原始佣金*奖励利率*(存留期数-1)*30/360
						// 上一个返佣对象
						String beforeExceptPaymentDate = userCommissionList.get(pos - 1).getExceptPaymentDate();
						UserCommissionInfoEntity prevUserCommissionInfoEntity = userCommissionInfoRepository.findLatestMaxByInvestIdAndExceptPaymentDate(sourceInvestInfoEntity.getId(), userCommissionList.get(pos).getExceptPaymentDate(), beforeExceptPaymentDate);
						interestAmount =  cacl(prevUserCommissionInfoEntity.getPaymentPrincipal(), commissionAwardRate, new BigDecimal(pos*30), BigDecimal.ONE);
						userCommissionInfoEntity.setPaymentInterest(interestAmount);
					}

					commissionList.add(userCommissionInfoEntity);	
				}
			}
			userCommissionInfoRepository.save(commissionList);
		}

		return new ResultVo(true, "保存佣金转出成功"); 
	}
	
	/**
	 * 创建单笔佣金
	 *
	 * @author  wangjf
	 * @date    2017年1月12日 下午1:27:53
	 * @param investAmount          投资金额
	 * @param commissionRate        提成比例
	 * @param commissionAwardRate   奖励利率
	 * @param interestDays          计息天数 30天
	 * @param currentTerm           当前期数
	 * @param custManagerId         业务员cust_id
	 * @param investId              投资id
	 * @param exceptPaymentDate     预计返佣日期
	 * @param userId
	 * @param remainderScale        剩余比例
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public UserCommissionInfoEntity createCommission(BigDecimal investAmount, BigDecimal commissionRate, BigDecimal commissionAwardRate,
			BigDecimal interestDays, Integer currentTerm, String custManagerId, 
			String investId, String exceptPaymentDate, String userId,
			BigDecimal remainderScale) {
		// 月标 ---> 佣金=投资金额*提成比例*30*剩余占比/360
        // 天标 ---> 佣金=投资金额*提成比例*期限（天数）/360
        BigDecimal principalAmount = cacl(investAmount, commissionRate, interestDays, remainderScale);
		// 补贴=当期佣金*奖励利率*(存留期数-1)*30/360
		BigDecimal interestAmount = BigDecimal.ZERO;
		if(currentTerm == 1) { // 第一期利息为0
			interestAmount = BigDecimal.ZERO;
		}
		else {
			interestAmount = cacl(principalAmount, commissionAwardRate, new BigDecimal((currentTerm - 2 )*30), BigDecimal.ONE);
			interestAmount = ArithUtil.add(interestAmount, cacl(principalAmount, commissionAwardRate, interestDays, BigDecimal.ONE));
		}

		UserCommissionInfoEntity userCommissionInfoEntity = new UserCommissionInfoEntity();
		userCommissionInfoEntity.setCustId(custManagerId);
		userCommissionInfoEntity.setInvestId(investId);
		userCommissionInfoEntity.setCurrentTerm(currentTerm);// 若从后面几期开始，此处仍从1开始计数
		userCommissionInfoEntity.setExceptPaymentDate(exceptPaymentDate);
		userCommissionInfoEntity.setTotalPaymentAmount(ArithUtil.add(principalAmount, interestAmount));
		userCommissionInfoEntity.setPaymentPrincipal(principalAmount);
		userCommissionInfoEntity.setPaymentInterest(interestAmount);
		userCommissionInfoEntity.setFactPaymentAmount(BigDecimal.ZERO);
		userCommissionInfoEntity.setMakeUpAmount(BigDecimal.ZERO);
		userCommissionInfoEntity.setPaymentPrincipalRate(commissionRate);
		userCommissionInfoEntity.setPaymentInterestRate(commissionAwardRate);
		userCommissionInfoEntity.setMakeUpRate(BigDecimal.ZERO);
		userCommissionInfoEntity.setPaymentStatus(Constant.PAYMENT_STATUS_02);
		userCommissionInfoEntity.setTradeNo(numberService.generateCommissionTradeNo());
		userCommissionInfoEntity.setBasicModelProperty(userId, true);
		
		return userCommissionInfoEntity;
	}

	/**
	 * 计算佣金或利息
	 *
	 * @author  wangjf
	 * @date    2017年1月12日 下午3:47:25
	 * @param source
	 * @param rate
	 * @param days
	 * @param remainderScale
	 * @return
	 */
	public BigDecimal cacl(BigDecimal source, BigDecimal rate, BigDecimal days, BigDecimal remainderScale) {
		
		BigDecimal dest = ArithUtil.div(ArithUtil.mul(ArithUtil.mul(ArithUtil.mul(source, rate), days), remainderScale), new BigDecimal(Constant.DAYS_OF_YEAR));
		return ArithUtil.formatScale2(dest);
	}
	
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo commissionWithdraw(Map<String, Object> params)
			throws SLException {
		
		// 业绩奖励不走线上 update by mali 2017-3-30 11:29:01
		/*String userCommissionId = (String)params.get("userCommissionId");
		String userId = (String)params.get("userId");
		
		// 1) 查询佣金记录
		UserCommissionInfoEntity userCommissionInfoEntity = userCommissionInfoRepository.findOne(userCommissionId);
		if(userCommissionInfoEntity == null) {
			return new ResultVo(false, "业绩奖励方案不存在");
		}
		if(!Constant.PAYMENT_STATUS_02.contains(userCommissionInfoEntity.getPaymentStatus())) {
			return new ResultVo(false, "非未结算状态不能结算");
		}
		
		AccountInfoEntity accountInfoEntity = accountInfoRepository.findByCustId(userCommissionInfoEntity.getCustId());
		if(accountInfoEntity == null) {
			return new ResultVo(false, "业务员账户不存在");
		}
		AccountInfoEntity awardAccount = accountInfoRepository.findByCustId(Constant.CUST_ID_COMMISION);
		if(awardAccount == null) {
			return new ResultVo(false, "公司业绩奖励账户不存在");
		}
		
		// 资金到帐
		BigDecimal tradeAmount = userCommissionInfoEntity.getTotalPaymentAmount();
		String requestNo = numberService.generateTradeBatchNumber();
		custAccountService.updateAccount(awardAccount, null, accountInfoEntity, 
				null, "1", SubjectConstant.TRADE_FLOW_TYPE_COMMISION_AWARD_LOAN, 
				requestNo, tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_AWARD_LOAN, 
				Constant.TABLE_BAO_T_USER_COMMISSION_INFO, userCommissionInfoEntity.getId(),  
				"", userId);
		
		// 修改状态
		userCommissionInfoEntity.setPaymentStatus(Constant.PAYMENT_STATUS_01);
		userCommissionInfoEntity.setFactPaymentAmount(tradeAmount);
		userCommissionInfoEntity.setFactPaymentDate(new Date());
		userCommissionInfoEntity.setBasicModelProperty(userId, false);*/
		
		return new ResultVo(true, "业绩到期奖励成功");
	}
}
