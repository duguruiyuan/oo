package com.slfinance.shanlincaifu.repository.custom;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.slfinance.exception.SLException;

public interface EmployeeRepositoryCustom {

	Page<Map<String, Object>> getCommissionInfo(Map<String, Object> param);
	
	List<Map<String, Object>> getAmount(Map<String, Object> param);
	
	Page<Map<String, Object>> getAtoneInfo(Map<String, Object> param);
	
	List<Map<String, Object>> getAtoneAmount(Map<String, Object> param);

	Page<Map<String, Object>> getCommissionInfoDetail(Map<String, Object> param);

	Page<Map<String, Object>> getAtoneInfoDetail(Map<String, Object> param);
	
	/**
	 * 员工业绩列表
	 * 
	 * @author zhiwen_feng
	 * @dete 2016-04-21
	 */
	public Page<Map<String, Object>> queryEmployeeMonthAchievement(Map<String, Object> params)throws SLException;
	

}
