/** 
 * @(#)ProjectRepaymentService.java 1.0.0 2016年1月6日 下午3:52:47  
 *  
 * Copyright © 2016 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service;

import java.math.BigDecimal;
import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.ProjectInfoEntity;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;

/**   
 * 项目还款服务接口
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2016年1月6日 下午3:52:47 $ 
 */
public interface ProjectRepaymentService {

	/**
	 * 创建还款计划 
	 *
	 * @author  wangjf
	 * @date    2016年1月6日 下午4:14:37
	 * @param projectInfoEntity
	 * @return
	 */
	public ResultVo createRepaymentPlan(ProjectInfoEntity projectInfoEntity, BigDecimal projectAmount) throws SLException;
	
	/**
	 * 近期应还数据列表
	 *
	 * @author  wangjf
	 * @date    2016年1月6日 下午6:25:44
	 * @param params
     *      <tt>start          :String:起始值</tt><br>
     *      <tt>length         :String:长度</tt><br>
     *      <tt>projectType    :String:资产类型（可选）</tt><br>
     *      <tt>companyName    :String:公司名称（可选）</tt><br>
     *      <tt>projectNo      :String:项目编号（可选）</tt><br>
     *      <tt>beginExceptDate:String:起始应还日期（可选）</tt><br>
     *      <tt>endExceptDate  :String:结束应还日期（可选）</tt><br>
	 * @return
     *      ResultVo
     *      <tt>iTotalDisplayRecords  :String:记录总数</tt><br>
     *      <tt>data                  :String:List<Map<String, Object>></tt><br>
     *      <tt>replaymentPlanId      :String:还款计划ID</tt><br>
     *      <tt>projectId             :String:项目ID</tt><br>
     *      <tt>projectType           :String:资产类型</tt><br>
     *      <tt>projectNo             :String:项目编号</tt><br>
     *      <tt>companyName           :String:公司名称</tt><br>
     *      <tt>typeTerm              :String:项目期限</tt><br>
     *      <tt>currTerm              :String:当前期数</tt><br>
     *      <tt>repaymentTotalAmount  :String:应还金额</tt><br>
     *      <tt>termAlreadyRepayAmount:String:已还金额</tt><br>
     *      <tt>freezeRepayAmount     :String:冻结金额</tt><br>
     *      <tt>exceptDate            :String:应还日期</tt><br>
     *      <tt>isAmountFrozen        :String:财务是否还款</tt><br>
	 */
	public ResultVo queryLatestRepaymentList(Map<String, Object> params);
	
	/**
	 * 应还总额、已还总额、公司可用余额查询
	 *
	 * @author  wangjf
	 * @date    2016年1月6日 下午6:28:07
	 * @param params
     *      <tt>projectType    :String:资产类型（可选）</tt><br>
     *      <tt>companyName    :String:公司名称（可选）</tt><br>
     *      <tt>projectNo      :String:项目编号（可选）</tt><br>
     *      <tt>beginExceptDate:String:起始应还日期（可选）</tt><br>
     *      <tt>endExceptDate  :String:结束应还日期（可选）</tt><br>
	 * @return
     *      <tt>repaymentTotalAmount         :String:应还总额</tt><br>
     *      <tt>termAlreadyRepayAmount       :String:已还总额</tt><br>
     *      <tt>companyAccountAvailableAmount:String:公司可用余额</tt><br>
	 */
	public ResultVo queryLatesttRepaymentTotal(Map<String, Object> params);
	
	/**
	 * 批量还款
	 *
	 * @author  wangjf
	 * @date    2016年1月6日 下午6:29:10
	 * @param params
     *      <tt>List<Map<String, Object>>:String:还款列表</tt><br>
     *      <tt>replaymentPlanId         :String:还款计划ID</tt><br>
     *      <tt>userId:String:创建人ID</tt><br>
	 * @return
	 */
	@Rules(rules = { 
			@Rule(name = "userId", required = true, requiredMessage = "创建人不能为空!")
	})
	public ResultVo multiRepayment(Map<String, Object> params) throws SLException;
	
