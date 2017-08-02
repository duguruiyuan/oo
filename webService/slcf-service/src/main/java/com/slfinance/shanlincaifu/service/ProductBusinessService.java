/** 
 * @(#)SmsUtil.java 1.0.0 2015年04月23日 下午13:56:25  
 *  
 * Copyright © 2014 善林金融.  All rights reserved.  
 */
package com.slfinance.shanlincaifu.service;

import java.util.List;
import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;

/**
 * 
 *  产品业务服务接口
 * 
 * @author caoyi
 * @version $Revision:1.0.0, $Date: 2015年04月23日 下午13:56:25 $
 */
public interface ProductBusinessService {
	
	/**
	 * 获取活期宝今日明细
	 * 
	 * @author caoyi
	 * @date 2015年04月23日 下午13:56:25
	 * @return Map <br>
	 *         <tt>key:custAmount, title:投资人当前金额, type:{@link String} </tt><br>
	 *         <tt>key:canOpenAmount, title:可开放价值, type:{@link String} </tt><br>
	 *         <tt>key:usableValue, title:开放中价值, type:{@link String} </tt><br>
	 *         <tt>key:companyIncomeAmount, title:公司收益账户持有价值, type:{@link String} </tt><br>
	 *         <tt>key:companyLoanAmount, title:最大债权人账户持有价值, type:{@link String} </tt><br>
	 *         <tt>key:todayRepaymentAmount, title:今日还款金额, type:{@link String} </tt><br>
	 *         <tt>key:untreatedRepaymentAmount, title:未处理还款金额, type:{@link String} </tt><br>
	 *         <tt>key:untreatedAllotAmount, title:分配未处理债权价值, type:{@link String} </tt><br>
	 *         <tt>key:canUseAllotAmount, title:分配可使用债权价值, type:{@link String} </tt><br>
	 */
	public Map<String, Object> findBaoCurrentDetailInfo() throws SLException;
	
	/**
	 * 获取活期宝当前价值分配
	 * 
	 * @author caoyi
	 * @date 2015年04月28日 下午16:08:16
	 * @return Map <br>
	 *         <tt>key:openValue, title:预计开放价值, type:{@link String} </tt><br>
	 *         <tt>key:alreadyPreValue, title:已预留价值, type:{@link String} </tt><br>
	 *         <tt>key:expectPreValue, title:预计预留价值, type:{@link String} </tt><br>
	 *         <tt>key:unopenValue, title:未开放价值, type:{@link String} </tt><br>
	 *         <tt>key:canOpenValue, title:可开放价值, type:{@link String} </tt><br>
	 */
	public Map<String, Object> findBaoCurrentVauleSum() throws SLException;
	
	/**
	 * 获取活期宝分配规则
	 * 
	 * @author caoyi
	 * @date 2015年04月28日 下午16:49:45
	 * @return Map <br>
	 *         <tt>key:allotScale, title:活期宝分配比例, type:{@link String} </tt><br>
	 *         <tt>key:expectPreValue, title:活期宝预计预留价值, type:{@link String} </tt><br>
	 */
	public Map<String, Object> findBaoCurrentVauleSet() throws SLException;
	
	/**
	 * 重定义活期宝分配规则
	 * 
	 * @author caoyi
	 * @date 2015年04月28日 下午18:23:24
	 * @param Map
	 * <br>
	 *            <tt>key:allotScale, title:活期宝分配比例, type:{@link String} </tt><br>
	 *            <tt>key:expectPreValue, title:活期宝预计预留价值, type:{@link String} </tt><br>
	 * @return ResultVo
	 */
	@Rules(rules = { 
			@Rule(name = "allotScale", required = true, requiredMessage = "活期宝分配比例不能为空",number=true,numberMessage="活期宝分配比例必须为数字"), 
			@Rule(name = "expectPreValue", required = true, requiredMessage = "活期宝预计预留价值不能为空",number=true,numberMessage="活期宝预计预留价值必须为数字")
	})
	public ResultVo updateBaoSetVaule(Map<String, Object> param) throws SLException;
	
