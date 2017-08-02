package com.slfinance.shanlincaifu.controller;

import java.text.ParseException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.slfinance.annotation.AutoDispatch;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.WebPortalService;

/**
 * 
 * <网贷天眼信息公示控制器>
 * <功能详细描述>
 * 
 * @author  Mgp
 * @version  [版本号, 2017年6月14日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
@SuppressWarnings("all")
@RestController
@RequestMapping("WebPortal")
public class WebPortalController {

	@Autowired
	private WebPortalService webPortalService;

	@RequestMapping(value = "/loans",method =RequestMethod.POST)
	public ResponseEntity loans(@RequestBody Map<String, Object> model) throws SLException{
		try {
			Map<String, Object> result =webPortalService.loanInfoMap(model);
			System.err.println(JSONObject.toJSONString(result, SerializerFeature.PrettyFormat));
			return new ResponseEntity(result, HttpStatus.OK);
			
		} catch (ParseException e) {
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@RequestMapping(value="/data",method=RequestMethod.POST)
	public ResponseEntity data(@RequestBody Map<String, Object> model) {
		try {
			Map<String,Object> result = webPortalService.investInfoMap(model);
			System.err.println(JSONObject.toJSONString(result, SerializerFeature.PrettyFormat));
			return new ResponseEntity(result, HttpStatus.OK);
		} catch (ParseException e) {
			return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value="/token",method=RequestMethod.POST)
	public ResponseEntity token(@RequestBody Map<String,Object> model) {
		Map<String, Object> result = webPortalService.token(model.get("username")+"", model.get("password")+"");
		return new ResponseEntity(result, HttpStatus.OK);
	}
}
