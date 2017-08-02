package com.slfinance.shanlincaifu.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.WealthCustIntoEntity;
import com.slfinance.shanlincaifu.repository.WealthCustIntoRepository;
import com.slfinance.shanlincaifu.repository.custom.WealthCustIntoRepositoryCustom;
import com.slfinance.shanlincaifu.service.WealthCustIntoService;

@Service("wealthCustIntoService")
public class WealthCustIntoServiceImpl implements WealthCustIntoService {
	
	@Autowired
	WealthCustIntoRepositoryCustom wealthCustIntoRepositoryCustom;
	
	@Autowired
	WealthCustIntoRepository wealthCustIntoRepository;
	
	/** 同步理财客户信息表BAO_T_WEALTH_CUST_INTO */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public boolean synchronizeCustInfoFromWealth() {
		
		wealthCustIntoRepositoryCustom.synchronizeCustInfoFromWealth();
		
		return true;
	}

	@Override
	public WealthCustIntoEntity findCustInfoByCardId(String credentialsCode) {
	
		return wealthCustIntoRepository.findCustInfoByCardId(credentialsCode);
	}

}
