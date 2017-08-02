package com.slfinance.shanlincaifu.repository.custom.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.WealthInfoRepositoryCustom;
import com.slfinance.shanlincaifu.utils.ArithUtil;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.SqlCondition;
import com.slfinance.vo.ResultVo;

@Repository
public class WealthInfoRepositoryImpl implements WealthInfoRepositoryCustom {

	@Autowired
	private RepositoryUtil repositoryUtil;
	
	@Override
	public ResultVo queryWealthList(Map<String, Object> params)throws SLException {
		Map<String, Object> result = new HashMap<String, Object>();
		StringBuilder sqlString = new StringBuilder()
		.append("  select t.id \"wealthId\", p.lending_type \"lendingType\", t.lending_no \"lendingNo\", t.plan_total_amount \"planTotalAmount\", ")
		.append("        p.type_term \"typeTerm\", p.year_rate \"yearRate\", t.award_rate \"awardRate\", t.release_date \"releaseDate\",  ")
		.append("        t.effect_date \"effectDate\", t.end_date \"endDate\", t.wealth_status \"wealthStatus\" ")
		.append("  from bao_t_wealth_info t ")
		.append("   inner join bao_t_wealth_type_info p on t.wealth_type_id = p.id ")
		.append("  where 1 = 1  ");
		
		List<Object> objList = new ArrayList<Object>();
		if(!StringUtils.isEmpty(params.get("lendingType"))) {
			sqlString.append(" and p.lending_type = ?");
			objList.add(params.get("lendingType").toString());
		}
		if(!StringUtils.isEmpty(params.get("typeTerm"))) {
			sqlString.append(" and p.type_term = ?");
			objList.add(params.get("typeTerm").toString());
		}
		if(!StringUtils.isEmpty(params.get("wealthStatus"))) {
			sqlString.append(" and t.wealth_status = ?");
			objList.add(params.get("wealthStatus").toString());
		}
		if(!StringUtils.isEmpty(params.get("beginReleaseDate"))) {
			sqlString.append(" and to_char(t.release_date, 'yyyy-MM-dd') >= ?");
			objList.add(params.get("beginReleaseDate").toString());
		}
		if(!StringUtils.isEmpty(params.get("endReleaseDate"))) {
			sqlString.append(" and to_char(t.release_date, 'yyyy-MM-dd') <= ?");
			objList.add(params.get("endReleaseDate").toString());
		}
		if(!StringUtils.isEmpty(params.get("beginEffectDate"))) {
			sqlString.append(" and to_char(t.effect_date, 'yyyy-MM-dd') >= ?");
			objList.add(params.get("beginEffectDate").toString());
		}
		if(!StringUtils.isEmpty(params.get("endEffectDate"))) {
			sqlString.append(" and to_char(t.effect_date, 'yyyy-MM-dd') <= ?");
			objList.add(params.get("endEffectDate").toString());
		}
		if(!StringUtils.isEmpty(params.get("beginEndDate"))) {
			sqlString.append(" and to_char(t.end_date, 'yyyy-MM-dd') >= ?");
			objList.add(params.get("beginEndDate").toString());
		}
		if(!StringUtils.isEmpty(params.get("endEndDate"))) {
			sqlString.append(" and to_char(t.end_date, 'yyyy-MM-dd') <= ?");
			objList.add(params.get("endEndDate").toString());
		}
		if(!StringUtils.isEmpty(params.get("lendingNo"))) {
			sqlString.append(" and t.lending_no like '" + (String) params.get("lendingNo") +"%'");
		}
		
		sqlString.append(" order by decode(t.wealth_status, '待审核', 1, '审核回退', 2, '待发布', 3, '发布中', 4, '已满额', 5, '收益中', 6, '提前赎回中', 7, '到期处理中', 8, '提前赎回', 9, '已到期', 10, '流标', 11, '拒绝', 12) asc, t.release_date asc, p.lending_type asc, t.lending_no asc ");
		
		Page<Map<String, Object>> page = repositoryUtil.queryForPageMap(sqlString.toString(), objList.toArray(), Integer.valueOf(params.get("start").toString()),
				Integer.valueOf(params.get("length").toString()));
		result.put("iTotalDisplayRecords", page.getTotalElements());
		result.put("data", page.getContent());
		return new ResultVo(true, "查询成功！", result);
	}

	@Override
	public ResultVo queryWealthDetailById(Map<String, Object> params)throws SLException {
		
		StringBuffer SqlString = new StringBuffer()
		.append("  select t.id \"wealthId\", p.lending_type \"lendingType\", t.lending_no \"lendingNo\", t.plan_total_amount \"planTotalAmount\", p.type_term \"typeTerm\", ")
		.append("        p.year_rate \"yearRate\", t.award_rate \"awardRate\", t.release_date \"releaseDate\", t.effect_date \"effectDate\", t.end_date \"endDate\", ")
		.append("        t.wealth_status \"wealthStatus\", t.increase_amount \"increaseAmount\", p.income_type \"incomeType\", t.wealth_descr \"wealthDescr\", t.invest_min_amount \"investMinAmount\", p.id \"wealthTypeId\"  ")
		.append("  from bao_t_wealth_info t,  ")
		.append("      bao_t_wealth_type_info p ")
		.append("  where t.wealth_type_id = p.id  ")
		.append("     and  t.id = ? ");
		
		Map<String, Object> result = repositoryUtil.queryForMap(SqlString.toString(), new Object[] { params.get("wealthId").toString()}).get(0);
		
		StringBuffer SqlLogString = new StringBuffer()
		.append("  select T.ID \"auditId\", T.CREATE_DATE \"auditDate\", U.USER_NAME \"auditUser\", ")
		.append("        T.OPER_AFTER_CONTENT \"auditStatus\", T.OPER_DESC \"auditMemo\" ")
		.append("  from BAO_T_LOG_INFO T ")
		.append("  INNER JOIN COM_T_USER U ON T.OPER_PERSON = U.ID  ")
		.append("  where T.RELATE_TYPE = ? AND T.RELATE_PRIMARY = ? ")
		.append("    AND T.LOG_TYPE = ?  "
				+ " ORDER BY T.CREATE_DATE DESC ");
		
		result.put("auditList", repositoryUtil.queryForMap(SqlLogString.toString(), new Object[] {Constant.TABLE_BAO_T_WEALTH_INFO, params.get("wealthId"), Constant.OPERATION_TYPE_57}));
		return new ResultVo(true, "查询成功！", result);
	}

