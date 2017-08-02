package com.slfinance.shanlincaifu.repository.custom;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.slfinance.exception.SLException;



/**   
 * 
 * 散标管理数据访问接口 
 * @author  lyy
 * @version $Revision:1.0.0, $Date: 2016年11月25日 下午16:31:46 $ 
 */
public interface LoanManagerRepositoryCustom{

	
	/**     
	 * 借款管理-投资记录列表查询
	 * @date 2016年11月25日下午16:25:59
	 * @author lyy
	 * @param param Map
	 *      <tt>start               :String:起始值</tt><br>
     *      <tt>length              :String:长度</tt><br>
     *      <tt>loanCode            :String:借款编号(可以为空)</tt><br>
     *      <tt>custName            :String:客户姓名(可以为空)</tt><br>
     *      <tt>mobile              :String:手机号码(可以为空)</tt><br>
     *      <tt>investStatus        :String:投资状态(可以为空)</tt><br>
     *      <tt>investDateStart     :String:投资时间-区间头(可以为空)</tt><br>
     *      <tt>investDateEnd       :String:投资时间-区间末(可以为空)</tt><br>
     *      <tt>investStartDateStart:String:起息日期(借款信息表)-区间头(可以为空)</tt><br>
     *      <tt>investStartDateEnd  :String:起息日期(借款信息表)-区间末(可以为空)</tt><br>
     *      <tt>expireDateStart     :String:到期日期-区间头(可以为空)</tt><br>
     *      <tt>expireDateEnd       :String:到期日期-区间末(可以为空)</tt><br>
	 * @return 
     *      <tt>custName         :String:客户姓名</tt><br>
     *      <tt>mobile           :String:手机号码</tt><br>
     *      <tt>investAmount     :String:投资金额（元）</tt><br>
     *      <tt>loanCode         :String:借款编号</tt><br>
     *      <tt>investDate       :String:投资时间</tt><br>
     *      <tt>investStartDate  :String:起息日期(借款信息表)</tt><br>
     *      <tt>expireDate       :String:到期日期</tt><br>
     *      <tt>holdScale        :String:当前持比(借款信息表)</tt><br>
     *      <tt>investStatus     :String:投资状态</tt><br>
	 */
	Page<Map<String, Object>> queryInvestListForPageMap(Map<String, Object> param);

	/**
	 * 借款管理-投资记录列表查询-金额汇总
	 * @return investTotalAmount:String:投资金额
	 */
	BigDecimal queryInvestTotalAmount(Map<String, Object> param);
	
	/**
	 * 转让记录列表-汇总数据查询
	 * @date 2016年11月25日下午16:25:59
	 * @author lyy
	 * @return  <tt>transferCount</tt><br>
	 *			<tt>transferTotalAmount</tt><br>
	 *			<tt>transferTotalValue</tt><br>
	 */
	Map<String, Object> queryLoanTransferTotalAmount(Map<String, Object> param);
	/**
	 * 转让记录列表查询
	 * @date 2016年11月25日下午16:25:59
	 * @author lyy
	 * @param Map<String, Object>
     *      <tt>start            :String:起始值</tt><br>
     *      <tt>length           :String:长度</tt><br>
     *      <tt>receiveCustName  :String:受让人(可以为空)</tt><br>
     *      <tt>receiveCustMobile:String:受让人手机号码(可以为空)</tt><br>
     *      <tt>senderCustName   :String:转让人(可以为空)</tt><br>
     *      <tt>senderCustMobile :String:转让人手机号码(可以为空)</tt><br>
     *      <tt>loanCode         :String:借款编号(可以为空)</tt><br>
     *      <tt>investStatus     :String:投资状态(可以为空)</tt><br>
     *      <tt>createDateStart  :String:购买日期-区间头(可以为空)</tt><br>
     *      <tt>createDateEnd    :String:购买日期-区间末可以为空)</tt><br>
     *      <tt>expireDateStart  :String:到期日期-区间头(可以为空)</tt><br>
     *      <tt>expireDateEnd    :String:到期日期-区间末(可以为空)</tt><br>
	 * @return Page < Map < String, Object > >.list<br>
     *      <tt>receiveCustName    :String:受让人(债权转让表)</tt><br>
     *      <tt>receiveCustMobile  :String:手机号码</tt><br>
     *      <tt>tradeAmount        :String:受让价格（元）</tt><br>
     *      <tt>tradeValue         :String:受让价值（元） (转让比例*债权PV)</tt><br>
     *      <tt>loanCode           :String:借款编号</tt><br>
     *      <tt>senderCustName     :String:转让人(债权转让表)</tt><br>
     *      <tt>senderCustMobile   :String:手机号码</tt><br>
     *      <tt>createDate         :String:购买日期(债权转让表)</tt><br>
     *      <tt>expireDate         :String:到期日期</tt><br>
     *      <tt>investStatus       :String:投资状态</tt><br>
	 */
	Page<Map<String, Object>> queryLoanTransferListForPageMap(
			Map<String, Object> param);

