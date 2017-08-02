/** 
 * @(#)OpenController.java 1.0.0 2015年7月2日 上午9:50:58  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.CustInfoEntity;
import com.slfinance.shanlincaifu.service.BannerService;
import com.slfinance.shanlincaifu.service.OpenService;
import com.slfinance.shanlincaifu.utils.CacheUtils;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.vo.ResultVo;

/**   
 * 对外开放服务
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年7月2日 上午9:50:58 $ 
 */
@RestController
@RequestMapping("openservice")
public class OpenController extends WelcomeController {
	
	@Autowired 
	private OpenService openService;
	
	@Autowired
	private BannerService bannerService;
	
	
	/**
	 * 注册
	 *
	 * @author  wangjf
	 * @date    2015年7月13日 上午11:56:40
	 * @param request
	 * @param response
	 * @param requestParams
	 * @return
	 * @throws SLException
	 * @throws IOException
	 */
	@RequestMapping(value = "/register", method = RequestMethod.POST, produces="application/json")
	public @ResponseBody Map<String, Object> register(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> requestParams) throws SLException, IOException {
		requestParams.put("messageType", Constant.SMS_TYPE_REGISTER);
		return openService.openRegister(requestParams);
	}
	
	/**
	 * 获取注册短信验证码
	 *
	 * @author  wangjf
	 * @date    2015年7月13日 上午11:56:47
	 * @param request
	 * @param response
	 * @param requestParams
	 * @return
	 * @throws SLException
	 * @throws IOException
	 */
	@RequestMapping(value = "/registersms", method = RequestMethod.POST, produces="application/json")
	public @ResponseBody Map<String, Object> sendRegisterSms(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> requestParams) throws SLException, IOException {
		requestParams.put("messageType", Constant.SMS_TYPE_REGISTER);
		requestParams.put("custId", Constant.SYSTEM_USER_BACK);
		return openService.openSendSms(requestParams);
	}
	
	/**
	 * 查询实名认证情况
	 *
	 * @author  wangjf
	 * @date    2015年8月10日 下午4:51:58
	 * @param request
	 * @param response
	 * @param requestParams
	 * @return
	 * @throws SLException
	 * @throws IOException
	 */
	@RequestMapping(value = "/query/realnameauth", method = RequestMethod.POST, produces="application/json")
	public @ResponseBody Map<String, Object> queryRealNameAuth(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> requestParams) throws SLException, IOException {
		return openService.queryRealNameAuth(requestParams);
	}
	
	/**
	 * 查询充值情况
	 *
	 * @author  wangjf
	 * @date    2015年8月10日 下午4:52:09
	 * @param request
	 * @param response
	 * @param requestParams
	 * @return
	 * @throws SLException
	 * @throws IOException
	 */
	@RequestMapping(value = "/query/recharge", method = RequestMethod.POST, produces="application/json")
	public @ResponseBody Map<String, Object> queryRecharge(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> requestParams) throws SLException, IOException {
		return openService.queryRecharge(requestParams);
	}
	
	/**
	 * 保存下载信息
	 * 
	 * @author zhangt
	 * @date   2015年10月21日下午3:54:57
	 * @param request
	 * @param response
	 * @param requestParams
	 * @return
	 * @throws SLException
	 * @throws IOException
	 */
	@RequestMapping(value = "/download", method = RequestMethod.POST, produces="application/json")
	public @ResponseBody Map<String, Object> saveDownloadMessage(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> requestParams) throws SLException, IOException {
		requestParams.put("openServiceType", Constant.OPERATION_TYPE_24);
		return openService.handleRequest(requestParams);
	}
	
	/**
	 * 确认吉融通流量订购结果
	 * 
	 * @author zhangt
	 * @date   2015年10月23日上午11:26:36
	 * @param request
	 * @param response
	 * @param requestParams
	 * @return
	 * @throws SLException
	 * @throws IOException
	 */
	@RequestMapping(value = "/confirm", method = RequestMethod.POST, produces="application/json")
	public @ResponseBody String confirmStatus(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> requestParams) throws SLException, IOException {
		return openService.confirmStatus(requestParams);
	}
	
	/**
	 * 查询注册情况
	 *
	 * @author  wangjf
	 * @date    2015年12月22日 下午4:58:26
	 * @param request
	 * @param response
	 * @param requestParams
	 * @return
	 * @throws SLException
	 * @throws IOException
	 */
	@RequestMapping(value = "/queryRegister", method = RequestMethod.POST, produces="application/json")
	public @ResponseBody Map<String, Object> queryRegister(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> requestParams) throws SLException, IOException {
		return openService.queryRegister(requestParams);
	}
	
