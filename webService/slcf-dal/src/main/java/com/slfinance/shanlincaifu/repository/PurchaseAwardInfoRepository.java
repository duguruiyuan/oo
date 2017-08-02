package com.slfinance.shanlincaifu.repository;


import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.PurchaseAwardInfoEntity;
/**
 * 加息券数据接口
 * <一句话功能简述>
 * <功能详细描述>
 * 
 * @author  Mgp
 * @version  [版本号, 2017年7月13日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public interface PurchaseAwardInfoRepository extends PagingAndSortingRepository<PurchaseAwardInfoEntity, String>{
	
//	@Query("select * from PurchaseAwardInfoEntity where custId=?1 and InvestId=?2")
	List<PurchaseAwardInfoEntity> findByCustIdAndInvestId(String custId,String InvestId);
	
	
	PurchaseAwardInfoEntity findByCustIdAndInvestIdAndCurrentTermAndAwardStatus(String custId,String InvestId,Integer CurrentTerm,String wardStatus);

	@Query("select P from PurchaseAwardInfoEntity P where P.awardStatus = ?1 order by P.currentTerm")
	List<PurchaseAwardInfoEntity> listByAwardStatus(String status);

	@Modifying
	@Query("update PurchaseAwardInfoEntity set awardStatus = ?1 where currentTerm > ?2 ")
	void updateByCurrentTerm(String awardStatus, Integer currentTerm);

}
