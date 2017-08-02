/** 
 * @(#)TermServiceImpl.java 1.0.0 2015年8月12日 下午1:52:04  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AccountFlowInfoEntity;
import com.slfinance.shanlincaifu.entity.AccountInfoEntity;
import com.slfinance.shanlincaifu.entity.AtoneInfoEntity;
import com.slfinance.shanlincaifu.entity.CustInfoEntity;
import com.slfinance.shanlincaifu.entity.DailyInterestInfoEntity;
import com.slfinance.shanlincaifu.entity.DeviceInfoEntity;
import com.slfinance.shanlincaifu.entity.EmptionInfoEntity;
import com.slfinance.shanlincaifu.entity.InvestDetailInfoEntity;
import com.slfinance.shanlincaifu.entity.InvestInfoEntity;
import com.slfinance.shanlincaifu.entity.LogInfoEntity;
import com.slfinance.shanlincaifu.entity.ProductDetailInfoEntity;
import com.slfinance.shanlincaifu.entity.ProductInfoEntity;
import com.slfinance.shanlincaifu.entity.ProductRateInfoEntity;
import com.slfinance.shanlincaifu.entity.SubAccountInfoEntity;
import com.slfinance.shanlincaifu.repository.AccountFlowInfoRepository;
import com.slfinance.shanlincaifu.repository.AccountInfoRepository;
import com.slfinance.shanlincaifu.repository.ActivityInfoRepository;
import com.slfinance.shanlincaifu.repository.AtoneInfoRepository;
import com.slfinance.shanlincaifu.repository.AuditInfoRespository;
import com.slfinance.shanlincaifu.repository.CustInfoRepository;
import com.slfinance.shanlincaifu.repository.DeviceInfoRepository;
import com.slfinance.shanlincaifu.repository.EmptionInfoRepository;
import com.slfinance.shanlincaifu.repository.ExpandInfoRepository;
import com.slfinance.shanlincaifu.repository.InterfaceDetailInfoRepository;
import com.slfinance.shanlincaifu.repository.InvestDetailInfoRepository;
import com.slfinance.shanlincaifu.repository.InvestInfoRepository;
import com.slfinance.shanlincaifu.repository.LogInfoEntityRepository;
import com.slfinance.shanlincaifu.repository.ProductDetailInfoRepository;
import com.slfinance.shanlincaifu.repository.ProductInfoRepository;
import com.slfinance.shanlincaifu.repository.ProductRateInfoRepository;
import com.slfinance.shanlincaifu.repository.SubAccountInfoRepository;
import com.slfinance.shanlincaifu.repository.SystemMessageInfoRepository;
import com.slfinance.shanlincaifu.repository.custom.AccountInfoRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.AtoneInfoRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.DailySettlementRepository;
import com.slfinance.shanlincaifu.repository.custom.LoanInfoRepositoryCustom;
import com.slfinance.shanlincaifu.service.AccessService;
import com.slfinance.shanlincaifu.service.AccountFlowService;
import com.slfinance.shanlincaifu.service.CustActivityInfoService;
import com.slfinance.shanlincaifu.service.FlowNumberService;
import com.slfinance.shanlincaifu.service.LoanInfoService;
import com.slfinance.shanlincaifu.service.ParamService;
import com.slfinance.shanlincaifu.service.ProductBusinessService;
import com.slfinance.shanlincaifu.service.SMSService;
import com.slfinance.shanlincaifu.service.TermService;
import com.slfinance.shanlincaifu.utils.ArithUtil;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.SharedUtil;
import com.slfinance.shanlincaifu.utils.SubjectConstant;
import com.slfinance.vo.ResultVo;

/**   
 * 定期宝实现类
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年8月12日 下午1:52:04 $ 
 */
@Slf4j
@Service("termService")
public class TermServiceImpl implements TermService {
	
	@Autowired
	private ProductDetailInfoRepository productDetailInfoRepository;
	
	@Autowired
	private CustInfoRepository custInfoRepository;
	
	@Autowired
	private AccountInfoRepository accountInfoRepository;
	
	@Autowired
	private AccountInfoRepositoryCustom accountInfoRepositoryCustom;
	
	@Autowired
	private FlowNumberService numberService;
	
	@Autowired
	private AccountFlowInfoRepository accountFlowInfoRepository;
	
//	@Autowired
//	private AccountFlowDetailRepository accountFlowDetailRepository;
//	
//	@Autowired
//	private FlowBusiRelationRepository flowBusiRelationRepository;
	
	@Autowired
	private ProductInfoRepository productInfoRepository;
	
	@Autowired
	private InvestInfoRepository investInfoRepository;
	
	@Autowired
	private InvestDetailInfoRepository investDetailInfoRepository;
	
	@Autowired
	private SubAccountInfoRepository subAccountInfoRepository;
	
	@Autowired
	private AtoneInfoRepository atoneInfoRepository;
	
	@Autowired
	private AuditInfoRespository auditInfoRespository;
	
	@Autowired
	private EmptionInfoRepository emptionInfoRepository;
	
	@Autowired
	private LoanInfoRepositoryCustom loanInfoRepositoryCustom;
	
	@Autowired
	private AtoneInfoRepositoryCustom atoneInfoRepositoryCustom;
	
	@Autowired
	private ParamService paramService;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private SystemMessageInfoRepository systemMessageInfoRepository;
	
	@Autowired
	private LogInfoEntityRepository logInfoEntityRepository;
	
	@Autowired
	private ProductRateInfoRepository productRateInfoRepository;	
	
	@Autowired
	private AccountFlowService accountFlowService;
	
	@Autowired
	private DailySettlementRepository dailySettlementRepository;
	
	@Autowired
	private LoanInfoService loanInfoService;
	
	@Autowired
	private CustActivityInfoService custActivityInfoService;
	
	@Autowired
	private ActivityInfoRepository activityInfoRepository;
	
	@Autowired
	private DeviceInfoRepository deviceInfoRepository;
	
	@Autowired
	private InterfaceDetailInfoRepository interfaceDetailInfoRepository;
	
	@Autowired
	private ExpandInfoRepository expandInfoRepository;
	
	@Autowired
	private AccessService accessService;
	
	@Autowired
	private ProductBusinessService productBusinessService;
	
	@Autowired
	private SMSService sMSService;

	@Override
	public Map<String, Object> findTermBAODetail(Map<String, Object> params) {
		Map<String,Object> rtnMap=new HashMap<>();
		String productId = (String)params.get("productId");
		if(!StringUtils.isEmpty(productId)) {
			ProductDetailInfoEntity pdi = productDetailInfoRepository.findByProductId(productId);
			ProductInfoEntity pie = productInfoRepository.findOne(productId);
			if(pdi==null){
				log.info("产品为空！{}", productId);
				rtnMap.put("partakeOrganizs", "");
				rtnMap.put("partakeCrerigs", "");
				rtnMap.put("ensureMethod", "");
				rtnMap.put("alreadyInvestPeople", "");
				rtnMap.put("alreadyInvestAmount", "");
				rtnMap.put("accumulativeLucre", "");
				return rtnMap;
			}else{
				rtnMap.put("currUsableValue", pdi.getCurrUsableValue());
			}
			rtnMap.putAll(productBusinessService.findPartakeForDisplay(Constant.PRODUCT_TYPE_04));
			//rtnMap.put("partakeOrganizs", pdi.getPartakeOrganizs());
			//rtnMap.put("partakeCrerigs", pdi.getPartakeCrerigs());
			rtnMap.put("ensureMethod", Constant.ENSURE_METHOD_01);
			rtnMap.put("alreadyInvestPeople", pdi.getAlreadyInvestPeople());
			rtnMap.put("alreadyInvestAmount", pdi.getAlreadyInvestAmount());
			rtnMap.put("accumulativeLucre", pdi.getAccumulativeLucre());
			
			rtnMap.put("productInfo", pie);
			//BAO_T_PRODUCT_RATE_INFO BAO产品利率信息
			List<ProductRateInfoEntity> prodList=
					this.productRateInfoRepository.findProductRateInfoByProductId(productId);
			if(prodList==null){
				log.info("产品利率信息为空或者小于2");
				rtnMap.put("minYearRate", "0");
				rtnMap.put("maxYearRate", "0");
				
				rtnMap.put("minAwardRate", "0");
				rtnMap.put("maxAwardRate", "0");
			}else{
				//年利率
				BigDecimal minYearRate=prodList.get(0).getYearRate();
				//奖励
				BigDecimal minAwardRate=prodList.get(0).getAwardRate();
				rtnMap.put("minYearRate", minYearRate);
				rtnMap.put("minAwardRate", minAwardRate);
				

				if(prodList.size()>1){
					
					BigDecimal maxYearRate=prodList.get(prodList.size()-1).getYearRate();
					BigDecimal maxAwardRate=prodList.get(prodList.size()-1).getAwardRate();
					rtnMap.put("maxYearRate", maxYearRate);
					rtnMap.put("maxAwardRate", maxAwardRate);
				}
			}
			
			//投资笔数
			int investCount = investDetailInfoRepository.findInvestCount(Constant.PRODUCT_TYPE_04);
		    rtnMap.put("investCount", investCount);
		    
			//累计用户赚取
			BigDecimal incomeAmount=this.accountFlowInfoRepository.sumTradeAmountByTradeType("定期宝收益");
			rtnMap.put("incomeAmount", incomeAmount==null?BigDecimal.ZERO:incomeAmount);
			
			if(params.get("custId")!=null){
				//已登录用户
				
				//个人可投资金额
				BigDecimal mda=paramService.findMaxWithdrawAmount();
				
				//用户持有份额
				BigDecimal ownerAmount = subAccountInfoRepository.queryUserTotalValue((String)params.get("custId"), productId, Constant.TERM_INVEST_STATUS_EARN, pdi.getCurrTerm());
				ownerAmount = (ownerAmount == null ? BigDecimal.ZERO : ownerAmount);
				log.info("个人可投资金额:"+mda+",用户持有份额:"+ownerAmount+",产品剩余可投金额:"+pdi.getCurrUsableValue());
				//用户剩余可购份额：(50w-用户持有份额)与某产品剩余可购买份额，比较取小，若(50w-用户持有份额)<0，则取0，（50w-用户持有份额，截取）
				BigDecimal _amount=mda.subtract(ownerAmount);
				
				
				if(_amount.compareTo(BigDecimal.ZERO)<0){
					rtnMap.put("userAllowAmount", "0");
				}else if(_amount.compareTo(pdi.getCurrUsableValue())>0){
					//>
					rtnMap.put("userAllowAmount", pdi.getCurrUsableValue());
				}else{
					rtnMap.put("userAllowAmount", _amount);
				}
				
			}
		}
		else {
			// 取所有定期宝产品
			List<ProductDetailInfoEntity> productList = productDetailInfoRepository.findTermProductDetailInfoByProductName(Constant.PRODUCT_TYPE_04);
			if(productList != null && productList.size() > 0) {
				ProductDetailInfoEntity pdi = productList.get(0);
				
				rtnMap.putAll(productBusinessService.findPartakeForDisplay(Constant.PRODUCT_TYPE_04));
				//rtnMap.put("partakeOrganizs", pdi.getPartakeOrganizs());
				//rtnMap.put("partakeCrerigs", pdi.getPartakeCrerigs());
				rtnMap.put("ensureMethod", Constant.ENSURE_METHOD_01);
				
				BigDecimal alreadyInvestPeople = new BigDecimal("0");
				BigDecimal alreadyInvestAmount = new BigDecimal("0");
				BigDecimal accumulativeLucre = new BigDecimal("0");
				for(ProductDetailInfoEntity p : productList) {
					alreadyInvestPeople = ArithUtil.add(alreadyInvestPeople, p.getAlreadyInvestPeople());
					alreadyInvestAmount = ArithUtil.add(alreadyInvestAmount, p.getAlreadyInvestAmount());
					accumulativeLucre = ArithUtil.add(accumulativeLucre, pdi.getAccumulativeLucre());
				}
				rtnMap.put("alreadyInvestPeople", alreadyInvestPeople);
				rtnMap.put("alreadyInvestAmount", alreadyInvestAmount);
				rtnMap.put("accumulativeLucre", accumulativeLucre);
			}
			else {
				rtnMap.put("partakeOrganizs", "");
				rtnMap.put("partakeCrerigs", "");
				rtnMap.put("ensureMethod", "");
				rtnMap.put("alreadyInvestPeople", "");
				rtnMap.put("alreadyInvestAmount", "");
				rtnMap.put("accumulativeLucre", "");
			}
			
			//投资笔数
			int investCount = investDetailInfoRepository.findInvestCount(Constant.PRODUCT_TYPE_04);
		    rtnMap.put("investCount", investCount);
		    
			//累计用户赚取
			BigDecimal incomeAmount=this.accountFlowInfoRepository.sumTradeAmountByTradeType(SubjectConstant.TRADE_FLOW_TYPE_04);
			rtnMap.put("incomeAmount", incomeAmount==null?BigDecimal.ZERO:incomeAmount);
		}
		
		return rtnMap;
	}
	
