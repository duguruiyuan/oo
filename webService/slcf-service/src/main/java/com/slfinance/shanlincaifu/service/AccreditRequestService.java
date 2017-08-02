/** 
 * @(#)AccessService.java 1.0.0 2015年11月3日 上午10:09:01  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service;

import java.util.List;
import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AccreditRequestEntity;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;

/**   
 * 授权申请
 *  
 * 
 * @author  廖兵兵
 * @version $Revision:1.0.0
 */
public interface AccreditRequestService {

	
	public AccreditRequestEntity save(AccreditRequestEntity entity) throws SLException;

	public List<AccreditRequestEntity> findByStatus(String status)throws SLException;
}
