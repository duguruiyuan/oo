/** 
 * @(#)AfficheInfoService.java 1.0.0 2014年10月15日 上午9:48:19  
 *  
 * Copyright © 2014 善林金融.  All rights reserved.  
 */

package com.slfinance.shanlincaifu.service;

import java.util.List;
import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AfficheInfoEntity;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;

/**
 * 网站公告业务接口
 * 
 * @author zhangzs
 * @version $Revision:1.0.0, $Date: 2014年10月15日 上午9:48:19 $
 */
public interface AfficheInfoService {

	/**
	 * 分页查询网站公告列表
	 * 
	 * 	 * @param paramsMap
	 *            <ul>
	 *            	<li>start        	分页开始记录数 		{@link java.lang.Integer}</li>
	 *              <li>length      	每页显示记录数 		{@link java.lang.Integer}</li>
	 *			  	<li>afficheType 	公告类型			{@link java.lang.String}</li>
	 *			  	<li>afficheStatus   公告状态			{@link java.lang.String}</li>
	 *			  	<li>afficheTitle 	公告标题   			{@link java.lang.String}</li>
	 *            </ul>
	 * @return <ul>
	 *         <li>iTotalDisplayRecords 总记录数 {@link java.lang.Integer}</li>
	 *         <li>data {@link java.util.List} 分页记录结果集</li>
	 *         <ul>
	 *         <li>{@link java.util.Map} 每行记录</li>
     *         <ul>
	 *		  	<li>id 	                              公告ID 	  {@link java.lang.String}</li>
	 *	     	<li>afficheTitle  标题                {@link java.lang.String}</li>
	 *	     	<li>afficheType   类型                {@link java.lang.String}</li>
	 *	     	<li>createUser    创建人            {@link java.lang.String}</li>
	 *	     	<li>createDate    创建时间        {@link java.util.Date}</li>
	 *	     	<li>publishUser   发布人            {@link java.lang.String}</li>
	 *	     	<li>publishTime   发布时间        {@link java.util.Date}</li>
	 *        </ul> 
	 *        </ul>
	 */
	@Rules(rules = {
			@Rule(name = "start", required = true, requiredMessage = "分页数据不能为空"),
			@Rule(name = "length", required = true, requiredMessage = "分页数据不能为空")
			})
	public Map<String,Object> findAllAffiche(Map<String, Object> paramsMap) throws SLException;

	/**
	 * 网站公告--新增
	 * 
	 * @param AfficheInfoEntity
	 *		  	<li>id 	                              公告ID 	   {@link java.lang.String}</li>
	 *	     	<li>afficheTitle  标题                 {@link java.lang.String}</li>
	 *	     	<li>afficheType   类型                 {@link java.lang.String}</li>
	 *	     	<li>afficheContent内容                 {@link java.lang.String}</li>
	 *	     	<li>createUser更新用户id {@link java.lang.String}</li>
	 * @return 
	 *         <li>result {@link com.slfinance.vo.ResultVo} 返回结果</li>
	 *         <ul>
	 *         <li>result.success 是否成功 {@link java.lang.Boolean} required</li>
	 *         <li>result.message 响应消息 {@link java.lang.String}</li>
	 *         </ul>
	 * @throws SLException
	 */
	public ResultVo createAffiche(AfficheInfoEntity afficheInfo) throws SLException;

	/**
	 * 网站公告--查询单个
	 * 
	 * @param id
	 * 	 		<li>id 	                              公告ID 	   {@link java.lang.String}</li>
	 * @return 
	 * 	 		<li>id 	                              公告ID 	  {@link java.lang.String}</li>
	 *	     	<li>afficheTitle  标题                {@link java.lang.String}</li>
	 *	     	<li>afficheType   类型                {@link java.lang.String}</li>
	 *	     	<li>createUser    创建人            {@link java.lang.String}</li>
	 *	     	<li>createDate    创建时间        {@link java.util.Date}</li>
	 *	     	<li>publishUser   发布人            {@link java.lang.String}</li>
	 *	     	<li>publishTime   发布时间        {@link java.util.Date}</li>
	 * @throws SLException
	 */
	public Map<String,Object> findOneAffiche(String id) throws SLException;
	
	/**
	 * 网站公告--查询单个(替换img图片的src增加前缀http://image.shanlinbao.com)
	 * 
	 * @param id
	 * 	 		<li>id 	                              公告ID 	   {@link java.lang.String}</li>
	 * @return 
	 * 	 		<li>id 	                              公告ID 	  {@link java.lang.String}</li>
	 *	     	<li>afficheTitle  标题                {@link java.lang.String}</li>
	 *	     	<li>afficheType   类型                {@link java.lang.String}</li>
	 *	     	<li>createUser    创建人            {@link java.lang.String}</li>
	 *	     	<li>createDate    创建时间        {@link java.util.Date}</li>
	 *	     	<li>publishUser   发布人            {@link java.lang.String}</li>
	 *	     	<li>publishTime   发布时间        {@link java.util.Date}</li>
	 * @throws SLException
	 */
	public Map<String,Object> findOneAfficheReplaceImgSrc(String id) throws SLException;

