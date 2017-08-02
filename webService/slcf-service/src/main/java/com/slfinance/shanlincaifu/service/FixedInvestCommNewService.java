package com.slfinance.shanlincaifu.service;

import java.util.List;
import java.util.Map;

import com.slfinance.exception.SLException;

/**
 * 
 * @author zhangt
 * 报表管理--新定期宝业绩统计
 */
public interface FixedInvestCommNewService {

	/**
	 * 业绩统计
	 * @param param
	 * @return
	 * @throws SLException
	 */
	Map<String, Object> findAllFixedInvestCommInfo(Map<String, Object> param) throws SLException;

	/**
	 * 业绩详情
	 * @param param
	 * @return
	 * @throws SLException
	 */
	Map<String, Object> getFixedInvestCommListByMonth(Map<String, Object> param) throws SLException;
	
	/**
	 * 导出数据
	 * @param param
	 * @return
	 * @throws SLException
	 */
	public List<Map<String,Object>> export(Map<String, Object> param)throws SLException;
}
