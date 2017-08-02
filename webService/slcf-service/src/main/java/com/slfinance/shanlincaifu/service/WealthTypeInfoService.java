package com.slfinance.shanlincaifu.service;

import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;


/**   
 * 基础产品类型中心业务接口
 *  
 * @author  liyy
 * @version $Revision:1.0.0, $Date: 2016年02月23日 $ 
 */
public interface WealthTypeInfoService {

	/**
	 * 计划名称/出借方式查询
	 * @param param
     *      <tt>enableStatus:String:启用标识（有效）</tt><br>
     * @return ResultVo
     *      <tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
     *      <tt>data
     *      	<tt>wealthTypeId:String:计划类型主键</tt><br>
     *      	<tt>lendingType :String:出借方式</tt><br>
     *      </tt><br>
	 */
	public ResultVo findWealthType(Map<String, Object> param);
	
	/**
	 * 查询计划类型详情
	 * @param param
     *      <tt>wealthTypeId:String:计划类型主键</tt><br>
	 * @return ResultVo
	 * 		<tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
     *      <tt>data
     *      	<tt>wealthTypeId:String:计划类型主键</tt><br>
     *      	<tt>lendingType :String:出借方式</tt><br>
     *      	<tt>typeTerm    :String:项目期限</tt><br>
     *      	<tt>yearRate    :String:年化收益率</tt><br>
     *      	<tt>incomeType  :String:结算方式</tt><br>
     *      	<tt>enableStatus:String:状态</tt><br>
     *      	<tt>lendingNo   :String:项目期数</tt><br>
     *      </tt><br>
	 */
	public ResultVo findWealthTypeById(Map<String, Object> param);
	
	/**
	 * 基础产品-产品列表查询
	 * @param param
     *      <tt>start       :String:起始值</tt><br>
     *      <tt>length      :String:长度</tt><br>
     *      <tt>lendingType :String:出借方式（可选）</tt><br>
     *      <tt>typeTerm    :String:项目期限（可选）</tt><br>
     *      <tt>incomeType  :String:结算方式（可选）</tt><br>
     *      <tt>enableStatus:String:状态（可选）</tt><br>
	 * @return ResultVo
	 * 		<tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
     *      <tt>data
     *     		<tt>wealthTypeId:String:计划类型主键</tt><br>
     *     		<tt>lendingType :String:出借方式</tt><br>
     *      	<tt>typeTerm    :String:项目期限</tt><br>
     *      	<tt>yearRate    :String:年化收益率</tt><br>
     *      	<tt>incomeType  :String:结算方式</tt><br>
     *      	<tt>enableStatus:String:状态</tt><br>.
     *      	<tt>sort        :String:展示排序</tt><br> 
     *      </tt><br>
	 */
	public ResultVo queryWealthTypeList(Map<String, Object> param);
	
	/**
	 * 基础产品-产品查看查询
	 * @param param
     *      <tt>wealthTypeId:String:计划类型主键</tt><br>
	 * @return ResultVo
	 * 		<tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
     *      <tt>data
     *      	<tt>wealthTypeId:String:计划类型主键</tt><br>
     *      	<tt>lendingType :String:出借方式</tt><br>
     *      	<tt>typeTerm    :String:项目期限</tt><br>
     *      	<tt>yearRate    :String:年化收益率</tt><br>
     *      	<tt>incomeType  :String:结算方式</tt><br>
     *      	<tt>enableStatus:String:状态</tt><br>
     *      </tt><br>
	 */
	public ResultVo queryWealthTypeDetailById(Map<String, Object> param);
	
	/**
	 * 基础产品-新建/编辑 产品
	 * @param param
     *      <tt>wealthTypeId:String:计划类型主键(编辑时非空)</tt><br>
     *      <tt>lendingType :String:出借方式</tt><br>
     *      <tt>typeTerm    :String:项目期限</tt><br>
     *      <tt>yearRate    :String:年化收益率</tt><br>
     *      <tt>incomeType  :String:结算方式</tt><br>
     *      <tt>enableStatus:String:状态</tt><br>
     *      <tt>userId      :String:创建人</tt><br>
     *      <tt>sort        :String:展示排序</tt><br> 
	 * @return ResultVo
	 * 		<tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
	 * @throws SLException 
	 */
	@Rules(rules = {
			@Rule(name = "lendingType", required = true, requiredMessage = "计划名称不能为空") 
			, @Rule(name = "typeTerm", required = true, requiredMessage = "项目期限不能为空") 
			, @Rule(name = "yearRate", required = true, requiredMessage = "年化收益率不能为空") 
			, @Rule(name = "incomeType", required = true, requiredMessage = "结算方式不能为空") 
			, @Rule(name = "userId", required = true, requiredMessage = "创建人不能为空")
			, @Rule(name = "sort", required = true, requiredMessage = "展示排序不能为空") 
			, @Rule(name = "rebateRatio", required = true, requiredMessage = "折年系数不能为空")
	})
	public ResultVo saveWealthType(Map<String, Object> param) throws SLException;
	
	/**
	 * 基础产品-启用/停用产品
	 * @param param
     *      <tt>wealthTypeId:String:计划类型主键</tt><br>
     *      <tt>enableStatus:String:状态</tt><br>
     *      <tt>userId      :String:创建人</tt><br>
	 * @return ResultVo
	 * 		<tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
     * @throws SLException 
	 */
	@Rules(rules = {
			@Rule(name = "wealthTypeId", required = true, requiredMessage = "产品ID不能为空") 
	})
	public ResultVo enableWealthType(Map<String, Object> param) throws SLException;
	
	/**
	 * 匹配规则-规则列表
	 * @param param
     *      <tt>start :String:起始值</tt><br>
     *      <tt>length:String:长度</tt><br>
	 * @return ResultVo
	 * 		<tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
     *      <tt>data
     *      	<tt>matchRuleId  :String:规则主键ID</tt><br>
     *     		<tt>InvestMiniAmt:String:投资金额下限</tt><br>
     *      	<tt>InvestMaxAmt :String:投资金额上限</tt><br>
     *     		<tt>DebtMiniAmt  :String:债权金额下限</tt><br>
     *      	<tt>DebtMaxAmt   :String:债权金额上限</tt><br>
     *      </tt><br>
	 */
	public ResultVo queryMatchRuleList(Map<String, Object> param);
	
	/**
	 * 匹配规则-新增/编辑规则
	 * @param param
     *      <tt>matchRuleId  :String:规则主键ID(编辑时非空)</tt><br>
     *      <tt>InvestMiniAmt:String:投资金额下限</tt><br>
     *      <tt>InvestMaxAmt :String:投资金额上限</tt><br>
     *      <tt>DebtMiniAmt  :String:债权金额下限</tt><br>
     *      <tt>DebtMaxAmt   :String:债权金额上限</tt><br>
     *      <tt>userId       :String:创建人</tt><br>
	 * @return ResultVo
	 * 		<tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
     * @throws SLException
	 */
	public ResultVo saveMatchRule(Map<String, Object> param) throws SLException;
	

	/**
	 * 匹配规则-删除规则
	 * @param param
     *      <tt>matchRuleId  :String:规则主键ID(编辑时非空)</tt><br>
     *      <tt>userId       :String:创建人</tt><br>
	 * @return ResultVo
	 * 		<tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
     * @throws SLException
	 */
	public ResultVo deleteMatchRule(Map<String, Object> param) throws SLException;
}
