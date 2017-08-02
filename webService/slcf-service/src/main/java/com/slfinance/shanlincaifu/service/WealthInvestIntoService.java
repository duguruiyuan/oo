package com.slfinance.shanlincaifu.service;

import java.util.List;

import com.slfinance.shanlincaifu.entity.WealthInvestInfoEntity;

public interface WealthInvestIntoService {

	/** 同步理财投资信息表BAO_T_WEALTH_INVEST_INFO */
	boolean synchronizeInvestInfoFromWealth();

	List<WealthInvestInfoEntity> findInvestInfoByCardId(String credentialsCode);
}
