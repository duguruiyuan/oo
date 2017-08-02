package com.slfinance.shanlincaifu.service;

import java.util.List;
import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;
/**
 * 
 * <运营报告相关>
 * <功能详细描述>
 * 
 * @author  yangcheng
 * @version  [版本号, 2017年6月14日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public interface OperationalReportService {
	/**
	 * 
	 * <新建报告>
	 * <功能详细描述>
	 *
	 * @param params
	 * @return [参数说明]
	 * @return ResultVo [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	@Rules(rules = { 
			@Rule(name = "reportName", required = true, requiredMessage = "报告名称不能为空!")
	})
	public ResultVo createReport(Map<String, Object> params)throws SLException;
	/**
	 * 
	 * <后台查询所有运营报告>
	 * <功能详细描述>
	 *
	 * @param param
	 * @return [参数说明]
	 * @return Map<String,Object> [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	public Map<String, Object> backFindAllReportList(Map<String, Object> params);
	/**
	 * 
	 * <更新运营报告发布状态>
	 * <功能详细描述>
	 *
	 * @param Map--param
	 * @return [参数说明]
	 * @return ResultVo [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	@Rules(rules = { 
			@Rule(name = "reportId", required = true, requiredMessage = "报告ID不能为空!")
	})
	public ResultVo updateReleaseStatus(Map<String, Object> params)throws SLException;
	/**
	 * 
	 * <前端查询所有运营报告>
	 * <功能详细描述>
	 *
	 * @param param
	 * @return [参数说明]
	 * @return ResultVo [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	public Map<String, Object> frontFindAllReportList(Map<String, Object> params);
	/**
	 * 
	 * <根据报告ID查询详细信息>
	 * <功能详细描述>
	 *
	 * @param param
	 * @return [参数说明]
	 * @return Map<String,Object> [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	@Rules(rules = { 
			@Rule(name = "reportId", required = true, requiredMessage = "报告ID不能为空!")
	})
	public Map<String, Object> findByReportId(Map<String, Object> params);
	/**
	 * 
	 * <查询所有报告年份>
	 * <功能详细描述>
	 *
	 * @param params
	 * @return [参数说明]
	 * @return List<Map<String,Object>> [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	public List<Map<String,Object>> findAllReportTime(Map<String, Object> params);
}
