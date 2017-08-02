/** 
 * @(#)AppMarketingActivitiesService.java 1.0.0 2015年6月3日 上午9:38:07  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */

package com.slfinance.shanlincaifu.service.mobile;

import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.vo.ResultVo;

/**
 * 手机端活动中心业务接口
 * 
 * @author zhangzs
 * @version $Revision:1.0.0, $Date: 2015年6月3日 上午9:38:07 $
 */
public interface AppMarketingActivitiesService {

	/**
	 * 活动中心首页展示
	 * 
	 * @param paramsMap
	 *          <ul>
	 *            <li>custId 当前登陆用户{@link java.lang.String}</li>
	 *          </ul>
	 * 
	 * @return
	 * 	 		<li>recommendAmount  我的推荐金额   {@link java.math.BigDecimal}</li>
	 *	     	<li>experienceAmount 我的体验金金额{@link java.math.BigDecimal}</li>
	 * @throws SLException
	 */
	ResultVo getMarketingActivitiesInfo(Map<String, Object> paramsMap)throws SLException;
	
	/**
	 * 我的推荐列表分页
	 * 
	 * @param paramsMap
	 *          <ul>
	 *            <li>start  分页开始数（当前页数-1）	{@link java.lang.String}</li>
	 *            <li>length 分页大小				{@link java.lang.String}</li>
	 *            <li>custId 	      当前登陆用户		{@link java.lang.String}</li>
	 *            <li>spreadLevel 推荐级别        		{@link java.lang.String} 可选</li>
	 *          </ul>
	 * 
	 * @return <ul>
	 *         <li>iTotalDisplayRecords 总记录数 {@link java.lang.Integer}</li>
	 *         <li>data {@link java.util.Map} 分页记录结果集</li>
	 *         <ul>
	 *         <li>{@link java.util.Map} 每行记录</li>
	 *         <ul>
	 *		  	<li>rewardSettle 	已结算金额  	{@link java.lang.String}</li>
	 *         </ul> 
	 *         <ul>
	 *	     	<li>rewardNotSettle 未结算金额	{@link java.math.BigDecimal}</li>
	 *         </ul> 
     *         <ul>
     *         		<li>{@link java.util.List} 推荐记录记录</li>
     *         		</ul>
	 *		  			<li>loginName 	推荐用户 	{@link java.lang.String}</li>
	 *	     			<li>registDate  推荐日期        {@link java.util.Date}</li>
	 *	     			<li>spreadLevel 推荐级别        {@link java.lang.String}</li>
	 *	     			<li>awardAmount 奖励金额	{@link java.math.BigDecimal}</li>
	 *         		</ul> 
     *         <ul>
	 *        </ul>
	 *        
	 * @throws SLException
	 */
	ResultVo getRecommendList(Map<String, Object> paramsMap)throws SLException;
	
	/**
	 * 我的体验金信息
	 * 
	 * @param paramsMap
	 *          <ul>
	 *            <li>custId 当前登陆用户{@link java.lang.String}</li>
	 *          </ul>
	 * 
	 * @return 
	 *		  	<li>ExperienceGoldIncome 体验金收益 	{@link java.math.BigDecimal}</li>
	 *	     	<li>ExperienceGoldCount  体验金总额    	{@link java.math.BigDecimal}</li>
	 *	     	<li>ExperienceGoldUnUsed 可用体验金    	{@link java.math.BigDecimal}</li>
	 *	     	<li>ExperienceGoldUsed   已用体验金		{@link java.math.BigDecimal}</li>
	 *        
	 * @throws SLException
	 */
	ResultVo getExperAmountInfo(Map<String, Object> paramsMap)throws SLException;
	
	/**
	 * 我的体验金信息-分页列表
	 * 
	 * @param paramsMap
	 *          <ul>
	 *            <li>custId 当前登陆用户			{@link java.lang.String}</li>
	 *            <li>start  分页开始数（当前页数-1）	{@link java.lang.String}</li>
	 *            <li>length 分页大小				{@link java.lang.String}</li>
	 *          </ul>
	 * 
	 * @return 
	 * 		  <ul>
	 *         <li>iTotalDisplayRecords 总记录数 {@link java.lang.Integer}</li>
	 *         <li>data {@link java.util.List} 分页记录结果集</li>
	 *         <ul>
	 *         <li>{@link java.util.Map} 每行记录</li>
     *         <ul>
	 *		  	<li>receiveAmount		领取金额 	{@link java.math.BigDecimal}</li>
	 *	     	<li>usableAmount  		使用金额    	{@link java.math.BigDecimal}</li>
	 *	     	<li>source             	来源    	{@link java.lang.String}</li>
	 *	     	<li>receiveDate	                        使用日期	{@link java.util.Date}</li>
	 *        </ul> 
	 *        </ul>
	 *        
	 * @throws SLException
	 */
	ResultVo getCustExperienceList(Map<String, Object> paramsMap)throws SLException;
	