	/**
	 * 
	 * 用户计划投资列表
	 * 
	 * @date 2016-02-23
	 * @author zhiwen_feng
	 * @param params
	 * @return ResultVo
	 * @throws SLException
	 */
	@Override
	public ResultVo queryWealthJoinList(Map<String, Object> params)throws SLException {
		Map<String,Object> result = Maps.newHashMap();
		String newDay = DateUtils.formatDate(new Date(), "yyyyMM") + "01";
		
		List<Object> objList = Lists.newArrayList();
		StringBuffer SqlString = new StringBuffer()
				.append(" select * from (")
				.append(" select  i.id                 \"investId\",  ")
				.append("         w.id                 \"wealthId\",  ")
				.append("         c.id                 \"custId\",  ")
				.append("         c.cust_name          \"custName\",  ")
				.append("         c.login_name         \"loginName\",  ")
				.append("         w.lending_no         \"lendingNo\",  ")
				.append("         i.invest_amount      \"investAmount\",  ")
				.append("         i.invest_date        \"investDate\", ")
				.append("         p.lending_type       \"lendingType\",  ")
				.append("         p.type_term          \"typeTerm\",  ")
				.append("         i.invest_status      \"investStatus\",  ")
				.append("         w.effect_date        \"effectDate\",  ")
				.append("         w.end_date           \"endDate\",  ")
				.append("         a.atone_total_amount \"accountAmount\",  ")
				.append("         a.cleanup_date       \"atoneDate\",  ")
				.append("         a.atone_total_value  \"atoneAmount\",  ")
				.append("         decode(nvl(c.is_employee, '否'), '是', c.cust_name, custManage.Cust_Name) \"custManageName\"  ")
				.append("     from bao_t_wealth_info w  ")
				.append("      inner join bao_t_wealth_type_info p  on w.wealth_type_id = p.id  ")
				.append("      inner join bao_t_invest_info i on i.wealth_id = w.id  ")
				.append("      inner join bao_t_cust_info c on c.id = i.cust_id  ")
				.append("      left join  bao_t_atone_info a on a.invest_id = i.id and a.audit_status = '通过'  ")
				.append("      left join  bao_t_cust_recommend_info r on r.quilt_cust_id = i.cust_id and r.record_status = '有效' ")
				.append("      left join  bao_t_cust_info custManage on custManage.id = r.cust_id   ")
				.append("   where i.invest_date >= '"+ newDay +"' %s");
		
		StringBuilder whereSqlString= new StringBuilder();
		SqlCondition sqlCondition = new SqlCondition(whereSqlString, params);
		sqlCondition.addString("custName", "c.cust_name")
					.addString("loginName", "c.login_name")
				    .addString("investStatus", "i.invest_status")
				    .addString("lendingType", "p.lending_type")
				    .addString("typeTerm", "p.type_term")
				    .addBeginDate("beginEffectDate", "w.effect_date")
				    .addEndDate("endEffectDate", "w.effect_date")
				    .addBeginDate("beginEndDate", "w.end_date")
				    .addEndDate("endEndDate", "w.end_date")
				    .addBeginDate("beginAtoneDate", "a.cleanup_date")
				    .addEndDate("endAtoneDate", "a.cleanup_date")
				    .addString("lendingNo", "w.lending_no")
					.addString("custManageName", "custManage.cust_name");
		
		String sql = String.format(SqlString.toString(), sqlCondition.toString());
		SqlString = new StringBuffer(sql)
				.append(" union all")
				.append(" select i.id                 \"investId\",  ")
				.append("         w.id                 \"wealthId\",  ")
				.append("         c.id                 \"custId\",  ")
				.append("         c.cust_name          \"custName\",  ")
				.append("         c.login_name         \"loginName\",  ")
				.append("         w.lending_no         \"lendingNo\",  ")
				.append("         i.invest_amount      \"investAmount\",  ")
				.append("         i.invest_date        \"investDate\", ")
				.append("         p.lending_type       \"lendingType\",  ")
				.append("         p.type_term          \"typeTerm\",  ")
				.append("         i.invest_status      \"investStatus\",  ")
				.append("         w.effect_date        \"effectDate\",  ")
				.append("         w.end_date           \"endDate\",  ")
				.append("         a.atone_total_amount \"accountAmount\",  ")
				.append("         a.cleanup_date       \"atoneDate\",  ")
				.append("         a.atone_total_value  \"atoneAmount\",  ")
				.append("         custManage.Cust_Name \"custManageName\"  ")
				.append("     from bao_t_wealth_info w  ")
				.append("      inner join bao_t_wealth_type_info p  on w.wealth_type_id = p.id  ")
				.append("      inner join bao_t_invest_info i on i.wealth_id = w.id  ")
				.append("      inner join bao_t_cust_info c on c.id = i.cust_id  ")
				.append("      left join bao_t_commission_detail_info q on q.invest_id = i.id ")
				.append("      left join bao_t_commission_info p on p.id = q.commission_id     ")
				.append("      left join  bao_t_atone_info a on a.invest_id = i.id and a.audit_status = '通过'  ")
				.append("      left join  bao_t_cust_info custManage on custManage.id = p.cust_id   ")
				.append("      where i.invest_date < '"+ newDay +"' %s")
				.append(" )t  ")
				.append(" order by t.\"effectDate\" desc, t.\"lendingType\", t.\"lendingNo\", t.\"investDate\" desc  ");
		
		objList.addAll(sqlCondition.getObjectList());
		objList.addAll(sqlCondition.getObjectList());
		Page<Map<String, Object>> page = repositoryUtil.queryForPageMap(String.format(SqlString.toString(), sqlCondition.toString()), objList.toArray(), Integer.parseInt(params.get("start").toString()), Integer.parseInt(params.get("length").toString()));
		result.put("iTotalDisplayRecords", page.getTotalElements());
		result.put("data", page.getContent());
		return new ResultVo(true, "查询成功！", result);
	}
	
//	/**
//	 * 添加可选条件的sql
//	 * 
//	 * @author zhiwen_feng
//	 * @param list 参数列表
//	 * @param params 入参
//	 * @param SqlString sql
//	 * @param property 字段
//	 * @param sql 需要添加的sql
//	 */
//	private void addSql(List<Object> objList, Map<String, Object> params, StringBuffer SqlString, String property, String sql){
//		if(!StringUtils.isEmpty(params.get(property))) {
//			SqlString.append(sql);
//			objList.add(params.get(property));
//		}
//	}

