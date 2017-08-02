package com.slfinance.shanlincaifu.service;

import com.slfinance.annotation.IPRequestLimits;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.CustInfoEntity;
import com.slfinance.shanlincaifu.entity.LogInfoEntity;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;
import java.util.List;
import java.util.Map;

public interface CustomerService {

	public ResultVo findByLoginName(Map<String, Object> params);

	public ResultVo findByMobile(Map<String, Object> params);

	public ResultVo findByLoginNameAndCredentialsCode(Map<String, Object> params);

	public ResultVo findByCondition(Map<String, Object> param);

	/**
	 * 用户注册
	 * 
	 * @param custInfoMap
	 *            <tt>mobile： String:手机号码</tt><br>
	 *            <tt>loginName： String:昵称</tt><br>
	 *            <tt>loginPassword： String:登录密码</tt><br>
	 *            <tt>messageType： String:验证类型</tt><br>
	 *            <tt>verityCode： String:验证码</tt><br>
	 * @return
	 */
	@Rules(rules = { 
		@Rule(name = "mobile", 
					required = true, requiredMessage = "手机号码不能为空或包含空格", 
					mobile = true, mobileMessage = "手机格式错误"), 
		@Rule(name = "loginName", 
					required = true, requiredMessage = "登录名不能为空或包含空格", 
					minLength = 5, minLengthMessage = "登录名长度不能少于6位", 
					maxLength = 21, maxLengthMessage = "登录名长度不能大于20位", 
					letterOrDigitOrSymbol = true, letterOrDigitOrSymbolMessage = "登录名必须为字母或者数字或特殊符号（@._）之一"), 					
		@Rule(name = "messageType", required = true, requiredMessage = "验证类型不能为空"), 
		@Rule(name = "verityCode", required = true, requiredMessage = "验证码不能为空"),
	})
	@IPRequestLimits(rate = 20, daily = 100, monthly = 1000)
	public ResultVo register(Map<String, Object> custInfoMap);
	
	/**
	 * 用户注册(内部方法)
	 *
	 * @author  wangjf
	 * @date    2015年7月2日 下午7:29:16
	 * @param custInfoMap
	 * @return
	 */
	public ResultVo innerRegister(Map<String, Object> custInfoMap);

	/**
	 * @author HuangXiaodong 2015-04-17 用于用户登录
	 * @param Map
	 *            <String, Object> mobile: 用户手机号
	 * @return ResultVo
	 */
	public ResultVo login(Map<String, Object> param);

	/**
	 * @author HuangXiaodong 2015-04-17 用于用户登录记录登录日志用
	 * @param Map
	 *            <String, Object> custId: 用户id loginIp：登陆ip
	 * @return ResultVo
	 */
	public ResultVo recordLoginLog(Map<String, Object> param);

	public ResultVo updateCust(Map<String, Object> param);

	/**
	 * 
	 * 重置密码
	 * 
	 * @author richad
	 * @param params
	 * @return
	 */	  
	public ResultVo updatePasswordByType(Map<String, Object> params);

	/**
	 * 
	 * 设置密码
	 * 
	 * @author richad
	 * @param params
	 * @return
	 */	  
	public ResultVo setLoginPassword(Map<String, Object> params);

	/**
	 * @author HuangXiaodong 2015-04-22 获取上次登录信息
	 * @param Map
	 *            <String, Object> custId: 用户id
	 * @return ResultVo
	 */
	public ResultVo findLastLoginInfo(Map<String, Object> param);

	/**
	 * 获取用户活期宝/体验宝信息
	 * 
	 * @author wangjf 2015-04-22 用户活期宝信息
	 * @param Map
	 *            <tt>custId： String:用户id</tt><br>
	 *            <tt>productType： String:产品类型[活期宝或体验宝](为空时默认为活期宝)</tt><br>
	 * @return data: Map<String, Object>:累积收益
	 *         <tt>yesterdayTradeAmount： BigDecimal:昨日收益</tt><br>
	 *         <tt>sumTradeAmount： BigDecimal:累计收益</tt><br>
	 *         <tt>accountAmount： BigDecimal:持有金额</tt><br>
	 *         <tt>sumJoinAmount： BigDecimal:累积加入金额</tt><br>
	 *         <tt>sumAtoneAmount： BigDecimal: 累积赎回金额</tt><br>
	 *         <tt>sumHoldLoan： BigDecimal: 持有债权数</tt><br>
	 *         <tt>accountAvailableValue： BigDecimal: 可用价值/可赎回金额</tt><br>
	 *         <tt>sumAtoningAmount： BigDecimal: 赎回中金额</tt><br>
	 */
	public ResultVo findBaoCountInfoByCustId(Map<String, Object> param);

