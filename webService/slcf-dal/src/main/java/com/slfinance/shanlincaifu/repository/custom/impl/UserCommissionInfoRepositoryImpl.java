package com.slfinance.shanlincaifu.repository.custom.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.UserCommissionInfoRepositoryCustom;
import com.slfinance.shanlincaifu.utils.DateUtils;

public class UserCommissionInfoRepositoryImpl implements
		UserCommissionInfoRepositoryCustom {

	@Autowired
	private RepositoryUtil repositoryUtil;
	
	@Override
	public List<Map<String, Object>> queryWaitingCommissionList(
			Map<String, Object> params) {
		
		StringBuilder sql = new StringBuilder()
		.append("   select t.id \"investId\", decode(nvl(n.is_employee, '###'), '是', t.cust_id, m.cust_id) \"custId\" ")
        // 2017-3-25 09:23:55 查询结果增加LOAN_UNIT字段
		.append("   , l.LOAN_UNIT \"loanUnit\"")
        .append("   from bao_t_invest_info t ")
        .append("   inner join bao_t_cust_info n on n.id = t.cust_id ")
		.append("   inner join bao_t_loan_info l on l.id = t.loan_id ")
		.append("   left join bao_t_cust_recommend_info m on m.quilt_cust_id = t.cust_id and m.record_status = '有效' ")
		.append("   where (n.is_employee = '是' or (n.is_employee is null and m.cust_id is not null)) ")
		.append("   and l.loan_status in ('正常', '逾期', '提前结清', '已到期') ")
		.append("   and not exists ( ")
		.append("         select 1 ")
		.append("         from bao_t_user_commission_info s ")
		.append("         where s.invest_id = t.id ")
		.append("   ) ")
		.append("   and t.effect_date >= ? and t.effect_date < ? ")
		.append("   order by t.create_date ");

		return repositoryUtil.queryForMap(sql.toString(), new Object[]{
			params.get("commonMonth") + "01",
			DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(params.get("commonMonth") + "01", "yyyyMMdd"), 1), "yyyyMMdd"),
		});
	}

	@Override
	public List<Map<String, Object>> queryWaitingTransferOutCommissionList(
			Map<String, Object> params) {
		
		StringBuilder sql = new StringBuilder()
		.append(" select t.id  \"investId\" ")
		.append("   from bao_t_invest_info         t, ")
		.append("        bao_t_loan_transfer_apply s, ")
		.append("        bao_t_wealth_hold_info    h ")
		.append("  where t.memo is null ")
		.append("    and t.transfer_apply_id = s.id ")
		.append("    and s.sender_hold_id = h.id ")
		.append("    and h.invest_id in (select u.invest_id ")
		.append("                          from bao_t_user_commission_info u ")
		.append("                         where u.payment_status = '未结算') ")
		.append("    and t.cust_id not in (select m.quilt_cust_id ")
		.append("                            from bao_t_cust_recommend_info m ")
		.append("                           where m.record_status = '有效') ");

		return repositoryUtil.queryForMap(sql.toString(), new Object[]{});
	}

}
