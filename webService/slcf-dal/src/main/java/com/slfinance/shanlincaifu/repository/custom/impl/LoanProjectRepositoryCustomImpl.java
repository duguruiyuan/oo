package com.slfinance.shanlincaifu.repository.custom.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.LoanProjectRepositoryCustom;
import com.slfinance.shanlincaifu.utils.Constant;

@Repository
public class LoanProjectRepositoryCustomImpl implements
		LoanProjectRepositoryCustom {
	
	@Autowired
	private RepositoryUtil repositoryUtil;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public List<Map<String, Object>> findWaitingUnReleaseLoan(
			Map<String, Object> params) {
		
		StringBuilder sql = new StringBuilder()
		.append("   select t.id \"id\", LOAN_DESC \"loanDesc\", LOAN_CODE  \"loanCode\" ")
		.append("   from bao_t_loan_info t  ")
		.append("   where t.loan_status = ? ")
		.append("   and t.rasie_end_date <= ? ");
		
		return repositoryUtil.queryForMap(sql.toString(), new Object[]{params.get("loanStatus"), params.get("rasieEndDate")});
	}

	@Override
	public List<Map<String, Object>> findWaitingGrantConfirmList(
			Map<String, Object> params) {
		
		StringBuilder sql = new StringBuilder()
		.append("  select t.id \"projectId\", t.loan_desc \"projectName\", t.loan_code \"projectNo\",   ")
		.append("                decode(s.trade_status, '处理成功', '放款成功', '放款失败') \"grantStatus\"  ")
		.append("             from bao_t_loan_info t, bao_t_audit_info a, bao_t_trade_flow_info s   ")
		.append("             where t.id = a.relate_primary and a.id = s.relate_primary    ")
		.append("             and t.grant_status = '放款中'  ")
		.append("             and t.loan_status = '满标复核'    ")
		.append("   		  and t.company_name != '意真金融' ")
		.append("   		  and t.company_name != '巨涟金融' ")
		.append("             and a.audit_status = '通过'  ")
		.append("             and (s.trade_status = '处理成功' or s.trade_status = '处理失败')  ")
		.append("             and s.trade_desc = '代付'  ")
		.append("             and s.last_update_date = (  ")
		.append("                 select max(y.last_update_date)  ")
		.append("                 from bao_t_audit_info x, bao_t_trade_flow_info y  ")
		.append("                 where x.id = y.relate_primary  ")
		.append(" 		            and x.audit_status = '通过'  ")
		.append(" 		            and y.trade_desc = '代付'  ")
		.append(" 		            and x.relate_primary = t.id   ")
		.append(" 		        ) ")
		.append(" 		      and t.grant_type = ?  "); // 放款到帐生效

		return repositoryUtil.queryForMap(sql.toString(), new Object[]{Constant.GRANT_TYPE_02});
	}
	
	@Override
	public List<Map<String, Object>> findWaitingGrantConfirm4CompanyList(
			Map<String, Object> params) {
		
		StringBuilder sql = new StringBuilder()
		.append("  select t.id \"projectId\", t.loan_desc \"projectName\", t.loan_code \"projectNo\",   ")
		.append("                decode(s.trade_status, '处理成功', '放款成功', '放款失败') \"grantStatus\"  ")
		.append("             from bao_t_loan_info t, bao_t_audit_info a, bao_t_trade_flow_info s   ")
		.append("             where t.id = a.relate_primary and a.id = s.relate_primary    ")
		.append("             and t.grant_status = '放款中'  ")
		.append("             and t.loan_status = '满标复核'    ")
		.append("   		  and t.company_name = ? ")
		.append("             and a.audit_status = '通过'  ")
		.append("             and (s.trade_status = '处理成功' or s.trade_status = '处理失败')  ")
		.append("             and s.trade_desc = '代付'  ")
		.append("             and s.last_update_date = (  ")
		.append("                 select max(y.last_update_date)  ")
		.append("                 from bao_t_audit_info x, bao_t_trade_flow_info y  ")
		.append("                 where x.id = y.relate_primary  ")
		.append(" 		            and x.audit_status = '通过'  ")
		.append(" 		            and y.trade_desc = '代付'  ")
		.append(" 		            and x.relate_primary = t.id   ")
		.append(" 		        ) ")
		.append(" 		      and t.grant_type = ?  "); // 放款到帐生效

		return repositoryUtil.queryForMap(sql.toString(), new Object[]{params.get("companyName"), Constant.GRANT_TYPE_02});
	}
	
	@Override
	public List<Map<String, Object>> findWaitingGrantConfirmListYZ(
			Map<String, Object> params) {
		
		StringBuilder sql = new StringBuilder()
		.append("  select t.id \"projectId\", t.loan_desc \"projectName\", t.loan_code \"projectNo\",   ")
		.append("                decode(s.trade_status, '处理成功', '放款成功', '放款失败') \"grantStatus\"  ")
		.append("             from bao_t_loan_info t, bao_t_audit_info a, bao_t_trade_flow_info s   ")
		.append("             where t.id = a.relate_primary and a.id = s.relate_primary    ")
		.append("             and t.grant_status = '放款中'  ")
		.append("             and t.loan_status = '满标复核'    ")
		.append("   		  and t.company_name = '意真金融' ")
		.append("             and a.audit_status = '通过'  ")
		.append("             and (s.trade_status = '处理成功' or s.trade_status = '处理失败')  ")
		.append("             and s.trade_desc = '代付'  ")
		.append("             and s.last_update_date = (  ")
		.append("                 select max(y.last_update_date)  ")
		.append("                 from bao_t_audit_info x, bao_t_trade_flow_info y  ")
		.append("                 where x.id = y.relate_primary  ")
		.append(" 		            and x.audit_status = '通过'  ")
		.append(" 		            and y.trade_desc = '代付'  ")
		.append(" 		            and x.relate_primary = t.id   ")
		.append(" 		        ) ")
		.append(" 		      and t.grant_type = ?  "); // 放款到帐生效

		return repositoryUtil.queryForMap(sql.toString(), new Object[]{Constant.GRANT_TYPE_02});
	}
	
	@Override
	public List<Map<String, Object>> findWaitingGrantConfirmListJL(
			Map<String, Object> params) {
		
		StringBuilder sql = new StringBuilder()
		.append("  select t.id \"projectId\", t.loan_desc \"projectName\", t.loan_code \"projectNo\",   ")
		.append("                decode(s.trade_status, '处理成功', '放款成功', '放款失败') \"grantStatus\"  ")
		.append("             from bao_t_loan_info t, bao_t_audit_info a, bao_t_trade_flow_info s   ")
		.append("             where t.id = a.relate_primary and a.id = s.relate_primary    ")
		.append("             and t.grant_status = '放款中'  ")
		.append("             and t.loan_status = '满标复核'    ")
		.append("   		  and t.company_name = '巨涟金融' ")
		.append("             and a.audit_status = '通过'  ")
		.append("             and (s.trade_status = '处理成功' or s.trade_status = '处理失败')  ")
		.append("             and s.trade_desc = '代付'  ")
		.append("             and s.last_update_date = (  ")
		.append("                 select max(y.last_update_date)  ")
		.append("                 from bao_t_audit_info x, bao_t_trade_flow_info y  ")
		.append("                 where x.id = y.relate_primary  ")
		.append(" 		            and x.audit_status = '通过'  ")
		.append(" 		            and y.trade_desc = '代付'  ")
		.append(" 		            and x.relate_primary = t.id   ")
		.append(" 		        ) ")
		.append(" 		      and t.grant_type = ?  "); // 放款到帐生效

		return repositoryUtil.queryForMap(sql.toString(), new Object[]{Constant.GRANT_TYPE_02});
	}

	@Override
	public List<Map<String, Object>> findWaitingGrantList(
			Map<String, Object> params) {
		
		StringBuilder sql = new StringBuilder()
		.append("   select t.id \"loanId\", t.loan_type \"loanType\", t.loan_status \"loanStatus\", ")
		.append("          t.loan_code \"loanCode\", t.grant_status \"grantStatus\" ")
		.append("   from bao_t_loan_info t  ")
		.append("   where t.loan_status = '满标复核' ")
		.append("   and t.grant_status = '待放款' ")
		.append("   and t.company_name != '意真金融' ")
				.append("   and t.company_name != '巨涟金融' ")
		// 剔除雪橙金服的满标复核数据
		.append("   and t.id not in (select id  from bao_t_loan_info where loan_status = '满标复核' and company_name ='" + Constant.DEBT_SOURCE_XCJF + "') ");
		
		return repositoryUtil.queryForMap(sql.toString(), new Object[]{});
	}
	
	@Override
	public List<Map<String, Object>> findWaitingGrant4CompanyList(
			Map<String, Object> params) {
		
		StringBuilder sql = new StringBuilder()
		.append("   select t.id \"loanId\", t.loan_type \"loanType\", t.loan_status \"loanStatus\", ")
		.append("          t.loan_code \"loanCode\", t.grant_status \"grantStatus\" ")
		.append("   from bao_t_loan_info t  ")
		.append("   where t.loan_status = '满标复核' ")
		.append("   and t.grant_status = '待放款' ")
		.append("   and t.company_name = ? ");
		return repositoryUtil.queryForMap(sql.toString(), new Object[]{params.get("companyName")});
	}
	
	@Override
	public List<Map<String, Object>> findWaitingGrantYZList(
			Map<String, Object> params) {
		
		StringBuilder sql = new StringBuilder()
		.append("   select t.id \"loanId\", t.loan_type \"loanType\", t.loan_status \"loanStatus\", ")
		.append("          t.loan_code \"loanCode\", t.grant_status \"grantStatus\" ")
		.append("   from bao_t_loan_info t  ")
		.append("   where t.loan_status = '满标复核' ")
		.append("   and t.grant_status = '待放款' ")
		.append("   and t.company_name = '意真金融' ");
		return repositoryUtil.queryForMap(sql.toString(), new Object[]{});
	}
	
	/*
	 * 查询意真巨涟待放款标的
	 * (non-Javadoc)
	 * @see com.slfinance.shanlincaifu.repository.custom.LoanProjectRepositoryCustom#findWaitingGrantJLList(java.util.Map)
	 */
	@Override
	public List<Map<String, Object>> findWaitingGrantJLList(
			Map<String, Object> params) {
		
		StringBuilder sql = new StringBuilder()
		.append("   select t.id \"loanId\", t.loan_type \"loanType\", t.loan_status \"loanStatus\", ")
		.append("          t.loan_code \"loanCode\", t.grant_status \"grantStatus\" ")
		.append("   from bao_t_loan_info t  ")
		.append("   where t.loan_status = '满标复核' ")
		.append("   and t.grant_status = '待放款' ")
		.append("   and t.company_name = '巨涟金融' ");
		return repositoryUtil.queryForMap(sql.toString(), new Object[]{});
	}

	@Override
	public List<Map<String, Object>> findWaitingCancelList(
			Map<String, Object> params) {
		
		StringBuilder sql = new StringBuilder()
// SLCF-3181 每期还款复投和复投后不可转让需求（债权转让调整） 转让标的剩余金额需募集期结束再退回。
//		.append("   select a.id \"id\" ")
//		.append("   from bao_t_loan_transfer_apply a ")
//		.append("   inner join bao_t_wealth_hold_info h on h.id = a.sender_hold_id ")
//		.append("   inner join bao_t_credit_right_value c on c.loan_id = h.loan_id and c.value_date = to_char(?, 'yyyyMMdd') ")
//		.append("   left  join bao_t_repayment_plan_info r on r.loan_id = h.loan_id and r.expect_repayment_date = to_char(?, 'yyyyMMdd') ")
//		.append("   where a.cancel_status = '未撤销' and a.apply_status in ('待转让','部分转让成功') and a.audit_status='通过' ")
//		.append("   and a.remainder_trade_scale*decode(nvl(r.repayment_status, '未还款'),'未还款',c.value_repayment_before,c.value_repayment_after) < 1000 ")
//		.append("   union ")
		.append("   select a.id \"id\" ")
		.append("   from bao_t_loan_transfer_apply a ")
		.append("   inner join bao_t_wealth_hold_info h on h.id = a.sender_hold_id ")
		.append("   inner join bao_t_loan_info l on l.id = h.loan_id ")
		.append("   where a.cancel_status = '未撤销' and a.apply_status in ('待转让','部分转让成功') and a.audit_status='通过' ")
		.append("   and l.loan_status = '逾期' ")
		.append("   union ")
		.append("   select a.id \"id\" ")
		.append("   from bao_t_loan_transfer_apply a ")
		.append("   where a.cancel_status = '未撤销' and a.apply_status in ('待转让','部分转让成功') and a.audit_status='通过' ")
		.append("   and a.transfer_end_date < ? ");
		
		return repositoryUtil.queryForMap(sql.toString(), new Object[]{ /*params.get("currentDate"), params.get("currentDate"), */params.get("currentDate")});
	}

	@Override
	public List<Map<String, Object>> findWaitingPublishLoan(
			Map<String, Object> params) {
		StringBuilder sql = new StringBuilder()
		.append("   select t.id \"id\", LOAN_DESC \"loanDesc\", LOAN_CODE  \"loanCode\" ")
		.append("   from bao_t_loan_info t  ")
		.append("   where t.loan_status = ? ")
		.append("   and t.publish_date <= ? ");
		
		return repositoryUtil.queryForMap(sql.toString(), new Object[]{params.get("loanStatus"), params.get("publishDate")});
	}

}
