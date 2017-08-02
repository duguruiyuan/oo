package com.slfinance.shanlincaifu.repository.custom.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.slfinance.shanlincaifu.repository.common.RepositoryUtil;
import com.slfinance.shanlincaifu.repository.custom.AppManageRepositoryCustom;
import com.slfinance.shanlincaifu.utils.SqlCondition;

@Repository
public class AppManageRepositoryImpl implements AppManageRepositoryCustom {

	@Autowired
	private RepositoryUtil repositoryUtil;
	
	public List<Map<String, Object>> queryAppVersionList(Map<String, Object> params) {
		StringBuilder sqlString= new StringBuilder()
		.append("  select t.id \"id\", t.app_source \"appSource\", t.app_version \"appVersion\",  ")
		.append("        t.update_url \"updateUrl\", t.app_supported_version \"appSupportedVersion\", t.create_date \"createDate\",  ")
		.append("        t.create_user \"createUser\", t.last_update_user \"lastUpdateUser\", t.last_update_date \"lastUpdateDate\",  ")
		.append("        t.version \"version\", t.record_status \"recordStatus\", t.memo \"memo\" ")
		.append("  from bao_t_app_manage_info t ")
		.append("  where 1 = 1 ");
		
		SqlCondition sql = new SqlCondition(sqlString, params);
		sql.addString("appSource", "app_source");
		return repositoryUtil.queryForMap(sql.toString(), sql.toArray());
	}

}
