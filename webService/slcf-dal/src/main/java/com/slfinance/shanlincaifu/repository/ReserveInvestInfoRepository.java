package com.slfinance.shanlincaifu.repository;
import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.ReserveInvestInfoEntity;


/**   
 * 用户预约投资表数据访问接口
 *  
 * @author  Tool
 * @version $Revision:1.0.0, $Date: 2017-06-17 10:35:14 $ 
 */
public interface ReserveInvestInfoRepository extends PagingAndSortingRepository<ReserveInvestInfoEntity, String>{

	/**
	 * 预约投资-现金贷
	 * 购买项目-根据预约记录
	 */
	@Query(value=" SELECT id FROM BAO_T_RESERVE_INVEST_INFO " +
				" WHERE 1=1 " + /* CAN_INVEST_TERM LIKE '%'||?||'%' */ 
				"   AND RESERVE_STATUS='排队中'" +
				" ORDER BY RESERVE_DATE ", nativeQuery=true)
	List<String> findIdsByParamOrderByDate();
	
	/**
	 * 预约投资-现金贷
	 * 购买项目-根据（同一个客户集成预约记录）
	 */
	@Query(value=" SELECT cust_id FROM BAO_T_RESERVE_INVEST_INFO " +
			"  WHERE 1=1  " +
			"    AND RESERVE_STATUS = '排队中' " +
			"  GROUP BY cust_id " +
			"  HAVING sum(REMAINDER_AMOUNT) > 0 " +
			"  ORDER BY min(RESERVE_DATE) ", nativeQuery=true)
	List<String> findReserveAmountForCustId();
	
	/**
	 * 已预约金额
	 *
	 * @author  fengyl
	 * @date    2017年6月20日 
	 * @param custId
	 * @return
	 */
	@Query(value = "select sum(t.reserve_amount) from BAO_T_RESERVE_INVEST_INFO  t where t.create_date<= sysdate ", nativeQuery = true)
	public BigDecimal getTotalReserveAmount();
	/**
	 * 已加入人数
	 *
	 * @author  fengyl
	 * @date    2017年6月20日 
	 * @param custId
	 * @return
	 */
	@Query(value = "select count(distinct(t.cust_id)) from BAO_T_RESERVE_INVEST_INFO  t where t.create_date<= sysdate ", nativeQuery = true)
	public BigDecimal getTotalReservePeople();
	/**
	 * 待投金额
	 *
	 * @author  fengyl
	 * @date    2017年6月20日 
	 * @param custId
	 * @return
	 */
	@Query(value = "select nvl(SUM(REMAINDER_AMOUNT),0)  from  BAO_T_RESERVE_INVEST_INFO where  RESERVE_STATUS = '排队中' and cust_id = ? ", nativeQuery = true)
	public BigDecimal getRemainderReserveAmount(String custId);
	public List<ReserveInvestInfoEntity> findByCustIdAndReserveStatus(String custId,String reserveStatus);
	@Query(value = " select * from  BAO_T_RESERVE_INVEST_INFO where RESERVE_STATUS = ?1 and cust_id = ?2 order by reserve_end_date ", nativeQuery = true)
	public List<ReserveInvestInfoEntity> findByReserveStatusAndCustId(String reserveStatus, String custId);
	
	public List<ReserveInvestInfoEntity> findByCustIdAndReserveStatusOrderByReserveDate(String custId,String reserveStatus);
	
	@Query(value ="select count(i.id) from BAO_T_RESERVE_INVEST_INFO i where i.reserve_status=? and i.cust_id  = ? ",nativeQuery = true)
	BigDecimal getCountByCustIdAndReserveStatus(String reserveStatus,String custId);
	/**预约金额查询
	 * @author zhangyb
	 * @return
	 */
	@Query(value="select sum(t.remainder_amount) from bao_t_reserve_invest_info t where t.reserve_status = '排队中' ", nativeQuery = true)
	public BigDecimal getTotalAmount();
	

}
