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
import com.slfinance.shanlincaifu.entity.AgentEntity;
import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.CustApplyInfoRepositoryCustom;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.SqlCondition;

/**
 * 善林财富-客户管理
 * 
 * @author liyy
 * @version $Revision:1.0.0, $Date: 2016年2月26日 下午3:06:28 $
 */
@Repository
public class CustApplyInfoRepositoryImpl implements CustApplyInfoRepositoryCustom{

	@Autowired
	private RepositoryUtil repositoryUtil;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	/**
	 * 线下业务-客户转移-客户转移列表
	 *
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param param
     *      <tt>start          :String:起始值</tt><br>
     *      <tt>length         :String:长度</tt><br>
     *      <tt>userId         :String:客户经理ID</tt><br>
     *      <tt>loginName      :String:用户名（可选）</tt><br>
     *      <tt>custName       :String:用户名（可选）</tt><br>
     *      <tt>credentialsCode:String:证件号码(可选)</tt><br>
     *      <tt>mobile         :String:手机号（可选）</tt><br>
     *      <tt>auditStatus    :String:审核状态（可选）</tt><br>
     *      <tt>custId         :String:客户ID（可选）</tt><br>
     *      <tt>beginOperateDate:String:开始创建时间（可选）</tt><br>
     *      <tt>endOperateDate  :String:结束创建时间（可选）</tt><br>
	 * @return Page 
     *      	<tt>iTotalDisplayRecords:String:总数</tt><br>
     *      	<tt>data:List
     *      		<tt>custApplyId    :String:客户申请ID</tt><br>
     *      		<tt>loginName      :String:用户名</tt><br>
     *      		<tt>custName       :String:客户姓名</tt><br>
     *      		<tt>credentialsCode:String:证件号码</tt><br>
     *      		<tt>mobile         :String:手机号</tt><br>
     *      		<tt>createUser     :String:操作人</tt><br>
     *      		<tt>createDate     :String:操作时间</tt><br>
     *      		<tt>registerDate   :String:注册时间</tt><br>
     *      		<tt>newCustManager :String:新业务员</tt><br>
     *      		<tt>auditStatus    :String:审核状态</tt><br>
     *          </tt><br>
     *  @throws SLException
	 */
	@Deprecated
	public Page<Map<String, Object>> queryAllCustTransferList(
			Map<String, Object> param) {
		
		SqlCondition sqlCon = new SqlCondition(new StringBuilder(), param)
		.addSql(" SELECT * FROM ( ")
		
		.addSql("  SELECT ca.ID \"custApplyId\", c.LOGIN_NAME \"loginName\", c.CUST_NAME \"custName\" ")
		.addSql(", c.CREDENTIALS_CODE \"credentialsCode\", c.MOBILE \"mobile\", CASE WHEN uOp1.CUST_NAME IS NOT NULL THEN uOp1.CUST_NAME ELSE uOp2.USER_NAME END \"createUser\" ")
		.addSql(", ca.CREATE_DATE \"createDate\", c.CREATE_DATE \"registerDate\", CASE WHEN u1.CUST_NAME IS NOT NULL THEN u1.CUST_NAME ELSE u2.USER_NAME END \"newCustManager\" ")
		.addSql(", a.AUDIT_STATUS \"auditStatus\" ")
		.addSql("    FROM BAO_T_CUST_INFO manager, /* 客户经理 */ ")
		.addSql("         BAO_T_CUST_INFO c, /* 客户 */ ")
		.addSql("         BAO_T_CUST_APPLY_INFO ca ")
		.addSql("    LEFT JOIN BAO_T_AUDIT_INFO a ")
		.addSql("          ON a.RELATE_TYPE = '" + Constant.TABLE_BAO_T_CUST_APPLY_INFO + "' ")
		.addSql("         AND a.RELATE_PRIMARY = ca.ID ")
		.addSql("    LEFT JOIN BAO_T_CUST_INFO u1 ON u1.ID = ca.NEW_CUST_MANAGER_ID ")// 取客户经理
		.addSql("    LEFT JOIN COM_T_USER u2 ON u2.ID = ca.NEW_CUST_MANAGER_ID ")// 取客户经理
		.addSql("    LEFT JOIN BAO_T_CUST_INFO uOp1 ON uOp1.ID = ca.CREATE_USER ")// 取操作人
		.addSql("    LEFT JOIN COM_T_USER uOp2 ON uOp2.ID = ca.CREATE_USER ")// 取操作人
		.addSql("   WHERE ca.TRANSFER_CUST_ID = c.ID ")
		.addSql("     AND ca.APPLY_TYPE = '" + Constant.OPERATION_TYPE_54 + "' ")
		.addSql("     AND ca.CUST_ID = manager.ID  ")
		/* 递归查询同一部门下客户经理的客户 Start */
		.addSql("     AND manager.CREDENTIALS_CODE IN ( ")
		.addSql("          SELECT CREDENTIALS_CODE FROM COM_T_USER  ")
		.addSql("        	WHERE AGENT_ID IN ( ")
		.addSql("                 SELECT ID FROM COM_T_AGENT ")
		.addSql("                  START WITH ID = (SELECT AGENT_ID FROM COM_T_USER WHERE ID = '"+param.get("userId").toString()+"' ) ") /* 大后台的userId是user表里的Id */
		.addSql("         	     CONNECT BY PRIOR id = agent_parent_id ")
		.addSql("         	) ")
		.addSql("         ) ")
		/* 递归查询同一部门下客户经理的客户 End */
		// .addString("userId", "ca.CUST_ID")
		.addString("loginName", "c.LOGIN_NAME")
		.addString("custName", "c.CUST_NAME")
		.addString("credentialsCode", "c.CREDENTIALS_CODE")
		.addString("mobile", "c.MOBILE")
		.addString("auditStatus", "a.AUDIT_STATUS")
		.addString("custId", "c.ID")
		.addBeginDate("beginOperateDate", "ca.CREATE_DATE")
		.addEndDate("endOperateDate", "ca.CREATE_DATE")
		
		.addSql(" UNION ALL  ")/* 公司部门才能看原客户经理为空（自己注册用户，被申请的转移）的数据 */
		.addSql("  SELECT ca.ID \"custApplyId\", c.LOGIN_NAME \"loginName\", c.CUST_NAME \"custName\" ")
		.addSql(", c.CREDENTIALS_CODE \"credentialsCode\", c.MOBILE \"mobile\", CASE WHEN uOp1.CUST_NAME IS NOT NULL THEN uOp1.CUST_NAME ELSE uOp2.USER_NAME END \"createUser\" ")
		.addSql(", ca.CREATE_DATE \"createDate\", c.CREATE_DATE \"registerDate\", CASE WHEN u1.CUST_NAME IS NOT NULL THEN u1.CUST_NAME ELSE u2.USER_NAME END \"newCustManager\" ")
		.addSql(", a.AUDIT_STATUS \"auditStatus\" ")
		.addSql("    FROM BAO_T_CUST_INFO c, /* 客户 */ ")
		.addSql("         BAO_T_CUST_APPLY_INFO ca ")
		.addSql("    LEFT JOIN BAO_T_AUDIT_INFO a ")
		.addSql("          ON a.RELATE_TYPE = '" + Constant.TABLE_BAO_T_CUST_APPLY_INFO + "' ")
		.addSql("         AND a.RELATE_PRIMARY = ca.ID ")
		.addSql("    LEFT JOIN BAO_T_CUST_INFO u1 ON u1.ID = ca.NEW_CUST_MANAGER_ID ")// 取客户经理
		.addSql("    LEFT JOIN COM_T_USER u2 ON u2.ID = ca.NEW_CUST_MANAGER_ID ")// 取客户经理
		.addSql("    LEFT JOIN BAO_T_CUST_INFO uOp1 ON uOp1.ID = ca.CREATE_USER ")// 取操作人
		.addSql("    LEFT JOIN COM_T_USER uOp2 ON uOp2.ID = ca.CREATE_USER ")// 取操作人
		.addSql("   WHERE ca.TRANSFER_CUST_ID = c.ID ")
		.addSql("     AND ca.APPLY_TYPE = '" + Constant.OPERATION_TYPE_54 + "' ")
		.addSql("     AND ca.CUST_ID IS NULL  ") /* 只能是NULL的数据 */
		.addSql("     AND '1' = (SELECT AGENT_ID FROM COM_T_USER WHERE ID = '"+param.get("userId").toString()+"' ) ") /* 大后台的userId是user表里的Id,公司部门才能看原客户经理为空（自己注册用户，被申请的转移）的数据 */
		// .addString("userId", "ca.CUST_ID")
		.addString("loginName", "c.LOGIN_NAME")
		.addString("custName", "c.CUST_NAME")
		.addString("credentialsCode", "c.CREDENTIALS_CODE")
		.addString("mobile", "c.MOBILE")
		.addString("auditStatus", "a.AUDIT_STATUS")
		.addString("custId", "c.ID")
		.addBeginDate("beginOperateDate", "ca.CREATE_DATE")
		.addEndDate("endOperateDate", "ca.CREATE_DATE")
		.addSql(" ) ")
		
		.addSql(" ORDER BY decode(\"auditStatus\", '待审核', 1, '审核回退', 2, '通过', 3, '拒绝', 4) ")
		.addSql("        , \"createDate\" DESC ");
		
		return repositoryUtil.queryForPageMap(sqlCon.toString(), sqlCon.toArray(), Integer.parseInt(param.get("start").toString()), Integer.parseInt(param.get("length").toString()));
	}
	
