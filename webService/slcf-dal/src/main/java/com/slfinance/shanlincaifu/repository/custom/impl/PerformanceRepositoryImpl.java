package com.slfinance.shanlincaifu.repository.custom.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.PerformanceRepositoryCustom;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.SqlCondition;

@Repository
public class PerformanceRepositoryImpl implements PerformanceRepositoryCustom{
	
	@Autowired
	RepositoryUtil repositoryUtil;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * 昨日奖励
	 * @author  lyy
	 * @date    2016-12-05 14:29:33
	 * @param params
     *      <tt>custId:String:客户经理ID</tt><br>
	 * @return
     *      <tt>yesterdayAwardAmount:String:奖励金额</tt><br>
	 * @throws SLException
	 */
	public BigDecimal queryYesterdayAward(Map<String, Object> params) {
		StringBuilder sql = new StringBuilder()
		.append(" SELECT nvl(sum(nvl(award.TOTAL_PAYMENT_AMOUNT,0)),0) \"amount\" ")
		.append("  FROM BAO_T_USER_COMMISSION_INFO award ")
		.append(" WHERE award.EXCEPT_PAYMENT_DATE = to_char(SYSDATE-1, 'yyyyMMdd') ")
		.append(" AND award.CUST_ID = ? ")
		.append(" AND award.PAYMENT_STATUS != '已废弃' ");

		
		List<Object> args = new ArrayList<Object>();
		args.add(params.get("custId"));
		return jdbcTemplate.queryForObject(sql.toString(), args.toArray(), BigDecimal.class);
	}

	/**
	 * 奖励汇总
	 * @author  lyy
	 * @date    2016-12-05 14:29:33
	 * @param params
     *      <tt>custId:String:客户经理ID</tt><br>
	 * @return
     *      <tt>monthlySettlement  :String:本月已结算奖励</tt><br>
     *      <tt>monthlyUnSettlement:String:本月未结算奖励</tt><br>
	 * @throws SLException
	 */
	public List<Map<String, Object>> queryMyTotalAwardMonth(
			Map<String, Object> params) {
		StringBuilder sql = new StringBuilder()
		.append(" SELECT award.PAYMENT_STATUS \"paymentStatus\", sum(TRUNC(award.TOTAL_PAYMENT_AMOUNT,2)) \"amount\" ")
		.append("   FROM BAO_T_USER_COMMISSION_INFO award ")
		.append("  WHERE substr(award.EXCEPT_PAYMENT_DATE,1,6) = to_char(SYSDATE-1, 'yyyyMM') ")
		.append("    AND award.CUST_ID = ? ")
		.append("    AND award.PAYMENT_STATUS != '已废弃' ")
		.append("  GROUP BY award.PAYMENT_STATUS ")
		;
		
		List<Object> args = new ArrayList<Object>();
		args.add(params.get("custId"));
		return repositoryUtil.queryForMap(sql.toString(), args.toArray());
	}

	/**
	 * 奖励汇总
	 * @author  lyy
	 * @date    2016-12-05 14:29:33
	 * @param params
     *      <tt>custId:String:客户经理ID</tt><br>
	 * @return
     *      <tt>totalSettlement    :String:累计已结算奖励</tt><br>
     *      <tt>totalUnSettlement  :String:累计未结算奖励</tt><br>
	 * @throws SLException
	 */
	public List<Map<String, Object>> queryMyTotalAwardHisTotalList(
			Map<String, Object> params) {
		StringBuilder sql = new StringBuilder()
		.append(" SELECT award.PAYMENT_STATUS \"paymentStatus\", sum(TRUNC(award.TOTAL_PAYMENT_AMOUNT,2)) \"amount\" ")
		.append("   FROM BAO_T_USER_COMMISSION_INFO award ")
		.append("  WHERE 1=1 ")
		.append("    AND award.CUST_ID = ? ")
		.append("    AND award.PAYMENT_STATUS != '已废弃' ")
		.append("  GROUP BY award.PAYMENT_STATUS ")
		;
		
		List<Object> args = new ArrayList<Object>();
		args.add(params.get("custId"));
		return repositoryUtil.queryForMap(sql.toString(), args.toArray());
	}

