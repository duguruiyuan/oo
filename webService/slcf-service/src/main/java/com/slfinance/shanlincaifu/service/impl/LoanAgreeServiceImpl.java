/** 
 * @(#)AccessServiceImpl.java 1.0.0 2015年11月3日 上午11:25:47  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.impl;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AccreditRequestEntity;
import com.slfinance.shanlincaifu.entity.AppAccessInfoEntity;
import com.slfinance.shanlincaifu.entity.LoanAgreeEntity;
import com.slfinance.shanlincaifu.repository.AccreditRequestRepository;
import com.slfinance.shanlincaifu.repository.AppAccessInfoRepository;
import com.slfinance.shanlincaifu.repository.LoanAgreeRepository;
import com.slfinance.shanlincaifu.service.AccessService;
import com.slfinance.shanlincaifu.service.AccreditRequestService;
import com.slfinance.shanlincaifu.service.LoanAgreeService;
import com.slfinance.shanlincaifu.service.ParamService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.SharedUtil;
import com.slfinance.vo.ResultVo;

/**   
 * 借款协议
 *  
 * @author  廖兵兵
 * @version $Revision:1.0.0
 */
@Service("loanAgreeService")
public class LoanAgreeServiceImpl implements LoanAgreeService {

	@Autowired
	private LoanAgreeRepository loanAgreeRepository;
	
	
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public LoanAgreeEntity save(LoanAgreeEntity entity)
			throws SLException {
		
		return loanAgreeRepository.save(entity);
	}

	/**
	 * 根据custId查询对象
	 */
	@Override
	public LoanAgreeEntity findByCustId(String custId) throws SLException {
		
		return loanAgreeRepository.findByCustId(custId);
	}
	
	

}
