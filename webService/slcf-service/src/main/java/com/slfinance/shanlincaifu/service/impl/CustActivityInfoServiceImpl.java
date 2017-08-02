/**
 * @(#)CustActivityServiceImpl.java 1.0.0 2015年4月23日 下午2:55:28  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.*;
import com.slfinance.shanlincaifu.repository.*;
import com.slfinance.shanlincaifu.repository.custom.CustActivityInfoRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.InvestInfoRepositoryCustom;
import com.slfinance.shanlincaifu.service.*;
import com.slfinance.shanlincaifu.utils.*;
import com.slfinance.vo.ResultVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

/**
 * 善林财富活动服务实现
 *
 * @author  caoyi
 * @version $Revision:1.0.0, $Date: 2015年5月19日 下午15:02:28 $
 */
@Slf4j
@Service("custActivityInfoService")
@Transactional(readOnly = true)
public class CustActivityInfoServiceImpl implements CustActivityInfoService{

	@Autowired
	private CustActivityInfoRepositoryCustom custActivityInfoRepositoryCustom;

	@Autowired
	private ActivityInfoRepository activityInfoRepository;

	@Autowired
	private CustActivityInfoRepository custActivityInfoRepository;

	@Autowired
	private AccountInfoRepository accountInfoRepository;

	@Autowired
	private FlowNumberService numberService;

	@Autowired
	private AccountFlowService accountFlowService;

	@Autowired
	private ParamService paramService;

	@Autowired
	private AccountFlowInfoRepository accountFlowInfoRepository;

	@Autowired
	private LogInfoEntityRepository logInfoEntityRepository;

	@Autowired
	private InvestInfoRepository investInfoRepository;

	@Autowired
	private ExpandInfoRepository expandInfoRepository;

	@Autowired
	private InterfaceDetailInfoRepository interfaceDetailInfoRepository;

	@Autowired
	private FlowNumberService flowNumberService;

	@Autowired
	private SMSService smsService;

	@Autowired
	CustInfoRepository custInfoRepository;

	@Autowired
	CustAccountService custAccountService;

	@Autowired
	private CustRecommendInfoRepository custRecommendInfoRepository;

	@Autowired
	private CustApplyInfoRepository custApplyInfoRepository;

	@Autowired
	private LoanInfoRepository loanInfoRepository;

	@Autowired
	InvestDetailInfoRepository investDetailInfoRepository;

	@Autowired
	DeviceService deviceService;

	@Autowired
	ProjectInvestInfoRepository projectInvestInfoRepository;

	@Autowired
    private InvestInfoRepositoryCustom investInfoRepositoryCustom;

	/**
	 * 获取我的推荐记录
	 *
	 * @author caoyi
	 * @date 2015年05月19日 上午11:49:25
	 */
	@Override
	public Map<String, Object> findCustRecommendList(Map<String, Object> param) throws SLException {
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> paramValue = new HashMap<String, Object>();
		String  activityId=(String)param.get("activityId");
		BigDecimal myAward=BigDecimal.ZERO;
		BigDecimal activityAwardFor13 = BigDecimal.ZERO;
		BigDecimal activityAwardFor17 =BigDecimal.ZERO;
		List<Map<String, Object>> amountFor17=null;
		if (StringUtils.isNotEmpty(activityId)) {
			ActivityInfoEntity activityInfo = activityInfoRepository.findByActIdAndStatus(activityId,Constant.VALID_STATUS_VALID);
			if (activityInfo == null) { // 活动不存在或者已结束不予奖励
				result.put("flag", "活动不存在");
				return result;
			}
			// memo2,activityId用于查询6月市场部活动
			if (StringUtils.isNotEmpty(activityId) && Constant.ACTIVITY_ID_REGIST_13.equals(activityId)) {
				param.put("memo2", "activityCode13");// Bao_t_Cust_Info表
				param.put("activityId", "13");// BAO_T_CUST_ACTIVITY_INFO表
				myAward = custActivityInfoRepositoryCustom.findCustAwardAmount(param);
			}
			if (Constant.ACTIVITY_ID_REGIST_17.equals(activityInfo.getId())) {
				param.put("activityId", "");
				param.put("startDate", activityInfo.getStartDate());
				param.put("endDate", activityInfo.getExpireDate());
				param.put("isBackCash", true);
			    amountFor17= custActivityInfoRepositoryCustom.findTotalInvestAmountAndTotalYearAmount(param);
				myAward=new BigDecimal(amountFor17.get(0).get("totalMyAward").toString());
			}
		} else {
			List<ActivityInfoEntity> activityList = activityInfoRepository.findListActivityInfo();
			for (ActivityInfoEntity activityInfo : activityList) {
				if (activityInfo == null) { // 活动不存在或者已结束不予奖励
					result.put("flag", "活动不存在");
					return result;
				}
				paramValue.clear();
				paramValue.put("custId", (String) param.get("custId"));
				if (Constant.ACTIVITY_ID_REGIST_13.equals(activityInfo.getId())) {
					paramValue.put("activityId", activityInfo.getId());
					 activityAwardFor13 = custActivityInfoRepositoryCustom.findCustAwardAmount(paramValue);
				}
				if (Constant.ACTIVITY_ID_REGIST_17.equals(activityInfo.getId())) {
					paramValue.put("startDate", activityInfo.getStartDate());
					paramValue.put("endDate", activityInfo.getExpireDate());
					List<Map<String, Object>> amount= custActivityInfoRepositoryCustom.findTotalInvestAmountAndTotalYearAmount(paramValue);
					activityAwardFor17=new BigDecimal(amount.get(0).get("totalMyAward").toString());
			 }
				myAward = ArithUtil.add(activityAwardFor13, activityAwardFor17);
			}
		}
		int count=custActivityInfoRepositoryCustom.getCountByInviteOriginId(param);
//		List<Map<String, Object>> amount=custActivityInfoRepositoryCustom.findTotalInvestAmountAndTotalYearAmount(param);
		Page<Map<String, Object>> page = custActivityInfoRepositoryCustom.findCustRecommendList(param);
		result.put("iTotalDisplayRecords", page.getTotalElements());
		result.put("data", page.getContent());
		result.put("count", count);
		result.put("myAward", myAward);//我的奖励金
		result.put("totalInvestAmount", amountFor17==null?BigDecimal.ZERO:amountFor17.get(0).get("totalInvestAmount"));//累计出借金额
		result.put("totalYearInvestAmount",amountFor17==null?BigDecimal.ZERO: amountFor17.get(0).get("totalYearInvestAmount"));//合计年化出借金额
		log.info("===========查询我的推荐记录==============");
		return result;
	}

	/**
	 * 获取我的体验金记录
	 *
	 * @author caoyi
	 * @date 2015年05月19日 下午14:36:25
	 */
	@Override
	public Map<String, Object> findCustExperienceList(Map<String, Object> param) throws SLException {
		Map<String, Object> result = new HashMap<String, Object>();
		Page<Map<String, Object>> page = custActivityInfoRepositoryCustom.findCustExperienceList(param);
		result.put("iTotalDisplayRecords", page.getTotalElements());
		result.put("data", page.getContent());
		log.info("===========查询我的体验金记录==============");
		return result;
	}

	/**
	 * 体验金总奖励
	 *
	 * @param param
	 * @return
	 */
	public Map<String, Object> findExperienceGold(Map<String, Object> param){
		return custActivityInfoRepositoryCustom.findExperienceGold(param);
	}

	public Map<String, Object> findExperienceGoldAudit(Map<String, Object> param){
		return custActivityInfoRepositoryCustom.findExperienceGoldAudit(param);
	}
	/**
	 * 体验金总奖励
	 *
	 * @param param
	 * @return
	 */
	public Map<String, Object> findExperienceGoldById(Map<String, Object> param){
		return custActivityInfoRepositoryCustom.findExperienceGoldById(param);
	}
	/**
	 * 获取体验金信息
	 *
	 * @param param
	 * @return
	 */
	public List<Map<String,Object>> findExperienceGoldDetail(Map<String, Object> param){
		return custActivityInfoRepositoryCustom.findExperienceGoldDetail(param);
	}

	/**
	 * 推荐奖励信息
	 *
	 * @param param
	 * @return
	 */
	public Map<String, Object> findRewardById(Map<String, Object> param){
		return custActivityInfoRepositoryCustom.findRewardById(param);
	}

	/**
	 * 获取金牌推荐人统计信息
	 *
	 * @author caoyi
	 * @date 2015年08月24日 下午20:32:25
	 * @param param
	 *       <tt>custId, String:客户ID</tt><br>
	 * <br>
	 * @return Map<String, object>:
	 *         <tt>totalAmount： String:累计佣金收益 </tt><br>
	 *         <tt>custCount： String:累计推荐好友</tt><br>
	 *         <tt>commissionAmount： String:累计佣金</tt><br>
	 *         <tt>rewardAmount： String:累计奖励</tt><br>
	 */
	@Override
	public Map<String, Object> findCustCommissionInfo(Map<String, Object> param) throws SLException {
		Map<String, Object>  result=custActivityInfoRepositoryCustom.findCustCommissionInfo(param);
		log.info("===========获取金牌推荐人统计信息==============");
		return result;
	}

	/**
	 * 获取我的佣金记录
	 *
	 * @author caoyi
	 * @date 2015年08月25日 上午10:10:13
	 * @param param
	 *       <tt>start：int:分页起始页</tt><br>
	 *       <tt>length：int:每页长度</tt><br>
	 *       <tt>custId, String:客户ID</tt><br>
	 *       <tt>dateBegin： String:开始日期(可以为空)</tt><br>
	 *       <tt>dateEnd： String:结束日期(可以为空)</tt><br>
	 *       <tt>tradeStatus： String:是否核算(可以为空)</tt><br>
	 * <br>
	 * @return iTotalDisplayRecords: 总条数 data:List<Map<String, object>>
	 *         Map<String, object>: <tt>orderNo： String:序号</tt><br>
	 *         <tt>commissionId： String:记录ID</tt><br>
	 *         <tt>tradeDate： String:交易日期</tt><br>
	 *         <tt>investAmount： String:累计在投金额</tt><br>
	 *         <tt>commissionAmount： String:推广佣金</tt><br>
	 *         <tt>rewardAmount： String:推广奖励</tt><br>
	 *         <tt>tradeStatus： String:是否结算</tt><br>
	 */
	@Override
	public Map<String, Object> findCustCommissionList(Map<String, Object> param) throws SLException {
		Map<String, Object> result = new HashMap<String, Object>();
		Page<Map<String, Object>> page = custActivityInfoRepositoryCustom.findCustCommissionList(param);
		result.put("iTotalDisplayRecords", page.getTotalElements());
		result.put("data", page.getContent());
		log.info("===========获取我的佣金记录==============");
		return result;
	}