	/**
	 * 项目列表
	 * 
	 * @author zhiwen_feng
	 * @date 2016-02-24
	 * @param params
     *      <tt>start           :String:起始值</tt><br>
     *      <tt>length          :String:长度</tt><br>
     *      <tt>lendingType     :String:出借方式（可选）</tt><br>
     *      <tt>typeTerm        :String:项目期限（可选）</tt><br>
     *      <tt>wealthStatus    :String:项目状态（可选）</tt><br>
     *      <tt>beginReleaseDate:String:开始发布日期（可选）</tt><br>
     *      <tt>endReleaseDate  :String:结束发布日期（可选）</tt><br>
     *      <tt>beginEffectDate :String:开始生效日期（可选）</tt><br>
     *      <tt>endEffectDate   :String:结束生效日期（可选）</tt><br>
     *      <tt>beginEndDate    :String:开始到期日期（可选）</tt><br>
     *      <tt>endEndDate      :String:结束到期日期（可选）</tt><br>
	 * @return ResultVo
     *      <tt>totalPlanTotalAmount    :String:项目总金额汇总</tt><br>
     *      <tt>totalAlreadyInvestAmount:String:已募集金额汇总</tt><br>
     *      <tt>totalWaitingMatchAmount :String:待匹配金额汇总</tt><br>
     *      <tt>data                    :String:List<Map<String,Object>></tt><br>
     *      <tt>wealthId                :String:计划主键</tt><br>
     *      <tt>lendingType             :String:计划名称</tt><br>
     *      <tt>lendingNo               :String:项目期数</tt><br>
     *      <tt>planTotalAmount         :String:项目总额(元)</tt><br>
     *      <tt>alreadyInvestAmount     :String:已募集金额（元）</tt><br>
     *      <tt>waitingMatchAmount      :String:待匹配金额(元)</tt><br>
     *      <tt>investScale             :String:已投百分比</tt><br>
     *      <tt>typeTerm                :String:项目期限(月)</tt><br>
     *      <tt>yearRate                :String:年化收益率</tt><br>
     *      <tt>awardRate               :String:奖励利率</tt><br>
     *      <tt>incomeType              :String:结算方式</tt><br>
     *      <tt>releaseDate             :String:发布日期    </tt><br>
     *      <tt>effectDate              :String:生效日期</tt><br>
     *      <tt>endDate                 :String:到期日期</tt><br>
     *      <tt>wealthStatus            :String:项目状态</tt><br>
	 * @throws SLException
	 */
	@Override
	public Map<String, Object> queryAllWealthList(Map<String, Object> params)throws SLException {
		Map<String, Object> result = Maps.newHashMap();
		
		StringBuffer SqlString = new StringBuffer()
		.append("  select t.id \"wealthId\", p.lending_type \"lendingType\", t.lending_no \"lendingNo\", t.plan_total_amount \"planTotalAmount\",  ")
		.append("             pro.already_invest_amount \"alreadyInvestAmount\", pro.already_invest_scale \"alreadyInvestScale\", p.type_term \"typeTerm\",  ")
		.append("             t.year_rate \"yearRate\", round(t.award_rate, 4) \"awardRate\", p.income_type \"incomeType\", t.release_date \"releaseDate\",  ")
		.append("             t.effect_date \"effectDate\", t.end_date \"endDate\", t.wealth_status \"wealthStatus\", t.plan_total_amount - pro.already_invest_amount \"currUsableAmount\",  ")
		.append("             nvl((select sum(sa.account_amount) from bao_t_invest_info i, bao_t_sub_account_info sa where sa.relate_primary = i.id and i.wealth_id = t.id), 0) \"waitingMatchAmount\" ")
		.append("       from   ")
		.append("                  bao_t_wealth_info t  ")
		.append("       inner join bao_t_wealth_type_info p on t.wealth_type_id = p.id  ")
		.append("       inner join bao_t_project_invest_info pro on pro.wealth_id = t.id  ")
		.append("       where 1 = 1 and   t.wealth_status in ('发布中', '已满额', '收益中', '到期处理中', '已到期', '流标')  %s ");
		StringBuilder whereSqlString= new StringBuilder();
		SqlCondition sqlCondition = new SqlCondition(whereSqlString, params);
		sqlCondition.addString("lendingType", "p.lending_type")
					.addString("typeTerm", "p.type_term")
					.addString("wealthStatus", "t.wealth_status")
					.addBeginDate("beginReleaseDate", "t.release_date")
					.addEndDate("endReleaseDate", "t.release_date")
					.addBeginDate("beginEffectDate", "t.effect_date")
					.addEndDate("endEffectDate", "t.effect_date")
					.addBeginDate("beginEndDate", "t.end_date")
					.addEndDate("endEndDate", "t.end_date")
					.addRightLike("lendingNo", "t.lending_no");
		SqlString.append("   order by decode(t.wealth_status, '发布中', '1', '已满额', '2', '收益中', '3', '到期处理中', '4', '已到期', '5', '流标', '6'), t.effect_date desc, p.lending_type, t.lending_no desc ");
		
		Page<Map<String, Object>> pageVo = repositoryUtil.queryForPageMap(String.format(SqlString.toString(), sqlCondition.toString()),
				sqlCondition.toArray(), Integer.parseInt(params.get("start").toString()), Integer.parseInt(params.get("length").toString()));
		
		result.put("iTotalDisplayRecords", pageVo.getTotalElements());
		result.put("data", pageVo.getContent());
		return result;
	}

	@Override
	public Map<String, Object> queryAllWealthSummary(Map<String, Object> params)throws SLException {
		Map<String, Object> result = Maps.newHashMap();
		StringBuffer SqlString = new StringBuffer()
		.append("  select nvl(sum(t.plan_total_amount), 0)  \"totalPlanTotalAmount\", ")
		.append("         nvl(sum(pro.already_invest_amount),0) \"totalAlreadyInvestAmount\" ")
		.append("  from  ")
		.append("             bao_t_wealth_info t ")
		.append("  inner join bao_t_wealth_type_info p on t.wealth_type_id = p.id ")
		.append("  inner join bao_t_project_invest_info pro on pro.wealth_id = t.id ")
		.append("  where 1 = 1 and  t.wealth_status in('发布中', '已满额', '收益中', '到期处理中', '已到期', '流标') %s");
		
		StringBuffer SqlString1 = new StringBuffer()
		.append("  select nvl(sum(sa.account_amount), 0)  \"totalWaitingMatchAmount\" ")
		.append("  from  ")
		.append("             bao_t_wealth_info t ")
		.append("  inner join bao_t_wealth_type_info p on t.wealth_type_id = p.id ")
		.append("  inner join bao_t_project_invest_info pro on pro.wealth_id = t.id ")
		.append("  left join  bao_t_invest_info i on i.wealth_id = t.id ")
		.append("  left join  bao_t_sub_account_info sa on sa.relate_type = 'BAO_T_INVEST_INFO' and sa.relate_primary = i.id  ")
		.append("  where 1 = 1 and  t.wealth_status in('发布中', '已满额', '收益中', '到期处理中', '已到期', '流标') %s");
		
		StringBuilder whereSqlString= new StringBuilder();
		SqlCondition sqlCondition = new SqlCondition(whereSqlString, params);
		sqlCondition.addString("lendingType", "p.lending_type")
					.addString("typeTerm", "p.type_term")
					.addString("wealthStatus", "t.wealth_status")
					.addBeginDate("beginReleaseDate", "t.release_date")
					.addEndDate("endReleaseDate", "t.release_date")
					.addBeginDate("beginEffectDate", "t.effect_date")
					.addEndDate("endEffectDate", "t.effect_date")
					.addBeginDate("beginEndDate", "t.end_date")
					.addEndDate("endEndDate", "t.end_date");
		result = repositoryUtil.queryForMap(String.format(SqlString.toString(), sqlCondition.toString()),sqlCondition.toArray()).get(0);
		result.putAll(repositoryUtil.queryForMap(String.format(SqlString1.toString(), sqlCondition.toString()),sqlCondition.toArray()).get(0));
		return result;
	}

