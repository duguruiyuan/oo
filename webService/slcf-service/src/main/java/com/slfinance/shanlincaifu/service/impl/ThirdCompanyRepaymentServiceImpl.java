package com.slfinance.shanlincaifu.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AccountInfoEntity;
import com.slfinance.shanlincaifu.entity.CustInfoEntity;
import com.slfinance.shanlincaifu.repository.AccountInfoRepository;
import com.slfinance.shanlincaifu.repository.LogInfoEntityRepository;
import com.slfinance.shanlincaifu.repository.ProjectInfoRepository;
import com.slfinance.shanlincaifu.repository.ProjectInvestInfoRepository;
import com.slfinance.shanlincaifu.repository.RepaymentPlanInfoRepository;
import com.slfinance.shanlincaifu.repository.RepaymentRecordInfoRepository;
import com.slfinance.shanlincaifu.repository.SubAccountInfoRepository;
import com.slfinance.shanlincaifu.repository.custom.LoanInfoRepositoryCustom;
import com.slfinance.shanlincaifu.service.CustAccountService;
import com.slfinance.shanlincaifu.service.CustomerService;
import com.slfinance.shanlincaifu.service.FlowNumberService;
import com.slfinance.shanlincaifu.service.LoanRepaymentService;
import com.slfinance.shanlincaifu.service.ParamService;
import com.slfinance.shanlincaifu.service.ProjectRepaymentService;
import com.slfinance.shanlincaifu.service.ThirdCompanyRepaymentService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.vo.ResultVo;

/**
 * 商户还款服务实现
 * 
 * @author zhangt
 * @date 2016年11月30日上午11:06:48
 */
@Service("thirdCompanyRepaymentService")
public class ThirdCompanyRepaymentServiceImpl implements
		ThirdCompanyRepaymentService {

	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private LoanInfoRepositoryCustom loanInfoRepositoryCustom;
	
	@Autowired
	private AccountInfoRepository accountInfoRepository;
	
	@Autowired
	private ParamService paramService;
	
	@Autowired
	private RepaymentPlanInfoRepository repaymentPlanInfoRepository;
	
	@Autowired
	private ProjectRepaymentService projectRepaymentService;
	
	@Autowired
	private LogInfoEntityRepository logInfoEntityRepository;
	
	@Autowired
	private ProjectInfoRepository projectInfoRepository;
	
	@Autowired
	private FlowNumberService numberService;
	
	@Autowired
	private SubAccountInfoRepository subAccountInfoRepository;
	
	@Autowired
	private CustAccountService custAccountService;
	
	@Autowired
	private ProjectInvestInfoRepository projectInvestInfoRepository;
	
	@Autowired
	private RepaymentRecordInfoRepository repaymentRecordInfoRepository;
	
	@Autowired
	private LoanRepaymentService loanRepaymentService;
	
	@Override
	public ResultVo login(Map<String, Object> param) {
		CustInfoEntity custInfo = customerService.findByLoginNameOrMobile(param);
		if (custInfo == null) {
			return new ResultVo(false, "用户名或密码错误");
		}
		
		if (!Constant.CUST_TYPE_COMPNAY.equals(custInfo.getCustType())) {
			return new ResultVo(false, "用户没有权限登录");
		}
		return new ResultVo(true, "查询成功", custInfo);
	}

	@Override
	public ResultVo queryRepaymentList(Map<String, Object> param) {
		Map<String, Object> resultMap = Maps.newHashMap();
		String userId = (String) param.get("userId");
		AccountInfoEntity accountInfoEntity = accountInfoRepository.findByCustId(userId);
		if (accountInfoEntity == null) {
			return new ResultVo(false, "账户不存在");
		}
		resultMap.put("availableAccAmount", accountInfoEntity.getAccountAvailableAmount());

		Page<Map<String, Object>> page = loanInfoRepositoryCustom.queryThirdCompanyRepaymentList(param);
		resultMap.put("iTotalDisplayRecords", page.getTotalElements());
		resultMap.put("data", page.getContent());
		return new ResultVo(true, "查询还款列表成功", resultMap);
	}

	@Override
	public ResultVo queryProjectDetailInfo(Map<String, Object> param)
			throws SLException {
		Map<String, Object> resultMap = Maps.newHashMap();
//		List<Map<String, Object>> projectInfoList = thirdCompanyRepaymentRepositoryCustom.queryProjectDetailInfo(param);
//		if (projectInfoList == null || projectInfoList.size() <= 0) {
//			return new ResultVo(false, "项目不存在");
//		}
//		resultMap.put("projectInfo", projectInfoList.get(0));
//		
//		param.put("overdueRate", paramService.findOverdueRate());
//		List<Map<String, Object>> repaymentList = thirdCompanyRepaymentRepositoryCustom.queryRepaymentPlanList(param);
//		resultMap.put("repaymentList", repaymentList);
//		
		return new ResultVo(true, "查询项目详情成功", resultMap);
	}

	@Override
	public ResultVo normalRepayment(Map<String, Object> param)
			throws SLException {
		
		return loanRepaymentService.userRepayment(param);
	}

}
