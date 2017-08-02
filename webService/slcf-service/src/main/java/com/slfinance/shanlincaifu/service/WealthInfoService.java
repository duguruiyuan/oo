package com.slfinance.shanlincaifu.service;

import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;

/**
 * 优选计划服务
 *
 * @date 2016-02-23
 * @author zhiwen_feng
 *
 */
public interface WealthInfoService {
	
	/**
	 * 优选计划列表
	 * 
	 * @author zhiwen_feng
	 * @date 2016-02-23
	 * @param parmas
     *      <tt>start           :String:起始值</tt><br>
     *      <tt>lendingType     :String:出借方式（可选）</tt><br>
     *      <tt>typeTerm        :String:项目期限（可选）</tt><br>
     *      <tt>wealthStatus    :String:项目状态（可选）</tt><br>
     *      <tt>beginReleaseDate:String:开始发布日期（可选）</tt><br>
     *      <tt>endReleaseDate  :String:结束发布日期（可选）</tt><br>
     *      <tt>beginEffectDate :String:开始生效日期（可选）</tt><br>
     *      <tt>endEffectDate   :String:结束生效日期（可选）</tt><br>
     *      <tt>beginEndDate    :String:开始到期日期（可选）</tt><br>
     *      <tt>endEndDate      :String:结束到期日期（可选）</tt><br>
     *      <tt>lendingNo       :String:项目期数（可选）</tt></br>
	 * @return ResultVo
     *      <tt>wealthId       :String:计划主键</tt><br>
     *      <tt>lendingType    :String:计划名称</tt><br>
     *      <tt>lendingNo      :String:项目期数</tt><br>
     *      <tt>planTotalAmount:String:项目总额(元)</tt><br>
     *      <tt>typeTerm       :String:项目期限(月)</tt><br>
     *      <tt>yearRate       :String:年化收益率</tt><br>
     *      <tt>awardRate      :String:奖励利率</tt><br>
     *      <tt>releaseDate    :String:发布日期    </tt><br>
     *      <tt>effectDate     :String:生效日期</tt><br>
     *      <tt>endDate        :String:到期日期</tt><br>
     *      <tt>wealthStatus   :String:项目状态</tt><br>
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "起始值不能为空!", number = true, numberMessage = "起始值必须为数字"),
			@Rule(name = "length", required = true, requiredMessage = "长度不能为空!", number = true, numberMessage = "长度必须为数字")
	})
	public ResultVo queryWealthList(Map<String, Object> parmas)throws SLException;
	
	/**
	 * 查看项目
	 * 
	 * @author zhiwen_feng
	 * @date 2016-02-23
	 * @param params
     *      <tt>wealthId:String:计划主键</tt><br>
	 * @return ResultVo
     *      <tt>wealthId       :String:计划主键</tt><br>
     *      <tt>lendingType    :String:计划名称</tt><br>
     *      <tt>lendingNo      :String:项目期数</tt><br>
     *      <tt>planTotalAmount:String:项目总额(元)</tt><br>
     *      <tt>investMinAmount:String:起投金额</tt><br>
     *      <tt>increaseAmount :String:递增金额</tt><br>
     *      <tt>incomeType     :String:结算方式</tt><br>
     *      <tt>awardRate      :String:奖励利率</tt><br>
     *      <tt>typeTerm       :String:项目期限(月)</tt><br>
     *      <tt>yearRate       :String:年化收益率</tt><br>
     *      <tt>releaseDate    :String:发布日期    </tt><br>
     *      <tt>effectDate     :String:生效日期</tt><br>
     *      <tt>endDate        :String:到期日期</tt><br>
     *      <tt>wealthStatus   :String:项目状态</tt><br>
     *      <tt>wealthDescr    :String:项目描述</tt><br>
     *      <tt>auditList      :String:审核列表:List<Map<String, Object>></tt><br>
     *      <tt>审核ID          :String:         auditId</tt><br>
     *      <tt>审核时间           :String:         auditDate</tt><br>
     *      <tt>审核人员           :String:         auditUser</tt><br>
     *      <tt>审核结果           :String:         auditStatus</tt><br>
     *      <tt>审核备注           :String:         auditMemo</tt><br>
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "wealthId", required = true, requiredMessage = "计划主键id不能为空!")
	})
	public ResultVo queryWealthDetailById(Map<String, Object> params) throws SLException;
	
	/**
	 * 新建/编辑项目
	 * 
	 * @author zhiwen_feng
	 * @date 2016-02-23
	 * @param params
     *      <tt>wealthId       :String:计划主键（编辑时非空）</tt><br>
     *      <tt>wealthTypeId   :String:计划名称ID</tt><br>
     *      <tt>planTotalAmount:String:项目总额(元)</tt><br>
     *      <tt>investMinAmount:String:起投金额</tt><br>
     *      <tt>increaseAmount :String:递增金额</tt><br>
     *      <tt>investMaxAmount:String:最大投资金额</tt></br>
     *      <tt>incomeType     :String:结算方式</tt><br>
     *      <tt>awardRate      :String:奖励利率</tt><br>
     *      <tt>releaseDate    :String:发布日期    </tt><br>
     *      <tt>effectDate     :String:生效日期</tt><br>
     *      <tt>endDate        :String:到期日期</tt><br>
     *      <tt>wealthStatus   :String:项目状态</tt><br>
     *      <tt>wealthDescr    :String:项目描述</tt><br>
     *      <tt>userId         :String:创建人）</tt><br>
	 * @return ResultVo
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "wealthTypeId", required = true, requiredMessage = "计划名称ID不能为空!"),
			@Rule(name = "planTotalAmount", required = true, requiredMessage = "项目总额不能为空!", number = true, numberMessage = "项目总额只能是数字", digist = true, digistMessage = "项目总额只能是整数"),
			@Rule(name = "investMinAmount", required = true, requiredMessage = "起投金额不能为空!", number = true, numberMessage = "起投金额只能是数字"),
			@Rule(name = "increaseAmount", required = true, requiredMessage = "递增金额不能为空!", number = true, numberMessage = "递增金额只能是数字"),
			@Rule(name = "releaseDate", required = true, requiredMessage = "发布日期不能为空!"),
			@Rule(name = "effectDate", required = true, requiredMessage = "生效日期不能为空!"),
			@Rule(name = "wealthStatus", required = true, requiredMessage = "项目状态不能为空!"),
			@Rule(name = "userId", required = true, requiredMessage = "创建人不能为空!")
	})
	public ResultVo saveWealth(Map<String, Object> params)throws SLException;
	
	/**
	 * 
	 * 用户计划投资列表
	 * 
	 * @date 2016-02-23
	 * @author zhiwen_feng
	 * @param params
     *      <tt>start          :String:起始值</tt><br>
     *      <tt>length         :String:长度</tt><br>
     *      <tt>custName       :String:用户名（可选）</tt><br>
     *      <tt>investStatus   :String:投资状态（可选）</tt><br>
     *      <tt>lendingType    :String:出借方式（可选）</tt><br>
     *      <tt>typeTerm       :String:项目期限（可选）</tt><br>
     *      <tt>beginEffectDate:String:开始起息日（可选）</tt><br>
     *      <tt>endEffectDate  :String:结束起息日（可选）</tt><br>
     *      <tt>beginEndDate   :String:开始到期日期（可选）</tt><br>
     *      <tt>endEndDate     :String:结束到期日期（可选）</tt><br>
     *      <tt>beginAtoneDate :String:开始赎回日期（可选）</tt><br>
     *      <tt>endAtoneDate   :String:结束赎回日期（可选）</tt><br>
	 * @return ResultVo
     *      <tt>investId     :String:投资主键</tt><br>
     *      <tt>wealthId     :String:计划主键</tt><br>
     *      <tt>custId       :String:客户主键</tt><br>
     *      <tt>custName     :String:用户名</tt><br>
     *      <tt>investAmount :String:投资金额</tt><br>
     *      <tt>lendingType  :String:出借方式</tt><br>
     *      <tt>typeTerm     :String:项目期数</tt><br>
     *      <tt>investStatus :String:投资状态</tt><br>
     *      <tt>effectDate   :String:起息日期</tt><br>
     *      <tt>endDate      :String:到期日期</tt><br>
     *      <tt>atoneAmount  :String:赎回价值</tt><br>
     *      <tt>accountAmount:String:到帐金额</tt><br>
     *      <tt>atoneDate    :String:赎回时间</tt><br>
     *      <tt>custManageName:String:客户经理</tt><br>
     *      <tt>loginName    :String:客户昵称</tt><br>
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "起始值不能为空!", number = true, numberMessage = "起始值必须为数字"),
			@Rule(name = "length", required = true, requiredMessage = "长度不能为空!", number = true, numberMessage = "长度必须为数字")
	})
	public ResultVo queryWealthJoinList(Map<String, Object> params)throws SLException;
	
	/**
	 * 审核项目
	 * 
	 * @author zhiwen_feng
	 * @date 2016-02-23
	 * @param params
     *      <tt>wealthId   :String:计划主键</tt><br>
     *      <tt>auditStatus:String:审核状态</tt><br>
     *      <tt>auditMemo  :String: 审核备注</tt><br>
     *      <tt>userId     :String:创建人</tt><br>
	 * @return ResultVo
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "wealthId", required = true, requiredMessage = "计划主键不能为空!"),
			@Rule(name = "auditStatus", required = true, requiredMessage = "审核状态不能为空!"),
			@Rule(name = "auditMemo", required = true, requiredMessage = "审核备注不能为空!"),
			@Rule(name = "userId", required = true, requiredMessage = "创建人不能为空!")
	})
	public ResultVo auditWealth(Map<String, Object> params)throws SLException;
	
	/**
	 * 发布项目
	 * 
	 * @date 2016-02-24
	 * @author zhiwen_feng
	 * @param params
     *      <tt>wealthId:String:计划主键</tt><br>
     *      <tt>userId  :String:创建人</tt><br>
	 * @return ResultVo
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "wealthId", required = true, requiredMessage = "计划主键不能为空!"),
			@Rule(name = "userId", required = true, requiredMessage = "创建人不能为空!")
	})
	public ResultVo publishWealth(Map<String, Object> params)throws SLException;
	
	/**
	 * 项目列表
	 * 
	 * @author zhiwen_feng
	 * @date 2016-02-24
	 * @param params
     *      <tt>start           :String:起始值</tt><br>
     *      <tt>length          :String:长度</tt><br>
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
     *      <tt>data                    :String:List<Map<String,Object>></tt><br>
     *      <tt>wealthId                :String:计划主键</tt><br>
     *      <tt>lendingType             :String:计划名称</tt><br>
     *      <tt>lendingNo               :String:项目期数</tt><br>
     *      <tt>planTotalAmount         :String:项目总额(元)</tt><br>
     *      <tt>alreadyInvestAmount     :String:已募集金额（元）</tt><br>
     *      <tt>waitingMatchAmount      :String:待匹配金额(元)</tt><br>
     *      <tt>investScale             :String:已投百分比</tt><br>
     *      <tt>typeTerm                :String:项目期限(月)</tt><br>
     *      <tt>yearRate                :String:年化收益率</tt><br>
     *      <tt>awardRate               :String:奖励利率</tt><br>
     *      <tt>incomeType              :String:结算方式</tt><br>
     *      <tt>releaseDate             :String:发布日期    </tt><br>
     *      <tt>effectDate              :String:生效日期</tt><br>
     *      <tt>endDate                 :String:到期日期</tt><br>
     *      <tt>wealthStatus            :String:项目状态</tt><br>
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "起始值不能为空!", number = true, numberMessage = "起始值必须为数字"),
			@Rule(name = "length", required = true, requiredMessage = "长度不能为空!", number = true, numberMessage = "长度必须为数字")
	})
	public ResultVo queryAllWealthList(Map<String, Object> params)throws SLException;
	
	/**
	 * 待匹配列表
	 * 
	 * @author zhiwen_feng
	 * @date 2016-02-25
	 * @param params
     *      <tt>start           :String:起始值</tt><br>
     *      <tt>length          :String:长度</tt><br>
     *      <tt>wealthId		:String:优选计划id
     *      <tt>custName        :String:用户名（可选）</tt><br>
     *      <tt>beginOperateDate:String:开始操作时间（可选）</tt><br>
     *      <tt>endOperateDate
	 * @return ResultVo
     *      <tt>custName          :String:用户名</tt><br>
     *      <tt>waitingMatchAmount:String:待匹配金额(元)</tt><br>
     *      <tt>operateDate       :String:最后修改时间</tt><br>
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "起始值不能为空!", number = true, numberMessage = "起始值必须为数字"),
			@Rule(name = "length", required = true, requiredMessage = "长度不能为空!", number = true, numberMessage = "长度必须为数字")
	})
	public ResultVo queryWaitingMatchList(Map<String, Object> params)throws SLException;
	
	/**
	 * 债权预算
	 * 
	 * @author zhiwen_feng
	 * @date 2016-02-25
	 * @param params
     *      <tt>currentDate:String:日期（可选）</tt><br>
	 * @return ResultVo
     *      <tt>exceptRepaymentAmount:String:预计还款金额（元）</tt><br>
     *      <tt>exceptAtoneAmount    :String:预计退出金额（元）</tt><br>
     *      <tt>exceptUsableValue    :String:预计可用债权价值（元）</tt><br>
     *      <tt>exceptMatchAmount    :String:预计待匹配金额（元）</tt><br>

	 * @throws SLException
	 */
	public ResultVo queryMatchLoanList(Map<String, Object> params)throws SLException;
	
