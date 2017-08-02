package com.slfinance.shanlincaifu.repository.custom.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.slfinance.shanlincaifu.repository.custom.WealthCustIntoRepositoryCustom;

@Repository
public class WealthCustIntoRepositoryImpl implements WealthCustIntoRepositoryCustom {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	/** 同步理财客户信息表BAO_T_WEALTH_CUST_INTO */
	public void synchronizeCustInfoFromWealth() {
		StringBuilder sql = new StringBuilder()
		.append(" INSERT INTO BAO_T_WEALTH_CUST_INTO ( ID /* 1 */ ")
		.append(" 	, CUSTOMER_CODE /* 2 */ ")
		.append(" 	, NAME /* 3 */ ")
		.append(" 	, CARD_TYPE /* 4 */ ")
		.append(" 	, CARD_ID /* 5 */ ")
		.append(" 	, CUSTOMER_MANAGER_NAME /* 6 */ ")
		.append(" 	, CUSTOMER_MANAGER_CARD_TYPE /* 7 */ ")
		.append(" 	, CUSTOMER_MANAGER_CARD_ID /* 8 */ ")
		.append(" 	, CUSTOMER_MANAGER_ID /* 9 */ ")
		.append(" 	, SOURCE /* 10 */ ")
		.append(" 	, \"TYPE\" /* 11 */ ")
		.append(" 	, RECORD_STATUS /* 12 */ ")
		.append(" 	, CREATE_USER /* 13 */ ")
		.append(" 	, CREATE_DATE /* 14 */ ")
		.append(" 	, LAST_UPDATE_USER /* 15 */ ")
		.append(" 	, LAST_UPDATE_DATE /* 16 */ ")
		.append(" 	, VERSION /* 17 */ ")
		.append(" 	, MEMO /* 18 */ ")
		.append(" ) ")
		.append(" SELECT cust.ID /* 1 */ ")
		.append(" 	, cust.CUSTOMER_CODE /* 2 */ ")
		.append(" 	, cust.NAME /* 3 */ ")
		.append(" 	, cust.CARD_TYPE /* 4 */ ")
		.append(" 	, cust.CARD_ID /* 5 */ ")
		.append(" 	, userer.USER_NAME CUSTOMER_MANAGER_NAME /* 6 */ ")
		.append(" 	, '1' CUSTOMER_MANAGER_CARD_TYPE /* 7 */ ")
		.append(" 	, userer.CREDENTIALS_CODE CUSTOMER_MANAGER_CARD_ID /* 8 */ ")
		.append(" 	, cust.CUSTOMER_MANAGER_ID /* 9 */ ")
		.append(" 	, cust.SOURCE /* 10 */ ")
		.append(" 	, cust.\"TYPE\" /* 11 */ ")
		.append(" 	, '有效' RECORD_STATUS /* 12 */ ")
		.append(" 	, cust.CREATE_USER_ID CREATE_USER /* 13 */ ")
		.append(" 	, cust.CREATE_DATE /* 14 */ ")
		.append(" 	, cust.UPDATE_USER_ID LAST_UPDATE_USER /* 15 */ ")
		.append(" 	, cust.UPDATE_DATE LAST_UPDATE_DATE /* 16 */ ")
		.append(" 	, cust.VERSION /* 17 */ ")
		.append(" 	, cust.MEMO /* 18 */  ")
		.append("   FROM FT_T_CUSTOMER_INFO@SLCF_WEALTH_LINK cust,  ")
		.append("   COM_T_USER@SLCF_WEALTH_LINK userer ")
		.append("  WHERE NOT EXISTS ( SELECT * FROM BAO_T_WEALTH_CUST_INTO wc WHERE wc.ID = cust.ID ) ")
		.append("    AND exists ( ")
		.append("       select count(1), s.card_id  ")
		.append("       from FT_T_CUSTOMER_INFO@SLCF_WEALTH_LINK s ")
		.append("       where s.card_id = cust.card_id ")
		.append("         and s.TYPE != 5 ")
		.append("       group by s.card_id ")
		.append("       having count(1) = 1 ")
		.append(" 	 ) /* 滤掉身份证号相同 */")
		.append("    AND cust.CUSTOMER_MANAGER_ID = userer.ID ")
		.append("    AND userer.CREDENTIALS_CODE IS NOT NULL ")
		.append("    AND userer.CREDENTIALS_TYPE IS NOT NULL ")
		.append("    AND cust.TYPE != 5 /* 过滤掉无效客户 */")
		.append("    AND cust.CREATE_DATE > (SELECT nvl(max(CREATE_DATE), to_date('1900-01-01','yyyy-MM-dd'))  FROM BAO_T_WEALTH_CUST_INTO) ")
		.append("  ");
		jdbcTemplate.execute(sql.toString());
	}
}
