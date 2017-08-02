package com.slfinance.shanlincaifu.repository;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.slfinance.shanlincaifu.entity.BusinessDeptInfoEntity;


/**   
 * 营业部组织结构表数据访问接口
 *  
 * @author  Tool
 * @version $Revision:1.0.0, $Date: 2017-04-19 18:56:13 $ 
 */
public interface BusinessDeptInfoRepository extends PagingAndSortingRepository<BusinessDeptInfoEntity, String>{

	@Query(value = "select distinct com.city_name \"cityName\" from bao_t_business_dept_info com where com.city_name is not null and com.province_name = ?", nativeQuery=true)
	List<Map<String,Object>> queryCityNameByProvinceName(String provinceName);

	@Query(value = "select distinct com.province_name \"provinceName\" from bao_t_business_dept_info com where com.province_name is not null", nativeQuery=true)
	List<Map<String,Object>> queryProvince();

	@Query(value = "select distinct com.dept_name \"deptName\" from bao_t_business_dept_info com where com.dept_name is not null and com.city_name = ?", nativeQuery=true)
	List<Map<String,Object>> queryDebtNameByCityNameProvinceName(String cityName);

}