	/**
	 * 奖励列表
	 * @author  lyy
	 * @date    2016-12-05 14:29:33
	 * @param params
     *      <tt>custId  :String:客户经理ID</tt><br>
     *      <tt>investId:String:投资ID（可以为空，为空表示查询所有，不为空表示查询当笔投资的奖励情况）</tt><br>
	 * @return
     *      <tt>awardDate        :String:奖励日期</tt><br>
     *      <tt>exceptAwardAmount:String:应奖励金额</tt><br>
     *      <tt>factAwardAmount  :String:已结算奖励</tt><br>
	 * @throws SLException
	 */
	public Page<Map<String, Object>> queryMyAwardList(Map<String, Object> params) {
		StringBuilder sql = new StringBuilder()
		.append(" SELECT to_char(to_date(award.EXCEPT_PAYMENT_DATE,'yyyy-MM-dd'),'yyyy-MM-dd') \"awardDate\" ")
		.append("      , sum(TRUNC(award.TOTAL_PAYMENT_AMOUNT ,2)) \"exceptAwardAmount\" ")
		.append("      , sum(TRUNC(award.FACT_PAYMENT_AMOUNT  ,2)) \"factAwardAmount\" ")
		.append("   FROM BAO_T_USER_COMMISSION_INFO award ")
		.append("  WHERE award.PAYMENT_STATUS != '已废弃' ")
		;
		List<Object> list = new ArrayList<Object>();
		if(!StringUtils.isEmpty(params.get("beginAwardDate"))){
			sql.append(" and to_date(award.EXCEPT_PAYMENT_DATE,'yyyy-MM-dd') >= ? ");
			list.add(DateUtils.parseStandardDate(params.get("beginAwardDate").toString()));
		} else {
			sql.append(" and to_date(award.EXCEPT_PAYMENT_DATE,'yyyy-MM-dd') >= TRUNC(SYSDATE) ");
		}
		if(!StringUtils.isEmpty(params.get("endAwardDate"))){
			sql.append(" and to_date(award.EXCEPT_PAYMENT_DATE,'yyyy-MM-dd') < ? ");
			list.add(DateUtils.getAfterDay(DateUtils.parseStandardDate(params.get("endAwardDate").toString()), 1));
		}
		
		SqlCondition condition = new SqlCondition(sql, params, list)
		.addString("custId", "award.CUST_ID")
		.addString("investId", "award.INVEST_ID")
		.addSql(" GROUP BY award.EXCEPT_PAYMENT_DATE ")
		.addSql(" ORDER BY award.EXCEPT_PAYMENT_DATE ")
		;
		return repositoryUtil.queryForPageMap(sql.toString(), condition.toArray(), Integer.parseInt(params.get("start").toString()), Integer.parseInt(params.get("length").toString()));
	}

	/**
	 * 奖励项目列表
	 * @author  lyy
	 * @date    2016-12-05 14:29:33
	 * @param params
     *      <tt>custId   :String:客户经理ID</tt><br>
     *      <tt>awardDate:String:奖励日期</tt><br>
	 * @return
     *      <tt>projectName      :String:奖励来源</tt><br>
     *      <tt>exceptAwardAmount:String:应奖励金额</tt><br>
	 * @throws SLException
	 */
	public Page<Map<String, Object>> queryMyAwardProjectList(
			Map<String, Object> params) {
		StringBuilder sql = new StringBuilder()
		.append(" SELECT loan.LOAN_TITLE \"projectName\" ")
		.append("      , award.TOTAL_PAYMENT_AMOUNT \"exceptAwardAmount\" ")
		.append("      , invest.ID \"investId\" ")
		.append("      , loan.ID \"loanId\" ")
		.append("      , award.ID \"comId\" ")
		.append("      , invest.INVEST_MODE \"investMode\" ")
		.append("   FROM BAO_T_USER_COMMISSION_INFO award, BAO_T_INVEST_INFO invest, BAO_T_LOAN_INFO loan  ")
		.append("   WHERE loan.ID = invest.LOAN_ID ")
		.append("     AND invest.ID = award.INVEST_ID ")
		.append("     AND award.CUST_ID = ? ")
		.append("     AND award.PAYMENT_STATUS != '已废弃' ")
		.append("     AND to_char(to_date(award.EXCEPT_PAYMENT_DATE,'yyyy-MM-dd'),'yyyy-MM-dd') = ? ")
		.append("   ORDER BY \"projectName\", invest.CREATE_DATE DESC ")
		;
		
		List<Object> args = new ArrayList<Object>();
		args.add(params.get("custId"));
		args.add(params.get("awardDate"));
		;
		return repositoryUtil.queryForPageMap(sql.toString(), args.toArray(), Integer.parseInt(params.get("start").toString()), Integer.parseInt(params.get("length").toString()));
	}

