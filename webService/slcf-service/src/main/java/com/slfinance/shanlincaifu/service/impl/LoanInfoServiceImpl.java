package com.slfinance.shanlincaifu.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AccountFlowInfoEntity;
import com.slfinance.shanlincaifu.entity.AccountInfoEntity;
import com.slfinance.shanlincaifu.entity.CreditRightValueEntity;
import com.slfinance.shanlincaifu.entity.LoanCustInfoEntity;
import com.slfinance.shanlincaifu.entity.LoanDetailInfoEntity;
import com.slfinance.shanlincaifu.entity.LoanInfoEntity;
import com.slfinance.shanlincaifu.entity.RepaymentPlanInfoEntity;
import com.slfinance.shanlincaifu.repository.AccountFlowInfoRepository;
import com.slfinance.shanlincaifu.repository.AccountInfoRepository;
import com.slfinance.shanlincaifu.repository.LoanCustInfoRepository;
import com.slfinance.shanlincaifu.repository.LoanInfoRepository;
import com.slfinance.shanlincaifu.repository.custom.AccountInfoRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.CustDailyValueHistoryRepositroyCustom;
import com.slfinance.shanlincaifu.repository.custom.LoanAllotHistoryRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.LoanInfoRepositoryCustom;
import com.slfinance.shanlincaifu.service.CustAccountService;
import com.slfinance.shanlincaifu.service.FlowNumberService;
import com.slfinance.shanlincaifu.service.LoanInfoService;
import com.slfinance.shanlincaifu.utils.BizUtil;
import com.slfinance.shanlincaifu.utils.CommonUtils;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.vo.LoanInfoVo;
import com.slfinance.shanlincaifu.vo.RepaymentPlanVo;
import com.slfinance.vo.ResultVo;

@Service("loanInfoService")
public class LoanInfoServiceImpl implements LoanInfoService {
	
	@Autowired
	private LoanInfoRepositoryCustom loanInfoRepositoryCustom;
	
	@Autowired
	private LoanInfoRepository loanInfoRepository;
	
	@Autowired
	private LoanCustInfoRepository loanCustInfoRepository;
	
	@Autowired
	private AccountInfoRepository accountInfoRepository;
	
	@Autowired
	private AccountInfoRepositoryCustom accountInfoRepositoryCustom;
	
	@Autowired
	private FlowNumberService numberService;
	
//	@Autowired
//	private AccountFlowDetailRepository accountFlowDetailRepository;
	
	@Autowired
	private AccountFlowInfoRepository accountFlowInfoRepository;
	
	@Autowired
	private CustDailyValueHistoryRepositroyCustom custDailyValueHistoryRepositroyCustom;
	
	@Autowired
	private LoanAllotHistoryRepositoryCustom loanAllotHistoryRepositoryCustom;
	
	@Autowired
	private CustAccountService custAccountService;
	
	/**
	 * @desc 预算债权PV值
	 * @author zhoudl
	 * **/
	@Transactional(readOnly=false,rollbackFor=SLException.class)
	@Override
	public void execLoanPv() throws SLException {
		List<RepaymentPlanVo> planVoList = loanInfoRepositoryCustom.queryUnExecutePvLoan();
		if((planVoList==null)||planVoList.isEmpty()){return;}
		List<CreditRightValueEntity> entityList = BizUtil.getCreditRightValueEntitys(planVoList);
		loanInfoRepositoryCustom.batchInsertPv(entityList);
		List<String> loanIds = BizUtil.getLoanIds(planVoList);
		loanInfoRepositoryCustom.batchUpdateLoanExecStatus(loanIds);
		
	}

	/**
	 * @desc 根据参数条件,查询相应的债权信息
	 * @param debtSourceCode 债权来源编号
	 * @param repaymentDay   还款日
	 * @param importDate     债权导入日期
	 * @param repaymentCycle 还款频率
	 * @param assetTypeCode  资产类型
	 * @param start          起始记录参数
	 * @param length         每页记录大小
	 * @author zhoudl
	 * @date 2015年04月15日 下午午13:41:36
	 * **/
	@Override
	public Map<String, Object> queryConditionLoan(Map<String, Object> conditionMap) throws SLException {
		Map<String, Object> pageMap = loanInfoRepositoryCustom.queryConditionLoan(conditionMap);
		return pageMap;
	}

