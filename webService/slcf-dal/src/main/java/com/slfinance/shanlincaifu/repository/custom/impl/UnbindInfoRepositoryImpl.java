/** 
 * @(#)UnbindInfoRepositoryImpl.java 1.0.0 2015年7月9日 下午2:04:31  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

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
import com.slfinance.shanlincaifu.entity.UnbindInfoEntity;
import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.UnbindInfoRepositoryCustom;
import com.slfinance.shanlincaifu.utils.DateUtils;
import com.slfinance.shanlincaifu.utils.SharedUtil;

/**   
 * 银行卡解绑自定义数据访问接口实现类
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年7月9日 下午2:04:31 $ 
 */
@Repository
public class UnbindInfoRepositoryImpl implements UnbindInfoRepositoryCustom {

	@Autowired
	private RepositoryUtil repositoryUtil;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public Page<Map<String, Object>> findUnBindList(Map<String, Object> param) {

		StringBuffer sqlString= new StringBuffer()
		.append(" select ID \"unbindId\", RELATE_PRIMARY \"bankId\", UNBIND_TYPE \"unbindType\", UNBIND_STATUS \"unbindStatus\", UNBIND_DESC \"unbindDesc\", CREATE_DATE  \"createDate\" ")
		.append(" from BAO_T_UNBIND_INFO t ")
		.append(" where 1 = 1 ");
		
		
		List<Object> objList = new ArrayList<Object>();
		if(!StringUtils.isEmpty(param.get("custId"))){
			sqlString.append(" and CUST_ID = ?");
			objList.add(param.get("custId"));
		}
		
		if(!StringUtils.isEmpty(param.get("bankId"))){
			sqlString.append(" and RELATE_PRIMARY = ?");
			objList.add(param.get("bankId"));
		}

		sqlString.append(" order by CREATE_DATE desc ");
		
		return repositoryUtil.queryForPageMap(sqlString.toString(), objList.toArray(), (int)param.get("start"), (int)param.get("length"));
	}

