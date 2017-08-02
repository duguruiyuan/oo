package com.slfinance.shanlincaifu.repository.custom;

import java.util.List;
import java.util.Map;

import com.slfinance.shanlincaifu.entity.AutoInvestInfoEntity;
import com.slfinance.shanlincaifu.utils.SqlCondition;

/**   
 * 智能投顾数据访问接口
 *  
 * @author  guoyk
 * @version $Revision:1.0.0, $Date: 2017年06月16日 上午11:11:54 $ 
 */
public interface AutoInvestRepositoryCustom {

	/**
	 * 查询符合单标的用户
	 *
	 * @author  guoyk
	 * @date    2017年06月16日 上午11:11:54
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> queryAutoInvestInfoForSZD(Map<String, Object> param);
	
	/**
	 * 实时查询该用户是否符合标的信息
	 *
	 * @author  guoyk
	 * @date    2017年06月20日 下午15:22:54
	 * @param param
	 * @return
	 */
	public List<Map<String, Object>> queryAutoInvestInfoCurrent(Map<String, Object> param);
	
	public SqlCondition getAutoInvestInfoSql(Map<String, Object> param);

	public int updateAutoInvestInfoToAvoidDuplicate(String custId, boolean b);
}
