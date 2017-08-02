package com.slfinance.shanlincaifu.repository;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.slfinance.shanlincaifu.entity.WealthHoldInfoEntity;


/**   
 * BAO理财计划用户持有情况表数据访问接口
 *  
 * @author  Tool
 * @version $Revision:1.0.0, $Date: 2016-02-23 13:38:49 $ 
 */
public interface WealthHoldInfoRepository extends PagingAndSortingRepository<WealthHoldInfoEntity, String>{

	/**
	 * 通过客户ID和债权ID查询持有情况
	 *
	 * @author  wangjf
	 * @date    2016年2月23日 下午1:41:38
	 * @param custId
	 * @param loanId
	 * @return
	 */
	public List<WealthHoldInfoEntity> findByCustIdAndLoanId(String custId, String loanId);
	
	/**
	 * 通过债权ID查询持有情况
	 *
	 * @author  wangjf
	 * @date    2016年2月23日 下午1:41:38
	 * @param loanId
	 * @return
	 */
	public List<WealthHoldInfoEntity> findByLoanId(String loanId);
	
	/**
	 * 通过投资ID和状态查询持有信息
	 *
	 * @author  wangjf
	 * @date    2016年3月2日 下午2:25:10
	 * @param investId
	 * @param holdStatus
	 * @return
	 */
	public List<WealthHoldInfoEntity> findByInvestIdAndHoldStatus(String investId, String holdStatus);
	
	/**
	 * 通过客户ID、债权ID、理财计划ID查询数据
	 *
	 * @author  wangjf
	 * @date    2016年3月7日 下午4:21:32
	 * @param custId
	 * @param loanId
	 * @param wealthId
	 * @return
	 */
	@Query("select A from WealthHoldInfoEntity A where A.custId = :custId and A.loanId = :loanId and A.investId in (select B.id from InvestInfoEntity B where B.wealthId = :wealthId)")
	public WealthHoldInfoEntity findByCustIdAndLoanIdAndWealthId(@Param("custId")String custId, @Param("loanId")String loanId, @Param("wealthId")String wealthId);
	
	/**
	 * 通过借款ID和持有状态查询
	 *
	 * @author  wangjf
	 * @date    2016年12月6日 下午8:37:43
	 * @param loanId
	 * @param holdStatus
	 * @return
	 */
	public List<WealthHoldInfoEntity> findByLoanIdAndHoldStatus(String loanId, String holdStatus);
	
	/**
	 * 通过投资查询持有情况(仅针对一笔投资一笔持有情况的情况)
	 *
	 * @author  wangjf
	 * @date    2017年1月3日 上午10:54:41
	 * @param investId
	 * @return
	 */
	public WealthHoldInfoEntity findByInvestId(String investId);
}
