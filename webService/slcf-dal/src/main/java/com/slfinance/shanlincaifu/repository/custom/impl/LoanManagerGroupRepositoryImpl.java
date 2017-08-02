package com.slfinance.shanlincaifu.repository.custom.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans .factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.google.common.collect.Maps;
import com.slfinance.shanlincaifu.repository.ParamRepository;
import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.LoanManagerGroupRepositoryCustom;
import com.slfinance.shanlincaifu.utils.CommonUtils;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.SqlCondition;


@Repository
public class LoanManagerGroupRepositoryImpl implements LoanManagerGroupRepositoryCustom{
	@Autowired
	RepositoryUtil repositoryUtil;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	ParamRepository paramRepository;
	@Override
	public List<Map<String,Object>> queryTradeInfoByOneStepInvest(Map<String, Object> parms) {
		SqlCondition sqlCondition = getTradeInfoByOneStepInvestSql(parms);
		return jdbcTemplate.queryForList(sqlCondition.toString());
	}
	
	private SqlCondition getTradeInfoByOneStepInvestSql(Map<String, Object> param){
		String value = paramRepository.findByTypeNameAndParameterName(Constant.SLCF_COM_PARAM, Constant.SLCF_COM_PARAM_05).getValue();
		String loanValue = "'" + value.replaceAll(",", "','") + "'";
		
		StringBuilder sql = new StringBuilder()
		.append("  select ")
		.append("             loan.LOAN_TERM \"loanTerm\",loan.LOAN_UNIT \"loanUnit\" ")
		.append("             from BAO_T_LOAN_INFO loan ")
		.append("             WHERE 1=1  ")
		.append("             AND loan.LOAN_STATUS='募集中'  ")
		.append("             AND loan.NEWER_FLAG='普通标'  ")
	    .append("   AND (loan.CHANNEL_FLAG != '是' or loan.CHANNEL_FLAG is null) ")// 是否用于渠道
//		.append("   AND loan.IS_RUN_AUTO_INVEST ='Y' ")
		.append("   AND (loan.loan_type in (" + loanValue +  ") or (loan.IS_RUN_AUTO_INVEST ='Y' and loan.loan_type not in (" + loanValue +  "))) ")
		.append("             group by loan.LOAN_TERM,loan.LOAN_UNIT ")
		.append("             order by loan.loan_term*decode(loan.loan_unit,'天',1,'月',30) ");
		
		return  new SqlCondition(sql,param );
	}

	@Override
	public BigDecimal getCanBuyTotalAmount(Map<String, Object> params) {
		StringBuilder sql = new StringBuilder()
		.append(" 		SELECT  ")
		.append(" 		      nvl(sum(nvl(TRUNC(loan.LOAN_AMOUNT,2),0) - nvl(TRUNC(pi.ALREADY_INVEST_AMOUNT,2),0)),0) \"remainAmount\" ")
		.append(" 		  FROM BAO_T_LOAN_INFO loan ")
		.append(" 		 INNER JOIN BAO_T_PROJECT_INVEST_INFO pi ON pi.LOAN_ID = loan.ID ")
		.append(" 		 WHERE 1=1 ")
		.append(" 		   AND loan.LOAN_STATUS='募集中' ")
		.append(" 		   AND loan.NEWER_FLAG='普通标' ")
		.append(" 		   AND decode(loan.LOAN_UNIT,'月',30 , 1) * loan.LOAN_TERM = ? ")
		;
		return jdbcTemplate.queryForObject(sql.toString(), new Object[]{params.get("loanTerm")}, BigDecimal.class);
	}
	
	@Override
	public List<Map<String, Object>> findDispatchLoans(
			Map<String, Object> params) {
		StringBuilder sql = new StringBuilder()
		.append(" WITH t1 AS ( ")
		.append(" 	SELECT t.*, ROWNUM \"sort\" FROM ( ")
		.append(" 		SELECT loan.id \"loanId\" ")
		.append(" 		     , TRUNC(loan.LOAN_AMOUNT,2) - nvl(TRUNC(pi.ALREADY_INVEST_AMOUNT,2),0) \"remainAmount\" ")
		.append(" 		     , sum(TRUNC(loan.LOAN_AMOUNT,2) - nvl(TRUNC(pi.ALREADY_INVEST_AMOUNT,2),0)) OVER(ORDER BY pi.ALREADY_INVEST_SCALE DESC, loan.PUBLISH_DATE) \"cumulationAmount\" ") 
		.append(" 		  FROM BAO_T_LOAN_INFO loan ")
		.append(" 		 INNER JOIN BAO_T_PROJECT_INVEST_INFO pi ON pi.LOAN_ID = loan.ID ")
		.append(" 		 WHERE 1=1 ")
		.append(" 		   AND loan.LOAN_STATUS='募集中' ")
		.append(" 		   AND loan.NEWER_FLAG='普通标' ")
		.append(" 		   AND decode(loan.LOAN_UNIT,'月',30 , 1) * loan.LOAN_TERM = ? ")
		.append(" 	) t ")
		.append(" )  ")
		.append(" SELECT t1.* ") 
		.append("   FROM t1 ")
		.append("  WHERE t1.\"sort\" <= ( SELECT min(\"sort\") FROM t1 WHERE t1.\"cumulationAmount\" >= ?) ")
		;
		return repositoryUtil.queryForMap(sql.toString(), new Object[]{params.get("loanTerm"), params.get("tradeAmount")});
	}

	@Override
	public List<Map<String, Object>> findCanInvestLoanList(
			Map<String, Object> params) {
		String value = paramRepository.findByTypeNameAndParameterName(Constant.SLCF_COM_PARAM, Constant.SLCF_COM_PARAM_05).getValue();
		String loanValue = "'" + value.replaceAll(",", "','") + "'";
		StringBuilder sql = new StringBuilder()
		.append("   select l.id \"loanId\" ")
		.append("   from bao_t_loan_info l ")
		.append("   inner join bao_t_project_invest_info pi on pi.loan_id = l.id ")
		.append("   where l.loan_status = '募集中' ")
		.append("   AND (l.CHANNEL_FLAG != '是' or l.CHANNEL_FLAG is null) ")// 是否用于渠道
		.append("   AND (l.SPECIAL_USERS_FLAG != '是' or l.SPECIAL_USERS_FLAG is null) ")// 优选项目列表不显示特殊用户标的
//		.append("   AND l.IS_RUN_AUTO_INVEST ='Y' ")
		.append("   AND (l.loan_type in (" + loanValue +  ") or (l.IS_RUN_AUTO_INVEST ='Y' and l.loan_type not in (" + loanValue +  "))) ")
		.append("   and l.newer_flag = '普通标' ");
		
		SqlCondition sqlCondition = new SqlCondition(sql, params)	
		.addString("loanTerm", "l.loan_term")
		.addString("loanUnit", "l.loan_unit");
		
		if(!StringUtils.isEmpty((String)params.get("repaymentType")) 
				&& !"不限".equals((String)params.get("repaymentType"))) {
			sqlCondition.addString("repaymentType", "l.repayment_method");
		}
		
		String transferType = (String)params.get("transferType");
		if(!StringUtils.isEmpty(transferType)) {
			switch(transferType) {
			case "可转让" :
				sqlCondition.addSql("and l.seat_term != -1");
				break;
			case "不可转让":
				sqlCondition.addSql("and l.seat_term = -1");
				break;
			}
		}
		sqlCondition.append("   order by pi.already_invest_scale desc, l.publish_date asc ");
		
		return repositoryUtil.queryForMap(sqlCondition.toString(), sqlCondition.toArray());
	}

