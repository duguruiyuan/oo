package com.slfinance.shanlincaifu.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.TransAccountInfoService;
import com.slfinance.shanlincaifu.utils.CommonUtils;
import com.slfinance.vo.ResultVo;

/**
 * 
 * 
 * @author zhumin 调帐
 * @version $Revision:1.0.0, $Date: 2015年4月22日 上午11:16:56 $
 */
@Controller
@RequestMapping(value = "/transAccountInfo", produces = "application/json;charset=UTF-8")
public class TransAccountInfoController {
	@Autowired
	private TransAccountInfoService transAccountInfoService;

	/**
	 * 还款计划导入
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/importTransAccountInfo", method = RequestMethod.POST)
	public @ResponseBody ResultVo importTransAccountInfo(HttpServletRequest request, HttpServletResponse response) throws SLException {
		try {
			String xml = CommonUtils.receiveInputStream(request.getInputStream());
			List<Map<String, Object>> result = CommonUtils.xmlToObject(xml, List.class);
			transAccountInfoService.batchImport(result);
			return new ResultVo(true, "操作成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResultVo(false, e.getMessage());
		}
	}

	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> list(@RequestBody Map<String, Object> paramsMap) throws SLException {
		return transAccountInfoService.list(paramsMap);

	}
	
	@RequestMapping(value = "/sendAdjustAccountProcess", method = RequestMethod.POST)
	public @ResponseBody ResultVo sendAdjustAccountProcess(@RequestBody Map<String, Object> paramsMap) throws SLException {
		return  transAccountInfoService.sendAdjustAccountProcess(paramsMap);

	}

}
