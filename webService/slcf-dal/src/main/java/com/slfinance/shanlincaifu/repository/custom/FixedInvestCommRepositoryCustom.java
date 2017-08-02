package com.slfinance.shanlincaifu.repository.custom;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.slfinance.exception.SLException;

/**
 * 
 * yanq
 * 
 *查询定期宝业绩信息
 * */
public interface FixedInvestCommRepositoryCustom {
	/**查询定期宝业绩列表*/
	public Page<Map<String, Object>> getFixedInvestCommListPage(Map<String, Object> param) throws SLException;
	/**
	 * 查看详情
	 * 查询每个月份的业绩列表*/
	public Page<Map<String, Object>> getFixedInvestCommListByMonth(Map<String, Object> monthMap) throws SLException;

	/**
	 * 导出当月报表数据
	 * @param param
	 * @return
	 * @throws SLException
	 */
	public List<Map<String,Object>> export(Map<String, Object> param)throws SLException;
}
