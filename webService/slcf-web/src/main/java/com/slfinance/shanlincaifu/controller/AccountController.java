/** 
 * @(#)AccountController.java 1.0.0 2015年4月22日 上午11:16:56  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.google.common.base.Strings;
import com.slfinance.annotation.AutoDispatch;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.AccountFlowService;
import com.slfinance.shanlincaifu.service.AccountService;
import com.slfinance.shanlincaifu.service.SMSService;
import com.slfinance.shanlincaifu.service.ThirdPartyPayRequestService;
import com.slfinance.shanlincaifu.service.ThirdPartyPayService;
import com.slfinance.shanlincaifu.utils.Json;
import com.slfinance.shanlincaifu.utils.SharedUtil;
import com.slfinance.thirdpp.util.ShareConstant;
import com.slfinance.thirdpp.util.ShareUtil;
import com.slfinance.thirdpp.vo.TradeResponseVo;
import com.slfinance.thirdpp.vo.TradeResultVo;
import com.slfinance.vo.ResultVo;

/**   
 * 
 *  
 * @author  HuangXiaodong
 * @version $Revision:1.0.0, $Date: 2015年4月22日 上午11:16:56 $ 
 */
@Slf4j
@RestController
@AutoDispatch(serviceInterface= {AccountService.class})
@RequestMapping("account")
public class AccountController extends WelcomeController {

	@Autowired
	ThirdPartyPayService thirdPartyPayService;
	
	@Autowired
	AccountFlowService accountFlowService;
	
	@Autowired
	ThirdPartyPayRequestService thirdPartyPayRequestService;
	
	@Autowired
	AccountService accountService;
	
	@Autowired
	SMSService smsService;
	
	@Value("${web.recharge.web.call.back.url}")
	private String rechargeReturnCallBackUrl;
	
