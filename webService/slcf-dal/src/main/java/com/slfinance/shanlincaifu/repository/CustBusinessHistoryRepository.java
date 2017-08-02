package com.slfinance.shanlincaifu.repository;
import org.springframework.data.repository.PagingAndSortingRepository;
import com.slfinance.shanlincaifu.entity.CustBusinessHistoryEntity;


/**   
 * 客户业务数据历史信息表数据访问接口
 *  
 * @author  Tool
 * @version $Revision:1.0.0, $Date: 2015-12-10 18:09:07 $ 
 */
public interface CustBusinessHistoryRepository extends PagingAndSortingRepository<CustBusinessHistoryEntity, String>{

}
