/** 
 * @(#)CommissionInfoRepositoryCustom.java 1.0.0 2015年8月24日 下午7:17:11  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.repository.custom;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;

import com.slfinance.shanlincaifu.entity.CommissionInfoEntity;
import com.slfinance.vo.ResultVo;

/**   
 * 自定义提成数据访问层
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年8月24日 下午7:17:11 $ 
 */
public interface CommissionInfoRepositoryCustom {

	/**
	 * 批量插入
	 *
	 * @author  wangjf
	 * @date    2015年8月24日 下午7:18:36
	 * @param now
	 */
	public void batchInsert(CommissionInfoEntity commissionInfoEntity);
	
	/**
	 * 查询待转账数据
	 *
	 * @author  wangjf
	 * @date    2015年8月25日 上午9:52:07
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> findTransfer(Map<String, Object> params);
	
	/**
	 * 批量插入（定期宝推广奖励）
	 *
	 * @author  wangjf
	 * @date    2015年10月12日 上午10:41:32
	 * @param commissionInfoEntity
	 */
	public void batchTermInsert(CommissionInfoEntity commissionInfoEntity);
	
	/**
	 * 分页查询提成信息
	 * @param custId
	 * @param nowDate
	 * @return
	 */
	Map<String, Object> findCommissInfoPage(String custId,Date nowDate,String typeName,String startDate,String endDate,int pageNum,int pageSize);

	/**
	 * 分页查询提成信息详情
	 * @param custId
	 * @param nowDate
	 * @return
	 */
	Map<String, Object> findCommissDetailPage(String id ,String custId,Date nowDate,String typeName,int pageNum,int pageSize);
	
	/**
	 * 批量插入项目和优选计划
	 *
	 * @author  wangjf
	 * @date    2015年8月24日 下午7:18:36
	 * @param now
	 */
	public void batchProjectAndWealth(CommissionInfoEntity commissionInfoEntity);
	
	/**
	 * 汇总项目和优选计划业绩
	 *
	 * @author  wangjf
	 * @date    2016年3月5日 下午12:21:48
	 * @param params
	 * @return
	 */
	public Map<String, Object> sumProjectAndWealthCommission(Map<String, Object> params);
	
	/**
	 * 查询项目和优选计划业绩
	 *
	 * @author  wangjf
	 * @date    2016年3月5日 下午12:37:25
	 * @param params
	 * @return
	 */
	public Page<Map<String, Object>> findProjectAndWealthCommission(Map<String, Object> params);

	/**
	 * 批量插入项目、优选计划、散标
	 *
	 * @author  wangjf
	 * @date    2016年12月30日 下午1:34:35
	 * @param commissionInfoEntity
	 */
	public void batchProjectAndWealthAndLoan(CommissionInfoEntity commissionInfoEntity);

    /**
     * 查询业绩列表
     *
     * @param params
     * @return
     */
    Page<Map<String,Object>> queryNextTeamCustWealthList(Map<String, Object> params);

    /**
     * 根据工号查询当前级别
     *
     * @param jobNo
     * @return
     */
    int getCurrentRanking(String jobNo);

    /**
     * 根据工号查询姓名
     *
     * @param jobNo 工号
     * @return
     */
    String getJobNoByManagerName(String jobNo);

    /**
     * 查询业绩详情
     *
     * @param params
     * @return
     */
    Page<Map<String,Object>> queryNextTeamCustWealthDetail(Map<String, Object> params);
	
	/**
	 * 
	 * <查询平台交易数据>
	 * <功能详细描述>
	 *
	 * @return [参数说明]
	 * @return Map<String,Object> [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	public Map<String, Object> queryPlatformData();

	/**
	 * 
	 * <查询用户数据>
	 * <功能详细描述>
	 *
	 * @param custType
	 * @return [参数说明]
	 * @return Map<String,Object> [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	public Map<String, Object> queryCustData(String custType);

	/**
	 * 
	 * <交易总额>
	 * <功能详细描述>
	 *
	 * @return [参数说明]
	 * @return ResultVo [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	Map<String, Object> findTradeTotalAmount();
	
	/**
	 * 
	 * <累计注册人数>
	 * <功能详细描述>
	 *
	 * @return [参数说明]
	 * @return ResultVo [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	Map<String, Object> findRegisterTotalNumber();
	
	/**
	 * 
	 * <逾期金额>
	 * <功能详细描述>
	 *
	 * @return [参数说明]
	 * @return ResultVo [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	Map<String, Object> findOverdueAmount();
	
	/**
	 * 
	 * <待偿还金额>
	 * <功能详细描述>
	 *
	 * @return [参数说明]
	 * @return ResultVo [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	Map<String, Object> findUnRepaymentAmount();
	
	/**
	 * 
	 * <借款人数>
	 * <功能详细描述>
	 *
	 * @return [参数说明]
	 * @return ResultVo [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	Map<String, Object> findBorrowerNumber();
	
	/**
	 * 
	 * <月度成交量>
	 * <功能详细描述>
	 *
	 * @return [参数说明]
	 * @return ResultVo [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	String findTradeAmountMonth();
	
	/**
	 * 
	 * <投资人数>
	 * <功能详细描述>
	 *
	 * @return [参数说明]
	 * @return ResultVo [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	String findInvestNumber();
	
	/**
	 * 
	 * <性别所占比例>
	 * <功能详细描述>
	 *
	 * @return [参数说明]
	 * @return ResultVo [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	Map<String, Object> findGenderRatio();
	
	/**
	 * 
	 * <年龄分布比例>
	 * <功能详细描述>
	 *
	 * @return [参数说明]
	 * @return ResultVo [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	Map<String, Object> findAgeRatio();
	
	/**
	 * 
	 * <投资人地区分布>
	 * <功能详细描述>
	 *
	 * @return [参数说明]
	 * @return ResultVo [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	Map<String, Object> findInvesterAreaRatio();
	
	/**
	 * 
	 * <借款金额分布比例>
	 * <功能详细描述>
	 *
	 * @return [参数说明]
	 * @return ResultVo [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	Map<String, Object> findBorrowAmountRatio();
	
	/**
	 * 
	 * <不同期标的数>
	 * <功能详细描述>
	 *
	 * @return [参数说明]
	 * @return ResultVo [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	Map<String, Object> findProjectNumberNotTerm();
	
	/**
	 * 
	 * <还款方式>
	 * <功能详细描述>
	 *
	 * @return [参数说明]
	 * @return ResultVo [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	Map<String, Object> findRepaymentType();
	
	/**
	 * 
	 * <借款人地区分布比例>
	 * <功能详细描述>
	 *
	 * @return [参数说明]
	 * @return ResultVo [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	Map<String, Object> findBorrowerAreaRatio();

}
