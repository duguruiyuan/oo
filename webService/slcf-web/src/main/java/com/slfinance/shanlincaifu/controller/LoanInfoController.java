package com.slfinance.shanlincaifu.controller;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.LoanCustInfoEntity;
import com.slfinance.shanlincaifu.entity.RepaymentPlanInfoEntity;
import com.slfinance.shanlincaifu.service.LoanInfoService;
import com.slfinance.shanlincaifu.utils.CommonUtils;
import com.slfinance.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 债权业务控制器
 *
 * @author  zhoudl
 * @version $Revision:1.0.0, $Date: 2015年5月5日 上午9:26:12 $
 */

@Controller
@RequestMapping(value="/loanInfo", produces="application/json;charset=UTF-8")
public class LoanInfoController {

	@Autowired
	private LoanInfoService loanInfoService;

	/**
	 * 债权列表导入
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/importLoan", method = RequestMethod.POST)
	public @ResponseBody ResultVo importLoan(HttpServletRequest request,HttpServletResponse response) throws SLException{
		try {
			String xml = CommonUtils.receiveInputStream(request.getInputStream());
			List<LoanCustInfoEntity> loanList = (List<LoanCustInfoEntity>) CommonUtils.xmlToObject(xml, List.class);
			return loanInfoService.importLoanInfo(loanList);
		} catch (Exception e) {
			throw new SLException(e);
		}
	}

	/**
	 * 还款计划导入
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/importRepaymentPlan", method = RequestMethod.POST)
	public @ResponseBody ResultVo importRepaymentPlan(HttpServletRequest request,HttpServletResponse response) throws SLException{
		try {
			String xml = CommonUtils.receiveInputStream(request.getInputStream());
			List<RepaymentPlanInfoEntity> repayPlanList = (List<RepaymentPlanInfoEntity>) CommonUtils.xmlToObject(xml, List.class);
			return loanInfoService.importRepaymentPlan(repayPlanList);
		} catch (Exception e) {
			throw new SLException(e);
		}
	}

	/**
	 * 查询债权列表
	 */
	@RequestMapping(value="/queryLoan", method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> queryLoan(@RequestBody Map<String, Object> paramsMap) throws SLException{
		return loanInfoService.queryConditionLoan(paramsMap);
	}

	/**
	 * 根据ID查询还款计划详情
	 */
	@RequestMapping(value="/queryLoanRepayPlan", method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> queryLoanRepayPlan(@RequestBody Map<String, Object> paramsMap) throws SLException{
		return loanInfoService.queryLoanRepaymentDetail(paramsMap);
	}

	@RequestMapping(value = "/queryExpLoan", method = {RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody ResultVo queryExpLoan() {
		return loanInfoService.queryExpLoan();
	}

}
