/** 
 * @(#)InvestInfoRepository.java 1.0.0 2015年4月21日 下午3:44:35  
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

import com.slfinance.shanlincaifu.entity.InvestInfoEntity;
import com.slfinance.shanlincaifu.utils.Constant;

/**   
 * 
 *  
 * @author  HuangXiaodong
 * @version $Revision:1.0.0, $Date: 2015年4月21日 下午3:44:35 $ 
 */
public interface InvestInfoRepository extends PagingAndSortingRepository<InvestInfoEntity, String>{
	
	@Query("select sum(investAmount) from InvestInfoEntity where productId=?")
	public BigDecimal sumInvestAmountByProductId(String productId);
	
	/**根据用户id和产品类型查询持有金额*/
	@Query("select NULLIF(sub.accountAmount,0) from SubAccountInfoEntity sub"
           +" where sub.custId = ("
           +" select  inv.custId  from InvestInfoEntity inv"  
           +" where inv.custId=? and inv.productId= (select pr.id from ProductInfoEntity  pr where pr.productType=?)) "
		   )
	public BigDecimal accountAmountBycustIdAndProductType(String custId,String productType);
	/**根据用户id和产品类型查询投资金额和条数*/
	@Query("select  sum(inv.investAmount) ,  count(*)  from InvestInfoEntity inv"  
           +" where inv.custId=? and inv.productId= (select pr.id from ProductInfoEntity  pr where pr.productType=?) "
		   )
	public List<Object[]> investAmountBycustIdAndProductType(String custId,String productType);
	
	/**
	 * 根据客户ID查询投资信息(投资次数)
	 *
	 * @author  wangjf
	 * @date    2015年4月29日 下午2:47:43
	 * @param custId
	 * @return
	 */
	@Query("select count(A.id), NULLIF(sum(A.investAmount),0) from InvestInfoEntity A where A.investStatus=?3 and A.custId = ?1 and A.productId = ?2 ")
	public List<Object[]> findInvestInfoByCustId(String custId, String productId, String investStatus);
	
	/**
	 * 根据客户ID查询投资信息(投资金额)
	 *
	 * @author  wangjf
	 * @date    2015年5月27日 下午6:23:41
	 * @param custId
	 * @param productId
	 * @return
	 */
	@Query(value=" SELECT NVL(SUM(TRUNC_AMOUNT_WEB(INVEST_AMOUNT)),0) FROM BAO_T_INVEST_INFO A WHERE A.INVEST_STATUS = ?3 AND A.CUST_ID = ?1 AND A.PRODUCT_ID = ?2 ", nativeQuery = true)
	public BigDecimal queryTotalInvestAmountByCustId(String custId, String productId, String investStatus);
	
	/**
	 * 通过客户ID和投资日期查询投资
	 *
	 * @author  wangjf
	 * @date    2015年4月29日 下午2:47:02
	 * @param custId
	 * @param investDate
	 * @return
	 */
	@Query("select A from InvestInfoEntity A where A.investStatus=?4 and A.custId = ?1 and A.productId = ?2 and investDate = ?3 ")
	public InvestInfoEntity findInvestInfoByCustIdAndInvestDate(String custId, String productId, String investDate, String investStatus);
	
	/**
	 * 查询某产品单天投资金额
	 *
	 * @author  wangjf
	 * @date    2015年8月22日 上午11:09:48
	 * @param custId
	 * @param productId
	 * @param investDate
	 * @return
	 */
	@Query("select NULLIF(sum(A.investAmount),0) from InvestInfoEntity A where A.investStatus=?3 and A.productId = ?1 and investDate = ?2 and currTerm = ?4 ")
	public BigDecimal sumTotalInvestAmountByProductIdAndInvestDate(String productId, String investDate, String investStatus, String currTerm);
	
	/**
	 * 查询用户所有有效投资信息
	 *
	 * @author  wangjf
	 * @date    2015年4月30日 下午4:16:59
	 * @param custId
	 * @return
	 */
	@Query("select A from InvestInfoEntity A where A.investStatus=?3 and A.custId = ?1 and A.productId = ?2 ORDER BY A.investDate ASC")
	public List<InvestInfoEntity> findByCustId(String custId, String productId, String investStatus);
	
