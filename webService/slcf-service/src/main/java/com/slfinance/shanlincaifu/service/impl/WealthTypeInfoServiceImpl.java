package com.slfinance.shanlincaifu.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AutoMatchRuleEntity;
import com.slfinance.shanlincaifu.entity.WealthTypeInfoEntity;
import com.slfinance.shanlincaifu.repository.AutoMatchRuleRepository;
import com.slfinance.shanlincaifu.repository.WealthTypeInfoRepository;
import com.slfinance.shanlincaifu.repository.custom.WealthTypeInfoRepositoryCustom;
import com.slfinance.shanlincaifu.service.WealthTypeInfoService;
import com.slfinance.shanlincaifu.utils.ArithUtil;
import com.slfinance.shanlincaifu.utils.Constant;
import com.slfinance.shanlincaifu.utils.SharedUtil;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;

/**   
 * 基础产品类型中心业务接口实现
 *  
 * @author  liyy
 * @version $Revision:1.0.0, $Date: 2016年02月23日 $ 
 */
@Slf4j
@Service("wealthTypeInfoService")
public class WealthTypeInfoServiceImpl implements WealthTypeInfoService{

	@Autowired
	WealthTypeInfoRepository wealthTypeInfoRepository;
	
	@Autowired
	WealthTypeInfoRepositoryCustom wealthTypeInfoRepositoryCustom;
	
