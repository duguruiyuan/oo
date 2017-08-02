package com.slfinance.shanlincaifu.service;

import java.util.List;
import java.util.Map;

/**
 * 业务数据总览
 * @author zhangt
 *
 */
public interface CustBusinessHistoryService {
	
	/**
	 * 数据总览
	 * @param param
	 * @return
	 */
	public Map<String, Object> findCustBusinessHistoryInfo(Map<String, Object> param);
	
	/**
	 * 导出数据
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> export(Map<String, Object> param);

}