	/**
	 * 取赎回的投资
	 *
	 * @author  wangjf
	 * @date    2015年5月11日 下午1:58:32
	 * @param custId
	 * @param productId
	 * @return
	 */
	@Query(" SELECT C  "
			+" FROM InvestInfoEntity C  "
			+" WHERE C.id IN ( "
			+"        SELECT D.relatePrimary  "
			+"        FROM SubAccountInfoEntity D  "
			+"        WHERE D.id IN ( "
			+"             SELECT A.accountId "
			+"             FROM AccountFlowInfoEntity A "
			+"             WHERE A.relateType = ?  AND A.relatePrimary = ? "
			+"        )  "
			+" ) ")
	public List<InvestInfoEntity> queryByAtoneId(String relateType, String relatePrimary);
	
	/**
	 * 到期赎回更新投资状态为无效
	 * @param 投资id
	 * 
	 * @return
	 */
	@Modifying
	@Query("update InvestInfoEntity set INVEST_STATUS='无效',LAST_UPDATE_DATE=? where id=?")
	public int updateStatusByIdForWithdraw(Date updateDate,String id);
	
	/**
	 * 根据客户ID查询投资信息(投资次数)
	 *
	 * @author  wangjf
	 * @date    2015年4月29日 下午2:47:43
	 * @param custId
	 * @return
	 */
	@Query("select count(A.id) from InvestInfoEntity A where A.custId = ? and A.productId = ?")
	public BigDecimal countInvestInfoByCustId(String custId, String productId);
	
	/**
	 * 查询客户累计投资金额
	 *
	 * @author	zhangzs
	 * @date    2015年6月18日 下午11:23:41
	 * @param custId
	 * @param productType
	 * @return
	 */
	@Query(value=" SELECT NVL(SUM(TRUNC_AMOUNT_WEB(INVEST_AMOUNT)),0) FROM BAO_T_INVEST_INFO A WHERE A.CUST_ID = ?1 AND A.PRODUCT_ID = ( SELECT ID FROM BAO_T_PRODUCT_INFO WHERE PRODUCT_NAME = ?2 AND ROWNUM = 1 ) ", nativeQuery = true)
	public BigDecimal getAlreadyInvestAmount(String custId, String productType);
	
	/**
	 * 查询客户某一产品的累计投资金额
	 *
	 * @author	zhangzs
	 * @date    2015年8月24日 下午14:30:31
	 * @param custId
	 * @param productType
	 * @return
	 */
	@Query(value=" SELECT NVL(SUM(TRUNC_AMOUNT_WEB(INVEST_AMOUNT)), 0) FROM BAO_T_INVEST_INFO A WHERE A.CUST_ID = ?1 AND A.PRODUCT_ID IN (SELECT ID FROM BAO_T_PRODUCT_INFO WHERE PRODUCT_TYPE IN (SELECT ID FROM BAO_T_PRODUCT_TYPE_INFO WHERE TYPE_NAME = ?2)) ", nativeQuery = true)
	public BigDecimal getInvestedAmount(String custId, String productType);
	
	/**
	 * 查询客户某一产品、某一期的累计投资金额
	 *
	 * @author	zhangzs
	 * @date    2015年8月26日 下午14:30:31
	 * @param custId
	 * @param productId
	 * @param currTerm
	 * @return
	 */
	@Query(value=" SELECT NVL(SUM(TRUNC_AMOUNT_WEB(INVEST_AMOUNT)), 0) FROM BAO_T_INVEST_INFO A WHERE A.CUST_ID = ? AND A.PRODUCT_ID = ?  AND A.CURR_TERM = ? ", nativeQuery = true)
	public BigDecimal getInvestedAmountByCurTerm(String custId, String productId,String currTerm);
	
	/**
	 * 查询客户某一产品、某一期的累计投资金额
	 *
	 * @author	zhangzs
	 * @date    2015年8月26日 下午14:30:31
	 * @param productId
	 * @param currTerm
	 * @return
	 */
	@Query(value=" SELECT NVL(SUM(TRUNC_AMOUNT_WEB(INVEST_AMOUNT)), 0) FROM BAO_T_INVEST_INFO A WHERE A.PRODUCT_ID = ?  AND A.CURR_TERM = ? ", nativeQuery = true)
	public BigDecimal getInvestedAmountByCurTerm(String productId,String currTerm);
	
