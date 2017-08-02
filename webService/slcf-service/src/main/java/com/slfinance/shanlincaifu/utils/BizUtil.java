package com.slfinance.shanlincaifu.utils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;

import org.springframework.util.StringUtils;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AccountFlowInfoEntity;
import com.slfinance.shanlincaifu.entity.AccountInfoEntity;
import com.slfinance.shanlincaifu.entity.CreditRightValueEntity;
import com.slfinance.shanlincaifu.entity.LoanCustInfoEntity;
import com.slfinance.shanlincaifu.entity.LoanDetailInfoEntity;
import com.slfinance.shanlincaifu.entity.LoanInfoEntity;
import com.slfinance.shanlincaifu.entity.RepaymentPlanInfoEntity;
import com.slfinance.shanlincaifu.entity.SubAccountInfoEntity;
import com.slfinance.shanlincaifu.repository.AccountFlowInfoRepository;
import com.slfinance.shanlincaifu.service.FlowNumberService;
import com.slfinance.shanlincaifu.vo.LoanInfoVo;
import com.slfinance.shanlincaifu.vo.RepaymentPlanVo;

public class BizUtil {
	
	public static List<String> ListMapToList(List<Map<String, Object>> queryForList){
		List<String> valueList=new Vector<String>();
		for (Map<String, Object> map : queryForList) {
			for (Entry<String, Object> entry : map.entrySet()) {
				String value = CommonUtils.emptyToString(entry.getValue());
				valueList.add(value);
			}
		}
		return valueList;
	}
	
	public static Map<String,Object> getCurrLoanDetail(Date paramDate,LoanInfoVo loanInfoVo,List<RepaymentPlanInfoEntity> repayPlanList){
		Map<String,Object> loanMap=new HashMap<String, Object>();
		Date investStartDate = loanInfoVo.getInvestStartDate();
		Date investEndDate=loanInfoVo.getInvestEndDate();
		//借款总期数
		int loanTotalTerm=repayPlanList.size();
		//在债权未生效之前导入
		if(CommonUtils.emptyToInt(DateUtils.showDateString(investStartDate))>=CommonUtils.emptyToInt(DateUtils.showDateString(paramDate))){
			//第一期还款计划记录
			RepaymentPlanInfoEntity planEntity = repayPlanList.get(0);
			//第一期还款计划预计还款日
			String expectRepaymentDate=planEntity.getExpectRepaymentDate();
			loanMap.put("currPv", loanInfoVo.getHoldAmount());
			loanMap.put("currTerm", 1);
			loanMap.put("alreadyPaymentTerm", 0);
			loanMap.put("nextExpiry", expectRepaymentDate);
			loanMap.put("lastExpiry", DateUtils.formatDate(investStartDate, "yyyy-MM-dd"));
			loanMap.put("currTremEndDate", expectRepaymentDate);
			return loanMap;
		}
		
		//在债权失效之后导入
		if(CommonUtils.emptyToInt(DateUtils.showDateString(investEndDate))<CommonUtils.emptyToInt(DateUtils.showDateString(paramDate))){
			RepaymentPlanInfoEntity planEntity =null;
			if(loanTotalTerm==1){
				planEntity = repayPlanList.get(0);
			}else{
				planEntity = repayPlanList.get(loanTotalTerm-2);
			}
			
			loanMap.put("currPv", 0);
			loanMap.put("currTerm", loanTotalTerm);
			loanMap.put("alreadyPaymentTerm", loanTotalTerm);
			loanMap.put("nextExpiry",DateUtils.formatDate(investEndDate, "yyyy-MM-dd"));
			loanMap.put("lastExpiry", planEntity.getExpectRepaymentDate());
			loanMap.put("currTremEndDate", DateUtils.formatDate(investEndDate, "yyyy-MM-dd"));
			return loanMap;
		}
		
		//上一还款日,默认放款日期
		Date prevRepayDay=loanInfoVo.getInvestStartDate();
		//剩余本金,第一期为持有金额
		BigDecimal remainderPrincipal=loanInfoVo.getHoldAmount();
		boolean quite=false;
		for (RepaymentPlanInfoEntity planEntity : repayPlanList) {
			//当前期数
			int currTerm=planEntity.getCurrentTerm();
			//已付款期数
			//int alreadyPaymentTerm=currTerm;
			//下期还款日
			Date nextTermRepayDate = DateUtils.parseDate(planEntity.getExpectRepaymentDate(), "yyyy-MM-dd");
			//当期天数
			int currTermDays = DateUtils.datePhaseDiffer(prevRepayDay, nextTermRepayDate);
			//当期应还利息
			BigDecimal repaymentInterest = planEntity.getRepaymentInterest();
			
			for (int i = 1; i <=currTermDays; i++) {
		          Date valueDate = DateUtils.getAfterDay(prevRepayDay, i);
		          //当前计息天数比例
		          double accrualDayScale=(i*1.0)/currTermDays;
		          //截止当天利息
		          BigDecimal interest=new BigDecimal(String.valueOf(accrualDayScale)).multiply(repaymentInterest);
		          //当前PV值
		          BigDecimal currPv=remainderPrincipal.add(interest);
		          //参数日期
		          int paramDateInt=CommonUtils.emptyToInt(DateUtils.showDateString(paramDate));
		          //价值日期
		          int valueDateInt=CommonUtils.emptyToInt(DateUtils.showDateString(valueDate));
		          //还款日日期
		          //int nextTermRepayDateInt=CommonUtils.emptyToInt(DateUtils.showDateString(nextTermRepayDate));
		         
		          if(paramDateInt==valueDateInt){
		        	  
		        	  //是否为还款日
		        	  /*
		        	  if(paramDateInt==nextTermRepayDateInt){//还款日
		        		  currPv=currPv.subtract(planEntity.getRepaymentTotalAmount());
		        	  }else{
		        		  alreadyPaymentTerm=alreadyPaymentTerm-1;
		        	  }*/
		        	
		        	  quite=true;
		        	  loanMap.put("currPv", currPv.setScale(18, BigDecimal.ROUND_DOWN)); 
					  loanMap.put("currTerm", currTerm);
					  loanMap.put("alreadyPaymentTerm", currTerm-1);
					  loanMap.put("currTremEndDate", planEntity.getExpectRepaymentDate());
					  loanMap.put("nextExpiry", planEntity.getExpectRepaymentDate());
					  
					  if(currTerm==1){//第一期
						  loanMap.put("lastExpiry",DateUtils.formatDate(loanInfoVo.getInvestStartDate(), "yyyy-MM-dd"));
					  }else if(currTerm==loanTotalTerm){//最后一期
						  RepaymentPlanInfoEntity lastPlan = repayPlanList.get(loanTotalTerm-2); //倒数第二期
						  loanMap.put("lastExpiry", lastPlan.getExpectRepaymentDate());
					  }else{
						  RepaymentPlanInfoEntity lastPlan = repayPlanList.get(currTerm-2);
						  loanMap.put("lastExpiry", lastPlan.getExpectRepaymentDate());
					  }
		        	  break;
		          }
			     
		  }
			
		prevRepayDay=nextTermRepayDate;
		remainderPrincipal=planEntity.getRemainderPrincipal();
		if(quite){break;}
		
	   }
		
		return loanMap;
	}
	
