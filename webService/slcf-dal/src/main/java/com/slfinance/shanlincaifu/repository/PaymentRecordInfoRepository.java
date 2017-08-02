package com.slfinance.shanlincaifu.repository;
import java.math.BigDecimal;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.PaymentRecordInfoEntity;


/**   
 * BAO付款记录表数据访问接口
 *  
 * @author  Tool
 * @version $Revision:1.0.0, $Date: 2016-01-14 17:34:20 $ 
 */
public interface PaymentRecordInfoRepository extends PagingAndSortingRepository<PaymentRecordInfoEntity, String>{
	
	/**
	 * 查询企业借款已获收益
	 * @author zhangt
	 * @param custId
	 * @return
	 */
	@Query(value = "select nvl(sum(trunc(b.trade_amount, 2)), 0) from bao_t_payment_record_info a, bao_t_payment_record_detail b where a.id = b.pay_record_id and a.cust_id = ?1 and b.subject_type not in ('本金', '风险金垫付本金') and a.loan_id is null ", nativeQuery = true)
	public BigDecimal queryReceivedAmountByCustId(String custId);

}
