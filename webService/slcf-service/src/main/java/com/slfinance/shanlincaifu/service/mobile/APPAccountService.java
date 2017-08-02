/** 
 * @(#)APPAccountService.java 1.0.0 2015年5月15日 下午2:52:15  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.mobile;

import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.vo.ResultVo;

/**   
 * APP手机端账户模块业务接口
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年5月15日 下午2:52:15 $ 
 */
public interface APPAccountService {
	
	/**
	 * 账户首页
	 * 
	 * @param 
	 *	     	<li>custId 	        	 反馈信息用户的id {@link java.lang.String}</li>
	 *			<li>newUnreadMsgMthod 	 新方法取未读消息数 true/false {@link java.lang.String}</li>
	 * @return 
	 *         <li>data {@link java.util.Map} 分页记录结果集</li>
	 *         <li>{@link java.util.Map} 每行记录</li>
     *         <ul>
	 *		  	<li>unReadMsgNum 	               未读消息数 {@link java.lang.String}</li>
	 *	     	<li>sumAccountAmount     总资产         {@link java.math.BigDecimal}</li>
	 *	     	<li>yesterdayTradeAmount 昨日收益     {@link java.math.BigDecimal}</li>
	 *	     	<li>accountAmount        可用金额     {@link java.math.BigDecimal}</li>
	 *	     	<li>sumTradeAmount       累计收益      {@link java.math.BigDecimal}</li>
	 *	     	<li>shareHoldingAmount   持有份额      {@link java.math.BigDecimal}</li>
	 *        </ul> 
	 */
	public ResultVo user(Map<String,Object> paramsMap)throws SLException;
	
	/**
	 * 账户总览
	 * 
	 * @param 
	 *	     	<li>custId 	         反馈信息用户的id {@link java.lang.String}</li>
	 * @return <
	 *         <li>data {@link java.util.Map} 分页记录结果集</li>
	 *         <li>{@link java.util.Map} 每行记录</li>
     *         <ul>
	 *		  	<li>enableAccountAmount 可用余额      {@link java.lang.String}</li>
	 *	     	<li>freezeAmount        冻结金额       {@link java.math.BigDecimal}</li>
	 *	     	<li>enableTasteAmount   可用体验金   {@link java.math.BigDecimal}</li>
	 *	     	<li>rewardNotSettle     未结算奖励   {@link java.math.BigDecimal}</li>
	 *	     	<li>sumTradeAmount      累计收益       {@link java.math.BigDecimal}</li>
	 *        </ul>
	 * @throws SLException 
	 */
	public ResultVo accountALL(Map<String,Object> paramsMap)throws SLException;
	
	/**
	 * 交易记录
	 * 
	 * @param 
	 *	     	<li>custId 	         反馈信息用户的id {@link java.lang.String}</li>
	 *	     	<li>pageNum    当前页数从0开始      {@link java.lang.String}</li>
	 *	     	<li>pageSize   每页显示数据             {@link java.lang.String}</li>
	 * @return 
	 *         <li>iTotalDisplayRecords 总记录数 {@link java.lang.Integer}</li>
	 *         <li>data {@link java.util.List} 分页记录结果集</li>
	 *         <li>{@link java.util.Map} 每行记录</li>
	 *         <ul>
	 *		  	<li>tradeType   交易类型       {@link java.lang.String}</li>
	 *	     	<li>tradeAmount 交易金额       {@link java.math.BigDecimal}</li>
	 *	     	<li>tradeDate   交易日期       {@link java.util.Date}</li>
	 *        </ul> 
	 * @throws SLException
	 */
	public ResultVo tradeListALL(Map<String,Object> paramsMap)throws SLException;
	
	/**
	 * 消息列表
	 * 
	 * @param 
	 *	     	<li>custId 	         反馈信息用户的id {@link java.lang.String}</li>
	 *	 	    <li>pageNum    当前页数从0开始      {@link java.lang.String}</li>
	 *	     	<li>pageSize   每页显示数据             {@link java.lang.String}</li>
	 * @return 
	 * 	       <li>iTotalDisplayRecords 总记录数 {@link java.lang.Integer}</li>
	 *         <li>data {@link java.util.List} 分页记录结果集</li>
	 *         <li>{@link java.util.Map} 每行记录</li>
	 *         <ul>
	 *		  	<li>sendTitle  			标题         {@link java.lang.String}</li>
	 *	     	<li>sendContent			内容          {@link java.lang.String}</li>
	 *	     	<li>sendDate   			发送时间   {@link java.util.Date}</li>
	 *	     	<li>isRead              是否已阅读{@link java.lang.String}</li>
	 *	     	<li>receiveCust.custName收件人姓名{@link java.lang.String}</li>
	 *        </ul> 
	 * @throws SLException
	 */
	public ResultVo messageListALL(Map<String,Object> paramsMap)throws SLException;
	
