package com.slfinance.shanlincaifu.repository.custom.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.LoanServicePlanInfoRepositoryCustom;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.SqlCondition;

@Service
public class LoanServicePlanInfoRepositoryImpl implements
		LoanServicePlanInfoRepositoryCustom {
	
	@Autowired
	private RepositoryUtil repositoryUtil;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public Page<Map<String, Object>> queryLoanExpenseList(
			Map<String, Object> params) {
		SqlCondition sqlCon = new SqlCondition(new StringBuilder(), params)
		.append(" select \"loanServicePlanId\" ,\"loanId\" ,\"companyName\" ,\"loanTitle\" ,\"manageRate\" , ")
		.append(" \"manageExpenseDealType\",\"exceptAmount\" ,\"exceptDate\",\"payStatus\",\"custId\"  ")
		.append(" from ")
		.append(" ( ")
		.append(" SELECT lspi.ID \"loanServicePlanId\"  ")
		.append("      , lspi.LOAN_ID \"loanId\"  ")
		.append("      , loan.cust_id  \"custId\"  ")
		.append("      , cust.login_name \"companyName\"  ")
		.append("      , loan.LOAN_TITLE \"loanTitle\"  ")
		.append("      , loan.MANAGE_RATE*100  \"manageRate\"  ")
		.append("      , nvl(loan.manage_expense_deal_type,'线下') \"manageExpenseDealType\" ")
		.append("      , lspi.EXCEPT_AMOUNT \"exceptAmount\"  ")
		.append("      , to_date(lspi.EXCEPT_DATE, 'yyyyMMdd') \"exceptDate\"  ")
		.append("      , lspi.PAYMENT_STATUS \"payStatus\"  ")
		.append("       FROM BAO_T_LOAN_SERVICE_PLAN_INFO lspi  ")
		.append("      , BAO_T_LOAN_INFO loan  ")
		.append("      , BAO_T_CUST_INFO cust  ")
		.append("      WHERE lspi.LOAN_ID = loan.ID  ")
		.append("        AND cust.ID = loan.CUST_ID  ")
		.append("        AND loan.LOAN_STATUS IN ('已到期', '正常', '一次性结清')  ")
		.append("        AND loan.manage_expense_deal_type = '线下'       ")
		.append(" union all ")
		.append(" SELECT '' \"loanServicePlanId\"  ")
		.append("      , loan.ID \"loanId\"  ")
		.append("      , loan.cust_id  \"custId\"  ")
		.append("      , cust.login_name \"companyName\"  ")
		.append("      , loan.LOAN_TITLE \"loanTitle\"  ")
		.append("      , loan.MANAGE_RATE*100  \"manageRate\"  ")
		.append("      , nvl(loan.manage_expense_deal_type,'线下') \"manageExpenseDealType\" ")
		.append("      ,  loan.plat_service_amount  \"exceptAmount\"  ")
		.append("      ,loan.invest_start_date    \"exceptDate\"  ")
		.append("     ,'已收'   \"payStatus\" ")
		.append("      from BAO_T_LOAN_INFO loan  ")
		.append("      left join  BAO_T_CUST_INFO cust on cust.ID = loan.CUST_ID  ")
		.append("      where  loan.LOAN_STATUS IN ('已到期', '正常', '一次性结清')  ")
		.append("        AND loan.manage_expense_deal_type = '期初'       ")
		.append(" union all ")
		.append(" SELECT '' \"loanServicePlanId\"  ")
		.append("      , loan.ID \"loanId\"  ")
		.append("      , loan.cust_id  \"custId\"  ")
		.append("      , cust.login_name \"companyName\"  ")
		.append("      , loan.LOAN_TITLE \"loanTitle\"  ")
		.append("      , loan.MANAGE_RATE*100  \"manageRate\"  ")
		.append("      , nvl(loan.manage_expense_deal_type,'线下') \"manageExpenseDealType\" ")
		.append("      , loan.monthly_manage_amount  \"exceptAmount\" ")
		.append("      , to_date(ren.expect_repayment_date , 'yyyyMMdd')   \"exceptDate\"  ")
		.append("      , decode(ren.repayment_status,'已还款','已收','未还款','待收')    \"payStatus\" ")
		.append("      from BAO_T_LOAN_INFO loan  ")
		.append("      left join  BAO_T_CUST_INFO cust on cust.ID = loan.CUST_ID  ")
		.append("      left join  BAO_T_REPAYMENT_PLAN_INFO  ren on loan.id = ren.loan_id ")
		.append("      where  loan.LOAN_STATUS IN ('已到期', '正常', '一次性结清')         ")
		.append("        AND loan.manage_expense_deal_type = '期末'       ")
		.append("      )a where 1=1 ")
		.addString("payStatus", "a.\"payStatus\"")
		.addString("loanTitle", "a.\"loanTitle\"")
		.addString("custId",    "a.\"custId\"");
		
		List<Object> args = sqlCon.getObjectList();
		if(!StringUtils.isEmpty(params.get("manageExpenseDealType"))){
			if(Constant.MANAGE_EXPENSE_DEAL_TYPE_01.equals(params.get("manageExpenseDealType"))){
				sqlCon.append(" and (a.\"manageExpenseDealType\" = ? or a.\"manageExpenseDealType\" is null)");
			}else {
				sqlCon.append(" and a.\"manageExpenseDealType\" = ? ");
			}
			args.add(params.get("manageExpenseDealType"));
		}
		sqlCon.append("       ORDER BY decode(a.\"payStatus\", '待收', 1, 99), a.\"exceptDate\"  ");
		return repositoryUtil.queryForPageMap(
				sqlCon.toString(), 
				sqlCon.toArray(), 
				Integer.valueOf(params.get("start").toString()),
				Integer.valueOf(params.get("length").toString()));
	}
	
	
}
