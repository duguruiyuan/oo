/** 
 * @(#)APPUserSafe.java 1.0.0 2015年5月18日 上午10:20:04  
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
 * 用户登陆相关测试
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年5月18日 上午10:20:04 $ 
 */
public interface APPUserSafeService {

	/**
	 * 忘记密码-发送手机验证码
	 * 
	 * @param parasMap
	 *            <ul>
	 *              <li>mobile      	手机号 		{@link java.lang.String}</li>
	 *			  	<li>messageType 	 类型			{@link java.lang.String}</li>
	 *            </ul>
	 * @return
	 */
	public ResultVo checkRegistAndSendSMS(Map<String,Object> parasMap)throws SLException;
	
	/**
	 * 忘记密码-验证码是否正确
	 * 
	 * 	  @param parasMap
	 *            <ul>
	 *              <li>mobile      	手机号 		{@link java.lang.String}</li>
	 *			  	<li>messageType 	                        类型			{@link java.lang.String}</li>
	 *			  	<li>code            公告状态		{@link java.lang.String}</li>
	 *            </ul>
	 * @return
	 * @throws SLException
	 */
	public ResultVo checkCodeAndUserVerified(Map<String,Object> parasMap)throws SLException;
	
	/**
	 * 忘记密码-实名认证
	 * 
	 * 	  	  @param parasMap
	 *            <ul>
	 *              <li>mobile      	手机号 		{@link java.lang.String}</li>
	 *              <li>custName      	用户名 		{@link java.lang.String}</li>
	 *			  	<li>credentialsCode 身份证		{@link java.lang.String}</li>
	 *            </ul>
	 * @return
	 * @throws SLException
	 */
	public ResultVo validateIdentity(Map<String,Object> parasMap)throws SLException;
	
	
	/**
	 * 忘记密码-修改
	 * 
	 * 	  	  @param parasMap
	 *            <ul>
	 *              <li>mobile      手机号{@link java.lang.String}</li>
	 *              <li>password    新密码  {@link java.lang.String}</li>
	 *              <li>smsCode    验证码  {@link java.lang.String}</li>
	 *			  	<li>type        类型      {@link java.lang.String}1代表登陆密码；2代表交易密码</li>
	 *            </ul>
	 * @return
	 * @throws SLException
	 */
	public ResultVo resetPwd(Map<String,Object> parasMap)throws SLException;
	
	/**
	 * 忘记密码-修改(校验验证码)
	 * 
	 * 	  	  @param parasMap
	 *            <ul>
	 *              <li>mobile     		手机号  	{@link java.lang.String}</li>
	 *              <li>password   		新密码  	{@link java.lang.String}</li>
	 *              <li>smsCode    		验证码  	{@link java.lang.String}</li>
	 *			  	<li>type			类型		{@link java.lang.String}1代表登陆密码；2代表交易密码</li>
	 *			  	<li>operIpaddress	操作IP	{@link java.lang.String}</li>
	 *            </ul>
	 * @return
	 * @throws SLException
	 */
	public ResultVo resetPassWord(Map<String,Object> parasMap)throws SLException;
	
	/**
	 * 重设交易密码
	 * 
	 * @author zhangzs
	 * @date 2015年5月29日 上午11:33:31
	 * @param params
	 *            <ul>
	 *            <li>id 			用户ID 	{@link java.lang.String}</li>
	 *            <li>tradePassword 交易密码 	{@link java.lang.String}</li>
	 *            <li>smsCode 		验证码 	{@link java.lang.String}</li>
	 *            <li>mobile 		手机号 	{@link java.lang.String}</li>
	 *            </ul>
	 * @return <li>result {@link com.slfinance.vo.ResultVo} 返回结果</li>
	 *         <ul>
	 *         <li>result.success 是否成功 {@link java.lang.Boolean} required</li>
	 *         <li>result.message 响应消息 {@link java.lang.String}</li>
	 *         </ul>
	 * @throws SLException
	 */
	public ResultVo resetTradePassword(Map<String, Object> params) throws SLException;
	
	/**
	 * 修改手机-修改绑定手机-获取验证码
	 * 
	 * 	  	  @param parasMap
	 *            <ul>
	 *              <li>custId      用户ID{@link java.lang.String}</li>
	 *              <li>messageType 类型      {@link java.lang.String}</li>
	 *			  	<li>mobile      手机号   {@link java.lang.String}</li>
	 *            </ul>
	 * @return
	 * @throws SLException
	 */
	public ResultVo validCode(Map<String,Object> parasMap)throws SLException;
	
