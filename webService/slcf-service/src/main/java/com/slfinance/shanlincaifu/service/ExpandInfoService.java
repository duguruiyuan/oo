package com.slfinance.shanlincaifu.service;

import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;

public interface ExpandInfoService {

	/**
	 * 保存扩展信息
	 * @param param
	 * @return
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "relateType", required = true, requiredMessage = "关联类型不能为空"),
			@Rule(name = "relatePrimary", required = true, requiredMessage = "关联主键不能为空!"),
			@Rule(name = "userId", required = true, requiredMessage = "用户Id不能为空!")
	})
	public ResultVo saveExpandInfo(Map<String, Object> param) throws SLException;
}