	@Override
	public Map<String, Object> findUnBindById(Map<String, Object> param) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		// 取得申请信息
		StringBuffer sqlString= new StringBuffer()
		.append(" select ID \"unbindId\", RELATE_PRIMARY \"bankId\", UNBIND_TYPE \"unbindType\", UNBIND_STATUS \"unbindStatus\", UNBIND_DESC \"unbindDesc\", CREATE_DATE  \"createDate\" ")
		.append(" from BAO_T_UNBIND_INFO t ")
		.append(" where id = ? ");
		
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sqlString.toString(), new Object[] { (String)param.get("unbindId")});
		if(list == null || list.size() == 0) {
			return result;
		}
		result.putAll(list.get(0));

		// 取附件信息
		sqlString = new StringBuffer()
		.append("  select ID \"attachmentId\", ATTACHMENT_TYPE \"attachmentType\", ATTACHMENT_NAME \"attachmentName\", STORAGE_PATH \"storagePath\", DOC_TYPE \"docType\", CREATE_DATE \"createDate\" ")
		.append("  from BAO_T_ATTACHMENT_INFO T ")
		.append("  where T.RELATE_PRIMARY = ? ");
		
		list = repositoryUtil.queryForMap(sqlString.toString(), new Object[] { (String)param.get("unbindId")});
		result.put("attachmentList", list);
		
		return result;
	}

	@Override
	public Page<Map<String, Object>> findAllUnBindList(Map<String, Object> param) {
	
		StringBuffer sqlString= new StringBuffer()
		.append(" select t.id \"unbindId\", t.relate_primary \"bankId\", s.id \"custId\", s.login_name \"loginName\", s.cust_name \"custName\",  ")
		.append("        s.credentials_code \"credentialsCode\", s.mobile \"mobile\", t.create_date \"createDate\", ")
		.append("        m.trade_status \"unbindStatus\", m.audit_status  \"auditStatus\" ")
		.append(" from bao_t_unbind_info t, bao_t_cust_info s, bao_t_audit_info m ")
		.append(" where t.cust_id = s.id and m.relate_primary = t.id ");
		
		List<Object> objList = new ArrayList<Object>();
		if(!StringUtils.isEmpty(param.get("custName"))){
			sqlString.append(" and s.cust_name = ?");
			objList.add(param.get("custName"));
		}
		
		if(!StringUtils.isEmpty(param.get("loginName"))){
			sqlString.append(" and s.login_name = ?");
			objList.add(param.get("loginName"));
		}
		
		if(!StringUtils.isEmpty(param.get("credentialsCode"))){
			sqlString.append(" and s.credentials_code = ?");
			objList.add(param.get("credentialsCode"));
		}
		
		if(!StringUtils.isEmpty(param.get("mobile"))){
			sqlString.append(" and s.mobile = ?");
			objList.add(param.get("mobile"));
		}
		
		if(!StringUtils.isEmpty(param.get("opearteDateBegin"))){
			sqlString.append(" and t.create_date >= ?");
			objList.add(DateUtils.parseStandardDate((String)param.get("opearteDateBegin")));
		}

		if(!StringUtils.isEmpty(param.get("opearteDateEnd"))){
			sqlString.append(" and t.create_date <= ?");
			objList.add(DateUtils.getEndDate(DateUtils.parseStandardDate((String)param.get("opearteDateEnd"))));
		}
		
		if(!StringUtils.isEmpty(param.get("unbindStatus"))){
			sqlString.append(" and m.trade_status = ?");
			objList.add(param.get("unbindStatus"));
		}
		
		if(!StringUtils.isEmpty(param.get("auditStatus"))){
			sqlString.append(" and m.audit_status = ?");
			objList.add(param.get("auditStatus"));
		}

		sqlString.append(" order by t.create_date desc ");
		
		return repositoryUtil.queryForPageMap(sqlString.toString(), objList.toArray(), (int)param.get("start"), (int)param.get("length"));
	}

	@Override
	public Map<String, Object> findUserBankInfo(Map<String, Object> param) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		StringBuffer sqlString= new StringBuffer()
		.append(" select s.id \"custId\", s.login_name \"loginName\", s.cust_name \"custName\",  ")
		.append("        s.credentials_code \"credentialsCode\", s.mobile \"mobile\", s.credentials_type \"credentialsType\", ")
		.append("        s.cust_gender \"custGender\", t.bank_name \"bankName\", t.card_no \"cardNo\" ")
		.append("  from bao_t_bank_card_info t, bao_t_cust_info s ")
		.append("  where t.cust_id = s.id and t.id = ? ");
		
		List<Map<String, Object>> list = repositoryUtil.queryForMap(sqlString.toString(), new Object[] { (String)param.get("bankId")});
		if(list == null || list.size() == 0) {
			return result;
		}
		
		return list.get(0);
	}

	@Override
	public List<Map<String, Object>> findUnBindByCustId(
			Map<String, Object> param) {
		
		StringBuffer sqlString= new StringBuffer()
		.append("  select t.id \"unbindId\", t.create_date \"createDate\", s.bank_name \"bankName\", s.card_no \"cardNo\", ")
		.append("        t.unbind_type \"unbindType\", m.audit_time \"auditTime\", m.audit_user \"auditUser\", ")
		.append("        (select a.user_name from com_t_user a where a.id = m.audit_user ) \"auditUserName\", ")
		.append("        m.audit_status \"auditStatus\", m.memo \"memo\" ")
		.append("  from bao_t_unbind_info t, bao_t_bank_card_info s, bao_t_audit_info m ")
		.append("  where t.relate_primary = s.id and t.id = m.relate_primary and t.cust_id = ? ")
		.append("  order by t.create_date desc ");
		
		return repositoryUtil.queryForMap(sqlString.toString(), new Object[] { (String)param.get("custId")});
	}

	@Override
	public UnbindInfoEntity insertUnBindInfo(UnbindInfoEntity unbindInfoEntity) throws SLException{
		StringBuffer sqlString= new StringBuffer()
		.append(" insert into bao_t_unbind_info ")
		.append("   (id, relate_type, relate_primary,  ")
		.append("    unbind_code, unbind_type, unbind_status,  ")
		.append("    unbind_desc, record_status, create_user,  ")
		.append("    create_date, last_update_user, last_update_date,  ")
		.append("    version, memo, cust_id) ")
		.append(" select ")
		.append("    ?, ?, ?,  ")
		.append("    ?, ?, ?,  ")
		.append("    ?, ?, ?,  ")
		.append("    ?, ?, ?,  ")
		.append("    ?, ?, ? ")
		.append(" from dual ")
		.append(" where not exists ( ")
		.append("    select 1 ")
		.append("    from bao_t_unbind_info t  ")
		.append("    where t.relate_primary = ? ")
		.append("    and to_char(t.create_date, 'yyyyMMddhh24miss') = to_char(?, 'yyyyMMddhh24miss') ")
		.append(" ) ");
		
		unbindInfoEntity.setId(SharedUtil.getUniqueString());
		
		int rows = jdbcTemplate.update(sqlString.toString(), 
				new Object[]{	unbindInfoEntity.getId(),
								unbindInfoEntity.getRelateType(),
								unbindInfoEntity.getRelatePrimary(),
								unbindInfoEntity.getUnbindCode(),
								unbindInfoEntity.getUnbindType(),
								unbindInfoEntity.getUnbindStatus(),
								unbindInfoEntity.getUnbindDesc(),
								unbindInfoEntity.getRecordStatus(),
								unbindInfoEntity.getCreateUser(),
								unbindInfoEntity.getCreateDate(),
								unbindInfoEntity.getLastUpdateUser(),
								unbindInfoEntity.getLastUpdateDate(),
								unbindInfoEntity.getVersion(),
								unbindInfoEntity.getMemo(),
								unbindInfoEntity.getCustId(),
								unbindInfoEntity.getRelatePrimary(),
								unbindInfoEntity.getCreateDate()});	
		if(rows != 1) {
			throw new SLException("插入解绑信息失败");
		}
		
		return unbindInfoEntity;
	}

}