	/**
	 * 单笔还款
	 *
	 * @author  wangjf
	 * @date    2016年1月6日 下午6:31:46
	 * @param params
     *      <tt>replaymentPlanId:String:还款计划ID</tt><br>
     *      <tt>userId:String:创建人ID</tt><br>
	 * @return
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "replaymentPlanId", required = true, requiredMessage = "还款计划ID不能为空!"),
			@Rule(name = "userId", required = true, requiredMessage = "创建人不能为空!")
	})
	public ResultVo singleRepayment(Map<String, Object> params) throws SLException;
	
	/**
	 * 逾期中数据列表
	 *
	 * @author  wangjf
	 * @date    2016年1月6日 下午6:31:48
	 * @param params
     *      <tt>start      :String:起始值</tt><br>
     *      <tt>length     :String:长度</tt><br>
     *      <tt>projectType:String:资产类型（可选）</tt><br>
     *      <tt>companyName:String:公司名称（可选）</tt><br>
     *      <tt>projectNo  :String:项目编号（可选）</tt><br>
     *      <tt>payStatus  :String:垫付状态（可选）</tt><br> 
	 * @return
     *      ResultVo
     *      <tt>iTotalDisplayRecords          :String:记录总数</tt><br>
     *      <tt>data                          :String:List<Map<String, Object>></tt><br>
     *      <tt>replaymentPlanId              :String:还款计划ID</tt><br>
     *      <tt>projectId                     :String:项目ID</tt><br>
     *      <tt>projectType                   :String:资产类型</tt><br>
     *      <tt>projectNo                     :String:项目编号</tt><br>
     *      <tt>companyName                   :String:公司名称</tt><br>
     *      <tt>typeTerm                      :String:项目期限</tt><br>
     *      <tt>currTerm                      :String:当前期数</tt><br>
     *      <tt>repaymentTotalAmount          :String:应还金额</tt><br>
     *      <tt>termAlreadyRepayAmount        :String:已还金额</tt><br>
     *      <tt>freezeRepayAmount（公司分账户冻结价值字段）:String:冻结金额</tt><br>
     *      <tt>exceptDate                    :String:应还日期</tt><br>
     *      <tt>overdueExpense                :String:逾期费用</tt><br>
     *      <tt>overdueDays                   :String:逾期天数</tt><br>
     *      <tt>payStatus                     :String:垫付状态</tt><br>
	 */
	public ResultVo queryOverdueRepaymentList(Map<String, Object> params);
	
	/**
	 * 应还总额、已还总额、公司可用余额、逾期总额查询
	 *
	 * @author  wangjf
	 * @date    2016年1月6日 下午6:31:51
	 * @param params
     *      <tt>projectType:String:资产类型（可选）</tt><br>
     *      <tt>companyName:String:公司名称（可选）</tt><br>
     *      <tt>projectNo  :String:项目编号（可选）</tt><br>
     *      <tt>payStatus  :String:垫付状态（可选）</tt><br>
	 * @return
     *      <tt>repaymentTotalAmount         :String:应还总额</tt><br>
     *      <tt>termAlreadyRepayAmount       :String:已还总额</tt><br>
     *      <tt>companyAccountAvailableAmount:String:公司可用余额</tt><br>
     *      <tt>overdueExpense               :String:逾期费用总和</tt><br>
	 */
	public ResultVo queryOverdueRepaymentTotal(Map<String, Object> params);
	
	/**
	 * 逾期费用计算（确认框中费用）
	 *
	 * @author  wangjf
	 * @date    2016年1月6日 下午6:31:53
	 * @param params
	 * 		<tt>replaymentPlanId:String:还款计划ID</tt><br>
	 * @return
     *      <tt>overdueExpense:String:逾期费用</tt><br>
     *      <tt>overdueDays   :String:逾期天数</tt><br>
	 */
	@Deprecated
	public ResultVo caclOverdueRepayment(Map<String, Object> params);
	
	/**
	 * 逾期还款
	 *
	 * @author  wangjf
	 * @date    2016年1月6日 下午6:31:55
	 * @param params
	 * 		<tt>projectId:String:项目ID</tt><br>
	 * 		<tt>userId:String:创建人ID</tt><br>
	 * @return
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "projectId", required = true, requiredMessage = "项目ID不能为空!"),
			@Rule(name = "userId", required = true, requiredMessage = "创建人不能为空!")
	})
	public ResultVo overdueRepayment(Map<String, Object> params) throws SLException;
	
	/**
	 * 还款数据列表
	 *
	 * @author  wangjf
	 * @date    2016年1月6日 下午6:31:58
	 * @param params
     *      <tt>start              :String:起始值</tt><br>
     *      <tt>length             :String:长度</tt><br>
     *      <tt>projectType        :String:资产类型（可选）</tt><br>
     *      <tt>companyName        :String:公司名称（可选）</tt><br>
     *      <tt>projectNo          :String:项目编号（可选）</tt><br>
     *      <tt>productTerm        :String:项目期限（可选）</tt><br>
     *      <tt>repaymentMethod    :String:还款方式（可选）</tt><br>
     *      <tt>projectStatus      :String:项目状态（可选）</tt><br>
     *      <tt>beginReleaseDate   :String:开始发布日期（可选）</tt><br>
     *      <tt>endReleaseDate     :String:结束发布日期（可选）</tt><br>
     *      <tt>beginProjectEndDate:String:开始到期日期（可选）</tt><br>
     *      <tt>endPojectEndDate   :String:结束到期日期（可选）</tt><br>
     *      <tt>beginRepaymentDate :String:开始下一还款日（可选）</tt><br>
     *      <tt>endRepaymentDate   :String:结束下一还款日（可选）</tt><br>
	 * @return
     *      ResultVo
     *      <tt>iTotalDisplayRecords :String:记录总数</tt><br>
     *      <tt>data                 :String:List<Map<String, Object>></tt><br>
     *      <tt>replaymentPlanId     :String:还款计划ID</tt><br>
     *      <tt>projectId            :String:项目ID</tt><br>
     *      <tt>projectType          :String:资产类型</tt><br>
     *      <tt>projectNo            :String:项目编号</tt><br>
     *      <tt>companyName          :String:公司名称</tt><br>
     *      <tt>alreadyInvestAmount  :String:已投资总额</tt><br>
     *      <tt>remainderPrincipal   :String:剩余本金</tt><br>
     *      <tt>nextRepaymentDate    :String:下一还款日</tt><br>
     *      <tt>repaymentTotalAmount :String:本期应还金额</tt><br>
     *      <tt>earlyRepaymentExpense:String:提前结清手续费</tt><br>
     *      <tt>releaseDate          :String:发布日期</tt><br>
     *      <tt>projectEndDate       :String:到期日期</tt><br>
     *      <tt>projectStatus        :String:项目状态</tt><br>

	 */
	public ResultVo queryAllRepaymentList(Map<String, Object> params);
	
