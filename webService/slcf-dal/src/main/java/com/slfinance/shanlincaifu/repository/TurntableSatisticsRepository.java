package com.slfinance.shanlincaifu.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.slfinance.shanlincaifu.entity.TurntableSatisticsEntity;


/**   
 * 转盘统计信息数据访问接口
 * @author  lixx
 */
public interface TurntableSatisticsRepository extends PagingAndSortingRepository<TurntableSatisticsEntity, String>{
	
	/**
	 * 根据转盘ID查询转盘统计信息
	 */
	public TurntableSatisticsEntity findByTurntableId(@Param("turntableId") String turntableId);

	
}
