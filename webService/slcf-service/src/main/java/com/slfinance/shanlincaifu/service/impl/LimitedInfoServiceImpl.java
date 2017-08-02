package com.slfinance.shanlincaifu.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.slfinance.shanlincaifu.repository.custom.LimitedInfoCustom;
import com.slfinance.shanlincaifu.service.LimitedInfoService;
import com.slfinance.vo.ResultVo;

@Service("limitedInfoService")
public class LimitedInfoServiceImpl implements LimitedInfoService {

	@Autowired
	LimitedInfoCustom limitedInfoCustom;
	
	public ResultVo queryLimitedInfo(Map<String , Object> params){
		List<Map<String, Object>> list = limitedInfoCustom.queryLimitedInfo(params);
		
		Map<String, Object> data = Maps.newHashMap();
		data.put("data", list);
		return new ResultVo(true, "查询成功", data);
	}
	
}
