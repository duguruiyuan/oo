package com.slfinance.shanlincaifu.repository.custom;

import java.math.BigDecimal;
import java.util.Map;
import com.slfinance.exception.SLException;


public interface WdzjRepositoryCustom{	
	/**
	 *
	 * @return totalAmount:String:当天借款标总额
	 */
	BigDecimal queryCurrentTotalAmount(Map<String, Object> param) ;	
	/**
	 * 网贷之家返回数据
	 * 
	 * @author fengyl
	 * @date 2017-4-17
	 * @param params
	 * @return
	 *         <tt>totalPage  :String:总页数(根据pageSize计算)</tt><br>
	 *         <tt>currentPage:String:当前页数</tt><br>
	 *         <tt>totalCount:     String:总标数</tt><br>
	 *         <tt>totalAmount    :String:当天借款标总额</tt><br>
	 *         <tt>borrowList: List<WdzjLoaninfoVO>:借款标信息</tt><br>
	 * @throws SLException
	 */
	Map<String, Object> queryLoaninfoAndInvestlistMap(Map<String, Object> param);
}