	/* 
	 * 购买定期宝
	 * @see com.slfinance.shanlinbao.service.TermService#joinTermBao(java.util.Map)
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo joinTermBao(Map<String, Object> params) throws SLException {
		String custId = (String)params.get("custId");
		BigDecimal tradeAmount = new BigDecimal((String)params.get("tradeAmount"));
		String productId = (String)params.get("productId");
		String appSource = (String)params.get("appSource"); // 来源
		appSource = Strings.nullToEmpty(StringUtils.isEmpty(appSource) ? Constant.INVEST_SOURCE_PC : appSource).toLowerCase();
		
		// 检查是否是ios设备，若是ios设备且缺少应用版本号则不允许购买
		ResultVo resultVo = accessService.checkAppVersion(params);
		if(!ResultVo.isSuccess(resultVo)) {
			return resultVo;
		}

		// 1、业务校验
		// 1.0 验证用户状态
		CustInfoEntity custInfoEntity = custInfoRepository.findOne(custId);
		if(custInfoEntity == null){
			throw new SLException("用户不存在！");
		}
		if(!Constant.ENABLE_STATUS_01.equals(custInfoEntity.getEnableStatus())){
			throw new SLException("账户为非正常状态，可能被冻结，请联系客服！");
		}
				
		// 1.1  客户账户校验： 验证金额是否足够
		AccountInfoEntity accountInfoEntity = accountInfoRepository.findByCustId(custId);
		if(accountInfoEntity == null) {
			throw new SLException("账户不存在");
		}
		if(accountInfoEntity.getAccountAvailableAmount().compareTo(tradeAmount) < 0){
			throw new SLException(String.format("账户可用余额%s小于加入金额%s", accountInfoEntity.getAccountAvailableAmount().toString(), tradeAmount.toString()));
		}

		// 1.2 加入金额验证 ：起投金额   上限
		ProductInfoEntity productInfoEntity = productInfoRepository.findOne(productId);
		if(productInfoEntity == null) {
			throw new SLException("产品不存在");
		}
		if(productInfoEntity.getInvestMinAmount().compareTo(tradeAmount) > 0 || productInfoEntity.getInvestMaxAmount().compareTo(tradeAmount) < 0){
			throw new SLException(String.format("加入金额不能大于上限%s，且不能小于下限%s", productInfoEntity.getInvestMaxAmount().toString(), productInfoEntity.getInvestMinAmount().toString()));
		}
		
		// 1.3 验证用户投资金额是否为整数倍递增
		BigDecimal investIncrase = ArithUtil.sub(tradeAmount, productInfoEntity.getInvestMinAmount());
		if(investIncrase.intValue() % productInfoEntity.getIncreaseAmount().intValue() != 0){
			throw new SLException(String.format("递增金额必须为%s的整数倍", productInfoEntity.getIncreaseAmount().toString()));
		}
		
		// 1.4 可投金额验证：判断客户此次投资是否超过剩余可投金额
		ProductDetailInfoEntity productDetailInfoEntity = productDetailInfoRepository.findByProductId(productInfoEntity.getId());
		if(productDetailInfoEntity == null) {
			throw new SLException("产品详情不存在");
		}
		if(productDetailInfoEntity.getCurrUsableValue().compareTo(tradeAmount) < 0){
			throw new SLException(String.format("加入金额为%s，实际剩余可加入金额为%s", tradeAmount.toString(), ArithUtil.formatScale2(productDetailInfoEntity.getCurrUsableValue()).toString()));//update by zhangzs 显示金额截取
		}
		
		// 1.5 验证定期宝产品状态为:开放中
		if(!productInfoEntity.getProductStatus().equals(Constant.PRODUCT_STATUS_BID_ING)){
			throw new SLException("产品状态非开放中，不能加入！");
		}
		
//		// 1.6 验证总投资金额是否大于个人投资上限
//		BigDecimal investAmounts = subAccountInfoRepository.queryUserTotalValue(custId, productInfoEntity.getId(), Constant.TERM_INVEST_STATUS_EARN, productDetailInfoEntity.getCurrTerm());
//		BigDecimal maxWithdrawAmount = paramService.findMaxInvestAmount();
//		if(ArithUtil.add(investAmounts, tradeAmount).compareTo(maxWithdrawAmount) > 0){
//			throw new SLException(String.format("加入金额为%s，个人累积最大可加入金额为%s, 实际剩余可加入金额为%s", tradeAmount.toString(), maxWithdrawAmount.toString(), ArithUtil.sub(maxWithdrawAmount, investAmounts).toString()));
//		}

		// 2、验证用户定期宝是否开户
		BigDecimal countInvestTimes = investInfoRepository.countInvestInfoByCustId(custId, productInfoEntity.getId());
		
		// 3、更新定期宝募集表
		productDetailInfoEntity.setCurrUsableValue(ArithUtil.sub(productDetailInfoEntity.getCurrUsableValue(), tradeAmount));
		productDetailInfoEntity.setAlreadyInvestAmount(ArithUtil.add(productDetailInfoEntity.getAlreadyInvestAmount(), tradeAmount));
		productDetailInfoEntity.setBasicModelProperty(custId, false);
		if(countInvestTimes.compareTo(new BigDecimal("0")) == 0){ //投资次数为0表示新用户需将参与人数加1
			productDetailInfoEntity.setAlreadyInvestPeople(ArithUtil.add(productDetailInfoEntity.getAlreadyInvestPeople(), new BigDecimal("1")));
		}
		if(productDetailInfoEntity.getCurrUsableValue().compareTo(new BigDecimal("0")) == 0) { // 可开放价值变为0，将状态置为满标中
			productInfoEntity.setProductStatus(Constant.PRODUCT_STATUS_BID_FINISH);
		}
		
		// 4、新增投资记录
		String investDate = DateUtils.formatDate(new Date(), "yyyyMMdd");
		String expireDate = DateUtils.formatDate(DateUtils.getAfterMonthNext(new Date(), productInfoEntity.getTypeTerm().intValue()), "yyyyMMdd");
		InvestInfoEntity newInvestInfoEntity = null;
		SubAccountInfoEntity subAccountInfoEntity = null;
		String tradeCode = numberService.generateLoanContractNumber();
		if(countInvestTimes.compareTo(new BigDecimal("0")) == 0){// 投资次数为0为新投资，直接新增投资记录
			newInvestInfoEntity = saveInvest(null, custId, productInfoEntity.getId(), tradeAmount, investDate, "", tradeCode, true, appSource);
			subAccountInfoEntity = saveSubAccountInfo(custId, accountInfoEntity.getId(), newInvestInfoEntity.getId());
			newInvestInfoEntity.setExpireDate(expireDate);
			newInvestInfoEntity.setCurrTerm(productDetailInfoEntity.getCurrTerm());
		}
		else { // 投资次数不为0，需判断之前有没有投资，有则新加详情，否则新建一笔投资
			InvestInfoEntity investInfoEntity = investInfoRepository.findInvestInfoByCustIdAndInvestDate(custId, productInfoEntity.getId(), investDate, Constant.TERM_INVEST_STATUS_EARN);
			if(investInfoEntity == null){
				newInvestInfoEntity = saveInvest(null, custId, productInfoEntity.getId(), tradeAmount, investDate, "", tradeCode, true, appSource);
				subAccountInfoEntity = saveSubAccountInfo(custId, accountInfoEntity.getId(), newInvestInfoEntity.getId());
				newInvestInfoEntity.setExpireDate(expireDate);
				newInvestInfoEntity.setCurrTerm(productDetailInfoEntity.getCurrTerm());
			} 
			else {
				newInvestInfoEntity = saveInvest(investInfoEntity, custId, productInfoEntity.getId(), tradeAmount, investDate, "", tradeCode, false, appSource);
				subAccountInfoEntity = subAccountInfoRepository.findByRelatePrimary(newInvestInfoEntity.getId());
			}
		}
		
		// 6、更新用户主账户及记录流水（主账户——>分账户）
		String reqeustNo = numberService.generateTradeBatchNumber();
		// 6.1  更新用户主账户(主账户现金转出)
		accountInfoEntity.setAccountTotalAmount(ArithUtil.sub(accountInfoEntity.getAccountTotalAmount(), tradeAmount));
		accountInfoEntity.setAccountAvailableAmount(ArithUtil.sub(accountInfoEntity.getAccountAvailableAmount(), tradeAmount));
		accountInfoEntity.setBasicModelProperty(custId, false);	
		AccountFlowInfoEntity accountFlowInfoEntity = accountFlowService.saveAccountFlow(accountInfoEntity, subAccountInfoEntity, accountInfoEntity, subAccountInfoEntity, "2", 
				SubjectConstant.TRADE_FLOW_TYPE_JOIN_TERM, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, 
				tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_JOIN_TERM, 
				subAccountInfoEntity.getRelateType(), subAccountInfoEntity.getRelatePrimary(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
		accountFlowInfoEntity.setMemo(String.format("%s%s期", productInfoEntity.getProductName(), productDetailInfoEntity.getCurrTerm()));
		
		// 6.2 更新用户分账户(分账户现金转入)
		subAccountInfoEntity.setAccountAmount(ArithUtil.add(subAccountInfoEntity.getAccountAmount(), tradeAmount));
		accountFlowService.saveAccountFlow(accountInfoEntity, subAccountInfoEntity, accountInfoEntity, subAccountInfoEntity, "3", 
				SubjectConstant.TRADE_FLOW_TYPE_JOIN_TERM, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING, 
				tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_JOIN_TERM, 
				subAccountInfoEntity.getRelateType(), subAccountInfoEntity.getRelatePrimary(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
		
		// 7、由公司购买定期宝（规则如下：先使用赎回中的金额，再使用收益分账户金额，再使用居间人账户金额）
		// 查询赎回中金额
		BigDecimal atoneAmount = subAccountInfoRepository.sumTermAtone(Constant.PRODUCT_TYPE_04, Constant.TRADE_STATUS_01);
		
		SubAccountInfoEntity earnSubAccount = subAccountInfoRepository.findBySubAccountNo(Constant.SUB_ACCOUNT_NO_ERAN_12); // 公司收益分账户
		if(earnSubAccount == null || earnSubAccount.getAccountAvailableValue() == null) {
			throw new SLException("未初始化公司收益分账户！");
		}
		SubAccountInfoEntity centerSubAccount = subAccountInfoRepository.findBySubAccountNo(Constant.SUB_ACCOUNT_NO_CENTER_11); // 公司居间人分账户
		if(centerSubAccount == null || centerSubAccount.getAccountAvailableValue() == null) {
			throw new SLException("未初始化公司居间人分账户！");
		}
		AccountInfoEntity earnMainAccount = accountInfoRepository.findOne(earnSubAccount.getAccountId()); // 公司收益主账户
		if(earnMainAccount == null) {
			throw new SLException("未初始化公司收益主账户！");
		}
		AccountInfoEntity centerMainAccount = accountInfoRepository.findOne(centerSubAccount.getAccountId()); // 公司居间人主账户
		if(centerMainAccount == null) {
			throw new SLException("未初始化公司居间人主账户！");
		}
		
		BigDecimal transferAmount = tradeAmount;
		if(atoneAmount != null && atoneAmount.compareTo(new BigDecimal("0")) > 0) { // 从赎回中记录购买
			
			// 查询赎回记录
			List<SubAccountInfoEntity> atoneList = subAccountInfoRepository.queryTermAtone(Constant.PRODUCT_TYPE_04, Constant.TRADE_STATUS_01, new Date());
			
			for(SubAccountInfoEntity s: atoneList){
				
				BigDecimal subAmount = s.getAccountFreezeValue().compareTo(transferAmount) > 0 ? transferAmount : s.getAccountFreezeValue();
				
				// a.更新赎回分账户
				saveAtoneAccount(accountInfoEntity, subAccountInfoEntity, null, s, subAmount, reqeustNo);
				
				// b.更新用户分账户(现金——>价值)
				saveJoinUserAccount(accountInfoEntity, subAccountInfoEntity, null, s, subAmount, reqeustNo);
				
				// c.剩余投资金额
				transferAmount = ArithUtil.sub(transferAmount, subAmount);
				
				if(transferAmount.compareTo(new BigDecimal("0")) == 0) {
					break;
				}
			}
		}
		
		if(transferAmount.compareTo(new BigDecimal("0")) > 0) { // 剩余购买金额大于0，继续从收益账户购买
			if(earnSubAccount.getAccountAvailableValue().compareTo(new BigDecimal("0")) > 0){ // 从收益账户购买
				
				// 应从收益账户购买金额
				BigDecimal earnCanJoinAmount = earnSubAccount.getAccountAvailableValue().compareTo(transferAmount) > 0 ? transferAmount : earnSubAccount.getAccountAvailableValue();
				
				// a.更新公司收益分账户(价值——>现金)
				saveCenterAccount(accountInfoEntity, subAccountInfoEntity, earnMainAccount, earnSubAccount, custId, reqeustNo, earnCanJoinAmount);
				
				// b.更新用户分账户(现金——>价值)
				saveJoinUserAccount(accountInfoEntity, subAccountInfoEntity, earnMainAccount, earnSubAccount, earnCanJoinAmount, reqeustNo);
				
				// c.剩余投资金额
				transferAmount = ArithUtil.sub(transferAmount, earnCanJoinAmount);
			}
		}
			
		if(transferAmount.compareTo(new BigDecimal("0")) > 0) { // 剩余购买金额大于0，继续从居间人账户购买
			if(centerSubAccount.getAccountAvailableValue().compareTo(new BigDecimal("0")) > 0){ // 从居间人账户购买
				
				if(centerSubAccount.getAccountAvailableValue().compareTo(transferAmount) >= 0){// 若居间人账户足额
					
					// a.更新公司居间人分账户(价值——>现金)
					saveCenterAccount(accountInfoEntity, subAccountInfoEntity, centerMainAccount, centerSubAccount, custId, reqeustNo, transferAmount);
					
					// b.更新用户分账户(现金——>价值)
					saveJoinUserAccount(accountInfoEntity, subAccountInfoEntity, centerMainAccount, centerSubAccount, transferAmount, reqeustNo);
					
				} 
				else {// 若居间人账户不足额，则抛出异常，回滚之前所有操作
					log.error("用户{}购买产品{}，投资金额{}，购买失败！", custId, productInfoEntity.getProductName(), tradeAmount);
					throw new SLException(String.format("购买%s失败！系统可购买份额不足！", productInfoEntity.getProductName()));
				}
			}
		}
		
		// 8、记录日志
		LogInfoEntity logInfoEntity = new LogInfoEntity();
		logInfoEntity.setRelateType(Constant.TABLE_BAO_T_INVEST_INFO);
		logInfoEntity.setRelatePrimary(newInvestInfoEntity.getId());
		logInfoEntity.setLogType(Constant.OPERATION_TYPE_21);
		logInfoEntity.setOperDesc("");
		logInfoEntity.setOperPerson(custId);
		logInfoEntity.setOperIpaddress((String)params.get("ipAddress"));
		logInfoEntity.setMemo(String.format("%s购买%s，投资金额%s", custInfoEntity.getLoginName(), productInfoEntity.getProductName(), tradeAmount.toString()));
		logInfoEntity.setBasicModelProperty(custId, true);
		logInfoEntityRepository.save(logInfoEntity);
		
		// 9、记录设备信息
		DeviceInfoEntity deviceInfoEntity = null;
		if(params.containsKey("channelNo")) {
			deviceInfoEntity = new DeviceInfoEntity();
			deviceInfoEntity.setRelateTableIdentification(Constant.TABLE_BAO_T_INVEST_INFO);
			deviceInfoEntity.setRelatePrimary(newInvestInfoEntity.getId());
			deviceInfoEntity.setCustId(custId);
			deviceInfoEntity.setTradeType(Constant.OPERATION_TYPE_21);
			deviceInfoEntity.setMeId((String)params.get("meId"));
			deviceInfoEntity.setMeVersion((String)params.get("meVersion"));
			deviceInfoEntity.setAppSource(appSource);
			deviceInfoEntity.setChannelNo(paramService.findByChannelNo((String)params.get("channelNo")));
			deviceInfoEntity.setBasicModelProperty(custId, true);
			deviceInfoRepository.save(deviceInfoEntity);
		}
		
		// 10、活动4 购买定期宝送体验金
		Map<String, Object> custActivityMap = new HashMap<String, Object>();
		custActivityMap.put("activityId", Constant.ACTIVITY_ID_REGIST_04);
		custActivityMap.put("custId", custId);
		custActivityMap.put("custAccount", accountInfoEntity);
		custActivityMap.put("tradeAmount", tradeAmount);
		custActivityMap.put("tradeNo", numberService.generateTradeNumber());
		custActivityInfoService.custActivityRecommend(custActivityMap);
		
		// 11、活动5 购买定期宝送流量
		custActivityMap.put("activityId", Constant.ACTIVITY_ID_REGIST_05);
		custActivityMap.put("custId", custId);
		custActivityMap.put("investId", newInvestInfoEntity.getId());
		custActivityMap.put("deviceInfoEntity", deviceInfoEntity);
		custActivityMap.put("tradeNo", numberService.generateTradeNumber());
		custActivityMap.put("channelNo", (String)params.get("channelNo"));
		custActivityInfoService.custActivityRecommend(custActivityMap);
		
		// 12、活动7 购买定期宝推荐有奖送体验金
		custActivityMap.put("activityId", Constant.ACTIVITY_ID_REGIST_07);
		custActivityMap.put("custInfoEntity", custInfoEntity);
		custActivityMap.put("custAccount", accountInfoEntity);
		custActivityMap.put("tradeNo", numberService.generateTradeNumber());
		custActivityInfoService.custActivityRecommend(custActivityMap);
		
		return new ResultVo(true);
	}
	
	/* 
	 * 提前赎回申请
	 * @see com.slfinance.shanlinbao.service.TermService#termWithdrawApply(java.util.Map)
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo termWithdrawApply(Map<String, Object> params)
			throws SLException {
		String custId = (String)params.get("custId");
		String investId = (String)params.get("investId");
		String tradePassword = (String)params.get("tradePassword");
		String appSource = (String)params.get("appSource"); // 来源
		appSource = Strings.nullToEmpty(StringUtils.isEmpty(appSource) ? Constant.INVEST_SOURCE_PC : appSource).toLowerCase();
		
		// 1.0 验证用户状态
		CustInfoEntity custInfoEntity = custInfoRepository.findOne(custId);
		if(custInfoEntity == null){
			throw new SLException("用户不存在！");
		}
		if(!Constant.ENABLE_STATUS_01.equals(custInfoEntity.getEnableStatus())){
			throw new SLException("账户为非正常状态，可能被冻结，请联系客服！");
		}
		
		// 1.1 验证交易密码
		if(!tradePassword.equals(custInfoEntity.getTradePassword())){
			throw new SLException("交易密码不正确");
		}
		AccountInfoEntity accountInfoEntity = accountInfoRepository.findByCustId(custId);
		if(accountInfoEntity == null) {
			throw new SLException("账户不存在");
		}
		
		// 查询投资记录
		InvestInfoEntity investInfoEntity = investInfoRepository.findOne(investId);
		if(investInfoEntity == null) {
			throw new SLException("该笔投资不存在");
		}
		// 更新投资状态
		investInfoEntity.setInvestStatus(Constant.TERM_INVEST_STATUS_ADVANCE);
		
		// 查询产品记录
		ProductInfoEntity productInfoEntity = productInfoRepository.findOne(investInfoEntity.getProductId());
		
		// 计算投资天数
		Date now = new Date();
		int investDay = DateUtils.datePhaseDiffer(DateUtils.parseDate(investInfoEntity.getInvestDate(), "yyyyMMdd"), now);
		
		// 判断投资天数是否大于封闭期（封闭月数*30）
		if(productInfoEntity.getSeatTerm() == null) {
			throw new SLException("该产品不允许提前赎回!");
		}
		Date seatDate = DateUtils.getAfterMonthNext(DateUtils.parseDate(investInfoEntity.getInvestDate(), "yyyyMMdd"), productInfoEntity.getSeatTerm().intValue());
		if(now.compareTo(seatDate) <= 0) {
			throw new SLException("该笔投资在封闭期内，不允许赎回！");
		}
		
		ProductRateInfoEntity productRateInfoEntity = null;
		List<ProductRateInfoEntity> rateList = productRateInfoRepository.findProductRateInfoByProductId(investInfoEntity.getProductId());
		for(ProductRateInfoEntity rate : rateList){
//			if(rate.getLowerLimitDay() <= investDay && investDay <= rate.getUpperLimitDay()) {
//				productRateInfoEntity = rate;
//				break;
//			}
			productRateInfoEntity = rate;
		}
		if(productRateInfoEntity == null) {
			throw new SLException("未找到该产品合适的利率");
		}
		
		AtoneInfoEntity existsAtone = atoneInfoRepository.findByInvestId(investInfoEntity.getId());
		if(existsAtone != null) {
			throw new SLException("该笔投资已经赎回，请勿重复赎回");
		}
		
		// 预期收益：投资金额*年利率/365*实际天数
		// 手续费：投资金额*30%
		BigDecimal exceptInterest = ArithUtil.div(ArithUtil.mul(ArithUtil.mul(investInfoEntity.getInvestAmount(), productRateInfoEntity.getYearRate()), new BigDecimal(String.valueOf(investDay))), new BigDecimal("365"));
		BigDecimal manageExpenses = ArithUtil.mul(investInfoEntity.getInvestAmount(), productInfoEntity.getQuitRate());
		BigDecimal factInterest = ArithUtil.sub(exceptInterest, manageExpenses);
		BigDecimal factIncome = ArithUtil.add(investInfoEntity.getInvestAmount(), factInterest);
		
		// 精确到小数点后八位
		manageExpenses = ArithUtil.formatScale(manageExpenses, 8);
		factInterest = ArithUtil.formatScale(factInterest, 8);
		factIncome = ArithUtil.formatScale(factIncome, 8);
		
		// 2 生成赎回订单表(赎回债权详情表由定时任务处理)
		AtoneInfoEntity atoneInfoEntity = new AtoneInfoEntity();
		atoneInfoEntity.setCustId(custId);
		atoneInfoEntity.setInvestId(investId);
		atoneInfoEntity.setProductId(investInfoEntity.getProductId());
		atoneInfoEntity.setOperType("");
		atoneInfoEntity.setTradeCode(numberService.generateTradeNumber());
		atoneInfoEntity.setCleanupDate(new Date());
		atoneInfoEntity.setAtoneMethod(Constant.ATONE_METHOD_ADVANCE);
		atoneInfoEntity.setAtoneExpenses(manageExpenses);
		atoneInfoEntity.setAtoneStatus(Constant.TRADE_STATUS_01);
		atoneInfoEntity.setAuditStatus(Constant.AUDIT_STATUS_PASS);
		atoneInfoEntity.setAtoneTotalAmount(factIncome);
		atoneInfoEntity.setAlreadyAtoneAmount(factIncome);
		atoneInfoEntity.setTradeSource(appSource);
		atoneInfoEntity.setBasicModelProperty(custId, true);
		atoneInfoEntity = atoneInfoRepository.save(atoneInfoEntity);
		
		// 3.记录日志
		LogInfoEntity logInfoEntity = new LogInfoEntity();
		logInfoEntity.setRelateType(Constant.TABLE_BAO_T_ATONE_INFO);
		logInfoEntity.setRelatePrimary(atoneInfoEntity.getId());
		logInfoEntity.setLogType(Constant.OPERATION_TYPE_22);
		logInfoEntity.setOperDesc("");
		logInfoEntity.setOperPerson(custId);
		logInfoEntity.setOperIpaddress((String)params.get("ipAddress"));
		logInfoEntity.setMemo(String.format("%s赎回%s，赎回金额(已扣手续费)%s，手续费%s", custInfoEntity.getLoginName(), productInfoEntity.getProductName(), factIncome.toString(), manageExpenses.toString()));
		logInfoEntity.setBasicModelProperty(custId, true);
		logInfoEntityRepository.save(logInfoEntity);
		
		if(params.containsKey("channelNo")) {
			DeviceInfoEntity deviceInfoEntity = new DeviceInfoEntity();
			deviceInfoEntity.setRelateTableIdentification(Constant.TABLE_BAO_T_ATONE_INFO);
			deviceInfoEntity.setRelatePrimary(atoneInfoEntity.getId());
			deviceInfoEntity.setCustId(custId);
			deviceInfoEntity.setTradeType(Constant.OPERATION_TYPE_22);
			deviceInfoEntity.setMeId((String)params.get("meId"));
			deviceInfoEntity.setMeVersion((String)params.get("meVersion"));
			deviceInfoEntity.setAppSource(appSource);
			deviceInfoEntity.setChannelNo(paramService.findByChannelNo((String)params.get("channelNo")));
			deviceInfoEntity.setBasicModelProperty(custId, true);
			deviceInfoRepository.save(deviceInfoEntity);
		}
		
		// 4.冻结用户价值
		String reqeustNo = numberService.generateTradeBatchNumber();
		SubAccountInfoEntity subAccount = subAccountInfoRepository.findByRelatePrimary(investInfoEntity.getId());
		freezeUserAccount(accountInfoEntity, subAccount, accountInfoEntity, subAccount, subAccount.getAccountAvailableValue(), reqeustNo, atoneInfoEntity, SubjectConstant.TRADE_FLOW_TYPE_ATONE_TERM_FREEZE, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_TERM_FREEZE);
			
		// 5.发短信和站内信
		Map<String, Object> smsParams = new HashMap<String, Object>();
		smsParams.put("mobile", custInfoEntity.getMobile());
		smsParams.put("custId", custInfoEntity.getId());
		smsParams.put("messageType", Constant.SMS_TYPE_ADVANCED_ATONE_APPLY);
		smsParams.put("values", new Object[] {
				ArithUtil.formatScale(factIncome, 2).toPlainString(),
				ArithUtil.formatScale(investInfoEntity.getInvestAmount(), 2).toPlainString(),
				ArithUtil.formatScale(manageExpenses, 2).toPlainString()});
		smsParams.put("systemMessage", new Object[] {
				ArithUtil.formatScale(factIncome, 2).toPlainString(),
				ArithUtil.formatScale(investInfoEntity.getInvestAmount(), 2).toPlainString(),
				ArithUtil.formatScale(manageExpenses, 2).toPlainString()});
		sMSService.asnySendSMSAndSystemMessage(smsParams);
		
		return new ResultVo(true);

	}
	
	/* 
	 * 到期赎回
	 * @see com.slfinance.shanlinbao.service.TermService#termAtoneWithdraw(java.util.Map)
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo termAtoneWithdraw(Map<String, Object> params)
			throws SLException {
		
		Date now = new Date();
		List<InvestInfoEntity> investList = investInfoRepository.queryCanTermAtoneInvest(Constant.TERM_INVEST_STATUS_EARN, now, Constant.PRODUCT_TYPE_04);
		List<ProductRateInfoEntity> rateList = productRateInfoRepository.findProductRateInfoByTypeName(Constant.PRODUCT_TYPE_04);
		List<ProductInfoEntity> productList = productInfoRepository.findTermProductInfoByProductTypeName(Constant.PRODUCT_TYPE_04);
		for(InvestInfoEntity invest : investList){
			
			invest.setInvestStatus(Constant.TERM_INVEST_STATUS_WAIT);
			
			// 1.0 验证用户状态
			CustInfoEntity custInfoEntity = custInfoRepository.findOne(invest.getCustId());
			if(custInfoEntity == null){
				log.error("用户{}不存在", invest.getCustId());
				continue;
			}
			if(!Constant.ENABLE_STATUS_01.equals(custInfoEntity.getEnableStatus())){
				log.error("用户{}为非正常状态", invest.getCustId());
				continue;
			}
			
			AccountInfoEntity accountInfoEntity = accountInfoRepository.findByCustId(invest.getCustId());
			if(accountInfoEntity == null) {
				log.error("用户{}账户不存在", invest.getCustId());
				continue;
			}
			
			// 找出产品利率
			ProductRateInfoEntity productRateInfoEntity = null;
			for(ProductRateInfoEntity rate : rateList){
				if(rate.getProductId().equals(invest.getProductId())) {
					productRateInfoEntity = rate;
				}
			}
			if(productRateInfoEntity == null) {
				log.error("投资{}未找到产品利率", invest.getId());
				continue;
			}
			
			// 找出产品信息
			ProductInfoEntity productInfoEntity = null;
			for(ProductInfoEntity product : productList){
				if(product.getId().equals(invest.getProductId())){
					productInfoEntity = product;
					break;
				}
			}
			if(productInfoEntity == null) {
				log.error("投资{}未找到产品信息", invest.getId());
				continue;
			}
			
			// 计算预期收益
			// 预期收益：投资金额*年利率/12*月数
			// 奖励收益：投资金额*奖励利率/12*月数
			BigDecimal exceptInterest = ArithUtil.div(ArithUtil.mul(ArithUtil.mul(invest.getInvestAmount(), productRateInfoEntity.getYearRate()), productInfoEntity.getTypeTerm()), new BigDecimal("12"));
			BigDecimal awardInterest = ArithUtil.div(ArithUtil.mul(ArithUtil.mul(invest.getInvestAmount(), productRateInfoEntity.getAwardRate()), productInfoEntity.getTypeTerm()), new BigDecimal("12"));
			BigDecimal factInterest = ArithUtil.add(exceptInterest, awardInterest);
			BigDecimal factIncome = ArithUtil.add(invest.getInvestAmount(), factInterest);
			
			exceptInterest = ArithUtil.formatScale(exceptInterest, 8);
			awardInterest = ArithUtil.formatScale(awardInterest, 8);
			factInterest = ArithUtil.formatScale(factInterest, 8);
			factIncome = ArithUtil.formatScale(factIncome, 8);
			
			// 2 生成赎回订单表(赎回债权详情表由定时任务处理)
			AtoneInfoEntity atoneInfoEntity = new AtoneInfoEntity();
			atoneInfoEntity.setCustId(invest.getCustId());
			atoneInfoEntity.setInvestId(invest.getId());
			atoneInfoEntity.setProductId(invest.getProductId());
			atoneInfoEntity.setOperType("");
			atoneInfoEntity.setTradeCode(numberService.generateTradeNumber());
			atoneInfoEntity.setCleanupDate(new Date());
			atoneInfoEntity.setAtoneMethod(Constant.ATONE_METHOD_EXPIRE);
			atoneInfoEntity.setAtoneExpenses(new BigDecimal("0"));
			atoneInfoEntity.setAtoneStatus(Constant.TRADE_STATUS_01);
			atoneInfoEntity.setAuditStatus(Constant.AUDIT_STATUS_PASS);
			atoneInfoEntity.setAtoneTotalAmount(factIncome);
			atoneInfoEntity.setAlreadyAtoneAmount(ArithUtil.add(invest.getInvestAmount(), exceptInterest));// 注：此处记录一个预期收益，等赎回到账后再改为预期收益+奖励收益，这么做的原因是防止赎回到账时重复计算
			atoneInfoEntity.setTradeSource(Constant.INVEST_SOURCE_PC);
			atoneInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
			atoneInfoEntity = atoneInfoRepository.save(atoneInfoEntity);
			
			// 3.记录日志
			LogInfoEntity logInfoEntity = new LogInfoEntity();
			logInfoEntity.setRelateType(Constant.TABLE_BAO_T_ATONE_INFO);
			logInfoEntity.setRelatePrimary(atoneInfoEntity.getId());
			logInfoEntity.setLogType(Constant.OPERATION_TYPE_22);
			logInfoEntity.setOperDesc("");
			logInfoEntity.setOperPerson(Constant.SYSTEM_USER_BACK);
			logInfoEntity.setOperIpaddress("");
			logInfoEntity.setMemo(String.format("%s赎回%s，赎回金额(含奖励)%s，奖励%s", custInfoEntity.getLoginName(), productInfoEntity.getProductName(), factIncome.toString(), awardInterest.toString()));
			logInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
			logInfoEntityRepository.save(logInfoEntity);
			
			// 4.冻结用户价值
			String reqeustNo = numberService.generateTradeBatchNumber();
			SubAccountInfoEntity subAccount = subAccountInfoRepository.findByRelatePrimary(invest.getId());
			freezeUserAccount(accountInfoEntity, subAccount, accountInfoEntity, subAccount, subAccount.getAccountAvailableValue(), reqeustNo, atoneInfoEntity, SubjectConstant.TRADE_FLOW_TYPE_ATONE_TERM_FREEZE_TERM, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_TERM_FREEZE_TERM);
		}
		
		return new ResultVo(true);
	}

	/* 
	 * 公司回购
	 * @see com.slfinance.shanlinbao.service.TermService#termAtoneBuy(java.util.Map)
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo termAtoneBuy(Map<String, Object> params) throws SLException {
		
		Date now = new Date();
		int buyBackDays = paramService.findBuyBackDay();
		
		// 查询赎回中的记录
		List<SubAccountInfoEntity> atoneList = subAccountInfoRepository.queryTermAtone(Constant.PRODUCT_TYPE_04, Constant.TRADE_STATUS_01, DateUtils.getEndDate(DateUtils.getAfterDay(now, 0-buyBackDays)));
		
		SubAccountInfoEntity centerSubAccount = subAccountInfoRepository.findBySubAccountNo(Constant.SUB_ACCOUNT_NO_CENTER_11); // 公司居间人分账户
		if(centerSubAccount == null || centerSubAccount.getAccountAvailableValue() == null) {
			throw new SLException("未初始化公司居间人分账户！");
		}
		AccountInfoEntity centerMainAccount = accountInfoRepository.findOne(centerSubAccount.getAccountId()); // 公司居间人主账户
		if(centerMainAccount == null) {
			throw new SLException("未初始化公司居间人主账户！");
		}
		
		for(SubAccountInfoEntity subAccount : atoneList){
			AtoneInfoEntity atoneInfoEntity = atoneInfoRepository.findByInvestId(subAccount.getRelatePrimary());
			saveUserAccount(null, subAccount, centerMainAccount, centerSubAccount, subAccount.getAccountFreezeValue(), numberService.generateTradeBatchNumber(), atoneInfoEntity);
		}
		
		return new ResultVo(true);
	}
	
	/* 
	 * 每日结息
	 * @see com.slfinance.shanlinbao.service.TermService#termDailySettlement(java.util.Map)
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo termDailySettlement(Map<String, Object> params)
			throws SLException {
		
		Date now = new Date();
		params.put("currDate", DateUtils.formatDate(now, "yyyyMMdd"));
		params.put("typeName", Constant.PRODUCT_TYPE_04);
		/* 判断每日结息每天重复执行 */
		int count = dailySettlementRepository.findCurrDateRecordCount(params);
		if (count > 0) {
			log.error("每日结息不能重复执行");
			return new ResultVo(false);
		}
		
