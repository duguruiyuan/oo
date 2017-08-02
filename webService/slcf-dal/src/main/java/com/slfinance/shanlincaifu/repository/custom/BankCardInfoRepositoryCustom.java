/** 
 * @(#)BankCardInfoRepositoryCustom.java 1.0.0 2015年4月28日 上午10:43:32  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository.custom;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.BankCardInfoEntity;

/**   
 * 自定义银行卡数据访问接口
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年4月28日 上午10:43:32 $ 
 */
public interface BankCardInfoRepositoryCustom {
	
	/**
	 * 根据银行名称获取银行编号
	 *
	 * @author  wangjf
	 * @date    2015年4月28日 上午10:45:34
	 * @param bankCode
	 * @return
	 */
	public String findByBankName(String bankName);
	
	/**
	 * 根据银行编号获取银行名称
	 *
	 * @author  wangjf
	 * @date    2015年5月20日 上午11:53:44
	 * @param bankCode
	 * @return
	 */
	public String findByBankCode(String bankCode);
	
	/**
	 * 根据客户ID查询银行卡信息
	 *
	 * @author  wangjf
	 * @date    2015年6月1日 下午2:27:18
	 * @param custId
	 * @return
	 */
	public Map<String, Object> findByCustId(String custId);
	
	/**
	 * 查询银行列表
	 *
	 * @author  wangjf
	 * @date    2015年7月16日 下午3:17:38
	 * @param param
	 * @return
	 */
	public Page<Map<String, Object>> findBankList(Map<String, Object> param);
	
	/**
	 * 插入银行对象（客户和银行卡号必须唯一）
	 *
	 * @author  wangjf
	 * @date    2015年7月23日 下午1:42:12
	 * @param bankCardInfoEntity
	 * @return
	 */
	public void insertBank(final BankCardInfoEntity bankCardInfoEntity) throws SLException;
	
	/**
	 * 根据客户ID查询有效银行卡信息
	 * 
	 * @author zhangt
	 * @date   2016年2月23日上午11:51:38
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> findBankCardList(Map<String, Object> param);
	
	/**
	 * 线下业务-附属银行卡-客户银行卡审核列表
	 *
	 * @author  liyy
	 * @date    2016年2月24日 
	 * @param param
     *      <tt>start          :String:起始值</tt><br>
     *      <tt>length         :String:长度</tt><br>
     *      <tt>custName       :String:用户名（可选）</tt><br>
     *      <tt>credentialsCode:String:证件号码(可选)</tt><br>
     *      <tt>mobile         :String:手机号（可选）</tt><br>
     *      <tt>auditStatus    :String:审核状态（可选）</tt><br>
     *      <tt>bankCardNo     :String:银行卡号（可选）</tt><br>
     * @return Page
     */
	public Page<Map<String, Object>> queryWealthBankList(
			Map<String, Object> param);

	/**
	 * 线下业务-附属银行卡-客户银行卡查看详情
	 *
	 * @author  liyy
	 * @date    2016年2月24日 
	 * @param param
     *      <tt>tradeFlowId    :String:交易过程流水ID</tt><br>
	 * @return List
	 * 		<tt>tradeFlowId    :String:交易过程流水ID</tt><br>
	 * 		<tt>custName    :String:客户名称</tt><br>
	 *  	<tt>credentialsCode    :String:证件号码</tt><br>
	 *  	<tt>mobile    :String:手机</tt><br>
	 *  	<tt>bankName    :String:银行名称</tt><br>
	 *  	<tt>bankCardNo    :String:银行卡号</tt><br>
	 *  	<tt>branchBankName    :String:支行名称</tt><br>
	 *  	<tt>openProvince    :String:开户省</tt><br>
	 *  	<tt>openCity    :String:开户市</tt><br>
	 *  	<tt>cardId    :String:银行卡表主键ID</tt><br>
	 *  	<tt>custId    :String:客户ID</tt><br>
	 */
	public List<Map<String, Object>> queryBankDetailById(
			Map<String, Object> param);
	
	/**
	 * 线下业务-附属银行卡-客户银行卡查看详情-附件列表
	 *
	 * @author  liyy
	 * @date    2016年2月24日 
	 * @param param :Map
	 * 		<tt>tradeFlowId :String:交易过程流水ID</tt><br>
	 * 		<tt>custName :String:客户名称</tt><br>
	 * 		<tt>credentialsCode :String:证件号码</tt><br>
	 * 		<tt>mobile :String:手机</tt><br>
	 * 		<tt>bankName :String:银行名称</tt><br>
	 * 		<tt>bankCardNo :String:银行卡号</tt><br>
	 * 		<tt>branchBankName :String:支行名称</tt><br>
	 * 		<tt>openProvince :String:开户省</tt><br>
	 * 		<tt>openCity :String:开户市</tt><br>
	 * @return List
	 */
	public List<Map<String, Object>> queryAttachmentInfoListById(
			Map<String, Object> param);

	/**
	 * 线下业务-附属银行卡-客户银行卡查看详情-审核列表
	 *
	 * @author  liyy
	 * @date    2016年2月24日 
	 * @param param :Map
	 * 		<tt>tradeFlowId :String:交易过程流水ID</tt><br>
	 * 		<tt>custName :String:客户名称</tt><br>
	 * 		<tt>credentialsCode :String:证件号码</tt><br>
	 * 		<tt>mobile :String:手机</tt><br>
	 * 		<tt>bankName :String:银行名称</tt><br>
	 * 		<tt>bankCardNo :String:银行卡号</tt><br>
	 * 		<tt>branchBankName :String:支行名称</tt><br>
	 * 		<tt>openProvince :String:开户省</tt><br>
	 * 		<tt>openCity :String:开户市</tt><br>
	 * @return List
	 */
	public List<Map<String, Object>> queryAuditInfoListById(
			Map<String, Object> param);
	
	/**
	 * 提现银行卡列表
	 * 
	 * @author zhangt
	 * @date   2016年2月27日下午1:29:12
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> queryWithDrawBankList(Map<String, Object> param);

	public Map<String, Object> queryBankCardInfoByLoanId(String loanId);
}
