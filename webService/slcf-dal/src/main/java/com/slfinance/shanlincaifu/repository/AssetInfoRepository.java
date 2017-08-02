package com.slfinance.shanlincaifu.repository;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.slfinance.shanlincaifu.entity.AssetInfoEntity;


/**   
 * 资产信息表数据访问接口
 *  
 * @author  Tool
 * @version $Revision:1.0.0, $Date: 2017-02-23 13:51:48 $ 
 */
public interface AssetInfoRepository extends PagingAndSortingRepository<AssetInfoEntity, String>{

	@Query(value=" select new Map(a.custName as custName, a.credentialsCode as credentialsCode, a.loanAmount as loanAmount, a.investEndDate as investEndDate, a.loanDesc as loanUse) FROM AssetInfoEntity a WHERE a.loanId = :loanId ")
	List<Map<String, Object>> findMapAssetInfoByloanId(@Param("loanId") String loanId);
	/**
	 * 根据借款Id查询
	 * @author  fengyl
	 * @date    2017年2月24日 
	 * @param loanId
	 * @return
	 */
	public List<AssetInfoEntity> findLoanByLoanId(String loanId);
}
