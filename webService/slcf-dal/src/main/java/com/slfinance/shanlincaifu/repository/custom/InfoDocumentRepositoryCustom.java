package com.slfinance.shanlincaifu.repository.custom;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
/**
 * 
 * <自定义信息披露数据访问接口>
 * <功能详细描述>
 * 
 * @author  yangcheng
 * @version  [版本号, 2017年6月19日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public interface InfoDocumentRepositoryCustom {
	/**
	 * 
	 * <后台查询所有运营报告>
	 * <功能详细描述>
	 *
	 * @param param
	 * @return [参数说明]
	 * @return Page<Map<String,Object>> [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	public Page<Map<String, Object>> backFindAllReportList(Map<String, Object> params);
	/**
	 * 
	 * <前端查询所有运营报告>
	 * <功能详细描述>
	 *
	 * @param param
	 * @return [参数说明]
	 * @return Map<String,Object> [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	public Page<Map<String, Object>> frontFindAllReportList(Map<String, Object> params);
	/**
	 * 
	 * <根据报告id查询详细信息>
	 * <功能详细描述>
	 *
	 * @param param
	 * @return [参数说明]
	 * @return Map<String,Object> [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	public Map<String, Object> findByReportId(Map<String, Object> params);
	/**
	 * 
	 * <查询所有法律法规列表>
	 * <功能详细描述>
	 *
	 * @param params
	 * @return [参数说明]
	 * @return Page<Map<String,Object>> [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	public Page<Map<String, Object>> findAllLawsAndRegulationsList(Map<String, Object> params);
	/**
	 * 
	 * <查询所有法律法规列表>
	 * <功能详细描述>
	 *
	 * @param params
	 * @return [参数说明]
	 * @return Page<Map<String,Object>> [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	public Page<Map<String, Object>> backFindAllLawsAndRegulationsList(Map<String, Object> params);
	/**
	 * 
	 * <根据法规ID查询详细信息>
	 * <功能详细描述>
	 *
	 * @param params
	 * @return [参数说明]
	 * @return Map<String,Object> [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	public Map<String, Object> findBylawsAndRegulationsId(Map<String, Object> params);
	/**
	 * 
	 * <查询所有报告年份>
	 * <功能详细描述>
	 *
	 * @param params
	 * @return [参数说明]
	 * @return Map<String,Object> [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	public List<Map<String, Object>> findAllReportTime(Map<String, Object> params);
}