	/**
	 * 本月注册人数
	 * @author  lyy
	 * @date    2016-12-05 14:29:33
	 * @param params
     *      <tt>start :String:起始值</tt><br>
     *      <tt>length:String:长度</tt><br>
     *      <tt>custId:String:客户ID（客户经理）</tt><br>
	 * @return
     *      <tt>custId         :String:客户ID</tt><br>
     *      <tt>custName       :String:用户名</tt><br>
     *      <tt>credentialsCode:String:证件号码</tt><br>
     *      <tt>mobile         :String:手机号</tt><br>
     *      <tt>registerDate   :String:注册时间</tt><br>
	 * @throws SLException
	 */
	public Page<Map<String, Object>> queryMonthlyRegisterList(
			Map<String, Object> params) {
		StringBuilder sql = new StringBuilder()
		.append(" SELECT cust.ID \"custId\" ")
		.append("      , cust.CUST_NAME \"custName\" ")
		.append("      , substr(cust.CREDENTIALS_CODE,1,2)||'******'||substr(cust.CREDENTIALS_CODE,-2) \"credentialsCode\" ")
		.append("      , cust.MOBILE \"mobile\" ")
		.append("      , cust.CREATE_DATE \"registerDate\" ")
		.append("   FROM BAO_T_CUST_RECOMMEND_INFO cr, BAO_T_CUST_INFO cust ")
		.append("  WHERE cust.ID = cr.QUILT_CUST_ID ")
		.append("    AND to_char(cust.CREATE_DATE,'yyyy-MM') = to_char(SYSDATE,'yyyy-MM') ")
		.append("    AND cr.RECORD_STATUS='有效' ")
		.append("    AND cr.CUST_ID = ? ")
		.append("  ORDER BY cust.CREATE_DATE desc ")
		;
		
		List<Object> args = new ArrayList<Object>();
		args.add(params.get("custId"))
		;
		return repositoryUtil.queryForPageMap(sql.toString(), args.toArray(), Integer.parseInt(params.get("start").toString()), Integer.parseInt(params.get("length").toString()));
	}

