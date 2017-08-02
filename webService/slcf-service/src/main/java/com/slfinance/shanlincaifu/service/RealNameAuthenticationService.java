/** 
 * @(#)RealNameAuthenticationService.java 1.0.0 2015年4月25日 上午9:42:46  
 *  
 * Copyright © 2015 善林金融.  All rights reserved.  
 */ 

package com.slfinance.shanlincaifu.service;

import java.math.BigDecimal;
import java.util.Map;

import com.slfinance.exception.SLException;
import com.slfinance.shanlincaifu.validate.annotations.Rule;
import com.slfinance.shanlincaifu.validate.annotations.Rules;
import com.slfinance.vo.ResultVo;

/**   
 * 用户实名认证业务接口
 *  
 * @author  zhangzs
 * @version $Revision:1.0.0, $Date: 2015年4月25日 上午9:42:46 $ 
 */
public interface RealNameAuthenticationService {

	/**
	 * 实名认证
	 * 
	 * @author zhangzs
	 * @param Map
	 * 	          <tt>custId:String:客户主键ID</tt>
	 *            <tt>custName:String:客户姓名</tt>
	 *            <tt>credentialsCode:String:证件号码</tt>
	 * @return 
	 */
	@Rules(rules = {
			@Rule(name = "custId", required = true, requiredMessage = "客户编号不能为空"),
			@Rule(name = "custName", required = true, requiredMessage = "客户姓名不能为空"),
			@Rule(name = "credentialsCode", required = true, requiredMessage = "身份证号不能为空" ,identification=true,identificationMessage="身份证格式错误")
			})
	public ResultVo verifyIdentification(Map<String, Object> paramsMap) throws SLException;
	
	public ResultVo preverifyIdentification(Map<String, Object> paramsMap) throws SLException;
	
	public ResultVo postverifyIdentification(ResultVo resutlVo) throws SLException;
	
	/**
	 * 实名认证次数
	 * 
	 * @author zhangzs
	 * @param Map
	 * 	          <tt>custId:String:客户主键ID</tt>
	 * @return 
	 */
	@Rules(rules = {
			@Rule(name = "custId", required = true, requiredMessage = "用户ID不能为空")
			})
	public BigDecimal getRealNameAuthCount(Map<String, Object> paramsMap) throws SLException;
	
	
	/**
	 * 业务员实名认证
	 * 
	 * @param paramsMap
	 * @return
	 * @throws SLException
	 */
	public ResultVo salesManVerifyIdentification(Map<String, Object> paramsMap) throws SLException;
	
	/**
	 * 业务员实名认证（前置）
	 * 
	 * @param paramsMap
	 * @return
	 * @throws SLException
	 */
	public ResultVo presalesManVerifyIdentification(Map<String, Object> paramsMap) throws SLException;
	
	/**
	 * 业务员实名认证（后置）
	 * 
	 * @param resutlVo
	 * @return
	 * @throws SLException
	 */
	public ResultVo postsalesManVerifyIdentification(ResultVo resutlVo) throws SLException;
	
	/**
	 * 实名认证列表
	 * 
	 * @author HuangXiaodong
	 * @date 2017年6月24日
	 * @param Map
	 *         <tt>start           :String:起始值</tt><br>
     *         <tt>length          :String:长度</tt><br>
	 *         <tt>custName           :String:客户姓名(可以为空)</tt><br>
	 *         <tt>credentialsCode       :String:证件号码(可以为空)</tt><br>
	 *         <tt>tradeStatus        :String:认证结果(可以为空)</tt><br>
	 *         <tt>loginName          :String:认证人(可以为空)</tt><br>
	 *          <tt>startDate   ：Date:认证开始时间</tt><br>
	 *          <tt>endDate：        Date:认证结束时间</tt><br>
	 * @return ResultVo <tt>custName  :String:客户姓名</tt><br>
	 *         <tt>credentialsCode        :String:证件号码</tt><br>
	 *         <tt>tradeStatus        :String:认证结果</tt><br>
	 *         <tt>custAuthDate： BigDecimal:认证时间</tt><br>
	 *         <tt>loginName          :String:认证人</tt><br>
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "start", required = true, requiredMessage = "分页起始位置不能为空", digist = true, digistMessage = "分页起始位置必须为整数"),
			@Rule(name = "length", required = true, requiredMessage = "分页长度不能为空", digist = true, digistMessage = "分页长度必须为整数") })
	public ResultVo queryCustAuthInfoList(Map<String, Object> param);
	
	/**
	 * 实名认证记录
	 * 
	 * @author HuangXiaodong
	 * @date 2017年6月24日
	 * @param params
	 *         <tt>custName           :String:客户姓名(不能为空)</tt><br>
	 *         <tt>credentialsCode       :String:证件号码(不能为空)</tt><br>
	 * @return ResultVo
	 * @throws SLException
	 */
	@Rules(rules = {
			@Rule(name = "custName", required = true, requiredMessage = "客户姓名不能为空!"),
			@Rule(name = "credentialsCode", required = true, requiredMessage = "证件号码不能为空!")
			})
	public ResultVo saveCustAuthInfo(Map<String, Object> params)
			throws SLException;
	
}