	public static List<LoanDetailInfoEntity> computeRate(Map<String, LoanInfoVo> mapLoanVo,Map<String, List<RepaymentPlanInfoEntity>> mapPlanList) throws SLException{
		List<LoanDetailInfoEntity> loanDetailEntityList=new ArrayList<LoanDetailInfoEntity>();
		RepaymentPlanEntitySortUtil sortUtil=new RepaymentPlanEntitySortUtil();
		for (Entry<String, LoanInfoVo> entry : mapLoanVo.entrySet()) {
			LoanDetailInfoEntity loanDetailInfoEntity=new LoanDetailInfoEntity();
			//债权编号
			String loanCode = entry.getKey();
			//债权对象
			LoanInfoVo loanInfoVo = entry.getValue();
			
			List<RepaymentPlanInfoEntity> repayPlanEntityList = mapPlanList.get(loanCode);
			Collections.sort(repayPlanEntityList,sortUtil);
			//持有金额
			BigDecimal holdAmount = CommonUtils.emptyToDecimal(loanInfoVo.getHoldAmount());
			//日利率
			BigDecimal dayRate=BigDecimal.ZERO;
			//月利率
			BigDecimal monthRate=BigDecimal.ZERO;
			//年利率
			BigDecimal yearRate=BigDecimal.ZERO;
			//还款方式
			String repaymentMethod=CommonUtils.emptyToString(loanInfoVo.getRepaymentMethod());
			
			if(Constant.REPAYMENT_METHOD_02.equals(repaymentMethod) || Constant.REPAYMENT_METHOD_03.equals(repaymentMethod)){//每期还息到期付本、到期一次性还本付息
				//获取总期数
				int totalTerm=loanInfoVo.getLoanTerm();
				//获取总利息
				BigDecimal totalInterestAmount=getRepayPlanTotalInterest(repayPlanEntityList);
				//获取借款总天数
				int totalDays=DateUtils.datePhaseDiffer(loanInfoVo.getInvestStartDate(), loanInfoVo.getInvestEndDate());
				//日利率
				dayRate=totalInterestAmount.divide(CommonUtils.emptyToDecimal(totalDays),18,BigDecimal.ROUND_DOWN).divide(holdAmount,18,BigDecimal.ROUND_DOWN);
				//月利率
				monthRate=totalInterestAmount.divide(CommonUtils.emptyToDecimal(totalTerm),18,BigDecimal.ROUND_DOWN).divide(holdAmount,18,BigDecimal.ROUND_DOWN);
			}else {//等额本息
				//月利率
				monthRate = getRepayPlanAvgMonthRate(repayPlanEntityList, loanInfoVo);//平均月利率
				//日利率
				dayRate=monthRate.divide(CommonUtils.emptyToDecimal("30"),18,BigDecimal.ROUND_DOWN);
			}
			
			yearRate=CommonUtils.getEightBitDigit(monthRate.multiply(CommonUtils.emptyToDecimal("12")));
			
			if((yearRate.compareTo(new BigDecimal(Constant.LOAN_MIN_YEAR_RATE))==-1) || (yearRate.compareTo(new BigDecimal(Constant.LOAN_MAX_YEAR_RATE))!=-1)){
				StringBuffer msg=new StringBuffer();
				msg.append("债权编号:").append(loanCode).append(",年化利率为:").append(yearRate.multiply(new BigDecimal("100")).setScale(0, BigDecimal.ROUND_DOWN)).append("%,不在区间7%--20%范围内,请检查!!!");
				throw new SLException(msg.toString());
			}
			
			//封装详情对象
			LoanInfoEntity loanInfoEntity=new LoanInfoEntity();
			loanInfoEntity.setId(loanInfoVo.getLoanId());
			loanDetailInfoEntity.setLoanInfoEntity(loanInfoEntity);
			loanDetailInfoEntity.setDayIrr(dayRate);
			loanDetailInfoEntity.setMonthIrr(monthRate);
			loanDetailInfoEntity.setYearIrr(yearRate);
			
			Map<String,Object> loanMap= getCurrLoanDetail(DateUtils.parseDate(loanInfoVo.getImportDate(), "yyyy-MM-dd"), loanInfoVo, repayPlanEntityList);
			int alreadyPaymentTerm = CommonUtils.emptyToInt(loanMap.get("alreadyPaymentTerm"));
			
			//修改之前已还款状态
			for (int i = 0; i <alreadyPaymentTerm; i++) {
				RepaymentPlanInfoEntity planEntity = repayPlanEntityList.get(i);
				planEntity.setTermAlreadyRepayAmount(planEntity.getRepaymentTotalAmount());
				planEntity.setRepaymentStatus(Constant.REPAYMENT_STATUS_CLEAN);
			}
			
			loanDetailInfoEntity.setImportPv(CommonUtils.emptyToDecimal(loanMap.get("currPv")));
			loanDetailInfoEntity.setCurrTerm(CommonUtils.emptyToDecimal(loanMap.get("currTerm")));
			loanDetailInfoEntity.setAlreadyPaymentTerm(CommonUtils.emptyToDecimal(loanMap.get("alreadyPaymentTerm")));
			loanDetailInfoEntity.setNextExpiry(DateUtils.getDateToTimeStamp(DateUtils.parseDate(CommonUtils.emptyToString(loanMap.get("nextExpiry")), "yyyy-MM-dd")));
			loanDetailInfoEntity.setLastExpiry(DateUtils.getDateToTimeStamp(DateUtils.parseDate(CommonUtils.emptyToString(loanMap.get("lastExpiry")), "yyyy-MM-dd")));
			loanDetailInfoEntity.setCurrTremEndDate(DateUtils.getDateToTimeStamp(DateUtils.parseDate(CommonUtils.emptyToString(loanMap.get("currTremEndDate")), "yyyy-MM-dd")));
			loanDetailInfoEntity.setLastUpdateDate(new Date());
			loanDetailEntityList.add(loanDetailInfoEntity);
		}
		return loanDetailEntityList;
	}
	
