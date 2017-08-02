/** 
 * @(#)AppThirdService.java 1.0.0 2015年8月19日 上午11:16:12  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.mobile;

import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;

/**   
 * 善林财富三期手机端接口
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年8月19日 上午11:16:12 $ 
 */
public interface AppThirdService {
	
	/**
	 * 活期宝、体验宝、定期宝产品列表
	 * 
	 * @param paramsMap
	 * 
	 * @return 
	 * 		   <ul>
	 *         <li>result {@link com.slfinance.vo.ResultVo} 返回结果</li>
	 *         <ul>
	 *         <li>result.success 	是否成功 		{@link java.lang.Boolean} required</li>
	 *         <li>result.data		产品列表数据 	{@link java.util.List}</li>
	 *         	<ul>
	 *		  		<li>id 	                              产品ID		{@link java.lang.String}</li>
	 *	     		<li>productCat     	产品标示		{@link java.lang.String}</li>
	 *	     		<li>productType     投资类型		{@link java.lang.String}</li>
	 *	     		<li>productName     投资名称		{@link java.lang.String}</li>
	 *	     		<li>productDesc 	简介(描述)	{@link java.lang.String}</li>
	 *	     		<li>yearRate		年化收益      	{@link java.math.BigDecimal}</li>
	 *	     		<li>awardRate		到期奖励      	{@link java.math.BigDecimal}</li>
	 *	     		<li>typeTerm		投资期限      	{@link java.lang.Integer}</li>
	 *	 	     	<li>useableAmount   剩余金额		{@link java.math.BigDecimal}</li>
	 *	 	     	<li>investedScale   已投比例		{@link java.math.BigDecimal}</li>
	 *         	</ul>
	 * @throws SLException
	 */
	ResultVo investListAll(Map<String,Object> paramsMap) throws SLException;
	
	/**
	 * 活期宝、体验宝、定期宝购买投资页面
	 * 
	 * @param paramsMap
	 * 	  	  <ul>
	 *	     	<li>custId    用户ID(登陆是传递数据)		{@link java.lang.String}required</li>
	 *	     	<li>productId 产品ID    				{@link java.lang.String}required</li>
	 *        </ul>
	 * 
	 * @return 
	 *         <li>result {@link com.slfinance.vo.ResultVo} 返回结果</li>
	 *         <ul>
	 *         <li>result.success 	是否成功 		{@link java.lang.Boolean} required</li>
	 *         <li>result.data		投资数据	 	{@link java.util.Map}</li>
	 *		   	<ul>
	 *				<li>id					产品ID 				{@link java.lang.String}</li>
	 *				<li>yearRate			预计年化收益   			{@link java.math.BigDecimal}</li>
	 *		     	<li>awardRate			到期奖励      			{@link java.math.BigDecimal}</li>
	 *				<li>useableAmount		剩余金额   				{@link java.math.BigDecimal}</li>
	 *				<li>incomeTopDate		收益到账(首次收益时间)	{@link java.util.Date}</li>
	 *				<li>investCount			投资笔数   				{@link java.lang.Integer}</li>
	 *				<li>loanCount			债权数量   				{@link java.lang.Integer}</li>
	 *				<li>currUseBuyAmount	当前可购份额  			{@link java.math.BigDecimal}</li>
	 *				<li>userUseableAmount	可用余额  				{@link java.math.BigDecimal}</li>
	 *				<li>investMaxAmount		投资上限  				{@link java.math.BigDecimal}</li>
	 *	 	     	<li>typeTerm			投资期限      			{@link java.lang.Integer}</li>
	 *	     		<li>productDesc 		简介(描述)			{@link java.lang.String}</li>
	 *	     		<li>termName 			期数					{@link java.lang.String}</li>
	 *         	</ul>
	 * @throws SLException
	 */	
	@Rules(rules = {
			@Rule(name = "productId", required = true, requiredMessage = "产品ID不能为空")
			})
	ResultVo investbBuyDetail(Map<String,Object> paramsMap) throws SLException;
	