	/**
	 * 查询客户某一产品在投金额的总和(收益中、提前赎回中、到期处理中)
	 *
	 * @author	zhangzs
	 * @date    2015年8月26日 下午14:30:31
	 * @param custId
	 * @param typeName
	 * @return
	 */
	@Query(value=" SELECT NVL(SUM(TRUNC_AMOUNT_WEB(INVEST_AMOUNT)), 0) FROM BAO_T_INVEST_INFO A WHERE A.INVEST_STATUS IN ('收益中','到期处理中','提前赎回中')  AND A.CUST_ID = ?1 AND A.PRODUCT_ID IN (SELECT ID FROM BAO_T_PRODUCT_INFO WHERE PRODUCT_TYPE IN (SELECT ID FROM BAO_T_PRODUCT_TYPE_INFO WHERE TYPE_NAME = ?2)) ", nativeQuery = true)
	public BigDecimal getInvestingAmout(String custId,String typeName);
	
	@Query(value = "select s.* "
			+ " from bao_t_invest_info s "
			+ " where "
			+ " s.invest_status = ?1 and s.expire_date <= to_char(?2, 'yyyyMMdd') "
			+ " and s.product_id in (select a.id from bao_t_product_info a, bao_t_product_type_info b where a.product_type = b.id and b.type_name = ?3) "
			+ " and not exists ( "
			+ "     select 1 "
			+ "     from bao_t_atone_info m "
			+ "     where m.invest_id = s.id "
			+ " ) order by s.create_date asc ", nativeQuery = true)
	public List<InvestInfoEntity> queryCanTermAtoneInvest(String investStatus, Date now, String productName);
	
	/**
	 * 查询某段时间内的投资次数
	 * 
	 * @param custId
	 * @param productId
	 * @param investStartDate
	 * @param investEndDate
	 * @param investStatus
	 * @return
	 */
	@Query("select count(B.id) from InvestDetailInfoEntity B where B.investId in (select A.id from InvestInfoEntity A where A.custId = ?1 and A.productId in (select c.id from ProductInfoEntity c  where c.productType in (select d.id from ProductTypeInfoEntity d where d.typeName = ?2)) and investDate between ?3 and ?4) ")
	public BigDecimal countInvestInfoByCustIdAndInvestDate(String custId, String productId, String investStartDate, String investEndDate);
	
	/**
	 * 查询用户是否投资过某个产品(如定期宝、活期宝、体验宝等)
	 *
	 * @author  wangjf
	 * @date    2015年12月3日 下午3:59:21
	 * @param custId
	 * @param productId
	 * @return
	 */
	@Query("select count(B.id) from InvestDetailInfoEntity B where B.investId in (select A.id from InvestInfoEntity A where A.custId = ?1 and A.productId in (select c.id from ProductInfoEntity c  where c.productType in (select d.id from ProductTypeInfoEntity d where d.typeName = ?2))) ")
	public BigDecimal countInvestInfoByCustIdAndInvestDate(String custId, String productId);
	
	/**
	 * 根据客户id查询企业借款投资金额
	 * @param custId
	 * @param investStatus
	 * @return
	 */
	@Query(value=" SELECT NVL(SUM(TRUNC_AMOUNT_WEB(INVEST_AMOUNT)),0) FROM BAO_T_INVEST_INFO A WHERE A.INVEST_STATUS = ?2 AND A.CUST_ID = ?1 AND A.PROJECT_ID is not null ", nativeQuery = true)
	public BigDecimal queryTotalProjectInvestAmountByCustId(String custId, String investStatus);
	
	/**
	 * 查看客户项目状态为发布中, 满标复核的投资金额
	 * @param custId
	 * @return
	 */
	@Query(value = "select TRUNC_AMOUNT_WEB(sum(a.invest_amount)) from bao_t_invest_info a, bao_t_project_info b where a.project_id = b.id and b.project_status in ('"+Constant.PROJECT_STATUS_06+"', '"+Constant.PROJECT_STATUS_07+"') and a.invest_status = '有效' and a.cust_id = ?1 and a.loan_id is null", nativeQuery = true)
	public BigDecimal queryProjectInvestAmountByCustId(String custId);
	/**
	 * 根据客户id查询企业借款累计投资金额
	 * @param custId
	 * @return
	 */
	@Query(value=" SELECT NVL(SUM(trunc(A.INVEST_AMOUNT, 2)),0) FROM BAO_T_INVEST_INFO A WHERE A.CUST_ID = ?1 AND A.PROJECT_ID is not null ", nativeQuery = true)
	public BigDecimal queryTotalProjectInvestAmountByCustId(String custId);
	
