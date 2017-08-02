/** 
 * @(#)CustActivityService.java 1.0.0 2015年04月23日 下午13:56:25  
 *  
 * Copyright © 2014 善林金融.  All rights reserved.  
 */
package com.slfinance.shanlincaifu.service;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;
import java.util.List;
import java.util.Map;

/**
 * 
 * 善林财富活动服务接口
 * 
 * @author caoyi
 * @version $Revision:1.0.0, $Date: 2015年05月19日 上午11:49:25 $
 */
public interface CustActivityInfoService {

	/**
	 * 获取我的推荐记录
	 * 
	 * @author caoyi
	 * @date 2015年05月19日 上午11:49:25 
	 * @param param
	 *       <tt>start：int:分页起始页</tt><br>
	 *       <tt>length：int:每页长度</tt><br>
	 *       <tt>custId, String:客户ID</tt><br>
	 *       <tt>spreadLevel： String:层级(可以为空)</tt><br>
	 *       <tt>registDateBegin： String:注册开始日期(可以为空)</tt><br>
	 *       <tt>registDateEnd： String:注册结束日期(可以为空)</tt><br>
	 *       <tt>realName： String:是否实名认证(可以为空)</tt><br>
	 *       <tt>invest： String:是否投资(可以为空)</tt><br>
	 *       <tt>tradeStatus： String:是否核算(可以为空)</tt><br>
	 * <br>
	 * @return iTotalDisplayRecords: 总条数 data:List<Map<String, object>>
	 *         Map<String, object>: <tt>orderNo： String:序号</tt><br>
	 *         <tt>loginName： String:推荐用户</tt><br>
	 *         <tt>registDate： String:注册日期</tt><br>
	 *         <tt>realName： String:实名认证</tt><br>
	 *         <tt>invest： String:是否投资</tt><br>
	 *         <tt>spreadLevel： String:层级</tt><br>
	 *         <tt>awardAmount： BigDecimal:奖励金额</tt><br>
	 *         <tt>experienceAmount： BigDecimal:体验金奖励</tt><br>       
	 *         <tt>tradeStatus： BigDecimal:是否核算</tt><br>
	 *         <tt>count：邀请人数: int</tt><br>
	 *         <tt>myAward：BigDecimal:我的奖励金</tt><br>
	 *         <tt>totalInvestAmount：BigDecimal:累计出借金额</tt><br>
	 *         <tt>totalYearInvestAmount：BigDecimal:合计年化出借金额</tt><br>
	 */
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数"), 
			@Rule(name = "custId", required = true, requiredMessage = "客户主键不能为空!") })
	public Map<String, Object> findCustRecommendList(Map<String, Object> param) throws SLException;

	/**
	 * 获取我的体验金记录
	 * 
	 * @author caoyi
	 * @date 2015年05月19日 下午14:36:25 
	 * @param param
	 *       <tt>start：int:分页起始页</tt><br>
	 *       <tt>length：int:每页长度</tt><br>
	 *       <tt>custId： String:客户ID</tt><br>
	 *       <tt>receiveDateBegin： String:领取日期开始(可以为空)</tt><br>
	 *       <tt>receiveDateEnd： String:领取日期结束(可以为空)</tt><br>
	 *       <tt>source： String:来源(可以为空)</tt><br>
	 *       <tt>tradeStatus： String:状态(可以为空)</tt><br>
	 * <br>
	 * @return iTotalDisplayRecords: 总条数 data:List<Map<String, object>>
	 *         Map<String, object>: <tt>orderNo： String:序号</tt><br>
	 *         <tt>receiveAmount： BigDecimal:领取金额</tt><br>
	 *         <tt>usableAmount： BigDecimal:使用金额</tt><br>
	 *         <tt>source： String:来源</tt><br>
	 *         <tt>tradeStatus： String:状态</tt><br>
	 *         <tt>receiveDate： String:获取日期</tt><br>

	 */
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数"), 
			@Rule(name = "custId", required = true, requiredMessage = "客户主键不能为空!") })
	public Map<String, Object> findCustExperienceList(Map<String, Object> param) throws SLException;
	
	/**
	 * 体验金管理
	 * 
	 * <tt>search_custName用户名 </tt><br>
	 * <tt>search_realName 真实姓名 </tt><br>
	 * <tt>search_credentialsCode 证件号码</tt><br>
	 * 证件号码
	 * 
	 * 返回参数<tt> 体验金总额 experienceGoldCount </tt><br>
	 * <tt>可用体验金 experienceGoldApprove<tt> 已用体验金
	 * experienceGoldUsed </tt><br>
	 * <tt>体验金收益 experienceGoldIncome</tt><br>
	 * 
	 * @param param
	 * @return
	 */
	public Map<String, Object> findExperienceGold(Map<String, Object> param);

	/**
	 * 统计信息
	 * @param param
	 * @return
	 */
	public Map<String, Object> findExperienceGoldAudit(Map<String, Object> param);

	/**
	 * 体验金总奖励
	 * @param param
	 * @return
	 */
	public Map<String, Object> findExperienceGoldById(Map<String, Object> param);

	/**
	 * 获取体验金信息
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> findExperienceGoldDetail(Map<String, Object> param);

	/**
	 * 获取奖励信息
	 * @param param
	 * @return
	 */
	public Map<String, Object> findRewardById(Map<String, Object> param);
	
	
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
	public Map<String, Object> findCustCommissionInfo(Map<String, Object> param) throws SLException; 
	
	
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
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数"), 
			@Rule(name = "custId", required = true, requiredMessage = "客户主键不能为空!") })
	public Map<String, Object> findCustCommissionList(Map<String, Object> param) throws SLException;
	
	
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
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数"), 
			@Rule(name = "commissionId", required = true, requiredMessage = "佣金记录ID不能为空!") })
	public Map<String, Object> findCustCommissionDetailList(Map<String, Object> param) throws SLException;