	/**
	 * 查询活动
	 * 
	 * @param request
	 * @param response
	 * @param requestParams
	 * @return
	 * @throws SLException
	 * @throws IOException
	 */
	@RequestMapping(value = "/queryActivity", method = RequestMethod.POST, produces="application/json")
	public @ResponseBody ResultVo queryActivity(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> requestParams) throws SLException, IOException {
		return openService.queryFlowDataActivityUrl(requestParams);
	} 
	
	/**
	 * 保存项目
	 *
	 * @author  wangjf
	 * @date    2016年12月1日 上午11:32:55
	 * @param request
	 * @param response
	 * @param requestParams
	 * @return
	 * @throws SLException
	 * @throws IOException
	 */
	@RequestMapping(value = "/saveProject", method = RequestMethod.POST, produces="application/json")
	public @ResponseBody Map<String, Object> saveProject(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> requestParams) throws SLException, IOException {
		return openService.saveProject(requestParams);
	}


	/**
	 *
	 * 逾期还款请求代扣接口
	 * @author  张祥
	 * @date    2017年7月12日 上午10:46:55
	 * @param request
	 * @param response
	 * @param requestParams
	 * @return
	 * @throws SLException
	 * @throws IOException
	 */
	@RequestMapping(value = "/repaymentLimit", method = RequestMethod.POST, produces="application/json")
	public @ResponseBody Map<String,Object> repaymentLimit(HttpServletRequest request, HttpServletResponse response,@RequestBody Map<String, Object> requestParams) throws SLException, IOException {
		return openService.timeLimitWithHold(requestParams);
	}




	/**
	 * 查询项目状态
	 *
	 * @author  wangjf
	 * @date    2016年12月1日 上午11:33:24
	 * @param request
	 * @param response
	 * @param requestParams
	 * @return
	 * @throws SLException
	 * @throws IOException
	 */
	@RequestMapping(value = "/queryProject", method = RequestMethod.POST, produces="application/json")
	public @ResponseBody Map<String, Object> queryProject(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> requestParams) throws SLException, IOException {
		return openService.queryProject(requestParams);
	} 
	
	/**
	 * 查询预约金额
	 *
	 * @author  zhangyb
	 * @date    2017年7月17日 
	 * @param request
	 * @param response
	 * @param requestParams
	 * @return
	 * @throws SLException
	 * @throws IOException
	 */
	@RequestMapping(value = "/queryPreAmount", method = RequestMethod.POST, produces="application/json")
	public @ResponseBody Map<String, Object> queryPreAmount(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> requestParams) throws SLException, IOException {
		return openService.queryPreAmount(requestParams);
	} 
	
    /**
     * 接收流标/放款通知 
     * @author zhangze
     * @date    2017年6月2日 下午1:29:24
     * @param request
     * @param response
     * @param requestParams
     * @return
     * @throws SLException
     * @throws IOException
     */
    @RequestMapping(value = "/operateProject", method = RequestMethod.POST, produces="application/json")
    public @ResponseBody Map<String, Object> operateProject(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> requestParams) throws SLException, IOException {
        return openService.operateProject(requestParams);
    } 
	
	/**
	 * 查询实名认证
	 *
	 * @author  wangjf
	 * @date    2016年12月1日 上午11:33:59
	 * @param request
	 * @param response
	 * @param requestParams
	 * @return
	 * @throws SLException
	 * @throws IOException
	 */
	@RequestMapping(value = "/queryRealName", method = RequestMethod.POST, produces="application/json")
	public @ResponseBody Map<String, Object> queryRealName(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> requestParams) throws SLException, IOException {
		return openService.queryRealName(requestParams);
	} 
	
	/**
	 * 绑卡或者解绑
	 *
	 * @author  wangjf
	 * @date    2016年12月1日 上午11:34:34
	 * @param request
	 * @param response
	 * @param requestParams
	 * @return
	 * @throws SLException
	 * @throws IOException
	 */
	@RequestMapping(value = "/bindCard", method = RequestMethod.POST, produces="application/json")
	public @ResponseBody Map<String, Object> bindCard(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> requestParams) throws SLException, IOException {
		return openService.bindCard(requestParams);
	} 
	
