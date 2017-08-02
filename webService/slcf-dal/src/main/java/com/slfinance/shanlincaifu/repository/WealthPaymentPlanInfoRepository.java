package com.slfinance.shanlincaifu.repository;
import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.slfinance.shanlincaifu.entity.WealthPaymentPlanInfoEntity;


/**   
 * 理财返息计划表数据访问接口
 *  
 * @author  Tool
 * @version $Revision:1.0.0, $Date: 2016-03-02 12:08:37 $ 
 */
public interface WealthPaymentPlanInfoRepository extends PagingAndSortingRepository<WealthPaymentPlanInfoEntity, String>{
	
	/**
	 * 根据客户id 和 返息状态 查询 预期本金
	 * 
	 * @author zhangt
	 * @date   2016年3月2日下午3:27:11
	 * @param custId
	 * @param paymentStatus
	 * @return
	 */
	@Query("select nvl(sum(trunc(w.exceptPaymentPrincipal, 2)), 0) from WealthPaymentPlanInfoEntity w where w.custId = :custId and w.paymentStatus = :paymentStatus")
	public BigDecimal findExceptPaymentPrincipalByCustId(@Param("custId") String custId, @Param("paymentStatus") String paymentStatus);
	
	/**
	 * 根据客户id 和 返息状态 查询 预期利息
	 * 
	 * @author zhangt
	 * @date   2016年3月2日下午3:31:18
	 * @param custId
	 * @param paymentStatus
	 * @return
	 */
	@Query("select nvl(sum(trunc(w.exceptPaymentInterest, 2)), 0) from WealthPaymentPlanInfoEntity w where w.custId = :custId and w.paymentStatus = :paymentStatus")
	public BigDecimal findExceptPaymentInterestByCustId(@Param("custId") String custId, @Param("paymentStatus") String paymentStatus);
	
	/**
	 * 根据客户id 和 返息状态 查询 预收奖励
	 *  
	 * @author zhangt
	 * @date   2016年3月2日下午3:34:23
	 * @param custId
	 * @param paymentStatus
	 * @return
	 */
	@Query("select nvl(sum(trunc(w.exceptPaymentAward, 2)), 0) from WealthPaymentPlanInfoEntity w where w.custId = :custId and w.paymentStatus = :paymentStatus")
	public BigDecimal findExceptPaymentAwardByCustId(@Param("custId") String custId, @Param("paymentStatus") String paymentStatus);

	/**
	 * 根据投资ID和回收状态查询理财计划
	 *
	 * @author  wangjf
	 * @date    2016年3月3日 上午9:48:16
	 * @param investId
	 * @return
	 */
	public List<WealthPaymentPlanInfoEntity> findByInvestIdAndPaymentStatusOrderByExceptPaymentDateAsc(String investId, String paymentStatus);
	
	/**
	 * 根据投资ID、回收状态、预期日期查询理财计划
	 *
	 * @author  wangjf
	 * @date    2016年3月5日 上午10:44:48
	 * @param investId
	 * @param paymentStatus
	 * @param exceptPaymentDate
	 * @return
	 */
	public WealthPaymentPlanInfoEntity findByInvestIdAndPaymentStatusAndExceptPaymentDate(String investId, String paymentStatus, String exceptPaymentDate);
}
