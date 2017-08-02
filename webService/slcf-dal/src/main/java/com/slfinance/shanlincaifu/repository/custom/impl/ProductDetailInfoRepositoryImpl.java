/** 
 * @(#)ProductDetailInfoRepositoryImpl.java 1.0.0 2015年4月25日 下午3:31:05  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository.custom.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.ProductDetailInfoRepositoryCustom;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.SubjectConstant;

/**   
 * 
 *  
 * @author  HuYaHui
 * @version $Revision:1.0.0, $Date: 2015年4月25日 下午3:31:05 $ 
 */
@Repository
public class ProductDetailInfoRepositoryImpl implements ProductDetailInfoRepositoryCustom{
	@Autowired
	private RepositoryUtil repositoryUtil;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	public Map<String, Object> findDetailByCondition(Map<String,Object> params){
		//赎回详情
		String sql="select btai.TRADE_CODE \"tradeCode\",btci.LOGIN_NAME \"loginName\",btci.CUST_NAME \"custName\", btci.CREDENTIALS_CODE \"credentialsCode\",btci.CREDENTIALS_Type \"credentialsType\","+
		" btai.ATONE_METHOD \"atoneMethod\",btai.ATONE_TOTAL_AMOUNT \"atoneTotalAmount\","+
		" (select btli.oper_ipaddress from BAO_T_LOG_INFO btli where btli.relate_type='"+Constant.TABLE_BAO_T_ATONE_INFO+"' and btli.relate_primary=btai.id and rownum=1) \"IP\","+
		" btai.CREATE_DATE \"createDate\",btai.MEMO \"memo\""+
		" from BAO_T_ATONE_INFO btai,BAO_T_CUST_INFO btci"+
		" where btai.cust_id = btci.id "+
		" and btai.TRADE_CODE=?";
		return jdbcTemplate.queryForMap(sql, new Object[]{params.get("tradeCode")});
	}
	public BigDecimal countAtoneListByCondition(Map<String,Object> params){
		//赎回管理-列表
		StringBuffer sql=new StringBuffer();
		sql.append("select sum(btai.ATONE_TOTAL_AMOUNT)");
		sql.append(" from BAO_T_CUST_INFO btci,BAO_T_ATONE_INFO btai");
		sql.append(" where btai.CUST_ID = btci.id ");
		
		List<Object> objList=new ArrayList<>();
		
		if(!params.containsKey("typeName") || Constant.PRODUCT_TYPE_01.equals((String)params.get("typeName"))) { // 产品名称为空时默认为活期宝
			sql.append(String.format(" and btai.ATONE_METHOD in ('%s','%s') ", Constant.ATONE_METHOD_NORMAL, Constant.ATONE_METHOD_IMMEDIATE));
		}
		else {
			sql.append(String.format(" and btai.ATONE_METHOD in ('%s','%s') ", Constant.ATONE_METHOD_ADVANCE, Constant.ATONE_METHOD_EXPIRE));
		}
		
		//用户昵称
		Object loginName=params.get("loginName");
		if(!StringUtils.isEmpty(loginName)){
			sql.append(" and btci.LOGIN_NAME like ?");
			objList.add("%"+loginName+"%");
		}
		//交易编号
		Object tradeCode=params.get("tradeCode");
		if(!StringUtils.isEmpty(tradeCode)){
			sql.append(" and btai.TRADE_CODE=?");
			objList.add(tradeCode);
		}
		//赎回方式
		Object atoneMethod=params.get("atoneMethod");
		if(!StringUtils.isEmpty(atoneMethod)){
			sql.append(" and btai.ATONE_METHOD=?");
			objList.add(atoneMethod);
		}
		//交易状态
		Object tradeStatus=params.get("tradeStatus");
		if(!StringUtils.isEmpty(tradeStatus)){
			sql.append(" and btai.ATONE_STATUS=?");
			objList.add(tradeStatus);
		}
		//审核状态
		Object atoneStatus=params.get("atoneStatus");
		if(!StringUtils.isEmpty(atoneStatus)){
			sql.append(" and btai.AUDIT_STATUS=?");
			objList.add(atoneStatus);
		}
		//证件号码
		Object credentialsCode=params.get("credentialsCode");
		if(!StringUtils.isEmpty(credentialsCode)){
			sql.append(" and btci.CREDENTIALS_CODE=?");
			objList.add(credentialsCode);
		}
		//开始时间
		Object startDate=params.get("startDate");
		if(!StringUtils.isEmpty(startDate)){
			sql.append(" and trunc(btai.CREATE_DATE)>=to_date(?,'yyyy-MM-dd')");
			objList.add(startDate);
		}
		//结束时间
		Object endDate=params.get("endDate");
		if(!StringUtils.isEmpty(endDate)){
			sql.append(" and trunc(btai.CREATE_DATE)<=to_date(?,'yyyy-MM-dd')");
			objList.add(endDate);
		}
		return jdbcTemplate.queryForObject(sql.toString(), objList.toArray(), BigDecimal.class);
	}
	
