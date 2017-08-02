package com.slfinance.shanlincaifu.service.impl;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.CustInfoEntity;
import com.slfinance.shanlincaifu.repository.CustInfoRepository;
import com.slfinance.shanlincaifu.service.AccountService;
import com.slfinance.shanlincaifu.service.RealNameAuthThenRechargeService;
import com.slfinance.shanlincaifu.service.RealNameAuthenticationService;
import com.slfinance.vo.ResultVo;

/**
 * 充值前调用实名认证业务接口实现
 *
 * @author gaoll
 * @version $Revision:1.0.0, $Date: 2015年11月2日 下午4:09:32 $
 */
@Slf4j
@Service("realNameAuthThenRechargeService")
public class RealNameAuthThenRechargeServiceImpl implements
RealNameAuthThenRechargeService {
	
	@Autowired
	private RealNameAuthenticationService realNameAuthenticationService;
	
	@Autowired
	private AccountService accountService; 
	
	@Autowired
	CustInfoRepository custInfoRepository;
	
	@Override
	public ResultVo AuthenticationThenRecharge(Map<String, Object> params) throws SLException {
		String custId = (String) params.get("custId");
		CustInfoEntity customer = custInfoRepository.findOne(custId);
		if(customer == null) return new ResultVo(false, "该用户不存在");
		ResultVo resultVo = new ResultVo(true);
		// 若没有实名认证过，做实名认证，认证成功，充值，认证失败，不充值。若已经实名认证过，则直接充值。
		if(StringUtils.isEmpty(customer.getCredentialsCode())) {
			try {
				resultVo = realNameAuthenticationService.preverifyIdentification(params);
				if(!ResultVo.isSuccess(resultVo))
					return resultVo;
				resultVo = realNameAuthenticationService.verifyIdentification(params);
				if(!ResultVo.isSuccess(resultVo))
					return resultVo;
			} catch (SLException e) {
				log.error(String.format("用户%s实名认证失败！%s",
						(String) params.get("custId"), e.getMessage()));
				return new ResultVo(false, e.getMessage());
			} catch (Exception e) {
				log.error(String.format("用户%s实名认证失败！%s",
						(String) params.get("custId"), e.getMessage()));
				return new ResultVo(false, e.getMessage());
			} finally {
				realNameAuthenticationService.postverifyIdentification(resultVo);
			}
		}
		try {
			resultVo = accountService.rechargeApply(params);
		} catch (Exception e) {
			log.error(String.format("用户%s充值失败！%s",
					(String) params.get("custId"), e.getMessage()));
			return new ResultVo(false, e.getMessage());
		}
		
		return resultVo;

	}
}
