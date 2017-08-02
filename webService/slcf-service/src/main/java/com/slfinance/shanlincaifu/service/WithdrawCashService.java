/** 
 * @(#)WithdrawCashService.java 1.0.0 2015年4月27日 上午10:27:55  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 
package com.slfinance.shanlincaifu.service;
import java.util.List;
import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;

/**   
 * 提现模块业务Service
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年4月27日 上午10:27:55 $ 
 */
public interface WithdrawCashService {
	
	/**
	 * 提现管理--列表
	 * 
	 * @param paramsMap
	 *            <ul>
	 *            	<li>pageNum        	分页开始记录数 		{@link java.lang.Integer}</li>
	 *              <li>pageSize      	 每页显示记录数 		{@link java.lang.Integer}</li>
	 *			  	<li>custName 	 	客户名称 				{@link java.lang.String}</li>
	 *			  	<li>tradeNo     	订单编号 			{@link java.lang.String}</li>
	 *			  	<li>tradeStatus 	交易状态    			{@link java.lang.String}</li>
	 *			  	<li>auditStatus 	审核状态    			{@link java.lang.String}</li>
	 *			  	<li>credentialsCode 证件号码 		 	{@link java.lang.String}</li>
	 *			  	<li>startDate       操作时间[开始时间]	{@link java.util.Date}  </li>
	 * 				<li>endDate         操作时间[结束时间] 	{@link java.util.Date}  </li>
	 *            </ul>
	 * @return <ul>
	 *         <li>iTotalDisplayRecords 总记录数 {@link java.lang.Integer}</li>
	 *         <li>data {@link java.util.List} 分页记录结果集</li>
	 *         <ul>
	 *         <li>{@link java.util.Map} 每行记录</li>
     *         <ul>
	 *         <li>id                  id      {@link java.lang.String}</li>
	 *         <li>custId              用户ID	{@link java.lang.String}</li>
	 *         <li>custCode            用户编号        {@link java.lang.String}</li>
	 *         <li>custName            用户姓名        {@link java.lang.String}</li>
	 *         <li>credentialsCode     证件号码        {@link java.lang.String}</li>
	 *         <li>credentialsType     证件类型        {@link java.lang.String}</li>
	 *         <li>nickName            用户昵称        {@link java.lang.String}</li>
	 *         <li>tradeNo             订单编号        {@link java.lang.String}</li>
	 *         <li>tradeType           类型                {@link java.lang.String}</li>
	 *         <li>bankName            提现银行        {@link java.lang.String}</li>
	 *         <li>tradeAmount         提现金额        {@link java.math.BigDecimal}</li>
	 *         <li>procedureFee        提现手续费    {@link java.math.BigDecimal}</li>
	 *         <li>realTradeAmount     实际到账金额{@link java.math.BigDecimal}</li>
	 *         <li>tradeStatus         交易状态        {@link java.lang.String}</li>
	 *         <li>auditStatus         审核状态        {@link java.lang.String}</li>
	 *         <li>withDrawTime        操作时间        {@link java.util.Date}</li>
	 *         <li>createDate                  {@link java.util.Date}</li>
	 *         <li>bankCardNumber      分支银行卡号{@link java.lang.String}</li>
	 *         <li>bankBranchName      分支银行名称{@link java.lang.String}</li>
	 *         <li>memo                备注                {@link java.lang.String}</li>
	 *        </ul> 
	 *        </ul>
	 */
	@Rules(rules = {
			@Rule(name = "start", required = true, requiredMessage = "分页数据不能为空"),
			@Rule(name = "length", required = true, requiredMessage = "分页数据不能为空")
			})
	public Map<String,Object> findAllWithdrawCashList(Map<String,Object> paramsMap) throws SLException;
	
