package com.slfinance.shanlincaifu.repository.custom;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.CreditRightValueEntity;
import com.slfinance.shanlincaifu.entity.LoanCustInfoEntity;
import com.slfinance.shanlincaifu.entity.LoanDetailInfoEntity;
import com.slfinance.shanlincaifu.entity.LoanInfoEntity;
import com.slfinance.shanlincaifu.entity.RepaymentPlanInfoEntity;
import com.slfinance.shanlincaifu.vo.RepaymentPlanVo;
import com.slfinance.vo.ResultVo;


/**   
 * 
 * 借款业务自定义数据访问接口 
 * @author  zhoudl
 * @version $Revision:1.0.0, $Date: 2015年4月23日 上午10:04:46 $ 
 */
public interface LoanInfoRepositoryCustom{

	/**
	 * @desc 查询为执行PV计算的债权
	 * @author zhoudl
	 * **/
	public List<RepaymentPlanVo> queryUnExecutePvLoan() throws SLException;
	
	/**
	 * @desc 查询债权信息
	 * @author zhoudl
	 * **/
	public Map<String, Object> queryConditionLoan(Map<String,Object> conditionMap) throws SLException;
	
	/**
	 * @desc 根据主键ID,查询还款明细
	 * @author zhoudl
	 * **/
	public List<Map<String,Object>> queryLoanRepaymentDetail(Map<String,Object> conditionMap) throws SLException;
	
	/**
	 * @desc 批量插入PV值
	 * @author zhoudl
	 * **/
	public boolean batchInsertPv(final List<CreditRightValueEntity> list) throws SLException;
	
	/**
	 * @desc 批量更新债权PV执行状态
	 * @author zhoudl
	 * **/
	public boolean batchUpdateLoanExecStatus(final List<String> loanIds) throws SLException;
	
	/**
	 * @desc 批量插入借款客户
	 * @author zhoudl
	 * **/
	public boolean batchInsertLoanCust(final List<LoanCustInfoEntity> loanCustList) throws SLException;
	
	/**
	 * @desc 批量插入借款信息
	 * @author zhoudl
	 * **/
	public boolean batchInsertLoan(final List<LoanInfoEntity> loanList) throws SLException;
	
	/**
	 * @desc 批量插入借款详情
	 * @author zhoudl
	 * **/
	public boolean batchInsertLoanDetail(final List<LoanDetailInfoEntity> loanDetailList) throws SLException;
	
	/**
	 * @desc 验证债权编号是否存在
	 * @author zhoudl
	 * **/
	public String validateLoanCode(final Map<String, List<String>> loanCodeMap) throws SLException;
	
	/**
	 * @desc 批量插入还款计划
	 * @author zhoudl
	 * **/
	public boolean batchInsertRepaymentPlan(final List<RepaymentPlanInfoEntity> repaymentPlanList) throws SLException;
	
	/**
	 * @desc 批量更新借款详情表
	 * @author zhoudl
	 * **/
	public boolean batchUpdateLoanDetail(final List<LoanDetailInfoEntity> loanDetailList) throws SLException;
	
	/**
	 * @desc 根据债权编号,获取债权主键ID
	 * @author zhoudl
	 * **/
	public List<Map<String, Object>> findByLoanCode(final Set<String> loanCodeSet) throws SLException;
	
	/**
	 * @desc 根据债权编号,查找还款计划
	 * @author zhoudl
	 * **/
	public String repaymentPlanFindByLoanCode(Set<String> loanCodeSet) throws SLException;
	
	/**
	 * @desc 根据第三方机构号查询
	 * @author zhoudl
	 * **/
	public List<Map<String, Object>> queryDebtSourceCode(Map<String, List<String>> debtSourceCodeMap) throws SLException;
	
	/**
	 * @desc 查询所有债权资质类型
	 * @return java.util.Map
	 * @author zhoudl
	 * **/
	public Map<String,String> queryAllAssetType() throws SLException;
	
	/**
	 * @desc 查询所有第三方机构信息
	 * @return java.util.List
	 * @author zhoudl
	 * **/
	public List<Map<String,Object>> queryAllDebtSourceCode() throws SLException;
	
