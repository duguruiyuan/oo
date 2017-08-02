package com.slfinance.shanlincaifu.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.RepaymentPlanCopeEntity;
import com.slfinance.shanlincaifu.service.RepaymentPlanCopeService;
import com.slfinance.vo.ResultVo;


@Controller
@RequestMapping(value="/repaymentPlan")
public class RepaymentPlanCopeController {

	@Autowired
	private RepaymentPlanCopeService repaymentPlanCopeService;

	@SuppressWarnings("unchecked")
	@RequestMapping(value="/update",produces="application/json")
	public @ResponseBody ResultVo updatePlan(@RequestBody Map<String, Object> requestParams) throws SLException{
		try {
			String loanNo = (String) requestParams.get("loanNo");
			List<Map<String, Object>> plan = (List<Map<String, Object>>) requestParams.get("plan");
			List<RepaymentPlanCopeEntity> planlist = Lists.newArrayList();
			ObjectMapper mapper2 = new ObjectMapper();
			for (Map<String, Object> map : plan) {
				RepaymentPlanCopeEntity pj = mapper2.readValue(mapper2.writeValueAsString(map), RepaymentPlanCopeEntity.class);
				planlist.add(pj);
			}
			repaymentPlanCopeService.updatePlan(loanNo,planlist);
			
			return new ResultVo(true);
		} catch (Exception e) {
			throw new SLException(e);
		}
	}
	@SuppressWarnings("unused")
	@RequestMapping(value="/query")
	public @ResponseBody ResultVo queryByLoanCode(@RequestParam(value="loanNo",required=true) String LoanNo) throws SLException{

		try {
			List<RepaymentPlanCopeEntity> list = repaymentPlanCopeService.queryByLoanCode(LoanNo);
			ResultVo resultVo = new ResultVo(true);
			resultVo.putValue("data", list);
			return resultVo;
		} catch (Exception e) {
			throw new SLException(e);
		}
	}

	

}
