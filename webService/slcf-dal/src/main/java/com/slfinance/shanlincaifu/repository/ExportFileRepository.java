package com.slfinance.shanlincaifu.repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.slfinance.shanlincaifu.entity.ExportFileEntity;


/**   
 * 系统生成文件记录表数据访问接口
 *  
 * @author  Tool
 * @version $Revision:1.0.0, $Date: 2016-03-07 13:23:46 $ 
 */
public interface ExportFileRepository extends PagingAndSortingRepository<ExportFileEntity, String>{

	/**
	 * 通过客户ID、关联表类型、关联表主键查询
	 *
	 * @author  wangjf
	 * @date    2016年3月7日 下午3:25:28
	 * @param custId
	 * @param relateType
	 * @param relatePrimary
	 * @return
	 */
	@Query(" select A from ExportFileEntity A where custId = :custId and relateType = :relateType and relatePrimary = :relatePrimary and recordStatus = '有效' ")
	public ExportFileEntity findByCustIdAndRelateTypeAndRelatePrimary(@Param("custId")String custId, @Param("relateType")String relateType, @Param("relatePrimary")String relatePrimary);
	
	/**
	 * 通过关联表类型、关联表主键查询
	 *
	 * @author  wangjf
	 * @date    2016年12月9日 下午3:59:59
	 * @param relateType
	 * @param relatePrimary
	 * @return
	 */
	@Query(value=" select a.* from BAO_T_EXPORT_FILE a where a.relate_Type = ?1 and a.relate_Primary = ?2 and record_Status = '有效' AND ROWNUM = 1 ", nativeQuery=true)
	public ExportFileEntity findByRelateTypeAndRelatePrimary(String relateType, String relatePrimary);
}
