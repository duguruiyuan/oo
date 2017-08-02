/** 
 * @(#)FixedInvestRepositoryCustom.java 1.0.0 2015年8月15日 上午11:40:21  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository.custom;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.slfinance.exception.SLException;

/**   
 * 定期宝业务列表、统计、交易、投资记录接口
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年8月15日 上午11:40:21 $ 
 */
public interface FixedInvestRepositoryCustom {

	/**
	 * 定期宝分页列表查询
	 * 
	 * @param paramsMap
	 * 	      <ul>
	 *	     	<li>pageNum    当前页数从0开始	{@link java.lang.Integer}</li>
	 *	     	<li>pageSize   每页显示数据       	{@link java.lang.Integer}</li>
	 *	     	<li>productList产品名称         	{@link java.lang.Integer}</li>
	 *        </ul>
	 * 
	 * @return 
	 *         <ul>
	 *		  		<li>id 	                              产品ID		{@link java.lang.String}</li>
	 *	     		<li>productName     投资名称		{@link java.util.List}</li>
	 *	     		<li>productType     投资类型		{@link java.lang.String}</li>
	 *	     		<li>productDesc 	简介(描述)	{@link java.lang.String}</li>
	 *	     		<li>yearRate		年化收益      	{@link java.math.BigDecimal}</li>
	 *	     		<li>awardRate		奖励利率      	{@link java.math.BigDecimal}</li>
	 *	     		<li>typeTerm		投资期限      	{@link java.lang.Integer}</li>
	 *	     		<li>useableAmount   剩余金额		{@link java.math.BigDecimal}</li>
	 *	     		<li>productCat		产品标示		{@link java.lang.String}</li>
	 *         </ul>
	 * @throws SLException
	 */
	Page<Map<String, Object>> getFixedInvestListPage(Map<String,Object> paramsMap) throws SLException;
	
	/**
	 * 定期宝分页列表查询--按照欢迎程序
	 * 
	 * @param paramsMap
	 * 	      <ul>
	 *	     	<li>pageNum    当前页数从0开始	{@link java.lang.Integer}</li>
	 *	     	<li>pageSize   每页显示数据       	{@link java.lang.Integer}</li>
	 *	     	<li>productList产品名称         	{@link java.lang.Integer}</li>
	 *        </ul>
	 * 
	 * @return 
	 *         <ul>
	 *		  		<li>id 	                              产品ID		{@link java.lang.String}</li>
	 *	     		<li>productName     投资名称		{@link java.util.List}</li>
	 *	     		<li>productType     投资类型		{@link java.lang.String}</li>
	 *	     		<li>productDesc 	简介(描述)	{@link java.lang.String}</li>
	 *	     		<li>yearRate		年化收益      	{@link java.math.BigDecimal}</li>
	 *	     		<li>awardRate		奖励利率      	{@link java.math.BigDecimal}</li>
	 *	     		<li>typeTerm		投资期限      	{@link java.lang.Integer}</li>
	 *	     		<li>useableAmount   剩余金额		{@link java.math.BigDecimal}</li>
	 *         </ul>
	 * @throws SLException
	 */
	Page<Map<String, Object>> getInvestListByFavoPage(Map<String,Object> paramsMap) throws SLException;
	
	/**
	 * 定期宝分页列表查询--手机端返回已投比例
	 * 
	 * @param paramsMap
	 * 	      <ul>
	 *	     	<li>pageNum    当前页数从0开始	{@link java.lang.Integer}</li>
	 *	     	<li>pageSize   每页显示数据       	{@link java.lang.Integer}</li>
	 *	     	<li>productList产品名称         	{@link java.lang.Integer}</li>
	 *        </ul>
	 * 
	 * @return 
	 *         <ul>
	 *		  		<li>id 	                              产品ID		{@link java.lang.String}</li>
	 *	     		<li>productName     投资名称		{@link java.util.List}</li>
	 *	     		<li>productType     投资类型		{@link java.lang.String}</li>
	 *	     		<li>productDesc 	简介(描述)	{@link java.lang.String}</li>
	 *	     		<li>yearRate		年化收益      	{@link java.math.BigDecimal}</li>
	 *	     		<li>awardRate		奖励利率      	{@link java.math.BigDecimal}</li>
	 *	     		<li>typeTerm		投资期限      	{@link java.lang.Integer}</li>
	 *	     		<li>useableAmount   剩余金额		{@link java.math.BigDecimal}</li>
	 *	     		<li>investedScale   已投比例		{@link java.math.BigDecimal}</li>
	 *         </ul>
	 * @throws SLException
	 */
	Page<Map<String, Object>> getInvestPage(Map<String,Object> paramsMap) throws SLException;
	
