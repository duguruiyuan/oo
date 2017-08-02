package com.slfinance.shanlincaifu.repository.custom;

import java.util.List;
import java.util.Map;

public interface AppManageRepositoryCustom {
	
	/**
	 * 查询APP管理列表
	 * 
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> queryAppVersionList(Map<String, Object> params);
}