	/**
	 * @author HuangXiaodong 2015-04-22 用户善林理财信息
	 * @param Map
	 *            <String, Object> custId: 用户id
	 * @return ResultVo
	 */
	public ResultVo findWealthCountInfoByCustId(Map<String, Object> param);

	/**
	 * 我的账户-我的投资-活期宝-交易明细
	 *
	 * @author wangjf
	 * @date 2015年4月28日 下午4:38:49
	 * @param param
	 *            <tt>start：int:分页起始页</tt><br>
	 *            <tt>length：int:每页长度</tt><br>
	 *            <tt>custId： String:客户ID</tt><br>
	 *            <tt>tradeType： String:交易类型(可以为空)</tt><br>
	 *            <tt>opearteDateBegin：String:操作开始时间(可以为空)</tt><br>
	 *            <tt>opearteDateEnd：String:操作开始时间(可以为空)</tt><br>
	 *            <tt>productType： String:产品类型(可以为空)</tt><br>
	 * @return iTotalDisplayRecords: 总条数 data:List<Map<String, object>>
	 *         Map<String, object>: <tt>requestNo： String:请求编号</tt><br>
	 *         <tt>tradeDate： String:交易日期</tt><br>
	 *         <tt>tradeType： String:交易类型</tt><br>
	 *         <tt>tradeAmount： BigDecimal:交易金额</tt><br>
	 */
	@Rules(rules = { @Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), @Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数"), @Rule(name = "custId", required = true, requiredMessage = "客户主键不能为空!") })
	public Map<String, Object> findAllBaoAccountDetailByCustId(Map<String, Object> param);
	
	/**
	 * 查询赎回记录
	 *
	 * @author  wangjf
	 * @date    2015年6月3日 下午4:25:04
	 * @param param
	 *            <tt>start：int:分页起始页</tt><br>
	 *            <tt>length：int:每页长度</tt><br>
	 *            <tt>custId： String:客户ID</tt><br>
	 *            <tt>tradeType： String:交易类型(可以为空)</tt><br>
	 *            <tt>tradeStatus： String:交易状态(可以为空)</tt><br>
	 *            <tt>opearteDateBegin：String:操作开始时间(可以为空)</tt><br>
	 *            <tt>opearteDateEnd：String:操作开始时间(可以为空)</tt><br>
	 *            <tt>productType： String:产品类型(可以为空默认为活期宝)</tt><br>
	 * @return iTotalDisplayRecords: 总条数 data:List<Map<String, object>>
	 *         Map<String, object>: <tt>requestNo： String:请求编号</tt><br>
	 *         <tt>tradeDate： String:交易日期</tt><br>
	 *         <tt>tradeType： String:交易类型</tt><br>
	 *         <tt>tradeAmount： BigDecimal:交易金额</tt><br>
	 *         <tt>atoneExpenses： BigDecimal:手续费</tt><br>
	 *         <tt>atoneStatus： String:状态</tt><br>
	 *         <tt>memo： String:备注</tt><br>
	 */
	@Rules(rules = { @Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), @Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数"), @Rule(name = "custId", required = true, requiredMessage = "客户主键不能为空!") })
	public Map<String, Object> findAllAtoneByCustId(Map<String, Object> param);

	/**
	 * 查询客户信息
	 * 
	 * @param param
	 * @return
	 */
	public Map<String, Object> findAllCustInfo(Map<String, Object> param);

	/**
	 * 账户总览-获取累计收益
	 *
	 * @author wangjf
	 * @date 2015年4月28日 下午4:30:04
	 * @param param
	 *            <tt>custId： String:客户ID</tt><br>
	 *            <tt>tradeType： List<String>:交易类型(当需统计多个受益时可为多个)</tt><br>
	 *            <tt>tradeDate： String:交易日期（可为空，当为空时统计所有时间收益）</tt><br>
	 * @return data: Map<String, Object>:累积收益
	 *         <tt>sumTradeAmount： BigDecimal:收益</tt><br>
	 *         <tt>sumAccountAmount： BigDecimal:总资产</tt><br>
	 */
	@Rules(rules = { @Rule(name = "custId", required = true, requiredMessage = "客户主键不能为空!") })
	public ResultVo findTotalIncomeByCustId(Map<String, Object> param);

	/** 统计客户邀请信息 */
	public ResultVo queryInviteData(Map<String, Object> custMap) throws SLException;

	/** 根据邀请级别查询客户 */
	public ResultVo queryCustSpreadLevel(Map<String, Object> custMap) throws SLException;

	public Map<String, Object> findCustInfoEntityDetail(Map<String, Object> custMap) throws SLException;

	/*
	 * 手机登录
	 */
	@Rules(rules = { 
			@Rule(name = "mobile", 
						required = true, requiredMessage = "手机号码不能为空或包含空格", 
						mobile = true, mobileMessage = "手机格式错误"), 				
			@Rule(name = "loginPassword", required = true, requiredMessage = "登录密码不能为空")
		})
	public ResultVo loginMobile(Map<String, Object> param);

	/**
	 * 发送短信
	 * 
	 * @param param
	 * @return
	 */
	@Rules(rules = { 
			@Rule(name = "mobile", 
						required = true, requiredMessage = "手机号码不能为空或包含空格", 
						mobile = true, mobileMessage = "手机格式错误")
		})
	public ResultVo sendSMS(Map<String, Object> param);

	/**
	 * 修改个人信息
	 * 
	 * @param custInfoMap
	 *            <tt>id： String:id</tt><br>
	 *            <tt>userNumber： String:用户工号</tt><br>
	 *            <tt>ipAddress： String:IP地址</tt><br>
	 *            <tt>enableStatus： String:状态</tt><br>
	 *            <tt>memo： String:描述</tt><br>
	 * @return
	 */
	public ResultVo updateCustStatus(Map<String, Object> param);

	/**
	 * 更绑定新手机
	 * 
	 * @param params
	 * @return
	 */
	public ResultVo updateBindMobile(Map<String, Object> params);

	/**
	 * 设置交易密码
	 * 
	 * @param custInfoMap
	 *            <tt>mobile： String:手机号码</tt><br>
	 *            <tt>tradePassword： String:交易密码</tt><br>
	 * @return
	 */
	public ResultVo updateTradePasswrod(Map<String, Object> params);

	/**
	 * 根据手机号修改登录密码
	 * 
	 * @param param
	 *            <tt>mobile： 手机号</tt><br>
	 *            <tt>verityCode：验证吗</tt><br>
	 *            <tt>password：登录密码</tt><br>
	 *            <tt>messageType： 短信类型</tt><br
	 * @param params
	 * @return
	 */
	public ResultVo updateLoginPasswrodByMobile(Map<String, Object> params);

	/**
	 * 根据客户ID查询客户
	 *
	 * @author wangjf
	 * @date 2015年5月14日 上午10:41:05
	 * @param custId
	 * @return
	 */
	public CustInfoEntity findByCustId(String custId);

	/**
	 * @author HuangXiaodong 2015-04-22 用户活期宝信息
	 * @param Map
	 *            <String, Object> custId: 用户id
	 * @return data: Map<String, Object>:累积收益
	 *         <tt>yesterdayTradeAmount： BigDecimal:昨日收益</tt><br>
	 *         <tt>sumTradeAmount： BigDecimal:累计收益</tt><br>
	 *         <tt>accountAmount： BigDecimal:持有份额</tt><br>
	 *         <tt>accountAvailableValue： BigDecimal: 可用价值/可赎回金额</tt><br>
	 */
	public ResultVo findExperienceBaoCountInfoByCustId(Map<String, Object> param);

	/**
	 * 交易查询
	 *
	 * @author HuangXiaodong
	 * @date 2015年5月19日 上午11:33:31
	 * @param param
	 *            <tt>tradeType： String:交易类型</tt><br>
	 *            <tt>tradeDateBegin： Date:交易开始时间</tt><br>
	 *            <tt>tradeDateEnd： Date:交易结束时间</tt><br>
	 *            <tt>custId:用户id</tt><br>
	 * @return data: List<Map<String, Object>>: <tt>tradeAmount:交易金额</tt><br>
	 *         <tt>tradeDate： BigDecimal:交易时间</tt><br>
	 */
	public ResultVo findBaoTradeInfo(Map<String, Object> param);

	/**
	 * 重设交易密码
	 * 
	 * @author zhangzs
	 * @date 2015年5月29日 上午11:33:31
	 * @param params
	 *            <ul>
	 *            <li>id 用户ID {@link java.lang.String}</li>
	 *            <li>tradePassword 交易密码 {@link java.lang.String}</li>
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
	 * 更新用户信息和新增日志
	 * 
	 * @author zhangzs
	 * @date 2015年5月29日 上午11:33:31
	 * @param params
	 *            <ul>
	 *            <li>updateInfo 用户信息 {@link com.slfinance.shanlinbao.entity.CustInfoEntity}</li>
	 *            <li>logList    交易密码 {@link com.slfinance.shanlinbao.entity.LogInfoEntity}</li>
	 *            <li>custID     id	   {@link java.lang.String}</li>
	 *            </ul>
	 * @throws SLException
	 */
	public void updCustAndInsertLog(CustInfoEntity updateInfo,List<LogInfoEntity> logList,String custId) throws SLException;
	
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
	 *	     	<li>portraitPath       		头像路径	{@link java.lang.String}</li>
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
	public Map<String,Object> getCustAndContactCustInfo(Map<String,Object> param) throws SLException;
	
	
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
	 *			<li>contanctInfo {@link java.util.Map} 用户基本信息</li>
	 *				<ul>
	 *	     			<li>id                  联系人ID  {@link java.lang.String}</li>
	 *	     			<li>contactName         名称         	{@link java.lang.String}</li>
	 *	     			<li>relationType        关系         	{@link java.lang.String}</li>
	 *	     			<li>contanctTelePhone	联系电话	{@link java.lang.String}</li>
	 *              </ul>
	 *         <li>operIpaddress 操作ip {@link java.lang.String}</li>
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
			@Rule(name = "communAddress",  required = true, requiredMessage = "地址不能为空!",maxLength=101,maxLengthMessage="地址长度最大100"), 
			@Rule(name = "zipCode",required = true, digist=true,digistMessage ="只能是数字", maxLength=7,maxLengthMessage="邮编长度只能是六位"), 
			@Rule(name = "contanctInfo", required = true, requiredMessage = "联系人信息不能为空!")
			})
	public ResultVo updateCustAndContactCustInfo(Map<String,Object> custInfoMap) throws SLException;
	
