/** 
 * @(#)FixedInvestService.java 1.0.0 2015年8月13日 上午11:25:55  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service;

import java.math.BigDecimal;
import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;

/**   
 * 定期宝业务列表、统计、交易、投资记录业务接口
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年8月13日 上午11:25:55 $ 
 */
public interface FixedInvestService {
	
	/**
	 * 定期宝分页列表查询
	 * 
	 * @param paramsMap
	 * 	      <ul>
	 *	     	<li>pageNum    当前页数从0开始	{@link java.lang.Integer}</li>
	 *	     	<li>pageSize   每页显示数据       	{@link java.lang.Integer}</li>
	 *        </ul>
	 * 
	 * @return 
	 *         <li>result {@link com.slfinance.vo.ResultVo} 返回结果</li>
	 *         <ul>
	 *         <li>result.success 	是否成功 		{@link java.lang.Boolean} required</li>
	 *         <li>result.data		定期宝列表数据 	{@link java.util.Map}</li>
	 *		   	<ul>
	 *         		<li>iTotalDisplayRecords 总记录数            {@link java.lang.Integer}</li>
	 *         		<li>data 				分页记录结果集   {@link java.util.List} </li>
	 *         		<ul>
	 *         			<li>{@link java.util.Map} 每行记录</li>
	 *         		</ul>
     *         		<ul>
	 *		  			<li>id 	                              产品ID		{@link java.lang.String}</li>
	 *	     			<li>productName     投资名称		{@link java.lang.String}</li>
	 *	     			<li>productType     投资类型		{@link java.lang.String}</li>
	 *	     			<li>productDesc 	简介(描述)	{@link java.lang.String}</li>
	 *	     			<li>yearRate		年化收益      	{@link java.math.BigDecimal}</li>
	 *	     			<li>awardRate		到期奖励      	{@link java.math.BigDecimal}</li>
	 *	     			<li>typeTerm		投资期限      	{@link java.lang.Integer}</li>
	 *	     			<li>useableAmount   剩余金额		{@link java.math.BigDecimal}</li>
	 *	     			<li>investMinAmount 起投金额		{@link java.math.BigDecimal}</li>
	 *	     			<li>increaseAmount  递增金额		{@link java.math.BigDecimal}</li>
	 *        		</ul> 
	 *         </ul>
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "pageNum", required = true, requiredMessage = "页数开始数据不能为空"),
			@Rule(name = "pageSize", required = true, requiredMessage = "分页大小不能为空")
			})
	ResultVo getFixedInvestListPage(Map<String,Object> paramsMap) throws SLException;
	
	/**
	 * 定期宝购买页面详情
	 * 
	 * @param paramsMap
	 * 	      <ul>
	 *	     	<li>id    定期宝ID				{@link java.lang.String}</li>
	 *	     	<li>custId用户ID(登录时传递)	    {@link java.lang.String}</li>
	 *        </ul>
	 * 
	 * @return 
	 *         <li>result {@link com.slfinance.vo.ResultVo} 返回结果</li>
	 *         <ul>
	 *         <li>result.success 	是否成功 		{@link java.lang.Boolean} required</li>
	 *         <li>result.data		定期宝列表数据 	{@link java.util.Map}</li>
	 *		   <ul>
	 *				<li>id					定期宝ID 				{@link java.lang.String}</li>
	 *				<li>useBuyAmount		可购份额 				{@link java.math.BigDecimal}</li>
	 *				<li>productName			产品名称    				{@link java.lang.String}</li>
	 *				<li>termName			期数(产品代表名称+日期)	{@link java.lang.String}</li>
	 *				<li>yearRate			预计年化收益   			{@link java.math.BigDecimal}</li>
	 *		     	<li>awardRate			到期奖励      			{@link java.math.BigDecimal}</li>
	 *				<li>useableAmount		剩余金额   				{@link java.math.BigDecimal}</li>
	 *				<li>incomeType			收益类型   				{@link java.lang.String}</li>
	 *				<li>investCount			投资笔数   				{@link java.lang.Integer}</li>
	 *				<li>loanCount			债权数量   				{@link java.lang.Integer}</li>
	 *				<li>totalInvestAmount	累计投资  				{@link java.math.BigDecimal}</li>
	 *				<li>currUseBuyAmount	当前可购份额  			{@link java.math.BigDecimal}</li>
	 *				<li>userUseableAmount	可用余额  				{@link java.math.BigDecimal}</li>
	 *				<li>investMaxAmount		投资上限  				{@link java.math.BigDecimal}</li>
	 *				<li>incomeToAmountTime	收益日期  				{@link java.util.Date}</li>
	 *	     		<li>typeTerm			投资期限      			{@link java.lang.Integer}</li>
	 *	 	     	<li>investMinAmount 	起投金额				{@link java.math.BigDecimal}</li>
	 *	     		<li>increaseAmount  	递增金额				{@link java.math.BigDecimal}</li>
	 *         </ul>
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "id", required = true, requiredMessage = "产品ID不能为空")
			})
	ResultVo getFixedInvestDatail(Map<String,Object> paramsMap) throws SLException;

	/**
	 * 定期宝产品加入记录
	 * 
	 * @param paramsMap
	 * 	      <ul>
	 *	     	<li>id		定期宝ID		{@link java.lang.String}</li>
	 *	     	<li>pageNum 当前页数从0开始	{@link java.lang.Integer}</li>
	 *	     	<li>pageSize每页显示数据       	{@link java.lang.Integer}</li>
	 *	 	    <li>termName当前期数       	{@link java.lang.String}</li>
	 *        </ul>
	 * 
	 * @return 
	 *         <li>result {@link com.slfinance.vo.ResultVo} 返回结果</li>
	 *         <ul>
	 *         <li>result.success 	是否成功 			{@link java.lang.Boolean} required</li>
	 *         <li>result.data		加入列表数据 		{@link java.util.Map}</li>
	 *		   <ul>
	 *	       		<li>iTotalDisplayRecords 总记录数            {@link java.lang.Integer}</li>
	 *         		<li>data 				  分页记录结果集  {@link java.util.List} </li>
	 *         		<ul>
	 *					<li>loginName	昵称		{@link java.lang.String}</li>
	 *					<li>investAmount加入金额	{@link java.math.BigDecimal}</li>
	 *					<li>date		加入日期   	{@link java.util.Date}</li>
	 *         		</ul>
	 *         </ul>
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "id", required = true, requiredMessage = "产品ID不能为空"),
			@Rule(name = "pageNum", required = true, requiredMessage = "当前页数不能为空"),
			@Rule(name = "pageSize", required = true, requiredMessage = "每页显示大小不能为空"),
			@Rule(name = "termName", required = true, requiredMessage = "当前期数不能为空")
			})
	ResultVo getInvestListByProIdPage(Map<String,Object> paramsMap) throws SLException;
	
	/**
	 * 帐户总览统计信息
	 * 
	 * @param paramsMap
	 * 	      <ul>
	 *	     	<li>custId	用户ID	{@link java.lang.String}</li>
	 *        </ul>
	 * 
	 * @return 
	 *         <li>result {@link com.slfinance.vo.ResultVo} 返回结果</li>
	 *         <ul>
	 *         <li>result.success 	是否成功 			{@link java.lang.Boolean} required</li>
	 *         <li>result.data		加入列表数据 		{@link java.util.Map}</li>
	 *		   <ul>
	 *         		<li>accountInfo 账户总览统计   {@link java.util.Map} </li>
	 *         			<ul>
	 *						<li>accountAmount	账户余额		{@link java.math.BigDecimal}</li>
	 *						<li>useableAmount	可用金额  		{@link java.math.BigDecimal}</li>
	 *						<li>freezeAmount	冻结金额   		{@link java.math.BigDecimal}</li>
	 *						<li>totalAmount		总资产   		{@link java.math.BigDecimal}</li>
	 *						<li>rewardNotSettle	未结算现金奖励   	{@link java.math.BigDecimal}</li>
	 *						<li>sumTradeAmount	累计收益   		{@link java.math.BigDecimal}</li>
	 *						<li>enableExpeAmount未使用体验金奖励{@link java.math.BigDecimal}</li>
	 *         			</ul>
	 *         		<li>investList	投资列表	{@link java.util.Map} </li>
	 *         			<ul>
	 *						<li>productName		产品名称		{@link java.lang.String}</li>
	 *						<li>yesterIncome	昨日收益		{@link java.math.BigDecimal}</li>
	 *						<li>holdAmount		持有份额		{@link java.math.BigDecimal}</li>
	 *						<li>sumIncome		累计收益		{@link java.math.BigDecimal}</li>
	 *						<li>preIncome		预计收益		{@link java.math.BigDecimal}</li>
	 *						<li>tradeAmunt		在投金额		{@link java.math.BigDecimal}</li>
	 *         			</ul>
	 *         </ul>
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "custId", required = true, requiredMessage = "用户ID不能为空")
			})
	ResultVo getFixedInvestStatisicInfo(Map<String,Object> paramsMap) throws SLException;

	/**
	 * 定期宝投资详情
	 * 
	 * @param paramsMap
	 * 
	 * 	      <ul>
	 *	     	<li>id		产品ID	{@link java.lang.String}</li>
	 *	     	<li>investId投资ID	{@link java.lang.String}</li>
	 *        </ul>
	 * @return 
	 *         <li>result {@link com.slfinance.vo.ResultVo} 返回结果</li>
	 *         <ul>
	 *         <li>result.success 	是否成功 		{@link java.lang.Boolean} required</li>
	 *         <li>result.data		定期宝投资详情 	{@link java.util.Map}</li>
	 *		   <ul>
	 *				<li>productName			产品名称    				{@link java.lang.String}</li>
	 *				<li>termName			期数(产品代表名称+日期)	{@link java.lang.String}</li>
	 *				<li>yearRate			年化利率   				{@link java.math.BigDecimal}</li>
	 *				<li>awardRate			奖励利率   				{@link java.math.BigDecimal}</li>
	 *				<li>typeTerm			投资期限   				{@link java.math.BigDecimal}</li>
	 *				<li>incomeType			收益类型   				{@link java.lang.String}</li>
	 *				<li>investCount			投资金额			   	{@link java.math.BigDecimal}</li>
	 *				<li>withDrawAmount		赎回金额|预计到账金额   	{@link java.math.BigDecimal}</li>
	 *				<li>preIncome			预计收益   				{@link java.math.BigDecimal}</li>
	 *				<li>incomeDate			起息日期  				{@link java.util.Date}</li>
	 *				<li>withDrawDate		赎回日期(已到期投资)  	{@link java.math.BigDecimal}</li>
	 *				<li>date				到期日期  				{@link java.util.Date}</li>
	 *				<li>state				当前状态  				{@link java.lang.String}</li>
	 *				<li>enable				是否可以赎回 			{@link java.lang.String}</li>
	 *         </ul>
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "id", required = true, requiredMessage = "产品ID不能为空"),
			@Rule(name = "investId", required = true, requiredMessage = "投资ID不能为空"),
			})
	ResultVo getFixedInvestDetail(Map<String,Object> paramsMap) throws SLException;

	/**
	 * 定期宝投资详情-加入记录
	 * 
	 * @param paramsMap
	 * 
	 * 	      <ul>
	 *	     	<li>custId		用户ID		{@link java.lang.String}</li>
	 *	     	<li>investId	投资ID		{@link java.lang.String}</li>
	 *	     	<li>pageNum    	当前页数从0开始	{@link java.lang.Integer}</li>
	 *	     	<li>pageSize   	每页显示数据       	{@link java.lang.Integer}</li>
	 *	     	<li>startDate	开始日期		{@link java.lang.String}</li>
	 *	     	<li>endDate		截止日期		{@link java.lang.String}</li>
	 *	     	<li>tradeType	交易类型		{@link java.lang.String}</li>
	 *        </ul>
	 * @return 
	 *         <li>result {@link com.slfinance.vo.ResultVo} 返回结果</li>
	 *         <ul>
	 *         <li>result.success 	是否成功 		{@link java.lang.Boolean} required</li>
	 *         <li>result.data		定期宝列表数据 	{@link java.util.Map}</li>
	 *		   	<ul>
	 *         		<li>iTotalDisplayRecords 总记录数            {@link java.lang.Integer}</li>
	 *         		<li>data 				分页记录结果集   {@link java.util.List} </li>
	 *         		<ul>
	 *         			<li>{@link java.util.Map} 每行记录</li>
	 *         		</ul>
     *         		<ul>
	 *	     			<li>tradeDate   交易日期		{@link java.util.Date}</li>
	 *	     			<li>tradeType   交易类型		{@link java.lang.String}</li>
	 *	     			<li>tradeAmount 金额			{@link java.math.BigDecimal}</li>
	 *	     			<li>memo		详情	      	{@link java.lang.String}</li>
	 *        		</ul> 
	 *         </ul>
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "custId", required = true, requiredMessage = "用户ID不能为空"),
			@Rule(name = "investId", required = true, requiredMessage = "投资ID不能为空"),
			@Rule(name = "pageNum", required = true, requiredMessage = "当前页数不能为空"),
			@Rule(name = "pageSize", required = true, requiredMessage = "每页显示大小不能为空")
			})
	ResultVo getFixedTradeInfoPage(Map<String,Object> paramsMap) throws SLException;
	
	/**
	 * 用户定期宝产品可购买额度 
	 * 用户剩余可购份额：(maxValue-用户持有份额)与某产品剩余可购买份额，比较取小，若(maxValue-用户持有份额)<0，则取0，（maxValue-用户持有份额，截取）
	 * 
	 * @param paramsMap
	 * 	  	 <ul>
	 *	     	<li>custId	用户ID		{@link java.lang.String}</li>
	 *	     	<li>id		产品ID		{@link java.lang.String}</li>
	 *        </ul>
	 * 
	 * @return
	 * 		<li>{@link java.math.BigDecimal} 返回结果</li>
	 * 
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "custId", required = true, requiredMessage = "用户ID不能为空"),
			@Rule(name = "id", required = true, requiredMessage = "产品ID不能为空")
			})
	BigDecimal getCurrUseBuyAmount(Map<String,Object> paramsMap) throws SLException;
	
	/**
	 * 校验产品时候可以提前赎回 
	 * 6个月产品大于等于3个月可赎回，12-18月产品大于等于六个月可赎回
	 * 
	 * @param paramsMap
	 * 	  	 <ul>
	 *	     	<li>id			产品ID		{@link java.lang.String}</li>
	 *	     	<li>investId	投资ID		{@link java.lang.String}</li>
	 *        </ul>
	 * 
	 * @return
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "id", required = true, requiredMessage = "产品ID不能为空"),
			@Rule(name = "investId", required = true, requiredMessage = "投资ID不能为空")
			})
	boolean getEnableAtone(String investId,String id)throws SLException;

	/**
	 * 定期宝分页列表查询-按照欢迎程度
	 * 
	 * @param paramsMap
	 * 	      <ul>
	 *	     	<li>pageNum    当前页数从0开始	{@link java.lang.Integer}</li>
	 *	     	<li>pageSize   每页显示数据       	{@link java.lang.Integer}</li>
	 *        </ul>
	 * 
	 * @return 
	 *         <li>result {@link com.slfinance.vo.ResultVo} 返回结果</li>
	 *         <ul>
	 *         <li>result.success 	是否成功 		{@link java.lang.Boolean} required</li>
	 *         <li>result.data		定期宝列表数据 	{@link java.util.Map}</li>
	 *		   	<ul>
	 *         		<li>iTotalDisplayRecords 总记录数            {@link java.lang.Integer}</li>
	 *         		<li>data 				分页记录结果集   {@link java.util.List} </li>
	 *         		<ul>
	 *         			<li>{@link java.util.Map} 每行记录</li>
	 *         		</ul>
     *         		<ul>
	 *		  			<li>id 	                              产品ID		{@link java.lang.String}</li>
	 *	     			<li>productName     投资名称		{@link java.lang.String}</li>
	 *	     			<li>productType     投资类型		{@link java.lang.String}</li>
	 *	     			<li>productDesc 	简介(描述)	{@link java.lang.String}</li>
	 *	     			<li>yearRate		年化收益      	{@link java.math.BigDecimal}</li>
	 *	     			<li>awardRate		到期奖励      	{@link java.math.BigDecimal}</li>
	 *	     			<li>typeTerm		投资期限      	{@link java.lang.Integer}</li>
	 *	     			<li>useableAmount   剩余金额		{@link java.math.BigDecimal}</li>
	 *	     			<li>investMinAmount 起投金额		{@link java.math.BigDecimal}</li>
	 *        		</ul> 
	 *         </ul>
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "pageNum", required = true, requiredMessage = "页数开始数据不能为空"),
			@Rule(name = "pageSize", required = true, requiredMessage = "分页大小不能为空")
			})
	ResultVo getInvestListByFavoPage(Map<String, Object> paramsMap) throws SLException;
	
	/**
	 * 申请成为金牌推荐人条件判断
	 * 
	 * @param paramsMap
	 * 
	 * 	      <ul>
	 *	     	<li>custId		用户ID	{@link java.lang.String}</li>
	 *        </ul>
	 * @return 
	 *         <li>result {@link com.slfinance.vo.ResultVo} 返回结果</li>
	 *         <ul>
	 *         <li>result.success 	是否成功 		{@link java.lang.Boolean} required</li>
	 *         <li>result.data		金牌推荐人条件 	{@link java.util.Map}</li>
	 *		   <ul>
	 *				<li>amountIsEnable	定期宝在投金额是否合格		{@link java.lang.Boolean}</li>
	 *				<li>recomNumIsEnable推荐好友数是否合格			{@link java.lang.Boolean}</li>
	 *				<li>isApplied		没有申请 |申请审核中 |已申请 	{@link java.lang.String</li>
	 *         </ul>
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "custId", required = true, requiredMessage = "用户信息不能为空")
			})
	ResultVo getRecommendFalg(Map<String, Object> paramsMap) throws SLException;
	
	/**
	 * 定期宝在投金额是否合格
	 * 
	 * @param paramsMap
	 * 
	 * 	      <ul>
	 *	     	<li>custId		用户ID	{@link java.lang.String}</li>
	 *        </ul>
	 * @return 
	 *         <li>result {@link com.slfinance.vo.ResultVo} 返回结果</li>
	 *         <li>result.success 	是否合格(true是合格) 	{@link java.lang.Boolean} required</li>
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "custId", required = true, requiredMessage = "用户信息不能为空")
	})
	ResultVo getInvestAmountIsEnable(Map<String, Object> paramsMap) throws SLException;
	
	/**
	 * 推荐好友数是否合格
	 * 
	 * @param paramsMap
	 * 
	 * 	      <ul>
	 *	     	<li>custId		用户ID	{@link java.lang.String}</li>
	 *        </ul>
	 * @return 
	 *         <li>result {@link com.slfinance.vo.ResultVo} 返回结果</li>
	 *         <li>result.success 	是否合格(true是合格) 		{@link java.lang.Boolean} required</li>
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "custId", required = true, requiredMessage = "用户信息不能为空")
	})
	ResultVo recomNumIsEnable(Map<String, Object> paramsMap) throws SLException;
	
	/**
	 * 申请金牌推荐人
	 * 
	 * @param paramsMap
	 * 
	 * 	      <ul>
	 *	     	<li>custId			用户ID	{@link java.lang.String}</li>
	 *	     	<li>operIpaddress	IP 		{@link java.lang.String}</li>
	 *        </ul>
	 * @return 
	 *         <li>result {@link com.slfinance.vo.ResultVo} 返回结果</li>
	 *         <ul>
	 *         <li>result.success 	是否成功 		{@link java.lang.Boolean} required</li>
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "custId", required = true, requiredMessage = "用户信息不能为空")
	})
	ResultVo putRecommendInfo(Map<String, Object> paramsMap) throws SLException;
	
}
