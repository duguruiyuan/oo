package com.slfinance.shanlincaifu.repository;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.WealthInvestInfoEntity;


/**   
 * 原线下理财投资信息表数据访问接口
 *  
 * @author  Tool
 * @version $Revision:1.0.0, $Date: 2016-06-29 09:29:09 $ 
 */
public interface WealthInvestInfoRepository extends PagingAndSortingRepository<WealthInvestInfoEntity, String>{

	/** 原线下数据-根据身份证查投资 */
	@Query(value=" SELECT * FROM BAO_T_WEALTH_INVEST_INFO WHERE CUSTOMER_ID = (SELECT ID FROM BAO_T_WEALTH_CUST_INTO WHERE CARD_ID=? ) "
			+ " AND FLOW_ID IN ('02000002', '02000005', '02000006', '02000007', '02000008', '02000009', '02000010', '02000011', '02000013', '02000014') "
			+ " ORDER BY CREATE_DATE desc ", nativeQuery=true )
	List<WealthInvestInfoEntity> findInvestInfoByCardId(String credentialsCode);

	
}
