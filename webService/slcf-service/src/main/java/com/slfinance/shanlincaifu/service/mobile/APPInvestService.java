/** 
 * @(#)APPInvestService.java 1.0.0 2015年5月20日 下午5:20:46  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.mobile;

import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.vo.ResultVo;

/**   
 * 手机端我的投资-活期宝业务接口
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年5月20日 下午5:20:46 $ 
 */
public interface APPInvestService {
	
	/**
	 * 账户信息
	 * 
	 * @param 
	 * 	 		<li>custId 		用户ID  			  {@link java.lang.String}</li>
	 * 	 		<li>productType 产品类型( 活期宝或体验宝 ){@link java.lang.String}</li>
	 * 
	 * @return 
	 *         <ul>
	 *         <li>data {@link java.util.Map} 结果集</li>
	 *         <ul>
     *         <ul>
	 * 	 		<li>yestedayIncome      昨日收益        {@link java.math.BigDecimal}</li>
	 *	     	<li>currentAmount       当前总额        {@link java.math.BigDecimal}</li>
	 *	        <li>minYearRate         最小年化利率{@link java.math.BigDecimal}</li>
	 *	        <li>maxYearRate         最大年化利率{@link java.math.BigDecimal}</li>
	 *	     	<li>sumJoinAmount       累计加入金额{@link java.math.BigDecimal}</li>
	 *	     	<li>alreadyInvestAmount 累计投资金额{@link java.math.BigDecimal}</li>
	 *	     	<li>minAwardRate        奖励年化利率 {@link java.math.BigDecimal}</li>
	 *	     	<li>sumRedemptionsAmount累计赎回金额{@link java.math.BigDecimal}</li>
	 *	     	<li>sumIncome           累计收益        {@link java.math.BigDecimal}</li>
	 *	     	<li>loanCount           持有债权数    {@link java.math.BigDecimal}</li>
	 *	     	<li>accountAmount       持有金额        {@link java.math.BigDecimal}</li>
	 *        </ul> 
	 *
	 * @throws SLException
	 */
	public ResultVo accountInfo(Map<String,Object> paramsMap)throws SLException;
	
	/**
	 * 赎回操作详细页面
	 * 
	 * @param 
	 * 	 		<li>custId 用户ID  {@link java.lang.String}</li>
	 * @return 
	 *         <li>data {@link java.util.Map} 结果集</li>
	 *         <ul>
	 *	     	<li>accountAmount 当前持有价值（可赎回金额） {@link java.math.BigDecimal}</li>
	 *         </ul>
	 * @throws SLException
	 */
	public ResultVo redeemInfo(Map<String,Object> paramsMap)throws SLException;
	
	/**
	 * 赎回操作
	 * 
	 * @param 
	 * 	 		<li>custId        用户ID 							{@link java.lang.String}</li>
	 * 	 		<li>tradeAmount   赎回金额   						{@link java.math.BigDecimal}</li>
	 * 	 		<li>tradePassword 交易密码  						{@link java.lang.String}</li>
	 * 	 		<li>redeemType    赎回方式(数据传递‘普通赎回’或‘快速赎回’)  {@link java.lang.String}</li>
	 * @return 
	 *         <li>result 返回结果  {@link com.slfinance.vo.ResultVo} </li>
	 *         <ul>
	 *         <li>result.success 是否成功 {@link java.lang.Boolean} required</li>
	 *         <li>result.message 响应消息 {@link java.lang.String}</li>
	 *         </ul>
	 * 
	 * @throws SLException
	 */
	public ResultVo redeem(Map<String,Object> paramsMap)throws SLException;
	
