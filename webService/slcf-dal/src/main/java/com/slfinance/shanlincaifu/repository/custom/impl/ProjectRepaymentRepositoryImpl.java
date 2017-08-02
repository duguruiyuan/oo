/** 
 * @(#)ProjectRepaymentRepositoryImpl.java 1.0.0 2016年1月6日 下午7:16:18  
 *  
 * Copyright © 2016 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository.custom.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.ProjectRepaymentRepositoryCustom;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.SqlCondition;

/**   
 * 自定义项目还款数据访问实现类
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2016年1月6日 下午7:16:18 $ 
 */
@Repository
public class ProjectRepaymentRepositoryImpl implements
		ProjectRepaymentRepositoryCustom {

	@Autowired
	private RepositoryUtil repositoryUtil;
	
	@Override
	public Page<Map<String, Object>> queryLatestRepaymentList(
			Map<String, Object> params) {
		StringBuilder sqlString = new StringBuilder()	
		.append("  select s.id \"replaymentPlanId\", t.id \"projectId\", m.id \"subAccountId\", ")
		.append("        t.project_type \"projectType\", t.project_no \"projectNo\",  ")
		.append("        t.company_name \"companyName\", t.type_term \"typeTerm\", s.current_term \"currentTerm\", ")
		.append("        s.repayment_total_amount \"repaymentTotalAmount\", s.term_already_repay_amount \"termAlreadyRepayAmount\", ")
		.append("        m.account_freeze_value \"freezeRepayAmount\", to_date(s.expect_repayment_date, 'yyyyMMdd') \"exceptDate\", ")
		.append("        s.is_amount_frozen \"isAmountFrozen\" ")
		.append("  from bao_t_project_info t, bao_t_repayment_plan_info s, bao_t_sub_account_info m ")
		.append("  where t.id = s.PROJECT_ID and m.relate_primary = t.id ")
		.append("  and t.project_status = ?  ")
		.append("  and s.repayment_status = ? ");
		
		List<Object> objList = Lists.newArrayList();
		objList.add(Constant.PROJECT_STATUS_08); // 还款中
		objList.add(Constant.REPAYMENT_STATUS_WAIT); // 未还款
		if(StringUtils.isEmpty((String)params.get("beginExceptDate"))
				&& StringUtils.isEmpty((String)params.get("endExceptDate"))) {
			sqlString.append("  and s.expect_repayment_date <= to_char(?, 'yyyyMMdd') ");
			objList.add(DateUtils.getAfterDay(new Date(), 7)); // 近7天
		}
		SqlCondition sqlCondition = new SqlCondition(sqlString, params, objList);
		sqlCondition.addString("projectType", "t.project_type")
		            .addString("companyName", "t.company_name") 
		            .addString("projectNo", "t.project_no")			            
		            .addBeginDate("beginExceptDate", "to_date(s.expect_repayment_date, 'yyyyMMdd')")
		            .addEndDate("endExceptDate", "to_date(s.expect_repayment_date, 'yyyyMMdd')")
		            .addSql( " order by expect_repayment_date, project_no ");
				
		return repositoryUtil.queryForPageMap(sqlCondition.toString(), sqlCondition.toArray(), Integer.valueOf(params.get("start").toString()),
				Integer.valueOf(params.get("length").toString()));
	}

	@Override
	public Map<String, Object> queryLatesttRepaymentTotal(
			Map<String, Object> params) {
		
		Map<String, Object> result = Maps.newHashMap();
		
		StringBuilder sqlString = new StringBuilder()	
		.append("  select sum(s.repayment_total_amount) \"repaymentTotalAmount\", sum(s.term_already_repay_amount) \"termAlreadyRepayAmount\", m.account_available_amount \"companyAccountAvailableAmount\" ")
		.append("  from bao_t_project_info t, bao_t_repayment_plan_info s, bao_t_account_info m ")
		.append("  where t.id = s.PROJECT_ID and t.cust_id = m.cust_id ")
		.append("  and t.project_status = ?  ")
		.append("  and s.repayment_status = ? ");
		
		List<Object> objList = Lists.newArrayList();
		objList.add(Constant.PROJECT_STATUS_08); // 还款中
		objList.add(Constant.REPAYMENT_STATUS_WAIT); // 未还款
		if(StringUtils.isEmpty((String)params.get("beginExceptDate"))
				&& StringUtils.isEmpty((String)params.get("endExceptDate"))) {
			sqlString.append("  and s.expect_repayment_date <= to_char(?, 'yyyyMMdd') ");
			objList.add(DateUtils.getAfterDay(new Date(), 7)); // 近7天
		}
		SqlCondition sqlCondition = new SqlCondition(sqlString, params, objList);
		sqlCondition.addString("projectType", "t.project_type")
		            .addString("companyName", "t.company_name") 
		            .addString("projectNo", "t.project_no")			            
		            .addBeginDate("beginExceptDate", "to_date(s.expect_repayment_date, 'yyyyMMdd')")
		            .addEndDate("endExceptDate", "to_date(s.expect_repayment_date, 'yyyyMMdd')")
		            .addList("planList", "s.id")
		            .addSql("  group by m.account_available_amount ");
		
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sqlCondition.toString(), sqlCondition.toArray());
		if(list != null && list.size() > 0) {
			result = list.get(0);
		}
		return result;
	}

	@Override
	public Page<Map<String, Object>> queryOverdueRepaymentList(
			Map<String, Object> params) {

		StringBuilder sqlString = new StringBuilder()	
		.append("       select a.projectId \"projectId\", a.projectNo \"projectNo\", a.projectType \"projectType\",  ")
		.append("              a.companyName \"companyName\", a.typeTerm \"typeTerm\", ")
		.append("              b.repaymentTotalAmount \"repaymentTotalAmount\", b.termAlreadyRepayAmount \"termAlreadyRepayAmount\", ")
		.append("              c.overdueDays \"overdueDays\", c.overdueExpense \"overdueExpense\", c.payStatus \"payStatus\", ")
		.append("              d.account_available_amount \"companyAccountAvailableAmount\" ")
		.append("       from ")
		.append("       ( ")
		.append("         select t.id projectId, t.project_no projectNo, t.project_type projectType,  ")
		.append("               t.company_name companyName, t.type_term typeTerm, t.cust_id custId ")
		.append("         from bao_t_project_info t ")
		.append("         where t.project_status = ? ")
		.append("       ) a ")
		.append("       inner join ")
		.append("       ( ")
		.append("         select t.id projectId, sum(s.repayment_total_amount) repaymentTotalAmount, sum(s.term_already_repay_amount) termAlreadyRepayAmount ")
		.append("         from bao_t_project_info t, bao_t_repayment_plan_info s ")
		.append("         where t.id = s.PROJECT_ID ")
		.append("         and t.project_status = ? ")
		.append("         and s.repayment_status = ? ")
		.append("         and s.expect_repayment_date <= ? ")
		.append("         group by t.id  ")
		.append("       ) b on b.projectid = a.projectId ")
		.append("       inner join ")
		.append("       ( ")
		.append("         select t.id projectId, ")
		.append("               (trunc(?) - to_date(s.expect_repayment_date, 'yyyyMMdd')) overdueDays, ")
		.append("               (s.remainder_principal + s.repayment_principal)*(trunc(?) - to_date(s.expect_repayment_date, 'yyyyMMdd'))*? overdueExpense, ")
		.append("               s.is_riskamount_repay payStatus ")
		.append("         from bao_t_project_info t, bao_t_repayment_plan_info s ")
		.append("         where t.id = s.PROJECT_ID ")
		.append("         and t.project_status = ? ")
		.append("         and s.repayment_status = ? ")
		.append("         and s.current_term = ( ")
		.append("             select min(m.current_term) ")
		.append("             from bao_t_repayment_plan_info m ")
		.append("             where s.project_id = m.project_id ")
		.append("             and m.repayment_status = ? ")
		.append("         ) ")
		.append("       )c on c.projectid = a.projectId ")
		.append("       inner join bao_t_account_info d on d.cust_id = a.custId ")
		.append("       where 1 = 1 ");
		
		List<Object> objList = Lists.newArrayList();
		objList.add(Constant.PROJECT_STATUS_10); // 已逾期
		objList.add(Constant.PROJECT_STATUS_10); // 已逾期
		objList.add(Constant.REPAYMENT_STATUS_WAIT); // 未还款
		objList.add(DateUtils.getCurrentDate("yyyyMMdd"));
		objList.add(new Date());
		objList.add(new Date());
		objList.add(new BigDecimal(params.get("overdueRate").toString()));// 逾期管理费率
		objList.add(Constant.PROJECT_STATUS_10); // 已逾期
		objList.add(Constant.REPAYMENT_STATUS_WAIT); // 未还款
		objList.add(Constant.REPAYMENT_STATUS_WAIT); // 未还款
		
		SqlCondition sqlCondition = new SqlCondition(sqlString, params, objList);
		sqlCondition.addString("projectType", "a.projectType")
		            .addString("companyName", "a.companyName") 
		            .addString("projectNo", "a.projectNo")
		            .addSql(" order by c.overdueDays desc, a.projectNo asc");
		
		return repositoryUtil.queryForPageMap(sqlCondition.toString(), sqlCondition.toArray(), Integer.valueOf(params.get("start").toString()),
				Integer.valueOf(params.get("length").toString()));
	}

	@Override
	public Map<String, Object> queryOverdueRepaymentTotal(
			Map<String, Object> params) {
		Map<String, Object> result = Maps.newHashMap();
		
		StringBuilder sqlString = new StringBuilder()	
		.append("       select sum(b.repaymentTotalAmount) \"repaymentTotalAmount\", sum(b.termAlreadyRepayAmount) \"termAlreadyRepayAmount\", ")
        .append("              sum(c.overdueExpense) \"overdueExpense\" ")
		.append("       from ")
		.append("       ( ")
		.append("         select t.id projectId, t.project_no projectNo, t.project_type projectType,  ")
		.append("               t.company_name companyName, t.type_term typeTerm, t.cust_id custId ")
		.append("         from bao_t_project_info t ")
		.append("         where t.project_status = ? ")
		.append("       ) a ")
		.append("       inner join ")
		.append("       ( ")
		.append("         select t.id projectId, sum(s.repayment_total_amount) repaymentTotalAmount, sum(s.term_already_repay_amount) termAlreadyRepayAmount ")
		.append("         from bao_t_project_info t, bao_t_repayment_plan_info s ")
		.append("         where t.id = s.PROJECT_ID ")
		.append("         and t.project_status = ? ")
		.append("         and s.repayment_status = ? ")
		.append("         and s.expect_repayment_date <= ? ")
		.append("         group by t.id  ")
		.append("       ) b on b.projectid = a.projectId ")
		.append("       inner join ")
		.append("       ( ")
		.append("         select t.id projectId, ")
		.append("               (trunc(?) - to_date(s.expect_repayment_date, 'yyyyMMdd')) overdueDays, ")
		.append("               (s.remainder_principal + s.repayment_principal)*(trunc(?) - to_date(s.expect_repayment_date, 'yyyyMMdd'))*? overdueExpense, ")
		.append("               s.is_riskamount_repay payStatus ")
		.append("         from bao_t_project_info t, bao_t_repayment_plan_info s ")
		.append("         where t.id = s.PROJECT_ID ")
		.append("         and t.project_status = ? ")
		.append("         and s.repayment_status = ? ")
		.append("         and s.current_term = ( ")
		.append("             select min(m.current_term) ")
		.append("             from bao_t_repayment_plan_info m ")
		.append("             where s.project_id = m.project_id ")
		.append("             and m.repayment_status = ? ")
		.append("         ) ")
		.append("       )c on c.projectid = a.projectId ")
		.append("       where 1 = 1 ");
		
		List<Object> objList = Lists.newArrayList();
		objList.add(Constant.PROJECT_STATUS_10); // 已逾期
		objList.add(Constant.PROJECT_STATUS_10); // 已逾期
		objList.add(Constant.REPAYMENT_STATUS_WAIT); // 未还款
		objList.add(DateUtils.getCurrentDate("yyyyMMdd"));
		objList.add(new Date());
		objList.add(new Date());
		objList.add(new BigDecimal(params.get("overdueRate").toString()));// 逾期管理费率
		objList.add(Constant.PROJECT_STATUS_10); // 已逾期
		objList.add(Constant.REPAYMENT_STATUS_WAIT); // 未还款
		objList.add(Constant.REPAYMENT_STATUS_WAIT); // 未还款
		
		SqlCondition sqlCondition = new SqlCondition(sqlString, params, objList);
		sqlCondition.addString("projectType", "a.projectType")
		            .addString("companyName", "a.companyName") 
		            .addString("projectNo", "a.projectNo")
		            .addSql(" order by c.overdueDays desc, a.projectNo asc");
		
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sqlCondition.toString(), sqlCondition.toArray());
		if(list != null && list.size() > 0) {
			result = list.get(0);
		}
		return result;
	}

	@Override
	public Page<Map<String, Object>> queryAllRepaymentList(
			Map<String, Object> params) {
		
		StringBuilder sqlString = new StringBuilder()	
		.append("     select a.id \"projectId\", a.project_no \"projectNo\", a.project_type \"projectType\",    ")
		.append("                a.company_name \"companyName\", a.cust_id \"custId\", a.repaymnet_method \"repaymnetMethod\",    ")
		.append("                a.already_invest_amount \"alreadyInvestAmount\", a.remainder_principal \"remainderPrincipal\", to_date(a.repayment_day, 'yyyyMMdd') \"nextRepaymentDate\",  ")
		.append("                a.repayment_total_amount \"repaymentTotalAmount\", a.penalty_amount \"earlyRepaymentExpense\",  ")
		.append("                a.release_date \"releaseDate\", a.project_end_date \"projectEndDate\", a.project_status \"projectStatus\", ")
		.append("                a.account_available_amount \"companyAccountAvailableAmount\", a.effect_date \"effectDate\", ")
		.append("                nvl((trunc(?) - trunc(prev_repayment_date))/(to_date(a.expect_repayment_date, 'yyyymmdd') - trunc(prev_repayment_date))*a.repayment_interest, 0) \"earlyInterest\",  ")
		.append("                nvl((trunc(?) - trunc(prev_repayment_date))/(to_date(a.expect_repayment_date, 'yyyymmdd') - trunc(prev_repayment_date))*a.account_manage_expense, 0) \"earlyManageExpense\"   ")
		.append("     from ")
		.append("     ( ")
		.append("     select t.id, t.project_no, t.project_type,    ")
		.append("                t.company_name, t.cust_id, t.repaymnet_method,   ")
		.append("                s.already_invest_amount, t.remainder_principal, t.repayment_day,  ")
		.append("                m.repayment_total_amount, m.penalty_amount, m.repayment_interest,  ")
		.append("                m.account_manage_expense, m.expect_repayment_date, ")
		.append("                t.release_date, t.project_end_date, t.project_status, ")
		.append("                n.account_available_amount, t.effect_date, t.type_term, ")
		.append("                case m.current_term when 1 then t.effect_date ")
		.append("                                    else ")
		.append("                                    to_date((select q.expect_repayment_date from bao_t_repayment_plan_info q where q.project_id = t.id and q.current_term = m.current_term - 1), 'yyyymmdd')  ")
		.append("                end prev_repayment_date                                    ")
		.append("         from bao_t_project_info t  ")
		.append("         inner join bao_t_project_invest_info s on s.project_id = t.id  ")
		.append("         inner join bao_t_repayment_plan_info m on m.project_id = t.id and m.expect_repayment_date = t.repayment_day  ")
		.append("         inner join bao_t_account_info n on n.cust_id = t.cust_id  ")
		.append("         where 1 = 1 ")
		.append("     ) a where 1 = 1 ");
		
		List<Object> objList = Lists.newArrayList();
		objList.add(new Date());
		objList.add(new Date());
		
		SqlCondition sqlCondition = new SqlCondition(sqlString, params, objList);
		sqlCondition.addString("projectId", "a.id")
					.addString("projectType", "a.project_type")
		            .addString("companyName", "a.company_name") 
		            .addString("projectNo", "a.project_no")	
		            .addString("projectName", "a.project_name")
		            .addString("productTerm", "a.type_term")
		            .addString("repaymentMethod", "a.repaymnet_method")
		            .addString("projectStatus", "a.project_status")
		            .addBeginDate("beginReleaseDate", "a.release_date")
		            .addEndDate("endReleaseDate", "a.release_date")
		            .addBeginDate("beginEffectDate", "a.effect_date")
		            .addEndDate("endEffectDate", "a.effect_date")
		            .addBeginDate("beginProjectEndDate", "a.project_end_date")
		            .addEndDate("endPojectEndDate", "a.project_end_date")
		            .addBeginDate("beginRepaymentDate", "to_date(a.repayment_day, 'yyyyMMdd')")
		            .addEndDate("endRepaymentDate", "to_date(a.repayment_day, 'yyyyMMdd')")
		            .addList("projectStatusList", "a.project_status");
		
		return repositoryUtil.queryForPageMap(sqlCondition.toString(), sqlCondition.toArray(), Integer.valueOf(params.get("start").toString()),
				Integer.valueOf(params.get("length").toString()));
	}
	

	@Override
	public Page<Map<String, Object>> queryAllRepaymentListNew(
			Map<String, Object> params) {
		StringBuilder sqlString = new StringBuilder()	
		.append("   select  n.account_available_amount \"companyAccountAvailableAmount\", ")
		.append("            a.projectId \"projectId\", a.projectNo \"projectNo\", a.projectType \"projectType\",  ")
		.append("            a.companyName \"companyName\", a.custId \"custId\", a.repaymnetMethod \"repaymnetMethod\", ")
		.append("            a.alreadyInvestAmount \"alreadyInvestAmount\", a.remainderPrincipal \"remainderPrincipal\", a.nextRepaymentDate \"nextRepaymentDate\", ")
		.append("            a.repaymentTotalAmount \"repaymentTotalAmount\", a.earlyPenaltyAmount \"earlyPenaltyAmount\", a.repaymentInterest \"repaymentInterest\", ")
		.append("            a.accountManageExpense \"accountManageExpense\", a.expectRepaymentDate \"expectRepaymentDate\", a.releaseDate \"releaseDate\", ")
		.append("            a.projectEndDate \"projectEndDate\", a.projectStatus \"projectStatus\", a.effectDate \"effectDate\", ")
		.append("            a.earlyRepaymentExpense \"earlyRepaymentExpense\", a.isAmountFrozen \"isAmountFrozen\", a.typeTerm \"typeTerm\" ")
		.append("   from( ")
		.append("   select t.id projectId, ")
		.append("        t.project_no projectNo, ")
		.append("        t.project_type projectType, ")
		.append("        t.company_name companyName, ")
		.append("        t.cust_id custId, ")
		.append("        t.repaymnet_method repaymnetMethod, ")
		.append("        s.already_invest_amount alreadyInvestAmount, ")
		.append("        t.remainder_principal remainderPrincipal, ")
		.append("        to_date(t.repayment_day, 'yyyyMMdd') nextRepaymentDate, ")
		.append("        m.repayment_total_amount repaymentTotalAmount, ")
		.append("        m.penalty_amount earlyPenaltyAmount, ")
		.append("        m.repayment_interest repaymentInterest, ")
		.append("        m.account_manage_expense accountManageExpense, ")
		.append("        m.expect_repayment_date expectRepaymentDate, ")
		.append("        t.release_date releaseDate, ")
		.append("        t.project_end_date projectEndDate, ")
		.append("        t.project_status projectStatus,              ")
		.append("        t.effect_date effectDate, ")
		.append("        m.advance_cleanup_total_amount earlyRepaymentExpense, ")
		.append("        m.is_amount_frozen isAmountFrozen, ")
		.append("        t.type_term typeTerm ")
		.append("   from bao_t_project_info t ")
		.append("  inner join bao_t_project_invest_info s ")
		.append("     on s.project_id = t.id ")
		.append("  inner join bao_t_repayment_plan_info m ")
		.append("     on m.project_id = t.id ")
		.append("    and m.expect_repayment_date = t.repayment_day ")
		.append("  where 1 = 1 %s")
		.append("  ) a inner join bao_t_account_info n on n.cust_id = a.custId ")
		.append("  order by decode(a.projectStatus, '已逾期', '1', '还款中', '2', '提前结清', '3', '已到期', '4', '5'), ")
		.append("           a.nextRepaymentDate, ")
		.append("           a.projectNo          ");
				
		StringBuilder whereSqlString = new StringBuilder();
		SqlCondition sqlCondition = new SqlCondition(whereSqlString, params);
		sqlCondition.addString("projectId", "t.id")
					.addString("projectType", "t.project_type")
		            .addString("companyName", "t.company_name") 
		            .addString("projectNo", "t.project_no")	
		            .addString("projectName", "t.project_name")
		            .addString("productTerm", "t.type_term")
		            .addString("repaymentMethod", "t.repaymnet_method")
		            .addString("projectStatus", "t.project_status")
		            .addBeginDate("beginReleaseDate", "t.release_date")
		            .addEndDate("endReleaseDate", "t.release_date")
		            .addBeginDate("beginEffectDate", "t.effect_date")
		            .addEndDate("endEffectDate", "t.effect_date")
		            .addBeginDate("beginProjectEndDate", "t.project_end_date")
		            .addEndDate("endPojectEndDate", "t.project_end_date")
		            .addBeginDate("beginRepaymentDate", "to_date(t.repayment_day, 'yyyyMMdd')")
		            .addEndDate("endRepaymentDate", "to_date(t.repayment_day, 'yyyyMMdd')")
		            .addList("projectStatusList", "t.project_status");
		
		return repositoryUtil.queryForPageMap(String.format(sqlString.toString(), sqlCondition.toString()), sqlCondition.toArray(), Integer.valueOf(params.get("start").toString()),
				Integer.valueOf(params.get("length").toString()));
	}

	@Override
	public Map<String, Object> queryAllRepaymentTotal(Map<String, Object> params) {
		
		Map<String, Object> result = Maps.newHashMap();
		
		StringBuilder sqlString = new StringBuilder()	
		.append(" select sum(s.already_invest_amount) \"alreadyInvestAmount\", sum(t.remainder_principal) \"remainderPrincipal\",  ")
		.append("        sum(m.repayment_total_amount) \"repaymentTotalAmount\", sum(m.penalty_amount) \"earlyPenaltyAmount\",  ")
		.append("        sum(m.advance_cleanup_total_amount) \"earlyRepaymentExpense\" ")   
		.append(" 	 from bao_t_project_info t ")
		.append("    inner join bao_t_project_invest_info s on s.project_id = t.id ")
		.append("    inner join bao_t_repayment_plan_info m on m.project_id = t.id and m.expect_repayment_date = t.repayment_day ")
		.append(" 	 where 1 = 1 ");
		
		SqlCondition sqlCondition = new SqlCondition(sqlString, params);
		sqlCondition.addString("projectId", "t.id")
					.addString("projectType", "t.project_type")
		            .addString("companyName", "t.company_name") 
		            .addString("projectNo", "t.project_no")	
		            .addString("projectName", "t.project_name")
		            .addString("productTerm", "t.type_term")
		            .addString("repaymentMethod", "t.repaymnet_method")
		            .addString("projectStatus", "t.project_status")
		            .addBeginDate("beginReleaseDate", "t.release_date")
		            .addEndDate("endReleaseDate", "t.release_date")
		            .addBeginDate("beginEffectDate", "t.effect_date")
		            .addEndDate("endEffectDate", "t.effect_date")
		            .addBeginDate("beginProjectEndDate", "t.project_end_date")
		            .addEndDate("endPojectEndDate", "t.project_end_date")
		            .addBeginDate("beginRepaymentDate", "to_date(t.repayment_day, 'yyyyMMdd')")
		            .addEndDate("endRepaymentDate", "to_date(t.repayment_day, 'yyyyMMdd')")
		            .addList("projectStatusList", "t.project_status");
		
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sqlCondition.toString(), sqlCondition.toArray());
		if(list != null && list.size() > 0) {
			result = list.get(0);
		}
		return result;
	}

	@Override
	public List<Map<String, Object>> queryWaitingRepaymentList(
			Map<String, Object> params) {
		
		StringBuilder sqlString = new StringBuilder()	
		.append("  select t.id \"projectId\", t.project_status \"projectStatus\", s.id \"replaymentPlanId\", ")
		.append("        s.is_riskamount_repay \"isRiskamountRepay\", t.project_no \"projectNo\", t.project_name \"projectName\" ")
		.append("  from bao_t_project_info t, bao_t_repayment_plan_info s ")
		.append("  where t.id = s.project_id ")
		.append("  and s.repayment_status = ? ")
		.append("  and s.expect_repayment_date = ? ")
		.append("  order by t.effect_date");

		return repositoryUtil.queryForMap(sqlString.toString(), new Object[] {(String)params.get("repaymentStatus"), (String)params.get("expectRepaymentDate")});
	}

	@Override
	public List<Map<String, Object>> queryWaitingRiskRepaymentList(
			Map<String, Object> params) {
		StringBuilder sqlString = new StringBuilder()	
		.append("  select t.id \"projectId\", t.project_status \"projectStatus\", s.id \"replaymentPlanId\", ")
		.append("        s.is_riskamount_repay \"isRiskamountRepay\", t.project_no \"projectNo\", t.project_name \"projectName\" ")
		.append("  from bao_t_project_info t, bao_t_repayment_plan_info s ")
		.append("  where t.id = s.project_id ")
		.append("  and s.repayment_status = ? ")
		.append("  and s.is_riskamount_repay = ? ")
		.append("  and s.expect_repayment_date <= ? ")
		.append("  order by t.effect_date, s.expect_repayment_date");

		return repositoryUtil.queryForMap(sqlString.toString(), new Object[] {(String)params.get("repaymentStatus"), (String)params.get("isRiskamountRepay"), (String)params.get("expectRepaymentDate")});
	}

	@Override
	public List<Map<String, Object>> queryRepaymentList(
			Map<String, Object> params) {
		
		StringBuilder sqlString = new StringBuilder()
		.append(" select s.create_date \"createDate\", s.subject_type \"tradeType\", s.trade_amount \"tradeAmount\",  ")
		.append("        s.subject_direction \"bankrollFlowDirection\" ")
		.append("  from bao_t_repayment_record_info t, bao_t_repay_record_detail_info s ")
		.append(" where t.id = s.repay_record_id ");
		
		SqlCondition sqlCondition = new SqlCondition(sqlString, params);
		sqlCondition.addString("accountFlowId", "t.relate_primary")
					.addString("custId", "t.cust_id");
		
		return repositoryUtil.queryForMap(sqlCondition.toString(), sqlCondition.toArray());
	}
}
