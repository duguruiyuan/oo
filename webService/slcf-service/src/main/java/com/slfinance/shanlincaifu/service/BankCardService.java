/** 
 * @(#)BankCardService.java 1.0.0 2015年4月21日 上午11:24:11  
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
 * 
 *  
 * @author  HuangXiaodong
 * @version $Revision:1.0.0, $Date: 2015年4月21日 上午11:24:11 $ 
 */
public interface BankCardService{
	
	/**
	 * 设置默认银行卡
	 *
	 * @author  richard
	 * @date    2015年4月23日 下午7:59:07
	 * @param param
	        <tt>custID：int:客户ID</tt><br>
	         <tt>custID：int:是否设置为默认</tt><br>
	 * @return
	 		<tt>resultVo： isSuccess:是否成功</tt><br>
	 */
	public ResultVo setDefault(Map<String, Object> param);
	
	/**
	 * 设置默认银行卡
	 *
	 * @author  richard
	 * @date    2015年4月23日 下午7:59:07
	 * @param param
	        <tt>bankCard：BankCardInfoEntity:银行卡信息</tt><br>
	 * @return
	 		<tt>resultVo： isSuccess:是否成功</tt><br>
	 */
	public ResultVo createBank(Map<String, Object> param) throws SLException;
	
	/**
	 * 查询银卡
	 *
	 * @author  wangjf
	 * @date    2015年5月18日 上午10:43:55
	 * @param param
	  			<tt>custId： String:客户ID</tt><br>
	 * @return
	 			<tt>id： String:银行卡主键</tt><br>
	 			<tt>custId： String:客户ID</tt><br>
	 			<tt>bankName： String:银行名称</tt><br>
	 			<tt>cardNo： String:银行卡卡号</tt><br>
	 			<tt>protocolNo： String:协议号</tt><br>
	 			<tt>bankCode： String:银行编号</tt><br>
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "custId", required = true, requiredMessage = "客户主键不能为空!")
	})
	public Map<String, Object> queryBankCard(Map<String, Object> param) throws SLException;

	/**
	 * 取第三方支持的银行
	 *
	 * @author  wangjf
	 * @date    2015年5月18日 下午7:43:06
	 * @return 
	 		<tt>： String:银行编号</tt><br>
	 		<tt>： String:银行名称</tt><br>
	 * @throws SLException
	 */
	public Map<String, Object> querySupportBank(Map<String, Object> param) throws SLException;
	
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
	@Rules(rules = { 
			@Rule(name = "bankCardNo", required = true, requiredMessage = "银行卡号不能为空!")
	})
	public Map<String, Object> queryThirdBankByCardNo(Map<String, Object> paramsMap) throws SLException;
	