	/**
	 * 自动生效优选计划
	 * 
	 * @date 2016-02-26
	 * @author zhiwen_feng
	 * @param params
	 * @return
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "wealthId", required = true, requiredMessage = "计划主键不能为空!"),
			@Rule(name = "userId", required = true, requiredMessage = "创建人不能为空!")
	})
	public ResultVo autoReleaseWealth(Map<String, Object> params)throws SLException;
	
	/**
	 * 优选计划列表（精选）
	 * 
	 * @author zhiwen_feng
	 * @date 2016-02-26
	 * @param params
     *      <tt>typeName:String:产品类型名称（优选计划）</tt><br>
	 * @return ResultVo
     *      <tt>wealthId       :String:计划主键</tt><br>
     *      <tt>lendingType    :String:计划名称</tt><br>
     *      <tt>lendingNo      :String:项目期数</tt><br>
     *      <tt>planTotalAmount:String:项目总额(元)</tt><br>
     *      <tt>investMinAmount:String:起投金额:</tt><br>
     *      <tt>investMaxAmount:String:投资上限 </tt><br>
     *      <tt>increaseAmount :String:递增金额 </tt><br>
     *      <tt>currUsableValue:String:剩余金额</tt><br>
     *      <tt>investScale    :String:已投百分比</tt><br>
     *      <tt>typeTerm       :String:项目期限(月)</tt><br>
     *      <tt>yearRate       :String:年化收益率</tt><br>
     *      <tt>awardRate      :String:奖励利率</tt><br>
     *      <tt>incomeType     :String:结算方式</tt><br>
     *      <tt>releaseDate    :String:发布日期    </tt><br>
     *      <tt>effectDate     :String:生效日期</tt><br>
     *      <tt>endDate        :String:到期日期</tt><br>
     *      <tt>wealthStatus   :String:项目状态</tt><br>
	 * @throws SLException
	 */
	public ResultVo queryPriorityWealthList(Map<String, Object> params)throws SLException;
	
