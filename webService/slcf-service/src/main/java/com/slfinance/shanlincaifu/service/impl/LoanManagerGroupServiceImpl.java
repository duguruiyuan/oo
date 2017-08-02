package com.slfinance.shanlincaifu.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AccountFlowInfoEntity;
import com.slfinance.shanlincaifu.entity.AccountInfoEntity;
import com.slfinance.shanlincaifu.entity.LoanInfoEntity;
import com.slfinance.shanlincaifu.entity.LoanReserveEntity;
import com.slfinance.shanlincaifu.entity.LogInfoEntity;
import com.slfinance.shanlincaifu.entity.ProjectInvestInfoEntity;
import com.slfinance.shanlincaifu.repository.AccountInfoRepository;
import com.slfinance.shanlincaifu.repository.CustInfoRepository;
import com.slfinance.shanlincaifu.repository.LoanInfoRepository;
import com.slfinance.shanlincaifu.repository.LoanReserveRepository;
import com.slfinance.shanlincaifu.repository.LogInfoEntityRepository;
import com.slfinance.shanlincaifu.repository.ProjectInvestInfoRepository;
import com.slfinance.shanlincaifu.repository.custom.LoanManagerGroupRepositoryCustom;
import com.slfinance.shanlincaifu.service.CustAccountService;
import com.slfinance.shanlincaifu.service.FlowNumberService;
import com.slfinance.shanlincaifu.service.GroupService;
import com.slfinance.shanlincaifu.service.LoanManagerGroupService;
import com.slfinance.shanlincaifu.service.LoanManagerService;
import com.slfinance.shanlincaifu.utils.ArithUtil;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.SubjectConstant;
import com.slfinance.shanlincaifu.utils.UserUtils;
import com.slfinance.vo.ResultVo;
//import lombok.extern.slf4j.Slf4j;

//@Slf4j
@Service("loanManagerGroupService")
public class LoanManagerGroupServiceImpl implements LoanManagerGroupService {

	@Autowired
	private LoanManagerGroupRepositoryCustom loanManagerGroupRepositoryCustom;
	
	@Autowired
	CustInfoRepository custInfoRepository;
	
	@Autowired
	ProjectInvestInfoRepository projectInvestInfoRepository;
	
	@Autowired
	FlowNumberService numberService;
	
	@Autowired
	LoanInfoRepository loanInfoRepository;
	
	@Autowired
	LoanManagerService loanManagerService;

	@Autowired
	InnerClass innerClass;
	
	@Autowired
	GroupService groupService;
	
	@Autowired
	private RedisTemplate<String, Object> listRedisTemplate;
	
	public ResultVo buyLoanForGroup(Map<String, Object> params) throws SLException {
		return groupService.pushLoanGroup(params);
	}
	
	@Service
	public static class InnerClass {
		
		@Autowired
		AccountInfoRepository accountInfoRepository;
		
		@Autowired
		CustAccountService custAccountService;
		
		@Autowired
		LoanInfoRepository loanInfoRepository;
		
		@Autowired
		ProjectInvestInfoRepository projectInvestInfoRepository;
		
		@Autowired
		LoanReserveRepository loanReserveRepository;
		
		@Autowired
		FlowNumberService numberService;
		
		@Autowired
		LogInfoEntityRepository logInfoEntityRepository;

		@Transactional(readOnly=false, rollbackFor=SLException.class)
		public ResultVo freezeAmountAndLoans(String custId, BigDecimal tradeAmount, String reqeustNo, List<String> loanIds, String tradeType) throws SLException {
			AccountInfoEntity custAccount = accountInfoRepository.findByCustId(custId);
			if(custAccount == null){
				return new ResultVo(false, "账户异常");
			}
			
			LoanReserveEntity loanReserve = new LoanReserveEntity();
			loanReserve.setBasicModelProperty(custId, true);
			loanReserve.setCustId(custId);
			loanReserve.setReserveAmount(tradeAmount);
			loanReserve.setAlreadyInvestAmount(BigDecimal.ZERO);
			loanReserve.setRemainderAmount(tradeAmount);
			loanReserve.setReserveStatus(Constant.LOAN_MANAGER_GROUP_02);
			loanReserve.setMemo("一键预约");
			loanReserve = loanReserveRepository.save(loanReserve);
			
			// 金额冻结
			custAccountService.updateAccount(custAccount, null, null,
					null, "7", tradeType, 
					reqeustNo, tradeAmount, tradeType, 
					Constant.TABLE_BAO_T_LOAN_RESERVE, loanReserve.getId(),
					tradeType.concat("资金冻结"), custId);
			
			List<LoanInfoEntity> loanInfos =  (List<LoanInfoEntity>) loanInfoRepository.findAll(loanIds);
			for (LoanInfoEntity loanInfo : loanInfos) {
				loanInfo.setBasicModelProperty(custId, false);
				loanInfo.setLoanStatus(Constant.LOAN_MANAGER_GROUP_03);
				
				ProjectInvestInfoEntity projectInvestInfo = projectInvestInfoRepository.findByLoanId(loanInfo.getId());
				projectInvestInfo.setBasicModelProperty(custId, false);// 防止项目正在被普通人购买，因为购买时，不会更新loanInfo表
			}
			Map<String, Object> data = Maps.newHashMap();
			data.put("loanReserveId", loanReserve.getId());
			return new ResultVo(true, "冻结成功", data);
		}
		
