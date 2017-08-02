/** 
 * @(#)InvestService.java 1.0.0 2015年4月24日 下午2:11:13  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AccountFlowInfoEntity;
import com.slfinance.shanlincaifu.entity.AccountInfoEntity;
import com.slfinance.shanlincaifu.entity.AtoneDetailInfoEntity;
import com.slfinance.shanlincaifu.entity.AtoneInfoEntity;
import com.slfinance.shanlincaifu.entity.AuditInfoEntity;
import com.slfinance.shanlincaifu.entity.CustActivityInfoEntity;
import com.slfinance.shanlincaifu.entity.CustInfoEntity;
import com.slfinance.shanlincaifu.entity.DeviceInfoEntity;
import com.slfinance.shanlincaifu.entity.EmptionInfoEntity;
import com.slfinance.shanlincaifu.entity.InvestDetailInfoEntity;
import com.slfinance.shanlincaifu.entity.InvestInfoEntity;
import com.slfinance.shanlincaifu.entity.JobRunListenerEntity;
import com.slfinance.shanlincaifu.entity.LogInfoEntity;
import com.slfinance.shanlincaifu.entity.ProductDetailInfoEntity;
import com.slfinance.shanlincaifu.entity.ProductInfoEntity;
import com.slfinance.shanlincaifu.entity.ProductRateInfoEntity;
import com.slfinance.shanlincaifu.entity.SubAccountInfoEntity;
import com.slfinance.shanlincaifu.entity.SystemMessageInfoEntity;
import com.slfinance.shanlincaifu.job.DailySettlementJob;
import com.slfinance.shanlincaifu.repository.AccountFlowInfoRepository;
import com.slfinance.shanlincaifu.repository.AccountInfoRepository;
import com.slfinance.shanlincaifu.repository.ActivityInfoRepository;
import com.slfinance.shanlincaifu.repository.AtoneInfoRepository;
import com.slfinance.shanlincaifu.repository.AuditInfoRespository;
import com.slfinance.shanlincaifu.repository.CustActivityInfoRepository;
import com.slfinance.shanlincaifu.repository.CustInfoRepository;
import com.slfinance.shanlincaifu.repository.DeviceInfoRepository;
import com.slfinance.shanlincaifu.repository.EmptionInfoRepository;
import com.slfinance.shanlincaifu.repository.InvestDetailInfoRepository;
import com.slfinance.shanlincaifu.repository.InvestInfoRepository;
import com.slfinance.shanlincaifu.repository.JobRunListenerRepository;
import com.slfinance.shanlincaifu.repository.LogInfoEntityRepository;
import com.slfinance.shanlincaifu.repository.ProductDetailInfoRepository;
import com.slfinance.shanlincaifu.repository.ProductInfoRepository;
import com.slfinance.shanlincaifu.repository.ProductRateInfoRepository;
import com.slfinance.shanlincaifu.repository.SubAccountInfoRepository;
import com.slfinance.shanlincaifu.repository.SystemMessageInfoRepository;
import com.slfinance.shanlincaifu.repository.custom.AccountInfoRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.AtoneInfoRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.CustActivityInfoRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.FixedInvestRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.InvestInfoRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.LoanInfoRepositoryCustom;
import com.slfinance.shanlincaifu.service.AccessService;
import com.slfinance.shanlincaifu.service.AccountFlowService;
import com.slfinance.shanlincaifu.service.FlowNumberService;
import com.slfinance.shanlincaifu.service.InvestService;
import com.slfinance.shanlincaifu.service.ParamService;
import com.slfinance.shanlincaifu.service.ProductBusinessService;
import com.slfinance.shanlincaifu.utils.ArithUtil;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.SubjectConstant;
import com.slfinance.spring.mail.MailType;
import com.slfinance.vo.ResultVo;

/**   
 * 
 *  
 * @author  HuYaHui
 * @version $Revision:1.0.0, $Date: 2015年4月24日 下午2:11:13 $ 
 */
@Slf4j
@Service("investService")
public class InvestServiceImpl implements InvestService{
	
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
	private ActivityInfoRepository activityInfoRepository;
	
	@Autowired
	private CustActivityInfoRepository custActivityInfoRepository;
	
	@Autowired
	private AccountFlowService accountFlowService;
	
	@Autowired
	private CustActivityInfoRepositoryCustom custActivityInfoRepositoryCustom;
	
	@Autowired
	private JobRunListenerRepository jobRunListenerMapper;
	
	@Autowired
	private DeviceInfoRepository deviceInfoRepository;
	
	@Autowired
	private AccessService accessService;
	
	@Autowired
	private ProductBusinessService productBusinessService;
	
	@Autowired
	private InvestInfoRepositoryCustom investInfoRepositoryCustom;
	
	@Autowired
	private FixedInvestRepositoryCustom fixedInvestRepositoryCustom;
	
	public Map<String, Object> findOwnerList(Map<String, Object> params){
		Map<String,Object> rtnMap=new HashMap<>();
		String productName=params.get("productName")+"";
		Page<Map<String,Object>> page=this.custInfoRepository.findOwnerList(params);
		
		//可投金额,产品详情
		ProductDetailInfoEntity pdi=
				this.productDetailInfoRepository.findProductDetailInfoByProductName(productName);
		//总记录数
		rtnMap.put("iTotalDisplayRecords", page.getTotalElements());
		//总人数
		rtnMap.put("totalInvestPeopleNum", pdi.getAlreadyInvestPeople());
		//总投资金额
		rtnMap.put("totalInvestAmount", pdi.getAlreadyInvestAmount());
		//数据
		rtnMap.put("data", page.getContent());
		return rtnMap;
	}
	
