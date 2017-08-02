package com.slfinance.shanlincaifu.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.slfinance.shanlincaifu.entity.SystemMessageReadInfoEntity;

/**
 * BaoTSystemMessageInfo entity. @author MyEclipse Persistence Tools
 */
@RepositoryRestResource(collectionResourceRel = "messagesRead", path = "messagesRead")
public interface SystemMessageReadInfoRepository extends PagingAndSortingRepository<SystemMessageReadInfoEntity, String>, JpaSpecificationExecutor<SystemMessageReadInfoEntity> {
	
	/**
	 * 通过custId获取
	 *
	 * @author  Wangwei
	 * @date    2015年10月24日 上午10:40:36
	 */
	@Query(value = "from SystemMessageReadInfoEntity where message.id =:messageId and receiveCust.id = :custId")
	public SystemMessageReadInfoEntity findOneByMessageIdAndCustId(@Param("messageId") String messageId,@Param("custId") String custId);
	
	@Query(value = "from SystemMessageReadInfoEntity where relatePrimary =:relatePrimary and receiveCust.id = :custId")
	public SystemMessageReadInfoEntity findOneByRelatePrimaryAndCustId(@Param("relatePrimary") String messageId,@Param("custId") String custId);
	
	
}