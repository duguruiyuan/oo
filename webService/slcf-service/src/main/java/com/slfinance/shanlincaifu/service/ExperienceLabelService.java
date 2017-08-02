package com.slfinance.shanlincaifu.service;


import java.util.Map;

import com.slfinance.vo.ResultVo;

/**
 * 体验标接口
 * <一句话功能简述>
 * <功能详细描述>
 * 
 * @author  Mgp
 * @version  [版本号, 2017年6月29日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public interface ExperienceLabelService{
	/**体验标的数据*/
	Map<String, Object> querySubject(Map<String, Object> params) throws Exception;
	/**新增体验标*/
	public ResultVo insertLabel(Map<String, Object> params) throws Exception;
	/**查询详情*/
	public Map<String, Object> findByLabel(Map<String,Object> params) throws Exception;
	/**更新数据*/
	public ResultVo updateLabel(Map<String,Object> params) throws Exception;
	/**启用标的*/
	public ResultVo enableLabel(Map<String,Object> params);
}
