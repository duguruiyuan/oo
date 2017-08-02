package com.slfinance.shanlincaifu.service;

import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;

public interface CustManagerService {

	/**
	 * 线下充值申请
	 * 
	 * @author zhangt
	 * @date   2016年2月25日下午7:42:51
	 * @param param
	 *      <tt>tradeFlowId                                      :String:交易过程ID（编辑时非空）</tt><br>
     *      <tt>custId                                           :String:客户ID</tt><br>
     *      <tt>bankId                                           :String:银行ID</tt><br>
     *      <tt>tradeAmount                                      :String:充值金额</tt><br>
     *      <tt>thirdPay                                         :String:划扣公司</tt><br>
     *      <tt>userId                                           :String:创建人</tt><br>
     *      <tt>posList                                          :String:终端列表:List<Map<String, Object>></tt><br>
     *      <tt>终端ID                                             :String:        posId</tt><br>
     *      <tt>终端号                                              :String:        posNo</tt><br>
     *      <tt>参考号                                              :String:        referenceNo</tt><br>
     *      <tt>attachmentList                                   :String:附件列表:List<Map<String, Object>></tt><br>
     *      <tt>attachmentId（若附件没有修改，请将该ID上传，若修改则不上传ID，新增也不上传该字段）:String:          附件ID</tt><br>
     *      <tt>attachmentType                                   :String:         附件类型</tt><br>
     *      <tt>attachmentName                                   :String:         附件名称</tt><br>
     *      <tt>storagePath                                      :String:         存储路径</tt><br>
	 * @return
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空!"),
			@Rule(name = "bankId", required = true, requiredMessage = "银行ID不能为空!"),
			@Rule(name = "tradeAmount", required = true, requiredMessage = "充值金额不能为空!"),
			@Rule(name = "userId", required = true, requiredMessage = "创建人不能为空!")
	})
	public ResultVo saveWealthRechargeByManager(Map<String, Object> param) throws SLException;
	
	/**
	 * 线下充值列表
	 * 
	 * @author zhangt
	 * @date   2016年2月23日下午8:25:01
	 * @param param
	 *      <tt>start           :String:起始值</tt><br>
     *      <tt>length          :String:长度</tt><br>
     *      <tt>custName        :String:用户名（可选）</tt><br>
     *      <tt>credentialsCode :String:证件号码(可选)</tt><br>
     *      <tt>mobile          :String:手机号（可选）</tt><br>
     *      <tt>auditStatus     :String:审核状态（可选）</tt><br>
     *      <tt>beginOperateDate:String:开始创建时间（可选）</tt><br>
     *      <tt>endOperateDate  :String:结束创建时间（可选）</tt><br>
     *      <tt>custManagerId   :String:客户经理ID（可选）</tt><br>
     *      <tt>thirdPay        :String:划扣公司（可选）</tt><br>
	 * @return
	 *      <tt>tradeFlowId    :String:交易过程ID</tt><br>
     *      <tt>custName       :String:用户名</tt><br>
     *      <tt>credentialsCode:String:证件号码</tt><br>
     *      <tt>mobile         :String:手机号</tt><br>
     *      <tt>tradeType      :String:交易类型</tt><br>
     *      <tt>tradeAmount    :String:交易金额</tt><br>
     *      <tt>createUser     :String:创建人</tt><br>
     *      <tt>createDate     :String:创建时间</tt><br>
     *      <tt>auditStatus    :String:审核状态</tt><br>
     *      <tt>thirdPay       :String:划扣公司</tt><br>
	 */
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "起始值不能为空!", number = true, numberMessage = "起始值必须为数字"),
			@Rule(name = "length", required = true, requiredMessage = "长度不能为空!", number = true, numberMessage = "长度必须为数字")
	})
	public ResultVo queryWealthRechargeList(Map<String, Object> param);
	
	/**
	 * 线下充值查看
	 * 
	 * @author zhangt
	 * @date   2016年2月23日下午8:26:34
	 * @param param
	 *      <tt>tradeFlowId:String:交易过程ID</tt><br>
	 * @return
	 *      <tt>tradeFlowId    :String:交易过程ID</tt><br>
     *      <tt>custName       :String:用户名</tt><br>
     *      <tt>credentialsCode:String:证件号码</tt><br>
     *      <tt>mobile         :String:手机号</tt><br>
     *      <tt>registerDate   :String:注册时间</tt><br>
     *      <tt>bankName       :String:银行名称</tt><br>
     *      <tt>bankCardNo     :String:银行卡号</tt><br>
     *      <tt>branchBankName :String:支行名称</tt><br>
     *      <tt>openProvince   :String:开户行所在省</tt><br>
     *      <tt>openCity       :String:开户行所在市</tt><br>
     *      <tt>tradeAmount    :String:充值金额</tt><br>
     *      <tt>thirdPay       :String:划扣公司</tt><br>
     *      <tt>posList        :String:终端列表:List<Map<String, Object>></tt><br>
     *      <tt>终端ID           :String:        posId</tt><br>
     *      <tt>终端号            :String:        posNo</tt><br>
     *      <tt>参考号            :String:        referenceNo</tt><br>
     *      <tt>attachmentList :String:附件列表:List<Map<String, Object>></tt><br>
     *      <tt>attachmentType :String:         附件类型</tt><br>
     *      <tt>attachmentName :String:         附件名称</tt><br>
     *      <tt>storagePath    :String:         存储路径</tt><br>
     *      <tt>auditList      :String:审核列表:List<Map<String, Object>></tt><br>
     *      <tt>审核ID           :String:         auditId</tt><br>
     *      <tt>审核时间           :String:         auditDate</tt><br>
     *      <tt>审核人员           :String:         auditUser</tt><br>
     *      <tt>审核结果           :String:         auditStatus</tt><br>
     *      <tt>审核备注           :String:         auditMemo</tt><br>
	 */
	@Rules(rules = { 
			@Rule(name = "tradeFlowId", required = true, requiredMessage = "交易过程ID不能为空!")
	})
	public ResultVo queryWealthRechargeDetailById(Map<String, Object> param);
	
	/**
	 * 线下充值初审
	 * 
	 * @author zhangt
	 * @date   2016年2月23日下午8:28:05
	 * @param param
	 *      <tt>tradeFlowId:String:交易过程ID</tt><br>
     *      <tt>auditStatus:String:审核状态</tt><br>
     *      <tt>auditMemo  :String: 审核备注</tt><br>
     *      <tt>userId     :String:创建人</tt><br>
	 * @return
	 */
	@Rules(rules = { 
			@Rule(name = "tradeFlowId", required = true, requiredMessage = "交易过程ID不能为空!"),
			@Rule(name = "auditStatus", required = true, requiredMessage = "审核状态不能为空!"),
			@Rule(name = "auditMemo", required = true, requiredMessage = "审核备注不能为空!"),
			@Rule(name = "userId", required = true, requiredMessage = "操作人ID不能为空!")
	})
	public ResultVo auditWealthRechargeFirst(Map<String, Object> param) throws SLException;
	
	/**
	 * 线下充值复审
	 * @author liyy
	 * @date   2016年4月5日
	 * @param param
	 *      <tt>tradeFlowId:String:交易过程ID</tt><br>
     *      <tt>auditStatus:String:审核状态</tt><br>
     *      <tt>auditMemo  :String: 审核备注</tt><br>
     *      <tt>userId     :String:创建人</tt><br>
	 * @return
	 */
	@Rules(rules = { 
			@Rule(name = "tradeFlowId", required = true, requiredMessage = "交易过程ID不能为空!"),
			@Rule(name = "auditStatus", required = true, requiredMessage = "审核状态不能为空!"),
			@Rule(name = "auditMemo", required = true, requiredMessage = "审核备注不能为空!"),
			@Rule(name = "userId", required = true, requiredMessage = "操作人ID不能为空!")
	})
	public ResultVo auditWealthRechargeSecond(Map<String, Object> param) throws SLException;
	
	/**
	 * 线下充值终审
	 * 
	 * @author zhangt
	 * @date   2016年2月23日下午8:29:10
	 * @param param
	 *      <tt>tradeFlowId:String:交易过程ID</tt><br>
     *      <tt>auditStatus:String:审核状态</tt><br>
     *      <tt>auditMemo  :String: 审核备注</tt><br>
     *      <tt>userId     :String:创建人</tt><br>
	 * @return
	 */
	@Rules(rules = { 
			@Rule(name = "tradeFlowId", required = true, requiredMessage = "交易过程ID不能为空!"),
			@Rule(name = "auditStatus", required = true, requiredMessage = "审核状态不能为空!"),
			@Rule(name = "auditMemo", required = true, requiredMessage = "审核备注不能为空!"),
			@Rule(name = "userId", required = true, requiredMessage = "操作人ID不能为空!")
	})
	public ResultVo auditWealthRechargeLast(Map<String, Object> param) throws SLException;
	
	/**
	 * 线下提现列表
	 * 
	 * @author zhangt
	 * @date   2016年2月23日下午8:29:59
	 * @param param
     *      <tt>start           :String:起始值</tt><br>
     *      <tt>length          :String:长度</tt><br>
     *      <tt>custName        :String:用户名（可选）</tt><br>
     *      <tt>credentialsCode :String:证件号码(可选)</tt><br>
     *      <tt>mobile          :String:手机号（可选）</tt><br>
     *      <tt>auditStatus     :String:审核状态（可选）</tt><br>
     *      <tt>beginOperateDate:String:开始创建时间（可选）</tt><br>
     *      <tt>endOperateDate  :String:结束创建时间（可选）</tt><br>
     *      <tt>custManagerId   :String:客户经理ID（可选）</tt><br>
     *      <tt>tradeStatus     :String:提现状态</tt><br>
	 * @return
	 *      <tt>tradeFlowId    :String:交易过程ID</tt><br>
     *      <tt>custName       :String:用户名</tt><br>
     *      <tt>credentialsCode:String:证件号码</tt><br>
     *      <tt>mobile         :String:手机号</tt><br>
     *      <tt>tradeType      :String:交易类型</tt><br>
     *      <tt>tradeAmount    :String:交易金额</tt><br>
     *      <tt>createUser     :String:创建人</tt><br>
     *      <tt>createDate     :String:创建时间</tt><br>
     *      <tt>auditStatus    :String:审核状态</tt><br>
     *      <tt>tradeStatus    :String:提现状态</tt><br>
	 */
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "起始值不能为空!", number = true, numberMessage = "起始值必须为数字"),
			@Rule(name = "length", required = true, requiredMessage = "长度不能为空!", number = true, numberMessage = "长度必须为数字")
	})
	public ResultVo queryWealthWithDrawList(Map<String, Object> param);
	
	/**
	 * 财务导出
	 * 
	 * @author zhangt
	 * @date   2016年2月23日下午8:31:16
	 * @param param
	 *      <tt>start           :String:起始值</tt><br>
     *      <tt>length          :String:长度</tt><br>
     *      <tt>custName        :String:用户名（可选）</tt><br>
     *      <tt>credentialsCode :String:证件号码(可选)</tt><br>
     *      <tt>mobile          :String:手机号（可选）</tt><br>
     *      <tt>auditStatus     :String:审核状态（可选）</tt><br>
     *      <tt>beginOperateDate:String:开始创建时间（可选）</tt><br>
     *      <tt>endOperateDate  :String:结束创建时间（可选）</tt><br>
     *      <tt>tradeStatus     :String:提现状态</tt><br>
	 * @return
	 *      <tt>tradeFlowId    :String:交易过程ID</tt><br>
     *      <tt>custName       :String:用户名</tt><br>
     *      <tt>credentialsCode:String:证件号码</tt><br>
     *      <tt>mobile         :String:手机号</tt><br>
     *      <tt>tradeType      :String:交易类型</tt><br>
     *      <tt>tradeAmount    :String:交易金额</tt><br>
     *      <tt>createUser     :String:创建人</tt><br>
     *      <tt>createDate     :String:创建时间</tt><br>
     *      <tt>auditStatus    :String:审核状态</tt><br>
	 */
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "起始值不能为空!", number = true, numberMessage = "起始值必须为数字"),
			@Rule(name = "length", required = true, requiredMessage = "长度不能为空!", number = true, numberMessage = "长度必须为数字")
	})
	public ResultVo exportWealthWithDraw(Map<String, Object> param) throws SLException;
	
	/**
	 * 批量导入
	 * 
	 * @author zhangt
	 * @date   2016年2月23日下午8:32:33
	 * @param param
	 *      <tt>wealthWithDrawList:String:批量导入列表:List<Map<String, Object>></tt><br>
     *      <tt>transferType      :String:指令类型</tt><br>
     *      <tt>tradeAmount       :String:交易金额</tt><br>
     *      <tt>payerBankNo       :String:付款人账号</tt><br>
     *      <tt>payeeBankNo       :String:收款人账号</tt><br>
     *      <tt>payeeCustName     :String:收款人姓名</tt><br>
     *      <tt>currency          :String:币种</tt><br>
     *      <tt>tradeDate         :String:处理时间</tt><br>
     *      <tt>tradeStatus       :String:处理结果</tt><br>
     *      <tt>fileName 	      :String:文件名</tt><br>
     *      <tt>userId   		  :String:操作人Id</tt><br>
	 * @return
	 */
	@Rules(rules = { 
			@Rule(name = "fileName", required = true, requiredMessage = "文件名不能为空!"),
			@Rule(name = "userId", required = true, requiredMessage = "操作人ID不能为空!")
	})
	public ResultVo importWealthWithDraw(Map<String, Object> param) throws SLException;
	
	/**
	 * 线下提现查看
	 * 
	 * @author zhangt
	 * @date   2016年2月23日下午8:33:46
	 * @param param
     *      <tt>tradeFlowId:String:交易过程ID</tt><br>
	 * @return
     *      <tt>tradeFlowId    :String:交易过程ID</tt><br>
     *      <tt>custName       :String:用户名</tt><br>
     *      <tt>credentialsCode:String:证件号码</tt><br>
     *      <tt>mobile         :String:手机号</tt><br>
     *      <tt>registerDate   :String:注册时间</tt><br>
     *      <tt>bankName       :String:银行名称</tt><br>
     *      <tt>bankCardNo     :String:银行卡号</tt><br>
     *      <tt>branchBankName :String:支行名称</tt><br>
     *      <tt>openProvince   :String:开户行所在省</tt><br>
     *      <tt>openCity       :String:开户行所在市</tt><br>
     *      <tt>tradeAmount    :String:提现金额</tt><br>
     *      <tt>auditList      :String:审核列表:List<Map<String, Object>></tt><br>
     *      <tt>审核ID           :String:         auditId</tt><br>
     *      <tt>审核时间           :String:         auditDate</tt><br>
     *      <tt>审核人员           :String:         auditUser</tt><br>
     *      <tt>审核结果           :String:         auditStatus</tt><br>
     *      <tt>审核备注           :String:         auditMemo</tt><br>
	 */
	@Rules(rules = { 
			@Rule(name = "tradeFlowId", required = true, requiredMessage = "交易过程ID不能为空!")
	})
	public ResultVo queryWealthWithDrawDetailById(Map<String, Object> param);
	
	/**
	 * 线下提现处理
	 * 
	 * @author zhangt
	 * @date   2016年3月2日下午1:45:51
	 * @param param
	 *      <tt>tradeFlowId:String:交易过程ID</tt><br>
     *      <tt>auditStatus:String:审核状态</tt><br>
     *      <tt>auditMemo  :String: 审核备注</tt><br>
     *      <tt>userId     :String:创建人</tt><br>
	 * @return
	 */
	@Rules(rules = { 
			@Rule(name = "tradeFlowId", required = true, requiredMessage = "交易过程ID不能为空!"),
			@Rule(name = "auditStatus", required = true, requiredMessage = "审核状态不能为空!"),
			@Rule(name = "auditMemo", required = true, requiredMessage = "审核备注不能为空!"),
			@Rule(name = "userId", required = true, requiredMessage = "创建人ID不能为空!")
	})
	public ResultVo auditWealthWithDraw(Map<String, Object> param) throws SLException;

	/**
	 * 线下业务-客户转移-客户转移列表（部门额相关）
	 *
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param param
     *      <tt>start           :String:起始值</tt><br>
     *      <tt>length          :String:长度</tt><br>
     *      <tt>userId          :String:客户经理ID</tt><br>
     *      <tt>loginName      :String:用户名（可选）</tt><br>
     *      <tt>custName        :String:用户名（可选）</tt><br>
     *      <tt>credentialsCode :String:证件号码(可选)</tt><br>
     *      <tt>mobile          :String:手机号（可选）</tt><br>
     *      <tt>auditStatus     :String:审核状态（可选）</tt><br>
     *      <tt>custId         :String:客户ID（可选）</tt><br>
     *      <tt>beginOperateDate:String:开始创建时间（可选）</tt><br>
     *      <tt>endOperateDate  :String:结束创建时间（可选）</tt><br>
	 * @return ResultVo
	 * 		<tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
     *      <tt>data:Objcet
     *      	<tt>iTotalDisplayRecords:String:总数</tt><br>
     *      	<tt>data:List
     *      		<tt>custApplyId    :String:客户申请ID</tt><br>
     *      		<tt>loginName      :String:用户名</tt><br>
     *      		<tt>custName       :String:客户姓名</tt><br>
     *      		<tt>credentialsCode:String:证件号码</tt><br>
     *      		<tt>mobile         :String:手机号</tt><br>
     *      		<tt>createUser     :String:操作人</tt><br>
     *      		<tt>createDate     :String:操作时间</tt><br>
     *      		<tt>registerDate   :String:注册时间</tt><br>
     *      		<tt>newCustManager :String:新业务员</tt><br>
     *      		<tt>auditStatus    :String:审核状态</tt><br>
     *          </tt><br>
     *      </tt><br>
     *  @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数"),
			@Rule(name = "userId", required = true, requiredMessage = "客户经理不能为空")
	})
	public ResultVo queryAllCustTransferList(Map<String, Object> param) throws SLException;
	
	/**
	 * 前台-客户转移-客户转移列表
	 *
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param param
     *      <tt>start           :String:起始值</tt><br>
     *      <tt>length          :String:长度</tt><br>
     *      <tt>loginName      :String:用户名（可选）</tt><br>
     *      <tt>custName        :String:用户名（可选）</tt><br>
     *      <tt>credentialsCode :String:证件号码(可选)</tt><br>
     *      <tt>mobile          :String:手机号（可选）</tt><br>
     *      <tt>auditStatus     :String:审核状态（可选）</tt><br>
     *      <tt>custId         :String:客户ID（可选）</tt><br>
     *      <tt>userId          :String:客户经理ID（可选）</tt><br>
     *      <tt>beginOperateDate:String:开始创建时间（可选）</tt><br>
     *      <tt>endOperateDate  :String:结束创建时间（可选）</tt><br>
	 * @return ResultVo
	 * 		<tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
     *      <tt>data:Objcet
     *      	<tt>iTotalDisplayRecords:String:总数</tt><br>
     *      	<tt>data:List
     *      		<tt>custApplyId    :String:客户申请ID</tt><br>
     *      		<tt>loginName      :String:用户名</tt><br>
     *      		<tt>custName       :String:客户姓名</tt><br>
     *      		<tt>credentialsCode:String:证件号码</tt><br>
     *      		<tt>mobile         :String:手机号</tt><br>
     *      		<tt>createUser     :String:操作人</tt><br>
     *      		<tt>createDate     :String:操作时间</tt><br>
     *      		<tt>registerDate   :String:注册时间</tt><br>
     *      		<tt>newCustManager :String:新业务员</tt><br>
     *      		<tt>auditStatus    :String:审核状态</tt><br>
     *          </tt><br>
     *      </tt><br>
     *  @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数")
	})
	public ResultVo queryCustTransferList(Map<String, Object> param) throws SLException;
	
	/**
	 * 线下业务-客户转移-客户转移查看详情
	 *
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param param
     *      <tt>custApplyId：客户申请ID</tt><br>
	 * @return ResultVo
	 * 		<tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
     *      <tt>data
     *      	<tt>custApplyId                  :String:客户申请ID</tt><br>
     *      	<tt>custId                       :String:用户ID</tt><br>
     *      	<tt>loginName                    :String:用户名</tt><br>
     *      	<tt>custName                     :String:客户姓名</tt><br>
     *      	<tt>credentialsCode              :String:证件号码</tt><br>
     *      	<tt>mobile                       :String:手机号</tt><br>
     *      	<tt>registerDate                 :String:注册时间</tt><br>
     *      	<tt>oldCustManagerId             :String:原客户经理主键</tt><br>
     *      	<tt>oldCustManagerName           :String:原客户经理名称</tt><br>
     *      	<tt>oldCustManagerMobile         :String:原客户经理手机号</tt><br>
     *      	<tt>oldCustManagerCredentialsCode:String:原客户经理身份证号</tt><br>
     *      	<tt>newCustManagerId             :String:新客户经理主键</tt><br>
     *      	<tt>newCustManagerName           :String:新客户经理名称</tt><br>
     *      	<tt>newCustManagerMobile         :String:新客户经理手机号</tt><br>
     *      	<tt>newCustManagerCredentialsCode:String:新客户经理身份证号</tt><br>
     *      	<tt>attachmentList:List<Map<String, Object>>:附件列表
     *     			<tt>attachmentType:String:         附件类型</tt><br>
     *      		<tt>attachmentName:String:         附件名称</tt><br>
     *      		<tt>storagePath   :String:         存储路径</tt><br>
     *      	</tt><br>
     *     		<tt>auditList:List<Map<String, Object>>:审核日志列表
     *      		<tt>auditId    :String:审核ID</tt><br>
     *      		<tt>auditDate  :String:审核时间</tt><br>
     *      		<tt>auditUser  :String:审核人员</tt><br>
     *      		<tt>auditStatus:String:审核结果</tt><br>
     *      		<tt>auditMemo  :String:审核备注</tt><br>
     *      	</tt><br>
     *      </tt><br>
     *  @throws SLException
	 */
	 @Rules(rules = { 
				@Rule(name = "custApplyId", required = true, requiredMessage = "客户申请ID不能为空") 
	 })
	public ResultVo queryCustTransferDetailById(Map<String, Object> param) throws SLException;

	/**
	 * 线下业务-客户转移-客户转移保存 新建/编辑
	 *
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param param
     *      <tt>custApplyId                  :String:客户申请ID（编辑时非空）</tt><br>
     *      <tt>custId                       :String:用户ID</tt><br>
     *      <tt>oldCustManagerId             :String:原客户经理主键</tt><br>
     *      <tt>oldCustManagerName           :String:原客户经理名称</tt><br>
     *      <tt>oldCustManagerMobile         :String:原客户经理手机号</tt><br>
     *      <tt>oldCustManagerCredentialsCode:String:原客户经理身份证号</tt><br>
     *      <tt>newCustManagerId             :String:新客户经理主键</tt><br>
     *      <tt>newCustManagerName           :String:新客户经理名称</tt><br>
     *      <tt>newCustManagerMobile         :String:新客户经理手机号</tt><br>
     *      <tt>newCustManagerCredentialsCode:String:新客户经理身份证号</tt><br>
     *      <tt>userId                       :String:创建人</tt><br>
     *      <tt>attachmentList:List:附件列表
     *      	<tt>attachmentType:String:附件类型</tt><br>
     *      	<tt>attachmentName:String:附件名称</tt><br>
     *      	<tt>storagePath   :String:存储路径</tt><br>
     *      </tt><br>
	 * @return ResultVo
	 * 		<tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
     *  @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "custId", required = true, requiredMessage = "请完善客户信息") 
			, @Rule(name = "newCustManagerName", required = true, requiredMessage = "新客户经理名称不能为空")
			, @Rule(name = "newCustManagerMobile", required = true, requiredMessage = "新客户经理手机号不能为空")
			, @Rule(name = "newCustManagerCredentialsCode", required = true, requiredMessage = "新客户经理身份证号不能为空")
			, @Rule(name = "userId", required = true, requiredMessage = "操作人ID不能为空")
	})
	public ResultVo saveCustTransfer(Map<String, Object> param) throws SLException;
	
	/**
	 * 我是业务员-客户转移-客户转移
	 *
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param param
     *      <tt>custId                       :String:用户ID</tt><br>
     *      <tt>oldCustManagerId             :String:原客户经理主键</tt><br>
     *      <tt>newCustManagerName           :String:新客户经理名称</tt><br>
     *      <tt>newCustManagerMobile         :String:新客户经理手机号</tt><br>
     *      <tt>newCustManagerCredentialsCode:String:新客户经理身份证号</tt><br>
     *      <tt>userId                       :String:创建人</tt><br>
	 * @return ResultVo
	 * 		<tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
     *  @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "custId", required = true, requiredMessage = "请完善客户信息") 
			, @Rule(name = "oldCustManagerId", required = true, requiredMessage = "原客户经理ID不能为空")
			, @Rule(name = "newCustManagerName", required = true, requiredMessage = "新客户经理名称不能为空")
			, @Rule(name = "newCustManagerMobile", required = true, requiredMessage = "新客户经理手机号不能为空")
			, @Rule(name = "newCustManagerCredentialsCode", required = true, requiredMessage = "新客户经理身份证号不能为空")
			, @Rule(name = "userId", required = true, requiredMessage = "操作人ID不能为空")
	})
	public ResultVo saveCustTransferByManager(Map<String, Object> param) throws SLException;
	
	/**
	 * 同步原线下客户经理(实名认证/定时同步)
	 *
	 * @author  liyy
	 * @date    2016年6月29日 
	 * @param param
     *      <tt>custId                       :String:用户ID</tt><br>
     *      <tt>newCustManagerId             :String:新客户经理ID</tt><br>
     *      <tt>userId                       :String:创建人</tt><br>
	 * @return ResultVo
	 * 		<tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
     *  @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "custId", required = true, requiredMessage = "客户Id不能为空") 
			, @Rule(name = "newCustManagerId", required = true, requiredMessage = "新客户经理Id不能为空")
			, @Rule(name = "userId", required = true, requiredMessage = "操作人ID不能为空")
	})
	public ResultVo saveCustTransferForSyncOldOffLineWealth(Map<String, Object> param) throws SLException;
	
	/**
	 * 线下业务-客户转移-客户转移审核
	 *
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param param
     *      <tt>custApplyId:String:客户申请ID</tt><br>
     *      <tt>auditStatus:String:审核状态</tt><br>
     *      <tt>auditMemo  :String: 审核备注</tt><br>
     *      <tt>userId     :String:创建人</tt><br>
	 * @return ResultVo
	 * 		<tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
     *  @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "custApplyId", required = true, requiredMessage = "客户申请ID不能为空") 
			, @Rule(name = "auditStatus", required = true, requiredMessage = "审核状态不能为空")
			, @Rule(name = "auditMemo", required = true, requiredMessage = "审核备注")
			, @Rule(name = "userId", required = true, requiredMessage = "创建人不能为空")
	})
	public ResultVo auditCustTransfer(Map<String, Object> param) throws SLException;
	
	/**
	 * 我是业务员-客户管理-客户列表查询
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param param:Map
     *      <tt>start            :String:起始值</tt><br>
     *      <tt>length           :String:长度</tt><br>
     *      <tt>custId           :String:客户ID（客户经理）</tt><br>
     *      <tt>custName         :String:用户名（可以为空）</tt><br>
     *      <tt>credentialsCode  :String:证件号码（可以为空）</tt><br>
     *      <tt>mobile           :String:手机号（可以为空）</tt><br>
     *      <tt>beginRegisterDate:String:开始注册时间（可以为空）</tt><br>
     *      <tt>endRegisterDate  :String:结束注册时间（可以为空）</tt><br>
     * @param isSelf boolean:原客户经理发起
	 * @return ResultVo
	 * 		<tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
     *      <tt>data:Map
     *      	<tt>iTotalDisplayRecords:String:总数</tt><br>
     *      	<tt>data:List
     *      		<tt>custId         :String:客户ID</tt><br>
     *      		<tt>custName       :String:用户名</tt><br>
     *      		<tt>credentialsCode:String:证件号码</tt><br>
     *      		<tt>mobile         :String:手机号</tt><br>
     *      		<tt>registerDate   :String:注册时间</tt><br>
     *          </tt><br>
     *      </tt><br>
     *  @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数") 
			, @Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数")
			, @Rule(name = "custId", required = true, requiredMessage = "客户ID（客户经理）不能为空")
	})
	public ResultVo queryCustByManager(Map<String, Object> param) throws SLException;
	
	/**
	 * 我是业务员-客户管理-投资信息查询
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param param:Map
     *      <tt>start :String:起始值</tt><br>
     *      <tt>length:String:长度</tt><br>
     *      <tt>custId:String:客户ID</tt><br>
	 * @return ResultVo
	 * 		<tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
     *      <tt>data:Map
     *      	<tt>iTotalDisplayRecords:String:总数</tt><br>
     *      	<tt>data:List
     *      		<tt>investDate  :String:投资时间</tt><br>
     *      		<tt>investAmount:String:投资金额</tt><br>
     *      		<tt>lendingType :String:计划名称</tt><br>
     *      		<tt>lendingNo   :String:项目期数</tt><br>
     *      		<tt>typeTerm    :String:项目期限(月)</tt><br>
     *      		<tt>yearRate    :String:年化收益率</tt><br>
     *      		<tt>awardRate   :String:奖励利率</tt><br>
     *      		<tt>effectDate  :String:生效日期</tt><br>
     *      		<tt>endDate     :String:到期日期</tt><br>
     *      		<tt>investStatus:String:投资状态（当前状态）</tt><br>
     *          </tt><br>
     *      </tt><br>
     *  @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数") 
			, @Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数")
	})
	public ResultVo queryCustWealthByManager(Map<String, Object> param) throws SLException;
	
	/**
	 * 我是业务员-附属银行卡-客户银行卡列表查询
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param param:Map
     *      <tt>start :String:起始值</tt><br>
     *      <tt>length:String:长度</tt><br>
     *      <tt>custId:String:客户ID</tt><br>
	 * @return ResultVo
	 * 		<tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
     *      <tt>data:Map
     *      	<tt>iTotalDisplayRecords:String:总数</tt><br>
     *      	<tt>data:List
     *      		<tt>tradeFlowId    :String:交易过程流水ID</tt><br>
     *      		<tt>custName       :String:用户名</tt><br>
     *      		<tt>credentialsCode:String:证件号码</tt><br>
     *      		<tt>mobile         :String:手机号</tt><br>
     *      		<tt>bankName       :String:银行名称</tt><br>
     *      		<tt>bankCardNo     :String:银行卡号</tt><br>
     *      		<tt>branchBankName :String:支行名称</tt><br>
     *      		<tt>openProvince   :String:开户行所在省</tt><br>
     *      		<tt>openCity       :String:开户行所在市</tt><br>
     *      		<tt>auditStatus    :String:审核状态</tt><br>
     *          </tt><br>
     *      </tt><br>
     *  @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数") 
			, @Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数")
	})
	public ResultVo queryCustBankByManager(Map<String, Object> param) throws SLException;
	
	/**
	 * 我是业务员-附属银行卡-查看客户银行卡
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param param:Map
     *      <tt>tradeFlowId:String:交易过程流水ID</tt><br>
	 * @return ResultVo
	 * 		<tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
     *      <tt>data
     *      	<tt>tradeFlowId    :String:交易过程流水ID</tt><br>
     *      	<tt>custId         :String:客户ID</tt><br>
     *      	<tt>custName       :String:用户名</tt><br>
     *      	<tt>credentialsCode:String:证件号码</tt><br>
     *      	<tt>mobile         :String:手机号</tt><br>
     *      	<tt>bankCode       :String:银行名称code</tt><br>
     *      	<tt>bankName       :String:银行名称</tt><br>
     *      	<tt>bankCardNo     :String:银行卡号</tt><br>
     *      	<tt>branchBankName :String:支行名称</tt><br>
     *      	<tt>openProvince   :String:开户行所在省code</tt><br>
     *      	<tt>openProvinceName   :String:开户行所在省</tt><br>
     *      	<tt>openCity       :String:开户行所在市code</tt><br>
     *      	<tt>openCityName       :String:开户行所在市</tt><br>
     *      	<tt>tradeStatus       :String:当前状态</tt><br>
     *      	<tt>attachmentList:List<Map<String, Object>>:附件列表
     *     			<tt>attachmentId  :String:         附件ID</tt><br>
     *     			<tt>attachmentType:String:         附件类型</tt><br>
     *      		<tt>attachmentName:String:         附件名称</tt><br>
     *      		<tt>storagePath   :String:         存储路径</tt><br>
     *      	</tt><br>
     *     		<tt>auditList:List<Map<String, Object>>:审核日志列表
     *      		<tt>auditId    :String:审核ID</tt><br>
     *      		<tt>auditDate  :String:审核时间</tt><br>
     *      		<tt>auditUser  :String:审核人员</tt><br>
     *      		<tt>auditStatus:String:审核结果</tt><br>
     *      		<tt>auditMemo  :String:审核备注</tt><br>
     *      	</tt><br>
     *      </tt><br>
     *  @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "tradeFlowId", required = true, requiredMessage = "交易过程流水ID不能为空") 
	})
	public ResultVo queryCustBankDetailByManager(Map<String, Object> param) throws SLException;
	
	/**
	 * 我是业务员-附属银行卡-新增客户银行卡
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param param:Map
     *      <tt>tradeFlowId   :String:交易过程流水ID(编辑时非空)</tt><br>
     *      <tt>custId        :String:客户ID</tt><br>
     *      <tt>bankName      :String:银行名称(code)</tt><br>
     *      <tt>bankCardNo    :String:银行卡号</tt><br>
     *      <tt>branchBankName:String:支行名称</tt><br>
     *      <tt>openProvince  :String:开户行所在省</tt><br>
     *      <tt>openCity      :String:开户行所在市</tt><br>
     *      <tt>userId        :String:创建人</tt><br>
     *      <tt>attachmentList:List< Map< String, Object>>:附件列表<br>
     *      	<tt>attachmentId（非空时编辑，否则新增）:String: 附件ID</tt><br>
     *      	<tt>attachmentType          :String: 附件类型</tt><br>
     *      	<tt>attachmentName          :String: 附件名称</tt><br>
     *      	<tt>storagePath             :String:存储路径</tt><br>
     *      </tt><br>
	 * @return ResultVo
	 * 		<tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
     * @throws SLException
     */
	@Rules(rules = { 
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空") 
			, @Rule(name = "userId", required = true, requiredMessage = "操作人ID不能为空")
			, @Rule(name = "bankName", required = true, requiredMessage = "银行名称不能为空")
			, @Rule(name = "openProvince", required = true, requiredMessage = "开户行所在省/市不能为空")
			, @Rule(name = "openCity", required = true, requiredMessage = "开户行所在市/区不能为空")
			, @Rule(name = "branchBankName", required = true, requiredMessage = "支行名称不能为空")
			, @Rule(name = "bankCardNo", required = true, requiredMessage = "银行卡号不能为空")
	})
	public ResultVo saveCustBankByManager(Map<String, Object> param) throws SLException;
	
	/**
	 * 我是业务员-附属银行卡-作废客户银行卡
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param param:Map
     *      <tt>tradeFlowId:String:交易过程流水ID</tt><br>
     *      <tt>userId     :String:创建人</tt><br>
	 * @return ResultVo
	 * 		<tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
     * @throws SLException
     */
	@Rules(rules = { 
			@Rule(name = "tradeFlowId", required = true, requiredMessage = "交易流水ID不能为空") 
			, @Rule(name = "userId", required = true, requiredMessage = "操作人ID不能为空")
	})
	public ResultVo abandonCustBankByManager(Map<String, Object> param) throws SLException;
	
	/**
	 * 我是业务员-公用-查询客户经理名下所有客户
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param param:Map
     *      <tt>custManagerId:String:客户经理主键(可选)</tt><br>
     *      <tt>custName     :String:用户姓名(可选)</tt><br>
	 * @return ResultVo
	 * 		<tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
     *      <tt>data:Map
     *      	<tt>data:List
	 *      		<tt>custId         :String:客户ID</tt><br>
	 *      		<tt>custName:String:用户名</tt><br>
	 *      		<tt>credentialsCode:String:证件号码</tt><br>
	 *      		<tt>mobile         :String:手机</tt><br>
	 *      		<tt>registerDate   :String:注册时间</tt><br>
	 *      		<tt>custManagerId  :String:客户经理Id</tt><br>  
     *      	</tt><br>
     *      </tt><br>
     * @throws SLException
     */
	public ResultVo queryCustNameByManager(Map<String, Object> param) throws SLException;
	
	/**
	 * 我是业务员-公用-查询业务员信息
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param param:Map
     *      <tt>custName       :String:用户姓名(可选)</tt><br>
     *      <tt>credentialsCode:String:证件号码（可以为空）</tt><br>
     *      <tt>mobile:String:手机号（可以为空）</tt><br>
	 * @return ResultVo
	 * 		<tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
     *      <tt>data:Map
     *      	<tt>data:List
     *      		<tt>custId         :String:客户ID</tt><br>
     *      		<tt>custName       :String:用户名</tt><br>
     *      		<tt>credentialsCode:String:证件号码</tt><br>
     *      		<tt>mobile         :String:手机号</tt><br>
     *      		<tt>registerDate   :String:注册时间</tt><br>
     *      	</tt><br>
     *      </tt><br>
     * @throws SLException
     */
	public ResultVo queryCustManager(Map<String, Object> param) throws SLException;
	
	/**
	 * 我是业务员-公用-查询客户下面附属银行
	 * @author  liyy
	 * @date    2016年2月26日 
	 * @param param:Map
     *      <tt>custId:String:客户ID</tt><br>
	 * @return ResultVo
	 * 		<tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
     *      <tt>data:Map
     *      	<tt>data:List
     *      		<tt>bankId  :String:银行主键</tt><br>
     *      		<tt>bankName:String:bankName</tt><br>
     *      	</tt><br>
     *      </tt><br>
     * @throws SLException
     */
	@Rules(rules = { 
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空") 
	})
	public ResultVo queryCustBankByCustId(Map<String, Object> param) throws SLException;
	
	/**
	 * 数据汇总
	 *
	 * @author  wangjf
	 * @date    2016年3月5日 上午11:27:39
	 * @param params
     *      <tt>custId      :String:客户ID</tt><br>
     *      <tt>jobNo       :String:登录人工号</tt><br>
     *      <tt>ranking     :String:登录人级别</tt><br>
	 * @return ResultVo
     *      <tt>totalMonthlyInvestAmount:String:本月年化投资额</tt><br>
     *      <tt>totalMonthlyIncomeAmount:String:本月进账金额</tt><br>
     *      <tt>totalInvestAmount       :String:累计年化投资额</tt><br>
     *      <tt>totalIncomeAmount       :String:累计进账金额</tt><br>
     *      <tt>totalMonthlyRegister    :String:本月注册人数</tt><br>
     *      <tt>totalRegister           :String:累计注册人数</tt><br>  
     *      <tt>totalMonthlyInvestCount :String:本月投资人数</tt><br>  
     *      <tt>totalInvestCount        :String:累计投资人数</tt><br>      
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空!")
	})
	public ResultVo queryMyWeathSummary(Map<String, Object> params)throws SLException;
	
	/**
	 * 业绩查询
	 *
	 * @author  wangjf
	 * @date    2016年3月5日 上午11:27:54
	 * @param params
     *      <tt>start          :String:起始值</tt><br>
     *      <tt>length         :String:长度</tt><br>
     *      <tt>custId         :String:客户ID（客户经理）</tt><br>
     *      <tt>beginInvestDate:String:开始投资时间（可以为空）</tt><br>
     *      <tt>endInvestDate  :String:结束投资时间（可以为空）</tt><br>
     *      <tt>custName       :String:客户姓名（可以为空）</tt><br>
     *      <tt>mobile         :String:手机号（可以为空）</tt><br>
	 * @return
     *      <tt>custName       :String:用户名</tt><br>
     *      <tt>credentialsCode:String:证件号码</tt><br>
     *      <tt>mobile         :String:手机号</tt><br>
     *      <tt>registerDate   :String:注册时间</tt><br>
     *      <tt>investDate     :String:投资时间</tt><br>
     *      <tt>investAmount   :String:投资金额</tt><br>
     *      <tt>typeTerm       :String:项目期限(月)</tt><br>
     *      <tt>lendingType    :String:计划名称</tt><br>
     *      <tt>lendingNo      :String:项目期数</tt><br>
     *      <tt>investStatus   :String:投资状态（当前状态）</tt><br>
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"), 
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数"),
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空!")
	})
	public ResultVo queryMyWeathSummaryList(Map<String, Object> params)throws SLException;
	
	/**
	 * 查询所有业务员信息
	 * @author zhiwen_feng
	 * @date    2016年3月19日 
	 * @param param:Map
     *      <tt>custName       :String:用户姓名</tt><br>
	 * @return ResultVo
	 * 		<tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
     *      <tt>data:Map
     *      	<tt>data:List
     *      		<tt>custId         :String:客户ID</tt><br>
     *      		<tt>custName       :String:用户名</tt><br>
     *      		<tt>credentialsCode:String:证件号码</tt><br>
     *      		<tt>mobile         :String:手机号</tt><br>
     *      		<tt>registerDate   :String:注册时间</tt><br>
     *      	</tt><br>
     *      </tt><br>
     * @throws SLException
     */
	@Rules(rules = { 
			@Rule(name = "custName", required = true, requiredMessage = "客户姓名不能为空!")
	})
	public ResultVo queryCustName(Map<String, Object> params)throws SLException;
	
	/**
	 * 新增客户银行卡
	 * @author  liyy
	 * @date    2016年5月31日 
	 * @param params Map <br>
     *      <tt>tradeFlowId             :String:交易过程流水ID(编辑时非空)</tt><br>
     *      <tt>custId                  :String:客户ID</tt><br>
     *      <tt>bankName                :String:银行名称</tt><br>
     *      <tt>bankCardNo              :String:银行卡号</tt><br>
     *      <tt>branchBankName          :String:支行名称</tt><br>
     *      <tt>openProvince            :String:开户行所在省</tt><br>
     *      <tt>openCity                :String:开户行所在市</tt><br>
     *      <tt>userId                  :String:创建人</tt><br>
     **      <tt>custName                :String:用户名</tt><br>
     **      <tt>credentialsCode         :String:证件号码</tt><br>
     *      <tt>attachmentList          :String:附件列表:List<Map<String, Object>></tt><br>
     *      	<tt>attachmentId            :String:附件ID（非空时编辑，否则新增）</tt><br>
     *      	<tt>attachmentType          :String:         附件类型</tt><br>
     *      	<tt>attachmentName          :String:         附件名称</tt><br>
     *      	<tt>storagePath             :String:         存储路径</tt><br>
	 * @return ResultVo
	 * 		<tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
     *      <tt>data
     *      </tt><br>
     *  @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空") 
			, @Rule(name = "userId", required = true, requiredMessage = "操作人ID不能为空")
			, @Rule(name = "bankName", required = true, requiredMessage = "银行名称不能为空")
			, @Rule(name = "openProvince", required = true, requiredMessage = "开户行所在省/市不能为空")
			, @Rule(name = "openCity", required = true, requiredMessage = "开户行所在市/区不能为空")
			, @Rule(name = "branchBankName", required = true, requiredMessage = "支行名称不能为空")
			, @Rule(name = "bankCardNo", required = true, requiredMessage = "银行卡号不能为空")
			, @Rule(name = "custName", required = true, requiredMessage = "客户名称不能为空")
			, @Rule(name = "credentialsCode", required = true, requiredMessage = "身份证不能为空")
	})
	public ResultVo saveCustBank(Map<String, Object> param) throws SLException;
	
	/**
	 * 查询业务员信息
	 * @author  liyy
	 * @date    2016年5月31日 
	 * @param params Map <br>
     *      <tt>custId                  :String:客户ID</tt><br>
	 * @return ResultVo
	 * 		<tt>message:String</tt><br>
     *      <tt>success:String</tt><br>
     *      <tt>data:Map
     *      	<tt>data:Map
     *      		<tt>custId         :String:业务员ID</tt><br>
     *      		<tt>custName       :String:业务员用户名</tt><br>
     *      		<tt>credentialsCode:String:业务员证件号码</tt><br>
     *      		<tt>mobile         :String:业务员手机号</tt><br>
     *      		<tt>registerDate   :String:业务员注册时间</tt><br>
     *      	</tt><br>
     *      </tt><br>
     *  @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空") 
	})
	public ResultVo queryCustManagerByCustId(Map<String, Object> param) throws SLException;
	
	/**
	 * 线下充值列表（客户自己的数据）
	 * 
	 * @author liyy
	 * @date   2016年6月1日
	 * @param param
     *      <tt>start           :String:起始值</tt><br>
     *      <tt>length          :String:长度</tt><br>
     *      <tt>custName        :String:用户名（可选）</tt><br>
     *      <tt>credentialsCode :String:证件号码(可选)</tt><br>
     *      <tt>mobile          :String:手机号（可选）</tt><br>
     *      <tt>auditStatus     :String:审核状态（可选）</tt><br>
     *      <tt>beginOperateDate:String:开始创建时间（可选）</tt><br>
     *      <tt>endOperateDate  :String:结束创建时间（可选）</tt><br>
     *      <tt>custId          :String:客户ID</tt><br>
	 * @return
	 *      <tt>tradeFlowId    :String:交易过程ID</tt><br>
     *      <tt>custName       :String:用户名</tt><br>
     *      <tt>credentialsCode:String:证件号码</tt><br>
     *      <tt>mobile         :String:手机号</tt><br>
     *      <tt>tradeType      :String:交易类型</tt><br>
     *      <tt>tradeAmount    :String:交易金额</tt><br>
     *      <tt>createUser     :String:创建人</tt><br>
     *      <tt>createDate     :String:创建时间</tt><br>
     *      <tt>auditStatus    :String:审核状态</tt><br>
	 */
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "起始值不能为空!", number = true, numberMessage = "起始值必须为数字"),
			@Rule(name = "length", required = true, requiredMessage = "长度不能为空!", number = true, numberMessage = "长度必须为数字"),
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空!")
	})
	public ResultVo queryWealthRechargeListByCustId(Map<String, Object> param);
	
	/**
	 * 转移线下理财客户
	 * @author zhiwen_feng
	 * @param params
	 * @return
	 */
	public ResultVo transferOffLineCustManager(Map<String, Object> params) throws SLException;
	
	
	/**
	 * 业绩详情BySalesMan
	 * @author liyy
	 * @param params
     *      <tt>custId     :String:客户ID（可选）</tt><br>
     *      <tt>investId   :String:投资ID</tt><br>
     *      <tt>productType:String:产品类型（优选计划、优选项目、债权转让）</tt><br>
	 * @return ResultVo
     *      <tt>investDate     :String:投资时间</tt><br>
     *      <tt>investAmount   :String:投资金额</tt><br>
     *      <tt>lendingType    :String:计划名称</tt><br>
     *      <tt>lendingNo      :String:项目期数</tt><br>
     *      <tt>typeTerm       :String:项目期限(月)</tt><br>
     *      <tt>yearRate       :String:年化收益率</tt><br>
     *      <tt>awardRate      :String:奖励利率</tt><br>
     *      <tt>effectDate     :String:生效日期（起息日期）</tt><br>
     *      <tt>endDate        :String:到期日期（到期日期）</tt><br>
     *      <tt>investStatus   :String:投资状态（当前状态）</tt><br>
     *      <tt>custName       :String:用户名</tt><br>
     *      <tt>credentialsCode:String:证件号码</tt><br>
     *      <tt>mobile         :String:手机号</tt><br>
     *      <tt>registerDate   :String:注册时间</tt><br>
     *      <tt>productId      :String:产品ID</tt><br>
	 */
	@Rules(rules = { 
			@Rule(name = "investId", required = true, requiredMessage = "投资信息不能为空!"),
			@Rule(name = "productType", required = true, requiredMessage = "产品类型不能为空!")
	})
	public ResultVo queryCustWealthDetailByManager(Map<String, Object> params) throws SLException;

    /**
     * 业绩列表查询
     *
     * @author  马立
     * @date    2017-5-11
     * @param params
     *      <tt>start          :String:起始值</tt><br>
     *      <tt>length         :String:长度</tt><br>
     *      <tt>custId         :String:客户ID（客户经理）</tt><br>
     *      <tt>beginInvestDate:String:开始投资时间（可以为空）</tt><br>
     *      <tt>endInvestDate  :String:结束投资时间（可以为空）</tt><br>
     *      <tt>managerName    :String:经理姓名（可以为空）</tt><br>
     * @return
     *      <tt>jobNo          :String:下属工号</tt><br>
     *      <tt>subRanking     :String:下属级别</tt><br>
     *      <tt>subName       :String:下属名称</tt><br>
     *      <tt>investAmount   :String:投资金额</tt><br>
     * @throws SLException
     */
    @Rules(rules = {
            @Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"),
            @Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数"),
            @Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空!"),
            @Rule(name = "ranking", required = true, requiredMessage = "客户级别不能为空!")
    })
    ResultVo queryNextTeamCustWealthList(Map<String, Object> params)throws SLException;

    /**
     * 业绩列表查询
     *
     * @author  马立
     * @date    2017-5-11
     * @param params
     *      <tt>start          :String:起始值</tt><br>
     *      <tt>length         :String:长度</tt><br>
     *      <tt>ranking        :String:登录人的级别</tt><br>
     *      <tt>subJobNo       :String:下属的工号</tt><br>
     *      <tt>subRanking     :String:下属级别</tt><br>
     *      <tt>beginInvestDate:String:开始投资时间（可以为空）</tt><br>
     *      <tt>endInvestDate  :String:结束投资时间（可以为空）</tt><br>
     *      <tt>managerName    :String:经理姓名（可以为空）</tt><br>
     * @return
     *      <tt>jobNo          :String:投资人工号/下属工号</tt><br>
     *      <tt>shopName       :String:门店名称</tt><br>
     *      <tt>investAmount   :String:投资金额</tt><br>
     * @throws SLException
     */
    @Rules(rules = {
            @Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"),
            @Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数"),
            @Rule(name = "ranking", required = true, requiredMessage = "登陆者级别不能为空!"),
            @Rule(name = "subJobNo", required = true, requiredMessage = "下属工号不能为空!"),
            @Rule(name = "subRanking", required = true, requiredMessage = "下属级别不能为空!")

    })
    ResultVo queryNextTeamCustWealthDetail(Map<String, Object> params)throws SLException;
}
