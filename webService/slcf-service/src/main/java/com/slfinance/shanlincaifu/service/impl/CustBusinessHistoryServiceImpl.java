package com.slfinance.shanlincaifu.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.slfinance.shanlincaifu.repository.custom.CustBusinessHistoryRepositoryCustom;
import com.slfinance.shanlincaifu.service.CustBusinessHistoryService;

/**
 * 业务数据总览
 * @author zhangt
 *
 */
@Service
public class CustBusinessHistoryServiceImpl implements
		CustBusinessHistoryService {

	@Autowired
	private CustBusinessHistoryRepositoryCustom custBusinessHistoryRepositoryCustom;
	
	//数据总览
	@Override
	public Map<String, Object> findCustBusinessHistoryInfo(Map<String, Object> param) {
		Page<Map<String, Object>> page = custBusinessHistoryRepositoryCustom.getCustBusinessHistoryPage(param);
		HashMap<String, Object> result = new HashMap<String, Object>();
		result.put("iTotalDisplayRecords", page.getTotalElements());
		result.put("data", page.getContent());
		return result;
	}

	//导出数据
	@Override
	public List<Map<String, Object>> export(Map<String, Object> param) {
		return custBusinessHistoryRepositoryCustom.export(param);
	}

}