	/**
	 * 查看优选计划
	 * 
	 * @date 2016-02-26
	 * @author zhiwen_feng
	 * @param params
     *      <tt>wealthId:String:计划主键</tt><br>
     *      <tt>custId  :String:客户ID</tt><br>
	 * @return ResultVo
     *      <tt>wealthId           :String:计划主键</tt><br>
     *      <tt>lendingType        :String:计划名称</tt><br>
     *      <tt>lendingNo          :String:项目期数</tt><br>
     *      <tt>planTotalAmount    :String:项目总额(元)</tt><br>
     *      <tt>investMinAmount    :String:起投金额</tt><br>
     *      <tt>increaseAmount     :String:递增金额</tt><br>
     *      <tt>incomeType         :String:结算方式</tt><br>
     *      <tt>awardRate          :String:奖励利率</tt><br>
     *      <tt>typeTerm           :String:项目期限(月)</tt><br>
     *      <tt>yearRate           :String:年化收益率</tt><br>
     *      <tt>releaseDate        :String:发布日期    </tt><br>
     *      <tt>effectDate         :String:生效日期</tt><br>
     *      <tt>endDate            :String:到期日期</tt><br>
     *      <tt>wealthStatus       :String:项目状态</tt><br>
     *      <tt>wealthDescr        :String:项目描述</tt><br>
     *      <tt>reminderAmount     :String:剩余可够份额</tt><br>
     *      <tt>currUsableAmount   :String:用户可够份额</tt><br>
     *      <tt>alreadyInvestAmount:String:已投金额</tt><br>
     *      <tt>alreadyInvestScale :String:已投比例</tt><br>
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "wealthId", required = true, requiredMessage = "计划主键不能为空!")
	})
	public ResultVo queryWealthDetail(Map<String, Object> params)throws SLException;
	
	/**
	 * 马上投资
	 * 
	 * @date 2016-02-27
	 * @author zhiwen_feng
	 * @param params
     *      <tt>wealthId   :String:计划主键</tt><br>
     *      <tt>custId     :String:客户ID</tt><br>
     *      <tt>tradeAmount:String:投资金额</tt><br>
     *      <tt>channelNo  :String:渠道号（可以为空）</tt><br>
     *      <tt>meId       :String:设备ID（可以为空）</tt><br>
     *      <tt>meVersion  :String:设备版本号（可以为空）</tt><br>
     *      <tt>appSource  :String:设备来源（可以为空）</tt><br>
     *      <tt>ipAddress  :String:ip地址（可以为空）</tt><br>
	 * @return ResultVo
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "wealthId", required = true, requiredMessage = "计划主键不能为空!"),
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空!"),
			@Rule(name = "tradeAmount", required = true, requiredMessage = "投资金额不能为空!", number = true, numberMessage = "投资金额必须是数字！", digist =true, digistMessage = "投资金额必须是整数！")
	})
	public ResultVo joinWealth(Map<String, Object> params)throws SLException;
	
	/**
	 * 收益计算器
	 * 
	 * @date 2016-02-27
	 * @author zhiwen_feng
	 * @param params
     *      <tt>wealthTypeId:String:计划主键</tt><br>
     *      <tt>tradeAmount :String:金额</tt><br>
	 * @return ResultVo
     *      <tt>totalAmount :String:持有份额</tt><br>
     *      <tt>incomeAmount:String:预期收益</tt><br>
     *      <tt>awardAmount :String:奖励收益</tt><br>
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "wealthId", required = true, requiredMessage = "计划主键不能为空!"),
			@Rule(name = "tradeAmount", required = true, requiredMessage = "金额不能为空!", number = true, numberMessage = "金额必须是数字！")
	})
	public ResultVo caclWealth(Map<String, Object> params)throws SLException;
	
	/**
	 * 收益计算器
	 * 
	 * @date 2016-02-27
	 * @author zhiwen_feng
	 * @param params
     *      <tt>wealthTypeId:String:计划主键</tt><br>
     *      <tt>tradeAmount :String:金额</tt><br>
	 * @return ResultVo
     *      <tt>totalAmount :String:持有份额</tt><br>
     *      <tt>incomeAmount:String:预期收益</tt><br>
     *      <tt>awardAmount :String:奖励收益</tt><br>
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "wealthTypeId", required = true, requiredMessage = "计划主键不能为空!"),
			@Rule(name = "tradeAmount", required = true, requiredMessage = "金额不能为空!", number = true, numberMessage = "金额必须是数字！")
	})
	public ResultVo caclWealth2(Map<String, Object> params)throws SLException;
	
	/**
	 * 投资记录
	 * 
	 * @DATE 2016-02-27
	 * @author zhiwen_feng
	 * @param params
     *      <tt>start   :String:起始值</tt><br>
     *      <tt>length  :String:长度</tt><br>
     *      <tt>wealthId:String:项目ID</tt><br>
	 * @return ResultVo
     *      <tt>investDate  :String:购买时间</tt><br>
     *      <tt>investAmount:String:购买金额</tt><br>
     *      <tt>loginName   :String:用户名称</tt><br> 
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "起始值不能为空!", number = true, numberMessage = "起始值必须为数字"),
			@Rule(name = "length", required = true, requiredMessage = "长度不能为空!", number = true, numberMessage = "长度必须为数字")
	})
	public ResultVo queryWealthInvestList(Map<String, Object> params)throws SLException;
	
	/**
	 * 自动撮合
	 * 
	 * @author zhiwen_feng
	 * @date 2016-02-29
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public ResultVo autoMatchLoan(Map<String, Object> params)throws SLException;
	
	
	/**
	 * 提前赎回查看
	 *
	 * @author  wangjf
	 * @date    2016年3月2日 上午10:55:18
	 * @param params
     *      <tt>wealthId:String:计划主键</tt><br>
     *      <tt>custId  :String:客户ID</tt><br> 
	 * @return
     *      <tt>wealthId        :String:计划主键</tt><br>
     *      <tt>custId          :String:客户ID</tt><br>
     *      <tt>lendingType     :String:计划名称</tt><br>
     *      <tt>investAmount    :String:投资金额</tt><br>
     *      <tt>atoneExpenses   :String:赎回费用</tt><br>
     *      <tt>atoneTotalAmount:String:预计到帐金额</tt><br>
     *      <tt>atoneDate       :String:预计到帐日期</tt><br>
     *      <tt>lendingNo       :String:期数</tt><br>
	 */
	public ResultVo queryAdvancedWealthAtoneDetail(Map<String, Object> params);
	