		// 保存每日债权数据(前一天)
		loanInfoService.saveDailyLoanAndValue(DateUtils.getAfterDay(now, -1), Constant.PRODUCT_TYPE_04);
		
		// ================================开始结息==========================================
		params.put("execDate", DateUtils.formatDate(now, "yyyyMMdd"));
		params.put("preExecDate", DateUtils.formatDate(DateUtils.getAfterDay(now, -1), "yyyyMMdd"));
		/*活期宝总价值*/
		BigDecimal totalPv=dailySettlementRepository.findTotalPV(params);
		/*活期宝实际总利息*/
		BigDecimal totalInterest=dailySettlementRepository.findActualInterest(params);
		/*还款未处理金额*/
		BigDecimal untreatedAmount=dailySettlementRepository.untreatedAmount(params);
		
		params.put("totalPv", totalPv);
		params.put("totalInterest", totalInterest);
		params.put("untreatedAmount", untreatedAmount);
		
		// 统计待结息数据
		int total = dailySettlementRepository.countTermSubAccount(params);
		Map<String, String> allRequestNoMap = new HashMap<String, String>();
		int page = 5000;
		for(int i = 0; i < total/page; i ++) {
			params.put("start", i*page);
			params.put("length", page);
			List<Map<String,Object>> subAccountList=dailySettlementRepository.findTermSubAccount(params);
			singleTermDailySettlement(subAccountList, (String)params.get("preExecDate"), (String)params.get("execDate"), allRequestNoMap);
		}
		
