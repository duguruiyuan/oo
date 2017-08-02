/** 
 * @(#)IndexService.java 1.0.0 2015年11月9日 下午5:13:05  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service;

import com.slfinance.exception.SLException;
import com.slfinance.vo.ResultVo;

/**   
 * 首页服务接口
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年11月9日 下午5:13:05 $ 
 */
public interface IndexService {

	/**
	 * 查询首页产品信息
	 *
	 * @author  wangjf
	 * @date    2015年11月9日 下午6:05:46
	 * @param 
	 * @return
	 * 		   <ul>
	 *         <li>result {@link com.slfinance.vo.ResultVo} 返回结果</li>
	 *         <ul>
	 *         <li>result.success 	是否成功 		{@link java.lang.Boolean} required</li>
	 *         <li>result.data		产品列表数据 	{@link java.util.List}</li>
	 *         	<ul>
	 *		  		<li>id 	                              产品ID		{@link java.lang.String}</li>
	 *	     		<li>productType     产品类型		{@link java.lang.String}</li>
	 *	     		<li>productName     投资名称		{@link java.lang.String}</li>
	 *	     		<li>productDesc 	简介(描述)	{@link java.lang.String}</li>
	 *	     		<li>yearRate		年化利率      	{@link java.math.BigDecimal}</li>
	 *	     		<li>awardRate		奖励利率    	{@link java.math.BigDecimal}</li>
	 *	     		<li>typeTerm		投资期限      	{@link java.lang.Integer}</li>
	 *	 	     	<li>useableAmount   剩余金额		{@link java.math.BigDecimal}</li>
	 *	 	     	<li>investedScale   已投比例		{@link java.math.BigDecimal}</li>
	 *         	</ul>
	 * @throws SLException 
	 */
	public ResultVo queryProduct() throws SLException;
}
