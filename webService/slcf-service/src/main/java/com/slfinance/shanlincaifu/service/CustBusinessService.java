/** 
 * @(#)CustBusinessService.java 1.0.0 2015年12月11日 上午9:35:39  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service;

import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.vo.ResultVo;

/**   
 * 客户业务信息服务接口
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年12月11日 上午9:35:39 $ 
 */
public interface CustBusinessService {
	
	/**
	 * 查询注册报告
	 *
	 * @author  wangjf
	 * @date    2015年12月11日 上午9:39:56
	 * @param params
	 *            <tt>start: int:分页起始页</tt><br>
	 *            <tt>length: int:每页长度</tt><br>
	 *            <tt>tradeType: String:业务类型（默认为注册）</tt><br>
	 *            <tt>appSource: String:终端（web,ios,andriod,wap之一）</tt><br>
	 *            <tt>summaryDate: String:查询年月（yyyy-MM）</tt><br>
	 *            <tt>summaryDateBegin: String:查询年月日开始时间（yyyy-MM-dd HH）</tt><br>
	 *            <tt>summaryDateEnd: String:查询年月日结束时间（yyyy-MM-dd HH）</tt><br>
	 *            <tt>utmSource: String:广告来源</tt><br>
	 *            <tt>utmMedium: String:广告媒介</tt><br>
	 *            <tt>channelNo: String:渠道号</tt><br>
	 * @return
	 * 			  iTotalDisplayRecords: 总条数
	 *		 	  data:List<Map<String, object>>
	 *		 	  Map<String, object>:	
	 * 			  <tt>summaryDate: String:注册日期</tt><br>	
	 * 	          <tt>appSource: String:终端</tt><br>
	 *            <tt>utmSource: String:广告来源</tt><br>
	 *            <tt>utmMedium: String:广告媒介</tt><br>
	 *            <tt>utmCampaign: String:广告名称</tt><br>
	 *            <tt>utmContent: String:广告内容</tt><br>
	 *            <tt>utmTerm: String:广告字词</tt><br>
	 *            <tt>newRegister: String:注册人数</tt><br>
	 *            <tt>channelNo: String:渠道号</tt><br>
	 */
	public Map<String, Object> findRegisterReport(Map<String, Object> params);
	
	/**
	 * 导出注册报告
	 *
	 * @author  wangjf
	 * @date    2015年12月11日 上午9:41:16
	 * @param params
	 *            <tt>tradeType: String:业务类型（默认为注册）</tt><br>
	 *            <tt>appSource: String:终端（web,ios,andriod,wap之一）</tt><br>
	 *            <tt>summaryDate: String:查询年月（yyyy-MM）</tt><br>
	 *            <tt>summaryDateBegin: String:查询年月日开始时间（yyyy-MM-dd HH）</tt><br>
	 *            <tt>summaryDateEnd: String:查询年月日结束时间（yyyy-MM-dd HH）</tt><br>
	 *            <tt>utmSource: String:广告来源</tt><br>
	 *            <tt>utmMedium: String:广告媒介</tt><br>
	 *            <tt>channelNo: String:渠道号</tt><br>
	 * @return
	 */
	public ResultVo exportRegisterReport(Map<String, Object> params) throws SLException;
}
