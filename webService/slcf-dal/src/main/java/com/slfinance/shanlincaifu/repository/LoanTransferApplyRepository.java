package com.slfinance.shanlincaifu.repository;
import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.slfinance.shanlincaifu.entity.LoanTransferApplyEntity;


/**   
 * 债权转让申请表数据访问接口
 *  
 * @author  Tool
 * @version $Revision:1.0.0, $Date: 2016-12-26 17:38:21 $ 
 */
public interface LoanTransferApplyRepository extends PagingAndSortingRepository<LoanTransferApplyEntity, String>{

	/**
	 * 统计已转让比例(包含正在转让的比例)
	 * 注：剔除已撤销的部分，撤销部分中若部分转让成功则剔除已转让成功的部分
	 * @author  wangjf
	 * @date    2016年12月28日 上午9:13:50
	 * @param senderHoldId
	 * @return
	 */
	@Query(" select sum("
			+ " case when a.cancelStatus = '未撤销' then a.tradeScale"
			+ " else (a.tradeScale - a.remainderTradeScale) end"
			+ " ) "
			+ " from LoanTransferApplyEntity a"
			+ " where a.senderHoldId = :senderHoldId and a.applyStatus = '通过' ")
	BigDecimal sumTradeScaleBySenderHoldId(@Param("senderHoldId")String senderHoldId);
	
	/**
	 * 通过持有人ID和撤销状态查询转让申请情况
	 *
	 * @author  wangjf
	 * @date    2016年12月28日 上午9:41:59
	 * @param senderHoldId
	 * @param cancelStatus
	 * @return
	 */
	@Query(" select L from LoanTransferApplyEntity L where L.senderHoldId = :senderHoldId and L.cancelStatus = :cancelStatus and L.applyStatus != '转让成功' and L.applyStatus = '通过' ")
	List<LoanTransferApplyEntity> findBySenderHoldIdAndCancelStatus(@Param("senderHoldId")String senderHoldId, @Param("cancelStatus")String cancelStatus);

	List<LoanTransferApplyEntity> findBySenderHoldIdAndAuditStatus(String holdId, String auditStatus);
	
	@Query(" select max(a.stickyLevel) from LoanTransferApplyEntity a where a.stickyStatus = ?1 ")
	String findMaxStickyLevel(String stickyStatus);
	
	@Modifying
	@Query(" update LoanTransferApplyEntity a set a.stickyLevel = (a.stickyLevel - 1) where a.stickyLevel > ?1 and a.stickyStatus = ?2")
	int cancelStickyUpdateStickyLevel(String stickyLevel , String stickyStatus);
	
	@Modifying
	@Query(" update LoanTransferApplyEntity a set a.stickyLevel = ?1 where a.stickyLevel = (?1 - 1) and a.stickyStatus = ?2")
	int moveUpUpdateStickyLevel(String stickyLevel , String stickyStatus);
	
}