	/**
	 * 手工修改活期宝当前价值分配
	 * 
	 * @author caoyi
	 * @date 2015年04月29日 下午16:19:24
	 * @param Map
	 * <br>
	 *            <tt>key:canOpenValue, title:可开放价值汇总, type:{@link String} </tt><br>
	 *            <tt>key:openValue, title:活期宝预计开放价值, type:{@link String} </tt><br>
	 *            <tt>key:alreadyPreValue, title:活期宝已预留价值, type:{@link String} </tt><br>
	 *            <tt>key:unopenValue, title:未开放价值, type:{@link String} </tt><br>
	 * @return ResultVo
	 */
	@Rules(rules = { 
//			@Rule(name = "canOpenValue", required = true, requiredMessage = "可开放价值汇总不能为空",number=true,numberMessage="可开放价值汇总必须为数字"), 
//			@Rule(name = "openValue", required = true, requiredMessage = "活期宝预计开放价值不能为空",number=true,numberMessage="活期宝预计开放价值必须为数字"), 
			@Rule(name = "alreadyPreValue", required = true, requiredMessage = "活期宝已预留价值不能为空",number=true,numberMessage="活期宝已预留价值必须为数字"), 
			@Rule(name = "unopenValue", required = true, requiredMessage = "活期宝未开放价值不能为空",number=true,numberMessage="活期宝未开放价值必须为数字")
	})
	public ResultVo updateBaoCurrentVaule(Map<String, Object> param) throws SLException;
	
	/**
	 * 获取债权还款预算
	 * 
	 * @author caoyi
	 * @date 2015年04月29日 下午17:52:36
	 * @param Map <br>
	 *         <tt>key:productType, title:产品类型, type:{@link String} </tt><br>
	 *         <tt>key:startDate, title:开始日期, type:{@link String} </tt><br>
	 *         <tt>key:endDate, title:结束日期, type:{@link String} </tt><br>
	 * @return Map <br>
	 *         <tt>key:preRepayAmount, title:预计还款金额, type:{@link String} </tt><br>
	 *         <tt>key:untreatedRepaymentAmount, title:未处理还款金额, type:{@link String} </tt><br>
	 *         <tt>key:alreadyPreValue, title:已预留债权价值, type:{@link String} </tt><br>
	 */
	@Rules(rules = { 
			@Rule(name = "productType", required = true, requiredMessage = "产品类型不能为空"), 
			@Rule(name = "startDate", required = true, requiredMessage = "开始日期不能为空"), 
			@Rule(name = "endDate", required = true, requiredMessage = "结束日期不能为空")
	})
	public Map<String, Object> loanRepaymentForecast(Map<String, Object> param) throws SLException;
	
	/**
	 * 查询产品预计还款明细
	 * 
	 * @author caoyi
	 * @date 2015年04月29日 下午20:16:36
	 * @param Map <br>
	 *         <tt>key:productType, title:产品类型, type:{@link String} </tt><br>
	 *         <tt>key:startDate, title:开始日期, type:{@link String} </tt><br>
	 *         <tt>key:endDate, title:结束日期, type:{@link String} </tt><br>
	 *         <tt>key:start, title:开始条数, type:{@link String} </tt><br>
	 *         <tt>key:length, title:每页长度, type:{@link String} </tt><br>
	 * @return Map <br>
	 * 		   iTotalDisplayRecords: 总条数
	 * 		   data:List<Map<String, object>>
	 * 		   Map<String, object>:
	 *         <tt>key:repaymentDate, title:还款日期, type:{@link String} </tt><br>
	 *         <tt>key:repaymentAmount, title:预计还款金额, type:{@link String} </tt><br>
	 */
	@Rules(rules = { 
			@Rule(name = "productType", required = true, requiredMessage = "产品类型不能为空"), 
			@Rule(name = "startDate", required = true, requiredMessage = "开始日期不能为空"), 
			@Rule(name = "endDate", required = true, requiredMessage = "结束日期不能为空"),
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数") 
	})
	public Map<String, Object> findLoanRepaymentList(Map<String, Object> param) throws SLException;
	
	
	/**
	 * 获取债权价值预算
	 * 
	 * @author caoyi
	 * @date 2015年04月30日 上午10:43:36
	 * @param Map <br>
	 *         <tt>key:productType, title:产品类型, type:{@link String} </tt><br>
	 *         <tt>key:queryDate, title:查询日期, type:{@link String} </tt><br>
	 * @return Map <br>
	 *         <tt>key:eranAccountValue, title:公司收益账户持有债权价值, type:{@link String} </tt><br>
	 *         <tt>key:centerAccountValue, title:最大债权人持有债权价值, type:{@link String} </tt><br>
	 *         <tt>key:sum, title:汇总, type:{@link String} </tt><br>
	 */
	@Rules(rules = { 
			@Rule(name = "productType", required = true, requiredMessage = "产品类型不能为空"), 
			@Rule(name = "queryDate", required = true, requiredMessage = "查询日期不能为空")
	})
	public Map<String, Object> loanValueForecast(Map<String, Object> param) throws SLException;
	
