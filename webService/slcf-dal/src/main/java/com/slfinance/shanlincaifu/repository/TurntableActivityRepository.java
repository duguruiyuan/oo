package com.slfinance.shanlincaifu.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.TurntableActivityEntity;


/**   
 * 转盘活动数据访问接口
 * @author  lixx
 */
public interface TurntableActivityRepository extends PagingAndSortingRepository<TurntableActivityEntity, String>{

	
}