	/**
	 * 借款信息列表查询
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 * @param params
     *      <tt>start          :String:起始值</tt><br>
     *      <tt>length         :String:长度</tt><br>
     *      <tt>loanCode       :String:借款编号(可以为空)</tt><br>
     *      <tt>custName       :String:客户姓名(可以为空)</tt><br>
     *      <tt>mobile         :String:手机号码(可以为空)</tt><br>
     *      <tt>credentialsCode:String:证件号码(可以为空)</tt><br>
     *      <tt>loanTerm       :String:借款期限(可以为空)</tt><br>
     *      <tt>loanType       :String:借款类型(可以为空)</tt><br>
     *      <tt>loanStatus     :String:借款状态(可以为空)</tt><br>
     *      <tt>companyName    :String:公司名称(可以为空)</tt><br>
     *      <tt>repaymentMethod:String:还款方式(可以为空)</tt><br>
     *      <tt>createDateStart:String:申请日期-区间头(可以为空)</tt><br>
     *      <tt>createDateEnd  :String:申请日期-区间末(可以为空)</tt><br>
	 * @return
     *      <tt>data           :String:List<Map<String,Object>></tt><br>
     *      <tt>loanId         :String:借款信息表主键Id</tt><br>
     *      <tt>loanCode       :String:借款编号</tt><br>
     *      <tt>custName       :String:客户姓名</tt><br>
     *      <tt>mobile         :String:手机号码</tt><br>
     *      <tt>credentialsCode:String:证件类型</tt><br>
     *      <tt>loanType       :String:借款类型</tt><br>
     *      <tt>loanAmount     :String:借款金额（元）</tt><br>
     *      <tt>loanTerm       :String:借款期限</tt><br>
     *      <tt>yearIrr        :String:借款利率 （详细表）</tt><br>
     *      <tt>repaymentMethod:String:还款方式</tt><br>
     *      <tt>loanStatus     :String:借款状态</tt><br>
     *      <tt>createDate     :String:申请日期</tt><br>
	 * @throws SLException
	 */
	Page<Map<String, Object>> queryLoanInfoList(Map<String, Object> param);
	
	
	/**
	 * 借款信息列表查询
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 * @param params
     *      <tt>start          :String:起始值</tt><br>
     *      <tt>length         :String:长度</tt><br>
     *      <tt>loanCode       :String:借款编号(可以为空)</tt><br>
     *      <tt>custName       :String:客户姓名(可以为空)</tt><br>
     *      <tt>mobile         :String:手机号码(可以为空)</tt><br>
     *      <tt>credentialsCode:String:证件号码(可以为空)</tt><br>
     *      <tt>loanTerm       :String:借款期限(可以为空)</tt><br>
     *      <tt>loanType       :String:借款类型(可以为空)</tt><br>
     *      <tt>loanStatus     :String:借款状态(可以为空)</tt><br>
     *      <tt>repaymentMethod:String:还款方式(可以为空)</tt><br>
     *      <tt>createDateStart:String:申请日期-区间头(可以为空)</tt><br>
     *      <tt>createDateEnd  :String:申请日期-区间末(可以为空)</tt><br>
	 * @return
     *      <tt>loanInfoCount       :String:统计笔数</tt><br>
     *      <tt>loanInfoAmountCount :String:统计金额</tt><br>
	 * @throws SLException
	 */
	Map<String, Object> queryLoanInfoStatistics(Map<String, Object> param);
	
	/**
	 * 领取列表页面金额查询
	 * @param param
	 * @return
	 */
	Map<String, Object> queryLoanInfoReceives(Map<String, Object> param);
	
	/**
	 * 借款信息详情
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 * @param params
     *      <tt>loanId:String:借款信息表主键Id</tt><br>
	 * @return
     *      <tt>loanCode       :String:借款编号</tt><br>
     *      <tt>loanType       :String:借款类型</tt><br>
     *      <tt>loanDesc       :String:借款用途</tt><br>
     *      <tt>repaymentMethod:String:还款方式</tt><br>
     *      <tt>loanAmount     :String:借款金额</tt><br>
     *      <tt>loanTerm       :String:借款期限</tt><br>
     *      <tt>yearIrr        :String:借款利率 （详细表）</tt><br>
     *      <tt>rasieEndDate   :String:募集日期</tt><br>
     *      <tt>publishDate    :String:发布日期</tt><br>
     *      <tt>investStartDate:String:起息日期</tt><br>
     *      <tt>investEndDate  :String:到期日期</tt><br>
	 * @throws SLException
	 */
	List<Map<String, Object>> queryLoanBasicInfoByLoanId(
			Map<String, Object> params);
	
