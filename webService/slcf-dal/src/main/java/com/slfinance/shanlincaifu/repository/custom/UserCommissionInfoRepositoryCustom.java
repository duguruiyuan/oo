package com.slfinance.shanlincaifu.repository.custom;

import java.util.List;
import java.util.Map;

public interface UserCommissionInfoRepositoryCustom {

	/**
	 * 查询待计算提成的投资数据
	 *
	 * @author  wangjf
	 * @date    2016年12月5日 下午8:33:39
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> queryWaitingCommissionList(Map<String, Object> params);
	
	/**
	 * 查询待计算债权转让转出提成的投资数据
	 *
	 * @author  wangjf
	 * @date    2017年3月10日 下午12:00:56
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> queryWaitingTransferOutCommissionList(Map<String, Object> params);
}
