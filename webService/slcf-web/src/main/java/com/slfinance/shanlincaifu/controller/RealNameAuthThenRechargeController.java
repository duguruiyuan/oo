package com.slfinance.shanlincaifu.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.base.Strings;
import com.slfinance.annotation.AutoDispatch;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.RealNameAuthThenRechargeService;
import com.slfinance.shanlincaifu.utils.Json;
import com.slfinance.vo.ResultVo;

/**
 * 充值前调用实名认证服务控制器
 *
 * @author gaoll
 * @version $Revision:1.0.0, $Date: 2015年11月2日 下午4:11:44 $
 */
@Slf4j
@RestController
@AutoDispatch(serviceInterface = { RealNameAuthThenRechargeService.class })
@RequestMapping("realNameAuthThenRecharge")
public class RealNameAuthThenRechargeController extends WelcomeController {

	@RequestMapping("/{functionName:[a-zA-Z-]+}Auto")
	public @ResponseBody Object dipatch(HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable("functionName") String name,
			@RequestBody Map<String, Object> model) throws SLException {
		return autoDispatch(request, response, name, model);
	}

	@RequestMapping(value = "/{functionName:[a-zA-Z-]+}Auto", method = RequestMethod.GET)
	public @ResponseBody Object dipatch2(HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable("functionName") String name,
			@RequestParam Map<String, Object> model) throws SLException {
		return autoDispatch(request, response, name, model);
	}
	
	@Autowired
	RealNameAuthThenRechargeService realNameAuthThenRechargeService;
	
	/**
	 * 充值
	 * @param request
	 * @param response
	 * @return
	 * @throws SLException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/trustRecharge", method = RequestMethod.POST, produces="application/json")
	public @ResponseBody ResultVo trustRecharge(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> requestParams) throws SLException, IOException {
		log.debug("=====================开始处理充值=========================");
		/** 取出前台传过来的数据 */
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("custId", Strings.nullToEmpty((String)requestParams.get("custId")));      // 不允许为空
		params.put("bankNo", Strings.nullToEmpty((String)requestParams.get("bankCode")));    // 新卡不为空
		params.put("tradeAmount", Strings.nullToEmpty((String)requestParams.get("amount"))); // 不允许为空
		params.put("bankCardNo", Strings.nullToEmpty((String)requestParams.get("cardNo")));  // 不允许为空
		params.put("agreeNo", Strings.nullToEmpty((String)requestParams.get("agreeNo")));    // 银行卡存在时不能为空
		params.put("payType", Strings.nullToEmpty((String)requestParams.get("payType")));    // 支付方式 D:AUTH_PAY
		params.put("ipAddress", Strings.nullToEmpty((String)requestParams.get("ipAddress"))); // 取客户端IP地址
		params.put("appSource", Strings.nullToEmpty((String)requestParams.get("appSource"))); // 取来源
		params.put("wapSource", Strings.nullToEmpty((String)requestParams.get("wapSource"))); // 取WAP来源
		params.put("channelNo", Strings.nullToEmpty((String)requestParams.get("channelNo"))); // 渠道来源
		params.put("utid", Strings.nullToEmpty((String)requestParams.get("utid"))); // 校花ID
		params.put("meId", Strings.nullToEmpty((String)requestParams.get("meId"))); // 校花ID
		params.put("meVersion", Strings.nullToEmpty((String)requestParams.get("meVersion"))); // 校花ID
		params.put("custName", Strings.nullToEmpty((String)requestParams.get("custName"))); // 客户姓名
		params.put("credentialsCode", Strings.nullToEmpty((String)requestParams.get("credentialsCode"))); // 身份证号码
		params.put("appVersion", Strings.nullToEmpty((String)requestParams.get("appVersion"))); // app版本号
		
		/** 新增过程流水表 */
		ResultVo resultVo = realNameAuthThenRechargeService.AuthenticationThenRecharge(params);
		if(!ResultVo.isSuccess(resultVo)) {
			log.warn("提交充值申请失败：" + resultVo.toString());
			return resultVo;
		}
		Map<String, Object> thirdResultMap = (Map<String, Object>)resultVo.getValue("data");
		Map<String, Object> resultMap = Json.ObjectMapper.readValue((String)thirdResultMap.get("result"), Map.class);
		if(!"android".equals((String)params.get("appSource")) 
				&& !"ios".equals((String)params.get("appSource")))
			resultMap.put("req_url", (String)thirdResultMap.get("url"));

		log.debug("=====================完成处理充值=========================");
		
		return new ResultVo(true, "充值成功", resultMap);
	}

}
