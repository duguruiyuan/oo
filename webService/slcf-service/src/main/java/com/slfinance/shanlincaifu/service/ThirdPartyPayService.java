/** 
 * @(#)ThirdPartyPayService.java 1.0.0 2015年4月28日 上午10:04:43  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.web.client.RestClientException;

import com.slfinance.exception.SLException;
import com.slfinance.vo.ResultVo;

/**   
 * 第三方账户接口
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年4月28日 上午10:04:43 $ 
 */
public interface ThirdPartyPayService {

	/**
	 * 充值
	 * 
	 * @param params
	 *            <tt>key: custId, title: 客户ID, type:{@link String} </tt><br>
	 *            <tt>key: tradeCode, title: 交易编号, type:{@link String} </tt><br>
	 *            <tt>key: tradeAmount, title: 交易金额, type:{@link String} </tt>
	 *            <tt>key: bankCardNo, title: 银行卡号, type:{@link String} </tt><br>
	 *            <tt>key: bankNo, title: 银行卡编号（所属银行）, type:{@link String} </tt><br>
	 *            <tt>key: agreeNo, title: 协议号, type:{@link String} </tt><br>
	 *            <tt>key: custIp, title: 客户IP地址, type:{@link String} </tt><br>
	 *            <tt>key: backUrl, title: 修改银行卡地址, type:{@link String} </tt><br>
	 *            <tt>key: payType, title: 支付方式, type:{@link String} </tt><br>
	 *            </tt><br>
	 * @return resultMap
	 *         <tt>key: thirdPartyType, title: 第三方类型, type:{@link String} </tt><br>
	 *         <tt>key: bizSystemType, title: 业务系统类型, type:{@link String} </tt><br>
	 *         <tt>key: tradeType, title: 交易类型, type:{@link String} </tt><br>
	 *         <tt>key: batchCode, title: 批次号, type:{@link String} </tt><br>
	 *         <tt>key: isSync, title: 是否同步调用, type:{@link String} </tt><br>
	 *         <tt>key: isAsyncNotify, title: 是否异步通知, type:{@link String} </tt><br>
	 *         <tt>key: tradeResultVo.getOtherPropertyMap(), title:其他属性（第三方报文）,type:{@link Map<String, Object>}
	 *         		result:      
	 *         		<tt>key: version, title: 版本号, type:{@link String} </tt><br>
	 *         		<tt>key: user_id, title: 用户唯一ID即客户编号, type:{@link String} </tt><br>
	 *         		<tt>key: busi_partner, title: 商户编号, type:{@link String} </tt><br>
	 *         		<tt>key: no_order, title: 交易比那好哦, type:{@link String} </tt>
	 *         		<tt>key: dt_order, title: 订单时间, type:{@link String} </tt><br>
	 *         		<tt>key: name_goods, title:商品名称, type:{@link String} </tt><br>
	 *         		<tt>key: info_order, title :订单描述：, type:{@link String} </tt><br>
	 *         		<tt>key: money_order, title:交易金额, type:{@link String} </tt><br>
	 *         		<tt>key: userreq_ip, title:用户IP, type:{@link String} </tt><br>
	 *         		<tt>key: url_order, title:订单地址, type:{@link String} </tt><br>
	 *         		<tt>key: valid_order, title:订单有效时间, type:{@link String} </tt><br>
	 *         		<tt>key: bank_code, title:银行编号, type:{@link String} </tt><br>
	 *         		<tt>key: pay_type, title:支付类型, type:{@link String} </tt><br>
	 *         		<tt>key: timestamp, title:请求时间, type:{@link String} </tt><br>
	 *         		<tt>key: risk_item, title:风险控制, type:{@link String} </tt><br>
	 *         		<tt>key: no_agree, title:协议号, type:{@link String} </tt><br>
	 *         		<tt>key: id_type, title:证据类型, type:{@link String} </tt><br>
	 *         		<tt>key: id_no, title:证据号码, type:{@link String} </tt><br>
	 *         		<tt>key: acct_name, title:用户名称, type:{@link String} </tt><br>
	 *         		<tt>key: flag_modify, title:, type:{@link String} </tt><br>
	 *         		<tt>key: card_no, title:银行卡号, type:{@link String} </tt><br>
	 *         		<tt>key: back_url, title:修改银行卡地址, type:{@link String} </tt><br>
	 *         		url:
	 *         		<tt>key: url, title: 第三方地址, type:{@link String} </tt><br>
	 *         </tt><br>
	 * @throws RestClientException
	 * @throws Exception
	 */
	public ResultVo trustRecharge(Map<String, Object> params) throws SLException;
	