	/**
	 * 发标(手动或定时) 
	 * 活期宝、定期宝
	 * 
	 * @author caoyi
	 * @date 2015年05月01日下午13:44:36
	 */
	public void releaseBid() throws SLException;
	
	/**
	 * 可开放价值计算(手动或定时)
	 * 活期宝、定期宝
	 * @author caoyi
	 * @date 2015年05月01日下午15:45:18
	 */
	public void computeOpenValue() throws SLException;
	
	/**
	 * 获取累计成交统计
	 * 
	 * @author caoyi
	 * @date 2015年05月5日 下午16:33:15
	 * @return Map <br>
	 *         <tt>key:custCount, title:累计注册用户, type:{@link String} </tt><br>
	 *         <tt>key:tradeAmount, title:累计成交金额, type:{@link String} </tt><br>
	 *         <tt>key:tradeCount, title:累计成交记录, type:{@link String} </tt><br>
	 *         <tt>key:incomeAmount, title:累计为客户赚取, type:{@link String} </tt><br>
	 */
	public Map<String, Object> findTotalTradetInfo() throws SLException;
	
	/**
	 * 收益计算器
	 * 
	 * @author caoyi
	 * @date 2015年07月14日 下午17:44:25 
	 * @param param
	 *       <tt>investMethod ：String:投资方式</tt><br>
	 *       <tt>investAmount ：String:投资金额</tt><br>
	 *       <tt>days ：String:投资天数</tt><br>
	 * <br>
	 * @return Map<String, object>: 
	 * 		  <tt>incomeAmount ： String:累计收益</tt><br>
	 *        <tt>totalAmount： String:持有份额</tt><br>
	 */
	public Map<String, Object> incomeCalculator(Map<String, Object> param) throws SLException;
	
	/**
	 * 批量审核赎回活期宝
	 *
	 * @author  wangjf
	 * @date    2015年8月3日 下午6:22:44
	 * @param params
	 * 		<tt>auditCustId： String:审核用户ID</tt><br>
	 		<tt>auditStatus： String:审核状态</tt><br>	
	 		<tt>auditMemo： String:审核备注</tt><br>
	 *		<tt>auditList:link java.util.List  赎回审核列表 	 
	 *            <tt>atoneId： String:赎回ID</tt><br> 
	 *	    </tt>
	 * @return
	 *         <tt>
	 *         <tt>result.success: Boolean: 是否成功 </tt>
	 *         <tt>result.data: Map: 处理结果 
	 *         		<tt>success: String: 提现成功个数</tt>
	 *              <tt>failed: String: 提现失败个数  </tt>
	 *         </tt>
	 *         </tt>
	 * @throws SLException
	 */
	public ResultVo batchAuditWithdrawBaoNormal(Map<String, Object> params) throws SLException;
	
	
	/**
	 * 获取定期宝今日明细
	 * 
	 * @author caoyi
	 * @date 2015年08月15日 上午12:08:25
	 * @return Map <br>
	 *         <tt>key: custAmount, title:投资人当前金额, type:{@link String} </tt><br>
	 *         <tt>key: canOpenAmount, title:可开放价值, type:{@link String} </tt><br>
	 *         <tt>key: usableValue, title:开放中价值, type:{@link String} </tt><br>
	 *         <tt>key: companyIncomeAmount, title:公司收益账户持有价值, type:{@link String} </tt><br>
	 *         <tt>key: companyLoanAmount, title:最大债权人账户持有价值, type:{@link String} </tt><br>
	 *         <tt>key: todayRepaymentAmount, title:今日还款金额, type:{@link String} </tt><br>
	 *         <tt>key: untreatedRepaymentAmount, title:未处理还款金额, type:{@link String} </tt><br>
	 *         <tt>key: untreatedAllotAmount, title:分配未处理债权价值, type:{@link String} </tt><br>
	 *         <tt>key: canUseAllotAmount, title:今日分配未处理债权价值, type:{@link String} </tt><br>
	 *         <tt>key: atoneAmount, title:赎回中的价值, type:{@link String} </tt><br>
	 */
	public Map<String, Object> findTermCurrentDetailInfo() throws SLException;
	
