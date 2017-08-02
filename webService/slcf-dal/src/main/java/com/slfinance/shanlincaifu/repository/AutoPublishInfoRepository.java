package com.slfinance.shanlincaifu.repository;
import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.AutoPublishInfoEntity;


/**   
 * 自动发布规则表数据访问接口
 *  
 * @author  Tool
 * @version $Revision:1.0.0, $Date: 2017-04-11 13:25:43 $ 
 */
public interface AutoPublishInfoRepository extends PagingAndSortingRepository<AutoPublishInfoEntity, String>{
	@Query(value = "select * from  bao_t_auto_publish_info", nativeQuery=true)
	public List<AutoPublishInfoEntity> findAllAutoPublishInfo();
	
	@Query(value = "select nvl(sum(loan.loan_amount),0) from bao_t_loan_info loan where loan.loan_status = '募集中' and loan.newer_flag != '新手标' ", nativeQuery=true)
	public  BigDecimal findTotalLoanAmount();
	
}