		if( total%page != 0 ) {
			params.put("start", total - total%page);
			params.put("length", total%page);
			List<Map<String,Object>> subAccountList=dailySettlementRepository.findTermSubAccount(params);
			singleTermDailySettlement(subAccountList, (String)params.get("preExecDate"), (String)params.get("execDate"), allRequestNoMap);
		}
		// ================================完成结息==========================================
		
		return new ResultVo(true);
	}

	/* 
	 * 赎回到帐
	 * @see com.slfinance.shanlinbao.service.TermService#termAtoneSettlement(java.util.Map)
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo termAtoneSettlement(Map<String, Object> params)
			throws SLException {
		params.put("typeName", Constant.PRODUCT_TYPE_04);
		int total = atoneInfoRepositoryCustom.countTermAtone(params);
		int page = 5000;
		
		List<Map<String, Object>> smsList = Lists.newArrayList();
		List<AccountInfoEntity> accountList = new ArrayList<AccountInfoEntity>();
		for(int i = 0; i < total/page; i ++) {
			params.put("start", i*page);
			params.put("length", page);
			List<Map<String, Object>> atoneList = atoneInfoRepositoryCustom.findTermAtone(params);
			singleTermAtoneSettlement(atoneList, smsList, accountList);
		}
		
		if( total%page != 0 ) {
			params.put("start", total - total%page);
			params.put("length", total%page);
			List<Map<String, Object>> atoneList = atoneInfoRepositoryCustom.findTermAtone(params);
			singleTermAtoneSettlement(atoneList, smsList, accountList);
		}
		
		atoneInfoRepositoryCustom.batchUpdateAccount(accountList);

		return new ResultVo(true, "赎回到帐成功", smsList);
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
	public InvestInfoEntity saveInvest(InvestInfoEntity oldInvestInfoEntity, String custId, String productId, BigDecimal tradeAmount, String investDate, String expireDate, String tradeCode, boolean insert,String investSource)
	{
		InvestInfoEntity tmp = null;
		if(insert){
			InvestInfoEntity investInfoEntity = new InvestInfoEntity();
			investInfoEntity.setCustId(custId);
			investInfoEntity.setProductId(productId);
			investInfoEntity.setInvestAmount(tradeAmount);
			investInfoEntity.setInvestStatus(Constant.TERM_INVEST_STATUS_EARN);
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
		investDetailInfoEntity.setInvestSource(StringUtils.isNotEmpty(investSource) ? investSource : Constant.INVEST_SOURCE_PC);//update by zhangzs 默认值情况下是PC端
		investDetailInfoEntity.setBasicModelProperty(custId, true);
		investDetailInfoRepository.save(investDetailInfoEntity);
		
		return tmp;
	}

	/**
	 * 创建分账户
	 *
	 * @author  wangjf
	 * @date    2015年4月29日 下午7:16:50
	 * @param custId
	 * @param accountId
	 * @param investId
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public SubAccountInfoEntity saveSubAccountInfo(String custId, String accountId, String investId)
	{
		SubAccountInfoEntity subAccountInfoEntity = new SubAccountInfoEntity();
		subAccountInfoEntity.setCustId(custId);
		subAccountInfoEntity.setAccountId(accountId);
		subAccountInfoEntity.setRelateType(Constant.TABLE_BAO_T_INVEST_INFO);
		subAccountInfoEntity.setRelatePrimary(investId);
		subAccountInfoEntity.setSubAccountNo(numberService.generateCustomerNumber());
		subAccountInfoEntity.setAccountAmount(new BigDecimal("0"));
		subAccountInfoEntity.setAccountTotalValue(new BigDecimal("0"));
		subAccountInfoEntity.setAccountAvailableValue(new BigDecimal("0"));
		subAccountInfoEntity.setAccountFreezeValue(new BigDecimal("0"));
		subAccountInfoEntity.setBasicModelProperty(custId, true);
		subAccountInfoRepository.save(subAccountInfoEntity);
		
		return subAccountInfoEntity;
	}
	
	/**
	 * 赎回分账处理
	 *
	 * @author  wangjf
	 * @date    2015年8月14日 下午5:20:32
	 * @param accountInfoEntity
	 * @param s
	 * @param earnMainAccount
	 * @param earnSubAccount
	 * @param transferAmount
	 * @param reqeustNo
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public void saveAtoneAccount(AccountInfoEntity accountInfoEntity, SubAccountInfoEntity s, AccountInfoEntity earnMainAccount, SubAccountInfoEntity earnSubAccount, BigDecimal transferAmount, String reqeustNo)
	{
		// 7.1 更新赎回分账户(价值转出)
		earnSubAccount.setAccountTotalValue(ArithUtil.sub(earnSubAccount.getAccountTotalValue(), transferAmount));
		earnSubAccount.setAccountFreezeValue(ArithUtil.sub(earnSubAccount.getAccountFreezeValue(), transferAmount));
		accountFlowService.saveAccountFlow(earnMainAccount, earnSubAccount, accountInfoEntity, s, "4", 
				SubjectConstant.TRADE_FLOW_TYPE_ATONE_TRANSFER, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, 
				transferAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_TRANSFER, 
				s.getRelateType(), s.getRelatePrimary(), SubjectConstant.SUBJECT_TYPE_VALUE);
		
		// 7.2 更新赎回分账户（资金转入）
		earnSubAccount.setAccountAmount(ArithUtil.add(earnSubAccount.getAccountAmount(), transferAmount));
		earnSubAccount.setBasicModelProperty(s.getCustId(), false);		
		accountFlowService.saveAccountFlow(earnMainAccount, earnSubAccount, accountInfoEntity, s, "4", 
				SubjectConstant.TRADE_FLOW_TYPE_ATONE_TRANSFER, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING, 
				transferAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_TRANSFER, 
				s.getRelateType(), s.getRelatePrimary(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
		
		// 4 生成买单记录
		AtoneInfoEntity atoneInfoEntity = atoneInfoRepository.findByInvestId(earnSubAccount.getRelatePrimary());
		EmptionInfoEntity emptionInfoEntity = new EmptionInfoEntity();
		emptionInfoEntity.setAtoneId(atoneInfoEntity.getId());
		emptionInfoEntity.setEmptionAmount(transferAmount);
		emptionInfoEntity.setEmptionDate(new Date());
		emptionInfoEntity.setAccountId(earnSubAccount.getId());//购买账户为当期用户账户
		emptionInfoEntity.setCustId(s.getCustId());
		emptionInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
		emptionInfoRepository.save(emptionInfoEntity);
	}
	
	/**
	 * 居间人账户处理
	 *
	 * @author  wangjf
	 * @date    2015年4月29日 下午7:17:35
	 * @param centerMainAccount
	 * @param centerSubAccount
	 * @param earnMainAccount
	 * @param earnSubAccount
	 * @param custId
	 * @param reqeustNo
	 * @param tradeAmount
	 * @param fixLimited
	 * @param fixLimitedAmount
	 * @param fixLimitedScalce
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public void saveCenterAccount(AccountInfoEntity custAccount, SubAccountInfoEntity subAccountInfoEntity, 
			AccountInfoEntity centerMainAccount, SubAccountInfoEntity centerSubAccount, String custId,
			String reqeustNo, BigDecimal tradeAmount)
	{
		// 7.1 更新居间人分账户(价值转出)
		centerSubAccount.setAccountTotalValue(ArithUtil.sub(centerSubAccount.getAccountTotalValue(), tradeAmount));
		centerSubAccount.setAccountAvailableValue(ArithUtil.sub(centerSubAccount.getAccountAvailableValue(), tradeAmount));
		accountFlowService.saveAccountFlow(centerMainAccount, centerSubAccount, custAccount, subAccountInfoEntity, "4", 
				SubjectConstant.TRADE_FLOW_TYPE_JOIN_TERM, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, 
				tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_JOIN_TERM, 
				subAccountInfoEntity.getRelateType(), subAccountInfoEntity.getRelatePrimary(), SubjectConstant.SUBJECT_TYPE_VALUE);
		
		// 7.2 更新居间人分账户（资金转入）
		centerSubAccount.setAccountAmount(ArithUtil.add(centerSubAccount.getAccountAmount(), tradeAmount));
		centerSubAccount.setBasicModelProperty(custId, false);		
		accountFlowService.saveAccountFlow(centerMainAccount, centerSubAccount, custAccount, subAccountInfoEntity, "4", 
				SubjectConstant.TRADE_FLOW_TYPE_JOIN_TERM, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING, 
				tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_JOIN_TERM, 
				subAccountInfoEntity.getRelateType(), subAccountInfoEntity.getRelatePrimary(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
			

		// 7.3 更新居间人分账户（资金转出）
		centerSubAccount.setAccountAmount(ArithUtil.sub(centerSubAccount.getAccountAmount(), tradeAmount));
		centerSubAccount.setBasicModelProperty(custId, false);
		accountFlowService.saveAccountFlow(centerMainAccount, centerSubAccount, centerMainAccount, centerSubAccount, "3", 
				SubjectConstant.TRADE_FLOW_TYPE_JOIN_TERM, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, 
				tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_JOIN_TERM, 
				subAccountInfoEntity.getRelateType(), subAccountInfoEntity.getRelatePrimary(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
		
		// 7.4 更新居间人主账户（资金转入）
		centerMainAccount.setAccountTotalAmount(ArithUtil.add(centerMainAccount.getAccountTotalAmount(), tradeAmount));
		centerMainAccount.setAccountAvailableAmount(ArithUtil.add(centerMainAccount.getAccountAvailableAmount(), tradeAmount));
		centerMainAccount.setBasicModelProperty(custId, false);
		accountFlowService.saveAccountFlow(centerMainAccount, centerSubAccount, centerMainAccount, centerSubAccount, "2", 
				SubjectConstant.TRADE_FLOW_TYPE_JOIN_TERM, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING, 
				tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_JOIN_TERM, 
				subAccountInfoEntity.getRelateType(), subAccountInfoEntity.getRelatePrimary(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
	}
	
	/**
	 * 用户账户处理
	 *
	 * @author  wangjf
	 * @date    2015年4月30日 下午5:40:52
	 * @param custAccount
	 * @param subAccountInfoEntity
	 * @param companyCustAccount
	 * @param companySubAccountInfoEntity
	 * @param tradeAmount
	 * @param reqeustNo
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public void saveJoinUserAccount(AccountInfoEntity custAccount, SubAccountInfoEntity subAccountInfoEntity, 
			AccountInfoEntity companyCustAccount, SubAccountInfoEntity companySubAccountInfoEntity,
			BigDecimal tradeAmount, String reqeustNo)
	{
		// 8、更新用户分账户及记录流水
		
		// 8.1 记录用户分账流水(资金转出)
		subAccountInfoEntity.setAccountAmount(ArithUtil.sub(subAccountInfoEntity.getAccountAmount(), tradeAmount));
		accountFlowService.saveAccountFlow(custAccount, subAccountInfoEntity, companyCustAccount, companySubAccountInfoEntity, "4", 
				SubjectConstant.TRADE_FLOW_TYPE_JOIN_TERM, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, 
				tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_JOIN_TERM, 
				subAccountInfoEntity.getRelateType(), subAccountInfoEntity.getRelatePrimary(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
		
		// 8.2 记录用户分账流水（价值转入）
		subAccountInfoEntity.setAccountTotalValue(ArithUtil.add(subAccountInfoEntity.getAccountTotalValue(), tradeAmount));
		subAccountInfoEntity.setAccountAvailableValue(ArithUtil.add(subAccountInfoEntity.getAccountAvailableValue(), tradeAmount));
		subAccountInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
		accountFlowService.saveAccountFlow(custAccount, subAccountInfoEntity, companyCustAccount, companySubAccountInfoEntity, "4", 
				SubjectConstant.TRADE_FLOW_TYPE_JOIN_TERM, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING, 
				tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_JOIN_TERM, 
				subAccountInfoEntity.getRelateType(), subAccountInfoEntity.getRelatePrimary(), SubjectConstant.SUBJECT_TYPE_VALUE);
	}
	
	/**
	 * 赎回——冻结用户分账
	 *
	 * @author  wangjf
	 * @date    2015年4月30日 下午4:50:21
	 * @param accountInfoEntity
	 * @param s
	 * @param transferAmount
	 * @param reqeustNo
	 * @param atoneInfoEntity
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public void freezeUserAccount(AccountInfoEntity accountInfoEntity, SubAccountInfoEntity s, AccountInfoEntity earnMainAccount, SubAccountInfoEntity earnSubAccount, 
			BigDecimal transferAmount, String reqeustNo, AtoneInfoEntity atoneInfoEntity,
			String tradeType, String subjectType)
	{
		// 5.1 冻结用户分账户（价值）
		s.setAccountAvailableValue(ArithUtil.sub(s.getAccountAvailableValue(), transferAmount));
		s.setAccountFreezeValue(ArithUtil.add(s.getAccountFreezeValue(), transferAmount));
		
		// 5.2 记录用户分账户流水（价值 ）
		accountFlowService.saveAccountFlow(accountInfoEntity, s, accountInfoEntity, s, "4", 
				tradeType, reqeustNo, null, 
				transferAmount, subjectType, 
				Constant.TABLE_BAO_T_ATONE_INFO, atoneInfoEntity.getId(), SubjectConstant.SUBJECT_TYPE_VALUE);	
	}

	/**
	 * 赎回回购
	 *
	 * @author  wangjf
	 * @date    2015年8月15日 下午4:02:06
	 * @param accountInfoEntity
	 * @param s
	 * @param earnMainAccount
	 * @param earnSubAccount
	 * @param transferAmount
	 * @param reqeustNo
	 * @param atoneInfoEntity
	 * @param atoneType
	 * @param oldTradeNo
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public void saveUserAccount(AccountInfoEntity accountInfoEntity, SubAccountInfoEntity s, 
			AccountInfoEntity earnMainAccount, SubAccountInfoEntity earnSubAccount, 
			BigDecimal transferAmount, String reqeustNo, AtoneInfoEntity atoneInfoEntity)
	{
		String tradeType = SubjectConstant.TRADE_FLOW_TYPE_ATONE_BUY_BACK;
		String subjectType = SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_BUY_BACK;
		
		// 6 更新公司收益分账户价值并记录流水
		
		// 6.1 更新公司收益主账户（资金转出）
		earnMainAccount.setAccountTotalAmount(ArithUtil.sub(earnMainAccount.getAccountTotalAmount(), transferAmount));
		earnMainAccount.setAccountAvailableAmount(ArithUtil.sub(earnMainAccount.getAccountAvailableAmount(), transferAmount));
		earnMainAccount.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
		
		accountFlowService.saveAccountFlow(earnMainAccount, earnSubAccount, earnMainAccount, earnSubAccount, "2", 
				tradeType, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, 
				transferAmount, subjectType, 
				Constant.TABLE_BAO_T_ATONE_INFO, atoneInfoEntity.getId(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
		
		// 6.2 更新公司收益分账户（资金转入）
		earnSubAccount.setAccountAmount(ArithUtil.add(earnSubAccount.getAccountAmount(), transferAmount));
		
		accountFlowService.saveAccountFlow(earnMainAccount, earnSubAccount, accountInfoEntity, s, "3", 
				tradeType, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING, 
				transferAmount, subjectType, 
				Constant.TABLE_BAO_T_ATONE_INFO, atoneInfoEntity.getId(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
		
		// 6.3 记录用户分账户流水（价值转出 ）
		s.setAccountTotalValue(ArithUtil.sub(s.getAccountTotalValue(), transferAmount));
		s.setAccountFreezeValue(ArithUtil.sub(s.getAccountFreezeValue(), transferAmount));
		
		accountFlowService.saveAccountFlow(accountInfoEntity, s, earnMainAccount, earnSubAccount, "4", 
				SubjectConstant.TRADE_FLOW_TYPE_ATONE_TRANSFER, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, 
				transferAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_TRANSFER, 
				Constant.TABLE_BAO_T_ATONE_INFO, atoneInfoEntity.getId(), SubjectConstant.SUBJECT_TYPE_VALUE);
		
		// 6.4 记录用户分账户流水（现金转入）
		s.setAccountAmount(ArithUtil.add(s.getAccountAmount(), transferAmount));
		
		accountFlowService.saveAccountFlow(accountInfoEntity, s, earnMainAccount, earnSubAccount, "4", 
				SubjectConstant.TRADE_FLOW_TYPE_ATONE_TRANSFER, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING, 
				transferAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_TRANSFER, 
				Constant.TABLE_BAO_T_ATONE_INFO, atoneInfoEntity.getId(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
		
		// 6.5 更新公司收益分账户（资金转出）
		earnSubAccount.setAccountAmount(ArithUtil.sub(earnSubAccount.getAccountAmount(), transferAmount));
		
		accountFlowService.saveAccountFlow(earnMainAccount, earnSubAccount, accountInfoEntity, s, "4", 
				tradeType, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, 
				transferAmount, subjectType, 
				Constant.TABLE_BAO_T_ATONE_INFO, atoneInfoEntity.getId(), SubjectConstant.SUBJECT_TYPE_AMOUNT);

		// 6.6 更新公司收益账户（价值转入）
		earnSubAccount.setAccountTotalValue(ArithUtil.add(earnSubAccount.getAccountTotalValue(), transferAmount));
		earnSubAccount.setAccountAvailableValue(ArithUtil.add(earnSubAccount.getAccountAvailableValue(), transferAmount));
		earnSubAccount.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
		
		accountFlowService.saveAccountFlow(earnMainAccount, earnSubAccount, accountInfoEntity, s, "4",
				tradeType, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING, 
				transferAmount, subjectType, 
				Constant.TABLE_BAO_T_ATONE_INFO, atoneInfoEntity.getId(), SubjectConstant.SUBJECT_TYPE_VALUE);
		
		// 4 生成买单记录
		EmptionInfoEntity emptionInfoEntity = new EmptionInfoEntity();
		emptionInfoEntity.setAtoneId(atoneInfoEntity.getId());
		emptionInfoEntity.setEmptionAmount(transferAmount);
		emptionInfoEntity.setEmptionDate(new Date());
		emptionInfoEntity.setAccountId(earnSubAccount.getId());//购买账户为当期用户账户
		emptionInfoEntity.setCustId(earnSubAccount.getCustId());
		emptionInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
		emptionInfoRepository.save(emptionInfoEntity);
		
	}
	
	/**
	 * 单次定期宝结息
	 *
	 * @author  wangjf
	 * @date    2015年8月15日 下午6:09:21
	 * @param subAccountList
	 * @param preDate
	 * @param execDate
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public void singleTermDailySettlement(List<Map<String,Object>> subAccountList, String preDate, String execDate,
			Map<String, String> allRequestNoMap) {
		List<DailyInterestInfoEntity> list=new ArrayList<DailyInterestInfoEntity>();
		List<AccountFlowInfoEntity> accountFlowList = new ArrayList<AccountFlowInfoEntity>();
//		List<AccountFlowDetailEntity> accountFlowDetailList = new ArrayList<AccountFlowDetailEntity>();
//		List<FlowBusiRelationEntity> flowBusiList = new ArrayList<FlowBusiRelationEntity>();
		List<SubAccountInfoEntity> subAccountInfoList = new ArrayList<SubAccountInfoEntity>();
		 
		Map<String, Integer> requestNOMap = new HashMap<String, Integer>();
		int countRequestNo = 0;
		List<String> custIdList = new ArrayList<String>();
		for(Map<String,Object> subAccount:subAccountList){
			
			/*客户分账户实际利息*/
			BigDecimal factInterest = ArithUtil.formatScale(new BigDecimal(subAccount.get("factInterest").toString()), 8);
			
			SubAccountInfoEntity custSubAccount = new SubAccountInfoEntity();
			custSubAccount.setId(subAccount.get("id").toString());
			custSubAccount.setCustId(subAccount.get("custId").toString());
			custSubAccount.setRelatePrimary(subAccount.get("relatePrimary").toString());
			custSubAccount.setRelateType(subAccount.get("relateType").toString());
			custSubAccount.setAccountTotalValue(new BigDecimal(subAccount.get("accountTotalValue").toString()).add(factInterest));
			custSubAccount.setAccountAvailableValue(new BigDecimal(subAccount.get("accountAvailableValue").toString()));
			custSubAccount.setAccountFreezeValue(new BigDecimal(subAccount.get("accountFreezeValue").toString()));
			if(custSubAccount.getAccountAvailableValue().compareTo(new BigDecimal("0")) == 0) {
				custSubAccount.setAccountFreezeValue(ArithUtil.add(custSubAccount.getAccountFreezeValue(), factInterest));
			}
			else {
				custSubAccount.setAccountAvailableValue(ArithUtil.add(custSubAccount.getAccountAvailableValue(), factInterest));
			}
			custSubAccount.setAccountAmount(new BigDecimal(subAccount.get("accountAmount").toString()));
			custSubAccount.setVersion(new Integer(subAccount.get("version").toString()));
			subAccountInfoList.add(custSubAccount);

			//同一个客户的收益，记录流水时，REQUEST_NO要是同一个
			if(!allRequestNoMap.containsKey(subAccount.get("custId").toString()) 
					&& !requestNOMap.containsKey(subAccount.get("custId").toString())){
				requestNOMap.put(subAccount.get("custId").toString(), countRequestNo ++);
			}

			/* 记录结息分账户流水 */
			DailyInterestInfoEntity dailyInterest=new DailyInterestInfoEntity();
			dailyInterest.setId(SharedUtil.getUniqueString());
			dailyInterest.setCurrDate(execDate);
			dailyInterest.setSubAccountId(custSubAccount.getId());
			dailyInterest.setCreateDate(new Date());
			dailyInterest.setExpectInterest(factInterest);
			dailyInterest.setFactInterest(factInterest);
			dailyInterest.setFactGainInterest(factInterest);
			list.add(dailyInterest);
			
			/* 记录客户分账户流水 */
			AccountFlowInfoEntity accountFlowInfo = new AccountFlowInfoEntity();
			accountFlowInfo.setId(SharedUtil.getUniqueString());
			accountFlowInfo.setAccountTotalAmount(custSubAccount.getAccountTotalValue());
			accountFlowInfo.setAccountAvailable(custSubAccount.getAccountAvailableValue());
			accountFlowInfo.setAccountFreezeAmount(custSubAccount.getAccountFreezeValue());
			accountFlowInfo.setAccountType(Constant.ACCOUNT_TYPE_SUB);
			accountFlowInfo.setAccountId(custSubAccount.getId());
			accountFlowInfo.setCustId(custSubAccount.getCustId());
			if(Constant.CUST_ID_CENTER.equals(custSubAccount.getCustId())
					|| Constant.CUST_ID_ERAN.equals(custSubAccount.getCustId())) {
				accountFlowInfo.setTradeType(SubjectConstant.TRADE_FLOW_TYPE_DAILY_INCOME);
			}
			else {
				accountFlowInfo.setTradeType(SubjectConstant.TRADE_FLOW_TYPE_04);
			}
			accountFlowInfo.setRequestNo(subAccount.get("custId").toString());// 请求编号
			accountFlowInfo.setTradeNo(""); // 交易编号
			accountFlowInfo.setBankrollFlowDirection(SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING);
			accountFlowInfo.setTradeAmount(ArithUtil.formatScale(factInterest,8));
			accountFlowInfo.setTradeDate(DateUtils.parseDate(preDate, "yyyyMMdd"));
			accountFlowInfo.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
			accountFlowInfo.setFlowType(SubjectConstant.SUBJECT_TYPE_VALUE);
			accountFlowInfo.setMemo("");
			accountFlowInfo.setCashAmount(custSubAccount.getAccountAmount());
			accountFlowInfo.setTargetAccount(custSubAccount.getId());
			accountFlowInfo.setRelateType(custSubAccount.getRelateType());
			accountFlowInfo.setRelatePrimary(custSubAccount.getRelatePrimary());
			accountFlowList.add(accountFlowInfo);
			
