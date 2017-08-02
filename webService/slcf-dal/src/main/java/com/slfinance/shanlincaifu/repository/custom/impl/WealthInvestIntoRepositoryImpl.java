package com.slfinance.shanlincaifu.repository.custom.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Maps;
import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.WealthInvestIntoRepositoryCustom;
import com.slfinance.shanlincaifu.utils.SqlCondition;

@Repository
public class WealthInvestIntoRepositoryImpl implements WealthInvestIntoRepositoryCustom {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private RepositoryUtil repositoryUtil;

	/** 同步理财投资信息表BAO_T_WEALTH_INVEST_INFO */
	public void synchronizeInvestInfoFromWealth() {
		StringBuilder sql = new StringBuilder()
		.append(" INSERT INTO BAO_T_WEALTH_INVEST_INFO ( ID /* 1 */ ")
		.append(" 	, CUSTOMER_ID /* 2 */ ")
		.append(" 	, INVEST_START_DATE /* 3 */ ")
		.append(" 	, INVEST_END_DATE /* 4 */ ")
		.append(" 	, MATCH_AMT /* 5 */ ")
		.append(" 	, INVEST_AMT /* 6 */ ")
		.append(" 	, PRODUCT_TYPE /* 7 */ ")
		.append(" 	, FLOW_ID /* 8 */ ")
		.append(" 	, PROTOCOL_NO /* 9 */ ")
		.append(" 	, LENDING_NO /* 10 */ ")
		.append(" 	, STATUS /* 11 */ ")
		.append(" 	, AUDIT_DATE /* 12 */ ")
		.append(" 	, RECORD_STATUS /* 13 */ ")
		.append(" 	, CREATE_USER /* 14 */ ")
		.append(" 	, CREATE_DATE /* 15 */ ")
		.append(" 	, LAST_UPDATE_USER /* 16 */ ")
		.append(" 	, LAST_UPDATE_DATE /* 17 */ ")
		.append(" 	, VERSION /* 18 */ ")
		.append(" 	, MEMO /* 19 */ ")
		.append(" ) ")
		.append(" SELECT invest.ID /* 1 */ ")
		.append(" 	, invest.CUSTOMER_ID /* 2 */ ")
		.append(" 	, invest.INVEST_START_DATE /* 3 */ ")
		.append(" 	, invest.INVEST_END_DATE /* 4 */ ")
		.append(" 	, invest.MATCH_AMT /* 5 */ ")
		.append(" 	, invest.INVEST_AMT /* 6 */ ")
		.append(" 	, invest.PRODUCT_TYPE /* 7 */ ")
		.append(" 	, invest.FLOW_ID /* 8 */ ")
		.append(" 	, invest.PROTOCOL_NO /* 9 */ ")
		.append(" 	, invest.LENDING_NO /* 10 */ ")
		.append(" 	, invest.STATUS /* 11 */ ")
		.append(" 	, NVL((SELECT max(aud.OPT_DATE) FROM FT_T_AUDIT@SLCF_WEALTH_LINK aud WHERE aud.BUSI_ID = invest.ID),CREATE_DATE) AUDIT_DATE /* 12 */ ")
		.append(" 	, '有效' RECORD_STATUS /* 13 */ ")
		.append(" 	, invest.CREATE_USER_ID CREATE_USER /* 14 */ ")
		.append(" 	, invest.CREATE_DATE /* 15 */ ")
		.append(" 	, invest.UPDATE_USER_ID LAST_UPDATE_USER /* 16 */ ")
		.append(" 	, invest.UPDATE_DATE LAST_UPDATE_DATE /* 17 */ ")
		.append(" 	, invest.VERSION /* 18 */ ")
		.append(" 	, invest.MEMO /* 19 */ ")
		.append("   FROM FT_T_INVEST_INFO@SLCF_WEALTH_LINK invest ")
		.append("  WHERE NOT EXISTS ( SELECT * FROM BAO_T_WEALTH_INVEST_INFO wi WHERE wi.ID = invest.ID ) ")
		.append("    AND EXISTS ( SELECT * FROM BAO_T_WEALTH_CUST_INTO wc WHERE wc.ID = invest.CUSTOMER_ID ) ")
		.append("    AND invest.FLOW_ID IN ('02000002', '02000005', '02000006', '02000007' ,'02000008', '02000009', '02000010', '02000011', '02000013', '02000014') ")
		.append("    AND invest.CREATE_DATE > (SELECT nvl(max(CREATE_DATE), to_date('1900-01-01','yyyy-MM-dd')) FROM BAO_T_WEALTH_INVEST_INFO) ")
		.append("     ");
		jdbcTemplate.execute(sql.toString());
	}

