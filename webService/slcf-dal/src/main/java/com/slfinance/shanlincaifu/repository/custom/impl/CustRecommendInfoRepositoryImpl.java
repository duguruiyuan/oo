/** 
 * @(#)CustRecommendInfoRepositoryImpl.java 1.0.0 2015年8月24日 下午4:13:06  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository.custom.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.CustRecommendInfoEntity;
import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.CustRecommendInfoRepositoryCustom;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.SharedUtil;
import com.slfinance.shanlincaifu.utils.SqlCondition;

/**   
 * 自定义推荐人客户关系实现类
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年8月24日 下午4:13:06 $ 
 */
@Repository
public class CustRecommendInfoRepositoryImpl implements
		CustRecommendInfoRepositoryCustom {
	
	@Autowired
	private RepositoryUtil repositoryUtil;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public List<Map<String, Object>> findWaitSettlement(
			Map<String, Object> params) {
				
		return null;
	}

	@Override
	public int countWaitSettlement(Map<String, Object> params) {
		
		return 0;
	}

	/**
	 * 我是业务员-公用-查询客户经理名下所有客户
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param param :Map<br>
     *      <tt>custManagerId:String:客户经理主键(可选)</tt><br>
     *      <tt>custName     :String:用户姓名(可选)</tt><br>
	 * @return List<Map<String, Object>><br>
	 *      		<tt>custId         :String:客户ID</tt><br>
	 *      		<tt>custName       :String:用户名</tt><br>
	 *      		<tt>credentialsCode:String:证件号码</tt><br>
	 *      		<tt>mobile         :String:手机</tt><br>
	 *      		<tt>registerDate   :String:注册时间</tt><br>
	 *      		<tt>custManagerId  :String:客户经理Id</tt><br>  
     * @throws SLException
     */
	@Override
	public List<Map<String, Object>> queryCustNameByManager(
			Map<String, Object> param) {
		SqlCondition sqlCon = new SqlCondition(new StringBuilder(), param)
		.addSql(" SELECT * FROM ( ")
		.addSql(" SELECT cr.CUST_ID \"custManagerId\", c.ID \"custId\", c.CUST_NAME  \"custName\" ")
		.addSql("      , c.CREDENTIALS_CODE \"credentialsCode\", c.MOBILE \"mobile\", c.CREATE_DATE \"registerDate\" ")
		.addSql(" , (SELECT count(1) FROM BAO_T_CUST_APPLY_INFO ca WHERE ca.APPLY_TYPE = '客户经理变更' AND (ca.APPLY_STATUS = '审核中' OR ca.APPLY_STATUS = '审核回退' ) AND ca.CUST_ID = cr.CUST_ID AND ca.TRANSFER_CUST_ID = c.ID ) \"isDoing\" ")
		.addSql("   FROM BAO_T_CUST_RECOMMEND_INFO cr, BAO_T_CUST_INFO c ")
		.addSql("  WHERE c.ID =  cr.QUILT_CUST_ID ")
		.addSql("    AND cr.RECORD_STATUS = '"+Constant.VALID_STATUS_VALID+"' ")
		.addSql("    AND c.CUST_NAME IS NOT NULL ")
		.addSql("    AND c.CREDENTIALS_CODE IS NOT NULL ")
		.addString("custManagerId", "cr.CUST_ID")
		.addLike("custName", "c.CUST_NAME")
		.addSql(" ) WHERE \"isDoing\" = 0 ");
		
		return repositoryUtil.queryForMap(sqlCon.toString(), sqlCon.toArray());
	}

	/**
	 * 我是业务员-公用-查询业务员信息
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param param:Map
     *      <tt>custId         :String:客户ID(可选)</tt><br>
     *      <tt>custName       :String:用户姓名(可选)</tt><br>
     *      <tt>credentialsCode:String:证件号码（可以为空）</tt><br>
     *      <tt>mobile         :String:手机号（可以为空）</tt><br>
	 * @return List<Map<String, Object>><br>
     *      		<tt>custId         :String:客户ID</tt><br>
     *      		<tt>custName       :String:用户名</tt><br>
     *      		<tt>credentialsCode:String:证件号码</tt><br>
     *      		<tt>mobile         :String:手机号</tt><br>
     *      		<tt>registerDate   :String:注册时间</tt><br>
     * @throws SLException
     */
	@Override
	public List<Map<String, Object>> queryCustManager(Map<String, Object> param) {
		
		SqlCondition sqlCon = new SqlCondition(new StringBuilder(), param)
		.addSql(" SELECT c.ID \"custId\", c.CUST_NAME \"custName\", c.CREDENTIALS_CODE \"credentialsCode\", c.MOBILE \"mobile\", c.CREATE_DATE \"registerDate\" ")
		.addSql("   FROM BAO_T_CUST_INFO c ")
		.addSql("  WHERE 1=1 ");
		if(!StringUtils.isEmpty(param.get("custId"))){
			sqlCon.addString("custId", "c.ID");
		} else {
			sqlCon.addSql(" AND IS_EMPLOYEE = '是' ");
		}
		sqlCon.addRightLike("custName", "c.CUST_NAME")
		.addRightLike("credentialsCode", "c.CREDENTIALS_CODE")
		.addRightLike("mobile", "c.MOBILE");
		
		return repositoryUtil.queryForMap(sqlCon.toString(), sqlCon.toArray());
	}

	/**
	 * 我是业务员-公用-查询客户下面附属银行
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param param:Map
     *      <tt>custId:String:客户ID</tt><br>
	 * @return List<Map<String, Object>><br>
     *      		<tt>bankId  :String:银行主键</tt><br>
     *      		<tt>bankName:String:bankName</tt><br>
     * @throws SLException
     */
	@Override
	public List<Map<String, Object>> queryCustBankByCustId(
			Map<String, Object> param) {
		
		SqlCondition sqlCon = new SqlCondition(new StringBuilder(), param)
		.addSql(" SELECT bc.ID \"bankId\", bc.BANK_NAME \"bankName\" ")
		.addSql(" , bc.CARD_NO \"bankCardNo\", bc.SUB_BRANCH_NAME \"branchBankName\" ")
		.addSql(" , bc.OPEN_PROVINCE \"openProvince\", bc.OPEN_CITY \"openCity\" ")
		.addSql(" , pa1.PARAMETER_NAME \"openProvinceName\" , pa2.PARAMETER_NAME \"openCityName\" ")
		.addSql("   FROM BAO_T_BANK_CARD_INFO bc ")
		.addSql("   LEFT JOIN COM_T_PARAM pa1 ")
		.addSql("          ON pa1.\"TYPE\" = 'province' ")
 		.addSql("         AND pa1.value = bc.OPEN_PROVINCE ")
		.addSql("   LEFT JOIN COM_T_PARAM pa2 ")
		.addSql("          ON pa2.\"TYPE\" = 'city' ")
 		.addSql("         AND pa2.value = bc.OPEN_CITY ")
		.addSql("  WHERE 1=1")
		.addSql("    AND bc.RECORD_STATUS ='"+Constant.VALID_STATUS_VALID+"' ")
		.addSql("    AND bc.BANK_FLAG ='"+Constant.CUST_TYPE_OFFLINE+"' ")
		.addString("custId", "bc.CUST_ID");
		
		return repositoryUtil.queryForMap(sqlCon.toString(), sqlCon.toArray());
	}

	@Override
	public List<Map<String, Object>> queryCustName(Map<String, Object> params) throws SLException {
		StringBuilder sqlString = new StringBuilder()
		.append("  SELECT C.ID                \"custId\",  ")
		.append("        C.CUST_NAME         \"custName\", ")
		.append("        C.CREDENTIALS_CODE  \"credentialsCode\", ")
		.append("        C.MOBILE            \"mobile\", ")
		.append("        C.CREATE_DATE       \"registerDate\", ")
		.append("        cr.cust_id          \"custManagerId\" ")
		.append("  FROM BAO_T_CUST_INFO C  ")
		.append("  LEFT JOIN BAO_T_CUST_RECOMMEND_INFO CR ON CR.QUILT_CUST_ID = C.ID AND CR.RECORD_STATUS = '有效' ")
		.append("  WHERE C.CUST_KIND IS NULL ")
		.append("    AND C.CUST_NAME = ? ");
		List<Object> objList = Lists.newArrayList();
		objList.add(params.get("custName"));
		return repositoryUtil.queryForMap(sqlString.toString(), objList.toArray());
	}

	/** 批量更新 */
	public int batchUpdateRecommend(final List<CustRecommendInfoEntity> list) {
		StringBuffer sql = new StringBuffer()
		.append("INSERT INTO BAO_T_CUST_RECOMMEND_INFO (")
		.append("ID,CUST_ID,QUILT_CUST_ID,APPLY_ID,SOURCE")
		.append(",START_DATE,RECORD_STATUS,VERSION,MEMO")
		.append(",CREATE_USER,CREATE_DATE,LAST_UPDATE_USER,LAST_UPDATE_DATE)")
		.append(" VALUES(?,?,?,?,?")// 1-5
		.append(",?,?,?,?")// 6-9
		.append(",?,?,?,?)")// 10-13
		;

		int[] count = jdbcTemplate.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setString(1, SharedUtil.getUniqueString());
				ps.setString(2, list.get(i).getCustId());
				ps.setString(3, list.get(i).getQuiltCustId());
				ps.setString(4, list.get(i).getApplyId());
				ps.setString(5, list.get(i).getSource());
				ps.setTimestamp(6, list.get(i).getStartDate());
				ps.setString(7, list.get(i).getRecordStatus());
				ps.setInt(8, list.get(i).getVersion());
				ps.setString(9, list.get(i).getMemo());
				ps.setString(10, list.get(i).getCreateUser());
				ps.setTimestamp(11, new Timestamp(list.get(i).getCreateDate().getTime()));
				ps.setString(12, list.get(i).getLastUpdateUser());
				ps.setTimestamp(13, new Timestamp(list.get(i).getLastUpdateDate().getTime()));
			}

			public int getBatchSize() {
				return list.size();
			}
		});
		return (count!=null?count.length:0);
	}

	@Override
	public List<Map<String, Object>> queryTransferByQuiltCustId(
			Map<String, Object> params) {
		
		StringBuilder sql = new StringBuilder()
		.append("  select id \"id\", CUST_ID \"custId\", QUILT_CUST_ID \"quiltCustId\" ")
		.append("  from BAO_T_CUST_RECOMMEND_INFO ")
		.append("  where 1=1 ");
		
		SqlCondition sqlCondition = new SqlCondition(sql, params);
		sqlCondition.addString("recordStatus", "RECORD_STATUS");
		sqlCondition.addList("quiltCustIdList", "QUILT_CUST_ID");
		
		return repositoryUtil.queryForMap(sqlCondition.toString(), sqlCondition.toArray());
	}

}