	public ResultVo messageListALLNew(Map<String,Object> paramsMap)throws SLException;
	
	/**
	 * 消息列表 new By salesMan APP
	 * @author  lyy
	 * @date    2016-11-14 19:39:40
	 */
	public ResultVo messageListBySalesMan(Map<String, Object> paramsMap) throws SLException;
	
	/**
	 * 未读消息 By salesMan APP
	 * @author  lyy
	 * @date    2016-11-14 19:39:40
	 */
	ResultVo unreadMessageCountBySalesMan(Map<String, Object> paramsMap);
	
	/**
	 * 更新用户已读状态
	 * 
	 * @param 
	 *		  	<li>id 	            消息ID   {@link java.lang.String}</li>
	 *	     	<li>custId  登录用户ID {@link java.lang.String}</li>
	 * @return 
	 *         <li>result {@link com.slfinance.vo.ResultVo} 返回结果</li>
	 *         <ul>
	 *         <li>result.success 是否成功 {@link java.lang.Boolean} required</li>
	 *         <li>result.message 响应消息 {@link java.lang.String}</li>
	 *         </ul>
	 * @throws SLException
	 */
	public ResultVo updateIsRead(Map<String,Object> paramsMap)throws SLException;
	
	public ResultVo updateIsReadNew(Map<String,Object> paramsMap)throws SLException;
	
	/**
	 * 查询账户信息
	 *
	 * @author  wangjf
	 * @date    2015年5月25日 上午11:03:26
	 * @param params
	 * 		<li>custId  登录用户ID {@link java.lang.String}</li>
	 *      <li>productType  产品类型（如活期宝或体验宝） {@link java.lang.String}</li>
	 * @return
	 *         <li>data {@link java.util.Map} 记录结果集</li>
     *         <ul>
	 *		  	<li>yesterdayTradeAmount 昨日收益      {@link java.lang.BigDecimal}</li>
	 *	     	<li>yearRate        年化利率       {@link java.math.BigDecimal}</li>
	 *	     	<li>sumAccountAmount   当前总额   {@link java.math.BigDecimal}</li>
	 *	     	<li>sumJoinAmount 累计加入金额   {@link java.math.BigDecimal}</li>
	 *	     	<li>sumAtoneAmount      累计赎回金额       {@link java.math.BigDecimal}</li>
	 *          <li>sumTradeAmount      累计收益       {@link java.math.BigDecimal}</li>
	 *          <li>sumHoldLoan      持有债权数       {@link java.math.BigDecimal}</li>
	 *          <li>accountAmount      持有金额 /可赎回金额      {@link java.math.BigDecimal}</li>
	 *          </ul>
	 * @throws SLException
	 */
	public ResultVo queryAccount(Map<String,Object> params) throws SLException;
	
	/**
	 * 查询银行信息（获取用户银行卡信息、客户信息、提现金额）
	 *
	 * @author  wangjf
	 * @date    2015年5月25日 下午2:13:42
	 * @param params
	 		<li>custId  登录用户ID {@link java.lang.String}</li>
	 * @return
	 * 			<tt>id： String:银行卡主键</tt><br>
	 			<tt>custId： String:客户ID</tt><br>
	 			<tt>bankName： String:银行名称</tt><br>
	 			<tt>cardNo： String:银行卡卡号</tt><br>
	 			<tt>protocolNo： String:协议号</tt><br>
				<tt>openProvince： String:开户省</tt><br>
	  			<tt>openCity： String:开户市</tt><br>
				<tt>subBranchName： String:支行名称</tt><br>
				<tt>custName： String:客户名称</tt><br>
				<tt>credentialsCode： String:证件号码</tt><br>
				<tt>withdrawalAmount： BigDecimal:可提现金额</tt><br>
				<tt>withdrawalExpense： BigDecimal:提现手续费</tt><br>
	 * @throws SLException
	 */
	public ResultVo queryBank(Map<String,Object> params) throws SLException;
	
	/**
	 * 查询第三方支持的银行
	 *
	 * @author  wangjf
	 * @date    2015年6月1日 上午11:01:03
	 * @return
	 * @throws SLException
	 */
	public ResultVo querySupportBank() throws SLException;
	
