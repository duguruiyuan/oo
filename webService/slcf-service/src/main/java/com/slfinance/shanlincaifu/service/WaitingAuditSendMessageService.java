package com.slfinance.shanlincaifu.service;

public interface WaitingAuditSendMessageService {

	/**
	 * 线下充值、附属银行卡，有待审核的数据
	 */
	void waitingAuditSendMessage();
	
	/**
	 * 监控一个POS单线上线下同时使用
	 */
	void monitorPosOnOnlineAndOffline();
}
