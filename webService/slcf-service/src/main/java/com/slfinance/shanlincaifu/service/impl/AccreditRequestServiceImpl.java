/** 
 * @(#)AccessServiceImpl.java 1.0.0 2015年11月3日 上午11:25:47  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AccreditRequestEntity;
import com.slfinance.shanlincaifu.entity.AppAccessInfoEntity;
import com.slfinance.shanlincaifu.repository.AccreditRequestRepository;
import com.slfinance.shanlincaifu.repository.AppAccessInfoRepository;
import com.slfinance.shanlincaifu.service.AccessService;
import com.slfinance.shanlincaifu.service.AccreditRequestService;
import com.slfinance.shanlincaifu.service.ParamService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.SharedUtil;
import com.slfinance.vo.ResultVo;

/**   
 * 授权申请
 *  
 * @author  廖兵兵
 * @version $Revision:1.0.0
 */
@Service("accreditRequestService")
public class AccreditRequestServiceImpl implements AccreditRequestService {

	@Autowired
	private AccreditRequestRepository accreditRequestRepository;
	
	
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public AccreditRequestEntity save(AccreditRequestEntity entity)
			throws SLException {
		
		return accreditRequestRepository.save(entity);
	}


	@Override
	public List<AccreditRequestEntity> findByStatus(String status)
			throws SLException {
		
		return accreditRequestRepository.findByStatus(status);
	}
	
	

}