	/**
	 * 我的投资-债权列表
	 * 我的投资-债权列表(已分配的债权)/业务管理-查询债权数据列表
	 *
	 * @author  wangjf
	 * @date    2015年4月27日 下午6:24:42
	 * @param 
	 		<tt>start：int:分页起始页</tt><br>
	 		<tt>length：int:每页长度</tt><br>
	 		<tt>productType：String:产品类型</tt><br>
	 		<tt>custId：String:客户编号</tt><br>
	 		<tt>debtSource： String:来源机构(可为空)</tt><br>
			<tt>productCode： String:借款产品(可为空)</tt><br>
			<tt>yearRateBegin： BigDecimal:年化利率开始(可为空)</tt><br>
			<tt>yearRateEnd： BigDecimal:年化利率结束(可为空)</tt><br>
			<tt>loanTerm： BigInteger:借款期限(可为空)</tt><br>
			<tt>creditRightStatus： String:借款状态(可为空)</tt><br>
			<tt>repaymentDay： String:还款日(可为空)</tt><br>
			<tt>importDateBegin： String:导入日期开始(可为空)</tt><br>
			<tt>importDateEnd： String:导入日期结束(可为空)</tt><br>
			<tt>allotDateBegin： String:分配日期开始(可为空)</tt><br>
			<tt>allotDateEnd： String:分配日期结束(可为空)</tt><br>
	 * @return
	 		<tt>id： String:债权编号</tt><br>
	 		<tt>loanCode： String:债权编号</tt><br>
	 		<tt>debtSource： String:来源机构</tt><br>
	 		<tt>loanAmount： String:借款金额</tt><br>
	 		<tt>loanTerm： String:借款期限</tt><br>
	 		<tt>yearRate： String:年化利率</tt><br>
	 		<tt>holdAmount： BigDecimal:持有金额/投资金额</tt><br>
			<tt>holdScale： BigDecimal:持有比例</tt><br>	 		
	 * @throws SLException
	 */
	public Page<Map<String, Object>> findLoanList(Map<String, Object> param) throws SLException;
	
	/**
	 * 业务管理-债权数据汇总（查询已分配的债权价值、金额、笔数）
	 *
	 * @author  wangjf
	 * @date    2015年4月24日 下午2:25:50
	 * @param param
	        <tt>start：int:分页起始页</tt><br>
	 		<tt>length：int:每页长度</tt><br>
	 		<tt>productType：String:产品类型</tt><br>
	 		<tt>custId：String:客户编号</tt><br>
	 		<tt>debtSource： String:来源机构(可为空)</tt><br>
			<tt>productCode： String:借款产品(可为空)</tt><br>
			<tt>yearRateBegin： BigDecimal:年化利率开始(可为空)</tt><br>
			<tt>yearRateEnd： BigDecimal:年化利率结束(可为空)</tt><br>
			<tt>loanTerm： BigInteger:借款期限(可为空)</tt><br>
			<tt>creditRightStatus： String:借款状态(可为空)</tt><br>
			<tt>repaymentDay： String:还款日(可为空)</tt><br>
			<tt>importDateBegin： String:导入日期开始(可为空)</tt><br>
			<tt>importDateEnd： String:导入日期结束(可为空)</tt><br>
			<tt>allotDateBegin： String:分配日期开始(可为空)</tt><br>
			<tt>allotDateEnd： String:分配日期结束(可为空)</tt><br>
	 * @return
	  		<tt>totalPv： BigDecimal:价值汇总</tt><br>
	 		<tt>totalLoanAmount： BigDecimal:债权金额汇总</tt><br>
	 		<tt>totalLoans： Integer:总笔数</tt><br>
	 */
	public Map<String, Object> findLoanListCount(Map<String, Object> param);
	
	/**
	 * 根据债权ID取还款计划
	 *
	 * @author  wangjf
	 * @date    2015年4月27日 下午7:57:47
	 * @param id
	 * @return 
	 		<tt>currentTerm： BigInteger:还款期数</tt><br>
	 		<tt>repaymentAmount： BigDecimal:还款本息</tt><br>
	 		<tt>repaymentStatus： BigInteger:还款状态</tt><br>
	 * @throws SLException
	 */
	public List<Map<String, Object>> findRepaymentPlanList(String id, BigDecimal holdScale) throws SLException;
	
	/**
	 * 通过债权ID取债权明细
	 *
	 * @author  wangjf
	 * @date    2015年4月27日 下午8:15:30
	 * @param loanId
	 * @return
			<tt>id： String:债权ID</tt><br>
	 		<tt>loanCode： String:债权编号</tt><br>
	 		<tt>debtSource： String:来源机构</tt><br>
			<tt>productCode： String:借款产品</tt><br>
			<tt>custName： String:借款人</tt><br>
			<tt>loanAmount： BigDecimal:借款金额（元）</tt><br>
			<tt>currentValue： BigDecimal:当前价值</tt><br>
			<tt>loanTerm： String:借款期限（月）</tt><br>
			<tt>repaymentDay： String:还款日</tt><br>
			<tt>grantDate： Date:放款日期</tt><br>
			<tt>importDate： Date:导入日期</tt><br>
			<tt>creditRightStatus： String:债权状态</tt><br>
			<tt>yearRate： BigDecimal:年化利率</tt><br>
			<tt>holdAmount： BigDecimal:持有金额/投资金额</tt><br>
			<tt>holdScale： BigDecimal:持有比例</tt><br>
	 * @throws SLException
	 */
	public Map<String, Object> findLoanDeatilById(String loanId)throws SLException;
	
