package com.slfinance.shanlincaifu.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.LoanCustInfoEntity;
import com.slfinance.shanlincaifu.entity.LoanInfoEntity;
import com.slfinance.shanlincaifu.entity.RepaymentPlanInfoEntity;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;

/**
 * @Desc 债权业务服务类
 * @author zhoud
 * **/
public interface LoanInfoService {
	
	/**
	 * @desc 预算债权PV值
	 * @author zhoudl
	 * **/
	public void execLoanPv() throws SLException;

	/**
	 * @desc 根据参数条件,查询相应的债权信息
	 * @param debtSourceCode 债权来源编号
	 * @param repaymentDay   还款日
	 * @param importDate     债权导入日期
	 * @param start          起始记录参数
	 * @param length         每页记录大小
	 * @author zhoudl
	 * @date 2015年04月15日 下午午13:41:36
	 * **/
	public Map<String,Object> queryConditionLoan(Map<String,Object> conditionMap) throws SLException;
	
	/**
	 * @desc 根据主键ID,查询还款明细
	 * @param loanId 债权主键ID
	 * @return java.util.Map
	 * @author zhoudl
	 * **/
	public Map<String,Object> queryLoanRepaymentDetail(Map<String,Object> conditionMap) throws SLException;
	
	/**
	 * @desc 获取所有借款客户信息
	 * @return java.util.Map
	 * @author zhoudl
	 * **/
	public Map<String,LoanCustInfoEntity> findAllLoanCust() throws SLException; 
	
	/**
	 * @desc 批量导入债权信息
	 * @param LoanCustInfoEntity 借款客户信息对象
	 * @return ResultVo
	 * @author zhoudl
	 * **/
	public ResultVo importLoanInfo(List<LoanCustInfoEntity> list) throws SLException;
	
	/**
	 * @desc 批量导入还款计划
	 * @param RepaymentPlanInfoEntity 还款计划实体对象
	 * @return ResultVo
	 * **/
	public ResultVo importRepaymentPlan(List<RepaymentPlanInfoEntity> planEntityList) throws SLException;
	
	/**
	 * 我的投资-债权列表
	 *
	 * @author  wangjf
	 * @date    2015年4月27日 下午6:24:42
	 * @param 
	 		<tt>start：int:分页起始页</tt><br>
	 		<tt>length：int:每页长度</tt><br>
	 		<tt>productType： String:产品类型</tt><br>
	 		<tt>custId：String:客户编号</tt><br>
	 		<tt>debtSource： String:来源机构(可为空)</tt><br>
			<tt>productCode： String:借款产品(可为空)</tt><br>
			<tt>yearRateBegin： String:年化利率开始(可为空)</tt><br>
			<tt>yearRateEnd： String:年化利率结束(可为空)</tt><br>
			<tt>loanTerm： String:借款期限(可为空)</tt><br>
			<tt>creditRightStatus： String:借款状态(可为空)</tt><br>
			<tt>repaymentDay： String:还款日(可为空)</tt><br>
			<tt>importDateBegin： String:导入日期开始(可为空)</tt><br>
			<tt>importDateEnd： String:导入日期结束(可为空)</tt><br>
			<tt>allotDateBegin： String:分配日期开始(可为空)</tt><br>
			<tt>allotDateEnd： String:分配日期结束(可为空)</tt><br>
	 * @return
	 			data:List<Map<String, object>>
	 			Map<String, object>:
	 			<tt>id： String:债权ID</tt><br>
		 		<tt>loanCode： String:债权编号</tt><br>
		 		<tt>debtSourceCode： String:来源机构</tt><br>
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
				<tt>allotDate： Date:分配日期</tt><br>	
				<tt>holdAmount： BigDecimal:持有金额/投资金额</tt><br>
				<tt>holdScale： BigDecimal:持有比例</tt><br>	
				<tt>assetTypeCode： String:资产类型</tt><br>	
				<tt>loanDesc： String:借款用途</tt><br>	
				<tt>repaymentMethod： String:还款方式</tt><br>	
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数"),
			@Rule(name = "productType", required = true, requiredMessage = "产品类型不能为空!")
	})
	public Map<String, Object> findLoanList(Map<String, Object> param) throws SLException;
	
	/**
	 * 我的投资-债权明细
	 *
	 * @author  wangjf
	 * @date    2015年4月27日 下午8:18:07
	 * @param <tt>loanId： String:债权ID</tt><br>
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
				<tt>holdAmount： BigDecimal:持有金额/投资金额</tt><br>
				<tt>holdScale： BigDecimal:持有比例</tt><br>
	 
 				data:List<Map<String, object>>
 				<tt>currentTerm： BigInteger:还款期数</tt><br>
	 			<tt>repaymentAmount： BigDecimal:还款本息</tt><br>
	 			<tt>repaymentStatus： BigInteger:还款状态</tt><br>
	  			
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "loanId", required = true, requiredMessage = "债权主键不能为空!")
	})
	public Map<String, Object> findLoanDetailInfo(Map<String, Object> param) throws SLException;
	
	/**
	 * 业务管理-债权数据汇总（查询已分配的债权价值、金额、笔数）
	 *
	 * @author  wangjf
	 * @date    2015年4月24日 下午2:25:50
	 * @param param
	        <tt>start：int:分页起始页</tt><br>
	 		<tt>length：int:每页长度</tt><br>
	 		<tt>productType：String:产品类型</tt><br>
	 		<tt>custId：String:客户编号</tt><br>
	 		<tt>debtSource： String:来源机构(可为空)</tt><br>
			<tt>productCode： String:借款产品(可为空)</tt><br>
			<tt>yearRateBegin： String:年化利率开始(可为空)</tt><br>
			<tt>yearRateEnd： String:年化利率结束(可为空)</tt><br>
			<tt>loanTerm： String:借款期限(可为空)</tt><br>
			<tt>creditRightStatus： String:借款状态(可为空)</tt><br>
			<tt>repaymentDay： String:还款日(可为空)</tt><br>
			<tt>importDateBegin： String:导入日期开始(可为空)</tt><br>
			<tt>importDateEnd： String:导入日期结束(可为空)</tt><br>
			<tt>allotDateBegin： String:分配日期开始(可为空)</tt><br>
			<tt>allotDateEnd： String:分配日期结束(可为空)</tt><br>
	 * @return
	  		<tt>totalPv： BigDecimal:价值汇总</tt><br>
	 		<tt>totalLoanAmount： BigDecimal:债权金额汇总</tt><br>
	 		<tt>totalLoans： Integer:总笔数</tt><br>
	 */
	public Map<String, Object> findLoanListCount(Map<String, Object> param);
	
