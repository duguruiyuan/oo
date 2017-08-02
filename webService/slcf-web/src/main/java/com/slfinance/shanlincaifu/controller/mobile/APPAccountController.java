/** 
 * @(#)APPAccountController.java 1.0.0 2015年5月20日 下午2:40:26  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.controller.mobile;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.service.mobile.APPAccountService;
import com.slfinance.vo.ResultVo;

/**   
 * APP手机端账户模块业务控制器
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年5月20日 下午2:40:26 $ 
 */
@Controller
@RequestMapping(value="/account", produces="application/json;charset=UTF-8")
public class APPAccountController {
	
	@Autowired
	private APPAccountService appAccountService;
	
	/**
	 * 账户首页
	 */
	@RequestMapping(value="/user", method = RequestMethod.POST)
	public @ResponseBody ResultVo user(@RequestBody Map<String,Object> paramsMap)throws SLException{
		return appAccountService.user(paramsMap);
	}
	
	/**
	 * 账户总览
	 */
	@RequestMapping(value="/accountALL", method = RequestMethod.POST)
	public @ResponseBody ResultVo accountALL(@RequestBody Map<String,Object> paramsMap)throws SLException{
		return appAccountService.accountALL(paramsMap);
	}
	
	/**
	 * 交易记录
	 */
	@RequestMapping(value="/tradeListALL", method = RequestMethod.POST)
	public @ResponseBody ResultVo tradeListALL(@RequestBody Map<String,Object> paramsMap)throws SLException{
		return appAccountService.tradeListALL(paramsMap);
	}
	
	/**
	 * 用户加入记录、赎回记录、收益记录
	 */
	@RequestMapping(value="/tradeListALLInfo", method = RequestMethod.POST)
	public @ResponseBody ResultVo tradeListALLInfo(@RequestBody Map<String,Object> paramsMap)throws SLException{
		return appAccountService.tradeListALLInfo(paramsMap);
	}
	
	/**
	 * 消息列表
	 */
	@RequestMapping(value="/messageListALL", method = RequestMethod.POST)
	public @ResponseBody ResultVo messageListALL(@RequestBody Map<String,Object> paramsMap)throws SLException{
		return appAccountService.messageListALL(paramsMap);
	}
	
	/**
	 * 消息列表 new By salesMan APP
	 * @author  lyy
	 * @date    2016-11-14 19:39:40
	 */
	@RequestMapping(value="/messageListBySalesMan", method = RequestMethod.POST)
	public @ResponseBody ResultVo messageListBySalesMan(@RequestBody Map<String,Object> paramsMap)throws SLException{
		return appAccountService.messageListBySalesMan(paramsMap);
	}
	
	/**
	 * 未读消息 By salesMan APP
	 * @author  lyy
	 * @date    2016-11-14 19:39:40
	 */
	@RequestMapping(value="/unreadMessageCountBySalesMan", method = RequestMethod.POST)
	public @ResponseBody ResultVo unreadMessageCountBySalesMan(@RequestBody Map<String,Object> paramsMap)throws SLException{
		return appAccountService.unreadMessageCountBySalesMan(paramsMap);
	}
	
	/**
	 * 消息列表 new
	 */
	@RequestMapping(value="/messageListALLNew", method = RequestMethod.POST)
	public @ResponseBody ResultVo messageListALLNew(@RequestBody Map<String,Object> paramsMap)throws SLException{
		return appAccountService.messageListALLNew(paramsMap);
	}
	
	/**
	 * 消息更新已读
	 */
	@RequestMapping(value="/updateIsRead", method = RequestMethod.POST)
	public @ResponseBody ResultVo updateIsRead(@RequestBody Map<String,Object> paramsMap)throws SLException{
		return appAccountService.updateIsRead(paramsMap);
	}
	
	/**
	 * 消息更新已读 new
	 */
	@RequestMapping(value="/updateIsReadNew", method = RequestMethod.POST)
	public @ResponseBody ResultVo updateIsReadNew(@RequestBody Map<String,Object> paramsMap)throws SLException{
		return appAccountService.updateIsReadNew(paramsMap);
	}
	
	/**
	 * 查询账户信息(活期宝或者体验宝)
	 */
	@RequestMapping(value="/queryAccount", method = RequestMethod.POST)
	public @ResponseBody ResultVo queryAccount(@RequestBody Map<String,Object> paramsMap)throws SLException{
		return appAccountService.queryAccount(paramsMap);
	}
	
	/**
	 * 查询银行卡
	 */
	@RequestMapping(value="/queryBank", method = RequestMethod.POST)
	public @ResponseBody ResultVo queryBank(@RequestBody Map<String,Object> paramsMap)throws SLException{
		return appAccountService.queryBank(paramsMap);
	}
	
	/**
	 * 查询第三方支持的银行
	 */
	@RequestMapping(value="/querySupportBank", method = RequestMethod.POST)
	public @ResponseBody ResultVo querySupportBank(@RequestBody Map<String,Object> paramsMap)throws SLException{
		return appAccountService.querySupportBank();
	}
	
	/**
	 * 根据卡号查询银行卡信息
	 */
	@RequestMapping(value="/queryThirdBankByCardNo", method = RequestMethod.POST)
	public @ResponseBody ResultVo queryThirdBankByCardNo(@RequestBody Map<String,Object> paramsMap)throws SLException{
		return appAccountService.queryThirdBankByCardNo(paramsMap);
	}
	
	/**
	 * 获取金牌推荐人统计信息
	 */
	@RequestMapping(value="/findCustCommissionListMobile", method = RequestMethod.POST)
	public @ResponseBody ResultVo findCustCommissionListMobile(@RequestBody Map<String,Object> paramsMap)throws SLException{
		return appAccountService.findCustCommissionListMobile(paramsMap);
	}
	
	/**
	 *  获取我的佣金详情记录
	 */
	@RequestMapping(value="/findCustCommissionDetailListMobile", method = RequestMethod.POST)
	public @ResponseBody ResultVo findCustCommissionDetailListMobile(@RequestBody Map<String,Object> paramsMap)throws SLException{
		return appAccountService.findCustCommissionDetailListMobile(paramsMap);
	}
	
	/**
	 * 交易记录
	 */
	@RequestMapping(value="/tradeFlowList", method = RequestMethod.POST)
	public @ResponseBody ResultVo tradeFlowList(@RequestBody Map<String,Object> paramsMap)throws SLException{
		return appAccountService.tradeFlowList(paramsMap);
	}
	
	/**
	 * 查询提现金额
	 */
	@RequestMapping(value="/queryWithdrawalAmount", method = RequestMethod.POST)
	public @ResponseBody ResultVo queryWithdrawalAmount(@RequestBody Map<String,Object> paramsMap)throws SLException{
		return appAccountService.queryWithdrawalAmount(paramsMap);
	} 
	
}
