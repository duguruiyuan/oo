/** 
 * @(#)ActivityInfoRepository.java 1.0.0 2015年5月16日 下午2:38:50  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */

package com.slfinance.shanlincaifu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.CustActivityDetailEntity;

/**
 * 
 * 
 * @author HuYaHui
 * @version $Revision:1.0.0, $Date: 2015年5月16日 下午2:38:50 $
 */
public interface CustActivityDetailEntityRepository extends PagingAndSortingRepository<CustActivityDetailEntity, String> {

	@Query("SELECT a from CustActivityDetailEntity a where a.custActivityId IN (SELECT caie.id  FROM CustActivityInfoEntity caie WHERE  caie.activityId IN(SELECT b.id from ActivityInfoEntity b where b.activityName=?) ) and a.tradeStatus=? and a.custId=? and  to_char(a.createDate,'mm')=? and a.recordStatus='有效'")
	public List<CustActivityDetailEntity> findCustActivityDetailByDefine(String activityName, String tradeStatus, String custId, String mm);
}
