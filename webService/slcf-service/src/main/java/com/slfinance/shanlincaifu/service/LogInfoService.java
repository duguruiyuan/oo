package com.slfinance.shanlincaifu.service;

import java.util.Map;

import com.slfinance.shanlincaifu.entity.LogInfoEntity;

/**
 * 
 * @author zhumin
 *
 */
public interface LogInfoService {
	
	/**
	 * 查询用户操作类型
	 * @param relatePrimary
	 * @param pageable
	 * @return
	 */
	Map<String, Object> findOperationLogByCustId(Map<String, Object> param);

	LogInfoEntity saveLogInfo(LogInfoEntity logInfo) ;
	
}
