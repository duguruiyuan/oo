/** 
 * @(#)ParamEntityService.java 1.0.0 2015年5月1日 上午10:45:59  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;

/**   
 * 参数服务接口
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年5月1日 上午10:45:59 $ 
 */
public interface ParamService {

	
	/**
	 * 获取投资生效时间
	 * 注：必须为时间Time（HH:mm:ss），如15:00:00
	 *
	 * @author  wangjf
	 * @date    2015年5月1日 下午2:36:35
	 * @return
	 */
	public String findInvestValidTime();

	/**
	 * 取是否是固定收益
	 *
	 * @author  wangjf
	 * @date    2015年5月1日 下午2:39:42
	 * @return
	 */
	public Boolean findFixLimited();
	
	/**
	 * 取固定收益额度
	 *
	 * @author  wangjf
	 * @date    2015年5月1日 下午2:40:20
	 * @return
	 */
	public BigDecimal findFixLimitedAmount();
	
	/**
	 * 取提现比例
	 *
	 * @author  wangjf
	 * @date    2015年5月1日 下午2:40:47
	 * @return
	 */
	public BigDecimal findFixLimitedScalce();
	
	/**
	 * 取每天最大可赎回金额
	 *
	 * @author  wangjf
	 * @date    2015年5月1日 下午2:41:41
	 * @return
	 */
	public BigDecimal findMaxDayWithdrawAmount();
	
	/**
	 * 取每天最多可赎回次数
	 *
	 * @author  wangjf
	 * @date    2015年5月1日 下午2:42:13
	 * @return
	 */
	public long findMaxDayWithdrawCount();
	
	/**
	 * 取每月最多可赎回次数
	 *
	 * @author  wangjf
	 * @date    2015年5月1日 下午2:42:46
	 * @return
	 */
	public long findMaxMonthWithdrawCount();
	
	/**
	 * 个人累积投资金额
	 *
	 * @author  wangjf
	 * @date    2015年5月8日 下午3:11:53
	 * @return
	 */
	public BigDecimal findMaxWithdrawAmount();
	
	/**
	 * 个人累积投资最大限额
	 *
	 * @author  zhangzs
	 * @date    2015年8月15日 下午5:17:36
	 * @return
	 */
	public BigDecimal findMaxInvestAmount();
	
	/**
	 * 注册送体验金金额
	 *
	 * @author  caoyi
	 * @date    2015年5月16日 下午13:40:53
	 * @return
	 */
	public BigDecimal findActivityRegistAmount();
	
	/**
	 * 推荐奖励
	 *
	 * @author  wangjf
	 * @date    2015年5月18日 下午2:48:18
	 * @ParamEntity startLevel 起始级别
	 * @ParamEntity endLevel 结束级别
	 * @return
	 */
	public BigDecimal findActivityRecommend(String startLevel, String endLevel);
	
	/**
	 * 获取认证充值手续费比例
	 *
	 * @author  wangjf
	 * @date    2015年5月20日 上午10:02:28
	 * @return
	 */
	public BigDecimal findRechargeAuthPayExpenseScale();
	
	/**
	 * 获取认证充值最小手续费
	 *
	 * @author  wangjf
	 * @date    2015年5月20日 上午10:02:28
	 * @return
	 */
	public BigDecimal findRechargeAuthPayExpenseMinAmount();
	
	/**
	 * 获取网银充值手续费比例
	 *
	 * @author  wangjf
	 * @date    2015年5月20日 上午10:02:28
	 * @return
	 */
	public BigDecimal findRechargeBankPayExpenseScale();
	
	/**
	 * 获取网银充值最小手续费
	 *
	 * @author  wangjf
	 * @date    2015年5月20日 上午10:02:28
	 * @return
	 */
	public BigDecimal findRechargeBankPayExpenseMinAmount();
	
	/**
	 * 获取提现手续费
	 *
	 * @author  caoyi
	 * @date    2015年5月26日 下午14:11:28
	 * @return
	 */
	public BigDecimal findWithDrawExpenseAmount();
	
	/**
	 * 获取实名认证手续费
	 *
	 * @author  caoyi
	 * @date    2015年5月26日 下午14:11:28
	 * @return
	 */
	public BigDecimal findRealNameExpenseAmount();
	