	/**
	 * @desc 根据主键ID,查询还款明细
	 * @param loanId 债权主键ID
	 * @return java.util.Map
	 * @author zhoudl
	 * **/
	@Override
	public Map<String,Object> queryLoanRepaymentDetail(Map<String, Object> conditionMap) throws SLException {
		List<Map<String, Object>> mapList=loanInfoRepositoryCustom.queryLoanRepaymentDetail(conditionMap);
		Map<String,Object> resultMap=new HashMap<String, Object>();
		if((mapList!=null)&&!mapList.isEmpty()){
			Map<String, Object> firstMap = mapList.get(0);
			resultMap.put("data", mapList);
			resultMap.put("debtSourceCode", firstMap.get("debtSourceCode"));
			resultMap.put("loanCode", firstMap.get("loanCode"));
			resultMap.put("custName", firstMap.get("custName"));
			resultMap.put("repaymentCycle", firstMap.get("repaymentCycle"));
			resultMap.put("assetTypeCode", firstMap.get("assetTypeCode"));
		}
		return resultMap;
	}

	/**
	 * @desc 获取所有借款客户信息
	 * @return java.util.Map
	 * @author zhoudl
	 * **/
	@Override
	public Map<String, LoanCustInfoEntity> findAllLoanCust() throws SLException {
		Map<String, LoanCustInfoEntity> loanCustMap=new HashMap<String, LoanCustInfoEntity>();
		Iterable<LoanCustInfoEntity> iterable = loanCustInfoRepository.findAll();
		for (LoanCustInfoEntity entity : iterable) {
			String key=CommonUtils.emptyToString(entity.getCredentialsType())+CommonUtils.emptyToString(entity.getCredentialsCode());
			if(!StringUtils.isEmpty(key)){
				loanCustMap.put(key, entity);
			}
		}
		return loanCustMap;
	}

