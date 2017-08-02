package com.slfinance.shanlincaifu.repository.custom.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Maps;
import com.slfinance.shanlincaifu.entity.ReserveInvestInfoEntity;
import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.LoanReserveRepositoryCustom;
import com.slfinance.shanlincaifu.utils.ArithUtil;

@Repository
public class LoanReserveRepositoryImpl implements LoanReserveRepositoryCustom{
	@Autowired
	RepositoryUtil repositoryUtil;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;


	public Page<Map<String, Object>> queryMyReserveList(
			Map<String, Object> param) {
		List<Object> args = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder()
		.append("  select t.cust_id \"custId\", ")
		.append("                sum(t.reserve_amount) \"reserveAmount\", ")
		.append("                max(t.reserve_date) \"reserveDate\", ")
		.append("                sum(t.remainder_amount) \"remainderAmount\", ")
		.append("                sum(t.already_invest_amount) \"alreadyInvestAmount\", ")
		.append("                t.reserve_status \"reserveStatus\", ")
		.append("                decode(t.Reserve_Status, '排队中', '撤销', '不可撤销') \"operate\" ")
		.append("           from BAO_T_RESERVE_INVEST_INFO t ")
		.append("          where t.cust_id = ? ")
		.append("          group by t.reserve_status, t.cust_id,t.batch_no ");
		args.add(param.get("custId"));
//		SqlCondition sqlCondition = new SqlCondition(sql, param, args)
//		.addString("custId", "\"custId\"");
		sql.append("order by decode(\"reserveStatus\", '排队中', 1,'预约成功',2,'待出借金额超时退回',3,'待出借金额已撤销',4) ,\"reserveDate\" desc ") ;
		return repositoryUtil.queryForPageMap(sql.toString(), args.toArray(), Integer.parseInt(param.get("start").toString()), Integer.parseInt(param.get("length").toString()));
	}

	public Map<String, Object> queryMyReserveIncome(
			Map<String, Object> param) {
		StringBuilder sql = new StringBuilder()
		.append(" select TRUNC_AMOUNT_WEB(sum(rd.trade_amount)) \"amount\" ")//0  已赚金额
		.append("   from bao_t_account_flow_info     f, ")
		.append("        bao_t_payment_record_info   pr, ")
		.append("        bao_t_payment_record_detail rd, ")
		.append("        bao_t_invest_info           i ")
//		.append("        , BAO_T_RESERVE_INVEST_INFO   r ")
		.append("  where f.relate_primary = i.id ")
		.append("    and f.id = pr.relate_primary ")
		.append("    and pr.id = rd.pay_record_id ")
//		.append("    and i.product_id = r.id ")
//		.append("    and i.cust_id = r.cust_id ")
		.append("    and rd.subject_type in ('还风险金逾期费用', '利息', '逾期费用', '违约金') ")
		.append("    and i.loan_id is not null ")
		.append("    AND i.PRODUCT_ID IS NOT null ")
		.append("    and i.cust_id = ? ")
		.append(" union all ")
		.append(" select TRUNC_AMOUNT_WEB(sum(trunc(r.repayment_interest * h.hold_scale,2))) \"amount\" ")//1  待收收益
		.append("   from bao_t_invest_info i ")
		.append("  inner join bao_t_wealth_hold_info h ")
		.append("     on h.invest_id = i.id ")
		.append("    and h.loan_id = i.loan_id ")
		.append("  inner join bao_t_loan_info l ")
		.append("     on l.id = h.loan_id ")
		.append("  inner join bao_t_repayment_plan_info r ")
		.append("     on r.loan_id = l.id ")
//		.append("  inner join BAO_T_RESERVE_INVEST_INFO r ")
//		.append("     on i.product_id = r.id ")
//		.append("    and i.cust_id = r.cust_id ")
		.append("  where r.repayment_status = '未还款' ")
		.append("    and i.invest_status = '收益中' ")
		.append("    and i.PRODUCT_ID is not null ")
		.append("    and i.cust_id = ? ")
		.append(" union all ")
		.append("   select TRUNC_AMOUNT_WEB(sum(r.repayment_principal*h.hold_scale)) \"amount\" ") // 2 
		.append("   from bao_t_invest_info i ")
		.append("   inner join bao_t_wealth_hold_info h on h.invest_id = i.id and h.loan_id = i.loan_id ")
		.append("   inner join bao_t_loan_info l on l.id = h.loan_id ")
		.append("   inner join bao_t_repayment_plan_info r on r.loan_id = l.id  ")
		.append("   where r.repayment_status = '未还款' ")
		.append("     and i.invest_status = '收益中' ")
		.append("     and i.product_id is not null ")
		.append("     and i.cust_id = ? ")
		.append("   union all ")
		.append("    select TRUNC_AMOUNT_WEB(sum(i.invest_amount)) \"amount\" ") // 3
		.append("   from bao_t_invest_info i  ")
		.append("   inner join bao_t_loan_info l on l.id = i.loan_id ")
		.append("   where i.invest_status = '投资中' ")
		.append("     and i.product_id is not null ")
		.append("     and i.cust_id = ? ")
		.append(" union all ")
		.append(" select nvl(sum(t.reserve_amount),0) \"amount\" ")//4  预约总金额
		.append("   from BAO_T_RESERVE_INVEST_INFO t ")
		.append("  where t.cust_id = ? ");
		List<Object> listObject = new ArrayList<Object>();
		listObject.add(param.get("custId"));
		listObject.add(param.get("custId"));
		listObject.add(param.get("custId"));
		listObject.add(param.get("custId"));
		listObject.add(param.get("custId"));
		Map<String, Object> result = Maps.newHashMap();
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sql.toString(), listObject.toArray());
		result.put("earnTotalAmount", list.get(0).get("amount"));//
		result.put("exceptTotalAmount", list.get(1).get("amount"));
		result.put("investTotalAmount", ArithUtil.add(new BigDecimal(list.get(2).get("amount").toString()), new BigDecimal(list.get(3).get("amount").toString())));
		result.put("reserveTotalAmount", list.get(4).get("amount"));
		return result;
	}

	public List<ReserveInvestInfoEntity> getReserveCancelByReserveStatusAndReserveEndDate(
			Map<String, Object> param) {
		StringBuilder sql=new StringBuilder()
		.append(" select \"custId\" ")
		.append("   from (select t.cust_id \"custId\", max(t.reserve_end_date) \"reserveEndDate\" ")
		.append("           from BAO_T_RESERVE_INVEST_INFO t ")
		.append("          where t.reserve_status = '排队中' ")
		.append("          group by t.reserve_status, t.cust_id ")
		.append("          order by max(t.reserve_end_date)) ")
		.append("  where \"reserveEndDate\" < ")
		.append("        sysdate ");
		
		return  repositoryUtil.queryForList(sql.toString(), ReserveInvestInfoEntity.class);
	}
}
