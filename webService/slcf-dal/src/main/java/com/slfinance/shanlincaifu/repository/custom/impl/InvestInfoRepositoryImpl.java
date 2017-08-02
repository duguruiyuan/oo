package com.slfinance.shanlincaifu.repository.custom.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.slfinance.shanlincaifu.entity.InvestInfoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.InvestInfoRepositoryCustom;
import com.slfinance.shanlincaifu.utils.SqlCondition;
import com.slfinance.vo.ResultVo;

/**
 * 数据总览
 * @author zhangt
 *
 */
@Repository
public class InvestInfoRepositoryImpl implements InvestInfoRepositoryCustom{

	@Autowired
	private RepositoryUtil repositoryUtil;
	
	@Override
	public List<Map<String, Object>> findInvestBusinessHistory() {
		List<Object> listObject = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer()
		.append("select trunc(btidi.create_date) \"createDate\", btci.cust_source \"investSource\", count(btti.cust_id) \"investCount\", sum(btidi.invest_amount) \"investAmount\"")
		.append(" from bao_t_invest_info btti, bao_t_cust_info btci, bao_t_invest_detail_info btidi, bao_t_product_info btpi, bao_t_product_type_info btpti")
		.append(" where btti.cust_id = btci.id ")
		.append(" and btidi.invest_id = btti.id ")
		.append(" and btci.login_name not in ('居间人账户','公司收益账户','系统管理员','公司客服账户','风险金账户')")
		.append(" and (btci.is_recommend != '是' or btci.is_recommend is null)")
		.append(" and btci.invite_origin_id not in (select id from bao_t_cust_info btcii where btcii.is_recommend = '是' )")
		.append(" and btti.product_id = btpi.id and btpi.product_type = btpti.id and btpti.type_name != '体验宝' ")
		.append(" group by trunc(btidi.create_date), btci.cust_source order by trunc(btidi.create_date)");
		
		return repositoryUtil.queryForMap(sql.toString(), listObject.toArray());
	}

	@Override
	public Page<Map<String, Object>> queryProjectJoinPage(
			Map<String, Object> params) {
		List<Object> listObject = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer()		
		.append(" select b.create_date   \"createDate\", ")
		.append("        b.invest_amount \"investAmount\", ")
		.append("        substr(c.mobile, 0, 3) || '***' || substr(c.mobile, 8)    \"loginName\" ")
		.append("   from bao_t_invest_info a, bao_t_invest_detail_info b, bao_t_cust_info c ")
		.append("  where a.id = b.invest_id ")
		.append("    and a.cust_id = c.id ");
		
		if (!StringUtils.isEmpty((String)params.get("projectId"))) {
			String projectId = (String) params.get("projectId");
			sql.append(" and a.project_id = ? ");
			listObject.add(projectId);
		}
		
		sql.append(" order by b.create_date desc");
		
		return repositoryUtil.queryForPageMap(
				sql.toString(), 
				listObject.toArray(), 
				Integer.parseInt(params.get("start").toString()), 
				Integer.parseInt(params.get("length").toString()));
	}