	/**
	 * @desc 批量导入债权信息
	 * @param LoanCustInfoEntity 借款客户信息对象
	 * @return ResultVo
	 * @author zhoudl
	 * **/
	@Transactional(readOnly=false,rollbackFor=SLException.class)
	@Override
	public ResultVo importLoanInfo(List<LoanCustInfoEntity> LoanCustInfoList) throws SLException {
		if((LoanCustInfoList==null) || LoanCustInfoList.isEmpty()){
			throw new SLException("债权记录不能为空,请检查");
		}
		
		//借款客户集合
		List<LoanCustInfoEntity> loanCustList=new ArrayList<LoanCustInfoEntity>();
		//债权列表集合
		List<LoanInfoEntity> loanList=new ArrayList<LoanInfoEntity>();
		//借款详情集合
		List<LoanDetailInfoEntity> loanDetailList=new ArrayList<LoanDetailInfoEntity>();

		Map<String, LoanCustInfoEntity> loanCustMap = this.findAllLoanCust();
		
		for (LoanCustInfoEntity loanCust : LoanCustInfoList) {
			String key=CommonUtils.emptyToString(loanCust.getCredentialsType())+CommonUtils.emptyToString(loanCust.getCredentialsCode());
			if(loanCustMap.containsKey(key)){
				LoanCustInfoEntity loanCustInfoEntity = loanCustMap.get(key);
				for (LoanInfoEntity loan : loanCust.getLoanList()) {
					String loanId=UUID.randomUUID().toString();
					loan.setId(loanId);
					loan.setLoanCustInfoEntity(loanCustInfoEntity);
					loan.setCreditAcctStatus(Constant.REG_ENABLE_STATUS);
					//持有比例
					BigDecimal holdScale = loan.getHoldAmount().divide(loan.getLoanAmount(),18,BigDecimal.ROUND_DOWN);
					loan.setHoldScale(holdScale);
					loanList.add(loan);
					//债权详情
					LoanDetailInfoEntity loanDetailInfoEntity=new LoanDetailInfoEntity();
					loanDetailInfoEntity.setId(UUID.randomUUID().toString());
					loanDetailInfoEntity.setLoanInfoEntity(loan);
					loanDetailInfoEntity.setCreditRemainderPrincipal(loan.getHoldAmount());
					loanDetailInfoEntity.setWealthRemainderPrincipal(loan.getHoldAmount());
					loanDetailInfoEntity.setCreditRightStatus(Constant.REG_ENABLE_STATUS);
					loanDetailInfoEntity.setCreateDate(new Date());
					loanDetailList.add(loanDetailInfoEntity);
				}
				
			}else{
				//添加新客户
				String custId=UUID.randomUUID().toString();
				loanCust.setId(custId);
				loanCust.setCreateDate(new Date());
				loanCustList.add(loanCust);
				//防止重复插入
				loanCustMap.put(key, loanCust);
				for (LoanInfoEntity loan : loanCust.getLoanList()) {
					String loanId=UUID.randomUUID().toString();
					loan.setId(loanId);
					loan.setLoanCustInfoEntity(loanCust);
					loan.setCreditAcctStatus(Constant.REG_ENABLE_STATUS);
					//持有比例
					BigDecimal holdScale = loan.getHoldAmount().divide(loan.getLoanAmount(),18,BigDecimal.ROUND_DOWN);
					loan.setHoldScale(holdScale);
					loanList.add(loan);
			        //债权详情
					LoanDetailInfoEntity loanDetailInfoEntity=new LoanDetailInfoEntity();
					loanDetailInfoEntity.setId(UUID.randomUUID().toString());
					loanDetailInfoEntity.setLoanInfoEntity(loan);
					loanDetailInfoEntity.setCreditRemainderPrincipal(loan.getHoldAmount());
					loanDetailInfoEntity.setWealthRemainderPrincipal(loan.getHoldAmount());
					loanDetailInfoEntity.setCreditRightStatus(Constant.REG_ENABLE_STATUS);
					loanDetailInfoEntity.setCreateDate(new Date());
					loanDetailList.add(loanDetailInfoEntity);
				}
                
			}
		}
		
		//借款信息逻辑与数据格式验证
		String vaildMsg = BizUtil.vaildLoanInfo(loanList);
		if(!StringUtils.isEmpty(vaildMsg)){
			throw new SLException(vaildMsg);
		}
		//获取所有第三方机构信息
		List<Map<String, Object>> debtSourceCodeMapList = loanInfoRepositoryCustom.queryAllDebtSourceCode();
		//组装第三方机构信息到Map对象
		Map<String, String> debtContainerMap = BizUtil.listMapToStringMap(debtSourceCodeMapList);
		for (LoanInfoEntity loan : loanList) {
			StringBuffer debtSourceVsg=new StringBuffer();
			StringBuffer keySb=new StringBuffer();
			String debtSourceCode = CommonUtils.emptyToString(loan.getDebtSourceCode()).trim();
			String debtSourceName = CommonUtils.emptyToString(loan.getDebtSourceName()).trim();
			keySb.append(debtSourceCode).append("=").append(debtSourceName);
			if(!debtContainerMap.containsKey(keySb.toString())){
				debtSourceVsg.append("机构编号:").append(debtSourceCode).append(",机构名称:").append(debtSourceName).append(",信息不一致或机构不存在,请检查!!!");
				throw new SLException(debtSourceVsg.toString());
			}
			
		}
		
		//获取所有资质类型
		Map<String, String> assetCodeMap = loanInfoRepositoryCustom.queryAllAssetType();
		for (LoanInfoEntity loan : loanList) {
			StringBuffer assetCodeVsg=new StringBuffer();
			String assetTypeCode = CommonUtils.emptyToString(loan.getAssetTypeCode()).trim();
			if(!assetCodeMap.containsKey(assetTypeCode)){
				assetCodeVsg.append("资产类型:").append(assetTypeCode).append(",不存在,请检查!!!");
				throw new SLException(assetCodeVsg.toString());
			}
		}
		
		//由机构编号分组债权编号
		Map<String, List<String>> debtSourceCodeMap = BizUtil.externalLoanGroup(LoanCustInfoList);
		//验证债权编号是否存在重复
		String validateMessage = loanInfoRepositoryCustom.validateLoanCode(debtSourceCodeMap);
		if(!StringUtils.isEmpty(validateMessage)){
			throw new SLException(validateMessage);
		}
		
		boolean loanCustResult=loanInfoRepositoryCustom.batchInsertLoanCust(loanCustList);
		boolean loanResult=loanInfoRepositoryCustom.batchInsertLoan(loanList);
		boolean loanDetailResult=loanInfoRepositoryCustom.batchInsertLoanDetail(loanDetailList);
		if(!loanResult || !loanCustResult || !loanDetailResult){
			throw new SLException("债权导入失败!!!");
		}
		return new ResultVo(true, "债权导入成功!!!");
	}

