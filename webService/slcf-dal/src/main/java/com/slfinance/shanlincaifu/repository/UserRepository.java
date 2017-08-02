/** 
 * @(#)ComUserRepository.java 1.0.0 2015年4月29日 上午10:22:54  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.UserEntity;

/**   
 * 公共用户接口
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年4月29日 上午10:22:54 $ 
 */
public interface UserRepository extends PagingAndSortingRepository<UserEntity, String> {
	
	
	@Query(value="select u.user_name from com_t_user u where u.id=?1",nativeQuery=true)
	public String findUserNameById(String id);
	
	public UserEntity findUserByCredentialsCode(String credentialsCode);
}
