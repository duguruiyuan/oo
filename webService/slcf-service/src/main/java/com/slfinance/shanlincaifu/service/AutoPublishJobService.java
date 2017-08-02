package com.slfinance.shanlincaifu.service;

import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.vo.ResultVo;

/**
 * @Desc 自动发布JOB帮助类
 * @author guoyk
 * @date 2017/04/11 19:59:30
 */
public interface AutoPublishJobService {

	/**
	 * 自动发布-定时任务
	 */
    ResultVo autoPublish(Map<String, Object> params) throws SLException;
}