	/**
	 * 获取我的佣金详情记录
	 *
	 * @author caoyi
	 * @date 2015年08月25日 上午11:20:26
	 * @param param
	 *       <tt>start：int:分页起始页</tt><br>
	 *       <tt>length：int:每页长度</tt><br>
	 *       <tt>commissionId, String:佣金记录ID</tt><br>
	 * <br>
	 * @return iTotalDisplayRecords: 总条数 data:List<Map<String, object>>
	 *         Map<String, object>: <tt>orderNo： String:序号</tt><br>
	 *         <tt>loginName： String:推荐人</tt><br>
	 *         <tt>tradeDate： String:交易日期</tt><br>
	 *         <tt>investAmount： String:在投金额</tt><br>
	 *         <tt>commissionAmount： String:推广佣金</tt><br>
	 *         <tt>rewardAmount： String:推广奖励</tt><br>
	 *         <tt>tradeStatus： String:是否结算</tt><br>
	 */
	@Override
	public Map<String, Object> findCustCommissionDetailList(Map<String, Object> param) throws SLException {
		Map<String, Object> result = new HashMap<String, Object>();
		Page<Map<String, Object>> page = custActivityInfoRepositoryCustom.findCustCommissionDetailList(param);
		result.put("iTotalDisplayRecords", page.getTotalElements());
		result.put("data", page.getContent());
		log.info("===========获取我的佣金详情记录==============");
		return result;
	}

//	@Transactional(readOnly = false, rollbackFor = SLException.class)
//	@Override
//	public ResultVo receiveExperience(Map<String, Object> map)
//			throws SLException {
//
//		// 活动ID
//		String activityId = (String) map.get("activityId");
//		// 奖励体验金金额
//		BigDecimal activityRecommend = (BigDecimal)map.get("activityRecommend");
//		// 客户ID
//		String custId = (String) map.get("custId");
//		// 交易编号
//		String tradeNo = (String) map.get("tradeNo");
//		// 被推荐客户ID
//		String quiltCustId = (String) map.get("quiltCustId");
//		// 流水描述
//		String flowDecr = (String) map.get("flowDecr");
//		// 来源
//		String activitySource = (String) map.get("activitySource");
//
//		ActivityInfoEntity activityInfoEntity = activityInfoRepository.findByActId(activityId, Constant.VALID_STATUS_VALID, DateUtils.getStartDate(new Date()));
//		if(activityInfoEntity == null) { // 活动不存在或者已结束不予奖励
//			return new ResultVo(false, "活动已经结束，无法领取体验金");
//		}
//
//		CustActivityInfoEntity custActivityInfoEntity = new CustActivityInfoEntity();
//		custActivityInfoEntity.setCustId(custId);
//		custActivityInfoEntity.setActivityId(activityInfoEntity.getId());
//		custActivityInfoEntity.setActivitySource(activitySource);
//		custActivityInfoEntity.setTotalAmount(activityRecommend);
//		custActivityInfoEntity.setUsableAmount(activityRecommend);
//		custActivityInfoEntity.setTradeCode(tradeNo);
//		custActivityInfoEntity.setTradeStatus(Constant.USER_ACTIVITY_TRADE_STATUS_01);
//		custActivityInfoEntity.setStartDate(activityInfoEntity.getStartDate());
//		custActivityInfoEntity.setExpireDate(activityInfoEntity.getExpireDate());
//		custActivityInfoEntity.setRewardShape(Constant.REAWARD_SPREAD_01);
//		custActivityInfoEntity.setQuiltCustId(quiltCustId);
//		custActivityInfoEntity.setBasicModelProperty(custId, true);
//		custActivityInfoEntity = custActivityInfoRepository.save(custActivityInfoEntity);
//
//		AccountInfoEntity custAccount = accountInfoRepository.findByCustId(custId);
//		if(custAccount == null) {
//			log.warn(String.format("送体验金用户账户不存在，用户ID：%s", custId));
//			return new ResultVo(false, String.format("送体验金用户账户不存在，用户ID：%s", custId));
//		}
//
//		AccountFlowInfoEntity accountFlowInfoEntity = accountFlowService.saveAccountFlow(
//				custAccount, null, null, null, "1",
//				SubjectConstant.TRADE_FLOW_TYPE_EXPERIENCE_AMOUNT,
//				numberService.generateTradeBatchNumber(),
//				SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING,
//				activityRecommend,
//				SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_EXPERIENCE_AMOUNT,
//				"", "", SubjectConstant.SUBJECT_TYPE_AMOUNT);
//		accountFlowInfoEntity.setTradeNo(tradeNo);
//		accountFlowInfoEntity.setMemo(flowDecr);
//		return new ResultVo(true);
//	}

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo custActivityRecommend(Map<String, Object> params){

		String activityId = (String)params.get("activityId");
		switch(activityId) {
		case Constant.ACTIVITY_ID_REGIST_01: // 1) 注册送体验金（送1000体验金）
			custActivityRecommend1((CustInfoEntity)params.get("custInfoEntity"), (AccountInfoEntity)params.get("custAccount"), (String)params.get("tradeNo"));
			break;
		case Constant.ACTIVITY_ID_REGIST_02: // 2) 推荐有奖送现金（第一次充值成功推荐人前1-8级奖励现金）
			custActivityRecommend2((CustInfoEntity)params.get("custInfoEntity"), (String)params.get("tradeNo"));
			break;
		case Constant.ACTIVITY_ID_REGIST_03: // 3) 推荐有奖送体验金（第一次累积充值金额达100奖励体验金10000）
			custActivityRecommend3((CustInfoEntity)params.get("custInfoEntity"), (AccountInfoEntity)params.get("custAccount"), (String)params.get("tradeNo"));
			break;
		case Constant.ACTIVITY_ID_REGIST_04: // 4) 购买定期宝送体验金(买多少体验宝送多少体验金)
			custActivityRecommend4((String)params.get("custId"), (AccountInfoEntity)params.get("custAccount"), (BigDecimal)params.get("tradeAmount"), (String)params.get("tradeNo"));
			break;
		case Constant.ACTIVITY_ID_REGIST_05: // 5) 购买定期宝送流量
			custActivityRecommend5((String)params.get("custId"), (String)params.get("investId"),
					(String)params.get("tradeCode"), (DeviceInfoEntity)params.get("deviceInfoEntity"), (String)params.get("channelNo"));
			break;
		case Constant.ACTIVITY_ID_REGIST_06: // 6) 注册送流量
			custActivityRecommend6((CustInfoEntity)params.get("custInfoEntity"), (String)params.get("uid"), (String)params.get("meId"),
					(String)params.get("meVersion"), (InterfaceDetailInfoEntity)params.get("interfaceDetailInfoEntity"));
			break;
		case Constant.ACTIVITY_ID_REGIST_07: // 7) 购买定期宝推荐有奖送体验金
			custActivityRecommend7((CustInfoEntity)params.get("custInfoEntity"), (AccountInfoEntity)params.get("custAccount"), (String)params.get("tradeNo"));
			break;
		case Constant.ACTIVITY_ID_REGIST_13: // 13) 2017 6月市场部活动
			custActivityRecommend13();
			break;
		case Constant.ACTIVITY_ID_REGIST_14_REGISTER: // 14) 市场部棋王争霸赛(注册页面)
			params.put("activityCode", Constant.ACTIVITY_ID_REGIST_14);
			chessMarketDepartmentActivityByRegister(params);
			break;
		case Constant.ACTIVITY_ID_REGIST_14_LOGIN: // 14) 市场部棋王争霸赛(登录页面)
			params.put("activityCode", Constant.ACTIVITY_ID_REGIST_14);
			chessMarketDepartmentActivityByLogin(params);
			break;
			case Constant.ACTIVITY_ID_REGIST_15: // 15) 红包加息体验金
				custActivityRecommend15(params);
				break;
			case Constant.ACTIVITY_ID_REGIST_16: // 16) 市场部大转盘
			custActivityRecommend16((CustInfoEntity)params.get("custInfoEntity"), (AccountInfoEntity)params.get("custAccount"),(String)params.get("lotteryDrawMoney"),(String)params.get("tradeNo"));
			break;
		}

		return new ResultVo(true);
	}