	/**
	 * 借款人信息
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 * @param params
     *      <tt>loanId:String:借款信息表主键Id</tt><br>
	 * @return
     *      <tt>custName       :String:姓名</tt><br>
     *      <tt>custGender     :String:性别</tt><br>
     *      <tt>age            :String:年龄(根据身份证算)</tt><br>
     *      <tt>jobType        :String:职业类别</tt><br>
     *      <tt>custEducation  :String:学历</tt><br>
     *      <tt>marriageState  :String:婚否</tt><br>
     *      <tt>credentialsType:String:证件类型</tt><br>
     *      <tt>credentialsCode:String:证件号码</tt><br>
	 * @throws SLException
	 */
	List<Map<String, Object>> queryLoanerInfoByLoanId(Map<String, Object> params);
	
	/**
	 * 散标投资列表查询ByApp
	 * @date 2016年11月25日下午16:25:59
	 * @author lyy
	 * @param param Map<String, Object>
     *      <tt>start      :String:起始值</tt><br>
     *      <tt>length     :String:长度</tt><br>
     *      <tt>orderBy    :String:排序字段</tt><br>
     *      <tt>loanType   :String:借款类型</tt><br>
     *      <tt>minTerm    :String:最小投标期限</tt><br>
     *      <tt>maxTerm    :String:最大投标期限</tt><br>
     *      <tt>minYearRate:String:最小年利率</tt><br>
     *      <tt>maxYearRate:String:最大年利率</tt><br>
     * @return ResultVo.data.data :List<Map<String,Object>></tt><br>
     *      <tt>disperseId     :String:散标主键</tt><br>
     *      <tt>disperseType   :String:散标名称 </tt><br>
     *      <tt>loanUse:借款用途 </tt><br>
     *      <tt>loanUserSex:性别 </tt><br>
     *      <tt>loanUserAge:年龄 </tt><br>
     *      <tt>loanUserCity:省市 </tt><br>
     *      <tt>description:说明             </tt><br>
     *      <tt>yearRate       :String:年化收益率</tt><br>
     *      <tt>typeTerm       :String:项目期限(月)</tt><br>
     *      <tt>remainAmount   :String:剩余金额</tt><br>
     *      <tt>investScale    :String:已投百分比</tt><br>
     *      <tt>investMinAmount:String:起投金额</tt><br>
     *      <tt>repaymentMethod:String:还款方式</tt><br>
     *      <tt>security       :String:安全保障</tt><br>
     *      <tt>disperseStatus :String:散标状态</tt><br>
	 */
	Page<Map<String, Object>> queryDisperseList(Map<String, Object> param);

	/**
	 * 散标投资列表查询ByApp
	 * @date 2016年11月25日下午16:25:59
	 * @author lyy
	 * @param param Map<String, Object>
     *      <tt>start      :String:起始值</tt><br>
     *      <tt>length     :String:长度</tt><br>
     *      <tt>orderBy    :String:排序字段</tt><br>
     *      <tt>loanType   :String:借款类型</tt><br>
     *      <tt>minTerm    :String:最小投标期限</tt><br>
     *      <tt>maxTerm    :String:最大投标期限</tt><br>
     *      <tt>minYearRate:String:最小年利率</tt><br>
     *      <tt>maxYearRate:String:最大年利率</tt><br>
     * @return ResultVo.data.data :List<Map<String,Object>></tt><br>
     *      <tt>disperseId     :String:散标主键</tt><br>
     *      <tt>disperseType   :String:散标名称 </tt><br>
     *      <tt>loanUse:借款用途 </tt><br>
     *      <tt>loanUserSex:性别 </tt><br>
     *      <tt>loanUserAge:年龄 </tt><br>
     *      <tt>loanUserCity:省市 </tt><br>
     *      <tt>description:说明             </tt><br>
     *      <tt>yearRate       :String:年化收益率</tt><br>
     *      <tt>typeTerm       :String:项目期限(月)</tt><br>
     *      <tt>remainAmount   :String:剩余金额</tt><br>
     *      <tt>investScale    :String:已投百分比</tt><br>
     *      <tt>investMinAmount:String:起投金额</tt><br>
     *      <tt>repaymentMethod:String:还款方式</tt><br>
     *      <tt>security       :String:安全保障</tt><br>
     *      <tt>disperseStatus :String:散标状态</tt><br>
	 */
	Page<Map<String, Object>> queryDisperseListInSpecialChannel(Map<String, Object> param);