	/**
	 * 债权详情
	 * 
	 * @author zhiwen_feng
	 * @param loanId
	 * @return
	 * @throws SLException
	 */
	public Map<String, Object> queryLoanInfoDetailByLoanId(String loanId)throws SLException;
	
	
	/**
	 * 业务管理列表查询
	 * 
	 * 
	 * @author zhiwen_feng
	 * @date 2016-11-29
	 * 
	 * @param params
     *      <tt>start           :String:起始值</tt><br>
     *      <tt>length          :String:长度</tt><br>
     *      <tt>loanCode        :String:借款编号(可以为空)</tt><br>
     *      <tt>custName        :String:客户姓名(可以为空)</tt><br>
     *      <tt>loanType        :String:借款类型(可以为空)</tt><br>
     *      <tt>loanStatus      :String:借款状态(可以为空)</tt><br>
     *      <tt>loanTerm        :String:还款期限(可以为空)</tt><br>
     *      <tt>repaymentMethod :String:还款方式(可以为空)</tt><br>
     *      <tt>publishDateStart:String:发布日期-区间头(可以为空)</tt><br>
     *      <tt>publishDateEnd  :String:发布日期-区间末(可以为空)</tt><br>
	 * @return
     *      <tt>data           :String:List<Map<String,Object>></tt><br>
     *      <tt>loanId         :String:借款信息表主键Id</tt><br>
     *      <tt>loanCode       :String:借款编号</tt><br>
     *      <tt>custName       :String:客户姓名</tt><br>
     *      <tt>loanType       :String:借款类型</tt><br>
     *      <tt>publishDate    :String:发布日期</tt><br>
     *      <tt>rasieEndDate   :String:募集日期</tt><br>
     *      <tt>loanAmount     :String:借款金额（元）</tt><br>
     *      <tt>yearIrr        :String:借款利率 （详细表）</tt><br>
     *      <tt>loanTerm       :String:借款期限</tt><br>
     *      <tt>repaymentMethod:String:还款方式</tt><br>
     *      <tt>loanStatus     :String:借款状态</tt><br>
	 * @throws SLException
	 */
	public Map<String, Object> queryBusinessManageList(Map<String, Object> params) throws SLException;
	
	/**
	 * 散标投资列表查询
	 * 
	 * @author zhiwen_feng
	 * @date 2016-11-30
	 * @param params
     *      <tt>custId        :String:客户ID</tt><br>
     *      <tt>disperseStatus:String:散标状态</tt><br>
     *      <tt>start         :String:起始值</tt><br>
     *      <tt>length        :String:长度</tt><br>
	 * @return List<Map<String, Object>>
     *      <tt>disperseId    :String:散标主键</tt><br>
     *      <tt>disperseType  :String:散标名称</tt><br>
     *      <tt>creditNo      :String: 债权编号            </tt><br>
     *      <tt>yearRate      :String:年化收益率</tt><br>
     *      <tt>investlAmount :String:投资金额</tt><br>
     *      <tt>typeTerm      :String:项目期限(月)</tt><br>
     *      <tt>investScale   :String:已投百分比</tt><br>
     *      <tt>disperseStatus:String:散标状态</tt><br>
     *      <tt>disperseDate  :String:投资时间</tt><br>
	 * @throws SLException
	 */
	public Map<String, Object> queryMyDisperseList(Map<String, Object> params) throws SLException;
	
	/**
	 * 投资详情
	 * 
	 * @author zhiwen_feng
	 * @date 2016-11-30
	 * @param params
     *      <tt>disperseId:String:散标主键</tt><br>
     *      <tt>custId    :String:客户ID</tt><br>
	 * @return Map<String, Object>
     *      <tt>disperseId     :String:散标主键</tt><br>
     *      <tt>loanNo         :String: 借款编号</tt><br>
     *      <tt>investlAmount  :String:投资金额</tt><br>
     *      <tt>investlDate    :String:投资日期</tt><br>
     *      <tt>investTerm     :String:投资期限</tt><br>
     *      <tt>waitingIncome  :String:待收收益</tt><br>
     *      <tt>interestDate   :String:起息日期</tt><br>
     *      <tt>nextPaymentDay :String:下个还款日             </tt><br>
     *      <tt>yearRate       :String:年化收益率</tt><br>
     *      <tt>getIncome      :String:已获收益</tt><br>
     *      <tt>expireDate     :String:到期日期</tt><br>
     *      <tt>repaymentMethod:String:还款方式</tt><br>
     *      <tt>remainPrincipal:String:剩余本金</tt><br>
     *      <tt>investScale    :String:已投百分比</tt><br>
     *      <tt>investStatus   :String:投资状态</tt><br>
	 * @throws SLException
	 */
	public Map<String, Object> queryMyDisperseDetail(Map<String, Object> params)throws SLException;
	