		@Transactional(readOnly=false, rollbackFor=SLException.class)
		public ResultVo unFreezeLoanByLoanId(String custId, String loanId) {
			LoanInfoEntity loanInfo = loanInfoRepository.findOne(loanId);
			if(Constant.LOAN_MANAGER_GROUP_03.equals(loanInfo.getLoanStatus())) {
				loanInfo.setLoanStatus(Constant.LOAN_STATUS_05);
				loanInfo.setBasicModelProperty(custId, false);
			}
			return new ResultVo(true, "解冻项目");
		}

		@Transactional(readOnly=false, rollbackFor=SLException.class)
		public ResultVo unFreezeAmountAndLoans(String custId, BigDecimal tradeAmount, String oldTradeNo, int loanTerm) throws SLException{
			if(tradeAmount.compareTo(BigDecimal.ZERO) > 0){
				String reqeustNo = numberService.generateTradeBatchNumber();
				AccountInfoEntity acc = accountInfoRepository.findByCustId(custId);
				
				String tradeType = SubjectConstant.TRADE_FLOW_TYPE_REMIAN_GROUP_UNFREEZE; //出借优选项目失败
//				if(loanTerm%30 == 0){
//					tradeType = String.format(tradeType, (loanTerm/30)+ "个月");
//				}else {
//					tradeType = String.format(tradeType, loanTerm+ "天");
//				}
				
				// 金额冻结
				List<AccountFlowInfoEntity> accFlowList = custAccountService.updateAccount(acc, null, null,
						null, "8", tradeType, 
						reqeustNo, tradeAmount, tradeType, 
						"", "",
						tradeType.concat("资金解冻"), custId);
				accFlowList.get(0).setOldTradeNo(oldTradeNo);
			}
			return new ResultVo(true, "解冻成功");
		}
		
		@Transactional(readOnly=false, rollbackFor=SLException.class)
		public ResultVo saveLogInfo(String custId, String relateType, String relatePrimary, String logType, String memo, String ip){
			LogInfoEntity logInfo = new LogInfoEntity();
			logInfo.setBasicModelProperty(custId, true);
			logInfo.setRelateType(relateType);
			logInfo.setRelatePrimary(relatePrimary);
			logInfo.setLogType(logType);
			logInfo.setOperDesc(memo);
			logInfo.setOperPerson(custId);
			logInfo.setMemo(memo);
			logInfo.setOperIpaddress(ip);
			
			logInfoEntityRepository.save(logInfo);
			return new ResultVo(true);
		}
	}
	
	/**
	 * 一键出借交易详情页
	 *
	 * @author  guoyk
	 * @date    2017-07-21 10:38:28
	 * @return
	 *      <tt>totalcount        :String:标的总个数</tt><br>
     *      <tt>totalAmount       :String:标的总金额</tt><br>
     *      <tt>loanTermMapList   :String:List<Map<String,Object>>:期限集合</tt><br>
     *      <tt>Map<String,Object>:String:</tt><br>
     *      <tt>loanTerm          :String:期限</tt><br>
     *      <tt>loanUnit          :String:期限单位</tt><br>
     *      <tt>minYearRate       :String:最小年化利率</tt><br>
     *      <tt>maxYearRate       :String:最大年化利率</tt><br>
     *      <tt>minInvestAmount   :String:起投金额</tt><br>
	 * @throws SLException
	 */
	@Override
	public ResultVo queryTradeInfoByOneStepInvest(Map<String, Object> params)
			throws SLException {
		//查询可购买的期限种类
		List<Map<String,Object>> loanTermMapList = loanManagerGroupRepositoryCustom.queryTradeInfoByOneStepInvest(params);
		Map<String, Object> result = Maps.newHashMap();
		//如果没有符合的标的
		if (loanTermMapList.size()==0 || loanTermMapList==null) {
			return new ResultVo(false, "没有可购买的标的！", result);
		}
		//根据一键出借参数 查询可购买的标的个数（初始页面默认选择：最小期限，不选转让类型，不选还款方式）
		BigDecimal loanTerm = (BigDecimal)loanTermMapList.get(0).get("loanTerm");
		String loanUnit = (String)loanTermMapList.get(0).get("loanUnit");
		params.put("loanTerm", loanTerm);
		params.put("loanUnit", loanUnit);
		params.put("transferType", "");
		params.put("repaymentType", "");
		//根据默认的条件 ，查询标的的可购买金额，个数。利率
		Map<String, Object> resultMap = loanManagerGroupRepositoryCustom.queryTradeInfoByLoanTermAndLoanUnit(params);
		result.put("loanTermMapList", loanTermMapList);
//		result.put("totalcount", resultMap.get("totalcount"));
//		result.put("totalAmount", resultMap.get("remainAmount"));
//		result.put("minYearRate", resultMap.get("minYearRate"));
//		result.put("maxYearRate", resultMap.get("maxYearRate"));
//		result.put("minInvestAmount", 100);
		result.put("totalcount", 0);
		result.put("totalAmount", 0);
		result.put("minYearRate", 0);
		result.put("maxYearRate", 0);
		result.put("minInvestAmount", 100);
		return new ResultVo(true, "一键出借交易详情页查询成功", result);
	}
	
