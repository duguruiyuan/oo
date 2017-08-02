package com.slfinance.shanlincaifu.repository.custom.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.AutoInvestRepositoryCustom;
import com.slfinance.shanlincaifu.utils.SqlCondition;

@Repository
public class AutoInvestRepositoryCustomImpl implements AutoInvestRepositoryCustom {
	@Autowired
	RepositoryUtil repositoryUtil;
	
	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public List<Map<String, Object>> queryAutoInvestInfoForSZD(Map<String, Object> param) {

		SqlCondition autoInvestInfoSql = getAutoInvestInfoSql(param);
		autoInvestInfoSql.addSql("order by a.cust_priority desc,a.open_date asc ");
		
		List<Map<String, Object>> resultList = repositoryUtil.queryForMap(autoInvestInfoSql.toString(), autoInvestInfoSql.toArray());
		return resultList;
	}

	@Override
	public List<Map<String, Object>> queryAutoInvestInfoCurrent(Map<String, Object> param) {
		SqlCondition autoInvestInfoSql = getAutoInvestInfoSql(param);
		autoInvestInfoSql.addString("autoInvestId", "a.id");
		List<Map<String, Object>> resultList =repositoryUtil.queryForMap(autoInvestInfoSql.toString(), autoInvestInfoSql.toArray());
		return resultList;
	}
	
	 public SqlCondition getAutoInvestInfoSql(Map<String, Object> param){
		 StringBuilder sqlString = new StringBuilder() 
			.append(" select  a.id \"id\" from bao_t_auto_invest_info  a  ")
			.append(" where  a.open_status = '启用' ");
			List<Object> args = new ArrayList<Object>();
			if (!StringUtils.isEmpty(param.get("loanTerm")) && !StringUtils.isEmpty(param.get("loanUnit"))) {
				sqlString
				.append(" and a.limited_term_min*decode(a.LOAN_UNIT, '天',1,'月',30,1) <= ? ")
				.append(" and a.limited_term*decode(a.LOAN_UNIT, '天',1,'月',30,1) >= ? ");
				if ("月".equals(param.get("loanUnit").toString())) {
					int loanTerm = Integer.valueOf(param.get("loanTerm").toString())*30;
					args.add(loanTerm);
					args.add(loanTerm);
				}else if ("天".equals(param.get("loanUnit").toString())) {
					int loanTerm = Integer.valueOf(param.get("loanTerm").toString());
					args.add(loanTerm);
					args.add(loanTerm);
				}
			}
			if (!StringUtils.isEmpty(param.get("yearRate"))) {
				sqlString.append(" and a.limited_year_rate <= ? and a.limited_year_rate_max >= ? ");
				args.add(param.get("yearRate"));
				args.add(param.get("yearRate"));
			}
			if (!StringUtils.isEmpty(param.get("repaymentMethod"))) {
				sqlString.append("and a.repayment_method like ? ");
				args.add("%"+param.get("repaymentMethod").toString()+"%");
			}
			if (!StringUtils.isEmpty(param.get("seatTerm")) && !StringUtils.isEmpty(param.get("type"))) {
				sqlString.append("and a.can_invest_product like ? ");
				if ("转让标".equals(param.get("type").toString())) {
					if("-1".equals(param.get("seatTerm").toString())){
						args.add("%转让专区（不可转让标的）%");
					}else {
						args.add("%转让专区（可转让标的）%");
					}
				}
				if ("优选标".equals(param.get("type").toString())) {
					if("-1".equals(param.get("seatTerm").toString())){
						args.add("%优选项目（不可转让标的）%");
					}else {
						args.add("%优选项目（可转让标的）%");
					}
				}
			}else if(StringUtils.isEmpty(param.get("seatTerm")) && !StringUtils.isEmpty(param.get("type"))){
				sqlString.append("and a.can_invest_product like ? ");
				if ("转让标".equals(param.get("type").toString())) {
					args.add("%转让专区（可转让标的）%");
				}
				if ("优选标".equals(param.get("type").toString())) {
					args.add("%优选项目（可转让标的）%");
				}
			}
			
		 SqlCondition sqlCondition = new SqlCondition(sqlString,param,args);
		 return sqlCondition;
	}

	@Override
	public int updateAutoInvestInfoToAvoidDuplicate(String custId, boolean flag) {
		
		int rows = 0;
		
		if(flag) { // 设置标志位
			StringBuilder sqlString = new StringBuilder()
			.append("update bao_t_cust_info set memo = 'locking' where id = ? and memo is null");
			
			rows = jdbcTemplate.update(sqlString.toString(), new Object[]{custId});	
		}
		else { // 取消标志位
			StringBuilder sqlString = new StringBuilder()
			.append("update bao_t_cust_info set memo = null where id = ? and memo = 'locking'");
			
			rows = jdbcTemplate.update(sqlString.toString(), new Object[]{custId});
		}
		
		return rows;
	}

}