	/**
	 * 散标投资列表forJob
	 * @date 2016年11月25日下午16:25:59
	 * @author lyy
	 * @param param Map<String, Object>
     *      <tt>minTerm    :String:最小投标期限</tt><br>
     *      <tt>maxTerm    :String:最大投标期限</tt><br>
     *      <tt>minYearRate:String:最小年利率</tt><br>
     *      <tt>maxYearRate:String:最大年利率</tt><br>
     * @return ResultVo.data.data :List<Map<String,Object>></tt><br>
     *      <tt>disperseId     :String:散标主键</tt><br>
     *      <tt>disperseType   :String:散标名称 </tt><br>
     *      <tt>loanUse:借款用途 </tt><br>
     *      <tt>loanUserSex:性别 </tt><br>
     *      <tt>loanUserAge:年龄 </tt><br>
     *      <tt>loanUserCity:省市 </tt><br>
     *      <tt>description:说明             </tt><br>
     *      <tt>yearRate       :String:年化收益率</tt><br>
     *      <tt>typeTerm       :String:项目期限(月)</tt><br>
     *      <tt>remainAmount   :String:剩余金额</tt><br>
     *      <tt>investScale    :String:已投百分比</tt><br>
     *      <tt>investMinAmount:String:起投金额</tt><br>
     *      <tt>repaymentMethod:String:还款方式</tt><br>
     *      <tt>security       :String:安全保障</tt><br>
     *      <tt>disperseStatus :String:散标状态</tt><br>
	 */
	List<Map<String, Object>> queryDisperseListForJob(Map<String, Object> param);

	/**
	 * 散标投资详情ByApp
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 * @param params
     *      <tt>disperseId:String:散标主键</tt><br>
	 * @return
     *      <tt>disperseId       :String:散标主键</tt><br>
     *      <tt>disperseType     :String:散标名称</tt><br>
     *      <tt>loanUse          :String:借款用途</tt><br>
     *      <tt>loanUserSex      :String:性别</tt><br>
     *      <tt>loanUserAge      :String:年龄</tt><br>
     *      <tt>loanUserCity     :String:省市</tt><br>
     *      <tt>remainTime       :String:剩余时间</tt><br>
     *      <tt>description      :String:说明</tt><br>
     *      <tt>yearRate         :String:年化收益率</tt><br>
     *      <tt>typeTerm         :String:项目期限(月)</tt><br>
     *      <tt>totalAmount      :String:项目总额</tt><br>
     *      <tt>remainAmount     :String:剩余金额</tt><br>
     *      <tt>investScale      :String:已投百分比</tt><br>
     *      <tt>investMinAmount  :String:起投金额</tt><br>
     *      <tt>increaseAmount   :String:递增金额</tt><br>
     *      <tt>repaymentMethod  :String:还款方式</tt><br>
     *      <tt>loanNo           :String:借款编号</tt><br>
     *      <tt>transferCondition:String:转让条件</tt><br>
     *      <tt>security         :String:安全保障</tt><br>
     *      <tt>disperseStatus   :String:散标状态</tt><br>
     *      <tt>publishDate      :String:发布借款日期</tt><br>
     *      <tt>interestStartDate:String:开始计息日期</tt><br>
     *      <tt>expireDate       :String:借款到期日期</tt><br>
	 * @throws SLException
	 */
	List<Map<String, Object>> queryDisperseDetail(Map<String, Object> params);

	/**
	 * 审核日志
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 * @param params
     *      <tt>loanId:String:借款信息表主键Id</tt><br>
	 * @return
     *      <tt>data       :String:List<Map<String,Object>>（BAO_T_LOG_INFO）</tt><br>
     *      <tt>auditDate  :String:审核时间</tt><br>
     *      <tt>auditUser  :String:审核人员</tt><br>
     *      <tt>auditStatus:String:审核结果</tt><br>
     *      <tt>auditMemo  :String:审核备注</tt><br>
	 * @throws SLException
	 */
	List<Map<String, Object>> queryLoanAuditInfoByLoanId(
			Map<String, Object> params);