	/**
	 * 本月投资人数
	 * @author  lyy
	 * @date    2016-12-05 14:29:33
	 * @param params
     *      <tt>start :String:起始值</tt><br>
     *      <tt>length:String:长度</tt><br>
     *      <tt>custId:String:客户ID（客户经理）</tt><br>
	 * @return
     *      <tt>custId         :String:客户ID</tt><br>
     *      <tt>custName       :String:用户名</tt><br>
     *      <tt>credentialsCode:String:证件号码</tt><br>
     *      <tt>mobile         :String:手机号</tt><br>
     *      <tt>investStatus   :String:投资状态</tt><br>
	 * @throws SLException
	 */
	public Page<Map<String, Object>> queryMonthlyInvestorList(
			Map<String, Object> params) {
		/*
		StringBuilder sql = new StringBuilder()
		.append("   select distinct cust_id \"custId\" ")
		.append("      , CUST_NAME \"custName\" ")
		.append("      , substr(CREDENTIALS_CODE,1,2)||'******'||substr(CREDENTIALS_CODE,-2) \"credentialsCode\" ")
		.append("      , MOBILE \"mobile\" ")
		.append("      , CREATE_DATE \"registerDate\" ")
		.append("   from  ")
		.append("   ( ")
		.append("   select t.cust_id, cust.CUST_NAME, cust.CREDENTIALS_CODE, cust.MOBILE, t.INVEST_STATUS, cust.CREATE_DATE ")
		.append("   from bao_t_invest_info t, bao_t_cust_recommend_info s, bao_t_project_info m, BAO_T_CUST_INFO cust")
		.append("   where t.cust_id = s.quilt_cust_id and t.project_id = m.id ")
		.append("   AND cust.ID = t.CUST_ID ")
		.append("   and t.project_id is not null ")
		.append("   and t.effect_date >= ? and t.effect_date < ? ")
		.append("   and m.project_status in ('还款中','已逾期','已到期','提前结清') ")
		.append("   and s.record_status = '有效' ")
		.append("   and t.cust_id not in (select id     ")
		.append("                         from bao_t_cust_info    ")
		.append("                         where is_recommend = '是' )  ")
		.append("   and s.cust_id = ? ")
		.append("   union all ")
		.append("   select t.cust_id, cust.CUST_NAME, cust.CREDENTIALS_CODE, cust.MOBILE, t.INVEST_STATUS, cust.CREATE_DATE ")
		.append("     from bao_t_invest_info t, bao_t_project_info m, BAO_T_CUST_INFO cust    ")
		.append("    where t.project_id = m.id and t.project_id is not null   ")
		.append("    AND cust.ID = t.CUST_ID ")
		.append("    and t.cust_id in (select id     ")
		.append("                      from bao_t_cust_info     ")
		.append("                      where is_recommend = '是' )      ")
		.append("    and t.effect_date >= ? and t.effect_date < ? ")
		.append("    and m.project_status in ('还款中','已逾期','已到期','提前结清')  ")
		.append("    and t.cust_id = ?  ")
		.append("    union all   ")
		.append("   select t.cust_id, cust.CUST_NAME, cust.CREDENTIALS_CODE, cust.MOBILE, t.INVEST_STATUS, cust.CREATE_DATE ")
		.append("     from bao_t_invest_info t, bao_t_cust_recommend_info s, bao_t_wealth_info n, bao_t_wealth_type_info m, BAO_T_CUST_INFO cust  ")
		.append("    where t.cust_id = s.quilt_cust_id and t.wealth_id = n.id and n.wealth_type_id = m.id   ")
		.append("      AND cust.ID = t.CUST_ID ")
		.append("      and t.wealth_id is not null       ")
		.append("      and to_char(n.effect_date, 'yyyyMMdd') >= ? and to_char(n.effect_date, 'yyyyMMdd') < ? ")
		.append("      and t.invest_status in ('收益中','提前赎回中','提前赎回','到期赎回中','到期赎回')  ")
		.append("      and s.record_status = '有效'   ")
		.append("      and t.cust_id not in (select id     ")
		.append("                            from bao_t_cust_info     ")
		.append("                            where is_recommend = '是' )     ")
		.append("      and s.cust_id = ?  ")
		.append("    union all   ")
		.append("    select t.cust_id, cust.CUST_NAME, cust.CREDENTIALS_CODE, cust.MOBILE, t.INVEST_STATUS, cust.CREATE_DATE ")
		.append("     from bao_t_invest_info t, bao_t_wealth_info n, bao_t_wealth_type_info m, BAO_T_CUST_INFO cust    ")
		.append("    where t.wealth_id = n.id and n.wealth_type_id = m.id and t.wealth_id is not null   ")
		.append("      AND cust.ID = t.CUST_ID ")
		.append("    and t.cust_id in (select id     ")
		.append("                      from bao_t_cust_info     ")
		.append("                      where is_recommend = '是' )     ")
		.append("    and t.effect_date >= ? and t.effect_date < ? ")
		.append("    and t.invest_status in ('收益中','提前赎回中','提前赎回','到期赎回中','到期赎回')  ")
		.append("    and t.cust_id = ?  ")
		.append("    union all   ")
		.append("           select  t.cust_id, c.CUST_NAME, c.CREDENTIALS_CODE, c.MOBILE, t.INVEST_STATUS, c.CREATE_DATE ")
		.append("            from bao_t_invest_info t, bao_t_cust_recommend_info s, bao_t_loan_info n, bao_t_loan_detail_info m, bao_t_cust_info c  ")
		.append("           where t.cust_id = s.quilt_cust_id and t.loan_id = n.id and n.id = m.loan_id and t.cust_id = c.id ")
		.append("             and t.loan_id is not null  ")
		.append("             and t.effect_date >= ? and t.effect_date < ? ")
		.append("             and t.invest_status in ('收益中','提前结清','已到期', '已转让') ")
		.append("             and s.record_status = '有效'  ")
		.append("             and t.cust_id not in (select id    ")
		.append("                                   from bao_t_cust_info    ")
		.append("                                   where is_recommend = '是' )    ")
		.append("             and s.cust_id = ? ") // 查询当月业务员旗下所有客户【散标】投资情况
		.append("           union all  ")
		.append("           select  t.cust_id, c.CUST_NAME, c.CREDENTIALS_CODE, c.MOBILE, t.INVEST_STATUS, c.CREATE_DATE ")
		.append("            from bao_t_invest_info t, bao_t_loan_info n, bao_t_loan_detail_info m, bao_t_cust_info c     ")
		.append("           where t.loan_id = n.id and n.id = m.loan_id and t.cust_id = c.id  ")
		.append("           and t.loan_id is not null  ")
		.append("           and t.cust_id in (select id    ")
		.append("                             from bao_t_cust_info    ")
		.append("                             where is_recommend = '是' )    ")
		.append("           and t.effect_date >= ? and t.effect_date < ? ")
		.append("           and t.invest_status in ('收益中','提前结清','已到期', '已转让') ")
		.append("           and t.cust_id = ?  ")// 查询当月业务员自己【散标】投资情况
		.append("    ) q ");
		;
		String currentMonth = DateUtils.getCurrentDate("yyyyMM");
		
		List<Object> args = new ArrayList<Object>();
		args.add(currentMonth + "01");
		args.add(DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(currentMonth + "01", "yyyyMMdd"), 1), "yyyyMMdd"));
		args.add(params.get("custId"));
		args.add(currentMonth + "01");
		args.add(DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(currentMonth + "01", "yyyyMMdd"), 1), "yyyyMMdd"));
		args.add(params.get("custId"));
		args.add(currentMonth + "01");
		args.add(DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(currentMonth + "01", "yyyyMMdd"), 1), "yyyyMMdd"));
		args.add(params.get("custId"));
		args.add(currentMonth + "01");
		args.add(DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(currentMonth + "01", "yyyyMMdd"), 1), "yyyyMMdd"));
		args.add(params.get("custId"));
		args.add(currentMonth + "01");
		args.add(DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(currentMonth + "01", "yyyyMMdd"), 1), "yyyyMMdd"));
		args.add(params.get("custId"));
		args.add(currentMonth + "01");
		args.add(DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(currentMonth + "01", "yyyyMMdd"), 1), "yyyyMMdd"));
		args.add(params.get("custId"));
		*/
		// 2017/1/17 update by liyy --CommissionInfoRepositoryImpl.sumProjectAndWealthCommission -- 本月投资统计
		StringBuilder sql = new StringBuilder()
		.append("   select distinct cust_id \"custId\" ")
		.append("      , CUST_NAME \"custName\" ")
		.append("      , substr(CREDENTIALS_CODE,1,2)||'******'||substr(CREDENTIALS_CODE,-2) \"credentialsCode\" ")
		.append("      , MOBILE \"mobile\" ")
		.append("      , CREATE_DATE \"registerDate\" ")
		.append("   from ( ")
		.append(" select i.cust_id, c.CUST_NAME, c.CREDENTIALS_CODE, c.MOBILE, i.INVEST_STATUS, c.CREATE_DATE ")
		.append("   from  bao_t_commission_info t, bao_t_commission_detail_info s  ")
		.append("       , BAO_T_INVEST_INFO i , bao_t_cust_info c      ")
		.append("  where 1=1 ")
		.append("    AND c.ID = i.CUST_ID ")
		.append("    AND i.effect_date >= ? and i.effect_date < ?  ")
		.append("    AND i.ID = s.INVEST_ID  ")
		.append("    and s.commission_id = t.id ")
		.append("    and t.cust_id = ?  ")
		.append("    ) q ");
		;
		String currentMonth = DateUtils.getCurrentDate("yyyyMM");
		List<Object> args = new ArrayList<Object>();
		args.add(currentMonth + "01");
		args.add(DateUtils.formatDate(DateUtils.getAfterMonth(DateUtils.parseDate(currentMonth + "01", "yyyyMMdd"), 1), "yyyyMMdd"));
		args.add(params.get("custId"));
		
		return repositoryUtil.queryForPageMap(sql.toString(), args.toArray(), Integer.parseInt(params.get("start").toString()), Integer.parseInt(params.get("length").toString()));
	}
}