	/**
	 * 根据产品id查询该产品的投资记录
	 * 
	 * @author zhangzs
	 * @param paramsMap
	 * 	  	 <ul>
	 *	     	<li>id		产品ID		{@link java.lang.String}</li>
	 *	     	<li>pageNum 当前页数从0开始	{@link java.lang.Integer}</li>
	 *	     	<li>pageSize每页显示数据       	{@link java.lang.Integer}</li>
	 *	     	<li>termName当前期数       	{@link java.lang.String}</li>
	 *        </ul>
	 * 
	 * @return
	 * 		<li>loginName		昵称	 		{@link java.lang.String}</li>
	 * 		<li>investAmount	投资金额		{@link java.math.BigDecimal}</li>
	 * 		<li>createDate		创建日期		{@link java.util.Date}</li>
	 * 
	 * @throws SLException
	 */
	Page<Map<String,Object>> findInvestList(Map<String, Object> paramsMap) throws SLException;
	
	/**
	 * 根据产品类型查询该产品的投资记录
	 * 
	 * @author zhangzs
	 * @param paramsMap
	 * 	  	 <ul>
	 *	     	<li>productList	产品类型		{@link java.util.List}</li>
	 *	     	<li>pageNum 	当前页数从0开始	{@link java.lang.Integer}</li>
	 *	     	<li>pageSize	每页显示数据       	{@link java.lang.Integer}</li>
	 *        </ul>
	 * 
	 * @return
	 * 		<li>loginName		昵称	 		{@link java.lang.String}</li>
	 * 		<li>investAmount	投资金额		{@link java.math.BigDecimal}</li>
	 * 		<li>createDate		创建日期		{@link java.util.Date}</li>
	 * 		<li>productCat		产品标识		{@link java.lang.String}</li>
	 * 
	 * @throws SLException
	 */
	Page<Map<String,Object>> findInvestListByProduct(Map<String, Object> paramsMap) throws SLException;
	
	/**
	 * 获取产品类型的最大利率、最小利率、最大奖励利率、最小奖励利率
	 * 
	 * @author zhangzs
	 * @Date 2015年8月20日
	 * @param typeName
	 * 	  	 <ul>
	 *	     	<li>typeName 	产品类型	{@link java.lang.String}</li>
	 *        </ul>
	 * 
	 * @return		 
	 * 		<li>minYearRate		最小收益率	 	{@link java.math.BigDecimal}</li>
	 * 		<li>maxYearRate		最大收益率		{@link java.math.BigDecimal}</li>
	 * 		<li>minAwardRate	最小奖励利率	{@link java.math.BigDecimal}</li>
	 * 		<li>maxAwardRate	最大奖励利率	{@link java.math.BigDecimal}</li>
	 * @throws SLException 
	 */
	Map<String,Object> getProductRate(String typeName) throws SLException;
	
	/**
	 * 获得产品的已投资金额
	 * 
	 * @author zhangzs
	 * @Date 2015年8月20日
	 * @param 
	 * 	  	  <ul>
	 *	     	<li>typeNameList 	产品类型集合	{@link java.util.List}</li>
	 *        </ul>
	 * @return
	 * @throws SLException
	 */
	BigDecimal getInvestedAmount(List<String> typeNameList)throws SLException;
	
