/** 
 * @(#)UpdateAuditAndTradeFlowInfoService.java 1.0.0 2015年4月30日 上午11:14:18  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service;

import com.slfinance.exception.SLException;
import com.slfinance.vo.ResultVo;

/**   
 * 提现业务审核相关Service接口实现
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年4月30日 上午11:14:18 $ 
 */
public interface WithDrawCheckService {

	/**
	 * 更新提现审核信息和交易流水信息
	 * @param auditId
	 * @param flowId
	 * @param custId
	 * @return
	 * @throws SLException
	 */
	public ResultVo updateWithdrawAndFlowProcess(String auditId,String flowId,String custId) throws SLException;
	
}