	public Page<Map<String, Object>> findAtoneListByCondition(Map<String,Object> params){
		//赎回管理-列表
		StringBuffer sql=new StringBuffer();
		sql.append("select btai.id \"id\",btai.TRADE_CODE \"tradeCode\",btci.LOGIN_NAME \"loginName\",btci.CUST_NAME \"custName\",btci.CREDENTIALS_CODE \"credentialsCode\", btai.ATONE_METHOD \"atoneMethod\", btai.ATONE_TOTAL_AMOUNT \"atoneTotalAmount\", btai.ATONE_STATUS \"atoneStatus\", btai.CREATE_DATE \"createDate\", btai.AUDIT_STATUS \"auditStatus\" ");
		sql.append(" from BAO_T_ATONE_INFO  btai, BAO_T_CUST_INFO   btci");
		sql.append(" where btai.CUST_ID = btci.id ");		
		
		int pageNum=Integer.valueOf(params.get("start")+"");
		int pageSize=Integer.valueOf(params.get("length")+"");
		List<Object> objList=new ArrayList<>();
		
		if(!params.containsKey("typeName") || Constant.PRODUCT_TYPE_01.equals((String)params.get("typeName"))) { // 产品名称为空时默认为活期宝
			sql.append(String.format(" and btai.ATONE_METHOD in ('%s','%s') ", Constant.ATONE_METHOD_NORMAL, Constant.ATONE_METHOD_IMMEDIATE));
		}
		else {
			sql.append(String.format(" and btai.ATONE_METHOD in ('%s','%s') ", Constant.ATONE_METHOD_ADVANCE, Constant.ATONE_METHOD_EXPIRE));
		}
		
		//用户昵称
		Object loginName=params.get("loginName");
		if(!StringUtils.isEmpty(loginName)){
			sql.append(" and btci.LOGIN_NAME like ?");
			objList.add("%"+loginName+"%");
		}
		//交易编号
		Object tradeCode=params.get("tradeCode");
		if(!StringUtils.isEmpty(tradeCode)){
			sql.append(" and btai.TRADE_CODE=?");
			objList.add(tradeCode);
		}
		//赎回方式
		Object atoneMethod=params.get("atoneMethod");
		if(!StringUtils.isEmpty(atoneMethod)){
			sql.append(" and btai.ATONE_METHOD=?");
			objList.add(atoneMethod);
		}
		//交易状态
		Object tradeStatus=params.get("tradeStatus");
		if(!StringUtils.isEmpty(tradeStatus)){
			sql.append(" and btai.ATONE_STATUS=?");
			objList.add(tradeStatus);
		}
		//审核状态
		Object atoneStatus=params.get("atoneStatus");
		if(!StringUtils.isEmpty(atoneStatus)){
			sql.append(" and btai.AUDIT_STATUS=?");
			objList.add(atoneStatus);
		}
		//证件号码
		Object credentialsCode=params.get("credentialsCode");
		if(!StringUtils.isEmpty(credentialsCode)){
			sql.append(" and btci.CREDENTIALS_CODE=?");
			objList.add(credentialsCode);
		}
		//开始时间
		Object startDate=params.get("startDate");
		if(!StringUtils.isEmpty(startDate)){
			sql.append(" and trunc(btai.CREATE_DATE)>=to_date(?,'yyyy-MM-dd')");
			objList.add(startDate);
		}
		//结束时间
		Object endDate=params.get("endDate");
		if(!StringUtils.isEmpty(endDate)){
			sql.append(" and trunc(btai.CREATE_DATE)<=to_date(?,'yyyy-MM-dd')");
			objList.add(endDate);
		}
		
		
		sql.append(" order by btai.ATONE_STATUS desc, btai.create_date desc");
		return repositoryUtil.queryForPageMap(sql.toString(), objList.toArray(),pageNum,pageSize);
	}
	
	public BigDecimal countInvestByCondition(Map<String,Object> paramMap){
		String productName="";
		StringBuffer sql=new StringBuffer("select sum(a.A1) from("+
				"		 SELECT "+
				"		       SUM(NVL(SUBACCOUNT.ACCOUNT_TOTAL_VALUE,0))  a1"+
				"		    FROM BAO_T_CUST_INFO CUST,BAO_T_INVEST_INFO INVEST,BAO_T_SUB_ACCOUNT_INFO SUBACCOUNT,BAO_T_PRODUCT_INFO PRODUCT, BAO_T_PRODUCT_TYPE_INFO PRODUCTTYPE"+ 
				"		       WHERE   CUST.ID =INVEST.CUST_ID"+
				"		         AND   INVEST.ID =SUBACCOUNT.RELATE_PRIMARY"+
				"		         AND   INVEST.PRODUCT_ID = PRODUCT.ID "+
				"		         AND   PRODUCT.PRODUCT_TYPE = PRODUCTTYPE.ID"+
				"		         AND SUBACCOUNT.RECORD_STATUS = '有效' "+
				"		         AND PRODUCTTYPE.TYPE_NAME = ?");
		if(StringUtils.isEmpty(paramMap.get("productName"))){
			productName=Constant.PRODUCT_TYPE_01;
		}else{
			productName=paramMap.get("productName")+"";
		}
		List<Object> objList=new ArrayList<>(); 
		objList.add(productName);
		//用户昵称：
		Object loginName=paramMap.get("loginName");
		if(!StringUtils.isEmpty(loginName)){
			sql.append(" and CUST.login_name like ?");
			objList.add("%"+loginName+"%");
		}
		//真实姓名：
		Object custName=paramMap.get("custName");
		if(!StringUtils.isEmpty(custName)){
			sql.append(" and CUST.cust_name=?");
			objList.add(custName);
		}
		//证件号码：
		Object credentialsCode=paramMap.get("credentialsCode");
		if(!StringUtils.isEmpty(credentialsCode)){
			sql.append(" and CUST.credentials_code=?");
			objList.add(credentialsCode);
		}
		sql.append("		      GROUP BY CUST.ID"+
				"		) a");
		return jdbcTemplate.queryForObject(sql.toString(), objList.toArray(), BigDecimal.class);
	}
	
