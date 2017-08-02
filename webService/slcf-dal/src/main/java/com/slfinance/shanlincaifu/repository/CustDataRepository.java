package com.slfinance.shanlincaifu.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.CustDataEntity;

public interface CustDataRepository extends PagingAndSortingRepository<CustDataEntity, String>, JpaSpecificationExecutor<CustDataEntity> {

	/**
	 * 
	 * <查询理财客户最新数据>
	 * <功能详细描述>
	 *
	 * @param custType
	 * @return [参数说明]
	 * @return List<CustDataEntity> [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	@Query("select A from CustDataEntity A where A.custType = ? order by A.createDate desc")
	List<CustDataEntity> findLastOne(String custType);

	@Query("select A from CustDataEntity A where A.custType = ? and A.investPopulationMonth is not null order by A.createDate desc")
	List<CustDataEntity> findlastMonthData(String custType);
	
}