	/**
	 * @desc 批量导入还款计划
	 * @param RepaymentPlanInfoEntity 还款计划实体对象
	 * @return ResultVo
	 * **/
	@Transactional(readOnly=false,rollbackFor=SLException.class)
	@Override
	public ResultVo importRepaymentPlan(List<RepaymentPlanInfoEntity> planEntityList) throws SLException {
		Set<String> loanCodeSet = BizUtil.getDistinctLoanCode(planEntityList);
		if((loanCodeSet==null)||(loanCodeSet.isEmpty())){
			throw new SLException("计划数据为空,请检查!!!");
		}
		
		List<Map<String, Object>> queryMapList = loanInfoRepositoryCustom.findByLoanCode(loanCodeSet);
		Map<String, LoanInfoVo> mapLoanVo = BizUtil.mapToLoanVo(queryMapList);
	
		StringBuffer loanCodeSb=new StringBuffer();
		//前端传递的债权编号个数
		int paramLoanCodeNum=loanCodeSet.size();
		//本地搜索的债权编号个数
		int localLoanCodeNum=queryMapList.size();
		//如果参数债权编号在本地数据库中查询不到,说明在债权未导入之前,直接导入还款计划
		if(paramLoanCodeNum>localLoanCodeNum){
			for (String loanCode : loanCodeSet) {
				if(!mapLoanVo.containsKey(loanCode)){
					loanCodeSb.append(loanCode).append(",");
				}
			}
		}
		
		if(!loanCodeSb.toString().equals("")){
			int index=loanCodeSb.toString().length()-1;
			String codeString=loanCodeSb.toString().substring(0, index);
			throw new SLException("债权编号:("+codeString+")未找到对应债权信息,请检查!!!");
		}
		
		String message = loanInfoRepositoryCustom.repaymentPlanFindByLoanCode(loanCodeSet);
		if(!StringUtils.isEmpty(message)){
			throw new SLException(message);
		}
		
	
		//根据债权编号,分组还款计划到MAP
		Map<String, List<RepaymentPlanInfoEntity>> planMap = BizUtil.repaymentPlanToMap(planEntityList);
		
		//验证还款记录与债权信息是否匹配
		String vaildMsg = BizUtil.vaildRepayPlan(mapLoanVo, planMap);
		if(!StringUtils.isEmpty(vaildMsg)){
			throw new SLException(vaildMsg);
		}
		
		//根据债权持有比例,重新计算还款记录相应金额
		BizUtil.computeRepaymentPlanAmountByHoldScale(mapLoanVo, planEntityList);
		
		//根据还款计划计算年化利息与当前价值
		List<LoanDetailInfoEntity> loanDetailEntityList = BizUtil.computeRate(mapLoanVo, planMap);
		
		//汇总导入债权总价值(金额)
		BigDecimal totalAmount=new BigDecimal("0");
		for (LoanDetailInfoEntity loanDetail : loanDetailEntityList) {
			totalAmount=totalAmount.add(loanDetail.getImportPv());
		}
	
		//获取活期宝主账户对象
		AccountInfoEntity accountInfoEntity = accountInfoRepository.findOne(Constant.ACCOUNT_ID_WEALTH_CENTER);
		//AccountInfoEntity newAccountInfoEntity = accountInfoEntity.clone();
		Map<String,Object> paramMap=new HashMap<String, Object>();
		paramMap.put("amount", totalAmount);
		paramMap.put("id", accountInfoEntity.getId());
		//更新居间人账户
		int rows = accountInfoRepositoryCustom.updateAccountById(paramMap);
		if(rows==0){
			throw new SLException("账户更新失败,请重试!");
		}
		
//		if(newAccountInfoEntity.getAccountAvailableAmount().compareTo(totalAmount) < 0){
//			throw new SLException("居间人账户可用余额不够!");
//		}
		
//		newAccountInfoEntity.setAccountTotalAmount(ArithUtil.sub(newAccountInfoEntity.getAccountTotalAmount(), totalAmount));
//		newAccountInfoEntity.setAccountAvailableAmount(ArithUtil.sub(newAccountInfoEntity.getAccountAvailableAmount(), totalAmount));
		
		//记录流水记录
		AccountFlowInfoEntity accountFlowInfoEntity=BizUtil.getAccountFlowEntity(accountInfoEntity, numberService, totalAmount);
		accountFlowInfoEntity=accountFlowInfoRepository.save(accountFlowInfoEntity);
		
//		//记录流水详情记录
//		AccountFlowDetailEntity accountFlowDetailEntity=BizUtil.getAccountFlowDetailEntity(accountFlowInfoEntity.getId(), totalAmount);
//		accountFlowDetailEntity=accountFlowDetailRepository.save(accountFlowDetailEntity);
		
		//反推年化利率
		boolean loanDetail = loanInfoRepositoryCustom.batchUpdateLoanDetail(loanDetailEntityList);
		if(loanDetail==false){
			throw new SLException("年化利率计算失败,请联系管理员");
		}
		
		//批量保存还款计划
		boolean repayPaln = loanInfoRepositoryCustom.batchInsertRepaymentPlan(planEntityList);
		if(repayPaln==false){
			throw new SLException("保存还款计划失败,请联系管理员");
		}
			
		//custAccountService.updateCompanyAccount(newAccountInfoEntity, accountInfoEntity);
		
		return new ResultVo(true, "导入成功");
	}

