/** 
 * @(#)TradeFlowInfoRepository.java 1.0.0 2015年4月21日 下午2:04:46  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository.custom.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.TradeFlowInfoEntity;
import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.TradeFlowInfoRepositoryCustom;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.SqlCondition;
import com.slfinance.shanlincaifu.utils.SubjectConstant;

/**   
 * 
 *  
 * @author  HuangXiaodong
 * @version $Revision:1.0.0, $Date: 2015年4月21日 下午2:04:46 $ 
 */
@Repository
public class TradeFlowInfoRepositoryImpl implements TradeFlowInfoRepositoryCustom{
	
	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private RepositoryUtil repositoryUtil;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	/**根据用户id和交易类型汇总所有交易金额*/
	@SuppressWarnings("unchecked")
	@Override
	public List<TradeFlowInfoEntity> findTrade(final String custId, final Date starDate, final Date endDate, final String tradeType){
		
		StringBuffer sql= new StringBuffer("select tr  from TradeFlowInfoEntity tr where 1=1");
		List<Object> objList=new ArrayList<Object>();
		//用户id
		if(!StringUtils.isEmpty(custId)){
			sql.append(" and custId=?");
			objList.add(custId);
		}
		//开始时间
		if(!StringUtils.isEmpty(starDate)){
			sql.append(" and tradeDate>=?");
			objList.add(starDate);
		}
		//结束时间
		if(!StringUtils.isEmpty(endDate)){
			sql.append(" and tradeDate<=?");
			objList.add(endDate);
		}
		//结束时间
		if(!StringUtils.isEmpty(tradeType)){
			sql.append(" and tradeType=?");
			objList.add(endDate);
		}
//		repositoryUtil.queryForList(sql.toString(), objList.toArray(), TradeFlowInfoEntity.class);
		Query query=em.createQuery(sql.toString());
		for (int i = 0; i < objList.size(); i++) {
			query.setParameter(i+1, objList.get(i));
		}
		return query.getResultList();
	}

	/**
	 * 充值管理--统计
	 *
	 * @author  wangjf
	 * @date    2015年4月25日 上午11:35:54
	 * @param map
	 		<tt>nickName：String:用户昵称</tt><br>
	  		<tt>custName：String:真实姓名</tt><br>
	  		<tt>credentialsCode：String:证件号码</tt><br>
	  		<tt>opearteDateBegin：String:操作开始时间</tt><br>
	  		<tt>opearteDateEnd：String:操作开始时间</tt><br>
	  		<tt>tradeType：String:交易类型</tt><br>
	  		<tt>tradeStatus：String:交易状态</tt><br>
	  		<tt>tradeNo：String:交易编号</tt><br>
	 * @return
	  		<tt>totalTradeAmount： BigDecimal:充值金额汇总</tt><br>
	  		<tt>totalTradeExpenses： BigDecimal:充值手续费汇总</tt><br>
	  		<tt>totalFactAmount： BigDecimal:实际到账金额汇总</tt><br>
	 */
	@Override
	public Map<String, Object> findAllRechargeSum(Map<String, Object> param) {
		StringBuffer whereCustSqlString= new StringBuffer();
		StringBuffer whereAccountSqlString= new StringBuffer();
		
		List<Object> objList=new ArrayList<Object>();
		if(!StringUtils.isEmpty(param.get("nickName"))){
			whereCustSqlString.append(" and LOGIN_NAME = ?");
			objList.add(param.get("nickName"));
		}
	
		if(!StringUtils.isEmpty(param.get("custName"))){
			whereCustSqlString.append(" and CUST_NAME = ?");
			objList.add(param.get("custName"));
		}
		
		if(!StringUtils.isEmpty(param.get("credentialsCode"))){
			whereCustSqlString.append(" and CREDENTIALS_CODE = ?");
			objList.add(param.get("credentialsCode"));
		}
		
		if(!StringUtils.isEmpty(param.get("opearteDateBegin"))){
			whereAccountSqlString.append(" and T.CREATE_DATE >= ?");
			objList.add(DateUtils.parseStandardDate((String)param.get("opearteDateBegin")));
		}

		if(!StringUtils.isEmpty(param.get("opearteDateEnd"))){
			whereAccountSqlString.append(" and T.CREATE_DATE <= ?");
			objList.add(DateUtils.getEndDate(DateUtils.parseStandardDate((String)param.get("opearteDateEnd"))));
		}
		
		if(!StringUtils.isEmpty(param.get("tradeType"))){
			whereAccountSqlString.append(" and T.TRADE_TYPE = ?");
			objList.add(param.get("tradeType"));
		}
		
		if(!StringUtils.isEmpty(param.get("tradeStatus"))){
			whereAccountSqlString.append(" and T.TRADE_STATUS = ?");
			objList.add(param.get("tradeStatus"));
		}
		
		if(!StringUtils.isEmpty(param.get("tradeNo"))){
			whereAccountSqlString.append(" and T.TRADE_NO = ?");
			objList.add(param.get("tradeNo"));
		}
		
		StringBuffer sqlString= new StringBuffer()
		.append(" select NVL(sum(TRUNC_AMOUNT_WEB(t.Trade_Amount)), 0) \"totalTradeAmount\",  ")
		.append("          NVL(sum(TRUNC_AMOUNT_WEB(t.Trade_Expenses)), 0) \"totalTradeExpenses\", ")
		.append("          NVL(sum(TRUNC_AMOUNT_WEB(t.Trade_Amount) - TRUNC_AMOUNT_WEB(t.trade_expenses)), 0) \"totalFactAmount\" ")
		.append("   from BAO_T_TRADE_FLOW_INFO T ")
		.append("   where T.TRADE_TYPE = '充值' %s ");
		
		StringBuffer whereSqlString = new StringBuffer();
		if(!StringUtils.isEmpty(whereCustSqlString.toString()))
		{
			whereSqlString.append(String.format(" and CUST_ID IN (select id from BAO_T_CUST_INFO where 1=1 %s)", whereCustSqlString.toString()));
		}
		
		if(!StringUtils.isEmpty(whereAccountSqlString.toString()))
		{
			whereSqlString.append(String.format(" %s", whereAccountSqlString.toString()));
		}
		
		List<Map<String, Object>> list = repositoryUtil.queryForMap(String.format(sqlString.toString(), whereSqlString.toString()), objList.toArray());
		return list.get(0);
	}

