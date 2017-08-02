/** 
 * @(#)OpenService.java 1.0.0 2015年7月2日 下午1:52:14  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service;

import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.entity.CustInfoEntity;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;

/**   
 * 开放服务
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年7月2日 下午1:52:14 $ 
 */
public interface OpenService {

	/**
	 * 注册
	 *
	 * @author  wangjf
	 * @date    2015年7月2日 下午1:53:24
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public Map<String, Object> openRegister(Map<String, Object> params) throws SLException;
	
	/**
	 * 获取短信验证码
	 *
	 * @author  wangjf
	 * @date    2015年7月13日 上午11:55:04
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public Map<String, Object> openSendSms(Map<String, Object> params) throws SLException;
	
	/**
	 * 保存外部推送的实名认证
	 *
	 * @author  wangjf
	 * @date    2015年8月8日 下午3:09:58
	 * @param params
	 * 		<tt>custId： String:用户ID</tt><br>
	 *      <tt>channelNo： String:渠道号</tt><br>
	 *      <tt>utid： String:校花ID</tt><br>
	 * @return
	 * @throws SLException
	 */
	public void saveRealNameAuth(Map<String, Object> params)throws SLException;
	
	/**
	 * 保存外部推送的充值
	 *
	 * @author  wangjf
	 * @date    2015年8月8日 下午3:10:14
	 * @param params
	 *      <tt>tradeFlowId： String:充值过程流水ID</tt><br>
	 * 		<tt>custId： String:用户ID</tt><br>
	 *      <tt>channelNo： String:渠道号</tt><br>
	 *      <tt>utid： String:校花ID</tt><br>
	 * @return
	 * @throws SLException
	 */
	public void saveRecharge(Map<String, Object> params)throws SLException;
	
	/**
	 * 查询外部推送的实名认证
	 *
	 * @author  wangjf
	 * @date    2015年8月8日 下午3:53:21
	 * @param params
	 * @throws SLException
	 */
	public Map<String, Object> queryRealNameAuth(Map<String, Object> params)throws SLException;
	
	/**
	 * 查询外部推送的充值
	 *
	 * @author  wangjf
	 * @date    2015年8月8日 下午3:53:24
	 * @param params
	 * @throws SLException
	 */
	public Map<String, Object> queryRecharge(Map<String, Object> params)throws SLException;
	
	/**
	 * 保存下载信息
	 * 
	 * @author zhangt
	 * @date   2015年10月21日下午3:56:39
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public Map<String, Object> saveDownloadMessage(Map<String, Object> params)throws SLException;
	
	/**
	 * 确认吉融通流量订购结果
	 * 
	 * @author zhangt
	 * @date   2015年10月23日上午10:27:22
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public String confirmStatus(Map<String, Object> params)throws SLException;
	
	/**
	 * 处理请求
	 * 注：此处仅作消息入队，具体业务交由OpenDispatcherService分发处理
	 *
	 * @author  wangjf
	 * @date    2015年12月19日 下午1:14:11
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public Map<String, Object> handleRequest(Map<String, Object> params) throws SLException;
	
	/**
	 * 查询设备是否注册
	 *
	 * @author  wangjf
	 * @date    2015年12月22日 下午1:53:51
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public Map<String, Object> queryRegister(Map<String, Object> params) throws SLException;
	
	/**
	 * 查询吉融流量活动地址
	 * 
	 * @param params
	 * @return
	 * @throws SLException
	 */
	@Rules(rules = { 
			@Rule(name = "channelNo", required = true, requiredMessage = "渠道号不能为空!"),
			@Rule(name = "custId", required = true, requiredMessage = "客户ID不能为空!")
	})
	public ResultVo queryFlowDataActivityUrl(Map<String, Object> params) throws SLException;
	
	/**
	 * 保存项目
	 *
	 * @author  wangjf
	 * @date    2016年11月29日 下午7:56:05
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public Map<String, Object> saveProject(Map<String, Object> params) throws SLException;
	
	/**
	 * 查询项目
	 *
	 * @author  wangjf
	 * @date    2016年11月30日 下午1:30:30
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public Map<String, Object> queryProject(Map<String, Object> params) throws SLException;
	
	/**
	 * 查询预约金额
	 *
	 * @author  zhangyb
	 * @date    2017年7月17日 
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public Map<String, Object> queryPreAmount(Map<String, Object> params) throws SLException;
	
    /**
     * 接收流标/放款通知
     *
     * @author  zhagnze
     * @date    2017年6月2日 下午1:31:30
     * @param params
     * @return
     * @throws SLException
     */
    public Map<String, Object> operateProject(Map<String, Object> params) throws SLException;
    
	/**
	 * 还款通知
	 *
	 * @author  wangjf
	 * @date    2016年11月30日 下午3:17:49
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public Map<String, Object> repayment(Map<String, Object> params) throws SLException;
	
	/**
	 * 实名认证查询
	 *
	 * @author  wangjf
	 * @date    2016年11月30日 下午3:25:28
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public Map<String, Object> queryRealName(Map<String, Object> params) throws SLException;
	
	/**
	 * 绑定或解绑银行卡
	 *
	 * @author  wangjf
	 * @date    2016年11月30日 下午3:28:21
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public Map<String, Object> bindCard(Map<String, Object> params) throws SLException;
	
	/**
	 * 查询协议
	 *
	 * @author  wangjf
	 * @date    2016年12月9日 下午6:26:47
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public Map<String, Object> queryProtocol(Map<String, Object> params) throws SLException;
	/**
	 * 网贷之家接口查询
	 *
	 * @author fengyl
	 * @date 2017年4月17日
	 * @param page
	 * @param pageSize
	 * @param date
	 * @return
	 * @throws SLException
	 */
	public Map<String, Object> queryWdzj(Map<String, Object> params) throws SLException;
	
	/**
	 * 网贷之家登录
	 * @author  fengyl
	 * @date    2017年4月17日 
	 * @param username
	 * @param password
	 * @return
	 * @throws SLException
	 */
	CustInfoEntity loginWdzj(String username, String password);
	
	/**
	 * 获取微信签名
	 *
	 * @author  wangjf
	 * @date    2017年4月27日 下午2:25:09
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public Map<String,Object> getWXSignature(Map<String, Object> params)
			throws SLException;


	/**
	 * 逾期代扣
	 *
	 * @author
	 * @date    2017年7月12日 上午10:43:09
	 * @param params
	 * @return
	 * @throws SLException
	 */
	public Map<String,Object>  timeLimitWithHold(Map<String, Object> params) throws SLException;
	
}