	/**
	 * 提前赎回
	 *
	 * @author  wangjf
	 * @date    2016年3月2日 上午10:55:20
	 * @param params
     *      <tt>wealthId     :String:计划主键</tt><br>
     *      <tt>custId       :String:客户ID</tt><br>
     *      <tt>tradePassword:String:交易密码</tt><br>
	 * @return
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "wealthId", required = true, requiredMessage = "计划主键不能为空!"),
			@Rule(name = "custId", required = true, requiredMessage = "客户主键不能为空!"),
			@Rule(name = "tradePassword", required = true, requiredMessage = "交易密码不能为空!")
	})
	public ResultVo advancedAtoneWealth(Map<String, Object> params)throws SLException;
	
	/**
	 * 优选计划收益展示
	 * 
	 * @author zhiwen_feng
	 * @param params
     *      <tt>custId:String:客户ID</tt><br>
	 * @return ResultVo 
     *      <tt>created             :String:投资中:Map<String, Object></tt><br>
     *      <tt>totalCounts         :String:     项目数</tt><br>
     *      <tt>totalTradeAmount    :String:     投资金额</tt><br>
     *      <tt>totalIncomeAmount   :String:     收益</tt><br>
     *      <tt>totalPrevTradeAmount:String:     历史投资金额</tt><br>
     *      <tt>doing               :String:收益中:Map<String, Object></tt><br>
     *      <tt>totalCounts         :String:     项目数</tt><br>
     *      <tt>totalTradeAmount    :String:     投资金额</tt><br>
     *      <tt>totalIncomeAmount   :String:     收益</tt><br>
     *      <tt>totalPrevTradeAmount:String:     历史投资金额</tt><br>
     *      <tt>finished            :String:已结束:Map<String, Object></tt><br>
     *      <tt>totalCounts         :String:     项目数</tt><br>
     *      <tt>totalTradeAmount    :String:     投资金额</tt><br>
     *      <tt>totalIncomeAmount   :String:     收益</tt><br>
     *      <tt>totalPrevTradeAmount:String:     历史投资金额</tt><br>
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空!")
	})
	public ResultVo queryWealthTotalIncome(Map<String, Object> params)throws SLException;
	
	/**
	 * 优选计划列表(投资人)
	 * 
	 * @author zhiwen_feng
	 * @date 2016-03-03
	 * @param params
     *      <tt>start          :String:起始值</tt><br>
     *      <tt>length         :String:长度</tt><br>
     *      <tt>beginEffectDate:String:开始生效日期（可选）</tt><br>
     *      <tt>endEffectDate  :String:结束生效日期（可选）</tt><br>
     *      <tt>investStatus   :String:投资状态（可以为空）</tt><br>
	 * @return ResultVo
     *      <tt>wealthId       :String:计划主键</tt><br>
     *      <tt>lendingType    :String:计划名称</tt><br>
     *      <tt>lendingNo      :String:项目期数</tt><br>
     *      <tt>planTotalAmount:String:项目总额(元)</tt><br>
     *      <tt>investMinAmount:String:起投金额:</tt><br>
     *      <tt>investMaxAmount:String:投资上限 </tt><br>
     *      <tt>increaseAmount :String:递增金额 </tt><br>
     *      <tt>typeTerm       :String:项目期限(月)</tt><br>
     *      <tt>yearRate       :String:年化收益率</tt><br>
     *      <tt>awardRate      :String:奖励利率</tt><br>
     *      <tt>incomeType     :String:结算方式</tt><br>
     *      <tt>releaseDate    :String:发布日期    </tt><br>
     *      <tt>effectDate     :String:生效日期</tt><br>
     *      <tt>endDate        :String:到期日期</tt><br>
     *      <tt>wealthStatus   :String:项目状态</tt><br>
     *      <tt>investAmount   :String:投资金额</tt><br>
     *      <tt>exceptAmount   :String:待收收益</tt><br>
     *      <tt>investStatus   :String:投资状态</tt><br>
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "start", required = true, requiredMessage = "起始值不能为空!", number = true, numberMessage = "起始值必须为数字"),
			@Rule(name = "length", required = true, requiredMessage = "长度不能为空!", number = true, numberMessage = "长度必须为数字")
	})
	public ResultVo queryMyWealthList(Map<String, Object> params)throws SLException;
	
	/**
	 * 优选计划查看
	 * 
	 * @author zhiwen_feng
	 * @date 2016-03-03
	 * @param params
     *      <tt>wealthId:String:计划主键</tt><br>
     *      <tt>custId  :String:客户ID</tt><br>
	 * @return ResultVo
     *      <tt>wealthId      :String:计划主键</tt><br>
     *      <tt>lendingType   :String:计划名称</tt><br>
     *      <tt>lendingNo     :String:项目期数</tt><br>
     *      <tt>typeTerm      :String:项目期限(月)</tt><br>
     *      <tt>yearRate      :String:年化收益率</tt><br>
     *      <tt>awardRate     :String:奖励利率</tt><br>
     *      <tt>incomeType    :String:结算方式</tt><br>
     *      <tt>releaseDate   :String:发布日期    </tt><br>
     *      <tt>effectDate    :String:生效日期</tt><br>
     *      <tt>endDate       :String:到期日期</tt><br>
     *      <tt>wealthStatus  :String:项目状态</tt><br>
     *      <tt>investAmount  :String:投资金额</tt><br>
     *      <tt>exceptAmount  :String:预期收益</tt><br>
     *      <tt>receviedAmount:String:已收收益</tt><br>
     *      <tt>investStatus  :String:投资状态（当前状态）</tt><br>
     *      <tt>reminderDay   :String:剩余天数</tt><br>
     *      <tt>accountAmount :String:可用余额</tt><br>
     *      <tt>currUsableAmount :String:剩余可投金额</tt><br>
     *      <tt>cleanupDate :String:退出日期</tt><br>
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "wealthId", required = true, requiredMessage = "计划主键不能为空!"),
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空!")
	})
	public ResultVo queryMyWealthDetail(Map<String, Object> params)throws SLException;
	
	/**
	 * 我的债权列表
	 * 
	 * @date 2016-03-04
	 * @author zhiwen_feng
	 * @param params
     *      <tt>wealthId:String:计划主键</tt><br>
     *      <tt>custId  :String:客户ID</tt><br>
	 * @return ResultVo
     *      <tt>wealthHoldId     :String:用户债权持有ID</tt><br>
     *      <tt>loanId           :String:债权ID</tt><br>
     *      <tt>custId           :String:客户ID</tt><br>
     *      <tt>loanCode         :String:债权编号</tt><br>
     *      <tt>loanCustName     :String:借款人</tt><br>
     *      <tt>investAmount     :String:投标金额</tt><br>
     *      <tt>investDate       :String:投标时间</tt><br>
     *      <tt>totalExceptAmount:String:应收回款</tt><br>
     *      <tt>receivedAmount   :String:已收回款</tt><br>
     *      <tt>exceptAmount     :String:待收回款</tt><br>
     *      <tt>repaymentStatus  :String:还款状态</tt><br>
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "wealthId", required = true, requiredMessage = "计划主键不能为空!"),
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空!")
	})
	public ResultVo queryMyWealthLoan(Map<String, Object> params)throws SLException;
	
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
	@Rules(rules = { 
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空!")
	})
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
     *      <tt>custName       :String:投资用户姓名（若未投资则为空）</tt><br>
     *      <tt>credentialsCode:String:投资用户身份证（若未投资则为空）</tt><br>
     *      <tt>mobile         :String:投资用户手机（若未投资则为空）</tt><br>
     *      <tt>loginName      :String:投资用户昵称（若未投资则为空）</tt><br>
     *      <tt>companyName    :String:公司名称</tt><br>
     *      <tt>companyAddress :String:公司地址</tt><br>
     *      <tt>companyUrl     :String:公司网址</tt><br>
     *      <tt>companyTel     :String:公司电话</tt><br>
     *      <tt>lendingType    :String:计划名称</tt><br>
     *      <tt>lendingNo      :String:项目期数</tt><br>
     *      <tt>investAmount   :String:投资金额（若未投资则为空）</tt><br>
     *      <tt>incomeType     :String:结算方式</tt><br>
     *      <tt>yearRate       :String:年化收益率</tt><br>
     *      <tt>awardRate      :String:奖励利率</tt><br>
     *      <tt>typeTerm       :String:项目期限(月)</tt><br>
     *      <tt>effectDate     :String:生效日期（计划开始时间）（申请结束时间）（投资开始时间）</tt><br>
     *      <tt>endDate        :String:到期日期（计划结束时间）（投资开始结束）</tt><br>
     *      <tt>investMinAmount:String:起投金额</tt><br>
     *      <tt>increaseAmount :String:递增金额</tt><br>
     *      <tt>releaseDate    :String:发布日期（申请开始时间）</tt><br>
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "wealthId", required = true, requiredMessage = "计划主键不能为空!"),
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空!")
	})
	public ResultVo queryWealthContract(Map<String, Object> params)throws SLException;
	
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
	@Rules(rules = { 
			@Rule(name = "wealthId", required = true, requiredMessage = "计划主键不能为空!"),
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空!"),
			@Rule(name = "loanId", required = true, requiredMessage = "债权ID不能为空!")
	})
	public ResultVo queryWealthLoanContract(Map<String, Object> params)throws SLException;
	
	/**
	 * 
	 * 优选计划列表
	 * 
	 *@author zhiwen_feng
	 *@date 2016-03-07 
	 * @param params
     *      <tt>start :String:起始值</tt><br>
     *      <tt>length:String:长度</tt><br>
	 * @return ResultVo 
     *      <tt>wealthId           :String:计划主键</tt><br>
     *      <tt>lendingType        :String:计划名称</tt><br>
     *      <tt>lendingNo          :String:项目期数</tt><br>
     *      <tt>planTotalAmount    :String:项目总额(元)</tt><br>
     *      <tt>alreadyInvestAmount:String:已募集金额（元）</tt><br>
     *      <tt>waitingMatchAmount :String:待匹配金额(元)</tt><br>
     *      <tt>investScale        :String:已投百分比</tt><br>
     *      <tt>typeTerm           :String:项目期限(月)</tt><br>
     *      <tt>yearRate           :String:年化收益率</tt><br>
     *      <tt>awardRate          :String:奖励利率</tt><br>
     *      <tt>incomeType         :String:结算方式</tt><br>
     *      <tt>releaseDate        :String:发布日期    </tt><br>
     *      <tt>effectDate         :String:生效日期</tt><br>
     *      <tt>endDate            :String:到期日期</tt><br>
     *      <tt>wealthStatus       :String:项目状态</tt><br>
	 * @throws SLException
	 */
	public ResultVo queryShowWealthList(Map<String, Object> params)throws SLException;
	