	/**
	 * 充值管理--列表
	 *
	 * @author  wangjf
	 * @date    2015年4月25日 上午11:33:55
	 * @param map
	        <tt>start：int:分页起始页</tt><br>
	  		<tt>length：int:每页长度</tt><br>
	 		<tt>nickName：String:用户昵称</tt><br>
	  		<tt>custName：String:真实姓名</tt><br>
	  		<tt>credentialsCode：String:证件号码</tt><br>
	  		<tt>opearteDateBegin：String:操作开始时间</tt><br>
	  		<tt>opearteDateEnd：String:操作开始时间</tt><br>
	  		<tt>tradeType：String:交易类型</tt><br>
	  		<tt>tradeStatus：String:交易状态</tt><br>
	  		<tt>tradeNo：String:交易编号</tt><br>
	 * @return
	 * 		<tt>custId：String:客户ID</tt><br>
	        <tt>flowId：String:交易过程流水ID</tt><br>
	  		<tt>nickName：String:用户昵称</tt><br>
	  		<tt>tradeNo：String:交易编号</tt><br>
	 		<tt>tradeType：String:交易类型</tt><br>
	 		<tt>bankName：String:充值银行</tt><br>
	 		<tt>tradeAmount：BigDecimal:充值金额</tt><br>
	  		<tt>tradeExpenses：BigDecimal:手续额</tt><br>
	  		<tt>factAmount：BigDecimal:实际到账金额</tt><br>
	 		<tt>tradeStatus：String:交易状态</tt><br>
	  		<tt>operateDate：Date:操作时间</tt><br>
	  		<tt>ipAddress：String:IP地址</tt><br>
	 */
	@Override
	public Page<Map<String, Object>> findAllRechargeList(Map<String, Object> param) {
		StringBuffer whereCustSqlString= new StringBuffer();
		StringBuffer whereAccountSqlString= new StringBuffer();
		
		StringBuffer sqlString= new StringBuffer()
		.append(" select T.ID \"flowId\", S.ID \"custId\", S.LOGIN_NAME \"nickName\", T.TRADE_NO \"tradeNo\", T.TRADE_TYPE \"tradeType\",  ")
		.append("          T.BANK_NAME \"bankName\", NVL(TRUNC_AMOUNT_WEB(T.TRADE_AMOUNT), 0) \"tradeAmount\", NVL(TRUNC_AMOUNT_WEB(T.Trade_Expenses), 0) \"tradeExpenses\",  ")
		.append("          NVL(TRUNC_AMOUNT_WEB(T.TRADE_AMOUNT) - TRUNC_AMOUNT_WEB(T.TRADE_EXPENSES), 0) \"factAmount\", T.Trade_Status \"tradeStatus\", T.Create_Date \"operateDate\", ")
		.append("   OPER_IPADDRESS \"ipAddress\"")
		.append("   from BAO_T_TRADE_FLOW_INFO T, BAO_T_CUST_INFO S, BAO_T_LOG_INFO M ")
		.append("   where T.TRADE_TYPE = ? AND t.cust_id = s.id AND M.RELATE_PRIMARY = T.ID %s ")
		.append("   ORDER BY T.Create_Date DESC ");
		
		List<Object> objList=new ArrayList<Object>();
		objList.add(SubjectConstant.TRADE_FLOW_TYPE_RECHARGE);
		if(!StringUtils.isEmpty(param.get("nickName"))){
			whereCustSqlString.append(" and LOGIN_NAME LIKE ?");
			objList.add(new StringBuffer().append("%").append(param.get("nickName")).append("%"));
		}
	
		if(!StringUtils.isEmpty(param.get("custName"))){
			whereCustSqlString.append(" and CUST_NAME = ?");
			objList.add(param.get("custName"));
		}
		
		if(!StringUtils.isEmpty(param.get("credentialsCode"))){
			whereCustSqlString.append(" and CREDENTIALS_CODE = ?");
			objList.add(param.get("credentialsCode"));
		}
		
		if(!StringUtils.isEmpty(param.get("opearteDateBegin"))){
			whereAccountSqlString.append(" and T.Create_Date >= ?");
			objList.add(DateUtils.parseStandardDate((String)param.get("opearteDateBegin")));
		}

		if(!StringUtils.isEmpty(param.get("opearteDateEnd"))){
			whereAccountSqlString.append(" and T.Create_Date <= ?");
			objList.add(DateUtils.getEndDate(DateUtils.parseStandardDate((String)param.get("opearteDateEnd"))));
		}
		
		if(!StringUtils.isEmpty(param.get("tradeType"))){
			whereAccountSqlString.append(" and T.TRADE_TYPE = ?");
			objList.add(param.get("tradeType"));
		}
		
		if(!StringUtils.isEmpty(param.get("tradeStatus"))){
			whereAccountSqlString.append(" and T.TRADE_STATUS = ?");
			objList.add(param.get("tradeStatus"));
		}
		
		if(!StringUtils.isEmpty(param.get("tradeNo"))){
			whereAccountSqlString.append(" and T.TRADE_NO = ?");
			objList.add(param.get("tradeNo"));
		}
		
		StringBuffer whereSqlString = new StringBuffer();
		if(!StringUtils.isEmpty(whereCustSqlString.toString()))
		{
			whereSqlString.append(String.format(" %s", whereCustSqlString.toString()));
		}
		
		if(!StringUtils.isEmpty(whereAccountSqlString.toString()))
		{
			whereSqlString.append(String.format(" %s", whereAccountSqlString.toString()));
		}

		return repositoryUtil.queryForPageMap(String.format(sqlString.toString(), whereSqlString.toString()), objList.toArray(), (int)param.get("start"), (int)param.get("length"));
	}

