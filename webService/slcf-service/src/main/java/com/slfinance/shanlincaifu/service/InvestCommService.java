package com.slfinance.shanlincaifu.service;

import java.util.List;
import java.util.Map;

import com.slfinance.exception.SLException;

/**
 * 
 * @author zhangt
 * 活期宝业绩统计
 */
public interface InvestCommService {

	/**
	 * 业绩统计列表
	 * @param param
	 * @return
	 * @throws SLException
	 */
	public  Map<String, Object> findAllInvestCommInfo(Map<String, Object> param) throws SLException;
	
	/**
	 * 业绩详情
	 * @param param
	 * @return
	 * @throws SLException
	 */
	public  Map<String, Object> findInvestCommInfoByMonth(Map<String, Object> param) throws SLException;
	
	/**
	 * 导出数据
	 * @param param
	 * @return
	 * @throws SLException
	 */
	public  List<Map<String, Object>> export(Map<String, Object> param) throws SLException;
	
}