	@Override
	public ResultVo queryWaitingMatchList(Map<String, Object> params)throws SLException {
		Map<String, Object> result = Maps.newHashMap();
		
		StringBuffer SqlString = new StringBuffer()
		.append(" select c.cust_name \"custName\", sa.account_amount \"accountAmount\", sa.last_update_date  \"lastUpdateDate\" ")
		.append("        from  ")
		.append("             bao_t_wealth_info t ")
		.append("  inner join bao_t_invest_info i on i.wealth_id = t.id ")
		.append("  inner join bao_t_sub_account_info sa on sa.relate_type = 'BAO_T_INVEST_INFO' and sa.relate_primary = i.id ")
		.append("  inner join bao_t_cust_info c on i.cust_id = c.id ")
		.append(" where sa.account_amount > 0 and t.wealth_status in('发布中', '已满额', '收益中', '到期处理中', '已到期') %s")
		.append("  order by sa.account_amount desc, sa.last_update_date desc");
		
		StringBuilder whereSqlString= new StringBuilder();
		SqlCondition sqlCondition = new SqlCondition(whereSqlString, params);
		sqlCondition.addString("wealthId", "t.id")
					.addString("custName", "c.cust_name")
					.addBeginDate("beginOperateDate", "sa.last_update_date")
					.addEndDate("endOperateDate", "sa.last_update_date")
					.addRightLike("lendingNo", "t.lending_no");

		Page<Map<String, Object>> pageVo = repositoryUtil.queryForPageMap(String.format(SqlString.toString(), sqlCondition.toString()), sqlCondition.toArray(), 
				Integer.parseInt(params.get("start").toString()), Integer.parseInt(params.get("length").toString()));
		
		result.put("iTotalDisplayRecords", pageVo.getTotalElements());
		result.put("data", pageVo.getContent());
		return new ResultVo(true, "查询成功", result);
	}

	@Override
	public List<Map<String, Object>> queryPriorityWealthList(Map<String, Object> params) throws SLException {

		StringBuffer SqlString = new StringBuffer()
		.append("  select t.id \"wealthId\", ")
		.append("         p.lending_type \"lendingType\", ")
		.append("         t.lending_no \"lendingNo\", ")
		.append("         t.wealth_type_id \"wealthTypeId\", ")
		.append("         t.plan_total_amount \"planTotalAmount\", ")
		.append("         t.invest_min_amount \"investMinAmount\", ")
		.append("         t.invest_max_amount \"investMaxAmount\", ")
		.append("         t.increase_amount \"increaseAmount\", ")
		.append("         decode(t.wealth_status, '发布中', t.plan_total_amount - pi.already_invest_amount, 0) \"currUsableValue\", ")
		.append("         decode(t.wealth_status, '发布中', pi.already_invest_scale, 1)  \"investScale\", ")
		.append("         p.type_term \"typeTerm\", ")
		.append("         t.year_rate \"yearRate\", ")
		.append("         round(t.award_rate, 4) \"awardRate\", ")
		.append("         p.income_type \"incomeType\", ")
		.append("         t.release_date \"releaseDate\", ")
		.append("         t.effect_date \"effectDate\", ")
		.append("         t.end_date \"endDate\", ")
		.append("         t.wealth_status \"wealthStatus\", ")
		.append("         p.type_term \"sort\", ")
		.append("         pi.already_invest_peoples \"investPeoples\" ")
		.append("    from bao_t_wealth_info t ")
		.append(" 		  inner join bao_t_wealth_type_info p on t.wealth_type_id = p.id  ")
		.append(" 		  inner join bao_t_project_invest_info pi on pi.wealth_id = t.id  ")
		.append(" 		 where t.wealth_status in ('发布中', '已满额', '收益中', '到期处理中', '已到期', '流标')  ")
		.append(" 		  order by decode(t.wealth_status, '发布中', 1, '已满额', 2, '收益中', '3', '到期处理中', '4', '已到期', '5', '流标', '6'), p.sort asc, t.release_date desc  ");
		
		return repositoryUtil.queryForMap(SqlString.toString(), new Object[]{});
	}

	@Override
	public ResultVo queryWealthDetail(Map<String, Object> params)throws SLException {
		Map<String, Object> data = null;
		StringBuffer SqlString = new StringBuffer()
		.append("  select t.id \"wealthId\", ")
		.append("         p.lending_type \"lendingType\", ")
		.append("         p.rebate_ratio \"rebateRatio\",  ")
		.append("         t.lending_no \"lendingNo\", ")
		.append("         t.plan_total_amount \"planTotalAmount\", ")
		.append("         t.invest_min_amount \"investMinAmount\", ")
		.append("         t.increase_amount \"increaseAmount\", ")
		.append("         p.income_type \"incomeType\", ")
		.append("         t.award_rate \"awardRate\", ")
		.append("         p.type_term \"typeTerm\", ")
		.append("         t.year_rate \"yearRate\", ")
		.append("         round(t.award_rate, 4) \"awardRate\", ")
		.append("         t.release_date \"releaseDate\", ")
		.append("         t.effect_date \"effectDate\", ")
		.append("         t.end_date \"endDate\", ")
		.append("         t.wealth_status \"wealthStatus\", ")
		.append("         t.wealth_descr \"wealthDescr\", ")
		.append("         decode(t.wealth_status, '发布中', t.plan_total_amount - pi.already_invest_amount, 0)  \"reminderAmount\", ")
		.append("         decode(t.wealth_status, '发布中', t.plan_total_amount - pi.already_invest_amount, 0) \"currUsableValue\", ")
		.append("         pi.already_invest_amount \"alreadyInvestAmount\", ")
		.append("         pi.already_invest_scale \"investScale\" ")
		.append("    from bao_t_wealth_info t ")
		.append("       inner join bao_t_wealth_type_info p on t.wealth_type_id = p.id  ")
		.append("       inner join bao_t_project_invest_info pi on pi.wealth_id = t.id  ")
		.append("       where t.id = ? ");
		data = repositoryUtil.queryForMap(SqlString.toString(), new Object[]{params.get("wealthId")}).get(0);
		data.put("exitFee", params.get("exitFee"));
		
		return new ResultVo(true, "查询查看优选计划成功！", data);
	}

	/**
	 * 投资记录 --分页 
	 */
	@Override
	public ResultVo queryWealthInvestList(Map<String, Object> params)throws SLException {
		Map<String, Object> result = Maps.newHashMap();
		StringBuffer SqlString = new StringBuffer()
		.append("  select t.id \"wealthId\", d.create_date \"investDate\", d.invest_amount \"investAmount\", substr(c.mobile,1,3)||'****'||substr(c.mobile,-4,4) \"loginName\" ")
		.append("        from bao_t_wealth_info t ")
		.append("  inner join bao_t_wealth_type_info p on t.wealth_type_id = p.id ")
		.append("  inner join bao_t_invest_info i on i.wealth_id = t.id ")
		.append("  inner join bao_t_invest_detail_info d on d.invest_id = i.id ")
		.append("  inner join bao_t_cust_info c on c.id = i.cust_id ")
		.append("  where t.id = ? ")
		.append("  order by t.effect_date desc, p.lending_type, p.type_term, d.create_date desc  ");
		
		Page<Map<String, Object>> pageVo = repositoryUtil.queryForPageMap(SqlString.toString(), new Object[]{params.get("wealthId")}, 
				Integer.parseInt(params.get("start").toString()), Integer.parseInt(params.get("length").toString()));
		
		result.put("iTotalDisplayRecords", pageVo.getTotalElements());
		result.put("data", pageVo.getContent());
		return new ResultVo(true, "查询成功", result);
	}

