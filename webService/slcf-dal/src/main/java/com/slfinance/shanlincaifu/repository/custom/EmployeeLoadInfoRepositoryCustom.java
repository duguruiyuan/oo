package com.slfinance.shanlincaifu.repository.custom;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.slfinance.shanlincaifu.entity.EmployeeLoadInfoEntity;
import com.slfinance.vo.ResultVo;

public interface EmployeeLoadInfoRepositoryCustom {
	
	/**
	 * 查询所有导入列表 
	 */
	public Page<Map<String, Object>> queryAllImportEmployeeLoadInfo(Map<String, Object> params);
	
	/**
	 * 查询所有的导入原始数据
	 * 
	 * @author zhiwen_feng
	 * @return
	 */
	public List<Map<String, Object>> queryAllEmployeeLoadInfo();
	
	/**
	 * 分页查询
	 * 
	 * @author zhiwen_feng
	 * @date 2016-04-15
	 * @param params
	 * @return
	 */
	public Page<Map<String, Object>> queryAllEmployeeLoadInfo(Map<String, Object> params);
	
	/**
	 * 处理原始数据
	 * 
	 * @author zhiwen_feng
	 * @serialData 2016-04-18
	 * @return
	 */
	public ResultVo handleOriginalData();
	
	/**
	 * 批量插入原始数据
	 * 
	 * @author zhiwen_feng
	 * @date 2016-04-28
	 * @param list
	 * @return
	 */
	public ResultVo batchImportDate(List<EmployeeLoadInfoEntity> list, String importBatchNo, String userId);

}