	/**
	 * 校验邀请码是否正确
	 *
	 * @author  wangjf
	 * @date    2015年7月29日 下午1:52:32
	 * @param map
			<tt>investCode： String:邀请码</tt><br>
	 * @return
	 * @throws SLException
	 */
	public ResultVo checkInvestCode(Map<String, Object> map) throws SLException;
	
	/**
	 * 上传客户头像
	 *
	 * @author  wangjf
	 * @date    2015年8月20日 上午11:15:04
	 * @param map
	 * 		<tt>custId： String:客户ID</tt><br>
	 *      <tt>attachmentType： String:附件类型（用户头像）</tt><br>	
	 *      <tt>attachmentName： String:附件名称</tt><br>
	 *      <tt>storagePath： String:存储路径</tt><br>
	 *      <tt>docType： String:文档类型（PIG）</tt><br>
	 * @return
	 * @throws SLException
	 */
	public ResultVo uploadCustomPhoto(Map<String, Object> map) throws SLException;
	
	/**
	 * 用户注册2.0版
	 * 
	 * @param custInfoMap
	 *            <tt>mobile： String:手机号码</tt><br>
	 *            <tt>loginName： String:昵称</tt><br>
	 *            <tt>loginPassword： String:登录密码</tt><br>
	 *            <tt>messageType： String:验证类型</tt><br>
	 *            <tt>verityCode： String:验证码</tt><br>
	 * @return
	 */
	@Rules(rules = { 
		@Rule(name = "mobile", 
					required = true, requiredMessage = "手机号码不能为空或包含空格", 
					mobile = true, mobileMessage = "手机格式错误"), 					
		@Rule(name = "messageType", required = true, requiredMessage = "验证类型不能为空"), 
		@Rule(name = "verityCode", required = true, requiredMessage = "验证码不能为空"),
	})
	@IPRequestLimits(rate = 20, daily = 100, monthly = 1000)
	public ResultVo registerNew(Map<String, Object> custInfoMap);
	
