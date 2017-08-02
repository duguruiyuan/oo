/** 
 * @(#)ProjectService.java 1.0.0 2016年1月5日 上午9:48:37  
 *  
 * Copyright © 2016 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service;

import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;

/**   
 * 项目管理服务类
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2016年1月5日 上午9:48:37 $ 
 */
public interface ProjectService {

	/**
	 * 我要投资-直投项目-融资租赁列表
	 *
	 * @author  wangjf
	 * @date    2016年1月5日 上午9:51:04
	 * @param params
     *      <tt>start:String:起始值</tt><br>
     *      <tt>length:String:长度</tt><br> 
	 * @return
     *      ResultVo
     *      <tt>iTotalDisplayRecords:String:记录总数</tt><br>
     *      <tt>data                :String:List<Map<String, Object>></tt><br>
     *      <tt>id                  :String:项目ID  </tt><br>
     *      <tt>projectType         :String:资产类型</tt><br>
     *      <tt>projectNo           :String:项目编号</tt><br>
     *      <tt>companyName         :String:公司名称</tt><br>
     *      <tt>projectName         :String:项目名称</tt><br>
     *      <tt>projectStatus       :String:项目状态</tt><br>
     *      <tt>yearRate            :String:年化利率</tt><br>
     *      <tt>projectTotalAmount  :String:项目总额</tt><br>
     *      <tt>currUsableValue     :String:剩余金额</tt><br>
     *      <tt>investScale         :String:百分比  </tt><br>
     *      <tt>typeTerm            :String:项目期限</tt><br>
     *      <tt>seatTerm            :String:封闭期限</tt><br>
     *      <tt>investMinAmount     :String:起投金额</tt><br>
     *      <tt>investMaxAmount     :String:投资上限</tt><br>
     *      <tt>increaseAmount      :String:递增金额</tt><br>
     *      <tt>releaseDate         :String:发布日期</tt><br>
     *      <tt>effectDate          :String:生效日期</tt><br>
     *      <tt>projectEndDate      :String:到期日期</tt><br>
     *      <tt>repaymnetMethod     :String:还款方式</tt><br>
     *      <tt>ensureMethod        :String:保障方式</tt><br>
	 */
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "起始值不能为空!", number = true, numberMessage = "起始值必须为数字"),
			@Rule(name = "length", required = true, requiredMessage = "长度不能为空!", number = true, numberMessage = "长度必须为数字")
	})
	public ResultVo queryAllProjectList(Map<String, Object> params);
	
	/**
	 * 我要投资-直投项目-融资租赁明细
	 *
	 * @author  wangjf
	 * @date    2016年1月5日 上午9:53:14
	 * @param params
     *      <tt>projectId:String:项目ID</tt><br>
	 * @return
     *      <tt>id                       :String:项目ID  </tt><br>
     *      <tt>projectType              :String:资产类型</tt><br>
     *      <tt>projectNo                :String:项目编号</tt><br>
     *      <tt>companyName              :String:公司名称</tt><br>
     *      <tt>projectName              :String:项目名称</tt><br>
     *      <tt>projectStatus            :String:项目状态</tt><br>
     *      <tt>yearRate                 :String:年化利率</tt><br>
     *      <tt>projectTotalAmount       :String:项目总额</tt><br>
     *      <tt>currUsableValue          :String:剩余金额</tt><br>
     *      <tt>investScale              :String:百分比  </tt><br>
     *      <tt>typeTerm                 :String:项目期限</tt><br>
     *      <tt>seatTerm                 :String:封闭期限</tt><br>
     *      <tt>investMinAmount          :String:起投金额</tt><br>
     *      <tt>investMaxAmount          :String:投资上限</tt><br>
     *      <tt>increaseAmount           :String:递增金额</tt><br>
     *      <tt>releaseDate              :String:发布日期</tt><br>
     *      <tt>effectDate               :String:生效日期</tt><br>
     *      <tt>projectEndDate           :String:到期日期</tt><br>
     *      <tt>repaymnetMethod          :String:还款方式</tt><br>
     *      <tt>ensureMethod             :String:保障方式</tt><br>
     *      <tt>isAtone                  :String:可否赎回</tt><br>
     *      <tt>projectDescr             :String:项目描述</tt><br>
     *      <tt>companyDescr             :String:企业信息</tt><br>
     *      <tt>interestsMethod          :String:计息方式</tt><br>
     *      <tt>List<Map<String, Object>>:String:附件列表</tt><br>
     *      <tt>attachmentType           :String:         附件类型</tt><br>
     *      <tt>attachmentName           :String:         附件名称</tt><br>
     *      <tt>storagePath              :String:         存储路径</tt><br>
	 */
	@Rules(rules = { 
			@Rule(name = "projectId", required = true, requiredMessage = "项目ID不能为空!")
	})
	public ResultVo queryProjectDetail(Map<String, Object> params);
	
	/**
	 * 我要投资-直投项目-加入记录
	 *
	 * @author  wangjf
	 * @date    2016年1月5日 上午10:47:13
	 * @param params
     *      <tt>start    :String:起始值</tt><br>
     *      <tt>length   :String:长度</tt><br>
     *      <tt>projectId:String:项目ID</tt><br>
	 * @return
     *      ResultVo
     *      <tt>iTotalDisplayRecords:String:记录总数</tt><br>
     *      <tt>data                :String:List<Map<String, Object>></tt><br>
     *      <tt>loginName           :String:用户昵称</tt><br>
     *      <tt>tradeAmount         :String:加入金额</tt><br>
     *      <tt>tradeDate           :String:加入日期</tt><br>
	 */
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "起始值不能为空!", number = true, numberMessage = "起始值必须为数字"),
			@Rule(name = "length", required = true, requiredMessage = "长度不能为空!", number = true, numberMessage = "长度必须为数字"),
			@Rule(name = "projectId", required = true, requiredMessage = "项目ID不能为空!")
	})
	public ResultVo queryProjectJoinList(Map<String, Object> params);
	
	/**
	 * 我要投资-直投项目-融资租赁收益计算
	 *
	 * @author  wangjf
	 * @date    2016年1月5日 上午10:48:34
	 * @param params
     *      <tt>repaymentMethod:String:还款方式</tt><br>
     *      <tt>typeTerm:String:投资期限</tt><br>
     *      <tt>tradeAmount:String:投资金额</tt><br>
     *      <tt>yearRate:String:年化收益</tt><br>
	 * @return
     *      <tt>incomeAmount:String:预期收益</tt><br>
     *      <tt>totalAmount :String:持有份额</tt><br>
	 */
	@Rules(rules = { 
			@Rule(name = "repaymentMethod", required = true, requiredMessage = "还款方式不能为空!"),
			@Rule(name = "typeTerm", required = true, requiredMessage = "投资期限不能为空!", number = true, numberMessage = "投资期限只能为数字"),
			@Rule(name = "tradeAmount", required = true, requiredMessage = "投资金额不能为空!", number = true, numberMessage = "投资金额只能为数字"),
			@Rule(name = "yearRate", required = true, requiredMessage = "年化收益不能为空!", number = true, numberMessage = "年化收益只能为数字"),
	})
	public ResultVo caclProject(Map<String, Object> params) throws SLException;
	
	/**
	 * 我要投资-直投项目-立即购买
	 *
	 * @author  wangjf
	 * @date    2016年1月5日 上午11:32:59
	 * @param params
     *      <tt>projectId  :String:项目ID</tt><br>
     *      <tt>tradeAmount:String:投资金额</tt><br>
     *      <tt>custId     :String:客户ID</tt><br>
     *      <tt>channelNo  :String:渠道号（可以为空）</tt><br>
     *      <tt>meId       :String:设备ID（可以为空）</tt><br>
     *      <tt>meVersion  :String:设备版本号（可以为空）</tt><br>
     *      <tt>appSource  :String:设备来源（可以为空）</tt><br>
     *      <tt>ipAddress  :String:ip地址（可以为空）</tt><br>
	 * @return
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "projectId", required = true, requiredMessage = "项目ID不能为空!"),
			@Rule(name = "tradeAmount", required = true, requiredMessage = "投资金额不能为空!", number = true, numberMessage = "投资金额只能为数字"),
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空!")
	})
	public ResultVo joinProject(Map<String, Object> params) throws SLException;
	
	/**
	 * 我的账户-账户总览-企业借款收益情况
	 *
	 * @author  wangjf
	 * @date    2016年1月5日 上午11:34:17
	 * @param params
     *      <tt>custId:String:客户ID</tt><br>
	 * @return
	 *      <tt>holdAmount          :String:在投金额/累积投资</tt><br>
     *      <tt>exceptAmount        :String:预期收益</tt><br>
     *      <tt>sumTradeAmount      :String:累积收益</tt><br>
     *      <tt>totalAmount         :String:持有份额</tt><br>
     *      <tt>exceptRepayAmount   :String:待收收益</tt><br>
     *      <tt>exceptRepayPrincipal:String:待收本金</tt><br>
     *      <tt>receivedAmount      :String:已获收益</tt><br>
	 */
	@Rules(rules = {
			@Rule(name = "custId", required = true, requiredMessage = "用户ID不能为空")
	})
	public ResultVo queryProjectIncome(Map<String, Object> params);
	
	/**
	 * 投资管理-直投列表-项目列表
	 *
	 * @author  wangjf
	 * @date    2016年1月5日 上午11:36:07
	 * @param params
     *      <tt>start :String:起始值</tt><br>
     *      <tt>length:String:长度</tt><br>
     *      <tt>custId:String:客户ID</tt><br>
	 * @return
     *      ResultVo
     *      <tt>iTotalDisplayRecords:String:记录总数</tt><br>
     *      <tt>data                :String:List<Map<String, Object>></tt><br>
     *      <tt>id                  :String:项目ID  </tt><br>
     *      <tt>projectType         :String:资产类型</tt><br>
     *      <tt>projectNo           :String:项目编号</tt><br>
     *      <tt>companyName         :String:公司名称</tt><br>
     *      <tt>projectName         :String:项目名称</tt><br>
     *      <tt>projectStatus       :String:项目状态</tt><br>
     *      <tt>yearRate            :String:年化利率</tt><br>
     *      <tt>projectTotalAmount  :String:项目总额</tt><br>
     *      <tt>currUsableValue     :String:剩余金额</tt><br>
     *      <tt>investScale         :String:百分比  </tt><br>
     *      <tt>typeTerm            :String:项目期限</tt><br>
     *      <tt>seatTerm            :String:封闭期限</tt><br>
     *      <tt>investMinAmount     :String:起投金额</tt><br>
     *      <tt>investMaxAmount     :String:投资上限</tt><br>
     *      <tt>increaseAmount      :String:递增金额</tt><br>
     *      <tt>releaseDate         :String:发布日期</tt><br>
     *      <tt>effectDate          :String:生效日期</tt><br>
     *      <tt>projectEndDate      :String:到期日期</tt><br>
     *      <tt>repaymnetMethod     :String:还款方式</tt><br>
     *      <tt>ensureMethod        :String:保障方式</tt><br>
	 */
	@Rules(rules = {
			@Rule(name = "custId", required = true, requiredMessage = "用户ID不能为空")
	})
	public ResultVo queryMyProjectList(Map<String, Object> params);
	
	/**
	 * 投资管理-直投列表-还款明细
	 *
	 * @author  wangjf
	 * @date    2016年1月5日 上午11:37:35
	 * @param params
     *      <tt>projectId:String:项目ID</tt><br>
	 * @return
     *      <tt>currentTerm                 :String:当前期数     </tt><br>
     *      <tt>expectRepaymentDate         :String:当期还款日期 </tt><br>
     *      <tt>repaymentTotalAmount(本金+利息) :String:当期应收本息 </tt><br>
     *      <tt>termAlreadyRepayAmount      :String:当期已还金额 </tt><br>
     *      <tt>repaymentInterest           :String:当期应还利息 </tt><br>
     *      <tt>repaymentPrincipal          :String:当期应还本金 </tt><br>
     *      <tt>remainderPrincipal          :String:当前剩余本金 </tt><br>
     *      <tt>repaymentStatus             :String:还款状态     </tt><br>
	 */
	@Rules(rules = {
			@Rule(name = "projectId", required = true, requiredMessage = "项目Id不能为空")
	})
	public ResultVo queryProjectRepaymentList(Map<String, Object> params);
	
	/**
	 * 投资管理-直投列表-投资记录
	 *
	 * @author  wangjf
	 * @date    2016年1月5日 上午11:40:34
	 * @param params
     *      <tt>start    :String:起始值</tt><br>
     *      <tt>length   :String:长度</tt><br>
     *      <tt>projectId:String:项目ID</tt><br>
	 * @return
     *      ResultVo
     *      <tt>iTotalDisplayRecords:String:记录总数</tt><br>
     *      <tt>data                :String:List<Map<String, Object>></tt><br>
     *      <tt>investDate          :String:购买时间</tt><br>
     *      <tt>investAmount        :String:购买金额</tt><br>
     *      <tt>loginName           :String:用户名称</tt><br>
	 */
	public ResultVo queryProjectInvestList(Map<String, Object> params);
	
	/**
	 * 投资管理-直投列表-查看合同
	 *
	 * @author  wangjf
	 * @date    2016年1月5日 上午11:52:48
	 * @param params
     *      <tt>projectId:String:项目ID</tt><br>
	 * @return
	 */
	@Rules(rules = {
			@Rule(name = "projectId", required = true, requiredMessage = "项目ID不能为空!"),
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空!")
			})
	public ResultVo queryProjectContract(Map<String, Object> params);
	
	/**
	 * 项目管理-新建/编辑/审核项目-项目列表
	 *
	 * @author  wangjf
	 * @date    2016年1月5日 上午11:54:27
	 * @param params
     *      <tt>start              :String:起始值</tt><br>
     *      <tt>length             :String:长度</tt><br>
     *      <tt>projectType        :String:资产类型（可选）</tt><br>
     *      <tt>companyName        :String:公司名称（可选）</tt><br>
     *      <tt>projectNo          :String:项目编号（可选）</tt><br>
     *      <tt>projectName        :String:项目名称（可选）</tt><br>
     *      <tt>productTerm        :String:项目期限（可选）</tt><br>
     *      <tt>repaymentMethod    :String:还款方式（可选）</tt><br>
     *      <tt>projectStatus      :String:项目状态（可选）</tt><br>
     *      <tt>beginReleaseDate   :String:开始发布日期（可选）</tt><br>
     *      <tt>endReleaseDate     :String:结束发布日期（可选）</tt><br>
     *      <tt>beginEffectDate    :String:开始生效日期（可选）</tt><br>
     *      <tt>endEffectDate      :String:结束生效日期（可选）</tt><br>
     *      <tt>beginProjectEndDate:String:开始到期日期（可选）</tt><br>
     *      <tt>endPojectEndDate   :String:结束到期日期（可选）</tt><br>
	 * @return
	 *      ResultVo
     *      <tt>iTotalDisplayRecords:String:记录总数</tt><br>
     *      <tt>data                :String:List<Map<String, Object>></tt><br>
     *      <tt>id                  :String:项目ID  </tt><br>
     *      <tt>projectType         :String:资产类型</tt><br>
     *      <tt>projectNo           :String:项目编号</tt><br>
     *      <tt>companyName         :String:公司名称</tt><br>
     *      <tt>projectName         :String:项目名称</tt><br>
     *      <tt>projectStatus       :String:项目状态</tt><br>
     *      <tt>yearRate            :String:年化利率</tt><br>
     *      <tt>projectTotalAmount  :String:项目总额</tt><br>
     *      <tt>currUsableValue     :String:剩余金额</tt><br>
     *      <tt>investScale         :String:百分比  </tt><br>
     *      <tt>typeTerm            :String:项目期限</tt><br>
     *      <tt>seatTerm            :String:封闭期限</tt><br>
     *      <tt>investMinAmount     :String:起投金额</tt><br>
     *      <tt>investMaxAmount     :String:投资上限</tt><br>
     *      <tt>increaseAmount      :String:递增金额</tt><br>
     *      <tt>releaseDate         :String:发布日期</tt><br>
     *      <tt>effectDate          :String:生效日期</tt><br>
     *      <tt>projectEndDate      :String:到期日期</tt><br>
     *      <tt>repaymnetMethod     :String:还款方式</tt><br>
     *      <tt>ensureMethod        :String:保障方式</tt><br>
	 */
	public ResultVo queryProjectList(Map<String, Object> params);
	
	/**
	 * 项目管理-新建/编辑/审核项目-项目明细（查看项目、编辑时查看、审核时查看）
	 *
	 * @author  wangjf
	 * @date    2016年1月5日 上午11:58:25
	 * @param params
	 * 		<tt>projectId:String:项目ID</tt><br>
	 * @return
     *      <tt>id                       :String:项目ID  </tt><br>
     *      <tt>projectType              :String:资产类型</tt><br>
     *      <tt>projectNo                :String:项目编号</tt><br>
     *      <tt>companyName              :String:公司名称</tt><br>
     *      <tt>projectName              :String:项目名称</tt><br>
     *      <tt>projectStatus            :String:项目状态</tt><br>
     *      <tt>yearRate                 :String:年化利率</tt><br>
     *      <tt>projectTotalAmount       :String:项目总额</tt><br>
     *      <tt>currUsableValue          :String:剩余金额</tt><br>
     *      <tt>investScale              :String:百分比  </tt><br>
     *      <tt>typeTerm                 :String:项目期限</tt><br>
     *      <tt>seatTerm                 :String:封闭期限</tt><br>
     *      <tt>investMinAmount          :String:起投金额</tt><br>
     *      <tt>investMaxAmount          :String:投资上限</tt><br>
     *      <tt>increaseAmount           :String:递增金额</tt><br>
     *      <tt>releaseDate              :String:发布日期</tt><br>
     *      <tt>effectDate               :String:生效日期</tt><br>
     *      <tt>projectEndDate           :String:到期日期</tt><br>
     *      <tt>repaymnetMethod          :String:还款方式</tt><br>
     *      <tt>ensureMethod             :String:保障方式</tt><br>
     *      <tt>isAtone                  :String:可否赎回</tt><br>
     *      <tt>projectDescr             :String:项目描述</tt><br>
     *      <tt>companyDescr             :String:企业信息</tt><br>
     *      <tt>interestsMethod          :String:计息方式</tt><br>
     *      <tt>List<Map<String, Object>>:String:附件列表</tt><br>
     *      <tt>attachmentType           :String:         附件类型</tt><br>
     *      <tt>attachmentName           :String:         附件名称</tt><br>
     *      <tt>storagePath              :String:         存储路径</tt><br>		
	 */
	public ResultVo queryProjectDetailById(Map<String, Object> params);
	
	/**
	 * 项目管理-新建/编辑/审核项目-暂存项目
	 *
	 * @author  wangjf
	 * @date    2016年1月5日 下午12:00:29
	 * @param params
     *      <tt>projectType              :String:资产类型</tt><br>
     *      <tt>projectNo                :String:项目编号</tt><br>
     *      <tt>companyName              :String:公司名称</tt><br>
     *      <tt>projectName              :String:项目名称</tt><br>
     *      <tt>projectStatus            :String:项目状态</tt><br>
     *      <tt>actualYearRate           :String:原始年化利率</tt><br>
     *      <tt>yearRate                 :String:用户年化利率</tt><br>
     *      <tt>projectTotalAmount       :String:项目总额</tt><br>
     *      <tt>typeTerm                 :String:项目期限</tt><br>
     *      <tt>seatTerm                 :String:封闭期限</tt><br>
     *      <tt>investMinAmount          :String:起投金额</tt><br>
     *      <tt>increaseAmount           :String:递增金额</tt><br>
     *      <tt>releaseDate              :String:发布日期</tt><br>
     *      <tt>effectDate               :String:生效日期</tt><br>
     *      <tt>projectEndDate           :String:到期日期</tt><br>
     *      <tt>repaymnetMethod          :String:还款方式</tt><br>
     *      <tt>ensureMethod             :String:保障方式</tt><br>
     *      <tt>isAtone                  :String:可否赎回</tt><br>
     *      <tt>projectDescr             :String:项目描述</tt><br>
     *      <tt>companyDescr             :String:企业信息</tt><br>
     *      <tt>List<Map<String, Object>>:String:附件列表</tt><br>
     *      <tt>attachmentType           :String:         附件类型</tt><br>
     *      <tt>attachmentName           :String:         附件名称</tt><br>
     *      <tt>storagePath              :String:         存储路径</tt><br>
     *      <tt>userId                   :String:创建人</tt><br>
	 * @return
	 * @throws SLException
	 */
	public ResultVo applyProject(Map<String, Object> params) throws SLException;
	
	/**
	 * 项目管理-新建/编辑/审核项目-提交项目（保存并审核）
	 *
	 * @author  wangjf
	 * @date    2016年1月5日 下午12:02:00
	 * @param params
     *      <tt>projectType              :String:资产类型</tt><br>
     *      <tt>projectNo                :String:项目编号</tt><br>
     *      <tt>companyName              :String:公司名称</tt><br>
     *      <tt>projectName              :String:项目名称</tt><br>
     *      <tt>projectStatus            :String:项目状态</tt><br>
     *      <tt>actualYearRate           :String:原始年化利率</tt><br>
     *      <tt>yearRate                 :String:用户年化利率</tt><br>
     *      <tt>projectTotalAmount       :String:项目总额</tt><br>
     *      <tt>typeTerm                 :String:项目期限</tt><br>
     *      <tt>seatTerm                 :String:封闭期限</tt><br>
     *      <tt>investMinAmount          :String:起投金额</tt><br>
     *      <tt>increaseAmount           :String:递增金额</tt><br>
     *      <tt>releaseDate              :String:发布日期</tt><br>
     *      <tt>effectDate               :String:生效日期</tt><br>
     *      <tt>projectEndDate           :String:到期日期</tt><br>
     *      <tt>repaymnetMethod          :String:还款方式</tt><br>
     *      <tt>ensureMethod             :String:保障方式</tt><br>
     *      <tt>isAtone                  :String:可否赎回</tt><br>
     *      <tt>projectDescr             :String:项目描述</tt><br>
     *      <tt>companyDescr             :String:企业信息</tt><br>
     *      <tt>List<Map<String, Object>>:String:附件列表</tt><br>
     *      <tt>attachmentType           :String:         附件类型</tt><br>
     *      <tt>attachmentName           :String:         附件名称</tt><br>
     *      <tt>storagePath              :String:         存储路径</tt><br>
     *      <tt>userId                   :String:创建人</tt><br>
	 * @return
	 * @throws SLException
	 */
	public ResultVo saveProject(Map<String, Object> params) throws SLException;
	
	/**
	 * 删除项目
	 *
	 * @author  wangjf
	 * @date    2016年1月5日 下午12:05:06
	 * @param params
     *      <tt>projectId:String:项目ID</tt><br>
     *      <tt>userId   :String:创建人</tt><br>
	 * @return
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "projectId", required = true, requiredMessage = "项目ID不能为空!")
			})
	public ResultVo deleteProject(Map<String, Object> params) throws SLException;
	
	/**
	 * 审核项目
	 *
	 * @author  wangjf
	 * @date    2016年1月5日 下午12:05:09
	 * @param params
     *      <tt>projectId  :String:项目ID</tt><br>
     *      <tt>auditStatus:String:审核状态</tt><br>
     *      <tt>auditMemo  :String: 审核备注</tt><br>
     *      <tt>userId     :String:创建人</tt><br>
	 * @return
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "projectId", required = true, requiredMessage = "项目ID不能为空!"),
			@Rule(name = "auditStatus", required = true, requiredMessage = "审核状态不能为空!"),
			@Rule(name = "auditMemo", required = true, requiredMessage = "审核备注不能为空!")
	})
	public ResultVo auditProject(Map<String, Object> params) throws SLException;
	
	/**
	 * 发布项目
	 * 
	 * 注：手动发布项目时调用本接口前，需先调用接口judgeProject判断是否有相同期限的项目正在发布中，给出提示，是否确认发布
	 *
	 * @author  wangjf
	 * @date    2016年1月5日 下午12:05:11
	 * @param params
     *      <tt>projectId:String:项目ID</tt><br>
     *      <tt>userId   :String:创建人</tt><br>
	 * @return
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "projectId", required = true, requiredMessage = "项目ID不能为空!"),
			@Rule(name = "userId", required = true, requiredMessage = "创建人不能为空!")
	})
	public ResultVo publishProject(Map<String, Object> params) throws SLException;
	
	/**
	 * 流标项目（未满标人工流标）
	 *
	 * @author  wangjf
	 * @date    2016年1月5日 下午12:05:14
	 * @param params
     *      <tt>projectId:String:项目ID</tt><br>
     *      <tt>userId   :String:创建人</tt><br>
	 * @return
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "projectId", required = true, requiredMessage = "项目ID不能为空!"),
			@Rule(name = "userId", required = true, requiredMessage = "创建人不能为空!")
	})
	public ResultVo unReleaseProject(Map<String, Object> params) throws SLException;
	
	/**
	 * 发标项目（未满标人工发标）
	 *
	 * @author  wangjf
	 * @date    2016年1月5日 下午12:05:16
	 * @param params
     *      <tt>projectId:String:项目ID</tt><br>
     *      <tt>userId   :String:创建人</tt><br>
	 * @return
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "projectId", required = true, requiredMessage = "项目ID不能为空!"),
			@Rule(name = "userId", required = true, requiredMessage = "创建人不能为空!")
	})
	public ResultVo releaseProject(Map<String, Object> params) throws SLException;
	
	/**
	 * 判断是否有相同期限的项目正在发布中
	 *
	 * @author  wangjf
	 * @date    2016年1月6日 上午9:52:12
	 * @param params
	 * @return
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "typeTerm", required = true, requiredMessage = "项目期限不能为空!", number = true, numberMessage = "项目期限只能为数字"),
	})
	public ResultVo judgeProject(Map<String, Object> params)throws SLException;
	
	/**
	 * 判断项目编号是否重复
	 * @param params
	 * @return
	 */
	@Rules(rules = { 
			@Rule(name = "projectNo", required = true, requiredMessage = "项目编号不能为空!")
	})
	public ResultVo isExistsProject(Map<String, Object> params);
	
	
	/**
	 * 
	 * @author zhangt
	 * @date   2016年3月1日下午12:59:29
	 * @param params
	 *      <tt>projectId:String:项目主键</tt><br>
     *      <tt>custId   :String:客户ID</tt><br>
	 * @return
	 *      <tt>计划主键                          :String:项目Id</tt><br>
     *      <tt>projectName   :String:项目名称 </tt><br>
     *      <tt>typeTerm      :String:项目期限(月)</tt><br>
     *      <tt>yearRate      :String:年化收益率</tt><br>
     *      <tt>awardRate     :String:奖励利率</tt><br>
     *      <tt>incomeType    :String:结算方式</tt><br>
     *      <tt>releaseDate   :String:发布日期    </tt><br>
     *      <tt>effectDate    :String:生效日期(项目计息日期)</tt><br>
     *      <tt>endDate       :String:到期日期（项目结束日期）</tt><br>
     *      <tt>projectStatus :String:项目状态 </tt><br>
     *      <tt>investAmount  :String:投资金额</tt><br>
     *      <tt>exceptAmount  :String:待收收益</tt><br>
     *      <tt>receviedAmount:String:已收收益</tt><br>
     *      <tt>investStatus  :String:投资状态（当前状态）</tt><br>
     *      <tt>reminderDay   :String:剩余天数</tt><br>
     *      <tt>accountAmount :String:可用余额</tt><br>
	 */
	@Rules(rules = { 
			@Rule(name = "projectId", required = true, requiredMessage = "项目主键不能为空!"),
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空!")
	})
	public ResultVo queryMyProjectDetail(Map<String, Object> params);
	
	/**
	 * 企业借款收益展示
	 * 
	 * @author zhangt
	 * @date   2016年3月4日上午11:56:34
	 * @param params
	 *      <tt>custId:String:客户ID</tt><br>
	 * @return
	 *      <tt>created             :String:投资中:Map<String, Object></tt><br>
     *      <tt>totalCounts         :String:     项目数</tt><br>
     *      <tt>totalTradeAmount    :String:     投资金额</tt><br>
     *      <tt>totalIncomeAmount   :String:     收益</tt><br>
     *      <tt>totalPrevTradeAmount:String:     历史投资金额</tt><br>
     *      <tt>doing               :String:收益中:Map<String, Object></tt><br>
     *      <tt>totalCounts         :String:     项目数</tt><br>
     *      <tt>totalTradeAmount    :String:     投资金额</tt><br>
     *      <tt>totalIncomeAmount   :String:     收益</tt><br>
     *      <tt>totalPrevTradeAmount:String:     历史投资金额</tt><br>
     *      <tt>finished            :String:已结束:Map<String, Object></tt><br>
     *      <tt>totalCounts         :String:     项目数</tt><br>
     *      <tt>totalTradeAmount    :String:     投资金额</tt><br>
     *      <tt>totalIncomeAmount   :String:     收益</tt><br>
     *      <tt>totalPrevTradeAmount:String:     历史投资金额</tt><br>
	 */
	@Rules(rules = { 
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空!")
	})
	public ResultVo queryProjectTotalIncome(Map<String, Object> params);
}