	/**
	 * 根据客户经理id取agent信息
	 * @param userId String 客户经理id
	 * @return AgentEntity
	 * @author  liyy
	 * @date    2016年4月8日 
	 */
	public List<AgentEntity> getAgentEntityByUserId(String userId) {
		StringBuffer sql = new StringBuffer()
		.append(" SELECT ID \"id\", AGENT_NAME \"agentName\", AGENT_PARENT_ID \"agentParentId\", AGENT_TYPE \"agentType\", DATA_PERMISSION \"dataPermission\", AGENT_CODE \"agentCode\", TEL \"tel\", ADDRESS \"address\", CITY_ID \"cityId\", STATUS \"status\", CREATE_USER_ID \"createUserId\", CREATE_DATE \"createDate\", UPDATE_USER_ID \"updateUserId\", UPDATE_DATE \"updateDate\", VERSION \"version\", MEMO \"memo\", AGENT_ENCODED \"agentEncoded\" ")
		.append("   FROM COM_T_AGENT ")
		.append("  WHERE ID = ( ")
		.append("     SELECT AGENT_ID FROM COM_T_USER WHERE ID = ? ")
		.append("   ) ")
		;
		List<Object> args = new ArrayList<Object>();
		args.add(userId);
		
		return repositoryUtil.queryForList(sql.toString(), args.toArray(), AgentEntity.class);
	}
	
