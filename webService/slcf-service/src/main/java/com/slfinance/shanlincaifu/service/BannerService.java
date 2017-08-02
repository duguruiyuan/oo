/** 
 * @(#)BannerService.java 1.0.0 2015年10月16日 下午1:50:50  
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
 * Banner服务
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年10月16日 下午1:50:50 $ 
 */
public interface BannerService {

	/**
	 * 保存Banner
	 *
	 * @author  wangjf
	 * @date    2015年10月16日 下午1:52:05
	 * @param params
	 *            <tt>id： String:Banner的id，(可以为空，为空时表示新增，不为空则表示修改)</tt><br>
	 *            <tt>bannerTitle： String:Banner标题</tt><br>
	 *            <tt>bannerUrl： String:Banner跳转地址</tt><br>
	 *            <tt>bannerSort： String:Banner排序</tt><br>
	 *            <tt>appSource： String:来源（web,ios,andriod,wap之一）</tt><br>
	 *            <tt>bannerImagePath： String:图片路径</tt><br>
	 *            <tt>userId： String:操作员ID</tt><br>
	 *            <tt>bannerType： String:类型(外部、内部)</tt><br>
	 *            <tt>tradeType： String:交易类型(首页、引导页、启动页)</tt><br>
	 *            <tt>isShare： String:是否分享</tt><br>
	 * @return
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "bannerTitle", required = true, requiredMessage = "Banner标题不能为空!"), 
			@Rule(name = "bannerUrl", required = true, requiredMessage = "Banner跳转地址不能为空!"), 
			@Rule(name = "bannerSort", required = true, requiredMessage = "Banner排序不能为空!", number = true, numberMessage = "Banner排序必须为数字"), 
			@Rule(name = "appSource", required = true, requiredMessage = "Banner来源不能为空!", inRange = { "web", "wap", "android", "ios", "winphone", "others", "android-jirong", "pos" }, inRangeMessage = "Banner来源不在区间内"),
			@Rule(name = "bannerImagePath", required = true, requiredMessage = "Banner图片路径不能为空!"),
			@Rule(name = "userId", required = true, requiredMessage = "操作员ID不能为空!"),
			@Rule(name = "bannerType", required = true, requiredMessage = "类型不能为空!"),
			@Rule(name = "tradeType", required = true, requiredMessage = "交易类型不能为空!")
//			@Rule(name = "isShare", required = true, requiredMessage = "是否分享不能为空!")
			})
	public ResultVo saveBanner(Map<String, Object> params) throws SLException;
	
	/**
	 * 更新排序/失效/发布Banner
	 *
	 * @author  wangjf
	 * @date    2015年10月16日 下午1:52:59
	 * @param params
	 * 		<tt>id： String:Banner的id</tt><br>
	 * 		<tt>bannerSort： String:Banner排序</tt><br>
	 *      <tt>bannerStatus： String:banner状态</tt><br>
	 * @return
	 * @throws SLException
	 */
	public ResultVo publishBanner(Map<String, Object> params) throws SLException;
	
	/**
	 * 查询Banner
	 *
	 * @author  wangjf
	 * @date    2015年10月16日 下午1:53:39
	 * @param params
	 * 		<tt>start：int:分页起始页</tt><br>
	  		<tt>length：int:每页长度</tt><br>
	 *      <tt>bannerTitle： String:来源（web,ios,andriod,wap之一）</tt><br>
	 * 		<tt>appSource： String:来源（web,ios,andriod,wap之一）</tt><br>
	 *      <tt>bannerStatus： String:banner状态</tt><br>
	 *      <tt>tradeType： String:交易类型(首页、引导页、启动页)</tt><br>
	 * @return
	 * 		<tt>iTotalDisplayRecords: 总条数
	 * 		<tt>data： List<Map<String, Object>>：banner数据
	 * 					<tt>bannerTitle： String:Banner标题</tt><br>
	 * 				    <tt>bannerUrl： String:Banner跳转地址</tt><br>
	 *                  <tt>bannerSort： String:Banner排序</tt><br>
	 *                  <tt>bannerImagePath： String:图片路径</tt><br>
	 *                  <tt>bannerType： String:类型(外部、内部)</tt><br>
	 *                  <tt>tradeType： String:交易类型(首页、引导页、启动页)</tt><br>
	 *                  <tt>bannerContent： String:活动内容</tt><br>
	 *                  <tt>isShare： String:是否分享</tt><br>
	 * 		</tt><br>
	 * @throws SLException
	 */
	public Map<String, Object> queryBanner(Map<String, Object> params) throws SLException;
	
	
	/**
	 * 查询App的Banner
	 *
	 * @author  wangjf
	 * @date    2015年10月27日 下午3:18:30
	 * @param params
	 * 			<tt>appSource： String:来源（web,ios,andriod,wap之一）</tt><br>
	 * @return
	 * 	  		<tt>data： List<Map<String, Object>>：banner数据
	 * 					<tt>bannerTitle： String:Banner标题</tt><br>
	 * 				    <tt>bannerUrl： String:Banner跳转地址</tt><br>
	 *                  <tt>bannerSort： String:Banner排序</tt><br>
	 *                  <tt>bannerImagePath： String:图片路径</tt><br>
	 *                  <tt>bannerType： String:类型(外部、内部)</tt><br>
	 *                  <tt>tradeType： String:交易类型(首页、引导页、启动页)</tt><br>
	 *                  <tt>bannerContent： String:活动内容</tt><br>
	 *                  <tt>isShare： String:是否分享</tt><br>
	 * 		     </tt><br>
	 * @throws SLException
	 */
	public ResultVo queryAppBanner(Map<String, Object> params) throws SLException;
	