	/**
	 * 验证码发送次数
	 * 
	 *@param parasMap
	 *            <ul>
	 *              <li>mobile      	手机号 		{@link java.lang.String}</li>
	 *			  	<li>smsType 	             类型			{@link java.lang.String}</li>
	 *            </ul>
	 * @return
	 */
	public ResultVo codeSendCount(Map<String, Object> parasMap)throws SLException;

	/**
	 * 查询客户基本信息和联系人基本信息(善林财富二期)
	 * 
	 * @author zhangzs
	 * @date 2015年7月09日 上午10:08:31
	 * 
	 * @param 
	 * 	 		<li>custId 		用户ID  			  {@link java.lang.String}</li>
	 * 
	 * @return 
     *         <ul>
	 * 	 		<li>id      				用户id	{@link java.lang.String}</li>
	 *	     	<li>custName       			名称		{@link java.lang.String}</li>
	 *	     	<li>credentialsCode       	身份证	{@link java.lang.String}</li>
	 *	        <li>mobile         			手机号	{@link java.lang.String}</li>
	 *	        <li>email          			邮箱		{@link java.lang.String}</li>
	 *	     	<li>natvicePlaceProvince 	省		{@link java.lang.String}</li>
	 *	     	<li>natvicePlaceCity		市		{@link java.lang.String}</li>
	 *	     	<li>natvicePlaceCounty		县		{@link java.lang.String}</li>
	 *	     	<li>communAddress			通信地址	{@link java.lang.String}</li>
	 *	     	<li>qqCode					QQ		{@link java.lang.String}</li>
	 *	     	<li>zipCode					邮编		{@link java.lang.String}</li>
	 *			<li>contanctInfo {@link java.util.Map} 用户基本信息</li>
	 *				<ul>
	 *	     			<li>id                  联系人ID  {@link java.lang.String}</li>
	 *	     			<li>contactName         名称         	{@link java.lang.String}</li>
	 *	     			<li>ralationType        关系         	{@link java.lang.String}</li>
	 *	     			<li>contanctTelePhone	联系电话	{@link java.lang.String}</li>
	 *              </ul>
	 *         </ul>
	 *
	 * @throws SLException
	 */
	@Rules(rules = { @Rule(name = "custId", required = true, requiredMessage = "客户主键不能为空!") })
	public ResultVo getCustAndContactCustInfo(Map<String,Object> param) throws SLException;
	
	
	/***
	 * 修改账户用户信息和联系人信息(善林财富二期)
	 * 
	 * @author zhangzs
	 * @date 2015年7月09日 下午2:08:31
	 * @param 
     *         <ul>
	 * 	 		<li>id      				用户id	{@link java.lang.String}</li>
	 *	     	<li>natvicePlaceProvince 	省		{@link java.lang.String}</li>
	 *	     	<li>natvicePlaceCity		市		{@link java.lang.String}</li>
	 *	     	<li>natvicePlaceCounty		县		{@link java.lang.String}</li>
	 *	     	<li>communAddress			通信地址	{@link java.lang.String}</li>
	 *	     	<li>qqCode					QQ		{@link java.lang.String}</li>
	 *	 	    <li>zipCode					邮编		{@link java.lang.String}</li>
	 *          <li>operIpaddress 			操作ip 	{@link java.lang.String}</li>
	 *         	<li>contactId               联系人ID  {@link java.lang.String}</li>
	 *	     	<li>contactName         	名称         	{@link java.lang.String}</li>
	 *	     	<li>relationType        	关系         	{@link java.lang.String}</li>
	 *	     	<li>contanctTelePhone		联系电话	{@link java.lang.String}</li>
	 *         </ul>
	 *
	 * @return <li>result {@link com.slfinance.vo.ResultVo} 返回结果</li>
	 *         <ul>
	 *         <li>result.success 是否成功 {@link java.lang.Boolean} required</li>
	 *         <li>result.message 响应消息 {@link java.lang.String}</li>
	 *         </ul>
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "id", required = true, requiredMessage = "客户主键不能为空!"), 
			@Rule(name = "natvicePlaceProvince", required = true, requiredMessage = "省不能为空!"), 
			@Rule(name = "natvicePlaceCity", required = true, requiredMessage = "市不能为空!"), 
			@Rule(name = "natvicePlaceCounty", required = true, requiredMessage = "县不能为空!"), 
			@Rule(name = "communAddress",  required = true, requiredMessage = "地址不能为空!"), 
			@Rule(name = "zipCode",required = true, digist=true,digistMessage ="只能是数字", maxLength=7,maxLengthMessage="邮编长度只能是六位"), 
			@Rule(name = "contactName", required = true, requiredMessage = "姓名不能为空!"),
			@Rule(name = "relationType", required = true, requiredMessage = "联系人关系不能为空!"),
			@Rule(name = "contanctTelePhone", required = true, requiredMessage = "联系方式不能为空!")
			})
	public ResultVo postCustAndContactCustInfo(Map<String,Object> custInfoMap) throws SLException;

	/***
	 * 登陆返回已绑定银行卡信息(善林财富二期)
	 * 
	 * @author zhangzs
	 * @date 2015年9月7日 下午2:08:31
	 * @param 
     *         <ul>
	 * 	 		<li>mobile      	客户手机	{@link java.lang.String}</li>
	 *	     	<li>loginPassword 	登陆密码	{@link java.lang.String}</li>
	 *         </ul>
	 *
	 * @return <li>result {@link com.slfinance.vo.ResultVo} 返回结果</li>
	 *         <ul>
	 *         <li>result.success 是否成功 {@link java.lang.Boolean} required</li>
	 *         <li>result.message 响应消息 {@link java.lang.String}</li>
	 *         <li>result.data 	     用户信息 {@link java.util.Map}</li>
	 *         </ul>
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "mobile", required = true, requiredMessage = "客户手机不能为空!"), 
			@Rule(name = "loginPassword", required = true, requiredMessage = "登陆密码不能为空!")
			})
	public ResultVo loginMobile(Map<String, Object> parasMap)throws SLException;

	/**
	 * 获取用户信息
	 * @author liyy
	 * @date 2017年2月13日 下午2:57:31
	 */
	@Rules(rules = { 
			@Rule(name = "mobile", required = true, requiredMessage = "客户手机不能为空!")
			})
	public ResultVo getUserInfo(Map<String, Object> parasMap) throws SLException;

	
	/**
	 * 获取用户信息
	 * @author liyy
	 * @date 2017年2月13日 下午2:57:31
	 */
	@Rules(rules = { 
			@Rule(name = "custId", required = true, requiredMessage = "用户Id不能为空!"),
			@Rule(name = "password", required = true, requiredMessage = "密码不能为空!")
	})
	public ResultVo checkLoginPassword(Map<String, Object> parasMap);

