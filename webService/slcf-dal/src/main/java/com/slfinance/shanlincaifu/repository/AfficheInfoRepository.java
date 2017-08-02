/** 
 * @(#)AfficheInfoRepository.java 1.0.0 2015年4月28日 下午4:47:17  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.AfficheInfoEntity;

/**   
 * 网站公告业务
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年4月28日 下午4:47:17 $ 
 */
public interface AfficheInfoRepository extends PagingAndSortingRepository<AfficheInfoEntity, String>,JpaSpecificationExecutor<AfficheInfoEntity>{

	/**根据类型和状态查询公告数量**/
	public int countByAfficheTypeAndAfficheStatus(String afficheType,String afficheStatus);
	
	/**查询已发布公告**/
	public List<AfficheInfoEntity> findByAfficheTypeAndAfficheStatusOrderByPublishTimeDesc(String afficheType,String afficheStatus);

	/**查询已发布行业动态和媒体报道**/
	public List<AfficheInfoEntity> findTop5ByAfficheTypeInAndAfficheStatusOrderByPublishTimeDesc(Collection<String> asList, String afficheStatusPublished);
}
