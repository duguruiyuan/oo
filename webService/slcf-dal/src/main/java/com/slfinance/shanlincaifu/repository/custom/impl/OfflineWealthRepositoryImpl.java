package com.slfinance.shanlincaifu.repository.custom.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.OfflineWealthRepositoryCustom;

@Repository
public class OfflineWealthRepositoryImpl implements
		OfflineWealthRepositoryCustom {
	
	@Autowired
	private RepositoryUtil repositoryUtil;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public List<Map<String, Object>> findOffLineCustInfo(Map<String, Object> params) {
		StringBuilder sqlString = new StringBuilder()
				.append(" select cust.id                     \"custId\", ")
				.append("        cust.credentials_code       \"custIdcard\", ")
				.append("        min(wi.audit_date)   \"investStartDate\", ")
				.append("        wc.customer_manager_card_id \"manageCardId\"       ")
				.append(" from bao_t_cust_info cust  ")
				.append(" inner join bao_t_wealth_cust_into wc on wc.card_id = cust.credentials_code ")
				.append(" inner join bao_t_wealth_invest_info wi on wi.customer_id = wc.id ")
				.append(" where cust.wealth_flag = '00' ")
				.append(" group by cust.id, cust.credentials_code, wc.customer_manager_card_id ");
		return repositoryUtil.queryForMap(sqlString.toString(), new Object[]{});
	}

}