	/**
	 * 查询银行卡
	 *
	 * @author  wangjf
	 * @date    2015年6月1日 下午2:49:34
	 * @param param
	 * 		<tt>custId： String:客户ID</tt><br>	
	 * @return
	 * 		<tt>id： String:银行卡主键</tt><br>
 			<tt>custId： String:客户ID</tt><br>
 			<tt>bankName： String:银行名称</tt><br>
 			<tt>cardNo： String:银行卡卡号</tt><br>
 			<tt>protocolNo： String:协议号</tt><br>
 			<tt>bankCode： String:银行编号</tt><br>
 			<tt>provinceName： String:省份名字</tt><br>
 			<tt>cityName： String:城市名字</tt><br>
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "custId", required = true, requiredMessage = "客户主键不能为空!")
	})
	public Map<String, Object> queryBankCardDetail(Map<String, Object> param) throws SLException;
	
	/**
	 * 解绑银行卡
	 *
	 * @author  wangjf
	 * @date    2015年7月8日 下午4:08:35
	 * @param param
	 * 		<tt>custId： String:客户ID</tt><br>	
	 * 		<tt>bankId： String:银行卡主键</tt><br>
	 * 	    <tt>tradePassword： String:交易密码</tt><br>
	 * 		<tt>unbindType： String:解绑类型（已丢失/未丢失）</tt><br>
	 * 		<tt>unbindReason： String:解绑原因</tt><br>		
	 * 		<tt>List<Map<String, String>>： attachmentList
	 * 			<tt>attachmentType： String:附件类型（手持银行卡照片/银行挂失证明/手持身份证正面/手持身份证反面）</tt><br>	
	 *          <tt>attachmentName： String:附件名称</tt><br>
	 *          <tt>storagePath： String:存储路径</tt><br>
	 *          <tt>docType： String:文档类型（WORD/EXCEL/PIG）</tt><br>
	 * 		</tt><br>	
	 * @return
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "custId", required = true, requiredMessage = "客户主键不能为空!"),
			@Rule(name = "bankId", required = true, requiredMessage = "银行主键不能为空!"),
			@Rule(name = "tradePassword", required = true, requiredMessage = "交易密码不能为空!"),
			@Rule(name = "unbindType", required = true, requiredMessage = "解绑类型不能为空!"),
			@Rule(name = "unbindReason", required = true, requiredMessage = "解绑原因不能为空!")
	})
	public ResultVo unBindBankCard(Map<String, Object> param) throws SLException;
	
	public ResultVo preauditUnBindCard(Map<String, Object> param) throws SLException;
	public ResultVo postauditUnBindCard(ResultVo result) throws SLException;
	
	/**
	 * 查询解绑的银行卡
	 *
	 * @author  wangjf
	 * @date    2015年7月9日 下午1:37:37
     * @param param
	 *      <tt>start：int:分页起始页</tt><br>
	 *      <tt>length：int:每页长度</tt><br>
	 * 		<tt>custId： String:客户主键(可选)</tt><br>
	 * 		<tt>bankId： String:银行卡主键(可选)</tt><br>
	 * @return 
	 * 		iTotalDisplayRecords: 总条数
 			data:List<Map<String, object>>
 			Map<String, object>:
	 *      <tt>unbindId： String:绑定ID</tt><br>
	 *      <tt>bankId： String:银行卡主键</tt><br>
	 * 		<tt>unbindType： String:绑定类型</tt><br>
	 * 		<tt>unbindStatus： String:绑定状态</tt><br>
	 * 		<tt>unbindDesc： String:绑定状描述</tt><br>
	 * 		<tt>createDate： Date:绑定时间</tt><br>
	 * 		
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数")
	})
	public Map<String, Object> queryUnBindCard(Map<String, Object> param) throws SLException;
	
	/**
	 * 根据申请ID查询解绑信息
	 *
	 * @author  wangjf
	 * @date    2015年7月9日 下午3:25:38
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
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "unbindId", required = true, requiredMessage = "解绑申请主键不能为空!")
	})
	public Map<String, Object> queryUnBindCardById(Map<String, Object> param)throws SLException;
	
	/**
	 * 查询所有解绑信息
	 *
	 * @author  wangjf
	 * @date    2015年7月9日 下午5:23:41
	 * @param param
	 * 		<tt>start：int:分页起始页</tt><br>
	  		<tt>length：int:每页长度</tt><br>
	  		<tt>loginName：String:用户昵称(可选)</tt><br>
	  		<tt>custName：String:真实姓名(可选)</tt><br>
	  		<tt>credentialsCode：String:证件号码(可选)</tt><br>
	  		<tt>mobile：String:手机号码(可选)</tt><br>
	  		<tt>opearteDateBegin：String:申请开始时间(可选)</tt><br>
	  		<tt>opearteDateEnd：String:申请结束时间(可选)</tt><br>
	  		<tt>unbindStatus： String:交易状态(可选)</tt><br>
	  		<tt>auditStatus： String:审核状态(可选)</tt><br>
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
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数")
	})
	public Map<String, Object> queryAllUnBindCard(Map<String, Object> param) throws SLException;
	
	/**
	 * 查询审核用的解绑信息
	 *
	 * @author  wangjf
	 * @date    2015年7月9日 下午5:24:50
	 * @param param
	 * 		<tt>unbindId： String:解绑申请ID</tt><br>
	 * @return
	  		<tt>userInfo: Map<String, Object>:用户信息
		 		<tt>loginName：String:用户昵称</tt><br>
		  		<tt>custName：String:真实姓名</tt><br>
		  		<tt>credentialsType：String:证件类型</tt><br>
		  		<tt>credentialsCode：String:证件号码</tt><br>
		  		<tt>mobile：String:手机号码</tt><br>
		  		<tt>custGender：String:客户性别</tt><br>
		  		<tt>bankName：String:银行名称</tt><br>
		  		<tt>cardNo：String:银行卡号</tt><br>
	  		</tt><br>
	  		
	  		<tt>unBindList: List<Map<String, Object>>:历史解约列表
	  			 <tt>createDate：String:申请时间</tt><br>
		         <tt>bankName：String:银行名称</tt><br>
		         <tt>cardNo：String:银行卡号</tt><br>
		         <tt>unbindType： String:银行卡信息</tt><br>
		         <tt>auditDate： String:审核时间</tt><br>
		         <tt>auditUser： String:审核人(ID)</tt><br>
		         <tt>auditUserName： String:审核人</tt><br>
		         <tt>auditStatus： String:审核状态</tt><br>
		         <tt>auditMemo： String:审核备注</tt><br>		
	  		</tt><br>
	  		
	  		<tt>unbindInfo: Map<String, Object>:用户信息
		  	    <tt>unbindId： String:绑定ID</tt><br>
		        <tt>bankId： String:银行卡主键</tt><br>
		  		<tt>unbindType： String:绑定类型</tt><br>
		  		<tt>unbindStatus： String:绑定状态</tt><br>
		  		<tt>unbindDesc： String:绑定状描述</tt><br>
		  		<tt>createDate： Date:绑定时间</tt><br>
		        <tt>attachmentList：List<Map<String, Object>:附件列表
		       	   <tt>attachmentId: String:附件ID
		  		   <tt>attachmentType： String:附件类型（手持银行卡照片/银行挂失证明/手持身份证正面/手持身份证反面）</tt><br>	
		           <tt>attachmentName： String:附件名称</tt><br>
		           <tt>storagePath： String:存储路径</tt><br>
		           <tt>docType： String:文档类型（WORD/EXCEL/PIG）</tt><br>
		        </tt><br>
		     </tt><br>  
		     
		     <tt>auditInfo: Map<String, Object>:审核信息
		     	 <tt>auditId： String:审核ID</tt><br>
		     	 <tt>auditUser： String:审核人</tt><br>
		         <tt>auditStatus： String:审核状态</tt><br>
		         <tt>auditMemo： String:审核备注</tt><br>
		     </tt><br> 	 
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "unbindId", required = true, requiredMessage = "解绑申请主键不能为空!")
	})
	public Map<String, Object> queryAuditUnBindCardById(Map<String, Object> param) throws SLException;
	
	/**
	 * 审核解绑银行卡
	 *
	 * @author  wangjf
	 * @date    2015年7月10日 上午9:47:09
	 * @param param
	 * 		<tt>auditId： String:审核ID</tt><br>
	 * 		<tt>auditUserId： String:审核人ID</tt><br>
	 * 		<tt>auditStatus： String:审核状态</tt><br>
		    <tt>auditMemo： String:审核备注</tt><br>	
	 * @return
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "auditId", required = true, requiredMessage = "审核ID不能为空!"),
			@Rule(name = "auditUserId", required = true, requiredMessage = "审核人ID不能为空!"),
			@Rule(name = "auditStatus", required = true, requiredMessage = "审核状态不能为空!"),
			@Rule(name = "auditMemo", required = true, requiredMessage = "审核备注不能为空!")
	})
	public ResultVo auditUnBindCard(Map<String, Object> param) throws SLException;
	
	/**
	 * 查询银行卡管理列表
	 *
	 * @author  wangjf
	 * @date    2015年7月16日 下午12:00:48
	 * @param param
	 *          <tt>start：int:分页起始页</tt><br>
	  			<tt>length：int:每页长度</tt><br>
	  			<tt>custId： String:客户ID</tt><br>
	 * @return
	 * 		iTotalDisplayRecords: 总条数
 			data:List<Map<String, object>>
 			Map<String, object>:
	 			<tt>id： String:银行卡主键</tt><br>
	 			<tt>custId： String:客户ID</tt><br>
	 			<tt>bankName： String:银行名称</tt><br>
	 			<tt>bankCode： String:银行编号</tt><br>
	 			<tt>cardNo： String:银行卡卡号</tt><br>
	 			<tt>recordStatus： String:绑定状态</tt><br>
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数"),
			@Rule(name = "custId", required = true, requiredMessage = "客户主键不能为空!")
	})
	public Map<String, Object> queryBankManager(Map<String, Object> param) throws SLException;
	
	/**
	 * 补银行卡信息
	 *
	 * @author  wangjf
	 * @date    2015年8月6日 上午11:31:14
	 * @return
	 * @throws SLException
	 */
	public ResultVo mendBankCard() throws SLException;
	
