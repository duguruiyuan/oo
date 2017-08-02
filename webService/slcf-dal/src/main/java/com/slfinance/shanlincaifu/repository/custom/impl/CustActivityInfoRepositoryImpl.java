/** 
 * @(#)CustActivityRepositoryImpl.java 1.0.0 2015年5月19日 下午3:36:46  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */
package com.slfinance.shanlincaifu.repository.custom.impl;

import com.google.common.collect.Lists;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.ActivityInfoEntity;
import com.slfinance.shanlincaifu.entity.CustActivityDetailEntity;
import com.slfinance.shanlincaifu.entity.CustActivityInfoEntity;
import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.CustActivityInfoRepositoryCustom;
import com.slfinance.shanlincaifu.utils.*;
import com.slfinance.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * @author caoyi
 * @version $Revision:1.0.0, $Date: 2015年5月19日 下午3:36:46 $
 */
@Repository
public class CustActivityInfoRepositoryImpl implements
		CustActivityInfoRepositoryCustom {
	@Autowired
	private RepositoryUtil repositoryUtil;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@PersistenceContext
	private EntityManager manager;

	/**
	 * 善林包体验金
	 */
	@Override
	public Map<String, Object> findExperienceGold(Map<String, Object> param) {
		Map<String, Object> result = new HashMap<String, Object>();
		List<Object> listObject = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		sb.append("select /*+ PARALLEL(BAO_T_ACCOUNT_FLOW_INFO 2) */ sum(t.TOTAL_AMOUNT)  \"experienceGoldCount\",sum(t.USABLE_AMOUNT)   \"experienceGoldUnUsed\",  sum(t.TOTAL_AMOUNT) - sum(t.USABLE_AMOUNT) \"experienceGoldUsed\" , t.cust_id \"custId\", tt.login_name \"custName\" ,tt.cust_name \"realName\",   tt.credentials_code \"credentialsCode\",  TTT.trade_amount ");
		sb.append(" \"experienceGoldIncome\",tt.create_date  from BAO_T_CUST_INFO tt,BAO_T_CUST_ACTIVITY_INFO t ");
		sb.append(", ( SELECT sum(trade_amount)as trade_amount, cust_id FROM BAO_T_ACCOUNT_FLOW_INFO  WHERE TRADE_TYPE = '体验宝收益'  group by cust_id  ) TTT");
		sb.append(" where t.cust_id=tt.id AND tt.id = TTT.cust_id(+) AND    T.REWARD_SHAPE ='");

		sb.append(Constant.REAWARD_SPREAD_01);
		sb.append("' ");

		if (!StringUtils.isEmpty(param.get("custName"))) {
			sb.append(" AND TT.LOGIN_NAME like ? ");
			listObject.add("%" + param.get("custName") + "%");
		}
		if (!StringUtils.isEmpty(param.get("realName"))) {
			sb.append(" AND TT.CUST_NAME=?");
			listObject.add(param.get("realName"));
		}
		if (!StringUtils.isEmpty(param.get("credentialsCode"))) {
			sb.append(" AND TT.CREDENTIALS_CODE=?");
			listObject.add(param.get("credentialsCode"));
		}
		sb.append(" group by t.cust_id, tt.login_name,tt.cust_name,tt.credentials_code,tt.create_date,TTT.trade_amount order by tt.create_date desc");

		int pageNum = Integer.valueOf(param.get("start") + "");
		int pageSize = Integer.valueOf(param.get("length") + "");

		Page<Map<String, Object>> resultPage = repositoryUtil.queryForPageMap(
				sb.toString(), listObject.toArray(), pageNum, pageSize);
		result.put("iTotalDisplayRecords", resultPage.getTotalElements());
		result.put("data", resultPage.getContent());
		result.put("totalInfo", findExperienceGoldAudit(param));
		return result;
	}

	/**
	 * 总统计
	 */
	public Map<String, Object> findExperienceGoldAudit(Map<String, Object> param) {
		StringBuffer sbTotal = new StringBuffer(
				"SELECT  SUM(T.TOTAL_AMOUNT) AS EXPERIENCEGOLDCOUNT,SUM(T.USABLE_AMOUNT) AS EXPERIENCEGOLDUNUSED,SUM(T.TOTAL_AMOUNT) - SUM(T.USABLE_AMOUNT) AS EXPERIENCEGOLDINCOME FROM   BAO_T_CUST_INFO TT, BAO_T_CUST_ACTIVITY_INFO T  WHERE T.CUST_ID = TT.ID");
		sbTotal.append(" AND T.REWARD_SHAPE = '");
		sbTotal.append(Constant.REAWARD_SPREAD_01);
		sbTotal.append("' ");

		StringBuffer sb = new StringBuffer(
				"SELECT SUM(TRADE_AMOUNT) as experienceGoldIncomeEnd " );
		sb.append(" FROM BAO_T_ACCOUNT_FLOW_INFO ");
		sb.append(" WHERE TRADE_TYPE = '体验宝收益' ");
		sb.append(" and cust_id in ( select id from BAO_T_CUST_INFO TT where 1 = 1 ");
		List<Object> objList = new ArrayList<Object>();
		if (!StringUtils.isEmpty(param.get("custName"))) {
			sb.append(" AND TT.LOGIN_NAME like ? ");
			sbTotal.append(" AND TT.LOGIN_NAME like ? ");
			objList.add("%" + param.get("custName") + "%");
		}
		if (!StringUtils.isEmpty(param.get("realName"))) {
			sb.append(" AND TT.CUST_NAME=?");
			sbTotal.append(" AND TT.CUST_NAME=?");
			objList.add(param.get("realName"));
		}
		if (!StringUtils.isEmpty(param.get("credentialsCode"))) {
			sb.append(" AND TT.CREDENTIALS_CODE=?");
			sbTotal.append(" AND TT.CREDENTIALS_CODE=?");
			objList.add(param.get("credentialsCode"));
		}
		sb.append(" ) ");
		List<Map<String, Object>> sbTotalList = repositoryUtil.queryForMap(
				sbTotal.toString(), objList.toArray());
		Map<String, Object> result = sbTotalList.get(0);

		List<Map<String, Object>> list = repositoryUtil.queryForMap(
				sb.toString(), objList.toArray());
		result.putAll(list.get(0));
		return result;
	}

	/**
	 * 体验金总奖励
	 * 
	 * @param param
	 * @return
	 */
	public Map<String, Object> findExperienceGoldById(Map<String, Object> param) {
		List<Object> objList = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer(
				"SELECT SUM(T.TOTAL_AMOUNT)  \"ExperienceGoldCount\" ,SUM(T.USABLE_AMOUNT)   \"ExperienceGoldUnUsed\" ,  SUM(T.TOTAL_AMOUNT) - SUM(T.USABLE_AMOUNT)  \"ExperienceGoldUsed\" ");
		sb.append(" FROM BAO_T_CUST_INFO TT,BAO_T_CUST_ACTIVITY_INFO T WHERE TT.ID=T.CUST_ID AND T.REWARD_SHAPE ='");
		sb.append(Constant.REAWARD_SPREAD_01);
		sb.append("'");
		
		if (!StringUtils.isEmpty(param.get("custId"))) {
			sb.append(" AND TT.ID=?");
			objList.add(param.get("custId"));
		}
		
		List<Map<String, Object>> list = repositoryUtil.queryForMap(
				sb.toString(), objList.toArray());
		Map<String, Object> result = list.get(0);
		objList = new ArrayList<Object>();
		sb = new StringBuffer("SELECT SUM(TRADE_AMOUNT) \"ExperienceGoldIncome\" FROM BAO_T_ACCOUNT_FLOW_INFO TTT WHERE TTT.TRADE_TYPE= ? ");
		objList.add(SubjectConstant.TRADE_FLOW_TYPE_03);
		if (!StringUtils.isEmpty(param.get("custId"))) {
			sb.append(" AND TTT.cust_id=?");
			objList.add(param.get("custId"));
		}
		List<Map<String, Object>> list2 = repositoryUtil.queryForMap(sb.toString(), objList.toArray());
		result.putAll(list2.get(0));
		return result;
	}

	/**
	 * 获取体验金信息
	 * 
	 * @return
	 */
	public List<Map<String, Object>> findExperienceGoldDetail(
			Map<String, Object> param) {

		List<Object> objList = new ArrayList<Object>();

		StringBuffer sb = new StringBuffer(
				"SELECT T.TOTAL_AMOUNT AS EXPERIENCEGOLDCOUNT,T.USABLE_AMOUNT AS EXPERIENCEGOLDUNUSED,(T.TOTAL_AMOUNT - T.USABLE_AMOUNT) AS EXPERIENCEGOLDINCOME,T.ACTIVITY_SOURCE AS EXPERIENCEGOLDSOURCE,T.TRADE_STATUS AS EXPERIENCEGOLDSTATUS, T.CREATE_DATE  AS EXPERIENCEGOLDDATE FROM   BAO_T_CUST_ACTIVITY_INFO  T  ");
		sb.append("  WHERE T.reward_shape='" + Constant.REAWARD_SPREAD_01
				+ "' ");
		if (!StringUtils.isEmpty(param.get("custId"))) {
			sb.append(" AND T.CUST_ID=?");
			objList.add(param.get("custId"));
		}
		
		sb.append(" order by T.CREATE_DATE desc " );

		return repositoryUtil.queryForMap(sb.toString(), objList.toArray());
	}

	/**
	 * 推荐奖励信息
	 */
	public Map<String, Object> findRewardById(Map<String, Object> param) {

		StringBuilder sql = new StringBuilder()
				.append(" select nvl(sum(b.trade_amount),0) as \"rewardCount\", ")
				.append("        nvl(sum(case ")
				.append("              when b.trade_status != '"
						+ Constant.USER_ACTIVITY_TRADE_STATUS_05 + "' then ")
				.append("               b.trade_amount ")
				.append("              else ")
				.append("               0 ")
				.append("            end),0) as \"rewardNotSettle\", ")
				.append("        nvl(sum(case ")
				.append("              when b.trade_status = '"
						+ Constant.USER_ACTIVITY_TRADE_STATUS_05 + "' then ")
				.append("               b.trade_amount ")
				.append("              else ")
				.append("               0 ")
				.append("            end),0) as \"rewardSettle\" ")
				.append("   from BAO_T_CUST_ACTIVITY_INFO   a, ")
				.append("        bao_t_cust_activity_detail b ")
				.append("  where a.id = b.cust_activity_id ")
				.append("    and activity_id = '"
						+ Constant.ACTIVITY_ID_REGIST_02 + "' ")
				.append("    and b.cust_id = ? ");

		List<Object> objList = new ArrayList<Object>();
		objList.add(param.get("custId"));

		List<Map<String, Object>> list = repositoryUtil.queryForMap(
				sql.toString(), objList.toArray());
		return list.get(0);

		// HashMap<String, Object> result = new HashMap<String, Object>();
		// // 推荐总奖励
		// result.put("rewardCount", total.toString());
		// // 未结算奖励
		// result.put("rewardNotSettle", process.toString());
		// // 已结算奖励
		// result.put("rewardSettle", total.subtract(process).toString());
		// return result;
	}

	/**
	 * 获取我的推荐记录
	 * 
	 * @author caoyi
	 * @date 2015年05月19日 上午11:49:25
	 * @param Map
	 *            <String, Object>
	 * @return Page<Map<String, Object>>
	 */
	@Override
	public Page<Map<String, Object>> findCustRecommendList(
			Map<String, Object> param) {
//		StringBuilder sql = new StringBuilder()
//				.append(" select rownum as \"orderNo\", ")
//				.append("        a.login_name as \"loginName\", ")
//				.append("        a.create_date as \"registDate\", ")
//				.append("        a.realname as \"realName\", ")
//				.append("        case ")
//				.append("          when d.cust_id is null then ")
//				.append("           '否' ")
//				.append("          else ")
//				.append("           '是' ")
//				.append("        end as \"invest\", ")
//				.append("        a.spread_level as \"spreadLevel\", ")
//				.append("        case ")
//				.append("          when b.cust_id is null then ")
//				.append("           '0' ")
//				.append("          else ")
//				.append("           c.value ")
//				.append("        end as \"awardAmount\", ")
//				.append("        case ")
//				.append("          when e.cust_id is null then ")
//				.append("           0 ")
//				.append("          else ")
//				.append("            e.total_amount ")
//				.append("        end as \"experienceAmount\", ")
//				.append("        case ")
//				.append("          when (b.cust_id is null and e.cust_id is null) then ")
//				.append("           '" + Constant.USER_ACTIVITY_TRADE_STATUS_06
//						+ "' ")
//				.append("          when e.cust_id is not null then ")
//				.append("           '" + Constant.USER_ACTIVITY_TRADE_STATUS_05
//						+ "' ")
//				.append("          else ")
//				.append("           b.trade_status ")
//				.append("        end as \"tradeStatus\" ")
//				.append("   from (select t.id, ")
//				.append("                t.login_name, ")
//				.append("                t.create_date, ")
//				.append("                case ")
//				.append("                  when t.credentials_code is null then ")
//				.append("                   '否' ")
//				.append("                  else ")
//				.append("                   '是' ")
//				.append("                end as realname, ")
//				.append("                t.spread_level - ")
//				.append("                (select to_number(spread_level) ")
//				.append("                   from bao_t_cust_info ")
//				.append("                  where id = ? ) as spread_level ")
//				.append("           from bao_t_cust_info t ")
//				.append("          where t.query_permission like ")
//				.append("                (select query_permission ")
//				.append("                   from bao_t_cust_info ")
//				.append("                  where id = ? ) || '%' ")
//				.append("            and id != ? ")
//				.append("            and to_number(t.spread_level) <= ")
//				.append("                (select to_number(spread_level) ")
//				.append("                   from bao_t_cust_info ")
//				.append("                  where id = ? ) + "
//						+ Constant.AWARD_SPREAD_LEVEL + ") a, ")
//				.append("        (select a.cust_id, b.trade_status ")
//				.append("           from BAO_T_CUST_ACTIVITY_INFO a, bao_t_cust_activity_detail b ")
//				.append("          where a.id = b.cust_activity_id ")
//				.append("            and activity_id ='"
//						+ Constant.ACTIVITY_ID_REGIST_02 + "' ")
//				.append("            and b.cust_id = ? ) b, ")
//				.append("        (select t.parameter_name, t.value ")
//				.append("           from com_t_param t ")
//				.append("          where t.type = ? ) c, ")
//				.append("        (select cust_id, min(t.create_date) as create_date ")
//				.append("           from bao_t_trade_flow_info t ")
//				.append("          where t.trade_type='充值' and t.trade_status='处理成功' ")
//				.append("          group by t.cust_id) d, ")
//				.append("        (select a.quilt_cust_id as cust_id, '已结算' trade_status,a.total_amount ")
//				.append("           from BAO_T_CUST_ACTIVITY_INFO   a ")
//				.append(String.format(" where activity_id in ('%s', '%s') ", Constant.ACTIVITY_ID_REGIST_03, Constant.ACTIVITY_ID_REGIST_07))
//				.append("           and a.cust_id = ?) e ")
//				.append("  where a.id = b.cust_id(+) ")
//				.append("    and a.id = d.cust_id(+) ")
//				.append("    and a.id = e.cust_id(+) ")
//				.append("    and a.spread_level = c.parameter_name ");
//
//		StringBuffer whereSqlString = new StringBuffer();
//		List<Object> objList = new ArrayList<Object>();
//		objList.add(param.get("custId"));
//		objList.add(param.get("custId"));
//		objList.add(param.get("custId"));
//		objList.add(param.get("custId"));
//		objList.add(param.get("custId"));
//		objList.add(ParamsNameConstant.ACTIVITY_RECOMMEND);
//		objList.add(param.get("custId"));
//		if (!StringUtils.isEmpty(param.get("spreadLevel"))) {
//			whereSqlString.append(" and a.spread_level = ?");
//			objList.add(param.get("spreadLevel"));
//		}
//		if (!StringUtils.isEmpty(param.get("registDateBegin"))) {
//			whereSqlString.append(" and a.create_date >= ?");
//			objList.add(DateUtils.parseStandardDate((String) param
//					.get("registDateBegin")));
//		}
//		if (!StringUtils.isEmpty(param.get("registDateEnd"))) {
//			whereSqlString.append(" and a.create_date <= ?");
//			objList.add(DateUtils.getEndDate(DateUtils
//					.parseStandardDate((String) param.get("registDateEnd"))));
//		}
//		if (!StringUtils.isEmpty(param.get("realName"))) {
//			whereSqlString.append(" and a.realname = ? ");
//			objList.add(param.get("realName"));
//		}
//		if (!StringUtils.isEmpty(param.get("invest"))) {
//			if (param.get("invest").equals("是"))
//				whereSqlString.append(" and d.cust_id is not null ");
//			if (param.get("invest").equals("否"))
//				whereSqlString.append(" and d.cust_id is null ");
//		}
//		if (!StringUtils.isEmpty(param.get("tradeStatus"))) {
//			if (param.get("tradeStatus").equals("是"))
//				whereSqlString.append(" and (b.trade_status = '"
//						+ Constant.USER_ACTIVITY_TRADE_STATUS_05 + "' or e.trade_status= '"+ Constant.USER_ACTIVITY_TRADE_STATUS_05 + "')");
//			if (param.get("tradeStatus").equals("否"))
//				whereSqlString.append(" and ((b.trade_status = '"
//						+ Constant.USER_ACTIVITY_TRADE_STATUS_06
//						+ "' or b.trade_status is null) and "
//						+ " (e.trade_status = '"
//						+ Constant.USER_ACTIVITY_TRADE_STATUS_06+"'"
//					    + " or e.trade_status is null)) ");
//		}
//		whereSqlString.append(" order by spread_level,a.create_date ");
		StringBuilder sql = new StringBuilder()
		.append("  select t.login_name \"loginName\", substr(t.mobile, 0, 3) || '***' || substr(t.mobile, 8) \"mobile\",  ")
		.append("         t.create_date \"registDate\", nvl(t.cust_source, 'web') \"appSource\" ")
		.append("         , CASE WHEN t.CREDENTIALS_CODE IS NOT NULL ")
		.append("                THEN (SELECT CASE WHEN count(1) > 0 THEN '已投资' ELSE '已认证' END ")
		.append("                        FROM BAO_T_INVEST_INFO i, BAO_T_LOAN_INFO l ")
		.append("                       WHERE ((i.INVEST_MODE = '加入' AND l.NEWER_FLAG = '普通标') OR (i.INVEST_MODE = '转让')) ")
		.append("                         AND l.ID = i.LOAN_ID ")
		.append("                         AND i.INVEST_STATUS IN ( ")
		.append("                             '到期赎回', ")
		.append("                             '收益中', ")
		.append("                             '提前结清', ")
		.append("                             '提前赎回', ")
//		.append("                             '投资中', ") // 生效的才算已投资
		.append("                             '已到期', ")
		.append("                             '已转让')  ")
		.append("                             AND i.CUST_ID = t.ID) ")
		.append("                ELSE '已注册' ")
		.append("         END \"userStatus\" ")
		.append("  from bao_t_cust_info t ")
		.append("  where t.invite_origin_id = ? ");
		
		List<Object> objList = new ArrayList<Object>();
		SqlCondition sqlCondition = new SqlCondition(sql, param, objList);
		objList.add(param.get("custId"));
		//isBackCash为true是好友返现活动
		if (!StringUtils.isEmpty(param.get("isBackCash"))&&(Boolean)param.get("isBackCash")) {
			sqlCondition.append("            and exists ")
			.append("          (select * ")
			.append("                   from BAO_T_INVEST_INFO invest, bao_T_loan_info loan ")
			.append("                  where invest.loan_id = loan.id ")
			.append("                    and invest.cust_id = t.id ")
			.append("                    and loan.newer_flag != '新手标' ")
			.append("                    and invest.invest_mode != '转让' ")
			.append("                    and loan.loan_status in ('正常', '已到期') ")
			.addBeginDate("startDate", "invest.create_date")
			.addEndDate("endDate", "invest.create_date")
			.addSql(" ) ");
		}
//		sqlCondition.addBeginDate("startDate", "t.create_date")
//		.addEndDate("endDate", "t.create_date")
		sqlCondition.addString("memo2", "t.memo2");
		sql.append(" order by t.create_date asc");
		return repositoryUtil.queryForPageMap(
				sqlCondition.toString(), objList.toArray(),
				(int) param.get("start"), (int) param.get("length"));
	}

	/**
	 * 获取我的体验金记录
	 * 
	 * @author caoyi
	 * @date 2015年05月19日 下午14:36:25
	 * @param Map
	 *            <String, Object>
	 * @return Page<Map<String, Object>>
	 */
	@Override
	public Page<Map<String, Object>> findCustExperienceList(
			Map<String, Object> param) {
		StringBuilder sql = new StringBuilder()
				.append(" select rownum               as \"orderNo\", ")
				.append("        t.total_amount       as \"receiveAmount\", ")
				.append("        t.total_amount-t.usable_amount      as \"usableAmount\", ")
				.append("        t.activity_source    as \"source\", ")
				.append("        t.trade_status       as \"tradeStatus\", ")
				.append("        t.create_date        as \"receiveDate\" ")
				.append("   from BAO_T_CUST_ACTIVITY_INFO t ")
				.append("  where t.reward_shape='" + Constant.REAWARD_SPREAD_01
						+ "' %s");

		StringBuffer whereSqlString = new StringBuffer();
		List<Object> objList = new ArrayList<Object>();
		if (!StringUtils.isEmpty(param.get("custId"))) {
			whereSqlString.append(" and t.cust_id = ?");
			objList.add(param.get("custId"));
		}
		if (!StringUtils.isEmpty(param.get("source"))) {
			whereSqlString.append(" and t.activity_source = ?");
			objList.add(param.get("source"));
		}
		if (!StringUtils.isEmpty(param.get("tradeStatus"))) {
			whereSqlString.append(" and t.trade_status = ?");
			objList.add(param.get("tradeStatus"));
		}
		if (!StringUtils.isEmpty(param.get("receiveDateBegin"))) {
			whereSqlString.append(" and t.create_date >= ?");
			objList.add(DateUtils.parseStandardDate((String) param
					.get("receiveDateBegin")));
		}
		if (!StringUtils.isEmpty(param.get("receiveDateEnd"))) {
			whereSqlString.append(" and t.create_date <= ?");
			objList.add(DateUtils.getEndDate(DateUtils
					.parseStandardDate((String) param.get("receiveDateEnd"))));
		}
		whereSqlString.append(" order by t.create_date ");
		return repositoryUtil.queryForPageMap(
				String.format(sql.toString(), whereSqlString.toString()),
				objList.toArray(), (int) param.get("start"),
				(int) param.get("length"));
	}

	@Override
	public List<Map<String, Object>> caclCustActivityDetail(
			Map<String, Object> map) {
		StringBuilder sql = new StringBuilder()
				.append(" SELECT  ")
				.append("        T.ID \"custId\",  ")
				.append("        (SELECT S.VALUE FROM COM_T_PARAM S WHERE S.TYPE = ? AND S.PARAMETER_NAME = TO_CHAR(TO_NUMBER(?) - TO_NUMBER(T.SPREAD_LEVEL))) \"tradeAmount\" ")
				.append(" FROM BAO_T_CUST_INFO T  ")
				.append(" WHERE T.ID != ? ")
				.append(" AND TO_NUMBER(?) - TO_NUMBER(SPREAD_LEVEL) <= TO_NUMBER(?) ")
				// .append(" AND T.QUERY_PERMISSION LIKE ? ")
				.append(" START WITH ID = ?  ")
				.append(" CONNECT BY ID = PRIOR INVITE_ORIGIN_ID ");

		List<Object> objList = new ArrayList<Object>();
		objList.add(ParamsNameConstant.ACTIVITY_RECOMMEND);
		objList.add((String) map.get("custSpreadLevel"));
		objList.add((String) map.get("custId"));
		objList.add((String) map.get("custSpreadLevel"));
		objList.add(Constant.AWARD_SPREAD_LEVEL);
		// objList.add(((String)map.get("queryPermission")).substring(0, 10) +
		// "%");
		objList.add((String) map.get("custId"));

		return repositoryUtil.queryForMap(sql.toString(), objList.toArray());
	}

	

	@Override
	public BigDecimal getExpAmountByCustId(String custId) throws SLException {
		StringBuffer sql = new StringBuffer(" select sum(nvl(TOTAL_AMOUNT, 0)) \"totalAmount\" from BAO_T_CUST_ACTIVITY_INFO where activity_id = '3' and activity_source = '推荐送体验金' and reward_shape ='体验金' and cust_id =? ");
		List<Object> objList = new ArrayList<Object>();
		objList.add(custId);
		List<Map<String, Object>> resultMap = repositoryUtil.queryForMap(sql.toString(), objList.toArray());
		return resultMap != null & resultMap.size() > 0 ? (BigDecimal)resultMap.get(0).get("totalAmount"):BigDecimal.ZERO;
	}
	
	@Override
	public void batchInsertActivityDetail(List<CustActivityDetailEntity> list) {

		for (int i = 0; i < list.size(); i++) {
			manager.persist(list.get(i));
		}
		manager.flush();
		manager.clear();
	}

	/**
     * 获取金牌推荐人统计信息
	 * 
	 * @author caoyi
	 * @date 2015年08月25日 上午11:42:36 
	 * @param Map<String, Object>
	 * @return Map<String, Object>
	 */
	@Override
	public Map<String, Object> findCustCommissionInfo(Map<String, Object> param) {
		StringBuilder sql=new StringBuilder()
		.append(" select * ")
		.append("   from (select nvl(sum(t.commission_amount) + sum(t.reward_amount), 0) as \"totalAmount\", ")
		.append("                nvl(sum(t.commission_amount), 0) as \"commissionAmount\", ")
		.append("                nvl(sum(t.reward_amount), 0) as \"rewardAmount\" ")
		.append("           from BAO_T_COMMISSION_INFO t ")
		.append("          where t.cust_id = ? ), ")
		.append("        (select count(1) as \"custCount\" ")
		.append("           from BAO_T_CUST_RECOMMEND_INFO t ")
		.append("          where t.cust_id = ? ")
		.append("            and t.record_status = '有效') ");
		
		List<Object> objList=new ArrayList<Object>();
		objList.add(param.get("custId"));
		objList.add(param.get("custId"));
		List<Map<String, Object>>  list=repositoryUtil.queryForMap(sql.toString(), objList.toArray());
		return list.get(0);
	}

	/**
     * 获取我的佣金记录
	 * 
	 * @author caoyi
	 * @date 2015年08月25日 上午11:44:32 
	 * @param Map<String, Object>
	 * @return Page<Map<String, Object>>
	 */
	@Override
	public Page<Map<String, Object>> findCustCommissionList(Map<String, Object> param) {
		StringBuilder sql = new StringBuilder()
		.append("  select rownum              as \"orderNo\", ")
		.append("         id                  as \"commissionId\", ")
		.append("         t.comm_date         as \"tradeDate\", ")
		.append("         t.invest_amount     as \"investAmount\", ")
		.append("         t.commission_amount as \"commissionAmount\", ")
		.append("         t.reward_amount     as \"rewardAmount\", ")
		.append("         t.trade_status      as \"tradeStatus\" from BAO_T_COMMISSION_INFO t where t.cust_id = ?  %s ");

		StringBuffer whereSqlString = new StringBuffer();
		List<Object> objList = new ArrayList<Object>();
		objList.add(param.get("custId"));
		if (!StringUtils.isEmpty(param.get("dateBegin"))) {
			whereSqlString.append(" and t.comm_date >= ? ");
			objList.add(DateUtils.parseStandardDate((String) param.get("dateBegin")));
		}
		if (!StringUtils.isEmpty(param.get("dateEnd"))) {
			whereSqlString.append(" and t.comm_date <= ? ");
			objList.add(DateUtils.getEndDate(DateUtils.parseStandardDate((String) param.get("dateEnd"))));
		}
		if (!StringUtils.isEmpty(param.get("tradeStatus"))) {
			if (param.get("tradeStatus").equals("是"))
				whereSqlString.append(" and t.trade_status = '"+ Constant.USER_ACTIVITY_TRADE_STATUS_05 + "'");
			if (param.get("tradeStatus").equals("否"))
				whereSqlString.append(" and t.trade_status = '"+ Constant.USER_ACTIVITY_TRADE_STATUS_06 + "'");
		}
		whereSqlString.append(" order by t.comm_date desc ");
		return repositoryUtil.queryForPageMap(String.format(sql.toString(), whereSqlString.toString()),objList.toArray(), (int) param.get("start"),(int) param.get("length"));
	}

	/**
     * 获取我的佣金详情记录
	 * 
	 * @author caoyi
	 * @date 2015年08月25日 上午11:45:32 
	 * @param Map<String, Object>
	 * @return Page<Map<String, Object>>
	 */
	@Override
	public Page<Map<String, Object>> findCustCommissionDetailList(Map<String, Object> param) {
		StringBuilder sql = new StringBuilder()
		.append(" select rownum              as \"orderNo\", ")
		.append("        b.login_name        as \"loginName\", ")
		.append("        c.comm_date         as \"tradeDate\", ")
		.append("        a.invest_amount     as \"investAmount\", ")
		.append("        a.commission_amount as \"commissionAmount\", ")
		.append("        a.reward_amount     as \"rewardAmount\", ")
		.append("        a.trade_status      as \"tradeStatus\" ")
		.append("   from BAO_T_COMMISSION_DETAIL_INFO a, ")
		.append("        BAO_T_CUST_INFO              b, ")
		.append("        BAO_T_COMMISSION_INFO        c ")
		.append("  where a.quilt_cust_id = b.id ")
		.append("    and a.commission_id = c.id ")
		.append("    and a.commission_id = ? ");

		List<Object> objList=new ArrayList<Object>();
		objList.add(param.get("commissionId"));
		return repositoryUtil.queryForPageMap(sql.toString(),objList.toArray(), (int) param.get("start"),(int) param.get("length"));
	}

	@Override
	public Page<Map<String, Object>> findCustByBindCard(Map<String, Object> param) {
		
		StringBuilder sql = new StringBuilder()
		.append("   select substr(t.cust_name, 1, 1) || decode(t.cust_gender, '男', '先生', '女士') \"custName\",  ")
		.append("          substr(t.mobile, 1, 3) || '****' ||substr(t.mobile, length(t.mobile)-3, 4) \"mobile\",  ")
		.append("          a.counts \"inviteCounts\" ")
		.append("   from bao_t_cust_info t ")
		.append("   inner join  ")
		.append("   ( ")
		.append("     select t.invite_origin_id, count(1) counts ")
		.append("     from bao_t_cust_info t, bao_t_bank_card_info s ")
		.append("     where t.id = s.cust_id  ")
		.append("     and s.record_status = '有效'  ")
		.append("     and s.bank_flag = '线上' ")
		.append("     and s.last_update_date between ? and ? ")
		.append("     and t.invite_origin_id is not null and t.invite_origin_id != '0' ")
		.append("     group by t.invite_origin_id ")
		.append("     having count(1) >= 10 ")
		.append("   )a on t.id = a.invite_origin_id ")
		.append("   inner join ( ")
		.append("     select t.invite_origin_id, min(s.last_update_date) lastUpdateDate ")
		.append("     from bao_t_cust_info t, bao_t_bank_card_info s ")
		.append("     where t.id = s.cust_id  ")
		.append("     and s.record_status = '有效'  ")
		.append("     and s.bank_flag = '线上' ")
		.append("     and s.last_update_date between ? and ? ")
		.append("     and t.invite_origin_id is not null and t.invite_origin_id != '0' ")
		.append("     group by t.invite_origin_id ")
		.append("     having count(1) >= 10 ")
		.append("   )b on t.id = b.invite_origin_id ")
		.append("   where 1 = 1 ");
		
		List<Object> objList = Lists.newArrayList();
		objList.add(param.get("startDate"));
		objList.add(param.get("expireDate"));
		objList.add(param.get("startDate"));
		objList.add(param.get("expireDate"));
		
		SqlCondition sqlCondition = new SqlCondition(sql, param, objList);
		sqlCondition.addListNotIn("excludeCustIds", "t.id")
		            .addSql("order by a.counts desc, b.lastUpdateDate");
		
		return repositoryUtil.queryForPageMap(sqlCondition.toString(), sqlCondition.toArray(), Integer.parseInt(param.get("start").toString()), Integer.parseInt(param.get("length").toString()));
	}

	@Override
	public Page<Map<String, Object>> queryAwardList(Map<String, Object> param) {
		StringBuilder sql = new StringBuilder()
				.append("   select substr(b.cust_name, 1, 1)|| decode(b.cust_gender, '男', '先生', '女士') \"custName\", ")
				.append("   substr(b.mobile, 1, 3) || '****' ||substr(b.mobile, length(b.mobile)-3, 4) \"mobile\",   ")
				.append("        trunc(sum(case ")
				.append("                    when i.transfer_apply_id is not null then ")
				.append("                     case ")
				.append("                       when ta.transfer_seat_term = -1 then ")
				.append("                        case ")
				.append("                          when d.loan_unit = '月' then ")
				.append("                           a.invest_amount * d.loan_term / 12 ")
				.append("                          else ")
				.append("                           a.invest_amount * d.loan_term / 360 ")
				.append("                        end ")
				.append("                       else ")
				.append("                        a.invest_amount / 12 ")
				.append("                     end ")
				.append("                    else ")
				.append("                     case ")
				.append("                       when d.seat_term = -1 then ")
				.append("                        case ")
				.append("                          when d.loan_unit = '月' then ")
				.append("                           a.invest_amount * d.loan_term / 12 ")
				.append("                          else ")
				.append("                           a.invest_amount * d.loan_term / 360 ")
				.append("                        end ")
				.append("                       else ")
				.append("                        a.invest_amount / 12 ")
				.append("                     end ")
				.append("                  end), ")
				.append("              0) \"yearAchievement\" ")
				.append("           from BAO_T_COMMISSION_DETAIL_INFO a, ")
				.append("                BAO_T_CUST_INFO              b, ")
				.append("                BAO_T_COMMISSION_INFO        c,  ")
				.append("                BAO_T_LOAN_INFO              d , BAO_T_INVEST_INFO i")
				.append("   left join bao_t_loan_transfer_apply ta ")
				.append("     on ta.id = i.transfer_apply_id ")
				.append("          where a.commission_id = c.id ")
				.append("            and c.cust_id = b.id and a.relate_primary=d.id ")
				.append("    and a.invest_id = i.id ")
				.append("          and a.create_date between ? and ? ")
				.append("          group by b.cust_name, b.mobile,b.cust_gender ")
				.append(" having trunc(sum(case ")
				.append("   when i.transfer_apply_id is not null then ")
				.append("    case ")
				.append("      when ta.transfer_seat_term = -1 then ")
				.append("       case ")
				.append("         when d.loan_unit = '月' then ")
				.append("          a.invest_amount * d.loan_term / 12 ")
				.append("         else ")
				.append("          a.invest_amount * d.loan_term / 360 ")
				.append("       end ")
				.append("      else ")
				.append("       a.invest_amount / 12 ")
				.append("    end ")
				.append("   else ")
				.append("    case ")
				.append("      when d.seat_term = -1 then ")
				.append("       case ")
				.append("         when d.loan_unit = '月' then ")
				.append("          a.invest_amount * d.loan_term / 12 ")
				.append("         else ")
				.append("          a.invest_amount * d.loan_term / 360 ")
				.append("       end ")
				.append("      else ")
				.append("       a.invest_amount / 12 ")
				.append("    end ")
				.append(" end), 0) >= 200000 ")
				.append("          order by \"yearAchievement\" desc,max(a.create_date) asc  ");
	
		List<Object> objList = Lists.newArrayList();
		objList.add(param.get("startDate"));
		objList.add(param.get("expireDate"));
		SqlCondition sqlCondition = new SqlCondition(sql, param, objList);
		return repositoryUtil.queryForPageMap(sqlCondition.toString(),sqlCondition.toArray(),Integer.parseInt(param.get("start").toString()),Integer.parseInt(param.get("length").toString()));
	}

	@Override
	public BigDecimal findCustAwardFor201706(String custId) {
		StringBuilder sql = new StringBuilder()
		.append(" SELECT nvl(sum(ca.total_amount),0) \"amount\" ")
		.append("   FROM BAO_T_CUST_ACTIVITY_INFO ca ")
		.append("  WHERE 1=1 ")
		.append("    AND ca.CUST_ID = ? ")
		.append("    AND ca.activity_id = '13' ")
		;
		
		return jdbcTemplate.queryForObject(sql.toString(), new Object[]{custId}, BigDecimal.class);
	}

	/*间接统计
		SELECT count(1)
		FROM BAO_T_CUST_INFO c
		WHERE 1=1
		and c.cust_type = '理财客户'
		AND c.CREATE_DATE <= to_date('2017-05-27','yyyy-MM-dd') + 1
		AND c.CREATE_DATE >= to_date('2017-04-27','yyyy-MM-dd')
		start with c.invite_origin_id = '05d1f64d-2ff0-4dfe-8816-48bad8cdd05e'
		connect by prior c.ID = c.INVITE_ORIGIN_ID  
	 */
	@Override
	public int findCustInvitedFor201706(String custId) {
		StringBuilder sql = new StringBuilder()
		.append(" SELECT count(1) \"count\" ")
		.append("   FROM BAO_T_CUST_INFO c ")
		.append("  WHERE 1=1 ")
		//.append("    AND c.cust_type = '理财客户' ") // 参考 findCustRecommendList
		.append("    AND c.invite_origin_id = ? ")
		;
		
		return jdbcTemplate.queryForObject(sql.toString(), new Object[]{custId}, Integer.class);
	}

	@Override
	public Object redEnvelopeList(Map<String, Object> requestParams) {
		StringBuilder sql = new StringBuilder("select T1.AWARD_NAME awardName");
		sql.append(",	t1.AWARD_TYPE awardType")
				.append(", t.ID ID ")
				.append(",	t1.AWARD_STATUS awardStatus")
				.append(",	t1.GRANT_AMOUNT grantAmount")
				.append(", t1.START_AMOUNT startAmount")
				.append(", t1.USE_SCOPE useScope")
				.append(", t1.INCREASE_UNIT increaseUnit")
				.append(",	t1.SEAT_TERM seatTerm")
				.append(", t1.LOAN_ALLOTTED_TIME loanAllottedTime")
				.append(", t.start_date startTime")
				.append(", t.expire_date expireDate")
				.append(", t.last_update_date lastUpdateDate")
				.append(", t.trade_status tradeStatus")
				.append(", t1.IS_TRANSFER isTransfer")
				.append(", t1.subject_repayment_methods repaymentMethods")
				.append(" from BAO_T_CUST_ACTIVITY_INFO t")
				.append(" left join bao_t_activity_award t1")
				.append(" on t.activity_award_id = t1.id")
				.append(" where t.cust_id = ?")
				.append(" and t.record_status = '有效'")
				.append(" and t1.record_status = '有效'")
				.append(" and t1.award_type = '"+Constant.REAWARD_SPREAD_04+"'")
				.append(" and t1.award_status = '启用'");

		String tradeStatus = CommonUtils.emptyToString(requestParams.get("tradeStatus"));
		if ("未使用".equals(tradeStatus)) {
			sql.append("and t.trade_status = '"+Constant.USER_ACTIVITY_TRADE_STATUS_01+"'");
		} else if ("已使用".equals(tradeStatus)) {
			sql.append("and t.trade_status = '"+Constant.USER_ACTIVITY_TRADE_STATUS_03+"'");
		} else if ("已过期".equals(tradeStatus)) {
			sql.append("and t.trade_status = '"+Constant.USER_ACTIVITY_TRADE_STATUS_04+"' ");
		}

		sql.append(" order by ")
				.append(" decode(t.trade_status, '"+Constant.USER_ACTIVITY_TRADE_STATUS_01+"', 1, '"+Constant.USER_ACTIVITY_TRADE_STATUS_03+"', 2, '已过期', 3) asc ")
				.append(", t1.grant_amount desc")
				.append(", t1.deadline_time asc");

		int start = CommonUtils.emptyToInt(requestParams.get("start"));
		int length = CommonUtils.emptyToInt(requestParams.get("length"));

		if (length == 0) {
			return repositoryUtil.queryForMap(sql.toString(),
					new Object[] {requestParams.get("custId")});
		}

		return repositoryUtil.queryForPageMap(sql.toString(),
				new Object[] {requestParams.get("custId")},start
				, length);
	}

	@Override
	public Map<String, Object> findRewardByIdAndCustId(String custActivityId, String custId) {
		StringBuilder sql = new StringBuilder("select T1.AWARD_NAME awardName");
		sql.append(",	t1.AWARD_TYPE awardType")
				.append(", t.ID ID ")
				.append(",	t1.AWARD_STATUS awardStatus")
				.append(",	t1.GRANT_AMOUNT grantAmount")
				.append(", t1.START_AMOUNT startAmount")
				.append(", t1.USE_SCOPE useScope")
				.append(", t1.INCREASE_UNIT increaseUnit")
				.append(",	t1.SEAT_TERM seatTerm")
				.append(", t1.LOAN_ALLOTTED_TIME loanAllottedTime")
				.append(", t.start_date startTime")
				.append(", t.expire_date expireDate")
				.append(", t.last_update_date lastUpdateDate")
				.append(", t.trade_status tradeStatus")
				.append(", t1.IS_TRANSFER isTransfer")
				.append(", t1.subject_repayment_methods repaymentMethods")
				.append(" from BAO_T_CUST_ACTIVITY_INFO t")
				.append(" left join bao_t_activity_award t1")
				.append(" on t.activity_award_id = t1.id")
				.append(" where t.cust_id = ?")
				.append(" and t.id = ?")
				.append(" and t.record_status = '有效'")
				.append(" and t1.record_status = '有效'")
				.append(" and (t1.award_type = '"+Constant.REAWARD_SPREAD_04+"' or t1.award_type = '"+Constant.REAWARD_SPREAD_05+"') ")
				.append(" and t1.award_status = '启用'")
				.append(" and t.trade_status = '"+Constant.USER_ACTIVITY_TRADE_STATUS_01+"'")
				.append(" and (to_char(t.expire_date,'yyyyMMdd') >= to_char(sysdate,'yyyyMMdd') or t.expire_date is null) ");

		List<Map<String, Object>> r = repositoryUtil.queryForMap(sql.toString(),
				new Object[] {custId, custActivityId});
		if (r != null && !r.isEmpty()) {
			return r.get(0);
		}
		return null;
	}

	@Override
	public CustActivityInfoEntity findById(String custActivityId) {
		return manager.find(CustActivityInfoEntity.class, custActivityId);
	}

//	public Page<Map<String, Object>> queryActualTimeAwardList(
//			Map<String, Object> param) {
//		StringBuilder sql = new StringBuilder()
//				.append(" select substr(cust.cust_name, 1, 1) || ")
//				.append("        decode(cust.cust_gender, '男', '先生', '女士') \"custName\", ")
//				.append("        substr(cust.mobile, 1, 3) || '****' || ")
//				.append("        substr(cust.mobile, length(cust.mobile) - 3, 4) \"mobile\", ")
//				.append("        trunc(sum(case ")
//				.append("                    when loan.repayment_method != '等额本息' then ")
//				.append("                     case ")
//				.append("                       when loan.loan_unit = '月' then ")
//				.append("                        loan.loan_term / 12 * i.invest_amount * 1 ")
//				.append("                       else ")
//				.append("                        loan.loan_term / 360 * i.invest_amount * 1 ")
//				.append("                     end ")
//				.append("                    else ")
//				.append("                     case ")
//				.append("                       when loan.loan_unit = '月' then ")
//				.append("                        loan.loan_term / 12 * i.invest_amount * 1.5 ")
//				.append("                       else ")
//				.append("                        loan.loan_term / 360 * i.invest_amount * 1.5 ")
//				.append("                     end ")
//				.append("                   ")
//				.append("                  end), ")
//				.append("              0) \"totalYearInvestAmount\" ")
//				.append("   from BAO_T_INVEST_INFO i, BAO_T_LOAN_INFO loan, BAO_T_CUST_INFO cust ")
//				.append("  where loan.id = i.loan_id ")
//				.append("    and i.cust_id = cust.id ")
//				.append("    and i.INVEST_MODE != '转让' ")
//				.append("    AND loan.NEWER_FLAG != '新手标' ")
//				.append("    and loan.loan_status in ('正常', '已到期')  ")
//				.append("    and i.create_date between ? and ? ")
//				.append("  group by cust.cust_name, cust.mobile, cust.cust_gender ")
//				.append(" having trunc(sum(case ")
//				.append("   when loan.repayment_method != '等额本息' then ")
//				.append("    case ")
//				.append("      when loan.loan_unit = '月' then ")
//				.append("       loan.loan_term / 12 * i.invest_amount * 1 ")
//				.append("      else ")
//				.append("       loan.loan_term / 360 * i.invest_amount * 1 ")
//				.append("    end ")
//				.append("   else ")
//				.append("    case ")
//				.append("      when loan.loan_unit = '月' then ")
//				.append("       loan.loan_term / 12 * i.invest_amount * 1.5 ")
//				.append("      else ")
//				.append("       loan.loan_term / 360 * i.invest_amount * 1.5 ")
//				.append("    end ")
//				.append(" end), 0) >= 100000 ")
//				.append("  order by \"totalYearInvestAmount\" desc, max(i.create_date) asc ");
//		List<Object> objList = Lists.newArrayList();
//		objList.add(param.get("startDate"));
//		objList.add(param.get("expireDate"));
//		SqlCondition sqlCondition = new SqlCondition(sql, param, objList);
//		return repositoryUtil.queryForPageMap(sql.toString(),
//				sqlCondition.toArray(),
//				Integer.parseInt(param.get("start").toString()),
//				Integer.parseInt(param.get("length").toString()));
//	}

	public List<Map<String, Object>> findTotalInvestAmountAndTotalYearAmount(
			Map<String, Object> param) {
		SqlCondition sqlCondition = getTotalInvestAmountAndTotalYearAmountSql(param);
		StringBuilder sql = new StringBuilder()
				.append(" SELECT ")
				.append(" nvl(trunc(sum(\"totalAmount\"),2),0) \"totalInvestAmount\", ")
				.append(" nvl(trunc(sum(\"myAward\"),2),0) \"totalMyAward\", ")
				.append(" nvl(trunc(sum(\"yearAmount\"),2),0) \"totalYearInvestAmount\" ")
				.append("  FROM (").append(sqlCondition.toString())
				.append(" ) ");
		List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(
				sql.toString(), sqlCondition.toArray());
		return queryForList;

	}

	private SqlCondition getTotalInvestAmountAndTotalYearAmountSql(
			Map<String, Object> param) {
		StringBuilder sql = new StringBuilder()
				.append("   select \"totalAmount\", ")
				.append("          \"yearAmount\", ")
				.append("          case ")
				.append("            when \"yearAmount\" >= 100 and \"yearAmount\" <= 1000 then ")
				.append("             \"yearAmount\" * 0.004 ")
				.append("            when \"yearAmount\" > 1000 and \"yearAmount\" <= 10000 then ")
				.append("             \"yearAmount\" * 0.006 ")
				.append("            when \"yearAmount\" > 10000 and \"yearAmount\" <= 50000 then ")
				.append("             \"yearAmount\" * 0.008 ")
				.append("            when \"yearAmount\" > 50000 then ")
				.append("             \"yearAmount\" * 0.01 ")
				.append("            else ")
				.append("             0 ")
				.append("          end \"myAward\" ")
				.append("     from (select sum(i.invest_amount) \"totalAmount\", ")
				.append("         sum(case ")
				.append("                    when loan.repayment_method != '等额本息' then ")
				.append("                     case ")
				.append("                       when loan.loan_unit = '月' then ")
				.append("                        loan.loan_term / 12 * i.invest_amount * 1 ")
				.append("                       else ")
				.append("                        loan.loan_term / 360 * i.invest_amount * 1 ")
				.append("                     end ")
				.append("                    else ")
				.append("                     case ")
				.append("                       when loan.loan_unit = '月' then ")
				.append("                        loan.loan_term / 12 * i.invest_amount * 1.5 ")
				.append("                       else ")
				.append("                        loan.loan_term / 360 * i.invest_amount * 1.5 ")
				.append("                     end ")
				.append("                  end ")
				.append("              ) \"yearAmount\" ")
				.append("   from BAO_T_INVEST_INFO i, BAO_T_LOAN_INFO loan, BAO_T_CUST_INFO cust ")
				.append("  where loan.id = i.loan_id ")
				.append("    and i.cust_id = cust.id ")
				.append("    and i.INVEST_MODE != '转让' ")
				.append("    and loan.newer_flag != '新手标' ")
//		        .append("    and loan.loan_status!= '流标'  ")
				.append("    AND loan.loan_status in ('正常', '已到期') ");

		List<Object> objList = Lists.newArrayList();
		SqlCondition sqlCondition = new SqlCondition(sql, param, objList)
				.addString("custId", "cust.invite_origin_id")
				.addBeginDate("startDate", "i.create_date")
				.addEndDate("endDate", "i.create_date")
				.addString("memo2", "cust.memo2")
//				.append("group by loan.loan_term, loan.loan_unit, loan.repayment_method,cust.id ")
				.append("group by cust.id ")
				.append("  order by max(i.create_date) asc ")
				.append("  )");
		return sqlCondition;
	}

	public int getCountByInviteOriginId(Map<String, Object> param) {
		ArrayList<Object> listObject = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder().append(" select count(*) from Bao_t_Cust_Info t where 1=1 ");
		SqlCondition sqlCondition = new SqlCondition(sql, param, listObject);
		// isBackCash为true是好友返现活动
		if (!StringUtils.isEmpty(param.get("isBackCash")) && (Boolean) param.get("isBackCash")) {
			sqlCondition
					.append("    and exists ")
					.append("  (select * ")
					.append("           from BAO_T_INVEST_INFO invest, bao_T_loan_info loan ")
					.append("          where invest.loan_id = loan.id ")
					.append("            and invest.cust_id = t.id ")
					.append("            and loan.newer_flag != '新手标' ")
					.append("            and invest.invest_mode != '转让' ")
					.append("            and loan.loan_status in ('正常', '已到期') ")
					.addBeginDate("startDate", "invest.create_date")
					.addEndDate("endDate", "invest.create_date").addSql(" ) ");
		}
				sqlCondition.addString("custId", "t.invite_origin_id")
//				.addBeginDate("startDate", "t.create_date")
//				.addEndDate("endDate", "t.create_date")
				.addString("memo2", "t.memo2");
		int count = jdbcTemplate.queryForObject(sql.toString(),sqlCondition.toArray(), Integer.class);
		return count;
	}
	/**
	 * 2017平台大促需求 奖励管理-活动列表
	 */
	public List<ActivityInfoEntity> getListActivityInfo() {
		StringBuilder sql = new StringBuilder();
		sql.append("  select a.id \"id\", ")
		.append("        a.start_date startDate, ")
		.append("        a.expire_date expireDate, ")
		.append("         decode(a.activity_name, ")
		.append("                '6月市场部活动', ")
		.append("                '6月加息节', ")
		.append("                a.activity_name) \"activityName\", ")
		.append("         a.activity_code \"activityCode\", ")
		.append("         a.activity_content \"activityContent\", ")
		.append("         case ")
		.append("           when a.expire_date >= sysdate and a.start_date <= sysdate then ")
		.append("            '是' ")
		.append("           else ")
		.append("            '否' ")
		.append("         end \"是否进行\" ")
		.append("    from BAO_T_ACTIVITY_INFO a ")
		.append("   where a.id in ('13', '17') ")
		.append("   order by \"是否进行\" desc, a.start_date desc ");
		return repositoryUtil.queryForList(sql.toString(),ActivityInfoEntity.class);
	}
	public BigDecimal findCustAwardAmount(Map<String, Object> param) {
		List<Object> listObject = Lists.newArrayList();
		StringBuilder sql = new StringBuilder().append(" select nvl(sum(t.total_amount),0) \"amount\" from BAO_T_CUST_ACTIVITY_INFO t where 1=1 ");
		SqlCondition sqlCondition = new SqlCondition(sql, param, listObject)
				.addString("custId", "t.cust_id")
				.addBeginDate("startDate", "t.create_date")
				.addEndDate("endDate", "t.create_date")
				.addString("activityId", "t.activity_id");
		BigDecimal count = jdbcTemplate.queryForObject(sql.toString(),sqlCondition.toArray(), BigDecimal.class);
		return count;
	}


	@Override
	public ResultVo findExpLoadDetail(String loanId, String investId, String custId) {
		StringBuilder sql = new StringBuilder();
		sql.append("select a.id \"loanId\",a.LOAN_TITLE \"loanTitle\",a.LOAN_TERM \"loanTerm\",a.LOAN_INFO \"loanInfo\",")
				.append("a.PROTOCAL_TYPE \"protocaltype\" ,b.YEAR_IRR \"yearRate\", ")
				.append("a.LOAN_CODE \"loanCode\", ")
				.append("a.LOAN_UNIT \"loanUnit\", ");
		if(StringUtils.isEmpty(investId)) {
			sql.append("'投资' as \"investStatus\", '0' as \"investAmount\", to_char(sysdate, 'yyyyMMdd') as \"buyDate\", ")
					.append("'0' as \"profit\", ");
		} else {
			sql.append("c.INVEST_STATUS as \"investStatus\", c.INVEST_AMOUNT as \"investAmount\",c.INVEST_DATE as \"buyDate\", ")
					.append("d.TRADE_AMOUNT as \"profit\", ")
					.append("c.EXPIRE_DATE as \"expireDate\", ");
		}
		sql.append("nvl(e.ALREADY_INVEST_PEOPLES, 0) \"investers\" ")
				.append("from BAO_T_LOAN_INFO a left JOIN BAO_T_LOAN_DETAIL_INFO b on a.id = b.LOAN_ID ")
				.append("left JOIN BAO_T_INVEST_INFO c on a.id=c.LOAN_ID ")
				.append("left JOIN BAO_T_ACCOUNT_FLOW_INFO d on c.id=d.RELATE_PRIMARY ")
				.append("left JOIN BAO_T_PROJECT_INVEST_INFO e on a.id=e.LOAN_ID ")
				.append("where 1=1 and a.id=?");

		List<Map<String, Object>> r = null;
		List<Map<String, Object>> list = null;
		if(StringUtils.isEmpty(investId)) {
			 r = repositoryUtil.queryForMap(sql.toString(), new Object[] {loanId});
		} else {
			sql.append(" and c.id=?");
			r = repositoryUtil.queryForMap(sql.toString(), new Object[] {loanId, investId});
		}

		if(!StringUtils.isEmpty(custId)) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("select * from BAO_T_CUST_ACTIVITY_INFO t where t.CUST_ID = ? and t.TRADE_STATUS = ? and t.REWARD_SHAPE = ?");
			list = repositoryUtil.queryForMap(stringBuilder.toString(), new Object[] {custId,
					Constant.USER_ACTIVITY_TRADE_STATUS_01, Constant.REAWARD_SPREAD_01});
			r.get(0).put("size", list.size());
		}

		if (r != null && !r.isEmpty()) {
			return new ResultVo(true, "查询成功", r.get(0));
		}
		return new ResultVo(false,"查询失败，无此体验标，请核对体验标id");
	}

	@Override
	public List<CustActivityInfoEntity> findNotUsedAndUsedExpireExpLoan() {
		StringBuilder sql = new StringBuilder();
		sql.append("select * from BAO_T_CUST_ACTIVITY_INFO t where 1=1 and ")
				.append("(t.TRADE_STATUS = '已领取' or t.TRADE_STATUS='全部使用') and ")
				.append("to_char(sysdate, 'yyyy-mm-dd') > to_char(t.EXPIRE_DATE,'yyyy-mm-dd') and ")
				.append("t.REWARD_SHAPE='体验金' ");
		List<CustActivityInfoEntity> list = null;
		list = repositoryUtil.queryForList(sql.toString(), CustActivityInfoEntity.class);
		return list;
	}

	@Override
	public ResultVo findExpCouponList(Map<String, Object> params) {

		String custId = params.get("custId").toString();
		String type = params.get("type").toString();//红包类型
		String tradeStatus = params.get("tradeStatus") == null ? "" : params.get("tradeStatus").toString();
		int start = params.get("start") == null ? 0 : Integer.valueOf(params.get("start").toString());
		int length = params.get("length") == null ? 0 : Integer.valueOf(params.get("length").toString());

		StringBuilder sql = new StringBuilder();
		sql.append("select t.ID \"id\",                                        ")
				.append("t.CUST_ID \"custId\",                                 ")
				.append("t.ACTIVITY_ID \"activityId\",                         ")
				.append("t.ACTIVITY_SOURCE \"activitySource \",                ")
				.append("t.ACTIVITY_DESC \"activityDesc\",                     ")
				.append("t.TOTAL_AMOUNT \"totalAmount\",                       ")
				.append("t.USABLE_AMOUNT \"usableAmount\",                     ")
				.append("t.TRADE_CODE \"tradeCode\",                           ")
				.append("t.TRADE_STATUS \"tradeStatus\",                       ")
				.append("t.START_DATE \"startDate\",                           ")
				.append("t.EXPIRE_DATE \"expireDate\",                         ")
				.append("t.RECORD_STATUS \"recordStatus\",                     ")
				.append("t.CREATE_USER \"createUser\",                         ")
				.append("t.CREATE_DATE \"createDate\",                         ")
				.append("t.LAST_UPDATE_USER \"lastUpdateUser\",                ")
				.append("t.LAST_UPDATE_DATE \"lasrUpdateDate\",                ")
				.append("t.MEMO \"memo\",                                      ")
				.append("t.REWARD_SHAPE \"rewardShape\",                       ")
				.append("t.QUILT_CUST_ID \"quiltCustId\",                      ")
				.append("t.ACTIVITY_AWARD_ID \"activityAwardId\",              ")
				.append("t.LOAN_ID \"loanId\",                                 ")
				.append("t.INVEST_AMOUNT \"investAmount\",                     ")
				.append("a.IS_TRANSFER \"isTransfer\",                         ")
				.append("a.SUBJECT_REPAYMENT_METHODS \"repaymentMethods\",     ")
				.append("a.INCREASE_UNIT \"increaseUnit\",                     ")
				.append("a.SEAT_TERM \"seatTerm\"                              ")
				.append("from BAO_T_CUST_ACTIVITY_INFO t                       ")
				.append("left join BAO_T_ACTIVITY_AWARD a                      ")
				.append("on t.ACTIVITY_AWARD_ID=a.id                           ")
				.append("where a.AWARD_STATUS='启用' and t.CUST_ID = ?         ")
				.append("and t.REWARD_SHAPE = ?                                ");

		Map<String, Object> map = new HashMap<>();

		if(length != 0) {
			if("未使用".equals(tradeStatus)) {
				sql.append("and t.TRADE_STATUS = '已领取' and to_char(sysdate, 'yyyy-mm-dd') >= to_char(t.START_DATE,'yyyy-mm-dd')  ")
						.append("and to_char(sysdate, 'yyyy-mm-dd') <= to_char(t.EXPIRE_DATE,'yyyy-mm-dd')  ")
						.append("order by t.USABLE_AMOUNT desc, t.EXPIRE_DATE asc    ");
			} else if("已使用".equals(tradeStatus)) {
				sql.append("and t.TRADE_STATUS = '全部使用' and to_char(sysdate, 'yyyy-mm-dd') >= to_char(t.START_DATE,'yyyy-mm-dd') ")
						.append("order by t.USABLE_AMOUNT desc, t.EXPIRE_DATE asc    ");
			} else if("已过期".equals(tradeStatus)) {
				sql.append("and (t.TRADE_STATUS = '已领取' or t.TRADE_STATUS = '已过期') ")
						.append("and to_char(sysdate, 'yyyy-mm-dd') > to_char(t.EXPIRE_DATE,'yyyy-mm-dd')  ")
						.append("order by t.USABLE_AMOUNT desc, t.EXPIRE_DATE desc   ");
			} else if(org.apache.commons.lang3.StringUtils.isBlank(tradeStatus)) {
				sql.append("order by DECODE(t.TRADE_STATUS, '已领取', 1, '全部使用', 2, '已过期', 3)  asc, t.USABLE_AMOUNT desc, t.EXPIRE_DATE asc ");
			} else {
				return new ResultVo(false, "使用状态(tradeStatus)有误");
			}
			Page<Map<String, Object>> page = repositoryUtil.queryForPageMap(sql.toString(), new Object[]{custId, type}, start, length);
			map.put("page", page.getContent());
			map.put("size", page.getContent().size());
			map.put("total", page.getTotalElements());

		} else {
			sql.append("and t.TRADE_STATUS = '已领取' and to_char(sysdate, 'yyyy-mm-dd') >= to_char(t.START_DATE,'yyyy-mm-dd')  ")
					.append("and to_char(sysdate, 'yyyy-mm-dd') <= to_char(t.EXPIRE_DATE,'yyyy-mm-dd')                          ")
					.append("order by t.USABLE_AMOUNT desc, t.EXPIRE_DATE asc  ");
			List<Map<String, Object>> list = repositoryUtil.queryForMap(sql.toString(), new Object[]{custId, type});
			map.put("page", list);
			map.put("size", list.size());
		}
		return new ResultVo(true,"查询成功", map);
	}
	/**
	 * 更新红包状态为已过期
	 * @return
	 */
	public int updateActivityByExpireDate()
	{
		StringBuilder sql=new StringBuilder()
		.append(" update bao_t_cust_activity_info t")
		.append(" set t.trade_status = '已过期' ")
		.append(" where t.trade_status = '已领取' ")
		.append(" and t.expire_date is not null ")
		.append(" and to_char(t.expire_date, 'yyyy/MM/dd') < ")
		.append(" to_char(SYSDATE, 'yyyy/MM/dd')");
		return jdbcTemplate.update(sql.toString());
	}
}