	/**
	 * 线下业务-客户转移-客户转移列表
	 *
	 * @author  liyy
	 * @date    2016年4月8日 
	 * @param param
     *      <tt>start          :String:起始值</tt><br>
     *      <tt>length         :String:长度</tt><br>
     *      <tt>userId         :String:客户经理ID</tt><br>
     *      <tt>loginName      :String:用户名（可选）</tt><br>
     *      <tt>custName       :String:用户名（可选）</tt><br>
     *      <tt>credentialsCode:String:证件号码(可选)</tt><br>
     *      <tt>mobile         :String:手机号（可选）</tt><br>
     *      <tt>auditStatus    :String:审核状态（可选）</tt><br>
     *      <tt>custId         :String:客户ID（可选）</tt><br>
     *      <tt>beginOperateDate:String:开始创建时间（可选）</tt><br>
     *      <tt>endOperateDate  :String:结束创建时间（可选）</tt><br>
     * @param dataPermission String 数据权限
	 * @return Page 
     *      	<tt>iTotalDisplayRecords:String:总数</tt><br>
     *      	<tt>data:List
     *      		<tt>custApplyId    :String:客户申请ID</tt><br>
     *      		<tt>loginName      :String:用户名</tt><br>
     *      		<tt>custName       :String:客户姓名</tt><br>
     *      		<tt>credentialsCode:String:证件号码</tt><br>
     *      		<tt>mobile         :String:手机号</tt><br>
     *      		<tt>createUser     :String:操作人</tt><br>
     *      		<tt>createDate     :String:操作时间</tt><br>
     *      		<tt>registerDate   :String:注册时间</tt><br>
     *      		<tt>newCustManager :String:新业务员</tt><br>
     *      		<tt>auditStatus    :String:审核状态</tt><br>
     *          </tt><br>
     *  @throws SLException
	 */
	public Page<Map<String, Object>> queryAllCustTransferListNew(Map<String, Object> param, String dataPermission) {
		SqlCondition sqlCon = new SqlCondition(new StringBuilder(), param)
		.addSql(" SELECT * FROM ( ")
		
		.addSql("  SELECT ca.ID \"custApplyId\", c.LOGIN_NAME \"loginName\", c.CUST_NAME \"custName\" ")
		.addSql(", c.CREDENTIALS_CODE \"credentialsCode\", c.MOBILE \"mobile\", CASE WHEN uOp1.CUST_NAME IS NOT NULL THEN uOp1.CUST_NAME ELSE uOp2.USER_NAME END \"createUser\" ")
		.addSql(", ca.CREATE_DATE \"createDate\", c.CREATE_DATE \"registerDate\", CASE WHEN u1.CUST_NAME IS NOT NULL THEN u1.CUST_NAME ELSE u2.USER_NAME END \"newCustManager\" ")
		.addSql(", a.AUDIT_STATUS \"auditStatus\" ")
		.addSql("    FROM BAO_T_CUST_INFO manager, /* 客户经理 */ ")
		.addSql("         BAO_T_CUST_INFO c, /* 客户 */ ")
		.addSql("         BAO_T_CUST_APPLY_INFO ca ")
		.addSql("    LEFT JOIN BAO_T_AUDIT_INFO a ")
		.addSql("          ON a.RELATE_TYPE = '" + Constant.TABLE_BAO_T_CUST_APPLY_INFO + "' ")
		.addSql("         AND a.RELATE_PRIMARY = ca.ID ")
		.addSql("    LEFT JOIN BAO_T_CUST_INFO u1 ON u1.ID = ca.NEW_CUST_MANAGER_ID ")// 取客户经理
		.addSql("    LEFT JOIN COM_T_USER u2 ON u2.ID = ca.NEW_CUST_MANAGER_ID ")// 取客户经理
		.addSql("    LEFT JOIN BAO_T_CUST_INFO uOp1 ON uOp1.ID = ca.CREATE_USER ")// 取操作人
		.addSql("    LEFT JOIN COM_T_USER uOp2 ON uOp2.ID = ca.CREATE_USER ")// 取操作人
		.addSql("   WHERE ca.TRANSFER_CUST_ID = c.ID ")
		.addSql("     AND ca.APPLY_TYPE = '" + Constant.OPERATION_TYPE_54 + "' ")
		.addSql("     AND ca.CUST_ID = manager.ID  ")
		/* 客户经理拥有权限 Start */
		.addSql("     AND manager.CREDENTIALS_CODE IN ( ")
		.addSql("          SELECT CREDENTIALS_CODE FROM COM_T_USER  ")
		.addSql("        	WHERE AGENT_ID IN ( ")
		.addSql("        	   SELECT ID FROM COM_T_AGENT ")
		.addSql("        	    WHERE 1=1 AND ( ");
		if(dataPermission.indexOf("|") > -1){
			String[] array = dataPermission.split("|");
			for(int i=0;i<array.length;i++){
				if(i != 0){
					sqlCon.addSql("          or ");
				}
				sqlCon.addSql("        	        DATA_PERMISSION LIKE '" + array[i] + "%' ");
			}
		} else {
			sqlCon.addSql("        	        DATA_PERMISSION LIKE '" + dataPermission + "%' ");
		}
		sqlCon.addSql("        	      ) ")
		.addSql("         	) ")
		.addSql("         ) ")
		/* 客户经理拥有权限 End */
		// .addString("userId", "ca.CUST_ID")
		.addString("loginName", "c.LOGIN_NAME")
		.addString("custName", "c.CUST_NAME")
		.addString("credentialsCode", "c.CREDENTIALS_CODE")
		.addString("mobile", "c.MOBILE")
		.addString("auditStatus", "a.AUDIT_STATUS")
		.addString("custId", "c.ID")
		.addBeginDate("beginOperateDate", "ca.CREATE_DATE")
		.addEndDate("endOperateDate", "ca.CREATE_DATE")
		
		.addSql(" UNION ALL  ")/* 公司部门才能看原客户经理为空（自己注册用户，被申请的转移）的数据 */
		.addSql("  SELECT ca.ID \"custApplyId\", c.LOGIN_NAME \"loginName\", c.CUST_NAME \"custName\" ")
		.addSql(", c.CREDENTIALS_CODE \"credentialsCode\", c.MOBILE \"mobile\", CASE WHEN uOp1.CUST_NAME IS NOT NULL THEN uOp1.CUST_NAME ELSE uOp2.USER_NAME END \"createUser\" ")
		.addSql(", ca.CREATE_DATE \"createDate\", c.CREATE_DATE \"registerDate\", CASE WHEN u1.CUST_NAME IS NOT NULL THEN u1.CUST_NAME ELSE u2.USER_NAME END \"newCustManager\" ")
		.addSql(", a.AUDIT_STATUS \"auditStatus\" ")
		.addSql("    FROM BAO_T_CUST_INFO c, /* 客户 */ ")
		.addSql("         BAO_T_CUST_APPLY_INFO ca ")
		.addSql("    LEFT JOIN BAO_T_AUDIT_INFO a ")
		.addSql("          ON a.RELATE_TYPE = '" + Constant.TABLE_BAO_T_CUST_APPLY_INFO + "' ")
		.addSql("         AND a.RELATE_PRIMARY = ca.ID ")
		.addSql("    LEFT JOIN BAO_T_CUST_INFO u1 ON u1.ID = ca.NEW_CUST_MANAGER_ID ")// 取客户经理
		.addSql("    LEFT JOIN COM_T_USER u2 ON u2.ID = ca.NEW_CUST_MANAGER_ID ")// 取客户经理
		.addSql("    LEFT JOIN BAO_T_CUST_INFO uOp1 ON uOp1.ID = ca.CREATE_USER ")// 取操作人
		.addSql("    LEFT JOIN COM_T_USER uOp2 ON uOp2.ID = ca.CREATE_USER ")// 取操作人
		.addSql("   WHERE ca.TRANSFER_CUST_ID = c.ID ")
		.addSql("     AND ca.APPLY_TYPE = '" + Constant.OPERATION_TYPE_54 + "' ")
		.addSql("     AND ca.CUST_ID IS NULL  ") /* 只能是NULL的数据 */
		//.addSql("     AND '1' = (SELECT AGENT_ID FROM COM_T_USER WHERE ID = '"+param.get("userId").toString()+"' ) ") /* 大后台的userId是user表里的Id,公司部门才能看原客户经理为空（自己注册用户，被申请的转移）的数据 */
		.addSql("     AND EXISTS (SELECT 1 FROM COM_T_USER T, COM_T_AGENT S WHERE T.AGENT_ID = S.ID AND (S.DATA_PERMISSION = '00' or S.DATA_PERMISSION like '00|%') AND T.ID = '"+param.get("userId").toString()+"' ) ") /* update by wangjf 2016-06-28缺少原客户经理的需拥有善林金融数据权限('00')才能查看*/ 
		// .addString("userId", "ca.CUST_ID")
		.addString("loginName", "c.LOGIN_NAME")
		.addString("custName", "c.CUST_NAME")
		.addString("credentialsCode", "c.CREDENTIALS_CODE")
		.addString("mobile", "c.MOBILE")
		.addString("auditStatus", "a.AUDIT_STATUS")
		.addString("custId", "c.ID")
		.addBeginDate("beginOperateDate", "ca.CREATE_DATE")
		.addEndDate("endOperateDate", "ca.CREATE_DATE")
		.addSql(" ) ")
		
		.addSql(" ORDER BY decode(\"auditStatus\", '待审核', 1, '审核回退', 2, '通过', 3, '拒绝', 4) ")
		.addSql("        , \"createDate\" DESC ");
		
		return repositoryUtil.queryForPageMap(sqlCon.toString(), sqlCon.toArray(), Integer.parseInt(param.get("start").toString()), Integer.parseInt(param.get("length").toString()));
	}
	
