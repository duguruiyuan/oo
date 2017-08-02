/** 
 * @(#)UpdateAuditAndTradeFlowInfoServiceImpl.java 1.0.0 2015年4月30日 上午11:15:17  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */

package com.slfinance.shanlincaifu.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AuditInfoEntity;
import com.slfinance.shanlincaifu.entity.TradeFlowInfoEntity;
import com.slfinance.shanlincaifu.repository.AuditInfoRepository;
import com.slfinance.shanlincaifu.repository.TradeFlowInfoRepository;
import com.slfinance.shanlincaifu.service.WithDrawCheckService;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.vo.ResultVo;

/**
 *提现业务审核相关Service接口实现
 * 
 * @author zhangzs
 * @version $Revision:1.0.0, $Date: 2015年4月30日 上午11:15:17 $
 */
@Service
public class WithDrawCheckServiceImpl implements WithDrawCheckService {

	@Autowired
	private AuditInfoRepository auditInfoRepository;

	@Autowired
	private TradeFlowInfoRepository tradeFlowInfoRepository;
	
	/**
	 * 更新提现审核信息和交易流水信息
	 */
	@Override
	@Transactional(readOnly = false,propagation = Propagation.REQUIRES_NEW,rollbackFor=SLException.class  )
	public ResultVo updateWithdrawAndFlowProcess(String auditId, String flowId,
			String custId) throws SLException {
		// 更新审核信息
		AuditInfoEntity auditInfo = new AuditInfoEntity();
		auditInfo.setTradeStatus(Constant.TRADE_STATUS_02);
		auditInfo.setAuditStatus(Constant.AUDIT_STATUS_PASS);
		auditInfo.setMemo("提现调用TPP发生异常");
		auditInfo.setAuditUser(custId);
		auditInfo.setAuditTime(new Date());
		auditInfo.setBasicModelProperty(custId, false);
		AuditInfoEntity auditInfoUpd = auditInfoRepository.findOne(auditId);
		if (!auditInfoUpd.updateAuditInfo(auditInfo))
			throw new SLException("更新提现审核失败");

		// 更新流水信息
		TradeFlowInfoEntity flowInfo = new TradeFlowInfoEntity();
		flowInfo.setTradeStatus(Constant.TRADE_STATUS_02);
		flowInfo.setBasicModelProperty(custId, false);
		TradeFlowInfoEntity flowInfoUpd = tradeFlowInfoRepository.findOne(flowId);
		if (!flowInfoUpd.updateTradeFlowInfo(flowInfo))
			throw new SLException("更新流水信息失败");

		return new ResultVo(true);
	}

}
