package com.slfinance.shanlincaifu.service;

import com.slfinance.shanlincaifu.entity.WealthCustIntoEntity;

public interface WealthCustIntoService {

	/** 同步理财客户信息表BAO_T_WEALTH_CUST_INTO */
	boolean synchronizeCustInfoFromWealth();

	/**
	 * 通过身份证号查询客户信息
	 * 
	 * @param credentialsCode
	 * @return
	 */
	WealthCustIntoEntity findCustInfoByCardId(String credentialsCode);
}
