/** 
 * @(#)ParamServiceImpl.java 1.0.0 2015年5月1日 上午10:46:40  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.InterfaceDetailInfoEntity;
import com.slfinance.shanlincaifu.entity.ParamEntity;
import com.slfinance.shanlincaifu.repository.BusinessDeptInfoRepository;
import com.slfinance.shanlincaifu.repository.InterfaceDetailInfoRepository;
import com.slfinance.shanlincaifu.repository.ParamRepository;
import com.slfinance.shanlincaifu.repository.custom.ParamRepositoryCustom;
import com.slfinance.shanlincaifu.service.ParamService;
import com.slfinance.shanlincaifu.utils.ArithUtil;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.ParamsNameConstant;
import com.slfinance.thirdpp.util.ShareConstant;
import com.slfinance.vo.ResultVo;

/**   
 * 参数服务实现类
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年5月1日 上午10:46:40 $ 
 */
@Service("paramService")
public class ParamServiceImpl implements ParamService {
	
	@Autowired
	private ParamRepository paramRepository;
	
	@Autowired
	private ParamRepositoryCustom paramRepositoryCustom;
	
	@Autowired
	private InterfaceDetailInfoRepository interfaceDetailInfoRepository;
	
	@Autowired
	private BusinessDeptInfoRepository businessDeptInfoRepository;
	
	static String TYPE_NAME = "活期宝设置";
	static String SHAN_LIN_TYPE_NAME = "善林财富设置";

	@Cacheable(value="cache1", key="'findInvestValidTime'")
	@Override
	public String findInvestValidTime() {
		ParamEntity paramEntity = paramRepository.findByTypeNameAndParameterName(TYPE_NAME, "投资生效时间");
		if(paramEntity == null)
			return "15:00:00";
		return paramEntity.getValue();
	}

	@Cacheable(value="cache1", key="'findFixLimited'")
	@Override
	public Boolean findFixLimited() {
		ParamEntity paramEntity = paramRepository.findByTypeNameAndParameterName(TYPE_NAME, "可赎回金额是否为固定额度");
		if(paramEntity == null)
			return true;
		return paramEntity.getValue().equals("1");
	}

	@Cacheable(value="cache1", key="'findFixLimitedAmount'")
	@Override
	public BigDecimal findFixLimitedAmount() {
		ParamEntity paramEntity = paramRepository.findByTypeNameAndParameterName(TYPE_NAME, "赎回固定额度");
		if(paramEntity == null)
			return new BigDecimal("0");
		return new BigDecimal(paramEntity.getValue());
	}

	@Cacheable(value="cache1", key="'findFixLimitedScalce'")
	@Override
	public BigDecimal findFixLimitedScalce() {
		ParamEntity paramEntity = paramRepository.findByTypeNameAndParameterName(TYPE_NAME, "非固定额度比例值");
		if(paramEntity == null)
			return new BigDecimal("0.2");
		return new BigDecimal(paramEntity.getValue());
	}

	@Cacheable(value="cache1", key="'findMaxDayWithdrawAmount'")
	@Override
	public BigDecimal findMaxDayWithdrawAmount() {
		ParamEntity paramEntity = paramRepository.findByTypeNameAndParameterName(TYPE_NAME, "每人每天最大可赎回额度");
		if(paramEntity == null)
			return new BigDecimal("50000");
		return new BigDecimal(paramEntity.getValue());
	}

	@Cacheable(value="cache1", key="'findMaxDayWithdrawCount'")
	@Override
	public long findMaxDayWithdrawCount() {
		ParamEntity paramEntity = paramRepository.findByTypeNameAndParameterName(TYPE_NAME, "每人每天最大可赎回次数");
		if(paramEntity == null)
			return 5;
		return Long.parseLong(paramEntity.getValue());
	}

