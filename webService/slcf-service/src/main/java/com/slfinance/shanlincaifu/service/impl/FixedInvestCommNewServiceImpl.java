package com.slfinance.shanlincaifu.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.repository.custom.FixedInvestCommNewRepositoryCustom;
import com.slfinance.shanlincaifu.service.FixedInvestCommNewService;

/**
 * 
 * @author zhangt
 * 报表管理--新定期宝业绩统计
 */
@Service
public class FixedInvestCommNewServiceImpl implements FixedInvestCommNewService {

	@Autowired
	private FixedInvestCommNewRepositoryCustom fixedInvestCommNewRepositoryCustom;
	
	//业绩统计
	@Override
	public Map<String, Object> findAllFixedInvestCommInfo(
			Map<String, Object> param) throws SLException {	
		Page<Map<String, Object>> page = fixedInvestCommNewRepositoryCustom.getFixedInvestCommListPage(param);
		HashMap<String, Object> result = new HashMap<String, Object>();
		result.put("iTotalDisplayRecords", page.getTotalElements());
		result.put("data", page.getContent());
		return result;
	}

	//业绩详情
	@Override
	public Map<String, Object> getFixedInvestCommListByMonth(
			Map<String, Object> param) throws SLException {
		Page<Map<String, Object>> page = fixedInvestCommNewRepositoryCustom.getFixedInvestCommListByMonth(param);
		HashMap<String, Object> result = new HashMap<String, Object>();
		result.put("iTotalDisplayRecords", page.getTotalElements());
		result.put("data", page.getContent());
		return result;
	}

	//导出数据
	@Override
	public List<Map<String, Object>> export(Map<String, Object> param)
			throws SLException {
		return fixedInvestCommNewRepositoryCustom.export(param);
	}

}
