package com.slfinance.shanlincaifu.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.WealthInvestInfoEntity;
import com.slfinance.shanlincaifu.repository.WealthInvestInfoRepository;
import com.slfinance.shanlincaifu.repository.custom.WealthInvestIntoRepositoryCustom;
import com.slfinance.shanlincaifu.service.WealthInvestIntoService;

@Service("wealthInvestIntoService")
public class WealthInvestIntoServiceImpl implements WealthInvestIntoService {
	
	@Autowired
	WealthInvestIntoRepositoryCustom wealthInvestIntoRepositoryCustom;
	
	@Autowired
	WealthInvestInfoRepository wealthInvestInfoRepository;

	/** 同步理财投资信息表BAO_T_WEALTH_INVEST_INFO */
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public boolean synchronizeInvestInfoFromWealth() {
		wealthInvestIntoRepositoryCustom.synchronizeInvestInfoFromWealth();
		return true;
	}

	@Override
	public List<WealthInvestInfoEntity> findInvestInfoByCardId(
			String credentialsCode) {
		return wealthInvestInfoRepository.findInvestInfoByCardId(credentialsCode);
	}

}