	/**
	 * 根据客户id,投资状态(项目状态不为流标)查询企业借款累计投资金额
	 * @param custId
	 * @param investStatus
	 * @return
	 */
	@Query(value= "select nvl(sum(TRUNC_AMOUNT_WEB(INVEST_AMOUNT)),0) from bao_t_invest_info btii, bao_t_project_info btpi where btii.project_id = btpi.id and btpi.project_status <> '流标' and btii.invest_status = ?2 and btii.cust_id = ?1", nativeQuery = true)
	public BigDecimal queryAlreadyProjectInvestAmountByCustId(String custId, String investStatus);
	
	@Query(value="select btii.project_id \"projectId\" from bao_t_invest_info btii where btii.cust_id = ? and btii.project_id is not null", nativeQuery = true)
	public List<String> findProjectIdByCustId(String custId);
	
	/**
	 * 根据客户Id查询预期收益
	 * @param custId
	 * @return
	 */
	@Query(value = "select nvl(sum(nvl(trunc(max(btrpi.repayment_interest) *"
			+ "                                     sum(btii.invest_amount) /"
			+ "                                      max(btpii.already_invest_amount), 2),"
			+ "                0)"
			+ "         + nvl(trunc(sum(btii.invest_amount) * max(btpi.award_rate) / 12 * max(btpi.type_term), 2), 0)), 0)"
			+ "  from bao_t_invest_info         btii,"
			+ "        (select sum(a.repayment_interest) as repayment_interest, a.project_id as project_id from bao_t_repayment_plan_info a "
			+ "          where a.repayment_status = '未还款' and a.is_riskamount_repay = '否' group by a.project_id) btrpi,"
			+ "       bao_t_project_invest_info btpii,"
			+ "       bao_t_project_info btpi "
			+ " where btii.project_id = btrpi.project_id"
			+ "   and btpii.project_id = btii.project_id"
			+ "   and btii.project_id = btpi.id"
			+ "   and btii.cust_id = ?1"
			+"    and btii.loan_id is null"
			+ " group by btii.project_id", nativeQuery = true)
	public BigDecimal queryProjectExceptAmountByCustId(String custId);
	
	/**
	 * 根据客户id和项目id查询客户投资金额
	 * @param custId
	 * @param projectId
	 * @return
	 */
	@Query(value = "select nvl(sum(TRUNC_AMOUNT_WEB(btii.invest_amount)),0) from bao_t_invest_info btii where btii.cust_id = ?1 and btii.project_id = ?2 and btii.invest_status = '有效'", nativeQuery = true)
	public BigDecimal queryAlreadyInvestAmountByCustIdAndProjectId(String custId, String projectId);
	
	/**
	 * 根据客户id和项目id查询客户累计投资金额
	 * @param custId
	 * @param projectId
	 * @return
	 */
	@Query(value = "select nvl(sum(TRUNC_AMOUNT_WEB(btii.invest_amount)),0) from bao_t_invest_info btii where btii.cust_id = ?1 and btii.project_id = ?2", nativeQuery = true)
	public BigDecimal queryTotalInvestAmountByCustIdAndProjectId(String custId, String projectId);
	
	/**
	 * 查询代收本金
	 * @param custId
	 * @return
	 */
	@Query(value = "select nvl(sum(max(btrpi.repayment_principal) * sum(btii.invest_amount) /"
			+ "       max(btpii.already_invest_amount)), 0)"
			+ "  from bao_t_invest_info btii,"
			+ "       (select a.project_id as project_id,  sum(a.repayment_principal) as repayment_principal"
			+ "          from bao_t_repayment_plan_info a"
			+ "         where a.repayment_status = '未还款' and a.is_riskamount_repay = '否' group by a.project_id) btrpi,"
			+ "       bao_t_project_invest_info btpii"
			+ " where btii.project_id = btpii.project_id"
			+ "   and btii.project_id = btrpi.project_id"
			+ "   and btii.cust_id = ?1"
			+"    and btii.loan_id is null"
			+ " group by btii.project_id", nativeQuery = true)
	public BigDecimal queryProjectRemainderPrincipal(String custId);

	/** 根据客户id查询所有持有债权价值总和 */
	@Query(value = "select nvl(sum(decode(nvl(p.repayment_status, '未还款'), '未还款', val.value_repayment_before, val.value_repayment_after) * h.hold_scale), 0)"
			+"  	from "        
			+ "  		BAO_T_WEALTH_HOLD_INFO h "  
			+ "			inner join bao_t_credit_right_value val on val.loan_id = h.loan_id and val.value_date = ?2"  
			+ "			left join bao_t_repayment_plan_info p on p.loan_id = h.loan_id and p.expect_repayment_date = ?2"
			+ "	where h.is_center =  ?1 ", nativeQuery = true)
	public BigDecimal findAllHoldCreditValueByCustId(String isCenter, String endDate);
	