	/**
	 * 前台-客户转移-客户转移列表
	 *
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param param
     *      <tt>start           :String:起始值</tt><br>
     *      <tt>length          :String:长度</tt><br>
     *      <tt>loginName       :String:用户名（可选）</tt><br>
     *      <tt>custName        :String:用户名（可选）</tt><br>
     *      <tt>credentialsCode :String:证件号码(可选)</tt><br>
     *      <tt>mobile          :String:手机号（可选）</tt><br>
     *      <tt>auditStatus     :String:审核状态（可选）</tt><br>
     *      <tt>custId          :String:客户ID（可选）</tt><br>
     *      <tt>userId          :String:客户经理ID（可选）</tt><br>
     *      <tt>beginOperateDate:String:开始创建时间（可选）</tt><br>
     *      <tt>endOperateDate  :String:结束创建时间（可选）</tt><br>
	 * @return Page 
     *      	<tt>iTotalDisplayRecords:String:总数</tt><br>
     *      	<tt>data:List
     *      		<tt>custApplyId    :String:客户申请ID</tt><br>
     *      		<tt>loginName      :String:用户名</tt><br>
     *      		<tt>custName       :String:客户姓名</tt><br>
     *      		<tt>credentialsCode:String:证件号码</tt><br>
     *      		<tt>mobile         :String:手机号</tt><br>
     *      		<tt>createUser     :String:操作人</tt><br>
     *      		<tt>createDate     :String:操作时间</tt><br>
     *      		<tt>registerDate   :String:注册时间</tt><br>
     *      		<tt>newCustManager :String:新业务员</tt><br>
     *      		<tt>auditStatus    :String:审核状态</tt><br>
     *          </tt><br>
     *  @throws SLException
	 */
	@Override
	public Page<Map<String, Object>> queryCustTransferList(
			Map<String, Object> param) {
		SqlCondition sqlCon = new SqlCondition(new StringBuilder(), param)
		.addSql("  SELECT ca.ID \"custApplyId\", c.LOGIN_NAME \"loginName\", c.CUST_NAME \"custName\" ")
		.addSql(", c.CREDENTIALS_CODE \"credentialsCode\", c.MOBILE \"mobile\", CASE WHEN uOp1.CUST_NAME IS NOT NULL THEN uOp1.CUST_NAME ELSE uOp2.USER_NAME END \"createUser\" ")
		.addSql(", ca.CREATE_DATE \"createDate\", c.CREATE_DATE \"registerDate\", CASE WHEN u1.CUST_NAME IS NOT NULL THEN u1.CUST_NAME ELSE u2.USER_NAME END \"newCustManager\" ")
		.addSql(", a.AUDIT_STATUS \"auditStatus\" ")
		.addSql("    FROM BAO_T_CUST_INFO c, BAO_T_CUST_APPLY_INFO ca ")
		.addSql("    LEFT JOIN BAO_T_AUDIT_INFO a ")
		.addSql("          ON a.RELATE_TYPE = '" + Constant.TABLE_BAO_T_CUST_APPLY_INFO + "' ")
		.addSql("         AND a.RELATE_PRIMARY = ca.ID ")
		.addSql("    LEFT JOIN BAO_T_CUST_INFO u1 ON u1.ID = ca.NEW_CUST_MANAGER_ID ")// 取客户经理
		.addSql("    LEFT JOIN COM_T_USER u2 ON u2.ID = ca.NEW_CUST_MANAGER_ID ")// 取客户经理
		.addSql("    LEFT JOIN BAO_T_CUST_INFO uOp1 ON uOp1.ID = ca.CREATE_USER ")// 取操作人
		.addSql("    LEFT JOIN COM_T_USER uOp2 ON uOp2.ID = ca.CREATE_USER ")// 取操作人
		.addSql("   WHERE ca.TRANSFER_CUST_ID = c.ID ")
		.addSql("        AND ca.APPLY_TYPE = '" + Constant.OPERATION_TYPE_54 + "' ")
		.addString("loginName", "c.LOGIN_NAME")
		.addString("custName", "c.CUST_NAME")
		.addString("credentialsCode", "c.CREDENTIALS_CODE")
		.addString("mobile", "c.MOBILE")
		.addString("auditStatus", "a.AUDIT_STATUS")
		.addString("custId", "c.ID")
		.addString("userId", "ca.CUST_ID")
		.addBeginDate("beginOperateDate", "ca.CREATE_DATE")
		.addEndDate("endOperateDate", "ca.CREATE_DATE")
		.addSql(" ORDER BY ca.CREATE_DATE DESC ");
		
		return repositoryUtil.queryForPageMap(sqlCon.toString(), sqlCon.toArray(), Integer.parseInt(param.get("start").toString()), Integer.parseInt(param.get("length").toString()));
	}

