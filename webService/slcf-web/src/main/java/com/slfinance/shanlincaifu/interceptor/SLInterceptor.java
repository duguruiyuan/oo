package com.slfinance.shanlincaifu.interceptor;

import java.io.IOException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.slfinance.shanlincaifu.cfg.AppConfig;

/**
 * 对与参数进行拦截
 * 
 * @author zhumin
 *
 */
@Slf4j
public class SLInterceptor extends HandlerInterceptorAdapter {

	private final AppConfig appConfig;
	
	private final String interceptorTime;
	
	private final String seed;
	
	@Autowired
	public SLInterceptor(AppConfig config) {
		appConfig = config;
		interceptorTime = appConfig.getSignAuth().getInterceptorTime();
		seed = appConfig.getSignAuth().getSeeds();
		
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object arg2) throws Exception {
//		if(request.getRequestURL().toString().endsWith("Job"))
//		{
//			return true;
//		}
		String service = null;
		String serviceTime = null;
		String sign = null;
		String appSource = null;
		if (appConfig.getSignAuth().isEnabled()) {

//			String strIP = getIpAddrByRequest(request);
//			if(!isInner(strIP)){
//					writeResponse(852, "验签失败=007", response);
//				return false;
//			}
			
			// 方法名称
			service = request.getHeader("service");
			// 调用时间 new Date()).getTime()
			serviceTime = request.getHeader("serviceTime");

			// 签名
			sign = request.getHeader("sign");
			// 来源
			appSource = request.getHeader("appSource");
			if (StringUtils.isEmpty(service)) {
				writeResponse(852, "验签失败=001", response);
				return false;

			} else if (StringUtils.isEmpty(serviceTime)) {
				writeResponse(852, "验签失败=002", response);
				return false;
			} else if (!serviceTime.matches("[0-9]+")) {
				writeResponse(852, "验签失败=003", response);
				return false;
			} else if (StringUtils.isEmpty(sign)) {
				writeResponse(852, "验签失败=004", response);
				return false;
			} else if (StringUtils.isEmpty(appSource)) {
				writeResponse(852, "验签失败=005", response);
				return false;
			} else {
				long diff = (new Date()).getTime() - Long.valueOf(serviceTime);
				long mm = diff / (1000 * 60);
				// 抛弃超过1分钟的签名
				if (mm > Integer.valueOf(interceptorTime == null || "".equals(interceptorTime) ? "15" : interceptorTime)) {
					writeResponse(852, "验签失败=006", response);
					return false;
				}
			}
			String _sing = "";
			// 不同应用不同签名
			if (appConfig.getSignAuth().getAppSource().equals(appSource)) {
				StringBuilder builder = new StringBuilder();
				String hashString = builder.append(service).append(serviceTime).append(appSource).append(seed).toString();
				// web1 为约定字符串
				_sing = Hashing.md5().hashString(hashString, Charsets.UTF_8).toString();
			}
			if (!_sing.equalsIgnoreCase(sign)) {
				// 验证失败
				writeResponse(852, "验证失败", response);
				return false;
			}

		}

		return true;
	}

	private void writeResponse(int code, String message, HttpServletResponse response) throws IOException {
		log.debug("验证失败:" + message);
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.addIntHeader("code", code);
		response.addHeader("message", "message");
		response.flushBuffer();
	}

	/**
	 * 10.0.0.0~10.255.255.255 
	 * 172.16.0.0~172.31.255.255
	 * 192.168.0.0~192.168.255.255
	 * 
	 * @param ip
	 * @return
	 */
	static String  reg = "(10|172|192)\\.([0-1][0-9]{0,2}|[2][0-5]{0,2}|[3-9][0-9]{0,1})\\.([0-1][0-9]{0,2}|[2][0-5]{0,2}|[3-9][0-9]{0,1})\\.([0-1][0-9]{0,2}|[2][0-5]{0,2}|[3-9][0-9]{0,1})";// 正则表达式=。
	static Pattern p = Pattern.compile(reg);
	public boolean isInner(String ip) {
		Matcher matcher = p.matcher(ip);
		return matcher.find();
	}

	/**
	 * 请求ip
	 * 
	 * 获取
	 * 
	 * @param request
	 * @return
	 */
	public  String getIpAddrByRequest(HttpServletRequest request) {

		String ip = request.getHeader("x-forwarded-for");

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {

			ip = request.getHeader("Proxy-Client-IP");

		}

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {

			ip = request.getHeader("WL-Proxy-Client-IP");

		}

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {

			ip = request.getRemoteAddr();

		}

		return ip;

	}
}
