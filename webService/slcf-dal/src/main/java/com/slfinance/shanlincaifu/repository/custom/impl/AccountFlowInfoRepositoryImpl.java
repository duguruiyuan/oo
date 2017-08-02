/** 
 * @(#)AccountFlowInfoRepositoryImpl.java 1.0.0 2015年4月25日 下午12:08:43  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */

package com.slfinance.shanlincaifu.repository.custom.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AccountFlowInfoEntity;
import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.AccountFlowInfoRepositoryCustom;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.PageFuns;
import com.slfinance.shanlincaifu.utils.SqlCondition;
import com.slfinance.shanlincaifu.utils.SubjectConstant;
import com.slfinance.vo.ResultVo;

/**
 * 自定义账户流水数据访问类
 * 
 * @author wangjf
 * @version $Revision:1.0.0, $Date: 2015年4月25日 下午12:08:43 $
 */
@Repository
public class AccountFlowInfoRepositoryImpl implements
		AccountFlowInfoRepositoryCustom {

	@Autowired
	private RepositoryUtil repositoryUtil;

	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * 用户资金流水列表
	 *
	 * @author wangjf
	 * @date 2015年4月25日 上午11:32:03
	 * @param param
	 *            <tt>start：int:分页起始页</tt><br>
	 *            <tt>length：int:每页长度</tt><br>
	 *            <tt>nickName：String:用户昵称</tt><br>
	 *            <tt>custName：String:真实姓名</tt><br>
	 *            <tt>credentialsCode：String:证件号码</tt><br>
	 *            <tt>opearteDateBegin：String:操作开始时间</tt><br>
	 *            <tt>opearteDateEnd：String:操作开始时间</tt><br>
	 *            <tt>tradeType：String:交易类型</tt><br>
	 * @return <tt>id：String:用户ID</tt><br>
	 *         <tt>nickName：String:用户昵称</tt><br>
	 *         <tt>tradeType：String:交易类型</tt><br>
	 *         <tt>tradeAmount：BigDecimal:操作金额</tt><br>
	 *         <tt>accountAvailable：BigDecimal:可用余额</tt><br>
	 *         <tt>accountFreezeAmount：BigDecimal:冻结余额</tt><br>
	 *         <tt>bankrollFlowDirection：String:资金流向</tt><br>
	 *         <tt>targetNickName：String:对方账户</tt><br>
	 *         <tt>operateDate：Date:操作时间</tt><br>
	 */
	@Override
	public Page<Map<String, Object>> findAllAccountFlowList(Map<String, Object> param) {
		StringBuffer whereCustSqlString= new StringBuffer();
		StringBuffer whereAccountSqlString= new StringBuffer();
		
		StringBuffer sqlString= new StringBuffer()
		.append(" select S.ID \"id\", S.LOGIN_NAME \"nickName\", S.CUST_NAME \"custName\",  ")
		.append("        S.CREDENTIALS_CODE \"credentialsCode\", Q.TRADE_TYPE \"tradeType\", TRUNC_AMOUNT_WEB(Q.TRADE_AMOUNT) \"tradeAmount\",  ")
		.append("        TRUNC_AMOUNT_WEB(Q.ACCOUNT_AVAILABLE) \"accountAvailable\", TRUNC_AMOUNT_WEB(Q.ACCOUNT_FREEZE_AMOUNT) \"accountFreezeAmount\",  ")
		.append("        Q.BANKROLL_FLOW_DIRECTION \"bankrollFlowDirection\",  ")
		.append("        case when Q.TRADE_TYPE = '购买债权转让' and Q.BANKROLL_FLOW_DIRECTION = '收入' then '债权转让成功' || substr(Q.MEMO, 7)  else Q.MEMO  end \"targetNickName\", ")
		.append("        Q.CREATE_DATE \"operateDate\" ")
		.append("   from BAO_T_ACCOUNT_FLOW_INFO Q ")
//		.append("   INNER JOIN BAO_T_ACCOUNT_FLOW_DETAIL N ON Q.ID = N.ACCOUNT_FLOW_ID ")
		.append("   INNER JOIN BAO_T_CUST_INFO S ON Q.CUST_ID = S.ID ")
//		.append("   LEFT JOIN BAO_T_ACCOUNT_INFO T ON T.ID = N.TARGET_ACCOUNT ")
//		.append("   LEFT JOIN BAO_T_CUST_INFO S2 ON S2.ID = T.ID ")
		.append("   WHERE Q.ACCOUNT_TYPE = ?  AND (S.CUST_KIND IS NULL OR S.CUST_KIND = '网站用户') %s ")
		.append("   ORDER BY Q.TRADE_NO DESC ");
		
		List<Object> objList=new ArrayList<Object>();
		objList.add(Constant.ACCOUNT_TYPE_MAIN);
		if (!StringUtils.isEmpty(param.get("nickName"))) {
			whereCustSqlString.append(" and S.LOGIN_NAME = ?");
			objList.add(new StringBuffer().append(param.get("nickName")));
		}

		if (!StringUtils.isEmpty(param.get("custName"))) {
			whereCustSqlString.append(" and S.CUST_NAME = ?");
			objList.add(param.get("custName"));
		}

		if (!StringUtils.isEmpty(param.get("credentialsCode"))) {
			whereCustSqlString.append(" and S.CREDENTIALS_CODE = ?");
			objList.add(param.get("credentialsCode"));
		}

		// 过滤公司账户
		whereAccountSqlString.append(" and Q.ACCOUNT_ID NOT IN (?, ?, ?, ?, ?, ?, ?, ?)");
		objList.add(Constant.ACCOUNT_ID_CENTER);
		objList.add(Constant.ACCOUNT_ID_ERAN);
		objList.add(Constant.ACCOUNT_ID_RISK);
		objList.add(Constant.ACCOUNT_ID_PROJECT_ERAN);
		objList.add(Constant.ACCOUNT_ID_PROJECT_RISK);
		objList.add(Constant.ACCOUNT_ID_REPAYMENT);
		objList.add(Constant.ACCOUNT_ID_WEALTH_CENTER);
		objList.add(Constant.ACCOUNT_ID_COMMISION);

		if (!StringUtils.isEmpty(param.get("opearteDateBegin"))) {
			whereAccountSqlString.append(" and Q.CREATE_DATE >= ?");
			objList.add(DateUtils.parseStandardDate((String) param
					.get("opearteDateBegin")));
		}

		if (!StringUtils.isEmpty(param.get("opearteDateEnd"))) {
			whereAccountSqlString.append(" and Q.CREATE_DATE <= ?");
			objList.add(DateUtils.getEndDate(DateUtils
					.parseStandardDate((String) param.get("opearteDateEnd"))));
		}

		if (!StringUtils.isEmpty(param.get("tradeType"))) {
			whereAccountSqlString.append(" and Q.TRADE_TYPE = ?");
			objList.add(param.get("tradeType"));
		} else {
			whereAccountSqlString.append(" and Q.TRADE_TYPE NOT IN (?, ?) ");
			objList.add(SubjectConstant.TRADE_FLOW_TYPE_FREEZE);
			objList.add(SubjectConstant.TRADE_FLOW_TYPE_UNFREEZE);
		}

		StringBuffer whereSqlString = new StringBuffer();
		if (!StringUtils.isEmpty(whereCustSqlString.toString())) {
			whereSqlString.append(String.format(" %s",
					whereCustSqlString.toString()));
		}

		if (!StringUtils.isEmpty(whereAccountSqlString.toString())) {
			whereSqlString.append(String.format(" %s",
					whereAccountSqlString.toString()));
		}

		return repositoryUtil.queryForPageMap(
				String.format(sqlString.toString(), whereSqlString.toString()),
				objList.toArray(), (int) param.get("start"),
				(int) param.get("length"));
	}

	@SuppressWarnings("unchecked")
	@Override
	public BigDecimal findIncomeByCustId(Map<String, Object> param) {
		StringBuffer sqlString = new StringBuffer()
				.append(" select NVL(SUM(TRUNC_AMOUNT_WEB(T.TRADE_AMOUNT)), 0) \"tradeAmount\" from BAO_T_ACCOUNT_FLOW_INFO T WHERE 1 = 1 %s");

		StringBuffer whereSqlString = new StringBuffer();

		List<Object> objList = new ArrayList<Object>();
		if (!StringUtils.isEmpty(param.get("custId"))) {
			whereSqlString.append(" and CUST_ID = ?");
			objList.add(param.get("custId"));
		}

		if (!StringUtils.isEmpty(param.get("tradeDate"))) {
			whereSqlString.append(" and TRADE_DATE >= ? and TRADE_DATE<= ?");
			objList.add(DateUtils.getStartDate((Date) param.get("tradeDate")));
			objList.add(DateUtils.getEndDate((Date) param.get("tradeDate")));
		}

		if (!StringUtils.isEmpty(param.get("tradeType"))) {
			List<String> tradeTypeList = (List<String>) param.get("tradeType");
			whereSqlString.append(" and ( ");
			for (int i = 0; i < tradeTypeList.size(); i++) {
				if (i == 0) {
					whereSqlString.append(" TRADE_TYPE = ? ");
				} else {
					whereSqlString.append(" OR TRADE_TYPE = ? ");
				}
				objList.add(tradeTypeList.get(i));
			}
			whereSqlString.append(" ) ");
		}

		List<Map<String, Object>> list = repositoryUtil.queryForMap(
				String.format(sqlString.toString(), whereSqlString.toString()),
				objList.toArray());
		if (list == null || list.size() == 0)
			return new BigDecimal("0");
		Map<String, Object> map = list.get(0);
		if (map == null || map.size() == 0)
			return new BigDecimal("0");

		return (BigDecimal) map.get("tradeAmount");
	}
	
	public static void main(String[] args) {
		BigDecimal result = new BigDecimal("0.00000014");
		//result.setScale(8, BigDecimal.ROUND_HALF_UP);
		System.out.println(result.toPlainString());
	}

	/**
	 * 交易查询
	 *
	 * @author HuangXiaodong
	 * @date 2015年5月19日 上午11:33:31
	 * @param param
	 *            <tt>tradeType： String:交易类型</tt><br>
	 *            <tt>tradeDateBegin： Date:交易开始时间</tt><br>
	 *            <tt>tradeDateEnd： Date:交易结束时间</tt><br>
	 *            <tt>custId:用户id</tt><br>
	 * @return List<Map<String, Object>> <tt>tradeAmount:交易金额</tt><br>
	 *         <tt>tradeDate： BigDecimal:交易时间</tt><br>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> findSumTradeAmount(
			Map<String, Object> param) {

		StringBuffer sqlString = new StringBuffer()
				.append(" select SUM(TRUNC_AMOUNT_WEB(M.TradeAmount)) \"tradeAmount\" , M.TradeDate \"tradeDate\" from(  ")
				.append(" select NVL(T.TRADE_AMOUNT, 0) TradeAmount ,TO_CHAR(T.TRADE_DATE, 'YYYY-MM-DD') TradeDate ,T.TRADE_TYPE  from   ")
				.append(" BAO_T_ACCOUNT_FLOW_INFO T  WHERE 1 = 1   ");

		// TODO Auto-generated method stub
		List<Object> objList = new ArrayList<Object>();
		if (!StringUtils.isEmpty(param.get("custId"))) {
			sqlString.append(" and CUST_ID  = ?");
			objList.add(param.get("custId"));
		}

		if (!StringUtils.isEmpty(param.get("tradeType"))) {
			List<String> tradeTypeList = (List<String>) param.get("tradeType");
			sqlString.append(" and ( ");
			for (int i = 0; i < tradeTypeList.size(); i++) {
				if (i == 0) {
					sqlString.append(" TRADE_TYPE = ? ");
				} else {
					sqlString.append(" OR TRADE_TYPE = ? ");
				}
				objList.add(tradeTypeList.get(i));
			}
			sqlString.append(" ) ");
		}

		if (!StringUtils.isEmpty(param.get("tradeDateBegin"))) {
			sqlString.append("  and TO_CHAR(T.TRADE_DATE, 'YYYY-MM-DD')>=  ?");
			objList.add(param.get("tradeDateBegin"));
		}
		if (!StringUtils.isEmpty(param.get("tradeDateEnd"))) {
			sqlString.append("  and TO_CHAR(T.TRADE_DATE, 'YYYY-MM-DD')<=  ?");
			objList.add(param.get("tradeDateEnd"));
		}

		sqlString
				.append(" )M group by  M.TradeDate  order by M.TradeDate asc ");

		List<Map<String, Object>> list = repositoryUtil.queryForMap(
				sqlString.toString(), objList.toArray());
		return list;
	}

	static Object[] types = { SubjectConstant.TRADE_FLOW_TYPE_JOIN, // 加入活期宝
			SubjectConstant.TRADE_FLOW_TYPE_ATONE, // 快速赎回
			SubjectConstant.TRADE_FLOW_TYPE_ATONE_NORMAL, // 普通赎回
			// SubjectConstant.TRADE_FLOW_TYPE_ATONE_NORMAL_FREEZE, // 赎回冻结
			// SubjectConstant.TRADE_FLOW_TYPE_ATONE_NORMAL_UNFREEZE, // 赎回解冻
			SubjectConstant.TRADE_FLOW_TYPE_01, // 活期宝收益

			SubjectConstant.TRADE_FLOW_TYPE_JOIN_EXPERIENCE, // 加入体验宝
			SubjectConstant.TRADE_FLOW_TYPE_TYBWITHDRAW, // 赎回体验宝
			SubjectConstant.TRADE_FLOW_TYPE_03, // 体验宝收益

			SubjectConstant.TRADE_FLOW_TYPE_JOIN_TERM, // 购买定期宝
//			SubjectConstant.TRADE_FLOW_TYPE_ATONE_TERM, // 赎回定期宝
			SubjectConstant.TRADE_FLOW_TYPE_ATONE_DIFF , // 定期宝补差收益 A
			SubjectConstant.TRADE_FLOW_TYPE_ATONE_EXPENSE , // 提前赎回手续费 A
			SubjectConstant.TRADE_FLOW_TYPE_ATONE_RISK , // 计提风险金 A
			SubjectConstant.TRADE_FLOW_TYPE_ATONE_MANAGE , // 账户管理费 A
			SubjectConstant.TRADE_FLOW_TYPE_ATONE_AWARD , // 到期奖励 A
			SubjectConstant.TRADE_FLOW_TYPE_ATONE_TERM_ADVANCE , // 提前赎回// A																			
			SubjectConstant.TRADE_FLOW_TYPE_ATONE_TERM_FINISH,  // 到期赎回// A
			SubjectConstant.TRADE_FLOW_TYPE_ATONE_TERM          // 赎回定期宝
	};

	@Override
	public Page<Map<String, Object>> findAllBaoAccountDetailByCustId(
			Map<String, Object> param) {
		StringBuffer sqlString = new StringBuffer()
				.append(" SELECT T.REQUEST_NO requestNo, TO_CHAR(T.TRADE_DATE, 'YYYY-MM-DD') tradeDate, T.TRADE_TYPE tradeType, SUM(TRUNC_AMOUNT_WEB(T.TRADE_AMOUNT)) tradeAmount ")
				.append(" FROM BAO_T_ACCOUNT_FLOW_INFO T ")
				.append(" WHERE T.ACCOUNT_TYPE = '分账' AND T.CUST_ID = ? %s")
				.append(" GROUP BY T.REQUEST_NO, TO_CHAR(T.TRADE_DATE, 'YYYY-MM-DD'), T.TRADE_TYPE ");

		StringBuffer whereSqlString = new StringBuffer();
		List<Object> objList = new ArrayList<Object>();
		objList.add((String) param.get("custId"));

		if (!StringUtils.isEmpty(param.get("tradeType"))) {
			whereSqlString.append(" and TRADE_TYPE = ?");
			objList.add(param.get("tradeType"));
		} else if (!StringUtils.isEmpty(param.get("productType"))) {
			if (Constant.PRODUCT_TYPE_01.equals((String) param
					.get("productType"))) { // 活期宝
				whereSqlString.append(" and ( ");
				for (int i = 0; i < 4; i++) {
					if (i == 0) {
						whereSqlString.append(" TRADE_TYPE = ? ");
					} else {
						whereSqlString.append(" OR TRADE_TYPE = ? ");
					}
					objList.add(types[i]);
				}
				whereSqlString.append(" ) ");
			} else if (Constant.PRODUCT_TYPE_03.equals((String) param
					.get("productType"))) { // 体验宝
				whereSqlString.append(" and ( ");
				for (int i = 4; i < 4 + 3; i++) {
					if (i == 4) {
						whereSqlString.append(" TRADE_TYPE = ? ");
					} else {
						whereSqlString.append(" OR TRADE_TYPE = ? ");
					}
					objList.add(types[i]);
				}
				whereSqlString.append(" ) ");
			} else if (Constant.PRODUCT_TYPE_04.equals((String) param
					.get("productType"))) { // 定期宝
				whereSqlString.append(" and ( ");
				for (int i = 7; i < types.length; i++) {
					if (i == 7) {
						whereSqlString.append(" TRADE_TYPE = ? ");
					} else {
						whereSqlString.append(" OR TRADE_TYPE = ? ");
					}
					objList.add(types[i]);
				}
				whereSqlString.append(" ) ");
			}
		} else { // 未设置业务类型时取所有业务类型
			whereSqlString.append(" and ( ");
			for (int i = 0; i < types.length; i++) {
				if (i == 0) {
					whereSqlString.append(" TRADE_TYPE = ? ");
				} else {
					whereSqlString.append(" OR TRADE_TYPE = ? ");
				}
				objList.add(types[i]);
			}
			whereSqlString.append(" ) ");
		}

		if (!StringUtils.isEmpty(param.get("opearteDateBegin"))) {
			whereSqlString.append(" and TRADE_DATE >= ?");
			objList.add(DateUtils.parseStandardDate((String) param
					.get("opearteDateBegin")));
		}

		if (!StringUtils.isEmpty(param.get("opearteDateEnd"))) {
			whereSqlString.append(" and TRADE_DATE <= ?");
			objList.add(DateUtils.getEndDate(DateUtils
					.parseStandardDate((String) param.get("opearteDateEnd"))));
		}

		/** 增加投资条件限制 **/
		if (!StringUtils.isEmpty(param.get("investId"))) {
			whereSqlString
					.append(" and T.ACCOUNT_ID IN ( select id from BAO_T_SUB_ACCOUNT_INFO re where re.relate_primary = ? ) ");
			objList.add(param.get("investId"));
		}

		StringBuffer sqlAllString = new StringBuffer();
		sqlAllString
				.append("SELECT requestNo \"requestNo\", tradeDate \"tradeDate\", tradeAmount \"tradeAmount\", ");
		sqlAllString.append("case tradeType ");
		sqlAllString.append(String.format("    when '%s' then '%s' ",
				SubjectConstant.TRADE_FLOW_TYPE_01, "收益"));
		sqlAllString.append(String.format("    when '%s' then '%s' ",
				SubjectConstant.TRADE_FLOW_TYPE_03, "收益"));
		sqlAllString.append(String.format("    when '%s' then '%s' ",
				SubjectConstant.TRADE_FLOW_TYPE_JOIN, "购买"));
		sqlAllString.append(String.format("    when '%s' then '%s' ",
				SubjectConstant.TRADE_FLOW_TYPE_JOIN_EXPERIENCE, "购买"));
		sqlAllString.append(String.format("    when '%s' then '%s' ",
				SubjectConstant.TRADE_FLOW_TYPE_ATONE, "快速赎回"));
		sqlAllString.append(String.format("    when '%s' then '%s' ",
				SubjectConstant.TRADE_FLOW_TYPE_ATONE_NORMAL, "普通赎回"));
		sqlAllString.append(String.format("    when '%s' then '%s' ",
				SubjectConstant.TRADE_FLOW_TYPE_TYBWITHDRAW, "赎回"));
		sqlAllString.append(String.format("    when '%s' then '%s' ",
				SubjectConstant.TRADE_FLOW_TYPE_ATONE_NORMAL_FREEZE, "普通赎回冻结"));
		sqlAllString.append(String
				.format("    when '%s' then '%s' ",
						SubjectConstant.TRADE_FLOW_TYPE_ATONE_NORMAL_UNFREEZE,
						"普通赎回解冻"));		
		sqlAllString.append(String.format("    when '%s' then '%s' ",
				SubjectConstant.TRADE_FLOW_TYPE_JOIN_TERM, "购买"));
//		sqlAllString.append(String.format("    when '%s' then '%s' ",
//				SubjectConstant.TRADE_FLOW_TYPE_ATONE_TERM, "赎回"));
		sqlAllString.append(String.format("    when '%s' then '%s' ",
				SubjectConstant.TRADE_FLOW_TYPE_ATONE_DIFF , "补差收益"));
		sqlAllString.append(String.format("    when '%s' then '%s' ",
				SubjectConstant.TRADE_FLOW_TYPE_ATONE_EXPENSE , "提前赎回手续费"));
		sqlAllString.append(String.format("    when '%s' then '%s' ",
				SubjectConstant.TRADE_FLOW_TYPE_ATONE_RISK , "计提风险金"));
		sqlAllString.append(String.format("    when '%s' then '%s' ",
				SubjectConstant.TRADE_FLOW_TYPE_ATONE_MANAGE , "账户管理费"));
		sqlAllString.append(String.format("    when '%s' then '%s' ",
				SubjectConstant.TRADE_FLOW_TYPE_ATONE_AWARD , "到期奖励"));
		sqlAllString.append(String.format("    when '%s' then '%s' ",
				SubjectConstant.TRADE_FLOW_TYPE_ATONE_TERM_ADVANCE , "提前赎回"));
		sqlAllString.append(String.format("    when '%s' then '%s' ",
				SubjectConstant.TRADE_FLOW_TYPE_ATONE_TERM_FINISH , "到期赎回"));		
		sqlAllString.append("    else tradeType ");
		sqlAllString.append("end \"tradeType\" ");
		sqlAllString.append(" FROM ( ");
		sqlAllString.append(String.format(sqlString.toString(),
				whereSqlString.toString()));
		sqlAllString.append(" ) A ");
		sqlAllString.append(" ORDER BY requestNo DESC ");

		return repositoryUtil.queryForPageMap(sqlAllString.toString(),
				objList.toArray(), (int) param.get("start"),
				(int) param.get("length"));
	}

	@Override
	public BigDecimal findTotalAccountAmount(Map<String, Object> param) {
		StringBuffer sqlString = new StringBuffer()
				.append(" SELECT accountTotalValue + accountTotalAmount + accountUnuseValue + accountUnuseAmount \"tradeAmount\" ")
				.append(" FROM  ")
				.append(" (SELECT NVL(SUM(TRUNC_AMOUNT_WEB(T.ACCOUNT_TOTAL_VALUE)), 0) accountTotalValue ")
				// 持有份额
				.append(" 		 FROM BAO_T_SUB_ACCOUNT_INFO T ")
				.append(" 		 WHERE T.CUST_ID = ? AND T.RELATE_TYPE = ? ")
				.append(" 		 AND T.RELATE_PRIMARY IN ( ")
				.append(" 		       SELECT ID ")
				.append(" 		       FROM BAO_T_INVEST_INFO S ")
				.append(" 		       WHERE INVEST_STATUS = ? AND S.PRODUCT_ID IN ( ")
				.append(" 		             SELECT ID ")
				.append(" 		             FROM BAO_T_PRODUCT_INFO M ")
				.append(" 		             WHERE M.PRODUCT_TYPE IN ( ")
				.append(" 		                   SELECT ID ")
				.append(" 		                   FROM BAO_T_PRODUCT_TYPE_INFO N ")
				.append(" 		             ) ")
				.append(" 		       ) ")
				.append(" 		 ) ")
				.append(" ) A, ")
				.append(" (SELECT NVL(SUM(TRUNC_AMOUNT_WEB(T.ACCOUNT_TOTAL_AMOUNT)), 0) accountTotalAmount ")
				// 账户余额
				.append(" FROM BAO_T_ACCOUNT_INFO T ")
				.append(" WHERE T.CUST_ID = ?) B, ")
				.append(" (SELECT TRUNC_AMOUNT_WEB(NVL(SUM(T.USABLE_AMOUNT),0))  accountUnuseValue")
				// 未使用体验金
				.append(" FROM BAO_T_CUST_ACTIVITY_INFO T WHERE T.CUST_ID = ?  ")
				.append(" AND T.TRADE_STATUS IN ('已领取', '部分使用') ) C, ")
				.append(" (select nvl(sum(TRUNC_AMOUNT_WEB(c.value)), 0) accountUnuseAmount ")
				// 未使用推荐奖励
				.append("   from (select t.id, ")
				.append("                t.login_name, ")
				.append("                t.create_date, ")
				.append("                case ")
				.append("                  when t.credentials_code is null then ")
				.append("                   '否' ")
				.append("                  else ")
				.append("                   '是' ")
				.append("                end as realname, ")
				.append("                t.spread_level - ")
				.append("                (select to_number(spread_level) ")
				.append("                   from bao_t_cust_info ")
				.append("                  where id = ?) as spread_level ")
				.append("           from bao_t_cust_info t ")
				.append("          where t.query_permission like ")
				.append("                (select query_permission ")
				.append("                   from bao_t_cust_info ")
				.append("                  where id = ?) || '%' ")
				.append("            and id != ? ")
				.append("            and to_number(t.spread_level) <= ")
				.append("                (select to_number(spread_level) ")
				.append("                   from bao_t_cust_info ")
				.append("                  where id = ?) + "
						+ Constant.AWARD_SPREAD_LEVEL + ") a, ")
				.append("        (select * from BAO_T_CUST_ACTIVITY_INFO where activity_id = '2') b, ")
				.append("        (select t.parameter_name, t.value ")
				.append("           from com_t_param t ")
				.append("          where t.type = 'activityRecommend') c ")
				.append("  where a.id = b.cust_id ")
				.append("    and a.spread_level = c.parameter_name ")
				.append("    and b.trade_status = '未结算' ) D");

		List<Map<String, Object>> list = repositoryUtil.queryForMap(
				sqlString.toString(),
				new Object[] { (String) param.get("custId"),
						Constant.TABLE_BAO_T_INVEST_INFO,
						Constant.VALID_STATUS_VALID,
						(String) param.get("custId"),
						(String) param.get("custId"),
						(String) param.get("custId"),
						(String) param.get("custId"),
						(String) param.get("custId"),
						(String) param.get("custId") });
		if (list == null || list.size() == 0)
			return new BigDecimal("0");
		Map<String, Object> map = list.get(0);
		if (map == null || map.size() == 0)
			return new BigDecimal("0");

		return (BigDecimal) map.get("tradeAmount");
	}

	/**
	 * 获取加入记录
	 */
	@Override
	public Map<String, Object> joinedCount(Map<String, Object> paramsMap)
			throws SLException {
		StringBuffer sql = new StringBuffer(
				"SELECT COUNT(BTIDI.ID) \"count\" FROM BAO_T_INVEST_DETAIL_INFO BTIDI,BAO_T_INVEST_INFO BTII,BAO_T_CUST_INFO BTCI WHERE BTIDI.INVEST_ID = BTII.ID AND BTII.CUST_ID = BTCI.ID AND BTII.PRODUCT_ID = (SELECT ID FROM BAO_T_PRODUCT_INFO WHERE PRODUCT_NAME = ? ) ");
		List<Map<String, Object>> countMap = repositoryUtil.queryForMap(
				sql.toString(),
				new Object[] { (String) paramsMap.get("productName") });
		return (null != countMap && countMap.size() > 0) ? countMap.get(0)
				: null;
	}

	/**
	 * 查询用户持有份额
	 */
	@Override
	public BigDecimal getShareHoldingAmount(Map<String, Object> params) {
		String sql = "select sum(subaccount.account_total_value) from bao_t_cust_info cust,bao_t_sub_account_info subaccount,bao_t_invest_info invest,bao_t_product_info product,bao_t_product_type_info protype"
				+ " where cust.id=invest.cust_id and invest.product_id=product.id and product.product_type=protype.id and protype.type_name in ('体验宝','活期宝') "
				+ " and invest.id=subaccount.relate_primary"
				+ " and subaccount.relate_type='"
				+ Constant.TABLE_BAO_T_INVEST_INFO
				+ "' and subaccount.record_status='有效' and cust.id=?";
		Object obj = entityManager.createNativeQuery(sql)
				.setParameter(1, params.get("custId")).getSingleResult();
		if (obj == null) {
			return BigDecimal.ZERO;
		} else {
			return new BigDecimal(obj + "");
		}
	}

	/**
	 * 查询用户对产品的持有份额
	 */
	@Override
	public BigDecimal getHoldingAmount(String custId, List<String> productList)
			throws SLException {
		StringBuffer sql = new StringBuffer(
				" select sum(subaccount.account_total_value) \"holdingAmount\" from bao_t_cust_info cust,bao_t_sub_account_info subaccount,bao_t_invest_info invest,bao_t_product_info product,bao_t_product_type_info protype");
		StringBuilder condition = new StringBuilder(
				" where cust.id=invest.cust_id and invest.product_id=product.id and product.product_type=protype.id  and invest.id=subaccount.relate_primary and subaccount.record_status='有效' ");
		List<Object> objList = new ArrayList<>();

		PageFuns.buildWhereSql(condition)
				.append(" subaccount.relate_type = ? ");
		objList.add(Constant.TABLE_BAO_T_INVEST_INFO);

		if (!StringUtils.isEmpty(custId)) {
			PageFuns.buildWhereSql(condition).append(" cust.id=? ");
			objList.add(custId);
		}
		if (productList != null && productList.size() > 0) {
			condition.append(" and protype.type_name in ").append(
					PageFuns.buildWhereInParams(productList, objList));
		}
		List<Map<String, Object>> holdAmountMap = repositoryUtil.queryForMap(
				sql.append(condition).toString(), objList.toArray());
		if (holdAmountMap != null && holdAmountMap.size() == 1
				&& holdAmountMap.get(0).containsKey("holdingAmount"))
			return (BigDecimal) holdAmountMap.get(0).get("holdingAmount");
		return BigDecimal.ZERO;

	}

	/**
	 * 查询合并后的分页记录数据
	 */
	@Override
	public Page<AccountFlowInfoEntity> getUnionAccountFlowPage(
			Map<String, Object> paramsMap) throws SLException {
		String tradeTypeList = (String) paramsMap.get("tradeTypeList");
		String[] tradeTypeArrays = !StringUtils.isEmpty(tradeTypeList) ? tradeTypeList.split(",") : null;
		
		StringBuilder sql = new StringBuilder("SELECT TRADE_DATE,TRADE_TYPE ");
		String orderSql = " ORDER BY  CREATE_DATE DESC ";
		String groupSql = "";
		if(Arrays.asList(tradeTypeArrays).contains(SubjectConstant.TRADE_FLOW_TYPE_01) || Arrays.asList(tradeTypeArrays).contains(SubjectConstant.TRADE_FLOW_TYPE_03)){
			sql.append(" ,SUM(NVL(TRADE_AMOUNT,0)) TRADE_AMOUNT ");
			orderSql = " ORDER BY  TRADE_DATE DESC ";
			groupSql = " GROUP BY  TRADE_DATE,TRADE_TYPE ";
		}
			
		if(!(Arrays.asList(tradeTypeArrays).contains(SubjectConstant.TRADE_FLOW_TYPE_01) || Arrays.asList(tradeTypeArrays).contains(SubjectConstant.TRADE_FLOW_TYPE_03)))
			sql.append(",NVL(TRADE_AMOUNT,0) TRADE_AMOUNT,CREATE_DATE ");
		
		/**赎回页面按照trade_no排序**/
		boolean contailFreezenOrUnFreezen = Lists.newArrayList(tradeTypeArrays).contains(SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_NORMAL_FREEZE) && Lists.newArrayList(tradeTypeArrays).contains(SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_NORMAL_UNFREEZE);
		if(contailFreezenOrUnFreezen)
			sql.append(" TRADE_NO ");
		if( contailFreezenOrUnFreezen || Arrays.asList(tradeTypeArrays).equals(Arrays.asList(SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_TERM_FINISH,SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_TERM_ADVANCE)) || Arrays.asList(tradeTypeArrays).equals(Arrays.asList(SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_NORMAL,SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE)))
			orderSql = " ORDER BY TRADE_NO DESC ";
		
		
		sql.append(" FROM  BAO_T_ACCOUNT_FLOW_INFO ");
		StringBuilder condition = new StringBuilder();
		List<Object> objList = new ArrayList<>();
		// 用户
		if (!StringUtils.isEmpty((String) paramsMap.get("custId"))) {
			PageFuns.buildWhereSql(condition).append(" CUST_ID=? ");
			objList.add((String) paramsMap.get("custId"));
		}


		if (tradeTypeArrays != null && tradeTypeArrays.length > 0) {
			condition.append(" AND TRADE_TYPE IN ").append(PageFuns.buildWhereInParams(Arrays.asList(tradeTypeArrays),objList));
			/** 体验宝收益或者活期宝收益时,是分账 **/
			if (null != tradeTypeArrays && tradeTypeArrays.length == 1 && Arrays.asList(SubjectConstant.TRADE_FLOW_TYPE_01,SubjectConstant.TRADE_FLOW_TYPE_03).contains(tradeTypeArrays[tradeTypeArrays.length - 1])) {
				condition.append(" AND ACCOUNT_TYPE = ? ");
				objList.add(Constant.ACCOUNT_TYPE_SUB);
			} else {
				condition.append(" AND ACCOUNT_TYPE = ? ");
				objList.add(Constant.ACCOUNT_TYPE_MAIN);
			}
			String tempSql = sql.toString();
			sql.append(condition).append(" AND TRADE_AMOUNT > 0 ").append(groupSql);
			if( Lists.newArrayList(tradeTypeArrays).contains(SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_NORMAL_FREEZE) && Lists.newArrayList(tradeTypeArrays).contains(SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_NORMAL_UNFREEZE) ){
				sql.append(" UNION ALL ").append(tempSql)
				.append(" WHERE CUST_ID = ? AND TRADE_TYPE IN ( ?, ?) AND ACCOUNT_TYPE = ? AND TRADE_AMOUNT > 0 ").append(groupSql); 
				objList.add((String) paramsMap.get("custId"));
				objList.add(SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_NORMAL_FREEZE);
				objList.add(SubjectConstant.ACCOUNT_FLOW_SUBJECT_TYPE_COMMON_ATONE_NORMAL_UNFREEZE);
				objList.add(Constant.ACCOUNT_TYPE_SUB);
			}
		}
	
		return repositoryUtil.queryForPage(sql.append(orderSql).toString(), objList.toArray(),(int) paramsMap.get("pageNum"),(int) paramsMap.get("pageSize"),AccountFlowInfoEntity.class);
	}

	@Override
	public Page<Map<String, Object>> findCompanyAccount(
			Map<String, Object> param) {
		if ("02".equals((String) param.get("companyType"))) { // 收益人账户
			StringBuffer sqlString = new StringBuffer()
					.append(" select distinct S2.ID \"id\", S.LOGIN_NAME \"nickName\", S.CUST_NAME \"custName\",    ")
					.append("              S.CREDENTIALS_CODE \"credentialsCode\", Q.TRADE_TYPE \"tradeType\", TRUNC_AMOUNT_WEB(Q.TRADE_AMOUNT) \"tradeAmount\",    ")
					.append("              TRUNC_AMOUNT_WEB(Q.ACCOUNT_AVAILABLE) \"accountAvailable\", TRUNC_AMOUNT_WEB(Q.ACCOUNT_FREEZE_AMOUNT) \"accountFreezeAmount\",    ")
					.append("              Q.BANKROLL_FLOW_DIRECTION \"bankrollFlowDirection\", Q.CREATE_DATE \"operateDate\" , Q.MEMO \"memo\",     ")
					.append("              S2.Login_Name \"targetNickName\", S2.Cust_Name \"targetCustName\", S2.Credentials_Code \"targetCredentialsCode\"  ")
					.append("        from ")
					.append("        ( ")
					.append("          select distinct q.id, company_id, account_id, TRADE_TYPE, trade_amount, account_available, account_freeze_amount, ")
					.append("                 account_total_amount, bankroll_flow_direction, create_date, cust_id, memo ")
					.append("          from ")
					.append("          ( ")
					.append("            (select t.id, t.cust_id company_id, t.account_id, t.TRADE_TYPE, t.trade_amount, t.account_available, t.account_freeze_amount,  ")
					.append("                    t.account_total_amount, t.bankroll_flow_direction, t.create_date, t.request_no, t.memo ")
					.append("             from BAO_T_ACCOUNT_FLOW_INFO t  ")
					.append("            where  t.ACCOUNT_TYPE = '总账' AND t.CUST_ID = 'C00002' AND T.TRADE_TYPE != '实名认证手续费')Q ")
					.append("            INNER JOIN ")
					.append("            (select t.cust_id, t.request_no from BAO_T_ACCOUNT_FLOW_INFO t ")
					.append("            where t.CUST_ID NOT IN ('C00007', 'C00002'))P ")
					.append("            ON Q.REQUEST_NO = P.REQUEST_NO ")
					.append("          ) ")
					.append("          UNION ")
					.append("          select distinct t.id, t.cust_id company_id, account_id, TRADE_TYPE, trade_amount, account_available, account_freeze_amount, ")
					.append("                 account_total_amount, bankroll_flow_direction, t.create_date, t.relate_primary cust_id, t.memo  ")
					.append("          from BAO_T_ACCOUNT_FLOW_INFO t ")
					.append("          where  t.ACCOUNT_TYPE = '总账' AND t.CUST_ID = 'C00002' AND T.TRADE_TYPE = '实名认证手续费' ")
// 2016/05/24 udpate by liyy
//					.append("          UNION ")
//					.append("          select distinct t.id, t.cust_id company_id, account_id, TRADE_TYPE, trade_amount, account_available, account_freeze_amount, ")
//					.append("                 account_total_amount, bankroll_flow_direction, t.create_date, '' cust_id, t.memo  ")
//					.append("          from BAO_T_ACCOUNT_FLOW_INFO t ")
//					.append("          where  t.ACCOUNT_TYPE = '总账' AND t.CUST_ID = 'C00002' AND T.TRADE_TYPE = '快速赎回额度调整' ")
//					.append("          and not exists ( ")
//					.append("              select 1 ")
//					.append("              from BAO_T_ACCOUNT_FLOW_INFO s  ")
//					.append("              where s.CUST_ID != 'C00002'  ")
//					.append("              and s.REQUEST_NO = t.REQUEST_NO ")
//					.append("          ) ")
					.append("        ) Q ")
					.append("        INNER JOIN BAO_T_CUST_INFO S ON Q.company_id = S.ID  ")
					.append("        LEFT JOIN BAO_T_CUST_INFO S2 ON Q.CUST_ID = S2.ID ")
					.append("        WHERE 1 = 1 ");

			List<Object> objList = new ArrayList<Object>();
			if (!StringUtils.isEmpty(param.get("nickName"))) {
				sqlString.append(" and S2.LOGIN_NAME LIKE ?");
				objList.add(new StringBuffer().append("%")
						.append(param.get("nickName")).append("%"));
			}

			if (!StringUtils.isEmpty(param.get("custName"))) {
				sqlString.append(" and S2.CUST_NAME = ?");
				objList.add(param.get("custName"));
			}

			if (!StringUtils.isEmpty(param.get("credentialsCode"))) {
				sqlString.append(" and S2.CREDENTIALS_CODE = ?");
				objList.add(param.get("credentialsCode"));
			}

			if (!StringUtils.isEmpty(param.get("opearteDateBegin"))) {
				sqlString.append(" and Q.CREATE_DATE >= ?");
				objList.add(DateUtils.parseStandardDate((String) param
						.get("opearteDateBegin")));
			}

			if (!StringUtils.isEmpty(param.get("opearteDateEnd"))) {
				sqlString.append(" and Q.CREATE_DATE <= ?");
				objList.add(DateUtils.getEndDate(DateUtils
						.parseStandardDate((String) param.get("opearteDateEnd"))));
			}

			if (!StringUtils.isEmpty(param.get("tradeType"))) {
				sqlString.append(" and Q.TRADE_TYPE = ?");
				objList.add(param.get("tradeType"));
			}

			if (!StringUtils.isEmpty(param.get("bankrollFlowDirection"))) {
				sqlString.append(" and Q.BANKROLL_FLOW_DIRECTION = ?");
				objList.add(param.get("bankrollFlowDirection"));
			}

			sqlString.append(" ORDER BY Q.CREATE_DATE DESC ");

			return repositoryUtil.queryForPageMap(sqlString.toString(),
					objList.toArray(), (int) param.get("start"),
					(int) param.get("length"));
		} else if ("01".equals((String) param.get("companyType"))) { // 居间人账户

			StringBuffer sqlString = new StringBuffer()
					.append(" select distinct S2.ID \"id\", S.LOGIN_NAME \"nickName\", S.CUST_NAME \"custName\",   ")
					.append("             S.CREDENTIALS_CODE \"credentialsCode\", Q.TRADE_TYPE \"tradeType\", TRUNC_AMOUNT_WEB(Q.TRADE_AMOUNT) \"tradeAmount\",   ")
					.append("             TRUNC_AMOUNT_WEB(Q.ACCOUNT_AVAILABLE) \"accountAvailable\", TRUNC_AMOUNT_WEB(Q.ACCOUNT_FREEZE_AMOUNT) \"accountFreezeAmount\",   ")
					.append("             Q.BANKROLL_FLOW_DIRECTION \"bankrollFlowDirection\", Q.CREATE_DATE \"operateDate\" , Q.MEMO \"memo\",    ")
					.append("             S2.Login_Name \"targetNickName\", S2.Cust_Name \"targetCustName\", S2.Credentials_Code \"targetCredentialsCode\" ")
					.append("        from ")
					.append("        ( ")
					.append("          select distinct q.id, company_id, account_id, TRADE_TYPE, trade_amount, account_available, account_freeze_amount, ")
					.append("                 account_total_amount, bankroll_flow_direction, create_date, cust_id, memo ")
					.append("          from ")
					.append("          ( ")
					.append("            (select t.id, t.cust_id company_id, t.account_id, t.TRADE_TYPE, t.trade_amount, t.account_available, t.account_freeze_amount,  ")
					.append("                    t.account_total_amount, t.bankroll_flow_direction, t.create_date, t.request_no, t.memo ")
					.append("             from BAO_T_ACCOUNT_FLOW_INFO t  ")
					.append("            where  t.ACCOUNT_TYPE = '总账' AND t.CUST_ID = 'C00007')Q ")
					.append("            INNER JOIN ")
					.append("            (select t.cust_id, t.request_no from BAO_T_ACCOUNT_FLOW_INFO t ")
					.append("            where t.CUST_ID NOT IN ('C00007', 'C00002'))P ")
					.append("            ON Q.REQUEST_NO = P.REQUEST_NO ")
					.append("          ) ")
					.append("          UNION ")
					.append("          select distinct t.id, t.cust_id company_id, account_id, TRADE_TYPE, trade_amount, account_available, account_freeze_amount, ")
					.append("                 account_total_amount, bankroll_flow_direction, t.create_date, '' cust_id, t.memo  ")
					.append("          from BAO_T_ACCOUNT_FLOW_INFO t ")
					.append("          where  t.ACCOUNT_TYPE = '总账' AND t.CUST_ID = 'C00007' AND T.TRADE_TYPE IN ('购买债权') ")
					.append("        ) Q ")
					.append("        INNER JOIN BAO_T_CUST_INFO S ON Q.company_id = S.ID  ")
					.append("        LEFT JOIN BAO_T_CUST_INFO S2 ON Q.CUST_ID = S2.ID ")
					.append("        WHERE 1 = 1 ");

			List<Object> objList = new ArrayList<Object>();
			if (!StringUtils.isEmpty(param.get("nickName"))) {
				sqlString.append(" and S2.LOGIN_NAME LIKE ?");
				objList.add(new StringBuffer().append("%")
						.append(param.get("nickName")).append("%"));
			}

			if (!StringUtils.isEmpty(param.get("custName"))) {
				sqlString.append(" and S2.CUST_NAME = ?");
				objList.add(param.get("custName"));
			}

			if (!StringUtils.isEmpty(param.get("credentialsCode"))) {
				sqlString.append(" and S2.CREDENTIALS_CODE = ?");
				objList.add(param.get("credentialsCode"));
			}

			if (!StringUtils.isEmpty(param.get("opearteDateBegin"))) {
				sqlString.append(" and Q.CREATE_DATE >= ?");
				objList.add(DateUtils.parseStandardDate((String) param
						.get("opearteDateBegin")));
			}

			if (!StringUtils.isEmpty(param.get("opearteDateEnd"))) {
				sqlString.append(" and Q.CREATE_DATE <= ?");
				objList.add(DateUtils.getEndDate(DateUtils
						.parseStandardDate((String) param.get("opearteDateEnd"))));
			}

			if (!StringUtils.isEmpty(param.get("tradeType"))) {
				sqlString.append(" and Q.TRADE_TYPE = ?");
				objList.add(param.get("tradeType"));
			}

			if (!StringUtils.isEmpty(param.get("bankrollFlowDirection"))) {
				sqlString.append(" and Q.BANKROLL_FLOW_DIRECTION = ?");
				objList.add(param.get("bankrollFlowDirection"));
			}

			sqlString.append(" ORDER BY Q.CREATE_DATE DESC ");

			return repositoryUtil.queryForPageMap(sqlString.toString(),
					objList.toArray(), (int) param.get("start"),
					(int) param.get("length"));
		} else if ("03".equals((String) param.get("companyType"))) { // 风险金账户

			StringBuffer sqlString = new StringBuffer()
					.append(" select distinct S2.ID \"id\", S.LOGIN_NAME \"nickName\", S.CUST_NAME \"custName\",   ")
					.append("             S.CREDENTIALS_CODE \"credentialsCode\", Q.TRADE_TYPE \"tradeType\", TRUNC_AMOUNT_WEB(Q.TRADE_AMOUNT) \"tradeAmount\",   ")
					.append("             TRUNC_AMOUNT_WEB(Q.ACCOUNT_AVAILABLE) \"accountAvailable\", TRUNC_AMOUNT_WEB(Q.ACCOUNT_FREEZE_AMOUNT) \"accountFreezeAmount\",   ")
					.append("             Q.BANKROLL_FLOW_DIRECTION \"bankrollFlowDirection\", Q.CREATE_DATE \"operateDate\" , Q.MEMO \"memo\",   ")
					.append("             S2.Login_Name \"targetNickName\", S2.Cust_Name \"targetCustName\", S2.Credentials_Code \"targetCredentialsCode\" ")
					.append("        from ")
					.append("        ( ")
					.append("          select distinct q.id, company_id, account_id, TRADE_TYPE, trade_amount, account_available, account_freeze_amount, ")
					.append("                 account_total_amount, bankroll_flow_direction, create_date, cust_id, memo ")
					.append("          from ")
					.append("          ( ")
					.append("            (select t.id, t.cust_id company_id, t.account_id, t.TRADE_TYPE, t.trade_amount, t.account_available, t.account_freeze_amount,  ")
					.append("                    t.account_total_amount, t.bankroll_flow_direction, t.create_date, t.request_no, t.memo ")
					.append("             from BAO_T_ACCOUNT_FLOW_INFO t  ")
					.append("            where  t.ACCOUNT_TYPE = '总账' AND t.CUST_ID = 'C00006')Q ")
					.append("            INNER JOIN ")
					.append("            (select t.cust_id, t.request_no from BAO_T_ACCOUNT_FLOW_INFO t ")
					.append("            where t.CUST_ID NOT IN ('C00006'))P ")
					.append("            ON Q.REQUEST_NO = P.REQUEST_NO ")
					.append("          ) ")
					.append("        ) Q ")
					.append("        INNER JOIN BAO_T_CUST_INFO S ON Q.company_id = S.ID  ")
					.append("        LEFT JOIN BAO_T_CUST_INFO S2 ON Q.CUST_ID = S2.ID ")
					.append("        WHERE 1 = 1 ");

			List<Object> objList = new ArrayList<Object>();
			if (!StringUtils.isEmpty(param.get("nickName"))) {
				sqlString.append(" and S2.LOGIN_NAME LIKE ?");
				objList.add(new StringBuffer().append("%")
						.append(param.get("nickName")).append("%"));
			}

			if (!StringUtils.isEmpty(param.get("custName"))) {
				sqlString.append(" and S2.CUST_NAME = ?");
				objList.add(param.get("custName"));
			}

			if (!StringUtils.isEmpty(param.get("credentialsCode"))) {
				sqlString.append(" and S2.CREDENTIALS_CODE = ?");
				objList.add(param.get("credentialsCode"));
			}

			if (!StringUtils.isEmpty(param.get("opearteDateBegin"))) {
				sqlString.append(" and Q.CREATE_DATE >= ?");
				objList.add(DateUtils.parseStandardDate((String) param
						.get("opearteDateBegin")));
			}

			if (!StringUtils.isEmpty(param.get("opearteDateEnd"))) {
				sqlString.append(" and Q.CREATE_DATE <= ?");
				objList.add(DateUtils.getEndDate(DateUtils
						.parseStandardDate((String) param.get("opearteDateEnd"))));
			}

			if (!StringUtils.isEmpty(param.get("tradeType"))) {
				sqlString.append(" and Q.TRADE_TYPE = ?");
				objList.add(param.get("tradeType"));
			}

			if (!StringUtils.isEmpty(param.get("bankrollFlowDirection"))) {
				sqlString.append(" and Q.BANKROLL_FLOW_DIRECTION = ?");
				objList.add(param.get("bankrollFlowDirection"));
			}

			sqlString.append(" ORDER BY Q.CREATE_DATE DESC ");

			return repositoryUtil.queryForPageMap(sqlString.toString(),
					objList.toArray(), (int) param.get("start"),
					(int) param.get("length"));
		} else if ("04".equals((String) param.get("companyType"))) { //业绩佣金账户 
			StringBuffer sqlString = new StringBuffer()
			.append(" select m.ID \"id\" ")
			.append(" 	, n.Login_Name \"nickName\" ")
			.append(" 	, t.TRADE_TYPE \"tradeType\" ")
			.append(" 	, t.TRADE_AMOUNT \"tradeAmount\" ")
			.append(" 	, t.BANKROLL_FLOW_DIRECTION \"bankrollFlowDirection\" ")
			.append(" 	, m.Login_Name \"targetNickName\" ")
			.append(" 	, m.CUST_NAME \"targetCustName\" ")
			.append(" 	, m.CREDENTIALS_CODE \"targetCredentialsCode\" ")
			.append(" 	, t.MEMO \"memo\" ")
			.append(" 	, t.CREATE_DATE \"operateDate\" ")
			.append("  from BAO_T_ACCOUNT_FLOW_INFO t  ")
			.append(" inner join bao_t_account_info s on s.id = t.target_account /* 对方信息 */ ")
			.append(" inner join bao_t_cust_info m on m.id = s.cust_id /* 对方信息 */ ")
			.append(" inner join bao_t_cust_info n on n.id = t.cust_id /* 信息 */ ")
			.append(" where t.ACCOUNT_TYPE = '总账'  ")
			.append(" and t.cust_id = 'C00008' ")
			;
			List<Object> objList = new ArrayList<Object>();
			if (!StringUtils.isEmpty(param.get("nickName"))) {
				sqlString.append(" and m.LOGIN_NAME LIKE ?");
				objList.add(new StringBuffer().append("%").append(param.get("nickName")).append("%"));
			}
			if (!StringUtils.isEmpty(param.get("custName"))) {
				sqlString.append(" and m.CUST_NAME = ?");
				objList.add(param.get("custName"));
			}
			if (!StringUtils.isEmpty(param.get("credentialsCode"))) {
				sqlString.append(" and m.CREDENTIALS_CODE = ?");
				objList.add(param.get("credentialsCode"));
			}
			if (!StringUtils.isEmpty(param.get("opearteDateBegin"))) {
				sqlString.append(" and t.CREATE_DATE >= ?");
				objList.add(DateUtils.parseStandardDate((String) param.get("opearteDateBegin")));
			}
			if (!StringUtils.isEmpty(param.get("opearteDateEnd"))) {
				sqlString.append(" and t.CREATE_DATE <= ?");
				objList.add(DateUtils.getEndDate(DateUtils.parseStandardDate((String) param.get("opearteDateEnd"))));
			}
			if (!StringUtils.isEmpty(param.get("tradeType"))) {
				sqlString.append(" and t.TRADE_TYPE = ?");
				objList.add(param.get("tradeType"));
			}
			if (!StringUtils.isEmpty(param.get("bankrollFlowDirection"))) {
				sqlString.append(" and t.BANKROLL_FLOW_DIRECTION = ?");
				objList.add(param.get("bankrollFlowDirection"));
			}
			sqlString.append(" ORDER BY t.CREATE_DATE DESC ");
			
			return repositoryUtil.queryForPageMap(sqlString.toString(),
					objList.toArray(), (int) param.get("start"),
					(int) param.get("length"));
		}

		return null;
	}

	@Override
	public Map<String, Object> findCompanyAccountSum(Map<String, Object> param) {
		if ("02".equals((String) param.get("companyType"))) { // 收益人账户
			StringBuffer sqlString = new StringBuffer()
					.append(" select NVL(SUM(TRUNC_AMOUNT_WEB(Q.TRADE_AMOUNT)), 0) \"totalTradeAmount\"  ")
					.append("        from ")
					.append("        ( ")
					.append("          select distinct q.id, company_id, account_id, TRADE_TYPE, trade_amount, account_available, account_freeze_amount, ")
					.append("                 account_total_amount, bankroll_flow_direction, create_date, cust_id ")
					.append("          from ")
					.append("          ( ")
					.append("            (select t.id, t.cust_id company_id, t.account_id, t.TRADE_TYPE, t.trade_amount, t.account_available, t.account_freeze_amount,  ")
					.append("                    t.account_total_amount, t.bankroll_flow_direction, t.create_date, t.request_no ")
					.append("             from BAO_T_ACCOUNT_FLOW_INFO t  ")
					.append("            where  t.ACCOUNT_TYPE = '总账' AND t.CUST_ID = 'C00002' AND T.TRADE_TYPE != '实名认证手续费')Q ")
					.append("            INNER JOIN ")
					.append("            (select t.cust_id, t.request_no from BAO_T_ACCOUNT_FLOW_INFO t ")
					.append("            where t.CUST_ID NOT IN ('C00007', 'C00002'))P ")
					.append("            ON Q.REQUEST_NO = P.REQUEST_NO ")
					.append("          ) ")
					.append("          UNION ")
					.append("          select distinct t.id, t.cust_id company_id, account_id, TRADE_TYPE, trade_amount, account_available, account_freeze_amount, ")
// 2016/05/24 udpate by liyy Start
//					.append("                 account_total_amount, bankroll_flow_direction, t.create_date, s.relate_primary cust_id  ")
//					.append("          from BAO_T_ACCOUNT_FLOW_INFO t inner join BAO_T_FLOW_BUSI_RELATION s on t.id = s.account_flow_id ")
					.append("                 account_total_amount, bankroll_flow_direction, t.create_date, t.relate_primary cust_id  ")
					.append("          from BAO_T_ACCOUNT_FLOW_INFO t ")
// 2016/05/24 udpate by liyy End
					.append("          where  t.ACCOUNT_TYPE = '总账' AND t.CUST_ID = 'C00002' AND T.TRADE_TYPE = '实名认证手续费' ")
					.append("        ) Q ")
					.append("        INNER JOIN BAO_T_CUST_INFO S ON Q.company_id = S.ID  ")
					.append("        INNER JOIN BAO_T_CUST_INFO S2 ON Q.CUST_ID = S2.ID ")
					.append("        WHERE 1 = 1 ");

			List<Object> objList = new ArrayList<Object>();
			if (!StringUtils.isEmpty(param.get("nickName"))) {
				sqlString.append(" and S2.LOGIN_NAME LIKE ?");
				objList.add(new StringBuffer().append("%")
						.append(param.get("nickName")).append("%"));
			}

			if (!StringUtils.isEmpty(param.get("custName"))) {
				sqlString.append(" and S2.CUST_NAME = ?");
				objList.add(param.get("custName"));
			}

			if (!StringUtils.isEmpty(param.get("credentialsCode"))) {
				sqlString.append(" and S2.CREDENTIALS_CODE = ?");
				objList.add(param.get("credentialsCode"));
			}

			if (!StringUtils.isEmpty(param.get("opearteDateBegin"))) {
				sqlString.append(" and Q.CREATE_DATE >= ?");
				objList.add(DateUtils.parseStandardDate((String) param
						.get("opearteDateBegin")));
			}

			if (!StringUtils.isEmpty(param.get("opearteDateEnd"))) {
				sqlString.append(" and Q.CREATE_DATE <= ?");
				objList.add(DateUtils.getEndDate(DateUtils
						.parseStandardDate((String) param.get("opearteDateEnd"))));
			}

			if (!StringUtils.isEmpty(param.get("tradeType"))) {
				sqlString.append(" and Q.TRADE_TYPE = ?");
				objList.add(param.get("tradeType"));
			}

			if (!StringUtils.isEmpty(param.get("bankrollFlowDirection"))) {
				sqlString.append(" and Q.BANKROLL_FLOW_DIRECTION = ?");
				objList.add(param.get("bankrollFlowDirection"));
			}

			List<Map<String, Object>> list = repositoryUtil.queryForMap(
					sqlString.toString(), objList.toArray());
			if (list == null || list.size() == 0) {
				return new HashMap<String, Object>();
			}
			return list.get(0);
		} else if ("01".equals((String) param.get("companyType"))) { // 居间人账户

			StringBuffer sqlString = new StringBuffer()
					.append(" select NVL(SUM(TRUNC_AMOUNT_WEB(Q.TRADE_AMOUNT)), 0) \"totalTradeAmount\"   ")
					.append("        from ")
					.append("        ( ")
					.append("          select distinct q.id, company_id, account_id, TRADE_TYPE, trade_amount, account_available, account_freeze_amount, ")
					.append("                 account_total_amount, bankroll_flow_direction, create_date, cust_id ")
					.append("          from ")
					.append("          ( ")
					.append("            (select t.id, t.cust_id company_id, t.account_id, t.TRADE_TYPE, t.trade_amount, t.account_available, t.account_freeze_amount,  ")
					.append("                    t.account_total_amount, t.bankroll_flow_direction, t.create_date, t.request_no ")
					.append("             from BAO_T_ACCOUNT_FLOW_INFO t  ")
					.append("            where  t.ACCOUNT_TYPE = '总账' AND t.CUST_ID = 'C00007')Q ")
					.append("            INNER JOIN ")
					.append("            (select t.cust_id, t.request_no from BAO_T_ACCOUNT_FLOW_INFO t ")
					.append("            where t.CUST_ID NOT IN ('C00007', 'C00002'))P ")
					.append("            ON Q.REQUEST_NO = P.REQUEST_NO ")
					.append("          ) ")
					.append("          UNION ")
					.append("          select distinct t.id, t.cust_id company_id, account_id, TRADE_TYPE, trade_amount, account_available, account_freeze_amount, ")
					.append("                 account_total_amount, bankroll_flow_direction, t.create_date, '' cust_id  ")
					.append("          from BAO_T_ACCOUNT_FLOW_INFO t ")
					.append("          where  t.ACCOUNT_TYPE = '总账' AND t.CUST_ID = 'C00007' AND T.TRADE_TYPE IN ('购买债权') ")
					.append("        ) Q ")
					.append("        INNER JOIN BAO_T_CUST_INFO S ON Q.company_id = S.ID  ")
					.append("        LEFT JOIN BAO_T_CUST_INFO S2 ON Q.CUST_ID = S2.ID ")
					.append("        WHERE 1 = 1 ");

			List<Object> objList = new ArrayList<Object>();
			if (!StringUtils.isEmpty(param.get("nickName"))) {
				sqlString.append(" and S2.LOGIN_NAME LIKE ?");
				objList.add(new StringBuffer().append("%")
						.append(param.get("nickName")).append("%"));
			}

			if (!StringUtils.isEmpty(param.get("custName"))) {
				sqlString.append(" and S2.CUST_NAME = ?");
				objList.add(param.get("custName"));
			}

			if (!StringUtils.isEmpty(param.get("credentialsCode"))) {
				sqlString.append(" and S2.CREDENTIALS_CODE = ?");
				objList.add(param.get("credentialsCode"));
			}

			if (!StringUtils.isEmpty(param.get("opearteDateBegin"))) {
				sqlString.append(" and Q.CREATE_DATE >= ?");
				objList.add(DateUtils.parseStandardDate((String) param
						.get("opearteDateBegin")));
			}

			if (!StringUtils.isEmpty(param.get("opearteDateEnd"))) {
				sqlString.append(" and Q.CREATE_DATE <= ?");
				objList.add(DateUtils.getEndDate(DateUtils
						.parseStandardDate((String) param.get("opearteDateEnd"))));
			}

			if (!StringUtils.isEmpty(param.get("tradeType"))) {
				sqlString.append(" and Q.TRADE_TYPE = ?");
				objList.add(param.get("tradeType"));
			}

			if (!StringUtils.isEmpty(param.get("bankrollFlowDirection"))) {
				sqlString.append(" and Q.BANKROLL_FLOW_DIRECTION = ?");
				objList.add(param.get("bankrollFlowDirection"));
			}

			List<Map<String, Object>> list = repositoryUtil.queryForMap(
					sqlString.toString(), objList.toArray());
			if (list == null || list.size() == 0) {
				return new HashMap<String, Object>();
			}
			return list.get(0);
		} else if ("03".equals((String) param.get("companyType"))) { // 风险金账户

			StringBuffer sqlString = new StringBuffer()
					.append(" select NVL(SUM(TRUNC_AMOUNT_WEB(Q.TRADE_AMOUNT)), 0) \"totalTradeAmount\"   ")
					.append("        from ")
					.append("        ( ")
					.append("          select distinct q.id, company_id, account_id, TRADE_TYPE, trade_amount, account_available, account_freeze_amount, ")
					.append("                 account_total_amount, bankroll_flow_direction, create_date, cust_id ")
					.append("          from ")
					.append("          ( ")
					.append("            (select t.id, t.cust_id company_id, t.account_id, t.TRADE_TYPE, t.trade_amount, t.account_available, t.account_freeze_amount,  ")
					.append("                    t.account_total_amount, t.bankroll_flow_direction, t.create_date, t.request_no ")
					.append("             from BAO_T_ACCOUNT_FLOW_INFO t  ")
					.append("            where  t.ACCOUNT_TYPE = '总账' AND t.CUST_ID = 'C00006')Q ")
					.append("            INNER JOIN ")
					.append("            (select t.cust_id, t.request_no from BAO_T_ACCOUNT_FLOW_INFO t ")
					.append("            where t.CUST_ID NOT IN ('C00006'))P ")
					.append("            ON Q.REQUEST_NO = P.REQUEST_NO ")
					.append("          ) ")
					.append("        ) Q ")
					.append("        INNER JOIN BAO_T_CUST_INFO S ON Q.company_id = S.ID  ")
					.append("        LEFT JOIN BAO_T_CUST_INFO S2 ON Q.CUST_ID = S2.ID ")
					.append("        WHERE 1 = 1 ");

			List<Object> objList = new ArrayList<Object>();
			if (!StringUtils.isEmpty(param.get("nickName"))) {
				sqlString.append(" and S2.LOGIN_NAME LIKE ?");
				objList.add(new StringBuffer().append("%")
						.append(param.get("nickName")).append("%"));
			}

			if (!StringUtils.isEmpty(param.get("custName"))) {
				sqlString.append(" and S2.CUST_NAME = ?");
				objList.add(param.get("custName"));
			}

			if (!StringUtils.isEmpty(param.get("credentialsCode"))) {
				sqlString.append(" and S2.CREDENTIALS_CODE = ?");
				objList.add(param.get("credentialsCode"));
			}

			if (!StringUtils.isEmpty(param.get("opearteDateBegin"))) {
				sqlString.append(" and Q.CREATE_DATE >= ?");
				objList.add(DateUtils.parseStandardDate((String) param
						.get("opearteDateBegin")));
			}

			if (!StringUtils.isEmpty(param.get("opearteDateEnd"))) {
				sqlString.append(" and Q.CREATE_DATE <= ?");
				objList.add(DateUtils.getEndDate(DateUtils
						.parseStandardDate((String) param.get("opearteDateEnd"))));
			}

			if (!StringUtils.isEmpty(param.get("tradeType"))) {
				sqlString.append(" and Q.TRADE_TYPE = ?");
				objList.add(param.get("tradeType"));
			}

			if (!StringUtils.isEmpty(param.get("bankrollFlowDirection"))) {
				sqlString.append(" and Q.BANKROLL_FLOW_DIRECTION = ?");
				objList.add(param.get("bankrollFlowDirection"));
			}

			List<Map<String, Object>> list = repositoryUtil.queryForMap(
					sqlString.toString(), objList.toArray());
			if (list == null || list.size() == 0) {
				return new HashMap<String, Object>();
			}
			return list.get(0);
		} else if ("04".equals((String) param.get("companyType"))) { //业绩佣金账户 
			StringBuffer sqlString = new StringBuffer()
			.append(" select NVL(SUM(TRUNC_AMOUNT_WEB(t.TRADE_AMOUNT)), 0) \"totalTradeAmount\" ")
			.append("  from BAO_T_ACCOUNT_FLOW_INFO t  ")
			.append(" inner join bao_t_account_info s on s.id = t.target_account /* 对方信息 */ ")
			.append(" inner join bao_t_cust_info m on m.id = s.cust_id /* 对方信息 */ ")
			.append(" inner join bao_t_cust_info n on n.id = t.cust_id /* 信息 */ ")
			.append(" where t.ACCOUNT_TYPE = '总账'  ")
			.append(" and t.cust_id = 'C00008' ")
			;
			List<Object> objList = new ArrayList<Object>();
			if (!StringUtils.isEmpty(param.get("nickName"))) {
				sqlString.append(" and m.LOGIN_NAME LIKE ?");
				objList.add(new StringBuffer().append("%").append(param.get("nickName")).append("%"));
			}
			if (!StringUtils.isEmpty(param.get("custName"))) {
				sqlString.append(" and m.CUST_NAME = ?");
				objList.add(param.get("custName"));
			}
			if (!StringUtils.isEmpty(param.get("credentialsCode"))) {
				sqlString.append(" and m.CREDENTIALS_CODE = ?");
				objList.add(param.get("credentialsCode"));
			}
			if (!StringUtils.isEmpty(param.get("opearteDateBegin"))) {
				sqlString.append(" and t.CREATE_DATE >= ?");
				objList.add(DateUtils.parseStandardDate((String) param.get("opearteDateBegin")));
			}
			if (!StringUtils.isEmpty(param.get("opearteDateEnd"))) {
				sqlString.append(" and t.CREATE_DATE <= ?");
				objList.add(DateUtils.getEndDate(DateUtils.parseStandardDate((String) param.get("opearteDateEnd"))));
			}
			if (!StringUtils.isEmpty(param.get("tradeType"))) {
				sqlString.append(" and t.TRADE_TYPE = ?");
				objList.add(param.get("tradeType"));
			}
			if (!StringUtils.isEmpty(param.get("bankrollFlowDirection"))) {
				sqlString.append(" and t.BANKROLL_FLOW_DIRECTION = ?");
				objList.add(param.get("bankrollFlowDirection"));
			}
			
			List<Map<String, Object>> list = repositoryUtil.queryForMap(
					sqlString.toString(), objList.toArray());
			if (list == null || list.size() == 0) {
				return new HashMap<String, Object>();
			}
			return list.get(0);
		}

		return new HashMap<String, Object>();
	}

	@Override
	public ResultVo findAllAuditInfoPage(
			Map<String, Object> param) {
		StringBuilder sql= new StringBuilder()
		.append("select a.* from (")
		.append(" select btci.login_name \"loginName\", ")
		.append("        btai.id  \"id\", ")
		.append("        btci.cust_name \"custName\", ")
		.append("        btai.apply_type \"applyType\", ")
		.append("        btai.trade_amount \"tradeAmount\", ")
		.append("        btai.audit_status \"auditStatus\", ")
		.append("        btai.create_date \"createDate\" ")
		.append("   from bao_t_audit_info   btai, ")
		.append("        bao_t_account_info btacc, ")
		.append("        bao_t_cust_info    btci ")
		.append("  where btai.relate_primary = btacc.id ")
		.append("    and btacc.cust_id = btci.id ")
		.append("    and btai.audit_status = '"+ Constant.AUDIT_STATUS_UNREVIEW +"'")
		.append("    and btacc.id = '"+ Constant.ACCOUNT_ID_PROJECT_RISK +"' %s ")
		.append("    order by  btai.create_date) a ")
		.append("   union all ")
		.append("select b.* from (")
		.append(" select btci.login_name \"loginName\", ")
		.append("        btai.id  \"id\", ")
		.append("        btci.cust_name \"custName\", ")
		.append("        btai.apply_type \"applyType\", ")
		.append("        btai.trade_amount \"tradeAmount\", ")
		.append("        btai.audit_status \"auditStatus\", ")
		.append("        btai.create_date \"createDate\" ")
		.append("   from bao_t_audit_info   btai, ")
		.append("        bao_t_account_info btacc, ")
		.append("        bao_t_cust_info    btci ")
		.append("  where btai.relate_primary = btacc.id ")
		.append("    and btacc.cust_id = btci.id ")
		.append("    and btai.audit_status <> '"+ Constant.AUDIT_STATUS_UNREVIEW +"'")
		.append("    and btacc.id = '"+ Constant.ACCOUNT_ID_PROJECT_RISK +"' %s ")
		.append("    order by  btai.create_date desc) b ");
		
		StringBuilder summaSql = new StringBuilder()
		.append("  select nvl(sum(btai.trade_amount),0) \"totalTradeAmount\" ")
		.append("    from bao_t_audit_info   btai,  ")
		.append("         bao_t_account_info btacc,  ")
		.append("         bao_t_cust_info    btci  ")
		.append("   where btai.relate_primary = btacc.id  ")
		.append("     and btacc.cust_id = btci.id  ")
		.append("     and btacc.id = '"+ Constant.ACCOUNT_ID_PROJECT_RISK +"' %s ");
		
		StringBuilder sqlString = new StringBuilder();
		SqlCondition sqlCondition = new SqlCondition(sqlString, param);
		sqlCondition.addString("loginName", "btci.login_name")
					.addString("custName", "btci.cust_name")
					.addBeginDate("startDate", "btai.create_date")
					.addEndDate("endDate", "btai.create_date")
					.addString("auditStatus", "btai.audit_status");
		
		List<Object> objList = Lists.newArrayList();
		objList.addAll(sqlCondition.getObjectList());
		objList.addAll(sqlCondition.getObjectList());
		
		Page<Map<String, Object>> page = repositoryUtil.queryForPageMap(
				String.format(sql.toString(), sqlCondition.toString(), sqlCondition.toString()),
				objList.toArray(),
				Integer.parseInt(param.get("start").toString()),
				Integer.parseInt(param.get("length").toString()));
		
		List<Map<String, Object>> list = repositoryUtil.queryForMap(
				String.format(summaSql.toString(), sqlCondition.toString()), sqlCondition.toArray());
		
		BigDecimal totalTradeAmount = BigDecimal.ZERO;
		if (list != null && list.size() > 0) {
			totalTradeAmount = new BigDecimal(list.get(0).get("totalTradeAmount").toString());
		}
		
		Map<String, Object> result = Maps.newHashMap();
		result.put("totalTradeAmount", totalTradeAmount);
		result.put("iTotalDisplayRecords", page.getTotalElements());
		result.put("data", page.getContent());
		return new ResultVo(true, "企业借款风险金审核记录查询成功", result);
	}

	@Override
	public Page<Map<String, Object>> findAllAccountFlowPage(
			Map<String, Object> param) {
		StringBuilder sql = new StringBuilder()
		.append(" select btafi.id \"id\", ")
		.append("        btci.login_name \"loginName\", ")
		.append("        btci.cust_name \"custName\", ")
		.append("        btafi.trade_type \"tradeType\", ")
		.append("        btafi.trade_amount \"tradeAmount\", ")
		.append("        btafi.bankroll_flow_direction \"bankrollFlowDirection\", ")
		.append("        btafi.create_date \"createDate\", ")
		.append("        btafi.cust_id     \"custId\" ")
		.append("   from bao_t_account_flow_info btafi, ")
		.append("        bao_t_cust_info         btci ")
		.append("  where btafi.cust_id = btci.id ")
		.append("    and btafi.cust_id = '"+ Constant.CUST_ID_PROJECT_RISK +"' %s ")
		.append("  order by btafi.create_date desc ");
		
		StringBuilder sqlString = new StringBuilder();
		SqlCondition sqlCondition = new SqlCondition(sqlString, param);
		sqlCondition.addString("loginName", "btci.login_name")
					.addString("custName", "btci.cust_name")
					.addString("tradeType", "btafi.trade_type")
					.addBeginDate("startDate", "btafi.create_date")
					.addEndDate("endDate", "btafi.create_date")
					.addString("bankrollFlowDirection", "btafi.bankroll_flow_direction");
		
		return repositoryUtil.queryForPageMap(
				String.format(sql.toString(), sqlCondition.toString()),
				sqlCondition.toArray(),
				Integer.parseInt(param.get("start").toString()),
				Integer.parseInt(param.get("length").toString()));
	}

	@Override
	public BigDecimal findSumAccountFlow(Map<String, Object> param) {
		StringBuilder sql = new StringBuilder()
		.append(" select NVL(sum(btafi.trade_amount), 0) \"totalTradeAmount\" ")
		.append("   from bao_t_account_flow_info btafi, ")
		.append("        bao_t_cust_info         btci ")
		.append("  where btafi.cust_id = btci.id ")
		.append("    and btafi.cust_id = '"+ Constant.CUST_ID_PROJECT_RISK +"' %s ")
		.append("  order by btafi.create_date desc ");
		
		StringBuilder sqlString = new StringBuilder();
		SqlCondition sqlCondition = new SqlCondition(sqlString, param);
		sqlCondition.addString("loginName", "btci.login_name")
					.addString("custName", "btci.cust_name")
					.addString("tradeType", "btafi.trade_type")
					.addBeginDate("startDate", "btafi.create_date")
					.addEndDate("endDate", "btafi.create_date")
					.addString("bankrollFlowDirection", "btafi.bankroll_flow_direction");
		
		List<Map<String, Object>> list = repositoryUtil.queryForMap(
				String.format(sql.toString(), sqlCondition.toString()),
				sqlCondition.toArray());
		
		if (list == null || list.size() == 0) {
			return BigDecimal.ZERO;
		}
		
		return new BigDecimal(list.get(0).get("totalTradeAmount").toString());
	}

	@Override
	public List<Map<String, Object>> findSumTradeAmountNew(
			Map<String, Object> param) {

		StringBuilder sqlString = new StringBuilder()
		.append(" select NVL(SUM(TRUNC_AMOUNT_WEB(M.TradeAmount)), 0) \"tradeAmount\" , M.TradeDate \"tradeDate\" from( ")
		.append("   select NVL(T.TRADE_AMOUNT, 0) TradeAmount ,TO_CHAR(T.TRADE_DATE, 'YYYY-MM-DD') TradeDate ")
		.append("   from BAO_T_ACCOUNT_FLOW_INFO T   ")
		.append("   WHERE 1 = 1 %s ")
		.append("   union all ")
		.append("   select  NVL(sum(s.trade_amount), 0) TradeAmount, TO_CHAR(t.create_date, 'YYYY-MM-DD') TradeDate ")
		.append("   from bao_t_payment_record_info t, bao_t_payment_record_detail s ")
		.append("   where t.id = s.pay_record_id ")
		.append("   %s ")
		.append("   group by TO_CHAR(t.create_date, 'YYYY-MM-DD') ")
		.append(" ) M group by  M.TradeDate  order by M.TradeDate asc ");
		
		List<Object> objList = Lists.newArrayList();
		
		StringBuilder whereString1 = new StringBuilder();
		SqlCondition sqlCondition1 = new SqlCondition(whereString1, param, objList);
		sqlCondition1.addString("custId", "CUST_ID");
		sqlCondition1.addList("tradeType", "TRADE_TYPE");
		sqlCondition1.addBeginDate("tradeDateBegin", "trunc(T.TRADE_DATE)");
		sqlCondition1.addEndDate("tradeDateEnd", "trunc(T.TRADE_DATE)");
		
		StringBuilder whereString2 = new StringBuilder();
		SqlCondition sqlCondition2 = new SqlCondition(whereString2, param, objList);
		sqlCondition2.addString("custId", "CUST_ID");
		sqlCondition2.addListNotIn("subjectType", "SUBJECT_TYPE");
		sqlCondition2.addBeginDate("tradeDateBegin", "trunc(s.CREATE_DATE)");
		sqlCondition2.addEndDate("tradeDateEnd", "trunc(s.CREATE_DATE)");
		 
		return repositoryUtil.queryForMap(
				String.format(sqlString.toString(), sqlCondition1.toString(), sqlCondition2.toString()), sqlCondition2.toArray());
	}
	
	@Override
	public ResultVo queryMyWealthFlow(Map<String, Object> params)throws SLException {
		Map<String, Object> result = Maps.newHashMap();
		StringBuffer SqlString = new StringBuffer()
		.append("  select af.create_date \"createDate\", af.trade_type \"tradeType\", af.trade_amount \"tradeAmount\", ")
		.append("        af.account_total_amount \"accountTotalAmount\", af.bankroll_flow_direction \"bankrollFlowDirection\", ")
		.append("        af.memo \"memo\" ")
		.append("    from bao_t_wealth_info t  ")
		.append("  inner join bao_t_invest_info i on t.id = i.wealth_id ")
		.append("  inner join bao_t_sub_account_info sa on sa.relate_primary = i.id ")
		.append("  inner join bao_t_account_flow_info af on af.account_id = sa.id ")
		.append("  where af.trade_amount >= 0.01 and t.id = ? and i.cust_id = ? and af.account_type = ?")
		.append("  order by af.trade_no desc ");
		
		Page<Map<String, Object>> pageVo = repositoryUtil.queryForPageMap(SqlString.toString(), new Object[] {(String) params.get("wealthId"), (String) params.get("custId"), (String) params.get("accountType")}, 
				Integer.parseInt(params.get("start").toString()), Integer.parseInt(params.get("length").toString()));
		result.put("iTotalDisplayRecords", pageVo.getTotalElements());
		result.put("data", pageVo.getContent());
		return new ResultVo(true, "查询成功", result);
	}
}