	/**
	 * 提现管理--明细-提现详细
	 * 
	 * @param paramsMap
	 *            <ul>
	 *            	<li>id        	提现ID 		{@link java.lang.String}</li>
	 *            </ul>
	 * @return 
	 *        <ul>
	 *			<li>id                  id      {@link java.lang.String}</li>
	 *			<li>custId              用户ID	{@link java.lang.String}</li>
	 *			<li>custCode            用户编号        {@link java.lang.String}</li>
	 *			<li>custName            用户姓名        {@link java.lang.String}</li>
	 *			<li>credentialsCode     证件号码        {@link java.lang.String}</li>
	 *			<li>credentialsType     证件类型        {@link java.lang.String}</li>
	 *			<li>nickName            用户昵称        {@link java.lang.String}</li>
	 *			<li>tradeNo             订单编号        {@link java.lang.String}</li>
	 *			<li>tradeType           类型                {@link java.lang.String}</li>
	 *			<li>bankName            提现银行        {@link java.lang.String}</li>
	 *			<li>tradeAmount         提现金额        {@link java.math.BigDecimal}</li>
	 *			<li>procedureFee        提现手续费    {@link java.math.BigDecimal}</li>
	 *			<li>realTradeAmount     实际到账金额{@link java.math.BigDecimal}</li>
	 *			<li>tradeStatus         交易状态        {@link java.lang.String}</li>
	 * 			<li>auditStatus         审核状态        {@link java.lang.String}</li>
	 *			<li>withDrawTime        操作时间        {@link java.util.Date}</li>
	 *			<li>createDate                  {@link java.util.Date}</li>
	 *			<li>bankCardNumber      分支银行卡号{@link java.lang.String}</li>
	 *			<li>bankBranchName      分支银行名称{@link java.lang.String}</li>
	 *			<li>memo                备注                {@link java.lang.String}</li>
	 *			<li>version             版本信息         {@link java.lang.Integer}</li>
	 *        </ul>
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "id", required = true, requiredMessage = "ID不能为空")
			})
	public Map<String,Object> findWithdrawalCashDetailInfo(Map<String,Object> paramsMap) throws SLException;
	
	/**
	 *  提现管理--明细-审核记录
	 *  
	 * @param paramsMap
	 *         <ul>
	 *		  	<li>id 	 	提现ID {@link java.lang.String}</li>
	 *         </ul>
	 * @return
     *        <ul>
	 *         <li>id          id      {@link java.lang.String}</li>
	 *         <li>applyTime   提现时间	   {@link java.lang.String}</li>
	 *         <li>tradeAmount 提现金额        {@link java.lang.String}</li>
	 *         <li>auditTime   审核时间        {@link java.lang.String}</li>
	 *         <li>auditUser   审核人        {@link java.lang.String}</li>
	 *         <li>auditStatus 审核状态       {@link java.lang.String}</li>
	 *         <li>memo        审核备注        {@link java.lang.String}</li>
	 *        </ul> 
	 * @throws SLException
	 */
	public List<Map<String, Object>> findAuditLogByWithDraw(Map<String, Object> paramsMap) throws SLException;
	
	/**
	 * 提现管理--统计
	 * 
	 * @param paramsMap
	 *         <ul>
	 *		  	<li>custName 	 	用户名称 			{@link java.lang.String}</li>
	 *		  	<li>tradeNo     	订单编号 			{@link java.lang.String}</li>
	 *		  	<li>tradeStatus 	交易状态    			{@link java.lang.String}</li>
	 *		  	<li>auditStatus 	审核状态    			{@link java.lang.String}</li>
	 *		  	<li>credentialsCode 证件号码 		 	{@link java.lang.String}</li>
	 *		  	<li>startDate       操作时间[开始时间]	{@link java.util.Date}  </li>
	 *			<li>endDate         操作时间[结束时间] 	{@link java.util.Date}  </li>
	 *         </ul>
	 * @return <ul>
	 *         <li>statisticsInfo {@link java.util.Map} 统计信息记录结果集</li>
	 *         <ul>
	 *         <li>{@link java.util.Map} 每行记录</li>
	 * 	         <ul>
	 *	 			<li>tradeAmountCount    提现金额汇总        {@link java.math.BigDecimal}</li>
	 *	 		    <li>tradeCostCount      实际到账金额汇总{@link java.math.BigDecimal}</li>
	 *	 			<li>realTradeAmountCount提现手续费汇总    {@link java.math.BigDecimal}</li>
	 *	          </ul> 
	 *         </ul>
	 */
	public Map<String,Object> findAllWithdrawCashSum(Map<String,Object> paramsMap) throws SLException;
	