	@Cacheable(value="cache1", key="'findMaxMonthWithdrawCount'")
	@Override
	public long findMaxMonthWithdrawCount() {
		ParamEntity paramEntity = paramRepository.findByTypeNameAndParameterName(TYPE_NAME, "每人每月最大可赎回次数");
		if(paramEntity == null)
			return 100;
		return Long.parseLong(paramEntity.getValue());
	}
	
	@Cacheable(value="cache1", key="'findMaxWithdrawAmount'")
	@Override
	public BigDecimal findMaxWithdrawAmount() {
		ParamEntity paramEntity = paramRepository.findByTypeNameAndParameterName(TYPE_NAME, "个人累积投资金额");
		if(paramEntity == null)
			return new BigDecimal("500000");
		return new BigDecimal(paramEntity.getValue());
	}
	
	/**
	 * 个人累积投资最大限额
	 */
	@Override
	public BigDecimal findMaxInvestAmount(){
//		ParamEntity paramEntity = paramRepository.findByTypeNameAndParameterName(TYPE_NAME, "个人累积投资最大限额");
//		if(paramEntity == null)
//			return new BigDecimal(Long.MAX_VALUE);
//		return new BigDecimal(paramEntity.getValue());
		return new BigDecimal("2000000");
	}
	
	@Cacheable(value="cache1", key="'findActivityRegistAmount'")
	@Override
	public BigDecimal findActivityRegistAmount() {
		List<ParamEntity> list = paramRepository.findByType(ParamsNameConstant.ACTIVITY_REGIST);
		if(list != null && list.size() > 0){
			return new BigDecimal(list.get(0).getValue());
		}
		else
			return new BigDecimal("1000");
	}

	@Override
	public BigDecimal findActivityRecommend(String startLevel, String endLevel) {
		List<ParamEntity> list = paramRepository.findByType(ParamsNameConstant.ACTIVITY_RECOMMEND);
		BigDecimal activityRecommend = new BigDecimal("0");
		for(ParamEntity entity : list){
			if(entity.getParameterName().compareTo(startLevel) >= 0 
					&& entity.getParameterName().compareTo(endLevel) <= 0){
				activityRecommend = ArithUtil.add(activityRecommend, new BigDecimal(entity.getValue()));
			}
		}
		return activityRecommend;
	}

	@Cacheable(value="cache1", key="'findRechargeAuthPayExpenseScale'")
	@Override
	public BigDecimal findRechargeAuthPayExpenseScale() {
		ParamEntity paramEntity = paramRepository.findByTypeNameAndParameterName(SHAN_LIN_TYPE_NAME, "认证充值手续费比例值");
		if(paramEntity == null)
			return new BigDecimal("0.0025");
		return new BigDecimal(paramEntity.getValue());
	}

	@Cacheable(value="cache1", key="'findRechargeAuthPayExpenseMinAmount'")
	@Override
	public BigDecimal findRechargeAuthPayExpenseMinAmount() {
		ParamEntity paramEntity = paramRepository.findByTypeNameAndParameterName(SHAN_LIN_TYPE_NAME, "认证充值手续费最小值");
		if(paramEntity == null)
			return new BigDecimal("1");
		return new BigDecimal(paramEntity.getValue());
	}

	@Cacheable(value="cache1", key="'findRechargeBankPayExpenseScale'")
	@Override
	public BigDecimal findRechargeBankPayExpenseScale() {
		ParamEntity paramEntity = paramRepository.findByTypeNameAndParameterName(SHAN_LIN_TYPE_NAME, "网银充值手续费比例值");
		if(paramEntity == null)
			return new BigDecimal("0.002");
		return new BigDecimal(paramEntity.getValue());
	}

	@Cacheable(value="cache1", key="'findRechargeBankPayExpenseMinAmount'")
	@Override
	public BigDecimal findRechargeBankPayExpenseMinAmount() {
		ParamEntity paramEntity = paramRepository.findByTypeNameAndParameterName(SHAN_LIN_TYPE_NAME, "网银充值手续费最小值");
		if(paramEntity == null)
			return new BigDecimal("0.1");
		return new BigDecimal(paramEntity.getValue());
	}

