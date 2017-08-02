package com.slfinance.shanlincaifu.repository.custom;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.slfinance.exception.SLException;

/**
 * 营业额自定义数据访问接口 
 * @author  guoyk
 * @version $Revision:1.0.0, $Date: 2017年4月18日 下午19:18:46 $ 
 */
public interface TurnoverInfoRepositoryCustom {
	/***
	 * 营业额详情列表查询
	 * @author  guoyk
	 */
	public Page<Map<String, Object>> queryTurnoverInfoList(Map<String, Object> params);
	
	/***
	 * 营业额主页列表查询
	 * @author  guoyk
	 */
	public Page<Map<String, Object>> queryTurnoverList(Map<String, Object> params);
	
	/**
	 * 查询营业额金额汇总
	 * @author  guoyk
	 */
	public BigDecimal queryTurnoverTotalAmount(Map<String, Object> param);
	/**
	 * 查询营业额金额汇总
	 * @author  guoyk
	 */
	public BigDecimal queryTurnoverTotalAmountList(Map<String, Object> param);
	
	/***
	 * 营业额数据更新
	 * @author  guoyk
	 */
	public Integer updateTurnover(Map<String, Object> param)throws SLException;
	
	/***
	 * 营业额导入重复数据删除
	 * @author  guoyk
	 */
	public void deleteRepeatTurnover();
	
	/***
	 * 营业额导入记录查询
	 * @author  guoyk
	 */
	public List<Map<String, Object>> queryImportRecord();
	
}
