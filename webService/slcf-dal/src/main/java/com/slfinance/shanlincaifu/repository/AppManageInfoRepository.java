package com.slfinance.shanlincaifu.repository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.AppManageInfoEntity;


/**   
 * 数据访问接口
 *  
 * @author  Tool
 * @version $Revision:1.0.0, $Date: 2016-07-18 11:08:25 $ 
 */
public interface AppManageInfoRepository extends PagingAndSortingRepository<AppManageInfoEntity, String>{

	public AppManageInfoEntity findByAppSource(String appSource);
}