	@Override
	public Map<String, Object> queryTradeInfoByLoanTermAndLoanUnit(Map<String, Object> params) {
		String value = paramRepository.findByTypeNameAndParameterName(Constant.SLCF_COM_PARAM, Constant.SLCF_COM_PARAM_05).getValue();
		String loanValue = "'" + value.replaceAll(",", "','") + "'";
		StringBuilder sql = new StringBuilder()
		.append("           SELECT   ")
		.append(" 		 		 nvl(sum(nvl(TRUNC(loan.LOAN_AMOUNT,2),0) - nvl(TRUNC(pi.ALREADY_INVEST_AMOUNT,2),0)),0) \"remainAmount\"  ")
		.append("          ,count(1) \"totalcount\" ")
		.append("          ,nvl(max(ld.year_irr),0) \"maxYearRate\" ")
		.append("          ,nvl(min(ld.year_irr),0) \"minYearRate\" ")
		.append(" 		 		 FROM BAO_T_LOAN_INFO loan  ")
		.append(" 		 		 INNER JOIN BAO_T_PROJECT_INVEST_INFO pi ON pi.LOAN_ID = loan.ID  ")
		.append("          inner join bao_t_loan_detail_info ld on ld.loan_id = loan.id ")
		.append(" 		 		 WHERE 1=1  ")
		.append("                  AND (loan.CHANNEL_FLAG != '是' or loan.CHANNEL_FLAG is null) ")// 是否用于渠道
//		.append("                  AND loan.IS_RUN_AUTO_INVEST ='Y' ")
		.append("   AND (loan.loan_type in (" + loanValue +  ") or (loan.IS_RUN_AUTO_INVEST ='Y' and loan.loan_type not in (" + loanValue +  "))) ")
		.append(" 		 		   AND loan.LOAN_STATUS='募集中'  ")
		.append(" 		 		   AND loan.NEWER_FLAG='普通标'  ");
		List<Object> args = new ArrayList<Object>();
		if (!StringUtils.isEmpty(params.get("loanTerm"))) {
			sql.append("and loan.LOAN_TERM = ?");
			args.add(params.get("loanTerm").toString());
		}
		
		if (!StringUtils.isEmpty(params.get("loanUnit"))) {
			sql.append(" and loan.LOAN_UNIT = ?");
			args.add(params.get("loanUnit").toString());
		}
		
		if (!StringUtils.isEmpty(params.get("transferType"))) {
			if ("可转让".equals(params.get("transferType"))) {
				sql.append(" and loan.seat_term = 30");
			}else if ("不可转让".equals(params.get("transferType"))){
				sql.append(" and (loan.seat_term = -1 or loan.seat_term is null)");
			}
		}
		if (!StringUtils.isEmpty(params.get("repaymentType"))&& !"不限".equals(params.get("repaymentType"))) {
			sql.append(" and loan.repayment_method = ?");
			args.add(params.get("repaymentType"));
		}
		List<Map<String, Object>> forMap = repositoryUtil.queryForMap(sql.toString(), args.toArray());
		return forMap.get(0)==null?new HashMap<String, Object>():forMap.get(0);
	}