	/**
	 * 线下业务-客户转移-客户转移查看详情-基本信息
	 *
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param param
     *      <tt>custApplyId：客户申请ID</tt><br>
	 * @return List<Map<String, Object>> 
     *      	<tt>custApplyId                  :String:客户申请ID</tt><br>
     *      	<tt>custId                       :String:用户ID</tt><br>
     *      	<tt>loginName                    :String:用户名</tt><br>
     *      	<tt>custName                     :String:客户姓名</tt><br>
     *      	<tt>credentialsCode              :String:证件号码</tt><br>
     *      	<tt>mobile                       :String:手机号</tt><br>
     *      	<tt>registerDate                 :String:注册时间</tt><br>
     *      	<tt>oldCustManagerId             :String:原客户经理主键</tt><br>
     *      	<tt>oldCustManagerName           :String:原客户经理名称</tt><br>
     *      	<tt>oldCustManagerMobile         :String:原客户经理手机号</tt><br>
     *      	<tt>oldCustManagerCredentialsCode:String:原客户经理身份证号</tt><br>
     *      	<tt>newCustManagerId             :String:新客户经理主键</tt><br>
     *      	<tt>newCustManagerName           :String:新客户经理名称</tt><br>
     *      	<tt>newCustManagerMobile         :String:新客户经理手机号</tt><br>
     *      	<tt>newCustManagerCredentialsCode:String:新客户经理身份证号</tt><br>
     */
	@Override
	public List<Map<String, Object>> queryCustTransferDetailById(
			Map<String, Object> param) {
		SqlCondition sqlCon = new SqlCondition(new StringBuilder(), param)
		.addSql("  SELECT ca.ID \"custApplyId\", c.ID \"custId\", c.LOGIN_NAME \"loginName\", c.CUST_NAME \"custName\", c.CREDENTIALS_CODE \"credentialsCode\", c.MOBILE \"mobile\", c.CREATE_DATE \"createDate\", c.CREATE_DATE \"registerDate\" ")
		.addSql("     , uOld.ID \"oldCustManagerId\", uOld.CUST_NAME \"oldCustManagerName\", uOld.MOBILE \"oldCustManagerMobile\", uOld.CREDENTIALS_CODE \"oldCustManagerCredentialsCode\" ")
		.addSql("     , uNew.ID \"newCustManagerId\", uNew.CUST_NAME \"newCustManagerName\", uNew.MOBILE \"newCustManagerMobile\", uNew.CREDENTIALS_CODE \"newCustManagerCredentialsCode\" ")
		.addSql("    FROM BAO_T_CUST_INFO c, BAO_T_CUST_APPLY_INFO ca ")
		.addSql("    LEFT JOIN BAO_T_CUST_INFO uOld ON uOld.ID = ca.CUST_ID ")
		.addSql("    LEFT JOIN BAO_T_CUST_INFO uNew ON uNew.ID = ca.NEW_CUST_MANAGER_ID ")
		.addSql("   WHERE ca.TRANSFER_CUST_ID = c.ID ")
		.addString("custApplyId", "ca.ID");
		
		return repositoryUtil.queryForMap(sqlCon.toString(), sqlCon.toArray());
	}

	/**
	 * 附件信息
	 *
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param relType String RELATE_TYPE
	 * @param relId String RELATE_PRIMARY
	 * @return List :attachmentList
	 * 			<tt>attachmentId  :String:附件ID</tt><br>
     *      	<tt>attachmentType:String:附件类型</tt><br>
     *      	<tt>attachmentName:String:附件名称</tt><br>
     *      	<tt>storagePath   :String:存储路径</tt><br>
     */
	@Override
	public List<Map<String, Object>> queryAttachmentInfoListById(
			String relType, String relId) {

		SqlCondition sqlCon = new SqlCondition(new StringBuilder(), new HashMap<String, Object>())
		.addSql(" SELECT achm.ID \"attachmentId\", achm.ATTACHMENT_TYPE \"attachmentType\", achm.ATTACHMENT_NAME \"attachmentName\", achm.STORAGE_PATH \"storagePath\" ")
		.addSql("   FROM BAO_T_ATTACHMENT_INFO achm ")
		.addSql("  WHERE achm.RECORD_STATUS = '" + Constant.VALID_STATUS_VALID + "' ")
		.addSql("    AND achm.RELATE_TYPE = '" + relType + "' ")
		.addSql("    AND achm.RELATE_PRIMARY = '" + relId + "' ");
		return repositoryUtil.queryForMap(sqlCon.toString(), sqlCon.toArray());
	}

	/**
	 * 审核信息
	 *
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param logType String 日志类型
	 * @param relType String 关联类型
	 * @param relId String 关联ID
	 * @return List :auditList
     *      	<tt>auditId    :String:审核ID</tt><br>
     *      	<tt>auditDate  :String:审核时间</tt><br>
     *      	<tt>auditUser  :String:审核人员</tt><br>
     *      	<tt>auditStatus:String:审核结果</tt><br>
     *      	<tt>auditMemo  :String:审核备注</tt><br>
     */
	@Override
	public List<Map<String, Object>> queryAuditInfoListById(
			String logType, String relType, String relId) {
		
		SqlCondition sqlCon = new SqlCondition(new StringBuilder(), new HashMap<String, Object>())
		.addSql("  SELECT log.ID \"auditId\", log.CREATE_DATE \"auditDate\", CASE WHEN c.CUST_NAME IS NOT NULL THEN c.CUST_NAME ELSE u.USER_NAME END \"auditUser\", log.OPER_AFTER_CONTENT \"auditStatus\", log.MEMO \"auditMemo\" ")
		.addSql("    FROM BAO_T_LOG_INFO log ")
		.addSql("    LEFT JOIN BAO_T_CUST_INFO c ON c.ID = log.OPER_PERSON ")
		.addSql("    LEFT JOIN COM_T_USER u ON u.ID = log.OPER_PERSON ")
		.addSql("   WHERE log.LOG_TYPE = '" + logType + "' ")
		.addSql("     AND log.RELATE_TYPE = '" + relType + "' ")
		.addSql("     AND log.RELATE_PRIMARY = '" + relId + "' ")
		.addSql("  ORDER BY log.CREATE_DATE DESC ");
		
		return repositoryUtil.queryForMap(sqlCon.toString(), sqlCon.toArray());
	}

