package com.slfinance.shanlincaifu.service;

import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;

/**
 * 员工表服务类
 * 
 * @author zhiwen_feng
 *
 */
public interface EmployeeService {

	/**
	 * 业绩详情
	 * @author  liyy
	 * @date    2016年4月19日
	 * @param map
	 		<tt>start：String:起始值</tt><br>
	 		<tt>length：String:长度</tt><br>
	 		<tt>commMonth：String:业绩月份</tt><br>
	  		<tt>empName：String:姓名(可选)</tt><br>
	  		<tt>credentialsCode：String:身份证号码(可选)</tt><br>
	  		<tt>provinceName：String:省份(可选)</tt><br>
	  		<tt>cityName：String:城市(可选)</tt><br>
	  		<tt>team3：String:所在小团队(可选)</tt><br>
	  		<tt>team2：String:所在大团队(可选)</tt><br>
	  		<tt>team1：String:所在门店/营业部(可选)</tt><br>
	 * @return
			<tt>totalYearInvestAmount：String:累计年化业绩</tt><br>
			<tt>totalInvestAmount：String:累计进账总额</tt><br>
			<tt>iTotalDisplayRecords：String:起始值</tt><br>
			<tt>data：List:起始值
				<tt>custId：</tt><br>
				<tt>empNo：</tt><br>
				<tt>empName：</tt><br>
				<tt>credentialsCode：</tt><br>
				<tt>deptId：</tt><br>
				<tt>yearInvestAmount：</tt><br>
				<tt>investAmount：</tt><br>
				<tt>proName：</tt><br>
				<tt>team3Manager：小团队经理</tt><br>
				<tt>team2Manager：大团队经理</tt><br>
				<tt>team1Manager：营业部经理</tt><br>
				<tt>team1：所属营业部</tt><br>
			</tt><br>
	 */
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "起始值不能为空!", number = true, numberMessage = "起始值必须为数字")
			, @Rule(name = "length", required = true, requiredMessage = "长度不能为空!", number = true, numberMessage = "长度必须为数字")
			, @Rule(name = "commMonth", required = true, requiredMessage = "业绩月份不能为空!")
	})
	public ResultVo queryCommissionList(Map<String, Object> param);
	
	/**
	 * 业绩明细
	 * @author  liyy
	 * @date    2016年4月19日
	 * @param map
	 		<tt>custId：String:客户ID</tt><br>
	 		<tt>commMonth：String:业绩月份</tt><br>
	 		<tt>start：String:业绩月份</tt><br>
	 		<tt>length：String:业绩月份</tt><br>
	 * @return
	 */
	@Rules(rules = { 
			@Rule(name = "commMonth", required = true, requiredMessage = "业绩月份不能为空!")
			, @Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空!")
			, @Rule(name = "start", required = true, requiredMessage = "起始值不能为空!", number = true, numberMessage = "起始值必须为数字")
			, @Rule(name = "length", required = true, requiredMessage = "长度不能为空!", number = true, numberMessage = "长度必须为数字")
			
	})
	public ResultVo queryCommissionDetailList(Map<String, Object> param);
	
	/**
	 * 赎回详情
	 * @author  liyy
	 * @date    2016年4月21日
	 * @param map
	 		<tt>start：String:起始值</tt><br>
	 		<tt>length：String:长度</tt><br>
	 		<tt>commMonth：String:业绩月份</tt><br>
	  		<tt>empName：String:姓名(可选)</tt><br>
	  		<tt>credentialsCode：String:身份证号码(可选)</tt><br>
	  		<tt>provinceName：String:省份(可选)</tt><br>
	  		<tt>cityName：String:城市(可选)</tt><br>
	  		<tt>team3：String:所在小团队(可选)</tt><br>
	  		<tt>team2：String:所在大团队(可选)</tt><br>
	  		<tt>team1：String:所在门店/营业部(可选)</tt><br>
	 * @return
			<tt>sumYearInvestAmount：String:累计年化业绩</tt><br>
			<tt>sumInvestAmount：String:累计进账总额</tt><br>
			<tt>iTotalDisplayRecords：String:起始值</tt><br>
			<tt>data：List:起始值
				<tt>commissionId：</tt><br>
				<tt>custId：</tt><br>
				<tt>empNo：</tt><br>
				<tt>empName：</tt><br>
				<tt>credentialsCode：</tt><br>
				<tt>deptId：</tt><br>
				<tt>yearInvestAmount：</tt><br>
				<tt>investAmount：</tt><br>
				<tt>proName：</tt><br>
				<tt>team3Manager：小团队经理</tt><br>
				<tt>team2Manager：大团队经理</tt><br>
				<tt>team1Manager：营业部经理</tt><br>
				<tt>team1：所属营业部</tt><br>
			</tt><br>
	 */
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "起始值不能为空!", number = true, numberMessage = "起始值必须为数字")
			, @Rule(name = "length", required = true, requiredMessage = "长度不能为空!", number = true, numberMessage = "长度必须为数字")
			, @Rule(name = "commMonth", required = true, requiredMessage = "业绩月份不能为空!")
	})
	public ResultVo queryCommissionAtoneList(Map<String, Object> param);
	
	/**
	 * 赎回明细
	 * @author  liyy
	 * @date    2016年4月21日
	 * @param map
	 		<tt>custId：String:客户ID</tt><br>
	 		<tt>commMonth：String:业绩月份</tt><br>
	 		<tt>start：String:业绩月份</tt><br>
	 		<tt>length：String:业绩月份</tt><br>
	 * @return
	 */
	@Rules(rules = { 
			@Rule(name = "commMonth", required = true, requiredMessage = "业绩月份不能为空!")
			, @Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空!")
			, @Rule(name = "start", required = true, requiredMessage = "起始值不能为空!", number = true, numberMessage = "起始值必须为数字")
			, @Rule(name = "length", required = true, requiredMessage = "长度不能为空!", number = true, numberMessage = "长度必须为数字")
			
	})
	public ResultVo queryCommissionAtoneDetailList(Map<String, Object> param);
	
	/**
	 * 员工业绩列表
	 * 
	 * @author zhiwen_feng
	 * @dete 2016-04-21
	 * @param params
	 *		<tt>start：String:起始值</tt><br>
	 * 		<tt>length：String:长度</tt><br>
	 * 		<tt>commMonth：String:业绩月份</tt><br>
	 * @return ResultVo
     *      <tt>commDate        :String:报告生成时间	</tt><br>
     *      <tt>incomeAmount    :String:当月进账总额</tt><br>
     *      <tt>yearInvestAmount:String:当月年化业绩</tt><br>
     *      <tt>commMonth       :String:业绩月份</tt><br>
	 * @throws SLException
	 */
	public ResultVo queryEmployeeMonthAchievement(Map<String, Object> params)throws SLException;
}