	/**
	 * 首页接口 
	 * 
	 * @author zhiwen_feng
	 * @param params
	 * @return ResultVo
     *      <tt>wealthId            :String:计划主键/项目主键 </tt><br>
     *      <tt>lendingType         :String:计划名称/项目名称 </tt><br>
     *      <tt>lendingNo           :String:项目期数/项目期数 </tt><br>
     *      <tt>planTotalAmount     :String:项目总额(元) </tt><br>
     *      <tt>investMinAmount     :String:起投金额: </tt><br>
     *      <tt>investMaxAmount     :String:投资上限 </tt><br>
     *      <tt>increaseAmount      :String:递增金额 </tt><br>
     *      <tt>currUsableValue     :String:剩余金额 </tt><br>
     *      <tt>investScale         :String:已投百分比 </tt><br>
     *      <tt>typeTerm            :String:项目期限(月) </tt><br>
     *      <tt>yearRate            :String:年化收益率 </tt><br>
     *      <tt>awardRate           :String:奖励利率 </tt><br>
     *      <tt>incomeType          :String:结算方式 </tt><br>
     *      <tt>releaseDate         :String:发布日期 </tt><br>
     *      <tt>effectDate          :String:生效日期 </tt><br>
     *      <tt>endDate             :String:到期日期 </tt><br>
     *      <tt>wealthStatus        :String:项目状态 </tt><br>
     *      <tt>currUsableValue     :String:用户可够份额 </tt><br>
     *      <tt>type                :String:类型（优选计划or企业借款） </tt><br>
     *      <tt>alreadyInvestPeoples:String:参与人数 </tt><br>
	 * @throws SLException
	 */
	public ResultVo queryPriority(Map<String, Object> params)throws SLException;
	
