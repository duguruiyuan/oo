/** 
 * @(#)FixedInvestRepositoryCustomImpl.java 1.0.0 2015年8月15日 上午11:46:21  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository.custom.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.FixedInvestRepositoryCustom;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.PageFuns;
import com.slfinance.shanlincaifu.utils.SqlCondition;

/**   
 * 定期宝业务列表、统计、交易、投资记录接口实现
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年8月15日 上午11:46:21 $ 
 */
@Repository
public class FixedInvestRepositoryCustomImpl implements FixedInvestRepositoryCustom {


	@Autowired
	private RepositoryUtil repositoryUtil;
	
	/**定期宝分页列表查询返回字段**/
//	final String pageListColumn = " PRO.ID \"id\",PRO.PRODUCT_NAME \"productName\",PRO.INCOME_HANDLE_METHOD \"productType\",PRO.PRODUCT_DESC \"productDesc\",PRO.INVEST_MIN_AMOUNT \"investMinAmount\", PRO.INCREASE_AMOUNT\"increaseAmount\",RA.YEAR_RATE \"yearRate\",RA.AWARD_RATE \"awardRate\",PRO.TYPE_TERM \"typeTerm\",DET.CURR_USABLE_VALUE \"useableAmount\", TY.TYPE_NAME \"productCat\" ";
	
	/**定期宝分页列表查询返回字段**/
	final String pageListColumn = " PRO.ID \"id\",PRO.PRODUCT_NAME \"productName\",PRO.INCOME_HANDLE_METHOD \"productType\",PRO.PRODUCT_DESC \"productDesc\",PRO.INVEST_MIN_AMOUNT \"investMinAmount\", PRO.INCREASE_AMOUNT \"increaseAmount\", (SELECT MIN(RA.YEAR_RATE) FROM BAO_T_PRODUCT_RATE_INFO RA WHERE RA.product_id = PRO.id) \"yearRate\", (SELECT MIN(RA.AWARD_RATE) FROM BAO_T_PRODUCT_RATE_INFO RA WHERE RA.product_id = PRO.id) \"awardRate\", PRO.TYPE_TERM \"typeTerm\", DET.CURR_USABLE_VALUE \"useableAmount\", TY.TYPE_NAME \"productCat\" ";
	
	/**产品当天已投资金额**/
	final String investAmountCurr = " (SELECT  NVL(SUM(NVL(T.INVEST_AMOUNT,0)),0) FROM BAO_T_INVEST_INFO T WHERE T.RECORD_STATUS = '有效' AND  T.PRODUCT_ID =PRO.ID AND TRUNC(T.CREATE_DATE) = TRUNC(?) ) ";
	
	/**产品当天已投资总金额**/
	final String investTotalAmount = " ("+investAmountCurr+" + DET.CURR_USABLE_VALUE ) ";
	
	/**产品已投资比例返回字段**/
//	final String productInvestedScale = "DECODE(DET.CURR_USABLE_VALUE,0,1,TRUNC("+investAmountCurr+"/"+investTotalAmount+",4)) \"investedScale\"";
	
	/**产品已投资比例返回字段**/
	final String productInvestedScale = "CASE WHEN DET.CURR_USABLE_VALUE < PRO.INVEST_MIN_AMOUNT THEN 1 ELSE TRUNC(("+investAmountCurr+"/"+investTotalAmount+"),4) END \"investedScale\"";
	
	/**定期宝分页列表查询表关联**/
//	final String pageListTable = " FROM BAO_T_PRODUCT_INFO PRO ,BAO_T_PRODUCT_DETAIL_INFO DET,BAO_T_PRODUCT_RATE_INFO RA,BAO_T_PRODUCT_TYPE_INFO TY";

	/**定期宝分页列表查询表关联**/
	final String pageListTable = " FROM BAO_T_PRODUCT_INFO PRO, BAO_T_PRODUCT_DETAIL_INFO DET, BAO_T_PRODUCT_TYPE_INFO TY";
	
	/**定期宝分页列表查询排序**/
	final String pageListOrder = "  ORDER BY DECODE(TY.TYPE_NAME,'活期宝','PRO.ID','活期宝','PRO.ID'), PRO.TYPE_TERM ASC, PRO.CREATE_DATE DESC ";
	
	/**定期宝分页列表查询按照欢迎程度排序**/
	final String pageByFavoListOrder = "  ORDER BY  RA.Year_Rate DESC ";
	
