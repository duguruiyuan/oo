package com.slfinance.shanlincaifu.repository.custom.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.LimitedInfoCustom;

@Repository
public class LimitedInfoRepositoryImpl implements LimitedInfoCustom{

	@Autowired
	RepositoryUtil repositoryUtil;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public List<Map<String, Object>> queryLimitedInfo(Map<String, Object> params) {
		StringBuilder sql = new StringBuilder()
		.append(" SELECT limit.ID \"limitedId\" ")
		.append("      , limit.RECHARGE_TYPE \"rechargeType\" ")
		.append("      , limit.BANK_NAME \"bankName\" ")
		.append("      , CASE WHEN limit.SINGLE_LIMITED <10000 THEN limit.SINGLE_LIMITED||'' ELSE limit.SINGLE_LIMITED/10000 ||'万' END \"singleLimited\" ")
		.append("      , CASE WHEN limit.DAILY_LIMITED <10000 THEN limit.DAILY_LIMITED||'' ELSE limit.DAILY_LIMITED/10000 ||'万' END \"dailyLimited\" ")
		.append("      , CASE WHEN limit.MONTHLY_LIMITED <10000 THEN limit.MONTHLY_LIMITED||'' ELSE limit.MONTHLY_LIMITED/10000 ||'万' END \"monthlyLimited\" ")
		.append("      , limit.BANK_TELEPHONE \"bankTelephone\" ")
		.append("      , limit.DESCR \"descr\" ")
		.append("      , limit.MEMO \"memo\" ")
		.append("   FROM BAO_T_BANK_LIMITED_INFO limit ")
		.append("  where limit.RECHARGE_TYPE=? ")
		.append("  order by BANK_CODE asc ")
		;
		
		List<Object> objList = Lists.newArrayList();
		objList.add(params.get("rechargeType"));
		return repositoryUtil.queryForMap(sql.toString(), objList.toArray());
	}

	
}
