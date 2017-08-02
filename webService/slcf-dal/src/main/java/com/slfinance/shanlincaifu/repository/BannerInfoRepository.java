package com.slfinance.shanlincaifu.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.BannerInfoEntity;

/**   
 * banner信息数据访问接口
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年10月16日 下午2:22:13 $ 
 */
public interface BannerInfoRepository extends PagingAndSortingRepository<BannerInfoEntity, String>{

}