	/**
	 * 定期宝分页列表查询
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Page<Map<String, Object>> getFixedInvestListPage(Map<String, Object> paramsMap) throws SLException {
		
		StringBuilder sql = new StringBuilder()
		.append(" SELECT PRO.ID \"id\", ")
		.append("        PRO.PRODUCT_NAME \"productName\", ")
		.append("        PRO.INCOME_HANDLE_METHOD \"productType\", ")
		.append("        PRO.PRODUCT_DESC \"productDesc\", ")
		.append("        PRO.INVEST_MIN_AMOUNT \"investMinAmount\", ")
		.append("        PRO.INCREASE_AMOUNT\"increaseAmount\", ")
		.append("        RA.YEAR_RATE \"yearRate\", ")
		.append("        RA.AWARD_RATE \"awardRate\", ")
		.append("        PRO.TYPE_TERM \"typeTerm\", ")
		.append("        DET.CURR_USABLE_VALUE \"useableAmount\", ")
		.append("        TY.TYPE_NAME \"productCat\" ")
		.append("   FROM BAO_T_PRODUCT_INFO        PRO, ")
		.append("        BAO_T_PRODUCT_DETAIL_INFO DET, ")
		.append("        BAO_T_PRODUCT_RATE_INFO   RA, ")
		.append("        BAO_T_PRODUCT_TYPE_INFO   TY ")
		.append("  WHERE PRO.ID = DET.ID ")
		.append("    AND TY.ID = PRO.PRODUCT_TYPE ")
		.append("    AND RA.PRODUCT_ID = PRO.ID ")
		.append("    AND exists (SELECT 1 ")
		.append("                FROM BAO_T_PRODUCT_RATE_INFO ")
		.append("                where product_id = PRO.id ")
		.append("                group by product_id ")
		.append("                having MIN(YEAR_RATE) = RA.YEAR_RATE) ")
		.append("    AND RA.LOWER_LIMIT_DAY = 0 ")
		.append("    AND PRO.ENABLE_STATUS = '启用' ")
		.append("    %s ")
		.append("order by decode(TY.type_name, '活期宝', '1', '定期宝', '2', '体验宝', '3') asc , to_number(pro.favorite_sort) asc ");
		
		StringBuffer whereSqlString = new StringBuffer();
		List<Object> objList = new ArrayList<Object>();
		
		if(!org.springframework.util.StringUtils.isEmpty(paramsMap.get("proId"))){
			whereSqlString.append(" AND PRO.ID = ? ");
			objList.add(paramsMap.get("proId"));
		}
		
		if (org.springframework.util.StringUtils.isEmpty(paramsMap.get("productList"))) {
			paramsMap.put("productList", Arrays.asList(Constant.PRODUCT_TYPE_04));
		}
		
		List<String> productList = (List<String>) paramsMap.get("productList");
		whereSqlString.append(" and ( ");
		for (int i = 0; i < productList.size(); i++) {
			if (i == 0) {
				whereSqlString.append(" TY.TYPE_NAME = ? ");
			} else {
				whereSqlString.append(" OR TY.TYPE_NAME = ? ");
			}
			objList.add(productList.get(i));
		}
		whereSqlString.append(" ) ");
		
		return repositoryUtil.queryForPageMap(String.format(sql.toString(), whereSqlString.toString()), objList.toArray(), new Integer(paramsMap.get("pageNum").toString()), new Integer(paramsMap.get("pageSize").toString()));
		
//		List<Object> objList = new ArrayList<Object>();
//		String columnSql = pageListColumn;
//		if(StringUtils.isNotEmpty((String)paramsMap.get("proSca"))) {
//			columnSql +=","+(String)paramsMap.get("proSca");
//			objList.add(DateTime.now().toDate());
//			objList.add(DateTime.now().toDate());
//		} 
//		StringBuilder sql = new StringBuilder(" SELECT ").append(columnSql).append(pageListTable);
////		StringBuilder condition = new StringBuilder(" WHERE PRO.ID = DET.ID AND TY.ID = PRO.PRODUCT_TYPE AND  RA.PRODUCT_ID = PRO.ID AND RA.YEAR_RATE = (SELECT MIN(YEAR_RATE)FROM BAO_T_PRODUCT_RATE_INFO where product_id = PRO.id) AND PRO.ENABLE_STATUS = '启用' ");
//		StringBuilder condition = new StringBuilder(" WHERE PRO.ID = DET.ID AND TY.ID = PRO.PRODUCT_TYPE AND PRO.ENABLE_STATUS = '启用' ");
//		
//		PageFuns.buildWhereSql(condition).append(" TY.TYPE_NAME IN ").append(PageFuns.buildWhereInParams(paramsMap.get("productList") != null ? (List<String>)paramsMap.get("productList") : Arrays.asList(Constant.PRODUCT_TYPE_04),objList));
//		if(!org.springframework.util.StringUtils.isEmpty(paramsMap.get("proId"))){
//			condition.append(" AND PRO.ID=").append(paramsMap.get("proId")).append(" ");
//		}
//		sql.append(condition).append(paramsMap.get("favoOrder") != null ?  paramsMap.get("favoOrder") : pageListOrder);
//	
//		return repositoryUtil.queryForPageMap(sql.toString(),objList.toArray(), new Integer(paramsMap.get("pageNum").toString()), new Integer(paramsMap.get("pageSize").toString()));
	}
	
	/**
	 * 首页列表查询
	 * @author gaoll
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Page<Map<String, Object>> getFixedProductsListPage(Map<String, Object> paramsMap) throws SLException {
		StringBuilder sql = new StringBuilder().append("select pro.id \"id\", "
				+ "pro_type.type_name \"productType\", "
				+ "pro.product_name \"productName\", "
				+ "pro.product_desc \"productDesc\", "
//				+ "(select min(year_rate) "
//				+ "from bao_t_product_rate_info t "
//				+ "where t.product_id = pro.id) \"yearRate\", "
//				+ "(select min(award_rate) "
//				+ "from bao_t_product_rate_info t "
//				+ "where t.product_id = pro.id) \"awardRate\", "
				+ "ra.year_rate \"yearRate\","
			    + "ra.award_rate \"awardRate\","
				+ "case pro.id "
				+ "when '1' then "
				+ "'随进随出' "
				+ "when '2' then "
				+ "'10天' "
				+ "else "
				+ "to_char(type_term) "
				+ "end \"typeTerm\", "
				+ "pro_detail.curr_usable_value \"useableAmount\", "
				+ "case "
				+ "when pro_detail.curr_usable_value < pro.invest_min_amount then "
				+ "1 "
				+ "else "
				+ "trunc(((select NVL(sum(p.INVEST_AMOUNT), 0) "
				+ "from BAO_T_INVEST_INFO p "
				+ "where p.CREATE_DATE >= ? "
				+ "AND p.CREATE_DATE < ? "
				+ "AND p.Invest_Status IN ('有效', '收益中') "
				+ "AND p.product_id = pro.id) / "
				+ "((select NVL(sum(p.INVEST_AMOUNT), 0) "
				+ "from BAO_T_INVEST_INFO p "
				+ "where p.CREATE_DATE >= ? "
				+ "AND p.CREATE_DATE < ? "
				+ "AND p.Invest_Status IN ('有效', '收益中') "
				+ "AND p.product_id = pro.id) + "
				+ "pro_detail.curr_usable_value)), "
				+ "4) "
				+ "end \"investedScale\" "
				+ "from bao_t_product_info pro, bao_t_product_detail_info pro_detail, bao_t_product_type_info pro_type, bao_t_product_rate_info ra "
				+ "where pro.id = pro_detail.product_id and pro.product_type = pro_type.id and pro.id = ra.product_id and pro.enable_status = '启用' "
				+ "and exists (select 1 "
				+ "            from bao_t_product_rate_info "
	            + "            where product_id = pro.id "
	            + "            group by product_id "
	            + "            having min(year_rate) = ra.year_rate) "
	            + "and ra.lower_limit_day = 0 "
	            + " %s "
				+ "order by decode(pro_type.type_name, '活期宝', '1', '定期宝', '2', '体验宝', '3') asc , to_number(pro.favorite_sort) asc ");
		
		StringBuffer whereSqlString = new StringBuffer();
		List<Object> objList = new ArrayList<Object>();
		objList.add(DateUtils.getStartDate(new Date()));
		objList.add(DateUtils.getNextDate(new Date()));
		objList.add(DateUtils.getStartDate(new Date()));
		objList.add(DateUtils.getNextDate(new Date()));
		
		List<String> productList = (List<String>) paramsMap.get("productList");
		whereSqlString.append(" and ( ");
		for (int i = 0; i < productList.size(); i++) {
			if (i == 0) {
				whereSqlString.append(" pro_type.TYPE_NAME = ? ");
			} else {
				whereSqlString.append(" OR pro_type.TYPE_NAME = ? ");
			}
			objList.add(productList.get(i));
		}
		whereSqlString.append(" ) ");
		
		return repositoryUtil.queryForPageMap(String.format(sql.toString(), whereSqlString.toString()), objList.toArray(), (Integer)paramsMap.get("pageNum"), (Integer)paramsMap.get("pageSize"));
	}

	/**
	 * 定期宝分页列表查询--按照欢迎程序
	 */
	@Override
	public Page<Map<String, Object>> getInvestListByFavoPage(Map<String,Object> paramsMap) throws SLException{
		
		StringBuilder sql = new StringBuilder()
		.append(" SELECT PRO.ID \"id\", ")
		.append("        PRO.PRODUCT_NAME \"productName\", ")
		.append("        PRO.INCOME_HANDLE_METHOD \"productType\", ")
		.append("        PRO.PRODUCT_DESC \"productDesc\", ")
		.append("        PRO.INVEST_MIN_AMOUNT \"investMinAmount\", ")
		.append("        PRO.INCREASE_AMOUNT\"increaseAmount\", ")
		.append("        RA.YEAR_RATE \"yearRate\", ")
		.append("        RA.AWARD_RATE \"awardRate\", ")
		.append("        PRO.TYPE_TERM \"typeTerm\", ")
		.append("        DET.CURR_USABLE_VALUE \"useableAmount\", ")
		.append("        TY.TYPE_NAME \"productCat\" ")
		.append("   FROM BAO_T_PRODUCT_INFO        PRO, ")
		.append("        BAO_T_PRODUCT_DETAIL_INFO DET, ")
		.append("        BAO_T_PRODUCT_RATE_INFO   RA, ")
		.append("        BAO_T_PRODUCT_TYPE_INFO   TY ")
		.append("  WHERE PRO.ID = DET.ID ")
		.append("    AND TY.ID = PRO.PRODUCT_TYPE ")
		.append("    AND RA.PRODUCT_ID = PRO.ID ")
		.append("    AND RA.LOWER_LIMIT_DAY = 0 ")
		.append("    AND PRO.ENABLE_STATUS = '启用' ")
		.append("    AND TY.TYPE_NAME = '定期宝' ")
		.append("  ORDER BY RA.YEAR_RATE DESC ");
		
		return repositoryUtil.queryForPageMap(sql.toString(), null, new Integer(paramsMap.get("pageNum").toString()), new Integer(paramsMap.get("pageSize").toString()));
		
		//paramsMap.put("favoOrder", pageByFavoListOrder);
		//return getFixedInvestListPage(paramsMap);
	}
	