	/**
	 * 回款计划
	 * 
	 * @author zhiwen_feng
	 * @date 2016-11-30
	 * @param params
     *      <tt>disperseId:String:散标主键</tt><br>
     *      <tt>custId    :String:客户ID</tt><br>
     *      <tt>start     :String:起始值</tt><br>
     *      <tt>length    :String:长度</tt><br>
	 * @return
     *      <tt>paybackDate     :String:应还日期</tt><br>
     *      <tt>paybackTotal    :String:应还本息和</tt><br>
     *      <tt>paybackPrincipal:String:应还本金</tt><br>
     *      <tt>paybackInterest :String:应还利息</tt><br>
     *      <tt>punishAmount    :String:逾期违约金</tt><br>
     *      <tt>makeupAmount    :String:提前还款补偿金</tt><br>
     *      <tt>paybackStatus   :String:还款状态</tt><br>
	 * @throws SLException
	 */
	public Map<String, Object> queryMyDispersePaybackPlan(Map<String, Object> params)throws SLException;

	
	/**
	 * 更新放款状态
	 *
	 * @author  wangjf
	 * @date    2016年12月1日 下午4:07:50
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public int updateLoanGrantStatus(Map<String, Object> params)throws SLException;

	
	/**
	 * 债权转让列表查询
	 * 
	 * @author zhiwen_feng
	 * @date 2016-12-01
	 * @param params
     *      <tt>custId      :String:客户ID</tt><br>
     *      <tt>creditStatus:String:债权状态</tt><br>
     *      <tt>start       :String:起始值</tt><br>
     *      <tt>length      :String:长度</tt><br>
	 * @return 
     *      <tt>creditId       :String:债权主键</tt><br>
     *      <tt>creditNo       :String: 债权编号            </tt><br>
     *      <tt>yearRate       :String:年化收益率</tt><br>
     *      <tt>investlAmount  :String:投资金额</tt><br>
     *      <tt>remainPrincipal:String:剩余本金</tt><br>
     *      <tt>getIncome      :String:已获收益</tt><br>
     *      <tt>remainTerm     :String:剩余期数</tt><br>
     *      <tt>investStatus   :String:投资状态</tt><br>
     *      <tt>nextPaymentDay :String:下个还款日</tt><br>
	 * @throws SLException
	 */
	public Map<String, Object> queryMyCreditList(Map<String, Object> params) throws SLException;

	/**
	 * 债权转让列表查询ForJob(可转让债权列表)
	 *
	 * @author  fengyl
	 * @date    2017-3-14 
	 * @param params
     *      <tt>custId:String:客户ID</tt><br>
     *      <tt>termMax:Integer:最低期限(自动转让月份)</tt><br>
	 * @return
     *      <tt>holdId           :String:债权持有表ID</tt><br>
     *      <tt>investId         :String:投资主键</tt><br>
     *      <tt>disperseId       :String:散标主键</tt><br>
     *      <tt>loanTitle        :String:借款名称</tt><br>
     *      <tt>loanCode         :String: 债权编号</tt><br>
     *      <tt>yearRate         :String:借款年利率</tt><br>
     *      <tt>investAmount     :String:投资金额</tt><br>
     *      <tt>exceptRepayAmount:String:待收本息</tt><br>
     *      <tt>remainTerm       :String:剩余期限</tt><br>
     *      <tt>investStatus     :String:投资状态</tt><br>
     *      <tt>nextPaymentDay   :String:下个还款日</tt><br>
	 * @throws SLException
	 */
	public List<Map<String, Object>> queryMyCreditListForJob(Map<String, Object> params) throws SLException;
	
	/**
	 * 投资详情
	 * 
	 * @author zhiwen_feng
	 * @date 2016-12-01
	 * @param params
     *      <tt>creditId:String:债权主键</tt><br>
     *      <tt>custId  :String:客户ID</tt><br>
	 * @return
     *      <tt>creditId       :String:债权主键</tt><br>
     *      <tt>creditNo       :String: 债权编号</tt><br>
     *      <tt>investlAmount  :String:投资金额</tt><br>
     *      <tt>investlDate    :String:投资日期</tt><br>
     *      <tt>investTerm     :String:投资期限</tt><br>
     *      <tt>waitingIncome  :String:待收收益</tt><br>
     *      <tt>nextPaymentDay :String:下个还款日            </tt><br>
     *      <tt>yearRate       :String:年化收益率</tt><br>
     *      <tt>getIncome      :String:已获收益</tt><br>
     *      <tt>expireDate     :String:到期日期</tt><br>
     *      <tt>repaymentMethod:String:还款方式</tt><br>
     *      <tt>remainPrincipal:String:剩余本金</tt><br>
     *      <tt>investScale    :String:已投百分比</tt><br>
     *      <tt>investStatus   :String:投资状态</tt><br>
	 * @throws SLException
	 */
	public Map<String, Object> queryMyCreditDetail(Map<String, Object> params) throws SLException;
	