	/**
	 * 充值管理--明细
	 *
	 * @author  wangjf
	 * @date    2015年4月25日 上午11:33:55
	 * @param flowId
	 * @return
	        <tt>id：String:用户ID</tt><br>
	  		<tt>nickName：String:用户昵称</tt><br>
	  		<tt>custName：String:真实姓名</tt><br>
	  		<tt>credentialsCode：String:证件号码</tt><br>
	  		<tt>tradeNo：String:交易编号</tt><br>
	 		<tt>tradeType：String:交易类型</tt><br>
	 		<tt>bankName：String:充值银行</tt><br>
	 		<tt>branchBankName：String:支行名称</tt><br>
	 		<tt>bankCardNo：String:银行卡号</tt><br>
	 		<tt>tradeAmount：BigDecimal:充值金额</tt><br>
	  		<tt>tradeExpenses：BigDecimal:手续额</tt><br>
	  		<tt>factAmount：BigDecimal:实际到账金额</tt><br>
	  		<tt>memo：String:备注</tt><br>
	 		<tt>tradeStatus：String:交易状态</tt><br>
	  		<tt>operateDate：Date:操作时间</tt><br>	
	  		<tt>ipAddress：String:IP地址</tt><br>	
	 */
	@Override
	public Map<String, Object> findRechargeDetailInfo(String flowId) {
		StringBuffer sqlString= new StringBuffer()
		.append(" select T.ID \"flowId\", S.LOGIN_NAME \"nickName\",  ")
		.append("          S.CUST_NAME \"custName\", S.CREDENTIALS_CODE \"credentialsCode\", ")
		.append("          T.TRADE_NO \"tradeNo\", T.TRADE_TYPE \"tradeType\",  ")
		.append("          T.BANK_NAME \"bankName\", T.BRANCH_BANK_NAME \"branchBankName\", T.BANK_CARD_NO \"bankCardNo\", ")
		.append("          NVL(TRUNC_AMOUNT_WEB(T.TRADE_AMOUNT), 0) \"tradeAmount\", NVL(TRUNC_AMOUNT_WEB(T.Trade_Expenses), 0) \"tradeExpenses\",  ")
		.append("          NVL(TRUNC_AMOUNT_WEB(T.TRADE_AMOUNT) - TRUNC_AMOUNT_WEB(T.TRADE_EXPENSES), 0) \"factAmount\", T.Memo \"memo\", ")
		.append("          T.Trade_Status \"tradeStatus\", T.Create_Date \"operateDate\", OPER_IPADDRESS \"ipAddress\" ")
		.append("   from BAO_T_TRADE_FLOW_INFO T, BAO_T_CUST_INFO S,  BAO_T_LOG_INFO M ")
		.append("   where T.TRADE_TYPE = '充值' AND t.cust_id = s.id AND M.RELATE_PRIMARY = T.ID ")
		.append("   and T.id = ? ");

		List<Map<String, Object>> list = repositoryUtil.queryForMap(sqlString.toString(), new Object[]{flowId});
		return list.get(0);
	}

