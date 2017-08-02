/** 
 * @(#)UnbindInfoRepositoryCustom.java 1.0.0 2015年7月9日 下午2:03:48  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository.custom;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.UnbindInfoEntity;

/**   
 * 银行卡解绑自定义数据访问接口
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年7月9日 下午2:03:48 $ 
 */
public interface UnbindInfoRepositoryCustom {
	
	/**
	 * 查询解绑银行卡信息
	 *
	 * @author  wangjf
	 * @date    2015年7月9日 下午2:07:28
	 * @param param
	 *      <tt>start：int:分页起始页</tt><br>
	 *      <tt>length：int:每页长度</tt><br>
	 * 		<tt>custId： String:客户主键(可选)</tt><br>
	 * 		<tt>bankId： String:银行卡主键(可选)</tt><br>
	 * @return 
	 *      <tt>unbindId： String:绑定ID</tt><br>
	 *      <tt>bankId： String:银行卡主键</tt><br>
	 * 		<tt>unbindType： String:绑定类型</tt><br>
	 * 		<tt>unbindStatus： String:绑定状态</tt><br>
	 * 		<tt>unbindDesc： String:绑定状描述</tt><br>
	 * 		<tt>createDate： Date:绑定时间</tt><br>
	 */
	public Page<Map<String, Object>> findUnBindList(Map<String, Object> param);
	
	/**
	 * 根据解绑申请ID查询解绑信息
	 *
	 * @author  wangjf
	 * @date    2015年7月9日 下午2:54:31
	 * @param param
	 *      <tt>unbindId： String:绑定ID</tt><br> 
	 * @return
	 *      <tt>unbindId： String:绑定ID</tt><br>
	 *      <tt>bankId： String:银行卡主键</tt><br>
	 * 		<tt>unbindType： String:绑定类型</tt><br>
	 * 		<tt>unbindStatus： String:绑定状态</tt><br>
	 * 		<tt>unbindDesc： String:绑定状描述</tt><br>
	 * 		<tt>createDate： Date:绑定时间</tt><br>
	 *      <tt>attachmentList：List<Map<String, Object>:附件列表
	 *      	<tt>attachmentId:String:附件ID
	 * 			<tt>attachmentType： String:附件类型（手持银行卡照片/银行挂失证明/手持身份证正面/手持身份证反面）</tt><br>	
	 *          <tt>attachmentName： String:附件名称</tt><br>
	 *          <tt>storagePath： String:存储路径</tt><br>
	 *          <tt>docType： String:文档类型（WORD/EXCEL/PIG）</tt><br>
	 *      </tt><br>
	 */
	public Map<String, Object> findUnBindById(Map<String, Object> param);

	/**
	 * 查询所有解绑信息
	 *
	 * @author  wangjf
	 * @date    2015年7月9日 下午4:26:26
	 * @param param
	 * 		<tt>start：int:分页起始页</tt><br>
	  		<tt>length：int:每页长度</tt><br>
	  		<tt>loginName：String:用户昵称</tt><br>
	  		<tt>custName：String:真实姓名</tt><br>
	  		<tt>credentialsCode：String:证件号码</tt><br>
	  		<tt>mobile：String:手机号码</tt><br>
	  		<tt>opearteDateBegin：String:申请开始时间</tt><br>
	  		<tt>opearteDateEnd：String:申请结束时间</tt><br>
	  		<tt>unbindStatus： String:交易状态</tt><br>
	  		<tt>auditStatus： String:审核状态</tt><br>
	 * @return
	 * 		<tt>unbindId： String:解绑申请ID</tt><br>
	 * 		<tt>bankId： String:银行卡主键</tt><br>
	  		<tt>custId： String:客户ID</tt><br>
	  		<tt>loginName：String:用户昵称</tt><br>
	  		<tt>custName：String:真实姓名</tt><br>
	  		<tt>credentialsCode：String:证件号码</tt><br>
	  		<tt>mobile：String:手机号码</tt><br>
	  		<tt>createDate：String:申请时间</tt><br>
	  		<tt>unbindStatus： String:交易状态</tt><br>
	  		<tt>auditStatus： String:审核状态</tt><br>
	 */
	public Page<Map<String, Object>> findAllUnBindList(Map<String, Object> param);
	
	/**
	 * 查询用户银行信息
	 *
	 * @author  wangjf
	 * @date    2015年7月9日 下午4:47:59
	 * @param param
	 * 		<tt>bankId： String:银行卡主键(可选)</tt><br>
	 * @return
	 * 		<tt>loginName：String:用户昵称</tt><br>
	  		<tt>custName：String:真实姓名</tt><br>
	  		<tt>credentialsType：String:证件类型</tt><br>
	  		<tt>credentialsCode：String:证件号码</tt><br>
	  		<tt>mobile：String:手机号码</tt><br>
	  		<tt>custGender：String:客户性别</tt><br>
	  		<tt>bankName：String:银行名称</tt><br>
	  		<tt>cardNo：String:银行卡号</tt><br>
	 */
	public Map<String, Object> findUserBankInfo(Map<String, Object> param);
	
	/**
	 * 根据客户ID查询解约申请信息
	 *
	 * @author  wangjf
	 * @date    2015年7月9日 下午4:50:51
	 * @param param
	 * 		<tt>custId： String:客户ID</tt><br>
	 * @return
	 * 		<tt>createDate：String:申请时间</tt><br>
	 *      <tt>bankName：String:银行名称</tt><br>
	 *      <tt>cardNo：String:银行卡号</tt><br>
	 *      <tt>unbindType： String:银行卡信息</tt><br>
	 *      <tt>auditDate： String:审核时间</tt><br>
	 *      <tt>auditUser： String:审核人</tt><br>
	 *      <tt>auditStatus： String:审核状态</tt><br>
	 *      <tt>auditMemo： String:审核备注</tt><br>
	 */
	public List<Map<String, Object>> findUnBindByCustId(Map<String, Object> param);
	
	/**
	 * 插入解绑银行卡
	 *
	 * @author  wangjf
	 * @date    2015年8月19日 下午3:50:06
	 * @param unbindInfoEntity
	 */
	public UnbindInfoEntity insertUnBindInfo(UnbindInfoEntity unbindInfoEntity) throws SLException;
}