	/**
	 * 银行卡列表（附属银行卡）
	 * 
	 * @author zhangt
	 * @date   2016年2月23日上午10:10:32
	 * @param param
     *      <tt>custId:String:客户ID</tt><br>
	 * @return
	 *      <tt>bankId      :String:银行主键</tt><br>
     *      <tt>bankName    :String:银行名称</tt><br>
     *      <tt>bankCardNo  :String:银行卡号</tt><br>
     *      <tt>bankCode    :String:银行编号</tt><br>
     *      <tt>recordStatus:String:绑定状态</tt><br>
	 */
	@Rules(rules = { 
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空!")
	})
	public ResultVo queryBankList(Map<String, Object> param);
	
	/**
	 * 查看附属银行卡
	 * 
	 * @author zhangt
	 * @date   2016年2月23日上午10:11:36
	 * @param param
	 *      <tt>bankId       :String:银行卡ID</tt><br>
     *      <tt>custId       :String:客户ID</tt><br>
     *      <tt>tradePassword:String:交易密码</tt><br>
	 * @return
	 *      <tt>bankId      :String:银行卡ID</tt><br>
     *      <tt>bankName    :String:银行名称</tt><br>
     *      <tt>bankCardNo  :String:银行卡号</tt><br>
     *      <tt>bankCode    :String:银行编号</tt><br>
     *      <tt>recordStatus:String:绑定状态</tt><br>
	 */
	@Rules(rules = { 
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空!"),
			@Rule(name = "bankId", required = true, requiredMessage = "银行卡ID不能为空!"),
			@Rule(name = "tradePassword", required = true, requiredMessage = "交易密码不能为空!")
	})
	public ResultVo queryWealthBankDetailByBankId(Map<String, Object> param);
	