	/**
	 * 获取实名认证手续费
	 *
	 * @author  caoyi
	 * @date    2015年5月26日 下午14:11:28
	 * @return
	 */
	public BigDecimal findRealNameExpenseAmount(String thirdPartyName);
	
	/**
	 * 获取认证充值每日最大访问次数
	 *
	 * @author  wangjf
	 * @date    2015年5月30日 下午5:53:47
	 * @return
	 */
	public BigDecimal findAuthRechargeDailyAllRequest();
	
	/**
	 * 获取网银充值每日最大访问次数
	 *
	 * @author  wangjf
	 * @date    2015年5月30日 下午5:54:06
	 * @return
	 */
	public BigDecimal findBankRechargeDailyAllRequest();
	
	/**
	 * 获取认证充值每日可成功充值最大次数
	 *
	 * @author  wangjf
	 * @date    2015年5月30日 下午5:54:30
	 * @return
	 */
	public BigDecimal findAuthRechargeDailySuccessRequest();
	
	/**
	 * 获取网银充值每日可成功充值最大次数
	 *
	 * @author  wangjf
	 * @date    2015年5月30日 下午5:54:56
	 * @return
	 */
	public BigDecimal findBankRechargeDailySuccessRequest();
	
	/**
	 * 获取认证充值单笔限额
	 *
	 * @author  wangjf
	 * @date    2015年5月30日 下午5:56:13
	 * @return
	 */
	public BigDecimal findAuthRechargeSingleLimited();
	
	/**
	 * 获取认证充值单日限额
	 *
	 * @author  wangjf
	 * @date    2015年5月30日 下午5:57:21
	 * @return
	 */
	public BigDecimal findAuthRechargeDailyLimited();
	
	/**
	 * 获取认证充值单月限额
	 *
	 * @author  wangjf
	 * @date    2015年5月30日 下午5:57:47
	 * @return
	 */
	public BigDecimal findAuthRechargeMonthlyLimited();
	
	/**
	 * 获取最小充值金额
	 *
	 * @author  wangjf
	 * @date    2015年6月4日 下午4:12:26
	 * @return
	 */
	public BigDecimal findMinRechargeAmountLimited();
	
	/**
	 * 推荐送体验金金额
	 *
	 * @author  caoyi
	 * @date    2015年5月16日 下午13:40:53
	 * @return
	 */
	public BigDecimal findActivityRecommend();
	
	/**
	 * 获取体验金奖励天数
	 *
	 * @author  wangjf
	 * @date    2015年7月27日 上午11:50:39
	 * @return
	 */
	public int findExpireDays();
	
	
	/**
	 * 获取公司回购天数
	 *
	 * @author  wangjf
	 * @date    2015年8月15日 下午3:54:41
	 * @return
	 */
	public int findBuyBackDay();
	
	/**
	 * @description 根据参数类型查询参数列表
	 * @ParamEntity    	<tt>type：String:参数名称</tt><br> 
	 * @return      Map	<tt>paramList：List<ParamEntity>:参数列表</tt><br>                        
	 */
	public Map<String, Object> findByParamType(Map<String, Object> params) throws Exception;
	
	/**
	 * @description 根据父ID查询参数列表
	 * @ParamEntity    	<tt>parentId：String:参数名称</tt><br>
	 * @return      Map <tt>paramList：List<ParamEntity>:参数列表</tt><br>                       
	 */
	public Map<String, Object> findByParentId(Map<String, Object> params) throws Exception;
		
	/**
	 * @description 查询下级参数 返回树形结构 包括自身节点信息（如机构类型）
	 * @ParamEntity    	<tt>id：String:参数名称</tt><br>
	 * @return      Map <tt>paramList：List<ParamEntity>:参数列表</tt><br>
	 */
	public Map<String, Object> findLowerParamById(Map<String, Object> params) throws Exception;
	
	/**
	 * @description 查询下级参数 返回树形结构 不包括自身节点信息（如机构类型）
	 * @ParamEntity    	<tt>id：String:参数名称</tt><br>
	 * @return      Map <tt>paramList：List<ParamEntity>:参数列表</tt><br>
	 */
	public Map<String, Object> findLowerParamByParentId(Map<String, Object> params) throws Exception;
		