	/**
	 * 还款计划
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 * @param params
     *      <tt>loanId:String:借款信息表主键Id</tt><br>
	 * @return
     *      <tt>data                :String:List<Map<String,Object>></tt><br>
     *      <tt>expectRepaymentDate :String:还款日期(yyyy-MM-dd)</tt><br>
     *      <tt>repaymentStatus     :String:还款状态</tt><br>
     *      <tt>repaymentTotalAmount:String:应还本息和</tt><br>
     *      <tt>repaymentPrincipal  :String:应还本金</tt><br>
     *      <tt>repaymentInterest   :String:应还利息</tt><br>
     *      <tt>penaltyAmount       :String:提前还款补偿金</tt><br>
	 * @throws SLException
	 */
	List<Map<String, Object>> queryRepaymentPlanByLoanId(
			Map<String, Object> params);

	/**
	 * 投资记录
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 * @param params
     *      <tt>loanId:String:借款信息表主键Id</tt><br>
	 * @return
     *      <tt>data        :String:List<Map<String,Object>></tt><br>
     *      <tt>custName    :String:投资人</tt><br>
     *      <tt>investAmount:String:投资金额（元）</tt><br>
     *      <tt>investDate  :String:投资日期</tt><br>
	 * @throws SLException
	 */
	Page<Map<String, Object>> queryInvestInfoByLoanId(Map<String, Object> params);
	
	/**
	 * 转让记录
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 * @param params
     *      <tt>loanId:String:借款信息表主键Id</tt><br>
	 * @return
     *      <tt>data             :String:List<Map<String,Object>></tt><br>
     *      <tt>receiveCustName  :String:受让人</tt><br>
     *      <tt>tradeValue:String:受让债权价值（元）</tt><br>
     *      <tt>tradeAmount      :String:受让价格（元）</tt><br>
     *      <tt>senderCustName   :String:转让人</tt><br>
     *      <tt>transferDate     :String:转让日期</tt><br>
	 * @throws SLException
	 */
	Page<Map<String, Object>> queryLoanTransferByLoanId(
			Map<String, Object> params);

	/**
	 * 借款人详情ByApp
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 * @param params
     *      <tt>disperseId:String:散标主键</tt><br>
	 * @return
     *      <tt>name     :String:姓名</tt><br>
     *      <tt>sex      :String:性别</tt><br>
     *      <tt>age      :String:年龄</tt><br>
     *      <tt>marriage :String:婚否</tt><br>
     *      <tt>education:String:学历</tt><br>
     *      <tt>jobType  :String:职业类型</tt><br>
     *      <tt>cardType :String:证件类型</tt><br>
     *      <tt>cardNo   :String:证件号码</tt><br>
	 * @throws SLException
	 */
	List<Map<String, Object>> queryDisperseLoanUser(Map<String, Object> params);

	/**
	 * 投资记录ByApp
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 * @param params
     *      <tt>disperseId:String:散标主键</tt><br>
     *      <tt>start     :String:起始值</tt><br>
     *      <tt>length    :String:长度</tt><br>
	 * @return
     *      <tt>investor    :String:投资人</tt><br>
     *      <tt>investAmount:String:投资金额（元）</tt><br>
     *      <tt>investDate  :String:投资日期</tt><br>
	 * @throws SLException
	 */
	Page<Map<String, Object>> queryDisperseInvestRecord(
			Map<String, Object> params);

	/**
	 * 还款计划ByApp
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 * @param params
     *      <tt>disperseId:String:散标主键</tt><br>
	 * @return
     *      <tt>paymentDate     :String:还款日期</tt><br>
     *      <tt>paymentStatus   :String:还款状态</tt><br>
     *      <tt>principalPayment:String:应还本息</tt><br>
     *      <tt>punishPayment   :String:应还罚息</tt><br>
	 * @throws SLException
	 */
	List<Map<String, Object>> queryDispersePaymentPlan(
			Map<String, Object> params);

	/**
	 * 历史借款
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 */
	int queryLoanHisInfoCount(Map<String, Object> params);

	/**
	 * 历史借款
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 */
	BigDecimal queryLoanHisInfoAmount(Map<String, Object> params);

	/**
	 * 历史借款
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 */
	BigDecimal queryLoanHisInfoPay(Map<String, Object> params);

