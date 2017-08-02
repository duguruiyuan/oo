package com.slfinance.shanlincaifu.repository.custom;

import java.util.List;
import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.vo.ResultVo;

public interface WealthInfoRepositoryCustom {

	/**
	 * 理财计划列表
	 * 
	 * @author zhiwen_feng
	 * @date 2016-02-23
	 * @param parmas
	 * @return
	 * @throws SLException
	 */
	ResultVo queryWealthList(Map<String, Object> parmas)throws SLException;
	
	/**
	 * 查看项目
	 * 
	 * @author zhiwen_feng
	 * @param params
	 * @return
	 * @throws SLException
	 */
	ResultVo queryWealthDetailById(Map<String, Object> params)throws SLException;
	
	/**
	 * 用户计划投资列表
	 * 
	 * @date 2016-02-23
	 * @author zhiwen_feng
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo queryWealthJoinList(Map<String, Object> params)throws SLException;
	
	/**
	 * 项目列表
	 * 
	 * @author zhiwen_feng
	 * @date 2016-02-24
	 * @param params
	 * @return ResultVo
	 * @throws SLException
	 */
	public Map<String, Object> queryAllWealthList(Map<String, Object> params)throws SLException;
	
	/**
	 * 项目列表
	 * 
	 * @author zhiwen_feng
	 * @date 2016-02-24
	 * @param params
     *      <tt>lendingType     :String:出借方式（可选）</tt><br>
     *      <tt>typeTerm        :String:项目期限（可选）</tt><br>
     *      <tt>wealthStatus    :String:项目状态（可选）</tt><br>
     *      <tt>beginReleaseDate:String:开始发布日期（可选）</tt><br>
     *      <tt>endReleaseDate  :String:结束发布日期（可选）</tt><br>
     *      <tt>beginEffectDate :String:开始生效日期（可选）</tt><br>
     *      <tt>endEffectDate   :String:结束生效日期（可选）</tt><br>
     *      <tt>beginEndDate    :String:开始到期日期（可选）</tt><br>
     *      <tt>endEndDate      :String:结束到期日期（可选）</tt><br>
	 * @return ResultVo
     *      <tt>totalPlanTotalAmount    :String:项目总金额汇总</tt><br>
     *      <tt>totalAlreadyInvestAmount:String:已募集金额汇总</tt><br>
     *      <tt>totalWaitingMatchAmount :String:待匹配金额汇总</tt><br>
	 * @throws SLException
	 */
	public Map<String, Object> queryAllWealthSummary(Map<String, Object> params) throws SLException;
	
	/**
	 * 待匹配列表
	 * 
	 * @author zhiwen_feng
	 * @date 2016-02-25
	 * @param params
	 * @return ResultVo
	 * @throws SLException
	 */
	public ResultVo queryWaitingMatchList(Map<String, Object> params)throws SLException;
	
	/**
	 * 优选理财计划列表
	 * 
	 * @author zhiwen_feng
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public List<Map<String, Object>> queryPriorityWealthList(Map<String, Object> params)throws SLException;
	
	/**
	 * 查看优选计划
	 * 
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo queryWealthDetail(Map<String, Object> params)throws SLException;
	
	/**
	 * 投资记录 --分页
	 * 
	 * @DATE 2016-02-29
	 * @author zhiwen_feng
	 * @param params
     *      <tt>wealthId:String:项目ID</tt><br>
	 * @return ResultVo
	 * @throws SLException
	 */
	public ResultVo queryWealthInvestList(Map<String, Object> params)throws SLException; 
	
	/**
	 * 客户理财计划汇总信息
	 * 
	 * @author zhiwen_feng
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public List<Map<String, Object>> queryProjectIncome(Map<String, Object> params)throws SLException;
	
	/**
	 * 优选计划列表(投资人)
	 * 
	 * @author zhiwen_feng
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo queryMyWealthList(Map<String, Object> params)throws SLException;
	
	/**
	 * 优选计划查看
	 * 
	 * @author zhiwen_feng
	 * @date 2016-03-03
	 * @param params
	 * @return ResultVo
	 * @throws SLException
	 */
	public ResultVo queryMyWealthDetail(Map<String, Object> params)throws SLException;
	