	@RequestMapping("/{functionName:[a-zA-Z-]+}Auto")
	public @ResponseBody Object dipatch(HttpServletRequest request, HttpServletResponse response, @PathVariable("functionName") String name, @RequestBody Map<String, Object> model) throws SLException{
		return autoDispatch(request, response, name, model);
	}
	@RequestMapping(value="/{functionName:[a-zA-Z-]+}Auto", method=RequestMethod.GET)
	public @ResponseBody Object dipatch2(HttpServletRequest request, HttpServletResponse response, @PathVariable("functionName") String name, @RequestParam Map<String, Object> model) throws SLException{
		return autoDispatch(request, response, name, model);
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/withdrawalCashAuditAuto", method = RequestMethod.POST, produces="application/json")
	public @ResponseBody ResultVo withdrawal(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> requestParams) throws SLException, IOException {
		ResultVo resultVo = accountService.withdrawalCashAudit(requestParams);
		if(ResultVo.isSuccess(resultVo)) {
			smsService.asnySendSMS((Map<String, Object>)resultVo.getValue("data"));
		}
		return resultVo;
	}
	
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
		params.put("custId", Strings.nullToEmpty((String)requestParams.get("custId")));      //不允许为空
		params.put("bankNo", Strings.nullToEmpty((String)requestParams.get("bankCode")));    //新卡不为空
		params.put("tradeAmount", Strings.nullToEmpty((String)requestParams.get("amount"))); //不允许为空
		params.put("bankCardNo", Strings.nullToEmpty((String)requestParams.get("cardNo")));  //不允许为空
		params.put("agreeNo", Strings.nullToEmpty((String)requestParams.get("agreeNo")));    //银行卡存在时不能为空
		params.put("payType", Strings.nullToEmpty((String)requestParams.get("payType")));    //支付方式 D:AUTH_PAY
		params.put("ipAddress", Strings.nullToEmpty((String)requestParams.get("ipAddress"))); // 取客户端IP地址
		params.put("appSource", Strings.nullToEmpty((String)requestParams.get("appSource"))); // 取来源
		params.put("wapSource", Strings.nullToEmpty((String)requestParams.get("wapSource"))); // 取WAP来源
		params.put("channelNo", Strings.nullToEmpty((String)requestParams.get("channelNo"))); // 渠道来源
		params.put("utid", Strings.nullToEmpty((String)requestParams.get("utid"))); // 校花ID
		params.put("meId", Strings.nullToEmpty((String)requestParams.get("meId"))); // 校花ID
		params.put("meVersion", Strings.nullToEmpty((String)requestParams.get("meVersion"))); // 校花ID
		params.put("appVersion", Strings.nullToEmpty((String)requestParams.get("appVersion"))); // app版本号
		params.put("backUrl", Strings.nullToEmpty((String)requestParams.get("backUrl"))); // 返回修改地址
		params.put("returnUrl", Strings.nullToEmpty((String)requestParams.get("returnUrl"))); // 成功返回地址
		
		/** 新增过程流水表 */
		ResultVo resultVo = accountService.rechargeApply(params);
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
	
	/**
	 * tpp充值同步回调
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws SLException
	 */
	@RequestMapping(value="/returnUrl", method=RequestMethod.GET)
	public ModelAndView returnUrl (HttpServletRequest request, HttpServletResponse response) throws SLException {
		log.info("=====================开始处理充值同步回调=========================");
		log.info(String.format("=====================同步回调地址：%s=========================", rechargeReturnCallBackUrl));
		RedirectView view=new RedirectView(rechargeReturnCallBackUrl);
		return new ModelAndView(view, null);
	}
	
	/**
	 * tpp充值异步回调方法
	 * 
	 * @param map
	 * @return
	 * @throws SLException
	 */
	@ResponseBody
	@RequestMapping(value = "/recharge",  method = RequestMethod.POST)
	public String recharge(HttpServletRequest request, HttpServletResponse response) throws SLException {
		try {
			log.info("=====================开始处理tpp充值回调=========================");
			String xml = SharedUtil.receiveInputStream(request.getInputStream());
			TradeResultVo tradeResultVo = ShareUtil.xmlToObject(xml, TradeResultVo.class);
			TradeResponseVo tradeResponseVo = tradeResultVo.getItemVos().get(0);
			Map<String, Object> flowMap = new HashMap<String, Object>();

			String responseCode = tradeResponseVo.getResponseCode();
			flowMap.put("tradeCode", tradeResponseVo.getTradeCode());
			flowMap.put("responseCode", tradeResponseVo.getResponseCode());
			flowMap.put("noAgree", tradeResponseVo.getOtherPropertyMap().get("no_agree"));
			
			/**
			 * 处理成功
			 */
			if (responseCode.equals(ShareConstant.TRADE_STATUS_SUCCESS_CODE)) {
				/** 调用充值回调 */
				accountFlowService.callbackRechargeSuccess(flowMap);
			} else {
				accountFlowService.callbackRechargeFailed(flowMap);
			}
			log.info("=====================结束处理tpp充值回调=========================");
			return ShareConstant.TRADE_STATUS_SUCCESS;
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 手机同步回调
	 *
	 * @author  wangjf
	 * @date    2015年6月8日 下午4:40:09
	 * @param request
	 * @param response
	 * @param requestParams
	 * @return
	 * @throws SLException
	 * @throws IOException
	 */
	@RequestMapping(value = "/appSynAuth", method = RequestMethod.POST, produces="application/json")
	public @ResponseBody ResultVo appSynAuth(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> requestParams) throws SLException, IOException {
		if(StringUtils.isEmpty((String)requestParams.get("tradeCode"))){
			return new ResultVo(false, "tradeCode不能为空");
		}
		return new ResultVo(true, "回调成功", thirdPartyPayService.appSynAuth(requestParams));
	}

    /**
     * 
     * 功能描述：获取真实的IP地址
     * @param request
     * @return
     * @author guoyx
     */
    public String getIpAddr(HttpServletRequest request)
    {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
        {
            ip = request.getRemoteAddr();
        }
        if (!StringUtils.isEmpty(ip) && ip.contains(","))
        {
            String[] ips = ip.split(",");
            ip = ips[ips.length - 1];
        }
        return ip;
    }
    
	/**
	 * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
	 *
	 * @param params
	 *            需要排序并参与字符拼接的参数组
	 * @param encode
	 *            是否需要urlEncode
	 * @return 拼接后字符串
	 */
	public String createLinkString(Map<String, Object> paramMap, boolean isEncode) {
		List<String> keys = new ArrayList<String>(paramMap.keySet());
		String prestr = "";
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = (String)paramMap.get(key);
			if (isEncode) {
				try {
					value = URLEncoder.encode(value, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
				prestr = prestr + key + "=" + value;
			} else {
				prestr = prestr + key + "=" + value + "&";
			}
		}

		return prestr;
	}
}