	@Cacheable(value="cache1", key="'findWithDrawExpenseAmount'")
	@Override
	public BigDecimal findWithDrawExpenseAmount() {
		ParamEntity paramEntity = paramRepository.findByTypeNameAndParameterName(SHAN_LIN_TYPE_NAME, "提现手续费");
		if(paramEntity == null)
			return new BigDecimal("1");
		return new BigDecimal(paramEntity.getValue());
	}
	
	@Cacheable(value="cache1", key="'findRealNameExpenseAmount'")
	@Override
	public BigDecimal findRealNameExpenseAmount() {
		ParamEntity paramEntity = paramRepository.findByTypeNameAndParameterName(SHAN_LIN_TYPE_NAME, "实名认证手续费");
		if(paramEntity == null)
			return new BigDecimal("0.9");
		return new BigDecimal(paramEntity.getValue());
	}

	@Cacheable(value="cache1", key="'findRealNameExpenseAmount'")
	@Override
	public BigDecimal findRealNameExpenseAmount(String thirdPartyName) {
		ParamEntity paramEntity = null;
		if(ShareConstant.THIRD_PARTY_TYPE_LLPay.equals(thirdPartyName)) {
			paramEntity = paramRepository.findByTypeNameAndParameterName(SHAN_LIN_TYPE_NAME, "实名认证手续费");
			if(paramEntity == null)
				return new BigDecimal("3");
		}
		else {
			paramEntity = paramRepository.findByTypeNameAndParameterName(SHAN_LIN_TYPE_NAME, "国政通实名认证手续费");
			if(paramEntity == null)
				return new BigDecimal("1.5");
		}

		return new BigDecimal(paramEntity.getValue());
	}

	@Cacheable(value="cache1", key="'findAuthRechargeDailyAllRequest'")
	@Override
	public BigDecimal findAuthRechargeDailyAllRequest() {
		ParamEntity paramEntity = paramRepository.findByTypeNameAndParameterName(SHAN_LIN_TYPE_NAME, "认证充值每日最大访问次数");
		if(paramEntity == null)
			return new BigDecimal("50");
		return new BigDecimal(paramEntity.getValue());
	}

	@Cacheable(value="cache1", key="'findBankRechargeDailyAllRequest'")
	@Override
	public BigDecimal findBankRechargeDailyAllRequest() {
		ParamEntity paramEntity = paramRepository.findByTypeNameAndParameterName(SHAN_LIN_TYPE_NAME, "网银充值每日最大访问次数");
		if(paramEntity == null)
			return new BigDecimal("50");
		return new BigDecimal(paramEntity.getValue());
	}

	@Cacheable(value="cache1", key="'findAuthRechargeDailySuccessRequest'")
	@Override
	public BigDecimal findAuthRechargeDailySuccessRequest() {
		ParamEntity paramEntity = paramRepository.findByTypeNameAndParameterName(SHAN_LIN_TYPE_NAME, "认证充值每日可成功充值最大次数");
		if(paramEntity == null)
			return new BigDecimal("15");
		return new BigDecimal(paramEntity.getValue());
	}

	@Cacheable(value="cache1", key="'findBankRechargeDailySuccessRequest'")
	@Override
	public BigDecimal findBankRechargeDailySuccessRequest() {
		ParamEntity paramEntity = paramRepository.findByTypeNameAndParameterName(SHAN_LIN_TYPE_NAME, "网银充值每日可成功充值最大次数");
		if(paramEntity == null)
			return new BigDecimal("20");
		return new BigDecimal(paramEntity.getValue());
	}

	@Cacheable(value="cache1", key="'findAuthRechargeSingleLimited'")
	@Override
	public BigDecimal findAuthRechargeSingleLimited() {
		ParamEntity paramEntity = paramRepository.findByTypeNameAndParameterName(SHAN_LIN_TYPE_NAME, "认证充值单笔限额");
		if(paramEntity == null)
			return new BigDecimal("50000");
		return new BigDecimal(paramEntity.getValue());
	}