	/**
	 * 提现
	 * 
	 * @param params
	 *            <tt>key: custCode, title: 第三方账户, type:{@link String} </tt><br>
	 *            <tt>key: tradeCode, title: 交易编号 唯一性, type:{@link String} </tt><br>
	 *            <tt>key: accountType, title: 账户类型, type:{@link String} </tt><br>
	 *            <tt>key: tradeAmount, title: 交易金额, type:{@link BigDecimal} </tt>
	 *            <tt>key: otherPropertyMap, title: 其他属性Map集合, type:{@link Map} 
	 * 				 <p>cardId: 第三方响应的卡标识ID
	 * @throws Exception
	 */
	public ResultVo trustWithdrawCash(Map<String, Object> params) throws SLException;
	
	/**
	 * 实名认证
	 * 
	 * @param paramsMap
	 *		  	<li>custName 	            用户名     {@link java.lang.String}</li>
	 *	     	<li>credentialsCode 身份证号  {@link java.lang.String}</li>
	 * @return 
	 *         <li>result {@link com.slfinance.vo.ResultVo} 返回结果</li>
	 *         <ul>
	 *         <li>result.success 是否成功 {@link java.lang.Boolean} required</li>
	 *         <li>result.message 响应消息 {@link java.lang.String}</li>
	 *         </ul>
	 * @throws SLException
	 */
	public ResultVo realNameAuth(Map<String, Object> paramsMap) throws SLException;
	
	/**
	 * 查询第三方支持的银行
	 *
	 * @author  wangjf
	 * @date    2015年5月18日 下午7:34:33
	 * @return
	 * @throws SLException
	 */
	public Map<String, Object> querySupportBank() throws SLException;
	
	/**
	 * 根据卡号查询银行卡信息
	 *
	 * @author  wangjf
	 * @date    2015年5月19日 下午3:01:36
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
	public Map<String, Object> queryThirdBankByCardNo(Map<String, Object> paramsMap) throws SLException;
	
	/**
	 * 查询用户签约信息
	 *
	 * @author  wangjf
	 * @date    2015年6月3日 上午10:55:26
	 * @param paramsMap
	 * @return
	 * @throws SLException
	 */
	public Map<String, Object> queryUserBank(Map<String, Object> paramsMap) throws SLException;
	
	/**
	 * 同步通知
	 *
	 * @author  wangjf
	 * @date    2015年6月8日 下午4:25:40
	 * @param paramsMap
	 * @return
	 * @throws SLException
	 */
	public Map<String, Object> appSynAuth(Map<String, Object> paramsMap) throws SLException;
	
	/**
	 * 解约银行卡
	 *
	 * @author  wangjf
	 * @date    2015年7月8日 下午3:18:41
	 * @param paramsMap
	 * 		<tt>key: custCode, title: 客户编号, type:{@link String} </tt><br>
	 * 		<tt>key: noAgree, title: 协议编号, type:{@link String} </tt><br>
	 * @return
	 * @throws SLException
	 */
	public ResultVo unbindUserBank(Map<String, Object> paramsMap) throws SLException;
	
	/**
	 * 查询充值情况
	 *
	 * @author  wangjf
	 * @date    2015年10月10日 上午11:21:18
	 * @param paramsMap
	 * @return
	 * @throws SLException
	 */
	public ResultVo queryRecharge(Map<String, Object> paramsMap) throws SLException;
	
	/**
	 * 查询提现情况
	 *
	 * @author  wangjf
	 * @date    2015年10月10日 上午11:22:50
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo queryWithdrawCash(Map<String, Object> params) throws SLException;
	
	/**
	 * 查询商户余额
	 *
	 * @author  wangjf
	 * @date    2016年6月13日 下午1:17:29
	 * @param paramsMap
	 * @return
	 * @throws SLException
	 */
	public ResultVo queryAccountAmount(Map<String, Object> paramsMap) throws SLException;
}