	/**
	 * 网站公告--修改
	 * 	
	 * @param AfficheInfoEntity
	 *		  	<li>id 	                              公告ID 	   {@link java.lang.String}</li>
	 *	     	<li>afficheTitle  标题                 {@link java.lang.String}</li>
	 *	     	<li>afficheType   类型                 {@link java.lang.String}</li>
	 *	     	<li>afficheContent内容                 {@link java.lang.String}</li>
	 *	     	<li>lastUpdateUser更新用户id {@link java.lang.String}</li>
	 * @return 
	 *         <li>result {@link com.slfinance.vo.ResultVo} 返回结果</li>
	 *         <ul>
	 *         <li>result.success 是否成功 {@link java.lang.Boolean} required</li>
	 *         <li>result.message 响应消息 {@link java.lang.String}</li>
	 *         </ul>
	 * @throws SLException
	 */
	public ResultVo updateAffiche(AfficheInfoEntity afficheInfo) throws SLException;

	/**
	 * 网站公告--失效
	 * 
	 * @param 
	 *         <ul>
	 *		  	<li>id 	       ID 	     {@link java.lang.String}</li>
	 *	     	<li>custId 	         操作用户的id {@link java.lang.String}</li>
	 *         </ul>
	 * @return
	 *         <li>result {@link com.slfinance.vo.ResultVo} 返回结果</li>
	 *         <ul>
	 *         <li>result.success 是否成功 {@link java.lang.Boolean} required</li>
	 *         <li>result.message 响应消息 {@link java.lang.String}</li>
	 *         </ul>
	 * @throws SLException
	 */
	public ResultVo invalidAffiche(String id,String custId) throws SLException;

	/**
	 * 网站公告--发布
	 * 
	 * @param 
	 *         <ul>
	 *		  	<li>id 	       ID 	     {@link java.lang.String}</li>
	 *	     	<li>custId 	         操作用户的id {@link java.lang.String}</li>
	 *         </ul>
	 * @return
	 *         <li>result {@link com.slfinance.vo.ResultVo} 返回结果</li>
	 *         <ul>
	 *         <li>result.success 是否成功 {@link java.lang.Boolean} required</li>
	 *         <li>result.message 响应消息 {@link java.lang.String}</li>
	 *         </ul>
	 * @throws SLException
	 */
	public ResultVo publishAffiche(String id,String custId) throws SLException;

	/**
	 * 网站公告--删除
	 * 
	 * @param 
	 *         <ul>
	 *		  	<li>id 	       ID 	     {@link java.lang.String}</li>
	 *	     	<li>custId 	         操作用户的id {@link java.lang.String}</li>
	 *         </ul>
	 * @return
	 *         <li>result {@link com.slfinance.vo.ResultVo} 返回结果</li>
	 *         <ul>
	 *         <li>result.success 是否成功 {@link java.lang.Boolean} required</li>
	 *         <li>result.message 响应消息 {@link java.lang.String}</li>
	 *         </ul>
	 * @throws SLException
	 */
	public ResultVo deleteAffiche(String id,String custId) throws SLException;
	
	/**
	 * 网站公告--网站最新公告查询	
	 * 
	 * @return 
	 * 	 		<li>id 	                              公告ID 	  {@link java.lang.String}</li>
	 *	     	<li>afficheTitle  标题                {@link java.lang.String}</li>
	 *	     	<li>afficheType   类型                {@link java.lang.String}</li>
	 *	     	<li>createUser    创建人            {@link java.lang.String}</li>
	 *	     	<li>createDate    创建时间        {@link java.util.Date}</li>
	 *	     	<li>publishUser   发布人            {@link java.lang.String}</li>
	 *	     	<li>publishTime   发布时间        {@link java.util.Date}</li>
	 */
	public Map<String,Object> findNewestWebsiteAffiche() throws SLException;
	
	/**
	 * 网站公告--查询行业动态、媒体报道
	 * 
	 * @return 
	 * 	 		<li>id 	                              公告ID 	  {@link java.lang.String}</li>
	 *	     	<li>afficheTitle  标题                {@link java.lang.String}</li>
	 *	     	<li>afficheType   类型                {@link java.lang.String}</li>
	 *	     	<li>createUser    创建人            {@link java.lang.String}</li>
	 *	     	<li>createDate    创建时间        {@link java.util.Date}</li>
	 *	     	<li>publishUser   发布人            {@link java.lang.String}</li>
	 *	     	<li>publishTime   发布时间        {@link java.util.Date}</li>
	 */
	public List<Map<String,Object>> findAffiche() throws SLException;
	
	/**
	 * 读取公告
	 *
	 * @author  wangjf
	 * @date    2015年12月14日 下午6:17:38
	 * @param params
	 * 		<tt>id： String:公告id</tt><br>
	 *      <tt>custId： String:用户id</tt><br>
	 * @return
	 * @throws SLException
	 */
	public ResultVo readAffiche(Map<String, Object> params) throws SLException;
	
	/**
	 * 统计公告数量
	 *
	 * @author  wangjf
	 * @date    2015年12月15日 上午10:13:36
	 * @param params
	 * 		<tt>custId： String:用户id</tt><br>
	 * @return
	 * 		<tt>allAfficheNum： String:所有公告数量</tt><br>	
	 *      <tt>readAfficheNum： String:已读公告数量</tt><br>	
	 *      <tt>unReadAfficheNum： String:未读公告数量</tt><br>	
	 * @throws SLException
	 */
	public Map<String, Object> countAffiche(Map<String, Object> params) throws SLException;
}
