package com.slfinance.shanlincaifu.service;

import java.util.List;
import java.util.Map;

import com.slfinance.exception.SLException;

/**
 * yanq
 * 
 * */
public interface FixedInvestCommService {
	/**
	 * 查询所有员工定期宝业绩
	 * */

	Map<String, Object> findAllFixedInvestCommInfo(Map<String, Object> param) throws SLException;

	/**
	 * 查看详情
	 * 
	 * 查询每个月份的业绩列表
	 * 
	 * */

	Map<String, Object> getFixedInvestCommListByMonth(Map<String, Object> paramsMap) throws SLException;
	
	
	/**
	 * 导出数据
	 * @param param
	 * @return
	 * @throws SLException
	 */
	public List<Map<String,Object>> export(Map<String, Object> param)throws SLException;
}
