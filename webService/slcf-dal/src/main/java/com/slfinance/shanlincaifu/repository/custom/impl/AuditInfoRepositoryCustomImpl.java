package com.slfinance.shanlincaifu.repository.custom.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.AuditInfoRepositoryCustom;
import com.slfinance.shanlincaifu.utils.Constant;

@Repository
public class AuditInfoRepositoryCustomImpl implements AuditInfoRepositoryCustom{
	
	@Autowired
	private RepositoryUtil repositoryUtil;

	@Override
	public List<Map<String, Object>> findByRelatePrimary(Map<String, Object> params) {
		List<Object> listObject = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder()
				.append("  select btai.audit_time                     \"auditTime\",  ")
				.append("         nvl(ctu.user_name, '系统管理员')    \"auditUser\",  ")
				.append("         btai.apply_type                     \"applyType\",  ")
				.append("         btai.audit_status                   \"auditStatus\",  ")
				.append("         btai.memo                           \"memo\", ")
				.append("         btai.id                             \"auditId\", ")
				.append("         btai.apply_type                     \"auditName\", ")
				.append("         nvl(ctu.user_name, '系统管理员')    \"auditUser\", ")
				.append("         to_char(btai.audit_time, 'yyyy-MM-dd HH:mm:ss') \"auditDate\", ")
				.append("         btai.id							\"auditId\" ")
				.append("    from bao_t_audit_info btai,  ")
				.append("         com_t_user ctu  ")
				.append("   where btai.create_user = ctu.id(+)  ")
				.append("     and btai.apply_type not in ('"+Constant.OPERATION_TYPE_60+"', '"+Constant.OPERATION_TYPE_06+"')  ");
		
		
		if(!StringUtils.isEmpty(params.get("projectId"))){
			String projectId = (String) params.get("projectId");
			sql.append(" and btai.relate_primary = ?");
			listObject.add(projectId);
		}
		
		sql.append(" order by btai.audit_time desc");
		
		return repositoryUtil.queryForMap(sql.toString(), listObject.toArray());
	}

}
