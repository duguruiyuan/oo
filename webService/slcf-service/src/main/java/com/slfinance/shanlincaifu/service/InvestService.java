/** 
 * @(#)InvestService.java 1.0.0 2015年4月24日 下午2:11:13  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;

/**   
 * 投资服务
 *  
 * @author  HuYaHui
 * @version $Revision:1.0.0, $Date: 2015年4月24日 下午2:11:13 $ 
 */
public interface InvestService {
	
	/**
	 * 获取活期宝加入记录
	 * @param productId
	 * @return map
	 * <br>
	 *            <tt>key:iTotalDisplayRecords, title:总记录数, type:{@link Long} </tt><br>
	 *            <tt>key:totalInvestPeopleNum, title:总人数, type:{@link BigDecimal} </tt><br>
	 *            <tt>key:totalInvestAmount, title:总投资金额, type:{@link BigDecimal} </tt><br>
	 *<li>{@link java.util.Map} 每行记录</li>
	 *		<ul>
	 *		  	<li>loginName 	     用户名 	  {@link java.lang.String}</li>
	 *	     	<li>investAmount  标题                {@link java.lang.BigDecimal}</li>
	 *	     	<li>createDate    创建时间        {@link java.util.Date}</li>
	 *      </ul>
	 */
	@Rules(rules = { 
			@Rule(name = "productName", required = true, requiredMessage = "产品名称不能为空!"),
	})
	public Map<String, Object> findOwnerList(Map<String, Object> params);
	
	/**
	 * 获取活期宝产品详情
	 * @return
	 * @param Map
	 * <br>
	 *            <tt>key:currUsableValue, title:当前可投额度, type:{@link String} </tt><br>
	 *            <tt>key:partakeOrganizs, title:参与机构, type:{@link String} </tt><br>
	 *            <tt>key:partakeCrerigs, title:参与债权, type:{@link String} </tt><br>
	 *            <tt>key:alreadyInvestPeople, title:参与人数, type:{@link String} </tt><br>
	 *            <tt>key:ensureMethod, title:保障方式, type:{@link String} </tt><br>
	 *            <tt>key:alreadyInvestAmount, title:累计投资额, type:{@link String} </tt><br>
	 *            <tt>key:accumulativeLucre, title:为用户累计赚取, type:{@link String} </tt><br>
	 */
	@Rules(rules = { 
			@Rule(name = "productName", required = true, requiredMessage = "产品名称不能为空!"),
	})
	public Map<String, Object> findBAODetail(Map<String, Object> params);
	
	/**
	 * 投资记录-列表（多表）
	 * @param params
	 * @return
	 */
	public Map<String, Object> findByCondition(Map<String, Object> params);
	
	
	/**
	 * 加入活期宝
	 *
	 * @author  wangjf
	 * @date    2015年4月29日 上午10:43:21
	 * @param params
	 		<tt>custId： String:用户ID</tt><br>
	  		<tt>tradeAmount： String:加入金额</tt><br>
	 * @return
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "custId", required = true, requiredMessage = "客户主键不能为空!"),
			@Rule(name = "tradeAmount", required = true, requiredMessage = "交易金额不能为空!", number = true, numberMessage = "交易金额只能为数字", digist = true, digistMessage = "交易金额只能为整数")			
	})
	public ResultVo joinBao(Map<String, Object> params) throws SLException;
	
	/**
	 * 赎回活期宝（快速赎回）
	 *
	 * @author  wangjf
	 * @date    2015年4月30日 上午11:40:01
	 * @param params
	 		<tt>custId： String:用户ID</tt><br>
	  		<tt>tradeAmount： String:赎回金额</tt><br>
	  		<tt>tradePassword： String:交易密码</tt><br>
	 * @return
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "custId", required = true, requiredMessage = "客户主键不能为空!"),
			@Rule(name = "tradeAmount", required = true, requiredMessage = "交易金额不能为空!", number = true, numberMessage = "交易金额只能为数字"),
			@Rule(name = "tradePassword", required = true, requiredMessage = "交易密码不能为空!")
	})
	public ResultVo withdrawBao(Map<String, Object> params) throws SLException;
	
	/**
	 * 赎回活期宝（普通赎回）
	 *
	 * @author  wangjf
	 * @date    2015年4月30日 上午11:40:01
	 * @param params
	 		<tt>custId： String:用户ID</tt><br>
	  		<tt>tradeAmount： String:赎回金额</tt><br>
	  		<tt>tradePassword： String:交易密码</tt><br>
	 * @return
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "custId", required = true, requiredMessage = "客户主键不能为空!"),
			@Rule(name = "tradeAmount", required = true, requiredMessage = "交易金额不能为空!", number = true, numberMessage = "交易金额只能为数字"),
			@Rule(name = "tradePassword", required = true, requiredMessage = "交易密码不能为空!")
	})
	public ResultVo withdrawBaoNormal(Map<String, Object> params) throws SLException;
	
	/**
	 * 审核赎回活期宝（普通赎回）
	 *
	 * @author  wangjf
	 * @date    2015年5月8日 下午4:20:29
	 * @param params
	 		<tt>atoneId： String:赎回ID</tt><br>	
	  		<tt>auditCustId： String:审核用户ID</tt><br>
	 		<tt>auditStatus： String:审核状态</tt><br>	
	 		<tt>auditMemo： String:审核备注</tt><br>
	 * @return
	 */
	@Rules(rules = { 
			@Rule(name = "atoneId", required = true, requiredMessage = "赎回ID不能为空!"),
			@Rule(name = "auditCustId", required = true, requiredMessage = "审核用户ID不能为空!"),
			@Rule(name = "auditStatus", required = true, requiredMessage = "审核状态不能为空!"),
			@Rule(name = "auditMemo", required = true, requiredMessage = "审核备注不能为空!")
	})
	public ResultVo auditWithdrawBaoNormal(Map<String, Object> params) throws SLException;
	
	/**
	 * 补全赎回详情（定时任务）
	 *
	 * @author  wangjf
	 * @date    2015年5月1日 上午11:11:22
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo fullAtoneDetail(Map<String, Object> params) throws SLException;
	
	/**
	 * 加入体验宝
	 *
	 * @author  wangjf
	 * @date    2015年5月18日 上午11:29:47
	 * @param params
	 		<tt>custId： String:用户ID</tt><br>
	  		<tt>tradeAmount： String:加入金额</tt><br>
	 * @return
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "custId", required = true, requiredMessage = "客户主键不能为空!"),
			@Rule(name = "tradeAmount", required = true, requiredMessage = "交易金额不能为空!", number = true, numberMessage = "交易金额只能为数字", digist = true, digistMessage = "交易金额只能为整数")			
	})
	public ResultVo joinExperienceBao(Map<String, Object> params) throws SLException;
	
	/***
	 * 获取收益发放日期
	 * 
	 * zhangzs
	 * 2015年8月20日
	 * @return
	 * @throws SLException
	 */
	public Date getInvestIncomeDate()throws SLException;
	
	/**
	 * 查询投资排行
	 * @author zhangt
	 * @date  2016年1月12日
	 * @param params
	 * @return
	 */
	public ResultVo queryInvestRankInfo(Map<String, Object> params);
	
	/**
	 * 查询产品利率
	 *
	 * @author  wangjf
	 * @date    2016年1月15日 下午4:23:46
	 * @param params
	 * @return
	 */
	public ResultVo queryProductRate(Map<String, Object> params) throws SLException;
}