	/**
	 * 我的债权列表
	 * 
	 * @date 2016-03-04
	 * @author zhiwen_feng
	 * @param params
	 * @return ResultVo
	 * @throws SLException
	 */
	public ResultVo queryMyWealthLoan(Map<String, Object> params)throws SLException;
	
	/**
	 * 数据汇总
	 * 
	 * @author zhiwen_feng
	 * @date 2016-03-04
	 * @param params
	 * @return ResultVo
	 * @throws SLException
	 */
	public List<Map<String, Object>> queryMyWeathSummary(Map<String, Object> params)throws SLException;
	
	/**
	 * 优选计划汇总
	 * 
	 * @author zhiwen_feng
	 * @date 2016-03-07
	 * @param params
     *      <tt>custId:String:客户ID</tt><br>
	 * @return ResultVo
     *      <tt>earnTotalAmount  :String:已赚金额</tt><br>
     *      <tt>exceptTotalAmount:String:待收收益</tt><br>
     *      <tt>investTotalAmount:String:在投金额</tt><br>
     *      <tt>tradeTotalAmount :String:投资总金额</tt><br>
	 * @throws SLException
	 */
	public ResultVo queryMyWealthIncome(Map<String, Object> params)throws SLException;
	
	/**
	 * 查看协议
	 * 
	 * @author zhiwen_feng
	 * @date 2016-03-07
	 * @param params
     *      <tt>wealthId:String:计划主键</tt><br>
     *      <tt>custId  :String:客户ID</tt><br>
	 * @return ResultVo 
	 * @throws SLException
	 */
	public Map<String, Object> queryWealthContract(Map<String, Object> params)throws SLException;
	
	/**
	 * 债权协议下载
	 * 
	 * @author zhiwen_feng
	 * @date 2016-03-07
	 * @param params
     *      <tt>wealthId:String:计划主键</tt><br>
     *      <tt>custId  :String:客户ID</tt><br>
     *      <tt>loanId  :String:债权ID</tt><br>
	 * @return ResultVo
     *      <tt>senderCustName         :String:出让人姓名</tt><br>
     *      <tt>senderCredentialsCode  :String:出让人身份证号码</tt><br>
     *      <tt>senderLoginName        :String:出让人账号</tt><br>
     *      <tt>receiverCustName       :String:受让人姓名</tt><br>
     *      <tt>receiverCredentialsCode:String:受让人身份证号码</tt><br>
     *      <tt>receiverLoginName      :String:受让人账号</tt><br>
     *      <tt>receiverAddress        :String:受让人地址（为法人需该项）</tt><br>
     *      <tt>loanCode               :String:债权编号</tt><br>
     *      <tt>currentTerm            :String:债权当前期数</tt><br>
     *      <tt>endTerm                :String:债权结束期数（期数=债权总期数/还款频率）</tt><br>
     *      <tt>loanAmount             :String:债权价值</tt><br>
	 * @throws SLException
	 */
	public List<Map<String, Object>> queryWealthLoanContract(Map<String, Object> params)throws SLException;
	
	/**
	 * 
	 * 优选计划列表
	 * 
	 *@author zhiwen_feng
	 *@date 2016-03-07 
	 * @param params
	 * @return ResultVo 
	 * @throws SLException
	 */
	public ResultVo queryShowWealthList(Map<String, Object> params)throws SLException;
	
	/**
	 * 查询生效发短信需要信息
	 * 
	 * @author zhiwen_feng
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public List<Map<String, Object>> querySendSmsEffectWealthInfo(Map<String, Object> params)throws SLException;
	
	/**
	 * 查询所有优选计划列表
	 * 
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo queryAllShowWealthList(Map<String, Object> params)throws SLException;
	
}
