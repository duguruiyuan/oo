/** 
 * @(#)SubAccountInfoRepository.java 1.0.0 2015年4月27日 下午5:31:27  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.slfinance.shanlincaifu.entity.SubAccountInfoEntity;

/**   
 * 分账户数据访问类
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年4月27日 下午5:31:27 $ 
 */
public interface SubAccountInfoRepository extends PagingAndSortingRepository<SubAccountInfoEntity, String> {

	/** 根据分账号查询分账信息 */
	public SubAccountInfoEntity findBySubAccountNo(@Param("subAccountNo") String subAccountNo);
	
	/**
	 * 通过投资编号查找分账信息
	 *
	 * @author  wangjf
	 * @date    2015年4月29日 下午4:14:12
	 * @param relatePrimary
	 * @return
	 */
	public SubAccountInfoEntity findByRelatePrimary(@Param("relatePrimary") String relatePrimary);
	
	/**
	 * 统计用户所拥有的债权价值(仅有效的)
	 *
	 * @author  wangjf
	 * @date    2015年4月30日 下午3:32:44
	 * @param custId
	 * @return
	 */
	@Query("select NULLIF(sum(accountAvailableValue), 0) from SubAccountInfoEntity A where relatePrimary in (select id from InvestInfoEntity B where B.investStatus= ?3 and B.custId = ?1 and B.productId = ?2)")
	public BigDecimal queryUserAllValue(String custId, String productId, String investStatus);
	
	/**
	 * 统计用户所拥有的债权价值(仅有效的)
	 *
	 * @author  wangjf
	 * @date    2015年4月30日 下午3:32:44
	 * @param custId
	 * @return
	 */
	@Query("select NULLIF(sum(accountTotalValue), 0) from SubAccountInfoEntity A where relatePrimary in (select id from InvestInfoEntity B where B.investStatus= ?3 and B.custId = ?1 and B.productId = ?2)")
	public BigDecimal queryUserTotalValue(String custId, String productId, String investStatus);
	
	/**
	 * 统计用户所拥有的债权价值(仅有效的)
	 *
	 * @author  wangjf
	 * @date    2015年4月30日 下午3:32:44
	 * @param custId
	 * @return
	 */
	@Query("select NULLIF(sum(accountTotalValue), 0) from SubAccountInfoEntity A where relatePrimary in (select id from InvestInfoEntity B where B.investStatus= ?3 and B.custId = ?1 and B.productId = ?2 and B.currTerm = ?4)")
	public BigDecimal queryUserTotalValue(String custId, String productId, String investStatus, String currTerm);
	
	/**
	 * 查询用户所有有效投资的分账信息
	 *
	 * @author  wangjf
	 * @date    2015年4月30日 下午4:23:19
	 * @param custId
	 * @return
	 */
	@Query("select A from SubAccountInfoEntity A where relatePrimary in (select id from InvestInfoEntity B where B.investStatus= ?3 and B.custId = ?1 and B.productId = ?2)")
	public List<SubAccountInfoEntity> findByCustId(String custId, String productId, String investStatus);
	
	/**
	 * 结息后更新用户的分账价值
	 * @param totalValue
	 * @param accountAvailableValue
	 * @param id
	 * @return
	 */
	@Modifying
	@Query("update SubAccountInfoEntity set ACCOUNT_TOTAL_VALUE=?,ACCOUNT_AVAILABLE_VALUE=?,LAST_UPDATE_DATE=? where id=?")
	public int updateById(BigDecimal totalValue,BigDecimal accountAvailableValue,Date updateDate,String id);
	
	/**
	 * 到期赎回更新用户的分账价值为0，并置状态为无效
	 * @param totalValue
	 * @param accountAvailableValue
	 * @param id
	 * @return
	 */
	@Modifying
	@Query("update SubAccountInfoEntity set ACCOUNT_TOTAL_VALUE=0,ACCOUNT_AVAILABLE_VALUE=0,RECORD_STATUS='无效',LAST_UPDATE_DATE=? where id=?")
	public int updateByIdForWithdraw(Date updateDate,String id);

	/**
	 * 查询赎回中记录
	 *
	 * @author  wangjf
	 * @date    2015年8月14日 下午4:40:01
	 * @param typeName
	 * @param investStatus
	 * @param atoneStatus
	 * @return
	 */
	@Query(value = "select t.* "
	+" from bao_t_sub_account_info t, bao_t_invest_info p, bao_t_atone_info q "
	+" where t.relate_primary = p.id and p.id = q.invest_id "
	+" and q.product_id in (select a.id from bao_t_product_info a, bao_t_product_type_info b where a.product_type = b.id and b.type_name = ?1) "
	+" and q.atone_status = ?2 and t.account_freeze_value != 0 "
	+" and q.create_date <= ?3 "
	+" order by q.create_date asc, p.create_date asc ", nativeQuery = true)
	public List<SubAccountInfoEntity> queryTermAtone(String typeName, String atoneStatus, Date date);
	