	/**
	 * 批量查询债权信息
	 */
	public List<Map<String, Object>> queryMyCreditDetailForBatch(Map<String, Object> params) throws SLException;
	/**
	 * 回款计划
	 * 
	 * @author zhiwen_feng
	 * @date 2016-21-01
	 * @param params
     *      <tt>creditId:String:债权主键</tt><br>
     *      <tt>custId  :String:客户ID</tt><br>
     *      <tt>start   :String:起始值</tt><br>
     *      <tt>length  :String:长度</tt><br>
	 * @return
     *      <tt>paybackDate     :String:应还日期</tt><br>
     *      <tt>paybackTotal    :String:应还本息和</tt><br>
     *      <tt>paybackPrincipal:String:应还本金</tt><br>
     *      <tt>paybackInterest :String:应还利息</tt><br>
     *      <tt>punishAmount    :String:逾期违约金</tt><br>
     *      <tt>makeupAmount    :String:提前还款补偿金</tt><br>
     *      <tt>paybackStatus   :String:还款状态</tt><br>
	 * @throws SLException
	 */
	public Map<String, Object> queryMyCreditPaybackPlan(Map<String, Object> params) throws SLException; 
	
	/**
	 * 近期应还数据查询
	 * 
	 * @author zhiwen_feng
	 * @date 2016-12-01
	 * @param params
     *      <tt>start                  :String:起始值</tt><br>
     *      <tt>length                 :String:长度</tt><br>
     *      <tt>loanCode               :String:借款编号(可以为空)</tt><br>
     *      <tt>custName               :String:客户姓名(可以为空)</tt><br>
     *      <tt>repaymentMethod        :String:还款方式(可以为空)</tt><br>
     *      <tt>loanType               :String:借款类型(可以为空)</tt><br>
     *      <tt>expectRpaymentDateStart:String:应还日期-区间头(可以为空)</tt><br>
     *      <tt>expectRpaymentDateEnd  :String:应还日期-区间末(可以为空)</tt><br>
	 * @return
     *      <tt>totalCount          :String:应还款笔数</tt><br>
     *      <tt>totalAmount         :String:应还金额汇总</tt><br>
     *      <tt>data                :String:List<Map<String,Object>></tt><br>
     *      <tt>repaymentPlanId     :String:还款计划表主键Id</tt><br>
     *      <tt>loanCode            :String:借款编号</tt><br>
     *      <tt>repaymentTotalAmount:String:应还金额（元）</tt><br>
     *      <tt>expectRpaymentDate  :String:应还日期</tt><br>
     *      <tt>currentTerm         :String:当前期数   (格式“currentTerm/loanTerm”)</tt><br>
     *      <tt>yearIrr             :String:借款利率</tt><br>
     *      <tt>repaymentMethod     :String:还款方式</tt><br>
     *      <tt>loanAmount          :String:借款金额（元）</tt><br>
     *      <tt>custName            :String:客户姓名</tt><br>
     *      <tt>loanType            :String:借款类型</tt><br>
	 * @throws SLException
	 */
	public Map<String, Object> queryRecentlyRepaymentList(Map<String, Object> params) throws SLException;
	
