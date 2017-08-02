package com.slfinance.shanlincaifu.service;

import com.slfinance.exception.SLException;

/**
 * 业务数据汇总定时任务
 * @author zhangt
 *
 */
public interface SummarizationBusinessHistoryService {

	/**
	 * 业务数据汇总定时任务
	 * @throws SLException
	 */
	public void sumBusinessHistory()  throws SLException;

}