	//@Cacheable(key="#params['productName'] + 'findBAODetail'", value="cache1")
	public Map<String, Object> findBAODetail(Map<String, Object> params){
		Map<String,Object> rtnMap=new HashMap<>();
		String productName=params.get("productName")+"";
		//可投金额,产品详情
		ProductDetailInfoEntity pdi=this.productDetailInfoRepository.findProductDetailInfoByProductName(productName);
		//查询 BAO_T_PRODUCT_INFO BAO产品信息表
		ProductInfoEntity pie=this.productInfoRepository.findProductInfoByProductTypeName(productName);
		if(pdi==null){
			log.info("产品为空！"+productName);
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
		
		//modify by caoyi 2015-05-06
		rtnMap.putAll(productBusinessService.findPartakeForDisplay(productName));
		//rtnMap.put("partakeOrganizs", pdi.getPartakeOrganizs());
		//rtnMap.put("partakeCrerigs", pdi.getPartakeCrerigs());
		rtnMap.put("ensureMethod", Constant.ENSURE_METHOD_01);
		rtnMap.put("alreadyInvestPeople", pdi.getAlreadyInvestPeople());
		rtnMap.put("alreadyInvestAmount", pdi.getAlreadyInvestAmount());
		rtnMap.put("accumulativeLucre", pdi.getAccumulativeLucre());
		
		rtnMap.put("productInfo", pie);
		//BAO_T_PRODUCT_RATE_INFO BAO产品利率信息
		List<ProductRateInfoEntity> prodList=
				this.productRateInfoRepository.findProductRateInfoByTypeName(productName);
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
		
		
//		//累计投资金额	投资表
//		BigDecimal b=investInfoRepository.sumInvestAmountByProductId(pdi.getId());
//		rtnMap.put("investAmount", b==null?BigDecimal.ZERO:b);
		
//		//累计用户赚取
//		String tradeType=SubjectConstant.TRADE_FLOW_TYPE_JOIN;
//		BigDecimal tradeAmount=this.accountFlowInfoRepository.sumTradeAmountByTradeType(tradeType);
//		rtnMap.put("tradeAmount", tradeAmount==null?BigDecimal.ZERO:tradeAmount);
		
		// add by caoyi 2015-07-18 14:06
		//投资笔数
		int investCount = investDetailInfoRepository.findInvestCount(productName);
	    rtnMap.put("investCount", investCount);
	    
		//累计用户赚取
	    if(StringUtils.isEmpty((String)params.get("exclueInCome"))) {
	    	String tradeType=productName+"收益";
			BigDecimal incomeAmount=this.accountFlowInfoRepository.sumTradeAmountByTradeType(tradeType);
			rtnMap.put("incomeAmount", incomeAmount==null?BigDecimal.ZERO:incomeAmount);
	    }

		if(params.get("custId")!=null){
			//已登录用户
			
			//个人可投资金额
			BigDecimal mda=paramService.findMaxWithdrawAmount();
			
			//用户持有份额
			BigDecimal ownerAmount=custInfoRepository.getOwnerAmount(params);
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
		return rtnMap;
	}
	public static void main(String[] args) {
		System.out.println(BigDecimal.valueOf(-1).compareTo(BigDecimal.ZERO));
	}
	
	public Map<String, Object> findByCondition(Map<String, Object> params) {
		Map<String,Object> rtnMap=new HashMap<String, Object>();
		Page<Map<String,Object>> page=productDetailInfoRepository.findByCondition(params);
		for(Map<String,Object> map:page.getContent()){
			map.put("productName", params.get("productName"));
			if(org.springframework.util.StringUtils.isEmpty(map.get("yestTradeAmount"))){
				map.put("yestTradeAmount", "0");
			}else{
				BigDecimal db = new BigDecimal(map.get("yestTradeAmount")+"");
				String ii = db.toPlainString();
				map.put("yestTradeAmount", ii);
			}
			if(org.springframework.util.StringUtils.isEmpty(map.get("tradeAmount"))){
				map.put("tradeAmount", "0");
			}
		}
		rtnMap.put("iTotalDisplayRecords", page.getTotalElements());
		rtnMap.put("data", page.getContent());
		BigDecimal sumAmount=productDetailInfoRepository.countInvestByCondition(params);
		rtnMap.put("totalAmount",sumAmount==null?BigDecimal.ZERO:sumAmount);
		return rtnMap;
	}

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo joinBao(Map<String, Object> params) throws SLException {
		
		String custId = (String)params.get("custId");
		BigDecimal tradeAmount = new BigDecimal((String)params.get("tradeAmount"));
		String appSource = StringUtils.isEmpty((String)params.get("investSource")) ? (String)params.get("appSource") : (String)params.get("investSource");
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
		ProductInfoEntity productInfoEntity = productInfoRepository.findProductInfoByProductTypeName(Constant.PRODUCT_TYPE_01);
		if(productInfoEntity == null) {
			throw new SLException("产品不存在");
		}
		if(productInfoEntity.getInvestMinAmount().compareTo(tradeAmount) > 0 || productInfoEntity.getInvestMaxAmount().compareTo(tradeAmount) < 0){
			throw new SLException(String.format("加入金额不能大于上限%s，且不能小于下限%s", productInfoEntity.getInvestMaxAmount().toString(), productInfoEntity.getInvestMinAmount().toString()));
		}
		
		// 1.3 可投金额验证：判断客户此次投资是否超过剩余可投金额
		ProductDetailInfoEntity productDetailInfoEntity = productDetailInfoRepository.findByProductId(productInfoEntity.getId());
		if(productDetailInfoEntity == null) {
			throw new SLException("产品详情不存在");
		}
		if(productDetailInfoEntity.getCurrUsableValue().compareTo(tradeAmount) < 0){
			throw new SLException(String.format("加入金额为%s，实际剩余可加入金额为%s", tradeAmount.toString(), ArithUtil.formatScale2(productDetailInfoEntity.getCurrUsableValue()).toString()));//update by zhangzs 显示金额截取
		}
		
		// 1.4 验证活期宝产品状态为:开放中
		if(!productInfoEntity.getProductStatus().equals(Constant.PRODUCT_STATUS_BID_ING)){
			throw new SLException("产品状态非开放中，不能加入！");
		}
		
		// 1.5 验证总投资金额是否大于个人投资上限
		BigDecimal investAmounts = subAccountInfoRepository.queryUserTotalValue(custId, productInfoEntity.getId(), Constant.VALID_STATUS_VALID);
		BigDecimal maxWithdrawAmount = paramService.findMaxWithdrawAmount();
		if(ArithUtil.add(investAmounts, tradeAmount).compareTo(maxWithdrawAmount) > 0){
			throw new SLException(String.format("加入金额为%s，个人累积最大可加入金额为%s, 实际剩余可加入金额为%s", tradeAmount.toString(), maxWithdrawAmount.toString(), ArithUtil.sub(maxWithdrawAmount, investAmounts).toString()));
		}

		// 2、验证用户活期宝是否开户
		BigDecimal countInvestTimes = investInfoRepository.countInvestInfoByCustId(custId, productInfoEntity.getId());
		
		// 3、更新活期宝募集表
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
		// 判断当前时间是否是15:00:00前，若是则取投资日期为当天，否则取投资时间为当天+1
		String paramTime = paramService.findInvestValidTime();//系统设置的时间
		String investDate = "";
		if(DateUtils.getCurrentDate("HH:mm:ss").compareTo(paramTime) < 0){ // 小于生效时间
			investDate = DateUtils.formatDate(new Date(), "yyyyMMdd");
		}
		else {
			investDate = DateUtils.formatDate(DateUtils.getAfterDay(new Date(), 1), "yyyyMMdd");
		}
		
		InvestInfoEntity newInvestInfoEntity = null;
		SubAccountInfoEntity subAccountInfoEntity = null;
		String tradeCode = numberService.generateLoanContractNumber();
		if(countInvestTimes.compareTo(new BigDecimal("0")) == 0){// 投资次数为0为新投资，直接新增投资记录
			newInvestInfoEntity = saveInvest(null, custId, productInfoEntity.getId(), tradeAmount, investDate, "", tradeCode, true, appSource);
			subAccountInfoEntity = saveSubAccountInfo(custId, accountInfoEntity.getId(), newInvestInfoEntity.getId());
		}
		else { // 投资次数不为0，需判断之前有没有投资，有则新加详情，否则新建一笔投资
			InvestInfoEntity investInfoEntity = investInfoRepository.findInvestInfoByCustIdAndInvestDate(custId, productInfoEntity.getId(), investDate, Constant.VALID_STATUS_VALID);
			if(investInfoEntity == null){
				newInvestInfoEntity = saveInvest(null, custId, productInfoEntity.getId(), tradeAmount, investDate, "", tradeCode, true, appSource);
				subAccountInfoEntity = saveSubAccountInfo(custId, accountInfoEntity.getId(), newInvestInfoEntity.getId());
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
		accountFlowService.saveAccountFlow(accountInfoEntity, subAccountInfoEntity, accountInfoEntity, subAccountInfoEntity, "2", 
				SubjectConstant.TRADE_FLOW_TYPE_JOIN, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, 
				tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_JOIN, 
				subAccountInfoEntity.getRelateType(), subAccountInfoEntity.getRelatePrimary(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
		
		// 6.2 更新用户分账户(分账户现金转入)
		subAccountInfoEntity.setAccountAmount(ArithUtil.add(subAccountInfoEntity.getAccountAmount(), tradeAmount));
		accountFlowService.saveAccountFlow(accountInfoEntity, subAccountInfoEntity, accountInfoEntity, subAccountInfoEntity, "3", 
				SubjectConstant.TRADE_FLOW_TYPE_JOIN, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING, 
				tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_JOIN, 
				subAccountInfoEntity.getRelateType(), subAccountInfoEntity.getRelatePrimary(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
		
		// 7、判断债权价值来源(公司分账户债权价值——>客户分账户，客户分账户现金——>公司分账户)
		SubAccountInfoEntity earnSubAccount = subAccountInfoRepository.findBySubAccountNo(Constant.SUB_ACCOUNT_NO_ERAN); // 公司收益分账户
		if(earnSubAccount == null || earnSubAccount.getAccountAvailableValue() == null) {
			throw new SLException("未初始化公司收益分账户！");
		}
		SubAccountInfoEntity centerSubAccount = subAccountInfoRepository.findBySubAccountNo(Constant.SUB_ACCOUNT_NO_CENTER); // 公司居间人分账户
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
		/*
		 1.首先查找公司活期宝收益分账户，如果分账户中债权价值大于>0,则先从公司收益分账户购买债权价值
		 2.如果公司收益账户价值不够，再从最大债权人账户购买债权价值
		 3.购买公司收益分账户中的债券价值，不要提取保证金。
		 4.购买最大债权人的价值，需要提取保证金
		 5.提取保证金的比例，可配置，配置在参数表当中(前期可能配置为0,公司每天保持固定额度给活期宝提现用)
		 * 
		 */
		boolean fixLimited = paramService.findFixLimited();// 提现保证金是否固定额度（true固定，false比例）
		BigDecimal fixLimitedAmount = paramService.findFixLimitedAmount();// 固定额度提现保证金
		BigDecimal fixLimitedScalce = paramService.findFixLimitedScalce();// 按比例提现保证金
		if(earnSubAccount.getAccountAvailableValue().compareTo(new BigDecimal("0")) > 0){ // 从收益账户购买
			
			if(earnSubAccount.getAccountAvailableValue().compareTo(tradeAmount) >= 0){// 若收益账户足额
				
				// a.更新公司收益分账户(价值——>现金)
				saveEarnAccount(accountInfoEntity, subAccountInfoEntity, earnMainAccount, earnSubAccount, custId, reqeustNo, tradeAmount, fixLimited, fixLimitedAmount, fixLimitedScalce);
				
				// b.更新用户分账户(现金——>价值)
				saveJoinUserAccount(accountInfoEntity, subAccountInfoEntity, earnMainAccount, earnSubAccount, tradeAmount, reqeustNo);
			} 
			else {// 若收益账户不足额，则部分从收益账户购买，部分从居间人账户购买
				
				// 应从收益账户购买金额
				BigDecimal earnCanJoinAmount = earnSubAccount.getAccountAvailableValue();
				// 应从居间人账户购买金额
				BigDecimal centerCanJoinAmount = ArithUtil.sub(tradeAmount, earnCanJoinAmount);
				
				// a.更新公司收益分账户(价值——>现金)
				saveEarnAccount(accountInfoEntity, subAccountInfoEntity, earnMainAccount, earnSubAccount, custId, reqeustNo, earnCanJoinAmount, fixLimited, fixLimitedAmount, fixLimitedScalce);
				
				// b.更新用户分账户(现金——>价值)
				saveJoinUserAccount(accountInfoEntity, subAccountInfoEntity, earnMainAccount, earnSubAccount, earnCanJoinAmount, reqeustNo);
				
				// c.更新公司居间人分账户(价值——>现金)
				saveCenterAccount(accountInfoEntity, subAccountInfoEntity, centerMainAccount, centerSubAccount, earnMainAccount, earnSubAccount, custId, reqeustNo, centerCanJoinAmount, fixLimited, fixLimitedAmount, fixLimitedScalce);
				
				// d.更新用户分账户(现金——>价值)
				saveJoinUserAccount(accountInfoEntity, subAccountInfoEntity, centerMainAccount, centerSubAccount, centerCanJoinAmount, reqeustNo);
			}
		}
		else { // 从居间人账户购买
			
			// a.更新公司居间人分账户(价值——>现金)
			saveCenterAccount(accountInfoEntity, subAccountInfoEntity, centerMainAccount, centerSubAccount, earnMainAccount, earnSubAccount, custId, reqeustNo, tradeAmount, fixLimited, fixLimitedAmount, fixLimitedScalce);
			
			// b.更新用户分账户(现金——>价值)
			saveJoinUserAccount(accountInfoEntity, subAccountInfoEntity, centerMainAccount, centerSubAccount, tradeAmount, reqeustNo);
		}
		
		// 8、发送通知
		//sendEmail(custInfoRepository.findOne(custId), "", "加入活期宝", "");
		
		// 9、记录日志
		LogInfoEntity logInfoEntity = new LogInfoEntity();
		logInfoEntity.setRelateType(Constant.TABLE_BAO_T_INVEST_INFO);
		logInfoEntity.setRelatePrimary(newInvestInfoEntity.getId());
		logInfoEntity.setLogType(Constant.OPERATION_TYPE_08);
		logInfoEntity.setOperDesc("");
		logInfoEntity.setOperPerson(custId);
		logInfoEntity.setOperIpaddress((String)params.get("ipAddress"));
		logInfoEntity.setMemo(String.format("%s购买活期宝，投资金额%s", custInfoEntity.getLoginName(), tradeAmount.toString()));
		logInfoEntity.setBasicModelProperty(custId, true);
		logInfoEntityRepository.save(logInfoEntity);
		
		// 记录设备信息
		if(params.containsKey("channelNo")) {
			DeviceInfoEntity deviceInfoEntity = new DeviceInfoEntity();
			deviceInfoEntity.setRelateTableIdentification(Constant.TABLE_BAO_T_INVEST_INFO);
			deviceInfoEntity.setRelatePrimary(newInvestInfoEntity.getId());
			deviceInfoEntity.setCustId(custId);
			deviceInfoEntity.setTradeType(Constant.OPERATION_TYPE_08);
			deviceInfoEntity.setMeId((String)params.get("meId"));
			deviceInfoEntity.setMeVersion((String)params.get("meVersion"));
			deviceInfoEntity.setAppSource(appSource);
			deviceInfoEntity.setChannelNo(paramService.findByChannelNo((String)params.get("channelNo")));
			deviceInfoEntity.setBasicModelProperty(custId, true);
			deviceInfoRepository.save(deviceInfoEntity);
		}
		
		//// 10、生成推荐奖励
		//// 注：根据用户推广级别，取推荐奖励，当级别<8时，取1~当前级别总和;当级别大于等于8时，取1～8级别总和
		//if(!StringUtils.isEmpty(custInfoEntity.getInviteOriginId()) // 用户受邀邀请码不为空时才需计算推荐奖励
		//		&& !StringUtils.isEmpty(custInfoEntity.getSpreadLevel()) // 推广级别不能为空
		//		&& !custInfoEntity.getSpreadLevel().equals("0")          // 并且推广级别非0
		//		&& countInvestTimes.compareTo(new BigDecimal("0")) == 0){ // 第一次投资
		//	
		//	ActivityInfoEntity activityInfoEntity = activityInfoRepository.findByActId(Constant.ACTIVITY_ID_REGIST_02, Constant.VALID_STATUS_VALID, new Date());
		//	if(activityInfoEntity != null) { // 活动不存在或者已结束，不再生成奖励
		//		
		//		// 插入被推荐人（当前投资人）奖励（之前所有推荐人奖励总和）
		//		String startLevel = "1";
		//		String endLevel = (Integer.valueOf(custInfoEntity.getSpreadLevel()) >= 8 ? "8" : custInfoEntity.getSpreadLevel());
		//		BigDecimal activityRecommend = paramService.findActivityRecommend(startLevel, endLevel);
		//		CustActivityInfoEntity custActivityInfoEntity = new CustActivityInfoEntity();
		//		custActivityInfoEntity.setCustId(custId);
		//		custActivityInfoEntity.setActivityId(activityInfoEntity.getId());
		//		custActivityInfoEntity.setActivitySource(Constant.ACTIVITY_SOURCE_02);
		//		custActivityInfoEntity.setTotalAmount(activityRecommend);
		//		custActivityInfoEntity.setUsableAmount(activityRecommend);
		//		custActivityInfoEntity.setTradeCode(tradeCode);
		//		custActivityInfoEntity.setTradeStatus(Constant.USER_ACTIVITY_TRADE_STATUS_06);
		//		custActivityInfoEntity.setStartDate(activityInfoEntity.getStartDate());
		//		custActivityInfoEntity.setExpireDate(activityInfoEntity.getExpireDate());
		//		custActivityInfoEntity.setRewardShape(Constant.REAWARD_SPREAD_02);
		//		custActivityInfoEntity.setBasicModelProperty(custId, true);
		//		custActivityInfoEntity = custActivityInfoRepository.save(custActivityInfoEntity);
		//		
		//		// 插入推荐人奖励明细
		//		// 通过被推荐人的ID递归找到所有父节点（8层以内的），按照级别依此给予奖励
		//		Map<String, Object> map = new HashMap<String, Object>();
		//		map.put("custId", custId);
		//		map.put("custSpreadLevel", custInfoEntity.getSpreadLevel());				
		//		map.put("queryPermission", custInfoEntity.getQueryPermission());
		//		List<Map<String, Object>> custActivityDetailList = custActivityInfoRepositoryCustom.caclCustActivityDetail(map);
		//		List<CustActivityDetailEntity> custActivityDetailEntityList = new ArrayList<CustActivityDetailEntity>();
		//		for(Map<String, Object> m : custActivityDetailList) {
		//			CustActivityDetailEntity custActivityDetailEntity = new CustActivityDetailEntity();
		//			custActivityDetailEntity.setCustId((String)m.get("custId"));
		//			custActivityDetailEntity.setCustActivityId(custActivityInfoEntity.getId());
		//			custActivityDetailEntity.setTradeAmount(new BigDecimal(m.get("tradeAmount").toString()));
		//			custActivityDetailEntity.setUsableAmount(new BigDecimal(m.get("tradeAmount").toString()));
		//			custActivityDetailEntity.setTradeStatus(Constant.USER_ACTIVITY_TRADE_STATUS_06);
		//			custActivityDetailEntity.setBasicModelProperty(custId, true);
		//			custActivityDetailEntityList.add(custActivityDetailEntity);
		//		}
		//		custActivityInfoRepositoryCustom.batchInsertActivityDetail(custActivityDetailEntityList);
		//	}
		//}
		
		return new ResultVo(true);
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
	 * 收益账户处理
	 *
	 * @author  wangjf
	 * @date    2015年4月29日 下午7:17:22
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
	public void saveEarnAccount(AccountInfoEntity custAccount, SubAccountInfoEntity subAccountInfoEntity, 
			AccountInfoEntity earnMainAccount, SubAccountInfoEntity earnSubAccount, String custId,
			String reqeustNo, BigDecimal tradeAmount, Boolean fixLimited, BigDecimal fixLimitedAmount, BigDecimal fixLimitedScalce)
	{
		// 7.1 更新收益分账户（价值转出）
		earnSubAccount.setAccountTotalValue(ArithUtil.sub(earnSubAccount.getAccountTotalValue(), tradeAmount));
		earnSubAccount.setAccountAvailableValue(ArithUtil.sub(earnSubAccount.getAccountAvailableValue(), tradeAmount));
		accountFlowService.saveAccountFlow(earnMainAccount, earnSubAccount, custAccount, subAccountInfoEntity, "4", 
				SubjectConstant.TRADE_FLOW_TYPE_JOIN, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, 
				tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_JOIN, 
				subAccountInfoEntity.getRelateType(), subAccountInfoEntity.getRelatePrimary(), SubjectConstant.SUBJECT_TYPE_VALUE);
		
		// 7.2 更新收益分账户（资金转入）
		earnSubAccount.setAccountAmount(ArithUtil.add(earnSubAccount.getAccountAmount(), tradeAmount));
		earnSubAccount.setBasicModelProperty(custId, false);
		accountFlowService.saveAccountFlow(earnMainAccount, earnSubAccount, custAccount, subAccountInfoEntity, "4", 
				SubjectConstant.TRADE_FLOW_TYPE_JOIN, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING, 
				tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_JOIN, 
				subAccountInfoEntity.getRelateType(), subAccountInfoEntity.getRelatePrimary(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
		
		// 7.3 如果是固定额度，需判断提现保证金是否超过固定额度，若超过需更新至主账户
		if(fixLimited){ // 按固定额度
			if(earnSubAccount.getAccountAmount().compareTo(fixLimitedAmount) > 0){
				// 可转入收益主账户金额
				BigDecimal transferAmount = ArithUtil.sub(earnSubAccount.getAccountAmount(), fixLimitedAmount);
				// 7.3.1 更新收益分账户(资金转出)
				earnSubAccount.setAccountAmount(ArithUtil.sub(earnSubAccount.getAccountAmount(), transferAmount));
				earnSubAccount.setBasicModelProperty(custId, false);
				accountFlowService.saveAccountFlow(earnMainAccount, earnSubAccount, earnMainAccount, earnSubAccount, "3", 
						SubjectConstant.TRADE_FLOW_TYPE_WITHDRAW_LIMITED, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, 
						transferAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_WITHDRAW_LIMITED, 
						subAccountInfoEntity.getRelateType(), subAccountInfoEntity.getRelatePrimary(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
				
				// 7.3.2 更新收益主账户（价值转入）
				earnMainAccount.setAccountTotalAmount(ArithUtil.add(earnMainAccount.getAccountTotalAmount(), transferAmount));
				earnMainAccount.setAccountAvailableAmount(ArithUtil.add(earnMainAccount.getAccountAvailableAmount(), transferAmount));
				earnMainAccount.setBasicModelProperty(custId, false);
				accountFlowService.saveAccountFlow(earnMainAccount, earnSubAccount, earnMainAccount, earnSubAccount, "2", 
						SubjectConstant.TRADE_FLOW_TYPE_WITHDRAW_LIMITED, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING, 
						transferAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_WITHDRAW_LIMITED, 
						subAccountInfoEntity.getRelateType(), subAccountInfoEntity.getRelatePrimary(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
			}
		} else { // 按比例
			// 暂不处理
		}
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
			AccountInfoEntity centerMainAccount, SubAccountInfoEntity centerSubAccount, 
			AccountInfoEntity earnMainAccount, SubAccountInfoEntity earnSubAccount, String custId,
			String reqeustNo, BigDecimal tradeAmount, Boolean fixLimited, BigDecimal fixLimitedAmount, BigDecimal fixLimitedScalce)
	{
		// 7.1 更新居间人分账户(价值转出)
		centerSubAccount.setAccountTotalValue(ArithUtil.sub(centerSubAccount.getAccountTotalValue(), tradeAmount));
		centerSubAccount.setAccountAvailableValue(ArithUtil.sub(centerSubAccount.getAccountAvailableValue(), tradeAmount));
		accountFlowService.saveAccountFlow(centerMainAccount, centerSubAccount, custAccount, subAccountInfoEntity, "4", 
				SubjectConstant.TRADE_FLOW_TYPE_JOIN, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, 
				tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_JOIN, 
				subAccountInfoEntity.getRelateType(), subAccountInfoEntity.getRelatePrimary(), SubjectConstant.SUBJECT_TYPE_VALUE);
		
		// 7.2 更新居间人分账户（资金转入）
		centerSubAccount.setAccountAmount(ArithUtil.add(centerSubAccount.getAccountAmount(), tradeAmount));
		centerSubAccount.setBasicModelProperty(custId, false);		
		accountFlowService.saveAccountFlow(centerMainAccount, centerSubAccount, custAccount, subAccountInfoEntity, "4", 
				SubjectConstant.TRADE_FLOW_TYPE_JOIN, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING, 
				tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_JOIN, 
				subAccountInfoEntity.getRelateType(), subAccountInfoEntity.getRelatePrimary(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
		
		// 7.3 记录提现保证金
		BigDecimal transferAmount = tradeAmount;
		if(!fixLimited){ // 按比例
			BigDecimal limitedAmount = ArithUtil.mul(tradeAmount, fixLimitedScalce);
			transferAmount = ArithUtil.sub(tradeAmount, limitedAmount);
			
			// 7.3.1 更新居间人分账户(资金转出)
			centerSubAccount.setAccountAmount(ArithUtil.sub(centerSubAccount.getAccountAmount(), limitedAmount));
			accountFlowService.saveAccountFlow(centerMainAccount, centerSubAccount, earnMainAccount, earnSubAccount, "3", 
					SubjectConstant.TRADE_FLOW_TYPE_WITHDRAW_LIMITED, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, 
					limitedAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_WITHDRAW_LIMITED, 
					subAccountInfoEntity.getRelateType(), subAccountInfoEntity.getRelatePrimary(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
			
			// 7.3.2 更新收益主账户（资金转入）
			earnMainAccount.setAccountTotalAmount(ArithUtil.add(centerMainAccount.getAccountTotalAmount(), limitedAmount));
			earnMainAccount.setAccountAvailableAmount(ArithUtil.add(centerMainAccount.getAccountAvailableAmount(), limitedAmount));
			earnMainAccount.setBasicModelProperty(custId, false);
			accountFlowService.saveAccountFlow(earnMainAccount, earnSubAccount, centerMainAccount, centerSubAccount, "2", 
					SubjectConstant.TRADE_FLOW_TYPE_WITHDRAW_LIMITED, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING, 
					limitedAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_WITHDRAW_LIMITED, 
					subAccountInfoEntity.getRelateType(), subAccountInfoEntity.getRelatePrimary(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
		}
		
		// 7.4 更新与记录居间人主账户
		// 7.4.1 更新居间人分账户（资金转出）
		centerSubAccount.setAccountAmount(ArithUtil.sub(centerSubAccount.getAccountAmount(), transferAmount));
		centerSubAccount.setBasicModelProperty(custId, false);
		accountFlowService.saveAccountFlow(centerMainAccount, centerSubAccount, centerMainAccount, centerSubAccount, "3", 
				SubjectConstant.TRADE_FLOW_TYPE_JOIN, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, 
				transferAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_JOIN, 
				subAccountInfoEntity.getRelateType(), subAccountInfoEntity.getRelatePrimary(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
		
		// 7.4.2 更新居间人主账户（资金转入）
		centerMainAccount.setAccountTotalAmount(ArithUtil.add(centerMainAccount.getAccountTotalAmount(), transferAmount));
		centerMainAccount.setAccountAvailableAmount(ArithUtil.add(centerMainAccount.getAccountAvailableAmount(), transferAmount));
		centerMainAccount.setBasicModelProperty(custId, false);
		accountFlowService.saveAccountFlow(centerMainAccount, centerSubAccount, centerMainAccount, centerSubAccount, "2", 
				SubjectConstant.TRADE_FLOW_TYPE_JOIN, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING, 
				transferAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_JOIN, 
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
				SubjectConstant.TRADE_FLOW_TYPE_JOIN, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, 
				tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_JOIN, 
				subAccountInfoEntity.getRelateType(), subAccountInfoEntity.getRelatePrimary(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
		
		// 8.2 记录用户分账流水（价值转入）
		subAccountInfoEntity.setAccountTotalValue(ArithUtil.add(subAccountInfoEntity.getAccountTotalValue(), tradeAmount));
		subAccountInfoEntity.setAccountAvailableValue(ArithUtil.add(subAccountInfoEntity.getAccountAvailableValue(), tradeAmount));
		subAccountInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
		accountFlowService.saveAccountFlow(custAccount, subAccountInfoEntity, companyCustAccount, companySubAccountInfoEntity, "4", 
				SubjectConstant.TRADE_FLOW_TYPE_JOIN, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING, 
				tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_JOIN, 
				subAccountInfoEntity.getRelateType(), subAccountInfoEntity.getRelatePrimary(), SubjectConstant.SUBJECT_TYPE_VALUE);
	}

	/**
	 * 赎回活期宝（快速赎回）
	 *
	 * @author  wangjf
	 * @date    2015年4月30日 上午11:40:01
	 * @param params
	 		<tt>custId： String:用户ID</tt><br>
	  		<tt>tradeAmount： String:赎回金额</tt><br>
	  		<tt>tradePassword： String:交易密码</tt><br>
	 * @return
	 * @throws SLException
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo withdrawBao(Map<String, Object> params) throws SLException {
		
		JobRunListenerEntity jobRunListener = jobRunListenerMapper.findByJobClassName(DailySettlementJob.class.getName());
		if ("运行中".equals(jobRunListener.getExecuteStatus())) {
			return new ResultVo(false, "系统正在结息，暂不可做赎回操作，请稍后再试！");
		}
		
		String custId = (String)params.get("custId");
		BigDecimal tradeAmount = new BigDecimal((String)params.get("tradeAmount"));
		String tradePassword = (String)params.get("tradePassword");
		String appSource = (String)params.get("appSource"); // 来源
		
		// 每天最大可赎回金额
		BigDecimal maxDayWithdrawAmount = paramService.findMaxDayWithdrawAmount();
		// 每天最大可赎回次数 
		long maxDayWithdrawCount = paramService.findMaxDayWithdrawCount();
		// 每月最大可赎回次数
		long maxMonthWithdrawCount = paramService.findMaxMonthWithdrawCount();
		
		// 1 业务验证
		// 1.0 验证用户状态
		CustInfoEntity custInfoEntity = custInfoRepository.findOne(custId);
		if(!custInfoEntity.getEnableStatus().equals(Constant.ENABLE_STATUS_01)){
			throw new SLException("账户为非正常状态，可能被冻结，请联系客服！");
		}
		
		// 1.1 验证交易密码
		if(custInfoEntity.getTradePassword() == null) {
			throw new SLException("请设置交易密码");
		}
		if(!tradePassword.equals(custInfoEntity.getTradePassword())){
			throw new SLException("交易密码不正确");
		}
		AccountInfoEntity accountInfoEntity = accountInfoRepository.findByCustId(custId);
		if(accountInfoEntity == null) {
			throw new SLException("账户不存在");
		}
		
		// 1.2 判断用户赎回金额是否达到上限
		ProductInfoEntity productInfoEntity = productInfoRepository.findProductInfoByProductTypeName(Constant.PRODUCT_TYPE_01);
		List<Object> dayList = atoneInfoRepository.queryByCreateDate(custId, productInfoEntity.getId(), DateUtils.getStartDate(new Date()), DateUtils.getEndDate(new Date()), Constant.ATONE_METHOD_IMMEDIATE);
		Object[] dayObj = (Object[])dayList.get(0);
		BigDecimal dayWithdrawAmount = (BigDecimal)dayObj[0];
		long dayWithdrawCount = (long)dayObj[1];
		dayWithdrawAmount = ArithUtil.add(dayWithdrawAmount, tradeAmount);
		if(dayWithdrawAmount.compareTo(maxDayWithdrawAmount)> 0){
			throw new SLException(String.format("当日可赎回金额不能超过%s，实际赎回金额为%s", maxDayWithdrawAmount.toString(), dayWithdrawAmount.toString()));
		}

		// 1.3 判断用户日赎回，月赎回次数是否达到上限
		dayWithdrawCount ++;
		if(dayWithdrawCount > maxDayWithdrawCount){
			throw new SLException(String.format("当日可赎回次数不能超过%d，实际赎回次数为%d", maxDayWithdrawCount, dayWithdrawCount));
		}
		
		// 获取当月赎回次数
		dayList = atoneInfoRepository.queryByCreateDate(custId, productInfoEntity.getId(), DateUtils.getMonthStartDate(new Date()), DateUtils.getEndDate(new Date()), Constant.ATONE_METHOD_IMMEDIATE);
		dayObj = (Object[])dayList.get(0);
		dayWithdrawCount = (long)dayObj[1];
		if(dayWithdrawCount > maxMonthWithdrawCount){
			// 冻结用户
			custInfoEntity.setEnableStatus(Constant.ENABLE_STATUS_02);
			custInfoEntity.setBasicModelProperty(custId, false);
			
			// 记录日志
			LogInfoEntity logInfoEntity = new LogInfoEntity();
			logInfoEntity.setRelateType(Constant.TABLE_BAO_T_CUST_INFO);
			logInfoEntity.setRelatePrimary(custId);
			logInfoEntity.setLogType(Constant.OPERATION_TYPE_01);
			logInfoEntity.setOperDesc("");
			logInfoEntity.setOperPerson(custId);
			logInfoEntity.setOperIpaddress((String)params.get("ipAddress"));
			logInfoEntity.setMemo(String.format("当月可赎回次数不能超过%d，实际赎回次数为%d。系统已将您的账户冻结!", maxMonthWithdrawCount, dayWithdrawCount));
			logInfoEntity.setBasicModelProperty(custId, true);
			logInfoEntityRepository.save(logInfoEntity);
			throw new SLException(String.format("当月可赎回次数不能超过%d，实际赎回次数为%d。系统已将您的账户冻结!", maxMonthWithdrawCount, dayWithdrawCount));
		}
		
		// 1.4 判断是否超过可提现金额，公司收益主账户：可提现金额字段
		SubAccountInfoEntity earnSubAccount = subAccountInfoRepository.findBySubAccountNo(Constant.SUB_ACCOUNT_NO_ERAN); // 公司收益分账户
		AccountInfoEntity earnMainAccount = accountInfoRepository.findOne(earnSubAccount.getAccountId()); // 公司收益主账户
		if(tradeAmount.compareTo(earnSubAccount.getAccountAmount()) > 0){
			log.error(String.format("可赎回金额为%s，实际赎回金额为%s", earnSubAccount.getAccountAmount().toString(), tradeAmount.toString()));
			throw new SLException("快速赎回额度不足，请使用普通赎回！");
		}
		
		// 1.5 判断用户赎回金额是否超过所拥有的债权价值
		BigDecimal currentValue = subAccountInfoRepository.queryUserAllValue(custId, productInfoEntity.getId(), Constant.VALID_STATUS_VALID);
		if(tradeAmount.compareTo(currentValue) > 0){
			throw new SLException(String.format("用户持有价值%s，实际赎回价值%s", currentValue.toString(), tradeAmount.toString()));
		}
		
		// 2 生成赎回订单表(赎回债权详情表由定时任务处理)
		AtoneInfoEntity atoneInfoEntity = new AtoneInfoEntity();
		atoneInfoEntity.setCustId(custId);
		atoneInfoEntity.setProductId(productInfoEntity.getId());
		atoneInfoEntity.setOperType("");
		atoneInfoEntity.setTradeCode(numberService.generateTradeNumber());
		atoneInfoEntity.setCleanupDate(new Date());
		atoneInfoEntity.setAtoneMethod(Constant.ATONE_METHOD_IMMEDIATE);
		atoneInfoEntity.setAtoneExpenses(new BigDecimal("0"));
		atoneInfoEntity.setAtoneStatus(Constant.TRADE_STATUS_03);// 取交易状态，此处为成功
		atoneInfoEntity.setAuditStatus(Constant.AUDIT_STATUS_PASS);
		atoneInfoEntity.setAtoneTotalAmount(tradeAmount);
		atoneInfoEntity.setAlreadyAtoneAmount(tradeAmount);
		atoneInfoEntity.setTradeSource(StringUtils.isEmpty(appSource) ? Constant.INVEST_SOURCE_PC : appSource);
		atoneInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
		atoneInfoEntity = atoneInfoRepository.save(atoneInfoEntity);
		
		// 3 生成赎回审核记录
		AuditInfoEntity auditInfoEntity = new AuditInfoEntity();
		auditInfoEntity.setCustId(custId);
		auditInfoEntity.setRelateType(Constant.TABLE_BAO_T_ATONE_INFO);
		auditInfoEntity.setRelatePrimary(atoneInfoEntity.getId());
		auditInfoEntity.setApplyType(Constant.ATONE_METHOD_IMMEDIATE);
		auditInfoEntity.setApplyTime(new Timestamp(System.currentTimeMillis()));
		auditInfoEntity.setTradeAmount(tradeAmount);
		auditInfoEntity.setTradeStatus(Constant.TRADE_STATUS_03);
		auditInfoEntity.setAuditStatus(Constant.AUDIT_STATUS_PASS);
		auditInfoEntity.setAuditTime(new Date());
		auditInfoEntity.setAuditUser(Constant.SYSTEM_USER_BACK);
		auditInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
		auditInfoEntity = auditInfoRespository.save(auditInfoEntity);
		
		// 4 生成买单记录
		EmptionInfoEntity emptionInfoEntity = new EmptionInfoEntity();
		emptionInfoEntity.setAtoneId(atoneInfoEntity.getId());
		emptionInfoEntity.setEmptionAmount(tradeAmount);
		emptionInfoEntity.setEmptionDate(new Date());
		emptionInfoEntity.setAccountId(earnSubAccount.getId());//购买账户为收益分账户ID
		emptionInfoEntity.setCustId(earnSubAccount.getCustId());
		emptionInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
		emptionInfoEntity = emptionInfoRepository.save(emptionInfoEntity);
		
		// 5 更新用户活期宝分账户并记录流水与处理投资记录
		String reqeustNo = numberService.generateTradeBatchNumber();
		List<InvestInfoEntity> investList = investInfoRepository.findByCustId(custId, productInfoEntity.getId(), Constant.VALID_STATUS_VALID);
		List<SubAccountInfoEntity> subAccountList = subAccountInfoRepository.findByCustId(custId, productInfoEntity.getId(), Constant.VALID_STATUS_VALID);
		BigDecimal transferAmount = tradeAmount; //等待赎回的金额
		for(InvestInfoEntity i : investList){
			if(transferAmount.compareTo(new BigDecimal("0")) <= 0)break;
			for(SubAccountInfoEntity s : subAccountList){
				if(s.getRelatePrimary().equals(i.getId())){
					if(s.getAccountAvailableValue().compareTo(transferAmount) >= 0){ // 分账户金额大于赎回金额
						
						// 当前分账户满足赎回金额，直接赎回（用户分账户价值——>用户分账户现金——>用户主账户）
						saveUserAccount(accountInfoEntity, s, earnMainAccount, earnSubAccount, transferAmount, reqeustNo, atoneInfoEntity, Constant.ATONE_METHOD_IMMEDIATE, "");
						// 等待赎回的金额 = 原赎回金额-已赎回金额
						transferAmount = ArithUtil.sub(transferAmount, transferAmount);
					}
					else {// 分账户金额小于赎回金额
						// 等待赎回的金额 = 原赎回金额-已赎回金额
						transferAmount = ArithUtil.sub(transferAmount, s.getAccountAvailableValue());
						// 当前分账户金额全部赎回（用户分账户价值——>用户分账户现金——>用户主账户）
						saveUserAccount(accountInfoEntity, s, earnMainAccount, earnSubAccount, s.getAccountAvailableValue(), reqeustNo, atoneInfoEntity, Constant.ATONE_METHOD_IMMEDIATE, "");
					}
					
					// 若分账户余额为0表示该分账户已经全部赎回，此时该笔投资失效
					if(s.getAccountTotalValue().compareTo(new BigDecimal("0")) == 0){
						i.setInvestStatus(Constant.VALID_STATUS_INVALID);
						i.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
					}
					
					break;
				}
			}
		}	
		
		// 6、发送通知
		//sendEmail(custInfoRepository.findOne(custId), "", "赎回活期宝", "");
		
		// 7、记录日志
		// 7.1 记录赎回日志
		LogInfoEntity logInfoEntity = new LogInfoEntity();
		logInfoEntity.setRelateType(Constant.TABLE_BAO_T_ATONE_INFO);
		logInfoEntity.setRelatePrimary(atoneInfoEntity.getId());
		logInfoEntity.setLogType(Constant.OPERATION_TYPE_09);
		logInfoEntity.setOperDesc("");
		logInfoEntity.setOperPerson(custId);
		logInfoEntity.setOperIpaddress((String)params.get("ipAddress"));
		logInfoEntity.setMemo(String.format("%s赎回活期宝，赎回金额%s", custInfoEntity.getLoginName(), tradeAmount.toString()));
		logInfoEntity.setBasicModelProperty(custId, true);
		logInfoEntityRepository.save(logInfoEntity);
		
		// 记录设备信息
		if(params.containsKey("channelNo")) {
			DeviceInfoEntity deviceInfoEntity = new DeviceInfoEntity();
			deviceInfoEntity.setRelateTableIdentification(Constant.TABLE_BAO_T_ATONE_INFO);
			deviceInfoEntity.setRelatePrimary(atoneInfoEntity.getId());
			deviceInfoEntity.setCustId(custId);
			deviceInfoEntity.setTradeType(Constant.OPERATION_TYPE_09);
			deviceInfoEntity.setMeId((String)params.get("meId"));
			deviceInfoEntity.setMeVersion((String)params.get("meVersion"));
			deviceInfoEntity.setAppSource(Strings.nullToEmpty((String)params.get("appSource")).toLowerCase());
			deviceInfoEntity.setChannelNo(paramService.findByChannelNo((String)params.get("channelNo")));
			deviceInfoEntity.setBasicModelProperty(custId, true);
			deviceInfoRepository.save(deviceInfoEntity);
		}
		
		// 7.2 记录赎回审核日志
		LogInfoEntity logInfoEntityAudit = new LogInfoEntity();
		logInfoEntityAudit.setRelateType(Constant.TABLE_BAO_T_AUDIT_INFO);
		logInfoEntityAudit.setRelatePrimary(auditInfoEntity.getId());
		logInfoEntityAudit.setLogType(Constant.OPERATION_TYPE_12);
		logInfoEntityAudit.setOperDesc("");
		logInfoEntityAudit.setOperPerson(custInfoEntity.getId());
		logInfoEntityAudit.setOperIpaddress((String)params.get("ipAddress"));
		logInfoEntityAudit.setOperBeforeContent(Constant.AUDIT_STATUS_REVIEWD);
		logInfoEntityAudit.setOperAfterContent(auditInfoEntity.getAuditStatus());
		logInfoEntityAudit.setMemo(String.format("完成对%s赎回活期宝的审核，赎回金额%s", custInfoEntity.getLoginName(), tradeAmount.toString()));
		logInfoEntityAudit.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
		logInfoEntityRepository.save(logInfoEntityAudit);
		
		return new ResultVo(true);
	} 
	
	/**
	 * 赎回活期宝（普通赎回）
	 *
	 * @author  wangjf
	 * @date    2015年4月30日 上午11:40:01
	 * @param params
	 		<tt>custId： String:用户ID</tt><br>
	  		<tt>tradeAmount： String:赎回金额</tt><br>
	  		<tt>tradePassword： String:交易密码</tt><br>
	 * @return
	 * @throws SLException
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo withdrawBaoNormal(Map<String, Object> params) throws SLException
	{
		JobRunListenerEntity jobRunListener = jobRunListenerMapper.findByJobClassName(DailySettlementJob.class.getName());
		if ("运行中".equals(jobRunListener.getExecuteStatus())) {
			return new ResultVo(false, "系统正在结息，暂不可做赎回操作，请稍后再试！");
		}
		
		ProductInfoEntity productInfoEntity = productInfoRepository.findProductInfoByProductTypeName(Constant.PRODUCT_TYPE_01);
		String custId = (String)params.get("custId");
		BigDecimal tradeAmount = new BigDecimal((String)params.get("tradeAmount"));
		String tradePassword = (String)params.get("tradePassword");
		String appSource = (String)params.get("appSource"); // 来源
		
		// 1.0 验证用户状态
		CustInfoEntity custInfoEntity = custInfoRepository.findOne(custId);
		if(custInfoEntity == null){
			throw new SLException("用户不存在！");
		}
		if(!Constant.ENABLE_STATUS_01.equals(custInfoEntity.getEnableStatus())){
			throw new SLException("账户为非正常状态，可能被冻结，请联系客服！");
		}
		
		// 1.1 验证交易密码
		if(custInfoEntity.getTradePassword() == null) {
			throw new SLException("请设置交易密码");
		}
		if(!tradePassword.equals(custInfoEntity.getTradePassword())){
			throw new SLException("交易密码不正确");
		}
		AccountInfoEntity accountInfoEntity = accountInfoRepository.findByCustId(custId);
		if(accountInfoEntity == null) {
			throw new SLException("账户不存在");
		}
		
		// 1.2 判断是否超过可提现金额，公司收益主账户：可提现金额字段
//		SubAccountInfoEntity earnSubAccount = subAccountInfoRepository.findBySubAccountNo(Constant.SUB_ACCOUNT_NO_ERAN); // 公司收益分账户
//		if(tradeAmount.compareTo(earnSubAccount.getAccountAmount()) > 0){
//			throw new SLException(String.format("可赎回金额为%s，实际赎回金额为%s", earnSubAccount.getAccountAmount().toString(), tradeAmount.toString()));
//		}
		
		// 1.3 判断用户赎回金额是否超过所拥有的债权价值
		BigDecimal currentValue = subAccountInfoRepository.queryUserAllValue(custId, productInfoEntity.getId(), Constant.VALID_STATUS_VALID);
		if(tradeAmount.compareTo(currentValue) > 0){
			throw new SLException(String.format("用户持有价值%s，实际赎回价值%s", currentValue.toString(), tradeAmount.toString()));
		}
		
		// 1.4 每日赎回次数限制、每月赎回次数限制
//		// 每天最大可赎回次数 
//		long maxDayWithdrawCount = paramService.findMaxDayWithdrawCount();
//		// 每月最大可赎回次数
//		//long maxMonthWithdrawCount = paramService.findMaxMonthWithdrawCount();
//		List<Object> dayList = atoneInfoRepository.queryByCreateDate(custId, productInfoEntity.getId(), DateUtils.getStartDate(new Date()), DateUtils.getEndDate(new Date()), Constant.ATONE_METHOD_NORMAL);
//		Object[] dayObj = (Object[])dayList.get(0);
//		long dayWithdrawCount = (long)dayObj[1];
//
//		// 判断用户日赎回，月赎回次数是否达到上限
//		dayWithdrawCount ++;
//		if(dayWithdrawCount > maxDayWithdrawCount){
//			throw new SLException(String.format("当日可赎回次数不能超过%d，实际赎回次数为%d", maxDayWithdrawCount, dayWithdrawCount));
//		}
		
		// 2 生成赎回订单表(赎回债权详情表由定时任务处理)
		AtoneInfoEntity atoneInfoEntity = new AtoneInfoEntity();
		atoneInfoEntity.setCustId(custId);
		atoneInfoEntity.setProductId(productInfoEntity.getId());
		atoneInfoEntity.setOperType("");
		atoneInfoEntity.setTradeCode(numberService.generateTradeNumber());
		atoneInfoEntity.setCleanupDate(new Date());
		atoneInfoEntity.setAtoneMethod(Constant.ATONE_METHOD_NORMAL);
		atoneInfoEntity.setAtoneExpenses(new BigDecimal("0"));
		atoneInfoEntity.setAtoneStatus(Constant.TRADE_STATUS_01);
		atoneInfoEntity.setAuditStatus(Constant.AUDIT_STATUS_REVIEWD);
		atoneInfoEntity.setAtoneTotalAmount(tradeAmount);
		atoneInfoEntity.setAlreadyAtoneAmount(tradeAmount);
		atoneInfoEntity.setTradeSource(StringUtils.isEmpty(appSource) ? Constant.INVEST_SOURCE_PC : appSource);
		atoneInfoEntity.setBasicModelProperty(custId, true);
		atoneInfoEntity = atoneInfoRepository.save(atoneInfoEntity);
		
		// 3.记录日志
		LogInfoEntity logInfoEntity = new LogInfoEntity();
		logInfoEntity.setRelateType(Constant.TABLE_BAO_T_ATONE_INFO);
		logInfoEntity.setRelatePrimary(atoneInfoEntity.getId());
		logInfoEntity.setLogType(Constant.OPERATION_TYPE_09);
		logInfoEntity.setOperDesc("");
		logInfoEntity.setOperPerson(custId);
		logInfoEntity.setOperIpaddress((String)params.get("ipAddress"));
		logInfoEntity.setMemo(String.format("%s赎回活期宝，赎回金额%s", custInfoEntity.getLoginName(), tradeAmount.toString()));
		logInfoEntity.setBasicModelProperty(custId, true);
		logInfoEntityRepository.save(logInfoEntity);
		
		// 记录设备信息
		if(params.containsKey("channelNo")) {
			DeviceInfoEntity deviceInfoEntity = new DeviceInfoEntity();
			deviceInfoEntity.setRelateTableIdentification(Constant.TABLE_BAO_T_ATONE_INFO);
			deviceInfoEntity.setRelatePrimary(atoneInfoEntity.getId());
			deviceInfoEntity.setCustId(custId);
			deviceInfoEntity.setTradeType(Constant.OPERATION_TYPE_09);
			deviceInfoEntity.setMeId((String)params.get("meId"));
			deviceInfoEntity.setMeVersion((String)params.get("meVersion"));
			deviceInfoEntity.setAppSource(Strings.nullToEmpty((String)params.get("appSource")).toLowerCase());
			deviceInfoEntity.setChannelNo(paramService.findByChannelNo((String)params.get("channelNo")));
			deviceInfoEntity.setBasicModelProperty(custId, true);
			deviceInfoRepository.save(deviceInfoEntity);
		}
		
		// 4.冻结用户价值
		String reqeustNo = numberService.generateTradeBatchNumber();
		List<InvestInfoEntity> investList = investInfoRepository.findByCustId(custId, productInfoEntity.getId(), Constant.VALID_STATUS_VALID);
		List<SubAccountInfoEntity> subAccountList = subAccountInfoRepository.findByCustId(custId, productInfoEntity.getId(), Constant.VALID_STATUS_VALID);
		BigDecimal transferAmount = atoneInfoEntity.getAtoneTotalAmount(); //等待赎回的金额
		for(InvestInfoEntity i : investList){
			if(transferAmount.compareTo(new BigDecimal("0")) <= 0)break;
			for(SubAccountInfoEntity s : subAccountList){
				if(s.getRelatePrimary().equals(i.getId())){
					if(s.getAccountAvailableValue().compareTo(transferAmount) >= 0){ // 分账户金额大于赎回金额
						
						// 当前分账户满足赎回金额，直接赎回（用户分账户价值——>用户分账户现金——>用户主账户）
						freezeUserAccount(accountInfoEntity, s, accountInfoEntity, s, transferAmount, reqeustNo, atoneInfoEntity);
						// 等待赎回的金额 = 原赎回金额-已赎回金额
						transferAmount = ArithUtil.sub(transferAmount, transferAmount);
					}
					else if(s.getAccountAvailableValue().compareTo(new BigDecimal("0")) > 0){// 分账户金额小于赎回金额且分账户大于0
						
						// 等待赎回的金额 = 原赎回金额-已赎回金额
						transferAmount = ArithUtil.sub(transferAmount, s.getAccountAvailableValue());
						// 当前分账户金额全部赎回（用户分账户价值——>用户分账户现金——>用户主账户）
						freezeUserAccount(accountInfoEntity, s, accountInfoEntity, s, s.getAccountAvailableValue(), reqeustNo, atoneInfoEntity);
					}
					
					break;
				}
			}
		}	
				
		return new ResultVo(true);
	}
	
	/**
	 * 赎回——更新用户分账
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
	public void saveUserAccount(AccountInfoEntity accountInfoEntity, SubAccountInfoEntity s, 
			AccountInfoEntity earnMainAccount, SubAccountInfoEntity earnSubAccount, 
			BigDecimal transferAmount, String reqeustNo, AtoneInfoEntity atoneInfoEntity, 
			String atoneType, String oldTradeNo)
	{
		// 判断交易类型是普通赎回还是快速赎回
		String tradeType = atoneType.equals(Constant.ATONE_METHOD_IMMEDIATE) ? SubjectConstant.TRADE_FLOW_TYPE_ATONE : SubjectConstant.TRADE_FLOW_TYPE_ATONE_NORMAL;
		String subjectType = atoneType.equals(Constant.ATONE_METHOD_IMMEDIATE) ? SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE : SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_NORMAL;
		
		// 5.1 判断是否需要先解冻
		if(Constant.ATONE_METHOD_NORMAL.equals(atoneType)){ // 普通赎回需先解冻
			
			unFreezeUserAccount(accountInfoEntity, s, accountInfoEntity, s, transferAmount, reqeustNo, atoneInfoEntity, oldTradeNo);
		}
		
		// 5.2 记录用户分账户流水（价值出 ）
		s.setAccountTotalValue(ArithUtil.sub(s.getAccountTotalValue(), transferAmount));
		s.setAccountAvailableValue(ArithUtil.sub(s.getAccountAvailableValue(), transferAmount));
		
		accountFlowService.saveAccountFlow(accountInfoEntity, s, earnMainAccount, earnSubAccount, "4", 
				tradeType, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, 
				transferAmount, subjectType, 
				Constant.TABLE_BAO_T_ATONE_INFO, atoneInfoEntity.getId(), SubjectConstant.SUBJECT_TYPE_VALUE);
		
		// 5.3 记录用户分账户流水（现金入）
		s.setAccountAmount(ArithUtil.add(s.getAccountAmount(), transferAmount));
		
		accountFlowService.saveAccountFlow(accountInfoEntity, s, earnMainAccount, earnSubAccount, "4", 
				tradeType, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING, 
				transferAmount, subjectType, 
				Constant.TABLE_BAO_T_ATONE_INFO, atoneInfoEntity.getId(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
		
		// 6 更新公司收益分账户价值并记录流水
		
		if(Constant.ATONE_METHOD_NORMAL.equals(atoneType)){ // 普通赎回时主账户资金转出，分账户资金转入
			
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
			
			accountFlowService.saveAccountFlow(earnMainAccount, earnSubAccount, accountInfoEntity, s, "3", 
					tradeType, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING, 
					transferAmount, subjectType, 
					Constant.TABLE_BAO_T_ATONE_INFO, atoneInfoEntity.getId(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
		}
		
		// 6.1 更新公司收益分账户（资金转出）
		earnSubAccount.setAccountAmount(ArithUtil.sub(earnSubAccount.getAccountAmount(), transferAmount));
		
		// 6.2 记录公司收益分账户流水(资金转出)
		accountFlowService.saveAccountFlow(earnMainAccount, earnSubAccount, accountInfoEntity, s, "4", 
				tradeType, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, 
				transferAmount, subjectType, 
				Constant.TABLE_BAO_T_ATONE_INFO, atoneInfoEntity.getId(), SubjectConstant.SUBJECT_TYPE_AMOUNT);

		// 6.3 更新公司收益账户（价值转入）
		earnSubAccount.setAccountTotalValue(ArithUtil.add(earnSubAccount.getAccountTotalValue(), transferAmount));
		earnSubAccount.setAccountAvailableValue(ArithUtil.add(earnSubAccount.getAccountAvailableValue(), transferAmount));
		earnSubAccount.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
		
		// 6.4 记录公司收益分账户流水(价值转入)
		accountFlowService.saveAccountFlow(earnMainAccount, earnSubAccount, accountInfoEntity, s, "4",
				tradeType, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING, 
				transferAmount, subjectType, 
				Constant.TABLE_BAO_T_ATONE_INFO, atoneInfoEntity.getId(), SubjectConstant.SUBJECT_TYPE_VALUE);
		
		// 7.1  更新用户分账户（分账户——>主账户）
		s.setAccountAmount(ArithUtil.sub(s.getAccountAmount(), transferAmount));
		s.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
		
		// 7.2 记录用户分账户流水（现金）
		accountFlowService.saveAccountFlow(accountInfoEntity, s, accountInfoEntity, s, "3", 
				tradeType, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, 
				transferAmount, subjectType, 
				Constant.TABLE_BAO_T_ATONE_INFO, atoneInfoEntity.getId(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
		
		// 7.3  更新用户主账户（分账户——>主账户）
		accountInfoEntity.setAccountTotalAmount(ArithUtil.add(accountInfoEntity.getAccountTotalAmount(), transferAmount));
		accountInfoEntity.setAccountAvailableAmount(ArithUtil.add(accountInfoEntity.getAccountAvailableAmount(), transferAmount));
		accountInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
		
		// 7.4 记录用户主账户流水
		accountFlowService.saveAccountFlow(accountInfoEntity, s, accountInfoEntity, s, "2", 
				tradeType, reqeustNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING, 
				transferAmount, subjectType, 
				Constant.TABLE_BAO_T_ATONE_INFO, atoneInfoEntity.getId(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
		
	}

	/**
	 * 补全赎回详情（定时任务）
	 *
	 * @author  wangjf
	 * @date    2015年5月1日 上午11:11:22
	 * @param params
	 * @return
	 * @throws SLException
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo fullAtoneDetail(Map<String, Object> params) throws SLException {
	
		List<AtoneInfoEntity> atoneList = atoneInfoRepository.queryByCleanupDate(Constant.AUDIT_STATUS_PASS, DateUtils.getStartDate(new Date()), DateUtils.getEndDate(new Date()));
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("productType", Constant.PRODUCT_TYPE_01);
		Map<String, Object> result = loanInfoRepositoryCustom.findLoanListCount(param);
		BigDecimal totalLoans = (BigDecimal)result.get("totalLoans");
		BigDecimal atoneScale = ArithUtil.div(new BigDecimal("1"), totalLoans);

		for(AtoneInfoEntity a : atoneList){
			AtoneDetailInfoEntity atoneDetailInfoEntity = new AtoneDetailInfoEntity();
			atoneDetailInfoEntity.setAtoneId(a.getId());
			atoneDetailInfoEntity.setLoanId("");
			atoneDetailInfoEntity.setAtoneAmount(ArithUtil.div(a.getAlreadyAtoneAmount(), totalLoans));
			atoneDetailInfoEntity.setAtoneScale(atoneScale);
			atoneDetailInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
			
			atoneInfoRepositoryCustom.batchInsertAllotDetail(atoneDetailInfoEntity);
		}
		return new ResultVo(true);
	}
	
	/**
	 * 发送通知
	 *
	 * @author  wangjf
	 * @date    2015年5月6日 下午4:15:54
	 * @param custEntity
	 * @param content
	 * @param title
	 * @param templateName
	 * @throws SLException 
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public void sendEmail(CustInfoEntity custEntity, String content, String title, String templateName) throws SLException{
		Map<String,Object> smsInfo = Maps.newHashMap();
		smsInfo.put("to", custEntity.getEmail());
		smsInfo.put("type", MailType.TEXT);
		smsInfo.put("title", title);
		smsInfo.put("ftlPath", templateName);
		
		Map<String,Object> dataSet = Maps.newHashMap(); 
		dataSet.put("customerTle", "");
		dataSet.put("customerEmail", "");
		smsInfo.put("dataSet", dataSet);
		
		emailService.sendEmail(smsInfo);
		
		CustInfoEntity systemEntity = custInfoRepository.findByLoginName(Constant.SYSTEM_USER_BACK);
		SystemMessageInfoEntity systemMessageInfoEntity = new SystemMessageInfoEntity();
		systemMessageInfoEntity.setSendCust(systemEntity);
		systemMessageInfoEntity.setReceiveCust(custEntity);
		systemMessageInfoEntity.setSendTitle(title);
		systemMessageInfoEntity.setSendContent(content);
		systemMessageInfoEntity.setSendDate(new Date());
		systemMessageInfoEntity.setIsRead(Constant.SITE_MESSAGE_NOREAD);
		systemMessageInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
		systemMessageInfoRepository.save(systemMessageInfoEntity);
	}

	/**
	 * 审核赎回活期宝（普通赎回）
	 *
	 * @author  wangjf
	 * @date    2015年5月8日 下午4:20:29
	 * @param params
	 		<tt>atoneId： String:赎回ID</tt><br>	
	  		<tt>auditCustId： String:审核用户ID</tt><br>
	 		<tt>auditStatus： String:审核状态</tt><br>	
	 		<tt>auditMemo： String:审核备注</tt><br>
	 * @return
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo auditWithdrawBaoNormal(Map<String, Object> params) throws SLException{
		
		String auditCustId = (String)params.get("auditCustId");
		String auditStatus = (String)params.get("auditStatus");
		String auditMemo = (String)params.get("auditMemo");
		AtoneInfoEntity atoneInfoEntity = atoneInfoRepository.findOne((String)params.get("atoneId"));
		if(atoneInfoEntity == null){
			throw new SLException("赎回信息不存在");
		}
		// 判断赎回记录是否为已处理
		if(Constant.TRADE_STATUS_04.equals(atoneInfoEntity.getAtoneStatus())
				|| Constant.TRADE_STATUS_03.equals(atoneInfoEntity.getAtoneStatus())) {
			throw new SLException("该记录已经审核过，请勿重复审核");
		}
	
		CustInfoEntity custInfoEntity = custInfoRepository.findOne(atoneInfoEntity.getCustId());
		String custId = custInfoEntity.getId();
		
		// 1 生成赎回审核记录
		AuditInfoEntity auditInfoEntity = new AuditInfoEntity();
		auditInfoEntity.setCustId(custId);
		auditInfoEntity.setRelateType(Constant.TABLE_BAO_T_ATONE_INFO);
		auditInfoEntity.setRelatePrimary(atoneInfoEntity.getId());
		auditInfoEntity.setApplyType(Constant.ATONE_METHOD_NORMAL);
		auditInfoEntity.setApplyTime(new Timestamp(System.currentTimeMillis()));
		auditInfoEntity.setTradeAmount(atoneInfoEntity.getAtoneTotalAmount());
		auditInfoEntity.setTradeStatus(Constant.TRADE_STATUS_03);
		auditInfoEntity.setAuditTime(new Date());
		auditInfoEntity.setAuditStatus(auditStatus);
		auditInfoEntity.setAuditUser(auditCustId);
		auditInfoEntity.setMemo(auditMemo);
		auditInfoEntity.setBasicModelProperty(auditCustId, true);
		auditInfoEntity = auditInfoRespository.save(auditInfoEntity);
		
		// 2 记录日志
		LogInfoEntity logInfoEntity = new LogInfoEntity();
		logInfoEntity.setRelateType(Constant.TABLE_BAO_T_AUDIT_INFO);
		logInfoEntity.setRelatePrimary(auditInfoEntity.getId());
		logInfoEntity.setLogType(Constant.OPERATION_TYPE_12);
		logInfoEntity.setOperDesc("");
		logInfoEntity.setOperPerson(custId);
		logInfoEntity.setOperIpaddress((String)params.get("ipAddress"));
		logInfoEntity.setOperBeforeContent(atoneInfoEntity.getAuditStatus());
		logInfoEntity.setOperAfterContent(auditStatus);
		logInfoEntity.setMemo(String.format("完成对%s赎回活期宝的审核，赎回金额%s。本次审核操作：%s", custInfoEntity.getLoginName(), atoneInfoEntity.getAtoneTotalAmount().toString(), auditStatus));
		logInfoEntity.setBasicModelProperty(auditCustId, true);
		logInfoEntityRepository.save(logInfoEntity);
		
		// 取公司账户与客户账户
		SubAccountInfoEntity earnSubAccount = subAccountInfoRepository.findBySubAccountNo(Constant.SUB_ACCOUNT_NO_ERAN); // 公司收益分账户
		AccountInfoEntity earnMainAccount = accountInfoRepository.findOne(earnSubAccount.getAccountId()); // 公司收益主账户
		AccountInfoEntity accountInfoEntity = accountInfoRepository.findByCustId(custId);
		
		// 3处理审核不过的情况
		if(!auditStatus.equals(Constant.AUDIT_STATUS_PASS)){

			atoneInfoEntity.setAtoneStatus(Constant.TRADE_STATUS_04);
			atoneInfoEntity.setAuditStatus(auditStatus);
			atoneInfoEntity.setBasicModelProperty(auditCustId, false);
			atoneInfoEntity.setMemo(auditMemo);
			
			// 审核不通过解冻分账户
			String reqeustNo = numberService.generateTradeBatchNumber();
			ProductInfoEntity productInfoEntity = productInfoRepository.findProductInfoByProductTypeName(Constant.PRODUCT_TYPE_01);
			List<AccountFlowInfoEntity> accountFlowList = accountFlowInfoRepository.queryByFlowBusi(Constant.TABLE_BAO_T_ATONE_INFO, atoneInfoEntity.getId());// 取当前赎回对应的冻结记录
			List<SubAccountInfoEntity> subAccountList = subAccountInfoRepository.findByCustId(custId, productInfoEntity.getId(), Constant.VALID_STATUS_VALID);
			for(AccountFlowInfoEntity flow : accountFlowList){
				for(SubAccountInfoEntity s : subAccountList){
					if(flow.getAccountId().equals(s.getId())){
						unFreezeUserAccount(accountInfoEntity, s, accountInfoEntity, s, flow.getTradeAmount(), reqeustNo, atoneInfoEntity, flow.getTradeNo());
						break;
					}
				}
			}
			
			return new ResultVo(true);
		}
		
		atoneInfoEntity.setAtoneStatus(Constant.TRADE_STATUS_03);
		atoneInfoEntity.setAuditStatus(auditStatus);
		atoneInfoEntity.setBasicModelProperty(auditCustId, false);
		
		// 4 生成买单记录		
		EmptionInfoEntity emptionInfoEntity = new EmptionInfoEntity();
		emptionInfoEntity.setAtoneId(atoneInfoEntity.getId());
		emptionInfoEntity.setEmptionAmount(atoneInfoEntity.getAtoneTotalAmount());
		emptionInfoEntity.setEmptionDate(new Date());
		emptionInfoEntity.setAccountId(earnSubAccount.getId());//购买账户为收益分账户ID
		emptionInfoEntity.setCustId(earnSubAccount.getCustId());
		emptionInfoEntity.setBasicModelProperty(auditCustId, true);
		emptionInfoEntity = emptionInfoRepository.save(emptionInfoEntity);
		
		// 5 更新用户活期宝分账户并记录流水与处理投资记录
		String reqeustNo = numberService.generateTradeBatchNumber();
		ProductInfoEntity productInfoEntity = productInfoRepository.findProductInfoByProductTypeName(Constant.PRODUCT_TYPE_01);
		List<AccountFlowInfoEntity> accountFlowList = accountFlowInfoRepository.queryByFlowBusi(Constant.TABLE_BAO_T_ATONE_INFO, atoneInfoEntity.getId());// 取当前赎回对应的冻结记录
		List<SubAccountInfoEntity> subAccountList = subAccountInfoRepository.findByCustId(custId, productInfoEntity.getId(), Constant.VALID_STATUS_VALID);
		for(AccountFlowInfoEntity flow : accountFlowList){
			for(SubAccountInfoEntity s : subAccountList){
				if(flow.getAccountId().equals(s.getId())){
					saveUserAccount(accountInfoEntity, s, earnMainAccount, earnSubAccount, flow.getTradeAmount(), reqeustNo, atoneInfoEntity, Constant.ATONE_METHOD_NORMAL, flow.getTradeNo());
					if(s.getAccountTotalValue().compareTo(new BigDecimal("0")) < 0) {
						throw new SLException("赎回异常！");
					}
				}
			}
		}
		
		// 将投资记录状态置为无效
		List<InvestInfoEntity> investList = investInfoRepository.queryByAtoneId(Constant.TABLE_BAO_T_ATONE_INFO, atoneInfoEntity.getId());
		for(InvestInfoEntity entity : investList){
			if(Constant.VALID_STATUS_VALID.equals(entity.getInvestStatus())){
				for(SubAccountInfoEntity s : subAccountList){
					if(entity.getId().equals(s.getRelatePrimary())){						
						// 使该笔投资失效
						if(s.getAccountTotalValue().compareTo(new BigDecimal("0")) == 0){
							entity.setInvestStatus(Constant.VALID_STATUS_INVALID);
							entity.setBasicModelProperty(custId, false);
						}
						break;
					}
				}
			}
		}
		
		// 6、发送通知
		//sendEmail(custInfoRepository.findOne(custId), "", "赎回活期宝", "");
	
		return new ResultVo(true);
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
	public void freezeUserAccount(AccountInfoEntity accountInfoEntity, SubAccountInfoEntity s, AccountInfoEntity earnMainAccount, SubAccountInfoEntity earnSubAccount, BigDecimal transferAmount, String reqeustNo, AtoneInfoEntity atoneInfoEntity)
	{
		// 5.1 冻结用户分账户（价值）
		s.setAccountAvailableValue(ArithUtil.sub(s.getAccountAvailableValue(), transferAmount));
		s.setAccountFreezeValue(ArithUtil.add(s.getAccountFreezeValue(), transferAmount));
		
		// 5.2 记录用户分账户流水（价值 ）
		accountFlowService.saveAccountFlow(accountInfoEntity, s, accountInfoEntity, s, "4", 
				SubjectConstant.TRADE_FLOW_TYPE_ATONE_NORMAL_FREEZE, reqeustNo, null, 
				transferAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_NORMAL_FREEZE, 
				Constant.TABLE_BAO_T_ATONE_INFO, atoneInfoEntity.getId(), SubjectConstant.SUBJECT_TYPE_VALUE);	
	}
	
	/**
	 * 赎回——解冻用户分账
	 *
	 * @author  wangjf
	 * @date    2015年5月11日 下午1:53:07
	 * @param accountInfoEntity
	 * @param s
	 * @param earnMainAccount
	 * @param earnSubAccount
	 * @param transferAmount
	 * @param reqeustNo
	 * @param atoneInfoEntity
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public void unFreezeUserAccount(AccountInfoEntity accountInfoEntity, SubAccountInfoEntity s, AccountInfoEntity earnMainAccount, 
			SubAccountInfoEntity earnSubAccount, BigDecimal transferAmount, String reqeustNo, 
			AtoneInfoEntity atoneInfoEntity, String oldTradeNo)
	{
		// 5.1 冻结用户分账户（价值）
		s.setAccountAvailableValue(ArithUtil.add(s.getAccountAvailableValue(), transferAmount));
		s.setAccountFreezeValue(ArithUtil.sub(s.getAccountFreezeValue(), transferAmount));
		
		// 5.2 记录用户分账户流水（价值 ）
		AccountFlowInfoEntity accountFlowInfoEntity = accountFlowService.saveAccountFlow(accountInfoEntity, s, accountInfoEntity, s, "4", 
				SubjectConstant.TRADE_FLOW_TYPE_ATONE_NORMAL_UNFREEZE, reqeustNo, null, 
				transferAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_NORMAL_UNFREEZE, 
				Constant.TABLE_BAO_T_ATONE_INFO, atoneInfoEntity.getId(), SubjectConstant.SUBJECT_TYPE_VALUE);
		accountFlowInfoEntity.setOldTradeNo(oldTradeNo);
	}

	/**
	 * 加入体验宝
	 *
	 * @author  wangjf
	 * @date    2015年5月18日 上午11:29:47
	 * @param params
	 		<tt>custId： String:用户ID</tt><br>
	  		<tt>tradeAmount： String:加入金额</tt><br>
	 * @return
	 * @throws SLException
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo joinExperienceBao(Map<String, Object> params)
			throws SLException {
		String custId = (String)params.get("custId");
		BigDecimal tradeAmount = new BigDecimal((String)params.get("tradeAmount"));
		String appSource = StringUtils.isEmpty((String)params.get("investSource")) ? (String)params.get("appSource") : (String)params.get("investSource");
		appSource = Strings.nullToEmpty(StringUtils.isEmpty(appSource) ? Constant.INVEST_SOURCE_PC : appSource).toLowerCase();
		
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
		List<CustActivityInfoEntity> custActivityList = custActivityInfoRepository.findByCustId(custId, Constant.REAWARD_SPREAD_01);
		BigDecimal useableAmount = new BigDecimal("0");
		for(CustActivityInfoEntity custActivityInfoEntity : custActivityList){
			if(Constant.USER_ACTIVITY_TRADE_STATUS_01.equals(custActivityInfoEntity.getTradeStatus()) 
					|| Constant.USER_ACTIVITY_TRADE_STATUS_02.equals(custActivityInfoEntity.getTradeStatus())){ // 交易状态为已领取或者部分使用的可以使用
				useableAmount = ArithUtil.add(useableAmount, custActivityInfoEntity.getUsableAmount());
			}
		}
		
		if(useableAmount.compareTo(tradeAmount) < 0){
			throw new SLException(String.format("可用体验金余额%s小于加入金额%s", useableAmount.toString(), tradeAmount.toString()));
		}
		
		AccountInfoEntity accountInfoEntity = accountInfoRepository.findByCustId(custId);
		if(accountInfoEntity == null) {
			throw new SLException("账户不存在");
		}
		
		// 1.2 加入金额验证 ：起投金额   上限
		ProductInfoEntity productInfoEntity = productInfoRepository.findProductInfoByProductTypeName(Constant.PRODUCT_TYPE_03);
		if(productInfoEntity == null) {
			throw new SLException("产品不存在");
		}
		if(productInfoEntity.getInvestMinAmount().compareTo(tradeAmount) > 0 || productInfoEntity.getInvestMaxAmount().compareTo(tradeAmount) < 0){
			throw new SLException(String.format("加入金额不能大于上限%s，且不能小于下限%s", productInfoEntity.getInvestMaxAmount().toString(), productInfoEntity.getInvestMinAmount().toString()));
		}
		
		// 1.3 可投金额验证：判断客户此次投资是否超过剩余可投金额
		ProductDetailInfoEntity productDetailInfoEntity = productDetailInfoRepository.findByProductId(productInfoEntity.getId());
		if(productDetailInfoEntity == null) {
			throw new SLException("产品详情不存在");
		}
		if(productDetailInfoEntity.getCurrUsableValue().compareTo(tradeAmount) < 0){
			throw new SLException(String.format("加入金额为%s，实际剩余可加入金额为%s", tradeAmount.toString(), ArithUtil.formatScale2(productDetailInfoEntity.getCurrUsableValue()).toString()));//update by zhangzs 截取显示金额
		}
		
		// 1.4 验证体验宝产品状态为:开放中
		if(!productInfoEntity.getProductStatus().equals(Constant.PRODUCT_STATUS_BID_ING)){
			throw new SLException("产品状态非开放中，不能加入！");
		}
		
		// 2、验证用户体验宝是否开户
		BigDecimal countInvestTimes = investInfoRepository.countInvestInfoByCustId(custId, productInfoEntity.getId());
		
		// 3、更新体验宝募集表
		productDetailInfoEntity.setCurrUsableValue(ArithUtil.sub(productDetailInfoEntity.getCurrUsableValue(), tradeAmount));
		productDetailInfoEntity.setAlreadyInvestAmount(ArithUtil.add(productDetailInfoEntity.getAlreadyInvestAmount(), tradeAmount));
		productDetailInfoEntity.setBasicModelProperty(custId, false);
		if(countInvestTimes.compareTo(new BigDecimal("0")) == 0){ //投资次数为0表示新用户需将参与人数加1
			productDetailInfoEntity.setAlreadyInvestPeople(ArithUtil.add(productDetailInfoEntity.getAlreadyInvestPeople(), new BigDecimal("1")));
		}
		
		// 4、新增投资记录
		// 判断当前时间是否是15:00:00前，若是则取投资日期为当天，否则取投资时间为当天+1
		String paramTime = paramService.findInvestValidTime();//系统设置的时间
		int expireDays = paramService.findExpireDays();
		String investDate = "";
		String expireDate = "";
		if(DateUtils.getCurrentDate("HH:mm:ss").compareTo(paramTime) < 0){ // 小于生效时间
			investDate = DateUtils.formatDate(new Date(), "yyyyMMdd");
			expireDate = DateUtils.formatDate(DateUtils.getAfterDay(new Date(), expireDays) , "yyyyMMdd");
		}
		else {
			investDate = DateUtils.formatDate(DateUtils.getAfterDay(new Date(), 1), "yyyyMMdd");
			expireDate = DateUtils.formatDate(DateUtils.getAfterDay(new Date(), expireDays + 1) , "yyyyMMdd");
		}
		
		InvestInfoEntity newInvestInfoEntity = null;
		SubAccountInfoEntity subAccountInfoEntity = null;
		String tradeCode = numberService.generateLoanContractNumber();
		if(countInvestTimes.compareTo(new BigDecimal("0")) == 0){// 投资次数为0为新投资，直接新增投资记录
			newInvestInfoEntity = saveInvest(null, custId, productInfoEntity.getId(), tradeAmount, investDate, expireDate, tradeCode, true, appSource);
			subAccountInfoEntity = saveSubAccountInfo(custId, accountInfoEntity.getId(), newInvestInfoEntity.getId());
		}
		else { // 投资次数不为0，需判断之前有没有投资，有则新加详情，否则新建一笔投资
			InvestInfoEntity investInfoEntity = investInfoRepository.findInvestInfoByCustIdAndInvestDate(custId, productInfoEntity.getId(), investDate, Constant.VALID_STATUS_VALID);
			if(investInfoEntity == null){
				newInvestInfoEntity = saveInvest(null, custId, productInfoEntity.getId(), tradeAmount, investDate, expireDate, tradeCode, true, appSource);
				subAccountInfoEntity = saveSubAccountInfo(custId, accountInfoEntity.getId(), newInvestInfoEntity.getId());
			} 
			else {
				newInvestInfoEntity = saveInvest(investInfoEntity, custId, productInfoEntity.getId(), tradeAmount, investDate, expireDate, tradeCode, false, appSource);
				subAccountInfoEntity = subAccountInfoRepository.findByRelatePrimary(newInvestInfoEntity.getId());
			}
		}
		
		newInvestInfoEntity.setExpireDate(expireDate);
		
		// 6、更新体验金账户
		BigDecimal transferAmount = tradeAmount;
		for(CustActivityInfoEntity custActivityInfoEntity : custActivityList){
			if(transferAmount.compareTo(new BigDecimal("0")) == 0)break;
			if(Constant.USER_ACTIVITY_TRADE_STATUS_01.equals(custActivityInfoEntity.getTradeStatus()) 
					|| Constant.USER_ACTIVITY_TRADE_STATUS_02.equals(custActivityInfoEntity.getTradeStatus())){ // 交易状态为已领取或者部分使用的可以使用
				
				// 体验金金额大于交易金额，则直接从体验金扣减，否则使用全部的体验金
				if(custActivityInfoEntity.getUsableAmount().compareTo(transferAmount) > 0) { 
					custActivityInfoEntity.setUsableAmount(ArithUtil.sub(custActivityInfoEntity.getUsableAmount(), transferAmount));
					transferAmount = ArithUtil.sub(transferAmount, transferAmount);
				}
				else {
					transferAmount = ArithUtil.sub(transferAmount, custActivityInfoEntity.getUsableAmount());
					custActivityInfoEntity.setUsableAmount(ArithUtil.sub(custActivityInfoEntity.getUsableAmount(), custActivityInfoEntity.getUsableAmount()));
				}
				
				// 判断账户余额是否为0，为零时置为全部使用
				if(custActivityInfoEntity.getUsableAmount().compareTo(new BigDecimal("0")) == 0){
					custActivityInfoEntity.setTradeStatus(Constant.USER_ACTIVITY_TRADE_STATUS_03);
					custActivityInfoEntity.setBasicModelProperty(custId, false);
				}
				else {
					custActivityInfoEntity.setTradeStatus(Constant.USER_ACTIVITY_TRADE_STATUS_02);
					custActivityInfoEntity.setBasicModelProperty(custId, false);
				}
			}
		}
		
		// 7、主账户现金减少（因为体验宝主账户是不从主账户中出金额的，故此仅记流水，不做账户更新）
		String requestNo = numberService.generateTradeBatchNumber();
		AccountFlowInfoEntity accountFlowInfoEntity = accountFlowService.saveAccountFlow(accountInfoEntity, subAccountInfoEntity, accountInfoEntity, subAccountInfoEntity, "2", 
				SubjectConstant.TRADE_FLOW_TYPE_JOIN_EXPERIENCE, requestNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT, 
				tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_JOIN_EXPERIENCE, 
				subAccountInfoEntity.getRelateType(), subAccountInfoEntity.getRelatePrimary(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
		accountFlowInfoEntity.setMemo(String.format("使用体验金%s元，剩余体验金%s元", tradeAmount.toString(), ArithUtil.sub(useableAmount, tradeAmount).toString()));
		
		// 8、分账户价值增加
		subAccountInfoEntity.setAccountTotalValue(ArithUtil.add(subAccountInfoEntity.getAccountTotalValue(), tradeAmount));
		subAccountInfoEntity.setAccountAvailableValue(ArithUtil.add(subAccountInfoEntity.getAccountAvailableValue(), tradeAmount));
		subAccountInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, false);
		AccountFlowInfoEntity subAccountFlowInfoEntity = accountFlowService.saveAccountFlow(null, subAccountInfoEntity, null, subAccountInfoEntity, "4", 
				SubjectConstant.TRADE_FLOW_TYPE_JOIN_EXPERIENCE, requestNo, SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING, 
				tradeAmount, SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_JOIN_EXPERIENCE, 
				subAccountInfoEntity.getRelateType(), subAccountInfoEntity.getRelatePrimary(), SubjectConstant.SUBJECT_TYPE_VALUE);
		subAccountFlowInfoEntity.setMemo(String.format("使用体验金%s元，剩余体验金%s元", tradeAmount.toString(), ArithUtil.sub(useableAmount, tradeAmount).toString()));
		
		// 9、发送通知
		//sendEmail(custInfoRepository.findOne(custId), "", "加入体验宝", "");
		
		// 10、记录日志
		LogInfoEntity logInfoEntity = new LogInfoEntity();
		logInfoEntity.setRelateType(Constant.TABLE_BAO_T_INVEST_INFO);
		logInfoEntity.setRelatePrimary(newInvestInfoEntity.getId());
		logInfoEntity.setLogType(Constant.OPERATION_TYPE_14);
		logInfoEntity.setOperDesc("");
		logInfoEntity.setOperPerson(custId);
		logInfoEntity.setOperIpaddress((String)params.get("ipAddress"));
		logInfoEntity.setMemo(String.format("%s购买体验宝，投资金额%s", custInfoEntity.getLoginName(), tradeAmount.toString()));
		logInfoEntity.setBasicModelProperty(custId, true);
		logInfoEntityRepository.save(logInfoEntity);
		
		// 记录设备信息
		if(params.containsKey("channelNo")) {
			DeviceInfoEntity deviceInfoEntity = new DeviceInfoEntity();
			deviceInfoEntity.setRelateTableIdentification(Constant.TABLE_BAO_T_INVEST_INFO);
			deviceInfoEntity.setRelatePrimary(newInvestInfoEntity.getId());
			deviceInfoEntity.setCustId(custId);
			deviceInfoEntity.setTradeType(Constant.OPERATION_TYPE_14);
			deviceInfoEntity.setMeId((String)params.get("meId"));
			deviceInfoEntity.setMeVersion((String)params.get("meVersion"));
			deviceInfoEntity.setAppSource(appSource);
			deviceInfoEntity.setChannelNo(paramService.findByChannelNo((String)params.get("channelNo")));
			deviceInfoEntity.setBasicModelProperty(custId, true);
			deviceInfoRepository.save(deviceInfoEntity);
		}
		
		return new ResultVo(true);
	}
	
	
	/***
	 * 获取收益发放日期
	 * 
	 * zhangzs
	 * 2015年8月20日
	 * @return
	 * @throws SLException
	 */
	public Date getInvestIncomeDate()throws SLException{
		String investValidTime =  paramService.findInvestValidTime();
		int dayCount = 1;
		if (DateUtils.getCurrentDate("HH:mm:ss").compareTo(investValidTime) >= 0)
			dayCount = 2;
		return DateUtils.getAfterDay(new Date(), dayCount);
		
	}

	@Override
	public ResultVo queryInvestRankInfo(Map<String, Object> params) {
		//设置分页的默认参数
		Object start=params.get("start");
		Object length=params.get("length");
		if(null==start||Strings.isNullOrEmpty((String)start)){
			params.put("start", 0);
		}
		if(null==length||Strings.isNullOrEmpty((String)length)){
			params.put("length", 10);
		}
		List<Map<String,Object>> resultMap = investInfoRepositoryCustom.findInvestRankingInfo(params);
		return new ResultVo(true, "投资排行查询成功", resultMap);
	}

	@Override
	public ResultVo queryProductRate(Map<String, Object> params) throws SLException{
		Map<String, Object> result = Maps.newHashMap();
		result.put("productRateList", fixedInvestRepositoryCustom.getProdctYearRateList(params));
		return new ResultVo(true, "查询产品利率成功", result);
	}
	
}