	/**
	 * 提现管理--提现审核查询-历史提现记录
	 * 	 <ul>
	 *		<li>提现审核明细页面</li>
	 *		<li>id 	 	AuditInfoEntity的主键ID(必填数据) 	  {@link java.lang.String}</li>
	 *		<li>custId 	AuditInfoEntity的用户custId(必填数据)  {@link java.lang.String}</li>
	 *   </ul>
	 * 	 <ul>
	 *		<li>提现审核操作页面</li>
	 *		<li>id 	 	AuditInfoEntity的主键ID(万万不能传的数据,你懂的){@link java.lang.String}</li>
	 *		<li>custId 	AuditInfoEntity的用户custId(必填数据) {@link java.lang.String}</li>
	 *   </ul>
	 * @return
     *        <ul>
	 *         <li>id          id      {@link java.lang.String}</li>
	 *         <li>applyTime   提现时间	   {@link java.lang.String}</li>
	 *         <li>tradeAmount 提现金额        {@link java.lang.String}</li>
	 *         <li>auditTime   审核时间        {@link java.lang.String}</li>
	 *         <li>auditUser   审核人        {@link java.lang.String}</li>
	 *         <li>auditStatus 审核状态       {@link java.lang.String}</li>
	 *         <li>memo        审核备注        {@link java.lang.String}</li>
	 *        </ul> 
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "custId", required = true, requiredMessage = "用户ID不能为空")
			})
	public List<Map<String, Object>> findWithdrawCashAuditList(Map<String,Object> paramsMap) throws SLException;
	
	/**
	 * 提现管理--提现审核提交
	 * 
	 * @param paramsMap
	 *         <ul>
	 *		  	<li>id 	                     提现审核ID 	 {@link java.lang.String}</li>
	 *		  	<li>auditStatus提现审核状态      {@link java.lang.String}</li>
	 *		  	<li>memo 	         提现审核备注 	 {@link java.lang.String}</li>
	 *	     	<li>custId 	         操作用户的id {@link java.lang.String}</li>
	 *			<li>version    版本信息             {@link java.lang.Integer}</li>
	 *         </ul>
	 * @return
	 *         <li>result {@link com.slfinance.vo.ResultVo} 返回结果</li>
	 *         <ul>
	 *         <li>result.success 是否成功 {@link java.lang.Boolean} required</li>
	 *         <li>result.message 响应消息 {@link java.lang.String} required</li>
	 *         </ul>
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "id", required = true, requiredMessage = "提现审核ID不能为空"),
			@Rule(name = "auditStatus", required = true, requiredMessage = "提现审核状态不能为空"),
			@Rule(name = "memo", required = true, requiredMessage = "提现审核备注不能为空"),
			@Rule(name = "custId", required = true, requiredMessage = "用户ID不能为空"),
			@Rule(name = "ipAddress", required = true, requiredMessage = "用户IP地址不能为空")
			})
	public ResultVo saveWithdrawCashAudit(Map<String,Object> paramsMap) throws SLException;
	
	/**
	 * 提现回调成功处理业务
	 * 
	 * @param paramsMap
	 * 	       <ul>
	 *		  	<li>tradeNo    交易编号 	    {@link java.lang.String}</li>
	 *		  	<li>tradeCode  第三方返回交易码       {@link java.lang.String}</li>
	 *		  	<li>status 	         过程处理状态 	    {@link java.lang.String}</li>
	 *	     	<li>auditStatus审核状态                       {@link java.lang.String}</li>
	 *			<li>tradeStatus交易状态                       {@link java.lang.String}</li>
	 *         </ul>
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "tradeNo", required = true, requiredMessage = "交易编号不能为空"),
			@Rule(name = "tradeCode", required = true, requiredMessage = "第三方返回交易码不能为空"),
			@Rule(name = "status", required = true, requiredMessage = "过程处理状态不能为空"),
			@Rule(name = "auditStatus", required = true, requiredMessage = "审核状态不能为空"),
			@Rule(name = "tradeStatus", required = true, requiredMessage = "交易状态不能为空")
			})
	public ResultVo callbackWithdrawCash(Map<String,Object> paramsMap) throws SLException;

	/**
	 * 提现回调失败处理业务

	 * @param paramsMap
	 * 	  	 <ul>
	 *		  	<li>tradeNo    交易编号 	    {@link java.lang.String}</li>
	 *		  	<li>tradeCode  第三方返回交易码       {@link java.lang.String}</li>
	 *		  	<li>status 	         过程处理状态 	    {@link java.lang.String}</li>
	 *	     	<li>auditStatus审核状态                       {@link java.lang.String}</li>
	 *			<li>tradeStatus交易状态                       {@link java.lang.String}</li>
	 *       <ul>
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "tradeNo", required = true, requiredMessage = "交易编号不能为空"),
			@Rule(name = "tradeCode", required = true, requiredMessage = "第三方返回交易码不能为空"),
			@Rule(name = "status", required = true, requiredMessage = "过程处理状态不能为空"),
			@Rule(name = "auditStatus", required = true, requiredMessage = "审核状态不能为空"),
			@Rule(name = "tradeStatus", required = true, requiredMessage = "交易状态不能为空")
			})
	public ResultVo callbackWithdrawCashFailed(Map<String, Object> paramsMap)throws SLException;
	
	/**
	 * 提现管理--提现审核查询-历史提现记录-分页
	 * 
	 * @param paramsMap
	 *            <ul>
	 *            	<li>start        	分页开始记录数 		{@link java.lang.Integer}</li>
	 *              <li>length      	 每页显示记录数 		{@link java.lang.Integer}</li>
	 *            </ul>
	 * 	  		  <ul>
	 *				<li>提现审核明细页面</li>
	 *				<li>id 	 	AuditInfoEntity的主键ID(必填数据) 	  {@link java.lang.String}</li>
	 *				<li>custId 	AuditInfoEntity的用户custId(必填数据)  {@link java.lang.String}</li>
	 *   		  </ul>
	 * 	 		  <ul>
	 *				<li>提现审核操作页面</li>
	 *				<li>id 	 	AuditInfoEntity的主键ID(万万不能传的数据,你懂的){@link java.lang.String}</li>
	 *				<li>custId 	AuditInfoEntity的用户custId(必填数据) {@link java.lang.String}</li>
	 *   		   </ul>           
	 * @return <ul>
	 *         <li>iTotalDisplayRecords 总记录数 {@link java.lang.Integer}</li>
	 *         <li>data {@link java.util.List} 分页记录结果集</li>
	 *         <ul>
	 *         <li>{@link java.util.Map} 每行记录</li>
     *        <ul>
	 *         <li>id          id      {@link java.lang.String}</li>
	 *         <li>applyTime   提现时间        {@link java.lang.String}</li>
	 *         <li>tradeAmount 提现金额        {@link java.lang.String}</li>
	 *         <li>auditTime   审核时间        {@link java.lang.String}</li>
	 *         <li>auditUser   审核人            {@link java.lang.String}</li>
	 *         <li>auditStatus 审核状态         {@link java.lang.String}</li>
	 *         <li>memo        审核备注          {@link java.lang.String}</li>
	 *        </ul> 
	 *        </ul>
	 */
	@Rules(rules = {
			@Rule(name = "start", required = true, requiredMessage = "分页数据不能为空"),
			@Rule(name = "length", required = true, requiredMessage = "分页数据不能为空"),
			@Rule(name = "custId", required = true, requiredMessage = "用户ID不能为空")
			})
	public Map<String, Object> findWithdrawCashAuditListPage(Map<String, Object> paramsMap)throws SLException;
}