	/**
	 * 定期宝分页列表查询--手机端多返回已投资比例
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Page<Map<String, Object>> getInvestPage(Map<String,Object> paramsMap) throws SLException{
		
		StringBuilder sql = new StringBuilder()
		.append(" SELECT PRO.ID \"id\", ")
		.append("        PRO.PRODUCT_NAME \"productName\", ")
		.append("        PRO.INCOME_HANDLE_METHOD \"productType\", ")
		.append("        PRO.PRODUCT_DESC \"productDesc\", ")
		.append("        PRO.INVEST_MIN_AMOUNT \"investMinAmount\", ")
		.append("        PRO.INCREASE_AMOUNT\"increaseAmount\", ")
		.append("        RA.YEAR_RATE \"yearRate\", ")
		.append("        RA.AWARD_RATE \"awardRate\", ")
		.append("        PRO.TYPE_TERM \"typeTerm\", ")
		.append("        DET.CURR_USABLE_VALUE \"useableAmount\", ")
		.append("        TY.TYPE_NAME \"productCat\", ")
		.append("        case  ")
		.append("          when DET.CURR_USABLE_VALUE < PRO.INVEST_MIN_AMOUNT ")
		.append("          then 1 ")
		.append("          else ")
		.append("               TRUNC((SELECT NVL(SUM(NVL(T.INVEST_AMOUNT, 0)), 0) ")
		.append("                        FROM BAO_T_INVEST_INFO T ")
		.append("                       WHERE T.RECORD_STATUS IN ('有效', '收益中') ")
		.append("                         AND T.PRODUCT_ID = PRO.ID ")
		.append("                         AND T.CREATE_DATE >= ? AND T.CREATE_DATE < ?) / ")
		.append("                     ((SELECT NVL(SUM(NVL(T.INVEST_AMOUNT, 0)), 0) ")
		.append("                         FROM BAO_T_INVEST_INFO T ")
		.append("                        WHERE T.RECORD_STATUS IN ('有效', '收益中') ")
		.append("                          AND T.PRODUCT_ID = PRO.ID ")
		.append("                          AND TRUNC(T.CREATE_DATE) >= ? AND T.CREATE_DATE < ? ) + ")
		.append("                     DET.CURR_USABLE_VALUE),4)  ")
		.append("          end \"investedScale\", ")
		//
		.append("		 case  ")
		.append("		 when (to_number(to_char( ? , 'HH24')) >= 8 and ")
		.append("		 to_number(to_char( ? , 'HH24')) < 24) then ")
		.append("		  	'true' ")
		.append(" 		 else ")
		.append("		  	'false' ")
		.append(" 		 end \"isSale\", ")
		.append("		 case  ")
		.append("		 when (to_number(to_char( ? , 'HH24')) >= 8 and ")
		.append("		 to_number(to_char( ? , 'HH24')) < 24) then ")
		.append("		  	'抢购' ")
		.append(" 		 else ")
		.append("		  	'8点开抢' ")
		.append(" 		 end \"whenPurchase\" ")
		//
		.append("   FROM BAO_T_PRODUCT_INFO        PRO, ")
		.append("        BAO_T_PRODUCT_DETAIL_INFO DET, ")
		.append("        BAO_T_PRODUCT_RATE_INFO   RA, ")
		.append("        BAO_T_PRODUCT_TYPE_INFO   TY ")
		.append("  WHERE PRO.ID = DET.ID ")
		.append("    AND TY.ID = PRO.PRODUCT_TYPE ")
		.append("    AND RA.PRODUCT_ID = PRO.ID ")
		.append("    AND exists (SELECT 1 ")
		.append("                FROM BAO_T_PRODUCT_RATE_INFO ")
		.append("                where product_id = PRO.id ")
		.append("                group by product_id ")
		.append("                having MIN(YEAR_RATE) = RA.YEAR_RATE) ")
		.append("    AND RA.LOWER_LIMIT_DAY = 0 ")
		.append("    AND PRO.ENABLE_STATUS = '启用' ")
		.append("    %s ")
		.append("order by decode(TY.type_name, '活期宝', '1', '定期宝', '2', '体验宝', '3') asc , to_number(pro.favorite_sort) asc ");
		
		StringBuffer whereSqlString = new StringBuffer();
		List<Object> objList = new ArrayList<Object>();
		objList.add(DateUtils.getStartDate(new Date()));
		objList.add(DateUtils.getNextDate(new Date()));
		objList.add(DateUtils.getStartDate(new Date()));
		objList.add(DateUtils.getNextDate(new Date()));
		objList.add(new Date());
		objList.add(new Date());
		objList.add(new Date());
		objList.add(new Date());
		
		if (org.springframework.util.StringUtils.isEmpty(paramsMap.get("productList")) 
				&& StringUtils.isEmpty((String)paramsMap.get("typeName")) ) {
			paramsMap.put("productList", Arrays.asList(Constant.PRODUCT_TYPE_04));
		}
		
		if (!org.springframework.util.StringUtils.isEmpty(paramsMap.get("productList"))) {
			List<String> productList = (List<String>) paramsMap.get("productList");
			whereSqlString.append(" and ( ");
			for (int i = 0; i < productList.size(); i++) {
				if (i == 0) {
					whereSqlString.append(" TY.TYPE_NAME = ? ");
				} else {
					whereSqlString.append(" OR TY.TYPE_NAME = ? ");
				}
				objList.add(productList.get(i));
			}
			whereSqlString.append(" ) ");
		}
		
		if(!StringUtils.isEmpty((String)paramsMap.get("proId"))) {
			whereSqlString.append(" and PRO.ID = ? ");
			objList.add((String)paramsMap.get("proId"));
		}
		
		if(!StringUtils.isEmpty((String)paramsMap.get("typeName"))) {
			whereSqlString.append(" and TY.TYPE_NAME = ? ");
			objList.add((String)paramsMap.get("typeName"));
		}
		
		return repositoryUtil.queryForPageMap(String.format(sql.toString(), whereSqlString.toString()), objList.toArray(), new Integer(paramsMap.get("pageNum").toString()), new Integer(paramsMap.get("pageSize").toString()));
//		paramsMap.put("proSca", productInvestedScale);
//		return getFixedInvestListPage(paramsMap);
	}
	
	/**
	 * 根据产品id查询该产品的投资记录
	 */
	@Override
	public Page<Map<String,Object>> findInvestList(Map<String, Object> paramsMap) throws SLException {
		StringBuffer sql = new StringBuffer(" select btci.LOGIN_NAME \"loginName\", btidi.INVEST_AMOUNT \"investAmount\",btii.INVEST_DATE \"createDate\", btidi.CREATE_DATE \"investDate\" ");
		sql.append(" from BAO_T_INVEST_DETAIL_INFO btidi,BAO_T_INVEST_INFO btii,BAO_T_CUST_INFO btci, bao_t_product_info t ");
		sql.append(" where btidi.invest_id=btii.id and btii.cust_id=btci.id  and btii.PRODUCT_ID = t.id and t.product_name in (select product_name from bao_t_product_info where id = ?)");
		sql.append(" order by btidi.CREATE_DATE desc ");
		return repositoryUtil.queryForPageMap(sql.toString(), new Object[]{paramsMap.get("id")}, (int)paramsMap.get("pageNum"), (int)paramsMap.get("pageSize"));
	}
	