	/**
	 * 查询赎回中金额
	 *
	 * @author  wangjf
	 * @date    2015年8月17日 上午9:54:34
	 * @param typeName
	 * @param investStatus
	 * @param atoneStatus
	 * @return
	 */
	@Query(value = "select sum(t.account_freeze_value)  "
	+" from bao_t_sub_account_info t, bao_t_invest_info p, bao_t_atone_info q  "
	+" where t.relate_primary = p.id and p.id = q.invest_id  "
	+" and q.product_id in (select a.id from bao_t_product_info a, bao_t_product_type_info b where a.product_type = b.id and b.type_name = ?1)  "
	+" and q.atone_status = ?2 and t.account_freeze_value != 0  ", nativeQuery = true)
	public BigDecimal sumTermAtone(String typeName, String atoneStatus);
	
	/**
	 * 查询预计待匹配金额
	 * @author zhiwen_feng
	 * @param endDate
	 * @return
	 */
	@Query(nativeQuery = true, value = "select trunc(nvl(sum(sa.account_amount), 0), 2)  "
			+ "								from bao_t_wealth_info t  "
			+ "									inner join bao_t_invest_info i on i.wealth_id = t.id "
			+ "									inner join bao_t_sub_account_info sa on sa.relate_primary = i.id and sa.relate_type = 'BAO_T_INVEST_INFO' "
			+ "								where sa.account_amount > 0 ")
	public BigDecimal sumAllWealthAccountAmount();
	
	/**
	 * 截止日期赎回价值
	 * 
	 * @author zhiwen_feng
	 * @param endDate
	 * @return
	 */
	@Query(nativeQuery = true, value = "select trunc(nvl(sum(decode(nvl(p.repayment_status, '未还款'), '未还款', c.value_repayment_before, c.value_repayment_after) * h.hold_scale), 0), 2) "
			+ "								 from "
			+ "									bao_t_invest_info i"
			+ "						 inner join bao_t_wealth_info t on t.id = i.wealth_id"
			+ "						 inner join bao_t_wealth_hold_info h on h.invest_id = i.id"
			+ "						 inner join bao_t_credit_right_value c on c.loan_id = h.loan_id and c.value_date = to_char(sysdate, 'yyyyMMdd') "
			+ "						 left join  bao_t_repayment_plan_info p on p.loan_id = c.loan_id and p.expect_repayment_date = to_char(sysdate, 'yyyyMMdd')  "
			+ "						where i.invest_status in ('收益中', '到期处理中') and to_char(t.end_date, 'yyyyMMdd') >= to_char(sysdate, 'yyyyMMdd') "
			+ "							and to_char(t.end_date, 'yyyyMMdd') <= ?1" )
	public BigDecimal sumAllBackValue(String endDate);
	
	/**
	 * 截止日期提前退出价值
	 * @author zhiwen_feng
	 * @param endDate
	 * @return
	 */
	@Query(nativeQuery = true, value = "select trunc(nvl(sum(decode(nvl(p.repayment_status, '未还款'), '未还款', c.value_repayment_before, c.value_repayment_after) * h.hold_scale), 0), 2) "
			+ "								 from bao_t_invest_info i  "
			+ "						 inner join BAO_T_ATONE_INFO a on a.invest_id = i.id "
			+ "						 inner join bao_t_wealth_hold_info h on h.invest_id = i.id  "
			+ "						 inner join bao_t_credit_right_value c on c.loan_id = h.Loan_Id and c.value_date = to_char(sysdate, 'yyyyMMdd')   "
			+ "						left join  bao_t_repayment_plan_info p on p.loan_id = c.loan_id and p.expect_repayment_date = to_char(sysdate, 'yyyyMMdd')"
			+ "					where i.invest_status = '提前赎回中' and to_char(a.cleanup_date, 'yyyyMMdd') <= ?1" )
	public BigDecimal sumAllEarlyExitValue(String endDate);
	
	/**
	 * 查询所有分账户
	 *
	 * @author  wangjf
	 * @date    2016年12月6日 下午8:34:42
	 * @param loanId
	 * @return
	 */
	@Query("select A from SubAccountInfoEntity A where A.relatePrimary in (select B.investId from WealthHoldInfoEntity B where loanId = :loanId and holdStatus = :holdStatus)")
	public List<SubAccountInfoEntity> findAllSubAccountByLoanId(@Param("loanId")String loanId, @Param("holdStatus")String holdStatus);
	
	 
	/**
	 * 通过借款ID查询分账户
	 *
	 * @author  wangjf
	 * @date    2017年1月5日 上午9:22:55
	 * @param loanId
	 * @return
	 */
	@Query("select A from SubAccountInfoEntity A where A.relatePrimary in (select B.id from InvestInfoEntity B where loanId = :loanId)")
	public List<SubAccountInfoEntity> findSubAccountWithInvestByLoanId(@Param("loanId")String loanId);
}