	@Override
	public Map<String, Object> findLoanList(Map<String, Object> param) throws SLException {
		Map<String, Object> result = new HashMap<String, Object>();
		//param.put("productType", Constant.PRODUCT_TYPE_01);
		Page<Map<String, Object>> page = loanInfoRepositoryCustom.findLoanList(param);
		result.put("iTotalDisplayRecords", page.getTotalElements());
		result.put("data", page.getContent());
		return result;
	}

	@Override
	public Map<String, Object> findLoanDetailInfo(Map<String, Object> param) throws SLException {
		Map<String, Object> result = loanInfoRepositoryCustom.findLoanDeatilById((String)param.get("loanId"));
		result.put("data", loanInfoRepositoryCustom.findRepaymentPlanList((String)param.get("loanId"), (BigDecimal)result.get("holdScale")));
		return result;
	}

	@Override
	public Map<String, Object> findLoanListCount(Map<String, Object> param) {
		return loanInfoRepositoryCustom.findLoanListCount(param);
	}

	@Override
	public Map<String, Object> findDailyValueList(Map<String, Object> param) {
//		Map<String, Object> result = new HashMap<String, Object>();
//		Page<Map<String, Object>> page = custDailyValueHistoryRepositroyCustom.findDailyValueList(param);
//		result.put("iTotalDisplayRecords", page.getTotalElements());
//		result.put("data", page.getContent());
//		return result;
		Map<String, Object> result = new HashMap<String, Object>();
		Page<Map<String, Object>> page = custDailyValueHistoryRepositroyCustom.findDailyLoan(param);
		result.put("iTotalDisplayRecords", page.getTotalElements());
		result.put("data", page.getContent());
		return result;
	}

	@Override
	public Map<String, Object> findDailyLoanList(Map<String, Object> param) {
		Map<String, Object> result = new HashMap<String, Object>();
		Page<Map<String, Object>> page = loanAllotHistoryRepositoryCustom.findDailyLoanList(param);
		result.put("iTotalDisplayRecords", page.getTotalElements());
		result.put("data", page.getContent());
		return result;
	}

	@Transactional(readOnly=false,rollbackFor=SLException.class)
	@Override
	public void saveDailyLoanAndValue(Date now, String productName)
			throws SLException {
		
		// 保存每日债权
		loanAllotHistoryRepositoryCustom.saveDailyLoanList(now, productName);
		
		// 保存每日用户持有价值
		custDailyValueHistoryRepositroyCustom.saveDailyValueList(now, productName);
	}

	@Override
	public ResultVo queryExpLoan() {
		LoanInfoEntity loanInfoEntity = null;
		try {
			loanInfoEntity = loanInfoRepository.findByNewerFlagAndLoanStatus();
		} catch (Exception e) {
			e.printStackTrace();
			return new ResultVo(false, "体验标信息有误");
		}
		return new ResultVo(true, "查询成功", loanInfoEntity);
	}
	
	
	@Override
	public LoanInfoEntity findByLoanCode(String loanCode) {
		LoanInfoEntity loanInfoEntity = null;
		try {
			loanInfoEntity = loanInfoRepository.findByLoanCode(loanCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return loanInfoEntity;
	}


}