	/**
	 * 债权组成列表
	 * 
	 * @param 
	 *	     	<li>custId 	         用户的id    {@link java.lang.String}</li>
	 *	     	<li>pageNum    当前页数从0开始{@link java.lang.String}</li>
	 *	     	<li>pageSize   每页显示数据       {@link java.lang.String}</li>
	 * 
	 * @return 
	 *         <li>iTotalDisplayRecords 总记录数            {@link java.lang.Integer}</li>
	 *         <li>data 				分页记录结果集{@link java.util.List} </li>
	 *         <li>{@link java.util.Map} 每行记录</li>
     *         <ul>
	 *		  	<li>id 	                                          债权ID 	  {@link java.lang.String}</li>
	 *	     	<li>loanCode          债权编号        {@link java.lang.String}</li>
	 *	     	<li>yearRate          年化利率        {@link java.math.BigDecimal}</li>
	 *	     	<li>creditRightStatus 借款状态        {@link java.lang.String}</li>
	 *	     	<li>debtSourceCode    债权来源        {@link java.util.Date}</li>
	 *	     	<li>loanAmount        债权金额        {@link java.math.BigDecimal}</li>
	 *	     	<li>alreadyPaymentTerm已还款期数    {@link java.math.BigDecimal}</li>
	 *	     	<li>loanTerm          借款期数        {@link java.math.BigDecimal}</li>
	 *	     	<li>assetTypeCode     资产类型        {@link java.lang.String}</li>
	 *        </ul> 
	 * 
	 * @throws SLException
	 */
	public ResultVo loanList(Map<String,Object> paramsMap)throws SLException;
	
	/**
	 * 购买活期宝页面详细
	 * 
	 * @param paramsMap
	 *	     	<li>custId 	         用户的id    {@link java.lang.String}</li>
	 *	     	<li>用户id不为空时，返回所有字段</li>
	 *	     	<li>用户id为空时，返回交易记录和产品的可购买份额</li>
	 * 
	 * @return
	 *         <li>data {@link java.util.Map} 结果集</li>
	 *         <ul>
	 *	     	<li>minYearRate             最小年化收益率 			{@link java.math.BigDecimal}</li>
	 *	     	<li>maxYearRate     		最大年化收益率 			{@link java.math.BigDecimal}</li>
	 *	     	<li>minAwardRate     		奖励收益率	 			{@link java.math.BigDecimal}</li>
	 *	     	<li>ensureMethod  			保障方式 				{@link java.math.BigDecimal}</li>
	 *	     	<li>                       	投资期限 (活期)			{@link java.math.BigDecimal}</li>
	 *	     	<li>currUsableValue        	剩余可购买份额(个人) 		{@link java.math.BigDecimal}</li>
	 *	     	<li>accountAvailableAmount 	可用余额 				{@link java.math.BigDecimal}</li>
	 *	     	<li>currAllowTenderValue 	当前可购买份额(产品) 		{@link java.math.BigDecimal}</li>
	 *	     	<li>incomeToAmountTime 	首笔收益到账|收益发放日期                     {@link java.math.BigDecimal}</li>
	 *	     	<li>joinedCount 			加入记录                     		{@link java.math.BigDecimal}</li>
	 *	     	<li>						每日收益:根据最小年化收益率和购买金额确定</li>
	 *         </ul>
	 * @throws SLException

	 */
	public ResultVo buyDetailToCurrent(Map<String,Object> paramsMap)throws SLException;

	/**
	 * 购买体验宝页面详细
	 * 
	 * @param paramsMap
	 * 
	 *	     	<li>custId 	         用户的id    {@link java.lang.String}</li>
	 *	 	    <li>用户id不为空时，返回所有字段</li>
	 *	     	<li>用户id为空时，返回交易记录和产品的可购买份额</li>
	 * @return
	 *         <li>data {@link java.util.Map} 结果集</li>
	 *         <ul>
	 *	     	<li>minYearRate             最小年化收益率 			{@link java.math.BigDecimal}</li>
	 *	     	<li>maxYearRate     		最大年化收益率 			{@link java.math.BigDecimal}</li>
	 *	     	<li>minAwardRate     		奖励收益率	 			{@link java.math.BigDecimal}</li>
	 *	     	<li>  			                                     收益结算=日结 			 </li>
	 *	     	<li>                       	投资期限 (15天)			{@link java.math.BigDecimal}</li>
	 *	     	<li>currUsableValue        	剩余可购买份额 (个人)		{@link java.math.BigDecimal}</li>
	 *	     	<li>accountAvailableAmount 	可用体验金 				{@link java.math.BigDecimal}</li>
	 *	     	<li>currAllowTenderValue 	当前可购买份额(产品) 		{@link java.math.BigDecimal}</li>
	 *	     	<li>incomeToAmountTime	 	首笔收益到账|收益发放日期         {@link java.math.BigDecimal}</li>
	 *	 	    <li>joinedCount 			加入记录                     		{@link java.math.BigDecimal}</li>
	 *	     	<li>						每日收益:根据最小年化收益率和购买金额确定</li>
	 *         </ul>
	 * @throws SLException
	 */
	public ResultVo buyDetailToExperience(Map<String,Object> paramsMap)throws SLException;
	