	/**
	 * 优选债权
	 * 
	 * @author zhiwen_feng
	 * @date 2016-05-10
	 * @param params
	 * 			<tt>wealthId:String:计划主键</tt><br>
	 *          <tt>start :String:起始值</tt><br>
     *          <tt>length:String:长度</tt><br>
	 * @return ResultVo
     *      <tt>loanId       :String:借款id</tt><br>
     *      <tt>loanCode     :String:债权编号</tt><br>
     *      <tt>loanCustName :String:借款人</tt><br>
     *      <tt>loanDesc     :String:借款用途</tt><br>
     *      <tt>loanAmount   :String:借款金额</tt><br>
     *      <tt>assetTypeCode:String:债权来源</tt><br>
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "wealthId", required = true, requiredMessage = "计划主键不能为空!"),
			@Rule(name = "start", required = true, requiredMessage = "起始值不能为空!", number = true, numberMessage = "起始值必须为数字"),
			@Rule(name = "length", required = true, requiredMessage = "长度不能为空!", number = true, numberMessage = "长度必须为数字")
	})
	public ResultVo queryLoanInfoByWealthId(Map<String, Object> params)throws SLException;
	
	/**
	 * 债权详情
	 * 
	 * @author zhiwen_feng
	 * @date 2016-05-10
	 * @param params
	 * 			<tt>loanId        :String:债权id</tt><br>
	 * @return ResultVo
     *      <tt>loanCode        :String:债权编号</tt><br>
     *      <tt>assetTypeCode   :String:债权来源	</tt><br>
     *      <tt>loanDesc        :String:借款用途</tt><br>
     *      <tt>repaymentMethod :String:还款方式</tt><br>
     *      <tt>loanAmount      :String:借款金额</tt><br>
     *      <tt>loanTerm        :String:借款期限</tt><br>
     *      <tt>guaranteeMethod :String:保障方式</tt><br>
     *      <tt>loanCustName    :String:姓名</tt><br>
     *      <tt>sex             :String:性别</tt><br>
     *      <tt>age             :String:年龄</tt><br>
     *      <tt>jobType         :String:职业类型</tt><br>
     *      <tt>education       :String:学历</tt><br>
     *      <tt>marriage        :String:婚否</tt><br>
     *      <tt>credentialsType:String:证件类型</tt><br>
     *      <tt>credentialsCode :String:证件号码</tt><br>
	 * @throws SLException
	 */
	public ResultVo queryLoanInfoDetailByLoanId(Map<String, Object> params)throws SLException;
	
	/**
	 * 查询业务员分享产品信息
	 * 
	 * @param params
	 * 			<tt>mobile        :String:手机号码</tt><br>	
	 * 			<tt>ids           :String:手机号码</tt><br>	
	 * @return
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "mobile", 
						required = true, requiredMessage = "手机号码不能为空或包含空格", 
						mobile = true, mobileMessage = "手机格式错误"),
			@Rule(name = "ids", required = true, requiredMessage = "ids不能为空")
		})
	public ResultVo queryEmployeeWealthList(Map<String, Object> params)throws SLException;
}
