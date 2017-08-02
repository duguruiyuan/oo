package com.slfinance.shanlincaifu.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.CustInfoEntity;
import com.slfinance.shanlincaifu.entity.WealthCustIntoEntity;
import com.slfinance.shanlincaifu.entity.WealthInvestInfoEntity;
import com.slfinance.shanlincaifu.repository.custom.OfflineWealthRepositoryCustom;
import com.slfinance.shanlincaifu.repository.custom.WealthInvestIntoRepositoryCustom;
import com.slfinance.shanlincaifu.service.CustManagerService;
import com.slfinance.shanlincaifu.service.CustomerService;
import com.slfinance.shanlincaifu.service.OfflineWealthService;
import com.slfinance.shanlincaifu.service.WealthCustIntoService;
import com.slfinance.shanlincaifu.service.WealthInvestIntoService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.vo.ResultVo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("offlineWealthService")
public class OfflineWealthServiceImpl implements OfflineWealthService {
	
	@Autowired
	CustomerService customerService;
	
	@Autowired
	CustManagerService custManagerService;
	
	@Autowired
	WealthCustIntoService wealthCustIntoService;
	
	@Autowired
	WealthInvestIntoService wealthInvestIntoService;
	
	@Autowired
	OfflineWealthRepositoryCustom offlineWealthRepositoryCustom;
	
	@Autowired
	WealthInvestIntoRepositoryCustom wealthInvestIntoRepositoryCustom;

	/**
	 * 线下客户转移
	 */
	@Override
	public ResultVo transferOffLineCustManagerjob() throws SLException {
		//查找所有带出来的客户（bao_t_cust_info 中wealth_flag = 00）
		List<Map<String, Object>> list = offlineWealthRepositoryCustom.findOffLineCustInfo(new HashMap<String, Object>());
		for (Map<String, Object> map : list) {
			custManagerService.transferOffLineCustManager(map);
		}
		return new ResultVo(true);
	}

	/**
	 * 增量同步线下理财的客户信息和投资信息到财富中
	 * 1.同步理财客户信息表BAO_T_WEALTH_CUST_INTO
	 * 2.同步理财投资信息表BAO_T_WEALTH_INVEST_INFO
	 * @author liyy
	 * @serialData
	 * @return
	 */
	public ResultVo synchronizeCustInfoAndInvestInfoFromWealthToSlcf() {
		// 1.同步理财客户信息表BAO_T_WEALTH_CUST_INTO
		wealthCustIntoService.synchronizeCustInfoFromWealth();
		// 2.同步理财投资信息表BAO_T_WEALTH_INVEST_INFO
		wealthInvestIntoService.synchronizeInvestInfoFromWealth();
		
		return new ResultVo(true, "增量同步线下理财的客户信息和投资信息到财富中，执行成功");
	}

	@Transactional(readOnly=false, rollbackFor = SLException.class)
	@Override
	public ResultVo custTransfer(String custId, String credentialsCode)
			throws SLException {
		// 原线下是否存在客户
		WealthCustIntoEntity wealthCustInto = wealthCustIntoService.findCustInfoByCardId(credentialsCode);
		if(wealthCustInto == null){
			log.debug("原线下表中不存在改客户！");
			return new ResultVo(false);
		}
		// 原线下是否存在过投资
		List<WealthInvestInfoEntity> investList = wealthInvestIntoService.findInvestInfoByCardId(credentialsCode);
		if(investList == null || investList.size() == 0) {
			log.debug("原线下表中不存在有效投资");
			return new ResultVo(false);
		}
		// 判断原线下客户经理是否是 财富的客户经理
		String offWealthMgrCardId = wealthCustInto.getCustomerManagerCardId();
		if(StringUtils.isEmpty(offWealthMgrCardId)){
			log.debug("原线下客户经理为空");
			return new ResultVo(false);
		}
		CustInfoEntity mgrInfo = customerService.findCustInfoByCardIdAndIsEmployee(offWealthMgrCardId);
		if(mgrInfo == null){
			log.debug("原线下客户经理不存在于财富客户表中");
			return new ResultVo(false);
		}
		
		// 转移关系
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("userId", custId);
		param.put("custId", custId); // 数据的custid
//		param.put("oldCustManagerId", mgrInfo.getId());// 转出
		param.put("newCustManagerId", mgrInfo.getId());// 转入
		param.put("memo", "实名认证");
		custManagerService.saveCustTransferForSyncOldOffLineWealth(param);
		// 线上线下-同步状态变更
		customerService.changeWealthFlagById(custId, Constant.WEALTH_FLAG_01);
		return new ResultVo(true, "转移成功");
	}

	@Override
	public ResultVo queryOfflineInvestList(Map<String, Object> params) throws SLException {
		Map<String, Object> result = wealthInvestIntoRepositoryCustom.queryOfflineInvestList(params);
		return new ResultVo(true, "查询成功", result);
	}
}
