package com.slfinance.shanlincaifu.repository.custom;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.slfinance.exception.SLException;

/**   
 * 基础产品类型数据访问接口
 *  
 * @author  liyy
 * @version $Revision:1.0.0, $Date: 2016年02月23日 $ 
 */
public interface WealthTypeInfoRepositoryCustom {

	/**
	 * 计划名称/出借方式查询
	 * @param param
     *      <tt>enableStatus:String:启用标识（有效）</tt><br>
     * @return List
	 * @throws SLException 
	 */
	List<Map<String, Object>> findWealthType(Map<String, Object> param) throws SLException;

	/**
	 * 查询计划类型详情
	 * @param param
     *      <tt>wealthTypeId:String:计划类型主键</tt><br>
	 * @return List
	 * @throws SLException 
	 */
	List<Map<String, Object>> findWealthTypeById(Map<String, Object> param) throws SLException;

	/**
	 * 基础产品-产品列表查询
	 * @param param
     *      <tt>start       :String:起始值</tt><br>
     *      <tt>length      :String:长度</tt><br>
     *      <tt>lendingType :String:出借方式（可选）</tt><br>
     *      <tt>typeTerm    :String:项目期限（可选）</tt><br>
     *      <tt>incomeType  :String:结算方式（可选）</tt><br>
     *      <tt>enableStatus:String:状态（可选）</tt><br>
	 * @return Page
	 * @throws SLException 
	 */
	Page<Map<String, Object>> queryWealthTypeList(Map<String, Object> param) throws SLException;
	
	/**
	 * 取得最新productNo
	 * @return List productNo
	 * @throws SLException 
	 */
	List<Map<String, Object>> getWealthTypeNewProductNo() throws SLException;

	/**
	 * 基础产品-产品查看查询
	 * @param param
     *      <tt>wealthTypeId:String:计划类型主键</tt><br>
	 * @return Map
	 * @throws SLException 
	 */
	List<Map<String, Object>> queryWealthTypeDetailById(Map<String, Object> param) throws SLException;

	/**
	 * 匹配规则-规则列表
	 * @param param
     *      <tt>start :String:起始值</tt><br>
     *      <tt>length:String:长度</tt><br>
	 * @return list
     * @throws SLException 
	 */
	List<Map<String, Object>> queryMatchRuleList(Map<String, Object> param)throws SLException;

	
	int checkParam(String wealthTypeId ,String key, Object condision);

}
