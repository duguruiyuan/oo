package com.slfinance.shanlincaifu.repository.custom;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

/**
 * 报表--数据总览
 * @author zhangt
 *
 */
public interface CustBusinessHistoryRepositoryCustom {

	/**
	 * 数据总览
	 * @param param
	 * @return
	 */
	public  Page<Map<String, Object>> getCustBusinessHistoryPage(Map<String, Object> param);
	
	/**
	 * 导出数据
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> export(Map<String, Object> param);
}