	/**
	 * 近期应还数据统计
	 * @author zhiwen_feng
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public Map<String, Object> queryRecentlyRepaymentCount(Map<String, Object> params) throws SLException;
	
	/**
	 * 逾期中数据查询
	 * 
	 * @author zhiwen_feng
	 * @date 2016-12-01
	 * @param params
     *      <tt>start                  :String:起始值</tt><br>
     *      <tt>length                 :String:长度</tt><br>
     *      <tt>loanCode               :String:借款编号(可以为空)</tt><br>
     *      <tt>custName               :String:客户姓名(可以为空)</tt><br>
     *      <tt>repaymentMethod        :String:还款方式(可以为空)</tt><br>
     *      <tt>loanType               :String:借款类型(可以为空)</tt><br>
     *      <tt>expectRpaymentDateStart:String:应还日期-区间头(可以为空)</tt><br>
     *      <tt>expectRpaymentDateEnd  :String:应还日期-区间末(可以为空)</tt><br>
	 * @return
     *      <tt>totalCount          :String:逾期期数</tt><br>
     *      <tt>totalOverdueExpense :String:逾期管理费</tt><br>
     *      <tt>totalAmount         :String:逾期金额</tt><br>
     *      <tt>data                :String:List<Map<String,Object>></tt><br>
     *      <tt>repaymentPlanId     :String:还款计划表主键Id</tt><br>
     *      <tt>loanCode            :String:借款编号</tt><br>
     *      <tt>repaymentTotalAmount:String:应还金额（元）</tt><br>
     *      <tt>expectRpaymentDate  :String:应还日期</tt><br>
     *      <tt>currentTerm         :String:应还期数   (格式“currentTerm/loanTerm”)</tt><br>
     *      <tt>overdueDays         :String:逾期天数</tt><br>
     *      <tt>overdueExpense      :String:逾期管理费</tt><br>
     *      <tt>loanAmount          :String:借款金额（元）</tt><br>
     *      <tt>custName            :String:客户姓名</tt><br>
     *      <tt>repaymentMethod     :String:还款方式</tt><br>
     *      <tt>loanType            :String:借款类型</tt><br>
	 * @throws SLException
	 */
	public Map<String, Object> queryOverdueDataList(Map<String, Object> params) throws SLException;
	
	/**
	 * 逾期中数据统计
	 * 
	 * @author zhiwen_feng
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public Map<String, Object> queryOverdueDataCount(Map<String, Object> params) throws SLException;
	
	/**
	 * 还款数据查询
	 * 
	 * @author zhiwen_feng
	 * @date 2016-12-01
	 * @param params
     *      <tt>start               :String:起始值</tt><br>
     *      <tt>length              :String:长度</tt><br>
     *      <tt>loanCode            :String:借款编号(可以为空)</tt><br>
     *      <tt>custName            :String:客户姓名(可以为空)</tt><br>
     *      <tt>loanTerm            :String:借款期限(可以为空)</tt><br>
     *      <tt>repaymentMethod     :String:还款方式(可以为空)</tt><br>
     *      <tt>loanType            :String:借款类型(可以为空)</tt><br>
     *      <tt>loanStatus          :String:借款状态(可以为空)</tt><br>
     *      <tt>investStartDateStart:String:起息日期(可以为空)</tt><br>
     *      <tt>investStartDateEnd  :String:起息日期(可以为空)</tt><br>
     *      <tt>investEndDateStart  :String:到期日期</tt><br>
     *      <tt>investEndDateEnd    :String:到期日期</tt><br>
	 * @return
     *      <tt>totalCount             :String:借款笔数</tt><br>
     *      <tt>totalAmount            :String:借款金额</tt><br>
     *      <tt>totalRepaymentAmount   :String:待还本息</tt><br>
     *      <tt>totalAlreadyRepayAmount:String:已还本息</tt><br>
     *      <tt>data                   :String:List<Map<String,Object>></tt><br>
     *      <tt>loanId        		   :String:散表主键Id</tt><br>
     *      <tt>loanCode               :String:借款编号</tt><br>
     *      <tt>custName               :String:客户姓名</tt><br>
     *      <tt>loanType               :String:借款类型</tt><br>
     *      <tt>loanAmount             :String:借款金额（元）</tt><br>
     *      <tt>loanTerm               :String:借款期限</tt><br>
     *      <tt>yearIrr                :String:借款利率</tt><br>
     *      <tt>repaymentMethod        :String:还款方式</tt><br>
     *      <tt>loanStatus             :String:借款状态</tt><br>
     *      <tt>waitingPayment         :String:待还本息</tt><br>
     *      <tt>hasPayment             :String:已还本息</tt><br>
     *      <tt>startDate              :String:起息日期</tt><br>
     *      <tt>endDate                :String:到期日期</tt><br>
	 * @throws SLException
	 */
	public Map<String, Object> queryAlreadyRepayList(Map<String, Object> params) throws SLException;
	