	@Override
	public Map<String, Object> queryMyDisperseListByGroup(Map<String, Object> params) {
		String paramEntity=paramRepository.findByTypeNameAndParameterName(Constant.SLCF_COM_PARAM, Constant.SLCF_COM_PARAM_05).getValue();
		String loanValue = "'" + paramEntity.replaceAll(",", "','") + "'";
		
		Map<String, Object> result = Maps.newHashMap();
		List<Object> listObject = new ArrayList<Object>();
		listObject.add((String)params.get("onlineTime"));/* 债权转让改造上线日2017-06-01(具体时间这天左右) */
		StringBuilder sql = new StringBuilder()
		.append(" select ")
//		.append("  \"awardAmount\",   ")
				.append(" \"award\" , ")
		.append("  \"investRedPacket\",\"redPacketType\",\"waitingIncome\", \"disperseId\", \"groupBatchNo\", \"expLoanInvestDate\", \"disperseType\",      ")
		.append("  \"creditNo\", \"yearRate\", \"awardRate\",\"minYearRate\",\"maxYearRate\", \"investAmount\", \"typeTerm\",\"investScale\",\"disperseStatus\",    ")
		.append("   \"disperseDate\",\"getIncome\",\"remainderPrincipal\", \"endDate\", \"releaseDate\", \"loanUse\", \"investId\", \"loanTitle\",\"transferApplyId\" ")
		.append("  ,\"newerFlag\", \"loanUnit\",\"investSource\", \"reseverFlag\",\"createDate\" ,\"investMode\",\"productId\",\"investEndDate\" ,\"isGroup\",\"reserveAmount\",\"alreadyInvestAmount\" from (")
		.append(" select")
				.append(" nvl(cai.total_amount, 0) \"award\" , ")
		.append("  i.invest_red_packet as \"investRedPacket\",i.red_packet_type as \"redPacketType\",")
//		.append("  (select trunc(nvl(sum(pu.award_amount),0),2) from bao_t_purchase_award pu where pu.invest_id = i.id and pu.award_status='未结清') as \"awardAmount\", ")
		.append("  (case when l.loan_status in ('流标', '已到期', '提前结清') then 0  ")	 
		.append("             when lta.create_date < to_date(?, 'yyyy-MM-dd HH24:mi:ss') ")/* 债权转让改造上线日2017-06-01(具体时间这天左右) */
		.append("             THEN trunc(decode(i.invest_status, '投资中', i.invest_amount/l.loan_amount, h.hold_scale)*(select nvl(sum(r.repayment_interest), 0) from bao_t_repayment_plan_info r where r.loan_id = l.id and r.repayment_status = '未还款') , 2) ")
		.append("             else ")
		.append("             /* 剩余持比的收益 */ ")
		.append("             CASE WHEN i.INVEST_MODE = '转让' THEN  ")
		.append("                  CASE WHEN to_char(?, 'yyyyMMdd') /* ba.非还款日当天购买(下个还款日-转让时间)/(下个还款日-上个还款日）*持有比例*当期本息 */ ")
		.append("                            < (SELECT min(r.EXPECT_REPAYMENT_DATE) /* 购买时间当期的还款日时间 */ ")
		.append("                                 FROM bao_t_repayment_plan_info r  ")
		.append("                                WHERE r.EXPECT_REPAYMENT_DATE >= i.effect_date ")
		.append("                                  AND r.loan_id = l.id) ")
		.append("                       THEN trunc(h.hold_scale * (select nvl(sum(r.repayment_interest), 0) ")
		.append("                                     from bao_t_repayment_plan_info r  ")
		.append("                                    where r.EXPECT_REPAYMENT_DATE = to_char(d.NEXT_EXPIRY, 'yyyyMMdd') ")
		.append("                                      and r.loan_id = l.id )  ")
		.append("                                * (d.NEXT_EXPIRY - to_date(i.effect_date, 'yyyyMMdd')) ")
		.append("                                / (d.NEXT_EXPIRY - nvl(d.Last_expiry, trunc(l.INVEST_START_DATE))) ")
		.append("                            , 2) ")
		.append("                          + trunc(h.hold_scale *  ")
		.append("                                 (select nvl(sum(r.repayment_interest), 0) ")
		.append("                                    from bao_t_repayment_plan_info r  ")
		.append("                                   where r.EXPECT_REPAYMENT_DATE > to_char(d.NEXT_EXPIRY, 'yyyyMMdd') ")
		.append("                                     and r.loan_id = l.id ) ")
		.append("                            , 2) ")
		.append("                       WHEN to_char(?, 'yyyyMMdd') /* bb.还款日当天购买(未还款的情况下)1/(下个还款日-上个还款日)*持有比例*当期本息  */ ")
		.append("                            >= (SELECT min(r.EXPECT_REPAYMENT_DATE) /* 购买时间当期的还款日时间 */ ")
		.append("                                FROM bao_t_repayment_plan_info r  ")
		.append("                               WHERE r.EXPECT_REPAYMENT_DATE >= i.effect_date ")
		.append("                                 AND r.loan_id = l.id) ")
		.append("                                  AND '未还款' = (SELECT r.REPAYMENT_STATUS ")
		.append("                                                  FROM bao_t_repayment_plan_info r  ")
		.append("                                                 WHERE r.EXPECT_REPAYMENT_DATE = (SELECT min(r.EXPECT_REPAYMENT_DATE) FROM bao_t_repayment_plan_info r WHERE r.EXPECT_REPAYMENT_DATE >= i.effect_date AND r.loan_id = l.id) ")
		.append("                                                   AND r.loan_id = l.id) ")
		.append("                       THEN trunc(h.hold_scale * (select nvl(sum(r.repayment_interest), 0) ")
		.append("                                     from bao_t_repayment_plan_info r  ")
		.append("                                    where r.EXPECT_REPAYMENT_DATE = to_char(d.NEXT_EXPIRY, 'yyyyMMdd') ")
		.append("                                      and r.loan_id = l.id )  ")
		.append("                                  * (d.NEXT_EXPIRY - to_date(i.effect_date, 'yyyyMMdd') + case when to_date(i.effect_date, 'yyyyMMdd') = d.NEXT_EXPIRY then 1 else 0 end) ")
		.append("                                  / (d.NEXT_EXPIRY - nvl(d.Last_expiry, trunc(l.INVEST_START_DATE))) ")
		.append("                            , 2) ")
		.append("                          + trunc(h.hold_scale *  ")
		.append("                                 (select nvl(sum(r.repayment_interest), 0) ")
		.append("                                    from bao_t_repayment_plan_info r  ")
		.append("                                   where r.EXPECT_REPAYMENT_DATE > to_char(d.NEXT_EXPIRY, 'yyyyMMdd') ")
		.append("                                     and r.loan_id = l.id ) ")
		.append("                            , 2) ")
		.append("                       ELSE trunc(h.hold_scale *  ")
		.append("                                 (select nvl(sum(r.repayment_interest), 0) ")
		.append("                                    from bao_t_repayment_plan_info r  ")
		.append("                                   where r.repayment_status = '未还款' ")
		.append("                                     and r.loan_id = l.id)  ")
		.append("                            , 2) END ")
		.append("             ELSE trunc(h.hold_scale *  ")
		.append("                       (select nvl(sum(r.repayment_interest), 0) ")
		.append("                          from bao_t_repayment_plan_info r  ")
		.append("                         where r.repayment_status = '未还款' ")
		.append("                           and r.loan_id = l.id)  ")
		.append("                  , 2)   ")
 		.append(" +(SELECT  trunc(sum(case when l.repayment_method = '等额本息'  ")
		.append("              then cc.REPAYMENT_INTEREST * nvl(l.award_rate,0)/d.year_irr * h.hold_scale         ")
		.append("              when l.repayment_method = '每期还息到期付本'         ")
		.append("              then  (cc.repayment_principal + cc.remainder_principal)* nvl(l.award_rate,0) *30/360*h.hold_scale         ")
		.append("              else  (cc.repayment_principal + cc.remainder_principal)* nvl(l.award_rate,0) *decode(l.loan_unit,'天',1,'月',30)*l.loan_term/360*h.hold_scale                        ")
		.append("              end ) ,2) from  bao_t_repayment_plan_info cc where cc.loan_id = l.id and cc.repayment_status = '未还款' ) ")
		.append("           end+ /* 转让人当月转出持比的收益 */ ")
		.append("             /* i.非还款日[转让时间！=下个还款日](转让时间-上个还款日)/(下个还款日-上个还款日)*转让比例*当期本息 */ ")
		.append("             CASE WHEN TRUNC(?) < d.NEXT_EXPIRY /* (c1)条件包括上个还款日当天已还款之后购买的的转让 */ ")
		.append("                  THEN TRUNC(nvl((SELECT sum(lt.TRADE_SCALE /* 转让比例 */ ")
		.append("                                          * (select nvl(sum(r.repayment_interest), 0)  ")
		.append("                                               from bao_t_repayment_plan_info r  ")
		.append("                                              where r.EXPECT_REPAYMENT_DATE = to_char(d.NEXT_EXPIRY, 'yyyyMMdd') ")
		.append("                                                and r.loan_id = l.id ) ")
		.append("                                          * (to_date(invest.effect_date, 'yyyyMMdd') - nvl(d.Last_expiry, trunc(l.INVEST_START_DATE)))/* 还款日当天已还款之后的数据，下期没有收益 */ ")
		.append("                                          / (d.NEXT_EXPIRY - nvl(d.Last_expiry, trunc(l.INVEST_START_DATE)))) ")
		.append("                                    FROM BAO_T_LOAN_TRANSFER lt, bao_t_wealth_hold_info wh, bao_t_invest_info invest ")
		.append("                                   WHERE invest.id = wh.invest_id ")
		.append("                                     AND wh.id = lt.receive_hold_id ")
		.append("                                     AND to_date(invest.effect_date, 'yyyyMMdd') < ? ")
		.append("                                     AND to_date(invest.effect_date, 'yyyyMMdd') >= nvl(d.Last_expiry, trunc(l.INVEST_START_DATE)) /* 配合(c1) */ ")
		.append("                                     AND lt.SENDER_HOLD_ID = h.id) ")
		.append("                       , 0), 2) ")
		.append("                  /* ii.还款日(未还款的情况下转让时间<还款计划表实际还款时间) (转让时间-上个还款日-1)/(下个还款日-上个还款日)*转让比例*当期本息  */ ")
		.append("                  WHEN TRUNC(?) >= d.NEXT_EXPIRY /* (c2)当期未还款,如果已还款d.NEXT_EXPIRY变为下期还款日期在上面的when条件中 */ ")
		.append("                  THEN TRUNC(nvl((SELECT sum(lt.TRADE_SCALE /* 转让比例 */ ")
		.append("                                          * (select nvl(r.repayment_interest, 0) /* 当期本息 */ ")
		.append("                                               from bao_t_repayment_plan_info r  ")
		.append("                                              where r.EXPECT_REPAYMENT_DATE = to_char(d.NEXT_EXPIRY, 'yyyyMMdd') ")
		.append("                                                and r.loan_id = l.id ) ")
		.append("                                          * (to_date(invest.effect_date, 'yyyyMMdd') - nvl(d.Last_expiry, trunc(l.INVEST_START_DATE)) - case when to_date(invest.effect_date, 'yyyyMMdd') = d.next_expiry then 1 else 0 end) ")
		.append("                                          / (d.NEXT_EXPIRY - nvl(d.Last_expiry, trunc(l.INVEST_START_DATE)))) ")
		.append("                                    FROM BAO_T_LOAN_TRANSFER lt, bao_t_wealth_hold_info wh, bao_t_invest_info invest ")
		.append("                                   WHERE invest.id = wh.invest_id ")
		.append("                                     AND wh.id = lt.receive_hold_id ")
		.append("                                     AND to_date(invest.effect_date, 'yyyyMMdd') <= d.NEXT_EXPIRY ")
		.append("                                     AND to_date(invest.effect_date, 'yyyyMMdd') >= nvl(d.Last_expiry, trunc(l.INVEST_START_DATE)) ")
		.append("                                     AND lt.SENDER_HOLD_ID = h.id) ")
		.append("                       , 0), 2) ")
		.append("                  ELSE 0 END")
		.append("           END)+nvl(pa1.award_amount,0) \"waitingIncome\" ")
		.append("           , l.id              \"disperseId\",     ")
		.append("            ''              \"groupBatchNo\",      ")
		.append("           i.INVEST_DATE              \"expLoanInvestDate\",     ")//体验标购买时间-huifei
		.append("           i.EXPIRE_DATE              \"expLoanExpireDate\",     ")//体验标到期时间
		.append("           l.loan_type       \"disperseType\",     ")
		.append("           l.loan_code       \"creditNo\",     ")
		.append("           d.year_irr        \"yearRate\",     ")
		.append("           nvl(l.award_rate ,0)  \"awardRate\",    ")
		.append("           0  \"minYearRate\",    ")
		.append("           0  \"maxYearRate\",    ")
		.append("           i.invest_amount   \"investAmount\",     ")
		.append("           l.loan_term       \"typeTerm\",     ")
		.append("           trunc(p.already_invest_scale*100, 2)      \"investScale\",     ")
		.append("           decode(i.invest_status,'投资中','出借中',i.invest_status)    \"disperseStatus\",     ")
		.append("           to_char(i.create_date, 'yyyy-MM-dd HH24:mi:ss')     \"disperseDate\",    ")
//		.append("           trunc(decode(i.invest_status, '投资中', i.invest_amount/l.loan_amount, h.hold_scale)*(select nvl(sum(r.repayment_interest), 0) from bao_t_repayment_plan_info r where r.loan_id = l.id and r.repayment_status = '未还款') , 2) \"waitingIncome\",    ")
//		.append("           (select trunc(nvl(sum(rd.trade_amount), 0), 2) from bao_t_account_flow_info f, bao_t_payment_record_info pr, bao_t_payment_record_detail rd where f.relate_primary = i.id and f.id = pr.relate_primary and pr.id = rd.pay_record_id and rd.subject_type in ('还风险金逾期费用', '利息', '逾期费用', '违约金') ) \"getIncome\",   ")
		.append("           nvl(afi.trade_amount,0)+nvl(pa2.award_amount,0) \"getIncome\",  ")
		.append("           TRUNC_AMOUNT_WEB(decode(i.invest_status, '投资中', i.invest_amount/l.loan_amount, h.hold_scale)*d.credit_remainder_principal) \"remainderPrincipal\", ")
		.append("           to_char(to_date(i.expire_date, 'yyyyMMdd'), 'yyyy-MM-dd') \"endDate\",   ")
		.append("           to_char(to_date(i.effect_date, 'yyyyMMdd'), 'yyyy-MM-dd') \"releaseDate\",   ")
		.append("           l.loan_desc         \"loanUse\",  ")
		.append("           i.id                \"investId\",   ")
		.append("           l.LOAN_TITLE        \"loanTitle\",   ")
		.append("           i.TRANSFER_APPLY_ID \"transferApplyId\"  ")
		.append("           , l.newer_flag \"newerFlag\"  ")
		.append("           , l.LOAN_UNIT \"loanUnit\"  ")
		.append("           , case when b.invest_source = 'auto' then '自动投标' when b.invest_source = 'reserveinvest' then '优先投' else '手动投标' end \"investSource\"  ")
		.append("           , CASE WHEN l.LOAN_TYPE in ("+loanValue+") then '否' else '否' end \"reseverFlag\" ")
//性能损耗		.append("           , CASE WHEN (SELECT instr(value, l.LOAN_TYPE) FROM COM_T_PARAM WHERE TYPE='shanLinCaiFuSet' AND PARAMETER_NAME='可投产品') > 0 then '否' else '否' end \"reseverFlag\" ")
		.append("           ,i.create_date \"createDate\" ")
		.append("           ,i.invest_mode \"investMode\" ")
		.append("           ,i.product_id \"productId\" ")
		.append("           ,l.invest_end_date \"investEndDate\" ")
		.append("           ,'否' \"isGroup\" ")//判断是否是集合
		.append("           , 0 \"reserveAmount\" ")
		.append("           , 0 \"alreadyInvestAmount\" ")
		.append("      from BAO_T_INVEST_INFO i ")
		.append("      inner join bao_t_invest_detail_info b on b.invest_id = i.id ")
		.append("      inner join bao_t_loan_info l on l.id = i.loan_id   ")
		.append("       left join bao_t_wealth_hold_info h on h.invest_id = i.id and h.loan_id = i.loan_id ")
		.append("      inner join BAO_T_PROJECT_INVEST_INFO p on p.LOAN_ID = l.id  ")
		.append("      inner join bao_t_loan_detail_info d on d.loan_id = l.id      ")
		.append("       LEFT JOIN BAO_T_LOAN_TRANSFER_APPLY lta ON lta.ID = i.TRANSFER_APPLY_ID ")
		.append("          left join bao_t_loan_reserve_relation lrr on lrr.invest_id = i.id ")
		.append("          left JOIN  bao_t_loan_reserve lr on  lrr.loan_reserve_id = lr.id ")
		.append("       LEFT JOIN ( ")
		.append("                 select f.relate_primary, trunc(nvl(sum(rd.trade_amount), 0), 2) trade_amount ")
		.append("                    from bao_t_account_flow_info     f, ")
		.append("                         bao_t_payment_record_info   pr, ")
		.append("                         bao_t_payment_record_detail rd ")
		.append("                   where f.id = pr.relate_primary ")
		.append("                     and pr.id = rd.pay_record_id ")
		.append("                     and rd.subject_type in ")
		.append("                         ('还风险金逾期费用', '利息', '逾期费用', '违约金','优选项目奖励收益') ")
		.append("                     group by f.relate_primary ")
		.append("            ) afi on afi.relate_primary = i.id ")
				.append(" left join BAO_T_CUST_ACTIVITY_INFO cai on cai.loan_id = l.id and cai.cust_id = ? and i.CUST_ACTIVITY_ID = cai.id ")
				/*yangc 加息券奖励金收益*/
				.append("   left join (select trunc(nvl(sum(pu.award_amount),0),2)as award_amount,pu.invest_id as invest_id from bao_t_purchase_award pu where  pu.award_status='未结清' GROUP BY pu.invest_id ) pa1 ON pa1.invest_id=i.id ")
				.append("   left join (select trunc(nvl(sum(pu.award_amount),0),2)as award_amount,pu.invest_id as invest_id from bao_t_purchase_award pu where  pu.award_status='已结清' GROUP BY pu.invest_id ) pa2 on pa2.invest_id = i.id ")
		.append("      where i.cust_id = ? and i.group_batch_no is null and lr.reserve_status is null ")
		.append("    union all  ");//优选项目集合列表
//		if (!StringUtils.isEmpty(params.get("disperseStatus"))&& "投资中".equals(params.get("disperseStatus").toString())) {
//		 sql.append("   select * from (select   ")
//			.append("         0 as \"investRedPacket\", ")
//			.append("         '' as \"redPacketType\", ")
//			.append("         0 as \"waitingIncome\", ")
//			.append("         ''\"disperseId\",  ")
//			.append("          lr.request_no  \"groupBatchNo\",  ")
//			.append("          ''  \"expLoanInvestDate\",  ")
//			.append("          ''    \"expLoanExpireDate\",  ")
//			.append("          ''     \"disperseType\",  ")
//			.append("          ''      \"creditNo\",  ")
//			.append("          0         \"yearRate\",    ")
//			.append("          nvl(min(li.award_rate) ,0)  \"awardRate\",  ")
//			.append("          nvl(min(ldi.year_irr), 0) \"minYearRate\" , ")
//			.append("          nvl(max(ldi.year_irr), 0)  \"maxYearRate\",  ")
//			.append("          sum(ii.invest_amount)   \"investAmount\" ,   ")
//			.append("          lr.loan_term  \"typeTerm\",  ")
//			.append("           0                  \"investScale\",   ")
//			.append("          '出借中' \"disperseStatus\",  ")
//			.append("          to_char(lr.create_date, 'yyyy-MM-dd HH24:mi:ss')   \"disperseDate\",  ")
//			.append("           0 \"getIncome\",    ")
//			.append("           0 \"remainderPrincipal\",  ")
//			.append("           ''   \"endDate\",  ")
//			.append("           ''   \"releaseDate\",  ")
//			.append("           ''     \"loanUse\",  ")
//		    .append("           ''      \"investId\",   ")
//		    .append("           lr.reserve_title       \"loanTitle\",   ")
//		    .append("           ''                      \"transferApplyId\",  ")
//			.append("           '普通标'                      \"newerFlag\",  ")
//			.append("            lr.loan_unit \"loanUnit\" ,                                ")
//			.append("            '一键出借'      \"investSource\",  ")
//			.append("            ''                      \"reseverFlag\"  ")
//			.append("                        ,ii.create_date \"createDate\"  ")
//			.append("                        ,'加入' \"investMode\"  ")
//			.append("                        ,'' \"productId\"  ")
//			.append("                        ,li.invest_end_date  \"investEndDate\"  ")
//			.append("                        ,'是' \"isGroup\"  ")
//			.append("                        , lr.reserve_amount \"reserveAmount\"  ")
//			.append("                       , lr.already_invest_amount \"alreadyInvestAmount\"  ")
//			.append(" from bao_t_loan_reserve lr ")
//			.append(" left join bao_t_loan_reserve_relation lrr on lrr.loan_reserve_id = lr.id ")
//			.append(" left join bao_t_invest_info ii on ii.id = lrr.invest_id and ii.invest_status = '投资中' ")
//			.append(" left join bao_t_loan_info li on li.id = ii.loan_id ")
//			.append(" left join bao_t_loan_detail_info ldi on ldi.loan_id = li.id ")
//			.append(" group by lr.request_no, lr.reserve_title,  '一键出借', lr.already_invest_amount, lr.loan_term, lr.loan_unit, '出借中' , lr.reserve_amount ,lr.create_date,ii.create_date,li.invest_end_date")
//		    .append(" order by lr.create_date  asc )");
//		};
//		if (!StringUtils.isEmpty(params.get("disperseStatus"))&& "收益中".equals(params.get("disperseStatus").toString())) {
//			sql.append("   select  * from (select   ")
//			.append("         0 as \"investRedPacket\", ")
//			.append("         '' as \"redPacketType\", ")
//			.append("  		 (sum(whi.hold_scale*rpi.repayment_interest) + sum( ")
//			.append("        case when li.repayment_method = '等额本息'    ")
//			.append("         then rpi.REPAYMENT_INTEREST * nvl(li.award_rate,0)/ldi.year_irr * whi.hold_scale         ")
//			.append("         when li.repayment_method = '每期还息到期付本'          ")
//			.append("         then  (rpi.repayment_principal + rpi.remainder_principal)* nvl(li.award_rate,0) *30/360*whi.hold_scale           ")
//			.append("         else  (rpi.repayment_principal + rpi.remainder_principal)* nvl(li.award_rate,0) *decode(li.loan_unit,'天',1,'月',30)*li.loan_term/360*whi.hold_scale                          ")
//			.append("         end  ")
//			.append("        )) \"waitingIncome\",")
//			.append("         ''\"disperseId\",  ")
//			.append("          lr.request_no  \"groupBatchNo\",  ")
//			.append("          ''  \"expLoanInvestDate\",  ")
//			.append("          ''    \"expLoanExpireDate\",  ")
//			.append("          ''     \"disperseType\",  ")
//			.append("          ''      \"creditNo\",  ")
//			.append("          0         \"yearRate\",    ")
//			.append("          nvl(min(li.award_rate) ,0)  \"awardRate\",  ")
//			.append("          nvl(min(ldi.year_irr), 0) \"minYearRate\" , ")
//			.append("          nvl(max(ldi.year_irr), 0)  \"maxYearRate\",  ")
//			.append("          sum(ii.invest_amount)   \"investAmount\" ,   ")
//			.append("          lr.loan_term  \"typeTerm\",  ")
//			.append("           0                  \"investScale\",   ")
//			.append("          '收益中' \"disperseStatus\",  ")
//			.append("          to_char(lr.create_date, 'yyyy-MM-dd HH24:mi:ss')   \"disperseDate\",  ")
//			.append("           0 \"getIncome\",    ")
//			.append("           sum(TRUNC_AMOUNT_WEB(decode(ii.invest_status, '投资中', ii.invest_amount/li.loan_amount, whi.hold_scale)*ldi.credit_remainder_principal)) \"remainderPrincipal\",  ")
//			.append("           ''   \"endDate\",  ")
//			.append("           ''   \"releaseDate\",  ")
//			.append("           ''     \"loanUse\",  ")
//			.append("           ''      \"investId\",   ")
//			.append("           lr.reserve_title       \"loanTitle\",   ")
//			.append("           ''                      \"transferApplyId\",  ")
//			.append("           '普通标'                      \"newerFlag\",  ")
//			.append("            lr.loan_unit \"loanUnit\" ,                                ")
//			.append("            '一键出借'      \"investSource\",  ")
//			.append("            ''                      \"reseverFlag\"  ")
//			.append("                        ,ii.create_date \"createDate\"  ")
//			.append("                        ,'加入' \"investMode\"  ")
//			.append("                        ,'' \"productId\"  ")
//			.append("                        ,li.invest_end_date  \"investEndDate\"  ")
//			.append("                        ,'是' \"isGroup\"  ")
//			.append("                        , lr.reserve_amount \"reserveAmount\"  ")
//			.append("                       , lr.already_invest_amount \"alreadyInvestAmount\"  ")
//			.append(" from bao_t_loan_reserve lr ")
//			.append(" left join bao_t_loan_reserve_relation lrr on lrr.loan_reserve_id = lr.id ")
//			.append(" left join bao_t_invest_info ii on ii.id = lrr.invest_id and ii.invest_status = '收益中' ")
//			.append(" left join bao_t_wealth_hold_info whi on whi.invest_id = ii.id ")
//			.append(" left join bao_t_repayment_plan_info rpi on rpi.loan_id = ii.loan_id and rpi.repayment_status = '未还款' ")
//			.append(" left join bao_t_loan_info li on li.id = ii.loan_id ")
//			.append(" left join bao_t_loan_detail_info ldi on ldi.loan_id = li.id ")
//			.append(" group by lr.request_no, lr.reserve_title,  '一键出借', lr.already_invest_amount, lr.loan_term, lr.loan_unit, '收益中' , lr.reserve_amount,lr.create_date,ii.create_date ,li.invest_end_date ")
//			.append(" order by lr.create_date  asc )");
//		};
//		if (!StringUtils.isEmpty(params.get("disperseStatus"))&& "已结束".equals(params.get("disperseStatus").toString())) {
//			sql.append("  select * from ( select   ")
//			.append("         0 as \"investRedPacket\", ")
//			.append("         '' as \"redPacketType\", ")
//			.append("         0 as \"waitingIncome\", ")
//			.append("         ''\"disperseId\",  ")
//			.append("          lr.request_no  \"groupBatchNo\",  ")
//			.append("          ''  \"expLoanInvestDate\",  ")
//			.append("          ''    \"expLoanExpireDate\",  ")
//			.append("          ''     \"disperseType\",  ")
//			.append("          ''      \"creditNo\",  ")
//			.append("          0         \"yearRate\",    ")
//			.append("          nvl(min(li.award_rate) ,0)  \"awardRate\",  ")
//			.append("          nvl(min(ldi.year_irr), 0) \"minYearRate\",  ")
//			.append("          nvl(max(ldi.year_irr), 0)  \"maxYearRate\",  ")
//			.append("          sum(ii.invest_amount)   \"investAmount\" ,   ")
//			.append("          lr.loan_term  \"typeTerm\",  ")
//			.append("           0                  \"investScale\",   ")
//			.append("          ii.invest_status \"disperseStatus\",  ")
//			.append("          to_char(lr.create_date, 'yyyy-MM-dd HH24:mi:ss')   \"disperseDate\",  ")
//			.append("           sum(nvl(afi.trade_amount,0)) \"getIncome\",    ")
//			.append("           0 \"remainderPrincipal\",  ")
//			.append("           ''   \"endDate\",  ")
//			.append("           ''   \"releaseDate\",  ")
//			.append("           ''     \"loanUse\",  ")
//			.append("           ''      \"investId\",   ")
//			.append("           lr.reserve_title       \"loanTitle\",   ")
//			.append("           ''                      \"transferApplyId\",  ")
//			.append("           '普通标'                      \"newerFlag\",  ")
//			.append("            lr.loan_unit \"loanUnit\" ,                                ")
//			.append("            '一键出借'      \"investSource\",  ")
//			.append("            ''                      \"reseverFlag\"  ")
//			.append("                        ,ii.create_date \"createDate\"  ")
//			.append("                        ,'加入' \"investMode\"  ")
//			.append("                        ,'' \"productId\"  ")
//			.append("                        ,li.invest_end_date  \"investEndDate\"  ")
//			.append("                        ,'是' \"isGroup\"  ")
//			.append("                        , lr.reserve_amount \"reserveAmount\"  ")
//			.append("                       , lr.already_invest_amount \"alreadyInvestAmount\"  ")
//			.append(" from bao_t_loan_reserve lr ")
//			.append(" left join bao_t_loan_reserve_relation lrr on lrr.loan_reserve_id = lr.id ")
//			.append(" left join bao_t_invest_info ii on ii.id = lrr.invest_id and ii.invest_status in ('流标','已到期','已转让','提前结清') ")
//			.append(" left join bao_t_loan_info li on li.id = ii.loan_id ")
//			.append(" left join bao_t_loan_detail_info ldi on ldi.loan_id = li.id ")
//			.append(" left join ( ")
//			.append("     select f.relate_primary, trunc(nvl(sum(rd.trade_amount), 0), 2) trade_amount   ")
//			.append("            from bao_t_account_flow_info     f,   ")
//			.append("                 bao_t_payment_record_info   pr,   ")
//			.append("                 bao_t_payment_record_detail rd   ")
//			.append("           where f.id = pr.relate_primary   ")
//			.append("             and pr.id = rd.pay_record_id   ")
//			.append("             and rd.subject_type in   ")
//			.append("                 ('还风险金逾期费用', '利息', '逾期费用', '违约金','优选项目奖励收益')   ")
//			.append("             group by f.relate_primary   ")
//			.append(" ) afi on  afi.relate_primary = ii.id ")
//			.append(" group by lr.request_no, lr.reserve_title,  '一键出借', lr.already_invest_amount, lr.loan_term, lr.loan_unit, ii.invest_status , lr.reserve_amount, lr.create_date,ii.create_date,li.invest_end_date ")
//			.append(" order by lr.create_date  asc )");
//		};
//		
		sql.append("   select   ")
				.append(" 0 as \"award\" , ")
		.append("         0 as \"investRedPacket\", ")
		.append("         '' as \"redPacketType\", ")
//		.append("         0 as \"awardAmount\",   ")
		.append("        sum((select  ")
		.append("                    nvl(sum(trunc_amount_web(h.hold_scale * r.repayment_interest)),0)    ")
		.append("                    from bao_t_repayment_plan_info r where r.repayment_status = '未还款'   ")
		.append("                    and r.loan_id = l.id ) )    ")
		.append("      +sum((SELECT  trunc(sum(case when l.repayment_method = '等额本息'   ")
		.append("                   then cc.REPAYMENT_INTEREST * nvl(l.award_rate,0)/d.year_irr * h.hold_scale          ")
		.append("                   when l.repayment_method = '每期还息到期付本'          ")
		.append("                   then  (cc.repayment_principal + cc.remainder_principal)* nvl(l.award_rate,0) *30/360*h.hold_scale          ")
		.append("                   else  (cc.repayment_principal + cc.remainder_principal)* nvl(l.award_rate,0) *decode(l.loan_unit,'天',1,'月',30)*l.loan_term/360*h.hold_scale                         ")
		.append("                   end ) ,2) from  bao_t_repayment_plan_info cc where cc.loan_id = l.id and cc.repayment_status = '未还款' ) ) ")
		.append("                            \"waitingIncome\",   ")
		.append("                           ''                      \"disperseId\",  ")
		.append("                         i.group_batch_no  \"groupBatchNo\",  ")
		.append("                           ''                      \"expLoanInvestDate\",  ")
		.append("                           ''                      \"expLoanExpireDate\",  ")
		.append("                           ''                      \"disperseType\",  ")
		.append("                           ''                      \"creditNo\",  ")
		.append("                         0                   \"yearRate\",      ")
		.append("                         nvl(min(l.award_rate) ,0)  \"awardRate\",  ")
		.append("                         min(nvl(d.year_irr,0)) \"minYearRate\",  ")
		.append("                         min(nvl(d.year_irr,0)) \"maxYearRate\",           ")
		.append("                         sum(i.invest_amount)   \"investAmount\" ,  ")
		.append("                                 min(lr.loan_term)    \"typeTerm\",   ")
		.append("                           0                      \"investScale\",  ")
		.append("                        decode(?,'已结束','已结束','投资中','出借中','收益中','收益中') \"disperseStatus\",  ")
		.append("                        to_char(min(lr.create_date), 'yyyy-MM-dd HH24:mi:ss')   \"disperseDate\",    ")
		.append("                sum(nvl(afi.trade_amount,0)) \"getIncome\",   ")
		.append("                         sum(TRUNC_AMOUNT_WEB(decode(i.invest_status, '投资中', i.invest_amount/l.loan_amount, h.hold_scale)*d.credit_remainder_principal))   ")
		.append("                         \"remainderPrincipal\",  ")
		.append("                           to_char(to_date(min(i.expire_date), 'yyyyMMdd'), 'yyyy-MM-dd')   \"endDate\",  ")
		.append("                           to_char(to_date(min(i.effect_date), 'yyyyMMdd'), 'yyyy-MM-dd')   \"releaseDate\",  ")
		.append("                              ''                   \"loanUse\",  ")
		.append("                             ''                    \"investId\",   ")
		.append("                             min(lr.reserve_title) \"loanTitle\", ")
		.append("                           ''                      \"transferApplyId\",  ")
		.append("                           '普通标'                      \"newerFlag\",  ")
		.append("                           min(lr.loan_unit) \"loanUnit\" ,                                ")
		.append("                           '一键出借'      \"investSource\",  ")
		.append("                           ''                      \"reseverFlag\"  ")
		.append("                        ,min(lr.create_date) \"createDate\"  ")
		.append("                        ,'加入' \"investMode\"  ")
		.append("                        ,'' \"productId\"  ")
		.append("                        ,min(l.invest_end_date)  \"investEndDate\"  ")
		.append("                        ,'是' \"isGroup\"  ")
		.append("                        , min(lr.reserve_amount) \"reserveAmount\"  ")
		.append("                       , min(lr.already_invest_amount) \"alreadyInvestAmount\"  ")
		.append("              ")
		.append("                          from BAO_T_INVEST_INFO i   ")
		.append("                        /* inner join bao_t_loan_reserve_relation lrr on lrr.invest_id = i.id*/ ")
		.append("                          inner join bao_t_loan_reserve lr on lr.request_no = i.group_batch_no ")
//		.append("                          inner join bao_t_invest_detail_info b on b.invest_id = i.id   ")
		.append("                          inner join bao_t_loan_info l on l.id = i.loan_id   ")
		.append("                          inner join bao_t_loan_detail_info d on d.loan_id = l.id          ")
		.append("                          left join bao_t_wealth_hold_info h on h.invest_id = i.id and h.loan_id = i.loan_id   ")
		.append("                          /*inner join BAO_T_PROJECT_INVEST_INFO p on p.LOAN_ID = l.id */                                          ")
		.append("                        /* left join bao_t_account_flow_info af on    af.request_no = i.group_batch_no*/             ")
		.append("            LEFT JOIN (  ")
		.append("                      select f.relate_primary, trunc(nvl(sum(rd.trade_amount), 0), 2) trade_amount  ")
		.append("                         from bao_t_account_flow_info     f,  ")
		.append("                              bao_t_payment_record_info   pr,  ")
		.append("                              bao_t_payment_record_detail rd  ")
		.append("                        where f.id = pr.relate_primary  ")
		.append("                          and pr.id = rd.pay_record_id  ")
		.append("                          and rd.subject_type in  ")
		.append("                              ('还风险金逾期费用', '利息', '逾期费用', '违约金','优选项目奖励收益')  ")
		.append("                          group by f.relate_primary  ")
		.append("                 ) afi on afi.relate_primary = i.id  ")
				.append(" left join BAO_T_CUST_ACTIVITY_INFO cai on cai.loan_id = l.id and cai.cust_id = ? and i.CUST_ACTIVITY_ID = cai.id ")
		.append("           where  ")
		.append("           i.cust_id = ?  ")
		.append("           and i.group_batch_no is not null           ")
//		.append("           and i.invest_status = ? ")
		.append("           and decode(i.invest_status,'流标','已结束','已到期','已结束','提前结清','已结束','已转让','已结束',i.invest_status)= ? ")
		.append("                          group by i.group_batch_no    ");
		
		if (!StringUtils.isEmpty(params.get("disperseStatus"))&& "投资中".equals(params.get("disperseStatus").toString())) {
			sql.append("  union all select 0 as \"award\" , 0 as \"investRedPacket\", ")
			.append("                '' as \"redPacketType\", ")
//			.append("                0 as \"awardAmount\",    ")
			.append("                0 as \"waitingIncome\", ")
			.append("                '' \"disperseId\", ")
			.append("                lr.request_no \"groupBatchNo\", ")
			.append("                '' \"expLoanInvestDate\", ")
			.append("                '' \"expLoanExpireDate\", ")
			.append("                '' \"disperseType\", ")
			.append("                '' \"creditNo\", ")
			.append("                0 \"yearRate\", ")
			.append("                nvl(min(li.award_rate), 0) \"awardRate\", ")
			.append("                nvl(min(ldi.year_irr), 0) \"minYearRate\", ")
			.append("                nvl(max(ldi.year_irr), 0) \"maxYearRate\", ")
			.append("                -1 \"investAmount\", ")
			.append("                lr.loan_term \"typeTerm\", ")
			.append("                0 \"investScale\", ")
			.append("                '出借中' \"disperseStatus\", ")
			.append("                to_char(lr.create_date, 'yyyy-MM-dd HH24:mi:ss') \"disperseDate\", ")
			.append("                0 \"getIncome\", ")
			.append("                0 \"remainderPrincipal\", ")
			.append("                '' \"endDate\", ")
			.append("                '' \"releaseDate\", ")
			.append("                '' \"loanUse\", ")
			.append("                '' \"investId\", ")
			.append("                lr.reserve_title \"loanTitle\", ")
			.append("                '' \"transferApplyId\", ")
			.append("                '普通标' \"newerFlag\", ")
			.append("                lr.loan_unit \"loanUnit\", ")
			.append("                '一键出借' \"investSource\", ")
			.append("                '' \"reseverFlag\", ")
			.append("                lr.create_date \"createDate\", ")
			.append("                '加入' \"investMode\", ")
			.append("                '' \"productId\", ")
			.append("                  null  \"investEndDate\",  ")
			.append("                '是' \"isGroup\", ")
			.append("                lr.reserve_amount \"reserveAmount\", ")
			.append("                lr.already_invest_amount \"alreadyInvestAmount\" ")
			.append("           from bao_t_loan_reserve lr ")
			.append("           left join bao_t_loan_reserve_relation lrr on lrr.loan_reserve_id = ")
			.append("                                                        lr.id ")
			.append("           left join bao_t_loan_info li on li.id = lrr.loan_id ")
			.append("           left join bao_t_loan_detail_info ldi on ldi.loan_id = li.id ")
			.append("           where  ")
			.append("           not exists ( ")
			.append("                 select 1 ")
			.append("                 from bao_t_loan_reserve_relation lrr2 ")
			.append("                 where lrr2.loan_reserve_id = lr.id ")
			.append("                 and lrr2.invest_id is not null ")
			.append("           ) ")
			.append("                and   lr.cust_id = ? ")
			.append("                and   lr.reserve_status = '处理中' ")
			.append("          group by lr.request_no, ")
			.append("                   lr.reserve_title, ")
			.append("                   '一键出借', ")
			.append("                   lr.already_invest_amount, ")
			.append("                   lr.loan_term, ")
			.append("                   lr.loan_unit, ")
			.append("                   '出借中', ")
			.append("                   lr.reserve_amount, ")
			.append("                   lr.create_date ")
			;
		}
		
//		.append(" union all")//新手标	
//		.append(" select 0 as \"investRedPacket\", ")
//		.append("        '' as \"redPacketType\", ")
//		.append("       a.\"waitingIncome\" \"waitingIncome\", ")
//		.append("        '' \"disperseId\", ")
//		.append("        '' \"groupBatchNo\", ")
//		.append("        '' \"expLoanInvestDate\", ")
//		.append("        '' \"expLoanExpireDate\", ")
//		.append("        '' \"disperseType\", ")
//		.append("        '' \"creditNo\", ")
//		.append("        0 \"yearRate\", ")
//		.append("        nvl(l.award_rate, 0) \"awardRate\", ")
//		.append("        nvl(ld.year_irr,0)  \"minYearRate\", ")
//		.append("        nvl(ld.year_irr,0)  \"maxYearRate\", ")
//		.append("        case when lr.already_invest_amount = 0 then -1 else a.\"investAmount\" end \"investAmount\", ")
//		.append("        l.loan_term \"typeTerm\", ")
//		.append("        0 \"investScale\", ")
////		.append("        case when lr.already_invest_amount = 0 then '-1' else decode(?,'投资中','出借中','收益中','收益中','已结束') end \"disperseStatus\", ")
////		.append("        nvl(a.\"investStatus\",'出借中')  \"disperseStatus\", ")//如果排队中，没有关联投资表，投资状态为空
//		.append("  		case when lr.reserve_status = '排队中' then nvl(a.\"investStatus\", '出借中') ")
//		.append("             when lr.reserve_status = '新手标预约金额部分超时退回' then nvl(a.\"investStatus\", '已结束')  ")
//		.append("             else a.\"investStatus\"  end \"disperseStatus\", ")
//		.append("         to_char(lr.create_date, 'yyyy-MM-dd HH24:mi:ss') \"disperseDate\", ")
//		.append("        a.\"getIncome\" \"getIncome\", ")
//		.append("        lr.remainder_amount \"remainderPrincipal\", ")
//		.append("        '' \"endDate\", ")
//		.append("        '' \"releaseDate\", ")
//		.append("        '' \"loanUse\", ")
//		.append("        '' \"investId\", ")
//		.append("         l.loan_title \"loanTitle\", ")
//		.append("        '' \"transferApplyId\", ")
//		.append("        '新手标' \"newerFlag\", ")
//		.append("        '天' \"loanUnit\", ")
//		.append("       case when lr.already_invest_amount = 0 then '-1' else '自动投标' end \"investSource\", ")
//		.append("        '' \"reseverFlag\", ")
//		.append("       a.\"createDate\" \"createDate\", ")
//		.append("        '加入' \"investMode\", ")
//		.append("        '' \"productId\", ")
//		.append("        a.\"investEndDate\" \"investEndDate\", ")
//		.append("        '是' \"isGroup\", ")
//		.append("        lr.reserve_amount \"reserveAmount\", ")
//		.append("        case when a.\"investStatus\" in ('流标') then (lr.already_invest_amount - a.\"investAmount\") else a.\"investAmount\" end \"alreadyInvestAmount\" ")
//		.append("       from bao_t_loan_reserve lr  ")
//		.append("       inner join bao_t_loan_info l on lr.loan_id = l.id and l.newer_flag = '新手标集合' ")
//		.append("        left join bao_t_loan_detail_info ld  on ld.loan_id = l.id  ")
//		.append("       left join (select lrr.loan_reserve_id id , nvl(sum(i.invest_amount),0)\"investAmount\", max(ld.year_irr) \"maxYearRate\", ")
//		.append("       min(ld.year_irr)\"minYearRate\" , ")
//		.append("       decode(i.invest_status,'投资中','出借中',i.invest_status) \"investStatus\" , ")
//		.append("       case when ? = '收益中' or ? = '投资中' then sum( i.invest_amount*loan.loan_term*(ld.year_irr+nvl(loan.award_rate,0))/360) ")
//		.append("             else 0 end \"waitingIncome\" ")
//		.append("             , sum(nvl(afi.trade_amount,0)) \"getIncome\" ")
//		.append("             ,min(loan.invest_end_date) \"investEndDate\" ")
//		.append("             ,min(i.create_date)\"createDate\" ")
//		.append("       from bao_t_loan_reserve_relation lrr, ")
//		.append("       bao_t_loan_detail_info ld, ")
//		.append("       bao_t_loan_info loan , ")
//		.append("        bao_t_invest_info i  ")
//		.append("        LEFT JOIN (  ")
//		.append("            select f.relate_primary, trunc(nvl(sum(rd.trade_amount), 0), 2) trade_amount  ")
//		.append("               from bao_t_account_flow_info     f,  ")
//		.append("                    bao_t_payment_record_info   pr,  ")
//		.append("                    bao_t_payment_record_detail rd  ")
//		.append("              where f.id = pr.relate_primary  ")
//		.append("                and pr.id = rd.pay_record_id  ")
//		.append("                and rd.subject_type in  ")
//		.append("                    ('还风险金逾期费用', '利息', '逾期费用', '违约金','优选项目奖励收益')  ")
//		.append("                group by f.relate_primary  ")
//		.append("       ) afi on afi.relate_primary = i.id  ")
//		.append("       where  ")
//		.append("         lrr.invest_id = i.id  ")
//		.append("        and ld.loan_id = i.loan_id ")
//		.append("        and loan.id = i.loan_id ")
//		.append("        and decode(i.invest_status,'流标','已结束','已到期','已结束','提前结清','已结束','已转让','已结束',i.invest_status)= ? ")
//		.append("        group by lrr.loan_reserve_id  ,i.invest_status   ")
//		.append("        ) a on a.id = lr.id ")
//		.append("       where lr.cust_id = ? ");
//		if ("收益中".equals((String) params.get("disperseStatus"))) {
//			sql.append(" and exists( ")
//			.append("     select 1 from bao_t_loan_reserve_relation lrr, bao_t_invest_info i ")
//			.append("     where lrr.invest_id = i.id ")
//			.append("     and i.invest_status = '收益中' ")
//			.append("     and lrr.loan_reserve_id = lr.id ")
//			.append("                              ) ");
//		}else if("已结束".equals((String) params.get("disperseStatus"))){
//			sql.append(" and  lr.reserve_status in( '新手标预约金额部分超时退回','预约成功') ");
//		}else if("投资中".equals((String) params.get("disperseStatus"))) {
//			sql.append(" and  lr.reserve_status in( '排队中','预约成功') ");
////			   .append(" and a.\"investAmount\" !=0");
//		}
		sql.append(" ) where 1=1 ");
		Date date = new Date();
		listObject.add(date);
		listObject.add(date);
		listObject.add(date);
		listObject.add(date);
		listObject.add(date);
		listObject.add(params.get("custId"));
		listObject.add(params.get("custId"));
		listObject.add(params.get("disperseStatus"));
		listObject.add(params.get("custId"));
		listObject.add(params.get("custId"));
		listObject.add(params.get("disperseStatus"));
		if (!StringUtils.isEmpty(params.get("disperseStatus"))&& "投资中".equals(params.get("disperseStatus").toString())) {
			listObject.add(params.get("custId"));
		}
	
		if(!StringUtils.isEmpty(params.get("startDate"))){
			sql.append(" and \"createDate\" >= ? ");
			listObject.add(DateUtils.parseStandardDate(params.get("startDate").toString()));
		}
		if(!StringUtils.isEmpty(params.get("endDate"))){
			sql.append(" and \"createDate\" < ? ");
			listObject.add(DateUtils.getAfterDay(DateUtils.parseStandardDate(params.get("endDate").toString()), 1));
		}
		if (!StringUtils.isEmpty(params.get("productType"))) {
			if ("优选项目".equals((String) params.get("productType"))) {
				sql.append("   and \"investMode\" ='加入' ");
			} else if ("债权转让".equals((String) params.get("productType"))) {
				sql.append("   and \"investMode\" ='转让' ");
			} else if ("优先投".equals((String) params.get("productType"))) {
				sql.append("   and \"productId\" is not null ");
			}
		}
		if (!StringUtils.isEmpty(params.get("disperseStatus"))) {
			if ("已结束".equals((String) params.get("disperseStatus"))) {
				sql.append("   and \"disperseStatus\" in('已到期', '提前结清','流标', '已转让','已结束') ");
				sql.append(" order by  \"investEndDate\" desc nulls LAST, \"releaseDate\" desc nulls LAST, decode(\"disperseStatus\", '已到期', 1, '已转让', 2, '流标', 3, '提前结清', 4, 5)");
			} else {
				if ("投资中".equals(params.get("disperseStatus").toString())) {
					sql.append("   and \"disperseStatus\" = '出借中' ");
				}else {
					sql.append("   and \"disperseStatus\" = ? ");
					listObject.add(params.get("disperseStatus"));
				}
				if("投资中".equals((String) params.get("disperseStatus"))) {
					sql.append(" order by \"createDate\" desc");
				}else if("收益中".equals((String) params.get("disperseStatus"))) {
					sql.append(" order by  \"releaseDate\" desc, \"investEndDate\" asc, \"createDate\" desc");
				}
			}
		}else {
			sql.append(" order by \"createDate\" desc ");
		}
//		
		int start=CommonUtils.emptyToInt(params.get("start"));
		int length=CommonUtils.emptyToInt(params.get("length"));
		Page<Map<String, Object>> queryPage = repositoryUtil.queryForPageMap(sql.toString(), listObject.toArray(), start, length);
		result.put("iTotalDisplayRecords", queryPage.getTotalElements());
		result.put("data", queryPage.getContent());
		return result;
	}

