package com.slfinance.shanlincaifu.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.RecommendedAwardsService;
import com.slfinance.vo.ResultVo;

/**
 * 
 * @author zhumin
 *
 */
@Controller
@RequestMapping(value = "/recommendedAwards", produces = "application/json;charset=UTF-8")
public class RecommendedAwardsController {

	@Autowired
	private RecommendedAwardsService recommendedAwardsService;

	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> list(
			@RequestBody Map<String, Object> paramsMap) throws SLException {
		return recommendedAwardsService.list(paramsMap);

	}

	@RequestMapping(value = "/grentRecommendedAwardsData", method = RequestMethod.POST)
	public @ResponseBody ResultVo grentRecommendedAwardsData(@RequestBody Map<String, Object> paramsMap) {
		return recommendedAwardsService.grentRecommendedAwardsData(paramsMap);
	}

	@RequestMapping(value = "/findCustActivityDetailEntityById", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> findCustActivityDetailEntityById(
			@RequestBody Map<String, Object> paramsMap) {
		return recommendedAwardsService
				.findCustActivityDetailEntityById(paramsMap);
	}
}
