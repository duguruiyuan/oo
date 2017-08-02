package com.slfinance.shanlincaifu.service.impl;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import com.slfinance.shanlincaifu.entity.CustInfoEntity;
import com.slfinance.shanlincaifu.repository.CustInfoRepository;
import com.slfinance.shanlincaifu.repository.custom.WdzjRepositoryCustom;
import com.slfinance.shanlincaifu.service.WdzjService;
//import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

//@Slf4j
@Service("wdzjService")
public class WdzjServiceImpl implements WdzjService {

	@Autowired
	private WdzjRepositoryCustom wdzjRepositoryCustom;
	
	@Autowired
	private CustInfoRepository custInfoRepository;
	
	@Override
	public Map<String, Object> queryWdzj(Map<String, Object> params) {
		return wdzjRepositoryCustom.queryLoaninfoAndInvestlistMap(params);
	}


}
