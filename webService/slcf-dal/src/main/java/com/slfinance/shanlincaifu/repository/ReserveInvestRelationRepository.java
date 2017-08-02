package com.slfinance.shanlincaifu.repository;
import org.springframework.data.repository.PagingAndSortingRepository;
import com.slfinance.shanlincaifu.entity.ReserveInvestRelationEntity;


/**   
 * 用户预约投资关联表数据访问接口
 *  
 * @author  Tool
 * @version $Revision:1.0.0, $Date: 2017-06-22 19:17:10 $ 
 */
public interface ReserveInvestRelationRepository extends PagingAndSortingRepository<ReserveInvestRelationEntity, String>{

}