	/**
	 * 根据产品类型查询该产品的投资记录
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Page<Map<String,Object>> findInvestListByProduct(Map<String, Object> paramsMap) throws SLException{
		List<Object> objList = new ArrayList<>();
		StringBuilder sql = new StringBuilder("select c.LOGIN_NAME \"loginName\",de.INVEST_AMOUNT \"investAmount\",de.CREATE_DATE \"createDate\",pi.type_name \"productCat\" from BAO_T_INVEST_DETAIL_INFO de,BAO_T_INVEST_INFO it, BAO_T_CUST_INFO c,BAO_T_PRODUCT_INFO p,BAO_T_PRODUCT_TYPE_INFO pi");
		StringBuilder contionSlq = new StringBuilder(" where de.invest_id = it.id and it.cust_id = c.id and it.PRODUCT_ID  = p.id and pi.id = p.product_type and pi.type_name in  ").append(PageFuns.buildWhereInParams((List<String>)paramsMap.get("productList"), objList));
		sql.append(contionSlq).append(" order by de.CREATE_DATE desc");
		return repositoryUtil.queryForPageMap(sql.toString(), objList.toArray(), (int)paramsMap.get("pageNum"), (int)paramsMap.get("pageSize"));
	}
	
	/**
	 * 获取产品类型的最大利率、最小利率、最大奖励利率、最小奖励利率
	 */
	public Map<String,Object> getProductRate(String typeName) throws SLException {
		StringBuilder sbsql = new StringBuilder("SELECT MIN(PR.YEAR_RATE) \"minYearRate\",MAX(PR.YEAR_RATE) \"maxYearRate\",MAX(PR.AWARD_RATE) \"maxAwardRate\",MIN(PR.AWARD_RATE) \"minAwardRate\" FROM BAO_T_PRODUCT_RATE_INFO PR, BAO_T_PRODUCT_INFO P,BAO_T_PRODUCT_TYPE_INFO PT WHERE PR.PRODUCT_ID = P.ID AND P.PRODUCT_TYPE = PT.ID AND PT.TYPE_NAME = ? ");
		List<Map<String,Object>> list = repositoryUtil.queryForMap(sbsql.toString(), new Object[]{typeName != null ? typeName : Constant.PRODUCT_TYPE_04});
		if(list != null && list.size() == 1 )
			return list.get(0);
		return Maps.newHashMap();
	}
	
