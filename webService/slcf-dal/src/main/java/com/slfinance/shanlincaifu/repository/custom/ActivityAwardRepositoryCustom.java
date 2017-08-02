package com.slfinance.shanlincaifu.repository.custom;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

public interface ActivityAwardRepositoryCustom {

	/**
	 * 
	 * <查询红包信息列表>
	 * <红包列表>
	 *
	 * @param params
	 * @return [参数说明]
	 * @return Page<Map<String,Object>> [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	Page<Map<String, Object>> queryRedPackedList(Map<String, Object> params);

	/**
	 * 
	 * <查询红包详情>
	 * <红包详情>
	 *
	 * @param params
	 * @return [参数说明]
	 * @return Page<Map<String,Object>> [返回类型说明]
	 * @throws ParseException 
	 * @see [类、类#方法、类#成员]
	 */
	Page<Map<String, Object>> queryRedPacketUseDetails(Map<String, Object> params) throws ParseException;

	/**
	 * 
	 * <查询使用的红包总金额和张数>
	 * <功能详细描述>
	 *
	 * @param params
	 * @return [参数说明]
	 * @return Map<String,Object> [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	Map<String, Object> queryUsedData(Map<String, Object> params);

	/**
	 * 
	 * <查询发放的红包总金额和张数>
	 * <功能详细描述>
	 *
	 * @param params
	 * @return [参数说明]
	 * @return Map<String,Object> [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	Map<String, Object> queryTotalData(Map<String, Object> params);

    /**
     * 根据用户活动id查询红包、加息券信息
     *
     * @param custActivityId 用户活动id
     *
     * @return
     */
    List<Map<String, Object>> findByCustActivityId(String custActivityId);
}
