package com.slfinance.shanlincaifu.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.TradeLogInfoEntity;

/**   
 * 报文日志信息数据访问接口
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年4月23日 下午8:18:00 $ 
 */
public interface TradeLogInfoRepository extends PagingAndSortingRepository<TradeLogInfoEntity, String>{

	TradeLogInfoEntity findByTradeCode(String tradeCode);
}
