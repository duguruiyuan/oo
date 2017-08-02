/** 
 * @(#)RecommedBusiService.java 1.0.0 2015年10月12日 下午3:02:38  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service;

import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;

/**   
 * 金牌推荐人前端WEB业务相关接口
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年10月12日 下午3:02:38 $ 
 */
public interface RecommedBusiService {

	/**
	 * 统计信息(推广奖励、年化投资、推荐好友)和推广奖励信息(活期宝、定期宝)
	 * 
	 * @param paramsMap
	 * 	      <ul>
	 *	     	<li>custId		登录用户ID				{@link java.lang.String} required</li>
	 *	     	<li>productType	产品类型(活期宝|定期宝)	{@link java.lang.String} required</li>
	 *	     	<li>pageNum		当前页数从0开始			{@link java.lang.Integer} required</li>
	 *	     	<li>pageSize	每页显示数据       			{@link java.lang.Integer} required</li>
	 *	     	<li>startDate	开始日期       	{@link java.lang.String}</li>
	 *	     	<li>endDate		结束日期       	{@link java.lang.String}</li>
	 *	     	<li>isSettled	是否结算       	{@link java.lang.String}</li>
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
			@Rule(name = "pageNum", required = true, requiredMessage = "页数开始数据不能为空",digist = true, digistMessage="数据类型错误"),
			@Rule(name = "pageSize", required = true, requiredMessage = "分页大小不能为空",digist = true, digistMessage="数据类型错误")
			})
	ResultVo getRecommedInfo(Map<String ,Object> paramsMap)throws SLException;
	
	/**
	 * 金牌推荐人当天或当月在投详情
	 * 
	 * @param paramsMap
	 * 	      <ul>
	 *	     	<li>id			提成ID			{@link java.lang.String} required</li>
	 *	     	<li>custId		登录用户ID			{@link java.lang.String} required</li>
	 *	     	<li>productType	产品类型(活期宝|定期宝){@link java.lang.String} required</li>
	 *	 	    <li>pageNum		当前页数从0开始		{@link java.lang.Integer} required</li>
	 *	     	<li>pageSize	每页显示数据       		{@link java.lang.Integer} required</li>
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
	 *					<li>currTerm		产品期数	{@link java.lang.String}</li>
	 *					<li>yearInvestAmount年化投资金额{@link java.math.BigDecimal}</li>
	 *        		</ul> 
	 *         	</ul>
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "id", required = true, requiredMessage = "提成ID不能为空"),
			@Rule(name = "productType", required = true, requiredMessage = "产品信息不能为空"),
			@Rule(name = "custId", required = true, requiredMessage = "用户信息不能为空"),
			@Rule(name = "pageNum", required = true, requiredMessage = "页数开始数据不能为空",digist = true, digistMessage="数据类型错误"),
			@Rule(name = "pageSize", required = true, requiredMessage = "分页大小不能为空",digist = true, digistMessage="数据类型错误")
			})
	ResultVo getInvestListDetail(Map<String ,Object> paramsMap)throws SLException;
	
}
