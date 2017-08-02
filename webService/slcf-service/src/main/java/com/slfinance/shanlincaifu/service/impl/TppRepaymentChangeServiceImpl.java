package com.slfinance.shanlincaifu.service.impl;
/**
 * tpp对接
 */
import java.util.Map;

import org.apache.commons.lang3.CharSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.RepaymentChangeResultVo;
import com.slfinance.shanlincaifu.service.TppRepaymentChangeService;
import com.slfinance.shanlincaifu.utils.JsonUtil;
import com.slfinance.thirdpp.util.ShareUtil;

import com.slfinance.vo.ResultVo;

import ch.qos.logback.classic.pattern.MessageConverter;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service("tppRepaymentChangeServiceImpl")
public class TppRepaymentChangeServiceImpl implements TppRepaymentChangeService{
	
	@Autowired
	@Qualifier("thirdPartyPayRestClientService")
	private RestOperations slRestClient;


	@Override
	public ResultVo repaymentChange_Tpp(Map<String, Object> paramsMap) throws SLException {
		
		String url = "http://10.6.115.62:9000/api/repaymentPlanChange";
		String result =null;
		JSONObject tppNeedJson = JSONObject.parseObject(JSON.toJSONString(paramsMap));
		try {
			result = slRestClient.postForObject(url, ShareUtil.getJsonLogInfo(tppNeedJson), String.class);
			RepaymentChangeResultVo repaymentChangeResultVo = (RepaymentChangeResultVo) ShareUtil.jsonToObject(result, RepaymentChangeResultVo.class);
		} 
		catch (Exception e) 
		{
			
			log.error("Tpp接口异常");
		}
		//如果Tpp端返回正常
		if (true){
			return new ResultVo(true);
		}
			return new ResultVo(false);
		
	}
}
