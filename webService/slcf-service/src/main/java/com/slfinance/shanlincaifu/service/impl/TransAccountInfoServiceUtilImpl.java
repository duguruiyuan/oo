package com.slfinance.shanlincaifu.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.slfinance.shanlincaifu.repository.CustActivityDetailEntityRepository;

/**
 * 
 * @author zhumin
 *
 */
@Service
public class TransAccountInfoServiceUtilImpl {

	@Autowired
	private CustActivityDetailEntityRepository custActivityDetailEntityRepository;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateCustActivityDetailEntityTradeStatus(List<String> listStr, String userActivityTradeStatus) {
		String sql = "UPDATE BAO_T_CUST_ACTIVITY_DETAIL T SET T.TRADE_STATUS='" + userActivityTradeStatus + "' WHERE ID IN(";
		String ids = "";
		if (null != listStr && listStr.size() > 0) {
			for (String id : listStr) {
				ids = ids + "'" + id + "',";
			}
		}
		String idsSQL = ids.substring(0, ids.length() - 1);
		sql = sql.concat(idsSQL).concat(")");
		jdbcTemplate.update(sql);
	}
}