	/**
	 * 债权投资列表查询
	 *
	 * @author  wangjf
	 * @date    2016-12-26 16:56:52
	 * @param params
     *      <tt>start  :String:起始值</tt><br>
     *      <tt>length :String:</tt><br>
     *      <tt>orderBy:String:排序字段（可以为空）</tt><br>
     *      <tt>isRise :String:升or降（可以为空）</tt><br>
	 * @return
     *      <tt>transferApplyId   :String:转让申请ID</tt><br>
     *      <tt>disperseId        :String:散标主键</tt><br>
     *      <tt>loanTitle         :String:借款名称</tt><br>
     *      <tt>yearRate          :String: 借款年利率</tt><br>
     *      <tt>tradeValue        :String: 债权价值</tt><br>
     *      <tt>tradeAmount       :String: 转让金额</tt><br>
     *      <tt>nextPayDate       :String: 距下次还款</tt><br>
     *      <tt>remainInvestAmount:String:剩余可投金额</tt><br>
     *      <tt>remainTerm        :String:剩余期限</tt><br>
     *      <tt>investScale       :String:进度</tt><br>
     *      <tt>transferStatus    :String:转让状态</tt><br>
	 * @throws SLException
	 */
	Page<Map<String, Object>> queryCreditList(Map<String, Object> params);

	/**
	 * 债权投资列表查询（善意贷特殊渠道）
	 *
	 * @author  wangjf
	 * @date    2016-12-26 16:56:52
	 * @param params
     *      <tt>start  :String:起始值</tt><br>
     *      <tt>length :String:</tt><br>
     *      <tt>orderBy:String:排序字段（可以为空）</tt><br>
     *      <tt>isRise :String:升or降（可以为空）</tt><br>
	 * @return
     *      <tt>transferApplyId   :String:转让申请ID</tt><br>
     *      <tt>disperseId        :String:散标主键</tt><br>
     *      <tt>loanTitle         :String:借款名称</tt><br>
     *      <tt>yearRate          :String: 借款年利率</tt><br>
     *      <tt>tradeValue        :String: 债权价值</tt><br>
     *      <tt>tradeAmount       :String: 转让金额</tt><br>
     *      <tt>nextPayDate       :String: 距下次还款</tt><br>
     *      <tt>remainInvestAmount:String:剩余可投金额</tt><br>
     *      <tt>remainTerm        :String:剩余期限</tt><br>
     *      <tt>investScale       :String:进度</tt><br>
     *      <tt>transferStatus    :String:转让状态</tt><br>
	 * @throws SLException
	 */
	Page<Map<String, Object>> queryCreditListInSpecialChannel(Map<String, Object> params);

	/**
	 * 债权投资列表查询ForJob
	 * @author  liyy
	 * @date    2017-3-8 10:24:20
	 * @param params
	 * @return
     *      <tt>transferApplyId   :String:转让申请ID</tt><br>
     *      <tt>disperseId        :String:散标主键</tt><br>
     *      <tt>loanTitle         :String:借款名称</tt><br>
     *      <tt>yearRate          :String: 借款年利率</tt><br>
     *      <tt>tradeValue        :String: 债权价值</tt><br>
     *      <tt>tradeAmount       :String: 转让金额</tt><br>
     *      <tt>nextPayDate       :String: 距下次还款</tt><br>
     *      <tt>remainInvestAmount:String:剩余可投金额</tt><br>
     *      <tt>remainTerm        :String:剩余期限</tt><br>
     *      <tt>investScale       :String:进度</tt><br>
     *      <tt>transferStatus    :String:转让状态</tt><br>
	 * @throws SLException
	 */
	List<Map<String, Object>> queryCreditListForJob(Map<String, Object> params);

	/**
	 * 债权转让详情
	 * @author  wangjf
	 * @date    2016-12-26 16:56:52
	 * @param params
     *      <tt>transferApplyId:String:转让申请ID</tt><br>
	 * @return
     *      <tt>transferApplyId  :String:转让申请ID</tt><br>
     *      <tt>disperseId       :String:散标主键</tt><br>
     *      <tt>loanTitle        :String:借款名称</tt><br>
     *      <tt>transferCode     :String: 转让编号</tt><br>
     *      <tt>remainTime       :String:剩余时间</tt><br>
     *      <tt>yearRate         :String:年化收益率</tt><br>
     *      <tt>remainTerm       :String:剩余期限</tt><br>
     *      <tt>tradeAmount      :String:转让总额</tt><br>
     *      <tt>tradeScale       :String:转让总比</tt><br>
     *      <tt>remainAmount     :String:剩余金额</tt><br>
     *      <tt>investScale      :String:已投百分比</tt><br>
     *      <tt>tradeValue       :String:债权价值</tt><br>
     *      <tt>interestTime     :String:计息日期</tt><br>
     *      <tt>repaymentMethod  :String:还款方式</tt><br>
     *      <tt>nextTermPay      :String:下期还款</tt><br>
     *      <tt>transferCondition:String:转让条件</tt><br>
     *      <tt>security         :String:安全保障</tt><br>
     *      <tt>loanStatus       :String:债权状态</tt><br>
     *      <tt>transferStartDate:String:转让开始时间（发布日期）</tt><br>
     *      <tt>transferEndDate  :String:转让结束日期</tt><br>
     *      <tt>serverDate       :String:系统当前时间</tt><br>
     *      <tt>totalAmount      :String:项目总额（总本金）</tt><br>
     *      <tt>totalValue       :String:项目总值（总价值）</tt><br>
     *      <tt>totalInterest    :String:总利息</tt><br>
	 * @throws SLException
	 */
	List<Map<String, Object>> queryCreditDetail(Map<String, Object> params);
	
