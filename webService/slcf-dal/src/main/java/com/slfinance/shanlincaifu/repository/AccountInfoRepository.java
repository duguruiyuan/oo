package com.slfinance.shanlincaifu.repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.slfinance.shanlincaifu.entity.AccountInfoEntity;

/**
 * BaoTAccountInfo entity. @author MyEclipse Persistence Tools
 * 
 * 
 */
@RepositoryRestResource(collectionResourceRel = "accounts", path = "accounts")
public interface AccountInfoRepository extends PagingAndSortingRepository<AccountInfoEntity, String> {
	/** 根据手机号查询记录 */
	public AccountInfoEntity findByAccountNo(@Param("accountNo") String accountNo);
	/**根据客户id查询客户的账户信息*/
	public AccountInfoEntity findByCustId(@Param("custId") String custId);
	
	/**
	 * 根据客户信息主键查询交易流水信息
	 * @author  zhangzs
	 * @param custId
	 * @return
	 */
	public AccountInfoEntity findFirstByCustIdOrderByCreateDateDesc(String custId);
	/**
	 * 体验宝到期赎回更新用户的主账价值
	 * @param totalValue
	 * @param accountAvailableValue
	 * @param id
	 * @return
	 */
	@Modifying 
	@Query("update AccountInfoEntity set ACCOUNT_TOTAL_AMOUNT=?,ACCOUNT_AVAILABLE_AMOUNT=?,LAST_UPDATE_DATE=? where id=?")
	public int updateByIdForWithdraw(BigDecimal totalValue,BigDecimal accountAvailableValue,Date updateDate,String id);
	
	/**
	 * 查询企业借款累计赎回
	 * 
	 * @author liyy
	 * @date 2016年3月16日 
	 * @param custId :String:客户ID
	 * @return String custAccountId
	 */
	@Query(value=" SELECT ID \"custAccountId\" FROM BAO_T_ACCOUNT_INFO WHERE CUST_ID = ?1 ", nativeQuery = true)
	public String findCustAccountIdByCustId(String custId);
	
	/**
	 * 通过项目查询账户信息（仅新投资部分）
	 *
	 * @author  wangjf
	 * @date    2016年1月6日 上午11:34:37
	 * @param projectId
	 * @return
	 */
	@Query("select A from AccountInfoEntity A where A.custId in (select B.custId from InvestInfoEntity B where loanId = :loanId)")
	public List<AccountInfoEntity> findAccountByLoanId(@Param("loanId")String loanId);
	
	/**
	 * 通过项目查询账户信息(含债权转让部分)
	 *
	 * @author  wangjf
	 * @date    2016年1月6日 上午11:34:37
	 * @param projectId
	 * @return
	 */
	@Query("select A from AccountInfoEntity A where A.custId in (select B.custId from WealthHoldInfoEntity B where loanId = :loanId and holdStatus = :holdStatus)")
	public List<AccountInfoEntity> findAllAccountByLoanId(@Param("loanId")String loanId, @Param("holdStatus")String holdStatus);
}