package com.slfinance.shanlincaifu.repository.custom;

import java.util.List;
import java.util.Map;

public interface LoanProjectRepositoryCustom {

	/**
	 * 查找待流标的项目
	 *
	 * @author  wangjf
	 * @date    2016年11月28日 下午6:43:52
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> findWaitingUnReleaseLoan(Map<String, Object> params);
	
	
	/**
	 * 查询待放款确认的项目
	 *
	 * @author  wangjf
	 * @date    2016年12月17日 下午1:27:00
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> findWaitingGrantConfirmList(Map<String, Object> params);
	
	/**
	 * 根据资产端公司名称查询待放款确认的项目
	 *
	 * @author  zhagnze
	 * @date    2017年6月19日 下午1:27:00
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> findWaitingGrantConfirm4CompanyList(Map<String, Object> params);
	
	/**
	 * 查询意真待放款确认的项目
	 *
	 * @author  zhagnze
	 * @date    2017年6月19日 下午1:27:00
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> findWaitingGrantConfirmListYZ(Map<String, Object> params);
	
	/***
	 * 
	 * <b>方法名：</b>：findWaitingGrantConfirmListJL<br>
	 * <b>功能说明：</b>查询巨涟等待放款确认的项目<br>
	 * @author <font color='blue'>张祥</font> 
	 * @date  2017年7月4日 下午2:33:57
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> findWaitingGrantConfirmListJL(Map<String, Object> params);
	
	/**
	 * 查询待放款的项目
	 *
	 * @author  wangjf
	 * @date    2016年12月17日 下午1:27:00
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> findWaitingGrantList(Map<String, Object> params);
	
	/**
	 * 根据资产端公司名称查询待放款的项目
	 *
	 * @author  zhangze
	 * @date    2017年6月12日 下午1:27:00
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> findWaitingGrant4CompanyList(Map<String, Object> params);
	
	/**
	 * 查询意真待放款的项目
	 *
	 * @author  zhangze
	 * @date    2017年6月12日 下午1:27:00
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> findWaitingGrantYZList(Map<String, Object> params);
	
	/**
	 * 
	 * <b>方法名：</b>：findWaitingGrantJLList<br>
	 * <b>功能说明：</b>查询意真巨涟待放款项目<br>
	 * @author <font color='blue'>张祥</font> 
	 * @date  2017年7月4日 下午2:05:18
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> findWaitingGrantJLList(Map<String, Object> params);
	
	/**
	 * 查询待撤销的转让申请
	 *
	 * @author  wangjf
	 * @date    2016年12月30日 上午10:02:52
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> findWaitingCancelList(Map<String, Object> params);
	
	/**
	 * 查询待发布的借款
	 *
	 * @author  wangjf
	 * @date    2017年2月9日 上午9:24:18
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> findWaitingPublishLoan(Map<String, Object> params);
}
