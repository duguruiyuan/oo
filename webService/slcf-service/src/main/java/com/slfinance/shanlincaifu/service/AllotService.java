/** 
 * @(#)AllotService.java 1.0.0 2015年4月23日 下午7:53:18  
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
 * 分配信息服务接口
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年4月23日 下午7:53:18 $ 
 */
public interface AllotService {
	
	/**
	 * 根据条件查询所有分配信息
	 *
	 * @author  wangjf
	 * @date    2015年4月23日 下午4:00:16
	 * @param 查询条件<br>
	 		<tt>start：int:分页起始页</tt><br>
	  		<tt>length：int:每页长度</tt><br>
			<tt>allotCode：String:分配编号/交易编号(可为空)</tt><br>
			<tt>allotType：String:分配类型(可为空)</tt><br>
			<tt>allotStatus：String:分配状态(可为空)</tt><br>
			<tt>useDateBegin： String:使用日期开始(可为空)</tt><br>
			<tt>useDateEnd： String:使用日期结束(可为空)</tt><br>
	 * @return List集合
	 		iTotalDisplayRecords: 总条数
 			data:List<Map<String, object>>
 			Map<String, object>:
	  		<tt>id：String:分配信息ID</tt><br>
	  		<tt>typeName：String:分配对象名称</tt><br>
	  		<tt>allotCode：String:分配编号/交易编号</tt><br>
	  		<tt>allotDate：Date:分配日期</tt><br>
	  		<tt>allotAmount：Date:分配总价值</tt><br>
			<tt>allotStatus：String:分配状态</tt><br>
			<tt>useDate： Date:使用日期</tt><br>
	 */
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数") 
	})
	public Map<String, Object> findLoanAllotList(Map<String, Object> param);
	
	/**
	 * 取消分配
	 *
	 * @author  wangjf
	 * @date    2015年4月23日 下午7:54:47
	 * @param 条件<br>
	 		<tt>id：String:分配主键</tt><br>
	 		<tt>updateUser：String:更新用户</tt><br>
	 * @return
	 */
	@Rules(rules = { 
			@Rule(name = "id", required = true, requiredMessage = "分配主键不能为空!"),
			@Rule(name = "updateUser", required = true, requiredMessage = "更新客户不能为空!")
	})
	public ResultVo cancelLoanAllot(Map<String, Object> param) throws SLException;
	
	/**
	 * 查询可分配债权
	 *
	 * @author  wangjf
	 * @date    2015年4月23日 下午7:59:07
	 * @param param
	        <tt>start：int:分页起始页</tt><br>
	  		<tt>length：int:每页长度</tt><br>
			<tt>debtSource： String:来源机构(可为空)</tt><br>
			<tt>productCode： String:借款产品(可为空)</tt><br>
			<tt>yearRateBegin： String:年化利率开始(可为空)</tt><br>
			<tt>yearRateEnd： String:年化利率结束(可为空)</tt><br>
			<tt>loanTerm： String:借款期限(可为空)</tt><br>
			<tt>creditRightStatus： String:借款状态(可为空)</tt><br>
			<tt>repaymentDay： String:还款日(可为空)</tt><br>
			<tt>importDateBegin： String:导入日期开始(可为空)</tt><br>
			<tt>importDateEnd： String:导入日期结束(可为空)</tt><br>
	 * @return
	 		<tt>totalPv： BigDecimal:价值汇总</tt><br>
	 		<tt>totalLoanAmount： BigDecimal:债权金额汇总</tt><br>
	 		<tt>totalLoans： Integer:总笔数</tt><br>
	 		<tt>iTotalDisplayRecords: 总条数</tt><br>
	 		<tt>	
	 			data:List<Map<String, object>>
	 			Map<String, object>:
	 			<tt>id： String:债权ID</tt><br>
		 		<tt>loanCode： String:债权编号</tt><br>
		 		<tt>debtSource： String:来源机构</tt><br>
				<tt>productCode： String:借款产品</tt><br>
				<tt>custName： String:借款人</tt><br>
				<tt>loanAmount： BigDecimal:借款金额（元）</tt><br>
				<tt>currentValue： BigDecimal:当前价值</tt><br>
				<tt>loanTerm： String:借款期限（月）</tt><br>
				<tt>repaymentDay： String:还款日</tt><br>
				<tt>grantDate： Date:放款日期</tt><br>
				<tt>importDate： Date:导入日期</tt><br>
				<tt>creditRightStatus： String:债权状态</tt><br>
				<tt>yearRate： BigDecimal:年化利率</tt><br>
	 		</tt><br>
	 */
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数") 
	})
	public Map<String, Object> findCanAllotLoanList(Map<String, Object> param);

	/**
	 * 债权分配
	 *
	 * @author  wangjf
	 * @date    2015年4月23日 下午8:00:21
	 * @param param
	  		<tt>productId： String:产品类型ID</tt><br>
	 		<tt>allotAmount： String:分配金额</tt><br>
	  		<tt>createUser： String:创建用户</tt><br>
	  		<tt>loanList： List<Map<String, Object>:分配的债权列表
	  				Map<String, Object>:债权以ID和价值提供
	  				<tt>loanId： String:债权ID</tt><br>
		 			<tt>currentValue： BigDecimal:债权价值</tt><br>
	  		</tt><br>
	  		<tt>useDate： String:使用日期</tt><br>
	 * @return
	 */
	@Rules(rules = { 
			@Rule(name = "productId", required = true, requiredMessage = "产品类型主键不能为空!"),
			@Rule(name = "createUser", required = true, requiredMessage = "创建客户ID不能为空!"),
			@Rule(name = "allotAmount", required = true, requiredMessage = "分配金额不能为空!", number = true, numberMessage = "分配金额只能为数字"),	
			@Rule(name = "useDate", required = true, requiredMessage = "使用日期不能为空!", dateFormat = "yyyy-MM-dd", dateFormatMessage= "使用日期格式不正确")
	})
	public ResultVo allotLoan(Map<String, Object> param) throws SLException;
	
	/**
	 * 查询分配明细
	 *
	 * @author  wangjf
	 * @date    2015年4月23日 下午8:02:12
	 * @param 
	 * 		<tt>start：int:分页起始页</tt><br>
	  		<tt>length：int:每页长度</tt><br>
	  		<tt>id：String:分配信息ID</tt><br>
	 * @return	
 		    iTotalDisplayRecords: 总条数
 			data:List<Map<String, object>>
 			Map<String, object>:
 			<tt>id： String:债权ID</tt><br>
	 		<tt>loanCode： String:债权编号</tt><br>
	 		<tt>debtSource： String:来源机构</tt><br>
			<tt>productCode： String:借款产品</tt><br>
			<tt>custName： String:借款人</tt><br>
			<tt>loanAmount： BigDecimal:借款金额（元）</tt><br>
			<tt>currentValue： BigDecimal:当前价值</tt><br>
			<tt>loanTerm： String:借款期限（月）</tt><br>
			<tt>repaymentDay： String:还款日</tt><br>
			<tt>grantDate： Date:放款日期</tt><br>
			<tt>importDate： Date:导入日期</tt><br>
			<tt>creditRightStatus： String:债权状态</tt><br>
			<tt>yearRate： BigDecimal:年化利率</tt><br>
	 */
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数"),
			@Rule(name = "id", required = true, requiredMessage = "分配主键不能为空!")
	})
	public Map<String, Object> findLoanAllotDetailList(Map<String, Object> param);
	
	/**
	 * 将债权分配给居间人
	 *
	 * @author  wangjf
	 * @date    2016年2月25日 下午3:41:39
	 * @param param
	 * @return
	 */
	public ResultVo autoAllotWealth(Map<String, Object> param);
}