	/**
	 * 获得产品的已投资金额
	 */
	public BigDecimal getInvestedAmount(List<String> typeNameList)throws SLException{
		StringBuilder sql = new StringBuilder(" select sum(pd.already_invest_amount) \"tradeMount\" from bao_t_product_detail_info pd,bao_t_product_info pi,bao_t_product_type_info pt ");
		List<Object> objList = new ArrayList<Object>();
		StringBuilder condition = new StringBuilder(" where pd.product_id = pi.id and pi.product_type = pt.id ");
		PageFuns.buildWhereSql(condition).append(" pt.type_name in ").append(PageFuns.buildWhereInParams(typeNameList != null ? typeNameList : Arrays.asList(Constant.PRODUCT_TYPE_04),objList));
		List<Map<String,Object>> list = repositoryUtil.queryForMap(sql.append(condition).toString(), objList.toArray());
		if(list != null && list.size() ==1 && list.get(0) != null )
			return (BigDecimal)((Map<String,Object> )list.get(0)).get("tradeMount");
		return BigDecimal.ZERO;
	}
	
	/**
	 * 获取产品的预计收益
	 */
	public BigDecimal getPreIncomeAllProduct(List<String> typeNameList,List<String> investStatus,String custId,Date date)throws SLException{
		StringBuilder sql = new StringBuilder(" select sum (case it.invest_status  when ? then it.invest_amount * (pri.year_rate) * ( trunc(ao.create_date)- trunc(to_date(it.invest_date,'yyyyMMdd')) ) / 365 when ? then  it.invest_amount * (pri.year_rate + pri.award_rate) *pi.type_term / 12 when ? then it.invest_amount * (pri.year_rate + pri.award_rate) *pi.type_term / 12 else 0 end ) tradeMount ");
		sql.append("   from bao_t_product_info pi, bao_t_product_rate_info pri,bao_t_product_type_info pti,bao_t_invest_info it left join bao_t_atone_info ao on ao.invest_id = it.id ");
		List<Object> objList = Lists.newArrayList();
		objList.add(Constant.TERM_INVEST_STATUS_ADVANCE);
//		objList.add(DateUtils.formatDate(date, "yyyyMMdd"));
		objList.add(Constant.TERM_INVEST_STATUS_EARN);
		objList.add(Constant.TERM_INVEST_STATUS_WAIT);
		
		StringBuilder condition = new StringBuilder("  where it.product_id = pi.id and pri.product_id = pi.id and pti.id = pi.product_type ");
		PageFuns.buildWhereSql(condition).append(" pti.type_name in ").append(PageFuns.buildWhereInParams(typeNameList != null ? typeNameList : Arrays.asList(Constant.PRODUCT_TYPE_04),objList));
		PageFuns.buildWhereSql(condition).append(" it.invest_status in ").append(PageFuns.buildWhereInParams(investStatus != null ? investStatus : Arrays.asList(Constant.TERM_INVEST_STATUS_ADVANCE,Constant.TERM_INVEST_STATUS_EARN,Constant.TERM_INVEST_STATUS_WAIT),objList));
		if(StringUtils.isNotEmpty(custId)){
			PageFuns.buildWhereSql(condition).append(" it.cust_id = ? ");
			objList.add(custId);
		}
		
		List<Map<String,Object>> list = repositoryUtil.queryForMap(sql.append(condition).toString(), objList.toArray());
		if(list != null && list.size() ==1 && list.get(0) != null )
			return (BigDecimal)((Map<String,Object> )list.get(0)).get("tradeMount");
		return BigDecimal.ZERO;
	}