	/**
	 * 还款数据查询统计
	 * 
	 * @author zhiwen_feng
	 * @date  2016-12-02
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public Map<String, Object> queryAlreadyRepayCount(Map<String, Object> params) throws SLException;

	/**
	 * 财务放款列表查询
	 * 
	 * @author zhiwen_feng
	 * @date 2016-12-01
	 * @param params
     *      <tt>start             :String:起始值</tt><br>
     *      <tt>length            :String:长度</tt><br>
     *      <tt>loanCode          :String:借款编号(可以为空)</tt><br>
     *      <tt>custName          :String:客户姓名(可以为空)</tt><br>
     *      <tt>loanType          :String:借款类型(可以为空)</tt><br>
     *      <tt>lendStatus        :String:放款状态(可以为空)</tt><br>
     *      <tt>lendDateStart     :String:放款日期-区间头(可以为空)</tt><br>
     *      <tt>lendDateEnd       :String:放款日期-区间末(可以为空)</tt><br>
     *      <tt>fullScaleDateStart:String:满标日期-区间头(可以为空)</tt><br>
     *      <tt>fullScaleDateEnd  :String:满标日期-区间末(可以为空)</tt><br>
     *      <tt>reviewedDateStart :String:复核通过日期-区间头(可以为空)</tt><br>
     *      <tt>reviewedDateEnd   :String:复核通过日期-区间末(可以为空)</tt><br>
	 * @return
     *      <tt>loanCode       :String:借款编号</tt><br>
     *      <tt>custName       :String:客户姓名</tt><br>
     *      <tt>credentialsCode:String:证件号码</tt><br>
     *      <tt>loanType       :String:借款类型</tt><br>
     *      <tt>loanAmount     :String:借款金额（元）</tt><br>
     *      <tt>fullScaleDate  :String:满标日期</tt><br>
     *      <tt>reviewedDate   :String:复核通过日期</tt><br>
     *      <tt>lendDate       :String:放款日期</tt><br>
     *      <tt>lendStatus     :String:放款状态</tt><br>
     *      <tt>auditorName    :String:审核人员</tt><br>
	 * @throws SLException
	 */
	public Map<String, Object> queryLendMoneyList(Map<String, Object> params) throws SLException;
	
	/**
	 * 根据债权id查询投资信息
	 * 
	 * @author zhiwen_feng
	 * @date 2016-12-02
	 * @param loanId
	 * @return
	 */
	public List<Map<String, Object>> queryLoanInfobyLoanIds(Map<String, Object> params); 
	
	/**
	 * 散标投资汇总
	 * 
	 * @param params
     *      <tt>custId:String:客户ID</tt><br>
	 * @return
     *      <tt>earnTotalAmount  :String:已赚金额</tt><br>
     *      <tt>exceptTotalAmount:String:待收收益</tt><br>
     *      <tt>investTotalAmount:String:在投金额</tt><br>
     *      <tt>tradeTotalAmount :String:投资总金额</tt><br>
	 * @throws SLException
	 */
	public Map<String, Object> queryMyDisperseIncome(Map<String, Object> params)throws SLException;
	
	/**
	 * 查询所有优选计划列表
	 * 
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo queryAllShowLoanList(Map<String, Object> params)throws SLException;
	
	/**
	 * 查询商户还款列表
	 *
	 * @author  wangjf
	 * @date    2016年12月20日 下午3:55:22
	 * @param params
	 * @return
	 */
	public Page<Map<String, Object>> queryThirdCompanyRepaymentList(
			Map<String, Object> params);
	
	
	/**
	 * 债权转让列表查询(转让中债权列表)
	 *
	 * @author  wangjf
	 * @date    2016-12-26 16:56:52
	 * @param params
     *      <tt>custId:String:客户ID</tt><br>
     *      <tt>start :String:起始值</tt><br>
     *      <tt>length:String:长度</tt><br>
	 * @return
     *      <tt>transferApplyId:String:转让申请ID</tt><br>
     *      <tt>disperseId     :String:散标主键</tt><br>
     *      <tt>loanTitle      :String:借款名称</tt><br>
     *      <tt>loanCode       :String: 债权编号</tt><br>
     *      <tt>yearRate       :String:借款年利率</tt><br>
     *      <tt>remainTerm     :String:剩余期限</tt><br>
     *      <tt>transferApplyId:String:转让申请Id</tt><br>
     *      <tt>holdValue      :String:当前债权价值</tt><br>
     *      <tt>tradeAmount    :String:转让金额</tt><br>
     *      <tt>transferEndDate:String:转让结束日期</tt><br>
	 * @throws SLException
	 */
	public Page<Map<String, Object>> queryMyCreditTransferingList(Map<String, Object> params)  throws SLException;
	
	/**
	 * 债权转让列表查询(转出的债权列表)
	 *
	 * @author  wangjf
	 * @date    2016-12-26 16:56:52
	 * @param params
     *      <tt>custId:String:客户ID</tt><br>
     *      <tt>start :String:起始值</tt><br>
     *      <tt>length:String:长度</tt><br>
	 * @return
     *      <tt>transferId      :String:转让记录表ID</tt><br>
     *      <tt>disperseId      :String:散标主键</tt><br>
     *      <tt>loanTitle       :String:借款名称</tt><br>
     *      <tt>loanCode        :String: 债权编号</tt><br>
     *      <tt>yearRate        :String:借款年利率</tt><br>
     *      <tt>transferApplyId :String:转让申请Id</tt><br>
     *      <tt>transferAmount  :String:转出金额</tt><br>
     *      <tt>transferValue   :String:转出价值</tt><br>
     *      <tt>transferInterest:String:转让收益</tt><br>
     *      <tt>investEndDate   :String:到期日期</tt><br>
	 * @throws SLException
	 */
	public Page<Map<String, Object>> queryMyCreditBeTransferedList(Map<String, Object> params)  throws SLException;

