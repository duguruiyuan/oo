/*
 * Copyright 2012-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.slfinance.shanlincaifu.controller;

import com.slfinance.annotation.AutoDispatch;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.CustActivityInfoService;
import com.slfinance.shanlincaifu.service.CustActivityService;
import com.slfinance.shanlincaifu.utils.CommonUtils;
import com.slfinance.vo.ResultVo;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 *  善林财富活动服务
 * 
 * @author caoyi
 * @version $Revision:1.0.0, $Date: 2015年05月19日 下午17:32:25 $
 */
@RestController
@AutoDispatch(serviceInterface= {CustActivityInfoService.class})
	@RequestMapping("custActivityInfo")
public class CustActivityInfoController extends WelcomeController{
	
	@Autowired
	private CustActivityService custActivityService;
	
	@RequestMapping(value="/{functionName:[a-zA-Z0-9-]+}Auto", method=RequestMethod.POST)
	public @ResponseBody Object dipatch(HttpServletRequest request, HttpServletResponse response, @PathVariable("functionName") String name, @RequestBody Map<String, Object> model) throws SLException {
		return autoDispatch(request, response, name, model);
	}
	@RequestMapping(value="/{functionName:[a-zA-Z-]+}Auto", method=RequestMethod.GET)
	public @ResponseBody Object dipatch2(HttpServletRequest request, HttpServletResponse response, @PathVariable("functionName") String name, @RequestParam Map<String, Object> model) throws SLException {
		return autoDispatch(request, response, name, model);
	}
	
	/**
	 * 查询银行卡绑定送奖励的客户
	 *
	 * @author  wangjf
	 * @date    2016年12月29日 上午11:41:45
	 * @param request
	 * @param response
	 * @param params
	 * @return
	 */
	@RequestMapping(value="/queryAwardBankCardList", method=RequestMethod.POST)
	public @ResponseBody ResultVo queryAwardBankCardList(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> params) {
		return custActivityService.queryAwardBankCardList(params);
	}
	/**
	 * 查询红包奖励榜
	 *
	 * @author  fengyl
	 * @date    2017年4月27日 
	 * @return
	 */
	@RequestMapping(value="/queryAwardList", method=RequestMethod.POST)
	public @ResponseBody ResultVo queryAwardList(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> params) {
		return custActivityService.queryAwardList(params);
	}

	@RequestMapping(value = "redEnvelopeList", method = RequestMethod.POST)
	public ResultVo redEnvelopeList(@RequestBody Map<String, Object> params){
		return custActivityService.redEnvelopeList(params);
	}


	/**
	 * 之前这方法仅查询红包，现在调整成也可以查询加息券信息，方法名暂不改【2017-7-13】
	 * @param params
	 * @return
	 */
	@RequestMapping(value = "queryRedEnvelope", method = RequestMethod.POST)
	public ResultVo queryRedEnvelope(@RequestBody Map<String, Object> params){
		return new ResultVo(true, "", custActivityService.findRewardByIdAndCustId(
				CommonUtils.emptyToString(params.get("custActivityId")),
				CommonUtils.emptyToString(params.get("custId"))));
	}
//	/**
//	 * 查询实时豪礼榜
//	 *
//	 * @author fengyl
//	 * @date 2017年6月29日
//	 * @return
//	 */
//	@RequestMapping(value = "/queryActualTimeAwardList", method = RequestMethod.POST)
//	public @ResponseBody ResultVo queryActualTimeAwardList(
//			HttpServletRequest request, HttpServletResponse response,
//			@RequestBody Map<String, Object> params) {
//		return custActivityService.queryActualTimeAwardList(params);
//	}
}
