package com.slfinance.shanlincaifu.repository.custom.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.WealthTypeInfoRepositoryCustom;
import com.slfinance.shanlincaifu.utils.SqlCondition;

/**   
 * 基础产品类型数据访问接口实现
 *  
 * @author  liyy
 * @version $Revision:1.0.0, $Date: 2016年02月23日 $ 
 */
@Repository
public class WealthTypeInfoRepositoryImpl implements WealthTypeInfoRepositoryCustom{

	@Autowired
	private RepositoryUtil repositoryUtil;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * 计划名称/出借方式查询
	 * @param param
     *      <tt>enableStatus:String:启用标识（有效）</tt><br>
     * @return List<Map<String, Object>>
	 * @throws SLException 
	 */
	@Override
	public List<Map<String, Object>> findWealthType(Map<String, Object> param) throws SLException {
		StringBuffer sqlString= new StringBuffer()
		.append("  SELECT t.ID  \"wealthTypeId\", t.LENDING_TYPE \"lendingType\" ")
		.append("    FROM BAO_T_WEALTH_TYPE_INFO t  ")
		.append("   WHERE t.ENABLE_STATUS = ? ")
		.append("   ORDER BY t.SORT");
		
		List<Object> objList=new ArrayList<Object>();
		objList.add(param.get("enableStatus"));
		return repositoryUtil.queryForMap(sqlString.toString(), objList.toArray());
	}

	/**
	 * 查询计划类型详情
	 * @param param
     *      <tt>wealthTypeId:String:计划类型主键</tt><br>
	 * @return List
	 * @throws SLException 
	 */
	@Override
	public List<Map<String, Object>> findWealthTypeById(
			Map<String, Object> param) throws SLException {
		
		StringBuffer sqlString= new StringBuffer()
//		.append("  SELECT wt.ID  \"wealthTypeId\", wt.LENDING_TYPE \"lendingType\", wt.TYPE_TERM \"typeTerm\", wt.YEAR_RATE \"yearRate\", wt.INCOME_TYPE \"incomeType\", wt.ENABLE_STATUS ")
//		.append(" 	 \"enableStatus\", w.lending_no \"lendingNo\" ")
//		.append("  FROM BAO_T_WEALTH_TYPE_INFO wt , BAO_T_WEALTH_INFO w ")
//		.append("  WHERE wt.ID = w.WEALTH_TYPE_ID ")
//		.append("    AND wt.ID = ? ");
		.append("  SELECT wt.ID  \"wealthTypeId\", wt.LENDING_TYPE \"lendingType\", wt.TYPE_TERM \"typeTerm\", wt.YEAR_RATE \"yearRate\", wt.INCOME_TYPE \"incomeType\", wt.ENABLE_STATUS ")
		.append(" 	 \"enableStatus\", '' \"lendingNo\", wt.rebate_ratio \"rebateRatio\" ")
		.append("  FROM BAO_T_WEALTH_TYPE_INFO wt ")
		.append("  WHERE wt.ID = ? ");
		
		List<Object> objList=new ArrayList<Object>();
		objList.add(param.get("wealthTypeId"));
		return repositoryUtil.queryForMap(sqlString.toString(), objList.toArray());
	}

	/**
	 * 基础产品-产品列表查询
	 * @param param
     *      <tt>start       :String:起始值</tt><br>
     *      <tt>length      :String:长度</tt><br>
     *      <tt>lendingType :String:出借方式（可选）</tt><br>
     *      <tt>typeTerm    :String:项目期限（可选）</tt><br>
     *      <tt>incomeType  :String:结算方式（可选）</tt><br>
     *      <tt>enableStatus:String:状态（可选）</tt><br>
	 * @return Page
	 * @throws SLException 
	 */
	@Override
	public Page<Map<String, Object>> queryWealthTypeList(
			Map<String, Object> param) throws SLException {
		
		StringBuilder sqlString= new StringBuilder()
		.append("     SELECT wt.ID              \"wealthTypeId\", ")
		.append("            wt.LENDING_TYPE    \"lendingType\", ")
		.append("            wt.TYPE_TERM       \"typeTerm\", ")
		.append("            wt.YEAR_RATE * 100 \"yearRate\", ")
		.append("            wt.INCOME_TYPE     \"incomeType\", ")
		.append("            wt.ENABLE_STATUS   \"enableStatus\",  ")
		.append("            wt.SORT            \"sort\", ")
		.append("            wt.PRODUCT_NO      \"productNo\", ")
		.append("            wt.rebate_ratio * 100    \"rebateRatio\" ")
		.append("       FROM BAO_T_WEALTH_TYPE_INFO wt ")
		.append(" WHERE 1=1 ");
		
		SqlCondition sqlCondition = new SqlCondition(sqlString, param) 
		.addString("lendingType","wt.LENDING_TYPE")
		.addString("typeTerm", "wt.TYPE_TERM")
		.addString("incomeType", "wt.INCOME_TYPE")
		.addString("enableStatus", "wt.ENABLE_STATUS")
		.addSql(" ORDER BY decode(wt.ENABLE_STATUS, '有效', 1, '无效', 2), wt.PRODUCT_NO ");
		
		int start = Integer.parseInt(param.get("start").toString());
		int length = Integer.parseInt(param.get("length").toString());
		return repositoryUtil.queryForPageMap(sqlCondition.toString(), sqlCondition.toArray(), start, length);
	}
	