	/**
	 * 购买活期宝
	 * 
	 * @param params
	 *	     	<li>custId 	            用户的id    {@link java.lang.String}</li>
	 *	     	<li>tradeAmount 交易金额              {@link java.lang.String}</li>
	 *	     	<li>investSource交易来源              {@link java.lang.String}</li>
	 * 
	 * @return 
	 *         <li>result {@link com.slfinance.vo.ResultVo} 返回结果</li>
	 *         <ul>
	 *         <li>result.success 是否成功 {@link java.lang.Boolean} required</li>
	 *         <li>result.message 响应消息 {@link java.lang.String}</li>
	 *         </ul>
	 * @throws SLException
	 */
	public ResultVo joinCurrentBao(Map<String, Object> params) throws SLException;
	
	/**
	 * 购买体验宝
	 * 
	 * @param params
	 *	     	<li>custId 	               用户的id    {@link java.lang.String}</li>
	 *	     	<li>tradeAmount  交易金额              {@link java.lang.String}</li>
	 *	  		<li>investSource 交易来源              {@link java.lang.String}</li>
	 *        
	 * @return 
	 *         <li>result {@link com.slfinance.vo.ResultVo} 返回结果</li>
	 *         <ul>
	 *         <li>result.success 是否成功 {@link java.lang.Boolean} required</li>
	 *         <li>result.message 响应消息 {@link java.lang.String}</li>
	 *         </ul>
	 * @throws SLException
	 */
	public ResultVo joinExperienceBao(Map<String, Object> params) throws SLException;
	
	/**
	 * 投资列表-体验宝和活期宝信息详细
	 * 
	 * @param paramsMap
	 * 
	 *	     	<li>productType投资类型(活期宝、体验宝){@link java.lang.String}</li>
	 * @return
	 *         <li>data {@link java.util.Map} 结果集</li>
	 *         <ul>
	 *	     	<li>minYearRate             最小年化收益率 			{@link java.math.BigDecimal}</li>
	 *	     	<li>maxYearRate     		最大年化收益率 			{@link java.math.BigDecimal}</li>
	 *	     	<li>currAllowTenderValue 	当前可购买份额 			{@link java.math.BigDecimal}</li>
	 *	     	<li>joinedCount 	                        加入记录				{@link java.lang.Integer}</li>
	 *         </ul>
	 * @throws SLException
	 */
	public ResultVo investInfo(Map<String, Object> params) throws SLException;
	
	/**
	 * 投资列表-加入记录-加入列表
	 * 
	 * @param paramsMap
	 * 
	 *	     	<li>start      分页开始索引数		{@link java.lang.String}</li>
	 *	     	<li>length     每页显示数量			{@link java.lang.String}</li>
	 *	     	<li>productType投资类型(活期宝、体验宝){@link java.lang.String}</li>
	 * @return
	 *         <li>iTotalDisplayRecords 总记录数            {@link java.lang.Integer}</li>
	 *         <li>data 				分页记录结果集 {@link java.util.List} </li>
	 *         <li>{@link java.util.Map} 每行记录</li>
     *         <ul>
	 *	     	<li>loginName	昵称	 	{@link java.math.BigDecimal}</li>
	 *	     	<li>investAmount加入金额	{@link java.math.BigDecimal}</li>
	 *	     	<li>createDate	加入日期	{@link java.math.BigDecimal}</li>
	 *        </ul>
	 *        
	 * @throws SLException
	 */
	public ResultVo joinListInfo(Map<String, Object> params) throws SLException;
	