	/**
	 * 客户理财计划汇总信息
	 */
	@Override
	public List<Map<String, Object>> queryProjectIncome(Map<String, Object> params)throws SLException {
		StringBuffer SqlString = new StringBuffer()
		.append("  select nvl(sum(a.\"investAmount\"), 0) \"totalTradeAmount\",   ")
		.append("         nvl(sum(a.\"backAmount\"), 0)   \"totalIncomeAmount\",   ")
		.append("         count(1)                      \"totalCounts\",  ")
		.append("         a.\"investStatus\"              \"investStatus\"  ")
		.append("  from (   ")
		.append("   select i.invest_amount \"investAmount\",  ")
		.append("         ((select sum(r.fact_payment_amount - r.except_payment_principal) from bao_t_wealth_payment_plan_info r where r.invest_id = i.id and r.payment_status = '已回收')  ")
		.append("         + (select nvl(sum(s.atone_expenses), 0) from bao_t_atone_info s where s.invest_id = i.id and s.atone_method = '提前赎回' and s.atone_status = '处理成功'))         \"backAmount\",  ")
		.append("         decode(i.invest_status, '投资中', '1', '收益中', '2', '提前赎回中', '2', '到期赎回中', '2', '提前赎回', '3', '到期赎回', '3') \"investStatus\"  ")
		.append("    from bao_t_invest_info i  ")
		.append("    inner join bao_t_wealth_info t on t.id = i.wealth_id  ")
		.append("    where i.cust_id = ? and i.record_status = '有效'  ")
		.append("  )a   ")
		.append("  group by a.\"investStatus\"  ");
		return repositoryUtil.queryForMap(SqlString.toString(), new Object[]{params.get("custId")});
	}

	@Override
	public ResultVo queryMyWealthList(Map<String, Object> params)throws SLException {
		Map<String, Object> result = Maps.newHashMap();
		
		StringBuffer SqlString = new StringBuffer()
		.append(" select t.id \"wealthId\", p.lending_type \"lendingType\", t.lending_no \"lendingNo\",  ")
		.append("        t.plan_total_amount \"planTotalAmount\", t.invest_min_amount \"investMinAmount\", ")
		.append("        t.invest_max_amount \"investMaxAmount\", t.increase_amount \"increaseAmount\", ")
		.append("        p.type_term \"typeTerm\", round(t.year_rate, 4) \"yearRate\", round(t.award_rate, 4) \"awardRate\", ")
		.append("        p.income_type \"incomeType\", t.release_date \"releaseDate\", t.effect_date \"effectDate\", ")
		.append("        t.end_date \"endDate\", t.wealth_status \"wealthStatus\", i.invest_amount \"investAmount\", ")
		.append("        trunc(nvl((select sum(pl.except_payment_amount - pl.except_payment_principal) from bao_t_wealth_payment_plan_info pl where pl.invest_id = i.id and pl.payment_status = '未回收'), 0), 2) \"exceptAmount\", ")
		.append("        decode(i.invest_status,'投资中','出借中',i.invest_status) \"investStatus\", t.plan_total_amount - pro.already_invest_amount \"currUsableValue\"  ")
		.append("   from bao_t_invest_info i ")
		.append("   inner join bao_t_wealth_info t on t.id = i.wealth_id ")
		.append("   inner join bao_t_wealth_type_info p on p.id = t.wealth_type_id ")
		.append("   left join bao_t_project_invest_info pro on pro.wealth_id = t.id ")
		.append("   where 1 = 1  %s")
		.append("   order by decode(i.invest_status, '投资中', 1, '收益中', 2, '到期赎回中', 3, '到期赎回', 4, '提前赎回中', 5, '提前赎回', 6) asc, t.effect_date asc, t.end_date asc  ");

		StringBuilder whereSqlString= new StringBuilder();
		SqlCondition sqlCondition = new SqlCondition(whereSqlString, params);
		sqlCondition.addString("custId", "i.cust_id")
					.addString("investStatus", "i.invest_status")
					.addBeginDate("beginEffectDate", "t.effect_date")
					.addEndDate("endEffectDate", "t.effect_date");

		Page<Map<String, Object>> pageVo = repositoryUtil.queryForPageMap(String.format(SqlString.toString(), sqlCondition.toString()), sqlCondition.toArray(), 
				Integer.parseInt(params.get("start").toString()), Integer.parseInt(params.get("length").toString()));
		
		result.put("iTotalDisplayRecords", pageVo.getTotalElements());
		result.put("data", pageVo.getContent());
		return new ResultVo(true, "查询成功", result);
	}

	/**
	 * 优选计划查看
	 */
	@Override
	public ResultVo queryMyWealthDetail(Map<String, Object> params)throws SLException {
		Map<String, Object> result = Maps.newHashMap();
		StringBuffer SqlString = new StringBuffer()
		.append("  select t.id \"wealthId\", p.lending_type \"lendingType\", t.lending_no \"lendingNo\",   ")
		.append("             p.type_term \"typeTerm\", t.year_rate \"yearRate\", round(t.award_rate, 4) \"awardRate\",  ")
		.append("             p.income_type \"incomeType\", t.release_date \"releaseDate\", t.effect_date \"effectDate\",  ")
		.append("             t.end_date \"endDate\", t.wealth_status \"wealthStatus\", i.invest_amount \"investAmount\",  ")
		.append("             nvl((select sum(nvl(pl.except_payment_interest, 0) + nvl(pl.except_payment_award, 0) ) from bao_t_wealth_payment_plan_info pl, bao_t_invest_info invest where invest.id = pl.invest_id and invest.invest_status in ('收益中', '到期处理中', '提前赎回中') and pl.invest_id = i.id ), 0) \"exceptAmount\",  ")
		.append("             (trunc(nvl((select sum(nvl(pl.fact_payment_amount, 0) - nvl(pl.except_payment_principal, 0)) from bao_t_wealth_payment_plan_info pl where pl.invest_id = i.id and pl.payment_status = '已回收'), 0), 2)  ")
		.append("             + (select nvl(sum(s.atone_expenses), 0) from bao_t_atone_info s where s.invest_id = i.id and s.atone_method = '提前赎回' and s.atone_status = '处理成功') )\"receviedAmount\",  ")
		.append("             i.invest_status \"investStatus\", decode(t.wealth_status, '已到期', 0, '发布中', trunc(t.end_date) - trunc(t.effect_date) + 1, '已满额', trunc(t.end_date) - trunc(t.effect_date) + 1, trunc(t.end_date) - trunc(sysdate)) \"reminderDay\", trunc(sa.account_amount, 2) \"accountAmount\", decode(substr(t.first_repay_day,-2,1), 0, substr(t.first_repay_day,-1,1), substr(t.first_repay_day,-2,2))  \"returnInterestDate\",        ")
		.append("             a.cleanup_date \"cleanupDate\"     ")
		.append("        from bao_t_invest_info i  ")
		.append("        inner join bao_t_wealth_info t on t.id = i.wealth_id  ")
		.append("        inner join bao_t_wealth_type_info p on p.id = t.wealth_type_id  ")
		.append("        inner join bao_t_sub_account_info sa on sa.relate_primary = i.id  ")
		.append("       left join bao_t_atone_info a on a.invest_id = i.id and a.atone_status = '处理成功'  ")
		.append("        where 1 = 1  %s ")
		.append("        order by decode(i.invest_status, '投资中', 1, '收益中', 2, '到期赎回中', 3, '到期赎回', 4, '提前赎回中', 5, '提前赎回', 6) asc, t.effect_date asc, t.end_date asc   ");
		
		StringBuilder whereSqlString= new StringBuilder();
		SqlCondition sqlCondition = new SqlCondition(whereSqlString, params);
		sqlCondition.addString("wealthId", "t.id")
					.addString("custId", "i.cust_id");
		List<Map<String, Object>> list = repositoryUtil.queryForMap(String.format(SqlString.toString(), sqlCondition.toString()), sqlCondition.toArray());
		if(null != list && list.size() > 0) {
			result = list.get(0);
		}
		
		return new ResultVo(true, "查询成功", result);
	}