	public static Map<String,List<RepaymentPlanInfoEntity>> repaymentPlanToMap(List<RepaymentPlanInfoEntity> planEntityList){
		Map<String,List<RepaymentPlanInfoEntity>> entityMap=new HashMap<String, List<RepaymentPlanInfoEntity>>();
		for (RepaymentPlanInfoEntity entity : planEntityList) {
			if(entityMap.containsKey(entity.getLoanCode())){
				String loanCode=entity.getLoanCode();
				entityMap.get(loanCode).add(entity);
			}else{
				List<RepaymentPlanInfoEntity> repayPlanList=new ArrayList<RepaymentPlanInfoEntity>();
				repayPlanList.add(entity);
				entityMap.put(entity.getLoanCode(), repayPlanList);
			}
		}
		return entityMap;
	}
	
	public static Map<String,LoanInfoVo> mapToLoanVo(List<Map<String, Object>> mapList) throws SLException{
		Map<String,LoanInfoVo> loanInfoVoMap=new HashMap<String, LoanInfoVo>();
		for (Map<String, Object> map : mapList) {
			LoanInfoVo loanInfoVo=new LoanInfoVo();
			loanInfoVo.setLoanId(CommonUtils.emptyToString(map.get("id")));
			loanInfoVo.setLoanCode(CommonUtils.emptyToString(map.get("loanCode")));
			loanInfoVo.setImportDate(CommonUtils.emptyToString(map.get("importDate")));
			loanInfoVo.setInvestStartDate(DateUtils.parseDate(CommonUtils.emptyToString(map.get("investStartDate")), "yyyy-MM-dd"));
			loanInfoVo.setInvestEndDate(DateUtils.parseDate(CommonUtils.emptyToString(map.get("investEndDate")), "yyyy-MM-dd"));
			loanInfoVo.setLoanAmount(CommonUtils.emptyToDecimal(map.get("loanAmount")));
			loanInfoVo.setHoldAmount(CommonUtils.emptyToDecimal(map.get("holdAmount")));
			loanInfoVo.setHoldScale(CommonUtils.emptyToDecimal(map.get("holdScale")));
			loanInfoVo.setRepaymentDay(CommonUtils.emptyToString(map.get("repaymentDay")));
			loanInfoVo.setRepaymentMethod(CommonUtils.emptyToString(map.get("repaymentMethod")));
			loanInfoVo.setLoanTerm(CommonUtils.emptyToInt(map.get("loanTerm")));
			loanInfoVo.setRepaymentCycle(CommonUtils.emptyToInt(map.get("repaymentCycle")));
			loanInfoVoMap.put(CommonUtils.emptyToString(map.get("loanCode")), loanInfoVo);
		}
		return loanInfoVoMap;
	}
	
	public static Set<String> getDistinctLoanCode(List<RepaymentPlanInfoEntity> planEntityList){
		Set<String> loanCodeSet=new HashSet<String>();
		for (RepaymentPlanInfoEntity planEntity : planEntityList) {
			loanCodeSet.add(CommonUtils.emptyToString(planEntity.getLoanCode()));
		}
		return loanCodeSet;
	}
	
