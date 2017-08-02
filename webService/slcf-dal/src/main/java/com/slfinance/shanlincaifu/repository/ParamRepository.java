/** 
 * @(#)ParamRepository.java 1.0.0 2015年5月1日 下午2:46:07  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.ParamEntity;

/**   
 * 参数数据访问接口
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年5月1日 下午2:46:07 $ 
 */
public interface ParamRepository extends PagingAndSortingRepository<ParamEntity, String>{
	
	/**
	 * 通过类型名称和参数名称查询参数信息
	 *
	 * @author  wangjf
	 * @date    2015年5月1日 下午2:49:54
	 * @param typeName
	 * @param parameterName
	 * @return
	 */
	public ParamEntity findByTypeNameAndParameterName(String typeName, String parameterName);
	
	/**
	 */
	public List<ParamEntity> findListByTypeAndParameterName(String typeName, String parameterName);
	
	/**
	 * 通过类型查询参数信息
	 *
	 * @author  caoyi
	 * @date    2015年5月16日 下午1:49:54
	 * @param type
	 * @return
	 */
	public List<ParamEntity> findByType(String type);
	
	/**
	 * 通过父ID查询参数信息 
	 *
	 * @author  wangjf
	 * @date    2015年10月22日 下午4:21:06
	 * @param parentId
	 * @return
	 */
	public List<ParamEntity> findByParentId(String parentId);
	
	/**
	 * 通过ID查询下级机构
	 *
	 * @author  wangjf
	 * @date    2015年10月22日 下午4:28:16
	 * @param id
	 * @return
	 */
	@Query(value = "select * from com_t_param where status = '1' start with id = ?1 connect by prior id = parent_id", nativeQuery=true)
	public List<ParamEntity> findLowerParamById(String id);
	
	/**
	 * 通过父ID查询下级机构
	 *
	 * @author  wangjf
	 * @date    2015年10月22日 下午4:51:07
	 * @param id
	 * @return
	 */
	@Query(value = "select * from com_t_param where status = '1' start with parent_id = ?1 connect by prior id = parent_id", nativeQuery=true)
	public List<ParamEntity> findLowerParamByParentId(String id);
	
	/**
	 * 根据type和value查询参数
	 *
	 * @author  wangjf
	 * @date    2015年10月22日 下午5:05:25
	 * @param type
	 * @param value
	 * @return
	 */
	public ParamEntity findByTypeAndValue(String type, String value);
	
	/**
	 * 通过类型查询参数信息
	 *
	 * @author  caoyi
	 * @date    2015年5月16日 下午1:49:54
	 * @param type
	 * @return
	 */
	public List<ParamEntity> findByTypeAndStatusOrderBySortAsc(String type, String status);
	
	/**
	 * 通过类型查询参数信息
	 *
	 * @author  wangjf
	 * @date    2015年10月26日 下午5:00:24
	 * @param parentId
	 * @param status
	 * @return
	 */
	public List<ParamEntity> findByParentIdAndStatusOrderBySortAsc(String parentId, String status);
	
	/**
	 * 通过类型和状态统计并按照备注和参数名称正序排序
	 * 
	 * @param type
	 * @param status
	 * @return
	 */
	public List<ParamEntity> findByTypeAndStatusOrderByMemoAscParameterNameAsc(String type, String status);
}
