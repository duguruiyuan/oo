package com.slfinance.shanlincaifu.repository.custom;

import java.math.BigDecimal;
import java.util.Map;

import com.slfinance.vo.ResultVo;

public interface RepaymentPlanInfoRepositoryCustom {

	public ResultVo findRepaymentPlanInfoList(Map<String, Object> params);
	
	public BigDecimal findAwardRateInfoForNewerFlag(String investId);
	
}
