/** 
 * @(#)AccountFlowService.java 1.0.0 2015年4月25日 下午6:17:31  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service;

import java.util.Map;

import org.springframework.data.domain.Page;

import com.slfinance.shanlincaifu.entity.TradeFlowInfoEntity;

/**   
 * 交易流水接口
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年4月25日 下午6:17:31 $ 
 */
public interface TradeFlowService {
	
	/**
	 * 充值流水列表
	 *
	 * @author  ricahrd
	 * @date    2015年4月28日 
	 * @param param
	        <tt>start：int:分页起始页</tt><br>
	  		<tt>length：int:每页长度</tt><br>
	  		<tt>custId：String:用户ID</tt><br>
	  		<tt>opearteDateBegin：String:操作开始时间</tt><br>
	  		<tt>opearteDateEnd：String:操作开始时间</tt><br>
	  		<tt>tradeType：String:交易类型</tt><br>
	 * @return
	 		Page<TradeFlowInfoEntity>
	 */
	Page<TradeFlowInfoEntity> findTradeFlowInfoPagable(Map<String, Object> param);
	

}
