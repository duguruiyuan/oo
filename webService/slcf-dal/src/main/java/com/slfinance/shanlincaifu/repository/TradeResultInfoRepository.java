package com.slfinance.shanlincaifu.repository;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.slfinance.shanlincaifu.entity.TradeResultInfoEntity;


/**   
 * 线下交易处理结果表数据访问接口
 *  
 * @author  Tool
 * @version $Revision:1.0.0, $Date: 2016-02-29 13:30:24 $ 
 */
public interface TradeResultInfoRepository extends PagingAndSortingRepository<TradeResultInfoEntity, String>{
	
	/**
	 * 按照文件名查询
	 * 
	 * @author zhangt
	 * @date   2016年2月29日下午5:26:43
	 * @param fileName
	 * @return
	 */
	public List<TradeResultInfoEntity> findByFileName(String fileName);
	
	@Query("select new Map (t.transferType as transferType, t.payeeBankCardNo as payeeBankCardNo, t.payeyCustName as payeyCustName,"
			+ " t.payeyBankCardNo as payeyBankCardNo, t.currencyType as currencyType, t.tradeAmount as tradeAmount, to_char(t.fileTradeDate, 'yyyy-MM-dd HH:mi:ss') as fileTradeDate,"
			+ " t.fileTradeStatus as fileTradeStatus, to_char(t.tradeDate, 'yyyy-MM-dd HH:mi:ss') as tradeDate, t.tradeStatus as tradeStatus, t.memo as memo) from TradeResultInfoEntity t where t.fileName = :fileName")
	public List<Map<String, Object>> findMapByFileName(@Param("fileName") String fileName);

}
