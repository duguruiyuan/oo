package com.slfinance.shanlincaifu.repository;

import com.slfinance.shanlincaifu.entity.SpecialUsersUrlEntity;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * 特殊用户标的设置有效期
 *
 * @author  mali
 * @version $Revision:1.0.0, $Date: 2017年7月21日 下午11:16:12 $
 */
public interface SpecialUsersUrlRepository extends PagingAndSortingRepository<SpecialUsersUrlEntity, String> {

	@Query("select A from SpecialUsersUrlEntity A where A.token = ?")
	SpecialUsersUrlEntity findBytoken(String token);
}