	/**
	 * 还款通知
	 *
	 * @author  wangjf
	 * @date    2016年12月3日 下午4:42:24
	 * @param request
	 * @param response
	 * @param requestParams
	 * @return
	 * @throws SLException
	 * @throws IOException
	 */
	@RequestMapping(value = "/repayment", method = RequestMethod.POST, produces="application/json")
	public @ResponseBody Map<String, Object> repayment(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> requestParams) throws SLException, IOException {
		return openService.repayment(requestParams);
	} 
	
	/**
	 * 查询协议
	 *
	 * @author  wangjf
	 * @date    2016年12月9日 下午9:19:00
	 * @param request
	 * @param response
	 * @param requestParams
	 * @return
	 * @throws SLException
	 * @throws IOException
	 */
	@RequestMapping(value = "/queryProtocol", method = RequestMethod.POST, produces="application/json")
	public @ResponseBody Map<String, Object> queryProtocol(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> requestParams) throws SLException, IOException {
		return openService.queryProtocol(requestParams);
	} 
	

	/**
	 * 网贷之家接口查询
	 *
	 * @author fengyl
	 * @date 2017年4月17日
	 * @param response
	 * @param page
	 * @param pageSize
	 * @param date
	 * @param token
	 * @return
	 * @throws SLException
	 */
	@RequestMapping(value = "/queryWdzj")
	public @ResponseBody Map<String, Object> queryWdzj(HttpServletResponse response, int page, int pageSize,String date,String token) throws SLException {
		
		@SuppressWarnings("unchecked")
		List<String> tokenList = (List<String>) CacheUtils.get("wdzj", "token");
		if (tokenList== null || !tokenList.contains(token)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	    	return new HashMap<>();
		}
		
		HashMap<String, Object> requestParams = new HashMap<>();
		requestParams.put("page", page);
		requestParams.put("pageSize", pageSize);
		requestParams.put("date", date);
	    return openService.queryWdzj(requestParams);
	} 
	
	/**
	 * 网贷之家登录
	 * @author  fengyl
	 * @date    2017年4月17日 
	 * @param username
	 * @param password
	 * @return
	 * @throws SLException
	 */
	@RequestMapping(value = "/loginWdzj")
	public @ResponseBody Map<String, Map<String, String>> loginWdzj(
			HttpServletResponse response, String username, String password)
			throws SLException {
		CustInfoEntity custInfoEntity = openService.loginWdzj(username,password);
		if (custInfoEntity == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return new HashMap<>();
		}

		@SuppressWarnings("unchecked")
		List<String> tokenList = (List<String>) CacheUtils.get("wdzj", "token");
		if (tokenList == null) {
			tokenList = new ArrayList<>();
			CacheUtils.put("wdzj", "token", tokenList);
		}

		String token = UUID.randomUUID().toString();
		tokenList.add(token);

		Map<String, String> data = new HashMap<>();
		data.put("token", token);
		HashMap<String, Map<String, String>> result = new HashMap<>();
		result.put("data", data);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/queryWxShare")
	public @ResponseBody ResultVo queryWxShare(@RequestBody Map<String, Object> params)
			throws SLException {
		Map<String, Object> resultMap = Maps.newHashMap();
		
		Map<String, Object> requestParam = Maps.newHashMap();
		requestParam.put("bannerType", params.get("bannerType"));
		ResultVo resultVo = bannerService.queryActivity(requestParam);
		if(!ResultVo.isSuccess(resultVo)) { // 查询到数据
			return resultVo;
		}
		// 活动数据
		List<Map<String, Object>> dataList = (List<Map<String, Object>>)resultVo.getValue("data");
		Map<String, Object> data = dataList.get(0);
		
		Map<String, Object> wxParam = Maps.newHashMap();
		wxParam.put("url", params.get("url"));
		wxParam.put("isForceRefresh", params.get("isForceRefresh"));
		Map<String, Object> wxRespose = openService.getWXSignature(wxParam);
		if(wxRespose.containsKey("error")) {
			return new ResultVo(false, wxRespose.get("error"));
		}
		
		resultMap.put("imgUrl", data.get("bannerImagePath"));
		resultMap.put("link", data.get("bannerUrl"));
		resultMap.put("desc", data.get("bannerContent"));
		resultMap.put("title", data.get("bannerTitle"));
		resultMap.put("AppId", wxRespose.get("appID"));
		resultMap.put("Timestamp", wxRespose.get("timestamp"));
		resultMap.put("NonceStr", wxRespose.get("nonceStr"));
		resultMap.put("Signature", wxRespose.get("signature"));
		
		return new ResultVo(true, "查询成功", resultMap);
	}
	
}