	@Override
	public ResultVo queryMyWealthLoan(Map<String, Object> params)throws SLException {
		Map<String, Object> result = Maps.newHashMap();
		StringBuffer SqlString = new StringBuffer()
		.append("  select h.id \"wealthHoldId\", loan.id \"loanId\", i.cust_id \"custId\",  ")
		.append("                  loan.loan_code \"loanCode\", lc.cust_name \"loanCustName\",   ")
		.append("                  h.hold_amount \"investAmount\", i.create_date \"investDate\",   ")
		.append("                  case when h.hold_status = '已转让' then 0 else trunc(nvl((select sum(s.repayment_total_amount) from bao_t_repayment_plan_info s where s.loan_id = h.loan_id and s.repayment_status = '未还款'), 0) * h.hold_scale, 2) end \"exceptAmount\",   ")
		.append("                  decode(h.hold_status, '已转让', '已转让', loan.credit_acct_status)  \"repaymentStatus\", loan.loan_desc \"loanDesc\"   ")
		.append("               from bao_t_wealth_info t    ")
		.append("            inner join bao_t_invest_info i on i.wealth_id = t.id   ")
		.append("            inner join bao_t_wealth_hold_info h on h.invest_id = i.id   ")
		.append("            inner join bao_t_loan_info loan on loan.id = h.loan_id    ")
		.append("            inner join bao_t_loan_cust_info lc on lc.id = loan.relate_primary  ")
		.append("            where t.id = ?   ")
		.append("              and i.cust_id = ?   ")
		.append("            order by i.invest_date desc, loan.loan_code asc   ");
		
		Page<Map<String, Object>> pageVo = repositoryUtil.queryForPageMap(SqlString.toString(), new Object[] {(String) params.get("wealthId"), (String) params.get("custId")}, 
				Integer.parseInt(params.get("start").toString()), Integer.parseInt(params.get("length").toString()));
		result.put("iTotalDisplayRecords", pageVo.getTotalElements());
		result.put("data", pageVo.getContent());
		return new ResultVo(true, "查询成功", result);
	}

	@Override
	public List<Map<String, Object>> queryMyWeathSummary(Map<String, Object> params)throws SLException {
		StringBuffer SqlString = new StringBuffer()
		.append(" select  ")
		.append("        nvl(sum(trunc(i.invest_amount * p.type_term / 12, 2)), 0) \"sumInvestAmount\", ")
		.append("        nvl(sum(i.invest_amount), 0) \"sumIncomeAmount\" ")
		.append("  from bao_t_invest_info i ")
		.append("   inner join bao_t_wealth_info t on t.id = i.wealth_id ")
		.append("   inner join bao_t_wealth_type_info p on p.id = t.wealth_type_id ")
		.append("   inner join bao_t_cust_info c on i.cust_id = c.id ")
		.append(" where 1 = 1 %s ")
		.append(" union all ")
		.append(" select nvl(sum(trunc(i.invest_amount * pro.type_term / 12, 2)), 0) \"sumInvestAmount\", ")
		.append("        nvl(sum(i.invest_amount), 0) \"sumIncomeAmount\" ")
		.append("    from bao_t_invest_info i ")
		.append("  inner join bao_t_project_info pro on pro.id = i.project_id ")
		.append("  inner join bao_t_cust_info cust on cust.id = i.cust_id ")
		.append(" where 1 =1 %s ");
		
		StringBuilder whereSqlString1= new StringBuilder();
		SqlCondition sqlCondition1 = new SqlCondition(whereSqlString1, params);
		sqlCondition1.addString("custId", "c.invite_origin_id")
					 .addRightLike("yearMonth", "i.invest_date");
		
		StringBuilder whereSqlString2= new StringBuilder();
		SqlCondition sqlCondition2 = new SqlCondition(whereSqlString2, params);
		sqlCondition2.addString("custId", "cust.invite_origin_id")
					 .addRightLike("yearMonth", "i.invest_date");
		
		List<Object> objList = sqlCondition1.getObjectList();
		objList.addAll(sqlCondition2.getObjectList());
		
		List<Map<String, Object>> list = repositoryUtil.queryForMap(String.format(SqlString.toString(), sqlCondition1.toString(), sqlCondition2.toString()), objList.toArray());
		return list;
	}

	@Override
	public ResultVo queryMyWealthIncome(Map<String, Object> params)throws SLException {
		Map<String, Object> data = Maps.newHashMap();
		StringBuffer SqlString = new StringBuffer()
		.append(" select nvl(sum(p.fact_payment_amount - p.except_payment_principal), 0) \"amount\" from bao_t_invest_info i, bao_t_wealth_info t, bao_t_wealth_payment_plan_info p  ")
		.append(" where t.id = i.wealth_id and p.invest_id = i.id and i.record_status = '有效'  ")
		.append("   and p.payment_status = '已回收' ")
		.append("   and i.cust_id = ? ") //已赚金额 (手续费未加)
		.append(" union all ")
		.append(" select nvl(sum(p.except_payment_amount - p.except_payment_principal), 0) \"amount\" from bao_t_invest_info i, bao_t_wealth_info t, bao_t_wealth_payment_plan_info p  ")
		.append(" where t.id = i.wealth_id and p.invest_id = i.id and i.record_status = '有效'  ")
		.append("   and p.payment_status = '未回收' ")
		.append("   and i.cust_id = ? ") //待收收益
		.append(" union all   ")
		.append(" select nvl(sum(i.invest_amount), 0) \"amount\" from bao_t_invest_info i, bao_t_wealth_info t  ")
		.append(" where t.id = i.wealth_id and i.record_status = '有效'  ")
		.append("   and i.invest_status in ('投资中', '收益中', '到期处理中', '提前赎回中') ")
		.append("   and i.cust_id = ? ") //在投金额
		.append(" union all ")
		.append(" select nvl(sum(i.invest_amount), 0) \"amount\" from bao_t_wealth_info t, bao_t_invest_info i  ")
		.append(" where t.id = i.wealth_id and t.record_status = '有效' ")
		.append("   and i.cust_id = ? ") //投资总金额
		.append(" union all ")
		.append(" select nvl(sum(a.atone_expenses), 0) \"amount\" from bao_t_atone_info a, bao_t_invest_info i, bao_t_wealth_info t  ")
		.append(" where a.invest_id = i.id and i.wealth_id = t.id and a.invest_id = i.id  ")
		.append("   and a.atone_method = '提前赎回' and a.atone_status = '处理成功' ")
		.append("   and i.cust_id = ? "); //提前退出手续费
		
		List<Object> objList = Lists.newArrayList();
		objList.add(params.get("custId"));
		objList.add(params.get("custId"));
		objList.add(params.get("custId"));
		objList.add(params.get("custId"));
		objList.add(params.get("custId"));
		
		List<Map<String, Object>> list = repositoryUtil.queryForMap(SqlString.toString(), objList.toArray());
		BigDecimal earnTotalAmount = new BigDecimal(((Map<String, Object>)list.get(0)).get("amount").toString()); //已收收益
		BigDecimal atoneExpenses = new BigDecimal(((Map<String, Object>)list.get(4)).get("amount").toString()); //手续费
		earnTotalAmount = ArithUtil.add(earnTotalAmount, atoneExpenses);
		
		data.put("earnTotalAmount", earnTotalAmount);
		data.put("exceptTotalAmount", ((Map<String, Object>)list.get(1)).get("amount"));
		data.put("investTotalAmount", ((Map<String, Object>)list.get(2)).get("amount"));
		data.put("tradeTotalAmount", ((Map<String, Object>)list.get(3)).get("amount"));
		return new ResultVo(true, "查询成功！", data);
	}