	/**
	 * 更新用户昵称（仅能更新一次）
	 *
	 * @author  wangjf
	 * @date    2015年11月2日 上午10:59:37
	 * @param params
	 * @return
	 */
	@Rules(rules = { 
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空"), 					
			@Rule(name = "newLoginName", 
			required = true, requiredMessage = "用户名不能为空或包含空格", 
			minLength = 5, minLengthMessage = "用户名长度不能少于6位", 
			maxLength = 21, maxLengthMessage = "用户名长度不能大于20位", 
			letterOrDigitOrSymbol = true, letterOrDigitOrSymbolMessage = "用户名必须为字母或者数字或特殊符号（@._）之一"), 
		})
	public ResultVo updateUserNickName(Map<String, Object> params);
	
	/**
	 * 检查是否已经修改过用户登录名
	 *
	 * @author  wangjf
	 * @date    2015年11月4日 下午5:48:04
	 * @param params
	 * @return
	 */
	@Rules(rules = { 
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空"), 
	})
	public ResultVo checkUpdatedUserNickName(Map<String, Object> params);
	
	/** 线上线下-同步状态变更 */
	public void changeWealthFlagById(String custId, String wealthFlag);
	
	public CustInfoEntity findCustInfoByCardIdAndIsEmployee(String credentialsCode);
	