//	/**
//	 * 获取体验金
//	 * 
//	 * @param map
//	 * @return
//	 * @throws SLException
//	 */
//	public ResultVo receiveExperience(Map<String, Object> map) throws SLException;
	
	/**
	 * 客户活动奖励
	 *
	 * @author  wangjf
	 * @date    2015年11月21日 下午2:20:47
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo custActivityRecommend(Map<String, Object> params);
	
	/**
	 * 客户投资满额活动奖励by登陆
	 *
	 * @author  guoyk
	 * @date    2017年4月27日 下午2:10:47
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo custActivityInvestRecommendByLogin(Map<String, Object> params);
	
	/**
	 * 客户投资满额活动奖励by注册
	 *
	 * @author  guoyk
	 * @date    2017年4月27日 下午2:10:47
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo custActivityInvestRecommendByRegister(Map<String, Object> params);
	
	/**
	 * 查询邀请人信息(邀请人管理)
	 * 
	 * @author fengyl
	 * @date 2017年5月24日
	 * @param param
	 *            <tt>custId, String:客户ID</tt><br>
	 * <br>
	 * @return Map<String, object>： <tt>custName： String:  好友名称</tt><br>
	 *         <tt>inviteCode： String:好友邀请码</tt><br>
	 *         <tt>mobile： String: 邀请人手机号码</tt><br>
	 */
	@Rules(rules = { 
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空!") })
	public ResultVo queryInviterInfo(Map<String, Object> params);
	/**
	 * 保存邀请人信息(邀请人管理)
	 *
	 * @author  fengyl
	 * @date    2017年5月24日 
	 * @param params
	 * @return
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空!") })
	public ResultVo saveInviterInfo(Map<String, Object> params);
	
	/**
	 * inviteCode
	 */
	@Rules(rules = { 
			@Rule(name = "inviteCode", required = true, requiredMessage = "邀请编号不能为空!") 
	})
	public ResultVo getInvestCodeForActivity201706(Map<String, Object> params);
	
	
	@Rules(rules = { 
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空!") 
	})
	public ResultVo queryActivityRecommend13(Map<String, Object> params);
	
	/**
	 * 获取六月加息活动有效活动日期
	 *
	 * @author  guoyk
	 * @date    2017年6月8日 下午1:27:35
	 * @return
	 */
	public String queryJuneActivity();
	
	/**
	 * 市场部棋王争霸赛by注册
	 *
	 * @author  guoyk
	 * @date    2017年6月22日 上午11:42:47
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo chessMarketDepartmentActivityByRegister(Map<String, Object> params);
	/**
	 * 市场部棋王争霸赛by登录
	 *
	 * @author  guoyk
	 * @date    2017年6月22日 上午11:42:48
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo chessMarketDepartmentActivityByLogin(Map<String, Object> params);

	/**
	 * 红包加息体验金--注册即送.
	 * @param params
	 *        客户编号 custId
	 *        活动标识 activityId
	 * @return 红包赠送是否成功标识
	 */
	ResultVo custActivityRecommend15(Map<String, Object> params);

	/**
	 * 公司红包账户支出流水任务
	 */
  void redEnvelopeFlowJob() throws SLException;

	/**
	 * 查询好友投资返现活动信息
	 *
	 * @author fengyl
	 * @date 2017年6月29日
	 * @param param
	 *            <tt>custId：String:客户ID</tt><br>
	 *            <tt>activityId：String:活动id(可为空)</tt><br>
	 * <br>
	 * @return Map<String, object>： <tt>custName： String:好友名称</tt><br>
	 *         <tt>activityName： String:活动编号</tt><br>
	 *         <tt> id： String: 活动id</tt><br>
	 *         <tt> activityContent： String: 活动规则(根据活动id查询)</tt><br>
	 *
	 */
	@Rules(rules = {
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空!")
	})
	public ResultVo queryActivityRecommend(Map<String, Object> params);


	/**
	 * 条件查询用户的加息券（未使用、已使用、已过期）
	 * @param params
	 * @return
	 */
	ResultVo queryCustCouponInfo(Map<String, Object> params);

	/**
	 *条件查询用户的体验金（未使用、已使用、已过期）
	 * @param params
	 * @return
	 */
	ResultVo queryCustExpLoan(Map<String, Object> params);

	/**
	 * 查询体验金收益
	 * @param params
	 * @return
	 */
	ResultVo queryExpLoanProfit(Map<String, Object> params);

	/**
	 *查询体验投资详情
	 * @param params
	 * @return
	 */
	ResultVo queryExpLoanDetail(Map<String, Object> params);

	/**
	 * 购买体验金
	 * @param params
	 * @return
	 */
	ResultVo buyExpLoan(Map<String, Object> params);

	/**
	 * 体验金定时任务
	 */
	ResultVo expLoanTimeJob();
}