	/**
	 * 根据卡号查询银行卡信息
	 *
	 * @author  wangjf
	 * @date    2015年6月2日 下午4:25:21
	 * @param paramsMap
	 		<tt>key: bankCardNo, title: 银行卡号, type:{@link String} </tt><br>
	 * @return
	 		<tt>key: bankCode, title: 银行编号, type:{@link String} </tt><br>
	 		<tt>key: bankName, title: 银行名称, type:{@link String} </tt><br>
	 		<tt>key: cardType, title: 卡片类型, type:{@link String} </tt><br>
	 		<tt>key: singleAmount, title: 单笔限额, type:{@link String} </tt><br>
	 		<tt>key: dayAmount, title: 单日限额, type:{@link String} </tt><br>
	 		<tt>key: monthAmount, title: 单月限额, type:{@link String} </tt><br>
	 * @throws SLException
	 */
	public ResultVo queryThirdBankByCardNo(Map<String, Object> paramsMap) throws SLException;

	/**
	 * 用户加入记录、赎回记录、收益记录
	 * 
	 * @param 
	 *	     	<li>custId 	               反馈信息用户的id{@link java.lang.String}</li>
	 *	     	<li>tradeTypeList交易类型		{@link java.lang.String}</li>
	 *	     	<li>pageNum    	   当前页数从0开始  {@link java.lang.String}</li>
	 *	     	<li>pageSize     每页显示数据         {@link java.lang.String}</li>
	 * @return 
	 *         <li>iTotalDisplayRecords 总记录数 {@link java.lang.Integer}</li>
	 *         <li>data {@link java.util.List} 分页记录结果集</li>
	 *         <li>{@link java.util.Map} 每行记录</li>
	 *         <ul>
	 *		  	<li>tradeType   交易类型       {@link java.lang.String}</li>
	 *	     	<li>tradeAmount 交易金额       {@link java.math.BigDecimal}</li>
	 *	     	<li>tradeDate   交易日期       {@link java.util.Date}</li>
	 *        </ul> 
	 * @throws SLException
	 */
	public ResultVo tradeListALLInfo(Map<String, Object> paramsMap)throws SLException;
	
	
	/**
	 * 获取金牌推荐人统计信息
	 */
	public ResultVo findCustCommissionDetailListMobile(Map<String, Object> paramsMap)throws SLException;
	/**
	 *  获取我的佣金详情记录
	 */
	public ResultVo findCustCommissionListMobile(Map<String, Object> paramsMap)throws SLException;
	
	/**
	 * 交易记录
	 * 
	 * @param 
	 *	     	<li>custId 	         反馈信息用户的id {@link java.lang.String}</li>
	 *			<li>tradeType  交易类型                     {@link java.lang.String}</li>
	 *	     	<li>pageNum    当前页数从0开始      {@link java.lang.String}</li>
	 *	     	<li>pageSize   每页显示数据             {@link java.lang.String}</li>
	 * @return 
	 *         <li>iTotalDisplayRecords 总记录数 {@link java.lang.Integer}</li>
	 *         <li>data {@link java.util.List} 分页记录结果集</li>
	 *         <li>{@link java.util.Map} 每行记录</li>
	 *         <ul>
	 *		  	<li>tradeType   交易类型       {@link java.lang.String}</li>
	 *	     	<li>tradeAmount 交易金额       {@link java.math.BigDecimal}</li>
	 *	     	<li>tradeDate   交易日期       {@link java.util.Date}</li>
	 *          <li>expense     手续费           {@link java.math.BigDecimal}</li>
	 *          <li>bankName    银行名称       {@link java.lang.String}</li>
	 *          <li>bankCardNo  银行卡号       {@link java.lang.String}</li>
	 *          <li>tradeStatus 交易状态       {@link java.lang.String}</li>
	 *          <li>memo        备注               {@link java.lang.String}</li>
	 *        </ul> 
	 * @throws SLException
	 */
	public ResultVo tradeFlowList(Map<String,Object> paramsMap)throws SLException;
	
	/**
	 * 查询提现金额
	 *
	 * @author  wangjf
	 * @date    2016年3月1日 下午1:45:46
	 * @param params
	 		<li>custId  登录用户ID {@link java.lang.String}</li>
	 * @return
				<tt>custName： String:客户名称</tt><br>
				<tt>credentialsCode： String:证件号码</tt><br>
				<tt>withdrawalAmount： BigDecimal:可提现金额</tt><br>
				<tt>withdrawalExpense： BigDecimal:提现手续费</tt><br>
	 * @throws SLException
	 */
	public ResultVo queryWithdrawalAmount(Map<String,Object> paramsMap) throws SLException;
}