	/**
	 * 通过ID查询Banner
	 *
	 * @author  wangjf
	 * @date    2015年10月28日 下午3:27:59
	 * @param params
	 * 		<tt>id： String:bannerId</tt><br>
	 * @return
	 * 		<tt>id： String:Banner的id</tt><br>
     *      <tt>bannerTitle： String:Banner标题</tt><br>
     *      <tt>bannerUrl： String:Banner跳转地址</tt><br>
	 *      <tt>bannerSort： String:Banner排序</tt><br>
	 *      <tt>appSource： String:来源（web,ios,andriod,wap之一）</tt><br>
	 *      <tt>bannerImagePath： String:图片路径</tt><br>
	 *      <tt>bannerStatus： String:Banner状态</tt><br>   
	 *      <tt>bannerType： String:类型(外部、内部)</tt><br>
	 *      <tt>tradeType： String:交易类型(首页、引导页、启动页)</tt><br> 
	 *      <tt>bannerContent： String:活动内容</tt><br>
	 *      <tt>isShare： String:是否分享</tt><br>
	 */
	public Map<String, Object> queryBannerById(Map<String, Object> params);
	
	/**
	 * 查询App的引导页
	 *
	 * @author  wangjf
	 * @date    2015年10月27日 下午3:18:30
	 * @param params
	 * 			<tt>appSource： String:来源（web,ios,andriod,wap之一）</tt><br>
	 * @return
	 * 	  		<tt>data： List<Map<String, Object>>：banner数据
	 * 					<tt>bannerTitle： String:Banner标题</tt><br>
	 * 				    <tt>bannerUrl： String:Banner跳转地址</tt><br>
	 *                  <tt>bannerSort： String:Banner排序</tt><br>
	 *                  <tt>bannerImagePath： String:图片路径</tt><br>
	 *                  <tt>bannerType： String:类型(外部、内部)</tt><br>
	 *                  <tt>tradeType： String:交易类型(首页、引导页、启动页)</tt><br>
	 * 		     </tt><br>
	 * @throws SLException
	 */
	public ResultVo queryAppGuide(Map<String, Object> params) throws SLException;
	
	/**
	 * 查询App的启动页
	 *
	 * @author  wangjf
	 * @date    2015年10月27日 下午3:18:30
	 * @param params
	 * 			<tt>appSource： String:来源（web,ios,andriod,wap之一）</tt><br>
	 * @return
	 * 	  		<tt>data： List<Map<String, Object>>：banner数据
	 * 					<tt>bannerTitle： String:Banner标题</tt><br>
	 * 				    <tt>bannerUrl： String:Banner跳转地址</tt><br>
	 *                  <tt>bannerSort： String:Banner排序</tt><br>
	 *                  <tt>bannerImagePath： String:图片路径</tt><br>
	 *                  <tt>bannerType： String:类型(外部、内部)</tt><br>
	 *                  <tt>tradeType： String:交易类型(首页、引导页、启动页)</tt><br>
	 * 		     </tt><br>
	 * @throws SLException
	 */
	public ResultVo queryAppStart(Map<String, Object> params) throws SLException;
	
