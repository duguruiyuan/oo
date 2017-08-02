package com.slfinance.shanlincaifu.repository.custom.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.SendRepaymentCustom;

@Repository
public class SendRepaymentCustomImpl implements SendRepaymentCustom {

	@Autowired
	private RepositoryUtil repositoryUtil;

	@Override
	public List<Map<String, Object>> querySendMessageList() {
		StringBuilder sql = new StringBuilder()
		.append(" select s.loan_code \"loanCode\", s.loan_type \"loanType\", t.expect_repayment_date \"expectRepaymentDate\", t.current_term \"currentTerm\" ")
		.append(" from bao_t_repayment_plan_info t,  bao_t_loan_info s ")
		.append(" where t.loan_id = s.id and t.expect_repayment_date <= to_char(sysdate, 'yyyymmdd')  ")
		.append(" and s.loan_status = '正常' ")
		.append(" and t.repayment_status = '未还款' ")
		.append(" AND s.DEBT_SOURCE_CODE IN ('SXSW', 'SXRZ') ")
		;
		
		return repositoryUtil.queryForMap(sql.toString(), null);
	}
}
