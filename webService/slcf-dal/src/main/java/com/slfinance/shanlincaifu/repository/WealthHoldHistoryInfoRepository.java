package com.slfinance.shanlincaifu.repository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.WealthHoldHistoryInfoEntity;


/**   
 * 债权持有情况历史表数据访问接口
 *  
 * @author  Tool
 * @version $Revision:1.0.0, $Date: 2016-02-23 09:44:53 $ 
 */
public interface WealthHoldHistoryInfoRepository extends PagingAndSortingRepository<WealthHoldHistoryInfoEntity, String>{

}
