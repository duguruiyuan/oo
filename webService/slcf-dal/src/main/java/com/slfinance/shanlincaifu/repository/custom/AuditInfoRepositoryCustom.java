package com.slfinance.shanlincaifu.repository.custom;

import java.util.List;
import java.util.Map;

/**
 * 审核信息访问接口
 * @author zhangt
 *
 */
public interface AuditInfoRepositoryCustom {

	/**
	 * 根据项目主键查询
	 * 
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> findByRelatePrimary(Map<String, Object> params);
}
