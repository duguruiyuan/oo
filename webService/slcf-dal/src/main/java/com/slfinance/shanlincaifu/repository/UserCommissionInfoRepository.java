package com.slfinance.shanlincaifu.repository;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.slfinance.shanlincaifu.entity.UserCommissionInfoEntity;


/**   
 * 业务员佣金表数据访问接口
 *  
 * @author  Tool
 * @version $Revision:1.0.0, $Date: 2016-12-05 18:56:04 $ 
 */
public interface UserCommissionInfoRepository extends PagingAndSortingRepository<UserCommissionInfoEntity, String>{

	/**
	 * 根据结算状态查询佣金
	 *
	 * @author  wangjf
	 * @date    2016年12月8日 下午2:19:00
	 * @param paymentStatus
	 * @return
	 */
	@Query("select A from UserCommissionInfoEntity A where A.paymentStatus = :paymentStatus and A.exceptPaymentDate <= :exceptPaymentDate and A.totalPaymentAmount > 0")
	public List<UserCommissionInfoEntity> findByWaitingSettlement(@Param("paymentStatus")String paymentStatus, @Param("exceptPaymentDate")String exceptPaymentDate);
	
	/**
	 * 通过投资ID查询佣金方案
	 *
	 * @author  wangjf
	 * @date    2017年1月3日 下午7:34:29
	 * @param investId
	 * @return
	 */
	@Query("select A from UserCommissionInfoEntity A where A.paymentStatus != '已废弃' and A.investId = :investId order by currentTerm asc ")
	public List<UserCommissionInfoEntity> findByInvestIdOrderByCurrentTermAsc(@Param("investId")String investId);
	
	/**
	 * 查询当期原始佣金
	 *  
	 * 以返佣日期4月10日为例，上一返佣日期为3月10日，则在3月10日与4月10日之间的债权转让取当期原始佣金为返佣日期为4月10日且创建时间小于3月10日
	 * @author  wangjf
	 * @date    2017年1月11日 下午4:40:31
	 * @param investId
	 * @param exceptPaymentDate
	 * @param beforeExceptPaymentDate
	 * @return
	 */
	@Query("select A from UserCommissionInfoEntity A "
			+ " where A.investId = :investId "
			+ " and A.exceptPaymentDate = :exceptPaymentDate "
			+ " and A.tradeNo = (select max(B.tradeNo) "
			+ "                     from UserCommissionInfoEntity B "
			+ "                     where B.investId = A.investId "
			+ "                     and B.exceptPaymentDate = A.exceptPaymentDate"
			+ "                     and B.createDate < to_date(:beforeExceptPaymentDate, 'yyyyMMdd'))")
	public UserCommissionInfoEntity findLatestMaxByInvestIdAndExceptPaymentDate(@Param("investId")String investId, @Param("exceptPaymentDate")String exceptPaymentDate, @Param("beforeExceptPaymentDate")String beforeExceptPaymentDate);
	
	/**
	 * 查询当期原始佣金
	 * 
	 * 取第一期的时候，以最开始金额为准备
	 * @author  wangjf
	 * @date    2017年1月12日 下午1:19:03
	 * @param investId
	 * @param exceptPaymentDate
	 * @return
	 */
	@Query("select A from UserCommissionInfoEntity A "
			+ " where A.investId = :investId "
			+ " and A.exceptPaymentDate = :exceptPaymentDate "
			+ " and A.tradeNo = (select min(B.tradeNo) "
			+ "                     from UserCommissionInfoEntity B "
			+ "                     where B.investId = A.investId "
			+ "                     and B.exceptPaymentDate = A.exceptPaymentDate)")
	public UserCommissionInfoEntity findLatestMinByInvestIdAndExceptPaymentDate(@Param("investId")String investId, @Param("exceptPaymentDate")String exceptPaymentDate);
	
}
