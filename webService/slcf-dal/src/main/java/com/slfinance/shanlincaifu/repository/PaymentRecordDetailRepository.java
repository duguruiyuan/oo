package com.slfinance.shanlincaifu.repository;
import org.springframework.data.repository.PagingAndSortingRepository;
import com.slfinance.shanlincaifu.entity.PaymentRecordDetailEntity;


/**   
 * P2P付款记录详情信息表数据访问接口
 *  
 * @author  Tool
 * @version $Revision:1.0.0, $Date: 2016-01-14 17:34:20 $ 
 */
public interface PaymentRecordDetailRepository extends PagingAndSortingRepository<PaymentRecordDetailEntity, String>{

}