	/**
	 * 查询转让申请的还款计划
	 *
	 * @author  wangjf
	 * @date    2017年1月5日 上午10:48:41
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> queryCreditPaymentPlan(Map<String, Object> params);
	
	/**
	 * 资产信息列表（债权列表）
	 * @author  guoyk
	 * @date    2017-02-24 9:48:18
	 * @param params
     *      <tt>loanId:String:借款信息表主键Id</tt><br>
     *      <tt>start:String:起始值</tt><br>
     *      <tt>length:String:长度</tt><br>
	 * @return
     *      <tt>iTotalDisplayRecords:String: 总条数</tt><br>
     *      <tt>data                :String:资产信息列表:List<Map<String, Object>></tt><br>
     *      <tt>custName            :String:借款人姓名</tt><br>
     *      <tt>credentialsCode     :String:借款人身份证号码</tt><br>
     *      <tt>loanAmount          :String:借款金额</tt><br>
     *      <tt>investEndDate       :String:借款到期日</tt><br>
     *      <tt>repaymentMethod     :String:还款方式</tt><br>
     *      <tt>loanUse             :String:借款用途</tt><br>
	 * @throws SLException
	 */
	public Page<Map<String, Object>> queryAssetListByLoanId(Map<String, Object> params);
	
	/**
	 * 查询自动投标
	 * @author guoyk
	 * @date 2017-3-08
	 * @param params
	 * 	  <tt>start :String:起始值</tt><br>
     *    <tt>length:String:长度</tt><br>
	 * @return
	 *    <tt>totalAmount    :String:当前自动投标金额</tt><br>
     *    <tt>data           :String:List<Map<String,Object>></tt><br>
     *    <tt>custName       :String:用户昵称（用户的名称）</tt><br>
     *    <tt>availableAmount:String:账户余额（元）</tt><br>
     *    <tt>yearRateMin    :String:最低年化利率</tt><br>
     *    <tt>termMax		 :String:投资期限上限（月）</tt><br>
     * @throws SLException
	 */
	public Page<Map<String, Object>> queryAutoInvestList(Map<String, Object> params);
	
	/**
	 * 查询自动投标-可用金额汇总
	 * @return totalAmount:String:当前自动投标可用总金额
	 */
	public List<Map<String, Object>> queryAutoInvestTotalAmount(Map<String, Object> param);
	/**
	 * 查询自动转让
	 * @author fengyl
	 * @date 2017-3-12
	 * @param params
	 * 	  <tt>start :String:起始值</tt><br>
     *    <tt>length:String:长度</tt><br>
     *    <tt>transferDateBegin:String:转让开始时间（可选）</tt><br>
     *    <tt>transferDateEnd:String:转让结束时间（可选）</tt><br>
	 * @return
	 *    <tt>totalAmount    :String:当前自动投标金额</tt><br>
     *    <tt>data           :String:List<Map<String,Object>></tt><br>
     *    <tt>custName       :String:用户昵称（用户的名称）</tt><br>
     *    <tt>loanTile       :String:项目名称</tt><br>
     *    <tt>canTransferAmount:String:可转让余额（元）</tt><br>
     *    <tt>yearRate    :String:年化利率</tt><br>
     *    <tt>typeTerm		 :String:期限（月）</tt><br>
     *    <tt>openDate        :String转让时间</tt><br>
     * @throws SLException
	 */
	public Page<Map<String, Object>> queryAutoTransferList(Map<String, Object> params);
	
	/**
	 * 查询自动转让-可用金额汇总
	 * @return totalAmount:String:当前自动转让可用金额
	 */
	public BigDecimal queryAutoTransferTotalAmount(Map<String, Object> param) ;	
		