	/**
	 * 查询活期宝
	 *
	 * @author  wangjf
	 * @date    2015年6月1日 下午4:26:14
	 * @param params	  		
	 * @return
	  		<tt>currUsableValue： BigDecimal:当前剩余可购买份额</tt><br>
	  		<tt>alreadyInvestPeople： BigDecimal:加入人数</tt><br>
	  		<tt>alreadyInvestAmount： BigDecimal:累计投资额</tt><br>
	  		<tt>yearRate： BigDecimal:年化利率</tt><br>
	 * @throws SLException
	 */
	public ResultVo queryBaoDetail(Map<String, Object> params) throws SLException;
	
	/**
	 * 查询体验宝
	 *
	 * @author  wangjf
	 * @date    2015年6月1日 下午4:27:28
	 * @param params
	 * @return
	  		<tt>currUsableValue： BigDecimal:当前剩余可购买份额</tt><br>
	  		<tt>alreadyInvestPeople： BigDecimal:加入人数</tt><br>
	  		<tt>alreadyInvestAmount： BigDecimal:累计投资额</tt><br>
	  		<tt>yearRate： BigDecimal:年化利率</tt><br>
	 * @throws SLException
	 */
	public ResultVo queryExperienceDetail(Map<String, Object> params) throws SLException;

	/**
	 * 投资首页展示用户购买活期宝记录，活期宝和体验宝产品介绍。
	 * 
	 * @param params
	 * 
	 *	     	<li>start      分页开始索引数{@link java.lang.String}</li>
	 *	     	<li>length     每页显示数量	{@link java.lang.String}</li>
	 * @return
	 * 
	 *         <li>data 购买活期宝记录、产品介绍 {@link  java.util.Map} </li>
	 *         <li>buyList 用户购买记录 {@link java.util.List} </li>
     *        	<ul>
	 *	     		<li>loginName	用户昵称	{@link java.lang.String}</li>
	 *	     		<li>investAmount购买金额	{@link java.math.BigDecimal}</li>
	 *	 	   		<li>productCat  产品标示	{@link java.lang.String}</li>
	 *        	</ul> 
	 *         <li>productList 产品介绍记录 {@link java.util.List}</li>
	 *        	<ul>
	 *	     		<li>procuctName	产品名称	{@link java.lang.String}</li>
	 *	     		<li>minYearRate 年化收益	{@link java.math.BigDecimal}</li>
	 *	     		<li>awardRate   奖励收益	{@link java.math.BigDecimal}</li>
	 *	     		<li>体验宝活期,活期宝15   期限	{@link java.math.BigDecimal}</li>
	 *        	</ul> 
	 *         <li>termProduct 定期宝产品信息 {@link java.util.Map} </li>
	 *        	<ul>
	 *        		<li>procuctId	产品ID   	{@link java.lang.String}</li>
	 *         	 	<li>procuctName	产品名称   	{@link java.lang.String}</li>
	 *         	 	<li>currentTerm	产品期数  	{@link java.lang.String}</li>
	 *	     		<li>yearRate	定期宝年化收益	{@link java.lang.String}</li>
	 *	     		<li>awardRate 奖励的年化收益	{@link java.lang.String}</li>
	 *	     		<li>typeTerm   投资期限	{@link java.lang.String}</li>
	 *	     		<li>investMinAmount 起投金额	{@link java.lang.String}</li>
	 *         		<li>investBearinteMethod  结息日期 {@link java.lang.String}</li>
	 *          	<li>incomeHandleMethod    赎回类型	{@link java.lang.String}</li>
	 *          	<li>planTotalAmount  总投资金额	{@link java.lang.String}</li>
	 *          	<li>alreadyInvestAmount  已投资金额	{@link java.lang.String}</li>
	 *        </ul>
	 * @throws SLException
	 */
	public ResultVo investIndex(Map<String, Object> params)throws SLException;
}