	/**
	 *查询客户某一产品、某一期的累计盈亏金额
	 */
	@Override
	public BigDecimal getAtonedAmount(String custId, List<String> typeNameList,List<String> investStatusList) {
		StringBuilder sql = new StringBuilder(" SELECT SUM(NVL(A.ATONE_TOTAL_AMOUNT,0) - NVL(T.INVEST_AMOUNT,0) + NVL(A.ATONE_EXPENSES,0)) \"amount\" FROM BAO_T_INVEST_INFO T, BAO_T_PRODUCT_INFO P, BAO_T_PRODUCT_TYPE_INFO PT, BAO_T_ATONE_INFO A ");
		
		List<Object> objList = new ArrayList<Object>();
		StringBuilder condition = new StringBuilder(" WHERE T.PRODUCT_ID = P.ID AND P.PRODUCT_TYPE = PT.ID AND A.INVEST_ID = T.ID ");
		PageFuns.buildWhereSql(condition).append("T.CUST_ID = ? ");
		objList.add(custId);
		
		PageFuns.buildWhereSql(condition).append(" PT.TYPE_NAME IN ").append(PageFuns.buildWhereInParams(typeNameList != null ? typeNameList : Arrays.asList(Constant.PRODUCT_TYPE_04),objList));
		PageFuns.buildWhereSql(condition).append(" T.INVEST_STATUS IN ").append(PageFuns.buildWhereInParams(investStatusList != null ? investStatusList : Arrays.asList(Constant.TERM_INVEST_STATUS_FINISH,Constant.TERM_INVEST_STATUS_ADVANCE_FINISH),objList));
		
		List<Map<String,Object>> list = repositoryUtil.queryForMap(sql.append(condition).toString(), objList.toArray());
		if(list != null && list.size() ==1 && list.get(0) != null )
			return (BigDecimal)((Map<String,Object> )list.get(0)).get("amount");
		return BigDecimal.ZERO;
	}