	public static Map<String,List<String>> externalLoanGroup(List<LoanCustInfoEntity> LoanCustInfoList){
		Map<String,List<String>> loanGroupMap=new HashMap<String, List<String>>();
		for (LoanCustInfoEntity loanCust : LoanCustInfoList) {
			for (LoanInfoEntity loanInfoEntity : loanCust.getLoanList()) {
				String debtSourceCode=CommonUtils.emptyToString(loanInfoEntity.getDebtSourceCode());
				String loanCode=CommonUtils.emptyToString(loanInfoEntity.getLoanCode());
				
				if(loanGroupMap.containsKey(debtSourceCode)){
					List<String> groupList = loanGroupMap.get(debtSourceCode);
					groupList.add(loanCode);
				}else{
					List<String> loanCodeGroup=new ArrayList<String>();
					loanCodeGroup.add(loanCode);
					loanGroupMap.put(debtSourceCode, loanCodeGroup);
				}
			}
		}
		return loanGroupMap;
	}

	
	public static List<String> getLoanIds(List<RepaymentPlanVo> planVoList){
		List<String> loanIds=new ArrayList<String>();
		Set<String> setIds=new HashSet<String>();
		for (RepaymentPlanVo planVo : planVoList) {
			setIds.add(planVo.getLoanId());
		}
		loanIds.addAll(setIds);
		return loanIds;
	}

	/**
	 * 还款计划按照债权分组
	 * zhoudl
	 * **/
	public static Map<String,List<RepaymentPlanVo>>  repaymentPlanGroup(List<RepaymentPlanVo> voList){
		Map<String,List<RepaymentPlanVo>> voMap=new HashMap<String, List<RepaymentPlanVo>>();
		if((voList!=null) && !voList.isEmpty()){
			for (RepaymentPlanVo vo : voList) {
				String loanId = vo.getLoanId();
				if(voMap.containsKey(loanId)){
					List<RepaymentPlanVo> list = voMap.get(loanId);
					list.add(vo);
				}else{
					List<RepaymentPlanVo> list=new ArrayList<RepaymentPlanVo>();
					list.add(vo);
					voMap.put(loanId, list);
				}
			}
		}
		return voMap;
	}
	
