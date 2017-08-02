package com.slfinance.shanlincaifu.repository;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.LoanTransferEntity;


/**   
 * BAO债权转让表数据访问接口
 *  
 * @author  Tool
 * @version $Revision:1.0.0, $Date: 2016-02-23 09:44:53 $ 
 */
public interface LoanTransferRepository extends PagingAndSortingRepository<LoanTransferEntity, String>{

	/**
	 * 通过受让持有ID查询转让情况
	 *
	 * @author  wangjf
	 * @date    2017年1月4日 下午12:07:57
	 * @param receiveHoldId
	 * @return
	 */
	LoanTransferEntity findByReceiveHoldId(String receiveHoldId);

	@Query(value=" SELECT * FROM BAO_T_LOAN_TRANSFER lt, bao_t_wealth_hold_info wh, bao_t_invest_info invest WHERE invest.id = wh.invest_id and wh.id = lt.receive_hold_id  AND lt.SENDER_HOLD_ID = ?1 AND to_date(invest.effect_date, 'yyyyMMdd')>= ?2 AND to_date(invest.effect_date, 'yyyyMMdd')<= ?3 ", nativeQuery=true)
	List<LoanTransferEntity> findBySenderHoldIdAndCreateDate(String id,
			Date lastExpiry, Date nextExpiry);
}