	@Override
	public ResultVo queryProjectInvestInfo(Map<String, Object> params) {
		Map<String, Object> resultMap = Maps.newHashMap();
		StringBuilder sql = new StringBuilder()
		.append(" select b.create_date \"investDate\", b.invest_amount \"investAmount\", c.login_name \"loginName\", b.INVEST_SOURCE \"appSource\" ")
		.append("   from bao_t_invest_info a, bao_t_invest_detail_info b, bao_t_cust_info c ")
		.append("  where a.id = b.invest_id ")
		.append("    and a.cust_id = c.id %s ")
		.append("    order by b.create_date desc ");
		
		StringBuilder summaSql = new StringBuilder()
		.append(" select sum(b.invest_amount) \"totalInvestAmount\", count(1) \"investCount\", count(distinct(a.cust_id)) \"investPep\" ")
		.append("   from bao_t_invest_info a, bao_t_invest_detail_info b, bao_t_cust_info c ")
		.append("  where a.id = b.invest_id ")
		.append("    and a.cust_id = c.id %s ");
		
		StringBuilder sqlString = new StringBuilder();
		SqlCondition sqlCondition = new SqlCondition(sqlString, params);
		sqlCondition.addString("projectId", "a.project_id")
					.addString("custId", "a.cust_id")
					.addString("loginName", "c.login_name")
					.addBeginDate("beginInvestDate", "b.create_date")
					.addEndDate("endInvestDate", "b.create_date");
		
		Page<Map<String, Object>> resultPage = repositoryUtil.queryForPageMap(
				String.format(sql.toString(), sqlCondition.toString()), 
				sqlCondition.toArray(), 
				Integer.parseInt(params.get("start").toString()), 
				Integer.parseInt(params.get("length").toString()));
		
		List<Map<String, Object>> list = repositoryUtil.queryForMap(
				String.format(summaSql.toString(), sqlCondition.toString()),
				sqlCondition.toArray());
		if(list.size() > 0){
			resultMap.put("summaList", list.get(0));
		}
		resultMap.put("iTotalDisplayRecords", resultPage.getTotalElements());
		resultMap.put("data", resultPage.getContent());
		return new ResultVo(true, "投资列表查询成功", resultMap);
	}

	@Override
	public List<Map<String, Object>> findInvestRankingInfo(
			Map<String, Object> params) {
		List<Object> listObject = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder()
		.append(" select rank() over (order by invest_amount desc) ranking, btci.login_name \"loginName\", a.invest_amount \"investAmount\", substr(btci.mobile,1,3)||'****'||substr(btci.mobile,-4,4) \"mobile\" ")
		.append("   from (select btii.cust_id cust_id, ")
		.append("                nvl(sum(round(btii.invest_amount, 3)), 0) invest_amount ")
		.append("           from bao_t_invest_info btii ")
		.append("          where btii.project_id is not null or btii.wealth_id is not null ")
		.append("             OR btii.loan_id IS NOT null")
		.append("          group by btii.cust_id) a, ")
		.append("        bao_t_cust_info btci ")
		.append("  where a.cust_id = btci.id ")
		.append("  order by a.invest_amount desc ");
		//edit by 2016/04/05
		return repositoryUtil.queryForPageMap(sql.toString(), listObject.toArray(), Integer.parseInt(params.get("start").toString()), Integer.parseInt(params.get("length").toString())).getContent();
		//return repositoryUtil.queryForMap(sql.toString(), listObject.toArray());
	}

	/**
	 * 查询需要匹配债权的数据
	 */
	@Override
	public List<Map<String, Object>> findNeedMatchInvestInfo(Map<String, Object> params) {
		List<Object> listObject = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder()
		.append(" select t.id \"investId\", sa.id \"subAccountId\", sa.account_amount \"accountAmount\", t.cust_id \"custId\", c.credentials_code \"credentialsCode\", wea.year_rate \"yearRate\"  ")
		.append("    from bao_t_invest_info t ")
		.append(" inner join bao_t_wealth_info wea on wea.id = t.wealth_id ")
		.append("   inner join bao_t_sub_account_info sa on sa.relate_primary = t.id ")
		.append("   inner join bao_t_cust_info c on c.id = t.cust_id ")
		.append(" where sa.account_amount >= 10 and t.invest_status = '收益中' ")
		.append(" order by t.invest_amount desc  ");
		
		return repositoryUtil.queryForMap(sql.toString(), listObject.toArray());
	}