	/**
	 * 获取定期宝当前价值分配
	 * 
	 * @author caoyi
	 * @date 2015年08月17日 下午15:06:16
	 * @return List Map <br>
	 *         <tt>key: name, title:名称, type:{@link String} </tt><br>
	 *         <tt>key: type, title:类型, type:{@link String} </tt><br>
	 *         <tt>key: value, title:价值, type:{@link String} </tt><br>
	 *         <tt>key: code, title:编号, type:{@link String} </tt><br>
	 */
	public List<Map<String, Object>> findTermCurrentVauleSum() throws SLException;
	
	/**
	 * 获取定期宝分配规则
	 * 
	 * @author caoyi
	 * @date 2015年08月17日 下午17:23:34
	 * @return List Map <br>
	 *         <tt>key: name, title:名称, type:{@link String} </tt><br>
	 *         <tt>key: type, title:类型, type:{@link String} </tt><br>
	 *         <tt>key: value, title:值, type:{@link String} </tt><br>
	 *         <tt>key: code, title:编号, type:{@link String} </tt><br>
	 */
	public List<Map<String, Object>> findTermCurrentVauleSet() throws SLException;
	
	
	/**
	 * 重定义定期宝分配规则
	 * 
	 * @author caoyi
	 * @date 2015年08月18日 上午10:43:34
	 * @param map key:data
	 * List<Map> <br>
	 *         <tt>key: name, title:名称, type:{@link String} </tt><br>
	 *         <tt>key: type, title:类型, type:{@link String} </tt><br>
	 *         <tt>key: value, title:值, type:{@link String} </tt><br>
	 *         <tt>key: code, title:编号, type:{@link String} </tt><br>
	 * @return ResultVo
	 */
	public ResultVo updateTermSetVaule(Map<String, Object> map) throws SLException;
	
	/**
	 * 手工修改定期宝当前价值分配
	 * 
	 * @author caoyi
	 * @date 2015年08月18日 下午17:14:28
	 * @param map key:data
	 * List<Map> <br>
	 *         <tt>key: name, title:名称, type:{@link String} </tt><br>
	 *         <tt>key: type, title:类型, type:{@link String} </tt><br>
	 *         <tt>key: value, title:值, type:{@link String} </tt><br>
	 *         <tt>key: code, title:编号, type:{@link String} </tt><br>
	 * @return ResultVo
	 */
	public ResultVo updateTermCurrentVaule(Map<String, Object> map) throws SLException;
	
	/**
	 * 可开放价值计算(手动或定时)
	 * 活期宝
	 * @author caoyi
	 * @date 2015年08月20日上午11:26:12
	 */
	public void computeBaoOpenValue() throws SLException;
	
	/**
	 * 可开放价值计算(手动或定时)
	 * 定期宝
	 * @author caoyi
	 * @date 2015年08月20日上午11:26:12
	 */
	public void computeTermOpenValue() throws SLException;
	
	/**
	 * 通过产品名称查询机构数和债权数
	 *
	 * @author  wangjf
	 * @date    2015年12月14日 下午3:44:09
	 * @param productName
	 * @return
	 * 		partakeOrganizs: BigDecimal :机构数
	 *      partakeCrerigs: BigDecimal:债权数
	 */
	public Map<String, Object> findPartakeForDisplay(String productName);
}

