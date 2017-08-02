/** 
 * @(#)APPAccountController.java 1.0.0 2015年5月20日 下午2:40:26  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.controller.mobile;

import java.util.Map;

import com.slfinance.shanlincaifu.entity.CustInfoEntity;
import com.slfinance.shanlincaifu.service.mobile.APPUserSafeService;
import com.slfinance.shanlincaifu.utils.CommonUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.CustomerService;
import com.slfinance.shanlincaifu.service.LoanManagerService;
import com.slfinance.shanlincaifu.service.WealthInfoService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.vo.ResultVo;

/**   
 * APP手机端账户模块业务控制器
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年5月20日 下午2:40:26 $ 
 */
@Controller
@RequestMapping(value="/product", produces="application/json;charset=UTF-8")
public class APPProductController {
	
	@Autowired
	private LoanManagerService loanManagerService;
	
	@Autowired
	private WealthInfoService wealthInfoService;
	
	@Autowired
	private CustomerService customerService;

    @Autowired
    private APPUserSafeService appUserSafeService;

	/**
	 * 账户首页
	 */
	@RequestMapping(value="/queryPriority", method = RequestMethod.POST)
	public @ResponseBody ResultVo queryPriority(@RequestBody Map<String,Object> paramsMap)throws SLException{
		ResultVo resultVo = new ResultVo(false);
		if(Constant.PRODUCT_TYPE_07.equals(paramsMap.get("productType"))) {
			resultVo = loanManagerService.queryPriority(paramsMap);
		}
		
		return resultVo;
	}
	/**
	 * 账户首页(包含新手标)
	 * @param paramsMap
	 * @return
	 * @throws SLException
	 */
	@RequestMapping(value="/queryPriorityNewerFlag", method = RequestMethod.POST)
	public @ResponseBody ResultVo queryPriorityNewerFlag(@RequestBody Map<String,Object> paramsMap)throws SLException{
		ResultVo resultVo = new ResultVo(false);
		if(Constant.PRODUCT_TYPE_07.equals(paramsMap.get("productType"))) {
			resultVo = loanManagerService.queryPriorityNewerFlag(paramsMap);
		}
		
		return resultVo;
	}
	
	/**
	 * 产品分享
	 *
	 * @author  wangjf
	 * @date    2016年12月7日 下午4:07:02
	 * @param paramsMap
	 * @return
	 * @throws SLException
	 */
	@RequestMapping(value="/shareProduct", method = RequestMethod.POST)
	public @ResponseBody ResultVo shareProduct(@RequestBody Map<String,Object> paramsMap)throws SLException{
		
		ResultVo resultVo = customerService.queryEmployee((String)paramsMap.get("mobile"));
		if(!ResultVo.isSuccess(resultVo)) {
            return resultVo;
        }
        CustInfoEntity data = (CustInfoEntity) resultVo.result.get("data");

		if(Constant.PRODUCT_TYPE_06.equals(paramsMap.get("productType"))) {
			resultVo = wealthInfoService.queryEmployeeWealthList(paramsMap);
		}
		else if(Constant.PRODUCT_TYPE_08.equals(paramsMap.get("productType"))) {
			resultVo = loanManagerService.queryEmployeeLoanList(paramsMap);
		}
		else if(Constant.PRODUCT_TYPE_09.equals(paramsMap.get("productType"))) {
			resultVo = loanManagerService.queryEmployeeTransferList(paramsMap);
		}
		else if(Constant.PRODUCT_TYPE_11.equals(paramsMap.get("productType"))){
			resultVo = loanManagerService.queryEmployeeLoanList(paramsMap);
		}

        String custId = data.getId();
        /** 2017-6-9 查询用户投资保有量对应的级别 */
        Map<String, Object> holdAmountRanking = appUserSafeService.getUserRanking(custId);
        if (!CommonUtils.isEmpty(holdAmountRanking)) {
        	resultVo.result.put("holdAmountRanking", holdAmountRanking.get("ranking"));
        	resultVo.result.put("showHoldAmountRanking", holdAmountRanking.get("isShow"));
        } else {
        	resultVo.result.put("holdAmountRanking", 0);
        	resultVo.result.put("showHoldAmountRanking", "0");
        }
		
		return resultVo;
	}
}