	/**
	 * @description 根据 参数ID查询单个参数
	 * @ParamEntity    	<tt>id：String:参数名称</tt><br>
	 * @return      Map <tt>param：ParamEntity:参数列表</tt><br>
	 */
	public Map<String, Object> findById(Map<String, Object> params) throws Exception;
	
	/**
	 * 根据类型获取参数
	 * 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> findByType(Map<String, Object> params) throws Exception;
	
	/**
	 * @description 根据 条件查询单个参数
	 * @ParamEntity    	Map<String, Object> map
	 *              <tt>value：String:参数名称</tt><br>
	 *             <tt>type：String:参数名称</tt><br>
	 * @return      Map <tt>param：ParamEntity:参数列表</tt><br>
	 */
	public Map<String, Object> findOneBySearch(Map<String, Object> map) throws Exception;

	/**
	 * 通过城市ID和银行名称 查询支行记录
	 * @ParamEntity Map <br>
	 *         <tt>key: cityId, title:城市ID, type:{@link String} </tt><br>
	 *         <tt>key: bankName, title:银行名称, type:{@link String} </tt><br>
	 * @return Map <br>
	 *         <tt>key: bankName, title:银行名称, type:{@link String} </tt><br>
	 *         <tt>key: subBankName, title:支行名称, type:{@link String} </tt><br>
	 * @throws Exception
	 */
	public Map<String, Object> findByCityIdAndBankName(Map<String, Object> map)throws Exception;
	
	/**
	 * 查询所有通道
	 *
	 * @author  wangjf
	 * @date    2015年10月28日 下午5:59:21
	 * @param map
	 * @return
	 * 		paramList:List<InterfaceDetailInfoEntity> 
	 * 		merchantCode:String:渠道编号
	 * @throws Exception
	 */
	public Map<String, Object> findAllChannel(Map<String, Object> map)throws Exception;
	
	/**
	 * 根据通道编号查找通道名称，若未找到则原样放回
	 *
	 * @author  wangjf
	 * @date    2015年10月30日 下午3:27:05
	 * @param channelNo
	 * @return
	 * @throws Exception
	 */
	public String findByChannelNo(String channelNo);
	
	/**
	 * 查询邀请好友内容
	 *
	 * @author  wangjf
	 * @date    2015年11月12日 上午9:44:20
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public ResultVo findInvestContent(Map<String, Object> map)throws Exception;
	
	/**
	 * 查询IOS当前版本号
	 *
	 * @author  wangjf
	 * @date    2015年12月18日 上午10:00:16
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> findCurrentIOSVersion(Map<String, Object> map);
	
	/**
	 * 获取违约金利率
	 *
	 * @author  wangjf
	 * @date    2015年5月20日 上午10:02:28
	 * @return
	 */
	public BigDecimal findPenaltyRate();
	
	/**
	 * 获取逾期管理费率
	 *
	 * @author  wangjf
	 * @date    2015年5月20日 上午10:02:28
	 * @return
	 */
	public BigDecimal findOverdueRate();
	
	/**
	 * 获取风险金利率
	 *
	 * @author  wangjf
	 * @date    2016年1月13日 下午12:13:37
	 * @return
	 */
	public BigDecimal findRiskRate();
	
	/**
	 * 
	 * @author zhangt
	 * @date   2016年2月27日上午11:30:31
	 * @return
	 */
	public Map<String, Object> findWithDrawPayer(Map<String, Object> map);
	
	/**
	 * 提前退出优选计划费率
	 *
	 * @author  wangjf
	 * @date    2016年3月2日 下午2:03:33
	 * @return
	 */
	public BigDecimal findAdvanceAtoneWealthRate();
	
	/**
	 * @description 根据参数类型查询参数列表
	 * @ParamEntity    	<tt>type：String:参数名称</tt><br> 
	 * @return      Map	<tt>paramList：List<ParamEntity>:参数列表</tt><br>                        
	 */
	public Map<String, Object> findAllBankList(Map<String, Object> params) throws Exception;
	
	/**
	 * 可发短信次数
	 * 
	 * @return
	 */
	public Long findMaxSendSmsTimes();
	
