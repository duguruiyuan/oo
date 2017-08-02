package com.slfinance.shanlincaifu.repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.CustInfoEntity;
import com.slfinance.shanlincaifu.repository.custom.CustInfoRepositoryCustom;

@RepositoryRestResource(collectionResourceRel = "customers", path = "cust")
public interface CustInfoRepository extends PagingAndSortingRepository<CustInfoEntity, String>, JpaSpecificationExecutor<CustInfoEntity>,CustInfoRepositoryCustom{

	@Modifying
	@Query("update CustInfoEntity c set c.mobile=? where c.id=?")
	public int updateByIdToMobile(String mobile,String id);
	
	public List<CustInfoEntity> findByIdNotIn(List<String> idList);  
	
	public CustInfoEntity findByLoginNameAndCredentialsCode(@Param("loginName") String loginName, @Param("credentialsCode") String credentialsCode);
	
	public CustInfoEntity findByLoginName(@Param("loginName") String loginName);

	/** 根据手机号查询记录 */
	public CustInfoEntity findByMobile(@Param("mobile") String mobile);

	/** 根据手机号,邮箱，登录名 查询记录 */
	public CustInfoEntity findByLoginNameAndCustCode(@Param("loginName") String loginName, @Param("custCode") String custCode);
	
	public Page<CustInfoEntity> findAll(@Param("page") Pageable page);
	
	/**根据手机号码和登陆密码查询数据*/
	public CustInfoEntity findByMobileAndLoginPassword(@Param("mobile") String mobile,@Param("loginPassword") String loginPassword);
	
	/**根据用户名和登陆密码查询数据*/
	public CustInfoEntity findByLoginNameAndLoginPassword(@Param("loginName") String mobile,@Param("loginPassword") String loginPassword);
	
	/**根据email查询改邮件的数量**/
	public Long countByEmail(@Param("email") String email);
	
	public List<CustInfoEntity> findByInviteOriginId(String inviteOriginId); 
	
	/**
	 * 根据验证码查询信息
	 * @param inviteCode
	 * @return
	 */
	public CustInfoEntity findByInviteCode(@Param("inviteCode") String inviteCode);

	/** 根据手机号和邀请码查询数据 */
	public CustInfoEntity findByMobileAndInviteCode(@Param("mobile") String mobile,@Param("inviteCode") String inviteCode);

	public int countByCustNameAndCredentialsCode(String custName,String credentialsCode);
	
	public int countByCredentialsCode(String credentialsCode);
	
	/**
	 * 根据用户id查询用户推荐总人数
	 * 
	 * zhangzs
	 * 2015年8月24日
	 * @param recCustId
	 * @param custId
	 * @param custRecId
	 * @return
	 */
	@Query(value="select count(t.id) from bao_t_cust_info t,(select query_permission,spread_level from bao_t_cust_info where id = ?) t1 where t.query_permission like  t1.query_permission || '%'  and t.spread_level = t1.spread_level + 1 and id != ? ",nativeQuery=true)
	public int getRecCountByCustId(String recCustId,String custId);
	
	/**
	 * 根据客户名称和客户种类查询
	 * zhangt
	 * @param loginName
	 * @param custKind
	 * @return
	 */
	public CustInfoEntity findByLoginNameAndCustKind(String loginName, String custKind);
	
	/** 根据联系人查询记录 */
	public CustInfoEntity findByTel(String tel);

	/**
	 * 附属银行卡作废
	 * @author liyy
	 * @date    2016年2月26日 
	 * @param custId :String : 客户ID
	 * @param bankCardNo :String : 银行卡号
	 * @param userId :String : 操作人
	 * @return int 
	 */
	@Modifying
	@Query(value=" update BAO_T_BANK_CARD_INFO SET RECORD_STATUS = '无效', LAST_UPDATE_USER = ?3, LAST_UPDATE_DATE = sysdate, VERSION=VERSION+1 where BANK_FLAG = '线下' and RECORD_STATUS = '有效' and CUST_ID = ?1 and CARD_NO = ?2 ", nativeQuery=true)
	public int abandonAtCustBank(String custId, String bankCardNo, String userId);

	/**
	 * 获取客户信息
	 * @author liyy
	 * @date    2016年2月26日 
	 * @param custName :String : 客户名称
	 * @param mobile   :String : 客户电话
	 * @param credentialsCode  :String : 证件号码
	 * @return CustInfoEntity 
	 */
	@Query(value=" SELECT * FROM BAO_T_CUST_INFO WHERE IS_EMPLOYEE='是' AND CUST_NAME = ?1 AND MOBILE = ?2 AND CREDENTIALS_CODE = ?3 ", nativeQuery=true)
	public CustInfoEntity findOneByNameAndMobileAndCode(
			String custName, String mobile,
			String credentialsCode);
	