	@Override
	public Map<String, Object> queryOfflineInvestList(Map<String, Object> params) {
		Map<String, Object> result = Maps.newHashMap();
		StringBuffer sql = new StringBuffer()
				.append("  select i.id                 \"investId\",  ")
				.append("         i.lending_no         \"lendingNo\",  ")
				.append("         i.contract_no        \"contractNo\",  ")
				.append("         v.product_term       \"typeTerm\",  ")
				.append("         v.year_irr           \"yearRate\",  ")
				.append("         i.invest_amt         \"investAmount\",  ")
				.append("         i.invest_start_date  \"investDate\",  ")
				.append("         i.invest_end_date    \"expireDate\",  ")
				.append("         s.appoint_trade_date \"atoneDate\",  ")
				.append("         decode(i.flow_id, '02000005', '投资生效', '02000006', '投资生效', '02000008', '投资生效', '02000009', '投资生效', '02000010', '投资生效', '02000014', '投资生效', '02000013', '投资生效',  ")
				.append("                           '02000011', '投资结束', '02000003', '投资作废', '02000012', '投资作废', '02000004', '投资作废', '审核中') \"investStatus\",  ")
				.append("         p.product_name       \"productName\"  ")
				.append("   from   ")
				.append("     bao_t_cust_info cust   ")
				.append("     inner join ft_t_customer_info@slcf_wealth_link c on c.card_id = cust.credentials_code  ")
				.append("     inner join ft_t_invest_info@slcf_wealth_link i on i.customer_id = c.id  ")
				.append("     inner join ft_t_product_info@slcf_wealth_link p on p.id = i.product_type  ")
				.append("     inner join ft_t_version_info@slcf_wealth_link v on v.version_no = i.agreement_ver and v.product_id = p.id  ")
				.append("     left  join ft_t_sell_info@slcf_wealth_link s on s.invest_id = i.id and s.sell_status not in ('04000007', '04000003') and i.flow_id = '02000011'  ")
				.append("   where 1 = 1  ")
				.append("   and exists ( ")
				.append("       select 1 ")
				.append("         from com_t_param t ")
				.append("        where t.type = 'shanLinCaiFuSet' ")
				.append("          and t.parameter_name = '是否展示线下数据' ")
				.append("          and t.value = 'Y' ")
				.append("   ) %s ")
				.append("   order by i.create_date desc  ");
		
		StringBuilder whereSqlString= new StringBuilder();
		SqlCondition sqlCondition = new SqlCondition(whereSqlString, params);
		sqlCondition.addString("custId", "cust.id")
					.addBeginDate("beginInvestDate", "i.invest_start_date")
					.addEndDate("endInvestDate", "i.invest_start_date")
					.addString("investStatus", "decode(i.flow_id, '02000005', '投资生效', '02000006', '投资生效', '02000008', '投资生效', '02000009', '投资生效', '02000010', '投资生效', '02000014', '投资生效', '02000013', '投资生效', '02000011', '投资结束', '02000003', '投资作废', '02000012', '投资作废', '02000004', '投资作废', '审核中')");
		
		Page<Map<String, Object>> pageVo = repositoryUtil.queryForPageMap(String.format(sql.toString(), sqlCondition.toString()), sqlCondition.toArray(), 
				Integer.parseInt(params.get("start").toString()), Integer.parseInt(params.get("length").toString()));
		result.put("iTotalDisplayRecords", pageVo.getTotalElements());
		result.put("data", pageVo.getContent());
		return result;
	}

}
