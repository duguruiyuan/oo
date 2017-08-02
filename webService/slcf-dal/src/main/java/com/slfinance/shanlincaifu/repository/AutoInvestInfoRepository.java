package com.slfinance.shanlincaifu.repository;


import com.slfinance.shanlincaifu.entity.AutoInvestInfoEntity;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;




/**   
 * 用户自动投标表数据访问接口
 *  
 * @author  Tool
 * @version $Revision:1.0.0, $Date: 2017-03-07 16:17:01 $ 
 */
public interface AutoInvestInfoRepository extends PagingAndSortingRepository<AutoInvestInfoEntity, String>{

	/**
	 * 通过客户ID查询
	 *
	 * @author  fengyl
	 * @date    2017年3月8日 
	 * @param custId
	 * @return
	 */
	AutoInvestInfoEntity findByCustId(String custId);
	@Query(value="select a.id from AutoInvestInfoEntity a where a.openStatus =:openStatus order by a.custPriority desc,a.openDate asc")
	List<String> findByOpenStatusOrderByCustPriorityDescOpenDateAsc(@Param("openStatus") String openStatus);


}
