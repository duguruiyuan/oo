/** 
 * @(#)ProjectInfoRepositoryCustom.java 1.0.0 2016年1月5日 下午1:39:28  
 *  
 * Copyright © 2016 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository.custom;

import java.util.Map;
import java.util.List;
import org.springframework.data.domain.Page;

import com.slfinance.vo.ResultVo;

/**   
 * 自定义项目数据访问接口
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2016年1月5日 下午1:39:28 $ 
 */
public interface ProjectInfoRepositoryCustom {

	/**
	 * 查询融资租赁列表
	 * @param params
	 * @return
	 */
	public Page<Map<String, Object>> queryAllProjectPage(Map<String, Object> params);
	
	/**
	 * 查询项目详细信息
	 * @param params
	 * @return
	 */
	public Map<String, Object> queryProjectDetail(Map<String, Object> params);
	
	/**
	 * 查询企业借款收益情况
	 * @param params
	 * @return
	 */
	public ResultVo findProjectIncomeByCustId(Map<String, Object> params);
	
	/**
	 * 直投列表--项目列表项目
	 * @param params
	 * @return
	 */
	public Page<Map<String, Object>> queryMyProjectPage(Map<String, Object> params);
	
	/**
	 * 查询项目
	 *
	 * @author  wangjf
	 * @date    2016年1月5日 下午1:51:10
	 * @param params
	 * @return
	 */
	public Page<Map<String, Object>> queryProjectList(Map<String, Object> params);
	
	/**
	 * 查询项目加入记录
	 *
	 * @author  wangjf
	 * @date    2016年1月5日 下午3:48:52
	 * @param params
	 * @return
	 */
	public Page<Map<String, Object>> queryProjectJoinList(Map<String, Object> params);
	
	/**
	 * 查询贴息数据
	 *
	 * @author  wangjf
	 * @date    2016年1月11日 下午6:03:53
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> queryWaitingSubsidyList(Map<String, Object> params);
	
	/**
	 * 查询合同所需信息
	 * @param params
	 * @return
	 */
	public ResultVo queryProjectContract(Map<String, Object> params);
	
	/**
	 * 查询个人投资项目详情
	 * 
	 * @author zhangt
	 * @date   2016年3月1日下午2:20:41
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> queryMyProjectDetail(Map<String, Object> params);
	
	/**
	 * 根据客户id和项目状态查询项目收益展示
	 * @author zhangt
	 * @date   2016年3月4日下午12:00:56
	 * @param params
	 * @return
	 */
	public Map<String, Object> queryProjectTotalIncome(Map<String, Object> params);
	
}