	@Override
	public Map<String, Object> queryWealthContract(Map<String, Object> params)throws SLException {
		StringBuffer SqlString = new StringBuffer()
		.append(" select c.cust_name         \"custName\", ")
		.append("        c.credentials_code  \"credentialsCode\", ")
		.append("        c.mobile            \"mobile\", ")
		.append("        c.login_name        \"loginName\", ")
		.append("        p.lending_type      \"lendingType\", ")
		.append("        t.lending_no        \"lendingNo\", ")
		.append("        i.invest_amount     \"investAmount\", ")
		.append("        p.income_type       \"incomeType\", ")
		.append("        round(t.year_rate, 4)         \"yearRate\", ")
		.append("        round(t.award_rate, 4)        \"awardRate\", ")
		.append("        p.type_term         \"typeTerm\", ")
		.append("        to_char(t.effect_date, 'yyyy-MM-dd')       \"effectDate\", ")
		.append("        to_char(t.end_date, 'yyyy-MM-dd')          \"endDate\", ")
		.append("        t.invest_min_amount \"investMinAmount\", ")
		.append("        t.increase_amount   \"increaseAmount\", ")
		.append("        to_char(t.release_date, 'yyyy-MM-dd')      \"releaseDate\" ")
		.append("   from bao_t_wealth_info t ")
		.append("  inner join bao_t_wealth_type_info p on p.id = t.wealth_type_id ")
		.append("   left join bao_t_invest_info i on i.wealth_id = t.id and i.record_status = '有效' ")
		.append("    and i.cust_id = ? ")
		.append("   left join bao_t_cust_info c   on c.id = i.cust_id ")
		.append("  where t.id = ? ");
		List<Object> objList = Lists.newArrayList();
		objList.add(params.get("custId"));
		objList.add(params.get("wealthId"));
		return repositoryUtil.queryForMap(SqlString.toString(), objList.toArray()).get(0);
	}

	@Override
	public List<Map<String, Object>> queryWealthLoanContract(Map<String, Object> params)throws SLException {
		StringBuffer SqlString = new StringBuffer()
		.append("   select zrc.cust_name \"senderCustName\",  ")
		.append("             zrc.credentials_code \"senderCredentialsCode\",  ")
		.append("             zrc.login_name \"senderLoginName\",  ")
		.append("             src.cust_name \"receiverCustName\",  ")
		.append("             src.credentials_code \"receiverCredentialsCode\",  ")
		.append("             src.login_name \"receiverLoginName\",  ")
		.append("             src.commun_address \"receiverAddress\",  ")
		.append("             l.loan_code \"loanCode\",  ")
		.append("             d.curr_term \"currentTerm\",  ")
		.append("             l.loan_term / l.repayment_cycle \"endTerm\",  ")
		.append("             tran.trade_amount \"loanAmount\", ")
		.append("             i.invest_date \"investDate\" ")
		.append("        from bao_t_wealth_info t  ")
		.append("       inner join bao_t_invest_info i on i.wealth_id = t.id  ")
		.append("       inner join bao_t_cust_info zrc on zrc.id = i.cust_id  ")
		.append("       inner join bao_t_wealth_hold_info h on h.invest_id = i.id  ")
		.append("       inner join bao_t_loan_info l on l.id = h.loan_id  ")
		.append("       inner join bao_t_loan_detail_info d on d.loan_id = l.id  ")
		.append("       inner join bao_t_credit_right_value val on val.loan_id = l.id and val.value_date = to_char(sysdate, 'yyyyMMdd')  ")
		.append("       left join bao_t_repayment_plan_info repay   on repay.loan_id = l.id  and repay.expect_repayment_date = to_char(sysdate, 'yyyyMMdd')  ")
		.append("       inner join bao_t_loan_transfer tran on tran.sender_hold_id = h.id  ")
		.append("       inner join bao_t_cust_info src on src.id = tran.receive_cust_id  ")
		.append("       where t.id = ? and i.cust_id = ? and l.id = ?  ")
		.append("       union all  ")
		.append("      select   ")
		.append("             zrc.cust_name \"receiverCustName\",  ")
		.append("             zrc.credentials_code \"receiverCredentialsCode\",  ")
		.append("             zrc.login_name \"receiverLoginName\",        ")
		.append("             src.cust_name \"senderCustName\",  ")
		.append("             src.credentials_code \"senderCredentialsCode\",  ")
		.append("             src.login_name \"senderLoginName\",        ")
		.append("             src.commun_address \"receiverAddress\",  ")
		.append("             l.loan_code \"loanCode\",  ")
		.append("             d.curr_term \"currentTerm\",  ")
		.append("             l.loan_term / l.repayment_cycle \"endTerm\",  ")
		.append("             tran.trade_amount \"loanAmount\", ")
		.append("             i.invest_date \"investDate\"  ")
		.append("        from bao_t_wealth_info t  ")
		.append("       inner join bao_t_invest_info i on i.wealth_id = t.id  ")
		.append("       inner join bao_t_cust_info src on src.id = i.cust_id  ")
		.append("       inner join bao_t_wealth_hold_info h on h.invest_id = i.id  ")
		.append("       inner join bao_t_loan_info l on l.id = h.loan_id  ")
		.append("       inner join bao_t_loan_detail_info d on d.loan_id = l.id  ")
		.append("       inner join bao_t_credit_right_value val on val.loan_id = l.id and val.value_date = to_char(sysdate, 'yyyyMMdd')  ")
		.append("       left join bao_t_repayment_plan_info repay   on repay.loan_id = l.id  and repay.expect_repayment_date = to_char(sysdate, 'yyyyMMdd')  ")
		.append("       inner join bao_t_loan_transfer tran on tran.receive_hold_id = h.id  ")
		.append("       inner join bao_t_cust_info zrc on zrc.id = tran.sender_cust_id  ")
		.append("       where t.id = ? and i.cust_id = ? and l.id = ?  ");
		
		List<Object> objList = Lists.newArrayList();
		objList.add(params.get("wealthId"));
		objList.add(params.get("custId"));
		objList.add(params.get("loanId"));
		objList.add(params.get("wealthId"));
		objList.add(params.get("custId"));
		objList.add(params.get("loanId"));
		return repositoryUtil.queryForMap(SqlString.toString(), objList.toArray());
	}