	/**
	 * 交易记录
	 * 
	 * @param paramsMap
	 *         <ul>
	 *	     	<li>custId 	        反馈信息用户的id		{@link java.lang.String}required</li>
	 *	     	<li>pageNum    当前页数从0开始      	{@link java.lang.String}required</li>
	 *	     	<li>pageSize   每页显示数据            	{@link java.lang.String}required</li>
	 *	     	<li>tradeType  交易类型            		{@link java.lang.String}required</li>
	 *         </ul>
	 * @return 
	 *         <li>result {@link com.slfinance.vo.ResultVo} 返回结果</li>
	 *         <ul>
	 *         <li>result.success 		是否成功 		{@link java.lang.Boolean} required</li>
	 *         <li>result.data			产品列表数据 	{@link java.util.Map}</li>
	 *         <li>iTotalDisplayRecords 总记录数 		{@link java.lang.Integer}</li>
	 *         <li>data 				分页记录结果集 	{@link java.util.List} </li>
	 *         	<ul>
	 *		  		<li>tradeType   交易类型       {@link java.lang.String}</li>
	 *	     		<li>tradeAmount 交易金额       {@link java.math.BigDecimal}</li>
	 *	     		<li>tradeDate   交易日期       {@link java.util.Date}</li>
	 *        	</ul> 
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "custId", required = true, requiredMessage = "用户ID不能为空"),
			@Rule(name = "pageNum", required = true, requiredMessage = "分页不能为空"),
			@Rule(name = "pageSize", required = true, requiredMessage = "分页不能为空")
			})
	ResultVo transactionList(Map<String,Object> paramsMap) throws SLException;
	
	/**
	 * 我的投资-活期宝、定期宝、体验宝页面
	 * 
	 * @param paramsMap
	 *         <ul>
	 *	     	<li>custId		用户的id		{@link java.lang.String}required</li>
	 *	     	<li>productType	产品名称	    {@link java.lang.String}required</li>
	 *	     	<li>pageNum    	当前页数从0开始  {@link java.lang.String}</li>
	 *	     	<li>pageSize   	每页显示数据	{@link java.lang.String}</li>
	 *	     	<li>status		投资状态		{@link java.lang.String}持有中、赎回中、已退出</li>
	 *         </ul>
	 * @return 
	 *         <li>result {@link com.slfinance.vo.ResultVo} 返回结果</li>
	 *         <ul>
	 *         <li>result.success 	是否成功 		{@link java.lang.Boolean} required</li>
	 *         <li>result.data		投资数据 		{@link java.util.Map}</li>
	 *         	<li>investStatis	投资统计 		{@link java.util.Map}</li>
	 *         		<ul>
	 *					<li>yearRate			预计年化收益   			{@link java.math.BigDecimal}</li>
	 *					<li>maxYearRate			最大利率	   			{@link java.math.BigDecimal}</li>
	 *					<li>holdAmount			持有份额	   			{@link java.math.BigDecimal}</li>
	 *		     		<li>awardRate			到期奖励      			{@link java.math.BigDecimal}</li>
	 *		     		<li>yestIncome			昨日收益      			{@link java.math.BigDecimal}</li>
	 *		     		<li>preIncome			预计收益      			{@link java.math.BigDecimal}</li>
	 *		     		<li>totalIncome			累计收益      			{@link java.math.BigDecimal}</li>
	 *		     		<li>totalAtone			累计赎回      			{@link java.math.BigDecimal}</li>
	 *		     		<li>totalInvest			累计投资      			{@link java.math.BigDecimal}</li>
	 *        		</ul> 
	 *         	<li>investList	投资列表 		{@link java.util.Map} 定期宝展示</li>
	 *         		<li>iTotalDisplayRecords 总记录数 		{@link java.lang.Integer}</li>
	 *         		<li>data 				分页记录结果集 	{@link java.util.List} </li>
	 *         			<ul>
	 *						<li>id			产品ID {@link java.lang.String}</li>
	 *						<li>investId	投资ID {@link java.lang.String}</li>
	 *						<li>productName产品类别   {@link java.lang.String}</li>
	 *		     			<li>typeTerm	期限        {@link java.lang.Integer}</li>
	 *        			</ul> 
	 *         	<li>investManage 活期宝投资类别		{@link java.util.List} 活期宝展示</li>
	 *         		<ul>
	 *					<li>lowerLimitDay		投资最小天数	{@link java.lang.Integer}</li>
	 *					<li>upperLimitDay		投资最大天数	{@link java.lang.Integer}</li>
	 *					<li>yearRate			年化收益		{@link java.math.BigDecimal}</li>
	 *		     		<li>awardRate			奖励收益		{@link java.math.BigDecimal}</li>
	 *		     		<li>accountTotalValue	持有金额        	{@link java.math.BigDecimal}</li>
	 *        		</ul> 
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "custId", required = true, requiredMessage = "用户ID不能为空"),
			@Rule(name = "productType", required = true, requiredMessage = "产品类型不能为空")
			})
	ResultVo investStatisticsInfo(Map<String,Object> paramsMap) throws SLException;
	
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
	 *				<li>withDrawAmount		赎回金额(已到期投资)   	{@link java.math.BigDecimal}</li>
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
			@Rule(name = "investId", required = true, requiredMessage = "投资ID不能为空")
			})
	ResultVo fixedInvestDetail(Map<String,Object> paramsMap) throws SLException;
	
	/**
	 * 提前退出定期宝详情
	 * 
	 * @param paramsMap
	 * 
	 * 	      <ul>
	 *	     	<li>id			产品ID	{@link java.lang.String}</li>
	 *	     	<li>investId	投资ID	{@link java.lang.String}</li>
	 *        </ul>
	 * @return 
	 *         <li>result {@link com.slfinance.vo.ResultVo} 返回结果</li>
	 *         <li>result.success 	是否成功 		{@link java.lang.Boolean} required</li>
	 *         <li>result.data		定期宝投资详情 	{@link java.util.Map}</li>
	 *		   <ul>
	 *				<li>termName		退出期数    		{@link java.lang.String}</li>
	 *				<li>tradeAmount		加入金额  		{@link java.math.BigDecimal}</li>
	 *				<li>preIncomeAmount	预计回收金额  	{@link java.math.BigDecimal}</li>
	 *				<li>expensive		提前退出费用  	{@link java.math.BigDecimal}</li>
	 *         </ul>
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "id", required = true, requiredMessage = "产品ID不能为空"),
			@Rule(name = "investId", required = true, requiredMessage = "投资ID不能为空")
			})
	ResultVo preAtoneDetail(Map<String,Object> paramsMap) throws SLException;
	
	/**
	 * 购买定期宝
	 * 
	 * @param params
	 * 	  	  <ul>
	 *	     	<li>custId		用户的id    	{@link java.lang.String}</li>
	 *	     	<li>productId 	产品ID    	{@link java.lang.String}</li>
	 *	     	<li>tradeAmount	交易金额           	{@link java.math.BigDecimal}</li>
	 *	     	<li>appSource	交易来源           	{@link java.lang.String}</li>
	 *        </ul>
	 * 
	 * @return 
	 *         <li>result {@link com.slfinance.vo.ResultVo} 返回结果</li>
	 *         <ul>
	 *         <li>result.success 是否成功 {@link java.lang.Boolean} required</li>
	 *         <li>result.message 响应消息 {@link java.lang.String}</li>
	 *         </ul>
	 * @throws SLException
	 */
	ResultVo joinTermBao(Map<String, Object> paramsMap) throws SLException;
	
