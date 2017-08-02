package com.slfinance.shanlincaifu.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import com.slfinance.shanlincaifu.entity.SystemMessageInfoEntity;

/**
 * BaoTSystemMessageInfo entity. @author MyEclipse Persistence Tools
 */
@RepositoryRestResource(collectionResourceRel = "messages", path = "messages")
public interface SystemMessageInfoRepository extends PagingAndSortingRepository<SystemMessageInfoEntity, String>, JpaSpecificationExecutor<SystemMessageInfoEntity> {

	@Query(value = "from SystemMessageInfoEntity where receiveCust.id =:custId order by createDate desc", countQuery = "select count(s) from SystemMessageInfoEntity s where s.receiveCust.id =:custId order by createDate desc")
	public Page<SystemMessageInfoEntity> findByReceiveCustId(@Param("custId") String custId, Pageable pageable);

	@Query(value = "from SystemMessageInfoEntity  where receiveCust.id =:custId and isRead in(:isRead) order by createDate desc", countQuery = "select count(s) from SystemMessageInfoEntity s where s.receiveCust.id =:custId and isRead in(:isRead) order by createDate desc")
	public Page<SystemMessageInfoEntity> findByReceiveCustIdAndIsRead(@Param("custId") String custId, @Param("isRead") Collection<String> readStatus, Pageable pageable);

	public Page<SystemMessageInfoEntity> findAll(Pageable pageable);

	@Query("select m.receiveCust.id, count(m.receiveCust) from SystemMessageInfoEntity m where m.receiveCust.id=:custId and m.isRead='未读' group by  m.receiveCust.id")
	@RestResource(exported = false)
	List<Object[]> unreadMessage4Cust(@Param("custId") String custId);

	/**
	 * 通过id获取用户所有消息
	 *
	 * @author  Wangwei
	 * @date    2015年10月24日 上午10:40:36
	 */
	@Query(value = "from SystemMessageInfoEntity s where s.receiveCust.id in (:custId,'ALL') and s.createDate >= (select c.createDate from CustInfoEntity c where c.id = :custId) order by createDate desc", countQuery = "select count(s) from SystemMessageInfoEntity s where s.receiveCust.id in (:custId,'ALL') and s.createDate >= (select c.createDate from CustInfoEntity c where c.id = :custId) order by createDate desc")
	public Page<SystemMessageInfoEntity> findAllByCustId(@Param("custId")String custId, Pageable pageable);
	
	/**
	 * 通过id获取用户相应状态的消息 已读
	 *
	 * @author  Wangwei
	 * @date    2015年10月24日 上午10:40:36
	 */
	@Query(value = "select smi from SystemMessageReadInfoEntity smri left join smri.message smi where smri.receiveCust.id=:custId and smi.createDate >= (select c.createDate from CustInfoEntity c where c.id = :custId) order by smi.createDate desc",countQuery = "select count(smi) from SystemMessageReadInfoEntity smri left join smri.message smi where smri.receiveCust.id=:custId and smi.createDate >= (select c.createDate from CustInfoEntity c where c.id = :custId) order by smi.createDate desc" )
	public Page<SystemMessageInfoEntity> findRead(@Param("custId") String custId, Pageable pageable);
	
	/**
	 * 通过id获取用户相应状态的消息 未读
	 *
	 * @author  Wangwei
	 * @date    2015年10月24日 上午10:40:36
	 */
	@Query(value = "select smi from SystemMessageInfoEntity smi where smi.id not in (select smri.message.id from SystemMessageReadInfoEntity smri where smri.receiveCust.id = :custId) and smi.receiveCust.id in (:custId,'ALL') and smi.createDate >= (select c.createDate from CustInfoEntity c where c.id = :custId) order by smi.createDate desc",countQuery ="select count(smi) from SystemMessageInfoEntity smi where smi.id not in (select smri.message.id from SystemMessageReadInfoEntity smri where smri.receiveCust.id = :custId) and smi.receiveCust.id in (:custId,'ALL') and smi.createDate >= (select c.createDate from CustInfoEntity c where c.id = :custId) order by smi.createDate desc")
	public Page<SystemMessageInfoEntity> findUnread(@Param("custId") String custId, Pageable pageable);
	
	/**
	 * 通过id获取未读消息数量
	 *
	 * @author  Wangwei
	 * @date    2015年10月24日 上午10:40:36
	 */
	@Query("select count(smi) from SystemMessageInfoEntity smi where smi.id not in (select smri.message.id from SystemMessageReadInfoEntity smri where smri.receiveCust.id = :custId) and smi.receiveCust.id in (:custId,'ALL') and smi.createDate >= (select c.createDate from CustInfoEntity c where c.id = :custId)")
	@RestResource(exported = false)
	int unreadMessageCount(@Param("custId") String custId);
	
	/**
	 * 通过id获取未读消息数量  By salesMan
	 * @author  lyy
	 * @date    2016-11-14 19:39:40
	 */
	@Query("select count(smi) from SystemMessageInfoEntity smi where smi.id not in (select smri.message.id from SystemMessageReadInfoEntity smri where smri.receiveCust.id = :custId) and smi.receiveCust.id in (:custId,'ALL','salesMan') and smi.createDate >= (select c.createDate from CustInfoEntity c where c.id = :custId)")
	@RestResource(exported = false)
	int unreadMessageCountBySalesMan(@Param("custId") String custId);
	
	/**
	 * 通过id获取所有消息数量
	 *
	 * @author  wangjf
	 * @date    2015年12月14日 下午5:22:03
	 * @param custId
	 * @return
	 */
	@Query(value = "select count(s) from SystemMessageInfoEntity s where s.receiveCust.id in (:custId,'ALL') and s.createDate >= (select c.createDate from CustInfoEntity c where c.id = :custId) ")
	int countAllMessage(@Param("custId") String custId);
}