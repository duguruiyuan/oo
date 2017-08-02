/** 
 * @(#)SmsInfoRepository.java 1.0.0 2015年4月23日 下午2:25:28  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.SmsInfoEntity;
import com.slfinance.shanlincaifu.repository.custom.SmsInfoRepositoryCustom;

/**   
 * 
 *  
 * @author  HuYaHui
 * @version $Revision:1.0.0, $Date: 2015年4月23日 下午2:25:28 $ 
 */
public interface SmsInfoRepository extends PagingAndSortingRepository<SmsInfoEntity, String>,SmsInfoRepositoryCustom,JpaSpecificationExecutor{
	
	@Modifying
	@Query("update SmsInfoEntity s set s.messageStatus='无效',lastValidTime=?"
			+ " where s.targetAddress=? and s.verityCode=? and s.messageType=? and s.messageStatus='有效'")
	public int updateByTargetAddressAndVerityCodeAndMessageType(Date dt,String targetAddress,String verityCode,String messageType);
	
	public SmsInfoEntity 
		findByTargetAddressAndVerityCodeAndMessageType(String targetAddress,String verityCode,String messageType);

	@Query("select s from SmsInfoEntity s "
			+ " where targetAddress=? and messageType=? and messageStatus='有效'"
			+ " order by sendDate desc")
	public List<SmsInfoEntity> 
		findByTargetAddressAndTargetTypeOrderBySendDateDesc(String mobile,String messageType);
	
	/**
	 * 根据邮箱地址和验证码和消息类型，查询最新有效的消息信息
	 * @author zhangzs
	 * @param targetAddress
	 * @param verityCode
	 * @param messageType
	 * @return
	 */
	@Query(value="SELECT T.* FROM ( SELECT S.* FROM BAO_T_SMS_INFO S "
			+ " WHERE TARGET_ADDRESS=?1 AND VERITY_CODE=?2 AND MESSAGE_TYPE=?3 AND LAST_VALID_TIME >?4 AND MESSAGE_STATUS='有效'"
			+ " ORDER BY SEND_DATE DESC ) T WHERE ROWNUM = 1",nativeQuery = true)
	public SmsInfoEntity checkVerityCodeAndTargetAddress(String targetAddress,String verityCode,String messageType,Date date);
	
	/**
	 * 根据邮箱地址和验证码和消息类型，查询最新消息信息 是为了统计有哪些验证码没有使用
	 * @author zhangzs
	 * @param targetAddress
	 * @param verityCode
	 * @param messageType
	 * @return
	 */
	@Query(value="SELECT T.* FROM ( SELECT S.* FROM BAO_T_SMS_INFO S "
			+ " WHERE TARGET_ADDRESS=?1 AND MESSAGE_TYPE=?2 AND LAST_VALID_TIME >?3 "
			+ " ORDER BY SEND_DATE DESC ) T WHERE ROWNUM = 1",nativeQuery = true)
	public SmsInfoEntity checkTargetAddressAndVerityCode(String targetAddress,String messageType,Date date);
	
	/**
	 * 根据消息类型和邮件地址更新消息状态
	 * 
	 * @author zhangzs
	 * @param messageStatus
	 * @param messageType
	 * @param targetAddress
	 * @param verityCode
	 * @return
	 */
	@Modifying
	@Query("update SmsInfoEntity u set u.messageStatus = ?1 where u.messageType = ?2 and u.targetAddress = ?3 and u.verityCode =?4 ")
	public int updMessageStatusByTypeAndEmail(String messageStatus,String messageType, String targetAddress,String verityCode );
	
}
