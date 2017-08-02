package com.slfinance.shanlincaifu.job;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.slfinance.shanlincaifu.service.WaitingAuditSendMessageService;
import com.slfinance.shanlincaifu.utils.Constant;

/**
 * 线下充值、附属银行卡，有待审核的数据
 */
@Slf4j
@Component
public class WaitingAuditSendMessageJob extends AbstractJob {
	
	@Autowired
	WaitingAuditSendMessageService waitingAuditSendMessageService;
	
	@Override
	public void execute() {
		log.info("线下充值、附属银行卡，有待审核的数据，开始");
		// 线下充值、附属银行卡，有待审核的数据
		waitingAuditSendMessageService.waitingAuditSendMessage();
		log.info("线下充值、附属银行卡，有待审核的数据，结束");
		
		log.info("监控一个POS单线上线下同时使用，开始");
		// 监控一个POS单线上线下同时使用
		waitingAuditSendMessageService.monitorPosOnOnlineAndOffline();
		log.info("监控一个POS单线上线下同时使用，结束");
	}

	@Override
	protected String getJobName() {
		return Constant.JOB_NAME_RECHARGE_OFFLINE_UNREVIEW;
	}

}
