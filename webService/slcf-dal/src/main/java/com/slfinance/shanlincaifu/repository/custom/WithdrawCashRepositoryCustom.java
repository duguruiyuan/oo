/** 
 * @(#)WithdrawCashRepository.java 1.0.0 2015年4月27日 上午11:47:08  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository.custom;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AuditInfoEntity;
import com.slfinance.shanlincaifu.vo.WithDrawCashVO;
import com.slfinance.shanlincaifu.vo.WithDrawCashStatisticsVO;

/**   
 * 提现业务Repository
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年4月27日 上午11:47:08 $ 
 */
public interface WithdrawCashRepositoryCustom {
	
	public Page<WithDrawCashVO> findAllWithdrawCashList(Map<String, Object> paramsMap) throws SLException;
	
	public WithDrawCashStatisticsVO findAllWithdrawCashSum(Map<String, Object> paramsMap) throws SLException;
	
	public WithDrawCashVO findWithdrawalCashDetailInfo(Map<String, Object> paramsMap) throws SLException;
	
	public List<AuditInfoEntity> findByCustIdOrId(Map<String,Object> paramsMap) throws SLException;
	
	public Page<AuditInfoEntity> findPageByCustIdOrId(Map<String,Object> paramsMap) throws SLException;
	
	public int updateWithDrawCashRecord(Map<String,Object> paramMap);
	
	public Page<Map<String, Object>> findAllWithdrawAuditList(Map<String, Object> params);
}
