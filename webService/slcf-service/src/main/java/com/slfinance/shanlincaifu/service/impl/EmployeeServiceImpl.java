package com.slfinance.shanlincaifu.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.repository.custom.EmployeeRepositoryCustom;
import com.slfinance.shanlincaifu.service.EmployeeService;
import com.slfinance.vo.ResultVo;

/**
 * 员工表服务类
 * 
 * @author liyy
 *
 */
@Slf4j
@Service("employeeService")
public class EmployeeServiceImpl implements EmployeeService {
	
	@Autowired
	EmployeeRepositoryCustom employeeRepositoryCustom;

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
			<tt>data：List:起始值</tt><br>
	 */
	public ResultVo queryCommissionList(Map<String, Object> param) {
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			Page<Map<String, Object>> page = employeeRepositoryCustom.getCommissionInfo(param);
			List<Map<String, Object>> list =  employeeRepositoryCustom.getAmount(param);
			
			data.put("totalYearInvestAmount", list.get(0).get("totalYearInvestAmount"));
			data.put("totalInvestAmount", list.get(0).get("totalInvestAmount"));
			data.put("iTotalDisplayRecords", page.getTotalElements());
			data.put("data", page.getContent());
			return new ResultVo(true, "业绩详情查询成功", data);
		} catch (Exception e) {
			log.error("业绩详情查询失败" + e.getMessage());
			return new ResultVo(false, "业绩详情查询失败");
		}
	}

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
	public ResultVo queryCommissionDetailList(Map<String, Object> param) {
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			Page<Map<String, Object>> page = employeeRepositoryCustom.getCommissionInfoDetail(param);
			
			data.put("iTotalDisplayRecords", page.getTotalElements());
			data.put("data", page.getContent());
			return new ResultVo(true, "业绩明细查询成功", data);
		} catch (Exception e) {
			log.error("业绩明细查询失败" + e.getMessage());
			return new ResultVo(false, "业绩明细查询失败");
		}
	}

	/**
	 * 赎回详情
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
	 * @return map
			<tt>totalYearInvestAmount：String:累计年化业绩</tt><br>
			<tt>totalInvestAmount：String:累计进账总额</tt><br>
			<tt>iTotalDisplayRecords：String:起始值</tt><br>
			<tt>data：List:起始值</tt><br>
	 */
	public ResultVo queryCommissionAtoneList(Map<String, Object> param) {
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			Page<Map<String, Object>> page = employeeRepositoryCustom.getAtoneInfo(param);
			List<Map<String, Object>> list =  employeeRepositoryCustom.getAtoneAmount(param);
			
			data.put("totalYearInvestAmount", list.get(0).get("totalYearInvestAmount"));
			data.put("totalInvestAmount", list.get(0).get("totalInvestAmount"));
			data.put("iTotalDisplayRecords", page.getTotalElements());
			data.put("data", page.getContent());
			return new ResultVo(true, "业绩详情查询成功", data);
		} catch (Exception e) {
			log.error("业绩详情查询失败" + e.getMessage());
			return new ResultVo(false, "业绩详情查询失败");
		}
	}

	/**
	 * 赎回明细
	 * @author  liyy
	 * @date    2016年4月21日
	 * @param map
	 		<tt>commissionId：String:提成Id</tt><br>
	 		<tt>commMonth：String:业绩月份</tt><br>
	 * @return
	 */
	public ResultVo queryCommissionAtoneDetailList(Map<String, Object> param) {
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			Page<Map<String, Object>> page = employeeRepositoryCustom.getAtoneInfoDetail(param);
			
			data.put("iTotalDisplayRecords", page.getTotalElements());
			data.put("data", page.getContent());
			return new ResultVo(true, "业绩明细查询成功", data);
		} catch (Exception e) {
			log.error("业绩明细查询失败" + e.getMessage());
			return new ResultVo(false, "业绩明细查询失败");
		}
	}

	@Override
	public ResultVo queryEmployeeMonthAchievement(Map<String, Object> params) throws SLException {
		Map<String, Object> map = Maps.newHashMap();
		Page<Map<String, Object>> page = employeeRepositoryCustom.queryEmployeeMonthAchievement(params);
		map.put("iTotalDisplayRecords", page.getTotalElements());
		map.put("data", page.getContent());
		return new ResultVo(true, "查询成功", map);
	}

}