	/**
	 * 定期宝产品加入记录
	 * 
	 * @param paramsMap
	 * 	      <ul>
	 *	     	<li>id		定期宝ID		{@link java.lang.String}</li>
	 *	     	<li>pageNum 当前页数从0开始	{@link java.lang.String}</li>
	 *	     	<li>pageSize每页显示数据       	{@link java.lang.String}</li>
	 *	     	<li>termName当前期数      	{@link java.lang.String}</li>
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
	ResultVo joinListInfoTrd(Map<String,Object> paramsMap) throws SLException;
	
	/**
	 * 申请提前赎回定期宝
	 * 
	 * @param paramsMap
	 * 	      <ul>
	 *	     	<li>investId		投资ID		{@link java.lang.String}</li>
	 *	     	<li>custId 			用户ID		{@link java.lang.String}</li>
	 *	     	<li>tradePassword	交易密码       	{@link java.lang.String}</li>
	 *	     	<li>appSource		APP来源标示       	{@link java.lang.String}</li>
	 *        </ul>
	 * 
	 * @return 
	 *         <li>result {@link com.slfinance.vo.ResultVo} 返回结果</li>
	 *         <ul>
	 *         <li>result.success 	是否成功 			{@link java.lang.Boolean} required</li>
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "investId", required = true, requiredMessage = "投资主键不能为空!"),
			@Rule(name = "custId", required = true, requiredMessage = "客户主键不能为空!"),
			@Rule(name = "tradePassword", required = true, requiredMessage = "交易密码不能为空!")
	})
	ResultVo termWithdrawApply(Map<String, Object> params) throws SLException;

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
	 *				<li>isApplied		没有申请 |申请审核中 |已申请 	{@link java.lang.String}</li>
	 *         </ul>
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "custId", required = true, requiredMessage = "用户信息不能为空")
			})
	ResultVo recommendFalg(Map<String, Object> paramsMap)throws SLException;

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
	ResultVo putRecommend(Map<String, Object> paramsMap)throws SLException;
	
	/**
	 * 金牌推荐人佣金统计信息
	 * 
	 * @param paramsMap
	 * 
	 * 	      <ul>
	 *	     	<li>custId			用户ID		{@link java.lang.String}</li>
	 *	 	    <li>pageNum 		当前页数从0开始	{@link java.lang.String}</li>
	 *	     	<li>pageSize		每页显示数据       	{@link java.lang.String}</li>
	 *        </ul>
	 * @return 
	 *         <li>result {@link com.slfinance.vo.ResultVo} 返回结果</li>
	 *         <ul>
	 *         <li>result.success 	是否成功 		{@link java.lang.Boolean} required</li>
	 *         <li>result.data		汇总信息	 	{@link java.util.Map}</li>
	 *         	<li>statisInfo		统计信息	 	{@link java.util.Map}</li>
	 *         		<ul>
	 *					<li>totalAmount		累计佣金收益		{@link java.math.BigDecimal}</li>
	 *					<li>custCount		累计推荐好友		{@link java.lang.Integer}</li>
	 *					<li>commissionAmount累计佣金 			{@link java.math.BigDecimal}</li>
	 *					<li>rewardAmount	累计奖励 			{@link java.math.BigDecimal}</li>
	 *         		</ul>
	 *         	<li>recList			推荐列表	 	{@link java.util.Map}</li>
	 *       		<ul>
	 *	       			<li>iTotalDisplayRecords 总记录数            {@link java.lang.Integer}</li>
	 *         			<li>data 				  分页记录结果集  {@link java.util.List} </li>
	 *         			<ul>
	 *					<li>tradeDate		日期		{@link java.util.Date}</li>
	 *					<li>commissionAmount推广佣金	{@link java.math.BigDecimal}</li>
	 *					<li>rewardAmount	推广奖励 	{@link java.math.BigDecimal}</li>
	 *					<li>commissionId	推荐ID 	{@link java.lang.String}</li>
	 *         			</ul>
	 *         		</ul>
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "custId", required = true, requiredMessage = "用户信息不能为空")
	})
	ResultVo RecInfo(Map<String, Object> paramsMap)throws SLException;
	
	/**
	 * 佣金详情列表
	 * 
	 * @param paramsMap
	 * 
	 * 	      <ul>
	 *	     	<li>commissionId 	推荐ID		{@link java.lang.String}</li>
	 *        </ul>
	 * @return 
	 *         <li>result {@link com.slfinance.vo.ResultVo} 返回结果</li>
	 *         <ul>
	 *         <li>result.success 	是否成功 		{@link java.lang.Boolean} required</li>
	 *         <li>result.data		列表信息	 	{@link java.util.Map}</li>
	 *         		<ul>
	 *					<li>loginName		推荐好友	{@link java.lang.String}</li>
	 *					<li>tradeDate		日期		{@link java.util.Date}</li>
	 *					<li>investAmount	再投金额 	{@link java.math.BigDecimal}</li>
	 *         		</ul>
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "custId", required = true, requiredMessage = "用户信息不能为空")
	})
	ResultVo awardList(Map<String, Object> paramsMap)throws SLException;

	/**
	 * 统计信息(推广奖励、年化投资、推荐好友)和推广奖励信息(活期宝、定期宝)
	 * 
	 * @param paramsMap
	 * 	      <ul>
	 *	     	<li>custId		登录用户ID				{@link java.lang.String} required</li>
	 *	     	<li>productType	产品类型(活期宝|定期宝)	{@link java.lang.String} required</li>
	 *	     	<li>pageNum		当前页数从0开始			{@link java.lang.String} required</li>
	 *	     	<li>pageSize	每页显示数据       			{@link java.lang.String} required</li>
	 *        </ul>
	 * @return 
	 *         <li>result {@link com.slfinance.vo.ResultVo} 返回结果</li>
	 *         <ul>
	 *         <li>result.success 	是否成功 		{@link java.lang.Boolean} required</li>
	 *         <li>result.data		统计信息	 	{@link java.util.Map}</li>
	 *         	<ul>
	 *         		<li>statisticInfo 统计信息   {@link java.util.Map} </li>
	 *         			<ul>
	 *						<li>recAward	推广奖励	{@link java.math.BigDecimal}</li>
	 *						<li>investAmount累计年化投资{@link java.math.BigDecimal}</li>
	 *						<li>recFrieCount累计推荐好友 {@link java.lang.Integer}</li>
	 *         			</ul>
	 *         		<li>recAwareList	推广奖励	{@link java.util.Map} </li>
	 *		   			<ul>
	 *         				<li>iTotalDisplayRecords 总记录数            {@link java.lang.Integer}</li>
	 *         				<li>data 				分页记录结果集   {@link java.util.List} </li>
     *         				<ul>
     *		  					<li>id					提成ID	{@link java.lang.String}</li>
	 *		  					<li>date				日期		{@link java.util.Date}</li>
	 *	     					<li>dateInvestAmount	当日在投金额{@link java.math.BigDecimal}</li>
	 *	     					<li>recAward    		推广奖励	{@link java.math.BigDecimal}</li>
	 *	     					<li>isSettled 			是否结算	{@link java.lang.String}</li>
	 *	     					<li>month				月份      	{@link java.lang.String}</li>
	 *							<li>monthInvestAmount	当月投资金额{@link java.math.BigDecimal}</li>
	 *        				</ul> 
	 *         			</ul>
	 *         	</ul>
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "custId", required = true, requiredMessage = "用户信息不能为空"),
			@Rule(name = "productType", required = true, requiredMessage = "产品信息不能为空"),
			@Rule(name = "pageNum", required = true, requiredMessage = "页数开始数据不能为空"),
			@Rule(name = "pageSize", required = true, requiredMessage = "分页大小不能为空")
			})
	ResultVo recommedInfo(Map<String ,Object> paramsMap)throws SLException;
	
	/**
	 * 金牌推荐人当天或当月在投详情
	 * 
	 * @param paramsMap
	 * 	      <ul>
	 *	     	<li>id			提成ID			{@link java.lang.String} required</li>
	 *	     	<li>custId		登录用户ID			{@link java.lang.String} required</li>
	 *	     	<li>productType	产品类型(活期宝|定期宝){@link java.lang.String} required</li>
	 *	 	    <li>pageNum		当前页数从0开始		{@link java.lang.String} required</li>
	 *	     	<li>pageSize	每页显示数据       		{@link java.lang.String} required</li>
	 *        </ul>
	 * @return 
	 *         <li>result {@link com.slfinance.vo.ResultVo} 返回结果</li>
	 *         <ul>
	 *         <li>result.success 	是否成功 	{@link java.lang.Boolean} required</li>
	 *         <li>result.data		在投详情	{@link java.util.List}</li>
	 *         	<ul>
	 *         		<li>iTotalDisplayRecords 总记录数            {@link java.lang.Integer}</li>
	 *         		<li>data 				分页记录结果集   {@link java.util.List} </li>
     *         		<ul>
	 *		  			<li>date			日期		{@link java.util.Date}</li>
	 *	     			<li>custName		推荐人	{@link java.lang.String}</li>
	 *	     			<li>investAmount    在投金额	{@link java.math.BigDecimal}</li>
	 *	     			<li>month			月份      	{@link java.lang.String}</li>
	 *					<li>productName		产品名称	{@link java.lang.String}</li>
	 *	 				<li>currTerm		产品期数	{@link java.lang.String}</li>
	 *					<li>yearInvestAmount年化投资金额{@link java.math.BigDecimal}</li>
	 *        		</ul> 
	 *         	</ul>
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "id", required = true, requiredMessage = "提成ID不能为空"),
			@Rule(name = "productType", required = true, requiredMessage = "产品信息不能为空"),
			@Rule(name = "custId", required = true, requiredMessage = "用户信息不能为空"),
			@Rule(name = "pageNum", required = true, requiredMessage = "页数开始数据不能为空"),
			@Rule(name = "pageSize", required = true, requiredMessage = "分页大小不能为空")
			})
	ResultVo investListDetail(Map<String ,Object> paramsMap)throws SLException;
	
	/**
	 * 产品列表（带利率）
	 *
	 * @author  wangjf
	 * @date    2016年1月12日 下午2:05:03
	 * @param paramsMap
	 * @return
	 * @throws SLException
	 */
	ResultVo investListAllWithYearRateList(Map<String,Object> paramsMap) throws SLException;
}