	/**
	 * 根据custId和custName查个数
	 * liyy
	 * 2016年5月31日
	 * @param custId String
	 * @param custName String
	 * @return int
	 */
	@Query(value=" SELECT count(1) FROM BAO_T_CUST_INFO WHERE ID = ?1 AND CUST_NAME = ?2 ",nativeQuery=true)
	public int findCountBycustIdAndCustName(String custId, String custName);
	

	/**
	 * 查询某个时间之后推荐的客户
	 * 
	 * @param inviteOriginId
	 * @param createDate
	 * @return
	 */
	@Query(value=" SELECT A FROM CustInfoEntity A where A.inviteOriginId = :inviteOriginId and A.createDate >= :createDate ")
	public List<CustInfoEntity> findByInviteOriginIdAndCreateDate(@Param("inviteOriginId")String inviteOriginId, @Param("createDate")Date createDate); 
	
	/**
	 * 根据custId和credentialsCode查个数
	 * liyy
	 * 2016年5月31日
	 * @param custId String
	 * @param credentialsCode String
	 * @return int
	 */
	@Query(value=" SELECT count(1) FROM BAO_T_CUST_INFO WHERE ID = ?1 AND CREDENTIALS_CODE = ?2 ",nativeQuery=true)
	public int findCountBycustIdAndCredentialsCode(String custId, String credentialsCode);

	
	/**
	 * 查询业务员信息
	 * @author  liyy
	 * @date    2016年5月31日 
	 * @param params Map <br>
     *      <tt>custId                  :String:客户ID</tt><br>
	 * @return ResultVo
     *      	<tt>map
     *      		<tt>custId         :String:业务员ID</tt><br>
     *      		<tt>custName       :String:业务员用户名</tt><br>
     *      		<tt>credentialsCode:String:业务员证件号码</tt><br>
     *      		<tt>mobile         :String:业务员手机号</tt><br>
     *      		<tt>registerDate   :String:业务员注册时间</tt><br>
     *      	</tt><br>
     *  @throws SLException
	 */
	@Query(value=" SELECT c.* FROM BAO_T_CUST_INFO c, BAO_T_CUST_RECOMMEND_INFO cr WHERE cr.CUST_ID=c.ID AND cr.RECORD_STATUS='有效' AND cr.QUILT_CUST_ID = ? ",nativeQuery=true)
	public CustInfoEntity queryCustManagerByCustId(String quiltCustId);
	
	//根据身份证查询客户经理
	public CustInfoEntity findByCredentialsCodeAndIsEmployee(String credentialsCode, String isEmployee);

	@Query(value=" SELECT * FROM BAO_T_CUST_INFO WHERE CREDENTIALS_CODE = ? AND IS_RECOMMEND = '是' AND IS_EMPLOYEE = '是' ", nativeQuery=true)
	public CustInfoEntity findCustInfoByCardIdAndIsEmployee(String credentialsCode);
	
	/**
	 * 通过身份证号和客户姓名查询客户信息
	 *
	 * @author  wangjf
	 * @date    2016年11月30日 下午3:31:28
	 * @param credentialsCode
	 * @param custName
	 * @return
	 */
	public CustInfoEntity findByCredentialsCodeAndCustName(String credentialsCode, String custName);
	
	/**
	 * 通过身份证号查询客户信息
	 * @author  liyy
	 * @date    2016年11月30日 下午3:31:28
	 * @param credentialsCode
	 * @param custName
	 * @return
	 */
	public List<CustInfoEntity> findByCredentialsCode(String credentialsCode);

	/**
	 * 根据证件号码和客户类型查询
	 * lyy
	 * */
	public CustInfoEntity findByCredentialsCodeAndCustType(String credentialsCode,
			String custTypeLoaner);
	
	/**
	 * 查询债权所有用户
	 *
	 * @author  wangjf
	 * @date    2016年12月2日 下午4:04:03
	 * @param loanId
	 * @return
	 */
	@Query("select A from CustInfoEntity A where A.id in (select B.custId from WealthHoldInfoEntity B where loanId = :loanId and holdStatus = :holdStatus)")
	public List<CustInfoEntity> findAllCustByLoanIdAndHoldStatus(@Param("loanId")String loanId, @Param("holdStatus")String holdStatus);
	