//			AccountFlowDetailEntity accountFlowDetailEntity = new AccountFlowDetailEntity();
//			accountFlowDetailEntity.setId(SharedUtil.getUniqueString());
//			accountFlowDetailEntity.setAccountFlowId(accountFlowInfo.getId());
//			if(Constant.CUST_ID_CENTER.equals(custSubAccount.getCustId())
//					|| Constant.CUST_ID_ERAN.equals(custSubAccount.getCustId())) {
//				accountFlowDetailEntity.setSubjectType(SubjectConstant.TRADE_FLOW_TYPE_DAILY_INCOME);
//			}
//			else {
//				accountFlowDetailEntity.setSubjectType(SubjectConstant.TRADE_FLOW_TYPE_04);
//			}
//			accountFlowDetailEntity.setSubjectDirection(SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING);
//			accountFlowDetailEntity.setTradeAmount(accountFlowInfo.getTradeAmount());
//			accountFlowDetailEntity.setTradeDesc(Constant.PRODUCT_TYPE_04);
//			accountFlowDetailEntity.setTargetAccount(custSubAccount.getId());
//			accountFlowDetailEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
//			accountFlowDetailList.add(accountFlowDetailEntity);
//			
//			FlowBusiRelationEntity flowBusiRelationEntity = new FlowBusiRelationEntity();
//			flowBusiRelationEntity.setId(SharedUtil.getUniqueString());
//			flowBusiRelationEntity.setAccountFlowId(accountFlowInfo.getId());
//			flowBusiRelationEntity.setRelateType(custSubAccount.getRelateType());
//			flowBusiRelationEntity.setRelatePrimary(custSubAccount.getRelatePrimary());
//			flowBusiRelationEntity.setCreateDate(new Date());
//			flowBusiList.add(flowBusiRelationEntity);
			
			if(!custIdList.contains(subAccount.get("custId").toString())){
				custIdList.add(subAccount.get("custId").toString());
			}
		}
		
		// 从库里面批量取RequestNo和TradeNo
		List<String> requestNoList = numberService.generateTradeBatchNumber(countRequestNo);
		List<String> tradeNoList = numberService.generateTradeNumber(accountFlowList.size());
		
		// 删除allRequestNoMap中已经不存在的custId
		Iterator<Map.Entry<String, String>> it = allRequestNoMap.entrySet().iterator(); 
        while(it.hasNext())
        { 
            Map.Entry<String, String> entry = it.next(); 
            if(!custIdList.contains(entry.getKey())) {
            	it.remove();
            }
        } 
		
        // 把新的requestNo放至allRequestNoMap
		for(Map.Entry<String, Integer> entry: requestNOMap.entrySet()){   
		     allRequestNoMap.put(entry.getKey(), requestNoList.get(entry.getValue()));
		}   
		
		// 从allRequestNoMap取RequestNo
		for(int i = 0; i < accountFlowList.size(); i ++) {
			accountFlowList.get(i).setTradeNo(tradeNoList.get(i));
			accountFlowList.get(i).setRequestNo(allRequestNoMap.get(accountFlowList.get(i).getRequestNo()));
		}
		
		/*批量插入结息表*/
		dailySettlementRepository.batchDailyInterestInfo(list);
		dailySettlementRepository.batchInsertAccountFlow(accountFlowList);				
