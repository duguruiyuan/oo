package com.slfinance.shanlincaifu.service;

import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;
/**
 * 
 * <法律法规相关>
 * <功能详细描述>
 * 
 * @author  yangcheng
 * @version  [版本号, 2017年6月14日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public interface LawsAndRegulationsService {
	/**
	 * 
	 * <生成新的法律法规>
	 * <功能详细描述>
	 *
	 * @param params
	 * @return [参数说明]
	 * @return ResultVo [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	public ResultVo createLawsAndRegulations(Map<String, Object> params)throws SLException;
	/**
	 * 
	 * <更新法律法规发布状态>
	 * <功能详细描述>
	 *
	 * @param param
	 * @return
	 * @throws SLException [参数说明]
	 * @return ResultVo [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	@Rules(rules = { 
			@Rule(name = "lawsAndRegulationsId", required = true, requiredMessage = "法律法规ID不能为空!")
	})
	public ResultVo updateReleaseStatus(Map<String, Object> params)throws SLException;
	/**
	 * 
	 * <查询所有法规列表>
	 * <功能详细描述>
	 *
	 * @param params
	 * @return [参数说明]
	 * @return Map<String,Object> [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	public Map<String, Object> findAllLawsAndRegulationsList(Map<String, Object> params);
	
	/**
	 * 
	 * <查询所有法规列表>
	 * <功能详细描述>
	 *
	 * @param params
	 * @return [参数说明]
	 * @return Map<String,Object> [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	public Map<String, Object> backFindAllLawsAndRegulationsList(Map<String, Object> params);

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
	
 }
