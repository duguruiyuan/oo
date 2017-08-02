package com.slfinance.shanlincaifu.repository.custom;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.ReserveInvestInfoEntity;

/**
 * 
 * 渠道管理访问接口
 * 
 * 
 * @author fengyl
 * @version $Revision:1.0.0, $Date: 2017年6月10日
 */
public interface LoanReserveRepositoryCustom {
	
//	Page<Map<String, Object>> queryReserveLoanList(Map<String, Object> param);
	/**
	 * 预约投资列表查询
	 *
	 * @author  fengyl
	 * @date    2017-06-17 
	 * @param params
     *      <tt>custId          :String:客户ID</tt><br>
     *      <tt>start           :String:起始值</tt><br>
     *      <tt>length          :String:长度</tt><br>
     *      <tt>reserveStartDate:String:预约起始日期（可选）</tt><br>
     *      <tt>reserveEndDate  :String:预约到期日期（可选）</tt><br>
	 * @return
     *      <tt>reserveId          :String:预约ID</tt><br>
     *      <tt>reserveDate        :String:预约日期</tt><br>
     *      <tt>reserveAmount      :String:预约金额</tt><br>
     *      <tt>remainderAmount    :String:待投金额</tt><br>
     *      <tt>alreadyInvestAmount:String:已投金额</tt><br>
     *      <tt>reserveStatus      :String:预约状态</tt><br>
	 * @throws SLException
	 */
	Page<Map<String, Object>> queryMyReserveList(Map<String, Object> param);
	/**
	 * 预约投资汇总
	 *
	 * @author  fengyl
	 * @date    2017-06-17 11:41:29
	 * @param params
     *      <tt>custId:String:客户ID</tt><br>
	 * @return
     *      <tt>earnTotalAmount   :String:已赚金额</tt><br>
     *      <tt>exceptTotalAmount :String:待收收益</tt><br>
     *      <tt>investTotalAmount :String:在投金额</tt><br>
     *      <tt>reserveTotalAmount:String:预约总金额</tt><br>
	 * @throws SLException
	 */
	Map<String, Object> queryMyReserveIncome(Map<String, Object> param);
	
	List<ReserveInvestInfoEntity> getReserveCancelByReserveStatusAndReserveEndDate(Map<String, Object> param);
}
