package com.slfinance.shanlincaifu.repository.custom;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;




/**   
 * 
 * 一键出借访问接口 
 * @author  fengyl
 * @version $Revision:1.0.0, $Date: 2017年7月13日 
 */
public interface LoanManagerGroupRepositoryCustom{
	
	/** 查询交易详情页期限 */
	public List<Map<String,Object>> queryTradeInfoByOneStepInvest(Map<String, Object> parms);
	
	/** 查询交易详情页标的个数 ,总金额,利率*/
	public Map<String, Object> queryTradeInfoByLoanTermAndLoanUnit(Map<String, Object> parms);
	
	/** 查询剩余可购买金额 */
	BigDecimal getCanBuyTotalAmount(Map<String, Object> params);
	
	/** 分配购买项目 */
	List<Map<String, Object>> findDispatchLoans(Map<String, Object> params);

	
	/**
	 * 查询可购买的标的
	 *
	 * @author  wangjf
	 * @date    2017年7月21日 下午1:02:28
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> findCanInvestLoanList(Map<String, Object> params);

	
	/** 优选项目-我的出借-查询 */
	Map<String, Object> queryMyDisperseListByGroup(Map<String, Object> params);
	
	/** 优选项目-我的出借-详情列表-查询 */
	Map<String, Object> queryMyDisperseListDetailByGroup (Map<String, Object> params);

}
