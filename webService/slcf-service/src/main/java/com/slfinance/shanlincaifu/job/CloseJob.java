/** 
 * @(#)CloseJob.java 1.0.0 2015年5月4日 下午4:41:27  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.job;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.slfinance.shanlincaifu.service.ProductService;
import com.slfinance.shanlincaifu.utils.Constant;

/**   
 * 关闭投标定时任务
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年5月4日 下午4:41:27 $ 
 */
@Component
public class CloseJob extends AbstractJob {

	@Autowired
	private ProductService productService;
	
	@Override
	public void execute() {
		Map<String, Object> param = new HashMap<String, Object>();
		try
		{
			// 定时关标
			productService.closeJob(param);
			// 恢复可提现额度
			productService.recoverAtoneLimited(param);
			// 定期宝关标
			productService.closeTermJob(param);
		}
		catch(Exception e) {
			logger.error("关闭异常：" + e.getMessage());
		}
	}

	@Override
	protected String getJobName() {
		return Constant.JOB_NAME_CLOSEJOB;
	}

}
