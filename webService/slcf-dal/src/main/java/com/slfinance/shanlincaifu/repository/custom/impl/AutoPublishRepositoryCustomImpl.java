package com.slfinance.shanlincaifu.repository.custom.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.AutoPublishRepositoryCustom;

@Repository
public class AutoPublishRepositoryCustomImpl implements AutoPublishRepositoryCustom {
	@Autowired
	RepositoryUtil repositoryUtil;
	@Override
	public List<Map<String, Object>> queryPublishInfo(Map<String, Object> param) {
		List<Object> paramList=new ArrayList<Object>();
		StringBuilder SqlString = new StringBuilder()
		.append(" select loan.id \"id\", ")
		.append("  loan.publish_date \"publishDate\" ")
		.append(" from  ")
		.append(" bao_t_auto_publish_info a, ")
		.append(" bao_t_loan_info loan  ")
		.append(" inner join BAO_T_LOAN_DETAIL_INFO d on d.loan_id = loan.id ")
		.append(" inner join bao_t_audit_info au on au.relate_primary = loan.id ")
		.append(" where ")
		.append(" loan.loan_status ='待发布'and loan.attachment_flag = '已完成' and loan.NEWER_FLAG != '新手标'")
		.append("   and au.apply_type='优选项目审核' and au.AUDIT_STATUS='通过'");
		
		if(!StringUtils.isEmpty(param.get("minTerm"))){
			SqlString.append(" and loan.LOAN_TERM*decode(loan.LOAN_UNIT, '天',1,'月',30,1) >= ? ");
			int minTerm = Integer.parseInt(param.get("minTerm").toString())*30;
//			// 大于12月，前台传minTerm=12；maxTerm=120，要特殊处理
//			int maxTerm = Integer.parseInt(param.get("maxTerm").toString());
//			if(maxTerm == 120) {
//				minTerm = 12*30 + 1;
//			}
			paramList.add(minTerm);
		}
		if(!StringUtils.isEmpty(param.get("maxTerm"))){
			SqlString.append(" and loan.LOAN_TERM*decode(loan.LOAN_UNIT, '天',1,'月',30,1) <= ? ");
			int maxTerm = Integer.parseInt(param.get("maxTerm").toString())*30;
//			// 小于1月，前台传minTerm=0；maxTerm=1，要特殊处理
//			int minTerm = Integer.parseInt(param.get("minTerm").toString());
//			if(minTerm == 0){
//				maxTerm = 29;
//			}
			paramList.add(maxTerm);
		}
		if(!StringUtils.isEmpty(param.get("minYearRate"))&&!StringUtils.isEmpty(param.get("maxYearRate"))){
			SqlString.append(" and d.year_irr  between ? and  ? ");
			paramList.add(param.get("minYearRate"));	
			paramList.add(param.get("maxYearRate"));	
		}
		if(!StringUtils.isEmpty(param.get("minRasieDays"))&&!StringUtils.isEmpty(param.get("maxRasieDays"))){
			SqlString.append(" and loan.rasie_days  between  ? and  ? ");
			paramList.add(param.get("minRasieDays"));	
			paramList.add(param.get("maxRasieDays"));	
		}
		if(!StringUtils.isEmpty(param.get("minLoanAmount"))&&!StringUtils.isEmpty(param.get("maxLoanAmount"))){
			SqlString.append(" and loan.loan_amount  between  ? and  ? ");
			paramList.add(param.get("minLoanAmount"));	
			paramList.add(param.get("maxLoanAmount"));	
		}
		if(!StringUtils.isEmpty(param.get("repaymentMethod"))){

			String repaymentMethodSplit = (String) param.get("repaymentMethod");
			String[] repaymentMethodList = repaymentMethodSplit.split(",");
			SqlString.append(" and ( ");
			for (int i = 0; i < repaymentMethodList.length; i++) {
				if (i == 0) {
					SqlString.append(" loan.repayment_method = ? ");
				} else {
					SqlString.append(" OR loan.repayment_method = ? ");
				}
				paramList.add(repaymentMethodList[i]);
			}
			SqlString.append(" ) ");
		}
		if(!StringUtils.isEmpty(param.get("debtSource"))){
			if(!"SLSW".equals(param.get("debtSource"))){
				SqlString.append(" and loan.cust_id = ? ");
				paramList.add(param.get("debtSource"));	
			}else if("SLSW".equals(param.get("debtSource"))){
				SqlString.append(" and loan.debt_source_code in ('SXSW','SXRZ') ");
//				paramList.add("");	
			}
		}
		SqlString.append(" order by au.audit_time asc,decode(loan.debt_source_code,'SXSW',1,'SXRZ',2,99)");
		
		List<Map<String, Object>> queryList = repositoryUtil.queryForMap(SqlString.toString(), paramList.toArray());
		return queryList;
	}

}