	@Autowired
	AutoMatchRuleRepository autoMatchRuleRepository;
	
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
	public ResultVo findWealthType(Map<String, Object> param) {
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			List<Map<String, Object>> list = wealthTypeInfoRepositoryCustom.findWealthType(param);
			data.put("data", list);
			return new ResultVo(true, "基础产品类型查询成功", data);
		} catch (Exception e) {
			log.error("基础产品类型查询失败");
			return new ResultVo(false, "基础产品类型查询失败");
		}
		
	}
	
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
	public ResultVo findWealthTypeById(Map<String, Object> param) {
		try {
			List<Map<String, Object>> list = wealthTypeInfoRepositoryCustom.findWealthTypeById(param);
			if(list == null || list.size() == 0){
				return new ResultVo(false, "查询计划类型详情查询失败");
			}
			return new ResultVo(true, "查询计划类型详情查询成功", list.get(0));
		} catch (Exception e) {
			log.error("查询计划类型详情查询失败");
			return new ResultVo(false, "查询计划类型详情查询失败");
		}
		
	}
	
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
     *      	<tt>enableStatus:String:状态</tt><br>
     *      	<tt>sort        :String:展示排序</tt><br> 
     *      </tt><br>
	 */
	public ResultVo queryWealthTypeList(Map<String, Object> param){
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			Page<Map<String, Object>> page = wealthTypeInfoRepositoryCustom.queryWealthTypeList(param);
			
			data.put("iTotalDisplayRecords", page.getTotalElements());
			data.put("data", page.getContent());
			return new ResultVo(true, "产品列表查询成功", data);
		} catch (Exception e) {
			log.error("产品列表查询失败");
			return new ResultVo(false, "产品列表查询失败");
		}
	}
	
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
	public ResultVo queryWealthTypeDetailById(Map<String, Object> param) {
		try {
			List<Map<String, Object>> list = wealthTypeInfoRepositoryCustom.queryWealthTypeDetailById(param);
			if(list == null || list.size() == 0){
				return new ResultVo(false, "产品查看查询失败");
			}
			return new ResultVo(true, "产品查看查询成功", list.get(0));
		} catch (Exception e) {
			log.error("产品查看查询失败");
			return new ResultVo(false, "产品查看查询失败");
		}
	}
	
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
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo saveWealthType(Map<String, Object> param) throws SLException {
		// 校验数据
		int flag = checkParam(param);
		if(flag == 1){
			log.error("计划名称已存在");
			throw new SLException("计划名称已存在");
		} else if(flag == 2){
			log.error("展示级别已存在");
			throw new SLException("展示级别已存在");
		}

		String wealthTypeId = (String) param.get("wealthTypeId");
		String lendingType = (String) param.get("lendingType");
		String typeTerm = (String) param.get("typeTerm");
		String yearRate = (String) param.get("yearRate");
		String incomeType = (String) param.get("incomeType");
		String enableStatus = (String) param.get("enableStatus");
		String userId = (String) param.get("userId");
		Integer sort = Integer.parseInt(param.get("sort").toString());
		BigDecimal rebateRatio = new BigDecimal(param.get("rebateRatio").toString()); //折年系数
		
		WealthTypeInfoEntity wealthTypeInfoEntity;
		if(!StringUtils.isEmpty(wealthTypeId)){
			if(StringUtils.isEmpty(enableStatus)){
				log.error("取得最新productNo失败");
				throw new SLException("取得最新productNo失败");
			}
			//编辑
			wealthTypeInfoEntity = wealthTypeInfoRepository.findOne(wealthTypeId);
			wealthTypeInfoEntity.setLendingType(lendingType);
			wealthTypeInfoEntity.setTypeTerm(Integer.valueOf(typeTerm));
			wealthTypeInfoEntity.setYearRate(ArithUtil.div(new BigDecimal(yearRate), new BigDecimal(100), 4));// 
			wealthTypeInfoEntity.setIncomeType(incomeType);
			wealthTypeInfoEntity.setEnableStatus(enableStatus);
			wealthTypeInfoEntity.setSort(sort);
			wealthTypeInfoEntity.setRebateRatio(ArithUtil.div(rebateRatio, new BigDecimal(100), 4));
			wealthTypeInfoEntity.setBasicModelProperty(userId, false);
		} else {
			//新建
			// 取得最新productNo
			List<Map<String, Object>> pnList = wealthTypeInfoRepositoryCustom.getWealthTypeNewProductNo();// 除以100
			if(pnList ==null || pnList.size() == 0){
				log.error("取得最新productNo失败");
				throw new SLException("取得最新productNo失败");
			}
			wealthTypeInfoEntity = new WealthTypeInfoEntity();
			wealthTypeInfoEntity.setProductNo((String) pnList.get(0).get("productNo"));
			wealthTypeInfoEntity.setLendingType(lendingType);
			wealthTypeInfoEntity.setTypeTerm(Integer.valueOf(typeTerm));
			wealthTypeInfoEntity.setYearRate(ArithUtil.div(new BigDecimal(yearRate), new BigDecimal(100), 4));// 除以100
			wealthTypeInfoEntity.setIncomeType(incomeType);
			wealthTypeInfoEntity.setEnableStatus(Constant.VALID_STATUS_VALID);// 新建时状态没传
			wealthTypeInfoEntity.setSort(sort);
			wealthTypeInfoEntity.setRebateRatio( ArithUtil.div(rebateRatio, new BigDecimal(100), 4));
			wealthTypeInfoEntity.setBasicModelProperty(userId, true);
			wealthTypeInfoRepository.save(wealthTypeInfoEntity);
		}
		return new ResultVo(true, "基础产品数据操作成功");
	}
	
	/** 校验数据 */
	private int checkParam(Map<String, Object> params) {
		// 校验计划名称
		if(wealthTypeInfoRepositoryCustom.checkParam((String)params.get("wealthTypeId"), "LENDING_TYPE", params.get("lendingType")) > 0){
			return 1;
		}
		
		// 校验展示级别
		if(wealthTypeInfoRepositoryCustom.checkParam((String)params.get("wealthTypeId"), "SORT", Integer.parseInt(params.get("sort").toString())) > 0){
			return 2;
		}
		return 0;
	}
	
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
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo enableWealthType(Map<String, Object> param) throws SLException {
		
		String wealthTypeId = (String) param.get("wealthTypeId");
		String enableStatus = (String) param.get("enableStatus");
		String userId = (String) param.get("userId");
		
		WealthTypeInfoEntity wealthTypeInfoEntity = wealthTypeInfoRepository.findOne(wealthTypeId);
		wealthTypeInfoEntity.setEnableStatus(enableStatus);
		wealthTypeInfoEntity.setRecordStatus(enableStatus);
		wealthTypeInfoEntity.setBasicModelProperty(userId, false);
		
		return new ResultVo(true, "产品"+enableStatus+"成功");
	}
	
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
	public ResultVo queryMatchRuleList(Map<String, Object> param) {
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			List<Map<String, Object>> list = wealthTypeInfoRepositoryCustom.queryMatchRuleList(param);
			
			data.put("data", list);
			return new ResultVo(true, "规则列表查询成功", data);
		} catch (Exception e) {
			log.error("规则列表查询失败");
			return new ResultVo(false, "规则列表查询失败");
		}
	}
	
	/**
	 * 匹配规则-新增/编辑规则
	 * @param param :Map
	 * 		<tt>userId       :String:创建人</tt><br>
	 * 		<tt>matchRuleList:List<Map>
     *      	<tt>matchRuleId  :String:规则主键ID(编辑时非空)</tt><br>
     *      	<tt>InvestMiniAmt:String:投资金额下限</tt><br>
     *      	<tt>InvestMaxAmt :String:投资金额上限</tt><br>
     *      	<tt>DebtMiniAmt  :String:债权金额下限</tt><br>
     *      	<tt>DebtMaxAmt   :String:债权金额上限</tt><br>
     *      </tt><br>
	 * @return ResultVo
	 * 		<tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
     * @throws SLException
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo saveMatchRule(Map<String, Object> param) throws SLException{
		List<Map<String, Object>> matchRuleList = (List<Map<String, Object>>) param.get("matchRuleList");
		String userId = (String) param.get("userId");
		
		for (int i = 0; i < matchRuleList.size(); i++) {
			Map<String, Object> matchRuleMap = matchRuleList.get(i);
			
			String matchRuleId = (String) matchRuleMap.get("matchRuleId");
			String investMiniAmt = matchRuleMap.get("investMiniAmt").toString();
			String investMaxAmt = matchRuleMap.get("investMaxAmt").toString();
			String debtMiniAmt = matchRuleMap.get("debtMiniAmt").toString();
			String debtMaxAmt = matchRuleMap.get("debtMaxAmt").toString();
			
			AutoMatchRuleEntity autoMatchRuleEntity;
			if(!StringUtils.isEmpty(matchRuleId)){
				//编辑
				autoMatchRuleEntity = autoMatchRuleRepository.findOne(matchRuleId);
				autoMatchRuleEntity.setInvestMiniAmt(new BigDecimal(investMiniAmt));
				autoMatchRuleEntity.setInvestMaxAmt(new BigDecimal(investMaxAmt));
				autoMatchRuleEntity.setDebtMiniAmt(new BigDecimal(debtMiniAmt));
				autoMatchRuleEntity.setDebtMaxAmt(new BigDecimal(debtMaxAmt));
				autoMatchRuleEntity.setBasicModelProperty(userId, false);
			} else {
				//新建
				autoMatchRuleEntity = new AutoMatchRuleEntity();
				autoMatchRuleEntity.setId(SharedUtil.getUniqueString());
				autoMatchRuleEntity.setInvestMiniAmt(new BigDecimal(investMiniAmt));
				autoMatchRuleEntity.setInvestMaxAmt(new BigDecimal(investMaxAmt));
				autoMatchRuleEntity.setDebtMiniAmt(new BigDecimal(debtMiniAmt));
				autoMatchRuleEntity.setDebtMaxAmt(new BigDecimal(debtMaxAmt));
				autoMatchRuleEntity.setBasicModelProperty(userId, true);
				autoMatchRuleRepository.save(autoMatchRuleEntity);
			}
		}
		return new ResultVo(true, "新增/编辑规则操作成功");
	}
	
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
	@Rules(rules = {
			@Rule(name = "matchRuleId", required = true, requiredMessage = "主键ID不能为空") 
			})
	@Transactional(readOnly = false, rollbackFor = SLException.class)
	public ResultVo deleteMatchRule(Map<String, Object> param) throws SLException{
		String matchRuleId = (String) param.get("matchRuleId");
//		String userId = (String) param.get("userId");
		
		autoMatchRuleRepository.delete(matchRuleId);
		
		return new ResultVo(true, "删除规则操作成功");
	}
}
