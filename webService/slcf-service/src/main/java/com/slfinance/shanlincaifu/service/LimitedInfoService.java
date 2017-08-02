package com.slfinance.shanlincaifu.service;

import java.util.Map;

import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;

public interface LimitedInfoService {

	@Rules(rules = { 
			@Rule(name = "rechargeType", required = true, requiredMessage = "类型不能为空") 
	})
	public ResultVo queryLimitedInfo(Map<String , Object> params);
}