	/**
	 * 获取产品的预计收益
	 * 
	 * @author zhangzs
	 * @Date 2015年8月21日
	 * @param 
	 * 	  	  <ul>
	 *	     	<li>typeNameList 	产品类型集合	{@link java.util.List}</li>
	 *	     	<li>investStatus 	投资状态集合	{@link java.util.List}</li>
	 *	     	<li>custId 			用户ID		{@link java.util.List}</li>
	 *	     	<li>date 		当前天数		{@link java.util.Date}</li>
	 *        </ul>
	 * @return
	 * @throws SLException
	 */
	BigDecimal getPreIncomeAllProduct(List<String> typeNameList,List<String> investStatus,String custId,Date date)throws SLException;
	
	
	/**
	 * 查询客户某一产品、某一期的累计投资金额
	 * 
	 * @author zhangzs
	 * @Date 2015年8月21日
	 * @param 
	 * 	  	  <ul>
	 *	     	<li>custId 			用户ID		{@link java.lang.String}</li>
	 *	     	<li>typeNameList 	产品类型集合	{@link java.util.List}</li>
	 *	     	<li>investStatusList投资状态集合	{@link java.util.List}</li>
	 *        </ul>
	 * @return
	 * @throws SLException
	 */
	BigDecimal getAtonedAmount(String custId, List<String> typeNameList,List<String> investStatusList);

	/**
	 * @author gaoll
	 * @date 2015年11月11日 上午11:15:07
	 * @param 
	 * 	  	<ul>
	 *	    	<li>pageNum 		当前页数从0开始			{@link java.lang.Integer}</li>
	 *	     	<li>pageSize 		每页显示数据大小			{@link java.lang.Integer}</li>
	 *	     	<li>productList 	产品类型集合				{@link java.util.List}</li>
	 *      </ul>
	 * @return
	 * 		<ul>
	 *      	<li>result {@link com.slfinance.vo.ResultVo} 返回结果</li>
	 *         	<ul>
	 *         		<li>result.success 	是否成功 		{@link java.lang.Boolean} required</li>
	 *         		<li>result.data		产品列表数据 	{@link java.util.List}</li>
	 *         		<ul>
	 *		  			<li>id 	                        产品ID		{@link java.lang.String}</li>
	 *	     			<li>productType     产品类型		{@link java.lang.String}</li>
	 *	     			<li>productName     投资名称		{@link java.lang.String}</li>
	 *	     			<li>productDesc 	简介(描述)	{@link java.lang.String}</li>
	 *	     			<li>yearRate		年化利率      	{@link java.math.BigDecimal}</li>
	 *	     			<li>awardRate		奖励利率    	{@link java.math.BigDecimal}</li>
	 *	     			<li>typeTerm		投资期限      	{@link java.lang.Integer}</li>
	 *	 	     		<li>useableAmount   剩余金额		{@link java.math.BigDecimal}</li>
	 *	 	     		<li>investedScale   已投比例		{@link java.math.BigDecimal}</li>
	 *    	</ul>
	 * @throws SLException
	 */
	Page<Map<String, Object>> getFixedProductsListPage(Map<String, Object> paramsMap) throws SLException;
	
	/**
	 * 获取精选页面
	 *
	 * @author  wangjf
	 * @date    2015年12月16日 下午2:07:46
	 * @param paramsMap
	 * @return
	 * @throws SLException
	 */
	List<Map<String, Object>> getPriorProductList(Map<String, Object> paramsMap) throws SLException;
	
	/**
	 * 获取产品利率
	 *
	 * @author  wangjf
	 * @date    2015年12月16日 下午4:00:20
	 * @param paramsMap
	 * 		<tt>productName： String:产品名称</tt><br>
	 *      <tt>minYearRate： String:年化利率</tt><br>
	 *      <tt>awardRate： String:奖励利率 </tt><br>
	 * @return
	 * @throws SLException
	 */
	List<Map<String, Object>> getProdctYearRateList(Map<String, Object> paramsMap) throws SLException;
	
}