	@Override
	public List<Map<String, Object>> countRecharge(String custId) {
		StringBuffer sqlString= new StringBuffer()
		.append(" SELECT A.SUB_TRADE_TYPE \"subTradeType\", NVL(ALLREQUEST, 0) \"allRequest\", NVL(SUCCESSREQUEST, 0) \"successRequest\", NVL(SUMDAILYTRADEAMOUNT, 0) \"sumDailyTradeAmount\", NVL(SUMMONTHLYTRADEAMOUNT, 0) \"sumMonthlyTradeAmount\" ")
		.append("      FROM( ")
		.append("      SELECT T.SUB_TRADE_TYPE, COUNT(1) ALLREQUEST FROM BAO_T_TRADE_FLOW_INFO T ") // 所有充值次数
		.append("      WHERE T.TRADE_TYPE = '充值' ")
		.append("      AND T.CUST_ID = ? ")
		.append("      AND T.TRADE_DATE BETWEEN ? AND ? ")
		.append("      GROUP BY T.SUB_TRADE_TYPE) A  ")
		.append("      LEFT JOIN  ")
		.append("      (SELECT T.SUB_TRADE_TYPE, COUNT(1) SUCCESSREQUEST FROM BAO_T_TRADE_FLOW_INFO T ") // 所有成功充值次数
		.append("      WHERE T.TRADE_TYPE = '充值' AND T.TRADE_STATUS IN ( '处理成功') ")
		.append("      AND T.CUST_ID = ? ")
		.append("      AND T.TRADE_DATE BETWEEN ? AND ? ")
		.append("      GROUP BY T.SUB_TRADE_TYPE) B ON B.SUB_TRADE_TYPE = A.SUB_TRADE_TYPE ")
		.append("      LEFT JOIN  ")
		.append("      (SELECT T.SUB_TRADE_TYPE, SUM(T.TRADE_AMOUNT) SUMDAILYTRADEAMOUNT FROM BAO_T_TRADE_FLOW_INFO T ") // 单日成功充值金额
		.append("      WHERE T.TRADE_TYPE = '充值' AND T.TRADE_STATUS IN ( '处理成功') ")
		.append("      AND T.CUST_ID = ? ")
		.append("      AND T.TRADE_DATE BETWEEN ? AND ? ")
		.append("      GROUP BY T.SUB_TRADE_TYPE) C ON C.SUB_TRADE_TYPE = A.SUB_TRADE_TYPE ")
		.append("      LEFT JOIN ")
		.append("      (SELECT T.SUB_TRADE_TYPE, SUM(T.TRADE_AMOUNT) SUMMONTHLYTRADEAMOUNT FROM BAO_T_TRADE_FLOW_INFO T ") // 单月成功充值金额
		.append("      WHERE T.TRADE_TYPE = '充值' AND T.TRADE_STATUS IN ( '处理成功') ")
		.append("      AND T.CUST_ID = ? ")
		.append("      AND T.TRADE_DATE BETWEEN ? AND ? ")
		.append("      GROUP BY T.SUB_TRADE_TYPE) D ON D.SUB_TRADE_TYPE = A.SUB_TRADE_TYPE ");
		
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sqlString.toString(), 
				new Object[]{	custId, 
								DateUtils.getStartDate(new Date()), 
								DateUtils.getEndDate(new Date()),
								custId, 
								DateUtils.getStartDate(new Date()), 
								DateUtils.getEndDate(new Date()),
								custId, 
								DateUtils.getStartDate(new Date()), 
								DateUtils.getEndDate(new Date()),
								custId,
								DateUtils.getMonthStartDate(new Date()),
								DateUtils.getEndDate(new Date())
							});
		return list;
	}

	@Override
	public List<Map<String, Object>> findTradeFlowBusinessHistory() {
		List<Object> listObject = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer()
		.append("select max(bttfi.trade_type) \"tradeType\", trunc(bttfi.create_date) \"createDate\",btci.cust_source \"tradeSource\",")
		.append("sum(case when bttfi.trade_type='充值' then 1 else 0 end) \"rechargeCount\",")
		.append("sum(case when bttfi.trade_type='充值' and bttfi.trade_status = '处理成功' then 1 else 0 end) \"rechargeSuccCount\",")
		.append("sum(case when bttfi.trade_type='充值' and bttfi.trade_status = '处理成功' then bttfi.trade_amount else 0 end) \"rechargAmount\",")
		.append("sum(case when  bttfi.trade_type='提现' and bttfi.trade_status = '处理成功' then 1 else 0 end) \"withdrawCount\",")
		.append("sum(case when  bttfi.trade_type='提现' and bttfi.trade_status = '处理成功' then bttfi.trade_amount else 0 end) \"withdrawAmount\"")
		.append("from  bao_t_trade_flow_info bttfi, bao_t_cust_info btci ")
		.append("where bttfi.cust_id = btci.id ")
		.append("and bttfi.trade_type in('充值', '提现') ")
		.append("and btci.login_name not in ('居间人账户','公司收益账户','系统管理员','公司客服账户','风险金账户') ")
		.append("and (btci.is_recommend != '是' or btci.is_recommend is null) ")
		.append("and btci.invite_origin_id not in (select id from bao_t_cust_info btcii where btcii.is_recommend = '是' ) ")
		.append("group by trunc(bttfi.create_date),btci.cust_source order by trunc(bttfi.create_date)");
		
		return repositoryUtil.queryForMap(sql.toString(), listObject.toArray());
	}

	@Override
	public List<Map<String, Object>> findRealNameHistory() {
		List<Object> listObject = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer()
		.append("select count(a.relate_primary) \"certiCount\",trunc(a.create_date) \"createDate\",btci.cust_source \"realNameSource\" from ")
		.append(" ( select btli.relate_primary,max(btli.create_date) as create_date from   bao_t_log_info btli, bao_t_cust_info btci")
		.append(" where btli.relate_type = 'BAO_T_CUST_INFO'  and btli.relate_primary = btci.id and btli.log_type = '实名认证'")
		.append(" and btci.cust_name is not null and btci.credentials_code is not null group by btli.relate_primary) a,")
		.append(" bao_t_cust_info btci ")
		.append(" where a.relate_primary = btci.id ")
		.append(" and btci.login_name not in ('居间人账户','公司收益账户','系统管理员','公司客服账户','风险金账户')")
		.append(" and (btci.is_recommend != '是' or btci.is_recommend is null) ")
		.append(" and btci.invite_origin_id not in (select id from bao_t_cust_info btcii where btcii.is_recommend = '是' ) ")
		.append(" group by btci.cust_source, trunc(a.create_date) order by trunc(a.create_date)");
		
		return repositoryUtil.queryForMap(sql.toString(), listObject.toArray());
	}

	@Override
	public Page<Map<String, Object>> findWealthTradeFlowList(
			Map<String, Object> param) {
		StringBuilder sql = new StringBuilder()
		.append(" select bttfi.id              \"tradeFlowId\", ")
		.append("        btci.cust_name        \"custName\",  ")
		.append("        btci.credentials_code \"credentialsCode\", ")
		.append("        btci.mobile           \"mobile\", ")
		.append("        bttfi.trade_type      \"tradeType\", ")
		.append("        bttfi.trade_amount    \"tradeAmount\", ")
		.append("        nvl(btci2.cust_name, ctu.user_name)         \"createUser\", ")
		.append("        bttfi.create_date     \"createDate\", ")
		.append("        bttfi.trade_date      \"tradeDate\", ")
		.append("        btai.audit_status     \"auditStatus\", ")
		.append("        bttfi.trade_status    \"tradeStatus\", ")
		.append("        bttfi.third_pay       \"thirdPay\" ") // @2016/4/7 add by liyy
		.append("   from bao_t_trade_flow_info bttfi, ")
		.append("        bao_t_cust_info       btci, ")
		.append("        bao_t_cust_info       btci2, ")
		.append("        com_t_user            ctu, ")
		.append("        bao_t_audit_info      btai ")
		.append("  where bttfi.cust_id = btci.id ")
		.append("    and bttfi.create_user = btci2.id(+)")
		.append("    and bttfi.create_user = ctu.id(+) ");
		
		SqlCondition sqlCondition = new SqlCondition(sql, param);
		sqlCondition.addString("tradeType", "bttfi.trade_type")
					.addString("custName", "btci.cust_name")
					.addString("credentialsCode", "btci.credentials_code")
					.addString("mobile", "btci.mobile")
					.addString("auditStatus", "btai.audit_status")
					.addBeginDate("beginOperateDate", "bttfi.create_date")
					.addEndDate("endOperateDate", "bttfi.create_date")
					.addString("tradeStatus", "bttfi.trade_status")
					.addList("thirdPay", "bttfi.third_pay") // @2016/4/7 add by liyy
					;
		
		if(!StringUtils.isEmpty(param.get("custManagerId"))){
			String custManagerId = (String) param.get("custManagerId");
			sqlCondition.addSql("and btci.id in (select btcri.quilt_cust_id from bao_t_cust_recommend_info btcri where btcri.record_status = '有效' and btcri.cust_id = '" + custManagerId + "')");
		}
		
		String tradeType = (String) param.get("tradeType");
		if(SubjectConstant.TRADE_FLOW_TYPE_OFFLINE_RECHARGE.equals(tradeType)){
			sqlCondition.addSql("and bttfi.id = btai.relate_primary ")
						// @2016/4/12 add by liyy
						// .addSql("order by decode(btai.audit_status, '待审核', 1, '初审通过', 2, '初审回退', 3, '终审通过', 4, '终审回退', 5, '初审拒绝', 6, '终审拒绝', 7), bttfi.trade_date desc");
						   .addSql("order by decode(btai.audit_status, '待审核', 1, '初审回退', 2, '复审回退', 3, '终审回退', 4, '初审通过', 5, '复审通过', 6, '终审通过', 7, '初审拒绝', 8, '复审拒绝', 9, '终审拒绝', 10), bttfi.trade_date desc");
		} else if (SubjectConstant.TRADE_FLOW_TYPE_OFFLINE_WITHDRAW.equals(tradeType)){
			sqlCondition.addSql("and btai.id = bttfi.relate_primary ")
						.addSql("order by decode(bttfi.trade_status, '未处理', 1, '处理中', 2, '提现成功', 3, '提现失败', 4), bttfi.create_date desc");
		}
		
		return repositoryUtil.queryForPageMap(
				sqlCondition.toString(), 
				sqlCondition.toArray(), 
				Integer.parseInt(param.get("start").toString()), 
				Integer.parseInt(param.get("length").toString()));
	}

	@Override
	public Map<String, Object> queryWealthTradeFlowDetailById(
			Map<String, Object> param) {
		Map<String, Object> resultMap = Maps.newHashMap();
		String tradeFlowId = (String) param.get("tradeFlowId");
		String tradeType = (String) param.get("tradeType");
		
		StringBuilder sql = new StringBuilder()		
		.append("      select btf.id                \"tradeFlowId\",  ")
		.append("             bcst.cust_name        \"custName\",  ")
		.append("             bcst.credentials_code \"credentialsCode\",  ")
		.append("             bcst.mobile           \"mobile\",  ")
		.append("             bcst.create_date      \"registerDate\",  ")
		.append("             btf.bank_name         \"bankName\",  ")
		.append("             btf.bank_card_no      \"bankCardNo\",  ")
		.append("             btf.branch_bank_name  \"branchBankName\",  ")
		.append("             ctp1.parameter_name   \"openProvince\",  ")
		.append("             ctp2.parameter_name   \"openCity\",  ")
		.append("             btf.trade_amount      \"tradeAmount\",  ")
		.append("             btf.third_pay         \"thirdPay\",  ")
		.append("             showT.VALUE           \"showType\",  ")
		.append("             btf.trade_date        \"createDate\",  ")
		.append("             btf.cust_id           \"custId\",  ")
		.append("             bbank.id              \"bankId\",  ")
		.append("             btf.trade_status      \"tradeStatus\",  ")
		.append("             decode(baud.audit_status, '初审回退', '审核回退', '终审回退', '审核回退', baud.audit_status)     \"auditStatus\"  ")
		.append("        from bao_t_trade_flow_info btf ")
		.append("        inner join bao_t_cust_info bcst on  btf.cust_id = bcst.id  ")
		.append("        inner join bao_t_bank_card_info bbank on btf.cust_id = bbank.cust_id and btf.bank_card_no = bbank.card_no and bbank.bank_flag = '线下'  ")
		.append("        left  join com_t_param ctp1 on btf.open_province = ctp1.value and ctp1.type = 'province' ")
		.append("        left  join com_t_param ctp2 on btf.open_city = ctp2.value     and ctp2.type = 'city' ")
		.append("        left  join com_t_param showT on btf.third_pay = showT.parameter_name AND showT.\"TYPE\"= 'thirdPayShow'   ")
		.append(Constant.OPERATION_TYPE_42.equals(tradeType) ? " left join bao_t_audit_info baud on btf.id = baud.relate_primary " : " left  join bao_t_audit_info baud on baud.id = btf.relate_primary ")
		.append("      where btf.id = ? ");
				
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sql.toString(), new Object[]{tradeFlowId});
		
		if(list != null && list.size() > 0){
			resultMap = list.get(0);
		}
		
		return resultMap;
	}

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public Page<Map<String, Object>> queryExportWealthWithDraw(
			Map<String, Object> param) {
		String payeeBankNo = (String) param.get("payerBankNo");
		String payerCustName = (String) param.get("payerCustName");
		String userId = (String) param.get("userId");
		
		//查询条件
		StringBuilder conditonSql = new StringBuilder();
		SqlCondition commonConditon = new SqlCondition(conditonSql, param);
		commonConditon.addString("tradeType", "btf.trade_type")
					  .addString("custName", "btc.cust_name")
					  .addString("credentialsCode", "btc.credentials_code")
					  .addString("mobile", "btc.mobile")
					  .addBeginDate("beginOperateDate", "btf.create_date")
					  .addEndDate("endOperateDate", "btf.create_date")
					  .addString("tradeStatus", "btf.trade_status");
		
		//查询
		StringBuilder sql = new StringBuilder()
		.append(" select btf.trade_amount \"tradeAmount\", ")
		.append("        '" + payeeBankNo + "' \"payerBankNo\", ")
		.append("        '" + payerCustName + "' \"payerCustName\", ")
		.append("        btf.bank_card_no \"payeeBankNo\", ")
		.append("        btc.cust_name \"payeeCustName\", ")
		.append("        btf.bank_name || btf.branch_bank_name \"payeeBankName\", ")
		.append("        ctp1.parameter_name \"payeeProvince\", ")
		.append("        ctp2.parameter_name \"payeeCity\", ")
		.append("        '' \"transferType\", ")
		.append("        '' \"transferDescr\", ")
		.append("        btf.id \"tradeflowId\" ")
		.append("   from bao_t_trade_flow_info btf, bao_t_cust_info btc, com_t_param ctp1, com_t_param ctp2 ")
		.append("  where btf.cust_id = btc.id ")
		.append("        and btf.open_province = ctp1.value(+) and ctp1.type = 'province' ")
		.append("        and btf.open_city = ctp2.value (+) and ctp2.type = 'city' ")
		.append(" %s order by btf.create_date desc");
		
		Page<Map<String, Object>> page = repositoryUtil.queryForPageMap(
				String.format(sql.toString(), commonConditon.toString()), 
				commonConditon.toArray(), 
				Integer.parseInt(param.get("start").toString()), 
				Integer.parseInt(param.get("length").toString()));
			
		//修改审核信息
		
		List<Object> updateAuditList = Lists.newArrayList();
		updateAuditList.add(Constant.AUDIT_STATUS_REVIEWD);
		updateAuditList.add(Constant.OFFLINE_WITHDRAW_STATUS_PROCESSING);
		updateAuditList.add(userId);
		updateAuditList.add(new Timestamp(new Date().getTime()));
		updateAuditList.add(new Timestamp(new Date().getTime()));
		updateAuditList.addAll(commonConditon.getObjectList());
		StringBuilder auditUpdateSql = new StringBuilder()
		.append("  update bao_t_audit_info baudit ")
		.append("     set baudit.audit_status     = ?, ")
		.append("         baudit.trade_status     = ?, ")
		.append("         baudit.last_update_user = ?, ")
		.append("         baudit.last_update_date = ?, ")
		.append("         baudit.audit_time       = ? ")
		.append("   where baudit.id in ( ")
		.append("     select btf.relate_primary from bao_t_trade_flow_info btf, bao_t_cust_info btc ")
		.append("    where btf.cust_id = btc.id %s")
		.append("  ) ");
		
		jdbcTemplate.update(String.format(auditUpdateSql.toString(), commonConditon.toString()), updateAuditList.toArray());
		
		//保存日志信息
		StringBuilder insertLogSql = new StringBuilder()
		.append(" insert into bao_t_log_info ")
		.append("   (id, ")
		.append("    relate_type, relate_primary,  log_type, ")
		.append("    oper_person,  oper_before_content, ")
		.append("    oper_after_content, oper_desc, ")
		.append("    create_user,   create_date, ")
		.append("    last_update_user,  last_update_date, version, record_status) ")
		.append(" select ")
		.append("   sys_guid(), ?, btf.id, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0, '有效' ")
		.append("    from bao_t_trade_flow_info btf, bao_t_cust_info btc ")
		.append("   where btf.cust_id = btc.id %s");
		
		List<Object> insertLogList = Lists.newArrayList();
		insertLogList.add(Constant.TABLE_BAO_T_TRADE_FLOW_INFO);
		insertLogList.add(Constant.LOG_TYPE_OFFLINE_WITHDRAW_AUDIT);
		insertLogList.add(userId);
		insertLogList.add(Constant.AUDIT_STATUS_REVIEWD);
		insertLogList.add(Constant.AUDIT_STATUS_REVIEWD);
		insertLogList.add("线下提现审核中");
		insertLogList.add(userId);
		insertLogList.add(new Timestamp(new Date().getTime()));
		insertLogList.add(userId);
		insertLogList.add(new Timestamp(new Date().getTime()));
		insertLogList.addAll(commonConditon.getObjectList());
		
		jdbcTemplate.update(String.format(insertLogSql.toString(), commonConditon.toString()), insertLogList.toArray());
		
		//修改交易流水信息
		StringBuilder updateFlowSql = new StringBuilder()
		.append(" update bao_t_trade_flow_info a  ")
		.append("    set a.trade_status     = ?,  ")
		.append("        a.last_update_user = ?,  ")
		.append("        a.last_update_date = ?,  ")
		.append("        a.trade_date       = ?  ")
		.append("  where a.id in (  ")
		.append("   select btf.id from bao_t_trade_flow_info btf, bao_t_cust_info btc ")
		.append("   where btf.cust_id = btc.id %s")
		.append("  ) ");
		
		List<Object> updateFlowList = Lists.newArrayList();
		updateFlowList.add(Constant.OFFLINE_WITHDRAW_STATUS_PROCESSING);
		updateFlowList.add(userId);
		updateFlowList.add(new Timestamp(new Date().getTime()));
		updateFlowList.add(new Timestamp(new Date().getTime()));
		updateFlowList.addAll(commonConditon.getObjectList());
		jdbcTemplate.update(String.format(updateFlowSql.toString(), commonConditon.toString()), updateFlowList.toArray());
		
		return page;

	}
	
	/**
	 * 线下充值列表（客户自己的数据）
	 * 
	 * @author liyy
	 * @date   2016年6月1日
	 * @param param
     *      <tt>start           :String:起始值</tt><br>
     *      <tt>length          :String:长度</tt><br>
     *      <tt>custName        :String:用户名（可选）</tt><br>
     *      <tt>credentialsCode :String:证件号码(可选)</tt><br>
     *      <tt>mobile          :String:手机号（可选）</tt><br>
     *      <tt>auditStatus     :String:审核状态（可选）</tt><br>
     *      <tt>beginOperateDate:String:开始创建时间（可选）</tt><br>
     *      <tt>endOperateDate  :String:结束创建时间（可选）</tt><br>
     *      <tt>custId          :String:客户ID</tt><br>
	 * @return
	 *      <tt>tradeFlowId    :String:交易过程ID</tt><br>
     *      <tt>custName       :String:用户名</tt><br>
     *      <tt>credentialsCode:String:证件号码</tt><br>
     *      <tt>mobile         :String:手机号</tt><br>
     *      <tt>tradeType      :String:交易类型</tt><br>
     *      <tt>tradeAmount    :String:交易金额</tt><br>
     *      <tt>createUser     :String:创建人</tt><br>
     *      <tt>createDate     :String:创建时间</tt><br>
     *      <tt>auditStatus    :String:审核状态</tt><br>
	 */
	public Page<Map<String, Object>> queryWealthRechargeListByCustId(
			Map<String, Object> param) {
		StringBuilder sql = new StringBuilder()
		.append(" select bttfi.id              \"tradeFlowId\", ")
		.append("        btci.cust_name        \"custName\",  ")
		.append("        btci.credentials_code \"credentialsCode\", ")
		.append("        btci.mobile           \"mobile\", ")
		.append("        bttfi.trade_type      \"tradeType\", ")
		.append("        bttfi.trade_amount    \"tradeAmount\", ")
		.append("        nvl(btci2.cust_name, ctu.user_name)         \"createUser\", ")
		.append("        bttfi.create_date     \"createDate\", ")
		.append("        bttfi.trade_date      \"tradeDate\", ")
		.append("        btai.audit_status     \"auditStatus\", ")
		.append("        bttfi.trade_status    \"tradeStatus\", ")
		.append("        bttfi.third_pay       \"thirdPay\" ") 
		.append("   from bao_t_trade_flow_info bttfi, ")
		.append("        bao_t_cust_info       btci, ")
		.append("        bao_t_cust_info       btci2, ")
		.append("        com_t_user            ctu, ")
		.append("        bao_t_audit_info      btai ")
		.append("  where bttfi.cust_id = btci.id ")
		.append("    and bttfi.create_user = btci2.id(+)")
		.append("    and bttfi.create_user = ctu.id(+) ");
		
		SqlCondition sqlCondition = new SqlCondition(sql, param);
		sqlCondition.addString("tradeType", "bttfi.trade_type")
					.addString("custName", "btci.cust_name")
					.addString("credentialsCode", "btci.credentials_code")
					.addString("mobile", "btci.mobile")
					.addString("auditStatus", "btai.audit_status")
					.addBeginDate("beginOperateDate", "bttfi.create_date")
					.addEndDate("endOperateDate", "bttfi.create_date")
					.addString("tradeStatus", "bttfi.trade_status")
					.addString("thirdPay", "bttfi.third_pay") 
					.addString("custId","btci.id"); // 根据客户ID查
					;
		
		String tradeType = (String) param.get("tradeType");
		if(SubjectConstant.TRADE_FLOW_TYPE_OFFLINE_RECHARGE.equals(tradeType)){
			sqlCondition.addSql("and bttfi.id = btai.relate_primary ")
						   .addSql("order by decode(btai.audit_status, '待审核', 1, '初审回退', 2, '复审回退', 3, '终审回退', 4, '初审通过', 5, '复审通过', 6, '终审通过', 7, '初审拒绝', 8, '复审拒绝', 9, '终审拒绝', 10), bttfi.trade_date desc");
		} else if (SubjectConstant.TRADE_FLOW_TYPE_OFFLINE_WITHDRAW.equals(tradeType)){
			sqlCondition.addSql("and btai.id = bttfi.relate_primary ")
						.addSql("order by decode(bttfi.trade_status, '未处理', 1, '处理中', 2, '提现成功', 3, '提现失败', 4), bttfi.create_date desc");
		}
		
		return repositoryUtil.queryForPageMap(
				sqlCondition.toString(), 
				sqlCondition.toArray(), 
				Integer.parseInt(param.get("start").toString()), 
				Integer.parseInt(param.get("length").toString()));
	}

	/**
	 * 线下充值，有待审核的数据
	 * 取大于waitingAuditDate且小于等于now的所有待审核数据条数
	 * @param lastDate Date
	 * @param nowDate Date
	 */
	public int countOffLineRechargeData(Date lastDate, Date nowDate) {
		List<Object> list = new ArrayList<Object>();
		
		StringBuilder sql = new StringBuilder()
		.append(" SELECT count(1) \"count\" ")
		.append("   FROM bao_t_trade_flow_info tf ")
		.append("   	 , bao_t_audit_info aud ")
		.append("  WHERE tf.id = aud.RELATE_PRIMARY ")
//		.append("    AND EXISTS( ")
//		.append(" 		SELECT * FROM BAO_T_BANK_CARD_INFO card  ")
//		.append("    		 WHERE card.CARD_NO = tf.BANK_CARD_NO ")
////		.append(" 		   AND card.RECORD_STATUS = '有效' ")
//		.append(" 		   AND card.BANK_FLAG = '线下' ")
//		.append("    ) ")
		.append("    AND aud.AUDIT_STATUS = ? ")
		.append("    AND tf.TRADE_TYPE = ? ")
		.append("    AND aud.LAST_UPDATE_DATE > ? ")
		.append("    AND aud.LAST_UPDATE_DATE <= ? ")
		;
		list.add(Constant.OFFLINE_RECHARGE_AUDIT_STATUS_01);
		list.add(Constant.OPERATION_TYPE_42);
		list.add(lastDate);
		list.add(nowDate);
		Integer count = jdbcTemplate.queryForObject(sql.toString(), list.toArray(), Integer.class);
		return (count != null ? count.intValue() : 0);
	}

	/**
	 * 附属银行卡，有待审核的数据
	 * 取大于waitingAuditDate且小于等于now的所有待审核数据条数
	 * @param lastDate Date
	 * @param nowDate Date
	 */
	public int countOffLineCardData(Date lastDate, Date nowDate) {
		List<Object> list = new ArrayList<Object>();
		
		StringBuilder sql = new StringBuilder()
		.append(" SELECT count(1) \"count\" ")
		.append("   FROM BAO_T_TRADE_FLOW_INFO tf   ")
		.append("   	, BAO_T_AUDIT_INFO aud   ")
		.append("   WHERE aud.RELATE_PRIMARY = tf.ID ")
		.append("    AND aud.RELATE_TYPE = ? ")
		.append("    AND aud.AUDIT_STATUS = ? ")
		.append("    AND tf.TRADE_TYPE = ?  ")
		.append("    AND aud.LAST_UPDATE_DATE > ? ")
		.append("    AND aud.LAST_UPDATE_DATE <= ? ")
		;
		list.add(Constant.TABLE_BAO_T_TRADE_FLOW_INFO);
		list.add(Constant.OFFLINE_RECHARGE_AUDIT_STATUS_01);
		list.add(Constant.OPERATION_TYPE_44);
		list.add(lastDate);
		list.add(nowDate);
		Integer count = jdbcTemplate.queryForObject(sql.toString(), list.toArray(), Integer.class);
		return (count != null ? count.intValue() : 0);
	}
	
	@Override
	public Page<Map<String, Object>> findRechargeList(Map<String, Object> params) {
		StringBuilder sql = new StringBuilder()
		.append(" select a.create_date \"createDate\", ")
		.append("        a.trade_amount \"tradeAmount\", ")
		.append("        a.bank_name \"bankName\", ")
		.append("        decode(a.bank_card_no, null, '', substr(a.bank_card_no, 1, 4) || '****' || substr(a.bank_card_no, -4)) \"bankCardNo\", ")
		.append("        a.trade_status \"tradeStatus\", ")
		.append("        a.trade_expenses \"tradeExpenses\" ")
		.append("   from bao_t_trade_flow_info a ")
		.append("  where a.trade_type = '充值' ");
		
		SqlCondition sqlCondition = new SqlCondition(sql, params);
		sqlCondition.addString("custId", "a.cust_id")
					.addString("tradeStatus", "a.trade_status")
					.addBeginDate("startCreateDate", "a.create_date")
					.addEndDate("endCreateDate", "a.create_date")
					.addSql("order by a.create_date desc");
		
		return repositoryUtil.queryForPageMap(				
				sqlCondition.toString(),
				sqlCondition.toArray(),
				Integer.parseInt(params.get("start").toString()),
				Integer.parseInt(params.get("length").toString()));
	}
}