	@Override
	public Page<Map<String,Object>> findByCondition(Map<String, Object> paramMap) {
		//1.1	列表
		int start=Integer.valueOf(paramMap.get("start")+"");
		int pageSize=Integer.valueOf(paramMap.get("length")+"");
		String productName="";
		if(StringUtils.isEmpty(paramMap.get("productName"))){
			productName=Constant.PRODUCT_TYPE_01;
		}else{
			productName=paramMap.get("productName")+"";
		}
		
		String tradeType="";
		if(StringUtils.isEmpty(paramMap.get("tradeType"))){
			tradeType=SubjectConstant.TRADE_FLOW_TYPE_01;
		}else{
			tradeType=paramMap.get("tradeType")+"";
		}
		
		StringBuffer sql=new StringBuffer("SELECT * FROM  (    SELECT "+
				"		MAX(CUST.CREATE_DATE) \"CREATE_DATE\","+
				"       MAX(CUST.LOGIN_NAME) \"loginName\","+
				"       CUST.ID,"+
				"       MAX(CUST.CUST_NAME) \"custName\","+
				"       MAX(CUST.CREDENTIALS_CODE) \"credentialsCode\","+
				"       MAX(PRODUCT.PRODUCT_NAME) \"productName\","+
				"       SUM(NVL(SUBACCOUNT.ACCOUNT_TOTAL_VALUE,0)) \"sumAmount\""+
				"    FROM BAO_T_CUST_INFO CUST,BAO_T_INVEST_INFO INVEST,BAO_T_SUB_ACCOUNT_INFO SUBACCOUNT,BAO_T_PRODUCT_INFO PRODUCT, BAO_T_PRODUCT_TYPE_INFO PRODUCTTYPE"+ 
				"       WHERE   CUST.ID =INVEST.CUST_ID"+
				"         AND   INVEST.ID =SUBACCOUNT.RELATE_PRIMARY"+
				"         AND   INVEST.PRODUCT_ID = PRODUCT.ID "+
				"         AND   PRODUCT.PRODUCT_TYPE = PRODUCTTYPE.ID"+
				"         AND SUBACCOUNT.RECORD_STATUS = '有效' "+
				"         AND PRODUCTTYPE.TYPE_NAME =? ");
		List<Object> objList=new ArrayList<>();
		objList.add(productName);
		//用户昵称：
		Object loginName=paramMap.get("loginName");
		if(!StringUtils.isEmpty(loginName)){
			sql.append(" and CUST.login_name like ?");
			objList.add("%"+loginName+"%");
		}
		//真实姓名：
		Object custName=paramMap.get("custName");
		if(!StringUtils.isEmpty(custName)){
			sql.append(" and CUST.cust_name=?");
			objList.add(custName);
		}
		//证件号码：
		Object credentialsCode=paramMap.get("credentialsCode");
		if(!StringUtils.isEmpty(credentialsCode)){
			sql.append(" and CUST.credentials_code=?");
			objList.add(credentialsCode);
		}
		sql.append("      GROUP BY CUST.ID"+
				") TAB1 LEFT JOIN ( SELECT FLOW.CUST_ID, SUM(NVL(FLOW.TRADE_AMOUNT,0)) \"tradeAmount\","+
				"  SUM(DECODE(TO_NUMBER(TO_CHAR(FLOW.TRADE_DATE, 'YYYYMMDD')), ?, NVL(FLOW.TRADE_AMOUNT,0), 0)) \"yestTradeAmount\""+
				"  FROM BAO_T_ACCOUNT_FLOW_INFO FLOW WHERE FLOW.TRADE_TYPE=?"+ 
				"  GROUP BY FLOW.CUST_ID )  TAB2 ON TAB1.ID=TAB2.CUST_ID order by TAB1.CREATE_DATE desc");
		objList.add(DateUtils.formatDate(DateUtils.getAfterDay(new Date(), -1), "yyyyMMdd"));
		objList.add(tradeType);
		Page<Map<String,Object>> page=repositoryUtil.queryForPageMap(sql.toString(), objList.toArray(), start, pageSize);
		return page;
	}

}
