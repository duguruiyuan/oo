package com.slfinance.shanlincaifu.service;

import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;

/**
 * 充值前调用实名认证服务接口
 * 
 * @author gaoll
 * @version $Revision:1.0.0, $Date: 2015年11月2日 下午3:49:12 $
 */
public interface RealNameAuthThenRechargeService {
	
	/**
	 * 实名认证后充值
	 *
	 * @author gaoll
	 * @date 2015年11月2日 下午4:07:45
	 * @param params
	 * @return
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "custId", required = true, requiredMessage = "客户编号不能为空"),
			@Rule(name = "tradeAmount", required = true, requiredMessage = "充值金额不能为空", number = true, numberMessage = "充值金额只能为数字"),
			@Rule(name = "bankNo", required = true, requiredMessage = "银行编号不能为空"),
			@Rule(name = "payType", required = true, requiredMessage = "支付类型不能为空"),
			@Rule(name = "custName", required = true, requiredMessage = "客户姓名不能为空"),
			@Rule(name = "credentialsCode", required = true, requiredMessage = "身份证号不能为空" ,identification=true, identificationMessage="身份证格式错误")
	})
	public ResultVo AuthenticationThenRecharge(Map<String, Object> params) throws SLException;

	}