	/**
	 * 根据理财计划查询投资信息
	 * 
	 * @author zhiwen_feng
	 * @param wealthId
	 * @return
	 */
	public List<InvestInfoEntity> findByWealthId(String wealthId);
	
	/**
	 * 根据理财计划id、客户id查询投资信息
	 * @author zhiwen_feng
	 * @param wealthId
	 * @param custId
	 * @param investStatus
	 * @return
	 */
	public InvestInfoEntity findByWealthIdAndCustIdAndInvestStatus(String wealthId, String custId, String investStatus);
	
	/**
	 * 根据理财计划id、客户id查询投资信息
	 * @param wealthId
	 * @param custId
	 * @return
	 */
	public InvestInfoEntity findByWealthIdAndCustId(String wealthId, String custId);
	
	/**
	 * 根据客户id和投资状态查询优选计划在投金额
	 * 
	 * @author zhangt
	 * @date   2016年3月7日下午5:27:43
	 * @param custId
	 * @param investStatus
	 * @return
	 */
	@Query(value = "select TRUNC_AMOUNT_WEB(sum(trunc(binv.invest_amount, 2))) from bao_t_invest_info binv where binv.cust_id = ? and binv.invest_status = ? and binv.loan_id is null ", nativeQuery = true)
	public BigDecimal queryPlanInvestAmountByCustId(String custId, String investStatus);
	
	/**
	 * 根据投资时间查询投资
	 * @author zhiwen_feng
	 * @param date
	 * @return
	 */
	@Query(value = "select i.* from bao_t_invest_info i where i.create_date < ?1 and i.cust_id = ?2", nativeQuery = true)
	public List<InvestInfoEntity> findInvestInfoByInvestDate(Date date, String custId);
	
	/**
	 * 通过借款ID查询投资
	 *
	 * @author  wangjf
	 * @date    2016年11月28日 下午7:43:06
	 * @param loanId
	 * @return
	 */
	public List<InvestInfoEntity> findByLoanId(String loanId);

	/**
	 * 根据借款loanId、客户id查询投资信息
	 * @author lyy
	 * @param loanId
	 * @param custId
	 * @param investStatus
	 * @return
	 */
	public int countByLoanIdAndCustIdAndInvestStatus(String loanId, String custId, String investStatus);
	/**
	   * 根据客户ID查询投资信息(投资次数)
	   *
	   * @author  fengyl
	   * @date    2017年3月29日 
	   * @param custId
	   * @return
	   */
	  @Query(value ="select count(i.id) from bao_t_invest_info i,bao_t_loan_info l where i.loan_id=l.id and i.invest_status in ('投资中','收益中','提前赎回中','提前赎回','到期赎回中','到期赎回','已到期','已转让') and l.newer_flag='新手标' and i.cust_id  = ?",nativeQuery = true)
	  public BigDecimal investCountInfoByCustId(String custId);

	/**
	 * 2017 市场部6月活动
	 */
	@Query(value=" SELECT c.ID FROM bao_t_invest_info i, BAO_T_LOAN_INFO l, BAO_T_CUST_INFO c WHERE c.memo2 = ?1 AND c.ID = i.CUST_ID AND i.LOAN_ID = l.ID AND ((i.INVEST_MODE = '加入' AND l.NEWER_FLAG = '普通标') OR (i.INVEST_MODE = '转让')) AND i.EFFECT_DATE >= ?2 AND i.EFFECT_DATE <= ?3 AND NOT EXISTS (SELECT * FROM BAO_T_CUST_ACTIVITY_INFO ca WHERE ca.QUILT_CUST_ID=c.id AND ca.ACTIVITY_ID='13') GROUP BY c.ID HAVING sum(i.INVEST_AMOUNT) >= 100 ", nativeQuery=true)
	public List<String> findByLoanIdFor201706(String memo2, String startDate, String endDate);

	/**
	 * 过滤已到期的体验金投资记录
	 * @param type
	 * @return
	 */
	List<InvestInfoEntity> findByRedPacketTypeAndInvestStatusAndExpireDate(String type, String status, String expireDate);


}