	/**
	 * 组装债权PV实体jilv
	 * **/
	public static List<CreditRightValueEntity> getCreditRightValueEntitys(List<RepaymentPlanVo> planVoList){
		List<CreditRightValueEntity> entityList=new ArrayList<CreditRightValueEntity>();
		if((planVoList==null)||planVoList.isEmpty()){
			return entityList;
		}
		Map<String, List<RepaymentPlanVo>> groupMap = BizUtil.repaymentPlanGroup(planVoList);
		RepaymentPlanSortUtil sortUtil=new RepaymentPlanSortUtil();
		
		for (Entry<String,List<RepaymentPlanVo>> entry : groupMap.entrySet()) {
			List<RepaymentPlanVo> voList = entry.getValue();
			Collections.sort(voList, sortUtil);

			RepaymentPlanVo planVo=voList.get(0);
			//上期还款日期(默认起息日)
			Date preTremRepayDate=planVo.getInvestStartDate();
			//剩余本金(第一期为持有金额)
			BigDecimal remainderPrincipal=planVo.getHoldAmount();
			
			//添加放款当天PV值
			CreditRightValueEntity GrantCurrDayEntity=new CreditRightValueEntity();
			GrantCurrDayEntity.setId(UUID.randomUUID().toString());
			GrantCurrDayEntity.setValueDate(DateUtils.showDateString(planVo.getInvestStartDate()));
			GrantCurrDayEntity.setValueRepaymentAfter(remainderPrincipal);
			GrantCurrDayEntity.setValueRepaymentBefore(remainderPrincipal);
			GrantCurrDayEntity.setLoanId(planVo.getLoanId());
			entityList.add(GrantCurrDayEntity);
			
			for (RepaymentPlanVo vo : voList) {
				//当期应还利息
				BigDecimal repaymentInterest = vo.getRepaymentInterest();
				//下期还款日期
				Date nextTermRepayDate=vo.getExpectRepaymentDate();
				//当期天数(整形)
				int currTermDays = DateUtils.datePhaseDiffer(preTremRepayDate, nextTermRepayDate);
				//当前总天数(BigDecimal)
				BigDecimal currTermDaysDecimal=CommonUtils.emptyToDecimal(currTermDays);
				for (int i = 1; i <=currTermDays; i++) {
					//价值对应日期
					Date valueDate = DateUtils.getAfterDay(preTremRepayDate, i);
					//当前利息占当期利息比例
					BigDecimal scaleDecimal=CommonUtils.emptyToDecimal(i).divide(currTermDaysDecimal,18,BigDecimal.ROUND_DOWN);
					//截止当天利息
					BigDecimal interAmount=scaleDecimal.multiply(repaymentInterest).setScale(8, BigDecimal.ROUND_DOWN);
					
					BigDecimal beforePv=remainderPrincipal.add(interAmount);
					BigDecimal afterPv=remainderPrincipal.add(interAmount);
					//判断是否为还款日
					if(i==currTermDays){
						afterPv=afterPv.subtract(vo.getRepaymentTotalAmount());
					}
					CreditRightValueEntity entity=new CreditRightValueEntity();
					entity.setId(UUID.randomUUID().toString());
					entity.setValueDate(DateUtils.showDateString(valueDate));
					entity.setValueRepaymentAfter(afterPv);
					entity.setValueRepaymentBefore(beforePv);
					entity.setLoanId(vo.getLoanId());
					entityList.add(entity);
				}
				//当前还款日,变为上期还款日
				preTremRepayDate=vo.getExpectRepaymentDate();
				//下期剩余本金
				remainderPrincipal=vo.getRemainderPrincipal();
			}
			
		}
		
		return entityList;
	}
	
	
	/**
	 * 生成账户流水记录(善林财富结息，体验宝结息，体验宝到期赎回)
	 * @param sourceSubAccount
	 * @param tradeType
	 * @param reqeustNo
	 * @param bankrollFlowDirection
	 * @param tradeAmount
	 * @param subjectType
	 * @param memo
	 */
	public static void saveAccountFlow(AccountInfoEntity accountInfoEntity,SubAccountInfoEntity sourceSubAccount, 
			String tradeType, String reqeustNo, 
			String bankrollFlowDirection, BigDecimal tradeAmount, String subjectType,String memo,String desc,String destAccountId,Date tradeDate, FlowNumberService numberService,AccountFlowInfoRepository accountFlowInfoRepository,String flowType)
	{
		AccountFlowInfoEntity accountFlowInfo = new AccountFlowInfoEntity();
		if(accountInfoEntity==null){
			accountFlowInfo.setAccountTotalAmount(sourceSubAccount.getAccountTotalValue());
			accountFlowInfo.setAccountAvailable(sourceSubAccount.getAccountAvailableValue());
			accountFlowInfo.setAccountFreezeAmount(sourceSubAccount.getAccountFreezeValue());
			accountFlowInfo.setAccountType(Constant.ACCOUNT_TYPE_SUB);
			accountFlowInfo.setAccountId(sourceSubAccount.getId());
			accountFlowInfo.setCustId(sourceSubAccount.getCustId());
		}else{
			accountFlowInfo.setAccountTotalAmount(accountInfoEntity.getAccountTotalAmount());
			accountFlowInfo.setAccountAvailable(accountInfoEntity.getAccountAvailableAmount());
			accountFlowInfo.setAccountFreezeAmount(accountInfoEntity.getAccountFreezeAmount());
			accountFlowInfo.setAccountType(Constant.ACCOUNT_TYPE_MAIN);
			accountFlowInfo.setAccountId(accountInfoEntity.getId());
			accountFlowInfo.setCustId(accountInfoEntity.getCustId());
		}
		

		accountFlowInfo.setTradeType(tradeType);
		accountFlowInfo.setRequestNo(reqeustNo);// 请求编号
		accountFlowInfo.setTradeNo(numberService.generateTradeNumber()); // 交易编号
		accountFlowInfo.setBankrollFlowDirection(bankrollFlowDirection);//资金方向转出
		accountFlowInfo.setTradeAmount(tradeAmount);
		accountFlowInfo.setTradeDate(tradeDate);
		accountFlowInfo.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
		accountFlowInfo.setFlowType(flowType);
		accountFlowInfo.setMemo(memo);
		accountFlowInfo.setTargetAccount(destAccountId);
		if(accountInfoEntity==null){
			accountFlowInfo.setRelateType(sourceSubAccount.getRelateType());
			accountFlowInfo.setRelatePrimary(sourceSubAccount.getRelatePrimary());
		}else{
			accountFlowInfo.setRelateType(sourceSubAccount.getRelateType());
			accountFlowInfo.setRelatePrimary(sourceSubAccount.getRelatePrimary());
		}
		if(sourceSubAccount!=null)
			accountFlowInfo.setCashAmount(sourceSubAccount.getAccountAmount());
		accountFlowInfo = accountFlowInfoRepository.save(accountFlowInfo);
		
//		// 3.3 记录账户流水详情表
//		AccountFlowDetailEntity accountFlowDetailEntity = new AccountFlowDetailEntity();
//		accountFlowDetailEntity.setAccountFlowId(accountFlowInfo.getId());
//		accountFlowDetailEntity.setSubjectType(subjectType);
//		accountFlowDetailEntity.setSubjectDirection(bankrollFlowDirection);
//		accountFlowDetailEntity.setTradeAmount(tradeAmount);
//		accountFlowDetailEntity.setTradeDesc(desc);
//		accountFlowDetailEntity.setTargetAccount(destAccountId);
//		accountFlowDetailEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
//		accountFlowDetailRepository.save(accountFlowDetailEntity);	
//	
//		// 3.4 记录业务流水
//		FlowBusiRelationEntity flowBusiRelationEntity = new FlowBusiRelationEntity();
//		flowBusiRelationEntity.setAccountFlowId(accountFlowInfo.getId());
//		if(accountInfoEntity==null){
//			flowBusiRelationEntity.setRelateType(sourceSubAccount.getRelateType());
//			flowBusiRelationEntity.setRelatePrimary(sourceSubAccount.getRelatePrimary());
//		}else{
//			flowBusiRelationEntity.setRelateType(sourceSubAccount.getRelateType());
//			flowBusiRelationEntity.setRelatePrimary(sourceSubAccount.getRelatePrimary());
//		}
//		flowBusiRelationEntity.setCreateDate(new Date());
//		flowBusiRelationRepository.save(flowBusiRelationEntity);	
	}
	
