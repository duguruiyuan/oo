package com.slfinance.shanlincaifu.service;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;
import java.util.List;
import java.util.Map;

public interface LoanManagerService extends BeanSelfAware {

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
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数")
	})
	public ResultVo queryBusinessManageList(Map<String, Object> params) throws SLException;
	
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
	@Rules(rules = { 
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空") 
	})
	public ResultVo queryMyTotalInvest(Map<String, Object> params) throws SLException;
	
	/**
	 * 债权收益查询
	 * 
	 * @author zhiwen_feng
	 * @date 2016-11-29
	 * @param params
	 * 		<tt>custId:String:客户ID</tt><br>
	 * @return Map<String, Object>
     *      <tt>transferTotalValue   :String:转让总价值</tt><br>
     *      <tt>transferTotalAmount  :String:转让总金额</tt><br>
     *      <tt>transferTotalInterest:String:转让总收益</tt><br>
     *      <tt>transferTotalExpense :String:转让总费用</tt><br>
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空") 
	})
	public ResultVo queryMyCreditIncome(Map<String, Object> params) throws SLException;
	
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
     *      <tt>creditNo      :String:债权编号</tt><br>
     *      <tt>yearRate      :String:年化收益率</tt><br>
     *      <tt>investlAmount :String:投资金额</tt><br>
     *      <tt>typeTerm      :String:项目期限(月)</tt><br>
     *      <tt>investScale   :String:已投百分比</tt><br>
     *      <tt>disperseStatus:String:散标状态</tt><br>
     *      <tt>disperseDate  :String:投资时间</tt><br>
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数")
	})
	public ResultVo queryMyDisperseList(Map<String, Object> params) throws SLException;
	
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
	@Rules(rules = { 
			@Rule(name = "disperseId", required = true, requiredMessage = "散标主键不能为空"),
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空") 
	})
	public ResultVo queryMyDisperseDetail(Map<String, Object> params)throws SLException;
	
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
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数"),
			@Rule(name = "disperseId", required = true, requiredMessage = "散标主键不能为空"),
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空") 
	})
	public ResultVo queryMyDispersePaybackPlan(Map<String, Object> params)throws SLException;
	
	/**
	 *  发布
	 *  
	 *  @author zhiwen_feng
	 *  @date 2016-11-30
	 * @param params
     *      <tt>loanId                  :String:借款信息表主键Id</tt><br>
     *      <tt>auditList               :String:List<Map<String,Object>> 审核信息</tt><br>
     *      <tt>审核ID                    :String:      auditId</tt><br>
     *      <tt>List<Map<String,Object>>:String:      attachmentList</tt><br>
     *      <tt>attachmentId            :String:         附件ID</tt><br>
     *      <tt>attachmentType          :String:         附件类型</tt><br>
     *      <tt>attachmentName          :String:         附件名称</tt><br>
     *      <tt>storagePath             :String:         存储路径</tt><br>
	 * @return ResultVo
	 * @throws SLException
	 */
	public ResultVo publishLoanInfo(Map<String, Object> params)throws SLException;
	
	/**
	 * 强制流标
	 * 
	 * @author zhiwen_feng
	 * @date 2016-11-30
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo forcebidders(Map<String, Object> params)throws SLException;
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
     * @return ResultVo
     *      <tt>investCount      :String:投资笔数</tt><br>
     *      <tt>investTotalAmount:String:投资金额</tt><br>
     *      
     *      <tt>data             :List<Map<String,Object>></tt><br>
     *      <tt>custName         :String:客户姓名</tt><br>
     *      <tt>mobile           :String:手机号码</tt><br>
     *      <tt>investAmount     :String:投资金额（元）</tt><br>
     *      <tt>loanCode         :String:借款编号</tt><br>
     *      <tt>investDate       :String:投资时间</tt><br>
     *      <tt>investStartDate  :String:起息日期(借款信息表)</tt><br>
     *      <tt>expireDate       :String:到期日期</tt><br>
     *      <tt>holdScale        :String:当前持比(借款信息表)</tt><br>
     *      <tt>investStatus     :String:投资状态</tt><br>
     * 
	 */
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数")
	})
	public ResultVo queryInvestList(Map<String, Object> param);
	
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
	 * @return ResultVo.data
	 *      <tt>transferCount      :String:投资笔数</tt><br>
     *      <tt>transferTotalAmount:String:受让价格</tt><br>
     *      <tt>transferTotalValue :String:受让价值</tt><br>
     *      data        :List<Map<String,Object>></tt><br>
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
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数")
	})
	public ResultVo queryLoanTransferList(Map<String, Object> param);
	
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
     *      <tt>auditDateStart :String:审核日期-区间头(可以为空)</tt><br>
     *      <tt>auditDateEnd   :String:审核日期-区间末(可以为空)</tt><br>
     *      <tt>receiveUser    :String:领取人(可以为空)</tt><br>
     *      <tt>receiveStatus  :String:领取状态(可以为空)</tt><br>
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
     *      <tt>receiveStatus  :String:领取状态</tt><br>
     *      <tt>auditTime      :String:审核日期</tt><br>
     *      <tt>receiveUser    :String:领取人员</tt><br>
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数")
	})
	public ResultVo queryLoanInfoList(Map<String, Object> params);
	

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
     *      <tt>serviceName    :String:债权转让人姓名</tt><br>
     *      <tt>serviceCode    :String:债权转让人证件号码</tt><br>
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "loanId", required = true, requiredMessage = "借款信息表主键不能为空") 
	})
	public ResultVo queryLoanBasicInfoByLoanId(Map<String, Object> params);
	
	/**
	 * 资产信息列表（债权列表）
	 * @author  guoyk
	 * @date    2017-02-24 9:48:18
	 * @param params
     *      <tt>loanId:String:借款信息表主键Id</tt><br>
     *      <tt>start :int:起始值</tt><br>
     *      <tt>length:int:长度</tt><br>
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
	@Rules(rules = { 
			@Rule(name = "loanId", required = true, requiredMessage = "借款信息表主键不能为空"), 
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数")
	})
	public ResultVo queryAssetListByLoanId(Map<String, Object> params);
	
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
	@Rules(rules = { 
			@Rule(name = "loanId", required = true, requiredMessage = "借款信息表主键不能为空") 
	})
	public ResultVo queryLoanerInfoByLoanId(Map<String, Object> params);
	
	/**
	 * 散标投资精选列表ByApp
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
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
	public ResultVo queryPriorityDisperseList(Map<String, Object> params);
	
	/**
	 * 债权转让精选列表ByApp
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 * @param params
     *      <tt>typeName:String:债权转让类型名称</tt><br>
	 * @return
     *      <tt>transferApplyId      :String:转让申请ID</tt><br>
     *      <tt>disperseId           :String:散标主键</tt><br>
     *      <tt>loanTitle            :String:借款名称</tt><br>
     *      <tt>yearRate             :String:年化收益率</tt><br>
     *      <tt>yearRate             :String: 借款年利率</tt><br>
     *      <tt>tradeValue           :String: 债权价值</tt><br>
     *      <tt>tradeAmount          :String: 转让金额</tt><br>
     *      <tt>nextPayDate          :String: 距下次还款</tt><br>
     *      <tt>remainderInvestAmount:String:剩余可投金额</tt><br>
     *      <tt>nextPayDate          :String: 距下次还款</tt><br>
     *      <tt>typeTerm             :String:剩余期限</tt><br>
     *      <tt>investScale          :String:进度</tt><br>
     *      <tt>transferStatus       :String:转让状态</tt><br>
	 * @throws SLException
	 */
	public ResultVo queryPriorityCreditList(Map<String, Object> params) throws SLException;
	
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
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数")
	})
	public ResultVo queryDisperseList(Map<String, Object> param);

	/**
	 * 散标投资列表查询ByApp（用于善意贷特殊渠道购买）
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
	@Rules(rules = {
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"),
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数")
	})
	public ResultVo queryDisperseListInSpecialChannel(Map<String, Object> param);

	/**
	 * 散标投资列表查询forPC
	 * @param param
	 * @return
	 */
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数")
	})
	public ResultVo queryDisperseListForPc (Map<String, Object> param)throws SLException;

	/**
	 * 散标投资列表查询forPC（用于善意贷特殊渠道购买）
	 * @param param
	 * @return
	 */
	@Rules(rules = {
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"),
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数")
	})
	public ResultVo queryDisperseListForPcInSpecialChannel(Map<String, Object> param)throws SLException;

	
	/**
	 * 散标投资列表forJob
	 * @date 2017年3月8日 9:39:00
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
	public List<Map<String, Object>> queryDisperseListForJob(Map<String, Object> param);
	
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
     *      <tt>totalInterest    :String:总利息            </tt><br>
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "disperseId", required = true, requiredMessage = "散标主键不能为空") 
	})
	public ResultVo queryDisperseDetail(Map<String, Object> params) ;

	/**
	 * 历史借款
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 * @param params
     *      <tt>credentialsCode:String:证件号码</tt><br>
	 * @return
     *      <tt>applyLoanCount :String:申请借款</tt><br>
     *      <tt>loanedCount    :String:成功借款</tt><br>
     *      <tt>payoffCount    :String:还清笔数</tt><br>
     *      <tt>loanTotalAmount:String:借款总额</tt><br>
     *      <tt>payAmount      :String:待还本息</tt><br>
     *      <tt>overdueCount   :String:逾期笔数</tt><br>
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "loanId", required = true, requiredMessage = "信息表主键不能为空")
	})
	public ResultVo queryLoanHisInfo(Map<String, Object> params);
	
	/**
	 * 历史借款ByApp
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 * @param params
     *      <tt>disperseId:String:证件号码</tt><br>
	 * @return
     *      <tt>applyLoanCount :String:申请借款</tt><br>
     *      <tt>loanedCount    :String:成功借款</tt><br>
     *      <tt>payoffCount    :String:还清笔数</tt><br>
     *      <tt>loanTotalAmount:String:借款总额</tt><br>
     *      <tt>payAmount      :String:待还本息</tt><br>
     *      <tt>overdueCount   :String:逾期笔数</tt><br>
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "disperseId", required = true, requiredMessage = "信息表主键不能为空")
	})
	public ResultVo queryDisperseHistoryLoan(Map<String, Object> params);
	
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
	@Rules(rules = { 
			@Rule(name = "loanId", required = true, requiredMessage = "借款信息表不能为空") 
	})
	public ResultVo queryLoanAuditInfoByLoanId(Map<String, Object> params);
	
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
	@Rules(rules = { 
			@Rule(name = "loanId", required = true, requiredMessage = "借款信息表不能为空") 
	})
	public ResultVo queryRepaymentPlanByLoanId(Map<String, Object> params);
	
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
	@Rules(rules = { 
			@Rule(name = "loanId", required = true, requiredMessage = "借款信息表不能为空"),
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数")
	})
	public ResultVo queryInvestInfoByLoanId(Map<String, Object> params);
	
	/**
	 * 转让记录
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 * @param params
     *      <tt>loanId:String:借款信息表主键Id</tt><br>
	 * @return
     *      <tt>data           :String:List<Map<String,Object>></tt><br>
     *      <tt>receiveCustName:String:受让人</tt><br>
     *      <tt>tradeLoanAmount:String:受让债权价值（元） (tradeAmount)</tt><br>
     *      <tt>tradeAmount    :String:受让价格（元）</tt><br>
     *      <tt>senderCustName :String:转让人</tt><br>
     *      <tt>createDate     :String:转让日期</tt><br>
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "loanId", required = true, requiredMessage = "借款信息表不能为空"),
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数")
	})
	public ResultVo queryLoanTransferByLoanId(Map<String, Object> params);
	
	/**
	 * 审核
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 * @param params
     *      <tt>loanId     :String:借款信息表主键Id</tt><br>
     *      <tt>userId     :String:操作人ID</tt><br>
     *      <tt>auditStatus:String:审核状态</tt><br>
     *      <tt>aduitMemo  :String:审核备注</tt><br>
	 * @return
     *      ResultVo
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "loanId", required = true, requiredMessage = "借款信息表不能为空"),
			@Rule(name = "userId", required = true, requiredMessage = "操作人不能为空"), 
			@Rule(name = "auditStatus", required = true, requiredMessage = "审核状态不能为空")
	})
	public ResultVo auditLoan(Map<String, Object> params) throws SLException;
	
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
	@Rules(rules = { 
			@Rule(name = "disperseId", required = true, requiredMessage = "借款信息表不能为空")
	})
	public ResultVo queryDisperseLoanUser(Map<String, Object> params);
	
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
	@Rules(rules = { 
			@Rule(name = "disperseId", required = true, requiredMessage = "借款信息表不能为空"),
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数")
	})
	public ResultVo queryDisperseInvestRecord(Map<String, Object> params);
	
	/**
	 * 转让记录ByApp
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 * @param params
     *      <tt>disperseId:String:散标主键</tt><br>
     *      <tt>start     :String:起始值</tt><br>
     *      <tt>length    :String:长度</tt><br>
	 * @return
     *      <tt>acceptor        :String:受让人</tt><br>
     *      <tt>acceptLoanAmount:String:受让债权金额</tt><br>
     *      <tt>acceptAmount    :String:受让金额</tt><br>
     *      <tt>transferUser    :String:转让人</tt><br>
     *      <tt>transferDate    :String:转让日期</tt><br>
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "disperseId", required = true, requiredMessage = "借款信息表不能为空"),
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数")
	})
	public ResultVo queryDisperseTransferRecord(Map<String, Object> params);
	
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
     *      
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "disperseId", required = true, requiredMessage = "借款信息表不能为空"),
	})
	public ResultVo queryDispersePaymentPlan(Map<String, Object> params);
	
	/**
	 * 购买散标投资ByApp
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 * @param params
     *      <tt>disperseId :String:散标主键</tt><br>
     *      <tt>custId     :String:客户ID</tt><br>
     *      <tt>tradeAmount:String:投资金额</tt><br>
     *      <tt>channelNo  :String:渠道号（可以为空）</tt><br>
     *      <tt>meId       :String:设备ID（可以为空）</tt><br>
     *      <tt>meVersion  :String:设备版本号（可以为空）</tt><br>
     *      <tt>appSource  :String:设备来源（可以为空）</tt><br>
     *      <tt>ipAddress  :String:ip地址（可以为空）</tt><br>
	 * @return
     *      ResultVo
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "disperseId", required = true, requiredMessage = "借款信息主键不能为空!"),
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空!")
//			@Rule(name = "tradeAmount", required = true, requiredMessage = "投资金额不能为空!", number = true, numberMessage = "投资金额必须是数字！", digist =true, digistMessage = "投资金额必须是整数！")
	})
	public ResultVo buyDispersion(Map<String, Object> params) throws SLException;

	/**
	 * 购买散标投资ByApp -- 使用红包
	 * @date    2017-06-28 17:40:18
	 * @param params
	 *      <tt>disperseId :String:散标主键</tt><br>
	 *      <tt>custId     :String:客户ID</tt><br>
	 *      <tt>tradeAmount:String:投资金额</tt><br>
	 *      <tt>channelNo  :String:渠道号（可以为空）</tt><br>
	 *      <tt>meId       :String:设备ID（可以为空）</tt><br>
	 *      <tt>meVersion  :String:设备版本号（可以为空）</tt><br>
	 *      <tt>appSource  :String:设备来源（可以为空）</tt><br>
	 *      <tt>ipAddress  :String:ip地址（可以为空）</tt><br>
	 *       <tt>custActivityId	:String 客户活动ID（可以为空）	</tt>
	 * @return
	 *      ResultVo
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "disperseId", required = true, requiredMessage = "借款信息主键不能为空!"),
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空!")
	})
	ResultVo buyDispersionExt(Map<String, Object> params) throws SLException;
	
	/**
	 * 认证信息
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 * @param params
     *      <tt>loanId:String:借款信息表主键Id</tt><br>
	 * @return
     *      <tt>data       :String:List<Map<String,Object>></tt><br>
     *      <tt>applyType  :String:审核项目</tt><br>
     *      <tt>auditStatus:String:状态</tt><br>
     *      <tt>auditDate  :String:审核日期</tt><br>
	 * @throws SLException
	 */
	public ResultVo queryAuthInfoByLoanId(Map<String, Object> params);
	/**
	 * 认证信息ByApp
	 * @author  lyy
	 * @date    2016-11-28 16:48:18
	 * @param params
     *      <tt>disperseId:String:散标主键</tt><br>
	 * @return
     *      <tt>checkItem  :String:审核项目</tt><br>
     *      <tt>checkStatus:String:状态</tt><br>
     *      <tt>checkDate  :String:审核日期</tt><br>
	 * @throws SLException
	 */
	public ResultVo queryDisperseCheckInfo(Map<String, Object> params);
	
	/**
	 * 附件列表(供前端使用)
	 *
	 * @author  wangjf
	 * @date    2016年12月1日 上午11:06:34
	 * @param params
	 * 		<tt>disperseId:String:散标主键</tt><br>
	 * @return
     *      <tt>data          :String:List<Map<String,Object>></tt><br>
     *      <tt>attachmentId  :String:附件ID</tt><br>
     *      <tt>attachmentType:String:附件类型</tt><br>
     *      <tt>attachmentName:String:附件名称</tt><br>
     *      <tt>storagePath   :String:存储路径</tt><br>	
	 */
	public ResultVo queryDisperseAttachmentList(Map<String, Object> params);
		
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
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数")
	})
	public ResultVo queryMyCreditPaybackPlan(Map<String, Object> params) throws SLException; 
	
	/**
	 * 附件信息查询
	 * 
	 * @author zhiwen_feng
	 * @date 2016-12-01
	 * @param params
     *      <tt>loanId:String:借款信息表主键Id</tt><br>
	 * @return
     *      <tt>data          :list:List<Map<String,Object>> 审核信息</tt><br>
     *      <tt>审核ID         :String:      auditId</tt><br>
     *      <tt>审核名称                    :String:      auditName</tt><br>
     *      <tt>审核人                        :String:      auditUser</tt><br>
     *      <tt>审核时间                    :String:      auditDate</tt><br>
     *      <tt>List<Map<String,Object>>:String:      attachmentList</tt><br>
     *      <tt>attachmentId            :String:         附件ID</tt><br>
     *      <tt>attachmentType          :String:         附件类型</tt><br>
     *      <tt>attachmentName          :String:         附件名称</tt><br>
     *      <tt>storagePath             :String:         存储路径</tt><br>
	 * @throws SLException
	 */
	public ResultVo queryAttachmentByLoanId(Map<String, Object> params) throws SLException;
	
	/**
	 * 编辑附件查询附件列表
	 * @author liyy
	 * @date 2017-2-7
	 * @param params
     *      <tt>loanId:String:借款信息表主键Id</tt><br>
	 * @return
     *      <tt>data          :list:List<Map<String,Object>> 审核信息</tt><br>
     *      <tt>审核ID         :String:      auditId</tt><br>
     *      <tt>审核名称                    :String:      auditName</tt><br>
     *      <tt>审核人                        :String:      auditUser</tt><br>
     *      <tt>审核时间                    :String:      auditDate</tt><br>
     *      <tt>List<Map<String,Object>>:String:      attachmentList</tt><br>
     *      <tt>attachmentId            :String:         附件ID</tt><br>
     *      <tt>attachmentType          :String:         附件类型</tt><br>
     *      <tt>attachmentName          :String:         附件名称</tt><br>
     *      <tt>storagePath             :String:         存储路径</tt><br>
	 * @throws SLException
	 */
	public ResultVo queryAttachmentByLoanIdInEdit(Map<String, Object> params) throws SLException;
	
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
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数")
	})
	public ResultVo queryRecentlyRepaymentList(Map<String, Object> params) throws SLException;
	
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
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数")
	})
	public ResultVo queryOverdueDataList(Map<String, Object> params) throws SLException;
	
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
     *      <tt>repaymentPlanId        :String:还款计划表主键Id</tt><br>
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
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数")
	})
	public ResultVo queryAlreadyRepayList(Map<String, Object> params) throws SLException;
	
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
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数")
	})
	public ResultVo queryLendMoneyList(Map<String, Object> params) throws SLException;
	
	/**
	 * 批量放款
	 * 
	 * @author zhiwen_feng
	 * @date 2016-12-01
	 * @param params
     *      <tt>LoadIds:String:List<String></tt><br>
     *      <tt>loanId :String:借款信息表主键Id</tt><br>	 * 
	 * @return
	 * @throws SLException
	 */
	public ResultVo batchLending(Map<String, Object> params) throws SLException;


	/**
	 * APP首页
	 *
	 * @author  wangjf
	 * @date    2016年12月3日 上午9:56:41
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo queryPriority(Map<String, Object> params) throws SLException;
	/**
	 * APP首页(包含新手标)
	 *
	 * @author  fengyl
	 * @date    2017年3月28日 
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo queryPriorityNewerFlag(Map<String, Object> params) throws SLException;
	
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
	@Rules(rules = { 
			@Rule(name = "custId", required = true, requiredMessage =  "客户ID不能为空")
	})
	public ResultVo queryMyDisperseIncome(Map<String, Object> params)throws SLException;
	
	/**
	 * 优选项目查询BySalesMan
	 * @param params
	 * start:起始值
	 * length:长度
	 * productType：产品类型（优选项目/转让专区）
	 * @return
     *      <tt>disperseId     :String:散标主键</tt><br>
     *      <tt>disperseType   :String:散标名称</tt><br>
     *      <tt>loanUse        :String:借款用途</tt><br>
     *      <tt>loanUserSex    :String:性别</tt><br>
     *      <tt>loanUserAge    :String:年龄</tt><br>
     *      <tt>loanUserCity   :String:省市</tt><br>
     *      <tt>description    :String:说明</tt><br>
     *      <tt>yearRate       :String:年化收益率</tt><br>
     *      <tt>typeTerm       :String:项目期限</tt><br>
     *      <tt>typeUnit       :String: 期限单位</tt><br>
     *      <tt>remainAmount   :String:剩余金额</tt><br>
     *      <tt>investScale    :String:已投百分比</tt><br>
     *      <tt>investMinAmount:String:起投金额</tt><br>
     *      <tt>repaymentMethod:String:还款方式</tt><br>
     *      <tt>security       :String:安全保障</tt><br>
     *      <tt>disperseStatus :String:散标状态</tt><br>
     *      <tt>rebateRatio    :String:折标系数</tt><br>
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "productType", required = true, requiredMessage =  "产品类型不能为空"),
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数")
	})
	public ResultVo queryAwardLoanList(Map<String, Object> params)throws SLException;
	
	/**
	 * 查询推荐项目列表
	 *
	 * @author  wangjf
	 * @date    2016年12月7日 下午5:22:18
	 * @param params
	 * @return
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "ids", required = true, requiredMessage =  "ids不能为空"),
			@Rule(name = "mobile", required = true, requiredMessage =  "手机号不能为空"),
	})
	public ResultVo queryEmployeeLoanList(Map<String, Object> params)throws SLException;
	
	/**
	 * 查看协议
	 * 
	 * @author zhiwen_feng
	 * @date 2016-03-07
	 * @param params
     *      <tt>loanId:String:项目主键</tt><br>
     *      <tt>custId  :String:客户ID</tt><br>
	 * @return ResultVo 
     *      <tt>loanCode    :String:借款编号</tt><br>
     *      <tt>loanCustName	   :String:借款人姓名</tt><br>
     *      <tt>loanCredentialsCode:String:借款人身份证号码</tt><br>
     *      <tt>loanAmount      :String:借款金额</tt><br>
     *      <tt>incomeType     :String:结算方式</tt><br>
     *      <tt>yearRate       :String:年化收益率</tt><br>
     *      <tt>awardRate      :String:奖励利率</tt><br>
     *      <tt>typeTerm       :String:项目期限(月)</tt><br>
     *      <tt>effectDate     :String:生效日期（放款日期）（借款出借日期）</tt><br>
     *      <tt>endDate        :String:到期日期</tt><br>
     *      <tt>monthlyManageAmount:String:月账户管理费</tt><br>
     *      <tt>accountManageAmount:String:平台管理费</tt><br>
     *      <tt>loanDesc       :String:借款用途 </tt><br>
     *      <tt>advancedRepaymentRate:String:提前还款违约金费率 </tt><br>
     *      <tt>overdueRepaymentRate:String:逾期还款罚息费率 </tt><br>
     *      <tt>investorList   :List<Map<String, Object>>:投资人列表
	 *      	<tt>custName       :String:投资用户姓名（若未投资则为空）</tt><br>
	 *      	<tt>credentialsCode:String:投资用户身份证（若未投资则为空）</tt><br>
	 *      	<tt>loginName      :String:投资用户昵称（若未投资则为空）</tt><br>
	 *          <tt>investAmount   :String:投资金额（若未投资则为空）</tt><br>
	 *          <tt>investDate     :String:投资时间（若未投资则为空）</tt><br>
     *      </tt>
     *      	
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "loanId", required = true, requiredMessage = "项目主键不能为空!"),
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空!")
	})
	public ResultVo queryLoanContract(Map<String, Object> params)throws SLException;
	
	/**
	 * 通过债权编号取债权
	 *
	 * @author  wangjf
	 * @date    2016年12月9日 下午6:33:27
	 * @param loanCode
	 * @return
	 */
	public ResultVo findByLoanCode(String loanCode);
	
	/**
	 * 启用/停用放款定时任务
	 * @author  liyy
	 * @date    2016年12月20日 下午6:56:10
	 * @return
	 * @throws SLException
	 */
	public Map<String, Object> getGrantTimerStatus(Map<String, Object> params) throws SLException;
	
	/**
	 * 启用/停用放款定时任务
	 *
	 * @author  wangjf
	 * @date    2016年12月17日 下午4:02:10
	 * @param params
	 * 		<tt>status    :String:状态（启用/停用）</tt><br>
	 * @return
	 * @throws SLException
	 */
	public ResultVo startGrantTimer(Map<String, Object> params)throws SLException;
	
	/**
	 * 判断借款类型、还款方式、还款期限在项目折年系数表中是否为商务（Flag标识），若为商务则表示需要通知。 
	 */
	public boolean needToNotifyBiz(String repaymentMethod, Integer loanTerm, String loanType, String loanUnit);
	
	/**
	 * 企业借款-借款类型
	 * @author  liyy
	 * @date    2016年12月19日 上午11:28:10
	 * @param params
	 * 		<tt>status    :type:类型（传企业）</tt>><br>
	 * @return
	 * @throws SLException
	 */
	public Map<String, Object> queryLoanTypeList(Map<String, Object> params)throws SLException;
	
	/**
	 * 保存企业借款
	 * @author  liyy
	 * @date    2016年12月19日 上午11:28:10
	 * @param params
     *      <tt>loanNo           :String:借款编号</tt><br>
     *      <tt>loanType         :String:借款类型</tt><br>
     *      <tt>custId           :String:公司ID（客户表ID）</tt><br>
     *      <tt>loanDesc         :String:借款标题</tt><br>
     *      <tt>repaymentMethod  :String:还款方式</tt><br>
     *      <tt>protocolType     :String:协议模板</tt><br>
     *      <tt>yearRate         :String:借款年利率</tt><br>
     *      <tt>monthlyManageRate:String:服务费率</tt><br>
     *      <tt>awardRate        :String:奖励利率</tt><br>
     *      <tt>loanAmount       :String:借款金额</tt><br>
     *      <tt>loanTerm         :String:借款期限</tt><br>
     *      <tt>termUnit         :String:借款单位（默认天）</tt><br>
     *      <tt>investMinAmount  :String:起投金额</tt><br>
     *      <tt>increaseAmount   :String:递增金额</tt><br>
     *      <tt>publishDate      :String:发布日期</tt><br>
     *      <tt>rasieDays        :String:募集天数</tt><br>
     *      <tt>loanInfo         :String:项目信息</tt><br>
     *      <tt>auditList        :String:审核信息:List<Map<String,Object>></tt><br>
     *      <tt>auditType        :String:类型</tt><br>
     *      <tt>fileName         :String:文件名称</tt><br>
     *      <tt>filePath         :String:文件路径</tt><br>
	 * @return ResultVo
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "loanCode", required = true, requiredMessage = "借款编号不能为空!"),
			@Rule(name = "loanType", required = true, requiredMessage = "借款类型不能为空!"),
			@Rule(name = "custId", required = true, requiredMessage = "公司ID不能为空!"),
			@Rule(name = "loanDesc", required = true, requiredMessage = "借款标题不能为空!"),
			@Rule(name = "seatTerm", required = true, requiredMessage = "转让设置不能为空!"),
			@Rule(name = "repaymentMethod", required = true, requiredMessage = "还款方式不能为空!"),
			@Rule(name = "protocolType", required = true, requiredMessage = "协议模板不能为空!"),
			@Rule(name = "yearRate", required = true, requiredMessage = "借款年利率不能为空!", number = true, numberMessage = "借款年利率只能是数字"),
			@Rule(name = "manageRate", required = true, requiredMessage = "服务费率不能为空!", number = true, numberMessage = "服务费率只能是数字"),
//			@Rule(name = "awardRate", required = true, requiredMessage = "奖励利率不能为空!", number = true, numberMessage = "奖励利率只能是数字"),
			@Rule(name = "loanAmount", required = true, requiredMessage = "借款金额不能为空!", number = true, numberMessage = "借款金额只能是数字"),
			@Rule(name = "loanTerm", required = true, requiredMessage = "借款期限不能为空!", number = true, numberMessage = "借款期限只能是数字"),
			@Rule(name = "loanUnit", required = true, requiredMessage = "借款单位不能为空!"),
			@Rule(name = "investMinAmount", required = true, requiredMessage = "起投金额不能为空!", number = true, numberMessage = "起投金额只能是数字"),
			@Rule(name = "increaseAmount", required = true, requiredMessage = "递增金额不能为空!", number = true, numberMessage = "递增金额只能是数字"),
//			@Rule(name = "publishDate", required = true, requiredMessage = "发布日期不能为空!"),
			@Rule(name = "rasieDays", required = true, requiredMessage = "募集天数不能为空!"),
//			@Rule(name = "loanInfo", required = true, requiredMessage = "项目信息不能为空!"),
			@Rule(name = "userId", required = true, requiredMessage = "创建人不能为空!"),
//			@Rule(name = "isAllowAutoInvest", required = true, requiredMessage = "是否允许自动投标不能为空!")
			@Rule(name = "manageExpenseDealType", required = true, requiredMessage = "服务费类型不能为空!"),
			@Rule(name = "grantType", required = true, requiredMessage = "生效方式不能为空!")
	})
	public ResultVo saveLoan(Map<String, Object> params)throws SLException;
	
	/**
	 * @author  liyy
	 * @date    2016年12月19日 上午11:28:10
	 */
	@Rules(rules = { 
			@Rule(name = "loanId", required = true, requiredMessage = "借款id不能为空!")
	})
	public ResultVo getLoanAttachment(Map<String, Object> params) throws SLException;

	/**
	 * 获取已募集金额 
	 */
	public ResultVo getAlreadyAmount(Map<String, Object> params);

	Map<String, Object> queryBankData(Map<String, Object> params);
	
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
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数")
	})
	public ResultVo queryCreditList(Map<String, Object> params)  throws SLException;

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
	@Rules(rules = {
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"),
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数")
	})
	public ResultVo queryCreditListInSpecialChannel(Map<String, Object> params)  throws SLException;

	/**
	 * 债权投资列表查询ForJob
	 * @author  liyy
	 * @date    2017-3-8 10:21:52
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
	public List<Map<String, Object>> queryCreditListForJob(Map<String, Object> params)  throws SLException;

	/**
	 * 债权转让详情
	 *
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
	@Rules(rules = { 
			@Rule(name = "transferApplyId", required = true, requiredMessage = "转让申请Id不能为空!")
	})
	public ResultVo queryCreditDetail(Map<String, Object> params)  throws SLException;
	
	/**
	 * 购买债权转让
	 *
	 * @author  wangjf
	 * @date    2016-12-26 16:56:52
	 * @param params
     *      <tt>transferApplyId:String:转让申请ID</tt><br>
     *      <tt>custId         :String:客户ID</tt><br>
     *      <tt>tradeAmount    :String:投资金额</tt><br>
     *      <tt>channelNo      :String:渠道号（可以为空）</tt><br>
     *      <tt>meId           :String:设备ID（可以为空）</tt><br>
     *      <tt>meVersion      :String:设备版本号（可以为空）</tt><br>
     *      <tt>appSource      :String:设备来源（可以为空）</tt><br>
     *      <tt>ipAddress      :String:ip地址（可以为空）</tt><br>
	 * @return
     *      ResultVo
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "transferApplyId", required = true, requiredMessage = "转让申请ID不能为空!"),
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空!"),
			@Rule(name = "tradeAmount", required = true, requiredMessage = "投资金额不能为空!", number = true, numberMessage = "投资金额只能为数字"),
	})
	public ResultVo buyCredit(Map<String, Object> params)  throws SLException;

	/**
	 * 购买债权转让-- 使用红包
	 *
	 * @author  wangjf
	 * @date    2016-12-26 16:56:52
	 * @param params
	 *      <tt>transferApplyId:String:转让申请ID</tt><br>
	 *      <tt>custId         :String:客户ID</tt><br>
	 *      <tt>tradeAmount    :String:投资金额</tt><br>
	 *      <tt>channelNo      :String:渠道号（可以为空）</tt><br>
	 *      <tt>meId           :String:设备ID（可以为空）</tt><br>
	 *      <tt>meVersion      :String:设备版本号（可以为空）</tt><br>
	 *      <tt>appSource      :String:设备来源（可以为空）</tt><br>
	 *      <tt>ipAddress      :String:ip地址（可以为空）</tt><br>
	 * @return
	 *      ResultVo
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "transferApplyId", required = true, requiredMessage = "转让申请ID不能为空!"),
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空!"),
			@Rule(name = "tradeAmount", required = true, requiredMessage = "投资金额不能为空!", number = true, numberMessage = "投资金额只能为数字"),
	})
	ResultVo buyCreditExt(Map<String, Object> params)  throws SLException;
	
	/**
	 * 债权转让列表查询(可转让债权列表)
	 *
	 * @author  wangjf
	 * @date    2016-12-26 16:56:52
	 * @param params
     *      <tt>custId:String:客户ID</tt><br>
     *      <tt>start :String:起始值</tt><br>
     *      <tt>length:String:长度</tt><br>
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
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数"),
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空!"),
	})
	public ResultVo queryMyCreditList(Map<String, Object> params)  throws SLException;
	
	/**
	 * 债权转让列表查询ForJob(可转让债权列表)
	 *
	 * @author  fengyl
	 * @date    2017-3-14 
	 * @param params
     *      <tt>custId:String:客户ID</tt><br>
     *      <tt>limitedTerm:Integer:最低期限(自动转让月份)</tt><br>
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
	public List<Map<String, Object>> queryMyCreditListForJob(Map<String, Object> params)  throws SLException;

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
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数"),
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空!"),
	})
	public ResultVo queryMyCreditTransferingList(Map<String, Object> params)  throws SLException;

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
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数"),
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空!"),
	})
	public ResultVo queryMyCreditBeTransferedList(Map<String, Object> params)  throws SLException;

	/**
	 * 查看转让详细信息（可转让债权列表-转让）
	 *
	 * @author  wangjf
	 * @date    2016-12-26 16:56:52
	 * @param params
     *      <tt>holdId:String:债权持有表ID</tt><br>
	 * @return
     *      <tt>holdId             :String:债权持有表ID</tt><br>
     *      <tt>nowLoanValue       :String:当前债权价值</tt><br>
     *      <tt>remainPrincipal    :String:剩余本金</tt><br>
     *      <tt>remainTerm         :String:剩余期限(月)</tt><br>
     *      <tt>exceptRepayAmount  :String:待收本息</tt><br>
     *      <tt>tradeScale         :String:转让比例（默认100%）</tt><br>
     *      <tt>reducedScale       :String:折价比例（默认100%）</tt><br>
     *      <tt>tradeAmount        :String:转让金额</tt><br>
     *      <tt>transferExpense    :String:转让费用</tt><br>
     *      <tt>exceptArriveAmount :String:预计到账金额</tt><br>
     *      <tt>tradeEndDate       :String:转让结束日期</tt><br>
     *      <tt>transferRate       :String:转让服务费率</tt><br>
     *      <tt>transferDay        :String:转让天数</tt><br>
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "holdId", required = true, requiredMessage = "债权持有Id不能为空!")
	})
	public ResultVo queryMyCreditDetail(Map<String, Object> params) throws SLException;

	/**
	 * 查看转让详细信息（可转让债权列表-转让） 批量统计
	 * @author  lyy
	 * @date    2017-7-1 00:00:0
	 * @param params
     *      <tt>holdId:String:债权持有表ID</tt><br>
	 * @return
     *      <tt>holdId             :String:债权持有表ID</tt><br>
     *      <tt>nowLoanValue       :String:当前债权价值</tt><br>
     *      <tt>remainPrincipal    :String:剩余本金</tt><br>
     *      <tt>remainTerm         :String:剩余期限(月)</tt><br>
     *      <tt>exceptRepayAmount  :String:待收本息</tt><br>
     *      <tt>tradeScale         :String:转让比例（默认100%）</tt><br>
     *      <tt>reducedScale       :String:折价比例（默认100%）</tt><br>
     *      <tt>tradeAmount        :String:转让金额</tt><br>
     *      <tt>transferExpense    :String:转让费用</tt><br>
     *      <tt>exceptArriveAmount :String:预计到账金额</tt><br>
     *      <tt>tradeEndDate       :String:转让结束日期</tt><br>
     *      <tt>transferRate       :String:转让服务费率</tt><br>
     *      <tt>transferDay        :String:转让天数</tt><br>
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "holdIds", required = true, requiredMessage = "债权持有Id数组不能为空!")
	})
	public ResultVo queryMyCreditDetailForBatch(Map<String, Object> params) throws SLException;
	/**
	 * 查看转让详细信息（转让中债权列表-查看）
	 *
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
	@Rules(rules = { 
			@Rule(name = "transferApplyId", required = true, requiredMessage = "转让申请Id不能为空!")
	})
	public ResultVo queryMyCreditTransferingDetail(Map<String, Object> params)  throws SLException;

	/**
	 * 查看转让详细信息（转出的债权列表-查看）
	 *
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
	@Rules(rules = { 
			@Rule(name = "transferId", required = true, requiredMessage = "转让记录Id不能为空!")
	})
	public ResultVo queryMyCreditBeTransferedDetail(Map<String, Object> params)  throws SLException;

	/**
	 * 转让撤销
	 *
	 * @author  wangjf
	 * @date    2016-12-26 16:56:52
	 * @param params
     *      <tt>transferApplyId:String:转让申请Id(转让中债权\转出的债权)</tt><br>
     *      <tt>tradePass      :String:交易密码</tt><br>
     *      <tt>custId         :String:客户ID</tt><br>
	 * @return
     *      ResultVo
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "transferApplyId", required = true, requiredMessage = "转让申请Id不能为空!"),
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空!"),
			@Rule(name = "tradePass", required = true, requiredMessage = "交易密码不能为空!"),
	})
	public ResultVo canceTransferDebt(Map<String, Object> params)  throws SLException;

	
	/**
	 * 批量债权转让
	 * @author  lyy
	 * @date    2017-7-1 00:00:00
	 * @param params
     *      <tt>holdIds      :String:债权持有表IDs</tt><br>
     *      <tt>custId       :String:客户ID</tt><br>
     *      <tt>tradeScale   :String:转让比例</tt><br>
     *      <tt>discountScale:String:折价比例</tt><br>
     *      <tt>tradePass    :String:交易密码</tt><br>
	 * @return
     *      ResultVo
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "holdIds", required = true, requiredMessage = "债权持有ID数组不能为空!"),
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空!"),
			@Rule(name = "tradePass", required = true, requiredMessage = "交易密码不能为空!"),
			@Rule(name = "holdIdsLength", required = true, requiredMessage = "转让数目不能为空!"),
//			@Rule(name = "tradeScale", required = true, requiredMessage = "转让比例不能为空!", number = true, numberMessage = "转让比例只能为数字"),
			@Rule(name = "discountScale", required = true, requiredMessage = "折价比例不能为空!", number = true, numberMessage = "折价比例只能为数字")
	})
	public ResultVo transferDebtForBatch(Map<String, Object> params)  throws SLException;
	/**
	 * 债权转让
	 *
	 * @author  wangjf
	 * @date    2016-12-26 16:56:52
	 * @param params
     *      <tt>holdId       :String:债权持有表ID</tt><br>
     *      <tt>custId       :String:客户ID</tt><br>
     *      <tt>tradeScale   :String:转让比例</tt><br>
     *      <tt>discountScale:String:折价比例</tt><br>
     *      <tt>tradePass    :String:交易密码</tt><br>
	 * @return
     *      ResultVo
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "holdId", required = true, requiredMessage = "债权持有表ID不能为空!"),
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空!"),
			@Rule(name = "tradeScale", required = true, requiredMessage = "转让比例不能为空!", number = true, numberMessage = "转让比例只能为数字"),
			@Rule(name = "discountScale", required = true, requiredMessage = "折价比例不能为空!", number = true, numberMessage = "折价比例只能为数字")
	})
	public ResultVo transferDebt(Map<String, Object> params)  throws SLException;
	
	/**
	 * 查询推荐转让列表
	 *
	 * @author  wangjf
	 * @date    2016年12月7日 下午5:22:18
	 * @param params
	 * @return
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "ids", required = true, requiredMessage =  "ids不能为空"),
			@Rule(name = "mobile", required = true, requiredMessage =  "手机号不能为空"),
	})
	public ResultVo queryEmployeeTransferList(Map<String, Object> params)throws SLException;
	
	
	/**
	 * 查询转让协议
	 *
	 * @author  wangjf
	 * @date    2017年1月4日 上午9:22:33
	 * @param params
	 * @return
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空!")
	})
	public ResultVo queryTransferContract(Map<String, Object> params)throws SLException;
	
	/**
	 * 债权转让--转让记录
	 *
	 * @author  wangjf
	 * @date    2017年1月5日 上午10:35:12
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo queryCreditTransferRecord(Map<String, Object> params)throws SLException;
	
	/**
	 * 债权转让--还款记录
	 *
	 * @author  wangjf
	 * @date    2017年1月5日 上午10:35:51
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo queryCreditPaymentPlan(Map<String, Object> params)throws SLException;
	
	/**
	 * 保存附件-完成编辑
	 * @author  liyy
	 * @date    2017年2月7日 下午15:35:51
	 * @param <tt>loanId         :String:借款信息表主键Id</tt><br>
     *        <tt>auditId        :String:审核ID</tt><br>
     *        <tt>attachmentId   :String:         附件ID(原附件ID)</tt><br>
     *        <tt>attachmentType :String:         附件类型</tt><br>
     *        <tt>attachmentName :String:         附件名称</tt><br>
     *        <tt>storagePath    :String:         存储路径</tt><br>
     *        <tt>showType       :String:         展示类型</tt><br>
	 * @return ResultVo
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "loanId", required = true, requiredMessage = "借款信息表主键Id不能为空!"),
			@Rule(name = "auditId", required = true, requiredMessage = "审核ID不能为空!"),
			@Rule(name = "attachmentId", required = true, requiredMessage = " 附件ID不能为空!"),
			@Rule(name = "attachmentFlag", required = true, requiredMessage = " 附件标识不能为空!")
	})
	public ResultVo saveLoanAttachment(Map<String, Object> params) throws SLException;
	
	/**
	 * 定时发布
	 * 
	 * @author fengyl
	 * @date 2017-2-7
	 * @param params
	 *        <tt>loanId                  :String:借款信息表主键Id</tt><br>
	 *        <tt>publishDate             :String:发布时间</tt><br>
	 * @return ResultVo
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "loanId", required = true, requiredMessage = "借款信息表主键Id不能为空!"),
			@Rule(name = "publishDate", required = true, requiredMessage = "发布时间不能为空!")
	})
	public ResultVo timerPublishLoanInfo(Map<String, Object> params)throws SLException;

	/**
	 * 债权转让及回购协议
	 * @author liyy
	 * @date 2017-2-23
	 * @param params
	 *        <tt>loanId:String:借款信息表主键Id</tt><br>
	 *        <tt>custId:String:客户信息Id</tt><br>
	 * @return ResultVo
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "loanId", required = true, requiredMessage = "借款信息不能为空!"),
			@Rule(name = "custId", required = true, requiredMessage = "客户信息不能为空!")
	})
	public ResultVo queryAssetContract(Map<String, Object> params) throws SLException;
	
	/**
	 * 服务费回款列表查询
	 * @author liyy
	 * @date 2017-2-24
	 * @param params
     *      <tt>start :String:起始值</tt><br>
     *      <tt>length:String:长度</tt><br>
	 * @return ResultVo
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数")
	})
	public ResultVo queryLoanExpenseList(Map<String, Object> params) throws SLException;
	
	/**
	 * 确认回款
	 * @author liyy
	 * @date 2017-2-24
	 * @param params
     *      <tt>loanServicePlanId：借款服务费计划Id</tt><br>
     *      <tt>userId：操作人</tt><br>
	 * @return ResultVo
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "loanServicePlanId", required = true, requiredMessage = "借款服务费计划Id不能为空"), 
			@Rule(name = "userId", required = true, requiredMessage = "操作人不能为空")
	})
	public ResultVo payExpense(Map<String, Object> params) throws SLException;
	
	public ResultVo isExistsLoan(Map<String, Object> params) throws SLException;
	
	/**
	 * 查询善融贷
	 * @author liyy
	 * @date 2017-2-27
	 * @param params
     *      <tt>investId：投资ID </tt><br>
     *      <tt>custId：客户ID</tt><br>
	 * @return ResultVo
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "investId", required = true, requiredMessage = "投资Id不能为空"), 
			@Rule(name = "custId", required = true, requiredMessage = "客户Id不能为空")
	})
	public ResultVo queryFinancingContract(Map<String, Object> params) throws SLException;
	
	/**
	 * 查询善意贷
	 * @author liyy
	 * @date 2017-3-12
	 * @param params
     *      <tt>investId：投资ID </tt><br>
     *      <tt>custId：客户ID</tt><br>
	 * @return ResultVo
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "investId", required = true, requiredMessage = "投资Id不能为空"), 
			@Rule(name = "loanId", required = true, requiredMessage = "借款Id不能为空"), 
			@Rule(name = "custId", required = true, requiredMessage = "客户Id不能为空")
	})
	public ResultVo queryFingertipContract(Map<String, Object> params) throws SLException;
	
	/**
	 * 查询雪澄金服
	 * @author guoyk
	 * @date 2017-7-2
	 * @param params
	 *      <tt>investId：投资ID </tt><br>
	 *      <tt>custId：客户ID</tt><br>
	 * @return ResultVo
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "investId", required = true, requiredMessage = "投资Id不能为空"), 
			@Rule(name = "loanId", required = true, requiredMessage = "借款Id不能为空"), 
			@Rule(name = "custId", required = true, requiredMessage = "客户Id不能为空")
	})
	public ResultVo querySnowOrangeContract(Map<String, Object> params) throws SLException;
	
	/**
	 * 查询协议通用接口
	 * @author liyy
	 * @date 2017-2-27
	 * @param params
     *      <tt>investId：投资ID </tt><br>
     *      <tt>custId：客户ID</tt><br>
	 * @return ResultVo
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "investId", required = true, requiredMessage = "投资Id不能为空"), 
			@Rule(name = "custId", required = true, requiredMessage = "客户Id不能为空")
	})
	public ResultVo queryContract(Map<String, Object> params) throws SLException;
	
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
	@Rules(rules = {
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数")
	})
	public ResultVo queryAutoInvestList(Map<String, Object> params)throws SLException;
	
	/**
	 * 查询自动投标信息
	 * @author fengyl
	 * @date 2017-3-8
	 * @param params
     *      <tt>custId：客户ID</tt><br>
	 * @return ResultVo
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "custId", required = true, requiredMessage = "客户Id不能为空")
	})
	public ResultVo queryAutoInvestInfo(Map<String, Object> params) throws SLException;
	/**
	 * 保存自动投标设置
	 * 
	 * @author fengyl
	 * @date 2017年3月8日
	 * @param params
	 *            Map < String, Object >
	 * @return ResultVo
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "custId", required = true, requiredMessage = "客户Id不能为空"),
			@Rule(name = "limitedYearRate", required = true, requiredMessage = "最低年化利率不能为空"),
			@Rule(name = "limitedTerm", required = true, requiredMessage = "投资期限上限不能为空")
/* 兼容APP版本，暂时不更新 */
//			,@Rule(name = "limitedYearRateMax", required = true, requiredMessage = "最大年化利率不能为空"),
//			@Rule(name = "limitedTermMin", required = true, requiredMessage = "投资期限下限不能为空"),
//			@Rule(name = "repaymentMethod", required = true, requiredMessage = "复投回款类型不能为空"),
//			@Rule(name = "canInvestProduct", required = true, requiredMessage = "复投类型不能为空"),
//			@Rule(name = "keepAvailableAmount", required = true, requiredMessage = "保留余额不能为空")
	})
	public ResultVo saveAutoInvest(Map<String, Object> params)throws SLException;
	/**
	 * 启用/禁用自动投标
	 * 
	 * @author fengyl
	 * @date 2017年3月8日
	 * @param params
	 *            Map < String, Object >
	 * @return ResultVo
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "custId", required = true, requiredMessage = "客户Id不能为空"),
			@Rule(name = "openStatus", required = true, requiredMessage = "开启状态不能为空")
	})
	public ResultVo enableAutoInvest(Map<String, Object> params)throws SLException;
	
	/**
	 * 设置不在提醒
	 * @author liyy
	 * @date 2017年3月25日
	 * @param params
	 * @return ResultVo
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "custId", required = true, requiredMessage = "客户Id不能为空"),
			@Rule(name = "pointStatus", required = true, requiredMessage = "提示状态不能为空")
	})
	public ResultVo setInvestPoint(Map<String, Object> params)throws SLException;
	/**
	 * 查询自动转让
	 * @author fengyl
	 * @date 2017-3-13
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
	@Rules(rules = {
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数")
	})
	public ResultVo queryAutoTransferList(Map<String, Object> params)throws SLException;
	
	/**
	 * 查询自动转让信息
	 * @author fengyl
	 * @date 2017-3-13
	 * @param params
     *      <tt>custId：客户ID</tt><br>
	 * @return ResultVo
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "custId", required = true, requiredMessage = "客户Id不能为空")
	})
	public ResultVo queryAutoTransferInfo(Map<String, Object> params) throws SLException;
	/**
	 * 保存自动转让设置
	 * 
	 * @author fengyl
	 * @date 2017年3月13日
	 * @param params
	 *            Map < String, Object >
	 * @return ResultVo
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "custId", required = true, requiredMessage = "客户Id不能为空"),
			@Rule(name = "termMax", required = true, requiredMessage = "最低期限不能为空")
			/* 兼容APP版本，暂时不更新 */
