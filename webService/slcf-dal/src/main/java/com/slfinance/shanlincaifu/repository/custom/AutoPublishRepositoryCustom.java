package com.slfinance.shanlincaifu.repository.custom;

import java.util.List;
import java.util.Map;

public interface AutoPublishRepositoryCustom {

	//自动发布的筛选
	public List<Map<String, Object>> queryPublishInfo(Map<String, Object> param);
}