	/**
	 * 查询用户每天价值情况
	 *
	 * @author  wangjf
	 * @date    2015年5月23日 下午12:03:43
	 * @param param
	 * 		<tt>start：int:分页起始页</tt><br>
	 		<tt>length：int:每页长度</tt><br>
	 		<tt>productName：String:产品名称</tt><br>
	 		<tt>custId：String:客户编号</tt><br>
	 		<tt>dateBegin： String:日期开始(可为空)</tt><br>
			<tt>dateEnd： String:日期结束(可为空)</tt><br>
	 * @return
	 		iTotalDisplayRecords: 总条数
	 * 		data:List<Map<String, object>>
	 		Map<String, object>:
	 * 		<tt>dailyValueId： String:主键</tt><br>
	 		<tt>date： String:日期</tt><br>
	  		<tt>productName： String:产品名称</tt><br>
	 		<tt>holdValue： BigDecimal:持有价值</tt><br>
	 		<tt>subAccountId:String:分账户ID</tt><br>
	 */
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数"),
			@Rule(name = "custId", required = true, requiredMessage = "客户编号不能为空!")
	})
	public Map<String, Object> findDailyValueList(Map<String, Object> param);
	
	/**
	 * 查询用户每天所拥有的债权情况
	 *
	 * @author  wangjf
	 * @date    2015年5月23日 下午12:05:01
	 * @param param
	 		<tt>start：int:分页起始页</tt><br>
	 		<tt>length：int:每页长度</tt><br>
	 		<tt>dailyValueId： String:用户每天价值情况主键</tt><br>
	 		<tt>custId：String:客户主键</tt><br>
	 		<tt>subAccountId:String:分账户ID</tt><br>
	 * @return
	 * 		iTotalDisplayRecords: 总条数
	 * 		data:List<Map<String, object>>
	 		Map<String, object>:
	 * 		<tt>loanCode： String:债权编号</tt><br>
	 		<tt>debtSource： String:来源机构</tt><br>
			<tt>loanAmount： BigDecimal:借款金额（元）</tt><br>
			<tt>holdValue： BigDecimal:持有价值</tt><br>
			<tt>loanTerm： String:借款期限（月）</tt><br>
			<tt>yearRate： BigDecimal:年化利率</tt><br>
			<tt>assetTypeCode： String:资产类型</tt><br>	
			<tt>loanDesc： String:借款用途</tt><br>	
			<tt>repaymentMethod： String:还款方式</tt><br>	
	 */
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数"),
			@Rule(name = "dailyValueId", required = true, requiredMessage = "用户每天价值情况主键不能为空!")
	})
	public Map<String, Object> findDailyLoanList(Map<String, Object> param);
	
	/**
	 * 保存每日债权和用户价值
	 *
	 * @author  wangjf
	 * @date    2015年5月23日 下午3:22:52
	 * @param now
	 */
	public void saveDailyLoanAndValue(Date now, String productName) throws SLException;

	public ResultVo queryExpLoan();
	
	/**
	 * 根据loanCode查询对象
	 * @param loanCode
	 * @return
	 */
	public LoanInfoEntity findByLoanCode(String loanCode);
}