	/**
	 * 
	 * @author zhangt
	 * @date   2016年2月23日下午4:21:22
	 * @param param
	 *      <tt>bankId        :String:银行卡ID</tt><br>
     *      <tt>custId        :String:客户ID</tt><br>
     *      <tt>tradePassword :String:交易密码</tt><br>
     *      <tt>branchBankName:String:支行名称</tt><br>
     *      <tt>openProvince  :String:开户行所在省</tt><br>
     *      <tt>openCity      :String:开户行所在市</tt><br>
	 * @return ResultVo
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空!"),
			@Rule(name = "bankId", required = true, requiredMessage = "银行卡ID不能为空!"),
			@Rule(name = "tradePassword", required = true, requiredMessage = "交易密码不能为空!")
	})
	public ResultVo updateBank(Map<String, Object> param) throws SLException;
	
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
	 * @return ResultVo
	 * 		<tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
     *      <tt>data
     *      	<tt>iTotalDisplayRecords:String:总数</tt><br>
     *      	<tt>data
     *      		<tt>tradeFlowId    :String:交易过程流水ID</tt><br>
     *      		<tt>custName       :String:用户名</tt><br>
     *      		<tt>credentialsCode:String:证件号码</tt><br>
     *      		<tt>mobile         :String:手机号</tt><br>
     *      		<tt>bankName       :String:银行名称</tt><br>
     *      		<tt>bankCardNo     :String:银行卡号</tt><br>
     *      		<tt>branchBankName :String:支行名称</tt><br>
     *      		<tt>openProvince   :String:开户行所在省</tt><br>
     *      		<tt>openCity       :String:开户行所在市</tt><br>
     *      		<tt>auditStatus    :String:审核状态</tt><br>
     *          </tt><br>
     *      </tt><br>
	 */
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数"),
	})
	public ResultVo queryWealthBankList(Map<String, Object> param);
	
	/**
	 * 线下业务-附属银行卡-客户银行卡查看详情
	 *
	 * @author  liyy
	 * @date    2016年2月24日 
	 * @param param
     *      <tt>tradeFlowId    :String:交易过程流水ID</tt><br>
	 * @return ResultVo
	 * 		<tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
     *      <tt>data
     *      	<tt>tradeFlowId    :String:交易过程流水ID</tt><br>
     *      	<tt>custName       :String:用户名</tt><br>
     *      	<tt>credentialsCode:String:证件号码</tt><br>
     *      	<tt>mobile         :String:手机号</tt><br>
     *      	<tt>bankName       :String:银行名称</tt><br>
     *      	<tt>bankCardNo     :String:银行卡号</tt><br>
     *      	<tt>branchBankName :String:支行名称</tt><br>
     *      	<tt>openProvince   :String:开户行所在省</tt><br>
     *      	<tt>openCity       :String:开户行所在市</tt><br>
     *      	<tt>attachmentList:List<Map<String, Object>>:附件列表
     *     			<tt>attachmentType:String:         附件类型</tt><br>
     *      		<tt>attachmentName:String:         附件名称</tt><br>
     *      		<tt>storagePath   :String:         存储路径</tt><br>
     *      	</tt><br>
     *     		<tt>auditList:List<Map<String, Object>>:审核列表
     *      		<tt>审核ID:String:         auditId</tt><br>
     *      		<tt>审核时间:String:         auditDate</tt><br>
     *      		<tt>审核人员:String:         auditUser</tt><br>
     *      		<tt>审核结果:String:         auditStatus</tt><br>
     *      		<tt>审核备注:String:         auditMemo</tt><br>
     *      	</tt><br>
     *      </tt><br>
	 */
	@Rules(rules = { 
			@Rule(name = "tradeFlowId", required = true, requiredMessage = "交易过程流水ID不能为空")
	})
	public ResultVo queryWealthBankDetailById(Map<String, Object> param);
	
	/**
	 * 线下业务-附属银行卡-客户银行卡审核
	 *
	 * @author  liyy
	 * @date    2016年2月25日 
	 * @param param
     *      <tt>tradeFlowId:String:交易过程流水ID</tt><br>
     *      <tt>auditStatus:String:审核状态</tt><br>
     *      <tt>auditMemo  :String: 审核备注</tt><br>
     *      <tt>userId     :String:创建人</tt><br>
	 * @return ResultVo
	 * 		<tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
     *  @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "tradeFlowId", required = true, requiredMessage = "交易过程流水ID不能为空")
			, @Rule(name = "auditStatus", required = true, requiredMessage = "审核状态不能为空")
			, @Rule(name = "auditMemo", required = true, requiredMessage = "审核备注不能为空")
			, @Rule(name = "userId", required = true, requiredMessage = "创建人不能为空")
	})
	public ResultVo auditWealthBank(Map<String, Object> param) throws SLException;
	
	
	/**
	 * 提现银行卡列表
	 * 
	 * @author zhangt
	 * @date   2016年2月27日下午1:25:36
	 * @param param
	 *      <tt>custId:String:客户ID</tt><br>
	 * @return
	 *      <tt>id           :String:银行卡ID</tt><br>
     *      <tt>custId       :String:客户ID</tt><br>
     *      <tt>bankName     :String:银行名称</tt><br>
     *      <tt>cardNo       :String:银行卡号</tt><br>
     *      <tt>protocolNo   :String:协议号（线上充值有，线下没有）</tt><br>
     *      <tt>openProvince :String:开户行省份</tt><br>
     *      <tt>openCity     :String:开户行城市</tt><br>
     *      <tt>subBranchName:String:支行名称</tt><br>
     *      <tt>bankCode     :String:银行编号</tt><br>
     *      <tt>bankFlag     :String:银行表示（线上、线下）</tt><br>
	 */
	@Rules(rules = { 
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空!")
	})
	public ResultVo queryWithDrawBankList(Map<String, Object> param);
	
	/**
	 * 查询修改银行卡详情
	 * 
	 * @author zhangt
	 * @date   2016年2月27日下午2:56:39
	 * @param param
	 *       <tt>bankId:String:银行卡ID</tt><br>
	 * @return
	 *      <tt>id           :String:银行卡ID</tt><br>
     *      <tt>custId       :String:客户ID</tt><br>
     *      <tt>bankName     :String:银行名称</tt><br>
     *      <tt>cardNo       :String:银行卡号</tt><br>
     *      <tt>protocolNo   :String:协议号（线上充值有，线下没有）</tt><br>
     *      <tt>openProvince :String:开户行省份</tt><br>
     *      <tt>openCity     :String:开户行城市</tt><br>
     *      <tt>subBranchName:String:支行名称</tt><br>
     *      <tt>bankCode     :String:银行编号</tt><br>
     *      <tt>bankFlag     :String:银行表示（线上、线下）</tt><br>
	 */
	@Rules(rules = { 
			@Rule(name = "bankId", required = true, requiredMessage = "银行卡ID不能为空!")
	})
	public ResultVo queryBankById(Map<String, Object> param);
	
	/**
	 * 查询充值银行卡列表
	 * 
	 * @param param
	 * @return
	 */
	public ResultVo queryRechargeBankList(Map<String, Object> param);
	
	/**
	 * 判断是否允许展示线下银行卡
	 * @author  liyy
	 * @date    2016年5月31日
	 * @param param
	        <tt>custId:客户ID</tt><br>
	 * @return
	 		<tt>resultVo： isSuccess:是否成功</tt><br>
	 */
	@Rules(rules = { 
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空!")
	})
	public ResultVo queryCanShowCustBank(Map<String, Object> param);
	
	/**
	 * 认证银行卡列表
	 * @author  liyy
	 * @date    2016年5月31日
	 * @param param
	        <tt>custId:客户ID</tt><br>
	 * @return
	 		<tt>resultVo： isSuccess:是否成功</tt><br>
	 */
	@Rules(rules = { 
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空!")
	})
	public ResultVo queryAuthBankList(Map<String, Object> param);
	
	
	/**
	 * 绑定或解绑银行卡
	 *
	 * @author  wangjf
	 * @date    2016年11月30日 下午4:11:14
	 * @param param
	 * @return
	 * @throws SLException 
	 */
	@Rules(rules = { 
			@Rule(name = "tradeType", required = true, requiredMessage = "交易类型不能为空!"),
			@Rule(name = "custName", required = true, requiredMessage = "客户姓名不能为空!"),
			@Rule(name = "credentialsCode", required = true, requiredMessage = "身份证号码不能为空!"),
			@Rule(name = "bankName", required = true, requiredMessage = "银行卡名称不能为空!"),
			@Rule(name = "bankCardNo", required = true, requiredMessage = "银行卡卡号不能为空!")
	})
	public ResultVo bindBankCard(Map<String, Object> param) throws SLException;
	
	/**
	 * 补全单张银行卡信息
	 *
	 * @author  wangjf
	 * @date    2016年12月9日 下午12:17:40
	 * @param paramsMap
	 * @return
	 */
	public ResultVo mendOneBank(Map<String, Object> paramsMap);
	
	/**
	 * 根据loanId查询银行卡信息
	 *
	 * @author  guoyk
	 * @date    2017年7月2日 
	 * @param paramsMap
	 * @return
	 */
	public Map<String, Object> queryBankCardInfoByLoanId(String loanId);
	
	
}
