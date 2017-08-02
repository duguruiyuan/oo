package com.slfinance.shanlincaifu.repository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.ProjectInvestInfoEntity;
import com.slfinance.shanlincaifu.entity.RealNameInfoEntity;


/**   
 * 用户预约投资表数据访问接口
 *  
 * @author  Tool
 * @version $Revision:1.0.0, $Date: 2017-06-23 17:05:29 $ 
 */
public interface RealNameInfoRepository extends PagingAndSortingRepository<RealNameInfoEntity, String>{
	
	
	
	/**
	 *通过身份证，匹配状态查询
	 *
	 * @author  HuangXiaodong
	 * @date    2017年6月25日 13:29:17
	 * @param projectId
	 * @return
	 */
	RealNameInfoEntity findByCredentialsCodeAndTradeStatus(String credentialsCode,String tradeStatus);

}