	/**
	 * 查询转让审核列表
	 * @author guoyk
	 * @date 2017年3月13日 
	 * @param params
	 *      <tt>start      :String:起始值</tt><br>
     *      <tt>length     :String:长度</tt><br>
     *      <tt>auditStatus:String:审核状态（可选）</tt><br>
	 * @return
	 *      <tt>iTotalDisplayRecords:String: 总条数</tt><br>
     *      <tt>data                :String:List<Map<String,Object>></tt><br>
     *      <tt>transferApplyId     :String:转让申请ID</tt><br>
     *      <tt>loanId              :String:借款ID</tt><br>
     *      <tt>loanCode            :String:借款编号</tt><br>
     *      <tt>loanType            :String:借款类型</tt><br>
     *      <tt>tradeAmount         :String: 转让金额</tt><br>
     *      <tt>remainTerm          :String:剩余期限</tt><br>
     *      <tt>yearReate           :String:借款年利率</tt><br>
     *      <tt>applyDate           :String:申请日期</tt><br>
     *      <tt>auditStatus         :String:审核状态）</tt><br> 
	 * @throws SLException
	 */
	Page<Map<String, Object>> queryAuditTransferList(Map<String, Object> params);
	/**
	 * 新手标查询列表
	 * @author  fengyl
	 * @date    2017-3-28 
	 * @param params
     *      <tt>typeName:String:散标类型名称</tt><br>
	 * @return
     *      <tt>disperseId    :String:散标主键</tt><br>
     *      <tt>disperseType  :String:散标名称</tt><br>
     *      <tt>loanUse:借款用途</tt><br>
     *      <tt>loanUserSex:性别</tt><br>
     *      <tt>loanUserAge:年龄</tt><br>
     *      <tt>loanUserCity:省市</tt><br>
     *      <tt>description:说明</tt><br>
     *      <tt>yearRate      :String:年化收益率</tt><br>
     *      <tt>typeTerm      :String:项目期限(月)</tt><br>
     *      <tt>investScale   :String:已投百分比</tt><br>
     *      <tt>disperseStatus:String:散标状态</tt><br>
	 */
	Page<Map<String, Object>> queryNewerFlagList(Map<String, Object> params);

	/***
	 * 修改智能投顾前台展示状态
	 * @author  guoyk
	 * @date    2017-05-26
	 */
	public int updateISRunAutoInvest();
	
	/**
	 * 领取列表查询
	 * @author  zhangyb
	 * @date    2017-06-12 10:01:18
	 * @param params
     *      <tt>start          :String:起始值</tt><br>
     *      <tt>length         :String:长度</tt><br>
     *      <tt>loanCode       :String:借款编号(可以为空)</tt><br>
     *      <tt>custName       :String:客户姓名(可以为空)</tt><br>
     *      <tt>mobile         :String:手机号码(可以为空)</tt><br>
     *      <tt>credentialsCode:String:证件号码(可以为空)</tt><br>
     *      <tt>loanTerm       :String:借款期限(可以为空)</tt><br>
     *      <tt>loanType       :String:借款类型(可以为空)</tt><br>
     *      <tt>loanStatus     :String:借款状态(可以为空)</tt><br>
     *      <tt>repaymentMethod:String:还款方式(可以为空)</tt><br>
     *      <tt>createDateStart:String:申请日期-区间头(可以为空)</tt><br>
     *      <tt>createDateEnd  :String:申请日期-区间末(可以为空)</tt><br>
     *      <tt>auditTime       :String:审核日期(可以为空)</tt><br>
     *      <tt>auditUser       :String:审核人(可以为空)</tt><br>
     *      <tt>receiveStatus   :String:领取状态(可以为空)</tt><br>
	 * @return
     *      <tt>data           :String:List<Map<String,Object>></tt><br>
     *      <tt>loanId         :String:借款信息表主键Id</tt><br>
     *      <tt>loanCode       :String:借款编号</tt><br>
     *      <tt>custName       :String:客户姓名</tt><br>
     *      <tt>mobile         :String:手机号码</tt><br>
     *      <tt>credentialsCode:String:证件类型</tt><br>
     *      <tt>loanType       :String:借款类型</tt><br>
     *      <tt>loanAmount     :String:借款金额（元）</tt><br>
     *      <tt>loanTerm       :String:借款期限</tt><br>
     *      <tt>yearIrr        :String:借款利率 （详细表）</tt><br>
     *      <tt>repaymentMethod:String:还款方式</tt><br>
     *      <tt>loanStatus     :String:借款状态</tt><br>
     *      <tt>createDate     :String:申请日期</tt><br>
     *      <tt>auditTime       :String:审核日期(可以为空)</tt><br>
	 * @throws SLException
	 */
	Page<Map<String, Object>> queryReceiveList(Map<String, Object> param);

}
