package com.slfinance.shanlincaifu.repository.custom;

import java.util.List;
import java.util.Map;

public interface OfflineWealthRepositoryCustom {

	/**
	 * 查询线下理财用户信息
	 * 
	 * @author zhiwen_feng
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> findOffLineCustInfo(Map<String, Object> params);
}