	/**
	 * 导入债权记录流水
	 * **/
	public static AccountFlowInfoEntity getAccountFlowEntity(AccountInfoEntity accountInfoEntity,FlowNumberService numberService,BigDecimal tradeAmount){
		AccountFlowInfoEntity accountFlowInfoEntity=new AccountFlowInfoEntity();
		BigDecimal accountAvailableAmount = accountInfoEntity.getAccountAvailableAmount().subtract(tradeAmount);
		accountFlowInfoEntity.setAccountAvailable(accountAvailableAmount);
		accountFlowInfoEntity.setAccountFreezeAmount(accountInfoEntity.getAccountFreezeAmount());
		accountFlowInfoEntity.setAccountId(accountInfoEntity.getId());
		BigDecimal accountTotalAmount = accountInfoEntity.getAccountTotalAmount().subtract(tradeAmount);
		accountFlowInfoEntity.setAccountTotalAmount(accountTotalAmount);
		accountFlowInfoEntity.setAccountType(Constant.ACCOUNT_TYPE_MAIN);
		accountFlowInfoEntity.setBankrollFlowDirection(SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT);
		accountFlowInfoEntity.setCreateDate(new Date());
		accountFlowInfoEntity.setCustId(Constant.CUST_ID_WEALTH_CENTER);
		accountFlowInfoEntity.setRecordStatus(Constant.VALID_STATUS_VALID);
		accountFlowInfoEntity.setRequestNo(numberService.generateTradeBatchNumber());
		accountFlowInfoEntity.setTradeAmount(tradeAmount);
		accountFlowInfoEntity.setTradeDate(new Date());
		accountFlowInfoEntity.setTradeNo(numberService.generateTradeNumber());
		accountFlowInfoEntity.setTradeType(SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_TENDER_MAKEOVER_IN);
		accountFlowInfoEntity.setFlowType(SubjectConstant.SUBJECT_TYPE_AMOUNT);
		accountFlowInfoEntity.setVersion(0);
		return accountFlowInfoEntity;
	}
	
//	/**
//	 * 导入债权记录流水详情
//	 * **/
//	public static AccountFlowDetailEntity getAccountFlowDetailEntity(String accountFlowId,BigDecimal tradeAmount){
//		AccountFlowDetailEntity accountFlowDetailEntity=new AccountFlowDetailEntity();
//		accountFlowDetailEntity.setAccountFlowId(accountFlowId);
//		accountFlowDetailEntity.setCreateDate(new Date());
//		accountFlowDetailEntity.setRecordStatus(Constant.VALID_STATUS_VALID);
//		accountFlowDetailEntity.setSubjectDirection(SubjectConstant.SUBJECT_TYPE_DIRECTION_OUT);
//		accountFlowDetailEntity.setSubjectType(SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_TENDER_MAKEOVER_IN);
//		accountFlowDetailEntity.setTradeAmount(tradeAmount);
//		accountFlowDetailEntity.setTradeDesc(SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_TENDER_MAKEOVER_IN);
//		accountFlowDetailEntity.setVersion(0);
//		return accountFlowDetailEntity;
//	}
	
	/**
	 * 根据债权持有比例,重新计算还款记录相应金额
	 * **/
	public static void computeRepaymentPlanAmountByHoldScale(Map<String, LoanInfoVo> mapToLoanVo,List<RepaymentPlanInfoEntity> planEntityList){
		for (RepaymentPlanInfoEntity planEntity : planEntityList) {
			LoanInfoVo loanInfoVo = mapToLoanVo.get(planEntity.getLoanCode());
			planEntity.getLoanEntity().setId(loanInfoVo.getLoanId());
			//持有比例
			BigDecimal holdScale = loanInfoVo.getHoldScale();
			//当期应还款利息
			BigDecimal repaymentInterest = holdScale.multiply(planEntity.getRepaymentInterest()).setScale(8,BigDecimal.ROUND_DOWN);
			//当期应还款本金
			BigDecimal repaymentPrincipal=holdScale.multiply(planEntity.getRepaymentPrincipal()).setScale(8,BigDecimal.ROUND_DOWN);
			//当期还款总金额
			//BigDecimal repaymentTotalAmount=holdScale.multiply(planEntity.getRepaymentTotalAmount()).setScale(8,BigDecimal.ROUND_DOWN);
			BigDecimal repaymentTotalAmount=repaymentInterest.add(repaymentPrincipal);
			//当期剩余本金
			BigDecimal remainderPrincipal=holdScale.multiply(planEntity.getRemainderPrincipal()).setScale(8,BigDecimal.ROUND_DOWN);
			//提前结清金额
			BigDecimal advanceCleanupTotalAmount=holdScale.multiply(planEntity.getAdvanceCleanupTotalAmount()).setScale(8,BigDecimal.ROUND_DOWN);
			//利息
			planEntity.setRepaymentInterest(repaymentInterest);
			//本金
			planEntity.setRepaymentPrincipal(repaymentPrincipal);
			//总金额
			planEntity.setRepaymentTotalAmount(repaymentTotalAmount);
			//剩余本金
			planEntity.setRemainderPrincipal(remainderPrincipal);
			//提前结清金额
			planEntity.setAdvanceCleanupTotalAmount(advanceCleanupTotalAmount);
			planEntity.setRecordStatus(Constant.VALID_STATUS_VALID);
			planEntity.setRepaymentStatus(Constant.REPAYMENT_STATUS_WAIT);
		}
	}
	
