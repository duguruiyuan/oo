/** 
 * @(#)BankCardInfoRepositoryImpl.java 1.0.0 2015年4月28日 上午10:46:21  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository.custom.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Repository;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.BankCardInfoEntity;
import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.BankCardInfoRepositoryCustom;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.SqlCondition;

/**   
 * 自定义银行卡数据访问类
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年4月28日 上午10:46:21 $ 
 */
@Repository
public class BankCardInfoRepositoryImpl implements BankCardInfoRepositoryCustom{

	@Autowired
	private RepositoryUtil repositoryUtil;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public String findByBankName(String bankName) {
		StringBuffer sqlString= new StringBuffer()
		.append(" select t.value \"value\" from com_t_param t where t.type = '8' and t.type_name = '银行名称' and t.parameter_name = ?");
		
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sqlString.toString(), new Object[]{bankName});
		if(list == null || list.size() == 0)
			return "";
		Map<String, Object> map = list.get(0);
		if(map == null || map.size() == 0)
			return "";
		return (String)map.get("value");
	}

	@Override
	public String findByBankCode(String bankCode) {
		StringBuffer sqlString= new StringBuffer()
		.append(" select t.parameter_name \"parameterName\" from com_t_param t where t.type = '8' and t.type_name = '银行名称' and t.value = ?");
		
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sqlString.toString(), new Object[]{bankCode});
		if(list == null || list.size() == 0)
			return "";
		Map<String, Object> map = list.get(0);
		if(map == null || map.size() == 0)
			return "";
		return (String)map.get("parameterName");
	}

	@Override
	public Map<String, Object> findByCustId(String custId) {
		StringBuffer sqlString= new StringBuffer()
		.append(" SELECT A.ID \"id\", A.CUST_ID \"custId\", A.BANK_NAME \"bankName\", A.CARD_NO \"cardNo\",  ")
		.append("        A.OPEN_PROVINCE \"openProvince\", A.OPEN_CITY \"openCity\",  ")
		.append("        A.SUB_BRANCH_NAME \"subBranchName\", A.PROTOCOL_NO \"protocolNo\",       ")
		.append("        (SELECT T.VALUE FROM COM_T_PARAM T WHERE T.TYPE = '8' AND T.TYPE_NAME = '银行名称' AND T.PARAMETER_NAME = A.BANK_NAME) \"bankCode\", ")
		.append("        (SELECT T.PARAMETER_NAME FROM COM_T_PARAM T WHERE T.TYPE = 'province' AND T.TYPE_NAME = '省' AND T.VALUE = A.OPEN_PROVINCE) \"provinceName\", ")
		.append("        (SELECT T.PARAMETER_NAME FROM COM_T_PARAM T WHERE T.TYPE = 'city' AND T.TYPE_NAME = '城市' AND T.VALUE = A.OPEN_CITY) \"cityName\" ")
		.append(" FROM ")
		.append(" ( ")
		.append(" SELECT T.ID, T.CUST_ID, T.BANK_NAME, T.CARD_NO, T.OPEN_PROVINCE, T.OPEN_CITY, T.SUB_BRANCH_NAME, T.PROTOCOL_NO, ROWNUM ROW_NUM ")
		.append(" FROM BAO_T_BANK_CARD_INFO T  ")
		.append(" WHERE T.CUST_ID = ? AND RECORD_STATUS = '有效' and T.bank_flag = '线上' ")
		.append(" ORDER BY T.CREATE_DATE DESC ")
		.append(" )A ")
		.append(" WHERE ROW_NUM = 1 ");
		
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sqlString.toString(), new Object[]{custId});
		if(list == null || list.size() == 0)
			return new HashMap<String, Object>();
		Map<String, Object> map = list.get(0);
		if(map == null || map.size() == 0)
			return new HashMap<String, Object>();

		return map;
	}

	@Override
	public Page<Map<String, Object>> findBankList(Map<String, Object> param) {
		
		StringBuffer sqlString= new StringBuffer()
		.append(" select id \"id\", cust_id \"custId\", bank_name \"bankName\", card_no \"cardNo\", record_status \"recordStatus\",  ")
		.append("        (select t.value  ")
		.append("         from com_t_param t  ")
		.append("         where t.type = '8'  ")
		.append("         and t.type_name = '银行名称'  ")
		.append("         and t.parameter_name = bank_name ) \"bankCode\", last_update_date ")
		.append(" from  ")
		.append(" ( ")
		.append("   select t.id, t.cust_id, t.bank_name, t.card_no, '已绑定' record_status, t.last_update_date  ")
		.append("   from bao_t_bank_card_info t  ")
		.append("   where t.record_status = '有效'  ")
		.append("   and not exists (select 1 from bao_t_unbind_info s  ")
		.append("                   where s.relate_primary = t.id and s.unbind_status = '处理中') ")
		.append("   union ")
		.append("   select t.id, t.cust_id, t.bank_name, t.card_no, '解绑审核中' record_status, t.last_update_date  ")
		.append("   from bao_t_bank_card_info t  ")
		.append("   where t.record_status = '有效'  ")
		.append("   and exists (select 1 from bao_t_unbind_info s  ")
		.append("               where s.relate_primary = t.id and s.unbind_status = '处理中') ")
		.append("   union ")
		.append("   select t.id, t.cust_id, t.bank_name, t.card_no, '已解绑' record_status, t.last_update_date  ")
		.append("   from bao_t_bank_card_info t  ")
		.append("   where t.record_status = '无效'  ")
		.append("   and exists (select 1 from bao_t_unbind_info s  ")
		.append("               where s.relate_primary = t.id and s.unbind_status = '处理成功') ")
		.append(" )a ")
		.append(" where  cust_id = ?")
		.append(" order by record_status asc, last_update_date desc");
		
		return repositoryUtil.queryForPageMap(sqlString.toString(), new Object[]{(String)param.get("custId")}, (int)param.get("start"), (int)param.get("length"));
	}

	@Override
	public void insertBank(final BankCardInfoEntity bankCardInfoEntity)
			throws SLException {
		
		StringBuffer sql = new StringBuffer()
		.append(" insert into bao_t_bank_card_info                                               ")
		.append("   (id, cust_id, bank_name, card_no,                                            ")
		.append("    open_province, open_city, sub_branch_name, is_default,                      ")
		.append("    record_status, create_user, create_date, last_update_user, last_update_date,")
		.append("    version, memo, protocol_no, bank_flag)                                      ")
		.append(" select                                                                         ")
		.append("    sys_guid(), ?, ?, ?,                                                        ")
		.append("    null, null, null, '1',                                                      ")
		.append("    '无效', ?, ?, ?, ?,                                                         ")
		.append("    0, null, null, ?                                                            ")
		.append(" from dual                                                                      ")
		.append(" where not exists (                                                             ")
		.append("       select 1                                                                 ")
		.append("       from bao_t_bank_card_info                                                ")
		.append("       where cust_id = ? and card_no = ? and bank_flag = ?                      ")
		.append(" )                                                                              ");
		
		int rows = jdbcTemplate.update(sql.toString(), new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				
				ps.setString(1, bankCardInfoEntity.getCustInfoEntity().getId());
				ps.setString(2, bankCardInfoEntity.getBankName());
				ps.setString(3, bankCardInfoEntity.getCardNo());
				ps.setString(4, bankCardInfoEntity.getCreateUser());
				ps.setTimestamp(5, new Timestamp(bankCardInfoEntity.getCreateDate().getTime()));
				ps.setString(6, bankCardInfoEntity.getLastUpdateUser());
				ps.setTimestamp(7, new Timestamp(bankCardInfoEntity.getLastUpdateDate().getTime()));
				ps.setString(8, bankCardInfoEntity.getBankFlag());
				ps.setString(9, bankCardInfoEntity.getCustInfoEntity().getId());
				ps.setString(10, bankCardInfoEntity.getCardNo());
				ps.setString(11, bankCardInfoEntity.getBankFlag());
				
			}
		});
		
		if(rows != 1) {
			throw new SLException("插入银行卡失败");
		}
	}

	@Override
	public List<Map<String, Object>> findBankCardList(Map<String, Object> param) {
		
		StringBuilder sql = new StringBuilder()
		.append("  select id \"bankId\", cust_id \"custId\", bank_name \"bankName\", card_no \"bankCardNo\", record_status \"recordStatus\",   ")
		.append("         (select t.value   ")
		.append("          from com_t_param t   ")
		.append("          where t.type = '8'   ")
		.append("          and t.type_name = '银行名称'   ")
		.append("          and t.parameter_name = bank_name ) \"bankCode\", bank_flag \"bankFlag\", last_update_date \"lastUpdateDate\" ")
		.append("  from   ")
		.append("  (  ")
		.append("    select t.id, t.cust_id, t.bank_name, t.card_no, '绑定中' record_status, t.bank_flag, t.last_update_date ")
		.append("    from bao_t_bank_card_info t   ")
		.append("    where t.record_status = '有效'  and t.bank_flag = '线上' ")
		.append("    and not exists (select 1 from bao_t_unbind_info s   ")
		.append("                    where s.relate_primary = t.id and s.unbind_status = '处理中')  ")
		.append("    union  ")
		.append("    select t.id, t.cust_id, t.bank_name, t.card_no, '解绑中' record_status, t.bank_flag, t.last_update_date   ")
		.append("    from bao_t_bank_card_info t   ")
		.append("    where t.record_status = '有效'  and t.bank_flag = '线上' ")
		.append("    and exists (select 1 from bao_t_unbind_info s   ")
		.append("                where s.relate_primary = t.id and s.unbind_status = '处理中')  ")
		.append("    union  ")
		.append("    select t.id, t.cust_id, t.bank_name, t.card_no, '已解绑' record_status, t.bank_flag, t.last_update_date   ")
		.append("    from bao_t_bank_card_info t   ")
		.append("    where t.record_status = '无效'  and t.bank_flag = '线上' ")
		.append("    and exists (select 1 from bao_t_unbind_info s   ")
		.append("                where s.relate_primary = t.id and s.unbind_status = '处理成功')  ")
		.append("    union ")
		.append("    select t.id, t.cust_id, t.bank_name, t.card_no, '绑定中' record_status, t.bank_flag, t.last_update_date   ")
		.append("    from bao_t_bank_card_info t   ")
		.append("    where t.record_status = '有效'  and t.bank_flag = '线下' ")
		.append("  ) btbci where 1 = 1 ");
		
		SqlCondition sqlCondition = new SqlCondition(sql, param);
		sqlCondition.addString("custId", "btbci.cust_id")
					.addString("bankId", "btbci.id")
					.addString("bankFlag", "bank_flag")
					.addSql("order by decode (btbci.bank_flag, '"+Constant.BANK_FLAG_ONLINE+"', 1, '"+Constant.BANK_FLAG_OFFLINE+"', 2), btbci.last_update_date desc");
			
		return repositoryUtil.queryForMap(sqlCondition.toString(), sqlCondition.toArray());
	}

	/**
	 * 线下业务-附属银行卡-客户银行卡审核列表
	 *
	 * @author  liyy
	 * @date    2016年2月24日 
	 * @param param
     *      <tt>start          :String:起始值</tt><br>
     *      <tt>length         :String:长度</tt><br>
     *      <tt>custName       :String:用户名（可选）</tt><br>
     *      <tt>credentialsCode:String:证件号码(可选)</tt><br>
     *      <tt>mobile         :String:手机号（可选）</tt><br>
     *      <tt>auditStatus    :String:审核状态（可选）</tt><br>
     *      <tt>bankCardNo     :String:银行卡号（可选）</tt><br>
     * @return Page
     */
	@Override
	public Page<Map<String, Object>> queryWealthBankList(
			Map<String, Object> param) {
		StringBuilder sqlString = new StringBuilder()
		.append("  SELECT 	tf.ID \"tradeFlowId\", ")
		.append(" 	tf.BANK_NAME \"bankName\", ")
		.append(" 	tf.BANK_CARD_NO \"bankCardNo\", ")
		.append(" 	tf.BRANCH_BANK_NAME \"branchBankName\", ")
		.append(" 	pa1.PARAMETER_NAME \"openProvince\", ")
		.append(" 	pa2.PARAMETER_NAME \"openCity\", ")
		.append(" 	c.MOBILE \"mobile\", ")
		.append(" 	c.CREDENTIALS_CODE \"credentialsCode\", ")
		.append(" 	c.CUST_NAME \"custName\", ")
		.append(" 	a.AUDIT_STATUS \"auditStatus\" ")
		.append("  FROM BAO_T_CUST_INFO c, BAO_T_TRADE_FLOW_INFO tf  ")
		.append("   LEFT JOIN COM_T_PARAM pa1 ")
		.append("          ON pa1.\"TYPE\" = 'province' ")
 		.append("         AND pa1.value = tf.OPEN_PROVINCE ")
		.append("   LEFT JOIN COM_T_PARAM pa2 ")
		.append("          ON pa2.\"TYPE\" = 'city' ")
 		.append("         AND pa2.value = tf.OPEN_CITY ")
		.append(" 	, BAO_T_AUDIT_INFO a  ")
		.append("  WHERE tf.TRADE_TYPE = '" + Constant.OPERATION_TYPE_44 + "' ")
		.append("   AND tf.CUST_ID = c.ID ")
		.append("   AND a.RELATE_TYPE = '" + Constant.TABLE_BAO_T_TRADE_FLOW_INFO + "' ")
		.append("   AND a.RELATE_PRIMARY = tf.ID ");
		
		SqlCondition sqlCon = new SqlCondition(sqlString, param)
		.addString("custName", "c.CUST_NAME")
		.addString("credentialsCode", "c.CREDENTIALS_CODE")
		.addString("mobile", "c.MOBILE")
		.addString("auditStatus", "a.AUDIT_STATUS")
		.addString("bankCardNo", "tf.BANK_CARD_NO")
		.addSql("  ORDER BY decode(a.AUDIT_STATUS, '待审核', 1, '审核回退', 2, '通过', 3, '拒绝', 4, '作废', 5)")
		.addSql("         , tf.CREATE_DATE DESC ");

		return repositoryUtil.queryForPageMap(sqlCon.toString(), sqlCon.toArray(), Integer.parseInt(param.get("start").toString()), Integer.parseInt(param.get("length").toString()));
	}

	/**
	 * 线下业务-附属银行卡-客户银行卡查看详情-卡信息
	 *
	 * @author  liyy
	 * @date    2016年2月24日 
	 * @param param
     *      <tt>tradeFlowId    :String:交易过程流水ID</tt><br>
	 * @return List
	 * 		<tt>tradeFlowId    :String:交易过程流水ID</tt><br>
	 * 		<tt>custName    :String:客户名称</tt><br>
	 *  	<tt>credentialsCode    :String:证件号码</tt><br>
	 *  	<tt>mobile    :String:手机</tt><br>
	 *  	<tt>bankName    :String:银行名称</tt><br>
	 *  	<tt>bankCardNo    :String:银行卡号</tt><br>
	 *  	<tt>branchBankName    :String:支行名称</tt><br>
	 *  	<tt>openProvince    :String:开户省</tt><br>
	 *  	<tt>openCity    :String:开户市</tt><br>
	 */
	@Override
	public List<Map<String, Object>> queryBankDetailById(
			Map<String, Object> param) {
		
		SqlCondition sqlCon = new SqlCondition(new StringBuilder(), param)
		.addSql("  SELECT tf.ID \"tradeFlowId\", ")
		.addSql(" 	tf.BANK_NAME \"bankName\", ")
		.addSql(" 	tf.BANK_CARD_NO \"bankCardNo\", ")
		.addSql(" 	tf.BRANCH_BANK_NAME \"branchBankName\", ")
		.addSql(" 	pa1.PARAMETER_NAME \"openProvince\", ")
		.addSql(" 	pa2.PARAMETER_NAME \"openCity\", ")
		.addSql(" 	c.MOBILE \"mobile\", ")
		.addSql(" 	c.CREDENTIALS_CODE \"credentialsCode\", ")
		.addSql(" 	c.CUST_NAME \"custName\" ")
		.addSql("   FROM BAO_T_CUST_INFO c, BAO_T_TRADE_FLOW_INFO tf ")
		.addSql("   LEFT JOIN COM_T_PARAM pa1 ")
		.addSql("          ON pa1.\"TYPE\" = 'province' ")
 		.addSql("         AND pa1.ID = tf.OPEN_PROVINCE ")
		.addSql("   LEFT JOIN COM_T_PARAM pa2 ")
		.addSql("          ON pa2.\"TYPE\" = 'city' ")
 		.addSql("         AND pa2.ID = tf.OPEN_CITY ")
		.addSql("  WHERE tf.CUST_ID = c.ID ")
		.addString("tradeFlowId", "tf.ID");

		return repositoryUtil.queryForMap(sqlCon.toString(), sqlCon.toArray());
	}

	/**
	 * 线下业务-附属银行卡-客户银行卡查看详情-附件列表
	 *
	 * @author  liyy
	 * @date    2016年2月24日 
	 * @param param :Map
	 * 		<tt>tradeFlowId :String:交易过程流水ID</tt><br>
	 * 		<tt>custName :String:客户名称</tt><br>
	 * 		<tt>credentialsCode :String:证件号码</tt><br>
	 * 		<tt>mobile :String:手机</tt><br>
	 * 		<tt>bankName :String:银行名称</tt><br>
	 * 		<tt>bankCardNo :String:银行卡号</tt><br>
	 * 		<tt>branchBankName :String:支行名称</tt><br>
	 * 		<tt>openProvince :String:开户省</tt><br>
	 * 		<tt>openCity :String:开户市</tt><br>
	 * @return List
	 */
	@Override
	public List<Map<String, Object>> queryAttachmentInfoListById(
			Map<String, Object> param) {
		
		SqlCondition sqlCon = new SqlCondition(new StringBuilder(), param)
		.addSql(" SELECT achm.ATTACHMENT_TYPE \"attachmentType\", achm.ATTACHMENT_NAME \"attachmentName\", achm.STORAGE_PATH \"storagePath\" ")
		.addSql("   FROM BAO_T_ATTACHMENT_INFO achm ")
		.addSql("  WHERE achm.RECORD_STATUS = '" + Constant.VALID_STATUS_VALID + "' ")
		.addSql("    AND achm.RELATE_TYPE = '" + Constant.TABLE_BAO_T_TRADE_FLOW_INFO + "' ")
		.addString("tradeFlowId", "achm.RELATE_PRIMARY");
		return repositoryUtil.queryForMap(sqlCon.toString(), sqlCon.toArray());
	}

	/**
	 * 线下业务-附属银行卡-客户银行卡查看详情-审核列表
	 *
	 * @author  liyy
	 * @date    2016年2月24日 
	 * @param param :Map
	 * 		<tt>tradeFlowId :String:交易过程流水ID</tt><br>
	 * 		<tt>custName :String:客户名称</tt><br>
	 * 		<tt>credentialsCode :String:证件号码</tt><br>
	 * 		<tt>mobile :String:手机</tt><br>
	 * 		<tt>bankName :String:银行名称</tt><br>
	 * 		<tt>bankCardNo :String:银行卡号</tt><br>
	 * 		<tt>branchBankName :String:支行名称</tt><br>
	 * 		<tt>openProvince :String:开户省</tt><br>
	 * 		<tt>openCity :String:开户市</tt><br>
	 * @return List
	 */
	@Override
	public List<Map<String, Object>> queryAuditInfoListById(
			Map<String, Object> param) {
		
		SqlCondition sqlCon = new SqlCondition(new StringBuilder(), param)
		.addSql("  SELECT log.ID \"auditId\", log.CREATE_DATE \"auditDate\", CASE WHEN u.USER_NAME IS NOT NULL THEN u.USER_NAME ELSE c.CUST_NAME END \"auditUser\", log.OPER_AFTER_CONTENT \"auditStatus\", log.MEMO \"auditMemo\" ")
		.addSql("    FROM BAO_T_LOG_INFO log ")
		.addSql("    LEFT JOIN COM_T_USER u ON u.ID = log.OPER_PERSON ")
		.addSql("    LEFT JOIN BAO_T_CUST_INFO c ON c.ID = log.OPER_PERSON ")
		.addSql("   WHERE log.RELATE_TYPE = '" + Constant.TABLE_BAO_T_TRADE_FLOW_INFO + "' ")
		.addSql("     AND  log.LOG_TYPE = '" + Constant.OPERATION_TYPE_45 + "' ")
		.addString("tradeFlowId", "log.RELATE_PRIMARY")
		.addSql("  ORDER BY log.CREATE_DATE DESC ");
		return repositoryUtil.queryForMap(sqlCon.toString(), sqlCon.toArray());
	}

	@Override
	public List<Map<String, Object>> queryWithDrawBankList(
			Map<String, Object> param) {
		StringBuilder sql = new StringBuilder()
		.append(" select bcard.id              \"id\", ")
		.append("        bcard.cust_id         \"custId\", ")
		.append("        bcard.bank_name       \"bankName\", ")
		.append("        bcard.card_no         \"cardNo\", ")
		.append("        bcard.protocol_no     \"protocolNo\", ")
		.append("        bcard.open_province   \"openProvince\", ")
		.append("        bcard.open_city       \"openCity\", ")
		.append("        bcard.sub_branch_name \"subBranchName\", ")
		.append("        ctp.value             \"bankCode\", ")
		.append("        bcard.bank_flag       \"bankFlag\", ")
		.append("        pa1.PARAMETER_NAME    \"provinceName\", ")
		.append("        pa2.PARAMETER_NAME    \"cityName\"  ")
		.append("   from bao_t_bank_card_info bcard ")
		.append("   left join com_t_param ctp on ctp.parameter_name = bcard.bank_name and ctp.type = '8' ")
		.append("   LEFT JOIN COM_T_PARAM pa1 ")
		.append("          ON pa1.\"TYPE\" = 'province' ")
 		.append("         AND pa1.value = bcard.OPEN_PROVINCE ")
		.append("   LEFT JOIN COM_T_PARAM pa2 ")
		.append("          ON pa2.\"TYPE\" = 'city' ")
 		.append("         AND pa2.value = bcard.OPEN_CITY ")
		.append("  where bcard.record_status = '有效' ");

		SqlCondition sqlCondition = new SqlCondition(sql, param);
		sqlCondition.addString("custId", "bcard.cust_id")
					.addString("bankId", "bcard.id")
					.addSql("order by  decode(bcard.bank_flag, '线上', 1, '线下', 2), bcard.create_date desc");
		
		return repositoryUtil.queryForMap(sqlCondition.toString(), sqlCondition.toArray());
	}

	@Override
	public Map<String, Object> queryBankCardInfoByLoanId(String loanId) {
		StringBuilder sql = new StringBuilder()
		.append("    select lc.card_no \"cardNo\",lc.cust_name \"custName\",bci.bank_name \"bankName\",bci.sub_branch_name \"subBankName\" ,lc.HOME_ADDRESS \"homeAddress\" ")
		.append("    from bao_t_bank_card_info bci ")
		.append("    , bao_t_loan_info loan ")
		.append("    ,bao_t_loan_cust_info lc ")
		.append(" 			 where lc.ID = loan.RELATE_PRIMARY ")
		.append(" 			and loan.cust_id = bci.cust_id ")
		.append(" 			 and loan.id = ? ");
		List<Object> args = new ArrayList<Object>();
		args.add(loanId);
		List<Map<String, Object>> resuList = repositoryUtil.queryForMap(sql.toString(), args.toArray());
		return resuList==null?new HashMap<String, Object>():resuList.get(0);
	}

}
