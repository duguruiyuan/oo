package com.slfinance.shanlincaifu.repository;

import java.util.List;
import org.springframework.data.repository.PagingAndSortingRepository;
import com.slfinance.shanlincaifu.entity.AutoTransferInfoEntity;


/**   
 * 用户自动转让表数据访问接口
 *  
 * @author  Tool
 * @version $Revision:1.0.0, $Date: 2017-03-13 10:56:29 $ 
 */
public interface AutoTransferInfoRepository extends PagingAndSortingRepository<AutoTransferInfoEntity, String>{
	/**
	 * 通过客户ID查询
	 *
	 * @author  fengyl
	 * @date    2017年3月8日 
	 * @param custId
	 * @return
	 */
	AutoTransferInfoEntity findByCustId(String custId);
	
	List<AutoTransferInfoEntity> findByOpenStatusOrderByCustPriorityDescOpenDateAsc(String openStatus);
}
