package com.slfinance.shanlincaifu.repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.BusinessImportInfoEntity;


/**   
 * 营业部组织导入表数据访问接口
 *  
 * @author  Tool
 * @version $Revision:1.0.0, $Date: 2017-04-19 22:14:02 $ 
 */
public interface BusinessImportInfoRepository extends PagingAndSortingRepository<BusinessImportInfoEntity, String>{

	@Query("select A from BusinessImportInfoEntity A where A.createDate = (select max(B.createDate) from BusinessImportInfoEntity B where B.importStatus = '成功') and A.importStatus = '成功'")
	BusinessImportInfoEntity findByMaxCreateDate();
}