	@Override
	public List<Map<String, Object>> getPriorProductList(
			Map<String, Object> paramsMap) throws SLException {
		StringBuilder sql = new StringBuilder()
		.append("  select  ")
		.append("   PROCUCT_ID \"procuctId\",  ")
		.append("   PRODUCT_NAME \"procuctName\",  ")
		.append("   CURR_TERM \"currentTerm\", ")
		.append("   TYPE_NAME \"procuctType\",  ")
		.append("   PRODUCT_DESC \"productDesc\",  ")
		.append("   INVEST_MIN_AMOUNT \"investMinAmount\",  ")
		.append("   INCREASE_AMOUNT\"increaseAmount\",  ")
		.append("   YEAR_RATE \"minYearRate\",  ")
		.append("   YEAR_RATE \"yearRate\",  ")
		.append("   AWARD_RATE \"awardRate\",  ")
		.append("   TYPE_TERM \"typeTerm\", ")
		.append("   INVEST_BEARINTE_METHOD \"investBearinteMethod\", ")
		.append("   INCOME_HANDLE_METHOD \"incomeHandleMethod\", ")
		.append("   CURR_USABLE_VALUE \"useableAmount\",  ")
		.append("   TYPE_NAME \"productCat\", ")
		.append("   ALREADY_INVEST_AMOUNT \"alreadyInvestAmount\", ")
		.append("   ALREADY_INVEST_AMOUNT + CURR_USABLE_VALUE \"planTotalAmount\", ")
		.append("   case   ")
		.append("     when CURR_USABLE_VALUE < INVEST_MIN_AMOUNT  ")
		.append("     then 1  ")
		.append("     else TRUNC(ALREADY_INVEST_AMOUNT/(ALREADY_INVEST_AMOUNT+CURR_USABLE_VALUE), 4) ")
		.append("    end \"investedScale\", ")
		.append("    IS_SALE \"isSale\", ")
		.append("    WHEN_PURCHASE \"whenPurchase\" ")
		.append(" from (     ")
		.append("  SELECT PRO.ID PROCUCT_ID,  ")
		.append("             PRO.PRODUCT_NAME,  ")
		.append("             DET.CURR_TERM, ")
		.append("             PRO.PRODUCT_DESC,  ")
		.append("             PRO.INVEST_MIN_AMOUNT,  ")
		.append("             PRO.INCREASE_AMOUNT,  ")
		.append("             RA.YEAR_RATE,  ")
		.append("             RA.AWARD_RATE,  ")
		.append("             PRO.TYPE_TERM, ")
		.append("             PRO.INVEST_BEARINTE_METHOD, ")
		.append("             decode(TY.TYPE_NAME, '定期宝', '投资无上限', TO_CHAR(PRO.INVEST_MAX_AMOUNT)) INCOME_HANDLE_METHOD, ")
		.append("             DET.CURR_USABLE_VALUE,  ")
		.append("             TY.TYPE_NAME, ")
		.append("             case TY.TYPE_NAME ")
		.append("             when '定期宝' then ")
		.append("                (select NVL(sum(A.invest_Amount),0)  ")
		.append("                from bao_t_invest_info A  ")
		.append("                where A.invest_Status in ('收益中','有效')  ")
		.append("                and A.product_Id = PRO.ID  ")
		.append("                and invest_Date = ?  ")
		.append("                and A.CURR_TERM = DET.CURR_TERM) ")
		.append("              else  ")
		.append("                (SELECT NVL(SUM(NVL(T.INVEST_AMOUNT, 0)), 0)  ")
		.append("                             FROM BAO_T_INVEST_INFO T  ")
		.append("                            WHERE T.RECORD_STATUS IN ('有效', '收益中')  ")
		.append("                              AND T.PRODUCT_ID = PRO.ID  ")
		.append("                              AND T.CREATE_DATE >= ? AND T.CREATE_DATE < ?) ")
		.append("             end ALREADY_INVEST_AMOUNT, ")
		.append("          case   ")
		.append("          when (to_number(to_char( ? , 'HH24')) >= 8 and  ")
		.append("          to_number(to_char( ? , 'HH24')) < 24) then  ")
		.append("             'true'  ")
		.append("           else  ")
		.append("             'false'  ")
		.append("           end IS_SALE,  ")
		.append("          case   ")
		.append("          when (to_number(to_char( ? , 'HH24')) >= 8 and  ")
		.append("          to_number(to_char( ? , 'HH24')) < 24) then  ")
		.append("             '抢购'  ")
		.append("           else  ")
		.append("             '8点开抢'  ")
		.append("           end WHEN_PURCHASE  ")
		.append("        FROM BAO_T_PRODUCT_INFO        PRO,  ")
		.append("             BAO_T_PRODUCT_DETAIL_INFO DET,  ")
		.append("             BAO_T_PRODUCT_RATE_INFO   RA,  ")
		.append("             BAO_T_PRODUCT_TYPE_INFO   TY  ")
		.append("       WHERE PRO.ID = DET.ID  ")
		.append("         AND TY.ID = PRO.PRODUCT_TYPE  ")
		.append("         AND RA.PRODUCT_ID = PRO.ID  ")
		.append("         AND exists (SELECT 1  ")
		.append("                     FROM BAO_T_PRODUCT_RATE_INFO  ")
		.append("                     where product_id = PRO.id  ")
		.append("                     group by product_id  ")
		.append("                     having MIN(YEAR_RATE) = RA.YEAR_RATE)  ")
		.append("         AND RA.LOWER_LIMIT_DAY = 0  ")
		.append("         AND PRO.ENABLE_STATUS = '启用' ")
		.append("         and PRO.ID IN ( ")
		.append("             select id from( ")
		.append("             select t.id, rownum from bao_t_product_info t, bao_t_product_detail_info s, bao_t_product_type_info m ")
		.append("             where t.id = s.product_id and m.id = t.product_type and m.type_name = '定期宝' and t.enable_status = '启用' ")
		.append("             order by  ")
		.append("             case when t.product_name = '单季盈' then case when s.curr_usable_value >= t.invest_min_amount then 1 else 11 end ")
		.append("                  when t.product_name = '全年盈' then case when s.curr_usable_value >= t.invest_min_amount then 2 else 12 end ")
		.append("                  when t.product_name = '双季盈' then case when s.curr_usable_value >= t.invest_min_amount then 3 else 13 end ")
		.append("                  else case when s.curr_usable_value >= t.invest_min_amount then 4 else 14 end ")
		.append("             end ")
		.append("             ) where rownum = 1 ")
		.append("         ) ")
		.append(" )     ");
		
		StringBuffer whereSqlString = new StringBuffer();
		List<Object> objList = new ArrayList<Object>();
		objList.add(DateUtils.formatDate(new Date(), "yyyyMMdd"));
		objList.add(DateUtils.getStartDate(new Date()));
		objList.add(DateUtils.getNextDate(new Date()));
		objList.add(new Date());
		objList.add(new Date());
		objList.add(new Date());
		objList.add(new Date());
				
		return repositoryUtil.queryForMap(String.format(sql.toString(), whereSqlString.toString()), objList.toArray());
	}