	@Cacheable(value="cache1", key="'findAuthRechargeDailyLimited'")
	@Override
	public BigDecimal findAuthRechargeDailyLimited() {
		ParamEntity paramEntity = paramRepository.findByTypeNameAndParameterName(SHAN_LIN_TYPE_NAME, "认证充值单日限额");
		if(paramEntity == null)
			return new BigDecimal("50000");
		return new BigDecimal(paramEntity.getValue());
	}

	@Cacheable(value="cache1", key="'findAuthRechargeMonthlyLimited'")
	@Override
	public BigDecimal findAuthRechargeMonthlyLimited() {
		ParamEntity paramEntity = paramRepository.findByTypeNameAndParameterName(SHAN_LIN_TYPE_NAME, "认证充值单月限额");
		if(paramEntity == null)
			return new BigDecimal("200000");
		return new BigDecimal(paramEntity.getValue());
	}

	@Cacheable(value="cache1", key="'findMinRechargeAmountLimited'")
	@Override
	public BigDecimal findMinRechargeAmountLimited() {
		ParamEntity paramEntity = paramRepository.findByTypeNameAndParameterName(SHAN_LIN_TYPE_NAME, "最小充值金额");
		if(paramEntity == null)
			return new BigDecimal("1");
		return new BigDecimal(paramEntity.getValue());
	}

	@Override
	public BigDecimal findActivityRecommend() {
//		ParamEntity paramEntity = paramRepository.findByTypeNameAndParameterName("推荐有奖", "推荐送体验金");
//		if(paramEntity == null)
//			return new BigDecimal("10000");
//		return new BigDecimal(paramEntity.getValue());
		return new BigDecimal("10000");
	}

	@Override
	public int findExpireDays() {
		// 2015-08-01之前奖励15天，之后奖励10天
		if(DateUtils.formatDate(new Date(), "yyyyMMdd").compareTo("20150801") < 0) {
			return 15;
		}
		else 
			return 10;
	}

	@Cacheable(value="cache1", key="'findBuyBackDay'")
	@Override
	public int findBuyBackDay() {
		ParamEntity paramEntity = paramRepository.findByTypeNameAndParameterName(SHAN_LIN_TYPE_NAME, "公司回购天数");
		if(paramEntity == null)
			return new Integer("0");
		return new Integer(paramEntity.getValue());
	}

