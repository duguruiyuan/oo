package com.slfinance.shanlincaifu.repository;
import org.springframework.data.repository.PagingAndSortingRepository;
import com.slfinance.shanlincaifu.entity.RepayRecordDetailInfoEntity;


/**   
 * BAO还款记录详情信息表数据访问接口
 *  
 * @author  Tool
 * @version $Revision:1.0.0, $Date: 2016-01-14 10:37:31 $ 
 */
public interface RepayRecordDetailInfoRepository extends PagingAndSortingRepository<RepayRecordDetailInfoEntity, String>{

}
