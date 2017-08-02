package com.slfinance.shanlincaifu.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.repository.custom.FixedInvestCommRepositoryCustom;
import com.slfinance.shanlincaifu.service.FixedInvestCommService;

/**
 * 
 * yanq
 * */
@SuppressWarnings("unused")
@Slf4j
@Service("fixedInvestCommService")
@Transactional(readOnly = true)
public class FixedInvestCommServiceImpl implements FixedInvestCommService {
	@Autowired
	private FixedInvestCommRepositoryCustom fixedInvestCommRepositoryCustom;

	/**
	 * 查询定期宝业绩
	 * 
	 * */
	public Map<String, Object> findAllFixedInvestCommInfo(Map<String, Object> param) throws SLException {

		Page<Map<String, Object>> page = fixedInvestCommRepositoryCustom.getFixedInvestCommListPage(param);
		HashMap<String, Object> result = new HashMap<String, Object>();
		result.put("iTotalDisplayRecords", page.getTotalElements());
		result.put("data", page.getContent());
		return result;
	}

	public Map<String, Object> getFixedInvestCommListByMonth(Map<String, Object> paramsMap) throws SLException {
		Page<Map<String, Object>> page = fixedInvestCommRepositoryCustom.getFixedInvestCommListByMonth(paramsMap);
		HashMap<String, Object> result = new HashMap<String, Object>();
		result.put("iTotalDisplayRecords", page.getTotalElements());
		result.put("data", page.getContent());
		return result;
	}

	public List<Map<String, Object>> export(Map<String, Object> param) throws SLException {
		return fixedInvestCommRepositoryCustom.export(param);
	}

}