//			,@Rule(name = "minYearRate", required = true, requiredMessage = "转让标的最低利率不能为空"),
//			@Rule(name = "maxYearRate", required = true, requiredMessage = "转让标的最高利率不能为空"),
//			@Rule(name = "minTerm", required = true, requiredMessage = "转让标的最低期限不能为空"),
//			@Rule(name = "maxTerm", required = true, requiredMessage = "转让标的最高期限不能为空"),
//			@Rule(name = "repaymentMethod", required = true, requiredMessage = "转让标的还款方式不能为空"),
//			@Rule(name = "canTransferProduct", required = true, requiredMessage = "转让标的类型不能为空")
	})
	public ResultVo saveAutoTransfer(Map<String, Object> params)throws SLException;
	/**
	 * 启用/禁用自动转让
	 * 
	 * @author fengyl
	 * @date 2017年3月13日
	 * @param params
	 *            Map < String, Object >
	 * @return ResultVo
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "custId", required = true, requiredMessage = "客户Id不能为空"),
			@Rule(name = "openStatus", required = true, requiredMessage = "开启状态不能为空")
	})
	public ResultVo enableAutoTransfer(Map<String, Object> params)throws SLException;
	
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
	@Rules(rules = {
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数")
	})
	public ResultVo queryAuditTransferList(Map<String, Object> params)throws SLException;
	
	
//	/**
//	 * 批量审核转让
//	 * @author lyy
//	 * @date 2017年7月1日 
//	 * @param
//	 *      <tt>transferApplyIds:String:转让申请IDs</tt><br>
//     *      <tt>auditStatus     :String:审核状态</tt><br>
//     *      <tt>userId          :String:审核人</tt><br>
//	 * @return ResultVo
//	 * @throws SLException
//	 */
//	@Rules(rules = {
//			@Rule(name = "transferApplyIds", required = true, requiredMessage = "转让申请数组不能为空"),
//			@Rule(name = "auditStatus", required = true, requiredMessage = "审核状态不能为空"),
//			@Rule(name = "userId", required = true, requiredMessage = "审核人不能为空"),
//	})
//	public ResultVo auditTransferForBatch(Map<String, Object> params) throws SLException;
	
	/**
	 * 审核转让
	 * @author guoyk
	 * @date 2017年3月13日 
	 * @param
	 *      <tt>transferApplyId:String:转让申请ID</tt><br>
     *      <tt>auditStatus    :String:审核状态</tt><br>
     *      <tt>userId         :String:审核人</tt><br>
	 * @return ResultVo
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "transferApplyId", required = true, requiredMessage = "转让申请ID不能为空"),
			@Rule(name = "auditStatus", required = true, requiredMessage = "审核状态不能为空"),
			@Rule(name = "userId", required = true, requiredMessage = "审核人不能为空"),
	})
	public ResultVo auditTransfer(Map<String, Object> params)throws SLException;
	
	
	
	/**
	 * 批量审核转让
	 * @author lixx
	 * @date 2017年6月28日 
	 * @param
	 *      <tt>transferApplyIds:List<String>:转让申请ID列表</tt><br>
     *      <tt>auditStatus    :String:审核状态</tt><br>
     *      <tt>userId         :String:审核人</tt><br>
     *      <tt>transferRate   :String:转让费率</tt><br>
	 * @return ResultVo
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "transferApplyIds", required = true, requiredMessage = "转让申请ID不能为空"),
			@Rule(name = "auditStatus", required = true, requiredMessage = "审核状态不能为空"),
			@Rule(name = "userId", required = true, requiredMessage = "审核人不能为空"),
			@Rule(name = "transferRate", required = true, requiredMessage = "转让费率不能为空")
	})
	public ResultVo batchAuditTransfer(Map<String, Object> params)throws SLException;
	

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
	@Rules(rules = {
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数")
	})
	public ResultVo queryNewerFlagList(Map<String, Object> params);
	
	/**
	 * 自动发标查询（发标池）
	 * @author  guoyk
	 * @date    2017-4-11 
	 * @param params
     *      <tt>id:String:借款主键</tt><br>
     * @return 
     *      <tt>id					:String:自动发布主键</tt><br>
     *      <tt>debtSource			:String:资产来源</tt><br>
     *      <tt>minTerm				:String:最低期限</tt><br>
     *      <tt>maxTerm 			:String:最高期限</tt><br>
     *      <tt>minYearRate 		:String:最低利率</tt><br>
     *      <tt>maxYearRate 		:String:最高利率</tt><br>
     *      <tt>minRasieDays 		:String:最低募集天数</tt><br>
     *      <tt>maxRasieDays 		:String:最高募集天数</tt><br>
     *      <tt>repaymentMethod	 	:String:还款方式</tt><br>
     *      <tt>minLoanAmount 		:String:最低借款金额</tt><br>
     *      <tt>maxLoanAmount 		:String:最高借款金额</tt><br>
     *      <tt>maxLoanNumber 		:String:标的个数</tt><br>
     *      <tt>minTotalLoanAmount 	:String:最低线上金额</tt><br>
     *      <tt>maxTotalLoanAmount 	:String:最高线上金额</tt><br>
	 */
	@Rules(rules = {
			@Rule(name = "id", required = true, requiredMessage = "自动发布主键不能为空")
	})
	public ResultVo queryAutoPublish(Map<String, Object> params);
	
	/**
	 * 自动发标列表查询
	 * @author  guoyk
	 * @date    2017-4-12 
	 * @return
	 *      <tt>id					:String:自动发布主键</tt><br>
	 *      <tt>debtSourceCh		:String:资产来源(中文)</tt><br>
     *      <tt>debtSource			:String:资产来源</tt><br>
     *      <tt>minTerm				:String:最低期限</tt><br>
     *      <tt>maxTerm 			:String:最高期限</tt><br>
     *      <tt>minYearRate 		:String:最低利率</tt><br>
     *      <tt>maxYearRate 		:String:最高利率</tt><br>
     *      <tt>minRasieDays 		:String:最低募集天数</tt><br>
     *      <tt>maxRasieDays 		:String:最高募集天数</tt><br>
     *      <tt>repaymentMethod	 	:String:还款方式</tt><br>
     *      <tt>minLoanAmount 		:String:最低借款金额</tt><br>
     *      <tt>maxLoanAmount 		:String:最高借款金额</tt><br>
     *      <tt>maxLoanNumber 		:String:标的个数</tt><br>
     *      <tt>minTotalLoanAmount 	:String:最低线上金额</tt><br>
     *      <tt>maxTotalLoanAmount 	:String:最高线上金额</tt><br>
	 */
	public ResultVo queryAutoPublishList();
	
	/**
	 * 保存自动发标规则（发标池）
	 * @author  guoyk
	 * @date    2017-4-11 
	 * @param params
	 *  	<tt>id					:String:主键</tt><br>
	 *   	<tt>userId				:String:用户id</tt><br>
     *      <tt>debtSource			:String:资产来源</tt><br>
     *      <tt>minTerm				:String:最低期限</tt><br>
     *      <tt>maxTerm 			:String:最高期限</tt><br>
     *      <tt>minYearRate 		:String:最低利率</tt><br>
     *      <tt>maxYearRate 		:String:最高利率</tt><br>
     *      <tt>minRasieDays 		:String:最低募集天数</tt><br>
     *      <tt>maxRasieDays 		:String:最高募集天数</tt><br>
     *      <tt>repaymentMethod	 	:String:还款方式</tt><br>
     *      <tt>minLoanAmount 		:String:最低借款金额</tt><br>
     *      <tt>maxLoanAmount 		:String:最高借款金额</tt><br>
     *      <tt>maxLoanNumber 		:String:标的个数</tt><br>
     *      <tt>minTotalLoanAmount 	:String:最低线上金额</tt><br>
     *      <tt>maxTotalLoanAmount 	:String:最高线上金额</tt><br>
     * @return ResultVo
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "userId", required = true, requiredMessage = "用户id不能为空")
	})
	public ResultVo saveAutoPublish(Map<String, Object> params)throws SLException;
	
	/**
	 * 取消自动发标
	 * @author  guoyk
	 * @date    2017-4-12 
	 * @param params
     *      <tt>id:String:自动发布主键</tt><br>
     * @return ResultVo
	 * @throws SLException
	 */
	public ResultVo cancleAutoPublish(Map<String, Object> params)throws SLException;
	
	/**
	 * 附件编辑标识接口
	 * @author  guoyk
	 * @date    2017-4-10 
	 * @param params
     *      <tt>loanId:String:借款主键</tt><br>
     *      <tt>attachmentFlag:String:附件编辑标识</tt><br>
     * @return ResultVo
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "loanId", required = true, requiredMessage = "借款主键不能为空"),
			@Rule(name = "attachmentFlag", required = true, requiredMessage = "附件编辑标识不能为空")
	})
	public ResultVo editAttachmentFlag(Map<String, Object> params)throws SLException;
	
	/**
	 * 批量修改转让设置
	 * @author  fengyl
	 * @date    2017-5-9
	 * @param params
     *      <tt>loanIds:<List>String</tt><br>
     *      <tt>seatTerm:封闭天数</tt><br>
     * @return ResultVo
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "seatTerm", required = true, requiredMessage = "封闭天数不能为空"),
			@Rule(name = "custId", required = true, requiredMessage = "用户id不能为空")
	})
	public ResultVo batchModifyTransferSeatTerm(Map<String, Object> params)throws SLException;
	
	/**
	 * 充值页面设置不在提醒
	 * @author  fengyl
	 * @date    2017-5-20
	 * @param params
     *      <tt>custId		   :String:客户主键</tt><br>
     *      <tt>rechargeAmount :String:封闭天数</tt><br>
     * @return Map<String, Object> 
	 *      <tt>flag 		   :String:是否提示</tt><br>
	 *      <tt>amount         :String:(flag为true时)返回提示金额</tt><br>
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "custId", required = true, requiredMessage = "客户Id不能为空"),
			@Rule(name = "rechargeAmount",required = true, requiredMessage = "充值金额不能为空!", number = true, numberMessage = "充值金额只能是数字")
	})
	public ResultVo queryIsShowAutoInvestRechargeTipDialog(Map<String, Object> params) throws SLException;
	
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
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数"),
			@Rule(name = "userId", required = true, requiredMessage = "用户id不能为空")
	})
	public ResultVo queryReceiveList(Map<String, Object> params);
	
	/**
	 * 批量修改领取状态
	 * @author  zhangyb
	 * @date    2017-06-12
	 * @param params
     *      <tt>loanCode:		<List>String</tt><br>
     *      <tt>receiveFlag:	String</tt><br>
     *      <tt>userId:			String</tt><br>
     * @return ResultVo
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "loanCodes", required = true, requiredMessage = "借款编号不能为空"),
			@Rule(name = "receiveFlag", required = true, requiredMessage = "领取条件不能为空"),
			@Rule(name = "userId", required = true, requiredMessage = "用户id不能为空")
	})
	public ResultVo batchModifyReceiveStaus(Map<String, Object> params)throws SLException;
	
	/**
	 * 保存发布设置
	 * @author  guoyk
	 * @date    2017-06-23
	 * @param params
	 *      <tt>loanId			 :String:借款id</tt><br>
	 *      <tt>userId			 :String:操作人id</tt><br>
	 *      <tt>loanTitle        :String:借款标题</tt><br>
     *      <tt>isNewerFlag      :String:新手标标识</tt><br>
     *      <tt>awardRate        :String:奖励利率</tt><br>
     *      <tt>isAllowAutoInvest:String:是否支持自动投标标识</tt><br>
     *      <tt>isPublishNow     :String:是否立刻发布标识</tt><br>
     *      <tt>specialUsersFlag :String:是否特殊用户</tt><br>
	 * @return ResultVo
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "loanId", required = true, requiredMessage = "借款id不能为空"),
			@Rule(name = "userId", required = true, requiredMessage = "操作人不能为空")
	})
	public ResultVo savePublishInstall(Map<String, Object> params)throws SLException;
	
	
	/**
	 * 转让申请置顶和取消置顶操作
	 * @author  lixx
	 * @date    2017-06-29
	 * @param params
     *      <tt>transferApplyId: String:转让申请ID</tt><br>
     *      <tt>stickyStatus:    String:设置状态(置顶或取消置顶)</tt><br>
     * @return ResultVo
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "transferApplyId", required = true, requiredMessage = "转让申请ID不能为空"),
			@Rule(name = "stickyStatus", required = true, requiredMessage = "设置状态不能为空")
	})
	public ResultVo transferStickyOperation(Map<String, Object> params)throws SLException;
	
	/**
	 * 转让申请上移操作
	 * @author  lixx
	 * @date    2017-06-29
	 * @param params
     *      <tt>transferApplyId: String:转让申请ID</tt><br>
     * @return ResultVo
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "transferApplyId", required = true, requiredMessage = "转让申请ID不能为空"),
	})
	public ResultVo transferMoveOperation(Map<String, Object> params)throws SLException;
	/**
	 * 雪橙借款审核附件
	 * @author  wuxt
	 * @date    2017-07-27
	 * @param params
     *      <tt>loanId: String:借款ID</tt><br>
     * @return ResultVo
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "loanId", required = true, requiredMessage = "借款ID不能为空"),
	})
	public ResultVo getXCFileBrowseUrl(Map<String, Object> params)throws SLException;
}