	@Override
	public List<Map<String, Object>> getProdctYearRateList(
			Map<String, Object> paramsMap) throws SLException {
		
		StringBuilder sql = new StringBuilder()
		.append(" select PRODUCT_NAME \"productName\",  ")
		.append("        YEAR_RATE \"minYearRate\",  ")
		.append("        AWARD_RATE \"awardRate\" ")
		.append(" FROM BAO_T_PRODUCT_INFO        PRO,  ")
		.append("     BAO_T_PRODUCT_DETAIL_INFO \"DET\",  ")
		.append("     BAO_T_PRODUCT_RATE_INFO   \"RA\",  ")
		.append("     BAO_T_PRODUCT_TYPE_INFO   TY  ")
		.append(" WHERE PRO.ID = DET.ID  ")
		.append(" AND TY.ID = PRO.PRODUCT_TYPE  ")
		.append(" AND RA.PRODUCT_ID = PRO.ID  ")
		.append(" AND exists (SELECT 1  ")
		.append("             FROM BAO_T_PRODUCT_RATE_INFO  ")
		.append("             where product_id = PRO.id  ")
		.append("             group by product_id  ")
		.append("             having MIN(YEAR_RATE) = RA.YEAR_RATE)  ")
		.append(" AND RA.LOWER_LIMIT_DAY = 0  ")
		.append(" AND PRO.ENABLE_STATUS = '启用' ");
		
		SqlCondition sqlCondition = new SqlCondition(sql, paramsMap);
		sqlCondition.addString("typeName", "TY.TYPE_NAME");
		
		return repositoryUtil.queryForMap(sqlCondition.toString(), sqlCondition.toArray());
	}
}
