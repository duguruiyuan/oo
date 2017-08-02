package com.slfinance.shanlincaifu.service;

import java.util.Map;

import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;


public interface WdzjService {
	@Rules(rules = {
			@Rule(name = "page", required = true, requiredMessage = "当前页不能为空", digist = true, digistMessage = "当前页必须为整数"), 
			@Rule(name = "pageSize", required = true, requiredMessage = "每页的借款标数不能为空", digist = true, digistMessage = "每页的借款标数必须为整数"),
			@Rule(name = "date", required = true, requiredMessage = "满标时间不能为空")
			
	})
	public Map<String, Object> queryWdzj(Map<String, Object> params);
}
