/** 
 * @(#)AllotRepositoryCustom.java 1.0.0 2015年4月23日 下午3:56:20  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository.custom;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.slfinance.shanlincaifu.entity.AllotDetailInfoEntity;

/**   
 * 自定义分配信息接口
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年4月23日 下午3:56:20 $ 
 */
public interface AllotRepositoryCustom {

	
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
	 * @return 
	 * 		
	  		<tt>id：String:分配信息ID</tt><br>
	  		<tt>typeName：String:分配对象名称</tt><br>
	  		<tt>allotCode：String:分配编号/交易编号</tt><br>
	  		<tt>allotDate：Date:分配日期</tt><br>
	  		<tt>allotAmount：Date:分配总价值</tt><br>
			<tt>allotStatus：String:分配状态</tt><br>
			<tt>useDate： Date:使用日期</tt><br>
	 */
	public Page<Map<String, Object>> findAllot(Map<String, Object> param);
	 
	/**
	 * 查询可以分配的债权
	 *
	 * @author  wangjf
	 * @date    2015年4月24日 上午9:45:44
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
	public Page<Map<String, Object>> findCanAllotLoan(Map<String, Object> param);
	
	/**
	 * 查询可以分配的债权价值、金额、笔数
	 *
	 * @author  wangjf
	 * @date    2015年4月24日 下午2:25:50
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
	 */
	public Map<String, Object> findCanAllotLoanCount(Map<String, Object> param);
	
	/**
	 * 根据分配信息主键查询债权信息
	 *
	 * @author  wangjf
	 * @date    2015年4月24日 下午3:40:39
	 * @param 
	 * 		<tt>start：int:分页起始页</tt><br>
	  		<tt>length：int:每页长度</tt><br>
	  		<tt>id：String:分配信息ID</tt><br>
	 * @return
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
	public Page<Map<String, Object>> findAllotLoanById(Map<String, Object> param);
	
	/**
	 * 批量插入分配详情
	 *
	 * @author  wangjf
	 * @date    2015年4月27日 下午5:14:09
	 * @param list
	 */
	public void batchInsertAllotDetail(List<AllotDetailInfoEntity> list);
	
	/**
	 * 根据分配信息主键查询债权当前价值
	 *
	 * @author  wangjf
	 * @date    2015年5月18日 下午6:02:06
	 * @param param
	 * @return
	 */
	public BigDecimal findAllotLoanCount(Map<String, Object> param);
	
	/**
	 * 根据债权ID列表查询债权PV
	 *
	 * @author  wangjf
	 * @date    2015年5月27日 下午5:30:49
	 * @param loanList
	 * @return
	 */
	public List<Map<String, Object>> findByLoanIdList(List<Map<String, Object>> loanList);
	
	/**
	 * 统计根据债权ID列表查询债权PV
	 *
	 * @author  wangjf
	 * @date    2015年5月27日 下午5:49:18
	 * @param loanList
	 * @return
	 */
	public BigDecimal sumByLoanIdList(List<Map<String, Object>> loanList);
	
	/**
	 * 判断债权是否已经分配
	 *
	 * @author  wangjf
	 * @date    2015年5月29日 下午2:45:02
	 * @param loanList
	 * @return
	 */
	public BigDecimal judgeAllotedLoanIdList(List<Map<String, Object>> loanList);
}
