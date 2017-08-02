package com.slfinance.shanlincaifu.repository.custom;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.slfinance.vo.ResultVo;

public interface TransAccountInfoRepositoryCustom {
	/**
	 * 查询转账信息
	 * 
	 * @param param
	 * @return
	 */
	public Page<Map<String, Object>> findTransAccountInfoEntity(
			Map<String, Object> param);

	/**
	 * 寻找推荐奖励的信息
	 * 
	 * @param param
	 * @return
	 */
	public Page<Map<String, Object>> findTransAccountInfoEntityRecommendedAwards(
			Map<String, Object> param);

	/**
	 * 推荐奖励统计
	 * 
	 * @param param
	 * @return
	 */
	Map<String, Object> findTransAccountInfoEntityRecommendedAwardsCount(
			Map<String, Object> param);

	/**
	 * 推荐明细查询
	 * 
	 * @param param
	 * @return
	 */
	public Page<Map<String, Object>> findCustActivityDetailEntityById(
			Map<String, Object> param);
	
	/**
	 * 查询融资租赁列表
	 * 
	 * @param param
	 * @return
	 */
	public Page<Map<String, Object>> findCompanyTransAccountList(Map<String, Object> param);
	
	/**
	 * 查询调账信息
	 * @param param
	 * @return
	 */
	public ResultVo findCompanyTransAccountById(Map<String, Object> param);
}