	/**
	 * 已投总额、剩余本金总额、本期应还总额、提前结清手续费总额
	 *
	 * @author  wangjf
	 * @date    2016年1月6日 下午6:32:00
	 * @param params
     *      <tt>projectType        :String:资产类型（可选）</tt><br>
     *      <tt>companyName        :String:公司名称（可选）</tt><br>
     *      <tt>projectNo          :String:项目编号（可选）</tt><br>
     *      <tt>productTerm        :String:项目期限（可选）</tt><br>
     *      <tt>repaymentMethod    :String:还款方式（可选）</tt><br>
     *      <tt>projectStatus      :String:项目状态（可选）</tt><br>
     *      <tt>beginReleaseDate   :String:开始发布日期（可选）</tt><br>
     *      <tt>endReleaseDate     :String:结束发布日期（可选）</tt><br>
     *      <tt>beginProjectEndDate:String:开始到期日期（可选）</tt><br>
     *      <tt>endPojectEndDate   :String:结束到期日期（可选）</tt><br>
     *      <tt>beginRepaymentDate :String:开始下一还款日（可选）</tt><br>
     *      <tt>endRepaymentDate   :String:结束下一还款日（可选）</tt><br>
	 * @return
     *      <tt>alreadyInvestAmount  :String:已投资总额</tt><br>
     *      <tt>remainderPrincipal   :String:剩余本金</tt><br>
     *      <tt>repaymentTotalAmount :String:本期应还金额</tt><br>
     *      <tt>earlyRepaymentExpense:String:提前结清手续费</tt><br>
	 */
	public ResultVo queryAllRepaymentTotal(Map<String, Object> params);
	
	/**
	 * 提前结清费用计算（确认框中费用）
	 *
	 * @author  wangjf
	 * @date    2016年1月6日 下午6:32:04
	 * @param params
     *      <tt>projectId:String:项目ID</tt><br>
	 * @return
     *      <tt>earlyRepaymentExpense:String:提前结清手续费</tt><br>
	 */
	@Deprecated
	public ResultVo caclEarlyRepayment(Map<String, Object> params);
	
	/**
	 * 提前结清还款
	 *
	 * @author  wangjf
	 * @date    2016年1月6日 下午6:32:07
	 * @param params
     *      <tt>projectId:String:项目ID</tt><br>
     *      <tt>userId:String:创建人ID</tt><br>
	 * @return
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "projectId", required = true, requiredMessage = "项目ID不能为空!"),
			@Rule(name = "userId", required = true, requiredMessage = "创建人不能为空!")
	})
	public ResultVo earlyRepayment(Map<String, Object> params) throws SLException;
	
	/**
	 * 正常还款
	 *
	 * @author  wangjf
	 * @date    2016年1月11日 下午2:13:17
	 * @param params
     *      <tt>replaymentPlanId:String:还款计划ID</tt><br>
     *      <tt>userId:String:创建人ID</tt><br> 		
	 * @return
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "replaymentPlanId", required = true, requiredMessage = "还款计划ID不能为空!"),
			@Rule(name = "userId", required = true, requiredMessage = "创建人不能为空!")
	})
	public ResultVo normalRepayment(Map<String, Object> params) throws SLException;
	
	/**
	 * 风险金垫付
	 *
	 * @author  wangjf
	 * @date    2016年1月11日 下午2:13:37
	 * @param params
     *      <tt>replaymentPlanId:String:还款计划ID</tt><br>
     *      <tt>userId:String:创建人ID</tt><br> 
	 * @return
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "replaymentPlanId", required = true, requiredMessage = "还款计划ID不能为空!"),
			@Rule(name = "userId", required = true, requiredMessage = "创建人不能为空!")
	})
	public ResultVo riskRepayment(Map<String, Object> params) throws SLException;
}
