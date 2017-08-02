/** 
 * @(#)ProductService.java 1.0.0 2015年5月1日 上午10:40:12  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service;

import java.util.Date;
import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.annotation.Idempotent;
import com.slfinance.vo.ResultVo;

/**   
 * 产品服务接口
 *  
 * @author  wangjf
 * @version $Revision:1.0.0, $Date: 2015年5月1日 上午10:40:12 $ 
 */
public interface ProductService {

	/**
	 * 定时关标
	 *
	 * @author  wangjf
	 * @date    2015年5月1日 上午10:43:26
	 * @param param
	 * @return
	 * @throws SLException
	 */
	@Idempotent
	public ResultVo closeJob(Map<String, Object> param) throws SLException;
	
	/**
	 * 恢复可赎回额度
	 *
	 * @author  wangjf
	 * @date    2015年6月1日 下午3:14:00
	 * @param param
	 * @return
	 * @throws SLException
	 */
	@Idempotent
	public ResultVo recoverAtoneLimited(Map<String, Object> param) throws SLException;
	
	/**
	 * 活期宝每日结息
	 * 步骤：1.计算活期宝实际总利息
	 *     2.计算活期宝预计利息
	 *     3.获取客户分账信息列表
	 *     4.循环分账列表，计算每个分账的预计利息和实际利息
	 *     5.生成流水信息
	 *     6.更新客户分账信息
	 * 	   7.批量插入结息表
	 * @author linhj
	 * @throws SLException 
	 * @date 2015年05月01日 下午午15:43:41
	*/
	public void currentDailySettlement(Date now) throws SLException;
	
	/**
	 * 体验宝每日结息
	 * 步骤：1.计算活期宝预计利息
	 *     2.获取客户分账信息列表
	 *     3.循环分账列表，计算每个分账的预计利息
	 *     4.生成流水信息
	 *     5.更新客户分账信息
	 * 	   6.批量插入结息表
	 * @author linhj
	 * @throws SLException 
	 * @date 2015年05月01日 下午午15:43:41
	*/
	public void experienceDailySettlement(Date now) throws SLException;
	
	/**
	 * 体验宝到期赎回
	 * 1.查询投资有效的所有到期的体验宝分账户信息，关联产品类型表，投资表，分账表，查询15天到期的分账户
	 * 2.分账户价值置0，状态无效，投资表状态置无效
	 * 3.记账,若体验金本金1000，收益为10元，则：公司收益账户 现金-10
	 * 						    客户主账户     赎回体验宝    +1010
	 *                                回收体验金   -1000
	 * 						    客户分账户     价值 赎回体验宝    -1010
	 *                                
	 * @param now
	 */
	public void experienceWithdraw(Date now) throws SLException;
	
	/**
	 * 体验宝到期赎回 短信通知用户
	 * 查询需要通知的用户发送短信
	 *                                
	 * @param now
	 */
	public void experienceWithdrawSendSms(Date now) throws SLException;
	
	/**
	 * 定期宝关标
	 *
	 * @author  wangjf
	 * @date    2015年8月20日 下午3:38:54
	 * @param param
	 * @return
	 * @throws SLException
	 */
	@Idempotent
	public ResultVo closeTermJob(Map<String, Object> param) throws SLException;
	
	/**
	 * 回收未赎回的金额
	 * 注:活期宝最大赎回到分，若用户赎回后账户中金额小于1分则把剩余金额给收益分账户
	 *
	 * @author  wangjf
	 * @date    2015年12月15日 下午3:26:07
	 * @param param
	 * @return
	 * @throws SLException
	 */
	public ResultVo recoverUnAtone(Map<String, Object> param) throws SLException;
}
