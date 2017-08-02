package com.slfinance.shanlincaifu.repository;
// default package

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.slfinance.shanlincaifu.entity.BankCardInfoEntity;
import com.slfinance.shanlincaifu.entity.CustInfoEntity;

/**
 * BaoTBankCardInfo Repository. @author MyEclipse Persistence Tools
 */

@RepositoryRestResource(collectionResourceRel = "banks", path = "bank")
public interface BankCardInfoRepository extends PagingAndSortingRepository<BankCardInfoEntity, String>{
	
	/**
	 * 通过客户ID和卡号查询银行卡信息
	 *
	 * @author  wangjf
	 * @date    2015年7月8日 上午11:54:08
	 * @param custId
	 * @param cardNo
	 * @return
	 */
	@Query("from BankCardInfoEntity bank where bank.custInfoEntity.id=?1 and bank.cardNo=?2 and bank.bankFlag=?3 ORDER BY bank.createDate DESC")
	public List<BankCardInfoEntity> findByCardNoOrderByCreateDateDesc(String custId, String cardNo, String bankFlag);
	
	/**
	 * 查找客户默认银行卡
	 * @param isDefault
	 * @return
	 */
	@Query("from BankCardInfoEntity bank where bank.custInfoEntity.id=? and bank.isDefault=1")
	public BankCardInfoEntity findCustDefaultBank(String custID);
	
	/**
	 * 查找客户默认银行卡
	 * 
	 * @author  zhangzs
	 * @date    2015年6月9日 上午11:35:28
	 * @param custId
	 * @return
	 */
	@Query("from BankCardInfoEntity bank where bank.custInfoEntity.id=? and bank.isDefault=1 and bank.recordStatus='有效' ORDER BY bank.createDate DESC")
	public List<BankCardInfoEntity> findCustValidBank(String custId);
	
	/**
	 * 查找客户绑定银行卡
	 * 
	 * @author  zhangzs
	 * @date    2015年9月7日 上午11:35:28
	 * @param custId
	 * @return
	 */
	@Query("SELECT new BankCardInfoEntity(bankName,cardNo)  FROM BankCardInfoEntity T WHERE recordStatus = '有效'  AND T.custInfoEntity.id = ?1 order by decode (T.bankFlag, '线上', 1, '线下', 2), T.lastUpdateDate desc ")
	public List<BankCardInfoEntity> findBingBankListByCustId(String custId);
	
	/**
	 * 查询客户提现时的默认银行卡信息
	 * 
	 * @author  zhangzs
	 * @date    2015年6月9日 上午11:35:28
	 * @param custId
	 * @param cardNo
	 * @param isDefault
	 * @param recordStatus
	 * @return
	 */
	public BankCardInfoEntity findFirstByCustInfoEntityAndCardNoAndIsDefaultAndRecordStatus(CustInfoEntity custId,String cardNo,String isDefault,String recordStatus);
	
	/**
	 * 根据客户ID查询
	 *
	 * @author  wangjf
	 * @date    2015年5月18日 上午11:35:28
	 * @param custId
	 * @return
	 */
	@Query("select A from BankCardInfoEntity A where A.custInfoEntity.id=? and A.bankFlag = '线上' ORDER BY A.createDate DESC")
	public List<BankCardInfoEntity> findByCustIdOrderByCreateDateDesc(String custId);
	
	/**
	 * 根据客户ID查询线下银行卡
	 * @author  liyy
	 * @date    2016年3月23日
	 * @param custId :String:客户ID
	 * @return List<BankCardInfoEntity>
	 */
	@Query(value=" select * from BAO_T_BANK_CARD_INFO bc where bc.CUST_ID = ? AND bc.RECORD_STATUS='有效' AND bc.BANK_FLAG = '线下' ORDER BY bc.CREATE_DATE DESC ", nativeQuery=true)
	public List<BankCardInfoEntity> findOffCardByCustIdOrderByCreateDateDesc(String custId);
	
	/**
	 * 找出状态为有效且协议号为空的数据
	 *
	 * @author  wangjf
	 * @date    2015年8月6日 上午11:35:57
	 * @param recordStatus
	 * @return
	 */
	@Query("select A from BankCardInfoEntity A where A.recordStatus = ? and A.protocolNo is null ")
	public List<BankCardInfoEntity> findByRecordStatus(String recordStatus);
	
	@Modifying
	@Query("update BankCardInfoEntity A set A.protocolNo = ?2, A.lastUpdateUser = ?3, A.lastUpdateDate = ?4, A.version = version + 1 where A.id = ?1 ")
	public int updateProtocolNoById(String id, String protocolNo, String lastUpdateUser, Date lastUpdateDate);
	
	@Modifying
	@Query("update BankCardInfoEntity A set A.bankName = ?2 where A.id = ?1 ")
	public int updateBankNameById(String id, String bankName);
	
	@Modifying
	@Query("update BankCardInfoEntity A set A.bankName = ?2, A.protocolNo = ?3 where A.id = ?1 ")
	public int updateProtocolNoAndBankNameById(String id, String bankName, String protocolNo);
	
	@Query("select A from BankCardInfoEntity A where A.cardNo = ? and A.recordStatus = '有效' ")
	public List<BankCardInfoEntity> findByCardNo(String cardNo);
	
}