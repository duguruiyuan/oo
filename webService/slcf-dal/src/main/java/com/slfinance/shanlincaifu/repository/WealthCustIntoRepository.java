package com.slfinance.shanlincaifu.repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.WealthCustIntoEntity;


/**   
 * 原线下理财客户信息表数据访问接口
 *  
 * @author  Tool
 * @version $Revision:1.0.0, $Date: 2016-06-29 09:29:09 $ 
 */
public interface WealthCustIntoRepository extends PagingAndSortingRepository<WealthCustIntoEntity, String>{

	@Query(value=" SELECT * FROM BAO_T_WEALTH_CUST_INTO WHERE CARD_ID = ? ", nativeQuery=true)
	WealthCustIntoEntity findCustInfoByCardId(String credentialsCode);

}