	/**
	 * 我是业务员-客户管理-客户列表查询
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param param:Map
     *      <tt>start            :String:起始值</tt><br>
     *      <tt>length           :String:长度</tt><br>
     *      <tt>custId           :String:客户ID（客户经理）</tt><br>
     *      <tt>custName         :String:用户名（可以为空）</tt><br>
     *      <tt>credentialsCode  :String:证件号码（可以为空）</tt><br>
     *      <tt>mobile           :String:手机号（可以为空）</tt><br>
     *      <tt>beginRegisterDate:String:开始注册时间（可以为空）</tt><br>
     *      <tt>endRegisterDate  :String:结束注册时间（可以为空）</tt><br>
     * @param isSelf boolean:原客户经理发起
	 * @return Page:Map
     *      	<tt>iTotalDisplayRecords:String:总数</tt><br>
     *      	<tt>data:List
     *      		<tt>custId         :String:客户ID</tt><br>
     *      		<tt>custName       :String:用户名</tt><br>
     *      		<tt>credentialsCode:String:证件号码</tt><br>
     *      		<tt>mobile         :String:手机号</tt><br>
     *      		<tt>registerDate   :String:注册时间</tt><br>
     *          </tt><br>
     *      </tt><br>
     *  @throws SLException
	 */
	@Override
	public Page<Map<String, Object>> queryCustByManager(
			Map<String, Object> param) {
		
		SqlCondition sqlCon = new SqlCondition(new StringBuilder(), param)
		.addSql(" SELECT c.ID \"custId\", c.CUST_NAME \"custName\", decode(c.CREDENTIALS_CODE, null, '', substr(c.CREDENTIALS_CODE, 1, 4) || '****' || substr(c.credentials_code, -4)) \"credentialsCode\", decode(c.MOBILE, null , '', c.MOBILE) \"mobile\", c.CREATE_DATE \"registerDate\" ")
		.addSql("      , CASE WHEN (SELECT count(DISTINCT i.ID)  ")
		.addSql("                     FROM BAO_T_COMMISSION_INFO t ")
		.addSql("                        , bao_t_commission_detail_info s, BAO_T_INVEST_INFO i ")
		.addSql("                    WHERE i.ID = s.INVEST_ID ")
		.addSql("                      AND s.commission_id = t.id ")
		.addSql("                      and t.CUST_ID = cr.CUST_ID ")
		.addSql("                      and s.QUILT_CUST_ID = cr.QUILT_CUST_ID ")
		.addSql("                  ) > 0")
		.addSql("        THEN '已投资' ELSE '未投资' END \"investStatus\" ")
		.addSql("   FROM BAO_T_CUST_RECOMMEND_INFO cr, BAO_T_CUST_INFO c ")
		.addSql("  WHERE cr.CUST_ID = '" + param.get("custId").toString() + "'")
		.addSql("    AND cr.QUILT_CUST_ID = c.ID ")
		.addSql("    AND cr.RECORD_STATUS = '" + Constant.VALID_STATUS_VALID + "' ")
		.addSql("    AND c.IS_EMPLOYEE IS NULL ")
		.addString("custName", "c.CUST_NAME")
		.addString("credentialsCode", "c.CREDENTIALS_CODE")
		.addString("mobile", "c.MOBILE")
		.addBeginDate("beginRegisterDate", "c.CREATE_DATE")
		.addEndDate("endRegisterDate", "c.CREATE_DATE")
		
		.addSql("  ORDER BY c.CUST_NAME DESC ");
		return repositoryUtil.queryForPageMap(sqlCon.toString(), sqlCon.toArray(), Integer.parseInt(param.get("start").toString()), Integer.parseInt(param.get("length").toString()));
	}

	/**
	 * 我是业务员-客户管理-投资信息查询
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param param:Map
     *      <tt>start :String:起始值</tt><br>
     *      <tt>length:String:长度</tt><br>
     *      <tt>custId:String:客户ID</tt><br>
	 * @return Page:Map
     *      	<tt>iTotalDisplayRecords:String:总数</tt><br>
     *      	<tt>data:List
     *      		<tt>investDate  :String:投资时间</tt><br>
     *      		<tt>investAmount:String:投资金额</tt><br>
     *      		<tt>lendingType :String:计划名称</tt><br>
     *      		<tt>lendingNo   :String:项目期数</tt><br>
     *      		<tt>typeTerm    :String:项目期限(月)</tt><br>
     *      		<tt>yearRate    :String:年化收益率</tt><br>
     *      		<tt>awardRate   :String:奖励利率</tt><br>
     *      		<tt>effectDate  :String:生效日期</tt><br>
     *      		<tt>endDate     :String:到期日期</tt><br>
     *      		<tt>investStatus:String:投资状态（当前状态）</tt><br>
     *          </tt><br>
     *      </tt><br>
     *  @throws SLException
	 */
	@Override
	public Page<Map<String, Object>> queryCustWealthByManager(
			Map<String, Object> param) {

		SqlCondition sqlCon = new SqlCondition(new StringBuilder(), param)
		.addSql(" SELECT * FROM ( ")
		.addSql(" SELECT i.CREATE_DATE \"investDate\", i.INVEST_AMOUNT \"investAmount\", i.INVEST_STATUS \"investStatus\", ")
		.addSql("        w.LENDING_NO \"lendingNo\", w.AWARD_RATE \"awardRate\", w.EFFECT_DATE \"effectDate\", w.END_DATE \"endDate\", ")
		.addSql("        wt.LENDING_TYPE || w.LENDING_NO || '期' \"lendingType\", wt.TYPE_TERM \"typeTerm\", w.YEAR_RATE \"yearRate\" ")
		.addSql("      , '月' \"typeUnit\" ")
		.addSql("      , i.ID \"investId\" ")
		.addSql("      , '优选计划' AS \"productType\" ")
		.addSql("      , '普通标' \"newerFlag\" ")
		.addSql("   FROM BAO_T_INVEST_INFO i ")
		.addSql("   LEFT JOIN BAO_T_WEALTH_INFO w ")
		.addSql("          ON w.ID = i.WEALTH_ID ")
		.addSql("   LEFT JOIN BAO_T_WEALTH_TYPE_INFO wt ")
		.addSql("          ON wt.ID = w.WEALTH_TYPE_ID ")
		.addSql("  WHERE 1=1 ")
		.addSql("    and i.invest_status in ('收益中','提前赎回中','提前赎回','到期赎回中','到期赎回') ")
		.addSql("    AND i.WEALTH_ID IS NOT NULL ")
		.addString("custId", "i.CUST_ID") //条件
		.addSql(" UNION ALL   ")
		.addSql("  SELECT i.CREATE_DATE \"investDate\", i.INVEST_AMOUNT \"investAmount\" ")
		.addSql("       , decode(p.project_status, '发布中','投资中',  '满标复核','投资中', '还款中','收益中', '已逾期','收益中', '已到期','已结束', '提前结清','已结束', '流标','已结束') \"investStatus\" ")
		.addSql("       , p.PROJECT_NO \"lendingNo\", p.AWARD_RATE \"awardRate\", p.EFFECT_DATE \"effectDate\", p.PROJECT_END_DATE \"endDate\"  ")
		.addSql("       , p.PROJECT_NAME \"lendingType\", p.TYPE_TERM \"typeTerm\", p.YEAR_RATE \"yearRate\" ")
		.addSql("      , '月' \"typeUnit\" ")
		.addSql("      , i.ID \"investId\" ")
		.addSql("      , '企业借款' AS \"productType\" ")
		.addSql("      , '普通标' \"newerFlag\" ")
		.addSql("    FROM BAO_T_INVEST_INFO i  ")
		.addSql("    LEFT JOIN BAO_T_PROJECT_INFO p  ")
		.addSql("           ON p.ID = i.PROJECT_ID  ")
		.addSql("   WHERE 1=1  ")
		.addSql("     and p.project_status in ('还款中','已逾期','已到期','提前结清') ")
		.addSql("     AND i.PROJECT_ID IS NOT NULL  ")
		.addString("custId", "i.CUST_ID") //条件
		.addSql(" UNION ALL   ")
		.addSql(" SELECT i.CREATE_DATE \"investDate\", i.INVEST_AMOUNT \"investAmount\" ")
		.addSql("      , i.INVEST_STATUS \"investStatus\", loan.LOAN_CODE \"lendingNo\" ")
		.addSql("      , 0 \"awardRate\" ")
		.addSql("      , loan.INVEST_START_DATE \"effectDate\" ")
		.addSql("      , loan.INVEST_END_DATE \"endDate\"   ")
		.addSql("      , loan.LOAN_TITLE \"lendingType\" ")
		.addSql("      , loan.LOAN_TERM \"typeTerm\" ")
		.addSql("      , ld.YEAR_IRR \"yearRate\"  ")
		.addSql("      , loan.LOAN_UNIT \"typeUnit\" ")
		.addSql("      , i.ID \"investId\" ")
		.addSql("      , decode(i.invest_mode, '加入', '优选项目', '债权转让') AS \"productType\" ")
		.addSql("      , nvl(loan.newer_flag,'普通标') \"newerFlag\" ")
		.addSql("   FROM BAO_T_INVEST_INFO i ")
		.addSql("  INNER JOIN BAO_T_LOAN_INFO loan ON loan.ID = i.LOAN_ID ")
		.addSql("  INNER JOIN BAO_T_LOAN_DETAIL_INFO ld ON ld.LOAN_ID = loan.ID ")
		.addSql("  WHERE 1=1  ")
		.addSql("    and i.invest_status in ('收益中','提前结清','已到期', '已转让') ") // SLCF-1988 已转让状态的投资信息也需要展示出来
		.addSql("    AND i.LOAN_ID IS NOT NULL ")
		.addString("custId", "i.CUST_ID") //条件
		.addSql(" ) q ")
		.addSql(" where EXISTS (SELECT i.* ")
		.addSql("                 FROM BAO_T_COMMISSION_INFO t ")
		.addSql("                    , bao_t_commission_detail_info s ")
		.addSql("                    , BAO_T_INVEST_INFO i  ")
		.addSql("                WHERE i.ID = s.INVEST_ID ")
		.addSql("                  AND s.commission_id = t.id ")
		.addString("managerId", "t.CUST_ID") // 客户经理Id
		.addString("custId", "s.QUILT_CUST_ID") // 客户Id
		.addSql("                  AND i.ID = q.\"investId\")  ")
		.addSql("ORDER BY \"investDate\" DESC ")
		;
		
		return repositoryUtil.queryForPageMap(sqlCon.toString(), sqlCon.toArray(), Integer.parseInt(param.get("start").toString()), Integer.parseInt(param.get("length").toString()));
	}

