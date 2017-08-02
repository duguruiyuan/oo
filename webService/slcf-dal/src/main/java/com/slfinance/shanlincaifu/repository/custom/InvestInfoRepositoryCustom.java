package com.slfinance.shanlincaifu.repository.custom;

import java.util.List;
import java.util.Map;

import com.slfinance.shanlincaifu.entity.InvestInfoEntity;
import org.springframework.data.domain.Page;

import com.slfinance.exception.SLException;
import com.slfinance.vo.ResultVo;

/**
 * 数据总览
 * @author zhangt
 * @date   2015年12月11日下午4:39:22
 */
public interface InvestInfoRepositoryCustom {

	/**
	 * 数据总览--投资次数/投资总额
	 * @author zhangt
	 * @date   2015年12月11日下午4:39:22
	 * @return
	 */
	public List<Map<String,Object>> findInvestBusinessHistory();
	
	/**
	 * 查询加入记录
	 * @param params
	 * @return
	 */
	public Page<Map<String, Object>> queryProjectJoinPage(Map<String, Object> params);
	
	/**
	 * 查询投资记录
	 * @param params
	 * @return
	 */
	public ResultVo queryProjectInvestInfo(Map<String, Object> params);
	
	/**
	 * 查询投资排行
	 * @param params
	 * @return
	 */
	public List<Map<String,Object>> findInvestRankingInfo(Map<String, Object> params);
	
	/**
	 * 查询需要匹配债权的数据
	 * 
	 * @author zhiwen_feng
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> findNeedMatchInvestInfo(Map<String, Object> params);
	
	/**
	 * 查询所有满足条件的债权--债权匹配用
	 * 
	 * @date 2016-02-29
	 * @author zhiwen_feng
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> findAllCanAutoMatchInfo(Map<String, Object> params);
	
	/**
	 * 通过借款ID查询投资信息
	 *
	 * @author  wangjf
	 * @date    2016年11月30日 下午1:17:25
	 * @param loanId
	 * @return
	 */
	public List<Map<String, Object>> queryInvestListByLoanId(String loanId);
	
	/**
	 * 我的投资总览查询
	 * 
	 * @author zhiwen_feng
	 * @date 2016-11-29 
	 * @param params
     *      <tt>custId:String:客户ID</tt><br>
	 * @return Map<String, Object>
     *      <tt>totalDisperseAmount  :String:散标投资金额</tt><br>
     *      <tt>totalBuyCreditAmount :String:购买转让债权金额</tt><br>
     *      <tt>totalSaleCreditAmount:String:转出债权金额</tt><br>
	 * @throws SLException
	 */
	public Map<String, Object> queryMyTotalInvest(Map<String, Object> params) throws SLException;
	
	/**
	 * 债权收益查询
	 * 
	 * @author zhiwen_feng
	 * @date 2016-11-29
	 * @param params
	 * 		<tt>custId:String:客户ID</tt><br>
	 * @return Map<String, Object>
     *      <tt>waitingIncome :String:待收收益</tt><br>
     *      <tt>getIncome     :String:已获收益</tt><br>
     *      <tt>punishAmount  :String:逾期罚金</tt><br>
     *      <tt>transferIncome:String:转让盈亏</tt><br>
	 * @throws SLException
	 */
	public Map<String, Object> queryMyCreditIncome(Map<String, Object> params) throws SLException;

	/**
	 * 查询用户是否投资体验标
	 * @return
	 */
	List<InvestInfoEntity> queryCustIdInInvestTable(String loanId, String custId);

}
