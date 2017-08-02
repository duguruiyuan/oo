/** 
 * @(#)AccountFlowService.java 1.0.0 2015年4月25日 下午6:17:31  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AccountFlowInfoEntity;
import com.slfinance.shanlincaifu.entity.AccountInfoEntity;
import com.slfinance.shanlincaifu.entity.CustInfoEntity;
import com.slfinance.shanlincaifu.entity.SubAccountInfoEntity;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;
import java.math.BigDecimal;
import java.util.Map;
import org.springframework.data.domain.Page;

/**   
 * 账户流水接口
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年4月25日 下午6:17:31 $ 
 */
public interface AccountFlowService {
	
	/**
	 * 用户资金流水列表
	 *
	 * @author  wangjf
	 * @date    2015年4月25日 上午11:32:03
	 * @param param
	        <tt>start：int:分页起始页</tt><br>
	  		<tt>length：int:每页长度</tt><br>
	  		<tt>nickName：String:用户昵称</tt><br>
	  		<tt>custName：String:真实姓名</tt><br>
	  		<tt>credentialsCode：String:证件号码</tt><br>
	  		<tt>opearteDateBegin：String:操作开始时间</tt><br>
	  		<tt>opearteDateEnd：String:操作开始时间</tt><br>
	  		<tt>tradeType：String:交易类型</tt><br>
	 * @return
	  		iTotalDisplayRecords: 总条数
	 		data:List<Map<String, object>>
	 		Map<String, object>:
	 	    <tt>id：String:用户ID</tt><br>
	  		<tt>nickName：String:用户昵称</tt><br>
	 		<tt>tradeType：String:交易类型</tt><br>
	 		<tt>tradeAmount：BigDecimal:操作金额</tt><br>
	  		<tt>accountAvailable：BigDecimal:可用余额</tt><br>
	  		<tt>accountFreezeAmount：BigDecimal:冻结余额</tt><br>
	 		<tt>bankrollFlowDirection：String:资金流向</tt><br>
	  		<tt>targetNickName：String:对方账户</tt><br>
	  		<tt>operateDate：Date:操作时间</tt><br>
	 */
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数") 
	})
	Map<String, Object> findAllAccountFlowList(Map<String, Object> param);
	
	/**
	 * 充值管理--统计
	 *
	 * @author  wangjf
	 * @date    2015年4月25日 上午11:35:54
	 * @param map
	 		<tt>nickName：String:用户昵称</tt><br>
	  		<tt>custName：String:真实姓名</tt><br>
	  		<tt>credentialsCode：String:证件号码</tt><br>
	  		<tt>opearteDateBegin：String:操作开始时间</tt><br>
	  		<tt>opearteDateEnd：String:操作开始时间</tt><br>
	  		<tt>tradeType：String:交易类型</tt><br>
	  		<tt>tradeStatus：String:交易状态</tt><br>
	  		<tt>tradeNo：String:交易编号</tt><br>
	 * @return
	  		<tt>totalTradeAmount：BigDecimal:充值金额汇总</tt><br>
	  		<tt>totalTradeExpenses：BigDecimal:充值手续费汇总</tt><br>
	  		<tt>totalFactAmount：BigDecimal:实际到账金额汇总</tt><br>
	 */
	public Map<String, Object> findAllRechargeSum(Map<String, Object> map);
	
	/**
	 * 充值管理--列表
	 *
	 * @author  wangjf
	 * @date    2015年4月25日 上午11:33:55
	 * @param map
	        <tt>start：int:分页起始页</tt><br>
	  		<tt>length：int:每页长度</tt><br>
	 		<tt>nickName：String:用户昵称</tt><br>
	  		<tt>custName：String:真实姓名</tt><br>
	  		<tt>credentialsCode：String:证件号码</tt><br>
	  		<tt>opearteDateBegin：String:操作开始时间</tt><br>
	  		<tt>opearteDateEnd：String:操作开始时间</tt><br>
	  		<tt>tradeType：String:交易类型</tt><br>
	  		<tt>tradeStatus：String:交易状态</tt><br>
	  		<tt>tradeNo：String:交易编号</tt><br>
	 * @return
	  		iTotalDisplayRecords: 总条数
	 		data:List<Map<String, object>>
	 		Map<String, object>:
	 		<tt>custId：String:客户ID</tt><br>
	        <tt>flowId：String:交易过程流水ID</tt><br>
	  		<tt>nickName：String:用户昵称</tt><br>
	  		<tt>tradeNo：String:交易编号</tt><br>
	 		<tt>tradeType：String:交易类型</tt><br>
	 		<tt>bankName：String:充值银行</tt><br>
	 		<tt>tradeAmount：BigDecimal:充值金额</tt><br>
	  		<tt>tradeExpenses：BigDecimal:手续额</tt><br>
	  		<tt>factAmount：BigDecimal:实际到账金额</tt><br>
	 		<tt>tradeStatus：String:交易状态</tt><br>
	  		<tt>operateDate：Date:操作时间</tt><br>
	  		<tt>ipAddress：String:IP地址</tt><br>
	 */
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数") 
	})
	public Map<String, Object> findAllRechargeList(Map<String, Object> map);
	
	/**
	 * 充值管理--明细
	 *
	 * @author  wangjf
	 * @date    2015年4月25日 上午11:33:55
	 * @param flowId
	 * @return
	        <tt>id：String:用户ID</tt><br>
	  		<tt>nickName：String:用户昵称</tt><br>
	  		<tt>custName：String:真实姓名</tt><br>
	  		<tt>credentialsCode：String:证件号码</tt><br>
	  		<tt>tradeNo：String:交易编号</tt><br>
	 		<tt>tradeType：String:交易类型</tt><br>
	 		<tt>bankName：String:充值银行</tt><br>
	 		<tt>branchBankName：String:支行名称</tt><br>
	 		<tt>bankCardNo：String:银行卡号</tt><br>
	 		<tt>tradeAmount：BigDecimal:充值金额</tt><br>
	  		<tt>tradeExpenses：BigDecimal:手续额</tt><br>
	  		<tt>factAmount：BigDecimal:实际到账金额</tt><br>
	  		<tt>memo：String:备注</tt><br>
	 		<tt>tradeStatus：String:交易状态</tt><br>
	  		<tt>operateDate：Date:操作时间</tt><br>		
	 */
	@Rules(rules = { 
			@Rule(name = "flowId", required = true, requiredMessage = "交易过程流水ID不能为空!")
	})
	public Map<String, Object> findRechargeDetailInfo(Map<String, Object> map);
	
	/**
	 * 插入充值过程流水
	 *
	 * @author  wangjf
	 * @date    2015年4月28日 上午10:30:07
	 * @param params
	 * 		<tt>custId： String:用户ID</tt><br>
			<tt>bankNo： String:银行编号</tt><br>
			<tt>tradeAmount： BigDecimal:充值金额</tt><br> 
			<tt>tradeCode： String:交易编号（跟第三方保持一致）</tt><br>
	 * @return
	 */
	public void insertRechargeFlowProcessInfo(Map<String, Object> params) throws SLException;
	
	/**
	 * 充值成功回调
	 *
	 * @author  wangjf
	 * @date    2015年4月28日 上午11:44:09
	 * @param params
	 * @throws SLException
	 */
	public void callbackRechargeSuccess(Map<String, Object> params) throws SLException;
	
	/**
	 * 充值失败回调
	 *
	 * @author  wangjf
	 * @date    2015年4月28日 上午11:44:54
	 * @param params
	 * @throws SLException
	 */
	public void callbackRechargeFailed(Map<String, Object> params) throws SLException;
	
	/**
	 * 用户交易明细 -- 针对主账号
	 * @param param
	 * 		<tt>custId： String:用户ID</tt><br>
	 *		<tt>pageNumber： String:页号</tt><br>
  	 *		<tt>pageSize： String: 页面大小</tt><br>
  	 *		<tt>pageSize： String: 页面大小</tt><br>
	 *		<tt>tradeType： String:交易类型</tt><br>
	 *		<tt>beginDate： String:开始时间</tt><br>
	 *		<tt>endDate： String:结束时间</tt><br>
	 * @return
	 */
	@Rules(rules = { 
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空!"),
			@Rule(name = "pageNumber", required = true, requiredMessage = "页号不能为空!", digist = true),
			@Rule(name = "pageNumber", required = true, requiredMessage = "页面大小不能为空!", digist = true)
			
	})
	Page<AccountFlowInfoEntity> findAccountFlowInfoPagable(Map<String, Object> param);
	
	/**
	 * 记录账户流水
	 *
	 * @author  wangjf
	 * @date    2015年5月21日 上午10:54:09
	 * @param sourceMainAccount
	 * @param sourceSubAccount
	 * @param destMainAccount
	 * @param destSubAccount
	 * @param accountType
	 * 		"1":主账户——>主账户 
	 		"2":主账户——>分账户
	 		"3":分账户——>主账户
	 		"4":分账户——>分账户
	 * @param tradeType
	 * @param reqeustNo
	 * @param bankrollFlowDirection
	 * @param tradeAmount
	 * @param subjectType
	 * @param flowBusiRelateType
	 * @param flowRelatePrimary
	 * @param flowType
	 * @return
	 */
	public AccountFlowInfoEntity saveAccountFlow(AccountInfoEntity sourceMainAccount, SubAccountInfoEntity sourceSubAccount, 
			AccountInfoEntity destMainAccount, SubAccountInfoEntity destSubAccount,
			String accountType, String tradeType, String reqeustNo, String bankrollFlowDirection, BigDecimal tradeAmount, String subjectType,
			String flowBusiRelateType, String flowRelatePrimary, String flowType);

	/**
	 * 记录账户流水 -- 使用红包加息劵体验金
	 *
	 * @author  wangjf
	 * @date    2015年5月21日 上午10:54:09
	 * @param sourceMainAccount
	 * @param sourceSubAccount
	 * @param destMainAccount
	 * @param destSubAccount
	 * @param accountType
	 * 		"1":主账户——>主账户
	"2":主账户——>分账户
	"3":分账户——>主账户
	"4":分账户——>分账户
	 * @param tradeType
	 * @param reqeustNo
	 * @param bankrollFlowDirection
	 * @param tradeAmount
	 * @param subjectType
	 * @param flowBusiRelateType
	 * @param flowRelatePrimary
	 * @param flowType
	 * @return
	 */
	AccountFlowInfoEntity saveAccountFlowExt(AccountInfoEntity sourceMainAccount, SubAccountInfoEntity sourceSubAccount,
			AccountInfoEntity destMainAccount, SubAccountInfoEntity destSubAccount,
			String accountType, String tradeType, String reqeustNo, String bankrollFlowDirection, BigDecimal tradeAmount, String subjectType,
			String flowBusiRelateType, String flowRelatePrimary, String flowType);

	/**
	 * 实名认证之后扣除手续费
	 * 
	 * @author  zhangzs
	 * @date    2015年5月22日 上午17:54:09
	 * @param resultVo 第三方返回信息
	 * @param custInfo 客户信息
	 * @param thirdPartyPayParams 请求第三方参数
	 * @throws SLException 
	 */
	void insertRealNameAuthExpense(ResultVo resultVo, CustInfoEntity custInfo,Map<String, Object> thirdPartyPayParams) throws SLException;

	/**
	 *  根据交易编号查询账户流水信息,事务是只读的;
	 * 
	 * @param tradeNo
	 * @return
	 */
	AccountFlowInfoEntity findByTradeNoIsRead(String tradeNo);
	
	/**
	 * 公司资金流水列表
	 *
	 * @author  wangjf
	 * @date    2015年4月25日 上午11:32:03
	 * @param param
	        <tt>start：int:分页起始页</tt><br>
	  		<tt>length：int:每页长度</tt><br>
	  		<tt>nickName：String:用户昵称(可选)</tt><br>
	  		<tt>custName：String:真实姓名(可选)</tt><br>
	  		<tt>credentialsCode：String:证件号码(可选)</tt><br>
	  		<tt>opearteDateBegin：String:操作开始时间(可选)</tt><br>
	  		<tt>opearteDateEnd：String:操作开始时间(可选)</tt><br>
	  		<tt>tradeType：String:交易类型(可选)</tt><br>
	  		<tt>bankrollFlowDirection：String:资金流向(可选)</tt><br>
	  		<tt>companyType：String:公司类型(01：居间人账户， 02：收益账户，03:风险金账户)</tt><br>
	 * @return
	  		iTotalDisplayRecords: 总条数
	 		data:List<Map<String, object>>
	 		Map<String, object>:
	 	    <tt>id：String:用户ID</tt><br>
	  		<tt>nickName：String:昵称</tt><br>
	 		<tt>tradeType：String:交易类型</tt><br>
	 		<tt>tradeAmount：BigDecimal:操作金额</tt><br>
	 		<tt>bankrollFlowDirection：String:资金流向</tt><br>
	  		<tt>targetNickName：String:对方昵称</tt><br>
	  		<tt>targetCustName：String:对方姓名</tt><br>
	  		<tt>targetCredentialsCode：String:对方证件号码</tt><br>
	  		<tt>memo：String:描述</tt><br>
	  		<tt>operateDate：Date:操作时间</tt><br>
	 */
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数"),
			@Rule(name = "companyType", required = true, requiredMessage = "公司类型不能为空")
	})
	public Map<String, Object> findCompanyAccount(Map<String, Object> param);
	
	/**
	 * 汇总公司账户
	 *
	 * @author  wangjf
	 * @date    2015年7月13日 下午3:25:44
	 * @param map
	        <tt>start：int:分页起始页</tt><br>
	  		<tt>length：int:每页长度</tt><br>
	  		<tt>nickName：String:用户昵称(可选)</tt><br>
	  		<tt>custName：String:真实姓名(可选)</tt><br>
	  		<tt>credentialsCode：String:证件号码(可选)</tt><br>
	  		<tt>opearteDateBegin：String:操作开始时间(可选)</tt><br>
	  		<tt>opearteDateEnd：String:操作开始时间(可选)</tt><br>
	  		<tt>tradeType：String:交易类型(可选)</tt><br>
	  		<tt>bankrollFlowDirection：String:资金流向(可选)</tt><br>
	  		<tt>companyType：String:公司类型(01：居间人账户， 02：收益账户，03:风险金账户)</tt><br>	
	 * @return
	 * 		<tt>totalTradeAmount： BigDecimal:汇总交易金额</tt><br>
	 */
	@Rules(rules = { 
			@Rule(name = "companyType", required = true, requiredMessage = "公司类型不能为空")
	})
	public Map<String, Object> findCompanyAccountSum(Map<String, Object> map);
	
	/**
	 * 查询
	 * @author zhangt
	 * @param param
	 * @return
	 */
	public ResultVo findProjectAuditInfoPage(Map<String, Object> param);
	
	/**
	 * 保存企业借款风险金充值记录
	 * @author zhangt
	 * @param param
	 * @return
	 */
	@Rules(rules = {
			@Rule(name = "custId", required = true, requiredMessage = "客户主键不能为空!"),
			@Rule(name = "tradeType", required = true, requiredMessage = "交易类型不能为空!"),
			@Rule(name = "memo", required = true, requiredMessage = "备注不能为空!"),
			@Rule(name = "tradeAmount", required = true, requiredMessage = "充值金额不能为空!", number = true, numberMessage = "充值金额只能为数字") })
	public ResultVo insertRiskRechargeInfo(Map<String, Object> param) throws SLException;
	
	/**
	 * 审核
	 * @author zhangt
	 * @param param
	 * @return
	 */
	public ResultVo auditRiskRechargeInfo(Map<String, Object> param) throws SLException;
	
	/**
	 * 查看企业借款风险金
	 * @author zhangt
	 * @param param
	 * @return
	 */
	public ResultVo findTradeFlowInfoById(Map<String, Object> param);
	
	/**
	 * 风险金流水
	 * @param param
	 * @return
	 */
	public ResultVo findProjectAccountFlowPage(Map<String, Object> param);
	
	/**
	 * 我的资金流水
	 * 
	 * @date 2016-03-04
	 * @author zhiwen_feng
	 * @param params
     *      <tt>wealthId:String:计划主键</tt><br>
     *      <tt>custId  :String:客户ID</tt><br>
	 * @return ResultVo
     *      <tt>createDate           :String:交易日期</tt><br>
     *      <tt>tradeType            :String:交易类型</tt><br>
     *      <tt>tradeAmount          :String:交易金额</tt><br>
     *      <tt>accountTotalAmount   :String:结余</tt><br>
     *      <tt>bankrollFlowDirection:String:方向</tt><br>
     *      <tt>memo                 :String:备注</tt><br>
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "wealthId", required = true, requiredMessage = "计划主键不能为空!"),
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空!")
	})
	public ResultVo queryMyWealthFlow(Map<String, Object> params)throws SLException;
	
	/**
	 * 查询充值记录
	 * 
	 * @author zhangt
	 * @date 2016年12月2日下午1:48:47
	 * @param param
	 * @return
	 */
	public ResultVo findRechargeList(Map<String, Object> param);
	
	/**
	 * 通过交易类型和关联主键查询
	 *
	 * @author  wangjf
	 * @date    2017年7月11日 下午8:04:48
	 * @param tradeType
	 * @param relatePrimary
	 * @return
	 */
	int queryByTradeTypeAndRelatePrimary(String tradeType, String relatePrimary);
}