    /**
     * 获取用户等级
     * @author mali
     * @date 2017-6-8 16:27:02
     *
     * @param paramsMap
     *      <tt>custId     :String:客户ID</tt><br>
     * @return ResultVo
     *      <tt>ranking     :int:级别</tt><br>
     *      <tt>isShow      :String:是否点亮（0.否 1.是）</tt><br>
     *
     */
    @Rules(rules = {
            @Rule(name = "custId", required = true, requiredMessage = "用户Id不能为空!")
    })
    ResultVo getUserRanking(Map<String, Object> paramsMap);

    Map<String, Object> getUserRanking(String custId);

    /**
     * 获取用户等级详情
     *
     * @author mali
     * @date 2017-6-8 20:58:46
     *
     * @param paramsMap
     *      <tt>custId     :String:客户ID</tt><br>
     *
     * @return ResultVo
     *      <tt>currRank     :int:当前级别</tt><br>
     *      <tt>gap          :BigDecimal:举例下个级别相差的金额</tt><br>
     *      <tt>records      :List:举例下个级别相差的金额</tt><br>
     *      <tt>records(i).ranking    :int: 级别</tt>
     *      <tt>records(i).beginDate  :String: 开始时间</tt>
     *      <tt>records(i).endDate    :String: 结束时间</tt>
     *
     */
    @Rules(rules = {
            @Rule(name = "custId", required = true, requiredMessage = "用户Id不能为空!")
    })
    ResultVo getUserRankingDetail(Map<String, Object> paramsMap);

}
