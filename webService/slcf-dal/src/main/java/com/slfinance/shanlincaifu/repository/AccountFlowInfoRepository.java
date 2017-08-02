package com.slfinance.shanlincaifu.repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.slfinance.shanlincaifu.entity.AccountFlowInfoEntity;

/**
 * BaoTAccountInfo entity. @author MyEclipse Persistence Tools
 * 
 * 
 */
@RepositoryRestResource(collectionResourceRel = "accountFlow", path = "accountFlow")
public interface AccountFlowInfoRepository extends PagingAndSortingRepository<AccountFlowInfoEntity, String>, JpaSpecificationExecutor<AccountFlowInfoEntity>{
	
	@Query("select sum(tradeAmount) from AccountFlowInfoEntity where tradeType=?")
	public BigDecimal sumTradeAmountByTradeType(String tradeType);
	
	public Page<AccountFlowInfoEntity> findByTradeType(@Param("tradeType") String tradeType, Pageable page);
	
	public AccountFlowInfoEntity findFirstByTradeNoOrderByCreateDateDesc(String tradeNo);
	
	public AccountFlowInfoEntity findByRelatePrimaryAndTradeType(String relatePrimary,String tradeType);
	
	public AccountFlowInfoEntity findFirstByRequestNoOrderByCreateDateDesc(String requestNo);
	
	@Query( " SELECT A "
			+" FROM AccountFlowInfoEntity A "
			+" WHERE A.relateType = ?1 AND A.relatePrimary = ?2 ")
	public List<AccountFlowInfoEntity> queryByFlowBusi(String relateType, String relatePrimary);
	
	/**
	 * 汇总用户某种交易类型
	 *
	 * @author  wangjf
	 * @date    2015年7月24日 上午10:40:36
	 * @param custId
	 * @param tradeType
	 * @return
	 */
	@Query("select NVL(sum(tradeAmount), 0) from AccountFlowInfoEntity where custId= ? and tradeType= ?")
	public BigDecimal sumTradeAmountByCustIdAndTradeType(String custId, String tradeType);
	
	/**
	 * 按照客户交易类型和创建时间统计
	 *
	 * @author  wangjf
	 * @date    2016年1月19日 下午2:01:21
	 * @param custId
	 * @param tradeType
	 * @param tradeDate
	 * @return
	 */
	@Query(value = "select count(id) from BAO_T_ACCOUNT_FLOW_INFO where CUST_ID = ?1 AND TRADE_TYPE = ?2 AND CREATE_DATE > ?3 AND CREATE_DATE < ?3 + 1", nativeQuery = true) 
	public int countByCustIdAndTradeTypeAndCreateDate(String custId, String tradeType, Date createDate);

	/**
	 * 通过关联id查询
	 * @param investId
	 * @return
	 */
	AccountFlowInfoEntity findByRelatePrimary(String investId);
	
	/**
	 * 通过交易类型和关联主键查询
	 *
	 * @author  wangjf
	 * @date    2017年7月11日 下午8:07:26
	 * @param tradeType
	 * @param relatePrimary
	 * @return
	 */
	@Query(value = "select count(id) from BAO_T_ACCOUNT_FLOW_INFO where TRADE_TYPE = ?1 AND RELATE_PRIMARY = ?2", nativeQuery = true) 
	public int countByTradeTypeAndRelatePrimary(String tradeType, String relatePrimary);
}