	/**
	 * 查看转让详细信息（转让中债权列表-查看）
	 * @author  wangjf
	 * @date    2016-12-26 16:56:52
	 * @param params
     *      <tt>transferApplyId:String:转让申请Id(转让中债权\转出的债权)</tt><br>
	 * @return
     *      <tt>transferApplyId  :String:转让申请Id</tt><br>
     *      <tt>nowLoanValue     :String:当前债权价值</tt><br>
     *      <tt>remainPrincipal  :String:剩余本金</tt><br>
     *      <tt>remainTerm       :String:剩余期限(月)</tt><br>
     *      <tt>exceptRepayAmount:String:待收本息</tt><br>
     *      <tt>tradeScale       :String:转让比例</tt><br>
     *      <tt>discountScale    :String:折价比例</tt><br>
     *      <tt>transferAmount   :String:转让金额</tt><br>
     *      <tt>transferExpense  :String:转让费用</tt><br>
     *      <tt>exceptAmount     :String:预计到账金额（收入金额）</tt><br>
     *      <tt>tradeEndDate     :String:转让结束日期</tt><br>
	 * @throws SLException
	 */
	public Object queryMyCreditTransferingDetail(Map<String, Object> params);

	/**
	 * 查看转让详细信息（转出的债权列表-查看）
	 * @author  wangjf
	 * @date    2016-12-26 16:56:52
	 * @param params
     *      <tt>transferId:String:转让记录表ID</tt><br>
	 * @return
     *      <tt>transferValue    :String:转出债权价值</tt><br>
     *      <tt>remainTerm       :String:剩余期限(月)</tt><br>
     *      <tt>exceptRepayAmount:String:待收本息</tt><br>
     *      <tt>tradeAmount      :String:转让金额</tt><br>
     *      <tt>transferExpense  :String:转让费用</tt><br>
     *      <tt>exceptAmount     :String:预计到账金额</tt><br>
     *      <tt>tradeDate        :String:成交时间</tt><br>
     *      <tt>profitAmount     :String:盈亏</tt><br>
	 * @throws SLException
	 */
	public Object queryMyCreditBeTransferedDetail(Map<String, Object> params);
	
	/**
	 * 查询所有债权转让列表
	 * 
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo queryAllShowTransferList(Map<String, Object> params)throws SLException;
	
	/**
	 * 查询转让协议
	 *
	 * @author  wangjf
	 * @date    2017年1月4日 上午10:54:41
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public Map<String, Object> queryTransferContract(Map<String, Object> params)throws SLException;

	/**
	 * 善融贷接口
	 */
	public Map<String, Object> queryFinancingContract(Map<String, Object> params)throws SLException;
	
	/**
	 * 善意贷接口
	 */
	public Map<String, Object> queryFingertipContract(Map<String, Object> params)throws SLException;

	/**
	 * @desc 批量修改封闭天数(批量修改转让设置)
	 * @author fengyl
	 * **/
	public boolean batchModifyTransferSeatTerm(final List<String> loanIds,
			String seatTerm,String userId) throws SLException;
	
	public List<Map<String, Object>> queryWaitingAdvanceList(Map<String, Object> params)throws SLException;

	/**
	 * 批量修改领取状态
	 * @param loanCodes
	 * @return
	 */
	public boolean batchModifyReceiveStaus(List<String> loanCodes,boolean receiveFlag,String userId);
	
	/**
	 * 根据转让申请表主键查询标的信息
	 * @param applyId
	 * @return
	 */
	public Map<String, Object> queryLoanInfoByApplyId(String applyId);

	/**
	 * 
	 * <查询散标已回收本金、累计收益>
	 * <功能详细描述>
	 *
	 * @param params
	 * @return [参数说明]
	 * @return Map<String,Object> [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	public Map<String,Object> queryEarnTotalPrincipal(Map<String, Object> params);
	/**
	 * 
	 * <体验标的累计收益、待收收益>
	 * <功能详细描述>
	 *
	 * @param params
	 * @return [参数说明]
	 * @return Map<String,Object> [返回类型说明]
	 * @see [类、类#方法、类#成员]
	 */
	public Map<String,Object> queryEarnAndExceptTotalExperience (Map<String, Object> params);
	
	public List<String> findListReserveLoan();
	
	public String findReserveLoan(String loanId);
	
	public String findInfoToAutoInvest(String loanId);
}