	/**
	 * 我是业务员-附属银行卡-客户银行卡列表查询
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param param:Map
     *      <tt>start :String:起始值</tt><br>
     *      <tt>length:String:长度</tt><br>
     *      <tt>custId:String:客户ID</tt><br>
	 * @return Page:Map
     *      	<tt>iTotalDisplayRecords:String:总数</tt><br>
     *      	<tt>data:List
     *      		<tt>tradeFlowId    :String:交易过程流水ID</tt><br>
     *      		<tt>custName       :String:用户名</tt><br>
     *      		<tt>credentialsCode:String:证件号码</tt><br>
     *      		<tt>mobile         :String:手机号</tt><br>
     *      		<tt>bankName       :String:银行名称</tt><br>
     *      		<tt>bankCardNo     :String:银行卡号</tt><br>
     *      		<tt>branchBankName :String:支行名称</tt><br>
     *      		<tt>openProvince   :String:开户行所在省</tt><br>
     *      		<tt>openCity       :String:开户行所在市</tt><br>
     *      		<tt>auditStatus    :String:审核状态</tt><br>
     *          </tt><br>
     *      </tt><br>
     *  @throws SLException
	 */
	@Override
	public Page<Map<String, Object>> queryCustBankByManager(
			Map<String, Object> param) {

		SqlCondition sqlCon = new SqlCondition(new StringBuilder(), param)
		.addSql(" SELECT tf.ID \"tradeFlowId\", c.CUST_NAME \"custName\", c.CREDENTIALS_CODE \"credentialsCode\", c.MOBILE \"mobile\" ")
		.addSql("       , tf.BANK_NAME \"bankName\", tf.BANK_CARD_NO \"bankCardNo\", tf.BRANCH_BANK_NAME \"branchBankName\" ")
		.addSql("       , pa1.PARAMETER_NAME \"openProvince\", pa2.PARAMETER_NAME \"openCity\" ")
		.addSql("       , a.AUDIT_STATUS  \"auditStatus\" ")
		.addSql("   FROM BAO_T_CUST_INFO c ")
		.addSql("   INNER JOIN BAO_T_TRADE_FLOW_INFO tf ")
		.addSql("          ON tf.RELATE_TYPE = '" + Constant.TABLE_BAO_T_CUST_INFO + "' ")
		.addSql("         AND tf.TRADE_TYPE = '" + Constant.OPERATION_TYPE_44 + "' ")
		.addSql("         AND tf.RELATE_PRIMARY = c.ID ")
//		.addSql("   LEFT JOIN COM_T_PARAM pa0 ")
//		.addSql("          ON pa0.\"TYPE\" = '8' ")
// 		.addSql("         AND pa0.value = tf.BANK_NAME ")
		.addSql("   LEFT JOIN COM_T_PARAM pa1 ")
		.addSql("          ON pa1.\"TYPE\" = 'province' ")
 		.addSql("         AND pa1.value = tf.OPEN_PROVINCE ")
		.addSql("   LEFT JOIN COM_T_PARAM pa2 ")
		.addSql("          ON pa2.\"TYPE\" = 'city' ")
 		.addSql("         AND pa2.value = tf.OPEN_CITY ")
		.addSql("   LEFT JOIN BAO_T_AUDIT_INFO a ")
		.addSql("          ON a.RELATE_TYPE = '" + Constant.TABLE_BAO_T_TRADE_FLOW_INFO + "' ")
		.addSql("         AND a.RELATE_PRIMARY = tf.ID ")
		.addSql("  WHERE 1=1 ")
		.addString("custId", "c.ID")
		.addSql("  ORDER BY a.CREATE_DATE DESC ")
		;
		return repositoryUtil.queryForPageMap(sqlCon.toString(), sqlCon.toArray(), Integer.parseInt(param.get("start").toString()), Integer.parseInt(param.get("length").toString()));
	}

