package com.slfinance.shanlincaifu.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.repository.custom.InvestCommRepositoryCustom;
import com.slfinance.shanlincaifu.service.InvestCommService;

/**
 * 
 * @author zhangt
 * 报表管理--活期宝业绩统计
 */
@Service
@Transactional(readOnly = true)
public class InvestCommServiceImpl implements InvestCommService {

	@Autowired
	private InvestCommRepositoryCustom investCommRepositoryCustom;
	
	//业绩统计
	@Override
	public Map<String, Object> findAllInvestCommInfo(Map<String, Object> param)
			throws SLException {
		Page<Map<String, Object>> page = investCommRepositoryCustom.getInvestCommListPage(param);
		HashMap<String, Object> result = new HashMap<String, Object>();
		result.put("iTotalDisplayRecords", page.getTotalElements());
		result.put("data", page.getContent());
		return result;
	}

	//业绩详情
	@Override
	public Map<String, Object> findInvestCommInfoByMonth(
			Map<String, Object> param) throws SLException {
		Page<Map<String, Object>> page = investCommRepositoryCustom.getInvestCommListByMonth(param);
		HashMap<String, Object> result = new HashMap<String, Object>();
		result.put("iTotalDisplayRecords", page.getTotalElements());
		result.put("data", page.getContent());
		return result;
	}

	//导出数据
	@Override
	public List<Map<String, Object>> export(Map<String, Object> param)
			throws SLException {	
		return investCommRepositoryCustom.export(param);
	}

}
