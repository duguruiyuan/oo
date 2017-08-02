/** 
 * @(#)ProjectCompanyServiceImpl.java 1.0.0 2016年1月11日 上午11:46:36  
 *  
 * Copyright © 2016 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service.impl;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AccountInfoEntity;
import com.slfinance.shanlincaifu.entity.CustInfoEntity;
import com.slfinance.shanlincaifu.repository.AccountInfoRepository;
import com.slfinance.shanlincaifu.repository.CustInfoRepository;
import com.slfinance.shanlincaifu.service.FlowNumberService;
import com.slfinance.shanlincaifu.service.ProjectCompanyService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.vo.ResultVo;

/**   
 * 公司服务实现类
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2016年1月11日 上午11:46:36 $ 
 */
@Service("projectCompanyService")
public class ProjectCompanyServiceImpl implements ProjectCompanyService {

	@Autowired
	private CustInfoRepository custInfoRepository;
	
	@Autowired
	private AccountInfoRepository accountInfoRepository;
	
	@Autowired
	private FlowNumberService flowNumberService;
	
	@Override
	public ResultVo queryCompanyList(Map<String, Object> params) {
		Map<String, Object> resultMap = Maps.newHashMap();
		
		params.put("projectType", Constant.CUST_KIND_02);
		Page<Map<String, Object>> page = custInfoRepository.findCompanyList(params);
		resultMap.put("iTotalDisplayRecords", page.getTotalElements());
		resultMap.put("data", page.getContent());
		
		return new ResultVo(true, "查询公司数据成功", resultMap);
	}

	@Transactional(readOnly = false, rollbackFor = SLException.class)
	@Override
	public ResultVo saveCompany(Map<String, Object> params) throws SLException {
		
		CustInfoEntity existsCust = custInfoRepository.findByLoginName((String)params.get("companyName"));
		if(existsCust != null) {
			return new ResultVo(false, "融资租赁公司名称不能重复");
		}

		existsCust = custInfoRepository.findByTel((String)params.get("telephone"));
		if(existsCust != null) {
			return new ResultVo(false, "融资租赁公司联系电话不能重复");
		}
		
		// 新建客户信息
		CustInfoEntity custInfoEntity = new CustInfoEntity();
		custInfoEntity.setLoginName((String)params.get("companyName"));
		custInfoEntity.setCustKind((String)params.get("projectType"));
		custInfoEntity.setTel((String)params.get("telephone"));
		custInfoEntity.setCommunAddress((String)params.get("communAddress"));
		custInfoEntity.setCustName((String)params.get("custName"));
		custInfoEntity.setMemo((String)params.get("memo"));
		custInfoEntity.setBasicModelProperty((String)params.get("userId"), true);
		custInfoEntity = custInfoRepository.save(custInfoEntity);
		
		// 创建账户
		AccountInfoEntity accountInfoEntity = new AccountInfoEntity();
		accountInfoEntity.setCustId(custInfoEntity.getId());
		accountInfoEntity.setAccountNo(flowNumberService
				.generateCustomerNumber());
		accountInfoEntity.setAccountTotalAmount(BigDecimal.ZERO);
		accountInfoEntity.setAccountFreezeAmount(BigDecimal.ZERO);
		accountInfoEntity.setAccountAvailableAmount(BigDecimal.ZERO);
		accountInfoEntity.setBasicModelProperty((String)params.get("userId"), true);
		accountInfoRepository.save(accountInfoEntity);
		
		return new ResultVo(true, "添加商户成功");
	}

	@Override
	public ResultVo queryCompanyById(Map<String, Object> params) {
		Map<String, Object> resultMap = Maps.newHashMap();
		
		params.put("start", 0);
		params.put("length", Integer.MAX_VALUE);
		Page<Map<String, Object>> page = custInfoRepository.findCompanyList(params);
		if(page.getTotalElements() > 0) {
			resultMap = page.getContent().get(0);
		}
		
		return new ResultVo(true, "查询公司数据成功", resultMap);
	}

	@Override
	public ResultVo findByCompanyName(Map<String, Object> params) {
		
		params.put("start", 0);
		params.put("length", Integer.MAX_VALUE);
		params.put("projectType", Constant.CUST_KIND_02);
		Page<Map<String, Object>> page = custInfoRepository.findCompanyList(params);
		
		return new ResultVo(true, "查询公司数据成功", page.getContent());
	}

}