	@Rules(rules = { 
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空"), 
	})
	public CustInfoEntity findCustInfoById(Map<String, Object> params);
	
	/**
	 * 创建借款客户
	 *
	 * @author  wangjf
	 * @date    2016年12月6日 下午6:08:05
	 * @param params
	 * @return
	 * @throws SLException 
	 */
	public ResultVo createLoanCust(Map<String, Object> params) throws SLException;
	
	/**
	 * 通过手机查询业务员
	 *
	 * @author  wangjf
	 * @date    2016年12月7日 下午4:10:54
	 * @param mobile
	 * @return
	 */
	public ResultVo queryEmployee(String mobile);
	
	/**
	 * 企业商户-商户列表
	 * @author liyy
	 * @date   2016年12月16日下午15:30:00
	 * @param param
	 *      <tt>start           :String:起始值</tt><br>
     *      <tt>length          :String:长度</tt><br>
	 * @return
     *      <tt>custId                :String:客户编号</tt><br>
     *      <tt>custName              :String:姓名</tt><br>
     *      <tt>accountAvailableAmount:String:账户余额</tt><br>
     *      <tt>bankName              :String:银行卡名称</tt><br>
     *      <tt>cardNo                :String:银行卡卡号</tt><br>
     *      <tt>tel                   :String:联系电话（客户表联系电话）</tt><br>
     *      <tt>address               :String:地址（客户表通讯地址）</tt><br>
	 */
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "起始值不能为空!", number = true, numberMessage = "起始值必须为数字"),
			@Rule(name = "length", required = true, requiredMessage = "长度不能为空!", number = true, numberMessage = "长度必须为数字")
	})
	public ResultVo queryCompanyUserList(Map<String, Object> params);
	
	public CustInfoEntity findByLoginNameOrMobile(Map<String, Object> param);
	
	/**
	 * 校验用户交易密码
	 *
	 * @author  wangjf
	 * @date    2016年12月27日 下午1:49:24
	 * @param param
	 * 		<tt>custId                :String:客户ID</tt><br>
	 * 		<tt>tradePassword         :String:交易密码</tt><br>
	 * @return
	 */
	public ResultVo checkUserTradePassword(String custId, String tradePassword);
	
	/**
	 * 设置风险评估
	 * @author  liyy
	 * @date    2016年12月27日 下午1:49:24
	 * @param param
	 * 		<tt>custId                :String:客户ID</tt><br>
	 * 		<tt>tradePassword         :String:交易密码</tt><br>
	 * @return
	 */
	@Rules(rules = { 
			@Rule(name = "custId", required = true, requiredMessage = "客户主键不能为空!"),
			@Rule(name = "riskAssessment", required = true, requiredMessage = "风险评估不能为空!"),
			@Rule(name = "riskAssessmentAnswer", required = true, requiredMessage = "风险评估答案不能为空!")
	})
	public ResultVo setRiskAssessment(Map<String, Object> params);

	/**
	 *
	 * @author fengyl
	 * @date 2017年3月23日
	 * @param param
	 *            <tt>start           :String:起始值</tt><br>
	 *            <tt>length          :String:长度</tt><br>
	 *            <tt>loginName： String:客户昵称(可以为空)</tt><br>
	 *            <tt>riskAssessment：String:评估类型(可以为空)</tt><br>
	 * @return iTotalDisplayRecords: 总条数 data:List<Map<String, object>>
	 *         Map<String, object>: <tt>loginName： String:客户昵称</tt><br>
	 *         <tt>riskAssessment： String:风险类型</tt><br>
	 *         <tt>assessTime： String:评估时间</tt><br>
	 *         <tt>num1： String:题目1</tt><br>
	 *         <tt>num2： String:题目2</tt><br>
	 *         <tt>num3： String:题目3</tt><br>
	 *         <tt>num4： String:题目4</tt><br>
	 *         <tt>num5： String:题目5</tt><br>
	 *         <tt>num6： String:题目6</tt><br>
	 *         <tt>num7： String:题目7</tt><br>
	 *         <tt>num8： String:题目8</tt><br>
	 *         <tt>num9： String:题目9</tt><br>
	 *         <tt>num10： String:题目10</tt><br>
	 */
	@Rules(rules = {
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"),
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数") })
	public ResultVo queryCustRiskList(Map<String, Object> param)
			throws SLException;

}
