package com.slfinance.shanlincaifu.repository.custom;

import java.util.Map;

public interface WealthInvestIntoRepositoryCustom {

	/** 同步理财投资信息表BAO_T_WEALTH_INVEST_INFO */
	void synchronizeInvestInfoFromWealth();
	
	/**
	 * 历史投资 (线下理财)
	 * 
	 * @author zhiwen_feng
	 * @param params
	 * @return
	 */
	public Map<String, Object> queryOfflineInvestList(Map<String, Object> params);

}