	/**
	 * 活动1 注册送体验金
	 *
	 * 【活动规则：注册送1000体验金】
	 * 【活动时间：2015/6/1至2016/5/30】
	 *
	 * @author  wangjf
	 * @date    2015年11月21日 下午2:33:44
	 * @param custInfoEntity
	 * @param tradeNo
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public void custActivityRecommend1(CustInfoEntity  custInfoEntity, AccountInfoEntity custAccount, String tradeNo) {
		/** 活动信息 20150516 */
		ActivityInfoEntity aie = activityInfoRepository.findByActId(Constant.ACTIVITY_ID_REGIST_01, Constant.VALID_STATUS_VALID, new Date());
		if (aie != null) {

			// 体验金
			BigDecimal ara = paramService.findActivityRegistAmount();
			// 保存表BAO_T_CUST_ACTIVITY_INFO(BAO客户活动信息表)
			CustActivityInfoEntity caie = new CustActivityInfoEntity();
			caie.setCustId(custInfoEntity.getId());
			caie.setTotalAmount(ara);
			caie.setUsableAmount(ara);
			caie.setTradeStatus(Constant.USER_ACTIVITY_TRADE_STATUS_01);
			caie.setActivityId(aie.getId());
			caie.setTradeCode(tradeNo);
			caie.setStartDate(aie.getStartDate());
			caie.setExpireDate(aie.getExpireDate());
			caie.setActivitySource(Constant.ACTIVITY_SOURCE_01);
			caie.setRewardShape(Constant.REAWARD_SPREAD_01);
			caie.setBasicModelProperty(custInfoEntity.getId(), true);
			custActivityInfoRepository.save(caie);

			// 记录日志
			LogInfoEntity lie = new LogInfoEntity();
			lie.setRelateType(Constant.TABLE_BAO_T_CUST_INFO);
			lie.setRelatePrimary(custInfoEntity.getId());
			lie.setOperPerson(custInfoEntity.getId());
			lie.setLogType(Constant.OPERATION_TYPE_13);
			lie.setBasicModelProperty(custInfoEntity.getId(), true);
			logInfoEntityRepository.save(lie);

			AccountFlowInfoEntity accountFlowInfoEntity = accountFlowService.saveAccountFlow(
					custAccount, null, null, null, "1",
					SubjectConstant.TRADE_FLOW_TYPE_EXPERIENCE_AMOUNT,
					numberService.generateTradeBatchNumber(),
					SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING,
					ara,
					SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_EXPERIENCE_AMOUNT,
					"", "", SubjectConstant.SUBJECT_TYPE_AMOUNT);
			accountFlowInfoEntity.setTradeNo(tradeNo);
		}
		/** 活动信息 20150516 */
	}


	/**
	 * 活动2 推荐奖励
	 *
	 * 【活动规则：第一次充值成功推荐人前1-8级奖励现金】
	 * 【活动时间：2015/6/1至2015/7/31】
	 *
	 * @author  wangjf
	 * @date    2015年7月25日 下午5:28:06
	 * @param custInfoEntity
	 * @param tradeNo
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public void custActivityRecommend2(CustInfoEntity  custInfoEntity, String tradeNo) {

		if(StringUtils.isEmpty(custInfoEntity.getInviteOriginId()) // 用户受邀邀请码不为空时才需计算推荐奖励
				|| StringUtils.isEmpty(custInfoEntity.getSpreadLevel()) // 推广级别不能为空
				|| custInfoEntity.getSpreadLevel().equals("0")){          // 并且推广级别非0
			return;
		}

		BigDecimal countInvestTimes = custActivityInfoRepository.countByCustId(custInfoEntity.getId(), Constant.ACTIVITY_ID_REGIST_02);
		if(countInvestTimes.compareTo(new BigDecimal("0")) != 0) { // 推荐奖励次数非0不予奖励（送现金奖励）
			return;
		}

		countInvestTimes = custActivityInfoRepository.countByQuiltCustId(custInfoEntity.getId(), Constant.ACTIVITY_ID_REGIST_03);
		if(countInvestTimes.compareTo(new BigDecimal("0")) != 0) { // 推荐奖励次数非0不予奖励（送体验金奖励）
			return;
		}

		// 注：根据用户推广级别，取推荐奖励，当级别<8时，取1~当前级别总和;当级别大于等于8时，取1～8级别总和
		ActivityInfoEntity activityInfoEntity = activityInfoRepository.findByActId(Constant.ACTIVITY_ID_REGIST_02, Constant.VALID_STATUS_VALID, DateUtils.getStartDate(new Date()));
		if(activityInfoEntity == null) { // 活动不存在或者已结束，不再生成奖励
			return;
		}

		// 插入被推荐人（当前投资人）奖励（之前所有推荐人奖励总和）
		String startLevel = "1";
		String endLevel = (Integer.valueOf(custInfoEntity.getSpreadLevel()) >= 8 ? "8" : custInfoEntity.getSpreadLevel());
		BigDecimal activityRecommend = paramService.findActivityRecommend(startLevel, endLevel);
		CustActivityInfoEntity custActivityInfoEntity = new CustActivityInfoEntity();
		custActivityInfoEntity.setCustId(custInfoEntity.getId());
		custActivityInfoEntity.setActivityId(activityInfoEntity.getId());
		custActivityInfoEntity.setActivitySource(Constant.ACTIVITY_SOURCE_02);
		custActivityInfoEntity.setTotalAmount(activityRecommend);
		custActivityInfoEntity.setUsableAmount(activityRecommend);
		custActivityInfoEntity.setTradeCode(tradeNo);
		custActivityInfoEntity.setTradeStatus(Constant.USER_ACTIVITY_TRADE_STATUS_06);
		custActivityInfoEntity.setStartDate(activityInfoEntity.getStartDate());
		custActivityInfoEntity.setExpireDate(activityInfoEntity.getExpireDate());
		custActivityInfoEntity.setRewardShape(Constant.REAWARD_SPREAD_02);
		custActivityInfoEntity.setBasicModelProperty(custInfoEntity.getId(), true);
		custActivityInfoEntity = custActivityInfoRepository.save(custActivityInfoEntity);

		// 插入推荐人奖励明细
		// 通过被推荐人的ID递归找到所有父节点（8层以内的），按照级别依此给予奖励
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("custId", custInfoEntity.getId());
		map.put("custSpreadLevel", custInfoEntity.getSpreadLevel());
		map.put("queryPermission", custInfoEntity.getQueryPermission());
		List<Map<String, Object>> custActivityDetailList = custActivityInfoRepositoryCustom.caclCustActivityDetail(map);
		List<CustActivityDetailEntity> custActivityDetailEntityList = new ArrayList<CustActivityDetailEntity>();
		for(Map<String, Object> m : custActivityDetailList) {
			CustActivityDetailEntity custActivityDetailEntity = new CustActivityDetailEntity();
			custActivityDetailEntity.setCustId((String)m.get("custId"));
			custActivityDetailEntity.setCustActivityId(custActivityInfoEntity.getId());
			custActivityDetailEntity.setTradeAmount(new BigDecimal(m.get("tradeAmount").toString()));
			custActivityDetailEntity.setUsableAmount(new BigDecimal(m.get("tradeAmount").toString()));
			custActivityDetailEntity.setTradeStatus(Constant.USER_ACTIVITY_TRADE_STATUS_06);
			custActivityDetailEntity.setBasicModelProperty(custInfoEntity.getId(), true);
			custActivityDetailEntityList.add(custActivityDetailEntity);
		}
		custActivityInfoRepositoryCustom.batchInsertActivityDetail(custActivityDetailEntityList);

	}

	/**
	 * 活动3 推荐奖励
	 *
	 * 【活动规则：第一次累积充值金额达100奖励体验金10000】
	 * 【活动时间：2015/8/1至2015/10/31】
	 *
	 * @author  wangjf
	 * @date    2015年7月25日 下午5:28:54
	 * @param custInfoEntity
	 * @param tradeNo
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public void custActivityRecommend3(CustInfoEntity  custInfoEntity, AccountInfoEntity custAccount, String tradeNo) {

		if(StringUtils.isEmpty(custInfoEntity.getInviteOriginId()) // 用户受邀邀请码不为空时才需计算推荐奖励
				|| StringUtils.isEmpty(custInfoEntity.getSpreadLevel()) // 推广级别不能为空
				|| custInfoEntity.getSpreadLevel().equals("0")){          // 并且推广级别非0
			return;
		}

		BigDecimal countInvestTimes = custActivityInfoRepository.countByCustId(custInfoEntity.getId(), Constant.ACTIVITY_ID_REGIST_02);
		if(countInvestTimes.compareTo(new BigDecimal("0")) != 0) { // 推荐奖励次数非0不予奖励（送现金奖励）
			return;
		}

		countInvestTimes = custActivityInfoRepository.countByQuiltCustId(custInfoEntity.getId(), Constant.ACTIVITY_ID_REGIST_03);
		if(countInvestTimes.compareTo(new BigDecimal("0")) != 0) { // 推荐奖励次数非0不予奖励（送体验金奖励）
			return;
		}

		BigDecimal sumRechargeAmount = accountFlowInfoRepository.sumTradeAmountByCustIdAndTradeType(custInfoEntity.getId(), SubjectConstant.TRADE_FLOW_TYPE_RECHARGE);
		if(sumRechargeAmount.compareTo(new BigDecimal("100")) < 0) { // 统计累积充值金额未达到100不予奖励
			return;
		}

		ActivityInfoEntity activityInfoEntity = activityInfoRepository.findByActId(Constant.ACTIVITY_ID_REGIST_03, Constant.VALID_STATUS_VALID, DateUtils.getStartDate(new Date()));
		if(activityInfoEntity == null) { // 活动不存在或者已结束不予奖励
			return;
		}

		// 生成推荐人活动记录
		BigDecimal activityRecommend = paramService.findActivityRecommend();
		CustActivityInfoEntity custActivityInfoEntity = new CustActivityInfoEntity();
		custActivityInfoEntity.setCustId(custInfoEntity.getInviteOriginId());
		custActivityInfoEntity.setActivityId(activityInfoEntity.getId());
		custActivityInfoEntity.setActivitySource(Constant.ACTIVITY_SOURCE_03);
		custActivityInfoEntity.setTotalAmount(activityRecommend);
		custActivityInfoEntity.setUsableAmount(activityRecommend);
		custActivityInfoEntity.setTradeCode(tradeNo);
		custActivityInfoEntity.setTradeStatus(Constant.USER_ACTIVITY_TRADE_STATUS_01);
		custActivityInfoEntity.setStartDate(activityInfoEntity.getStartDate());
		custActivityInfoEntity.setExpireDate(activityInfoEntity.getExpireDate());
		custActivityInfoEntity.setRewardShape(Constant.REAWARD_SPREAD_01);
		custActivityInfoEntity.setQuiltCustId(custInfoEntity.getId());
		custActivityInfoEntity.setBasicModelProperty(custInfoEntity.getId(), true);
		custActivityInfoEntity = custActivityInfoRepository.save(custActivityInfoEntity);

		AccountFlowInfoEntity accountFlowInfoEntity = accountFlowService.saveAccountFlow(
				custAccount, null, null, null, "1",
				SubjectConstant.TRADE_FLOW_TYPE_EXPERIENCE_AMOUNT,
				numberService.generateTradeBatchNumber(),
				SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING,
				activityRecommend,
				SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_EXPERIENCE_AMOUNT,
				"", "", SubjectConstant.SUBJECT_TYPE_AMOUNT);
		accountFlowInfoEntity.setMemo(String.format("推荐用户%s的奖励", custInfoEntity.getLoginName()));
	}

	/**
	 * 活动4 购买定期宝送体验金
	 *
	 * 【活动规则：购买定期宝送体验金，买多少送多少】
	 * 【活动时间：2015/10/1至2015/10/8，2015/12/08至2016/01/12】
	 *
	 * @author  wangjf
	 * @date    2015年10月28日 下午4:51:22
	 * @param custId
	 * @param tradeAmount
	 * @throws SLException
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public void custActivityRecommend4(String custId, AccountInfoEntity custAccount, BigDecimal tradeAmount, String tradeNo){
		ActivityInfoEntity activityInfoEntity = activityInfoRepository.findByActId(Constant.ACTIVITY_ID_REGIST_04, Constant.VALID_STATUS_VALID, DateUtils.getStartDate(new Date()));
		if(activityInfoEntity != null) { // 活动不存在或者已结束不予奖励
			BigDecimal activityInvest = investInfoRepository.countInvestInfoByCustIdAndInvestDate(custId, Constant.PRODUCT_TYPE_04);

			if(activityInvest == null || activityInvest.compareTo(new BigDecimal("1")) <= 0) {

				CustActivityInfoEntity custActivityInfoEntity = new CustActivityInfoEntity();
				custActivityInfoEntity.setCustId(custId);
				custActivityInfoEntity.setActivityId(activityInfoEntity.getId());
				custActivityInfoEntity.setActivitySource(Constant.ACTIVITY_SOURCE_04);
				custActivityInfoEntity.setTotalAmount(tradeAmount);
				custActivityInfoEntity.setUsableAmount(tradeAmount);
				custActivityInfoEntity.setTradeCode(tradeNo);
				custActivityInfoEntity.setTradeStatus(Constant.USER_ACTIVITY_TRADE_STATUS_01);
				custActivityInfoEntity.setStartDate(activityInfoEntity.getStartDate());
				custActivityInfoEntity.setExpireDate(activityInfoEntity.getExpireDate());
				custActivityInfoEntity.setRewardShape(Constant.REAWARD_SPREAD_01);
				custActivityInfoEntity.setBasicModelProperty(custId, true);
				custActivityInfoEntity = custActivityInfoRepository.save(custActivityInfoEntity);

				AccountFlowInfoEntity accountFlowInfoEntity = accountFlowService.saveAccountFlow(
						custAccount, null, null, null, "1",
						SubjectConstant.TRADE_FLOW_TYPE_EXPERIENCE_AMOUNT,
						numberService.generateTradeBatchNumber(),
						SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING,
						tradeAmount,
						SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_EXPERIENCE_AMOUNT,
						Constant.TABLE_BAO_T_CUST_ACTIVITY_INFO, custActivityInfoEntity.getId(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
				accountFlowInfoEntity.setTradeNo(tradeNo);
				accountFlowInfoEntity.setMemo("购买定期宝送体验金");
			}
		}
	}

	/**
	 * 活动5 购买定期宝送流量
	 *
	 * 【活动规则：通过“吉融”渠道购买定期宝的用户送500M流量包】
	 * 【活动时间：2015/11/1至2016/11/1】
	 *
	 * @author  wangjf
	 * @date    2015年10月28日 下午4:55:02
	 * @param custId
	 * @param investId
	 * @param tradeCode
	 * @param deviceInfoEntity
	 * @param channelNo
	 * @param
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public void custActivityRecommend5(String custId, String investId,
			String tradeCode, DeviceInfoEntity deviceInfoEntity, String channelNo) {

		if(StringUtils.isEmpty(channelNo)) {
			return;
		}

		InterfaceDetailInfoEntity interfaceDetailInfoEntity = interfaceDetailInfoRepository.findByInterfaceMerchantCode(Constant.OPERATION_TYPE_21, channelNo);
		if(interfaceDetailInfoEntity == null || !"Y".equals(interfaceDetailInfoEntity.getIsNotice())) {
			return;
		}

		ActivityInfoEntity activityInfoEntity = activityInfoRepository.findByActId(Constant.ACTIVITY_ID_REGIST_05, Constant.VALID_STATUS_VALID, DateUtils.getStartDate(new Date()));
		if(activityInfoEntity != null) { // 活动不存在或者已结束不予奖励
			BigDecimal activityInvest = investInfoRepository.countInvestInfoByCustIdAndInvestDate(custId, Constant.PRODUCT_TYPE_04, DateUtils.formatDate(activityInfoEntity.getStartDate(), "yyyyMMdd"), DateUtils.formatDate(activityInfoEntity.getExpireDate(), "yyyyMMdd"));

			if(activityInvest == null || activityInvest.compareTo(new BigDecimal("1")) <= 0) {
				ExpandInfoEntity expandInfoEntity = new ExpandInfoEntity();
				expandInfoEntity.setRelateTableIdentification(Constant.TABLE_BAO_T_INVEST_INFO);
				expandInfoEntity.setRelatePrimary(investId);
				expandInfoEntity.setInnerTradeCode(numberService.generateOpenServiceTradeNumber());
				expandInfoEntity.setTradeCode(tradeCode);
				expandInfoEntity.setThirdPartyType(interfaceDetailInfoEntity.getThirdPartyType());
				expandInfoEntity.setInterfaceType(interfaceDetailInfoEntity.getInterfaceType());
				expandInfoEntity.setMerchantCode(interfaceDetailInfoEntity.getMerchantCode());
				expandInfoEntity.setRequestTime(DateUtils.getCurrentDate("yyyyMMddHHmmss"));
				expandInfoEntity.setAlreadyNotifyTimes(0);
				expandInfoEntity.setMemo("waiting");// 此标识表示需要同步
				expandInfoEntity.setMeId(deviceInfoEntity.getMeId());
				expandInfoEntity.setMeVersion(deviceInfoEntity.getMeVersion());
				expandInfoEntity.setAppSource(Strings.nullToEmpty(deviceInfoEntity.getAppSource()).toLowerCase());
				expandInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
				expandInfoRepository.save(expandInfoEntity);
			}
		}
	}

	/**
	 * 活动6 注册送流量
	 *
	 * 【活动规则：通过“吉融”渠道注册的用户送10M流量包】
	 * 【活动时间：2015/11/1至2016/11/1】
	 *
	 * @author  wangjf
	 * @date    2015年11月21日 下午3:54:08
	 * @param custInfoEntity
	 * @param uid
	 * @param meId
	 * @param meVersion
	 * @param interfaceDetailInfoEntity
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public void custActivityRecommend6(CustInfoEntity custInfoEntity, String uid, String meId,
			String meVersion, InterfaceDetailInfoEntity interfaceDetailInfoEntity) {

		ActivityInfoEntity activityInfoEntity = activityInfoRepository.findByActId(Constant.ACTIVITY_ID_REGIST_06, Constant.VALID_STATUS_VALID, DateUtils.getStartDate(new Date()));
		if(activityInfoEntity != null) {
			ExpandInfoEntity expandInfoEntity = new ExpandInfoEntity();
			expandInfoEntity.setRelateTableIdentification(Constant.TABLE_BAO_T_CUST_INFO);
			expandInfoEntity.setRelatePrimary(custInfoEntity.getId());
			expandInfoEntity.setInnerTradeCode(numberService.generateOpenServiceTradeNumber());
			expandInfoEntity.setTradeCode(uid);
			expandInfoEntity.setThirdPartyType(interfaceDetailInfoEntity.getThirdPartyType());
			expandInfoEntity.setInterfaceType(interfaceDetailInfoEntity.getInterfaceType());
			expandInfoEntity.setMerchantCode(interfaceDetailInfoEntity.getMerchantCode());
			expandInfoEntity.setRequestTime(DateUtils.getCurrentDate("yyyyMMddHHmmss"));
			expandInfoEntity.setAlreadyNotifyTimes(0);
			expandInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
			expandInfoEntity.setMemo("waiting");// 此标识表示需要同步
			expandInfoEntity.setMeId(meId);
			expandInfoEntity.setMeVersion(meVersion);
			expandInfoEntity.setAppSource(Strings.nullToEmpty(custInfoEntity.getCustSource()).toLowerCase());
			expandInfoRepository.save(expandInfoEntity);
		}
	}

	/**
	 * 活动7 购买定期宝推荐有奖送体验金
	 *
	 * 【活动规则：要求好友注册的时间和首投定期宝都在活动期间内才送1万块】
	 * 【活动时间：2015/12/08至2016/01/12】
	 *
	 * @author  wangjf
	 * @date    2015年11月21日 下午3:58:12
	 * @param custInfoEntity
	 * @param
	 * @param custAccount
	 * @param
	 * @param tradeNo
	 * @throws SLException
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public void custActivityRecommend7(CustInfoEntity custInfoEntity, AccountInfoEntity custAccount, String tradeNo){

		if(StringUtils.isEmpty(custInfoEntity.getInviteOriginId()) // 用户受邀邀请码不为空时才需计算推荐奖励
				|| StringUtils.isEmpty(custInfoEntity.getSpreadLevel()) // 推广级别不能为空
				|| custInfoEntity.getSpreadLevel().equals("0")){          // 并且推广级别非0
			return;
		}

		ActivityInfoEntity activityInfoEntity = activityInfoRepository.findByActId(Constant.ACTIVITY_ID_REGIST_07, Constant.VALID_STATUS_VALID, DateUtils.getStartDate(new Date()));
		if(activityInfoEntity == null) { // 活动不存在或者已结束不予奖励
			return;
		}

//		BigDecimal countInvestTimes = custActivityInfoRepository.countByCustId(custInfoEntity.getId(), Constant.ACTIVITY_ID_REGIST_02);
//		if(countInvestTimes.compareTo(new BigDecimal("0")) != 0) { // 推荐奖励次数非0不予奖励（在以往活动充值送现金中已经奖励过）
//			return;
//		}
//
//		countInvestTimes = custActivityInfoRepository.countByQuiltCustId(custInfoEntity.getId(), Constant.ACTIVITY_ID_REGIST_03);
//		if(countInvestTimes.compareTo(new BigDecimal("0")) != 0) { // 推荐奖励次数非0不予奖励（在以往活动充值送体验金中已经奖励过）
//			return;
//		}

		if(DateUtils.truncateDate(custInfoEntity.getCreateDate()).compareTo(activityInfoEntity.getStartDate()) < 0
				|| DateUtils.truncateDate(custInfoEntity.getCreateDate()).compareTo(activityInfoEntity.getExpireDate()) > 0) { // 用户注册时间不在活动期间不予奖励
			return;
		}

		BigDecimal countInvestTimes = custActivityInfoRepository.countByQuiltCustId(custInfoEntity.getId(), Constant.ACTIVITY_ID_REGIST_07);
		if(countInvestTimes.compareTo(new BigDecimal("0")) != 0) { // 推荐奖励次数非0不予奖励（在本次购买定期宝送体验金中已经奖励过）
			return;
		}

		// 生成推荐人活动记录
		BigDecimal activityRecommend = paramService.findActivityRecommend();
		CustActivityInfoEntity custActivityInfoEntity = new CustActivityInfoEntity();
		custActivityInfoEntity.setCustId(custInfoEntity.getInviteOriginId());
		custActivityInfoEntity.setActivityId(activityInfoEntity.getId());
		custActivityInfoEntity.setActivitySource(Constant.ACTIVITY_SOURCE_03);
		custActivityInfoEntity.setTotalAmount(activityRecommend);
		custActivityInfoEntity.setUsableAmount(activityRecommend);
		custActivityInfoEntity.setTradeCode(tradeNo);
		custActivityInfoEntity.setTradeStatus(Constant.USER_ACTIVITY_TRADE_STATUS_01);
		custActivityInfoEntity.setStartDate(activityInfoEntity.getStartDate());
		custActivityInfoEntity.setExpireDate(activityInfoEntity.getExpireDate());
		custActivityInfoEntity.setRewardShape(Constant.REAWARD_SPREAD_01);
		custActivityInfoEntity.setQuiltCustId(custInfoEntity.getId());
		custActivityInfoEntity.setBasicModelProperty(custInfoEntity.getId(), true);
		custActivityInfoEntity = custActivityInfoRepository.save(custActivityInfoEntity);

		AccountInfoEntity inviteOriginCustAccount = accountInfoRepository.findByCustId(custInfoEntity.getInviteOriginId());
		if(inviteOriginCustAccount == null) {
			log.warn(String.format("购买定期宝送推荐体验金推荐用户账户不存在，用户ID：%s", custInfoEntity.getInviteOriginId()));
			return;
		}

		AccountFlowInfoEntity accountFlowInfoEntity = accountFlowService.saveAccountFlow(
				inviteOriginCustAccount, null, null, null, "1",
				SubjectConstant.TRADE_FLOW_TYPE_EXPERIENCE_AMOUNT,
				numberService.generateTradeBatchNumber(),
				SubjectConstant.SUBJECT_TYPE_DIRECTION_COMING,
				activityRecommend,
				SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_EXPERIENCE_AMOUNT,
				Constant.TABLE_BAO_T_CUST_ACTIVITY_INFO, custActivityInfoEntity.getId(), SubjectConstant.SUBJECT_TYPE_AMOUNT);
		accountFlowInfoEntity.setTradeNo(tradeNo);
		accountFlowInfoEntity.setMemo(String.format("推荐用户%s的奖励", custInfoEntity.getLoginName()));
	}

	@Override
	public ResultVo custActivityInvestRecommendByLogin(Map<String, Object> param) {
		String custId = (String)param.get("custId");
		CustInfoEntity custInfo = custInfoRepository.findOne(custId);

		ResultVo resultVo = internalCustActivityInfoService.custActivityInvestRecommendByLogin(param);
		if(ResultVo.isSuccess(resultVo)) {
			//发送短信
			 Map<String, Object> smsParams = new HashMap<String, Object>();
			 smsParams.put("mobile", custInfo.getMobile());
			 smsParams.put("custId", custInfo.getId());
			 smsParams.put("messageType", Constant.SMS_TYPE_ACTIVITY_REWARD);
			 smsParams.put("values", new Object[] {});
			 smsService.asnySendSMSAndSystemMessage(smsParams);
		}

		return new ResultVo(true,"活动奖励设置成功");
	}

	@Override
	public ResultVo custActivityInvestRecommendByRegister(Map<String, Object> params) {

		ResultVo resultVo = internalCustActivityInfoService.custActivityInvestRecommendByRegister(params);

		if(ResultVo.isSuccess(resultVo)) {
			//发送短信
			 CustInfoEntity savedEntity =  (CustInfoEntity)params.get("savedEntity");
			 Map<String, Object> smsParams = new HashMap<String, Object>();
			 smsParams.put("mobile", savedEntity.getMobile());
			 smsParams.put("custId", savedEntity.getId());
			 smsParams.put("messageType", Constant.SMS_TYPE_ACTIVITY_REWARD);
			 smsParams.put("values", new Object[] {});
			 smsService.asnySendSMSAndSystemMessage(smsParams);
		}


		return new ResultVo(true,"活动奖励设置成功");
	}

	@Override
	@Transactional(rollbackFor = SLException.class)
	public ResultVo custActivityRecommend15(Map<String, Object> params) {
		CustInfoEntity cust = (CustInfoEntity)params.get("custInfoEntity");
		String custId = cust.getId();
		String activityId = CommonUtils.emptyToString(params.get("activityId"));

		//先查询该用户
		List<CustActivityInfoEntity> activities = custActivityInfoRepository.findByCustIdAndActivityId(custId, activityId);
		if (activities == null || activities.isEmpty()) {
//				现金红包记录
//				≥100  3张1元现金红包
//				≥1000 5张5元现金红包
//				≥5000 5张10元现金红包
//				≥10000 3张30元现金红包
//				≥20000 2张80元 现金红包
//				≥50000 1张180元现金红包
//				≥100000 1张380元现金红包
			Date today = new Date();
			List<CustActivityInfoEntity> activityInfoEntities = new ArrayList<>();
			for (int i = 0; i < 3; i++) {
				// 3张1元现金红包
				CustActivityInfoEntity custActivityInfo =
						genRedEnvelope(activityId,Constant.ACTIVITY_AWARD_ID_15_1, BigDecimal.ONE,
								custId, today, org.apache.commons.lang3.time.DateUtils.addMonths(today, 1)
								,Constant.ACTIVITY_DESC_15_01,Constant.REAWARD_SPREAD_04);
				// 3张30元现金红包
				CustActivityInfoEntity custActivityInfo2 =
						genRedEnvelope(activityId, Constant.ACTIVITY_AWARD_ID_15_30, new BigDecimal(30),
								custId, today, org.apache.commons.lang3.time.DateUtils.addMonths(today, 1)
								,Constant.ACTIVITY_DESC_15_01,Constant.REAWARD_SPREAD_04);
				activityInfoEntities.add(custActivityInfo);
				activityInfoEntities.add(custActivityInfo2);
			}
			for (int i = 0; i < 5; i++) {
				// 5张5元现金红包
				CustActivityInfoEntity custActivityInfo =
						genRedEnvelope(activityId, Constant.ACTIVITY_AWARD_ID_15_5, new BigDecimal(5),
								custId, today, org.apache.commons.lang3.time.DateUtils.addMonths(today, 1)
								,Constant.ACTIVITY_DESC_15_01,Constant.REAWARD_SPREAD_04);
				// 5张10元现金红包
				CustActivityInfoEntity custActivityInfo2 =
						genRedEnvelope(activityId, Constant.ACTIVITY_AWARD_ID_15_10, new BigDecimal(10),
								custId, today, org.apache.commons.lang3.time.DateUtils.addMonths(today, 1)
								,Constant.ACTIVITY_DESC_15_01,Constant.REAWARD_SPREAD_04);
				activityInfoEntities.add(custActivityInfo);
				activityInfoEntities.add(custActivityInfo2);
			}
			for (int i = 0; i < 2; ++i) {
				// 2张80元 现金红包
				CustActivityInfoEntity custActivityInfo =
						genRedEnvelope(activityId, Constant.ACTIVITY_AWARD_ID_15_80, new BigDecimal(80),
								custId, today, org.apache.commons.lang3.time.DateUtils.addMonths(today, 1)
								,Constant.ACTIVITY_DESC_15_01,Constant.REAWARD_SPREAD_04);
				activityInfoEntities.add(custActivityInfo);
			}
			// 1张180元现金红包
			CustActivityInfoEntity custActivityInfo =
					genRedEnvelope(activityId, Constant.ACTIVITY_AWARD_ID_15_180, new BigDecimal(180),
							custId, today, org.apache.commons.lang3.time.DateUtils.addMonths(today, 1)
							,Constant.ACTIVITY_DESC_15_01,Constant.REAWARD_SPREAD_04);
			// 1张380元现金红包
			CustActivityInfoEntity custActivityInfo2 =
					genRedEnvelope(activityId, Constant.ACTIVITY_AWARD_ID_15_380, new BigDecimal(380),
							custId, today, org.apache.commons.lang3.time.DateUtils.addMonths(today, 1)
							,Constant.ACTIVITY_DESC_15_01,Constant.REAWARD_SPREAD_04);
			//体验金 5000元
			CustActivityInfoEntity custActivityInfo3 = 
					genRedEnvelope(activityId, Constant.ACTIVITY_AWARD_ID_15_5000, new BigDecimal(5000),
							custId, today, org.apache.commons.lang3.time.DateUtils.addMonths(today, 1)
							,Constant.ACTIVITY_DESC_15_02,Constant.REAWARD_SPREAD_01);
			//加息券2%
			for (int i = 0; i < 2; i++) {
				String awardId = i == 0 ? Constant.ACTIVITY_AWARD_ID_15_201 : Constant.ACTIVITY_AWARD_ID_15_202;
				int rate = i == 0 ? 2 : 1;
				CustActivityInfoEntity custActivityInfo4 = 
						genRedEnvelope(activityId, awardId, new BigDecimal(rate),
								custId, today, org.apache.commons.lang3.time.DateUtils.addMonths(today, 1)
								,Constant.ACTIVITY_DESC_15_03,Constant.REAWARD_SPREAD_05);
				activityInfoEntities.add(custActivityInfo4);
			}
			
			activityInfoEntities.add(custActivityInfo);
			activityInfoEntities.add(custActivityInfo2);
			activityInfoEntities.add(custActivityInfo3);
			custActivityInfoRepository.save(activityInfoEntities);

			return new ResultVo(true,"活动奖励设置成功");
		}
		return new ResultVo(false,"活动奖励设置失败");

	}

	/**
	 * 活动16 市场部大转盘
	 *
	 * 【活动规则：转盘抽奖送红包】
	 * 【活动时间：2017/7/10至】
	 *
	 * @author  zhangyb
	 * @date  	2017年6月29日
	 * @param custInfoEntity
	 * @param lotteryDrawMoney
	 * @param tradeNo
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo custActivityRecommend16(CustInfoEntity  custInfoEntity, AccountInfoEntity custAccount,String lotteryDrawMoney,String tradeNo) {
		ActivityInfoEntity aie = activityInfoRepository.findByActId(Constant.ACTIVITY_ID_REGIST_16, Constant.VALID_STATUS_VALID, new Date());
		List<CustActivityInfoEntity> activityInfoEntities = new ArrayList<>();
		String[] amount = {Constant.ACTIVITY_AWARD_ID_16_10,Constant.ACTIVITY_AWARD_ID_16_20,Constant.ACTIVITY_AWARD_ID_16_50,Constant.ACTIVITY_AWARD_ID_16_100,Constant.ACTIVITY_AWARD_ID_16_200,Constant.ACTIVITY_AWARD_ID_16_500};
		
		if (aie != null) {
			for(int i = 0; i < amount.length; i++){
				CustActivityInfoEntity caie = new CustActivityInfoEntity();
				// 保存表BAO_T_CUST_ACTIVITY_INFO(BAO客户活动信息表)
				caie.setCustId(custInfoEntity.getId());
				caie.setTotalAmount(new BigDecimal(amount[i].substring(amount[i].length()-2)));
				caie.setUsableAmount(new BigDecimal(amount[i].substring(amount[i].length()-2)));
				caie.setTradeStatus(Constant.USER_ACTIVITY_TRADE_STATUS_01);
				caie.setActivityId(aie.getId());
				caie.setTradeCode(tradeNo);
				caie.setStartDate(new Date());
				Long expireTime = System.currentTimeMillis()+30*24*60*60*1000l;
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis(expireTime);
				caie.setExpireDate(c.getTime());
				caie.setActivitySource(Constant.ACTIVITY_SOURCE_06);
				caie.setActivityDesc(Constant.ACTIVITY_DESC_16_01);
				caie.setRewardShape(Constant.REAWARD_SPREAD_04);
				caie.setBasicModelProperty(custInfoEntity.getId(), true);
				caie.setActivityAwardId(amount[i]);
				activityInfoEntities.add(caie);
			}
			custActivityInfoRepository.save(activityInfoEntities);
		}
		return new ResultVo(true,"活动奖励设置成功");

	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void redEnvelopeFlowJob() throws SLException {
		List<CustActivityInfoEntity> custActivityInfoEntities = custActivityInfoRepository
				.findByAwaitingPayment("待扣款", Constant.USER_ACTIVITY_TRADE_STATUS_03);
		if (custActivityInfoEntities == null || custActivityInfoEntities.isEmpty()) {
			return;
		}
		AccountInfoEntity account = accountInfoRepository.findByAccountNo(Constant.ACCOUNT_NO_RED_ENVELOPE);
		// 记录红包账户支出流水
		for (CustActivityInfoEntity entity : custActivityInfoEntities) {
			String memo = entity.getMemo();
			String investId = memo.substring(memo.indexOf("投资编号") + 5, memo.indexOf("请求编号") - 1);
			String requestNo = memo.substring(memo.indexOf("请求编号") + 5, memo.indexOf("END") - 1);
			custAccountService.updateAccountExt(account, null, null,
					null, "6", SubjectConstant.TRADE_FLOW_TYPE_RED_ENVELOPE_REVERT,
					requestNo, entity.getTotalAmount(), SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_RED_ENVELOPE_REVERT,
					Constant.TABLE_BAO_T_INVEST_INFO, investId,
					memo, entity.getCustId(), null);
			// 标识活动记录流水已处理
			entity.setMemo(memo.replaceAll("待扣款","已扣款"));
		}
	}
	/**
	 * 
	 * <注册送红包送加息券>
	 * <功能详细描述>
	 *
	 * @param activityId 活动ID
	 * @param activityAwardId 活动奖励ID：对应红包、加息券、体验金的ID
	 * @param amount 金额
	 * @param custId 用户ID
	 * @param startDate
	 * @param expireDate 
	 * @param sourceDesc 红包的描述
	 * @param rewardShape
	 * @return [参数说明]
	 * @return CustActivityInfoEntity [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	private CustActivityInfoEntity genRedEnvelope(String activityId, String activityAwardId,
			BigDecimal amount, String custId, Date startDate, Date expireDate,String sourceDesc,String rewardShape) {
		CustActivityInfoEntity custActivityInfo = new CustActivityInfoEntity();
		custActivityInfo.setActivityId(activityId);
		custActivityInfo.setCustId(custId);
		custActivityInfo.setActivityDesc(sourceDesc);
		custActivityInfo.setActivitySource(sourceDesc);
		custActivityInfo.setTotalAmount(amount);
		custActivityInfo.setUsableAmount(amount);
		custActivityInfo.setStartDate(startDate);
		custActivityInfo.setExpireDate(expireDate);
		custActivityInfo.setBasicModelProperty(custId, true);
		custActivityInfo.setTradeStatus(Constant.USER_ACTIVITY_TRADE_STATUS_01);
		custActivityInfo.setRewardShape(rewardShape);
		custActivityInfo.setTradeCode(flowNumberService.generateActivitySequnce());
		custActivityInfo.setActivityAwardId(activityAwardId);
		custActivityInfo.setMemo("红包加息体验金");
		return custActivityInfo;
	}

	@Autowired
	private InternalCustActivityInfoService internalCustActivityInfoService;

	@Service
	public static class InternalCustActivityInfoService {

		@Autowired
		private ActivityInfoRepository activityInfoRepository;

		@Autowired
		private CustActivityInfoRepository custActivityInfoRepository;

		@Autowired
		private FlowNumberService flowNumberService;

		@Autowired
		CustInfoRepository custInfoRepository;

		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public ResultVo custActivityInvestRecommendByLogin(Map<String, Object> param) {
			String custId = (String)param.get("custId");
			CustInfoEntity custInfo = custInfoRepository.findOne(custId);
			 //保存活动信息
			if (!org.springframework.util.StringUtils.isEmpty(param.get("activityCode"))) {
				//先查询该活动是否有效
				ActivityInfoEntity activityInfo = activityInfoRepository.findByActId(param.get("activityCode").toString(), Constant.VALID_STATUS_VALID, new Date());
				if (activityInfo!=null) {
						//在查询该用户是否参与过该活动
						CustActivityInfoEntity custActivityInfoEntity = custActivityInfoRepository.findByActivityIdAndCustId(param.get("activityCode").toString(),custInfo.getId());
						if (custActivityInfoEntity==null) {
							CustActivityInfoEntity custActivityInfo = new CustActivityInfoEntity();
							custActivityInfo.setActivityId(CommonUtils.emptyToString(activityInfo.getId()));
							custActivityInfo.setCustId(CommonUtils.emptyToString(custInfo.getId()));
							custActivityInfo.setActivityDesc(Constant.ACTIVITY_DESC_02);
							custActivityInfo.setActivitySource(CommonUtils.emptyToString(activityInfo.getActivityName()));
							custActivityInfo.setTotalAmount(new BigDecimal("50"));
							custActivityInfo.setUsableAmount(new BigDecimal("50"));
							custActivityInfo.setStartDate(activityInfo.getStartDate());
							custActivityInfo.setExpireDate(activityInfo.getExpireDate());
							custActivityInfo.setBasicModelProperty(custInfo.getId(), true);
							custActivityInfo.setTradeStatus(Constant.USER_ACTIVITY_TRADE_STATUS_01);
							 custActivityInfo.setRewardShape(Constant.REAWARD_SPREAD_02);
							custActivityInfo.setTradeCode(flowNumberService.generateActivitySequnce());
							custActivityInfoRepository.save(custActivityInfo);
							return new ResultVo(true,"活动奖励设置成功");
						}
				}
			}
			return new ResultVo(false,"活动奖励设置失败");
		}

		@Transactional(readOnly = false, rollbackFor = SLException.class)
		public ResultVo custActivityInvestRecommendByRegister(Map<String, Object> params) {

			String activityCode = (String)params.get("activityCode");
			if (params.get("savedEntity")!=null) {
				CustInfoEntity savedEntity =  (CustInfoEntity)params.get("savedEntity");
				 //判断是否符合
				 ActivityInfoEntity activityInfo = activityInfoRepository.findByActId(activityCode, Constant.VALID_STATUS_VALID, new Date());
				 if (activityInfo!=null) {
						 CustActivityInfoEntity custActivityInfo = new CustActivityInfoEntity();
						 custActivityInfo.setActivityId(CommonUtils.emptyToString(activityInfo.getId()));
						 custActivityInfo.setCustId(CommonUtils.emptyToString(savedEntity.getId()));
						 custActivityInfo.setActivityDesc(Constant.ACTIVITY_DESC_01);
						 custActivityInfo.setActivitySource(CommonUtils.emptyToString(activityInfo.getActivityName()));
						 custActivityInfo.setTotalAmount(new BigDecimal("50"));
						 custActivityInfo.setUsableAmount(new BigDecimal("50"));
						 custActivityInfo.setStartDate(activityInfo.getStartDate());
						 custActivityInfo.setExpireDate(activityInfo.getExpireDate());
						 custActivityInfo.setBasicModelProperty(savedEntity.getId(), true);
						 custActivityInfo.setTradeStatus(Constant.USER_ACTIVITY_TRADE_STATUS_01);
						 custActivityInfo.setRewardShape(Constant.REAWARD_SPREAD_02);
						 custActivityInfo.setTradeCode(flowNumberService.generateActivitySequnce());
						 custActivityInfoRepository.save(custActivityInfo);
						 return new ResultVo(true,"活动奖励设置成功");
				 }
			}
			return new ResultVo(false);
		}
	}

	public ResultVo queryInviterInfo(Map<String, Object> params) {
		Map<String, Object> result = Maps.newHashMap();
		String custId = (String) params.get("custId");
		CustInfoEntity custInfoEntity = custInfoRepository.findByCustId(custId);
		CustInfoEntity custInviteInfo = custInfoRepository.getInviteInfoByCustId(custId);
		if (custInfoEntity == null && custInviteInfo==null) {
			CustInfoEntity custInfo = custInfoRepository.findOne(custId);
			if (custInfo == null) {
				return new ResultVo(false, "客户信息不存在");
			}
			log.warn("相差:"+ getDiscrepantDays(custInfo.getCreateDate(), new Date())+ "天");
			// 60天内可补录邀请码
			if (getDiscrepantDays(custInfo.getCreateDate(), new Date()) <= 60) {
				result.put("flag", "能填");
			} else {
				result.put("flag", "不能填");
			}
		}
		//bao_t_cust_recommend_info表有数据就取该表的cust_id的客户信息,该表无数据就取bao_t_cust_info表的invietId客户信息
		if (custInfoEntity != null) {
			result.put("mobile", custInfoEntity.getMobile());
			result.put("inviteCode", custInfoEntity.getInviteCode());
			result.put("custName", custInfoEntity.getCustName());
			result.put("flag", "已填");
		} else {
			if (custInviteInfo != null) {
				result.put("mobile", custInviteInfo.getMobile());
				result.put("inviteCode", custInviteInfo.getInviteCode());
				result.put("custName", custInviteInfo.getCustName());
				result.put("flag", "已填");
			}
		}
		return new ResultVo(true, "查询成功", result);
	}

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo saveInviterInfo(Map<String, Object> params) {
		String custId = (String) params.get("custId");
		String inviteCode = (String) params.get("inviteCode");
		String mobile = (String) params.get("mobile");
		CustInfoEntity custInfoEntity = null;
		if (StringUtils.isEmpty(inviteCode) && StringUtils.isEmpty(mobile)) {
			return new ResultVo(false, "邀请码或者邀请人手机号请至少填写一种");
		}
		if (!StringUtils.isEmpty(inviteCode) && !StringUtils.isEmpty(mobile)) {
			custInfoEntity = custInfoRepository.findByMobileAndInviteCode(mobile, inviteCode);
			if (custInfoEntity == null) {
				return new ResultVo(false, "邀请码和邀请人手机号不匹配!");
			}
		} else {
			if (!StringUtils.isEmpty(inviteCode)) {
				custInfoEntity = custInfoRepository.findByInviteCode(inviteCode);
				if (custInfoEntity == null) {
					return new ResultVo(false, "邀请码不正确!");
				}
			}
			if (!StringUtils.isEmpty(mobile)) {
				custInfoEntity = custInfoRepository.findByMobile(mobile);
				if (custInfoEntity == null) {
					return new ResultVo(false, "邀请人手机号不正确!");
				}
			}
		}
		if (custId.equals(custInfoEntity.getInviteOriginId())) {
			return new ResultVo(false, "不能互相邀请!");
		}
		CustInfoEntity custInfo = custInfoRepository.findOne(custId);
		if (custInfo == null) {
			return new ResultVo(false, "客户信息不存在");
		}
		if (custInfo.getMobile().equals(custInfoEntity.getMobile())) {
			return new ResultVo(false, "不能邀请自己!");
		}
		CustRecommendInfoEntity  custRecommendInfo=custRecommendInfoRepository.findByQuiltCustIdAndRecordStatus(custId,Constant.VALID_STATUS_VALID);
		if(custRecommendInfo==null){
			if("0".equals(custInfo.getInviteOriginId())){
				String beforeInviteOriginId = custInfo.getInviteOriginId() == null ? "0": custInfo.getInviteOriginId();
				custInfo.setBasicModelProperty(custId, false);
				custInfo.setInviteOriginId(custInfoEntity.getId());
				custInfoRepository.save(custInfo);
				// 记录日志
				LogInfoEntity logInfoEntity = new LogInfoEntity();
				logInfoEntity.setBasicModelProperty(custId, true);
				logInfoEntity.setRelateType(Constant.TABLE_BAO_T_LOAN_INFO);
				logInfoEntity.setRelatePrimary(custInfoEntity.getId());
				logInfoEntity.setLogType("我的邀请人");
				logInfoEntity.setOperBeforeContent(beforeInviteOriginId);
				logInfoEntity.setOperAfterContent(custInfoEntity.getId());
				logInfoEntity.setOperDesc("");
				logInfoEntity.setOperPerson(custId);
				logInfoEntityRepository.save(logInfoEntity);
			}else{
				return new ResultVo(false, "该客户已被邀请过!");
			}
			String isRecommend = custInfoEntity.getIsRecommend();
			if ("是".equals(isRecommend)) {
				List<CustApplyInfoEntity> custApplyList = custApplyInfoRepository.findCustApplyInfoEntityCustomer(custInfoEntity.getId());
				if (custApplyList != null && custApplyList.size() > 0) {
					for (CustApplyInfoEntity c : custApplyList) {
						if (Constant.OPERATION_TYPE_23.equals(c.getApplyType())&& Constant.AUDIT_STATUS_PASS.equals(c.getApplyStatus())) {
							CustRecommendInfoEntity custRecommendInfoEntity = new CustRecommendInfoEntity();
							custRecommendInfoEntity.setCustId(custInfoEntity.getId());
							custRecommendInfoEntity.setQuiltCustId(custId);
							custRecommendInfoEntity.setApplyId(c.getId());
							custRecommendInfoEntity.setSource(Constant.CUST_RECOMMEND_SOURCE_REGISTER);
							custRecommendInfoEntity.setStartDate(new Timestamp(new Date().getTime()));
							custRecommendInfoEntity.setRecordStatus(Constant.VALID_STATUS_VALID);
							custRecommendInfoEntity.setBasicModelProperty(custId, true);
							custRecommendInfoRepository.save(custRecommendInfoEntity);
							// 记录日志
							LogInfoEntity logInfoEntity = new LogInfoEntity();
							logInfoEntity.setBasicModelProperty(custId, true);
							logInfoEntity.setRelateType(Constant.TABLE_BAO_T_CUST_RECOMMEND_INFO);
							logInfoEntity.setRelatePrimary(custInfoEntity.getId());
							logInfoEntity.setLogType("我的邀请人");
							logInfoEntity.setOperBeforeContent("");
							logInfoEntity.setOperAfterContent(custInfoEntity.getId());
							logInfoEntity.setOperDesc("");
							logInfoEntity.setOperPerson(custId);
							logInfoEntityRepository.save(logInfoEntity);
							break;
						}
					}
				}
			}
		}else{
			return new ResultVo(false, "该客户已被邀请过!");
		}
		return new ResultVo(true, "保存邀请人信息成功");
	}

	public static int getDiscrepantDays(Date dateStart, Date dateEnd) {
		return (int) ((dateEnd.getTime() - dateStart.getTime()) / 1000 / 60 / 60 / 24);
	}

	/**
	 * 活动13 2017 6月市场部活动
	 * 取得邀请人邀请码
	 * 活动时间：2017/06/15至2017/07/15】
	 * @author  lyy
	 * @date    2017年06月09日
	 * @throws SLException
	 */
	public ResultVo getInvestCodeForActivity201706(Map<String, Object> params) {
		ActivityInfoEntity activityInfoEntity = activityInfoRepository.findByIdAndActivityStatusAndDate(Constant.ACTIVITY_ID_REGIST_13, Constant.VALID_STATUS_VALID, DateUtils.getStartDate(new Date()));
		if(activityInfoEntity == null) { // 活动不存在或者已结束不予奖励
			return new ResultVo(false, "活动不存在或者已结束");
		}

		String inviteCode = (String) params.get("inviteCode");
		CustInfoEntity custInfo = custInfoRepository.findByInviteCode(inviteCode);
		if(custInfo == null){
			return new ResultVo(false, "该邀请码不存在");
		}

		String isRecommend = custInfo.getIsRecommend();
		String inviteCode1="";// 客户经理邀请码
		String inviteCode2="";// 邀请人邀请码

		if("是".equals(isRecommend)){
			inviteCode1 = custInfo.getInviteCode();
		} else {
			inviteCode2 = custInfo.getInviteCode();
//			CustInfoEntity cust = custInfo;
//			while(true){
//				if(StringUtils.isEmpty(cust.getInviteOriginId())){
//					break;
//				}
//				CustInfoEntity parent = custInfoRepository.findOne(cust.getInviteOriginId());
//				if(parent == null){
//					break;
//				} else {
//					if("是".equals(parent.getIsRecommend())){
//						inviteCode1 = parent.getInviteCode();
//						break;
//					} else {
//						cust = parent;
//						continue;
//					}
//				}
//			}
			// 根据关系表找 update 2017-06-23
			CustRecommendInfoEntity custRecommendInfo = custRecommendInfoRepository.findInfoCustRecommendByQuiltCustId(custInfo.getId());
			if(custRecommendInfo != null){
				CustInfoEntity mgrCust = custInfoRepository.findOne(custRecommendInfo.getCustId());
				inviteCode1 = mgrCust.getInviteCode();
			}
		}

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("inviteCode1", inviteCode1);
		data.put("inviteCode2", inviteCode2);
		data.put("activityCode", Constant.ACTIVITY_ID_REGIST_13);
		return new ResultVo(true, "查询邀请码", data);
	}

	/**
	 * 活动13 2017 6月市场部活动
	 * 【活动时间：2017/06/15至2017/07/15】
	 * @author  lyy
	 * @date    2017年06月09日
	 * @throws SLException
	 */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public void custActivityRecommend13() {
		log.info("6月活动执行开始：".concat(DateUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss")));
		ActivityInfoEntity activityInfoEntity = activityInfoRepository.findByIdAndActivityStatusAndDate(Constant.ACTIVITY_ID_REGIST_13, Constant.VALID_STATUS_VALID, DateUtils.getStartDate(new Date()));
		if(activityInfoEntity == null) { // 活动不存在或者已结束不予奖励
			return;
		}
		// 满足条件的投资人
		List<String> custIds = investInfoRepository.findByLoanIdFor201706(Constant.ACTIVITY_ID_13, DateUtils.formatDate(activityInfoEntity.getStartDate(), "yyyyMMdd"), DateUtils.formatDate(activityInfoEntity.getExpireDate(), "yyyyMMdd"));
		if(custIds == null || custIds.size() == 0){
			return;
		}
		String activityName = activityInfoEntity.getActivityName();
		String activityDesc = activityInfoEntity.getActivityDesc();
//		Date startDate = activityInfoEntity.getStartDate();
//		Date expireDate = activityInfoEntity.getExpireDate();
		// 奖励金额
		BigDecimal big5 = new BigDecimal("5");
		BigDecimal big10 = BigDecimal.TEN;
		String tradeCode = flowNumberService.generateTradeNumber();
		String tradeStatus = Constant.USER_ACTIVITY_TRADE_STATUS_01;

		List<CustActivityInfoEntity> caList = new ArrayList<CustActivityInfoEntity>(custIds.size());
		CustActivityInfoEntity custActivityInfoEntity;
		for (String custId : custIds) {
			CustInfoEntity custInfo = custInfoRepository.findOne(custId);
			if("是".equals(custInfo.getIsRecommend())) {
				// 客户经理投资不算奖励
				continue;
			}

			String invitedMgr = "";// 客户经理
			String invitedMan = "";// 推荐人

			// 根据关系表查 update 2017-06-23
			if(StringUtils.isEmpty(custInfo.getInviteOriginId())){
				// TODO
				saveLogInfo(custId, "邀请码为空");
				continue;
			}
			CustInfoEntity parent = custInfoRepository.findOne(custInfo.getInviteOriginId());
			if(parent == null){
				// TODO
				saveLogInfo(custId, "未找到上级推荐人");
				continue;
			}

			CustRecommendInfoEntity custRecommendInfo = custRecommendInfoRepository.findInfoCustRecommendByQuiltCustId(custId);
			if(custRecommendInfo == null){
				// TODO
				saveLogInfo(custId, "未找到客户经理");
				continue;
			} else if(parent.getId().equals(custRecommendInfo.getCustId())){// 客户经理就是推荐人
				invitedMgr = custRecommendInfo.getCustId();
			} else { // 推荐人不是客户经理
				invitedMgr = custRecommendInfo.getCustId(); // 客户经理
				invitedMan = parent.getId(); // 推荐人
			}

//			// 取直接邀请人，和客户经理
//			CustInfoEntity cust = custInfo;
//			for(int i=1;;i++) {
//				if(StringUtils.isEmpty(cust.getInviteOriginId())){
//					// TODO 记录错误日志 邀请码为空
//					saveLogInfo(custId, "邀请码为空");
//					break;
//				}
//				if(cust.getId().equals(cust.getInviteOriginId())){
//					// TODO 该客户自己邀请自己
//					saveLogInfo(custId, "该客户的邀请人是自己");
//					break;
//				}
//				CustInfoEntity parent = custInfoRepository.findOne(cust.getInviteOriginId());
//				if(parent == null){
//					// TODO 记录错误日志 未找到邀请的客户经理并且上级为空
//					saveLogInfo(custId, "未找到邀请的客户经理");
//					break;
//				} else {
//					// 知道到邀请经理就退出
//					if("是".equals(parent.getIsRecommend())){
//						invitedMgr = parent.getId();
//						break;
//					} else {
//						if(i==1){
//							invitedMan = parent.getId();
//						}
//						cust = parent;
//						continue;
//					}
//				}
//			}

			// 客户经理有，邀请人没有
			if(!StringUtils.isEmpty(invitedMgr) && StringUtils.isEmpty(invitedMan)){
				custActivityInfoEntity = new CustActivityInfoEntity();
				custActivityInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
				custActivityInfoEntity.setCustId(invitedMgr);
				custActivityInfoEntity.setActivityId(Constant.ACTIVITY_ID_REGIST_13);
				custActivityInfoEntity.setActivitySource(activityName);
				custActivityInfoEntity.setActivityDesc(activityDesc);
				custActivityInfoEntity.setTotalAmount(big10);
				custActivityInfoEntity.setUsableAmount(big10);
				custActivityInfoEntity.setTradeCode(tradeCode);
				custActivityInfoEntity.setTradeStatus(tradeStatus);
//				custActivityInfoEntity.setStartDate(startDate);
//				custActivityInfoEntity.setExpireDate(expireDate);
//				custActivityInfoEntity.setMemo(actibityMemo);
				custActivityInfoEntity.setRewardShape("现金");
				custActivityInfoEntity.setQuiltCustId(custId);
				// add
				caList.add(custActivityInfoEntity);
			} else if(!StringUtils.isEmpty(invitedMgr) && !StringUtils.isEmpty(invitedMan)) {
				// 客户经理有，邀请人也有
				custActivityInfoEntity = new CustActivityInfoEntity();
				custActivityInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
				custActivityInfoEntity.setCustId(invitedMgr);
				custActivityInfoEntity.setActivityId(Constant.ACTIVITY_ID_REGIST_13);
				custActivityInfoEntity.setActivitySource(activityName);
				custActivityInfoEntity.setActivityDesc(activityDesc);
				custActivityInfoEntity.setTotalAmount(big5);
				custActivityInfoEntity.setUsableAmount(big5);
				custActivityInfoEntity.setTradeCode(tradeCode);
				custActivityInfoEntity.setTradeStatus(tradeStatus);
//				custActivityInfoEntity.setStartDate(startDate);
//				custActivityInfoEntity.setExpireDate(expireDate);
//				custActivityInfoEntity.setMemo(actibityMemo);
				custActivityInfoEntity.setRewardShape("现金");
				custActivityInfoEntity.setQuiltCustId(custId);
				// add
				caList.add(custActivityInfoEntity);

				custActivityInfoEntity = new CustActivityInfoEntity();
				custActivityInfoEntity.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
				custActivityInfoEntity.setCustId(invitedMan);
				custActivityInfoEntity.setActivityId(Constant.ACTIVITY_ID_REGIST_13);
				custActivityInfoEntity.setActivitySource(activityName);
				custActivityInfoEntity.setActivityDesc(activityDesc);
				custActivityInfoEntity.setTotalAmount(big5);
				custActivityInfoEntity.setUsableAmount(big5);
				custActivityInfoEntity.setTradeCode(tradeCode);
				custActivityInfoEntity.setTradeStatus(tradeStatus);
//				custActivityInfoEntity.setStartDate(startDate);
//				custActivityInfoEntity.setExpireDate(expireDate);
//				custActivityInfoEntity.setMemo(actibityMemo);
				custActivityInfoEntity.setRewardShape("现金");
				custActivityInfoEntity.setQuiltCustId(custId);
				// add
				caList.add(custActivityInfoEntity);
			} else {
				// TODO
				saveLogInfo(custId, "奖励记录时出错，关联客户经理没找到!");
			}
		}
		custActivityInfoRepository.save(caList);
		log.info("6月活动执行结束：".concat(DateUtils.getCurrentDate("yyyy-MM-dd HH:mm:ss")));
		return;
	}

	private void saveLogInfo(String custId, String memo) {
		String reason = String.format("custId=%s, reason=%s", custId, memo);
		// 记录日志
		LogInfoEntity log = new LogInfoEntity();
		log.setBasicModelProperty(Constant.SYSTEM_USER_BACK, true);
		log.setRelateType(Constant.TABLE_BAO_T_ACTIVITY_INFO);
		log.setRelatePrimary(Constant.ACTIVITY_ID_REGIST_13);
		log.setLogType("6月市场部活动");
		log.setOperDesc(reason);
		log.setOperPerson(Constant.SYSTEM_USER_BACK);
		log.setMemo(reason);

		logInfoEntityRepository.save(log);
	}

	/**
	 * 市场部六月活动奖励查询
	 */
	public ResultVo queryActivityRecommend13(Map<String, Object> params){
		ActivityInfoEntity activityInfoEntity = activityInfoRepository.findOne(Constant.ACTIVITY_ID_REGIST_13);
		if(activityInfoEntity == null) { // 活动不存在或者已结束不予奖励
			return new ResultVo(false, "活动不存在或者已结束");
		}
		String custId = (String) params.get("custId");

		BigDecimal award = custActivityInfoRepositoryCustom.findCustAwardFor201706(custId);
		int count = custActivityInfoRepositoryCustom.findCustInvitedFor201706(custId);

		Map<String, Object> data = Maps.newHashMap();
		data.put("income", award);
		data.put("invitedCount", count);
		data.put(Constant.ACTIVITY_ID_13, DateUtils.truncateDate(new Date()).compareTo(activityInfoEntity.getExpireDate())>0?false:true);
		return new ResultVo(true, "查询活动参数", data);
	}

	/**
	 * 获取六月加息活动有效活动日期
	 *
	 * @author  guoyk
	 * @date    2017年6月8日 下午1:27:35
	 * @return
	 */
	@Cacheable(value="cache1", key="'queryJuneActivity'")
	@Override
	public String queryJuneActivity() {
		ActivityInfoEntity activityInfo = activityInfoRepository.findByActIdAndStatus(Constant.ACTIVITY_ID_REGIST_12,"有效");
		if (activityInfo==null) {
			return "2017-06-15,2017-07-15";
		}
		String startDate = DateUtils.formatDate(activityInfo.getStartDate(), "yyyy-MM-dd");
		String expireDate = DateUtils.formatDate(activityInfo.getExpireDate(), "yyyy-MM-dd");

		return String.format(startDate+"%s"+expireDate, ",");
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo chessMarketDepartmentActivityByRegister(Map<String, Object> params) {

		String activityCode = (String)params.get("activityCode");
		String custId =  (String)params.get("custId");
		boolean flag1 = StringUtils.isEmpty((String)params.get("inviteCode"));
		boolean flag2 = StringUtils.isEmpty((String)params.get("activityPlaceCity"));

			 //判断是否符合
			 ActivityInfoEntity activityInfo = activityInfoRepository.findByIdAndActivityStatusAndDate(activityCode, Constant.VALID_STATUS_VALID, new Date());
			 if (activityInfo!=null) {
				 if (!flag1) {//①　如果该用户在注册时填写了邀请码，记录该用户，并在活动结束后3个工作日内，给该用户发放红包（10个10元红包）；
					 for (int i = 0; i < 10; i++) {
						 CustActivityInfoEntity custActivityInfo = new CustActivityInfoEntity();
						 custActivityInfo.setActivityId(CommonUtils.emptyToString(activityInfo.getId()));
						 custActivityInfo.setCustId(custId);
						 custActivityInfo.setActivityDesc(Constant.ACTIVITY_DESC_03_02);
						 custActivityInfo.setActivitySource(CommonUtils.emptyToString(activityInfo.getActivityName()));
						 custActivityInfo.setTotalAmount(new BigDecimal("10"));
						 custActivityInfo.setUsableAmount(new BigDecimal("10"));
						 custActivityInfo.setStartDate(activityInfo.getStartDate());
						 custActivityInfo.setExpireDate(activityInfo.getExpireDate());
						 custActivityInfo.setBasicModelProperty(custId, true);
						 custActivityInfo.setTradeStatus(Constant.USER_ACTIVITY_TRADE_STATUS_01);
						 custActivityInfo.setRewardShape(Constant.REAWARD_SPREAD_02);
						 custActivityInfo.setTradeCode(flowNumberService.generateActivitySequnce());
						 custActivityInfo.setMemo("有邀请码(10个10元红包)");
						 custActivityInfoRepository.save(custActivityInfo);
					}
				 }else {//②　如果用户在注册时未填写邀请码，记录该用户，并在活动结束后3个工作日内，给该用户发放红包（5个10元红包）；
					 for (int i = 0; i < 5; i++) {
						 CustActivityInfoEntity custActivityInfo = new CustActivityInfoEntity();
						 custActivityInfo.setActivityId(CommonUtils.emptyToString(activityInfo.getId()));
						 custActivityInfo.setCustId(custId);
						 custActivityInfo.setActivityDesc(Constant.ACTIVITY_DESC_03_01);
						 custActivityInfo.setActivitySource(CommonUtils.emptyToString(activityInfo.getActivityName()));
						 custActivityInfo.setTotalAmount(new BigDecimal("10"));
						 custActivityInfo.setUsableAmount(new BigDecimal("10"));
						 custActivityInfo.setStartDate(activityInfo.getStartDate());
						 custActivityInfo.setExpireDate(activityInfo.getExpireDate());
						 custActivityInfo.setBasicModelProperty(custId, true);
						 custActivityInfo.setTradeStatus(Constant.USER_ACTIVITY_TRADE_STATUS_01);
						 custActivityInfo.setRewardShape(Constant.REAWARD_SPREAD_02);
						 custActivityInfo.setTradeCode(flowNumberService.generateActivitySequnce());
						 custActivityInfo.setMemo("无邀请码(5个10元红包)");
						 custActivityInfoRepository.save(custActivityInfo);
					}
				 }
				 //如果传来的城市信息不为空，存储城市信息
				 if (!flag2) {
					CustInfoEntity custInfoEntity = custInfoRepository.findOne(custId);
					custInfoEntity.setActivityPlaceCity((String)params.get("activityPlaceCity"));
				}
				 return new ResultVo(true,"活动奖励设置成功");
			 }
		return new ResultVo(false);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo chessMarketDepartmentActivityByLogin(Map<String, Object> params) {

		String custId = (String)params.get("custId");
		String activityCode = (String)params.get("activityCode");
		 //保存活动信息
		if (!org.springframework.util.StringUtils.isEmpty(activityCode)) {
			//先查询该活动是否有效
			ActivityInfoEntity activityInfo = activityInfoRepository.findByIdAndActivityStatusAndDate(activityCode, Constant.VALID_STATUS_VALID, new Date());
			if (activityInfo!=null) {
				//再判断改用户是否已经参与活动
				CustActivityInfoEntity custActivityInfoEntity = custActivityInfoRepository.findByActivityIdAndCustId(activityCode,custId);
				if (custActivityInfoEntity == null) {
					//5个红包循环插入5条记录
					for (int i = 0; i < 5; i++) {
						CustActivityInfoEntity custActivityInfo = new CustActivityInfoEntity();
						custActivityInfo.setActivityId(CommonUtils.emptyToString(activityInfo.getId()));
						custActivityInfo.setCustId(custId);
						custActivityInfo.setActivityDesc(Constant.ACTIVITY_DESC_04);
						custActivityInfo.setActivitySource(CommonUtils.emptyToString(activityInfo.getActivityName()));
						custActivityInfo.setTotalAmount(new BigDecimal("10"));
						custActivityInfo.setUsableAmount(new BigDecimal("10"));
						custActivityInfo.setStartDate(activityInfo.getStartDate());
						custActivityInfo.setExpireDate(activityInfo.getExpireDate());
						custActivityInfo.setBasicModelProperty(custId, true);
						custActivityInfo.setTradeStatus(Constant.USER_ACTIVITY_TRADE_STATUS_01);
						custActivityInfo.setRewardShape(Constant.REAWARD_SPREAD_02);
						custActivityInfo.setTradeCode(flowNumberService.generateActivitySequnce());
						custActivityInfo.setMemo("5个10元红包");
						custActivityInfoRepository.save(custActivityInfo);
					}
					return new ResultVo(true,"活动奖励设置成功");
				}
			}
		}
		return new ResultVo(false,"活动奖励设置失败");

	}
	public ResultVo queryActivityRecommend(Map<String, Object> params) {
		Map<String, Object> data = Maps.newHashMap();
		Map<String, Object> paramValue = Maps.newHashMap();
		String activityId = (String) params.get("activityId");
		BigDecimal award = BigDecimal.ZERO;
		BigDecimal activityAwardFor13 = BigDecimal.ZERO;
		BigDecimal activityAwardFor17 = BigDecimal.ZERO;
		if (StringUtils.isNotEmpty(activityId)) {
			ActivityInfoEntity activityInfo = activityInfoRepository.findByActIdAndStatus(activityId,Constant.VALID_STATUS_VALID);
			if (activityInfo == null) { //活动不存在或者已结束不予奖励
				return new ResultVo(false, "活动不存在或者已结束");
			}
			data.put("activityContent", activityInfo.getActivityContent());
		} else {
			List<ActivityInfoEntity> activityList = custActivityInfoRepositoryCustom.getListActivityInfo();
			int invitedCount = custActivityInfoRepositoryCustom.getCountByInviteOriginId(params);
			for (ActivityInfoEntity activityInfo : activityList) {
				if (activityInfo == null) { //活动不存在或者已结束不予奖励
					return new ResultVo(false, "活动不存在或者已结束");
				}
				paramValue.clear();
				paramValue.put("custId", params.get("custId"));
				if (Constant.ACTIVITY_ID_REGIST_13.equals(activityInfo.getId())) {
					paramValue.put("activityId", activityInfo.getId());
					 activityAwardFor13 = custActivityInfoRepositoryCustom.findCustAwardAmount(paramValue);
				}
				if (Constant.ACTIVITY_ID_REGIST_17.equals(activityInfo.getId())) {
//					paramValue.put("activityId", "");
					paramValue.put("startDate", activityInfo.getStartDate());
					paramValue.put("endDate", activityInfo.getExpireDate());
					List<Map<String, Object>> amount= custActivityInfoRepositoryCustom.findTotalInvestAmountAndTotalYearAmount(paramValue);
					activityAwardFor17=new BigDecimal(amount.get(0).get("totalMyAward").toString());
				}
					award = ArithUtil.add(activityAwardFor13,activityAwardFor17);
			}
			data.put("invitedCount", invitedCount);// 邀请人数是查询邀请的所有人
			data.put("income", award);// 奖励金额只算6月市场部活动和好友投资返现的奖励
			data.put("activityList", activityList);
		}
		return new ResultVo(true, "查询活动参数", data);

	}

	@Override
	public ResultVo queryCustExpLoan(Map<String, Object> params) {
		return this.queryCustCouponInfo(params);
	}

	@Override
	public ResultVo queryCustCouponInfo(Map<String, Object> params) {

		if(params.get("custId") == null) {
			return new ResultVo(false,"custId不能为空");
		}

		if(params.get("type") == null) {
			return new ResultVo(false,"type不能为空");
		}
		return custActivityInfoRepositoryCustom.findExpCouponList(params);
	}

	@Override
	public ResultVo queryExpLoanProfit(Map<String, Object> params) {

		LoanInfoEntity loanInfoEntity = null;
		try {
			loanInfoEntity = loanInfoRepository.findByNewerFlagAndLoanStatus();
			if(loanInfoEntity == null) {
				return new ResultVo(false, "没有体验标，请检查");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResultVo(false, "体验标有多条数据");
		}
		String id = (String)params.get("id");
		if(StringUtils.isBlank(id)) {
			return new ResultVo(false,"体验金id不能为空");
		}
		CustActivityInfoEntity custInfo = custActivityInfoRepository.findById(id);

		if(custInfo == null) {
		    return new ResultVo(false,"没有此体验金券");
        }

		BigDecimal yearIrr = loanInfoEntity.getLoanDetailInfoEntity().getYearIrr();
		long days = loanInfoEntity.getLoanTerm();
		BigDecimal usableAmount = custInfo.getUsableAmount();

		//计算出体验金的收益
		BigDecimal dayRate = ArithUtil.div(yearIrr, new BigDecimal(360));
		BigDecimal profit = (dayRate.multiply(new BigDecimal(days))).multiply(usableAmount).setScale(2, BigDecimal.ROUND_DOWN);

		Map<String, Object> map = new HashMap<>();
		map.put("expAmount",usableAmount);
		map.put("profit", profit);

		return new ResultVo(true, "成功返回", map);
	}

	@Override
	public ResultVo queryExpLoanDetail(Map<String, Object> params) {

	    if(params.get("loanId") == null) {
	        return new ResultVo(false,"体验标id不能为空");
        }
		String loanId = (String)params.get("loanId");

        String custId = ( params.get("custId") == null ? "" : params.get("custId").toString());

		String investId = (params.get("investId") == null ? "" : params.get("investId").toString());

		return custActivityInfoRepositoryCustom.findExpLoadDetail(loanId, investId, custId);
	}

	@Override
	@Transactional(rollbackFor = SLException.class)//事务处理
	public ResultVo buyExpLoan(Map<String, Object> params) {

		String custId = CommonUtils.emptyToString(params.get("custId"));//用户id
		String expLoanId = CommonUtils.emptyToString(params.get("expLoanId"));//体验金id
		BigDecimal expAmount = CommonUtils.emptyToDecimal(params.get("expAmount"));//体验金额
		BigDecimal profit = CommonUtils.emptyToDecimal(params.get("profit"));//预期收益
		String appSource = CommonUtils.emptyToString(params.get("appSource"));//投资来源 手机端、PC端
        //String tradePassword = CommonUtils.emptyToString(params.get("tradePassword"));//交易密码
		params.put("id", expLoanId);

		ResultVo result = queryExpLoanProfit(params);
		HashMap<String, Object> map = (HashMap<String, Object>) result.getValue("data");
		if (null == map) {
			return result;
		}

		/*CustInfoEntity custInfoEntity = custInfoRepository.findById(custId);

		if (!tradePassword.equals(custInfoEntity.getTradePassword())) {
			return new ResultVo(false, "交易密码错误");
		}*/

		BigDecimal myProfit = (BigDecimal) map.get("profit");
		BigDecimal myExpAmount = (BigDecimal) map.get("expAmount");
		if(myProfit.compareTo(profit) != 0) {
			return new ResultVo(false, "预算收益不一致，请核对");
		}
		if(myExpAmount.compareTo(expAmount) != 0) {
			return new ResultVo(false, "体验金额不一致，请核对");
		}

		//修改体验券的状态为:"全部使用"
		CustActivityInfoEntity custActivityInfoEntity = custActivityInfoRepository.findById(expLoanId);
		if(Constant.USER_ACTIVITY_TRADE_STATUS_03.equals(custActivityInfoEntity.getTradeStatus())) {
			return new ResultVo(false, "该体验金券已经使用");
		}
		custActivityInfoEntity.setTradeStatus(Constant.USER_ACTIVITY_TRADE_STATUS_03);
		custActivityInfoRepository.save(custActivityInfoEntity);

		//todo 体验金新建一笔投资

		LoanInfoEntity loanInfoEntity = loanInfoRepository.findByNewerFlagAndLoanStatus();
		if (loanInfoEntity == null) {
			return new ResultVo(false, "无可用体验标");
		}
		//1 投资表
		InvestInfoEntity investInfoEntity = new InvestInfoEntity();
		investInfoEntity.setCustId(custId);
		investInfoEntity.setInvestAmount(expAmount);
		investInfoEntity.setInvestStatus(Constant.INVEST_STATUS_EARN); //收益中
		investInfoEntity.setInvestMode(Constant.INVEST_METHOD_JOIN); //加入
		investInfoEntity.setInvestDate(DateUtils.formatDate(new Date(), "yyyyMMdd"));
		investInfoEntity.setExpireDate(DateUtils.formatDate(DateUtils.getAfterDay(new Date(), loanInfoEntity.getLoanTerm().intValue()), "yyyyMMdd"));//到期时间
		investInfoEntity.setInvestRedPacket(profit);
		investInfoEntity.setRedPacketType(Constant.EXPERIENCE_AMOUNT);
		investInfoEntity.setLoanId(loanInfoEntity.getId());
		investInfoEntity.setBasicModelProperty(custId, true);
		investInfoEntity.setCustActivityId(custActivityInfoEntity.getId());
		investInfoEntity = investInfoRepository.save(investInfoEntity);

		//2 投资详情
		InvestDetailInfoEntity investDetailInfoEntity = new InvestDetailInfoEntity();
		investDetailInfoEntity.setInvestId(investInfoEntity.getId());
		investDetailInfoEntity.setTradeNo(numberService.generateLoanContractNumber());
		investDetailInfoEntity.setInvestAmount(expAmount);
		investDetailInfoEntity.setInvestSource(appSource);
		investDetailInfoEntity.setBasicModelProperty(custId, true);
		investDetailInfoRepository.save(investDetailInfoEntity);

		//3 项目投资表

		ProjectInvestInfoEntity projectInvestInfo = projectInvestInfoRepository.findByLoanId(loanInfoEntity.getId());
		ResultVo loanDetail = custActivityInfoRepositoryCustom.findExpLoadDetail(loanInfoEntity.getId(), "", "");
		HashMap<String, Object> mapDetail = (HashMap<String, Object>) loanDetail.getValue("data");
		int investPeoples = Integer.valueOf(mapDetail.get("investers").toString());//投资人数

		if (projectInvestInfo == null) {

			ProjectInvestInfoEntity projectInvestInfoEntity = new ProjectInvestInfoEntity();
			projectInvestInfoEntity.setAlreadyInvestPeoples(1);
			projectInvestInfoEntity.setLoanId(investInfoEntity.getLoanId());
			projectInvestInfoEntity.setRecordStatus(Constant.VALID_STATUS_VALID);
			projectInvestInfoEntity.setBasicModelProperty(custId, true);
			projectInvestInfoRepository.save(projectInvestInfoEntity);

		} else {
			List<InvestInfoEntity> list = investInfoRepositoryCustom.queryCustIdInInvestTable(loanInfoEntity.getId(), custId);
			if (list.size() == 0) {
				projectInvestInfo.setAlreadyInvestPeoples(investPeoples + 1);
				projectInvestInfo.setBasicModelProperty(custId, false);
				projectInvestInfoRepository.save(projectInvestInfo);
			}
		}


		//4 账户流水
		String reqeustNo = numberService.generateTradeBatchNumber();
		AccountInfoEntity accountInfo = accountInfoRepository.findByCustId(custId); //客户账户表
		AccountFlowInfoEntity accountFlowInfoEntity = new AccountFlowInfoEntity();
		accountFlowInfoEntity.setCustId(custId);
		accountFlowInfoEntity.setAccountId(accountInfo.getCustId());
		accountFlowInfoEntity.setAccountType("");
		accountFlowInfoEntity.setTradeType(Constant.OPERATION_TYPE_90);
		accountFlowInfoEntity.setFlowType(SubjectConstant.SUBJECT_TYPE_EXPERIENCE_AMOUNT);
		accountFlowInfoEntity.setRequestNo(reqeustNo);
		accountFlowInfoEntity.setTradeNo(numberService.generateTradeBatchNumber());
		accountFlowInfoEntity.setBankrollFlowDirection(Constant.BANKROLL_FLOW_DIRECTION_IN);
		accountFlowInfoEntity.setTradeAmount(profit);
		accountFlowInfoEntity.setTradeDate(new Timestamp(System.currentTimeMillis()));
		accountFlowInfoEntity.setAccountTotalAmount(accountInfo.getAccountTotalAmount());
		accountFlowInfoEntity.setAccountFreezeAmount(accountInfo.getAccountFreezeAmount());
		accountFlowInfoEntity.setAccountAvailable(accountInfo.getAccountAvailableAmount());
		accountFlowInfoEntity.setCashAmount(BigDecimal.ZERO);
		accountFlowInfoEntity.setRecordStatus(Constant.VALID_STATUS_VALID);
		//关联投资表
		accountFlowInfoEntity.setRelateType("BAO_T_INVEST_INFO");
		accountFlowInfoEntity.setRelatePrimary(investInfoEntity.getId());//投资表id
		accountFlowInfoEntity.setAccountActivityAmount(accountInfo.getAccountActivityAmount());
		accountFlowInfoEntity.setBasicModelProperty(custId, true);
		accountFlowInfoEntity.setMemo(Constant.OPERATION_TYPE_90);
		accountFlowInfoRepository.save(accountFlowInfoEntity);


		// 5 记录设备信息(体验标)
		Map<String, Object> deviceParams = Maps.newConcurrentMap();
		deviceParams.putAll(params);
		deviceParams.put("relateType", Constant.TABLE_BAO_T_INVEST_INFO);
		deviceParams.put("relatePrimary", investInfoEntity.getId());
		deviceParams.put("tradeType", Constant.OPERATION_TYPE_90);
		deviceParams.put("userId", custId);
		deviceService.saveUserDevice(deviceParams);

		// 6 记录日志
		LogInfoEntity logInfoEntity = new LogInfoEntity();
		logInfoEntity.setRelateType(Constant.TABLE_BAO_T_INVEST_INFO);
		logInfoEntity.setRelatePrimary(investInfoEntity.getId());
		logInfoEntity.setLogType("体验金投资");
		logInfoEntity.setOperDesc("");
		logInfoEntity.setOperPerson(custId);
		logInfoEntity.setOperIpaddress("");
		logInfoEntity.setMemo(String.format("%s购买体验标，投资金额%s", "", expAmount.toString()));
		logInfoEntity.setBasicModelProperty(custId, true);
		logInfoEntityRepository.save(logInfoEntity);

		return new ResultVo(true, "购买成功");
	}

	@Override
	@Transactional(rollbackFor = SLException.class)
    public ResultVo expLoanTimeJob() {

	    //1、update已过期的体验金券
		List<CustActivityInfoEntity> list= custActivityInfoRepositoryCustom.findNotUsedAndUsedExpireExpLoan();
		for (CustActivityInfoEntity custActivityInfoEntity : list){
			custActivityInfoEntity.setTradeStatus(Constant.USER_ACTIVITY_TRADE_STATUS_04);
			custActivityInfoRepository.save(custActivityInfoEntity);
		}

		//2、到期后，把收益金额插入账户活动金额
		String currentDate = DateUtils.formatDate(new Date(), "yyyyMMdd");
		//LoanInfoEntity loanInfoEntity = loanInfoRepository.findByNewerFlagAndLoanStatus();//获取已启用的体验标
		List<InvestInfoEntity> investInfoList = investInfoRepository.findByRedPacketTypeAndInvestStatusAndExpireDate(Constant.EXPERIENCE_AMOUNT,
				Constant.INVEST_STATUS_EARN, currentDate);//过滤已到期的投资记录
		for(InvestInfoEntity investInfo : investInfoList) {

			//到期后，更改投资表记录为已过期
			investInfo.setInvestStatus(Constant.INVEST_STATUS_END);
			investInfoRepository.save(investInfo);

			//通过investId查询账户流水
			AccountFlowInfoEntity accountFlowInfoEntity = accountFlowInfoRepository.findByRelatePrimary(investInfo.getId());
			BigDecimal tradeAmount = accountFlowInfoEntity.getTradeAmount();

			//通过custId查询账户表
			String custId = investInfo.getCustId();
			AccountInfoEntity accountInfoEntity = accountInfoRepository.findByCustId(investInfo.getCustId());
			if(accountInfoEntity == null) {
				return new ResultVo(false, "该用户不存在");
			}
			//更新活动金额 （原有的活动金额+当前收益的）
			BigDecimal accountAmount = (accountInfoEntity.getAccountActivityAmount() == null ? BigDecimal.ZERO : accountInfoEntity.getAccountActivityAmount());
			BigDecimal totalAmount = accountAmount.add(tradeAmount);
			accountInfoEntity.setAccountActivityAmount(totalAmount);
			accountInfoRepository.save(accountInfoEntity);

			//3、insert账户流水表
			String reqeustNo = numberService.generateTradeBatchNumber();
			AccountInfoEntity accountInfo = accountInfoRepository.findByCustId(custId); //客户账户表
			AccountFlowInfoEntity accountFlowInfo = new AccountFlowInfoEntity();
			accountFlowInfo.setCustId(custId);
			accountFlowInfo.setAccountId(accountInfo.getId());
			accountFlowInfo.setAccountType("");
			accountFlowInfo.setTradeType(Constant.OPERATION_TYPE_90);
			accountFlowInfo.setFlowType(SubjectConstant.SUBJECT_TYPE_EXPERIENCE_AMOUNT);
			accountFlowInfo.setRequestNo(reqeustNo);
			accountFlowInfo.setTradeNo(numberService.generateTradeBatchNumber());
			accountFlowInfo.setBankrollFlowDirection(Constant.BANKROLL_FLOW_DIRECTION_IN);
			accountFlowInfo.setTradeAmount(tradeAmount);
			accountFlowInfo.setTradeDate(new Timestamp(System.currentTimeMillis()));
			accountFlowInfo.setAccountTotalAmount(accountInfo.getAccountTotalAmount());
			accountFlowInfo.setAccountFreezeAmount(accountInfo.getAccountFreezeAmount());
			accountFlowInfo.setAccountAvailable(accountInfo.getAccountAvailableAmount());
			accountFlowInfo.setCashAmount(BigDecimal.ZERO);
			accountFlowInfo.setRecordStatus(Constant.VALID_STATUS_VALID);
			//关联投资表
			accountFlowInfo.setRelateType("BAO_T_ACCOUNT_INFO");
			accountFlowInfo.setRelatePrimary(accountInfo.getId());
			accountFlowInfo.setAccountActivityAmount(accountInfo.getAccountActivityAmount());
			accountFlowInfo.setBasicModelProperty(custId, true);
			accountFlowInfo.setMemo(Constant.OPERATION_TYPE_90);
			accountFlowInfoRepository.save(accountFlowInfo);

		}
        return new ResultVo(true, "体验标定时任务执行成功");
    }
}