	@Cacheable(value="cache1", key="'findByParamType_' + #params['type']")
	@Override
	public Map<String, Object> findByParamType(Map<String, Object> params)
			throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<ParamEntity> list = paramRepository.findByTypeAndStatusOrderBySortAsc((String)params.get("type"), "1");		
		resultMap.put("paramList", list);
		return resultMap;
	}

	@Cacheable(value="cache1", key="'findByParentId_' + #params['parentId']")
	@Override
	public Map<String, Object> findByParentId(Map<String, Object> params) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<ParamEntity> list = paramRepository.findByParentIdAndStatusOrderBySortAsc((String)params.get("parentId"), "1");
		resultMap.put("paramList", list);
		return resultMap;
	}

	@Override
	public Map<String, Object> findLowerParamById(Map<String, Object> params)
			throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<ParamEntity> list = paramRepository.findLowerParamById((String)params.get("id"));
		resultMap.put("paramList", list);
		return resultMap;
	}

	@Override
	public Map<String, Object> findLowerParamByParentId(Map<String, Object> params)
			throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<ParamEntity> list = paramRepository.findLowerParamByParentId((String)params.get("id"));
		resultMap.put("paramList", list);
		return resultMap;
	}

	@Override
	public Map<String, Object> findById(Map<String, Object> params) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		ParamEntity paramEntity = paramRepository.findOne((String)params.get("id"));
		resultMap.put("param", paramEntity);
		return resultMap;
	}
	
	@Override
	public Map<String, Object> findByType(Map<String, Object> params)
			throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<ParamEntity> list = paramRepository.findByType((String)params.get("type"));
		resultMap.put("paramList", list);
		return resultMap;
	}

	@Override
	public Map<String, Object> findOneBySearch(Map<String, Object> params)
			throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String type = (String)params.get("type");
		String value = (String)params.get("value");
		ParamEntity paramEntity = paramRepository.findByTypeAndValue(type, value);
		resultMap.put("param", paramEntity);
		return resultMap;
	}

	@Override
	public Map<String, Object> findByCityIdAndBankName(
			Map<String, Object> map) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, Object>> list = paramRepositoryCustom.findByCityIdAndBankName(map);
		resultMap.put("paramList", list);
		return resultMap;
	}

	@Cacheable(value="cache1", key="'findAllChannel'")
	@Override
	public Map<String, Object> findAllChannel(Map<String, Object> map)
			throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<InterfaceDetailInfoEntity> list = interfaceDetailInfoRepository.findByInterfaceType(Constant.OPERATION_TYPE_16);
		resultMap.put("paramList", list);
		return resultMap;
	}

	@Cacheable(value="cache1", key="'findByChannelNo_' + #channelNo", unless = "#result == ''")
	@Override
	public String findByChannelNo(String channelNo){
		String result = channelNo;
		List<InterfaceDetailInfoEntity> list = interfaceDetailInfoRepository.findByMerchantCode(channelNo);
		if(list != null && list.size() > 0) {
			result = list.get(0).getThirdPartyType();
		}
		return result;
	}

	@Cacheable(value="cache1", key="'findInvestContent'")
	@Override
	public ResultVo findInvestContent(Map<String, Object> map)
			throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		ParamEntity paramEntity = paramRepository.findByTypeNameAndParameterName(SHAN_LIN_TYPE_NAME, "邀请好友内容");
		if(paramEntity == null)
		{
			resultMap.put("content", "善林财富年末大放血，一天赚够一整年！投多少送多少，预期年化收益率高至13%！戳我领钱");
		}
		else {
			resultMap.put("content", paramEntity.getValue());
		}
		return new ResultVo(true, "邀请内容", resultMap);
	}

	@Override
	public Map<String, Object> findCurrentIOSVersion(Map<String, Object> map) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		ParamEntity paramEntity = paramRepository.findByTypeNameAndParameterName(SHAN_LIN_TYPE_NAME, "iOS当前版本号");
		if(paramEntity == null)
		{
			resultMap.put("appVersion", "2.6");
		}
		else {
			resultMap.put("appVersion", paramEntity.getValue());
		}
		return resultMap;
	}

	@Override
	public BigDecimal findPenaltyRate() {
		
		return new BigDecimal("0.01");
	}

	@Override
	public BigDecimal findOverdueRate() {
		
		return new BigDecimal("0.0005");
	}

	@Override
	public BigDecimal findRiskRate() {
		
		return new BigDecimal("0");
	}

	@Override
	public Map<String, Object> findWithDrawPayer(Map<String, Object> map) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<ParamEntity> list = paramRepository.findByType((String)map.get("type"));
		if(list != null && list.size() > 0){
			resultMap.put("payerBankNo", list.get(0).getParameterName());
			resultMap.put("payerCustName", list.get(0).getValue());
		} else {
			resultMap.put("payerBankNo", "6230585000000033688");
			resultMap.put("payerCustName", "周伯云");
		}
		return resultMap;
	}

	@Override
	public BigDecimal findAdvanceAtoneWealthRate() {
		// update by wangjf 2016-5-16 费率有0.03改为0.05
		return new BigDecimal("0.05");
	}

	@Cacheable(value="cache1", key="'findAllBankList_' + #params['type']")
	@Override
	public Map<String, Object> findAllBankList(Map<String, Object> params)
			throws Exception {	
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<ParamEntity> list = paramRepository.findByTypeAndStatusOrderByMemoAscParameterNameAsc((String)params.get("type"), "1");		
		resultMap.put("paramList", list);
		return resultMap;
	}

	@Override
	public Long findMaxSendSmsTimes() {
		
		return 10L;
	}

	/**
	 * 划扣公司
	 * @return
	 */
	@Override
	public ResultVo findThirdPayList(Map<String, Object> params)
			throws Exception {
		Map<String, Object> resultMap = Maps.newHashMap();
		List<Map<String, Object>> data = paramRepositoryCustom.findThirdPayList(params);
		resultMap.put("thirdPayList", data);
		return new ResultVo(true, "划扣公司查询成功", resultMap);
	}

	@Cacheable(value="cache1", key="'findMonitorEmail'")
	@Override
	public String findMonitorEmail(String type) throws Exception {
		ParamEntity paramEntity = paramRepository.findByTypeNameAndParameterName(SHAN_LIN_TYPE_NAME, type);
		if(paramEntity == null)
			return "";
		return paramEntity.getValue();
	}

	@Cacheable(value="cache1", key="'findCommissionRate'")
	@Override
	public BigDecimal findCommissionRate(){
		ParamEntity paramEntity = paramRepository.findByTypeNameAndParameterName(SHAN_LIN_TYPE_NAME, "佣金利率");
		if(paramEntity == null)
			return new BigDecimal("0.018");
		return new BigDecimal(paramEntity.getValue());
	}

	@Cacheable(value="cache1", key="'findCommissionAwardRate'")
	@Override
	public BigDecimal findCommissionAwardRate(){
		ParamEntity paramEntity = paramRepository.findByTypeNameAndParameterName(SHAN_LIN_TYPE_NAME, "佣金补贴利率");
		if(paramEntity == null)
			return new BigDecimal("0.12");
		return new BigDecimal(paramEntity.getValue());
	}

	@Cacheable(value="cache1", key="'findLoanManageRate'")
	@Override
	public BigDecimal findLoanManageRate() {
		ParamEntity paramEntity = paramRepository.findByTypeNameAndParameterName(SHAN_LIN_TYPE_NAME, "优选项目平台服务费率");
		if(paramEntity == null)
			return new BigDecimal("0.03");
		return new BigDecimal(paramEntity.getValue());
	}

	@Cacheable(value="cache1", key="'findWapUrl'")
	@Override
	public String findWapUrl() {
		ParamEntity paramEntity = paramRepository.findByTypeNameAndParameterName(SHAN_LIN_TYPE_NAME, "WAP地址");
		if(paramEntity == null)
			return "http://m.shanlincaifu.com";
		return paramEntity.getValue();
	}

	@Cacheable(value="cache1", key="'findCompanyRechargeBankPayExpense'")
	@Override
	public BigDecimal findCompanyRechargeBankPayExpense() {
		ParamEntity paramEntity = paramRepository.findByTypeNameAndParameterName(SHAN_LIN_TYPE_NAME, "公司网银充值手续费");
		if(paramEntity == null)
			return new BigDecimal("20");
		return new BigDecimal(paramEntity.getValue());
	}

	@Cacheable(value="cache1", key="'findTransferRate'")
	@Override
	public BigDecimal findTransferRate() {
		ParamEntity paramEntity = paramRepository.findByTypeNameAndParameterName(SHAN_LIN_TYPE_NAME, "债权转让手续费率");
		if(paramEntity == null)
			return new BigDecimal("0.04");
		return new BigDecimal(paramEntity.getValue());
	}

	@Cacheable(value="cache1", key="'findTransferDay'")
	@Override
	public Integer findTransferDay() {
		ParamEntity paramEntity = paramRepository.findByTypeNameAndParameterName(SHAN_LIN_TYPE_NAME, "债权转让申请天数");
		if(paramEntity == null)
			return 3;
		return new Integer(paramEntity.getValue());
	}

	@Cacheable(value="cache1", key="'findTransferProtocalType'")
	@Override
	public String findTransferProtocalType() {
		ParamEntity paramEntity = paramRepository.findByTypeNameAndParameterName(SHAN_LIN_TYPE_NAME, "债权转让协议模板");
		if(paramEntity == null)
			return "z_20170104";
		return paramEntity.getValue();
	}

	@Cacheable(value="cache1", key="'findTransferNeedHoldDay'")
	@Override
	public Integer findTransferNeedHoldDay() {
		ParamEntity paramEntity = paramRepository.findByTypeNameAndParameterName(SHAN_LIN_TYPE_NAME, "债权转让持有天数");
		if(paramEntity == null)
			return 30;
		return new Integer(paramEntity.getValue());
	}

	@Cacheable(value="cache1", key="'findTransferFromEndDay'")
	@Override
	public Integer findTransferFromEndDay() {
		ParamEntity paramEntity = paramRepository.findByTypeNameAndParameterName(SHAN_LIN_TYPE_NAME, "债权转让截止天数");
		if(paramEntity == null)
			return 30;
		return new Integer(paramEntity.getValue());
	}

	@Cacheable(value="cache1", key="'findLoanProtocalType'")
	@Override
	public String findLoanProtocalType() {
		ParamEntity paramEntity = paramRepository.findByTypeNameAndParameterName(SHAN_LIN_TYPE_NAME, "信贷借款协议模板");
		if(paramEntity == null)
			return "g_20161220";
		return paramEntity.getValue();
	}

	@Override
	public List<String> findExcludeCustIds() {
		ParamEntity paramEntity = paramRepository.findByTypeNameAndParameterName(SHAN_LIN_TYPE_NAME, "排除绑卡送iphone不符合条件的用户");
		if(paramEntity == null)
			return Lists.newArrayList();
		String value = paramEntity.getValue();
		return Lists.newArrayList(value.split(","));
	}

	@Cacheable(value="slcf_business",key="'queryProvince'", cacheManager="redis")
	@Override
	public Map<String, Object> queryProvince() throws SLException {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String,Object>> findProvince = businessDeptInfoRepository.queryProvince();
		resultMap.put("paramList", findProvince);
		return resultMap;
	}

	@Cacheable(value="slcf_business",key="'queryCity' + #param['provinceName']", cacheManager="redis")
	@Override
	public Map<String, Object> queryCity(Map<String, Object> param)
			throws SLException {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String,Object>> byProvinceName = businessDeptInfoRepository.queryCityNameByProvinceName(param.get("provinceName").toString());
		resultMap.put("paramList", byProvinceName);
		return resultMap;
	}

	@Cacheable(value="slcf_business",key="'queryDeptName' + #param['cityName']", cacheManager="redis")
	@Override
	public Map<String, Object> queryDeptName(Map<String, Object> param)
			throws SLException {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String,Object>> byDebtName = businessDeptInfoRepository.queryDebtNameByCityNameProvinceName(param.get("cityName").toString());
		resultMap.put("paramList", byDebtName);
		return resultMap;
	}
	
	@Cacheable(value="cache1", key="'findTransferRefromOnlineTime'")
	@Override
	public String findTransferRefromOnlineTimeByParameterName(String onlineTime) {
		ParamEntity paramEntity = paramRepository.findByTypeNameAndParameterName(SHAN_LIN_TYPE_NAME, onlineTime);
		if(paramEntity == null)
			return "2017-06-07 11:15:00";
		return paramEntity.getValue();
	}

	@Override
	public int findLoanGroupLockMinutes() {
		ParamEntity paramEntity = paramRepository.findByTypeNameAndParameterName(SHAN_LIN_TYPE_NAME, "一键出借锁定时间(分钟)");
		if(paramEntity == null)
			return 1; // 默认锁定30分钟
		return Integer.parseInt(paramEntity.getValue());
	}
}