	@Override
	public ResultVo queryShowWealthList(Map<String, Object> params)throws SLException {
		Map<String, Object> result = Maps.newHashMap();
		StringBuilder SqlString = new StringBuilder()
		.append(" select *  ")
		.append("        from (select a.*,  ")
		.append("                     ROW_NUMBER() OVER(PARTITION BY a.\"wealthTypeId\" order by decode(a.\"incomeType\", '到期结算本息', 1, '按月返息，到期返本', 2) asc, a.\"typeTerm\" asc, decode(a.\"wealthStatus\", '待审核', 1, '审核回退', 2, '待发布', 3, '发布中', 4, '已满额', 5, '收益中', 6, '提前赎回中', 7, '到期处理中', 8, '提前赎回', 9, '已到期', 10, '流标', 11, '拒绝', 12) asc, a.\"releaseDate\" desc) as c  ")
		.append("                from (select t.id                      \"wealthId\",  ")
		.append("                             p.lending_type            \"lendingType\",  ")
		.append("                             p.rebate_ratio            \"rebateRatio\",  ")
		.append("                             t.lending_no              \"lendingNo\",  ")
		.append("                             t.plan_total_amount       \"planTotalAmount\",  ")
		.append("                             t.invest_min_amount       \"investMinAmount\",  ")
		.append("                             pro.already_invest_amount \"alreadyInvestAmount\",  ")		
		.append("                             decode(t.wealth_status, '发布中', pro.already_invest_scale, 1) \"investScale\",  ")
		.append("                             p.type_term               \"typeTerm\",  ")
		.append("                             t.year_rate               \"yearRate\",  ")
		.append("                             round(t.award_rate, 4)    \"awardRate\",  ")
		.append("                             p.income_type             \"incomeType\",  ")
		.append("                             t.release_date            \"releaseDate\",  ")
		.append("                             t.effect_date             \"effectDate\",  ")
		.append("                             t.end_date                \"endDate\",  ")
		.append("                             t.wealth_status           \"wealthStatus\",  ")
		.append("                             t.wealth_type_id          \"wealthTypeId\",  ")
		.append("                             decode(t.wealth_status, '发布中', t.plan_total_amount - pro.already_invest_amount, 0) \"currUsableValue\"  ")
		.append("                        from bao_t_wealth_info t  ")
		.append("                       inner join bao_t_wealth_type_info p   ")
		.append("                          on p.id = t.wealth_type_id  ")
		.append("                       inner join bao_t_project_invest_info pro  ")
		.append("                          on pro.wealth_id = t.id) a)  ")
		.append("       where c = 1 ")
		.append("       order by decode(\"incomeType\", '到期结算本息', 1, '按月返息，到期返本', 2) asc,  ")
		.append("                \"typeTerm\" asc  ");
		
		SqlCondition sql = new SqlCondition(SqlString, params);
	
		Page<Map<String, Object>> pageVo = repositoryUtil.queryForPageMap(sql.toString(), sql.toArray(), 
				Integer.parseInt(params.get("start").toString()), Integer.parseInt(params.get("length").toString()));
		result.put("iTotalDisplayRecords", pageVo.getTotalElements());
		result.put("data", pageVo.getContent());
		return new ResultVo(true, "查询成功", result);
	}

	@Override
	public List<Map<String, Object>> querySendSmsEffectWealthInfo(Map<String, Object> params)throws SLException {
		StringBuffer SqlString = new StringBuffer()
		.append(" 		select  ")
		.append("              p.lending_type || t.lending_no \"lendingType\", ")
		.append("              round(t.year_rate * 100, 2)                  \"yearRate\", ")
		.append("              i.invest_amount                              \"investAmount\", ")
		.append("              c.cust_name                                  \"custName\", ")
		.append("              c.id                                         \"custId\", ")
		.append("              c.mobile                                     \"mobile\", ")
		.append("              to_char(i.create_date, 'yyyy-MM-dd')         \"investDate\", ")
		.append("              to_char(t.effect_date, 'yyyy-MM-dd')         \"effectDate\", ")
		.append("              to_char(t.end_date, 'yyyy-MM-dd')            \"endDate\" ")
		.append("               ")
		.append("         from bao_t_wealth_info t, ")
		.append("              bao_t_wealth_type_info p,  ")
		.append("              bao_t_invest_info i,  ")
		.append("              bao_t_cust_info c ")
		.append("       where t.wealth_type_id = p.id ")
		.append("         and t.id = i.wealth_id ")
		.append("         and i.cust_id = c.id ")
		.append("         and t.id = ? ");
		List<Object> objList = Lists.newArrayList();
		objList.add(params.get("wealthId"));
		return repositoryUtil.queryForMap(SqlString.toString(), objList.toArray());
	}
	
	@Override
	public ResultVo queryAllShowWealthList(Map<String, Object> params)throws SLException {
		Map<String, Object> result = Maps.newHashMap();
		StringBuilder SqlString = new StringBuilder()
		.append("  select  t.id                      \"wealthId\",   ")
		.append("          p.lending_type            \"lendingType\",   ")
		.append("          p.rebate_ratio            \"rebateRatio\",   ")
		.append("          t.lending_no              \"lendingNo\",   ")
		.append("          t.plan_total_amount       \"planTotalAmount\",   ")
		.append("          t.invest_min_amount       \"investMinAmount\",   ")
		.append("          pro.already_invest_amount \"alreadyInvestAmount\",       ")
		.append("          decode(t.wealth_status, '发布中', pro.already_invest_scale, 1) \"investScale\",   ")
		.append("          p.type_term               \"typeTerm\",   ")
		.append("          t.year_rate               \"yearRate\",   ")
		.append("          round(t.award_rate, 4)    \"awardRate\",   ")
		.append("          p.income_type             \"incomeType\",   ")
		.append("          t.release_date            \"releaseDate\",   ")
		.append("          t.effect_date             \"effectDate\",   ")
		.append("          t.end_date                \"endDate\",   ")
		.append("          t.wealth_status           \"wealthStatus\",   ")
		.append("          t.wealth_type_id          \"wealthTypeId\",   ")
		.append("          decode(t.wealth_status, '发布中', t.plan_total_amount - pro.already_invest_amount, 0) \"currUsableValue\"   ")
		.append("     from bao_t_wealth_info t   ")
		.append("    inner join bao_t_wealth_type_info p    ")
		.append("       on p.id = t.wealth_type_id   ")
		.append("    inner join bao_t_project_invest_info pro   ")
		.append("       on pro.wealth_id = t.id  ")
		.append("    where 1 = 1  ");
		
		SqlCondition sql = new SqlCondition(SqlString, params);
		sql.addList("ids", "t.id");
		sql.addSql(" order by decode(p.income_type, '到期结算本息', 1, '按月返息，到期返本', 2) asc, p.type_term asc ");
	
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sql.toString(), sql.toArray());
		result.put("iTotalDisplayRecords", list.size());
		result.put("data", list);
		return new ResultVo(true, "查询成功", result);
	}

}