	/**
	 * 取得最新productNo
	 * @return List productNo
	 * @throws SLException 
	 */
	@Override
	public List<Map<String, Object>> getWealthTypeNewProductNo()
			throws SLException {
		
		SqlCondition sqlCon = new SqlCondition(new StringBuilder(), new HashMap<String, Object>())
		.addSql("  SELECT CASE WHEN length(PRODUCT_NO) < 2  ")
		.addSql("        THEN '0'||to_char(PRODUCT_NO)  ")
		.addSql("        ELSE to_char(PRODUCT_NO) END \"productNo\" ")
		.addSql("  FROM ( ")
		.addSql("  SELECT nvl(max(to_number(nvl(PRODUCT_NO,0))),0)+1 PRODUCT_NO  ")
		.addSql("    FROM BAO_T_WEALTH_TYPE_INFO ")
		.addSql("  ) t ");
		
		return repositoryUtil.queryForMap(sqlCon.toString(), sqlCon.toArray());
	}

	/**
	 * 基础产品-产品查看查询
	 * @param param
     *      <tt>wealthTypeId:String:计划类型主键</tt><br>
	 * @return List
	 * @throws SLException 
	 */
	@Override
	public List<Map<String, Object>> queryWealthTypeDetailById(
			Map<String, Object> param) throws SLException {
		
		StringBuffer sqlString= new StringBuffer()
				.append("  SELECT wt.ID              \"wealthTypeId\", ")
				.append("         wt.LENDING_TYPE    \"lendingType\", ")
				.append("         wt.TYPE_TERM       \"typeTerm\", ")
				.append("         wt.YEAR_RATE * 100 \"yearRate\", ")
				.append("         wt.INCOME_TYPE     \"incomeType\", ")
				.append("         wt.ENABLE_STATUS   \"enableStatus\", ")
				.append("         wt.SORT            \"sort\", ")
				.append("         wt.PRODUCT_NO      \"productNo\", ")
				.append("         wt.rebate_ratio * 100    \"rebateRatio\" ")
				.append("    FROM BAO_T_WEALTH_TYPE_INFO wt ")
				.append("   WHERE wt.ID = ? ");
		
		List<Object> objList=new ArrayList<Object>();
		objList.add(param.get("wealthTypeId"));
		return repositoryUtil.queryForMap(sqlString.toString(), objList.toArray());
	}

	/**
	 * 匹配规则-规则列表
	 * @param param
     *      <tt>start :String:起始值</tt><br>
     *      <tt>length:String:长度</tt><br>
	 * @return list
     * @throws SLException 
	 */
	@Override
	public List<Map<String, Object>> queryMatchRuleList(
			Map<String, Object> param) throws SLException {
		
		StringBuffer sqlString= new StringBuffer()
		.append(" SELECT r.ID \"matchRuleId\", r.INVEST_MINI_AMT \"investMiniAmt\", r.INVEST_MAX_AMT \"investMaxAmt\", r.DEBT_MINI_AMT \"debtMiniAmt\", r.DEBT_MAX_AMT \"debtMaxAmt\" ")
		.append("   FROM BAO_T_AUTO_MATCH_RULE r ")
		.append(" ORDER BY r.INVEST_MINI_AMT ");
		
		List<Object> objList=new ArrayList<Object>();
//		int start = Integer.parseInt(param.get("start").toString());
//		int length = Integer.parseInt(param.get("length").toString());
		return repositoryUtil.queryForMap(sqlString.toString(), objList.toArray());
	}

	/**
	 * 匹配规则-规则列表
	 * @param key String
	 * @param condision Object
	 * @return int
	 */
	@SuppressWarnings("deprecation")
	@Override
	public int checkParam(String wealthTypeId, String key, Object condision) {
		StringBuilder sql = new StringBuilder()
		.append(" SELECT count(1) \"count\" ")
		.append("   FROM BAO_T_WEALTH_TYPE_INFO ")
		.append("  WHERE "+key+" = ? ");
		if(!StringUtils.isEmpty(wealthTypeId)){
			sql.append("  and  ID != '"+wealthTypeId+"' ");
		}
		
		List<Object> objList=new ArrayList<Object>();
		objList.add(condision);
		return jdbcTemplate.queryForInt(sql.toString(), objList.toArray());
	}

}
