/** 
 * @(#)AccountService.java 1.0.0 2015年4月21日 上午11:24:11  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */

package com.slfinance.shanlincaifu.service;

import java.util.List;
import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AccountInfoEntity;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;

/**
 * 账户服务接口
 * 
 * @author HuangXiaodong
 * @version $Revision:1.0.0, $Date: 2015年4月21日 上午11:24:11 $
 */
public interface AccountService {

	public AccountInfoEntity findByCustId(String custId);

	public ResultVo findAccountInfo(Map<String, Object> param);

	/**
	 * 用户资金统计
	 *
	 * @author wangjf
	 * @date 2015年4月25日 上午11:24:31
	 * @param param
	 *            <tt>nickName：String:用户昵称</tt><br>
	 *            <tt>custName：String:真实姓名</tt><br>
	 *            <tt>credentialsCode：String:证件号码</tt><br>
	 *            <tt>opearteDateBegin：String:操作开始时间</tt><br>
	 *            <tt>opearteDateEnd：String:操作开始时间</tt><br>
	 *            <tt>tradeType：String:交易类型</tt><br>
	 * @return <tt>totalAccountTotalAmount：BigDecimal:账户余额</tt><br>
	 *         <tt>totalAccountAvailable：BigDecimal:可用余额</tt><br>
	 *         <tt>totalAccountFreezeAmount：BigDecimal:冻结余额</tt><br>
	 *         <tt>totalIncomeAmount：BigDecimal:已得收益</tt><br>
	 */
	public Map<String, Object> findAllCustAccountSum(Map<String, Object> param);

	
	/**
	 * 用户资金统计
	 *
	 * @author mzhu
	 * @date 2015年4月25日 上午11:24:31
	 * @param param
	 *            <tt>nickName：String:用户昵称</tt><br>
	 *            <tt>custName：String:真实姓名</tt><br>
	 *            <tt>credentialsCode：String:证件号码</tt><br>
	 *            <tt>opearteDateBegin：String:操作开始时间</tt><br>
	 *            <tt>opearteDateEnd：String:操作开始时间</tt><br>
	 *            <tt>tradeType：String:交易类型</tt><br>
	 */
	public List<Map<String, Object>> findAllAccountFlowSumNew(
			Map<String, Object> param);