	@Override
	public List<Map<String, Object>> findAllCanAutoMatchInfo(Map<String, Object> params) {
		List<Object> listObject = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder()
		.append("  select loan.id \"loanId\", h.id \"holdId\", h.cust_id \"custId\",  ")
		.append("             decode(nvl(r.repayment_status, '未还款'), '未还款', val.value_repayment_before, val.value_repayment_after) * h.hold_scale \"holdAmount\",  ")
		.append("             decode(nvl(r.repayment_status, '未还款'), '未还款', val.value_repayment_before, val.value_repayment_after) \"loanValue\"  ")
		.append("        from bao_t_wealth_hold_info h   ")
		.append("       inner join bao_t_cust_info c on c.id = h.cust_id  ")
		.append("       inner join bao_t_loan_info loan on loan.id = h.loan_id  ")
		.append("       inner join bao_t_loan_detail_info d on d.loan_id = loan.id  ")
		.append("       inner join bao_t_loan_cust_info loanCust on loanCust.Id = loan.relate_primary  ")
		.append("       inner join bao_t_credit_right_value val on val.value_date = to_char(sysdate, 'yyyyMMdd') and val.loan_id = loan.id  ")
		.append("       left join bao_t_repayment_plan_info r on r.loan_id = loan.id and r.expect_repayment_date = to_char(sysdate, 'yyyyMMdd')    ")
		.append("      where h.is_center = ? ")
		.append("       and decode(nvl(r.repayment_status, '未还款'), '未还款', val.value_repayment_before, val.value_repayment_after) * h.hold_scale >= 50  ")
		.append("       and not exists (  ")
		.append("          select * from bao_t_invest_info invest  ")
		.append("             inner join bao_t_cust_info cust on cust.id = invest.cust_id  ")
		.append("             inner join bao_t_wealth_info wealth on wealth.id = invest.wealth_id  ")
		.append("          where ((c.credentials_code is not null and cust.credentials_code = c.credentials_code)   ")
		.append("               or loanCust.Credentials_Code = cust.credentials_code ")
		.append("               or d.year_irr <= wealth.year_rate ) ")
		.append("             and invest.id = ? ")
		.append("       )  ")
		.append("  and not exists ( ")
		.append("      select * from bao_t_wealth_hold_info hh ")
		.append("        inner join bao_t_invest_info ii on ii.id = hh.invest_id ")
		.append("      where ii.cust_id = (select a.cust_id from bao_t_invest_info a where a.id = ?) ")
		.append("        and loan.id = hh.loan_id ")
		.append("       ")
		.append("  ) ")
		.append("  order by d.year_irr asc, loan.id asc");
		
		listObject.add(params.get("isCenter"));
		listObject.add(params.get("investId"));
		listObject.add(params.get("investId"));
		return repositoryUtil.queryForMap(sql.toString(), listObject.toArray());
	}
	
	@Override
	public List<Map<String, Object>> queryInvestListByLoanId(String loanId) {
		List<Object> listObject = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer()		
		.append(" select a.invest_date          \"investDate\", ")
		.append("        a.invest_amount        \"investAmount\", ")
		.append("        c.cust_name     	    \"custName\", ")
		.append("        c.credentials_type     \"credentialsType\", ")
		.append("        c.credentials_code     \"idCard\", ")
		.append("        c.LOGIN_NAME           \"loginName\", ")
		.append("        l.loan_code           \"loanCode\", ")
		.append("        l.loan_term          \"investTerm\" ")
		.append("   from bao_t_invest_info a, bao_t_cust_info c ,bao_t_loan_info l ")
		.append("  where a.cust_id = c.id and a.loan_id = l.id ");
		
		if (!StringUtils.isEmpty(loanId)) {
			sql.append(" and a.loan_id = ? ");
			listObject.add(loanId);
		}
		
		sql.append(" order by a.create_date desc");
		
		return repositoryUtil.queryForMap(
				sql.toString(), 
				listObject.toArray());
	}