	/**
	 * 查询债权所有用户
	 * @author  liyy
	 * @date    2017年5月24日 
	 * @param loanId
	 * @return
	 */
	@Query("select A from CustInfoEntity A where A.id in (select B.custId from WealthHoldInfoEntity B where loanId = :loanId) ")
	public List<CustInfoEntity> findAllCustByLoanId(@Param("loanId")String loanId);

	/**
	 * 商户名称校验
	 */
	@Query(value=" SELECT count(1) count FROM bao_t_cust_info WHERE CUST_TYPE=?1 AND CUST_NAME = ?2 ", nativeQuery=true)
	public int findByCustTypeAndCustName(String custType, String custName);

	/**
	 * 根据客户id查询cust_recommend_info表的cust_id对应的客户信息
	 *
	 * @author fengyl
	 * @date 2017年5月24日
	 * @param custId
	 * @return
	 */
	@Query(value = " select t.* from  bao_t_cust_info t,bao_t_cust_info a,bao_t_cust_recommend_info b where t.id =b.cust_id and  a.id=b.quilt_cust_id and b.record_status='有效' and b.quilt_cust_id=? ", nativeQuery = true)
	public CustInfoEntity findByCustId(String custId);
	/**
	 * 根据客户id查询cust_info表的invietId对应的客户信息
	 *
	 * @author fengyl
	 * @date 2017年5月24日
	 * @param custId
	 * @return
	 */
	@Query(value = " select a.* from  bao_t_cust_info a,bao_t_cust_info b where a.id=b.invite_origin_id and  b.id=? ", nativeQuery = true)
	public CustInfoEntity getInviteInfoByCustId(String custId);

    /**
     * 根据客户id查询当前持有的总存续金额
     *
     * @author mali
     * @date 2017-6-8 19:40:31
     *
     * @param custId 用户id
     * @return
     */
    @Query(value = "SELECT A.REPAYMENT_PRINCIPAL + B.REPAYMENT_PRINCIPAL FROM " +
            "(SELECT NVL(SUM(H.HOLD_SCALE * RP.REPAYMENT_PRINCIPAL), 0) REPAYMENT_PRINCIPAL " +
            "         FROM BAO_T_WEALTH_HOLD_INFO H " +
            "        INNER JOIN BAO_T_CUST_RECOMMEND_INFO C ON H.CUST_ID = C.QUILT_CUST_ID " +
            "        INNER JOIN BAO_T_LOAN_INFO L ON L.ID = H.LOAN_ID " +
            "        INNER JOIN (SELECT SUM(R.REPAYMENT_PRINCIPAL) REPAYMENT_PRINCIPAL, R.LOAN_ID " +
            "                      FROM BAO_T_REPAYMENT_PLAN_INFO R " +
            "                     WHERE R.EXPECT_REPAYMENT_DATE > TO_CHAR(SYSDATE,'YYYYMMDD') " +
            "                     GROUP BY R.LOAN_ID) RP ON RP.LOAN_ID = L.ID " +
            "        WHERE H.HOLD_STATUS = '持有中' AND C.CUST_ID = :custId) A, " +
            "(SELECT NVL(SUM(H.HOLD_SCALE * RP.REPAYMENT_PRINCIPAL), 0) REPAYMENT_PRINCIPAL " +
            "         FROM BAO_T_WEALTH_HOLD_INFO H " +
            "        INNER JOIN BAO_T_LOAN_INFO L ON L.ID = H.LOAN_ID " +
            "        INNER JOIN (SELECT SUM(R.REPAYMENT_PRINCIPAL) REPAYMENT_PRINCIPAL, R.LOAN_ID " +
            "                      FROM BAO_T_REPAYMENT_PLAN_INFO R " +
            "                     WHERE R.EXPECT_REPAYMENT_DATE > TO_CHAR(SYSDATE,'YYYYMMDD') " +
            "                     GROUP BY R.LOAN_ID) RP ON RP.LOAN_ID = L.ID " +
            "        WHERE H.HOLD_STATUS = '持有中' AND H.CUST_ID = :custId) B "
            , nativeQuery = true)
    BigDecimal getHoldAmount(@Param("custId") String custId);

    /**
     * 查询基础数据中的最大级别
     *
     * @author mali
     * @date 2017-6-8 19:40:31
     * @param custId 用户id
     *
     * @return
     */
    @Query(value = "SELECT MAX(RANKING) FROM BAO_T_DAILY_REMAINDER_INFO T WHERE T.CUST_ID = ?1", nativeQuery = true)
    Object findMaxRanking(String custId);

    //@Query(value = "select t from BAO_T_CUST_INFO t where id = ?1", nativeQuery = true)
	CustInfoEntity findById(String id);
}