	/**
	 * 一键出借交易详情页-查询标的总金额和数量
	 *
	 * @author  guoyk
	 * @date    2017-07-21 10:38:28
	 * @param params
     *      <tt>loanTerm     :String:期限</tt><br>
     *      <tt>loanUnit     :String:期限单位</tt><br>
     *      <tt>transferType :String:转让类型</tt><br>
     *      <tt>repaymentType:String:还款类型</tt><br>
	 * @return
	 *      <tt>totalAmount		  :String:标的总金额</tt><br>
     *      <tt>totalcount 	      :String:标的总个数</tt><br>
	 *      <tt>minYearRate       :String:最小年化利率</tt><br>
     *      <tt>maxYearRate       :String:最大年化利率</tt><br>
     *      <tt>minInvestAmount   :String:起投金额</tt><br>
	 * @throws SLException
	 */
	public ResultVo queryTotalCountAndTotalAmountByOneStepInvest(Map<String, Object> params) throws SLException {
		
		String transferTypeCode = params.get("transferType").toString();
		String repaymentTypeCode = params.get("repaymentType").toString();
		
		String transferType = UserUtils.convertTransferType2Word(transferTypeCode);
		params.put("transferType", transferType);
		String repaymentType = UserUtils.convertRepaymentType2Word(repaymentTypeCode);
		params.put("repaymentType", repaymentType);
		
		Map<String, Object> resultMap = loanManagerGroupRepositoryCustom.queryTradeInfoByLoanTermAndLoanUnit(params);
		BigDecimal remainAmount = new BigDecimal(resultMap.get("remainAmount").toString());
		
		String amountKey = String.format("slcf:yjcj:%s:%s:%s:%s", params.get("loanTerm").toString(), "月".equals(params.get("loanUnit").toString()) ? "MONTH" : "DAY", transferTypeCode, repaymentTypeCode);
		Object dwaitingAmount = listRedisTemplate.opsForValue().get(amountKey);
		BigDecimal waitingAmount = BigDecimal.ZERO;
		if(dwaitingAmount != null) {
			waitingAmount = new BigDecimal(dwaitingAmount.toString());
		}
		if(waitingAmount.compareTo(BigDecimal.ZERO) < 0) {
			waitingAmount = BigDecimal.ZERO;
		}
		remainAmount = ArithUtil.sub(remainAmount, waitingAmount);
		if(remainAmount.compareTo(BigDecimal.ZERO) < 0) {
			remainAmount = BigDecimal.ZERO;
		}
		
		Map<String, Object> result = Maps.newHashMap();
		result.put("totalcount", resultMap.get("totalcount"));
		result.put("totalAmount", remainAmount);
		result.put("minYearRate", resultMap.get("minYearRate"));
		result.put("maxYearRate", resultMap.get("maxYearRate"));
		result.put("minInvestAmount", 100);
		return new ResultVo(true, "一键出借交易详情页标的信息查询成功", result);
	}

	@Override
	public ResultVo queryMyDisperseListByGroup(Map<String, Object> params) throws SLException {
		Map<String, Object> result = loanManagerGroupRepositoryCustom.queryMyDisperseListByGroup(params);
		return new ResultVo(true, "优选项目-我的出借-查询成功！", result);
	}

	@Override
	public ResultVo queryMyDisperseListDetailByGroup(Map<String, Object> params)
			throws SLException {
		Map<String, Object> listDetailByGroup = loanManagerGroupRepositoryCustom.queryMyDisperseListDetailByGroup(params);
		return new ResultVo(true,"优选项目-我的出借-详情列表-查询成功！",listDetailByGroup);
	}

	@Override
	public ResultVo queryLoanTermAndLoanUnit(Map<String, Object> params)
			throws SLException {
		// 查询可购买的期限种类
		List<Map<String, Object>> loanTermMapList = loanManagerGroupRepositoryCustom.queryTradeInfoByOneStepInvest(params);
		Map<String, Object> result = Maps.newHashMap();
		result.put("data", loanTermMapList);
		return new ResultVo(true, "查询成功", result);
	}
}
