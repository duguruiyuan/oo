package com.slfinance.shanlincaifu.repository;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.slfinance.shanlincaifu.entity.InvestInfoEntity;
import com.slfinance.shanlincaifu.entity.WealthInfoEntity;


/**   
 * 数据访问接口
 *  
 * @author  Tool
 * @version $Revision:1.0.0, $Date: 2016-02-23 09:44:53 $ 
 */
public interface WealthInfoRepository extends PagingAndSortingRepository<WealthInfoEntity, String>{

	/**
	 * 通过下个返息日和结算类型查询理财计划
	 *
	 * @author  wangjf
	 * @date    2016年2月23日 下午3:24:12
	 * @param nextRepayDay
	 * @param incomeType
	 * @return
	 */
	@Query("select A from WealthInfoEntity A where nextRepayDay = :nextRepayDay and A.wealthTypeId in (select B.id from WealthTypeInfoEntity B where B.incomeType = :incomeType)")
	public List<WealthInfoEntity> findByNextRepayDayAndIncomeType(@Param("nextRepayDay")String nextRepayDay, @Param("incomeType")String incomeType);
	
	/**
	 * 通过理财计划ID查询投资情况
	 *
	 * @author  wangjf
	 * @date    2016年2月23日 下午3:35:45
	 * @param wealthId
	 * @return
	 */
	@Query("select A from InvestInfoEntity A where wealthId = :wealthId")
	public List<InvestInfoEntity> findByWealthId(@Param("wealthId")String wealthId);
	
	/**
	 * 通过到期时间和状态取理财计划
	 *
	 * @author  wangjf
	 * @date    2016年2月24日 下午3:30:00
	 * @param endDate
	 * @param wealthStatus
	 * @return
	 */
	@Query("select A from WealthInfoEntity A where endDate >= :endDate and endDate < :endDate + 1 and wealthStatus = :wealthStatus")
	public List<WealthInfoEntity> findByEndDateAndWealthStatus(@Param("endDate")Date endDate, @Param("wealthStatus")String wealthStatus);

	/**
	 * 统计同一种类型的产品当天发布个数
	 *
	 * @author  zhiwen_feng
	 * @date    2016年2月29日 上午11:45:24
	 * @param wealthTypeId
	 * @param releaseDate
	 * @return
	 */
	@Query(value = "select t.* from bao_t_wealth_info t where t.wealth_type_id = ?1 and to_char(t.release_date, 'yyyy-MM-dd') = ?2 ",nativeQuery = true)
	public List<WealthInfoEntity> findCountWealthByReleaseDayWealthTypeId(String wealthTypeId, String releaseDate);
	
	/**
	 * 根据计划状态查询计划总额
	 * @author zhiwen_feng
	 * @param wealthStatus
	 * @return
	 */
	@Query(value = "select trunc(nvl(sum(a.plan_total_amount), 0), 0) from bao_t_wealth_info a where a.wealth_status = ?1 ", nativeQuery = true)
	public BigDecimal sumWealthPlanAmountByWealthStatus(String wealthStatus);
	
	/**
	 * 根据发布日期状态查询理财计划
	 * 
	 * @author zhiwen_feng
	 * @param releaseDate
	 * @param wealthStatus
	 * @return
	 */
	@Query(value= "select a.* from bao_t_wealth_info a where a.wealth_status = ?1 and to_char(a.release_date, 'yyyy-MM-dd') = ?2 ", nativeQuery = true)
	public List<WealthInfoEntity> findByReleaseDateWealthStatus(String wealthStatus, String releaseDate);
	
	/**
	 * 根据生效日期、状态 查询
	 * 
	 * @author zhiwen_feng
	 * @param effectDate
	 * @param wealthStatus
	 * @return
	 */
	@Query(value = "select t.* from bao_t_wealth_info t where to_char(t.effect_date, 'yyyy-MM-dd') = to_char(sysdate, 'yyyy-MM-dd') and t.wealth_status in ('发布中', '已满额') ", nativeQuery = true)
	public List<WealthInfoEntity> findAutoReleaseWealth();

	/**
	 * 根据理财计划、状态 查询
	 * @param wealthTypeId
	 * @param wealthStatus
	 * @return
	 */
	public List<WealthInfoEntity> findByWealthTypeIdAndWealthStatusOrderByEffectDate(String wealthTypeId, String wealthStatus);
	
	/**
	 * 查询可流标的理财计划
	 *
	 * @author  wangjf
	 * @date    2016年3月1日 下午6:21:40
	 * @param currentDate
	 * @return
	 */
	@Query("select A from WealthInfoEntity A "
			+ "where (effectDate < :effectDate and wealthStatus = :wealthStatus1) "
			+ "or (effectDate < :effectDate and wealthStatus = :wealthStatus2)"
			+ "or (effectDate < :effectDate and wealthStatus = :wealthStatus3 "
			+ "		and A.id in (select B.wealthId "
			+ "					 from ProjectInvestInfoEntity B"
			+ "					 where B.alreadyInvestAmount = 0)"
			+ ") " )
	public List<WealthInfoEntity> findCanUnReleaseWealth(@Param("effectDate") Date effectDate, @Param("wealthStatus1")String wealthStatus1, 
			@Param("wealthStatus2")String wealthStatus2, @Param("wealthStatus3")String wealthStatus3);
	
}
