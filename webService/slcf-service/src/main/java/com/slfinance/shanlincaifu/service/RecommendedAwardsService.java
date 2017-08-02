package com.slfinance.shanlincaifu.service;

import java.util.Map;

import com.slfinance.vo.ResultVo;

/**
 * 推荐奖励
 * @author zhumin
 *
 */
public interface RecommendedAwardsService {
	public Map<String, Object> list(Map<String, Object> param);
	
	public ResultVo grentRecommendedAwardsData(Map<String, Object> paramsMap);
	
	
	public Map<String, Object> findCustActivityDetailEntityById(Map<String, Object> param);
}