//		dailySettlementRepository.batchInsertAccountFlowDetail(accountFlowDetailList);
//		dailySettlementRepository.batchInsertFlowBusiRelation(flowBusiList);
		dailySettlementRepository.batchUpdateSubAccountInfo(subAccountInfoList);
		
	} 
	
	/**
	 * 单次赎回到账
	 *
	 * @author  wangjf
	 * @date    2015年8月17日 上午10:59:45
	 * @param atoneList
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public void singleTermAtoneSettlement(List<Map<String,Object>> atoneMapList, List<Map<String, Object>> smsList, List<AccountInfoEntity> accountList) {
		
		List<AtoneInfoEntity> atoneList = new ArrayList<AtoneInfoEntity>();
		List<InvestInfoEntity> investList = new ArrayList<InvestInfoEntity>();
		List<SubAccountInfoEntity> subAccountList = new ArrayList<SubAccountInfoEntity>();
		
		SubAccountInfoEntity earnSubAccount = subAccountInfoRepository.findBySubAccountNo(Constant.SUB_ACCOUNT_NO_ERAN_12); // 公司收益分账户
		AccountInfoEntity earnMainAccount = accountInfoRepository.findOne(earnSubAccount.getAccountId()); // 公司收益主账户
		SubAccountInfoEntity riskSubAccount = subAccountInfoRepository.findBySubAccountNo(Constant.SUB_ACCOUNT_NO_RISK_13); // 公司风险金分账户
		AccountInfoEntity riskMainAccount = accountInfoRepository.findOne(riskSubAccount.getAccountId()); // 公司风险金主账户
		
		for(Map<String,Object> subAccount:atoneMapList){

			// 投资金额
			BigDecimal investAmount = new BigDecimal(subAccount.get("investAmount").toString());
			// 预期收益
			BigDecimal exceptIncome = new BigDecimal(subAccount.get("atoneTotalAmount").toString());
			// 实际收益
			BigDecimal factIncome = new BigDecimal(subAccount.get("accountAmount").toString());
			// 赎回手续费（到期赎回为0）
			BigDecimal atoneExpenses = new BigDecimal(subAccount.get("atoneExpenses").toString());
			// 未扣手续费的预期收益
			BigDecimal exceptIncomeWithoutExpenses = ArithUtil.add(exceptIncome, atoneExpenses);
			
			// 准备发送短信内容
			Map<String, Object> smsParams = new HashMap<String, Object>();
			smsParams.put("atoneMethod", subAccount.get("atoneMethod"));
			smsParams.put("mobile", subAccount.get("mobile"));
			smsParams.put("custId", subAccount.get("custId"));			
			if(Constant.ATONE_METHOD_EXPIRE.equals((String)subAccount.get("atoneMethod"))) { // 到期赎回
				smsParams.put("messageType", Constant.SMS_TYPE_TERM_ATONE);
				smsParams.put("values", new Object[] { // 短信息内容
						subAccount.get("investDate").toString(),
						subAccount.get("productName").toString(),
						subAccount.get("currTerm").toString(),
						ArithUtil.formatScale(exceptIncome, 2).toPlainString()});
				smsParams.put("systemMessage", new Object[] { // 站内信内容
						subAccount.get("productName").toString(),
						subAccount.get("currTerm").toString(),
						ArithUtil.formatScale(exceptIncome, 2).toPlainString(),
						ArithUtil.formatScale(investAmount, 2).toPlainString(),
						ArithUtil.formatScale(ArithUtil.sub(exceptIncome, investAmount), 2).toPlainString()});
			}
			else { // 提前赎回
				smsParams.put("messageType", Constant.SMS_TYPE_ADVANCED_ATONE_SUCCESS);
				smsParams.put("values", new Object[] { // 短信息内容
						subAccount.get("productName").toString(),
						subAccount.get("currTerm").toString(),
						ArithUtil.formatScale(exceptIncome, 2).toPlainString()});
				smsParams.put("systemMessage", new Object[] { // 站内信内容
						subAccount.get("productName").toString(),
						subAccount.get("currTerm").toString(),
						ArithUtil.formatScale(exceptIncome, 2).toPlainString()});
			}
			smsList.add(smsParams);
			
			// 赎回信息
			AtoneInfoEntity atoneInfoEntity = new AtoneInfoEntity();
			atoneInfoEntity.setId(subAccount.get("atoneId").toString());
			atoneInfoEntity.setAtoneMethod(subAccount.get("atoneMethod").toString());
			atoneInfoEntity.setAlreadyAtoneAmount(new BigDecimal(subAccount.get("alreadyAtoneAmount").toString()));
			atoneInfoEntity.setAtoneStatus(Constant.TRADE_STATUS_03);
			atoneInfoEntity.setVersion(Integer.valueOf(subAccount.get("atoneVersion").toString()));
			atoneInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
			atoneList.add(atoneInfoEntity);
			
			// 投资信息
			InvestInfoEntity investInfoEntity = new InvestInfoEntity();
			investInfoEntity.setId(subAccount.get("investId").toString());
			investInfoEntity.setProductId(subAccount.get("productId").toString());
			investInfoEntity.setCurrTerm(subAccount.get("currTerm").toString());
			investInfoEntity.setInvestStatus(Constant.ATONE_METHOD_ADVANCE.equals(atoneInfoEntity.getAtoneMethod()) ? Constant.TERM_INVEST_STATUS_ADVANCE_FINISH : Constant.TERM_INVEST_STATUS_FINISH);
			investInfoEntity.setVersion(Integer.valueOf(subAccount.get("investVersion").toString()));
			investInfoEntity.setInvestAmount(investAmount);
			investInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
			investList.add(investInfoEntity);
			
			// 分账信息
			SubAccountInfoEntity custSubAccount = new SubAccountInfoEntity();
			custSubAccount.setId(subAccount.get("subAccountId").toString());
			custSubAccount.setCustId(subAccount.get("custId").toString());
			custSubAccount.setRelatePrimary(subAccount.get("relatePrimary").toString());
			custSubAccount.setRelateType(subAccount.get("relateType").toString());
			custSubAccount.setAccountTotalValue(new BigDecimal(subAccount.get("accountTotalValue").toString()));
			custSubAccount.setAccountAvailableValue(new BigDecimal(subAccount.get("accountAvailableValue").toString()));
			custSubAccount.setAccountFreezeValue(new BigDecimal(subAccount.get("accountFreezeValue").toString()));
			custSubAccount.setAccountAmount(new BigDecimal(subAccount.get("accountAmount").toString()));
			custSubAccount.setVersion(Integer.valueOf(subAccount.get("subVersion").toString()));
			custSubAccount.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
			subAccountList.add(custSubAccount);
			
			// 账户信息
			AccountInfoEntity accountInfoEntity = null;
			for(AccountInfoEntity acc : accountList) {
				if(acc.getId().equals(subAccount.get("accountId").toString())) { // 账户已经存在
					accountInfoEntity = acc;
					break;
				}
			}
			
			if(accountInfoEntity == null) { // 不存在账户时需初始化
				accountInfoEntity = new AccountInfoEntity();
				accountInfoEntity.setId(subAccount.get("accountId").toString());
				accountInfoEntity.setCustId(subAccount.get("custId").toString());
				accountInfoEntity.setAccountTotalAmount(new BigDecimal(subAccount.get("accountTotalAmount").toString()));
				accountInfoEntity.setAccountFreezeAmount(new BigDecimal(subAccount.get("accountFreezeAmount").toString()));
				accountInfoEntity.setAccountAvailableAmount(new BigDecimal(subAccount.get("accountAvailableAmount").toString()));
				accountInfoEntity.setVersion(Integer.valueOf(subAccount.get("accountVersion").toString()));
				accountInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
				accountList.add(accountInfoEntity);
			}
			
			String requestNo = numberService.generateTradeBatchNumber();
			
			// 记录赎回定期宝(实际收益)和记录预期收益(不含手续费)
			saveUserAtone(accountInfoEntity, custSubAccount, earnMainAccount, earnSubAccount, factIncome, ArithUtil.add(ArithUtil.sub(exceptIncome, investAmount), atoneExpenses), requestNo, atoneInfoEntity);
			
			if(Constant.ATONE_METHOD_ADVANCE.equals(atoneInfoEntity.getAtoneMethod())) { // 提前赎回
				if(exceptIncomeWithoutExpenses.compareTo(factIncome) > 0) { // 预期收益大于实际收益
					// 差额 = 预期收益 - 实际收益 （由公司补差额部分）
					BigDecimal diff = ArithUtil.sub(exceptIncomeWithoutExpenses, factIncome);
					
					// 公司补差
					saveCompanyDiff(accountInfoEntity, custSubAccount, earnMainAccount, earnSubAccount, diff, requestNo, atoneInfoEntity, SubjectConstant.TRADE_FLOW_TYPE_ATONE_DIFF, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_DIFF);
					
					// 用户出手续费
					saveUserExpense(accountInfoEntity, custSubAccount, earnMainAccount, earnSubAccount, atoneExpenses, requestNo, atoneInfoEntity, SubjectConstant.TRADE_FLOW_TYPE_ATONE_EXPENSE, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_EXPENSE);

					// 用户收益
					saveUserIncome(accountInfoEntity, custSubAccount, earnMainAccount, earnSubAccount, investAmount, exceptIncome, requestNo, atoneInfoEntity, SubjectConstant.TRADE_FLOW_TYPE_ATONE_TERM_ADVANCE, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_TERM_ADVANCE, investInfoEntity);
				}
				else if(exceptIncomeWithoutExpenses.compareTo(factIncome) < 0) { // 预期收益小于实际收益
					// 差额 = 实际收益 - 预期收益（差额部分作为公司收益，其中30%作为风险金计入风险金账户，70%作为账户管理费计入收益账户）
					BigDecimal diff = ArithUtil.sub(factIncome, exceptIncomeWithoutExpenses);
					BigDecimal diffRisk =  ArithUtil.mul(diff, new BigDecimal("0.3"));
					BigDecimal diffManage =  ArithUtil.mul(diff, new BigDecimal("0.7"));
					
					// 差价30% 出计提风险金
					saveUserExpense(accountInfoEntity, custSubAccount, riskMainAccount, riskSubAccount, diffRisk, requestNo, atoneInfoEntity, SubjectConstant.TRADE_FLOW_TYPE_ATONE_RISK, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_RISK);
					
					// 差价70% 出账户管理费
					saveUserExpense(accountInfoEntity, custSubAccount, earnMainAccount, earnSubAccount, diffManage, requestNo, atoneInfoEntity, SubjectConstant.TRADE_FLOW_TYPE_ATONE_MANAGE, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_MANAGE);
					
					// 用户出手续费
					saveUserExpense(accountInfoEntity, custSubAccount, earnMainAccount, earnSubAccount, atoneExpenses, requestNo, atoneInfoEntity, SubjectConstant.TRADE_FLOW_TYPE_ATONE_EXPENSE, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_EXPENSE);

					// 用户收益
					saveUserIncome(accountInfoEntity, custSubAccount, earnMainAccount, earnSubAccount, investAmount, exceptIncome, requestNo, atoneInfoEntity, SubjectConstant.TRADE_FLOW_TYPE_ATONE_TERM_ADVANCE, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_TERM_ADVANCE, investInfoEntity);
				}
				else  { // 预期收益等于实际收益
					
					// 用户出手续费
					saveUserExpense(accountInfoEntity, custSubAccount, earnMainAccount, earnSubAccount, atoneExpenses, requestNo, atoneInfoEntity, SubjectConstant.TRADE_FLOW_TYPE_ATONE_EXPENSE, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_EXPENSE);

					// 用户收益
					saveUserIncome(accountInfoEntity, custSubAccount, earnMainAccount, earnSubAccount, investAmount, exceptIncome, requestNo, atoneInfoEntity, SubjectConstant.TRADE_FLOW_TYPE_ATONE_TERM_ADVANCE, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_TERM_ADVANCE, investInfoEntity);
				}
			}
			else { // 到期赎回
				
				atoneInfoEntity.setAlreadyAtoneAmount(exceptIncome);
				// 注：到期赎回时，赎回表中atoneTotalAmount记录的是预期到手收益，包含了奖励部分，alreadyAtoneAmount记录的为预期收益（不包含奖励部分）
				BigDecimal alreadyAtoneAmount = new BigDecimal(subAccount.get("alreadyAtoneAmount").toString());
				// 预期收益
				exceptIncomeWithoutExpenses = alreadyAtoneAmount;
				// 奖励金额
				BigDecimal awardAmount = ArithUtil.sub(exceptIncome, alreadyAtoneAmount);
				if(exceptIncomeWithoutExpenses.compareTo(factIncome) > 0) { // 预期收益大于实际收益
					BigDecimal diff = ArithUtil.sub(exceptIncomeWithoutExpenses, factIncome);
					
					// 公司补差
					saveCompanyDiff(accountInfoEntity, custSubAccount, earnMainAccount, earnSubAccount, diff, requestNo, atoneInfoEntity, SubjectConstant.TRADE_FLOW_TYPE_ATONE_DIFF, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_DIFF);
					
					// 用户奖励
					saveCompanyDiff(accountInfoEntity, custSubAccount, earnMainAccount, earnSubAccount, awardAmount, requestNo, atoneInfoEntity, SubjectConstant.TRADE_FLOW_TYPE_ATONE_AWARD, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_AWARD);

					// 用户收益
					saveUserIncome(accountInfoEntity, custSubAccount, earnMainAccount, earnSubAccount, investAmount, exceptIncome, requestNo, atoneInfoEntity, SubjectConstant.TRADE_FLOW_TYPE_ATONE_TERM_FINISH, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_TERM_FINISH, investInfoEntity);
				}
				else if(exceptIncomeWithoutExpenses.compareTo(factIncome) < 0) { // 预期收益小于实际收益
					
					BigDecimal diff = ArithUtil.sub(factIncome, exceptIncomeWithoutExpenses);
					BigDecimal diffRisk =  ArithUtil.mul(diff, new BigDecimal("0.3"));
					BigDecimal diffManage =  ArithUtil.mul(diff, new BigDecimal("0.7"));
					
					// 差价30% 出计提风险金
					saveUserExpense(accountInfoEntity, custSubAccount, riskMainAccount, riskSubAccount, diffRisk, requestNo, atoneInfoEntity, SubjectConstant.TRADE_FLOW_TYPE_ATONE_RISK, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_RISK);
					
					// 差价70% 出账户管理费
					saveUserExpense(accountInfoEntity, custSubAccount, earnMainAccount, earnSubAccount, diffManage, requestNo, atoneInfoEntity, SubjectConstant.TRADE_FLOW_TYPE_ATONE_MANAGE, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_MANAGE);
					
					// 用户奖励
					saveCompanyDiff(accountInfoEntity, custSubAccount, earnMainAccount, earnSubAccount, awardAmount, requestNo, atoneInfoEntity, SubjectConstant.TRADE_FLOW_TYPE_ATONE_AWARD, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_AWARD);

					// 用户收益
					saveUserIncome(accountInfoEntity, custSubAccount, earnMainAccount, earnSubAccount, investAmount, exceptIncome, requestNo, atoneInfoEntity, SubjectConstant.TRADE_FLOW_TYPE_ATONE_TERM_FINISH, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_TERM_FINISH, investInfoEntity);
				}
				else  { // 预期收益等于实际收益
					
					// 用户奖励
					saveCompanyDiff(accountInfoEntity, custSubAccount, earnMainAccount, earnSubAccount, awardAmount, requestNo, atoneInfoEntity, SubjectConstant.TRADE_FLOW_TYPE_ATONE_AWARD, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_AWARD);

					// 用户收益
					saveUserIncome(accountInfoEntity, custSubAccount, earnMainAccount, earnSubAccount, investAmount, exceptIncome, requestNo, atoneInfoEntity, SubjectConstant.TRADE_FLOW_TYPE_ATONE_TERM_FINISH, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_TERM_FINISH, investInfoEntity);
				}
			}
		}
		
		atoneInfoRepositoryCustom.batchUpdateAtone(atoneList);
		atoneInfoRepositoryCustom.batchUpdateInvest(investList);
		atoneInfoRepositoryCustom.batchUpdateSubAccount(subAccountList);
	}
	
	/**
	 * 公司补差/奖励
	 *
	 * @author  wangjf
	 * @date    2015年8月17日 下午1:47:12
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public void saveCompanyDiff(AccountInfoEntity accountInfoEntity, SubAccountInfoEntity subAccountInfoEntity, 
			AccountInfoEntity earnMainAccount, SubAccountInfoEntity earnSubAccount, BigDecimal transferAmount, 
			String reqeustNo, AtoneInfoEntity atoneInfoEntity, String tradeType, String subjectType)
	{		
		// 6 更新公司收益分账户价值并记录流水
		
		// 公司收益主账户（资金转出）
		earnMainAccount.setAccountTotalAmount(ArithUtil.sub(earnMainAccount.getAccountTotalAmount(), transferAmount));
		earnMainAccount.setAccountAvailableAmount(ArithUtil.sub(earnMainAccount.getAccountAvailableAmount(), transferAmount));
		earnMainAccount.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
		
		accountFlowService.saveAccountFlow(earnMainAccount, earnSubAccount, earnMainAccount, earnSubAccount, "2", 
				tradeType, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, 
				transferAmount, subjectType, 
				Constant.TABLE_BAO_T_ATONE_INFO, atoneInfoEntity.getId(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
		
		// 公司收益分账户（资金转入）
		earnSubAccount.setAccountAmount(ArithUtil.add(earnSubAccount.getAccountAmount(), transferAmount));
		
		accountFlowService.saveAccountFlow(earnMainAccount, earnSubAccount, accountInfoEntity, subAccountInfoEntity, "3", 
				tradeType, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING, 
				transferAmount, subjectType, 
				Constant.TABLE_BAO_T_ATONE_INFO, atoneInfoEntity.getId(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
		
		// 6.1 更新公司收益分账户（资金转出）
		earnSubAccount.setAccountAmount(ArithUtil.sub(earnSubAccount.getAccountAmount(), transferAmount));
		
		// 6.2 记录公司收益分账户流水(资金转出)
		accountFlowService.saveAccountFlow(earnMainAccount, earnSubAccount, accountInfoEntity, subAccountInfoEntity, "4", 
				tradeType, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, 
				transferAmount, subjectType, 
				Constant.TABLE_BAO_T_ATONE_INFO, atoneInfoEntity.getId(), SubjectConstant.SUBJECT_TYPE_AMOUNT);

		// 6.3 记录用户分账户流水（现金入）
		subAccountInfoEntity.setAccountAmount(ArithUtil.add(subAccountInfoEntity.getAccountAmount(), transferAmount));
		
		accountFlowService.saveAccountFlow(accountInfoEntity, subAccountInfoEntity, earnMainAccount, earnSubAccount, "4", 
				tradeType, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING, 
				transferAmount, subjectType, 
				Constant.TABLE_BAO_T_ATONE_INFO, atoneInfoEntity.getId(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
	}
	
	/**
	 * 用户手续费/计提风险金/账户管理费
	 *
	 * @author  wangjf
	 * @date    2015年8月17日 下午2:08:24
	 * @param accountInfoEntity
	 * @param subAccountInfoEntity
	 * @param earnMainAccount
	 * @param earnSubAccount
	 * @param transferAmount
	 * @param reqeustNo
	 * @param atoneInfoEntity
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public void saveUserExpense(AccountInfoEntity accountInfoEntity, SubAccountInfoEntity subAccountInfoEntity, 
			AccountInfoEntity earnMainAccount, SubAccountInfoEntity earnSubAccount, BigDecimal transferAmount, 
			String reqeustNo, AtoneInfoEntity atoneInfoEntity, String tradeType, String subjectType){
	
		// 7.1 更新用户分账户(资金转出)
		subAccountInfoEntity.setAccountAmount(ArithUtil.sub(subAccountInfoEntity.getAccountAmount(), transferAmount));
		accountFlowService.saveAccountFlow(accountInfoEntity, subAccountInfoEntity, earnMainAccount, earnSubAccount, "4", 
				tradeType, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, 
				transferAmount, subjectType, 
				Constant.TABLE_BAO_T_ATONE_INFO, atoneInfoEntity.getId(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
		
		// 7.2 更新收益分账户（资金转入）
		earnSubAccount.setAccountAmount(ArithUtil.add(earnSubAccount.getAccountAmount(), transferAmount));		
		accountFlowService.saveAccountFlow(earnMainAccount, earnSubAccount, accountInfoEntity, subAccountInfoEntity, "4", 
				tradeType, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING, 
				transferAmount, subjectType, 
				Constant.TABLE_BAO_T_ATONE_INFO, atoneInfoEntity.getId(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
			
		// 7.3 更新收益分账户（资金转出）
		earnSubAccount.setAccountAmount(ArithUtil.sub(earnSubAccount.getAccountAmount(), transferAmount));
		accountFlowService.saveAccountFlow(earnMainAccount, earnSubAccount, earnMainAccount, earnSubAccount, "3", 
				tradeType, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, 
				transferAmount, subjectType, 
				Constant.TABLE_BAO_T_ATONE_INFO, atoneInfoEntity.getId(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
		
		// 7.4 更新收益主账户（资金转入）
		earnMainAccount.setAccountTotalAmount(ArithUtil.add(earnMainAccount.getAccountTotalAmount(), transferAmount));
		earnMainAccount.setAccountAvailableAmount(ArithUtil.add(earnMainAccount.getAccountAvailableAmount(), transferAmount));
		accountFlowService.saveAccountFlow(earnMainAccount, earnSubAccount, earnMainAccount, earnSubAccount, "2", 
				tradeType, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING, 
				transferAmount, subjectType, 
				Constant.TABLE_BAO_T_ATONE_INFO, atoneInfoEntity.getId(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
	}
	
	/**
	 * 用户收益
	 *
	 * @author  wangjf
	 * @date    2015年8月17日 下午2:26:01
	 * @param accountInfoEntity
	 * @param subAccountInfoEntity
	 * @param earnMainAccount
	 * @param earnSubAccount
	 * @param transferAmount
	 * @param reqeustNo
	 * @param atoneInfoEntity
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public void saveUserIncome(AccountInfoEntity accountInfoEntity, SubAccountInfoEntity subAccountInfoEntity, 
			AccountInfoEntity earnMainAccount, SubAccountInfoEntity earnSubAccount, BigDecimal investAmount,  
			BigDecimal transferAmount, String reqeustNo, AtoneInfoEntity atoneInfoEntity, 
			String tradeType, String subjectType, InvestInfoEntity investInfoEntity) {
		
		// 7.1 更新用户分账户(资金转出)
		subAccountInfoEntity.setAccountAmount(ArithUtil.sub(subAccountInfoEntity.getAccountAmount(), transferAmount));
		accountFlowService.saveAccountFlow(accountInfoEntity, subAccountInfoEntity, earnMainAccount, earnSubAccount, "4", 
				tradeType, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, 
				transferAmount, subjectType, 
				Constant.TABLE_BAO_T_ATONE_INFO, atoneInfoEntity.getId(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
				
		// 7.2  更新用户主账户(资金转入)
		accountInfoEntity.setAccountTotalAmount(ArithUtil.add(accountInfoEntity.getAccountTotalAmount(), transferAmount));
		accountInfoEntity.setAccountAvailableAmount(ArithUtil.add(accountInfoEntity.getAccountAvailableAmount(), transferAmount));
		AccountFlowInfoEntity accountFlowInfoEntity = accountFlowService.saveAccountFlow(accountInfoEntity, subAccountInfoEntity, accountInfoEntity, subAccountInfoEntity, "2", 
				tradeType, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING, 
				transferAmount, subjectType, 
				Constant.TABLE_BAO_T_ATONE_INFO, atoneInfoEntity.getId(), SubjectConstant.SUBJECT_TYPE_AMOUNT);	
		ProductInfoEntity productInfoEntity = productInfoRepository.findOne(investInfoEntity.getProductId());
		//ProductDetailInfoEntity productDetailInfoEntity = productDetailInfoRepository.findByProductId(productInfoEntity.getId());
		accountFlowInfoEntity.setMemo(String.format("%s%s期", productInfoEntity.getProductName(), investInfoEntity.getCurrTerm()));
	}
	
	/**
	 * 记录两笔单独流水（赎回和收益）
	 *
	 * @author  wangjf
	 * @date    2015年8月22日 上午10:10:08
	 * @param accountInfoEntity
	 * @param subAccountInfoEntity
	 * @param earnMainAccount
	 * @param earnSubAccount
	 * @param atoneAmount
	 * @param incomeAmount
	 * @param reqeustNo
	 * @param atoneInfoEntity
	 */
	public void saveUserAtone(AccountInfoEntity accountInfoEntity, SubAccountInfoEntity subAccountInfoEntity, 
			AccountInfoEntity earnMainAccount, SubAccountInfoEntity earnSubAccount, BigDecimal atoneAmount,  
			BigDecimal incomeAmount, String reqeustNo, AtoneInfoEntity atoneInfoEntity) {
		
		// 1) 记录赎回流水
		accountFlowService.saveAccountFlow(accountInfoEntity, subAccountInfoEntity, earnMainAccount, earnSubAccount, "4", 
				SubjectConstant.TRADE_FLOW_TYPE_ATONE_TERM, reqeustNo, "--", 
				atoneAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_TERM, 
				Constant.TABLE_BAO_T_ATONE_INFO, atoneInfoEntity.getId(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
		
		// 2) 记录收益流水
		accountFlowService.saveAccountFlow(accountInfoEntity, subAccountInfoEntity, earnMainAccount, earnSubAccount, "4", 
				SubjectConstant.TRADE_FLOW_TYPE_ATONE_INCOME, reqeustNo, "--", 
				incomeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_INCOME, 
				Constant.TABLE_BAO_T_ATONE_INFO, atoneInfoEntity.getId(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
	}

	@Override
	public Map<String, Object> findAdvancedAtone(Map<String, Object> params) {
		String investId = (String)params.get("investId");
		
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("investTitle", "");
		result.put("investAmount", "0");
		result.put("exceptAmount", "0");
		result.put("expense", "0");
		
		// 查询投资记录
		InvestInfoEntity investInfoEntity = investInfoRepository.findOne(investId);
		if(investInfoEntity == null) {
			log.error("该笔投资{}不存在", investId);
			return result;
		}
		
		// 查询产品记录
		ProductInfoEntity productInfoEntity = productInfoRepository.findOne(investInfoEntity.getProductId());
		if(productInfoEntity == null) {
			log.error("该产品{}不存在", investInfoEntity.getProductId());
			return result;
		}
		
		// 计算投资天数
		Date now = new Date();
		int investDay = DateUtils.datePhaseDiffer(DateUtils.parseDate(investInfoEntity.getInvestDate(), "yyyyMMdd"), now);
		
		// 判断投资天数是否大于封闭期（封闭月数*30）
		if(productInfoEntity.getSeatTerm() == null) {
			log.error("该产品{}不允许提前赎回!", productInfoEntity.getProductName());
			return result;
		}
		Date seatDate = DateUtils.getAfterMonthNext(DateUtils.parseDate(investInfoEntity.getInvestDate(), "yyyyMMdd"), productInfoEntity.getSeatTerm().intValue());
		if(now.compareTo(seatDate) <= 0) {
			log.error("该笔投资{}在封闭期内，不允许赎回！", investId);
			return result;
		}
		
		// 获取产品利率
		ProductRateInfoEntity productRateInfoEntity = null;
		List<ProductRateInfoEntity> rateList = productRateInfoRepository.findProductRateInfoByProductId(investInfoEntity.getProductId());
		for(ProductRateInfoEntity rate : rateList){
//			if(rate.getLowerLimitDay() <= investDay && investDay <= rate.getUpperLimitDay()) {
//			productRateInfoEntity = rate;
//			break;
//		}
			productRateInfoEntity = rate;
		}
		if(productRateInfoEntity == null) {
			log.error("未找到该产品{0}合适的利率", productInfoEntity.getProductName());
			return result;
		}
		
		// 预期收益：投资金额*年利率/365*实际天数
		// 手续费：投资金额*30%
		// 预计回收：投资金额+预期收益-手续费
		BigDecimal exceptInterest = ArithUtil.div(ArithUtil.mul(ArithUtil.mul(investInfoEntity.getInvestAmount(), productRateInfoEntity.getYearRate()), new BigDecimal(String.valueOf(investDay))), new BigDecimal("365"));
		BigDecimal manageExpenses = ArithUtil.mul(investInfoEntity.getInvestAmount(), productInfoEntity.getQuitRate());
		BigDecimal factIncome = ArithUtil.sub(ArithUtil.add(investInfoEntity.getInvestAmount(), exceptInterest), manageExpenses);
		
		// 精确到小数点后八位
		manageExpenses = ArithUtil.formatScale(manageExpenses, 2);
		factIncome = ArithUtil.formatScale(factIncome, 2);
		
		result.put("investTitle", String.format("%s%s期", productInfoEntity.getProductName(), investInfoEntity.getCurrTerm()));
		result.put("investAmount", investInfoEntity.getInvestAmount());
		result.put("exceptAmount", factIncome);
		result.put("expense", manageExpenses);
		
		return result;
	}
}