	/**
	 * 我的体验金信息、分页列表
	 * 
	 * @param paramsMap
	 *          <ul>
	 *            <li>custId 当前登陆用户			{@link java.lang.String}</li>
	 *            <li>start  分页开始数（当前页数-1）	{@link java.lang.String}</li>
	 *            <li>length 分页大小				{@link java.lang.String}</li>
	 *          </ul>
	 * 
	 * @return 
	 * 			<ul>
	 *		  		<ul>amountMap 	                           体验金信息             {@link java.util.Map}</ul>
	 *		  		<li>ExperienceGoldIncome 体验金收益 	{@link java.math.BigDecimal}</li>
	 *	     		<li>ExperienceGoldCount  体验金总额    	{@link java.math.BigDecimal}</li>
	 *	     		<li>ExperienceGoldUnUsed 可用体验金    	{@link java.math.BigDecimal}</li>
	 *	     		<li>ExperienceGoldUsed   已用体验金		{@link java.math.BigDecimal}</li>
	 * 			</ul>
	 * 			<ul>
	 *		  		<ul>listMap 	                           分页列表信息	{@link java.util.List}</ul>
	 * 		  			<ul>
	 *         				<li>iTotalDisplayRecords 总记录数 {@link java.lang.Integer}</li>
	 *         				<li>data {@link java.util.List} 分页记录结果集</li>
	 *         			<ul>
	 *         				<li>{@link java.util.Map} 每行记录</li>
     *         			<ul>
	 *		  				<li>receiveAmount		领取金额 	{@link java.math.BigDecimal}</li>
	 *	     				<li>usableAmount  		使用金额    	{@link java.math.BigDecimal}</li>
	 *	     				<li>source             	来源    	{@link java.lang.String}</li>
	 *	     				<li>receiveDate			使用日期	{@link java.util.Date}</li>
	 *        			</ul> 
	 *       		</ul>
	 * 			</ul>
	 *        
	 * @throws SLException
	 */
	ResultVo  getExpAmountAndListInfo(Map<String, Object> paramsMap)throws SLException;
	
	/**
	 * 我的推荐列表分页
	 * 
	 * @param paramsMap
	 *          <ul>
	 *            <li>start  分页开始数（当前页数-1）	{@link java.lang.String}</li>
	 *            <li>length 分页大小				{@link java.lang.String}</li>
	 *            <li>custId 当前登陆用户		    {@link java.lang.String}</li>
	 *          </ul>
	 * 
	 * @return <ul>
	 *         <li>iTotalDisplayRecords 总记录数 {@link java.lang.Integer}</li>
	 *         <li>data {@link java.util.Map} 分页记录结果集</li>
	 *         <ul>
	 *         <li>{@link java.util.Map} 每行记录</li>
	 *         <ul>
	 *		  	<li>amount		现金奖励  	{@link java.lang.String}</li>
	 *         </ul> 
	 *         <ul>
	 *	     	<li>expAmount	体验金奖励	{@link java.math.BigDecimal}</li>
	 *         </ul> 
     *         <ul>
     *         		<li>{@link java.util.List} 推荐记录记录</li>
     *         		</ul>
	 *		  			<li>loginName 		推荐用户 		{@link java.lang.String}</li>
	 *	     			<li>registDate  	推荐日期        	{@link java.util.Date}</li>
	 *	     			<li>spreadLevel 	推荐级别        	{@link java.lang.String}</li>
	 *	     			<li>awardAmount 	现金奖励金额	{@link java.math.BigDecimal}</li>
	 *	     			<li>experienceAmount体验金金额		{@link java.math.BigDecimal}</li>
	 *         		</ul> 
     *         <ul>
	 *        </ul>
	 *        
	 * @throws SLException
	 */
	ResultVo findRecommendList(Map<String, Object> paramsMap)throws SLException;
}