	@Override
	public Map<String, Object> queryMyTotalInvest(Map<String, Object> params) throws SLException {
		Map<String, Object> result = Maps.newHashMap();
		List<Object> listObject = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder()
				.append(" select nvl(sum(i.invest_amount), 0) \"amount\" from BAO_T_INVEST_INFO i where i.cust_id = ? and i.invest_mode != '转让'")
				.append(" union all ")
				.append(" select nvl(sum(i.invest_amount), 0) \"amount\" from BAO_T_INVEST_INFO i where i.cust_id = ? and i.invest_mode = '转让' ")
				.append(" union all ")
				.append(" select nvl(sum(ta.trade_amount), 0) \"amount\" from BAO_T_LOAN_TRANSFER_APPLY ta where ta.sender_cust_id = ? and ta.apply_status = '转让成功'");
		listObject.add(params.get("custId"));
		listObject.add(params.get("custId"));
		listObject.add(params.get("custId"));
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sql.toString(), listObject.toArray());
		result.put("totalDisperseAmount", list.get(0).get("amount"));
		result.put("totalBuyCreditAmount", list.get(1).get("amount"));
		result.put("totalSaleCreditAmount", list.get(2).get("amount"));
		return result;
	}

	/*
     *      <tt>transferTotalValue   :String:转让总价值</tt><br>
     *      <tt>transferTotalAmount  :String:转让总金额</tt><br>
     *      <tt>transferTotalInterest:String:转让总收益</tt><br>
     *      <tt>transferTotalExpense :String:转让总费用</tt><br> 
	 */
	@Override
	public Map<String, Object> queryMyCreditIncome(Map<String, Object> params) throws SLException {
		StringBuilder sqlString = new StringBuilder()
//		.append("   select nvl(sum(trunc(t.trade_amount, 2)), 0) \"transferTotalAmount\",  ")
//		.append("          nvl(sum(trunc(t.trade_value, 2)), 0) \"transferTotalValue\",  ")
//		.append("          nvl(sum(trunc(t.manage_amount, 2)), 0) \"transferTotalExpense\",  ")
//		.append("          nvl(sum(trunc(t.trade_amount - t.trade_value, 2)), 0) \"transferTotalInterest\" ")
	
		.append("   select nvl(sum(case when t.create_date < to_date(?, 'yyyy-MM-dd HH24:mi:ss') then trunc(t.trade_amount, 2) else trunc(t.TRADE_PRINCIPAL * t.REDUCED_SCALE, 2) end), 0) \"transferTotalAmount\",  ")
		.append("          nvl(sum(case when t.create_date < to_date(?, 'yyyy-MM-dd HH24:mi:ss') then trunc(t.trade_value, 2) else t.TRADE_PRINCIPAL end), 0) \"transferTotalValue\",  ")
		.append("          nvl(sum(trunc(t.manage_amount, 2)), 0) \"transferTotalExpense\",  ")
		.append("          nvl(sum(case when t.create_date < to_date(?, 'yyyy-MM-dd HH24:mi:ss') then TRUNC(t.trade_amount - t.trade_value, 2) else trunc(t.TRADE_PRINCIPAL * t.REDUCED_SCALE, 2) - trunc(t.TRADE_PRINCIPAL, 2) end), 0) \"transferTotalInterest\" ")
		.append("  from bao_t_loan_transfer_apply t ")
		.append("   inner join bao_t_wealth_hold_info s on s.id = t.sender_hold_id ")
		.append("   inner join bao_t_loan_info r on r.id = s.loan_id ")
		.append("   where t.remainder_trade_scale < 1  ");
		
		List<Object> listObject = new ArrayList<Object>();
		listObject.add((String)params.get("onlineTime"));
		listObject.add((String)params.get("onlineTime"));
		listObject.add((String)params.get("onlineTime"));
		
		SqlCondition sql = new SqlCondition(sqlString, params, listObject);
		sql.addString("custId", "t.sender_cust_id");
		
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sql.toString(), sql.toArray());
		return list.get(0);
	}

	@Override
	public List<InvestInfoEntity> queryCustIdInInvestTable(String loanId, String custId) {
		StringBuilder sql = new StringBuilder();
		sql.append("select * from BAO_T_INVEST_INFO t where t.LOAN_ID = ? and t.CUST_ID = ? ");
		List<InvestInfoEntity> list = repositoryUtil.queryForList(sql.toString(), new Object[]{loanId, custId}, InvestInfoEntity.class);
		return list;
	}

}