	/**
	 * 验证还款记录与债权信息是否匹配
	 * **/
	public static String vaildRepayPlan(Map<String, LoanInfoVo> mapLoanVo,Map<String, List<RepaymentPlanInfoEntity>> repayPlanMap){
		StringBuffer validMsg=new StringBuffer("");
		RepaymentPlanEntitySortUtil sortUtil=new RepaymentPlanEntitySortUtil();
		for (Entry<String, LoanInfoVo> entry : mapLoanVo.entrySet()) {
			//key:债权编号
			String loanCode = entry.getKey();
			//value:债权对象
			LoanInfoVo loanInfoVo = entry.getValue();
			//根据债权编号获取对应的还款记录
			List<RepaymentPlanInfoEntity> planList = repayPlanMap.get(loanCode);
			Collections.sort(planList, sortUtil);
			//计划记录个数
			int planCount = planList.size();
			//贷款期数
			int loanTerm = loanInfoVo.getLoanTerm();
			//还款周期
			int repaymentCycle= loanInfoVo.getRepaymentCycle();
			
			if(Constant.REPAYMENT_METHOD_03.equals(loanInfoVo.getRepaymentMethod())){//到期一次性还本付息
				int currTerm=planList.get(0).getCurrentTerm();
				if((planCount!=1) || (currTerm!=1)){
					validMsg.append("债权编号:").append(loanCode).append(",只能存在一条还款记录,并且只能为第一期,(到期一次性还本付息)");
				}
				
			}else{
				int term=loanTerm/repaymentCycle;
				if(planCount!=term){
					validMsg.append("债权编号:").append(loanCode).append(",还款记录数、贷款期数、还款周期不匹配,请检查!!!");
				}
			}
			
			if(!"".equals(validMsg.toString())){break;}
			
			//获取还款计划中还款总本金
			BigDecimal repayPlanTotalAmount = getRepayPlanTotalAmount(planList);
			BigDecimal loanAmount = CommonUtils.emptyToDecimal(loanInfoVo.getLoanAmount());
			//验证还款总本金与持有金额是否一致
			if(Math.abs(ArithUtil.sub(loanAmount, repayPlanTotalAmount).doubleValue()) > 1){
				validMsg.append("债权编号:").append(loanCode).append(",还款计划中本金总额与贷款本金不一致,请检查!!!)");
			}
			
			if(!"".equals(validMsg.toString())){break;}
			
			//验证所有还款期数中,应还本金或剩余本金是否一致
			validMsg.append(validRepayPricipal(planList, loanInfoVo));
			if(!"".equals(validMsg.toString())){break;}
			
		}
		
		return validMsg.toString();
	}
	
	/**
	 * 借款信息逻辑验证
	 * **/
	public static String vaildLoanInfo(List<LoanInfoEntity> loanList){
		StringBuffer validMsg=new StringBuffer("");
		for (LoanInfoEntity loan : loanList) {
			String loanCode = CommonUtils.emptyToString(loan.getLoanCode()).trim();
			String debtSourceCode=CommonUtils.emptyToString(loan.getDebtSourceCode()).trim();
			BigDecimal loanAmount = CommonUtils.emptyToDecimal(loan.getLoanAmount());
			BigDecimal holdAmount = CommonUtils.emptyToDecimal(loan.getHoldAmount());
			
			int repaymentCycle =CommonUtils.emptyToInt(loan.getRepaymentCycle());
			int loanTerm = CommonUtils.emptyToInt(loan.getLoanTerm());
			String repaymentMethod = CommonUtils.emptyToString(loan.getRepaymentMethod());
			
			// update by wangjf 2016-5-11 12:02 
			// 1)将起息时间由原先的首个还款日（Excel中的首个还款日-还款频率）改为放款日期
			// 2)由于首个还款日可能跟放款日同月(如1号放款，还款日为当月25号)，也可以是放款日的下个月(如15号放款，还款日为下个月10号)，
			//   故到期日-期数 + 1 = 首还款日；0<首还款日-放款日<=1
			loan.setRepaymentDay(DateUtils.formatDate(loan.getInvestEndDate(), "d"));
			loan.setGrantDate(loan.getFirstRepayDay());
			loan.setInvestStartDate(loan.getFirstRepayDay());
			Timestamp InvestStartDate = loan.getInvestStartDate();
		    Timestamp investEndDate=loan.getInvestEndDate();

//		    int spaceMonths = DateUtils.monthPhaseDiffer(InvestStartDate, investEndDate);
//		    if(Math.abs(loanTerm - spaceMonths) > 1){
//		    	validMsg.append("债权编号:").append(loanCode).append(",借款月数与实际借款周期不匹配,请检查!!!");
//		    }
//		    
//		    if(!StringUtils.isEmpty(validMsg.toString())){break;}
		    
		    // 检查放款日不能大于等于首期还款日
		    int diffMonth = DateUtils.monthPhaseDiffer(InvestStartDate, DateUtils.getAfterMonth(investEndDate, -loanTerm + 1));
		    if(diffMonth <= 0 || diffMonth >1) {
		    	validMsg.append("债权编号:").append(loanCode).append(",放款日与首个还款日间隔须大于0且小于等于1个月,请检查!!!");
		    }
		    
		    if(!StringUtils.isEmpty(validMsg.toString())){break;}
			
			if(holdAmount.compareTo(loanAmount)==1){//持有金额大于借款金额
				validMsg.append("债权编号:").append(loanCode).append(",持有金额不能大于借款金额,请检查!!!");
			}
			
			if(!StringUtils.isEmpty(validMsg.toString())){break;}
			
			Date firstRepayDayYmd = DateUtils.truncateDate(loan.getFirstRepayDay());
			Date investEndDateYmd = DateUtils.truncateDate(loan.getInvestEndDate());
			if(firstRepayDayYmd.after(investEndDateYmd)){//首还款日在债权到期日之后
				validMsg.append("债权编号:").append(loanCode).append(",首还款日不能在债权到期日之后,请检查!!!");
			}
			
			if(!StringUtils.isEmpty(validMsg.toString())){break;}
			
			if(!loanCode.startsWith(debtSourceCode)){
				validMsg.append("第三方机构号: ").append(debtSourceCode).append(" 与债权编号:").append(loanCode).append(",不匹配,请检查!!!");
			}
			
			if(!StringUtils.isEmpty(validMsg.toString())){break;}
			
		
			if((loanTerm<repaymentCycle)||((loanTerm%repaymentCycle)!=0) || (Constant.REPAYMENT_METHOD_03.equals(repaymentMethod.trim()) && loanTerm!=repaymentCycle)){
				validMsg.append("第三方机构号: ").append(debtSourceCode).append(" 与债权编号:").append(loanCode).append(",贷款期数与还款期数不匹配,请检查!!!");
			}
			
			if(!StringUtils.isEmpty(validMsg.toString())){break;}
			
		}
		return validMsg.toString();
	}
	