	/**
	 * 我是业务员-附属银行卡-查看客户银行卡-基本信息
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param param:Map
     *      <tt>tradeFlowId:String:交易过程流水ID</tt><br>
	 * @return List< Map< String, Object>> <br>
     *      	<tt>tradeFlowId    :String:交易过程流水ID</tt><br>
     *      	<tt>custId         :String:客户ID</tt><br>
     *      	<tt>custName       :String:用户名</tt><br>
     *      	<tt>credentialsCode:String:证件号码</tt><br>
     *      	<tt>mobile         :String:手机号</tt><br>
     *      	<tt>bankCode       :String:银行名称code</tt><br>
     *      	<tt>bankName       :String:银行名称</tt><br>
     *      	<tt>bankCardNo     :String:银行卡号</tt><br>
     *      	<tt>branchBankName :String:支行名称</tt><br>
     *      	<tt>openProvince   :String:开户行所在省code</tt><br>
     *      	<tt>openProvinceName   :String:开户行所在省</tt><br>
     *      	<tt>openCity       :String:开户行所在市code</tt><br>
     *      	<tt>openCityName       :String:开户行所在市</tt><br>
     *      	<tt>tradeStatus       :String:当前状态</tt><br>
     */
	@Override
	public List<Map<String, Object>> queryCustBankDetailByManager(
			Map<String, Object> param) {
		
		SqlCondition sqlCon = new SqlCondition(new StringBuilder(), param)
		.addSql(" SELECT tf.ID \"tradeFlowId\", tf.BANK_NAME \"bankName\", tf.BANK_CARD_NO \"bankCardNo\", tf.BRANCH_BANK_NAME \"branchBankName\" ")
		.addSql("      , pa0.VALUE \"bankCode\" ")
		.addSql("      , tf.OPEN_PROVINCE \"openProvince\", tf.OPEN_CITY \"openCity\" ")
		.addSql("      , pa1.PARAMETER_NAME \"openProvinceName\", pa2.PARAMETER_NAME \"openCityName\" ")
		.addSql("      , c.ID \"custId\", c.CUST_NAME \"custName\", c.CREDENTIALS_CODE \"credentialsCode\", c.MOBILE \"mobile\" ")
		.addSql("      , aud.AUDIT_STATUS \"tradeStatus\" ")
		.addSql("   FROM BAO_T_TRADE_FLOW_INFO tf ")
		.addSql("   LEFT JOIN COM_T_PARAM pa0 ")
		.addSql("          ON pa0.\"TYPE\" = '8' ")
 		.addSql("         AND pa0.PARAMETER_NAME = tf.BANK_NAME ")
		.addSql("   LEFT JOIN COM_T_PARAM pa1 ")
		.addSql("          ON pa1.\"TYPE\" = 'province' ")
 		.addSql("         AND pa1.VALUE = tf.OPEN_PROVINCE ")
		.addSql("   LEFT JOIN COM_T_PARAM pa2 ")
		.addSql("          ON pa2.\"TYPE\" = 'city' ")
 		.addSql("         AND pa2.VALUE = tf.OPEN_CITY ")
		.addSql("   LEFT JOIN BAO_T_CUST_INFO c ")
		.addSql("          ON c.ID = tf.RELATE_PRIMARY ")
		.addSql("   LEFT JOIN BAO_T_AUDIT_INFO aud ")
		.addSql("          ON aud.RELATE_TYPE = '"+Constant.TABLE_BAO_T_TRADE_FLOW_INFO+"' ")
		.addSql("         AND aud.RELATE_PRIMARY = tf.ID ")
		.addSql("  WHERE 1=1 ")
		.addString("tradeFlowId", "tf.ID");
		
		return repositoryUtil.queryForMap(sqlCon.toString(), sqlCon.toArray());
	}

	/**
	 * 根据类型和code,取名称
	 * @author  liyy
	 * @param type :String:类型
	 * @param code :String:code
	 * @return String
	 */
	@Override
	public String getNameByCode(String type, String code) {
		
		StringBuilder sql = new StringBuilder()
		.append(" SELECT PARAMETER_NAME ")
		.append("  FROM COM_T_PARAM ")
		.append(" WHERE \"TYPE\"= '" + type + "' ")
		.append("   AND VALUE = '" + code + "' ");
		return jdbcTemplate.queryForObject(sql.toString(), String.class);
	}

	/**
	 * 判断同一客户同一卡号不能重复存在
	 * @author  liyy
	 * @param custId :String:客户Id
	 * @param cardNo :String:卡号
	 * @return int
	 */
	@SuppressWarnings("deprecation")
	@Override
	public int checkCustAndCardNo(String custId, String cardNo) {
		
		StringBuilder sql = new StringBuilder()
		.append(" SELECT count(1) ")
		.append("   FROM BAO_T_TRADE_FLOW_INFO tf, BAO_T_AUDIT_INFO aud  ")
		.append("  WHERE aud.RELATE_TYPE = 'BAO_T_TRADE_FLOW_INFO'  ")
		.append("    AND aud.RELATE_PRIMARY = tf.ID ")
		.append("    AND aud.AUDIT_STATUS IN ('"+Constant.BANK_AUDIT_STATUS_PASS+"','"+Constant.BANK_AUDIT_STATUS_UNREVIEW+"') ")
		.append("    AND tf.TRADE_TYPE = '"+Constant.OPERATION_TYPE_44+"' ")
		.append("    AND tf.CUST_ID = '"+custId+"' ")
		.append("    AND tf.BANK_CARD_NO = '"+cardNo+"' ");
		
		return jdbcTemplate.queryForInt(sql.toString());
	}

	/**
	 * 校验转移是否存在
	 * @author  liyy
	 * @param mgrId :String:原客户经理
	 * @param custId :String:客户Id
	 * @return int
	 */
	@SuppressWarnings("deprecation")
	@Override
	public int countCheckMgrAndCust(String mgrId, String custId) {
		StringBuilder sql = new StringBuilder()
		.append(" SELECT count(1) count ")
		.append("   FROM BAO_T_CUST_APPLY_INFO ca, BAO_T_AUDIT_INFO aud ")
		.append("  WHERE ca.APPLY_TYPE = '"+Constant.OPERATION_TYPE_54+"' ")
		.append("    AND aud.RELATE_TYPE = '"+Constant.TABLE_BAO_T_CUST_APPLY_INFO+"' ")
		.append("    AND aud.RELATE_PRIMARY = ca.ID ")
		.append("    AND aud.AUDIT_STATUS IN ('"+Constant.CUST_MANAGER_AUDIT_STATUS_FALLBACK+"', '"+Constant.CUST_MANAGER_AUDIT_STATUS_UNREVIEW+"') ");
		if(StringUtils.isEmpty(mgrId)) {
			sql.append("    AND ca.CUST_ID IS NULL ");
		}else {
			sql.append("    AND ca.CUST_ID = '"+mgrId+"' ");
		}
		sql.append("    AND ca.TRANSFER_CUST_ID = '"+custId+"' ");
		
		return jdbcTemplate.queryForInt(sql.toString());
	}
}