	/**
	 * 用户资金列表
	 *
	 * @author wangjf
	 * @date 2015年4月25日 上午11:28:39
	 * @param param
	 *            <tt>start：int:分页起始页</tt><br>
	 *            <tt>length：int:每页长度</tt><br>
	 *            <tt>nickName：String:用户昵称</tt><br>
	 *            <tt>custName：String:真实姓名</tt><br>
	 *            <tt>credentialsCode：String:证件号码</tt><br>
	 *            <tt>opearteDateBegin：String:操作开始时间</tt><br>
	 *            <tt>opearteDateEnd：String:操作开始时间</tt><br>
	 * @return iTotalDisplayRecords: 总条数 data:List<Map<String, object>>
	 *         Map<String, object>: <tt>id：String:用户ID</tt><br>
	 *         <tt>nickName：String:用户昵称</tt><br>
	 *         <tt>custName：String:真实姓名</tt><br>
	 *         <tt>credentialsCode：String:证件号码</tt><br>
	 *         <tt>accountAvailable：BigDecimal:可用余额</tt><br>
	 *         <tt>accountFreezeAmount：BigDecimal:冻结余额</tt><br>
	 *         <tt>incomeAmount：BigDecimal:已得收益</tt><br>
	 *         <tt>operateDate：Date:最近操作时间</tt><br>
	 */
	@Rules(rules = {
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"),
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数") })
	public Map<String, Object> findAllCustAccountList(Map<String, Object> param);

	/**
	 * 提现申请
	 *
	 * @author wangjf
	 * @date 2015年4月28日 上午10:09:35
	 * @param params
	 *            <tt>custId： String:用户ID</tt><br>
	 *            <tt>mobile： String:手机号</tt><br>
	 *            <tt>mobileVerifyCode： String:验证码</tt><br>
	 *            <tt>amount： String:提现金额</tt><br>
	 *            <tt>withdrawPsd： String:提现密码</tt><br>
	 *            <tt>bankId： String:用户提现的银行卡ID</tt><br>
	 * @return
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "bankId", required = true, requiredMessage = "银行卡ID不能为空!"),
			@Rule(name = "custId", required = true, requiredMessage = "客户主键不能为空!"),
			@Rule(name = "amount", required = true, requiredMessage = "提现金额不能为空!", number = true, numberMessage = "提现金额只能为数字"),
			@Rule(name = "withdrawPsd", required = true, requiredMessage = "交易密码不能为空!"),
//			@Rule(name = "openProvince", required = true, requiredMessage = "开户行省份不能为空!"),
//			@Rule(name = "openCity", required = true, requiredMessage = "开户行市不能为空!"),
//			@Rule(name = "subBranchName", required = true, requiredMessage = "开户支行不能为空!") 
			})
	public ResultVo withdrawalCashAudit(Map<String, Object> params)
			throws SLException;

	/**
	 * 充值申请
	 *
	 * @author wangjf
	 * @date 2015年5月11日 下午8:07:08
	 * @param params
	 * @return
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "custId", required = true, requiredMessage = "客户主键不能为空!"),
			@Rule(name = "tradeAmount", required = true, requiredMessage = "充值金额不能为空!", number = true, numberMessage = "充值金额只能为数字") })
	public ResultVo rechargeApply(Map<String, Object> params)
			throws SLException;

	/**
	 * 充值校验
	 *
	 * @author wangjf
	 * @date 2015年5月30日 下午2:48:59
	 * @param params
	 * @throws SLException
	 */
	public void checkRecharge(Map<String, Object> params) throws SLException;
	
	/**
	 * 批量审核
	 *
	 * @author  wangjf
	 * @date    2015年8月3日 上午9:43:14
	 * @param paramsMap
	 *         <tt>
	 *		  	<tt>auditList  提现审核列表 	 {@link java.util.List}
	 *                <tt>id 	                     提现审核ID 	 {@link java.lang.String}</tt>  
	 *	        </tt>
	 *		  	<tt>auditStatus提现审核状态      {@link java.lang.String}</tt>
	 *		  	<tt>memo 	         提现审核备注 	 {@link java.lang.String}</tt>
	 *	     	<tt>custId 	         操作用户的id {@link java.lang.String}</tt>
	 *			<tt>version    版本信息             {@link java.lang.Integer}</tt>
	 *         </tt>
	 * @return
	 *         <tt>result {@link com.slfinance.vo.ResultVo} 返回结果</tt>
	 *         <tt>
	 *         <tt>result.success 是否成功 {@link java.lang.Boolean} required</tt>
	 *         <tt>result.data 处理结果 {@link java.lang.Map} required
	 *         		<tt>success 提现成功个数      {@link java.lang.String}</tt>
	 *              <tt>failed提现失败个数      {@link java.lang.String}</tt>
	 *         </tt>
	 *         </tt>
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "auditStatus", required = true, requiredMessage = "提现审核状态不能为空"),
			@Rule(name = "memo", required = true, requiredMessage = "提现审核备注不能为空"),
			@Rule(name = "custId", required = true, requiredMessage = "用户ID不能为空"),
			@Rule(name = "ipAddress", required = true, requiredMessage = "用户IP地址不能为空")
			})
	public ResultVo saveBatchWithdrawCashAudit(Map<String,Object> paramsMap) throws SLException;
	
	
	/**
	 * 获取奖励信息活期宝
	 * 
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> findRewardInfo(Map<String, Object> param) throws SLException;

	/**
	 * 充值补单
	 *
	 * @author  wangjf
	 * @date    2015年10月10日 上午11:50:10
	 * @param param
	 * @return
	 * @throws SLException
	 */
	public ResultVo mendRecharge(Map<String, Object> param) throws SLException;
	
	/**
	 * 提现补单
	 *
	 * @author  wangjf
	 * @date    2015年10月10日 上午11:50:43
	 * @param param
	 * @return
	 * @throws SLException
	 */
	public ResultVo mendWithdrawCash(Map<String, Object> param) throws SLException;
	
	/**
	 * 通过客户ID查询
	 *
	 * @author  wangjf
	 * @date    2015年12月24日 下午5:52:42
	 * @param param
	 * @return
	 */
	public ResultVo findAccountByCustId(Map<String, Object> param);
	
	
	/**
	 * 查询账户
	 * 
	 * @author zhangt
	 * @date   2016年2月25日下午4:08:44
	 * @param param
	 *      <tt>custId:String:客户ID</tt><br>
	 * @return
	 */
	@Rules(rules = {
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空")	
	})
	public ResultVo queryAccountNew(Map<String, Object> param);
	
	/**
	 * 用户代付(完成提现申请+提现审核)
	 *
	 * 注：该接口为内部接口，不验证交易密码，用于商户放款或者个人放款
	 * @author  wangjf
	 * @date    2016年10月29日 下午2:37:13
	 * @param param
	 * @return
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "custId", required = true, requiredMessage = "客户主键不能为空!"),
			@Rule(name = "userId", required = true, requiredMessage = "操作人不能为空!"),
			@Rule(name = "tradeAmount", required = true, requiredMessage = "代付金额不能为空!", number = true, numberMessage = "代付金额只能为数字") })
	public ResultVo grantAccount(Map<String, Object> param) throws SLException;
	
	/**
	 * 查询公司账户
	 *
	 * @author  wangjf
	 * @date    2016年12月17日 下午2:41:44
	 * @param param
	 * @return
	 * 		<tt>amtBalance： String:商户可用余额</tt><br>
	 * @throws SLException
	 */
	public ResultVo queryCompanyAccount(Map<String, Object> param)throws SLException;
}