	@Override
	public Map<String, Object> queryMyDisperseListDetailByGroup(Map<String, Object> params) {
		String paramEntity=paramRepository.findByTypeNameAndParameterName(Constant.SLCF_COM_PARAM, Constant.SLCF_COM_PARAM_05).getValue();
		String loanValue = "'" + paramEntity.replaceAll(",", "','") + "'";
		
		Map<String, Object> result = Maps.newHashMap();
		List<Object> listObject = new ArrayList<Object>();
//		listObject.add((String)params.get("onlineTime"));/* 债权转让改造上线日2017-06-01(具体时间这天左右) */
		StringBuilder sql = new StringBuilder()
		.append(" select i.invest_red_packet as \"investRedPacket\",i.red_packet_type as \"redPacketType\", ")
// 		.append(" +(SELECT  trunc(sum(case when l.repayment_method = '等额本息'  ")
//		.append("              then cc.REPAYMENT_INTEREST * nvl(l.award_rate,0)/d.year_irr * h.hold_scale         ")
//		.append("              when l.repayment_method = '每期还息到期付本'         ")
//		.append("              then  (cc.repayment_principal + cc.remainder_principal)* nvl(l.award_rate,0) *30/360*h.hold_scale         ")
//		.append("              else  (cc.repayment_principal + cc.remainder_principal)* nvl(l.award_rate,0) *decode(l.loan_unit,'天',1,'月',30)*l.loan_term/360*h.hold_scale                        ")
//		.append("              end ) ,2) from  bao_t_repayment_plan_info cc where cc.loan_id = l.id and cc.repayment_status = '未还款' ) ")
//		.append("           end+ /* 转让人当月转出持比的收益 */ ")
		.append("   (select ")
		.append("               nvl(sum(trunc_amount_web(h.hold_scale * r.repayment_interest)),0)   ")
		.append("               from bao_t_repayment_plan_info r where r.repayment_status = '未还款'  ")
		.append("               and r.loan_id = l.id )  \"waitingIncome\",    ")
		.append("            l.id              \"disperseId\",     ")
		.append("           i.INVEST_DATE              \"expLoanInvestDate\",     ")//体验标购买时间-huifei
		.append("           i.EXPIRE_DATE              \"expLoanExpireDate\",     ")//体验标到期时间
		.append("           l.loan_type       \"disperseType\",     ")
		.append("           l.loan_code       \"creditNo\",     ")
		.append("           d.year_irr        \"yearRate\",     ")
		.append("           nvl(l.award_rate ,0)  \"awardRate\",    ")
		.append("           i.invest_amount   \"investAmount\",     ")
		.append("           l.loan_term       \"typeTerm\",     ")
		.append("           trunc(p.already_invest_scale*100, 2)      \"investScale\",     ")
		.append("           decode(i.invest_status,'投资中','出借中',i.invest_status)    \"disperseStatus\",     ")
		.append("           to_char(i.create_date, 'yyyy-MM-dd HH24:mi:ss')     \"disperseDate\",    ")
//		.append("           trunc(decode(i.invest_status, '投资中', i.invest_amount/l.loan_amount, h.hold_scale)*(select nvl(sum(r.repayment_interest), 0) from bao_t_repayment_plan_info r where r.loan_id = l.id and r.repayment_status = '未还款') , 2) \"waitingIncome\",    ")
//		.append("           (select trunc(nvl(sum(rd.trade_amount), 0), 2) from bao_t_account_flow_info f, bao_t_payment_record_info pr, bao_t_payment_record_detail rd where f.relate_primary = i.id and f.id = pr.relate_primary and pr.id = rd.pay_record_id and rd.subject_type in ('还风险金逾期费用', '利息', '逾期费用', '违约金') ) \"getIncome\",   ")
		.append("           nvl(afi.trade_amount,0) \"getIncome\",  ")
		.append("           TRUNC_AMOUNT_WEB(decode(i.invest_status, '投资中', i.invest_amount/l.loan_amount, h.hold_scale)*d.credit_remainder_principal) \"remainderPrincipal\", ")
		.append("           to_char(to_date(i.expire_date, 'yyyyMMdd'), 'yyyy-MM-dd') \"endDate\",   ")
		.append("           to_char(to_date(i.effect_date, 'yyyyMMdd'), 'yyyy-MM-dd') \"releaseDate\",   ")
		.append("           l.loan_desc         \"loanUse\",  ")
		.append("           i.id                \"investId\",   ")
		.append("           l.LOAN_TITLE        \"loanTitle\",   ")
		.append("           i.TRANSFER_APPLY_ID \"transferApplyId\"  ")
		.append("           , l.newer_flag \"newerFlag\"  ")
		.append("           , l.LOAN_UNIT \"loanUnit\"  ")
		.append("           , case when b.invest_source = 'auto' then '自动投标' when b.invest_source = 'reserveinvest' then '优先投' else '手动投标' end \"investSource\"  ")

		.append("           , CASE WHEN l.LOAN_TYPE in ("+loanValue+") then '否' else '否' end \"reseverFlag\" ")
//性能损耗		.append("           , CASE WHEN (SELECT instr(value, l.LOAN_TYPE) FROM COM_T_PARAM WHERE TYPE='shanLinCaiFuSet' AND PARAMETER_NAME='可投产品') > 0 then '否' else '否' end \"reseverFlag\" ")
		.append("      from BAO_T_INVEST_INFO i ")
		.append("      inner join bao_t_invest_detail_info b on b.invest_id = i.id ")
		.append("      inner join bao_t_loan_info l on l.id = i.loan_id   ")
		.append("       left join bao_t_wealth_hold_info h on h.invest_id = i.id and h.loan_id = i.loan_id ")
		.append("      inner join BAO_T_PROJECT_INVEST_INFO p on p.LOAN_ID = l.id  ")
		.append("      inner join bao_t_loan_detail_info d on d.loan_id = l.id      ")
		.append("       LEFT JOIN BAO_T_LOAN_TRANSFER_APPLY lta ON lta.ID = i.TRANSFER_APPLY_ID ")
		.append("       LEFT JOIN ( ")
		.append("                 select f.relate_primary, trunc(nvl(sum(rd.trade_amount), 0), 2) trade_amount ")
		.append("                    from bao_t_account_flow_info     f, ")
		.append("                         bao_t_payment_record_info   pr, ")
		.append("                         bao_t_payment_record_detail rd ")
		.append("                   where f.id = pr.relate_primary ")
		.append("                     and pr.id = rd.pay_record_id ")
		.append("                     and rd.subject_type in ")
		.append("                         ('还风险金逾期费用', '利息', '逾期费用', '违约金','优选项目奖励收益') ")
		.append("                     group by f.relate_primary ")
		.append("            ) afi on afi.relate_primary = i.id ")
		.append("      where i.cust_id = ?  and i.group_batch_no is not null")//凡是集合 批次号都不能为空
		.append("      and i.group_batch_no = ?");
		
//		Date date = new Date();
//		listObject.add(date);

		listObject.add(params.get("custId"));
//		listObject.add(params.get("disperseStatus"));
		listObject.add(params.get("groupBatchNo"));
		
		if(!StringUtils.isEmpty(params.get("startDate"))){
			sql.append(" and i.CREATE_DATE >= ? ");
			listObject.add(DateUtils.parseStandardDate(params.get("startDate").toString()));
		}
		if(!StringUtils.isEmpty(params.get("endDate"))){
			sql.append(" and i.CREATE_DATE < ? ");
			listObject.add(DateUtils.getAfterDay(DateUtils.parseStandardDate(params.get("endDate").toString()), 1));
		}
		if (!StringUtils.isEmpty(params.get("productType"))) {
			if ("优选项目".equals((String) params.get("productType"))) {
				sql.append("   and i.invest_mode ='加入' ");
			} else if ("债权转让".equals((String) params.get("productType"))) {
				sql.append("   and i.invest_mode ='转让' ");
			} else if ("优先投".equals((String) params.get("productType"))) {
				sql.append("   and i.product_id is not null ");
			}
		}
		if (!StringUtils.isEmpty(params.get("disperseStatus"))) {
			if ("已结束".equals((String) params.get("disperseStatus"))) {
				sql.append("   and i.invest_status in('已到期', '提前结清','流标', '已转让') ");
				sql.append(" order by l.invest_end_date desc nulls LAST, i.effect_date desc nulls LAST, decode(i.invest_status, '已到期', 1, '已转让', 2, '流标', 3, '提前结清', 4, 5)");
			} else {
				sql.append("   and i.invest_status = ? ");
				listObject.add(params.get("disperseStatus"));
				if("投资中".equals((String) params.get("disperseStatus"))) {
					sql.append(" order by i.create_date desc");
				}else if("收益中".equals((String) params.get("disperseStatus"))) {
					sql.append(" order by  i.effect_date desc, l.invest_end_date asc, i.create_date desc");
				}
			}
		}else {
			sql.append(" order by i.create_date desc ");
		}
		
		int start=CommonUtils.emptyToInt(params.get("start"));
		int length=CommonUtils.emptyToInt(params.get("length"));
		Page<Map<String, Object>> queryPage = repositoryUtil.queryForPageMap(sql.toString(), listObject.toArray(), start, length);
		result.put("iTotalDisplayRecords", queryPage.getTotalElements());
		result.put("data", queryPage.getContent());
		return result;
	
	}

}
