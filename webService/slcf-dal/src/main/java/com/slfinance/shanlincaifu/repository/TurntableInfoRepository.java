package com.slfinance.shanlincaifu.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.slfinance.shanlincaifu.entity.TurntableInfoEntity;


/**   
 * 转盘信息数据访问接口
 * @author  lixx
 */
public interface TurntableInfoRepository extends PagingAndSortingRepository<TurntableInfoEntity, String>{

	public List<TurntableInfoEntity> findByTurntableId(@Param("turntableId") String turntableId);
}