	/**
	 * 划扣公司
	 * @return
	 */
	@Rules(rules = { 
			@Rule(name = "type", required = true, requiredMessage = "类型不能为空!")
	})
	public ResultVo findThirdPayList(Map<String, Object> params) throws Exception;
	
	
	/**
	 * 查询监控邮箱
	 * 
	 * @param type
	 * 			"线下待审核监控邮箱"
	 *          "参卡号监控邮箱"
	 * @return
	 * @throws Exception
	 */
	public String findMonitorEmail(String type)throws Exception;
	
	/**
	 * 查询佣金利率
	 *
	 * @author  wangjf
	 * @date    2016年12月5日 下午7:04:49
	 * @return
	 * @throws Exception
	 */
	public BigDecimal findCommissionRate();
	
	/**
	 * 查询佣金补贴利率
	 *
	 * @author  wangjf
	 * @date    2016年12月5日 下午7:05:22
	 * @return
	 * @throws Exception
	 */
	public BigDecimal findCommissionAwardRate();
	
	/**
	 * 查询优选项目平台服务费率
	 *
	 * @author  wangjf
	 * @date    2016年12月5日 下午7:05:22
	 * @return
	 * @throws Exception
	 */
	public BigDecimal findLoanManageRate();
	
	/**
	 * 查询Wap地址
	 *
	 * @author  wangjf
	 * @date    2016年12月9日 下午9:12:22
	 * @return
	 */
	public String findWapUrl();
	
	/**
	 * 查询公司网银充值手续费
	 * 
	 * @author zhangt
	 * @date 2016年12月1日下午1:38:19
	 * @return
	 */
	public BigDecimal findCompanyRechargeBankPayExpense();
	
	/**
	 * 转让费率
	 *
	 * @author  wangjf
	 * @date    2016年12月27日 下午4:25:51
	 * @return
	 */
	public BigDecimal findTransferRate();
	
	/**
	 * 转让天数
	 *
	 * @author  wangjf
	 * @date    2016年12月27日 下午4:26:16
	 * @return
	 */
	public Integer findTransferDay();
	
	/**
	 * 转让协议模板
	 *
	 * @author  wangjf
	 * @date    2017年1月4日 下午12:13:37
	 * @return
	 */
	public String findTransferProtocalType();
	
	/**
	 * 转让需持有天数
	 *
	 * @author  wangjf
	 * @date    2017年1月4日 下午9:08:08
	 * @return
	 */
	public Integer findTransferNeedHoldDay();
	
	/**
	 * 距离项目到期日期X天不允许转让
	 *
	 * @author  wangjf
	 * @date    2017年1月4日 下午9:08:08
	 * @return
	 */
	public Integer findTransferFromEndDay();
	
	/**
	 * 信贷协议模板
	 *
	 * @author  wangjf
	 * @date    2017年1月4日 下午12:13:37
	 * @return
	 */
	public String findLoanProtocalType();
	
	/**
	 * 排除绑卡送iphone不符合条件的用户
	 *
	 * @author  wangjf
	 * @date    2017年3月6日 上午9:25:26
	 * @return
	 */
	public List<String> findExcludeCustIds();
	
	/***
	 * 查询省
	 * @author  guoyk
	 * @date    2017-4-19 
	 */
	public Map<String, Object> queryProvince() throws SLException;
	
	/***
	 * 查询市
	 * @author  guoyk
	 * @date    2017-4-19 
	 */
	public Map<String, Object> queryCity(Map<String,Object> param) throws SLException;
	
	/***
	 * 查询营业部
	 * @author  guoyk
	 * @date    2017-4-19 
	 */
	public Map<String, Object> queryDeptName(Map<String,Object> param) throws SLException;

	/**
	 * 债权转让调整上线时间 2017-05-31
	 * @author lyy
	 * @date 2017-5-23
	 */
	public String findTransferRefromOnlineTimeByParameterName(String onlineTime);
	
	/**
	 * 查询一键出借锁定时间
	 *
	 * @author  wangjf
	 * @date    2017年7月21日 下午2:12:58
	 * @return
	 */
	public int findLoanGroupLockMinutes();
}