	/**
	 * 保存活动
	 *
	 * @author  wangjf
	 * @date    2015年12月16日 上午10:01:42
	 * @param params
	 * 		      <tt>id： String:活动的id，(可以为空，为空时表示新增，不为空则表示修改)</tt><br>
	 *            <tt>bannerTitle： String:活动标题</tt><br>
	 *            <tt>bannerUrl： String:活动跳转地址</tt><br>
	 *            <tt>bannerContent： String:活动内容</tt><br>
	 *            <tt>bannerImagePath： String:图片路径</tt><br>
	 *            <tt>userId： String:操作员ID</tt><br>
	 *            <tt>bannerType： String:类型(外部、内部)</tt><br>
	 *            <tt>tradeType： String:交易类型(首页、引导页、启动页)</tt><br>
	 *            <tt>isRecommend： String:是否带推荐码(Y/N/A)</tt><br>
	 * @return
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "bannerTitle", required = true, requiredMessage = "活动标题不能为空!"), 
			@Rule(name = "bannerContent", required = true, requiredMessage = "活动内容不能为空!"), 
			@Rule(name = "bannerUrl", required = true, requiredMessage = "活动跳转地址不能为空!"), 			
			@Rule(name = "bannerImagePath", required = true, requiredMessage = "活动图片路径不能为空!"),
			@Rule(name = "userId", required = true, requiredMessage = "操作员ID不能为空!"),
			@Rule(name = "bannerType", required = true, requiredMessage = "类型不能为空!"),
			@Rule(name = "tradeType", required = true, requiredMessage = "交易类型不能为空!"),
			@Rule(name = "isRecommend", required = true, requiredMessage = "是否带推荐码不能为空!")
			})
	public ResultVo saveActivity(Map<String, Object> params) throws SLException;
	
	/**
	 * 查询活动
	 *
	 * @author  wangjf
	 * @date    2015年12月29日 上午9:34:54
	 * @param params
	 * 			<tt>bannerType：String:活动类型（可以为BannerId或者固定值）</tt><br>
	 * @return
	 * 	  		<tt>data： List<Map<String, Object>>：活动数据
	 * 					<tt>bannerTitle： String:活动标题</tt><br>
	 * 				    <tt>bannerUrl： String:活动链接</tt><br>
	 *                  <tt>bannerSort： String:活动排序</tt><br>
	 *                  <tt>bannerImagePath： String:图片路径</tt><br>
	 *                  <tt>bannerType： String:活动类型</tt><br>
	 *                  <tt>tradeType： String:交易类型(首页、引导页、启动页)</tt><br>
	 *                  <tt>isRecommend： String:是否带推荐码(Y/N/A)</tt><br>
	 * 		     </tt><br> 			
	 * @throws SLException
	 */
	public ResultVo queryActivity(Map<String, Object> params) throws SLException;
	
	/**
	 * 保存活动连接(App需跳转的H5页面地址)
	 *
	 * @author  wangjf
	 * @date    2015年12月30日 下午2:44:33
	 * @param params
	 * 		      <tt>id： String:活动的id，(可以为空，为空时表示新增，不为空则表示修改)</tt><br>
	 *            <tt>bannerTitle： String:活动标题</tt><br>
	 *            <tt>bannerUrl： String:活动跳转地址</tt><br>
	 *            <tt>userId： String:操作员ID</tt><br>
	 *            <tt>tradeType： String:交易类型(活动地址)</tt><br>
	 * @return
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "bannerTitle", required = true, requiredMessage = "活动标题不能为空!"), 
			@Rule(name = "bannerUrl", required = true, requiredMessage = "活动跳转地址不能为空!"), 			
			@Rule(name = "userId", required = true, requiredMessage = "操作员ID不能为空!"),
			@Rule(name = "tradeType", required = true, requiredMessage = "交易类型不能为空!")
			})
	public ResultVo saveActivityUrl(Map<String, Object> params)throws SLException;
	
	/**
	 * 查询活动连接(App需跳转的H5页面地址)
	 *
	 * @author  wangjf
	 * @date    2015年12月30日 下午2:50:02
	 * @param params
	 * 			<tt>bannerType：String:活动类型（标识符）</tt><br>
	 * @return
	 * 	  		<tt>data： List<Map<String, Object>>：活动数据
	 * 					<tt>bannerTitle： String:活动标题</tt><br>
	 * 				    <tt>bannerUrl： String:活动链接</tt><br>
	 *                  <tt>bannerSort： String:活动排序</tt><br>
	 *                  <tt>bannerImagePath： String:图片路径</tt><br>
	 *                  <tt>bannerType： String:活动类型</tt><br>
	 *                  <tt>tradeType： String:交易类型(首页、引导页、启动页)</tt><br>
	 *                  <tt>isRecommend： String:是否带推荐码(Y/N/A)</tt><br>
	 * 		     </tt><br> 	
	 * @throws SLException
	 */
	public ResultVo queryActivityUrl(Map<String, Object> params) throws SLException;
	
	/**
	 * 查询所有banner标题
	 *
	 * @author  wangjf
	 * @date    2015年12月31日 上午11:02:23
	 * @param params
	 * @return
	 * 		<tt>data： List<Map<String, Object>>：活动数据
	 * 		<tt>bannerTitle： String:Banner标题</tt><br>
	 * @throws SLException
	 */
	public ResultVo queryAllBannerTitle(Map<String, Object> params) throws SLException;
}
