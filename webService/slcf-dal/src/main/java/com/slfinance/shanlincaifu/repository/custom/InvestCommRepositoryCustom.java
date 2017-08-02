package com.slfinance.shanlincaifu.repository.custom;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.slfinance.exception.SLException;

/**
 * 
 * @author zhangt
 * 报表管理--活期宝业绩统计
 */
public interface InvestCommRepositoryCustom {
	
	/**
	 * 业绩列表
	 * @param param
	 * @return
	 * @throws SLException
	 */
	public Page<Map<String, Object>> getInvestCommListPage(Map<String, Object> param) throws SLException;
	
	/**
	 * 查看详情
	 * @param param
	 * @return
	 * @throws SLException
	 */
	public Page<Map<String, Object>> getInvestCommListByMonth(Map<String, Object> param) throws SLException; 
	
	/**
	 * 导出数据
	 * @param param
	 * @return
	 * @throws SLException
	 */
	public List<Map<String,Object>> export(Map<String, Object> param) throws SLException;
	
}
