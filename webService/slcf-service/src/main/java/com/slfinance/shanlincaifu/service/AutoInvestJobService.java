package com.slfinance.shanlincaifu.service;

import java.util.List;
import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.AutoInvestInfoEntity;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;

/**
 * @Desc 自动投标JOB帮助类
 * @author liyy
 * @date 2017/03/07 18:30:30
 */
public interface AutoInvestJobService extends BeanSelfAware {

	ResultVo autoInvest(Map<String, Object> params) throws SLException;
	/**
	 * 自动转让定时任务
	 *
	 * @author  fengyl
	 * @date    2017年3月14日 
	 * @return
	 */
	ResultVo autoTransfer(Map<String, Object> params) throws SLException;
	
	ResultVo updateAutoTransfer(String custId)throws SLException;
	
	public void credit(List<Map<String, Object>> creditList,AutoInvestInfoEntity custEntity);
	
	public void disperse(List<Map<String, Object>> loanList,AutoInvestInfoEntity custEntity);
	
	ResultVo updateISRunAutoInvest() throws SLException;
	
	/**
	 * 智能投顾.改
	 * @author  guoyk
	 * @date    2017-6-16 
	 * @param params
     *      <tt>limitedTerm       :String:最大期限</tt><br>
     *      <tt>limitedTermMin    :String:最小期限</tt><br>
     *      <tt>limitedYearRateMax:String:最大年化利率</tt><br>
     *      <tt>limitedYearRate   :String:最小年化利率</tt><br>
     *      <tt>loanUnit          :String:期限单位</tt><br>
     *      <tt>repaymentMethod   :String:还款方式</tt><br>
     *      <tt>canInvestProduct  :String:项目类型</tt><br>
     *      <tt>type          :String:标的类型</tt><br>
	 * @return
	 */
	@Rules(rules = {
			@Rule(name = "id", required = true, requiredMessage = "loanId或者transferApplyId不能为空"), 
			@Rule(name = "type", required = true, requiredMessage = "标的的类型(转让标或优选标)不能为空"),
			@Rule(name = "isShow", required = true, requiredMessage = "标的是否在前台购买区展示标识不能为空")
			
	})
	ResultVo autoInvestUp(Map<String, Object> params) throws SLException;
	
	ResultVo updateISRunAutoInvestForSingle(String loanType,String id) throws SLException;
	
	/**
	 * 预约投资-现金贷
	 * 购买项目-根据预约记录
	 */
//	ResultVo autoReserveInvestForReserveInvestId(String loanId);
	
	/**
	 * 预约投资-现金贷
	 * 购买项目-根据（同一个客户集成预约记录）
	 */
	ResultVo autoReserveInvestForCustId(String loanId);
}