	/**根据还款计划获取总本金**/
	public static BigDecimal getRepayPlanTotalAmount(List<RepaymentPlanInfoEntity> planList){
		BigDecimal repayTotalAmount=new BigDecimal("0");
		for (RepaymentPlanInfoEntity repayEntity : planList) {
			repayTotalAmount=repayTotalAmount.add(repayEntity.getRepaymentPrincipal());
		}
		return repayTotalAmount;
	}
	
	/**根据还款计划获取总利息**/
	public static BigDecimal getRepayPlanTotalInterest(List<RepaymentPlanInfoEntity> planList){
		BigDecimal repayTotalInterest=new BigDecimal("0");
		for (RepaymentPlanInfoEntity repayEntity : planList) {
			repayTotalInterest=repayTotalInterest.add(repayEntity.getRepaymentInterest());
		}
		return repayTotalInterest;
	}
	
	/**根据还款计划获取平均月利息**/
	public static BigDecimal getRepayPlanAvgMonthRate(List<RepaymentPlanInfoEntity> planList,LoanInfoVo loanInfoVo){
		int totalTerm = loanInfoVo.getLoanTerm();
		BigDecimal totalMonthRate=new BigDecimal("0");
		//上期剩余本金,默认为持有金额
		BigDecimal prevTermRemPricipal=loanInfoVo.getHoldAmount();
		for (RepaymentPlanInfoEntity planEntity : planList) {
			//当期应还利息
			BigDecimal repaymentInterest=CommonUtils.emptyToDecimal(planEntity.getRepaymentInterest());
			//当前月利率
			BigDecimal currentMonthRate=repaymentInterest.divide(prevTermRemPricipal,18,BigDecimal.ROUND_DOWN);
			totalMonthRate=totalMonthRate.add(currentMonthRate);
			prevTermRemPricipal=planEntity.getRemainderPrincipal();
		}
		//平均月利率
		BigDecimal avgMonthRate=totalMonthRate.divide(new BigDecimal(CommonUtils.emptyToString(totalTerm)),18,BigDecimal.ROUND_DOWN);
		return avgMonthRate;
	}
	
	/**根据还款计划验证每期还款本金**/
	public static String validRepayPricipal(List<RepaymentPlanInfoEntity> planList,LoanInfoVo loanInfoVo){
		StringBuffer validMsg=new StringBuffer("");
		//还款频率
		int repaymentCycle = loanInfoVo.getRepaymentCycle();
		//上一还款日默认为放款日期,即开始计息日期
		Date preRepaymentDay = loanInfoVo.getInvestStartDate();
		//上期剩余本金,默认为贷款本金
		BigDecimal prevTermRemPricipal=loanInfoVo.getLoanAmount();
		for (RepaymentPlanInfoEntity planEntity : planList) {
			int currTerm=planEntity.getCurrentTerm();
			//当期应还本金
			BigDecimal repaymentPrincipal=CommonUtils.emptyToDecimal(planEntity.getRepaymentPrincipal());
			//剩余本金
			BigDecimal remainderPrincipal=CommonUtils.emptyToDecimal(planEntity.getRemainderPrincipal());
			BigDecimal principal=repaymentPrincipal.add(remainderPrincipal);
			if(Math.abs(ArithUtil.sub(principal, prevTermRemPricipal).doubleValue()) > 1){
				validMsg.append("债权编号:").append(loanInfoVo.getLoanCode()).append(",第").append(currTerm).append("期,").append(",应还本金或剩余本金错误,请检查!!!");
			}
			if(!"".equals(validMsg.toString())){
				break;
			}
			
			//当前预计还款日期
			Date expectRepaymentDate=DateUtils.parseStandardDate(planEntity.getExpectRepaymentDate());
			//当前还款日期与上期间隔周期(月数)
			int spaceMonths = DateUtils.monthPhaseDiffer(preRepaymentDay, expectRepaymentDate);
			if(spaceMonths!=repaymentCycle && currTerm != 1){
				validMsg.append("债权编号:").append(loanInfoVo.getLoanCode()).append(",第").append(currTerm).append("期,").append(",间隔月数与还款频率不一致,请检查!!!");
			}
			
			if(!"".equals(validMsg.toString())){
				break;
			}
			
			prevTermRemPricipal=planEntity.getRemainderPrincipal();
			preRepaymentDay=DateUtils.parseStandardDate(planEntity.getExpectRepaymentDate());
		}
		
		return validMsg.toString();
	}
	
	public static Map<String,String> listMapToStringMap(List<Map<String, Object>> debtSourceCodeMapList){
		Map<String,String> debtContainerMap=new HashMap<String, String>();
		for (Map<String, Object> debtMap : debtSourceCodeMapList) {
			StringBuffer debtSb=new StringBuffer();
			String thirdPartyName=CommonUtils.emptyToString(debtMap.get("parameterName"));
			String thirdPartyCode=CommonUtils.emptyToString(debtMap.get("value"));
			debtSb.append(thirdPartyCode).append("=").append(thirdPartyName);
			debtContainerMap.put(debtSb.toString(), debtSb.toString());
		}
		return debtContainerMap;
	}
}
